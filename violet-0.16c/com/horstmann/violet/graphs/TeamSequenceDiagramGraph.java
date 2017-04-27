package com.horstmann.violet.graphs;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.io.ByteArrayOutputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import com.horstmann.violet.commands.*;
import com.horstmann.violet.client.Publisher;
import com.horstmann.violet.client.Subscriber;
import com.horstmann.violet.commands.AddNodeCommand;
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
public class TeamSequenceDiagramGraph extends SequenceDiagramGraph implements Closeable {

    private static final long serialVersionUID = -9088160815514315525L;

    private static String id = "0";
    private transient Publisher publisher;
    private transient Subscriber subscriber;

    public TeamSequenceDiagramGraph() throws JMSException {
        super();

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
//        super.add(n, p);
        return sendCommandToServer(new AddNodeCommand(n, p));
    }

    @Override
    public void removeNode(Node n) {
//        super.removeNode(n);
        sendCommandToServer(new RemoveNodeCommand(n.getID()));
    }

    @Override
    public boolean connect(Edge e, Point2D p1, Point2D p2) {
        e.setGraphID(id);
//        super.connect(e, p1, p2);
        return sendCommandToServer(new ConnectEdgeCommand(e, p1, p2));
    }

    @Override
    public void removeEdge(Edge e) {
//        super.removeEdge(e);
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

    public boolean connectLocal(Edge e, Point2D p1, Point2D p2) {
        if (!getEdges().contains(e)) {
            return super.connect(e, p1, p2);
        }

        return false;
    }

    public void removeEdgeLocal(String idOfEdgeToRemove) {
        for (Edge edge : getEdges()) {
            if (edge.getID().equals(idOfEdgeToRemove)) {
                super.removeEdge(edge);
                return;
            }
        }
    }

    public void close() {
        publisher.closePublisherConnection();
        subscriber.closeSubscriberConnection();
    }

}
