package views.network.threeway;

import java.awt.Color;

/**
 * Simple super class so that genegroups and genes can be treated similarly
 * in the display of the nodes. 
 * @author rcurtis
 */
public abstract class ThreeWayGeneGraphObject extends ThreeWayGraphNode
{
    /**
     * This is the color that has been assigned to this gene group.
     */
    protected Color myColor;
    /**
     * Whether or not this gene group should account for association value
     * in determining what color it should be.
     */
    private boolean isAssoc = false;
    /**
     * We take the association value and scale it appropriately.
     */
    private float scaleFactor = 0.0f;

    @Override
    public Color getNodeColor()
    {
        //we will need to check to see whether or not this node is
        //currently colored by association and then return the correct
        //hue. Otherwise, we have been assigned a color to return.
        if (isAssoc)
        {
            float colors[] = myColor.getRGBColorComponents(null);
            Color c = new Color(colors[0] * scaleFactor,
                                colors[1] * scaleFactor,
                                colors[2] * scaleFactor);
            return c;
        }
        else
        {
            return myColor;
        }
    }

    /**
     * Sets the scale factor for this gene and marks it as associated.
     */
    public void setColorScaleFactor(float factor)
    {
        this.isAssoc = true;
        if(factor > this.scaleFactor)
        {
            this.scaleFactor = factor;
        }
    }

    /**
     * Resets the scale factor and marks the node as not associated
     */
    public void setNodeAsNotAssoc()
    {
        scaleFactor = 0.0f;
        this.isAssoc = false;
    }
}
