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

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;

import org.thenesis.planetino2.graphics.Screen;
import org.thenesis.planetino2.graphics3D.texture.ShadedSurface;
import org.thenesis.planetino2.graphics3D.texture.Texture;
import org.thenesis.planetino2.math3D.TexturedPolygon3D;
import org.thenesis.planetino2.math3D.Transform3D;
import org.thenesis.planetino2.math3D.ViewWindow;

/**
 The ShadedSurfacePolygonRenderer is a PolygonRenderer that
 renders polygons with ShadedSurfaces. It keeps track of
 built surfaces, and clears any surfaces that weren't used
 in the last rendered frame to save memory.
 */
public class ShadedSurfacePolygonRenderer extends FastTexturedPolygonRenderer {

	// FIXME Replace by a linked list
	private Vector builtSurfaces = new Vector();

	public ShadedSurfacePolygonRenderer(Transform3D camera, ViewWindow viewWindow) {
		this(camera, viewWindow, true);
	}

	public ShadedSurfacePolygonRenderer(Transform3D camera, ViewWindow viewWindow, boolean eraseView) {
		super(camera, viewWindow, eraseView);
	}

	public void endFrame(Screen screen) {
		super.endFrame(screen);

		// clear all built surfaces that weren't used this frame.
		Enumeration i = builtSurfaces.elements();
		while (i.hasMoreElements()) {
			ShadedSurface surface = (ShadedSurface) i.nextElement();
			if (surface.isDirty()) {
				surface.clearSurface();
				builtSurfaces.removeElement(surface);
			} else {
				surface.setDirty(true);
			}
		}
	}

	protected void drawCurrentPolygon(Graphics g) {
		buildSurface();
		super.drawCurrentPolygon(g);
	}

	/**
	 Builds the surface of the polygon if it has a
	 ShadedSurface that is cleared.
	 */
	protected void buildSurface() {
		// build surface, if needed
		if (sourcePolygon instanceof TexturedPolygon3D) {
			Texture texture = ((TexturedPolygon3D) sourcePolygon).getTexture();
			if (texture instanceof ShadedSurface) {
				ShadedSurface surface = (ShadedSurface) texture;
				if (surface.isCleared()) {
					surface.buildSurface();
					builtSurfaces.addElement(surface);
				}
				surface.setDirty(false);
			}
		}
	}

}
