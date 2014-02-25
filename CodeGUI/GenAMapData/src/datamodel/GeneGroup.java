package datamodel;

import BiNGO.GoItems;
import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * A gene group holds a collection of genes. It has a name and a size.
 * It also has GO annotations. 
 * @author rcurtis
 */
public class GeneGroup implements Serializable
{
    /**
     * A pointer to all the genes in the group
     */
    private ArrayList<Trait> genes;
    /**
     * The base color that this group is based off of
     */
    private Color groupColor;
    /**
     * The name of the group - Group X
     */
    private String groupName;
    /**
     * The string representation of GO enrichment that belongs
     * to this gene group
     */
    private String goEnrichment = "";

    /**
     * Constructor
     */
    public GeneGroup(ArrayList<Trait> genes, Color c, String groupName)
    {
        this.genes = genes;
        this.groupColor = c;
        this.groupName = groupName;
    }

    /**
     * Returns all genes involved in this group as a traitset.
     */
    public ArrayList<Trait> getGenes()
    {
        return this.genes;
    }

    /**
     * Returns the color that this group should be colored
     */
    public Color getGeneColor()
    {
        return this.groupColor;
    }

    /**
     * Returns the name of this group
     */
    public String getGroupName()
    {
        return this.groupName;
    }

    /**
     * Adds a gene to this group
     */
    public void addGene(Trait t)
    {
        this.genes.add(t);
    }

    /**
     * Sets the color of this group to the specified color
     */
    public void setColor(Color c)
    {
        this.groupColor = c;
    }

    @Override
    public String toString()
    {
        return this.groupName + "(" + this.genes.size() + ")";
    }

    /**
     * This method returns what group number this group represents
     * @return
     */
    public int getGroupNo()
    {
        return Integer.parseInt(this.groupName.replace("Group ", ""));
    }

    /**
     * Gets the GO Enrichment string and values for this GeneGroup. 
     * @return
     */
    public String getGOEnrich()
    {
        return goEnrichment;
    }

    /**
     * Returns the arraylist of indeces associated with this trait subset. 
     */
    public ArrayList<Integer> getGeneSubsetIds()
    {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for (Trait g : this.genes)
        {
            ids.add(g.getId());
        }
        return ids;
    }

    /**
     * Sets the GO enrichment string for this GeneGroup.
     * @param s
     */
    public void setGoEnrichment(ArrayList<GoItems> goItems)
    {
        this.goEnrichment = "";
        for(int i = 0; i < goItems.size() && i < 10; i ++)
        {
            this.goEnrichment += goItems.get(i).descr + " " + goItems.get(i).pval + "<br>";
        }
    }
}
