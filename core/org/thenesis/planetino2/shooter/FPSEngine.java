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
package org.thenesis.planetino2.shooter;

import java.io.IOException;
import java.util.Enumeration;

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
import org.thenesis.planetino2.game.Box;
import org.thenesis.planetino2.game.BoxMatrix;
import org.thenesis.planetino2.game.CollisionDetection;
import org.thenesis.planetino2.game.CollisionDetectionWithSliding;
import org.thenesis.planetino2.game.GameCore3D;
import org.thenesis.planetino2.game.GameObject;
import org.thenesis.planetino2.game.GameObjectRenderer;
import org.thenesis.planetino2.game.MessageQueue;
import org.thenesis.planetino2.game.Player;
import org.thenesis.planetino2.game.Poster;
import org.thenesis.planetino2.game.Trigger;
import org.thenesis.planetino2.graphics.Color;
import org.thenesis.planetino2.graphics.Graphics;
import org.thenesis.planetino2.graphics.Screen;
import org.thenesis.planetino2.input.GameAction;
import org.thenesis.planetino2.input.InputManager;
import org.thenesis.planetino2.loader.MapLoader;
import org.thenesis.planetino2.loader.ObjectLoader;
import org.thenesis.planetino2.loader.ResourceLoader;
import org.thenesis.planetino2.math3D.BoxPolygonGroup;
import org.thenesis.planetino2.math3D.CompositePolygonGroup;
import org.thenesis.planetino2.math3D.Lightable;
import org.thenesis.planetino2.math3D.MovingTransform3D;
import org.thenesis.planetino2.math3D.PointLight3D;
import org.thenesis.planetino2.math3D.PolygonGroup;
import org.thenesis.planetino2.math3D.PosterPolygonGroup;
import org.thenesis.planetino2.math3D.Transform3D;
import org.thenesis.planetino2.math3D.TriggerPolygonGroup;
import org.thenesis.planetino2.math3D.Vector3D;
import org.thenesis.planetino2.math3D.ViewWindow;
import org.thenesis.planetino2.path.AStarSearchWithBSP;
import org.thenesis.planetino2.shooter.levels.KillboxLevel;
import org.thenesis.planetino2.shooter.levels.NearCraftLevel;
import org.thenesis.planetino2.shooter.levels.QuakeLevel;
import org.thenesis.planetino2.shooter.levels.TownOfFuryLevel;
import org.thenesis.planetino2.sound.Music;
import org.thenesis.planetino2.sound.SoundManager;
import org.thenesis.planetino2.util.Vector;

public class FPSEngine extends GameCore3D implements LevelManager, ShooterEngine {
	
	private static final float PLAYER_SPEED = .5f;
	private static final float PLAYER_TURN_SPEED = 0.04f;
	private static final float CAMERA_HEIGHT = 100;
	
	private static final float ANGLE_DEFAULT = (float) Math.toRadians(75);
	private static final float ANGLE_ZOOM = (float) Math.toRadians(25);
	
	public static final String OBJECT_FILENAME_HEALTH_PACK = "health_pack.obj3d";
	public static final String OBJECT_FILENAME_ADRENALINE = "adrenaline.obj";
	public static final String OBJECT_FILENAME_AMMO_PACK = "ammo_pack.obj";
	public static final String OBJECT_FILENAME_WEAPON = "GrindCable.obj";
	
	public static final int LEVEL_NUMBER = 4;
	public static int currentLevel;
	public static Level[] levels;
	
	protected ResourceLoader resourceLoader;
	protected SoundManager soundManager;

	protected ShooterObjectManager gameObjectManager;
	protected PolygonGroup botProjectileModel;
	
	protected WeaponManager weaponManager;
	
	public static GameAction chooseRiffleWeapon = new GameAction("riffle", GameAction.DETECT_INITAL_PRESS_ONLY);
	public static GameAction chooseGravityWeapon = new GameAction("gravityGun", GameAction.DETECT_INITAL_PRESS_ONLY);
	public static GameAction detachObjectFromGravityWeapon = new GameAction("detach object from gravityGun", GameAction.DETECT_INITAL_PRESS_ONLY);
	public static GameAction teleportPlayer = new GameAction("Teleport player", GameAction.DETECT_INITAL_PRESS_ONLY);
	
	private Brain averageBrain;
	private Brain aggressiveBrain;
	private Brain scaredBrain;

	protected BSPTree bspTree;
	protected String mapFile;
	protected CollisionDetection collisionDetection;
	
	private ShooterOverlay shooterOverlay;

	public FPSEngine(Screen screen, InputManager inputManager, ResourceLoader resourceLoader) {
		super(screen, inputManager);
		this.resourceLoader = resourceLoader;
		
		// Create Levels
		currentLevel = 1;
		levels = new Level[LEVEL_NUMBER];
		levels[0] = new QuakeLevel(this);
		levels[1] = new KillboxLevel(this);
		levels[2] = new NearCraftLevel(this);
		levels[3] = new TownOfFuryLevel(this);
	}
	
	@Override
	public void init() {
		
		// Set up the local lights for the model.
		float ambientLightIntensity = .8f;
		Vector lights = new Vector();
		lights.addElement(new PointLight3D(-100, 100, 100, .5f, -1));
		lights.addElement(new PointLight3D(100, 100, 0, .5f, -1));

		// Load weapons and projectiles
		ObjectLoader loader = new ObjectLoader(resourceLoader);
		loader.setLights(lights, ambientLightIntensity);
		try {
			weaponManager = new WeaponManager(loader);
			weaponManager.load();
			botProjectileModel = loader.loadObject("botprojectile.obj3d");
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		super.init();
		
		getCurrentLevel().initialize();
		
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
		soundManager.init();
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

		MapLoader loader = new MapLoader(resourceLoader, new BSPTreeBuilderWithPortals());
		loader.setObjectLights(lights, ambientLightIntensity);
		//MapLoader loader = new MapLoader();
		//loader.setObjectLights(lights, ambientLightIntensity);

		try {
			String mapName = getCurrentLevel().getMapName();
			bspTree = loader.loadMap(mapName); //quake-one_bot.map, killbox.map, NearCraft.map
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		collisionDetection = new CollisionDetectionWithSliding(bspTree);
		gameObjectManager = new ShooterObjectManager(bspTree.calcBounds(), collisionDetection);
		gameObjectManager.addPlayer(new ShooterPlayer(soundManager, weaponManager));
		
		((BSPRenderer) polygonRenderer).setGameObjectManager(gameObjectManager);

		createGameObjects(loader.getObjectsInMap());
		Transform3D start = loader.getPlayerStartTransform();
		gameObjectManager.getPlayer().getTransform().setTo(start);
		
		// Apply lights in last position
		applyLights(loader.getObjectsInMap(), loader.getLights());
		

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
	
	private void applyLights(Vector mapObjects, Vector lights) {
		Enumeration i = mapObjects.elements();
		while (i.hasMoreElements()) {
			PolygonGroup group = (PolygonGroup) i.nextElement();
			if (group instanceof Lightable) {
				Lightable lightable = ((Lightable)group);
				float ambientLightIntensity = lightable.getAmbientLightIntensity();
				((Lightable)group).applyLights(lights, ambientLightIntensity);
			}
		}
	}

	private void createGameObjects(Vector mapObjects) {

		ShooterPlayer player = (ShooterPlayer) gameObjectManager.getPlayer();
		player.setHealth(100);

		drawInstructions = false;
		MessageQueue queue = MessageQueue.getInstance();
		addOverlay(queue);
		shooterOverlay = new ShooterOverlay(player, gameObjectManager);
		addOverlay(shooterOverlay);
		queue.setDebug(false);
		//queue.add("Use the mouse/arrow keys to move.");
		//queue.add("Press Esc to exit.");

		createBrains();

		Enumeration i = mapObjects.elements();
		while (i.hasMoreElements()) {
			PolygonGroup group = (PolygonGroup) i.nextElement();
			String filename = group.getFilename();
			
			// Current level can set specific behavior for game objects 
			GameObject levelGameObject = getCurrentLevel().createGameObject(group);
			if (levelGameObject != null) {
				gameObjectManager.add(levelGameObject);
				continue;
			}
			
			// If no specific behavior, set the default one
			if ("DemonicEye2.obj".equals(filename)) {
				AIBot bot = new NoisyAIBot(soundManager, group, collisionDetection, scaredBrain, botProjectileModel);
				gameObjectManager.addEnnemy(bot);
			} else if ("Droid.obj".equals(filename)) {
				NoisyAIBot bot = new NoisyAIBot(soundManager, group, collisionDetection, averageBrain, botProjectileModel);
				bot.setFlyHeight(150);
				bot.setSoundLoop("alien_not_alone.wav");
				gameObjectManager.addEnnemy(bot);
			} else if ("drfreak.obj".equals(filename)) {
				NoisyAIBot bot = new NoisyAIBot(soundManager, group, collisionDetection, scaredBrain, botProjectileModel);
				bot.setFlyHeight(0);
				bot.setSoundLoop("old_man.wav");
				gameObjectManager.addEnnemy(bot);
			} else if ("Serpent.obj".equals(filename)) {
				NoisyAIBot bot = new NoisyAIBot(soundManager, group, collisionDetection, scaredBrain, botProjectileModel);
				bot.setFlyHeight(0);
				bot.setSoundLoop("snake.wav");
				gameObjectManager.addEnnemy(bot);
			} else if ("health_pack.obj3d".equals(filename)) {
				float angleVelocity = 0.0010f;
				MovingTransform3D mainTransform = group.getTransform();
				mainTransform.setAngleVelocityY(angleVelocity);
				mainTransform.setAngleVelocityX(angleVelocity);
				mainTransform.rotateAngleY(50f);
				mainTransform.rotateAngleX(50f);
				RespawnableItem item = new RespawnableItem(group);
				gameObjectManager.addRespawnableItem(item);
			} else if ("ammo_pack.obj".equals(filename)) {
				float angleVelocity = 0.0010f;
				MovingTransform3D mainTransform = group.getTransform();
				mainTransform.setAngleVelocityY(angleVelocity);
				mainTransform.setAngleVelocityX(angleVelocity);
				mainTransform.rotateAngleY(50f);
				mainTransform.rotateAngleX(50f);
				RespawnableItem item = new RespawnableItem(group);
				gameObjectManager.addRespawnableItem(item);
			} else if ("GrindCable.obj".equals(filename)) {
				float angleVelocity = 0.0010f;
				MovingTransform3D mainTransform = group.getTransform();
				mainTransform.setAngleVelocityY(angleVelocity);
				mainTransform.setAngleVelocityX(angleVelocity);
				mainTransform.rotateAngleY(50f);
				mainTransform.rotateAngleX(50f);
				gameObjectManager.add(new GameObject(group));
			} else if (group instanceof PosterPolygonGroup) {
				gameObjectManager.add(new Poster(group));
			} else if (group instanceof BoxPolygonGroup) {
				gameObjectManager.add(new Box(group));
			} else if (group instanceof CompositePolygonGroup) {
				gameObjectManager.add(new BoxMatrix((CompositePolygonGroup)group));
			} else if (group instanceof TriggerPolygonGroup) {
				TriggerPolygonGroup triggerGroup = (TriggerPolygonGroup) group;
				gameObjectManager.add(new Trigger(triggerGroup));
			} else {
				// static object
				gameObjectManager.add(new CatchableGameObject(group));
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

		ShooterPlayer player = (ShooterPlayer)gameObjectManager.getPlayer();
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
		if (chooseRiffleWeapon.isPressed()) {
			player.setWeapon(Weapon.WEAPON_RIFFLE);
	    }
		if (chooseGravityWeapon.isPressed()) {
			player.setWeapon(Weapon.WEAPON_GRAVITY_GUN);
	    }
		if (detachObjectFromGravityWeapon.isPressed()) {
			player.detachObjectFromGravityGun();
	    } 
		if (teleportPlayer.isPressed()) {
			player.teleport();
	    }
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
		
		// Check if the player has won/lost the match
		getCurrentLevel().checkGameState();
		
	}
	
	/* Level manager */
	
	public Level getCurrentLevel() {
		return levels[currentLevel];
	}
	
	public void changeLevel() {
		soundManager.close();
		removeOverlay(shooterOverlay);
		
		currentLevel++;
		if (currentLevel >= LEVEL_NUMBER) {
			currentLevel = 0;
		}
		init();
	}
	
	/* Shooter engine interface */

	public SoundManager getSoundManager() {
		return soundManager;
	}
	
	public ShooterObjectManager getGameObjectManager() {
		return gameObjectManager;
	}

	public BSPTree getBspTree() {
		return bspTree;
	}

	public CollisionDetection getCollisionDetection() {
		return collisionDetection;
	}

	public LevelManager getLevelManager() {
		return this;
	}
	
}
