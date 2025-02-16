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

import java.awt.Canvas;
import org.thenesis.planetino2.util.Vector;
import org.thenesis.planetino2.game.GameCore;
import org.thenesis.planetino2.game.GameCore3D;
import org.thenesis.planetino2.graphics.Color;
import org.thenesis.planetino2.graphics.Graphics;
import org.thenesis.planetino2.graphics.Screen;
import org.thenesis.planetino2.graphics3D.PolygonRenderer;
import org.thenesis.planetino2.graphics3D.SolidPolygonRenderer;
import org.thenesis.planetino2.input.GameAction;
import org.thenesis.planetino2.input.InputManager;
import org.thenesis.planetino2.math3D.Polygon3D;
import org.thenesis.planetino2.math3D.SolidPolygon3D;
import org.thenesis.planetino2.math3D.Transform3D;
import org.thenesis.planetino2.math3D.Vector3D;
import org.thenesis.planetino2.math3D.ViewWindow;

public class Simple3DTest2 extends GameCore3D {

	//	public static void main(String[] args) {
	//        new Simple3DTest2().run();
	//    }

	protected PolygonRenderer polygonRenderer;
	protected ViewWindow viewWindow;
	protected Vector polygons;

	private boolean drawFrameRate = false;
	private boolean drawInstructions = true;

	// for calculating frame rate
	private int numFrames;
	private long startTime;
	private float frameRate;

	protected InputManager inputManager;
	private GameAction exit = new GameAction("exit");
	private GameAction smallerView = new GameAction("smallerView", GameAction.DETECT_INITAL_PRESS_ONLY);
	private GameAction largerView = new GameAction("largerView", GameAction.DETECT_INITAL_PRESS_ONLY);
	private GameAction frameRateToggle = new GameAction("frameRateToggle", GameAction.DETECT_INITAL_PRESS_ONLY);
	private GameAction goForward = new GameAction("goForward");
	private GameAction goBackward = new GameAction("goBackward");
	private GameAction goUp = new GameAction("goUp");
	private GameAction goDown = new GameAction("goDown");
	private GameAction goLeft = new GameAction("goLeft");
	private GameAction goRight = new GameAction("goRight");
	private GameAction turnLeft = new GameAction("turnLeft");
	private GameAction turnRight = new GameAction("turnRight");
	private GameAction tiltUp = new GameAction("tiltUp");
	private GameAction tiltDown = new GameAction("tiltDown");
	private GameAction tiltLeft = new GameAction("tiltLeft");
	private GameAction tiltRight = new GameAction("tiltRight");

	public Simple3DTest2(Screen screen, InputManager inputManager) {
		super(screen, inputManager);
		this.inputManager = inputManager;
	}

	public void init() {
		super.init();

		//        inputManager = new InputManager(
		//            screen.getFullScreenWindow());
		//        inputManager.setRelativeMouseMode(true);
		//        inputManager.setCursor(InputManager.INVISIBLE_CURSOR);

		// create the polygon renderer
		createPolygonRenderer();

		// create polygons
		polygons = new Vector();
		createPolygons();
	}

	// create a house (convex polyhedra)
	public void createPolygons() {
		SolidPolygon3D poly;

		// walls
		poly = new SolidPolygon3D(new Vector3D(-200, 0, -1000), new Vector3D(200, 0, -1000), new Vector3D(200, 250,
				-1000), new Vector3D(-200, 250, -1000));
		poly.setColor(Color.WHITE);
		polygons.addElement(poly);
		poly = new SolidPolygon3D(new Vector3D(-200, 0, -1400), new Vector3D(-200, 250, -1400), new Vector3D(200, 250,
				-1400), new Vector3D(200, 0, -1400));
		poly.setColor(Color.WHITE);
		polygons.addElement(poly);
		poly = new SolidPolygon3D(new Vector3D(-200, 0, -1400), new Vector3D(-200, 0, -1000), new Vector3D(-200, 250,
				-1000), new Vector3D(-200, 250, -1400));
		poly.setColor(Color.GRAY);
		polygons.addElement(poly);
		poly = new SolidPolygon3D(new Vector3D(200, 0, -1000), new Vector3D(200, 0, -1400), new Vector3D(200, 250,
				-1400), new Vector3D(200, 250, -1000));
		poly.setColor(Color.GRAY);
		polygons.addElement(poly);

		// door and windows
		poly = new SolidPolygon3D(new Vector3D(0, 0, -1000), new Vector3D(75, 0, -1000), new Vector3D(75, 125, -1000),
				new Vector3D(0, 125, -1000));
		poly.setColor(new Color(0x660000));
		polygons.addElement(poly);
		poly = new SolidPolygon3D(new Vector3D(-150, 150, -1000), new Vector3D(-100, 150, -1000), new Vector3D(-100,
				200, -1000), new Vector3D(-150, 200, -1000));
		poly.setColor(new Color(0x660000));
		polygons.addElement(poly);

		// roof
		poly = new SolidPolygon3D(new Vector3D(-200, 250, -1000), new Vector3D(200, 250, -1000), new Vector3D(75, 400,
				-1200), new Vector3D(-75, 400, -1200));
		poly.setColor(new Color(0x660000));
		polygons.addElement(poly);
		poly = new SolidPolygon3D(new Vector3D(-200, 250, -1400), new Vector3D(-200, 250, -1000), new Vector3D(-75,
				400, -1200));
		poly.setColor(new Color(0x330000));
		polygons.addElement(poly);
		poly = new SolidPolygon3D(new Vector3D(200, 250, -1400), new Vector3D(-200, 250, -1400), new Vector3D(-75, 400,
				-1200), new Vector3D(75, 400, -1200));
		poly.setColor(new Color(0x660000));
		polygons.addElement(poly);
		poly = new SolidPolygon3D(new Vector3D(200, 250, -1000), new Vector3D(200, 250, -1400), new Vector3D(75, 400,
				-1200));
		poly.setColor(new Color(0x330000));
		polygons.addElement(poly);
	}

	public void createPolygonRenderer() {
		// make the view window the entire screen
		viewWindow = new ViewWindow(0, 0, screen.getWidth(), screen.getHeight(), (float) Math.toRadians(75));

		Transform3D camera = new Transform3D(0, 100, 0);
		polygonRenderer = new SolidPolygonRenderer(camera, viewWindow);
	}

	/**
	 Sets the view bounds, centering the view on the screen.
	 */
	public void setViewBounds(int width, int height) {
		width = Math.min(width, screen.getWidth());
		height = Math.min(height, screen.getHeight());
		width = Math.max(64, width);
		height = Math.max(48, height);
		viewWindow.setBounds((screen.getWidth() - width) / 2, (screen.getHeight() - height) / 2, width, height);
	}

	public void update(long elapsedTime) {
		if (exit.isPressed()) {
			stop();
			return;
		}

		// check options
		if (largerView.isPressed()) {
			setViewBounds(viewWindow.getWidth() + 64, viewWindow.getHeight() + 48);
		} else if (smallerView.isPressed()) {
			setViewBounds(viewWindow.getWidth() - 64, viewWindow.getHeight() - 48);
		}
		if (frameRateToggle.isPressed()) {
			drawFrameRate = !drawFrameRate;
		}

		// cap elapsedTime
		elapsedTime = Math.min(elapsedTime, 100);

		float angleChange = 0.0002f * elapsedTime;
		float distanceChange = .5f * elapsedTime;

		Transform3D camera = polygonRenderer.getCamera();
		Vector3D cameraLoc = camera.getLocation();

		// apply movement
		if (goForward.isPressed()) {
			cameraLoc.x -= distanceChange * camera.getSinAngleY();
			cameraLoc.z -= distanceChange * camera.getCosAngleY();
		}
		if (goBackward.isPressed()) {
			cameraLoc.x += distanceChange * camera.getSinAngleY();
			cameraLoc.z += distanceChange * camera.getCosAngleY();
		}
		if (goLeft.isPressed()) {
			cameraLoc.x -= distanceChange * camera.getCosAngleY();
			cameraLoc.z += distanceChange * camera.getSinAngleY();
		}
		if (goRight.isPressed()) {
			cameraLoc.x += distanceChange * camera.getCosAngleY();
			cameraLoc.z -= distanceChange * camera.getSinAngleY();
		}
		if (goUp.isPressed()) {
			cameraLoc.y += distanceChange;
		}
		if (goDown.isPressed()) {
			cameraLoc.y -= distanceChange;
		}

		// look up/down (rotate around x)
		int tilt = tiltUp.getAmount() - tiltDown.getAmount();
		tilt = Math.min(tilt, 200);
		tilt = Math.max(tilt, -200);

		// limit how far you can look up/down
		float newAngleX = camera.getAngleX() + tilt * angleChange;
		newAngleX = Math.max(newAngleX, (float) -Math.PI / 2);
		newAngleX = Math.min(newAngleX, (float) Math.PI / 2);
		camera.setAngleX(newAngleX);

		// turn (rotate around y)
		int turn = turnLeft.getAmount() - turnRight.getAmount();
		turn = Math.min(turn, 200);
		turn = Math.max(turn, -200);
		camera.rotateAngleY(turn * angleChange);

		// tilet head left/right (rotate around z)
		if (tiltLeft.isPressed()) {
			camera.rotateAngleZ(10 * angleChange);
		}
		if (tiltRight.isPressed()) {
			camera.rotateAngleZ(-10 * angleChange);
		}
	}

	public void draw(Graphics g) {

		// draw polygons
		polygonRenderer.startFrame(screen);
		for (int i = 0; i < polygons.size(); i++) {
			polygonRenderer.draw(g, (Polygon3D) polygons.elementAt(i));
		}
		polygonRenderer.endFrame(screen);

		drawText(g);
	}

	public void drawText(Graphics g) {

		//        // draw text
		//        g.setColor(Color.WHITE.getRGB());
		//        if (drawInstructions) {
		//            g.drawString("Use the mouse/arrow keys to move. " +
		//                "Press Esc to exit.", 5, fontSize);
		//        }
		//        // (you may have to turn off the BufferStrategy in
		//        // ScreenManager for more accurate tests)
		//        if (drawFrameRate) {
		//            calcFrameRate();
		//            g.drawString(frameRate + " frames/sec", 5,
		//                screen.getHeight() - 5);
		//        }
	}

	public void calcFrameRate() {
		numFrames++;
		long currTime = System.currentTimeMillis();

		// calculate the frame rate every 500 milliseconds
		if (currTime > startTime + 500) {
			frameRate = (float) numFrames * 1000 / (currTime - startTime);
			startTime = currTime;
			numFrames = 0;
		}
	}

}