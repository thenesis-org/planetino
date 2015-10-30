package org.thenesis.planetino2.graphics3D.texture;

import org.thenesis.planetino2.math3D.Rectangle3D;
import org.thenesis.planetino2.math3D.TexturedPolygon3D;
import org.thenesis.planetino2.math3D.Vector3D;

public class SmallShadedSurface extends Texture {
	
//	private int widthBits;
//	private int widthMask;
//	private int heightBits;
//	private int heightMask;
	
	private static final int surfaceEdgeLength = 2;
	
//	private int[] buffer; 
	private int meanColor;
	
	private ShadedTexture sourceTexture;
	private int sourceTextureEdgeLength; 
	

	public SmallShadedSurface(ShadedTexture texture, Rectangle3D vertexBounds, Vector3D[] texels) {
		super(surfaceEdgeLength, surfaceEdgeLength);
//		this.widthBits = countbits(width);
//		this.heightBits = countbits(height);
//		this.widthMask = width - 1;
//		this.heightMask = height - 1;
		
		this.sourceTexture = texture;
		sourceTextureEdgeLength = sourceTexture.getWidth(); // MUST be equal to sourceTexture.getHeight()
		
		// Get the real texel coordinates in the source texture
		setTargetTexelCoordinates(texels);
		
		meanColor = getMeanColor(texels); 
		
//		buffer = new int[surfaceEdgeLength * surfaceEdgeLength];
//		setBufferColor(0, texels[0]); // origin
//		setBufferColor(1, texels[1]);
//		setBufferColor(2, texels[2]);
//		buffer[3] = buffer[0]; // FIXME interpolate
	}
	
	private void setTargetTexelCoordinates(Vector3D[] texels) {
		int size = texels.length;
		for (int i = 0; i < size; i++) {
			texels[i].multiply(sourceTextureEdgeLength);
			//texels[i].x = (int) texels[i].x; 
			texels[i].y = (int) (sourceTextureEdgeLength - texels[i].y);
			if (texels[i].x < 0) texels[i].x = 0;
			if (texels[i].y < 0) texels[i].y = 0;
			if (texels[i].x >= sourceTextureEdgeLength) texels[i].x = sourceTextureEdgeLength - 1;
			if (texels[i].y >= sourceTextureEdgeLength) texels[i].y = sourceTextureEdgeLength - 1;
		}
	}
	
	private int getMeanColor(Vector3D[] texels) {
		int size = texels.length;
		int meanR = 0;
		int meanG = 0;
		int meanB = 0;
		for (int i = 0; i < size; i++) {
			int color = sourceTexture.getColor((int)texels[i].x, (int)texels[i].y, ShadedTexture.MAX_LEVEL * 2 / 3);
			int r = (color >> 16) & 0xFF;
			int g = (color >> 8) & 0xFF;
			int b = color & 0xFF;
			meanR += r;
			meanG += g;
			meanB += b;
		}
		meanR = meanR / size;
		meanG = meanG / size;
		meanB = meanB / size;
		int meanColor = 0xFF000000 | (meanR << 16) | (meanG << 8) | (meanB);
		return meanColor;
	}
	
//	private void setBufferColor(int bufferIndex, Vector3D texel) {
//		texel.multiply(sourceTextureEdgeLength);
//		int srcX = (int) texel.x; //(currentMaterial.texture.getWidth() - origin.x); // // 
//		int srcY = (int) (sourceTextureEdgeLength - texel.y);
//		if (srcX < 0) srcX = 0;
//		if (srcY < 0) srcY = 0;
//		if (srcX >= sourceTextureEdgeLength) srcX = sourceTextureEdgeLength - 1;
//		if (srcY >= sourceTextureEdgeLength) srcY = sourceTextureEdgeLength - 1;
//		int color = sourceTexture.getColor(srcX, srcY, ShadedTexture.MAX_LEVEL * 2 / 3);
//		buffer[bufferIndex] = color;
//	}
	

	@Override
	public int getColor(int x, int y) {
		return meanColor;
		//return buffer[(x & widthMask) + ((y & heightMask) << widthBits)];
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
