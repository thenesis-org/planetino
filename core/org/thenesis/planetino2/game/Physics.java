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

import org.thenesis.planetino2.math3D.Vector3D;

/**
 The Physics class is a singleton that represents various
 attributes (like gravity) and the functions to manipulate
 objects based on those physical attributes. Currently,
 only gravity and scoot-up (acceleration when
 traveling up stairs) are supported.
 */
public class Physics {

	/**
	 Default gravity in units per millisecond squared
	 */
	public static final float DEFAULT_GRAVITY_ACCEL = -.002f;

	/**
	 Default scoot-up (acceleration traveling up stairs)
	 in units per millisecond squared.
	 */
	public static final float DEFAULT_SCOOT_ACCEL = .006f;

	private static Physics instance;

	private float gravityAccel;
	private float scootAccel;
	private Vector3D velocity = new Vector3D();

	/**
	 Gets the Physics instance. If a Physics instance does
	 not yet exist, one is created with the default attributes.
	 */
	public static synchronized Physics getInstance() {
		if (instance == null) {
			instance = new Physics();
		}
		return instance;
	}

	protected Physics() {
		gravityAccel = DEFAULT_GRAVITY_ACCEL;
		scootAccel = DEFAULT_SCOOT_ACCEL;
	}

	/**
	 Gets the gravity acceleration in units per millisecond
	 squared.
	 */
	public float getGravityAccel() {
		return gravityAccel;
	}

	/**
	 Sets the gravity acceleration in units per millisecond
	 squared.
	 */
	public void setGravityAccel(float gravityAccel) {
		this.gravityAccel = gravityAccel;
	}

	/**
	 Gets the scoot-up acceleration in units per millisecond
	 squared. The scoot up acceleration can be used for
	 smoothly traveling up stairs.
	 */
	public float getScootAccel() {
		return scootAccel;
	}

	/**
	 Sets the scoot-up acceleration in units per millisecond
	 squared. The scoot up acceleration can be used for
	 smoothly traveling up stairs.
	 */
	public void setScootAccel(float scootAccel) {
		this.scootAccel = scootAccel;
	}

	/**
	 Applies gravity to the specified GameObject according
	 to the amount of time that has passed.
	 */
	public void applyGravity(GameObject object, long elapsedTime) {
		velocity.setTo(0, gravityAccel * elapsedTime, 0);
		object.getTransform().addVelocity(velocity);
	}

	/**
	 Applies the scoot-up acceleration to the specified
	 GameObject according to the amount of time that has passed.
	 */
	public void scootUp(GameObject object, long elapsedTime) {
		velocity.setTo(0, scootAccel * elapsedTime, 0);
		object.getTransform().addVelocity(velocity);
	}

	/**
	 Applies the negative scoot-up acceleration to the specified
	 GameObject according to the amount of time that has passed.
	 */
	public void scootDown(GameObject object, long elapsedTime) {
		velocity.setTo(0, -scootAccel * elapsedTime, 0);
		object.getTransform().addVelocity(velocity);
	}

	/**
	 Sets the specified GameObject's vertical velocity to jump
	 to the specified height. Calls getJumpVelocity() to
	 calculate the velocity, which uses the Math.sqrt()
	 function.
	 */
	public void jumpToHeight(GameObject object, float jumpHeight) {
		jump(object, getJumpVelocity(jumpHeight));
	}

	/**
	 Sets the specified GameObject's vertical velocity to the
	 specified jump velocity.
	 */
	public void jump(GameObject object, float jumpVelocity) {
		velocity.setTo(0, jumpVelocity, 0);
		object.getTransform().getVelocity().y = 0;
		object.getTransform().addVelocity(velocity);
	}

	/**
	 Returns the vertical velocity needed to jump the specified
	 height (based on current gravity). Uses the Math.sqrt()
	 function.
	 */
	public float getJumpVelocity(float jumpHeight) {
		// use velocity/acceleration formal: v*v = -2 * a(y-y0)
		// (v is jump velocity, a is accel, y-y0 is max height)
		return (float) Math.sqrt(-2 * gravityAccel * jumpHeight);
	}
}
