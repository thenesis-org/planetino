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

import org.thenesis.planetino2.game.GameObjectManager;
import org.thenesis.planetino2.math3D.Vector3D;

/**
    A BSPTreeTraverer traverses a 2D BSP tree either with a
    in-order or draw-order (front-to-back) order. Visited
    polygons are signaled using a BSPTreeTraverseListener.
*/
public class BSPTreeTraverser {

    private boolean traversing;
    private float x;
    private float z;
    private GameObjectManager objectManager;
    private BSPTreeTraverseListener listener;

    /**
        Creates a new BSPTreeTraverser with no
        BSPTreeTraverseListener.
    */
    public BSPTreeTraverser() {
        this(null);
    }


    /**
        Creates a new BSPTreeTraverser with the specified
        BSPTreeTraverseListener.
    */
    public BSPTreeTraverser(BSPTreeTraverseListener listener) {
        setListener(listener);
    }


    /**
        Sets the BSPTreeTraverseListener to use during traversals.
    */
    public void setListener(BSPTreeTraverseListener listener) {
        this.listener = listener;
    }


    /**
        Sets the GameObjectManager. If the GameObjectManager is
        not null during traversal, then the manager's markVisible()
        method is called to specify visible parts of the tree.
    */
    public void setGameObjectManager(
        GameObjectManager objectManager)
    {
        this.objectManager = objectManager;
    }


    /**
        Traverses a tree in draw-order (front-to-back) using
        the specified view location.
    */
    public void traverse(BSPTree tree, Vector3D viewLocation) {
        x = viewLocation.x;
        z = viewLocation.z;
        traversing = true;
        traverseDrawOrder(tree.getRoot());
    }


    /**
        Traverses a tree in in-order.
    */
    public void traverse(BSPTree tree) {
        traversing = true;
        traverseInOrder(tree.getRoot());
    }


    /**
        Traverses a node in draw-order (front-to-back) using
        the current view location.
    */
    private void traverseDrawOrder(BSPTree.Node node) {
        if (traversing && node != null) {
            if (node instanceof BSPTree.Leaf) {
                // no partition, just handle polygons
                visitNode(node);
            }
            else if (node.partition.getSideThin(x,z) != BSPLine.BACK) {
                traverseDrawOrder(node.front);
                visitNode(node);
                traverseDrawOrder(node.back);
            }
            else {
                traverseDrawOrder(node.back);
                visitNode(node);
                traverseDrawOrder(node.front);
            }
        }

    }


    /**
        Traverses a node in in-order.
    */
    private void traverseInOrder(BSPTree.Node node) {
        if (traversing && node != null) {
            traverseInOrder(node.front);
            visitNode(node);
            traverseInOrder(node.back);
        }
    }


    /**
        Visits a node in the tree. The BSPTreeTraverseListener's
        visitPolygon() method is called for every polygon in
        the node.
    */
    private void visitNode(BSPTree.Node node) {
        if (!traversing || node.polygons == null) {
            return;
        }

        boolean isBack = false;
        if (node instanceof BSPTree.Leaf) {
            BSPTree.Leaf leaf = (BSPTree.Leaf)node;
            isBack = leaf.isBack;
            // mark the bounds of this leaf as visible in
            // the game object manager.
            if (objectManager != null && leaf.bounds != null) {
                objectManager.markVisible(leaf.bounds);
            }
        }

        // visit every polygon
        for (int i=0; traversing && i<node.polygons.size(); i++) {
            BSPPolygon poly = (BSPPolygon)node.polygons.elementAt(i);
            traversing = listener.visitPolygon(poly, isBack);
        }
    }

}
