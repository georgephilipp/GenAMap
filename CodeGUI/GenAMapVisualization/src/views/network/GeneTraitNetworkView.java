package views.network;

import datamodel.Association;
import datamodel.Edge;
import datamodel.GeneGroup;
import datamodel.GeneTraitAssociation;
import datamodel.Marker;
import datamodel.Trait;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import heatchart.HeatChart;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;
import org.apache.commons.collections15.Transformer;
import views.AssociationView;
import views.heatmap.HeatChartScale;
import views.network.threeway.EdgeLevelStroker;
import views.network.threeway.MyThreeWayVertexShapeTransformer;
import views.network.threeway.ThreeWayAssociationEdge;
import views.network.threeway.ThreeWayGraph;
import views.network.threeway.ThreeWayGraphEdge;
import views.network.threeway.ThreeWayGraphNode;
import views.network.threeway.ThreeWayLayout;
import views.network.threeway.ThreeWayTraitNode;
import views.network.threeway.ThreeWayCorrelationEdge;
import views.snp.MyPickableVertexPaintTransformer;
import views.snp.VertexStringerImpl;
import views.network.threeway.ThreeWayEdgeLabelStringer;
import views.network.threeway.ThreeWayEdgeManager;
import views.network.threeway.ThreeWayEdgePaintTransformer;
import views.network.threeway.ThreeWayGeneGroupNode;
import views.network.threeway.ThreeWayGeneNode;
import views.network.threeway.ThreeWayGraphManager;
import views.network.threeway.ThreeWayVisualizationSettingsGUI;
import views.network.threeway.ThreeWayVisualizationSettingsGUI.ImagePanel1;

/**
 * This is the gene trait network object that links genes and traits as
 * described by a three-way association object. 
 *
 * @author rcurtis
 */
public class GeneTraitNetworkView
{
    /**
     * AssociationView - we will still link out so that we can coordinate
     * with the marker view
     */
    private AssociationView associationview;
    /**
     * The object that describes the visualization that we are showing here.
     */
    private GeneTraitAssociation gta;
    /**
     * What is showing the view of the nodes - the JUNG object
     */
    private VisualizationViewer vv;
    /**
     * The layout manager of the vv
     */
    private Layout vvLayout;
    /**
     * A collection of all gene-genegroup edges
     */
    private ArrayList<ThreeWayCorrelationEdge> geneEdges = new ArrayList<ThreeWayCorrelationEdge>();
    /**
     * A collection of all trait-traitgroup edges
     */
    private ArrayList<ThreeWayCorrelationEdge> traitEdges = new ArrayList<ThreeWayCorrelationEdge>();
    /**
     * A collection of all gene-trait edges
     */
    private ArrayList<ThreeWayAssociationEdge> genetraitEdges = new ArrayList<ThreeWayAssociationEdge>();
    /**
     * A collection of all traits
     */
    private ArrayList<ThreeWayTraitNode> traitNodes = new ArrayList<ThreeWayTraitNode>();
    /**
     * A collection of all genes
     */
    private ArrayList<ThreeWayGeneNode> geneNodes = new ArrayList<ThreeWayGeneNode>();
    /**
     * A collection of all genegroups
     */
    private ArrayList<ThreeWayGeneGroupNode> geneGroupNodes = new ArrayList<ThreeWayGeneGroupNode>();
    /**
     * The graph manager manages what edges are visible, etc. This keeps the
     * graph up to date. 
     */
    private ThreeWayGraphManager manager;
    /**
     * The GUI control that interacts with the visualizations in order to
     * provide the user control. 
     */
    private ThreeWayVisualizationSettingsGUI twvsg;

    /**
     * Constructor
     */
    public GeneTraitNetworkView(AssociationView assocview)
    {
        this.associationview = assocview;
    }

    /**
     * Draws the three way association on the canvas ... ready for action!!
     */
    public void drawOnCanvas(JPanel jp, String layoutloc, GeneTraitAssociation gta)
    {
        this.gta = gta;

        geneEdges = new ArrayList<ThreeWayCorrelationEdge>();
        traitEdges = new ArrayList<ThreeWayCorrelationEdge>();
        genetraitEdges = new ArrayList<ThreeWayAssociationEdge>();
        traitNodes = new ArrayList<ThreeWayTraitNode>();
        geneNodes = new ArrayList<ThreeWayGeneNode>();
        geneGroupNodes = new ArrayList<ThreeWayGeneGroupNode>();
        Graph g = buildVizGraph();
        vvLayout = new ThreeWayLayout(g, gta.getVizSetts(), new Dimension(jp.getWidth(), jp.getHeight() - 35));
        vv = new VisualizationViewer<ThreeWayGraphNode, ThreeWayGraphEdge>(vvLayout);

        vv.setBackground(jp.getBackground());

        final AntiScrollGraphMouseListener graphMouse = new AntiScrollGraphMouseListener();
        graphMouse.setMode(Mode.PICKING);
        vv.setGraphMouse(graphMouse);
        setUpVisualizationRenderingStuff(g);

        jp.add(vv, layoutloc);
        ((ThreeWayLayout) vvLayout).init();

        manager = new ThreeWayGraphManager(jp, gta, this, gta.getVizSetts());
        vv.addMouseListener(manager);
        twvsg = new ThreeWayVisualizationSettingsGUI(this.gta.getVizSetts(), gta);
        twvsg.setVisible(
                true);
    }

    /**
     * builds a graph from visualization settings.
     * @return graph as it should be displayed.
     */
    private Graph<? extends Object, ? extends Object> buildVizGraph()
    {
        ThreeWayGraph g1;
        g1 =
                new ThreeWayGraph();
        //add traits to graph
        for (Trait t : gta.getTraitSet().getTraits())
        {
            ThreeWayTraitNode tn = new ThreeWayTraitNode(t);
            g1.addVertex(tn);
            this.traitNodes.add(tn);
        }
        HashMap<Integer, ThreeWayGeneGroupNode> genegroupmap = new HashMap<Integer, ThreeWayGeneGroupNode>();
        //add genes to the graph
        for (GeneGroup g : gta.getGeneGroups())
        {
            ThreeWayGeneGroupNode twggn = new ThreeWayGeneGroupNode(g.getGeneColor(), g.getGenes().size(), g.getGroupName(), g, g.getGroupNo());
            g1.addVertex(twggn);
            this.geneGroupNodes.add(twggn);
            for (Trait t : g.getGenes())
            {
                this.geneNodes.add(new ThreeWayGeneNode(t, twggn));
            }
            genegroupmap.put(g.getGroupNo(), twggn);
        }
//add edges between traits to graph
        ArrayList<Edge> netStruct;
        try
        {
            netStruct = gta.getTraitNetworkStructure();//.getNetworkStructure(null, gta.getTraitSet().getTraits(), "default", true);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            return null;
        }
        for (int i = 0; i <
                netStruct.size(); i++)
        {
            if (java.lang.Math.abs(netStruct.get(i).weight) > 0)//controlPanel.getThresh())
            {
                ThreeWayCorrelationEdge twue = new ThreeWayCorrelationEdge(g1.getVertex(netStruct.get(i).getT1().getName()),
                        g1.getVertex(netStruct.get(i).getT2().getName()), netStruct.get(i).weight);
                if (!g1.containsEdge(twue))
                {
                    g1.addEdge(twue, twue.getT1(), twue.getT2());
                    this.traitEdges.add(twue);
                }
            }
        }

        int[][] adjMat = new int[gta.getGeneGroups().size()][gta.getGeneGroups().size()];

        //add edges between genes to graph
        try
        {
            netStruct = gta.getGeneNetworkStructure();// ().getNetworkStructure(null, gta.getGeneSet().getTraits(), "default", true);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            return null;
        }
        double max = 0.0;

        for (int i = 0; i <
                netStruct.size(); i++)
        {
            if (java.lang.Math.abs(netStruct.get(i).weight) > 0)
            {
                Trait gene1 = netStruct.get(i).getT1();
                Trait gene2 = netStruct.get(i).getT2();

                int ggrp1 = gta.getGroupNoForGene(gene1) - 1;
                int ggrp2 = gta.getGroupNoForGene(gene2) - 1;

                ThreeWayGeneNode n1 = null;
                ThreeWayGeneNode n2 = null;
                for (ThreeWayGeneNode twgn : this.geneNodes)
                {
                    if (twgn.getGene().getId() == gene1.getId())
                    {
                        n1 = twgn;
                    }
                    else if (twgn.getGene().getId() == gene2.getId())
                    {
                        n2 = twgn;
                    }
                }
                if (n1 != null && n2 != null)
                {
                    this.geneEdges.add(new ThreeWayCorrelationEdge(n1, n2, netStruct.get(i).weight));
                }
                if (ggrp1 >= 0 && ggrp2 >= 0 && ggrp1 != ggrp2)
                {
                    adjMat[ggrp1][ggrp2] += 1;
                    adjMat[ggrp2][ggrp1] += 1;
                    if (adjMat[ggrp2][ggrp1] > max)
                    {
                        max = (double) adjMat[ggrp2][ggrp1];
                    }
                }
            }
        }

        max /= 3.0;
        for (int i = 0; i <
                adjMat.length; i++)
        {
            for (int j = i + 1; j <
                    adjMat.length; j++)
            {
                if (adjMat[i][j] > 0)
                {
                    ThreeWayCorrelationEdge twue = new ThreeWayCorrelationEdge(
                            g1.getVertex("Group " + (i + 1)),
                            g1.getVertex("Group " + (j + 1)), ((double) adjMat[i][j]));
                    twue.setMax(max);
                    if (!g1.containsEdge(twue))
                    {
                        g1.addEdge(twue, twue.getT1(), twue.getT2());
                    }
                }
            }
        }
        ThreeWayEdgeManager.initAssocEdges(g1, gta, geneNodes, genetraitEdges);

        return g1;
    }

    /**
     * Updates the size of the visualization viewer to reflect the size of
     * the jpanel
     */
    public void refresh(JPanel jp)
    {
        ((ThreeWayLayout) vvLayout).refreshSize(new Dimension(jp.getWidth(), jp.getHeight() - 35));
        vvLayout.reset();
        vv.setGraphLayout(vvLayout);
        setUpVisualizationRenderingStuff(vv.getGraphLayout().getGraph());
    }

    /**
     * When refresh is called, the visualization view has to have renderers
     * set for all of the graph objects.
     * @param g the graph of objects that the viz viewer is showing.
     */
    public void setUpVisualizationRenderingStuff(Graph g)
    {
        Map<ThreeWayGraphNode, Color> colormap = new HashMap<ThreeWayGraphNode, Color>();
        for (Object gn : g.getVertices())
        {
            colormap.put((ThreeWayGraphNode) gn, ((ThreeWayGraphNode) gn).getNodeColor());
        }
        vv.getRenderContext().setVertexFillPaintTransformer(new MyPickableVertexPaintTransformer<ThreeWayGraphNode>(vv.getPickedVertexState(), colormap, Color.black, Color.cyan));
        vv.getRenderContext().setVertexShapeTransformer(new MyThreeWayVertexShapeTransformer());
        Map<ThreeWayGraphNode, String> map2 = new HashMap<ThreeWayGraphNode, String>();
        for (Object gn : g.getVertices())
        {
            map2.put((ThreeWayGraphNode) gn, ((ThreeWayGraphNode) gn).getNodeLabel());
        }
        Map<ThreeWayGraphNode, String> map3 = new HashMap<ThreeWayGraphNode, String>();
        for (Object gn : g.getVertices())
        {
            map3.put((ThreeWayGraphNode) gn, ((ThreeWayGraphNode) gn).getName());
        }
        final Transformer<ThreeWayGraphNode, String> vertexStringerImpl = new VertexStringerImpl<ThreeWayGraphNode>(map2);
        final Transformer<ThreeWayGraphNode, String> vertexStringerImpl2 = new VertexStringerImpl<ThreeWayGraphNode>(map3);
        ((VertexStringerImpl) vertexStringerImpl2).geneenabled = gta.getVizSetts().getIsShowGeneLabs();
        ((VertexStringerImpl) vertexStringerImpl2).traitenabled = gta.getVizSetts().getIsShowTraitLabs();
        ((VertexStringerImpl) vertexStringerImpl2).setEnabled(false);
        vv.setVertexToolTipTransformer(vertexStringerImpl);
        vv.getRenderContext().setVertexLabelTransformer(vertexStringerImpl2);
        vv.getRenderer().getVertexLabelRenderer().setPosition(Position.E);
        vv.getRenderContext().setEdgeLabelTransformer(new ThreeWayEdgeLabelStringer(true));
        ((ThreeWayEdgeLabelStringer) vv.getRenderContext().getEdgeLabelTransformer()).setAssocEnabeled(gta.getVizSetts().getIsShowAssocEdgeLabs());
        ((ThreeWayEdgeLabelStringer) vv.getRenderContext().getEdgeLabelTransformer()).setGeneEnabled(gta.getVizSetts().getIsShowGeneEdgeLabs());
        ((ThreeWayEdgeLabelStringer) vv.getRenderContext().getEdgeLabelTransformer()).setTraitEnabled(gta.getVizSetts().getIsShowTraitEdgeLabs());
        vv.getRenderContext().setEdgeStrokeTransformer(new EdgeLevelStroker(true));
        vv.getRenderContext().setEdgeDrawPaintTransformer(new ThreeWayEdgePaintTransformer());
    }

    /**
     * Returns the layout object associated with these nodes
     */
    public ThreeWayLayout getTheLayoutObject()
    {
        return (ThreeWayLayout) this.vvLayout;
    }

    /**
     * Returns all trait-trait edges
     */
    public Iterable<ThreeWayCorrelationEdge> getTraitEdges()
    {
        return this.traitEdges;
    }

    /**
     * Returns all gene nodes
     */
    public Iterable<ThreeWayGeneNode> getGenes()
    {
        return this.geneNodes;
    }

    /**
     * Returns all gene edges
     */
    public Iterable<ThreeWayCorrelationEdge> getGeneEdges()
    {
        return this.geneEdges;
    }

    /**
     * Returns all the gene groups in this graph.
     */
    public Iterable<ThreeWayGeneGroupNode> getGeneGroups()
    {
        return this.geneGroupNodes;
    }

    /**
     * Returns all the gene-trait associations.
     * @return
     */
    public Iterable<ThreeWayAssociationEdge> getAssocs()
    {
        return this.genetraitEdges;
    }

    /**
     * Returns a list of all the traits in this dataset
     */
    public Iterable<ThreeWayTraitNode> getTraits()
    {
        return this.traitNodes;
    }

    /**
     * Returns a pointer to the current visualization viewer object
     * @return
     */
    public VisualizationViewer getVV()
    {
        return this.vv;
    }

    /**
     * Returns the association view that is the parent of this object
     * @return
     */
    public AssociationView getAssocView()
    {
        return this.associationview;
    }

    /**
     * In this method, we reset the color map of the genes on screen to reflect their
     * association with the markers given to us. 
     */
    public void markUpByAssociation(ArrayList<Marker> markers, int i)
    {
        ArrayList<ImagePanel1> charts = new ArrayList<ImagePanel1>();
        ArrayList<Integer> markerID = new ArrayList<Integer>();
        for (Marker m : markers)
        {
            markerID.add(m.getId());
        }
        ArrayList<Integer> indxs = new ArrayList<Integer>();

        for (ThreeWayGeneNode n : this.geneNodes)
        {
            indxs.add(n.getGene().getId());
            n.setNodeAsNotAssoc();
            n.getGroup().setNodeAsNotAssoc();
        }
        double maxVal = 0.0;
        Collection<Association> assocs = gta.getSNPAssoc().findAssociations(markerID, indxs, -1);
        for (Association a : assocs)
        {
            Double d = Math.abs(a.getValue());
            if (d > maxVal)
            {
                maxVal = d;
            }
        }
        for (ThreeWayGeneNode n : this.geneNodes)
        {
            Color c = n.getGroup().getNodeColor();
            Color c3 = new Color(230, 255, 255);
            float scaleFactor = 0.4f;
            float colors[] = c.getRGBColorComponents(null);
            Color c2 = new Color(colors[0] * scaleFactor,
                    colors[1] * scaleFactor,
                    colors[2] * scaleFactor);
            boolean found = false;
            for (ImagePanel1 p : charts)
            {
                if (p.c.getRGB() == c.getRGB())
                {
                    found = true;
                    if(!p.label.equals(n.getGroup().getGroupNumber()+"")){
                        p.label = "Other";
                    }
                }
            }
            if (!found)
            {
                ImagePanel1 ip = new ImagePanel1();
                ip.c = c;
                ip.label = n.getGroup().getGroupNumber() + "";
                ip.scaleImage = HeatChartScale.DrawScale(0.0, maxVal + maxVal / 100.0, 100,
                        HeatChart.SCALE_LINEAR, c2, c, 99, c3).getChartImage();
                charts.add(ip);
            }
        }
        for (ThreeWayGeneNode n : this.geneNodes)
        {
            n.setColorScaleFactor(0.4f);
            n.getGroup().setColorScaleFactor(0.4f);
        }
        this.twvsg.showChartKeys(charts);
        maxVal *= 2.0;

        for (Association a : assocs)
        {
            float val = new Double(a.getValue() / maxVal + 0.4).floatValue();
            for (ThreeWayGeneNode n : this.geneNodes)
            {
                if (n.getGene().getId() == a.getTraitId())
                {
                    n.setColorScaleFactor(val);
                    n.getGroup().setColorScaleFactor(val);
                }
            }
        }
        setUpVisualizationRenderingStuff(vv.getGraphLayout().getGraph());
    }
    /**
     * A class of a pair, a double and an int
     */
    public class Pair
    {
        public double d;
        public int i;
        public double v;

        /**
         * Creates a new pair
         * @param d a double
         * @param i an int
         */
        public Pair(double d, int i, double v)
        {
            this.d = d;
            this.i = i;
            this.v = v;
        }
    }
}
