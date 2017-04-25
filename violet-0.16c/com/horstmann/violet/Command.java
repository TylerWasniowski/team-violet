package com.horstmann.violet;


import com.horstmann.violet.graphs.TeamSequenceDiagramGraph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Command implements Serializable {

    private TeamSequenceDiagramGraph.CommandType commandType;
    private List<Object> commandInputs;
    
    private static final long serialVersionUID = 42L;

    public Command(TeamSequenceDiagramGraph.CommandType commandType, Object... commandInputs) {
        this.commandType = commandType;

        this.commandInputs = new ArrayList<>();
        this.commandInputs.addAll(Arrays.asList(commandInputs));
    }

    public TeamSequenceDiagramGraph.CommandType getCommandType() {
        return commandType;
    }

    public List<Object> getCommandInputs() {
        return commandInputs;
    }

    @Override
    public String toString() {
        String ret = "";
        ret += "com.horstmann.violet.commands.Command Type: " + commandType + "\n";
        ret += "com.horstmann.violet.commands.Command Inputs: " + commandInputs;
        return ret;
    }

}