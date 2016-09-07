package org.thenesis.planetino2.demo.levels;

import org.thenesis.planetino2.demo.Level;
import org.thenesis.planetino2.demo.ShooterEngine;
import org.thenesis.planetino2.demo.ShooterPlayer;
import org.thenesis.planetino2.game.CollisionDetection;
import org.thenesis.planetino2.game.GameObject;
import org.thenesis.planetino2.math3D.MovingTransform3D;
import org.thenesis.planetino2.math3D.PolygonGroup;
import org.thenesis.planetino2.math3D.Vector3D;
import org.thenesis.planetino2.math3D.VoxelMatrixPolygonGroup;
import org.thenesis.planetino2.util.Vector;

public class TownOfFuryLevel extends Level {

	public TownOfFuryLevel(ShooterEngine engine) {
		super(engine);
	}

	public VoxelMatrixPolygonGroup sniperTerrain;
	public Vector sniperTerrainGameObjects = new Vector();

	public String getAmbientMusicName() {
		return "TownOfFury-Tension.wav";
	}
	
	public String getIntroSoundName() {
		return "TownOfFury-Intro.wav";
	}

	public String getMapName() {
		return "TownOfFury.map";
	}

	@Override
	public GameObject createGameObject(PolygonGroup polygonGroup) {
		if (polygonGroup.getName().startsWith("sniper_terrain")) {
			return new SniperTerrain(polygonGroup);
		} else if (polygonGroup.getName().equals("big_eye")) {
			return new BigEye(polygonGroup);
		}
		return null;
	}

}

class SniperTerrain extends GameObject {

	private static float SPEED_HORIZONTAL = 2f;
	private static float SPEED_VERTICAL = 1f;
	boolean first = true;
	Vector3D previousLocation;
	GameObject attachedObject;
	float edgeSize = 8000;
	long cumulatedTime = 0;

	public SniperTerrain(PolygonGroup polygonGroup) {
		super(polygonGroup);
		MovingTransform3D transform = getTransform();
		Vector3D velocity = transform.getVelocity();
		velocity.setTo(SPEED_HORIZONTAL, 0, 0);
		transform.setVelocity(velocity);
	}

	@Override
	public boolean isFlying() {
		return true;
	}

	@Override
	public void notifyObjectCollision(GameObject otherObject) {
		super.notifyObjectCollision(otherObject);
	}

	@Override
	protected void notifyObjectTouch(GameObject otherObject) {
		if (otherObject instanceof ShooterPlayer) {
			attach(otherObject);
		}
	}

	/**
	 * Not used to attach the player to the platform because the behavior is
	 * not the one expected
	 */
	@Override
	protected void notifyObjectRelease(GameObject otherObject) {
		super.notifyObjectRelease(otherObject);
	}

	@Override
	public void update(GameObject player, long elapsedTime) {
		super.update(player, elapsedTime);

		// Be sure the player is attached to the platform when the level is starting
		if (first) {
			attach(player);
			first = false;
		}

		// Move the platform around a square
		cumulatedTime += elapsedTime;
		float verticalSpeed = (float) (SPEED_VERTICAL * Math.random());
		MovingTransform3D transform = getTransform();
		Vector3D currentLocation = transform.getLocation();
		if ((currentLocation.x > edgeSize) && (currentLocation.z < edgeSize)) {
			transform.getVelocity().setTo(0, verticalSpeed, SPEED_HORIZONTAL);
		} else if ((currentLocation.x > edgeSize) && (currentLocation.z > edgeSize)) {
			transform.getVelocity().setTo(-SPEED_HORIZONTAL, -verticalSpeed, 0);
		} else if ((currentLocation.x < -edgeSize) && (currentLocation.z > edgeSize)) {
			transform.getVelocity().setTo(0, verticalSpeed, -SPEED_HORIZONTAL);
		} else if ((currentLocation.x < -edgeSize) && (currentLocation.z < -edgeSize)) {
			transform.getVelocity().setTo(SPEED_HORIZONTAL, -verticalSpeed, 0);
		}

		// Force the attached object to follow the platform
		if (attachedObject != null) {
			// First check if the attached object is still on the platform
			if ((!attachedObject.isJumping()) && (!CollisionDetection.areInCollision(this, attachedObject))) {
				attachedObject = null;
			} else {
				currentLocation = new Vector3D(getTransform().getLocation());
				currentLocation.subtract(previousLocation);
				attachedObject.getTransform().getLocation().add(currentLocation);
				previousLocation.setTo(getTransform().getLocation());
			}
		}
	}

	public void attach(GameObject object) {
		attachedObject = object;
		previousLocation = new Vector3D(getTransform().getLocation());
	}

}

class BigEye extends GameObject {

	private static final float TURN_SPEED = .001f;
    private static final long DECISION_TIME = 6000;

    protected MovingTransform3D mainTransform;
    protected long timeUntilDecision;
    protected Vector3D lastPlayerLocation;

    public BigEye(PolygonGroup polygonGroup) {
        super(polygonGroup);
        mainTransform = polygonGroup.getTransform();
        lastPlayerLocation = new Vector3D();
    }

    public void notifyVisible(boolean visible) {
        if (!isDestroyed()) {
            if (visible) {
                setState(STATE_ACTIVE);
            }
            else {
                setState(STATE_IDLE);
            }
        }
    }

    public void update(GameObject player, long elapsedTime) {
        if (mainTransform == null || isIdle()) {
            return;
        }

        Vector3D playerLocation = player.getLocation();
        if (playerLocation.equals(lastPlayerLocation)) {
            timeUntilDecision = DECISION_TIME;
        }
        else {
            timeUntilDecision-=elapsedTime;
            if (timeUntilDecision <= 0 &&
                !mainTransform.isTurningY())
            {
                float x = player.getX() - getX();
                float z = player.getZ() - getZ();
                //float y = player.getY() - getY();
                mainTransform.turnYTo(x, z, -2.0f, TURN_SPEED);
                //mainTransform.turnXTo(y, z, 1.5f, TURN_SPEED);	
                //mainTransform.turnZTo(x, y, 1.5f, TURN_SPEED);	
                lastPlayerLocation.setTo(playerLocation);
                timeUntilDecision = DECISION_TIME;
            }
        }
        super.update(player, elapsedTime);
    }
	
}
