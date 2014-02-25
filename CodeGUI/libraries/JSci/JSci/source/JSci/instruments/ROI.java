package JSci.instruments;

import java.awt.*;
import java.awt.event.*;

/** A Region Of Interest, that is a region delimited by a polygon,
that can be moved with the mouse.
*/

public interface ROI {

    /** @return the Shape of the ROI */
    Shape getShape();

    /** Called by the Player: set the component where to display the ROI
     * @param c The Component where to display the ROI 
    */
    public void setComponent(Component c);

    /** Called by the Player: draw the ROI
     * @param g the Graphics where to paint the ROI 
    */
    public void paint(Graphics g);
    
}
