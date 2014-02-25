package views.heatmap;

import heatchart.HeatChart;
import control.DataAddRemoveHandler;
import datamodel.LargeImageNavigator;
import datamodel.Edge;
import datamodel.Trait;
import datamodel.TraitSubset;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import algorithm.AlgorithmView;
import control.algodialog.ClusteringAlgorithmDialog;
import datamodel.Model;
import java.awt.BasicStroke;

/**
 * It is often favorable to see the results of an association analysis represented
 * as a heat chart where the y axis represents the snps and the x axis represents
 * the traits. In this case, we use this heat map class in order to interface
 * with the GPL HeatChart class. 
 *
 * @author kellychan
 * @author rcurtis
 */
public abstract class HeatMap extends JPanel implements MouseWheelListener,
        MouseMotionListener, MouseListener, ActionListener, KeyListener
{
    /**
     * The image panel has the responsibility of holding the actual image that
     * is shown on the screen
     */
    private ImagePanel heatmap;
    /**
     * The JPanel that shows the heat chart
     */
    protected JPanel jp;
    /**
     * Default chart width - this changes as the association view size changes.
     * We dynamically update the number of pixels per represented value in
     * order to account for this value.
     */
    private int CHARTWIDTH = 700;
    /**
     * Default chart height - this changes as the association view size changes.
     * We dynamically update the number of pixels per represented value in
     * order to account for this value.
     */
    private int CHARTHEIGHT = 700;
    /**
     * The number of traits in the heat chart
     */
    protected int numOfTraitsY;
    /**
     * The upper limit of the x axis
     */
    protected int xUpperLimit;
    /**
     * The lower limit of the x axis
     */
    protected int xLowerLimit;
    /**
     * The upper limit of the y axis
     */
    protected int yUpperLimit;
    /**
     * The lower limit of the y axis
     */
    protected int yLowerLimit;
    /**
     * the matrix of values. This is what will be shown in the heat map
     */
    protected double[][] zValues;
    /**
     * TODO
     */
    private boolean flag = false;
    /**
     * There are two types of zooming - the scroll wheel, and drag zooming, also
     * referred to as selected zooming
     */
    private boolean selectZooming = false;
    /**
     * When the control key is held down, select zoom is disabled
     */
    private boolean enableSelectZoom = false;
    /**
     * When the control key is held down, the user can pan around the image,
     * from the initial point that they started panning.
     */
    private Point panRef;
    /**
     * Similar to the pan ref, the select ref references where the user initially
     * started to select the area to zoom into
     */
    private Point selectRef;
    /**
     * The selection lines are drawn on the screen as the user moves the mouse
     * to select an area to zoom into.
     */
    private Line2D.Double selectionLine1 = new Line2D.Double();
    /**
     * The selection lines are drawn on the screen as the user moves the mouse
     * to select an area to zoom into.
     */
    private Line2D.Double selectionLine2 = new Line2D.Double();
    /**
     * The selection lines are drawn on the screen as the user moves the mouse
     * to select an area to zoom into.
     */
    private Line2D.Double selectionLine3 = new Line2D.Double();
    /**
     * The selection lines are drawn on the screen as the user moves the mouse
     * to select an area to zoom into.
     */
    private Line2D.Double selectionLine4 = new Line2D.Double();
    /**
     * Allow the user to use the wheel to zoom in
     */
    private boolean allowWheelZoom = true;
    /**
     * Allow the user to keep zooming in further (disabled if in too far)
     */
    private boolean allowZoomIn = true;
    /**
     * The top left point where the matrix image is drawn
     */
    protected Point matrixTopLeftPoint;
    /**
     * The bottom right point where the matrix image is drawn
     */
    protected Point matrixBottomRightPoint;
    /**
     * The popup menu that is displayed in order to access more matrix image
     * functions
     */
    private JPopupMenu popup;
    /**
     * the owner of this heat map
     */
    protected JFrame owner;
    /**
     * The currently displayed subset of traits.
     */
    protected TraitSubset traitsubset;
    /**
     * The list of indices used to sort the heat map
     */
    protected LinkedList<Integer> indices;
    /**
     * The currently selected clustering fo the data
     */
    protected String clustering = "default";
    /**
     * The heat chart currently ready to be shown on screen
     */
    protected HeatChart curChart;
    /**
     * The minimum threshold for the values shown in the chart - this can
     * be adjusted by the user
     */
    private double minThreshold;
    /**
     * The maximum threshold for the values shown in the chart - this can
     * be adjusted by the user
     */
    private double maxThreshold;
    /**
     * The color scale variable controls whether the color scale is linear, logrithmic,
     * or exponential
     */
    private double colorScale;
    /**
     * Is this chart an image greater than 200x200? If so, we use resolution data,
     * as controlled by a controller from the data model
     */
    protected boolean isLargeImage;
    /**
     * The controler from the data model that is used when the data is a large
     * dataset greater than 200x200
     */
    protected LargeImageNavigator lnn;
    /**
     * TODO
     */
    protected int strLen;
    /**
     * The trait nodes displayed in the network
     */
    protected ArrayList<Trait> traitnodes;
    /**
     * The edges displayed in the chart
     */
    protected ArrayList<Edge> edgeStruct;
    /**
     * The number of traits on the x axis
     */
    protected int numOfTraitsX;
    /**
     * Are the values in z actually p-values?
     */
    protected boolean isPvals = false;
    /**
     * A list of lines that is populated when the user does a gene module analysis
     */
    protected ArrayList<Line2D> lines = new ArrayList<Line2D>();
    /**
     * Remembers what button the user pressed to trigger the mouse event.
     */
    private int button = -1;

    /**
     * Constructor
     * @param owner
     * @param jp
     */
    public HeatMap(JFrame owner, JPanel jp)
    {
        this.owner = owner;
        this.jp = jp;
        panRef = new Point();
        selectRef = new Point();
        matrixTopLeftPoint = new Point(50 + 70, 40);
        matrixBottomRightPoint = new Point(CHARTWIDTH + 70, CHARTHEIGHT);
        popup = new JPopupMenu();
    }

    /**
     * Called when the heat chart needs to resize in order to fit a new
     * size on the screen.
     * @param jpanel
     */
    public void resize(JPanel jpanel)
    {
        if (heatmap == null)
        {
            return;
        }
        if (!heatmap.isVisible())
        {
            return;
        }
        CHARTWIDTH = jpanel.getWidth() - 70;
        CHARTHEIGHT = jpanel.getHeight();
        if (CHARTWIDTH < 400)
        {
            CHARTWIDTH = 400;
        }
        if (CHARTHEIGHT < 400)
        {
            CHARTHEIGHT = 400;
        }
        if (traitnodes.size() < 100)
        {
            matrixTopLeftPoint = new Point(50 + 70, 40);
        }
        else if (traitnodes.size() > 1000)
        {
            matrixTopLeftPoint = new Point(60 + 70, 40);
        }
        else
        {
            matrixTopLeftPoint = new Point(55 + 70, 40);
        }
        heatmap.image = createNewChart(false);
        heatmap.scaleImage = HeatChartScale.DrawScale(minThreshold, maxThreshold, CHARTHEIGHT - 50, colorScale,
                Color.WHITE, Color.BLACK, 10).getChartImage();
        if (heatmap.image != null)
        {
            matrixBottomRightPoint = new Point(heatmap.image.getWidth(heatmap) - 22 + 70,
                    heatmap.image.getHeight(heatmap) - 52);
        }
        jp.removeAll();
        jp.add(heatmap);
        jp.repaint();
        jp.updateUI();

        performSpecialResizeSteps();
    }

    /**
     * Called when the mouse is dragged across the heat map
     * @param e
     */
    public void mouseDragged(MouseEvent e)
    {

        if (this.button == e.BUTTON1)
        {
            Point p = e.getPoint();

            //user is doing selectZooming
            if (selectZooming)
            {
                //if user attempts to go out of graph area for end point of select zoom
                if (!withinGraph(p))
                {
                    p = normalizePoint(p);
                }

                //draw selection boundary
                selectionLine1 = new Line2D.Double(selectRef, new Point(p.x, selectRef.y));
                selectionLine2 = new Line2D.Double(new Point(p.x, selectRef.y), p);
                selectionLine3 = new Line2D.Double(new Point(selectRef.x, p.y), p);
                selectionLine4 = new Line2D.Double(selectRef, new Point(selectRef.x, p.y));
                jp.repaint();

                return;
            }

            if (withinGraph(p))
            {
                if (!flag)
                {
                    panRef = p;
                    flag = true;
                }

                /******************Panning*********************/
                int xMoved = p.x - panRef.x;
                int yMoved = p.y - panRef.y;
                float xPixelsPerTrait = 600 / getXNumOfTraits();
                float yPixelsPerTrait = 600 / getYNumOfTraits();
                int xTraitsToMove = (int) Math.ceil(xMoved / xPixelsPerTrait);
                int yTraitsToMove = (int) Math.ceil(yMoved / yPixelsPerTrait);

                //if user attempts to pan outside max x
                int actualXTraitsMoved = 0;
                boolean xOutOfBounds = false;
                if ((xUpperLimit - xTraitsToMove) > (numOfTraitsX - 1))
                {
                    actualXTraitsMoved = numOfTraitsX - 1 - xUpperLimit;
                    xUpperLimit = numOfTraitsX - 1;
                    xLowerLimit = xLowerLimit + actualXTraitsMoved;
                    xOutOfBounds = true;
                }
                if (xLowerLimit - xTraitsToMove < 0)
                {
                    actualXTraitsMoved = xLowerLimit - 0;
                    xLowerLimit = 0;
                    xUpperLimit = xUpperLimit - actualXTraitsMoved;
                    xOutOfBounds = true;
                }
                if (!xOutOfBounds || isLargeImage)
                {
                    //otherwise, move normally
                    actualXTraitsMoved = xTraitsToMove;
                    xUpperLimit = xUpperLimit - xTraitsToMove;
                    xLowerLimit = xLowerLimit - xTraitsToMove;
                }

                //if user attempts to pan outside max y
                int actualYTraitsMoved = 0;
                boolean yOutOfBounds = false;

                if ((yUpperLimit + yTraitsToMove) > (numOfTraitsY - 1))
                {
                    actualYTraitsMoved = numOfTraitsY - 1 - yUpperLimit;
                    yUpperLimit = numOfTraitsY - 1;
                    yLowerLimit = yLowerLimit + actualYTraitsMoved;
                    yOutOfBounds = true;
                }
                if (yLowerLimit + yTraitsToMove < 0)
                {
                    actualYTraitsMoved = yLowerLimit - 0;
                    yLowerLimit = 0;
                    yUpperLimit = yUpperLimit - actualYTraitsMoved;
                    yOutOfBounds = true;
                }
                if (!yOutOfBounds || isLargeImage)
                {
                    //otherwise, move normally
                    actualYTraitsMoved = yTraitsToMove;
                    yUpperLimit = yUpperLimit + yTraitsToMove;
                    yLowerLimit = yLowerLimit + yTraitsToMove;
                }

                //update reference point once the chart has changed
                panRef = new Point(panRef.x + Math.round(actualXTraitsMoved * xPixelsPerTrait),
                        panRef.y + Math.round(actualYTraitsMoved * yPixelsPerTrait));

                if (isLargeImage)
                {
                    heatmap.image = createNewChart(actualXTraitsMoved, actualYTraitsMoved);
                    jp.removeAll();
                    jp.add(heatmap);
                    jp.updateUI();
                    return;
                }

                heatmap.image = createNewChart(false);
                jp.removeAll();
                jp.add(heatmap);
                jp.updateUI();
            }
        }
    }

    /**
     * Called when the mouse is moved in the heat map
     * @param e
     */
    public void mouseMoved(MouseEvent e)
    {
        jp.requestFocus();
        Point p = e.getPoint();
        Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
        Cursor arrowCursor = new Cursor(Cursor.DEFAULT_CURSOR);

        jp.setCursor(arrowCursor);
        if (enableSelectZoom)
        {
            jp.setCursor(handCursor);
        }
    }

    /**
     * The heat chart is only displayed in part of the overall jpanel. Was the current
     * click actually within the heat chart?
     * @param p
     * @return
     */
    public boolean withinGraph(Point p)
    {
        if (p.x >= matrixTopLeftPoint.x && p.x <= matrixBottomRightPoint.x && p.y >= matrixTopLeftPoint.y && p.y <= matrixBottomRightPoint.y)
        {
            return true;
        }
        return false;
    }

    /**
     * Returns true if the user is currently all the way zoomed out
     * @return
     */
    public boolean isOriginalSize()
    {
        return (xUpperLimit == (numOfTraitsY - 1) && yUpperLimit == (numOfTraitsY - 1) && xLowerLimit == 0 && yLowerLimit == 0);
    }

    /**
     * Returns the number of traits in  Y
     * @return
     */
    public int getYNumOfTraits()
    {
        return yUpperLimit - yLowerLimit + 1;
    }

    /**
     * Returns the number of traits in X
     * @return
     */
    public int getXNumOfTraits()
    {
        return xUpperLimit - xLowerLimit + 1;
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseClicked(MouseEvent e)
    {
    }

    /**
     * We capture mouse press events and interpret them to update
     * the display of the graph
     * @param e
     */
    public void mousePressed(MouseEvent e)
    {
        this.button = e.getButton();
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            Point p = e.getPoint();
            if ((!enableSelectZoom) && !selectZooming && !flag)
            {
                //if user clicked outside graph...
                if (!withinGraph(p))
                {
                    //System.out.println("User clicked outside graph.");
                    selectRef = normalizePoint(p);
                }
                else
                {
                    //System.out.println("User clicked inside graph.");
                    selectRef = p;
                }
                selectZooming = true;
                flag = true;
            }
        }
    }

    /**
     * Determine what markers and or traits are currently displayed and switch
     * the overall view to the JUNG view.
     */
    protected abstract void switchToJung();

    /**
     * Get all the edges currently displayed in a large image. These edges
     * determine the z values displayed in the heat map
     * @return
     * @throws Exception
     */
    protected abstract ArrayList<Edge> getStructureForLargeImage() throws Exception;

    /**
     * This method is used in order to capture a right-click event. It updates
     * the heat chart depending on the users selection from the right-click menu.
     * @param e
     */
    public void actionPerformed(ActionEvent e)
    {
        String ac = e.getActionCommand();

        if (ac.equals("Add Cluster"))
        {
            ClusteringAlgorithmDialog cad = new ClusteringAlgorithmDialog(owner,
                    true, AlgorithmView.getInstance(), true, traitnodes.get(0).getTraitSet().getProjectName(),
                    traitnodes.get(0).getTraitSet().getName());
            cad.setVisible(true);
        }
        else if (ac.equals("Adjust Color Scale"))
        {
            //lock matrix
            jp.removeMouseWheelListener(this);
            jp.removeMouseMotionListener(this);
            jp.removeMouseListener(this);
            jp.removeKeyListener(this);

            //open adjust color scale dialog
            ColorScaleDialog csd = new ColorScaleDialog(owner, this);
            csd.setVisible(true);

        }
        else if (ac.contains("Delete"))
        {
            String s = ((JMenuItem) e.getSource()).getText().replace("delete ", "");
            this.traitnodes.get(0).getTraitSet().removeCluster(s);
        }
        else if (ac.equals("Switch to JUNG view"))
        {
            switchToJung();
        }
        else if (ac.equals("Export to temp.txt"))
        {
            exportToTemp();
        }
        else if (ac.equals("Save Traits as Subset"))
        {
            ArrayList<Integer> subset = new ArrayList<Integer>();
            if (this.isLargeImage)
            {
                lnn.getSubSet(subset);
            }
            else
            {
                getSubsetFromIdxs(subset);
            }
            if (subset.size() > 0)
            {
                /*NewDataNameGetter ndng = new NewDataNameGetter(jp.getParent(), true,
                "Choose a name for this subset", traitnodes.get(0).getTraitSet().getSubsetNames());
                ndng.setVisible(true);
                if(!ndng.SUCCESS) return;*/
                String name = traitnodes.get(0).getTraitSet().getNextSubsetName();
                traitnodes.get(0).getTraitSet().addSubset(new TraitSubset(traitnodes.get(0).getTraitSet(), subset, name));
                DataAddRemoveHandler.getInstance().refreshDisplay();
            }

        }
        else if (handleSpecialPopupCases(ac))
        {
        }
        else // we are displaying a certain cluster.
        {
            try
            {
                String s = ((JMenuItem) e.getSource()).getText();
                lines = null;
                if (!s.equals("default"))
                {
                    if (this.traitnodes.size() <= 200)
                    {
                        LinkedList<Integer> order = traitnodes.get(0).getTraitSet().getCluster(s, this.traitsubset);
                        fillInValuesAndDrawChart(order, edgeStruct);
                        clustering = s;
                    }
                    else
                    {
                        clustering = s;
                        try
                        {
                            edgeStruct = getStructureForLargeImage();
                        }
                        catch (Exception ex)
                        {
                            JOptionPane.showMessageDialog(owner, "Cluster Generation has failed!\n" + ex.getMessage(),
                                    "Initialization Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }
                else
                {
                    clustering = "default";
                    if (this.traitnodes.size() <= 200)
                    {
                        indices = getDefaultIndexOrdering(traitnodes);
                        fillInValuesAndDrawChart(indices, edgeStruct);
                    }
                    else
                    {
                        try
                        {
                            edgeStruct = getStructureForLargeImage();
                        }
                        catch (Exception ex)
                        {
                            JOptionPane.showMessageDialog(owner, "Cluster Generation has failed!\n" + ex.getMessage(),
                                    "Initialization Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }
                resize(jp);
            }
            catch (Exception exp)
            {
                JOptionPane.showMessageDialog(owner, "Resolution data not ready!\n" + exp.getMessage(),
                                    "Initialization not ready", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Gets the current trait subset given the subset indicies. 
     * @param subset
     */
    protected void getSubsetFromIdxs(ArrayList<Integer> subset)
    {
        if (traitnodes.size() <= 200)
        {
            ArrayList<Integer> idxs = getCurrentlyDisplayedIdxs();
            for (int i : idxs)
            {
                for (Trait t : traitnodes)
                {
                    if (t.getIdx() == i)
                    {
                        subset.add(t.getId());
                    }
                }
            }
        }
    }

    /**
     * Returns a list of what traits/markers are actually displayed on screen
     * @return
     */
    protected abstract ArrayList<Integer> getCurrentlyDisplayedIdxs();

    /**
     * Populates the z-value matrix for display
     */
    protected abstract void populateZValues();

    /**
     * Fills in the heat chart object and creates the image that is displayed
     * on screen.
     * @param indices
     * @param netStruct
     */
    protected void fillInValuesAndDrawChart(LinkedList<Integer> indices, ArrayList<Edge> netStruct)
    {
        minThreshold = 0.0;
        maxThreshold = getMaxWeight();
        colorScale = 1.0;
        this.indices = indices;
        jp.setOpaque(true);
        //define scale values
        populateZValues();
        //create chart and set properties
        HeatChart chart = new HeatChart(zValues);// xOffset, yOffset, xInterval, yInterval);
        chart.setXAxisValuesFrequency(getXFrequency());
        chart.setYAxisValuesFrequency(getYFrequency());
        //chart.setAxisValuesMinFontSize(8);
        chart.setTitle("Trait Network");
        chart.setXAxisLabel("Trait");
        chart.setYAxisLabel("Trait");
        chart.setChartHeight(CHARTWIDTH);
        chart.setChartWidth(CHARTHEIGHT);
        chart.setHighValueColour(Color.BLACK);
        chart.setLowValueColour(Color.WHITE);
        chart.setColourScale(colorScale);
        chart.setBackgroundColour(new java.awt.Color(246, 248, 254));
        int h = 0;
        if (chart.getChartHeight() > 50)
        {
            h = chart.getChartHeight() - 50;
        }

        //System.out.println("h= "+h);
        if (maxThreshold > 50)
        {
            maxThreshold = 50;
        }
        if (heatmap == null)
        {
            heatmap = new ImagePanel();

            heatmap.setBackground(new java.awt.Color(246, 248, 254));
            heatmap.image = chart.getChartImage();

            heatmap.scaleImage = HeatChartScale.DrawScale(minThreshold, maxThreshold, h, colorScale,
                    Color.WHITE, Color.BLACK, 10).getChartImage();
            jp.add(heatmap);
        }
        else
        {

            heatmap.image = createNewChart(false);
            heatmap.scaleImage = HeatChartScale.DrawScale(minThreshold, maxThreshold, h, colorScale,
                    Color.WHITE, Color.BLACK, 10).getChartImage();
            //jp.removeAll();
            jp.add(heatmap);
            jp.updateUI();
        }

    }

    /**
     * Gets the default index ordering of the traits on screen. The traits
     * can be ordered according to a clustering or by default.
     * @param nodes
     * @return
     */
    protected LinkedList<Integer> getDefaultIndexOrdering(ArrayList<Trait> nodes)
    {
        indices = new LinkedList<Integer>();
        int indicesSize = 0;
        //get trait indexes in an ordered linked list
        for (int i = 0; i < nodes.size(); i++)
        {
            indicesSize = indices.size();
            if (!indices.contains(nodes.get(i).getIdx()))
            {
                //if array is empty
                if (indicesSize == 0)
                {
                    indices.add(nodes.get(i).getIdx());
                }
                else
                {
                    boolean addedFlag = false;
                    for (int j = 0; j < indicesSize; j++)
                    {
                        if (indices.get(j) > nodes.get(i).getIdx())
                        {
                            indices.add(j, nodes.get(i).getIdx());
                            addedFlag = true;
                            break;
                        }
                    }
                    if (!addedFlag)
                    {
                        indices.addLast(nodes.get(i).getIdx());
                    }
                }
            }
        }
        return indices;
    }

    /**
     * Create a menu item for the right-click menu.
     */
    protected JMenuItem getMenuItem(String s, ActionListener al)
    {
        JMenuItem menuItem = new JMenuItem(s);
        menuItem.setActionCommand(s);
        menuItem.addActionListener(al);
        return menuItem;
    }

    /**
     * Manipulate p to represent a valid point in the heat chart.
     * @param p
     * @return
     */
    public Point normalizePoint(Point p)
    {

        //if cursor is at left area of graph...
        if (p.x < matrixTopLeftPoint.x)
        {
            if (p.y < matrixTopLeftPoint.y)
            {
                p.x = matrixTopLeftPoint.x;
                p.y = matrixTopLeftPoint.y;
                return p;
            }
            if (p.y > matrixBottomRightPoint.y)
            {
                p.x = matrixTopLeftPoint.x;
                p.y = matrixBottomRightPoint.y;
                return p;
            }
            p.x = matrixTopLeftPoint.x;
            return p;
        }

        //if cursor is on right area of graph...
        if (p.x > matrixBottomRightPoint.x)
        {
            if (p.y < matrixTopLeftPoint.y)
            {
                p.x = matrixBottomRightPoint.x;
                p.y = matrixTopLeftPoint.y;
                return p;
            }
            if (p.y > matrixBottomRightPoint.y)
            {
                p.x = matrixBottomRightPoint.x;
                p.y = matrixBottomRightPoint.y;
                return p;
            }
            p.x = matrixBottomRightPoint.x;
            return p;
        }

        //if cursor is on top area of graph...
        if (p.y < matrixTopLeftPoint.y && p.x > matrixTopLeftPoint.x && p.x < matrixBottomRightPoint.x)
        {
            p.y = matrixTopLeftPoint.y;
            return p;
        }

        //if cursor is on bottom area of graph
        if (p.y > matrixBottomRightPoint.y && p.x > matrixTopLeftPoint.x && p.x < matrixBottomRightPoint.x)
        {
            p.y = matrixBottomRightPoint.y;
            return p;
        }

        return p;
    }

    /**
     * Zooming happens on the mouse release, as does the pop-up menu.
     * @param e
     */
    public void mouseReleased(MouseEvent e)
    {
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            Point p = e.getPoint();
            if (!withinGraph(p) && selectZooming)
            {
                p = normalizePoint(p);
            }
            if (withinGraph(p) && selectZooming)
            {
                //calculate point a (left bottom point)
                Point a = new Point();
                if (selectRef.x <= p.x)
                {
                    a.x = selectRef.x;
                }
                else
                {
                    a.x = p.x;
                }

                if (selectRef.y >= p.y)
                {
                    a.y = selectRef.y;
                }
                else
                {
                    a.y = p.y;
                }

                //calculate point b (right top)
                Point b = new Point();
                if (selectRef.x >= p.x)
                {
                    b.x = selectRef.x;
                }
                else
                {
                    b.x = p.x;
                }
                if (selectRef.y <= p.y)
                {
                    b.y = selectRef.y;
                }
                else
                {
                    b.y = p.y;
                }

                float xPixelsPerTrait = (float) curChart.getCellWidth();//(matrixBottomRightPoint.x-matrixTopLeftPoint.x)/getXNumOfTraits();
                float yPixelsPerTrait = (float) curChart.getCellHeight();//(matrixBottomRightPoint.y-matrixTopLeftPoint.y)/getYNumOfTraits();
                //non-square zooming
                int distX1 = (int) Math.round((a.x - matrixTopLeftPoint.x) / xPixelsPerTrait);
                int distX2 = (int) Math.round((b.x - matrixTopLeftPoint.x) / xPixelsPerTrait);
                int distY1 = (int) Math.round((matrixBottomRightPoint.y - a.y) / yPixelsPerTrait);
                int distY2 = (int) Math.round((matrixBottomRightPoint.y - b.y) / yPixelsPerTrait);
                //if too little pixels selected
                if ((distX2 - distX1) <= 1 || (distY2 - distY1) <= 1)
                {
                    selectZooming = false;
                    flag = false;
                    selectionLine1 = new Line2D.Double();
                    selectionLine2 = new Line2D.Double();
                    selectionLine3 = new Line2D.Double();
                    selectionLine4 = new Line2D.Double();
                    System.out.println("The area you have selected is less than 4 traits");
                    performSpecialOneClickChecks(e.getX(), e.getY());
                    jp.updateUI();
                    return;
                }
                if (true)
                {
                    xLowerLimit = xLowerLimit + distX1;
                    xUpperLimit = xLowerLimit + (distX2 - distX1) - 1;
                    yLowerLimit = yLowerLimit + distY1;
                    yUpperLimit = yLowerLimit + (distY2 - distY1) - 1;
                    //System.out.println(xLowerLimit+","+xUpperLimit);
                    //System.out.println(yLowerLimit+","+yUpperLimit);
                }
                else
                {
                    squareZoom(a, b);
                }

                //check upper limits
                if (yUpperLimit > numOfTraitsY - 1)
                {
                    yUpperLimit = numOfTraitsY - 1;
                }
                if (xUpperLimit > numOfTraitsX - 1)
                {
                    xUpperLimit = numOfTraitsX - 1;
                }

                //check lower limits
                if (yLowerLimit < 0)
                {
                    yLowerLimit = 0;
                }
                if (xLowerLimit < 0)
                {
                    xLowerLimit = 0;
                }

                //System.out.println("X: "+xLowerLimit+","+xUpperLimit);
                //System.out.println("Y: "+yLowerLimit+","+yUpperLimit);
                //System.out.println("===========");
                selectZooming = false;
                allowZoomIn = false;
                selectionLine1 = new Line2D.Double();
                selectionLine2 = new Line2D.Double();
                selectionLine3 = new Line2D.Double();
                selectionLine4 = new Line2D.Double();
                heatmap.image = createNewChart(false);
                jp.removeAll();
                jp.add(heatmap);
                jp.updateUI();
            }
            flag = false;
        }
        else if (e.getButton() == MouseEvent.BUTTON3 || SwingUtilities.isRightMouseButton(e))
        {
            Point p = e.getPoint();
            popup.removeAll();
            JMenu set = new JMenu("Set Clustering");
            JMenu remove = new JMenu("Remove Cluster");
            if (!this.clustering.equals("default"))
            {
                JMenuItem clustrDefault = getMenuItem("default", this);
                set.add(clustrDefault);
            }
            for (String s : traitnodes.get(0).getTraitSet().getClusters())
            {
                if (!this.clustering.equals(s))
                {
                    set.add(getMenuItem(s, this));
                }
                remove.add(getMenuItem("delete " + s, this));
            }

            popup.add(set);
            popup.add(getMenuItem("Add Cluster", this));
            popup.add(getMenuItem("Adjust Color Scale", this));

            addSpecialtyPopups(popup);

            if (!isOriginalSize())
            {
                popup.add(getMenuItem("Save Traits as Subset", this));
            }
            popup.add(getMenuItem("Switch to JUNG view", this));
            popup.add(getMenuItem("Export to temp.txt", this));

            popup.add(remove);
            popup.show(this.jp, p.x, p.y);
        }
    }

    public void mouseExited(MouseEvent e)
    {
    }

    public void keyTyped(KeyEvent e)
    {
    }

    /**
     * When the control button is held down, clicks are interpreted as pans
     * instead of select zooms
     */
    public void keyPressed(KeyEvent e)
    {
        int keyCode = e.getKeyCode();
        if (KeyEvent.getKeyText(keyCode).equals("Ctrl"))
        {
            enableSelectZoom = true;
        }
    }

    /**
     * When the control button is held down, clicks are interpreted as pans
     * instead of select zooms
     */
    public void keyReleased(KeyEvent e)
    {
        int keyCode = e.getKeyCode();
        if (KeyEvent.getKeyText(keyCode).equals("Ctrl"))
        {
            enableSelectZoom = false;
        }
    }

    /**
     * Get the labels for the y axis
     * @param starty
     * @param yvals
     */
    protected abstract void getYLabels(int starty, Object[] yvals);

    /**
     * Get the axis labels and the title for the chart
     */
    protected abstract void setChartLabels();

    /**
     * Allows implementing classes to add methods to the popup menu.
     * @param popup
     */
    protected abstract void addSpecialtyPopups(JPopupMenu popup);

    /**
     * Allow subclasses to decide if they have a special popup case here to 
     * consider
     * @return
     */
    protected boolean handleSpecialPopupCases(String ac)
    {
        return false;
    }

    /**
     * Allows classes to perform other steps, like drawing modules on screen
     * when a resize event happens. 
     */
    protected void performSpecialResizeSteps()
    {
        if (lines == null)
        {
            lines = new ArrayList<Line2D>();
        }
    }

    /**
     * Allows subclasses to override if they want to find out when there are one-clicks
     * that they might want to caputre (like to bring up a popup fo GO information for
     * a module). 
     */
    protected void performSpecialOneClickChecks(int x, int y)
    {
    }

    /**
     * Write out the network to temp.txt file for further analysis
     */
    protected abstract void exportToTemp();
    /**
     * The image panel draws the heat chart on the screen. 
     */
    class ImagePanel extends JPanel
    {
        public Image image;
        public Image scaleImage;
        private BasicStroke stroke = new BasicStroke(1.0f);
        private BasicStroke wideStroke = new BasicStroke(5.0f);

        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            if (image != null)
            {
                //draw image with some x and y displacement
                g.drawImage(scaleImage, -10, 20, this);
                g.drawImage(image, 70, 0, this);
                //g.drawImage(scaleImage, 0, 20, this);
                Graphics2D g2D = (Graphics2D) g;
                g2D.setColor(Color.BLUE);
                g2D.setStroke(stroke);
                g2D.draw(selectionLine1);
                g2D.draw(selectionLine2);
                g2D.draw(selectionLine3);
                g2D.draw(selectionLine4);

                int i = 0;
                int offset = 0;
                for (Line2D line : lines)
                {
                    g2D.setColor(Model.colors[i]);
                    offset++;
                    if (offset == 4)
                    {
                        i++;
                        offset = 0;
                    }
                    g2D.setStroke(wideStroke);
                    g2D.draw(line);
                }
            }
        }
    }

    /**
     * Draws a new chart based on how far the user has panned in the image.
     * @param xMoved
     * @param yMoved
     * @return
     */
    public Image createNewChart(int xMoved, int yMoved)
    {
        lnn.pan(xMoved, yMoved, minThreshold, maxThreshold);
        this.yLowerLimit = 0;
        this.xLowerLimit = 0;
        this.yUpperLimit = lnn.getZVals().length;
        this.xUpperLimit = lnn.getZVals()[0].length;
        return drawNewChart(lnn.getZVals(), lnn.getXAxisLabels(), lnn.getYAxisLabels());
    }

    /**
     * Creates an image based on whether the user is zooming in or out and draws
     * it on the screen. 
     * @param isZoomOut
     * @return
     */
    public Image createNewChart(boolean isZoomOut)
    {
        try
        {
            if (isLargeImage)
            {
                if (isZoomOut)
                {
                    lnn.zoomOut(maxThreshold, minThreshold);
                }
                else
                {
                    lnn.callibrate(this.xLowerLimit, this.xUpperLimit, this.yLowerLimit, this.yUpperLimit,
                            this.maxThreshold, this.minThreshold);
                }
                this.yLowerLimit = 0;
                this.xLowerLimit = 0;
                this.yUpperLimit = lnn.getZVals().length;
                this.xUpperLimit = lnn.getZVals()[0].length;
                //numOfTraits = Math.max(lnn.getZVals().length, lnn.getZVals()[0].length);
                return drawNewChart(lnn.getZVals(), lnn.getXAxisLabels(), lnn.getYAxisLabels());
            }
            else
            {

                //new values for chart
                double[][] newZValues = new double[getYNumOfTraits()][getXNumOfTraits()];
                for (int y = 0; y < getYNumOfTraits(); y++)
                {
                    for (int x = 0; x < getXNumOfTraits(); x++)
                    {
                        newZValues[y][x] = minThreshold;
                    }
                }
                for (int y = 0; y < getYNumOfTraits(); y++)
                {
                    for (int x = 0; x < getXNumOfTraits(); x++)
                    {
                        double value = zValues[numOfTraitsY - 1 - yUpperLimit + y][xLowerLimit + x];
                        if (value >= maxThreshold)
                        {
                            newZValues[y][x] = maxThreshold;
                        }
                        else if (value >= minThreshold)
                        {
                            newZValues[y][x] = value;
                        }
                    }
                }

                Object[] yvals = new Object[newZValues.length];
                Object[] xvals = new Object[newZValues[0].length];
                int startx = xLowerLimit;
                int starty = numOfTraitsY - 1 - yUpperLimit;
                for (int i = startx; i < xvals.length + startx; i++)
                {
                    xvals[i - startx] = i + 1;
                }
                getYLabels(starty, yvals);
                return drawNewChart(newZValues, xvals, yvals);
            }
        }
        catch (NullPointerException ex)
        {
        }
        return null;
    }

    /**
     * Returns an image containing the heat chart that is to be displayed.
     * @param newZValues the z values of the chart
     * @param xvals the x axis labels
     * @param yvals the y axis labels
     * @return
     */
    public Image drawNewChart(double[][] newZValues, Object[] xvals, Object[] yvals)
    {
        curChart = new HeatChart(newZValues);
        curChart.setXAxisValuesFrequency(getXFrequency());
        curChart.setYAxisValuesFrequency(getYFrequency());
        setChartLabels();
        curChart.setChartHeight(CHARTHEIGHT);
        curChart.setChartWidth(CHARTWIDTH);
        curChart.setHighValueColour(Color.BLACK);
        curChart.setLowValueColour(Color.WHITE);
        curChart.setBackgroundColour(new java.awt.Color(246, 248, 254));
        curChart.setColourScale(colorScale);
        curChart.setXValuesHorizontal(true);
        curChart.setXValues(xvals);
        curChart.setYValues(yvals);
        curChart.setDataMax(maxThreshold);
        curChart.setDataMin(minThreshold);
        performSpecialResizeSteps();

        return curChart.getChartImage();
    }

    /**
     * When the mouse wheel moves, we want to update the image by zooming in or
     * out.
     * @param e
     */
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        if (!allowWheelZoom)
        {
            return;
        }

        int notches = e.getWheelRotation();
        if (notches < 0)
        {
            //when mouse wheel scrolls up, zoom in
            if (getXNumOfTraits() > 2 && allowZoomIn == true)
            {
                int diff = getXNumOfTraits() / 4;
                xUpperLimit = xUpperLimit - diff;
                xLowerLimit = xLowerLimit + diff;
                yUpperLimit = yUpperLimit - diff;
                yLowerLimit = yLowerLimit + diff;
                heatmap.image = createNewChart(false);
            }
            else
            {
                if (this.isLargeImage)
                {
                    int diff = getXNumOfTraits() / 4;
                    xUpperLimit = xUpperLimit - diff;
                    xLowerLimit = xLowerLimit + diff;
                    yUpperLimit = yUpperLimit - diff;
                    yLowerLimit = yLowerLimit + diff;
                    heatmap.image = createNewChart(false);
                }
                else
                {
                    System.out.println("Chart cannot be zoomed in further");
                }
            }

        }
        else
        {
            //when mouse wheel scrolls down, zoom out
            int newNumOfTraits;

            //X-side is longer
            if (getXNumOfTraits() >= getYNumOfTraits())
            {
                newNumOfTraits = getXNumOfTraits();
                //force square
                int diff = getXNumOfTraits() - getYNumOfTraits();
                yUpperLimit = yUpperLimit + diff / 2;
                if (yUpperLimit > numOfTraitsY - 1)
                {
                    yUpperLimit = numOfTraitsY - 1;
                }
                yLowerLimit = yUpperLimit + 1 - newNumOfTraits;
                if (yLowerLimit < 0)
                {
                    yUpperLimit = yLowerLimit - 1 + newNumOfTraits;
                }
            }
            else
            { //Y-side is longer
                newNumOfTraits = getYNumOfTraits();
                //force square
                int diff = getYNumOfTraits() - getXNumOfTraits();
                xUpperLimit = xUpperLimit + diff / 2;
                if (xUpperLimit > numOfTraitsY - 1)
                {
                    xUpperLimit = numOfTraitsY - 1;
                }
                xLowerLimit = xUpperLimit + 1 - newNumOfTraits;
                if (xLowerLimit < 0)
                {
                    xUpperLimit = xLowerLimit - 1 + newNumOfTraits;
                }
            }

            if (newNumOfTraits <= numOfTraitsY)
            {
                int diff = newNumOfTraits / 2;
                //if exceeds boundaries of chart
                if (xUpperLimit + diff > (numOfTraitsX - 1))
                {
                    int actualDiff = numOfTraitsX - 1 - xUpperLimit;
                    xUpperLimit = numOfTraitsX - 1;
                    xLowerLimit = xLowerLimit - diff - (diff - actualDiff);
                    if (xLowerLimit < 0)
                    {
                        xLowerLimit = 0;
                    }
                }
                else if (xLowerLimit - diff < 0)
                {
                    int actualDiff = xLowerLimit;
                    xLowerLimit = 0;
                    xUpperLimit = xUpperLimit + diff + (diff - actualDiff);
                    if (xUpperLimit > numOfTraitsX - 1)
                    {
                        xUpperLimit = numOfTraitsX - 1;
                    }
                }
                else
                {
                    xUpperLimit = xUpperLimit + diff;
                    xLowerLimit = xLowerLimit - diff;
                }

                if (yUpperLimit + diff > (this.numOfTraitsY - 1))
                {
                    int actualDiff = numOfTraitsY - 1 - yUpperLimit;
                    yUpperLimit = numOfTraitsY - 1;
                    yLowerLimit = yLowerLimit - diff - (diff - actualDiff);
                    if (yLowerLimit < 0)
                    {
                        yLowerLimit = 0;
                    }
                }
                else if (yLowerLimit - diff < 0)
                {
                    int actualDiff = yLowerLimit;
                    yLowerLimit = 0;
                    yUpperLimit = yUpperLimit + diff + (diff - actualDiff);
                    if (yUpperLimit > numOfTraitsY - 1)
                    {
                        yUpperLimit = numOfTraitsY - 1;
                    }
                }
                else
                {
                    yUpperLimit = yUpperLimit + diff;
                    yLowerLimit = yLowerLimit - diff;
                }
                allowZoomIn = true;
                if (this.isLargeImage)
                {
                    heatmap.image = createNewChart(true);
                }
                else
                {
                    heatmap.image = createNewChart(false);
                }
            }
            else
            {
                if (this.isLargeImage)
                {
                    heatmap.image = createNewChart(true);
                }
            }
        }
        jp.removeAll();
        jp.add(heatmap);
        jp.updateUI();
    }

    /**
     * Get the number of traits that each pixel in x represents
     * @return
     */
    public int getXFrequency()
    {
        if (getXNumOfTraits() < 5)
        {
            return 1;
        }
        if (getXNumOfTraits() < 10)
        {
            return 2;
        }
        return getXNumOfTraits() / 5;
    }

    /**
     * Get the number of traits that each pixel in y represetns.
     * @return
     */
    public int getYFrequency()
    {
        if (getYNumOfTraits() < 5)
        {
            return 1;
        }
        if (getYNumOfTraits() < 10)
        {
            return 2;
        }
        return getYNumOfTraits() / 5;
    }

    /**
     * Return the width in X
     * @return
     */
    public int getCellWidth()
    {
        return Math.round(600 / getXNumOfTraits());
    }

    /**
     * Return width in Y
     * @return
     */
    public int getCellHeight()
    {
        return Math.round(600 / getYNumOfTraits());
    }

    /**
     * not used
     * @param a
     * @param b
     */
    public void squareZoom(Point a, Point b)
    {
        float xPixelsPerTrait = (matrixBottomRightPoint.x - matrixTopLeftPoint.x) / getXNumOfTraits();
        float yPixelsPerTrait = (matrixBottomRightPoint.y - matrixTopLeftPoint.y) / getYNumOfTraits();

        //compare sides
        if ((b.x - a.x) >= (a.y - b.y))
        {
            //System.out.println("Case 1");
            int distX1 = (int) Math.round((a.x - matrixTopLeftPoint.x) / xPixelsPerTrait);
            xLowerLimit = xLowerLimit + distX1;
            int distX2 = (int) Math.round((b.x - matrixTopLeftPoint.x) / xPixelsPerTrait);
            int distX = distX2 - distX1;
            //xUpperLimit = xLowerLimit + distX;
            if ((xLowerLimit + distX) > (numOfTraitsY - 1))
            {
                xUpperLimit = numOfTraitsY - 1;
                xLowerLimit = xUpperLimit - distX;
            }
            else
            {
                xUpperLimit = xLowerLimit + distX;
            }

            int distY1 = (int) Math.round((matrixBottomRightPoint.y - a.y) / yPixelsPerTrait);
            int distY2 = (int) Math.round((matrixBottomRightPoint.y - b.y) / yPixelsPerTrait);
            int diff = (int) Math.round((distX - (distY2 - distY1)) / 2);
            int actualDistY1 = distY1 - diff;
            if (yLowerLimit + actualDistY1 < 0)
            {
                yLowerLimit = 0;
            }
            else
            {
                yLowerLimit = yLowerLimit + actualDistY1;
            }
            yUpperLimit = yLowerLimit + distX;

            if (yUpperLimit > numOfTraitsY - 1)
            {
                yUpperLimit = numOfTraitsY - 1;
                yLowerLimit = yUpperLimit - distX;
            }

        }
        else
        {
            //System.out.println("Case 2");
            int distY1 = (int) Math.round((matrixBottomRightPoint.y - a.y) / yPixelsPerTrait);
            yLowerLimit = yLowerLimit + distY1;
            int distY2 = (int) Math.round((matrixBottomRightPoint.y - b.y) / yPixelsPerTrait);
            int distY = distY2 - distY1;
            if ((yLowerLimit + distY) > (numOfTraitsY - 1))
            {
                yUpperLimit = numOfTraitsY - 1;
                yLowerLimit = yUpperLimit - distY;
            }
            else
            {
                yUpperLimit = yLowerLimit + distY;
            }

            int distX1 = (int) Math.round((a.x - matrixTopLeftPoint.x) / xPixelsPerTrait);
            int distX2 = (int) Math.round((b.x - matrixTopLeftPoint.x) / xPixelsPerTrait);
            int diff = (int) Math.round((distY - (distX2 - distX1)) / 2);
            int actualDistX1 = distX1 - diff;
            if (xLowerLimit + actualDistX1 < 0)
            {
                xLowerLimit = 0;
            }
            else
            {
                xLowerLimit = xLowerLimit + actualDistX1;
            }
            xUpperLimit = xLowerLimit + distY;

            if (xUpperLimit > numOfTraitsY - 1)
            {
                xUpperLimit = numOfTraitsY - 1;
                xLowerLimit = xUpperLimit - distY;
            }
        }
    }

    /**
     * Returns the maximum value in matrix
     * @return
     */
    public double getMaxWeight()
    {
        double maxWeight = 0;
        for (int i = 0; i < edgeStruct.size(); i++)
        {
            double weight = Math.abs(edgeStruct.get(i).getWeight());
            /*if(this.isPvals && weight != 0)
            {
            weight = -Math.log10(weight);
            }*/
            if (weight > maxWeight)
            {
                maxWeight = weight;
            }
        }
        return maxWeight;
    }

    /**
     * Returns the current maximum threshold for visualizing the data
     * @return
     */
    public double getMaxThreshold()
    {
        return maxThreshold;
    }

    /**
     * Returns the current minimum threshold for visualizing the data
     * @return
     */
    public double getMinThreshold()
    {
        return minThreshold;
    }

    /**
     * This method is called after the user closes the color adjustment dialog.
     * It adjusts the values that determine the color of each pixel in the matrix.
     * @param min
     * @param max
     * @param linearScale
     * @param expScale
     * @param logScale
     */
    public void applyAdjustColorScale(double min, double max, boolean linearScale, boolean expScale, boolean logScale)
    {
        //unlock matrix
        jp.addMouseWheelListener(this);
        jp.addMouseMotionListener(this);
        jp.addMouseListener(this);
        jp.addKeyListener(this);

        //update threshold values
        minThreshold = min;
        maxThreshold = max;

        //update color scale
        if (linearScale)
        {
            colorScale = HeatChart.SCALE_LINEAR;
        }
        else if (expScale)
        {
            colorScale = HeatChart.SCALE_EXPONENTIAL;
        }
        else if (logScale)
        {
            colorScale = HeatChart.SCALE_LOGARITHMIC;
        }

        //redraw chart
        heatmap.image = createNewChart(false);
        heatmap.scaleImage = HeatChartScale.DrawScale(minThreshold, maxThreshold, jp.getHeight() - 50, colorScale,
                Color.WHITE, Color.BLACK, 10).getChartImage();

        jp.removeAll();
        jp.add(heatmap);
        jp.updateUI();
    }

    /**
     * Resume looking for mouse controls. 
     */
    public void cancelAdjustColorScale()
    {
        //unlock matrix
        jp.addMouseWheelListener(this);
        jp.addMouseMotionListener(this);
        jp.addMouseListener(this);
        jp.addKeyListener(this);
    }

    /**
     * Returns whether the color scale is logrithmic, linear, or exponential
     * @return
     */
    public double getColorScale()
    {
        return colorScale;
    }
}
