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
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.swing.JFrame;

/**
 * Controls the process to read in a .bed file. 
 * @author fgrosan
 */
public class BedItem extends ThreadItem
{

    /**
     * The form that we can call to repaint this BedItem
     */
    private JFrame form;
    /**
     * The name of the file that we are creating. 
     */
    private String name;
    /**
     * The current status of the upload
     */
    private String status = "In queue ...";
    /**
     * The text of the last error, if any
     */
    private String errorText = "";
    /**
     * The project id
     */
    private int projId;
    /**
     * the name of the file with the markers in it
     */
    private String markerfile;
    /**
     * map file - the bim file with the map data in it.
     */
    private String mapfile;
    /**
     * the file with the family data in it
     */
    private String famfile;

    /**
     * Creates the item to be put into the queue to be run eventually.
     * @param form to call repaint on to refresh the display
     * @param name the name of the import
     * @param projId the project that we are working with.
     */
    public BedItem(JFrame form, String name, int projId, String markerfile, String bimfile, String famfile)
    {
        this.form = form;
        this.name = name;
        this.projId = projId;
        this.markerfile = markerfile;
        this.mapfile = bimfile;
        this.famfile = famfile;
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

    //class firing up a new thread to handle the file import
    class Task extends Thread
    {

        @Override
        public void run()
        {
            try
            {
                status = "Reading file ...";
                form.repaint();

                String markerSetId = updateMarkerSet(projId, name);
                if (null == markerSetId)
                {
                    errorText = ("Error in updating markerset table");
                    setIsError(true);
                    return;
                }

                //TODO - change this
                int format = Marker.NXJNO_FORMAT;
                MarkerLabelsImport markerLabelsImport = new MarkerLabelsImport(markerSetId, mapfile);
                SamplesImport samplesImport = new SamplesImport(format, projId, famfile, famfile);

                String now = Long.toString(System.currentTimeMillis());

                String markerIdsFile = "markers" + now + ".txt";
                String samplesIdsFile = "samples" + now + ".txt";
                String insertSQLFile = "insert" + now + ".txt";
                String bedFile = markerfile;
                status = "Getting marker positions ...";
                form.repaint();
                int numM = markerLabelsImport.insertMarkerLabels(markerIdsFile);
                setValue(1);
                status = "Getting sample family data ...";
                form.repaint();
                int numS = samplesImport.insertSamples(samplesIdsFile, true);
                setValue(2);
                form.repaint();
                int numInserts = (int) Math.ceil((double)numS / 1000.0);
                int cnt = (int) Math.ceil((double) numM * numInserts);

                try
                {
                    Runtime r = Runtime.getRuntime();
                    Process p;
                    if (OSValidator.isMac())
                    {
                        p = r.exec("./bed_mac " + bedFile + " " + markerIdsFile + " " + samplesIdsFile + " " + insertSQLFile);
                    }
                    else if (OSValidator.isWindows())
                    {
                        p = r.exec("./bed.exe " + bedFile + " " + markerIdsFile + " " + samplesIdsFile + " " + insertSQLFile);
                    }
                    else
                    {
                        p = r.exec("./bed " + bedFile + " " + markerIdsFile + " " + samplesIdsFile + " " + insertSQLFile);
                    }
                    BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line;
                    int i = 0;
                    while ((line = input.readLine()) != null)
                    {
                        //System.out.println(line);
                        String query = line.substring(0, line.length() - 3);
                        query = query.concat(";");
                        //System.out.println(query);

                        if (DataManager.runQuery(query) == null)
                        {
                            errorText = DataManager.getLastError();
                            setIsError(true);
                            File f = new File(markerIdsFile);
                            f.delete();
                            f = new File(samplesIdsFile);
                            f.delete();
                            f = new File(insertSQLFile);
                            f.delete();
                            return;
                        }
                        int percmpt = (int)(2 + (double)(i++) / (double)cnt * 96.0);
                        status = "Uploading markerset ...";
                        setValue(percmpt);
                        form.repaint();
                    }
                    input.close();
                }
                catch (Exception e)
                {
                    errorText = e.getMessage();
                    setIsError(true);
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

                if (!DataManager.runUpdateQuery("markerset", "loadcmpt", "1", whereArgs))
                {
                    errorText = DataManager.getLastError();
                    setIsError(true);
                }
                Model.getInstance().getProject(projId).getMarkers().add(
                        new MarkerSet(Integer.parseInt(markerSetId),
                        Model.getInstance().getProject(projId), name));
                DataAddRemoveHandler.getInstance().refreshDisplay();

                //todo - refactor this
                File f = new File(markerIdsFile);
                f.delete();
                f = new File(samplesIdsFile);
                f.delete();
                f = new File(insertSQLFile);
                f.delete();

                setValue(100);

            }
            catch (Exception e)
            {
                errorText = e.getMessage();
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
                errorText = DataManager.getLastError();
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
    }
}



