package views.network.threeway;

import edu.uci.ics.jung.graph.util.EdgeType;
import java.awt.Color;

/**
 * Represents an undirected edge in the graph between two genes or two traits
 * @author rcurtis
 */
public class ThreeWayCorrelationEdge extends ThreeWayGraphEdge
{

    @Override
    public EdgeType getEdgeType()
    {
        return EdgeType.UNDIRECTED;
    }

    @Override
    public Color getColor()
    {
        if(this.t1 instanceof ThreeWayTraitNode)
        {
            return Color.GRAY;
        }
        else if (this.t1 instanceof ThreeWayTraitGroup)
        {
            return Color.LIGHT_GRAY;
        }
        else if(this.t1 instanceof ThreeWayGeneGroupNode)
        {
            return Color.BLACK;
        }
        else
        {
            return Color.DARK_GRAY;
        }
    }

    public ThreeWayCorrelationEdge(ThreeWayGraphNode t1, ThreeWayGraphNode t2, double weight)
    {
        this.setUpEdge(t1, t2, weight);
    }
}
