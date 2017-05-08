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

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.horstmann.violet.edges.ObjectReferenceEdge;
import com.horstmann.violet.framework.Direction;
import com.horstmann.violet.framework.Edge;
import com.horstmann.violet.framework.Graph;
import com.horstmann.violet.framework.Grid;
import com.horstmann.violet.framework.MultiLineString;
import com.horstmann.violet.framework.Node;
import com.horstmann.violet.framework.RectangularNode;

/**
   A field node in an object diagram.
*/
public class FieldNode extends RectangularNode
{
   private static final long serialVersionUID = 1434565809710223792L;
   /**
    * Creates a Field Node
    */
   public FieldNode()
   {
      name = new MultiLineString();
      name.setJustification(MultiLineString.RIGHT);
      value = new MultiLineString();
      setBounds(new Rectangle2D.Double(0, 0, 
         DEFAULT_WIDTH, DEFAULT_HEIGHT));
   }
   /**
    * Draws the field node
    * @param g2 the graphics context
    */
   public void draw(Graphics2D g2)
   {
      super.draw(g2);
      Rectangle2D b = getBounds();
      double leftWidth = name.getBounds(g2).getWidth();
      MultiLineString equal = new MultiLineString();
      equal.setText(" = ");
      double midWidth = equal.getBounds(g2).getWidth();
      
      double rightWidth = value.getBounds(g2).getWidth();
      if (rightWidth == 0) rightWidth = DEFAULT_WIDTH / 2;
      rightWidth = Math.max(rightWidth, boxWidth - midWidth / 2);

      nameBounds = new Rectangle2D.Double(b.getX(),
         b.getY(), leftWidth, b.getHeight());
      name.draw(g2, nameBounds);
      Rectangle2D mid = new Rectangle2D.Double(b.getX() + 
         leftWidth, b.getY(), 
         midWidth,
         b.getHeight());
      equal.draw(g2, mid);
      valueBounds = new Rectangle2D.Double(b.getMaxX() -
         rightWidth, b.getY(), rightWidth, b.getHeight());
      if (boxedValue)
         value.setJustification(MultiLineString.CENTER);
      else
         name.setJustification(MultiLineString.LEFT);
      value.draw(g2, valueBounds);
      if (boxedValue) g2.draw(valueBounds);
   }
   /**
    * Tries to add the edge between the nodes at the given points
    * @param e the edge to add
    * @param p1 the start point
    * @param p2 the end point
    * @return whether the edge was added
    */
   public boolean addEdge(Edge e, Point2D p1, Point2D p2)
   {
      if (e instanceof ObjectReferenceEdge
         && e.getEnd() instanceof ObjectNode)
      {
         value.setText("");
         return true;
      }
      return false;
   }
   /**
    * Adds the node 
    * @param n the node to add
    * @param p the location to place the node
    * @return whether the node was added
    */
   public boolean addNode(Node n, Point2D p)
   {
      return n instanceof PointNode;
   }
   /**
    * Gets the connection point for the node
    * @param d the direction of the edge
    * @return the connection point
    */
   public Point2D getConnectionPoint(Direction d)
   {
      Rectangle2D b = getBounds();
      return new Point2D.Double(
         (b.getMaxX() + b.getX() + axisX) / 2,
         b.getCenterY());
   }
   /**
    * Prepares the node to be displayed
    * @param g the graph that contains the node
    * @param g2 the graphics context
    * @param grid the grid that contains the node
    */
   public void layout(Graph g, Graphics2D g2, Grid grid)
   {
      nameBounds = name.getBounds(g2); 
      valueBounds = value.getBounds(g2);
      MultiLineString equal = new MultiLineString();
      equal.setText(" = ");
      Rectangle2D e = equal.getBounds(g2);
      double leftWidth = nameBounds.getWidth();
      double midWidth = e.getWidth();
      double rightWidth = valueBounds.getWidth();
      if (rightWidth == 0) rightWidth = DEFAULT_WIDTH / 2;
      rightWidth = Math.max(rightWidth, boxWidth - midWidth / 2);
      double width = leftWidth + midWidth + rightWidth;
      double height = Math.max(nameBounds.getHeight(), Math.max(
         valueBounds.getHeight(), e.getHeight()));

      Rectangle2D b = getBounds();
      setBounds(new Rectangle2D.Double(b.getX(), b.getY(), width, height));
      axisX = leftWidth + midWidth / 2;
      
      valueBounds.setFrame(b.getMaxX() - rightWidth, b.getY(), valueBounds.getWidth(), valueBounds.getHeight());
   }

   
   /**
      Sets the name property value.
      @param newValue the field name
   */
   public void setName(MultiLineString newValue)
   {
      name = newValue;
   }

   /**
      Gets the name property value.
      @return the field name
   */
   public MultiLineString getName()
   {
      return name;
   }

   /**
      Sets the value property value.
      @param newValue the field value
   */
   public void setValue(MultiLineString newValue)
   {
      value = newValue;
   }

   /**
      Gets the value property value.
      @return the field value
   */
   public MultiLineString getValue()
   {
      return value;
   }

   /**
      Sets the box width.
      @param newValue the new box width
   */
   public void setBoxWidth(double newValue)
   {
      boxWidth = newValue;
   }
   
   /**
      Sets the boxedValue property value.
      @param newValue the new property value
   */
   public void setBoxedValue(boolean newValue)
   {
      boxedValue = newValue;
   }

   /**
      Gets the boxedValue property value.
      @return the property value
   */
   public boolean isBoxedValue()
   {
      return boxedValue;
   }
   /**
    * Clones the object
    * @return the clone of the object
    */
   public Object clone() throws CloneNotSupportedException
   {
      FieldNode cloned = (FieldNode)super.clone();
      cloned.name = (MultiLineString)name.clone();
      cloned.value = (MultiLineString)value.clone();
      return cloned;
   }

   /**
      Gets the x-offset of the axis (the location
      of the = sign) from the left corner of the bounding rectangle.
      @return the x-offset of the axis
   */
   public double getAxisX()
   {
      return axisX;
   }
   /**
    * Gets the node as a shape
    * @return the node as a shape
    */
   public Shape getShape()
   {
      if (boxedValue) return valueBounds; else return null;
   }

   private double axisX;
   private MultiLineString name;
   private MultiLineString value;
   private Rectangle2D nameBounds;
   private Rectangle2D valueBounds;
   private boolean boxedValue;
   private double boxWidth;

   public static int DEFAULT_WIDTH = 60;
   public static int DEFAULT_HEIGHT = 20;
}
