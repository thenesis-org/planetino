package org.thenesis.planetino2.math3D;

import java.util.Hashtable;

import org.thenesis.planetino2.graphics3D.texture.ShadedTexture;
import org.thenesis.planetino2.loader.QBLoader;
import org.thenesis.planetino2.loader.QBMatrix;
import org.thenesis.planetino2.loader.ObjectLoader.Material;
import org.thenesis.planetino2.util.Vector;

public class VoxelMatrixPolygonGroup extends PolygonGroup implements Lightable, CompositePolygonGroup {

	//public static final String BOX_BLOCK_FILENAME = "voxelMatrix_internal.obj";

	private Hashtable boxModelMap;
	private Vector elements;
	private QBMatrix matrix;
	private float elementScale;
	private float ambientLightIntensity;
	private PolygonGroupBounds bounds;

	public VoxelMatrixPolygonGroup(QBMatrix matrix, Vector3D location, float scale, float ambientLightIntensity) {
		this.matrix = matrix;
		this.elementScale = scale;
		this.ambientLightIntensity = ambientLightIntensity;
		boxModelMap = new Hashtable();
		elements = new Vector();
		bounds = new PolygonGroupBounds();
		matrix.removeHiddenVoxels();
		getTransform().getLocation().setTo(location);
		rebuild();
	}

	public void rebuild() {
		elements.clear();

		int matrixSizeX = matrix.getSizeX();
		int matrixSizeY = matrix.getSizeY();
		int matrixSizeZ = matrix.getSizeZ();

		float edgeSize = elementScale * 2;
		float midSizeX = matrixSizeX * edgeSize / 2;
		float midSizeZ = matrixSizeZ * edgeSize / 2;
		
		Vector3D elementLocation = new Vector3D();
		for (int z = 0; z < matrixSizeZ; z++) {
			elementLocation.z = z * edgeSize - midSizeZ;
			for (int y = 0; y < matrixSizeY; y++) {
				elementLocation.y = y * edgeSize;
				for (int x = 0; x < matrixSizeX; x++) {
					elementLocation.x = x * edgeSize - midSizeX;
					int nativeColor = matrix.getVoxelColor(x, y, z);
					boolean voxelVisible = matrix.isVoxelVisible(nativeColor);
					if (voxelVisible) {
						int argbColor = matrix.getRGBColor(nativeColor);
						int mask = matrix.getVisibityMask(nativeColor);
						String voxelColorName = getVoxelColorName(argbColor, mask);
						BoxModel boxModel = (BoxModel) boxModelMap.get(voxelColorName);
						if (boxModel == null) {
							boxModel = createVoxelBoxModel(argbColor, mask);
							boxModelMap.put(voxelColorName, boxModel);
						}
						Element element = new Element(boxModel, elementLocation, elementScale);
						addPolygonGroup(element);
						elements.addElement(element);
					}
				}
			}
		}
		
		bounds.setBottomHeight(0);
		bounds.setTopHeight(matrixSizeY * edgeSize);
		bounds.setRadius((float)Math.sqrt(midSizeX * midSizeX + midSizeZ * midSizeZ));
	}
	
	private static BoxModel createVoxelBoxModel(int color) {
		return createVoxelBoxModel(color, QBLoader.SIDE_MASK_ALL_SIDES_VISIBLE);
	}
	
	private static BoxModel createVoxelBoxModel(int color, int mask) {
		String colorName = Integer.toHexString(color);
		BoxModel boxDef = BoxModel.createBoxDef(colorName);
		
		// Create material
		int w = 2;
		int h = 2;
		int[] rgbData = new int[w * h];
		for (int i = 0; i < rgbData.length; i++) {
			rgbData[i] = color;
		}
		ShadedTexture texture = new ShadedTexture(rgbData, ShadedTexture.countbits(w - 1), ShadedTexture.countbits(h - 1));
		String library = "internal";
		String textureFileName = "internal";
		Material material = new Material(library, colorName, textureFileName, texture);
		
		// Create faces
		boolean animated = false;
		boolean stretched = false;
		int frameRate = 0;
		if ((mask & QBLoader.SIDE_MASK_TOP_SIDE_VISIBLE) == QBLoader.SIDE_MASK_TOP_SIDE_VISIBLE) {
			boxDef.setFaceModel(BoxModel.UP, material, animated, stretched, frameRate);
		}
		if ((mask & QBLoader.SIDE_MASK_BOTTOM_SIDE_VISIBLE) == QBLoader.SIDE_MASK_BOTTOM_SIDE_VISIBLE) {
			boxDef.setFaceModel(BoxModel.DOWN, material, animated, stretched, frameRate);
		}
		if ((mask & QBLoader.SIDE_MASK_BACK_SIDE_VISIBLE) == QBLoader.SIDE_MASK_BACK_SIDE_VISIBLE) {
			boxDef.setFaceModel(BoxModel.NORTH, material, animated, stretched, frameRate);
		}
		if ((mask & QBLoader.SIDE_MASK_FRONT_SIDE_VISIBLE) == QBLoader.SIDE_MASK_FRONT_SIDE_VISIBLE) {
			boxDef.setFaceModel(BoxModel.SOUTH, material, animated, stretched, frameRate);
		}
		if ((mask & QBLoader.SIDE_MASK_LEFT_SIDE_VISIBLE) == QBLoader.SIDE_MASK_LEFT_SIDE_VISIBLE) {
			boxDef.setFaceModel(BoxModel.EAST, material, animated, stretched, frameRate);
		}
		if ((mask & QBLoader.SIDE_MASK_RIGHT_SIDE_VISIBLE) == QBLoader.SIDE_MASK_RIGHT_SIDE_VISIBLE) {
			boxDef.setFaceModel(BoxModel.WEST, material, animated, stretched, frameRate);
		}
		return boxDef;
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

	public static String getVoxelColorName(int color, int mask) {
		return Integer.toHexString(color) + "_" + Integer.toHexString(mask);
	}

	public Vector getElements() {
		return elements;
	}
	
	@Override
	public PolygonGroupBounds getBounds() {
		return bounds;
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
		return "VoxelMatrix " + getName();
	}

}
