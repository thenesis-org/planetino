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

import org.thenesis.planetino2.math3D.Polygon3D;
import org.thenesis.planetino2.math3D.Vector3D;
import org.thenesis.planetino2.math3D.ViewWindow;
import org.thenesis.planetino2.util.MoreMath;

/**
 The ScanConverter class converts a projected polygon into a
 series of horizontal scans for drawing.
 */
public class ScanConverter {

	private static final int SCALE_BITS = 16;
	private static final int SCALE = 1 << SCALE_BITS;
	private static final int SCALE_MASK = SCALE - 1;

	protected ViewWindow view;
	protected Scan[] scans;
	protected int top;
	protected int bottom;

	/**
	 A horizontal scan line.
	 */
	public static class Scan {
		public int left;
		public int right;

		/**
		 Sets the left and right boundary for this scan if
		 the x value is outside the current boundary.
		 */
		public void setBoundary(int x) {
			if (x < left) {
				left = x;
			}
			if (x - 1 > right) {
				right = x - 1;
			}
		}

		/**
		 Clears this scan line.
		 */
		public void clear() {
			left = Integer.MAX_VALUE;
			right = Integer.MIN_VALUE;
		}

		/**
		 Determines if this scan is valid (if left <= right).
		 */
		public boolean isValid() {
			return (left <= right);
		}

		/**
		 Sets this scan.
		 */
		public void setTo(int left, int right) {
			this.left = left;
			this.right = right;
		}

		/**
		 Checks if this scan is equal to the specified values.
		 */
		public boolean equals(int left, int right) {
			return (this.left == left && this.right == right);
		}
	}

	/**
	 Creates a new ScanConverter for the specified ViewWindow.
	 The ViewWindow's properties can change in between scan
	 conversions.
	 */
	public ScanConverter(ViewWindow view) {
		this.view = view;
	}

	/**
	 Gets the top boundary of the last scan-converted polygon.
	 */
	public int getTopBoundary() {
		return top;
	}

	/**
	 Gets the bottom boundary of the last scan-converted
	 polygon.
	 */
	public int getBottomBoundary() {
		return bottom;
	}

	/**
	 Gets the scan line for the specified y value.
	 */
	public Scan getScan(int y) {
		return scans[y];
	}

	/**
	 Ensures this ScanConverter has the capacity to
	 scan-convert a polygon to the ViewWindow.
	 */
	protected void ensureCapacity() {
		int height = view.getTopOffset() + view.getHeight();
		if (scans == null || scans.length != height) {
			scans = new Scan[height];
			for (int i = 0; i < height; i++) {
				scans[i] = new Scan();
			}
			// set top and bottom so clearCurrentScan clears all
			top = 0;
			bottom = height - 1;
		}

	}

	/**
	 Clears the current scan.
	 */
	private void clearCurrentScan() {
		for (int i = top; i <= bottom; i++) {
			scans[i].clear();
		}
		top = Integer.MAX_VALUE;
		bottom = Integer.MIN_VALUE;
	}

	/**
	 Scan-converts a projected polygon. Returns true if the
	 polygon is visible in the view window.
	 */
	public boolean convert(Polygon3D polygon) {

		ensureCapacity();
		clearCurrentScan();

		int minX = view.getLeftOffset();
		int maxX = view.getLeftOffset() + view.getWidth() - 1;
		int minY = view.getTopOffset();
		int maxY = view.getTopOffset() + view.getHeight() - 1;

		int numVertices = polygon.getNumVertices();
		for (int i = 0; i < numVertices; i++) {
			Vector3D v1 = polygon.getVertex(i);
			Vector3D v2;
			if (i == numVertices - 1) {
				v2 = polygon.getVertex(0);
			} else {
				v2 = polygon.getVertex(i + 1);
			}

			// ensure v1.y < v2.y
			if (v1.y > v2.y) {
				Vector3D temp = v1;
				v1 = v2;
				v2 = temp;
			}
			float dy = v2.y - v1.y;

			// ignore horizontal lines
			if (dy == 0) {
				continue;
			}

			int startY = Math.max(MoreMath.ceil(v1.y), minY);
			int endY = Math.min(MoreMath.ceil(v2.y) - 1, maxY);
			top = Math.min(top, startY);
			bottom = Math.max(bottom, endY);
			float dx = v2.x - v1.x;

			// special case: vertical line
			if (dx == 0) {
				int x = MoreMath.ceil(v1.x);
				// ensure x within view bounds
				x = Math.min(maxX + 1, Math.max(x, minX));
				for (int y = startY; y <= endY; y++) {
					scans[y].setBoundary(x);
				}
			} else {
				// scan-convert this edge (line equation)
				float gradient = dx / dy;

				// (slower version)
				/*
				 for (int y=startY; y<=endY; y++) {
				 int x = MoreMath.ceil(v1.x + (y - v1.y) * gradient);
				 // ensure x within view bounds
				 x = Math.min(maxX+1, Math.max(x, minX));
				 scans[y].setBoundary(x);
				 }
				 */

				// (faster version)
				// trim start of line
				float startX = v1.x + (startY - v1.y) * gradient;
				if (startX < minX) {
					int yInt = (int) (v1.y + (minX - v1.x) / gradient);
					yInt = Math.min(yInt, endY);
					while (startY <= yInt) {
						scans[startY].setBoundary(minX);
						startY++;
					}
				} else if (startX > maxX) {
					int yInt = (int) (v1.y + (maxX - v1.x) / gradient);
					yInt = Math.min(yInt, endY);
					while (startY <= yInt) {
						scans[startY].setBoundary(maxX + 1);
						startY++;
					}
				}

				if (startY > endY) {
					continue;
				}

				// trim back of line
				float endX = v1.x + (endY - v1.y) * gradient;
				if (endX < minX) {
					int yInt = MoreMath.ceil(v1.y + (minX - v1.x) / gradient);
					yInt = Math.max(yInt, startY);
					while (endY >= yInt) {
						scans[endY].setBoundary(minX);
						endY--;
					}
				} else if (endX > maxX) {
					int yInt = MoreMath.ceil(v1.y + (maxX - v1.x) / gradient);
					yInt = Math.max(yInt, startY);
					while (endY >= yInt) {
						scans[endY].setBoundary(maxX + 1);
						endY--;
					}
				}

				if (startY > endY) {
					continue;
				}

				// line equation using integers
				int xScaled = (int) (SCALE * v1.x + SCALE * (startY - v1.y) * dx / dy) + SCALE_MASK;
				int dxScaled = (int) (dx * SCALE / dy);

				for (int y = startY; y <= endY; y++) {
					scans[y].setBoundary(xScaled >> SCALE_BITS);
					xScaled += dxScaled;
				}
			}
		}

		// check if visible (any valid scans)
		for (int i = top; i <= bottom; i++) {
			if (scans[i].isValid()) {
				return true;
			}
		}
		return false;
	}

}