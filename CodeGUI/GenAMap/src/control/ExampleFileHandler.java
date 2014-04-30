/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
/**
 *
 * @author gschoenh
 */
public class ExampleFileHandler 
{
    public static String failMessage = "I can't open the example file.\n Please look in the quickExamples or the Documentation/ExampleData folder or read Documentation/ExampleDataGuide.pdf"; 
    
    static String outputMessage(Process proc)
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line = "";
        try
        {
            while ( (line = br.readLine()) != null) 
            {
               builder.append(line);
            }
        }
        catch(IOException ex)
        {
            return "Could not read output stream";
        }
        
        return builder.toString();
    }
    
    static String errorMessage(Process proc)
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        StringBuilder builder = new StringBuilder();
        String line = "";
        try
        {
            while ( (line = br.readLine()) != null) 
            {
               builder.append(line);
            }
        }
        catch(IOException ex)
        {
            return "Could not read output stream";
        }
        
        return builder.toString();
    }
    
    
    public static boolean display(String fileCode)
    {
        String fileName = "quickExamples/" + fileCode + ".txt";
        
        Runtime load = Runtime.getRuntime();
        try
        {
            load.exec("notepad " + fileName);
        }
        catch (IOException ex)
        {
            try
            {
                load.exec("gedit " + fileName);
            }
            catch (IOException ex1)
            {
                try
                {
                    Process res = load.exec("open -e " + fileName);
                    String out = outputMessage(res);
                    String err = errorMessage(res);
                    if(!out.equals("")|| !err.equals(""))
                        return false;
                }
                catch (IOException ex2)
                {   
                    return false;
                }
            }
        }
        
        return true;        
    }    
}
