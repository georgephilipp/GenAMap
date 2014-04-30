package views.network.threeway;

import datamodel.Trait;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.Shape;

/**
 * This node represents a trait in a three way graph. 
 * @author rcurtis
 */
public class ThreeWayTraitNode extends ThreeWayTraitGraphObject
{
    /**
     * The trait that this node represents
     */
    private Trait trait;
    /**
     * The shape of a trait
     */
    private Polygon poly;
    /**
     * The trait group, if any, that this trait belongs to
     */
    private ThreeWayTraitGroup mygroup = null;

    /**
     * Constructor
     */
    public ThreeWayTraitNode(Trait t)
    {
        poly = new Polygon();
        poly.addPoint(-4, 8);
        poly.addPoint(4, 8);
        poly.addPoint(8, 0);
        poly.addPoint(4, -8);
        poly.addPoint(-4, -8);
        poly.addPoint(-8, 0);
        this.trait = t;
    }

    @Override
    public Shape getNodeShape()
    {
        return poly;
    }

    @Override
    public String getNodeLabel()
    {
        return trait.getName();
    }

    @Override
    public String getName()
    {
        return trait.getName();
    }

    @Override
    public boolean equals(Object n)
    {
        if (!(n instanceof ThreeWayTraitNode))
        {
            return false;
        }
        if (((ThreeWayTraitNode) n).trait.getId() == this.trait.getId())
        {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 71 * hash + (this.trait != null ? this.trait.hashCode() : 0);
        return hash;
    }

    /**
     * Returns the trait associated with this trait node.
     * @return
     */
    public Trait getTrait()
    {
        return this.trait;
    }

    /**
     * Returns null if there is no group, or the group is this
     * trait is in a group right now.
     */
    public ThreeWayTraitGroup getTraitGroup()
    {
        return this.mygroup;
    }

    /**
     * Sets the trait group to the value
     */
    public void setTraitGroup(ThreeWayTraitGroup twtg)
    {
        this.mygroup = twtg;
    }
}
