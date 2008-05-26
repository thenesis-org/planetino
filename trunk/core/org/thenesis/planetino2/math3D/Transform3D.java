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

import org.thenesis.planetino2.util.MoreMath;

/**
 The Transform3D class represents a rotation and translation.
 */
public class Transform3D {

	protected Vector3D location;
	private float cosAngleX;
	private float sinAngleX;
	private float cosAngleY;
	private float sinAngleY;
	private float cosAngleZ;
	private float sinAngleZ;

	/**
	 Creates a new Transform3D with no translation or rotation.
	 */
	public Transform3D() {
		this(0, 0, 0);
	}

	/**
	 Creates a new Transform3D with the specified translation
	 and no rotation.
	 */
	public Transform3D(float x, float y, float z) {
		location = new Vector3D(x, y, z);
		setAngle(0, 0, 0);
	}

	/**
	 Creates a new Transform3D
	 */
	public Transform3D(Transform3D v) {
		location = new Vector3D();
		setTo(v);
	}

	public Object clone() {
		return new Transform3D(this);
	}

	/**
	 Sets this Transform3D to the specified Transform3D.
	 */
	public void setTo(Transform3D v) {
		location.setTo(v.location);
		this.cosAngleX = v.cosAngleX;
		this.sinAngleX = v.sinAngleX;
		this.cosAngleY = v.cosAngleY;
		this.sinAngleY = v.sinAngleY;
		this.cosAngleZ = v.cosAngleZ;
		this.sinAngleZ = v.sinAngleZ;
	}

	/**
	 Gets the location (translation) of this transform.
	 */
	public Vector3D getLocation() {
		return location;
	}

	public float getCosAngleX() {
		return cosAngleX;
	}

	public float getSinAngleX() {
		return sinAngleX;
	}

	public float getCosAngleY() {
		return cosAngleY;
	}

	public float getSinAngleY() {
		return sinAngleY;
	}

	public float getCosAngleZ() {
		return cosAngleZ;
	}

	public float getSinAngleZ() {
		return sinAngleZ;
	}

	public float getAngleX() {
		return (float) MoreMath.atan2(sinAngleX, cosAngleX);
	}

	public float getAngleY() {
		return (float) MoreMath.atan2(sinAngleY, cosAngleY);
	}

	public float getAngleZ() {
		return (float) MoreMath.atan2(sinAngleZ, cosAngleZ);
	}

	public void setAngleX(float angleX) {
		cosAngleX = (float) Math.cos(angleX);
		sinAngleX = (float) Math.sin(angleX);
	}

	public void setAngleY(float angleY) {
		cosAngleY = (float) Math.cos(angleY);
		sinAngleY = (float) Math.sin(angleY);
	}

	public void setAngleZ(float angleZ) {
		cosAngleZ = (float) Math.cos(angleZ);
		sinAngleZ = (float) Math.sin(angleZ);
	}

	public void setAngle(float angleX, float angleY, float angleZ) {
		setAngleX(angleX);
		setAngleY(angleY);
		setAngleZ(angleZ);
	}

	public void rotateAngleX(float angle) {
		if (angle != 0) {
			setAngleX(getAngleX() + angle);
		}
	}

	public void rotateAngleY(float angle) {
		if (angle != 0) {
			setAngleY(getAngleY() + angle);
		}
	}

	public void rotateAngleZ(float angle) {
		if (angle != 0) {
			setAngleZ(getAngleZ() + angle);
		}
	}

	public void rotateAngle(float angleX, float angleY, float angleZ) {
		rotateAngleX(angleX);
		rotateAngleY(angleY);
		rotateAngleZ(angleZ);
	}

}
