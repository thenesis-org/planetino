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
package org.thenesis.planetino2.bsp2D;

/**
 * This stores a dimension in 2-dimensional space - a width (along the x-axis)
 * and height (along the y-axis). The storage is left to subclasses.
 *
 * @author Per Bothner (bothner@cygnus.com)
 * @author Eric Blake (ebb9@email.byu.edu)
 * @since 1.2
 * @status updated to 1.4
 */
public abstract class Dimension2D //implements Cloneable
{
  /**
   * The default constructor.
   */
  protected Dimension2D()
  {
  }

  /**
   * Get the width of this dimension. A negative result, while legal, is
   * undefined in meaning.
   *
   * @return the width
   */
  public abstract double getWidth();

  /**
   * Get the height of this dimension. A negative result, while legal, is
   * undefined in meaning.
   *
   * @return the height
   */
  public abstract double getHeight();

  /**
   * Set the size of this dimension to the requested values. Loss of precision
   * may occur.
   *
   * @param w the new width
   * @param h the new height
   */
  public abstract void setSize(double w, double h);

  /**
   * Set the size of this dimension to the requested value. Loss of precision
   * may occur.
   *
   * @param d the dimension containing the new values
   *
   * @throws NullPointerException if d is null
   */
  public void setSize(Dimension2D d)
  {
    setSize(d.getWidth(), d.getHeight());
  }

  /**
   * Create a new dimension of the same run-time type with the same contents
   * as this one.
   *
   * @return the clone
   *
   * @exception OutOfMemoryError If there is not enough memory available.
   *
   * @since 1.2
   */
//  public Object clone()
//  {
//    try
//      {
//        return super.clone();
//      }
//    catch (CloneNotSupportedException e)
//      {
//        throw (Error) new InternalError().initCause(e); // Impossible
//      }
//  }
} // class Dimension2D
