package views.network;

import BiNGO.GoItems;
import datamodel.Association;
import datamodel.AssociationSet;
import datamodel.Marker;
import datamodel.Network;
import datamodel.Edge;
import datamodel.Model;
import datamodel.Trait;
import datamodel.TraitSubset;
import java.awt.event.ComponentEvent;
import views.*;
import java.awt.Color;
import javax.swing.JPanel;
import edu.uci.ics.jung.visualization.renderers.BasicRenderer;
import edu.uci.ics.jung.graph.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import views.snp.MyPickableVertexPaintTransformer;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import heatchart.HeatChart;
import java.awt.event.ComponentListener;

import views.heatmap.HeatChartScale;
import views.network.threeway.ThreeWayVisualizationSettingsGUI.ImagePanel1;
import views.snp.ImagePanel;

/**
 * Networks can be visualized using the network view through JUNG, which will
 * display the networks with a ball and stick representation. We give the user
 * control over several aspects of the network visualization, which happens
 * through this class and the network control panel
 *
 * In this class, the calling code passes in the jpanel, on which the network
 * view is drawn and then interaction can start. 
 * @author rcurtis
 */
public class NetworkView extends Visualization implements MouseListener, ComponentListener, GoListener
{
    /**
     * the container that this network view belongs to
     */
    private JPanel container;
    /**
     * The JUNG object that drawns the network
     */
    private VisualizationViewer<Trait, Edge> vv;
    /**
     * The control panel, which helps to update the network visualization
     */
    private NetworkControlPanel controlPanel;
    /**
     * A redundant pointer back to the ASsociationView object
     */
    private AssociationView parent;
    /**
     * The visualization view's graph object
     */
    private Graph g;
    /**
     * The databse list of edges between nodes
     */
    private ArrayList<Edge> netStruct;
    /**
     * The list of all trait nodes
     */
    private ArrayList<Trait> nodes;
    /**
     * Yet another reference back to the jpanel that we are drawn on
     */
    private JPanel jp;
    /**
     * The popup handler which interacts with the user to generate commands
     * for the network view
     */
    private NetworkPopupHandler popupHandler;
    /**
     * The database pointer to the network of traits
     */
    private Network network;
    /**
     * A subsets of traits, pointer back to the database
     */
    private TraitSubset subset;
    /**
     * The jframe owner of this class
     */
    private JFrame owner;
    /**
     * If we are switching between associations using the same network data,
     * we can cut some corners, and so we keep track of tath.
     */
    private boolean isSameNodeSet = false;
    /**
     * In the population association view, we might want to switch between
     * different populations to look at association strenghts. This panel
     * is the means to do so.
     */
    private PopSelectionPanel popselpanel;
    /**
     * The association set that belongs to this object, or null
     */
    private AssociationSet assocs;
    /**
     * An arraylist of the markers in the association set.
     */
    private ArrayList<Marker> markers;
    /**
     * The coloring of the nodes for the GO categories currently displayed.
     * String is the name of the go category, and Color is the color assigned to it.
     */
    public Map<String, Color> currentGoMap;
    /**
     * The current colormap of the nodes displayed on screen.
     */
    Map<Trait, Color> colormap;
    /**
     * Determines whether or not the nodes are currently marked by association. 
     */
    private boolean isMarked = false;
    /**
     * Indicates the current population shown in the visualization. 
     */
    private int pop = -1;
    /**
     * A display to show the association strengths
     */
    private AssociationColorKey myscale;

    /** constructor
     *
     * @param owner
     * @param av
     */
    public NetworkView(JFrame owner, AssociationView av)
    {
        this.owner = owner;
        parent = av;
        myscale = av.getMyAssocScale();

    }

    /**
     * places a network and control panel for it onto a jpanel.
     *
     * @param jp : JPanel to draw onto
     * @param n : network to be represented
     * @param networkLayoutLoc : location to place network layout
     * @param contpanLayoutLoc : location to place control panel
     */
    public void drawNetworkOnCanvas(JPanel jp, Network n, TraitSubset s,
            String networkLayoutLoc, boolean associated, boolean isPop, int noPops)
    {
        if (jp == null || n == null || networkLayoutLoc == null)
        {
            throw new NullPointerException();
        }

        this.jp = jp;

        isSameNodeSet = (network != null && n.getTraitName().equals(this.network.getTraitName()) && subset == s);
        subset = s;

        try
        {
            if (!isSameNodeSet)
            {
                nodes = n.getNetworkNodes(s);
                myscale.setVisible(false);
                myscale.refresh();
            }
            netStruct = n.getNetworkStructure(s, nodes, "default");
        }
        catch (Exception e)
        {
            System.err.println(e);
            return;
        }
        network = n;
        if (!isSameNodeSet)
        {
            controlPanel = new NetworkControlPanel(this, netStruct);
        }
        else
        {
            controlPanel.setup(this, netStruct);
        }
        drawNetworkGraph(associated);
        //if(!isSameNodeSet)
        {
            controlPanel.init(vv, isSameNodeSet);
        }

        try
        {
            vv.removeMouseListener(popupHandler);
        }
        catch (Exception e)
        {
        }
        popupHandler = new NetworkPopupHandler(this, vv, controlPanel, associated, subset);

        vv.addMouseListener(popupHandler);

        container = new JPanel();
        container.setLayout(new BorderLayout());
        container.add(vv, BorderLayout.CENTER);
        container.add(controlPanel, BorderLayout.SOUTH);
        container.setBackground(jp.getBackground());
        container.addComponentListener(this);
        if (isPop)
        {
            popselpanel = new PopSelectionPanel(noPops, this, jp.getBackground(), true);
            popselpanel.setVisible(false);
            container.add(popselpanel, BorderLayout.EAST);
        }
        ((EdgeLabelStringer) vv.getRenderContext().getEdgeLabelTransformer()).enabled = false;

        jp.add(container, networkLayoutLoc);
    }

    /**
     * returns the number of nodes
     * @return
     */
    public int nodesSize()
    {
        return nodes.size();
    }

    /**
     * draws the network on the visualizer
     *
     */
    private void drawNetworkGraph(boolean associated)
    {
        g = buildVizGraph();

        if (!isSameNodeSet)
        {
            Layout<Trait, Edge> layout;
            if (!associated)
            {
                layout = controlPanel.getNewLayout(g, new Dimension(jp.getWidth(), jp.getHeight() - 35));
            }
            else
            {
                layout = controlPanel.getNewLayout(g, new Dimension(jp.getWidth(), parent.getCVHeight()));
            }
            BasicRenderer renderer = new BasicRenderer();

            vv = new VisualizationViewer<Trait, Edge>(layout);

            vv.setRenderer(renderer);
            vv.getRenderContext().setVertexFillPaintTransformer(new PickableVertexPaintTransformer<Trait>(vv.getPickedVertexState(), Color.black, Color.cyan));
            vv.setBackground(jp.getBackground());

            vv.getRenderContext().setEdgeDrawPaintTransformer(new PickableEdgePaintTransformer<Edge>(vv.getPickedEdgeState(), Color.black, Color.pink));
            establishGoMap();
        }
    }

    /**
     * returns a pointer to the network control panel
     * @return
     */
    public NetworkControlPanel getControlPanel()
    {
        return controlPanel;
    }

    /**
     * Returns a pointer to the database object representing the network of traits
     * @return
     */
    public Network GetNetwork()
    {
        return network;
    }

    /**
     * Returns a pointer to the JUNG object representing the visualization
     * @return
     */
    public VisualizationViewer<Trait, Edge> getVisualizationViewer()
    {
        return vv;
    }

    /**
     * builds a graph from visualization settings.
     * @return graph as it should be displayed.
     */
    private Graph<? extends Object, ? extends Object> buildVizGraph()
    {
        UndirectedGraph<Trait, Edge> g1;
        if (!isSameNodeSet)
        {
            g1 = new UndirectedSparseMultigraph<Trait, Edge>();
            for (Trait t : nodes)
            {
                g1.addVertex(t);
            }
        }
        else
        {
            g1 = (UndirectedGraph<Trait, Edge>) g;
            ArrayList<Edge> temp = new ArrayList<Edge>();
            for (Edge ne : g1.getEdges())
            {
                temp.add(ne);
            }
            for (Edge ne : temp)
            {
                g1.removeEdge(ne);
            }
        }
        for (int i = 0; i < netStruct.size(); i++)
        {
            if (java.lang.Math.abs(netStruct.get(i).weight) > controlPanel.getThresh())
            {
                try
                {
                    if (!g1.containsEdge(netStruct.get(i)))
                    {
                        g1.addEdge(netStruct.get(i), netStruct.get(i).getT1(),
                                netStruct.get(i).getT2());
                    }
                }
                catch (Exception e)
                {
                    System.out.println("I think this is where the error is.");
                }
            }
        }
        return g1;
    }

    /**
     * updates the jpanel
     *
     */
    public void updateUI()
    {
        jp.updateUI();
    }

    /**
     * Determines the colors of Traits.  Only used when viewing Associations.
     * @param map : map of Traits to values from 0 to 1
     */
    public void setColorMap(Map<Integer, Pair> map, boolean isPval, boolean isLogScale,
            double max, double min, boolean isPop, boolean isSetGoColor, String goCat, Color goColor,
            double logmn, Map<Integer, Pair> popmap)
    {
        Map<Trait, Color> colormap = new HashMap<Trait, Color>();

        float logmin = (float) (-1 * Math.log10(0.05));
        float logmax = 1;
        if (logmn != 0)
        {
            logmax = (float) (-1 * Math.log10(logmn));
        }

        if (isPop)
        {
            popselpanel.setVisible(isPop);
        }
        else
        {
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

        }

        for (int t : map.keySet())
        { //for each trait in the map, find it in the network and map a color to it

            Trait t2 = null;
            Collection<Trait> vs = vv.getGraphLayout().getGraph().getVertices();
            Double val = map.get(t).d;
            int popno = map.get(t).i;
            Double val2 = popmap.get(t).v;
            for (Trait trait : vs)
            {
                if (trait.getId() == t)
                {
                    t2 = trait;
                    break;
                }
            }
            //black for value of 0, white for 1
            float value = Math.abs(val.floatValue());
            float value2 = Math.abs(val2.floatValue());

            if (isPval)
            {
                if (value != 0)
                {
                    value = (float) (-1 * Math.log10(value));
                    value = (value - logmin) / (logmax - logmin);
                }
                else
                {
                    value = 50;
                }
            }
            else
            {
                value = (float) ((value - min) / (max - min));
                value2 = (float) ((value2 - min) / (max - min));
            }
            //ROUNDING ERROR!!
            if (value < 0)
            {
                value = 0;
            }
            if (value2 < 0)
            {
                value2 = 0;
            }
            if (value > 1)
            {
                value = 1;
            }

            value = value / 2.0f + 0.5f;
            value2 = value2 / 2.0f + 0.5f;

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
            else if (isSetGoColor)
            {
                ArrayList<String> curAnno = t2.getCurrentGoAnnotation(null);
                for (String s : curAnno)
                {
                    if (s.equals(goCat))
                    {
                        Color d = goColor;
                        Gini = (float) (d.getGreen()) / 255.0f;
                        Bini = (float) (d.getBlue()) / 255.0f;
                        Rini = (float) (d.getRed()) / 255.0f;
                    }
                }
            }
            float G, B, R;
            if (!isPop)
            {
                G = Gini * value;
                B = Bini * value;
                R = Rini * value;
            }
            else
            {
                float m = 1.0f - value2 / value;
                m = m / 2;
                m = m + 0.5f;
                G = Gini * m;
                B = Bini * m;
                R = Rini * m;

            }
            colormap.put(t2, new Color(R, G, B));
        }
        vv.getRenderContext().setVertexFillPaintTransformer(new MyPickableVertexPaintTransformer<Trait>(vv.getPickedVertexState(), colormap, Color.black, Color.PINK));
        vv.repaint();
    }

    /**
     * update teh size of the displayed network when the overall size changes
     * @param e
     */
    public void componentResized(ComponentEvent e)
    {
        refresh();
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

    @Override
    public void setPop(int popNo)
    {
        markUpByAssociation(assocs, markers, popNo);
    }

    /**
     * Sets the color map based on a certain category as the priority. 
     * @param cat
     * @param c
     */
    public void colorByCat(String cat, Color c)
    {
        Map<Integer, Pair> map = null;
        if (pop != -1)
        {
            return;
        }
        if (!this.isMarked)
        {
            if (this.subset.getGoList() > 0)
            {
                setDefaultNodeColor(this.subset.getGoItems());
            }
            else
            {
                setDefaultNodeColor(new ArrayList<GoItems>());
            }
            for (Trait t : nodes)
            {
                ArrayList<String> curAnno = t.getCurrentGoAnnotation(null);
                for (String s : curAnno)
                {
                    if (s.equals(cat))
                    {

                        colormap.put(t, c);
                    }
                }
            }

            vv.getRenderContext().setVertexFillPaintTransformer(new MyPickableVertexPaintTransformer<Trait>(vv.getPickedVertexState(), colormap, Color.black, Color.PINK));
            vv.repaint();
        }
        else
        {
            this.markUpByAssociation(assocs, markers, pop, true, cat, c);
        }
    }

    /**
     * Colors the traits by the strength of their association with the
     * markers. 
     * @param ac
     * @param markers
     * @param pop
     * @return
     */
    public Map<Integer, Pair> markUpByAssociation(AssociationSet ac, ArrayList<Marker> markers,
            int pop)
    {
        return this.markUpByAssociation(ac, markers, pop, false, null, Color.yellow);
    }

    /**
     * Goes through all traits and colors them by their default coloring based
     * on GO category and the enrichment for the traitsubset. 
     * @param list
     */
    public void setDefaultNodeColor(ArrayList<GoItems> list)
    {
        this.isMarked = false;
        for (Trait t : nodes)
        {
            colormap.put(t, Color.black);
            ArrayList<String> curAnno = t.getCurrentGoAnnotation(null);

            if (curAnno.size() > 0)
            {
                colormap.put(t, Color.white);
            }
            boolean found = false;
            for (GoItems go : list)
            {
                for (String s : curAnno)
                {
                    if (s.equals(go.descr))
                    {
                        Color c = currentGoMap.get(s);
                        if (c != null)
                        {
                            colormap.put(t, c);
                            found = true;
                            break;
                        }
                    }
                }
                if (found)
                {
                    break;
                }
            }
        }
    }

    /**
     * Establishes the GO map for this network. 
     */
    public void establishGoMap()
    {
        currentGoMap = new HashMap<String, Color>();
        colormap = new HashMap<Trait, Color>();
        if (subset != null && this.subset.getGoList() != -1)
        {
            ArrayList<GoItems> list = subset.getGoItems();
            GoResultsViewer.getInstance().setVisible(true, subset.getName(), list, null, currentGoMap, this, 2);
            for (int i = 0; i < list.size() && i < 23; i++)
            {
                currentGoMap.put(list.get(i).descr, Model.colors[i]);
            }
            setDefaultNodeColor(list);
            vv.getRenderContext().setVertexFillPaintTransformer(new MyPickableVertexPaintTransformer<Trait>(vv.getPickedVertexState(), colormap, Color.black, Color.PINK));
        }
    }
    /**
     * A class of a pair, a double and an int
     */
    public class Pair
    {
        public double d;
        public int i;
        public double v;

        /**
         * Creates a new pair
         * @param d a double
         * @param i an int
         */
        public Pair(double d, int i, double v)
        {
            this.d = d;
            this.i = i;
            this.v = v;
        }
    }

    /**
     * Colors the trait nodes by the strenght of their association to the markers.
     * We can also do this by go category, although I believe that this funcionality
     * is currently not used.
     * @param ac
     * @param markers
     * @param pop
     * @param setGoColor
     * @param goCat
     * @param goColor
     * @return
     */
    public Map<Integer, Pair> markUpByAssociation(AssociationSet ac, ArrayList<Marker> markers,
            int pop, boolean setGoColor, String goCat, Color goColor)
    {
        this.isMarked = true;
        this.pop = pop;
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
        ArrayList<Integer> indxs;
        if (subset == null)
        {
            indxs = this.network.getTraitSet().getIds(nodes);
        }
        else
        {
            indxs = subset.getIndeces();
        }
        Collection<Association> assocs = ac.findAssociations(markerID, indxs, pop);
        System.out.print(assocs.size() + " " + markers.size() + "\n");
        Map<Integer, Pair> map = new HashMap<Integer, Pair>();
        Map<Integer, Pair> map2 = new HashMap<Integer, Pair>();
        double maxVal = 0.0;
        double minVal = 0;//1e99;
        double absMinVal = 1e99;
        for (Association a : assocs)
        {
            if (map.get(a.getTraitId()) == null)
            {
                map.put(a.getTraitId(), new Pair(Math.abs(a.getValue()), a.getPopNo(), 0.0));
                map2.put(a.getTraitId(), new Pair(Math.abs(a.getValue()), a.getPopNo(), 0.0));
            }
            else
            {
                /*Double d = java.lang.Math.max(Math.abs(a.getValue()), map.get(a.getTraitId()).d);
                if (d > map.get(a.getTraitId()).d)
                {
                d = d - map.get(a.getTraitId()).d;
                map.put(a.getTraitId(), new Pair(d, a.getPopNo()));
                }
                else
                {
                d = map.get(a.getTraitId()).d - d;
                map.put(a.getTraitId(), new Pair(d, map.get(a.getTraitId()).i));
                }*/
                Double curBestForTrait = map2.get(a.getTraitId()).d;
                Double cur2ndForTrait = map2.get(a.getTraitId()).v;
                Double newbieForTrait = Math.abs(a.getValue());
                boolean isPval = ac.getIsPvals();

                if (((newbieForTrait > curBestForTrait && !isPval) ||
                      (newbieForTrait < curBestForTrait && isPval))
                        && map2.get(a.getTraitId()).i != a.getPopNo())
                {
                    map2.put(a.getTraitId(), new Pair(newbieForTrait, a.getPopNo(), curBestForTrait));
                    map.put(a.getTraitId(), new Pair(newbieForTrait, a.getPopNo(), curBestForTrait));
                }
                else if (((newbieForTrait > curBestForTrait && !isPval) ||
                        (newbieForTrait < curBestForTrait && isPval))
                        && map2.get(a.getTraitId()).i == a.getPopNo())
                {
                    map2.put(a.getTraitId(), new Pair(newbieForTrait, a.getPopNo(), cur2ndForTrait));
                    map.put(a.getTraitId(), new Pair(newbieForTrait, a.getPopNo(), cur2ndForTrait));
                }
                else if (((newbieForTrait > cur2ndForTrait && !isPval) ||
                        (newbieForTrait < cur2ndForTrait && isPval))
                        && map2.get(a.getTraitId()).i != a.getPopNo())
                {
                    map2.put(a.getTraitId(), new Pair(curBestForTrait, map2.get(a.getTraitId()).i, newbieForTrait));
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
            if (val < minVal)
            {
                minVal = val;
            }
        }
        setColorMap(map, ac.getIsPvals(), false, maxVal, absMinVal, ac.isPopAssocSet(),
                setGoColor, goCat, goColor, absMinVal, map2);

        return map;
    }

    /**
     * Returns the trait subset currently displayed in the network
     * @return
     */
    public TraitSubset getTraitSubset()
    {
        return this.subset;
    }

    /**
     * Returns the JFrame that owns this panel
     * @return
     */
    public JFrame getOwner()
    {
        return owner;
    }

    /**
     * Returns the association view that owns this panel
     * @return
     */
    public AssociationView getParent()
    {
        return parent;
    }

    /**
     * In order to refresh the view or resize, we rely on the control panel
     */
    public void refresh()
    {
        if (controlPanel != null)
        {
            controlPanel.resetView();
        }
    }

    /**
     * Determines if the network si the same one that we are already working with
     * @param n
     * @return
     */
    public boolean isSameTraitSet(Network n)
    {
        if (network == null)
        {
            return false;
        }
        return n.getTraitSet().getId() == this.network.getTraitSet().getId();
    }

    /**
     * Called when it is time to remove the visualization from the jpanel
     */
    public void removeFromCanvas()
    {
        //jp.remove(vv);
        //jp.remove(controlPanel);
        jp.remove(container);
    }

    public void mouseEntered(MouseEvent E)
    {
        //System.out.println("Mouse clicked");
    }

    public void mouseExited(MouseEvent E)
    {
    }

    public void mouseReleased(MouseEvent E)
    {
        //System.out.println("Mouse clicked");
    }

    public void mousePressed(MouseEvent E)
    {
        System.out.println("Mouse clicked");
    }

    public void mouseClicked(MouseEvent E)
    {
        System.out.println("Mouse clicked");
    }
}
