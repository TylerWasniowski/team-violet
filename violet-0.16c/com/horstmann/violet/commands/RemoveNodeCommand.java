package com.horstmann.violet.commands;

import com.horstmann.violet.framework.Graph;
import com.horstmann.violet.framework.Node;
import com.horstmann.violet.graphs.TeamSequenceDiagramGraph;

/**
 * Created by Tyler on 4/26/2017.
 */
public class RemoveNodeCommand implements Command {

    private Node node;

    public RemoveNodeCommand(Node node) {
        this.node = node;
    }

    @Override
    public boolean execute(TeamSequenceDiagramGraph graphToExecuteCommandOn) {
        graphToExecuteCommandOn.removeNodeLocal(node);
        return !graphToExecuteCommandOn.getNodes().contains(node);
    }
}
