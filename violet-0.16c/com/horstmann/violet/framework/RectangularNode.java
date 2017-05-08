/*
Violet - A program for editing UML diagrams.

Copyright (C) 2002 Cay S. Horstmann (http://horstmann.com)

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.horstmann.violet.framework;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
   A node that has a rectangular shape.
*/
public abstract class RectangularNode extends AbstractNode
{
   private static final long serialVersionUID = -4483825085278940812L;
   /**
    * Clones the object
    * @return the clone of the object
    */
   public Object clone() throws CloneNotSupportedException
   {
      RectangularNode cloned = (RectangularNode)super.clone();
      cloned.bounds = (Rectangle2D)bounds.clone();
      return cloned;
   }
   /**
    * Moves the node by the given values
    * @param dx movement in x direction
    * @param dy movement in y direction
    */
   public void translate(double dx, double dy)
   {
      bounds.setFrame(bounds.getX() + dx,
         bounds.getY() + dy, 
         bounds.getWidth(), 
         bounds.getHeight());
      super.translate(dx, dy);
   }
   /**
    * Checks whether the point is in the nodes bounding rectangle
    * @param p the point to test
    * @return whether the point is within the node
    */
   public boolean contains(Point2D p)
   {
      return bounds.contains(p);
   }
   /**
    * Gets the bounding rectangle of the node
    * @return the bounding rectangle of the node
    */
   public Rectangle2D getBounds()
   {
      return (Rectangle2D)bounds.clone();
   }
   /**
    * Sets the bound of the node
    * @param newBounds the new bounding rectangle
    */
   public void setBounds(Rectangle2D newBounds)
   {
      bounds = newBounds;
   }
   /**
    * Prepares the node to be displayed
    * @param g the graph where the node is displayed
    * @param g2 the graphics context
    * @param grid the grid
    */
   public void layout(Graph g, Graphics2D g2, Grid grid)
   {
      grid.snap(bounds);
   }
   /**
    * Gets the point where an edge would attach to the node
    * @param d the direction of the edge
    * @return the connection point
    */
   public Point2D getConnectionPoint(Direction d)
   {
      double slope = bounds.getHeight() / bounds.getWidth();
      double ex = d.getX();
      double ey = d.getY();
      double x = bounds.getCenterX();
      double y = bounds.getCenterY();
      
      if (ex != 0 && -slope <= ey / ex && ey / ex <= slope)
      {  
         // intersects at left or right boundary
         if (ex > 0) 
         {
            x = bounds.getMaxX();
            y += (bounds.getWidth() / 2) * ey / ex;
         }
         else
         {
            x = bounds.getX();
            y -= (bounds.getWidth() / 2) * ey / ex;
         }
      }
      else if (ey != 0)
      {  
         // intersects at top or bottom
         if (ey > 0) 
         {
            x += (bounds.getHeight() / 2) * ex / ey;
            y = bounds.getMaxY();
         }
         else
         {
            x -= (bounds.getHeight() / 2) * ex / ey;
            y = bounds.getY();
         }
      }
      return new Point2D.Double(x, y);
   }
   /**
    * Writes all non static and non transisent fields of
    * this class to the output stream
    * @param out the output stream
    * @throws IOException if there is a problem with the IO
    */
   private void writeObject(ObjectOutputStream out)
      throws IOException
   {
      out.defaultWriteObject();
      writeRectangularShape(out, bounds);
   }

   /**
      A helper method to overcome the problem that the 2D shapes
      aren't serializable. It writes x, y, width and height
      to the stream.
      @param out the stream
      @param s the shape      
   */
   private static void writeRectangularShape(
      ObjectOutputStream out, 
      RectangularShape s)
      throws IOException
   {
      out.writeDouble(s.getX());
      out.writeDouble(s.getY());
      out.writeDouble(s.getWidth());
      out.writeDouble(s.getHeight());
   }
   /**
    * Reads the non staic fields of this class from the input stream
    * @param in the input stream
    * @throws IOException if there is a problem with IO
    * @throws ClassNotFoundException if the object in the input stream does not match the fields of this class
    */
   private void readObject(ObjectInputStream in)
      throws IOException, ClassNotFoundException
   {
      in.defaultReadObject();
      bounds = new Rectangle2D.Double();
      readRectangularShape(in, bounds);
   }
   
   /**
      A helper method to overcome the problem that the 2D shapes
      aren't serializable. It reads x, y, width and height
      from the stream.
      @param in the stream
      @param s the shape whose frame is set from the stream values
   */
   private static void readRectangularShape(ObjectInputStream in,
      RectangularShape s)
      throws IOException
   {
      double x = in.readDouble();
      double y = in.readDouble();
      double width = in.readDouble();
      double height = in.readDouble();
      s.setFrame(x, y, width, height);
   }
   /**
    * Gets this node as a shape
    * @return this node as a shape
    */
   public Shape getShape()
   {
      return bounds;
   }

   @Override
   public String toString() {
      return "RectangularNode Bounds: " + getBounds().toString();
   }

   private transient Rectangle2D bounds;
}
