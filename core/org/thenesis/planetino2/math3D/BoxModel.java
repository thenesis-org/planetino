package org.thenesis.planetino2.math3D;

import org.thenesis.planetino2.graphics3D.texture.ShadedTexture;
import org.thenesis.planetino2.loader.ObjectLoader.Material;
import org.thenesis.planetino2.loader.QBLoader;

public class BoxModel {
	
	public static final int FACES = 6;
	
	public static final int UP = 0;
	public static final int DOWN = 1;
	public static final int NORTH = 2;
	public static final int SOUTH = 3;
	public static final int EAST = 4;
	public static final int WEST = 5;
	
	private String name;
	
	FaceModel[] faceModels;
	
	protected BoxModel() {
		faceModels = new FaceModel[FACES];
	}

	public static BoxModel createBoxDef(String name) {
		BoxModel boxDef = new BoxModel();
		boxDef.name = name;
		return boxDef;
	}
	
	public void setFaceModel(int type, Material material, boolean animated, boolean stretched, float frameRate) {
		if (faceModels[type] == null) {
			faceModels[type] = FaceModel.createFaceModel(type);
		}
		faceModels[type].setProperties(material, animated, stretched, frameRate);
	}
	
	public FaceModel[] getFaceModels() {
		return faceModels;
	}
	
	public String getName() {
		return name;
	}

	//@Override
	public String toString() {
		return "BoxModel [name=" + name + "]";
	}
	
	
	
	public static class FaceModel extends TexturedPolygon3D {
		
		public static final int SIZE = 1;
		public static final int SIZE_2 = SIZE * 2;
		
		public static final String[] TYPE_STRINGS = new String[] { "UP", "DOWN", "NORTH", "SOUTH", "EAST", "WEST" };
		
		public static Vector3D v1_up = new Vector3D(-SIZE, SIZE, SIZE); 
		public static Vector3D v2_up = new Vector3D(SIZE, SIZE, SIZE); 
		public static Vector3D v3_up = new Vector3D(SIZE, SIZE, -SIZE);
		public static Vector3D v4_up = new Vector3D(-SIZE, SIZE, -SIZE); 
		public static Vector3D v1_down = new Vector3D(-SIZE, -SIZE, SIZE); 
		public static Vector3D v2_down = new Vector3D(SIZE, -SIZE, SIZE); 
		public static Vector3D v3_down = new Vector3D(SIZE, -SIZE, -SIZE);
		public static Vector3D v4_down = new Vector3D(-SIZE, -SIZE, -SIZE); 
		
//		public static Vector3D v1_up = new Vector3D(0, SIZE_2, SIZE_2); 
//		public static Vector3D v2_up = new Vector3D(SIZE_2, SIZE_2, SIZE_2); 
//		public static Vector3D v3_up = new Vector3D(SIZE_2, SIZE_2, 0);
//		public static Vector3D v4_up = new Vector3D(0, SIZE_2, 0); 
//		public static Vector3D v1_down = new Vector3D(0, 0, SIZE_2); 
//		public static Vector3D v2_down = new Vector3D(SIZE_2, 0, SIZE_2); 
//		public static Vector3D v3_down = new Vector3D(SIZE_2, 0, 0);
//		public static Vector3D v4_down = new Vector3D(0, 0, 0); 
		
		protected int type;
		protected Material material;
		protected boolean animated;
		protected boolean stretched;
		protected float framesPerSecond;
		
		
		//up/down/north/south/east/west <texture_name> <static/animated> <stretch/repeat> [frame_rate] 
		
		public static FaceModel createFaceModel(int type) {
			switch (type) {
			case UP:
				return new FaceModel(type, v1_up, v2_up, v3_up, v4_up);
			case DOWN:
				return new FaceModel(type, v4_down, v3_down, v2_down, v1_down);
			case NORTH:
				return new FaceModel(type, v4_up, v3_up, v3_down, v4_down);
			case SOUTH:
				return new FaceModel(type, v2_up, v1_up, v1_down, v2_down);
			case EAST:
				return new FaceModel(type, v3_up, v2_up, v2_down, v3_down);
			case WEST:
				return new FaceModel(type, v1_up, v4_up, v4_down, v1_down);
			default:
				throw new IllegalStateException();
			}
		}
		
		FaceModel() {
		}

		private FaceModel(int type, Vector3D v1, Vector3D v2, Vector3D v3, Vector3D v4) {
			super(v1, v2, v3, v4);
			this.type = type;
		}
		
		public void setProperties(Material material, boolean animated, boolean stretched, float framesPerSecond) {
			this.material = material;
			this.animated = animated;
			this.stretched = stretched;
			this.framesPerSecond = framesPerSecond;
		}
		
		public int getType() {
			return type;
		}
		
		public String getTypeString() {
			return getTypeString(type); 
		}
		
		public static String getTypeString(int type) {
			return TYPE_STRINGS[type];
		}
		
		public static int getType(String typeString) {
			for (int i = 0; i < TYPE_STRINGS.length; i++) {
				if (typeString.equalsIgnoreCase(TYPE_STRINGS[i])) {
					return i;
				}
			}
			throw new IllegalStateException();
		}
		
		public float getFramesPerSecond() {
			return framesPerSecond;
		}

		//@Override
		public String toString() {
			return "Face [type=" + type + "]";
		}
		
	}
	
	

}
