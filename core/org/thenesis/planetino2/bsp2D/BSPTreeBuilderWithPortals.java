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

import org.thenesis.planetino2.math3D.Polygon3D;
import org.thenesis.planetino2.math3D.Vector3D;


/**
    The BSPTreeBuilderWithPortals class builds a BSP tree
    and adds portals to the leaves of the tree.

    Note that the portals aren't optimized. For example,
    adjacent collinear portals aren't merged, and "useless"
    portals aren't removed.
*/
public class BSPTreeBuilderWithPortals extends BSPTreeBuilder {


    /**
        Builds a BSP tree and adds portals to the leaves.
    */
    public BSPTree build(Vector polygons) {
        super.build(polygons);
        findPortalsOfLeaves(currentTree.getRoot());
        return currentTree;
    }


    /**
        Finds all the portals of the leaves of the specified node.
    */
    protected void findPortalsOfLeaves(BSPTree.Node node) {
        if (node instanceof BSPTree.Leaf) {
            findPortals((BSPTree.Leaf)node);
        }
        else {
            findPortalsOfLeaves(node.front);
            findPortalsOfLeaves(node.back);
        }
    }


    /**
        Finds all the portals of the specified leaf.
    */
    protected void findPortals(BSPTree.Leaf leaf) {
    	Vector lines = new Vector();
        leaf.portals = new Vector();
        for (int i=0; i<leaf.polygons.size(); i++) {
            Polygon3D poly = (Polygon3D)leaf.polygons.elementAt(i);
            for (int j=0; j<poly.getNumVertices(); j++) {
                int next = (j+1) % poly.getNumVertices();
                Vector3D v1 = poly.getVertex(j);
                Vector3D v2 = poly.getVertex(next);
                BSPLine line = new BSPLine(v1.x, v1.z, v2.x, v2.z);

                // check to see if line was already checked
                boolean checked = false;
                for (int k=0; !checked && k<lines.size(); k++) {
                    if (line.equalsIgnoreOrder(
                        (BSPLine)lines.elementAt(k)))
                    {
                        checked = true;
                    }
                }

                // create the portal
                if (!checked) {
                    lines.addElement(line);
                    Portal portal = createPortal(line);
                    if (portal != null) {
                        leaf.portals.addElement(portal);
                    }
                }
            }
        }
        ((Vector)leaf.portals).trimToSize();
    }


    /**
        Creates a portal for the specified line segment. Returns
        null if no portal could be created (if the line represents
        a solid wall or the line isn't found).
    */
    protected Portal createPortal(BSPLine line) {
        BSPTree.Node node = currentTree.getCollinearNode(line);
        if (node != null && node.polygons != null) {
            for (int i=0; i<node.polygons.size(); i++) {
                BSPPolygon poly = (BSPPolygon)node.polygons.elementAt(i);
                if (poly.isSolidWall() &&
                    line.equalsIgnoreOrder(poly.getLine()))
                {
                    // wall not passable
                    return null;
                }
            }
        }

        BSPTree.Leaf frontLeaf = currentTree.getFrontLeaf(line);
        BSPTree.Leaf backLeaf = currentTree.getBackLeaf(line);
        if (frontLeaf != null && backLeaf != null &&
            frontLeaf != backLeaf && frontLeaf.bounds != null &&
            backLeaf.bounds != null)
        {
            return new Portal(line, frontLeaf, backLeaf);
        }
        else {
            return null;
        }

    }
}
