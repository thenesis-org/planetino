package org.thenesis.planetino2.backend.awt;

import java.awt.Canvas;
import java.awt.FontMetrics;

import org.thenesis.planetino2.graphics.Font;

public class AWTFont implements Font {
	
	private static Canvas canvas = new Canvas();
	java.awt.Font nativeFont;
	private FontMetrics metrics;
	
	public AWTFont(java.awt.Font nativeFont) {
		metrics = canvas.getFontMetrics(nativeFont);
	}

	public int getHeight() {
		return metrics.getHeight();
	}

	public int charsWidth(char[] charArray, int offset, int length) {
		return metrics.charsWidth(charArray, offset, length);
	}

}
