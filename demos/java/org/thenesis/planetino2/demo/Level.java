package org.thenesis.planetino2.demo;

import org.thenesis.planetino2.sound.Music;
import org.thenesis.planetino2.sound.SoundManager;

public abstract class Level {
	
	public String getIntroSoundName() {
		return "prepare.wav";
	}
	
	public String getAmbientMusicName() {
		return "ambient_loop.wav";
	}
	
	public String getDeathSoundName() {
		return "death3_player.wav";
	}
	
	public String getLostSoundName() {
		return "youlose.wav";
	}
	
	public String getWinSoundName() {
		return "youwin.wav";
	}
	
	public String getEndMusicName() {
		return "OA07.wav";
	}
	
	public abstract String getMapName();
	
	public void checkGameState(LevelManager levelManager, ShooterObjectManager gameObjectManager, SoundManager soundManager) {
		ShooterPlayer player = (ShooterPlayer)gameObjectManager.getPlayer();
		if (player.getHealth() <= 0) {
			try {
				Music deathSound = soundManager.getMusic(getDeathSoundName());
				deathSound.playAndWait();
				Thread.sleep(2000);
				
				Music lostSound = soundManager.getMusic(getLostSoundName());
				lostSound.playAndWait();
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
			levelManager.changeLevel();
		} else if (gameObjectManager.getAliveEnemyCount() == 0) {
			try {
				Music winSound = soundManager.getMusic(getWinSoundName());
				winSound.playAndWait();
				Thread.sleep(500);
				Music winMusic = soundManager.getMusic(getEndMusicName());
				winMusic.play(false);
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}
			levelManager.changeLevel();
		}
	}

}
