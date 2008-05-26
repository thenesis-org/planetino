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
package org.thenesis.planetino2.game;

//import java.awt.Graphics;

import javax.microedition.lcdui.Graphics;

import org.thenesis.planetino2.math3D.Rectangle;

/**
 The GameObjectManager interface provides methods to keep
 track of and draw GameObjects.
 */
public interface GameObjectManager {

	/**
	 Marks all objects within the specified 2D bounds
	 as potentially visible (should be drawn).
	 */
	public void markVisible(Rectangle bounds);

	/**
	 Marks all objects as potentially visible (should be drawn).
	 */
	public void markAllVisible();

	/**
	 Adds a GameObject to this manager.
	 */
	public void add(GameObject object);

	/**
	 Adds a GameObject to this manager, specifying it as the
	 player object. An existing player object, if any,
	 is not removed.
	 */
	public void addPlayer(GameObject player);

	/**
	 Gets the object specified as the Player object, or null
	 if no player object was specified.
	 */
	public GameObject getPlayer();

	/**
	 Removes a GameObject from this manager.
	 */
	public void remove(GameObject object);

	/**
	 Updates all objects based on the amount of time passed
	 from the last update.
	 */
	public void update(long elapsedTime);

	/**
	 Draws all visible objects.
	 */
	public void draw(Graphics g, GameObjectRenderer r);

}
