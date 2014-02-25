package control.itempanel;

import control.DataAddRemoveHandler;
import datamodel.Model;
import datamodel.Network;
import datamodel.TraitSet;
import realdata.DataManager;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFrame;

/**
 * The NetworkUpload item will parse a file containing a trait network and
 * upload it into the database.
 * @author RCurtis
 */
public class NetworkUploadItem extends ThreadItem
{
    /**
     * The text of the last error to occur in loading this network
     */
    private String errorText = "";
    /**
     * The current status of the import. 
     */
    private String status = "In queue ...";
    /**
     * The name of the network being imported
     */
    private String name;
    /**
     * The matrix of edge weights in the network for a matrix-type readin
     */
    private double[][] net;
    /**
     * An array of edges for a edge-edge type read in.
     */
    private ArrayList<CytoNet> network;
    /**
     * The trait id that this network will belong to. 
     */
    private int tsid;
    /**
     * The form to call to repaint the visualization of the progress
     */
    private JFrame form;
    /**
     * The id of this project
     */
    private int projID;

    /**
     * Constructor to upload a edge-edge formatted file
     * @param name name of the loaded network
     * @param net the edges of the network
     * @param tsid the traitset id
     */
    public NetworkUploadItem(JFrame form, String name, ArrayList<CytoNet> net, int tsid, int projID)
    {
        this.projID = projID;
        this.form = form;
        this.name = name;
        this.tsid = tsid;
        this.network = net;
    }

    /**
     * Constructor to upload a matrix formatted file
     * @param name name of the loaded network
     * @param net the matrix of the edges for the network
     * @param tsid the traitsetid
     */
    public NetworkUploadItem(JFrame form,String name, double[][] net, int tsid, int projID)
    {
        this.projID = projID;
        this.form = form;
        this.name = name;
        this.tsid = tsid;
        this.net = net;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getErrorText()
    {
        return errorText;
    }

    @Override
    public String getStatus()
    {
        return status;
    }

    @Override
    public String getSuccessMessage()
    {
        return name + " has been uploaded.";
    }

    @Override
    public void start()
    {
        Task t = new Task();
        t.start();
    }

    class Task extends Thread
    {

        protected boolean isError;
        @Override
        public void run()
        {
            setValue(0);
            status = "Reading network file ..."; 
            form.repaint();

            String typeidx = "LDD";
            int ix = 0;
            while(true)
            {
                ArrayList<String> whereArgs = new ArrayList<String>();
                whereArgs.add("ts="+tsid);
                whereArgs.add("type=\""+typeidx+"\"");
                if(DataManager.runSelectQuery("type", "network", true, whereArgs, null).size() == 0)
                {
                    break;
                }
                else
                {
                    typeidx = "LD" + (ix++);
                }
                if( ix >= 11)
                {
                    setIsError(true);
                    errorText = "Cannot load any more networks for traitset";
                    return;
                }
            }

            ArrayList<String> args = new ArrayList<String>();
            args.add("" + tsid);
            int netid;
            args.add(typeidx);
            try
            {
                netid = Integer.parseInt(DataManager.runFunction("createNetwork", args));
            }
            catch(Exception e)
            {
                setIsError(true);
                errorText = e.getMessage();
                return;
            }
            args.clear();
            args.add("id=" + netid);
            DataManager.runUpdateQuery("network", "name", name, args);

            if(net != null)
            {
                if (loadNetworkMat(args, netid))
                {
                    return;
                }
            }
            else
            {
                HashMap<String, Integer> traitids = new HashMap<String,Integer>();

                ArrayList<ArrayList<String>> traitVals = new ArrayList<ArrayList<String>>();
                ArrayList<String> cols = new ArrayList<String>();
                cols.add("trait1");
                cols.add("trait2");
                cols.add("netid");
                cols.add("weight");
                for(int i = 0; i < network.size(); i ++)
                {
                    CytoNet cn = network.get(i);
                    setValue((int) (((double)i/(double)network.size())*97.0));
                    form.repaint();
                    int id1 = -1;
                    if(traitids.containsKey(cn.t1))
                        id1 = traitids.get(cn.t1);
                    else
                    {
                        args.clear();
                        args.add("traitsetid="+tsid);
                        args.add("name=\""+cn.t1+"\"");
                        id1 = Integer.parseInt((String)DataManager.
                                runSelectQuery("id", "trait", true, args, null).get(0));
                        traitids.put(cn.t1, id1);
                    }

                    int id2;
                    if(traitids.containsKey(cn.t2))
                    {
                        id2 = traitids.get(cn.t2);
                    }
                    else
                    {
                        args.clear();
                        args.add("traitsetid="+tsid);
                        args.add("name=\""+cn.t2+"\"");
                        id2 = Integer.parseInt((String)DataManager.
                                runSelectQuery("id", "trait", true, args, null).get(0));
                        traitids.put(cn.t2, id2);
                    }

                    ArrayList<String> temp = new ArrayList<String>();
                    temp.add(id1 + "");
                    temp.add(id2 + "");
                    temp.add(netid + "");
                    temp.add(cn.weight+"");
                    traitVals.add(temp);

                    if(traitVals.size() > 2000)
                    {
                        if (!DataManager.runMultipleInsertQuery(cols, traitVals, "networkval"))
                        {
                            setIsError(true);
                            errorText = DataManager.getLastError();
                            return;
                        }
                        traitVals.clear();
                    }
                }

                if(traitVals.size() > 0)
                {
                    if (!DataManager.runMultipleInsertQuery(cols, traitVals, "networkval"))
                    {
                        setIsError(true);
                        errorText = DataManager.getLastError();
                        return;
                    }
                }
            }

            ArrayList<String> where = new ArrayList<String>();
            where.add("id=" + netid);
            DataManager.runUpdateQuery("network", "loadcmpt", "1", where);
            setValue(100);
            form.repaint();
            TraitSet ts = Model.getInstance().getProject(projID).getTrait(tsid);
            ts.addNetwork(new Network(ts, typeidx, name, netid));
            DataAddRemoveHandler.getInstance().refreshDisplay();
        }

        private boolean loadNetworkMat(ArrayList<String> args, int netid) throws NumberFormatException
        {
            args.clear();
            args.add("traitsetid=" + tsid);
            ArrayList<String> traitIdStrings = DataManager.runSelectQuery("id", "trait", true, args, "idx");
            ArrayList<Integer> traitIds = new ArrayList<Integer>();
            for (String idString : traitIdStrings)
            {
                traitIds.add(Integer.parseInt(idString));
            }
            for (int i = 0; i < net.length - 1; i++)
            {
                setValue((int) (((double)i/(double)net.length)*95.0));
                form.repaint();
                ArrayList<String> cols = new ArrayList<String>();
                cols.add("trait1");
                cols.add("trait2");
                cols.add("netid");
                cols.add("weight");
                ArrayList<ArrayList<String>> traitVals = new ArrayList<ArrayList<String>>();
                for (int j = i + 1; j < net.length; j++)
                {
                    ArrayList<String> temp = new ArrayList<String>();
                    temp.add(traitIds.get(i) + "");
                    temp.add(traitIds.get(j) + "");
                    temp.add(netid + "");
                    temp.add(net[i][j] + "");
                    traitVals.add(temp);
                }
                if (!DataManager.runMultipleInsertQuery(cols, traitVals, "networkval"))
                {
                    setIsError(true);
                    errorText = DataManager.getLastError();
                    return true;
                }
                cols.clear();
            }
            return false;
        }
    }
}
