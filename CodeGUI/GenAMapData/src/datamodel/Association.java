package datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import realdata.DataManager;

/**
 * Association is a connection between a marker and a trait. Instead of
 * using the Edge class, we use this class when we are not drawing images.
 * This is more consistent with the database. We also use this class to read
 * in associationsets
 * @author Shirdoo
 */
public class Association implements Serializable
{
    /**
     * The Marker object of this Association
     */
    private Marker marker;
    /**
     * The weight of the connection between the trait and the marker
     */
    private double value;
    /**
     * The id of the marker in this association
     */
    private int markerid;
    /**
     * The trait id of the trait in this association
     */
    private int traitid;
    /**
     * The population number that this association belongs to.
     */
    private int popNo;

    /**
     * Creates a simple association object that has a pointer to a marker,
     * a trait, and a value between them.
     * @param marker The marker's id
     * @param trait The trait's id
     * @param value The weight between them
     */
    public Association(int marker, int trait, double value, int popNo)
    {
        this.markerid = marker;
        this.traitid = trait;
        if(value == 0)
            value = 1e-50;
        this.value = value;
        this.popNo = popNo;
    }

    public int getPopNo()
    {
        return this.popNo;
    }

    /**
     * Returns teh trait id of this association
     * @return
     */
    public int getTraitId()
    {
        return traitid;
    }

    /**
     * Returns the marker id of this association
     * @return
     */
    public int getMarkerId()
    {
        return markerid;
    }

    /**
     * Queries and finds the marker object that belongs to this association.
     * @return
     */
    public Marker getMarker()
    {
        if(marker == null)
        {
            marker = Marker.getMarker(markerid);
        }
        return marker;
    }

    /**
     * Returns the weight of the association between the marker and the trait. 
     * @return
     */
    public double getValue()
    {
        return value;
    }
}
