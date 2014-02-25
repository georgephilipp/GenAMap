package datamodel;

import control.GOFrame;
import control.itempanel.DeletionItem;
import control.itempanel.ThreadingItemFrame;
import realdata.DataManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import static javax.swing.WindowConstants.HIDE_ON_CLOSE;

/**
 * This is the database object that represents the 3-way association between
 * the genes and the traits. 
 * @author rcurtis
 */
public class GeneTraitAssociation implements Serializable
{

    /**
     * the id of this gene trait association set
     */
    private int id;
    /**
     * the name of this gene trait association set
     */
    private String name;
    /**
     * The traitset of genes
     */
    private TraitSet genes;
    /**
     * the traitset of traits
     */
    private TraitSet traits;
    /**
     * The network to show for genes
     */
    private Network geneNet;
    /**
     * The network to show for traits
     */
    private Network traitnet;
    /**
     * The associationSet between the genes and the genome
     */
    private AssociationSet snpGeneAssoc;
    /**
     * The gene groups that are shown in this gene trait association object.
     */
    private ArrayList<GeneGroup> genegroups;
    /**
     * We keep track of the genegroup that each trait is in. 
     */
    private HashMap<Integer, Integer> groups;
    /**
     * The gene-trait associations
     */
    private ArrayList<GTAssocData> associations;
    /**
     * The visualization settings that will be read and used to show
     * off this gene-trait association object to the world.
     */
    private ThreeWayVisualizationSettings visSets;
    /**
     * The max weight of a trait-trait edge. 
     */
    private double maxTraitEdgeWeight;
    /**
     * The max weight of a gene-gene edge.
     */
    private double maxGeneEdgeWeight;
    /**
     * The max weight of an association value
     */
    private double maxGTAssocWeight;

    /**
     * Constructor
     */
    public GeneTraitAssociation(int id, String name, int geneid, int traitid,
            int genenetid, int traitnetid, AssociationSet snpassocid, Project p)
    {
        this.id = id;
        this.name = name;
        this.genes = p.getTrait(geneid);
        this.traits = p.getTrait(traitid);
        this.geneNet = genes.getNetwork(genenetid);
        this.traitnet = traits.getNetwork(traitnetid);
        this.snpGeneAssoc = snpassocid;
        visSets = new ThreeWayVisualizationSettings();

        this.genegroups = new ArrayList<GeneGroup>();
        groups = new HashMap<Integer, Integer>();
        ArrayList<String> cols = new ArrayList<String>();
        cols.add("geneid");
        cols.add("grp");
        ArrayList<String> where = new ArrayList<String>();
        where.add("gtassocsetid=" + this.id);
        ArrayList<HashMap<String, String>> res =
                DataManager.runMultiColSelectQuery(cols, "genegroups", true, where, "grp");

        for (HashMap<String, String> r : res)
        {
            groups.put(Integer.parseInt(r.get("geneid")), Integer.parseInt(r.get("grp")));
        }

        /*int ix = 0;
        int oldie = -1;
        for(int i = 0; i < res.size(); i ++)
        {
        HashMap<String, String> r = res.get(i);

        int grp = Integer.parseInt(r.get("grp"));
        if(ix == 0 || oldie != grp)
        {
        oldie = grp;
        ix++;
        }

        groups.put(Integer.parseInt(r.get("geneid")), ix);
        }*/

        HashMap<Integer, GeneGroup> groupmap = new HashMap<Integer, GeneGroup>();

        for (Trait t : genes.getTraits())
        {
            Integer grp = groups.get(t.getId());
            if (grp != null)
            {
                GeneGroup gg = groupmap.get(grp);
                if (gg == null)
                {
                    gg = new GeneGroup(new ArrayList<Trait>(), Model.colors[17], "Group " + grp);
                    groupmap.put(grp, gg);
                    this.genegroups.add(gg);
                }
                gg.addGene(t);
            }
        }

        GeneGroup[] order = new GeneGroup[10];
        for (GeneGroup gg : this.genegroups)
        {
            for (int i = 0; i < 10; i++)
            {
                if (order[i] == null || order[i].getGenes().size() < gg.getGenes().size())
                {
                    for (int j = 9; j > i; j--)
                    {
                        order[j] = order[j - 1];
                    }
                    order[i] = gg;
                    break;
                }
            }
        }

        for (int i = 0; i < 10; i++)
        {
            if (order[i] != null)
            {
                order[i].setColor(Model.colors[i + 3]);
            }
        }
        HashSet reference = new HashSet<String>();
        for (Trait t : genes.getTraits())
        {
            reference.add(t.getName().toUpperCase());
        }
        GOFrame go = null;
        boolean isOK = false;

        for (int i = 0; i < this.genegroups.size(); i++)
        {
            GeneGroup gg = genegroups.get(i);
            //1) make sure that this is a subset in the database.
            boolean found = false;
            TraitSubset myset = null;
            for (TraitSubset ts : genes.getSubsets())
            {
                if (ts.getName().equals(this.name + "_GG" + i))
                {
                    found = true;
                    isOK = true;
                    myset = ts;
                }
            }
            if (!found)
            {
                myset = new TraitSubset(genes, gg.getGeneSubsetIds(), this.name + "_GG" + i);
                genes.addSubset(myset);
            }

            try
            {
                //2) make sure a go analysis has been performed.
                if (myset.getGoList() < 0 && !isOK)
                {
                    if (go == null)
                    {
                        go = new GOFrame(reference, reference, genes.getSpecies(), genes);
                    }
                    myset.runGOAnalysis(go, genes);
                }
                gg.setGoEnrichment(myset.getGoItems());
            }
            catch (Exception e)
            {
                System.out.println("You need to set a species when you load in trait data.");
            }
        }

        associations = new ArrayList<GTAssocData>();
        maxGTAssocWeight = 0.0;
        cols.clear();
        cols.add("genegroups.geneid");
        cols.add("traitid");
        cols.add("value");
        cols.add("grp");
        where.clear();
        where.add("genegroups.gtassocsetid=" + this.id);
        where.add("genegroups.geneid = genetraitassociation" + DataManager.getTeamCode() + ".geneid");
        res = DataManager.runMultiColSelectQuery(cols,
                "genetraitassociation, genegroups", true, where, null);
        for (HashMap<String, String> hm : res)
        {
            int gid = Integer.parseInt(hm.get("genegroups.geneid"));
            int tid = Integer.parseInt(hm.get("traitid"));
            double val = Double.parseDouble(hm.get("value"));
            if (Math.abs(val) > maxGTAssocWeight)
            {
                maxGTAssocWeight = Math.abs(val);
            }
            int grp = Integer.parseInt(hm.get("grp"));
            this.associations.add(new GTAssocData(gid, tid, val, grp));
        }
    }

    /**
     * Returns the name of this object
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Returns the association set of this object
     */
    public AssociationSet getSNPAssoc()
    {
        return this.snpGeneAssoc;
    }

    /**
     * Returns the traits involved with this object. 
     */
    public TraitSet getTraitSet()
    {
        return this.traits;
    }

    /**
     * Returns the genes involved with this object.
     */
    public TraitSet getGeneSet()
    {
        return this.genes;
    }

    /**
     * Returns the network that has the connections between traits. 
     * @return
     */
    public ArrayList<Edge> getTraitNetworkStructure() throws Exception
    {
        ArrayList<Edge> toRet = this.traitnet.getNetworkStructure(null, this.traits.getTraits(), "default", true);

        maxTraitEdgeWeight = 0.0;
        for (Edge e : toRet)
        {
            double d = Math.abs(e.weight);
            if (d > maxTraitEdgeWeight)
            {
                maxTraitEdgeWeight = d;
            }
        }

        return toRet;
    }

    /**
     * Returns the maximum weight of an edge between two traits
     */
    public double getMaxTraitEdgeWeight()
    {
        return this.maxTraitEdgeWeight;
    }

    /**
     * Returns the maximum weight of an edge between two genes
     */
    public double getMaxGeneEdgeWeight()
    {
        return this.maxGeneEdgeWeight;
    }

    /**
     * Returns the maximum weight of a gene-trait association
     */
    public double getMaxGTAssocWeight()
    {
        return this.maxGTAssocWeight;
    }

    /**
     * Returns the network with connections between genes.
     */
    public ArrayList<Edge> getGeneNetworkStructure() throws Exception
    {
        ArrayList<Integer> subby = new ArrayList<Integer>();
        for (GeneGroup gg : this.genegroups)
        {
            for (Trait g : gg.getGenes())
            {
                subby.add(g.getId());
            }
        }
        ArrayList<Edge> toRet = this.geneNet.getNetworkStructure(new TraitSubset(genes, subby), this.genes.getTraits(), "default", true);

        maxGeneEdgeWeight = 0.0;
        for (Edge e : toRet)
        {
            double d = Math.abs(e.weight);
            if (d > maxGeneEdgeWeight)
            {
                maxGeneEdgeWeight = d;
            }
        }

        return toRet;
    }

    /**
     * Returns the gene groups that are in this object.
     * @return
     */
    public ArrayList<GeneGroup> getGeneGroups()
    {
        return this.genegroups;
    }

    /**
     * This method takes in a gene and returns what gene group, if any, it
     * belongs to
     */
    public int getGroupNoForGene(Trait t)
    {
        Integer g = this.groups.get(t.getId());
        if (g != null)
        {
            return g;//.getGroupNo();
        }
        return -1;
    }

    /**
     * Returns the collections of associations
     */
    public ArrayList<GTAssocData> getGTAssocs()
    {
        return this.associations;
    }

    /**
     * Retrieves the visualization settings for this gene-trait
     * association object. This can be used to update the display. Most
     * objects should act as an observer to this object so they
     * know whenever it is changed. 
     * @return
     */
    public ThreeWayVisualizationSettings getVizSetts()
    {
        return this.visSets;
    }

    /**
     * Remove this GTA from the database
     */
    public void delete()
    {
        ThreadingItemFrame tif = ThreadingItemFrame.getInstance();
        DeletionItem di = new DeletionItem(tif, this.snpGeneAssoc.getProject().getId(),
                DeletionItem.DELETE_GTA, this.name, this.snpGeneAssoc.getName());
        tif.addToThreadList(di);
        tif.setVisible(true);
        tif.setDefaultCloseOperation(HIDE_ON_CLOSE);
    }
}
