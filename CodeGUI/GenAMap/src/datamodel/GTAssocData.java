package datamodel;

import java.io.Serializable;

/**
 * This object holds information about a gene-trait association found in
 * the database. It is a data holder. 
 * @author rcurtis
 */
public class GTAssocData implements Serializable
{
    /**
     * The id of the gene involved in this association
     */
    private int geneid;
    /**
     * The id of the trait involved in this association
     */
    private int traitid;
    /**
     * The value of association between the two
     */
    private double value;
    /**
     * The group number that the gene belongs to.
     */
    private int grp;

    /**
     * Constructor
     */
    public GTAssocData(int gid, int tid, double val, int genegrp)
    {
        this.geneid = gid;
        this.traitid = tid;
        this.value = val;
        this.grp = genegrp;
    }

    /**
     * Returns the geneid
     */
    public int getGeneId()
    {
        return this.geneid;
    }

    /**
     * Returns the traitid
     */
    public int getTraitId()
    {
        return this.traitid;
    }

    /**
     * Returns the value of the association
     */
    public double getValue()
    {
        return this.value;
    }

    /**
     * Gets the gene group of the geneid
     */
    public int getGeneGroup()
    {
        return this.grp;
    }
}
