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

import java.util.Vector;

/**
 The PolygonGroup is a group of polygons with a
 MovingTransform3D. PolygonGroups can also contain other
 PolygonGroups.
 */
public class PolygonGroup implements Transformable {

	private String name;
	private String filename;
	private Vector objects;
	private MovingTransform3D transform;
	private int iteratorIndex;

	/**
	 Creates a new, empty PolygonGroup.
	 */
	public PolygonGroup() {
		this("unnamed");
	}

	/**
	 Creates a new, empty PolygonGroup with te specified name.
	 */
	public PolygonGroup(String name) {
		setName(name);
		objects = new Vector();
		transform = new MovingTransform3D();
		iteratorIndex = 0;
	}

	/**
	 Gets the MovingTransform3D for this PolygonGroup.
	 */
	public MovingTransform3D getTransform() {
		return transform;
	}

	/**
	 Gets the name of this PolygonGroup.
	 */
	public String getName() {
		return name;
	}

	/**
	 Sets the name of this PolygonGroup.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 Gets the filename of this PolygonGroup.
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 Sets the filename of this PolygonGroup.
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 Adds a polygon to this group.
	 */
	public void addPolygon(Polygon3D o) {
		objects.addElement(o);
	}

	/**
	 Adds a PolygonGroup to this group.
	 */
	public void addPolygonGroup(PolygonGroup p) {
		objects.addElement(p);
	}

	/**
	 Clones this polygon group. Polygon3Ds are shared between
	 this group and the cloned group; Transform3Ds are copied.
	 */
	public Object clone() {
		PolygonGroup group = new PolygonGroup(name);
		group.setFilename(filename);
		for (int i = 0; i < objects.size(); i++) {
			Object obj = objects.elementAt(i);
			if (obj instanceof Polygon3D) {
				group.addPolygon((Polygon3D) obj);
			} else {
				PolygonGroup grp = (PolygonGroup) obj;
				group.addPolygonGroup((PolygonGroup) grp.clone());
			}
		}
		group.transform = (MovingTransform3D) transform.clone();
		return group;
	}

	/**
	 Gets the PolygonGroup in this group with the specified
	 name, or null if none found.
	 */
	public PolygonGroup getGroup(String name) {
		// check for this group
		if (this.name != null && this.name.equals(name)) {
			return this;
		}
		for (int i = 0; i < objects.size(); i++) {
			Object obj = objects.elementAt(i);
			if (obj instanceof PolygonGroup) {
				PolygonGroup subgroup = ((PolygonGroup) obj).getGroup(name);
				if (subgroup != null) {
					return subgroup;
				}
			}
		}

		// group not found
		return null;
	}

	/**
	 Resets the polygon iterator for this group.
	 @see #hasNext
	 @see #nextPolygon
	 */
	public void resetIterator() {
		iteratorIndex = 0;
		for (int i = 0; i < objects.size(); i++) {
			Object obj = objects.elementAt(i);
			if (obj instanceof PolygonGroup) {
				((PolygonGroup) obj).resetIterator();
			}
		}
	}

	/**
	 Checks if there is another polygon in the current
	 iteration.
	 @see #resetIterator
	 @see #nextPolygon
	 */
	public boolean hasNext() {
		return (iteratorIndex < objects.size());
	}

	/**
	 Gets the next polygon in the current iteration.
	 @see #resetIterator
	 @see #hasNext
	 */
	public Polygon3D nextPolygon() {
		Object obj = objects.elementAt(iteratorIndex);

		if (obj instanceof PolygonGroup) {
			PolygonGroup group = (PolygonGroup) obj;
			Polygon3D poly = group.nextPolygon();
			if (!group.hasNext()) {
				iteratorIndex++;
			}
			return poly;
		} else {
			iteratorIndex++;
			return (Polygon3D) obj;
		}
	}

	/**
	 Gets the next polygon in the current iteration, applying
	 the MovingTransform3Ds to it, and storing it in 'cache'.
	 */
	public void nextPolygonTransformed(Polygon3D cache) {
		Object obj = objects.elementAt(iteratorIndex);

		if (obj instanceof PolygonGroup) {
			PolygonGroup group = (PolygonGroup) obj;
			group.nextPolygonTransformed(cache);
			if (!group.hasNext()) {
				iteratorIndex++;
			}
		} else {
			iteratorIndex++;
			cache.setTo((Polygon3D) obj);
		}

		cache.add(transform);
	}

	/**
	 Updates the MovingTransform3Ds of this group and any
	 subgroups.
	 */
	public void update(long elapsedTime) {
		transform.update(elapsedTime);
		for (int i = 0; i < objects.size(); i++) {
			Object obj = objects.elementAt(i);
			if (obj instanceof PolygonGroup) {
				PolygonGroup group = (PolygonGroup) obj;
				group.update(elapsedTime);
			}
		}
	}

	// from the Transformable interface

	public void add(Vector3D u) {
		transform.getLocation().add(u);
	}

	public void subtract(Vector3D u) {
		transform.getLocation().subtract(u);
	}

	public void add(Transform3D xform) {
		addRotation(xform);
		add(xform.getLocation());
	}

	public void subtract(Transform3D xform) {
		subtract(xform.getLocation());
		subtractRotation(xform);
	}

	public void addRotation(Transform3D xform) {
		transform.rotateAngleX(xform.getAngleX());
		transform.rotateAngleY(xform.getAngleY());
		transform.rotateAngleZ(xform.getAngleZ());
	}

	public void subtractRotation(Transform3D xform) {
		transform.rotateAngleX(-xform.getAngleX());
		transform.rotateAngleY(-xform.getAngleY());
		transform.rotateAngleZ(-xform.getAngleZ());
	}

	@Override
	public String toString() {
		return "Object " + name + " (" + filename + ")";
	}
	
	

}
