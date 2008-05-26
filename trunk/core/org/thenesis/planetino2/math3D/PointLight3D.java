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
 A PointLight3D is a point light that has an intensity
 (between 0 and 1) and optionally a distance falloff value,
 which causes the light to diminish with distance.
 */
public class PointLight3D extends Vector3D {

	public static final float NO_DISTANCE_FALLOFF = -1;

	private float intensity;
	private float distanceFalloff;

	/**
	 Creates a new PointLight3D at (0,0,0) with an intensity
	 of 1 and no distance falloff.
	 */
	public PointLight3D() {
		this(0, 0, 0, 1, NO_DISTANCE_FALLOFF);
	}

	/**
	 Creates a copy of the specified PointLight3D.
	 */
	public PointLight3D(PointLight3D p) {
		setTo(p);
	}

	/**
	 Creates a new PointLight3D with the specified location
	 and intensity. The created light has no distance falloff.
	 */
	public PointLight3D(float x, float y, float z, float intensity) {
		this(x, y, z, intensity, NO_DISTANCE_FALLOFF);
	}

	/**
	 Creates a new PointLight3D with the specified location.
	 intensity, and no distance falloff.
	 */
	public PointLight3D(float x, float y, float z, float intensity, float distanceFalloff) {
		setTo(x, y, z);
		setIntensity(intensity);
		setDistanceFalloff(distanceFalloff);
	}

	/**
	 Sets this PointLight3D to the same location, intensity,
	 and distance falloff as the specified PointLight3D.
	 */
	public void setTo(PointLight3D p) {
		setTo(p.x, p.y, p.z);
		setIntensity(p.getIntensity());
		setDistanceFalloff(p.getDistanceFalloff());
	}

	/**
	 Gets the intensity of this light from the specified
	 distance.
	 */
	public float getIntensity(float distance) {
		if (distanceFalloff == NO_DISTANCE_FALLOFF) {
			return intensity;
		} else if (distance >= distanceFalloff) {
			return 0;
		} else {
			return intensity * (distanceFalloff - distance) / (distanceFalloff + distance);
		}
	}

	/**
	 Gets the intensity of this light.
	 */
	public float getIntensity() {
		return intensity;
	}

	/**
	 Sets the intensity of this light.
	 */
	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}

	/**
	 Gets the distances falloff value. The light intensity is
	 zero beyond this distance.
	 */
	public float getDistanceFalloff() {
		return distanceFalloff;
	}

	/**
	 Sets the distances falloff value. The light intensity is
	 zero beyond this distance. Set to NO_DISTANCE_FALLOFF if
	 the light does not diminish with distance.
	 */
	public void setDistanceFalloff(float distanceFalloff) {
		this.distanceFalloff = distanceFalloff;
	}

}
