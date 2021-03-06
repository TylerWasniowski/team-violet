package com.horstmann.violet.framework;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Rooke_000 on 4/29/2017.
 */
public abstract class AbstractUniquelyIdentifiable implements UniquelyIdentifiable {

    private static final long serialVersionUID = 7001529765569933224L;

    // These are for the syncing. We need to be able to identify unique objects.
    private String id;
    private String graphID;

    // Counts number of objects of each class
    private static Map<String, Integer> classNameToNumberOfObjects = new HashMap<>();
    /**
     * Increments the objects count in the Map and initializes the clientID to the empty string
     */
    public AbstractUniquelyIdentifiable() {
        incrementCountInMap();
        assignClientID("");
    }

    /**
     * Increments the value linked to the class name, or initializes the value to 1 if the value linked to the
     * class name was 0.
     * @return The new number linked to the class name
     */
    private Integer incrementCountInMap() {
        Integer numberOfObjectsOfThisClass = classNameToNumberOfObjects.get(this.getClass().toString());
        if (numberOfObjectsOfThisClass == null) {
            classNameToNumberOfObjects.put(this.getClass().toString(), 1);
        } else {
            classNameToNumberOfObjects.put(this.getClass().toString(), numberOfObjectsOfThisClass + 1);
        }

        return numberOfObjectsOfThisClass;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AbstractUniquelyIdentifiable))
            return false;

        AbstractUniquelyIdentifiable that = (AbstractUniquelyIdentifiable) o;

        return this.id.equals(that.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        try {
            AbstractUniquelyIdentifiable cloned = (AbstractUniquelyIdentifiable) super.clone();
            incrementCountInMap();
            cloned.assignClientID(this.graphID);
            return cloned;
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public String getClientID() {
        return this.graphID;
    }

    @Override
    public void assignClientID(String graphID) {
        if (graphID == null)
            this.graphID = "";
        else
            this.graphID = graphID;

        this.id = this.graphID + this.getClass().toString() + classNameToNumberOfObjects.get(this.getClass().toString());
    }

}
