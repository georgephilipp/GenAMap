package views.snp.comparison;

import datamodel.AssociationSet;
import java.util.ArrayList;

/**
 * When we are performing our comparisons, tests can or will not be included.
 * These tests should be able to compare to each other and to other tests.
 *
 * @author rcurtis
 */
public class Test
{
    /**
     * The name of the test!
     */
    private String name;
    /**
     * All of the categories that we are currently considering at this time. 
     */
    private ArrayList<Category> categories;
    /**
     * The significance level of this test right now
     */
    private double significanceLevel;
    /**
     * The type - pop or trait
     */
    private String type;

    /**
     * Constructor
     */
    public Test(AssociationSet assocs, double sigLevel, ArrayList<Integer> tids, ArrayList<Integer> popids,
            ArrayList<String> names, String type)
    {
        this.type = type;
        this.name = assocs.getName();
        this.significanceLevel = sigLevel;

        this.categories = new ArrayList<Category>();
        for (int i = 0; i < tids.size(); i++)
        {
            categories.add(new Category(assocs,
                    tids.get(i), popids.get(i), assocs.getIsPvals(), names.get(i), !name.contains("_cv"), sigLevel));
        }
    }

    /**
     * Gets the significance level of the given test
     */
    public double getSigLevel()
    {
        return significanceLevel;
    }

    /**
     * Returns the name of the test
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns a summary description of the test for each category. 
     * @return
     */
    public String getTestSummary(int bp)
    {
        String toret = "";

        toret += "---------------------------------------------------------------------------------\n";
        toret += "---------------------------" + name + "---------------------------\n";
        toret += "---------------------------------------------------------------------------------";
        toret += "\n";
        toret += "*** SIGNIFICANT SNPS ***\n";

        for (Category c : this.categories)
        {
            toret += c.getSignificantSnps();
        }

        toret += categories.get(0).getSharedSignificantSnps(categories, bp, type);


        return toret;
    }

    /**
     * Returns the names of all the categories for this test
     * @return
     */
    public ArrayList<String> getCatNames()
    {
        ArrayList<String> toRet = new ArrayList<String>();
        for (Category c : this.categories)
        {
            toRet.add(c.getName());
        }
        return toRet;
    }

    /**
     * Returns the category with the specified name
     */
    public Category getCategory(String name)
    {
        for (Category c : this.categories)
        {
            if (c.getName().equals(name))
            {
                return c;
            }
        }
        return null;
    }

    /**
     * Sets the significance level of the test
     * @param parseDouble
     */
    public void setSigLevel(double val)
    {
        this.significanceLevel = val;
        for (Category c : this.categories)
        {
            c.setSigLev(val);
        }
    }

    /**
     * Determine which category has the most significant snps and how many it has
     */
    public int getMaxSumSigSnps()
    {
        int max = 0;
        for (Category c : this.categories)
        {
            int candidate = c.getNumSigSnps();
            if (candidate > max)
            {
                max = candidate;
            }
        }
        return max;
    }

    /**
     * Get the number of significant snps for the given category
     */
    public int getMaxSumSigSnps(String name)
    {
        int max = 0;
        for (Category c : this.categories)
        {
            if (c.getName().equals(name))
            {
                return c.getNumSigSnps();
            }
        }
        return max;
    }
}
