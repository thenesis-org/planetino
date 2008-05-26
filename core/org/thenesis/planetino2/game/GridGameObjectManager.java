/*
 * Planetino - Copyright (C) 2007-2008 Guillaume Legris, Mathieu Legris
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA 
 */

/* Copyright (c) 2003, David Brackeen
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *   - Redistributions of source code must retain the above copyright notice, 
 *     this list of conditions and the following disclaimer.
 *   - Redistributions in binary form must reproduce the above copyright notice, 
 *     this list of conditions and the following disclaimer in the documentation 
 *     and/or other materials provided with the distribution.
 *   - Neither the name of David Brackeen nor the names of its contributors may be
 *     used to endorse or promote products derived from this software without 
 *     specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.thenesis.planetino2.game;

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;

import org.thenesis.planetino2.math3D.Rectangle;
import org.thenesis.planetino2.math3D.Vector3D;

/**
 The GridGameObjectManager is a GameObjectManager that
 integrally arranges GameObjects on a 2D grid for visibility
 determination and to limit the number of tests for
 collision detection.
 */
public class GridGameObjectManager implements GameObjectManager {

	/**
	 Default grid size of 512. The grid size should be larger
	 than the largest object's diameter.
	 */
	private static final int GRID_SIZE_BITS = 9;
	private static final int GRID_SIZE = 1 << GRID_SIZE_BITS;

	/**
	 The Cell class represents a cell in the grid. It contains
	 a list of game objects and a visible flag.
	 */
	private static class Cell {
		Vector objects;
		boolean visible;

		Cell() {
			objects = new Vector();
			visible = false;
		}
	}

	private Cell[] grid;
	private Rectangle mapBounds;
	private int gridWidth;
	private int gridHeight;
	private Vector allObjects;
	private Vector spawnedObjects;
	private GameObject player;
	private Vector3D oldLocation;
	private CollisionDetection collisionDetection;

	/**
	 Creates a new GridGameObjectManager with the specified
	 map bounds and collision detection handler. GameObjects
	 outside the map bounds will never be shown.
	 */
	public GridGameObjectManager(Rectangle mapBounds, CollisionDetection collisionDetection) {
		this.mapBounds = mapBounds;
		this.collisionDetection = collisionDetection;
		gridWidth = (mapBounds.width >> GRID_SIZE_BITS) + 1;
		gridHeight = (mapBounds.height >> GRID_SIZE_BITS) + 1;
		grid = new Cell[gridWidth * gridHeight];
		for (int i = 0; i < grid.length; i++) {
			grid[i] = new Cell();
		}
		allObjects = new Vector();
		spawnedObjects = new Vector();
		oldLocation = new Vector3D();
	}

	/**
	 Converts a map x-coordinate to a grid x-coordinate.
	 */
	private int convertMapXtoGridX(int x) {
		return (x - mapBounds.x) >> GRID_SIZE_BITS;
	}

	/**
	 Converts a map y-coordinate to a grid y-coordinate.
	 */
	private int convertMapYtoGridY(int y) {
		return (y - mapBounds.y) >> GRID_SIZE_BITS;
	}

	/**
	 Marks all objects as potentially visible (should be drawn).
	 */
	public void markAllVisible() {
		for (int i = 0; i < grid.length; i++) {
			grid[i].visible = true;
		}
	}

	/**
	 Marks all objects within the specified 2D bounds
	 as potentially visible (should be drawn).
	 */
	public void markVisible(Rectangle bounds) {
		int x1 = Math.max(0, convertMapXtoGridX(bounds.x));
		int y1 = Math.max(0, convertMapYtoGridY(bounds.y));
		int x2 = Math.min(gridWidth - 1, convertMapXtoGridX(bounds.x + bounds.width));
		int y2 = Math.min(gridHeight - 1, convertMapYtoGridY(bounds.y + bounds.height));

		for (int y = y1; y <= y2; y++) {
			int offset = y * gridWidth;
			for (int x = x1; x <= x2; x++) {
				grid[offset + x].visible = true;
			}
		}
	}

	/**
	 Adds a GameObject to this manager.
	 */
	public void add(GameObject object) {
		if (object != null) {
			if (object == player) {
				// ensure player always moves first
				allObjects.insertElementAt(object, 0);
			} else {
				allObjects.addElement(object);
			}
			Cell cell = getCell(object);
			if (cell != null) {
				cell.objects.addElement(object);
			}

		}
	}

	/**
	 Removes a GameObject from this manager.
	 */
	public void remove(GameObject object) {
		if (object != null) {
			allObjects.removeElement(object);
			Cell cell = getCell(object);
			if (cell != null) {
				cell.objects.removeElement(object);
			}
		}
	}

	/**
	 Adds a GameObject to this manager, specifying it as the
	 player object. An existing player object, if any,
	 is not removed.
	 */
	public void addPlayer(GameObject player) {
		this.player = player;
		if (player != null) {
			player.notifyVisible(true);
			add(player);
		}
	}

	/**
	 Gets the object specified as the Player object, or null
	 if no player object was specified.
	 */
	public GameObject getPlayer() {
		return player;
	}

	/**
	 Gets the cell the specified GameObject is in, or null if
	 the GameObject is not within the map bounds.
	 */
	private Cell getCell(GameObject object) {
		int x = convertMapXtoGridX((int) object.getX());
		int y = convertMapYtoGridY((int) object.getZ());
		return getCell(x, y);
	}

	/**
	 Gets the cell of the specified grid location, or null if
	 the grid location is invalid.
	 */
	private Cell getCell(int x, int y) {

		// check bounds
		if (x < 0 || y < 0 || x >= gridWidth || y >= gridHeight) {
			return null;
		}

		// get the cell at the x,y location
		return grid[x + y * gridWidth];
	}

	/**
	 Updates all objects based on the amount of time passed
	 from the last update and applied collision detection.
	 */
	public void update(long elapsedTime) {
		for (int i = 0; i < allObjects.size(); i++) {
			GameObject object = (GameObject) allObjects.elementAt(i);

			// save the object's old position
			Cell oldCell = getCell(object);
			oldLocation.setTo(object.getLocation());
			boolean isRegenerating = false;

			// move the object
			object.update(player, elapsedTime);

			// keep track of any spawned objects (add later)
			Vector spawns = object.getSpawns();
			if (spawns != null) {
				if (spawns.contains(object)) {
					isRegenerating = true;
				}

				Enumeration e = spawns.elements();
				while (e.hasMoreElements()) {
					spawnedObjects.addElement(e.nextElement());
				}
				//spawnedObjects.addAll(spawns);
			}

			// remove the object if destroyed
			if (object.isDestroyed() || isRegenerating) {
				allObjects.removeElementAt(i);
				i--;
				if (oldCell != null) {
					oldCell.objects.removeElement(object);
				}
				continue;
			}

			// if the object moved, do collision detection
			if (!object.getLocation().equals(oldLocation) || object.isJumping()) {

				// check walls, floors, and ceilings
				collisionDetection.checkBSP(object, oldLocation, elapsedTime);

				// check other objects
				if (checkObjectCollision(object, oldLocation)) {
					// revert to old position
					object.getLocation().setTo(oldLocation);
				}

				// update grid location
				Cell cell = getCell(object);
				if (cell != oldCell) {
					if (oldCell != null) {
						oldCell.objects.removeElement(object);
					}
					if (cell != null) {
						cell.objects.addElement(object);
					}
				}
			}

		}

		// add any spawned objects
		if (spawnedObjects.size() > 0) {
			for (int i = 0; i < spawnedObjects.size(); i++) {
				add((GameObject) spawnedObjects.elementAt(i));
			}
			spawnedObjects.removeAllElements();
		}
	}

	/**
	 Checks to see if the specified object collides with any
	 other object.
	 */
	public boolean checkObjectCollision(GameObject object, Vector3D oldLocation) {

		boolean collision = false;

		// use the object's (x,z) position (ground plane)
		int x = convertMapXtoGridX((int) object.getX());
		int y = convertMapYtoGridY((int) object.getZ());

		// check the object's surrounding 9 cells
		for (int i = x - 1; i <= x + 1; i++) {
			for (int j = y - 1; j <= y + 1; j++) {
				Cell cell = getCell(i, j);
				if (cell != null) {
					collision |= collisionDetection.checkObject(object, cell.objects, oldLocation);
				}
			}
		}

		return collision;
	}

	/**
	 Draws all visible objects and marks all objects as
	 not visible.
	 */
	public void draw(Graphics g, GameObjectRenderer r) {
		for (int i = 0; i < grid.length; i++) {
			Vector objects = grid[i].objects;
			for (int j = 0; j < objects.size(); j++) {
				GameObject object = (GameObject) objects.elementAt(j);
				boolean visible = false;
				if (grid[i].visible) {
					visible = r.draw(g, object);
				}
				if (object != player) {
					// notify objects if they are visible
					object.notifyVisible(visible);
				}
			}
			grid[i].visible = false;
		}
	}
}
