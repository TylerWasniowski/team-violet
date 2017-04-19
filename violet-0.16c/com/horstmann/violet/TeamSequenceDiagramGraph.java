package com.horstmann.violet;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.horstmann.violet.edges.CallEdge;
import com.horstmann.violet.edges.NoteEdge;
import com.horstmann.violet.edges.ReturnEdge;
import com.horstmann.violet.framework.Edge;
import com.horstmann.violet.framework.Node;
import com.horstmann.violet.nodes.CallNode;
import com.horstmann.violet.nodes.NoteNode;

/**
 * 
 *
 */
public class TeamSequenceDiagramGraph extends SequenceDiagramGraph {


   
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

   @Override
   public void removeNode(Node n) {
      super.removeNode(n);
   }

   @Override
   public boolean connect(Edge e, Point2D p1, Point2D p2) {
      return super.connect(e, p1, p2);
   }

   @Override
   public boolean add(Node n, Point2D p) {
      return super.add(n, p);
   }

   @Override
   public void removeEdge(Edge e) {
      super.removeEdge(e);
   }

   @Override
   public void addNode(Node n, Point2D p) {
      super.addNode(n, p);
   }

   @Override
   public void connect(Edge e, Node start, Node end) {
      super.connect(e, start, end);
   }

   @Override
   public void setMinBounds(Rectangle2D newValue) {
      super.setMinBounds(newValue);
   }
}
