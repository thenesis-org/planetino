package org.thenesis.planetino2.demo.levels;

import org.thenesis.planetino2.demo.Level;
import org.thenesis.planetino2.demo.ShooterEngine;

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
