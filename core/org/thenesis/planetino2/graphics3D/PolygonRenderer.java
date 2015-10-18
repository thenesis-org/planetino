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

import org.thenesis.planetino2.graphics.Graphics;
import org.thenesis.planetino2.graphics.Screen;
import org.thenesis.planetino2.math3D.Polygon3D;
import org.thenesis.planetino2.math3D.Transform3D;
import org.thenesis.planetino2.math3D.ViewWindow;

/**
 The PolygonRenderer class is an abstract class that transforms
 and draws polygons onto the screen.
 */
public abstract class PolygonRenderer {

	protected ScanConverter scanConverter;
	protected Transform3D camera;
	protected ViewWindow viewWindow;
	protected boolean clearViewEveryFrame;
	protected Polygon3D sourcePolygon;
	protected Polygon3D destPolygon;

	/**
	 Creates a new PolygonRenderer with the specified
	 Transform3D (camera) and ViewWindow. The view is cleared
	 when startFrame() is called.
	 */
	public PolygonRenderer(Transform3D camera, ViewWindow viewWindow) {
		this(camera, viewWindow, true);
	}

	/**
	 Creates a new PolygonRenderer with the specified
	 Transform3D (camera) and ViewWindow. If
	 clearViewEveryFrame is true, the view is cleared when
	 startFrame() is called.
	 */
	public PolygonRenderer(Transform3D camera, ViewWindow viewWindow, boolean clearViewEveryFrame) {
		this.camera = camera;
		this.viewWindow = viewWindow;
		this.clearViewEveryFrame = clearViewEveryFrame;
		init();
	}

	/**
	 Create the scan converter and dest polygon.
	 */
	protected void init() {
		destPolygon = new Polygon3D();
		scanConverter = new ScanConverter(viewWindow);
	}

	/**
	 Gets the camera used for this PolygonRenderer.
	 */
	public Transform3D getCamera() {
		return camera;
	}

	/**
	 Indicates the start of rendering of a frame. This method
	 should be called every frame before any polygons are drawn.
	 */
	public void startFrame(Screen screen) {
		Graphics g = screen.getGraphics();
		if (clearViewEveryFrame) {
			g.setColor(0x00000000); // Color.black
			g.fillRect(viewWindow.getLeftOffset(), viewWindow.getTopOffset(), viewWindow.getWidth(), viewWindow
					.getHeight());
		}
	}

	/**
	 Indicates the end of rendering of a frame. This method
	 should be called every frame after all polygons are drawn.
	 */
	public void endFrame(Screen screen) {
		// do nothing, for now.
	}

	/**
	 Transforms and draws a polygon.
	 */
	public boolean draw(Graphics g, Polygon3D poly) {
		if (poly.isFacing(camera.getLocation())) {
			sourcePolygon = poly;
			destPolygon.setTo(poly);
			destPolygon.subtract(camera);
			boolean visible = destPolygon.clip(-1);
			if (visible) {
				destPolygon.project(viewWindow);
				visible = scanConverter.convert(destPolygon);
				if (visible) {
					drawCurrentPolygon(g);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 Draws the current polygon. At this point, the current
	 polygon is transformed, clipped, projected,
	 scan-converted, and visible.
	 */
	protected abstract void drawCurrentPolygon(Graphics g);
}
