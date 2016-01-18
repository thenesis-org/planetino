package org.thenesis.planetino2.graphics3D.texture;

public class AnimatedRectangularSurface extends Texture {
	
	protected static final int DEFAULT_SHADE_LEVEL = ShadedTexture.MAX_LEVEL * 7 / 10;
	protected ShadedTexture texture;
	protected int imageWidth;
	protected int imageHeight;
	private int imageSize;
	private int imageCount;
	protected int imageIndex;
	protected int shadeLevel;
	
	protected int offsetY;

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
		
		setImageIndex(0);
		setShadeLevel(DEFAULT_SHADE_LEVEL);
		
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
		offsetY = imageIndex * imageHeight;
	}

	@Override
	public int getColor(int x, int y) {
		return texture.getColor(x, offsetY + y, shadeLevel);
	}
	
	public void setShadeLevel(int shadeLevel) {
		this.shadeLevel = shadeLevel;
	}

}
