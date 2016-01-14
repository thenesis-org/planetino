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

import org.thenesis.planetino2.util.Vector;

import org.thenesis.planetino2.util.Comparable;

/**
 The AStarSearch class, along with the AStarNode class,
 implements a generic A* search algorthim. The AStarNode
 class should be subclassed to provide searching capability.
 */
public class AStarSearch {

	/**
	 A simple priority list, also called a priority queue.
	 Objects in the list are ordered by their priority,
	 determined by the object's Comparable interface.
	 The highest priority item is first in the list.
	 */
	public static class PriorityList extends Vector {

		public void add(Comparable object) {
			for (int i = 0; i < size(); i++) {
				if (object.compareTo(elementAt(i)) <= 0) {
					insertElementAt(object, i);
					return;
				}
			}
			addElement(object);
		}
	}

	/**
	 Construct the path, not including the start node.
	 */
	protected Vector constructPath(AStarNode node) {
		Vector path = new Vector();
		while (node.pathParent != null) {
			path.insertElementAt(node, 0);
			node = node.pathParent;
		}
		return path;
	}

	/**
	 Find the path from the start node to the end node. A list
	 of AStarNodes is returned, or null if the path is not
	 found.
	 */
	public Vector findPath(AStarNode startNode, AStarNode goalNode) {

		PriorityList openList = new PriorityList();
		Vector closedList = new Vector();

		startNode.costFromStart = 0;
		startNode.estimatedCostToGoal = startNode.getEstimatedCost(goalNode);
		startNode.pathParent = null;
		openList.addElement(startNode);

		while (!openList.isEmpty()) {
			AStarNode node = (AStarNode) openList.elementAt(0);
			openList.removeElementAt(0);
			if (node == goalNode) {
				// construct the path from start to goal
				return constructPath(goalNode);
			}

			Vector neighbors = node.getNeighbors();
			for (int i = 0; i < neighbors.size(); i++) {
				AStarNode neighborNode = (AStarNode) neighbors.elementAt(i);
				boolean isOpen = openList.contains(neighborNode);
				boolean isClosed = closedList.contains(neighborNode);
				float costFromStart = node.costFromStart + node.getCost(neighborNode);

				// check if the neighbor node has not been
				// traversed or if a shorter path to this
				// neighbor node is  found.
				if ((!isOpen && !isClosed) || costFromStart < neighborNode.costFromStart) {
					neighborNode.pathParent = node;
					neighborNode.costFromStart = costFromStart;
					neighborNode.estimatedCostToGoal = neighborNode.getEstimatedCost(goalNode);
					if (isClosed) {
						closedList.removeElement(neighborNode);
					}
					if (!isOpen) {
						openList.addElement(neighborNode);
					}
				}
			}
			closedList.addElement(node);
		}

		// no path found
		return null;
	}

}
