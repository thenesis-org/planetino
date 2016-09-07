package org.thenesis.planetino2.demo;

import org.thenesis.planetino2.bsp2D.BSPTree;
import org.thenesis.planetino2.game.CollisionDetection;
import org.thenesis.planetino2.sound.SoundManager;

public interface ShooterEngine {

	public abstract LevelManager getLevelManager();

	public abstract SoundManager getSoundManager();

	public abstract ShooterObjectManager getGameObjectManager();

	public abstract BSPTree getBspTree();

	public abstract CollisionDetection getCollisionDetection();

}