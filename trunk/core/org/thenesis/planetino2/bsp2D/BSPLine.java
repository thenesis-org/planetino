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

import org.thenesis.planetino2.math3D.Vector3D;

public class BSPLine extends Line2D.Float {

    public static final int BACK = -1;
    public static final int COLLINEAR = 0;
    public static final int FRONT = 1;
    public static final int SPANNING = 2;

    /**
        X coordinate of the line normal.
    */
    public float nx;

    /**
        Y coordinate of the line normal.
    */
    public float ny;

    /**
        Top-most location of a line representing a wall.
    */
    public float top;

    /**
        Bottom-most location of a line representing a wall.
    */
    public float bottom;

    /**
        Creates a new line from (0,0) to (0,0)
    */
    public BSPLine() {
        super();
    }


    /**
        Creates a new BSPLine based on the specified BSPPolygon
        (only if the BSPPolygon is a vertical wall).
    */
    public BSPLine(BSPPolygon poly) {
        setTo(poly);
    }


    /**
        Creates a new BSPLine based on the specified coordinates.
    */
    public BSPLine(float x1, float y1, float x2, float y2) {
        setLine(x1, y1, x2, y2);
    }


    /**
        Sets this BSPLine to the specified BSPPolygon
        (only if the BSPPolygon is a vertical wall).
    */
    public void setTo(BSPPolygon poly) {
        if (!poly.isWall()) {
            throw new IllegalArgumentException(
                "BSPPolygon not a wall");
        }
        top = java.lang.Float.MIN_VALUE;
        bottom = java.lang.Float.MAX_VALUE;
        // find the two points (ignoring y) that are farthest apart
        float distance = -1;
        for (int i=0; i<poly.getNumVertices(); i++) {
            Vector3D v1 = poly.getVertex(i);
            top = Math.max(top, v1.y);
            bottom = Math.min(bottom, v1.y);
            for (int j=0; j<poly.getNumVertices(); j++) {
                Vector3D v2 = poly.getVertex(j);
                float newDist = (float)Point2D.distanceSq(
                    v1.x, v1.z, v2.x, v2.z);
                if (newDist > distance) {
                    distance = newDist;
                    x1 = v1.x;
                    y1 = v1.z;
                    x2 = v2.x;
                    y2 = v2.z;
                }
            }
        }
        nx = poly.getNormal().x;
        ny = poly.getNormal().z;
    }

    /**
        Calculates the normal to this line.
    */
    public void calcNormal() {
        nx = y2 - y1;
        ny = x1 - x2;
    }

    /**
        Normalizes the normal of this line (make the normal's
        length 1).
    */
    public void normalize() {
        float length = (float)Math.sqrt(nx * nx + ny * ny);
        nx/=length;
        ny/=length;
    }


    public void setLine(float x1, float y1, float x2, float y2) {
        super.setLine(x1, y1, x2, y2);
        calcNormal();
    }


    public void setLine(double x1, double y1, double x2,
        double y2)
    {
        super.setLine(x1, y1, x2, y2);
        calcNormal();
    }


    /**
        Flips this line so that the end points are reversed (in
        other words, (x1,y1) becomes (x2,y2) and vice versa) and
        the normal is changed to point the opposite direction.
    */
    public void flip() {
        float tx = x1;
        float ty = y1;
        x1 = x2;
        y1 = y2;
        x2 = tx;
        y2 = ty;
        nx = -nx;
        ny = -ny;
    }


    /**
        Sets the top and bottom height of this "wall".
    */
    public void setHeight(float top, float bottom) {
        this.top = top;
        this.bottom = bottom;
    }


    /**
        Returns true if the endpoints of this line match the
        endpoints of the specified line. Ignores normal and height
        values.
    */
    public boolean equals(BSPLine line) {
        return (x1 == line.x1 && x2 == line.x2 &&
            y1 == line.y1 && y2 == line.y2);
    }


    /**
        Returns true if the endpoints of this line match the
        endpoints of the specified line, ignoring endpoint order
        (if the first point of this line is equal to the second
        point of the specified line, and vice versa, returns true).
        Ignores normal and height values.
    */
    public boolean equalsIgnoreOrder(BSPLine line) {
        return equals(line) || (
            (x1 == line.x2 && x2 == line.x1 &&
            y1 == line.y2 && y2 == line.y1));
    }


    public String toString() {
        return "(" + x1 + ", " + y1 + ")->" +
            "(" + x2 + "," + y2 + ")" +
            " bottom: " + bottom + " top: " + top;
    }


    /**
        Gets the side of this line the specified point is on.
        This method treats the line as 1-unit thick, so points
        within this 1-unit border are considered collinear.
        For this to work correctly, the normal of this line
        must be normalized, either by setting this line to a
        polygon or by calling normalize().
        Returns either FRONT, BACK, or COLLINEAR.
    */
    public int getSideThick(float x, float y) {
        int frontSide = getSideThin(x-nx/2, y-ny/2);
        if (frontSide == FRONT) {
            return FRONT;
        }
        else if (frontSide == BACK) {
            int backSide = getSideThin(x+nx/2, y+ny/2);
            if (backSide == BACK) {
                return BACK;
            }
        }
        return COLLINEAR;
    }


    /**
        Gets the side of this line the specified point is on.
        Because of floating point inaccuracy, a collinear line
        will be rare. For this to work correctly, the normal of
        this line must be normalized, either by setting this line
        to a polygon or by calling normalize().
        Returns either FRONT, BACK, or COLLINEAR.
    */
    public int getSideThin(float x, float y) {
        // dot product between vector to the point and the normal
        float side = (x - x1)*nx + (y - y1)*ny;
        return (side < 0)?BACK:(side > 0)?FRONT:COLLINEAR;
    }


    /**
        Gets the side of this line that the specified line segment
        is on. Returns either FRONT, BACK, COLINEAR, or SPANNING.
    */
    public int getSide(Line2D.Float segment) {
        if (this == segment) {
            return COLLINEAR;
        }
        int p1Side = getSideThick(segment.x1, segment.y1);
        int p2Side = getSideThick(segment.x2, segment.y2);
        if (p1Side == p2Side) {
            return p1Side;
        }
        else if (p1Side == COLLINEAR) {
            return p2Side;
        }
        else if (p2Side == COLLINEAR) {
            return p1Side;
        }
        else {
            return SPANNING;
        }
    }


    /**
        Gets the side of this line that the specified polygon
        is on. Returns either FRONT, BACK, COLINEAR, or SPANNING.
    */
    public int getSide(BSPPolygon poly) {
        boolean onFront = false;
        boolean onBack = false;

        // check every point
        for (int i=0; i<poly.getNumVertices(); i++) {
            Vector3D v = poly.getVertex(i);
            int side = getSideThick(v.x, v.z);
            if (side == BSPLine.FRONT) {
                onFront = true;
            }
            else if (side == BSPLine.BACK) {
                onBack = true;
            }
        }

        // classify the polygon
        if (onFront && onBack) {
            return BSPLine.SPANNING;
        }
        else if (onFront) {
            return BSPLine.FRONT;
        }
        else if (onBack) {
            return BSPLine.BACK;
        }
        else {
            return BSPLine.COLLINEAR;
        }
    }


    /**
        Returns the fraction of intersection along this line.
        Returns a value from 0 to 1 if the segments intersect.
        For example, a return value of 0 means the intersection
        occurs at point (x1, y1), 1 means the intersection
        occurs at point (x2, y2), and .5 mean the intersection
        occurs halfway between the two endpoints of this line.
        Returns -1 if the lines are parallel.
    */
    public float getIntersection(Line2D.Float line) {
        // The intersection point I, of two vectors, A1->A2 and
        // B1->B2, is:
        // I = A1 + Ua * (A2 - A1)
        // I = B1 + Ub * (B2 - B1)
        //
        // Solving for Ua gives us the following formula.
        // Ua is returned.
        float denominator = (line.y2 - line.y1) * (x2 - x1) -
            (line.x2 - line.x1) * (y2 - y1);

        // check if the two lines are parallel
        if (denominator == 0) {
            return -1;
        }

        float numerator = (line.x2 - line.x1) * (y1 - line.y1) -
            (line.y2 - line.y1) * (x1 - line.x1);

        return numerator / denominator;
    }


    /**
        Returns the interection point of this line with the
        specified line.
    */
    public Point2D.Float getIntersectionPoint(Line2D.Float line) {
        return getIntersectionPoint(line, null);
    }


    /**
        Returns the interection of this line with the specified
        line. If interesection is null, a new point is created.
    */
    public Point2D.Float getIntersectionPoint(Line2D.Float line,
        Point2D.Float intersection)
    {
        if (intersection == null) {
            intersection = new Point2D.Float();
        }
        float fraction = getIntersection(line);
        intersection.setLocation(
            x1 + fraction * (x2 - x1),
            y1 + fraction * (y2 - y1));
        return intersection;
    }

}
