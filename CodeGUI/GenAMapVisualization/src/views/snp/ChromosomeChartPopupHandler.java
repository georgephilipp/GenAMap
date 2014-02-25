package views.snp;

import views.snp.comparison.TestComparisonFrame;
import datamodel.Trait;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import views.AssociationView;

/**
 * This class is to handle graph popup in chromosome view
 * @author anuj
 */
public class ChromosomeChartPopupHandler implements MouseListener, ActionListener, MouseWheelListener
{
    /**
     * A pointer back to the main chromosome view - this is what will do the
     * method implementations
     */
    private ChromosomeView cv;
    /**
     * The popup menu that will display. 
     */
    private JPopupMenu popup;
    /**
     * An object that coordinates what is shown on the screen! 
     */
    private ManhattanPlotsManager manager;

    /**
     * Creates a new popup handler. 
     * @param cv
     */
    public ChromosomeChartPopupHandler(ChromosomeView cv)
    {
        super();
        this.cv = cv;
        popup = new JPopupMenu();
    }

    /**
     * Displays the popup for the chromosome chart and links out to all
     * of the methods availabe to the user. 
     * @param e
     */
    protected void handlePopup(MouseEvent e)
    {
        if (e == null)
        {
            throw new NullPointerException();
        }

        //if (e.isPopupTrigger())
        if (SwingUtilities.isRightMouseButton(e))
        {
            final Point2D p = e.getPoint();
            //if(popup.getComponentCount() > 0)
            //popup.removeAll();
            popup = new JPopupMenu();

            // popup.add(getMenuItem("Switch to JUNG view", this));
            // popup.show(cv.goleft1, (int) p.getX(), (int) p.getY());
            // if(true)
            ///return;


            if (false && !manager.hasTwoSeries())
            {
                JMenu menu1 = new JMenu("Set Y-Axis Scale");
                JMenuItem a = getMenuItem("Linear", this);
                JMenuItem b = getMenuItem("Log", this);

                if (!cv.islogoption)
                {
                    menu1.setEnabled(false);
                }
                else if (cv.islogdisplay)
                {
                    a.setEnabled(true);
                    b.setEnabled(false);
                }
                else if (!cv.islogdisplay)
                {
                    a.setEnabled(false);
                    b.setEnabled(true);
                }
                menu1.add(a);
                menu1.add(b);
                popup.add(menu1);
                JMenu menu8 = new JMenu("Absolute value");
                JMenuItem h = getMenuItem("true", this);
                JMenuItem i = getMenuItem("false", this);
                menu8.add(h);
                menu8.add(i);
                if (cv.getChart().isAbs)
                {
                    h.setEnabled(false);
                    i.setEnabled(true);
                }
                else
                {
                    h.setEnabled(true);
                    i.setEnabled(false);
                }

                popup.add(menu8);
            }
            JMenu menu2 = new JMenu("Set Y Axis Range");
            JMenuItem c = getMenuItem("By Maximum", this);
            JMenuItem d = getMenuItem("By Window", this);

            if (cv.yaxislimitwindow)
            {
                c.setEnabled(true);
                d.setEnabled(false);

            }
            else if (!cv.yaxislimitwindow)
            {
                c.setEnabled(false);
                d.setEnabled(true);
            }
            menu2.add(c);
            menu2.add(d);
            popup.add(menu2);

            if (!cv.ac.isPopAssocSet())
            {
                JMenu menu3 = new JMenu("Remove Trait");
                //datamodel.Trait[] ta = new Trait[1];
                // cv.cvt.getSelected().toArray( ta);
                for (Trait tr : manager.getDisplayedTraits())
                {
                    JMenuItem dd = getMenuItem(tr.getName(), this);
                    menu3.add(dd);
                }

                popup.add(menu3);
            }
            else
            {
                JMenu menu3 = new JMenu("Remove Population");
                int cnt = 0;
                for (int pn = 1; pn <= cv.ac.getNumPops(); pn++)
                {
                    if (cv.cvt.populationToRemove.contains(pn))
                    {
                        continue;
                    }
                    JMenuItem dd = getMenuItem("Remove " + Integer.toString(pn), this);
                    cnt++;
                    menu3.add(dd);
                }
                if (cnt > 1)
                {
                    popup.add(menu3);
                }

                JMenu menu33 = new JMenu("Add Population");
                for (int pr : cv.cvt.populationToRemove)
                {
                    JMenuItem dd = getMenuItem("Add " + Integer.toString(pr), this);
                    menu33.add(dd);
                }


                if (cv.cvt.populationToRemove.size() == 0)
                {
                    menu33.setEnabled(false);
                }
                popup.add(menu33);


            }
            ArrayList<String> testlist = manager.getUndisplayedAssocSets();
            if (testlist.size() > 0)
            {
                JMenu menu4 = new JMenu("Add Test");
                for (int cn = 0; cn < testlist.size(); cn++)
                {
                    JMenuItem dd = getMenuItem("Show " + testlist.get(cn) + " results", this);
                    menu4.add(dd);
                }
                popup.add(menu4);
            }
            testlist = manager.getDisplayedAssocSets();
            if (testlist.size() > 1)
            {
                JMenu menu5 = new JMenu("Clear Test");
                for (int cn = 0; cn < testlist.size(); cn++)
                {
                    JMenuItem dd = getMenuItem("Clear " + testlist.get(cn) + " results", this);
                    menu5.add(dd);
                }
                popup.add(menu5);
            }
            //if(manager.getUndisplayedAssocSets().size() +
            //        manager.getDisplayedAssocSets().size() > 1)
            {
                JMenuItem menu6 = getMenuItem("Run Comparison Analysis", this);
                popup.add(menu6);
            }
            
            JMenuItem manht = null;
            //if (cv.islogoption)
            {
                if (cv.cvt.isClassicManhattan)
                {
                    manht = getMenuItem("Linear Plot", this);
                }
                else
                {
                    manht = getMenuItem("Dot Plot", this);
                }
                popup.add(manht);
            }
            JMenuItem r = getMenuItem("Remove", this);
            popup.add(r);
            //popup.add(getMenuItem("Switch to JUNG view", this));
            //if (popup.getComponentCount() > 0)
            // {
            popup.show(cv.goleft1, (int) p.getX(), (int) p.getY());
            //   }//
        }
    }

    public void mouseClicked(MouseEvent me)
    {
    }

    public void mousePressed(MouseEvent me)
    {
    }

    public void mouseReleased(MouseEvent me)
    {
        //else if(e.getButton() == MouseEvent.BUTTON3 || SwingUtilities.isRightMouseButton(e))
        if (me == null)
        {
            throw new NullPointerException();
        }

        if (me.isPopupTrigger())
        {
            handlePopup(me);
        }
    }

    public void mouseEntered(MouseEvent me)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }

    public void actionPerformed(ActionEvent e)
    {
        String ac = e.getActionCommand();

        if (ac.equals("LINEAR"))
        {
            cv.islogdisplay = false;
            cv.updateChart();
        }
        else if (ac.equals("LOG"))
        {

            cv.islogdisplay = true;
            cv.updateChart();
        }
        else if (ac.equals("TRUE"))
        {
            cv.getChart().isAbs = true;
            cv.updateChart();
        }
        else if (ac.equals("FALSE"))
        {
            cv.getChart().isAbs = false;
            cv.updateChart();
        }
        else if (ac.equals("BY MAXIMUM"))
        {
            cv.yaxislimitwindow = false;
            cv.updateChart();
        }
        else if (ac.equals("BY WINDOW"))
        {

            cv.yaxislimitwindow = true;
            cv.updateChart();
        }
        else if (ac.equals("REMOVE"))
        {
            cv.removeCharts();
            //cv.yaxislimitwindow = true;
            //cv.updateChart();
        }
        else if (ac.equals("LINEAR PLOT"))
        {
            cv.updateChartType(false);

        }
        else if (ac.equals("DOT PLOT"))
        {
            cv.updateChartType(true);

        }
        else if (ac.contains("SHOW"))
        {
            String nm = ac.split(" ")[1];
            manager.addTest(nm);
            cv.cvt.setup(cv.getChromosome(), cv.getAssocSet());
            cv.updateChart();
        }
        else if (ac.contains("CLEAR"))
        {
            String nm = ac.split(" ")[1];
            manager.removeTest(nm);
            cv.cvt.setup(cv.getChromosome(), cv.getAssocSet());
            cv.updateChart();
        }
        else if(ac.contains("COMPARISON"))
        {
            AssociationView.getCurrentRunningInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            TestComparisonFrame tcf = new TestComparisonFrame(manager);
            tcf.setVisible(true);
            AssociationView.getCurrentRunningInstance().setCursor(Cursor.getDefaultCursor());
        }
        else
        {
            if (!cv.ac.isPopAssocSet())
            {
                List<Trait> toRemove = new ArrayList<Trait>();
                for (Trait tt : manager.getDisplayedTraits())
                {
                    if (tt.getName().compareToIgnoreCase(ac) == 0)
                    {
                        toRemove.add(tt);
                        //cv.cvt.selected.remove(tt);
                    }
                }
                manager.getDisplayedTraits().removeAll(toRemove);
                cv.cvt.setup(cv.getChromosome(), cv.getAssocSet());
                cv.updateChart();
            }
            else
            {
                if (ac.split(" ")[0].compareToIgnoreCase("add") == 0)
                {
                    int pop = Integer.parseInt(ac.split(" ")[1]);
                    for(int i = 0; i < cv.cvt.populationToRemove.size();i ++)
                    {
                        if(cv.cvt.populationToRemove.get(i) == pop)
                            cv.cvt.populationToRemove.remove(i);
                    }
                    cv.cvt.setup(cv.getChromosome(), cv.getAssocSet());
                    cv.updateChart();
                }
                if (ac.split(" ")[0].compareToIgnoreCase("remove") == 0)
                {
                    cv.cvt.populationToRemove.add(Integer.parseInt(ac.split(" ")[1]));
                    cv.cvt.setup(cv.getChromosome(), cv.getAssocSet());
                    cv.updateChart();
                }
            }
        }
    }

    private JMenuItem getMenuItem(String s, ActionListener al)
    {
        JMenuItem menuItem = new JMenuItem(s);
        menuItem.setActionCommand(s.toUpperCase());
        menuItem.addActionListener(al);
        return menuItem;
    }

    public void mouseWheelMoved(MouseWheelEvent e)
    {
        int notches = e.getWheelRotation();
        if (notches < 0)
        {
            //System.out.println("WHeel up");
            //"Mouse wheel moved UP "
            cv.zoomIn();
        }
        else
        {
            //"Mouse wheel moved DOWN "
            //System.out.println("WHeel down");
            cv.zoomOut();
        }
    }

    void setManhattanPlotManager(ManhattanPlotsManager mpm)
    {
        this.manager = mpm;
    }
}
