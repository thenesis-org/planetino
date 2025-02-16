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

import java.util.Hashtable;

import org.thenesis.planetino2.graphics.Graphics;
import org.thenesis.planetino2.graphics.Image;
import org.thenesis.planetino2.graphics.Screen;
import org.thenesis.planetino2.graphics3D.texture.PowerOf2Texture;
import org.thenesis.planetino2.graphics3D.texture.ShadedSurface;
import org.thenesis.planetino2.graphics3D.texture.ShadedTexture;
import org.thenesis.planetino2.graphics3D.texture.Texture;
import org.thenesis.planetino2.math3D.Rectangle3D;
import org.thenesis.planetino2.math3D.TexturedPolygon3D;
import org.thenesis.planetino2.math3D.Transform3D;
import org.thenesis.planetino2.math3D.Vector3D;
import org.thenesis.planetino2.math3D.ViewWindow;

/**
 The FastTexturedPolygonRenderer is a PolygonRenderer that
 efficiently renders Textures.
 */
public class FastTexturedPolygonRenderer extends PolygonRenderer {

	public static final int SCALE_BITS = 12;
	public static final int SCALE = 1 << SCALE_BITS;

	public static final int INTERP_SIZE_BITS = 4;
	public static final int INTERP_SIZE = 1 << INTERP_SIZE_BITS;

	protected Vector3D a = new Vector3D();
	protected Vector3D b = new Vector3D();
	protected Vector3D c = new Vector3D();
	protected Vector3D viewPos = new Vector3D();
	//protected BufferedImage doubleBuffer;
	protected Image offscreenImage;
	protected int[] doubleBufferData;
	protected Hashtable scanRenderers;

	public FastTexturedPolygonRenderer(Transform3D camera, ViewWindow viewWindow) {
		this(camera, viewWindow, true);
	}

	public FastTexturedPolygonRenderer(Transform3D camera, ViewWindow viewWindow, boolean clearViewEveryFrame) {
		super(camera, viewWindow, clearViewEveryFrame);
	}

	protected void init() {
		destPolygon = new TexturedPolygon3D();
		scanConverter = new ScanConverter(viewWindow);

		// create renders for each texture (HotSpot optimization)
		scanRenderers = new Hashtable();
		scanRenderers.put(PowerOf2Texture.class, new PowerOf2TextureRenderer());
		scanRenderers.put(ShadedTexture.class, new ShadedTextureRenderer());
		scanRenderers.put(ShadedSurface.class, new ShadedSurfaceRenderer());

	}

	public void startFrame(Screen screen) {
		//Graphics g = screen.getGraphics();
		// initialize buffer
		//		if (doubleBuffer == null || doubleBuffer.getWidth() != viewWindow.getWidth()
		//				|| doubleBuffer.getHeight() != viewWindow.getHeight()) {
		//			doubleBuffer = new BufferedImage(viewWindow.getWidth(), viewWindow.getHeight(),
		//					BufferedImage.TYPE_USHORT_565_RGB);
		//			//doubleBuffer = g.getDeviceConfiguration().createCompatibleImage(
		//			//viewWindow.getWidth(), viewWindow.getHeight());
		//
		//			DataBuffer dest = doubleBuffer.getRaster().getDataBuffer();
		//			doubleBufferData = ((DataBufferUShort) dest).getData();
		//		}

		//		 initialize buffer
		if (doubleBufferData == null) {
			doubleBufferData = new int[viewWindow.getWidth() * viewWindow.getHeight()];
			//offscreenImage = Image.createImage(viewWindow.getWidth(), viewWindow.getHeight());
		}

		// clear view
		if (clearViewEveryFrame) {
			for (int i = 0; i < doubleBufferData.length; i++) {
				doubleBufferData[i] = 0;
			}
		}
	}

	public void endFrame(Screen screen) {
		Graphics g = screen.getGraphics();
		// draw the double buffer onto the screen
		//g.drawImage(doubleBuffer, viewWindow.getLeftOffset(), viewWindow.getTopOffset(), null);
		//System.out.println("[DEBUG] FastTexturedPolygonRenderer.endFrame(): not implemented yet");

		//		int w = viewWindow.getWidth();
		//		int h =  viewWindow.getHeight();
		//		offscreenImage.getGraphics().drawRGB(doubleBufferData,  0, w, 0, 0, w, h, false);
		//		g.drawImage(offscreenImage, viewWindow.getLeftOffset(), viewWindow.getTopOffset(), Graphics.TOP | Graphics.LEFT);

		int w = screen.getWidth();
		int h = screen.getHeight();
		//drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha)
		//g.drawRGB(doubleBufferData,  0, w,0,0, w, h, false);
		g.drawRGB(doubleBufferData, 0, w, viewWindow.getLeftOffset(), viewWindow.getTopOffset(), w, h, false);
	}

	protected void drawCurrentPolygon(Graphics g) {
		if (!(sourcePolygon instanceof TexturedPolygon3D)) {
			// not a textured polygon - return
			return;
		}
		TexturedPolygon3D poly = (TexturedPolygon3D) destPolygon;
		Texture texture = poly.getTexture();
		ScanRenderer scanRenderer = (ScanRenderer) scanRenderers.get(texture.getClass());
		scanRenderer.setTexture(texture);
		Rectangle3D textureBounds = poly.getTextureBounds();

		a.setToCrossProduct(textureBounds.getDirectionV(), textureBounds.getOrigin());
		b.setToCrossProduct(textureBounds.getOrigin(), textureBounds.getDirectionU());
		c.setToCrossProduct(textureBounds.getDirectionU(), textureBounds.getDirectionV());

		int y = scanConverter.getTopBoundary();
		viewPos.y = viewWindow.convertFromScreenYToViewY(y);
		viewPos.z = -viewWindow.getDistance();

		while (y <= scanConverter.getBottomBoundary()) {
			ScanConverter.Scan scan = scanConverter.getScan(y);

			if (scan.isValid()) {
				viewPos.x = viewWindow.convertFromScreenXToViewX(scan.left);
				int offset = (y - viewWindow.getTopOffset()) * viewWindow.getWidth()
						+ (scan.left - viewWindow.getLeftOffset());

				scanRenderer.render(offset, scan.left, scan.right);
			}
			y++;
			viewPos.y--;
		}
	}

	/**
	 The ScanRenderer class is an abstract inner class of
	 FastTexturedPolygonRenderer that provides an interface for
	 rendering a horizontal scan line.
	 */
	public abstract class ScanRenderer {

		protected Texture currentTexture;

		public void setTexture(Texture texture) {
			this.currentTexture = texture;
		}

		public abstract void render(int offset, int left, int right);

	}

	/*
	//================================================
	// FASTEST METHOD: no texture (for comparison)
	//================================================
	public class Method0 extends ScanRenderer {

		public void render(int offset, int left, int right) {
			for (int x = left; x <= right; x++) {
				doubleBufferData[offset++] = (short) 0x0007;
			}
		}
	}

	//================================================
	// METHOD 1: access pixel buffers directly
	// and use textures sizes that are a power of 2
	//================================================
	public class Method1 extends ScanRenderer {

		public void render(int offset, int left, int right) {
			for (int x = left; x <= right; x++) {
				int tx = (int) (a.getDotProduct(viewPos) / c.getDotProduct(viewPos));
				int ty = (int) (b.getDotProduct(viewPos) / c.getDotProduct(viewPos));
				doubleBufferData[offset++] = currentTexture.getColor(tx, ty);
				viewPos.x++;
			}
		}
	}

	//================================================
	// METHOD 2: avoid redundant calculations
	//================================================
	public class Method2 extends ScanRenderer {

		public void render(int offset, int left, int right) {
			float u = a.getDotProduct(viewPos);
			float v = b.getDotProduct(viewPos);
			float z = c.getDotProduct(viewPos);
			float du = a.x;
			float dv = b.x;
			float dz = c.x;
			for (int x = left; x <= right; x++) {
				doubleBufferData[offset++] = currentTexture.getColor((int) (u / z), (int) (v / z));
				u += du;
				v += dv;
				z += dz;
			}
		}
	}

	//================================================
	// METHOD 3: use ints instead of floats
	//================================================
	public class Method3 extends ScanRenderer {

		public void render(int offset, int left, int right) {
			int u = (int) (SCALE * a.getDotProduct(viewPos));
			int v = (int) (SCALE * b.getDotProduct(viewPos));
			int z = (int) (SCALE * c.getDotProduct(viewPos));
			int du = (int) (SCALE * a.x);
			int dv = (int) (SCALE * b.x);
			int dz = (int) (SCALE * c.x);
			for (int x = left; x <= right; x++) {
				doubleBufferData[offset++] = currentTexture.getColor(u / z, v / z);
				u += du;
				v += dv;
				z += dz;
			}
		}
	}
	*/

	//================================================
	// METHOD 4: reduce the number of divides
	// (interpolate every 16 pixels)
	// Also, apply a VM optimization by referring to
	// the texture's class rather than it's parent class.
	//================================================

	// the following three ScanRenderers are the same, but refer
	// to textures explicitly as either a PowerOf2Texture, a
	// ShadedTexture, or a ShadedSurface.
	// This allows HotSpot to do some inlining of the textures'
	// getColor() method, which significantly increases
	// performance.

	public class PowerOf2TextureRenderer extends ScanRenderer {

		public void render(int offset, int left, int right) {

			System.out.println("[DEBUG] FastTexturedPolygonRenderer.PowerOf2TextureRenderer.render()");

			PowerOf2Texture texture = (PowerOf2Texture) currentTexture;
			int[] buffer = texture.getRawData();
			int widthBits = texture.getWidthBits();
			int widthMask = texture.getWidthMask();
			int heightBits = texture.getHeightBits();
			int heightMask = texture.getHeightMask();

			float u = SCALE * a.getDotProduct(viewPos);
			float v = SCALE * b.getDotProduct(viewPos);
			float z = c.getDotProduct(viewPos);
			float du = INTERP_SIZE * SCALE * a.x;
			float dv = INTERP_SIZE * SCALE * b.x;
			float dz = INTERP_SIZE * c.x;
			int nextTx = (int) (u / z);
			int nextTy = (int) (v / z);
			int x = left;
			while (x <= right) {
				int tx = nextTx;
				int ty = nextTy;
				int maxLength = right - x + 1;
				if (maxLength > INTERP_SIZE) {
					u += du;
					v += dv;
					z += dz;
					nextTx = (int) (u / z);
					nextTy = (int) (v / z);
					int dtx = (nextTx - tx) >> INTERP_SIZE_BITS;
					int dty = (nextTy - ty) >> INTERP_SIZE_BITS;
					int endOffset = offset + INTERP_SIZE;
					while (offset < endOffset) {
						doubleBufferData[offset++] = buffer[((tx >> SCALE_BITS) & widthMask)
								+ (((ty >> SCALE_BITS) & heightMask) << widthBits)];
						//doubleBufferData[offset++] = texture.getColor(tx >> SCALE_BITS, ty >> SCALE_BITS);
						//doubleBufferData[offset++] = 0xFF999999 + offset + tx;
						//doubleBufferData[offset++] = Color.convertRBG565To888(texture.getColor(tx >> SCALE_BITS, ty >> SCALE_BITS));
						//System.out.println(Integer.toHexString(doubleBufferData[offset - 1]));
						tx += dtx;
						ty += dty;
					}
					x += INTERP_SIZE;
				} else {
					// variable interpolation size
					int interpSize = maxLength;
					u += interpSize * SCALE * a.x;
					v += interpSize * SCALE * b.x;
					z += interpSize * c.x;
					nextTx = (int) (u / z);
					nextTy = (int) (v / z);
					int dtx = (nextTx - tx) / interpSize;
					int dty = (nextTy - ty) / interpSize;
					int endOffset = offset + interpSize;
					while (offset < endOffset) {
						doubleBufferData[offset++] = buffer[((tx >> SCALE_BITS) & widthMask)
								+ (((ty >> SCALE_BITS) & heightMask) << widthBits)];
						//doubleBufferData[offset++] = texture.getColor(tx >> SCALE_BITS, ty >> SCALE_BITS);
						//doubleBufferData[offset++] = 0xFF00FF99 + offset + tx;
						//doubleBufferData[offset++] = Color.convertRBG565To888(texture.getColor(tx >> SCALE_BITS, ty >> SCALE_BITS));
						tx += dtx;
						ty += dty;
					}
					x += interpSize;
				}

			}
		}
	}

	public class ShadedTextureRenderer extends ScanRenderer {

		public void render(int offset, int left, int right) {

			System.out.println("[DEBUG] FastTexturedPolygonRenderer.ShadedTextureZRenderer.render()");

			ShadedTexture texture = (ShadedTexture) currentTexture;
			//int[] buffer = texture.getRawData();
			int widthBits = texture.getWidthBits();
			int widthMask = texture.getWidthMask();
			int heightBits = texture.getHeightBits();
			int heightMask = texture.getHeightMask();

			float u = SCALE * a.getDotProduct(viewPos);
			float v = SCALE * b.getDotProduct(viewPos);
			float z = c.getDotProduct(viewPos);
			float du = INTERP_SIZE * SCALE * a.x;
			float dv = INTERP_SIZE * SCALE * b.x;
			float dz = INTERP_SIZE * c.x;
			int nextTx = (int) (u / z);
			int nextTy = (int) (v / z);
			int x = left;
			while (x <= right) {
				int tx = nextTx;
				int ty = nextTy;
				int maxLength = right - x + 1;
				if (maxLength > INTERP_SIZE) {
					u += du;
					v += dv;
					z += dz;
					nextTx = (int) (u / z);
					nextTy = (int) (v / z);
					int dtx = (nextTx - tx) >> INTERP_SIZE_BITS;
					int dty = (nextTy - ty) >> INTERP_SIZE_BITS;
					int endOffset = offset + INTERP_SIZE;
					while (offset < endOffset) {
//						doubleBufferData[offset++] = buffer[((tx >> SCALE_BITS) & widthMask)
//								+ (((ty >> SCALE_BITS) & heightMask) << widthBits)];
						doubleBufferData[offset++] = texture.getColor(tx >> SCALE_BITS, ty >> SCALE_BITS);
						//doubleBufferData[offset++] = 0xFF55FF00 + offset + tx;
						tx += dtx;
						ty += dty;
					}
					x += INTERP_SIZE;
				} else {
					// variable interpolation size
					int interpSize = maxLength;
					u += interpSize * SCALE * a.x;
					v += interpSize * SCALE * b.x;
					z += interpSize * c.x;
					nextTx = (int) (u / z);
					nextTy = (int) (v / z);
					int dtx = (nextTx - tx) / interpSize;
					int dty = (nextTy - ty) / interpSize;
					int endOffset = offset + interpSize;
					while (offset < endOffset) {
//						doubleBufferData[offset++] = buffer[((tx >> SCALE_BITS) & widthMask)
//								+ (((ty >> SCALE_BITS) & heightMask) << widthBits)];
						doubleBufferData[offset++] = texture.getColor(tx >> SCALE_BITS, ty >> SCALE_BITS);
						//doubleBufferData[offset++] = 0xFFFFFF00 + offset + tx;
						tx += dtx;
						ty += dty;
					}
					x += interpSize;
				}

			}
		}
	}

	public class ShadedSurfaceRenderer extends ScanRenderer {

		public int checkBounds(int vScaled, int bounds) {
			int v = vScaled >> SCALE_BITS;
			if (v < 0) {
				vScaled = 0;
			} else if (v >= bounds) {
				vScaled = (bounds - 1) << SCALE_BITS;
			}
			return vScaled;
		}

		public void render(int offset, int left, int right) {

			//System.out.println("[DEBUG] FastTexturedPolygonRenderer.ShadedSurfaceZRenderer.render()");

			ShadedSurface texture = (ShadedSurface) currentTexture;
			ShadedTexture srcTexture = texture.getSourceTexture();
			//int[] buffer = srcTexture.getRawData();
			int widthBits = srcTexture.getWidthBits();
			int widthMask = srcTexture.getWidthMask();
			int heightBits = srcTexture.getHeightBits();
			int heightMask = srcTexture.getHeightMask();

			float u = SCALE * a.getDotProduct(viewPos);
			float v = SCALE * b.getDotProduct(viewPos);
			float z = c.getDotProduct(viewPos);
			float du = INTERP_SIZE * SCALE * a.x;
			float dv = INTERP_SIZE * SCALE * b.x;
			float dz = INTERP_SIZE * c.x;
			int nextTx = (int) (u / z);
			int nextTy = (int) (v / z);
			int x = left;
			while (x <= right) {
				int tx = nextTx;
				int ty = nextTy;
				int maxLength = right - x + 1;
				if (maxLength > INTERP_SIZE) {
					u += du;
					v += dv;
					z += dz;
					nextTx = (int) (u / z);
					nextTy = (int) (v / z);
					int dtx = (nextTx - tx) >> INTERP_SIZE_BITS;
					int dty = (nextTy - ty) >> INTERP_SIZE_BITS;
					int endOffset = offset + INTERP_SIZE;
					while (offset < endOffset) {
//						doubleBufferData[offset++] = buffer[((tx >> SCALE_BITS) & widthMask)
//								+ (((ty >> SCALE_BITS) & heightMask) << widthBits)];
						doubleBufferData[offset++] = texture.getColor(tx >> SCALE_BITS, ty >> SCALE_BITS);
						//doubleBufferData[offset++] = 0xFF0000FF + offset + tx;
						tx += dtx;
						ty += dty;
					}
					x += INTERP_SIZE;
				} else {
					// variable interpolation size
					int interpSize = maxLength;
					u += interpSize * SCALE * a.x;
					v += interpSize * SCALE * b.x;
					z += interpSize * c.x;
					nextTx = (int) (u / z);
					nextTy = (int) (v / z);

					// make sure tx, ty, nextTx, and nextTy are
					// all within bounds
					tx = checkBounds(tx, texture.getWidth());
					ty = checkBounds(ty, texture.getHeight());
					nextTx = checkBounds(nextTx, texture.getWidth());
					nextTy = checkBounds(nextTy, texture.getHeight());

					int dtx = (nextTx - tx) / interpSize;
					int dty = (nextTy - ty) / interpSize;
					int endOffset = offset + interpSize;
					while (offset < endOffset) {
//						doubleBufferData[offset++] = buffer[((tx >> SCALE_BITS) & widthMask)
//								+ (((ty >> SCALE_BITS) & heightMask) << widthBits)];
						doubleBufferData[offset++] = texture.getColor(tx >> SCALE_BITS, ty >> SCALE_BITS);
						//doubleBufferData[offset++] = 0xFF00FF + offset + tx;
						tx += dtx;
						ty += dty;
					}
					x += interpSize;
				}
			}

		}
	}

}
