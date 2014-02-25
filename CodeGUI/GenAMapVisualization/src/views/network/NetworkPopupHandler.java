package views.network;

import BiNGO.BiNGOresults2GenAMap;
import BiNGO.GoItems;
import control.DataAddRemoveHandler;
import datamodel.Project;
import datamodel.Model;
import datamodel.Trait;
import datamodel.TraitSet;
import datamodel.TraitSubset;
import datamodel.TraitTreeVal;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.layout.ObservableCachingLayout;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.picking.RadiusPickSupport;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import views.AssociationView;
import control.GOFrame;
import java.awt.Cursor;
import javax.swing.SwingUtilities;
import views.GoResultsViewer;
import views.snp.MyPickableVertexPaintTransformer;

/**
 * The user has acess to many controls through the right-click menu when interacting
 * with the network popup handler.
 * @author Sharath
 * @author rcurtis
 */
public class NetworkPopupHandler extends AntiScrollGraphMouseListener 
{
    /**
     * A pointer to the control panel for the network view, which is what
     * controls most of the network visualization settings
     */
    private NetworkControlPanel controlPanel;
    /**
     * The pointer to the popup menu that is displayed.
     */
    private JPopupMenu popup;
    /**
     * A pointer back to the network view, used to query for more information
     * regarding user's requests
     */
    private NetworkView nv;
    /**
     * associated datasets have different options available that datasets without
     * associations do not have
     */
    private boolean isAssociated;
    /**
     * A pointer to the name of the trait
     */
    private StringPointer traitName;
    /**
     * The JPanel that the popup resides in
     */
    private JPanel jp;
    /**
     * A pointer to the trait subsets that is currently displayed in the network. 
     */
    private TraitSubset subby;

    /**
     * Constructor
     * 
     */
    public NetworkPopupHandler(NetworkView nv, JPanel jp,
            NetworkControlPanel controlPanel, boolean associated, TraitSubset subby)
    {
        super();
        this.jp = jp;
        this.subby = subby;
        popup = new JPopupMenu();
        this.nv = nv;
        this.controlPanel = controlPanel;
        this.isAssociated = associated;
        traitName = new StringPointer();
    }

    /**
     * performs action based on triggering action.
     * @param e
     */
    @SuppressWarnings(
    {
        "unchecked", "serial", "serial"
    })
    protected void handlePopup(MouseEvent e)
    {
        //System.out.println("Am I getting here?");
        if (e == null)
        {
            throw new NullPointerException();
        }
        if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e) || e.getButton() == MouseEvent.BUTTON3)
        {
            Point2D isLabel = checkLabel(e, traitName, 'e');
            if (isLabel != null)
            {
                popup.removeAll();
                e.consume();
                NetworkLabelPopupHandler labelPopup = new NetworkLabelPopupHandler();
                labelPopup.handlePopup(e, traitName.s, jp);
            }
            else
            {
                popup.removeAll();
                final VisualizationViewer<Trait, Number> vv =
                        (VisualizationViewer<Trait, Number>) e.getSource();
                final Layout<Trait, Number> layout = vv.getGraphLayout();
                final Point2D p = e.getPoint();
                GraphElementAccessor<Trait, Number> pickSupport = vv.getPickSupport();
                if (pickSupport != null)
                {
                    final Trait vertex = pickSupport.getVertex(layout, p.getX(), p.getY());
                    final PickedState<Trait> pickedVertexState = vv.getPickedVertexState();
                    if (vertex != null)
                    {
                        if (pickedVertexState.isPicked(vertex))
                        {
                            popup.add(new AbstractAction("Unselect vertex")
                            {
                                public void actionPerformed(ActionEvent e)
                                {
                                    pickedVertexState.pick(vertex, false);
                                }
                            });
                        }
                        else
                        {
                            popup.add(new AbstractAction("Select vertex")
                            {
                                public void actionPerformed(ActionEvent e)
                                {
                                    pickedVertexState.pick(vertex, true);
                                }
                            });
                        }

                    }
                    Set<Trait> picked = vv.getPickedVertexState().getPicked();
                    String todisp = "";

                    if (AssociationView.getCurrentRunningInstance().isPopulationAssoc())
                    {
                        todisp = "View Manhattan Plot";
                    }
                    else if (AssociationView.getCurrentRunningInstance().isTraitsInChartEmpty())
                    {
                        todisp = "View Manhattan Plot";
                    }
                    else
                    {
                        todisp = "Add to Manhattan Plot";
                    }
                    //AssociationView.getCurrentRunningInstance()
                    AbstractAction rm = new AbstractAction(todisp)
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            AssociationView.getCurrentRunningInstance().addChart();
                            //AssociationView.getCurrentRunningInstance().refreshView();
                        }
                    };

                    if (picked.size() == 0 || (picked.size() > 1 && AssociationView.getCurrentRunningInstance().isPopulationAssoc()))
                    {
                        rm.setEnabled(false);
                    }
                    if (picked.size() > 10)
                    {
                        rm.setEnabled(false);
                    }

                    if (isAssociated)
                    {
                        popup.add(rm);
                    }
                    //if (picked.size() == 0)
                    {
                        popup.add(new AbstractAction("Perform GO Analysis")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                startGOAnalysis(null);
                            }
                        });
                    }
                    //if we are showing a marker trait association, then we need these options
                    if (isAssociated)
                    {
                        popup.add(new AbstractAction("Highlight Associated Markers")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                nv.getParent().highlightAssociatedMarkers(vv.getPickedVertexState().getPicked());
                            }
                        });

                        popup.add(new AbstractAction("Reset Vertex Colors")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                if (subby != null)
                                {
                                    nv.setDefaultNodeColor(subby.getGoItems());
                                }
                                vv.getRenderContext().setVertexFillPaintTransformer(new MyPickableVertexPaintTransformer<Trait>(vv.getPickedVertexState(), nv.colormap, Color.black, Color.PINK));
                                vv.repaint();
                            }
                        });
                    }

                    //get selected traits
                    String projectName = nv.GetNetwork().getProjectName();
                    String traitName = nv.GetNetwork().getTraitName();
                    final Project assocProj = Model.getInstance().getProject(projectName);

                    ArrayList<Trait> selectedTraits = new ArrayList<Trait>();

                    for (Trait t : (Collection<Trait>) nv.getVisualizationViewer().getGraphLayout().getGraph().getVertices())
                    {
                        if (nv.getVisualizationViewer().getPickedVertexState().isPicked(t))
                        {
                            selectedTraits.add(t);
                        }
                    }
                    final TraitSet ts = assocProj.getTrait(traitName);
                    final ArrayList<Integer> selectedIndeces = getIndeces(selectedTraits);

                    if (getIndeces(selectedTraits).size() != 0)
                    {
                        popup.add(new AbstractAction("Save Traits as Subset")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                /*NewDataNameGetter ndng = new NewDataNameGetter(nv.getOwner(), true, "Choose a name for these traits:",
                                ts.getSubsetNames());
                                ndng.show();

                                String subsetName = ndng.newName;*/
                                String subsetName = ts.getNextSubsetName();

                                TraitSubset subset = new TraitSubset(ts, selectedIndeces, subsetName);
                                ts.addSubset(subset);
                                DataAddRemoveHandler.getInstance().refreshDisplay();
                            }
                        });
                    }
                    JMenu viewMenu = new JMenu("View");
                    if (controlPanel.getVertexLabelsON())
                    {
                        viewMenu.add(new AbstractAction("Turn Vertex Labels Off")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                controlPanel.turnOffVertexLabels();
                            }
                        });
                    }
                    else
                    {
                        viewMenu.add(new AbstractAction("Turn Vertex Labels On")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                controlPanel.turnOnVertexLabels();
                            }
                        });
                    }
                    if (controlPanel.getEdgeLabelsON())
                    {
                        viewMenu.add(new AbstractAction("Turn Edge Labels Off")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                controlPanel.turnOffEdgeLabels();
                            }
                        });
                    }
                    else
                    {
                        viewMenu.add(new AbstractAction("Turn Edge Labels On")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                controlPanel.turnOnEdgeLabels();
                            }
                        });
                    }
                    if (controlPanel.getEdgeWeightsON())
                    {
                        viewMenu.add(new AbstractAction("Turn Edge Weights Off")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                controlPanel.turnOffEdgeWeights();
                            }
                        });
                    }
                    else
                    {
                        viewMenu.add(new AbstractAction("Turn Edge Weights On")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                controlPanel.turnOnEdgeWeights();
                            }
                        });
                    }
                    viewMenu.add(new AbstractAction("Reset View")
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            controlPanel.resetView();
                        }
                    });
                    popup.add(viewMenu);

                    JMenu setLayoutMenu = new JMenu("Set Layout");
                    if (!controlPanel.getLayoutClass().equals(KKLayout.class))
                    {
                        setLayoutMenu.add(new AbstractAction("KKLayout")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                controlPanel.setLayout(KKLayout.class);
                            }
                        });
                    }
                    if (!controlPanel.getLayoutClass().equals(FRLayout.class))
                    {
                        setLayoutMenu.add(new AbstractAction("FRLayout")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                controlPanel.setLayout(FRLayout.class);
                            }
                        });
                    }
                    if (!controlPanel.getLayoutClass().equals(CircleLayout.class))
                    {
                        setLayoutMenu.add(new AbstractAction("CircleLayout")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                controlPanel.setLayout(CircleLayout.class);
                            }
                        });
                    }
                    if (!controlPanel.getLayoutClass().equals(SpringLayout.class))
                    {
                        setLayoutMenu.add(new AbstractAction("SpringLayout")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                controlPanel.setLayout(SpringLayout.class);
                            }
                        });
                    }
                    if (!controlPanel.getLayoutClass().equals(SpringLayout2.class))
                    {
                        setLayoutMenu.add(new AbstractAction("SpringLayout2")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                controlPanel.setLayout(SpringLayout2.class);
                            }
                        });
                    }
                    if (!controlPanel.getLayoutClass().equals(ISOMLayout.class))
                    {
                        setLayoutMenu.add(new AbstractAction("ISOMLayout")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                controlPanel.setLayout(ISOMLayout.class);
                            }
                        });
                    }
                    popup.add(setLayoutMenu);

                    popup.add(new AbstractAction("Switch to Matrix View")
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            AssociationView.getCurrentRunningInstance().setIsJungView(false);
                            AssociationView.getCurrentRunningInstance().refreshView();
                        }
                    });
                }

                if (popup.getComponentCount() > 0)
                {
                    popup.show((Component) vv, (int) p.getX(), (int) p.getY());
                }
            }

        }
    }

    /**
     *
     * forwards click action to handlePopup
     * @param v
     * @param me
     */
    public void graphClicked(Object v, MouseEvent me)
    {
        if (me == null)
        {
            throw new NullPointerException();
        }
        handlePopup(me);
    }

    /**
     *
     * forwards press action to handlePopup
     * @param v
     * @param me
     */
    public void graphPressed(Object v, MouseEvent me)
    {
        if (me == null)
        {
            throw new NullPointerException();
        }
        handlePopup(me);
    }

    /**
     *
     * forwards release action to handlePopup
     * @param v
     * @param me
     */
    public void graphReleased(Object v, MouseEvent me)
    {
        if (me == null)
        {
            throw new NullPointerException();
        }
        handlePopup(me);
    }

    private ArrayList<Integer> getIndeces(ArrayList<Trait> selectedTraits)
    {
        ArrayList<Integer> ids = new ArrayList<Integer>();

        for (Trait t : selectedTraits)
        {
            ids.add(t.getId());
        }
        return ids;
    }

    /**
     * Gets the traits needed for the GO analysis and then starts the analysis. 
     */
    private void startGOAnalysis(HashSet selected)
    {
        ArrayList<Trait> traits = nv.GetNetwork().getTraitSet().getTraits();
        HashSet reference = new HashSet<String>();
        TraitSubset tss = nv.getTraitSubset();
        for (Trait t : traits)
        {
            reference.add(t.getName().toUpperCase());
        }
        if (selected == null)
        {
            selected = new HashSet<String>();

            if (tss == null)
            {
                selected = reference;
            }
            else
            {
                for (int i : tss.getIndeces())
                {
                    for (Trait t : traits)
                    {
                        if (t.getId() == i)
                        {
                            selected.add(t.getName().toUpperCase());
                            break;
                        }
                    }
                }
            }
        }
        GOFrame gf = new GOFrame(reference, selected, nv.GetNetwork().getTraitSet().getSpecies(),
                nv.GetNetwork().getTraitSet());
        gf.setVisible(true);
        BiNGOresults2GenAMap results = gf.getResults();
        ArrayList<GoItems> items = results.getEnrichedGoItems();
        String name = tss == null ? "Unnamed subset" : tss.getName();
        if (tss != null)
        {
            tss.updateGo(results.getStringRepresentationOfGoResults());
        }
        nv.establishGoMap();
        GoResultsViewer gif = GoResultsViewer.getInstance();
        gif.setVisible(true, name, items, null, nv.currentGoMap, nv, 2);
        gf.dispose();
    }

    /**
     * Checks to find out if the mouse clikc happened on a label in the visualization
     * graph
     * @param e the mouse event
     * @param traitname updated with the name of the trait
     * @param pos the position of the labels with respect to the nodes.
     * @return
     */
    public static Point2D checkLabel(MouseEvent e, StringPointer traitname, char pos)
    {
        Trait t;
        Point2D p;
        //The location used to pick the nearest vertex is shifted left
        if (pos == 'e')//this is a trait
        {
            VisualizationViewer<Trait, Number> vv =
                    (VisualizationViewer<Trait, Number>) e.getSource();
            Layout<Trait, Number> layout = vv.getGraphLayout();
            RadiusPickSupport<Trait, Number> pick = new RadiusPickSupport<Trait, Number>();
            t = pick.getVertex(layout, e.getX() - 15, e.getY());
            ObservableCachingLayout<Trait, Number> l = (ObservableCachingLayout<Trait, Number>) layout;
            p = l.transform(t);
        }
        else
        {
            VisualizationViewer<TraitTreeVal, Number> vv =
                    (VisualizationViewer<TraitTreeVal, Number>) e.getSource();
            Layout<TraitTreeVal, Number> layout = vv.getGraphLayout();
            RadiusPickSupport<TraitTreeVal, Number> pick = new RadiusPickSupport<TraitTreeVal, Number>();
            TraitTreeVal ttv = pick.getVertex(layout, e.getX(), e.getY() + 5);
            t = ttv.getTrait();
            ObservableCachingLayout<TraitTreeVal, Number> l =
                    (ObservableCachingLayout<TraitTreeVal, Number>) layout;
            p = l.transform(ttv);
            if (t == null)
            {
                return null;
            }
        }

        double highY, lowY, lowX, highX;
        int xOffset = 14;
        int charSize = 8;
        if (pos == 'e')
        {
            highY = p.getY() + 4;
            lowY = p.getY() - 4;
            lowX = p.getX() + xOffset;
            highX = lowX + charSize * t.getName().length();
        }
        else
        {
            highY = p.getY() + xOffset + charSize * 2;
            lowY = p.getY() + xOffset;
            lowX = p.getX() - charSize * t.getName().length() / 2;
            highX = p.getX() + charSize * t.getName().length() / 2;
        }


        double x = e.getX();
        double y = e.getY();

        if (y < highY && y > lowY)
        {
            if (x < highX && x > lowX)
            {
                p.setLocation(x, y);
                traitname.s = t.getName();
            }
            else
            {
                p = null;
            }
        }
        else
        {
            p = null;
        }

        return p;
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        super.mouseReleased(e);
        final VisualizationViewer<TraitTreeVal, Number> vv =
                (VisualizationViewer<TraitTreeVal, Number>) e.getSource();
        if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e) || e.getButton() == MouseEvent.BUTTON3)
        {
            this.handlePopup(e);
            vv.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return;
        }

        Collection picked = vv.getPickedVertexState().getPicked();
        if (picked.size() > 1 || picked.size() == 0)
        {
            if (subby != null && subby.getGoList() > 0)
            {
                GoResultsViewer.getInstance().setVisible(true, subby.getName(), subby.getGoItems(), null,
                        this.nv.currentGoMap, nv, 2);
                vv.setCursor(new Cursor(Cursor.HAND_CURSOR));
                return;
            }
            GoResultsViewer.getInstance().setVisible(false);
            vv.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return;
        }
        for (Object v : picked)
        {
            if (v instanceof Trait)
            {
                GoResultsViewer.getInstance().setVisible(false);
                Trait cur = (Trait) v;
                String nm = cur.getName();
                if (nv.currentGoMap.size() > 0)
                {
                    if (cur.getCurrentGoAnnotation(null).size() > 0)
                    {
                        GoResultsViewer.getInstance().setVisible(true, nm, null,
                                cur.getCurrentGoAnnotation(this.nv.currentGoMap), this.nv.currentGoMap, nv, 2);
                    }
                }
                else
                {
                    if (cur.getCurrentGoAnnotation(null).size() > 0)
                    {
                        GoResultsViewer.getInstance().setVisible(true, nm, null,
                                cur.getCurrentGoAnnotation(null), null, nv, 2);
                    }
                }
            }
        }
        vv.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
