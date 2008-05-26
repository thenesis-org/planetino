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
package org.thenesis.planetino2.math3D;


/**
 * This class represents a rectangle and all the interesting things you
 * might want to do with it.  Note that the coordinate system uses
 * the origin (0,0) as the top left of the screen, with the x and y
 * values increasing as they move to the right and down respectively.
 *
 * <p>It is valid for a rectangle to have negative width or height; but it
 * is considered to have no area or internal points. Therefore, the behavior
 * in methods like <code>contains</code> or <code>intersects</code> is
 * undefined unless the rectangle has positive width and height.
 *
 * <p>There are some public fields; if you mess with them in an inconsistent
 * manner, it is your own fault when you get NullPointerException,
 * ArrayIndexOutOfBoundsException, or invalid results. Also, this class is
 * not threadsafe.
 *
 * @author Warren Levy  (warrenl@cygnus.com)
 * @author Aaron M. Renn (arenn@urbanophile.com)
 * @author Eric Blake (ebb9@email.byu.edu)
 * @since 1.0
 * @status updated to 1.4
 */
public class Rectangle 
{

  /**
   * The X coordinate of the top-left corner of the rectangle.
   *
   * @see #setLocation(int, int)
   * @see #getLocation()
   * @serial the x coordinate
   */
  public int x;

  /**
   * The Y coordinate of the top-left corner of the rectangle.
   *
   * @see #setLocation(int, int)
   * @see #getLocation()
   * @serial the y coordinate
   */
  public int y;

  /**
   * The width of the rectangle.
   *
   * @see #setSize(int, int)
   * @see #getSize()
   * @serial
   */
  public int width;

  /**
   * The height of the rectangle.
   *
   * @see #setSize(int, int)
   * @see #getSize()
   * @serial
   */
  public int height;

  /**
   * Initializes a new instance of <code>Rectangle</code> with a top
   * left corner at (0,0) and a width and height of 0.
   */
  public Rectangle()
  {
  }

  /**
   * Initializes a new instance of <code>Rectangle</code> from the
   * coordinates of the specified rectangle.
   *
   * @param r the rectangle to copy from
   * @since 1.1
   */
  public Rectangle(Rectangle r)
  {
    x = r.x;
    y = r.y;
    width = r.width;
    height = r.height;
  }

  /**
   * Initializes a new instance of <code>Rectangle</code> from the specified
   * inputs.
   *
   * @param x the X coordinate of the top left corner
   * @param y the Y coordinate of the top left corner
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   */
  public Rectangle(int x, int y, int width, int height)
  {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  /**
   * Initializes a new instance of <code>Rectangle</code> with the specified
   * width and height. The upper left corner of the rectangle will be at
   * the origin (0,0).
   *
   * @param width the width of the rectangle
   * @param height the height of the rectange
   */
  public Rectangle(int width, int height)
  {
    this.width = width;
    this.height = height;
  }

  /**
   * Get the X coordinate of the upper-left corner.
   *
   * @return the value of x, as a double
   */
  public double getX()
  {
    return x;
  }

  /**
   * Get the Y coordinate of the upper-left corner.
   *
   * @return the value of y, as a double
   */
  public double getY()
  {
    return y;
  }

  /**
   * Get the width of the rectangle.
   *
   * @return the value of width, as a double
   */
  public double getWidth()
  {
    return width;
  }

  /**
   * Get the height of the rectangle.
   *
   * @return the value of height, as a double
   */
  public double getHeight()
  {
    return height;
  }

  /**
   * Returns the bounds of this rectangle. A pretty useless method, as this
   * is already a rectangle; it is included to mimic the
   * <code>getBounds</code> method in Component.
   *
   * @return a copy of this rectangle
   * @see #setBounds(Rectangle)
   * @since 1.1
   */
  public Rectangle getBounds()
  {
    return new Rectangle(this);
  }
  

  /**
   * Updates this rectangle to have the specified dimensions, rounded to the
   * integer precision used by this class (the values are rounded "outwards" so
   * that the stored rectangle completely encloses the specified double 
   * precision rectangle).
   *
   * @param x the new X coordinate of the upper left hand corner
   * @param y the new Y coordinate of the upper left hand corner
   * @param width the new width of this rectangle
   * @param height the new height of this rectangle
   * @since 1.2
   */
  public void setRect(double x, double y, double width, double height)
  {
    this.x = (int) Math.floor(x);
    this.y = (int) Math.floor(y);
    this.width = (int) Math.ceil(x + width) - this.x;
    this.height = (int) Math.ceil(y + height) - this.y;
  }
  /**
   * Translate the location of this rectangle by the given amounts.
   *
   * @param dx the x distance to move by
   * @param dy the y distance to move by
   * @see #setLocation(int, int)
   */
  public void translate(int dx, int dy)
  {
    x += dx;
    y += dy;
  }


  /**
   * Tests whether or not the specified point is inside this rectangle.
   * According to the contract of Shape, a point on the border is in only if
   * it has an adjacent point inside the rectangle in either the increasing
   * x or y direction.
   *
   * @param x the X coordinate of the point to test
   * @param y the Y coordinate of the point to test
   * @return true if the point is inside the rectangle
   * @since 1.1
   */
  public boolean contains(int x, int y)
  {
    return inside (x, y);
  }


  /**
   * Checks whether all points in the given rectangle are contained in this
   * rectangle.
   *
   * @param x the x coordinate of the rectangle to check
   * @param y the y coordinate of the rectangle to check
   * @param w the width of the rectangle to check
   * @param h the height of the rectangle to check
   * @return true if the parameters are contained in this rectangle
   * @since 1.1
   */
  public boolean contains(int x, int y, int w, int h)
  {
    return width > 0 && height > 0 && w > 0 && h > 0
      && x >= this.x && x + w <= this.x + this.width
      && y >= this.y && y + h <= this.y + this.height;
  }

  /**
   * Tests whether or not the specified point is inside this rectangle.
   *
   * @param x the X coordinate of the point to test
   * @param y the Y coordinate of the point to test
   * @return true if the point is inside the rectangle
   * @deprecated use {@link #contains(int, int)} instead
   */
  public boolean inside(int x, int y)
  {
    return width > 0 && height > 0
      && x >= this.x && x < this.x + width
      && y >= this.y && y < this.y + height;
  }

  /**
   * Tests whether or not the specified rectangle intersects this rectangle.
   * This means the two rectangles share at least one internal point.
   *
   * @param r the rectangle to test against
   * @return true if the specified rectangle intersects this one
   * @throws NullPointerException if r is null
   * @since 1.2
   */
  public boolean intersects(Rectangle r)
  {
    return r.width > 0 && r.height > 0 && width > 0 && height > 0
      && r.x < x + width && r.x + r.width > x
      && r.y < y + height && r.y + r.height > y;
  }

  

  
 
  /**
   * Expands the rectangle by the specified amount.  The horizontal
   * and vertical expansion values are applied both to the X,Y coordinate
   * of this rectangle, and its width and height.  Thus the width and
   * height will increase by 2h and 2v accordingly.
   *
   * @param h the horizontal expansion value
   * @param v the vertical expansion value
   */
  public void grow(int h, int v)
  {
    x -= h;
    y -= v;
    width += h + h;
    height += v + v;
  }

  /**
   * Tests whether or not this rectangle is empty.  An empty rectangle
   * has a non-positive width or height.
   *
   * @return true if the rectangle is empty
   */
  public boolean isEmpty()
  {
    return width <= 0 || height <= 0;
  }


  /**
   * Tests this rectangle for equality against the specified object.  This
   * will be true if an only if the specified object is an instance of
   * Rectangle2D with the same coordinates and dimensions.
   *
   * @param obj the object to test against for equality
   * @return true if the specified object is equal to this one
   */
  public boolean equals(Object obj)
  {
    // NOTE: No special hashCode() method is required for this class,
    // as this equals() implementation is functionally equivalent to
    // super.equals(), which does define a proper hashCode().

    if (! (obj instanceof Rectangle))
      return false;
    Rectangle r = (Rectangle) obj;
    return r.getX() == x && r.getY() == y
      && r.getWidth() == width && r.getHeight() == height;
  }

  /**
   * Returns a string representation of this rectangle. This is in the form
   * <code>getClass().getName() + "[x=" + x + ",y=" + y + ",width=" + width
   * + ",height=" + height + ']'</code>.
   *
   * @return a string representation of this rectangle
   */
  public String toString()
  {
    return getClass().getName() + "[x=" + x + ",y=" + y + ",width=" + width
      + ",height=" + height + ']';
  }
} // class Rectangle
