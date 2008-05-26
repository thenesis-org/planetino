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
 The Vector3D class implements a 3D vector with the
 floating-point values x, y, and z. Vectors can be thought of
 either as a (x,y,z) point or as a vector from (0,0,0) to
 (x,y,z).
 */
public class Vector3D implements Transformable {

	public float x;
	public float y;
	public float z;

	/**
	 Creates a new Vector3D at (0,0,0).
	 */
	public Vector3D() {
		this(0, 0, 0);
	}

	/**
	 Creates a new Vector3D with the same values as the
	 specified Vector3D.
	 */
	public Vector3D(Vector3D v) {
		this(v.x, v.y, v.z);
	}

	/**
	 Creates a new Vector3D with the specified (x, y, z) values.
	 */
	public Vector3D(float x, float y, float z) {
		setTo(x, y, z);
	}

	/**
	 Checks if this Vector3D is equal to the specified Object.
	 They are equal only if the specified Object is a Vector3D
	 and the two Vector3D's x, y, and z coordinates are equal.
	 */
	public boolean equals(Object obj) {
		Vector3D v = (Vector3D) obj;
		return (v.x == x && v.y == y && v.z == z);
	}

	/**
	 Checks if this Vector3D is equal to the specified
	 x, y, and z coordinates.
	 */
	public boolean equals(float x, float y, float z) {
		return (this.x == x && this.y == y && this.z == z);
	}

	/**
	 Sets the vector to the same values as the specified
	 Vector3D.
	 */
	public void setTo(Vector3D v) {
		setTo(v.x, v.y, v.z);
	}

	/**
	 Sets this vector to the specified (x, y, z) values.
	 */
	public void setTo(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 Adds the specified (x, y, z) values to this vector.
	 */
	public void add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}

	/**
	 Subtracts the specified (x, y, z) values to this vector.
	 */
	public void subtract(float x, float y, float z) {
		add(-x, -y, -z);
	}

	/**
	 Adds the specified vector to this vector.
	 */
	public void add(Vector3D v) {
		add(v.x, v.y, v.z);
	}

	/**
	 Subtracts the specified vector from this vector.
	 */
	public void subtract(Vector3D v) {
		add(-v.x, -v.y, -v.z);
	}

	/**
	 Multiplies this vector by the specified value. The new
	 length of this vector will be length()*s.
	 */
	public void multiply(float s) {
		x *= s;
		y *= s;
		z *= s;
	}

	/**
	 Divides this vector by the specified value. The new
	 length of this vector will be length()/s.
	 */
	public void divide(float s) {
		x /= s;
		y /= s;
		z /= s;
	}

	/**
	 Returns the length of this vector as a float.
	 */
	public float length() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	/**
	 Converts this Vector3D to a unit vector, or in other
	 words, a vector of length 1. Same as calling
	 v.divide(v.length()).
	 */
	public void normalize() {
		divide(length());
	}

	/**
	 Converts this Vector3D to a String representation.
	 */
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}

	/**
	 Rotate this vector around the x axis the specified amount.
	 The specified angle is in radians. Use Math.toRadians() to
	 convert from degrees to radians.
	 */
	public void rotateX(float angle) {
		rotateX((float) Math.cos(angle), (float) Math.sin(angle));
	}

	/**
	 Rotate this vector around the y axis the specified amount.
	 The specified angle is in radians. Use Math.toRadians() to
	 convert from degrees to radians.
	 */
	public void rotateY(float angle) {
		rotateY((float) Math.cos(angle), (float) Math.sin(angle));
	}

	/**
	 Rotate this vector around the z axis the specified amount.
	 The specified angle is in radians. Use Math.toRadians() to
	 convert from degrees to radians.
	 */
	public void rotateZ(float angle) {
		rotateZ((float) Math.cos(angle), (float) Math.sin(angle));
	}

	/**
	 Rotate this vector around the x axis the specified amount,
	 using pre-computed cosine and sine values of the angle to
	 rotate.
	 */
	public void rotateX(float cosAngle, float sinAngle) {
		float newY = y * cosAngle - z * sinAngle;
		float newZ = y * sinAngle + z * cosAngle;
		y = newY;
		z = newZ;
	}

	/**
	 Rotate this vector around the y axis the specified amount,
	 using pre-computed cosine and sine values of the angle to
	 rotate.
	 */
	public void rotateY(float cosAngle, float sinAngle) {
		float newX = z * sinAngle + x * cosAngle;
		float newZ = z * cosAngle - x * sinAngle;
		x = newX;
		z = newZ;
	}

	/**
	 Rotate this vector around the y axis the specified amount,
	 using pre-computed cosine and sine values of the angle to
	 rotate.
	 */
	public void rotateZ(float cosAngle, float sinAngle) {
		float newX = x * cosAngle - y * sinAngle;
		float newY = x * sinAngle + y * cosAngle;
		x = newX;
		y = newY;
	}

	/**
	 Adds the specified transform to this vector. This vector
	 is first rotated, then translated.
	 */
	public void add(Transform3D xform) {

		// rotate
		addRotation(xform);

		// translate
		add(xform.getLocation());
	}

	/**
	 Subtracts the specified transform to this vector. This
	 vector translated, then rotated.
	 */
	public void subtract(Transform3D xform) {

		// translate
		subtract(xform.getLocation());

		// rotate
		subtractRotation(xform);
	}

	/**
	 Rotates this vector with the angle of the specified
	 transform.
	 */
	public void addRotation(Transform3D xform) {
		rotateX(xform.getCosAngleX(), xform.getSinAngleX());
		rotateZ(xform.getCosAngleZ(), xform.getSinAngleZ());
		rotateY(xform.getCosAngleY(), xform.getSinAngleY());
	}

	/**
	 Rotates this vector with the opposite angle of the
	 specified transform.
	 */
	public void subtractRotation(Transform3D xform) {
		// note that sin(-x) == -sin(x) and cos(-x) == cos(x)
		rotateY(xform.getCosAngleY(), -xform.getSinAngleY());
		rotateZ(xform.getCosAngleZ(), -xform.getSinAngleZ());
		rotateX(xform.getCosAngleX(), -xform.getSinAngleX());
	}

	/**
	 Returns the dot product of this vector and the specified
	 vector.
	 */
	public float getDotProduct(Vector3D v) {
		return x * v.x + y * v.y + z * v.z;
	}

	/**
	 Sets this vector to the cross product of the two
	 specified vectors. Either of the specified vectors can
	 be this vector.
	 */
	public void setToCrossProduct(Vector3D u, Vector3D v) {
		// assign to local vars first in case u or v is 'this'
		float x = u.y * v.z - u.z * v.y;
		float y = u.z * v.x - u.x * v.z;
		float z = u.x * v.y - u.y * v.x;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 Gets the distance squared between this vector and the
	 specified vector.
	 */
	public float getDistanceSq(Vector3D v) {
		float dx = v.x - x;
		float dy = v.y - y;
		float dz = v.z - z;
		return dx * dx + dy * dy + dz * dz;
	}

}
