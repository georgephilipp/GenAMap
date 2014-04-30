package views.network.threeway;

import datamodel.Trait;
import java.awt.Rectangle;
import java.awt.Shape;

/**
 * Represents an individual gene with connections to traits, trait groups,
 * gene groups, etc. 
 * @author rcurtis
 */
public class ThreeWayGeneNode extends ThreeWayGeneGraphObject
{
    /**
     * The gene that this node represents
     */
    private Trait mygene;
    /**
     * What gene group this gene belongs to
     */
    private ThreeWayGeneGroupNode mygroup;

    /**
     * Constructor
     */
    public ThreeWayGeneNode(Trait gene, ThreeWayGeneGroupNode twgg)
    {
        this.myColor = twgg.myColor;
        this.mygene= gene;
        this.mygroup = twgg;
    }

    @Override
    public Shape getNodeShape()
    {
        return new Rectangle(16,16);
    }

    @Override
    public String getNodeLabel()
    {
        return mygene.getName();
    }

    @Override
    public String getName()
    {
        return mygene.getName();
    }

    /**
     * Returns the gene association with this gene node.
     * @return
     */
    public Trait getGene()
    {
        return this.mygene;
    }

    /**
     * Returns the group of this gene.
     * @return
     */
    public ThreeWayGeneGroupNode getGroup()
    {
        return this.mygroup;
    }
}
