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
package org.thenesis.planetino2.test;

public class BinaryTreeTest {

	public static void main(String[] args) {
		new BinaryTreeTest().run();
	}

	static class Node {
		Node left;
		Node right;
		int value;

		public Node(int value) {
			this.value = value;
		}
	}

	public void run() {
		// build the simple tree from chapter 11.
		Node root = new Node(5);
		System.out.println("Binary Tree Example");
		System.out.println("Building tree with root value " + root.value);
		insert(root, 1);
		insert(root, 8);
		insert(root, 6);
		insert(root, 3);
		insert(root, 9);
		System.out.println("Traversing tree in order");
		printInOrder(root);
		System.out.println("Traversing tree front-to-back from location 7");
		printFrontToBack(root, 7);
	}

	public void insert(Node node, int value) {
		if (value < node.value) {
			if (node.left != null) {
				insert(node.left, value);
			} else {
				System.out.println("  Inserted " + value + " to left of " + node.value);
				node.left = new Node(value);
			}
		} else if (value > node.value) {
			if (node.right != null) {
				insert(node.right, value);
			} else {
				System.out.println("  Inserted " + value + " to right of " + node.value);
				node.right = new Node(value);
			}
		}
	}

	public void printInOrder(Node node) {
		if (node != null) {
			printInOrder(node.left);
			System.out.println("  Traversed " + node.value);
			printInOrder(node.right);
		}
	}

	/**
	 uses in-order traversal when the origin is less than
	 the node's value

	 uses reverse-order traversal when the origin is greater
	 than the node's order
	 */
	public void printFrontToBack(Node node, int camera) {
		if (node == null)
			return;
		if (node.value > camera) {
			// print in order
			printFrontToBack(node.left, camera);
			System.out.println("  Traversed " + node.value);
			printFrontToBack(node.right, camera);
		} else if (node.value < camera) {
			// print reverse order
			printFrontToBack(node.right, camera);
			System.out.println("  Traversed " + node.value);
			printFrontToBack(node.left, camera);
		} else {
			// order doesn't matter
			printFrontToBack(node.left, camera);
			printFrontToBack(node.right, camera);
		}
	}

}
