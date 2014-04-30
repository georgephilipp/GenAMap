package datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import realdata.DataManager;

/**
 * A markerset is a collection of markers. These markers have a chromosomal
 * position and are referenced by the samples who have values at each position.
 * @author Shirdoo
 */
public class Marker implements Serializable
{
    /**
     * Markers can be loaded from files with an NxJ format
     */
    public static final int NXJ_FORMAT = 0;
    /**
     * Markers can be loaded from files with an NxJ format with no labels
     */
    public static final int NXJNO_FORMAT = 1;
    /**
     * Markers can be loaded from files with an JxN format
     */
    public static final int JXN_FORMAT = 2;
    /**
     * Markers can be loaded from files with an JxN format with no labels
     */
    public static final int JXNNO_FORMAT = 3;

    /**
     * Returns the list of all markers with the given markerset id.
     * @param msid
     * @return
     */
    public static ArrayList<Marker> getMarkers(int msid)
    {
        ArrayList<String> cols = new ArrayList<String>();
        ArrayList<String> whereArgs = new ArrayList<String>();
        ArrayList<Marker> markers = new ArrayList<Marker>();
        cols.add("id");
        cols.add("idx");
        whereArgs.add("markersetid=" + msid);
        ArrayList<HashMap<String, String>> markerList = DataManager.runMultiColSelectQuery(cols, "marker", true, whereArgs, null);
        for (HashMap<String, String> markerVals : markerList)
        {
            int id = Integer.parseInt(markerVals.get("id"));
            int idx = Integer.parseInt(markerVals.get("idx"));
            markers.add(new Marker(id));
        }
        return markers;
    }

    /**
     * Returns all the markerids associated with this markerset
     * @param msid the markerset id to query for. 
     * @return
     */
    public static ArrayList<Integer> getMarkerIds(int msid)
    {
        ArrayList<String> where = new ArrayList<String>();
        ArrayList<Integer> markers = new ArrayList<Integer>();
        where.add("markersetid="+msid);
        ArrayList<String> markerList = DataManager.runSelectQuery("id", "marker", true, where, "id LIMIT 0,40000");
        for(String s : markerList)
        {
            markers.add(Integer.parseInt(s));
        }
        return markers;
    }

    /**
     * Returns a marker object representing the marker with the given id.
     * @param markerid
     * @return
     */
    public static Marker getMarker(int markerid)
    {
        return new Marker(markerid);
    }

    /**
     * The id of this marker instance
     */
    private int id;
    /**
     * The index of this marker in the file that was originally read in.
     */
    private int idx;
    /**
     * The id of the markerset that owns this marker.
     */
    private int markerSetId;
    /**
     * What chromosome this marker belongs to
     */
    private int chr;
    /**
     * The locus of this marker on the chromosome.
     */
    private int locus;
    /**
     * The name of this marker.
     */
    private String name;
    /**
     * The collection of values for this marker (ordered by sample index)
     */
    private ArrayList<Integer> values;
    /**
     * The sample ids that belong to this marker.
     */
    private ArrayList<Sample> sampleID;

    /**
     * The full constructor that makes a marker object
     * @param id the database id of the marker
     * @param idx the index of the marker in the file
     * @param markersetid the markerset id that this marker belongs to
     * @param chr the chromosome that this marker resides on
     * @param locus where on the chromosome this marker is
     * @param name the name of the marker
     */
    public Marker(int id, int idx, int markersetid, int chr, int locus, String name)
    {
        this.id = id;
        this.idx = idx;
        this.markerSetId = markersetid;
        this.chr = chr;
        this.locus = locus;
        this.name = name;
        values = new ArrayList<Integer>();
    }

    /**
     * Marker constructor with just an id. Queries the database for all other
     * information. 
     * @param id
     */
    public Marker(int id)
    {
        ArrayList<String> cols = new ArrayList<String>();
        cols.add("idx");
        cols.add("markersetid");
        cols.add("chr");
        cols.add("locus");
        cols.add("name");
        ArrayList<String> whereArgs = new ArrayList<String>();
        whereArgs.add("id=" + id);
        HashMap<String, String> markerMap = (HashMap<String,String>)DataManager.runMultiColSelectQuery(cols, "marker", true, whereArgs, null).get(0);

        this.id = id;
        this.idx = Integer.parseInt(markerMap.get("idx"));
        this.markerSetId = Integer.parseInt(markerMap.get("markersetid"));
        this.chr = Integer.parseInt(markerMap.get("chr"));
        this.locus = Integer.parseInt(markerMap.get("locus"));
        this.name = markerMap.get("name");
        values = new ArrayList<Integer>();
    }

    /**
     * Non database constructor - creates a marker that doesn't have any
     * database information in it. This is a dataholder - can be used
     * to read in files. Probably will be deprecated.
     * @param chr
     * @param loc
     * @param s
     */
    public Marker(int chr, int loc, String s)
    {
        this.name = s;
        this.chr = chr;
        this.locus = loc;
        values = new ArrayList<Integer>();
        sampleID = new ArrayList<Sample>();
    }

    /**
     * Returns the name of this marker
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the index of this marker
     * @return
     */
    public int getIdx()
    {
        return this.idx;
    }

    /**
     * Determines how many samples there are that reference this marker.
     * @return
     */
    public int size()
    {
        ArrayList<String> whereArgs = new ArrayList<String>();
        whereArgs.add("markerid=" + id);
        return Integer.parseInt((String)DataManager.runSelectQuery("count(*)", "markerval", true, whereArgs, null).get(0));
    }

    /**
     * Returns the features for this marrker
     */
    public ArrayList<Double> getFeature()
    {
        ArrayList<Double> toreturn = new ArrayList<Double>();
        ArrayList<String> whereArgs = new ArrayList<String>();
        whereArgs.add("markerid=" + this.getId());
        ArrayList<String> features = DataManager.runSelectQuery("value", "featureval", true, whereArgs, "featureid");
        for(String f : features)
        {
            toreturn.add(Double.parseDouble(f));
        }

        return toreturn;
    }
    /**
     * Returns the chromosome this marker is on.
     * @return
     */
    public int getChromosome()
    {
        return this.chr;
    }

    /**
     * Returns the locus that this marker belongs to.
     * @return
     */
    public int getLocus()
    {
        return this.locus;
    }

    /**
     * Returns the database id of this marker
     * @return
     */
    public int getId()
    {
        return id;
    }

    /**
     * Returns the id of the markerset that this marker belongs to.
     * @return
     */
    public int getMarkerSetId()
    {
        return this.markerSetId;
    }

    /**
     * Returns a list of all samples that reference this marker.
     * @return
     */
    public ArrayList<Sample> getSamples()
    {
        return sampleID;
    }

    /**
     * Returns all the values that were stored in this marker object.
     * @return
     */
    public ArrayList<Integer> getValues()
    {
        return this.values;
    }

    /**
     * Adds a sample to this marker object. This is used when this object
     * is used to read in a file.
     * @param id
     * @param markerVal
     * @return
     */
    public boolean addSample(Sample id, int markerVal)
    {
        this.values.add(markerVal);
        this.sampleID.add(id);

        return true;
    }


    /**
     * Deletes this marker object so that it is never heard from again.
     */
    public static void delete(int id)
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("markerid = " + id);
        DataManager.deleteQuery("markerval", where);

        where.clear();
        where.add("id = " + id);
        DataManager.deleteQuery("marker", where);
    }
}
