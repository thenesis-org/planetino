package org.thenesis.planetino2.graphics;

import java.io.IOException;

import org.thenesis.planetino2.input.InputManager;
import org.thenesis.planetino2.sound.SoundPlayer;

public abstract class Toolkit {

	private static Toolkit toolkit;
	
	public static void setToolkit(Toolkit toolkit) {
		Toolkit.toolkit = toolkit; 
	}
	
	public static Toolkit getInstance() {
		return toolkit;
	}

	public abstract Image createImage(String string) throws IOException;
	
	public abstract Screen getScreen(int widthHint, int heightHint);
	
	public abstract Font getDefaultFont();
	
	public abstract InputManager getInputManager();
	
	public abstract SoundPlayer getSoundPlayer();

}
