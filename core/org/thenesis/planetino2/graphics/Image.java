package org.thenesis.planetino2.graphics;

public interface Image {

	public int getWidth();

	public int getHeight();

	/**
	 * @param rgbData an array of integers in which the ARGB pixel data is stored
	 * @param x the x-coordinate of the upper left corner of the region
	 * @param y the y-coordinate of the upper left corner of the region
	 * @param width the width of the region
	 * @param height the height of the region 
	 */
	public void getRGB(int[] rgbData, int x, int y, int width, int height);

}