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
 A Rectangle3D is a rectangle in 3D space, defined as an origin
 and vectors pointing in the directions of the base (width) and
 side (height).
 */
public class Rectangle3D implements Transformable {

	private Vector3D origin;
	private Vector3D directionU;
	private Vector3D directionV;
	private Vector3D normal;
	private float width;
	private float height;

	/**
	 Creates a rectangle at the origin with a width and height
	 of zero.
	 */
	public Rectangle3D() {
		origin = new Vector3D();
		directionU = new Vector3D(1, 0, 0);
		directionV = new Vector3D(0, 1, 0);
		width = 0;
		height = 0;
	}

	/**
	 Creates a new Rectangle3D with the specified origin,
	 direction of the base (directionU) and direction of
	 the side (directionV).
	 */
	public Rectangle3D(Vector3D origin, Vector3D directionU, Vector3D directionV, float width, float height) {
		this.origin = new Vector3D(origin);
		this.directionU = new Vector3D(directionU);
		this.directionU.normalize();
		this.directionV = new Vector3D(directionV);
		this.directionV.normalize();
		this.width = width;
		this.height = height;
	}

	/**
	 Sets the values of this Rectangle3D to the specified
	 Rectangle3D.
	 */
	public void setTo(Rectangle3D rect) {
		origin.setTo(rect.origin);
		directionU.setTo(rect.directionU);
		directionV.setTo(rect.directionV);
		width = rect.width;
		height = rect.height;
	}

	/**
	 Gets the origin of this Rectangle3D.
	 */
	public Vector3D getOrigin() {
		return origin;
	}

	/**
	 Gets the direction of the base of this Rectangle3D.
	 */
	public Vector3D getDirectionU() {
		return directionU;
	}

	/**
	 Gets the direction of the side of this Rectangle3D.
	 */
	public Vector3D getDirectionV() {
		return directionV;
	}

	/**
	 Gets the width of this Rectangle3D.
	 */
	public float getWidth() {
		return width;
	}

	/**
	 Sets the width of this Rectangle3D.
	 */
	public void setWidth(float width) {
		this.width = width;
	}

	/**
	 Gets the height of this Rectangle3D.
	 */
	public float getHeight() {
		return height;
	}

	/**
	 Sets the height of this Rectangle3D.
	 */
	public void setHeight(float height) {
		this.height = height;
	}

	/**
	 Calculates the normal vector of this Rectange3D.
	 */
	protected Vector3D calcNormal() {
		if (normal == null) {
			normal = new Vector3D();
		}
		normal.setToCrossProduct(directionU, directionV);
		normal.normalize();
		return normal;
	}

	/**
	 Gets the normal of this Rectangle3D.
	 */
	public Vector3D getNormal() {
		if (normal == null) {
			calcNormal();
		}
		return normal;
	}

	/**
	 Sets the normal of this Rectangle3D.
	 */
	public void setNormal(Vector3D n) {
		if (normal == null) {
			normal = new Vector3D(n);
		} else {
			normal.setTo(n);
		}
	}

	public void add(Vector3D u) {
		origin.add(u);
		// don't translate direction vectors or size
	}

	public void subtract(Vector3D u) {
		origin.subtract(u);
		// don't translate direction vectors or size
	}

	public void add(Transform3D xform) {
		addRotation(xform);
		add(xform.getLocation());
	}

	public void subtract(Transform3D xform) {
		subtract(xform.getLocation());
		subtractRotation(xform);
	}

	public void addRotation(Transform3D xform) {
		origin.addRotation(xform);
		directionU.addRotation(xform);
		directionV.addRotation(xform);
	}

	public void subtractRotation(Transform3D xform) {
		origin.subtractRotation(xform);
		directionU.subtractRotation(xform);
		directionV.subtractRotation(xform);
	}

}
