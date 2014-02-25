package JSci.instruments;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

/** Describes a mechanical device that can control the position of 
 * something
 */
public interface PositionControl extends Control {

    /** set the position
     * @param p the position
     */
    void setPosition(double p);

    /** get the position that must be reached. Since the mechanical
     * device needs some time to reach the position, getPosition()
     * can be different from getActualPosition()
     * @return the position that must be reached
     */
    double getPosition();

    /** get the actual position of the device. Since the mechanical
     * device needs some time to reach the position, getPosition()
     * can be different from getActualPosition()
     * @return the actual position
     */
    double getActualPosition();

    /** get the minimum position
     * @return the minimum position
     */
    double getMinimum();

    /** get the maximum position 
     * @return the maximum position
     */
    double getMaximum();

    /** sleeps for the time needed to stabilize the position. 
     */
    void sleep();

    /** get a Component through which we can control the position
     * @return the Component with the controls for the position
     */
    Component getControlComponent();

    /** get a String with the description of the units used
     * for all the values.
     * @return the unit
     */
    String getUnit();
    


    //////////////////////////////////////////////////////////////////
    // Events
   
    /** Adds a ChangeListener to the model's listener list.  The
     * ChangeListeners must be notified when the models value changes.
     * @param l the ChangeListener to add
     * @see #removeChangeListener
     */
    void addChangeListener(ChangeListener l);

    /** Removes a ChangeListener from the model's listener list.
     * @param l the ChangeListener to remove
     * @see #addChangeListener
     */
    void removeChangeListener(ChangeListener l);

    /** Returns an array of all the <code>ChangeListener</code>s added
     * to this PositionControl with addChangeListener().
     * @return all of the <code>ChangeListener</code>s added or an empty
     *         array if no listeners have been added
     */
    ChangeListener[] getChangeListeners();

    /** Return an array of all the listeners of the given type that
     * were added to this PositionControl.  For example to find all of the
     * ChangeListeners added to this model:
     * @param listenerType the type of listeners to return, e.g. ChangeListener.class
     * @return all of the objects receiving <em>listenerType</em> notifications
     *         from this model
     */
    EventListener[] getListeners(Class listenerType);


}

