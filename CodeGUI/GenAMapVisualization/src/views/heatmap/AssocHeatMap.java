package views.heatmap;

import control.DataAddRemoveHandler;
import datamodel.AssociationSet;
import datamodel.Edge;
import datamodel.LargeAssocNavigator;
import datamodel.Marker;
import datamodel.TraitSubset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import realdata.DataManager;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import views.AssociationView;

/**
 * Extends HeatMap to draw an association view.  
 * @author rcurtis
 */
public class AssocHeatMap extends HeatMap
{
    /**
     * List of all markers in the dataset
     */
    private ArrayList<Marker> markers;
    /**
     * The association set that we are dispalying in this heatmap
     */
    private AssociationSet assoc;

    /**
     * Constructor
     */
    public AssocHeatMap(JFrame owner, JPanel jp)
    {
        super(owner, jp);
    }

    /**
     * This method is called to ask this class to draw a heat chart representing
     * the associations between the snp and traits on the jpanel provided
     * @param jpanel The jpanel to draw on
     * @param a the association set to draw
     * @param s the subset of traits to account for.
     */
    public void drawAssocOnCanvas(JPanel jpanel, AssociationSet a, TraitSubset s)
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
        boolean isSameNodeSet = (assoc != null && a.getTraitSet().getName().equals(this.assoc.getTraitSet().getName()) && traitsubset == s);
        assoc = a;
        isPvals = a.getIsPvals();
        traitsubset = s;
        ArrayList<Integer> nodes;

        try
        {
            traitnodes = a.getTraitSet().getTraits(s);
            markers = a.getMarkerSet().getMarkers();

            ArrayList<String> cols = new ArrayList<String>();
            cols.add("thresh");
            cols.add("ispval");
            ArrayList<String> where = new ArrayList<String>();
            where.add("id=" + this.assoc.getId());
            HashMap<String, String> res = (HashMap<String,String>)DataManager.runMultiColSelectQuery(cols, "assocset", true, where, null).get(0);
            double thresh = Double.parseDouble(res.get("thresh"));
            boolean ispval = res.get("ispval").equals("1");

            if (!isSameNodeSet)
            {
                edgeStruct = a.getAssocStructure(s, traitnodes,
                        "default", s, ispval, thresh);
                if (edgeStruct == null)
                {
                    jpanel.add(new JLabel("Resolution data is not ready ..."));
                }
            }
            else
            {
                edgeStruct = a.getAssocStructure(s, traitnodes,
                        clustering, s, ispval, thresh);
                if (edgeStruct == null)
                {
                    jpanel.add(new JLabel("Resolution data is not ready ..."));
                }
            }

            nodes = getTraitNodesFromStruct(edgeStruct);
        }
        catch (Exception e)
        {

            System.out.println("Here: " + e);
            return;
        }

        //System.out.println(clustering);
        //System.out.println(isSameNodeSet);
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
            lnn = new LargeAssocNavigator(traitnodes, assoc.getDir(), assoc);
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

    private ArrayList<Integer> getTraitNodesFromStruct(ArrayList<Edge> netStruct)
    {
        int min = 999;
        int max = -999;
        for (Edge ne : netStruct)
        {
            if (min > ne.getT2Idx())
            {
                min = ne.getT2Idx();
            }
            if (max < ne.getT2Idx())
            {
                max = ne.getT2Idx();
            }
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

        String name;
//        if ((name = assoc.getTraitSet().isSubset(s)) != null)
//        {
//        }
        //else
        if (assoc.getTraitSet().isCompleteSet(subset))
        {
            AssociationView.getCurrentRunningInstance().setIsJungView(true);
            AssociationView.getCurrentRunningInstance().refreshView();
            return;
        }
        else
        {
            TraitSubset s = new TraitSubset(assoc.getTraitSet(), subset, assoc.getTraitSet().getNextSubsetName());
            /*NewDataNameGetter ndng = new NewDataNameGetter(jp.getParent(), true,
                    "Choose a name for this subset", assoc.getTraitSet().getSubsetNames());
            ndng.setVisible(true);
            if (!ndng.SUCCESS)
            {
                return;
            }*/
            assoc.getTraitSet().addSubset(s);
            DataAddRemoveHandler.getInstance().refreshDisplay();
            name = s.getName();
        }
        AssociationView.getCurrentRunningInstance().setIsJungView(true);
        AssociationView.getCurrentRunningInstance().selectSubset(name);
    }

    @Override
    protected ArrayList<Edge> getStructureForLargeImage() throws Exception
    {
        ArrayList<String> cols = new ArrayList<String>();
        cols.add("thresh");
        cols.add("ispval");
        ArrayList<String> where = new ArrayList<String>();
        where.add("id=" + this.assoc.getId());
        HashMap<String, String> res = (HashMap<String,String>)DataManager.runMultiColSelectQuery(cols, "assocset", true, where, null).get(0);
        double thresh = Double.parseDouble(res.get("thresh"));
        boolean ispval = res.get("ispval").equals("1");
        ArrayList<Edge> toret = this.assoc.getAssocStructure(traitsubset, traitnodes,
                clustering, traitsubset, ispval, thresh);
        lnn = new LargeAssocNavigator(traitnodes, assoc.getDir(), assoc);
        return toret;
    }

    @Override
    protected void populateZValues()
    {
        xUpperLimit = indices.size() - 1;
        xLowerLimit = 0;
        yUpperLimit = markers.size() - 1;
        yLowerLimit = 0;
        numOfTraitsX = xUpperLimit + 1;
        numOfTraitsY = yUpperLimit + 1;
        //populate zValues
        zValues = new double[yUpperLimit + 1][xUpperLimit + 1];
        for (int i = 0; i < edgeStruct.size(); i++)
        {
            //if(!isPvals)
            zValues[edgeStruct.get(i).getT1Idx()][indices.indexOf(edgeStruct.get(i).getT2Idx())] = Math.abs(edgeStruct.get(i).weight);
            /*else
            {
            double val =  Math.abs(edgeStruct.get(i).weight);
            if(val != 0)
            val = -Math.log10(val);
            zValues[edgeStruct.get(i).getT1Idx()][indices.indexOf(edgeStruct.get(i).getT2Idx())] = val;
            }*/
        }
    }

    @Override
    protected void getYLabels(int starty, Object[] yvals)
    {
        for (int i = starty; i < yvals.length + starty; i++)
        {
            yvals[i - starty] = i + 1;
        }
    }

    @Override
    protected ArrayList<Integer> getCurrentlyDisplayedIdxs()
    {
        ArrayList<Integer> idxs = new ArrayList<Integer>();

        for (int x = 0; x < getXNumOfTraits(); x++)
        {
            int ix2 = xLowerLimit + x;
            if (!idxs.contains(ix2))
            {
                idxs.add(ix2);
            }
        }
        return idxs;
    }

    @Override
    protected void setChartLabels()
    {
        curChart.setTitle("Association");
        curChart.setXAxisLabel("Trait");
        curChart.setYAxisLabel("Marker");
    }

    @Override
    protected void addSpecialtyPopups(JPopupMenu popup)
    {
        //nothing to do.
    }

    @Override
    protected void exportToTemp()
    {
        this.assoc.writeToFile();
    }
}
