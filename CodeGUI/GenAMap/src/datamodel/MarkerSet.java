package datamodel;

import control.itempanel.DeletionItem;
import control.itempanel.ThreadingItemFrame;
import datamodel.Model.ParameterSet;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import static javax.swing.WindowConstants.HIDE_ON_CLOSE;
import realdata.DataManager;

/**
 * Markerset is the other type of data that exists in GenAMap. This is important
 * to hold a collection of markers, which have values on a per-sample basis.
 *
 * This class is, like most others, a pointer directly to the database, which
 * then is able to monitor and control the values from the database. 
 * @author Shirdoo
 */
public class MarkerSet implements Serializable
{

    /**
     * The database id of this markerset.
     */
    private int id;
    /**
     * The name of this markerset
     */
    private String name;
    /**
     * The project taht this markerset belongs to.
     */
    private int projectId;
    /**
     * The name of the project that this markerset belongs to.
     */
    private String projectName;
    /**
     * An collection of the markers that exist in this markerset
     */
    private ArrayList<Marker> markers;
    /**
     * A collection of the samples that exist in this markerset.
     */
    private ArrayList<Sample> samples;
    /**
     * The total number of markers that are in this markerset.
     */
    private int numMarkers = -1;
    /**
     * A collection of populations that belong to this markerset.
     */
    private ArrayList<Population> pops;

    /**
     * Creates a new markerset instance to shadow the database instance
     * @param id the id of the markerset in memory
     * @param proj a pointer to the project that this markerset belongs to
     * @param name the name of this markerset.
     */
    public MarkerSet(int id, Project proj, String name)
    {
        this.id = id;
        this.name = name;
        this.projectId = proj.getId();
        this.projectName = proj.getName();
        this.pops = new ArrayList<Population>();
    }

    /**
     * dummy constructor that just creates and empty markerset object with the
     * given name. 
     * @param name
     */
    public MarkerSet(String name)
    {
        this.name = name;
    }

    /**
     * Returns true if this dataset has data in the database
     * @return
     */
    public boolean hasData()
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("id = " + id);
        return DataManager.runSelectQuery("hasdata", "markerset", true, where, null).get(0).equals("1");
    }

    /**
     * Returns the name of this markerset
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the number of markers that are in this markerset
     * @return
     */
    public int getNumMarkers()
    {
        return numMarkers;
    }

    /**
     * Returns the project name of this markerset
     * @return
     */
    public String getProjectName()
    {
        return projectName;
    }

    /**
     * Returns a collection of the population structures that belong to this
     * markerset
     * @return
     */
    public ArrayList<Population> getPopulations()
    {
        return pops;
    }

    /**
     * Loads the markers that belong to this markerset into memory. This is
     * only done on a by-need basis to avoid filling the memory.
     */
    private void loadMarkers()
    {
        this.markers = new ArrayList<Marker>();
        markers.clear();

        ArrayList<String> cols = new ArrayList<String>();
        cols.add("id");
        cols.add("idx");
        cols.add("chr");
        cols.add("locus");
        cols.add("name");
        ArrayList<String> whereArgs = new ArrayList<String>();
        whereArgs.add("markersetid=" + id);
        ArrayList<HashMap<String, String>> markerMap = DataManager.runMultiColSelectQuery(cols, "marker", true, whereArgs, "id");

        for (HashMap<String, String> m : markerMap)
        {
            markers.add(new Marker(Integer.parseInt(m.get("id")), Integer.parseInt(m.get("idx")),
                    id, Integer.parseInt(m.get("chr")), Integer.parseInt(m.get("locus")), m.get("name")));
        }
    }

    /**
     * Clears the markers to avoid continued space in memroy when we are not
     * using them.
     */
    public void closeMarkers()
    {
        if (markers != null)
        {
            this.markers.clear();
        }
    }

    /**
     * Returns the number of chromosomes associated with this markerset.
     * @return
     */
    public int getNumChr()
    {

        if (markers == null)
        {
            loadMarkers();
        }
        int[] counts = new int[50];
        for (int i = 0; i < markers.size(); i++)
        {
            counts[markers.get(i).getChromosome()] = 1;
        }

        int tot = 0;
        for (int i = 0; i < 50; i++)
        {
            if (counts[i] > 0)
            {
                tot = i;
            }
        }
        return tot;
    }

    /**
     * Returns a collection of markers that are located on a chromosome.
     * @param chr
     * @return
     */
    public ArrayList<Marker> getMarkersAtChr(int chr)
    {
        if (markers == null)
        {
            loadMarkers();
        }
        ArrayList<Marker> toRet = new ArrayList<Marker>();
        for (int i = 0; i < markers.size(); i++)
        {
            if (markers.get(i).getChromosome() == chr)
            {
                toRet.add(markers.get(i));
            }
        }

        Collections.sort(toRet, new Comparator()
        {

            public int compare(Object o1, Object o2)
            {
                Marker m1 = (Marker) o1;
                Marker m2 = (Marker) o2;
                if (m1.getLocus() > m2.getLocus())
                {
                    return 1;
                }
                if (m1.getLocus() < m2.getLocus())
                {
                    return -1;
                }
                return 0;
            }
        });

        return toRet;
    }

    /**
     * Deletes the markerset from memory.
     * @param j the owner of the deletion dialog
     * @param b Whether or not this markerset deletion should close the window
     * automatically or wait for user response.
     * @return
     */
    public boolean delete()
    {
        //delete individual markers
        closeMarkers();

        ThreadingItemFrame tif = ThreadingItemFrame.getInstance();
        DeletionItem di = new DeletionItem(tif, this.projectId,
                DeletionItem.DELETE_MARKERSET, this.name, this);
        tif.addToThreadList(di);
        tif.setVisible(true);
        tif.setDefaultCloseOperation(HIDE_ON_CLOSE);

        return true;
    }

    /**
     * Returns a collection of all markers in this markerset.
     * @return
     */
    public ArrayList<Marker> getMarkers()
    {
        if (markers == null || markers.size() == 0)
        {
            loadMarkers();
        }
        return markers;
    }

    /**
     * Returns the marker that has the specified marker id
     * @param markerId the id of the marker to retrieve.
     * @return
     */
    public Marker getMarker(int markerId)
    {
        if (markers == null)
        {
            loadMarkers();
        }
        for (Marker marker : markers)
        {
            if (marker.getId() == markerId)
            {
                return marker;
            }
        }
        return null;
    }

    /**
     * Returns the id of this markerset.
     * @return
     */
    public int getId()
    {
        return this.id;
    }

    /**
     * Returns a collection of all markerids that are in this markerset and belong
     * to this chromosome
     * @return
     */
    public ArrayList<Integer> getMarkerIds(int chr)
    {
        if (markers == null)
        {
            loadMarkers();
        }
        ArrayList<Integer> ret = new ArrayList<Integer>();
        if (chr == -1)
        {
            for (Marker m : markers)
            {
                ret.add(m.getId());
            }
        }
        else
        {
            for(Marker m : markers)
            {
                if(m.getChromosome() == chr)
                {
                    ret.add(m.getId());
                }
            }
        }
        return ret;

    }

    /**
     * Returns a collection of markerids that are in this markerset
     * @return
     */
    public ArrayList<Integer> getMarkerIds()
    {
        return getMarkerIds(-1);
    }

    /**
     * Returns all samples that are in this markerset.
     * @return
     */
    public ArrayList<Sample> getSamples()
    {
        if (samples == null || samples.size() == 0)
        {
            if (markers == null)
            {
                loadMarkers();
            }
            int mid = this.markers.get(0).getId();

            ArrayList<String> whereArgs = new ArrayList<String>();
            whereArgs.add("markerid=" + mid);
            ArrayList<String> res = DataManager.runSelectQuery("sampleid", "markerval", true, whereArgs, "sampleid");
            this.samples = new ArrayList<Sample>();
            for (String s : res)
            {
                this.samples.add(new Sample(Integer.parseInt(s)));
            }
        }

        return samples;
    }

    /**
     * Returns the number of samples involved in this markerset. 
     * @return
     */
    public int getNumSamples()
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("markersetid=" + this.id);
        int minid = Integer.parseInt((String)DataManager.runSelectQuery("min(id)", "marker", true, where, null).get(0));
        where.clear();
        where.add("markerid=" + minid);
        return Integer.parseInt((String)DataManager.runSelectQuery("count(*)", "markerval", true, where, null).get(0));
    }

    /**
     * When the program closes, it sets stored data to null to avoid
     * unnecessary serialization. 
     */
    void dropByNeed()
    {
        samples = null;
        markers = null;
    }

    /**
     * Renames this markerset to a new name
     * @param newName
     */
    public void rename(String newName)
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("id = " + id);
        DataManager.runUpdateQuery("markerset", "name", newName, where);
        this.name = newName;
    }

    /**
     * Returns the population with the given name.
     * @param name the name of the population to find
     * @return
     */
    public Population getPopulation(String name)
    {
        for (int i = 0; i < pops.size(); i++)
        {
            if (pops.get(i).getName().equals(name))
            {
                return pops.get(i);
            }
        }
        return null;
    }

    /**
     * Returns the population with the given name.
     * @param name the name of the population to find
     * @return
     */
    public Population getPopulation(int id)
    {
        for (int i = 0; i < pops.size(); i++)
        {
            if (pops.get(i).getId() == id)
            {
                return pops.get(i);
            }
        }
        return null;
    }

    /**
     * Sets the database and the model so they are in-sync with regards
     * to the populations. 
     * @param nets
     */
    public void refreshPops(ArrayList<ParameterSet> nets)
    {
        for (int i = pops.size() - 1; i >= 0; i--)
        {
            boolean cont = false;
            for (ParameterSet q : nets)
            {
                if (q.value_int == pops.get(i).getId())
                {
                    cont = true;
                }
            }
            if (!cont)
            {
                pops.remove(i);
            }
        }

        for (ParameterSet q : nets)
        {
            boolean cont = false;
            for (Population p : pops)
            {
                if (p.getId() == q.value_int)
                {
                    cont = true;
                }
            }
            if (!cont)
            {
                pops.add(new Population(this, q.value_string, q.value_int));
            }
        }
    }

    /**
     * Returns the smallest chromosome number in this dataset.
     * @return
     */
    public int getMinChr()
    {
        if (markers == null)
        {
            loadMarkers();
        }
        int[] counts = new int[50];
        for (int i = 0; i < markers.size(); i++)
        {
            counts[markers.get(i).getChromosome()] = 1;
        }

        int tot = 0;
        for (int i = 0; i < 50; i++)
        {
            if (counts[i] > 0)
            {
                return i;
            }
        }
        return tot;
    }

    /**
     * Remove this population from the database so that it will never be
     * seen again
     * @param popName the population to remove. 
     */
    public void removePop(String popName)
    {
        for (int i = 0; i < pops.size(); i++)
        {
            if (pops.get(i).getName().equals(popName))
            {
                pops.get(i).delete();
                pops.remove(i);
            }
        }
    }

    /**
     * Returns a list of all population names association with this markerset 
     * object
     * @return
     */
    public ArrayList<String> getPopulationNames()
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("markersetid=" + this.id);
        return DataManager.runSelectQuery("name", "popstruct", true, where, null);
    }
}
