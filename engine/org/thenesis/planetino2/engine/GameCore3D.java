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
package org.thenesis.planetino2.engine;

import java.util.Vector;

import org.thenesis.planetino2.graphics.Color;
import org.thenesis.planetino2.graphics.Font;
import org.thenesis.planetino2.graphics.Graphics;
import org.thenesis.planetino2.graphics.Screen;
import org.thenesis.planetino2.graphics3D.Overlay;
import org.thenesis.planetino2.graphics3D.PolygonRenderer;
import org.thenesis.planetino2.graphics3D.SolidPolygonRenderer;
import org.thenesis.planetino2.input.GameAction;
import org.thenesis.planetino2.input.InputManager;
import org.thenesis.planetino2.math3D.Polygon3D;
import org.thenesis.planetino2.math3D.Transform3D;
import org.thenesis.planetino2.math3D.Vector3D;
import org.thenesis.planetino2.math3D.ViewWindow;
import org.thenesis.planetino2.util.TimeSmoothie;

public abstract class GameCore3D extends GameCore {

	private static final long INSTRUCTIONS_TIME = 4000;

	protected PolygonRenderer polygonRenderer;
	protected ViewWindow viewWindow;
	protected Vector polygons;

	private Vector overlays = new Vector();

	protected TimeSmoothie timeSmoothie = new TimeSmoothie();

	protected boolean drawFrameRate = false;
	protected boolean drawInstructions = true;
	private boolean timeSmoothing = false;
	private long drawInstructionsTime = 0;

	// for calculating frame rate
	private int numFrames;
	private long startTime;
	private float frameRate;

	protected InputManager inputManager;
	private GameAction exit = new GameAction("exit");
	private GameAction smallerView = new GameAction("smallerView", GameAction.DETECT_INITAL_PRESS_ONLY);
	private GameAction largerView = new GameAction("largerView", GameAction.DETECT_INITAL_PRESS_ONLY);
	private GameAction frameRateToggle = new GameAction("frameRateToggle", GameAction.DETECT_INITAL_PRESS_ONLY);
	protected GameAction goForward = new GameAction("goForward");
	protected GameAction goBackward = new GameAction("goBackward");
	protected GameAction goUp = new GameAction("goUp");
	protected GameAction goDown = new GameAction("goDown");
	protected GameAction goLeft = new GameAction("goLeft");
	protected GameAction goRight = new GameAction("goRight");
	protected GameAction turnLeft = new GameAction("turnLeft");
	protected GameAction turnRight = new GameAction("turnRight");
	protected GameAction tiltUp = new GameAction("tiltUp");
	protected GameAction tiltDown = new GameAction("tiltDown");
	protected GameAction tiltLeft = new GameAction("tiltLeft");
	protected GameAction tiltRight = new GameAction("tiltRight");
	protected GameAction timeSmoothingToggle = new GameAction("timeSmoothingToggle",
			GameAction.DETECT_INITAL_PRESS_ONLY);

	public GameCore3D(Screen screen, InputManager inputManager) {
		super(screen);
		this.inputManager = inputManager;
	}

	@Override
	public void init() {
		super.init();

		//        inputManager = new InputManager(
		//            screen.getFullScreenWindow());
		//        inputManager.setRelativeMouseMode(true);
		//        inputManager.setCursor(InputManager.INVISIBLE_CURSOR);

		//        inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);
		//        inputManager.mapToKey(goForward, KeyEvent.VK_W);
		//        inputManager.mapToKey(goForward, KeyEvent.VK_UP);
		//        inputManager.mapToKey(goBackward, KeyEvent.VK_S);
		//        inputManager.mapToKey(goBackward, KeyEvent.VK_DOWN);
		//        inputManager.mapToKey(goLeft, KeyEvent.VK_A);
		//        inputManager.mapToKey(goLeft, KeyEvent.VK_LEFT);
		//        inputManager.mapToKey(goRight, KeyEvent.VK_D);
		//        inputManager.mapToKey(goRight, KeyEvent.VK_RIGHT);
		//        inputManager.mapToKey(goUp, KeyEvent.VK_PAGE_UP);
		//        inputManager.mapToKey(goDown, KeyEvent.VK_PAGE_DOWN);
		//        inputManager.mapToMouse(turnLeft,
		//            InputManager.MOUSE_MOVE_LEFT);
		//        inputManager.mapToMouse(turnRight,
		//            InputManager.MOUSE_MOVE_RIGHT);
		//        inputManager.mapToMouse(tiltUp,
		//            InputManager.MOUSE_MOVE_UP);
		//        inputManager.mapToMouse(tiltDown,
		//            InputManager.MOUSE_MOVE_DOWN);
		//
		//        inputManager.mapToKey(tiltLeft, KeyEvent.VK_INSERT);
		//        inputManager.mapToKey(tiltRight, KeyEvent.VK_DELETE);
		//
		//        inputManager.mapToKey(smallerView, KeyEvent.VK_SUBTRACT);
		//        inputManager.mapToKey(smallerView, KeyEvent.VK_MINUS);
		//        inputManager.mapToKey(largerView, KeyEvent.VK_ADD);
		//        inputManager.mapToKey(largerView, KeyEvent.VK_PLUS);
		//        inputManager.mapToKey(largerView, KeyEvent.VK_EQUALS);
		//        inputManager.mapToKey(frameRateToggle, KeyEvent.VK_R);
		//
		//        inputManager.mapToKey(timeSmoothingToggle, KeyEvent.VK_T);

//		inputManager.mapToKey(goForward, 'r');
//		inputManager.mapToKey(goForward, Canvas.KEY_NUM2);
//		inputManager.mapToKey(goBackward, 'a');
//		inputManager.mapToKey(goBackward, Canvas.KEY_NUM8);
//		inputManager.mapToKey(goLeft, 'z');
//		inputManager.mapToKey(goLeft, Canvas.KEY_NUM4);
//		inputManager.mapToKey(goRight, 'e');
//		inputManager.mapToKey(goRight, Canvas.KEY_NUM6);
//		inputManager.mapToKey(goUp, Canvas.KEY_NUM3);
//		inputManager.mapToKey(goDown, Canvas.KEY_NUM1);
//		inputManager.mapToMouse(turnLeft, InputManager.MOUSE_MOVE_LEFT);
//		inputManager.mapToMouse(turnRight, InputManager.MOUSE_MOVE_RIGHT);
//		inputManager.mapToMouse(tiltUp, InputManager.MOUSE_MOVE_UP);
//		inputManager.mapToMouse(tiltDown, InputManager.MOUSE_MOVE_DOWN);

		// create the polygon renderer
		createPolygonRenderer();

		// create polygons
		polygons = new Vector();
		createPolygons();
	}

	public abstract void createPolygons();

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

		// clear the screen if view size changed
		// (clear both buffers)
		for (int i = 0; i < 2; i++) {
			Graphics g = screen.getGraphics();
			g.setColor(Color.BLACK.getRGB());
			g.fillRect(0, 0, screen.getWidth(), screen.getHeight());
			screen.update();
		}

	}

	@Override
	public void update(long elapsedTime) {

		drawInstructionsTime += elapsedTime;
		if (drawInstructionsTime >= INSTRUCTIONS_TIME) {
			drawInstructions = false;
		}

		// check options
		if (exit.isPressed()) {
			stop();
			return;
		}
		if (largerView.isPressed()) {
			setViewBounds(viewWindow.getWidth() + 64, viewWindow.getHeight() + 48);
		} else if (smallerView.isPressed()) {
			setViewBounds(viewWindow.getWidth() - 64, viewWindow.getHeight() - 48);
		}
		if (frameRateToggle.isPressed()) {
			drawFrameRate = !drawFrameRate;
		}
		if (timeSmoothingToggle.isPressed()) {
			timeSmoothing = !timeSmoothing;
			drawInstructions = true;
			drawInstructionsTime = 0;
		}

		if (timeSmoothing) {
			elapsedTime = timeSmoothie.getTime(elapsedTime);
		}

		updateWorld(elapsedTime);
		
		// update overlays
        for (int i=0; i<overlays.size(); i++) {
            Overlay overlay = (Overlay)overlays.elementAt(i);
            if (overlay.isEnabled()) {
                overlay.update(elapsedTime);
            }
        }

	}

	public void updateWorld(long elapsedTime) {

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

	@Override
	public void draw(Graphics g) {
		int viewX1 = viewWindow.getLeftOffset();
		int viewY1 = viewWindow.getTopOffset();
		int viewX2 = viewX1 + viewWindow.getWidth();
		int viewY2 = viewY1 + viewWindow.getHeight();
		if (viewX1 != 0 || viewY1 != 0) {
			g.setColor(Color.BLACK.getRGB());
			g.fillRect(0, 0, viewX1, screen.getHeight());
			g.fillRect(viewX2, 0, screen.getWidth() - viewX2, screen.getHeight());
			g.fillRect(viewX1, 0, viewWindow.getWidth(), viewY1);
			g.fillRect(viewX1, viewY2, viewWindow.getWidth(), screen.getHeight() - viewY2);
		}

		drawPolygons(g);
		drawOverlays(g);
		//drawText(g);
	}

	public void drawPolygons(Graphics g) {
		polygonRenderer.startFrame(screen);
		for (int i = 0; i < polygons.size(); i++) {
			polygonRenderer.draw(g, (Polygon3D) polygons.elementAt(i));
		}
		polygonRenderer.endFrame(screen);
	}

//	public void drawText(Graphics g) {
//
//		// draw text
//		if (drawInstructions) {
//			// fade out the text over 500 ms
//			long fade = INSTRUCTIONS_TIME - drawInstructionsTime;
//			if (fade < 500) {
//				fade = fade * 255 / 500;
//				g.setColor(new Color(0xffffff | ((int) fade << 24), true).getRGB());
//			} else {
//				g.setColor(Color.WHITE.getRGB());
//			}
//
//			//            g.drawString("Use the mouse/arrow keys to move. " +
//			//                "Press Esc to exit.", 5, fontSize);
//			//            g.drawString("Press T to toggle time smoothing. " +
//			//                "(Currently " + (timeSmoothing?"ON":"OFF") + ")",
//			//                5, fontSize*2);
//		}
//
//		// (you may have to turn off the BufferStrategy in
//		// ScreenManager for more accurate tests)
//		//        if (drawFrameRate) {
//		//            g.setColor(Color.WHITE);
//		//            calcFrameRate();
//		//            g.drawString(frameRate + " frames/sec", 5,
//		//                screen.getHeight() - 5);
//		//        }
//	}

	public void drawOverlays(Graphics g) {

		// draw text
		if (drawInstructions) {
			// fade out the text over 500 ms
			long fade = INSTRUCTIONS_TIME - drawInstructionsTime;
			if (fade < 500) {
				fade = fade * 255 / 500;
				g.setColor(new Color(0xffffff | ((int) fade << 24), true).getRGB());
			} else {
				g.setColor(Color.WHITE.getRGB());
			}
			
			System.out.println("drawInstructions");

			g.drawString("Use the mouse/arrow keys to move. " + "Press Esc to exit.", 5, fontSize);
		}
		// (you may have to turn off the BufferStrategy in
		// ScreenManager for more accurate tests)
		if (drawFrameRate) {
			g.setColor(Color.WHITE.getRGB());
			calcFrameRate();
			g.drawString(frameRate + " frames/sec", 5, screen.getHeight() - 5);
		}

		// draw overlays
		for (int i = 0; i < overlays.size(); i++) {
			Overlay overlay = (Overlay) overlays.elementAt(i);
			if (overlay.isEnabled()) {
				Font prevFont = g.getFont();
				int prevColor = g.getColor();
				overlay.draw(g, viewWindow);
				g.setColor(prevColor);
				g.setFont(prevFont);
			}
		}
	}

	public void addOverlay(Overlay overlay) {
		overlays.addElement(overlay);
	}

	public void removeOverlay(Overlay overlay) {
		overlays.removeElement(overlay);
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