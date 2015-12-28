package org.thenesis.planetino2.math3D;

import org.thenesis.planetino2.graphics3D.texture.AnimatedRectangularSurface;
import org.thenesis.planetino2.math3D.ObjectLoader.Material;

public class PosterPolygonGroup extends PolygonGroup {
	
	private float posterHeight;
	private Vector3D edge;
	private Material posterMaterial;
	private int framesPerSecond;
	private int milisecondsPerFrame;

	public PosterPolygonGroup(Vector3D location, Vector3D edge, float height, Material material) {
		super();
		this.edge = edge;
		this.posterHeight = height;
		this.posterMaterial = material;
		setFramesPerSecond(25);
		build(location);
	}

	public void build(Vector3D location) {
		float h = posterHeight;
    	Vector3D v0 = new Vector3D(0 , 0, 0);
    	Vector3D v1 = new Vector3D(edge);
    	v1.y = location.y; // Force poster to be a rectangle
    	v1.subtract(location);
    	Vector3D v2 = new Vector3D(v1.x , v1.y + h, v1.z);
    	Vector3D v3 = new Vector3D(v0.x , v0.y + h, v0.z);
    	TexturedPolygon3D polygon = new TexturedPolygon3D(v0, v1, v2, v3);

    	buildSurface(polygon, posterMaterial);
    	
    	// Add the polygon group to the object list
    	setFilename("poster_internal.obj");
    	
    	addPolygon(polygon);
    	getTransform().getLocation().setTo(location);
    	
    	Rectangle3D br = polygon.calcBoundingRectangle();

	}
	
	/**
	 * Create a texture and stretched it to fill the rectangle.
	 * @param polygon
	 * @param material
	 */
	public void buildSurface(TexturedPolygon3D polygon, Material material) {
		
		Vector3D v0 = new Vector3D(polygon.getVertex(0));
		Vector3D v1 = new Vector3D(polygon.getVertex(1));
		Vector3D v3 = new Vector3D(polygon.getVertex(3));
		
		float w = v1.length();
		float h = v3.y - v0.y;
		
		if(material.texture != null) {
//    		polygon.setTexture(currentMaterial.texture);
//    		Rectangle3D boundingRect = polygon.calcBoundingRectangle();
//    		int rectW = (int) Math.floor(boundingRect.getWidth() + 0.5d);
//    		int rectH = (int) Math.floor(boundingRect.getHeight() + 0.5d);
    		int rectW = (int) Math.floor(w + 0.5d);
    		int rectH = (int) Math.floor(h + 0.5d);
    		AnimatedRectangularSurface rectTexture = new AnimatedRectangularSurface(posterMaterial.texture, rectW, rectH);
    		v0.subtract(v3);
    		//Rectangle3D textureBounds = new Rectangle3D(v3, v1, v0, rectW, rectH); 
    		Rectangle3D textureBounds = new Rectangle3D(v3, v1, v0, rectW, rectH); 
    		System.out.println("rectW= " + rectW + " rectH=" + rectH);
    		polygon.setTexture(rectTexture, textureBounds);
    	}
		
	}
	
	public int getFramesPerSecond() {
		return framesPerSecond;
	}

	public void setFramesPerSecond(int framesPerSecond) {
		this.framesPerSecond = framesPerSecond;
		milisecondsPerFrame = (int) (1000.0f / framesPerSecond);
	}

	//@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		int frames = (int) (elapsedTime / milisecondsPerFrame);
		
	}
	

}
