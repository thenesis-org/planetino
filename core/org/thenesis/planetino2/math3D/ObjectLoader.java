/*
 * Planetino - Copyright (C) 2007-2008 Guillaume Legris, Mathieu Legris
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA 
 */

/* Copyright (c) 2003, David Brackeen
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *   - Redistributions of source code must retain the above copyright notice, 
 *     this list of conditions and the following disclaimer.
 *   - Redistributions in binary form must reproduce the above copyright notice, 
 *     this list of conditions and the following disclaimer in the documentation 
 *     and/or other materials provided with the distribution.
 *   - Neither the name of David Brackeen nor the names of its contributors may be
 *     used to endorse or promote products derived from this software without 
 *     specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.thenesis.planetino2.math3D;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.thenesis.planetino2.graphics3D.texture.ShadedSurface;
import org.thenesis.planetino2.graphics3D.texture.ShadedTexture;
import org.thenesis.planetino2.graphics3D.texture.SmallShadedSurface;
import org.thenesis.planetino2.graphics3D.texture.Texture;
import org.thenesis.planetino2.util.BufferedReader;
import org.thenesis.planetino2.util.StringTokenizer;

/**
 The ObjectLoader class loads a subset of the
 Alias|Wavefront OBJ file specification.

 Lines that begin with '#' are comments.

 OBJ file keywords:
 <pre>
 mtllib [filename]    - Load materials from an external .mtl
 file.
 v [x] [y] [z]        - Define a vertex with floating-point
 coords (x,y,z).
 f [v1] [v2] [v3] ... - Define a new face. a face is a flat,
 convex polygon with vertices in
 counter-clockwise order. Positive
 numbers indicate the index of the
 vertex that is defined in the file.
 Negative numbers indicate the vertex
 defined relative to last vertex read.
 For example, 1 indicates the first
 vertex in the file, -1 means the last
 vertex read, and -2 is the vertex
 before that.
 g [name]             - Define a new group by name. The faces
 following are added to this group.
 usemtl [name]        - Use the named material (loaded from a
 .mtl file) for the faces in this group.
 </pre>

 MTL file keywords:
 <pre>
 newmtl [name]        - Define a new material by name.
 map_Kd [filename]    - Give the material a texture map.
 </pre>
 */
public class ObjectLoader {

	/**
	 The Material class wraps a ShadedTexture.
	 */
	public static class Material {
		
		//public File sourceFile;
		public String library;
		public String name;
		public String textureFileName;
		public ShadedTexture texture;
		
		public Material(String name) {
			this.name = name;
		}
		
		
		public Material() {
			this.name = "null";
		}
	}

	/**
	 A LineParser is an interface to parse a line in a text
	 file. Separate LineParsers and are used for OBJ and MTL
	 files.
	 */
	protected interface LineParser {
		public void parseLine(String line) throws IOException, NumberFormatException, NoSuchElementException;
	}

	protected String path;
	protected Vector vertices;
	protected Vector textureCoordinates;
	protected String currentMaterialLib;
	protected Material currentMaterial;
	protected Hashtable materials;
	protected Vector lights;
	protected float ambientLightIntensity;
	protected Hashtable parsers;
	private PolygonGroup object;
	private PolygonGroup currentGroup;

	/**
	 Creates a new ObjectLoader.
	 */
	public ObjectLoader() {
		materials = new Hashtable();
		vertices = new Vector();
		textureCoordinates = new Vector();
		parsers = new Hashtable();
		parsers.put("obj", new ObjLineParser());
		parsers.put("mtl", new MtlLineParser());
		currentMaterial = null;
		setLights(new Vector(), 1);
	}

	/**
	 Sets the lights used for the polygons in the parsed
	 objects. After calling this method calls to loadObject
	 use these lights.
	 */
	public void setLights(Vector lights, float ambientLightIntensity) {
		this.lights = lights;
		this.ambientLightIntensity = ambientLightIntensity;
	}

	/**
	 Loads an OBJ file as a PolygonGroup.
	 */
	public PolygonGroup loadObject(String path, String filename) throws IOException {		

		this.path = path;

		object = new PolygonGroup();
		object.setFilename(filename);

		vertices.removeAllElements();
		textureCoordinates.removeAllElements();
		currentGroup = object;
		parseFile(filename);

		return object;

		//        File file = new File(filename);
		//        object = new PolygonGroup();
		//        object.setFilename(file.getName());
		//        path = file.getParentFile();
		//
		//        vertices.clear();
		//        currentGroup = object;
		//        parseFile(file.getName());
		//
		//        return object;
	}

	/**
	 Gets a Vector3D from the list of vectors in the file.
	 Negative indeces count from the end of the list, postive
	 indeces count from the beginning. 1 is the first index,
	 -1 is the last. 0 is invalid and throws an exception.
	 */
	protected Vector3D getVector(String indexStr) {
		return getVector(indexStr, vertices);
	}
	
	/**
	 Gets a Vector3D from the list of vectors in the file.
	 Negative indeces count from the end of the list, postive
	 indeces count from the beginning. 1 is the first index,
	 -1 is the last. 0 is invalid and throws an exception.
	 */
	protected Vector3D getTextureVector(String indexStr) {
		return getVector(indexStr, textureCoordinates);
	}
	
	
	/**
	 Gets a Vector3D from the list of vectors.
	 Negative indeces count from the end of the list, postive
	 indeces count from the beginning. 1 is the first index,
	 -1 is the last. 0 is invalid and throws an exception.
	 */
	protected Vector3D getVector(String indexStr, Vector vector3DList) {
		int index = Integer.parseInt(indexStr);
		if (index < 0) {
			index = vector3DList.size() + index + 1;
		}
		return (Vector3D) vector3DList.elementAt(index - 1);
	}
	
	/**
	 * Used by the editor.
	 * @return the materials.
	 */
	public Hashtable getMaterials() {
		return materials;
	}
	
	
	/**
	 * Used by the editor.
	 * @return the lights.
	 */
	public Vector getLights() {
		return lights;
	}

	/**
	 Parses an OBJ (ends with ".obj") or MTL file (ends with
	 ".mtl").
	 */
	protected void parseFile(String filename) throws IOException {
		// get the file relative to the source path
		//File file = new File(path, filename);
		System.out.println("path: " + path + " filename: " + filename);
		BufferedReader reader = new BufferedReader(new InputStreamReader(getClass()
				.getResourceAsStream(path + filename)));

		//        File file = new File(path, filename);
		//        System.out.println("PATH: " + file);
		//        BufferedReader reader = new BufferedReader(
		//            new FileReader(file));

		// get the parser based on the file extention
		LineParser parser = null;
		int extIndex = filename.lastIndexOf('.');
		if (extIndex != -1) {
			String ext = filename.substring(extIndex + 1);
			parser = (LineParser) parsers.get(ext.toLowerCase());
		}
		if (parser == null) {
			parser = (LineParser) parsers.get("obj");
		}

		// parse every line in the file
		while (true) {
			String line = reader.readLine();
			// no more lines to read
			if (line == null) {
				reader.close();
				return;
			}

			line = line.trim();

			// ignore blank lines and comments
			if (line.length() > 0 && !line.startsWith("#")) {
				// interpret the line
				try {
					parser.parseLine(line);
				} catch (NumberFormatException ex) {
					throw new IOException(ex.getMessage());
				} catch (NoSuchElementException ex) {
					throw new IOException(ex.getMessage());
				}
			}

		}
	}

	/**
	 Parses a line in an OBJ file.
	 */
	protected class ObjLineParser implements LineParser {

		public void parseLine(String line) throws IOException, NumberFormatException, NoSuchElementException {
			StringTokenizer tokenizer = new StringTokenizer(line);
			String command = tokenizer.nextToken();
			if (command.equals("v")) {
				// create a new vertex
				vertices.addElement(new Vector3D(Float.parseFloat(tokenizer.nextToken()), Float.parseFloat(tokenizer
						.nextToken()), Float.parseFloat(tokenizer.nextToken())));
			} else if (command.equals("vt")) {
				// create new texture coordinates
				textureCoordinates.addElement(new Vector3D(Float.parseFloat(tokenizer.nextToken()), Float.parseFloat(tokenizer
						.nextToken()), 0));
			} else if (command.equals("f")) {
				// create a new face (flat, convex polygon)
				Vector currVertices = new Vector();
				Vector currTextureCoordinates = new Vector();
				while (tokenizer.hasMoreTokens()) {
					String faceStr = tokenizer.nextToken();
					String vertexIndexStr = faceStr;

					// ignore texture and normal coords
					int endIndex = faceStr.indexOf('/');
					if (endIndex != -1) {
						vertexIndexStr = faceStr.substring(0, endIndex);
						String textureCoordIndexStr = faceStr.substring(endIndex + 1);
						endIndex = textureCoordIndexStr.indexOf('/');
						if (endIndex != -1) {
							textureCoordIndexStr = textureCoordIndexStr.substring(0, endIndex);
						}
						currTextureCoordinates.addElement(getTextureVector(textureCoordIndexStr));
					}

					currVertices.addElement(getVector(vertexIndexStr));
				}

				// create textured polygon
				Vector3D[] vertexArray = new Vector3D[currVertices.size()];
				currVertices.copyInto(vertexArray);
				
				TexturedPolygon3D texturedPolygon = new TexturedPolygon3D(vertexArray);

				if (currTextureCoordinates.isEmpty()) {
					ShadedSurface.createShadedSurface(texturedPolygon, currentMaterial.texture, lights, ambientLightIntensity);
				} else {
//					Vector3D[] t = new Vector3D[currTextureCoordinates.size()];
//					currTextureCoordinates.copyInto(t);
//					Vector3D origin = new Vector3D(t[0]);
//					Vector3D directionU = new Vector3D(t[1]);
//					Vector3D directionV = new Vector3D(t[2]);
//					origin.x = origin.x * currentMaterial.texture.getWidth();
//					origin.y = origin.y * currentMaterial.texture.getHeight();
//					directionU.x = directionU.x * currentMaterial.texture.getWidth();
//					directionU.y = directionU.y * currentMaterial.texture.getHeight();
//					directionV.x = directionV.x * currentMaterial.texture.getWidth();
//					directionV.y = directionV.y * currentMaterial.texture.getHeight();
//					directionU.subtract(origin);
//					directionV.subtract(origin);
//					//System.out.println("origin=" + origin + " directionU=" + directionU + " directionV=" + directionV);
//					Rectangle3D bounds = new Rectangle3D(origin, directionU, directionV, currentMaterial.texture.getWidth(), currentMaterial.texture.getHeight());
//					ShadedSurface.createShadedSurface(texturedPolygon, currentMaterial.texture, bounds, lights, ambientLightIntensity);
					
					
//					Vector3D[] t = new Vector3D[currTextureCoordinates.size()];
//					currTextureCoordinates.copyInto(t);
//					Vector3D origin = new Vector3D(t[0]);
//					Vector3D directionU = new Vector3D(t[1]);
//					Vector3D directionV = new Vector3D(t[2]);
//					origin.multiply(currentMaterial.texture.getWidth());
////					origin.x = origin.x * currentMaterial.texture.getWidth();
////					origin.y = origin.y * currentMaterial.texture.getHeight();
////					directionU.x = directionU.x * currentMaterial.texture.getWidth();
////					directionU.y = directionU.y * currentMaterial.texture.getHeight();
////					directionV.x = directionV.x * currentMaterial.texture.getWidth();
////					directionV.y = directionV.y * currentMaterial.texture.getHeight();
//					//directionU.subtract(origin);
//					//directionV.subtract(origin);
//					int width = (int) (directionU.length());
//					int height = (int) (directionV.length());
//					Rectangle3D bounds = new Rectangle3D(origin, directionU, directionV, width, height);
//					ShadedSurface.createShadedSurface(texturedPolygon, currentMaterial.texture, bounds, lights, ambientLightIntensity);
//					//ExtendedShadedSurface.createShadedSurface(texturedPolygon, currentMaterial.texture, lights, ambientLightIntensity);
				
//					Vector3D[] t = new Vector3D[currTextureCoordinates.size()];
//					currTextureCoordinates.copyInto(t);
//					Vector3D origin = new Vector3D(t[0]);
//					Vector3D directionU = new Vector3D(t[1]);
//					Vector3D directionV = new Vector3D(t[2]);
//					origin.multiply(currentMaterial.texture.getWidth());
//					directionU.multiply(currentMaterial.texture.getWidth());
//					directionV.multiply(currentMaterial.texture.getWidth());
//					directionU.subtract(origin);
//					directionV.subtract(origin);
//
//					int width = (int) (directionU.length());
//					int height = (int) (directionV.length() + 1);
//					width = (width == 0) ? 1 : width;
//					height = (height == 0) ? 1 : height;
					
//					int width = 2;
//					int height = 2;
//					int size = width * height;
//					
//					//System.out.println("size=" + size);
//					int[] rgbBuffer = new int[size];
//					for (int y = 0; y < height; y++) {
//						for (int x = 0; x < width; x++) {
//							
////							origin = new Vector3D(0.1f, 0.98f, 0.0f);
////							directionU = new Vector3D(0.1f, 0.98f, 0.0f);
////							directionV = new Vector3D(0.1f, 0.98f, 0.0f);
//							origin = new Vector3D(t[0]);
////							directionU = new Vector3D(t[1]);
////							directionV = new Vector3D(t[2]);
////							System.out.println(origin + "  " + directionU + "   "+ directionV);
//							origin.multiply(currentMaterial.texture.getWidth());
////							directionU.multiply(currentMaterial.texture.getWidth());
////							directionV.multiply(currentMaterial.texture.getWidth());
////							directionU.subtract(origin);
////							directionV.subtract(origin);
////							directionU.normalize();
////							directionV.normalize();
////							System.out.println("origin=" + origin);
////							directionU.multiply(x);
////							directionV.multiply(y);
////							directionU.add(directionV);
////							origin.add(directionU);
//							//System.out.println(origin + "  " + directionU + "   "+ directionV);
//							int srcX = (int) origin.x; //(currentMaterial.texture.getWidth() - origin.x); // // 
//							int srcY = (int) (currentMaterial.texture.getWidth() - origin.y);
////							int srcX = (int) (currentMaterial.texture.getWidth() - origin.x);
////							int srcY = (int) (origin.y);
//							//System.out.println("srcX=" + srcX + " srcY=" + srcY);
//							if (srcX < 0) srcX = 0;
//							if (srcY < 0) srcY = 0;
//							if (srcX >= currentMaterial.texture.getWidth()) srcX = currentMaterial.texture.getWidth() - 1;
//							if (srcY >= currentMaterial.texture.getWidth()) srcY = currentMaterial.texture.getWidth() - 1;
//							//System.out.println("srcX=" + srcX + " srcY=" + srcY);
//							rgbBuffer[y * width + x] = currentMaterial.texture.sourceBuffer[srcY * currentMaterial.texture.getWidth() + srcX]; //
//							//currentMaterial.texture.getColor(srcX, srcY); //
//							//System.out.println("c=" + Integer.toHexString(rgbBuffer[y * width + x]));
//						}
//					}
//				
//					//ExtendedTexture texture = new ExtendedTexture(rgbBuffer, width, height);
//					//PowerOf2Texture texture = new PowerOf2Texture(rgbBuffer, Texture.countbits(width - 1), Texture.countbits(height - 1));
//					//texturedPolygon.setTexture(texture);
//					
//					ShadedTexture texture = new ShadedTexture(rgbBuffer, Texture.countbits(width - 1), Texture.countbits(height - 1));
//					ShadedSurface.createShadedSurface(texturedPolygon, texture, lights, ambientLightIntensity);
				
					
//					/* Works but use too much memory because of shade level calculations (64 levels) */
//					int width = 2;
//					int height = 2;
//					int size = width * height;
//					Vector3D origin = new Vector3D((Vector3D)currTextureCoordinates.elementAt(0));
//					origin.multiply(currentMaterial.texture.getWidth());
//					int srcX = (int) origin.x; //(currentMaterial.texture.getWidth() - origin.x); // // 
//					int srcY = (int) (currentMaterial.texture.getWidth() - origin.y);
//					if (srcX < 0) srcX = 0;
//					if (srcY < 0) srcY = 0;
//					if (srcX >= currentMaterial.texture.getWidth()) srcX = currentMaterial.texture.getWidth() - 1;
//					if (srcY >= currentMaterial.texture.getWidth()) srcY = currentMaterial.texture.getWidth() - 1;
//					int color = currentMaterial.texture.getColor(srcX, srcY); //currentMaterial.texture.sourceBuffer[srcY * currentMaterial.texture.getWidth() + srcX];
//					//System.out.println("size=" + size);
//					int[] rgbBuffer = new int[size];
//					for (int i = 0; i < size; i++) {
//						rgbBuffer[i] = color;
//					}
//					//ShadedTexture texture = new ShadedTexture(rgbBuffer, Texture.countbits(width - 1), Texture.countbits(height - 1));
//					//ExtendedShadedSurface.createShadedSurface(texturedPolygon, texture, lights, ambientLightIntensity);
//					ShadedTexture texture = new ShadedTexture(rgbBuffer, Texture.countbits(width - 1), Texture.countbits(height - 1));
//					ShadedSurface.createShadedSurface(texturedPolygon, texture, lights, ambientLightIntensity);
					
					
					/* Works but no shading yet */
					Vector3D[] texels = new Vector3D[currTextureCoordinates.size()];
					currTextureCoordinates.copyInto(texels);
					SmallShadedSurface.createSurface(texturedPolygon, currentMaterial.texture, texels);
					
					
					/* Use only color of origin from the shaded texture: doesn't work  */
//					Vector3D[] t = new Vector3D[currTextureCoordinates.size()];
//					currTextureCoordinates.copyInto(t);
//					Vector3D origin = new Vector3D(t[0]);
//					origin.multiply(currentMaterial.texture.getWidth());
//					int srcX = (int) origin.x; //(currentMaterial.texture.getWidth() - origin.x); // // 
//					int srcY = (int) (currentMaterial.texture.getWidth() - origin.y);
//					if (srcX < 0) srcX = 0;
//					if (srcY < 0) srcY = 0;
//					if (srcX >= currentMaterial.texture.getWidth()) srcX = currentMaterial.texture.getWidth() - 1;
//					if (srcY >= currentMaterial.texture.getWidth()) srcY = currentMaterial.texture.getWidth() - 1;
//					int color = currentMaterial.texture.getColor(srcX, srcY); //currentMaterial.texture.sourceBuffer[srcY * currentMaterial.texture.getWidth() + srcX];
//					//ShadedTexture texture = new ShadedTexture(rgbBuffer, Texture.countbits(width - 1), Texture.countbits(height - 1));
//					//ExtendedShadedSurface.createShadedSurface(texturedPolygon, texture, lights, ambientLightIntensity);
//					//ShadedTexture texture = new ShadedTexture(rgbBuffer, Texture.countbits(width - 1), Texture.countbits(height - 1));
//					//ShadedSurface.createShadedSurface(texturedPolygon, texture, lights, ambientLightIntensity);
//					Rectangle3D bounds = new Rectangle3D(origin, new Vector3D(t[1]), new Vector3D(t[2]), 2, 2);
//					ShadedSurface.createShadedSurface(texturedPolygon, currentMaterial.texture, bounds, lights, ambientLightIntensity);
				
				}
				
				//ShadedSurface.createShadedSurface(texturedPolygon, currentMaterial.texture, lights, ambientLightIntensity);

				// add the polygon to the current group
				currentGroup.addPolygon(texturedPolygon);
			} else if (command.equals("g")) {
				// define the current group
				if (tokenizer.hasMoreTokens()) {
					String name = tokenizer.nextToken();
					currentGroup = new PolygonGroup(name);
				} else {
					currentGroup = new PolygonGroup();
				}
				object.addPolygonGroup(currentGroup);
			} else if (command.equals("mtllib")) {
				// load materials from file
				String name = tokenizer.nextToken();
				currentMaterialLib = name;
				parseFile(name);
			} else if (command.equals("usemtl")) {
				// define the current material
				String name = tokenizer.nextToken();
				currentMaterial = (Material) materials.get(name);
				if (currentMaterial == null) {
					System.out.println("no material: " + name);
				}
			} else {
				// unknown command - ignore it
			}

		}
	}

	/**
	 Parses a line in a material MTL file.
	 */
	protected class MtlLineParser implements LineParser {

		public void parseLine(String line) throws NoSuchElementException {
			StringTokenizer tokenizer = new StringTokenizer(line);
			String command = tokenizer.nextToken();

			if (command.equals("newmtl")) {
				// create a new material if needed
				String name = tokenizer.nextToken();
				currentMaterial = (Material) materials.get(name);
				if (currentMaterial == null) {
					currentMaterial = new Material(name);
					materials.put(name, currentMaterial);
				}
				currentMaterial.library = currentMaterialLib;
			} else if (command.equals("map_Kd")) {
				// give the current material a texture
				String name = tokenizer.nextToken();
				currentMaterial.textureFileName = name;
				currentMaterial.texture = (ShadedTexture) Texture.createTexture(path, name, true);
				//				File file = new File(path, name);
				//				if (!file.equals(currentMaterial.sourceFile)) {
				//					currentMaterial.sourceFile = file;
				//					currentMaterial.texture = (ShadedTexture) Texture.createTexture(path, file.getPath(), true);
				//				}
			} else {
				// unknown command - ignore it
			}
		}
	}
}
