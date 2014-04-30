package datamodel;

/**
 * A class that stores an edge between two nodes - often two nodes are traits,
 * but can also be indices at a lower resolution representing a large collection
 * of traits. This class is used primarily for drawing images, it lets us
 * know what pixels to color. 
 *
 * Additionally, this can store a mapping between a marker and a trait
 * 
 * @author rcurtis
 */
public class Edge
{

    /**
     * The first trait in the connection - there will always be one trait
     */
    private Trait t1;
    /**
     * A marker that connects to the trait when t2 is not used
     */
    private Marker m1;
    /**
     * A trait that connects to t1 when m1 is not used.
     */
    private Trait t2;
    /**
     * The weight of the connection. 
     */
    public double weight;
    /**
     * When trait is not used at all, we just can use idx values
     * to define an edge
     */
    private int idx1;
    /**
     * When trait is not used at all, we just can use idx values
     * to define an edge
     */
    private int idx2;

    /**
     * Constructor for an edge between two traits.
     * @param t1
     * @param t2
     * @param weight
     */
    public Edge(Trait t1, Trait t2, double weight)
    {
        this.t1 = t1;
        this.t2 = t2;
        this.weight = weight;
    }

    /**
     * Constructor for an edge between two unknown quantities.
     * @param ix1 unknown quantity #1
     * @param ix2 unknown quantity #2
     * @param weight
     */
    public Edge(int ix1, int ix2, double weight)
    {
        idx1 = ix1;
        idx2 = ix2;
        this.weight = weight;
    }

    /**
     * Constructor for an edge between a marker and a trait.
     * @param m1
     * @param t2
     * @param weight
     */
    public Edge(Marker m1, Trait t2, double weight)
    {
        this.m1 = m1;
        this.t2 = t2;
        this.weight = weight;
    }

    /**
     * Returns the index of t1 to show where it is in the list
     * @return
     */
    public int getT1Idx()
    {
        if (t1 != null)
        {
            return t1.getIdx();
        }
        if (m1 != null)
        {
            return m1.getIdx();
        }
        return idx1;
    }

    /**
     * Returns the index of T2 to show where it is in the list
     * @return
     */
    public int getT2Idx()
    {
        if (t2 != null)
        {
            return t2.getIdx();
        }
        return idx2;
    }

    /**
     * Returns the marker in this edge
     * @return
     */
    public Marker getM1()
    {
        return m1;
    }

    /**
     * Returns the trait in this edge
     * @return
     */
    public Trait getT1()
    {
        return t1;
    }

    /**
     * Returns the other trait in this edge
     * @return
     */
    public Trait getT2()
    {
        return t2;
    }

    /**
     * Returns the weight of this edge.
     * @return
     */
    public double getWeight()
    {
        return weight;
    }
}
