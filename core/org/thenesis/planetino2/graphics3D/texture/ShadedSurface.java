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
package org.thenesis.planetino2.graphics3D.texture;

//import java.lang.ref.SoftReference;
import org.thenesis.planetino2.util.Vector;

import org.thenesis.planetino2.math3D.PointLight3D;
import org.thenesis.planetino2.math3D.Rectangle3D;
import org.thenesis.planetino2.math3D.TexturedPolygon3D;
import org.thenesis.planetino2.math3D.Vector3D;

/**
 A ShadedSurface is a pre-shaded Texture that maps onto a
 polygon.
 */
public abstract class ShadedSurface extends Texture {

	public static final int SURFACE_BORDER_SIZE = 1;

	public static final int SHADE_RES_BITS = 4;
	public static final int SHADE_RES = 1 << SHADE_RES_BITS;
	public static final int SHADE_RES_MASK = SHADE_RES - 1;
	public static final int SHADE_RES_SQ = SHADE_RES * SHADE_RES;
	public static final int SHADE_RES_SQ_BITS = SHADE_RES_BITS * 2;

	protected boolean dirty;
	protected ShadedTexture sourceTexture;
	protected Rectangle3D sourceTextureBounds;
	protected Rectangle3D surfaceBounds;
	protected byte[] shadeMap;
	protected int shadeMapWidth;
	protected int shadeMapHeight;

	// for incrementally calculating shade values
	protected int shadeValue;
	protected int shadeValueInc;

	/**
	 Creates a ShadedSurface with the specified width and
	 height.
	 */
	public ShadedSurface(int width, int height) {
		super(width, height);
		sourceTextureBounds = new Rectangle3D();
		dirty = true;
	}

	/**
	 Creates a ShadedSurface for the specified polygon. The
	 shade map is created from the specified list of point
	 lights and ambient light intensity.
	 */
	public static void createShadedSurface(TexturedPolygon3D poly, ShadedTexture texture, Vector lights,
			float ambientLightIntensity) {
		// create the texture bounds
		Vector3D origin = poly.getVertex(0);
		Vector3D dv = new Vector3D(poly.getVertex(1));
		dv.subtract(origin);
		Vector3D du = new Vector3D();
		du.setToCrossProduct(poly.getNormal(), dv);
		Rectangle3D bounds = new Rectangle3D(origin, du, dv, texture.getWidth(), texture.getHeight());

		createShadedSurface(poly, texture, bounds, lights, ambientLightIntensity);
	}

	/**
	 Creates a ShadedSurface for the specified polygon. The
	 shade map is created from the specified list of point
	 lights and ambient light intensity.
	 */
	public static void createShadedSurface(TexturedPolygon3D poly, ShadedTexture texture, Rectangle3D textureBounds,
			Vector lights, float ambientLightIntensity) {

		// create the surface bounds
		poly.setTexture(texture, textureBounds);
		Rectangle3D surfaceBounds = poly.calcBoundingRectangle();

		// give the surfaceBounds a border to correct for
		// slight errors when texture mapping
		Vector3D du = new Vector3D(surfaceBounds.getDirectionU());
		Vector3D dv = new Vector3D(surfaceBounds.getDirectionV());
		du.multiply(SURFACE_BORDER_SIZE);
		dv.multiply(SURFACE_BORDER_SIZE);
		surfaceBounds.getOrigin().subtract(du);
		surfaceBounds.getOrigin().subtract(dv);
		int width = (int) Math.ceil(surfaceBounds.getWidth() + SURFACE_BORDER_SIZE * 2);
		int height = (int) Math.ceil(surfaceBounds.getHeight() + SURFACE_BORDER_SIZE * 2);
		surfaceBounds.setWidth(width);
		surfaceBounds.setHeight(height);

		// Create the shaded surface texture 
		// If surface is too big, disable lightning for now (FIXME)
 		ShadedSurface surface = null;
		if ((width > 1024) && (height > 1024)) {
			surface = new DisabledShadingSurface(width, height, ambientLightIntensity);
		} else {
			surface = new PreShadedSurface(width, height);
		}
		surface.setTexture(texture, textureBounds);
		surface.setSurfaceBounds(surfaceBounds);

		// create the surface's shade map
		surface.buildShadeMap(lights, ambientLightIntensity);

		// set the polygon's surface
		poly.setTexture(surface, surfaceBounds);
	}

	/**
	 Gets the 16-bit color of the pixel at location (x,y) in
	 the bitmap. The x and y values are assumbed to be within
	 the bounds of the surface; otherwise an
	 ArrayIndexOutOfBoundsException occurs.
	 */
	public abstract int getColor(int x, int y);

//	private int getColor565(int x, int y) {
//		return buffer[x + y * width];
//	}

	/**
	 Gets the 16-bit color of the pixel at location (x,y) in
	 the bitmap. The x and y values are checked to be within
	 the bounds of the surface, and if not, the pixel on the
	 edge of the texture is returned.
	 */
	public int getColorChecked(int x, int y) {
		if (x < 0) {
			x = 0;
		} else if (x >= width) {
			x = width - 1;
		}
		if (y < 0) {
			y = 0;
		} else if (y >= height) {
			y = height - 1;
		}
		return getColor(x, y);
	}

	/**
	 Marks whether this surface is dirty. Surfaces marked as
	 dirty may be cleared externally.
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	/**
	 Checks wether this surface is dirty. Surfaces marked as
	 dirty may be cleared externally.
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 Clears this surface, allowing the garbage collector to
	 remove it from memory if needed.
	 */
	public abstract void clearSurface();

	/**
	 Checks if the surface has been cleared.
	 */
	public abstract boolean isCleared();

	/**
	 Sets the source texture for this ShadedSurface.
	 */
	private void setTexture(ShadedTexture texture) {
		this.sourceTexture = texture;
		sourceTextureBounds.setWidth(texture.getWidth());
		sourceTextureBounds.setHeight(texture.getHeight());
	}

	/**
	 Sets the source texture and source bounds for this
	 ShadedSurface.
	 */
	private void setTexture(ShadedTexture texture, Rectangle3D bounds) {
		setTexture(texture);
		sourceTextureBounds.setTo(bounds);
	}

	/**
	 Sets the surface bounds for this ShadedSurface.
	 */
	public void setSurfaceBounds(Rectangle3D surfaceBounds) {
		this.surfaceBounds = surfaceBounds;
	}

	/**
	 Gets the surface bounds for this ShadedSurface.
	 */
	public Rectangle3D getSurfaceBounds() {
		return surfaceBounds;
	}

	/**
	 Builds the surface. First, this method calls
	 retrieveSurface() to see if the surface needs to be
	 rebuilt. If not, the surface is built by tiling the
	 source texture and apply the shade map.
	 */
	public abstract void buildSurface();

	/**
	    Gets the shade (from the shade map) for the  specified
	    (u,v) location. The u and v values should be
	    left-shifted by SHADE_RES_BITS, and the extra bits are
	    used to interpolate between values. For an interpolation
	    example, a location halfway between shade values 1 and 3
	    would return 2.
	*/
	protected int getInterpolatedShade(int u, int v) {

		int fracU = u & SHADE_RES_MASK;
		int fracV = v & SHADE_RES_MASK;

		int offset = (u >> SHADE_RES_BITS) + ((v >> SHADE_RES_BITS) * shadeMapWidth);

		int shade00 = (SHADE_RES - fracV) * shadeMap[offset];
		int shade01 = fracV * shadeMap[offset + shadeMapWidth];
		int shade10 = (SHADE_RES - fracV) * shadeMap[offset + 1];
		int shade11 = fracV * shadeMap[offset + shadeMapWidth + 1];

		shadeValue = SHADE_RES_SQ / 2 + (SHADE_RES - fracU) * shade00 + (SHADE_RES - fracU) * shade01 + fracU * shade10 + fracU * shade11;

		// the value to increment as u increments
		shadeValueInc = -shade00 - shade01 + shade10 + shade11;

		return shadeValue >> SHADE_RES_SQ_BITS;
	}

	/**
	 Gets the shade (from the built shade map) for the
	 specified (u,v) location.
	 */
	protected int getShade(int u, int v) {
		return shadeMap[u + v * shadeMapWidth];
	}

	/**
	 Builds the shade map for this surface from the specified
	 list of point lights and the ambiant light intensity.
	 */
	protected void buildShadeMap(Vector pointLights, float ambientLightIntensity) {

		Vector3D surfaceNormal = surfaceBounds.getNormal();

		int polyWidth = (int) surfaceBounds.getWidth() - SURFACE_BORDER_SIZE * 2;
		int polyHeight = (int) surfaceBounds.getHeight() - SURFACE_BORDER_SIZE * 2;
		// assume SURFACE_BORDER_SIZE is <= SHADE_RES
		shadeMapWidth = polyWidth / SHADE_RES + 4;
		shadeMapHeight = polyHeight / SHADE_RES + 4;
		shadeMap = new byte[shadeMapWidth * shadeMapHeight];

		// calculate the shade map origin
		Vector3D origin = new Vector3D(surfaceBounds.getOrigin());
		Vector3D du = new Vector3D(surfaceBounds.getDirectionU());
		Vector3D dv = new Vector3D(surfaceBounds.getDirectionV());
		du.multiply(SHADE_RES - SURFACE_BORDER_SIZE);
		dv.multiply(SHADE_RES - SURFACE_BORDER_SIZE);
		origin.subtract(du);
		origin.subtract(dv);

		// calculate the shade for each sample point.
		Vector3D point = new Vector3D();
		du.setTo(surfaceBounds.getDirectionU());
		dv.setTo(surfaceBounds.getDirectionV());
		du.multiply(SHADE_RES);
		dv.multiply(SHADE_RES);
		for (int v = 0; v < shadeMapHeight; v++) {
			point.setTo(origin);
			for (int u = 0; u < shadeMapWidth; u++) {
				shadeMap[u + v * shadeMapWidth] = calcShade(surfaceNormal, point, pointLights, ambientLightIntensity);
				point.add(du);
			}
			origin.add(dv);
		}
	}

	public ShadedTexture getSourceTexture() {
		return sourceTexture;
	}

	    /**
	        Determine the shade of a point on the polygon.
	        This computes the Lambertian reflection for a point on
	        the plane. Each point light has an intensity and a
	        distance falloff value, but no specular reflection or
	        shadows from other polygons are computed. The value
	        returned is from 0 to ShadedTexture.MAX_LEVEL.
	    */
	public static byte calcShade(Vector3D normal, Vector3D point, Vector pointLights, float ambientLightIntensity) {
		float intensity = 0;
		
		int size = 0;
		if ((pointLights != null) && ((size = pointLights.size()) > 0)) {
			Vector3D directionToLight = new Vector3D();
			for (int i = 0; i < size; i++) {
				PointLight3D light = (PointLight3D) pointLights.elementAt(i);
				directionToLight.setTo(light);
				directionToLight.subtract(point);

				float distance = directionToLight.length();
				directionToLight.normalize();
				float lightIntensity = light.getIntensity(distance) * directionToLight.getDotProduct(normal);
				lightIntensity = Math.min(lightIntensity, 1);
				lightIntensity = Math.max(lightIntensity, 0);
				intensity += lightIntensity;
			}

			intensity = Math.min(intensity, 1);
			intensity = Math.max(intensity, 0);
		}

		intensity += ambientLightIntensity;
		intensity = Math.min(intensity, 1);
		intensity = Math.max(intensity, 0);
		//int level = Math.round(intensity*ShadedTexture.MAX_LEVEL);
		int level = (int) Math.floor(intensity * ShadedTexture.MAX_LEVEL + 0.5d);
		return (byte) level;
	}
}
