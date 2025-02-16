package org.thenesis.planetino2.backend.awt;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import org.thenesis.planetino2.graphics.Font;
import org.thenesis.planetino2.graphics.Image;
import org.thenesis.planetino2.graphics.Screen;
import org.thenesis.planetino2.graphics.Toolkit;
import org.thenesis.planetino2.input.InputManager;
import org.thenesis.planetino2.loader.ResourceLoader;
import org.thenesis.planetino2.sound.SoundPlayer;

public class AWTToolkit extends Toolkit {
	
	private AWTFont font;
	private InputManager inputManager;
	private AWTScreen awtScreen;
	private SoundPlayerSE soundPlayer;
	private ResourceLoader resourceLoader;
	
	@Override
	public Image createImage(InputStream is) throws IOException {
		java.awt.Image nativeImage =  ImageIO.read(is);
		return new AWTImage(nativeImage);
	}

	@Override
	public Screen getScreen(int widthHint, int heightHint) {
		if (awtScreen == null) {
			awtScreen = new AWTScreen(getInputManager(), widthHint, heightHint);
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
			inputManager = new AWTInputManager();
		}
		return inputManager;
	}
	
	class AWTInputManager extends InputManager {
		
		private Robot robot;
		/**
		 * An invisible cursor.
		 */
		private Cursor INVISIBLE_CURSOR;
		
		public AWTInputManager() {
			INVISIBLE_CURSOR = java.awt.Toolkit.getDefaultToolkit().createCustomCursor(java.awt.Toolkit.getDefaultToolkit().getImage(""), new java.awt.Point(0, 0), "invisible");
		}
		
		public String getKeyName(int keyCode) {
			return KeyEvent.getKeyText(keyCode);
		}

		@Override
		public void setRelativeMouseMode(boolean mode) {
			if (mode) {
				try {
					robot = new Robot();
					mouseLocation.x = awtScreen.panel.getWidth() / 2;
				    mouseLocation.y = awtScreen.panel.getHeight() / 2;
					recenterMouse();
				} catch (AWTException e) {
					isRelativeMouseModeEnabled = false;
					e.printStackTrace();
				}
				isRelativeMouseModeEnabled = true;
			} else {
				isRelativeMouseModeEnabled = false;
			}
			
		}

		@Override
		protected void recenterMouse() {
			if (isRelativeMouseModeEnabled) {
				java.awt.Point p = new java.awt.Point(awtScreen.panel.getWidth() / 2, awtScreen.panel.getHeight() / 2);
				SwingUtilities.convertPointToScreen(p, (Component)awtScreen.panel);
				isRecentering = true;
				centerLocation.x = p.x;
				centerLocation.y = p.y;
				robot.mouseMove(p.x, p.y);
			}
		}

		@Override
		public void showCursor(boolean enabled) {
			if (enabled) {
				awtScreen.panel.setCursor(Cursor.getDefaultCursor());
			} else {
				awtScreen.panel.setCursor(INVISIBLE_CURSOR);
			}
			
		}
		
	}

	@Override
	public SoundPlayer getSoundPlayer() {
		if (soundPlayer == null) {
			soundPlayer = new SoundPlayerSE();
		}
		return soundPlayer;
	}

	@Override
	public ResourceLoader getResourceLoader() {
		if (resourceLoader == null) {
			resourceLoader = new ResourceLoaderSE();
		}
		return resourceLoader;
	}

}
