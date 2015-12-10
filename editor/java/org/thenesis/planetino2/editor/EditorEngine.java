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
package org.thenesis.planetino2.editor;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import org.thenesis.planetino2.bsp2D.BSPRenderer;
import org.thenesis.planetino2.bsp2D.BSPTree;
import org.thenesis.planetino2.bsp2D.BSPTreeBuilderWithPortals;
import org.thenesis.planetino2.bsp2D.MapLoader;
import org.thenesis.planetino2.engine.GameCore3D;
import org.thenesis.planetino2.game.CollisionDetection;
import org.thenesis.planetino2.game.GameObject;
import org.thenesis.planetino2.game.GameObjectManager;
import org.thenesis.planetino2.game.GameObjectRenderer;
import org.thenesis.planetino2.game.Player;
import org.thenesis.planetino2.game.SimpleGameObjectManager;
import org.thenesis.planetino2.graphics.Color;
import org.thenesis.planetino2.graphics.Graphics;
import org.thenesis.planetino2.graphics.Screen;
import org.thenesis.planetino2.input.InputManager;
import org.thenesis.planetino2.math3D.MovingTransform3D;
import org.thenesis.planetino2.math3D.ObjectLoader;
import org.thenesis.planetino2.math3D.PointLight3D;
import org.thenesis.planetino2.math3D.PolygonGroup;
import org.thenesis.planetino2.math3D.Transform3D;
import org.thenesis.planetino2.math3D.Vector3D;
import org.thenesis.planetino2.math3D.ViewWindow;


public class EditorEngine extends GameCore3D {
	
	private static final float PLAYER_SPEED = .5f;
	private static final float PLAYER_TURN_SPEED = 0.04f;
	private static final float CAMERA_HEIGHT = 100;
	
	private static final float ANGLE_DEFAULT = (float) Math.toRadians(75);
	private static final float ANGLE_ZOOM = (float) Math.toRadians(25);
	
	protected GameObjectManager gameObjectManager;
	
	MapLoader loader;
	BSPTree bspTree;
	protected String mapFile;
	protected CollisionDetection collisionDetection;

	public EditorEngine(Screen screen, InputManager inputManager) {
		super(screen, inputManager);
		this.inputManager = inputManager;
		
		
		float ambientLightIntensity = .2f;
		Vector lights = new Vector();
		lights.addElement(new PointLight3D(-100, 100, 100, .3f, -1));
		lights.addElement(new PointLight3D(100, 100, 0, .3f, -1));
		loader = new MapLoader(new BSPTreeBuilderWithPortals());
		loader.setObjectLights(lights, ambientLightIntensity);
		try {
			//bspTree = loader.loadMap("/res/", "cacao_demo.map");
			//bspTree = loader.loadMap("/res/", "quake.map"); //quake-one_bot.map
			bspTree = loader.loadMap("/res/", "quake-one_bot.map");
			//bspTree = loader.loadMap("/res/", "linuxtag.map");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public void init() {
		super.init();
		drawFrameRate = false;
	}
	
	@Override
	public void createPolygonRenderer() {
		// make the view window the entire screen
		viewWindow = new ViewWindow(0, 0, screen.getWidth(), screen.getHeight(), (float) Math.toRadians(75));
		
		Transform3D camera = new Transform3D();
		polygonRenderer = new BSPRenderer(camera, viewWindow);
	}
	

	@Override
	public void createPolygons() {
		Graphics g = screen.getGraphics();
		g.setColor(Color.BLACK.getRGB());
		g.fillRect(0, 0, screen.getWidth(), screen.getHeight());
		g.setColor(Color.WHITE.getRGB());
		int fontHeight = g.getFont().getHeight();
		g.drawString("Loading...", 5, screen.getHeight() - fontHeight);
		screen.update();

		//collisionDetection = new CollisionDetectionWithSliding(bspTree);
		//gameObjectManager = new GridGameObjectManager(bspTree.calcBounds(), collisionDetection);
		gameObjectManager = new SimpleGameObjectManager();
		gameObjectManager.addPlayer(new Player());
		
		((BSPRenderer) polygonRenderer).setGameObjectManager(gameObjectManager);

		createGameObjects(loader.getObjectsInMap());
		Transform3D start = loader.getPlayerStartTransform();
		gameObjectManager.getPlayer().getTransform().setTo(start);

		//		CollisionDetection collisionDetection = new CollisionDetectionWithSliding(bspTree);
		//		gameObjectManager = new GridGameObjectManager(bspTree.calcBounds(), collisionDetection);
		//		gameObjectManager.addPlayer(new Player());
		//
		//		// set up player bounds
		//		PolygonGroupBounds playerBounds = gameObjectManager.getPlayer().getBounds();
		//		playerBounds.setTopHeight(Player.DEFAULT_PLAYER_HEIGHT);
		//		playerBounds.setRadius(Player.DEFAULT_PLAYER_RADIUS);
		//
		//		((BSPRenderer) polygonRenderer).setGameObjectManager(gameObjectManager);
		//
		//		createGameObjects(loader.getObjectsInMap());
		//		Transform3D start = loader.getPlayerStartLocation();
		//		gameObjectManager.getPlayer().getTransform().setTo(start);
	}

	private void createGameObjects(Vector mapObjects) {

		Player player = (Player) gameObjectManager.getPlayer();
		player.setHealth(100);

		drawInstructions = false;
//		MessageQueue queue = MessageQueue.getInstance();
//		addOverlay(queue);
//		HeadsUpDisplay hud = new HeadsUpDisplay(player);
//		addOverlay(hud);
//		queue.setDebug(false);
		//queue.add("Use the mouse/arrow keys to move.");
		//queue.add("Press Esc to exit.");

		Enumeration i = mapObjects.elements();
		while (i.hasMoreElements()) {
			PolygonGroup group = (PolygonGroup) i.nextElement();
			String filename = group.getFilename();
				// static object
				gameObjectManager.add(new GameObject(group));
		}
	}

	@Override
	public void drawPolygons(Graphics g) {

		polygonRenderer.startFrame(screen);

		// draw polygons in bsp tree (set z buffer)
		((BSPRenderer) polygonRenderer).draw(g, bspTree);

		// draw game object polygons (check and set z buffer)
		gameObjectManager.draw(g, (GameObjectRenderer) polygonRenderer);

		polygonRenderer.endFrame(screen);

	}
	
	@Override
	public void updateWorld(long elapsedTime) {

		float angleVelocity;

		// cap elapsedTime
		elapsedTime = Math.min(elapsedTime, 100);

		Player player = (Player)gameObjectManager.getPlayer();
		MovingTransform3D playerTransform = player.getTransform();
		Vector3D velocity = playerTransform.getVelocity();

		//playerTransform.stop();
		velocity.x = 0;
		velocity.y = 0;
		velocity.z = 0;
		float x = -playerTransform.getSinAngleY();
		float z = -playerTransform.getCosAngleY();

		if (goForward.isPressed()) {
			velocity.add(x * PLAYER_SPEED, 0, z * PLAYER_SPEED);
		}
		if (goBackward.isPressed()) {
			velocity.add(-x * PLAYER_SPEED, 0, -z * PLAYER_SPEED);
		}
		if (goLeft.isPressed()) {
			velocity.add(z * PLAYER_SPEED, 0, -x * PLAYER_SPEED);
		}
		if (goRight.isPressed()) {
			velocity.add(-z * PLAYER_SPEED, 0, x * PLAYER_SPEED);
		}
		if (goUp.isPressed()) {
			velocity.add(0, PLAYER_SPEED, 0); // FIXME
		}
		if (goDown.isPressed()) {
			velocity.add(0, -PLAYER_SPEED, 0); // FIXME
		}
//		if (jump.isPressed()) {
//			player.setJumping(true);
//		}
		if (zoom.isPressed()) {
	        viewWindow.setAngle(ANGLE_ZOOM);
	    } else {
	    	if (viewWindow.getAngle() != ANGLE_DEFAULT) {
	    		viewWindow.setAngle(ANGLE_DEFAULT);
	    	}
	    }
		if (exit.isPressed()) {
			stop();
		}

		playerTransform.setVelocity(velocity);

		// look up/down (rotate around x)
		angleVelocity = Math.min(tiltUp.getAmount(), 200);
		angleVelocity += Math.max(-tiltDown.getAmount(), -200);
		playerTransform.setAngleVelocityX(angleVelocity * PLAYER_TURN_SPEED / 200);

		// turn (rotate around y)
		angleVelocity = Math.min(turnLeft.getAmount(), 200);
		angleVelocity += Math.max(-turnRight.getAmount(), -200);
		playerTransform.setAngleVelocityY(angleVelocity * PLAYER_TURN_SPEED / 200);

		// update objects
		gameObjectManager.update(elapsedTime);

		// limit look up/down
		float angleX = playerTransform.getAngleX();
		float limit = (float) Math.PI / 2;
		if (angleX < -limit) {
			playerTransform.setAngleX(-limit);
		} else if (angleX > limit) {
			playerTransform.setAngleX(limit);
		}

		// set the camera to be 100 units above the player
		Transform3D camera = polygonRenderer.getCamera();
		camera.setTo(playerTransform);
		camera.getLocation().add(0, CAMERA_HEIGHT, 0);
		
		
	}

	public static float getPlayerSpeed() {
		return PLAYER_SPEED;
	}

	public GameObjectManager getGameObjectManager() {
		return gameObjectManager;
	}

	public MapLoader getLoader() {
		return loader;
	}
	
	/** 
	 * Rebuild map without reloading the map file. 
	 * This method is called by the editor when map objects have been
	 * modified by the user.
	 */
	public void rebuildMap() {
		bspTree = loader.rebuildBSPTree();
	}

	public void saveMap(String filename) {
		MapSaver mapSaver = new MapSaver(loader, filename);
		mapSaver.save();
	}
	
	
	
}
