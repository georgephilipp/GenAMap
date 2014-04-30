package views.network.threeway;

import java.awt.Color;

/**
 * This is a trait or a collection of traits in the graph structure
 * @author rcurtis
 */
public abstract class ThreeWayTraitGraphObject extends ThreeWayGraphNode
{

    @Override
    public Color getNodeColor()
    {
        return Color.BLUE;
    }
}
