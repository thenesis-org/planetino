package org.thenesis.planetino2.demo.levels;

import org.thenesis.planetino2.demo.Level;
import org.thenesis.planetino2.demo.RespawnableItem;
import org.thenesis.planetino2.engine.shooter3D.Bot;
import org.thenesis.planetino2.game.BoxMatrix;
import org.thenesis.planetino2.game.GameObject;
import org.thenesis.planetino2.math3D.MovingTransform3D;
import org.thenesis.planetino2.math3D.PolygonGroup;
import org.thenesis.planetino2.math3D.Vector3D;
import org.thenesis.planetino2.math3D.VoxelMatrixPolygonGroup;
import org.thenesis.planetino2.util.Vector;

public class TownOfFuryLevel extends Level {

	public VoxelMatrixPolygonGroup sniperTerrain;
	public Vector sniperTerrainGameObjects = new Vector();

	public String getAmbientMusicName() {
		return "TownOfFury.wav";
	}

	public String getMapName() {
		return "TownOfFury.map";
	}

	//@Override
	public GameObject createGameObject(PolygonGroup polygonGroup) {
//		// FIXME Temporary inefficient hack to get the VoxelMatrixPolygonGroup from its elements
		if (polygonGroup instanceof VoxelMatrixPolygonGroup) {
			VoxelMatrixPolygonGroup voxelGroup = (VoxelMatrixPolygonGroup) polygonGroup;
			if (voxelGroup.getName().equals("sniper_terrain")) {

				BoxMatrix gameObject = new BoxMatrix(voxelGroup); //new Blast(element, new Vector3D(100, 0, 0));
//				MovingTransform3D transform = gameObject.getTransform();
//				Vector3D velocity = transform.getVelocity();
//				velocity.setTo(10f, 0, 0);
//				//velocity.multiply(5);
//				transform.setVelocity(velocity);
//				//transform.setAngleVelocityX(ROT_SPEED);
//				//transform.setAngleVelocityY(ROT_SPEED);
//				//transform.setAngleVelocityZ(ROT_SPEED);

				sniperTerrainGameObjects.add(gameObject);
				return gameObject;
			}
		}
		return null;
	}
	
}
