package org.thenesis.planetino2.backend.awt;

import java.awt.event.KeyEvent;
import java.io.IOException;

import org.thenesis.planetino2.graphics.Font;
import org.thenesis.planetino2.graphics.Image;
import org.thenesis.planetino2.graphics.Screen;
import org.thenesis.planetino2.graphics.Toolkit;
import org.thenesis.planetino2.input.InputManager;

public class AWTToolkit extends Toolkit {
	
	private AWTFont font;
	private InputManager inputManager;
	private AWTScreen awtScreen;
	
	@Override
	public Image createImage(String path) throws IOException {
		java.awt.Image nativeImage = java.awt.Toolkit.getDefaultToolkit().createImage(path);
		return new AWTImage(nativeImage);
	}

	@Override
	public Screen getScreen() {
		if (awtScreen == null) {
			awtScreen = new AWTScreen(getInputManager());
		}
		return awtScreen;
	}

	@Override
	public Font getDefaultFont() {
		if (font == null) {
			java.awt.Font nativeFont  = new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, 12);
			font = new AWTFont(nativeFont);
		}
		return font;
	}

	@Override
	public InputManager getInputManager() {
		if (inputManager == null) {
			inputManager = new InputManager() {
				public String getKeyName(int keyCode) {
					return KeyEvent.getKeyText(keyCode);
				}
			};
		}
		return inputManager;
	}

}
