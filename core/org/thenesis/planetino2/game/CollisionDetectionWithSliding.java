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

import org.thenesis.planetino2.bsp2D.BSPPolygon;
import org.thenesis.planetino2.bsp2D.BSPTree;
import org.thenesis.planetino2.math3D.PolygonGroupBounds;
import org.thenesis.planetino2.math3D.Vector3D;

/**
 The CollisionDetectionWithSliding class handles collision
 detection between the GameObjects, and between GameObjects and
 a BSP tree. When a collision occurs, the GameObject slides
 to the side rather than stops.
 */
public class CollisionDetectionWithSliding extends CollisionDetection {

	private Vector3D scratch = new Vector3D();
	private Vector3D originalLocation = new Vector3D();

	/**
	 Creates a new CollisionDetectionWithSliding object for the
	 specified BSP tree.
	 */
	public CollisionDetectionWithSliding(BSPTree bspTree) {
		super(bspTree);
	}

	/**
	 Checks for a game object collision with the walls of the
	 BSP tree. Returns the first wall collided with, or null if
	 there was no collision. If there is a collision, the
	 object slides along the wall and again checks for a
	 collision. If a collision occurs on the slide, the object
	 reverts back to its old location.
	 */
	public BSPPolygon checkWalls(GameObject object, Vector3D oldLocation, long elapsedTime) {

		float goalX = object.getX();
		float goalZ = object.getZ();

		BSPPolygon wall = super.checkWalls(object, oldLocation, elapsedTime);
		// if collision found and object didn't stop itself
		if (wall != null && object.getTransform().isMoving()) {
			float actualX = object.getX();
			float actualZ = object.getZ();

			// dot product between wall's normal and line to goal
			scratch.setTo(actualX, 0, actualZ);
			scratch.subtract(goalX, 0, goalZ);
			float length = scratch.getDotProduct(wall.getNormal());

			float slideX = goalX + length * wall.getNormal().x;
			float slideZ = goalZ + length * wall.getNormal().z;

			object.getLocation().setTo(slideX, object.getY(), slideZ);
			originalLocation.setTo(oldLocation);
			oldLocation.setTo(actualX, oldLocation.y, actualZ);

			// use a smaller radius for sliding
			PolygonGroupBounds bounds = object.getBounds();
			float originalRadius = bounds.getRadius();
			bounds.setRadius(originalRadius - 1);

			// check for collision with slide position
			BSPPolygon wall2 = super.checkWalls(object, oldLocation, elapsedTime);

			// restore changed parameters
			oldLocation.setTo(originalLocation);
			bounds.setRadius(originalRadius);

			if (wall2 != null) {
				object.getLocation().setTo(actualX, object.getY(), actualZ);
				return wall2;
			}
		}

		return wall;
	}

	/**
	 Checks for object collisions with the floor and ceiling.
	 Uses object.getFloorHeight() and object.getCeilHeight()
	 for the floor and ceiling values.
	 Applies gravity if the object is above the floor,
	 and scoots the object up if the player is below the floor
	 (for smooth movement up stairs).
	 */
	protected void checkFloorAndCeiling(GameObject object, long elapsedTime) {
		float floorHeight = object.getFloorHeight();
		float ceilHeight = object.getCeilHeight();
		float bottomHeight = object.getBounds().getBottomHeight();
		float topHeight = object.getBounds().getTopHeight();
		Vector3D v = object.getTransform().getVelocity();
		Physics physics = Physics.getInstance();

		// check if on floor
		if (object.getY() + bottomHeight == floorHeight) {
			if (v.y < 0) {
				v.y = 0;
			}
		}
		// check if below floor
		else if (object.getY() + bottomHeight < floorHeight) {

			if (!object.isFlying()) {
				// if falling
				if (v.y < 0) {
					object.notifyFloorCollision();
					v.y = 0;
					object.getLocation().y = floorHeight - bottomHeight;
				} else if (!object.isJumping()) {
					physics.scootUp(object, elapsedTime);
				}
			} else {
				object.notifyFloorCollision();
				v.y = 0;
				object.getLocation().y = floorHeight - bottomHeight;
			}
		}
		// check if hitting ceiling
		else if (object.getY() + topHeight > ceilHeight) {
			object.notifyCeilingCollision();
			if (v.y > 0) {
				v.y = 0;
			}
			object.getLocation().y = ceilHeight - topHeight;
			if (!object.isFlying()) {
				physics.applyGravity(object, elapsedTime);
			}
		}
		// above floor
		else {
			if (!object.isFlying()) {
				// if scooting-up, stop the scoot
				if (v.y > 0 && !object.isJumping()) {
					v.y = 0;
					object.getLocation().y = floorHeight - bottomHeight;
				} else {
					physics.applyGravity(object, elapsedTime);
				}
			}
		}

	}

	/**
	 Handles an object collision. Object A is the moving
	 object, and Object B is the object that Object A collided
	 with. Object A slides around or steps on top of
	 Object B if possible.
	 */
	protected boolean handleObjectCollision(GameObject objectA, GameObject objectB, float distSq, float minDistSq,
			Vector3D oldLocation) {
		objectA.notifyObjectCollision(objectB);

		// if objectB has no polygons, it's a trigger area
        if (objectB.getPolygonGroup().isEmpty()) {
            return false;
        }
		
		if (objectA.isFlying()) {
			return true;
		}

		float stepSize = objectA.getBounds().getTopHeight() / 6;
		Vector3D velocity = objectA.getTransform().getVelocity();

		// step up on top of object if possible
		float objectABottom = objectA.getY() + objectA.getBounds().getBottomHeight();
		float objectBTop = objectB.getY() + objectB.getBounds().getTopHeight();
		if (objectABottom + stepSize > objectBTop
				&& objectBTop + objectA.getBounds().getTopHeight() < objectA.getCeilHeight()) {
			objectA.getLocation().y = (objectBTop - objectA.getBounds().getBottomHeight());
			if (velocity.y < 0) {
				objectA.setJumping(false);
				// don't let gravity get out of control
				velocity.y = -.01f;
			}
			return false;
		}

		if (objectA.getX() != oldLocation.x || objectA.getZ() != oldLocation.z) {
			// slide to the side
			float slideDistFactor = (float) Math.sqrt(minDistSq / distSq) - 1;
			scratch.setTo(objectA.getX(), 0, objectA.getZ());
			scratch.subtract(objectB.getX(), 0, objectB.getZ());
			scratch.multiply(slideDistFactor);
			objectA.getLocation().add(scratch);

			// revert location if passing through a wall
			if (super.checkWalls(objectA, oldLocation, 0) != null) {
				return true;
			}

			return false;
		}

		return true;
	}

}
