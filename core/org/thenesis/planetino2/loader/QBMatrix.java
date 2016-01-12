package org.thenesis.planetino2.loader;

public class QBMatrix {

	private String name;
	// read matrix size 
	private int sizeX;
	private int sizeY;
	private int sizeZ;
	private int posX;
	private int posY;
	private int posZ;
	private int[] data;
	private long colorFormat; // uint32
	private long zAxisOrientation; // uint32
	private long visibilityMaskEncoded; // uint32
	
	public QBMatrix(String name, int sizeX, int sizeY, int sizeZ, int posX, int posY, int posZ, int[] data, long colorFormat, long zAxisOrientation, long visibilityMaskEncoded) {
		this.name = name;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.data = data;
		this.colorFormat = colorFormat;
		this.zAxisOrientation = zAxisOrientation;
		this.visibilityMaskEncoded = visibilityMaskEncoded;
	}

	public String getName() {
		return name;
	}

	public int getSizeX() {
		return sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	public int getSizeZ() {
		return sizeZ;
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	public int getPosZ() {
		return posZ;
	}

	public int[] getData() {
		return data;
	}
	
	public long getColorFormat() {
		return colorFormat;
	}

	public long getVisibilityMask() {
		return visibilityMaskEncoded;
	}
	
	public long getZAxisOrientation() {
		return zAxisOrientation;
	}
	
	public int getRGBColor(int color) {
		int rbgColor;
		if (getColorFormat() == QBLoader.COLOR_FORMAT_RGBA) {
			rbgColor = ((color >> 8) & 0x00FFFFFF) | 0xFF000000;
		} else { //QBLoader.COLOR_FORMAT_BGRA
			int r, g, b;
			b = (color >> 24) & 0xFF;
			g = (color >> 16) & 0xFF;
			r = (color >> 8) & 0xFF;
			rbgColor = (0xFF << 24) | (r << 16) | (g << 8) | b;
		}
		return rbgColor;
	}
	
	public boolean isVoxelVisible(int color) {
		int alpha = (color >> 24) & 0xFF;
		boolean visible;
		if (visibilityMaskEncoded == QBLoader.VISIBILITY_VOXEL) {
			visible = (alpha == 0) ? false : true;
		} else {  // QBLoader.VISIBILITY_SIDE
			visible = ((alpha | QBLoader.SIDE_MASK_INVISIBLE) ==  QBLoader.SIDE_MASK_INVISIBLE) ? false : true;
		}
		return visible;
	}

}