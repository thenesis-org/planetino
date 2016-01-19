package org.thenesis.planetino2.math3D;

import org.thenesis.planetino2.util.Vector;

public interface Lightable {
	
	public float getAmbientLightIntensity();
	
	public void applyLights(Vector pointLights, float ambientLightIntensity);

}
