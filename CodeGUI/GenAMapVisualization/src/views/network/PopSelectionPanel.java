package views.network;

import datamodel.Model;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import views.Visualization;

/**
 * In the tree and network views, we will want to select only one population
 * to look at a time ... or we will want to be able to see all of the populations
 * together.
 * 
 * @author rcurtis
 */
public class PopSelectionPanel extends JPanel implements MouseListener
{
    /**
     * Pointers to all the labels in this view
     */
    private JLabel[] labels;
    /**
     * The label representing all of the populations
     */
    private JLabel all;
    /**
     * A pointer to the visualization object to call on events
     */
    private Visualization v;

    /**
     * Creates a PopSelectionPanel and sets up all the labels for viewing.
     * It also instantiates all the actions. This is the only method call to
     * this class. 
     * @param noPops the number of populations in the visualzation
     * @param v the visualization object to call when changes are made. 
     * @param background the background color for the JPanel
     */
    public PopSelectionPanel(int noPops, Visualization v, Color background, boolean isVerticle)
    {
        if (isVerticle)
        {
            this.setLayout(new GridLayout(noPops + 1, 1));
        }
        else
        {
            this.setLayout(new GridLayout(1, noPops + 1));
        }
        this.setBackground(background);

        labels = new JLabel[10];
        this.v = v;

        for (int i = 0; i < noPops; i++)
        {
            JLabel l = new JLabel("Pop #" + (i + 1) + "     ");
            l.setForeground(Model.getInstance().colors[i]);
            l.setFont(new Font("Serif", Font.PLAIN, 12));
            l.addMouseListener(this);

            labels[i] = l;
            this.add(l);
        }

        all = new JLabel("all");
        all.setFont(new Font("Serif", Font.BOLD, 16));
        all.addMouseListener(this);
        this.add(all);

    }

    /**
     * Ensures that the display is correctly matching what is shown when
     * the screen just fires up. 
     */
    public void setToDefautMode()
    {
        for(int i = 0; i < 10; i ++)
        {
            if(labels[i] != null)
            {
                labels[i].setFont(new Font("Serif", Font.PLAIN, 12));
            }
        }
        all.setFont(new Font("Serif", Font.BOLD, 16));
    }

    /**
     * To capture when the user clicks on the JLabel. We find out who it is
     * and then call the visualization accordingly
     * @param e
     */
    public void mouseClicked(MouseEvent e)
    {
        for (int i = 0; i < 10; i++)
        {
            if (e.getComponent().equals(labels[i]))
            {
                labels[i].setFont(new Font("Serif", Font.BOLD, 16));
                v.setPop(i+1);
            }
            else if(labels[i] != null)
            {
                labels[i].setFont(new Font("Serif", Font.PLAIN, 12));
            }
        }
        if(e.getComponent().equals(all))
        {
            all.setFont(new Font("Serif", Font.BOLD, 16));
            v.setPop(-1);
        }
        else
        {
            all.setFont(new Font("Serif", Font.PLAIN, 12));
        }
        this.repaint();
    }

    public void mousePressed(MouseEvent e)
    {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseReleased(MouseEvent e)
    {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Make sure we have a hand on the mouse cursor. 
     * @param e
     */
    public void mouseEntered(MouseEvent e)
    {
        Cursor c = new Cursor(Cursor.HAND_CURSOR);
        setCursor(c);
    }

    public void mouseExited(MouseEvent e)
    {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}
