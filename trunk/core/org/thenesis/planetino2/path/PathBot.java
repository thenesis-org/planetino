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
package org.thenesis.planetino2.path;

import java.util.Enumeration;

import org.thenesis.planetino2.game.GameObject;
import org.thenesis.planetino2.math3D.PolygonGroup;
import org.thenesis.planetino2.math3D.Vector3D;
import org.thenesis.planetino2.util.MoreMath;

/**
 A PathBot is a GameObject that follows a path from a
 PathFinder.
 */
public class PathBot extends GameObject {

	private static final float DEFAULT_TURN_SPEED = .005f;
	private static final float DEFAULT_SPEED = .25f;
	private static final long DEFAULT_PATH_RECALC_TIME = 4000;
	private static final float DEFAULT_FLY_HEIGHT = 64;

	protected PathFinder pathFinder;
	protected Enumeration currentPath;
	private Vector3D nextPathLocation;
	protected long timeUntilPathRecalc;
	private long pathRecalcTime;
	private Vector3D facing;

	private float turnSpeed;
	private float speed;
	private float flyHeight;

	public PathBot(PolygonGroup polygonGroup) {
		super(polygonGroup);
		nextPathLocation = new Vector3D();

		// set default values
		setPathRecalcTime(DEFAULT_PATH_RECALC_TIME);
		setSpeed(DEFAULT_SPEED);
		setTurnSpeed(DEFAULT_TURN_SPEED);
		setFlyHeight(DEFAULT_FLY_HEIGHT);
		setState(STATE_ACTIVE);
	}

	/**
	 Sets the location this object should face as it follows
	 the path. This value can change. If null, the this object
	 faces the direction it is moving.
	 */
	public void setFacing(Vector3D facing) {
		this.facing = facing;
	}

	/**
	 Sets the PathFinder class to use to follow the path.
	 */
	public void setPathFinder(PathFinder pathFinder) {
		if (this.pathFinder != pathFinder) {
			this.pathFinder = pathFinder;
			currentPath = null;

			// random amount of time until calulation, so
			// not all bot calc the path at the same time
			timeUntilPathRecalc = (long) (MoreMath.random() * 1000);
		}
	}

	public void setPathRecalcTime(long pathRecalcTime) {
		this.pathRecalcTime = pathRecalcTime;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public void setTurnSpeed(float turnSpeed) {
		this.turnSpeed = turnSpeed;
	}

	public void setFlyHeight(float flyHeight) {
		getTransform().getLocation().y += flyHeight - this.flyHeight;
		this.flyHeight = flyHeight;
	}

	public float getFlyHeight() {
		return flyHeight;
	}

	public void update(GameObject player, long elapsedTime) {

		//System.out.println("[DEBUG] PathBot.update()");

		if (pathFinder == null) {
			super.update(player, elapsedTime);
			return;
		}

		//System.out.println("[DEBUG] PathBot.update(): process path");

		timeUntilPathRecalc -= elapsedTime;

		// updtate the path to the player
		if (timeUntilPathRecalc <= 0) {
			currentPath = pathFinder.find(this, player);
			if (currentPath != null) {
				getTransform().stop();
			}
			timeUntilPathRecalc = pathRecalcTime;
		}

		// follow the path
		if (currentPath != null && !getTransform().isMovingIgnoreY()) {
			if (currentPath.hasMoreElements()) {
				nextPathLocation.setTo((Vector3D) currentPath.nextElement());
				nextPathLocation.y += flyHeight;
				getTransform().moveTo(nextPathLocation, speed);

				Vector3D faceLocation = facing;
				if (faceLocation == null) {
					faceLocation = nextPathLocation;
				}
				getTransform().turnYTo(faceLocation.x - getX(), faceLocation.z - getZ(), (float) -Math.PI / 2,
						turnSpeed);
			} else {
				currentPath = null;
				notifyEndOfPath();
			}

		}

		super.update(player, elapsedTime);
	}

	/**
	 When a collision occurs, back up for 200 ms and then
	 wait a few seconds before recaculating the path.
	 */
	protected void backupAndRecomputePath() {
		// back up for 200 ms
		nextPathLocation.setTo(getTransform().getVelocity());
		if (!isFlying()) {
			nextPathLocation.y = 0;
		}
		nextPathLocation.multiply(-1);
		getTransform().setVelocity(nextPathLocation, 200);

		// wait until computing the path again
		currentPath = null;
		timeUntilPathRecalc = (long) (MoreMath.random() * 1000);
	}

	public boolean isFlying() {
		return (flyHeight > 0);
	}

	public void notifyEndOfPath() {
		// do nothing
	}

	public void notifyWallCollision() {
		backupAndRecomputePath();
	}

	public void notifyObjectCollision(GameObject object) {
		backupAndRecomputePath();
	}

}
