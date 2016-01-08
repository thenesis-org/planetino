package org.thenesis.planetino2.game;

import org.thenesis.planetino2.math3D.TriggerPolygonGroup;

public class Trigger extends GameObject {
	
	public static final String TRIGGER_FILENAME = "trigger_internal.obj";

	public Trigger(TriggerPolygonGroup group) {
		super(group);
		rebuild();
	}

	public void rebuild() {
		TriggerPolygonGroup group = (TriggerPolygonGroup) getPolygonGroup();
        getBounds().setTopHeight(group.getHeight());
        getBounds().setRadius(group.getRadius());
	}

}
