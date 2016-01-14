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
package org.thenesis.planetino2.bsp2D;

import java.util.Enumeration;
import org.thenesis.planetino2.util.Vector;

import org.thenesis.planetino2.math3D.Vector3D;
import org.thenesis.planetino2.path.AStarNode;

/**
 A Portal represents a passable divider between two
 leaves in a BSP tree (think: entryway between rooms).
 The Portal class is also an AStarNode, so AI creatures
 can use the A* algorithm to find paths throughout the
 BSP tree.
 */
public class Portal extends AStarNode {

	private BSPLine divider;
	private BSPTree.Leaf front;
	private BSPTree.Leaf back;
	private Vector neighbors;
	private Vector3D midPoint;

	/**
	 Create a new Portal with the specified divider and front/
	 back leaves.
	 */
	public Portal(BSPLine divider, BSPTree.Leaf front, BSPTree.Leaf back) {
		this.divider = divider;
		this.front = front;
		this.back = back;
		midPoint = new Vector3D((divider.x1 + divider.x2) / 2, Math.max(front.floorHeight, back.floorHeight),
				(divider.y1 + divider.y2) / 2);
	}

	/**
	 Gets the mid-point along this Portal's divider.
	 */
	public Vector3D getMidPoint() {
		return midPoint;
	}

	/**
	 Builds the list of neighbors for the AStarNode
	 representation. The neighbors are the portals of the
	 front and back leaves, not including this portal.
	 */
	public void buildNeighborList() {
		neighbors = new Vector();
		if (front != null) {
			//neighbors.addAll(front.portals);
			Enumeration e = front.portals.elements();
			while (e.hasMoreElements()) {
				neighbors.addElement(e.nextElement());
			}
		}
		if (back != null) {
			//neighbors.addAll(back.portals);
			Enumeration e = back.portals.elements();
			while (e.hasMoreElements()) {
				neighbors.addElement(e.nextElement());
			}
		}

		// trim to size, then remove references to this node.
		// (ensures extra capacity for calls to addNeighbor()
		// without enlarging the array capacity)
		neighbors.trimToSize();
		while (neighbors.removeElement(this))
			;
	}

	/**
	 Adds a neighbor node to the list of neighbors.
	 */
	public void addNeighbor(AStarNode node) {
		if (neighbors == null) {
			buildNeighborList();
		}
		neighbors.addElement(node);
	}

	/**
	 Removes a neighbor node to the list of neighbors.
	 */
	public void removeNeighbor(AStarNode node) {
		if (neighbors == null) {
			buildNeighborList();
		}
		neighbors.removeElement(node);
	}

	// AStarNode methods

	public float getCost(AStarNode node) {
		return getEstimatedCost(node);
	}

	public float getEstimatedCost(AStarNode node) {
		if (node instanceof Portal) {
			Portal other = (Portal) node;
			float dx = midPoint.x - other.midPoint.x;
			float dz = midPoint.z - other.midPoint.z;
			return (float) Math.sqrt(dx * dx + dz * dz);
		} else {
			return node.getEstimatedCost(this);
		}
	}

	public Vector getNeighbors() {
		if (neighbors == null) {
			buildNeighborList();
		}
		return neighbors;
	}

}