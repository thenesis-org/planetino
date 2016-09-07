package org.thenesis.planetino2.demo;

import org.thenesis.planetino2.ai.Projectile;
import org.thenesis.planetino2.game.GameObject;
import org.thenesis.planetino2.game.Physics;
import org.thenesis.planetino2.game.Player;
import org.thenesis.planetino2.game.Trigger;
import org.thenesis.planetino2.math3D.MovingTransform3D;
import org.thenesis.planetino2.math3D.PolygonGroup;
import org.thenesis.planetino2.math3D.TriggerPolygonGroup;
import org.thenesis.planetino2.math3D.Vector3D;
import org.thenesis.planetino2.sound.Music;
import org.thenesis.planetino2.sound.Sound;
import org.thenesis.planetino2.sound.SoundManager;

public class ShooterPlayer extends Player {
	
	private static final float DEFAULT_MAX_ADRENALINE = 100.0F;
	
	private Weapon weapon;
	private boolean riffleItemCatched = false;
	
	private float adrenaline = DEFAULT_MAX_ADRENALINE;
	
	private SoundManager soundManager;
	private WeaponManager weaponManager;
	
	private boolean adrenalineMode = false;
	private boolean zoomViewMode = false;
	private int lifeCount = 3;
	private int kills = 0;
	public boolean goNextLevel = false;
	private boolean isInElevator = false;

	private Music itemCatchSound;
	private Music weaponChangeSound;
	private Music ammoCatchSound;
	private Sound fireSound;
	private Sound gravityFireSound;
	private Sound gravityThrowSound;
	private Music gravityCatchSound;
	private Sound jumpSound;
	private Sound painSound;
	private Music elevatorSound;
	
	private CatchableGameObject objectAttachedToGravityGun;

	public ShooterPlayer(SoundManager soundManager, WeaponManager weaponManager) {
		super();
		this.soundManager = soundManager;
		this.weaponManager = weaponManager;

		jumpSound = soundManager.getSound("jump1.wav");
		itemCatchSound = soundManager.getMusic("power_up2.wav");
		ammoCatchSound = soundManager.getMusic("change.wav");
		weaponChangeSound = soundManager.getMusic("weaponpickup.wav");
		fireSound = soundManager.getSound("hook_fire.wav");
		gravityFireSound = soundManager.getSound("gravity_catch-X08MMA-small.wav");
		gravityThrowSound = soundManager.getSound("gravity_throw-X03SFLGSM.wav");
		gravityCatchSound = soundManager.getMusic("gravity_catch-X08MMA-loop.wav");
		painSound = soundManager.getSound("pain25_2.wav");
		elevatorSound = soundManager.getMusic("antigravity_elevator.wav");
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
		setHealth(DEFAULT_MAX_HEALTH);
		this.adrenaline = DEFAULT_MAX_ADRENALINE;
	}

	public void capHealthAdd(float amount) {
		if (getHealth() < DEFAULT_MAX_HEALTH) {
			this.health += amount;
			if (this.health > DEFAULT_MAX_HEALTH) {
				this.health = DEFAULT_MAX_HEALTH;
			}
		}
	}

	public void capAdrenalineAdd(float amount) {
		if (getAdrenaline() < DEFAULT_MAX_ADRENALINE) {
			this.adrenaline += amount;
			if (this.adrenaline > DEFAULT_MAX_ADRENALINE) {
				this.adrenaline = DEFAULT_MAX_ADRENALINE;
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
		float fAmount = amount / DEFAULT_MAX_ADRENALINE;
		this.adrenaline -= fAmount;
		if (this.adrenaline < 0.0F) {
			this.adrenaline = 0.0F;
		}
		return true;
	}

	public float getMaxAdrenaline() {
		return DEFAULT_MAX_ADRENALINE;
	}

	@Override
	public void fireProjectile() {
		
		if (weapon == null) {
			return;
		}
		
		float x = -getTransform().getSinAngleY();
		float z = -getTransform().getCosAngleY();
		float cosX = getTransform().getCosAngleX();
		float sinX = getTransform().getSinAngleX();
		
		if (weapon.getType() == Weapon.WEAPON_RIFFLE) {
			if (weapon.getAmmo() <= 0) {
				return;
			}
			weapon.addAmmo(-1);
			Projectile blast = new Projectile((PolygonGroup) weapon.getBlastModel().clone(), new Vector3D(cosX * x, sinX, cosX * z), null, 40, 60);
			float dist = getBounds().getRadius() + blast.getBounds().getRadius();
			blast.getLocation().setTo(getX() + x * dist, getY() + BULLET_HEIGHT, getZ() + z * dist);
			fireSound.play();
			addSpawn(blast);
		} else if (weapon.getType() == Weapon.WEAPON_GRAVITY_GUN) {
			if (objectAttachedToGravityGun != null) {
				MovingTransform3D transform = objectAttachedToGravityGun.getTransform();
		        Vector3D velocity = transform.getVelocity();
		        velocity.setTo(cosX * x, sinX, cosX * z);
		        float launchSpeed = 2.0f;
		        velocity.multiply(launchSpeed);
		        transform.setVelocity(velocity);
		        //objectAttachedToGravityGun.getTransform().setVelocity(velocity);
		        //transform.setAngleVelocityX(.008f);
		        //transform.setAngleVelocityY(.005f);
		        //transform.setAngleVelocityZ(ROT_SPEED);
		        objectAttachedToGravityGun.setFlying(false);
		        objectAttachedToGravityGun.setJumping(true);
		        objectAttachedToGravityGun = null;
		        gravityCatchSound.stop();
		        gravityThrowSound.play();
			} else {
				//TriggerPolygonGroup trigger = new TriggerPolygonGroup(null, new Vector3D(), 50, 50);
				//GravityGunProjectile blast = new GravityGunProjectile(trigger, this, new Vector3D(cosX * x, sinX, cosX * z));
				GravityGunProjectile blast = new GravityGunProjectile((PolygonGroup) weapon.getBlastModel().clone(), this, new Vector3D(cosX * x, sinX, cosX * z));
				float dist = getBounds().getRadius() + blast.getBounds().getRadius();
				blast.getLocation().setTo(getX() + x * dist, getY() + BULLET_HEIGHT, getZ() + z * dist);
				gravityFireSound.play();
				addSpawn(blast);
			}
		}
		
		makeNoise(500L);
	}

	@Override
	public void notifyObjectCollision(GameObject obj) {
		super.notifyObjectCollision(obj);
		String filename = obj.getPolygonGroup().getFilename();
		if (filename.equalsIgnoreCase(DemoEngine.OBJECT_FILENAME_HEALTH_PACK)) {
			if (health >= maxHealth) {
				return;
			}
			if (!itemCatchSound.isPlaying()) {
				itemCatchSound.rewind();
			}
			itemCatchSound.play(false);
			capHealthAdd(50.0F);
			setState(obj, STATE_DESTROYED);
		} else if (filename.equalsIgnoreCase(DemoEngine.OBJECT_FILENAME_ADRENALINE)) {
			itemCatchSound.play(false);
			capAdrenalineAdd(50.0F);
			setState(obj, STATE_DESTROYED);
		} else if (filename.equalsIgnoreCase(DemoEngine.OBJECT_FILENAME_AMMO_PACK)) {
			if (weapon == null) {
				return;
			}
			if (!ammoCatchSound.isPlaying()) {
				ammoCatchSound.rewind();
			}
			ammoCatchSound.play(false);
			weapon.capAmmoAdd(50);
			setState(obj, STATE_DESTROYED);
		} else if (filename.equalsIgnoreCase(DemoEngine.OBJECT_FILENAME_WEAPON)) {
			riffleItemCatched = true;
			setWeapon(Weapon.WEAPON_RIFFLE);
			weapon.setMaxAmmo();
			setState(obj, STATE_DESTROYED);
		}
	}
	
	protected void notifyObjectTouch(GameObject otherObject) {
		if ((otherObject instanceof Trigger) && (otherObject.getName().equalsIgnoreCase("trigger_elevator"))) {
			isInElevator = true;
			setJumping(true);
			elevatorSound.rewind();
			elevatorSound.play(false);
		} 
	 }

	 protected void notifyObjectRelease(GameObject otherObject) {
		 if ((otherObject instanceof Trigger) && (otherObject.getName().equalsIgnoreCase("trigger_elevator"))) {
			isInElevator = false;
		} 
	 }
	
	@Override
	public void update(GameObject player, long elapsedTime) {
		super.update(player, elapsedTime);
		if(isInElevator) {
			Physics.getInstance().scootUp(this, elapsedTime);
		}
		
		if (objectAttachedToGravityGun != null) {
			float dist = getBounds().getRadius() + 2 * objectAttachedToGravityGun.getBounds().getRadius();
			float x = -getTransform().getSinAngleY();
			float z = -getTransform().getCosAngleY();
			float sinX = getTransform().getSinAngleX();
			objectAttachedToGravityGun.getLocation().setTo(getX() + x * dist, getY() + BULLET_HEIGHT + dist * sinX, getZ() + z * dist);
		}
	}

	@Override
	public void setJumping(boolean isJumping) {
		super.setJumping(isJumping);
		if (isJumping) {
			jumpSound.play();
		}
	}
	
	@Override
	public void addHealth(float addition) {
        super.addHealth(addition);
        // Note: dead sound is played in the main game loop
        if ((getHealth() > 0) && (addition < 0)) {
			painSound.play();
		}
    }
	
	public void attachToGravityGun(CatchableGameObject object) {
//		if (object instanceof RespawnableItem) {
//			notifyObjectCollision(object);
//			return;
//		}
		objectAttachedToGravityGun = object;
		objectAttachedToGravityGun.setFlying(true);
		MovingTransform3D transform = objectAttachedToGravityGun.getTransform();
        Vector3D velocity = transform.getVelocity();
        velocity.setTo(0, 0, 0);
        transform.setVelocity(velocity);
        
        gravityFireSound.stop();
        gravityCatchSound.rewind();
        gravityCatchSound.play(true);
        
	}

	public void setWeapon(int weaponType) {
		if (weaponType == Weapon.WEAPON_RIFFLE && (!riffleItemCatched)) {
			return;
		}
		setWeapon(weaponManager.getWeapon(weaponType));
	}
	
	public void setWeapon(Weapon weapon) {
		if (this.weapon == weapon) {
			return;
		}
		this.weapon = weapon;
		weaponChangeSound.rewind();
		weaponChangeSound.play(false);
	}

	public Weapon getWeapon() {
		return weapon;
	}
}
