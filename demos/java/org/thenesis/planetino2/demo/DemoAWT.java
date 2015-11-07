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

import java.awt.event.KeyEvent;

import org.thenesis.planetino2.backend.awt.AWTToolkit;
import org.thenesis.planetino2.graphics.Screen;
import org.thenesis.planetino2.graphics.Toolkit;
import org.thenesis.planetino2.input.InputManager;

public class DemoAWT  {
	
	public static void main(String[] args) {
		Toolkit.setToolkit(new AWTToolkit());
		
		Screen screen = Toolkit.getInstance().getScreen();
		InputManager inputManager = Toolkit.getInstance().getInputManager();
		
		inputManager.mapToKey(DemoEngine.exit, KeyEvent.VK_ESCAPE);
		inputManager.mapToKey(DemoEngine.goForward, KeyEvent.VK_R);
		inputManager.mapToKey(DemoEngine.goForward, KeyEvent.VK_UP);
		inputManager.mapToKey(DemoEngine.goBackward, KeyEvent.VK_A);
		inputManager.mapToKey(DemoEngine.goBackward, KeyEvent.VK_DOWN);
		inputManager.mapToKey(DemoEngine.goLeft, KeyEvent.VK_Z);
		inputManager.mapToKey(DemoEngine.goLeft, KeyEvent.VK_LEFT);
		inputManager.mapToKey(DemoEngine.goRight, KeyEvent.VK_E);
		inputManager.mapToKey(DemoEngine.goRight, KeyEvent.VK_RIGHT);
		inputManager.mapToKey(DemoEngine.goUp, KeyEvent.VK_PAGE_UP);
		inputManager.mapToKey(DemoEngine.goDown, KeyEvent.VK_PAGE_DOWN);
		inputManager.mapToMouse(DemoEngine.turnLeft, InputManager.MOUSE_MOVE_LEFT);
		inputManager.mapToMouse(DemoEngine.turnRight, InputManager.MOUSE_MOVE_RIGHT);
		inputManager.mapToMouse(DemoEngine.tiltUp, InputManager.MOUSE_MOVE_DOWN);
		inputManager.mapToMouse(DemoEngine.tiltDown, InputManager.MOUSE_MOVE_UP);
		inputManager.mapToMouse(DemoEngine.fire, InputManager.MOUSE_BUTTON_1);
		inputManager.mapToKey(DemoEngine.jump, KeyEvent.VK_SPACE);
		inputManager.mapToKey(DemoEngine.zoom, KeyEvent.VK_S);
		
		inputManager.setRelativeMouseMode(true);
		inputManager.showCursor(false);
		
		DemoEngine engine = new DemoEngine(screen, inputManager);
		
		Thread engineThread = new Thread(engine);
		engineThread.start();
	}


	



}
