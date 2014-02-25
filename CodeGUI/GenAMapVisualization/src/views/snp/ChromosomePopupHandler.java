package views.snp;

import datamodel.Marker;
import datamodel.Population;
import datamodel.Trait;
import realdata.BareBonesBrowserLaunch;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import edu.uci.ics.jung.visualization.picking.PickedState;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import views.FeatureResultsViewer;

/**
 * Most GUI controls work though the right-click trigger. The will allow the user
 * to zoom in on selections, and also to query for SNPs in databases, etc.
 *
 * The user can also use the chromosome view control panel in order to select
 * exact locations of where on the chromosome to go to. 
 * @author kellycha
 * @author mzuromskis
 */
public class ChromosomePopupHandler extends AbstractPopupGraphMousePlugin implements GraphMouseListener
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
    public final static Cursor busyCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);

    /**
     * Creates a new popup handler. 
     * @param cv
     */
    public ChromosomePopupHandler(ChromosomeView cv)
    {
        super();
        this.cv = cv;
        popup = new JPopupMenu();
    }

    @Override
    protected void handlePopup(MouseEvent e)
    {

        if (e == null)
        {
            throw new NullPointerException();
        }
        if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e) || e.getButton() == MouseEvent.BUTTON3)
        {
            final Point2D p = e.getPoint();
            popup.removeAll();
            final VisualizationViewer<Integer, Number> vv = (VisualizationViewer<Integer, Number>) e.getSource();

            GraphElementAccessor<Integer, Number> pickSupport = vv.getPickSupport();

            final Integer vertex = pickSupport.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
            final PickedState<Integer> pickedVertexState = vv.getPickedVertexState();
            if (!pickedVertexState.isPicked(vertex) && vertex != null)
            {
                vv.getPickedVertexState().clear();
                pickedVertexState.pick(vertex, true);
            }

            if (pickSupport != null && vertex != null)
            {
                JMenu menu = new JMenu("Online Resources");
                AbstractAction a = new AbstractAction("dbSNP")
                {
                    public void actionPerformed(ActionEvent a)
                    {
                        ArrayList<Marker> markers = cv.getPickedMarkers();
                        String name = markers.get(0).getName();
                        BareBonesBrowserLaunch.openURL("http://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?searchType=adhoc_search&type=rs&rs=" + name);
                    }
                };
                AbstractAction b = new AbstractAction("SGD (yeast)")
                {
                    public void actionPerformed(ActionEvent a)
                    {
                        ArrayList<Marker> markers = cv.getPickedMarkers();
                        String name = markers.get(0).getName();
                        BareBonesBrowserLaunch.openURL("http://www.yeastgenome.org/cgi-bin/locus.fpl?locus=" + name);
                    }
                };

                ArrayList<Marker> markers = cv.getPickedMarkers();
                //If the selected nodes comprise more than one marker, then these options are disabled.
                if (markers.size() != 1)
                {
                    a.setEnabled(false);
                    b.setEnabled(false);
                    menu.setEnabled(false);
                }
                menu.add(a);
                menu.add(b);
                popup.add(menu);
            }

             popup.add(new AbstractAction("View SNP Features")
            {
                public void actionPerformed(ActionEvent e)
                {
                   ArrayList<Marker> markers = cv.getPickedMarkers();
                   for(int di=0; di < markers.size(); di++)
                   {
                    String name = markers.get(di).getName();
                    System.out.println(name+" "+markers.get(di).getId());
                   }

                   FeatureResultsViewer.getInstance().setVisible(true, "Feature Values", markers, null, 2);
                }
            });
            
            popup.add(new AbstractAction("Reset View")
            {
                public void actionPerformed(ActionEvent e)
                {
                    cv.resetView();
                }
            });

            popup.add(new AbstractAction("Zoom Selection")
            {
                public void actionPerformed(ActionEvent e)
                {
                    cv.zoomSelect();
                }
            });

           

            if (cv.getAssociated() && vv.getPickedVertexState().getPicked().size() > 0)
            {
                String s = "Highlight Associated Traits";
                if (cv.getIs3Way())
                {
                    s = "Highlight Associated Genes";
                }
                popup.add(new AbstractAction(s)
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        cv.highlightAssociatedTraits();
                    }
                });

                if (!cv.getIs3Way())
                {
                    popup.add(new AbstractAction("Save Associated Traits As Subset")
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            cv.saveAssociationTraits();
                        }
                    });
                }
            }

            final ArrayList<Trait> t = cv.getChart() == null
                    ? new ArrayList<Trait>() : cv.getChart().getTraitList();
            final Population pr = cv.getPopulation();
            final int numPops = cv.getNumPops();

            if (cv.getAssociated() && vv.getPickedVertexState().getPicked().size() == 1 && cv.getChart() != null && t.size() == 1)
            {
                popup.add(new AbstractAction("Show frequency table")
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        vv.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
                        cv.showFrequencyTable(t, pr, numPops);
                    }
                });
            }

            if (cv.getAssociated())
            {

                popup.add(new AbstractAction("Reset Vertex Colors")
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        cv.resetVertexColors();
                    }
                });
            }

            if (popup.getComponentCount() > 0)
            {
                popup.show((Component) vv, (int) p.getX(), (int) p.getY());
            }
        }
    }

    public void graphClicked(Object v, MouseEvent me)
    {
        if (me == null)
        {
            throw new NullPointerException();
        }
        handlePopup(me);
    }

    public void graphPressed(Object v, MouseEvent me)
    {
        if (me == null)
        {
            throw new NullPointerException();
        }
        handlePopup(me);
    }

    public void graphReleased(Object v, MouseEvent me)
    {
        if (me == null)
        {
            throw new NullPointerException();
        }
        handlePopup(me);
    }
}
