package views.network.threeway;

import java.awt.Color;
import java.awt.Shape;

/**
 * This class is a node in the three way graph - it can be a trait, a gene
 * or a collection of either (but not both)
 * @author rcurtis
 */
public abstract class ThreeWayGraphNode
{
    /**
     * Indicates whether this node has an association ... this will be used
     * in the force-directed layout. 
     */
    protected boolean hasAssoc = false;
    /**
     * In the case of a filter event, the visibility of a node can be shut off. 
     */
    private boolean isVisible = true;
    /**
     * Gets the color for this node
     */
    public abstract Color getNodeColor();
    /**
     * Gets the shape for this node
     */
    public abstract Shape getNodeShape();
    /**
     * Gets the label for this node
     */
    public abstract String getNodeLabel();
    /**
     * Sets the hasAssoc value to the value specified
     */
    public void setHasAssoc(boolean b)
    {
        this.hasAssoc = b;
    }
    /**
     * Returns the name of the trait or gene for this node
     * @return
     */
    public abstract String getName();

    /**
     * Returns true if this node has an association edge. False if it does
     * not. 
     * @return
     */
    public boolean hasAssociation()
    {
        return this.hasAssoc;
    }

    /**
     * Sets the visibility of this node
     */
    public void setVisible(boolean b)
    {
        this.isVisible = b;
    }

    /**
     * Should this node be displayed in the graph?
     */
    public boolean getIsVisible()
    {
        return this.isVisible;
    }
}

