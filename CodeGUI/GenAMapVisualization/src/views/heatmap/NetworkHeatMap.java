package views.heatmap;

import BiNGO.GoItems;
import algorithm.AlgorithmView;
import control.DataAddRemoveHandler;
import control.algodialog.ModuleAlgorithmDialog;
import datamodel.LargeNetworkNavigator;
import datamodel.Network;
import datamodel.Edge;
import datamodel.Model;
import datamodel.TraitSubset;
import realdata.DataManager;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import views.AssociationView;
import views.GoResultsViewer;

/**
 * My idea is that HeatMap is the super class that has the common code between
 * the AssociationHeatMap and the NetworkHeatMap.
 *
 * I can switch between the two without a lot of reproducing code.  
 *
 * Thus, NetworkHeatMap show a heat map of network correlation data. 
 *
 * @author RCurtis
 */
public class NetworkHeatMap extends HeatMap
{
    /**
     * A pointer tot he network for this NetworkHeapMap
     */
    private Network net;
    /**
     * the module database ids that we can use to draw modules boxes on screen.
     */
    private ArrayList<ArrayList<Integer>> moduleIdxList = null;
    /**
     * Keeps track of where all the boxes are so we don't have to do translation
     * on a single-clieck. 
     */
    private ArrayList<ArrayList<Point>> drawnBoxes = new ArrayList<ArrayList<Point>>();
    /**
     * To be kept up to date with drawnBoxes - this way we know what module index is stored
     * in the box that was clicked in! 
     */
    private ArrayList<Integer> drawnIndex = new ArrayList<Integer>();
    /**
     * Gets the array list used to select the modules in the current display. 
     */
    private ArrayList<String> modwhere;

    /**
     * constructor
     * @param owner
     * @param jp
     */
    public NetworkHeatMap(JFrame owner, JPanel jp)
    {
        super(owner, jp);
    }

    /**
     * Given the jpanel, this method calls the appropriate methods in order
     * to draw the matrix of heat map values on the chart.
     * @param jpanel the jpanel to draw on
     * @param n the network that we will display
     * @param s the traitsubset
     */
    public void drawNetworkOnCanvas(JPanel jpanel, Network n, TraitSubset s)
    {

        if (jp.getMouseListeners().length > 0)
        {
            jp.removeMouseListener(jp.getMouseListeners()[0]);
            jp.removeMouseWheelListener(jp.getMouseWheelListeners()[0]);
            jp.removeMouseMotionListener(jp.getMouseMotionListeners()[0]);
            jp.removeKeyListener(jp.getKeyListeners()[0]);
        }
        jp.addMouseWheelListener(this);
        jp.addMouseMotionListener(this);
        jp.addMouseListener(this);
        jp.setFocusable(true);
        jp.addKeyListener(this);
        boolean isSameNodeSet = (net != null && n.getTraitName().equals(this.net.getTraitName()) && traitsubset == s);
        if (!isSameNodeSet)
        {
            lines.clear();
            moduleIdxList = null;
            drawnBoxes = new ArrayList<ArrayList<Point>>();
            drawnIndex = new ArrayList<Integer>();
            clustering = "default";
        }
        net = n;
        traitsubset = s;
        ArrayList<Integer> nodes;
        try
        {
            traitnodes = n.getNetworkNodes(s);
            edgeStruct = n.getNetworkStructure(s, traitnodes, clustering);
            if (edgeStruct == null)
            {
                jpanel.add(new JLabel("Resolution data not ready yet ..."));
                return;
            }
            nodes = getNodesFromStruct(edgeStruct);
        }
        catch (Exception e)
        {
            System.out.println(e);
            return;
        }

        if (traitnodes.size() == 0)
        {
            System.out.println("No traits found.");
            return;
        }
        if (traitnodes.size() <= 200)
        {
            isLargeImage = false;
            if (!isSameNodeSet)
            {
                indices = getDefaultIndexOrdering(traitnodes);
                clustering = "default";
            }
        }
        else
        {
            this.isLargeImage = true;
            lnn = new LargeNetworkNavigator(traitnodes, net.getDir(), net);
            if (!isSameNodeSet)
            {
                clustering = "default";
            }
            indices = new LinkedList<Integer>();
            for (int i : nodes)
            {
                indices.add(i);
            }
        }

        this.strLen = 2;
        if (this.traitnodes.size() > 100)
        {
            this.strLen = 3;
        }
        else if (this.traitnodes.size() > 1000)
        {
            this.strLen = 4;
        }

        fillInValuesAndDrawChart(indices, edgeStruct);
        resize(jp);
    }

    /**
     * In the case where the user has run an analysis to find gene modules,
     * this method will outline the 20 modules in color on the screen. 
     * @param idxs
     */
    private void drawModulesOnScreen(ArrayList<ArrayList<Integer>> idxs)
    {
        lines.clear();
        this.drawnBoxes.clear();
        this.drawnIndex.clear();
        int i = -1;
        for (ArrayList<Integer> ai : idxs)
        {
            i++;
            System.out.println(ai.get(0) + " " + ai.get(1));
            int min = ai.get(0);
            int max = ai.get(1);
            //Point p2 = new Point((int) (min / xPixelsPerTrait), (int) (curChart.getCellHeight() * zValues[0].length - min / yPixelsPerTrait));
            //Point p1 = new Point((int) (max / xPixelsPerTrait), (int) (curChart.getCellHeight() * zValues[0].length - max / yPixelsPerTrait));
            if(max >= min)
            {
                ArrayList<Point> temp = createOnScreenModPoints(min, max);
                if (temp == null)
                {
                    lines.add(new Line2D.Double());
                    lines.add(new Line2D.Double());
                    lines.add(new Line2D.Double());
                    lines.add(new Line2D.Double());
                    continue;
                }
                this.drawnBoxes.add(temp);
                this.drawnIndex.add(i);
                Point p1 = temp.get(0);
                Point p2 = temp.get(1);
                lines.add(new Line2D.Double(new Point(p2.x+0, p2.y), new Point(p1.x+0, p2.y)));
                lines.add(new Line2D.Double(new Point(p1.x+0, p2.y), new Point(p1.x+0, p1.y)));
                lines.add(new Line2D.Double(new Point(p2.x+0, p1.y), new Point(p1.x+0, p1.y)));
                lines.add(new Line2D.Double(new Point(p2.x+0, p2.y), new Point(p2.x+0, p1.y)));
            }
        }
        jp.repaint();
    }

    private ArrayList<Integer> getNodesFromStruct(ArrayList<Edge> netStruct)
    {
        int min = 999;
        int max = -999;
        for (Edge ne : netStruct)
        {
            if (min > ne.getT1Idx())
            {
                min = ne.getT1Idx();
            }
            if (max < ne.getT2Idx())
            {
                max = ne.getT2Idx();
            }
        }

        if(netStruct.size() == 0 || min >= max)
        {
            min = 0;
            max = 199;
        }
        
        ArrayList<Integer> ali = new ArrayList<Integer>();
        for (int i = min; i <= max; i++)
        {
            ali.add(i);
        }

        return ali;
    }

    @Override
    protected void switchToJung()
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

        if (subset.size() > 200)
        {
            JOptionPane.showMessageDialog(owner, "JUNG view cannot be displayed with more than 200 traits",
                    "Initialization Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = net.getTraitSet().getNextSubsetName();
//        if ((name = net.getTraitSet().isSubset(s)) != null)
//        {
//        }
        //else
        if (net.getTraitSet().isCompleteSet(subset))
        {
            AssociationView.getCurrentRunningInstance().setIsJungView(true);
            AssociationView.getCurrentRunningInstance().refreshView();
            return;
        }
        else if (subset.size() > 0)
        {
            TraitSubset s = new TraitSubset(net.getTraitSet(), subset, name);
            //name = s.getNextSubset();
                /*NewDataNameGetter ndng = new NewDataNameGetter(jp.getParent(), true,
            "Choose a name for this subset", net.getTraitSet().getSubsetNames());
            ndng.setVisible(true);
            if(!ndng.SUCCESS) return;*/
            net.getTraitSet().addSubset(s);
            DataAddRemoveHandler.getInstance().refreshDisplay();
            //name = ndng.newName;
        }
        AssociationView.getCurrentRunningInstance().setIsJungView(true);
        AssociationView.getCurrentRunningInstance().selectSubset(name);
    }

    @Override
    protected ArrayList<Edge> getStructureForLargeImage() throws Exception
    {
        ArrayList<Edge> toret = this.net.getNetworkStructure(traitsubset, traitnodes, clustering);
        lnn = new LargeNetworkNavigator(traitnodes, net.getDir(), net);
        return toret;
    }

    @Override
    protected void populateZValues()
    {
        int minIdx = 0;
        int maxIdx = indices.size() - 1;
        yUpperLimit = maxIdx;
        yLowerLimit = minIdx; //may be affected if assumption = 0
        xUpperLimit = maxIdx;
        xLowerLimit = minIdx; //may be affected if assumption = 0
        numOfTraitsY = maxIdx - minIdx + 1;
        numOfTraitsX = maxIdx - minIdx + 1;
        //populate zValues
        zValues = new double[numOfTraitsY][numOfTraitsY];
        for (int i = 0; i < edgeStruct.size(); i++)
        {
            zValues[numOfTraitsY - 1 - indices.indexOf(edgeStruct.get(i).getT1Idx())][indices.indexOf(edgeStruct.get(i).getT2Idx())] = Math.abs(edgeStruct.get(i).weight);
            zValues[numOfTraitsY - 1 - indices.indexOf(edgeStruct.get(i).getT2Idx())][indices.indexOf(edgeStruct.get(i).getT1Idx())] = Math.abs(edgeStruct.get(i).weight);
        }
    }

    @Override
    protected void getYLabels(int starty, Object[] yvals)
    {
        for (int i = starty; i < yvals.length + starty; i++)
        {
            yvals[i - starty] = numOfTraitsY - i;
        }
    }

    @Override
    protected ArrayList<Integer> getCurrentlyDisplayedIdxs()
    {
        ArrayList<Integer> idxes = new ArrayList<Integer>();
        for (int y = 0; y < getYNumOfTraits(); y++)
        {
            int ix1 = yUpperLimit - y;
            if (!idxes.contains(ix1))
            {
                idxes.add(ix1);
            }
        }
        for (int x = 0; x < getXNumOfTraits(); x++)
        {
            int ix2 = xLowerLimit + x;
            if (!idxes.contains(ix2))
            {
                idxes.add(ix2);
            }
        }
        return idxes;
    }

    @Override
    protected void setChartLabels()
    {
        curChart.setTitle("Trait Network");
        curChart.setXAxisLabel("Trait");
        curChart.setYAxisLabel("Trait");
    }

    @Override
    protected void addSpecialtyPopups(JPopupMenu popup)
    {
        JMenu modanal = new JMenu("Module Analysis");
        if (!this.clustering.equals("default") && this.traitsubset == null)
        {
            int cid = this.net.getTraitSet().getClusterId(clustering);
            for (String s : this.net.getModuleAnalyses(cid))
            {
                modanal.add(getMenuItem(s, this));
            }
        }
        modanal.add(getMenuItem("<NEW>", this));
        popup.add(modanal);
    }

    @Override
    protected boolean handleSpecialPopupCases(String text)
    {
        if (text.equals("<NEW>")) //
        {
            ModuleAlgorithmDialog mad;
            try
            {
                mad = new ModuleAlgorithmDialog(owner, true, AlgorithmView.getInstance(), this.net.getTraitSet().getProjectName(), this.net.getTraitSet().getName(), this.net.getName());
            }
            catch (IOException ex)
            {
                return false;
            }
            mad.setVisible(true);
            return true;
        }
        else if (text.contains(",")) // we are going to be showing a module
        {
            int netid = this.net.getId();
            String[] res = text.split(",");
            String assoc = res[0];
            String go = res[1];
            int associd = Model.getInstance().getProject(this.net.getProjectName()).getAssociation(assoc).getId();
            int clusterid = this.net.getTraitSet().getClusterId(clustering);
            modwhere = new ArrayList<String>();
            modwhere.add("netid=" + netid);
            modwhere.add("assocsetid=" + associd);
            modwhere.add("goanno=\'" + go + "\'");
            modwhere.add("clusterid=" + clusterid);
            ArrayList<String> mods = DataManager.runSelectQuery("traitlistid",
                    "netmodule", true, modwhere, "id");

            if(mods.size() > 20)
            {
                ArrayList<String> newmods = new ArrayList<String>();
                for(int i = 0; i < 20; i ++)
                {
                    newmods.add(mods.get(i));
                }
                mods = newmods;
            }

            moduleIdxList = new ArrayList<ArrayList<Integer>>();
            for (String mod : mods)
            {
                moduleIdxList.add(this.net.getMaxMinIndexFromTraitList(Integer.parseInt(mod)));
            }
            drawModulesOnScreen(moduleIdxList);

            return true;
        }
        return false;
    }

    /**
     * This method is used to help us find out if a user click was within a gene
     * module, triggering the display of enrichment information
     */
    private ArrayList<Point> createOnScreenModPoints(int min, int max)
    {
        Point p1 = new Point();
        Point p2 = new Point();
        int dist = max - min + 1;

        Object[] xaxis = lnn.getXAxisLabels();
        Object[] yaxis = lnn.getYAxisLabels();

        int displayMinX = (Integer) xaxis[0];
        int displayMaxX = (Integer) xaxis[xaxis.length - 1];
        int displayMaxY = (Integer) yaxis[0];
        int displayMinY = (Integer) yaxis[yaxis.length - 1];

        if ((max < displayMinX && max < displayMinY) ||
                (min > displayMaxX && min > displayMaxY))
        {
            return null;
        }

        double relMinX = (float) (min + 1 - displayMinX) / (float) (displayMaxX - displayMinX);
        double relMinY = (float) (min + 1 - displayMinY) / (float) (displayMaxY - displayMinY);
        double relMaxX = (float) (max + 1 - displayMinX) / (float) (displayMaxX - displayMinX);
        double relMaxY = (float) (max + 1 - displayMinY) / (float) (displayMaxY - displayMinY);
        if (relMinX < 0)
        {
            relMinX = 0;
        }
        if (relMinY < 0)
        {
            relMinY = 0;
        }
        if (relMaxX > displayMaxX)
        {
            relMaxX = displayMaxX;
        }
        if (relMaxY > displayMaxY)
        {
            relMaxY = displayMaxY;
        }

        p1.x = (int) ((matrixBottomRightPoint.x - matrixTopLeftPoint.x) * relMinX + matrixTopLeftPoint.x);
        p1.y = (int) ((matrixBottomRightPoint.y) - (matrixBottomRightPoint.y - matrixTopLeftPoint.y) * relMinY);
        p2.x = (int) ((matrixBottomRightPoint.x - matrixTopLeftPoint.x) * relMaxX + matrixTopLeftPoint.x);
        p2.y = (int) ((matrixBottomRightPoint.y) - (matrixBottomRightPoint.y - matrixTopLeftPoint.y) * relMaxY);

        ArrayList<Point> ans = new ArrayList<Point>();
        ans.add(p1);
        ans.add(p2);
        return ans;
    }

    @Override
    protected void performSpecialOneClickChecks(int x, int y)
    {
        if (drawnBoxes == null)
        {
            return;
        }
        for (int i = 0; i < this.drawnBoxes.size(); i++)
        {
            ArrayList<Point> box = drawnBoxes.get(i);
            if (x < box.get(1).x + 10 && x > box.get(0).x - 10)
            {
                if (y < box.get(0).y + 10 && y > box.get(1).y - 10)
                {
                    System.out.println(drawnIndex.get(i));
                    ArrayList<GoItems> list = net.getEnrichmentItems(drawnIndex.get(i), modwhere);
                    GoResultsViewer.getInstance().setVisible(true,
                            "Network module " + drawnIndex.get(i), list, null, null, null, 3);
                }
            }
        }
    }

    @Override
    protected void performSpecialResizeSteps()
    {
        if(lines == null)
        {
            lines = new ArrayList<Line2D>();
            moduleIdxList = null;
        }
        if (moduleIdxList != null)
        {
            drawModulesOnScreen(moduleIdxList);
        }
    }

    @Override
    protected void exportToTemp()
    {
        this.net.writeToFile();
    }
}
