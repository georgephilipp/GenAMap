package views.network;

import control.NewDataNameGetter;
import control.itempanel.ThreadingItemFrame;
import control.itempanel.TraitTreeGoUpdateItem;
import datamodel.TraitTreeVal;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import static javax.swing.WindowConstants.HIDE_ON_CLOSE;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.picking.PickedState;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import views.AssociationView;
import views.GoInformationTable;
import views.GoResultsViewer;

/**
 * The TraitTreePopupHandler manages the popup menu for the tree
 * visualization. This allows the user to set the number of levels,
 * the mouse mode, going to the root, setting the visualization root,
 * going up a level, and switching to the radial view.
 * 
 * @author rcurtis
 */
public class TraitTreePopupHandler extends AntiScrollGraphMouseListener //AbstractPopupGraphMousePlugin implements GraphMouse
{
    /**
     * This is the popup menu that we will add items to and then display in the end
     */
    private JPopupMenu popup;
    /**
     * This is the pointer back to the tree view that we will use
     */
    private TreeView tv;
    /**
     * Are we in an association view mode? If so, we have different options to
     * display to the user
     */
    private boolean isAssocView;
    /**
     * We might determine that the click was on a label - and if so then we
     * will call the label popup handler to take care of it.
     */
    private NetworkLabelPopupHandler labelPopup;
    /**
     * The name of the trait
     */
    private StringPointer traitName;
    /**
     * The jpanel that the treeview is on. We pass this to the networklabelpopup
     */
    private JPanel jp;
    /**
     * This is the information table that we are presenting to the user.
     * We only want to display one on the screen at a time...
     */
    GoInformationTable table;

    /**
     * Create a new popup hanlder
     * @param treevw The TraitTree this class is intimately associated with
     * @param associated a boolean representing whether association options should be there
     * @param jp the JPanel to draw on. 
     */
    public TraitTreePopupHandler(TreeView treevw, boolean associated, JPanel jp)
    {
        super();
        popup = new JPopupMenu();
        this.tv = treevw;
        this.isAssocView = associated;
        labelPopup = new NetworkLabelPopupHandler();
        traitName = new StringPointer();
        this.jp = jp;

        //mouse object for picking the vertices
        this.setMode(Mode.PICKING);
        this.mouseWheelMoved(null);
    }

    /**
     * Sends up a popup menu and gives all the options. 
     * @param e
     */
//    @Override
    @SuppressWarnings(
    {
        "unchecked", "serial", "serial"
    })
    protected void handlePopup(MouseEvent e)
    {
        if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e) || e.getButton() == MouseEvent.BUTTON3)
        {
            Point2D isLabel = NetworkPopupHandler.checkLabel(e, traitName, 's');
            if (isLabel != null)
            {
                popup.removeAll();
                labelPopup = new NetworkLabelPopupHandler();
                labelPopup.handlePopup(e, traitName.s, jp);
            }
            else
            {
                popup.removeAll();
                final VisualizationViewer<TraitTreeVal, Number> vv =
                        (VisualizationViewer<TraitTreeVal, Number>) e.getSource();
                final Layout<TraitTreeVal, Number> layout = vv.getGraphLayout();
                final Point2D p = e.getPoint();
                GraphElementAccessor<TraitTreeVal, Number> pickSupport = vv.getPickSupport();

                final TraitTreeVal vertex = pickSupport.getVertex(layout, p.getX(), p.getY());
                final PickedState<TraitTreeVal> pickedVertexState = vv.getPickedVertexState();
                if (!pickedVertexState.isPicked(vertex) && vertex != null)
                {
                    pickedVertexState.pick(vertex, true);
                }
                if (pickSupport != null && vertex != null && pickedVertexState.getPicked().size() == 1)
                {
                    popup.add(new AbstractAction("See as root")
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            tv.makeThisNodeVisualRoot(0);
                        }
                    });
                    if(!vertex.getName() .equals( "Tree Root"))
                    {
                    popup.add(new AbstractAction("Up 1 Level")
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            tv.makeThisNodeVisualRoot(1);
                        }
                    });
                    }
                }
                if (pickSupport != null)
                {

                    popup.add(new AbstractAction("Go to root")
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            tv.goToRoot();
                        }
                    });

                    //if we are showing a marker trait association, then we need these options
                    if (isAssocView)
                    {
                        /*popup.add(new AbstractAction("Highlight Associated Markers")
                        {

                        public void actionPerformed(ActionEvent e)
                        {
                        tv.getParent().highlightAssociatedMarkers(vv.getPickedVertexState().getPicked());
                        }
                        });*/

                        popup.add(new AbstractAction("Reset Vertex Colors")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                vv.getRenderContext().setVertexFillPaintTransformer(new PickableVertexPaintTransformer<TraitTreeVal>(vv.getPickedVertexState(), Color.blue, Color.cyan));
                                vv.repaint();
                            }
                        });
                        Set<TraitTreeVal> picked = vv.getPickedVertexState().getPicked();
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
                                AssociationView.getCurrentRunningInstance().refreshView();

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

                        popup.add(rm);
                        /* popup.add(new AbstractAction("Delete Charts")
                        {

                        public void actionPerformed(ActionEvent e)
                        {
                        AssociationView.getCurrentRunningInstance().removeCharts();
                        AssociationView.getCurrentRunningInstance().refreshView();

                        }
                        });*/
                    }

                    popup.add(new AbstractAction("GO TO:")
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            NewDataNameGetter ndng = new NewDataNameGetter(jp.getParent(), true, "Enter the name of the gene to go to:", new ArrayList<String>());
                            ndng.show();
                            if(ndng.SUCCESS)
                            {
                                tv.makeThisNodeVisualRoot(1, null, ndng.newName);
                            }
                        }
                    });

                    //get selected traits
                   /* String projectName = nv.GetNetwork().getProjectName();
                    String traitName = nv.GetNetwork().getTraitName();
                    final AssociationProject assocProj = Model.getInstance().getProject(projectName);

                    ArrayList<Trait> selectedTraits = new ArrayList<Trait>();

                    for (Trait t : (Collection<Trait>) nv.getVisualizationViewer().getGraphLayout().getGraph().getVertices())
                    {
                    if (nv.getVisualizationViewer().getPickedVertexState().isPicked(t))
                    {
                    selectedTraits.add(t);
                    }
                    }
                    final TraitSet ts = assocProj.getTrait(traitName);
                    final ArrayList<Integer> selectedIndeces = getIndeces(selectedTraits);*/

                    JMenu levelMenu = new JMenu("Levels");
                    int lev = tv.getLevels();
                    if (lev != 3)
                    {
                        levelMenu.add(new AbstractAction("3")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                tv.setLevels(3);
                            }
                        });
                    }
                    if (lev != 4)
                    {
                        levelMenu.add(new AbstractAction("4")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                tv.setLevels(4);
                            }
                        });
                    }
                    if (lev != 5)
                    {
                        levelMenu.add(new AbstractAction("5")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                tv.setLevels(5);
                            }
                        });
                    }
                    if (lev != 6)
                    {
                        levelMenu.add(new AbstractAction("6")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                tv.setLevels(6);
                            }
                        });
                    }
                    if (lev != 7)
                    {
                        levelMenu.add(new AbstractAction("7")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                tv.setLevels(7);
                            }
                        });
                    }
                    if (lev != 8)
                    {
                        levelMenu.add(new AbstractAction("8")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                tv.setLevels(8);
                            }
                        });
                    }
                    popup.add(levelMenu);
                    JMenu setLayoutMenu = new JMenu("Set Layout");

                    if (tv.isRadialLayout())
                    {
                        setLayoutMenu.add(new AbstractAction("Tree Layout")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                tv.setLayout(false);
                            }
                        });
                    }
                    else
                    {
                        setLayoutMenu.add(new AbstractAction("Radial Layout")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                tv.setLayout(true);


                            }
                        });
                    }
                    popup.add(setLayoutMenu);

                    if (pickSupport != null && vertex != null && pickedVertexState.getPicked().size() == 1)
                    {
                        popup.add(new AbstractAction("Save traits as subset")
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                tv.createSubsetFromSelectedIdx();
                            }
                        });
                    }
                }

                popup.add(new AbstractAction("Custom GO Analysis")
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        ThreadingItemFrame form = ThreadingItemFrame.getInstance();
                        TraitTreeGoUpdateItem item = new TraitTreeGoUpdateItem(form, tv.getTree(), true);
                        ThreadingItemFrame.getInstance().addToThreadList(
                                item);
                        //form.addToThreadList(item);
                        form.setVisible(true);
                        form.setDefaultCloseOperation(HIDE_ON_CLOSE);
                    }
                });

                popup.add(new AbstractAction("Reset Vertex Colors")
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        tv.colorByGoCat();
                    }
                });

                if (popup.getComponentCount() > 0)
                {
                    popup.show((Component) vv, (int) p.getX(), (int) p.getY());
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        super.mouseReleased(e);
        if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e) || e.getButton() == MouseEvent.BUTTON3)
        {
            this.handlePopup(e);
            return;
        }
        final VisualizationViewer<TraitTreeVal, Number> vv =
                (VisualizationViewer<TraitTreeVal, Number>) e.getSource();
        Collection picked = vv.getPickedVertexState().getPicked();
        if (picked.size() > 1 || picked.size() == 0)
        {
            if (table != null)
            {
                //vv.remove(this.table);
                GoResultsViewer.getInstance().setVisible(false);
            }
            return;
        }
        for (Object v : picked)
        {
            if (v instanceof TraitTreeVal)
            {
                if (table != null)
                {
                    vv.remove(this.table);
                    GoResultsViewer.getInstance().setVisible(false);
                }
                TraitTreeVal cur = (TraitTreeVal) v;
                String nm = cur.toString();
                nm = nm.replaceAll("<*html>", "");
                nm = nm.replaceAll("<*br>", "");
                nm = nm.replaceAll("<*center>", "");
                nm = nm.replaceAll("</", "");
                GoResultsViewer.getInstance().setVisible(true, nm, cur.getGoCats(),
                        cur.getGoCatListForTrait(tv.currentGoMap), this.tv.currentGoMap, tv, 2);
                //vv.add(table, BorderLayout.NORTH);
                //vv.updateUI();
            }
        }
    }
}
