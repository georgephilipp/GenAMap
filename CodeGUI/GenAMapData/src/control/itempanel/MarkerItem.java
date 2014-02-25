package control.itempanel;

import control.DataAddRemoveHandler;
import control.OSValidator;
import control.importing.MarkerLabelsImport;
import control.importing.SamplesImport;
import datamodel.Marker;
import datamodel.MarkerSet;
import datamodel.Model;
import datamodel.Project;
import realdata.DataManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.swing.JFrame;

/**
 * We support the import of an NxJ file, with no row or column headers. N is
 * the number of samples, and J is the number of SNPs. We use this class to
 * import it on a separate thread. 
 *
 * @author fgrosan
 * @author rcurtis
 */
public class MarkerItem extends ThreadItem
{
    /**
     * The formt that we use to call repaint on.
     */
    private JFrame form;
    /**
     * The name of the markerset we are importing
     */
    private String name;
    /**
     * The text of the last error that occured in uploading.
     */
    private String errorText;
    /**
     * The current upload status. 
     */
    private String status = "In queue ...";
    /**
     * The name with the labels of the markers
     */
    private String markerFile;
    /**
     * The sample file with the labels of the samples
     */
    private String sampleFile;
    /**
     * The name of the file with the marker values in it
     */
    private String matrixFile;
    /**
     * The name of the project
     */
    private String projName;

    /**
     * Creates a new instance of this marker item which will read in the data. It
     * points to the different files.
     * @param form the form that can be updated for the view
     * @param name the name of the new markerset;
     * @param projName the name of the project
     * @param sampleFile the sample file, can be null
     * @param markerFile the markerfile with the labels for the markers
     */
    public MarkerItem(JFrame form, String name, String projName, String sampleFile, String markerFile, String matrixFile)
    {
        this.form = form;
        this.name = name;
        this.projName = projName;
        this.sampleFile = sampleFile;
        this.markerFile = markerFile;
        this.matrixFile = matrixFile;
    }

    @Override
    public String getName()
    {
        return this.name;
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
        return name + " has been uploaded successfully!";
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
                status = "Creating markerset ...";
                setValue(1);
                form.repaint();

                String projectName = projName;
                int projectId = Model.getInstance().getProject(projectName).getId();

                String markerSetId = updateMarkerSet(projectId, name);
                if (null == markerSetId)
                {
                    errorText = "Error creating markerset. Check name.";
                    setIsError(true);
                    return;
                }

                //inserting marker labels into the database
                //and getting their ids into markerlabels.txt file
                int format = Marker.NXJNO_FORMAT;
                MarkerLabelsImport markerLabelsImport = new MarkerLabelsImport(markerSetId, markerFile);//, markerTextBox.getText());
                SamplesImport samplesImport = new SamplesImport(format, projectId, sampleFile, matrixFile);

                String now = Long.toString(System.currentTimeMillis());
                status = "Loading markers ...";
                form.repaint();
                String markerIdsFile = "markers" + now + ".txt";
                String samplesIdsFile = "samples" + now + ".txt";

                int numM = markerLabelsImport.insertMarkerLabels(markerIdsFile);

                if (matrixFile == null)
                {
                    ArrayList<String> where = new ArrayList<String>();
                    where.add("id = " + markerSetId);
                    if (!DataManager.runUpdateQuery("markerset", "hasdata", "0", where))
                    {
                        errorText = (DataManager.getLastError());
                        setIsError(true);
                        return;
                    }
                    if (!DataManager.runUpdateQuery("markerset", "loadcmpt", "1", where))
                    {
                        errorText = (DataManager.getLastError());
                        setIsError(true);
                        return;
                    }
                    Project pr = Model.getInstance().getProject(projectId);
                    pr.getMarkers().add(new MarkerSet(Integer.parseInt(markerSetId), pr, name));
                    DataAddRemoveHandler.getInstance().refreshDisplay();
                    setValue(100);
                    form.repaint();
                    return;
                }

                status = "Loading samples ...";
                setValue(2);
                form.repaint();
                int numS = samplesImport.insertSamples(samplesIdsFile, false);
                int cnt = (int) Math.ceil((double) numM / 500.0);
                int count = numS * cnt;

                try
                {
                    Runtime r = Runtime.getRuntime();
                    System.out.println(matrixFile);
                    status = "Uploading marker values ...";
                    form.repaint();

                    MatrixFileReader mfr = new MatrixFileReader(samplesIdsFile, markerIdsFile, matrixFile);
                    //Process p;// = r.exec("./matrix " + matrixFile + " " + markerIdsFile + " " + samplesIdsFile);
                    //if (OSValidator.isMac())
                    //{
                    //    p = r.exec("./matrix_mac " + matrixFile + " " + markerIdsFile + " " + samplesIdsFile);
                    //}
                    //else if (OSValidator.isWindows())
                    //{
                    //    p = r.exec("./matrix.exe " + matrixFile + " " + markerIdsFile + " " + samplesIdsFile);
                    //}
                    //else
                    //{
                    //    p = r.exec("./matrix " + matrixFile + " " + markerIdsFile + " " + samplesIdsFile);
                    //}
                    //BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    //String line;
                    //int i = 0;
                    //while ((line = input.readLine()) != null)
                    //{

                    //   String query = line.substring(0, line.length() - 3);
                    //   query = query.concat(";");
                    //System.out.println(query);

                    //   setValue((int)(((double)i++ / (double) count) * 95.0 + 3));
                    //   form.repaint();
                    //   DataManager.runQuery(query);
                    //}
                    //input.close();
                    ArrayList<String> lines;
                    int i = 0;
                    while ((lines = mfr.readline()) != null)
                    {
                        for (String line : lines)
                        {
                            String query = line;
                            query = query.concat(";");
                            setValue((int) (((double) i++ / (double) count) * 95.0 + 3));
                            form.repaint();
                            DataManager.runQuery(query);
                        }
                    }
                }
                catch (Exception e)
                {
                    errorText = e.getMessage();
                    setIsError(true);
                    File f = new File(markerIdsFile);
                    f.delete();
                    f = new File(samplesIdsFile);
                    f.delete();
                    return;
                }

                setValue(99);
                status = "Finishing up ...";
                form.repaint();
                ArrayList<String> whereArgs = new ArrayList<String>();
                whereArgs.add("id=" + markerSetId);

                File f = new File(markerIdsFile);
                f.delete();
                f = new File(samplesIdsFile);
                f.delete();


                if (!DataManager.runUpdateQuery("markerset", "loadcmpt", "1", whereArgs))
                {
                    errorText = (DataManager.getLastError());
                    setIsError(true);
                    return;
                }
                Project pr = Model.getInstance().getProject(projectId);
                pr.getMarkers().add(new MarkerSet(Integer.parseInt(markerSetId), pr, name));
                DataAddRemoveHandler.getInstance().refreshDisplay();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                errorText = ("There was an error in loadmarkers...");
                setIsError(true);
                return;
            }
            setValue(100);
        }

        /*
         * updating markerSet table
         * get this file's id
         */
        public String updateMarkerSet(int projectId, String markerFileName)
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
                System.err.println("Invalid file name");
                return null;
            }
            cols.clear();
            vals.clear();
            whereArgs.add("name='" + markerFileName + "'");
            whereArgs.add("projectid=" + projectId);
            String markerSetId = (String) DataManager.runSelectQuery("id", "markerset", true, whereArgs, null).get(0);
            whereArgs.clear();

            return markerSetId;

        }
    }
}



