package datamodel;

import java.io.Serializable;
import java.util.Observable;

/**
 * The user has a lot of control over how they visualize the three way association
 * objects. We want to put this into the data model so that it can be serialized
 * and stored. In order to do this, we use an observer pattern where changes
 * to this data object reflect changes in the GUI display as well as in the GUI
 * controller.
 *
 * @author rcurtis
 */
public class ThreeWayVisualizationSettings extends Observable implements Serializable
{
    /**
     * The user has two options for layout - KK layout and Force-directed
     * layout. This variable controls what is seen here.
     */
    private boolean isKKLayout = true;

    /**
     * The user has two options for layout - KK layout and Force-directed
     * layout. This variable controls what is seen here.
     */
    public boolean getIsKKLayout()
    {
        return this.isKKLayout;
    }

    /**
     * Sets the layout to KK layout
     */
    public void setToKKLayout()
    {
        if (!this.isKKLayout)
        {
            this.isKKLayout = true;
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Sets the layout to a force-directed layout.
     */
    public void setToFDLayout()
    {
        if (this.isKKLayout)
        {
            this.isKKLayout = false;
            setChanged();
            notifyObservers();
        }
    }
    /**
     * The number of iterations that need to be completed for the layout to
     * settle into an appropriate layout.
     */
    private int numberLayoutUpdateIterations = 1000;

    /**
     * The number of iterations that need to be completed for the layout to
     * settle into an appropriate layout.
     */
    public int getNumberLayoutUpdateIterations()
    {
        return this.numberLayoutUpdateIterations;
    }

    /**
     * The number of iterations that need to be completed for the layout to
     * settle into an appropriate layout.
     */
    public void setNumberLayoutUpdateIterations(int i)
    {
        if (this.numberLayoutUpdateIterations != i)
        {
            this.numberLayoutUpdateIterations = i;
            setChanged();
            notifyObservers();
        }
    }
    /**
     * The stretch parameter for the spring layout.
     */
    private double springStretch = 0.80;

    /**
     * The stretch parameter for the spring layout.
     */
    public double getSpringLayoutStretch()
    {
        return this.springStretch;
    }

    /**
     * The stretch parameter for the spring layout.
     */
    public void setSpringLayoutStretch(double d)
    {
        if (this.springStretch != d)
        {
            this.springStretch = d;
            setChanged();
            notifyObservers();
        }
    }
    /**
     * SpringLayout - the repulsion distance between nodes
     */
    private int repulsion_range_sq = 60;

    /**
     * SpringLayout - the repulsion distance between nodes
     */
    public int getSpringLayoutRepulsionRange()
    {
        return this.repulsion_range_sq;
    }

    /**
     * SpringLayout - the repulsion distance between nodes
     */
    public void setSpringLayoutRepulsionRange(int i)
    {
        if (this.repulsion_range_sq != i)
        {
            this.repulsion_range_sq = i;
            setChanged();
            notifyObservers();
        }
    }
    /**
     * SpringLayout - the attraction force between correlated traits and genes
     */
    private double correlation_force_multiplier = 12.0 / 3.0;

    /**
     * SpringLayout - the attraction force between correlated traits and genes
     */
    public double getSpringLayoutCorrelationForceMultiplier()
    {
        return this.correlation_force_multiplier;
    }

    /**
     * SpringLayout - the attraction force between correlated traits and genes
     */
    public void setSpringLayoutCorrelationForceMultiplier(double d)
    {
        if (this.correlation_force_multiplier != d)
        {
            this.correlation_force_multiplier = d;
            setChanged();
            notifyObservers();
        }
    }
    /**
     * In the Spring Layout - the force by association edges.
     */
    private double association_force_multiplier = 4.0 / 3.0;

    /**
     * In the Spring Layout - the force by association edges.
     */
    public double getSpringLayoutAssociationForceMultiplier()
    {
        return this.association_force_multiplier;
    }

    /**
     * In the Spring Layout - the force by association edges.
     */
    public void setSpringLayoutAssociationForceMultiplier(double d)
    {
        if (this.association_force_multiplier != d)
        {
            this.association_force_multiplier = d;
            setChanged();
            notifyObservers();
        }
    }
    /**
     * SpringLayout - The repulsion factor of how hard genes repulse eachother
     */
    private double gene_repulsion_factor = 50;

    /**
     * SpringLayout - The repulsion factor of how hard genes repulse eachother
     */
    public double getSpringLayoutGeneRepulsionFactor()
    {
        return this.gene_repulsion_factor;
    }

    /**
     * SpringLayout - The repulsion factor of how hard genes repulse eachother
     */
    public void setSpringLayoutGeneRepulsionFactor(double d)
    {
        if (this.gene_repulsion_factor != d)
        {
            this.gene_repulsion_factor = d;
            setChanged();
            notifyObservers();
        }
    }
    /**
     * SpringLayout - How hard traits repulse each other.
     */
    private double trait_repulsion_factor = 2;

    /**
     * SpringLayout - How hard traits repulse each other.
     */
    public double getSpringLayoutTraitRepulsionFactor()
    {
        return this.trait_repulsion_factor;
    }

    /**
     * SpringLayout - How hard traits repulse each other.
     */
    public void setSpringLayoutTraitRepulsionFactor(double d)
    {
        if (this.trait_repulsion_factor != d)
        {
            this.trait_repulsion_factor = d;
            setChanged();
            notifyObservers();
        }
    }
    /**
     * KKLayout - A multiplicative factor which partly specifies the "preferred" length of an edge (L).
     */
    private double length_factor = 0.9;

    /**
     * KKLayout - A multiplicative factor which partly specifies the "preferred" length of an edge (L).
     */
    public double getKKLayoutLengthFactor()
    {
        return this.length_factor;
    }

    /**
     * KKLayout - A multiplicative factor which partly specifies the "preferred" length of an edge (L).
     */
    public void setKKLayoutLengthFactor(double d)
    {
        if (d != this.length_factor)
        {
            this.length_factor = d;
            this.setChanged();
            this.notifyObservers();
        }
    }
    /**
     * KKLayout - A multiplicative factor which specifies the fraction of the graph's diameter to be
     * used as the inter-vertex distance between disconnected vertices.
     */
    private double disconnected_multiplier = 0.5;

    /**
     * KKLayout - A multiplicative factor which specifies the fraction of the graph's diameter to be
     * used as the inter-vertex distance between disconnected vertices.
     */
    public double getKKLayoutDisconnectedMultiplier()
    {
        return this.disconnected_multiplier;
    }

    /**
     * KKLayout - A multiplicative factor which specifies the fraction of the graph's diameter to be
     * used as the inter-vertex distance between disconnected vertices.
     */
    public void setKKLayoutDisconnectedMultiplier(double d)
    {
        if (this.disconnected_multiplier != d)
        {
            this.disconnected_multiplier = d;
            setChanged();
            notifyObservers();
        }
    }
    /**
     * A boolean indicate whether or not to show the association edges in the display
     * of the graph
     */
    private boolean isShowAssociationEdges = true;

    /**
     * A boolean indicate whether or not to show the association edges in the display
     * of the graph
     */
    public boolean getIsShowAssociationEdges()
    {
        return this.isShowAssociationEdges;
    }

    /**
     * A boolean indicate whether or not to show the association edges in the display
     * of the graph
     */
    public void setIsShowAssociationEdges(boolean b)
    {
        if (this.isShowAssociationEdges != b)
        {
            isShowAssociationEdges = b;
            setChanged();
            notifyObservers();
        }
    }
    /**
     * A boolean representing whether or not the display on the screen
     * should show nodes that do not have any associations to other nodes
     */
    private boolean isShowUnconnectedNodes = true;

    /**
     * A boolean representing whether or not the display on the screen
     * should show nodes that do not have any associations to other nodes
     */
    public boolean getIsShowUnconnectedNodes()
    {
        return this.isShowUnconnectedNodes;
    }

    /**
     * A boolean representing whether or not the display on the screen
     * should show nodes that do not have any associations to other nodes
     */
    public void setIsShowUnconnectedNodes(boolean b)
    {
        if (this.isShowUnconnectedNodes != b)
        {
            isShowUnconnectedNodes = b;
            setChanged();
            notifyObservers();
        }
    }
    /**
     * The threshold that limits the number of trait edges on the screen.
     */
    private double traitEdgeThreshold = 0.0;
    /**
     * The threshold that limits the number of gene edges on the screen.
     */
    private double geneEdgeThreshold = 0.0;
    /**
     * The threshold thatl imits the number of assoc edges on the screen.
     */
    private double assocEdgeThreshold = 0.0;

    /**
     * The threshold that limits the number of trait edges on the screen.
     */
    public double getTraitEdgeThreshold()
    {
        return this.traitEdgeThreshold;
    }

    /**
     * The threshold that limits the number of gene edges on the screen.
     */
    public double getGeneEdgeThreshold()
    {
        return this.geneEdgeThreshold;
    }

    /**
     * The threshold thatl imits the number of assoc edges on the screen.
     */
    public double getAssocEdgeThreshold()
    {
        return this.assocEdgeThreshold;
    }

    /**
     * The threshold that limits the number of trait edges on the screen.
     */
    public void setTraitEdgeThreshold(double d)
    {
        if (this.traitEdgeThreshold != d)
        {
            this.traitEdgeThreshold = d;
            setChanged();
            notifyObservers();
        }
    }

    /**
     * The threshold that limits the number of gene edges on the screen.
     */
    public void setGeneEdgeThreshold(double d)
    {
        if (this.geneEdgeThreshold != d)
        {
            this.geneEdgeThreshold = d;
            setChanged();
            notifyObservers();
        }
    }

    /**
     * The threshold thatl imits the number of assoc edges on the screen.
     */
    public void setAssocEdgeThreshold(double d)
    {
        if (this.assocEdgeThreshold != d)
        {
            this.assocEdgeThreshold = d;
            setChanged();
            notifyObservers();
        }
    }
    /**
     * Whether or not the visualization should show labels for the traits
     */
    private boolean isShowTraitLabs = true;
    /**
     * Whether or not the visualization should show labels for the genes
     */
    private boolean isShowGeneLabs = true;
    /**
     * Whether or not the visualization should show labels for the gene-gene edges
     */
    private boolean isShowGeneEdgeLabs = false;
    /**
     * Whether or not the visualization should show labels for the trait-trait edges
     */
    private boolean isShowTraitEdgeLabs = false;
    /**
     * Whether or not the visualization should show labels for the association edges
     */
    private boolean isShowAssocEdgeLabs = false;

    /**
     * Whether or not the visualization should show labels for the traits
     */
    public boolean getIsShowTraitLabs()
    {
        return this.isShowTraitLabs;
    }

    /**
     * Whether or not the visualization should show labels for the genes
     */
    public boolean getIsShowGeneLabs()
    {
        return this.isShowGeneLabs;
    }

    /**
     * Whether or not the visualization should show labels for the gene-gene edges
     */
    public boolean getIsShowGeneEdgeLabs()
    {
        return this.isShowGeneEdgeLabs;
    }

    /**
     * Whether or not the visualization should show labels for the trait-trait edges
     */
    public boolean getIsShowTraitEdgeLabs()
    {
        return this.isShowTraitEdgeLabs;
    }

    /**
     * Whether or not the visualization should show labels for the association edges
     */
    public boolean getIsShowAssocEdgeLabs()
    {
        return this.isShowAssocEdgeLabs;
    }

    /**
     * Whether or not the visualization should show labels for the traits
     */
    public void setIsShowTraitLabs(boolean b)
    {
        if (this.isShowTraitLabs != b)
        {
            this.isShowTraitLabs = b;
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Whether or not the visualization should show labels for the genes
     */
    public void setIsShowGeneLabs(boolean b)
    {
        if (this.isShowGeneLabs != b)
        {
            this.isShowGeneLabs = b;
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Whether or not the visualization should show labels for the gene-gene edges
     */
    public void setIsShowGeneEdgeLabs(boolean b)
    {
        if (this.isShowGeneEdgeLabs != b)
        {
            this.isShowGeneEdgeLabs = b;
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Whether or not the visualization should show labels for the trait-trait edges
     */
    public void setIsShowTraitEdgeLabs(boolean b)
    {
        if (this.isShowTraitEdgeLabs != b)
        {
            this.isShowTraitEdgeLabs = b;
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Whether or not the visualization should show labels for the association edges
     */
    public void setIsShowAssocEdgeLabs(boolean b)
    {
        if (this.isShowAssocEdgeLabs != b)
        {
            this.isShowAssocEdgeLabs = b;
            setChanged();
            notifyObservers();
        }
    }
}

