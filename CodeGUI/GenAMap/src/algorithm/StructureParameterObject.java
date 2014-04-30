package algorithm;

/**
 * When running a structure algorithm, we need to know the name of the
 * generated structure. This class holds this data so that it can be
 * passed to the database. It is an implementation of the Parameter object
 * and can be extended to hold other parameters if we choose to do so. 
 *
 * @author akgoyal
 */
public class StructureParameterObject extends algorithm.ParameterObject
{
    private String name;

    /**
     * Creates the object, complete with the name that will be given to the new
     * structure piece. 
     * @param nm
     */
    public StructureParameterObject( String nm)
    {
        name = nm;
    }

    /**
     * Returns the name that is held by this object. 
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the parameter list in a readable format for the GenAMap service
     * that is running on teh server side. 
     * @return
     */
    public String getParms()
    {
            return name+" ";
    }
}
