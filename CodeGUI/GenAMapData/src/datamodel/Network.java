package datamodel;

import BiNGO.GoItems;
import control.itempanel.DeletionItem;
import control.itempanel.NetModuleGoUpdateItem;
import control.itempanel.NetworkDownloadItem;
import control.itempanel.ThreadingItemFrame;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import realdata.DataManager;
import java.util.Comparator;
import javax.swing.JOptionPane;
import static javax.swing.WindowConstants.HIDE_ON_CLOSE;

/**
 * A network is a structure object that belongs to a traitset. When
 * a network is created, it assigns connections and connection strengths
 * between different traits. It can be visualized using an image,
 * or it can be visualized using a stick-and-ball representation. 
 * @author Shirdoo
 */
public class Network implements Serializable
{
    /**
     * The id of the traitset that this network belongs to
     */
    private int traitSetId;
    /**
     * The database id of this network
     */
    private int id;
    /**
     * The name of the traitset that this network belongs to.
     */
    private String traitSetName;
    /**
     * The project that this network belongs to.
     */
    private String projectName;
    /**
     * There are different types of networks - only one is really allowed
     * per traitset. What type is this?
     */
    private String networkType;
    /**
     * The name of this network
     */
    private String name;
    /**
     * A pointer to the traitset that this network belongs to. 
     */
    private TraitSet ts;
    /**
     * In the matrix view, the traits are going to be ordered by
     * some clustering. This value stores the current ordering of the traits.
     */
    private ArrayList<Trait> traitClustering = new ArrayList<Trait>();
    /**
     * Large networks have a data directory made to store different
     * resolutions of the data. This is the data where this data is stored.
     */
    private String directory;
    /**
     * Networks can be clustered by different clusterings. What cluster is currently
     * being displayed?
     */
    private String curClust;

    /**
     * A network constructor that creates a new network object based on its
     * database values.
     * @param ts the traitset this network belongs to.
     * @param networkType the type of network this is
     * @param name the user-defined name of the network
     * @param ID the database id of the network. 
     */
    public Network(TraitSet ts, String networkType, String name, int ID)
    {
        this.ts = ts;
        this.id = ID;
        this.name = name;
        this.networkType = networkType;
        this.traitSetName = ts.getName();
        this.projectName = ts.getProjectName();
        this.traitSetId = ts.getId();
        this.directory = networkType + "_" + ID;

        checkForUpdate(ts);
    }

    /**
     * Returns the type of this network.
     * @return
     */
    public String getType()
    {
        return this.networkType;
    }

    /**
     * Returns this networks' name - type is embedded in the name.
     * @return
     */
    public String getName()
    {
        if (name == null)
        {
            return networkType;
        }
        else
        {
            return name + "  (" + networkType + ")";
        }
    }

    /**
     * Instead of returning a name with the type embedded, this method
     * only returns the user-defined name of the network
     * @return
     */
    public String getTrueName()
    {
        return name;
    }

    public ArrayList<Edge> getNetworkStructure(TraitSubset subset, ArrayList<Trait> traits,
            String cluster) throws Exception
    {
        return getNetworkStructure(subset, traits, cluster, false);
    }

    /**
     * This returns a network structure for display. It checks to see if resolution
     * data is ready in the case of large datasets, and if it isn't, it starts
     * the process. In the case that the network is small, it queries the database.
     * In the case that the network is large, it loads the appropriate resolution. 
     * @param subset the trait subset to query against the network.
     * @param traits an array list of traits that are in this network
     * @param cluster The clustering to display
     * @return
     * @throws Exception
     */
    public ArrayList<Edge> getNetworkStructure(TraitSubset subset, ArrayList<Trait> traits,
            String cluster, boolean isReturnFull) throws Exception
    {
        ArrayList<Edge> edges = new ArrayList<Edge>();
        if (traits.size() <= 200 || isReturnFull)
        {
            getSmallNetworkStructure(subset, traits, edges);
            return edges;
        }

        File f = new File("data/" + directory + "/complete.txt");
        if (!f.exists())
        {
            return null;
        }
        for (String c : ts.getClusters())
        {
            f = new File("data/" + directory + c + "/complete.txt");
            if (!f.exists())
            {
                return null;
            }
        }
        getResolutionDataReady(cluster, traits, subset, false);

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
            Scanner scanner = new Scanner(new File("data/" + directory + curClust + "/net1_1.txt"), "DEFAULT");

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
     * Returns the directory to write resolution data to. This class does not
     * create the directory.
     * @return
     */
    public String getDir()
    {
        return "data/" + this.directory + curClust;
    }

    /**
     * Given a subset, this method determines exactly which traits are in
     * this network structure. I think that this is called before
     * getNetworkStructure to get the parameters to pass in. 
     * @param subset
     * @return
     * @throws Exception
     */
    public ArrayList<Trait> getNetworkNodes(TraitSubset subset) throws Exception
    {
        ArrayList<String> whereArgs = new ArrayList<String>();
        String filter;
        if (subset != null)
        {
            if (traitSetId != subset.getTraitSetId())
            {
                throw new Exception("Subset corresponds to different trait set");
            }

            filter = "id IN (";
            for (int i = 0; i < subset.getIndeces().size(); i++)
            {
                filter += (subset.getIndeces().get(i).intValue());
                filter += (i + 1 != subset.getIndeces().size()) ? "," : ")";

            }
            whereArgs.add(filter);
        }
        //DUMMY DATA REFRENCE
        whereArgs.add("traitsetid=" + traitSetId);
        ArrayList<String> cols = new ArrayList<String>();
        cols.add("id");
        cols.add("name");
        cols.add("idx");
        cols.add("golist");
        ArrayList<HashMap<String, String>> SQLMaps = DataManager.runMultiColSelectQuery(cols, "trait", true, whereArgs, null);
        whereArgs.clear();
        cols.clear();

        ArrayList<Trait> traits = new ArrayList<Trait>();
        for (HashMap<String, String> traitMap : SQLMaps)
        {
            int traitId = Integer.parseInt(traitMap.get("id"));
            String nm = traitMap.get("name");
            int idx = Integer.parseInt(traitMap.get("idx"));
            int golist = Integer.parseInt(traitMap.get("golist"));
            traits.add(new Trait(traitId, nm, idx, this.ts, golist));
        }
        return traits;
    }

    /**
     * Returns the project name this network belongs to.
     * @return
     */
    public String getProjectName()
    {
        return this.projectName;
    }

    /**
     * Returns the traitsetname this network belongs to.
     * @return
     */
    public String getTraitName()
    {
        return traitSetName;
    }

    /**
     * Returns teh traitset that this network belongs to.
     * @return
     */
    public TraitSet getTraitSet()
    {
        return this.ts;
    }

    /**
     * Deletes this network object from the database so that it will never
     * be seen again.
     * @return
     */
    public boolean delete()
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("id =" + this.id);
        DataManager.runUpdateQuery("network", "loadcmpt", "0", where);
        ThreadingItemFrame tif = ThreadingItemFrame.getInstance();
        DeletionItem di = new DeletionItem(tif, this.traitSetId, DeletionItem.DELETE_NETWORK,
                this.networkType);
        tif.addToThreadList(di);
        tif.setVisible(true);
        tif.setDefaultCloseOperation(HIDE_ON_CLOSE);

        return true;
    }

    /**
     * Calls the DownloaderHelper in order to get the resolution data ready.
     * This will determine the cluster that is needed, whether the data is in
     * the file, and then will call the helper to make the data if it is not there
     * @param cluster The clustering of the data that should be used.
     * @param traits The list of traits that should be in this network
     * @param subby The subset of traits that we want to display
     * @param ndh The downloader helper that will download resolution data.
     */
    private void getResolutionDataReady(String cluster, ArrayList<Trait> traits, TraitSubset subby, boolean isJustCheckDir)
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

            NetworkDownloadItem item = new NetworkDownloadItem(form, traitClustering, this.id, "data/" + directory + curClust);

            form.addToThreadList(item);
            form.setVisible(true);
            form.setDefaultCloseOperation(HIDE_ON_CLOSE);
        }
    }

    /**
     * If a network is sufficiently small, we don't need resolution data.
     * In this case, we just simply query the database through
     * this method.
     * @param subset the subset of traits to query for
     * @param traits the trait list of the traits displayed
     * @param edges The edges that are returned. 
     * @throws NumberFormatException
     */
    private void getSmallNetworkStructure(TraitSubset subset, ArrayList<Trait> traits, ArrayList<Edge> edges) throws NumberFormatException
    {
        ArrayList<String> whereArgs = new ArrayList<String>();
        if (subset != null)
        {
            String filter = "trait1 IN (";
            String filter2 = "trait2 IN (";
            //for (int i = 0; i < traits.size(); i++)
            for (int i = 0; i < subset.getIndeces().size(); i++)
            {
                filter += subset.getIndeces().get(i);
                filter2 += subset.getIndeces().get(i);
                filter += (i + 1 == subset.getIndeces().size()) ? ")" : ",";
                filter2 += (i + 1 == subset.getIndeces().size()) ? ")" : ",";
                /*filter += traits.get(i).getId();
                filter2 += traits.get(i).getId();
                filter += (i + 1 == traits.size()) ? ")" : ",";
                filter2 += (i + 1 == traits.size()) ? ")" : ",";*/
            }
            whereArgs.add(filter);
            whereArgs.add(filter2);
        }
        whereArgs.add("netid=" + this.id);
        whereArgs.add("abs(weight) > .1");
        ArrayList<String> cols = new ArrayList<String>();
        cols.add("trait1");
        cols.add("trait2");
        cols.add("weight");
        ArrayList<HashMap<String, String>> SQLMaps = DataManager.runMultiColSelectQuery(cols, "networkval", true, whereArgs, null);
        for (HashMap<String, String> netEdge : SQLMaps)
        {
            int t1Id = Integer.parseInt(netEdge.get("trait1"));
            int t2Id = Integer.parseInt(netEdge.get("trait2"));
            Trait t1 = getTrait(traits, t1Id);
            Trait t2 = getTrait(traits, t2Id);
            double weight = Double.parseDouble(netEdge.get("weight"));
            edges.add(new Edge(t1, t2, weight));
        }
    }

    /**
     * Returns a trait with the specified id if it is in the list.
     * @param traits The list to search through
     * @param id the id to find. 
     * @return
     */
    private Trait getTrait(ArrayList<Trait> traits, int id)
    {
        for (Trait t : traits)
        {
            if (t.getId() == id)
            {
                return t;
            }
        }
        return null;
    }

    /**
     * Renames this network object in the database.
     * @param newName
     */
    public void rename(String newName)
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("id = " + id);
        DataManager.runUpdateQuery("network", "name", newName, where);
        this.name = newName;
    }

    /**
     * Returns this network's database id. 
     * @return
     */
    public int getId()
    {
        return this.id;
    }

    /**
     * Considers each clustering to see if the resolution data for this
     * network has been downloaded yet. If things have not been downloaded,
     * it is added to the download queue. 
     */
    public void checkForUpdate(TraitSet t)
    {
        if (t.getNumTraits() < 200)
        {
            return;
        }

        try
        {
            getResolutionDataReady("default", t.getTraits(), null, true);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }

        for (String cluster : t.getClusters())
        {
            try
            {
                getResolutionDataReady(cluster, t.getTraits(), null, true);
            }
            catch (Exception e)
            {
                System.err.println(e.getMessage());
            }
        }

        for (String s : getModuleAnalyses(false, -1)) //these have not been GO updated.
        {
            ThreadingItemFrame form = ThreadingItemFrame.getInstance();
            NetModuleGoUpdateItem item = new NetModuleGoUpdateItem(form, this, s.split(",")[0],
                    s.split(",")[1]);

            form.addToThreadList(item);
            form.setVisible(true);
            form.setDefaultCloseOperation(HIDE_ON_CLOSE);
        }
    }

    public Iterable<String> getModuleAnalyses(int cluster)
    {
        return getModuleAnalyses(true, cluster);
    }

    public Iterable<String> getModuleAnalyses(boolean isGetUpdated, int cluster)
    {
        ArrayList<String> mods = new ArrayList<String>();
        ArrayList<String> where = new ArrayList<String>();
        where.add("netid = " + this.id);
        if (cluster != -1)
        {
            where.add("clusterid =" + cluster);
        }
        if (isGetUpdated)
        {
            where.add("golistid != -1");
        }
        else
        {
            where.add("golistid = -1");
        }
        ArrayList<String> cols = new ArrayList<String>();
        cols.add("goanno");
        cols.add("assocsetid");
        ArrayList<HashMap<String, String>> res = DataManager.runMultiColSelectQuery(cols, "netmodule", true, where, null);
        if (res == null)
        {
            System.err.println(DataManager.getLastError());
        }
        for (HashMap<String, String> hm : res)
        {
            where.clear();
            where.add("id=" + hm.get("assocsetid"));
            String nm = (String) DataManager.runSelectQuery("name", "assocset", true, where, null).get(0);
            //(Model.getInstance().getProject(this.ts.getProjectName()).
            //getAssociation(Integer.parseInt(hm.get("assocsetid"))).getName());
            nm += "," + hm.get("goanno");
            if (!mods.contains(nm))
            {
                mods.add(nm);
            }
        }

        return mods;
    }

    /**
     * Iterate through the current clustering and determine the max and min index
     * of the subset in the clustering. 
     */
    public ArrayList<Integer> getMaxMinIndexFromTraitList(int traitlist)
    {
        HashMap<Integer, Integer> cluster = new HashMap<Integer, Integer>();
        for (Trait t : this.traitClustering)
        {
            cluster.put(t.getId(), t.getSortIdx());
        }

        ArrayList<String> where = new ArrayList<String>();
        where.add("id=" + traitlist);
        String list = (String) DataManager.runSelectQuery("list", "traitlist", true, where, null).get(0);

        list = list.replace(",", " ");
        list = list.trim();
        String[] ids = list.split(" ");
        int min = 100000;
        int max = 0;
        for (String s : ids)
        {
            int id = Integer.parseInt(s);
            int idx = cluster.get(id);
            if (idx < min)
            {
                min = idx;
            }
            if (idx > max)
            {
                max = idx;
            }
        }
        ArrayList<Integer> toret = new ArrayList<Integer>();
        toret.add(min);
        toret.add(max);
        return toret;
    }

    /**
     * Returns an arraylist of go and eQTL items from querying the database
     * for the particular module number. 
     * @param i
     * @param modwhere
     * @return
     */
    public ArrayList<GoItems> getEnrichmentItems(int i, ArrayList<String> modwhere)
    {
        modwhere.add("idx=" + i);
        int goidx = Integer.parseInt((String) DataManager.runSelectQuery("golistid", "netmodule", true, modwhere, null).get(0));
        int eqtlidx = Integer.parseInt((String) DataManager.runSelectQuery("eQTLlistid", "netmodule", true, modwhere, null).get(0));

        modwhere.remove(modwhere.size() - 1);
        ArrayList<String> where = new ArrayList<String>();
        where.add("id=" + eqtlidx);
        String eqtl = (String) DataManager.runSelectQuery("list", "eQTLlist", true, where, null).get(0);
        where.clear();
        where.add("id=" + goidx);
        String go = null;
        try
        {
            go = (String) DataManager.runSelectQuery("list", "golist", true, where, null).get(0);
        }
        catch (Exception e)
        {
            return new ArrayList<GoItems>();
        }

        ArrayList<GoItems> toret = new ArrayList<GoItems>();

        String[] eqtls = eqtl.split(",");
        String[] gos = go.split(",");

        for (String s : gos)
        {
            if (s.length() > 1)
            {
                toret.add(GoItems.parse(s));
            }
        }

        Collections.sort(toret, new Comparator<GoItems>()
        {
            public int compare(GoItems o1, GoItems o2)
            {
                if (o1.pval < o2.pval)
                {
                    return -1;
                }
                return 1;
            }
        });

        ArrayList<GoItems> toret2 = new ArrayList<GoItems>();

        for (String s : eqtls)
        {
            if (s.length() > 1)
            {
                String[] stuff = s.split("\\*");
                int markerid = Integer.parseInt(stuff[stuff.length - 1]);
                where.clear();
                where.add("id=" + markerid);
                ArrayList<String> cols = new ArrayList<String>();
                cols.add("name");
                cols.add("chr");
                cols.add("locus");
                HashMap<String, String> res =
                        (HashMap<String, String>) DataManager.runMultiColSelectQuery(cols, "marker", true, where, null).get(0);
                s = s.replace("" + markerid, "");
                s += res.get("name") + "*" + res.get("chr") + "(" + res.get("locus") + ")";

                toret2.add(GoItems.parse(s));
            }
        }
        Collections.sort(toret2, new Comparator<GoItems>()
        {
            public int compare(GoItems o1, GoItems o2)
            {
                if (o1.pval < o2.pval)
                {
                    return -1;
                }
                return 1;
            }
        });

        if (toret.size() > 0 && toret2.size() > 0)
        {
            GoItems dummy = new GoItems();
            dummy.GO_ID = "-1";
            toret.add(dummy);
        }

        for (GoItems gi : toret2)
        {
            toret.add(gi);
        }

        return toret;
    }

    public void writeToFile()
    {
        try
        {
            ArrayList<Trait> tids = this.ts.getTraits();
            FileWriter fstream = new FileWriter("temp1.txt");
            BufferedWriter out = new BufferedWriter(fstream);

            for (int i = 0; i < tids.size(); i++)
            {
                for (int j = 0; j < tids.size(); j++)
                {
                    ArrayList<String> where = new ArrayList<String>();
                    where.add("assocsetid = " + this.id);
                    where.add("markerid=" + tids.get(i).getId());
                    where.add("traitid=" + tids.get(j).getId());
                    ArrayList<String> res = DataManager.runSelectQuery("weight", "association", true, where, null);
                    if (res.size() == 0)
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
            JOptionPane.showMessageDialog(null, "Export succeeded to temp1.txt");
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, "Export failed.");
        }
    }
}
