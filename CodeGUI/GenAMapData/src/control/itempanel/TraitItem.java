package control.itempanel;

import control.DataAddRemoveHandler;
import datamodel.Model;
import datamodel.Project;
import datamodel.TraitSet;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFrame;
import realdata.DataManager;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * A traititem will import a traitset into the database on a separate thread.
 * This can handle up to about 10000 traits, which is our maximum we want
 * to allow the user to do. 
 * @author flaviagrosan
 */
public class TraitItem extends ThreadItem
{
    /**
     * The form that owns this traitItem. 
     */
    private JFrame form;
    /**
     * The id of the project these traits belong to.
     */
    private String projectId;
    /**
     * The name that this traitset will have. 
     */
    private String traitSetName;
    /**
     * The current status of the trait upload. 
     */
    private String status = "In queue ... ";
    /**
     * The string of any errors that have happened
     */
    private String errorString = "";
    /**
     * The file with the trait labels in it - null if they are inside the file
     */
    private String traitFile;
    /**
     * The file with the sample labels in it - null if they are inside the file
     */
    private String sampleFile;
    /**
     * true if this file is KxN, or false if it is NxK
     */
    private boolean isKxNFormat;
    /**
     * The matrix with all the data in it - stored in a delimeted file
     */
    private String dataFile;
    /**
     * The id of the species that this trait set belongs to!
     */
    private int speciesId;
    /**
     * Tells us whether to generate sample labels, or to read them from the data file.
     */
    private boolean isGenSamps;

    /**
     * Creates a new TraitItem. 
     * @param form the owner of the executing threads
     * @param traits the list of traits to upload
     * @param traitSetName the name of the traitset.
     * @param projectId the project id. 
     */
    public TraitItem(JFrame form, String traitSetName, String projectId,
            String datafile, boolean isKxNFormat, String sampleFile,
            String traitFile, int speciesId, boolean isGenSamps)
    {
        this.form = form;
        this.projectId = projectId;
        this.traitSetName = traitSetName;
        this.setValue(0);
        this.dataFile = datafile;
        this.isKxNFormat = isKxNFormat;
        this.sampleFile = sampleFile;
        this.traitFile = traitFile;
        this.speciesId = speciesId;
        this.isGenSamps = isGenSamps;
    }

    @Override
    public void start()
    {
        Task t = new Task();
        t.start();
    }

    @Override
    public String getName()
    {
        return this.traitSetName;
    }

    @Override
    public String getErrorText()
    {
        return errorString;
    }

    @Override
    public String getStatus()
    {
        return this.status;
    }

    @Override
    public String getSuccessMessage()
    {
        return this.traitSetName + " upload complete. ";
    }
    class Task extends Thread
    {
        @Override
        public void run()
        {
            BufferedReader in = null;
            try
            {
                setValue(0);
                form.repaint();
                ArrayList<String> vals = new ArrayList<String>();
                ArrayList<String> cols = new ArrayList<String>();
                ArrayList<String> whereArgs = new ArrayList<String>();
                cols.add("name");
                vals.add(traitSetName);
                cols.add("projectid");
                vals.add(projectId);
                cols.add("speciesid");
                vals.add(speciesId + "");
                status = "Inserting traitset ...";
                form.repaint();
                if (!DataManager.runInsertQuery(cols, vals, "traitset"))
                {
                    setIsError(true);
                    errorString = DataManager.getLastError() + "test";
                    return;
                }
                cols.clear();
                vals.clear();
                whereArgs.add("name='" + traitSetName + "'");
                whereArgs.add("projectid=" + projectId);
                String traitSetId = (String) DataManager.runSelectQuery("id", "traitset", true, whereArgs, null).get(0);
                whereArgs.clear();
                ArrayList<String> sampleIds = new ArrayList<String>();
                ArrayList<String> samples = new ArrayList<String>();

                if (dataFile.trim().length() != 0)
                {
                    status = "Updating samples ...";
                    form.repaint();
                    if (readInSamples(samples))
                    {
                        return;
                    }
                    cols.add("name");
                    for (int i = 0; i < samples.size(); i++)
                    {
                        whereArgs.add("name='" + samples.get(i) + "'");
                        //whereArgs.add("idx=" + i);
                        ArrayList<String> sampleVals = DataManager.runSelectQuery("id", "sample", true, whereArgs, "id");
                        if (sampleVals.isEmpty())
                        {
                            vals.add(samples.get(i));
                            if (!DataManager.runInsertQuery(cols, vals, "sample"))
                            {
                                setIsError(true);
                                errorString = DataManager.getLastError();
                                return;
                            }
                            vals.clear();
                            sampleVals = DataManager.runSelectQuery("id", "sample", true, whereArgs, null);
                        }
                        sampleIds.add(sampleVals.get(0));
                        whereArgs.clear();
                    }
                }
                cols.clear();
                status = "Uploading traits ...";
                form.repaint();
                ArrayList<String> traitnames = new ArrayList<String>();
                if (this.readInTraits(traitnames))
                {
                    return;
                }

                ArrayList<ArrayList<String>> valList = new ArrayList<ArrayList<String>>();
                cols.add("name");
                cols.add("idx");
                cols.add("traitsetid");
                for (int i = 0; i < traitnames.size(); i++)
                {
                    ArrayList<String> traitVal = new ArrayList<String>();
                    traitVal.add(traitnames.get(i));
                    traitVal.add("" + i);
                    traitVal.add(traitSetId);
                    valList.add(traitVal);
                    if (valList.size() > 1000)
                    {
                        if (!DataManager.runMultipleInsertQuery(cols, valList, "trait"))
                        {
                            setIsError(true);
                            errorString = DataManager.getLastError();
                            return;
                        }
                        valList.clear();
                    }
                }
                if (valList.size() > 0 && !DataManager.runMultipleInsertQuery(cols, valList, "trait"))
                {
                    setIsError(true);
                    errorString = DataManager.getLastError();
                    return;
                }
                cols.clear();
                valList.clear();
                whereArgs.add("traitsetid=" + traitSetId);
                ArrayList<String> traitIdStrings = DataManager.runSelectQuery("id", "trait", true, whereArgs, "idx");
                ArrayList<Integer> traitIds = new ArrayList<Integer>();
                for (String idString : traitIdStrings)
                {
                    traitIds.add(Integer.parseInt(idString));
                }
                whereArgs.clear();
                if (traitIds.size() != traitnames.size())
                {
                    setIsError(true);
                    errorString = DataManager.getLastError();
                    return;
                }

                if (dataFile.trim().length() == 0)
                {
                    cols.clear();
                    whereArgs.clear();
                    whereArgs.add("name = \"" + traitSetName + "\"");
                    status = "Finishing up!";
                    form.repaint();
                    if (!DataManager.runUpdateQuery("traitset", "hasdata", "0", whereArgs))
                    {
                        setIsError(true);
                        errorString = DataManager.getLastError();
                    }
                    if (!DataManager.runUpdateQuery("traitset", "loadcmpt", "1", whereArgs))
                    {
                        setIsError(true);
                        errorString = DataManager.getLastError();
                    }
                    setValue(100);
                    form.repaint();
                    Project p = Model.getInstance().getProject(Integer.parseInt(projectId));
                    p.getTraits().add(new TraitSet(Integer.parseInt(traitSetId), p, traitSetName, speciesId, "."));
                    DataAddRemoveHandler.getInstance().refreshDisplay();
                    return;
                }


                status = "Uploading trait values ....";
                form.repaint();
                in = new BufferedReader(new FileReader(dataFile));
                String str;
                if (sampleFile == null)
                {
                    str = in.readLine();
                }
                int ln = 0;
                cols.clear();
                cols.add("sampleid");
                cols.add("traitid");
                cols.add("value");
                if (sampleFile.length() < 1 && !isGenSamps)
                {
                    sampleFile = null;
                    in.readLine();
                }
                while ((str = in.readLine()) != null)
                {
                    if (str.trim().length() == 0)
                    {
                        continue;
                    }
                    String[] line = str.split("\t");
                    if (line.length == 1)
                    {
                        str = str.replaceAll("  ", " ");
                        str = str.replaceAll("  ", " ");
                        str = str.replaceAll("  ", " ");
                        str = str.replaceAll("  ", " ");
                        str = str.replaceAll("  ", " ");
                        str = str.trim();
                        line = str.split(" ");
                    }
                    ArrayList<ArrayList<String>> traitValStrings = new ArrayList<ArrayList<String>>();


                    int i = (sampleFile == null) ? 1 : 0;
                    for (; i < line.length; i++)
                    {
                        ArrayList<String> traitVals = new ArrayList<String>();
                        if (isKxNFormat)
                        {
                            traitVals.add(sampleIds.get(i - (sampleFile == null ? 1 : 0)));
                            traitVals.add("" + traitIds.get(ln));
                        }
                        else
                        {
                            traitVals.add(sampleIds.get(ln));
                            traitVals.add("" + traitIds.get(i - (sampleFile == null ? 1 : 0)));
                        }
                        traitVals.add(line[i]);
                        traitValStrings.add(traitVals);
                    }
                    if (!DataManager.runMultipleInsertQuery(cols, traitValStrings, "traitval"))
                    {
                        setIsError(true);
                        errorString = DataManager.getLastError();
                        return;
                    }
                    ln++;
                    if (isKxNFormat)
                    {
                        setValue((int) (((double) ln / (double) traitnames.size()) * 99.0));
                    }
                    else
                    {
                        setValue((int) (((double) ln / (double) samples.size()) * 99.0));
                    }
                    form.repaint();
                }
                cols.clear();
                whereArgs.clear();
                whereArgs.add("name = \"" + traitSetName + "\"");
                status = "Finishing up!";
                form.repaint();
                if (!DataManager.runUpdateQuery("traitset", "loadcmpt", "1", whereArgs))
                {
                    setIsError(true);
                    errorString = DataManager.getLastError();
                }
                setValue(100);
                form.repaint();
                Project p = Model.getInstance().getProject(Integer.parseInt(projectId));
                p.getTraits().add(new TraitSet(Integer.parseInt(traitSetId), p, traitSetName, speciesId, "."));
                DataAddRemoveHandler.getInstance().refreshDisplay();
            }
            catch (Exception ex)
            {
                setIsError(true);
                ex.printStackTrace();
                errorString = "Cannot parse file: " + dataFile;
            }
            finally
            {
                try
                {
                    in.close();
                }
                catch (Exception ex)
                {
                }
            }
        }

        private boolean readInSamples(ArrayList<String> samples)
        {
            if (sampleFile != null && sampleFile.length() > 0)
            {
                BufferedReader in = null;
                try
                {
                    in = new BufferedReader(new FileReader(sampleFile));
                    String str;
                    int val = 0;
                    while ((str = in.readLine()) != null)
                    {
                        if ((str = str.trim()).length() > 0)
                        {
                            samples.add(str);
                        }
                    }
                }
                catch (Exception ex)
                {
                    setIsError(true);
                    errorString = "File is not found or cannot be read! " + sampleFile;
                    return true;
                }
                finally
                {
                    try
                    {
                        in.close();
                    }
                    catch (IOException ex)
                    {
                    }
                }
            }
            else
            {
                if (isKxNFormat)
                {
                    BufferedReader in = null;
                    try
                    {
                        in = new BufferedReader(new FileReader(dataFile));
                        String str = in.readLine();
                        String[] samps = str.split("\t");
                        in.close();
                        for (int i = 0; i < samps.length; i++)
                        {
                            if (!isGenSamps)
                            {
                                samples.add(samps[i]);
                            }
                            else
                            {
                                samples.add("S" + i);
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        setIsError(true);
                        errorString = "File is not found or cannot be read! " + dataFile;
                        return true;
                    }
                    finally
                    {
                        try
                        {
                            in.close();
                        }
                        catch (IOException ex)
                        {
                        }
                    }
                }
                else
                {
                    BufferedReader in = null;
                    try
                    {
                        in = new BufferedReader(new FileReader(dataFile));
                        String str;
                        if (!isGenSamps)
                        {
                            str = in.readLine();
                        }
                        int i = 0;
                        while ((str = in.readLine()) != null)
                        {
                            int idx = str.indexOf('\t');
                            if (idx == -1)
                            {
                                idx = str.indexOf(' ');
                            }
                            if (!isGenSamps)
                            {
                                samples.add(str.substring(0, idx));
                            }
                            else
                            {
                                samples.add("S" + (i));
                            }
                            i++;
                        }
                        in.close();
                    }
                    catch (Exception ex)
                    {
                        setIsError(true);
                        errorString = "File is not found or cannot be read! " + dataFile;
                        return true;
                    }
                    finally
                    {
                        try
                        {
                            in.close();
                        }
                        catch (IOException ex)
                        {
                        }
                    }
                }
            }
            return false;
        }

        private boolean readInTraits(ArrayList<String> traits)
        {
            if (traitFile != null && traitFile.length() > 0)
            {
                BufferedReader in = null;
                try
                {
                    in = new BufferedReader(new FileReader(traitFile));
                    String str;
                    while ((str = in.readLine()) != null)
                    {
                        if ((str = str.trim()).length() > 0)
                        {
                            traits.add(str);
                        }
                    }
                }
                catch (Exception ex)
                {
                    setIsError(true);
                    errorString = "File is not found or cannot be read! " + traitFile;
                    return true;
                }
                finally
                {
                    try
                    {
                        in.close();
                    }
                    catch (IOException ex)
                    {
                    }
                }
            }
            else
            {
                if (!isKxNFormat)
                {
                    BufferedReader in = null;
                    try
                    {
                        in = new BufferedReader(new FileReader(dataFile));
                        String str = in.readLine().trim();
                        String[] samps = str.split("\t");
                        in.close();
                        for (int i = 0; i < samps.length; i++)
                        {
                            traits.add(samps[i]);
                        }
                    }
                    catch (Exception ex)
                    {
                        setIsError(true);
                        errorString = "File is not found or cannot be read! " + dataFile;
                        return true;
                    }
                    finally
                    {
                        try
                        {
                            in.close();
                        }
                        catch (IOException ex)
                        {
                        }
                    }
                }
                else
                {
                    BufferedReader in = null;
                    try
                    {
                        in = new BufferedReader(new FileReader(dataFile));
                        String str = in.readLine();
                        while ((str = in.readLine()) != null)
                        {
                            int idx = str.indexOf('\t');
                            if (idx == -1)
                            {
                                idx = str.indexOf(' ');
                            }
                            traits.add(str.substring(0, idx));
                        }
                        in.close();
                    }
                    catch (Exception ex)
                    {
                        setIsError(true);
                        errorString = "File is not found or cannot be read! " + dataFile;
                        return true;
                    }
                    finally
                    {
                        try
                        {
                            in.close();
                        }
                        catch (IOException ex)
                        {
                        }
                    }
                }
            }
            return false;
        }
    }
}
