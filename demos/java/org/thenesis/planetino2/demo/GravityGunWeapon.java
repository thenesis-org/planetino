package org.thenesis.planetino2.demo;

import java.io.IOException;

import org.thenesis.planetino2.loader.ObjectLoader;
import org.thenesis.planetino2.math3D.PolygonGroup;

public class GravityGunWeapon extends Weapon {
	
	protected PolygonGroup blastModel;

	public GravityGunWeapon(ObjectLoader loader) throws IOException {
		super(loader);
		blastModel = loader.loadObject("elipsoid.obj");
	}

	private static final int DEFAULT_MAX_AMMO = 1;
	
	public int getAmmo() {
		return DEFAULT_MAX_AMMO;
	}

	public int getMaxAmmo() {
		return DEFAULT_MAX_AMMO;
	}
	
	public int getType() {
		return WEAPON_GRAVITY_GUN;
	}
	
	public void addAmmo(int v) {
		// Do nothing
	}
	
	public void setMaxAmmo() {
		// Do nothing
	}
	
	public PolygonGroup getBlastModel() {
		return blastModel;
	}

}
