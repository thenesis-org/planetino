package org.thenesis.planetino2.graphics;

public interface Graphics {

	public static final int LEFT = 0;
	public static final int TOP = 0;

	public void setColor(int rgb);

	public void drawString(String text, int x, int y);

	public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha);

	public void fillRect(int leftOffset, int topOffset, int width, int height);

	public void drawLine(int left2, int y, int right, int y2);

	public Font getFont();

	public int getColor();

	public void setFont(Font prevFont);

}