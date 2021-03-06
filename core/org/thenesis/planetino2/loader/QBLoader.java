package org.thenesis.planetino2.loader;

import java.io.IOException;
import java.io.InputStream;

/**
 *  <b>Header of the Qubicle Binary file format</b><br>
	The first 24 bytes are used for the header including following information:
	<ul>
     <li>Version (4 bytes): stores the version of the Qubicle Binary file format as major, minor, release, build. Current version is 1.1.0.0</li>
     <li>Color Format (4): Can be either 0 or 1. If 0 voxel data is encoded as RGBA, if 1 as BGRA</li>
      <li>Z-Axis Orientation (4): Can either be 0=left handed or 1=right handed</li>
      <li>Compression (4): If set to 1 data is compressed using run length encoding (RLE). If set to 0 data is uncompressed.</li>
      <li>Visibility-Mask encoded (4): If set to 0 the A value of RGBA or BGRA is either 0 (invisble voxel) or 255 (visible voxel). If set to 1 the visibility mask of each voxel is encoded into the A value telling your software which sides of the voxel are visible. You can save a lot of render time using this option. More info about this in the section visibility-mask encoding</li>
      <li>Matrix count: tells you how many matrices are stored in this file. </li>
 	</ul>
 	@see {@link http://minddesk.com/wiki/index.php?title=Qubicle_Constructor_1:Data_Exchange_With_Qubicle_Binary} 
 */
public class QBLoader {

	static final boolean DEBUG = false;

	public static final int ORIENTATION_LEFT_HANDED = 0;
	public static final int ORIENTATION_RIGHT_HANDED = 1;
	public static final int COLOR_FORMAT_RGBA = 0;
	public static final int COLOR_FORMAT_BGRA = 1;
	public static final int COMPRESSION_NONE = 0;
	public static final int COMPRESSION_RLE = 1;
	public static final int VISIBILITY_VOXEL = 0;
	public static final int VISIBILITY_SIDE = 1;
	public static final int CODEFLAG = 2;
	public static final int NEXTSLICEFLAG = 6;
	
	public static final int SIDE_MASK_INVISIBLE = 0;
	public static final int SIDE_MASK_LEFT_SIDE_VISIBLE = 2;
	public static final int SIDE_MASK_RIGHT_SIDE_VISIBLE = 4;
	public static final int SIDE_MASK_TOP_SIDE_VISIBLE = 8;
	public static final int SIDE_MASK_BOTTOM_SIDE_VISIBLE = 16;
	public static final int SIDE_MASK_FRONT_SIDE_VISIBLE = 32;
	public static final int SIDE_MASK_BACK_SIDE_VISIBLE = 64;
	public static final int SIDE_MASK_ALL_SIDES_VISIBLE = SIDE_MASK_LEFT_SIDE_VISIBLE | SIDE_MASK_RIGHT_SIDE_VISIBLE | SIDE_MASK_TOP_SIDE_VISIBLE
			| SIDE_MASK_BOTTOM_SIDE_VISIBLE | SIDE_MASK_FRONT_SIDE_VISIBLE | SIDE_MASK_BACK_SIDE_VISIBLE;

	private long version; // uint32
	private long colorFormat; // uint32
	private long zAxisOrientation; // uint32
	private long compression; // uint32
	private long visibilityMaskEncoded; // uint32
	private int numMatrices; // uint32

	private QBMatrix[] matrices;

	public QBLoader() {
		// TODO Auto-generated constructor stub
	}

	public void load(InputStream dis) throws IOException {
		readHeader(dis);
		readData(dis);
	}

	private void readHeader(InputStream dis) throws IOException {
		version = readLittleEndianUnsignedInt(dis);
		colorFormat = readLittleEndianUnsignedInt(dis);
		zAxisOrientation = readLittleEndianUnsignedInt(dis);
		compression = readLittleEndianUnsignedInt(dis);
		visibilityMaskEncoded = readLittleEndianUnsignedInt(dis);
		numMatrices = (int) readLittleEndianUnsignedInt(dis);

		int v = (int) version;
		int build = (v >> 24) & 0xFF;
		int release = (v >> 16) & 0xFF;
		int minor = (v >> 8) & 0xFF;
		int major = v & 0xFF;
		
		if ((major != 1) || (minor != 1)) {
			throw new IOException("Version " + major + "." + minor + " of the Qubicle Binary format is not supported");
		}

		if (DEBUG) {
			debug("Version (major.minor.release.build)", major + "." + minor + "." + release + "." + build);
			debug("colorFormat", colorFormat == COLOR_FORMAT_RGBA ? "RGBA" : "BGRA");
			debug("zAxisOrientation", zAxisOrientation == ORIENTATION_LEFT_HANDED ? "Left handed": "Right handed");
			debug("compression", compression == COMPRESSION_NONE ? "None" : "RLE");
			debug("visibilityMaskEncoded", visibilityMaskEncoded == VISIBILITY_VOXEL ? "Voxel" : "Side");
			debug("numMatrices", numMatrices);
		}

	}

	private void readData(InputStream dis) throws IOException {

		matrices = new QBMatrix[(int) numMatrices];
		
		for (int i = 0; i < numMatrices; i++) {
			// Read matrix name
			int nameLength = dis.read();
			byte[] buffer = new byte[nameLength];
			dis.read(buffer);
			String name = new String(buffer);

			// Read matrix size 
			int sizeX = (int) readLittleEndianUnsignedInt(dis);
			int sizeY = (int) readLittleEndianUnsignedInt(dis);
			int sizeZ = (int) readLittleEndianUnsignedInt(dis);

			// Read matrix position
			int posX = readLittleEndianInt(dis);
			int posY = readLittleEndianInt(dis);
			int posZ = readLittleEndianInt(dis);

			if (DEBUG) {
				debug("Matrix", i);
				debug("name", name);
				debug("sizeX", sizeX);
				debug("sizeY", sizeY);
				debug("sizeZ", sizeZ);
				debug("posX", posX);
				debug("posY", posY);
				debug("posZ", posZ);
			}

			// Create matrix and add to matrix list
			int[] matrixData = new int[sizeX * sizeY * sizeZ];

			if (compression == COMPRESSION_NONE) { // if uncompressed
				for (int z = 0; z < sizeZ; z++) {
					for (int y = 0; y < sizeY; y++) {
						for (int x = 0; x < sizeX; x++) {
							int color = readColor(dis);
							matrixData[x + y * sizeX + z * sizeX * sizeY] = color;
//							if (DEBUG) {
//								debug("(" + x + ", " + y + ", " + z + ")", Integer.toHexString(color));
//							}
						}
					}
				}
			} else { // if compressed
				int x = 0;
				int y = 0;
				int z = 0;
				while (z < sizeZ) {
					z++;
					int index = 0;
					while (true) {
						int data = (int) readLittleEndianUnsignedInt(dis);
						if (data == NEXTSLICEFLAG) {
							break;
						} else if (data == CODEFLAG) {
							int count = (int) readLittleEndianUnsignedInt(dis);
							data = readColor(dis);
							for (int j = 0; j < count; j++) {
								x = (index % sizeX) + 1; // mod = modulo e.g. 12 mod 8 = 4
								y = (index / sizeX) + 1; // div = integer division e.g. 12 div 8 = 1
								index++;
								matrixData[x + y * sizeX + z * sizeX * sizeY] = data;
							}
						} else {
							x = (index % sizeX) + 1;
							y = (index / sizeX) + 1;
							index++;
							matrixData[x + y * sizeX + z * sizeX * sizeY] = swapEndianness(data);
						}
					}
				}
			}
			
			QBMatrix matrix = new QBMatrix(name, sizeX, sizeY, sizeZ, posX, posY, posZ, matrixData, colorFormat, zAxisOrientation, visibilityMaskEncoded);
			matrices[i] = matrix;
		}

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

	public int getNumMatrices() {
		return numMatrices;
	}

	public QBMatrix[] getMatrices() {
		return matrices;
	}
	
	public QBMatrix getMatrix(String name) {
		for (int i = 0; i < matrices.length; i++) {
			if (matrices[i].getName().equalsIgnoreCase(name)) {
				return matrices[i];
			}
		}
		return null;
	}

	private long readLittleEndianUnsignedInt(InputStream dis) throws IOException {
		int fourthByte = dis.read() & 0xFF;
		int thirdByte = dis.read() & 0xFF;
		int secondByte = dis.read() & 0xFF;
		int firstByte = dis.read() & 0xFF;
		long anUnsignedInt = ((long) (firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte)) & 0xFFFFFFFFL;
		return anUnsignedInt;
	}

	private int readLittleEndianInt(InputStream dis) throws IOException {
		int fourthByte = dis.read() & 0xFF;
		int thirdByte = dis.read() & 0xFF;
		int secondByte = dis.read() & 0xFF;
		int firstByte = dis.read() & 0xFF;
		int signedInt = (firstByte << 24) | (secondByte << 16) | (thirdByte << 8) | fourthByte;
		return signedInt;
	}
	
	private int readColor(InputStream dis) throws IOException {
		int firstByte = dis.read() & 0xFF;
		int secondByte = dis.read() & 0xFF;
		int thirdByte = dis.read() & 0xFF;
		int fourthByte = dis.read() & 0xFF;
		int signedInt = (firstByte << 24) | (secondByte << 16) | (thirdByte << 8) | fourthByte;
		return signedInt;
	}
	
	private int swapEndianness(int v) {
		int firstByte = v & 0xFF;
		int secondByte = (v >> 8) & 0xFF;
		int thirdByte = (v >> 16) & 0xFF;
		int fourthByte = (v >> 24) & 0xFF;
		int swapValue = (firstByte << 24) | (secondByte << 16) | (thirdByte << 8) | fourthByte;
		return swapValue;
	}
	
	static void debug(String comment, int value) {
		System.out.println(comment + " : " + value);
	}

	static void debug(String comment, long value) {
		System.out.println(comment + " : " + value);
	}

	static void debug(String comment, String value) {
		System.out.println(comment + " : " + value);
	}

	public static void main(String[] args) {
		InputStream is = QBLoader.class.getResourceAsStream("/res/" + "NearCraft.qb"); // cube_3_3_red.qb monu9.qb
		QBLoader loader = new QBLoader();
		try {
			loader.load(is);
			QBMatrix[] matrices = loader.getMatrices();
			QBMatrix matrix = matrices[0];
			System.out.println("Matrix name: " + matrix.getName());
			int numberOfVoxels = matrix.getNumberOfVoxels();
			int visibleCount = matrix.getNumberOfVisibleVoxels();
			System.out.println("Potentially visible voxels : " + visibleCount + " / " + numberOfVoxels);
			int faceCount = matrix.getNumberOfVisibleFaces();
			System.out.println("Visible faces : " + faceCount + " / " + (visibleCount * 6) + " : " + (faceCount * 100 / (6 * visibleCount)) + "%");
			int hiddenCount = matrix.removeHiddenVoxels();
			System.out.println("Hidden voxels : " + hiddenCount + " / " + numberOfVoxels);
			int voxelReduction =  hiddenCount * 100 / visibleCount;
			System.out.println("Voxel reduction : " + voxelReduction + "%");
			int faceReduction = 100 - (faceCount * 100 / (6 * visibleCount));
			System.out.println("Face reduction : " + faceReduction + "%");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
