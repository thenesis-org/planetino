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
package org.thenesis.planetino2.bsp2D;

import java.util.Enumeration;
import java.util.Vector;

import org.thenesis.planetino2.graphics3D.texture.Texture;
import org.thenesis.planetino2.math3D.ObjectLoader.Material;
import org.thenesis.planetino2.math3D.Rectangle3D;
import org.thenesis.planetino2.math3D.Vector3D;

/**
 The RoomDef class represents a convex room with walls, a
 floor, and a ceiling. The floor may be above the ceiling, in
 which case the RoomDef is a "pillar" or "block" structure,
 rather than a "room". RoomDefs are used as a shortcut
 to create the actual BSPPolygons used in the 2D BSP tree.
 */
public class RoomDef {

	private static final Vector3D FLOOR_NORMAL = new Vector3D(0, 1, 0);

	private static final Vector3D CEIL_NORMAL = new Vector3D(0, -1, 0);

	private Floor floor;
	private Ceil ceil;
	private Vector vertices;
	private float ambientLightIntensity;

	private String roomName;

	/**
	 The HorizontalAreaDef class represents a floor or ceiling.
	 */
	public abstract static class HorizontalAreaDef {
		float height;
		Material material;
		Texture texture;
		Rectangle3D textureBounds;
		RoomDef roomDef;

		public HorizontalAreaDef(RoomDef roomDef, float height, Material material, Rectangle3D textureBounds) {
			this.roomDef = roomDef;
			this.height = height;
			setMaterial(material);
			this.textureBounds = textureBounds;
		}
		
		public void setMaterial(Material material) {
			this.material = material;
			this.texture = material.texture;
		}

		public Material getMaterial() {
			return material;
		}

		public RoomDef getRoomDef() {
			return roomDef;
		}
		
		public void setHeight(float height) {
			this.height = height;
		}

		public float getHeight() {
			return height;
		}
		
	}
	
	public static class Ceil extends HorizontalAreaDef {

		public Ceil(RoomDef roomDef, float height, Material material, Rectangle3D textureBounds) {
			super(roomDef, height, material, textureBounds);
		}
		
		@Override
		public String toString() {
			return "Ceil (" + height + ")";
		}
		
	}
	
	public static class Floor extends HorizontalAreaDef {

		public Floor(RoomDef roomDef, float height, Material material, Rectangle3D textureBounds) {
			super(roomDef, height, material, textureBounds);
		}
		
		@Override
		public String toString() {
			return "Floor (" + height + ")";
		}
		
	}
	
	

	/**
	 The Vertex class represents a Wall vertex.
	 */
	public static class Vertex {
		private RoomDef roomDef;
		private float x;
		private float z;
		private float bottom;
		private float top;
		private Material material;
		private Texture texture;
		private Rectangle3D textureBounds;

		public Vertex(RoomDef roomDef, float x, float z, float bottom, float top, Material material, Rectangle3D textureBounds) {
			this.roomDef = roomDef;
			this.x = x;
			this.z = z;
			this.bottom = bottom;
			this.top = top;
			setMaterial(material);
			this.textureBounds = textureBounds;
		}

		public boolean isWall() {
			return (bottom != top) && (texture != null);
		}
		
		public float getX() {
			return x;
		}

		public float getZ() {
			return z;
		}

		public float getBottom() {
			return bottom;
		}

		public float getTop() {
			return top;
		}

		void setX(float x) {
			this.x = x;
		}

		void setZ(float z) {
			this.z = z;
		}

		void setBottom(float bottom) {
			this.bottom = bottom;
		}

		void setTop(float top) {
			this.top = top;
		}
		
		public void setMaterial(Material material) {
			this.material = material;
			this.texture = material.texture;
		}

		public Material getMaterial() {
			return material;
		}

		public RoomDef getRoomDef() {
			return roomDef;
		}

		@Override
		public String toString() {
			return "Vertex (" + x + ", " + z + ", " + bottom + ", " + top + ")";
		}
		
	}

	/**
	 Creates a new RoomDef with an ambient light intensity of
	 0.5. The walls, floors and ceiling all use this
	 ambient light intensity.
	 */
	public RoomDef() {
		this(0.5f);
	}

	/**
	 Creates a new RoomDef with the specified
	 ambient light intensity. The walls, floors and ceiling
	 all use this ambient light intensity.
	 */
	public RoomDef(float ambientLightIntensity) {
		this.ambientLightIntensity = ambientLightIntensity;
		vertices = new Vector();
	}

	/**
	 Adds a new wall vertex at the specified (x,z) location,
	 with the specified texture. The wall stretches from
	 the floor to the ceiling. If the texture is null,
	 no polygon for the wall is created.
	 */
	public void addVertex(float x, float z, Material material) {
		addVertex(x, z, Math.min(floor.height, ceil.height), Math.max(floor.height, ceil.height), material);
	}

	/**
	 Adds a new wall vertex at the specified (x,z) location,
	 with the specified texture, bottom location, and top
	 location. If the texture is null, no polygon for the wall
	 is created.
	 */
	public void addVertex(float x, float z, float bottom, float top, Material material) {
		vertices.addElement(new Vertex(this, x, z, bottom, top, material, null));
	}

	/**
	 Adds a new wall vertex at the specified (x,z) location,
	 with the specified texture, texture bounds, bottom
	 location, and top location. If the texture is null, no
	 polygon for the wall is created.
	 */
	public void addVertex(float x, float z, float bottom, float top, Material material, Rectangle3D texBounds) {
		vertices.addElement(new Vertex(this, x, z, bottom, top, material, texBounds));
	}
	
	public void setVertexX(Vertex v, float x) {
		v.setX(x);
		invalidate();
	}
	
	public void setVertexZ(Vertex v, float z) {
		v.setZ(z);
		invalidate();
	}
	
	public void setVertexBottom(Vertex v, float bottom) {
		v.setBottom(bottom);
		invalidate();
	}
	
	public void setVertexTop(Vertex v, float top) {
		v.setTop(top);
		invalidate();
	}
	
	public void setVertexMaterial(Vertex v, Material material) {
		v.setMaterial(material);
		invalidate();
	}
	
	public void setCeilMaterial(Ceil c, Material material) {
		c.setMaterial(material);
		invalidate();
	}
	
	public void setHorizontalAreaHeight(HorizontalAreaDef h, float height) {
		h.setHeight(height);
		invalidate();
	}
	
	public void setFloorMaterial(Floor f, Material material) {
		f.setMaterial(material);
		invalidate();
	}

	/**
	 Sets the floor height and floor texture of this room. If
	 the texture is null, no floor polygon is created, but the
	 height of the floor is used as the default bottom wall
	 boundary.
	 */
	public void setFloor(float height, Material material) {
		setFloor(height, material, null);
	}

	/**
	 Sets the floor height, floor texture, and floor texture
	 bounds of this room. If the texture is null, no floor
	 polygon is created, but the height of the floor is used as
	 the default bottom wall boundary. If the texture bounds is
	 null, a default texture bounds is used.
	 */
	public void setFloor(float height, Material material, Rectangle3D texBounds) {
		Texture texture = material.texture;
		if (texture != null && texBounds == null) {
			texBounds = new Rectangle3D(new Vector3D(0, height, 0), new Vector3D(1, 0, 0), new Vector3D(0, 0, -1),
					texture.getWidth(), texture.getHeight());
		}
		floor = new Floor(this, height, material, texBounds);
	}

	/**
	 Sets the ceiling height and ceiling texture of this room.
	 If the texture is null, no ceiling polygon is created, but
	 the height of the ceiling is used as the default top wall
	 boundary.
	 */
	public void setCeil(float height, Material material) {
		setCeil(height, material, null);
	}

	/**
	 Sets the ceiling height, ceiling texture, and ceiling
	 texture bounds of this room. If the texture is null, no
	 floor polygon is created, but the height of the floor is
	 used as the default bottom wall boundary. If the texture
	 bounds is null, a default texture bounds is used.
	 */
	public void setCeil(float height, Material material, Rectangle3D texBounds) {
		Texture texture = material.texture;
		if (texture != null && texBounds == null) {
			texBounds = new Rectangle3D(new Vector3D(0, height, 0), new Vector3D(1, 0, 0), new Vector3D(0, 0, 1),
					texture.getWidth(), texture.getHeight());
		}
		ceil = new Ceil(this, height, material, texBounds);
	}

	/**
	 Creates and returns a list of BSPPolygons that represent
	 the walls, floor, and ceiling of this room.
	 */
	public Vector createPolygons() {
		Vector walls = createVerticalPolygons();
		Vector floors = createHorizontalPolygons();

		Vector list = new Vector(walls.size() + floors.size());

		//      list.addAll(walls);
		//      list.addAll(floors);
		Enumeration e = walls.elements();
		while (e.hasMoreElements()) {
			list.addElement(e.nextElement());
		}
		e = floors.elements();
		while (e.hasMoreElements()) {
			list.addElement(e.nextElement());
		}

		return list;
	}

	/**
	 Creates and returns a list of BSPPolygons that represent
	 the vertical walls of this room.
	 */
	public Vector createVerticalPolygons() {
		int size = vertices.size();
		Vector list = new Vector(size);
		if (size == 0) {
			return list;
		}
		Vertex origin = (Vertex) vertices.elementAt(0);
		Vector3D textureOrigin = new Vector3D(origin.x, ceil.height, origin.z);
		Vector3D textureDy = new Vector3D(0, -1, 0);

		for (int i = 0; i < size; i++) {
			Vertex curr = (Vertex) vertices.elementAt(i);

			if (!curr.isWall()) {
				continue;
			}

			// determine if wall is passable (useful for portals)
			int type = BSPPolygon.TYPE_WALL;
			if (floor.height > ceil.height) {
				if (floor.height - ceil.height <= BSPPolygon.PASSABLE_WALL_THRESHOLD) {
					type = BSPPolygon.TYPE_PASSABLE_WALL;
				}
			} else if (curr.top - curr.bottom <= BSPPolygon.PASSABLE_WALL_THRESHOLD) {
				type = BSPPolygon.TYPE_PASSABLE_WALL;
			} else if (curr.bottom - floor.height >= BSPPolygon.PASSABLE_ENTRYWAY_THRESHOLD) {
				type = BSPPolygon.TYPE_PASSABLE_WALL;
			}

			Vector wallVertices = new Vector();
			Vertex prev;
			Vertex next;
			if (floor.height < ceil.height) {
				prev = (Vertex) vertices.elementAt((i + size - 1) % size);
				next = (Vertex) vertices.elementAt((i + 1) % size);
			} else {
				prev = (Vertex) vertices.elementAt((i + 1) % size);
				next = (Vertex) vertices.elementAt((i + size - 1) % size);
			}

			// bottom vertices
			wallVertices.addElement(new Vector3D(next.x, curr.bottom, next.z));
			wallVertices.addElement(new Vector3D(curr.x, curr.bottom, curr.z));

			// optional vertices at T-Junctions on left side
			if (prev.isWall()) {
				if (prev.bottom > curr.bottom && prev.bottom < curr.top) {
					wallVertices.addElement(new Vector3D(curr.x, prev.bottom, curr.z));
				}
				if (prev.top > curr.bottom && prev.top < curr.top) {
					wallVertices.addElement(new Vector3D(curr.x, prev.top, curr.z));
				}

			}

			// top vertives
			wallVertices.addElement(new Vector3D(curr.x, curr.top, curr.z));
			wallVertices.addElement(new Vector3D(next.x, curr.top, next.z));

			// optional vertices at T-Junctions on left side
			if (next.isWall()) {
				if (next.top > curr.bottom && next.top < curr.top) {
					wallVertices.addElement(new Vector3D(next.x, next.top, next.z));
				}
				if (next.bottom > curr.bottom && next.bottom < curr.top) {
					wallVertices.addElement(new Vector3D(next.x, next.bottom, next.z));
				}

			}

			// create wall polygon
			Vector3D[] array = new Vector3D[wallVertices.size()];
			//wallVertices.toArray(array);
			wallVertices.copyInto(array);
			BSPPolygon poly = new BSPPolygon(array, type);
			poly.setAmbientLightIntensity(ambientLightIntensity);
			if (curr.textureBounds == null) {
				Vector3D textureDx = new Vector3D(next.x, 0, next.z);
				textureDx.subtract(new Vector3D(curr.x, 0, curr.z));
				textureDx.normalize();
				curr.textureBounds = new Rectangle3D(textureOrigin, textureDx, textureDy, curr.texture.getWidth(),
						curr.texture.getHeight());
			}
			poly.setTexture(curr.texture, curr.textureBounds);
			list.addElement(poly);
		}
		return list;
	}

	/**
	 Creates and returns a list of BSPPolygons that represent
	 the horizontal floor and ceiling of this room.
	 */
	public Vector createHorizontalPolygons() {

		Vector list = new Vector(2);
		int size = vertices.size();
		Vector3D[] floorVertices = new Vector3D[size];
		Vector3D[] ceilVertices = new Vector3D[size];

		// create vertices
		for (int i = 0; i < size; i++) {
			Vertex v = (Vertex) vertices.elementAt(i);
			floorVertices[i] = new Vector3D(v.x, floor.height, v.z);
			ceilVertices[size - (i + 1)] = new Vector3D(v.x, ceil.height, v.z);
		}

		// create floor polygon
		if (floor.texture != null) {
			BSPPolygon poly = new BSPPolygon(floorVertices, BSPPolygon.TYPE_FLOOR);
			poly.setTexture(floor.texture, floor.textureBounds);
			poly.setNormal(FLOOR_NORMAL);
			poly.setAmbientLightIntensity(ambientLightIntensity);
			list.addElement(poly);
		}

		// create ceiling polygon
		if (ceil.texture != null) {
			BSPPolygon poly = new BSPPolygon(ceilVertices, BSPPolygon.TYPE_FLOOR);
			poly.setTexture(ceil.texture, ceil.textureBounds);
			poly.setNormal(CEIL_NORMAL);
			poly.setAmbientLightIntensity(ambientLightIntensity);
			list.addElement(poly);
		}

		return list;
	}
	
	public void moveX(float x) {
		int wallVertexCount = vertices.size();
		for (int i = 0; i < wallVertexCount; i++) {
			Vertex vertex = (Vertex) vertices.elementAt(i);
			vertex.setX(vertex.getX() + x);
		}
		invalidate();
	}
	
	public void moveZ(float z) {
		int wallVertexCount = vertices.size();
		for (int i = 0; i < wallVertexCount; i++) {
			Vertex vertex = (Vertex) vertices.elementAt(i);
			vertex.setZ(vertex.getZ() + z);
		}
		invalidate();
	}
	
	public void moveY(float y) {
		int wallVertexCount = vertices.size();
		for (int i = 0; i < wallVertexCount; i++) {
			Vertex vertex = (Vertex) vertices.elementAt(i);
			vertex.setBottom(vertex.getBottom() + y);
			vertex.setTop(vertex.getTop() + y);
		}
		
		floor.height += y;
		ceil.height += y;
		
		invalidate();
	}
	
	public void stretchX(float x) {
		
		int wallVertexCount = vertices.size();
		if (wallVertexCount <= 0) {
			return;
		}
		
		Vertex vertex = (Vertex) vertices.elementAt(0);
		float minX = vertex.getX();
		float maxX = vertex.getX();
		for (int i = 1; i < wallVertexCount; i++) {
			vertex = (Vertex) vertices.elementAt(i);
			minX = Math.min(vertex.getX(), minX);
			maxX = Math.max(vertex.getX(), maxX);
		}
		
		float middleX = minX + (maxX - minX) / 2;
		
		for (int i = 0; i < wallVertexCount; i++) {
			vertex = (Vertex) vertices.elementAt(i);
			float diff = vertex.getX() - middleX;
			if (diff < 0) {
				vertex.setX(vertex.getX() - x);
			} else {
				vertex.setX(vertex.getX() + x);
			}
		}
		
		invalidate();
	}
	
	public void stretchZ(float z) {
		
		int wallVertexCount = vertices.size();
		if (wallVertexCount <= 0) {
			return;
		}
		
		Vertex vertex = (Vertex) vertices.elementAt(0);
		float minZ = vertex.getZ();
		float maxZ = vertex.getZ();
		for (int i = 1; i < wallVertexCount; i++) {
			vertex = (Vertex) vertices.elementAt(i);
			minZ = Math.min(vertex.getZ(), minZ);
			maxZ = Math.max(vertex.getZ(), maxZ);
		}
		
		float middleZ = minZ + (maxZ - minZ) / 2;
		
		for (int i = 0; i < wallVertexCount; i++) {
			vertex = (Vertex) vertices.elementAt(i);
			float diff = vertex.getZ() - middleZ;
			if (diff < 0) {
				vertex.setZ(vertex.getZ() - z);
			} else {
				vertex.setZ(vertex.getZ() + z);
			}
		}
		
		invalidate();
	}
	
	
	public void stretchY(float y) {
		
		int wallVertexCount = vertices.size();
		if (wallVertexCount <= 0) {
			return;
		}
		
		for (int i = 0; i < wallVertexCount; i++) {
			Vertex vertex = (Vertex) vertices.elementAt(i);
			vertex.setBottom(vertex.getBottom() - y);
			vertex.setTop(vertex.getTop() + y);
		}
		floor.height -= y;
		ceil.height += y;
		
		invalidate();
	}
	
	public void removeVertex(Vertex vertex) {
		vertices.remove(vertex);
		invalidate(); // Not really needed currently
	}
	
	/**
	 * Should be called when a vertex has been modified after RoomDef creation
	 */
	private void invalidate() {
		int size = vertices.size();
		for (int i = 0; i < size; i++) {
			Vertex curr = (Vertex) vertices.elementAt(i);
			curr.textureBounds = null;
		}
	}

	public void setName(String name) {
		this.roomName = name;
	}

	public String getName() {
		return roomName;
	}

	@Override
	public String toString() {
		if (roomName != null) {
			return "Room " + roomName;
		} else {
			return super.toString();
		}
	}
	
	public Vector getWallVertices() {
		return vertices;
	}

	public Floor getFloor() {
		return floor;
	}

	public Ceil getCeil() {
		return ceil;
	}

	public float getAmbientLightIntensity() {
		return ambientLightIntensity;
	}

	public void setAmbientLightIntensity(float ambientLightIntensity) {
		this.ambientLightIntensity = ambientLightIntensity;
		invalidate();
	}
	
	
	

}
