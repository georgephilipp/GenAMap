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

    class Task extends Thread
    {

        @Override
        public void run()
        {
            setValue(5);
            status = "Reading file ...";
            form.repaint();

            try
            {
                ArrayList<String> whereArgs = new ArrayList<String>();

                String strLine;
                int totalrow = 0;
                ArrayList<Integer> filepop = new ArrayList<Integer>();
                ArrayList<String> sampleids = new ArrayList<String>();
                FileInputStream fstream = new FileInputStream(filename);
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                while ((strLine = br.readLine()) != null)
                {
                    strLine = strLine.trim();
                    String[] vals = strLine.split("\t");
                    if (vals.length == 1)
                    {
                        vals = strLine.split(" ");
                    }
                    if (vals.length == 1)
                    {
                        errorText = "File was badly formatted";
                        setIsError(true);
                        return;
                    }

                    String val = "";
                    String sid = "";
                    if (vals.length == 2)
                    {
                        val = vals[1];
                        sid = vals[0];
                    }
                    else
                    {
                        val = vals[2];
                        sid = vals[0] + "_" + vals[1];
                    }

                    filepop.add(Integer.parseInt(val));
                    whereArgs.clear();
                    whereArgs.add("name=\'" + sid + "\'");
                    try
                    {
                        sampleids.add((String)DataManager.runSelectQuery("id", "sample", true, whereArgs, null).get(0));
                    }
                    catch (Exception ex)
                    {
                        errorText = sid + " was not in the database!!";
                        setIsError(true);
                        return;
                    }
                    totalrow++;
                }

                status = "Uploading " + name + " ...";
                form.repaint();

                ArrayList<String> vals = new ArrayList<String>();
                ArrayList<String> cols = new ArrayList<String>();
                cols.add("markersetid");
                vals.add("" + markersetid);
                cols.add("name");
                vals.add(name);
                if (!DataManager.runInsertQuery(cols, vals, "popstruct"))
                {
                    errorText = DataManager.getLastError();
                    setIsError(true);
                    return;
                }
                whereArgs = new ArrayList<String>();
                whereArgs.add("markersetid=" + markersetid);
                whereArgs.add("name='" + name + "'");
                popstructid = Integer.parseInt((String)DataManager.runSelectQuery("id", "popstruct", true, whereArgs, null).get(0));
                whereArgs.clear();

                //insert into structure table
                cols = new ArrayList<String>();
                cols.add("popstructid");
                cols.add("sampleid");
                cols.add("pop1");
                ArrayList<ArrayList<String>> mvals = new ArrayList<ArrayList<String>>();
                for (int p = 0; p < sampleids.size(); p++)
                {
                    vals = new ArrayList<String>();
                    vals.add("" + popstructid);
                    vals.add("" + sampleids.get(p));
                    vals.add("" + filepop.get(p));
                    mvals.add(vals);
                    try
                    {
                        setValue((int) ((double) 92 * ((double) (p + 1) / (double) sampleids.size())) + 5);
                        form.repaint();
                    }
                    catch (Exception e)
                    {
                    }
                }
                DataManager.runMultipleInsertQuery(cols, mvals, "structure");
            }
            catch (Exception ex)
            {
                errorText = ex.getMessage();
                setIsError(true);
                return;
            }

            MarkerSet ms = Model.getInstance().getProject(projid).getMarker(markersetid);
            ms.getPopulations().add(new Population(ms, name, popstructid));
            ArrayList<String> where = new ArrayList<String>();
            where.add("markersetid=" + markersetid);
            DataManager.runUpdateQuery("popstruct", "loadcmpt", "1", where);
            DataAddRemoveHandler.getInstance().refreshDisplay();

            StructureParameterObject paramOb = new StructureParameterObject("" + popstructid);
            AlgorithmView.getInstance().addAlgorithm("STM", 14, projid, 0, markersetid, paramOb);
            setValue(100);
        }
    }
}
