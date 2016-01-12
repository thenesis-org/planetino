package org.thenesis.planetino2.math3D;

import java.util.Hashtable;
import java.util.Vector;

import org.thenesis.planetino2.loader.QBMatrix;

public class VoxelMatrixPolygonGroup extends PolygonGroup {

	public static final String BOX_BLOCK_FILENAME = "voxelMatrix_internal.obj";

	private Hashtable boxModelMap;
	private Vector elements;
	private QBMatrix matrix;
	private Vector3D location;
	private float elementScale;

	public VoxelMatrixPolygonGroup(QBMatrix matrix, Vector3D location, float scale) {
		this.matrix = matrix;
		this.location = location;
		this.elementScale = scale;
		boxModelMap = new Hashtable();
		elements = new Vector();
		setFilename(BOX_BLOCK_FILENAME);
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
					int nativeColor = voxelMatrixData[x + y * sizeX + z * sizeX * sizeY];
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
						elements.add(element);
					}
				}
			}
		}
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
			super(boxModel, location, scale);
			this.boxBlock = VoxelMatrixPolygonGroup.this;
		}

	}

	@Override
	public String toString() {
		return "BoxBlock " + getName();
	}

}
