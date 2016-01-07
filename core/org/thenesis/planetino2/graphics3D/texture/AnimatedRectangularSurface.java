package org.thenesis.planetino2.graphics3D.texture;

public class AnimatedRectangularSurface extends Texture {
	
	protected ShadedTexture texture;
	protected int imageWidth;
	protected int imageHeight;
	private int imageSize;
	private int imageCount;
	protected int imageIndex;

	/**
	 * An animated Texture
	 * @param rgbBuffer
	 * @param width
	 * @param height
	 */
	public AnimatedRectangularSurface(ShadedTexture texture, int rectangleWidth, int rectangleHeight) {
		super(rectangleWidth, rectangleHeight);
		this.texture = texture;
		
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
		int offsetY = imageIndex * imageHeight; 
		return texture.getColor(x, offsetY + y, ShadedTexture.MAX_LEVEL);
	}

}
