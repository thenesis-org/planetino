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

import java.io.IOException;

import org.thenesis.planetino2.graphics.Color;
import org.thenesis.planetino2.graphics.DirectColorModel;
import org.thenesis.planetino2.graphics.Image;
import org.thenesis.planetino2.graphics.IndexColorModel;
import org.thenesis.planetino2.graphics.Toolkit;

/**
 The Texture class is an sabstract class that represents a
 16-bit color texture.
 */
public abstract class Texture {

	protected int width;
	protected int height;

	/**
	 Creates a new Texture with the specified width and height.
	 */
	public Texture(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 Gets the width of this Texture.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 Gets the height of this Texture.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 Gets the 16-bit color of this Texture at the specified
	 (x,y) location.
	 */
	public abstract int getColor(int x, int y);

	/**
	 Creates an unshaded Texture from the specified image file.
	 * @throws IOException 
	 */
	public static Texture createTexture(String path, String filename) {
		return createTexture(path, filename, false);
	}

	/**
	 Creates an Texture from the specified image file. If
	 shaded is true, then a ShadedTexture is returned.
	 * @throws IOException 
	 */
	public static Texture createTexture(String path, String filename, boolean shaded) {

		try {
			Image image = Toolkit.getInstance().createImage(path + filename);

			return createTexture(image, shaded);

		} catch (IOException e) {
			e.printStackTrace();
		}

		//System.out.println("[DEBUG] Texture.createTexture(): not implemented yet");

		//System.out.println("AAA:" + path + filename);

		//        try {
		//            return createTexture(ImageIO.read(Texture.class.getResourceAsStream(path + filename)),
		//                shaded);
		//        }
		//        catch (IOException ex) {
		//            ex.printStackTrace();
		//            return null;
		//        }

		return null;
	}

	/**
	 Creates an unshaded Texture from the specified image.
	 */
	public static Texture createTexture(Image image) {
		return createTexture(image, false);
	}

	/**
	 Creates an Texture from the specified image. If
	 shaded is true, then a ShadedTexture is returned.
	 */
	public static Texture createTexture(Image image, boolean shaded) {

		int width = image.getWidth();
		int height = image.getHeight();
		
		if (shaded) {

			int[] rgb888Data = new int[width * height];
			image.getRGB(rgb888Data, 0, 0, width, height);
			
			return new ShadedTexture(rgb888Data, countbits(width - 1), countbits(height - 1));

			//			short[] rgb565Data = new short[width * height];	
			//			convertImageToShort(image, rgb565Data);
			//			return new ShadedTexture(rgb565Data, countbits(width - 1), countbits(height - 1),
			//					new DirectColorModel(32, 0x00FF0000,  0x0000FF00,  0x000000FF));

			//			int w = image.getWidth();
			//			int h = image.getHeight();
			//			byte[] rgb232Data = new byte[width * height];
			//			convertImageTo232(image, rgb232Data);
			//			
			//			//byte[] rgbByteData = new byte[width * height];
			//			//convertImageToByteIndexed(image, rgbByteData);
			//			return new ShadedTexture(rgb232Data, countbits(width - 1), countbits(height - 1),
			//					new DirectColorModel(32, 0x00FF0000,  0x0000FF00,  0x000000FF));
			//return null;
		} else {

			int[] rgb888Data = new int[width * height];
			image.getRGB(rgb888Data, 0, 0, width, height);
			return new PowerOf2Texture(rgb888Data, countbits(width - 1), countbits(height - 1));

			//			short[] rgb565Data = new short[width * height];	
			//			convertImageToShort(image, rgb565Data);
			//			return new PowerOf2Texture(rgb565Data, countbits(width - 1), countbits(height - 1));
		}

		//        int type = image.getType();
		//        int width = image.getWidth();
		//        int height = image.getHeight();
		//
		//        if (!isPowerOfTwo(width) || !isPowerOfTwo(height)) {
		//            throw new IllegalArgumentException(
		//                "Size of texture must be a power of two.");
		//        }
		//
		//        if (shaded) {
		//            // convert image to an indexed image
		//            if (type != BufferedImage.TYPE_BYTE_INDEXED) {
		//                System.out.println("Warning: image converted to " +
		//                    "256-color indexed image. Some quality may " +
		//                    "be lost.");
		//                BufferedImage newImage = new BufferedImage(
		//                    image.getWidth(), image.getHeight(),
		//                    BufferedImage.TYPE_BYTE_INDEXED);
		//                Graphics2D g = newImage.createGraphics();
		//                g.drawImage(image, 0, 0, null);
		//                g.dispose();
		//                image = newImage;
		//            }
		//            DataBuffer dest = image.getRaster().getDataBuffer();
		//            return new ShadedTexture(
		//                ((DataBufferByte)dest).getData(),
		//                countbits(width-1), countbits(height-1),
		//                (IndexColorModel)image.getColorModel());
		//        }
		//        else {
		//            // convert image to an 16-bit image
		//            if (type != BufferedImage.TYPE_USHORT_565_RGB) {
		//                BufferedImage newImage = new BufferedImage(
		//                    image.getWidth(), image.getHeight(),
		//                    BufferedImage.TYPE_USHORT_565_RGB);
		//                Graphics2D g = newImage.createGraphics();
		//                g.drawImage(image, 0, 0, null);
		//                g.dispose();
		//                image = newImage;
		//            }
		//
		//            DataBuffer dest = image.getRaster().getDataBuffer();
		//            return new PowerOf2Texture(
		//                ((DataBufferUShort)dest).getData(),
		//                countbits(width-1), countbits(height-1));
		//        }

	}

	private static void convertImageToShort(Image src, short[] dest) {
		int w = src.getWidth();
		int h = src.getHeight();
		int[] rgbData = new int[w];

		//Random random = new Random();

		// Do conversion for each line
		for (int j = 0; j < h; j++) {
			// getRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height)
			src.getRGB(rgbData, 0, j, w, 1);

			for (int i = 0; i < w; i++) {

				dest[j * w + i] = (short) Color.convertRBG888To565(rgbData[i]);
				//dest[j * w + i] = (short)Color.convertRBG888To565(0x0000FF00); // +  random.nextInt(0xFF));

				//System.out.println(Integer.toHexString(rgbData[i]) + " ==> " + Integer.toHexString(dest[j * w + i]) + " ==> " +  Integer.toHexString(Color.convertRBG565To888(dest[j * w + i])));
			}

		}
	}

	/**
	 * (22:11:27) Mathieu: tu crées un CodeBook (dans org.thenesis.laboratory.graphics)
	 (22:11:48) Mathieu: donc: CodeBook cb=new CodeBook();
	 (22:12:12) Mathieu: ensuite: cb.create(256, 3); // 256 couleurs, 3 composantes (RGB)
	 (22:13:15) Mathieu: ensuite tu remplis directement la palette dans cb.data: cb.data[3*i+0]=r; cb.data[3*i+1]=g; cb.data[3*i+2]=b;
	 (22:13:57) Mathieu: tu crées ensuite l'image que tu veux convertir: Image img=new Image();
	 (22:14:55) Mathieu: img.create( width, height, 0, cb ); // Crée l'image
	 (22:15:58) Mathieu: Remplis l'image comme pour le codebook: image.data
	 (22:16:30) Guillaume: ok j'ai noté tout ça. je vais zessayer
	 (22:16:44) Mathieu: ensuite tu crées l'image destination: Image indexedImg=new Image();
	 (22:17:33) Mathieu: Et tu mappes: cb.map( img, dstImg ) ;
	 (22:17:35) Mathieu: et voilà
	 (22:18:07) Mathieu: tu récupères les indexs dans indexedImg.indexedData
	 (22:18:12) Mathieu: voilà
	 * @param src
	 * @param dest
	 */

	private static void convertImageTo232(Image src, byte[] dest) {

		int w = src.getWidth();
		int h = src.getHeight();
		int[] rgbData = new int[w];

		// Do conversion for each line
		for (int j = 0; j < h; j++) {
			// getRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height)
			src.getRGB(rgbData, 0, 0, w, 1);

			for (int i = 0; i < w; i++) {

				dest[j * w + i] = (byte) Color.convertRBG888To232(rgbData[i]);

				//System.out.println(Integer.toHexString(rgbData[i]) + " ==> " + Integer.toHexString(dest[j * w + i]) + " ==> " +  Integer.toHexString(Color.convertRBG565To888(dest[j * w + i])));
			}

		}

		//		int w = src.getWidth();
		//		int h = src.getHeight();
		//		int[] rgbData = new int[w];
		//
		//		// Do conversion for each line
		//		for (int j = 0; j < h; j++) {
		//			//    	getRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height)
		//			src.getRGB(rgbData, 0, w, 0, 0, w, 1);
		//
		//			//			int[] colorMap = new int[256];
		//			//			for (int i = 0; i < 6; i++)
		//			//				for (int j = 0; j < 6; j++)
		//			//					for (int k = 0; k < 6; k++) {
		//			//						
		//			//						colorMap[ (i + j + k) * 6 ] = (i + j + k) * 6;
		//			//						
		//			//						r[index] = (byte) (i * 51);
		//			//						g[index] = (byte) (j * 51);
		//			//						b[index] = (byte) (k * 51);
		//			//						index++;
		//			//					}
		//
		//			IndexColorModel model = createDefaultIndexedColorModel(false);
		//			
		////			for (int i = 0; i < w; i++) {
		////
		////				int r = ((rgbData[i] >> 16) & 0xFF) / 51;
		////				int g = ((rgbData[i] >> 8) & 0xFF) / 51;
		////				int b = (rgbData[i] & 0xFF) / 51;
		////
		////				dest[j * w + i] = (byte) ((r + 36) + (g + 6) + b);
		////				
		////				System.out.println(Integer.toHexString(rgbData[i]) + " ==> " + Integer.toHexString(r) + " ==> " + Integer.toHexString(model.getRed(dest[j * w + i])));
		////				
		////
		////				//System.out.println(Integer.toHexString(rgbData[i]) + " ==> " + Integer.toHexString(dest[j * w + i]) + " ==> " +  Integer.toHexString(Color.convertRBG565To888(dest[j * w + i])));
		////			}
		//
		//		}
	}

	/**
	 * Creates the default palettes for the predefined indexed color types
	 * (256-color or black-and-white)
	 *
	 * @param binary - If <code>true</code>, a black and white palette,
	 * otherwise a default 256-color palette is returned.
	 */
	private static IndexColorModel createDefaultIndexedColorModel(boolean binary) {
		if (binary) {
			byte[] t = new byte[] { 0, (byte) 255 };
			return new IndexColorModel(1, 2, t, t, t);
		}

		byte[] r = new byte[256];
		byte[] g = new byte[256];
		byte[] b = new byte[256];

		int index = 0;
		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 6; j++)
				for (int k = 0; k < 6; k++) {
					r[index] = (byte) (i * 51);
					g[index] = (byte) (j * 51);
					b[index] = (byte) (k * 51);
					index++;
				}

		while (index < 256) {
			r[index] = g[index] = b[index] = (byte) (18 + (index - 216) * 6);
			index++;
		}

		return new IndexColorModel(8, 256, r, g, b);
	}

	/**
	 Returns true if the specified number is a power of 2.
	 */
	public static boolean isPowerOfTwo(int n) {
		return ((n & (n - 1)) == 0);
	}

	/**
	 Counts the number of "on" bits in an integer.
	 */
	public static int countbits(int n) {
		int count = 0;
		while (n > 0) {
			count += (n & 1);
			n >>= 1;
		}
		return count;
	}

	public static void print(short[] array, int w, int h) {
		for (int j = 0; j < h; j++) {
			for (int i = 0; i < w; i++) {
				System.out.print(array[j * w + i] + " ");
			}
			System.out.println();
		}
	}
	
	public static void print(int[] array, int w, int h) {
		for (int j = 0; j < h; j++) {
			for (int i = 0; i < w; i++) {
				System.out.print(array[j * w + i] + " ");
			}
			System.out.println();
		}
	}
}
