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

/**
 The PowerOf2Texture class is a Texture with a width and height
 that are a power of 2 (32, 128, etc.).
 */
public final class PowerOf2Texture extends Texture {

	private int[] buffer;
	private int widthBits;
	private int widthMask;
	private int heightBits;
	private int heightMask;

	/**
	 Creates a new PowerOf2Texture with the specified buffer.
	 The width of the bitmap is 2 to the power of widthBits, or
	 (1 << widthBits). Likewise, the height of the bitmap is 2
	 to the power of heightBits, or (1 << heightBits).
	 */
	public PowerOf2Texture(int[] buffer, int widthBits, int heightBits) {
		super(1 << widthBits, 1 << heightBits);
		this.buffer = buffer;
		this.widthBits = widthBits;
		this.heightBits = heightBits;
		this.widthMask = getWidth() - 1;
		this.heightMask = getHeight() - 1;
	}

	/**
	 Gets the 16-bit color of the pixel at location (x,y) in
	 the bitmap.
	 */
	public int getColor(int x, int y) {
		//return Color.convertRBG565To888(buffer[(x & widthMask) + ((y & heightMask) << widthBits)]);
		return buffer[(x & widthMask) + ((y & heightMask) << widthBits)];
	}

	public int[] getRawData() {
		return buffer;
	}

	public int getHeightBits() {
		return heightBits;
	}

	public int getHeightMask() {
		return heightMask;
	}

	public int getWidthBits() {
		return widthBits;
	}

	public int getWidthMask() {
		return widthMask;
	}

}
