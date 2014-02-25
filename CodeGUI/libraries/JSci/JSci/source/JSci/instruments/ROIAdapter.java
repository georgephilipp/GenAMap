package JSci.instruments;

import java.awt.*;
import java.awt.event.*;

/** A Region Of Interest class adapter
*/

public abstract class ROIAdapter extends MouseMotionAdapter implements ROI, MouseListener, MouseMotionListener {
    public abstract Shape getShape();
    public abstract void setComponent(Component c);
    public abstract void paint(Graphics g);
    public void mouseReleased(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
}
