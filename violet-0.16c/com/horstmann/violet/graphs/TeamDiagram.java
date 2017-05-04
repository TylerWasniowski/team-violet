package com.horstmann.violet.graphs;

import com.horstmann.violet.commands.Command;
import com.horstmann.violet.framework.Edge;
import com.horstmann.violet.framework.GraphPanel;
import com.horstmann.violet.framework.Node;

import javax.swing.*;
import java.awt.geom.Point2D;

/**
 * All diagrams should implement this interface if they are to be synced with other clients.
 */
public interface TeamDiagram {

    /**
     * Sends a command to a server.
     * @param command the command to send to the server
     * @return true if the command is successfully sent, false if not
     */
    public boolean sendCommandToServer(Command command);

    /**
     * Finds the node with the given ID.
     * @param idOfNodeToFind the id of the node to find
     * @return the node with the given ID, or null if no such edge was found
     */
    public Node findNodeFromID(String idOfNodeToFind);

    /**
     * Finds the edge with the given ID.
     * @param idOfEdgeToFind the id of the edge to find
     * @return the edge with the given ID, or null if no such edge was found
     */
    public Edge findEdgeFromID(String idOfEdgeToFind);

    /**
     * Returns the Panel that is viewing this diagram. This allows the view to be repainted.
     * @return the Panel that is viewing this diagram.
     */
    public JPanel getPanel();

    /**
     * Adds the given node on this diagram at the given location,
     * AND sends the appropriate command to the server to do the same.
     * @param node the node to add
     * @param point the point to add the node to
     * @return true if it succeeds in LOCALLY adding the node
     */
    public boolean add(Node node, Point2D point);

    /**
     * Removes the given node from this diagram, AND sends the appropriate command to the server to do the same.
     * @param node the node to remove
     */
    public void removeNode(Node node);

    /**
     * Tries to connect the given edge from the node at the given start point to the node at the given end point,
     * AND if the connection succeeds, it sends the appropriate command to the server to do the same.
     * @param edge the edge to connect
     * @param startPoint the location of the node that the edge should start from
     * @param endPoint the location of the node that the edge should end from
     * @return true if the connection succeeded, false otherwise
     */
    public boolean connect(Edge edge, Point2D startPoint, Point2D endPoint);

    /**
     * Removes the given edge, AND sends the appropriate command to the server to do the same.
     * @param edge the edge to remove
     */
    public void removeEdge(Edge edge);

    /**
     * Adds the given node on this diagram at the given location WITHOUT sending the command to the server.
     * This is ONLY used when consuming an add node command.
     * @param node the node to add
     * @param point the point to add the node to
     * @return true if it succeeds in adding the node, false otherwise
     */
    public boolean addLocal(Node node, Point2D point);

    /**
     * Removes the node on this diagram with the given ID WITHOUT sending the command to the server.
     * This is ONLY used when consuming a remove node command.
     * @param idOfNodeToRemove the id of the node to remove.
     */
    public void removeNodeLocal(String idOfNodeToRemove);

    /**
     * Tries to connect the given edge from the node at the given start point to the node at the given end point
     * WITHOUT sending the command to the server. This is ONLY used when consuming a connect command.
     * @param edge the edge to connect
     * @param startPoint the location of the node that the edge should start from
     * @param endPoint the location of the node that the edge should end from
     * @return true if the connection succeeded, false otherwise
     */
    public boolean connectLocal(Edge edge, Point2D startPoint, Point2D endPoint);

    /**
     * Removes the node on this diagram with the given ID WITHOUT sending the command to the server.
     * This is ONLY used when consuming a remove edge command.
     * @param idOfNodeToRemove the id of the node to remove.
     */
    public void removeEdgeLocal(String idOfNodeToRemove);

    /**
     Causes the layout of the graph to be recomputed.
     */
    public void layout();

}
