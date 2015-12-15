package org.thenesis.planetino2.graphics3D.texture;

import org.thenesis.planetino2.math3D.Rectangle3D;
import org.thenesis.planetino2.math3D.Vector3D;

public class PreShadedSurface extends ShadedSurface {

	//private SoftReference bufferReference;
	private int[] buffer;

	/**
	 Creates a ShadedSurface with the specified width and
	 height.
	 */
	public PreShadedSurface(int width, int height) {
		this(null, width, height);
	}

	/**
	 Creates a ShadedSurface with the specified buffer,
	 width and height.
	 */
	public PreShadedSurface(int[] buffer, int width, int height) {
		super(width, height);
		this.buffer = buffer;
		//bufferReference = new SoftReference(buffer);
	}

	@Override
	public int getColor(int x, int y) {
		return buffer[x + y * width]; // << 16 | 0xFF000000;
		//return sourceTexture.getColor(x, y);
	}

	/**
	 Creates a new surface and add a SoftReference to it.
	 */
	protected void newSurface(int width, int height) {
		buffer = new int[width * height];
		//bufferReference = new SoftReference(buffer);
	}

	@Override
	public void clearSurface() {
		buffer = null;
	}

	@Override
	public boolean isCleared() {
		return (buffer == null);
	}

	/**
	 If the buffer has been previously built and cleared but
	 not yet removed from memory by the garbage collector,
	 then this method attempts to retrieve it. Returns true if
	 successfull.
	 */
	private boolean retrieveSurface() {
		//	        if (buffer == null) {
		//	            buffer = (short[])bufferReference.get();
		//	        }
		return !(buffer == null);
	}

	@Override
	public void buildSurface() {

		if (retrieveSurface()) {
			return;
		}

		int width = (int) surfaceBounds.getWidth();
		int height = (int) surfaceBounds.getHeight();

		// create a new surface (buffer)
		newSurface(width, height);

		// builds the surface.
		// assume surface bounds and texture bounds are aligned
		// (possibly with different origins)
		Vector3D origin = sourceTextureBounds.getOrigin();
		Vector3D directionU = sourceTextureBounds.getDirectionU();
		Vector3D directionV = sourceTextureBounds.getDirectionV();

		Vector3D d = new Vector3D(surfaceBounds.getOrigin());
		d.subtract(origin);
		int startU = (int) ((d.getDotProduct(directionU) - SURFACE_BORDER_SIZE));
		int startV = (int) ((d.getDotProduct(directionV) - SURFACE_BORDER_SIZE));
		int offset = 0;
		int shadeMapOffsetU = SHADE_RES - SURFACE_BORDER_SIZE - startU;
		int shadeMapOffsetV = SHADE_RES - SURFACE_BORDER_SIZE - startV;

		for (int v = startV; v < startV + height; v++) {
			sourceTexture.setCurrRow(v);
			int u = startU;
			int amount = SURFACE_BORDER_SIZE;
			while (u < startU + width) {
				getInterpolatedShade(u + shadeMapOffsetU, v + shadeMapOffsetV);

				// keep drawing until we need to recalculate
				// the interpolated shade. (every SHADE_RES pixels)
				int endU = Math.min(startU + width, u + amount);
				while (u < endU) {
					buffer[offset++] = sourceTexture.getColorCurrRow(u, shadeValue >> SHADE_RES_SQ_BITS);
					shadeValue += shadeValueInc;
					u++;
				}
				amount = SHADE_RES;
			}
		}

		// if the surface bounds is not aligned with the texture
		// bounds, use this (slower) code.
		/*Vector3D origin = sourceTextureBounds.getOrigin();
		 Vector3D directionU = sourceTextureBounds.getDirectionU();
		 Vector3D directionV = sourceTextureBounds.getDirectionV();

		 Vector3D d = new Vector3D(surfaceBounds.getOrigin());
		 d.subtract(origin);
		 int initTextureU = (int)(SCALE *
		 (d.getDotProduct(directionU) - SURFACE_BORDER_SIZE));
		 int initTextureV = (int)(SCALE *
		 (d.getDotProduct(directionV) - SURFACE_BORDER_SIZE));
		 int textureDu1 = (int)(SCALE * directionU.getDotProduct(
		 surfaceBounds.getDirectionV()));
		 int textureDv1 = (int)(SCALE * directionV.getDotProduct(
		 surfaceBounds.getDirectionV()));
		 int textureDu2 = (int)(SCALE * directionU.getDotProduct(
		 surfaceBounds.getDirectionU()));
		 int textureDv2 = (int)(SCALE * directionV.getDotProduct(
		 surfaceBounds.getDirectionU()));

		 int shadeMapOffset = SHADE_RES - SURFACE_BORDER_SIZE;

		 for (int v=0; v<height; v++) {
		 int textureU = initTextureU;
		 int textureV = initTextureV;

		 for (int u=0; u<width; u++) {
		 if (((u + shadeMapOffset) & SHADE_RES_MASK) == 0) {
		 getInterpolatedShade(u + shadeMapOffset,
		 v + shadeMapOffset);
		 }
		 buffer[offset++] = sourceTexture.getColor(
		 textureU >> SCALE_BITS,
		 textureV >> SCALE_BITS,
		 shadeValue >> SHADE_RES_SQ_BITS);
		 textureU+=textureDu2;
		 textureV+=textureDv2;
		 shadeValue+=shadeValueInc;

		 }
		 initTextureU+=textureDu1;
		 initTextureV+=textureDv1;
		 }*/
	}

}
