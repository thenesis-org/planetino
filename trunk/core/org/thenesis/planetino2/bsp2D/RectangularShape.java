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

import org.thenesis.planetino2.math3D.Rectangle;

/**
 * This class provides a generic framework, and several helper methods, for
 * subclasses which represent geometric objects inside a rectangular frame.
 * This does not specify any geometry except for the bounding box.
 *
 * @author Tom Tromey (tromey@cygnus.com)
 * @author Eric Blake (ebb9@email.byu.edu)
 * @since 1.2
 * @see Arc2D
 * @see Ellipse2D
 * @see Rectangle2D
 * @see RoundRectangle2D
 * @status updated to 1.4
 */
public abstract class RectangularShape implements Shape
{
  /**
   * Default constructor.
   */
  protected RectangularShape()
  {
  }

  /**
   * Get the x coordinate of the upper-left corner of the framing rectangle.
   *
   * @return the x coordinate
   */
  public abstract double getX();

  /**
   * Get the y coordinate of the upper-left corner of the framing rectangle.
   *
   * @return the y coordinate
   */
  public abstract double getY();

  /**
   * Get the width of the framing rectangle.
   *
   * @return the width
   */
  public abstract double getWidth();

  /**
   * Get the height of the framing rectangle.
   *
   * @return the height
   */
  public abstract double getHeight();

  /**
   * Get the minimum x coordinate in the frame. This is misnamed, or else
   * Sun has a bug, because the implementation returns getX() even when
   * getWidth() is negative.
   *
   * @return the minimum x coordinate
   */
  public double getMinX()
  {
    return getX();
  }

  /**
   * Get the minimum y coordinate in the frame. This is misnamed, or else
   * Sun has a bug, because the implementation returns getY() even when
   * getHeight() is negative.
   *
   * @return the minimum y coordinate
   */
  public double getMinY()
  {
    return getY();
  }

  /**
   * Get the maximum x coordinate in the frame. This is misnamed, or else
   * Sun has a bug, because the implementation returns getX()+getWidth() even
   * when getWidth() is negative.
   *
   * @return the maximum x coordinate
   */
  public double getMaxX()
  {
    return getX() + getWidth();
  }

  /**
   * Get the maximum y coordinate in the frame. This is misnamed, or else
   * Sun has a bug, because the implementation returns getY()+getHeight() even
   * when getHeight() is negative.
   *
   * @return the maximum y coordinate
   */
  public double getMaxY()
  {
    return getY() + getHeight();
  }

  /**
   * Return the x coordinate of the center point of the framing rectangle.
   *
   * @return the central x coordinate
   */
  public double getCenterX()
  {
    return getX() + getWidth() / 2;
  }

  /**
   * Return the y coordinate of the center point of the framing rectangle.
   *
   * @return the central y coordinate
   */
  public double getCenterY()
  {
    return getY() + getHeight() / 2;
  }

  /**
   * Return the frame around this object. Note that this may be a looser
   * bounding box than getBounds2D.
   *
   * @return the frame, in double precision
   * @see #setFrame(double, double, double, double)
   */
  public Rectangle2D getFrame()
  {
    return new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
  }

  /**
   * Test if the shape is empty, meaning that no points are inside it.
   *
   * @return true if the shape is empty
   */
  public abstract boolean isEmpty();

  /**
   * Set the framing rectangle of this shape to the given coordinate and size.
   *
   * @param x the new x coordinate
   * @param y the new y coordinate
   * @param w the new width
   * @param h the new height
   * @see #getFrame()
   */
  public abstract void setFrame(double x, double y, double w, double h);

  /**
   * Set the framing rectangle of this shape to the given coordinate and size.
   *
   * @param p the new point
   * @param d the new dimension
   * @throws NullPointerException if p or d is null
   * @see #getFrame()
   */
  public void setFrame(Point2D p, Dimension2D d)
  {
    setFrame(p.getX(), p.getY(), d.getWidth(), d.getHeight());
  }

  /**
   * Set the framing rectangle of this shape to the given rectangle.
   *
   * @param r the new framing rectangle
   * @throws NullPointerException if r is null
   * @see #getFrame()
   */
  public void setFrame(Rectangle2D r)
  {
    setFrame(r.getX(), r.getY(), r.getWidth(), r.getHeight());
  }

  /**
   * Set the framing rectangle of this shape using two points on a diagonal.
   * The area will be positive.
   *
   * @param x1 the first x coordinate
   * @param y1 the first y coordinate
   * @param x2 the second x coordinate
   * @param y2 the second y coordinate
   */
  public void setFrameFromDiagonal(double x1, double y1, double x2, double y2)
  {
    if (x1 > x2)
      {
	double t = x2;
	x2 = x1;
	x1 = t;
      }
    if (y1 > y2)
      {
	double t = y2;
	y2 = y1;
	y1 = t;
      }
    setFrame(x1, y1, x2 - x1, y2 - y1);
  }

  /**
   * Set the framing rectangle of this shape using two points on a diagonal.
   * The area will be positive.
   *
   * @param p1 the first point
   * @param p2 the second point
   * @throws NullPointerException if either point is null
   */
  public void setFrameFromDiagonal(Point2D p1, Point2D p2)
  {
    setFrameFromDiagonal(p1.getX(), p1.getY(), p2.getX(), p2.getY());
  }

  /**
   * Set the framing rectangle of this shape using the center of the frame,
   * and one of the four corners. The area will be positive.
   *
   * @param centerX the x coordinate at the center
   * @param centerY the y coordinate at the center
   * @param cornerX the x coordinate at a corner
   * @param cornerY the y coordinate at a corner
   */
  public void setFrameFromCenter(double centerX, double centerY,
                                 double cornerX, double cornerY)
  {
    double halfw = Math.abs(cornerX - centerX);
    double halfh = Math.abs(cornerY - centerY);
    setFrame(centerX - halfw, centerY - halfh, halfw + halfw, halfh + halfh);
  }

  /**
   * Set the framing rectangle of this shape using the center of the frame,
   * and one of the four corners. The area will be positive.
   *
   * @param center the center point
   * @param corner a corner point
   * @throws NullPointerException if either point is null
   */
  public void setFrameFromCenter(Point2D center, Point2D corner)
  {
    setFrameFromCenter(center.getX(), center.getY(),
                       corner.getX(), corner.getY());
  }

  /**
   * Tests if a point is inside the boundary of the shape.
   *
   * @param p the point to test
   * @return true if the point is inside the shape
   * @throws NullPointerException if p is null
   * @see #contains(double, double)
   */
  public boolean contains(Point2D p)
  {
    return contains(p.getX(), p.getY());
  }

  /**
   * Tests if a rectangle and this shape share common internal points.
   *
   * @param r the rectangle to test
   * @return true if the rectangle intersects this shpae
   * @throws NullPointerException if r is null
   * @see #intersects(double, double, double, double)
   */
  public boolean intersects(Rectangle2D r)
  {
    return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
  }

  /**
   * Tests if the shape completely contains the given rectangle.
   *
   * @param r the rectangle to test
   * @return true if r is contained in this shape
   * @throws NullPointerException if r is null
   * @see #contains(double, double, double, double)
   */
  public boolean contains(Rectangle2D r)
  {
    return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
  }

  /**
   * Returns a bounding box for this shape, in integer format. Notice that you
   * may get a tighter bound with getBounds2D.
   *
   * @return a bounding box
   */
  public Rectangle getBounds()
  {
    double x = getX();
    double y = getY();
    double maxx = Math.ceil(x + getWidth());
    double maxy = Math.ceil(y + getHeight());
    x = Math.floor(x);
    y = Math.floor(y);
    return new Rectangle((int) x, (int) y, (int) (maxx - x), (int) (maxy - y));
  }

  /**
   * Return an iterator along the shape boundary. If the optional transform
   * is provided, the iterator is transformed accordingly. The path is
   * flattened until all segments differ from the curve by at most the value
   * of the flatness parameter, within the limits of the default interpolation
   * recursion limit of 1024 segments between actual points. Each call
   * returns a new object, independent from others in use. The result is
   * threadsafe if and only if the iterator returned by
   * {@link #getPathIterator(AffineTransform)} is as well.
   *
   * @param at an optional transform to apply to the iterator
   * @param flatness the desired flatness
   * @return a new iterator over the boundary
   * @throws IllegalArgumentException if flatness is invalid
   * @since 1.2
   */
//  public PathIterator getPathIterator(AffineTransform at, double flatness)
//  {
//    return new FlatteningPathIterator(getPathIterator(at), flatness);
//  }

  /**
   * Create a new shape of the same run-time type with the same contents as
   * this one.
   *
   * @return the clone
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
} // class RectangularShape
