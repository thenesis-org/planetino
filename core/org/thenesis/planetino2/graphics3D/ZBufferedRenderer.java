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

import org.thenesis.planetino2.game.GameObject;
import org.thenesis.planetino2.game.GameObjectRenderer;
import org.thenesis.planetino2.graphics.Graphics;
import org.thenesis.planetino2.graphics.Screen;
import org.thenesis.planetino2.graphics3D.texture.PowerOf2Texture;
import org.thenesis.planetino2.graphics3D.texture.ShadedSurface;
import org.thenesis.planetino2.graphics3D.texture.ShadedTexture;
import org.thenesis.planetino2.graphics3D.texture.Texture;
import org.thenesis.planetino2.math3D.PolygonGroup;
import org.thenesis.planetino2.math3D.Rectangle3D;
import org.thenesis.planetino2.math3D.TexturedPolygon3D;
import org.thenesis.planetino2.math3D.Transform3D;
import org.thenesis.planetino2.math3D.ViewWindow;

/**
 The ZBufferedRenderer is a PolygonRenderer that
 renders polygons with a Z-Buffer to ensure correct rendering
 (closer objects appear in front of farther away objects).
 */
public class ZBufferedRenderer extends ShadedSurfacePolygonRenderer implements GameObjectRenderer {
	/**
	 The minimum distance for z-buffering. Larger values give
	 more accurate calculations for further distances.
	 */
	protected static final int MIN_DISTANCE = 12;

	protected TexturedPolygon3D temp;
	protected ZBuffer zBuffer;
	// used for calculating depth
	protected float w;

	public ZBufferedRenderer(Transform3D camera, ViewWindow viewWindow) {
		this(camera, viewWindow, true);
	}

	public ZBufferedRenderer(Transform3D camera, ViewWindow viewWindow, boolean eraseView) {
		super(camera, viewWindow, eraseView);
		temp = new TexturedPolygon3D();
	}

	protected void init() {
		destPolygon = new TexturedPolygon3D();
		scanConverter = new ScanConverter(viewWindow);

		// create renders for each texture (HotSpot optimization)
		scanRenderers = new Hashtable();
		scanRenderers.put(PowerOf2Texture.class, new PowerOf2TextureZRenderer());
		scanRenderers.put(ShadedTexture.class, new ShadedTextureZRenderer());
		scanRenderers.put(ShadedSurface.class, new ShadedSurfaceZRenderer());
	}

	public void startFrame(Screen screen) {
		super.startFrame(screen);
		// initialize depth buffer
		if (zBuffer == null || zBuffer.getWidth() != viewWindow.getWidth()
				|| zBuffer.getHeight() != viewWindow.getHeight()) {
			zBuffer = new ZBuffer(viewWindow.getWidth(), viewWindow.getHeight());
		} else if (clearViewEveryFrame) {
			zBuffer.clear();
		}
	}

	public boolean draw(Graphics g, GameObject object) {
		return draw(g, object.getPolygonGroup());
	}

	public boolean draw(Graphics g, PolygonGroup group) {
		boolean visible = false;
		group.resetIterator();
		while (group.hasNext()) {
			group.nextPolygonTransformed(temp);
			visible |= draw(g, temp);
		}
		return visible;
	}

	protected void drawCurrentPolygon(Graphics g) {
		if (!(sourcePolygon instanceof TexturedPolygon3D)) {
			// not a textured polygon - return
			return;
		}
		buildSurface();
		TexturedPolygon3D poly = (TexturedPolygon3D) destPolygon;
		Texture texture = poly.getTexture();
		ScanRenderer scanRenderer = (ScanRenderer) scanRenderers.get(texture.getClass());
		scanRenderer.setTexture(texture);
		Rectangle3D textureBounds = poly.getTextureBounds();

		a.setToCrossProduct(textureBounds.getDirectionV(), textureBounds.getOrigin());
		b.setToCrossProduct(textureBounds.getOrigin(), textureBounds.getDirectionU());
		c.setToCrossProduct(textureBounds.getDirectionU(), textureBounds.getDirectionV());

		// w is used to compute depth at each pixel
		w = SCALE * MIN_DISTANCE * Short.MAX_VALUE
				/ (viewWindow.getDistance() * c.getDotProduct(textureBounds.getOrigin()));

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

	// the following three ScanRenderers are the same, but refer
	// to textures explicitly as either a PowerOf2Texture, a
	// ShadedTexture, or a ShadedSurface.
	// This allows HotSpot to do some inlining of the textures'
	// getColor() method, which significantly increases
	// performance.

	public class PowerOf2TextureZRenderer extends ScanRenderer {

		public void render(int offset, int left, int right) {

			//			doubleBufferData[offset] = 0xFFFF0000;
			//			System.out.println("[DEBUG] PowerOf2TextureZRenderer.render()");
			//			int x = left;
			//			while (x <= right) {
			//				
			//				doubleBufferData[offset + x] = 0xFF00FF00;
			//				x++;
			//			}

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
			int depth = (int) (w * z);
			int dDepth = (int) (w * c.x);
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
						if (zBuffer.checkDepth(offset, (short) (depth >> SCALE_BITS))) {
//							doubleBufferData[offset] = buffer[((tx >> SCALE_BITS) & widthMask)
//									+ (((ty >> SCALE_BITS) & heightMask) << widthBits)];
							doubleBufferData[offset] = texture.getColor(tx >> SCALE_BITS, ty >> SCALE_BITS);
							//doubleBufferData[offset] = Color.convertRBG565To888(texture.getColor(tx >> SCALE_BITS, ty >> SCALE_BITS));
							//doubleBufferData[offset] = 0xFFFFFF00;
						}
						offset++;
						tx += dtx;
						ty += dty;
						depth += dDepth;
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
						if (zBuffer.checkDepth(offset, (short) (depth >> SCALE_BITS))) {
							doubleBufferData[offset] = buffer[((tx >> SCALE_BITS) & widthMask)
									+ (((ty >> SCALE_BITS) & heightMask) << widthBits)];
							//doubleBufferData[offset++] = texture.getColor(tx >> SCALE_BITS, ty >> SCALE_BITS);
							//doubleBufferData[offset++] = Color.convertRBG565To888(texture.getColor(tx >> SCALE_BITS, ty >> SCALE_BITS));
							//doubleBufferData[offset] = 0xFFFFFF00;
						}
						offset++;
						tx += dtx;
						ty += dty;
						depth += dDepth;
					}
					x += interpSize;

				}

			}
		}
	}

	public class ShadedTextureZRenderer extends ScanRenderer {

		public void render(int offset, int left, int right) {

			//			System.out.println("[DEBUG] ShadedTextureZRenderer.render()");
			//			
			//			int x = left;
			//			while (x <= right) {
			//				
			//				doubleBufferData[offset + x] = 0xFFFF0000;
			//				x++;
			//			}

			ShadedTexture texture = (ShadedTexture) currentTexture;
//			int[] buffer = texture.getRawData();
//			int widthBits = texture.getWidthBits();
//			int widthMask = texture.getWidthMask();
//			int heightBits = texture.getHeightBits();
//			int heightMask = texture.getHeightMask();

			float u = SCALE * a.getDotProduct(viewPos);
			float v = SCALE * b.getDotProduct(viewPos);
			float z = c.getDotProduct(viewPos);
			float du = INTERP_SIZE * SCALE * a.x;
			float dv = INTERP_SIZE * SCALE * b.x;
			float dz = INTERP_SIZE * c.x;
			int nextTx = (int) (u / z);
			int nextTy = (int) (v / z);
			int depth = (int) (w * z);
			int dDepth = (int) (w * c.x);
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
						if (zBuffer.checkDepth(offset, (short) (depth >> SCALE_BITS))) {
//							doubleBufferData[offset] = buffer[((tx >> SCALE_BITS) & widthMask)
//									+ (((ty >> SCALE_BITS) & heightMask) << widthBits)];
							doubleBufferData[offset] = texture.getColor(tx >> SCALE_BITS, ty >> SCALE_BITS);
							//doubleBufferData[offset] = Color.convertRBG565To888(texture.getColor(tx >> SCALE_BITS, ty >> SCALE_BITS));
							//doubleBufferData[offset] = 0xFF00FF00;
						}
						offset++;
						tx += dtx;
						ty += dty;
						depth += dDepth;
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
						if (zBuffer.checkDepth(offset, (short) (depth >> SCALE_BITS))) {
//							doubleBufferData[offset] = buffer[((tx >> SCALE_BITS) & widthMask)
//									+ (((ty >> SCALE_BITS) & heightMask) << widthBits)];
							doubleBufferData[offset] = texture.getColor(tx >> SCALE_BITS, ty >> SCALE_BITS);
							//doubleBufferData[offset] = Color.convertRBG565To888(texture.getColor(tx >> SCALE_BITS, ty >> SCALE_BITS));
							//doubleBufferData[offset] = 0xFFFF0000;
						}
						offset++;
						tx += dtx;
						ty += dty;
						depth += dDepth;
					}
					x += interpSize;
				}

			}
		}
	}

	public class ShadedSurfaceZRenderer extends ScanRenderer {

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

			//throw new UnsupportedOperationException();
			//System.out.println("[DEBUG] ShadedSurfaceZRenderer.render()");

			//			doubleBufferData[offset] = 0xFFFF0000;
			//			
			//			int x = left;
			//			while (x <= right) {
			//				
			//				doubleBufferData[offset + x] = 0xFF00FF00 + x;
			//				x++;
			//			}

			ShadedSurface texture = (ShadedSurface) currentTexture;
//			ShadedTexture srcTexture = texture.getSourceTexture();
//			int[] buffer = srcTexture.getRawData();
//			int widthBits = srcTexture.getWidthBits();
//			int widthMask = srcTexture.getWidthMask();
//			int heightBits = srcTexture.getHeightBits();
//			int heightMask = srcTexture.getHeightMask();

			float u = SCALE * a.getDotProduct(viewPos);
			float v = SCALE * b.getDotProduct(viewPos);
			float z = c.getDotProduct(viewPos);
			float du = INTERP_SIZE * SCALE * a.x;
			float dv = INTERP_SIZE * SCALE * b.x;
			float dz = INTERP_SIZE * c.x;
			int nextTx = (int) (u / z);
			int nextTy = (int) (v / z);
			int depth = (int) (w * z);
			int dDepth = (int) (w * c.x);
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
						if (zBuffer.checkDepth(offset, (short) (depth >> SCALE_BITS))) {
//							doubleBufferData[offset] = buffer[((tx >> SCALE_BITS) & widthMask)
//									+ (((ty >> SCALE_BITS) & heightMask) << widthBits)];
							doubleBufferData[offset] = texture.getColor(tx >> SCALE_BITS, ty >> SCALE_BITS);
							//doubleBufferData[offset] = Color.convertRBG565To888(texture.getColor(tx >> SCALE_BITS, ty >> SCALE_BITS));
							//doubleBufferData[offset] = 0xFFFF00FF;
						}
						offset++;
						tx += dtx;
						ty += dty;
						depth += dDepth;
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
						if (zBuffer.checkDepth(offset, (short) (depth >> SCALE_BITS))) {
//							doubleBufferData[offset] = buffer[((tx >> SCALE_BITS) & widthMask)
//									+ (((ty >> SCALE_BITS) & heightMask) << widthBits)];
							doubleBufferData[offset] = texture.getColor(tx >> SCALE_BITS, ty >> SCALE_BITS);
							//doubleBufferData[offset] = Color.convertRBG565To888(texture.getColor(tx >> SCALE_BITS, ty >> SCALE_BITS));
							//doubleBufferData[offset] = 0xFF00FF00;
						}
						offset++;
						tx += dtx;
						ty += dty;
						depth += dDepth;
					}
					x += interpSize;

				}

			}
		}
	}

}
