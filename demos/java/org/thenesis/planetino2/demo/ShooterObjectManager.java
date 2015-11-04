package org.thenesis.planetino2.demo;

import java.util.Vector;

import org.thenesis.planetino2.game.CollisionDetection;
import org.thenesis.planetino2.game.GridGameObjectManager;
import org.thenesis.planetino2.math3D.Rectangle;

public class ShooterObjectManager extends GridGameObjectManager {
	
	private Vector gameItems = new Vector();

	public ShooterObjectManager(Rectangle mapBounds, CollisionDetection collisionDetection) {
		super(mapBounds, collisionDetection);
	}
	
	public void addRespawnableItem(RespawnableItem item) {
		gameItems.add(item);
		super.add(item);
	}
	
	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		
		int size = gameItems.size();
		for (int i = 0; i < size; i++) {
			RespawnableItem item = (RespawnableItem)gameItems.elementAt(i);
			if(item.isDestroyed()) {
				item.update(getPlayer(), elapsedTime);
				if (item.isActive()) {
					add(item);
				}
			}
		}
		
	}
	
	

}
