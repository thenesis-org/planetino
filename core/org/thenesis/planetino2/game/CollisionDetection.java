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

import org.thenesis.planetino2.bsp2D.BSPLine;
import org.thenesis.planetino2.bsp2D.BSPPolygon;
import org.thenesis.planetino2.bsp2D.BSPTree;
import org.thenesis.planetino2.bsp2D.Point2D;
import org.thenesis.planetino2.math3D.BoxPolygonGroup;
import org.thenesis.planetino2.math3D.CompositePolygonGroup;
import org.thenesis.planetino2.math3D.PolygonGroup;
import org.thenesis.planetino2.math3D.PolygonGroupBounds;
import org.thenesis.planetino2.math3D.Vector3D;
import org.thenesis.planetino2.util.MoreMath;
import org.thenesis.planetino2.util.Vector;

/**
 The CollisionDetection class handles collision detection
 between the GameObjects, and between GameObjects and
 a BSP tree.  When a collision occurs, the GameObject stops.
 */
public class CollisionDetection {

	/**
	 Bounding game object corners used to test for
	 intersection with the BSP tree. Corners are in either
	 clockwise or counter-clockwise order.
	 */
	private static final Point2D.Float[] CORNERS = { new Point2D.Float(-1, -1), new Point2D.Float(-1, 1),
			new Point2D.Float(1, 1), new Point2D.Float(1, -1), };

	private BSPTree bspTree;
	private BSPLine path;
	private Point2D.Float intersection;

	/**
	 Creates a new CollisionDetection object for the
	 specified BSP tree.
	 */
	public CollisionDetection(BSPTree bspTree) {
		this.bspTree = bspTree;
		path = new BSPLine();
		intersection = new Point2D.Float();
	}

	/**
	 Checks a GameObject against the BSP tree. Returns true if
	 a wall collision occurred.
	 */
	public boolean checkBSP(GameObject object, Vector3D oldLocation, long elapsedTime) {

		boolean wallCollision = false;

		// check walls if x or z position changed
		if (object.getX() != oldLocation.x || object.getZ() != oldLocation.z) {
			wallCollision = (checkWalls(object, oldLocation, elapsedTime) != null);
		}

		getFloorAndCeiling(object);
		checkFloorAndCeiling(object, elapsedTime);

		return wallCollision;
	}

	/**
	 Gets the floor and ceiling values for the specified
	 GameObject. Calls object.setFloorHeight() and
	 object.setCeilHeight() to set the floor and ceiling
	 values.
	 */
	public void getFloorAndCeiling(GameObject object) {
		float x = object.getX();
		float z = object.getZ();
		float r = object.getBounds().getRadius() - 1;
		float floorHeight = Float.MIN_VALUE;
		float ceilHeight = Float.MAX_VALUE;
		BSPTree.Leaf leaf = bspTree.getLeaf(x, z);
		if (leaf != null) {
			floorHeight = leaf.floorHeight;
			ceilHeight = leaf.ceilHeight;
		}

		// check surrounding four points
		for (int i = 0; i < CORNERS.length; i++) {
			float xOffset = r * CORNERS[i].x;
			float zOffset = r * CORNERS[i].y;
			leaf = bspTree.getLeaf(x + xOffset, z + zOffset);
			if (leaf != null) {
				floorHeight = Math.max(floorHeight, leaf.floorHeight);
				ceilHeight = Math.min(ceilHeight, leaf.ceilHeight);
			}
		}

		object.setFloorHeight(floorHeight);
		object.setCeilHeight(ceilHeight);
	}

	/**
	 Checks for object collisions with the floor and ceiling.
	 Uses object.getFloorHeight() and object.getCeilHeight()
	 for the floor and ceiling values.
	 */
	protected void checkFloorAndCeiling(GameObject object, long elapsedTime) {
		boolean collision = false;

		float floorHeight = object.getFloorHeight();
		float ceilHeight = object.getCeilHeight();
		float bottomHeight = object.getBounds().getBottomHeight();
		float topHeight = object.getBounds().getTopHeight();

		if (!object.isFlying()) {
			object.getLocation().y = floorHeight - bottomHeight;
		}
		// check if below floor
		if (object.getY() + bottomHeight < floorHeight) {
			object.notifyFloorCollision();
			object.getTransform().getVelocity().y = 0;
			object.getLocation().y = floorHeight - bottomHeight;
		}
		// check if hitting ceiling
		else if (object.getY() + topHeight > ceilHeight) {
			object.notifyCeilingCollision();
			object.getTransform().getVelocity().y = 0;
			object.getLocation().y = ceilHeight - topHeight;
		}

	}

	/**
	 Checks for a game object collision with the walls of the
	 BSP tree. Returns the first wall collided with, or null if
	 there was no collision.
	 */
	public BSPPolygon checkWalls(GameObject object, Vector3D oldLocation, long elapsedTime) {
		Vector3D v = object.getTransform().getVelocity();
		PolygonGroupBounds bounds = object.getBounds();
		float x = object.getX();
		float y = object.getY();
		float z = object.getZ();
		float r = bounds.getRadius();
		float stepSize = 0;
		if (!object.isFlying()) {
			stepSize = BSPPolygon.PASSABLE_WALL_THRESHOLD;
		}
		float bottom = object.getY() + bounds.getBottomHeight() + stepSize;
		float top = object.getY() + bounds.getTopHeight();

		// pick closest intersection of 4 corners
		BSPPolygon closestWall = null;
		float closestDistSq = Float.MAX_VALUE;
		for (int i = 0; i < CORNERS.length; i++) {
			float xOffset = r * CORNERS[i].x;
			float zOffset = r * CORNERS[i].y;
			BSPPolygon wall = getFirstWallIntersection(oldLocation.x + xOffset, oldLocation.z + zOffset, x + xOffset, z
					+ zOffset, bottom, top);
			if (wall != null) {
				float x2 = intersection.x - xOffset;
				float z2 = intersection.y - zOffset;
				float dx = (x2 - oldLocation.x);
				float dz = (z2 - oldLocation.z);
				float distSq = dx * dx + dz * dz;
				// pick the wall with the closest distance, or
				// if the distances are equal, pick the current
				// wall if the offset has the same sign as the
				// velocity.
				if (distSq < closestDistSq
						|| (distSq == closestDistSq && MoreMath.sign(xOffset) == MoreMath.sign(v.x) && MoreMath
								.sign(zOffset) == MoreMath.sign(v.z))) {
					closestWall = wall;
					closestDistSq = distSq;
					object.getLocation().setTo(x2, y, z2);
				}
			}
		}

		if (closestWall != null) {
			object.notifyWallCollision();
		}

		// make sure the player bounds is empty
		// (avoid colliding with sharp corners)
		x = object.getX();
		z = object.getZ();
		r -= 1;
		for (int i = 0; i < CORNERS.length; i++) {
			int next = i + 1;
			if (next == CORNERS.length) {
				next = 0;
			}
			// use (r-1) so this doesn't interfere with normal
			// collisions
			float xOffset1 = r * CORNERS[i].x;
			float zOffset1 = r * CORNERS[i].y;
			float xOffset2 = r * CORNERS[next].x;
			float zOffset2 = r * CORNERS[next].y;

			BSPPolygon wall = getFirstWallIntersection(x + xOffset1, z + zOffset1, x + xOffset2, z + zOffset2, bottom,
					top);
			if (wall != null) {
				object.notifyWallCollision();
				object.getLocation().setTo(oldLocation.x, object.getY(), oldLocation.z);
				return wall;
			}
		}

		return closestWall;
	}

	/**
	 Gets the first intersection, if any, of the path (x1,z1)->
	 (x2,z2) with the walls of the BSP tree. Returns the
	 first BSPPolygon intersection, or null if no intersection
	 occurred.
	 */
	public BSPPolygon getFirstWallIntersection(float x1, float z1, float x2, float z2, float yBottom, float yTop) {
		return getFirstWallIntersection(bspTree.getRoot(), x1, z1, x2, z2, yBottom, yTop);
	}

	/**
	 Gets the first intersection, if any, of the path (x1,z1)->
	 (x2,z2) with the walls of the BSP tree, starting with
	 the specified node. Returns the first BSPPolyon
	 intersection, or null if no intersection occurred.
	 */
	protected BSPPolygon getFirstWallIntersection(BSPTree.Node node, float x1, float z1, float x2, float z2,
			float yBottom, float yTop) {
		if (node == null || node instanceof BSPTree.Leaf) {
			return null;
		}

		int start = node.partition.getSideThick(x1, z1);
		int end = node.partition.getSideThick(x2, z2);
		float intersectionX;
		float intersectionZ;

		if (end == BSPLine.COLLINEAR) {
			end = start;
		}

		if (start == BSPLine.COLLINEAR) {
			intersectionX = x1;
			intersectionZ = z1;
		} else if (start != end) {
			path.setLine(x1, z1, x2, z2);
			node.partition.getIntersectionPoint(path, intersection);
			intersectionX = intersection.x;
			intersectionZ = intersection.y;
		} else {
			intersectionX = x2;
			intersectionZ = z2;
		}

		if (start == BSPLine.COLLINEAR && start == end) {
			return null;
		}

		// check front part of line
		if (start != BSPLine.COLLINEAR) {
			BSPPolygon wall = getFirstWallIntersection((start == BSPLine.FRONT) ? node.front : node.back, x1, z1,
					intersectionX, intersectionZ, yBottom, yTop);
			if (wall != null) {
				return wall;
			}
		}

		// test this boundary
		if (start != end || start == BSPLine.COLLINEAR) {
			BSPPolygon wall = getWallCollision(node.polygons, x1, z1, x2, z2, yBottom, yTop);
			if (wall != null) {
				intersection.setLocation(intersectionX, intersectionZ);
				return wall;
			}
		}

		// check back part of line
		if (start != end) {
			BSPPolygon wall = getFirstWallIntersection((end == BSPLine.FRONT) ? node.front : node.back, intersectionX,
					intersectionZ, x2, z2, yBottom, yTop);
			if (wall != null) {
				return wall;
			}
		}

		// not found
		return null;
	}

	/**
	 Checks if the specified path collides with any of
	 the collinear list of polygons. The path crosses the line
	 represented by the polygons, but the polygons may not
	 necessarily cross the path.
	 */
	protected BSPPolygon getWallCollision(Vector polygons, float x1, float z1, float x2, float z2, float yBottom,
			float yTop) {
		path.setLine(x1, z1, x2, z2);
		for (int i = 0; i < polygons.size(); i++) {
			BSPPolygon poly = (BSPPolygon) polygons.elementAt(i);
			BSPLine wall = poly.getLine();

			// check if not wall
			if (wall == null) {
				continue;
			}

			// check if not vertically in the wall (y axis)
			if (wall.top <= yBottom || wall.bottom > yTop) {
				continue;
			}

			// check if moving to back of wall
			if (wall.getSideThin(x2, z2) != BSPLine.BACK) {
				continue;
			}

			// check if path crosses wall
			int side1 = path.getSideThin(wall.x1, wall.y1);
			int side2 = path.getSideThin(wall.x2, wall.y2);
			if (side1 != side2) {
				return poly;
			}
		}
		return null;
	}

	/**
	 Checks if the specified object collisions with any other
	 object in the specified list.
	 */
	public boolean checkObject(GameObject objectA, Vector objects, Vector3D oldLocation) {
		boolean collision = false;
		for (int i = 0; i < objects.size(); i++) {
			GameObject objectB = (GameObject) objects.elementAt(i);
			collision |= checkObject(objectA, objectB, oldLocation);
		}
		return collision;
	}

	/**
	 Returns true if the two specified objects collide.
	 Object A is the moving object, and Object B is the object
	 to check. Uses bounding upright cylinders (circular base
	 and top) to determine collisions.
	 */
	public boolean checkObject(GameObject objectA, GameObject objectB, Vector3D oldLocation) {
		// don't collide with self
		if (objectA == objectB) {
			return false;
		}

		PolygonGroupBounds boundsA = objectA.getBounds();
		PolygonGroupBounds boundsB = objectB.getBounds();

		// first, check y axis collision (assume height is pos)
		float Ay1 = objectA.getY() + boundsA.getBottomHeight();
		float Ay2 = objectA.getY() + boundsA.getTopHeight();
		float By1 = objectB.getY() + boundsB.getBottomHeight();
		float By2 = objectB.getY() + boundsB.getTopHeight();
		if (By2 < Ay1 || By1 > Ay2) {
			return false;
		}

		// next, check 2D, x/z plane collision (circular base)
		float dx = objectA.getX() - objectB.getX();
		float dz = objectA.getZ() - objectB.getZ();
		float minDist = boundsA.getRadius() + boundsB.getRadius();
		float distSq = dx * dx + dz * dz;
		float minDistSq = minDist * minDist;
		if (distSq < minDistSq) {
			if (objectB.getPolygonGroup() instanceof CompositePolygonGroup) {
				CompositePolygonGroup compositeGroup = ((CompositePolygonGroup) objectB.getPolygonGroup());
				Vector elements = compositeGroup.getElements();
				boolean collision = false;
				int size = elements.size();
				// FIXME Avoid GameObject creation and don't do exhaustive collision tests (one can guess)
				for (int j = 0; j < size; j++) {
					PolygonGroup group = (PolygonGroup)((PolygonGroup) elements.elementAt(j)).clone();
					group.getTransform().getLocation().add(objectB.getPolygonGroup().getTransform());
					GameObject collisionObject = new GameObject(group);
					collision |= checkObject(objectA, collisionObject, oldLocation);
				}
				return collision;
			} 
			// FIXME Avoid collision detection for skyboxes. Move outside game object list ? 
			else if ((objectB.getPolygonGroup() instanceof BoxPolygonGroup) && ((BoxPolygonGroup)objectB.getPolygonGroup()).isSkybox()) {
				return false;
			} else {
				return handleObjectCollision(objectA, objectB, distSq, minDistSq, oldLocation);
			}
		}
		return false;
	}

	/**
	 Handles an object collision. Object A is the moving
	 object, and Object B is the object that Object A collided
	 with.
	 */
	protected boolean handleObjectCollision(GameObject objectA, GameObject objectB, float distSq, float minDistSq,
			Vector3D oldLocation) {
		objectA.notifyObjectCollision(objectB);
		return true;
	}

}
