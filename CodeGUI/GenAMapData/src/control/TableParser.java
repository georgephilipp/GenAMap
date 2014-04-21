/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package control;
import static control.MatrixParser.checkTypeMatch;
import static control.MatrixParser.delimiterMap;
import java.io.*;
import java.util.*;

/**
 *
 * @author gschoenh
 */
public class TableParser 
{
    public int length;
    public boolean hasColHeader;
    public String colHeaderType;
    public ArrayList<String> colTypes;
    public ArrayList<ArrayList<Integer> > keys;
    public String delimiter;
    public ArrayList<HashSet<String> > regSets;
    
    int lineNumber;
    BufferedReader reader;
    ArrayList<HashMap> keyCache;
    String fileName;
        
    public TableParser()
    {
        length = -1;
        hasColHeader = false;
        colHeaderType = "String";
        colTypes = new ArrayList();
        keys = new ArrayList();
        delimiter = "Tab"; 
        lineNumber = 0;
        regSets = new ArrayList();
    }
    
    public String setup(String path)
    {
        try
        {
            if(reader != null)
                reader.close();
            FileInputStream fstream = new FileInputStream(path);
            DataInputStream in = new DataInputStream(fstream);
            reader = new BufferedReader(new InputStreamReader(in));   
            keyCache = new ArrayList();
            lineNumber = 0;
            fileName = path;
        }
        catch(IOException e)
        {
            return "Could not open path to file";
        }
        return "";
    }
    
    public void restart()
    {
        if(fileName.equals(""))
            throw new RuntimeException("cannot restart a parser that has no file name associated to it");
        setup(fileName);        
    }
    
    public int getLineNumber()
    {
        return lineNumber;
    }
    
    public int getNumEntriesSeen()
    {
        if(hasColHeader && lineNumber > 0)
            return lineNumber - 1;
        else
            return lineNumber;
    }
    
    static String keyToString(ArrayList<Integer> key)
    {
        StringBuilder res = new StringBuilder();
        int keySize = key.size();
        for(int i=0;i<keySize;i++)
        {
            res.append(key.get(i));
            if(i+1 != keySize)
                res.append(", ");                      
        }        
        return res.toString();
    }
    
    public ArrayList<String> readline()
    {      
        //set up key caches
        int numKeys = keys.size();
        if(lineNumber == 0)
        {
            for(int i=0;i<numKeys;i++)
                keyCache.add(new HashMap());
        }
                
        ArrayList<String> res = new ArrayList();
        
        //do pre-checks
        if(reader == null)
        {
            res.add("Input file was not provided to the parser");
            return res;
        }
        
        try
        {
            //get line
            String line = "";
            while(line != null && line.equals(""))
            {
                //the convention here is to ignore empty lines
                line = reader.readLine();
            }
            if(line != null)
                lineNumber++;
            
            //get delimiter
            String delim = "";
            if(delimiterMap.containsKey(delimiter))
                delim = delimiterMap.get(delimiter);
            else
            {
                res.add("Delimiter specified cannot be interpreted.\n If you chose this delimiter from a drop-down menu, this is a bug.\n Please contact the developers.\n");
                return res;                
            }            
                
            //check for end of file
            int numEntriesRead = lineNumber;
            if(hasColHeader)
                numEntriesRead = lineNumber - 1;
            if(length != -1)
            {
                if((numEntriesRead > length && line != null) || (numEntriesRead != length && line == null))
                {
                    res.add("The number of rows in the file did not match the expected number. \n Please check whether the number of rows in the file matches those in related data sets.\n");
                    return res;
                }              
            }
            if(line==null)
            {
                res.add("");
                res.add(null); 
                return res;
            }
            else
            {
                String[] parts = line.split(delim);
                
                //check whether the splitting worked
                int numParts = parts.length;
                int numCols = colTypes.size();
                if(numParts != numCols)
                {
                    res.add("When splitting row " + lineNumber + " on delimiter " + delimiter + ", " + numParts + " parts were obtained.\n The file requires " + numCols + "columns.\nPlease check whether you specified the right delimiter or whether you have non-standard lines such as comments in your file\n");
                    return res;                  
                }
                                
                //check whether type requirements work
                if(hasColHeader && lineNumber == 1)
                {
                    for(int i=0;i<numCols;i++)
                    {
                        if(!checkTypeMatch(parts[i], colHeaderType, regSets))
                        {
                            res.add("Column headers in file need to be convertible to type " + colHeaderType + "\n This was not true at position " + i);
                            return res;                            
                        } 
                    } 
                }
                else
                {
                    for(int i=0;i<numCols;i++)
                    {
                        if(!checkTypeMatch(parts[i], colTypes.get(i), regSets))
                        {
                            res.add("Entries in column " + i + " need to be convertible to type " + colTypes.get(i) + "\n This was not true at line " + lineNumber);
                            return res;                            
                        } 
                    }                     
                }
                
                //check whether the uniqueness requirement worked
                if(hasColHeader && lineNumber == 1)
                {
                    HashMap<String, Integer> colHeaderCache = new HashMap();
                    for(int i=0;i<numCols;i++)
                    {
                        if(colHeaderCache.containsKey(parts[i]))
                        {
                            res.add("Column Headers in file need to be unique.\n Header " + i + " is equal to " + colHeaderCache.get(parts[i]));
                            return res;                        
                        }
                        colHeaderCache.put(parts[i], i);
                    }                  
                }
                else
                {
                    for(int i=0;i<numKeys;i++)
                    {
                        ArrayList<Integer> key = keys.get(i);
                        int keySize = key.size();
                        HashMap curMap = keyCache.get(i);
                        boolean isNew = false;
                        for(int j=0;j<keySize;j++)
                        {
                            String curKey = parts[key.get(j)];
                            if(!curMap.containsKey(curKey))
                            {
                                if(j + 1 == keySize)
                                    curMap.put(curKey, lineNumber);
                                else
                                    curMap.put(curKey, new HashMap());
                                isNew = true;
                            }                            
                            if(j + 1 == keySize)
                            {
                                if(!isNew)
                                {
                                    res.add("Columns " + keyToString(key) + " form a key, but a non-unique result was detected.\nEntry in line " + lineNumber + " matches entry in line " + (Integer)curMap.get(curKey));
                                    return res;
                                }
                            }
                            else
                                curMap = (HashMap)curMap.get(curKey);
                        }
                    }
                }                
                        
                //Success! return the result
                res.add("");
                for(int i=0;i<numParts;i++)
                    res.add(parts[i]);
                return res;                
            }
        }
        catch(Exception e)
        {
            //unclear what went wrong
            res.add("An error occurred when parsing the file.\n Unfortuantely, we were unable to determine the cause.\n Please check your input file");
            return res;               
        }    
    }
    
    public String check()
    {
        ArrayList<String> line = new ArrayList();
        line.add("");
        line.add("");
        while(line.get(0).equals("") && line.get(1) != null)
            line = readline();
        return line.get(0);        
    }
}
