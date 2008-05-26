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

import org.thenesis.planetino2.math3D.TexturedPolygon3D;
import org.thenesis.planetino2.math3D.Vector3D;

/**
    A BSPPolygon is a TexturedPolygon3D with a type
    (TYPE_FLOOR, TYPE_WALL, or TYPE_PASSABLE_WALL) an
    ambient light intensity value, and a BSPLine representation
    if the type is a TYPE_WALL or TYPE_PASSABLE_WALL.
*/
public class BSPPolygon extends TexturedPolygon3D {

    public static final int TYPE_FLOOR = 0;
    public static final int TYPE_WALL = 1;
    public static final int TYPE_PASSABLE_WALL = 2;

    /**
        How short a wall must be so that monsters/players can
        step over it.
    */
    public static final int PASSABLE_WALL_THRESHOLD = 32;

    /**
        How tall an entryway must be so that monsters/players can
        pass through it
    */
    public static final int PASSABLE_ENTRYWAY_THRESHOLD = 128;


    private int type;
    private float ambientLightIntensity;
    private BSPLine line;

    /**
        Creates a new BSPPolygon with the specified vertices
        and type (TYPE_FLOOR, TYPE_WALL, or TYPE_PASSABLE_WALL).
    */
    public BSPPolygon(Vector3D[] vertices, int type) {
        super(vertices);
        this.type = type;
        ambientLightIntensity = 0.5f;
        if (isWall()) {
            line = new BSPLine(this);
        }
    }


    /**
        Clone this polygon, but with a different set of vertices.
    */
    public BSPPolygon clone(Vector3D[] vertices) {
        BSPPolygon clone = new BSPPolygon(vertices, type);
        clone.setNormal(getNormal());
        clone.setAmbientLightIntensity(getAmbientLightIntensity());
        if (getTexture() != null) {
            clone.setTexture(getTexture(), getTextureBounds());
        }
        return clone;
    }


    /**
        Returns true if the BSPPolygon is a wall.
    */
    public boolean isWall() {
        return (type == TYPE_WALL) || (type == TYPE_PASSABLE_WALL);
    }


    /**
        Returns true if the BSPPolygon is a solid wall (not
        passable).
    */
    public boolean isSolidWall() {
        return type == TYPE_WALL;
    }


    /**
        Gets the line representing the BSPPolygon. Returns null if
        this BSPPolygon is not a wall.
    */
    public BSPLine getLine() {
        return line;
    }


    public void setAmbientLightIntensity(float a) {
        ambientLightIntensity = a;
    }


    public float getAmbientLightIntensity() {
        return ambientLightIntensity;
    }

}
