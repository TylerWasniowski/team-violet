package com.horstmann.violet.commands;

import com.horstmann.violet.framework.Edge;
import com.horstmann.violet.framework.Node;
import com.horstmann.violet.framework.UniquelyIdentifiable;
import com.horstmann.violet.graphs.TeamDiagram;
import com.horstmann.violet.graphs.TeamSequenceDiagramGraph;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * Created by Rooke_000 on 4/29/2017.
 */
public class ChangePropertyCommand implements Command {

    private static final long serialVersionUID = 2361595660071833255L;

    private String idOfObject;
    private String nameOfPropertyInObject;
    private Object newProperty;
    /**
     * Creates a ChangePropertyCommand
     * @param idOfObject id of the object whose properties changed
     * @param nameOfPropertyInObject the name of property that got changed
     * @param newProperty the new property to write into the object
     */
    public ChangePropertyCommand(String idOfObject, String nameOfPropertyInObject, Object newProperty) {
        this.idOfObject = idOfObject;
        this.nameOfPropertyInObject = nameOfPropertyInObject;
        this.newProperty = newProperty;
    }

    @Override
    public boolean execute(TeamDiagram graphToExecuteCommandOn) {
        Object objectToChangePropertyOf;

        // Finds the object whose property was changed, and stores result into objectToChangePropertyOf
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
                    return true;
                }
            }
        } catch(IntrospectionException|IllegalAccessException|InvocationTargetException ex) {
            ex.printStackTrace();
            return false;
        }

        return false;
    }

}
