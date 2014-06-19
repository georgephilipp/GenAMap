package algorithm;

import ui.IOFilesForIOLasso;
import datamodel.Network;
import datamodel.Population;
import java.util.ArrayList;

/**
 * This is an object that we use to pass parameters to methods that are going
 * to reun on the database. To this point, we haven't developed the necessary
 * steps to be able to pass parameters back and forth between the db and the
 * main process.
 *
 * However, all of the pieces are in place at this point if we want to find
 * the way to create an input dialog from the users and change the front ends
 * of the server processes to receive parameters.
 *
 * For now, we just use this class to pass the network id back and forth between
 * the data model and the algoriths, which puts it out on the server.
 * @author rcurtis
 */
public class AssociationParameterObject extends algorithm.ParameterObject
{
    private Network network;
    private Network network2;
    private String name;
    private Population population;
    private String pop_number;
    private int t2id;
    private int associd;
    private IOFilesForIOLasso ioffiol;

    /**
     * A parameter object used to pass the network id to the server for an
     * association run, especially the GFlasso.
     * @param n the network object that has the id
     * @param nm the name of the new association set.
     * @param p the population object that has the id
     * @param p_n the population numbers to consider (for population generated from structure)
     */
    public AssociationParameterObject(Network n, String nm, Population p, String p_n, IOFilesForIOLasso ioffio)
    {
        this.network = n;
        name = nm;
        this.population = p;
        this.pop_number = p_n;
        this.ioffiol = ioffio;
    }

    /**
     * A parameter object used to pass the network id to the server for an
     * association run, especially the GFlasso.
     * @param n the network object that has the id
     * @param nm the name of the new association set.
     */
    public AssociationParameterObject(Network n, String nm)
    {
        this.network = n;
        name = nm;

    }

    @Override
    public ArrayList<String> getFiles()
    {
        if(this.ioffiol != null)
        {
            ArrayList<String> toret = new ArrayList<String>();
            toret.add(ioffiol.getInputFilePath());
            toret.add(ioffiol.getOutputFilePath());
            return toret;
        }
        return null;
    }

    public AssociationParameterObject(Network net, String text, Network net2, int i, int associd)
    {
        this.network = net;
        this.name = text;
        this.network2 = net2;
        this.t2id = i;
        this.associd = associd;
    }

    /**
     * Returns the network so its id can be passed to the server.
     * @return
     */
    public Network getNetwork()
    {
        return network;
    }

    /**
     * Returns the name that the new association data will be called.
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * Formats the parameters into a server-readable format. This is then
     * passed to the front end as paramters and dealt with there.
     * @return
     */
    public String getParms()
    {
        if (network2 != null)
        {
            return name + " " + network.getId() + " " + network2.getId() + " " + t2id + " " + associd;
        }
        else if (network != null)
        {
            return name + " " + network.getId();
        }
        else if (population != null)
        {
            return name + " " + population.getId() + " " + pop_number;
        }

        return name + " 0";
    }
}
