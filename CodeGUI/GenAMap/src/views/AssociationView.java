package views;

import views.heatmap.AssocHeatMap;
import views.heatmap.NetworkHeatMap;
import control.DataAddRemoveHandler;
import datamodel.AssociationSet;
import datamodel.Marker;
import datamodel.MarkerSet;
import datamodel.Network;
import datamodel.Trait;
import datamodel.TraitSubset;
import datamodel.TraitTree;
import java.awt.BorderLayout;
import java.util.*;
import javax.swing.JFrame;
import views.network.AssociationColorKey;
import views.snp.ChromosomeView;
import datamodel.Edge;
import datamodel.GeneTraitAssociation;
import views.network.NetworkView;
import views.network.TreeView;

import datamodel.Population;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.util.ArrayList;
import views.network.GeneTraitNetworkView;
import views.snp.PopulationView;

/**
 * AssociationView is in control of all the visualizations for population
 * structure, networks, trees, and association set. Visualization can be combined
 * because all visualizations are drawn on a grid layout, which is the
 * AssociationView.
 *
 * @author rcurtis
 */
public class AssociationView extends javax.swing.JPanel
{
    /**
     * The object used to draw and view trees
     */
    private TreeView tv;
    /**
     * The object used to draw and vizualize networks
     */
    private NetworkView nv;
    /**
     * The object used to visualize chromosomes and charts
     */
    private ChromosomeView cv;
    /**
     * The object used to visualize population structure
     */
    private PopulationView pv;
    /**
     * The object that draws the trait/gene associations. 
     */
    private GeneTraitNetworkView gtnv;
    /**
     * The one instance of this class
     */
    public static AssociationView instance;
    /**
     * A pointer to the association set currently being displayed. This is
     * used when the AssociationView is used as a communication between
     * different displays
     */
    private AssociationSet ac;
    /**
     * This object is used to draw a heat map display of a network visualization
     */
    private NetworkHeatMap netmap;
    /**
     * This object is used to draw a heat map display of a association visualization
     */
    private AssocHeatMap assocmap;
    /**
     * Whether or not we are in a JUNG layout or in a heat-map layout
     */
    private boolean isJUNGView;
    /**
     * The current view ... depends on what we are visualizing.
     */
    private int view;
    private static int ASSOC_VIEW = 0;
    private static int TRAIT_VIEW = 1;
    private static int MARKER_VIEW = 2;
    private static int POP_VIEW = 3;
    private static int TREE_VIEW = 4;
    private static int THREEWAY_VIEW = 5;
    /**
     * The network that is currently being viewed
     */
    private Network n;
    /**
     * The traitset that is currently being viewed. 
     */
    private TraitSubset ts;
    /**
     * The owner of this AssociationView object
     */
    private JFrame owner;
    /**
     * all markers that currently are being displayed - we use to grab associations
     */
    private ArrayList<Marker> assocMarkers;
    /**
     * Determines whether or not the view was an association view when the user
     * is switching between large views in JUNG view.
     */
    private boolean wasJUNGView = false;
    /**
     * A dialog that can show the color by association of
     * an association view.
     */
    private AssociationColorKey myscale = new AssociationColorKey();

    /**
     * Creates a new form AssociationView
     */
    public AssociationView()
    {
        super();
        initComponents();

        cv = new ChromosomeView(this);
        pv = new PopulationView();
        gtnv = new GeneTraitNetworkView(this);
        instance = this;
    }

    /**
     * Sets whether or not the current display should be in JUNG view. This
     * will be overridden if the view is not compatible with JUNG view. 
     * @param isJUNGVIEW
     */
    public void setIsJungView(boolean isJUNGVIEW)
    {
        isJUNGView = isJUNGVIEW;
        DataAddRemoveHandler.getInstance().setViewerType(isJUNGVIEW ? 1 : 0);
    }

    /**
     * Initializes the JUNG view by initializing the different parts
     * and setting a pointer to the owner of this class.
     * @param owner
     */
    public void init(JFrame owner)
    {
        netmap = new NetworkHeatMap(owner, this);
        assocmap = new AssocHeatMap(owner, this);
        nv = new NetworkView(owner, this);
        tv = new TreeView(this);

        this.isJUNGView = DataAddRemoveHandler.getInstance().getViewType() == 1;
        this.owner = owner;
    }

    /**
     * Returns the current height of the chromosome view so that the network view
     * can update its size accordingly.
     * @return
     */
    public int getCVHeight()
    {
        return cv.getHeight();
        //return 500;
    }

    /**
     * Sets up an association view between traits structured as a tree
     * and the markerset in the association a
     * @param tt the traittree structure to use to structure the traits
     * @param a the associationset between markers and traits
     */
    public void showAssociationView(TraitTree tt, AssociationSet a)
    {
        GoResultsViewer.getInstance().setVisible(false);
        ac = a;
        this.isJUNGView = true;
        this.removeAll();
        this.removeListeners();
        cv.drawOnCanvas(this, a.getMarkerSet(), BorderLayout.SOUTH, true, ac, false);
        if (!a.isPopAssocSet())
        {
            tv.drawTreeOnCanvas(this, tt, false, BorderLayout.CENTER, this, 0, false, 0);
        }
        else
        {
            pv.setFactor(3);
            pv.drawOnCanvas(this, a.getPopulation(), BorderLayout.NORTH, 3, ac.getNumPops());
            tv.drawTreeOnCanvas(this, tt, false, BorderLayout.CENTER, this, 3, true, a.getNumPops());
        }
        this.updateUI();
        view = TREE_VIEW;
    }

    /**
     * Shows an association view between markers and traits subsetted by s and
     * put into a network visualization determined by n
     * @param n the network to structure the traits by
     * @param s the subset of the traits to show
     * @param a the association set between markers and traits. 
     */
    public void showAssociationView(Network n, TraitSubset s, AssociationSet a)
    {
        GoResultsViewer.getInstance().setVisible(false);
        ac = a;
        boolean isSameSubby = ts == s;
        ts = s;
        this.n = n;
        if (!isJUNGView || (s != null && s.getIndeces().size() > 200) || (s == null && n.getTraitSet().getNumTraits() > 200))
        {
            this.removeAll();
            this.removeListeners();
            this.isJUNGView = false;
            assocmap.drawAssocOnCanvas(this, a, s);
            this.updateUI();
            wasJUNGView = false;
        }
        else
        {
            boolean isSaveTraits = nv.isSameTraitSet(n) && view == ASSOC_VIEW && wasJUNGView;
            wasJUNGView = true;
            if (!isSaveTraits)
            {
                this.removeAll();
                this.removeListeners();
                cv.removeCharts();
                cv.drawOnCanvas(this, a.getMarkerSet(), BorderLayout.SOUTH, true, ac, false);
            }
            else
            {
                if(!isSameSubby)
                {
                    cv.removeCharts();
                }
                cv.setAssociationSet(a);
                if(a.getMarkerSet() != cv.getMarkerSet())
                {
                    cv.removeFromCanvas();
                    cv.drawOnCanvas(this, a.getMarkerSet(), BorderLayout.SOUTH, true, ac, false);
                }
                else
                {
                    cv.updateChart();
                }
                nv.removeFromCanvas();
                pv.removeFromCanvas();
            }
            if (!a.isPopAssocSet())
            {
                nv.drawNetworkOnCanvas(this, n, s, BorderLayout.CENTER, true, false, 0);
            }
            else
            {
                nv.drawNetworkOnCanvas(this, n, s, BorderLayout.CENTER, true, true, a.getNumPops());
                pv.setFactor(3);
                pv.drawOnCanvas(this, a.getPopulation(), BorderLayout.NORTH, 2.5, a.getNumPops());
            }

            if (isSaveTraits && assocMarkers != null)
            {
                this.highlightAssociatedTraits(assocMarkers);
            }
            this.updateUI();
        }
       
        view = ASSOC_VIEW;
    }

    /**
     * Called from the network view, this method will add a manhattan plot to
     * the chromosome view.
     */
    public void addChart()
    {
        VisualizationViewer<Trait, Edge> vv = nv.getVisualizationViewer();
        Set<Trait> picked = vv.getPickedVertexState().getPicked();

        if (!picked.isEmpty())
        {
            ArrayList<Trait> picked_array = new ArrayList<Trait>();
            for(Trait q : picked)
                picked_array.add(q);
            cv.setislogdisplay(ac.getIsPvals());
            cv.addChart(picked_array);
        }
    }

    /**
     * Called from the network view, this method will add a series of Manhattan
     * plots to the chromosome view
     * @param picked_array the triats to add charts for
     */
    public void addChart(ArrayList<Trait> picked_array)
    {
        cv.setislogdisplay(false);
        cv.addChart(picked_array);
    }

    /**
     * Remove all charts from teh chromosome view
     */
    public void removeCharts()
    {
        cv.removeCharts();
    }

    /**
     * Adds a SNP visualization / genome browser from the markerset passed in. 
     * @param m
     */
    public void addJUNGSnpVisualization(MarkerSet m)
    {
        GoResultsViewer.getInstance().setVisible(false);
        this.removeAll();
        this.removeListeners();
        cv.drawOnCanvas(this, m, BorderLayout.SOUTH, false, null, false);
        this.updateUI();
        view = MARKER_VIEW;
    }

    /**
     * Adds a population visualization to the visualization
     * @param m the markerset to show on the genome browser
     * @param p the population to show the structure for. 
     */
    public void addpopVisualization(MarkerSet m, Population p)
    {
        GoResultsViewer.getInstance().setVisible(false);
        this.removeAll();
        this.removeListeners();
        cv.drawOnCanvas(this, m, BorderLayout.SOUTH, false, null, false);
        pv.setFactor(1);
        pv.drawOnCanvas(this, p, BorderLayout.CENTER, 1, p.isStructureGenerated() ? 10 : p.getTotPopIfUserGen());
        this.updateUI();
        view = POP_VIEW;
    }

    /**
     * Returns the one instance of this class.
     * @return
     */
    public static AssociationView getCurrentRunningInstance()
    {
        return instance;
    }

    /**
     * Sets a map in the ChromosomeView for determining the colors of markers
     * based on the current AssociationCollection and the passed set of traits
     * @param traits : set of traits which we want to use to highlight markers
     */
    public void highlightAssociatedMarkers(Collection<Trait> traits)
    {
        cv.setColorMap(traits);
    }

    /**
     * Sets a map in the NetworkView for determining the colors of traits based
     * on the current AssociationCollection and the passed set of markers
     * @param markers : set of markers which we want to use to highlight traits
     */
    public void highlightAssociatedTraits(ArrayList<Marker> markers)
    {
        assocMarkers = markers;
        if (view == ASSOC_VIEW)
        {
            nv.markUpByAssociation(ac, markers, -1);
        }
        else if (view == TREE_VIEW)
        {
            tv.markUpByAssociation(ac, markers, -1);
        }
        else
        {
            this.gtnv.markUpByAssociation(markers, -1);
        }
    }

    /**
     * Given the markers, we run a subset generation algorithm to find
     * all of the traits that have some association to them. 
     * @param markers
     */
    public void saveAssociatedAsSubset(ArrayList<Marker> markers)
    {
        DataAddRemoveHandler.getInstance().addSubsetFromAssociation(ac, markers, nv.getTraitSubset(), this.owner);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(new java.awt.Color(246, 248, 254));
        setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 102), 2, true));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    /**
     * When the association view resizes, all the pieces on the view need to
     * resize as well.
     * @param evt
     */
    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        if (!this.isJUNGView && view == TRAIT_VIEW)
        {
            netmap.resize(this);
        }
        else if (!this.isJUNGView && view == ASSOC_VIEW)
        {
            assocmap.resize(this);
        }
        else if (this.isJUNGView && this.view != POP_VIEW)
        {
            nv.refresh();

            tv.resize();
            if (ac != null && ac.isPopAssocSet())
            {
                pv.resize();
            }
        }

        if (this.view == POP_VIEW)
        {
            pv.resize();
        }
        if(this.view == THREEWAY_VIEW)
        {
            gtnv.refresh(this);
        }
    }//GEN-LAST:event_formComponentResized

    /**
     * In a heatmap, we can switch between subsets. 
     * @param name
     */
    public void selectSubset(String name)
    {
        if (view == ASSOC_VIEW)
        {
            DataAddRemoveHandler.getInstance().selectSubsetInAssocTree(name);
            this.showAssociationView(n, ts, ac);
        }
        else
        {
            DataAddRemoveHandler.getInstance().selectSubsetInTraitTree(name);
            this.showNetworkVisualization(n, ts);
        }
    }

    /**
     * Refreshes the view
     * @param name
     */
    public void refreshView()
    {
        if (view == ASSOC_VIEW)
        {
            this.showAssociationView(n, ts, ac);
        }
        else
        {
            this.showNetworkVisualization(n, ts);
        }
    }

    /**
     * Shows the visualization of traits structured as a tree
     * @param tt the tree of traits to visualize
     */
    void showTraitTreeVisualization(TraitTree tt)
    {
        GoResultsViewer.getInstance().setVisible(false);
        this.removeAll();
        this.removeListeners();
        tv.drawTreeOnCanvas(this, tt, false, BorderLayout.CENTER, this, 0, false, 0);
        this.isJUNGView = true;
        view = TREE_VIEW;

        this.updateUI();
    }

    /**
     * Shows the visualization of traits structured as a network
     * @param n the network of traits to visualize
     * @param s the subset of those triats to show. 
     */
    public void showNetworkVisualization(Network n, TraitSubset s)
    {
        GoResultsViewer.getInstance().setVisible(false);
        this.n = n;
        this.ts = s;
        this.removeAll();
        this.removeListeners();

        if (!isJUNGView || (s != null && s.getIndeces().size() > 200) || (s == null && n.getTraitSet().getNumTraits() > 200))
        {
            this.isJUNGView = false;
            netmap.drawNetworkOnCanvas(this, n, s);
        }
        else
        {
            nv.drawNetworkOnCanvas(this, n, s, BorderLayout.CENTER, false, false, 0);
        }
        this.updateUI();

        view = TRAIT_VIEW;
    }

    /**
     * Given a GTA object, the association view updates so that it shows the
     * three-way association visualization.
     * @param gta
     */
    public void showThreeWayAssociationView(GeneTraitAssociation gta)
    {
        GoResultsViewer.getInstance().setVisible(false);
        this.removeAll();
        this.removeListeners();

        cv.drawOnCanvas(this, gta.getSNPAssoc().getMarkerSet(), BorderLayout.SOUTH, true, gta.getSNPAssoc(), true);
        gtnv.drawOnCanvas(this, BorderLayout.CENTER, gta);

        this.updateUI();
        view = THREEWAY_VIEW;
    }


    /**
     * Returns true if the current visualization is of a population analysis
     * @return
     */
    public boolean isPopulationAssoc()
    {
        return ac != null && ac.isPopAssocSet();
    }

    /**
     * Returns true if no traits are in chromosome view's current chart.
     * @return
     */
    public boolean isTraitsInChartEmpty()
    {
        return cv.isChartEmpty();
    }

    /**
     * Removes all listeners from this object.
     */
    private void removeListeners()
    {
        if (this.getMouseListeners().length > 0)
        {
            this.removeMouseListener(this.getMouseListeners()[0]);
            this.removeMouseWheelListener(this.getMouseWheelListeners()[0]);
            this.removeMouseMotionListener(this.getMouseMotionListeners()[0]);
            this.removeKeyListener(this.getKeyListeners()[0]);
        }
    }

    /**
     * This little dialog can be shown when coloring by association.
     * Both the marker and trait view need to have pointers to it.
     * @return
     */
    public AssociationColorKey getMyAssocScale()
    {
        return this.myscale;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}









