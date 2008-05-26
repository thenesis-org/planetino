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
package org.thenesis.planetino2.math3D;

import org.thenesis.planetino2.graphics.Color;

/**
 The SolidPolygon3D class is a Polygon with a color.
 */
public class SolidPolygon3D extends Polygon3D {

	private Color color = Color.GREEN; // Green

	public SolidPolygon3D() {
		super();
	}

	public SolidPolygon3D(Vector3D v0, Vector3D v1, Vector3D v2) {
		this(new Vector3D[] { v0, v1, v2 });
	}

	public SolidPolygon3D(Vector3D v0, Vector3D v1, Vector3D v2, Vector3D v3) {
		this(new Vector3D[] { v0, v1, v2, v3 });
	}

	public SolidPolygon3D(Vector3D[] vertices) {
		super(vertices);
	}

	public void setTo(Polygon3D polygon) {
		super.setTo(polygon);
		if (polygon instanceof SolidPolygon3D) {
			color = ((SolidPolygon3D) polygon).color;
		}
	}

	/**
	 Gets the color of this solid-colored polygon used for
	 rendering this polygon.
	 */
	public Color getColor() {
		return color;
	}

	/**
	 Sets the color of this solid-colored polygon used for
	 rendering this polygon.
	 */
	public void setColor(Color color) {
		this.color = color;
	}

}
