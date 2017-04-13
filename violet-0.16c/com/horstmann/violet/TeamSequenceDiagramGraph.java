package com.horstmann.violet;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.horstmann.violet.framework.Edge;
import com.horstmann.violet.framework.Graph;
import com.horstmann.violet.framework.Grid;
import com.horstmann.violet.framework.Node;

/**
 * 
 *
 */
public class TeamSequenceDiagramGraph extends Graph{


   
   public Node[] getNodePrototypes()
   {
      return NODE_PROTOTYPES;
   }

   public Edge[] getEdgePrototypes()
   {
      return EDGE_PROTOTYPES;
   }

   private static final Node[] NODE_PROTOTYPES = new Node[3];

   private static final Edge[] EDGE_PROTOTYPES = new Edge[3];

   static
   {
      NODE_PROTOTYPES[0] = new ImplicitParameterNode();
      NODE_PROTOTYPES[1] = new CallNode();
      NODE_PROTOTYPES[2] = new NoteNode();
      EDGE_PROTOTYPES[0] = new CallEdge();
      EDGE_PROTOTYPES[1] = new ReturnEdge();
      EDGE_PROTOTYPES[2] = new NoteEdge();
   }
   
}
