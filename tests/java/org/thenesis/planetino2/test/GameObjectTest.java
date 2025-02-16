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
import java.io.InputStream;
import java.util.Random;

import javax.imageio.ImageIO;

import org.thenesis.planetino2.backend.awt.AWTImage;
import org.thenesis.planetino2.backend.awt.AWTToolkit;
import org.thenesis.planetino2.game.GameCore3D;
import org.thenesis.planetino2.game.GameObject;
import org.thenesis.planetino2.game.GameObjectManager;
import org.thenesis.planetino2.game.GameObjectRenderer;
import org.thenesis.planetino2.game.SimpleGameObjectManager;
import org.thenesis.planetino2.graphics.Graphics;
import org.thenesis.planetino2.graphics.Image;
import org.thenesis.planetino2.graphics.Screen;
import org.thenesis.planetino2.graphics.Toolkit;
import org.thenesis.planetino2.graphics3D.ZBufferedRenderer;
import org.thenesis.planetino2.graphics3D.texture.ShadedTexture;
import org.thenesis.planetino2.graphics3D.texture.Texture;
import org.thenesis.planetino2.input.GameAction;
import org.thenesis.planetino2.input.InputManager;
import org.thenesis.planetino2.loader.ObjectLoader;
import org.thenesis.planetino2.loader.ResourceLoader;
import org.thenesis.planetino2.math3D.MovingTransform3D;
import org.thenesis.planetino2.math3D.PointLight3D;
import org.thenesis.planetino2.math3D.PolygonGroup;
import org.thenesis.planetino2.math3D.Rectangle3D;
import org.thenesis.planetino2.math3D.TexturedPolygon3D;
import org.thenesis.planetino2.math3D.Transform3D;
import org.thenesis.planetino2.math3D.Vector3D;
import org.thenesis.planetino2.math3D.ViewWindow;
import org.thenesis.planetino2.shooter.Blast;
import org.thenesis.planetino2.shooter.Bot;
import org.thenesis.planetino2.util.Vector;

/*
 * (23:37:53) Guillaume: Pour faire des collines y'a un algo simple ?
 (23:38:37) Mathieu: tu cr�es une damier de n x n
 (23:39:04) Mathieu: ensuite pour chaque sommet tu mets une valeur al�atoire
 (23:39:29) Mathieu: ensuite tu lisses en moyennant avec les sommets adjacents
 (23:39:47) Guillaume: du style sin(x) * (1 + random(0.5d)) ?
 (23:40:01) Guillaume: du style sin( x ) * (1 + random(0.5d)) ?
 (23:40:11) Mathieu: tu peux mettre des valeurs compl�tement al�atoire entre -128 et 127
 (23:40:42) Mathieu: ensuite tu lisses chaque sommet en moyennant avec les sommets environnant
 (23:40:55) Mathieu: tu peux moyenner plusieurs fois pour lisser plus fort
 (23:41:16) Mathieu: le mieux c'set de le faire dans un logiciel de dessin
 (23:41:30) Mathieu: et de charger l'image au format niveau de gris
 (23:41:38) Mathieu: avec gimp il y en a pour quelques minutes
 (23:42:40) Guillaume: ok je vois.
 (23:42:55) Guillaume: je vais essayer �a
 (23:42:57) Guillaume: merci !
 (23:43:14) Mathieu: tu prends une image 16x16 ou 32x32 par exemple
 *
 */

public class GameObjectTest extends GameCore3D {

	//    public static void main(String[] args) {
	//    	//System.out.println(GameObjectTest.class.getResourceAsStream("/res/robot.obj"));
	//    	
	//        new GameObjectTest().run();
	//    }

	private static final int NUM_BOTS = 5;
	private static final int NUM_POWER_UPS = 7;
	private static final int GAME_AREA_SIZE = 1500;
	private static final float PLAYER_SPEED = .5f;
	private static final float PLAYER_TURN_SPEED = 0.04f;
	private static final float BULLET_HEIGHT = 75;

	protected GameAction fire = new GameAction("fire", GameAction.DETECT_INITAL_PRESS_ONLY);

	private PolygonGroup robotModel;
	private PolygonGroup powerUpModel;
	private PolygonGroup blastModel;
	private GameObjectManager gameObjectManager;
	private TexturedPolygon3D floor;
	private ResourceLoader resourceLoader;

	public GameObjectTest(Screen screen, InputManager inputManager, ResourceLoader resourceLoader) {
		super(screen, inputManager);
		this.resourceLoader = resourceLoader;
	}

	public void init() {
		super.init();
	}

	public void createPolygons() {

		// create floor
		Image image = null;
		try {
			image = resourceLoader.loadImage("roof1.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Texture floorTexture = Texture.createTexture(image, true);
		((ShadedTexture) floorTexture).setDefaultShadeLevel(ShadedTexture.MAX_LEVEL * 3 / 4);
		Rectangle3D floorTextureBounds = new Rectangle3D(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0), new Vector3D(0,
				0, 1), floorTexture.getWidth(), floorTexture.getHeight());
		float s = GAME_AREA_SIZE;
		floor = new TexturedPolygon3D(new Vector3D[] { new Vector3D(-s, 0, s), new Vector3D(s, 0, s),
				new Vector3D(s, 0, -s), new Vector3D(-s, 0, -s) });
		floor.setTexture(floorTexture, floorTextureBounds);

		// set up the local lights for the model.
		float ambientLightIntensity = .5f;
		Vector lights = new Vector();
		lights.addElement(new PointLight3D(-100, 100, 100, .5f, -1));
		lights.addElement(new PointLight3D(100, 100, 0, .5f, -1));

		// load the object models
		ObjectLoader loader = new ObjectLoader(resourceLoader);
		loader.setLights(lights, ambientLightIntensity);
		try {
			robotModel = loader.loadObject("robot.obj3d");
			powerUpModel = loader.loadObject("cube.obj3d");
			blastModel = loader.loadObject("blast.obj3d");
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// create game objects
		gameObjectManager = new SimpleGameObjectManager();
		gameObjectManager.addPlayer(new GameObject(new PolygonGroup("Player")));
		gameObjectManager.getPlayer().getLocation().y = 5;
		for (int i = 0; i < NUM_BOTS; i++) {
			Bot object = new Bot((PolygonGroup) robotModel.clone());
			placeObject(object);
		}
		for (int i = 0; i < NUM_POWER_UPS; i++) {
			GameObject object = new GameObject((PolygonGroup) powerUpModel.clone());
			placeObject(object);
		}
	}

	// randomly place objects in game area
	public void placeObject(GameObject object) {
		float size = GAME_AREA_SIZE;

		Random random = new Random();

		object.getLocation().setTo((float) (random.nextDouble() * size - size / 2), 0,
				(float) (random.nextDouble() * size - size / 2));
		gameObjectManager.add(object);
	}

	public void createPolygonRenderer() {
		viewWindow = new ViewWindow(0, 0, screen.getWidth(), screen.getHeight(), (float) Math.toRadians(75));

		Transform3D camera = new Transform3D();
		polygonRenderer = new ZBufferedRenderer(camera, viewWindow);
	}

	public void updateWorld(long elapsedTime) {

		float angleVelocity;

		// cap elapsedTime
		elapsedTime = Math.min(elapsedTime, 100);

		GameObject player = gameObjectManager.getPlayer();
		MovingTransform3D playerTransform = player.getTransform();
		Vector3D velocity = playerTransform.getVelocity();

		playerTransform.stop();
		float x = -playerTransform.getSinAngleY();
		float z = -playerTransform.getCosAngleY();
		if (goForward.isPressed()) {
			velocity.add(x, 0, z);
		}
		if (goBackward.isPressed()) {
			velocity.add(-x, 0, -z);
		}
		if (goLeft.isPressed()) {
			velocity.add(z, 0, -x);
		}
		if (goRight.isPressed()) {
			velocity.add(-z, 0, x);
		}
		if (fire.isPressed()) {
			float cosX = playerTransform.getCosAngleX();
			float sinX = playerTransform.getSinAngleX();
			Blast blast = new Blast((PolygonGroup) blastModel.clone(), new Vector3D(cosX * x, sinX, cosX * z));
			// blast starting location needs work. looks like
			// the blast is coming out of your forehead when
			// you're shooting down.
			blast.getLocation().setTo(player.getX(), player.getY() + BULLET_HEIGHT, player.getZ());
			gameObjectManager.add(blast);
		}

		velocity.multiply(PLAYER_SPEED);
		playerTransform.setVelocity(velocity);

		// look up/down (rotate around x)
		angleVelocity = Math.min(tiltUp.getAmount(), 200);
		angleVelocity += Math.max(-tiltDown.getAmount(), -200);
		playerTransform.setAngleVelocityX(angleVelocity * PLAYER_TURN_SPEED / 200);

		// turn (rotate around y)
		angleVelocity = Math.min(turnLeft.getAmount(), 200);
		angleVelocity += Math.max(-turnRight.getAmount(), -200);
		playerTransform.setAngleVelocityY(angleVelocity * PLAYER_TURN_SPEED / 200);

		// for now, mark the entire world as visible in this frame.
		gameObjectManager.markAllVisible();

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
		camera.getLocation().add(0, 100, 0);

	}

	public void draw(Graphics g) {

		polygonRenderer.startFrame(screen);

		// draw floor
		polygonRenderer.draw(g, floor);

		// draw objects
		gameObjectManager.draw(g, (GameObjectRenderer) polygonRenderer);

		polygonRenderer.endFrame(screen);

		//super.drawText(g);
	}

}