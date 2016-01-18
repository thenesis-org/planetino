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
import java.util.Enumeration;

import org.thenesis.planetino2.bsp2D.BSPRenderer;
import org.thenesis.planetino2.bsp2D.BSPTree;
import org.thenesis.planetino2.engine.shooter3D.Bot;
import org.thenesis.planetino2.engine.shooter3D.ShooterCore;
import org.thenesis.planetino2.game.CollisionDetection;
import org.thenesis.planetino2.game.CollisionDetectionWithSliding;
import org.thenesis.planetino2.game.GameObject;
import org.thenesis.planetino2.game.GameObjectRenderer;
import org.thenesis.planetino2.game.GridGameObjectManager;
import org.thenesis.planetino2.game.Player;
import org.thenesis.planetino2.graphics.Color;
import org.thenesis.planetino2.graphics.Graphics;
import org.thenesis.planetino2.graphics.Screen;
import org.thenesis.planetino2.input.InputManager;
import org.thenesis.planetino2.loader.MapLoader;
import org.thenesis.planetino2.math3D.PointLight3D;
import org.thenesis.planetino2.math3D.PolygonGroup;
import org.thenesis.planetino2.math3D.Transform3D;
import org.thenesis.planetino2.util.Vector;

public class CollisionTestWithSliding extends ShooterCore {

	//    public static void main(String[] args) {
	//        new CollisionTestWithSliding(args).run();
	//    }

	protected BSPTree bspTree;
	protected String mapFile;

	//    public CollisionTestWithSliding(String[] args) {
	//        super(args);
	//        for (int i=0; mapFile == null && i<args.length; i++) {
	//            if (mapFile == null && !args[i].startsWith("-")) {
	//                mapFile = args[i];
	//            }
	//        }
	//        if (mapFile == null) {
	//            mapFile = "../res/sample.map";
	//        }
	//    }

	public CollisionTestWithSliding(Screen screen, InputManager inputManager) {
		super(screen, inputManager);
		this.inputManager = inputManager;
	}

	public void createPolygons() {
		Graphics g = screen.getGraphics();
		g.setColor(Color.BLACK.getRGB());
		g.fillRect(0, 0, screen.getWidth(), screen.getHeight());
		g.setColor(Color.WHITE.getRGB());
		//g.drawString("Loading...", 5, screen.getHeight() - 5);
		screen.update();

		float ambientLightIntensity = .2f;
		Vector lights = new Vector();
		lights.addElement(new PointLight3D(-100, 100, 100, .3f, -1));
		lights.addElement(new PointLight3D(100, 100, 0, .3f, -1));

		MapLoader loader = new MapLoader();
		loader.setObjectLights(lights, ambientLightIntensity);

		try {
			bspTree = loader.loadMap("/res/", "test.map");
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		CollisionDetection collisionDetection = new CollisionDetectionWithSliding(bspTree);
		gameObjectManager = new GridGameObjectManager(bspTree.calcBounds(), collisionDetection);
		gameObjectManager.addPlayer(new Player());

		((BSPRenderer) polygonRenderer).setGameObjectManager(gameObjectManager);

		createGameObjects(loader.getObjectsInMap());
		Transform3D start = loader.getPlayerStartTransform();
		gameObjectManager.getPlayer().getTransform().setTo(start);
	}

	private void createGameObjects(Vector mapObjects) {
		Enumeration i = mapObjects.elements();
		while (i.hasMoreElements()) {
			PolygonGroup group = (PolygonGroup) i.nextElement();
			String filename = group.getFilename();
			if ("robot.obj".equals(filename)) {
				gameObjectManager.add(new Bot(group));
			} else {
				// static object
				gameObjectManager.add(new GameObject(group));
			}
		}
	}

	public void drawPolygons(Graphics g) {

		polygonRenderer.startFrame(screen);

		// draw polygons in bsp tree (set z buffer)
		((BSPRenderer) polygonRenderer).draw(g, bspTree);

		// draw game object polygons (check and set z buffer)
		gameObjectManager.draw(g, (GameObjectRenderer) polygonRenderer);

		polygonRenderer.endFrame(screen);

	}
}
