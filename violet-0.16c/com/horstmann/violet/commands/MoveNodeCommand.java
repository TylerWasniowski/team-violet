package com.horstmann.violet.commands;

import com.horstmann.violet.framework.Node;
import com.horstmann.violet.graphs.TeamDiagram;
import com.horstmann.violet.graphs.TeamSequenceDiagramGraph;

import java.awt.geom.Point2D;

/**
 * Created by Tyler on 4/26/2017.
 */
public class MoveNodeCommand implements Command {
    private static final long serialVersionUID = -5786588578050501402L;

    private String idOfNodeToMove;
    private Point2D newLocation;
    /**
     * Creates a new MoveNodeCommand
     * @param idOfNodeToMove the id of the node to move
     * @param newLocation the location where the node is moved to
     */
    public MoveNodeCommand(String idOfNodeToMove, Point2D newLocation) {
        this.idOfNodeToMove = idOfNodeToMove;
        this.newLocation = newLocation;
    }

    @Override
    public boolean execute(TeamDiagram graphToExecuteCommandOn) {

        Node nodeToMove = graphToExecuteCommandOn.findNodeFromID(idOfNodeToMove);
        if (nodeToMove == null)
            return false;

        nodeToMove.setLocation(newLocation);
        return (nodeToMove.getBounds().getX() == newLocation.getX()
                && nodeToMove.getBounds().getY() == newLocation.getY());
    }
}
