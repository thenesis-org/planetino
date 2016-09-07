package org.thenesis.planetino2.demo;

import org.thenesis.planetino2.ai.AIBot;
import org.thenesis.planetino2.game.GameObject;
import org.thenesis.planetino2.math3D.PolygonGroup;
import org.thenesis.planetino2.math3D.Vector3D;

public class CatchableGameObject extends GameObject {
	
	private boolean flying = false;
	private boolean stopVelocity = false;
	private boolean reduceVelocity = false;

	public CatchableGameObject(PolygonGroup polygonGroup) {
		super(polygonGroup);
	}

	@Override
	public boolean isFlying() {
		return flying;
	}
	
	public void setFlying(boolean flying) {
		this.flying = flying;
	}

	@Override
	public void notifyFloorCollision() {
		setJumping(false);
		stopVelocity = true;
	}

	@Override
	public void notifyCeilingCollision() {
		super.notifyCeilingCollision();
		reduceVelocity = true;
	}

	@Override
	public void notifyWallCollision() {
		super.notifyWallCollision();
		reduceVelocity = true;
	}
	
	@Override
	public void notifyObjectCollision(GameObject otherObject) {
		super.notifyObjectCollision(otherObject);
		
		reduceVelocity = true;
		
		// On impact, opponents are killed
		if (otherObject instanceof AIBot) {
			AIBot bot = (AIBot)otherObject;
			
//			// Move a bit (FIXME: doesn't work)
//			Vector3D velocity = new Vector3D(getTransform().getVelocity());
//			velocity.divide(2);
//			velocity.y = 0;
//			bot.getTransform().addVelocity(velocity);
			
			// Opponents are killed
			bot.addHealth(-bot.getMaxHealth());
		}
		
	}

	@Override
	public void update(GameObject player, long elapsedTime) {
		super.update(player, elapsedTime);
		
		if (reduceVelocity) {
			reduceVelocity(elapsedTime);
			reduceVelocity = false;
		}
		if (stopVelocity) {
			reduceVelocity(elapsedTime);
			Vector3D velocity = getTransform().getVelocity();
			if (velocity.length() == 0) {
				stopVelocity = false;
			}
		}
		
	}
	
	private void reduceVelocity(long elapsedTime) {
		Vector3D velocity = getTransform().getVelocity();
		velocity.divide(1 + 0.004f * elapsedTime);
		if (velocity.length() < 0.1f) {
			velocity.setTo(0, 0, 0);
		}
		getTransform().setVelocity(velocity);
	}

	
	
	
	
}
