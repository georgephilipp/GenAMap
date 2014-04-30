/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package control;
import java.io.*;
import java.util.*;
/**
 *
 * @author gschoenh
 */
public class MatrixParser 
{
    public int width;
    public boolean autoSetWidth;
    public int length;
    public String entryType;
    public boolean hasColHeader;
    public boolean hasRowHeader;
    public String colHeaderType;
    public String rowHeaderType;
    public String delimiter;
    public ArrayList<HashSet<String> > regSets;
    
    int lineNumber;
    String fileName;
    BufferedReader reader;
    HashMap<String, Integer> rowHeaderCache;
    
    public static final Map<String, String> delimiterMap;
    static
    {
        HashMap<String, String> delMap = new HashMap();
        delMap.put("Tab", "\t");
        delMap.put("Whitespace", " ");
        delMap.put("Comma", ",");   
        delimiterMap = Collections.unmodifiableMap(delMap);
    }
    
    public MatrixParser()
    {
        width = -1;
        autoSetWidth = true;
        length = -1;
        entryType = "String";
        hasColHeader = false;
        hasRowHeader = false;
        colHeaderType = "String";
        rowHeaderType = "String";
        delimiter = "Tab"; 
        lineNumber = 0;
        fileName = "";
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
            rowHeaderCache = new HashMap();
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
    
    public static boolean checkTypeMatch(String entry, String type, ArrayList<HashSet<String> > regSets)
    {
        Integer intVal = 0;
        
        if(type.equals("int") || type.equals("posInt") || type.equals("marker") || type.equals("nonNegInt"))
        {
            try
            {
                intVal = Integer.parseInt(entry);
            }
            catch(Exception e)
            {
                return false;
            }    
            
            if(type.equals("posInt"))
            {
                if(intVal <= 0)
                    return false;
            }

            if(type.equals("nonNegInt"))
            {
                if(intVal < 0)
                    return false;
            }

            if(type.equals("marker"))
            {
                if(intVal != 0 && intVal != 1 && intVal != 2)
                {
                    return false;                
                }            
            }
        }
        else if(type.equals("double"))
        {
            try
            {
                Double doubleVal = Double.parseDouble(entry);
                if(doubleVal.isInfinite() || doubleVal.isNaN())
                    return false;
            }
            catch(Exception e)
            {
                return false;
            }
        }
        else if(type.equals("float"))
        {
            try
            {
                Float floatVal = Float.parseFloat(entry);
                if(floatVal.isInfinite() || floatVal.isNaN())
                    return false;
            }
            catch(Exception e)
            {
                return false;
            }
        }
        else if(type.startsWith("String"))
        {
            String[] split1 = type.split("\\(");
            if(split1.length > 2)
                throw new RuntimeException();
            if(split1.length == 2)
            {
                String[] split2 = split1[1].split("\\)");
                if(split2.length != 1)
                    throw new RuntimeException();
                int maxLength = Integer.parseInt(split2[0]);
                if(entry.length() > maxLength)
                    return false;
            }          
        }
        else if(type.startsWith("special"))
        {
            String[] split = type.split(":");
            if(split.length != 2)
                throw new RuntimeException();
            int regSetIndex = Integer.parseInt(split[1]);
            if(!regSets.get(regSetIndex).contains(entry))
                return false;
        }
        else
            throw new RuntimeException();    

        return true;
    }
    
    public ArrayList<String> readline()
    {
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
                int requiredParts = 1;
                int numEntries = numParts;
                int firstEntryIndex = 0;
                int lastEntryIndex = numEntries - 1;
                if(hasRowHeader)
                {
                    numEntries--;
                    firstEntryIndex++; 
                    requiredParts++;
                }
                if(numParts < requiredParts)
                {
                    String ErrString = "When splitting row " + lineNumber + " on delimiter " + delimiter + ", " + numParts + " parts were obtained.\n";
                    if(hasRowHeader)
                        ErrString = ErrString + "For a matrix file with row headers, at least 2 parts are necessary.\n";
                    else
                        ErrString = ErrString + "For a matrix file with no row headers, at least 1 part is necessary.\n";
                    ErrString = ErrString + "Please check whether you specified the right delimiter or whether you have non-standard lines such as comments in your file\n";
                    res.add(ErrString);
                    return res;                  
                }
                
                //check whether the width requirement works
                if(width == -1 && autoSetWidth)
                    width = numEntries;
                if(width != -1 && width != numEntries)
                {
                    String ErrString = "When splitting row " + lineNumber + " on delimiter " + delimiter + ", " + numParts + " parts were obtained.\n";
                    ErrString = ErrString + "This does not match up either with the other rows in the file or with another data set that this matrix file relates to.\n";
                    if(hasRowHeader)
                        ErrString = ErrString + "We require " + (width+1) + " parts, one of which is the row header.\n";
                    else
                        ErrString = ErrString + "We require " + width + " parts.\n";
                    ErrString = ErrString + "Please check whether you specified the right delimiter or whether you have non-standard lines such as comments in your file.\n";
                    res.add(ErrString);
                    return res;
                }
                
                //check whether type requirements work
                if(hasRowHeader && (lineNumber > 1 || !hasColHeader))
                {
                    String rowHeader = parts[0];
                    if(!checkTypeMatch(rowHeader,rowHeaderType,regSets))
                    {
                        res.add("Row Headers in matrix file need to be convertible to type " + rowHeaderType + "\n This was not true at line " + lineNumber);
                        return res;
                    }
                }
                if(hasColHeader && lineNumber == 1)
                {
                    for(int i=firstEntryIndex;i<=lastEntryIndex;i++)
                    {
                        if(!checkTypeMatch(parts[i], colHeaderType, regSets))
                        {
                            res.add("Column headers in matrix file need to be convertible to type " + colHeaderType + "\n This was not true at position " + i);
                            return res;                            
                        } 
                    } 
                }
                else
                {
                    for(int i=firstEntryIndex;i<=lastEntryIndex;i++)
                    {
                        if(!checkTypeMatch(parts[i], entryType, regSets))
                        {
                            res.add("Entries in matrix file need to be convertible to type " + entryType + "\n This was not true at line " + lineNumber + " entry number " + i);
                            return res;                            
                        } 
                    }                     
                }
                
                //check whether the uniqueness requirement worked
                if(hasColHeader && lineNumber == 1)
                {
                    HashMap<String, Integer> colHeaderCache = new HashMap();
                    for(int i=firstEntryIndex;i<=lastEntryIndex;i++)
                    {
                        if(colHeaderCache.containsKey(parts[i]))
                        {
                            res.add("Column Headers in matrix file need to be unique.\n Header " + i + " is equal to " + colHeaderCache.get(parts[i]));
                            return res;                        
                        }
                        colHeaderCache.put(parts[i], i);
                    }                  
                }
                else if(hasRowHeader)
                {
                    if(rowHeaderCache.containsKey(parts[0]))
                    {
                        res.add("Row Headers in matrix file need to be unique.\n Header of row " + lineNumber + " is equal to header of row " + rowHeaderCache.get(parts[0]));
                        return res;                        
                    }
                    rowHeaderCache.put(parts[0], lineNumber);
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
