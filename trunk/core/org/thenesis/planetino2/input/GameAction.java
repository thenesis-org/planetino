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

/**
 The GameAction class is an abstract to a user-initiated
 action, like jumping or moving. GameActions can be mapped
 to keys or the mouse with the InputManager.
 */
public class GameAction {

	/**
	 Normal behavior. The isPressed() method returns true
	 as long as the key is held down.
	 */
	public static final int NORMAL = 0;

	/**
	 Initial press behavior. The isPressed() method returns
	 true only after the key is first pressed, and not again
	 until the key is released and pressed again.
	 */
	public static final int DETECT_INITAL_PRESS_ONLY = 1;

	private static final int STATE_RELEASED = 0;
	private static final int STATE_PRESSED = 1;
	private static final int STATE_WAITING_FOR_RELEASE = 2;

	private String name;
	private int behavior;
	private int amount;
	private int state;

	/**
	 Create a new GameAction with the NORMAL behavior.
	 */
	public GameAction(String name) {
		this(name, NORMAL);
	}

	/**
	 Create a new GameAction with the specified behavior.
	 */
	public GameAction(String name, int behavior) {
		this.name = name;
		this.behavior = behavior;
		reset();
	}

	/**
	 Gets the name of this GameAction.
	 */
	public String getName() {
		return name;
	}

	/**
	 Resets this GameAction so that it appears like it hasn't
	 been pressed.
	 */
	public void reset() {
		state = STATE_RELEASED;
		amount = 0;
	}

	/**
	 Taps this GameAction. Same as calling press() followed
	 by release().
	 */
	public synchronized void tap() {
		press();
		release();
	}

	/**
	 Signals that the key was pressed.
	 */
	public synchronized void press() {
		press(1);
	}

	/**
	 Signals that the key was pressed a specified number of
	 times, or that the mouse move a spcified distance.
	 */
	public synchronized void press(int amount) {
		if (state != STATE_WAITING_FOR_RELEASE) {
			this.amount += amount;
			state = STATE_PRESSED;
		}

	}

	/**
	 Signals that the key was released
	 */
	public synchronized void release() {
		state = STATE_RELEASED;
	}

	/**
	 Returns whether the key was pressed or not since last
	 checked.
	 */
	public synchronized boolean isPressed() {
		return (getAmount() != 0);
	}

	/**
	 For keys, this is the number of times the key was
	 pressed since it was last checked.
	 For mouse movement, this is the distance moved.
	 */
	public synchronized int getAmount() {
		int retVal = amount;
		if (retVal != 0) {
			if (state == STATE_RELEASED) {
				amount = 0;
			} else if (behavior == DETECT_INITAL_PRESS_ONLY) {
				state = STATE_WAITING_FOR_RELEASE;
				amount = 0;
			}
		}
		return retVal;
	}
}
