package views.network.threeway;

import java.awt.Paint;
import org.apache.commons.collections15.Transformer;

/**
 * Depending on the type of edge and what it is connecting, the edge will
 * want to be different colors. This class determines what those colors
 * will be. 
 * @author rcurtis
 */
public class ThreeWayEdgePaintTransformer implements Transformer<ThreeWayGraphEdge, Paint>
{
    public Paint transform(ThreeWayGraphEdge arg0)
    {
        return arg0.getColor();
    }
}
