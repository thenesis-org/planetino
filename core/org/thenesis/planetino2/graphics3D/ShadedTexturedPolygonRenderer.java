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
package org.thenesis.planetino2.graphics3D;

import org.thenesis.planetino2.graphics.Graphics;
import org.thenesis.planetino2.graphics3D.texture.ShadedTexture;
import org.thenesis.planetino2.graphics3D.texture.Texture;
import org.thenesis.planetino2.math3D.PointLight3D;
import org.thenesis.planetino2.math3D.TexturedPolygon3D;
import org.thenesis.planetino2.math3D.Transform3D;
import org.thenesis.planetino2.math3D.Vector3D;
import org.thenesis.planetino2.math3D.ViewWindow;

/**
 The ShadedTexturedPolygonRenderer class is a PolygonRenderer
 that renders ShadedTextured dynamically with one light source.
 By default, the ambient light intensity is 0.5 and there
 is no point light.
 */
public class ShadedTexturedPolygonRenderer extends FastTexturedPolygonRenderer {

	private PointLight3D lightSource;
	private float ambientLightIntensity = 0.5f;
	private Vector3D directionToLight = new Vector3D();

	public ShadedTexturedPolygonRenderer(Transform3D camera, ViewWindow viewWindow) {
		this(camera, viewWindow, true);
	}

	public ShadedTexturedPolygonRenderer(Transform3D camera, ViewWindow viewWindow, boolean clearViewEveryFrame) {
		super(camera, viewWindow, clearViewEveryFrame);
	}

	/**
	 Gets the light source for this renderer.
	 */
	public PointLight3D getLightSource() {
		return lightSource;
	}

	/**
	 Sets the light source for this renderer.
	 */
	public void setLightSource(PointLight3D lightSource) {
		this.lightSource = lightSource;
	}

	/**
	 Gets the ambient light intensity.
	 */
	public float getAmbientLightIntensity() {
		return ambientLightIntensity;
	}

	/**
	 Sets the ambient light intensity, generally between 0 and
	 1.
	 */
	public void setAmbientLightIntensity(float i) {
		ambientLightIntensity = i;
	}

	protected void drawCurrentPolygon(Graphics g) {
		// set the shade level of the polygon before drawing it
		if (sourcePolygon instanceof TexturedPolygon3D) {
			TexturedPolygon3D poly = ((TexturedPolygon3D) sourcePolygon);
			Texture texture = poly.getTexture();
			if (texture instanceof ShadedTexture) {
				calcShadeLevel();
			}
		}
		super.drawCurrentPolygon(g);
	}

	/**
	 Calculates the shade level of the current polygon
	 */
	private void calcShadeLevel() {
		TexturedPolygon3D poly = (TexturedPolygon3D) sourcePolygon;
		float intensity = 0;
		if (lightSource != null) {

			// average all the vertices in the polygon
			directionToLight.setTo(0, 0, 0);
			for (int i = 0; i < poly.getNumVertices(); i++) {
				directionToLight.add(poly.getVertex(i));
			}
			directionToLight.divide(poly.getNumVertices());

			// make the vector from the average vertex
			// to the light
			directionToLight.subtract(lightSource);
			directionToLight.multiply(-1);

			// get the distance to the light for falloff
			float distance = directionToLight.length();

			// compute the diffuse reflect
			directionToLight.normalize();
			Vector3D normal = poly.getNormal();
			intensity = lightSource.getIntensity(distance) * directionToLight.getDotProduct(normal);
			intensity = Math.min(intensity, 1);
			intensity = Math.max(intensity, 0);
		}

		intensity += ambientLightIntensity;
		intensity = Math.min(intensity, 1);
		intensity = Math.max(intensity, 0);
		int level = (int) Math.floor(intensity * ShadedTexture.MAX_LEVEL + 0.5d);
		((ShadedTexture) poly.getTexture()).setDefaultShadeLevel(level);
	}

}
