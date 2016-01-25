package org.thenesis.planetino2.sound;

import org.thenesis.planetino2.game.GameObject;
import org.thenesis.planetino2.game.Player;
import org.thenesis.planetino2.graphics.Toolkit;
import org.thenesis.planetino2.math3D.Transform3D;
import org.thenesis.planetino2.math3D.ViewWindow;
import org.thenesis.planetino2.math3D.Vector3D;

public class SoundManager {
	
	private SoundPlayer player;
	private ViewWindow viewWindow;
	private Transform3D camera;
	
	public SoundManager(ViewWindow viewWindow, Transform3D camera) {
		player = Toolkit.getInstance().getSoundPlayer();
		this.viewWindow = viewWindow;
		this.camera = camera;
	}
	
	public Sound getSound(String name) {
		return player.getSound(name);
	}
	
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
		
			sound.play(volume, 0.0); // TODO: No panning yet
		}
		
	}
	
	public Music getMusic(String name) {
		return player.getMusic(name);
	}


	public void updateVolumeAndPan(Music music, Player player, GameObject noisyObject, double initialVolume, boolean loop) {
		
		float distSq = player.getLocation().getDistanceSq(noisyObject.getLocation());
		float maxDist = player.getHearDistance() * player.getHearDistance();
		
//		System.out.println("distSq=" + distSq + " maxDist=" + maxDist);
		
		if (distSq < maxDist) {
			
			if (!music.isPlaying()) {
				music.play(loop);
			}
			
			double volume = initialVolume * (1.0f - Math.sqrt(distSq / maxDist));
			music.setVolume(volume);
//			System.out.println("Volume=" + volume);
			
//			float x = -player.getTransform().getSinAngleY();
//			float z = -player.getTransform().getCosAngleY();
//			float cosX = player.getTransform().getCosAngleX();
//			float sinX = player.getTransform().getSinAngleX();
//			Vector3D playerNormal = new Vector3D(cosX * x, sinX, cosX * z);
//			playerNormal.normalize();
//			
//			Vector3D objectVector = new Vector3D(noisyObject.getLocation());
//			objectVector.subtract(player.getLocation());
//			objectVector.normalize();
//			
//			float p = playerNormal.getDotProduct(objectVector);
//			double angle = Math.acos(p); // / Math.PI * 180;
			
			Vector3D objectLocation = new Vector3D(noisyObject.getLocation());
			objectLocation.subtract(camera);
			//System.out.println("objectLocation=" + noisyObject.getLocation());
			viewWindow.project(objectLocation); 
			//System.out.println("objectLocation2=" + objectLocation);
			double pan = (objectLocation.x - viewWindow.getWidth() / 2) * (viewWindow.getAngle() / (Math.PI/2)) /  viewWindow.getWidth();
			//System.out.println("distSq=" + distSq);
			//System.out.println("pan=" + pan);
			
			//System.out.println("angle=" + angle);
			
			if (pan > 1.0) {
				pan = 1.0;
			}
			if (pan < -1.0) {
				pan = -1.0;
			}
			music.setPan(pan);
			
		} else {
			music.stop();
		}
		
	}
	
	public void init() {
		player.init();
	}

	public void close() {
		player.close();
	}


}
