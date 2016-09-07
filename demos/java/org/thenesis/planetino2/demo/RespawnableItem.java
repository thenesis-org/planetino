package org.thenesis.planetino2.demo;

import org.thenesis.planetino2.game.GameObject;
import org.thenesis.planetino2.math3D.PolygonGroup;
import org.thenesis.planetino2.math3D.Vector3D;

public class RespawnableItem extends GameObject {

	private static final int DEFAULT_RESPAWN_TIME = 20000;
	private long elapsedTimeInDestroyedState;
	private Vector3D initialLocation;

	public RespawnableItem(PolygonGroup polygonGroup) {
		super(polygonGroup);
		initialLocation = new Vector3D(polygonGroup.getTransform().getLocation());
	}
	
	@Override
	public void update(GameObject player, long elapsedTime) {
		if (isDestroyed()) {
			if (elapsedTimeInDestroyedState > DEFAULT_RESPAWN_TIME) {
				getLocation().setTo(initialLocation);
				setState(STATE_ACTIVE);
				elapsedTimeInDestroyedState = 0;
				addSpawn(this);
			} else {
				elapsedTimeInDestroyedState += elapsedTime;
			}
		} else {
			super.update(player, elapsedTime);
		}
	}
	
	
	

}
