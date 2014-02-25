package views.network;

import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import java.awt.event.MouseWheelEvent;

/**
 * We don't want to be able to zoom in and out or move the visualization around.
 * It seems that there is a bug with JUNG that loses track of where the vertices
 * are, and then we can't query the internet for the traits.
 *
 * This method allows us to intercept the wheel movement so that we
 * don't have the auto-zooming properties of JUNG. We otherwise diable
 * the transforming and picking modes. 
 * @author RCurtis
 */
public class AntiScrollGraphMouseListener extends DefaultModalGraphMouse
{
    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        //do nothing
    }

}
