package org.thenesis.planetino2.game;

import org.thenesis.planetino2.math3D.PolygonGroup;
import org.thenesis.planetino2.math3D.PosterPolygonGroup;
import org.thenesis.planetino2.sound.Music;

public class Poster extends GameObject implements NoisyObject {

	public Poster(PolygonGroup polygonGroup) {
		super(polygonGroup);
	}
	
	//@Override
	public void update(GameObject player, long elapsedTime) {
		super.update(player, elapsedTime);
		PosterPolygonGroup posterPeer = (PosterPolygonGroup)getPolygonGroup();
		posterPeer.updateImage(elapsedTime);
	}

	//@Override
	public double getMaxSoundLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public Music getSoundLoop() {
		// TODO Auto-generated method stub
		return null;
	}

	//@Override
	public void setSoundLoop(String name) {
		// TODO Auto-generated method stub
		
	}
	
	

}
