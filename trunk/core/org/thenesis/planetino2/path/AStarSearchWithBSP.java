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
package org.thenesis.planetino2.path;

import java.util.Enumeration;
import java.util.Vector;

import org.thenesis.planetino2.bsp2D.BSPTree;
import org.thenesis.planetino2.bsp2D.Portal;
import org.thenesis.planetino2.game.GameObject;
import org.thenesis.planetino2.math3D.Vector3D;

/**
 The AStarSearchWithBSP class is a PathFinder that finds
 a path in a BSP tree using an A* search algorithm.
 */
public class AStarSearchWithBSP extends AStarSearch implements PathFinder {

	/**
	 The LeafNode class is an AStarNode that repesents a
	 location in a leaf of a BSP tree. Used for the start
	 and goal nodes of a search.
	 */
	public static class LeafNode extends AStarNode {
		BSPTree.Leaf leaf;
		Vector3D location;

		public LeafNode(BSPTree.Leaf leaf, Vector3D location) {
			this.leaf = leaf;
			this.location = location;
		}

		public float getCost(AStarNode node) {
			return getEstimatedCost(node);
		}

		public float getEstimatedCost(AStarNode node) {
			float otherX;
			float otherZ;
			if (node instanceof Portal) {
				Portal other = (Portal) node;
				otherX = other.getMidPoint().x;
				otherZ = other.getMidPoint().z;
			} else {
				LeafNode other = (LeafNode) node;
				otherX = other.location.x;
				otherZ = other.location.z;
			}
			float dx = location.x - otherX;
			float dz = location.z - otherZ;
			return (float) Math.sqrt(dx * dx + dz * dz);
		}

		public Vector getNeighbors() {
			return leaf.portals;
		}
	}

	private BSPTree bspTree;

	/**
	 Creates a new AStarSearchWithBSP for the specified
	 BSP tree.
	 */
	public AStarSearchWithBSP(BSPTree bspTree) {
		setBSPTree(bspTree);
	}

	public void setBSPTree(BSPTree bspTree) {
		this.bspTree = bspTree;
	}

	public Enumeration find(GameObject a, GameObject b) {
		return find(a.getLocation(), b.getLocation());
	}

	public Enumeration find(Vector3D start, Vector3D goal) {

		BSPTree.Leaf startLeaf = bspTree.getLeaf(start.x, start.z);
		BSPTree.Leaf goalLeaf = bspTree.getLeaf(goal.x, goal.z);

		// if start and goal is in the same leaf, no need to do
		// A* search
		if (startLeaf == goalLeaf) {
			//return Collections.singleton(goal).iterator();
			Vector v = new Vector();
			v.addElement(goal);
			return v.elements();
		}

		AStarNode startNode = new LeafNode(startLeaf, start);
		AStarNode goalNode = new LeafNode(goalLeaf, goal);

		// temporarily add the goalNode we just created to
		// the neighbors list
		Vector goalNeighbors = goalNode.getNeighbors();
		for (int i = 0; i < goalNeighbors.size(); i++) {
			Portal portal = (Portal) goalNeighbors.elementAt(i);
			portal.addNeighbor(goalNode);
		}

		// do A* search
		Vector path = super.findPath(startNode, goalNode);

		// remove the goal node from the neighbors list
		for (int i = 0; i < goalNeighbors.size(); i++) {
			Portal portal = (Portal) goalNeighbors.elementAt(i);
			portal.removeNeighbor(goalNode);
		}

		return convertPath(path);
	}

	/**
	 Converts path of AStarNodes to a path of Vector3D
	 locations.
	 */
	protected Enumeration convertPath(Vector path) {
		if (path == null) {
			return null;
		}
		for (int i = 0; i < path.size(); i++) {
			Object node = path.elementAt(i);
			if (node instanceof Portal) {
				path.setElementAt(((Portal) node).getMidPoint(), i);
			} else {
				path.setElementAt(((LeafNode) node).location, i);
			}
		}

		//return Collections.unmodifiableList(path).iterator();
		Vector v = new Vector();
		Enumeration e = path.elements();
		while (e.hasMoreElements()) {
			v.addElement(e.nextElement());
		}
		return v.elements();
	}

	public String toString() {
		return "AStarSearchWithBSP";
	}
}
