package com.horstmann.violet.graphs;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.horstmann.violet.framework.Edge;
import com.horstmann.violet.framework.Node;

/**
 * 
 *
 */
public class TeamSequenceDiagramGraph extends SequenceDiagramGraph {

   private static String id = "0";

   public TeamSequenceDiagramGraph() {
      super();
   }

   // Commands to both send and execute
   @Override
   public boolean add(Node n, Point2D p) {
      sendCommandToServer(new Command(CommandType.ADD_NODE, n, p));
//      return super.add(n, p);
      return true;
   }

   @Override
   public void removeNode(Node n) {
      sendCommandToServer(new Command(CommandType.REMOVE_NODE, n));
//      super.removeNode(n);
   }

   @Override
   public boolean connect(Edge e, Point2D p1, Point2D p2) {
      sendCommandToServer(new Command(CommandType.CONNECT_EDGE, e, p1, p2));
      return super.connect(e, p1, p2);
   }

   @Override
   public void removeEdge(Edge e) {
      sendCommandToServer(new Command(CommandType.REMOVE_EDGE, e));
      super.removeEdge(e);
   }

   @Override
   public void setMinBounds(Rectangle2D newValue) {
      sendCommandToServer(new Command(CommandType.SET_MIN_BOUNDS, newValue));
      super.setMinBounds(newValue);
   }

   // TODO: Implement this
   private void sendCommandToServer(Command command) {
      final String FILE_NAME = "serializationTest_Ignore.txt";
      File file = new File(FILE_NAME);
      try {
         file.createNewFile();
      } catch (IOException ex) {
         ex.printStackTrace();
      }

      try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file))) {
         objectOutputStream.writeObject(command);
      } catch (IOException ex) {
         ex.printStackTrace();
      }

      try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
         Command commandToExecute = (Command) objectInputStream.readObject();
         System.out.println(commandToExecute);
         executeCommand(commandToExecute);
      } catch (IOException|ClassNotFoundException ex) {
         ex.printStackTrace();
      }
   }

   // Commands to just execute locally and not send to server
   private boolean addLocal(Node n, Point2D p) {
      return super.add(n, p);
   }

   private void removeNodeLocal(Node n) {
      for (Object node: getNodes()) {
         if (n.getID().equals(((Node) node).getID())) {
            super.removeNode((Node) node);
            return;
         }
      }
   }

   private boolean connectLocal(Edge e, Point2D p1, Point2D p2) {
      return super.connect(e, p1, p2);
   }

   private void removeEdgeLocal(Edge e) {
      super.removeEdge(e);
   }

   private void setMinBoundsLocal(Rectangle2D newValue) {
      super.setMinBounds(newValue);
   }

   public void executeCommand(Command command) {
      CommandType commandType = command.getCommandType();
      List<Object> commandInputs = command.getCommandInputs();
      if (commandType == CommandType.ADD_NODE) {
         addLocal((Node) commandInputs.get(0), (Point2D) commandInputs.get(1));
      } else if (commandType == CommandType.REMOVE_NODE) {
         removeNodeLocal((Node) commandInputs.get(0));
      } else if (commandType == CommandType.CONNECT_EDGE) {
         connectLocal((Edge) commandInputs.get(0), (Point2D) commandInputs.get(1), (Point2D) commandInputs.get(2));
      } else if (commandType == CommandType.REMOVE_EDGE) {
         removeEdgeLocal((Edge) commandInputs.get(1));
      } else if (commandType == CommandType.SET_MIN_BOUNDS) {
         setMinBoundsLocal((Rectangle2D) commandInputs.get(1));
      }
   }


   public static String getID() {
      return id;
   }

   private class Command implements Serializable {

      private CommandType commandType;

      private List<Object> commandInputs;

      public Command(CommandType commandType, Object... commandInputs) {
         this.commandType = commandType;

         this.commandInputs = new ArrayList<>();
         this.commandInputs.addAll(Arrays.asList(commandInputs));
      }

      public CommandType getCommandType() {
         return commandType;
      }

      public List<Object> getCommandInputs() {
         return commandInputs;
      }

      @Override
      public String toString() {
         String ret = "";
         ret += "Command Type: " + commandType + "\n";
         ret += "Command Inputs: " + commandInputs;
         return ret;
      }

   }

   private enum CommandType {
      ADD_NODE, REMOVE_NODE, UPDATE_NODE,
      CONNECT_EDGE, REMOVE_EDGE, UPDATE_EDGE,
      SET_MIN_BOUNDS
   }

}
