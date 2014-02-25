package views.network.threeway;

import datamodel.GeneGroup;
import datamodel.Trait;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

/**
 * This is a node in the gene-trait graph that represents a gene. It can be associated
 * with the traits, or it can be associated with the genome as well. 
 * @author rcurtis
 */
public class ThreeWayGeneGroupNode extends ThreeWayGeneGraphObject
{
    /**
     * The number of genes represented by this gene group determines its size.
     */
    private int noGenes;
    /**
     * This is the name of the group. 
     */
    private String name;
    /**
     * A pointer to the gene group object represented here. 
     */
    private GeneGroup mygroups;
    /**
     * Indicates whether this group has been expanded or not
     */
    private boolean isExpanded = false;
    /**
     * The number of the gene group here
     */
    private int groupNumber;

    /**
     * Constructor
     */
    public ThreeWayGeneGroupNode(Color c, int noGenes, String name, GeneGroup g, int groupno)
    {
        this.myColor = c;
        this.noGenes = noGenes;
        this.name = name;
        this.mygroups = g;
        this.groupNumber = groupno;
    }

    /**
     * Returns the number of genes that are in this node object
     */
    public int getNoGenes()
    {
        return this.noGenes;
    }

    @Override
    public Shape getNodeShape()
    {
        return new Ellipse2D.Double(-1 * (noGenes+8)/2,-1 * (noGenes+8)/2,noGenes+8, noGenes + 8);
    }

    @Override
    public String getNodeLabel()
    {
        return "<HTML><center>" + name + " (" + noGenes + ") <br>" + mygroups.getGOEnrich() + "</center></HTML>";
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    /**
     * Returns the number of this group
     */
    public int getGroupNumber()
    {
        return this.groupNumber;
    }

    /**
     * Look to find out if this gene group is expanded, or if it is still a 
     * big circle representing all the genes. 
     * @return
     */
    public boolean isExpanded()
    {
        return this.isExpanded;
    }

    /**
     * Sets whether or not this group should be considered as expanded or not.
     * @param b
     */
    public void setIsExpanded(boolean b)
    {
        this.isExpanded = b;
    }
}
