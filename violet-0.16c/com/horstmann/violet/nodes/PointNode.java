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

package com.horstmann.violet.nodes;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.horstmann.violet.framework.AbstractNode;
import com.horstmann.violet.framework.Direction;


/**
   An inivisible node that is used in the toolbar to draw an
   edge, and in notes to serve as an end point of the node
   connector.
*/
public class PointNode extends AbstractNode
{
   private static final long serialVersionUID = -7770181404461767597L;

   /**
      Constructs a point node with coordinates (0, 0)
   */
   public PointNode()
   {
      point = new Point2D.Double();
   }
   /**
    * Draws the node
    * @param g2 the graphics context
    */
   public void draw(Graphics2D g2)
   {
   }
   /**
    * Moves the node
    * @param dx the amount to move in the x direction
    * @param dy the amount to move in the y direction
    */
   public void translate(double dx, double dy)
   {
      point.setLocation(point.getX() + dx,
         point.getY() + dy);
   }
   /**
    * Checks if the node conatains the given point
    * @param p the the point to test for
    * @return if the point is within the threshold of the node
    */
   public boolean contains(Point2D p)
   {
      final double THRESHOLD = 5;
      return point.distance(p) < THRESHOLD;
   }
   /**
    * Gets the bounding rectangle of the node
    * @return the bounding rectangle
    */
   public Rectangle2D getBounds()
   {
      return new Rectangle2D.Double(point.getX(), 
         point.getY(), 0, 0);
   }
   /**
    * Sets the bounding rectangle of the node
    * @param rect the new bounding rectangle
    */
   public void setBounds(Rectangle rect) {

   }
   /**
    * Gets the connection point of the node
    * @param d the direction of the edge
    * @return the connection point
    */
   public Point2D getConnectionPoint(Direction d)
   {
      return point;
   }

   private Point2D point;
}
