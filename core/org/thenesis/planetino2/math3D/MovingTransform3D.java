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
package org.thenesis.planetino2.math3D;

import org.thenesis.planetino2.util.MoreMath;

/**
 A MovingTransform3D is a Transform3D that has a location
 velocity and a angular rotation velocity for rotation around
 the x, y, and z axes.
 */
public class MovingTransform3D extends Transform3D {

	public static final int FOREVER = -1;

	// Vector3D used for calculations
	private static Vector3D temp = new Vector3D();

	// velocity (units per millisecond)
	private Vector3D velocity;
	private Movement velocityMovement;

	// angular velocity (radians per millisecond)
	private Movement velocityAngleX;
	private Movement velocityAngleY;
	private Movement velocityAngleZ;

	/**
	 Creates a new MovingTransform3D
	 */
	public MovingTransform3D() {
		init();
	}

	/**
	 Creates a new MovingTransform3D, using the same values as
	 the specified Transform3D.
	 */
	public MovingTransform3D(Transform3D v) {
		super(v);
		init();
	}

	protected void init() {
		velocity = new Vector3D(0, 0, 0);
		velocityMovement = new Movement();
		velocityAngleX = new Movement();
		velocityAngleY = new Movement();
		velocityAngleZ = new Movement();
	}

	public Object clone() {
		return new MovingTransform3D(this);
	}

	/**
	 Updates this Transform3D based on the specified elapsed
	 time. The location and angles are updated.
	 */
	public void update(long elapsedTime) {
		float delta = velocityMovement.getDistance(elapsedTime);
		if (delta != 0) {
			temp.setTo(velocity);
			temp.multiply(delta);
			location.add(temp);
		}

		rotateAngle(velocityAngleX.getDistance(elapsedTime), velocityAngleY.getDistance(elapsedTime), velocityAngleZ
				.getDistance(elapsedTime));
	}

	/**
	 Stops this Transform3D. Any moving velocities are set to
	 zero.
	 */
	public void stop() {
		velocity.setTo(0, 0, 0);
		velocityMovement.set(0, 0);
		velocityAngleX.set(0, 0);
		velocityAngleY.set(0, 0);
		velocityAngleZ.set(0, 0);
	}

	/**
	 Sets the velocity to move to the following destination
	 at the specified speed.
	 */
	public void moveTo(Vector3D destination, float speed) {
		temp.setTo(destination);
		temp.subtract(location);

		// calc the time needed to move
		float distance = temp.length();
		long time = (long) (distance / speed);

		// normalize the direction vector
		temp.divide(distance);
		temp.multiply(speed);

		setVelocity(temp, time);
	}

	/**
	 Returns true if currently moving.
	 */
	public boolean isMoving() {
		return !velocityMovement.isStopped() && !velocity.equals(0, 0, 0);
	}

	/**
	 Returns true if currently moving, ignoring the y movement.
	 */
	public boolean isMovingIgnoreY() {
		return !velocityMovement.isStopped() && (velocity.x != 0 || velocity.z != 0);
	}

	/**
	 Gets the amount of time remaining for this movement.
	 */
	public long getRemainingMoveTime() {
		if (!isMoving()) {
			return 0;
		} else {
			return velocityMovement.remainingTime;
		}
	}

	/**
	 Gets the velocity vector. If the velocity vector is
	 modified directly, call setVelocity() to ensure the
	 change is recognized.
	 */
	public Vector3D getVelocity() {
		return velocity;
	}

	/**
	 Sets the velocity to the specified vector.
	 */
	public void setVelocity(Vector3D v) {
		setVelocity(v, FOREVER);
	}

	/**
	 Sets the velocity. The velocity is automatically set to
	 zero after the specified amount of time has elapsed. If
	 the specified time is FOREVER, then the velocity is never
	 automatically set to zero.
	 */
	public void setVelocity(Vector3D v, long time) {
		if (velocity != v) {
			velocity.setTo(v);
		}
		if (v.x == 0 && v.y == 0 && v.z == 0) {
			velocityMovement.set(0, 0);
		} else {
			velocityMovement.set(1, time);
		}

	}

	/**
	 Adds the specified velocity to the current velocity. If
	 this MovingTransform3D is currently moving, it's time
	 remaining is not changed. Otherwise, the time remaining
	 is set to FOREVER.
	 */
	public void addVelocity(Vector3D v) {
		if (isMoving()) {
			velocity.add(v);
		} else {
			setVelocity(v);
		}
	}

	/**
	 Turns the x axis to the specified angle with the specified
	 speed.
	 */
	public void turnXTo(float angleDest, float speed) {
		turnTo(velocityAngleX, getAngleX(), angleDest, speed);
	}

	/**
	 Turns the y axis to the specified angle with the specified
	 speed.
	 */
	public void turnYTo(float angleDest, float speed) {
		turnTo(velocityAngleY, getAngleY(), angleDest, speed);
	}

	/**
	 Turns the z axis to the specified angle with the specified
	 speed.
	 */
	public void turnZTo(float angleDest, float speed) {
		turnTo(velocityAngleZ, getAngleZ(), angleDest, speed);
	}

	/**
	 Turns the x axis to face the specified (y,z) vector
	 direction with the specified speed.
	 */
	public void turnXTo(float y, float z, float angleOffset, float speed) {
		turnXTo((float) MoreMath.atan2(-z, y) + angleOffset, speed);
	}

	/**
	 Turns the y axis to face the specified (x,z) vector
	 direction with the specified speed.
	 */
	public void turnYTo(float x, float z, float angleOffset, float speed) {
		turnYTo((float) MoreMath.atan2(-z, x) + angleOffset, speed);
	}

	/**
	 Turns the z axis to face the specified (x,y) vector
	 direction with the specified speed.
	 */
	public void turnZTo(float x, float y, float angleOffset, float speed) {
		turnZTo((float) MoreMath.atan2(y, x) + angleOffset, speed);
	}

	/**
	 Ensures the specified angle is with -pi and pi. Returns
	 the angle, corrected if it is not within these bounds.
	 */
	protected float ensureAngleWithinBounds(float angle) {
		if (angle < -Math.PI || angle > Math.PI) {
			// transform range to (0 to 1)
			double newAngle = (angle + Math.PI) / (2 * Math.PI);
			// validate range
			newAngle = newAngle - Math.floor(newAngle);
			// transform back to (-pi to pi) range
			newAngle = Math.PI * (newAngle * 2 - 1);
			return (float) newAngle;
		}
		return angle;
	}

	/**
	 Turns the movement angle from the startAngle to the
	 endAngle with the specified speed.
	 */
	protected void turnTo(Movement movement, float startAngle, float endAngle, float speed) {
		startAngle = ensureAngleWithinBounds(startAngle);
		endAngle = ensureAngleWithinBounds(endAngle);
		if (startAngle == endAngle) {
			movement.set(0, 0);
		} else {

			float distanceLeft;
			float distanceRight;
			float pi2 = (float) (2 * Math.PI);

			if (startAngle < endAngle) {
				distanceLeft = startAngle - endAngle + pi2;
				distanceRight = endAngle - startAngle;
			} else {
				distanceLeft = startAngle - endAngle;
				distanceRight = endAngle - startAngle + pi2;
			}

			if (distanceLeft < distanceRight) {
				speed = -Math.abs(speed);
				movement.set(speed, (long) (distanceLeft / -speed));
			} else {
				speed = Math.abs(speed);
				movement.set(speed, (long) (distanceRight / speed));
			}
		}
	}

	/**
	 Sets the angular speed of the x axis.
	 */
	public void setAngleVelocityX(float speed) {
		setAngleVelocityX(speed, FOREVER);
	}

	/**
	 Sets the angular speed of the y axis.
	 */
	public void setAngleVelocityY(float speed) {
		setAngleVelocityY(speed, FOREVER);
	}

	/**
	 Sets the angular speed of the z axis.
	 */
	public void setAngleVelocityZ(float speed) {
		setAngleVelocityZ(speed, FOREVER);
	}

	/**
	 Sets the angular speed of the x axis over the specified
	 time.
	 */
	public void setAngleVelocityX(float speed, long time) {
		velocityAngleX.set(speed, time);
	}

	/**
	 Sets the angular speed of the y axis over the specified
	 time.
	 */
	public void setAngleVelocityY(float speed, long time) {
		velocityAngleY.set(speed, time);
	}

	/**
	 Sets the angular speed of the z axis over the specified
	 time.
	 */
	public void setAngleVelocityZ(float speed, long time) {
		velocityAngleZ.set(speed, time);
	}

	/**
	 Sets the angular speed of the x axis over the specified
	 time.
	 */
	public float getAngleVelocityX() {
		return isTurningX() ? velocityAngleX.speed : 0;
	}

	/**
	 Sets the angular speed of the y axis over the specified
	 time.
	 */
	public float getAngleVelocityY() {
		return isTurningY() ? velocityAngleY.speed : 0;
	}

	/**
	 Sets the angular speed of the z axis over the specified
	 time.
	 */
	public float getAngleVelocityZ() {
		return isTurningZ() ? velocityAngleZ.speed : 0;
	}

	/**
	 Returns true if the x axis is currently turning.
	 */
	public boolean isTurningX() {
		return !velocityAngleX.isStopped();
	}

	/**
	 Returns true if the y axis is currently turning.
	 */
	public boolean isTurningY() {
		return !velocityAngleY.isStopped();
	}

	/**
	 Returns true if the z axis is currently turning.
	 */
	public boolean isTurningZ() {
		return !velocityAngleZ.isStopped();
	}

	/**
	 The Movement class contains a speed and an amount of time
	 to continue that speed.
	 */
	protected static class Movement {
		// change per millisecond
		float speed;
		long remainingTime;

		/**
		 Sets this movement to the specified speed and time
		 (in milliseconds).
		 */
		public void set(float speed, long time) {
			this.speed = speed;
			this.remainingTime = time;
		}

		public boolean isStopped() {
			return (speed == 0) || (remainingTime == 0);
		}

		/**
		 Gets the distance traveled in the specified amount of
		 time in milliseconds.
		 */
		public float getDistance(long elapsedTime) {
			if (remainingTime == 0) {
				return 0;
			} else if (remainingTime != FOREVER) {
				elapsedTime = Math.min(elapsedTime, remainingTime);
				remainingTime -= elapsedTime;
			}
			return speed * elapsedTime;
		}
	}
}
