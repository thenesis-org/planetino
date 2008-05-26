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

import org.thenesis.planetino2.graphics3D.texture.Texture;

/**
 The TexturedPolygon3D class is a Polygon with a texture.
 */
public class TexturedPolygon3D extends Polygon3D {

	protected Rectangle3D textureBounds;
	protected Texture texture;

	public TexturedPolygon3D() {
		textureBounds = new Rectangle3D();
	}

	public TexturedPolygon3D(Vector3D v0, Vector3D v1, Vector3D v2) {
		this(new Vector3D[] { v0, v1, v2 });
	}

	public TexturedPolygon3D(Vector3D v0, Vector3D v1, Vector3D v2, Vector3D v3) {
		this(new Vector3D[] { v0, v1, v2, v3 });
	}

	public TexturedPolygon3D(Vector3D[] vertices) {
		super(vertices);
		textureBounds = new Rectangle3D();
	}

	public void setTo(Polygon3D poly) {
		super.setTo(poly);
		if (poly instanceof TexturedPolygon3D) {
			TexturedPolygon3D tPoly = (TexturedPolygon3D) poly;
			textureBounds.setTo(tPoly.textureBounds);
			texture = tPoly.texture;
		}
	}

	/**
	 Gets this polygon's texture.
	 */
	public Texture getTexture() {
		return texture;
	}

	/**
	 Gets this polygon's texture bounds.
	 */
	public Rectangle3D getTextureBounds() {
		return textureBounds;
	}

	/**
	 Sets this polygon's texture.
	 */
	public void setTexture(Texture texture) {
		this.texture = texture;
		textureBounds.setWidth(texture.getWidth());
		textureBounds.setHeight(texture.getHeight());
	}

	/**
	 Sets this polygon's texture and texture bounds.
	 */
	public void setTexture(Texture texture, Rectangle3D bounds) {
		setTexture(texture);
		textureBounds.setTo(bounds);
	}

	public void add(Vector3D u) {
		super.add(u);
		textureBounds.add(u);
	}

	public void subtract(Vector3D u) {
		super.subtract(u);
		textureBounds.subtract(u);
	}

	public void addRotation(Transform3D xform) {
		super.addRotation(xform);
		textureBounds.addRotation(xform);
	}

	public void subtractRotation(Transform3D xform) {
		super.subtractRotation(xform);
		textureBounds.subtractRotation(xform);
	}

	/**
	 Calculates the bounding rectangle for this polygon that
	 is aligned with the texture bounds.
	 */
	public Rectangle3D calcBoundingRectangle() {

		Vector3D u = new Vector3D(textureBounds.getDirectionU());
		Vector3D v = new Vector3D(textureBounds.getDirectionV());
		Vector3D d = new Vector3D();
		u.normalize();
		v.normalize();

		float uMin = 0;
		float uMax = 0;
		float vMin = 0;
		float vMax = 0;
		for (int i = 0; i < getNumVertices(); i++) {
			d.setTo(getVertex(i));
			d.subtract(getVertex(0));
			float uLength = d.getDotProduct(u);
			float vLength = d.getDotProduct(v);
			uMin = Math.min(uLength, uMin);
			uMax = Math.max(uLength, uMax);
			vMin = Math.min(vLength, vMin);
			vMax = Math.max(vLength, vMax);
		}

		Rectangle3D boundingRect = new Rectangle3D();
		Vector3D origin = boundingRect.getOrigin();
		origin.setTo(getVertex(0));
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

		// explictly set the normal since the texture directions
		// could create a normal negative to the polygon normal
		boundingRect.setNormal(getNormal());

		return boundingRect;
	}

}
