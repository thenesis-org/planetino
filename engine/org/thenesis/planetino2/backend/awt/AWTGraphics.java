package org.thenesis.planetino2.backend.awt;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import org.thenesis.planetino2.graphics.Font;
import org.thenesis.planetino2.graphics.Graphics;
import org.thenesis.planetino2.graphics.Toolkit;

public class AWTGraphics implements Graphics {
	
	private BufferedImage bufferedImage;
	private int[] imageData;
	private java.awt.Graphics nativeGraphics;
	
	public AWTGraphics(BufferedImage bufferedImage) {
		this.bufferedImage = bufferedImage;
		this.imageData = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
		this.nativeGraphics = bufferedImage.getGraphics();
		setFont(Toolkit.getInstance().getDefaultFont());
	}

	public void setColor(int rgb) {
		nativeGraphics.setColor(new Color(rgb));
	}

	public void drawString(String text, int x, int y) {
		nativeGraphics.drawString(text, x, y);
	}

	public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha) {
		//bufferedImage.setRGB(0, 0, width, height, rgbData, 0, width); // Slow !
		System.arraycopy(rgbData, offset, imageData, 0, rgbData.length);
	}

	public void fillRect(int x, int y, int width, int height) {
		nativeGraphics.fillRect(x, y, width, height);
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		nativeGraphics.drawLine(x1, y1, x2, y2);
	}

	public Font getFont() {
		java.awt.Font nativeFont = nativeGraphics.getFont();
		return new AWTFont(nativeFont);
	}

	public int getColor() {
		return nativeGraphics.getColor().getRGB();
	}

	public void setFont(Font prevFont) {
		nativeGraphics.setFont(((AWTFont)prevFont).nativeFont);
	}

}
