package org.thenesis.planetino2.loader;

public class QBMatrix {

	String name;
	// read matrix size 
	int sizeX;
	int sizeY;
	int sizeZ;
	int posX;
	int posY;
	int posZ;
	int[] data;

	public QBMatrix(String name, int sizeX, int sizeY, int sizeZ, int posX, int posY, int posZ, int[] data) {
		super();
		this.name = name;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.data = data;
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
	
	

}