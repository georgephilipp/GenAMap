package BiNGO;

/* * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * Authors : Steven Maere, Karel Heymans
 * *
 * * This program is free software; you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation; either version 2 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * * The software and documentation provided hereunder is on an "as is" basis,
 * * and the Flanders Interuniversitary Institute for Biotechnology
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Flanders Interuniversitary Institute for Biotechnology
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * the Flanders Interuniversitary Institute for Biotechnology
 * * has been advised of the possibility of such damage. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program; if not, write to the Free Software
 * * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * *
 * * Authors: Steven Maere, Karel Heymans
 * * Date: Mar.25.2005
 * * Description: Class which creates a file with information about the selected
 * * cluster: ontology type and curator, time of creation, alpha,
 * * sort of test and correction, p-values and corrected 
 * * p-values, term id and name, x, X, n, N.     
 **/
import cytoscape.data.annotation.Annotation;
import cytoscape.data.annotation.Ontology;
import java.math.BigDecimal;
import java.util.*;

/**
 * *****************************************************************
 * CreateBiNGOFile.java     Steven Maere & Karel Heymans (c) March 2005
 * --------------------
 * <p/>
 * Class which creates a file with information about the selected
 * cluster: ontology type and curator, time of creation, alpha,
 * sort of test and correction, p-values and corrected
 * p-values, term id and name, x, X, n, N.
 * ******************************************************************
 */
public class BiNGOresults2GenAMap
{

    /*--------------------------------------------------------------
    FIELDS.
    --------------------------------------------------------------*/
    /**
     * hashmap with key termID and value pvalue.
     */
    //private HashMap testMap;
    /**
     * hashmap with key termID and value corrected pvalue.
     */
    //private HashMap correctionMap;
    /**
     * hashmap with key termID and value x.
     */
    //private HashMap mapSmallX;
    /**
     * hashmap with key termID and value n.
     */
    //private HashMap mapSmallN;
    /**
     * integer with X.
     */
    //private HashMap mapBigX;
    /**
     * integer with N.
     */
    //private HashMap mapBigN;
    /**
     * String with alpha value.
     */
    private String alphaString;
    /**
     * String with used test.
     */
    private String testString;
    /**
     * String with used correction.
     */
    private String correctionString;
    /**
     * String for over- or underrepresentation.
     */
    private String overUnderString;
    /**
     * the annotation (remapped, i.e. including all parent annotations)
     */
    //private Annotation annotation;
    //private HashSet deleteCodes;
    /**
     * the ontology.
     */
    //private Ontology ontology;
    /**
     * the annotation file path.
     */
    //private String annotationFile;
    /**
     * the ontology file path.
     */
    private String ontologyFile;
    /**
     * the dir for saving the data file.
     */
    //private String dirName;
    /**
     * the file name for the data file.
     */
    //private String fileName;
    /**
     * the clusterVsString.
     */
    //private String clusterVsString;
    /**
     * the categoriesString.
     */
    //private String catString;
    /**
     * HashSet with the names of the selected nodes.
     */
    private HashSet selectedCanonicalNameVector;
    /**
     * hashmap with keys the GO categories and values HashSets of test set genes annotated to that category
     */
    //private HashSet noClassificationsSet;
    //private HashMap annotatedGenes;
    //private HashMap<String, HashSet<String>> alias;
    private final String NONE = BingoAlgorithm.NONE;
    private ArrayList<GoItems> enrichedGoCats = new ArrayList<GoItems>();
    private HashMap<String, ArrayList<String>> annotations = new HashMap<String, ArrayList<String>>();

    public String getStringRepresentationOfGoResults()
    {
        String res = ",";
        for(GoItems gi : enrichedGoCats)
        {
            res += gi.getStringRepresentation() + ",";
        }
        return res;
    }

    public ArrayList<GoItems> getEnrichedGoItems()
    {
        return this.enrichedGoCats;
    }

    public String getStringRepresentationOfMethod()
    {
        String ret = "";
        if(this.testString.toLowerCase().contains("--"))
        {
            ret += "NON,";
        }
        else if(this.testString.toLowerCase().contains(("hyper")))
        {
            ret += "HG,";
        }
        else
        {
            ret += "BIN,";
        }

        if(this.correctionString.toLowerCase().contains("fdr"))
        {
            ret += "FDR,";
        }
        else if(this.correctionString.toLowerCase().contains(("fwer")))
        {
            ret += "FWER,";
        }
        else
        {
            ret += "NON,";
        }
        ret += this.ontologyFile +",";
        ret += this.alphaString;
        return ret;
    }

    /*--------------------------------------------------------------
    CONSTRUCTORS.
    --------------------------------------------------------------*/
    /**
     * Constructor for an overrepresentation calculation with a correction.
     *
     * @param testMap                     HashMap with key: termID and value: pvalue.
     * @param correctionMap               HashMap with key: termID and value:  corrected pvalue.
     * @param mapSmallX                   HashMap with key: termID and value: #.
     * @param mapSmallN                   HashMap with key: termID and value: n.
     * @param bigX                        int with value of X.
     * @param bigN                        int with value of N.
     * @param alphaString                 String with value for significance level.
     * @param ontology                    the Ontology.
     * @param testString                  String with the name of the test.
     * @param correctionString            String with the name of the correction.
     * @param fileName                    String with the name for the data-file.
     * @param clusterVsString             String with option against what cluster must be tested.
     * @param selectedCanonicalNameVector HashSet with the selected genes.
     */
    public BiNGOresults2GenAMap(HashMap testMap,
            HashMap correctionMap,
            HashMap mapSmallX,
            HashMap mapSmallN,
            HashMap mapBigX,
            HashMap mapBigN,
            String alphaString,
            Annotation annotation,
            HashMap alias,
            Ontology ontology,
            String ontologyFile,
            String testString,
            String correctionString,
            String overUnderString,
            HashSet selectedCanonicalNameVector,
            HashSet referenceSet)
    {

        this.alphaString = alphaString;
        this.ontologyFile = ontologyFile;
        this.testString = testString;
        this.correctionString = correctionString;
        this.overUnderString = overUnderString;
        this.selectedCanonicalNameVector = selectedCanonicalNameVector;

        setupResults(testMap, correctionMap, mapSmallX,
                mapSmallN, mapBigX, mapBigN, annotation,
                ontology, referenceSet,
                alias);
    }

    /*--------------------------------------------------------------
    METHODS.
    --------------------------------------------------------------*/
    /**
     * Method that creates the file with information about the cluster.
     * <p/>
     * without correction:
     * -------------------
     * termID <tab> pvalue <tab> x <tab> n <tab> X <tab> N <tab> description <tab> test set genes in GO category <\n>
     * <p/>
     * with correction:
     * ----------------
     * termID <tab> pvalue <tab> corrected pvalues <tab> x <tab> n <tab> X <tab> N <tab> description <tab> test set genes in GO category <\n>
     */
    public void setupResults(HashMap testMap, HashMap correctionMap, HashMap mapSmallX,
            HashMap mapSmallN, HashMap mapBigX, HashMap mapBigN, Annotation annotation,
            Ontology ontology, HashSet referenceSet,
            HashMap<String, HashSet<String>> alias)
    {
        //output.write("Selected ontology file : " + ontologyFile + "\n"); - may need to remove dir info
        int j = 1;
        Iterator it = referenceSet.iterator();

        while (it.hasNext())
        {
            String name = it.next() + "";
            HashSet tmp = alias.get(name);
            if (tmp != null)
            {
                Iterator it2 = tmp.iterator();
                while (it2.hasNext())
                {
                    int[] nodeClassifications = annotation.getClassifications(it2.next() + "");
                    ArrayList<String> classes = new ArrayList<String>();
                    for (int k = 0; k < nodeClassifications.length; k++)
                    {
                        classes.add(ontology.getTerm(nodeClassifications[k]).getName());
                    }
                    this.annotations.put(name, classes);
                }
            }
        }

        //orden GO labels by increasing corrected p-value or increasing smallX
        HashSet keySet;
        if (!testString.equals(NONE))
        {
            keySet = new HashSet(testMap.keySet());
        }
        else
        {
            keySet = new HashSet(mapSmallX.keySet());
        }
        it = keySet.iterator();
        String[] keyLabels = new String[keySet.size()];
        for (int i = 0; it.hasNext(); i++)
        {
            keyLabels[i] = it.next().toString();
        }
        String[] ordenedKeySet;
        if (!testString.equals(NONE))
        {
            ordenedKeySet = ordenKeysByPvalues(keyLabels, testMap);
        }
        else
        {
            ordenedKeySet = ordenKeysBySmallX(keyLabels, mapSmallX);
        }
        boolean ok = true;

        for (int i = 0; (i < ordenedKeySet.length) && (ok == true); i++)
        {
            GoItems go = new GoItems();
            go.GO_ID = ordenedKeySet[i];
            // pvalue
            if (!testString.equals(NONE))
            {
                try
                {
                    go.pval = Double.parseDouble(
                            SignificantFigures.sci_format(testMap.get(new Integer(go.GO_ID)).toString(), 5));
                }
                catch (Exception e)
                {
                    //pvalue = "N/A";
                }
            }
            else
            {
                //pvalue = "N/A";
            }
            // corrected pvalue
            if (!correctionString.equals(NONE))
            {
                try
                {
                    go.correctedpval = Double.parseDouble(
                            SignificantFigures.sci_format(correctionMap.get(go.GO_ID).toString(), 5));
                }
                catch (Exception e)
                {
                    //correctedPvalue = "N/A";
                }
            }
            else
            {
                //correctedPvalue = "N/A";
            }
            // x
            try
            {
                go.x = Integer.parseInt(mapSmallX.get(new Integer(go.GO_ID)).toString());
            }
            catch (Exception e)
            {
                //smallX = "N/A";
            }
            // n
            try
            {
                go.n = Integer.parseInt(
                        mapSmallN.get(new Integer(go.GO_ID)).toString());
            }
            catch (Exception e)
            {
                //smallN = "N/A";
            }
            // X
            try
            {
                go.X = Integer.parseInt(
                        mapBigX.get(new Integer(go.GO_ID)).toString());
            }
            catch (Exception e)
            {
                //bigX = "N/A";
            }
            // N
            try
            {
                go.N = Integer.parseInt(
                        mapBigN.get(new Integer(go.GO_ID)).toString());
            }
            catch (Exception e)
            {
                //bigN = "N/A";
            }
            // name
            try
            {
                go.descr = ontology.getTerm(Integer.parseInt(go.GO_ID)).getName();
            }
            catch (Exception e)
            {
                go.descr = "?";
            }

            if(go.correctedpval < Double.parseDouble(this.alphaString))
            {
                this.enrichedGoCats.add(go);
            }
        }
    }
//original
//    public void makeFile()
//    {
//
//        // date and time for filename uniqueness.
//        String dateString = DateFormat.getDateInstance().format(new Date());
//        String timeString = DateFormat.getTimeInstance().format(new Date());
//
//        // actual writing of the file.
//
//
//        try
//        {
//            File results = new File("gotesting.txt");
//            BufferedWriter output = new BufferedWriter(new FileWriter(results));
//            System.out.println("BiNGO results file : " + results.getPath());
//            //try{
//            output.write("File created with BiNGO (c) on " + dateString + " at " + timeString + "\n");
//            output.write("\n");
//            output.write(ontology.toString());
//            output.write("\n");
//            output.write("Selected ontology file : " + ontologyFile + "\n");
//            output.write("Selected annotation file : " + annotationFile + "\n");
//            output.write("Discarded evidence codes : ");
//            Iterator it = deleteCodes.iterator();
//            while (it.hasNext())
//            {
//                output.write(it.next().toString() + "\t");
//            }
//            output.write("\n" + overUnderString + "\n");
//            output.write("Selected statistical test : " + testString + "\n");
//            output.write("Selected correction : " + correctionString + "\n");
//            output.write("Selected significance level : " + alphaString + "\n");
//            output.write("Testing option : " + clusterVsString + "\n");
//            output.write("The selected cluster :\n");
//            int j = 1;
//            it = selectedCanonicalNameVector.iterator();
//            while (it.hasNext())
//            {
//                String name = it.next() + "";
//                HashSet tmp = alias.get(name);
//                if (tmp != null)
//                {
//                    Iterator it2 = tmp.iterator();
//                    while (it2.hasNext())
//                    {
//                        int[] nodeClassifications = annotation.getClassifications(it2.next() + "");
//                        for (int k = 0; k < nodeClassifications.length; k++)
//                        {
//                            String cat = new Integer(nodeClassifications[k]).toString();
//                            if (!annotatedGenes.containsKey(cat))
//                            {
//                                HashSet catset = new HashSet();
//                                annotatedGenes.put(cat, catset);
//                            }
//                            ((HashSet) annotatedGenes.get(cat)).add(name);
//                        }
//                    }
//                }
//                output.write(name + "\t");
//                if (j == 255)
//                {
//                    output.write("\n");
//                    j = 0;
//                }
//                j++;
//            }
//            output.write("\n\n");
//            output.write("No annotations were retrieved for the following entities:\n");
//            Iterator it2 = noClassificationsSet.iterator();
//            j = 1;
//            while (it2.hasNext())
//            {
//                output.write(it2.next().toString() + "\t");
//                if (j == 255)
//                {
//                    output.write("\n");
//                    j = 0;
//                }
//                j++;
//            }
//            output.write("\n\n");
//
//            if (testString.equals(NONE))
//            {
//                output.write("GO-ID" + "\t" + "x" + "\t" + "n" + "\t" + "X" + "\t" + "N" + "\t" + "Description" + "\t" + "Genes in test set" + "\n");
//            }
//            else if (correctionString.equals(NONE))
//            {
//                output.write("GO-ID" + "\t" + "p-value" + "\t" + "x" + "\t" + "n" + "\t" + "X" + "\t" + "N" + "\t" + "Description" + "\t" + "Genes in test set" + "\n");
//            }
//            else
//            {
//                output.write("GO-ID" + "\t" + "p-value" + "\t" + "corr p-value" + "\t" + "x" + "\t" + "n" + "\t" + "X" + "\t" + "N" + "\t" + "Description" + "\t" + "Genes in test set" + "\n");
//            }
//            /*}
//            catch (Exception e){
//            System.out.println("Error: " + e);
//            }*/
//
//            //orden GO labels by increasing corrected p-value or increasing smallX
//
//            HashSet keySet;
//            if (!testString.equals(NONE))
//            {
//                keySet = new HashSet(testMap.keySet());
//            }
//            else
//            {
//                keySet = new HashSet(mapSmallX.keySet());
//            }
//            it = keySet.iterator();
//            String[] keyLabels = new String[keySet.size()];
//            for (int i = 0; it.hasNext(); i++)
//            {
//                keyLabels[i] = it.next().toString();
//            }
//            String[] ordenedKeySet;
//            if (!testString.equals(NONE))
//            {
//                ordenedKeySet = ordenKeysByPvalues(keyLabels);
//            }
//            else
//            {
//                ordenedKeySet = ordenKeysBySmallX(keyLabels);
//            }
//            boolean ok = true;
//
//            for (int i = 0; (i < ordenedKeySet.length) && (ok == true); i++)
//            {
//
//                String termID = ordenedKeySet[i];
//                String pvalue = "";
//                String correctedPvalue = "";
//                String smallX;
//                String smallN;
//                String bigX;
//                String bigN;
//                String description = null;
//                // pvalue
//                if (!testString.equals(NONE))
//                {
//                    try
//                    {
//                        pvalue = SignificantFigures.sci_format(testMap.get(new Integer(termID)).toString(), 5);
//                    }
//                    catch (Exception e)
//                    {
//                        pvalue = "N/A";
//                    }
//                }
//                else
//                {
//                    pvalue = "N/A";
//                }
//                // corrected pvalue
//                if (!correctionString.equals(NONE))
//                {
//                    try
//                    {
//                        correctedPvalue = SignificantFigures.sci_format(correctionMap.get(termID).toString(), 5);
//                    }
//                    catch (Exception e)
//                    {
//                        correctedPvalue = "N/A";
//                    }
//                }
//                else
//                {
//                    correctedPvalue = "N/A";
//                }
//                // x
//                try
//                {
//                    smallX = mapSmallX.get(new Integer(termID)).toString();
//                }
//                catch (Exception e)
//                {
//                    smallX = "N/A";
//                }
//                // n
//                try
//                {
//                    smallN = mapSmallN.get(new Integer(termID)).toString();
//                }
//                catch (Exception e)
//                {
//                    smallN = "N/A";
//                }
//                // X
//                try
//                {
//                    bigX = mapBigX.get(new Integer(termID)).toString();
//                }
//                catch (Exception e)
//                {
//                    bigX = "N/A";
//                }
//                // N
//                try
//                {
//                    bigN = mapBigN.get(new Integer(termID)).toString();
//                }
//                catch (Exception e)
//                {
//                    bigN = "N/A";
//                }
//                // name
//                try
//                {
//                    description = ontology.getTerm(Integer.parseInt(termID)).getName();
//                }
//                catch (Exception e)
//                {
//                    description = "?";
//                }
//
//                if (testString.equals(NONE))
//                {
//                    output.write(termID + "\t" + smallX + "\t" + smallN + "\t" + bigX + "\t" + bigN + "\t" + description + "\t");
//                    if (annotatedGenes.containsKey(termID))
//                    {
//                        Iterator k = ((HashSet) annotatedGenes.get(termID)).iterator();
//                        while (k.hasNext())
//                        {
//                            output.write(k.next().toString());
//                            if (k.hasNext())
//                            {
//                                output.write('|');
//                            }
//                        }
//                    }
//                    output.write("\n");
//                }
//                else if (correctionString.equals(NONE))
//                {
//                    if (catString.equals(CATEGORY_BEFORE_CORRECTION))
//                    {
//                        if ((new BigDecimal(testMap.get(new Integer(ordenedKeySet[i])).toString())).compareTo(new BigDecimal(alphaString)) < 0)
//                        {
//                            output.write(termID + "\t" + pvalue + "\t" + smallX + "\t" + smallN + "\t" + bigX + "\t" + bigN + "\t" + description + "\t");
//                            if (annotatedGenes.containsKey(termID))
//                            {
//                                Iterator k = ((HashSet) annotatedGenes.get(termID)).iterator();
//                                while (k.hasNext())
//                                {
//                                    output.write(k.next().toString());
//                                    if (k.hasNext())
//                                    {
//                                        output.write('|');
//                                    }
//                                }
//                            }
//                            output.write("\n");
//                        }
//                        else
//                        {
//                            ok = false;
//                        }
//                    }
//                    else
//                    {
//                        output.write(termID + "\t" + pvalue + "\t" + smallX + "\t" + smallN + bigX + "\t" + bigN + "\t" + "\t" + description + "\t");
//                        if (annotatedGenes.containsKey(termID))
//                        {
//                            Iterator k = ((HashSet) annotatedGenes.get(termID)).iterator();
//                            while (k.hasNext())
//                            {
//                                output.write(k.next().toString());
//                                if (k.hasNext())
//                                {
//                                    output.write('|');
//                                }
//                            }
//                        }
//                        output.write("\n");
//                    }
//                }
//                else
//                {
//                    if (catString.equals(CATEGORY_CORRECTION))
//                    {
//                        if ((new BigDecimal(correctionMap.get(ordenedKeySet[i]).toString())).compareTo(new BigDecimal(alphaString)) < 0)
//                        {
//                            output.write(termID + "\t" + pvalue + "\t" + correctedPvalue + "\t" + smallX + "\t" + smallN + "\t" + bigX + "\t" + bigN + "\t" + description + "\t");
//                            if (annotatedGenes.containsKey(termID))
//                            {
//                                Iterator k = ((HashSet) annotatedGenes.get(termID)).iterator();
//                                while (k.hasNext())
//                                {
//                                    output.write(k.next().toString());
//                                    if (k.hasNext())
//                                    {
//                                        output.write('|');
//                                    }
//                                }
//                            }
//                            output.write("\n");
//                        }
//                        else
//                        {
//                            ok = false;
//                        }
//                    }
//                    else if (catString.equals(CATEGORY_BEFORE_CORRECTION))
//                    {
//                        if ((new BigDecimal(testMap.get(new Integer(ordenedKeySet[i])).toString())).compareTo(new BigDecimal(alphaString)) < 0)
//                        {
//                            output.write(termID + "\t" + pvalue + "\t" + correctedPvalue + "\t" + smallX + "\t" + smallN + "\t" + bigX + "\t" + bigN + "\t" + description + "\t");
//                            if (annotatedGenes.containsKey(termID))
//                            {
//                                Iterator k = ((HashSet) annotatedGenes.get(termID)).iterator();
//                                while (k.hasNext())
//                                {
//                                    output.write(k.next().toString());
//                                    if (k.hasNext())
//                                    {
//                                        output.write('|');
//                                    }
//                                }
//                            }
//                            output.write("\n");
//                        }
//                        else
//                        {
//                            ok = false;
//                        }
//                    }
//                    else
//                    {
//                        output.write(termID + "\t" + pvalue + "\t" + correctedPvalue + "\t" + smallX + "\t" + smallN + bigX + "\t" + bigN + "\t" + "\t" + description + "\t");
//                        if (annotatedGenes.containsKey(termID))
//                        {
//                            Iterator k = ((HashSet) annotatedGenes.get(termID)).iterator();
//                            while (k.hasNext())
//                            {
//                                output.write(k.next().toString());
//                                if (k.hasNext())
//                                {
//                                    output.write('|');
//                                }
//                            }
//                        }
//                        output.write("\n");
//                    }
//                }
//            }
//
//            output.close();
//        }
//        catch (Exception e)
//        {
//            System.out.println("Error: " + e);
//
//        }
//
//    }

    public String[] ordenKeysByPvalues(String[] labels, HashMap testMap)
    {

        for (int i = 1; i < labels.length; i++)
        {
            int j = i;
            // get the first unsorted value ...
            String insert_label = labels[i];
            BigDecimal val = new BigDecimal(testMap.get(new Integer(labels[i])).toString());
            // ... and insert it among the sorted
            while ((j > 0) && (val.compareTo(new BigDecimal(testMap.get(new Integer(labels[j - 1])).toString())) < 0))
            {
                labels[j] = labels[j - 1];
                j--;
            }
            // reinsert value
            labels[j] = insert_label;
        }
        return labels;
    }

    public String[] ordenKeysBySmallX(String[] labels, HashMap mapSmallX)
    {

        for (int i = 1; i < labels.length; i++)
        {
            int j = i;
            // get the first unsorted value ...
            String insert_label = labels[i];
            BigDecimal val = new BigDecimal(mapSmallX.get(new Integer(labels[i])).toString());
            // ... and insert it among the sorted
            while ((j > 0) && (val.compareTo(new BigDecimal(mapSmallX.get(new Integer(labels[j - 1])).toString())) > 0))
            {
                labels[j] = labels[j - 1];
                j--;
            }
            // reinsert value
            labels[j] = insert_label;
        }
        return labels;
    }

    /**
     * Returns the annotation code for this run ...
     * @return
     */
    public String getAnnoCode()
    {
        if(this.ontologyFile.toLowerCase().contains("gener"))
        {
            return "GEN";
        }
        else if(this.ontologyFile.toLowerCase().contains("goa"))
        {
            return "GOA";
        }
        else if(this.ontologyFile.toLowerCase().contains("plants"))
        {
            return "PLA";
        }
        else if(this.ontologyFile.toLowerCase().contains("yeast"))
        {
            return "YST";
        }
        else if(this.ontologyFile.toLowerCase().contains("cellular"))
        {
            return "GCC";
        }
        else if(this.ontologyFile.toLowerCase().contains("full"))
        {
            return "FUL";
        }
        else if(this.ontologyFile.toLowerCase().contains("function"))
        {
            return "GMF";
        }
        else if(this.ontologyFile.toLowerCase().contains("biolog"))
        {
            return "GBP";
        }
        else
        {
            return "";
        }
    }

    /**
     * Return all of the annotations associated with this trait!
     * @param name
     * @return
     */
    public ArrayList<String> getAnnotations(String name)
    {
        return this.annotations.get(name.toUpperCase());
    }
}


