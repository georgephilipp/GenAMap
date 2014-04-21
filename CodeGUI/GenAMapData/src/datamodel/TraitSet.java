package datamodel;

import control.itempanel.DeletionItem;
import control.itempanel.ThreadingItemFrame;
import static javax.swing.WindowConstants.HIDE_ON_CLOSE;
import datamodel.Model.ParameterSet;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import javax.swing.JFrame;
import realdata.DataManager;
import java.util.HashMap;

/**
 * The Traitset is the pointer to the traitset object in the database. Basically
 * it is the the main object that stores a collection of traits.
 * @author Shirdoo
 */
public class TraitSet implements Serializable
{
    /**
     * The database id of this traitset.
     */
    private int id;
    /**
     * The name of this traitset
     */
    private String name;
    /**
     * The name of the project to which this traitset belongs
     */
    private String projectName;
    /**
     * The id of the project that this traitset belongs to
     */
    private int projectId;
    /**
     * The collection of traits that are under this traitset. This is not
     * serialized, but is opened by need to preserve memory.
     */
    private ArrayList<Trait> traits;
    /**
     * The collection of networks that belong to this traitset.
     */
    private ArrayList<Network> networks;
    /**
     * the collection of trees that belong to this traitset.
     */
    private ArrayList<TraitTree> trees;
    /**
     * All of the TraitSubsets that belong to this traitset.
     */
    private ArrayList<TraitSubset> subsets;
    /**
     * The number of traits that belong to this traitset.
     */
    private int numTraits;
    /**
     * The database samples that this traitset controls.
     */
    private ArrayList<Sample> samples;
    /**
     * The default subset is the subset containing all traits. This is what
     * is shown when is not a subset selected. 
     */
    private TraitSubset defaultSubby;
    /**
     * The species id that these traits belong to - 0 means none
     */
    private int speciesId;
    /**
     * The GO annotations that have been run for this traitset
     */
    private String goannos;

    /**
     * Creates a new TraitSet with the database id
     * @param id the database id of this traitset
     * @param proj the project to which the traitset belongs
     * @param name the name of this traitset.
     */
    public TraitSet(int id, Project proj, String name, int species, String goann)
    {
        this.id = id;
        this.name = name;
        this.projectId = proj.getId();
        this.projectName = proj.getName();
        this.speciesId = species;
        this.goannos = goann;

        this.traits = new ArrayList<Trait>();
        this.networks = new ArrayList<Network>();
        this.trees = new ArrayList<TraitTree>();
        updateTraitSubsets();
    }

    public boolean getHasData()
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("id = " + id);
        return DataManager.runSelectQuery("hasdata", "traitset", true, where, null).get(0).equals("1");
    }

    /**
     * Dummy constructor to represent a traitset that isn't completely loaded
     * in the database yet.
     * @param name
     */
    public TraitSet(String name)
    {
        this.name = name;
    }

    /**
     * Loads in all of the traits from the database into the array list
     * in this object. This allows us to only store these traits by need, instead
     * of holding onto all of the data from the database all the time. 
     */
    private void openTraits()
    {
        ArrayList<String> cols = new ArrayList<String>();
        ArrayList<String> whereArgs = new ArrayList<String>();
        ArrayList<Trait> traits_1 = new ArrayList<Trait>();
        cols.add("id");
        cols.add("name");
        cols.add("idx");
        cols.add("golist");
        whereArgs.add("traitsetid=" + this.getId());
        ArrayList<HashMap<String, String>> traitList = DataManager.runMultiColSelectQuery(cols, "trait", true, whereArgs, "id");
        for (HashMap<String, String> traitVals : traitList)
        {
            int id = Integer.parseInt(traitVals.get("id"));
            String name = traitVals.get("name");
            int idx = Integer.parseInt(traitVals.get("idx"));
            int golist = Integer.parseInt(traitVals.get("golist"));
            traits_1.add(new Trait(id, name, idx, this, golist));
        }
        traits = traits_1;
    }

    /**
     * Query the database to find out the name of the species to which this
     * traitset belongs
     * @return
     */
    public String getSpecies()
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("id=" + this.speciesId);
        ArrayList<String> qres = DataManager.runSelectQuery("name", "species", true, where, null);
        if(qres.isEmpty())
            return "";
        else
            return (String) qres.get(0);
    }

    /**
     * This method is called to release the traits from memory to save on memory
     * space.
     */
    public void closeTraits()
    {
        this.traits.clear();
    }

    /**
     * Returns the number of traits that are in this trait subset. 
     * @return
     */
    public int getNumTraits()
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("traitsetid = " + this.id);
        return Integer.parseInt((String) DataManager.runSelectQuery("count(*)", "trait", true, where, null).get(0));
        /*if (traits == null || traits.size() == 0)
        {
        openTraits();
        }
        if (numTraits != traits.size())
        {
        numTraits = traits.size();
        }
        return numTraits;*/
    }

    /**
     * Retrieves the idx-th trait from this dataset, as it was read in from
     * the file.
     * @param idx
     * @return
     */
    public Trait getTraitByIdx(int idx)
    {
        if (traits == null || traits.size() == 0)
        {
            openTraits();
        }
        if (traits != null)
        {
            return traits.get(idx);
        }
        return null;
    }

    /**
     * Given a traitid known to be part of a traitset, this method
     * will find that trait and return its object.
     * @param traitId
     * @return
     */
    public Trait getTrait(int traitId)
    {
        if (traits == null || traits.size() == 0)
        {
            openTraits();
        }
        for (Trait trait : traits)
        {
            if (trait.getId() == traitId)
            {
                return trait;
            }
        }
        return null;
    }

    /**
     * Finds out what index in the file this trait was when it was
     * first read in.
     * @param t1
     * @return
     */
    public int getIndex(Trait t1)
    {
        return getIndex(t1, 0);
    }

    /**
     * Finds the index of the trait passed in, but starts at a specified
     * index in its search to save time.
     * @param t1
     * @param startIndex
     * @return
     */
    private int getIndex(Trait t1, int startIndex)
    {
        if (traits == null)
        {
            return -1;
        }
        int traitSize = t1.getValues().size();
        for (int i = startIndex; i < traits.size(); i++)
        {
            Trait t2 = traits.get(i);
            boolean isEqual = true;
            for (int j = 0; j < traitSize; j++)
            {
                if (!t1.getValues().get(j).equals(t2.getValues().get(j)))
                {
                    isEqual = false;
                }
            }
            if (isEqual)
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the names of all networks that belong to this traitset.
     * @return
     */
    public ArrayList<String> getNetworkNames()
    {
        ArrayList<String> names = getTreeNames();
        for (int i = 0; i < networks.size(); i++)
        {
            String temp = this.networks.get(i).getTrueName();
            if (temp != null && temp.length() > 0)
            {
                names.add(temp);
            }
        }
        return names;
    }

    /**
     * Returns the list of all types of networks that belong to this traitset
     */
    public ArrayList<String> getNetworkTypes()
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("ts =" + id);
        return DataManager.runSelectQuery("type", "network", true, where, null);
        /*ArrayList<String> names = new ArrayList<String>();
        for (int i = 0; i < networks.size(); i++)
        {
        String temp = this.networks.get(i).getType();
        if (temp != null && temp.length() > 0)
        {
        names.add(temp);
        }
        }*/
    }

    /**
     * Gets the type of all the networks that belong to this traitset - in
     * this we we try to avoid duplicate networks for the traitset. 
     * @return
     */
    public ArrayList<String> getNetworkIdentifiers()
    {
        ArrayList<String> names = new ArrayList<String>();
        for (int i = 0; i < networks.size(); i++)
        {
            names.add(networks.get(i).getName());
        }
        return names;
    }

    /**
     * Returns a list of all the subsets that belong to this trait
     * set. This is for display purposes.
     * @return
     */
    public ArrayList<String> getSubsetNames()
    {
        ArrayList<String> names = new ArrayList<String>();
        for (int i = 0; i < subsets.size(); i++)
        {
            String temp = this.subsets.get(i).getName();
            if (temp != null && temp.length() > 0)
            {
                names.add(temp);
            }
        }
        return names;
    }

    /**
     * Returns an array of all network objects taht belong to this traitset.
     * @return
     */
    public ArrayList<Network> getNetworks()
    {
        return networks;
    }

    /**
     * REturns an array of all trees that belong to this traitset.
     * @return
     */
    public ArrayList<TraitTree> getTraitTrees()
    {
        return trees;
    }

    /**
     * Adds the network to the list of networks that this traitset
     * owns.
     * @param n
     */
    public void addNetwork(Network n)
    {
        networks.add(n);
    }

    /**
     * Adds the tree to the list of trees that htis traitset owns
     */
    public void addTree(TraitTree tt)
    {
        trees.add(tt);
    }

    /**
     * Removes the network from the database.
     * @param n The network to remove
     * @param j The JFrame to base the deletion dialog on. 
     */
    public boolean removeNetwork(String n, JFrame j)
    {
        boolean isdeled = false;
        for (int i = 0; i < networks.size(); i++)
        {
            if (networks.get(i).getName().equals(n))
            {
                networks.get(i).delete();
                networks.remove(i);
                isdeled = true;
            }
        }
        for (int i = 0; i < trees.size(); i++)
        {
            if (trees.get(i).getName().equals(n))
            {
                trees.get(i).delete();
                trees.remove(i);
                isdeled = true;
            }
        }

        return isdeled;
    }

    /**
     * Given the name of a tree or network structure, this method finds the
     * appropriate structure and returns it back to the caller. 
     * @param n the network or tree to find
     * @return
     */
    public Object getTraitStructure(String n)
    {
        for (int i = 0; i < networks.size(); i++)
        {
            if (networks.get(i).getName().equals(n))
            {
                return networks.get(i);
            }
        }

        for (int i = 0; i < trees.size(); i++)
        {
            if (trees.get(i).getName().equals(n))
            {
                return trees.get(i);
            }
        }
        return null;
    }

    /**
     * Adds the TraitSubset to the TraitSet's collection of subsets.
     * @param subset
     */
    public void addSubset(TraitSubset subset)
    {
        subsets.add(subset);
    }

    /**
     * Adds a bunch of subsets to the traitSet's collection of subsets.
     * @param subsets
     */
    public void addSubsets(ArrayList<TraitSubset> subsets)
    {
        for (TraitSubset subset : subsets)
        {
            addSubset(subset);
        }
    }

    /**
     * Returns an arraylist containing all traits that belong to this traitset.
     * @return
     */
    public ArrayList<Trait> getTraits()
    {
        if (this.traits == null || traits.size() == 0)
        {
            openTraits();
        }
        return traits;
    }

    /**
     * Returns an array of all traitsubsets that belong to this traitset.
     * @return
     */
    public ArrayList<TraitSubset> getSubsets()
    {
        return subsets;
    }

    /**
     * Given the name of a subset, this method finds that triatset and returns its object.
     * @param name
     * @return
     */
    public TraitSubset getSubset(String name)
    {
        for (TraitSubset s : subsets)
        {
            if (s.getName().equalsIgnoreCase(name))
            {
                return s;
            }
        }

        return null;
    }

    /**
     * This method deletes a subset from the traitset
     * @param subsetName the name of the subset to remove.
     * @return
     */
    public boolean removeSubset(String subsetName)
    {
        for (TraitSubset s : subsets)
        {
            if (s.getName().equalsIgnoreCase(subsetName))
            {
                subsets.remove(s);
                s.delete();
                return true;
            }
        }
        return false;
    }

    /**
     * This method will get all of the traits that belong to this traitset
     * and return it as a subset. It stores the result (a list of ints)
     * for future reference.
     * @return
     */
    public TraitSubset getDefaultSubset()
    {
        if (traits == null || traits.size() == 0)
        {
            openTraits();
        }

        if (this.defaultSubby == null || defaultSubby.getIndeces().size() == 0)
        {
            ArrayList<String> wheres = new ArrayList<String>();
            ArrayList<Integer> ids = new ArrayList<Integer>();
            wheres.add("traitsetid = " + this.id);
            ArrayList<String> idsAsStrings = DataManager.runSelectQuery("id", "trait", true, wheres, "idx");

            for (int i = 0; i < traits.size() && i < idsAsStrings.size(); i++)
            {
                ids.add(Integer.parseInt(idsAsStrings.get(i)));
            }
            defaultSubby = new TraitSubset(this, ids);
        }
        return defaultSubby;
    }

    /**
     * Returns the name of the traitset.
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the name of the project this traitset belongs to.
     * @return
     */
    public String getProjectName()
    {
        return projectName;
    }

    /**
     * Returns the id of the traitset.
     * @return
     */
    public int getId()
    {
        return id;
    }

    /**
     * Delete this traitset from the database so it is never seen again. This
     * method also deletes all traitsubsets, networks, and trees that
     * belong to the traitset.
     * @return true
     */
    public boolean delete()
    {
        //delete subsets
        for (TraitSubset subset : subsets)
        {
            if (!subset.delete())
            {
                return false;
            }
        }

        for (String s : getClusters())
        {
            removeCluster(s);
        }

        ThreadingItemFrame tif = ThreadingItemFrame.getInstance();
        DeletionItem di = new DeletionItem(tif, this.projectId,
                DeletionItem.DELETE_TRAITSET, this.name, this);
        tif.addToThreadList(di);
        tif.setVisible(true);
        tif.setDefaultCloseOperation(HIDE_ON_CLOSE);

        return true;
    }

    /**
     * Returns the network with the specified id.
     * @param netid
     * @return
     */
    Network getNetwork(int netid)
    {

        for (int i = 0; i < networks.size(); i++)
        {
            if (networks.get(i).getId() == netid)
            {
                return networks.get(i);
            }
        }
        return null;
    }

    /**
     * This method is called when a database refresh is called. This method
     * will ensure that all networks in the list match up with a valid
     * id from the database. 
     * @param nets
     */
    public void refreshNets(ArrayList<ParameterSet> nets)
    {
        for (int i = networks.size() - 1; i >= 0; i--)
        {
            boolean cont = false;
            for (ParameterSet q : nets)
            {
                if (q.value_int == networks.get(i).getId())
                {
                    cont = true;
                }
            }
            if (!cont)
            {
                networks.remove(i);
            }
        }

        for (ParameterSet q : nets)
        {
            boolean cont = false;
            for (Network n : networks)
            {
                if (n.getId() == q.value_int)
                {
                    cont = true;
                    n.checkForUpdate(this);
                }
            }
            if (!cont)
            {
                networks.add(new Network(this, q.value_string, q.value_string_2, q.value_int));
            }
        }
    }

    /**
     * This method is called on a model refresh with the database. It
     * ensures that all trees in the database are represented in the list. 
     * @param tree
     */
    public void refreshTrees(ArrayList<ParameterSet> tree)
    {
        for (int i = trees.size() - 1; i >= 0; i--)
        {
            boolean cont = false;
            for (ParameterSet q : tree)
            {
                if (q.value_int == trees.get(i).getId())
                {
                    cont = true;
                }
            }
            if (!cont)
            {
                trees.remove(i);
            }
        }

        for (ParameterSet q : tree)
        {
            boolean cont = false;
            for (TraitTree n : trees)
            {
                if (n.getId() == q.value_int)
                {
                    cont = true;
                    n.checkForUpdate();
                }
            }
            if (!cont)
            {
                trees.add(new TraitTree(q.value_int, q.value_string, this, q.value_bool1));
            }
        }
    }

    /**
     * Returns whether or not the passed in name is the name of a structure
     * in this traitset. 
     * @param toString
     * @return
     */
    public boolean isNetwork(String toString)
    {
        return getTraitStructure(toString) != null;
    }

    /**
     * Returns the samples that belong to this traitset. 
     * @return
     */
    public ArrayList<Sample> getSamples()
    {
        if (samples == null || samples.size() == 0)
        {
            if (traits == null || traits.size() == 0)
            {
                openTraits();
            }
            int tid = this.traits.get(0).getId();

            ArrayList<String> whereArgs = new ArrayList<String>();
            whereArgs.add("traitid=" + tid);
            ArrayList<String> res = DataManager.runSelectQuery("sampleid", "traitval", true, whereArgs, "sampleid");
            this.samples = new ArrayList<Sample>();
            for (String s : res)
            {
                this.samples.add(new Sample(Integer.parseInt(s)));
            }
        }
        return samples;
    }

    /**
     * Returns the number of samples involved in this traitset.
     * @return
     */
    public int getNumSamples()
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("traitsetid=" + this.id);
        try
        {
            int minid = Integer.parseInt((String) DataManager.runSelectQuery("min(id)", "trait", true, where, null).get(0));
            where.clear();
            where.add("traitid=" + minid);
            return Integer.parseInt((String) DataManager.runSelectQuery("count(*)", "traitval", true, where, null).get(0));
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    /**
     * Sets temporary variables to null before a serialization event.
     */
    public void dropByNeed()
    {
        samples = null;
        traits = null;
    }

    /**
     * Returns true if the subset exists in this traitset, and false if it
     * does not.
     * @param toString
     * @return
     */
    public boolean isSubset(String toString)
    {
        return getSubset(toString) != null;
    }

    /**
     * Renames this traitset and updates the database accordingly. 
     * @param newName
     */
    public void rename(String newName)
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("id = " + id);
        DataManager.runUpdateQuery("traitset", "name", newName, where);
        this.name = newName;
    }

    /**
     * If the user has a set cluster to order the nodes by in visualization,
     * this method is called to parse the file (already read in) and to update
     * the database with the new clustering
     * @param name the name of the cluster
     * @param netid the name of the network that created this clustering
     * @param s the text of the cluster file that will be stored in the database.
     */
    public void loadClusterFileIntoDB(String name, int netid, String s)
    {
        ArrayList<String> cols = new ArrayList<String>();
        cols.add("name");
        cols.add("traitid");
        if (netid != -1)
        {
            cols.add("netid");
        }
        cols.add("value");

        ArrayList<String> vals = new ArrayList<String>();
        vals.add(name);
        vals.add("" + this.id);
        if (netid != -1)
        {
            vals.add("" + netid);
        }
        vals.add(s);
        DataManager.runInsertQuery(cols, vals, "cluster");
    }

    /**
     * Returns a list of all clusters that belong to this traitset.
     * @return
     */
    public ArrayList<String> getClusters()
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("traitid = " + this.id);
        return DataManager.runSelectQuery("name", "cluster", true, where, "name");
    }

    /**
     * Removes the specified cluster from the traitset and the database.
     * @param name
     */
    public void removeCluster(String name)
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("traitid = " + this.id);
        where.add("name = '" + name + "'");
        DataManager.deleteQuery("cluster", where);
    }

    /**
     * Queries the database to retrieve the cluster of traits, by name. This method
     * returns an ordered list of these triats for display purposes. 
     * @param name the name of the cluster
     * @param subby a subset of traits to retrieve from the cluster.
     * @return
     */
    public LinkedList<Integer> getCluster(String name, TraitSubset subby)
    {
        if (subby == null)
        {
            subby = this.getDefaultSubset();
        }

        ArrayList<String> where = new ArrayList<String>();
        where.add("traitid = " + this.id);
        where.add("name = '" + name + "'");
        String clust = (String) DataManager.runSelectQuery("value", "cluster", true, where, null).get(0);

        String[] ordering = clust.split(",");

        LinkedList<Integer> toRet = new LinkedList<Integer>();
        for (int i = 0; i < ordering.length; i++)
        {
            if (ordering[i].length() > 0)
            {
                toRet.add(Integer.parseInt(ordering[i]) - 1);
            }
        }

        if (this.traits == null || this.traits.size() == 0)
        {
            openTraits();
        }

        for (Trait t : traits)
        {
            if (!subby.getIndeces().contains(t.getId()))
            {
                toRet.removeFirstOccurrence(t.getIdx());
            }
        }
        return toRet;
    }

    /**
     * Given a traitsubset, this method determines whether or not it belongs
     * to this traitset.
     * @param traitsubset
     * @return
     */
    public String isSubset(TraitSubset traitsubset)
    {
        for (TraitSubset s : this.subsets)
        {
            boolean notit = false;
            for (Integer i : traitsubset.getIndeces())
            {
                if (!s.getIndeces().contains(i))
                {
                    notit = true;
                }
            }
            if (!notit)
            {
                return s.getName();
            }
        }
        return null;
    }

    /**
     * This method determines whether or not the traitset passed in
     * contains every single trait in the traitset or no.
     * @param traitsubset
     * @return
     */
    public boolean isCompleteSet(ArrayList<Integer> traitsubset)
    {
        if (traits == null || traits.size() == 0)
        {
            openTraits();
        }

        for (Trait t : traits)
        {
            if (!hasID(traitsubset, (t.getId())))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * This method returns true if the id is in the list.
     * @param list the list to look through
     * @param id the id to find
     * @return true if id is in list.
     */
    private boolean hasID(ArrayList<Integer> list, int id)
    {
        for (int i : list)
        {
            if (id == i)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the database ids of the list of traits passed in.
     * @param nodes the list of traits to find their ids.
     * @return
     */
    public ArrayList<Integer> getIds(ArrayList<Trait> nodes)
    {
        HashSet<Integer> indeces = new HashSet<Integer>();
        if (traits == null || traits.size() == 0)
        {
            this.openTraits();
        }
        for (Trait t : traits)
        {
            int index = t.getId();
            indeces.add(index);
        }
        this.closeTraits();
        ArrayList<Integer> indexList = new ArrayList<Integer>();
        indexList.addAll(indeces);
        return indexList;
    }

    /**
     * Given a traitsubset s, this method returns a list of all
     * traits that are in that subset.
     * @param s the subset to find the traits for. 
     * @return
     */
    public ArrayList<Trait> getTraits(TraitSubset s)
    {
        if (traits == null || traits.size() == 0)
        {
            openTraits();
        }

        ArrayList<Trait> toret = new ArrayList<Trait>();

        if (s == null)
        {
            return traits;
        }

        for (Trait t : traits)
        {
            if (hasID(s.getIndeces(), (t.getId())))
            {
                toret.add(t);
            }
        }
        return toret;
    }

    /**
     * Returns the ids of all traits in the dataset. 
     * @return
     */
    public ArrayList<Integer> getIds()
    {
        if (traits == null || traits.size() == 0)
        {
            openTraits();
        }

        HashSet<Integer> indeces = new HashSet<Integer>();
        for (Trait t : traits)
        {
            int index = t.getId();
            indeces.add(index);
        }
        ArrayList<Integer> indexList = new ArrayList<Integer>();
        indexList.addAll(indeces);
        return indexList;
    }

    /**
     * Returns the names of all of the trees in the database
     * that belong to this traitset. 
     * @return
     */
    public ArrayList<String> getTreeNames()
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("tsid =" + this.id);
        return DataManager.runSelectQuery("name", "traittree", true, where, null);
    }

    /**
     * Returns a valid name for a new subset to belong to these traits.
     * @return
     */
    public String getNextSubsetName()
    {
        for (int i = 0; i < 1000; i++)
        {
            boolean found = false;
            for (TraitSubset t : this.subsets)
            {
                if (t.getName().equals("subset" + i))
                {
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                return "subset" + i;
            }
        }
        return null;
    }

    /**
     * Returns an array list containing all annotations that have
     * been considered for this traitset to this point
     * @return
     */
    public ArrayList<String> getGoAnnos()
    {
        String[] codes = goannos.split("\\.");
        ArrayList<String> gos = new ArrayList<String>();

        for (int i = 0; i < codes.length; i++)
        {
            if (codes[i].length() > 1)
            {
                gos.add(codes[i]);
            }
        }
        return gos;
    }

    public void addAnnotation(String code)
    {
        this.goannos = this.goannos + code + '.';
        ArrayList<String> where = new ArrayList<String>();
        where.add("id=" + this.id);
        DataManager.runUpdateQuery("traitset", "goannos", goannos, where);
    }

    /**
     * Updates the traitsubsets so that they match the database. 
     * @throws NumberFormatException
     */
    public void updateTraitSubsets() throws NumberFormatException
    {
        this.subsets = new ArrayList<TraitSubset>();
        defaultSubby = null;
        ArrayList<String> cols = new ArrayList<String>();
        cols.add("id");
        cols.add("name");
        cols.add("traitlist");
        cols.add("golist");
        ArrayList<String> where = new ArrayList<String>();
        where.add("tsid=" + this.id);
        ArrayList<HashMap<String, String>> res = DataManager.runMultiColSelectQuery(cols, "traitsubset", true, where, null);
        for (HashMap hm : res)
        {
            String nm = (String) hm.get("name");
            String sid = (String) hm.get("id");
            String tl = (String) hm.get("traitlist");
            String gl = (String) hm.get("golist");
            this.addSubset(new TraitSubset(this, Integer.parseInt(sid), nm, Integer.parseInt(tl), Integer.parseInt(gl)));
        }
    }

    /**
     * Returns the cluster id associated with this clustername.
     * @param clusterName the name of the cluster belonging to this traitset that we are interested in
     * @return db id.
     */
    public int getClusterId(String clusterName)
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("name=\'" + clusterName + "\'");
        where.add("traitid=" + this.id);
        return Integer.parseInt((String) DataManager.runSelectQuery("id", "cluster", true, where, null).get(0));
    }

    /**
     * Returns the project id that this traitset belongs to. 
     * @return
     */
    public int getProjectId()
    {
        return this.projectId;
    }
}
