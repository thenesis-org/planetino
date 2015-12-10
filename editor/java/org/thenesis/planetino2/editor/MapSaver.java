package org.thenesis.planetino2.editor;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.thenesis.planetino2.bsp2D.MapLoader;
import org.thenesis.planetino2.bsp2D.RoomDef;
import org.thenesis.planetino2.bsp2D.RoomDef.Ceil;
import org.thenesis.planetino2.bsp2D.RoomDef.Floor;
import org.thenesis.planetino2.bsp2D.RoomDef.Vertex;
import org.thenesis.planetino2.math3D.ObjectLoader.Material;
import org.thenesis.planetino2.math3D.PointLight3D;
import org.thenesis.planetino2.math3D.PolygonGroup;
import org.thenesis.planetino2.math3D.Transform3D;
import org.thenesis.planetino2.math3D.Vector3D;

/**
The MapSaver class saves maps in a text file based on
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
    [intensity]        specfied vector. Optionally, light
    [falloff]          intesity and falloff distance can
                       be specified.
player [v] [angle]   - Specifies the starting location of the
                       player and optionally a starting
                       angle, in radians, around the y-axis.
obj [uniqueName]     - Defines an object from an external
    [filename] [v]     OBJ file. The unique name allows this
    [angle]            object to be uniquely identfied, but
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
</pre>
*/
public class MapSaver {
	
	private static final String S = " ";
	private MapLoader mapLoader;
	private String filename;
	private PrintWriter writer;
	private String currentMaterialLibrary ;
	private String currentMaterial;
	
	MapSaver(MapLoader mapLoader, String filename) {
		this.mapLoader = mapLoader;
		this.filename = filename;
	}
	
	public void save() {
		
		currentMaterialLibrary = "";
		currentMaterial = "";
		
		Transform3D playerStartTransform = mapLoader.getPlayerStartTransform();
		Vector3D playerStartLocation = playerStartTransform.getLocation();
	    Vector rooms = mapLoader.getRooms();
	    Vector mapObjects = mapLoader.getObjectsInMap();
	    Vector lights = mapLoader.getLights();
	    
	    try {
			writer = new PrintWriter(filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
	    
	    /* Try to guess default material */
	    
	    // Create a list of the material library names
	    Hashtable materialMap = mapLoader.getMaterials();
	    Hashtable materialLibraryMap = new Hashtable();
	    Enumeration materialEnumeration = materialMap.elements();
	    while (materialEnumeration.hasMoreElements()) {
	    	Material material = (Material)materialEnumeration.nextElement();
	    	if (material.library != null) {
	    		materialLibraryMap.put(material.library, material.library);
	    	}
	    }
	    
	    // If only one material library is used, set it as default at the beginning of the file
	    if (materialLibraryMap.size() == 1) {
	    	Enumeration e = materialLibraryMap.keys();
	    	String materialLibrary = (String) e.nextElement();
	    	currentMaterialLibrary = materialLibrary;
			writer.println("# Default material library");
			writer.println("mtllib" + S + materialLibrary);
			writer.println();
	    }
	    
	    
	    /* Player start position */
	    
	    writer.println("# Player start location");
	    writeVector3D(playerStartLocation);
	    writer.print("player" + S + "-1" + S);
	    float angle = playerStartTransform.getAngleY();
	    if (angle != 0) {
	    	writer.print(angle);
	    }
	    writer.println();
	    writer.println();
	    
	    /* Rooms */
	    writer.println("###############");
	    writer.println("# Rooms");
	    writer.println("###############");
	    writer.println("");
	    int size = rooms.size();
	    for (int i = 0; i < size; i++) {
	    	currentMaterial = ""; // For better readability, reset current material to force material definition at the beginning of each room
	    	RoomDef roomDef = (RoomDef) rooms.elementAt(i);
	    	writer.println("ambientLightIntensity" + S + roomDef.getAmbientLightIntensity());
	    	writer.println("room" + S + roomDef.getName());
	    	Floor floor = roomDef.getFloor();
	    	writeUseMaterial(floor.getMaterial());
	    	writer.println("floor" + S + floor.getHeight());
	    	Ceil ceil = roomDef.getCeil();
	    	writeUseMaterial(ceil.getMaterial());
	    	writer.println("ceil" + S + ceil.getHeight());
	    	
	    	Vector vertices = roomDef.getWallVertices();
	    	int verticesCount = vertices.size();
	    	for (int j = 0; j < verticesCount; j++) {
	    		Vertex vertex = (Vertex)vertices.elementAt(j);
	    		Material material = vertex.getMaterial();
	    		writeUseMaterial(material);
	    		writer.print("wall" + S + vertex.getX() + S + vertex.getZ() + S);
	    		//if vertex.isBottomOrTopExplicitlyDefined
	    		writer.println(vertex.getBottom() + S + vertex.getTop());
	    	}
	    	
	    	writer.println();	
	    }
	    
	    writer.println("###############");
	    writer.println("# Objects");
	    writer.println("###############");
	    writer.println("");
	    
	    size = mapObjects.size();
	    for (int i = 0; i < size; i++) {
	    	PolygonGroup polygonGroup = (PolygonGroup) mapObjects.elementAt(i);
	    	Vector3D location = polygonGroup.getTransform().getLocation();
	    	angle = polygonGroup.getTransform().getAngleY();
	    	writeVector3D(location);
	    	String name = polygonGroup.getName();
	    	if (name.equals(PolygonGroup.NO_NAME)) {
	    		name = "null";
	    	}
	    	writer.print("obj" + S + name + S + polygonGroup.getFilename() + S + "-1" + S);
	    	if (angle != 0) {
	    		writer.println(angle);
	    	} else {
	    		writer.println();
	    	}
	    	writer.println();
	    }
	    
	    writer.println("###############");
	    writer.println("# Lights");
	    writer.println("###############");
	    writer.println("");
	    
	    size = lights.size();
	    for (int i = 0; i < size; i++) {
	    	PointLight3D pointLight = (PointLight3D) lights.elementAt(i);
	    	writeVector3D(pointLight);
	    	writer.println("pointlight" + S + "-1" + S + pointLight.getIntensity() + S + pointLight.getDistanceFalloff());
	    	writer.println();
	    }
	    
	    writer.println();
	    
	    writer.flush();
    	writer.close();
		
	}
	
	private void writeUseMaterial(Material material) {
		if ((material.library != null) && (!currentMaterialLibrary.equals(material.library))) {
			writer.println("mtllib" + S + material.library);
			currentMaterialLibrary = material.library;
		}
		if (!currentMaterial.equals(material.name)) {
			writer.println("usemtl" + S + material.name);
			currentMaterial = material.name;
		}
		
	}
	
	private void writeVector3D(Vector3D v) {
		writer.println("v" + S + v.x + S + v.y + S + v.z);
	}
	

}
