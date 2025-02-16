package org.thenesis.planetino2.shooter;

import org.thenesis.planetino2.game.GameObject;
import org.thenesis.planetino2.math3D.PolygonGroup;
import org.thenesis.planetino2.sound.Music;
import org.thenesis.planetino2.sound.SoundManager;

public abstract class Level {
	
	protected ShooterEngine engine;
	
	protected Level(ShooterEngine engine) {
		this.engine = engine;
	}
	
	public void initialize() {
		// Start music of the current level
		Music ambientMusic = engine.getSoundManager().getMusic(getAmbientMusicName());
		ambientMusic.setVolume(getAmbientMusicLevel());
		ambientMusic.play(true);
		Music introMusic = engine.getSoundManager().getMusic(getIntroSoundName());
		introMusic.play(false);
	}
	
	public String getIntroSoundName() {
		return "prepare.wav";
	}
	
	public String getAmbientMusicName() {
		return "ambient_loop.wav";
	}
	
	public float getAmbientMusicLevel() {
		return 0.3f;
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
	
	public void checkGameState() {
		
		LevelManager levelManager = engine.getLevelManager();
		ShooterObjectManager gameObjectManager = engine.getGameObjectManager();
		SoundManager soundManager = engine.getSoundManager();
		
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

	public GameObject createGameObject(PolygonGroup polygonGroup) {
		// Do nothing by default
		return null;
	}

}
