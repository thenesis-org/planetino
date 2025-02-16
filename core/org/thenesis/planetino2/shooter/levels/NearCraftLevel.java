package org.thenesis.planetino2.shooter.levels;

import org.thenesis.planetino2.shooter.Level;
import org.thenesis.planetino2.shooter.ShooterEngine;

public class NearCraftLevel extends Level {
	
	public NearCraftLevel(ShooterEngine engine) {
		super(engine);
	}

	public String getAmbientMusicName() {
		return "bell_loop-small.wav";
	}

	public String getMapName() {
		return "NearCraft.map";
	}
	
}
