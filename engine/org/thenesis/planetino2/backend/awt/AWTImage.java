package org.thenesis.planetino2.backend.awt;

import java.awt.image.BufferedImage;

import org.thenesis.planetino2.graphics.Image;

public class AWTImage implements Image {
	
	private java.awt.Image image;
	private BufferedImage bufferedImage;
	
	public AWTImage(java.awt.Image image) {
		this.image = image;
		bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		bufferedImage.getGraphics().drawImage(image, 0, 0, null);
	}

	public int getWidth() {
		return bufferedImage.getWidth();
	}

	public int getHeight() {
		return bufferedImage.getHeight();
	}

	public void getRGB(int[] rgbData, int x, int y, int width, int height) {
		bufferedImage.getRGB(0, 0, width, height, rgbData, 0, width);
	}

}
