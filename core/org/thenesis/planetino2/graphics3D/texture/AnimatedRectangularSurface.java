package org.thenesis.planetino2.graphics3D.texture;

public class AnimatedRectangularSurface extends Texture {
	
	private ShadedTexture texture;
	private int[] rgbBuffer;
	private int imageWidth;
	private int imageHeight;
	private int imageSize;
	private int imageCount;
	private int imageIndex;

	/**
	 * An animated Texture
	 * @param rgbBuffer
	 * @param width
	 * @param height
	 */
	public AnimatedRectangularSurface(ShadedTexture texture, int rectangleWidth, int rectangleHeight) {
		super(rectangleWidth, rectangleHeight);
		this.texture = texture;
//		this.imageWidth = texture.getWidth();
//		this.imageHeight = texture.getHeight();
//		this.imageSize = imageWidth * imageHeight;
//		this.imageCount = 1;
		
		this.imageWidth = texture.getWidth();
		this.imageHeight = imageWidth;
		this.imageSize = imageWidth * imageHeight;
		if ((texture.getHeight() % imageWidth) == 0) {
			this.imageCount = texture.getHeight() / imageWidth;
		} else {
			this.imageCount = 1;
		}	
		
		//System.out.println("rectangleWidth=" + rectangleWidth + " rectangleHeight=" + rectangleHeight);
		//System.out.println("imageWidth=" + imageWidth + " texture.getHeight()=" + texture.getHeight() + " image count=" + imageCount);
		
	}
	
	public int getImageCount() {
		return imageCount;
	}
	
	public int getImageIndex() {
		return imageIndex;
	}
	
	public void setImageIndex(int index) {
		this.imageIndex = index;
	}

	@Override
	public int getColor(int x, int y) {
		// Convert polygon coordinates in image coordinates. 
		float ratioX = ((float)x) / width;
		float ratioY = ((float)y) / height;
		x = (int) (ratioX * imageWidth);
		int offsetY = imageIndex * imageHeight; 
		y = (int) (ratioY * imageHeight) + offsetY;
		
		//int index = imageSize * imageIndex + y * imageWidth + x;
		//return rgbBuffer[index];
		//System.out.println("x=" + x + " y=" + y);
		return texture.getColor(x, y, ShadedTexture.MAX_LEVEL);
	}

}
