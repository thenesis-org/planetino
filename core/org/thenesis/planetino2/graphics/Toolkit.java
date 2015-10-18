package org.thenesis.planetino2.graphics;

import java.io.IOException;

import org.thenesis.planetino2.input.InputManager;

public abstract class Toolkit {

	private static Toolkit toolkit;
	
	public static void setToolkit(Toolkit toolkit) {
		Toolkit.toolkit = toolkit; 
	}
	
	public static Toolkit getInstance() {
		return toolkit;
	}

	public abstract Image createImage(String string) throws IOException;
	
	public abstract Screen getScreen();
	
	public abstract Font getDefaultFont();
	
	public abstract InputManager getInputManager();

}
