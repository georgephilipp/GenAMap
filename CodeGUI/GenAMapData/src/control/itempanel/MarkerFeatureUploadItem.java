package control.itempanel;

import control.DataAddRemoveHandler;
import datamodel.MarkerSet;
import realdata.DataManager;
import java.util.ArrayList;
import javax.swing.JFrame;
import control.MatrixParser;
import java.util.HashSet;
import javax.swing.JOptionPane;

/**
 * This will upload a read-in association set into the database. The file is
 * actually parsed and read in this method.
 * @author rcurtis
 */
public class MarkerFeatureUploadItem extends ThreadItem
{
    /**
     * the status of the currently executing item
     */
    private String status = "In queue ...";
    /**
     * the text of the last error
     */
    private String errorText = "";
    /**
     * The name of the association being uploaded
     */
    private String name;
    /**
     * The form to call repaint on when the status or percent complete changes
     */
    private JFrame form;
    /**
     * The markerset that this associationset belongs to
     */
    private MarkerSet markerSet;
   
    /**
     * The id of the project 
     */
    private int projectId;
   
    /**
     * The name of the file we are reading
     */
    private String fileName;

    /**
     * Creates a new AssociationUploadItem that will run to read the file and
     * insert the association set into the database
     * @param form the form to repaint when status changes
     * @param name the name of the association set
     * @param ts the traitset that we are working with
     * @param ms the markerset that we are working wit h
     * @param projID the id of the project we are working with
     */
    public MarkerFeatureUploadItem(JFrame form, MarkerSet ms, int projID,
            String filename)
    {
        this.form = form;
        this.markerSet = ms;
        this.projectId = projID;
        this.fileName = filename;
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
        return  "Features upload complete.";
    }

    @Override
    public void start()
    {
        Task t = new Task();
        t.start();
    }
    
    private String makeOrBlock(ArrayList<String> vals, String field, boolean isString)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for(int i=0;i<vals.size();i++)
        {
            builder.append(field);
            builder.append("=");
            if(isString)
                builder.append("\"");
            builder.append(vals.get(i));
            if(isString)
                builder.append("\"");
            if(i+1 != vals.size())
                builder.append(" OR ");
            else
                builder.append(")");
        }  
        return builder.toString();
    }
    
    class Task extends Thread
    {
        @Override
        public void run()
        {
            setValue(1);
            status = "Checking file ...";
            form.repaint();
            
            String msid = Integer.toString(markerSet.getId());
            
            MatrixParser mparser = new MatrixParser();
            mparser.autoSetWidth = true;
            mparser.hasColHeader = true;
            mparser.colHeaderType = "String(30)";
            mparser.entryType = "float";
            ArrayList<String> whereArgs = new ArrayList<String>();
            whereArgs.add("markersetid=" + msid);
            ArrayList<String> markerIds = DataManager.runSelectQuery("id", "marker", true, whereArgs, "idx");
            int numMarkers = markerIds.size();
            mparser.length = numMarkers;
            mparser.delimiter = "Tab";
            mparser.setup(fileName);
            
            String check = mparser.check();
            
            if(!check.equals(""))
            {
                JOptionPane.showMessageDialog(null, "Error while checking the data file.\n This is likely caused by in incorrectly formatted input file.\n Error message was:\n" + check);
                errorText = "Error while checking data file";
                setIsError(true);
                form.repaint();
                return;
            }
            
            mparser.restart();
            
            whereArgs.clear();
            whereArgs.add("markersetid=" + markerSet.getId());
            ArrayList<String> curFeatureList = DataManager.runSelectQuery("name", "feature", true, whereArgs, null);
            HashSet<String> curFeatureSet = new HashSet(curFeatureList);
            
            ArrayList<String> featureNames = mparser.readline();
            featureNames.remove(0);
            int numFeatures = featureNames.size();
            for(int i=0;i<featureNames.size();i++)
            {
                if(curFeatureSet.contains(featureNames.get(i)))
                {
                    JOptionPane.showMessageDialog(null, "Error while checking the data file.\nOne of the feature names in the data file is the name of a feature that already exists for this marker data set." );
                    errorText = "Error while checking data file";
                    setIsError(true);
                    form.repaint();
                    return;
                }
            }
            
            ArrayList<String> featureIds = null;
            boolean inserted = false;
            
            try
            {
                setValue(2);
                status = "Inserting feature names into DB ...";
                form.repaint();
                
                ArrayList<String> cols = new ArrayList();
                cols.add("name");
                cols.add("markersetid");
                
                ArrayList<ArrayList<String> > allVals = new ArrayList();
                for(String feature : featureNames)
                {
                    ArrayList<String> vals = new ArrayList();
                    vals.add(feature);
                    vals.add(msid);
                    allVals.add(vals);
                }
                
                if (!DataManager.runMultipleInsertQuery(cols, allVals, "feature"))
                {
                     throw new RuntimeException("failed to insert feature names");
                }
                
                inserted = true;
                
                whereArgs.clear();
                whereArgs.add(makeOrBlock(featureNames, "name", true));
                whereArgs.add("markersetid=" + msid);
                featureIds = DataManager.runSelectQuery("id", "feature", true, whereArgs, null);
                    
                if(featureIds.size() != numFeatures)
                {
                    throw new RuntimeException("failed to get feature ids");
                }
                                               
                setValue(3);
                status = "Importing features ...";
                form.repaint();
                
                cols.clear();
                cols.add("featureid");
                cols.add("markerid");
                cols.add("value");
                
                allVals.clear();
                for(int i=0;i<numMarkers;i++)
                {
                    ArrayList<String> line = mparser.readline();
                    for(int j=0;j<numFeatures;j++)
                    {
                        ArrayList<String> vals = new ArrayList();
                        vals.add(featureIds.get(j));
                        vals.add(markerIds.get(i));
                        vals.add(line.get(j+1));
                        allVals.add(vals);      
                        if(allVals.size() >= 1000)
                        {
                            if(!DataManager.runMultipleInsertQuery(cols, allVals, "featureval"))
                            {
                                throw new RuntimeException("failed to insert feature values");
                            }
                            allVals.clear();
                        }
                    }
                    setValue(3+(int)(96.0*((double)i)/((double)numMarkers)));
                    form.repaint();
                }
                if(allVals.size() > 0)
                {
                    if(!DataManager.runMultipleInsertQuery(cols, allVals, "featureval"))
                    {
                        throw new RuntimeException("failed to insert feature values");
                    }
                }
                else
                    throw new RuntimeException("bla");
            }
            catch (Exception ex)
            {                              
                if(featureIds != null)
                {
                    whereArgs.clear();
                    whereArgs.add(makeOrBlock(featureIds, "featureid", false));
                    whereArgs.add(makeOrBlock(markerIds, "markerid", false));
                    DataManager.deleteQuery("featureval", whereArgs);
                }
                
                if(inserted)
                {
                    whereArgs.clear();
                    whereArgs.add(makeOrBlock(featureNames, "name", true));
                    whereArgs.add("markersetid=" + msid);
                    DataManager.deleteQuery("feature", whereArgs);
                }
                                
                JOptionPane.showMessageDialog(null, "Error while uploading the data file into the database.\nThis is a bug. Please contact the developers.\n Error message was: " + ex.getMessage());
                errorText = ex.getMessage();
                setIsError(true);
                form.repaint();
                return;
            }
            
            DataAddRemoveHandler.getInstance().refreshDisplay();
            setValue(100);
        }
    }
}
