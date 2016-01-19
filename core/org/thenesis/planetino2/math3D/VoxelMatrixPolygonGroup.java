package org.thenesis.planetino2.math3D;

import java.util.Hashtable;

import org.thenesis.planetino2.loader.QBMatrix;
import org.thenesis.planetino2.util.Vector;

public class VoxelMatrixPolygonGroup extends PolygonGroup implements Lightable {

	public static final String BOX_BLOCK_FILENAME = "voxelMatrix_internal.obj";

	private Hashtable boxModelMap;
	private Vector elements;
	private QBMatrix matrix;
	private Vector3D location;
	private float elementScale;
	private float ambientLightIntensity;

	public VoxelMatrixPolygonGroup(QBMatrix matrix, Vector3D location, float scale, float ambientLightIntensity) {
		this.matrix = matrix;
		this.location = location;
		this.elementScale = scale;
		this.ambientLightIntensity = ambientLightIntensity;
		boxModelMap = new Hashtable();
		elements = new Vector();
		matrix.removeHiddenVoxels();
		rebuild();
	}

	public void rebuild() {
		elements.clear();

		int sizeX = matrix.getSizeX();
		int sizeY = matrix.getSizeY();
		int sizeZ = matrix.getSizeZ();
		int[] voxelMatrixData = matrix.getData();

		float edgeSize = elementScale * 2;
		Vector3D elementLocation = new Vector3D(location);
		for (int z = 0; z < sizeZ; z++) {
			elementLocation.z = location.z + z * edgeSize;
			for (int y = 0; y < sizeY; y++) {
				elementLocation.y = location.y + y * edgeSize;
				for (int x = 0; x < sizeX; x++) {
					elementLocation.x = location.x + x * edgeSize;
					int nativeColor = matrix.getVoxelColor(x, y, z);
					boolean voxelVisible = matrix.isVoxelVisible(nativeColor);
					if (voxelVisible) {
						int argbColor = matrix.getRGBColor(nativeColor);
						String voxelColorName = getVoxelColorName(argbColor);
						BoxModel boxModel = (BoxModel) boxModelMap.get(voxelColorName);
						if (boxModel == null) {
							boxModel = BoxModel.createVoxelBoxModel(argbColor);
							boxModelMap.put(voxelColorName, boxModel);
						}
						Element element = new Element(boxModel, elementLocation, elementScale);
						addPolygonGroup(element);
						elements.addElement(element);
					}
				}
			}
		}
	}
	
	public void applyLights(Vector pointLights, float ambientLightIntensity) {
		int size = elements.size();
		for (int i = 0; i < size; i++) {
			Element e = (Element)elements.elementAt(i);
			e.applyLights(pointLights, ambientLightIntensity);
		}
	}
	
	public float getAmbientLightIntensity() {
		return ambientLightIntensity;
	}

	public static String getVoxelColorName(int color) {
		return Integer.toHexString(color);
	}

	public Vector getElements() {
		return elements;
	}

	public class Element extends BoxPolygonGroup {

		VoxelMatrixPolygonGroup boxBlock;

		public Element(BoxModel boxModel, Vector3D location, float scale) {
			super(boxModel, location, scale, ambientLightIntensity);
			this.boxBlock = VoxelMatrixPolygonGroup.this;
		}

	}
	
	public class StaticElement extends StaticBoxPolygonGroup {

		VoxelMatrixPolygonGroup boxBlock;

		public StaticElement(BoxModel boxModel, Vector3D location, float scale) {
			super(boxModel, location, scale, ambientLightIntensity);
			this.boxBlock = VoxelMatrixPolygonGroup.this;
		}

	}

	@Override
	public String toString() {
		return "BoxBlock " + getName();
	}

}
