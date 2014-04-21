package control.itempanel;

import control.DataAddRemoveHandler;
import datamodel.Model;
import datamodel.Network;
import datamodel.TraitSet;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import realdata.DataManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.JFrame;
import control.TableParser;
import control.MatrixParser;
import javax.swing.JOptionPane;

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
     * The form to call to repaint the visualization of the progress
     */
    private JFrame form;
    /**
     * The id of this project
     */
    private int projID;
    /**
     * The trait id that this network will belong to. 
     */
    private int tsid;
    /**
     * The name of the network being imported
     */
    private String name;
    /**
     * The matrix of edge weights in the network for a matrix-type readin
     */
    private String fileName;
    /**
     * An array of edges for a edge-edge type read in.
     */
    private boolean isEdgeFormat;

    /**
     * Constructor to upload a edge-edge formatted file
     * @param name name of the loaded network
     * @param tsid the traitset id
     */
    public NetworkUploadItem(
            JFrame form,
            int projID,
            int tsid,
            String name, 
            String fileName,
            boolean isEdgeFormat
            )
    {
        this.form = form;
        this.projID = projID;
        this.tsid = tsid;
        this.name = name;
        this.fileName = fileName;
        this.isEdgeFormat = isEdgeFormat;
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
            //initialize
            ArrayList<String> whereArgs = new ArrayList();
            ArrayList<String> cols = new ArrayList();
            ArrayList<String> args = new ArrayList<String>();
            ArrayList<ArrayList<String> > allVals = new ArrayList();
            TableParser tparser = new TableParser();
            MatrixParser mparser = new MatrixParser();
            int edgeFileSize = -1;
            int netid = -1;
            ArrayList<String> line;
            
            //get traits
            setValue(1);
            status = "Get traits of trait set ...";
            whereArgs.add("traitsetid=" + tsid);
            ArrayList<String> traitIds = DataManager.runSelectQuery("id", "trait", true, whereArgs, "idx");            
            ArrayList<String> traitNames = DataManager.runSelectQuery("name", "trait", true, whereArgs, "idx");  
            whereArgs.clear();
            HashMap<String, String> traitNameMap = new HashMap();
            int numTraits = traitIds.size();
            for(int i=0;i<numTraits;i++)
                traitNameMap.put(traitNames.get(i), traitIds.get(i));            
            
            //Check file
            setValue(2);
            status = "Checking file contents file ..."; 
            form.repaint();
                        
            if(isEdgeFormat)
            {
                tparser.colTypes.add("special:0");
                tparser.colTypes.add("special:0");
                tparser.colTypes.add("float");
                //don't add keys. File might be too large for memory
                tparser.delimiter = "Tab";
                tparser.regSets.add(new HashSet());
                for(int i=0;i<numTraits;i++)
                    tparser.regSets.get(0).add(traitNames.get(i));
                tparser.setup(fileName);
                
                String check = tparser.check();
                
                if(!check.equals(""))
                {
                    JOptionPane.showMessageDialog(null, "There was an error while checking the data file.\n This is likely caused by an incorrectly formatted input file.\n In particular, this might be caused because one of the trait names is invalid.\n The error message was:\n" + check);
                    errorText = "Error while checking data file.";
                    setIsError(true);
                    form.repaint();
                    return; 
                }     
                
                edgeFileSize = tparser.getLineNumber();
                tparser.restart();
            }
            else
            {                
                mparser.width = numTraits;
                mparser.length = numTraits;
                mparser.entryType = "float";
                mparser.delimiter = "Tab";
                mparser.setup(fileName);
                
                String check = mparser.check();
                
                if(!check.equals(""))
                {
                    JOptionPane.showMessageDialog(null, "There was an error while checking the data file.\n This is likely caused by an incorrectly formatted input file.\n The error message was:\n" + check);
                    errorText = "Error while checking data file.";
                    setIsError(true);
                    form.repaint();
                    return; 
                }
                
                mparser.restart();
            }
            
            //Get the network type
            setValue(3);
            status = "Get network type ..."; 
            form.repaint();
            
            String typeidx = "LDD";
            int ix = -1;
            while(true)
            {
                whereArgs.add("ts="+tsid);
                whereArgs.add("type=\""+typeidx+"\"");
                if(DataManager.runSelectQuery("type", "network", true, whereArgs, null).isEmpty())
                {
                    break;
                }
                else
                {
                    whereArgs.clear();
                    ix++;
                    typeidx = "LD" + ix;
                }
                if( ix >= 10)
                {
                    JOptionPane.showMessageDialog(null, "Cannot load any more networks for traitset. Please delete some first.");
                    errorText = "Cannot load any more networks for traitset. Please delete some first.";
                    setIsError(true);
                    form.repaint();
                    return; 
                }
            }
            whereArgs.clear();
            
            //Create the network entry
            setValue(4);
            status = "Creating network ..."; 
            form.repaint();
            
            args.add("" + tsid);
            args.add(typeidx);
            try
            {
                netid = Integer.parseInt(DataManager.runFunction("createNetwork", args));
            }
            catch(Exception e)
            {
                JOptionPane.showMessageDialog(null, "Network creation failed.\n This is a bug. Please contact the developers.\n Error message was:\n" + e.getMessage());
                errorText = e.getMessage();
                setIsError(true);
                form.repaint();
                return; 
            }
            args.clear();
            args.add("id=" + netid);
            DataManager.runUpdateQuery("network", "name", name, args);
            args.clear();

            //Importing values
            setValue(5);
            status = "Importing values ..."; 
            form.repaint();
            
            cols.add("trait1");
            cols.add("trait2");
            cols.add("netid");
            cols.add("weight");            
            
            try
            {
                if(isEdgeFormat)
                {
                    for(int i=0;i<edgeFileSize;i++)
                    {
                        line = tparser.readline();
                        float weight = Float.parseFloat(line.get(3));
                        if(weight != 0)
                        {
                            int id1 = Integer.parseInt(traitNameMap.get(line.get(1)));
                            int id2 = Integer.parseInt(traitNameMap.get(line.get(2)));
                            ArrayList<String> vals = new ArrayList();
                            if(id1 <= id2)
                            {
                                vals.add("" + id1);
                                vals.add("" + id2);
                            }
                            else
                            {
                                vals.add("" + id2);
                                vals.add("" + id1);
                            }
                            vals.add("" + netid);
                            vals.add("" + weight);
                            allVals.add(vals);
                        }
                        if(allVals.size() >= 1000)
                        {
                            if (!DataManager.runMultipleInsertQuery(cols, allVals, "networkval"))
                            {
                                JOptionPane.showMessageDialog(null, "Failed to insert values into database.\n This is a bug. Please contact the developers.\n Error message was:\n" + DataManager.getLastError());
                                errorText = DataManager.getLastError();
                                setIsError(true);
                                form.repaint();
                                return; 
                            }
                            allVals.clear();
                            setValue(5 + (int)(((double)(i+1))*93.0/((double)edgeFileSize)));
                            form.repaint();
                        }
                    }
                    if(allVals.size() > 0)
                    {
                        if (!DataManager.runMultipleInsertQuery(cols, allVals, "networkval"))
                        {
                            throw new RuntimeException(DataManager.getLastError());
                        }
                        allVals.clear();                        
                    }   

                    tparser.restart();
                }
                else
                {
                    for(int i=0;i<numTraits;i++)
                    {
                        line = mparser.readline();
                        for(int j=i+1;j<numTraits;j++)
                        {
                            float weight = Float.parseFloat(line.get(j+1));
                            if(weight != 0)
                            {
                                ArrayList<String> vals = new ArrayList();
                                vals.add(traitIds.get(i));
                                vals.add(traitIds.get(j));
                                vals.add("" + netid);
                                vals.add("" + weight);
                                allVals.add(vals);
                            }
                            if(allVals.size() >= 1000)
                            {
                                if (!DataManager.runMultipleInsertQuery(cols, allVals, "networkval"))
                                {
                                    JOptionPane.showMessageDialog(null, "Failed to insert values into database.\n This is a bug. Please contact the developers.\n Error message was:\n" + DataManager.getLastError());
                                    errorText = DataManager.getLastError();
                                    setIsError(true);
                                    form.repaint();
                                    return;
                                }
                                allVals.clear();
                            }    
                        }   
                        setValue(5 + (int)(((double)(i+1))*93.0/((double)numTraits)));
                        form.repaint();
                    }                
                    if(allVals.size() > 0)
                    {
                        if (!DataManager.runMultipleInsertQuery(cols, allVals, "networkval"))
                        {
                            JOptionPane.showMessageDialog(null, "Failed to insert values into database.\n This is a bug. Please contact the developers.\n Error message was:\n" + DataManager.getLastError());
                            errorText = DataManager.getLastError();
                            setIsError(true);
                            form.repaint();
                            return;
                        }
                        allVals.clear();                        
                    }
                }
            }
            catch(Exception e)
            {
                if(netid != -1)
                {
                    whereArgs.clear();
                    whereArgs.add("netid=" + netid);
                    DataManager.deleteQuery("networkval", whereArgs);
                    whereArgs.clear();
                    whereArgs.add("id=" + netid);
                    DataManager.deleteQuery("network", whereArgs);
                    whereArgs.clear();
                }
                
                JOptionPane.showMessageDialog(null, "Error importing data.\n This is likely caused by in incorrectly formatted input file.\n Error message was:\n" + e.getMessage());
                errorText = e.getMessage();
                setIsError(true);
                form.repaint();
                return;
            }
            
            cols.clear();
            allVals.clear();

            //finishing up
            setValue(99);
            status = "Finishing up ..."; 
            form.repaint();
            
            whereArgs.clear();
            whereArgs.add("id=" + netid);
            DataManager.runUpdateQuery("network", "loadcmpt", "1", whereArgs);
            whereArgs.clear();
            setValue(100);
            form.repaint();
            TraitSet ts = Model.getInstance().getProject(projID).getTrait(tsid);
            ts.addNetwork(new Network(ts, typeidx, name, netid));
            DataAddRemoveHandler.getInstance().refreshDisplay();
        }
    }
}
