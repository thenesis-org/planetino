package org.thenesis.planetino2.loader;

public class QBMatrix {

	private String name;
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
	
	public int getNumberOfVoxels() {
		return sizeX * sizeY * sizeZ;
	}
	
	public int getNumberOfVisibleFaces() {
		if (visibilityMaskEncoded == QBLoader.VISIBILITY_VOXEL) {
			return getNumberOfVisibleVoxels() * 6;
		} else {  // QBLoader.VISIBILITY_SIDE
			int	visibleFaceCount = 0;
			for (int z = 0; z < sizeZ; z++) {
				for (int y = 0; y < sizeY; y++) {
					for (int x = 0; x < sizeX; x++) {
						int color = getVoxelColor(x, y, z);
						int alpha = color & 0xFF;
						if (alpha ==  QBLoader.SIDE_MASK_INVISIBLE) {
							continue;
						} 
						if ((alpha & QBLoader.SIDE_MASK_BACK_SIDE_VISIBLE) == QBLoader.SIDE_MASK_BACK_SIDE_VISIBLE) {
							visibleFaceCount++;
						}
						if ((alpha & QBLoader.SIDE_MASK_BOTTOM_SIDE_VISIBLE) == QBLoader.SIDE_MASK_BOTTOM_SIDE_VISIBLE) {
							visibleFaceCount++;
						}
						if ((alpha & QBLoader.SIDE_MASK_FRONT_SIDE_VISIBLE) == QBLoader.SIDE_MASK_FRONT_SIDE_VISIBLE) {
							visibleFaceCount++;
						}
						if ((alpha & QBLoader.SIDE_MASK_LEFT_SIDE_VISIBLE) == QBLoader.SIDE_MASK_LEFT_SIDE_VISIBLE) {
							visibleFaceCount++;
						}
						if ((alpha & QBLoader.SIDE_MASK_RIGHT_SIDE_VISIBLE) == QBLoader.SIDE_MASK_RIGHT_SIDE_VISIBLE) {
							visibleFaceCount++;
						}
						if ((alpha & QBLoader.SIDE_MASK_TOP_SIDE_VISIBLE) == QBLoader.SIDE_MASK_TOP_SIDE_VISIBLE) {
							visibleFaceCount++;
						}
					}
				}
			}
			return visibleFaceCount;
		}
	}
	
	public boolean isFaceVisible(int alpha, int mask) {
		if ((alpha & mask) == mask) {
			return true;
		} else {
			return false;
		}
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
	
	public int getVisibityMask(int color) {
		int alpha = color & 0xFF;
		int mask;
		if (visibilityMaskEncoded == QBLoader.VISIBILITY_VOXEL) {
			mask = (alpha == 0) ? 0 : QBLoader.SIDE_MASK_ALL_SIDES_VISIBLE;
		} else {  // QBLoader.VISIBILITY_SIDE
			mask = alpha;
		}
		return mask;
	}
	
	public boolean isVoxelVisible(int color) {
		int alpha = color & 0xFF;
		boolean visible;
		if (visibilityMaskEncoded == QBLoader.VISIBILITY_VOXEL) {
			visible = (alpha == 0) ? false : true;
		} else {  // QBLoader.VISIBILITY_SIDE
			visible = (alpha ==  QBLoader.SIDE_MASK_INVISIBLE) ? false : true;
		}
		return visible;
	}
	
	public boolean isVoxelVisible(int x, int y, int z) {
		int color = getVoxelColor(x, y, z);
		return isVoxelVisible(color);
	}
	
	public int getNumberOfVisibleVoxels() {
		int	visibleCount = 0;
		for (int z = 0; z < sizeZ; z++) {
			for (int y = 0; y < sizeY; y++) {
				for (int x = 0; x < sizeX; x++) {
					boolean visible = isVoxelVisible(x, y, z); 
					if (visible) {
						visibleCount++;
					}
				}
			}
		}
		return visibleCount;
	}
	
	public int removeHiddenVoxels() {
		
		int hiddenCount = 0;
		
		for (int z = 0; z < sizeZ; z++) {
			for (int y = 0; y < sizeY; y++) {
				for (int x = 0; x < sizeX; x++) {
					// Obvious case: if the voxel is not visible, it's hidden.
					int color = getVoxelColor(x, y, z);
					if (!isVoxelVisible(color)) {
						continue;
					}
					
					boolean hidden = isHidden(x, y, z); 
					if (hidden) {
						hiddenCount++;
						setVoxelColor(x, y, z, 0);
//						if (QBLoader.DEBUG) {
//							QBLoader.debug("(" + x + ", " + y + ", " + z + ")", " is hidden");
//						}
					}
				}
			}
		}
		
		if (QBLoader.DEBUG) {
			QBLoader.debug("hidden/visible", hiddenCount + "/" + getNumberOfVisibleVoxels());
		}
		
		return hiddenCount;
	}
	
	private void setVoxelColor(int x, int y, int z, int color) {
		data[x + y * sizeX + z * sizeX * sizeY] = color;
	}

	public int getVoxelColor(int x, int y, int z) {
		return data[x + y * sizeX + z * sizeX * sizeY];
	}
	
	/**
	 * Check if the voxel is hidden by its neighbors
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public boolean isHidden(int x, int y, int z) {
		
		// Voxels on the borders can't be hidden
		if ((x <= 0) || (x >= sizeX - 1) ) {
			return false;
		}
		if ((y <= 0) || (y >= sizeY - 1) ) {
			return false;
		}
		if ((z <= 0) || (z >= sizeZ - 1) ) {
			return false;
		}
		
		boolean isHidden = true;
		isHidden &= isVoxelVisible(x - 1, y, z);
		isHidden &= isVoxelVisible(x + 1, y, z);
		isHidden &= isVoxelVisible(x, y - 1, z);
		isHidden &= isVoxelVisible(x, y + 1, z);
		isHidden &= isVoxelVisible(x, y, z - 1);
		isHidden &= isVoxelVisible(x, y, z + 1);
		
		return isHidden;
	}

}