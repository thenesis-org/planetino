package org.thenesis.planetino2.sound;

public class NullSoundPlayer implements SoundPlayer {

	public NullSoundPlayer() {
	}

	public Sound getSound(String soundName) {
		return new SoundSE(soundName);
	}

	
	public org.thenesis.planetino2.sound.Music getMusic(String name) {
		return new MusicSE(name);
	}

	
	private class SoundSE implements Sound {

		public SoundSE(String soundName) {
		}
		
		public void play() {
			// Do nothing
		}

		public void play(double volume, double pan) {
			// Do nothing
		}

	}

	private class MusicSE implements org.thenesis.planetino2.sound.Music {
		
		boolean isLooping = false;

		public MusicSE(String soundName) {
			
		}
		
		public void play(boolean loop) {
			isLooping = true;
		}
		
		public void playAndWait() {
			// Do nothing
		}
		
		public void setVolume(double volume) {
			// Do nothing
		}
		
		public void setPan(double pan) {
			// Do nothing
		}
		
		public void stop() {
			isLooping = false;
		}
		
		public void rewind() {
			// Do nothing
		}
		
		public boolean isPlaying() {
			return isLooping;
		}

	}

	public void close() {
		// Do nothing
	}

	public void init() {
		// Do nothing
	}

}
