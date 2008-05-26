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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.thenesis.planetino2.graphics.Screen;
import org.thenesis.planetino2.input.InputManager;

public class PlanetinoDemo extends MIDlet {

	private Display display;

	//@Override
	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		// TODO Auto-generated method stub
	}

	//@Override
	protected void pauseApp() {
		// TODO Auto-generated method stub
	}

	//@Override
	protected void startApp() throws MIDletStateChangeException {
		display = Display.getDisplay(this);

		final ScreenImpl screenImpl = new ScreenImpl(false);
		InputManager inputManager = new InputManager() {
			public String getKeyName(int keyCode) {
				return screenImpl.getKeyName(keyCode);
			}
		};
		screenImpl.setInputManager(inputManager);

		DemoEngine engine = new DemoEngine(screenImpl, inputManager) {
			public void init() {
				super.init();
				this.inputManager.mapToKey(goForward, screenImpl.getKeyCode(Canvas.UP));
				this.inputManager.mapToKey(goBackward, screenImpl.getKeyCode(Canvas.DOWN));
				this.inputManager.mapToKey(goLeft, screenImpl.getKeyCode(Canvas.LEFT));
				this.inputManager.mapToKey(goRight, screenImpl.getKeyCode(Canvas.RIGHT));
				this.inputManager.mapToKey(goUp, screenImpl.getKeyCode(Canvas.GAME_A));
				this.inputManager.mapToKey(goDown, screenImpl.getKeyCode(Canvas.GAME_B));
				this.inputManager.mapToMouse(fire, InputManager.MOUSE_BUTTON_1);
				this.inputManager.mapToKey(jump, screenImpl.getKeyCode(Canvas.GAME_C));
			}
		};

		display.setCurrent(screenImpl);

		Thread engineThread = new Thread(engine);
		engineThread.start();
		//engine.run();

	}

	class ScreenImpl extends GameCanvas implements Screen {

		private InputManager inputManager;

		protected ScreenImpl(boolean suppressKeyEvents) {
			super(suppressKeyEvents);
			//System.out.println("[DEBUG] ScreenImpl.<init> :" + getWidth() + " " + getHeight());
		}

		public void setInputManager(InputManager inputManager) {
			this.inputManager = inputManager;
		}

		public Graphics getGraphics() {
			return super.getGraphics();
		}

		public void restoreScreen() {
			// TODO Auto-generated method stub
		}

		public void update() {
			flushGraphics();
		}

		protected void keyPressed(int keyCode) {
			inputManager.keyPressed(keyCode);
		}

		protected void keyReleased(int keyCode) {
			inputManager.keyReleased(keyCode);
		}

		protected void pointerDragged(int x, int y) {
			inputManager.pointerDragged(x, y);
		}

		protected void pointerPressed(int x, int y) {
			inputManager.pointerPressed(x, y);
		}

		protected void pointerReleased(int x, int y) {
			inputManager.pointerReleased();
		}

	}

	class InputManagerImpl extends InputManager {
		public String getKeyName(int keyCode) {
			return null;
		}
	}

}
