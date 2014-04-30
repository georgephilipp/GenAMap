package control.algodialog;

import algorithm.AlgorithmView;
import algorithm.StructureParameterObject;
import control.DataAddRemoveHandler;
import control.ExampleFileHandler;
import datamodel.MarkerSet;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import java.util.ArrayList;
import datamodel.Project;
import datamodel.Model;
import java.io.File;
import javax.swing.JOptionPane;

/**
 * A user can either start and algorithm to generate a population structure
 * object, or they can load in their own file of population values for
 * a user create population structure. Even in this case, an eigen value
 * generation process is started. 
 * @author ross
 */
public class PopulationStructDialog extends java.awt.Dialog
{
    /**
     * the AlgorithmView instance
     */
    private AlgorithmView view;
    /**
     * Whether or not the user is currently allowed to browse for files
     */
    private boolean canSelect = false;

    /**
     * Creates a population struct dialog to create or load a pop struct
     * @param parent the form that owns this population structure
     * @param modal
     * @param view the algorithm view instance
     * @param createSelected whether or not to auto select create
     * @param traits the name of the markerset
     * @param project the project
     */
    public PopulationStructDialog(java.awt.Frame parent, boolean modal, AlgorithmView view, boolean createSelected,
            String markers, String project)
    {
        super(parent, modal);
        this.view = view;
        initComponents();
        this.setLocation(parent.getLocation());

        this.popComboBox.setEnabled(false);

        this.algorithmComboBox.addItem("Structure");//Algorithms.PopulationAlgorithm.getalgorithms().get(i));

        ArrayList<Project> temp = Model.getInstance().getProjects();
        for (int i = 0; i < temp.size(); i++)
        {
            this.projectComboBox.addItem(temp.get(i).getName());
        }

        if (createSelected)
        {
            this.createRadBtn.setSelected(true);
            this.fileButton.setEnabled(false);
            this.exampleBtn.setEnabled(false);
            this.networkFileBox.setEnabled(false);
        }
        else
        {
            this.loadRadBtn.setSelected(true);
            this.fileButton.setEnabled(true);
            this.exampleBtn.setEnabled(true);
            this.networkFileBox.setEnabled(true);
            this.algorithmComboBox.setEnabled(false);
        }
        if (project != null)
        {
            for (int i = 0; i < this.projectComboBox.getItemCount(); i++)
            {
                String s = projectComboBox.getItemAt(i).toString();
                if (s.equals(project))
                {
                    this.projectComboBox.setSelectedIndex(i);
                    i = this.projectComboBox.getItemCount();
                }
            }

            if (markers != null)
            {
                for (int i = 0; i < this.popComboBox.getItemCount(); i++)
                {
                    if (this.popComboBox.getItemAt(i).toString().equals(markers))
                    {
                        this.popComboBox.setSelectedIndex(i);
                        i = this.popComboBox.getItemCount();
                    }
                }
            }
        }

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        projectComboBox = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        popComboBox = new javax.swing.JComboBox();
        networkFileBox = new javax.swing.JTextField();
        fileButton = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        algorithmComboBox = new javax.swing.JComboBox();
        createRadBtn = new javax.swing.JRadioButton();
        loadRadBtn = new javax.swing.JRadioButton();
        importButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        importButton1 = new javax.swing.JButton();
        errorLabel = new javax.swing.JLabel();
        netNameText = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        exampleBtn = new javax.swing.JButton();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanel1.setBorder(new javax.swing.border.LineBorder(java.awt.Color.blue, 2, true));

        projectComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<Select Project>" }));
        projectComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectComboBoxActionPerformed(evt);
            }
        });

        jLabel4.setText("Project");

        jLabel5.setText("Marker Set");

        popComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<Select Marker>" }));
        popComboBox.setEnabled(false);
        popComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popComboBoxActionPerformed(evt);
            }
        });

        networkFileBox.setEditable(false);
        networkFileBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                networkFileBoxMouseClicked(evt);
            }
        });

        fileButton.setText("...");
        fileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileButtonActionPerformed(evt);
            }
        });

        jLabel6.setText("Algorithm");

        algorithmComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<Select Algorithm>" }));
        algorithmComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                algorithmComboBoxActionPerformed(evt);
            }
        });

        buttonGroup1.add(createRadBtn);
        createRadBtn.setFont(new java.awt.Font("DejaVu Sans", 1, 18));
        createRadBtn.setText("Create a Population Structure");
        createRadBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createRadBtnActionPerformed(evt);
            }
        });

        buttonGroup1.add(loadRadBtn);
        loadRadBtn.setFont(new java.awt.Font("DejaVu Sans", 1, 18));
        loadRadBtn.setText("Load from File");
        loadRadBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadRadBtnActionPerformed(evt);
            }
        });

        importButton.setText("Run");
        importButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        importButton1.setText("Set Parameters");
        importButton1.setEnabled(false);
        importButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importButton1ActionPerformed(evt);
            }
        });

        errorLabel.setForeground(new java.awt.Color(255, 0, 0));
        errorLabel.setText("                             ");

        jLabel1.setText("Name:");

        exampleBtn.setText("example");
        exampleBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exampleBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(errorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(importButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addGap(42, 42, 42))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(createRadBtn)
                    .addComponent(loadRadBtn))
                .addGap(362, 362, 362))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addGap(106, 106, 106)
                            .addComponent(networkFileBox))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addGap(79, 79, 79)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel1)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel4)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(popComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(projectComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 142, Short.MAX_VALUE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(netNameText, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                                    .addGap(66, 66, 66)))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(76, 76, 76)
                        .addComponent(jLabel6)
                        .addGap(20, 20, 20)
                        .addComponent(algorithmComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(importButton1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(fileButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(exampleBtn)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(projectComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(popComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addComponent(createRadBtn))
                            .addComponent(netNameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(algorithmComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(loadRadBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(importButton1)
                        .addGap(41, 41, 41)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(networkFileBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fileButton)
                    .addComponent(exampleBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(importButton)
                    .addComponent(cancelButton)
                    .addComponent(errorLabel))
                .addContainerGap())
        );

        jLabel1.getAccessibleContext().setAccessibleName("Name");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    private void networkFileBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_networkFileBoxMouseClicked
        fileButtonActionPerformed(null);
}//GEN-LAST:event_networkFileBoxMouseClicked

    private void fileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileButtonActionPerformed
        if (!canSelect)
        {
            return;
        }

        JFileChooser c = new JFileChooser(Model.getInstance().GetLastFilePath());
        // Demonstrate "Open" dialog:
        int rVal = c.showOpenDialog(this);
        if (rVal == JFileChooser.APPROVE_OPTION)
        {
            this.networkFileBox.setText(c.getSelectedFile().getAbsolutePath());
            Model.getInstance().AccountForLastFilePath(c.getSelectedFile().getAbsolutePath());
        }
}//GEN-LAST:event_fileButtonActionPerformed

    /**
     * Calls the appropriate methods to get this data imported .. after checking
     * on a few conditions. 
     * @param evt
     */
    private void importButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importButtonActionPerformed

        String projectName;
        String traitName;
        if (this.projectComboBox.getSelectedItem().equals("<Select Project>"))
        {
            String s = "You must select a valid project option.";
            this.errorLabel.setText(s);
            return;
        }
        projectName = (String) this.projectComboBox.getSelectedItem();
        if (this.popComboBox.getSelectedItem().equals("<Select Marker>"))
        {
            String s = "You must select a valid marker option.";
            this.errorLabel.setText(s);
            return;
        }
        traitName = (String) this.popComboBox.getSelectedItem();

        if (this.netNameText.getText().compareToIgnoreCase("") == 0)
        {
            String s = "You must enter a population name.";
            this.errorLabel.setText(s);
            return;
        }
        
        if (this.netNameText.getText().length() > 30)
        {
            String s = "Name may be at most 30 characters.";
            this.errorLabel.setText(s);
            return;
        }

        String populationName = this.netNameText.getText();

        if (this.algorithmComboBox.getSelectedItem().equals("<Select Algorithm>") && !this.loadRadBtn.isSelected())
        {
            String s = "You must select a valid algorithms option.";
            this.errorLabel.setText(s);
            return;
        }
        if (this.networkFileBox.getText().compareToIgnoreCase("") == 0 && this.loadRadBtn.isSelected())
        {
            String s = "You must select a File having the population infromation.";
            this.errorLabel.setText(s);
            return;
        }

        int projID = Model.getInstance().getProject(projectName).getId();
        int markerID = Model.getInstance().getProject(projectName).getMarker(traitName).getId();
        ArrayList<String> popnames = Model.getInstance().getProject(projectName).getMarker(traitName).getPopulationNames();

        for (String s : popnames)
        {
            if (s.equals(populationName))
            {
                errorLabel.setText("Pop name already in use.");
                return;
            }
        }

        if (this.createRadBtn.isSelected())
        {
            StructureParameterObject paramOb = new StructureParameterObject(populationName);
            view.addAlgorithm("STR", 11, projID, 0, markerID, paramOb);
        }
        else
        {
            File file = new File(this.networkFileBox.getText());
            if (!file.exists())
            {
                this.errorLabel.setText("Data file does not exist.");
                return;
            }
            DataAddRemoveHandler.getInstance().addPopulation(this.networkFileBox.getText(), projID, markerID, this.netNameText.getText());
        }
        this.closeDialog(null);
}//GEN-LAST:event_importButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.closeDialog(null);
}//GEN-LAST:event_cancelButtonActionPerformed

    private void projectComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectComboBoxActionPerformed
        if (this.projectComboBox.getSelectedItem().equals("<Select Project>"))
        {
            this.popComboBox.setEnabled(false);
            return;
        }
        else
        {
            this.popComboBox.setEnabled(true);
        }

        Project project = Model.getInstance().getProject(this.projectComboBox.getSelectedItem().toString());

        this.popComboBox.removeAllItems();
        this.popComboBox.addItem("<Select Marker>");
        for (int i = 0; i < project.getMarkers().size(); i++)
        {
            if (project.getMarkers().get(i).hasData())
            {
                this.popComboBox.addItem(project.getMarkers().get(i).getName());
            }
        }
    }//GEN-LAST:event_projectComboBoxActionPerformed

    private void createRadBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createRadBtnActionPerformed
        this.fileButton.setEnabled(false);
        this.exampleBtn.setEnabled(false);
        this.networkFileBox.setEnabled(false);
        this.algorithmComboBox.setEnabled(true);
        this.jLabel1.setEnabled(false);
        canSelect = false;
    }//GEN-LAST:event_createRadBtnActionPerformed

    private void loadRadBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadRadBtnActionPerformed
        this.fileButton.setEnabled(true);
        this.exampleBtn.setEnabled(true);
        this.networkFileBox.setEnabled(true);
        //this.projectComboBox.setEnabled(false);
        //this.traitComboBox.setEnabled(false);
        this.algorithmComboBox.setEnabled(false);
//        this.EdgeButton.setEnabled(true);
        //  this.tabDelButton.setEnabled(true);
        this.jLabel1.setEnabled(true);
        this.netNameText.setEnabled(true);
        canSelect = true;
    }//GEN-LAST:event_loadRadBtnActionPerformed

    private void importButton1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_importButton1ActionPerformed
    {//GEN-HEADEREND:event_importButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_importButton1ActionPerformed

    private void algorithmComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_algorithmComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_algorithmComboBoxActionPerformed

    private void exampleBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_exampleBtnActionPerformed
    {//GEN-HEADEREND:event_exampleBtnActionPerformed
        //Open file in notepad or vi or show error message telling the user where to find the file
        if(!ExampleFileHandler.display("populationStructure"))
            JOptionPane.showMessageDialog(this, ExampleFileHandler.failMessage);
    }//GEN-LAST:event_exampleBtnActionPerformed

    private void popComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_popComboBoxActionPerformed
    {//GEN-HEADEREND:event_popComboBoxActionPerformed
        if(this.popComboBox.getSelectedIndex() <= 0)
        {
            return;
        }

        if (this.projectComboBox.getSelectedItem().equals("<Select Project>"))
        {
            this.popComboBox.setEnabled(false);
            return;
        }
        else
        {
            this.popComboBox.setEnabled(true);
        }

        Project project = Model.getInstance().getProject(this.projectComboBox.getSelectedItem().toString());

        String markerset = (String) popComboBox.getSelectedItem();
        MarkerSet ms = project.getMarker(markerset);

        if(!ms.hasData())
        {
            this.jLabel6.setEnabled(false);
            this.algorithmComboBox.setEnabled(false);
            this.createRadBtn.setEnabled(false);
            this.loadRadBtn.setSelected(true);
            this.loadRadBtnActionPerformed(null);
        }
        else
        {
            this.jLabel6.setEnabled(true);
            this.algorithmComboBox.setEnabled(true);
            this.createRadBtn.setEnabled(true);
            this.createRadBtn.setSelected(true);
            this.createRadBtnActionPerformed(null);
        }

    }//GEN-LAST:event_popComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox algorithmComboBox;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton cancelButton;
    private javax.swing.JRadioButton createRadBtn;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JButton exampleBtn;
    private javax.swing.JButton fileButton;
    private javax.swing.JButton importButton;
    private javax.swing.JButton importButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton loadRadBtn;
    private javax.swing.JTextField netNameText;
    private javax.swing.JTextField networkFileBox;
    private javax.swing.JComboBox popComboBox;
    private javax.swing.JComboBox projectComboBox;
    // End of variables declaration//GEN-END:variables
}