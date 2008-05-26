/*
 * Planetino - Copyright (C) 2007-2008 Guillaume Legris, Mathieu Legris
 * 
 * GNU Classpath - Copyright (C) 1999, 2000, 2002 Free Software Foundation
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
package org.thenesis.planetino2.graphics;

/**
 * @author C. Brian Jones (cbj@gnu.org) 
 */
public class IndexColorModel extends ColorModel {
	private int map_size;
	private boolean opaque;
	private int trans = -1;
	private int[] rgb;

	/**
	 * Each array much contain <code>size</code> elements.  For each 
	 * array, the i-th color is described by reds[i], greens[i], 
	 * blues[i], alphas[i], unless alphas is not specified, then all the 
	 * colors are opaque except for the transparent color. 
	 *
	 * @param bits the number of bits needed to represent <code>size</code> colors
	 * @param size the number of colors in the color map
	 * @param reds the red component of all colors
	 * @param greens the green component of all colors
	 * @param blues the blue component of all colors
	 */
	public IndexColorModel(int bits, int size, byte[] reds, byte[] greens, byte[] blues) {
		this(bits, size, reds, greens, blues, (byte[]) null);
	}

	/**
	 * Each array much contain <code>size</code> elements.  For each 
	 * array, the i-th color is described by reds[i], greens[i], 
	 * blues[i], alphas[i], unless alphas is not specified, then all the 
	 * colors are opaque except for the transparent color. 
	 *
	 * @param bits the number of bits needed to represent <code>size</code> colors
	 * @param size the number of colors in the color map
	 * @param reds the red component of all colors
	 * @param greens the green component of all colors
	 * @param blues the blue component of all colors
	 * @param trans the index of the transparent color
	 */
	public IndexColorModel(int bits, int size, byte[] reds, byte[] greens, byte[] blues, int trans) {
		this(bits, size, reds, greens, blues, (byte[]) null);
		this.trans = trans;
	}

	/**
	 * Each array much contain <code>size</code> elements.  For each 
	 * array, the i-th color is described by reds[i], greens[i], 
	 * blues[i], alphas[i], unless alphas is not specified, then all the 
	 * colors are opaque except for the transparent color. 
	 *
	 * @param bits the number of bits needed to represent <code>size</code> colors
	 * @param size the number of colors in the color map
	 * @param reds the red component of all colors
	 * @param greens the green component of all colors
	 * @param blues the blue component of all colors
	 * @param alphas the alpha component of all colors
	 */
	public IndexColorModel(int bits, int size, byte[] reds, byte[] greens, byte[] blues, byte[] alphas) {
		super(bits);
		map_size = size;
		opaque = (alphas == null);

		rgb = new int[size];
		if (alphas == null) {
			for (int i = 0; i < size; i++) {
				rgb[i] = (0xff000000 | ((reds[i] & 0xff) << 16) | ((greens[i] & 0xff) << 8) | (blues[i] & 0xff));
			}
		} else {
			for (int i = 0; i < size; i++) {
				rgb[i] = ((alphas[i] & 0xff) << 24 | ((reds[i] & 0xff) << 16) | ((greens[i] & 0xff) << 8) | (blues[i] & 0xff));
			}
		}
	}

	/**
	 * Each array much contain <code>size</code> elements.  For each 
	 * array, the i-th color is described by reds[i], greens[i], 
	 * blues[i], alphas[i], unless alphas is not specified, then all the 
	 * colors are opaque except for the transparent color. 
	 *
	 * @param bits the number of bits needed to represent <code>size</code> colors
	 * @param size the number of colors in the color map
	 * @param cmap packed color components
	 * @param start the offset of the first color component in <code>cmap</code>
	 * @param hasAlpha <code>cmap</code> has alpha values
	 */
	public IndexColorModel(int bits, int size, byte[] cmap, int start, boolean hasAlpha) {
		this(bits, size, cmap, start, hasAlpha, -1);
	}

	/**
	 * Each array much contain <code>size</code> elements.  For each 
	 * array, the i-th color is described by reds[i], greens[i], 
	 * blues[i], alphas[i], unless alphas is not specified, then all the 
	 * colors are opaque except for the transparent color. 
	 *
	 * @param bits the number of bits needed to represent <code>size</code> colors
	 * @param size the number of colors in the color map
	 * @param cmap packed color components
	 * @param start the offset of the first color component in <code>cmap</code>
	 * @param hasAlpha <code>cmap</code> has alpha values
	 * @param trans the index of the transparent color
	 */
	public IndexColorModel(int bits, int size, byte[] cmap, int start, boolean hasAlpha, int trans) {
		super(bits);
		map_size = size;
		opaque = !hasAlpha;
		this.trans = trans;
	}

	public final int getMapSize() {
		return map_size;
	}

	/**
	 * Get the index of the transparent color in this color model
	 */
	public final int getTransparentPixel() {
		return trans;
	}

	/**
	 * <br>
	 */
	public final void getReds(byte[] r) {
		getComponents(r, 2);
	}

	/**
	 * <br>
	 */
	public final void getGreens(byte[] g) {
		getComponents(g, 1);
	}

	/**
	 * <br>
	 */
	public final void getBlues(byte[] b) {
		getComponents(b, 0);
	}

	/**
	 * <br>
	 */
	public final void getAlphas(byte[] a) {
		getComponents(a, 3);
	}

	private void getComponents(byte[] c, int ci) {
		int i, max = (map_size < c.length) ? map_size : c.length;
		for (i = 0; i < max; i++)
			c[i] = (byte) ((generateMask(ci) & rgb[i]) >> (ci * pixel_bits));
	}

	/**
	 * Get the red component of the given pixel.
	 */
	public final int getRed(int pixel) {
		if (pixel < map_size)
			return (int) ((generateMask(2) & rgb[pixel]) >> (2 * pixel_bits));

		return 0;
	}

	/**
	 * Get the green component of the given pixel.
	 */
	public final int getGreen(int pixel) {
		if (pixel < map_size)
			return (int) ((generateMask(1) & rgb[pixel]) >> (1 * pixel_bits));

		return 0;
	}

	/**
	 * Get the blue component of the given pixel.
	 */
	public final int getBlue(int pixel) {
		if (pixel < map_size)
			return (int) (generateMask(0) & rgb[pixel]);

		return 0;
	}

	/**
	 * Get the alpha component of the given pixel.
	 */
	public final int getAlpha(int pixel) {
		if (pixel < map_size)
			return (int) ((generateMask(3) & rgb[pixel]) >> (3 * pixel_bits));

		return 0;
	}

	/**
	 * Get the RGB color value of the given pixel using the default
	 * RGB color model. 
	 *
	 * @param pixel a pixel value
	 */
	public final int getRGB(int pixel) {
		if (pixel < map_size)
			return rgb[pixel];

		return 0;
	}

	//pixel_bits is number of bits to be in generated mask
	private int generateMask(int offset) {
		return (((2 << pixel_bits) - 1) << (pixel_bits * offset));
	}

}
