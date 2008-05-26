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
 The Polygon3D class represents a polygon as a series of
 vertices.
 */
public class Polygon3D implements Transformable {

	// temporary vectors used for calculation
	private static Vector3D temp1 = new Vector3D();
	private static Vector3D temp2 = new Vector3D();

	private Vector3D[] v;
	private int numVertices;
	private Vector3D normal;

	/**
	 Creates an empty polygon that can be used as a "scratch"
	 polygon for transforms, projections, etc.
	 */
	public Polygon3D() {
		numVertices = 0;
		v = new Vector3D[0];
		normal = new Vector3D();
	}

	/**
	 Creates a new Polygon3D with the specified vertices.
	 */
	public Polygon3D(Vector3D v0, Vector3D v1, Vector3D v2) {
		this(new Vector3D[] { v0, v1, v2 });
	}

	/**
	 Creates a new Polygon3D with the specified vertices. All
	 the vertices are assumed to be in the same plane.
	 */
	public Polygon3D(Vector3D v0, Vector3D v1, Vector3D v2, Vector3D v3) {
		this(new Vector3D[] { v0, v1, v2, v3 });
	}

	/**
	 Creates a new Polygon3D with the specified vertices. All
	 the vertices are assumed to be in the same plane.
	 */
	public Polygon3D(Vector3D[] vertices) {
		this.v = vertices;
		numVertices = vertices.length;
		calcNormal();
	}

	/**
	 Sets this polygon to the same vertices as the specfied
	 polygon.
	 */
	public void setTo(Polygon3D polygon) {
		numVertices = polygon.numVertices;
		normal.setTo(polygon.normal);

		ensureCapacity(numVertices);
		for (int i = 0; i < numVertices; i++) {
			v[i].setTo(polygon.v[i]);
		}
	}

	/**
	 Ensures this polgon has enough capacity to hold the
	 specified number of vertices.
	 */
	protected void ensureCapacity(int length) {
		if (v.length < length) {
			Vector3D[] newV = new Vector3D[length];
			System.arraycopy(v, 0, newV, 0, v.length);
			for (int i = v.length; i < newV.length; i++) {
				newV[i] = new Vector3D();
			}
			v = newV;
		}
	}

	/**
	 Gets the number of vertices this polygon has.
	 */
	public int getNumVertices() {
		return numVertices;
	}

	/**
	 Gets the vertex at the specified index.
	 */
	public Vector3D getVertex(int index) {
		return v[index];
	}

	/**
	 Projects this polygon onto the view window.
	 */
	public void project(ViewWindow view) {
		for (int i = 0; i < numVertices; i++) {
			view.project(v[i]);
		}
	}

	// methods from the Transformable interface.

	public void add(Vector3D u) {
		for (int i = 0; i < numVertices; i++) {
			v[i].add(u);
		}
	}

	public void subtract(Vector3D u) {
		for (int i = 0; i < numVertices; i++) {
			v[i].subtract(u);
		}
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
		for (int i = 0; i < numVertices; i++) {
			v[i].addRotation(xform);
		}
		normal.addRotation(xform);
	}

	public void subtractRotation(Transform3D xform) {
		for (int i = 0; i < numVertices; i++) {
			v[i].subtractRotation(xform);
		}
		normal.subtractRotation(xform);
	}

	/**
	 Calculates the unit-vector normal of this polygon.
	 This method uses the first, second, and third vertices
	 to calcuate the normal, so if these vertices are
	 collinear, this method will not work. In this case,
	 you can get the normal from the bounding rectangle.
	 Use setNormal() to explicitly set the normal.
	 This method uses static objects in the Polygon3D class
	 for calculations, so this method is not thread-safe across
	 all instances of Polygon3D.
	 */
	public Vector3D calcNormal() {
		if (normal == null) {
			normal = new Vector3D();
		}
		temp1.setTo(v[2]);
		temp1.subtract(v[1]);
		temp2.setTo(v[0]);
		temp2.subtract(v[1]);
		normal.setToCrossProduct(temp1, temp2);
		normal.normalize();
		return normal;
	}

	/**
	 Gets the normal of this polygon. Use calcNormal() if
	 any vertices have changed.
	 */
	public Vector3D getNormal() {
		return normal;
	}

	/**
	 Sets the normal of this polygon.
	 */
	public void setNormal(Vector3D n) {
		if (normal == null) {
			normal = new Vector3D(n);
		} else {
			normal.setTo(n);
		}
	}

	/**
	 Tests if this polygon is facing the specified location.
	 This method uses static objects in the Polygon3D class
	 for calculations, so this method is not thread-safe across
	 all instances of Polygon3D.
	 */
	public boolean isFacing(Vector3D u) {
		temp1.setTo(u);
		temp1.subtract(v[0]);
		return (normal.getDotProduct(temp1) >= 0);
	}

	/**
	 Clips this polygon so that all vertices are in front of
	 the clip plane, clipZ (in other words, all vertices
	 have z <= clipZ).
	 The value of clipZ should not be 0, as this causes
	 divide-by-zero problems.
	 Returns true if the polygon is at least partially in
	 front of the clip plane.
	 */
	public boolean clip(float clipZ) {
		ensureCapacity(numVertices * 3);

		boolean isCompletelyHidden = true;

		// insert vertices so all edges are either completly
		// in front or behind the clip plane
		for (int i = 0; i < numVertices; i++) {
			int next = (i + 1) % numVertices;
			Vector3D v1 = v[i];
			Vector3D v2 = v[next];
			if (v1.z < clipZ) {
				isCompletelyHidden = false;
			}
			// ensure v1.z < v2.z
			if (v1.z > v2.z) {
				Vector3D temp = v1;
				v1 = v2;
				v2 = temp;
			}
			if (v1.z < clipZ && v2.z > clipZ) {
				float scale = (clipZ - v1.z) / (v2.z - v1.z);
				insertVertex(next, v1.x + scale * (v2.x - v1.x), v1.y + scale * (v2.y - v1.y), clipZ);
				// skip the vertex we just created
				i++;
			}
		}

		if (isCompletelyHidden) {
			return false;
		}

		// delete all vertices that have z > clipZ
		for (int i = numVertices - 1; i >= 0; i--) {
			if (v[i].z > clipZ) {
				deleteVertex(i);
			}
		}

		return (numVertices >= 3);
	}

	/**
	 Inserts a new vertex at the specified index.
	 */
	protected void insertVertex(int index, float x, float y, float z) {
		Vector3D newVertex = v[v.length - 1];
		newVertex.x = x;
		newVertex.y = y;
		newVertex.z = z;
		for (int i = v.length - 1; i > index; i--) {
			v[i] = v[i - 1];
		}
		v[index] = newVertex;
		numVertices++;
	}

	/**
	 Delete the vertex at the specified index.
	 */
	protected void deleteVertex(int index) {
		Vector3D deleted = v[index];
		for (int i = index; i < v.length - 1; i++) {
			v[i] = v[i + 1];
		}
		v[v.length - 1] = deleted;
		numVertices--;
	}

	/**
	 Inserts a vertex into this polygon at the specified index.
	 The exact vertex in inserted (not a copy).
	 */
	public void insertVertex(int index, Vector3D vertex) {
		Vector3D[] newV = new Vector3D[numVertices + 1];
		System.arraycopy(v, 0, newV, 0, index);
		newV[index] = vertex;
		System.arraycopy(v, index, newV, index + 1, numVertices - index);
		v = newV;
		numVertices++;
	}

	/**
	 Calculates and returns the smallest bounding rectangle for
	 this polygon.
	 */
	public Rectangle3D calcBoundingRectangle() {

		// the smallest bounding rectangle for a polygon shares
		// at least one edge with the polygon. so, this method
		// finds the bounding rectangle for every edge in the
		// polygon, and returns the smallest one.
		Rectangle3D boundingRect = new Rectangle3D();
		float minimumArea = Float.MAX_VALUE;
		Vector3D u = new Vector3D();
		Vector3D v = new Vector3D();
		Vector3D d = new Vector3D();
		for (int i = 0; i < getNumVertices(); i++) {
			u.setTo(getVertex((i + 1) % getNumVertices()));
			u.subtract(getVertex(i));
			u.normalize();
			v.setToCrossProduct(getNormal(), u);
			v.normalize();

			float uMin = 0;
			float uMax = 0;
			float vMin = 0;
			float vMax = 0;
			for (int j = 0; j < getNumVertices(); j++) {
				if (j != i) {
					d.setTo(getVertex(j));
					d.subtract(getVertex(i));
					float uLength = d.getDotProduct(u);
					float vLength = d.getDotProduct(v);
					uMin = Math.min(uLength, uMin);
					uMax = Math.max(uLength, uMax);
					vMin = Math.min(vLength, vMin);
					vMax = Math.max(vLength, vMax);
				}
			}
			// if this calculated area is the smallest, set
			// the bounding rectangle
			float area = (uMax - uMin) * (vMax - vMin);
			if (area < minimumArea) {
				minimumArea = area;
				Vector3D origin = boundingRect.getOrigin();
				origin.setTo(getVertex(i));
				d.setTo(u);
				d.multiply(uMin);
				origin.add(d);
				d.setTo(v);
				d.multiply(vMin);
				origin.add(d);
				boundingRect.getDirectionU().setTo(u);
				boundingRect.getDirectionV().setTo(v);
				boundingRect.setWidth(uMax - uMin);
				boundingRect.setHeight(vMax - vMin);
			}
		}
		return boundingRect;
	}
}
