package org.thenesis.planetino2.ai;

import org.thenesis.planetino2.game.CollisionDetection;
import org.thenesis.planetino2.game.GameObject;
import org.thenesis.planetino2.game.Player;
import org.thenesis.planetino2.math3D.PolygonGroup;
import org.thenesis.planetino2.sound.Music;
import org.thenesis.planetino2.sound.SoundManager;

public class NoisyAIBot extends AIBot {
	
	private static final String BOT_SOUND = "drown.wav";
	private static final String DEATH_SOUND = "death3.wav";
	private static final double MAX_SOUND_LEVEL = 0.2;
	private SoundManager soundManager;
	private Music soundLoop;
	private Music deathSound;
	private double soundLevel;

	public NoisyAIBot(SoundManager soundManager, PolygonGroup polygonGroup, CollisionDetection collisionDetection, Brain brain, PolygonGroup blastModel) {
		super(polygonGroup, collisionDetection, brain, blastModel);
		this.soundManager = soundManager;
		soundLoop = soundManager.getMusic(BOT_SOUND);
		deathSound = soundManager.getMusic(DEATH_SOUND);
		soundLevel = MAX_SOUND_LEVEL;
	}
	
	public void update(GameObject player, long elapsedTime) {
		super.update(player, elapsedTime);
		if (getAiState() == WOUNDED_STATE_DEAD) {
			soundLoop.stop();
			deathSound.play(false);
		} else {
			soundManager.updateVolumeAndPan(soundLoop, (Player)player, this, soundLevel);
		}
	}

//	@Override
//	public void notifyVisible(boolean visible) {
//		super.notifyVisible(visible);
//		
//		System.out.println("visible=" + visible);
//		
//		if (visible) {
//			soundLevel = MAX_SOUND_LEVEL;
//		} else {
//			soundLevel = MAX_SOUND_LEVEL / 3;
//		}
//		
//	}
	
	
}
