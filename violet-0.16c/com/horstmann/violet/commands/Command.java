package com.horstmann.violet.commands;

import com.horstmann.violet.framework.Graph;
import com.horstmann.violet.graphs.TeamSequenceDiagramGraph;

import java.io.Serializable;

/**
 * Created by Tyler on 4/25/2017.
 */
public interface Command extends Serializable {

    final long serialVersionUID = 5L;

    /**
     * Carries out the command
     * @return true if command was executed, false if it wasn't executed
     */
    public boolean execute(TeamSequenceDiagramGraph graphToExecuteCommandOn);

}
