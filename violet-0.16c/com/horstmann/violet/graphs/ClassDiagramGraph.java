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

package com.horstmann.violet.graphs;

import java.awt.geom.Point2D;

import com.horstmann.violet.edges.*;
import com.horstmann.violet.framework.Edge;
import com.horstmann.violet.framework.Graph;
import com.horstmann.violet.framework.Node;
import com.horstmann.violet.nodes.ClassNode;
import com.horstmann.violet.nodes.InterfaceNode;
import com.horstmann.violet.nodes.NoteNode;
import com.horstmann.violet.nodes.PackageNode;

/**
   A UML class diagram.
*/
public class ClassDiagramGraph extends Graph
{

   private static final long serialVersionUID = 7089103638372475069L;
   /**
    * Connects an edge between two points
    * @param e the edge 
    * @param p1 the start point
    * @param p2 the end point
    * @return Whether the edge was connected
    */
   public boolean connect(Edge e, Point2D p1, Point2D p2)
   {
      Node n1 = findNode(p1);
      Node n2 = findNode(p2);
      // if (n1 == n2) return false;
      return super.connect(e, p1, p2);
   }
   /**
    * Gets the Node prototypes for Class Diagrams
    * @return the node prototypes
    */
   public Node[] getNodePrototypes()
   {
      return NODE_PROTOTYPES;
   }
   /**
    * Gets the Edge prototypes for Class Diagrams
    * @return the edge prototypes
    */
   public Edge[] getEdgePrototypes()
   {
      return EDGE_PROTOTYPES;
   }

   private static final Node[] NODE_PROTOTYPES = new Node[4];

   private static final Edge[] EDGE_PROTOTYPES = new Edge[7];

   static
   {
      NODE_PROTOTYPES[0] = new ClassNode();
      NODE_PROTOTYPES[1] = new InterfaceNode();
      NODE_PROTOTYPES[2] = new PackageNode();
      NODE_PROTOTYPES[3] = new NoteNode();

      ClassRelationshipEdge dependency = new ClassRelationshipEdge();
      dependency.setLineStyle(LineStyle.DOTTED);
      dependency.setEndArrowHead(ArrowHead.V);
      EDGE_PROTOTYPES[0] = dependency;
      ClassRelationshipEdge inheritance = new ClassRelationshipEdge();
      inheritance.setBentStyle(BentStyle.VHV);
      inheritance.setEndArrowHead(ArrowHead.TRIANGLE);
      EDGE_PROTOTYPES[1] = inheritance;

      ClassRelationshipEdge interfaceInheritance = new ClassRelationshipEdge();
      interfaceInheritance.setBentStyle(BentStyle.VHV);
      interfaceInheritance.setLineStyle(LineStyle.DOTTED);
      interfaceInheritance.setEndArrowHead(ArrowHead.TRIANGLE);
      EDGE_PROTOTYPES[2] = interfaceInheritance;

      ClassRelationshipEdge association = new ClassRelationshipEdge();
      association.setBentStyle(BentStyle.HVH);
      association.setEndArrowHead(ArrowHead.V);
      EDGE_PROTOTYPES[3] = association;

      ClassRelationshipEdge aggregation = new ClassRelationshipEdge();
      aggregation.setBentStyle(BentStyle.HVH);
      aggregation.setStartArrowHead(ArrowHead.DIAMOND);
      EDGE_PROTOTYPES[4] = aggregation;

      ClassRelationshipEdge composition = new ClassRelationshipEdge();
      composition.setBentStyle(BentStyle.HVH);
      composition.setStartArrowHead(ArrowHead.BLACK_DIAMOND);
      EDGE_PROTOTYPES[5] = composition;

      EDGE_PROTOTYPES[6] = new NoteEdge();
   }
}





