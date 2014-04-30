package views.snp.comparison;

import datamodel.Association;
import datamodel.AssociationSet;
import datamodel.Marker;
import datamodel.MarkerSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Associations will belong to some kind of category - a trait or a population.
 * This category will then have associations with different SNPs. These associations
 * are ranked, and can be retreived. 
 * @author rcurtis
 */
public class Category
{
    /**
     * The name of the category - trait name or population name
     */
    private String name;
    /**
     * The list of associations to this category
     */
    private ArrayList<SNPAssoc> associations;
    /**
     * The list of associations sorted by their strenght of associaion
     */
    private ArrayList<SNPAssoc> sorted_assocs;
    /**
     * isPval will tell us what comparison to use when we are determining
     * significance levels of SNPs.
     */
    private boolean isPval;
    /**
     * The significance level of the snps for this category.
     */
    private double sigLev;

    /**
     * Constructor
     */
    public Category(AssociationSet ac, int tid, int popid, boolean isPval, String name, boolean isAbs, double sigLev)
    {
        this.sigLev = sigLev;
        this.name = name;
        this.isPval = isPval;
        ArrayList<Integer> traitid = new ArrayList<Integer>();
        traitid.add(tid);
        MarkerSet markers = ac.getMarkerSet();
        Collection<Association> assocs = ac.findAssociations(markers.getMarkerIds(), traitid, popid);

        associations = new ArrayList<SNPAssoc>();
        sorted_assocs = new ArrayList<SNPAssoc>();
        for (Association a : assocs)
        {
            Marker m = markers.getMarker(a.getMarkerId());
            double value = a.getValue();
            if (isAbs)
            {
                value = Math.abs(value);
            }
            associations.add(new SNPAssoc(m.getName(), m.getChromosome(), m.getLocus(), value));
            sorted_assocs.add(new SNPAssoc(m.getName(), m.getChromosome(), m.getLocus(), value));
        }
        Collections.sort(associations, new SNPAssoc.SNPLocationComparator());
        if (isPval)
        {
            Collections.sort(sorted_assocs, new SNPAssoc.SNPStrongSignficanceComparator());
        }
        else
        {
            Collections.sort(sorted_assocs, new SNPAssoc.SNPWeakSignficanceComparator());
        }
    }

    /**
     * Returns a short description about the significant SNPs in this category. 
     */
    public String getSignificantSnps()
    {
        String toret = "";

        String top10 = "Top Snps:\n";
        int cnt = 0;
        for (int i = 0; i < this.sorted_assocs.size(); i++)
        {
            SNPAssoc snp = sorted_assocs.get(i);
            if (isPval && snp.getSignificanceLevel() < sigLev)
            {
                if (cnt < 3)
                {
                    top10 += "\t" + snp.getName() + " " + snp.getLocation() + ":" + Double.toString(snp.getSignificanceLevel()) + ",\n";
                }
                cnt++;
            }
            else if (!isPval && snp.getSignificanceLevel() > sigLev)
            {
                if (cnt < 3)
                {
                    top10 += "\t" + snp.getName() + " " + snp.getLocation() + ":" + Double.toString(snp.getSignificanceLevel()) + ",\n";
                }
                cnt++;
            }
            else
            {
                break;
            }
        }
        top10 += "\t...\n";

        toret += name + " has " + cnt + " signficant SNPs at significance level: " + Double.toString(sigLev);
        toret += "\n" + top10;

        return toret;
    }

    /**
     * Determines whether the association list has an association for the given snp
     */
    private static SNPAssoc isAssocWithSnp(SNPAssoc snp, ArrayList<SNPAssoc> assocs)
    {
        for (SNPAssoc s : assocs)
        {
            if (snp.getName().equals(s.getName()))
            {
                return s;
            }
        }
        return null;
    }

    /**
     * Returns a summary of the snps that are shared among the different categories
     */
    public static String getSharedSignificantSnps(ArrayList<Category> cats, int bp, String type)
    {
        ArrayList<SNPAssoc> allSNPs = new ArrayList<SNPAssoc>();
        for (Category c : cats)
        {
            for (SNPAssoc s : c.associations)
            {
                if (isAssocWithSnp(s, allSNPs) == null)
                {
                    allSNPs.add(s);
                }
            }
        }
        Collections.sort(allSNPs, new SNPAssoc.SNPLocationComparator());

        ArrayList<Boolean> isSharedList = new ArrayList<Boolean>();
        for (int i = 0; i < allSNPs.size(); i++)
        {
            isSharedList.add(true);
        }

        for (int i = 0; i < allSNPs.size(); i++)
        {
            for (Category c : cats)
            {
                SNPAssoc s = isAssocWithSnp(allSNPs.get(i), c.associations);
                if (s == null ||
                        !s.isSignificant(c.sigLev, c.isPval))
                {
                    isSharedList.set(i, false);
                }
            }
        }
        String toret = "Shared significant snps for all " + type + ": ";
        String shared = "";
        int cnt = 0;
        for (int i = 0; i < allSNPs.size(); i++)
        {
            if (isSharedList.get(i))
            {
                cnt++;
                shared += "\t" + allSNPs.get(i).getName() + " " + allSNPs.get(i).getLocation() + ":";
                for (Category c : cats)
                {
                    SNPAssoc s = isAssocWithSnp(allSNPs.get(i), c.associations);
                    shared += Double.toString(s.getSignificanceLevel()) + ",";
                }
                shared += "\n";
            }
        }
        toret += cnt + "\n";
        toret += shared;

        /*toret += "\nAdditional shared SNPs in LD range: \n";
        cnt = 0;
        for (int i = 0; i < allSNPs.size(); i++)
        {
            if (!isSharedList.get(i))
            {
                boolean ok = true;
                ArrayList<Integer> cissnps = new ArrayList<Integer>();
                SNPAssoc snp = allSNPs.get(i);
                cissnps.add(i);
                for (int j = i - 1; j > -1; j--)
                {
                    if (snp.isSNPinCis(allSNPs.get(j), bp))
                    {
                        cissnps.add(j);
                    }
                    else
                    {
                        break;
                    }
                }
                for (int j = i + 1; j > allSNPs.size(); j++)
                {
                    if (snp.isSNPinCis(allSNPs.get(j), bp))
                    {
                        cissnps.add(j);
                    }
                    else
                    {
                        break;
                    }
                }

                for (Category c : cats)
                {
                    boolean hasSig = false;
                    for (int k : cissnps)
                    {
                        SNPAssoc s = isAssocWithSnp(allSNPs.get(i), c.associations);
                        if (s!= null && s.isSignificant(c.sigLev, c.isPval))
                        {
                            hasSig = true;
                        }
                    }
                    if (!hasSig)
                    {
                        ok = false;
                        break;
                    }
                }
                if (ok)
                {
                    cnt++;
                    toret += "\t" + allSNPs.get(i).getName() + " " + allSNPs.get(i).getLocation() + "\n";
                }
            }
        }
        toret += cnt + " total snps.\n\n";*/

        return toret;
    }

    /**
     * Returns the name of the category.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Sets the significance level of the category. 
     * @param val
     */
    public void setSigLev(double val)
    {
        this.sigLev = val;
    }

    /**
     * Determines how many significant snps belong to this category.
     */
    public int getNumSigSnps()
    {
        int cnt = 0;
        for (int i = 0; i < this.sorted_assocs.size(); i++)
        {
            SNPAssoc snp = sorted_assocs.get(i);
            if (isPval && snp.getSignificanceLevel() < sigLev)
            {
                cnt++;
            }
            else if (!isPval && snp.getSignificanceLevel() > sigLev)
            {
                cnt++;
            }
            else
            {
                break;
            }
        }

        return cnt;
    }

    /**
     * Returns the corresponding SNP
     */
    public SNPAssoc getSNP(int index)
    {
        if(index >= sorted_assocs.size())
            return null;
        return sorted_assocs.get(index);
    }

    /**
     * returns the significance level
     * @return
     */
    public double getSigLev()
    {
        return this.sigLev;
    }

    /**
     * returns the ispval
     */
    public boolean getIsPval()
    {
        return this.isPval;
    }
}
