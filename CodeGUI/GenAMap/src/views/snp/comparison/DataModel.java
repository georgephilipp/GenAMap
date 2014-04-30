package views.snp.comparison;

import datamodel.AssociationSet;
import datamodel.Trait;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import views.snp.ManhattanPlotsManager;

/**
 * The test comparision view works through the datamodel to get all of the data.
 * Thus, the data model parses the associations to determine what the relevant
 * data is and then to run the analyses in order to display the correct
 * display on the screen. 
 * @author rcurtis
 */
public class DataModel extends AbstractTableModel
{
    /**
     * The collection of tests that we are showing on the screen.
     */
    private ArrayList<Test> tests;
    /**
     * The current test that we are showing in the table.
     */
    private int testIdx;
    /**
     * Whether or not the table model is by test or population
     */
    private boolean isByTest;

    /**
     * Constructor - this is where we convert datatypes
     */
    public DataModel(ManhattanPlotsManager mpm)
    {
        tests = new ArrayList<Test>();
        String type;
        ArrayList<Trait> traits = mpm.getDisplayedTraits();
        ArrayList<AssociationSet> assocs = mpm.getAssocs();

        for (AssociationSet ac : assocs)
        {
            ArrayList<Integer> tids = new ArrayList<Integer>();
            ArrayList<Integer> pids = new ArrayList<Integer>();
            ArrayList<String> names = new ArrayList<String>();
            double sigLevel = 0.0;
            if (ac.getIsPvals())
            {
                sigLevel = 1e-5;
            }
            if (assocs.get(0).isPopAssocSet())
            {
                type = "populations";
                for (int i = 0; i < ac.getNumPops(); i++)
                {
                    pids.add(i + 1);
                    names.add("Pop " + (i + 1));
                    tids.add(traits.get(0).getId());
                }
            }
            else
            {
                type = "traits";
                for (Trait t : traits)
                {
                    pids.add(-1);
                    names.add(t.getName());
                    tids.add(t.getId());
                }
            }
            tests.add(new Test(ac, sigLevel, tids, pids, names, type));
        }
    }

    /**
     * Returns the tests association with this data
     */
    public ArrayList<Test> getTests()
    {
        return this.tests;
    }

    /**
     * Set up the table model
     */
    public void setUpTableModel(int i)
    {
        this.testIdx = i;
        this.isByTest = true;
    }

    public int getRowCount()
    {
        if(isByTest)
        return tests.get(testIdx).getMaxSumSigSnps();
        return getRowCount1();
    }

    public int getColumnCount()
    {
        if(isByTest)
        return tests.get(testIdx).getCatNames().size() * 3;
        return getColumnCount1();
    }

    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if(!isByTest)
            return getValueAt1(rowIndex, columnIndex);
        int ix = columnIndex / 3;
        Category c = tests.get(testIdx).getCategory(
                tests.get(testIdx).getCatNames().get(ix));
        SNPAssoc snp = c.getSNP(rowIndex);
        if(snp == null)
            return "";
        switch (columnIndex % 3)
        {
            case 0:
                if (snp.isSignificant(c.getSigLev(), c.getIsPval()))
                {
                    return snp.getName();
                }
            case 1:
                if (snp.isSignificant(c.getSigLev(), c.getIsPval()))
                {
                    return snp.getLocation();
                }
            case 2:
                if (snp.isSignificant(c.getSigLev(), c.getIsPval()))
                {
                    return snp.getSignificanceLevel();
                }
                break;
        }
        return "";
    }

    @Override
    public String getColumnName(int idx)
    {
        if(!isByTest)
            return getColumnName1(idx);
        if (idx % 3 == 1)
        {
            return this.tests.get(this.testIdx).getCatNames().get(idx / 3);
        }
        return "";
    }

    /**
     * Set up the table model
     */
    public void setUpTableModel(String catName)
    {
        this.isByTest = false;
        this.testIdx = tests.get(0).getCatNames().indexOf(catName);
    }

    public int getRowCount1()
    {
        String name = tests.get(0).getCatNames().get(testIdx);
        int max = 0;
        for(Test t : tests)
        {
            int val = t.getMaxSumSigSnps(name);
            if(max < val)
            {
                max = val;
            }
        }
        return max;
    }

    public int getColumnCount1()
    {
        return tests.size() * 3;
    }

    public Object getValueAt1(int rowIndex, int columnIndex)
    {
        int ix = columnIndex / 3;
        Category c = tests.get(ix).getCategory(
                tests.get(0).getCatNames().get(testIdx));
        SNPAssoc snp = c.getSNP(rowIndex);
        if(snp == null)
            return "";
        switch (columnIndex % 3)
        {
            case 0:
                if (snp.isSignificant(c.getSigLev(), c.getIsPval()))
                {
                    return snp.getName();
                }
            case 1:
                if (snp.isSignificant(c.getSigLev(), c.getIsPval()))
                {
                    return snp.getLocation();
                }
            case 2:
                if (snp.isSignificant(c.getSigLev(), c.getIsPval()))
                {
                    return snp.getSignificanceLevel();
                }
                break;
        }
        return "";
    }

    public String getColumnName1(int idx)
    {
        if (idx % 3 == 1)
        {
            return this.tests.get(idx / 3).getName();
        }
        return "";
    }
}

