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

/**
 * The PolygonGroupBounds represents both a cylindrical bounds (via a bounding sphere) and an AABB (axis-aligned bounding box) on the XZ plane around a PolygonGroup, which can be
 * used for collision detection.
 * 
 * A flag is maintained (cylindricalCollision) indicating whether the collision detection is likely to be more effective using a cylinder (bounding sphere) or an AABB.
 */
public class PolygonGroupBounds {

	private float topHeight;
	private float bottomHeight;
	private float radius; // bounding sphere radius

	// AABB bounds on the XZ plane
	private float minX, maxX;
	private float minZ, maxZ;

	// Flag indiquant si la collision est mieux détectée avec un cylindre (true) ou une AABB (false)
	private boolean cylindricalCollision;

	/**
	 * Crée un PolygonGroupBounds vide.
	 */
	public PolygonGroupBounds() {
	}

	/**
	 * Crée un PolygonGroupBounds en calculant les bornes pour le groupe.
	 */
	public PolygonGroupBounds(PolygonGroup group) {
		setToBounds(group);
	}

	/**
	 * Calcule et stocke les bornes du PolygonGroup, à la fois pour le bounding sphere et l'AABB sur le plan XZ. Ensuite, en fonction de la largeur et de la profondeur, détermine
	 * si la collision sera mieux détectée avec un cylindre (bounding sphere) ou avec une AABB.
	 */
	public void setToBounds(PolygonGroup group) {
		topHeight = -Float.MAX_VALUE;
		bottomHeight = Float.MAX_VALUE;
		minX = Float.MAX_VALUE;
		maxX = -Float.MAX_VALUE;
		minZ = Float.MAX_VALUE;
		maxZ = -Float.MAX_VALUE;
		radius = 0;

		group.resetIterator();
		while (group.hasNext()) {
			Polygon3D poly = group.nextPolygon();
			for (int i = 0; i < poly.getNumVertices(); i++) {
				Vector3D v = poly.getVertex(i);
				topHeight = Math.max(topHeight, v.y);
				bottomHeight = Math.min(bottomHeight, v.y);
				minX = Math.min(minX, v.x);
				maxX = Math.max(maxX, v.x);
				minZ = Math.min(minZ, v.z);
				maxZ = Math.max(maxZ, v.z);
				// Calcul pour le bounding sphere : maximum de v.x² + v.z²
				radius = Math.max(radius, v.x * v.x + v.z * v.z);
			}
		}

		if (radius == 0) {
			// Aucun sommet n'est présent
			topHeight = 0;
			bottomHeight = 0;
		} else {
			radius = (float) Math.sqrt(radius);
		}

		// Calcul de la largeur et de la profondeur pour l'AABB dans le plan XZ
		float width = getWidth();
		float depth = getDepth();
		if (width <= 0 || depth <= 0) {
			cylindricalCollision = true;
		} else {
			// Si le rapport entre la dimension la plus grande et la plus petite est proche de 1,
			// la forme est presque circulaire et le cylindre est adapté.
			float ratio = Math.max(width, depth) / Math.min(width, depth);
			cylindricalCollision = (ratio <= 1.2f);
		}
	}

	// Accesseurs pour le bounding sphere
	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	// Accesseurs pour la hauteur
	public float getTopHeight() {
		return topHeight;
	}

	public void setTopHeight(float topHeight) {
		this.topHeight = topHeight;
	}

	public float getBottomHeight() {
		return bottomHeight;
	}

	public void setBottomHeight(float bottomHeight) {
		this.bottomHeight = bottomHeight;
	}

	// Accesseurs pour l'AABB sur le plan XZ
	public float getMinX() {
		return minX;
	}

	public float getMaxX() {
		return maxX;
	}

	public float getMinZ() {
		return minZ;
	}

	public float getMaxZ() {
		return maxZ;
	}
	
	public void setMinX(float minX) {
		this.minX = minX;	
	}
	
	public void setMaxX(float maxX) {
		this.maxX = maxX;
	}
	
	public void setMinZ(float minZ) {
		this.minZ = minZ;
	}

	public void setMaxZ(float maxZ) {
        this.maxZ = maxZ;
    }

	// Méthodes utilitaires pour obtenir la largeur et la profondeur
	public float getWidth() {
		return maxX - minX;
	}

	public float getDepth() {
		return maxZ - minZ;
	}

	/**
	 * Renvoie true si la détection de collision est probablement meilleure en utilisant un cylindre (bounding sphere) plutôt qu'une AABB.
	 */
	public boolean useCylinderCollision() {
		return cylindricalCollision;
	}
	
	public String toString() {
        return "PolygonGroupBounds (topHeight=" + topHeight + ", bottomHeight=" + bottomHeight + ", radius=" + radius + ")";
    }
	
}
