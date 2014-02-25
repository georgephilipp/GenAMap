package views.network.threeway;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This node represents a collection of traits that have been combined into
 * one group. It can be manually renamed by the user as well. This is shown
 * as a triange in the graph. All traits are always blue! :)
 * @author rcurtis
 */
public class ThreeWayTraitGroup extends ThreeWayTraitGraphObject
{
    /**
     * The number of traits contained in this graph node. 
     */
    private int numTraits = 0;
    /**
     * The list of all traits in this traitgroup
     */
    private String traitlist = "";
    /**
     * The name of this trait group
     */
    private String name = "Trait Group";
    /**
     * The collection of all traits represented by this group
     */
    private Collection<ThreeWayTraitNode> traits;

    /**
     * The collection of all edges belonging to the traits in this group
     */
    //private Collection<ThreeWayGraphEdge> edges;
    /**
     * All of the edges that are going to or from this traitgroup
     */
    //private ArrayList<ThreeWayGraphEdge> myEdges = new ArrayList<ThreeWayGraphEdge>();
    /**
     * Constructor
     */
    public ThreeWayTraitGroup(Collection<ThreeWayTraitGraphObject> traits)//, Collection<ThreeWayGraphEdge> egdes1)
    {
        int cntr = -1;
        this.traits = new ArrayList<ThreeWayTraitNode>();
        //this.edges = new ArrayList<ThreeWayGraphEdge>();
        for (ThreeWayTraitGraphObject twgto : traits)
        {
            if (twgto instanceof ThreeWayTraitGroup)
            {
                this.traits.addAll(((ThreeWayTraitGroup) twgto).traits);
                //this.edges.addAll(((ThreeWayTraitGroup)twgto).edges);
            }
            else
            {
                this.traits.add((ThreeWayTraitNode) twgto);
            }
            if (twgto.hasAssoc)
            {
                this.hasAssoc = true;
            }
        }

        this.traitlist += "<HTML>";
        for (ThreeWayTraitNode twgn : this.traits)
        {
            if (cntr++ % 5 == 4)
            {
                this.traitlist += "<br>";
            }
            this.traitlist += twgn.getName() + ",";
        }
        this.traitlist = this.traitlist.substring(0, traitlist.length() - 1);
        this.traitlist += "</HTML>";
        this.numTraits = this.traits.size();
    }

    @Override
    public Shape getNodeShape()
    {
        int sz = (int) (2.0 * (double) numTraits / 2.0) + 5;
        Polygon poly = new Polygon();
        poly.addPoint(-1 * sz, 0);
        poly.addPoint(sz, 0);
        poly.addPoint(0, -1 * sz * 2);
        return poly;
    }

    @Override
    public String getNodeLabel()
    {
        return traitlist;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public boolean equals(Object n)
    {
        if (!(n instanceof ThreeWayTraitGroup))
        {
            return false;
        }
        if (((ThreeWayTraitGroup) n).traitlist.equals(this.traitlist))
        {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 11 * hash + (this.traitlist != null ? this.traitlist.hashCode() : 0);
        return hash;
    }

    /**
     * Returns the traits associated with this trait object
     */
    public Collection<ThreeWayTraitNode> getTraits()
    {
        return this.traits;
    }

    /**
     * Renames this trait collection
     */
    public void rename(String name)
    {
        this.name = name;
    }
}
