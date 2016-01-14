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

import org.thenesis.planetino2.util.Vector;

import org.thenesis.planetino2.graphics3D.texture.ShadedSurface;
import org.thenesis.planetino2.graphics3D.texture.ShadedTexture;
import org.thenesis.planetino2.graphics3D.texture.Texture;
import org.thenesis.planetino2.math3D.Point;
import org.thenesis.planetino2.math3D.Rectangle;
import org.thenesis.planetino2.math3D.Vector3D;


/**
    The BSPTree class represents a 2D Binary Space Partitioned
    tree of polygons. The BSPTree is built using a BSPTreeBuilder
    class, and can be travered using BSPTreeTraverser class.
*/
public class BSPTree {

    /**
        A Node of the tree. All children of the node are either
        to the front of back of the node's partition.
    */
    public static class Node {
        public Node front;
        public Node back;
        public BSPLine partition;
        public Vector polygons;
    }


    /**
        A Leaf of the tree. A leaf has no partition or front or
        back nodes.
    */
    public static class Leaf extends Node {
        public float floorHeight;
        public float ceilHeight;
        public boolean isBack;
        public Vector portals;
        public Rectangle bounds;
    }

    private Node root;

    /**
        Creates a new BSPTree with the specified root node.
    */
    public BSPTree(Node root) {
       this.root = root;
    }


    /**
        Gets the root node of this tree.
    */
    public Node getRoot() {
        return root;
    }


    /**
        Calculates the 2D boundary of all the polygons in this
        BSP tree. Returns a rectangle of the bounds.
    */
    public Rectangle calcBounds() {

        final Point min =
            new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        final Point max =
            new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);

        BSPTreeTraverser traverser = new BSPTreeTraverser();
        traverser.setListener(new BSPTreeTraverseListener() {

            public boolean visitPolygon(BSPPolygon poly,
                boolean isBack)
            {
                for (int i=0; i<poly.getNumVertices(); i++) {
                    Vector3D v = poly.getVertex(i);
                    int x = (int)Math.floor(v.x);
                    int y = (int)Math.floor(v.z);
                    min.x = Math.min(min.x, x);
                    max.x = Math.max(max.x, x);
                    min.y = Math.min(min.y, y);
                    max.y = Math.max(max.y, y);
                }

                return true;
            }
        });

        traverser.traverse(this);

        return new Rectangle(min.x, min.y,
            max.x - min.x, max.y - min.y);
    }


    /**
        Gets the leaf the x,z coordinates are in.
    */
    public Leaf getLeaf(float x, float z) {
        return getLeaf(root, x, z);
    }


    protected Leaf getLeaf(Node node, float x, float z) {
        if (node == null || node instanceof Leaf) {
            return (Leaf)node;
        }
        int side = node.partition.getSideThin(x, z);
        if (side == BSPLine.BACK) {
            return getLeaf(node.back, x, z);
        }
        else {
            return getLeaf(node.front, x, z);
        }
    }


    /**
        Gets the Node that is collinear with the specified
        partition, or null if no such node exists.
    */
    public Node getCollinearNode(BSPLine partition) {
        return getCollinearNode(root, partition);
    }


    protected Node getCollinearNode(Node node, BSPLine partition) {
        if (node == null || node instanceof Leaf) {
            return null;
        }
        int side = node.partition.getSide(partition);
        if (side == BSPLine.COLLINEAR) {
            return node;
        }
        if (side == BSPLine.FRONT) {
            return getCollinearNode(node.front, partition);
        }
        else if (side == BSPLine.BACK) {
            return getCollinearNode(node.back, partition);
        }
        else {
            // BSPLine.SPANNING: first try front, then back
            Node front = getCollinearNode(node.front, partition);
            if (front != null) {
                return front;
            }
            else {
                return getCollinearNode(node.back, partition);
            }
        }
    }


    /**
        Gets the Leaf in front of the specified partition.
    */
    public Leaf getFrontLeaf(BSPLine partition) {
        return getLeaf(root, partition, BSPLine.FRONT);
    }


    /**
        Gets the Leaf in back of the specified partition.
    */
    public Leaf getBackLeaf(BSPLine partition) {
        return getLeaf(root, partition, BSPLine.BACK);
    }


    protected Leaf getLeaf(Node node, BSPLine partition, int side)
    {
        if (node == null || node instanceof Leaf) {
            return (Leaf)node;
        }
        int segSide = node.partition.getSide(partition);
        if (segSide == BSPLine.COLLINEAR) {
            segSide = side;
        }
        if (segSide == BSPLine.FRONT) {
            return getLeaf(node.front, partition, side);
        }
        else if (segSide == BSPLine.BACK) {
            return getLeaf(node.back, partition, side);
        }
        else { // BSPLine.SPANNING
            // shouldn't happen
            return null;
        }
    }


    /**
        Creates surface textures for every polygon in this tree.
    */
    public void createSurfaces(final Vector lights) {
        BSPTreeTraverser traverser = new BSPTreeTraverser();
        traverser.setListener(new BSPTreeTraverseListener() {

            public boolean visitPolygon(BSPPolygon poly,
                boolean isBack)
            {
                Texture texture = poly.getTexture();
                if (texture instanceof ShadedTexture) {
                    ShadedSurface.createShadedSurface(poly,
                        (ShadedTexture)texture,
                        poly.getTextureBounds(), lights,
                        poly.getAmbientLightIntensity());
                }
                return true;
            }
        });

        traverser.traverse(this);
    }

}
