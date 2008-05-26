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

import org.thenesis.planetino2.graphics.Color;
import org.thenesis.planetino2.graphics.DirectColorModel;

/**
 The ShadedTexture class is a Texture that has multiple
 shades. The texture source image is stored as a 8-bit image
 with a palette for every shade.
 */
public final class ShadedTexture extends Texture {

	public static final int NUM_SHADE_LEVELS = 64;
	public static final int MAX_LEVEL = NUM_SHADE_LEVELS - 1;

	private static final int PALETTE_SIZE_BITS = 8;
	private static final int PALETTE_SIZE = 1 << PALETTE_SIZE_BITS;

	private int[] buffer;
	private DirectColorModel palette;
	private short[] shadeTable;
	private int defaultShadeLevel;
	private int widthBits;
	private int widthMask;
	private int heightBits;
	private int heightMask;

	// the row set in setCurrRow and used in getColorCurrRow
	private int currRow;

	/**
	 Creates a new ShadedTexture from the specified 8-bit image
	 buffer and palette. The width of the bitmap is 2 to the
	 power of widthBits, or (1 << widthBits). Likewise, the
	 height of the bitmap is 2 to the power of heightBits, or
	 (1 << heightBits). The texture is shaded from it's
	 original color to black.
	 */
	public ShadedTexture(int[] buffer, int widthBits, int heightBits, DirectColorModel palette) {
		this(buffer, widthBits, heightBits, palette, Color.BLACK);
	}

	/**
	 Creates a new ShadedTexture from the specified 8-bit image
	 buffer, palette, and target shaded. The width of the
	 bitmap is 2 to the power of widthBits, or (1 << widthBits).
	 Likewise, the height of the bitmap is 2 to the power of
	 heightBits, or (1 << heightBits). The texture is shaded
	 from it's original color to the target shade.
	 */
	public ShadedTexture(int[] buffer, int widthBits, int heightBits, DirectColorModel palette, Color targetShade) {
		super(1 << widthBits, 1 << heightBits);
		this.buffer = buffer;
		this.widthBits = widthBits;
		this.heightBits = heightBits;
		this.widthMask = getWidth() - 1;
		this.heightMask = getHeight() - 1;
		this.palette = palette;
		defaultShadeLevel = MAX_LEVEL;

		//makeShadeTable(targetShade);
	}

	//	/**
	//	 Creates the shade table for this ShadedTexture. Each entry
	//	 in the palette is shaded from the original color to the
	//	 specified target color.
	//	 */
	//	public void makeShadeTable(Color targetShade) {
	//
	//		shadeTable = new short[NUM_SHADE_LEVELS * PALETTE_SIZE];
	//
	//		for (int level = 0; level < NUM_SHADE_LEVELS; level++) {
	//			for (int i = 0; i < 64; i++) {
	//				int red = calcColor(i, targetShade.getRed(), level);
	//				int green = calcColor(i, targetShade.getGreen(), level);
	//				int blue = calcColor(i, targetShade.getBlue(), level);
	//
	//				int index = level * PALETTE_SIZE + i;
	//				// RGB 5:6:5
	//				shadeTable[index] = (short) (((red >> 3) << 11) | ((green >> 2) << 5) | (blue >> 3));
	//				//shadeTable[index] = (((red << 16) | (green  << 8) | (blue >> 3)));
	//				//System.out.println(Integer.toHexString(i) + " ==> " + Integer.toHexString(shadeTable[index]));
	//			}
	//		}
	//	}

	//	private int calcColor(int palColor, int target, int level) {
	//		return (palColor - target) * (level + 1) / NUM_SHADE_LEVELS + target;
	//	}

	/**
	 Sets the default shade level that is used when getColor()
	 is called.
	 */
	public void setDefaultShadeLevel(int level) {
		defaultShadeLevel = level;
	}

	/**
	 Gets the default shade level that is used when getColor()
	 is called.
	 */
	public int getDefaultShadeLevel() {
		return defaultShadeLevel;
	}

	/**
	 Gets the 16-bit color of this Texture at the specified
	 (x,y) location, using the default shade level.
	 */
	public int getColor(int x, int y) {

		return getColor(x, y, defaultShadeLevel);
	}

	/**
	 Gets the 16-bit color of this Texture at the specified
	 (x,y) location, using the specified shade level.
	 */
	public int getColor(int x, int y, int shadeLevel) {

		//Texture.print(buffer, width, height);

		//		return shadeTable[(shadeLevel << PALETTE_SIZE_BITS)
		//				| (buffer[(x & widthMask) | ((y & heightMask) << widthBits)])];
		//System.out.println("[DEBUG] ShadedTexture.getColor(): " + Integer.toHexString(buffer[(x & widthMask) | ((y & heightMask) << widthBits)]));
		//return Color.convertRBG232To888(buffer[(x & widthMask) | ((y & heightMask) << widthBits)]);

		//		return Color.convertRBG565To888 (shadeTable[(shadeLevel << PALETTE_SIZE_BITS)
		//	  				| (buffer[(x & widthMask) | ((y & heightMask) << widthBits)])]);

		//return Color.convertRBG565To888 (buffer[(x & widthMask) | ((y & heightMask) << widthBits)]);
		return buffer[(x & widthMask) | ((y & heightMask) << widthBits)];

		//return buffer[(x & widthMask) | ((y & heightMask) << widthBits)];
	}

	/**
	 Sets the current row for getColorCurrRow(). Pre-calculates
	 the offset for this row.
	 */
	public void setCurrRow(int y) {
		currRow = (y & heightMask) << widthBits;
	}

	/**
	 Gets the color at the specified x location at the specified
	 shade level. The current row defined in setCurrRow is
	 used.
	 */
	public short getColorCurrRow(int x, int shadeLevel) {
		return shadeTable[(shadeLevel << PALETTE_SIZE_BITS) | (0xff & buffer[(x & widthMask) | currRow])];
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
