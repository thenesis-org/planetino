package org.thenesis.planetino2.demo;

import org.thenesis.planetino2.ai.Projectile;
import org.thenesis.planetino2.game.GameObject;
import org.thenesis.planetino2.game.Player;
import org.thenesis.planetino2.graphics.Toolkit;
import org.thenesis.planetino2.math3D.PolygonGroup;
import org.thenesis.planetino2.math3D.Vector3D;
import org.thenesis.planetino2.sound.Sound;
import org.thenesis.planetino2.sound.SoundManager;

public class ShooterPlayer extends Player {
	private static final float DEFAULT_MAX_ADRENALINE = 100.0F;
	private static final int DEFAULT_MAX_AMMO = 500;
	private int ammo = 50;
	private float adrenaline = 100.0F;
	private SoundManager soundManager;
	private boolean adrenalineMode = false;
	private boolean zoomViewMode = false;
	private int lifeCount = 3;
	private int kills = 0;
	public boolean goNextLevel = false;
	//private static final AudioFormat PLAYBACK_FORMAT = new AudioFormat(44100.0F, 8, 1, true, false);
	public Sound s1;
	private Sound s2;
	private Sound itemCatchSound;
	private Sound fireSound;
	private Sound s5;

	public ShooterPlayer() {
		super();

		soundManager = Toolkit.getInstance().getSoundManager();

		this.s1 = soundManager.getSound("1.wav");
		this.s2 = soundManager.getSound("2.wav");
		this.itemCatchSound = soundManager.getSound("3.wav");
		this.fireSound = soundManager.getSound("4.wav");
		this.s5 = soundManager.getSound("5.wav");
	}

	public int getAmmo() {
		return this.ammo;
	}

	public int getMaxAmmo() {
		return 500;
	}

	public boolean isAdrenalineMode() {
		return this.adrenalineMode;
	}

	public boolean getZoomView() {
		return this.zoomViewMode;
	}

	public void setZoomView(boolean b) {
		this.zoomViewMode = b;
	}

	public void setAdrenalineMode(boolean enabled) {
		this.adrenalineMode = enabled;
	}

	public void resetPlayer() {
		setHealth(100.0F);
		this.adrenaline = 100.0F;
	}

	public void cappedHealthAdd(float amount) {
		if (getHealth() < 100.0F) {
			this.health += amount;
			if (this.health > 100.0F) {
				this.health = 100.0F;
			}
		}
	}

	public void cappedAdrenalineAdd(float amount) {
		if (getAdrenaline() < 100.0F) {
			this.adrenaline += amount;
			if (this.adrenaline > 100.0F) {
				this.adrenaline = 100.0F;
			}
		}
	}

	public float getAdrenaline() {
		return this.adrenaline;
	}

	public boolean useAdrenaline(int amount) {
		if (this.adrenaline <= 0.0F) {
			return false;
		}
		float fAmount = amount / 100.0F;
		this.adrenaline -= fAmount;
		if (this.adrenaline < 0.0F) {
			this.adrenaline = 0.0F;
		}
		return true;
	}

	public float getMaxAdrenaline() {
		return 100.0F;
	}

	@Override
	public void fireProjectile() {
		if (this.ammo <= 0) {
			return;
		}
		this.ammo -= 1;

		float x = -getTransform().getSinAngleY();
		float z = -getTransform().getCosAngleY();
		float cosX = getTransform().getCosAngleX();
		float sinX = getTransform().getSinAngleX();

		Projectile blast = new Projectile((PolygonGroup) this.blastModel.clone(), new Vector3D(cosX * x, sinX, cosX * z), null, 40, 60);
		//blast.setFromPlayer(true);
		float dist = getBounds().getRadius() + blast.getBounds().getRadius();

		blast.getLocation().setTo(getX() + x * dist, getY() + 75.0F, getZ() + z * dist);

		addSpawn(blast);

		soundManager.play(fireSound);

		makeNoise(500L);
	}

	private boolean hasSyrum = false;

	@Override
	public void notifyObjectCollision(GameObject obj) {
		if (obj.getPolygonGroup().getName().equalsIgnoreCase("healthPack")) {
			this.soundManager.play(this.itemCatchSound);
			cappedHealthAdd(50.0F);
			//obj.setState(2); // FIXME
		} else if (obj.getPolygonGroup().getName().equalsIgnoreCase("adrenaline")) {
			this.soundManager.play(this.itemCatchSound);
			cappedAdrenalineAdd(50.0F);
			//obj.setState(2); // FIXME
		} else if (obj.getPolygonGroup().getName().equalsIgnoreCase("ammo")) {
			this.soundManager.play(this.itemCatchSound);
			this.ammo += 50;
			//obj.setState(2); // FIXME
		} else if (obj.getPolygonGroup().getName().equalsIgnoreCase("syrum")) {
			this.soundManager.play(this.itemCatchSound);
			this.hasSyrum = true;
			//obj.setState(2); // FIXME
		} /*else if ((obj.getPolygonGroup().getFilename() != null) && (obj.getPolygonGroup().getFilename().equalsIgnoreCase("goal.obj"))) {
			if (this.hasSyrum) {
				this.sm.play(this.s2);

				this.goNextLevel = true;
			}
		} else if ((obj.getPolygonGroup().getFilename() != null) && (obj.getPolygonGroup().getFilename().equalsIgnoreCase("spike.obj"))) {
			setHealth(0.0F);
		} else if ((obj.getPolygonGroup().getFilename() != null) && (obj.getPolygonGroup().getFilename().equalsIgnoreCase("zombie.obj"))) {
			setHealth(getHealth() - 25.0F);
		}*/
	}

	public void playSound(Sound s) {
		this.soundManager.play(s);
	}
}
