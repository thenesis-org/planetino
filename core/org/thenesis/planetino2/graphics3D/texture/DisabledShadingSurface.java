package org.thenesis.planetino2.graphics3D.texture;


public class DisabledShadingSurface extends ShadedSurface {

	//private SoftReference bufferReference;
	private int shadeLevel;
	private boolean cleared = false;

	/**
	 Creates a ShadedSurface with the specified width and
	 height.
	 */
	public DisabledShadingSurface(int width, int height, float ambientLightIntensity) {
		super(width, height);
		this.shadeLevel = (int) Math.floor(ambientLightIntensity * ShadedTexture.MAX_LEVEL + 0.5d);
	}


	@Override
	public int getColor(int x, int y) {
		return sourceTexture.getColor(x, y, shadeLevel);
	}

	/**
	 Creates a new surface and add a SoftReference to it.
	 */
	protected void newSurface(int width, int height) {
	}

	@Override
	public void clearSurface() {
		cleared = true;
	}

	@Override
	public boolean isCleared() {
		return cleared;
	}

	/**
	 If the buffer has been previously built and cleared but
	 not yet removed from memory by the garbage collector,
	 then this method attempts to retrieve it. Returns true if
	 successfull.
	 */
	private boolean retrieveSurface() {
		return true;
	}

	@Override
	public void buildSurface() {
		cleared = false;
	}

}
