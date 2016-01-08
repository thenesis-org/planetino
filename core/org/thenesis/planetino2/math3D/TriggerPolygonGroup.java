package org.thenesis.planetino2.math3D;


public class TriggerPolygonGroup extends PolygonGroup {

	public static final String TRIGGER_FILENAME = "trigger_internal.obj";
	
	private float radius;
	private float height;
	
	public TriggerPolygonGroup(String uniqueName, Vector3D location, float radius, float height) {
		this.radius = radius;
		this.height = height;
		getTransform().getLocation().setTo(location);
		setName(uniqueName);
		setFilename(TRIGGER_FILENAME);
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}
	
	@Override
	public String toString() {
		return "Trigger " + getName();
	}

}
