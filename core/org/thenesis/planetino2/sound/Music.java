package org.thenesis.planetino2.sound;

public interface Music {
	
	public void play(boolean loop);
	
	public void playAndWait();
	
	public void setVolume(double volume);
	
	public void setPan(double volume);

	public void stop();
	
	public void rewind();

	public boolean isPlaying();

}
