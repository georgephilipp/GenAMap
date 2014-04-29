package control.itempanel;

import control.DataAddRemoveHandler;
import datamodel.AssociationSet;
import datamodel.Marker;
import datamodel.MarkerSet;
import datamodel.Model;
import datamodel.Network;
import datamodel.Trait;
import datamodel.TraitSet;
import realdata.DataManager;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.swing.JFrame;
import control.MatrixParser;
import javax.swing.JOptionPane;

/**
 * This will upload a read-in association set into the database. The file is
 * actually parsed and read in this method.
 * @author rcurtis
 */
public class AssociationUploadItem extends ThreadItem
{
    /**
     * the status of the currently executing item
     */
    private String status = "In queue ...";
    /**
     * the text of the last error
     */
    private String errorText = "";
    /**
     * The name of the association being uploaded
     */
    private String name;
    /**
     * The form to call repaint on when the status or percent complete changes
     */
    private JFrame form;
    /**
     * The markerset that this associationset belongs to
     */
    private MarkerSet markerSet;
    /**
     * The traitset that this associationset belongs to
     */
    private TraitSet traitSet;
    /**
     * The id of the project 
     */
    private int projectId;
    /**
     * the network we are working with, if any
     */
    private Network ntwrk;
    /**
     * The name of the file we are reading
     */
    private String fileName;

    /**
     * Creates a new AssociationUploadItem that will run to read the file and
     * insert the association set into the database
     * @param form the form to repaint when status changes
     * @param name the name of the association set
     * @param ts the traitset that we are working with
     * @param ms the markerset that we are working wit h
     * @param projID the id of the project we are working with
     */
    public AssociationUploadItem(JFrame form, String name, TraitSet ts, MarkerSet ms, int projID,
            String filename, Network net)
    {
        this.form = form;
        this.name = name;
        this.traitSet = ts;
        this.markerSet = ms;
        this.projectId = projID;
        this.fileName = filename;
        this.ntwrk = net;
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
        return name + " upload complete.";
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
            String assocSetId = null;
            
            setValue(1);
            status = "Checking file ...";
            form.repaint();
            
            MatrixParser mparser = new MatrixParser();
            mparser.width = traitSet.getNumTraits();
            mparser.length = markerSet.getNumMarkers();
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

            try
            {
                setValue(2);
                status = "Reading file ...";
                form.repaint();
                ArrayList<String> vals = new ArrayList<String>();
                ArrayList<String> cols = new ArrayList<String>();
                ArrayList<String> whereArgs = new ArrayList<String>();
                cols.add("name");
                vals.add(name);
                cols.add("projectid");
                vals.add("" + projectId);
                cols.add("tsid");
                vals.add("" + traitSet.getId());
                cols.add("msid");
                vals.add("" + markerSet.getId());
                if (ntwrk != null)
                {
                    cols.add("netid");
                    vals.add("" + ntwrk.getId());
                }
                if (!DataManager.runInsertQuery(cols, vals, "assocset"))
                {
                    setIsError(true);
                    errorText = DataManager.getLastError();
                    return;
                }
                setValue(5);
                cols.clear();
                vals.clear();
                whereArgs.add("name='" + name + "'");
                whereArgs.add("projectid=" + projectId);
                assocSetId = (String)DataManager.runSelectQuery("id", "assocset", true, whereArgs, null).get(0);
                whereArgs.clear();
                String strLine;
                FileInputStream fstream = new FileInputStream(fileName);
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                cols.add("markerid");
                cols.add("traitid");
                cols.add("value");
                cols.add("assocsetid");
                int row = 0;
                int col = 0;
                int assocCount = 0;
                while ((strLine = br.readLine()) != null)
                {
                    strLine = strLine.trim();
                    String[] ln = strLine.split(" ");
                    String[] tmp = strLine.split("\t");
                    if (tmp.length > ln.length)
                    {
                        ln = tmp;
                    }
                    Marker mark = markerSet.getMarkers().get(row);
                    ArrayList<ArrayList<String>> mvals = new ArrayList<ArrayList<String>>();
                    for (col = 0; col < ln.length; col++)
                    {
                        //try
                        //{
                            double value = Double.valueOf(ln[col]);
                            if (value == 0)
                            {}
                            else
                            {
                                Trait trait = traitSet.getTraitByIdx(col);
                                vals = new ArrayList<String>();
                                vals.add("" + mark.getId());
                                vals.add("" + trait.getId());
                                vals.add("" + value);
                                vals.add("" + assocSetId);
                                mvals.add(vals);
                            }
                            assocCount++;
                        //}
                        //catch (Exception ex)
                        //{
                        //    errorText = ex.getMessage();
                        //    setIsError(true);
                        //    return;
                        //}
                    }
                    try
                    {
                        setValue((int) ((double) 98 * (double) assocCount / (double) (markerSet.getMarkers().size() * traitSet.getNumTraits())));
                        status = "Uploading ...";
                        form.repaint();
                    }
                    catch (Exception e)
                    {
                    }
                    if(mvals.size() > 0)
                    {
                        if(!DataManager.runMultipleInsertQuery(cols, mvals, "association"))
                        {
                            throw new RuntimeException("Failed to insert association values into DB");
                        }
                    }
                    row++;
                }
                ArrayList<String> where = new ArrayList<String>();
                where.add("id=" + assocSetId);
                DataManager.runUpdateQuery("assocset", "loadcmpt", "1", where);
                Model.getInstance().getProject(projectId).getAssocs().add(new AssociationSet(
                        Integer.parseInt(assocSetId), Model.getInstance().getProject(projectId), name,
                        0, -1, traitSet.getId(), markerSet.getId(), false, -1, -1));
                DataAddRemoveHandler.getInstance().refreshDisplay();
                setValue(100);

            }
            catch (Exception e)
            {
                if(assocSetId != null)
                {
                    ArrayList<String> whereArgs = new ArrayList();
                    whereArgs.add("assocsetid=" + assocSetId);
                    DataManager.deleteQuery("association", whereArgs);
                    whereArgs.clear();
                    whereArgs.add("id=" + assocSetId);
                    DataManager.deleteQuery("assocset", whereArgs);
                    whereArgs.clear();
                }
                
                JOptionPane.showMessageDialog(null, "Error uploading data to database.\n This is likely a bug. Please contact the developers\n Error message was:\n" + e.getMessage());
                errorText = "Error uploading file";
                setIsError(true);
                form.repaint();
                return;
            }
        }
    }
}
