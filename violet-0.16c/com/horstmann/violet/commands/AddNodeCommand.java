package com.horstmann.violet.commands;

import com.horstmann.violet.framework.Graph;
import com.horstmann.violet.framework.Node;
import com.horstmann.violet.graphs.TeamSequenceDiagramGraph;

import java.awt.geom.Point2D;

/**
 * Created by Tyler on 4/25/2017.
 */
public class AddNodeCommand implements Command {

    private Node node;
    private Point2D point;

    public AddNodeCommand(Node node, Point2D point) {
        this.node = node;
        this.point = point;
    }

    @Override
    public boolean execute(TeamSequenceDiagramGraph graphToExecuteCommandOn) {
        return graphToExecuteCommandOn.addLocal(node, point);
    }

}
