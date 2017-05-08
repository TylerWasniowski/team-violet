package com.horstmann.violet.graphs;

import com.horstmann.violet.commands.Command;
import com.horstmann.violet.framework.*;
import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Closeable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * All diagrams should implement this interface if they are to be synced with other clients.
 */
public interface TeamDiagram extends Closeable, AutoCloseable{

    /**
     * Sends a command to a server.
     * @param command the command to send to the server
     * @return true if the command is successfully sent, false if not
     */
    public boolean sendCommandToServer(Command command);

    /**
     * Finds the item with the given ID.
     * @param idOfItemToFind the id of the item to find
     * @return the item with the given ID or null if item not found
     */
    public default UniquelyIdentifiable findItemFromID(String idOfItemToFind) {
        Node nodeWithGivenID = findNodeFromID(idOfItemToFind);

        if (nodeWithGivenID != null)
            return nodeWithGivenID;
        else
            return findEdgeFromID(idOfItemToFind);
    }

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
     * Gets the map of graphIDs mapped to the items that graph has selected.
     * @return the map of graphIDs mapped to the items that graph has selected
     */
    public ConcurrentHashMap<String, Pair<Color, Set<UniquelyIdentifiable>>> getItemSelectionsMap();

    /**
     * Maps the given graphID to the given set of items that graph is selecting.
     * @param graphID the ID of the graph selecting the given items
     * @param selectedItems the items the graph with the given graphID is selecting
     */
    public default void putItemSelections(String graphID, Set<UniquelyIdentifiable> selectedItems) {
        Pair<Color, Set<UniquelyIdentifiable>> colorItemSelectionsPair = getItemSelectionsMap().get(graphID);
        if (colorItemSelectionsPair != null) {
            // Given graphID has a color assigned to it, don't change it
            getItemSelectionsMap().put(graphID, new Pair<>(colorItemSelectionsPair.getKey(), selectedItems));
        } else {
            synchronized (getItemSelectionsMap()) {
                // This graphID has not given a color to the given graphID yet, make one
                // Use the number of graphIDs in the map as the seed for the hue
                Color color = Color.getHSBColor((((float) getItemSelectionsMap().size() % 9) / 8f), 1f, 0.55f);
                getItemSelectionsMap().put(graphID, new Pair<>(color, selectedItems));
            }
        }
    }

    /**
     * Returns the Panel that is viewing this diagram. This allows the view to be repainted.
     * @return the Panel that is viewing this diagram.
     */
    public JPanel getJPanel();

    /**
     * Adds the given node on this diagram at the given location, attaches this graphID to the node's ID
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
     * attaches this graphID to the given edge's id, AND if the connection succeeds, it sends the
     * appropriate command to the server to do the same.
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

    /**
     * gets the graph id
     * @return the graph id
     */
    public String getClientID();

}
