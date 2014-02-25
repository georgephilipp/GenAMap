package control.importing;

import realdata.DataManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class manages the marker label import and it creates a file containing
 * the Ids in the database
 * @author flaviagrosan
 */
public class MarkerLabelsImport
{
    private String markerSetId;
    private String markerLabelsFile;

    /**
     * Instantiates a new marker labels import class
     * @param markerSetId the markerset id that we are working with
     * @param markerLabelsFile The file that the marker labels are in.
     */
    public MarkerLabelsImport(String markerSetId, String markerLabelsFile)
    {
        this.markerSetId = markerSetId;
        this.markerLabelsFile = markerLabelsFile;
    }

    /**
     * Insert the marker labels from the file into the database
     * @param file the file with the marker labels in it
     * @return
     * @throws Exception
     */
    public int insertMarkerLabels(String file) throws Exception
    {
        if (!markerLabelsFile.equals(""))
        {
            //System.err.println("Parsing the marker label file!");
            int toRet = parseMarkerLabelsFile();
            writeIdstoFile(file);
            return toRet;
        }
        else
        {
            throw new Exception("Marker labels are necessary!");
        }
    }

    /**
     * inserts a markerLabelFile in the database, 1000 lines at a time
     */
    private int parseMarkerLabelsFile() throws Exception
    {
        ArrayList<ArrayList<String>> labels = new ArrayList<ArrayList<String>>();

        String row;
        int processed = 0;
        int count = 0;
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(markerLabelsFile));
            while ((row = in.readLine()) != null)
            {
                //System.err.println(row);
                if (row.length() == 0)
                {
                    continue;
                }

                if (processed == 1000)
                {

                    boolean error = insertLabels(labels, count);
                    if (!error)
                    {
                        System.err.println("Error in inserting marker labels");
                        return 0;
                    }

                    labels.clear();
                    count += processed;
                    processed = 0;
                }


                row = row.trim();
                if (row.length() > 0)
                {
                    row = row.replace('\t', ' ');
                    while (row.indexOf("  ") >= 0)
                    {
                        row = row.replaceAll("  ", " ");
                    }
                    String[] rowValues = row.split(" ");
                    //System.err.println("Row size: " + rowValues.length);

                    ArrayList<String> requiredValues = new ArrayList<String>();
                    if (rowValues.length == 3)
                    {
                        requiredValues.add(rowValues[0]);
                        requiredValues.add(rowValues[2]);
                        requiredValues.add(rowValues[1]);
                    }
                    else if (rowValues.length >= 4)
                    {
                        requiredValues.add(rowValues[0]);
                        requiredValues.add(rowValues[3]);
                        requiredValues.add(rowValues[1]);
                    }
                    else
                    {
                        throw new Exception("Invalid file format!");
                    }

                    labels.add(requiredValues);
                    processed++;
                }

            }
            if (!labels.isEmpty())
            {
                boolean error = insertLabels(labels, count);
                count += processed;
                if (!error)
                {
                    System.err.println("Error in inserting marker labels");
                    return 0;
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * inserts the marker labels in the database
     */
    private boolean insertLabels(ArrayList<ArrayList<String>> labels, int offset)
    {
        //ArrayList<String> vals = new ArrayList<String>();
        ArrayList<String> cols = new ArrayList<String>();
        //ArrayList<String> whereArgs = new ArrayList<String>();
        ArrayList<ArrayList<String>> valList = new ArrayList<ArrayList<String>>();

        //System.out.println("Offset: " + offset);

        //System.err.println("Size: " + labels.size());
        cols.add("chr");
        cols.add("locus");
        cols.add("idx");
        cols.add("markersetid");
        cols.add("name");

        for (int i = 0; i < labels.size(); i++)
        {
            int idxInt = i + offset;
            String idx = Integer.toString(idxInt);
            ArrayList<String> tableRow = new ArrayList<String>();
            tableRow.add(labels.get(i).get(0));
            tableRow.add(labels.get(i).get(1));
            tableRow.add(idx);
            tableRow.add(markerSetId);
            tableRow.add(labels.get(i).get(2));
            valList.add(tableRow);
        }

        if (!DataManager.runMultipleInsertQuery(cols, valList, "marker"))
        {
            System.err.println("Error inserting in marker table");
            return false;
        }

        cols.clear();
        valList.clear();
        return true;
    }

    /**
     * Writes the marker ids for the current markerset to the given file name.
     * This is used when outside programs are run to import data.
     * @param file
     */
    private void writeIdstoFile(String file)
    {

        ArrayList<String> whereArgs = new ArrayList<String>();
        whereArgs.add("markersetid=" + markerSetId);

        ArrayList<String> minSelect = DataManager.runSelectQuery("min(id)", "marker", true, whereArgs, "idx");
        ArrayList<String> maxSelect = DataManager.runSelectQuery("max(id)", "marker", true, whereArgs, "idx");

        int min = Integer.parseInt(minSelect.get(0));
        int max = Integer.parseInt(maxSelect.get(0));

        try
        {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));

            while (min <= max)
            {
                whereArgs.clear();
                whereArgs.add("markersetid=" + markerSetId);
                whereArgs.add("id>=" + min);
                int temp = min + 40000;
                whereArgs.add("id<" + temp);

                ArrayList<String> markerIdStrings = DataManager.runSelectQuery("id", "marker", true, whereArgs, "idx");
                min += 40000;

                for (String idString : markerIdStrings)
                {
                    System.out.println(idString);
                    out.write(idString + '\n');
                }
            }

            out.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
