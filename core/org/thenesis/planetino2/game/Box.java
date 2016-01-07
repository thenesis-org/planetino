package org.thenesis.planetino2.game;

import org.thenesis.planetino2.math3D.BoxPolygonGroup;
import org.thenesis.planetino2.math3D.PolygonGroup;
import org.thenesis.planetino2.sound.Music;

public class Box extends GameObject implements NoisyObject {

	public Box(PolygonGroup polygonGroup) {
		super(polygonGroup);
		//System.out.println("bounds=" + getBounds());
		//System.out.println("radius=" + getBounds().getRadius());
		//getBounds().setRadius(50); // Prevent collision detection (FIXME ? hacky ?)
	}
	
	//@Override
	public void update(GameObject player, long elapsedTime) {
		super.update(player, elapsedTime);
		BoxPolygonGroup posterPeer = (BoxPolygonGroup)getPolygonGroup();
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
