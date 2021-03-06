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

import org.thenesis.planetino2.util.MoreMath;

/**
 Tests trig table speed vs. trig function speed and prints
 a neat pattern.
 Note: this isn't an accurate benchmark since there are several
 factors involved (like printing output).
 */
public class TrigTableTest {

	public static final int COUNT = 8000000;

	public static void main(String[] args) {

		long funcTime = timeFunctionTest();
		long tableTime = timeTableTest();

		System.out.println("Function time: " + funcTime);
		System.out.println("Table time: " + tableTime);
	}

	public static long timeFunctionTest() {
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < COUNT; i++) {
			functionTest(i);
		}
		return System.currentTimeMillis() - startTime;
	}

	public static long timeTableTest() {
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < COUNT; i++) {
			tableTest(i);
		}
		return System.currentTimeMillis() - startTime;
	}

	public static void functionTest(int i) {
		float angle = i * (float) Math.PI * 2 / COUNT;
		double cosAngle = Math.cos(angle);
		if ((i & 65535) == 0) {
			printMessage((float) cosAngle, "Cosine Function");
		}
	}

	public static void tableTest(int i) {
		float angle = i * (float) Math.PI * 2 / COUNT;
		float cosAngle = MoreMath.cos(MoreMath.angleConvert(angle));
		if ((i & 65535) == 0) {
			printMessage(cosAngle, "Cosine Table");
		}
	}

	public static void printMessage(float cosAngle, String msg) {
		int x = (int) (30 * (1 + cosAngle));
		for (int j = 0; j < x; j++) {
			System.out.print(" ");
		}
		System.out.println(msg);
	}

}