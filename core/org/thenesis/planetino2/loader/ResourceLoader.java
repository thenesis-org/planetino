package org.thenesis.planetino2.loader;

import java.io.IOException;
import java.io.InputStream;

import org.thenesis.planetino2.graphics.Image;

public interface ResourceLoader {
	
	public InputStream getInputStream(String resourceName);
	
	public Image loadImage(String resourceName) throws IOException;

}
