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
import java.util.Vector;

import org.thenesis.planetino2.math3D.Rectangle;
import org.thenesis.planetino2.math3D.Vector3D;


/**
    The BSPTreeBuilder class builds a BSP tree from a list
    of polygons. The polygons must be BSPPolygons.

    Currently, the builder does not try to optimize the order of
    the partitions, and could be optimized by choosing partitions
    in an order that minimizes polygon splits and provides a more
    balanced, complete tree.
*/
public class BSPTreeBuilder {

    /**
        The bsp tree currently being built.
    */
    protected BSPTree currentTree;

    /**
        Builds a BSP tree.
    */
    public BSPTree build(Vector polygons) {
        currentTree = new BSPTree(createNewNode(polygons));
        buildNode(currentTree.getRoot());
        return currentTree;
    }


    /**
        Builds a node in the BSP tree.
    */
    protected void buildNode(BSPTree.Node node) {

        // nothing to build if it's a leaf
        if (node instanceof BSPTree.Leaf) {
            return;
        }

        // classify all polygons relative to the partition
        // (front, back, or collinear)
        Vector collinearList = new Vector();
        Vector frontList = new Vector();
        Vector backList = new Vector();
        Vector allPolygons = node.polygons;
        node.polygons = null;
        for (int i=0; i<allPolygons.size(); i++) {
            BSPPolygon poly = (BSPPolygon)allPolygons.elementAt(i);
            int side = node.partition.getSide(poly);
            if (side == BSPLine.COLLINEAR) {
                collinearList.addElement(poly);
            }
            else if (side == BSPLine.FRONT) {
                frontList.addElement(poly);
            }
            else if (side == BSPLine.BACK) {
                backList.addElement(poly);
            }
            else if (side == BSPLine.SPANNING) {
                BSPPolygon front = clipBack(poly, node.partition);
                BSPPolygon back = clipFront(poly, node.partition);
                if (front != null) {
                    frontList.addElement(front);
                }
                if (back != null) {
                    backList.addElement(back);
                }

            }
        }

        // clean and assign lists
        collinearList.trimToSize();
        frontList.trimToSize();
        backList.trimToSize();
        node.polygons = collinearList;
        node.front = createNewNode(frontList);
        node.back = createNewNode(backList);

        // build front and back nodes
        buildNode(node.front);
        buildNode(node.back);
        if (node.back instanceof BSPTree.Leaf) {
            ((BSPTree.Leaf)node.back).isBack = true;
        }
    }


    /**
        Creates a new node from a list of polygons. If none of
        the polygons are walls, a leaf is created.
    */
    protected BSPTree.Node createNewNode(Vector polygons) {

        BSPLine partition = choosePartition(polygons);

        // no partition available, so it's a leaf
        if (partition == null) {
            BSPTree.Leaf leaf = new BSPTree.Leaf();
            leaf.polygons = polygons;
            buildLeaf(leaf);
            return leaf;
        }
        else {
            BSPTree.Node node = new BSPTree.Node();
            node.polygons = polygons;
            node.partition = partition;
            return node;
        }
    }


    /**
        Builds a leaf in the tree, calculating extra information
        like leaf bounds, floor height, and ceiling height.
    */
    protected void buildLeaf(BSPTree.Leaf leaf) {

        if (leaf.polygons.size() == 0) {
            // leaf represents an empty space
            leaf.ceilHeight = Float.MAX_VALUE;
            leaf.floorHeight = Float.MIN_VALUE;
            leaf.bounds = null;
            return;
        }

        float minX = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float minY = Float.MAX_VALUE;
        float maxY = Float.MIN_VALUE;
        float minZ = Float.MAX_VALUE;
        float maxZ = Float.MIN_VALUE;

        // find min y, max y, and bounds
        Enumeration i = leaf.polygons.elements();
        while (i.hasMoreElements()) {
            BSPPolygon poly = (BSPPolygon)i.nextElement();
            for (int j=0; j<poly.getNumVertices(); j++) {
                Vector3D v = poly.getVertex(j);
                minX = Math.min(minX, v.x);
                maxX = Math.max(maxX, v.x);
                minY = Math.min(minY, v.y);
                maxY = Math.max(maxY, v.y);
                minZ = Math.min(minZ, v.z);
                maxZ = Math.max(maxZ, v.z);
            }
        }

        // find any platform within the leaf
        i = leaf.polygons.elements();
        while (i.hasMoreElements()) {
            BSPPolygon poly = (BSPPolygon)i.nextElement();
            // if a floor
            if (poly.getNormal().y == 1) {
                float y = poly.getVertex(0).y;
                if (y > minY && y < maxY) {
                    minY = y;
                }
            }
        }

        // set the leaf values
        leaf.ceilHeight = maxY;
        leaf.floorHeight = minY;
        leaf.bounds = new Rectangle(
            (int)Math.floor(minX), (int)Math.floor(minZ),
            (int)Math.ceil(maxX-minX+1),
            (int)Math.ceil(maxZ-minZ+1));
    }


    /**
        Chooses a line from a list of polygons to use as a
        partition. This method just returns the line formed by
        the first vertical polygon, or null if none found. A
        smarter method would choose a partition that minimizes
        polygon splits and provides a more balanced, complete tree.
    */
    protected BSPLine choosePartition(Vector polygons) {
        for (int i=0; i<polygons.size(); i++) {
            BSPPolygon poly = (BSPPolygon)polygons.elementAt(i);
            if (poly.isWall()) {
                return new BSPLine(poly);
            }
        }
        return null;
    }


    /**
        Clips away the part of the polygon that lines in front
        of the specified line. The returned polygon is the part
        of the polygon in back of the line. Returns null if the
        line does not split the polygon. The original
        polygon is untouched.
    */
    protected BSPPolygon clipFront(BSPPolygon poly, BSPLine line) {
        return clip(poly, line, BSPLine.FRONT);
    }


    /**
        Clips away the part of the polygon that lines in back
        of the specified line. The returned polygon is the part
        of the polygon in front of the line. Returns null if the
        line does not split the polygon. The original
        polygon is untouched.
    */
    protected BSPPolygon clipBack(BSPPolygon poly, BSPLine line) {
        return clip(poly, line, BSPLine.BACK);
    }


    /**
        Clips a BSPPolygon so that the part of the polygon on the
        specified side (either BSPLine.FRONT or BSPLine.BACK)
        is removed, and returnes the clipped polygon. Returns null
        if the line does not split the polygon. The original
        polygon is untouched.
    */
    protected BSPPolygon clip(BSPPolygon poly, BSPLine line,
        int clipSide)
    {
    	Vector vertices = new Vector();
        BSPLine polyEdge = new BSPLine();

        // add vertices that aren't on the clip side
        //Point2D.Float intersection = new Point2D.Float();
        for (int i=0; i<poly.getNumVertices(); i++) {
            int next = (i+1) % poly.getNumVertices();
            Vector3D v1 = poly.getVertex(i);
            Vector3D v2 = poly.getVertex(next);
            int side1 = line.getSideThin(v1.x, v1.z);
            int side2 = line.getSideThin(v2.x, v2.z);
            if (side1 != clipSide) {
                vertices.addElement(v1);
            }

            if ((side1 == BSPLine.FRONT && side2 == BSPLine.BACK) ||
                (side2 == BSPLine.FRONT && side1 == BSPLine.BACK))
            {
                // ensure v1.z < v2.z
                if (v1.z > v2.z) {
                    Vector3D temp = v1;
                    v1 = v2;
                    v2 = temp;
                }
                polyEdge.setLine(v1.x, v1.z, v2.x, v2.z);
                float f = polyEdge.getIntersection(line);
                Vector3D tPoint = new Vector3D(
                    v1.x + f * (v2.x - v1.x),
                    v1.y + f * (v2.y - v1.y),
                    v1.z + f * (v2.z - v1.z));
                vertices.addElement(tPoint);
                // remove any created t-junctions
                removeTJunctions(v1, v2, tPoint);
            }

        }

        // Remove adjacent equal vertices. (A->A) becomes (A)
        for (int i=0; i<vertices.size(); i++) {
            Vector3D v = (Vector3D)vertices.elementAt(i);
            Vector3D next = (Vector3D)vertices.elementAt(
                (i+1) % vertices.size());
            if (v.equals(next)) {
                vertices.removeElementAt(i);
                i--;
            }
        }

        if (vertices.size() < 3) {
            return null;
        }

        // make the polygon
        Vector3D[] array = new Vector3D[vertices.size()];
        //vertices.toArray(array);
        vertices.copyInto(array);
        return poly.clone(array);
    }


    /**
        Remove any T-Junctions from the current tree along the
        line specified by (v1, v2). Find all polygons with this
        edge and insert the T-intersection point between them.
    */
    protected void removeTJunctions(final Vector3D v1,
        final Vector3D v2, final Vector3D tPoint)
    {
        BSPTreeTraverser traverser = new BSPTreeTraverser(
            new BSPTreeTraverseListener() {
                public boolean visitPolygon(BSPPolygon poly,
                    boolean isBackLeaf)
                {
                    removeTJunctions(poly, v1, v2, tPoint);
                    return true;
                }
            }
        );
        traverser.traverse(currentTree);
    }


    /**
        Remove any T-Junctions from the specified polygon. The
        T-intersection point is inserted between the points
        v1 and v2 if there are no other points between them.
    */
    protected void removeTJunctions(BSPPolygon poly,
        Vector3D v1, Vector3D v2, Vector3D tPoint)
    {
        for (int i=0; i<poly.getNumVertices(); i++) {
            int next = (i+1) % poly.getNumVertices();
            Vector3D p1 = poly.getVertex(i);
            Vector3D p2 = poly.getVertex(next);
            if ((p1.equals(v1) && p2.equals(v2)) ||
                (p1.equals(v2) && p2.equals(v1)))
            {
                poly.insertVertex(next, tPoint);
                return;
            }
        }
    }

}
