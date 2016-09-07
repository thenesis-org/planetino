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
package org.thenesis.planetino2.input;

import org.thenesis.planetino2.util.Vector;

import org.thenesis.planetino2.math3D.Point;

/**
 The InputManager manages input of key and mouse events.
 Events are mapped to GameActions.
 */
public abstract class InputManager {

	// mouse codes
	public static final int MOUSE_MOVE_LEFT = 0;
	public static final int MOUSE_MOVE_RIGHT = 1;
	public static final int MOUSE_MOVE_UP = 2;
	public static final int MOUSE_MOVE_DOWN = 3;
	public static final int MOUSE_WHEEL_UP = 4;
	public static final int MOUSE_WHEEL_DOWN = 5;
	public static final int MOUSE_BUTTON_1 = 6;
	public static final int MOUSE_BUTTON_2 = 7;
	public static final int MOUSE_BUTTON_3 = 8;

	public static final int KEYCODE_OFFSET = 100;

	private static final int NUM_MOUSE_CODES = 9;

	// key codes are defined in java.awt.KeyEvent.
	// most of the codes (except for some rare ones like
	// "alt graph") are less than 600.
	private static final int NUM_KEY_CODES = 700;

	private GameAction[] keyActions = new GameAction[NUM_KEY_CODES];
	private GameAction[] mouseActions = new GameAction[NUM_MOUSE_CODES];

	protected Point mouseLocation;
	protected Point centerLocation;
	protected boolean isRecentering;
	protected boolean isRelativeMouseModeEnabled = false;

	/**
	 Creates a new InputManager that listens to input from the
	 specified component.
	 */
	public InputManager() {

		mouseLocation = new Point();
		centerLocation = new Point();

	}
	
	public abstract void showCursor(boolean enabled);

	/**
	 Sets whether realtive mouse mode is on or not. For
	 relative mouse mode, the mouse is "locked" in the center
	 of the screen, and only the changed in mouse movement
	 is measured. In normal mode, the mouse is free to move
	 about the screen.
	 */
	public abstract void setRelativeMouseMode(boolean mode);
	
	//public void setRelativeMouseMode(boolean mode) {
		//        if (mode == isRelativeMouseMode()) {
		//            return;
		//        }
		//
		//        if (mode) {
		//            try {
		//                robot = new Robot();
		//                mouseLocation.x = comp.getWidth() / 2;
		//                mouseLocation.y = comp.getHeight() / 2;
		//                recenterMouse();
		//            }
		//            catch (AWTException ex) {
		//                // couldn't create robot!
		//                robot = null;
		//            }
		//        }
		//        else {
		//            robot = null;
		//        }
	//}

	/**
	 Returns whether or not relative mouse mode is on.
	 */
	public final boolean isRelativeMouseMode() {
		//return (robot != null);
		return isRelativeMouseModeEnabled;
	}

	/**
	 Maps a GameAction to a specific key. The key codes are
	 defined in java.awt.KeyEvent. If the key already has
	 a GameAction mapped to it, the new GameAction overwrites
	 it.
	 */
	public void mapToKey(GameAction gameAction, int keyCode) {
		keyActions[keyCode + KEYCODE_OFFSET] = gameAction;
	}

	/**
	 Maps a GameAction to a specific mouse action. The mouse
	 codes are defined herer in InputManager (MOUSE_MOVE_LEFT,
	 MOUSE_BUTTON_1, etc). If the mouse action already has
	 a GameAction mapped to it, the new GameAction overwrites
	 it.
	 */
	public void mapToMouse(GameAction gameAction, int mouseCode) {
		mouseActions[mouseCode] = gameAction;
	}

	/**
	 Clears all mapped keys and mouse actions to this
	 GameAction.
	 */
	public void clearMap(GameAction gameAction) {
		for (int i = 0; i < keyActions.length; i++) {
			if (keyActions[i] == gameAction) {
				keyActions[i] = null;
			}
		}

		for (int i = 0; i < mouseActions.length; i++) {
			if (mouseActions[i] == gameAction) {
				mouseActions[i] = null;
			}
		}

		gameAction.reset();
	}

	/**
	 Gets a List of names of the keys and mouse actions mapped
	 to this GameAction. Each entry in the List is a String.
	 */
	public Vector getMaps(GameAction gameCode) {
		Vector list = new Vector();

		for (int i = 0; i < keyActions.length; i++) {
			if (keyActions[i] == gameCode) {
				list.addElement(getKeyName(i));
			}
		}

		for (int i = 0; i < mouseActions.length; i++) {
			if (mouseActions[i] == gameCode) {
				list.addElement(getMouseName(i));
			}
		}
		return list;
	}

	/**
	 Resets all GameActions so they appear like they haven't
	 been pressed.
	 */
	public void resetAllGameActions() {
		for (int i = 0; i < keyActions.length; i++) {
			if (keyActions[i] != null) {
				keyActions[i].reset();
			}
		}

		for (int i = 0; i < mouseActions.length; i++) {
			if (mouseActions[i] != null) {
				mouseActions[i].reset();
			}
		}
	}

	/**
	 Gets the name of a key code.
	 */
	public abstract String getKeyName(int keyCode);

	//    public static String getKeyName(int keyCode) {
	//        return KeyEvent.getKeyText(keyCode);
	//    }

	/**
	 Gets the name of a mouse code.
	 */
	public static String getMouseName(int mouseCode) {
		switch (mouseCode) {
		case MOUSE_MOVE_LEFT:
			return "Mouse Left";
		case MOUSE_MOVE_RIGHT:
			return "Mouse Right";
		case MOUSE_MOVE_UP:
			return "Mouse Up";
		case MOUSE_MOVE_DOWN:
			return "Mouse Down";
		case MOUSE_WHEEL_UP:
			return "Mouse Wheel Up";
		case MOUSE_WHEEL_DOWN:
			return "Mouse Wheel Down";
		case MOUSE_BUTTON_1:
			return "Mouse Button 1";
		case MOUSE_BUTTON_2:
			return "Mouse Button 2";
		case MOUSE_BUTTON_3:
			return "Mouse Button 3";
		default:
			return "Unknown mouse code " + mouseCode;
		}
	}

	/**
	 Gets the x position of the mouse.
	 */
	public int getMouseX() {
		return mouseLocation.x;
	}

	/**
	 Gets the y position of the mouse.
	 */
	public int getMouseY() {
		return mouseLocation.y;
	}

	/**
	 Uses the Robot class to try to postion the mouse in the
	 center of the screen.
	 <p>Note that use of the Robot class may not be available
	 on all platforms.
	 */
	protected abstract void recenterMouse();
	
//	private synchronized void recenterMouse() {
//		//        if (robot != null && comp.isShowing()) {
//		//            centerLocation.x = comp.getWidth() / 2;
//		//            centerLocation.y = comp.getHeight() / 2;
//		//            SwingUtilities.convertPointToScreen(centerLocation,
//		//                comp);
//		//            isRecentering = true;
//		//            robot.mouseMove(centerLocation.x, centerLocation.y);
//		//        }
//	}

	private GameAction getKeyAction(int keyCode) {
		//System.out.println("getKeyAction: " + keyCode);

		if (keyCode < keyActions.length) {
			return keyActions[keyCode + KEYCODE_OFFSET];
		} else {
			return null;
		}
	}

	private GameAction getMouseButtonAction(int button) {
		
		return mouseActions[button];

//		if (mouseActions[MOUSE_BUTTON_1] != null) {
//			return mouseActions[MOUSE_BUTTON_1];
//		} else {
//			return null;
//		}

		//        int mouseCode = getMouseButtonCode(e);
		//        if (mouseCode != -1) {
		//             return mouseActions[mouseCode];
		//        }
		//        else {
		//             return null;
		//        }
	}

	// from the KeyListener interface
	public void keyPressed(int keyCode) {
		GameAction gameAction = getKeyAction(keyCode);
		if (gameAction != null) {
			gameAction.press();
		}

	}

	// from the KeyListener interface
	public void keyReleased(int keyCode) {
		GameAction gameAction = getKeyAction(keyCode);
		if (gameAction != null) {
			gameAction.release();
		}
	}

	// from the MouseListener interface
	public void pointerPressed(int x, int y, int button) {
		mouseLocation.x = x;
		mouseLocation.y = y;

		GameAction gameAction = getMouseButtonAction(button);
		if (gameAction != null) {
			gameAction.press();
		}
	}

	// from the MouseListener interface
	public void pointerReleased(int button) {
		GameAction gameAction = getMouseButtonAction(button);
		if (gameAction != null) {
			gameAction.release();
		}
	}

	// from the MouseMotionListener interface
	public synchronized void pointerDragged(int x, int y) {
		// this event is from re-centering the mouse - ignore it
		if (isRecentering) //&& centerLocation.x == x && centerLocation.y == y)
		{
			isRecentering = false;
		} else {
			int dx = x - mouseLocation.x;
			int dy = y - mouseLocation.y;
			mouseHelper(MOUSE_MOVE_LEFT, MOUSE_MOVE_RIGHT, dx);
			mouseHelper(MOUSE_MOVE_UP, MOUSE_MOVE_DOWN, dy);

			if (isRelativeMouseMode()) {
				recenterMouse();
			}
		}

		mouseLocation.x = x;
		mouseLocation.y = y;

	}

	private void mouseHelper(int codeNeg, int codePos, int amount) {
		GameAction gameAction;
		if (amount < 0) {
			gameAction = mouseActions[codeNeg];
		} else {
			gameAction = mouseActions[codePos];
		}
		if (gameAction != null) {
			gameAction.press(Math.abs(amount));
			gameAction.release();
		}
	}

}
