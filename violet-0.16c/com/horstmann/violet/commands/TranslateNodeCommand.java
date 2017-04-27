package com.horstmann.violet.commands;

import com.horstmann.violet.framework.Node;
import com.horstmann.violet.graphs.TeamSequenceDiagramGraph;

import java.awt.geom.Point2D;

/**
 * Created by Tyler on 4/26/2017.
 */
public class TranslateNodeCommand implements Command {
    private static final long serialVersionUID = -5786588578050501402L;

    private Node nodeToTranslate;
    private Point2D newLocation;

    public TranslateNodeCommand(Node nodeToTranslate, Point2D newLocation) {
        this.nodeToTranslate = nodeToTranslate;
        this.newLocation = newLocation;
    }

    @Override
    public boolean execute(TeamSequenceDiagramGraph graphToExecuteCommandOn) {
        nodeToTranslate.setLocation(newLocation);

        return (nodeToTranslate.getBounds().getX() == newLocation.getX()
                && nodeToTranslate.getBounds().getY() == newLocation.getY());
    }
}
