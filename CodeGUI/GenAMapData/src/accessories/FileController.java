package accessories;

import java.io.*;
import java.util.ArrayList;

/**
 * FileController will take a string representing a file location, parse the file,
 * and then be able to return column headers, row headers, and the values in the
 * parsed file as a matrix.
 * @author ross
 */
public class FileController
{
    /**
     * Will be marked true when the file is read to completion, marking a complete
     * file.
     */
    private boolean isRead = false;
    private String delim;
    private ArrayList<ArrayList<Object>>  values;
    private ArrayList<String> rowHeaders;
    private ArrayList<String> colHeaders;

    /**
     * Will return the matrix that has been parsed from the file. If row and
     * header information were requested, these will have been removed.
     * @return a double ArrayList containing an object representing each
     * value in the file. Rows x Columns
     */
    public ArrayList<ArrayList<Object>> getMatrix()
    {
        return values;
    }

    /**
     * Returns the row headers that were parsed from this file.
     * @return an ArrayList of headers.
     */
    public ArrayList<String> getRowHeaders()
    {
        return rowHeaders;
    }

    /**
     * Returns the column headers that were parsed from this file
     * @return an ArrayList of headers.
     */
    public ArrayList<String> getColHeaders()
    {
        return colHeaders;
    }

    /**
     * Instantiates this FileController object. The file is read in, parsed into
     * its objects, and is ready for further access.
     * @param fileLoc the location of the file to be read in.
     * @param delim the delimiter between values in the file, could be "w" meaning white space
     * @param headers indicates whether or not there are headers in this file. 
     */
    public FileController(String fileLoc, String delim, boolean headers)
    {
        this.delim = delim;
        values = new ArrayList<ArrayList<Object>>();
        if(headers) rowHeaders = new ArrayList<String>();

        boolean hasReadHeader = false;
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(fileLoc));
            String str;

            int val = 0;
            while ((str = in.readLine()) != null)
            {
                if(!hasReadHeader && headers)
                {
                    readHeader(str);
                    hasReadHeader = true;
                }
                else
                {
                    System.out.println(val);
                    val++;
                    process(str, headers);
                }
            }
            in.close();
            isRead = true;
        }
        catch (IOException e)
        {

        }
    }

    public FileController(String fileLoc, String delim, boolean headers, int skipLines)
    {
        this.delim = delim;
        values = new ArrayList<ArrayList<Object>>();
        if(headers) rowHeaders = new ArrayList<String>();

        boolean hasReadHeader = false;
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(fileLoc));
            String str;

            int val = 0;
            int processed = 0;
            while ((str = in.readLine()) != null)
            {
                if(!hasReadHeader && headers)
                {
                    readHeader(str);
                    hasReadHeader = true;
                }
                else
                {
                    System.out.println(val);

                    if(val >= skipLines && processed < 10){
                        process(str, headers);
                        processed++;
                    }
                    else break;
                    val++;
                }
            }
            in.close();
            isRead = true;
        }
        catch (IOException e)
        {

        }
    }
    
    /**
     * Reads in the column header information from the first line.
     * @param str the first line.
     */
    private void readHeader(String str)
    {
        String del = delim;
        if(delim.equals("w"))
        {
            del = " ";
            str = processWhiteSpace(str);
        }
        
        String[] stuff = str.split(del);
        colHeaders = new ArrayList<String>();

        for(int i = 1; i < stuff.length; i ++)
        {
            colHeaders.add(stuff[i]);
        }
    }

    /**
     * Accounts for all white space in the line to make it into a space-delimited line.
     * @param s the string to process the white space from
     * @return the string with the processed white space removed.
     */
    private String processWhiteSpace(String s)
    {
        String del = " ";
        s = s.replace('\t', ' ');
        while(s.indexOf("  ") >= 0)
        {
            s = s.replaceAll("  ", del);
        }
        return s;
    }

    private void process(String str, boolean hasHeaders) throws IllegalArgumentException
    {
        str = str.trim();
        String del = delim;
        if(delim.equals("w"))
        {
            del = " ";
            str = processWhiteSpace(str);
        }

        String[] stuff = str.split(del);

        if(hasHeaders)rowHeaders.add(stuff[0]);

        ArrayList<Object> row = new ArrayList<Object>();
       
        int i = 0;
        if(hasHeaders) i = 1;

        if(i == 1 && stuff.length < 2 ||
           i == 0 && stuff.length < 1)
        {
            throw new IllegalArgumentException("Error reading file - do you have the right delimeter?");
        }

        System.out.println("Line length: " + stuff.length);
        for(; i < stuff.length; i ++)
        {
            try
            {
                
                if(stuff[i].length() > 0)
                    row.add(Double.parseDouble(stuff[i]));
            }
            catch (Exception e)//this isn't a double
            {
                row.add(stuff[i]);
            }
        }

        values.add(row);
    }

    /**
     * Indicates whether the file was in the appropriate format.
     * @return whether or not this file was read successfully
     */
    public boolean isValidFile()
    {
        return isRead;
    }

    /**
     * A static method which will read in a file where labels are present one per
     * line and will return the labels as an array.
     * @param file The file that will be parsed and read.
     * @return an ArrayList containing all of the labels from the file. 
     */
    public static ArrayList<String> readLabelFile(String file)
    {
        ArrayList<String> labels = new ArrayList<String>();

        try
        {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String str;
            while ((str = in.readLine()) != null)
            {
                labels.add(str);
            }
            in.close();
        }
        catch (IOException e)
        {
            return null;
        }

        return labels;
    }
}
