package datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import realdata.DataManager;

/**
 * In a project, the basic data comes in two different data types - traits and markers
 * Individuals, or samples, should have a valid entry for each traitset and markerset
 * to which they belong. A sample represents one entity with a genomic sequence and
 * values for measured phenotypes.
 *
 * @author Shirdoo
 */
public class Sample implements Serializable
{
    /**
     * The name or id of the sample as defined by the input data.
     */
    private String name;
    /**
     * The database id.
     */
    private int id;
    /**
     * The index of the sample in the input file. For files without sample labels, this
     * must be identical across marker and trait files for them to match up.
     */
    private int idx;
    /**
     * The database id of the project that this sample belongs to.
     */
    private int projectId;
    /**
     * An array list pointing to the markers that this sample has values for
     * This field is used on import, when reading through a file to insert into the db.
     */
    private ArrayList<Marker> marker;
    /**
     * An array list pointing to the traits that this sample has values for.
     * This field is used on import, when reading through a file to put into the db.
     */
    private ArrayList<Trait> trait;

    /**
     * Constructor that reads in sample information from the database and creates
     * the model object representing the database values.
     * @param id The id of the sample to query the database for.
     */
    public Sample(int id)
    {
        ArrayList<String> whereArgs = new ArrayList<String>();
        whereArgs.add("id=" + id);
        ArrayList<String> cols = new ArrayList<String>();
        cols.add("name");
        cols.add("idx");
        cols.add("projectid");
        ArrayList<HashMap<String, String>> sampleMaps = DataManager.runMultiColSelectQuery(cols, "sample", true, whereArgs, null);
        for ( HashMap<String, String> sampleMap : sampleMaps)
        {
            this.name = sampleMap.get("name");
            this.idx = Integer.parseInt(sampleMap.get("idx"));
            this.projectId = Integer.parseInt(sampleMap.get("projectid"));
        }

    }

    /**
     * Determines if the passed in sample is equal to the sample object
     * @param s A sample object
     * @return true if the sample ids match.
     */
    public boolean equals(Sample s)
    {
        if(this.id == s.id)
            return true;
        return false;
    }


    /**
     * Returns the name or id of the sample that was used to characterize the sample
     * in the input data.
     * @return the sample name/id as given by the input data.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Non database constructor that is used to create a sample object that is not
     * yet in the database. This would be used while reading in a file to put into the
     * database.
     * @param name the name of the sample.
     */
    public Sample(String name)
    {
        this.name = name;
        marker = new ArrayList<Marker>();
        trait = new ArrayList<Trait>();
    }

    /**
     * Adds a marker the the sample list of pointers for markers
     * @param m the marker to add.
     */
    public void addMarker(Marker m)
    {
        this.marker.add(m);
    }

    /**
     * Adds a trait to the sample's list of pointers for traits.
     * @param t the trait to add.
     */
    public void addTrait(Trait t)
    {
        this.trait.add(t);
    }
}
