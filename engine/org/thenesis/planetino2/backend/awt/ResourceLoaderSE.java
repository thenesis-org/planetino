package org.thenesis.planetino2.backend.awt;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.thenesis.planetino2.graphics.Image;
import org.thenesis.planetino2.graphics.Toolkit;
import org.thenesis.planetino2.loader.ResourceLoader;

public class ResourceLoaderSE implements ResourceLoader {

	public ResourceLoaderSE() {
	}
	
	static String getResourceDirectory() {
		return "/res/";
	}

	public InputStream getInputStream(String resourceName) {
		return ResourceLoaderSE.class.getResourceAsStream(getResourceDirectory() + resourceName);
	}

	public Image loadImage(String resourceName) throws IOException {
		InputStream is = getInputStream(resourceName);
		return Toolkit.getInstance().createImage(is);
	}

}
