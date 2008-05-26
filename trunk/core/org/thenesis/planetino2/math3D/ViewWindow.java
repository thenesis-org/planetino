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
 The ViewWindow class represents the geometry of a view window
 for 3D viewing.
 */
public class ViewWindow {

	private Rectangle bounds;
	private float angle;
	private float distanceToCamera;

	/**
	 Creates a new ViewWindow with the specified bounds on the
	 screen and horizontal view angle.
	 */
	public ViewWindow(int left, int top, int width, int height, float angle) {
		bounds = new Rectangle();
		this.angle = angle;
		setBounds(left, top, width, height);
	}

	/**
	 Sets the bounds for this ViewWindow on the screen.
	 */
	public void setBounds(int left, int top, int width, int height) {
		bounds.x = left;
		bounds.y = top;
		bounds.width = width;
		bounds.height = height;
		distanceToCamera = (bounds.width / 2) / (float) Math.tan(angle / 2);
	}

	/**
	 Sets the horizontal view angle for this ViewWindow.
	 */
	public void setAngle(float angle) {
		this.angle = angle;
		distanceToCamera = (bounds.width / 2) / (float) Math.tan(angle / 2);
	}

	/**
	 Gets the horizontal view angle of this view window.
	 */
	public float getAngle() {
		return angle;
	}

	/**
	 Gets the width of this view window.
	 */
	public int getWidth() {
		return bounds.width;
	}

	/**
	 Gets the height of this view window.
	 */
	public int getHeight() {
		return bounds.height;
	}

	/**
	 Gets the y offset of this view window on the screen.
	 */
	public int getTopOffset() {
		return bounds.y;
	}

	/**
	 Gets the x offset of this view window on the screen.
	 */
	public int getLeftOffset() {
		return bounds.x;
	}

	/**
	 Gets the distance from the camera to to this view window.
	 */
	public float getDistance() {
		return distanceToCamera;
	}

	/**
	 Converts an x coordinate on this view window to the
	 corresponding x coordinate on the screen.
	 */
	public float convertFromViewXToScreenX(float x) {
		return x + bounds.x + bounds.width / 2;
	}

	/**
	 Converts a y coordinate on this view window to the
	 corresponding y coordinate on the screen.
	 */
	public float convertFromViewYToScreenY(float y) {
		return -y + bounds.y + bounds.height / 2;
	}

	/**
	 Converts an x coordinate on the screen to the
	 corresponding x coordinate on this view window.
	 */
	public float convertFromScreenXToViewX(float x) {
		return x - bounds.x - bounds.width / 2;
	}

	/**
	 Converts an y coordinate on the screen to the
	 corresponding y coordinate on this view window.
	 */
	public float convertFromScreenYToViewY(float y) {
		return -y + bounds.y + bounds.height / 2;
	}

	/**
	 Projects the specified vector to the screen.
	 */
	public void project(Vector3D v) {
		// project to view window
		v.x = distanceToCamera * v.x / -v.z;
		v.y = distanceToCamera * v.y / -v.z;

		// convert to screen coordinates
		v.x = convertFromViewXToScreenX(v.x);
		v.y = convertFromViewYToScreenY(v.y);
	}
}
