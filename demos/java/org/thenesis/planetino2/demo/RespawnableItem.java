package org.thenesis.planetino2.demo;

import org.thenesis.planetino2.game.GameObject;
import org.thenesis.planetino2.math3D.PolygonGroup;

public class RespawnableItem extends GameObject {

	private long elapsedTimeInDestroyedState;

	public RespawnableItem(PolygonGroup polygonGroup) {
		super(polygonGroup);
	}
	
	@Override
	public void update(GameObject player, long elapsedTime) {
		if (isDestroyed()) {
			if (elapsedTimeInDestroyedState > 20000) {
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
