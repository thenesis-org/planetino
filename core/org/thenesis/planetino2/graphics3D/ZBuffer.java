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

/**
 The ZBuffer class implements a z-buffer, or depth-buffer,
 that records the depth of every pixel in a 3D view window.
 The value recorded for each pixel is the inverse of the
 depth (1/z), so there is higher precision for close objects
 and a lower precision for far-away objects (where high
 depth precision is not as visually important).
 */
public class ZBuffer {

	private short[] depthBuffer;
	private int width;
	private int height;

	/**
	 Creates a new z-buffer with the specified width and height.
	 */
	public ZBuffer(int width, int height) {
		depthBuffer = new short[width * height];
		this.width = width;
		this.height = height;
		clear();
	}

	/**
	 Gets the width of this z-buffer.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 Gets the height of this z-buffer.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 Gets the array used for the depth buffer
	 */
	public short[] getArray() {
		return depthBuffer;
	}

	/**
	 Clears the z-buffer. All depth values are set to 0.
	 */
	public void clear() {
		for (int i = 0; i < depthBuffer.length; i++) {
			depthBuffer[i] = 0;
		}
	}

	/**
	 Sets the depth of the pixel at at specified offset,
	 overwriting its current depth.
	 */
	public void setDepth(int offset, short depth) {
		depthBuffer[offset] = depth;
	}

	/**
	 Checks the depth at the specified offset, and if the
	 specified depth is lower (is greater than or equal to the
	 current depth at the specified offset), then the depth is
	 set and this method returns true. Otherwise, no action
	 occurs and this method returns false.
	 */
	public boolean checkDepth(int offset, short depth) {
		if (depth >= depthBuffer[offset]) {
			depthBuffer[offset] = depth;
			return true;
		} else {
			return false;
		}
	}

}