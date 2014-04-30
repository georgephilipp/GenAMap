package views.network.threeway;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

/**
 * I want to always show the value of the jslider as it moves along. I also
 * have it shown as a double ... even though the jslider interface only allows
 * for integers.
 * http://forums.oracle.com/forums/thread.jspa?threadID=1756101&start=45
 * @author rcurtis
 */
public class MyJSlider extends BasicSliderUI implements MouseMotionListener, MouseListener
{
    /**
     * We use a j popup menu to show the value as we scroll along
     */
    private final JPopupMenu pop;
    /**
     * This is the value in the menu
     */
    private final JMenuItem item;
    /**
     * The maximum value of the jslider
     */
    private double max;

    /**
     * Constructor
     */
    public MyJSlider(JSlider slider, double max)
    {
        super(slider);
        pop = new JPopupMenu();
        item = new JMenuItem();
        slider.addMouseListener(this);
        slider.addMouseMotionListener(this);
        this.max = max;
        pop.add(item);
    }

    public void setPopup(MouseEvent me)
    {
        item.setText(""+getValue());
        pop.show(me.getComponent(), me.getX()-30, me.getY());
    }

    public void mouseDragged(MouseEvent e)
    {
        setPopup(e);
    }

    public void mouseMoved(MouseEvent e)
    {
    }

    public void mouseClicked(MouseEvent e)
    {
    }

    public void mousePressed(MouseEvent me)
    {
        item.setText(""+getValue());
        pop.show(me.getComponent(), me.getX(), me.getY());
    }

    public void mouseReleased(MouseEvent e)
    {
        pop.setVisible(false);
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }

    /**
     * Determines what should be shown as the current value of the slider
     */
    public double getValue()
    {
        double val = (double)slider.getValue();
        val = val / 100;

        val = val * max;
        return val;
        //return Double.toString(val);
    }

    /**
     * Sets the value of the slider to represent the threshold given. 
     * @param d
     */
    public void setValue(double d)
    {
        int val = (int) (d / max * 100.0);
        slider.setValue(val);
    }
}
