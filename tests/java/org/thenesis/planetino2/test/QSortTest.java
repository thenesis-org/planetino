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
package org.thenesis.planetino2.test;

import java.util.Enumeration;
import org.thenesis.planetino2.util.Vector;

import org.thenesis.planetino2.util.Comparable;
import org.thenesis.planetino2.util.QSortAlgorithm;

public class QSortTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ComparableObject o1 = new ComparableObject(56);
		ComparableObject o2 = new ComparableObject(12);
		ComparableObject o3 = new ComparableObject(1678);
		ComparableObject o4 = new ComparableObject(-9);

		Vector v = new Vector();
		v.addElement(o1);
		v.addElement(o2);
		v.addElement(o3);
		v.addElement(o4);

		QSortAlgorithm.sort(v);

		Enumeration e = v.elements();
		while (e.hasMoreElements()) {
			System.out.println(e.nextElement());
		}

	}

}

class ComparableObject implements Comparable {

	private int x;

	public ComparableObject(int x) {
		this.x = x;
	}

	public int compareTo(Object o) {
		return x - ((ComparableObject) o).x;
	}

	public String toString() {
		return String.valueOf(x);
	}

}
