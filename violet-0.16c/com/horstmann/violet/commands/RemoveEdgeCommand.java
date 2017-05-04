package com.horstmann.violet.commands;

import com.horstmann.violet.framework.Edge;
import com.horstmann.violet.framework.Graph;
import com.horstmann.violet.graphs.TeamDiagram;
import com.horstmann.violet.graphs.TeamSequenceDiagramGraph;

/**
 * Created by Tyler on 4/26/2017.
 */
public class RemoveEdgeCommand implements Command {

    private static final long serialVersionUID = 5533608612833602004L;

    private String idOfEdgeToRemove;

    public RemoveEdgeCommand(String idOfEdgeToRemove) {
        this.idOfEdgeToRemove = idOfEdgeToRemove;
    }

    @Override
    public boolean execute(TeamDiagram graphToExecuteCommandOn) {
        graphToExecuteCommandOn.removeEdgeLocal(idOfEdgeToRemove);
        return graphToExecuteCommandOn.findEdgeFromID(idOfEdgeToRemove) == null;
    }
}
