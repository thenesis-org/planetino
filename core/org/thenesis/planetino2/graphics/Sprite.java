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
package org.thenesis.planetino2.graphics;



public class Sprite {

	protected Animation anim;
	// position (pixels)
	private float x;
	private float y;
	// velocity (pixels per millisecond)
	private float dx;
	private float dy;

	/**
	 Creates a new Sprite object with the specified Animation.
	 */
	public Sprite(Animation anim) {
		this.anim = anim;
	}

	/**
	 Updates this Sprite's Animation and its position based
	 on the velocity.
	 */
	public void update(long elapsedTime) {
		x += dx * elapsedTime;
		y += dy * elapsedTime;
		if (anim != null) {
			anim.update(elapsedTime);
		}
	}

	/**
	 Gets this Sprite's current x position.
	 */
	public float getX() {
		return x;
	}

	/**
	 Gets this Sprite's current y position.
	 */
	public float getY() {
		return y;
	}

	/**
	 Sets this Sprite's current x position.
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 Sets this Sprite's current y position.
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 Gets this Sprite's width, based on the size of the
	 current image.
	 */
	public int getWidth() {
		return anim.getImage().getWidth();
	}

	/**
	 Gets this Sprite's height, based on the size of the
	 current image.
	 */
	public int getHeight() {
		return anim.getImage().getHeight();
	}

	/**
	 Gets the horizontal velocity of this Sprite in pixels
	 per millisecond.
	 */
	public float getVelocityX() {
		return dx;
	}

	/**
	 Gets the vertical velocity of this Sprite in pixels
	 per millisecond.
	 */
	public float getVelocityY() {
		return dy;
	}

	/**
	 Sets the horizontal velocity of this Sprite in pixels
	 per millisecond.
	 */
	public void setVelocityX(float dx) {
		this.dx = dx;
	}

	/**
	 Sets the vertical velocity of this Sprite in pixels
	 per millisecond.
	 */
	public void setVelocityY(float dy) {
		this.dy = dy;
	}

	/**
	 Gets this Sprite's current image.
	 */
	public Image getImage() {
		return anim.getImage();
	}

	/**
	 Clones this Sprite. Does not clone position or velocity
	 info.
	 */
	public Object clone() {
		return new Sprite(anim);
	}
}
