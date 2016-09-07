package org.thenesis.planetino2.demo;

import java.io.IOException;

import org.thenesis.planetino2.loader.ObjectLoader;
import org.thenesis.planetino2.math3D.PolygonGroup;

public abstract class Weapon {
	
	public static final int WEAPON_RIFFLE = 0;
	public static final int WEAPON_GRAVITY_GUN = 1;
	
	protected ObjectLoader loader;
	
	public Weapon(ObjectLoader loader) {
		this.loader = loader;
	}
	
	public void capAmmoAdd(int amount) {
		if (getAmmo() < getMaxAmmo()) {
			addAmmo(amount);
			if (getAmmo() > getMaxAmmo()) {
				setMaxAmmo();
			}
		}
	}
	
	public abstract int getAmmo();

	public abstract int getMaxAmmo();
	
	public abstract int getType();
	
	public abstract void addAmmo(int v);
	
	public abstract void setMaxAmmo();
	
	public abstract PolygonGroup getBlastModel();

}
