package views.snp;

import datamodel.Association;
import datamodel.AssociationSet;
import datamodel.Marker;
import datamodel.Project;
import datamodel.Trait;
import java.util.ArrayList;
import java.util.Collection;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * We want to be able to coordinate the visualization of several association sets
 * at the same time in the JUNG view. We do this through the ManhattanPlotManager.
 *
 * Through this manager, we are able to filter on different properties and look
 * for top-ranking SNPs.
 * @author rcurtis
 */
public class ManhattanPlotsManager
{
    /**
     * The collection of associations that we are watching
     */
    private ArrayList<AssociationSet> assocs;
    /**
     * The parameters of the manager
     */
    private class Parameters
    {
        /**
         * Whether or not to display each association
         */
        public ArrayList<Boolean> isDisplayList;
        public boolean isShowingPopulations;
        /**
         * The data that we will be plotting
         */
        public ArrayList<XYSeriesCollection> dataset;

        /**
         * Constructor
         */
        public Parameters(ArrayList<AssociationSet> assocs)
        {
            isDisplayList = new ArrayList<Boolean>();
            for (int i = 0; i < assocs.size(); i++)
            {
                if (i == 0)
                {
                    isDisplayList.add(true);
                }
                else
                {
                    isDisplayList.add(false);
                }
            }
            isShowingPopulations = assocs.get(0).isPopAssocSet();
        }
    }
    /**
     * The parameters for display purposes.
     */
    private Parameters parms;
    /**
     * The traits that we are showing plots for.
     */
    private ArrayList<Trait> selected;
    /**
     * The associations that are loaded for the current chromosome.
     */
    private ArrayList<Collection<Association>> stored_assocs;
    /**
     * The associations that are loaded for the current chromsome. This is
     * by population.
     */
    private ArrayList<ArrayList<Collection<Association>>> stored_assocs_pop;

    /**
     * Creates a new ManhattanPlotManager
     * @param ac
     */
    public ManhattanPlotsManager(AssociationSet ac)
    {
        setManhattanAssociationSets(ac);
    }

    /**
     * Called whenever the association set of the chromosomeview changes
     * This will update the control so that we are always watching the correct
     * association sets.
     * @param ac
     */
    public void setManhattanAssociationSets(AssociationSet ac)
    {
        assocs = new ArrayList<AssociationSet>();
        assocs.add(ac);
        Project p = ac.getProject();
        for (AssociationSet as : p.getAssocs())
        {
            if (as.getMarkerSet() == ac.getMarkerSet() && as.getTraitSet() == ac.getTraitSet() && ac != as && as.getPopulation() == ac.getPopulation())
            {
                assocs.add(as);
            }
        }
        parms = new Parameters(assocs);
    }

    /**
     * Returns an array of strings representing the names of association
     * sets that have not been displayed on the current ManhattanPlot.
     * @return
     */
    public ArrayList<String> getUndisplayedAssocSets()
    {
        ArrayList<String> toRet = new ArrayList<String>();
        for (int i = 0; i < parms.isDisplayList.size(); i++)
        {
            if (!parms.isDisplayList.get(i))
            {
                toRet.add(assocs.get(i).getName());
            }
        }
        return toRet;
    }

    /**
     * Returns an array of strings representing the names of association sets
     * that are currently displayed in the ManhattanPlot
     */
    public ArrayList<String> getDisplayedAssocSets()
    {
        ArrayList<String> toRet = new ArrayList<String>();
        for (int i = 0; i < parms.isDisplayList.size(); i++)
        {
            if (parms.isDisplayList.get(i))
            {
                toRet.add(assocs.get(i).getName());
            }
        }
        return toRet;
    }

    /**
     * Create the dataset that we will use to build the Manhattan plot.
     * Build this from the collection of associations - keeping track of
     * who is visible on the screen.
     * @return
     */
    private XYSeriesCollection createDataset(int leftLocus, int rightLocus, ArrayList<ArrayList<Marker>> markers,
            int numPops, boolean isPvals, boolean isAbs, boolean isLogDisplay, ArrayList<Integer> popsToRemove)
    {
        //if(this.hasTwoSeries())
        {
            isLogDisplay = isPvals;
        }

        XYSeriesCollection ret = new XYSeriesCollection();
        for (int i = 0; i < assocs.size(); i++)
        {
            if (parms.isDisplayList.get(i) && assocs.get(i).getIsPvals() == isPvals)
            {
                isAbs = !assocs.get(i).getName().contains("_cv");
                if (parms.isShowingPopulations)
                {
                    addSeriesForPops(leftLocus, rightLocus, markers, ret, this.stored_assocs_pop.get(i), numPops,
                            isAbs, isLogDisplay, assocs.get(i).getName(), popsToRemove);
                }
                else
                {
                    addSeriesForTraits(leftLocus, rightLocus, markers, ret, stored_assocs.get(i),
                            isAbs, isLogDisplay,
                            this.getDisplayedAssocSets().size() > 1 ? assocs.get(i).getName() : "");
                }
            }
        }
        return ret;
    }

    /**
     * Return an array of all the the traits currently displayed on the screen.
     * @return
     */
    public ArrayList<Trait> getDisplayedTraits()
    {
        return selected;
    }

    /**
     * Called whenever ManhattanPlot dectects that we are working with a population
     * set. This just makes sure that we are only showing one trait.
     */
    public void checkNumberOfTraits()
    {
        if (parms.isShowingPopulations)
        {
            Trait last = null;
            while (selected.size() > 0)
            {
                last = selected.get(0);
                selected.remove(0);
            }
            if (last != null)
            {
                selected.add(last);
            }
        }
    }

    /**
     * Given a list of traits that the user wants to see, we set up our
     * visualization so that those traits are visible on the screen.
     * @param picked
     */
    public void setUpTraitArray(ArrayList<Trait> picked)
    {
        selected = new ArrayList<Trait>();
        for (Trait p : picked)
        {
            selected.add(p);
        }
    }

    /**
     * Takes the value of the association and determines what its value will
     * be in the current ManhattanPlot.
     * @param i
     * @param isAbs
     * @param isLogDisplay
     * @return
     */
    private double determineDisplayValue(Association i, boolean isAbs, boolean isLogDisplay)
    {
        double val = i.getValue();
        if (isAbs)
        {
            val = Math.abs(val);
        }
        if (isLogDisplay)
        {
            if (val <= 1e-50)
            {
                val = 50;
            }
            else
            {
                val = Math.log10(val) * (-1);
            }
        }
        return val;
    }

    /**
     * Returns the title that we want to be showing for the Manhattan Plot
     * @return
     */
    public String getTitle()
    {
        String title = "";
        if (parms.isShowingPopulations)
        {
            title = ((Trait) selected.toArray()[0]).getName() +
                    (this.getDisplayedAssocSets().size() > 1 ? "" : "(" + assocs.get(0).getName() + ")");
        }
        return title;
    }

    /**
     * Sets the chart range for the JFreeChart object. This should be done
     * for the range across all populations, tests, and chromosomes.
     * @param chart1
     */
    public void setOverallChartRange(JFreeChart chart1, boolean isAbs, boolean isLogDisplay)
    {
        Double min_y = null;
        Double max_y = null;

        Double pval_min = null;
        Double pval_max = null;
        if (this.hasTwoSeries())
        {
            isAbs = false;
            isLogDisplay = false;
        }

        for (int i = 0; i < this.assocs.size(); i++)
        {
            isAbs = !assocs.get(i).getName().contains("_cv");
            if (parms.isDisplayList.get(i))
            {
                for (int j = 0; j < selected.size(); j++)
                {
                    ArrayList<Double> maxNmin = assocs.get(i).getMaxNMinVals(selected.get(j).getId());
                    if (assocs.get(i).getIsPvals())
                    {
                        double mn = maxNmin.get(1);
                        double mx = maxNmin.get(0);

                        if (pval_min == null || mn < pval_min)
                        {
                            pval_min = mn;
                        }
                        if (pval_max == null || mx > pval_max)
                        {
                            pval_max = mx;
                        }
                    }
                    else
                    {
                        double mn = maxNmin.get(1);
                        double mx = maxNmin.get(0);
                        if (isAbs)
                        {
                            mn = maxNmin.get(1);
                            mx = Math.max(mx, Math.abs(mn));
                        }

                        if (min_y == null || min_y > mn)
                        {
                            min_y = mn;
                        }
                        if (max_y == null || max_y < mx)
                        {
                            max_y = mx;
                        }
                    }
                }
            }
        }
        if (min_y == null && pval_min == null)
        {
            min_y = 0.0;
        }
        if (max_y == null && pval_max == null)
        {
            max_y = 0.5;
        }
        chart1.getXYPlot().getRangeAxis(0).setAutoRange(false);
        if (min_y != null && pval_min != null)
        {
            chart1.getXYPlot().getRangeAxis(1).setAutoRange(false);
        }
        if (min_y != null)
        {
            if (max_y < 0.0)
            {
                max_y = 0.0;
            }
            if (min_y > 0.0)
            {
                min_y = 0.0;
            }
            if (isAbs)
            {
                min_y = 0.0;
            }
        }

        if (pval_min != null)
        {
            if (pval_min == 0)
            {
                pval_min = 1e-50;
            }
            System.out.println("" + pval_min);
            chart1.getXYPlot().getRangeAxis(0).setRange(0.0, -1 * Math.log10(pval_min) + (-1 * Math.log10(pval_min))/10.0);
        }
        else
        {
            chart1.getXYPlot().getRangeAxis(0).setRange(min_y, max_y + max_y / 20.0);
        }

        if (pval_min != null && min_y != null)
        {
            chart1.getXYPlot().getRangeAxis(1).setRange(min_y, max_y + max_y / 20.0);
        }
    }

    /**
     * Returns the dataset for the given series.
     * @param i
     * @return
     */
    public XYSeriesCollection getDataset(int i)
    {
        if (i < parms.dataset.size())
        {
            return parms.dataset.get(i);
        }
        return null;
    }

    /**
     * Gets the values for all of the associations and builds a dataset
     * which can then be displayed.
     * @param ac
     * @param markers
     */
    public void setupDataSets(ArrayList<Integer> markers, ArrayList<ArrayList<Marker>> markerlist,
            boolean isAbs, boolean isLogDisplay, int numPops, ArrayList<Integer> popsToRemove)
    {
        parms.dataset = new ArrayList<XYSeriesCollection>();
        ArrayList<Integer> traits = new ArrayList<Integer>();
        for (Trait t11 : selected)
        {
            traits.add(t11.getId());
        }
        if (!parms.isShowingPopulations)
        {
            stored_assocs = new ArrayList<Collection<Association>>();
            for (AssociationSet ac : this.assocs)
            {
                stored_assocs.add(ac.findAssociations(markers, traits, -1));
            }
        }
        else
        {
            stored_assocs_pop = new ArrayList<ArrayList<Collection<Association>>>();
            for (AssociationSet ac : this.assocs)
            {
                ArrayList<Collection<Association>> assocs_pop = new ArrayList<Collection<Association>>();
                for (int pp = 0; pp < numPops; pp++)
                {
                    Collection<Association> a = ac.findAssociations(markers, traits, pp + 1);
                    assocs_pop.add(a);
                }
                stored_assocs_pop.add(assocs_pop);
            }
        }
        XYSeriesCollection dataset = createDataset(-1, Integer.MAX_VALUE, markerlist, numPops, true, isAbs, isLogDisplay,
                popsToRemove);
        if (dataset.getSeriesCount() > 0)
        {
            parms.dataset.add(dataset);
        }
        dataset = createDataset(-1, Integer.MAX_VALUE, markerlist, numPops, false, isAbs, isLogDisplay,
                popsToRemove);
        if (dataset.getSeriesCount() > 0)
        {
            parms.dataset.add(dataset);
        }
    }

    /**
     * Creates a series given the locus and the given markers for each trait.
     * These are then added to the XYSeriesCollection that is passed in.
     * @param leftLocus the left locus currently shown
     * @param rightLocus the right locus currently shown
     * @param markers the collection of markers
     * @param ret the XYSeries to add to.
     */
    public void addSeriesForTraits(int leftLocus, int rightLocus, ArrayList<ArrayList<Marker>> markers, XYSeriesCollection ret,
            Collection<Association> assoc, boolean isAbs, boolean isLogDisplay, String assocname)
    {
        XYSeries series;
        for (Trait t : selected)
        {
            series = new XYSeries(t.getName() + "," + assocname);
            series.add(leftLocus, 0.0);
            series.add(rightLocus, 0.0);
            double minLoc = 0.0;
            double maxLoc = 1e10;
            for (int grp = 0; grp < markers.size(); grp++)
            {
                ArrayList<Marker> marker = markers.get(grp);
                for (int _m = 0; _m < marker.size(); _m++)
                {
                    Marker m1 = marker.get(_m);
                    int loc = m1.getLocus();
                    series.addOrUpdate(loc, 0.0);
                    if (loc < leftLocus && loc > minLoc)
                    {
                        minLoc = loc;
                    }
                    else if (loc > rightLocus && loc < maxLoc)
                    {
                        maxLoc = loc;
                    }
                }
            }
            for (Association i : assoc)
            {
                if (i.getMarker().getLocus() >= leftLocus && i.getMarker().getLocus() < rightLocus)
                {
                    if (i.getTraitId() == t.getId())
                    {
                        double val = determineDisplayValue(i, isAbs, isLogDisplay);
                        if (isAbs)
                        {
                            Math.abs(val);
                        }
                        series.update((double) i.getMarker().getLocus(), val);
                    }
                }
                else if (i.getMarker().getLocus() == minLoc)
                {
                    if (i.getTraitId() == t.getId())
                    {
                        double val = determineDisplayValue(i, isAbs, isLogDisplay);
                        series.update((double) leftLocus, val);
                    }
                }
                else if (i.getMarker().getLocus() == maxLoc)
                {
                    if (i.getTraitId() == t.getId())
                    {
                        double val = determineDisplayValue(i, isAbs, isLogDisplay);
                        series.update((double) rightLocus, val);
                    }
                }
            }
            ret.addSeries(series);
        }
    }

    /**
     * This function creates a dataset for the association having population
     */
    public void addSeriesForPops(int leftLocus, int rightLocus, ArrayList<ArrayList<Marker>> markers,
            XYSeriesCollection coll, ArrayList<Collection<Association>> assoc, int numPops, boolean isAbs, boolean isLogDisplay,
            String assocname, ArrayList<Integer> popsToRemove)
    {
        int totalpop = numPops;
        XYSeries series;

        ArrayList<Integer> traits = new ArrayList<Integer>();
        for (Trait t11 : selected)
        {
            traits.add(t11.getId());
            break;
        }
        for (int popno = 1; popno <= totalpop; popno++)
        {
            if (popsToRemove != null && popsToRemove.contains(popno))
            {
                continue;
            }
            double minLoc = 0.0;
            double maxLoc = 1e10;
            Collection<Association> a = assoc.get(popno - 1);
            series = new XYSeries(assocname);
            series.add(leftLocus, 0.0);
            series.add(rightLocus, 0.0);
            for (int grp = 0; grp < markers.size(); grp++)
            {
                ArrayList<Marker> m_g = markers.get(grp);
                for (int _m = 0; _m < m_g.size(); _m++)
                {
                    Marker m1 = m_g.get(_m);
                    int loc = m1.getLocus();
                    series.addOrUpdate(m1.getLocus(), 0.0);
                    if (loc < leftLocus && loc > minLoc)
                    {
                        minLoc = loc;
                    }
                    else if (loc > rightLocus && loc < maxLoc)
                    {
                        maxLoc = loc;
                    }
                }
            }
            for (Association i : a)
            {
                if (i.getMarker().getLocus() >= leftLocus && i.getMarker().getLocus() < rightLocus)
                {
                    double val = determineDisplayValue(i, isAbs, isLogDisplay);
                    if (isAbs)
                    {
                        val = Math.abs(val);
                    }
                    series.update((double) i.getMarker().getLocus(), val);
                }
                else if (i.getMarker().getLocus() == minLoc)
                {
                    double val = determineDisplayValue(i, isAbs, isLogDisplay);
                    series.update((double) leftLocus, val);
                }
                else if (i.getMarker().getLocus() == maxLoc)
                {
                    double val = determineDisplayValue(i, isAbs, isLogDisplay);
                    series.update((double) rightLocus, val);
                }
            }

            coll.addSeries(series);
        }
    }

    /**
     * Add a test to the visualization of the results
     * @param nm
     */
    public void addTest(String nm)
    {
        for (int i = 0; i < assocs.size(); i++)
        {
            if (assocs.get(i).getName().compareToIgnoreCase(nm) == 0)
            {
                parms.isDisplayList.set(i, Boolean.TRUE);
            }
        }
    }

    /**
     * Remove a test from the visualization of the results
     * @param nm
     */
    public void removeTest(String nm)
    {
        for (int i = 0; i < assocs.size(); i++)
        {
            if (assocs.get(i).getName().compareToIgnoreCase(nm) == 0)
            {
                parms.isDisplayList.set(i, Boolean.FALSE);
            }
        }
    }

    /**
     * Determines whether or not we are currently displaying series with two
     * axes or just one axis.
     * @return
     */
    public boolean hasTwoSeries()
    {
        return parms.dataset.size() > 1;
    }

    /**
     * Determines whether or not the current plot is a p-value plot or no.
     * @return
     */
    public boolean getIsPvalPlot()
    {
        return assocs.get(0).getIsPvals();
    }

    /**
     * REturns all associations that are available with these traits and markers
     * @return
     */
    public ArrayList<AssociationSet> getAssocs()
    {
        return this.assocs;
    }
}
