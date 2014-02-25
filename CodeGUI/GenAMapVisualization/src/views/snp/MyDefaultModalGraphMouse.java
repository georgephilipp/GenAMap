package views.snp;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * The JUNG package does not give us access to the picking mode of the Graph
 * Mouse.  We want to be able to edit the graph mouse so that the SNP view
 * is a set view.
 *
 * In order to do this, we add the functionality in a new class.
 * @author rcurtis
 */
public class MyDefaultModalGraphMouse<V, E> extends DefaultModalGraphMouse
{

    protected boolean isLocked = false;
    private int x;
    private boolean dragging = false;
    private ChromosomeView cv;
    public boolean isInWait = false;

    public MyDefaultModalGraphMouse(ChromosomeView cv)
    {
        this.cv = cv;
    }

    public void lockPickingMode()
    {
        ((PickingGraphMousePlugin) this.pickingPlugin).setLocked(true);
        this.isLocked = true;
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        VisualizationViewer<Integer, Number> vv = (VisualizationViewer<Integer, Number>) e.getComponent();
        Layout<Integer, Number> lay = vv.getGraphLayout();
        if (vv.getPickSupport().getVertex(lay, e.getX(), e.getY()) != null)
        {
            x = e.getX();
            dragging = true;
        }
        if (isLocked)
        {
            int i = e.getModifiers();
            if (i == 18)
            {
                return;
            }
        }
        super.mousePressed(e);
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        //System.out.print("dragging\n");
        if (dragging)
        {
            int dif = x - e.getX();
            x = e.getX();
            cv.shift(dif);

        }
        super.mouseDragged(e);
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        dragging = false;
        super.mouseReleased(e);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        if (e.getWheelRotation() < 0)
        {
            cv.zoomIn();
        }
        else
        {
            cv.zoomOut();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        VisualizationViewer<Integer, Number> vv = e == null ? cv.getVisualizationViewer() : (VisualizationViewer<Integer, Number>) e.getComponent();
        if (isInWait)
        {
            vv.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
        else
        {
            vv.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }
    }
}
