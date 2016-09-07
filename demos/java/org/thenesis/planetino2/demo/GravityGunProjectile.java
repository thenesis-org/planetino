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

package org.thenesis.planetino2.demo;

import org.thenesis.planetino2.game.CatchableGameObject;
import org.thenesis.planetino2.game.GameObject;
import org.thenesis.planetino2.math3D.MovingTransform3D;
import org.thenesis.planetino2.math3D.PolygonGroup;
import org.thenesis.planetino2.math3D.Vector3D;

/**
 * The Blast GameObject is a projectile, designed to travel in a straight line
 * for five seconds, then die. Blasts destroy Bots instantly.
 */
public class GravityGunProjectile extends GameObject {

	private static final long DIE_TIME = 2000;
	private static final float SPEED = 1.5f;
	private static final float ROT_SPEED = .008f;

	private MovingTransform3D transform;
	private long aliveTime;
	private ShooterPlayer player;

	/**
	 * Create a new Blast with the specified PolygonGroup and normalized vector
	 * direction.
	 */
	public GravityGunProjectile(PolygonGroup polygonGroup, ShooterPlayer player, Vector3D direction) {
		super(polygonGroup);
		this.player = player;
		transform = getTransform();
		Vector3D velocity = transform.getVelocity();
		velocity.setTo(direction);
		velocity.multiply(SPEED);
		transform.setVelocity(velocity);
		//transform.setAngleVelocityX(ROT_SPEED);
		//transform.setAngleVelocityY(ROT_SPEED);
		//transform.setAngleVelocityZ(ROT_SPEED);
		setState(STATE_ACTIVE);
	}

	public void update(GameObject player, long elapsedTime) {
		aliveTime += elapsedTime;
		if (aliveTime >= DIE_TIME) {
			setState(STATE_DESTROYED);
		} else {
			super.update(player, elapsedTime);
		}
	}

	public boolean isFlying() {
		return true;
	}

	public void notifyObjectCollision(GameObject object) {
		if (object instanceof CatchableGameObject) {
			player.attachToGravityGun((CatchableGameObject)object);
		}
		setState(STATE_DESTROYED);
	}

	public void notifyWallCollision() {
		getTransform().stop();
		setState(STATE_DESTROYED);
	}

	public void notifyFloorCollision() {
		//notifyWallCollision();
	}

	public void notifyCeilingCollision() {
		notifyWallCollision();
	}
}
