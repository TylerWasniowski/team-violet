package com.horstmann.violet.framework;

import java.io.Serializable;

/**
 * Created by Tyler on 4/15/2017.
 */
public interface Command extends Serializable {

    /**
     * If the current diagram is specified to be a team diagram, then it sends this Command to the server.
     * Executes the command saved in task.
     */
    default public void execute() {
        /*
         * Pseudo code:
         * if (diagram == teamDiagram) {
         *  sendCommandToServer();
         * }
         */

        task();
    }

    public void task();

}
