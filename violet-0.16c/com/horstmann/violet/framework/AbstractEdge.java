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
abstract class AbstractEdge implements Edge
{
   private static final long serialVersionUID = 9000165480500528416L;

   // These are for the syncing. We need to be able to identify unique edges.
   private String id;
   private String graphID;
   private static Map<String, Integer> classNameToNumberOfObjects = new HashMap<>(); // Counts number of objects of each class of edge

   public AbstractEdge() {
      this.id = this.getClass().toString() + incrementCountInMap();
   }

   public Object clone()
   {
      try
      {
         AbstractEdge cloned = (AbstractEdge)super.clone();
         cloned.id = this.graphID + cloned.getClass().toString() + incrementCountInMap();
         return cloned;
      }
      catch (CloneNotSupportedException exception)
      {
         return null;
      }
   }

   public void connect(Node s, Node e)
   {  
      start = s;
      end = e;
   }

   /**
    * Increments the value linked to the class name, or initializes the value to 1 if the value linked to the
    * class name was 0.
    * @return The new number linked to the class name
    */
   private Integer incrementCountInMap() {
      Integer numberOfObjectsOfThisClass = classNameToNumberOfObjects.get(this.getClass().toString());
      if (numberOfObjectsOfThisClass == null) {
         classNameToNumberOfObjects.put(this.getClass().toString(), 1);
      } else {
         classNameToNumberOfObjects.put(this.getClass().toString(), numberOfObjectsOfThisClass + 1);
      }

      return numberOfObjectsOfThisClass;
   }

   public boolean equals(Object o) {
      if (!(o instanceof AbstractEdge))
         return false;

      AbstractEdge that = (AbstractEdge) o;
      if ((this.id != null && that.id == null) || (this.id == null && that.id != null))
         return false;
      else if (this.id != null && !this.id.equals(that.id))
         return false;
      else if ((this.start != null && that.start == null) || (this.start == null && that.start != null))
         return false;
      else if (this.start != null && !this.start.equals(that.start))
         return false;
      else if ((this.end != null && that.end == null) || (this.end == null && that.end != null))
         return false;
      else if (this.end != null && !this.end.equals(that.end))
         return false;
      else
         return true;
   }

   public int hashCode() {
      return Objects.hash(id);
   }

   public Node getStart()
   {
      return start;
   }

   public Node getEnd()
   {
      return end;
   }

   public Rectangle2D getBounds(Graphics2D g2)
   {
      Line2D conn = getConnectionPoints();      
      Rectangle2D r = new Rectangle2D.Double();
      r.setFrameFromDiagonal(conn.getX1(), conn.getY1(),
         conn.getX2(), conn.getY2());
      return r;
   }

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

   public String getID() {
      return id;
   }

   public void setGraphID(String graphID) {
      this.graphID = graphID;
      id = graphID + this.getClass().toString() + classNameToNumberOfObjects.get(this.getClass().toString());
   }

   private Node start;
   private Node end;
}
