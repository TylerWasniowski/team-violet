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

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Statement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
   A graph consisting of selectable nodes and edges.
*/
public abstract class Graph implements Serializable
{
   private static final long serialVersionUID = 3L;
   /**
      Constructs a graph with no nodes or edges.
   */
   public Graph()
   {
      nodes = Collections.synchronizedList(new ArrayList<>());
      edges = Collections.synchronizedList(new ArrayList<>());
      nodesToBeRemoved = new ArrayList<>();
      edgesToBeRemoved = new ArrayList<>();
      needsLayout = true;
   }

   /**
      Adds an edge to the graph that joins the nodes containing
      the given points. If the points aren't both inside nodes,
      then no edge is added.
      @param e the edge to add
      @param p1 a point in the starting node
      @param p2 a point in the ending node
      @return whether the connection was successful
   */
   public boolean connect(Edge e, Point2D p1, Point2D p2)
   {
      Node n1 = findNode(p1);
      Node n2 = findNode(p2);
      if (n1 != null)
      {
         e.connect(n1, n2);
         if (n1.addEdge(e, p1, p2) && e.getEnd() != null)
         {
            edges.add(e);
            if (!nodes.contains(e.getEnd()))
               nodes.add(e.getEnd());
            needsLayout = true;
            return true;
         }
      }
      return false;
   }

   /**
      Adds a node to the graph so that the top left corner of
      the bounding rectangle is at the given point.
      @param n the node to add
      @param p the desired location
      @return whether the node was added
   */
   public boolean add(Node n, Point2D p)
   {
      Rectangle2D bounds = n.getBounds();
      n.translate(p.getX() - bounds.getX(), 
         p.getY() - bounds.getY()); 

      boolean accepted = false;
      boolean insideANode = false;
      for (int i = nodes.size() - 1; i >= 0 && !accepted; i--)
      {
         Node parent = (Node)nodes.get(i);
         if (parent.contains(p)) 
         {
            insideANode = true;
            if (parent.addNode(n, p)) accepted = true;
         }
      }
      if (insideANode && !accepted) 
         return false;
      nodes.add(n);
      needsLayout = true;
      return true;
   }

   /**
      Finds a node containing the given point.
      @param p a point
      @return a node containing p or null if no nodes contain p
   */
   public Node findNode(Point2D p)
   {
      for (int i = nodes.size() - 1; i >= 0; i--)
      {
         Node n = (Node)nodes.get(i);
         if (n.contains(p)) return n;
      }
      return null;
   }

   /**
      Finds an edge containing the given point.
      @param p a point
      @return an edge containing p or null if no edges contain p
   */
   public Edge findEdge(Point2D p)
   {
      for (int i = edges.size() - 1; i >= 0; i--)
      {
         Edge e = (Edge)edges.get(i);
         if (e.contains(p)) return e;
      }
      return null;
   }
   
   /**
      Draws the graph
      @param g2 the graphics context
      @param g the grid
   */
   public void draw(Graphics2D g2, Grid g)
   {
      layout(g2, g);

      for (int i = 0; i < nodes.size(); i++)
      {
         Node n = (Node)nodes.get(i);
         n.draw(g2);
      }

      for (int i = 0; i < edges.size(); i++)
      {
         Edge e = (Edge)edges.get(i);
         e.draw(g2);
      }
   }
   
   /**
      Removes a node and all edges that start or end with that node
      @param n the node to remove
   */
   public void removeNode(Node n)
   {
      if (nodesToBeRemoved.contains(n)) return;
      nodesToBeRemoved.add(n);
      // notify nodes of removals
      for (int i = 0; i < nodes.size(); i++)
      {
         Node n2 = nodes.get(i);
         n2.removeNode(this, n);
      }
      for (int i = 0; i < edges.size(); i++)
      {
         Edge e = edges.get(i);
         if (e.getStart() == n || e.getEnd() == n)
            removeEdge(e);
      }

      needsLayout = true;
   }

   /**
      Removes an edge from the graph.
      @param e the edge to remove
   */
   public void removeEdge(Edge e)
   {
      if (edgesToBeRemoved.contains(e)) return;
      edgesToBeRemoved.add(e);
      for (int i = nodes.size() - 1; i >= 0; i--)
      {
         Node n = nodes.get(i);
         n.removeEdge(this, e);
      }
      needsLayout = true;
   }

   /**
      Causes the layout of the graph to be recomputed.
   */
   public void layout()
   {
      needsLayout = true;
   }

   /**
      Computes the layout of the graph.
      If you override this method, you must first call 
      <code>super.layout</code>.
      @param g2 the graphics context
      @param g the grid to snap to
   */
   protected void layout(Graphics2D g2, Grid g)
   {
      if (!needsLayout) return;
      nodes.removeAll(nodesToBeRemoved);
      edges.removeAll(edgesToBeRemoved);
      nodesToBeRemoved.clear();
      edgesToBeRemoved.clear();

      for (int i = 0; i < nodes.size(); i++)
      {
         Node n = nodes.get(i);
         n.layout(this, g2, g);
      }
      needsLayout = false;
   }

   /**
      Gets the smallest rectangle enclosing the graph
      @param g2 the graphics context
      @return the bounding rectangle
   */
   public Rectangle2D getBounds(Graphics2D g2)
   {
      Rectangle2D r = minBounds;
      for (int i = 0; i < nodes.size(); i++)
      {
         Node n = nodes.get(i);
         Rectangle2D b = n.getBounds();
         if (r == null) r = b;
         else r.add(b);
      }
      for (int i = 0; i < edges.size(); i++)
      {
         Edge e = edges.get(i);
         r.add(e.getBounds(g2));
      }
      return r == null ? new Rectangle2D.Double() : new Rectangle2D.Double(r.getX(), r.getY(), 
            r.getWidth() + AbstractNode.SHADOW_GAP, r.getHeight() + AbstractNode.SHADOW_GAP);
   }
   
   public Rectangle2D getMinBounds() { return minBounds; }
   public void setMinBounds(Rectangle2D newValue) { minBounds = newValue; }

   /**
      Gets the node types of a particular graph type.
      @return an array of node prototypes
   */   
   public abstract Node[] getNodePrototypes();

   /**
      Gets the edge types of a particular graph type.
      @return an array of edge prototypes
   */   
   public abstract Edge[] getEdgePrototypes();
 
   /**
      Adds a persistence delegate to a given encoder that
      encodes the child nodes of this node.
      @param encoder the encoder to which to add the delegate
   */
   public static void setPersistenceDelegate(Encoder encoder)
   {
      encoder.setPersistenceDelegate(Graph.class, new
         DefaultPersistenceDelegate()
         {
            protected void initialize(Class type, 
               Object oldInstance, Object newInstance, 
               Encoder out) 
            {
               super.initialize(type, oldInstance, 
                  newInstance, out);
               Graph g = (Graph)oldInstance;
         
               for (int i = 0; i < g.nodes.size(); i++)
               {
                  Node n = g.nodes.get(i);
                  Rectangle2D bounds = n.getBounds();
                  Point2D p = new Point2D.Double(bounds.getX(),
                     bounds.getY());
                  out.writeStatement(
                     new Statement(oldInstance,
                        "addNode", new Object[]{ n, p }) );
               }
               for (int i = 0; i < g.edges.size(); i++)
               {
                  Edge e = g.edges.get(i);
                  out.writeStatement(
                     new Statement(oldInstance,
                        "connect", 
                        new Object[]{ e, e.getStart(), e.getEnd() }) );            
               }
            }
         });
   }

   /**
      Gets the nodes of this graph.
      @return an unmodifiable collection of the nodes
   */
   public Collection<Node> getNodes() { return nodes; }

   /**
      Gets the edges of this graph.
      @return an unmodifiable collection of the edges
   */
   public Collection<Edge> getEdges() { return edges; }

   /**
      Adds a node to this graph. This method should
      only be called by a decoder when reading a data file.
      @param n the node to add
      @param p the desired location
   */
   public void addNode(Node n, Point2D p)
   {
      Rectangle2D bounds = n.getBounds();
      n.translate(p.getX() - bounds.getX(), 
         p.getY() - bounds.getY()); 
      nodes.add(n); 
   }

   /**
      Adds an edge to this graph. This method should
      only be called by a decoder when reading a data file.
      @param e the edge to add
      @param start the start node of the edge
      @param end the end node of the edge
      @return whether the edge was connected
   */
   public boolean connect(Edge e, Node start, Node end)
   {
      if (e == null || start == null || end == null)
         return false;

      e.connect(start, end);
      edges.add(e);

      return true;
   }

   /**
    * Gets the Panel that the graph object belongs to.
    * @return the Panel that the graph object belongs to.
    */
   public JPanel getJPanel() {
      return panel;
   }


   /**
    * Sets the Panel that the graph object belongs to the given Panel.
    * @param panel the new Panel
    */
   public void setJPanel(JPanel panel) {
      this.panel = panel;
   }

   private List<Node> nodes;
   private List<Edge> edges;
   private transient List<Node> nodesToBeRemoved;
   private transient List<Edge> edgesToBeRemoved;
   private transient boolean needsLayout;
   private transient Rectangle2D minBounds;
   private JPanel panel;
}