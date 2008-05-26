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

import javax.microedition.lcdui.Graphics;

import org.thenesis.planetino2.graphics.Color;
import org.thenesis.planetino2.math3D.SolidPolygon3D;
import org.thenesis.planetino2.math3D.Transform3D;
import org.thenesis.planetino2.math3D.ViewWindow;

/**
 The SolidPolygonRenderer class transforms and draws
 solid-colored polygons onto the screen.
 */
public class SolidPolygonRenderer extends PolygonRenderer {

	public SolidPolygonRenderer(Transform3D camera, ViewWindow viewWindow) {
		this(camera, viewWindow, true);
	}

	public SolidPolygonRenderer(Transform3D camera, ViewWindow viewWindow, boolean clearViewEveryFrame) {
		super(camera, viewWindow, clearViewEveryFrame);
	}

	/**
	 Draws the current polygon. At this point, the current
	 polygon is transformed, clipped, projected,
	 scan-converted, and visible.
	 */
	protected void drawCurrentPolygon(Graphics g) {

		// set the color
		if (sourcePolygon instanceof SolidPolygon3D) {
			g.setColor(((SolidPolygon3D) sourcePolygon).getColor().getRGB());
		} else {
			g.setColor(Color.GREEN.getRGB());
		}

		// draw the scans
		int y = scanConverter.getTopBoundary();
		while (y <= scanConverter.getBottomBoundary()) {
			ScanConverter.Scan scan = scanConverter.getScan(y);
			if (scan.isValid()) {
				g.drawLine(scan.left, y, scan.right, y);
			}
			y++;
		}
	}
}
