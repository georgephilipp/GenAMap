package views.network.threeway;

import java.awt.BasicStroke;
import java.awt.Stroke;
import org.apache.commons.collections15.Transformer;

/**
 * This class controls the thickness and color and arrow? of the edges between
 * nodes. 
 * @author rcurtis
 */
public class EdgeLevelStroker implements Transformer<ThreeWayGraphEdge, Stroke>
{
    boolean enabled = true;
    private final Stroke basicStroke = new BasicStroke();

    public EdgeLevelStroker(boolean e)
    {
        enabled = e;
    }

    public Stroke transform(ThreeWayGraphEdge e)
    {
        if (e == null)
        {
            throw new NullPointerException();
        }

        if (enabled)
        {
            float val = (float) (3 * (java.lang.Math.abs(e.getWeight())));// - networkThresh) / (threshMax * 1.01 - networkThresh));
            
            BasicStroke b = new BasicStroke(val, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
            return b;
        }
        return basicStroke;
    }
}
