package datamodel;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import realdata.DataManager;

/**
 * A trait is one of the key data forms in GenAMap, representing a phenotype.
 * This class acts to both hold trait informaiton, as well as to help in the
 * reading in of a trait file in order to prepare it for insertion into the remote
 * database.
 *
 * When a file is read, the trait is first created in memory and then inserted into
 * the db, and then removed.
 *
 * However, the Trait class can also act as a data object representing the data from
 * the database. This is how the rest of GenAMap will access the data. 
 * @author Shirdoo
 */
public class Trait implements Serializable
{
    /**
     * A format identifier, representing a trait file that is represented by
     * N individuals on N lines with K traits as K columns. Headers are present
     * in this file.
     */
    public static final int NXK_FORMAT = 0;
    /**
     * A format identifier, representing a trait file that is represented by
     * N individuals on N lines with K traits as K columns. Headers are not present
     * in this file.
     */
    public static final int NXKNO_FORMAT = 1;
    /**
     * A format identifier, representing a trait file that is represented by
     * N individuals in N columns with K traits as K rows. Headers are present
     * in this file.
     */
    public static final int KXN_FORMAT = 2;
    /**
     * A format identifier, representing a trait file that is represented by
     * N individuals in N columns with K traits as K rows. Headers are not present
     * in this file.
     */
    public static final int KXNNO_FORMAT = 3;

    /**
     * Creates a new trait object from the database with the id passed in
     * and returns it. 
     * @param traitid
     * @return
     */
    public static Trait getTrait(int traitid)
    {
        return new Trait(traitid);
    }
    /**
     * The database id for this trait.
     */
    private int id;
    /**
     * The name of this trait as given by the input data.
     */
    private String name;
    /**
     * The index representing the order that this trait was read in the file.
     * This is important because it can be used for sorting, clustering, etc.
     */
    private int idx;
    /**
     * When displaying traits in the matrix view, the cluster index is used
     * to represent the index of the trait assigned by the clustering.
     */
    private int clusterIdx;
    /**
     * The traitset that contains this trait. This is a back pointer.
     */
    private TraitSet ts;
    /**
     * An array list of the samples that contain this trait, listed by database id.
     */
    private ArrayList<Integer> sampleIds;
    /**
     * An array list of the samples that contain this trait, referenced by their dataholding
     * class.
     */
    private ArrayList<Sample> sampleID;
    /**
     * An array list of all fo the values of this trait, in the order of the samples as
     * sorted by index. Each value in the list corresponds to a specific sample.
     */
    private ArrayList<Double> value;
    /**
     * The id of the list that contains the go information for this trait. 
     */
    private int golistid;
    /*
     * The string that is stored in this trait in the db for GO annotation. 
     */
    //private String GoAnnotationInDb;

    /**
     * Considers all of the go annotations currently in the db for this trait and
     * then updates accordingly if there is anything new. 
     * @param gocats
     */
    public void goUpdate(ArrayList<String> gocats)
    {
        if (gocats.size() == 0)
        {
            return;
        }
        ArrayList<String> cur = this.getCurrentGoAnnotation(null);
        boolean isChanged = false;
        String goAnno = GoAnnotationInDb();
        for (int i = 0; i < gocats.size(); i++)
        {
            boolean isIfound = false;
            for (int j = 0; j < cur.size(); j++)
            {
                if (cur.get(j).toLowerCase().equals(goprocess(gocats.get(i).toLowerCase())))
                {
                    isIfound = true;
                }
            }
            if (!isIfound)
            {
                goAnno += goprocess(gocats.get(i)) + ",";
                isChanged = true;
            }
        }
        if (isChanged)
        {
            ArrayList<String> where = new ArrayList<String>();
            where.add("id=" + this.golistid);
            DataManager.runUpdateQuery("golist", "list", goAnno, where);
        }
    }

    private String goprocess(String get)
    {
        get = get.replace("-", "");
        get = get.replace('\'', 'p');
        get = get.replace(",", "");
        return get;
    }
    /**
     * A Comparator object that compares two traits based on their index to determine
     * sorting order by index.
     */
    public static class TraitComparator implements Comparator
    {
        /**
         * Compare two traits by their sorting index value.
         * @param t1 a trait
         * @param t2 another trait
         * @return 0 if the index values of the traits are equal, 1 or -1 if they are not.
         */
        public int compare(Object t1, Object t2)
        {
            int idx1 = ((Trait) t1).idx;
            int idx2 = ((Trait) t2).idx;

            if (idx1 > idx2)
            {
                return 1;
            }
            else if (idx2 > idx1)
            {
                return -1;
            }
            return 0;
        }
    }
    /**
     * A comparator object that compares the two traits based on their index from
     * the clustering currently loading in. This helps to sort based on cluster index.
     */
    public static class TraitClusterComparator implements Comparator
    {
        /**
         * Compare two traits by their cluster index value.
         * @param t1 a trait
         * @param t2 another trait.
         * @return 0 if the clustering of teh traits are equal, 1 or -1 if they are not. 
         */
        public int compare(Object t1, Object t2)
        {
            int idx1 = ((Trait) t1).clusterIdx;
            int idx2 = ((Trait) t2).clusterIdx;

            if (idx1 > idx2)
            {
                return 1;
            }
            else if (idx2 > idx1)
            {
                return -1;
            }
            return 0;
        }
    }

    /**
     * Copy constructor to use when downloading networks - we want the
     * traits to stay the same even when we start jobs with different clusterings.
     * @param t
     */
    public Trait(Trait t)
    {
        this.clusterIdx = t.clusterIdx;
        this.id = t.id;
        this.idx = t.idx;
        this.name = t.name;
        this.ts = t.ts;
    }

    /**
     * Returns a new trait class representative of the data stored in the database
     * for the given id passed in. 
     * @param id the database id to use in order to query for the values of this trait object.
     */
    public Trait(int id)
    {
        this.id = id;

        ArrayList<String> cols = new ArrayList<String>();
        ArrayList<String> whereArgs = new ArrayList<String>();
        cols.add("name");
        cols.add("idx");
        cols.add("traitsetid");
        cols.add("golist");
        whereArgs.add("id=" + id);
        ArrayList<HashMap<String, String>> traitVals = DataManager.runMultiColSelectQuery(cols, "trait", true, whereArgs, null);
        for (HashMap<String, String> traitVal : traitVals)
        {
            this.name = traitVal.get("name");
            this.idx = Integer.parseInt(traitVal.get("idx"));
            this.ts = null;
            this.value = new ArrayList<Double>();
            this.sampleIds = new ArrayList<Integer>();
            this.golistid = Integer.parseInt(traitVal.get("golist"));
        }
    }

    /**
     * Returns a new trait representative of the data stored in the database. However,
     * this trait constructor assumes that the query for the trait information has already been
     * done (probably in a batch query), and so it does not need to query the database
     * and extra time. This avoidance of many queries can help to speed up the overall application. 
     * @param id the id in the database for this trait.
     * @param name the name of this trait as defined by the input data.
     * @param idx the sorting order of this trait
     * @param ts the traitset that this trait belongs to. 
     */
    public Trait(int id, String name, int idx, TraitSet ts, int goList)
    {
        this.id = id;
        this.name = name;
        this.idx = idx;
        //this.traitSetId = ts.getId();
        this.ts = ts;
        this.value = new ArrayList<Double>();
        this.sampleIds = new ArrayList<Integer>();
        this.golistid = goList;
    }

    /**
     * Returns the pointer to the traitset for the trait.
     * @return the traitset pointer.
     */
    public TraitSet getTraitSet()
    {
        return ts;
    }

    /**
     * Returns the name of the trait as read in by the input data.
     * @return the name of the trait as read in by the input data = gene name, or phenotype name, etc
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the database id fro this trait.
     * @return the database id. 
     */
    public int getId()
    {
        return id;
    }

    /**
     * Returns the index of this trait as it was read in from the file.
     * @return the index of the trait as determiend by the file used to read it in.
     */
    public int getIdx()
    {
        return idx;
    }

    /**
     * Returns the current sort index defined by the last clustering applied
     * to the trait set.
     * @return the current sort/cluster index.
     */
    public int getSortIdx()
    {
        return this.clusterIdx;
    }

    /**
     * Sets the cluster index for this trait so that it can be access in subsequent
     * passes through the data - tell the Trait where its sorting order is.
     * @param cidx the position of this trait in the traitset.
     */
    public void setClusterIdx(int cidx)
    {
        this.clusterIdx = cidx;
    }

    /**
     * Returns the list of values for this trait, sorted by sample index.
     * @return the list of values for this trait, sorted by sample index.
     */
    public ArrayList<Double> getValues()
    {
        return value;
    }

    /**
     * Returns the list of sample database ids for this trait.
     * @return the list of sample database ids for this trait.
     */
    public ArrayList<Integer> getSamples()
    {
        return sampleIds;
    }

    /**
     * Deletes all of the traitvals in the database for this trait,
     * and then deletes its own trait entry.
     * @return the success of the delete operation.
     */
    public boolean delete()
    {
        ArrayList<String> whereArgs = new ArrayList<String>();
        whereArgs.add("traitid=" + this.id);
        if (!DataManager.deleteQuery("traitval", whereArgs))
        {
            return false;
        }
        whereArgs.clear();
        whereArgs.add("id=" + this.id);
        if (!DataManager.deleteQuery("trait", whereArgs))
        {
            return false;
        }

        return true;

    }

    /**
     * Returns the list of samples assigned to this trait, probably due to reading in a file
     * @return the list of sample objects for this trait.
     */
    public ArrayList<Sample> getSamplesInMemory()
    {
        return sampleID;
    }

    /**
     * Creates a trait object in memory only which holds a list of values and samples.
     * @param name the name of this trait object.
     */
    public Trait(String name)
    {
        this.name = name;
        value = new ArrayList<Double>();
        sampleID = new ArrayList<Sample>();
    }

    /**
     * Adds a sample to this trait collection.
     * @param id the sample that this trait value belongs to
     * @param traitVal the value of the trait for this sample. 
     * @return true
     */
    public boolean addSample(Sample id, double traitVal)
    {
        this.value.add(traitVal);
        this.sampleID.add(id);

        return true;
    }

    public ArrayList<String> getCurrentGoAnnotation(Map<String, Color> colorMap)
    {
        ArrayList<String> toReturn = new ArrayList<String>();
        String goanno = this.GoAnnotationInDb();
        if (goanno == null || goanno.length() == 0)
        {
            ArrayList<String> args = new ArrayList<String>();
            args.add(id + "");
            this.golistid = Integer.parseInt(DataManager.runFunction("insert_go_list_trait", args));
            return toReturn;
        }
        String[] codes = goanno.split(",");

        if (colorMap != null)
        {
            for (int ci = 0; ci < 23; ci++)
            {
                for (int i = 0; i < codes.length; i++)
                {
                    if (codes[i].length() > 0 && colorMap.get(codes[i]) == Model.colors[ci])
                    {
                        toReturn.add(codes[i]);
                    }
                }
            }

            for(int i = 0; i < codes.length; i ++)
            {
                if(codes[i].length() > 0 && !toReturn.contains(codes[i]))
                {
                    toReturn.add(codes[i]);
                }
            }
        }
        else
        {
            for (int i = 0; i < codes.length; i++)
            {
                if (codes[i].length() > 0)
                {
                    toReturn.add(codes[i]);
                }
            }
        }

        return toReturn;
    }

    /**
     * Query the database to find out what the current db annotation for
     * this trait is - we want to do this on a trait by trait basis because
     * it gets really sluggish if we do it all at once and store it all in memory.
     * @return
     */
    private String GoAnnotationInDb()
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("id = " + this.golistid);
        ArrayList<String> answer = DataManager.runSelectQuery("list", "golist", true, where, null);
        if (answer.size() > 0)
        {
            return answer.get(0);
        }
        return null;
    }
}
