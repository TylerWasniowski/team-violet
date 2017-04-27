package com.horstmann.violet.commands;

import com.horstmann.violet.framework.Edge;
import com.horstmann.violet.framework.Graph;
import com.horstmann.violet.graphs.TeamSequenceDiagramGraph;

import java.awt.geom.Point2D;

/**
 * Created by Tyler on 4/25/2017.
 */
public class ConnectEdgeCommand implements Command {

    private static final long serialVersionUID = 8094178111891105571L;

    private Edge edge;
    private Point2D point1;
    private Point2D point2;

    public ConnectEdgeCommand(Edge edge, Point2D point1, Point2D point2) {
        this.edge = edge;
        this.point1 = point1;
        this.point2 = point2;
    }

    @Override
    public boolean execute(TeamSequenceDiagramGraph graphToExecuteCommandOn) {
        return graphToExecuteCommandOn.connectLocal(edge, point1, point2);
    }
}
