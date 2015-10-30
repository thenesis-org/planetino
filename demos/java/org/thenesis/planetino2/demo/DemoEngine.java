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
package org.thenesis.planetino2.demo;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import org.thenesis.planetino2.ai.AIBot;
import org.thenesis.planetino2.ai.Brain;
import org.thenesis.planetino2.ai.NoisyAIBot;
import org.thenesis.planetino2.ai.pattern.AimPattern;
import org.thenesis.planetino2.ai.pattern.AttackPatternRush;
import org.thenesis.planetino2.ai.pattern.AttackPatternStrafe;
import org.thenesis.planetino2.ai.pattern.DodgePatternRandom;
import org.thenesis.planetino2.ai.pattern.DodgePatternZigZag;
import org.thenesis.planetino2.ai.pattern.RunAwayPattern;
import org.thenesis.planetino2.bsp2D.BSPRenderer;
import org.thenesis.planetino2.bsp2D.BSPTree;
import org.thenesis.planetino2.bsp2D.BSPTreeBuilderWithPortals;
import org.thenesis.planetino2.bsp2D.MapLoader;
import org.thenesis.planetino2.engine.GameCore3D;
import org.thenesis.planetino2.engine.shooter3D.Bot;
import org.thenesis.planetino2.engine.shooter3D.HeadsUpDisplay;
import org.thenesis.planetino2.game.CollisionDetection;
import org.thenesis.planetino2.game.CollisionDetectionWithSliding;
import org.thenesis.planetino2.game.GameObject;
import org.thenesis.planetino2.game.GameObjectManager;
import org.thenesis.planetino2.game.GameObjectRenderer;
import org.thenesis.planetino2.game.GridGameObjectManager;
import org.thenesis.planetino2.game.MessageQueue;
import org.thenesis.planetino2.game.Player;
import org.thenesis.planetino2.graphics.Color;
import org.thenesis.planetino2.graphics.Graphics;
import org.thenesis.planetino2.graphics.Screen;
import org.thenesis.planetino2.input.GameAction;
import org.thenesis.planetino2.input.InputManager;
import org.thenesis.planetino2.math3D.MovingTransform3D;
import org.thenesis.planetino2.math3D.ObjectLoader;
import org.thenesis.planetino2.math3D.PointLight3D;
import org.thenesis.planetino2.math3D.PolygonGroup;
import org.thenesis.planetino2.math3D.Transform3D;
import org.thenesis.planetino2.math3D.Vector3D;
import org.thenesis.planetino2.math3D.ViewWindow;
import org.thenesis.planetino2.path.AStarSearchWithBSP;
import org.thenesis.planetino2.sound.Music;
import org.thenesis.planetino2.sound.SoundManager;

public class DemoEngine extends GameCore3D {
	
	private static final float PLAYER_SPEED = .5f;
	private static final float PLAYER_TURN_SPEED = 0.04f;
	private static final float CAMERA_HEIGHT = 100;
	
	public static GameAction fire = new GameAction("fire", GameAction.DETECT_INITAL_PRESS_ONLY);
	public static GameAction jump = new GameAction("jump", GameAction.DETECT_INITAL_PRESS_ONLY);
	
	protected SoundManager soundManager;

	protected GameObjectManager gameObjectManager;
	protected PolygonGroup blastModel;
	protected PolygonGroup botProjectileModel;
	
	private Brain averageBrain;
	private Brain aggressiveBrain;
	private Brain scaredBrain;

	protected BSPTree bspTree;
	protected String mapFile;
	protected CollisionDetection collisionDetection;

	public DemoEngine(Screen screen, InputManager inputManager) {
		super(screen, inputManager);
		this.inputManager = inputManager;
	}
	
	@Override
	public void init() {
		
		// set up the local lights for the model.
		float ambientLightIntensity = .8f;
		Vector lights = new Vector();
		lights.addElement(new PointLight3D(-100, 100, 100, .5f, -1));
		lights.addElement(new PointLight3D(100, 100, 0, .5f, -1));

		// load the object model
		ObjectLoader loader = new ObjectLoader();
		loader.setLights(lights, ambientLightIntensity);
		try {
			blastModel = loader.loadObject("/res/", "blast.obj3d");
			botProjectileModel = loader.loadObject("/res/", "botprojectile.obj3d");
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		super.init();
		
		((Player)gameObjectManager.getPlayer()).setBlastModel(blastModel);
		
		Music ambientMusic = soundManager.getMusic("ambient_loop.wav");
		ambientMusic.setVolume(0.3);
		ambientMusic.play(true);
		Music introMusic = soundManager.getMusic("prepare.wav");
		introMusic.play(false);
		
		drawFrameRate = true;

	}
	
	@Override
	public void createPolygonRenderer() {
		// make the view window the entire screen
		viewWindow = new ViewWindow(0, 0, screen.getWidth(), screen.getHeight(), (float) Math.toRadians(75));
		
		Transform3D camera = new Transform3D();
		polygonRenderer = new BSPRenderer(camera, viewWindow);
	}
	

	@Override
	public void createSoundManager() {
		soundManager = new SoundManager(viewWindow, polygonRenderer.getCamera());
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

		float ambientLightIntensity = .2f;
		Vector lights = new Vector();
		lights.addElement(new PointLight3D(-100, 100, 100, .3f, -1));
		lights.addElement(new PointLight3D(100, 100, 0, .3f, -1));

		MapLoader loader = new MapLoader(new BSPTreeBuilderWithPortals());
		loader.setObjectLights(lights, ambientLightIntensity);
		//MapLoader loader = new MapLoader();
		//loader.setObjectLights(lights, ambientLightIntensity);

		try {
			//bspTree = loader.loadMap("/res/", "cacao_demo.map");
			//bspTree = loader.loadMap("/res/", "quake.map"); //quake-one_bot.map
			bspTree = loader.loadMap("/res/", "quake-one_bot.map");
			//bspTree = loader.loadMap("/res/", "linuxtag.map");
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		collisionDetection = new CollisionDetectionWithSliding(bspTree);
		gameObjectManager = new GridGameObjectManager(bspTree.calcBounds(), collisionDetection);
		gameObjectManager.addPlayer(new ShooterPlayer(soundManager));

		((BSPRenderer) polygonRenderer).setGameObjectManager(gameObjectManager);

		createGameObjects(loader.getObjectsInMap());
		Transform3D start = loader.getPlayerStartLocation();
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
		MessageQueue queue = MessageQueue.getInstance();
		addOverlay(queue);
		addOverlay(new HeadsUpDisplay(player));
		queue.setDebug(false);
		//queue.add("Use the mouse/arrow keys to move.");
		//queue.add("Press Esc to exit.");

		createBrains();

		Enumeration i = mapObjects.elements();
		while (i.hasMoreElements()) {
			PolygonGroup group = (PolygonGroup) i.nextElement();
			String filename = group.getFilename();
			if ("robot.obj3d".equals(filename)) {
				gameObjectManager.add(new Bot(group));
			} else if ("averagebot.obj3d".equals(filename)) {
				AIBot bot = new NoisyAIBot(soundManager, group, collisionDetection, averageBrain, botProjectileModel);
				gameObjectManager.add(bot);
				bot.setFlyHeight(48);
			} else if ("aggressivebot.obj3d".equals(filename)) {
				AIBot bot = new AIBot(group, collisionDetection, aggressiveBrain, botProjectileModel);
				gameObjectManager.add(bot);
			} else if ("DemonicEye2.obj".equals(filename)) {
				AIBot bot = new NoisyAIBot(soundManager, group, collisionDetection, scaredBrain, botProjectileModel);
				gameObjectManager.add(bot);
			} else if ("health_pack.obj3d".equals(filename)) {
				float angleVelocity = 0.0010f;
				MovingTransform3D mainTransform = group.getTransform();
				mainTransform.setAngleVelocityY(angleVelocity);
				mainTransform.setAngleVelocityX(angleVelocity);
				mainTransform.rotateAngleY(50f);
				mainTransform.rotateAngleX(50f);
				gameObjectManager.add(new GameObject(group));
			} else if ("classpath-cube.obj3d".equals(filename)) {
				float angleVelocity = 0.0010f;
				MovingTransform3D mainTransform = group.getTransform();
				mainTransform.setAngleVelocityY(angleVelocity);
				mainTransform.setAngleVelocityX(angleVelocity);
				mainTransform.rotateAngleY(50f);
				mainTransform.rotateAngleX(50f);
				gameObjectManager.add(new GameObject(group));
			} else {
				// static object
				gameObjectManager.add(new GameObject(group));
			}
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

	protected void createBrains() {

		//		averageBrain = new Brain();
		//		aggressiveBrain = new Brain();
		//		scaredBrain = new Brain();

		averageBrain = new Brain();
		averageBrain.attackPathFinder = new AttackPatternRush(bspTree);
		averageBrain.aimPathFinder = new AimPattern(bspTree);
		averageBrain.dodgePathFinder = new DodgePatternRandom(bspTree);
		averageBrain.idlePathFinder = null;
		averageBrain.chasePathFinder = new AStarSearchWithBSP(bspTree);
		averageBrain.runAwayPathFinder = new RunAwayPattern(bspTree);

		averageBrain.attackProbability = 0.50f;
		averageBrain.dodgeProbability = 0.40f;
		averageBrain.runAwayProbability = 0.10f;

		averageBrain.decisionTime = 4000;
		averageBrain.aimTime = 1000;
		averageBrain.hearDistance = 1000;

		// aggresive brain
		aggressiveBrain = new Brain();
		aggressiveBrain.attackPathFinder = new AttackPatternStrafe(bspTree);
		aggressiveBrain.aimPathFinder = new AimPattern(bspTree);
		aggressiveBrain.dodgePathFinder = new DodgePatternZigZag(bspTree);
		aggressiveBrain.idlePathFinder = null;
		aggressiveBrain.chasePathFinder = new AStarSearchWithBSP(bspTree);
		aggressiveBrain.runAwayPathFinder = null;

		aggressiveBrain.attackProbability = 0.8f;
		aggressiveBrain.dodgeProbability = 0.2f;
		aggressiveBrain.runAwayProbability = 0;

		aggressiveBrain.decisionTime = 2000;
		aggressiveBrain.aimTime = 300;
		aggressiveBrain.hearDistance = 1000;

		// scaredy brain
		scaredBrain = new Brain();
		scaredBrain.attackPathFinder = new AttackPatternRush(bspTree);
		scaredBrain.aimPathFinder = new AimPattern(bspTree);
		scaredBrain.dodgePathFinder = new DodgePatternZigZag(bspTree);
		scaredBrain.idlePathFinder = null;
		scaredBrain.chasePathFinder = new AStarSearchWithBSP(bspTree);
		scaredBrain.runAwayPathFinder = new RunAwayPattern(bspTree);

		scaredBrain.attackProbability = 0.20f;
		scaredBrain.dodgeProbability = 0.40f;
		scaredBrain.runAwayProbability = 0.40f;

		scaredBrain.decisionTime = 4000;
		scaredBrain.aimTime = 1000;
		scaredBrain.hearDistance = 2000;

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
		if (jump.isPressed()) {
			player.setJumping(true);
		}
		 if (fire.isPressed()) {
	        player.fireProjectile();
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
}
