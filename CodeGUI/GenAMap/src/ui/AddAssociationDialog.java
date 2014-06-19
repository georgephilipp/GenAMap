/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import algorithm.AlgorithmView;
import algorithm.Algorithms;
import algorithm.AssociationParameterObject;
import control.DataAddRemoveHandler;
import controller.Constants;
import datamodel.MarkerSet;
import datamodel.Model;
import datamodel.Network;
import datamodel.Population;
import datamodel.Project;
import datamodel.TraitSet;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;
import realdata.DataManager;

/**
 *
 * @author Georg Schoenherr <Georg Schoenherr>
 */
public class AddAssociationDialog extends JDialog {
    private JPanel mainPanel;
    private JPanel createAssociationPanel;
    private JPanel loadAssociationPanel;
    
    JLabel projectLabel;
    JLabel traitSetLabel;
    JLabel markerSetLabel;
    JLabel nameLabel;
    JLabel algorithmLabel;
    JLabel networkLabel;
    JLabel numberLabel;
    JLabel informationLabel;
    
    JComboBox projectComboBox;
    JComboBox traitSetComboBox;
    JComboBox markerSetComboBox;
    JComboBox algorithmComboBox;
    JComboBox networkComboBox;
    JComboBox numberComboBox;
    
    JTextField nameTextField;
    JTextField associationFilePathTextField;
    
    JRadioButton createAssociationRadioButton;
    JRadioButton loadAssociationRadioButton;
    ButtonGroup radioButtonGroup;
    
    JButton runButton;
    JButton cancelButton;
    JButton browseButton;
    
    Component parentComponentReference;
    Component currentComponentReference;
    
    /**
     * The algorithm view instance
     */
    private AlgorithmView view;
    /**
     * Whether or not the user can browse for a file right now
     */
    private boolean canSelect = false;
    
    public AddAssociationDialog(Component parentComponent, boolean modal, AlgorithmView view, boolean createSelected,
            String project, String traits, String markers){
        
        super();
        parentComponentReference = parentComponent;
        currentComponentReference = this;
        this.view = view;
        
        labelFactory();
        textFieldFactory();
        comboBoxFactory();
        radioButtonFactory();
        buttonFactory();
        panelFactory();
        dialogSettings();
        componentSettings();
        
        for (int i = 0; i < Algorithms.AssociationAlgorithms.algonames().size(); i++)
        {
            this.algorithmComboBox.addItem(Algorithms.AssociationAlgorithms.getalgorithms().get(i));
        }
        
        ArrayList<Project> temp = Model.getInstance().getProjects();
        for (int i = 0; i < temp.size(); i++)
        {
            this.projectComboBox.addItem(temp.get(i).getName());
        }

        if (project != null)
        {
            this.projectComboBox.setSelectedItem(project);
        }
        if (traits != null)
        {
            this.traitSetComboBox.setSelectedItem(traits);
        }
        if (markers != null)
        {
            this.markerSetComboBox.setSelectedItem(markers);
        }

        if (project != null && traits == null)
        {
            if (this.traitSetComboBox.getItemCount() > 1)
            {
                this.traitSetComboBox.setSelectedIndex(1);
                if (this.markerSetComboBox.getItemCount() > 1)
                {
                    this.markerSetComboBox.setSelectedIndex(1);
                }
            }
        }
    }
    
    private void componentSettings(){
        createAssociationPanel.add(algorithmLabel, new AbsoluteConstraints(10, 10, 80, 20));
        createAssociationPanel.add(algorithmComboBox, new AbsoluteConstraints(100, 10, 200, 20));
        createAssociationPanel.add(networkLabel, new AbsoluteConstraints(10, 40, 80, 20));
        createAssociationPanel.add(networkComboBox, new AbsoluteConstraints(100, 40, 200, 20));
        createAssociationPanel.add(numberLabel, new AbsoluteConstraints(10, 70, 80, 20));
        createAssociationPanel.add(numberComboBox, new AbsoluteConstraints(100, 70, 150, 20));
        
        loadAssociationPanel.add(associationFilePathTextField, new AbsoluteConstraints(10, 10, 250, 20));
        loadAssociationPanel.add(browseButton, new AbsoluteConstraints(270, 10, 30, 20));
        loadAssociationPanel.add(informationLabel, new AbsoluteConstraints(10, 40, 300, 20));
        
        mainPanel.add(projectLabel, new AbsoluteConstraints(10, 10, 80, 20));
        mainPanel.add(projectComboBox, new AbsoluteConstraints(100, 10, 200, 20));
        mainPanel.add(traitSetLabel, new AbsoluteConstraints(10, 40, 80, 20));
        mainPanel.add(traitSetComboBox, new AbsoluteConstraints(100, 40, 200, 20));
        mainPanel.add(markerSetLabel, new AbsoluteConstraints(10, 70, 80, 20));
        mainPanel.add(markerSetComboBox, new AbsoluteConstraints(100, 70, 200, 20));
        mainPanel.add(nameLabel, new AbsoluteConstraints(10, 100, 80, 20));
        mainPanel.add(nameTextField, new AbsoluteConstraints(100, 100, 200, 20));
        mainPanel.add(createAssociationRadioButton, new AbsoluteConstraints(10, 140, 310, 30));
        mainPanel.add(loadAssociationRadioButton, new AbsoluteConstraints(330, 140, 310, 30));
        mainPanel.add(createAssociationPanel, new AbsoluteConstraints(10, 180, 310, 100));
        mainPanel.add(loadAssociationPanel, new AbsoluteConstraints(330, 180, 310, 100));
        mainPanel.add(runButton, new AbsoluteConstraints(470, 290, 80, 20));
        mainPanel.add(cancelButton, new AbsoluteConstraints(560, 290, 80, 20));
        
        this.add(mainPanel, new AbsoluteConstraints(0, 0, Constants.associationDialogWidth, Constants.associationDialogHeight));
    }
    
    private void dialogSettings(){
        this.pack();
        this.setTitle("Add Association");
        this.setSize(Constants.associationDialogWidth,Constants.associationDialogHeight);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLayout(new AbsoluteLayout());
        this.setResizable(false); 
        this.setLocationRelativeTo(parentComponentReference);
        //this.setModal(true);
        this.setIconImage(new ImageIcon(Constants.applicationIcon).getImage());
        this.validate();
        this.setVisible(true);  
    }
    
    private void panelFactory(){
        mainPanel = new JPanel();
        mainPanel.setEnabled(true);
        mainPanel.setVisible(true);
        mainPanel.setLayout(new AbsoluteLayout());
        mainPanel.setBorder(BorderFactory.createEtchedBorder());
        
        createAssociationPanel = new JPanel();
        createAssociationPanel.setEnabled(true);
        createAssociationPanel.setVisible(true);
        createAssociationPanel.setLayout(new AbsoluteLayout());
        createAssociationPanel.setBorder(BorderFactory.createEtchedBorder());
        
        loadAssociationPanel = new JPanel();
        loadAssociationPanel.setEnabled(true);
        loadAssociationPanel.setVisible(true);
        loadAssociationPanel.setLayout(new AbsoluteLayout());
        loadAssociationPanel.setBorder(BorderFactory.createEtchedBorder());
    }
    
    private void comboBoxFactory(){
        projectComboBox = new JComboBox();
        projectComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(projectComboBox.getSelectedIndex() < 0){
                    traitSetComboBox.setEnabled(false);
                    markerSetComboBox.setEnabled(false);
                }
                else{
                    traitSetComboBox.setEnabled(true);
                    markerSetComboBox.setEnabled(true);
                }
                
                projectComboBoxActionPerformed(e);
            }
        });
        
        traitSetComboBox = new JComboBox();
        traitSetComboBox.setEnabled(false);
        traitSetComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                traitComboBoxActionPerformed(e);
            }
        });
        
        markerSetComboBox = new JComboBox();
        markerSetComboBox.setEnabled(false);
        markerSetComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                markerComboBoxActionPerformed(e);
            }
        });
        
        algorithmComboBox = new JComboBox();
        algorithmComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                algorithmComboBoxActionPerformed(e);                
            }
        });
        
        networkComboBox = new JComboBox();
        networkComboBox.setEnabled(false);
        networkComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                networkComboBoxActionPerformed(e);
            }
        });
        
        numberComboBox = new JComboBox();
        numberComboBox.setEnabled(false);
    }
    
    private void radioButtonFactory(){
        createAssociationRadioButton= new JRadioButton(Constants.createAssociationText);
        createAssociationRadioButton.setSelected(true);
        createAssociationRadioButton.setFont(new Font("sansserif",1,16));
        createAssociationRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(createAssociationRadioButton.isSelected()){
                    algorithmComboBox.setEnabled(true);
                    
                    associationFilePathTextField.setEnabled(false);
                    browseButton.setEnabled(false);
                    canSelect = false;
                }                
            }
        });
        
        loadAssociationRadioButton = new JRadioButton(Constants.loadAssociationText);
        loadAssociationRadioButton.setSelected(false);
        loadAssociationRadioButton.setFont(new Font("sansserif",1,16));
        loadAssociationRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(loadAssociationRadioButton.isSelected()){
                    associationFilePathTextField.setEnabled(true);
                    browseButton.setEnabled(true);
                    canSelect = true;
                    
                    algorithmComboBox.setEnabled(false);
                    networkComboBox.setEnabled(false);
                    numberComboBox.setEnabled(false);
                }
            }
        });
        
        radioButtonGroup = new ButtonGroup();
        radioButtonGroup.add(createAssociationRadioButton);
        radioButtonGroup.add(loadAssociationRadioButton);
    }
    
    private void labelFactory(){
        projectLabel = new JLabel(Constants.projectLabelText);
        projectLabel.setFont(Constants.defaultFont);
        
        traitSetLabel = new JLabel(Constants.traitSetLabelText);
        traitSetLabel.setFont(Constants.defaultFont);
        
        markerSetLabel = new JLabel(Constants.markerSetLabelText);
        markerSetLabel.setFont(Constants.defaultFont);
        
        nameLabel = new JLabel(Constants.associationNameLabelText);
        nameLabel.setFont(Constants.defaultFont);
        
        algorithmLabel = new JLabel(Constants.algorithmLabelText);
        algorithmLabel.setFont(Constants.defaultFont);
        
        networkLabel = new JLabel(Constants.networkLabelText);
        networkLabel.setFont(Constants.defaultFont);
        
        numberLabel = new JLabel(Constants.numberLabelText);
        numberLabel.setFont(Constants.defaultFont);
        
        informationLabel = new JLabel(Constants.loadAssociationInfoLabelText);
        informationLabel.setFont(new Font("sansserif",1,12));
    }
    
    private void textFieldFactory(){
        nameTextField = new JTextField();
        nameTextField.setFont(Constants.defaultFont);
        
        associationFilePathTextField = new JTextField();
        associationFilePathTextField.setEditable(false);
        associationFilePathTextField.setFont(Constants.defaultFont);
        associationFilePathTextField.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e){
                associationFilePathTextFieldMouseClicked(e);
            }
        });
    }
    
    private void buttonFactory(){
        runButton = new JButton(Constants.runAssociationButtonText);
        runButton.setBorder(BorderFactory.createRaisedBevelBorder());
        runButton.setEnabled(true);
        runButton.addActionListener(new ActionListener() {
        @Override
            public void actionPerformed(ActionEvent e) {
                runButtonActionPerformed(e);
            }
        });
        
        cancelButton = new JButton(Constants.cancelButtonText);
        cancelButton.setBorder(BorderFactory.createRaisedBevelBorder());
        cancelButton.setEnabled(true);
        cancelButton.addActionListener(new ActionListener() {
        @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        browseButton = new JButton(Constants.browseAssociationButtonText);
        browseButton.setBorder(BorderFactory.createRaisedBevelBorder());
        browseButton.setEnabled(false);
        browseButton.addActionListener(new ActionListener() {
        @Override
            public void actionPerformed(ActionEvent e) {
               browseButtonActionPerformed(e);
            }
        });
    }
    
    private void associationFilePathTextFieldMouseClicked(java.awt.event.MouseEvent evt) {                                            
        browseButtonActionPerformed(null);
    }

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {                                           
        if (!canSelect)
        {
            return;
        }

        JFileChooser c = new JFileChooser(Model.getInstance().GetLastFilePath());
        int rVal = c.showOpenDialog(this);
        if (rVal == JFileChooser.APPROVE_OPTION)
        {
            this.associationFilePathTextField.setText(c.getSelectedFile().getAbsolutePath());
            Model.getInstance().AccountForLastFilePath(c.getSelectedFile().getAbsolutePath());
        }
    }
    
    /**
     * Checks conditions and starts the algorithm. 
     * @param evt
     */
    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {                                             
        if (this.projectComboBox.getSelectedItem().equals(""))
        {
            UIMessages.showErrorMessage("Please select a project", "Error");
            return;
        }

        if (this.traitSetComboBox.getSelectedIndex() < 0)
        {
            UIMessages.showErrorMessage("Please select a trait option", "Error");
            return;
        }

        String projectName = projectComboBox.getSelectedItem().toString();
        String traitName = traitSetComboBox.getSelectedItem().toString();
        String markerName = markerSetComboBox.getSelectedItem().toString();
        Project ap = Model.getInstance().getProject(projectName);
        TraitSet ts = ap.getTrait(traitName);
        MarkerSet ms = ap.getMarker(markerName);
        if (this.algorithmComboBox.getSelectedIndex() < 0 && !this.loadAssociationRadioButton.isSelected())
        {
            UIMessages.showErrorMessage("Please select an algorithm", "Error");
            return;
        }

        if(this.algorithmComboBox.getSelectedItem().equals("Adaptive Multi-Task Lasso"))
        {
            ArrayList<String> where = new ArrayList<String>();
            where.add("markersetid = " + ms.getId());
            if(Integer.parseInt(DataManager.runSelectQuery("count(*)", "feature", true, where, null).get(0)) < 1)
            {
                UIMessages.showErrorMessage("Selected marker set does not have features", "Error");
                return;
            }
        }

        if (this.markerSetComboBox.getSelectedIndex() < 0)
        {
            UIMessages.showErrorMessage("Please select a marker", "Error");
            return;
        }

        if (this.networkComboBox.getSelectedIndex() < 0 && this.networkComboBox.isEnabled())
        {
            UIMessages.showErrorMessage("Please select a valid network option for selected algorithm", "Error");
            return;
        }

        if (this.networkComboBox.getSelectedIndex() < 0 && this.networkComboBox.isEnabled())
        {
            UIMessages.showErrorMessage("Please select a valid Population option for selected algorithm", "Error");
            return;
        }

        if (this.numberComboBox.isEnabled() && this.numberComboBox.getSelectedIndex() < 0)
        {
            UIMessages.showErrorMessage("Please select a valid Population option for this algorithm", "Error");
            return;
        }
        if (this.nameTextField.getText().equals(""))
        {
            UIMessages.showErrorMessage("Please specify a valid name", "Error");
            return;
        }

        if (Model.getInstance().getProject(projectComboBox.getSelectedItem().toString()).isAssocNamePresentInProject(this.nameTextField.getText()))
        {
            UIMessages.showErrorMessage("Specified name already exists for this project", "Error");
            return;
        }

        
        String networkName = this.networkComboBox.getSelectedItem().toString();
        String algoName = this.algorithmComboBox.getSelectedItem().toString();

        String popno = this.numberComboBox.getSelectedItem().toString();
        if (!this.numberComboBox.isEnabled())
        {
            popno = "1";
        }
        algoName = (algoName.equals("")) ? null : algoName;
        Network net = (Network) ts.getTraitStructure(networkName);

        if (ts.getNetworks().size() == 0) //without a network, we will be unable
        //to view this visualization, so we add it here.
        {
            try
            {
                view.addAlgorithm(Algorithms.NetworkAlgorithms.algonames().get(0),
                        Algorithms.NetworkAlgorithms.jobTypeID().get(0),
                        ap.getId(), ts.getId(), ms.getId());
            }
            catch (Exception e)
            {
            }
        }

        if (this.createAssociationRadioButton.isSelected())
        {
            int structtype = algorithmComboBox.getSelectedIndex() < 0 ? -1 : Algorithms.AssociationAlgorithms.inputStructure().get(algorithmComboBox.getSelectedIndex());
            IOFilesForIOLasso ioffio = null;
            if(structtype == 3)
            {
                ioffio = new IOFilesForIOLasso(currentComponentReference);

                ioffio.setVisible(true);

                if(ioffio.getInputFilePath() == null || ioffio.getInputFilePath().length() < 5 ||
                        ioffio.getOutputFilePath() == null || ioffio.getOutputFilePath().length() < 5)
                {
                    UIMessages.showErrorMessage("I/O Lasso needs valid group files", "Error");
                    return;
                }
            }


            AssociationParameterObject apo = new AssociationParameterObject(
                (Network) ts.getTraitStructure(networkName),
                    this.nameTextField.getText(), ms.getPopulation(networkName), popno, ioffio);
                    int idx = this.algorithmComboBox.getSelectedIndex() - 1;
            
                view.addAlgorithm(Algorithms.AssociationAlgorithms.algonames().get(idx),
                    Algorithms.AssociationAlgorithms.jobTypeID().get(idx),
                    ap.getId(), ts.getId(), ms.getId(), apo);
        }
        else
        {
            DataAddRemoveHandler.getInstance().addAssociation(ap.getId(), ts, ms, this.nameTextField.getText(), this.associationFilePathTextField.getText(), null);
        }
        this.dispose();
    }       
    
    private void projectComboBoxActionPerformed(java.awt.event.ActionEvent evt) {                                                
        Project project = Model.getInstance().getProject(this.projectComboBox.getSelectedItem().toString());

        this.traitSetComboBox.removeAllItems();
        this.markerSetComboBox.removeAllItems();
        for (int i = 0; i < project.getTraits().size(); i++)
        {
            this.traitSetComboBox.addItem(project.getTraits().get(i).getName());
        }
        for (int i = 0; i < project.getMarkers().size(); i++)
        {
            this.markerSetComboBox.addItem(project.getMarkers().get(i).getName());
        }
    }
    
    private void traitComboBoxActionPerformed(java.awt.event.ActionEvent evt) {                                              
        if (this.traitSetComboBox.getSelectedObjects().length == 0 || this.traitSetComboBox.getSelectedIndex() < 0)
        {
            return;
        }

        String s = this.markerSetComboBox.getSelectedItem().toString();
        markerSetComboBox.removeAllItems();
        Project project = Model.getInstance().getProject(this.projectComboBox.getSelectedItem().toString());

        TraitSet ts = project.getTrait(this.traitSetComboBox.getSelectedItem().toString());
        MarkerSet ms = null;
        if(!s.equals(""));
        {
            ms = project.getMarker(s);
        }

        if((ts.getHasData() && ms == null) || (ts.getHasData() && ms != null && ms.hasData()))
        {
            algorithmComboBox.setEnabled(true);
            this.createAssociationRadioButton.setEnabled(true);
            this.createAssociationRadioButton.setSelected(true);            
        }
        else
        {
            this.algorithmComboBox.setEnabled(false);
            this.createAssociationRadioButton.setEnabled(false);
            this.loadAssociationRadioButton.setSelected(true);            
        }

        for (MarkerSet m : project.getMarkers())
        {
            if (isSameSampleSet(m, project.getTrait(this.traitSetComboBox.getSelectedItem().toString())))
            {
                markerSetComboBox.addItem(m.getName());
            }
        }

        for (int i = 0; i < markerSetComboBox.getItemCount(); i++)
        {
            if (markerSetComboBox.getItemAt(i).equals(s))
            {
                markerSetComboBox.setSelectedIndex(i);
            }
        }


        int structtype = algorithmComboBox.getSelectedIndex() < 0 ? -1 : Algorithms.AssociationAlgorithms.inputStructure().get(algorithmComboBox.getSelectedIndex());
        if (algorithmComboBox.getSelectedIndex() < 0 || !(structtype > 0) || structtype == 3 || !this.algorithmComboBox.isEnabled())
        {
            this.networkComboBox.setEnabled(false);
            return;
        }
        else
        {
            this.networkComboBox.setEnabled(true);
        }
        this.networkComboBox.removeAllItems();
        if (structtype == 1)
        {
            TraitSet t = project.getTrait(this.traitSetComboBox.getSelectedItem().toString());
            for (int i = 0; i < t.getNetworkIdentifiers().size(); i++)
            {
                this.networkComboBox.addItem(t.getNetworkIdentifiers().get(i));
            }
        }
        else if (structtype == 2)
        {
            MarkerSet m = project.getMarker(this.markerSetComboBox.getSelectedItem().toString());
            if (m == null)
            {
                return;
            }
            for (int i = 0; i < m.getPopulations().size(); i++)
            {
                this.networkComboBox.addItem(m.getPopulations().get(i));
            }
        }
    }                                            

    private boolean isSameSampleSet(MarkerSet m, TraitSet trait)
    {
        if (m.getNumSamples() != trait.getNumSamples())
        {
            return false;
        }
        return true;
    }
    
    private void algorithmComboBoxActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        int structtype = algorithmComboBox.getSelectedIndex() < 0 ? -1 : Algorithms.AssociationAlgorithms.inputStructure().get(algorithmComboBox.getSelectedIndex());
        if (structtype == 2)
        {
            networkLabel.setText("Population");
            numberLabel.setVisible(true);
            numberComboBox.setVisible(true);
        }
        else if(structtype == 1)
        {
            networkLabel.setText("Network");
            numberLabel.setVisible(false);
            numberComboBox.setVisible(false);
        }
        this.traitComboBoxActionPerformed(evt);
    }                                                 

    private void markerComboBoxActionPerformed(java.awt.event.ActionEvent evt)                                               
    {                                                   

        if (this.markerSetComboBox.getSelectedObjects().length == 0 || this.markerSetComboBox.getSelectedItem().equals("") || !this.markerSetComboBox.isEnabled())
        {
            return;
        }
        this.traitComboBoxActionPerformed(evt);        
    }
    
    private void networkComboBoxActionPerformed(java.awt.event.ActionEvent evt) {                                                

        if (this.networkComboBox.getSelectedObjects().length == 0 || !this.networkComboBox.isEnabled())
        {
            return;
        }

        if (numberComboBox.isVisible())
        {
            if (networkComboBox.getSelectedItem().toString().compareToIgnoreCase("<Select Population>") == 0 || networkComboBox.getSelectedItem().toString().compareToIgnoreCase("<Select Network>") == 0)
            {
                return;
            }
            else
            {
                Project project = Model.getInstance().getProject(this.projectComboBox.getSelectedItem().toString());
                MarkerSet m = project.getMarker(markerSetComboBox.getSelectedItem().toString());
                Population p = m.getPopulation(networkComboBox.getSelectedItem().toString());
                if (p.isStructureGenerated())
                {
                    numberComboBox.setEnabled(true);
                    numberComboBox.removeAllItems();
                    for (int n = 2; n < 11; n++)
                    {
                        numberComboBox.addItem(n);
                    }
                }
                else
                {
                    numberComboBox.removeAllItems();
                    numberComboBox.addItem(p.getTotPopIfUserGen());

                    numberComboBox.setEnabled(false);
                }
            }
        }
    }
}
