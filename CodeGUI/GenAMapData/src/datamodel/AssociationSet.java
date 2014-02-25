package datamodel;

import control.itempanel.AssociationDownloadItem;
import control.itempanel.DeletionItem;
import control.itempanel.ThreadingItemFrame;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import javax.swing.JOptionPane;
import realdata.DataManager;
import static javax.swing.WindowConstants.HIDE_ON_CLOSE;

/**
 * AssociationSet is the data that we built GenAMap for! AssociationSet is
 * what we use in order to store the associations between the markers and the
 * traits. It is in this object that we interface with the database in order
 * to ensure that we are accurate with respect to what is happening there.
 *
 * We can also control and change the data in some ways. Associations, like
 * networks, can be viewed using images when they are large, or they can be
 * viewed using stick and ball models with connections back to the genome. 
 * @author Shirdoo
 * @author fgrosan
 */
public class AssociationSet implements Serializable
{
    /**
     * The database id of this association set.
     */
    private int id;
    /**
     * The name of this associations et.
     */
    private String name;
    /**
     * The markerset that this association dataset maps to traits
     */
    private MarkerSet markerSet;
    /**
     * The traitset that this association dataset maps to markers
     */
    private TraitSet traitSet;
    /**
     * The download directory for temporary data for this association set
     */
    private String directory;
    /**
     * The current clustering for the display of this association set.
     */
    private String curClust;
    /**
     * An array list of traits in the order of the trait clustering
     */
    private ArrayList<Trait> traitClustering;
    /**
     * An indicator of whether or not the values in this association set are
     * p-value or exact values (like beta values). 
     */
    private boolean isPvals;
    /**
     * The number of populations in this associationset
     */
    private int numPops;
    /**
     * The reference id of the population for this associationset
     */
    private int popID;
    /**
     * A pointer tot he project that this assocset belongs to
     */
    private Project project;
    /**
     * All of the genetraitassociations that are branches of this associationset.
     */
    private ArrayList<GeneTraitAssociation> gene2traitAssocs;

    /**
     * Full constructor for an association object. Using data retrieved from
     * the database, we  establish this association set object to reflect
     * and control the data therein.
     * @param id The database id of this associationset
     * @param proj The project that this associationset belongs to
     * @param name The name of this association set.
     * @param thresh The threshold used to create this association set
     * @param netid The netid used to create this association set
     * @param tsid The traitset id used to create this association set
     * @param msid The markerset id used to create this association set
     * @param isPval Whether or not this association set uses pValues.
     * @param popID The id of the population, -1 if there isn't any
     * @param popRef The number of populations in the visualization
     */
    public AssociationSet(int id, Project proj, String name, double thresh,
            int netid, int tsid, int msid, boolean isPval, int popID, int numPops)
    {
        this.id = id;
        this.name = name;
        this.project = proj;
        this.markerSet = proj.getMarker(msid);
        this.traitSet = proj.getTrait(tsid);
        this.isPvals = isPval;
        directory = name + id;
        curClust = "";
        this.popID = popID;
        this.numPops = numPops;

        this.gene2traitAssocs = new ArrayList<GeneTraitAssociation>();
        refreshGTAs(id);

        this.checkForUpdate(traitSet);
    }

    /**
     * Returns the project that this association collection belongs to.
     */
    public Project getProject()
    {
        return this.project;
    }

    /**
     * Determines whether or not this AssocSet object is bound to a population or
     * not. 
     * @return
     */
    public boolean isPopAssocSet()
    {
        return this.popID >= 0;
    }

    /**
     * Returns the number of populations in this association set
     * @return
     */
    public int getNumPops()
    {
        return this.numPops;
    }

    /**
     * Returns the population object associated with this AssociationSet.
     * Does not do a check, so it could return null if there is no such thing!
     * @return
     */
    public Population getPopulation()
    {
        return this.markerSet.getPopulation(popID);
    }

    /**
     * Returns the name of this association set. 
     * @return
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Deletes this association set so that it will never be seen nor heard of
     * again.
     * @return
     */
    public boolean delete(int projId)
    {
        ThreadingItemFrame tif = ThreadingItemFrame.getInstance();
        DeletionItem di = new DeletionItem(tif, projId,
                DeletionItem.DELETE_ASSOCIATION, this.name);
        tif.addToThreadList(di);
        tif.setVisible(true);
        tif.setDefaultCloseOperation(HIDE_ON_CLOSE);

        return true;
    }

    /**
     * Returns the database id of this associationset
     * @return
     */
    public int getId()
    {
        return id;
    }

    /**
     * Returns the traitset that this associationset monitors
     * @return
     */
    public TraitSet getTraitSet()
    {
        return this.traitSet;
    }

    /**
     * Returns the markerset that this associationset monitors
     * @return
     */
    public MarkerSet getMarkerSet()
    {
        return this.markerSet;
    }

    void dropByNeed()
    {
    }

    /**
     * Finds all associations between the set of markers and traits that are
     * in this association set.
     * @param markerID a collection of markers that are a subset of the markerset
     * @param traitID a collection of traits that are a subset of the traitset
     * @return all associations between the set of markers and traits.
     */
    public Collection<Association> findAssociations(ArrayList<Integer> markerID, ArrayList<Integer> traitID, int pop)
    {
        ArrayList<String> cols = new ArrayList<String>();
        cols.add("markerid");
        cols.add("traitid");
        cols.add("value");
        cols.add("popref");

        ArrayList<String> where = new ArrayList<String>();
        where.add("assocsetid=" + this.id);
        String s = "traitid IN (";
        for (int i : traitID)
        {
            s += i + ",";
        }
        s = s.substring(0, s.length() - 1) + ")";
        where.add(s);

        s = "markerid IN (";
        for (int i : markerID)
        {
            s += i + ",";
        }
        s = s.substring(0, s.length() - 1) + ")";
        where.add(s);

        if (pop != -1)
        {
            where.add("popref = " + pop);
        }

        ArrayList<HashMap<String, String>> assocs = DataManager.runMultiColSelectQuery(cols, "association", true, where, null);
        ArrayList<Association> toRet = new ArrayList<Association>();

        for (HashMap<String, String> hm : assocs)
        {
            double val = Double.parseDouble(hm.get("value"));
            if (val == 0)
            {
                val = 1e-50;
            }
            toRet.add(new Association(Integer.parseInt(hm.get("markerid")), Integer.parseInt(hm.get("traitid")),
                    val, Integer.parseInt(hm.get("popref"))));
        }

        return toRet;
    }

    /**
     * Finds all associations between all markers with the specified group of traits
     * that are in this association set.
     * @param traitID a collection of traits that are a subset of the traitset
     * @return all associations between the set traits and the entire set of markers.
     */
    public Collection<Association> findAssociationsFromTraits(ArrayList<Integer> traitID, int pop)
    {
        ArrayList<String> cols = new ArrayList<String>();
        cols.add("markerid");
        cols.add("traitid");
        cols.add("value");
        cols.add("popref");

        ArrayList<String> where = new ArrayList<String>();
        where.add("assocsetid=" + this.id);
        String s = "traitid IN (";
        for (int i : traitID)
        {
            s += i + ",";
        }
        s = s.substring(0, s.length() - 1) + ")";
        where.add(s);

        if (pop != -1)
        {
            where.add("popref = " + pop);
        }

        ArrayList<HashMap<String, String>> assocs = DataManager.runMultiColSelectQuery(cols, "association", true, where, null);
        ArrayList<Association> toRet = new ArrayList<Association>();

        for (HashMap<String, String> hm : assocs)
        {
            double val = Double.parseDouble(hm.get("value"));
            if (val == 0)
            {
                val = 1e-50;
            }
            toRet.add(new Association(Integer.parseInt(hm.get("markerid")), Integer.parseInt(hm.get("traitid")),
                    val, Integer.parseInt(hm.get("popref"))));
        }

        return toRet;
    }

    /**
     * Returns a set of Edges that are then used to draw an image of the assocition
     * set. This method checks for resolution, makes resolution images if
     * appropriate, and then returns the collection of edges that is used to
     * draw a figure of the associtions.
     * @param subset the subset of traits that will be displayed
     * @param traits a collection of traits to find.
     * @param adh The downloader which will download the association data
     * @param cluster The cluster currently displayed
     * @param subby another copy of the subset
     * @param isPval whether or not this association set uses pvalues
     * @param thresh The threshold for this association set
     * @return a collection of connections between traits and markers. 
     * @throws Exception
     */
    public ArrayList<Edge> getAssocStructure(TraitSubset subset, ArrayList<Trait> traits,
            String cluster, TraitSubset subby, boolean isPval,
            double thresh) throws Exception
    {
        ArrayList<Edge> edges = new ArrayList<Edge>();
        if (traits.size() <= 200)
        {
            edges = getSmallAssociationStructure(this.getMarkerSet().getMarkers(), traits, edges, thresh, isPval);
            return edges;
        }

        File f = new File("data/" + directory + "/complete.txt");
        if (!f.exists())
        {
            return null;
        }
        for (String c : this.traitSet.getClusters())
        {
            f = new File("data/" + directory + c + "/complete.txt");
            if (!f.exists())
            {
                return null;
            }
        }
        getResolutionDataReady(cluster, traits, subby, this.id, false);

        Scanner checker = null;
        try
        {
            checker = new Scanner(new File("data/" + directory + curClust + "/complete.txt"), "DEFAULT");
        }
        catch (Exception ex)
        {
            return null;
        }

        if (checker != null)
        {
            Scanner scanner = new Scanner(new File("data/" + directory + curClust + "/assoc1_1.txt"), "DEFAULT");
            try
            {
                while (scanner.hasNextLine())
                {
                    String s = scanner.nextLine();
                    String[] vals = s.split(" ");
                    if (vals.length == 3)
                    {
                        edges.add(new Edge(Integer.parseInt(vals[0]), Integer.parseInt(vals[1]), Double.parseDouble(vals[2])));
                    }

                }
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
                return null;
            }
            return edges;
        }
        return null;
    }

    /**
     * When associationsets are small enough, we just query the database
     * directly to draw the association image. This method accomplishes
     * that trick. It finds all connections between markers and traits
     * and returns them in the collection.
     * @param markers the markers to find associations with
     * @param traits the traits to find associations with
     * @param edges the edges between the markers and traits
     * @param thresh the threshold to theshold the edges
     * @param isPval whether or not these are pvalue or absolute values.
     * @return all connections between markers and traits. 
     */
    private ArrayList<Edge> getSmallAssociationStructure(ArrayList<Marker> markers, ArrayList<Trait> traits,
            ArrayList<Edge> edges, double thresh, boolean isPval)
    {
        ArrayList<Integer> mids = new ArrayList<Integer>();
        ArrayList<Integer> tids = new ArrayList<Integer>();

        for (Marker m : markers)
        {
            mids.add(m.getId());
        }

        for (Trait t : traits)
        {
            tids.add(t.getId());
        }

        ArrayList<Association> assocs = (ArrayList<Association>) this.findAssociations(mids, tids, -1);

        edges = new ArrayList<Edge>();

        for (Association a : assocs)
        {
            if (a.getValue() < thresh && isPval)
            {
                edges.add(new Edge(this.markerSet.getMarker(a.getMarkerId()),
                        this.traitSet.getTrait(a.getTraitId()), -Math.log10(a.getValue())));

            }
            else
            {
                edges.add(new Edge(this.markerSet.getMarker(a.getMarkerId()),
                        this.traitSet.getTrait(a.getTraitId()), a.getValue()));
            }
        }
        return edges;
    }

    /**
     * Returns the name of the directory that resolution data will be stored
     * in. Data management is not done in this class.
     * @return
     */
    public String getDir()
    {
        return "data/" + this.directory + curClust;
    }

    /**
     * Gets the resolution data ready by checking whether or not it is there
     * and then creating it if it is not.
     * @param cluster The current clustering
     * @param traits The traits
     * @param subby What subset of the traits to use
     * @param adh The downloader helper. 
     */
    private void getResolutionDataReady(String cluster, ArrayList<Trait> traits, TraitSubset subby, int associd, boolean isJustCheckDir)
    {
        File f;
        if (cluster.equals("default"))
        {
            f = new File("data/" + directory);
            if (isJustCheckDir && f.exists())
            {
                return;
            }
            curClust = "";
            traitClustering = traits;
            for (int i = 0; i < traitClustering.size(); i++)
            {
                traitClustering.get(i).setClusterIdx(traits.get(i).getIdx());
            }
            Collections.sort(traitClustering, new Trait.TraitComparator());
        }
        else
        {
            f = new File("data/" + directory + cluster);
            if (isJustCheckDir && f.exists())
            {
                return;
            }
            curClust = cluster;
            traitClustering = traits;
            Collections.sort(traitClustering, new Trait.TraitComparator());
            LinkedList<Integer> clustering = this.getTraitSet().getCluster(cluster, subby);
            for (int i = 0; i < clustering.size(); i++)
            {
                traitClustering.get(clustering.get(i)).setClusterIdx(i);
            }
            Collections.sort(traitClustering, new Trait.TraitClusterComparator());
        }
        if (!f.exists())
        {
            f.mkdir();

            ThreadingItemFrame form = ThreadingItemFrame.getInstance();

            AssociationDownloadItem item = new AssociationDownloadItem(form, associd, "data/" + directory + curClust,
                    traitClustering, markerSet.getMarkers());

            form.addToThreadList(item);
            form.setVisible(true);
            form.setDefaultCloseOperation(HIDE_ON_CLOSE);
        }
    }

    /**
     * Returns whether or not these are pvalues we are working with in this
     * association set. 
     * @return
     */
    public boolean getIsPvals()
    {
        return this.isPvals;
    }

    /**
     * Considers each clustering to see if the resolution data for this
     * assocset has been downloaded yet. If things have not been downloaded,
     * it is added to the download queue.
     */
    public void checkForUpdate(TraitSet t)
    {
        refreshGTAs(this.id);

        if (t.getNumTraits() < 200)
        {
            return;
        }

        try
        {
            getResolutionDataReady("default", t.getTraits(), null, id, true);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }

        for (String cluster : t.getClusters())
        {
            try
            {
                getResolutionDataReady(cluster, t.getTraits(), null, id, true);
            }
            catch (Exception e)
            {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Renames the assocSet
     * @param newName
     */
    public void rename(String newName)
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("id = " + this.id);
        DataManager.runUpdateQuery("assocset", "name", newName, where);
        this.name = newName;
    }

    /**
     * Returns an array of the maximum and minimum associations associated
     * with this associationset. The array is structured as follows:
     * [1] overall maximum
     * [2] overall minimum
     * [3] absolute value minimum
     * @return
     */
    public ArrayList<Double> getMaxNMinVals(int traitid)
    {
        ArrayList<Double> toRet = new ArrayList<Double>();
        ArrayList<String> where = new ArrayList<String>();
        where.add("assocsetid=" + this.id);
        where.add("traitid=" + traitid);
        try
        {
            double d = Double.parseDouble((String) DataManager.runSelectQuery("max(value)",
                    "association", true, where, null).get(0));
            toRet.add(d);
        }
        catch (Exception e)
        {
            toRet.add(0.00000);
        }

        try
        {
            double d = Double.parseDouble((String) DataManager.runSelectQuery("min(value)",
                    "association", true, where, null).get(0));
            toRet.add(d);
        }
        catch (Exception e)
        {
            toRet.add(0.0);
        }
        try
        {
            double d = Double.parseDouble((String) DataManager.runSelectQuery("min(abs(value))",
                    "association", true, where, null).get(0));
            toRet.add(d);
        }
        catch (Exception e)
        {
            toRet.add(0.0);
        }

        return toRet;
    }

    /**
     * Returns the network or traitset with the name described ... or will
     * return the GTA object if the string does not describe a structure.
     */
    public Object getTraitStructure(String toString)
    {
        Object o = this.traitSet.getTraitStructure(toString);
        if (o == null)
        {
            for (GeneTraitAssociation gta : this.gene2traitAssocs)
            {
                if (gta.getName().equals(toString))
                {
                    return gta;
                }
            }
        }

        return o;
    }

    /**
     * Returns all of the third level association objects that are associated with
     * this particular object. 
     */
    public Iterable<GeneTraitAssociation> getGeneTraitAssocs()
    {
        return this.gene2traitAssocs;
    }

    /**
     * Returns the gene trait association set with the given name ... or null!
     */
    public GeneTraitAssociation getGeneTraitAssoc(String toString)
    {
        for (GeneTraitAssociation gta : this.gene2traitAssocs)
        {
            if (gta.getName().equals(toString))
            {
                return gta;
            }
        }
        return null;
    }

    private void refreshGTAs(int id) throws NumberFormatException
    {
        ArrayList<String> cols = new ArrayList<String>();
        cols.add("id");
        cols.add("name");
        cols.add("geneid");
        cols.add("traitid");
        cols.add("genenetid");
        cols.add("traitnetid");
        ArrayList<String> where = new ArrayList<String>();
        where.add("snpassocid=" + id);
        ArrayList<HashMap<String, String>> res = DataManager.runMultiColSelectQuery(cols, "genetraitassocset", true, where, null);
        for (HashMap<String, String> hm : res)
        {
            if (this.getGeneTraitAssoc(hm.get("name")) == null)
            {
                gene2traitAssocs.add(new GeneTraitAssociation(Integer.parseInt(hm.get("id")), hm.get("name"), Integer.parseInt(hm.get("geneid")), Integer.parseInt(hm.get("traitid")), Integer.parseInt(hm.get("genenetid")), Integer.parseInt(hm.get("traitnetid")), this, this.project));
            }
        }
    }

    public void writeToFile()
    {
        try
        {
            ArrayList<Integer> mids = this.markerSet.getMarkerIds();
            ArrayList<Trait> tids = this.traitSet.getTraits();
            FileWriter fstream = new FileWriter("temp1.txt");
            BufferedWriter out = new BufferedWriter(fstream);

            for (int i = 0; i < mids.size(); i++)
            {
                for (int j = 0; j < tids.size(); j++)
                {
                    ArrayList<String> where = new ArrayList<String>();
                    where.add("assocsetid = " + this.id);
                    where.add("markerid=" + mids.get(i));
                    where.add("traitid=" + tids.get(j).getId());
                    ArrayList<String> res = DataManager.runSelectQuery("value", "association", true, where, null);
                    if(res.size() == 0)
                    {
                        out.write("0");
                    }
                    else
                    {
                        out.write(res.get(0));
                    }
                    out.write("\t");
                }
                out.write("\r\n");
            }

            out.close();
            JOptionPane.showMessageDialog(null,"Export succeeded to temp1.txt");
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, "Export failed.");
        }
    }

    /**
     * Drops the specified GTA from the model list.
     * @param network
     */
    public void removeGTA(String network)
    {
        GeneTraitAssociation torm = null;
        for(GeneTraitAssociation gta : this.gene2traitAssocs)
        {
            if(gta.getName().equals(network))
            {
                torm = gta;
            }
        }

        this.gene2traitAssocs.remove(torm);
    }
}
