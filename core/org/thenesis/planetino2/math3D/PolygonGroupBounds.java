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

/**
 The PolygonGroupBounds represents a cylinder bounds around a
 PolygonGroup that can be used for collision detection.
 */
public class PolygonGroupBounds {

	private float topHeight;
	private float bottomHeight;
	private float radius;

	/**
	 Creates a new PolygonGroupBounds with no bounds.
	 */
	PolygonGroupBounds() {
	}

	/**
	 Creates a new PolygonGroupBounds with the bounds of
	 the specified PolygonGroup.
	 */
	PolygonGroupBounds(PolygonGroup group) {
		setToBounds(group);
	}

	/**
	 Sets this to the bounds of the specified PolygonGroup.
	 */
	public void setToBounds(PolygonGroup group) {
		topHeight = Float.MIN_VALUE;
		bottomHeight = Float.MAX_VALUE;
		radius = 0;

		group.resetIterator();
		while (group.hasNext()) {
			Polygon3D poly = group.nextPolygon();
			for (int i = 0; i < poly.getNumVertices(); i++) {
				Vector3D v = poly.getVertex(i);
				topHeight = Math.max(topHeight, v.y);
				bottomHeight = Math.min(bottomHeight, v.y);
				// compute radius squared
				radius = Math.max(radius, v.x * v.x + v.z * v.z);
			}
		}

		if (radius == 0) {
			// empty polygon group!
			topHeight = 0;
			bottomHeight = 0;
		} else {
			radius = (float) Math.sqrt(radius);
		}
	}

	public float getTopHeight() {
		return topHeight;
	}

	public void setTopHeight(float topHeight) {
		this.topHeight = topHeight;
	}

	public float getBottomHeight() {
		return bottomHeight;
	}

	public void setBottomHeight(float bottomHeight) {
		this.bottomHeight = bottomHeight;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

}
