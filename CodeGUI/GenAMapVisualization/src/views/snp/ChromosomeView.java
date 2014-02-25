package views.snp;

import datamodel.Association;
import datamodel.Marker;
import datamodel.MarkerSet;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Set;
import datamodel.Trait;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JComponent;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.decorators.DefaultVertexIconTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.picking.PickedInfo;
import edu.uci.ics.jung.visualization.renderers.DefaultVertexLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.TransformerUtils;
import views.AssociationView;
import datamodel.AssociationSet;
import datamodel.Population;
import heatchart.HeatChart;
import realdata.DataManager;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Observable;
import javastat.inference.ChisqTest;
import javax.swing.SwingConstants;
import org.jfree.chart.ChartRenderingInfo;
import views.heatmap.HeatChartScale;
import views.network.AssociationColorKey;

/**
 * Allows the user to browse through the genome in order to view different SNP
 * markers.  Markers can be coded according to SNP strength.  
 * @author rcurtis
 * @author jmoffatt
 * @author mzuromskis
 * @author akgoyal
 */
public class ChromosomeView extends Observable
{
    /**
     * The visualization view is the JUNG object that is used to display the
     * network
     */
    private VisualizationViewer<Integer, Number> vv;
    /**
     * The association view container that holds this object
     */
    private AssociationView parent;
    /**
     * The color of the background
     */
    private static final Color BACKCOLOR = new Color(246, 248, 254);
    /**
     * The chromosome that the user is currently viewing
     */
    private int chromosome; //currently viewed chromosome
    /**
     * index of furthest left marker being viewed
     */
    private int leftIndex; //index of furthest left marker being viewed
    /**
     * index of furthest right marker being viewed
     */
    private int rightIndex; //index of furthest right marker being viewed
    /**
     * locus of the left edge of the viewing area
     */
    private int leftLocus; //locus of the left edge of the viewing area
    /**
     * locus of the right edge of the viewing area
     */
    private int rightLocus; //locus of the right edge of the viewing area
    /**
     * how much to zoom in with each mouse roll
     */
    private static final double ZOOM_FACTOR = 1.5;
    /**
     * the maximum number of nodes to put on the DNA line at a time
     */
    private static final int MAX_NODES_VISIBLE = 50;
    /**
     * old width
     */
    private int old_width;
    /**
     * TODO
     */
    private JPanel contentPanel;
    /**
     * TODO
     */
    private JPanel allContentPanel;
    /**
     * The jpanel that contains this visualization
     */
    private JPanel jp;
    /**
     * TODO
     */
    private String layoutLoc;
    /**
     * that graph needed by the visualization viewer
     */
    private UndirectedSparseGraph<Integer, Number> g;
    /**
     * the markerset that is currently being displayed
     */
    private MarkerSet myMarkers;
    /**
     * the markers currently being viewed
     */
    private ArrayList<Marker> viewing; //markers currently being viewed
    /**
     * all the markers on the current chromosome
     */
    private ArrayList<Marker> markers; //all markers on the current chromosome
    /**
     * subgroups of markers
     */
    private ArrayList<ArrayList<Marker>> groups; //subgroups of 'markers'
    /**
     * locus's of the dividers
     */
    private int[] dividers = new int[5]; //locus's of the dividers
    /**
     * maximum size of an entry in groups. determined such that viewing will be divided into MAX_NODES_VISIBLE groups.
     */
    private int groupsize; //maximum size of an entry in groups. determined such that 'viewing' will be divided into MAX_NODES_VISIBLE groups.
    /**
     * the left locus text field
     */
    private final JFormattedTextField tf1 = new JFormattedTextField(new DecimalFormat("0")); //the left locus text field
    /**
     * the right locus text field
     */
    private final JFormattedTextField tf2 = new JFormattedTextField(new DecimalFormat("0")); //the right locus text field
    /**
     * used to determine the color of nodes
     */
    private Map<Marker, Double> colormap; //map used to determine color of nodes
    /**
     * the popup handler that will interface with the user to interact with
     * the graph display
     */
    private ChromosomePopupHandler popupHandler;
    /**
     * the popup handler that works with the manhattan plot visualizatoin
     */
    private ChromosomeChartPopupHandler chartpopupHandler;
    /**
     * Different controls are available for association datasets, so we keep track
     */
    private boolean associated;
    /**
     * TODO
     */
    private ArrayList<Point2D.Double> pointNodes;
    /**
     * the manhattan plot control object which interfaces with JFreeChart to
     * display the information
     */
    protected ManhattanPlot cvt;
    /**
     * the image panel that is used to display the MHP. Not sure I understand 
     * the name
     */
    protected ImagePanel goleft1;
    /**
     * We use a static layout to position the chromosomal nodes exactly
     */
    private StaticLayout sl;
    /**
     * TODO
     */
    protected JComponent cPanel;
    /**
     * The association set that is currently being displayed with these markers
     */
    protected AssociationSet ac;
    /**
     * TODO
     */
    JPanel chr1;
    /**
     * TODO
     */
    protected Graph<? extends Object, ? extends Object>[] g_array;
    //TODO: refactor these variables into the MHP object
    /**
     * if we want to plot the jfreechart in log scale
     */
    protected boolean islogdisplay;
    /**
     * if the chart should be plotted as a log(pval)
     */
    protected boolean islogoption;
    /**
     * the y axis limit window
     */
    protected boolean yaxislimitwindow;
    /**
     * The object that manages the manhattan plots
     */
    protected ManhattanPlotsManager mpm;
    /**
     * the chart label on the MHP
     */
    protected JLabel chartLab1;
    /**
     * The other chart label on the MHP
     */
    protected JLabel chartLab2;
    /**
     * There are different functions available for three-way visualization sets.
     * We keep track of this.
     */
    private boolean is3WayVis = false;
    /**
     * TODO
     */
    private ChangableEllipseVertexShapeTransformer shapeTransformer = new ChangableEllipseVertexShapeTransformer();
    /**
     * A dialog that shows the values of an association
     */
    private final AssociationColorKey myscale;
    private Collection<Trait> myColoredTraits;

    /**
     * Returns true if this marker visualization is part of a 3-way vis
     * @return
     */
    public boolean getIs3Way()
    {
        return this.is3WayVis;
    }

    /**
     * Returns the log option for the MHP
     * @return
     */
    public boolean getislogoption()
    {
        return islogoption;
    }

    /**
     * returns the currently displayed MHP
     * @return
     */
    ManhattanPlot getChart()
    {
        return cvt;
    }

    /**
     * Sets the log option for the MHP
     * @param l
     */
    public void setislogoption(boolean l)
    {
        islogoption = l;
    }

    /**
     * Determines whether or not the MHP is using a log display
     * @return
     */
    public boolean getislogdisplay()
    {
        return islogdisplay;
    }

    /**
     * sets the log display for the MHP
     * @param l
     */
    public void setislogdisplay(boolean l)
    {
        islogdisplay = l;
        if (l)
        {
            islogoption = false;
        }
    }

    /**
     * constructor
     * @param aThis
     */
    public ChromosomeView(AssociationView aThis)
    {
        aThis.addComponentListener(new ComponentListener()
        {
            public void componentResized(ComponentEvent e)
            {
                positionMarkers();
                if (cvt != null)
                {
                    drawchart();
                }
            }

            public void componentMoved(ComponentEvent e)
            {
            }

            public void componentShown(ComponentEvent e)
            {
            }

            public void componentHidden(ComponentEvent e)
            {
            }
        });
        islogdisplay = false;
        yaxislimitwindow = false;
        islogoption = true;
        parent = aThis;
        this.myscale = aThis.getMyAssocScale();
        g = new UndirectedSparseGraph<Integer, Number>();
        sl = new StaticLayout(g);
        vv = new VisualizationViewer<Integer, Number>(sl);
        final MyDefaultModalGraphMouse<Integer, Number> graphMouse = new MyDefaultModalGraphMouse<Integer, Number>(this);
        graphMouse.setMode(Mode.PICKING);
        vv.setGraphMouse(graphMouse);
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
        vv.getRenderContext().setVertexFillPaintTransformer(new PickableVertexPaintTransformer<Integer>(vv.getPickedVertexState(), Color.green, Color.yellow));
        popupHandler = new ChromosomePopupHandler(this);
        chartpopupHandler = new ChromosomeChartPopupHandler(this);
        vv.addMouseListener(popupHandler);

        cPanel = null;
    }

    /**
     * Given the jp, which is the association view, this method
     * draws the marker visualization on the canvas for exploration by the user.
     * @param jp
     * @param m
     * @param layoutLoc
     * @param associated
     * @param ac
     * @param is3way
     */
    public void drawOnCanvas(JPanel jp, MarkerSet m, String layoutLoc, boolean associated, AssociationSet ac, boolean is3way)
    {
        if (m == null || m.getMarkers() == null || m.getMarkers().size() == 0)
        {
            return;
        }

        this.myMarkers = m;
        myscale.setVisible(false);
        myscale.refresh();

        this.is3WayVis = is3way;

        this.jp = jp;
        this.layoutLoc = layoutLoc;
        this.ac = ac;
        if (ac != null)
        {
            this.mpm = new ManhattanPlotsManager(ac);
            chartpopupHandler.setManhattanPlotManager(mpm);
        }
        else
        {
            this.mpm = null;
            chartpopupHandler.setManhattanPlotManager(mpm);
        }
        colormap = null;
        vv.setSize(new Dimension(jp.getWidth() - 210, 60));
        vv.setPreferredSize(new Dimension(jp.getWidth(), 60));
        vv.setBackground(jp.getBackground());

        goToChromosome(myMarkers.getMinChr());
        if (allContentPanel != null)
        {
            jp.remove(allContentPanel);
        }
        setupContentPanel(associated);


        jp.add(allContentPanel, layoutLoc);
        this.associated = associated;
        this.shapeTransformer.setDifferentMakers(new HashSet<Integer>(), groups);
    }

    /**
     * Returns the height of the CV
     * @return
     */
    public int getHeight()
    {
        if (allContentPanel == null)
        {
            return 140;
        }
        else
        {
            return allContentPanel.getHeight();
        }

    }

    /**
     * updates the type of the MHP from dot plot to linear plot, or vice-versa
     * @param isClassicManhattan
     */
    public void updateChartType(boolean isClassicManhattan)
    {
        cvt.ChangeChartType(isClassicManhattan);
        drawchart();
    }

    /**
     * Returns the population associated with this markerset
     * @return
     */
    public Population getPopulation()
    {
        if (cvt != null)
        {
            return cvt.getPopulation();
        }
        return null;
    }

    /**
     * Shows the frequency table or distribution view for the locus currently selected for the
     * trait currently in the MHP.
     * @param t
     * @param p
     * @param numPops
     */
    public void showFrequencyTable(ArrayList<Trait> t, Population p, int numPops)
    {
        ((MyDefaultModalGraphMouse) vv.getGraphMouse()).isInWait = true;
        ((MyDefaultModalGraphMouse) vv.getGraphMouse()).mouseMoved(null);
        //vv.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //allContentPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //contentPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //vv.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        Trait tr = t.get(0);
        Marker mr = this.getPickedMarkers().get(0);
        openFreqTable(tr, mr, p, numPops);

        ((MyDefaultModalGraphMouse) vv.getGraphMouse()).isInWait = false;
        //vv.setCursor(Cursor.getDefaultCursor());
    }

    /**
     * Does the calculations for the FQ table or DST view
     * @param tr
     * @param mr
     * @param pr
     * @param numPops
     * @throws NumberFormatException
     */
    private void openFreqTable(Trait tr, Marker mr, Population pr, int numPops) throws NumberFormatException
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("traitsetid=" + tr.getTraitSet().getId());
        where.add("trait.id=traitid");
        where.add("trait.id=" + tr.getId());
        ArrayList<String> cols = new ArrayList<String>();
        cols.add("sampleid");
        cols.add("value");
        ArrayList<HashMap<String, String>> tres = DataManager.runMultiColSelectQuery(cols, "trait, traitval", true, where, null);
        where.clear();
        where.add("markersetid=" + mr.getMarkerSetId());
        where.add("marker.id = markerid");
        where.add("marker.id=" + mr.getId());
        ArrayList<HashMap<String, String>> mres = DataManager.runMultiColSelectQuery(cols, "marker, markerval", true, where, null);
        where.clear();
        cols.clear();
        ArrayList<HashMap<String, String>> pres = null;
        if (pr != null)
        {
            if (pr.isStructureGenerated())
            {
                cols.add("pop" + numPops);
            }
            else
            {
                cols.add("pop1");
            }
            cols.add("sampleid");
            where.add("popstructid=" + pr.getId());
            pres = DataManager.runMultiColSelectQuery(cols, "structure", true, where, null);
        }
        HashMap<String, String> tcats = new HashMap<String, String>();
        boolean isBinary = true;
        for (HashMap<String, String> hm : tres)
        {
            String smpl = hm.get("sampleid");
            String val = hm.get("value");
            if (isBinary && !val.equals("1") && !val.equals("2") && !val.equals("-99"))
            {
                isBinary = false;
            }
            tcats.put(smpl, val);
        }
        HashMap<String, String> pops = new HashMap<String, String>();
        if (pres != null)
        {
            for (HashMap<String, String> hm : pres)
            {
                String smpl = hm.get("sampleid");
                String val1 = hm.get("pop1");
                String val2 = hm.get("pop" + numPops);
                String val = val1 == null ? val2 : val1;
                pops.put(smpl, val);
            }
        }
        if (isBinary)
        {
            double[][] table = new double[3][2];
            ArrayList<double[][]> tables = new ArrayList<double[][]>();
            tables.add(table);
            if (pr != null)
            {
                for (int i = 0; i < numPops; i++)
                {
                    tables.add(new double[3][2]);
                }
            }
            for (int i = 0; i < 3; i++)
            {
                for (int j = 0; j < 2; j++)
                {
                    table[i][j] = 0;
                    for (int k = 1; k < numPops + 1; k++)
                    {
                        tables.get(k)[i][j] = 0;
                    }
                }
            }
            for (HashMap<String, String> hm : mres)
            {
                String smpl = hm.get("sampleid");
                String val = hm.get("value");
                String tval = tcats.get(smpl);
                String pval = pops.get(smpl);
                int pno = pval == null ? -1 : Integer.parseInt(pval);

                int i = Integer.parseInt(val);
                if (i >= 0 && i < 3)
                {
                    int j = Integer.parseInt(tval);
                    if (j == 1)
                    {
                        table[i][0]++;
                        if (pno != -1)
                        {
                            tables.get(pno)[i][0]++;
                        }
                    }
                    if (j == 2)
                    {
                        table[i][1]++;
                        if (pno != -1)
                        {
                            tables.get(pno)[i][1]++;
                        }
                    }
                }
            }
            ChisqTest testclass1 = new ChisqTest(table);
            double testStatistic = testclass1.testStatistic;
            double pValue = testclass1.pValue;

            ArrayList<Double> pvals = new ArrayList<Double>();
            ArrayList<Double> X2s = new ArrayList<Double>();
            pvals.add(pValue);
            X2s.add(testStatistic);
            for (int i = 0; i < numPops; i++)
            {
                testclass1 = new ChisqTest(tables.get(i + 1));
                testStatistic = testclass1.testStatistic;
                pValue = testclass1.pValue;
                pvals.add(pValue);
                X2s.add(testStatistic);
            }

            FreqTableViewer ftv = new FreqTableViewer(tables, pvals, X2s, "Frequency table for: " + tr.getName() + " vs. " + mr.getName());
            ftv.setVisible();
        }
        else
        {
            ArrayList<double[]> sumtables = new ArrayList<double[]>();
            ArrayList<double[]> counttables = new ArrayList<double[]>();
            ArrayList<ArrayList<ArrayList<Double>>> vals = new ArrayList<ArrayList<ArrayList<Double>>>();
            double[] sums = new double[3];
            double[] counts = new double[3];
            sumtables.add(sums);
            counttables.add(counts);

            //if (pr != null)
            if (numPops == -1)
            {
                numPops++;
            }
            {
                for (int i = 0; i < numPops + 1; i++)
                {
                    sumtables.add(new double[3]);
                    counttables.add(new double[3]);
                    ArrayList<ArrayList<Double>> temp = new ArrayList<ArrayList<Double>>();
                    for (int j = 0; j < 3; j++)
                    {
                        temp.add(new ArrayList<Double>());
                    }
                    vals.add(temp);
                }
            }
            for (int i = 0; i < 3; i++)
            {
                sumtables.get(0)[i] = 0;
                counttables.get(0)[i] = 0;
                for (int k = 1; k < numPops + 1; k++)
                {
                    sumtables.get(k)[i] = 0;
                    counttables.get(k)[i] = 0;
                }

            }
            for (HashMap<String, String> hm : mres)
            {
                String smpl = hm.get("sampleid");
                String val = hm.get("value");
                String tval = tcats.get(smpl);
                String pval = pops.get(smpl);
                int pno = pval == null ? -1 : Integer.parseInt(pval);


                int i = Integer.parseInt(val);
                if (i >= 0 && i < 3)
                {
                    double d;

                    try
                    {
                        d = Double.parseDouble(tval);
                    }
                    catch (Exception e)
                    {
                        d = -99;
                    }

                    if (d != -99)
                    {
                        sumtables.get(0)[i] += d;
                        counttables.get(0)[i]++;
                        vals.get(0).get(i).add(d);
                        if (pno != -1)
                        {
                            sumtables.get(pno)[i] += d;
                            counttables.get(pno)[i]++;
                            vals.get(pno).get(i).add(d);
                        }
                    }
                }
            }

            for (int i = 0; i < sumtables.size(); i++)
            {
                for (int j = 0; j < 3; j++)
                {
                    sumtables.get(i)[j] = sumtables.get(i)[j] / counttables.get(i)[j];
                }
            }
            DistributionViewer dv = new DistributionViewer(vals, sumtables, counttables, "Distribution for: " + tr.getName() + " by " + mr.getName());
            dv.setVisible(true);
        }
    }

    /**
     * updates the chart as the view on the chr's is changed
     */
    public void updateChart()
    {
        if (myColoredTraits != null)
        {
            setColorMap(myColoredTraits);
        }

        if (cvt == null)
        {
            return;
        }
        else
        {
            cvt.ChangeLocation(leftLocus, rightLocus, islogdisplay, yaxislimitwindow);
            drawchart();
            chartLab2.setVisible(true);
            chartLab1.setVisible(mpm.hasTwoSeries());
            if (mpm.hasTwoSeries() || mpm.getIsPvalPlot())
            {
                chartLab2.setText("<html><br><BR><PRE>   -log10(pval)</html>");
            }
            else
            {
                chartLab2.setText("<html><br><BR><PRE>   value</html>");
            }

        }
    }

    /**
     * Adds traits to the MHP, could bring up a new one if it is not there,
     * or adds the traits to the view that already is there. Knocks off traits
     * if necessary.
     * @param p
     */
    public void addChart(ArrayList<Trait> p)
    {
        if (!islogoption)
        {
            islogdisplay = true;
        }

        ArrayList<Trait> n = new ArrayList<Trait>();
        if (cvt == null)
        {
            cvt = new ManhattanPlot(ac, p, leftLocus, rightLocus, groupsize, this.chromosome,
                    groups, BACKCOLOR, (int) vv.getWidth(), islogdisplay, yaxislimitwindow, false, mpm);
        }
        else
        {
            boolean isClassicMan = cvt.isClassicManhattan;
            ArrayList<Trait> nset = mpm.getDisplayedTraits();
            if (!ac.isPopAssocSet())
            {
                for (Trait h : nset)
                {
                    n.add(h);
                }
            }
            for (Trait h : p)
            {
                if (!n.contains(h))
                {
                    n.add(h);
                }
            }
            if (n.size() > 10)
            {
                Set<Trait> toremove = new HashSet<Trait>();

                for (Trait rr : n)
                {
                    if (toremove.size() >= n.size() - 10)
                    {
                        break;
                    }
                    toremove.add(rr);

                }
                n.removeAll(toremove);
            }
            cvt = new ManhattanPlot(ac, n, leftLocus, rightLocus, groupsize, this.chromosome,
                    groups, BACKCOLOR, (int) goleft1.getWidth(), islogdisplay, yaxislimitwindow, isClassicMan, mpm);

        }

        drawchart();
    }

    /**
     * Opens up the MHP for the user to view
     */
    public void drawchart()
    {
        if (mpm.getDisplayedTraits().size() == 0)
        {
            removeCharts();
            return;
        }
        BufferedImage image = cvt.getManhattanPlot().createBufferedImage(vv.getWidth(), 190, vv.getWidth(), 190, new ChartRenderingInfo());//+(int)(38*vv.getWidth()/639)
        double startpoint = cvt.getManhattanPlot().getXYPlot().graphStartPoint;
        double twidth = cvt.getManhattanPlot().getXYPlot().getRangeAxis().tickwidth;
        goleft1.graphStart = (int) startpoint + 61;
        goleft1.xinsect = vv.getWidth() + (mpm.hasTwoSeries() ? -64 + 42 : -59);
        goleft1.tickwidth = (int) twidth;
        goleft1.setImage(image);
        goleft1.addMouseListener(chartpopupHandler);
        goleft1.setVisible(true);
        goleft1.repaint();
        chartLab2.setVisible(true);
        chartLab1.setVisible(mpm.hasTwoSeries());
        if (mpm.hasTwoSeries() || mpm.getIsPvalPlot())
        {
            chartLab2.setText("<html><br><BR><PRE>   -log10(pval)</html>");
        }
        else
        {
            chartLab2.setText("<html><br><BR><PRE>   value</html>");
        }

        contentPanel.repaint();
    }

    private void goToChromosome(int i)
    {
        vv.getPickedVertexState().clear();
        chromosome = i;
        markers = new ArrayList<Marker>(myMarkers.getMarkersAtChr(chromosome));
        viewing = new ArrayList<Marker>(markers);
        if (markers.size() > 0)
        {
            leftIndex = 0;
            rightIndex = markers.size() - 1;
            leftLocus = 0;
            rightLocus = markers.get(rightIndex).getLocus() + 10000;
        }
        else
        {
            leftIndex = 0;
            rightIndex = 0;
            leftLocus = 0;
            rightLocus = 0;

        }
        tf1.setValue(leftLocus);
        tf2.setValue(rightLocus);
        visWorkingSet();
    }

    private void createVisualizationForChromosome()
    {
        groupsize = 1;

        Map<Integer, String> map2 = new HashMap<Integer, String>();
        if (vv != null)
        {
            jp.remove(vv);
        }
        g = new UndirectedSparseGraph<Integer, Number>();
        //vertices for the endpoints of the horizontal line
        g.addVertex(-1);
        g.addVertex(-2);
        map2.put(-1, "");
        map2.put(-2, "");

        //load viewed markers
        viewing.clear();
        for (int i = leftIndex; i <= rightIndex; i++)
        {
            viewing.add(markers.get(i));
        }

        //break up markers into groups
        groupsize = (int) java.lang.Math.ceil(((double) (rightIndex - leftIndex + 1)) / MAX_NODES_VISIBLE);
        groups = new ArrayList<ArrayList<Marker>>();
        for (int i = 0; i < markers.size(); i++)
        {
            if (groups.size() == i / groupsize)
            {
                groups.add(i / groupsize, new ArrayList<Marker>());
            }
            groups.get(i / groupsize).add(markers.get(i));
        }
        setChanged();
        notifyObservers(groups);

        //build the edge structure
        int e = 1;
        for (int i = 0; i < groups.size(); i++)
        {
            if (i == 0)
            {
                g.addEdge(e++, -1, i);
                if (groups.size() == 1)
                {
                    g.addEdge(e++, i, -2);
                }
            }
            else if (i == groups.size() - 1)
            {
                g.addEdge(e++, i - 1, i);
                g.addEdge(e++, i, -2);
            }
            else
            {
                g.addEdge(e++, i - 1, i);
            }
            g.addVertex(i);

            //determine the label for the node
            if (groups.get(i).size() == 1)
            {
                map2.put(i, locToKBString(groups.get(i).get(0).getLocus(), groups.get(i).get(0).getName(), null));
            }
            else
            {
                map2.put(i, locToKBString(groups.get(i).get(0).getLocus(), groups.get(i).get(0).getName(), null) + "-" + locToKBString(groups.get(i).get(groups.get(i).size() - 1).getLocus(), groups.get(i).get(groups.get(i).size() - 1).getName(), null) + " (" + groups.get(i).size() + ")");
            }
        }

        if (groups.size() == 0)
        {
            g.addEdge(3, -1, -2);
        }

        //make the vertical lines
        if (groups.size() > 1)
        {
            for (int i = 1; i <= 5; i++)
            {
                int j = groups.size() + 5 + 2 * i;
                g.addVertex(j);
                g.addVertex(j + 1);
                //determine their positions here since we use this same value for the label
                dividers[i - 1] = (int) (((double) i) / 6 * (rightLocus - leftLocus) + leftLocus);
                //label the lines
                map2.put(j, locToKBString(dividers[i - 1], null, null));
                map2.put(j + 1, "");
                g.addEdge(e++, j, j + 1);
            }
        }

        createChromosomeVisualization(g, positionMarkers(true), jp);
        setUpVisualization(map2);
    }

    private Transformer<Integer, Point2D> positionMarkers()
    {
        return positionMarkers(false);
    }

    public VisualizationViewer getVisualizationViewer()
    {
        return vv;
    }

    /**
     * Clear the MHPs from view
     */
    public void removeCharts()
    {
        if (goleft1 != null)
        {
            goleft1.setVisible(false);
            chartLab1.setVisible(false);
            chartLab2.setVisible(false);
        }
        cvt = null;
    }

    /**
     * TODO
     * @return
     */
    public ArrayList<Point2D.Double> getPoints()
    {
        return pointNodes;
    }

    /**
     * Determines the position of each marker
     * @param overrideWidth
     * @return
     */
    private Transformer<Integer, Point2D> positionMarkers(boolean overrideWidth)
    {
        if (vv != null && (vv.getWidth() != old_width || overrideWidth))
        {
            old_width = vv.getWidth();
            Map<Integer, Point2D> map = new HashMap<Integer, Point2D>();

            //position the endpoints of the horizontal line off the screen
            map.put(-1, new Point2D.Double(-10, 30));
            map.put(-2, new Point2D.Double(old_width + 10, 30));

            //if(groups.size() > 1)
            if (leftIndex != rightIndex)
            {
                double maxVal = rightLocus;
                double minVal = leftLocus;
                double min = 0;
                double max = vv.getWidth();

                //position vertical lines
                for (int i = 1; i <= 5; i++)
                {
                    int j = 2 * i + groups.size() + 5;
                    map.put(j, new Point2D.Double((dividers[i - 1] - minVal) / (maxVal - minVal) * (max - min) + min, -10));
                    map.put(j + 1, new Point2D.Double((dividers[i - 1] - minVal) / (maxVal - minVal) * (max - min) + min, 70));
                }

                //position nodes
                pointNodes = new ArrayList<Point2D.Double>();
                for (int i = 0; i < groups.size(); i++)
                {

                    double avg = ((double) (groups.get(i).get(0).getLocus() + groups.get(i).get(groups.get(i).size() - 1).getLocus())) / 2;
                    Point2D.Double cur = new Point2D.Double((avg - minVal) / (maxVal - minVal) * (max - min) + min, 30);
                    map.put(i, cur);
                    pointNodes.add(cur);
                }
            }
            else //if there is only one node
            {
                pointNodes = new ArrayList<Point2D.Double>();
                pointNodes.add(new Point2D.Double((jp.getWidth() - 250) / 2, 30));
                map.put(0, new Point2D.Double((jp.getWidth() - 250) / 2, 30));
                pointNodes.add(new Point2D.Double((jp.getWidth() - 250) / 2, 30));
            }

            Transformer<Integer, Point2D> vertexLocations = TransformerUtils.mapTransformer(map);

            sl = new StaticLayout(g, vertexLocations);
            sl.lock(true);
            // sl.getSize().
            if (vv != null)
            {
                vv.setGraphLayout(sl);
            }
            return vertexLocations;
        }
        return null;
    }

    /**
     * Draws the labels across the markers for the chromosome
     * @return
     */
    protected JPanel addLabelsForChromosome()
    {
        JPanel labels = new JPanel();
        labels.setLayout(new BorderLayout());
        labels.setBackground(BACKCOLOR);
        JPanel chr = new JPanel();
        chr.setBackground(BACKCOLOR);
        JLabel l = new JLabel("CHR ", JLabel.CENTER);
        l.setFont(new Font("Tahoma", Font.BOLD, 11));
        chr.add(l);

        //chromosome selection combo box
        final JComboBox chroms = new JComboBox();
        chroms.setBackground(BACKCOLOR);
        chroms.setFont(new Font("Tahoma", Font.PLAIN, 11));
        for (int i = 1; i <= myMarkers.getNumChr(); i++)
        {
            if (myMarkers.getMarkersAtChr(i).size() > 0)
            {
                chroms.addItem(new Integer(i));
            }
        }
        chroms.setSelectedItem(chromosome);
        chroms.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (chroms.getSelectedItem().equals(chromosome))
                {
                    return;
                }
                goToChromosome((Integer) chroms.getSelectedItem());
                if (cvt != null)
                {
                    addChart(mpm.getDisplayedTraits());
                }
            }
        });

        chr.add(chroms);
        chr.add(new JLabel("    "));
        //labels.add(chr, BorderLayout.NORTH);
        //JPanel loc = new JPanel();
        if (viewing.size() > 0)
        {
            //set up the locus text fields
            tf1.setPreferredSize(new Dimension(60, 25));
            tf2.setPreferredSize(new Dimension(60, 25));
            chr.add(tf1);
            chr.add(new JLabel(" - "));
            chr.add(tf2);
            tf1.setValue(leftLocus);//(markers.get(leftIndex).getLocus());
            tf2.setValue(rightLocus);//(markers.get(rightIndex).getLocus());
            chr.add(new JLabel("    "));
            //button to change the viewing region to the values specified in the locus text fields
            final JButton jb = new JButton("Update View");
            jb.setFont(new Font("Tahoma", Font.PLAIN, 11));
            jb.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    int left = java.lang.Math.max(0, Integer.valueOf(tf1.getText()));
                    int right = java.lang.Math.min(markers.get(markers.size() - 1).getLocus(), Integer.valueOf(tf2.getText()));
                    if (left < right)
                    {
                        leftLocus = left;
                        rightLocus = right;
                        leftIndex = findClosestMarker(leftLocus, false);
                        rightIndex = findClosestMarker(rightLocus, true);
                        ArrayList<Marker> sel = getPickedMarkers();
                        visWorkingSet();
                        //setPickedMarkers(sel);
                        tf1.setValue(leftLocus);
                        tf2.setValue(rightLocus);
                        //if (cPanel != null)
                        // {
                        //     updateCharts();
                        // }
                        if (cvt != null)
                        {
                            addChart(mpm.getDisplayedTraits());
                        }
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(jb, "Minimum Locus is higher than Maximum Locus", "Error", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });
            chr.add(jb);
        }
        else
        {
            chr.add(new JLabel("no markers"), JLabel.CENTER);
        }
        labels.add(chr, BorderLayout.CENTER);
        return labels;
    }

    /**
     * sets up the graph layout object for the chromosome visualization
     * @param g
     * @param vertexLocations
     * @param jp
     */
    protected void createChromosomeVisualization(Graph g, Transformer<Integer, Point2D> vertexLocations, JPanel jp)
    {
        sl = new StaticLayout(g, vertexLocations);
        sl.lock(true);
        vv.getModel().setGraphLayout(sl);

    }

    private void setUpVisualization(Map<Integer, String> map2)
    {
        final Transformer<Integer, String> vertexStringerImpl = new VertexStringerImpl<Integer>(map2);
        Map<Integer, String> map3 = new HashMap<Integer, String>(map2);
        for (int i = 0; i <= groups.size() + 5; i++)
        {
            map3.remove(i);
        }
        final Transformer<Integer, String> vertexStringerImpl2 = new VertexStringerImpl<Integer>(map3);
        vv.getRenderContext().setVertexLabelTransformer(vertexStringerImpl2);

        DefaultVertexLabelRenderer labelCreation = new DefaultVertexLabelRenderer(Color.black);
        labelCreation.setFont(new Font("Times", Font.PLAIN, 300));
        labelCreation.setBackground(Color.red);
        vv.getRenderContext().setVertexLabelRenderer(labelCreation);
        labelCreation = ((DefaultVertexLabelRenderer) vv.getRenderContext().getVertexLabelRenderer());
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.SW);

        Transformer<Integer, Paint> vpf = new PickableVertexPaintTransformer<Integer>((PickedInfo) vv.getPickedVertexState(), (Paint) Color.green, (Paint) Color.yellow);
        //if a colormap is specified, then we need to use it
        if (colormap != null)
        {
            Map<Integer, Double> middlemap = new HashMap<Integer, Double>();
            Map<Integer, Color> integerColorMap = new HashMap<Integer, Color>();
            for (Marker m : colormap.keySet())
            { //determine the maximum value for each group
                int index = findMarker(m);
                if (index == -1)
                {
                    continue;
                }
                if (middlemap.get(index / groupsize) == null)
                {
                    middlemap.put(index / groupsize, colormap.get(m));
                }
                else
                {
                    middlemap.put(index / groupsize, java.lang.Math.max(colormap.get(m), middlemap.get(index / groupsize)));
                }
            }
            for (Integer i : middlemap.keySet())
            { //determine the color for each group
                Double val = middlemap.get(i);
                float G = val.floatValue();
                float B = val.floatValue();
                float R = val.floatValue();
                integerColorMap.put(i, new Color(R, G, B));
            }
            vpf = new MyPickableVertexPaintTransformer<Integer>((PickedInfo) vv.getPickedVertexState(), integerColorMap, (Paint) Color.black, (Paint) Color.yellow);
        }
        vv.getRenderContext().setVertexFillPaintTransformer(vpf);
        vv.getRenderContext().setEdgeDrawPaintTransformer(new PickableEdgePaintTransformer<Number>(vv.getPickedEdgeState(), Color.black, Color.cyan));

        Map<Integer, Icon> iconMap = new HashMap<Integer, Icon>();
        //final VertexIconShapeTransformer<Integer> vertexImageShapeFunction = new VertexIconShapeTransformer<Integer>(new ChangableEllipseVertexShapeTransformer());
        final DefaultVertexIconTransformer<Integer> vertexIconFunction = new DefaultVertexIconTransformer<Integer>();
        //vertexImageShapeFunction.setIconMap(iconMap);
        vertexIconFunction.setIconMap(iconMap);
        this.addObserver(shapeTransformer);
        vv.getRenderContext().setVertexShapeTransformer(shapeTransformer);
        vv.getRenderContext().setVertexIconTransformer(vertexIconFunction);
        vv.setVertexToolTipTransformer(vertexStringerImpl);
    }

    private void setupContentPanel(boolean associated)
    {
        contentPanel = new JPanel(new BorderLayout());
        allContentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKCOLOR);
        JPanel labels = addLabelsForChromosome();
        //contentPanel.add(labels, BorderLayout.NORTH);
        contentPanel.add(vv, BorderLayout.CENTER);

        //just check
        JPanel sss = new JPanel(new BorderLayout());
        sss.setBackground(BACKCOLOR);

        goleft1 = new ImagePanel();
        goleft1.addMouseListener(chartpopupHandler);
        goleft1.addMouseWheelListener(chartpopupHandler);
        goleft1.setSize(vv.getWidth(), vv.getHeight());
        goleft1.setVisible(false);
        sss.add(goleft1, BorderLayout.CENTER);
        sss.add(labels, BorderLayout.NORTH);
        JPanel contPan = new JPanel();
        contPan.setBackground(BACKCOLOR);

        final JPanel left = new JPanel();
        final JPanel right = new JPanel();

        //ClassLoader cldr = this.getClass().getClassLoader();
        //java.net.URL imageURL = cldr.getResource("images/left_arrow.gif")
        ImageIcon leftIcon = new ImageIcon(ChromosomeView.class.getResource("images/left_arrow.gif"));
        final JButton goleft = new JButton(leftIcon);
        final Insets margins = new Insets(0, 0, 0, 0);
        goleft.setMargin(margins);

        ImageIcon rightIcon = new ImageIcon(ChromosomeView.class.getResource("images/right_arrow.gif"));
        final JButton goright = new JButton(rightIcon);
        goright.setMargin(margins);

        //button to slide the viewed region to the left
        goleft.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                shift(-vv.getWidth() / 9);
            }
        });

        //button to slide the viewed region to the right
        goright.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                shift(vv.getWidth() / 9);
            }
        });

        String name = "images/dna_L.jpg";
        String name2 = "images/dna_R.jpg";
        //slide buttons + the DNA image next to them
        left.setBackground(BACKCOLOR);
        left.add(goleft, BorderLayout.WEST);
        left.add(new JLabel(new ImageIcon(ChromosomeView.class.getResource(name))), BorderLayout.EAST);
        right.setBackground(BACKCOLOR);
        right.add(new JLabel(new ImageIcon(ChromosomeView.class.getResource(name2))), BorderLayout.WEST);
        right.add(goright, BorderLayout.EAST);

        ////contentPanel.add(contPan, BorderLayout.SOUTH);
        contentPanel.add(right, BorderLayout.EAST);
        contentPanel.add(left, BorderLayout.WEST);

        allContentPanel = new JPanel(new BorderLayout());
        allContentPanel.add(contentPanel, BorderLayout.SOUTH);

        chartLab1 = new JLabel("<HTML><BR><BR><PRE>value (dashed)   </HTML>", SwingConstants.RIGHT);
        sss.add(chartLab1, BorderLayout.EAST);
        chartLab2 = new JLabel("<html><br><BR><PRE>   -log10(pval)</html>", SwingConstants.LEFT);
        sss.add(chartLab2, BorderLayout.WEST);
        chartLab1.setUI(new VerticleLabelUI(true));
        chartLab2.setUI(new VerticleLabelUI(false));
        chartLab1.setVisible(false);
        chartLab2.setVisible(false);
        ////allContentPanel.add(cPanel, BorderLayout.NORTH);
        allContentPanel.add(sss, BorderLayout.NORTH);
        jp.add(allContentPanel, layoutLoc);
    }

    private void visWorkingSet()
    {
        createVisualizationForChromosome();
        jp.updateUI();
    }

    //translate a locus to a string in kilo bases
    private String locToKBString(int loc, String name1, String name2)
    {
        String toRet;
        if (loc < 100)
        {
            toRet = loc + "";
        }
        else
        {
            toRet = (double) (loc / 100) / 10 + "kb";
        }

        if (name1 != null)
        {
            toRet += " " + name1;
        }
        if (name2 != null)
        {
            toRet += "-" + name2;
        }

        return toRet;
    }

    /**
     * get a list of the markers belonging to the selected nodes. 
     */
    public ArrayList<Marker> getPickedMarkers()
    {
        ArrayList<Marker> verts = new ArrayList<Marker>();
        for (Integer i : vv.getPickedVertexState().getPicked())
        {
            if (i < groups.size() && i >= 0)
            {
                verts.addAll(groups.get(i));
            }
        }
        //vv.getPickedVertexState().clear();
        return verts;
    }

    /**
     * find the index of the marker m.
     * @param m
     * @return -1 if not found
     */
    private int findMarker(Marker m)
    {
        for (int i = 0; i < markers.size(); i++)
        {
            if (markers.get(i).getChromosome() == m.getChromosome() && markers.get(i).getLocus() == m.getLocus())
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * finds the index of the marker whos locus is closest to loc when rounding up or down, as specified by roundup
     * @param loc
     * @param roundup
     * @return if loc is less than 0, then always returns 0.  if loc is greater than the greatest locus of all markers, then returns the index of the greatest marker.
     */
    private int findClosestMarker(int loc, boolean roundup)
    {
        if (loc < 0)
        {
            return 0;
        }
        if (loc > markers.get(markers.size() - 1).getLocus())
        {
            return markers.size() - 1;
        }
        for (int i = 0; i < markers.size(); i++)
        {
            if (markers.get(i).getLocus() == loc)
            {
                return i;
            }
            if (markers.get(i).getLocus() > loc)
            {
                if (roundup)
                {
                    return i;
                }
                else
                {
                    return java.lang.Math.max(i - 1, 0);
                }
            }
        }
        return -1;
    }

    /**
     * zooms the display all the way out
     */
    public void resetView()
    {
        ArrayList<Marker> sel = getPickedMarkers();
        goToChromosome(chromosome);
        //setPickedMarkers(sel);
        vv.getPickedVertexState().clear();
        tf1.setValue(leftLocus);
        tf2.setValue(rightLocus);
        if (cvt != null)
        {
            updateChart();
        }
        //  addChart(cvt.getSelected());
    }

    /**
     * shifts the display by pix pixels. negative pix shifts left, positive shifts right.
     * @param pix
     */
    public void shift(int pix)
    {
        double prop = (double) pix / vv.getWidth();
        int shift = 0;
        if (pix > 0)
        {
            shift = java.lang.Math.min(java.lang.Math.max((int) ((rightLocus - leftLocus) * prop), 1), markers.get(markers.size() - 1).getLocus() - rightLocus + 10000);
        }
        else if (pix < 0)
        {
            shift = -java.lang.Math.min(java.lang.Math.max((int) ((rightLocus - leftLocus) * -prop), 1), leftLocus);
        }
        else
        {
            return;
        }
        leftLocus += shift;
        rightLocus += shift;
        ArrayList<Marker> sel = getPickedMarkers();
        visWorkingSet();
        //setPickedMarkers(sel);
        tf1.setValue(leftLocus);
        tf2.setValue(rightLocus);
        if (cvt != null)
        {
            updateChart();
        }
        // addChart(cvt.getSelected());
    }

    /**
     * zooms the display in
     */
    public void zoomIn()
    {
        int min = groups.size(), max = 0;
        leftIndex = findClosestMarker(leftLocus, false);
        rightIndex = findClosestMarker(rightLocus, true);

        //if nodes are selected, then do zoomSelect instead
        for (Integer i : (Collection<Integer>) vv.getPickedVertexState().getPicked())
        {
            if (i < 0)
            {
                continue;
            }
            min = java.lang.Math.min(min, i);
            max = java.lang.Math.max(max, i);
        }
        if (!(min <= leftIndex / groupsize && max >= rightIndex / groupsize) && !(vv.getPickedVertexState().getPicked().isEmpty()))
        {
            zoomSelect();
            vv.getPickedVertexState().clear();
            return;
        }

        int center = (leftIndex + rightIndex) / 2;
        int halfwidth = (rightIndex - leftIndex + 1) / 2;
        halfwidth /= 1 + (ZOOM_FACTOR - 1) / 2;
        halfwidth = java.lang.Math.max(halfwidth, 1);
        leftIndex = center - halfwidth;
        rightIndex = center + halfwidth;
        leftIndex = java.lang.Math.max(leftIndex, 0);
        rightIndex = java.lang.Math.min(rightIndex, markers.size() - 1);
        leftLocus = markers.get(leftIndex).getLocus();
        rightLocus = markers.get(rightIndex).getLocus();
        ArrayList<Marker> sel = getPickedMarkers();
        visWorkingSet();
        //setPickedMarkers(sel);
        tf1.setValue(leftLocus);
        tf2.setValue(rightLocus);
        if (cvt != null)
        {
            updateChart();
        }
        vv.getPickedVertexState().clear();
        // addChart(cvt.getSelected());
    }

    /**
     * zoom the display out
     */
    public void zoomOut()
    {
        if (viewing.containsAll(markers))
        {
            return;
        }
        leftIndex = findClosestMarker(leftLocus, false);
        rightIndex = findClosestMarker(rightLocus, true);

        int center = (leftIndex + rightIndex) / 2;
        int halfwidth = (rightIndex - leftIndex + 1) / 2;
        halfwidth = java.lang.Math.max(halfwidth + 1, (int) (halfwidth * ZOOM_FACTOR));
        leftIndex = java.lang.Math.max(0, center - halfwidth);
        rightIndex = java.lang.Math.min(center + halfwidth, markers.size() - 1);
        leftLocus = markers.get(leftIndex).getLocus();
        rightLocus = markers.get(rightIndex).getLocus();
        if (leftIndex == 0)
        {
            leftLocus = 0;
        }
        if (rightLocus == markers.size() - 1)
        {
            rightLocus += 10000;
        }
        ArrayList<Marker> sel = getPickedMarkers();
        visWorkingSet();
        //setPickedMarkers(sel);
        vv.getPickedVertexState().clear();
        tf1.setValue(leftLocus);
        tf2.setValue(rightLocus);
        if (cvt != null)
        {
            updateChart();
        }
        //// addChart(cvt.getSelected());
    }

    /**
     * zoom in on the selected nodes
     */
    public void zoomSelect()
    {
        if (vv.getPickedVertexState().getPicked().isEmpty())
        {
            return;
        }

        int min = groups.size(), max = 0;
        for (Integer i : (Collection<Integer>) vv.getPickedVertexState().getPicked())
        {
            if (i == -1 || i == -2)
            {
                continue;
            }
            min = java.lang.Math.min(min, i);
            max = java.lang.Math.max(max, i);
        }
        if (min > max)
        {
            return;
        }
        leftIndex = 0;
        for (int i = leftIndex / groupsize; i < min; i++)
        {
            leftIndex += groups.get(i).size();
        }
        rightIndex = markers.size() - 1;
        for (int i = rightIndex / groupsize; i > max; i--)
        {
            rightIndex -= groups.get(i).size();
        }
        if (leftIndex == rightIndex)
        {
            leftIndex = java.lang.Math.max(leftIndex - 1, 0);
            rightIndex = java.lang.Math.min(rightIndex + 1, markers.size() - 1);
        }
        leftLocus = markers.get(leftIndex).getLocus();
        rightLocus = markers.get(rightIndex).getLocus();
        ArrayList<Marker> sel = getPickedMarkers();
        visWorkingSet();
        //setPickedMarkers(sel);
        tf1.setValue(leftLocus);
        tf2.setValue(rightLocus);
        if (cvt != null)
        {
            updateChart();
        }
        // addChart(cvt.getSelected());
        //  updateCharts();
    }

    /**
     * sets the map which determines the colors of nodes.  only should be used when viewing associations.
     * @param map
     */
    public void setColorMap(Collection<Trait> traitset)
    {
        ArrayList<Integer> traits = new ArrayList<Integer>();
        for (Trait t : traitset)
        {
            traits.add(t.getId());
        }
        myColoredTraits = traitset;

        Collection<Association> assocs = ac.findAssociationsFromTraits(traits, -1);
        Map<Marker, Double> map = new HashMap<Marker, Double>();
        double max = 0.0;

        for (Association a : assocs)
        {
            double val;
            if (ac.getIsPvals())
            {
                val = -1 * Math.log10(a.getValue());
            }
            else
            {
                val = Math.abs(a.getValue());
            }
            if (val > max)
            {
                max = val;
            }
        }

        for (Association a : assocs)
        {
            double val;
            if (ac.getIsPvals())
            {
                val = -1 * Math.log10(a.getValue());
            }
            else
            {
                val = Math.abs(a.getValue());
            }

            val = val / max;
            val = val / 2;
            val = val + .5;
            if (map.get(a.getMarker()) == null)
            {
                map.put(a.getMarker(), val);
            }
            else
            {
                map.put(a.getMarker(), java.lang.Math.max(val, map.get(a.getMarker())));
            }
        }
        //if (!ac.getIsPvals())
        {
            myscale.setMarkerAssociation(HeatChartScale.DrawScale(0.0,
                    max + max / 100.0, 366,
                    HeatChart.SCALE_LINEAR, Color.GRAY, Color.WHITE, 10, Color.WHITE));
        }

        colormap = map;
        ArrayList<Marker> sel = getPickedMarkers();
        visWorkingSet();
        //setPickedMarkers(sel);
    }

    /**
     * Called in order to have the traits associated with the selected markers
     * colored according to their association with the selected markers.
     */
    public void highlightAssociatedTraits()
    {
        ArrayList<Marker> ms = getPickedMarkers();
        //setPickedMarkers(ms);
        /*ArrayList<Integer> list = new ArrayList<Integer>();
        for(Marker m : ms)
        {
        list.add(m.getId());
        }*/
        ((ChangableEllipseVertexShapeTransformer) vv.getRenderContext().getVertexShapeTransformer()).setDifferentMakers(vv.getPickedVertexState().getPicked(), groups);
        vv.updateUI();
        parent.highlightAssociatedTraits(ms);
    }

    /**
     * Finds and saves all associated traits (with these markers) as a trait subset
     */
    public void saveAssociationTraits()
    {
        ArrayList<Marker> ms = getPickedMarkers();
        parent.saveAssociatedAsSubset(ms);
    }

    /**
     * Resets all vertex colors back to green
     */
    public void resetVertexColors()
    {
        vv.getRenderContext().setVertexFillPaintTransformer(
                new PickableVertexPaintTransformer<Integer>(
                vv.getPickedVertexState(), Color.green, Color.yellow));
        colormap = null;
        vv.repaint();
    }

    /**
     * Returns true if the current display involves an association between
     * markers and traits.
     * @return
     */
    public boolean getAssociated()
    {
        return associated;
    }

    /**
     * Returns true if there is no MHP displayed or inited
     * @return
     */
    public boolean isChartEmpty()
    {
        if (cvt == null)
        {
            return true;
        }
        else
        {
            return this.mpm.getDisplayedTraits().isEmpty();
        }
    }

    /**
     * Updates the association set that this chromosome view will use to
     * color the networks and to draw charts. 
     * @param a
     */
    public void setAssociationSet(AssociationSet a)
    {
        AssociationSet old = ac;
        this.ac = a;
        mpm.setManhattanAssociationSets(ac);

        if (cvt != null)
        {
            if (old.getMarkerSet() == ac.getMarkerSet())
            {
                cvt.setup(chromosome, ac);
                cvt.ChangeLocation(leftLocus, rightLocus, islogdisplay, yaxislimitwindow);
                this.islogoption = !ac.getIsPvals();
            }
            else
            {
                this.removeCharts();
            }
        }
    }

    /**
     * Returns the number of populations in the MHP
     * @return
     */
    public int getNumPops()
    {
        if (cvt != null)
        {
            return cvt.getNumPops();
        }
        return -1;
    }

    /**
     * Return the markerset that I am showing to the world. 
     * @return
     */
    public MarkerSet getMarkerSet()
    {
        return this.myMarkers;
    }

    /**
     * Removes itself from the association view.
     */
    public void removeFromCanvas()
    {
        jp.remove(this.allContentPanel);
    }

    /**
     * Returns the current chromosome being displayed.
     * @return
     */
    public int getChromosome()
    {
        return this.chromosome;
    }

    /**
     * Returns the current associationset this markerview is helping to display
     */
    public AssociationSet getAssocSet()
    {
        return this.ac;
    }
}
