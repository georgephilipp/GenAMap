package datamodel;

/**
 * Is a dummy object that holds two ints. This is used in an update tree
 * function - we create a simple data hash to avoid queries to the db. 
 * @author RCurtis
 */
public class TraitTreeValIntPointers
{
    /**
     * The id of the parent
     */
    public int parentid;
    /**
     * The list id of the node
     */
    public int listid;
    /**
     * The list of traitids
     */
    private String traitids="";

    /**
     * The hash will be called from the id of the node. It will return
     * the parentid and the listid.
     * @param parent
     * @param list
     */
    public TraitTreeValIntPointers(int parent, int list)
    {
        this.parentid = parent;
        this.listid = list;
    }

    /**
     * Adds a trait to the overall list of pointers for this traittreeval. 
     * @param s
     */
    public void addTrait(String s)
    {
        traitids += s + ",";
    }

    @Override
    public String toString()
    {
        return traitids;
    }
}
