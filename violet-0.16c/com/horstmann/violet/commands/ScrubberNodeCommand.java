package com.horstmann.violet.commands;

import com.horstmann.violet.framework.Node;
import com.horstmann.violet.graphs.TeamDiagram;

/**
 * Created by CSingh on 5/5/2017.
 */
public class ScrubberNodeCommand implements Command {
    
    private static final long serialVersionUID = -5173365451099251363L;
    private String idOfNodeToSelect;
    private String clientGraphId;
    
    public ScrubberNodeCommand(String cGraphId, String nId) {
        this.idOfNodeToSelect = nId;
        this.clientGraphId = cGraphId;
    }

    @Override
    public boolean execute(TeamDiagram graphToExecuteCommandOn) {
        Node nodeToSelect = graphToExecuteCommandOn.findNodeFromID(idOfNodeToSelect);
        if (nodeToSelect == null)
            return false;
        
        return graphToExecuteCommandOn.addToConnectedClientsMap(clientGraphId, nodeToSelect);
    }
}