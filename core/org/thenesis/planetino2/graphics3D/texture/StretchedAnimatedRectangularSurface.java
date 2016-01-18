package org.thenesis.planetino2.graphics3D.texture;

public class StretchedAnimatedRectangularSurface extends AnimatedRectangularSurface {

	public StretchedAnimatedRectangularSurface(ShadedTexture texture, int rectangleWidth, int rectangleHeight) {
		super(texture, rectangleWidth, rectangleHeight);
	}
	
	@Override
	public int getColor(int x, int y) {
		// Convert polygon coordinates in image coordinates. 
		float ratioX = ((float)x) / width;
		float ratioY = ((float)y) / height;
		x = (int) (ratioX * imageWidth);
		y = (int) (ratioY * imageHeight) + offsetY;
	
		return texture.getColor(x, y, shadeLevel);
	}

}
