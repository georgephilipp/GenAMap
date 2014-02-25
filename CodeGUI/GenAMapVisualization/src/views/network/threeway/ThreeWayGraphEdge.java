package views.network.threeway;

import edu.uci.ics.jung.graph.util.EdgeType;
import java.awt.Color;

/**
 * Represents an edge between two traits in the three-way visualization
 *
 * We have different types of edges in the graph - those between groups,
 * those between traits, and those between different data types
 * @author rcurtis
 */
public abstract class ThreeWayGraphEdge
{
    /**
     * The source of the edge between traits
     */
    protected ThreeWayGraphNode t1;
    /**
     * The dest of the edge between traits
     */
    protected ThreeWayGraphNode t2;
    /**
     * The weight of the edge between traits
     */
    protected double weight;
    /**
     * The scaling factor for this node.
     */
    protected double max = 1.0;
    /**
     * Determines whether or not this node is one connection or an agglomeration
     */
    protected boolean isOneConn = true;

    /**
     * Returns whether this edge is directed or undirected.
     */
    public abstract EdgeType getEdgeType();
    /**
     * Returns the color that this edge should be shown with
     */
    public abstract Color getColor();
    /**
     * Sets up the node wit the traits and the weight
     */
    protected void setUpEdge(ThreeWayGraphNode t1, ThreeWayGraphNode t2, double weight)
    {
        this.t1 = t1;
        this.t2 = t2;
        this.weight = weight;
    }
    /**
     * Returns the edge source, or the first trait
     */
    public ThreeWayGraphNode getT1()
    {
        return this.t1;
    }

    /**
     * Returns the edge dest, or the second trait
     */
    public ThreeWayGraphNode getT2()
    {
        return this.t2;
    }

    /**
     * Returns the weight between the traits
     */
    public double getWeight()
    {
        return this.weight / max;
    }

    /**
     * Sets the max to the value with which we want to weight the edges ...
     * @param d
     */
    public void setMax(double d)
    {
        this.max = d;
        isOneConn = false;
    }

    /**
     * Can be called to increase the weight of the edge by 1.
     */
    public void addValue()
    {
        this.weight++;
    }

    /**
     * Decreases the weight of the edge by one connection. 
     */
    void decreaseValue()
    {
        this.weight--;
    }
}
