package org.thenesis.planetino2.math3D;

import java.util.Vector;

public class BoxBlockPolygonGroup extends PolygonGroup {
	
	public static final String BOX_BLOCK_FILENAME = "boxBlock_internal.obj";
	
	private Vector elements;
	private BoxModel boxDef;
	private Vector3D location;
	private float elementScale;
	private int sizeX;
	private int sizeY;
	private int sizeZ;
	
	public BoxBlockPolygonGroup(BoxModel boxDef, Vector3D location, float scale, int sizeX, int sizeY, int sizeZ) {
		this.boxDef = boxDef;
		this.location = location;
		this.elementScale = scale;
		elements = new Vector();
		setFilename(BOX_BLOCK_FILENAME);
		setSize(sizeX, sizeY, sizeZ);
		rebuild();
	}
	
	public void setSize(int sizeX, int sizeY, int sizeZ) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
	}
	
	public void rebuild() {	
		elements.clear();
		
		float edgeSize = elementScale * 2;
		Vector3D elementLocation = new Vector3D(location);
		for (int z = 0; z < sizeZ; z++) {
			elementLocation.z = location.z + z * edgeSize;
			for (int y = 0; y < sizeY; y++) {
				elementLocation.y = location.y + y * edgeSize;
				for (int x = 0; x < sizeX; x++) {
					elementLocation.x = location.x + x * edgeSize;
					Element element = new Element(boxDef, elementLocation, elementScale);
					addPolygonGroup(element);
					elements.add(element);
				}
			}
		}
	}
	
	public Vector getElements() {
		return elements;
	}
	
	public class Element extends BoxPolygonGroup {
		
		BoxBlockPolygonGroup boxBlock;

		public Element(BoxModel boxDef, Vector3D location, float scale) {
			super(boxDef, location, scale);
			this.boxBlock = BoxBlockPolygonGroup.this;
		}
		
	}
	
	@Override
	public String toString() {
		return "BoxBlock " + getName();
	}
	

}
