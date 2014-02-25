package control.itempanel;

import control.DataAddRemoveHandler;
import control.OSValidator;
import control.importing.MarkerLabelsImport;
import control.importing.SamplesImport;
import datamodel.Marker;
import datamodel.MarkerSet;
import datamodel.Model;
import realdata.DataManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.swing.JFrame;

/**
 * PedItem is responsible for importing a ped/map file combination into
 * the database. 
 * @author fgrosan
 * @author rcurtis
 */
public class PedItem extends ThreadItem
{
    private JFrame form;
    public String name;
    private Task t;
    private String projectName;
    private String errorString = "";
    private String pedFile;
    private String mapFile;
    private String nameField;
    private boolean isInsertTrait = false;
    private String traitname = null;
    private String status = "Uploading";

    public PedItem(JFrame form, String name, String projectName, String pedFile, 
            String mapFile, String nameField, boolean isInsertTrait, String traitname)
    {
        this.form = form;
        this.name = name;
        this.projectName = projectName;
        this.pedFile = pedFile;
        this.mapFile = mapFile;
        this.nameField = nameField;
        this.isInsertTrait = isInsertTrait;
        this.traitname = traitname;
    }

    public void updateValue(int value)
    {
        setValue(value);
        form.repaint();
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getErrorText()
    {
        return this.errorString;
    }

    @Override
    public String getStatus()
    {
        return status;
    }

    @Override
    public String getSuccessMessage()
    {
        return pedFile + " upload complete!";
    }

    @Override
    public void start()
    {
        t = new Task();
        t.start();
    }

    //class firing up a new thread to handle the file import
    class Task extends Thread
    {

        @Override
        public void run()
        {

            try
            {
                int projectId = Model.getInstance().getProject(projectName).getId();

                status = "Creating markerset ....";
                form.repaint();
                String markerSetId = updateMarkerSet(projectId, nameField);
                if (null == markerSetId)
                {
                    errorString = ("Unable to read this file. Check name.");
                    return;
                }
                //System.err.println("markersetid: " + markerSetId);

                MarkerLabelsImport markerLabelsImport = new MarkerLabelsImport(markerSetId, mapFile);
                SamplesImport samplesImport = new SamplesImport(Marker.NXJNO_FORMAT, projectId, pedFile, pedFile);

                String now = Long.toString(System.currentTimeMillis());
                String markerIdsFile = "markers" + now + ".txt";
                String samplesIdsFile = "samples" + now + ".txt";
                String insertSQLFile = "insert" + now + ".txt";

                status = "Inserting markers ....";
                form.repaint();
                int numM = markerLabelsImport.insertMarkerLabels(markerIdsFile);
                updateValue(1);
                status = "Inserting samples ....";
                form.repaint();
                int numS = samplesImport.insertSamples(samplesIdsFile,
                        isInsertTrait, traitname, true, nameField, projectName);
                updateValue(2);

                try
                {
                    status = "Processing file ....";
                    form.repaint();
                    Runtime r = Runtime.getRuntime();
                    Process p;
                    if (OSValidator.isMac())
                    {
                        p = r.exec("./ped_mac " + pedFile + " " + markerIdsFile + " " + samplesIdsFile + " " + insertSQLFile);
                    }
                    else if (OSValidator.isWindows())
                    {
                        p = r.exec("./ped.exe " + pedFile + " " + markerIdsFile + " " + samplesIdsFile + " " + insertSQLFile);
                    }
                    else
                    {
                        p = r.exec("./ped " + pedFile + " " + markerIdsFile + " " + samplesIdsFile + " " + insertSQLFile);
                    }
                    BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line;
                    while ((line = input.readLine()) != null)
                    {
                        //System.out.println(line);
                        updateValue(Integer.parseInt(line.trim()) / 10 + 2);
                    }
                    input.close();
                }
                catch (Exception e)
                {
                    setIsError(true);
                    errorString = e.getMessage();
                    File f = new File(markerIdsFile);
                    f.delete();
                    f = new File(samplesIdsFile);
                    f.delete();
                    f = new File(insertSQLFile);
                    f.delete();
                    return;
                }

                status = "Uploading values ....";
                form.repaint();
                if(!insertFile(insertSQLFile, numS, numM))
                {
                    File f = new File(markerIdsFile);
                    f.delete();
                    f = new File(samplesIdsFile);
                    f.delete();
                    f = new File(insertSQLFile);
                    f.delete();
                    return;
                }

                ArrayList<String> whereArgs = new ArrayList<String>();
                whereArgs.add("id=" + markerSetId);

                status = "Finishing up ....";
                form.repaint();
                if (!DataManager.runUpdateQuery("markerset", "loadcmpt", "1", whereArgs))
                {
                    errorString = (DataManager.getLastError());
                    setIsError(true);
                }

                Model.getInstance().getProject(projectName).getMarkers().add(
                        new MarkerSet(Integer.parseInt(markerSetId), 
                        Model.getInstance().getProject(projectId), nameField));
                DataAddRemoveHandler.getInstance().refreshDisplay();

                File f = new File(markerIdsFile);
                f.delete();
                f = new File(samplesIdsFile);
                f.delete();
                f = new File(insertSQLFile);
                f.delete();

                updateValue(100);
            }
            catch (Exception e)
            {
                errorString = ("Unable to read this file.  Check formatting.");
                setIsError(true);
                return;
            }
        }

        /*
         * updating markerSet table
         * get this file's id
         */
        private String updateMarkerSet(int projectId, String markerFileName)
        {
            ArrayList<String> vals = new ArrayList<String>();
            ArrayList<String> cols = new ArrayList<String>();
            ArrayList<String> whereArgs = new ArrayList<String>();
            cols.add("name");
            vals.add(markerFileName);
            cols.add("projectid");
            vals.add(projectId + "");
            if (!DataManager.runInsertQuery(cols, vals, "markerset"))
            {
                errorString = ("Invalid file name");
                setIsError(true);
                return null;
            }
            cols.clear();
            vals.clear();
            whereArgs.add("name='" + markerFileName + "'");
            whereArgs.add("projectid=" + projectId);
            String markerSetId = (String)DataManager.runSelectQuery("id", "markerset", true, whereArgs, null).get(0);
            whereArgs.clear();

            return markerSetId;

        }

        /*
         * inserting the SQL file in the db
         */
        private boolean insertFile(String fileName, int numSamps, int numMarkers)
        {
            int tot = numSamps * numMarkers;
            try
            {
                BufferedReader input = new BufferedReader(new FileReader(fileName));
                String line;
                int i = 0;
                while ((line = input.readLine()) != null)
                {
                    //System.out.println(i++);
                    DataManager.runQuery(line);
                    updateValue(12 + (int) (86.0 * (i++ * 4001) / tot));
                }
                input.close();
                return true;
            }
            catch (Exception e)
            {
                setIsError(true);
                errorString = e.getMessage();
                return false;
            }
        }
    }
}



