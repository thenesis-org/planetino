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
package org.thenesis.planetino2.test;

import java.io.IOException;

import org.thenesis.planetino2.engine.GameCore3D;
import org.thenesis.planetino2.graphics.Image;
import org.thenesis.planetino2.graphics.Screen;
import org.thenesis.planetino2.graphics.Toolkit;
import org.thenesis.planetino2.graphics3D.FastTexturedPolygonRenderer;
import org.thenesis.planetino2.graphics3D.texture.Texture;
import org.thenesis.planetino2.input.InputManager;
import org.thenesis.planetino2.loader.ResourceLoader;
import org.thenesis.planetino2.math3D.Rectangle3D;
import org.thenesis.planetino2.math3D.TexturedPolygon3D;
import org.thenesis.planetino2.math3D.Transform3D;
import org.thenesis.planetino2.math3D.Vector3D;
import org.thenesis.planetino2.math3D.ViewWindow;

public class TextureMapTest2 extends GameCore3D {

	//    public static void main(String[] args) {
	//        new TextureMapTest2().run();
	//    }

	public TextureMapTest2(Screen screen, InputManager inputManager) {
		super(screen, inputManager);
		this.inputManager = inputManager;
	}

	// create a house (convex polyhedra)
	public void createPolygons() {

		// create Textures
		Texture wall = loadTexture("/res/", "wall1.png");
		Texture roof = loadTexture("/res/", "roof1.png");

		TexturedPolygon3D poly;

		// walls
		poly = new TexturedPolygon3D(new Vector3D(-200, 250, -1000), new Vector3D(-200, 0, -1000), new Vector3D(200, 0,
				-1000), new Vector3D(200, 250, -1000));
		setTexture(poly, wall);
		polygons.addElement(poly);

		poly = new TexturedPolygon3D(new Vector3D(200, 250, -1400), new Vector3D(200, 0, -1400), new Vector3D(-200, 0,
				-1400), new Vector3D(-200, 250, -1400));
		setTexture(poly, wall);
		polygons.addElement(poly);

		poly = new TexturedPolygon3D(new Vector3D(-200, 250, -1400), new Vector3D(-200, 0, -1400), new Vector3D(-200,
				0, -1000), new Vector3D(-200, 250, -1000));
		setTexture(poly, wall);
		polygons.addElement(poly);

		poly = new TexturedPolygon3D(new Vector3D(200, 250, -1000), new Vector3D(200, 0, -1000), new Vector3D(200, 0,
				-1400), new Vector3D(200, 250, -1400));
		setTexture(poly, wall);
		polygons.addElement(poly);

		// roof
		poly = new TexturedPolygon3D(new Vector3D(-200, 250, -1000), new Vector3D(200, 250, -1000), new Vector3D(75,
				400, -1200), new Vector3D(-75, 400, -1200));
		setTexture(poly, roof);
		polygons.addElement(poly);

		poly = new TexturedPolygon3D(new Vector3D(-200, 250, -1400), new Vector3D(-200, 250, -1000), new Vector3D(-75,
				400, -1200));
		setTexture(poly, roof);
		polygons.addElement(poly);

		poly = new TexturedPolygon3D(new Vector3D(200, 250, -1400), new Vector3D(-200, 250, -1400), new Vector3D(-75,
				400, -1200), new Vector3D(75, 400, -1200));
		setTexture(poly, roof);
		polygons.addElement(poly);

		poly = new TexturedPolygon3D(new Vector3D(200, 250, -1000), new Vector3D(200, 250, -1400), new Vector3D(75,
				400, -1200));
		setTexture(poly, roof);
		polygons.addElement(poly);
	}

	public void setTexture(TexturedPolygon3D poly, Texture texture) {
		Vector3D origin = poly.getVertex(0);

		Vector3D dv = new Vector3D(poly.getVertex(1));
		dv.subtract(origin);

		Vector3D du = new Vector3D();
		du.setToCrossProduct(poly.getNormal(), dv);

		Rectangle3D textureBounds = new Rectangle3D(origin, du, dv, texture.getWidth(), texture.getHeight());

		poly.setTexture(texture, textureBounds);
	}

	public Texture loadTexture(String path, String imageName) {
		ResourceLoader loader = Toolkit.getInstance().getResourceLoader();
		Image image = null;
		try {
			image = loader.loadImage(imageName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Texture.createTexture(image, false);
	}

	public void createPolygonRenderer() {
		viewWindow = new ViewWindow(0, 0, screen.getWidth(), screen.getHeight(), (float) Math.toRadians(75));

		Transform3D camera = new Transform3D(0, 100, 0);
		polygonRenderer = new FastTexturedPolygonRenderer(camera, viewWindow);
	}

}