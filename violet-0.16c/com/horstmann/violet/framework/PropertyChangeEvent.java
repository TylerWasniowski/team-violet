package com.horstmann.violet.framework;

import javax.swing.event.ChangeEvent;

/**
 * Created by Rooke_000 on 4/30/2017.
 */
public class PropertyChangeEvent extends ChangeEvent {

    private static final long serialVersionUID = 3068294419021884021L;
    
    private String propertyName;

    /**
     * Constructs a ChangeEvent object.
     *
     * @param propertyName the name of the property that was changed
     * @param source the Object that is the source of the event
     *               (typically <code>this</code>)
     */
    public PropertyChangeEvent(String propertyName, Object source) {
        super(source);
        this.propertyName = propertyName;
    }
    /**
     * Gets the property name
     * @return the property name
     */
    public String getPropertyName() {
        return propertyName;
    }

}
