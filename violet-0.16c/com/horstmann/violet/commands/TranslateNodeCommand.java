package com.horstmann.violet.commands;

import com.horstmann.violet.framework.Node;
import com.horstmann.violet.graphs.TeamSequenceDiagramGraph;

import java.awt.geom.Point2D;

/**
 * Created by Tyler on 4/26/2017.
 */
public class TranslateNodeCommand implements Command {
    private static final long serialVersionUID = -5786588578050501402L;

    private String idOfNodeToTranslate;
    private Point2D newLocation;

    public TranslateNodeCommand(String idOfNodeToTranslate, Point2D newLocation) {
        this.idOfNodeToTranslate = idOfNodeToTranslate;
        this.newLocation = newLocation;
    }

    @Override
    public boolean execute(TeamSequenceDiagramGraph graphToExecuteCommandOn) {

        Node nodeToTranslate = Command.findNodeFromID(graphToExecuteCommandOn, idOfNodeToTranslate);
        if (nodeToTranslate == null)
            return false;

        nodeToTranslate.setLocation(newLocation);
        return (nodeToTranslate.getBounds().getX() == newLocation.getX()
                && nodeToTranslate.getBounds().getY() == newLocation.getY());
    }
}
