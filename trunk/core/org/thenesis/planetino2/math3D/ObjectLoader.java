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
		public ShadedTexture texture;
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
		int index = Integer.parseInt(indexStr);
		if (index < 0) {
			index = vertices.size() + index + 1;
		}
		return (Vector3D) vertices.elementAt(index - 1);
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
			} else if (command.equals("f")) {
				// create a new face (flat, convex polygon)
				Vector currVertices = new Vector();
				while (tokenizer.hasMoreTokens()) {
					String indexStr = tokenizer.nextToken();

					// ignore texture and normal coords
					int endIndex = indexStr.indexOf('/');
					if (endIndex != -1) {
						indexStr = indexStr.substring(0, endIndex);
					}

					currVertices.addElement(getVector(indexStr));
				}

				// create textured polygon
				Vector3D[] array = new Vector3D[currVertices.size()];
				currVertices.copyInto(array);
				//currVertices.toArray(array);
				TexturedPolygon3D poly = new TexturedPolygon3D(array);

				// set the texture
				ShadedSurface.createShadedSurface(poly, currentMaterial.texture, lights, ambientLightIntensity);

				// add the polygon to the current group
				currentGroup.addPolygon(poly);
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
					currentMaterial = new Material();
					materials.put(name, currentMaterial);
				}
			} else if (command.equals("map_Kd")) {
				// give the current material a texture
				String name = tokenizer.nextToken();
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
