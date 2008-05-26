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
 * This interface represents an abstract shape. The shape is described by
 * a {@link PathIterator}, and has callbacks for determining bounding box,
 * where points and rectangles lie in relation to the shape, and tracing
 * the trajectory.
 *
 * <p>A point is inside if it is completely inside, or on the boundary and
 * adjacent points in the increasing x or y direction are completely inside.
 * Unclosed shapes are considered as implicitly closed when performing
 * <code>contains</code> or <code>intersects</code>.
 *
 * @author Aaron M. Renn (arenn@urbanophile.com)
 * @see PathIterator
 * @see AffineTransform
 * @see java.awt.geom.FlatteningPathIterator
 * @see java.awt.geom.GeneralPath
 * @since 1.0
 * @status updated to 1.4
 */
public interface Shape
{
  /**
   * Returns a <code>Rectange</code> that bounds the shape. There is no
   * guarantee that this is the minimum bounding box, particularly if
   * the shape overflows the finite integer range of a bound. Generally,
   * <code>getBounds2D</code> returns a tighter bound.
   *
   * @return the shape's bounding box
   * @see #getBounds2D()
   */
  Rectangle getBounds();

  /**
   * Returns a high precision bounding box of the shape. There is no guarantee
   * that this is the minimum bounding box, but at least it never overflows.
   *
   * @return the shape's bounding box
   * @see #getBounds()
   * @since 1.2
   */
  Rectangle2D getBounds2D();

  /**
   * Test if the coordinates lie in the shape.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @return true if (x,y) lies inside the shape
   * @since 1.2
   */
  boolean contains(double x, double y);

  /**
   * Test if the point lie in the shape.
   *
   * @param p the high-precision point
   * @return true if p lies inside the shape
   * @throws NullPointerException if p is null
   * @since 1.2
   */
  boolean contains(Point2D p);

  /**
   * Test if a high-precision rectangle intersects the shape. This is true
   * if any point in the rectangle is in the shape, with the caveat that the
   * operation may include high probability estimates when the actual
   * calculation is prohibitively expensive. The {@link java.awt.geom.Area} 
   * class can be used for more precise answers.
   *
   * @param x the x coordinate of the rectangle
   * @param y the y coordinate of the rectangle
   * @param w the width of the rectangle, undefined results if negative
   * @param h the height of the rectangle, undefined results if negative
   * @return true if the rectangle intersects this shape
   * @see java.awt.geom.Area
   * @since 1.2
   */
  boolean intersects(double x, double y, double w, double h);

  /**
   * Test if a high-precision rectangle intersects the shape. This is true
   * if any point in the rectangle is in the shape, with the caveat that the
   * operation may include high probability estimates when the actual
   * calculation is prohibitively expensive. The {@link java.awt.geom.Area} 
   * class can be used for more precise answers.
   *
   * @param r the rectangle
   * @return true if the rectangle intersects this shape
   * @throws NullPointerException if r is null
   * @see #intersects(double, double, double, double)
   * @since 1.2
   */
  boolean intersects(Rectangle2D r);

  /**
   * Test if a high-precision rectangle lies completely in the shape. This is
   * true if all points in the rectangle are in the shape, with the caveat
   * that the operation may include high probability estimates when the actual
   * calculation is prohibitively expensive. The {@link java.awt.geom.Area} 
   * class can be used for more precise answers.
   *
   * @param x the x coordinate of the rectangle
   * @param y the y coordinate of the rectangle
   * @param w the width of the rectangle, undefined results if negative
   * @param h the height of the rectangle, undefined results if negative
   * @return true if the rectangle is contained in this shape
   * @see java.awt.geom.Area
   * @since 1.2
   */
  boolean contains(double x, double y, double w, double h);

  /**
   * Test if a high-precision rectangle lies completely in the shape. This is
   * true if all points in the rectangle are in the shape, with the caveat
   * that the operation may include high probability estimates when the actual
   * calculation is prohibitively expensive. The {@link java.awt.geom.Area} 
   * class can be used for more precise answers.
   *
   * @param r the rectangle
   * @return true if the rectangle is contained in this shape
   * @throws NullPointerException if r is null
   * @see #contains(double, double, double, double)
   * @since 1.2
   */
  boolean contains(Rectangle2D r);

  /**
   * Return an iterator along the shape boundary. If the optional transform
   * is provided, the iterator is transformed accordingly. Each call returns
   * a new object, independent from others in use. It is recommended, but
   * not required, that the Shape isolate iterations from future changes to
   * the boundary, and document this fact.
   *
   * @param transform an optional transform to apply to the 
   *                  iterator (<code>null</code> permitted).
   * @return a new iterator over the boundary
   * @since 1.2
   */
  //PathIterator getPathIterator(AffineTransform transform);

  /**
   * Return an iterator along the flattened version of the shape boundary.
   * Only SEG_MOVETO, SEG_LINETO, and SEG_CLOSE points are returned in the
   * iterator. The flatness parameter controls how far points are allowed to
   * differ from the real curve; although a limit on accuracy may cause this
   * parameter to be enlarged if needed.
   *
   * <p>If the optional transform is provided, the iterator is transformed
   * accordingly. Each call returns a new object, independent from others in
   * use. It is recommended, but not required, that the Shape isolate
   * iterations from future changes to the boundary, and document this fact.
   *
   * @param transform an optional transform to apply to the 
   *                  iterator (<code>null</code> permitted).
   * @param flatness the maximum distance for deviation from the real boundary
   * @return a new iterator over the boundary
   * @since 1.2
   */
  //PathIterator getPathIterator(AffineTransform transform, double flatness);
} 
