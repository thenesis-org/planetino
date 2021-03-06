package org.thenesis.planetino2.backend.awt;

import kuusisto.tinysound.Music;
import kuusisto.tinysound.TinySound;

import org.thenesis.planetino2.sound.Sound;
import org.thenesis.planetino2.sound.SoundPlayer;

public class SoundPlayerSE implements SoundPlayer {

	public SoundPlayerSE() {
	}

	public Sound getSound(String soundName) {
		return new SoundSE(soundName);
	}
	
	public org.thenesis.planetino2.sound.Music getMusic(String name) {
		return new MusicSE(name);
	}
	
	private class SoundSE implements Sound {

		private kuusisto.tinysound.Sound nativeSound;

		public SoundSE(String soundName) {
			String dir = ResourceLoaderSE.getResourceDirectory();
			SoundSE.this.nativeSound = TinySound.loadSound(dir + soundName);
		}
		
		public void play() {
			nativeSound.play();
		}

		public void play(double volume, double pan) {
			nativeSound.play(volume, pan);
		}
		
		public void stop() {
			nativeSound.stop();
		}

	}

	private class MusicSE implements org.thenesis.planetino2.sound.Music {

		private Music nativeMusic;

		public MusicSE(String soundName) {
			String dir = ResourceLoaderSE.getResourceDirectory();
			MusicSE.this.nativeMusic = TinySound.loadMusic(dir + soundName);
		}
		
		public void play(boolean loop) {
//			if (!nativeMusic.playing()) {
//				nativeMusic.rewind();
//			}
			nativeMusic.play(loop);
		}
		
		public void playAndWait() {
			nativeMusic.play(false);
			try {
				while (nativeMusic.playing()) {
					Thread.sleep(1);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		public void setVolume(double volume) {
			nativeMusic.setVolume(volume);
		}
		
		public void setPan(double pan) {
			nativeMusic.setPan(pan);
		}
		
		public void stop() {
			nativeMusic.stop();
		}
		
		public void rewind() {
			nativeMusic.rewind();
		}
		
		public boolean isPlaying() {
			return nativeMusic.playing();
		}

	}

	public void close() {
		TinySound.shutdown();
	}

	public void init() {
		TinySound.init();
		
	}

}
