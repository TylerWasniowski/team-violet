package com.horstmann.violet.graphs;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import com.horstmann.violet.commands.*;
import com.horstmann.violet.client.Publisher;
import com.horstmann.violet.client.Subscriber;
import com.horstmann.violet.commands.AddNodeCommand;
import com.horstmann.violet.framework.Edge;
import com.horstmann.violet.framework.Node;

import javax.jms.JMSException;

/**
 * A SequenceDiagram that stays synced with other clients connected to the same project.
 */
public class TeamSequenceDiagramGraph extends SequenceDiagramGraph implements TeamDiagram, AutoCloseable, Closeable {

    private static final long serialVersionUID = -9088160815514315525L;

    // A unique id for this graph, used when figuring out what graph added what object to the synced diagram
    private String id;
    private String hostname;

    // The objects that communicate with the server.
    private transient Publisher publisher;
    private transient Subscriber subscriber;

    public TeamSequenceDiagramGraph() throws JMSException {
        super();

        id = UUID.randomUUID().toString();
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostname = id;
        }

        publisher = new Publisher();
        subscriber = new Subscriber(this);

        try {
            publisher.start();
            subscriber.start();
        } catch (JMSException ex) {
            publisher.closePublisherConnection();
            subscriber.closeSubscriberConnection();
            throw ex;
        }

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
            publisher.sendCommand(command);
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
    public void close() {
        publisher.closePublisherConnection();
        subscriber.closeSubscriberConnection();
    }

    /**
     * gets the hostname.
     */
    @Override
    public String getHostname() {
        return hostname;
    }

    /**
     * Adds to map that the graph id and node key value.
     * @param gId graph id
     * @param n the node from the graph
     * @return true if key is added and then found to be contained, false otherwise
     */
    @Override
    public boolean addToConnectedClientsMap(String clientGraphId, Node node) {
        return super.addToConnectedClientsToNode(clientGraphId, node);
    }

    /**
     * gets the graph id.
     */
    @Override
    public String getGraphId() {
        return id;
    }
}
