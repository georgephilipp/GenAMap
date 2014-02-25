package JSci.swing;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

/** A pointer for the JRoundDial or JArcDial */ 
public class JPointer {

    private double s;
    private int type;
    private Shape shape = null;
    private Color color = Color.BLACK;
    private boolean enabled = true;
    private boolean adjusting = false;


    /** 
     * @param t the type of the pointer
     */
    public JPointer(int t) {
	s=0.0;
	type=t;
    }

	
    /** get the value of the pointer
     * @return the value
     */
    public double getValue() {
	return s;
    }

    /** set the value of the pointer
     * @param v the value
     */
    public void setValue(double v) {
	if (s==v) return;
	s=v;
	if (!adjusting) fireStateChanged();
    }

    /** enable or disable the motion by the mouse
     * @param b should the mouse move the pointer?
     */
    public void setEnabled(boolean b) {
	enabled=b;
    }

    /** If you are adjusting the value, the changes should not be fired 
     * (dispatched) to the listeners. Typically, when the movement is done by
     * the mouse, only releasing the button actually fires the change.
     * If appropriate, this fires the state changed.
     * @param b are the following movements adjustments?
     */
    public void setAdjusting(boolean b) {
	if (adjusting && !b) fireStateChanged();
	adjusting=b;
    }


    /** set the color of the pointer
     * @param col the desired color
     */
    public void setColor(Color col) {
	color=col;
    }

    /** check if a point is inside the pointer image
     * @param p the point
     * @return is p inside the pointer image
     */
    public boolean contains(Point p) {
	return shape.contains(p) && enabled;
    }
	
    /** draw the pointer on the dial. Called by the dials.
     * @param g the graphics on which to draw
     * @param radius the radius of the dial
     * @param one the value of one turn
     * @param zero the angle (clockwise, starting from the top) at which the zero should 
     *	be placed
     * @param x0 horizontal coordinate of the center of the dial
     * @param y0 vertical coordinate of the center of the dial
     */
    protected void paintOnDial(Graphics2D g,double radius,double one,double zero,double x0,double y0) {
	AffineTransform at = AffineTransform.getTranslateInstance(x0,y0);
	at.scale(radius/1000.0,radius/1000.0);
	at.rotate(2.0*Math.PI*getValue()/one+zero);
	switch (type) {
	case POINTER_SIMPLE_TRIANGLE:
	    g.setColor(color);
	    shape=at.createTransformedShape(POINTER_SHAPE_SIMPLE_TRIANGLE);
	    g.fill(shape);
	    break;
	case POINTER_SIMPLE_QUADRANGLE:
	    g.setColor(color);
	    shape=at.createTransformedShape(POINTER_SHAPE_SIMPLE_QUADRANGLE);
	    g.fill(shape);
	    break;
	case POINTER_SIMPLE_STOP:
	    g.setColor(color);
	    shape=at.createTransformedShape(POINTER_SHAPE_SIMPLE_STOP);
	    g.fill(shape);
	    break;
	}
    }

    /** draw the slider on the dial. Called by the dials.
     * @param g the graphics on which to draw
     * @param start the value on the bottom of the JSliderPlus
     * @param end the value on the top of the JSliderPlus
     * @param width the width of the component
     * @param heigth the heigth of the component
     */
    protected void paintOnSlider(Graphics2D g,double start,double end,double width,double heigth) {
	AffineTransform at = AffineTransform.getTranslateInstance(
								  width/2.0,
								  heigth*(0.9-0.8*(s-start)/(end-start))
								  );
	at.scale(width/1000.0,heigth/2000.0);
	switch (type) {
	case SLIDER_SIMPLE_TRIANGLE:
	    g.setColor(color);
	    shape=at.createTransformedShape(SLIDER_SHAPE_SIMPLE_TRIANGLE);
	    g.fill(shape);
	    break;
	case SLIDER_SIMPLE_QUADRANGLE:
	    g.setColor(color);
	    shape=at.createTransformedShape(SLIDER_SHAPE_SIMPLE_QUADRANGLE);
	    g.fill(shape);
	    break;
	case SLIDER_SIMPLE_STOP:
	    g.setColor(color);
	    shape=at.createTransformedShape(SLIDER_SHAPE_SIMPLE_STOP);
	    g.fill(shape);
	    break;
	}
    }
    

    // EVENTS HANDLING

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
     * to this JPointer with addChangeListener().
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
     * @see #setValue
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
     * were added to this model.  For example to find all of the
     * ChangeListeners added to this model:
     * <pre>
     * myJPointer.getListeners(ChangeListener.class);
     * </pre>
     *
     * @param listenerType the type of listeners to return, e.g. ChangeListener.class
     * @return all of the objects receiving <em>listenerType</em> notifications 
     *         from this model
     */
    public EventListener[] getListeners(Class listenerType) { 
	return listenerList.getListeners(listenerType); 
    }



    // POINTER TYPES AND SHAPES


    public static final int POINTER_SIMPLE_TRIANGLE = 0;
    public static final int POINTER_SIMPLE_QUADRANGLE = 1;
    public static final int POINTER_SIMPLE_STOP = 2;


    private static final Shape POINTER_SHAPE_SIMPLE_TRIANGLE;
    private static final Shape POINTER_SHAPE_SIMPLE_QUADRANGLE;
    private static final Shape POINTER_SHAPE_SIMPLE_STOP;

	
    static {
	POINTER_SHAPE_SIMPLE_TRIANGLE = new Polygon(
						    new int[] {-100,100,00},
						    new int[] {100,100,-600},
						    3);
	POINTER_SHAPE_SIMPLE_QUADRANGLE = new Polygon(
						      new int[] {-100,0,100,00},
						      new int[] {0,100,0,-600},
						      4);
	int []x = new int[16];
	int []y = new int[16];
	for (int j=0;j<16;j++) {
	    x[j]=(int)(30*Math.cos(2.0*Math.PI*j/16));
	    y[j]=(int)(-595+30*Math.sin(2.0*Math.PI*j/16));
	}
	POINTER_SHAPE_SIMPLE_STOP = new Polygon(x,y,16);
    }


    // SLIDER TYPES AND SHAPES
    
    
    public static final int SLIDER_SIMPLE_TRIANGLE = 3;
    public static final int SLIDER_SIMPLE_QUADRANGLE = 4;
    public static final int SLIDER_SIMPLE_STOP = 5;


    private static final Shape SLIDER_SHAPE_SIMPLE_TRIANGLE;
    private static final Shape SLIDER_SHAPE_SIMPLE_QUADRANGLE;
    private static final Shape SLIDER_SHAPE_SIMPLE_STOP;

	
    static {
	SLIDER_SHAPE_SIMPLE_TRIANGLE = new Polygon(
					    new int[] {100,0,100},
					    new int[] {-100,0,100},
					    3);
	SLIDER_SHAPE_SIMPLE_QUADRANGLE = new Polygon(
					      new int[] {100,0,100,50},
					      new int[] {-100,0,100,0},
					      4);
	int []x = new int[16];
	int []y = new int[16];
	for (int j=0;j<16;j++) {
	    x[j]=(int)(200+30*Math.cos(2.0*Math.PI*j/16));
	    y[j]=(int)(30*Math.sin(2.0*Math.PI*j/16));
	}
	SLIDER_SHAPE_SIMPLE_STOP = new Polygon(x,y,16);
    }



}
