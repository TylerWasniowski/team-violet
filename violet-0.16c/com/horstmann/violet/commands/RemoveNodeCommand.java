package com.horstmann.violet.commands;

import com.horstmann.violet.framework.Graph;
import com.horstmann.violet.framework.Node;
import com.horstmann.violet.graphs.TeamDiagram;
import com.horstmann.violet.graphs.TeamSequenceDiagramGraph;

/**
 * Created by Tyler on 4/26/2017.
 */
public class RemoveNodeCommand implements Command {

    private static final long serialVersionUID = -6116885171081680050L;

    private String idOfNodeToRemove;
    /**
     * Creates a new RemoveNodeCommand
     * @param idOfNodeToRemove id of node to remove
     */
    public RemoveNodeCommand(String idOfNodeToRemove) {
        this.idOfNodeToRemove = idOfNodeToRemove;
    }

    @Override
    public boolean execute(TeamDiagram graphToExecuteCommandOn) {
        graphToExecuteCommandOn.removeNodeLocal(idOfNodeToRemove);
        return graphToExecuteCommandOn.findNodeFromID(idOfNodeToRemove) != null;
    }
}
