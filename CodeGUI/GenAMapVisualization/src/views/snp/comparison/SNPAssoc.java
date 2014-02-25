package views.snp.comparison;

import java.util.Comparator;

/**
 * This class represents an association. 
 * @author rcurtis
 */
public class SNPAssoc
{
    /**
     * The name of the SNP in this association
     */
    private String name;
    /**
     * The location in the genome of the SNP
     */
    private int locus;
    /**
     * The chromosome that this SNP belongs to
     */
    private int chromosome;
    /**
     * The strength of the association
     */
    private double strength;

    /**
     * Creates a new SNPAssoc object
     */
    public SNPAssoc(String name, int chromosome, int locus, double strength)
    {
        this.name = name;
        this.locus = locus;
        this.chromosome = chromosome;
        this.strength = strength;
    }

    /**
     * Determines whether this SNP is significantly association at the given
     * significance level.
     * @param level the significance level to consider
     * @param isPval whether or not the score should be greater or smaller
     * to be significant
     * @return true if this SNP is significantly associated
     */
    public boolean isSignificant(double level, boolean isPval)
    {
        if (isPval && strength < level)
        {
            return true;
        }
        else if (!isPval && strength > level)
        {
            return true;
        }
        return false;
    }

    /**
     * Determines whether the given SNP is located in cis with the SNP in this
     * object.
     * @param snp the snp to compare to
     * @param bp the number of base pairs to consider in cis.
     * @return
     */
    public boolean isSNPinCis(SNPAssoc snp, int bp)
    {
        if (snp.chromosome != chromosome)
        {
            return false;
        }
        if (locus + bp > snp.locus && locus - bp < snp.locus)
        {
            return true;
        }
        return false;
    }

    /**
     * Gets the name of this SNP.
     * @return
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Returns a string representaiton of the location of this SNP.
     * @return
     */
    public String getLocation()
    {
        return chromosome + "(" + locus + ")";
    }

    /**
     * Returns the significance level of this SNP. 
     * @return
     */
    public double getSignificanceLevel()
    {
        return strength;
    }
    /**
     * Compares two SNPs. This way we can presort the array of SNPs instead of
     * having to go through the entire list of SNPs every time we want to
     * find cis snps. 
     */
    public static class SNPLocationComparator implements Comparator<SNPAssoc>
    {
        public int compare(SNPAssoc o1, SNPAssoc o2)
        {
            if (o1.chromosome > o2.chromosome)
            {
                return +1;
            }
            if (o2.chromosome > o1.chromosome)
            {
                return -1;
            }
            if (o1.locus > o2.locus)
            {
                return +1;
            }
            if (o2.locus > o1.locus)
            {
                return -1;
            }
            return 0;
        }
    }
    /**
     * Compares two SNPs by significance, giving weight to higher values
     */
    public static class SNPStrongSignficanceComparator implements Comparator<SNPAssoc>
    {
        public int compare(SNPAssoc o1, SNPAssoc o2)
        {
            if (o1.strength > o2.strength)
            {
                return +1;
            }
            return -1;
        }
    }
    /**
     * Compares two SNPs by significance, giving weight to higher values
     */
    public static class SNPWeakSignficanceComparator implements Comparator<SNPAssoc>
    {
        public int compare(SNPAssoc o1, SNPAssoc o2)
        {
            if (o1.strength < o2.strength)
            {
                return +1;
            }
            return -1;
        }
    }
}
