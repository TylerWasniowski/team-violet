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
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
   A class that supplies convenience implementations for 
   a number of methods in the Edge interface
*/
public abstract class AbstractEdge extends AbstractUniquelyIdentifiable implements Edge
{
   private static final long serialVersionUID = 9000165480500528416L;
   /**
    * Clones the object
    * @return a clon of the object
    */
   public Object clone() throws CloneNotSupportedException
   {
      return super.clone();
   }
   /**
    * Connects two nodes
    * @param s the first node
    * @param e the second node
    */
   public void connect(Node s, Node e)
   {  
      start = s;
      end = e;
   }
   /**
    * Gets the start node
    * @return the start node
    */
   public Node getStart()
   {
      return start;
   }
   /**
    * Gets the ends node
    * @return the end node
    */
   public Node getEnd()
   {
      return end;
   }
   /**
    * Gets bounding rectangle of the edge
    * @param g2 the graphics context
    * @return the bounding rectangle of the edge
    */
   public Rectangle2D getBounds(Graphics2D g2)
   {
      Line2D conn = getConnectionPoints();      
      Rectangle2D r = new Rectangle2D.Double();
      r.setFrameFromDiagonal(conn.getX1(), conn.getY1(),
         conn.getX2(), conn.getY2());
      return r;
   }
   /**
    * Gets the line from the start to end point
    * @return the line from start point to end point
    */
   public Line2D getConnectionPoints()
   {
      Rectangle2D startBounds = start.getBounds();
      Rectangle2D endBounds = end.getBounds();
      Point2D startCenter = new Point2D.Double(
         startBounds.getCenterX(), startBounds.getCenterY());
      Point2D endCenter = new Point2D.Double(
         endBounds.getCenterX(), endBounds.getCenterY());
      Direction toEnd = new Direction(startCenter, endCenter);
      return new Line2D.Double(
         start.getConnectionPoint(toEnd),
         end.getConnectionPoint(toEnd.turn(180)));
   }

   private Node start;
   private Node end;
}
