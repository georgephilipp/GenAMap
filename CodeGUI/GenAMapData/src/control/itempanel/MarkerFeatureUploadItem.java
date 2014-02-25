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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.swing.JFrame;

/**
 * This will upload a read-in association set into the database. The file is
 * actually parsed and read in this method.
 * @author rcurtis
 */
public class MarkerFeatureUploadItem extends ThreadItem
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
     * The id of the project 
     */
    private int projectId;
   
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
    public MarkerFeatureUploadItem(JFrame form, MarkerSet ms, int projID,
            String filename)
    {
        this.form = form;
        this.markerSet = ms;
        this.projectId = projID;
        this.fileName = filename;
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
        return  "Features upload complete.";
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
            try
            {
                setValue(0);
                status = "Reading file ...";
                form.repaint();
                FileInputStream fstream = new FileInputStream(fileName);
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                //read featurenames
                String strLine = br.readLine();
                strLine = strLine.trim();
                strLine = strLine.replaceAll("  ", " ");
                strLine = strLine.replaceAll("\t", " ");
                String[] fname = strLine.split(" ");
                int totalFeatures = fname.length;
                int[] featureIds = new int[totalFeatures];

                //insert featurenames in the feature table and get the id
                int j=0;
                for(int cnt=0; cnt < fname.length; cnt++)
                {
                    String featname = fname[cnt];
                    ArrayList<String> vals = new ArrayList<String>();
                    ArrayList<String> cols = new ArrayList<String>();

                    cols.add("name");
                    cols.add("markersetid");
                    vals.add(featname);
                    vals.add(""+markerSet.getId());

                    if (!DataManager.runInsertQuery(cols, vals, "feature"))
                    {
                         setIsError(true);
                         errorText = DataManager.getLastError();
                         return;
                    }

                    //get feature id
                    ArrayList<String> whereArgs = new ArrayList<String>();
                    whereArgs.add("name='" + featname + "'");
                    whereArgs.add("markersetid=" + markerSet.getId());
                    String featureID = (String)DataManager.runSelectQuery("id", "feature", true, whereArgs, null).get(0);
                    featureIds[j++] = Integer.parseInt(featureID);
                }

                //read features
                int idx=0;
                while ((strLine = br.readLine()) != null)
                {
                    strLine = strLine.trim();
                    strLine = strLine.replaceAll("  ", " ");
                    strLine = strLine.replaceAll("\t", " ");
                    if(strLine.length() == 0)
                        continue;
                    String[] ln = strLine.split(" ");
                    ////String markerName = ln[0].trim();
                    //extract marker id for this markername
                    ArrayList<String> whereArgs = new ArrayList<String>();
                    ////whereArgs.add("name='" + markerName + "'");
                    whereArgs.add("idx=" + idx );
                    whereArgs.add("markersetid=" + markerSet.getId());
                    String markerID = (String)DataManager.runSelectQuery("id", "marker", true, whereArgs, null).get(0);

                    //now add features
                    for(int f=0; f < ln.length; f++)//change to f=1 when marker name is there 
                    {

                        ArrayList<String> vals = new ArrayList<String>();
                        ArrayList<String> cols = new ArrayList<String>();

                        cols.add("featureid");
                        cols.add("markerid");
                        cols.add("value");
                        vals.add(featureIds[f]+""); //f-1
                        vals.add(""+markerID);
                        vals.add(ln[f].trim());
                        if (!DataManager.runInsertQuery(cols, vals, "featureval"))
                        {
                             setIsError(true);
                             errorText = DataManager.getLastError();
                             return;
                        }


                    }
                    idx++;
                }


                DataAddRemoveHandler.getInstance().refreshDisplay();
                setValue(100);

            }
            catch (Exception ex)
            {
                errorText = ex.getMessage();
                setIsError(true);
                return;
            }
        }
    }
}
