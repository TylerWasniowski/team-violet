package com.horstmann.violet.graphs;

import com.horstmann.violet.commands.Command;

/**
 * Created by Tyler on 4/27/2017.
 */
public interface TeamDiagram {

    /**
     * Sends a command to a server.
     * @param command the command to send to the server
     * @return true if the command is successfully sent, false if not
     */
    public boolean sendCommandToServer(Command command);

}
