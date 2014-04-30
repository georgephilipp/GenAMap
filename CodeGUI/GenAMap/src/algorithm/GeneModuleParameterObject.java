package algorithm;

/**
 * This is an object that we use to pass parameters to methods that are going
 * to run on the database. To this point, we haven't developed the necessary
 * steps to be able to pass parameters back and forth between the db and the
 * main process.
 *
 * However, all of the pieces are in place at this point if we want to find
 * the way to create an input dialog from the users and change the front ends
 * of the server processes to receive parameters.
 *
 * For now, we just use this class to pass the essential parameters back and forth between
 * the data model and the algoriths, which puts it out on the server.
 * @author rcurtis
 */
public class GeneModuleParameterObject extends algorithm.ParameterObject
{
    private int netid;
    private int assocsetid;
    private String goanno;
    private int clusterid;

    /**
     * Creates the parameter object necessary to create the module searching
     * algorithm.
     * @param netid the id of the network that we are working with
     * @param assocsetid the association set id of the association set for eQTL analysis
     * @param clusterid the clusterid of the cluster that we will be utilizing
     * @param goanno the go annotation that we should use to do an enrichment analysis. 
     */
    public GeneModuleParameterObject(int netid, int assocsetid, int clusterid, String goanno)
    {
        this.netid = netid;
        this.assocsetid = assocsetid;
        this.goanno = goanno;
        this.clusterid = clusterid;
    }

   /**
     * Formats the parameters into a server-readable format. This is then
     * passed to the front end as paramters and dealt with there.
     * @return
     */
    public String getParms()
    {
        return netid + " " + assocsetid + " " + clusterid + " " + goanno;
    }
}
