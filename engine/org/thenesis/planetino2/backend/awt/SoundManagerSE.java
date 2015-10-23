package org.thenesis.planetino2.backend.awt;

import kuusisto.tinysound.TinySound;

import org.thenesis.planetino2.sound.Sound;
import org.thenesis.planetino2.sound.SoundManager;

public class SoundManagerSE extends SoundManager {
	
	public SoundManagerSE() {
		TinySound.init();
	}

	public Sound getSound(String soundName) {
		kuusisto.tinysound.Sound sound = TinySound.loadSound("/res/" + soundName);
		return new SoundSE(sound);
	}

	public void play(Sound sound) {
		((SoundSE)sound).nativeSound.play();
	}
	
	private class SoundSE implements Sound {
		
		private kuusisto.tinysound.Sound nativeSound;	
		
		public SoundSE(kuusisto.tinysound.Sound sound) {
			SoundSE.this.nativeSound = sound;
		}
		
	}

	public void play(Sound sound, double volume, double pan) {
		((SoundSE)sound).nativeSound.play(volume, pan);
	}

}
