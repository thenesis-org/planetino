package org.thenesis.planetino2.game;

import org.thenesis.planetino2.sound.Music;

public interface NoisyObject {

	public double getMaxSoundLevel();

	public Music getSoundLoop();

	public void setSoundLoop(String name);

}