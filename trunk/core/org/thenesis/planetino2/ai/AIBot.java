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
package org.thenesis.planetino2.ai;

import org.thenesis.planetino2.ai.pattern.AimPattern;
import org.thenesis.planetino2.game.CollisionDetection;
import org.thenesis.planetino2.game.GameObject;
import org.thenesis.planetino2.game.MessageQueue;
import org.thenesis.planetino2.game.Physics;
import org.thenesis.planetino2.math3D.PolygonGroup;
import org.thenesis.planetino2.math3D.Vector3D;
import org.thenesis.planetino2.path.PathBot;
import org.thenesis.planetino2.path.PathFinder;
import org.thenesis.planetino2.util.MoreMath;

public class AIBot extends PathBot {

	public static final int NORMAL_STATE_IDLE = 0;
	public static final int NORMAL_STATE_PATROL = 1;
	public static final int NORMAL_STATE_CHASE = 2;
	public static final int BATTLE_STATE_ATTACK = 3;
	public static final int BATTLE_STATE_DODGE = 4;
	public static final int BATTLE_STATE_RUN_AWAY = 5;
	public static final int WOUNDED_STATE_HURT = 6;
	public static final int WOUNDED_STATE_DEAD = 7;

	public static final int DECESION_READY = 9;

	private static final float DEFAULT_MAX_HEALTH = 100;
	private static final float CRITICAL_HEALTH_PERCENT = 5;

	private float maxHealth;
	private float health;
	private int aiState;
	private long elapsedTimeInState;
	private long elapsedTimeSinceDecision;
	private long timeSincePlayerLastSeen;
	private Vector3D startLocation;
	private boolean isRegenerating;

	private PolygonGroup blastModel;
	private CollisionDetection collisionDetection;
	protected Brain brain;

	// for displaying debug info only
	private boolean lastVisible;

	public AIBot(PolygonGroup polygonGroup, CollisionDetection collisionDetection, Brain brain, PolygonGroup blastModel) {
		super(polygonGroup);
		this.collisionDetection = collisionDetection;
		this.brain = brain;
		this.blastModel = blastModel;

		// random time until decision
		elapsedTimeSinceDecision = MoreMath.random((int) brain.decisionTime);
		maxHealth = DEFAULT_MAX_HEALTH;
		setHealth(maxHealth);
		aiState = NORMAL_STATE_IDLE;
		timeSincePlayerLastSeen = 10000;
	}

	public float getHealth() {
		return health;
	}

	public float getMaxHealth() {
		return maxHealth;
	}

	protected void setHealth(float health) {
		this.health = health;
	}

	/**
	 Adds the specified amount to this bot's health. If the
	 amount is less than zero, the bot's state is set to
	 WOUNDED_STATE_HURT.
	 */
	public void addHealth(float amount) {
		//System.out.println("[DEBUG] AIBot.addHealth(): " + amount);
		if (amount < 0) {
			if (health <= 0 || aiState == WOUNDED_STATE_HURT) {
				return;
			}
			MessageQueue.getInstance().debug(getName() + " hit");
			setAiState(WOUNDED_STATE_HURT, null);
			// make a decison in three seconds
			elapsedTimeSinceDecision = brain.decisionTime - 3000;
		}
		setHealth(health + amount);
	}

	/**
	 Returns true if the health is critically low (less than
	 CRITICAL_HEALTH_PERCENT).
	 */
	public boolean isCriticalHealth() {
		return (health / maxHealth < CRITICAL_HEALTH_PERCENT / 100);
	}

	/**
	 Gets the AI state for this bot (different frm the
	 GameObject state).
	 */
	public int getAiState() {
		return aiState;
	}

	/**
	 Sets the AI state for this bot (different from the
	 GameObject state).
	 */
	protected void setAiState(int aiState, GameObject player) {
		if (this.aiState == aiState) {
			return;
		}

		this.aiState = aiState;

		elapsedTimeInState = 0;
		PathFinder lastPattern = pathFinder;
		Vector3D playerLocation = null;
		if (player != null) {
			playerLocation = player.getLocation();
		}

		// update path
		switch (aiState) {
		case NORMAL_STATE_IDLE:
		case NORMAL_STATE_PATROL:
			setPathFinder(brain.idlePathFinder);
			setFacing(null);
			break;
		case NORMAL_STATE_CHASE:
			setPathFinder(brain.chasePathFinder);
			setFacing(null);
			break;
		case BATTLE_STATE_ATTACK:
			setPathFinder(brain.attackPathFinder);
			setFacing(playerLocation);
			break;
		case BATTLE_STATE_DODGE:
			setPathFinder(brain.dodgePathFinder);
			setFacing(null);
			break;
		case BATTLE_STATE_RUN_AWAY:
			setPathFinder(brain.runAwayPathFinder);
			setFacing(null);
			break;
		case WOUNDED_STATE_HURT:
			setPathFinder(null);
			setFacing(null);
			getTransform().stop();
			getTransform().setAngleVelocityY(MoreMath.random(0.001f, 0.05f), MoreMath.random(100, 500));
			break;
		case WOUNDED_STATE_DEAD:
			setPathFinder(null);
			setFacing(null);
			setJumping(true);
			getTransform().stop();
			Physics.getInstance().jumpToHeight(this, 16);
			getTransform().setAngleVelocityY(MoreMath.random(0.001f, 0.05f), MoreMath.random(100, 500));
			break;
		default:
			setPathFinder(null);
			setFacing(null);
			getTransform().stop();
			break;
		}

		if (lastPattern != pathFinder) {
			MessageQueue.getInstance().debug(getName() + " pattern: " + pathFinder);
		}
	}

	/**
	 Returns true if this bot regenerates after it dies.
	 */
	public boolean isRegenerating() {
		return isRegenerating;
	}

	/**
	 Sets whether this bot regenerates after it dies.
	 */
	public void setRegenerating(boolean isRegenerating) {
		this.isRegenerating = isRegenerating;
	}

	/**
	 Sets the PathFinder class to use to follow the path.
	 */
	public void setPathFinder(PathFinder pathFinder) {
		if (this.pathFinder != pathFinder) {
			super.setPathFinder(pathFinder);
			timeUntilPathRecalc = 0;
		}
	}

	/**
	 Causes this bot to regenerate, restoring its location
	 to its start location.
	 */
	protected void regenerate() {
		setHealth(maxHealth);
		setState(STATE_ACTIVE);
		setAiState(DECESION_READY, null);
		getLocation().setTo(startLocation);
		getTransform().stop();
		setJumping(false);
		setPathFinder(null);
		setFacing(null);
		lastVisible = false;
		timeSincePlayerLastSeen = 10000;
		// let the game object manager know this object regenerated
		// (so collision detection from old location to new
		// location won't be performed)
		addSpawn(this);
	}

	public void update(GameObject player, long elapsedTime) {
		updateHelper(player, elapsedTime);
		super.update(player, elapsedTime);
	}

	public void updateHelper(GameObject player, long elapsedTime) {

		elapsedTimeSinceDecision += elapsedTime;
		elapsedTimeInState += elapsedTime;
		timeSincePlayerLastSeen += elapsedTime;

		// record first location
		if (startLocation == null) {
			startLocation = new Vector3D(getLocation());
		}

		// regenerate if dead for 5 seconds
		if (aiState == WOUNDED_STATE_DEAD) {
			if (elapsedTimeInState >= 5000) {
				if (isRegenerating()) {
					regenerate();
				} else {
					setState(STATE_DESTROYED);
				}
			}
			return;
		}

		else if (aiState == WOUNDED_STATE_HURT) {
			if (elapsedTimeInState >= 500) {
				if (health <= 0) {
					setAiState(WOUNDED_STATE_DEAD, player);
					return;
				} else {
					aiState = DECESION_READY;
				}
			} else {
				return;
			}
		}

		// run away if health critical
		if (isCriticalHealth() && brain.runAwayPathFinder != null) {
			setAiState(BATTLE_STATE_RUN_AWAY, player);
			return;
		}

		// if idle and player visible, make decision every 500 ms
		if ((aiState == NORMAL_STATE_IDLE || aiState == NORMAL_STATE_PATROL) && elapsedTimeInState >= 500) {
			aiState = DECESION_READY;
		}

		// if time's up, make decision
		else if (elapsedTimeSinceDecision >= brain.decisionTime) {
			aiState = DECESION_READY;
		}

		// if done with current path, make decision
		else if (currentPath != null && !currentPath.hasMoreElements() && !getTransform().isMovingIgnoreY()) {
			aiState = DECESION_READY;
		}

		// make a new decision
		if (aiState == DECESION_READY) {
			elapsedTimeSinceDecision = 0;

			if (canSee(player)) {
				setAiState(chooseBattleState(), player);
			} else if (timeSincePlayerLastSeen < 3000 || canHear(player)) {
				setAiState(NORMAL_STATE_CHASE, player);
			} else {
				setAiState(NORMAL_STATE_IDLE, player);
			}
		}
		// fire projectile
		else if (aiState == BATTLE_STATE_ATTACK && elapsedTimeInState >= brain.aimTime && brain.aimPathFinder != null) {
			elapsedTimeInState -= brain.aimTime;

			// longer aim time == more accuracy
			float p = Math.min(1, brain.aimTime / 2000f);
			((AimPattern) brain.aimPathFinder).setAccuracy(p);
			Vector3D direction = (Vector3D) brain.aimPathFinder.find(this, player).nextElement();
			fireProjectile(direction);
		}

	}

	/**
	 Fires a projectile in the specified direction. The
	 direction vector should be normalized.
	 */
	public void fireProjectile(Vector3D direction) {

		//System.out.println("[DEBUG] AIBot.fireProjectile()");
		Projectile blast = new Projectile((PolygonGroup) blastModel.clone(), direction, this, 3, 6);
		float dist = 2 * (getBounds().getRadius() + blast.getBounds().getRadius());
		blast.getLocation().setTo(getX() + direction.x * dist, getY() + getBounds().getTopHeight() / 2,
				getZ() + direction.z * dist);

		// "spawns" the new game object
		addSpawn(blast);

		// make a "virtual" noise that bots can "hear"
		// (500 milliseconds)
		makeNoise(500);
	}

	public int chooseBattleState() {
		float p = (float) MoreMath.random();
		if (p <= brain.attackProbability) {
			return BATTLE_STATE_ATTACK;
		} else if (p <= brain.attackProbability + brain.dodgeProbability) {
			return BATTLE_STATE_DODGE;
		} else {
			return BATTLE_STATE_RUN_AWAY;
		}
	}

	/**
	 Checks if this object can see the specified object,
	 (assuming this object has eyes in the back of its head).
	 */
	public boolean canSee(GameObject object) {
		// check if a line from this bot to the object
		// hits any walls
		boolean visible = (collisionDetection.getFirstWallIntersection(getX(), getZ(), object.getX(), object.getZ(),
				getY(), getY() + 1) == null);

		if (visible) {
			timeSincePlayerLastSeen = 0;
		}

		// display debug message
		if (visible != lastVisible) {
			String message = visible ? " sees " : " no longer sees ";
			MessageQueue.getInstance().debug(getName() + message + object.getName());
			lastVisible = visible;
		}

		return visible;
	}

	/**
	 Checks if this object can hear the specified object. The
	 specified object must be making a noise and be within
	 hearing range of this object.
	 */
	public boolean canHear(GameObject object) {

		// check if object is making noise and this bot is not deaf
		if (!object.isMakingNoise() || brain.hearDistance == 0) {
			return false;
		}

		// check if this bot is close enough to hear the noise
		float distSq = getLocation().getDistanceSq(object.getLocation());
		float hearDistSq = brain.hearDistance * brain.hearDistance;
		boolean heard = (distSq <= hearDistSq);

		// display debug message
		if (heard) {
			MessageQueue.getInstance().debug(getName() + " hears " + object.getName());
		}

		return heard;
	}

	public boolean isFlying() {
		return (super.isFlying() && aiState != WOUNDED_STATE_DEAD);
	}

	public void notifyEndOfPath() {
		if (aiState != BATTLE_STATE_ATTACK) {
			setAiState(DECESION_READY, null);
		}
	}

	public void notifyHitPlayer(long damage) {
		// do nothing
	}

	public void notifyWallCollision() {
		if (aiState == BATTLE_STATE_RUN_AWAY) {
			getTransform().setVelocity(new Vector3D(0, 0, 0));
			setAiState(DECESION_READY, null);
		} else {
			super.notifyWallCollision();
		}
	}
}
