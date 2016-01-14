package org.thenesis.planetino2.demo;

import org.thenesis.planetino2.util.Vector;

import org.thenesis.planetino2.game.CollisionDetection;
import org.thenesis.planetino2.game.GameObject;
import org.thenesis.planetino2.game.GridGameObjectManager;
import org.thenesis.planetino2.math3D.Rectangle;

public class ShooterObjectManager extends GridGameObjectManager {
	
	private Vector gameItems = new Vector();
	private Vector enemies = new Vector();
	private Vector destroyedEnnemies = new Vector();

	public ShooterObjectManager(Rectangle mapBounds, CollisionDetection collisionDetection) {
		super(mapBounds, collisionDetection);
	}
	
	public void addRespawnableItem(RespawnableItem item) {
		gameItems.addElement(item);
		super.add(item);
	}
	
	public void addEnnemy(GameObject gameObject) {
		enemies.addElement(gameObject);
		super.add(gameObject);
	}
	
	public int getAliveEnemyCount() {
		return enemies.size();
	}
	
	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		
		// Items
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
		
		// Enemies
		destroyedEnnemies.clear();
		size = enemies.size();
		for (int i = 0; i < size; i++) {
			GameObject ennemy = (GameObject)enemies.elementAt(i);
			if(ennemy.isDestroyed()) {
				destroyedEnnemies.addElement(ennemy);
			}
		}
		size = destroyedEnnemies.size();
		for (int i = 0; i < size; i++) {
			enemies.removeElement(destroyedEnnemies.elementAt(i));
		}
	}
	
	

}
