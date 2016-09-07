package org.thenesis.planetino2.demo;

import java.io.IOException;

import org.thenesis.planetino2.loader.ObjectLoader;
import org.thenesis.planetino2.math3D.PolygonGroup;

public class RiffleWeapon extends Weapon {

	private static final int DEFAULT_MAX_AMMO = 50;
	
	private int ammo = 0;
	
	protected PolygonGroup blastModel;

	public RiffleWeapon(ObjectLoader loader) throws IOException {
		super(loader);
		blastModel = loader.loadObject("spiked_ball.obj");
	}
	
	public int getAmmo() {
		return this.ammo;
	}

	public int getMaxAmmo() {
		return DEFAULT_MAX_AMMO;
	}
	
	public int getType() {
		return WEAPON_RIFFLE;
	}
	
	public void addAmmo(int v) {
		ammo += v;
	}
	
	public void setMaxAmmo() {
		ammo = DEFAULT_MAX_AMMO;
	}
	
	public PolygonGroup getBlastModel() {
		return blastModel;
	}

}
