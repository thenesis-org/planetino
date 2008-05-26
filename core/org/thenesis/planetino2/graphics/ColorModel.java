/*
 * Planetino - Copyright (C) 2007-2008 Guillaume Legris, Mathieu Legris
 * 
 * GNU Classpath - Copyright (C) 1999, 2000, 2002 Free Software Foundation
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

/**************************************************************************
* Copyright (c) 2001, 2002, 2003 by Acunia N.V. All rights reserved.      *
*                                                                         *
* This software is copyrighted by and is the sole property of Acunia N.V. *
* and its licensors, if any. All rights, title, ownership, or other       *
* interests in the software remain the property of Acunia N.V. and its    *
* licensors, if any.                                                      *
*                                                                         *
* This software may only be used in accordance with the corresponding     *
* license agreement. Any unauthorized use, duplication, transmission,     *
*  distribution or disclosure of this software is expressly forbidden.    *
*                                                                         *
* This Copyright notice may not be removed or modified without prior      *
* written consent of Acunia N.V.                                          *
*                                                                         *
* Acunia N.V. reserves the right to modify this software without notice.  *
*                                                                         *
*   Acunia N.V.                                                           *
*   Philips-site 5, box 3       info@acunia.com                           *
*   3001 Leuven                 http://www.acunia.com                     *
*   Belgium - EUROPE                                                      *
**************************************************************************/

package org.thenesis.planetino2.graphics;

public abstract class ColorModel {

  protected int pixel_bits;
  private static ColorModel RGBdefault;

  public ColorModel(int b) {
    pixel_bits = b;
  }
  
  public abstract int getAlpha(int pixelValue);
  public abstract int getRed(int pixelValue);
  public abstract int getGreen(int pixelValue);
  public abstract int getBlue(int pixelValue);

  public int getPixelSize() {
    return pixel_bits;
  }

  public int getRGB(int pixelValue) {
    return (getAlpha(pixelValue) << 24) | (getRed(pixelValue) << 16) | (getGreen(pixelValue) << 8) | getBlue(pixelValue);
  }
  
  public static ColorModel getRGBdefault() {
    if(RGBdefault == null) RGBdefault = new DirectColorModel(32, 0x00ff0000, 0x0000ff00, 0x000000ff, 0xff000000);
    return RGBdefault;
  }

//  public void finalize() {
//  }
  
}

