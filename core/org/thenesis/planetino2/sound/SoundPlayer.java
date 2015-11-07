package org.thenesis.planetino2.sound;

public interface SoundPlayer {
	
	public void init();
	
	public Music getMusic(String name);
	
	public Sound getSound(String name);

	public void close();

}
