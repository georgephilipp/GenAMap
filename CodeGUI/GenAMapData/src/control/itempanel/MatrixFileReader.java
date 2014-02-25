package control.itempanel;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * First, the import for markers happened that we read it all into memory. Overflow.
 * So, we switched to a separate commandline process. There is no way that this can scale.
 *
 * So, now we put the marker file reader back into the java code. Hopefully it can work!
 *
 * This class reads a marker file, parses it, and sends the query commands back
 * to the caller.
 * @author rcurtis
 */
public class MatrixFileReader
{
    /**
     * A file with the list of all samples
     */
    private ArrayList<Integer> samples;
    /**
     * A file with a list of all markers
     */
    private BufferedReader markers;
    /**
     * to read in the file line by line
     */
    private BufferedReader myStream;
    /**
     * input
     */
    private double x;
    /**
     * helper variables
     */
    private int count, markerid, arrayval;
    /**
     * to keep track of position
     */
    int lastmarkerid = -1;

    /**
     * Constructor
     * @param sampleFile
     * @param markerFile
     */
    public MatrixFileReader(String markerFile, String sampleFile, String matrixFile) throws FileNotFoundException, IOException
    {
        samples = new ArrayList<Integer>();

        FileInputStream fstream = new FileInputStream(markerFile);
        DataInputStream in = new DataInputStream(fstream);
        markers = new BufferedReader(new InputStreamReader(in));

        fstream = new FileInputStream(sampleFile);
        in = new DataInputStream(fstream);
        BufferedReader br1 = new BufferedReader(new InputStreamReader(in));

        String str;

        while ((str = br1.readLine()) != null)
        {
            if (str.trim().length() > 0)
            {
                samples.add(Integer.parseInt(str));
            }
        }

        fstream = new FileInputStream(matrixFile);
        in = new DataInputStream(fstream);
        myStream = new BufferedReader(new InputStreamReader(in));

        str=null;

        while ((str = br1.readLine()) != null)
        {
            if (str.trim().length() > 0)
            {
                samples.add(Integer.parseInt(str));
            }
        }
    }

    /**
     * Returns a query line
     * @return
     */
    public ArrayList<String> readline() throws IOException
    {
        String s = markers.readLine();
        if(s == null)
        {
            markers.close();
            myStream.close();
            return null;
        }
        Double d = Double.parseDouble(s);
        markerid = d.intValue();
        ArrayList<String> queries = new ArrayList<String>();

        String toRet = "INSERT INTO markerval (sampleid, markerid, value) VALUES ";

        String line = myStream.readLine();
        line = line.trim();
        String[] vals = line.split("\\s+");
        int cnt = 0;

        for(count = 0; count < vals.length; count++)
        {
            d = Double.parseDouble(vals[count]);
            x = d;
            toRet += "(" + markerid + "," + samples.get(count) + "," +  x + "),";
            cnt ++;

            if(cnt == 500 && count != vals.length - 1)
            {
                queries.add(toRet.substring(0, toRet.length()-1));
                toRet = "INSERT INTO markerval (sampleid, markerid, value) VALUES ";
                cnt = 0;
            }
            else if(count == vals.length - 1)
            {
                queries.add(toRet.substring(0, toRet.length()-1));
            }
        }

        return queries;
    }
}
