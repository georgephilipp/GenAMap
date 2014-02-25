package views.network.threeway;

import datamodel.GTAssocData;
import datamodel.GeneTraitAssociation;
import edu.uci.ics.jung.graph.Graph;
import java.util.ArrayList;
import java.util.Collection;
import views.network.GeneTraitNetworkView;

/**
 * There is a TON of code that goes into adding, removing, and maintaining
 * the different edge types in these graphs. Because it is really repetitive
 * across the three types, I've included it all in this same class in order
 * to simplify the code and move it to one storage location.
 *
 * All this class does is add and remove edges from the graph. Crazy, huh? 
 *
 * It could be refactored to have fewer lines of code, but it isn't instantly
 * obvious to me how to best do this ... :)
 * @author rcurtis
 */
public class ThreeWayEdgeManager
{
    /**
     * Initializes the association edges for the entire graph.
     * @param g1
     */
    public static void initAssocEdges(ThreeWayGraph g1, GeneTraitAssociation gta,
            ArrayList<ThreeWayGeneNode> geneNodes, ArrayList<ThreeWayAssociationEdge> genetraitEdges)
    {
        Collection<GTAssocData> assocs = gta.getGTAssocs();
        ArrayList<ThreeWayAssociationEdge> edges = new ArrayList<ThreeWayAssociationEdge>();
        double max = 1.0;
        for (GTAssocData gtad : assocs)
        {
            String traitname = gta.getTraitSet().getTrait(gtad.getTraitId()).getName();
            ThreeWayGraphNode n1 = g1.getVertex("Group " + gtad.getGeneGroup());
            ThreeWayGraphNode n2 = g1.getVertex(traitname);
            ThreeWayGraphNode n3 = null;
            for (ThreeWayGeneNode gn : geneNodes)
            {
                if (gn.getGene().getId() == gtad.getGeneId())
                {
                    n3 = gn;
                    break;
                }
            }
            genetraitEdges.add(new ThreeWayAssociationEdge(n3, n2, gtad.getValue()));
            n3.setHasAssoc(true);
            n2.setHasAssoc(true);
            ThreeWayAssociationEdge twde = null;
            for (ThreeWayAssociationEdge d : edges)
            {
                if (d.matches(n1, n2))
                {
                    twde = d;
                    twde.addValue();
                    n1.setHasAssoc(true);
                    n2.setHasAssoc(true);
                    break;
                }
            }
            if (twde == null)
            {
                twde = new ThreeWayAssociationEdge(n1, n2, 1);
                n1.setHasAssoc(true);
                n2.setHasAssoc(true);
                edges.add(twde);
            }
            if (twde.getWeight() > max)
            {
                max = twde.getWeight();
            }
        }
        for (ThreeWayAssociationEdge d : edges)
        {
            d.setMax(max / 3.0);
            if (!g1.containsEdge(d))
            {
                g1.addEdge(d, d.getT1(), d.getT2());
            }
        }
    }

    /**
     * Adds and removes nodes that don't have any associations from the graph
     */
    public static void addOrRemoveNodesWOAssociation(Graph<ThreeWayGraphNode, ThreeWayGraphEdge> g,
            boolean isAddingNodes, GeneTraitNetworkView gtnv, double traitEdgeThreshold, double geneEdgeThreshold)
    {
        if (!isAddingNodes)
        {
            ArrayList<ThreeWayGraphNode> nodesToRemove = new ArrayList<ThreeWayGraphNode>();
            for (ThreeWayGraphNode twgn : g.getVertices())
            {
                if (!twgn.hasAssociation())
                {
                    nodesToRemove.add(twgn);
                }
            }
            for (ThreeWayGraphNode twgn : nodesToRemove)
            {
                g.removeVertex(twgn);
            }
        }
        else
        {
            for (ThreeWayGeneNode twgn : gtnv.getGenes())
            {
                if (!twgn.hasAssociation() && twgn.getGroup().isExpanded() && twgn.getIsVisible())
                {
                    g.addVertex(twgn);
                }
            }
            for (ThreeWayGeneGroupNode twggn : gtnv.getGeneGroups())
            {
                if (!twggn.hasAssociation() && twggn.getIsVisible())
                {
                    g.addVertex(twggn);
                }
            }
            for (ThreeWayTraitNode twtn : gtnv.getTraits())
            {
                if (!twtn.hasAssociation() && twtn.getIsVisible())
                {
                    g.addVertex(twtn);
                }
            }
            ArrayList<ThreeWayCorrelationEdge> edgesToAdd = new ArrayList<ThreeWayCorrelationEdge>();
            for (ThreeWayGeneNode twgn : gtnv.getGenes())
            {
                if (!twgn.hasAssociation() && twgn.getGroup().isExpanded() && twgn.getIsVisible())
                {
                    ThreeWayEdgeManager.getEdgesToAddForGene(twgn, edgesToAdd, gtnv, geneEdgeThreshold, isAddingNodes);
                }
            }
            for (ThreeWayGeneGroupNode twggn : gtnv.getGeneGroups())
            {
                if (!twggn.hasAssociation() && twggn.getIsVisible())
                {
                    ThreeWayEdgeManager.getEdgesToAddForGeneGroup(twggn, edgesToAdd, gtnv, geneEdgeThreshold, isAddingNodes);
                }
            }
            for (ThreeWayTraitNode twtn : gtnv.getTraits())
            {
                if (!twtn.hasAssociation() && twtn.getIsVisible())
                {
                    ThreeWayEdgeManager.getEdgesToAddForTrait(twtn, edgesToAdd, isAddingNodes, traitEdgeThreshold, gtnv);
                }
            }
            for (ThreeWayGraphEdge twge : edgesToAdd)
            {
                if (g.containsVertex(twge.getT1()) && g.containsVertex(twge.getT2()))
                {
                    g.addEdge(twge, twge.getT1(), twge.getT2());
                }
            }

        }
        ThreeWayEdgeManager.setMaxValsAcrossEdgeGroups(g);
        gtnv.setUpVisualizationRenderingStuff(g);
    }

    /**
     * Addds or removes the association edges
     */
    public static void dealWithChangingAssocEdgeVisiblity(Graph<ThreeWayGraphNode, ThreeWayGraphEdge> g,
            boolean isShowAssociationEdges, GeneTraitNetworkView gtnv)
    {
        if (!isShowAssociationEdges)
        {
            ArrayList<ThreeWayGraphEdge> edgesToGo = new ArrayList<ThreeWayGraphEdge>();
            for (ThreeWayGraphEdge twge : g.getEdges())
            {
                if (twge instanceof ThreeWayAssociationEdge)
                {
                    edgesToGo.add(twge);
                }
            }
            for (ThreeWayGraphEdge twge : edgesToGo)
            {
                g.removeEdge(twge);
            }
        }
        else
        {
            ArrayList<ThreeWayAssociationEdge> edgesToAdd = new ArrayList<ThreeWayAssociationEdge>();
            ThreeWayEdgeManager.addAssociationForAllNodes(edgesToAdd, isShowAssociationEdges, gtnv);
            for (ThreeWayAssociationEdge twae : edgesToAdd)
            {
                if (!g.containsEdge(twae) && g.containsVertex(twae.getT1()) && g.containsVertex(twae.getT2()))
                {
                    g.addEdge(twae, twae.getT1(), twae.getT2());
                }
            }
            ThreeWayEdgeManager.setMaxValsAcrossEdgeGroups(g);
        }
    }

    /**
     * Adds and removes nodes based on a new trait threshold
     */
    public static void dealWithTraitEdgeThreshChange(double newThresh, Graph<ThreeWayGraphNode, ThreeWayGraphEdge> g,
            GeneTraitNetworkView gtnv, double traitEdgeThreshold, boolean isShowNodesWithNoAssoc)
    {
        if (newThresh > traitEdgeThreshold)
        {
            for (ThreeWayCorrelationEdge twge : gtnv.getTraitEdges())
            {
                double weight = Math.abs(twge.weight);
                if (weight > traitEdgeThreshold && weight < newThresh)
                {
                    ThreeWayTraitNode t1 = (ThreeWayTraitNode) twge.getT1();
                    ThreeWayTraitNode t2 = (ThreeWayTraitNode) twge.getT2();
                    ThreeWayTraitGraphObject o1 = null;
                    ThreeWayTraitGraphObject o2 = null;
                    if (t1.getTraitGroup() == null && t2.getTraitGroup() == null)
                    {
                        g.removeEdge(twge);
                    }
                    else if (t1.getTraitGroup() == null)
                    {
                        o1 = t1;
                        o2 = t2.getTraitGroup();
                    }
                    else if (t2.getTraitGroup() == null)
                    {
                        o1 = t2;
                        o2 = t1.getTraitGroup();
                    }
                    else
                    {
                        if (t1.getTraitGroup() != t2.getTraitGroup())
                        {
                            o1 = t1.getTraitGroup();
                            o2 = t2.getTraitGroup();
                        }
                    }
                    if (o1 != null && o2 != null)
                    {
                        ThreeWayGraphEdge badEdge = null;
                        for (ThreeWayGraphEdge candidate : g.getEdges())
                        {
                            if ((candidate.getT1() == o1 && candidate.getT2() == o2) || (candidate.getT2() == o1 && candidate.getT1() == o2))
                            {
                                badEdge = candidate;
                                break;
                            }
                        }
                        if (badEdge != null)
                        {
                            //g.removeEdge(badEdge);
                            badEdge.decreaseValue();
                            if (badEdge.weight == 0)
                            {
                                g.removeEdge(badEdge);
                            }
                        }
                    }
                }
            }
        }
        else
        {
            for (ThreeWayCorrelationEdge twge : gtnv.getTraitEdges())
            {
                double weight = Math.abs(twge.weight);
                if (weight < traitEdgeThreshold && weight > newThresh)
                {
                    ThreeWayTraitNode t1 = (ThreeWayTraitNode) twge.getT1();
                    ThreeWayTraitNode t2 = (ThreeWayTraitNode) twge.getT2();
                    ThreeWayTraitGraphObject o1 = null;
                    ThreeWayTraitGraphObject o2 = null;
                    if (t1.getTraitGroup() == null && t2.getTraitGroup() == null)
                    {
                        if (twge.getT1().getIsVisible() && twge.getT2().getIsVisible() &&
                                (isShowNodesWithNoAssoc || (twge.getT1().hasAssociation() && twge.getT2().hasAssociation())))
                        {
                            g.addEdge(twge, twge.getT1(), twge.getT2());
                        }
                    }
                    else if (t1.getTraitGroup() == null)
                    {
                        o1 = t1;
                        o2 = t2.getTraitGroup();
                    }
                    else if (t2.getTraitGroup() == null)
                    {
                        o1 = t2;
                        o2 = t1.getTraitGroup();
                    }
                    else
                    {
                        if (t1.getTraitGroup() != t2.getTraitGroup())
                        {
                            o1 = t1.getTraitGroup();
                            o2 = t2.getTraitGroup();
                        }
                    }
                    if (o1 != null && o2 != null)
                    {
                        ThreeWayGraphEdge badEdge = null;
                        for (ThreeWayGraphEdge candidate : g.getEdges())
                        {
                            if ((candidate.getT1() == o1 && candidate.getT2() == o2) || (candidate.getT2() == o1 && candidate.getT1() == o2))
                            {
                                badEdge = candidate;
                                break;
                            }
                        }
                        if (badEdge != null)
                        {
                            badEdge.addValue();
                        }
                        else
                        {
                            ThreeWayCorrelationEdge ed = new ThreeWayCorrelationEdge(o1, o2, 1.0);
                            if (twge.getT1().getIsVisible() && twge.getT2().getIsVisible() &&
                                    (isShowNodesWithNoAssoc || (ed.getT1().hasAssociation() && ed.getT2().hasAssociation())))
                            {
                                g.addEdge(ed, ed.getT1(), ed.getT2());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Expands the trait edges by adding all the new edges to the newly
     * visible trait nodes
     */
    public static void epandTraitEdges(ThreeWayTraitGroup twtg, Graph g, ThreeWayLayout twl, double x, double y,
            GeneTraitNetworkView gtnv, boolean isShowNodesWithNoAssoc, double traitEdgeThreshold, 
            boolean isShowAssociationEdges, double assocThreshold)
    {
        ArrayList<ThreeWayCorrelationEdge> edgesToAdd = new ArrayList<ThreeWayCorrelationEdge>();
        ArrayList<ThreeWayAssociationEdge> edgesToAdd2 = new ArrayList<ThreeWayAssociationEdge>();
        for (ThreeWayGraphNode twgn : twtg.getTraits())
        {
            g.addVertex(twgn);
            twl.setLocation(twgn, x, y);
            ThreeWayEdgeManager.getEdgesToAddForTrait(twgn, edgesToAdd, isShowNodesWithNoAssoc, traitEdgeThreshold, gtnv);
            ThreeWayEdgeManager.addAssociationEdges(edgesToAdd2, twgn, isShowAssociationEdges, gtnv, assocThreshold);
        }
        for (ThreeWayCorrelationEdge twce : edgesToAdd)
        {
            if (g.containsVertex(twce.getT1()) && g.containsVertex(twce.getT2()))
            {
                g.addEdge(twce, twce.getT1(), twce.getT2());
            }
        }
        for (ThreeWayAssociationEdge twae : edgesToAdd2)
        {
            if (g.containsVertex(twae.getT1()) && g.containsVertex(twae.getT2()))
            {
                g.addEdge(twae, twae.getT1(), twae.getT2());
                
            }
        }
        ThreeWayEdgeManager.setMaxValsAcrossEdgeGroups(g);
    }

    /**
     * Called from a trait collapse call. This collapses all the edges for that trait.
     */
    public static void collapseTraitEdges(ThreeWayLayout twl, ThreeWayTraitGroup twtg, double x, double y, Graph g,
            GeneTraitNetworkView gtnv, double traitEdgeThreshold, boolean isShowNodesWithNoAssoc, 
            boolean isShowAssociationEdges, double assocThreshold)
    {
        twl.setLocation(twtg, x, y);
        ThreeWayEdgeManager.addAllEdgesToTraitGroup(twtg, g, gtnv, traitEdgeThreshold, isShowNodesWithNoAssoc);
        ArrayList<ThreeWayAssociationEdge> edgesToAdd = new ArrayList<ThreeWayAssociationEdge>();
        ThreeWayEdgeManager.addAssociationEdges(edgesToAdd, twtg, isShowAssociationEdges, gtnv, assocThreshold);
        for (ThreeWayAssociationEdge twae : edgesToAdd)
        {
            if (g.containsVertex(twae.getT1()) && g.containsVertex(twae.getT2()))
            {
                g.addEdge(twae, twae.getT1(), twae.getT2());
            }
        }
        ThreeWayEdgeManager.setMaxValsAcrossEdgeGroups(g);
    }

    /**
     * Goes through the graph to calculate the max values in groups and set
     * the max value for the different edges.
     */
    public static void setMaxValsAcrossEdgeGroups(Graph g)
    {
        double maxTT = 0.0;
        double maxGT = 0.0;
        double maxGG = 0.0;
        Collection<ThreeWayGraphEdge> lotsofedges = new ArrayList<ThreeWayGraphEdge>();
        lotsofedges.addAll(g.getEdges());

        for (Object o : lotsofedges)
        {
            ThreeWayGraphEdge edge = (ThreeWayGraphEdge) o;
            if (edge.t1 instanceof ThreeWayTraitGraphObject && edge.t2 instanceof ThreeWayTraitGraphObject)
            {
                if (edge.weight > maxTT)
                {
                    maxTT = edge.weight;
                }
            }
            else if (edge.t1 instanceof ThreeWayTraitGroup || edge.t2 instanceof ThreeWayTraitGroup)
            {
                if (edge.weight > maxGT)
                {
                    maxGT = edge.weight;
                }
            }
            else if (edge.t1 instanceof ThreeWayGeneGroupNode || edge.t2 instanceof ThreeWayGeneGroupNode)
            {
                if (edge.weight > maxGG)
                {
                    maxGG = edge.weight;
                }
            }
        }
        for (Object o : lotsofedges)
        {
            ThreeWayGraphEdge edge = (ThreeWayGraphEdge) o;
            if (edge.t1 instanceof ThreeWayTraitGraphObject && edge.t2 instanceof ThreeWayTraitGraphObject)
            {
                edge.max = maxTT;
            }
            else if (edge.t1 instanceof ThreeWayTraitGroup || edge.t2 instanceof ThreeWayTraitGroup)
            {
                edge.max = maxGT / 3.0;
            }
            else if (edge.t1 instanceof ThreeWayGeneGroupNode || edge.t2 instanceof ThreeWayGeneGroupNode)
            {
                edge.max = maxGG / 2.0;
            }
        }
    }

    /**
     * Adds all edges to the trait group object in the graph
     * @param twtg the trait group that will have edges added to it
     * @param g the graph we are working with. 
     */
    public static void addAllEdgesToTraitGroup(ThreeWayTraitGroup twtg, Graph g,
            GeneTraitNetworkView gtnv, double traitEdgeThreshold, boolean isShowNodesWithNoAssoc)
    {
        ArrayList<ThreeWayCorrelationEdge> edgesToAdd = new ArrayList<ThreeWayCorrelationEdge>();
        for (ThreeWayCorrelationEdge twue : gtnv.getTraitEdges())
        {
            if (Math.abs(twue.weight) < traitEdgeThreshold)
            {
                continue;
            }
            for (ThreeWayTraitNode twtn : twtg.getTraits())
            {
                ThreeWayTraitNode other = null;
                double weight = twue.weight;
                if (twtn == twue.getT1())
                {
                    other = (ThreeWayTraitNode) twue.getT2();
                }
                else if (twtn == twue.getT2())
                {
                    other = (ThreeWayTraitNode) twue.getT1();
                }
                if (other != null && (other.hasAssociation() || isShowNodesWithNoAssoc) && other.getIsVisible())
                {
                    if (other.getTraitGroup() != null)
                    {
                        if (other.getTraitGroup() == twtg)
                        {
                            continue;
                        }
                        else
                        {
                            boolean isFound = false;
                            for (ThreeWayCorrelationEdge twce : edgesToAdd)
                            {
                                if (twce.getT1() == other.getTraitGroup() || twce.getT2() == other.getTraitGroup())
                                {
                                    twce.addValue();
                                    isFound = true;
                                }
                            }
                            if (!isFound)
                            {
                                edgesToAdd.add(new ThreeWayCorrelationEdge(twtg, other.getTraitGroup(), 1.0));
                            }
                        }
                    }
                    else
                    {
                        boolean isFound = false;
                        for (ThreeWayCorrelationEdge twce : edgesToAdd)
                        {
                            if (twce.getT1() == other || twce.getT2() == other)
                            {
                                twce.addValue();
                                isFound = true;
                            }
                        }
                        if (!isFound)
                        {
                            edgesToAdd.add(new ThreeWayCorrelationEdge(twtg, other, 1.0));
                        }
                    }
                }
            }
        }
        for (ThreeWayCorrelationEdge twce : edgesToAdd)
        {
            g.addEdge(twce, twce.t1, twce.t2);
        }
    }

    /**
     * Creates a list of all edges that we need to add to this new trait in
     * the graph. 
     * @param twgn the trait that we are considering
     * @param edgesToAdd the list of edges that we return.
     */
    public static void getEdgesToAddForTrait(ThreeWayGraphNode twgn, ArrayList<ThreeWayCorrelationEdge> edgesToAdd,
            boolean isShowNodesWithNoAssoc, double traitEdgeThreshold, GeneTraitNetworkView gtnv)
    {
        for (ThreeWayCorrelationEdge twcn : gtnv.getTraitEdges())
        {
            if (Math.abs(twcn.weight) < traitEdgeThreshold)
            {
                continue;
            }
            ThreeWayTraitNode other = null;
            if (twcn.getT1() == twgn)
            {
                other = (ThreeWayTraitNode) twcn.getT2();
            }
            else if (twcn.getT2() == twgn)
            {
                other = (ThreeWayTraitNode) twcn.getT1();
            }
            if (other != null && (other.hasAssociation() || isShowNodesWithNoAssoc) && other.getIsVisible())
            {
                if (other.getTraitGroup() != null)
                {
                    boolean isFound = false;
                    for (ThreeWayCorrelationEdge twce : edgesToAdd)
                    {
                        if ((twce.getT1() == other.getTraitGroup() && twce.getT2() == twgn) ||
                                (twce.getT2() == other.getTraitGroup() && twce.getT1() == twgn))
                        {
                            twce.addValue();
                            isFound = true;
                        }
                    }
                    if (!isFound)
                    {
                        edgesToAdd.add(new ThreeWayCorrelationEdge(twgn, other.getTraitGroup(), 1.0));
                    }
                }
                else
                {
                    edgesToAdd.add(twcn);
                }
            }
        }
    }

    /**
     * Finds all the edges to add for this gene and returns them to be added
     * to the graph
     * @param twgn the gene that we are considering
     * @param edgesToAdd the list of edges that we return
     */
    public static void getEdgesToAddForGene(ThreeWayGraphNode twgn, ArrayList<ThreeWayCorrelationEdge> edgesToAdd,
            GeneTraitNetworkView gtnv, double geneEdgeThreshold, boolean isShowNodesWithNoAssoc)
    {
        for (ThreeWayCorrelationEdge twcn : gtnv.getGeneEdges())
        {
            if (Math.abs(twcn.weight) < geneEdgeThreshold)
            {
                continue;
            }
            ThreeWayGeneNode other = null;
            if (twcn.getT1() == twgn)
            {
                other = (ThreeWayGeneNode) twcn.getT2();
            }
            else if (twcn.getT2() == twgn)
            {
                other = (ThreeWayGeneNode) twcn.getT1();
            }
            if (other != null && (other.hasAssociation() || isShowNodesWithNoAssoc) && other.getIsVisible())
            {
                if (!other.getGroup().isExpanded())
                {
                    boolean isFound = false;
                    for (ThreeWayCorrelationEdge twce : edgesToAdd)
                    {
                        if ((twce.getT1() == other.getGroup() && twce.getT2() == twgn) ||
                                (twce.getT2() == other.getGroup() && twce.getT1() == twgn))
                        {
                            twce.addValue();
                            isFound = true;
                        }
                    }
                    if (!isFound)
                    {
                        edgesToAdd.add(new ThreeWayCorrelationEdge(twgn, other.getGroup(), 1.0));
                    }
                }
                else
                {
                    edgesToAdd.add(twcn);
                }
            }
        }
    }

    /**
     * Returns all the edges that we want to have in this gene group
     * @param twggn the new gene group to look for edges for
     * @param edgesToAdd the edges that we will add.
     */
    public static void getEdgesToAddForGeneGroup(ThreeWayGeneGroupNode twggn, ArrayList<ThreeWayCorrelationEdge> edgesToAdd,
            GeneTraitNetworkView gtnv, double geneEdgeThreshold, boolean isShowNodesWithNoAssoc)
    {
        for (ThreeWayCorrelationEdge twcn : gtnv.getGeneEdges())
        {
            if (Math.abs(twcn.weight) < geneEdgeThreshold)
            {
                continue;
            }
            ThreeWayGeneNode other = null;
            if (((ThreeWayGeneNode) twcn.getT1()).getGroup() == twggn)
            {
                other = (ThreeWayGeneNode) twcn.getT2();
            }
            else if (((ThreeWayGeneNode) twcn.getT2()).getGroup() == twggn)
            {
                other = (ThreeWayGeneNode) twcn.getT1();
            }
            if (other != null && (other.hasAssociation() || isShowNodesWithNoAssoc) && other.getIsVisible())
            {
                if (!other.getGroup().isExpanded())
                {
                    boolean isFound = false;
                    for (ThreeWayCorrelationEdge twce : edgesToAdd)
                    {
                        if ((twce.getT1() == other.getGroup() && twce.getT2() == twggn) ||
                                (twce.getT2() == other.getGroup() && twce.getT1() == twggn))
                        {
                            twce.addValue();
                            isFound = true;
                        }
                    }
                    if (!isFound)
                    {
                        if (other.getGroup() != twggn)
                        {
                            edgesToAdd.add(new ThreeWayCorrelationEdge(twggn, other.getGroup(), 1.0));
                        }
                    }
                }
                else
                {
                    boolean isFound = false;
                    for (ThreeWayCorrelationEdge twce : edgesToAdd)
                    {
                        if (twce.getT1() == other || twce.getT2() == other)
                        {
                            twce.addValue();
                            isFound = true;
                        }
                    }
                    if (!isFound)
                    {
                        edgesToAdd.add(new ThreeWayCorrelationEdge(twggn, other, 1.0));
                    }
                }
            }
        }
    }

    /**
     * Finds all the association edges that we should add for this node
     * @param edgesToAdd the list of edges that we will add
     * @param node the node for which we look for edges
     */
    public static void addAssociationEdges(ArrayList<ThreeWayAssociationEdge> edgesToAdd, ThreeWayGraphNode node,
            boolean isShowAssociationEdges, GeneTraitNetworkView gtnv, double assocThreshold)
    {
        if (!isShowAssociationEdges)
        {
            return;
        }
        for (ThreeWayAssociationEdge twae : gtnv.getAssocs())
        {
            if(Math.abs(twae.weight) < assocThreshold)
            {
                continue;
            }
            if (node instanceof ThreeWayTraitGraphObject)
            {
                if (node instanceof ThreeWayTraitNode)
                {
                    if (twae.getT2() == node)
                    {
                        node.setHasAssoc(true);
                        if (!((ThreeWayGeneNode) twae.getT1()).getGroup().isExpanded())
                        {
                            boolean isFound = false;
                            for (ThreeWayAssociationEdge twce : edgesToAdd)
                            {
                                if (twce.getT1() == ((ThreeWayGeneNode) twae.getT1()).getGroup() &&
                                        twce.getT2() == node)
                                {
                                    twce.addValue();
                                    isFound = true;
                                }
                            }
                            if (!isFound)
                            {
                                edgesToAdd.add(new ThreeWayAssociationEdge(((ThreeWayGeneNode) twae.getT1()).getGroup(), node, 1.0));
                            }
                        }
                        else
                        {
                            edgesToAdd.add(twae);
                        }
                    }
                }
                else
                {
                    if (((ThreeWayTraitNode) twae.getT2()).getTraitGroup() == node)
                    {
                        node.setHasAssoc(true);
                        if (!((ThreeWayGeneNode) twae.getT1()).getGroup().isExpanded())
                        {
                            boolean isFound = false;
                            for (ThreeWayAssociationEdge twce : edgesToAdd)
                            {
                                if (twce.getT1() == ((ThreeWayGeneNode) twae.getT1()).getGroup())
                                {
                                    twce.addValue();
                                    isFound = true;
                                }
                            }
                            if (!isFound)
                            {
                                edgesToAdd.add(new ThreeWayAssociationEdge(((ThreeWayGeneNode) twae.getT1()).getGroup(), node, 1.0));
                            }
                        }
                        else
                        {
                            boolean isFound = false;
                            for (ThreeWayAssociationEdge twce : edgesToAdd)
                            {
                                if (twce.getT1() == twae.getT1())
                                {
                                    twce.addValue();
                                    isFound = true;
                                }
                            }
                            if (!isFound)
                            {
                                edgesToAdd.add(new ThreeWayAssociationEdge(twae.getT1(), node, 1.0));
                            }
                        }
                    }
                }
            }
            else
            {
                if (node instanceof ThreeWayGeneNode)
                {
                    if (twae.getT1() == node)
                    {
                        node.setHasAssoc(true);
                        if (((ThreeWayTraitNode) twae.getT2()).getTraitGroup() != null)
                        {
                            boolean isFound = false;
                            for (ThreeWayAssociationEdge twce : edgesToAdd)
                            {
                                if (twce.getT2() == ((ThreeWayTraitNode) twae.getT2()).getTraitGroup() &&
                                        twce.getT1() == node)
                                {
                                    twce.addValue();
                                    isFound = true;
                                }
                            }
                            if (!isFound)
                            {
                                edgesToAdd.add(new ThreeWayAssociationEdge(node, ((ThreeWayTraitNode) twae.getT2()).getTraitGroup(), 1.0));
                            }
                        }
                        else
                        {
                            edgesToAdd.add(twae);
                        }
                    }
                }
                else
                {
                    if (((ThreeWayGeneNode) twae.getT1()).getGroup() == node)
                    {
                        node.setHasAssoc(true);
                        if (((ThreeWayTraitNode) twae.getT2()).getTraitGroup() != null)
                        {
                            boolean isFound = false;
                            for (ThreeWayAssociationEdge twce : edgesToAdd)
                            {
                                if (twce.getT2() == ((ThreeWayTraitNode) twae.getT2()).getTraitGroup())
                                {
                                    twce.addValue();
                                    isFound = true;
                                }
                            }
                            if (!isFound)
                            {
                                edgesToAdd.add(new ThreeWayAssociationEdge(node, ((ThreeWayTraitNode) twae.getT2()).getTraitGroup(), 1.0));
                            }
                        }
                        else
                        {
                            boolean isFound = false;
                            for (ThreeWayAssociationEdge twce : edgesToAdd)
                            {
                                if (twce.getT2() == twae.getT2())
                                {
                                    twce.addValue();
                                    isFound = true;
                                }
                            }
                            if (!isFound)
                            {
                                edgesToAdd.add(new ThreeWayAssociationEdge(node, twae.getT2(), 1.0));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * finds all of the association edges given the graph's state at this time.
     * @param edgesToAdd the list of edges that we will add
     */
    public static void addAssociationForAllNodes(ArrayList<ThreeWayAssociationEdge> edgesToAdd,
            boolean isShowAssociationEdges, GeneTraitNetworkView gtnv)
    {
        if (!isShowAssociationEdges)
        {
            return;
        }
        for (ThreeWayAssociationEdge twae : gtnv.getAssocs())
        {
            ThreeWayGeneNode gene = (ThreeWayGeneNode) twae.getT1();
            ThreeWayTraitNode trait = (ThreeWayTraitNode) twae.getT2();

            if (trait.getTraitGroup() == null && gene.getGroup().isExpanded())
            {
                edgesToAdd.add(twae);
            }
            else if (trait.getTraitGroup() == null)
            {
                boolean isFound = false;
                for (ThreeWayAssociationEdge twce : edgesToAdd)
                {
                    if (twce.getT2() == trait &&
                            twce.getT1() == gene.getGroup())
                    {
                        twce.addValue();
                        isFound = true;
                    }
                }
                if (!isFound)
                {
                    edgesToAdd.add(new ThreeWayAssociationEdge(
                            gene.getGroup(), (twae.getT2()), 1.0));
                }
            }
            else if (gene.getGroup().isExpanded())
            {
                boolean isFound = false;
                for (ThreeWayAssociationEdge twce : edgesToAdd)
                {
                    if (twce.getT2() == trait.getTraitGroup() &&
                            twce.getT1() == gene)
                    {
                        twce.addValue();
                        isFound = true;
                    }
                }
                if (!isFound)
                {
                    edgesToAdd.add(new ThreeWayAssociationEdge(
                            twae.getT1(), ((ThreeWayTraitNode) twae.getT2()).getTraitGroup(), 1.0));
                }
            }
            else
            {
                boolean isFound = false;
                for (ThreeWayAssociationEdge twce : edgesToAdd)
                {
                    if (twce.getT2() == trait.getTraitGroup() &&
                            twce.getT1() == gene.getGroup())
                    {
                        twce.addValue();
                        isFound = true;
                    }
                }
                if (!isFound)
                {
                    edgesToAdd.add(new ThreeWayAssociationEdge(
                            gene.getGroup(), trait.getTraitGroup(), 1.0));
                }
            }
        }
    }

    /**
     * Adds or removes gene edges based on the new threshold and the old threshold
     */
    public static void dealWithGeneEdgeThreshChange(double newThresh, Graph<ThreeWayGraphNode, ThreeWayGraphEdge> g,
            GeneTraitNetworkView gtnv, double geneEdgeThreshold, boolean isShowNodesWithNoAssoc)
    {
        if (newThresh > geneEdgeThreshold)
        {
            for (ThreeWayCorrelationEdge twge : gtnv.getGeneEdges())
            {
                double weight = Math.abs(twge.weight);
                if (weight > geneEdgeThreshold && weight < newThresh)
                {
                    ThreeWayGeneNode t1 = (ThreeWayGeneNode) twge.getT1();
                    ThreeWayGeneNode t2 = (ThreeWayGeneNode) twge.getT2();
                    ThreeWayGeneGraphObject o1 = null;
                    ThreeWayGeneGraphObject o2 = null;
                    if (t1.getGroup().isExpanded() && t2.getGroup().isExpanded())
                    {
                        g.removeEdge(twge);
                    }
                    else if (t1.getGroup().isExpanded())
                    {
                        o1 = t1;
                        o2 = t2.getGroup();
                    }
                    else if (t2.getGroup().isExpanded())
                    {
                        o1 = t2;
                        o2 = t1.getGroup();
                    }
                    else
                    {
                        if (t1.getGroup() != t2.getGroup())
                        {
                            o1 = t1.getGroup();
                            o2 = t2.getGroup();
                        }
                    }
                    if (o1 != null && o2 != null)
                    {
                        ThreeWayGraphEdge badEdge = null;
                        for (ThreeWayGraphEdge candidate : g.getEdges())
                        {
                            if ((candidate.getT1() == o1 && candidate.getT2() == o2) || (candidate.getT2() == o1 && candidate.getT1() == o2))
                            {
                                badEdge = candidate;
                                break;
                            }
                        }
                        if (badEdge != null)
                        {
                            badEdge.decreaseValue();
                            if (badEdge.weight == 0)
                            {
                                g.removeEdge(badEdge);
                            }
                        }
                    }
                }
            }
        }
        else
        {
            for (ThreeWayCorrelationEdge twge : gtnv.getGeneEdges())
            {
                double weight = Math.abs(twge.weight);
                if (weight < geneEdgeThreshold && weight > newThresh)
                {
                    ThreeWayGeneNode t1 = (ThreeWayGeneNode) twge.getT1();
                    ThreeWayGeneNode t2 = (ThreeWayGeneNode) twge.getT2();
                    ThreeWayGeneGraphObject o1 = null;
                    ThreeWayGeneGraphObject o2 = null;
                    if (t1.getGroup().isExpanded() && t2.getGroup().isExpanded())
                    {
                        if (twge.getT1().getIsVisible() && twge.getT2().getIsVisible() &&
                                (isShowNodesWithNoAssoc || (twge.getT1().hasAssociation() && twge.getT2().hasAssociation())))
                        {
                            g.addEdge(twge, twge.getT1(), twge.getT2());
                        }
                    }
                    else if (t1.getGroup().isExpanded())
                    {
                        o1 = t1;
                        o2 = t2.getGroup();
                    }
                    else if (t2.getGroup().isExpanded())
                    {
                        o1 = t2;
                        o2 = t1.getGroup();
                    }
                    else
                    {
                        if (t1.getGroup() != t2.getGroup())
                        {
                            o1 = t1.getGroup();
                            o2 = t2.getGroup();
                        }
                    }
                    if (o1 != null && o2 != null)
                    {
                        ThreeWayGraphEdge badEdge = null;
                        for (ThreeWayGraphEdge candidate : g.getEdges())
                        {
                            if ((candidate.getT1() == o1 && candidate.getT2() == o2) || (candidate.getT2() == o1 && candidate.getT1() == o2))
                            {
                                badEdge = candidate;
                                break;
                            }
                        }
                        if (badEdge != null)
                        {
                            badEdge.addValue();
                        }
                        else
                        {
                            ThreeWayCorrelationEdge ed = new ThreeWayCorrelationEdge(o1, o2, 1.0);
                            if (twge.getT1().getIsVisible() && twge.getT2().getIsVisible() &&
                                    (isShowNodesWithNoAssoc || (ed.getT1().hasAssociation() && ed.getT2().hasAssociation())))
                            {
                                g.addEdge(ed, ed.getT1(), ed.getT2());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Adds or removes edges from the graph. Does not affect visible nodes - that 
     * will have to be considered later. 
     */
    public static void dealWithAssocEdgeThreshChange(double newThresh, Graph<ThreeWayGraphNode, ThreeWayGraphEdge> g, GeneTraitNetworkView gtnv, double assocEdgeThreshold, boolean showNodesWithNoAssoc)
    {
        if (newThresh > assocEdgeThreshold)
        {
            for (ThreeWayAssociationEdge twge : gtnv.getAssocs())
            {
                double weight = Math.abs(twge.weight);
                if (weight > assocEdgeThreshold && weight < newThresh)
                {
                    ThreeWayGeneNode t1 = (ThreeWayGeneNode) twge.getT1();
                    ThreeWayTraitNode t2 = (ThreeWayTraitNode) twge.getT2();
                    ThreeWayGeneGraphObject o1 = null;
                    ThreeWayTraitGraphObject o2 = null;
                    if (t1.getGroup().isExpanded() && t2.getTraitGroup() == null)
                    {
                        g.removeEdge(twge);
                    }
                    else if (t1.getGroup().isExpanded())
                    {
                        o1 = t1;
                        o2 = t2.getTraitGroup();
                    }
                    else if (t2.getTraitGroup() == null)
                    {
                        o1 = t1.getGroup();
                        o2 = t2;
                    }
                    else
                    {
                        o1 = t1.getGroup();
                        o2 = t2.getTraitGroup();
                    }
                    if (o1 != null && o2 != null)
                    {
                        ThreeWayGraphEdge badEdge = null;
                        for (ThreeWayGraphEdge candidate : g.getEdges())
                        {
                            if ((candidate.getT1() == o1 && candidate.getT2() == o2) || (candidate.getT2() == o1 && candidate.getT1() == o2))
                            {
                                badEdge = candidate;
                                break;
                            }
                        }
                        if (badEdge != null)
                        {
                            badEdge.decreaseValue();
                            if (badEdge.weight == 0)
                            {
                                g.removeEdge(badEdge);
                            }
                        }
                    }
                }
            }
        }
        else
        {
            for (ThreeWayAssociationEdge twge : gtnv.getAssocs())
            {
                double weight = Math.abs(twge.weight);
                if (weight < assocEdgeThreshold && weight > newThresh)
                {
                    ThreeWayGeneNode t1 = (ThreeWayGeneNode) twge.getT1();
                    ThreeWayTraitNode t2 = (ThreeWayTraitNode) twge.getT2();
                    ThreeWayGeneGraphObject o1 = null;
                    ThreeWayTraitGraphObject o2 = null;
                    if (t1.getGroup().isExpanded() && t2.getTraitGroup() == null)
                    {
                        if (!g.containsVertex(t2))
                        {
                            g.addVertex(t2);
                        }
                        if (!g.containsVertex(t1))
                        {
                            g.addVertex(t1);
                        }
                        g.addEdge(twge, twge.getT1(), twge.getT2());
                    }
                    else if (t1.getGroup().isExpanded())
                    {
                        o1 = t1;
                        o2 = t2.getTraitGroup();
                    }
                    else if (t2.getTraitGroup() == null)
                    {
                        o2 = t2;
                        o1 = t1.getGroup();
                    }
                    else
                    {
                        o1 = t1.getGroup();
                        o2 = t2.getTraitGroup();
                    }
                    if (o1 != null && o2 != null)
                    {
                        ThreeWayGraphEdge badEdge = null;
                        for (ThreeWayGraphEdge candidate : g.getEdges())
                        {
                            if ((candidate.getT1() == o1 && candidate.getT2() == o2) || (candidate.getT2() == o1 && candidate.getT1() == o2))
                            {
                                badEdge = candidate;
                                break;
                            }
                        }
                        if (badEdge != null)
                        {
                            badEdge.addValue();
                        }
                        else
                        {
                            if (!g.containsVertex(o2))
                            {
                                g.addVertex(o2);
                            }
                            if (!g.containsVertex(o1))
                            {
                                g.addVertex(o1);
                            }
                            ThreeWayAssociationEdge ed = new ThreeWayAssociationEdge(o1, o2, 1.0);
                            g.addEdge(ed, ed.getT1(), ed.getT2());
                        }
                    }
                }
            }
        }
        updateHasAssociationForGraph(g, gtnv, newThresh);
    }

    private static void updateHasAssociationForGraph(Graph<ThreeWayGraphNode, ThreeWayGraphEdge> g, GeneTraitNetworkView gntv,
            double assocThresh)
    {
        ArrayList<ThreeWayGraphNode> traitGroups = new ArrayList<ThreeWayGraphNode>();
        for (ThreeWayTraitNode node : gntv.getTraits())
        {
            if (node.getTraitGroup() != null && !traitGroups.contains(node.getTraitGroup()))
            {
                traitGroups.add(node.getTraitGroup());
            }
        }
        for (ThreeWayGraphNode node : gntv.getGeneGroups())
        {
            node.setHasAssoc(false);
        }
        for (ThreeWayGraphNode node : traitGroups)
        {
            node.setHasAssoc(false);
        }
        for (ThreeWayGeneNode node : gntv.getGenes())
        {
            boolean hasAssoc = false;
            for (ThreeWayAssociationEdge edge : gntv.getAssocs())
            {
                if (edge.weight > assocThresh &&
                        edge.getT1() == node)
                {
                    hasAssoc = true;
                    node.getGroup().setHasAssoc(true);
                }
            }
            node.setHasAssoc(hasAssoc);
        }
        for (ThreeWayTraitNode node : gntv.getTraits())
        {
            boolean hasAssoc = false;
            for (ThreeWayAssociationEdge edge : gntv.getAssocs())
            {
                if (edge.weight > assocThresh &&
                        edge.getT2() == node)
                {
                    hasAssoc = true;
                    if (node.getTraitGroup() != null)
                    {
                        node.getTraitGroup().setHasAssoc(true);
                    }
                }
            }
            node.setHasAssoc(hasAssoc);
        }
    }

    /**
     * Adds and removes nodes from the graph. In the case of an add, all nodes
     * that were ever removed are added back!
     */
    public static void addOrRemoveNodes(Graph<ThreeWayGraphNode, ThreeWayGraphEdge> g,
            boolean isAddingNodes, GeneTraitNetworkView gtnv, double traitEdgeThreshold, double geneEdgeThreshold,
            boolean isShowNodesWOAssoc, ArrayList<ThreeWayGraphNode> nodesToRemove, boolean isShowAssocEdge,
            double assocThreshold)
    {
        if (!isAddingNodes)
        {
            for (ThreeWayGraphNode twgn : nodesToRemove)
            {
                g.removeVertex(twgn);
                twgn.setVisible(false);
                if (twgn instanceof ThreeWayTraitGroup)
                {
                    for (ThreeWayTraitNode n : ((ThreeWayTraitGroup) twgn).getTraits())
                    {
                        n.setVisible(false);
                    }
                }
            }
        }
        else
        {
            ArrayList<ThreeWayGeneNode> genes = new ArrayList<ThreeWayGeneNode>();
            for (ThreeWayGeneNode twgn : gtnv.getGenes())
            {
                if (!twgn.getIsVisible() && twgn.getGroup().isExpanded() && (twgn.hasAssoc || isShowNodesWOAssoc))
                {
                    g.addVertex(twgn);
                    genes.add(twgn);
                    twgn.setVisible(true);
                }
            }
            ArrayList<ThreeWayGeneGroupNode> groups = new ArrayList<ThreeWayGeneGroupNode>();
            for (ThreeWayGeneGroupNode twggn : gtnv.getGeneGroups())
            {
                if (!twggn.getIsVisible() && (twggn.hasAssoc || isShowNodesWOAssoc) && !twggn.isExpanded())
                {
                    g.addVertex(twggn);
                    groups.add(twggn);
                    twggn.setVisible(true);
                }
            }
            ArrayList<ThreeWayTraitNode> traits = new ArrayList<ThreeWayTraitNode>();
            for (ThreeWayTraitNode twtn : gtnv.getTraits())
            {
                if (!twtn.getIsVisible() && (twtn.hasAssoc || isShowNodesWOAssoc))
                {
                    g.addVertex(twtn);
                    traits.add(twtn);
                    twtn.setVisible(true);
                }
            }
            ArrayList<ThreeWayCorrelationEdge> edgesToAdd = new ArrayList<ThreeWayCorrelationEdge>();
            ArrayList<ThreeWayAssociationEdge> edgesToAdd2 = new ArrayList<ThreeWayAssociationEdge>();

            for (ThreeWayGeneGroupNode twggn : groups)
            {
                ThreeWayEdgeManager.getEdgesToAddForGeneGroup(twggn, edgesToAdd, gtnv, geneEdgeThreshold, isShowNodesWOAssoc);
                ThreeWayEdgeManager.addAssociationEdges(edgesToAdd2, twggn, isShowAssocEdge, gtnv, assocThreshold);
            }
            for (ThreeWayGeneNode twgn : genes)
            {
                ThreeWayEdgeManager.getEdgesToAddForGene(twgn, edgesToAdd, gtnv, geneEdgeThreshold, isShowNodesWOAssoc);
                ThreeWayEdgeManager.addAssociationEdges(edgesToAdd2, twgn, isShowAssocEdge, gtnv, assocThreshold);
            }
            for (ThreeWayTraitNode twtn : traits)
            {
                ThreeWayEdgeManager.getEdgesToAddForTrait(twtn, edgesToAdd, isShowNodesWOAssoc, traitEdgeThreshold, gtnv);
                ThreeWayEdgeManager.addAssociationEdges(edgesToAdd2, twtn, isShowAssocEdge, gtnv, assocThreshold);
                twtn.setTraitGroup(null);
            }
            for (ThreeWayGraphEdge twge : edgesToAdd)
            {
                if (g.containsVertex(twge.getT1()) && g.containsVertex(twge.getT2()))
                {
                    g.addEdge(twge, twge.getT1(), twge.getT2());

                }
            }
            for (ThreeWayGraphEdge twge : edgesToAdd2)
            {
                if (g.containsVertex(twge.getT1()) && g.containsVertex(twge.getT2()))
                {
                    g.addEdge(twge, twge.getT1(), twge.getT2());
                }
            }
        }
        ThreeWayEdgeManager.setMaxValsAcrossEdgeGroups(g);
        gtnv.setUpVisualizationRenderingStuff(g);
    }
}
