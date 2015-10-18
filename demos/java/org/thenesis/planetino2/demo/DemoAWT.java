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

import java.awt.Canvas;
import java.awt.event.KeyEvent;

import org.thenesis.planetino2.graphics.Graphics;
import org.thenesis.planetino2.graphics.Screen;
import org.thenesis.planetino2.input.InputManager;

public class DemoAWT  {


	//@Override
	protected void destroyApp(boolean unconditional) {
		// TODO Auto-generated method stub
	}

	//@Override
	protected void pauseApp() {
		// TODO Auto-generated method stub
	}

	//@Override
	protected void startApp() {

		final ScreenImpl screenImpl = new ScreenImpl();
		InputManager inputManager = new InputManager() {
			public String getKeyName(int keyCode) {
				return KeyEvent.getKeyText(keyCode);
			}
		};
		screenImpl.setInputManager(inputManager);

		DemoEngine engine = new DemoEngine(screenImpl, inputManager) {
			public void init() {
				super.init();
				
				//inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);
				inputManager.mapToKey(goForward, KeyEvent.VK_W);
				inputManager.mapToKey(goForward, KeyEvent.VK_UP);
				inputManager.mapToKey(goBackward, KeyEvent.VK_S);
				inputManager.mapToKey(goBackward, KeyEvent.VK_DOWN);
				inputManager.mapToKey(goLeft, KeyEvent.VK_A);
				inputManager.mapToKey(goLeft, KeyEvent.VK_LEFT);
				inputManager.mapToKey(goRight, KeyEvent.VK_D);
				inputManager.mapToKey(goRight, KeyEvent.VK_RIGHT);
				inputManager.mapToKey(goUp, KeyEvent.VK_PAGE_UP);
				inputManager.mapToKey(goDown, KeyEvent.VK_PAGE_DOWN);
				inputManager.mapToMouse(turnLeft, InputManager.MOUSE_MOVE_LEFT);
				inputManager.mapToMouse(turnRight, InputManager.MOUSE_MOVE_RIGHT);
				inputManager.mapToMouse(tiltUp, InputManager.MOUSE_MOVE_UP);
				inputManager.mapToMouse(tiltDown, InputManager.MOUSE_MOVE_DOWN);
				
				inputManager.mapToMouse(fire, InputManager.MOUSE_BUTTON_1);
				inputManager.mapToKey(jump, KeyEvent.VK_SPACE);
				
			}
		};

		Thread engineThread = new Thread(engine);
		engineThread.start();
		//engine.run();

	}



}
