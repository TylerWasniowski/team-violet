package com.horstmann.violet.commands;

/**
 * Created by Tyler on 4/25/2017.
 */
public interface Command {

    /**
     * Carries out the command
     * @return true if command was executed, false if it wasn't executed
     */
    public boolean execute();

}
