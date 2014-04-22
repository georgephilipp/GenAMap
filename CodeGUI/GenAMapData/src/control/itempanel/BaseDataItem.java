package control.itempanel;

import control.DataAddRemoveHandler;
import control.MatrixParser;
import control.TableParser;
import datamodel.MarkerSet;
import datamodel.Model;
import datamodel.Project;
import datamodel.TraitSet;
import java.util.ArrayList;
import javax.swing.JFrame;
import realdata.DataManager;
import javax.swing.JOptionPane;

/**
 * A BaseDataItem will import a markerset or traitset into the database on a separate thread.
 * @author flaviagrosan / gschoenh
 */
public class BaseDataItem extends ThreadItem
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
     * The type of the data set.
     */
    private String dataSetType;
    /**
     * The name that this data set will have. 
     */
    private String dataSetName;
    /**
     * The file with the feature labels in it
     */
    private String featureLabelFile;
    /**
     * The file with the sample labels in it
     */
    private String sampleLabelFile;
    /**
     * The matrix with all the data in it - stored in a delimited file
     */
    private String matrixFile;
    /**
     * true if this file is KxN, or false if it is NxK
     */
    private boolean isKxNFormat;
    /**
     * Only import feature names
     */
    private boolean noData;
    /**
     * All information - sample labels, feature labels and feature values - are contained within a single file
     */
    private boolean singleFile;
    /**
     * Tells us whether to generate sample labels, or to read them from the data file.
     */
    private boolean isGenSamps;
    /**
     * Chromosome location for markers may be specified. This is used by the chromosome browser.
     */
    private boolean withChromosomeLocation;
    /**
     * The name of the character used to seperate fields in files.
     */
    private String delimiter;
    /**
     * The id of the species that this data set belongs to (only relevant for traits)
     */
    private int speciesId;
    
    /**
     * The current status of the trait upload. 
     */
    private String status = "In queue ... ";
    /**
     * The string of any errors that have happened
     */
    private String errorText = "";
    /**
     * Creates a new BaseDataItem. 
     */
    public BaseDataItem(
            JFrame form, 
            String projectId,
            String dataSetType,
            String dataSetName,
            String featureLabelFile,
            String sampleLabelFile,
            String matrixFile, 
            boolean isKxNFormat,  
            boolean noData, 
            boolean singleFile, 
            boolean isGenSamps,
            boolean withChromosomeLocation,
            String delimiter,
            int speciesId
            )
    {
        this.setValue(0);
        this.form = form;
        this.projectId = projectId;
        this.dataSetType = dataSetType;
        this.dataSetName = dataSetName;        
        this.featureLabelFile = featureLabelFile;
        this.sampleLabelFile = sampleLabelFile;
        this.matrixFile = matrixFile;
        this.isKxNFormat = isKxNFormat;
        this.noData = noData;
        this.singleFile = singleFile;
        this.isGenSamps = isGenSamps;
        this.withChromosomeLocation = withChromosomeLocation;
        this.delimiter = delimiter;
        this.speciesId = speciesId;
    }

    @Override
    public void start()
    {
        BaseDataItem.Task t = new BaseDataItem.Task();
        t.start();
    }

    @Override
    public String getName()
    {
        return this.dataSetName;
    }

    @Override
    public String getErrorText()
    {
        return errorText;
    }

    @Override
    public String getStatus()
    {
        return this.status;
    }

    @Override
    public String getSuccessMessage()
    {
        return this.dataSetName + " upload complete. ";
    }
    class Task extends Thread
    {
        @Override
        public void run()
        {
            //initialize some objects
            TableParser fparser = new TableParser();
            TableParser sparser = new TableParser();
            MatrixParser vparser = new MatrixParser();
            int numFeatures = -1;
            int numSamples = -1;
            String dataSetId = "";
            ArrayList<String> line;
            try
            {
                if(!singleFile)
                {
                    //check feature label file
                    status = "Checking " + dataSetType + " label file ...";
                    setValue(1);
                    form.repaint();

                    fparser.colTypes.add("String(30)");
                    ArrayList<Integer> featureNameKey = new ArrayList();
                    featureNameKey.add(0);
                    fparser.keys.add(featureNameKey); 
                    if(withChromosomeLocation)
                    {
                        fparser.colTypes.add("posInt");
                        fparser.colTypes.add("posInt");
                        ArrayList<Integer> chromosomeLocationKey = new ArrayList();
                        chromosomeLocationKey.add(1);
                        chromosomeLocationKey.add(2);
                        fparser.keys.add(chromosomeLocationKey);
                    }                                        
                    fparser.delimiter = delimiter;
                    fparser.setup(featureLabelFile);

                    String check = fparser.check();

                    if(!check.equals(""))
                    {
                        JOptionPane.showMessageDialog(null, "There was an error while checking the " + dataSetType + " label file.\n This is likely caused by an incorrectly formatted input file.\n The error message was:\n" + check);
                        errorText = "Error while checking " + dataSetType + " label file.";
                        setIsError(true);
                        form.repaint();
                        return; 
                    }

                    numFeatures = fparser.getLineNumber();
                    fparser.length = numFeatures;
                    fparser.restart();
                    
                    //check sample label file
                    if(!isGenSamps && !noData)
                    {
                        status = "Checking sample label file ...";
                        setValue(2);
                        form.repaint();
                        
                        sparser.colTypes.add("String(30)");
                        ArrayList<Integer> sampleNameKey = new ArrayList();
                        sampleNameKey.add(0);
                        sparser.keys.add(sampleNameKey);
                        sparser.delimiter = delimiter;                       
                        sparser.setup(sampleLabelFile);

                        check = sparser.check();

                        if(!check.equals(""))
                        {
                            JOptionPane.showMessageDialog(null, "There was an error while checking the sample label file.\n This is likely caused by an incorrectly formatted input file.\n The error message was:\n" + check);
                            errorText = "Error while checking sample label file.";
                            setIsError(true);
                            form.repaint();
                            return; 
                        }

                        numSamples = sparser.getLineNumber();
                        sparser.length = numSamples;
                        sparser.restart();    
                    }
                }
                
                //check matrix file
                if(!noData)
                {
                    status = "Checking " + dataSetType + " value file ...";
                    setValue(3);
                    form.repaint();

                    if(isKxNFormat)
                    {
                        vparser.width = numSamples;
                        vparser.length = numFeatures;                                            
                    }
                    else
                    {
                        vparser.width = numFeatures;
                        vparser.length = numSamples;      
                    }
                    if(vparser.width == -1)
                        vparser.autoSetWidth = true;
                    if(singleFile)
                    {
                        vparser.hasColHeader = true;                        
                        vparser.hasRowHeader = true;
                        vparser.rowHeaderType = "String(30)";
                        vparser.colHeaderType = "String(30)";
                    }
                    vparser.entryType = "float";
                    vparser.delimiter = delimiter;
                    vparser.setup(matrixFile);

                    String check = vparser.check();

                    if(!check.equals(""))
                    {
                        JOptionPane.showMessageDialog(null, "There was an error while checking the " + dataSetType + " value file.\n This is likely caused by an incorrectly formatted input file.\n The error message was:\n" + check);
                        errorText = "Error while checking " + dataSetType + " value file.";
                        setIsError(true);
                        form.repaint();
                        return; 
                    }
                    
                    if(numSamples == -1)
                    {
                        if(isKxNFormat)
                            numSamples = vparser.width;
                        else
                        {
                            numSamples = vparser.getNumEntriesSeen();
                            vparser.length = numSamples;
                        }
                            
                    }
                    if(numFeatures == -1)
                    {
                        if(isKxNFormat)
                        {
                            numFeatures = vparser.getNumEntriesSeen();
                            vparser.length = numFeatures;
                        }
                        else
                            numFeatures = vparser.width;
                    }
                    
                    vparser.restart();
                }                        
                
                //insert data set name
                setValue(4);
                status = "Inserting data set name ...";
                form.repaint();
                ArrayList<String> vals = new ArrayList<String>();
                ArrayList<String> cols = new ArrayList<String>();
                ArrayList<String> whereArgs = new ArrayList<String>();
                cols.add("name");
                vals.add(dataSetName);
                cols.add("projectid");
                vals.add(projectId);
                if(dataSetType.equals("trait"))
                {
                    cols.add("speciesid");
                    vals.add(speciesId + "");
                }
                
                if (!DataManager.runInsertQuery(cols, vals, dataSetType + "set"))
                {
                    JOptionPane.showMessageDialog(null, "There was an error inserting the " + dataSetType + " name into the database.\n This is a bug. Please contact the developers. The error message was:\n" + DataManager.getLastError());
                    errorText = DataManager.getLastError();
                    setIsError(true);
                    form.repaint();
                    return; 
                }
                cols.clear();
                vals.clear();
                whereArgs.add("name='" + dataSetName + "'");
                whereArgs.add("projectid=" + projectId);
                dataSetId = (String) DataManager.runSelectQuery("id", dataSetType + "set", true, whereArgs, null).get(0);
                whereArgs.clear();
                
                //load sample labels                
                ArrayList<String> sampleIds = new ArrayList<String>();
                ArrayList<String> samples = new ArrayList<String>();
                if (!noData)
                {
                    status = "Importing sample labels ...";
                    setValue(5);
                    form.repaint();
                    
                    try
                    {
                        if (!singleFile)
                        {
                            if(!isGenSamps)
                            {
                                for(int i=0;i<numSamples;i++)
                                {
                                    line = sparser.readline();
                                    samples.add(line.get(1));
                                }
                                sparser.restart();
                            }
                            else
                            {
                                for(int i=1;i<=numSamples;i++)
                                {
                                    samples.add("S" + i);
                                }
                            }
                        }
                        else
                        {
                            if(isKxNFormat)
                            {
                                line = vparser.readline();
                                for(int i=0;i<numSamples;i++)
                                    samples.add(line.get(i+2));
                                vparser.restart();
                            }
                            else
                            {
                                vparser.readline();
                                for(int i=0;i<numSamples;i++)
                                    samples.add(vparser.readline().get(1));
                                vparser.restart();
                            }
                        }
                    }
                    catch(Exception e)
                    {
                        JOptionPane.showMessageDialog(null, "There was an error loading the sample labels.\n This is likely a bug. Please contact the developers.");
                        setIsError(true);
                        errorText = "Error loading sample labels";
                        form.repaint();
                        return;
                    }
                      
                    //insert sample ids
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
                                JOptionPane.showMessageDialog(null, "There was an error inserting the sample labels into the database.\n This is a bug. Please contact the developers. The error message was:\n" + DataManager.getLastError());
                                setIsError(true);
                                errorText = DataManager.getLastError();
                                form.repaint();
                                return;
                            }
                            vals.clear();
                            sampleVals = DataManager.runSelectQuery("id", "sample", true, whereArgs, null);
                        }
                        if(sampleVals.isEmpty())
                        {
                            JOptionPane.showMessageDialog(null, "There was an error retrieving the sample id from the database.\n This is a bug. Please contact the developers. The error message was:\n" + DataManager.getLastError());
                            setIsError(true);
                            errorText = DataManager.getLastError();
                            form.repaint();
                            return;
                        }
                        sampleIds.add(sampleVals.get(0));
                        whereArgs.clear();
                    }
                }
                cols.clear();
                
                //load feature labels
                status = "Importing " + dataSetType + " labels ...";
                setValue(6);
                form.repaint();
                ArrayList<String> featurenames = new ArrayList<String>();
                ArrayList<String> chrNums = new ArrayList();
                ArrayList<String> chrLocs = new ArrayList();
                try
                {
                    if (!singleFile)
                    {
                        for(int i=0;i<numFeatures;i++)
                        {
                            line = fparser.readline();
                            featurenames.add(line.get(1));
                            if(withChromosomeLocation)
                            {
                                chrNums.add(line.get(2));
                                chrLocs.add(line.get(3));
                            }
                        }
                        fparser.restart();
                    }
                    else
                    {
                        if(!isKxNFormat)
                        {
                            line = vparser.readline();
                            for(int i=0;i<numFeatures;i++)
                                featurenames.add(line.get(i+2));
                            vparser.restart();
                        }
                        else
                        {
                            vparser.readline();
                            for(int i=0;i<numFeatures;i++)
                                featurenames.add(vparser.readline().get(1));
                            vparser.restart();
                        }
                    }
                }
                catch(Exception e)
                {
                    JOptionPane.showMessageDialog(null, "There was an error loading the " + dataSetType + " labels.\n This is likely a bug. Please contact the developers.");
                    setIsError(true);
                    errorText = "Error loading " + dataSetType + " labels ...";
                    form.repaint();
                    return;
                }
                if(dataSetType.equals("marker") && !withChromosomeLocation)
                {
                    for(int i=1;i<=numFeatures;i++)
                    {
                        chrNums.add("1");
                        chrLocs.add("" + i);
                    }
                }
                           
                //insert feature labels
                ArrayList<ArrayList<String>> valList = new ArrayList<ArrayList<String>>();
                cols.add("name");
                cols.add("idx");
                cols.add(dataSetType + "setid");
                if(dataSetType.equals("marker"))
                {
                    cols.add("chr");
                    cols.add("locus");
                }
                for (int i = 0; i < featurenames.size(); i++)
                {
                    ArrayList<String> featureVal = new ArrayList<String>();
                    featureVal.add(featurenames.get(i));
                    featureVal.add("" + i);
                    featureVal.add(dataSetId);
                    if(dataSetType.equals("marker"))
                    {
                        featureVal.add(chrNums.get(i));
                        featureVal.add(chrLocs.get(i));
                    }
                    valList.add(featureVal);
                    if (valList.size() > 1000)
                    {
                        if (!DataManager.runMultipleInsertQuery(cols, valList, dataSetType))
                        {
                            JOptionPane.showMessageDialog(null, "There was an error inserting the " + dataSetType + " labels into the database.\n This is a bug. Please contact the developers. The error message was:\n" + DataManager.getLastError());
                            setIsError(true);
                            errorText = DataManager.getLastError();
                            form.repaint();
                            return;
                        }
                        valList.clear();
                    }
                }
                if (valList.size() > 0 && !DataManager.runMultipleInsertQuery(cols, valList, dataSetType))
                {
                    JOptionPane.showMessageDialog(null, "There was an error inserting the " + dataSetType + " labels into the database.\n This is a bug. Please contact the developers. The error message was:\n" + DataManager.getLastError());
                    setIsError(true);
                    errorText = DataManager.getLastError();
                    form.repaint();
                    return;
                }
                cols.clear();
                valList.clear();
                whereArgs.add(dataSetType + "setid=" + dataSetId);
                ArrayList<String> featureIds = DataManager.runSelectQuery("id", dataSetType, true, whereArgs, "idx");
                whereArgs.clear();
                if (featureIds.size() != numFeatures)
                {
                    JOptionPane.showMessageDialog(null, "There was an error retrieving the " + dataSetType + " ids from the database.\n This is a bug. Please contact the developers. The error message was:\n" + DataManager.getLastError());
                    setIsError(true);
                    errorText = DataManager.getLastError();
                    form.repaint();
                    return;
                }

                //finish off if there is no data to be imported
                if (noData)
                {
                    whereArgs.add("name = \"" + dataSetName + "\"");
                    status = "Finishing up!";
                    form.repaint();
                    if (!DataManager.runUpdateQuery(dataSetType + "set", "hasdata", "0", whereArgs))
                    {
                        JOptionPane.showMessageDialog(null, "There was an error finishing the import.\n This is a bug. Please contact the developers. The error message was:\n" + DataManager.getLastError());
                        setIsError(true);
                        errorText = DataManager.getLastError();
                        form.repaint();
                        return;
                    }
                    if (!DataManager.runUpdateQuery(dataSetType + "set", "loadcmpt", "1", whereArgs))
                    {
                        JOptionPane.showMessageDialog(null, "There was an error finishing the import.\n This is a bug. Please contact the developers. The error message was:\n" + DataManager.getLastError());
                        setIsError(true);
                        errorText = DataManager.getLastError();
                        form.repaint();
                        return;
                    }
                    setValue(100);
                    form.repaint();
                    Project p = Model.getInstance().getProject(Integer.parseInt(projectId));
                    if(dataSetType.equals("trait"))
                        p.getTraits().add(new TraitSet(Integer.parseInt(dataSetId), p, dataSetName, speciesId, "."));
                    else
                        p.getMarkers().add(new MarkerSet(Integer.parseInt(dataSetId), p, dataSetName));
                    DataAddRemoveHandler.getInstance().refreshDisplay();
                    return;
                }

                //load and insert feature values
                status = "Importing " + dataSetType + " values ....";
                form.repaint();
                
                ArrayList<String> lineIds = sampleIds;
                ArrayList<String> fieldIds = featureIds;
                if(isKxNFormat)
                {
                    lineIds = featureIds;
                    fieldIds = sampleIds;
                }
                int numLines = lineIds.size();
                int numFields = fieldIds.size();
                
                if(singleFile)
                    vparser.readline();
                
                cols.clear();
                if(isKxNFormat)
                {
                    cols.add(dataSetType + "id");
                    cols.add("sampleid");
                }
                else
                {
                    cols.add("sampleid");
                    cols.add(dataSetType + "id");
                }
                cols.add("value");
                int fieldOffset = 1;
                if(singleFile)
                    fieldOffset = 2;
                
                for(int i=0;i<numLines;i++)
                {
                    ArrayList<ArrayList<String>> insertionVals = new ArrayList();
                    try
                    {
                        line = vparser.readline();
                        for(int j=0;j<numFields;j++)
                        {
                            ArrayList<String> insertionVal = new ArrayList();
                            insertionVal.add(lineIds.get(i));
                            insertionVal.add(fieldIds.get(j));
                            insertionVal.add(line.get(j + fieldOffset));
                            insertionVals.add(insertionVal);
                        }  
                    }
                    catch(Exception e)
                    {
                        JOptionPane.showMessageDialog(null, "There was an error loading the " + dataSetType + " values.\n This is likely a bug. Please contact the developers");
                        setIsError(true);
                        errorText = "Error uploading " + dataSetType + " values";
                        form.repaint();
                        return;                        
                    }
                    if (!DataManager.runMultipleInsertQuery(cols, insertionVals, dataSetType + "val"))
                    {
                        JOptionPane.showMessageDialog(null, "There was an error inserting the " + dataSetType + " values into the database.\n This is a bug. Please contact the developers. The error message was:\n" + DataManager.getLastError());
                        setIsError(true);
                        errorText = DataManager.getLastError();
                        form.repaint();
                        return;
                    }
                                        
                    setValue(6 + (int) (((double) i / (double) numLines) * 93.0));
                    form.repaint();
                }
                cols.clear();
                whereArgs.clear();
                
                //finish up
                whereArgs.add("name = \"" + dataSetName + "\"");
                status = "Finishing up!";
                form.repaint();
                if (!DataManager.runUpdateQuery(dataSetType + "set", "loadcmpt", "1", whereArgs))
                {
                    JOptionPane.showMessageDialog(null, "There was an error finishing the import.\n This is a bug. Please contact the developers. The error message was:\n" + DataManager.getLastError());
                    setIsError(true);
                    errorText = DataManager.getLastError();
                    form.repaint();
                    return;
                }
                setValue(100);
                form.repaint();
                Project p = Model.getInstance().getProject(Integer.parseInt(projectId));
                if(dataSetType.equals("trait"))
                    p.getTraits().add(new TraitSet(Integer.parseInt(dataSetId), p, dataSetName, speciesId, "."));
                else
                    p.getMarkers().add(new MarkerSet(Integer.parseInt(dataSetId), p, dataSetName));
                DataAddRemoveHandler.getInstance().refreshDisplay();
            }
            catch (Exception ex)
            {
                if(!dataSetId.equals(""))
                {
                    String v = dataSetType + "val" + DataManager.getTeamCode();
                    String f = dataSetType;
                    String fid = dataSetType + "id";
                    String sid = dataSetType + "setid";
                    String cleanupQuery = "DELETE v FROM " + v + " v LEFT JOIN " + f + " f ON v." + fid + "=f.id" + " WHERE f." + sid + "=" + dataSetId + ";";
                    DataManager.runQuery(cleanupQuery);
                    ArrayList<String> whereArgs = new ArrayList();
                    whereArgs.add(dataSetType + "setid=" + dataSetId);
                    DataManager.deleteQuery(dataSetType, whereArgs);
                    whereArgs.clear();
                    whereArgs.add("id=" + dataSetId);
                    DataManager.deleteQuery(dataSetType + "set", whereArgs);     
                    whereArgs.clear();
                }                
                
                JOptionPane.showMessageDialog(null, "Internal error during importing.\n This is a bug. Please contact the developers.\n Error message was: " + ex.getMessage());
                setIsError(true);
                errorText = "Error importing file";
                form.repaint();
                return;
            }
        }
    }
}
