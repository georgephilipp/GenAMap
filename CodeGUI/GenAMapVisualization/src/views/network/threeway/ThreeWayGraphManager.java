package views.network.threeway;

import java.util.Observable;
import views.network.*;
import control.DataAddRemoveHandler;
import datamodel.Trait;
import datamodel.TraitSubset;
import datamodel.TraitTreeVal;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.layout.ObservableCachingLayout;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.picking.RadiusPickSupport;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import views.AssociationView;
import control.NewDataNameGetter;
import datamodel.GeneTraitAssociation;
import datamodel.ThreeWayVisualizationSettings;
import edu.uci.ics.jung.graph.Graph;
import java.awt.Cursor;
import java.util.Observer;
import javax.swing.SwingUtilities;
import views.snp.VertexStringerImpl;

/**
 * The threeway popup handler is similar in function to those popup handlers
 * that have come before. However, many of the controls are actually now in
 * the control box for the visualization.
 *
 * Thus, in this view we are interested primarily in linking out to external
 * sources for genes and traits or collapsing or expanding nodes. However, other
 * functionality from the previous implementation has been preserved where
 * appropriate. 
 * @author rcurtis
 */
public class ThreeWayGraphManager extends AntiScrollGraphMouseListener implements Observer
{
    /**
     * The popup menu that holds the menu options for the user to select from. 
     */
    private JPopupMenu popup;
    /**
     * The Jpanel that we are working with. This is used for opening dialog boxes
     * to query the user for information
     */
    private JPanel jp;
    /**
     * A pointer back to the object that we are working with. Not sure why we
     * are coupled like this ... 
     */
    private GeneTraitNetworkView gtnv;
    /**
     * The settings that determine which edges we will show or not show. We
     * manage all that information in this class
     */
    private ThreeWayVisualizationSettings settings;
    /**
     * A pointer to the gta object that we are working with here. 
     */
    private GeneTraitAssociation gta;
    /**
     * A boolean indicating whether or not we should show all of the association
     * edges on the screen. This helps us know what to do when it is changed -
     * we will manually remove or add all the association edges onto the graph.
     */
    private boolean isShowAssociationEdges = true;
    /**
     * A boolean indicating whether or not we should show nodes w/o association
     * edges on the screen. This helps us know what to do when it is changed -
     * we will manually remove or add all the nodes w/o association onto the graph.
     */
    private boolean isShowNodesWithNoAssoc = true;
    /**
     * The threshold for adding and removing trait edges
     */
    private double traitEdgeThreshold = 0.0;
    /**
     * The threshold for adding and removing gene edges
     */
    private double geneEdgeThreshold = 0.0;
    /**
     * The threshold for adding and removing association edges.
     */
    private double assocEdgeThreshold = 0.0;
    /**
     * If we are showing the labels for association edge labels
     */
    private boolean isShowAssocEdgeLabs = true;
    /**
     * If we are showing the labels for the gene edges.
     */
    private boolean isShowGeneEdgeLabs = true;
    /**
     * If we are showing the labels for the trait edges
     */
    private boolean isShowTraitEdgeLabs = true;
    /**
     * If we are showing the labels for the genes
     */
    private boolean isShowGeneLabs = true;
    /**
     * If we are shoing the lables for the traits
     */
    private boolean isShowTraitLabs = true;

    /**
     * Constructor
     * 
     */
    public ThreeWayGraphManager(JPanel jp, GeneTraitAssociation gta, GeneTraitNetworkView gtnv,
            ThreeWayVisualizationSettings sets)
    {
        super();
        this.gta = gta;
        this.jp = jp;
        popup = new JPopupMenu();
        this.gtnv = gtnv;
        this.settings = sets;
        sets.addObserver(this);
        update(null, null);
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
        if (e == null)
        {
            throw new NullPointerException();
        }
        if (e.isPopupTrigger()  || SwingUtilities.isRightMouseButton(e) || e.getButton() == MouseEvent.BUTTON3)
        {
            StringPointer traitName = new StringPointer();
            Point2D isLabel = checkLabel(e, traitName, 'e');
            if (isLabel != null)
            {
                popup.removeAll();
                e.consume();
                NetworkLabelPopupHandler labelPopup = new NetworkLabelPopupHandler();

                if (!traitName.s.contains("Group"))
                {
                    labelPopup.handlePopup(e, traitName.s, jp);
                    return;
                }
            }
            popup.removeAll();
            final VisualizationViewer<ThreeWayGraphNode, ThreeWayGraphEdge> vv =
                    (VisualizationViewer<ThreeWayGraphNode, ThreeWayGraphEdge>) e.getSource();
            final Layout<ThreeWayGraphNode, ThreeWayGraphEdge> layout = vv.getGraphLayout();
            final Point2D p = e.getPoint();
            GraphElementAccessor<ThreeWayGraphNode, ThreeWayGraphEdge> pickSupport = vv.getPickSupport();
            boolean hasOnlyGenes = true;
            final Set<ThreeWayGraphNode> picked = vv.getPickedVertexState().getPicked();
            final ArrayList<Trait> selectedGenesForAssoc = new ArrayList<Trait>();
            if (pickSupport != null)
            {
                for (ThreeWayGraphNode n : picked)
                {
                    if (n instanceof ThreeWayTraitGraphObject)
                    {
                        hasOnlyGenes = false;
                    }
                    else if (n instanceof ThreeWayGeneNode)
                    {
                        selectedGenesForAssoc.add(((ThreeWayGeneNode) n).getGene());
                    }
                    else
                    {
                        for (ThreeWayGeneNode gn : gtnv.getGenes())
                        {
                            if (gn.getGroup().getGroupNumber() == ((ThreeWayGeneGroupNode) n).getGroupNumber())
                            {
                                selectedGenesForAssoc.add(gn.getGene());
                            }
                        }
                    }
                }
            }
            if (pickSupport != null)
            {
                final ThreeWayGraphNode vertex = pickSupport.getVertex(layout, p.getX(), p.getY());
                final PickedState<ThreeWayGraphNode> pickedVertexState = vv.getPickedVertexState();

                String todisp = "";

                //we will need to see if these are genes or not!

                if (AssociationView.getCurrentRunningInstance().isTraitsInChartEmpty())
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
                        AssociationView.getCurrentRunningInstance().addChart(selectedGenesForAssoc);
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
                if (hasOnlyGenes && selectedGenesForAssoc.size() > 0)
                {
                    popup.add(rm);
                }
                if (hasOnlyGenes && selectedGenesForAssoc.size() > 0)
                {
                    popup.add(new AbstractAction("Highlight Associated Markers")
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            gtnv.getAssocView().highlightAssociatedMarkers(selectedGenesForAssoc);
                        }
                    });
                }

                /*popup.add(new AbstractAction("Reset Vertex Colors")
                {
                public void actionPerformed(ActionEvent e)
                {
                //if (subby != null)
                {
                //nv.setDefaultNodeColor(subby.getGoItems());
                }
                //vv.getRenderContext().setVertexFillPaintTransformer(new MyPickableVertexPaintTransformer<Trait>(vv.getPickedVertexState(), nv.colormap, Color.black, Color.PINK));
                vv.repaint();
                }
                });*/

                final ArrayList<ThreeWayTraitGraphObject> selectedTraits = new ArrayList<ThreeWayTraitGraphObject>();
                final ArrayList<ThreeWayGeneGraphObject> selectedGenes = new ArrayList<ThreeWayGeneGraphObject>();

                for (ThreeWayGraphNode twgn : picked)
                {
                    if (twgn instanceof ThreeWayTraitGraphObject)
                    {
                        selectedTraits.add((ThreeWayTraitGraphObject) twgn);
                    }
                    else if (twgn instanceof ThreeWayGeneGraphObject)
                    {
                        selectedGenes.add((ThreeWayGeneGraphObject) twgn);
                    }
                }

                if (selectedTraits.size() > 1 && selectedGenes.size() == 0)
                {
                    popup.add(new AbstractAction("Collapse traits")
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            collapseTraits(selectedTraits);
                        }
                    });
                    final ArrayList<Integer> selectedIndeces = getIndeces(selectedTraits);

                    if (getIndeces(selectedTraits).size() != 0)
                    {
                        popup.add(new AbstractAction("Save Traits as Subset")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                String subsetName = gta.getTraitSet().getNextSubsetName();

                                TraitSubset subset = new TraitSubset(gta.getTraitSet(), selectedIndeces, subsetName);
                                gta.getTraitSet().addSubset(subset);
                                DataAddRemoveHandler.getInstance().refreshDisplay();
                                pickedVertexState.clear();
                                vv.setPickedVertexState(pickedVertexState);
                            }
                        });
                    }
                }
                else if (selectedGenes.size() == 0 &&
                        selectedTraits.size() == 1 && selectedTraits.get(0) instanceof ThreeWayTraitGroup)
                {
                    popup.add(new AbstractAction("Expand Group")
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            Graph g = vv.getGraphLayout().getGraph();
                            ThreeWayLayout twl = gtnv.getTheLayoutObject();
                            double x = twl.getX(selectedTraits.get(0));
                            double y = twl.getY(selectedTraits.get(0));
                            g.removeVertex(selectedTraits.get(0));
                            ThreeWayTraitGroup twtg = (ThreeWayTraitGroup) selectedTraits.get(0);
                            for (ThreeWayTraitNode twgn : twtg.getTraits())
                            {
                                twgn.setTraitGroup(null);
                            }

                            ThreeWayEdgeManager.epandTraitEdges(twtg, g, twl, x, y, gtnv,
                                    isShowNodesWithNoAssoc, traitEdgeThreshold, isShowAssociationEdges,
                                    assocEdgeThreshold);

                            gtnv.setUpVisualizationRenderingStuff(vv.getGraphLayout().getGraph());
                            vv.repaint();
                            pickedVertexState.clear();
                            vv.setPickedVertexState(pickedVertexState);
                        }
                    });
                    popup.add(
                            new AbstractAction("Rename Group")
                            {
                                public void actionPerformed(ActionEvent e)
                                {
                                    NewDataNameGetter ndng = new NewDataNameGetter(jp, true, "Select a name for this group: ", new ArrayList<String>());
                                    ndng.show();
                                    if (ndng.SUCCESS)
                                    {
                                        ((ThreeWayTraitGroup) selectedTraits.get(0)).rename(ndng.newName);
                                        gtnv.setUpVisualizationRenderingStuff(vv.getGraphLayout().getGraph());
                                        vv.repaint();
                                    }
                                }
                            });
                }
                else if (selectedTraits.size() == 0 && selectedGenes.size() == 1 &&
                        selectedGenes.get(0) instanceof ThreeWayGeneGroupNode)
                {
                    popup.add(new AbstractAction("Expand Group")
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            Graph g = vv.getGraphLayout().getGraph();
                            ThreeWayLayout twl = gtnv.getTheLayoutObject();
                            double x = twl.getX(selectedGenes.get(0));
                            double y = twl.getY(selectedGenes.get(0));
                            g.removeVertex(selectedGenes.get(0));
                            ThreeWayGeneGroupNode twtg = (ThreeWayGeneGroupNode) selectedGenes.get(0);
                            twtg.setIsExpanded(true);
                            ArrayList<ThreeWayCorrelationEdge> edgesToAdd = new ArrayList<ThreeWayCorrelationEdge>();
                            ArrayList<ThreeWayAssociationEdge> edgesToAdd2 = new ArrayList<ThreeWayAssociationEdge>();
                            for (ThreeWayGeneNode twgn : gtnv.getGenes())
                            {
                                if (twgn.getGroup().getGroupNumber() == ((ThreeWayGeneGroupNode) selectedGenes.get(0)).getGroupNumber() && (isShowNodesWithNoAssoc || twgn.hasAssociation()))
                                {
                                    g.addVertex(twgn);
                                    twl.setLocation(twgn, x, y);
                                    ThreeWayEdgeManager.getEdgesToAddForGene(twgn, edgesToAdd, gtnv, geneEdgeThreshold, isShowNodesWithNoAssoc);
                                    ThreeWayEdgeManager.addAssociationEdges(edgesToAdd2, twgn, isShowAssociationEdges, gtnv,
                                            assocEdgeThreshold);
                                }
                            }

                            for (ThreeWayCorrelationEdge twce : edgesToAdd)
                            {
                                g.addEdge(twce, twce.getT1(), twce.getT2());
                            }

                            for (ThreeWayAssociationEdge twae : edgesToAdd2)
                            {
                                g.addEdge(twae, twae.getT1(), twae.getT2());
                            }

                            ThreeWayEdgeManager.setMaxValsAcrossEdgeGroups(g);

                            gtnv.setUpVisualizationRenderingStuff(vv.getGraphLayout().getGraph());
                            vv.repaint();
                            pickedVertexState.clear();
                            vv.setPickedVertexState(pickedVertexState);
                        }
                    });
                }
                else if (selectedTraits.size() == 0 && selectedGenes.size() >= 1)
                {
                    boolean canContinue = true;
                    for (ThreeWayGraphNode n : selectedGenes)
                    {
                        if (n instanceof ThreeWayGeneGroupNode)
                        {
                            canContinue = false;
                        }
                    }
                    if (canContinue)
                    {
                        popup.add(new AbstractAction("Collapse Gene Group(s)")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                Graph g = vv.getGraphLayout().getGraph();
                                ThreeWayLayout twl = gtnv.getTheLayoutObject();
                                double x = twl.getX(selectedGenes.get(0));
                                double y = twl.getY(selectedGenes.get(0));
                                ArrayList<Integer> grpsToRemove = new ArrayList<Integer>();
                                for (ThreeWayGeneGraphObject twggo : selectedGenes)
                                {
                                    ThreeWayGeneNode gene = (ThreeWayGeneNode) twggo;
                                    if (!grpsToRemove.contains(gene.getGroup().getGroupNumber()))
                                    {
                                        grpsToRemove.add(gene.getGroup().getGroupNumber());
                                    }
                                }

                                for (ThreeWayGeneNode twgn : gtnv.getGenes())
                                {
                                    if (grpsToRemove.contains(twgn.getGroup().getGroupNumber()))
                                    {
                                        g.removeVertex(twgn);
                                    }
                                }

                                ArrayList<ThreeWayCorrelationEdge> edgesToAdd = new ArrayList<ThreeWayCorrelationEdge>();
                                ArrayList<ThreeWayAssociationEdge> edgesToAdd2 = new ArrayList<ThreeWayAssociationEdge>();
                                for (ThreeWayGeneGroupNode twggn : gtnv.getGeneGroups())
                                {
                                    if (grpsToRemove.contains(twggn.getGroupNumber()))
                                    {
                                        g.addVertex(twggn);
                                        twggn.setIsExpanded(false);
                                        twl.setLocation(twggn, x, y);
                                        ThreeWayEdgeManager.getEdgesToAddForGeneGroup(twggn, edgesToAdd, gtnv, geneEdgeThreshold, isShowNodesWithNoAssoc);
                                        ThreeWayEdgeManager.addAssociationEdges(edgesToAdd2, twggn, isShowAssociationEdges, 
                                                gtnv,assocEdgeThreshold);
                                    }
                                }

                                for (ThreeWayAssociationEdge twae : edgesToAdd2)
                                {
                                    g.addEdge(twae, twae.getT1(), twae.getT2());
                                }
                                for (ThreeWayGraphEdge twge : edgesToAdd)
                                {
                                    g.addEdge(twge, twge.getT1(), twge.getT2());
                                }

                                ThreeWayEdgeManager.setMaxValsAcrossEdgeGroups(g);
                                gtnv.setUpVisualizationRenderingStuff(vv.getGraphLayout().getGraph());
                                vv.repaint();
                                pickedVertexState.clear();
                                vv.setPickedVertexState(pickedVertexState);
                            }
                        });
                    }
                }
                if (selectedTraits.size() == 0 && selectedGenes.size() >= 1)
                {

                    popup.add(new AbstractAction("Collapse Associated Traits")
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            Graph g = vv.getGraphLayout().getGraph();
                            ThreeWayLayout twl = gtnv.getTheLayoutObject();
                            ArrayList<ThreeWayTraitGraphObject> list = new ArrayList<ThreeWayTraitGraphObject>();
                            for (ThreeWayGeneGraphObject twggo : selectedGenes)
                            {
                                for (Object twge : g.getEdges())
                                {
                                    ThreeWayGraphEdge edge = (ThreeWayGraphEdge) twge;
                                    if (edge instanceof ThreeWayAssociationEdge &&
                                            edge.getT1() == twggo)
                                    {
                                        list.add((ThreeWayTraitGraphObject) edge.getT2());
                                    }
                                }
                            }
                            double x = twl.getX(list.get(0));
                            double y = twl.getY(list.get(0));
                            if (list.size() == 0)
                            {
                                return;
                            }
                            for (ThreeWayGraphNode o : list)
                            {
                                g.removeVertex(o);
                            }

                            createNewTraitGroup(list, g, twl, x, y);
                        }
                    });
                }
                if (selectedTraits.size() > 0 && selectedGenes.size() == 0)
                {
                    popup.add(new AbstractAction("Remove all other trait nets")
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            Graph g = vv.getGraphLayout().getGraph();
                            extractConnectedTraits(g, selectedTraits);
                            ArrayList<ThreeWayGraphNode> list = new ArrayList<ThreeWayGraphNode>();
                            for (Object n : g.getVertices())
                            {
                                if (n instanceof ThreeWayTraitGraphObject &&
                                        !selectedTraits.contains((ThreeWayTraitGraphObject) n))
                                {
                                    list.add((ThreeWayGraphNode) n);
                                }
                            }

                            ThreeWayEdgeManager.addOrRemoveNodes(g, false, gtnv,
                                    traitEdgeThreshold, geneEdgeThreshold, isShowNodesWithNoAssoc, list,
                                    isShowAssociationEdges,assocEdgeThreshold);
                        }
                    });
                }
                if (selectedGenes.size() > 0 && selectedTraits.size() == 0)
                {
                    popup.add(new AbstractAction("Remove all other gene nets")
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            Graph g = vv.getGraphLayout().getGraph();
                            extractConnectedGenes(g, selectedGenes);
                            ArrayList<ThreeWayGraphNode> list = new ArrayList<ThreeWayGraphNode>();
                            for (Object n : g.getVertices())
                            {
                                if (n instanceof ThreeWayGeneGraphObject &&
                                        !selectedGenes.contains((ThreeWayGeneGraphObject) n))
                                {
                                    list.add((ThreeWayGraphNode) n);
                                }
                            }

                            ThreeWayEdgeManager.addOrRemoveNodes(g, false, gtnv,
                                    traitEdgeThreshold, geneEdgeThreshold, isShowNodesWithNoAssoc, list,
                                    isShowAssociationEdges,assocEdgeThreshold);
                        }
                    });
                }
                if (selectedTraits.size() > 0 && selectedGenes.size() == 0)
                {

                    popup.add(new AbstractAction("Collapse connected into a group")
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            Graph g = vv.getGraphLayout().getGraph();
                            extractConnectedTraits(g, selectedTraits);
                            collapseTraits(selectedTraits);
                        }
                    });
                }
                if(selectedTraits.size() == 0 && selectedGenes.size() > 0)
                {
                    popup.add(new AbstractAction("Isolate associated traits")
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            Graph g = vv.getGraphLayout().getGraph();
                            extractAssociatedTraits(g, selectedGenes, selectedTraits);

                            ArrayList<ThreeWayGraphNode> list = new ArrayList<ThreeWayGraphNode>();
                            for (Object n : g.getVertices())
                            {
                                if (n instanceof ThreeWayTraitGraphObject &&
                                        !selectedTraits.contains((ThreeWayTraitGraphObject) n))
                                {
                                    list.add((ThreeWayGraphNode) n);
                                }
                            }

                            ThreeWayEdgeManager.addOrRemoveNodes(g, false, gtnv,
                                    traitEdgeThreshold, geneEdgeThreshold, isShowNodesWithNoAssoc, list,
                                    isShowAssociationEdges,assocEdgeThreshold);
                        }
                    });
                }
                if (selectedTraits.size() > 0 || selectedGenes.size() > 0)
                {

                    popup.add(new AbstractAction("Remove Selected Nodes")
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            Graph g = vv.getGraphLayout().getGraph();
                            ArrayList<ThreeWayGraphNode> list = new ArrayList<ThreeWayGraphNode>();
                            for (ThreeWayGeneGraphObject twggo : selectedGenes)
                            {
                                list.add(twggo);
                            }
                            for (ThreeWayTraitGraphObject twtgo : selectedTraits)
                            {
                                list.add(twtgo);
                            }

                            ThreeWayEdgeManager.addOrRemoveNodes(g, false, gtnv,
                                    traitEdgeThreshold, geneEdgeThreshold, isShowNodesWithNoAssoc, list,
                                    isShowAssociationEdges,assocEdgeThreshold);
                        }
                    });
                }

                popup.add(new AbstractAction("Add Removed Nodes")
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        Graph g = vv.getGraphLayout().getGraph();
                        ThreeWayEdgeManager.addOrRemoveNodes(g, true, gtnv,
                                traitEdgeThreshold, geneEdgeThreshold, isShowNodesWithNoAssoc, null,
                                isShowAssociationEdges,assocEdgeThreshold);
                    }
                });
            }

            if (popup.getComponentCount() > 0)
            {
                popup.show((Component) vv, (int) p.getX(), (int) p.getY());
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

    private ArrayList<Integer> getIndeces(ArrayList<ThreeWayTraitGraphObject> selectedTraits)
    {
        ArrayList<Integer> ids = new ArrayList<Integer>();

        for (ThreeWayTraitGraphObject t : selectedTraits)
        {
            if (t instanceof ThreeWayTraitNode)
            {
                ids.add(((ThreeWayTraitNode) t).getTrait().getId());
            }
            else
            {
                for (ThreeWayTraitNode tr : ((ThreeWayTraitGroup) t).getTraits())
                {
                    ids.add(tr.getTrait().getId());
                }
            }
        }
        return ids;
    }

    /**
     * We want to find out if we have right-clicked on a label. This will
     * cause a link action to be performed instead of the popup menu to show.
     */
    public static Point2D checkLabel(MouseEvent e, StringPointer traitname, char pos)
    {
        Trait t;
        Point2D p;
        //The location used to pick the nearest vertex is shifted left
        VisualizationViewer<ThreeWayGraphNode, ThreeWayGraphEdge> vv =
                (VisualizationViewer<ThreeWayGraphNode, ThreeWayGraphEdge>) e.getSource();
        Layout<ThreeWayGraphNode, ThreeWayGraphEdge> layout = vv.getGraphLayout();
        RadiusPickSupport<ThreeWayGraphNode, ThreeWayGraphEdge> pick = new RadiusPickSupport<ThreeWayGraphNode, ThreeWayGraphEdge>();
        ThreeWayGraphNode q = pick.getVertex(layout, e.getX() - 15, e.getY());
        ObservableCachingLayout<ThreeWayGraphNode, ThreeWayGraphEdge> l = (ObservableCachingLayout<ThreeWayGraphNode, ThreeWayGraphEdge>) layout;
        p = l.transform(q);

        if(q == null) return null;

        double highY, lowY, lowX, highX;
        int xOffset = 14;
        int charSize = 8;
        if (pos == 'e')
        {
            highY = p.getY() + 9;
            lowY = p.getY() - 4;
            lowX = p.getX() + xOffset;
            highX = lowX + charSize * q.getName().length();
        }
        else
        {
            highY = p.getY() + xOffset + charSize * 2;
            lowY = p.getY() + xOffset;
            lowX = p.getX() - charSize * q.getName().length() / 2;
            highX = p.getX() + charSize * q.getName().length() / 2;
        }


        double x = e.getX();
        double y = e.getY();

        if (y < highY && y > lowY)
        {
            if (x < highX && x > lowX)
            {
                p.setLocation(x, y);
                traitname.s = q.getName();
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
        if (e.isPopupTrigger()  || SwingUtilities.isRightMouseButton(e) || e.getButton() == MouseEvent.BUTTON3)
        {
            this.handlePopup(e);
            vv.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return;
        }

        vv.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void update(Observable o, Object arg)
    {
        Graph<ThreeWayGraphNode, ThreeWayGraphEdge> g = gtnv.getTheLayoutObject().getGraph();

        if (settings.getTraitEdgeThreshold() != this.traitEdgeThreshold)
        {
            double newThresh = settings.getTraitEdgeThreshold();
            ThreeWayEdgeManager.dealWithTraitEdgeThreshChange(newThresh, g, gtnv, this.traitEdgeThreshold,
                    isShowNodesWithNoAssoc);
            this.traitEdgeThreshold = newThresh;
            ThreeWayEdgeManager.setMaxValsAcrossEdgeGroups(g);
        }
        if (settings.getGeneEdgeThreshold() != this.geneEdgeThreshold)
        {
            double newThresh = settings.getGeneEdgeThreshold();
            ThreeWayEdgeManager.dealWithGeneEdgeThreshChange(newThresh, g, gtnv, this.geneEdgeThreshold,
                    isShowNodesWithNoAssoc);
            this.geneEdgeThreshold = newThresh;
            ThreeWayEdgeManager.setMaxValsAcrossEdgeGroups(g);
        }
        if (settings.getAssocEdgeThreshold() != this.assocEdgeThreshold)
        {
            double newThresh = settings.getAssocEdgeThreshold();
            ThreeWayEdgeManager.dealWithAssocEdgeThreshChange(newThresh, g, gtnv, this.assocEdgeThreshold,
                    isShowNodesWithNoAssoc);
            if (!isShowNodesWithNoAssoc && newThresh > assocEdgeThreshold)
            {
                ThreeWayEdgeManager.addOrRemoveNodesWOAssociation(g, isShowNodesWithNoAssoc, gtnv, traitEdgeThreshold, geneEdgeThreshold);
            }
            this.assocEdgeThreshold = newThresh;
            ThreeWayEdgeManager.setMaxValsAcrossEdgeGroups(g);
            gtnv.refresh(jp);
        }
        if (settings.getIsShowAssociationEdges() != this.isShowAssociationEdges)
        {
            this.isShowAssociationEdges = settings.getIsShowAssociationEdges();
            ThreeWayEdgeManager.dealWithChangingAssocEdgeVisiblity(g, isShowAssociationEdges, gtnv);
        }
        if (settings.getIsShowUnconnectedNodes() != this.isShowNodesWithNoAssoc)
        {
            this.isShowNodesWithNoAssoc = settings.getIsShowUnconnectedNodes();
            ThreeWayEdgeManager.addOrRemoveNodesWOAssociation(g, isShowNodesWithNoAssoc, gtnv, traitEdgeThreshold, geneEdgeThreshold);
        }
        if (settings.getIsShowAssocEdgeLabs() != this.isShowAssocEdgeLabs)
        {
            this.isShowAssocEdgeLabs = settings.getIsShowAssocEdgeLabs();
            ((ThreeWayEdgeLabelStringer) this.gtnv.getVV().getRenderContext().getEdgeLabelTransformer()).setAssocEnabeled(isShowAssocEdgeLabs);
        }
        if (settings.getIsShowGeneEdgeLabs() != this.isShowGeneEdgeLabs)
        {
            this.isShowGeneEdgeLabs = settings.getIsShowGeneEdgeLabs();
            ((ThreeWayEdgeLabelStringer) this.gtnv.getVV().getRenderContext().getEdgeLabelTransformer()).setGeneEnabled(isShowGeneEdgeLabs);
        }
        if (settings.getIsShowTraitEdgeLabs() != this.isShowTraitEdgeLabs)
        {
            this.isShowTraitEdgeLabs = settings.getIsShowTraitEdgeLabs();
            ((ThreeWayEdgeLabelStringer) this.gtnv.getVV().getRenderContext().getEdgeLabelTransformer()).setTraitEnabled(isShowTraitEdgeLabs);
        }
        if (settings.getIsShowGeneLabs() != this.isShowGeneLabs)
        {
            this.isShowGeneLabs = settings.getIsShowGeneLabs();
            ((VertexStringerImpl) gtnv.getVV().getRenderContext().getVertexLabelTransformer()).geneenabled = isShowGeneLabs;
        }
        if (settings.getIsShowTraitLabs() != this.isShowTraitLabs)
        {
            this.isShowTraitLabs = settings.getIsShowTraitLabs();
            ((VertexStringerImpl) gtnv.getVV().getRenderContext().getVertexLabelTransformer()).traitenabled = isShowTraitLabs;
        }
    }

    /**
     * Creates a new Trait group from the list of traits ... which have already
     * been removed from the main graph. 
     */
    private void createNewTraitGroup(ArrayList<ThreeWayTraitGraphObject> traits, Graph g, ThreeWayLayout twl,
            double x, double y)
    {
        ThreeWayTraitGroup twtg = new ThreeWayTraitGroup(traits); //, edges);
        for (ThreeWayTraitNode twtn : twtg.getTraits())
        {
            twtn.setTraitGroup(twtg);
        }
        g.addVertex(twtg);
        ThreeWayEdgeManager.collapseTraitEdges(twl, twtg, x, y, g, gtnv, traitEdgeThreshold, 
                isShowNodesWithNoAssoc, isShowAssociationEdges,assocEdgeThreshold);
        gtnv.setUpVisualizationRenderingStuff(gtnv.getVV().getGraphLayout().getGraph());
        gtnv.getVV().repaint();
        gtnv.getVV().getPickedVertexState().clear();
        gtnv.getVV().setPickedVertexState(gtnv.getVV().getPickedVertexState());
    }

    /**
     * Follow all present graph links connecting traits to find the connected
     * components of the given traits.
     */
    private void extractConnectedTraits(Graph g, ArrayList<ThreeWayTraitGraphObject> selectedTraits)
    {
        int size = selectedTraits.size();
        int oldsize = 0;
        while (oldsize != size)
        {
            oldsize = size;
            for (Object ed : g.getEdges())
            {
                if (!(ed instanceof ThreeWayCorrelationEdge))
                {
                    continue;
                }
                ThreeWayCorrelationEdge edge = (ThreeWayCorrelationEdge) ed;
                ArrayList<ThreeWayTraitGraphObject> toadd = new ArrayList<ThreeWayTraitGraphObject>();
                for (ThreeWayTraitGraphObject t : selectedTraits)
                {
                    if (t instanceof ThreeWayTraitNode && ((ThreeWayTraitNode) t).getTraitGroup() != null)
                    {
                        t = ((ThreeWayTraitNode) t).getTraitGroup();
                    }
                    if (t.equals(edge.getT1()) && !selectedTraits.contains((ThreeWayTraitGraphObject) edge.getT2()))
                    {
                        toadd.add((ThreeWayTraitGraphObject) edge.getT2());
                    }
                    else if (t.equals(edge.getT2()) && !selectedTraits.contains((ThreeWayTraitGraphObject) edge.getT1()))
                    {
                        toadd.add((ThreeWayTraitGraphObject) edge.getT1());
                    }
                }
                for (ThreeWayTraitGraphObject o : toadd)
                {
                    if (!selectedTraits.contains(o))
                    {
                        selectedTraits.add(o);
                        if (o instanceof ThreeWayTraitNode && ((ThreeWayTraitNode) o).getTraitGroup() != null)
                        {
                            selectedTraits.add(o);
                        }
                    }
                }
            }
            size = selectedTraits.size();
        }
    }

    /**
     * Follow all present graph links connecting traits to find the connected
     * components of the given traits.
     */
    private void extractConnectedGenes(Graph g, ArrayList<ThreeWayGeneGraphObject> selectedTraits)
    {
        int size = selectedTraits.size();
        int oldsize = 0;
        while (oldsize != size)
        {
            oldsize = size;
            for (Object ed : g.getEdges())
            {
                if (!(ed instanceof ThreeWayCorrelationEdge))
                {
                    continue;
                }
                ThreeWayCorrelationEdge edge = (ThreeWayCorrelationEdge) ed;
                ArrayList<ThreeWayGeneGraphObject> toadd = new ArrayList<ThreeWayGeneGraphObject>();
                for (ThreeWayGeneGraphObject t : selectedTraits)
                {
                    if (t instanceof ThreeWayGeneNode && !((ThreeWayGeneNode) t).getGroup().isExpanded())
                    {
                        t = ((ThreeWayGeneNode) t).getGroup();
                    }
                    if (t.equals(edge.getT1()) && !selectedTraits.contains((ThreeWayGeneGraphObject) edge.getT2()))
                    {
                        toadd.add((ThreeWayGeneGraphObject) edge.getT2());
                    }
                    else if (t.equals(edge.getT2()) && !selectedTraits.contains((ThreeWayGeneGraphObject) edge.getT1()))
                    {
                        toadd.add((ThreeWayGeneGraphObject) edge.getT1());
                    }
                }
                for (ThreeWayGeneGraphObject o : toadd)
                {
                    if (!selectedTraits.contains(o))
                    {
                        selectedTraits.add(o);
                        if (o instanceof ThreeWayGeneNode && !((ThreeWayGeneNode) o).getGroup().isExpanded())
                        {
                            selectedTraits.add(o);
                        }
                    }
                }
            }
            size = selectedTraits.size();
        }
    }

    /**
     * Collapse traits into a trait group
     */
    private void collapseTraits(ArrayList<ThreeWayTraitGraphObject> selectedTraits)
    {
        ArrayList<ThreeWayTraitGraphObject> traits = new ArrayList<ThreeWayTraitGraphObject>();
        Graph g = gtnv.getVV().getGraphLayout().getGraph();
        ThreeWayLayout twl = gtnv.getTheLayoutObject();
        double x = twl.getX(selectedTraits.get(0));
        double y = twl.getY(selectedTraits.get(0));
        for (ThreeWayTraitGraphObject twgn : selectedTraits)
        {
            traits.add(twgn);
            g.removeVertex(twgn);
        }
        createNewTraitGroup(traits, g, twl, x, y);
    }

    /**
     * Follow all present graph links connecting traits to find the connected
     * components of the given traits.
     */
    private void extractAssociatedTraits(Graph g, ArrayList<ThreeWayGeneGraphObject> selectedGenes,
            ArrayList<ThreeWayTraitGraphObject> selectedTraits)
    {
        int size = selectedTraits.size();
        int oldsize = -1;
        while (oldsize != size)
        {
            oldsize = size;
            for (Object ed : g.getEdges())
            {
                if (!(ed instanceof ThreeWayAssociationEdge))
                {
                    continue;
                }
                ThreeWayAssociationEdge edge = (ThreeWayAssociationEdge) ed;
                ArrayList<ThreeWayTraitGraphObject> toadd = new ArrayList<ThreeWayTraitGraphObject>();
                for (ThreeWayGeneGraphObject t : selectedGenes)
                {
                    if (t.equals(edge.getT1()) && !selectedTraits.contains((ThreeWayTraitGraphObject) edge.getT2()))
                    {
                        toadd.add((ThreeWayTraitGraphObject) edge.getT2());
                    }
                }
                for (ThreeWayTraitGraphObject o : toadd)
                {
                    if (!selectedTraits.contains(o))
                    {
                        selectedTraits.add(o);
                        if (o instanceof ThreeWayTraitNode && ((ThreeWayTraitNode) o).getTraitGroup() != null)
                        {
                            selectedTraits.add(o);
                        }
                    }
                }
            }
            size = selectedTraits.size();
        }
    }
}
