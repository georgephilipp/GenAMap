package views.network.threeway;

import edu.uci.ics.jung.graph.AbstractGraph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * This is a graph structure for showing the network between genes and traits
 * while also showing the connections between them. This is used by the JUNG
 * system in order to display the genes and traits on the screen. We also have
 * to make our own renders and layouts.
 *
 * We want to implement our own graph structure so that we can account for the unique
 * patterns in the data.
 * @author rcurtis
 */
public class ThreeWayGraph extends AbstractGraph<ThreeWayGraphNode, ThreeWayGraphEdge>
{
    public ThreeWayGraph()
    {
        vertices = new HashMap<ThreeWayGraphNode, Pair<Map<ThreeWayGraphNode, ThreeWayGraphEdge>>>();
        edges = new HashMap<ThreeWayGraphEdge, Pair<ThreeWayGraphNode>>();
    }
    /**
     * Map of vertices to adjacency maps of vertices to incident edges
     */
    protected Map<ThreeWayGraphNode, Pair<Map<ThreeWayGraphNode, ThreeWayGraphEdge>>> vertices;
    /**
     * Map of edges to incident vertex sets
     */
    protected Map<ThreeWayGraphEdge, Pair<ThreeWayGraphNode>> edges;

    @Override
    public boolean addEdge(ThreeWayGraphEdge edge, ThreeWayGraphNode n1, ThreeWayGraphNode n2)
    {
        Pair<ThreeWayGraphNode> p = new Pair(n1, n2);
        return addEdge(edge, p, null);
    }

    @Override
    public boolean addEdge(ThreeWayGraphEdge edge, Pair<? extends ThreeWayGraphNode> endpoints, EdgeType edgeType)
    {
        Pair<ThreeWayGraphNode> new_endpoints = getValidatedEndpoints(edge, endpoints);
        if (new_endpoints == null)
        {
            return false;
        }

        ThreeWayGraphNode v1 = new_endpoints.getFirst();
        ThreeWayGraphNode v2 = new_endpoints.getSecond();

        if (findEdge(v1, v2) != null)
        {
            return false;
        }

        edges.put(edge, new_endpoints);

        if (!vertices.containsKey(v1))
        {
            this.addVertex(v1);
        }

        if (!vertices.containsKey(v2))
        {
            this.addVertex(v2);
        }

        // map v1 to <v2, edge> and vice versa
        vertices.get(v1).getSecond().put(v2, edge);
        vertices.get(v2).getFirst().put(v1, edge);

        return true;
    }

    protected Collection<ThreeWayGraphEdge> getIncoming_internal(ThreeWayGraphNode vertex)
    {
        return vertices.get(vertex).getFirst().values();
    }

    protected Collection<ThreeWayGraphEdge> getOutgoing_internal(ThreeWayGraphNode vertex)
    {
        return vertices.get(vertex).getSecond().values();
    }

    protected Collection<ThreeWayGraphNode> getPreds_internal(ThreeWayGraphNode vertex)
    {
        return vertices.get(vertex).getFirst().keySet();
    }

    protected Collection<ThreeWayGraphNode> getSuccs_internal(ThreeWayGraphNode vertex)
    {
        return vertices.get(vertex).getSecond().keySet();
    }

    public Collection<ThreeWayGraphEdge> getInEdges(ThreeWayGraphNode vertex)
    {
        if (!containsVertex(vertex))
        {
            return null;
        }
        return Collections.unmodifiableCollection(getIncoming_internal(vertex));
    }

    public Collection<ThreeWayGraphEdge> getOutEdges(ThreeWayGraphNode vertex)
    {
        if (!containsVertex(vertex))
        {
            return null;
        }
        return Collections.unmodifiableCollection(getOutgoing_internal(vertex));
    }

    public Collection<ThreeWayGraphNode> getPredecessors(ThreeWayGraphNode vertex)
    {
        if (!containsVertex(vertex))
        {
            return null;
        }
        return Collections.unmodifiableCollection(getPreds_internal(vertex));
    }

    public Collection<ThreeWayGraphNode> getSuccessors(ThreeWayGraphNode vertex)
    {
        if (!containsVertex(vertex))
        {
            return null;
        }
        return Collections.unmodifiableCollection(getSuccs_internal(vertex));
    }

    public ThreeWayGraphNode getSource(ThreeWayGraphEdge directed_edge)
    {
        if (!containsEdge(directed_edge))
        {
            return null;
        }
        return edges.get(directed_edge).getFirst();
    }

    public ThreeWayGraphNode getDest(ThreeWayGraphEdge directed_edge)
    {
        if (!containsEdge(directed_edge))
        {
            return null;
        }
        return edges.get(directed_edge).getSecond();
    }

    public boolean isSource(ThreeWayGraphNode vertex, ThreeWayGraphEdge edge)
    {
        if (!containsEdge(edge) || !containsVertex(vertex))
        {
            return false;
        }
        return vertex.equals(this.getEndpoints(edge).getFirst());
    }

    public boolean isDest(ThreeWayGraphNode vertex, ThreeWayGraphEdge edge)
    {
        if (!containsEdge(edge) || !containsVertex(vertex))
        {
            return false;
        }
        return vertex.equals(this.getEndpoints(edge).getSecond());
    }

    public Pair<ThreeWayGraphNode> getEndpoints(ThreeWayGraphEdge edge)
    {
        if (!containsEdge(edge))
        {
            return null;
        }
        return edges.get(edge);
    }

    public Collection<ThreeWayGraphEdge> getEdges()
    {
        return Collections.unmodifiableCollection(edges.keySet());
    }

    public Collection<ThreeWayGraphNode> getVertices()
    {
        return Collections.unmodifiableCollection(vertices.keySet());
    }

    public boolean containsVertex(ThreeWayGraphNode vertex)
    {
        return vertices.containsKey(vertex);
    }

    public boolean containsEdge(ThreeWayGraphEdge edge)
    {
        return edges.containsKey(edge);
    }

    public int getEdgeCount()
    {
        return this.edges.size();
    }

    public int getVertexCount()
    {
        return this.vertices.size();
    }

    public Collection<ThreeWayGraphNode> getNeighbors(ThreeWayGraphNode vertex)
    {
        if (!containsVertex(vertex))
        {
            return null;
        }

        Collection<ThreeWayGraphNode> neighbors = new HashSet<ThreeWayGraphNode>();
        neighbors.addAll(getPreds_internal(vertex));
        neighbors.addAll(getSuccs_internal(vertex));
        return Collections.unmodifiableCollection(neighbors);
    }

    public Collection<ThreeWayGraphEdge> getIncidentEdges(ThreeWayGraphNode vertex)
    {
        if (!containsVertex(vertex))
        {
            return null;
        }

        Collection<ThreeWayGraphEdge> incident_edges = new HashSet<ThreeWayGraphEdge>();
        incident_edges.addAll(getIncoming_internal(vertex));
        incident_edges.addAll(getOutgoing_internal(vertex));
        return Collections.unmodifiableCollection(incident_edges);
    }

    public boolean addVertex(ThreeWayGraphNode vertex)
    {
        if (vertex == null)
        {
            throw new IllegalArgumentException("vertex may not be null");
        }
        if (!containsVertex(vertex))
        {
            vertices.put(vertex, new Pair<Map<ThreeWayGraphNode, ThreeWayGraphEdge>>(new HashMap<ThreeWayGraphNode, ThreeWayGraphEdge>(), new HashMap<ThreeWayGraphNode, ThreeWayGraphEdge>()));
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean removeVertex(ThreeWayGraphNode vertex)
    {
        if (!containsVertex(vertex))
        {
            return false;
        }

        // copy to avoid concurrent modification in removeEdge
        ArrayList<ThreeWayGraphEdge> incident = new ArrayList<ThreeWayGraphEdge>(getIncoming_internal(vertex));
        incident.addAll(getOutgoing_internal(vertex));

        for (ThreeWayGraphEdge edge : incident)
        {
            removeEdge(edge);
        }

        vertices.remove(vertex);

        return true;
    }

    public boolean removeEdge(ThreeWayGraphEdge edge)
    {
        if (!containsEdge(edge))
        {
            return false;
        }

        Pair<ThreeWayGraphNode> endpoints = this.getEndpoints(edge);
        ThreeWayGraphNode source = endpoints.getFirst();
        ThreeWayGraphNode dest = endpoints.getSecond();

        // remove vertices from each others' adjacency maps
        vertices.get(source).getSecond().remove(dest);
        vertices.get(dest).getFirst().remove(source);

        edges.remove(edge);
        return true;
    }

    public EdgeType getDefaultEdgeType()
    {
        return EdgeType.UNDIRECTED;
    }

    public Collection<ThreeWayGraphEdge> getEdges(EdgeType edge_type)
    {
        ArrayList<ThreeWayGraphEdge> toRet = new ArrayList<ThreeWayGraphEdge>();

        for (ThreeWayGraphEdge e : this.getEdges())
        {
            if (e.getEdgeType() == edge_type)
            {
                toRet.add(e);
            }
        }

        return toRet;
    }

    public int getEdgeCount(EdgeType edge_type)
    {
        return getEdges(edge_type).size();
    }

    public EdgeType getEdgeType(ThreeWayGraphEdge edge)
    {
        ThreeWayGraphEdge e = edge;
        return e.getEdgeType();
    }

    /**
     * Gets the vertex corresponding to this label
     */
    public ThreeWayGraphNode getVertex(String name)
    {
        Collection<ThreeWayGraphNode> twgn = this.getVertices();
        for(ThreeWayGraphNode t : twgn)
        {
            if(t.getName().equals(name))
            {
                return t;
            }
        }
        return null;
    }
}
