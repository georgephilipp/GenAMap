package control.importing;

import datamodel.Marker;
import datamodel.Sample;
import datamodel.Trait;
import realdata.DataManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * This class manages the insertion of new samples and the retrieval of old
 * samples from the database. 
 * @author flaviagrosan
 */
public class SamplesImport
{
    private int format;
    private int projectId;
    private String sampleFile;
    private String markerValFile;

    /**
     * Creates a new Samples import
     * @param format the format of the data (if reading from the markerval file)
     * @param projectId the project id
     * @param sampleFile the sample file with the list of samples
     * @param markerValFile the file with the marker values. 
     */
    public SamplesImport(int format, int projectId, String sampleFile, String markerValFile)
    {
        this.format = format;
        this.projectId = projectId;
        this.sampleFile = sampleFile;
        this.markerValFile = markerValFile;
    }

    /**
     * Inserts the samples from the file into the database
     * @param file the file with the sample names
     * @param isConcatTwoCols true if we should use the first two columns for the sample name.
     * @return
     */
    public int insertSamples(String file, boolean isConcatTwoCols)
    {
        return insertSamples(file, false, null, isConcatTwoCols, null, null);
    }

    /**
     * Inserts the samples from the file into the database
     * @param file the file with the sample names
     * @param isTraitInsert true if we are inserting samples for a traitset
     * @param traitname the name of the traitset ?
     * @param isConcatTwoCols true if we should use the first two columns for sample name
     * @param traitsetname the name of the traitset
     * @param project the name of the project. 
     * @return
     */
    public int insertSamples(String file, boolean isTraitInsert, String traitname,
            boolean isConcatTwoCols, String traitsetname, String project)
    {
        if (sampleFile != null && !sampleFile.equals(""))
        {
            int count = 0;
            try
            {
                FileInputStream fstream = new FileInputStream(sampleFile);
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                BufferedWriter out = new BufferedWriter(new FileWriter(file));
                String strLine;
                int ix = 0;
                Trait trait = null;
                if (isTraitInsert)
                {
                    trait = new Trait(traitname);
                }

                while ((strLine = br.readLine()) != null)
                {
                    count++;
                    int idx = strLine.indexOf(' ');
                    if (idx == -1)
                    {
                        idx = strLine.indexOf('\t');
                    }
                    if (idx == -1)
                    {
                        idx = strLine.indexOf(',');
                    }

                    String name;
                    if (idx == -1)
                    {
                        name = strLine;
                    }
                    else
                    {
                        name = getColFromString(strLine, strLine.charAt(idx), 1);
                        if (isConcatTwoCols)
                        {
                            name += "_" + getColFromString(strLine, strLine.charAt(idx), 2);
                        }
                    }
                    name = name.trim();

                    double traitVal = 0.0;
                    if (isTraitInsert)
                    {
                        traitVal = Double.parseDouble(getColFromString(strLine, strLine.charAt(idx), 6));
                    }
                    int sampid = insertSampleIntoDB(name.trim(), (ix++) + "", out);
                    if (sampid < 0)
                    {
                        System.err.println("Error reading in files.");
                        return 0;
                    }
                    else if (isTraitInsert)
                    {
                        trait.addSample(new Sample(sampid), traitVal);

                    }
                }
                /*if (isTraitInsert)
                {
                    ArrayList<Trait> ar = new ArrayList<Trait>();
                    ar.add(trait);
                    DataAddRemoveHandler.getInstance().addTraits(ar, traitsetname, project);
                }*/
                in.close();
                out.close();
                return count;
            }
            catch (Exception e)
            {
                System.err.println("Error reading in files.");
                return 0;
            }

        }
        else
        {
            if (format == Marker.NXJNO_FORMAT)
            {
                return generateSamples(0, file);
            }
        }
        return 0;
    }

    /**
     * Generate the samples if there are none provided
     * @param skip
     * @param file
     * @return
     */
    private int generateSamples(int skip, String file)
    {
        int count = 0;

        if (skip == 0)
        {
            try
            {
                BufferedReader in = new BufferedReader(new FileReader(markerValFile));
                BufferedWriter out = new BufferedWriter(new FileWriter(file));

                while (in.readLine() != null)
                {
                    String idx = Integer.toString(count);
                    String name = "S" + idx;
                    if (insertSampleIntoDB(name, idx, out) < 0)
                    {
                        return 0;
                    }

                    count++;

                }
                out.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return count;
    }

    /**
     * Check the db to see if the sample exists, and if it doesn't, create it.
     */
    private int insertSampleIntoDB(String name, String idx, BufferedWriter out) throws IOException
    {
        ArrayList<String> whereArgs = new ArrayList<String>();
        ArrayList<String> cols = new ArrayList<String>();
        ArrayList<String> vals = new ArrayList<String>();

        name = name.trim();
        cols.add("name");
        cols.add("idx");
        cols.add("projectid");

        whereArgs.add("name='" + name.trim() + "'");
        //whereArgs.add("idx=" + idx);
        //whereArgs.add("projectId=" + Integer.toString(projectId));
        //check is sample already in database
        ArrayList<String> sampleVals = DataManager.runSelectQuery("id", "sample", true, whereArgs, "id");
        if (sampleVals.isEmpty())
        {
            vals.add(name);
            vals.add(idx);
            vals.add(Integer.toString(projectId));
            if (!DataManager.runInsertQuery(cols, vals, "sample"))
            {
                System.err.println("Error in inserting sample");
                return -1;
            }
            vals.clear();
            sampleVals = DataManager.runSelectQuery("id", "sample", true, whereArgs, "id");
        }
        out.write(sampleVals.get(0));
        out.newLine();
        System.out.println(sampleVals.get(0));
        whereArgs.clear();
        return Integer.parseInt(sampleVals.get(0));
    }

    private String getColFromString(String strLine, char delim, int N)
    {
        String toRet = "";
        boolean isPlaced = false;
        int cnt = 0;

        for (int i = 0; i < strLine.length(); i++)
        {
            if (strLine.charAt(i) != delim)
            {
                toRet += strLine.charAt(i);
                isPlaced = false;
            }
            else
            {
                cnt += isPlaced ? 0 : 1;
                if (cnt > N - 1)
                {
                    return toRet;
                }
                else
                {
                    toRet = "";
                }
                //toRet += isPlaced ? "" : "_";
                isPlaced = true;
            }
        }

        return toRet;
    }
}
