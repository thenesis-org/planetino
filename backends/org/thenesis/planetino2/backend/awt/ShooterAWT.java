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
package org.thenesis.planetino2.backend.awt;

import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.thenesis.planetino2.graphics.Screen;
import org.thenesis.planetino2.graphics.Toolkit;
import org.thenesis.planetino2.input.InputManager;
import org.thenesis.planetino2.shooter.FPSEngine;

public class ShooterAWT  {
	
	public static void main(String[] args) {
		ShooterAWT demo = new ShooterAWT();
		demo.start();
	}
	
	public void start() {
		Toolkit.setToolkit(new AWTToolkit());
		
		Screen screen = Toolkit.getInstance().getScreen(1024, 768);
		//Screen screen = Toolkit.getInstance().getScreen(1920, 1080);
		screen.show();
		screen.setFullScreen(false);
		
		InputManager inputManager = Toolkit.getInstance().getInputManager();
		inputManager.mapToKey(FPSEngine.exit, KeyEvent.VK_ESCAPE);
		inputManager.mapToKey(FPSEngine.goForward, KeyEvent.VK_R);
		inputManager.mapToKey(FPSEngine.goForward, KeyEvent.VK_UP);
		inputManager.mapToKey(FPSEngine.goBackward, KeyEvent.VK_A);
		inputManager.mapToKey(FPSEngine.goBackward, KeyEvent.VK_DOWN);
		inputManager.mapToKey(FPSEngine.goLeft, KeyEvent.VK_Z);
		inputManager.mapToKey(FPSEngine.goLeft, KeyEvent.VK_LEFT);
		inputManager.mapToKey(FPSEngine.goRight, KeyEvent.VK_E);
		inputManager.mapToKey(FPSEngine.goRight, KeyEvent.VK_RIGHT);
		inputManager.mapToKey(FPSEngine.goUp, KeyEvent.VK_PAGE_UP);
		inputManager.mapToKey(FPSEngine.goDown, KeyEvent.VK_PAGE_DOWN);
		inputManager.mapToMouse(FPSEngine.turnLeft, InputManager.MOUSE_MOVE_LEFT);
		inputManager.mapToMouse(FPSEngine.turnRight, InputManager.MOUSE_MOVE_RIGHT);
		inputManager.mapToMouse(FPSEngine.tiltUp, InputManager.MOUSE_MOVE_DOWN);
		inputManager.mapToMouse(FPSEngine.tiltDown, InputManager.MOUSE_MOVE_UP);
		inputManager.mapToMouse(FPSEngine.fire, InputManager.MOUSE_BUTTON_1);
		inputManager.mapToKey(FPSEngine.jump, KeyEvent.VK_SPACE);
		inputManager.mapToKey(FPSEngine.zoom, KeyEvent.VK_S);
		inputManager.mapToKey(FPSEngine.chooseGravityWeapon, KeyEvent.VK_1);
		inputManager.mapToKey(FPSEngine.chooseGravityWeapon, KeyEvent.VK_G);
		inputManager.mapToKey(FPSEngine.chooseRiffleWeapon, KeyEvent.VK_2);
		inputManager.mapToKey(FPSEngine.detachObjectFromGravityWeapon, KeyEvent.VK_D);
		inputManager.mapToMouse(FPSEngine.teleportPlayer, InputManager.MOUSE_BUTTON_3);
		
		inputManager.setRelativeMouseMode(true);
		inputManager.showCursor(false);
		
		/* Do all tasks in the Swing EDT to avoid thread issues */
		//Thread engineThread = new Thread(engine);
		//engineThread.start();
		FPSEngine engine = new FPSEngine(screen, inputManager, Toolkit.getInstance().getResourceLoader());
		engine.init();
		Worker worker = new Worker(engine);
		try {
			while(engine.isRunning()) {
				SwingUtilities.invokeAndWait(worker);
			}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		engine.close();
		engine.lazilyExit();
		
	}
	
	
	private class Worker implements Runnable {
		 
		 private FPSEngine engine;
		 long startTime;
		 long currTime;
		 
		 Worker(FPSEngine engine) {
			 this.engine = engine;
			 startTime = System.currentTimeMillis();
			 currTime = startTime;
		 }
	      
		public void run() {
			long elapsedTime = System.currentTimeMillis() - currTime;
			currTime += elapsedTime;

			engine.tick(elapsedTime);

//			// don't take a nap! run as fast as possible
//			try {
//				Thread.sleep(1);
//			} catch (InterruptedException ex) {
//			}
			
		}

	   }


	



}
