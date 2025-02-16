package org.thenesis.planetino2.shooter;

import java.io.IOException;

import org.thenesis.planetino2.loader.ObjectLoader;
import org.thenesis.planetino2.math3D.PolygonGroup;

public class WeaponManager {
	
	public static final int WEAPON_RIFFLE = 0;
	public static final int WEAPON_GRAVITY_GUN = 1;
	
	private Weapon riffleWeapon;
	private Weapon gravityGunWeapon;
	
	protected ObjectLoader loader;
	
	public void load() throws IOException {
		gravityGunWeapon = new GravityGunWeapon(loader);
		riffleWeapon = new RiffleWeapon(loader);
	}
	
	public Weapon getWeapon(int type) {
		switch(type) {
		case WEAPON_RIFFLE:
			return riffleWeapon;
		case WEAPON_GRAVITY_GUN:
			return gravityGunWeapon;
		}
		return null;
	}
	
	public WeaponManager(ObjectLoader loader) {
		this.loader = loader;
	}

}
