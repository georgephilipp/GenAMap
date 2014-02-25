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
 * * Description: Class that is the listener for the BiNGO-button on the settingspanel.
 * * It collects all kinds of information: the ontology and annotation
 * * file, the alpha, which distribution and correction will be used, ...
 * * It also redirects the vizualisation and the making of a file with
 * * information. It also redirects calculation of the p-values and
 * * corrected p-values.  
 **/
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * ********************************************************************
 * SettingsPanelActionListener.java
 * --------------------------------
 * <p/>
 * Steven Maere & Karel Heymans (c) March 2005
 * <p/>
 * Class that is the listener for the BiNGO-button on the settingspanel.
 * It collects all kinds of information: the ontology and annotation
 * file, the alpha, which distribution and correction will be used, ...
 * It also redirects the vizualisation and the making of a file with
 * information. It also redirects calculation of the p-values and
 * corrected p-values.
 * *********************************************************************
 */
public class SettingsPanelActionListener implements ActionListener
{

    /*--------------------------------------------------------------
    FIELDS.
    --------------------------------------------------------------*/
    private SettingsPanel settingsPanel;
    private BingoParameters params;
    private BiNGO.GOlorize.GoBin goBin;
    /**
     * constant string for the none-label in the combobox.
     */
    private final String NONE = BingoAlgorithm.NONE;
    private HashSet<String> ecCodes;
    private HashMap<String, HashSet<String>> redundantIDs = new HashMap<String, HashSet<String>>();
    private boolean consistencyCheck = true;
    /**
     * constant strings for the checking versus option.
     */
    private final String GRAPH = BingoAlgorithm.GRAPH;
    private final String GENOME = BingoAlgorithm.GENOME;
    private final String VIZSTRING = BingoAlgorithm.VIZSTRING;
    /**
     * constant string for the none-label in the combobox.
     */
    private final String CATEGORY_BEFORE_CORRECTION = BingoAlgorithm.CATEGORY_BEFORE_CORRECTION;
    private final String CATEGORY_CORRECTION = BingoAlgorithm.CATEGORY_CORRECTION;

    /*--------------------------------------------------------------
    CONSTRUCTOR.
    --------------------------------------------------------------*/
    /**
     * Constructor with all the settings of the settingspanel as arguments.
     */
    public SettingsPanelActionListener(BingoParameters params, SettingsPanel settingsPanel)
    {
        this.params = params;
        this.settingsPanel = settingsPanel;
        //       this.bdsAnnot=settingsPanel.getBDSPanel();
        this.goBin = null;
        ecCodes = new HashSet();
        ecCodes.add("IEA");
        ecCodes.add("ISS");
        ecCodes.add("TAS");
        ecCodes.add("IDA");
        ecCodes.add("IGI");
        ecCodes.add("IMP");
        ecCodes.add("IEP");
        ecCodes.add("ND");
        ecCodes.add("RCA");
        ecCodes.add("IPI");
        ecCodes.add("NAS");
        ecCodes.add("IC");
        ecCodes.add("NR");
    }

    /*--------------------------------------------------------------
    LISTENER-PART.
    --------------------------------------------------------------*/
    /**
     * action that is performed when the BiNGO-button is clicked.
     *
     * @param event BiNGO-button clicked.
     */
    //public void actionPerformed(ActionEvent event)
    public BiNGOresults2GenAMap calcGoEnrichmentFromGenAMap(boolean isUpdateParms)
    {
        boolean status = true;
        if (isUpdateParms)
        {
            status = updateParameters();
            HashSet noClassificationsSet = new HashSet();
            redundantIDs = new HashMap<String, HashSet<String>>();
        }

        // passed all tests.
        if (status == true)
        {
            int[] testData = getClassificationsFromVector(params.getSelectedNodes(), new HashSet());
            boolean noElementsInTestData = false;
            // testing whether there are elements in sample data array.
            try
            {
                int firstElement = testData[0];
            }
            catch (Exception ex)
            {
                noElementsInTestData = true;
            }
            if (!noElementsInTestData)
            {
                return performCalculations(params.getSelectedNodes(), params.getAllNodes(), new HashSet());
            }
            else
            {
                if (isUpdateParms)
                {
                    JOptionPane.showMessageDialog(settingsPanel,
                            "The selected annotation does not produce any" + "\n" + "classifications for the selected nodes." + "\n" + "Maybe you chose the wrong type of gene identifier ?");
                }
            }
            //               }
        }
        return null;
    }


    /*--------------------------------------------------------------
    METHODS.
    --------------------------------------------------------------*/
    private String openResourceFile(String name)
    {
        File fi = new File(name);
        if (fi.exists())
        {
            return name;
        }
        return null;
    }

    public HashSet<String> conformize(HashSet<String> selection, HashSet<String> allNodes)
    {
        HashSet<String> conformizedSelection = new HashSet<String>();
        for (String s : selection)
        {
            boolean ok = false;
            for (String s2 : allNodes)
            {
                if (params.getAlias().get(s) != null && params.getAlias().get(s2) != null)
                {
                    if (params.getAlias().get(s).equals(params.getAlias().get(s2)))
                    {
                        conformizedSelection.add(s2);
                        ok = true;
                        /*if(!s.equals(s2)){
                        System.out.println(s + "\t" + s2);
                        }*/
                        break;
                    }
                }
            }
            if (ok == false)
            {
                conformizedSelection.add(s);
            }
        }
        return conformizedSelection;
    }

    public boolean updateParameters()
    {
        boolean status = true;
        params.setOverOrUnder(settingsPanel.getOverUnderPanel().getCheckedButton());
        params.setTextOrGraph(false);
        // testing whether, if nodes are selected from text area, there is something valid in the text area
        if (!params.isTextOrGraph())
        {
            //set the text from the window to the variable
            params.setTextInput(settingsPanel.getTextOrGraphPanel().getInputText());
            if (params.getTextInput() == null)
            {
                JOptionPane.showMessageDialog(settingsPanel, "Please paste one or more genes in the text field.");
                return false;
            }
        }

        //significance cut-off
        params.setCorrection((String) settingsPanel.getCorrectionBox().getSelectedItem());
        params.setTest((String) settingsPanel.getTestBox().getSelectedItem());
        params.setSignificance(new BigDecimal(settingsPanel.getAlphaField().getText()));
        // distribution selected?
        if (params.getTest().equals(NONE))
        {
            settingsPanel.getAlphaField().setText("1.00");
            params.setSignificance(new BigDecimal("1.00"));
            if (!params.getCorrection().equals(NONE))
            {
                JOptionPane.showMessageDialog(settingsPanel, "Multiple testing correction not possible without test selection...");
                return false;
            }
        }
        else
        {
            // checking of alpha is a decimal between 0 and 1.
            boolean alphaIncorrect = false;
            try
            {
                if (params.getSignificance().compareTo(new BigDecimal("1")) >= 0 || params.getSignificance().compareTo(new BigDecimal("0")) <= 0)
                {
                    alphaIncorrect = true;
                }

            }
            catch (Exception ex)
            {
                alphaIncorrect = true;
            }
            if (alphaIncorrect)
            {
                JOptionPane.showMessageDialog(settingsPanel, "Please input a valid significance level (i.e. a decimal number between 0 and 1).");
                return false;
            }
        }

        // category-option selected?
        params.setCategory((String) settingsPanel.getCategoriesBox().getSelectedItem());
        if (params.getCategory().equals(NONE))
        {
            JOptionPane.showMessageDialog(settingsPanel, "Please select what categories should be visualized.");
            return false;
        }
        // checking number of categories option
        if (params.getCategory().equals(CATEGORY_CORRECTION) && params.getCorrection().equals(NONE))
        {
            JOptionPane.showMessageDialog(settingsPanel,
                    "The option 'Overrepresented categories after correction'" + "\n" + "at the category box requires the selection of a" + "\n" + "correction in the correction box.");
            return false;
        }
        if (params.getCategory().equals(CATEGORY_BEFORE_CORRECTION) && params.getTest().equals(NONE))
        {
            JOptionPane.showMessageDialog(settingsPanel,
                    "The option 'Overrepresented categories before correction'" + "\n" + "at the category box requires at least the selection of a" + "\n" + "test in the test box.");
            return false;
        }

        //Get the specified species and the find out what is its corresponding file name
        String specified_species = settingsPanel.getAnnotationPanel().getSpecifiedSpecies();
        if (specified_species.equals(BingoAlgorithm.CUSTOM))
        {
            if (params.getAnnotationFile() == null || !params.getAnnotationFile().equals(settingsPanel.getAnnotationPanel().getSelection()))
            {
                params.setStatus(false);
            }
            params.setAnnotationFile(settingsPanel.getAnnotationPanel().getSelection());
            params.setAnnotation_default(settingsPanel.getAnnotationPanel().getDefault());
            params.setSpecies(settingsPanel.getAnnotationPanel().getSelection());
        }
        else
        {
            //get the file name for this species.
            String annot_filename = params.getSpeciesFilename(specified_species);
            if (params.getAnnotationFile() == null || !params.getAnnotationFile().equals(openResourceFile(annot_filename)))
            {
                params.setStatus(false);
            }
            params.setAnnotationFile(openResourceFile(annot_filename));
            params.setAnnotation_default(settingsPanel.getAnnotationPanel().getDefault());
            params.setSpecies(settingsPanel.getAnnotationPanel().getSelection());
        }
        if (settingsPanel.getOntologyPanel().getSpecifiedOntology().equals(BingoAlgorithm.CUSTOM))
        {
            if (params.getOntologyFile() == null || !params.getOntologyFile().equals(settingsPanel.getOntologyPanel().getSelection()))
            {
                params.setStatus(false);
            }
            params.setOntologyFile(settingsPanel.getOntologyPanel().getSelection());
            params.setOntology_default(settingsPanel.getOntologyPanel().getDefault());
            params.setNameSpace(BingoAlgorithm.NONE);
        }
        else
        {
            if (params.getOntologyFile() == null || !params.getOntologyFile().equals(openResourceFile(settingsPanel.getOntologyPanel().getSelection())))
            {
                params.setStatus(false);
            }
            params.setOntologyFile(openResourceFile(settingsPanel.getOntologyPanel().getSelection()));
            params.setOntology_default(settingsPanel.getOntologyPanel().getDefault());
            params.setNameSpace(BingoAlgorithm.NONE);
        }

        HashSet deleteCodes = new HashSet();
        String tmp = "";//settingsPanel.getEcField().getText().trim();
        String[] codes = tmp.split("\\s+");
        for (int i = 0; i < codes.length; i++)
        {
            if (codes[i].length() != 0)
            {
                if (ecCodes.contains(codes[i].toUpperCase()))
                {
                    deleteCodes.add(codes[i].toUpperCase());
                }
                else
                {
                    JOptionPane.showMessageDialog(settingsPanel, "Evidence code " + codes[i].toUpperCase() + " does not exist");
                    return false;
                }
            }
        }
        if (params.getDeleteCodes() == null || !params.getDeleteCodes().equals(deleteCodes))
        {
            params.setStatus(false);
        }
        params.setDeleteCodes(deleteCodes);

        //probably superfluous
        // annotation file selected?
        if (params.getAnnotationFile() == null)
        {
            JOptionPane.showMessageDialog(settingsPanel, "Please select an annotation file.");
            return false;
        }
        //probably superfluous
        // ontology file selected?
        else if (params.getOntologyFile() == null)
        {
            JOptionPane.showMessageDialog(settingsPanel, "Please select an ontology file.");
            return false;
        }

        //in case something annotation/ontology-related went wrong in a previous call, BingoParameters.status is false and AnnotationParser should be called again
        if (params.getStatus() == false)
        {
            AnnotationParser annParser = params.initializeAnnotationParser();
            annParser.run();
            //boolean success = TaskManager.executeTask(annParser, config);
            if (annParser.getStatus())
            {
                params.setAnnotation(annParser.getAnnotation());
                params.setOntology(annParser.getOntology());
                params.setAlias(annParser.getAlias());
                if (annParser.getOrphans())
                {
                    JOptionPane.showMessageDialog(settingsPanel,
                            "WARNING : Some category labels in the annotation file" + "\n" + "are not defined in the ontology. Please check the compatibility of" + "\n" + "these files. For now, these labels will be ignored and calculations" + "\n" + "will proceed.");
                }
                //only way to set status true is to pass annotation parse step
                params.setStatus(true);
            }
            else
            {
                params.setStatus(false);
                return false;
            }
        }

        //return if all parameters were set.
        return status;
    }

    /**
     * method that gets the canonical names from text input.
     *
     * @return HashSet containing the canonical names.
     */
    public HashSet getSelectedCanonicalNamesFromTextArea()
    {
        String textNodes = params.getTextInput();
        String[] nodes = textNodes.split("\\s+");
        // HashSet for storing the canonical names
        HashSet canonicalNameVector = new HashSet();
        HashSet<HashSet<String>> mapNames = new HashSet<HashSet<String>>();
        // iterate over every node view to get the canonical names.
        for (int i = 0; i < nodes.length; i++)
        {
            if (nodes[i] != null && nodes[i].length() != 0 && !canonicalNameVector.contains(nodes[i].toUpperCase()))
            {
                if (mapNames.contains(params.getAlias().get(nodes[i].toUpperCase())))
                {
                    redundantIDs.put(nodes[i].toUpperCase(), (HashSet<String>) params.getAlias().get(nodes[i].toUpperCase()));
                    /*int opt = JOptionPane.showOptionDialog(settingsPanel,
                    "WARNING : The test set contains multiple identifiers for the gene/protein " + "\n" +
                    nodes[i].toUpperCase() + ". If you press 'Yes', the redundant identifier will be ignored " + "\n" +
                    "and calculations will proceed. Press 'No' to abort calculations.","WARNING",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE,null,null,null);
                    if(opt == JOptionPane.NO_OPTION){
                    consistencyCheck = false;
                    }*/
                }
                //else{
                if (params.getAlias().get(nodes[i].toUpperCase()) != null)
                {
                    mapNames.add((HashSet<String>) params.getAlias().get(nodes[i].toUpperCase()));
                }
                canonicalNameVector.add(nodes[i].toUpperCase());
                //}
            }
        }
        return canonicalNameVector;
    }

    /**
     * method that gets the canonical names from text input in batch mode.
     *
     * @return vector containing the canonical names of batch instance.
     */
    public HashSet getBatchClusterFromTextArea(String textNodes)
    {

        String[] nodes = textNodes.split("\\s+");
        // HashSet for storing the canonical names
        HashSet canonicalNameVector = new HashSet();
        HashSet<HashSet<String>> mapNames = new HashSet<HashSet<String>>();
        // iterate over every node view to get the canonical names.
        // first term is cluster name...
        int j = 0;
        while (nodes[j].equals(""))
        {
            j++;
        }
        params.setCluster_name(nodes[j]);
        for (int i = j + 1; i < nodes.length; i++)
        {
            if (nodes[i] != null && nodes[i].length() != 0 && !canonicalNameVector.contains(nodes[i].toUpperCase()))
            {
                if (mapNames.contains(params.getAlias().get(nodes[i].toUpperCase())))
                {
                    redundantIDs.put(nodes[i].toUpperCase(), (HashSet<String>) params.getAlias().get(nodes[i].toUpperCase()));
                    /*int opt = JOptionPane.showOptionDialog(settingsPanel,
                    "WARNING : The test set contains multiple identifiers for the gene/protein " + "\n" +
                    nodes[i].toUpperCase() + ". If you press 'Yes', the redundant identifier will be ignored " + "\n" +
                    "and calculations will proceed. Press 'No' to abort calculations.","WARNING",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE,null,null,null);
                    if(opt == JOptionPane.NO_OPTION){
                    consistencyCheck = false;
                    }*/
                }
                else
                {
                    if (params.getAlias().get(nodes[i].toUpperCase()) != null)
                    {
                        mapNames.add((HashSet<String>) params.getAlias().get(nodes[i].toUpperCase()));
                    }
                    canonicalNameVector.add(nodes[i].toUpperCase());
                }
            }
        }

        return canonicalNameVector;
    }

    /**
     * method that gets the canonical names from the whole graph.
     *
     * @return HashSet containing the canonical names from the network.
     */
//    public HashSet getAllCanonicalNamesFromNetwork(CyNetwork network)
//    {
//        // HashSet for storing the canonical names
//        HashSet canonicalNameVector = new HashSet();
//        HashSet<HashSet<String>> mapNames = new HashSet<HashSet<String>>();
//        // iterate over every node view to get the canonical names.
//        for (Iterator i = network.nodesIterator(); i.hasNext();)
//        {
//            // getting next NodeView
//            //NodeView nView = (NodeView)i.next();
//            // first get the corresponding node
//            Node node = (Node) i.next();
//            // gets the canonical name of the given node from the attributes object
//            String canonicalName = node.getIdentifier().toUpperCase();
//            if (canonicalName != null && (canonicalName.length() != 0) && !canonicalNameVector.contains(canonicalName))
//            {
//                if (mapNames.contains(params.getAlias().get(node.getIdentifier().toUpperCase())))
//                {
//                    redundantIDs.put(node.getIdentifier().toUpperCase(), (HashSet<String>) params.getAlias().get(node.getIdentifier().toUpperCase()));
//                    int opt = JOptionPane.showOptionDialog(settingsPanel,
//                            "WARNING : The network contains multiple identifiers for the gene/protein " + "\n"
//                            + node.getIdentifier().toUpperCase() + ". If you press 'Yes', the redundant identifier will be ignored " + "\n"
//                            + "and calculations will proceed. Press 'No' to abort calculations.", "WARNING", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
//                    if (opt == JOptionPane.NO_OPTION)
//                    {
//                        consistencyCheck = false;
//                    }
//                }
//                else
//                {
//                    if (params.getAlias().get(node.getIdentifier().toUpperCase()) != null)
//                    {
//                        mapNames.add((HashSet<String>) params.getAlias().get(node.getIdentifier().toUpperCase()));
//                    }
//                    canonicalNameVector.add(canonicalName);
//                }
//            }
//        }
//
//        return canonicalNameVector;
//    }
    /**
     * method that gets the canonical names for the whole annotation.
     *
     * @return HashSet containing the canonical names.
     */
    public HashSet getAllCanonicalNamesFromAnnotation(HashSet selectedNodes)
    {
        String[] nodes = params.getAnnotation().getNames();
        // HashSet for storing the canonical names
        HashSet canonicalNameVector = new HashSet();
        for (int i = 0; i < nodes.length; i++)
        {
            if (nodes[i] != null && (nodes[i].length() != 0))
            {
                canonicalNameVector.add(nodes[i].toUpperCase());
            }
        }

        //replace canonical names in reference set that match one of the canonical names in the selected cluster, to get rid of e.g. splice variants if the non-splice-specific gene is part of the selection, and to avoid conflicts between names in ref set and selection
        HashMap<String, HashSet<String>> alias = params.getAlias();
        Iterator it2 = selectedNodes.iterator();
        while (it2.hasNext())
        {
            String name = it2.next() + "";
            HashSet tmp = alias.get(name);
            if (tmp != null)
            {
                Iterator it = tmp.iterator();
                while (it.hasNext())
                {
                    canonicalNameVector.remove(it.next() + "");
                }
                //add selected node name
                canonicalNameVector.add(name);
            }
        }
        return canonicalNameVector;
    }

    /**
     * method that gets the canonical names for the whole annotation.
     *
     * @return HashSet containing the canonical names.
     */
    public HashSet getAllCanonicalNamesFromReferenceSet(String refSet, HashSet selectedNodes)
    {
        HashSet<String> nodes = parseReferenceSet(refSet);
        // HashSet for storing the canonical names
        HashSet canonicalNameVector = new HashSet();
        for (String s : nodes)
        {
            if (s.length() != 0)
            {
                canonicalNameVector.add(s.toUpperCase());
            }
        }

        //replace canonical names in reference set that match one of the canonical names in the selected cluster, to get rid of e.g. splice variants if the non-splice-specific gene is part of the selection, and to avoid conflicts between names in ref set and selection
        HashMap<String, HashSet<String>> alias = params.getAlias();
        Iterator it2 = selectedNodes.iterator();
        while (it2.hasNext())
        {
            String name = it2.next() + "";
            HashSet tmp = alias.get(name);
            if (tmp != null)
            {
                Iterator it = tmp.iterator();
                while (it.hasNext())
                {
                    canonicalNameVector.remove(it.next() + "");
                }
                //add selected node name
                canonicalNameVector.add(name);
            }
        }
        return canonicalNameVector;
    }

    public HashSet<String> parseReferenceSet(String refSetFile)
    {
        HashSet<String> refSet = new HashSet<String>();
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(new File(refSetFile)));
            String line;
            while ((line = reader.readLine()) != null)
            {
                String[] tokens = line.trim().split("\t");
                refSet.add(tokens[0].trim().toUpperCase());
            }
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(settingsPanel, "Error reading reference file: " + e);
        }
        return refSet;
    }

    /**
     * Method that gets the classifications from a HashSet of canonical names.
     *
     * @param canonicalNameVector HashSet of canonical names.
     * @return int[] classifications.
     */
    public int[] getClassificationsFromVector(HashSet canonicalNameVector, HashSet noClassificationsSet)
    {
        // HashSet for the classifications.
        HashSet classificationsVector = new HashSet();
        HashMap<String, HashSet<String>> alias = params.getAlias();
        // array for go labels.
        int[] goLabelsName;
        Iterator it2 = canonicalNameVector.iterator();
        while (it2.hasNext())
        {
            String name = it2.next() + "";
            HashSet identifiers = alias.get(name.toUpperCase());
            HashSet cls = new HashSet();
            // array for go labels.
            if (identifiers != null)
            {
                Iterator it = identifiers.iterator();
                while (it.hasNext())
                {
                    goLabelsName = params.getAnnotation().getClassifications(it.next() + "");
                    for (int t = 0; t < goLabelsName.length; t++)
                    {
                        cls.add(goLabelsName[t] + "");
                    }
                }
            }
            if (cls.size() == 0)
            {
                noClassificationsSet.add(name);
            }
            Iterator it3 = cls.iterator();
            while (it3.hasNext())
            {
                classificationsVector.add(it3.next() + "");
            }
        }
        int[] classifications = new int[classificationsVector.size()];
        it2 = classificationsVector.iterator();
        int i = 0;
        while (it2.hasNext())
        {
            classifications[i] = Integer.parseInt(it2.next() + "");
            i++;
        }
        return classifications;
    }

    /**
     * Method that redirects the calculations of the distribution and the correction.
     * Redirects the visualization of the network and
     * redirects the making of a file with the interesting data.
     */
    public BiNGOresults2GenAMap performCalculations(HashSet selectedNodes, HashSet allNodes, HashSet noClassificationsSet)
    {

        HashMap testMap = null;
        HashMap correctionMap = null;
        HashMap mapSmallX = null;
        HashMap mapSmallN = null;
        HashMap mapBigX = null;
        HashMap mapBigN = null;

        BingoAlgorithm algorithm = new BingoAlgorithm(params);

        CalculateTestTask test = algorithm.calculate_distribution();

        boolean success = test.calculate();
//        boolean success = TaskManager.executeTask(test, config);

        testMap = test.getTestMap();
        CalculateCorrectionTask correction = algorithm.calculate_corrections(testMap);
        if ((correction != null) && (!params.getTest().equals(NONE)))
        {
//            success = TaskManager.executeTask(correction, config);
            success = correction.calculate();
            correctionMap = correction.getCorrectionMap();
        }
        mapSmallX = test.getMapSmallX();
        mapSmallN = test.getMapSmallN();
        mapBigX = test.getMapBigX();
        mapBigN = test.getMapBigN();

        DisplayBiNGOWindow display;
        BiNGOresults2GenAMap file;

//        if (params.getVisualization().equals(VIZSTRING))
//        {
//            display = new DisplayBiNGOWindow(testMap,
//                    correctionMap,
//                    mapSmallX,
//                    mapSmallN,
//                    mapBigX,
//                    mapBigN,
//                    params.getSignificance().toString(),
//                    params.getOntology(),
//                    params.getCluster_name(),
//                    params.getCategory() + "");
//
//            // displaying the BiNGO CyNetwork.
//            display.makeWindow();
//        }
//        if ((goBin == null) || goBin.isWindowClosed())
//        {
//            goBin = new BiNGO.GOlorize.GoBin(settingsPanel, startNetworkView);
//        }

        if (params.getAnnotationFile() == null)
        {
            params.setAnnotationFile("Cytoscape loaded annotation: " + params.getAnnotation().toString());
        }

        if (params.getOntologyFile() == null)
        {
            params.setOntologyFile("Cytoscape loaded ontology: " + params.getOntology().toString());
        }

        file = new BiNGOresults2GenAMap(testMap,
                correctionMap,
                mapSmallX,
                mapSmallN,
                mapBigX,
                mapBigN,
                params.getSignificance().toString(),
                params.getAnnotation(),
                params.getAlias(),
                params.getOntology(),
                params.getOntologyFile().toString(),
                params.getTest() + "",
                params.getCorrection() + "",
                params.getOverOrUnder() + "",
                selectedNodes,
                this.params.getAllNodes());

        return file;
    }

    public void actionPerformed(ActionEvent e)
    {
        if (settingsPanel.isRun)
        {
            settingsPanel.setResults(this.calcGoEnrichmentFromGenAMap(true));
        }
        else
        {
            this.updateParameters();
        }
    }
}
