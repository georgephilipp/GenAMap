package views.network.threeway;

import edu.uci.ics.jung.graph.util.EdgeType;
import java.awt.Color;

/**
 * Represents a directed edge between a trait and a gene.
 * @author rcurtis
 */
public class ThreeWayAssociationEdge extends ThreeWayGraphEdge
{
    @Override
    public EdgeType getEdgeType()
    {
        return EdgeType.UNDIRECTED;
    }

    @Override
    public Color getColor()
    {
        if(this.t1 instanceof ThreeWayGeneGroupNode || this.t2 instanceof ThreeWayTraitGroup)
        {
            return Color.RED;
        }
        return Color.MAGENTA;
    }

    public ThreeWayAssociationEdge(ThreeWayGraphNode t1, ThreeWayGraphNode t2, double weight)
    {
        this.setUpEdge(t1, t2, weight);
    }

    public boolean matches(ThreeWayGraphNode n1, ThreeWayGraphNode n2)
    {
        return this.t1 == n1 && this.t2 == n2;
    }
}
