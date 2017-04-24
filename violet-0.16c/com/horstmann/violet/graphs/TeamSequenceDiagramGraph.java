package com.horstmann.violet.graphs;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import com.horstmann.violet.client.Publisher;
import com.horstmann.violet.client.Subscriber;
import com.horstmann.violet.framework.Edge;
import com.horstmann.violet.framework.Node;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.util.*;
import org.apache.activemq.util.ByteArrayInputStream;

import javax.jms.JMSException;

/**
 *
 *
 */
public class TeamSequenceDiagramGraph extends SequenceDiagramGraph {

    private static String id = "0";
    private Publisher publisher;
    private Subscriber subscriber;

    public TeamSequenceDiagramGraph() {
        super();

        publisher = new Publisher();
        publisher.start();

        subscriber = new Subscriber();
        subscriber.start();

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
//      return super.connect(e, p1, p2);
        return true;
    }

    @Override
    public void removeEdge(Edge e) {
        sendCommandToServer(new Command(CommandType.REMOVE_EDGE, e));
//      super.removeEdge(e);
    }

    @Override
    public void setMinBounds(Rectangle2D newValue) {
        sendCommandToServer(new Command(CommandType.SET_MIN_BOUNDS, newValue));
//      super.setMinBounds(newValue);
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

        // Turn Command into String and send to server
        ByteArrayOutputStream byteArrayOutputStream;
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                byteArrayOutputStream = new ByteArrayOutputStream())) {
            objectOutputStream.writeObject(command);
            publisher.sendMessage(Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray()));
        } catch (IOException|InterruptedException|JMSException ex) {
            ex.printStackTrace();
        }
    }

    // Commands to just execute locally and not send to server
    private boolean addLocal(Node n, Point2D p) {
        return super.add(n, p);
    }

    private void removeNodeLocal(Node n) {
        for (Object node : getNodes()) {
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

    public void executeCommand(String commandString) {
        /*
         *  Decode String into ByteArray and read command from ByteArray with the ObjectInputStream,
         *  and then interpret Command and execute the correct methods
         */
        try (ObjectInputStream objectInputStream = new ObjectInputStream(
                new ByteArrayInputStream(Base64.getDecoder().decode(commandString)))) {
            Command command = (Command) objectInputStream.readObject();

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
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
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
