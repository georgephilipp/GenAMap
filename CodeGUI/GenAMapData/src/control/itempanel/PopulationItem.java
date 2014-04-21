package control.itempanel;

import algorithm.AlgorithmView;
import algorithm.StructureParameterObject;
import control.DataAddRemoveHandler;
import datamodel.MarkerSet;
import datamodel.Model;
import datamodel.Population;
import realdata.DataManager;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.swing.JFrame;
import java.util.HashMap;
import java.util.HashSet;
import control.TableParser;
import javax.swing.JOptionPane;

/**
 * This will load a population structure into the database. This should be a
 * very simple process to just upload on data point for each individual ...
 * @author rcurtis
 * @author akgoyal
 */
public class PopulationItem extends ThreadItem
{

    /**
     * The current status of the upload
     */
    private String status = "In queue ...";
    /**
     * The last error, if any
     */
    private String errorText = "";
    /**
     * The form to call repaint on. 
     */
    private JFrame form;
    /**
     * The name of the population we are uploading
     */
    private String name;
    /**
     * The population structure id from the database. 
     */
    private int popstructid;
    /**
     * The id of the markerset that htis pop belongs to
     */
    private int markersetid;
    /**
     * The id of the project that this pop belongs to
     */
    private int projid;
    /**
     * The file that contains the population stuff.
     */
    private String filename;

    public PopulationItem(int markersetid, int projectid, String filename, String name, JFrame form)
    {
        this.form = form;
        this.markersetid = markersetid;
        this.projid = projectid;
        this.filename = filename;
        this.name = name;
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
        return name + " has been loaded to the database.";
    }

    @Override
    public void start()
    {
        Task t = new Task();
        t.start();
    }

    private String makeOrBlock(ArrayList<String> vals, String field, boolean isString)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for(int i=0;i<vals.size();i++)
        {
            builder.append(field);
            builder.append("=");
            if(isString)
                builder.append("\"");
            builder.append(vals.get(i));
            if(isString)
                builder.append("\"");
            if(i+1 != vals.size())
                builder.append(" OR ");
            else
                builder.append(")");
        }  
        return builder.toString();
    }
    
    class Task extends Thread
    {

        @Override
        public void run()
        {
            String popstructid = null;
            
            try
            {
                setValue(1);
                status = "Retrieving samples for markerset ...";
                form.repaint();

                String sampleNameQuery = "SELECT DISTINCT sampleid FROM markerval" + DataManager.getTeamCode() + ",marker WHERE markerid=marker.id AND markersetid=" + markersetid + " ORDER BY sampleid;";
                ArrayList<String> sampleIds = DataManager.runQuery(sampleNameQuery);
                int numSamples = sampleIds.size();
                ArrayList<String> whereArgs = new ArrayList();
                whereArgs.add(makeOrBlock(sampleIds, "id", false));
                ArrayList<String> sampleNames = DataManager.runSelectQuery("name", "sample", true, whereArgs, "id");
                HashMap<String, String> nameToIdMap = new HashMap();

                if(sampleNames.size() != numSamples)
                {
                    JOptionPane.showMessageDialog(null, "Error while retrieving sample IDs.\n This is a bug. Please contact the developers.");
                    errorText = "Error retrieving sample IDs";
                    setIsError(true);
                    form.repaint();
                    return;
                }

                for(int i=0;i<numSamples;i++)
                {
                    nameToIdMap.put(sampleNames.get(i), sampleIds.get(i));
                }
                
                setValue(30);
                status = "Checking and loading data file ...";
                form.repaint();

                TableParser tparser = new TableParser();
                tparser.colTypes.add("special:0");
                tparser.colTypes.add("posInt");
                ArrayList<Integer> sampleKey = new ArrayList();
                sampleKey.add(0);
                tparser.keys.add(sampleKey);
                tparser.delimiter = "Tab";
                tparser.length = numSamples;
                tparser.regSets.add(new HashSet(sampleNames));
                tparser.setup(filename);

                HashSet<Integer> clusters = new HashSet();
                ArrayList<String> line = new ArrayList();
                ArrayList<ArrayList<String>> allVals = new ArrayList();
                int maxCluster = 0;
                
                sampleIds = null;
                sampleNames = null;
                double cursor = 0;
                
                while(true)
                {
                    cursor = cursor + 1;
                    line = tparser.readline();
                    if(line.size() == 1)
                    {
                        JOptionPane.showMessageDialog(null, "Error while checking the data file.\n This is likely caused by in incorrectly formatted input file.\n Error message was:\n" + line.get(0));
                        errorText = "Error while checking data file";
                        setIsError(true);
                        form.repaint();
                        return;
                    }
                    if(line.get(1) == null)
                        break;
                    ArrayList<String> vals = new ArrayList();
                    vals.add(nameToIdMap.get(line.get(1)));
                    vals.add(line.get(2));
                    allVals.add(vals);
                    int cluster = Integer.parseInt(line.get(2));
                    if(cluster > maxCluster)
                        maxCluster = cluster;
                    clusters.add(cluster);
                    setValue(30 + (int)(40.0*cursor/((double)numSamples)));
                    form.repaint();
                }
                
                tparser = null;
                
                for(int i=1;i<=maxCluster;i++)
                {
                    if(!clusters.contains(i))
                    {
                        JOptionPane.showMessageDialog(null, "Error while checking the data file.\n Cluster " + i + " was missing.");
                        errorText = "Populations were not specified correctly";
                        setIsError(true);
                        form.repaint();
                        return;
                    }
                }
                
                setValue(70);
                status = "Inserting data into DB ...";
                form.repaint();
                
                ArrayList<String> cols = new ArrayList();
                cols.add("markersetid");
                cols.add("name");
                ArrayList<String> vals = new ArrayList();
                vals.add("" + markersetid);
                vals.add(name);
                
                if (!DataManager.runInsertQuery(cols, vals, "popstruct"))
                {
                    throw new RuntimeException("failed to insert popstruct name into DB");
                }
                
                whereArgs.clear();
                whereArgs.add("markersetid=" + markersetid);
                whereArgs.add("name=\"" + name + "\"");
                popstructid = (String)DataManager.runSelectQuery("id", "popstruct", true, whereArgs, null).get(0);
                
                for(int i=0;i<numSamples;i++)
                    allVals.get(i).add(popstructid);
                cols.clear();
                cols.add("sampleid");
                cols.add("pop1");
                cols.add("popstructid");
                
                if (!DataManager.runMultipleInsertQuery(cols, allVals, "structure"))
                {
                    throw new RuntimeException("failed to insert values into DB");
                }
                
                setValue(99);
                status = "Finishing up ...";
                form.repaint();
                
                MarkerSet ms = Model.getInstance().getProject(projid).getMarker(markersetid);
                ms.getPopulations().add(new Population(ms, name, Integer.parseInt(popstructid)));
                ArrayList<String> where = new ArrayList<String>();
                where.add("markersetid=" + markersetid);
                DataManager.runUpdateQuery("popstruct", "loadcmpt", "1", where);
                DataAddRemoveHandler.getInstance().refreshDisplay();
            }
            catch(Exception e)
            {
                if(popstructid != null)
                {
                    ArrayList<String> whereArgs = new ArrayList();
                    whereArgs.add("popstructid=" + popstructid);
                    DataManager.deleteQuery("structure", whereArgs);                    
                }
                ArrayList<String> whereArgs = new ArrayList();
                whereArgs.add("markersetid=" + markersetid);
                whereArgs.add("name=\"" + name + "\"");
                DataManager.deleteQuery("popstruct", whereArgs);
                
                JOptionPane.showMessageDialog(null, "Error while uploading the data file into the database.\nThis is a bug. Please contact the developers.\n Error message was: " + e.getMessage());
                errorText = e.getMessage();
                setIsError(true);
                form.repaint();
                return;
            }

            //***StructureParameterObject paramOb = new StructureParameterObject("" + popstructid);
            //***AlgorithmView.getInstance().addAlgorithm("STM", 14, projid, 0, markersetid, paramOb);
            //***setValue(100);
        }
    }
}
