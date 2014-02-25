package JSci.instruments;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;


/** A mechanical device that can control the position of 
 * something. Implements the methods to notify changes.
 */
public abstract class PositionControlAdapter implements PositionControl {

    /** set the position
     * @param p the position
     */
    abstract public void setPosition(double p);

    /** get the position that must be reached. Since the mechanical
     * device needs some time to reach the position, getPosition()
     * can be different from getActualPosition()
     * @return the position that must be reached
     */
    abstract public double getPosition();

    /** get the actual position of the device. Since the mechanical
     * device needs some time to reach the position, getPosition()
     * can be different from getActualPosition()
     * @return the actual position
     */
    abstract public double getActualPosition();

    /** get the minimum position
     * @return the minimum position
     */
    abstract public double getMinimum();

    /** get the maximum position 
     * @return the maximum position
     */
    abstract public double getMaximum();

    /** sleeps for the time needed to stabilize the position. 
     */
    abstract public void sleep();

    /** get a Component through which we can control the position
     * @return the Component with the controls for the position
     */
    abstract public Component getControlComponent();

    /** get a String with the description of the units used
     * for all the values.
     * @return the unit
     */
    abstract public String getUnit();
    


    //////////////////////////////////////////////////////////////////
    // Events

    /**
     * Only one ChangeEvent is needed per model instance since the
     * event's only (read-only) state is the source property.  The source
     * of events generated here is always "this".
     */
    private transient ChangeEvent changeEvent = null;

    /**
     * The list of ChangeListeners for this model.  Subclasses may
     * store their own listeners here.
     */
    protected EventListenerList listenerList = new EventListenerList();


    /**
     * Adds a ChangeListener to the model's listener list.  The 
     * ChangeListeners must be notified when the models value changes.
     *
     * @param l the ChangeListener to add
     * @see #removeChangeListener
     */
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }
    

    /**
     * Removes a ChangeListener from the model's listener list.
     *
     * @param l the ChangeListener to remove
     * @see #addChangeListener
     */
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }


    /**
     * Returns an array of all the <code>ChangeListener</code>s added
     * to this model with addChangeListener().
     *
     * @return all of the <code>ChangeListener</code>s added or an empty
     *         array if no listeners have been added
     */
    public ChangeListener[] getChangeListeners() {
        return (ChangeListener[])listenerList.getListeners(
							   ChangeListener.class);
    }


    /** 
     * Run each ChangeListeners stateChanged() method.  
     * 
     * @see EventListenerList
     */
    protected void fireStateChanged() 
    {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -=2 ) {
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
            }          
        }
    }   


    /**
     * Return an array of all the listeners of the given type that 
     * were added to this model.
     *
     * @param listenerType the type of listeners to return, e.g. ChangeListener.class
     * @return all of the objects receiving <em>listenerType</em> notifications 
     *         from this model
     */
    public EventListener[] getListeners(Class listenerType) { 
	return listenerList.getListeners(listenerType); 
    }



}

