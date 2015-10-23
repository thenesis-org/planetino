package org.thenesis.planetino2.sound;

import org.thenesis.planetino2.game.GameObject;
import org.thenesis.planetino2.game.Player;
//import org.thenesis.planetino2.math3D.Vector3D;

public abstract class SoundManager {
	
	public abstract Sound getSound(String string);

	public abstract void play(Sound sound);
	
	public abstract void play(Sound sound, double volume, double pan);
	
	public void play(Sound sound, Player player, GameObject object) {
		float distSq = player.getLocation().getDistanceSq(object.getLocation());
		float maxDist = player.getHearDistance() * player.getHearDistance();
		
		if (distSq < maxDist) {
			double volume = 1.0f - distSq / maxDist;
			
//			float x = -player.getTransform().getSinAngleY();
//			float z = -player.getTransform().getCosAngleY();
//			float cosX = player.getTransform().getCosAngleX();
//			float sinX = player.getTransform().getSinAngleX();
			//Vector3D playerNormal = new Vector3D(cosX * x, sinX, cosX * z)
		
			play(sound, volume, 0.0); // TODO: No panning yet
		}
		

		
	}

}
