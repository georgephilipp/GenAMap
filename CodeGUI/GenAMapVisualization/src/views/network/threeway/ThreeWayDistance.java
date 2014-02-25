package views.network.threeway;

import edu.uci.ics.jung.algorithms.shortestpath.Distance;
import edu.uci.ics.jung.graph.Graph;
import java.util.Map;

/**
 * Calculates the distance between two nodes in a graph by actually
 * considering what edges are in the graph. 
 * @author rcurtis
 */
public class ThreeWayDistance implements Distance<ThreeWayGraphNode>
{
    /**
     * The graph that has all of the edges in it
     */
    private Graph<ThreeWayGraphNode, ThreeWayGraphEdge> g;
    /**
     * The highest value of the edge weights. We will want to scale all distances
     * between 0 and .5 where .5 is th farthest away.
     */
    private double maxVal = 0.0;
    /**
     * The minimum value of all the edge weights
     */
    private double minVal = 1e99;

    public ThreeWayDistance(Graph<ThreeWayGraphNode, ThreeWayGraphEdge> g)
    {
        this.g = g;
        for (ThreeWayGraphEdge twge : g.getEdges())
        {
            double w = Math.abs(twge.getWeight());
            if (w > maxVal)
            {
                maxVal = w;
            }
            if (w < minVal)
            {
                minVal = w;
            }
        }
    }

    public Number getDistance(ThreeWayGraphNode source, ThreeWayGraphNode target)
    {
        for (ThreeWayGraphEdge twge : g.getEdges())
        {
            if ((twge.t1.equals(source) && twge.t2.equals(target)) ||
                    twge.t2.equals(source) && twge.t1.equals(target))
            {
                double v = Math.abs(twge.getWeight());
                v = ((maxVal + .01 - 0) - (v - 0)) / (5 * (maxVal - 0));
                return v;
            }
        }
        return null;
    }

    public Map<ThreeWayGraphNode, Number> getDistanceMap(ThreeWayGraphNode source)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
