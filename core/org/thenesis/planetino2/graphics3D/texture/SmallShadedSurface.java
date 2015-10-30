package org.thenesis.planetino2.graphics3D.texture;

import org.thenesis.planetino2.math3D.Rectangle3D;
import org.thenesis.planetino2.math3D.TexturedPolygon3D;
import org.thenesis.planetino2.math3D.Vector3D;

public class SmallShadedSurface extends Texture {
	
//	private int widthBits;
//	private int widthMask;
//	private int heightBits;
//	private int heightMask;
	
	private ShadedTexture sourceTexture;
	private Vector3D origin;
	private int sourceTextureEdgeLength; 
	private static final int surfaceEdgeLength = 2;

	public SmallShadedSurface(ShadedTexture texture, Rectangle3D vertexBounds, Vector3D[] texels) {
		super(surfaceEdgeLength, surfaceEdgeLength);
		this.sourceTexture = texture;
		this.origin = texels[0];
		sourceTextureEdgeLength = sourceTexture.getWidth(); // MUST be equal to sourceTexture.getHeight()
		origin.multiply(sourceTextureEdgeLength);
	}
	

	@Override
	public int getColor(int x, int y) {
		int srcX = (int) origin.x; //(currentMaterial.texture.getWidth() - origin.x); // // 
		int srcY = (int) (sourceTextureEdgeLength - origin.y);
		if (srcX < 0) srcX = 0;
		if (srcY < 0) srcY = 0;
		if (srcX >= sourceTextureEdgeLength) srcX = sourceTextureEdgeLength - 1;
		if (srcY >= sourceTextureEdgeLength) srcY = sourceTextureEdgeLength - 1;
		int color = sourceTexture.getColor(srcX, srcY, ShadedTexture.MAX_LEVEL * 2 / 3);
		return color;
	}

	public static SmallShadedSurface createSurface(TexturedPolygon3D texturedPolygon, ShadedTexture texture, Vector3D[] texels) {
		Vector3D origin = texturedPolygon.getVertex(0);
		Vector3D dv = new Vector3D(texturedPolygon.getVertex(1));
		dv.subtract(origin);
		Vector3D du = new Vector3D();
		du.setToCrossProduct(texturedPolygon.getNormal(), dv);
		Rectangle3D vertexBounds = new Rectangle3D(origin, du, dv, 0, 0);
		
		SmallShadedSurface surface = new SmallShadedSurface(texture, vertexBounds, texels);
		texturedPolygon.setTexture(surface, vertexBounds);
		return surface;
	}
	
	

}
