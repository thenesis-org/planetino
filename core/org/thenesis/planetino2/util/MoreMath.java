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
package org.thenesis.planetino2.util;

import java.util.Random;
import java.util.Vector;

/**
 The MoreMath class provides functions not contained in the
 java.lang.Math or java.lang.StrictMath classes.
 */
public class MoreMath {

	// a trig table with 4096 entries
	private static final int TABLE_SIZE_BITS = 12;
	private static final int TABLE_SIZE = 1 << TABLE_SIZE_BITS;
	private static final int TABLE_SIZE_MASK = TABLE_SIZE - 1;
	private static final int HALF_PI = TABLE_SIZE / 4;
	private static final float CONVERSION_FACTOR = (float) (TABLE_SIZE / (2 * Math.PI));

	private static float[] sinTable;
	private static Random random = new Random();

	// init trig table when this class is loaded
	static {
		init();
	}

	private static void init() {
		sinTable = new float[TABLE_SIZE];
		for (int i = 0; i < TABLE_SIZE; i++) {
			sinTable[i] = (float) Math.sin(i / CONVERSION_FACTOR);
		}
	}

	/**
	 Cosine function, where angle is from 0 to 4096 instead of
	 0 to 2pi.
	 */
	public static float cos(int angle) {
		return sinTable[(HALF_PI - angle) & TABLE_SIZE_MASK];
	}

	/**
	 Sine function, where angle is from 0 to 4096 instead of
	 0 to 2pi.
	 */
	public static float sin(int angle) {
		return sinTable[angle & TABLE_SIZE_MASK];
	}

	/**
	 Converts an angle in radians to the system used by this
	 class (0 to 2pi becomes 0 to 4096)
	 */
	public static int angleConvert(float angleInRadians) {
		return (int) (angleInRadians * CONVERSION_FACTOR);
	}

	/**
	 Returns the sign of the number. Returns -1 for negative,
	 1 for positive, and 0 otherwise.
	 */
	public static int sign(short v) {
		return (v > 0) ? 1 : (v < 0) ? -1 : 0;
	}

	/**
	 Returns the sign of the number. Returns -1 for negative,
	 1 for positive, and 0 otherwise.
	 */
	public static int sign(int v) {
		return (v > 0) ? 1 : (v < 0) ? -1 : 0;
	}

	/**
	 Returns the sign of the number. Returns -1 for negative,
	 1 for positive, and 0 otherwise.
	 */
	public static int sign(long v) {
		return (v > 0) ? 1 : (v < 0) ? -1 : 0;
	}

	/**
	 Returns the sign of the number. Returns -1 for negative,
	 1 for positive, and 0 otherwise.
	 */
	public static int sign(float v) {
		return (v > 0) ? 1 : (v < 0) ? -1 : 0;
	}

	/**
	 Returns the sign of the number. Returns -1 for negative,
	 1 for positive, and 0 otherwise.
	 */
	public static int sign(double v) {
		return (v > 0) ? 1 : (v < 0) ? -1 : 0;
	}

	/**
	 Faster ceil function to convert a float to an int.
	 Contrary to the java.lang.Math ceil function, this
	 function takes a float as an argument, returns an int
	 instead of a double, and does not consider special cases.
	 */
	public static int ceil(float f) {
		if (f > 0) {
			return (int) f + 1;
		} else {
			return (int) f;
		}
	}

	/**
	 Faster floor function to convert a float to an int.
	 Contrary to the java.lang.Math floor function, this
	 function takes a float as an argument, returns an int
	 instead of a double, and does not consider special cases.
	 */
	public static int floor(float f) {
		if (f >= 0) {
			return (int) f;
		} else {
			return (int) f - 1;
		}
	}

	public static float random() {
		return random.nextFloat();
	}

	/**
	 Returns a random integer from 0 to max (inclusive).
	 */
	public static int random(int max) {
		return (int) Math.floor(random.nextFloat() * max + 0.5d);
	}

	/**
	 Returns a random integer from min to max (inclusive).
	 */
	public static int random(int min, int max) {
		return min + random(max - min);
	}

	/**
	 Returns a random float from 0 to max (inclusive).
	 */
	public static float random(float max) {
		return (float) random.nextFloat() * max;
	}

	/**
	 Returns a random float from min to max (inclusive).
	 */
	public static float random(float min, float max) {
		return min + random(max - min);
	}

	/**
	 Returns a random object from a List.
	 */
	public static Object random(Vector list) {
		if (list.size() == 0)
			return null;
		return list.elementAt(random(list.size() - 1));
	}

	/**
	 Returns true if a random "event" occurs. The specified
	 value, p, is the probability (0 to 1) that the random
	 "event" occurs.
	 */
	public static boolean chance(float p) {
		return (random.nextFloat() <= p);
	}

	/**
	 Returns true if the specified number is a power of 2.
	 */
	public static boolean isPowerOfTwo(int n) {
		return ((n & (n - 1)) == 0);
	}

	/**
	 Gets the number of "on" bits in an integer.
	 */
	public static int getBitCount(int n) {
		int count = 0;
		while (n > 0) {
			count += (n & 1);
			n >>= 1;
		}
		return count;
	}

	private static final double PI_L = 1.2246467991473532e-16; // Long bits 0x3ca1a62633145c07L.
	private static final double TWO_60 = 0x1000000000000000L; // Long bits 0x43b0000000000000L.

	/**
	 * A special version of the trigonometric function <em>arctan</em>, for
	 * converting rectangular coordinates <em>(x, y)</em> to polar
	 * <em>(r, theta)</em>. This computes the arctangent of x/y in the range
	 * of -pi to pi radians (-180 to 180 degrees). Special cases:<ul>
	 * <li>If either argument is NaN, the result is NaN.</li>
	 * <li>If the first argument is positive zero and the second argument is
	 * positive, or the first argument is positive and finite and the second
	 * argument is positive infinity, then the result is positive zero.</li>
	 * <li>If the first argument is negative zero and the second argument is
	 * positive, or the first argument is negative and finite and the second
	 * argument is positive infinity, then the result is negative zero.</li>
	 * <li>If the first argument is positive zero and the second argument is
	 * negative, or the first argument is positive and finite and the second
	 * argument is negative infinity, then the result is the double value
	 * closest to pi.</li>
	 * <li>If the first argument is negative zero and the second argument is
	 * negative, or the first argument is negative and finite and the second
	 * argument is negative infinity, then the result is the double value
	 * closest to -pi.</li>
	 * <li>If the first argument is positive and the second argument is
	 * positive zero or negative zero, or the first argument is positive
	 * infinity and the second argument is finite, then the result is the
	 * double value closest to pi/2.</li>
	 * <li>If the first argument is negative and the second argument is
	 * positive zero or negative zero, or the first argument is negative
	 * infinity and the second argument is finite, then the result is the
	 * double value closest to -pi/2.</li>
	 * <li>If both arguments are positive infinity, then the result is the
	 * double value closest to pi/4.</li>
	 * <li>If the first argument is positive infinity and the second argument
	 * is negative infinity, then the result is the double value closest to
	 * 3*pi/4.</li>
	 * <li>If the first argument is negative infinity and the second argument
	 * is positive infinity, then the result is the double value closest to
	 * -pi/4.</li>
	 * <li>If both arguments are negative infinity, then the result is the
	 * double value closest to -3*pi/4.</li>
	 *
	 * </ul><p>This returns theta, the angle of the point. To get r, albeit
	 * slightly inaccurately, use sqrt(x*x+y*y).
	 *
	 * @param y the y position
	 * @param x the x position
	 * @return <em>theta</em> in the conversion of (x, y) to (r, theta)
	 * @see #atan(double)
	 */
	public static double atan2(double y, double x) {

		if (x != x || y != y)
			return Double.NaN;
		if (x == 1)
			return atan(y);
		if (x == Double.POSITIVE_INFINITY) {
			if (y == Double.POSITIVE_INFINITY)
				return Math.PI / 4;
			if (y == Double.NEGATIVE_INFINITY)
				return -Math.PI / 4;
			return 0 * y;
		}
		if (x == Double.NEGATIVE_INFINITY) {
			if (y == Double.POSITIVE_INFINITY)
				return 3 * Math.PI / 4;
			if (y == Double.NEGATIVE_INFINITY)
				return -3 * Math.PI / 4;
			return (1 / (0 * y) == Double.POSITIVE_INFINITY) ? Math.PI : -Math.PI;
		}
		if (y == 0) {
			if (1 / (0 * x) == Double.POSITIVE_INFINITY)
				return y;
			return (1 / y == Double.POSITIVE_INFINITY) ? Math.PI : -Math.PI;
		}
		if (y == Double.POSITIVE_INFINITY || y == Double.NEGATIVE_INFINITY || x == 0)
			return y < 0 ? -Math.PI / 2 : Math.PI / 2;

		double z = Math.abs(y / x); // Safe to do y/x.
		if (z > TWO_60)
			z = Math.PI / 2 + 0.5 * PI_L;
		else if (x < 0 && z < 1 / TWO_60)
			z = 0;
		else
			z = atan(z);
		if (x > 0)
			return y > 0 ? z : -z;
		return y > 0 ? Math.PI - (z - PI_L) : z - PI_L - Math.PI;
	}

	/**
	 * Coefficients for computing {@link #atan(double)}.
	 */
	private static final double ATAN_0_5H = 0.4636476090008061, // Long bits 0x3fddac670561bb4fL.
			ATAN_0_5L = 2.2698777452961687e-17, // Long bits 0x3c7a2b7f222f65e2L.
			ATAN_1_5H = 0.982793723247329, // Long bits 0x3fef730bd281f69bL.
			ATAN_1_5L = 1.3903311031230998e-17, // Long bits 0x3c7007887af0cbbdL.
			AT0 = 0.3333333333333293, // Long bits 0x3fd555555555550dL.
			AT1 = -0.19999999999876483, // Long bits 0xbfc999999998ebc4L.
			AT2 = 0.14285714272503466, // Long bits 0x3fc24924920083ffL.
			AT3 = -0.11111110405462356, // Long bits 0xbfbc71c6fe231671L.
			AT4 = 0.09090887133436507, // Long bits 0x3fb745cdc54c206eL.
			AT5 = -0.0769187620504483, // Long bits 0xbfb3b0f2af749a6dL.
			AT6 = 0.06661073137387531, // Long bits 0x3fb10d66a0d03d51L.
			AT7 = -0.058335701337905735, // Long bits 0xbfadde2d52defd9aL.
			AT8 = 0.049768779946159324, // Long bits 0x3fa97b4b24760debL.
			AT9 = -0.036531572744216916, // Long bits 0xbfa2b4442c6a6c2fL.
			AT10 = 0.016285820115365782, // Long bits 0x3f90ad3ae322da11L.
			TWO_29 = 0x20000000, // Long bits 0x41c0000000000000L.
			TWO_66 = 7.378697629483821e19; // Long bits 0x4410000000000000L.

	/**
	 * The trigonometric function <em>arcsin</em>. The range of angles returned
	 * is -pi/2 to pi/2 radians (-90 to 90 degrees). If the argument is NaN, the
	 * result is NaN; and the arctangent of 0 retains its sign.
	 *
	 * @param x the tan to turn back into an angle
	 * @return arcsin(x)
	 * @see #atan2(double, double)
	 */
	public static double atan(double x) {
		double lo;
		double hi;
		boolean negative = x < 0;
		if (negative)
			x = -x;
		if (x >= TWO_66)
			return negative ? -Math.PI / 2 : Math.PI / 2;
		if (!(x >= 0.4375)) // |x|<7/16, or NaN.
		{
			if (!(x >= 1 / TWO_29)) // Small, or NaN.
				return negative ? -x : x;
			lo = hi = 0;
		} else if (x < 1.1875) {
			if (x < 0.6875) // 7/16<=|x|<11/16.
			{
				x = (2 * x - 1) / (2 + x);
				hi = ATAN_0_5H;
				lo = ATAN_0_5L;
			} else // 11/16<=|x|<19/16.
			{
				x = (x - 1) / (x + 1);
				hi = Math.PI / 4;
				lo = PI_L / 4;
			}
		} else if (x < 2.4375) // 19/16<=|x|<39/16.
		{
			x = (x - 1.5) / (1 + 1.5 * x);
			hi = ATAN_1_5H;
			lo = ATAN_1_5L;
		} else // 39/16<=|x|<2**66.
		{
			x = -1 / x;
			hi = Math.PI / 2;
			lo = PI_L / 2;
		}

		// Break sum from i=0 to 10 ATi*z**(i+1) into odd and even poly.
		double z = x * x;
		double w = z * z;
		double s1 = z * (AT0 + w * (AT2 + w * (AT4 + w * (AT6 + w * (AT8 + w * AT10)))));
		double s2 = w * (AT1 + w * (AT3 + w * (AT5 + w * (AT7 + w * AT9))));
		if (hi == 0)
			return negative ? x * (s1 + s2) - x : x - x * (s1 + s2);
		z = hi - ((x * (s1 + s2) - lo) - x);
		return negative ? -z : z;
	}
}
