package org.thenesis.planetino2.demo.levels;

import org.thenesis.planetino2.demo.Level;
import org.thenesis.planetino2.demo.ShooterEngine;

public class QuakeLevel extends Level {

	public QuakeLevel(ShooterEngine engine) {
		super(engine);
	}

	public String getMapName() {
		return "quake-one_bot.map";
	}
	
}
