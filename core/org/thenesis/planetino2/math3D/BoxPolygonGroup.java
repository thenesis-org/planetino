package org.thenesis.planetino2.math3D;

import org.thenesis.planetino2.graphics3D.texture.AnimatedRectangularSurface;
import org.thenesis.planetino2.graphics3D.texture.ShadedSurface;
import org.thenesis.planetino2.graphics3D.texture.StretchedAnimatedRectangularSurface;
import org.thenesis.planetino2.math3D.BoxModel.FaceModel;
import org.thenesis.planetino2.util.Vector;

public class BoxPolygonGroup extends PolygonGroup implements Lightable {

	public static final String BOX_FILENAME = "box_internal.obj";

	private BoxModel boxDef;
	private float ambientLightIntensity;
	private boolean isSkybox;

	private float scaleX, scaleY, scaleZ;

	private Face[] faces;
	private static Polygon3D cachedTransformedPolygonForLightning = new Polygon3D();

	public BoxPolygonGroup(BoxModel boxDef, Vector3D location, float scale, float ambientLightIntensity, boolean isSkybox) {
		this(boxDef, location, scale, scale, scale, ambientLightIntensity, isSkybox);
	}

	public BoxPolygonGroup(BoxModel boxDef, Vector3D location, float scale, float ambientLightIntensity) {
		this(boxDef, location, scale, ambientLightIntensity, false);
	}

	public BoxPolygonGroup(BoxModel boxDef, Vector3D location, float scaleX, float scaleY, float scaleZ, float ambientLightIntensity, boolean isSkybox) {
		this.boxDef = boxDef;
		this.ambientLightIntensity = ambientLightIntensity;
		this.isSkybox = isSkybox;
		faces = new Face[BoxModel.FACES];
		setFilename(BOX_FILENAME);

		FaceModel[] models = boxDef.getFaceModels();
		for (int i = 0; i < BoxModel.FACES; i++) {
			FaceModel model = models[i];
			if (model != null) {
				Face face = new Face(model);
				if (isSkybox) {
					face.flip();
				}
				faces[i] = face;
				addPolygon(face);
			}
		}
		// Appliquer l'échelle non uniforme
		setScale(scaleX, scaleY, scaleZ);
		getTransform().getLocation().setTo(location);
		rebuild();
	}

	public void setScale(float scale) {
		setScale(scale, scale, scale);
	}

	/**
	 * Définit une échelle non uniforme pour chaque axe.
	 */
	public void setScale(float scaleX, float scaleY, float scaleZ) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.scaleZ = scaleZ;

		resetIterator();
		while (hasNext()) {
			Polygon3D polygon = nextPolygon();
			int numVertices = polygon.getNumVertices();
			for (int i = 0; i < numVertices; i++) {
				Vector3D v = polygon.getVertex(i);
				v.x *= scaleX;
				v.y *= scaleY;
				v.z *= scaleZ;
			}
		}
	}

	public void rebuild() {
		for (int i = 0; i < BoxModel.FACES; i++) {
			Face face = faces[i];
			if (face != null) {
				face.rebuild();
			}
		}
	}

	public void updateImage(long elapsedTime) {
		for (int i = 0; i < BoxModel.FACES; i++) {
			Face face = faces[i];
			if (face != null) {
				face.updateImage(elapsedTime);
			}
		}
	}

	/**
	 * Basic implementation: only one light intensity is processed for each face of the box
	 * 
	 * @param pointLights
	 * @param ambientLightIntensity
	 */
	public void applyLights(Vector pointLights, float ambientLightIntensity) {
		Polygon3D transformedPolygon = cachedTransformedPolygonForLightning;
		for (int i = 0; i < BoxModel.FACES; i++) {
			Face face = faces[i];
			if (face != null) {
				transformedPolygon.setTo(face);
				transformedPolygon.add(getTransform());
				Vector3D normal = transformedPolygon.calcNormal();
				Vector3D point = new Vector3D(transformedPolygon.getVertex(0));
//				System.out.println("point=" + point + " normal=" + normal);
				byte shadeLevel = ShadedSurface.calcShade(normal, point, pointLights, ambientLightIntensity);
//				System.out.println("face " + i + " shadelevel: " + shadeLevel);
				AnimatedRectangularSurface surface = (AnimatedRectangularSurface) face.getTexture();
				surface.setShadeLevel(shadeLevel);
			}
		}
	}

	public float getAmbientLightIntensity() {
		return ambientLightIntensity;
	}

	@Override
	public String toString() {
		return "Box " + getName();
	}

	public boolean isSkybox() {
		return isSkybox;
	}

	public class Face extends FaceModel {

		private float framesPerSecond;
		private float milisecondsPerFrame;
		private long timeSinceLastFrame;

		/* Use to create a real face from a face model */
		Face(FaceModel f) {
			super();
			setTo(f);
			this.type = f.type;
			this.material = f.material;
			this.animated = f.animated;
			this.stretched = f.stretched;
			this.timeSinceLastFrame = 0;
			setFramesPerSecond(f.framesPerSecond);
		}

		public void rebuild() {

			Vector3D origin = new Vector3D(getVertex(0));
			Vector3D u = new Vector3D(getVertex(1));
			Vector3D v = new Vector3D(getVertex(3));

			float w = (float) Math.sqrt(u.getDistanceSq(origin));
			float h = (float) Math.sqrt(v.getDistanceSq(origin));

			u.subtract(origin);
			v.subtract(origin);

			// System.out.println(origin + " , " + u + " , " + v);

			if (material.texture != null) {
//				setTexture(material.texture);
				int rectW = (int) Math.floor(w + 0.5d);
				int rectH = (int) Math.floor(h + 0.5d);
				AnimatedRectangularSurface rectTexture;
				if (stretched) {
					rectTexture = new StretchedAnimatedRectangularSurface(material.texture, rectW, rectH);
				} else {
					rectTexture = new AnimatedRectangularSurface(material.texture, rectW, rectH);
				}

				Rectangle3D textureBounds = new Rectangle3D(origin, u, v, rectW, rectH);
				setTexture(rectTexture, textureBounds);
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
			// System.out.println("timeSinceLastFrame=" + timeSinceLastFrame + " milisecondsPerFrame=" + milisecondsPerFrame + " frames=" + frames);
			if (frames > 0) {
				// System.out.println("timeSinceLastFrame=" + timeSinceLastFrame + " milisecondsPerFrame=" + milisecondsPerFrame + " frames=" + frames);
				AnimatedRectangularSurface rectTexture = (AnimatedRectangularSurface) getTexture();
				int index = rectTexture.getImageIndex() + frames;
				if (index >= rectTexture.getImageCount()) {
					index = 0;
				}
				rectTexture.setImageIndex(index);
				timeSinceLastFrame = 0;
			}
		}

		public void flip() {
			int numVertices = getNumVertices();
			Vector3D[] invertedV = new Vector3D[numVertices];
			if (getType() == BoxModel.UP) {
				invertedV[0] = getVertex(2);
				invertedV[1] = getVertex(1);
				invertedV[2] = getVertex(0);
				invertedV[3] = getVertex(3);
			} else if (getType() == BoxModel.DOWN) {
				invertedV[0] = getVertex(0);
				invertedV[1] = getVertex(3);
				invertedV[2] = getVertex(2);
				invertedV[3] = getVertex(1);
			} else {
				invertedV[0] = getVertex(1);
				invertedV[1] = getVertex(0);
				invertedV[2] = getVertex(3);
				invertedV[3] = getVertex(2);
			}
			setTo(invertedV);
		}
	}

}
