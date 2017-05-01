package com.horstmann.violet.commands;

import com.horstmann.violet.graphs.TeamSequenceDiagramGraph;

/**
 * Created by Rooke_000 on 4/29/2017.
 */
public class ChangePropertyCommand implements Command {

    private String nodeToChangePropertyOf;
    private String propertyID;

    @Override
    public boolean execute(TeamSequenceDiagramGraph graphToExecuteCommandOn) {
        return false;
    }

}
