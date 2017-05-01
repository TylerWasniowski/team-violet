package com.horstmann.violet.commands;

import com.horstmann.violet.framework.Edge;
import com.horstmann.violet.framework.Node;
import com.horstmann.violet.framework.UniquelyIdentifiable;
import com.horstmann.violet.graphs.TeamSequenceDiagramGraph;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Rooke_000 on 4/29/2017.
 */
public class ChangePropertyCommand implements Command {

    private String idOfObject;
    private String nameOfPropertyInObject;
    private Object newProperty;

    public ChangePropertyCommand(String idOfObject, String nameOfPropertyInObject, Object newProperty) {
        this.idOfObject = idOfObject;
        this.nameOfPropertyInObject = nameOfPropertyInObject;
        this.newProperty = newProperty;
    }

    @Override
    public boolean execute(TeamSequenceDiagramGraph graphToExecuteCommandOn) {
        Object objectToChangePropertyOf;

        Node nodeWithGivenID = graphToExecuteCommandOn.findNodeFromID(idOfObject);
        if (nodeWithGivenID == null) {
            Edge edgeWithGivenID = graphToExecuteCommandOn.findEdgeFromID(idOfObject);
            if (edgeWithGivenID == null)
                return false;
            else
                objectToChangePropertyOf = edgeWithGivenID;

        } else {
            objectToChangePropertyOf = nodeWithGivenID;
        }

        try {
            // Look at all the properties in the object, and find the one with the right name,
                // then invoke the associated write method on the object with the new property.
            for (PropertyDescriptor propertyDescriptor :
                    Introspector.getBeanInfo(objectToChangePropertyOf.getClass()).getPropertyDescriptors()) {
                if (propertyDescriptor.getName().equals(nameOfPropertyInObject)) {
                    propertyDescriptor.getWriteMethod().invoke(objectToChangePropertyOf, newProperty);
                }
            }
        } catch(IntrospectionException|IllegalAccessException|InvocationTargetException ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

}
