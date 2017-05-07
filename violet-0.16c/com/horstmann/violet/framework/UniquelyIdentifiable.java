package com.horstmann.violet.framework;

import java.io.Serializable;

/**
 * Created by Rooke_000 on 4/29/2017.
 */
public interface UniquelyIdentifiable extends Cloneable, Serializable {

    /**
     * Gets the ID of this edge.
     * @return the ID of this edge.
     */
    String getID();

    /**
     * Gets the ID of the graph that this object belongs to
     * @return the ID of the graph that this object belongs to
     */
    String getClientID();

    /**
     * Assigns the graphID of this edge.
     * @param graphID the ID of the graph that this edge belongs to
     */
    void assignClientID(String graphID);

    Object clone() throws CloneNotSupportedException;

}
