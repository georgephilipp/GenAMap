package views.snp;

import datamodel.Trait;
import datamodel.MarkerSet;
import datamodel.Marker;
import java.util.ArrayList;
import java.awt.Dimension;
import java.io.Serializable;

import java.awt.Color;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;
import datamodel.AssociationSet;
import datamodel.Model;
import datamodel.Population;
import java.awt.BasicStroke;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.ui.RectangleEdge;

/**
 * In order to consider associations across a chromosome, users can add
 * a Manhattan plot across the chromosome. This can be done using multiple
 * traits or populations. We could customize this more in the future if we
 * would like to.
 * 
 * @author mzuromskis
 * @author akgoyal
 */
public class ManhattanPlot implements Serializable
{
    /**
     * The background color of the chart that matches the JPanel.
     */
    private Color BACKCOLOR;
    /**
     * I think that this is the JPanel that the manhattan plot is added
     * to and then updated. 
     */
    private JPanel jp;
    /**
     * The plot of the p-values or beta values taht we will show the user. 
     */
    private JFreeChart manhattan;
    /**
     * The number of populations in this association set that we are working with
     */
    private int numberPopulations;
    /**
     * The population object for this association set. Will be null if there
     * isn't one. 
     */
    private Population populations;
    /**
     * The pointer to the markerset for this manhattan plot. 
     */
    private MarkerSet m;
    /**
     * the location of zooming or scrolling
     */
    private int leftLocus;
    /**
     * the location of zooming or scrolling.
     */
    private int rightLocus;
    /**
     * The width of the view. 
     */
    private int cwidth;
    /**
     * The markers on display in the screen. 
     */
    ArrayList<ArrayList<Marker>> markers;
    /**
     * Determines the type of display - is the Y axis on a log scale or
     * a linear scale?
     */
    boolean isLogDisplay = false;
    /**
     * Should the Y Axis window be fixed at a certain size? 
     */
    boolean isFixYAxisWindow = false;
    /**
     * This tells if graph is in classical Manhattan format
     */
    protected boolean isClassicManhattan;
    /**
     * This arraylist keeps the population to be removed in chart
     */
    protected ArrayList<Integer> populationToRemove;
    /**
     * Determines whether to take the absolute value of values in the plot or not
     */
    boolean isAbs = false;
    /**
     * The manager that determines what associations to draw on the chart
     */
    private ManhattanPlotsManager manager;

    /**
     * Returns the currently created Manhattan Plot to draw in the
     * chromosome view. 
     * @return
     */
    public JFreeChart getManhattanPlot()
    {
        return manhattan;
    }

    /**
     * Returns null if there isn't a population component, or the population if
     * there is one associated with this chart.
     * @return
     */
    public Population getPopulation()
    {
        if (this.populations != null)
        {
            return populations;
        }
        return null;
    }

    /**
     * This function updates the chart according to zoom-in zoom-out and popup of the chart
     * to display the correct SNPs.
     * Let's function save so we don't have to requery if we are on the same cx.
     */
    public void ChangeLocation(int left, int right, boolean islog, boolean isYAxWindow)
    {
        leftLocus = left;
        rightLocus = right;
        isLogDisplay = islog;
        isFixYAxisWindow = isYAxWindow;
        manager.checkNumberOfTraits();
        manhattan = createChart();
    }

    /**
     * Changes the chart type between linear and dot.
     * @param isClassicManhatan
     */
    public void ChangeChartType(boolean isClassicManhatan)
    {
        isClassicManhattan = isClassicManhatan;
        manhattan = createChart();
    }

    /**
     * Creates a new ManhattanPlot object. 
     * @param a the association set we are looking at.
     * @param picked the traits that we are currently looking at. 
     * @param left where the left locus is located
     * @param right where the right locus is located
     * @param size the size of the chart
     * @param g the markers that we are currently displaying.
     * @param bg the background color of the chart. 
     * @param width the width of the chart
     * @param logd whether or not we should display this on log scale
     * @param yaxwind how we should treat the y axis. 
     */
    public ManhattanPlot(AssociationSet a, ArrayList<Trait> picked,
            int left, int right, int size, int chr,
            ArrayList<ArrayList<Marker>> g, Color bg, int width,
            boolean logd, boolean yaxwind, boolean isclassicMan, ManhattanPlotsManager mpm)
    {
        cwidth = width;
        BACKCOLOR = bg;
        markers = g;
        leftLocus = left;
        rightLocus = right;
        isClassicManhattan = isclassicMan;
        isLogDisplay = logd;
        isFixYAxisWindow = yaxwind;
        manager = mpm;

        leftLocus = left;
        rightLocus = right;
        this.populations = a.getPopulation();
        manager.setUpTraitArray(picked);
        jp = new JPanel();

        setup(chr, a);
        populationToRemove = new ArrayList<Integer>();
        manhattan = createChart();

        BufferedImage image = manhattan.createBufferedImage(cwidth, 122);

        jp.setLayout(null);
        jp.setPreferredSize(new Dimension(cwidth + 20, 122));
        JLabel lblChart = new JLabel();
        lblChart.setIcon(new ImageIcon(image));
        jp.add(lblChart);
        jp.setBackground(BACKCOLOR);
    }

    /*
     * This function query database to get the asscoiation. It is called once from constructor
     * and after that same data is used if zoom-in zoom-out is clicked. This is also called
     * when the association set is changed ...
     */
    public void setup(int chr, AssociationSet ac)
    {
        this.populations = ac.getPopulation();
        this.numberPopulations = ac.getNumPops();
        m = ac.getMarkerSet();
        this.isLogDisplay = ac.getIsPvals();
        ArrayList<Integer> marker = m.getMarkerIds(chr);
        manager.setupDataSets(marker, markers, isAbs, isLogDisplay, numberPopulations, this.populationToRemove);
    }

    /**
     * Create the Manhattan plot given the current dataset. 
     * @param dataset the dataset to use to create the chart. 
     * @return
     */
    private JFreeChart createChart()
    {
        String title = manager.getTitle();
        XYSeriesCollection dataset = manager.getDataset(0);
        XYSeriesCollection dataset2 = manager.getDataset(1);

        JFreeChart chart1 = ChartFactory.createXYLineChart(
                title, // chart title
                null, // domain axis label
                null, // range axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips
                false // urls
                );

        chart1.getLegend().setPosition(RectangleEdge.TOP);
        if (populations != null && manager.getDisplayedAssocSets().size() == 1)
        {
            chart1.getLegend().setVisible(false);
        }
        chart1.getXYPlot().getDomainAxis().setRange(leftLocus, rightLocus);
        chart1.getXYPlot().setBackgroundPaint(new Color(245, 255, 245));
        int newnumPops = numberPopulations - this.populationToRemove.size();
        if (dataset2 != null)
        {
            NumberAxis axis = new NumberAxis();
            chart1.getXYPlot().setRangeAxis(1, axis);
            chart1.getXYPlot().setDataset(1, dataset2);
            chart1.getXYPlot().mapDatasetToRangeAxis(1, 1);
            chart1.getXYPlot().getRangeAxis(1).setAxisLineVisible(false);
            chart1.getXYPlot().getRangeAxis(1).setTickMarksVisible(false);
            XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer();
            renderer2.setShapesVisible(true);
            if (isClassicManhattan)
            {
                renderer2.setLinesVisible(false);
            }
            for (int i = 0; newnumPops > 1 &&
                    i < newnumPops * manager.getDisplayedAssocSets().size(); i += newnumPops)
            {
                for (int j = 0; j < newnumPops; j++)
                {
                    renderer2.setSeriesPaint(i + j, Model.colors[(0 + j) % Model.colors.length]);
                    renderer2.setSeriesStroke(i + j, new BasicStroke(
                            2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                            1.0f, new float[]
                            {
                                6.0f, 6.0f
                            }, 0.0f));
                }
            }
            for (int i = 0; newnumPops < 2 &&
                    i < manager.getDisplayedTraits().size() * manager.getDisplayedAssocSets().size();
                    i += manager.getDisplayedTraits().size())
            {
                for (int j = 0; j < manager.getDisplayedTraits().size(); j++)
                {
                    renderer2.setSeriesPaint(i + j, Model.colors[(0 + j) % Model.colors.length]);
                    renderer2.setSeriesStroke(i+j, new BasicStroke(
                            2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                            1.0f, new float[]
                            {
                                6.0f, 6.0f
                            }, 0.0f));
                }
            }
            chart1.getXYPlot().setRenderer(1, renderer2);
        }
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart1.getXYPlot().getRenderer();
        if (manager.getDisplayedAssocSets().size() > 1)
        {
            renderer.setShapesVisible(true);
        }

        if (this.isClassicManhattan)
        {
            renderer.setShapesVisible(true);
            renderer.setLinesVisible(false);
        }

        for (int i = 0; newnumPops > 1 &&
                i < newnumPops * manager.getDisplayedAssocSets().size(); i += newnumPops)
        {
            for (int j = 0; j < newnumPops; j++)
            {
                renderer.setSeriesPaint(i + j, Model.colors[(0 + j) % Model.colors.length]);
            }
        }
        for (int i = 0; newnumPops < 2 &&
                i < manager.getDisplayedTraits().size() * manager.getDisplayedAssocSets().size();
                i += manager.getDisplayedTraits().size())
        {
            for (int j = 0; j < manager.getDisplayedTraits().size(); j++)
            {
                renderer.setSeriesPaint(i + j, Model.colors[(0 + j) % Model.colors.length]);
            }
        }
        /*for (int pr : populationToRemove)
        {
            renderer.setSeriesVisible(pr - 1, false);
        }*/
        if (!isFixYAxisWindow)
        {
            manager.setOverallChartRange(chart1, isAbs, isLogDisplay);
        }

        chart1.getXYPlot().getRangeAxis(0).setAxisLineVisible(false);
        chart1.getXYPlot().getRangeAxis(0).setTickMarksVisible(false);
        chart1.getXYPlot().getDomainAxis(0).setTickLabelsVisible(false);
        chart1.getXYPlot().getDomainAxis(0).setTickMarksVisible(false);







        /*if (isClassicManhattan)
        {
        XYDotRenderer dots = new XYDotRenderer();
        dots.setSeriesPaint(0, Model.colors[0]);
        dots.setSeriesPaint(1, Model.colors[1]);
        dots.setSeriesPaint(2, Model.colors[2]);
        dots.setSeriesPaint(3, Model.colors[3]);
        dots.setSeriesPaint(4, Model.colors[4]);
        dots.setSeriesPaint(5, Model.colors[5]);
        dots.setSeriesPaint(6, Model.colors[6]);
        dots.setSeriesPaint(7, Model.colors[7]);
        dots.setSeriesPaint(8, Model.colors[8]);
        dots.setSeriesPaint(9, Model.colors[9]);
        dots.setDotHeight(3);
        dots.setDotWidth(3);
        chart1.getXYPlot().setRenderer(dots);
        }*/

        chart1.setBackgroundPaint(BACKCOLOR);
        return chart1;
    }

    /**
     * Returns the number of populations in the chart.
     * @return
     */
    public int getNumPops()
    {
        if (populations != null)
        {
            return this.numberPopulations;
        }
        return -1;
    }

    /**
     * Returns the trait list of traits that this ManhattanPlot is currently showing
     * @return
     */
    public ArrayList<Trait> getTraitList()
    {
        return manager.getDisplayedTraits();
    }
}
