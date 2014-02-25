package datamodel;

import BiNGO.BiNGOresults2GenAMap;
import BiNGO.GoItems;
import control.GOFrame;
import realdata.DataManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * A trait subset will store the indices of the traitset that are viewed together.
 * Users can create trait subsets in a variety of means.
 *
 * @author rcurtis
 */
public class TraitSubset implements Serializable
{
    /**
     * The list of database ids of the triats that make up this subset.
     */
    private int traitids;
    /**
     * The name of this traitsubset
     */
    private String name;
    /**
     * The id of the traitset that this traitsubset belongs to.
     */
    private int traitSetId;
    /**
     * The database id of this subset
     */
    private int id;
    /**
     * The database id of the go list belonging to this traitsubset.
     */
    private int golist = -1;
    /**
     * A list of ints for a non database subset
     */
    private ArrayList<Integer> idlist = null;

    /**
     * Creates a new traitsubset. 
     * @param traitSet the traitset that this traitsubset belongs to.
     * @param indeces the indeces in this traitsubset
     * @param name the name of the traitsbuset
     */
    public TraitSubset(TraitSet traitSet, ArrayList<Integer> indeces, String name)
    {
        this.name = name;
        this.traitSetId = traitSet.getId();
        ArrayList<String> cols = new ArrayList<String>();
        cols.add("name");
        cols.add("tsid");
        ArrayList<String> vals = new ArrayList<String>();
        vals.add(name);
        vals.add(traitSet.getId() + "");
        DataManager.runInsertQuery(cols, vals, "traitsubset");

        ArrayList<String> where = new ArrayList<String>();
        where.add("name=\'" + name + "\'");
        where.add("tsid=\'" + this.traitSetId + "\'");
        id = Integer.parseInt((String)DataManager.runSelectQuery("id", "traitsubset", true, where, null).get(0));

        String s = "";
        for (int i : indeces)
        {
            s += i + ",";
        }

        vals.clear();
        vals.add(id + "");
        vals.add(s);
        this.traitids = Integer.parseInt(DataManager.runFunction("create_subset", vals));
    }

    /**
     * Creates a TraitSubset without a database instance
     */
    TraitSubset(TraitSet ts, ArrayList<Integer> ids)
    {
        this.traitSetId = ts.getId();
        this.idlist = ids;
    }

    /**
     * Creates a traitsubset from an id and database name.
     */
    TraitSubset(TraitSet ts, int id, String name, int traitlist, int golist)
    {
        this.traitSetId = ts.getId();
        this.name = name;
        this.id = id;
        this.traitids = traitlist;
        this.golist = golist;
    }

    /**
     * Deletes this traitsubset from memory (Nothing needs to be done since
     * it is only stored in memory)
     * @return
     */
    public boolean delete()
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("id=" + golist);
        /*DataManager.deleteQuery("golist", where);
        where.clear();
        where.add("id=" + traitids);
        DataManager.deleteQuery("traitlist", where);
        where.clear();*/
        where.add("id=" + this.id);
        DataManager.deleteQuery("traitsubset", where);
        return true;
    }

    /**
     * Returns the list of database ids that make up this traitsubset.
     * @return
     */
    public ArrayList<Integer> getIndeces()
    {
        if (idlist != null)
        {
            return idlist;
        }

        ArrayList<Integer> res = new ArrayList<Integer>();
        ArrayList<String> where = new ArrayList<String>();
        where.add("id=" + this.traitids);
        String list = (String)DataManager.runSelectQuery("list", "traitlist", true, where, null).get(0);
        list = list.replace(",", " ");
        list = list.trim();
        String[] items = list.split(" ");
        for (String s : items)
        {
            if (!s.equals(""))
            {
                res.add(Integer.parseInt(s));
            }
        }

        return res;
    }

    /**
     * Returns the id of the traitset that this traitsubset belongs to.
     * @return
     */
    public int getTraitSetId()
    {
        return this.traitSetId;
    }

    /**
     * Returns the name of this traitsubset.
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * Renames this traitsubset. 
     * @param newName
     */
    public void rename(String newName)
    {
        this.name = newName;
        ArrayList<String> where = new ArrayList<String>();
        where.add("id=" + id);
        DataManager.runUpdateQuery("traitsubset", "name", name, where);
    }

    /**
     * Updates the database so that the results of the BiNGO analysis will
     * be available every time that this subset is loaded to view. 
     * @param stringRepresentationOfGoResults
     */
    public void updateGo(String stringRepresentationOfGoResults)
    {
        ArrayList<String> args = new ArrayList<String>();
        args.add(this.id + "");
        args.add(stringRepresentationOfGoResults);
        try
        {
            this.golist = Integer.parseInt(DataManager.runFunction("insert_go_list_subset", args));
        }
        catch (Exception e)
        {
            golist = -1;
        }
    }

    /**
     * Returns the golist id for the go list belonging to this traitsbuset.
     * @return
     */
    public int getGoList()
    {
        return this.golist;
    }

    public ArrayList<GoItems> getGoItems()
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("id=" + golist);
        try
        {
            String list = (String)DataManager.runSelectQuery("list", "golist", true, where, null).get(0);

            ArrayList<GoItems> toret = new ArrayList<GoItems>();
            list = list.replace(",", "\t");
            String[] items = list.split("\t");
            for (String s : items)
            {
                if (s.length() > 1)
                {
                    toret.add(GoItems.parse(s));
                }
            }
            return toret;
        }
        catch (Exception e) //no go items b/c go analysis not run
        {
            return new ArrayList<GoItems>();
        }
    }

    public void runGOAnalysis(GOFrame go, TraitSet t)
    {
        go.setParameters(.05 + "", "FDR", "Hyper", "full");

        //get traits
        HashSet<String> traitname = new HashSet<String>();
        for (Integer i : this.getIndeces())
        {
            traitname.add(t.getTrait(i).getName().toUpperCase());
        }

        //run go enrichments
        go.autoPerformAnalysis(traitname);
        //insert results into database.
        BiNGOresults2GenAMap res = go.getResults();
        if (res != null)
        {
            ArrayList<String> where = new ArrayList<String>();
            this.updateGo(res.getStringRepresentationOfGoResults());
        }
        else
        {
            golist = 0;
        }
    }
}
