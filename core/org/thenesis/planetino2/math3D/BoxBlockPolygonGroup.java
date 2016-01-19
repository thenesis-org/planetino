package org.thenesis.planetino2.math3D;

import org.thenesis.planetino2.util.Vector;

public class BoxBlockPolygonGroup extends PolygonGroup implements Lightable {
	
	public static final String BOX_BLOCK_FILENAME = "boxBlock_internal.obj";
	
	private Vector elements;
	private BoxModel boxDef;
	private Vector3D location;
	private float elementScale;
	private int sizeX;
	private int sizeY;
	private int sizeZ;
	private float ambientLightIntensity;
	
	public BoxBlockPolygonGroup(BoxModel boxDef, Vector3D location, float scale, int sizeX, int sizeY, int sizeZ, float ambientLightIntensity) {
		this.boxDef = boxDef;
		this.location = location;
		this.elementScale = scale;
		elements = new Vector();
		setFilename(BOX_BLOCK_FILENAME);
		setSize(sizeX, sizeY, sizeZ);
		this.ambientLightIntensity = ambientLightIntensity;
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
					elements.addElement(element);
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
	
	public Vector getElements() {
		return elements;
	}
	
	public class Element extends BoxPolygonGroup {
		
		BoxBlockPolygonGroup boxBlock;

		public Element(BoxModel boxDef, Vector3D location, float scale) {
			super(boxDef, location, scale, ambientLightIntensity);
			this.boxBlock = BoxBlockPolygonGroup.this;
		}
		
	}
	
	@Override
	public String toString() {
		return "BoxBlock " + getName();
	}
	

}
