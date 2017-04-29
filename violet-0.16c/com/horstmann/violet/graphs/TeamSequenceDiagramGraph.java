package com.horstmann.violet.graphs;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.*;

import com.horstmann.violet.commands.*;
import com.horstmann.violet.client.Publisher;
import com.horstmann.violet.client.Subscriber;
import com.horstmann.violet.commands.AddNodeCommand;
import com.horstmann.violet.framework.Edge;
import com.horstmann.violet.framework.Node;

import javax.jms.JMSException;

/**
 *
 *
 */
public class TeamSequenceDiagramGraph extends SequenceDiagramGraph implements TeamDiagram, AutoCloseable, Closeable {

    private static final long serialVersionUID = -9088160815514315525L;

    private String id;
    private transient Publisher publisher;
    private transient Subscriber subscriber;

    public TeamSequenceDiagramGraph() throws JMSException {
        super();

        id = UUID.randomUUID().toString();

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
        n.setGraphID(id);
        super.add(n, p);
        return sendCommandToServer(new AddNodeCommand(n, p));
    }

    @Override
    public void removeNode(Node n) {
        super.removeNode(n);
        sendCommandToServer(new RemoveNodeCommand(n.getID()));
    }

    @Override
    public boolean connect(Edge e, Point2D p1, Point2D p2) {
        if (!getEdges().contains(e)) {
            return super.connect(e, p1, p2);
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
    public boolean addLocal(Node n, Point2D p) {
        if (!getNodes().contains(n)) {
            return super.add(n, p);
        }

        return false;
    }

    public void removeNodeLocal(String idOfNodeToRemove) {
        for (Node node : getNodes()) {
            if (node.getID().equals(idOfNodeToRemove)) {
                super.removeNode(node);
                return;
            }
        }
    }

    public void removeEdgeLocal(String idOfEdgeToRemove) {
        Edge edgeToRemove = findEdgeFromID(idOfEdgeToRemove);
        if (edgeToRemove != null)
            super.removeEdge(edgeToRemove);
    }

    public Node findNodeFromID(String idOfNodeToFind) {
        for (Node node: getNodes()) {
            if (idOfNodeToFind.equals(node.getID())) {
                return node;
            }
        }

        return null;
    }

    public Edge findEdgeFromID(String idOfEdgeToFind) {
        for (Edge edge: getEdges()) {
            if (idOfEdgeToFind.equals(edge.getID())) {
                return edge;
            }
        }

        return null;
    }

    public void close() {
        publisher.closePublisherConnection();
        subscriber.closeSubscriberConnection();
    }

}
