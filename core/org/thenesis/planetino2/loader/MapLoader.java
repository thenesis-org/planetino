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
package org.thenesis.planetino2.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;

import org.thenesis.planetino2.util.Vector;
import org.thenesis.planetino2.bsp2D.BSPTree;
import org.thenesis.planetino2.bsp2D.BSPTreeBuilder;
import org.thenesis.planetino2.bsp2D.RoomDef;
import org.thenesis.planetino2.math3D.BoxBlockPolygonGroup;
import org.thenesis.planetino2.math3D.BoxModel;
import org.thenesis.planetino2.math3D.BoxPolygonGroup;
import org.thenesis.planetino2.math3D.PointLight3D;
import org.thenesis.planetino2.math3D.PolygonGroup;
import org.thenesis.planetino2.math3D.PosterPolygonGroup;
import org.thenesis.planetino2.math3D.Transform3D;
import org.thenesis.planetino2.math3D.TriggerPolygonGroup;
import org.thenesis.planetino2.math3D.Vector3D;
import org.thenesis.planetino2.math3D.VoxelMatrixPolygonGroup;
import org.thenesis.planetino2.util.StringTokenizer;


/**
    The MapLoader class loads maps from a text file based on
    the Alias|Wavefront OBJ file specification.

    MAP file commands:
    <pre>
    v [x] [y] [z]        - Define a vertex with floating-point
                           coords (x,y,z).
    mtllib [filename]    - Load materials from an external .mtl
                           file.
    usemtl [name]        - Use the named material (loaded from a
                           .mtl file) for the next floor, ceiling,
                           or wall.
    ambientLightIntensity
        [value]          - Defines the ambient light intensity
                           for the next room, from 0 to 1.
    pointlight [v]       - Defines a point light located at the
        [intensity]        specified vector. Optionally, light
        [falloff]          intensity and falloff distance can
                           be specified.
    player [v] [angle]   - Specifies the starting location of the
                           player and optionally a starting
                           angle, in radians, around the y-axis.
    obj [uniqueName]     - Defines an object from an external
        [filename] [v]     OBJ file. The unique name allows this
        [angle]            object to be uniquely identified, but
                           can be "null" if no unique name is
                           needed. The filename is an external
                           OBJ file. Optionally, the starting
                           angle, in radians, around the y-axis
                           can be specified.
    room [name]          - Defines a new room, optionally giving
                           the room a name. A room consists of
                           vertical walls, a horizontal floor
                           and a horizontal ceiling. Concave rooms
                           are currently not supported, but can be
                           simulated by adjacent convex rooms.
    floor [height]       - Defines the height of the floor of
                           the current room, using the current
                           material. The current material can
                           be null, in which case no floor
                           polygon is created. The floor can be
                           above the ceiling, in which case a
                           "pillar" or "block" structure is
                           created, rather than a "room".
    ceil [height]        - Defines the height of the ceiling of
                           the current room, using the current
                           material. The current material can
                           be null, in which case no ceiling
                           polygon is created. The ceiling can be
                           below the floor, in which case a
                           "pillar" or "block" structure is
                           created, rather than a "room".
    wall [x] [z]         - Defines a wall vertex in a room using
         [bottom] [top]    the specified x and z coordinates.
                           Walls should be defined in clockwise
                           order. If "bottom" and "top" is not
                           defined, the floor and ceiling height
                           are used. If the current material is
                           null, or bottom is equal to top, no
                           wall polygon is created.
    poster [v]           - Defines a poster located at the specified 
           [width]         vector.
           [height]       
    </pre>
*/
public class MapLoader extends ObjectLoader {

    private BSPTreeBuilder builder;
    private Hashtable loadedObjects;
    private Transform3D playerStart;
    private RoomDef currentRoom;
	public BoxModel currentBoxDef;
    private Vector rooms;
    private Vector mapObjects;
	private Hashtable boxDefs;

    // use a separate ObjectLoader for objects
    private ObjectLoader objectLoader;


    /**
        Creates a new MapLoader using the default BSPTreeBuilder.
    */
    public MapLoader(ResourceLoader resourceLoader) {
        this(resourceLoader, null);
    }


    /**
        Creates a new MapLoader using the specified BSPTreeBuilder.
        If the builder is null, a default BSPTreeBuilder
        is created.
    */
    public MapLoader(ResourceLoader resourceLoader, BSPTreeBuilder builder) {
    	super(resourceLoader);
        if (builder == null) {
            this.builder = new BSPTreeBuilder();
        }
        else {
            this.builder = builder;
        }
        parsers.put("map", new MapLineParser());
        objectLoader = new ObjectLoader(resourceLoader);
        loadedObjects = new Hashtable();
        rooms = new Vector();
        mapObjects = new Vector();
        boxDefs = new Hashtable();
    }


    /**
        Loads a map file and creates a BSP tree. Objects
        created can be retrieved from the getObjectsInMap()
        method.
    */
    public BSPTree loadMap(String filename) throws IOException {
        
    	currentRoom = null;
        rooms.removeAllElements();
        vertices.removeAllElements();
        mapObjects.removeAllElements();
        boxDefs.clear();
        playerStart = new Transform3D();

        parseFile(filename);

        return createBSPTree();
    		
    }
    
    public BSPTree rebuildBSPTree() {
        return createBSPTree();
    }


    /**
        Creates a BSP tree from the rooms defined in the map file.
    */
    protected BSPTree createBSPTree() {
        // extract all polygons
        Vector allPolygons = new Vector();
        for (int i=0; i<rooms.size(); i++) {
            RoomDef room = (RoomDef)rooms.elementAt(i);
            //allPolygons.addAll(room.createPolygons());
            Enumeration e = room.createPolygons().elements();
            while(e.hasMoreElements()) {
            	allPolygons.addElement(e.nextElement());
            }
        }

        // build the tree
        BSPTree tree = builder.build(allPolygons);

        // create polygon surfaces based on the lights.
        tree.createSurfaces(lights);
        return tree;
    }
    
    public Vector getRooms() {
		return rooms;
	}

    /**
        Gets a list of all objects degined in the map file.
    */
    public Vector getObjectsInMap() {
        return mapObjects;
    }


    /**
        Gets the player start location defined in the map file.
    */
    public Transform3D getPlayerStartTransform() {
        return playerStart;
    }


    /**
        Sets the lights used for OBJ objects.
    */
    public void setObjectLights(Vector lights,
        float ambientLightIntensity)
    {
        objectLoader.setLights(lights, ambientLightIntensity);
    }


    /**
        Parses a line in a MAP file.
    */
    protected class MapLineParser implements LineParser {

        public void parseLine(String line) throws IOException,
            NoSuchElementException
        {
            StringTokenizer tokenizer = new StringTokenizer(line);
            String command = tokenizer.nextToken();

            if (command.equals("v")) {
                // create a new vertex
                vertices.addElement(new Vector3D(
                    Float.parseFloat(tokenizer.nextToken()),
                    Float.parseFloat(tokenizer.nextToken()),
                    Float.parseFloat(tokenizer.nextToken())));
            }
            else if (command.equals("mtllib")) {
                // load materials from file
                String name = tokenizer.nextToken();
                currentMaterialLib = name;
                parseFile(name);
            }
            else if (command.equals("usemtl")) {
                // define the current material
                String name = tokenizer.nextToken();
                if ("null".equals(name)) {
                    currentMaterial = new Material();
                }
                else {
                    currentMaterial =
                        (Material)materials.get(name);
                    if (currentMaterial == null) {
                        currentMaterial = new Material(name);
                        System.out.println("no material: " + name);
                    }
                }
            }
            else if (command.equals("pointlight")) {
                // create a point light
                Vector3D loc = getVector(tokenizer.nextToken());
                float intensity = 1;
                float falloff = PointLight3D.NO_DISTANCE_FALLOFF;
                if (tokenizer.hasMoreTokens()) {
                    intensity =
                        Float.parseFloat(tokenizer.nextToken());
                }
                if (tokenizer.hasMoreTokens()) {
                    falloff =
                        Float.parseFloat(tokenizer.nextToken());
                }
                lights.addElement(new PointLight3D(loc.x, loc.y, loc.z,
                    intensity, falloff));
            }
            else if (command.equals("ambientLightIntensity")) {
                // define the ambient light intensity
                ambientLightIntensity =
                    Float.parseFloat(tokenizer.nextToken());
            }
            else if (command.equals("player")) {
                // define the player start location
                playerStart.getLocation().setTo(
                    getVector(tokenizer.nextToken()));
                if (tokenizer.hasMoreTokens()) {
                    playerStart.setAngleY(
                        Float.parseFloat(tokenizer.nextToken()));
                }
            }
            else if (command.equals("trigger")) {
                String uniqueName = tokenizer.nextToken();
                Vector3D location = getVector(tokenizer.nextToken());
                float radius = Float.parseFloat(tokenizer.nextToken());
                float height = Float.parseFloat(tokenizer.nextToken());
                TriggerPolygonGroup trigger = new TriggerPolygonGroup(uniqueName, location, radius, height);
                mapObjects.addElement(trigger);
            }
            else if (command.equals("obj")) {
                // create a new obj from an object file
                String uniqueName = tokenizer.nextToken();
                String filename = tokenizer.nextToken();
                // check if the object is already loaded
                PolygonGroup object =
                    (PolygonGroup)loadedObjects.get(filename);
                if (object == null) {
//                    File file = new File(path, filename);
//                    String filePath = file.getPath();
//                    object = objectLoader.loadObject(path, filePath);
                  object = objectLoader.loadObject(filename);
                    loadedObjects.put(filename, object);
                }
                Vector3D loc = getVector(tokenizer.nextToken());
                PolygonGroup mapObject =
                    (PolygonGroup)object.clone();
                mapObject.getTransform().getLocation().setTo(loc);
                if (!uniqueName.equals("null")) {
                    mapObject.setName(uniqueName);
                }
                if (tokenizer.hasMoreTokens()) {
                    mapObject.getTransform().setAngleY(
                        Float.parseFloat(tokenizer.nextToken()));
                }
                mapObjects.addElement(mapObject);
            }
            else if (command.equals("room")) {
                // start a new room
                currentRoom = new RoomDef(ambientLightIntensity);
                if (tokenizer.hasMoreTokens()) {
                	currentRoom.setName(tokenizer.nextToken());
                }
                rooms.addElement(currentRoom);
                
            }
            else if (command.equals("floor")) {
                // define a room's floor
                float y = Float.parseFloat(tokenizer.nextToken());
                currentRoom.setFloor(y, currentMaterial);
            }
            else if (command.equals("ceil")) {
                // define a room's ceiling
                float y = Float.parseFloat(tokenizer.nextToken());
                currentRoom.setCeil(y, currentMaterial);
            }
            else if (command.equals("wall")) {
                // define a wall vertex in a room.
                float x = Float.parseFloat(tokenizer.nextToken());
                float z = Float.parseFloat(tokenizer.nextToken());
                if (tokenizer.hasMoreTokens()) {
                    float bottom =
                        Float.parseFloat(tokenizer.nextToken());
                    float top =
                        Float.parseFloat(tokenizer.nextToken());
                    currentRoom.addVertex(x, z, bottom, top,
                        currentMaterial);
                }
                else {
                    currentRoom.addVertex(x, z,
                        currentMaterial);
                }
            }
            else if (command.equals("poster")) {
//            	String uniqueName = tokenizer.nextToken();
//            	Vector3D v0 = new Vector3D(0 , 0, 0);
//            	Vector3D location = getVector(tokenizer.nextToken());
//            	Vector3D v1 = getVector(tokenizer.nextToken());
//            	v1.y = location.y; // Force poster to be a rectangle
//            	v1.subtract(location);
//                float h = Float.parseFloat(tokenizer.nextToken());
//            	Vector3D v2 = new Vector3D(v1.x , v1.y + h, v1.z);
//            	Vector3D v3 = new Vector3D(v0.x , v0.y + h, v0.z);
//            	TexturedPolygon3D polygon = new TexturedPolygon3D(v0, v1, v2, v3);
//            	
//            	if(currentMaterial.texture != null) {
////            		polygon.setTexture(currentMaterial.texture);
////            		Rectangle3D boundingRect = polygon.calcBoundingRectangle();
////            		int rectW = (int) Math.floor(boundingRect.getWidth() + 0.5d);
////            		int rectH = (int) Math.floor(boundingRect.getHeight() + 0.5d);
//            		int rectW = (int) Math.floor(v1.length() + 0.5d);
//            		int rectH = (int) Math.floor(h + 0.5d);
//            		AnimatedRectangularSurface rectTexture = new AnimatedRectangularSurface(currentMaterial.texture, rectW, rectH);
//            		v0.subtract(v3);
//            		Rectangle3D textureBounds = new Rectangle3D(v3, v1, v0, rectW, rectH); 
//            		polygon.setTexture(rectTexture, textureBounds);
//            	}
            	
            	// Add the polygon group to the object list
            	String uniqueName = tokenizer.nextToken();
            	String typeString = tokenizer.nextToken();
            	int type;
            	if (typeString.equalsIgnoreCase("wall")) {
            		type = PosterPolygonGroup.TYPE_WALL;
            	} else if (typeString.equalsIgnoreCase("floor")) {
            		type = PosterPolygonGroup.TYPE_FLOOR;
            	} else if (typeString.equalsIgnoreCase("ceil")) {
            		type = PosterPolygonGroup.TYPE_CEIL;
            	} else {
            		type = PosterPolygonGroup.TYPE_UNKNOWN;
            	}
            	Vector3D location = getVector(tokenizer.nextToken());
            	Vector3D edge = getVector(tokenizer.nextToken());
            	float h = Float.parseFloat(tokenizer.nextToken());
            	float framesPerSecond = Float.parseFloat(tokenizer.nextToken());
        		PosterPolygonGroup poster = new PosterPolygonGroup(type, location, edge, h, currentMaterial, framesPerSecond);
            	if (!uniqueName.equals("null")) {
            		poster.setName(uniqueName);
                }
            	if (tokenizer.hasMoreTokens()) {
            		poster.getTransform().setAngleY(
                        Float.parseFloat(tokenizer.nextToken()));
                }
            	mapObjects.addElement(poster);
            }
            else if (command.equals("BoxModel")) {
            	String uniqueName = tokenizer.nextToken();
            	if (tokenizer.hasMoreTokens()) {
            		String endString = tokenizer.nextToken();
            		checkKeyword(endString, "end");
            		boxDefs.put(uniqueName, currentBoxDef);
            	} else {
            		currentBoxDef = BoxModel.createBoxDef(uniqueName);
            	}
            }
            else if (command.equals("face")) {
            	//face up/down/north/south/east/west <static/animated> <stretch/repeat> [frame_rate]
            	String faceTypeString = tokenizer.nextToken();
            	int type = BoxModel.FaceModel.getType(faceTypeString); 
            	String animationMode = tokenizer.nextToken();
            	boolean animated = true;
            	String textureMode = tokenizer.nextToken();
            	boolean stretched = false;
            	if (textureMode.equalsIgnoreCase("stretch")) {
            		stretched = true;
            	}
            	float framesPerSecond = 0;
            	if (tokenizer.hasMoreTokens()) {
            		framesPerSecond = Float.parseFloat(tokenizer.nextToken());
            	}
            	currentBoxDef.setFaceModel(type, currentMaterial, animated, stretched, framesPerSecond);
            } else if (command.equals("box")) {
            	//box <boxes_name> <BoxDef_name> <location_index> <scale> [<rotate_x> <rotate_y> <rotate_z>]
            	String uniqueName = tokenizer.nextToken();
            	String boxDefName = tokenizer.nextToken();
            	Vector3D location = getVector(tokenizer.nextToken());
            	float scale = Float.parseFloat(tokenizer.nextToken());
            	BoxModel boxDef = (BoxModel) boxDefs.get(boxDefName);
            	BoxPolygonGroup box = new BoxPolygonGroup(boxDef, location, scale, ambientLightIntensity);
            	//box.getTransform().getLocation().setTo(location);
            	if (!uniqueName.equals("null")) {
            		box.setName(uniqueName);
                }
            	mapObjects.addElement(box);
            } else if (command.equals("skybox")) {
            	//skybox <BoxDef_name> <location_index> <scale> [<rotate_x> <rotate_y> <rotate_z>]
            	String boxDefName = tokenizer.nextToken();
            	Vector3D location = getVector(tokenizer.nextToken());
            	float scale = Float.parseFloat(tokenizer.nextToken());
            	BoxModel boxDef = (BoxModel) boxDefs.get(boxDefName);
            	BoxPolygonGroup box = new BoxPolygonGroup(boxDef, location, scale, ambientLightIntensity, true);
            	//box.getTransform().getLocation().setTo(location);
            	mapObjects.addElement(box);
            } else if (command.equals("boxBlock")) {
            	//box <boxes_name> <BoxDef_name> <location_index> <scale> [<rotate_x> <rotate_y> <rotate_z>]
            	String uniqueName = tokenizer.nextToken();
            	String boxDefName = tokenizer.nextToken();
            	Vector3D location = getVector(tokenizer.nextToken());
            	float scale = Float.parseFloat(tokenizer.nextToken());
            	int countX = Integer.parseInt(tokenizer.nextToken());
            	int countY = Integer.parseInt(tokenizer.nextToken());
            	int countZ = Integer.parseInt(tokenizer.nextToken());
            	BoxModel boxDef = (BoxModel) boxDefs.get(boxDefName);
            	BoxBlockPolygonGroup block = new BoxBlockPolygonGroup(boxDef, location, scale, countX, countY, countZ, ambientLightIntensity);
            	if (!uniqueName.equals("null")) {
            		block.setName(uniqueName);
                }
            	Vector elements = block.getElements();
				int size = elements.size();
				for (int j = 0; j < size; j++) {
					mapObjects.addElement((PolygonGroup)elements.elementAt(j));
				}
            } else if (command.equals("voxelMatrix")) {
            	//box <boxes_name> <BoxDef_name> <location_index> <scale> [<rotate_x> <rotate_y> <rotate_z>]
            	String uniqueName = tokenizer.nextToken();
            	String matrixFileName = tokenizer.nextToken();
            	String matrixName = tokenizer.nextToken();
            	Vector3D location = getVector(tokenizer.nextToken());
            	float scale = Float.parseFloat(tokenizer.nextToken());
            	//BoxModel boxDef = (BoxModel) boxDefs.get(boxDefName);
            	InputStream is = resourceLoader.getInputStream(matrixFileName);
            	QBLoader qbLoader = new QBLoader();
            	qbLoader.load(is);
            	QBMatrix matrix = qbLoader.getMatrix(matrixName);
            	if (matrix != null) {
					VoxelMatrixPolygonGroup voxelMatrix = new VoxelMatrixPolygonGroup(matrix, location, scale, ambientLightIntensity);
					if (!uniqueName.equals("null")) {
						voxelMatrix.setName(uniqueName);
					}
					voxelMatrix.setFilename(matrixName);
					mapObjects.addElement(voxelMatrix);
//					Vector elements = voxelMatrix.getElements();
//					int size = elements.size();
//					for (int j = 0; j < size; j++) {
//						PolygonGroup group = (PolygonGroup) elements.elementAt(j);
//						group.getTransform().getLocation().add(voxelMatrix.getTransform());
//						mapObjects.addElement((PolygonGroup) elements.elementAt(j));
//					}
            	} else {
            		System.out.println("Warning: Qubicle matrix " + matrixName + " can't be loaded");
            	}
            }
            else {
                System.out.println("Unknown command: " + command);
            }
        }
    }
    
    public boolean checkKeyword(String keyword, String expectedString) {
    	return true;
    }
}