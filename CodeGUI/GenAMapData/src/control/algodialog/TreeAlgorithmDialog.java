package control.algodialog;

import algorithm.AlgorithmView;
import algorithm.AssociationParameterObject;
import control.DataAddRemoveHandler;
import datamodel.TraitSet;
import java.io.FileNotFoundException;
import javax.swing.JFileChooser;
import datamodel.Project;
import datamodel.Model;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JFrame;
import datamodel.TraitTreeVal;
import java.util.*;

import datamodel.Network;
import javax.swing.JOptionPane;

/**
 * Reads in a tree, or calls the appropriate methods to create a tree.
 * This method give the user the options they need to create the tree they
 * want.
 * @author jvendries
 * @author rcurtis
 */
public class TreeAlgorithmDialog extends java.awt.Dialog
{
    AlgorithmView view;
    boolean canSelect = false;
    int traitSetId;
    JFrame parent;

    /**
     * Creates a new tree algorithm dialog
     * @param parent the parent frame of the dailog
     * @param modal
     * @param view the algorithm view that will run any algorithms created
     * @param createSelected if the create button should be auto selected
     * @param traits the trait that were selected in creating the tree
     * @param project the project name of the traits
     */
    public TreeAlgorithmDialog(java.awt.Frame parent, boolean modal, AlgorithmView view, boolean createSelected,
            String traits, String project)
    {
        super(parent, modal);
        this.view = view;
        initComponents();
        this.parent = (JFrame) parent;
        this.setLocation(parent.getLocation());

        this.traitComboBox.setEnabled(false);

        this.algorithmComboBox.addItem("Agglomerate Hierarchical Clustering");

        ArrayList<Project> temp = Model.getInstance().getProjects();
        for (int i = 0; i < temp.size(); i++)
        {
            this.projectComboBox.addItem(temp.get(i).getName());
        }

        if (createSelected)
        {
            this.createRadBtn.setSelected(true);
            this.fileButton.setEnabled(false);
            this.fileButton1.setEnabled(false);
            this.treeFileBox.setEnabled(false);
        }
        else
        {
            this.loadRadBtn.setSelected(true);
            this.fileButton.setEnabled(true);
            this.fileButton1.setEnabled(true);
            this.treeFileBox.setEnabled(true);
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

            if (traits != null)
            {
                for (int i = 0; i < this.traitComboBox.getItemCount(); i++)
                {
                    if (this.traitComboBox.getItemAt(i).toString().equals(traits))
                    {
                        this.traitComboBox.setSelectedIndex(i);
                        i = this.traitComboBox.getItemCount();
                    }
                }
            }
        }

    }

    /**
     * Parses a tree file in the siblings format and returns the root pointer. ((),())
     * @param fi the file to read in. 
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private TraitTreeVal loadSiblingsTree(String fi) throws FileNotFoundException, IOException
    {
        FileInputStream fstream = new FileInputStream(fi);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine;
        TraitTreeVal root = null;

        while ((strLine = br.readLine()) != null)
        {
            strLine = strLine.trim();
            int lastParenIndex = strLine.lastIndexOf(")");

            String newLine = strLine.substring(0, lastParenIndex) + ",)";
            String r = "";
            for (int i = 0; i < newLine.length(); i++)
            {
                if (newLine.charAt(i) != ' ')
                {
                    r += newLine.charAt(i);
                }
            }

            System.out.println("building " + r);
            int newLastParen = r.lastIndexOf(")");

            root = buildTree(r, new TraitTreeVal("ROOT"), 0, newLastParen);
            root.setParent(new TraitTreeVal("rootParent"));

        }//end of file read loop
        return root;

    }

    /**
     * Called from reading the siblings format. Puts the tree together. 
     * @param s
     * @param parent
     * @param from
     * @param to
     * @return
     */
    private TraitTreeVal buildTree(String s, TraitTreeVal parent, int from, int to)
    {
        if (s.charAt(from) != '(')
        {
            parent.setName(s.substring(from, to));
            return parent;
        }

        int b = 0;//bracket counter
        int x = from; //position marker

        for (int i = from; i < to; i++)
        {
            char c = s.charAt(i);

            if (c == '(')
            {
                b++;
            }
            else if (c == ')')
            {
                b--;
            }

            if (b == 0 || b == 1 && c == ',')
            {
                String name = Integer.toString(x + 1) + "to" + i;
                parent.addChild(buildTree(s, new TraitTreeVal(name), x + 1, i));
                x = i;
            }
        }

        return parent;

    }

    /**
     * Format being loaded in this function is:
     * T1   P1
     * Where values that star with T represent child nodes, which encode Traits, and
     * values that start with P are intermediate tree nodes which DO NOT represent Traits
     * @param fi file containing tree in format specified above
     * @return a list of traittreeeval objects containing the traits specified
     * @throws FileNotFoundException
     * @throws IOException
     */
    private TraitTreeVal loadTabsTree(String fi) throws FileNotFoundException, IOException
    {
        String networkFile = this.treeFileBox.getText();
        FileInputStream fstream = new FileInputStream(networkFile);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine;
        TraitTreeVal root = null;

        //this map keeps track of values we've read in so that:
        //1. we don't create duplicate TraitTreeEval objects
        //2. we can assign the same parent object to sibling nodes
        HashMap<String, TraitTreeVal> nodes = new HashMap<String, TraitTreeVal>();

        //System.out.println("in loadtree b3 while loop");
        while ((strLine = br.readLine()) != null)
        {
            strLine = strLine.trim();
            String[] ln = strLine.split("\t");

            //System.out.println("line items: "+ln.length+", line read: "+ln[0]+" "+ln[1]);
            if (strLine.length() > 0)
            {
                TraitTreeVal currentNode;
                TraitTreeVal parentNode;

                //checking if current child node has been created
                if (!nodes.containsKey(ln[0]))
                {
                    currentNode = new TraitTreeVal();
                    currentNode.setName(ln[0]);
                    //since this is a child of another node we assume for now that it is a leaf and create a
                    //trait object for it. as a backup also especifing it in the boolean field
                }
                else
                {
                    //CAN YOU HAVE MORE THAN ONE PARENT PER NODE?
                    currentNode = nodes.get(ln[0]);
                }
                //check if parent node has been created
                if (!nodes.containsKey(ln[1]))
                {
                    parentNode = new TraitTreeVal();
                    parentNode.setName(ln[1]);

                    //only leaf nodes can have traits
                    parentNode.setTrait(null);

                    //if the parent of this node is the root
                    if (ln[1].toUpperCase().equals("ROOT"))
                    {
                        //The root element must have a non-null parent with id 1.
                        TraitTreeVal rootParent = new TraitTreeVal();
                        rootParent.setId(1);
                        rootParent.setName("rootparent");//for debugging purposes.
                        parentNode.setParent(rootParent);

                        //tree.add(parentNode);
                        root = parentNode;
                    }
                }
                else
                {
                    parentNode = nodes.get(ln[1]);
                    //we may have encountered this node as a leaf. Because now we encounter it as a parent
                    //we must make sure it does not have a trait object
                    parentNode.setTrait(null);
                }

                //System.out.println("checkpoint 3");
                currentNode.setParent(parentNode);
                parentNode.addChild(currentNode);

                nodes.put(ln[0], currentNode);
                nodes.put(ln[1], parentNode);
            }
        }
        in.close();
        in = null;
        return root;
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
        traitComboBox = new javax.swing.JComboBox();
        treeFileBox = new javax.swing.JTextField();
        fileButton = new javax.swing.JButton();
        createRadBtn = new javax.swing.JRadioButton();
        loadRadBtn = new javax.swing.JRadioButton();
        importButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        algorithmComboBox = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        networkComboBox = new javax.swing.JComboBox();
        errorLabel = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        treeNameTextBox = new javax.swing.JTextField();
        tabDelButton = new javax.swing.JRadioButton();
        SiblingsButton = new javax.swing.JRadioButton();
        fileButton1 = new javax.swing.JButton();

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

        jLabel5.setText("Trait Set");

        traitComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<Select Trait>" }));
        traitComboBox.setEnabled(false);
        traitComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                traitComboBoxActionPerformed(evt);
            }
        });

        treeFileBox.setEditable(false);
        treeFileBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                treeFileBoxMouseClicked(evt);
            }
        });

        fileButton.setText("...");
        fileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(createRadBtn);
        createRadBtn.setFont(new java.awt.Font("DejaVu Sans", 1, 18));
        createRadBtn.setActionCommand("Create a new Clustering");
        createRadBtn.setLabel("Create a new Tree");
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

        jLabel7.setText("Algorithm");

        algorithmComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<Select Algorithm>" }));
        algorithmComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                algorithmComboBoxActionPerformed(evt);
            }
        });

        jLabel8.setText("Network");

        networkComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<Select Network>" }));
        networkComboBox.setEnabled(false);

        errorLabel.setForeground(new java.awt.Color(255, 51, 0));
        errorLabel.setText("                                      ");

        jLabel9.setText("Name");

        buttonGroup2.add(tabDelButton);
        tabDelButton.setLabel("Parent Child Format");

        buttonGroup2.add(SiblingsButton);
        SiblingsButton.setLabel("((),()) format");

        fileButton1.setText("example");
        fileButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(198, 198, 198)
                        .addComponent(importButton, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(algorithmComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(networkComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(errorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(createRadBtn)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addGap(31, 31, 31)
                                        .addComponent(treeNameTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(loadRadBtn)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(44, 44, 44)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(tabDelButton)
                                        .addGap(18, 18, 18)
                                        .addComponent(SiblingsButton))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(treeFileBox, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(fileButton, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(fileButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4))
                        .addGap(17, 17, 17)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(traitComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(projectComboBox, 0, 282, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(projectComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(traitComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(treeNameTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(createRadBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(algorithmComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(networkComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(loadRadBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(treeFileBox, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fileButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fileButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tabDelButton)
                    .addComponent(SiblingsButton))
                .addGap(13, 13, 13)
                .addComponent(errorLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(importButton)
                    .addComponent(cancelButton)))
        );

        add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    private void treeFileBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeFileBoxMouseClicked
        fileButtonActionPerformed(null);
}//GEN-LAST:event_treeFileBoxMouseClicked

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
            this.treeFileBox.setText(c.getSelectedFile().getAbsolutePath());
            Model.getInstance().AccountForLastFilePath(c.getSelectedFile().getAbsolutePath());
        }
}//GEN-LAST:event_fileButtonActionPerformed

    /**
     * Checks conditions and then parses the files/starts the algorithm. 
     * @param evt
     */
    private void importButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importButtonActionPerformed
        if (this.networkComboBox.getSelectedItem().equals("<Select Network>") && this.networkComboBox.isEnabled())
        {
            String s = "You must selected a valid network option for this algorithm";
            this.errorLabel.setText(s);
            return;
        }

        if (this.treeNameTextBox.getText().equals(""))
        {
            String s = "You must determine a valid name.";
            this.errorLabel.setText(s);
            return;
        }

        String projectName;
        String traitName;
        if (this.projectComboBox.getSelectedItem().equals("<Select Project>"))
        {
            String s = "You must select a valid project option.";
            this.errorLabel.setText(s);
            return;
        }
        projectName = (String) this.projectComboBox.getSelectedItem();
        if (this.traitComboBox.getSelectedItem().equals("<Select Traits>"))
        {
            String s = "You must select a valid traits option.";
            this.errorLabel.setText(s);
            return;
        }
        traitName = (String) this.traitComboBox.getSelectedItem();

        if (this.algorithmComboBox.getSelectedItem().equals("<Select Algorithm>") && !this.loadRadBtn.isSelected())
        {
            String s = "You must select a valid algorithms option.";
            this.errorLabel.setText(s);
            return;
        }

        if (this.treeNameTextBox.getText().equals(""))
        {
            errorLabel.setText("You must choose a name");
            return;
        }

        int projID = Model.getInstance().getProject(projectName).getId();
        int traitID = Model.getInstance().getProject(projectName).getTrait(traitName).getId();

        Project ap = Model.getInstance().getProject(projectName);
        TraitSet ts = ap.getTrait(traitName);

        String networkName = this.networkComboBox.getSelectedItem().toString();
        ArrayList<String> inUse = Model.getInstance().getProject(this.projectComboBox.getSelectedItem().toString()).getTrait(this.traitComboBox.getSelectedItem().toString()).getTreeNames();

        for (int i = 0; i < inUse.size(); i++)
        {
            if (this.treeNameTextBox.getText().equals(inUse.get(i)))
            {
                String s1 = "Tree name already exists for these traits";
                this.errorLabel.setText(s1);
                return;
            }
        }

        if (this.createRadBtn.isSelected())
        {
            AssociationParameterObject apo = new AssociationParameterObject(
                    (Network) ts.getTraitStructure(networkName),
                    this.treeNameTextBox.getText());

            view.addAlgorithm("TRE", 10, projID, traitID, -1, apo);
        }
        else if (this.loadRadBtn.isSelected())
        {
            if (this.tabDelButton.isSelected())
            {
                try
                {
                    String TreeFile = this.treeFileBox.getText();
                    TraitTreeVal root = this.loadTabsTree(TreeFile);
                    if (root == null)
                    {
                        this.errorLabel.setText("Error reading in the file.");
                        return;
                    }
                    DataAddRemoveHandler.getInstance().addTraitTree(projID, traitID, this.treeNameTextBox.getText(), root);
                }
                catch (Exception e)
                {
                    errorLabel.setText("There was an error reading this file.");
                    return;
                }
            }
            else if (this.SiblingsButton.isSelected())
            {

                try
                {
                    String TreeFile = this.treeFileBox.getText();
                    TraitTreeVal root = this.loadSiblingsTree(TreeFile);
                    if (root == null)
                    {
                        this.errorLabel.setText("Error reading in this file");
                        return;
                    }

                    DataAddRemoveHandler.getInstance().addTraitTree(projID, traitID, this.treeNameTextBox.getText(), root);
                }
                catch (Exception ex)
                {
                    this.errorLabel.setText("Error reading in this file");
                    return;
                }
            }
            else
            {
                this.errorLabel.setText("You must select a valid file format.");
                return;
            }

        }
        this.closeDialog(null);
}//GEN-LAST:event_importButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.closeDialog(null);
}//GEN-LAST:event_cancelButtonActionPerformed

    private void projectComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectComboBoxActionPerformed
        if (this.projectComboBox.getSelectedItem().equals("<Select Project>"))
        {
            this.traitComboBox.setEnabled(false);
            return;
        }
        else
        {
            this.traitComboBox.setEnabled(true);
        }

        Project project = Model.getInstance().getProject(this.projectComboBox.getSelectedItem().toString());

        this.traitComboBox.removeAllItems();
        this.traitComboBox.addItem("<Select Traits>");
        for (int i = 0; i < project.getTraits().size(); i++)
        {
            this.traitComboBox.addItem(project.getTraits().get(i).getName());
        }
    }//GEN-LAST:event_projectComboBoxActionPerformed

    private void createRadBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createRadBtnActionPerformed
        this.fileButton.setEnabled(false);
        this.fileButton1.setEnabled(false);
        this.treeFileBox.setEnabled(false);
        //this.projectComboBox.setEnabled(true);
        //this.traitComboBox.setEnabled(true);
        this.algorithmComboBox.setEnabled(true);
        networkComboBox.setEnabled(true);
        canSelect = false;
    }//GEN-LAST:event_createRadBtnActionPerformed

    private void loadRadBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadRadBtnActionPerformed
        this.fileButton.setEnabled(true);
        this.fileButton1.setEnabled(true);
        this.treeFileBox.setEnabled(true);
        //this.projectComboBox.setEnabled(false);
        //this.traitComboBox.setEnabled(false);
        this.algorithmComboBox.setEnabled(false);
        networkComboBox.setEnabled(false);
        canSelect = true;
    }//GEN-LAST:event_loadRadBtnActionPerformed

    private void traitComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_traitComboBoxActionPerformed
        if (this.traitComboBox.getSelectedObjects().length == 0 || this.traitComboBox.getSelectedItem().equals("<Select Traits>") || !this.traitComboBox.isEnabled())
        {

            return;
        }

        Project project = Model.getInstance().getProject(this.projectComboBox.getSelectedItem().toString());

        this.networkComboBox.setEnabled(true);

        this.networkComboBox.removeAllItems();
        this.networkComboBox.addItem("<Select Network>");
        TraitSet t = project.getTrait(this.traitComboBox.getSelectedItem().toString());

        if (t.getHasData())
        {
            this.jLabel7.setEnabled(true);
            this.jLabel8.setEnabled(true);
            this.algorithmComboBox.setEnabled(true);
            this.createRadBtn.setEnabled(true);
            this.createRadBtn.setSelected(true);
            this.createRadBtnActionPerformed(null);

            for (int i = 0; i < t.getNetworkIdentifiers().size(); i++)
            {
                this.networkComboBox.addItem(t.getNetworkIdentifiers().get(i));
            }
        }
        else
        {
            this.jLabel7.setEnabled(false);
            this.jLabel8.setEnabled(false);
            this.algorithmComboBox.setEnabled(false);
            this.createRadBtn.setEnabled(false);
            this.loadRadBtn.setSelected(true);
            this.loadRadBtnActionPerformed(null);
        }
    }//GEN-LAST:event_traitComboBoxActionPerformed

    private void algorithmComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_algorithmComboBoxActionPerformed
        this.traitComboBoxActionPerformed(evt);
    }//GEN-LAST:event_algorithmComboBoxActionPerformed

    private void fileButton1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_fileButton1ActionPerformed
    {//GEN-HEADEREND:event_fileButton1ActionPerformed
        //Open file in notepad or vi or show error message telling the user where to find the file
        Runtime load = Runtime.getRuntime();
        try
        {
            load.exec("notepad treeEXAMPLE1.txt");
            load.exec("notepad treeEXAMPLE2.txt");
        }
        catch (Exception ex)
        {
            try
            {
                load.exec("vi treeEXAMPLE1.txt");
                load.exec("vi treeEXAMPLE2.txt");
            }
            catch (Exception ex1)
            {
                JOptionPane.showMessageDialog(this, "I can't open the example file.\n" +
                        "Please look in the distribution directory for treeEXAMPLE1 and treeEXAMPLE2.txt");
            }
        }
    }//GEN-LAST:event_fileButton1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton SiblingsButton;
    private javax.swing.JComboBox algorithmComboBox;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton cancelButton;
    private javax.swing.JRadioButton createRadBtn;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JButton fileButton;
    private javax.swing.JButton fileButton1;
    private javax.swing.JButton importButton;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton loadRadBtn;
    private javax.swing.JComboBox networkComboBox;
    private javax.swing.JComboBox projectComboBox;
    private javax.swing.JRadioButton tabDelButton;
    private javax.swing.JComboBox traitComboBox;
    private javax.swing.JTextField treeFileBox;
    private javax.swing.JTextField treeNameTextBox;
    // End of variables declaration//GEN-END:variables
}
