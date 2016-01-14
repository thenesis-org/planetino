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

/*
 * Copyright (c) 2003 Sun Microsystems, Inc. All  Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * -Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduct the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT
 * BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT
 * OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN
 * IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that Software is not designed, licensed or intended for
 * use in the design, construction, operation or maintenance of any nuclear
 * facility.
 */
package org.thenesis.planetino2.util;

import org.thenesis.planetino2.util.Vector;

/**
 * A quick sort demonstration algorithm
 * SortAlgorithm.java
 *
 * @author James Gosling
 * @author Kevin A. Smith
 * @author Guillaume Legris, Mathieu Legris
 */
public class QSortAlgorithm { 

	/** This is a generic version of C.A.R Hoare's Quick Sort
	 * algorithm.  This will handle arrays that are already
	 * sorted, and arrays with duplicate keys.<BR>
	 *
	 * If you think of a one dimensional array as going from
	 * the lowest index on the left to the highest index on the right
	 * then the parameters to this function are lowest index or
	 * left and highest index or right.  The first time you call
	 * this function it will be with the parameters 0, a.length - 1.
	 *
	 * @param a       an integer array
	 * @param lo0     left boundary of array partition
	 * @param hi0     right boundary of array partition
	 */
	static void QuickSort(Vector v, int lo0, int hi0)  {
		int lo = lo0;
		int hi = hi0;
		Comparable mid;

		if (hi0 > lo0) {

			/* Arbitrarily establishing partition element as the midpoint of
			 * the array.
			 */
			mid = (Comparable) v.elementAt((lo0 + hi0) / 2);

			// loop through the array until indices cross
			while (lo <= hi) {
				/* find the first element that is greater than or equal to
				 * the partition element starting from the left Index.
				 */
				//Comparable clo = (Comparable) v.elementAt(lo);
				while ((lo < hi0) && (((Comparable)v.elementAt(lo)).compareTo(mid) < 0)) { //(a[lo] < mid)) {
					++lo;
				}

				/* find an element that is smaller than or equal to
				 * the partition element starting from the right Index.
				 */
				//Comparable chi = (Comparable) v.elementAt(hi);
				while ((hi > lo0) && (((Comparable)v.elementAt(hi)).compareTo(mid) > 0)) { //(a[hi] > mid))
					--hi;
				}

				// if the indexes have not crossed, swap
				if (lo <= hi) {
					swap(v, lo, hi);
					++lo;
					--hi;
				}
			}

			/* If the right index has not reached the left side of array
			 * must now sort the left partition.
			 */
			if (lo0 < hi)
				QuickSort(v, lo0, hi);

			/* If the left index has not reached the right side of array
			 * must now sort the right partition.
			 */
			if (lo < hi0)
				QuickSort(v, lo, hi0);

		}
	}

	private static void swap(Vector v, int i, int j) {

		Object tmp = v.elementAt(i);
		v.setElementAt(v.elementAt(j), i);
		v.setElementAt(tmp, j);

	}

	public static void sort(Vector v)  {
		QuickSort(v, 0, v.size() - 1);
	}

	//   /** This is a generic version of C.A.R Hoare's Quick Sort
	//    * algorithm.  This will handle arrays that are already
	//    * sorted, and arrays with duplicate keys.<BR>
	//    *
	//    * If you think of a one dimensional array as going from
	//    * the lowest index on the left to the highest index on the right
	//    * then the parameters to this function are lowest index or
	//    * left and highest index or right.  The first time you call
	//    * this function it will be with the parameters 0, a.length - 1.
	//    *
	//    * @param a       an integer array
	//    * @param lo0     left boundary of array partition
	//    * @param hi0     right boundary of array partition
	//    */
	//   void QuickSort(int a[], int lo0, int hi0) throws Exception
	//   {
	//      int lo = lo0;
	//      int hi = hi0;
	//      int mid;
	//
	//      if ( hi0 > lo0)
	//      {
	//
	//         /* Arbitrarily establishing partition element as the midpoint of
	//          * the array.
	//          */
	//         mid = a[ ( lo0 + hi0 ) / 2 ];
	//
	//         // loop through the array until indices cross
	//         while( lo <= hi )
	//         {
	//            /* find the first element that is greater than or equal to
	//             * the partition element starting from the left Index.
	//             */
	//	     while( ( lo < hi0 ) && ( a[lo] < mid ))
	//		 ++lo;
	//
	//            /* find an element that is smaller than or equal to
	//             * the partition element starting from the right Index.
	//             */
	//	     while( ( hi > lo0 ) && ( a[hi] > mid ))
	//		 --hi;
	//
	//            // if the indexes have not crossed, swap
	//            if( lo <= hi )
	//            {
	//               swap(a, lo, hi);
	//               ++lo;
	//               --hi;
	//            }
	//         }
	//
	//         /* If the right index has not reached the left side of array
	//          * must now sort the left partition.
	//          */
	//         if( lo0 < hi )
	//            QuickSort( a, lo0, hi );
	//
	//         /* If the left index has not reached the right side of array
	//          * must now sort the right partition.
	//          */
	//         if( lo < hi0 )
	//            QuickSort( a, lo, hi0 );
	//
	//      }
	//   }
	//
	//   private void swap(int a[], int i, int j)
	//   {
	//      int T;
	//      T = a[i];
	//      a[i] = a[j];
	//      a[j] = T;
	//
	//   }
	//
	//   public void sort(int a[]) throws Exception
	//   {
	//      QuickSort(a, 0, a.length - 1);
	//   }
}
