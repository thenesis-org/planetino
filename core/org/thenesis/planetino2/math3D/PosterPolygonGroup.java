package org.thenesis.planetino2.math3D;

import org.thenesis.planetino2.graphics3D.texture.AnimatedRectangularSurface;
import org.thenesis.planetino2.math3D.ObjectLoader.Material;

public class PosterPolygonGroup extends PolygonGroup {
	
	public static final int TYPE_UNKNOWN = 0;
	public static final int TYPE_WALL = 1;
	public static final int TYPE_FLOOR = 2;
	public static final int TYPE_CEIL = 3;
	
	public static final float FRAMES_PER_SECOND_DEFAULT = 25;
	
	public static final String POSTER_FILENAME = "poster_internal.obj";
	
	private int type;
	private Vector3D location;
	private float posterHeight;
	private Vector3D edge;
	private Material posterMaterial;
	private float framesPerSecond;
	private float milisecondsPerFrame;
	private TexturedPolygon3D polygon;
	private long timeSinceLastFrame;

	public PosterPolygonGroup(int type, Vector3D location, Vector3D edge, float height, Material material, float framesPerSecond) {
		super();
		this.location = location;
		this.type = type;
		this.edge = edge;
		this.posterHeight = height;
		this.posterMaterial = material;
		this.timeSinceLastFrame = 0;
		setFramesPerSecond(framesPerSecond);
		build(location);
	}

	public void build(Vector3D location) {
		float h = posterHeight;
    	Vector3D v0 = new Vector3D(0 , 0, 0);
    	Vector3D v1 = new Vector3D(edge);
    	v1.y = location.y; // Force poster to be a rectangle
    	v1.subtract(location);
    	Vector3D v2;
    	Vector3D v3;
    	if (type == TYPE_WALL) {
    		v2 = new Vector3D(v1.x , v1.y + h, v1.z);
    		v3 = new Vector3D(v0.x , v0.y + h, v0.z);
    		polygon = new TexturedPolygon3D(v0, v1, v2, v3);
    	} else if (type == TYPE_FLOOR){
    		v2 = new Vector3D(v1.x , v1.y, v1.z + h);
    		v3 = new Vector3D(v0.x , v0.y, v0.z + h);
    		polygon = new TexturedPolygon3D(v0, v1, v2, v3);
    	} else if (type == TYPE_CEIL){
    		v2 = new Vector3D(v1.x , v1.y, v1.z + h);
    		v3 = new Vector3D(v0.x , v0.y, v0.z + h);
    		//polygon = new TexturedPolygon3D(v1, v0, v3, v2);
    		polygon = new TexturedPolygon3D(v0, v3, v2, v1);
    	} else {
    		System.out.println("[ERROR] Poster type unknown. Set to type wall as default");
    		return;
    	}

    	buildSurface(polygon, posterMaterial);
    	
    	// Add the polygon group to the object list
    	setFilename(POSTER_FILENAME);
    	
    	addPolygon(polygon);
    	getTransform().getLocation().setTo(location);    	

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
		
		//float w = v1.length();
		//float h = posterHeight;
		float w = (float) Math.sqrt(v1.getDistanceSq(v0));
		float h = (float) Math.sqrt(v3.getDistanceSq(v0));
		
		
		if(material.texture != null) {
//    		polygon.setTexture(currentMaterial.texture);
//    		Rectangle3D boundingRect = polygon.calcBoundingRectangle();
//    		int rectW = (int) Math.floor(boundingRect.getWidth() + 0.5d);
//    		int rectH = (int) Math.floor(boundingRect.getHeight() + 0.5d);
    		int rectW = (int) Math.floor(w + 0.5d);
    		int rectH = (int) Math.floor(h + 0.5d);
    		AnimatedRectangularSurface rectTexture = new AnimatedRectangularSurface(posterMaterial.texture, rectW, rectH);
    		v0.subtract(v3);
    		Rectangle3D textureBounds = new Rectangle3D(v3, v1, v0, rectW, rectH); 
    		polygon.setTexture(rectTexture, textureBounds);
    	}
		
	}
	
	public float getFramesPerSecond() {
		return framesPerSecond;
	}

	public void setFramesPerSecond(float framesPerSecond) {
		this.framesPerSecond = framesPerSecond;
		milisecondsPerFrame = 1000.0f / framesPerSecond;
	}

	public void updateImage(long elapsedTime) {
		timeSinceLastFrame += elapsedTime;
		int frames = (int) (timeSinceLastFrame / milisecondsPerFrame);
		if (frames > 0) {
			//System.out.println("timeSinceLastFrame=" + timeSinceLastFrame + " milisecondsPerFrame=" + milisecondsPerFrame + " frames=" + frames);
			AnimatedRectangularSurface rectTexture = (AnimatedRectangularSurface) polygon.getTexture();
			int index = rectTexture.getImageIndex() + frames;
			if (index >= rectTexture.getImageCount()) {
				index = 0;
			}
			rectTexture.setImageIndex(index);
			timeSinceLastFrame = 0;
		}
	}

	public int getType() {
		return type;
	}

	public Vector3D getLocation() {
		return getTransform().getLocation();
	}

	public float getPosterHeight() {
		return posterHeight;
	}

	public Vector3D getEdge() {
		resetIterator();
		Polygon3D polygon = new Polygon3D();
		nextPolygonTransformed(polygon);
		if (type == TYPE_CEIL) {
			return polygon.getVertex(3);
		} else {
			return polygon.getVertex(1); 
		}
	}

	public Material getPosterMaterial() {
		return posterMaterial;
	}
	
	
	

}
