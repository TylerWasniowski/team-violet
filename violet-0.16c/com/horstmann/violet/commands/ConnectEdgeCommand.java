package com.horstmann.violet.commands;

import com.horstmann.violet.framework.Edge;
import com.horstmann.violet.framework.Graph;
import com.horstmann.violet.framework.Node;
import com.horstmann.violet.graphs.TeamSequenceDiagramGraph;

import java.awt.geom.Point2D;

/**
 * Created by Tyler on 4/25/2017.
 */
public class ConnectEdgeCommand implements Command {

    private static final long serialVersionUID = 8094178111891105571L;

    private Edge edge;
    private Node start;
    private Node end;

    public ConnectEdgeCommand(Edge edge, Node start, Node end) {
        this.edge = edge;
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean execute(TeamSequenceDiagramGraph graphToExecuteCommandOn) {
        return graphToExecuteCommandOn.connectLocal(edge, start, end);
    }
}
