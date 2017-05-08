package com.horstmann.violet.commands;

import com.horstmann.violet.framework.Graph;
import com.horstmann.violet.framework.Node;
import com.horstmann.violet.graphs.TeamDiagram;
import com.horstmann.violet.graphs.TeamSequenceDiagramGraph;

import java.awt.geom.Point2D;

/**
 * Created by Tyler on 4/25/2017.
 */
public class AddNodeCommand implements Command {

    private static final long serialVersionUID = 7311600224150423820L;

    private Node node;
    private Point2D point;
    /**
     * Creates an AddNodeCommand that consists of the node and it's position.
     * @param node the node to add
     * @param point the position of the node
     */
    public AddNodeCommand(Node node, Point2D point) {
        this.node = node;
        this.point = point;
    }

    @Override
    public boolean execute(TeamDiagram graphToExecuteCommandOn) {
        return graphToExecuteCommandOn.addLocal(node, point);
    }

}
