package views.network;

import BiNGO.GoItems;
import control.DataAddRemoveHandler;
import datamodel.Association;
import datamodel.AssociationSet;
import datamodel.Marker;
import datamodel.Model;
import realdata.DataManager;
import datamodel.Trait;
import datamodel.TraitTree;
import java.awt.event.ComponentEvent;
import views.*;
import java.awt.Color;
import java.awt.geom.Point2D;

import javax.swing.JPanel;
import java.util.*;

import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.subLayout.TreeCollapser;
import datamodel.TraitTreeVal;
import edu.uci.ics.jung.algorithms.layout.PolarPoint;

import edu.uci.ics.jung.graph.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.Shape;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;
import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import heatchart.HeatChart;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ComponentListener;
import javax.swing.JLabel;
import views.heatmap.HeatChartScale;
import views.snp.MyPickableVertexPaintTransformer;

/**
 * The TreeView allows the user to browse data structured as a tree. The tree
 * can be loaded in or can be created using our Hierarchical clustering algo.
 *
 * The TreeView functions the same way as other visualizations - it draws
 * itself onto the JPanel. Here we provide commands to browse through
 * the tree - as these trees can be thousands of levels deep, we want the tree
 * to only display a handful of level onto the screen at a time. 
 * @author jorge
 */
public class TreeView extends Visualization implements ComponentListener, GoListener
{
    /**
     * The JUNG visualization object that we use to draw the tree.
     */
    private VisualizationViewer<TraitTreeVal, String> vv2;
    /**
     * The representation of the tree object that we make for JUNG.
     */
    private DelegateForest tree;
    /**
     * A pointer to the JPanel that we draw the vv2 object onto. This allows
     * us to call methods back in the overall view - like those for associations
     */
    private JPanel jp;
    /**
     * The layout for viewing the tree in a radial view.
     */
    private MyRadialTreeLayout<TraitTreeVal, String> radialLayout;
    /**
     * The layout for viewing the tree in a top-down view.
     */
    private Layout<TraitTreeVal, String> layout;
    /**
     * Rings that we draw to show the levels of the radial layout.
     */
    private VisualizationServer.Paintable rings;
    /**
     * Not used in this implementaiton, but could be used in future versions.
     */
    private TreeCollapser collapser;
    /**
     * root of the whole tree. For ease of reference
     */
    private TraitTreeVal TTERoot;
    /**
     * A pointer to our traittree object that stores its id, etc.
     */
    private TraitTree tt;
    /**
     * We keep track of whether or not we are in association view mode. If we
     * are, then the user has the option to color the nodes according to the
     * association strength. 
     */
    private boolean isAssocView;
    /**
     * The user can choose to be in either picking or transforming mode.
     */
    private Mode mode = Mode.PICKING;
    /**
     * The user can display 3 to 8 levels of the tree at any one time. We
     * keep track of the current selection with this variable.
     */
    private int level = 5;
    /**
     * For all tree actions, the user utilizes the popup handler, which
     * communicates directly with this calss.
     */
    private TraitTreePopupHandler popupHandler;
    /**
     * A boolean to keep track of the current tree we are in.
     */
    private boolean isRadialLayout = false;
    /**
     * To keep track of which node is the root of the currently displayed
     * tree - useful when we are browsing through the tree.
     */
    private TraitTreeVal cur;
    /**
     * For visualization purposes, we keep track of the maximum number of nodes
     * across any one level in the shown tree. 
     */
    private int numLastLevNodes;
    /**
     * When specified by the user, we color the tree according to the associatoin
     * of the nodes to certain markers. 
     * When in tree view, we save these so we can requery whenever the
     * tree is adjusted, we thenreset the color map. 
     */
    private ArrayList<Marker> markers;
    /**
     * When specified by the user, we color the tree according to the associatoin
     * of the nodes to certain markers.
     * When in tree view, we save these so we can requery whenever the
     * tree is adjusted, we thenreset the color map.
     */
    private AssociationSet assocs;
    /**
     * To scale the size
     */
    private int scaleSizeFactor = 2;
    /**
     * The current colormap of the nodes displayed on screen. 
     */
    private Map<TraitTreeVal, Color> colormap;
    private AssociationColorKey myscale;

    /**
     * Creates a new TreeView object.
     * @param av
     */
    public TreeView(AssociationView av)
    {
        jp = av;
        myscale = av.getMyAssocScale();
    }
    /**
     * A poitner to the panel to switch between populations in a population view
     */
    private PopSelectionPanel popselpanel;
    /**
     * The coloring of the nodes for the GO categories currently displayed.
     * String is the name of the go category, and Color is the color assigned to it.
     */
    public Map<String, Color> currentGoMap;

    /**
     * Builds the tree visualization from the root down to the number of levels
     * specified by the user. This queries the database to find out the
     * values and properties of the ndoes to be displayed. 
     * @param root the node acting as the visual root
     * @param levelsToDisplay the number of levels to show
     * @param cols an array list of the columns to display, passed for convenience.
     */
    private void buildTTETree(TraitTreeVal root, int levelsToDisplay, ArrayList<String> cols)
    {
        //-------------RETRIEVING THE OTHER NODES BESIDES THE ROOT AND BUILDING THE TREE------------
        Queue<TraitTreeVal> q = new LinkedList<TraitTreeVal>();
        ArrayList<String> whereArgs = new ArrayList<String>();
        ArrayList<HashMap<String, String>> queryResults = new ArrayList<HashMap<String, String>>();

        TraitTreeVal Parent = q.poll();
        Integer currentTTEId = null;
        String currentName = null;
        Integer childLevel = null;
        Trait currentTrait = null;

        int currentLevel = root.getLevel();
        int targetLevel = currentLevel + levelsToDisplay - 1;
        int phonyLevel = targetLevel - currentLevel;

        q.offer(root);
        ArrayList<Integer> numLevNodes = new ArrayList<Integer>();
        for (int i = 0; i <= 8; i++)
        {
            numLevNodes.add(0);
        }

        while (!q.isEmpty() && currentLevel <= targetLevel)
        {
            whereArgs.clear();
            queryResults.clear();

            Parent = q.poll();
            currentLevel = Parent.getLevel();
            phonyLevel = targetLevel - currentLevel;

            numLevNodes.set(phonyLevel, numLevNodes.get(phonyLevel) + 1);
            if (currentLevel == targetLevel)
            {
                continue;
            }

            whereArgs.add("ttid=" + Integer.toString(tt.getId()));
            whereArgs.add("parentid=" + Parent.getId());
            queryResults = DataManager.runMultiColSelectQuery(cols, "traittreeval", true, whereArgs, null);

            //adding children.
            for (int i = 0; i < queryResults.size(); i++)
            {

                currentTTEId = Integer.parseInt(queryResults.get(i).get("id"));
                currentName = "Node " + currentTTEId;
                childLevel = Parent.getLevel() + 1;
                currentTrait = null; //a non-leaf node has no Trait
                //adding Trait
                if (!queryResults.get(i).get("traitid").equals("-1"))
                {
                    currentTrait = new Trait(Integer.parseInt(queryResults.get(i).get("traitid")));
                }
                TraitTreeVal child = new TraitTreeVal(currentTTEId, currentTrait, Parent, childLevel,
                        this.tt, currentName, Integer.parseInt(queryResults.get(i).get("golist")), Parent.getId());
                child.setParent(Parent);
                Parent.addChild(child);

            }

            if (Parent.getChildren() != null)
            {
                for (TraitTreeVal child : Parent.getChildren())
                {
                    q.offer(child);

                }//end of adding children to queue (for loop)

            }//end of if block
        }
        this.numLastLevNodes = 0;
        for (int i : numLevNodes)
        {
            if (i > numLastLevNodes)
            {
                numLastLevNodes = i;
            }
        }

        /*if(this.level == 5 && numLastLevNodes > 4 ||
        this.level == 4 && numLastLevNodes > 2 ||
        this.level == 6 && numLastLevNodes > 8 ||
        this.level == 7 && numLastLevNodes > 16 ||
        this.level == 8 && numLastLevNodes > 32)*/
        {
            switch (this.level)
            {
                case 3:
                    numLastLevNodes = Math.max(4, numLastLevNodes);
                    break;
                case 4:
                    numLastLevNodes = Math.max(8, numLastLevNodes);
                    break;
                case 5:
                    numLastLevNodes = Math.max(16, numLastLevNodes);
                    break;
                case 6:
                    numLastLevNodes = Math.max(32, numLastLevNodes);
                    break;
                case 7:
                    numLastLevNodes = Math.max(64, numLastLevNodes);
                    break;
                case 8:
                    numLastLevNodes = Math.max(128, numLastLevNodes);
                    break;
            }
        }

    }//end of function

    /**
     * Init the visualization object.
     */
    private void initializeVV()
    {

        this.vv2 = new VisualizationViewer<TraitTreeVal, String>(layout);
        this.vv2.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
        this.vv2.getRenderContext().setEdgeArrowPredicate(new myPredicate());
        this.vv2.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        this.vv2.getRenderContext().setVertexShapeTransformer(new ClusterVertexShapeFunction());
        this.vv2.setVertexToolTipTransformer(new ToStringLabeller());
        this.vv2.getRenderer().getVertexLabelRenderer().setPosition(Position.S);
        this.vv2.getRenderContext().setVertexFillPaintTransformer(new PickableVertexPaintTransformer<TraitTreeVal>(vv2.getPickedVertexState(), Color.blue, Color.pink));

        this.vv2.setBackground(jp.getBackground());
    }

    /**
     * This method is called from the AssociationView object to tell the tree
     * view to draw a certain tree on itself
     * @param jp The JPanel on which to draw the tree
     * @param tt_p The trait tree pointer to draw.
     * @param associated Whether or not we are part of an associated view
     * @param ttLayoutLoc where to put the visualization view
     * @param parent Redundant parameter.
     * @param if the scale should be adjusted, we can adjust it here.
     */
    public void drawTreeOnCanvas(JPanel jp, TraitTree tt_p, boolean associated,
            String ttLayoutLoc, AssociationView parent, int scale, boolean isPop, int noPop)
    {
        if (jp == null)
        {
            throw new NullPointerException();
        }

        if (!tt_p.isUpdated())
        {
            jp.add(new JLabel("Resolution data not ready yet ..."));
            return;
        }
        myscale.setVisible(false);
        this.scaleSizeFactor = 2 + scale;
        this.isAssocView = associated;
        assocs = null;
        markers = null;
        this.tt = tt_p;
        ArrayList<String> cols = new ArrayList<String>();
        cols.add("id");
        cols.add("traitid");
        cols.add("golist");

        ArrayList<String> whereArgs = new ArrayList<String>();
        whereArgs.add("ttid=" + Integer.toString(tt.getId()));
        whereArgs.add("parentid=1");

        //--------------RETRIEVING THE ROOT------------------
        //creating root parent. for debugging purposes
        TraitTreeVal Parent = new TraitTreeVal();
        Parent.setId(1);
        Parent.setName("rootparent");//for debugging purposes.

        ArrayList<HashMap<String, String>> queryResults = DataManager.runMultiColSelectQuery(cols, "traittreeval", true, whereArgs, null);

        //Creating root, initializing its fields
        int currentTTEId = Integer.parseInt(queryResults.get(0).get("id"));
        int currentLevel = 1;
        Trait currentTrait = null; //a non-leaf node has no Trait
        //in the (rare) case that the root is also a leaf (ie one element tree) we need to get its Trait
        if (!queryResults.get(0).get("traitid").equals("-1"))
        {
            currentTrait = new Trait(Integer.parseInt(queryResults.get(0).get("traitid")));
        }

        String currentName = "ROOT";

        TraitTreeVal root = new TraitTreeVal(currentTTEId, currentTrait, Parent, currentLevel,
                tt, currentName, Integer.parseInt(queryResults.get(0).get("golist")), Parent.getId());
        root.setParent(Parent);

        level = 5;

        this.TTERoot = (root);//needs to be called once, before the first time the tree is visualized.
        cur = root;

        buildTTETree(root, level, cols);
        //-----------BUILDING THE JUNG VISUALIZATION TREE-------------
        this.tree = buildJUNGTree(root);

        this.jp = jp;
        this.layout = new TreeLayout(tree, jp.getWidth() / (this.numLastLevNodes + 1), jp.getHeight() / (level + this.scaleSizeFactor));
        this.radialLayout = new MyRadialTreeLayout(tree);

        this.rings = new Rings();
        this.initializeVV();
        this.colorByGoCat();
        setMode(Mode.PICKING);
        //setLayout(this.isRadialLayout);
        resize();

        this.jp.add(vv2, ttLayoutLoc);
        if (isPop)
        {
            popselpanel = new PopSelectionPanel(noPop, this, jp.getBackground(), false);
            popselpanel.setVisible(false);
            vv2.add(popselpanel, BorderLayout.EAST);
        }
        vv2.addComponentListener(this);

        popupHandler = new TraitTreePopupHandler(this, associated, vv2);
        vv2.setGraphMouse(popupHandler);
    }

    /**
     * Not currently used
     */
    private void collapse()
    {
        Collection picked = new HashSet(vv2.getPickedVertexState().getPicked());
        if (picked.size() == 1)
        {
            Object troot = picked.iterator().next();
            Forest inGraph = (Forest) layout.getGraph();

            try
            {
                collapser.collapse(vv2.getGraphLayout(), inGraph, troot);
            }
            catch (InstantiationException e1)
            {
                e1.printStackTrace();
            }
            catch (IllegalAccessException e1)
            {
                e1.printStackTrace();
            }

            vv2.getPickedVertexState().clear();
            vv2.repaint();
        }
    }

    /**
     * Not currently used.
     */
    private void expand()
    {
        Collection picked = vv2.getPickedVertexState().getPicked();
        for (Object v : picked)
        {
            if (v instanceof Forest)
            {
                Forest inGraph = (Forest) layout.getGraph();
                collapser.expand(inGraph, (Forest) v);
            }
            vv2.getPickedVertexState().clear();
            vv2.repaint();
        }
    }

    /**
     * Returns the AssociationView object association with this TreeView.
     * @return
     */
    public AssociationView getParent()
    {
        return (AssociationView) this.jp;
    }

    /**
     * Called by the popup handler when a user changes the number of
     * levels. This method will set the number of visible levels on the
     * screen and redraw the tree.
     * @param lev
     */
    public void setLevels(int lev)
    {
        this.level = lev;

        this.makeThisNodeVisualRoot(0, cur);
        resize();

        if (this.isRadialLayout)
        {
            vv2.removePreRenderPaintable(rings);
        }
        rings = new Rings();
        if (this.isRadialLayout)
        {
            vv2.addPreRenderPaintable(rings);
        }
    }

    /**
     * Called by the popup handler to find out how many levels are displayed.
     * @return
     */
    public int getLevels()
    {
        return this.level;
    }

    /**
     * Returns the poitner to the traittree visualized by this view. 
     * @return
     */
    public TraitTree getTree()
    {
        return this.tt;
    }

    /**
     * Called by the popup handler to find out which layout is currently displayed.
     * @return
     */
    public boolean isRadialLayout()
    {
        return this.isRadialLayout;
    }

    /**
     * Called by the popup handler when the user sets the layout of the tree.
     * This redraws the tree using the new layout. 
     * @param isSetRadLayout
     */
    public void setLayout(boolean isSetRadLayout)
    {
        if (isSetRadLayout)
        {
            this.radialLayout.setSize(vv2.getSize());
            vv2.setGraphLayout(radialLayout);
            vv2.getRenderContext().getMultiLayerTransformer().setToIdentity();
            this.isRadialLayout = true;
        }
        else
        {
            vv2.setGraphLayout(layout);
            vv2.getRenderContext().getMultiLayerTransformer().setToIdentity();
            vv2.removePreRenderPaintable(rings);
            this.isRadialLayout = false;
        }
        if (this.isRadialLayout)
        {
            vv2.removePreRenderPaintable(rings);
        }
        rings = new Rings();
        if (this.isRadialLayout)
        {
            vv2.addPreRenderPaintable(rings);
        }
        vv2.repaint();
    }

    /**
     * Returns teh current graph control mode. 
     * @return
     */
    public Mode getMode()
    {
        return this.mode;
    }

    /**
     * Sets the current graph ctonrol mode - called by popup handler.
     * @param newMode
     */
    public void setMode(Mode newMode)
    {
        mode = newMode;
        //final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();
        //graphMouse.setMode(mode);
        //vv2.setGraphMouse(graphMouse);
    }

    /**
     * Called when the jpanel resizes or the number of levels changes. This
     * method makes the tree fit on the screen. 
     */
    public void resize()
    {
        if (layout != null)
        {
            this.layout = new TreeLayout(tree, jp.getWidth() / (this.numLastLevNodes + 1), jp.getHeight() / (level + scaleSizeFactor));
            this.radialLayout = new MyRadialTreeLayout(tree,
                    Math.min(jp.getHeight(), jp.getWidth()) / (level + 2));
            setLayout(this.isRadialLayout);
        }
    }

    /**
     * Changes and updates the tree so that it is displayed from the selected
     * node or moves up a level.
     * @param function 0 is we want tChanges and updates the tree so that it is displayed from the selected
     * node or moves up a level.o make the selected node root, and 1 if
     * we want to go up a level
     */
    public void makeThisNodeVisualRoot(int function)
    {
        makeThisNodeVisualRoot(function, null, null);
    }

    public void makeThisNodeVisualRoot(int function, TraitTreeVal node)
    {
        makeThisNodeVisualRoot(function, node, null);
    }

    /**
     * Returns a trait tree val object that has the given name.
     * @param name
     * @return
     */
    @SuppressWarnings("empty-statement")
    public TraitTreeVal getNodeWithName(String name)
    {
        int id;
        Trait traitid;
        int lev;
        TraitTree ttid = this.TTERoot.getTraitTree();
        int golist;

        ArrayList<String> where = new ArrayList<String>();
        where.add("name=\"" + name + "\"");
        where.add("traitsetid=" + this.TTERoot.getTraitTree().getTraitSet().getId());

        ArrayList<String> ans = DataManager.runSelectQuery("id", "trait", true, where, null);
        traitid = this.TTERoot.getTraitTree().getTraitSet().getTrait(Integer.parseInt(ans.get(0)));

        where.clear();
        where.add("traitid=" + traitid.getId());
        ArrayList<String> cols = new ArrayList<String>();
        cols.add("id");
        cols.add("parentid");
        cols.add("level");
        cols.add("golist");

        ArrayList<HashMap<String, String>> res = DataManager.runMultiColSelectQuery(cols, "traittreeval", true, where, null);

        id = Integer.parseInt(res.get(0).get("id"));
        lev = Integer.parseInt(res.get(0).get("level"));
        golist = Integer.parseInt(res.get(0).get("golist"));
        int pid = Integer.parseInt(res.get(0).get("parentid"));


        return new TraitTreeVal(id, traitid, null, lev, ttid, name, golist, pid);
    }

    /**
     * Changes and updates the tree so that it is displayed from the selected
     * node or moves up a level.
     * @param function 0 is we want tChanges and updates the tree so that it is
     * displayed from the selected
     * node or moves up a level.o make the selected node root, and 1 if
     * we want to go up a level
     * @param node the node that was selected for the function. If null,
     * then we look to the vv2 object.
     */
    public void makeThisNodeVisualRoot(int function, TraitTreeVal node, String nodename)
    {
        vv2.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //this returns the traittreeeval associated with the visual node
        Collection picked = vv2.getPickedVertexState().getPicked();

        if ((picked.size() > 1 || picked.size() == 0) && node == null && nodename == null)
        {
            return;
        }
        if (node != null && nodename == null)
        {
            picked = new ArrayList<TraitTreeVal>();
            picked.add(node);
        }
        if (nodename != null)
        {
            picked = new ArrayList<TraitTreeVal>();
            node = getNodeWithName(nodename);
            if (node != null)
            {
                picked.add(node);
            }
            else
            {
                return;
            }
        }
        for (Object v : picked)
        {
            if (v instanceof TraitTreeVal)
            {
                cur = (TraitTreeVal) v;
                ArrayList<String> cols = new ArrayList<String>();
                cols.add("id");
                cols.add("traitid");
                cols.add("golist");

                this.tree = null;

                TraitTreeVal oldNode = (TraitTreeVal) v;
                TraitTreeVal temp = oldNode; //this is for initializtion purposes only. temp will
                //be overwritten; if i don't do this java will complain
                if (function == 0)//make this root
                {
                    temp = new TraitTreeVal(oldNode.getId(), oldNode.getTrait(), oldNode.getParent(),
                            oldNode.getLevel(), this.tt, oldNode.getName(), oldNode.getGoCode(), oldNode.getParent().getId());

                }
                else
                {
                    if (function == 1)//move one up
                    {
                        if (oldNode.getId() != this.TTERoot.getId())
                        {
                            oldNode = oldNode.getParent();
                            cur = oldNode;
                            temp = new TraitTreeVal(oldNode.getId(), oldNode.getTrait(), oldNode.getParent(),
                                    oldNode.getLevel(), this.tt, oldNode.getName(), oldNode.getGoCode(), oldNode.getParent().getId());
                        }
                    }
                    else
                    {
                        if (function == -1)
                        {
                            if (oldNode.getChildren() != null)
                            {
                                oldNode = oldNode.getChildren().get(0);
                                temp = new TraitTreeVal(oldNode.getId(), oldNode.getTrait(), oldNode.getParent(),
                                        oldNode.getLevel(), this.tt, oldNode.getName(), oldNode.getGoCode(), oldNode.getParent().getId());
                            }
                        }
                    }
                }

                cur = temp;
                buildTTETree(temp, level, cols);

                this.tree = this.buildJUNGTree(temp);
                this.colorByGoCat();
                resize();
            }

            vv2.getPickedVertexState().clear();
        }
        vv2.setCursor(Cursor.getDefaultCursor());

        if (this.assocs != null)
        {
            this.markUpByAssociation(assocs, markers, -1);
        }
    }

    /**
     * Takes the tree back to the overall root. 
     */
    public void goToRoot()
    {
        this.tree = null;
        TTERoot.setName("ROOT");
        this.makeThisNodeVisualRoot(0, TTERoot);
    }

    /**
     * Called for by the popup handler - initiates the creation of a subset
     * based on the node that was selected. 
     */
    public void createSubsetFromSelectedIdx()
    {
        Collection picked = vv2.getPickedVertexState().getPicked();

        if ((picked.size() > 1 || picked.size() == 0))
        {
            return;
        }
        for (Object v : picked)
        {
            if (v instanceof TraitTreeVal)
            {
                cur = (TraitTreeVal) v;
                cur.createSubset(this.tt.getTraitSet(), jp);
                DataAddRemoveHandler.getInstance().refreshDisplay();
            }
        }
    }

    @Override
    public void setPop(int popNo)
    {
        this.markUpByAssociation(assocs, markers, popNo);
    }

    /**
     * Called by the GoInformation table on a selection event. We can adjust
     * what we are showing based on what the function gives to us. 
     * @param cat
     * @param c
     */
    public void colorByCat(String cat, Color c)
    {
        this.colorByGoCat(cur, cat, c);
        if (colormap != null)
        {
            vv2.getRenderContext().setVertexFillPaintTransformer(new MyPickableVertexPaintTransformer<TraitTreeVal>(vv2.getPickedVertexState(), colormap, Color.black, Color.pink));
            vv2.repaint();
        }
    }
    /**
     * A class of a pair, a double and an int
     */
    public class Pair
    {
        public double d;
        public int i;

        /**
         * Creates a new pair
         * @param d a double
         * @param i an int
         */
        public Pair(double d, int i)
        {
            this.d = d;
            this.i = i;
        }
    }

    /**
     * Given an association set, and a set of markers to which we want to find
     * associations, we determine now to color the nodes in the tree. We will
     * need to consider this method as we browse through the tree as well. 
     * @param ac
     * @param markers
     */
    public void markUpByAssociation(AssociationSet ac, ArrayList<Marker> markers, int pop)
    {
        if (popselpanel != null && pop == -1)
        {
            popselpanel.setToDefautMode();
        }
        this.assocs = ac;
        this.markers = markers;
        ArrayList<Integer> markerID = new ArrayList<Integer>();
        for (Marker m : markers)
        {
            markerID.add(m.getId());
        }
        HashMap<TraitTreeVal, ArrayList<Integer>> assignments = getLeafTraitList();
        ArrayList<Integer> indxs = new ArrayList<Integer>();

        for (ArrayList<Integer> a : assignments.values())
        {
            for (Integer i : a)
            {
                indxs.add(i);
            }
        }

        Collection<Association> assoc = ac.findAssociations(markerID, indxs, pop);
        Map<Integer, Pair> map = new HashMap<Integer, Pair>();
        double maxVal = 0.0;
        double minVal = 1e99;
        double absMinVal = 0.0;//1e99;
        for (Association a : assoc)
        {
            if (map.get(a.getTraitId()) == null)
            {
                map.put(a.getTraitId(), new Pair(Math.abs(a.getValue()), a.getPopNo()));
            }
            else
            {
                Double d;
                if (!ac.getIsPvals())
                {
                    d = java.lang.Math.max(Math.abs(a.getValue()), map.get(a.getTraitId()).d);
                }
                else
                {
                    d = java.lang.Math.min(Math.abs(a.getValue()), map.get(a.getTraitId()).d);
                }
                if (d == a.getValue())
                {
                    map.put(a.getTraitId(), new Pair(d, a.getPopNo()));
                }
            }
            double val = Math.abs(a.getValue());
            if (Math.abs(val) > maxVal)
            {
                maxVal = val;
            }
            if (Math.abs(val) < absMinVal)
            {
                absMinVal = Math.abs(val);
            }
            if (val < minVal && val != 0)
            {
                minVal = val;
            }
        }

        setColorMap(map, ac.getIsPvals(), maxVal, absMinVal, assignments, ac.isPopAssocSet(), minVal);
        if (this.popselpanel != null)
        {
            this.popselpanel.setVisible(true);
        }
    }

    /**
     * Determines the list of traits that are associated with the leaves of
     * this tree. This is all we need, since we can traverse up the tree
     * with the traits that we get from this!
     * @return
     */
    private HashMap<TraitTreeVal, ArrayList<Integer>> getLeafTraitList()
    {
        Collection<TraitTreeVal> ruts = tree.getRoots();
        Queue<TraitTreeVal> queue = new LinkedList<TraitTreeVal>();
        HashMap<TraitTreeVal, ArrayList<Integer>> traitsForLeafs = new HashMap<TraitTreeVal, ArrayList<Integer>>();
        for (TraitTreeVal root : ruts)
        {
            queue.add(root);
        }

        while (queue.size() > 0)
        {
            TraitTreeVal w = queue.poll();

            Collection<TraitTreeVal> chillins = tree.getChildren(w);
            if (chillins.size() == 0)
            {
                ArrayList<Integer> al;

                if (w.getTrait() != null)
                {
                    al = new ArrayList<Integer>();
                    al.add(w.getTrait().getId());
                }
                else
                {
                    al = w.getTraitsUnderNode();
                }
                traitsForLeafs.put(w, al);
            }
            else
            {
                for (TraitTreeVal q : chillins)
                {
                    queue.add(q);
                }
            }
        }

        return traitsForLeafs;
    }

    private void setColorMap(Map<Integer, Pair> map, boolean isPval, double max, double min,
            HashMap<TraitTreeVal, ArrayList<Integer>> assignments, boolean isPop, double logmn)
    {
        Map<TraitTreeVal, Color> colormap = new HashMap<TraitTreeVal, Color>();
        Map<TraitTreeVal, Float> vals = new HashMap<TraitTreeVal, Float>();
        HashMap<Integer, TraitTreeVal> assigned = reverseHashMapOfAssignments(assignments);

        float logmin = (float) (-1 * Math.log10(0.05));
        float logmax = 1;
        if (logmn != 0)
        {
            logmax = (float) (-1 * Math.log10(logmn));
        }

        for (int t : map.keySet())
        {

            TraitTreeVal t2 = null;
            Collection<TraitTreeVal> vs = vv2.getGraphLayout().getGraph().getVertices();
            Double val = map.get(t).d;
            Integer popno = map.get(t).i;
            for (TraitTreeVal ttv : vs)
            {
                if (isTraitUnderNode(assigned.get(t), ttv))
                {
                    t2 = ttv;
                    //black for value of 0, white for 1
                    float value = Math.abs(val.floatValue());

                    if (isPval)
                    {
                        if (value != 0)
                        {
                            value = (float) (-1 * Math.log10(value));
                            value = (value - logmin) / (logmax - logmin);
                        }
                    }
                    else
                    {
                        value = (float) ((value - min) / (max - min));
                    }
                    //ROUNDING ERROR!!
                    if (value < 0)
                    {
                        value = 0;
                    }
                    if (value > 1)
                    {
                        value = 1;
                    }

                    float Gini = 1.0f;
                    float Bini = 1.0f;
                    float Rini = 1.0f;
                    if (isPop)
                    {
                        Color[] colors = Model.colors;
                        Gini = (float) colors[popno - 1].getGreen() / 255.0f;
                        Bini = (float) colors[popno - 1].getBlue() / 255.0f;
                        Rini = (float) colors[popno - 1].getRed() / 255.0f;
                    }
                    /*else
                    {
                    Color c = colormap.get(ttv);
                    if (c == null)
                    {
                    c = Color.WHITE;
                    }
                    Gini = c.getGreen() / 255.0f;
                    Rini = c.getRed() / 255.0f;
                    Bini = c.getBlue() / 255.0f;
                    }*/
                    value = value / 2f;
                    value = value + .5f;
                    float G = Gini * value;
                    float B = Bini * value;
                    float R = Rini * value;
                    Color c = new Color(R, G, B);
                    /*if(colormap.get(t2) != null)
                    {
                    int d = colormap.get(t2).getGreen();
                    System.out.println(d + " " + c.getGreen());
                    }*/
                    if (colormap.get(t2) == null || (vals.get(t2) < value))
                    {
                        colormap.put(t2, c);
                        vals.put(t2, value);
                    }
                }
            }
        }

        if (!isPval)
        {
            myscale.setTraitAssociation(HeatChartScale.DrawScale(0.0,
                    max + max / 100.0, 366,
                    HeatChart.SCALE_LINEAR, Color.GRAY, Color.WHITE, 10, Color.WHITE));
        }
        else
        {
            myscale.setTraitAssociation(HeatChartScale.DrawScale(0.0,
                    logmax, 366, HeatChart.SCALE_LINEAR, Color.GRAY,
                    Color.WHITE, 10, Color.WHITE));
        }

        vv2.getRenderContext().setVertexFillPaintTransformer(new MyPickableVertexPaintTransformer<TraitTreeVal>(vv2.getPickedVertexState(), colormap, Color.black, Color.pink));
        vv2.repaint();
    }

    public void colorByGoCat()
    {
        colormap = colorByGoCat(cur, "", null);
        if (colormap != null)
        {
            vv2.getRenderContext().setVertexFillPaintTransformer(new MyPickableVertexPaintTransformer<TraitTreeVal>(vv2.getPickedVertexState(), colormap, Color.black, Color.pink));
            vv2.repaint();
        }
    }

    private Map<TraitTreeVal, Color> colorByGoCat(TraitTreeVal curr, String mainCat, Color c)
    {
        if (this.isAssocView && this.assocs != null && this.assocs.isPopAssocSet())
        {
            return new HashMap<TraitTreeVal, Color>();
        }
        ArrayList<BiNGO.GoItems> gocats = curr.getGoCats();
        Collection<TraitTreeVal> chillins = tree.getChildren(curr);
        LinkedList queue = new LinkedList();
        LinkedList iniqueue = new LinkedList();
        colormap = new HashMap<TraitTreeVal, Color>();

        if (chillins == null || chillins.size() == 0)
        {
            return null;
        }
        iniqueue.add(curr);
        for (TraitTreeVal ttv : chillins)
        {
            queue.add(ttv);
            iniqueue.add(ttv);
        }

        this.currentGoMap = new HashMap<String, Color>();
        Color[] colors = Model.colors;
        int idx = 0;
        while (idx < 23 && iniqueue.size() > 0)
        {
            TraitTreeVal val = (TraitTreeVal) iniqueue.poll();
            if (val.getTrait() == null)
            {
                ArrayList<GoItems> golist = val.getGoCats();
                int sidx = 0;
                for (int i = 0; i < golist.size() && idx < 23 && sidx < 6; i++)
                {
                    if (currentGoMap.get(golist.get(i).descr) == null)
                    {
                        currentGoMap.put(golist.get(i).descr, colors[idx++]);
                        sidx++;
                    }
                }
            }
            else
            {
                ArrayList<String> golist = val.getGoCatListForTrait(null);
                int sidx = 0;
                for (int i = 0; i < golist.size() && idx < 23 && sidx < 6; i++)
                {
                    if (currentGoMap.get(golist.get(i)) == null)
                    {
                        currentGoMap.put(golist.get(i), colors[idx++]);
                        sidx++;
                    }
                }
            }
            chillins = tree.getChildren(val);
            if (chillins != null && chillins.size() != 0)
            {
                for (TraitTreeVal ttv : chillins)
                {
                    iniqueue.add(ttv);
                }
            }
        }

        if (gocats.size() > 0)
        {
            float G = (float) colors[0].getGreen() / 255.0f;
            float B = (float) colors[0].getBlue() / 255.0f;
            float R = (float) colors[0].getRed() / 255.0f;
            Color co = new Color(R, G, B);
            colormap.put(curr, co);
        }

        for (GoItems go : gocats)
        {
            if (go.descr.equals(mainCat))
            {
                colormap.put(curr, c);
            }
        }

        while (queue.size() > 0)
        {
            TraitTreeVal node = (TraitTreeVal) queue.poll();

            ArrayList<GoItems> items = node.getGoCats();
            if (items != null && items.size() > 0)
            {
                colormap.put(node, Color.white);
                GoItems top = items.get(0);

                for (int i = 0; i < items.size(); i++)
                {
                    if (items.get(i).descr.equals(mainCat))
                    {
                        top = items.get(i);
                    }
                }

                Color cl = currentGoMap.get(top.descr);
                if (cl != null)
                {
                    colormap.put(node, cl);
                }
                /*for (int i = 0; i < 23 && i < gocats.size(); i++)
                {
                if (top.GO_ID.equals(gocats.get(i).GO_ID))
                {
                G = (float) colors[i].getGreen() / 255.0f;
                B = (float) colors[i].getBlue() / 255.0f;
                R = (float) colors[i].getRed() / 255.0f;
                c = new Color(R, G, B);
                colormap.put(node, c);
                i = 100;
                }
                }*/
            }
            else if (node.getTrait() != null)
            {
                Trait t = node.getTrait();
                ArrayList<String> gos = t.getCurrentGoAnnotation(null);
                if (gos.size() > 0)
                {
                    colormap.put(node, Color.white);
                }

                for (String s : gos)
                {
                    /*for (int i = 0; i < gocats.size() && i < 23; i++)
                    {
                    if (s.equals(gocats.get(i).descr))
                    {
                    G = (float) colors[i].getGreen() / 255.0f;
                    B = (float) colors[i].getBlue() / 255.0f;
                    R = (float) colors[i].getRed() / 255.0f;
                    c = new Color(R, G, B);
                    colormap.put(node, c);
                    i = 100;
                    }
                    }*/
                    Color cl = currentGoMap.get(s);
                    if (cl != null)
                    {
                        colormap.put(node, cl);
                        break;
                    }
                }

                for (String s : gos)
                {
                    if (s.equals(mainCat))
                    {
                        colormap.put(node, c);
                    }
                }
            }

            Collection<TraitTreeVal> temp = tree.getChildren(node);
            for (TraitTreeVal ttv : temp)
            {
                queue.add(ttv);
            }
        }
        return colormap;
    }

    private Map mergeMaps(Map defaults, Map overrides)
    {
        Map result = new HashMap(defaults);
        if (overrides != null)
        {
            result.putAll(overrides);
        }
        return result;
    }

    /**
     * We easily create a hashmap of TTV that map to arraylists of their assigned
     * traits, however, when we go back the other way, we can reverse the list
     * for ease of use
     * @param assignments
     * @return
     */
    private HashMap<Integer, TraitTreeVal> reverseHashMapOfAssignments(
            HashMap<TraitTreeVal, ArrayList<Integer>> assignments)
    {
        HashMap<Integer, TraitTreeVal> toRet = new HashMap<Integer, TraitTreeVal>();
        for (TraitTreeVal ttv : assignments.keySet())
        {
            for (Integer i : assignments.get(ttv))
            {
                toRet.put(i, ttv);
            }
        }
        return toRet;
    }

    /**
     * Look at both this node, and other nodes above it to see if it matches
     * @param get the traittreeval we are looking for
     * @param ttv where we begin the search
     * @return true if found
     */
    private boolean isTraitUnderNode(TraitTreeVal get, TraitTreeVal ttv)
    {
        if (get == ttv)
        {
            return true;
        }

        TraitTreeVal temp = (TraitTreeVal) tree.getParent(get);

        while (temp != null)
        {
            if (temp == ttv)
            {
                return true;
            }
            temp = (TraitTreeVal) tree.getParent(temp);
        }
        return false;
    }

    public void componentResized(ComponentEvent e)
    {
        this.radialLayout.setSize(vv2.getSize());
        if (this.isRadialLayout)
        {
            vv2.removePreRenderPaintable(rings);
        }
        rings = new Rings();
        if (this.isRadialLayout)
        {
            vv2.addPreRenderPaintable(rings);
        }
        vv2.repaint();
    }

    public void componentMoved(ComponentEvent e)
    {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void componentShown(ComponentEvent e)
    {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void componentHidden(ComponentEvent e)
    {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    /**
     * This is the class that draws the rings on the radial tree. 
     */
    class Rings implements VisualizationServer.Paintable
    {
        Collection<Double> depths;

        /**
         * Creates a new Rings object.
         */
        public Rings()
        {
            depths = getDepths();
        }

        /**
         * Determines how many depths are in the tree. 
         * @return
         */
        private Collection<Double> getDepths()
        {
            Set<Double> depths = new HashSet<Double>();
            Map<TraitTreeVal, PolarPoint> polarLocations = radialLayout.getPolarLocations();
            Set<TraitTreeVal> vertices = new HashSet<TraitTreeVal>(tree.getVertices());
            for (TraitTreeVal v : vertices)
            {
                PolarPoint pp = polarLocations.get(v);
                depths.add(pp.getRadius());
            }
            return depths;
        }

        /**
         * Draws the lines. 
         * @param g
         */
        public void paint(Graphics g)
        {
            g.setColor(Color.lightGray);

            Graphics2D g2d = (Graphics2D) g;
            Point2D center = radialLayout.getCenter();

            Ellipse2D ellipse = new Ellipse2D.Double();
            for (double d : depths)
            {
                ellipse.setFrameFromDiagonal(center.getX() - d, center.getY() - d, center.getX() + d, center.getY() + d);
                Shape shape = vv2.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).transform(ellipse);
                g2d.draw(shape);
            }
        }

        public boolean useTransform()
        {
            return true;
        }
    }
    class ClusterVertexShapeFunction<V> extends EllipseVertexShapeTransformer<V>
    {
        ClusterVertexShapeFunction()
        {
            setSizeTransformer(new ClusterVertexSizeFunction<V>(20));
        }

        @Override
        public Shape transform(V v)
        {
            if (v instanceof Graph)
            {
                int size = ((Graph) v).getVertexCount();
                if (size < 8)
                {
                    int sides = Math.max(size, 3);
                    return factory.getRegularPolygon(v, sides);
                }
                else
                {
                    return factory.getRegularStar(v, size);
                }
            }
            return super.transform(v);
        }
    }
    class ClusterVertexSizeFunction<V> implements Transformer<V, Integer>
    {
        int size;

        public ClusterVertexSizeFunction(Integer size)
        {
            this.size = size;
        }

        public Integer transform(V v)
        {
            if (v instanceof Graph)
            {
                return 30;
            }
            return size;
        }
    }

    /**
     * From a database tree object, this method creates a JUNG visualization
     * object.
     * @param root the root of the visualization tree.
     * @return the JUNG tree. 
     */
    public DelegateForest<TraitTreeVal, String> buildJUNGTree(TraitTreeVal root)
    {
        DelegateForest<TraitTreeVal, String> newTree = new DelegateForest<TraitTreeVal, String>();
        Queue<TraitTreeVal> q = new LinkedList<TraitTreeVal>();

        q.offer(root);
        newTree.setRoot(root);

        while (!q.isEmpty())
        {
            TraitTreeVal currentTTE = q.poll();
            if (currentTTE.getChildren() != null)
            {
                for (TraitTreeVal child : currentTTE.getChildren())
                {
                    newTree.addVertex(child);
                    String edgeName = currentTTE.getId() + " to " + child.getId();
                    newTree.addEdge(edgeName, currentTTE, child);

                    q.offer(child);

                }//end of adding children to queue (for loop)

            }//end of if block
        }

        this.tree = newTree;
        return newTree;
    }
}
