package com.horstmann.violet.graphs;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.horstmann.violet.client.PubSub;
import com.horstmann.violet.commands.*;
import com.horstmann.violet.client.Publisher;
import com.horstmann.violet.client.Subscriber;
import com.horstmann.violet.commands.AddNodeCommand;
import com.horstmann.violet.framework.Edge;
import com.horstmann.violet.framework.Node;
import com.horstmann.violet.framework.UniquelyIdentifiable;
import javafx.util.Pair;

import javax.jms.JMSException;

/**
 * A SequenceDiagram that stays synced with other clients connected to the same project.
 */
public class TeamSequenceDiagramGraph extends SequenceDiagramGraph implements TeamDiagram {

    private static final long serialVersionUID = -9088160815514315525L;

    // A unique id for this graph, used when figuring out what graph added what object to the synced diagram
    private String id;

    // The object that communicate with the server.
    private transient PubSub connectionToServer;

    // A map connecting graphIDs to the items that the graph is selecting
    private ConcurrentHashMap<String, Pair<Color, Set<UniquelyIdentifiable>>> graphIDsToItemSelections;

    public TeamSequenceDiagramGraph() throws JMSException {
        super();

        connectionToServer = new PubSub(this);

        id = connectionToServer.start();

        graphIDsToItemSelections = new ConcurrentHashMap<>();

    }

    // Commands to both send and execute
    @Override
    public boolean add(Node n, Point2D p) {
        n.assignGraphID(id);
        if (super.add(n, p)) {
            sendCommandToServer(new AddNodeCommand(n, p));
            return true;
        }

        return false;
    }

    @Override
    public void removeNode(Node n) {
        super.removeNode(n);
        sendCommandToServer(new RemoveNodeCommand(n.getID()));
    }

    @Override
    public boolean connect(Edge e, Point2D p1, Point2D p2) {
        e.assignGraphID(id);
        if (super.connect(e, p1, p2)) {
            sendCommandToServer(new ConnectEdgeCommand(e, p1, p2));
            return true;
        }

        return false;
    }

    @Override
    public void removeEdge(Edge e) {
        super.removeEdge(e);
        sendCommandToServer(new RemoveEdgeCommand(e.getID()));
    }

    @Override
    public void setMinBounds(Rectangle2D newValue) {
        super.setMinBounds(newValue);
    }

    @Override
    public boolean sendCommandToServer(Command command) {
        try {
            connectionToServer.sendCommand(command);
        } catch (InterruptedException|JMSException ex) {
            ex.printStackTrace();
            return false;
        }
//        command.execute(this);

        return true;
    }

    // Commands to just execute locally and not send to server
    @Override
    public boolean addLocal(Node n, Point2D p) {
        if (!getNodes().contains(n))
            return super.add(n, p);

        return false;
    }

    @Override
    public void removeNodeLocal(String idOfNodeToRemove) {
        for (Node node : getNodes()) {
            if (node.getID().equals(idOfNodeToRemove)) {
                super.removeNode(node);
                return;
            }
        }
    }

    @Override
    public boolean connectLocal(Edge edge, Point2D startPoint, Point2D endPoint) {
        if (!getEdges().contains(edge))
            return super.connect(edge, startPoint, endPoint);

        return false;
    }

    @Override
    public void removeEdgeLocal(String idOfEdgeToRemove) {
        Edge edgeToRemove = findEdgeFromID(idOfEdgeToRemove);
        if (edgeToRemove != null)
            super.removeEdge(edgeToRemove);
    }

    @Override
    public Node findNodeFromID(String idOfNodeToFind) {
        for (Node node: getNodes()) {
            if (idOfNodeToFind.equals(node.getID())) {
                return node;
            }
        }

        return null;
    }

    @Override
    public Edge findEdgeFromID(String idOfEdgeToFind) {
        for (Edge edge: getEdges()) {
            if (idOfEdgeToFind.equals(edge.getID())) {
                return edge;
            }
        }

        return null;
    }

    @Override
    public ConcurrentHashMap<String, Pair<Color, Set<UniquelyIdentifiable>>> getItemSelectionsMap() {
        return graphIDsToItemSelections;
    }

    @Override
    public void close() {
        connectionToServer.close();
        connectionToServer = null;
    }

    @Override
    public String getGraphID() {
        return id;
    }
}
