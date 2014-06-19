/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import control.algodialog.AssociationAlgorithmDialog;
import control.itempanel.ThreadingItemFrame;
import controller.Constants;
import datamodel.Model;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;
import realdata.Data1;

public class GenAMapMainDialog extends JFrame{
   private JMenuBar mainMenuBar;
   private JMenu fileMenu;
   private JMenu associationMenu;
   private JMenu userMenu;
   private JMenuItem exitMenuItem;
   private JMenuItem addAssociationMenuItem;
   private JMenuItem logOutMenuItem;
   
   private JTabbedPane treeHolderTabbedPane;
   
   private JPanel mainPanel;
   private JPanel markersTabPanel;
   private JPanel traitsTabPanel;
   private JPanel associationTabPanel;
   private JPanel algorithmControlPanel;
   private JPanel visualizationPanel;
   private JPanel statusBarPanel;
   
   private JScrollPane mainScrollPane;
   private JScrollPane markersTabScrollPane;
   private JScrollPane traitsTabScrollPane;
   private JScrollPane associationTabScrollPane;
   private JScrollPane algorithmControlCenterScrollPane;
   private JScrollPane visualizationPanelScrollPane;
   
   private JLabel algorithmControlLabel;
   private JLabel showLabel;
   private JLabel leftStatusLabel;
   private JLabel centerStatusLabel;
   private JLabel rightStatusLabel;
   
   private JRadioButton runningRadioButton;
   private JRadioButton errorRadioButton;
   private JRadioButton completeRadioButton;
   private JRadioButton allRadioButton;
   private ButtonGroup radioButtonGroup;
   
   private Component currentComponentReference;
   
   private views.AssociationView associationView1;
   private views.AssociationObjectTabs associationObjectTabs1;
   private algorithm.AlgorithmView algorithmView1;
    
   public GenAMapMainDialog() {
       super();
       currentComponentReference = this;
       algorithmView1 = new algorithm.AlgorithmView();
       associationObjectTabs1 = new views.AssociationObjectTabs();
       associationView1 = new views.AssociationView();
       
       if (Model.getInstance() == null)
       {
            System.exit(0);
       }
       
       
       associationView1.init(this);
       associationObjectTabs1.setup();
 
        dialogSettings();
        menuFactory();
        panelFactory();
        scrollPaneFactory();
        labelFactory();
        radioButtonFactory();
        tabbedPaneFactory();
        componentSettings();
        
        ThreadingItemFrame.getInstance().startJobs();
    }
    
    private void componentSettings(){
        statusBarPanel.add(leftStatusLabel, new AbsoluteConstraints(10,0,200,20));
        statusBarPanel.add(centerStatusLabel, new AbsoluteConstraints((Constants.mainDialogWidth/2)-100,0,200,20));
        statusBarPanel.add(rightStatusLabel, new AbsoluteConstraints(Constants.mainDialogWidth-200,0,200,20));
        
        //mainPanel.add(treeHolderTabbedPane, new AbsoluteConstraints(10,10,350,400));
        mainPanel.add(associationObjectTabs1, new AbsoluteConstraints(10,10,350,400));
        mainPanel.add(algorithmControlLabel, new AbsoluteConstraints(85,420,250,30));
        mainPanel.add(algorithmControlCenterScrollPane, new AbsoluteConstraints(10,450,350,250));
        mainPanel.add(visualizationPanelScrollPane, new AbsoluteConstraints(370,10,Constants.mainDialogWidth - 385, Constants.mainDialogHeight - 78));
        mainPanel.add(statusBarPanel, new AbsoluteConstraints(0,Constants.mainDialogHeight-70,Constants.mainDialogWidth,20));
        
        algorithmControlPanel.add(showLabel, new AbsoluteConstraints(10,10,40,20));
        algorithmControlPanel.add(runningRadioButton, new AbsoluteConstraints(50,10,80,20));
        algorithmControlPanel.add(completeRadioButton, new AbsoluteConstraints(130,10,80,20));
        algorithmControlPanel.add(errorRadioButton, new AbsoluteConstraints(220,10,60,20));
        algorithmControlPanel.add(allRadioButton, new AbsoluteConstraints(290,10,40,20));
        
        this.add(mainMenuBar, new AbsoluteConstraints(0,0,Constants.mainMenuBarWidth, Constants.mainMenuBarHeight));
        this.add(mainScrollPane, new AbsoluteConstraints(0,Constants.mainMenuBarHeight,Constants.mainDialogWidth,Constants.mainDialogHeight-Constants.mainMenuBarHeight));        
    }
    
    private void dialogSettings(){
        this.pack();
        this.setTitle("GenAMap");
        this.setSize(Constants.mainDialogWidth,Constants.mainDialogHeight);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLayout(new AbsoluteLayout());
        this.setResizable(false); 
        this.setIconImage(new ImageIcon(Constants.applicationIcon).getImage());
        this.validate();
        this.setVisible(true);   
        
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
    }
    
    private void panelFactory(){
        mainPanel = new JPanel();
        mainPanel.setEnabled(true);
        mainPanel.setVisible(true);
        mainPanel.setLayout(new AbsoluteLayout());
        mainPanel.setBorder(BorderFactory.createEtchedBorder());
        
        markersTabPanel = new JPanel();
        markersTabPanel.setEnabled(true);
        markersTabPanel.setVisible(true);
        markersTabPanel.setLayout(new AbsoluteLayout());
        markersTabPanel.setBorder(BorderFactory.createEtchedBorder());
        
        traitsTabPanel = new JPanel();
        traitsTabPanel.setEnabled(true);
        traitsTabPanel.setVisible(true);
        traitsTabPanel.setLayout(new AbsoluteLayout());
        traitsTabPanel.setBorder(BorderFactory.createEtchedBorder());
        
        associationTabPanel = new JPanel();
        associationTabPanel.setEnabled(true);
        associationTabPanel.setVisible(true);
        associationTabPanel.setLayout(new AbsoluteLayout());
        associationTabPanel.setBorder(BorderFactory.createEtchedBorder());
        
        algorithmControlPanel = new JPanel();
        algorithmControlPanel.setEnabled(true);
        algorithmControlPanel.setVisible(true);
        algorithmControlPanel.setLayout(new AbsoluteLayout());
        algorithmControlPanel.setBorder(BorderFactory.createEtchedBorder());
        
        visualizationPanel = new JPanel();
        visualizationPanel.setEnabled(true);
        visualizationPanel.setVisible(true);
        visualizationPanel.setLayout(new AbsoluteLayout());
        visualizationPanel.setBorder(BorderFactory.createEtchedBorder());
        
        statusBarPanel = new JPanel();
        statusBarPanel.setEnabled(true);
        statusBarPanel.setVisible(true);
        statusBarPanel.setLayout(new AbsoluteLayout());
        statusBarPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        statusBarPanel.setBackground(Color.lightGray);
    }
    
    private void scrollPaneFactory(){
        mainScrollPane = new JScrollPane(mainPanel);
        markersTabScrollPane = new JScrollPane(markersTabPanel);
        traitsTabScrollPane = new JScrollPane(traitsTabPanel);
        associationTabScrollPane = new JScrollPane(associationTabPanel);
        //algorithmControlCenterScrollPane = new JScrollPane(algorithmControlPanel);
        algorithmControlCenterScrollPane = new JScrollPane(algorithmView1);
        //visualizationPanelScrollPane = new JScrollPane(visualizationPanel);
        visualizationPanelScrollPane = new JScrollPane(associationView1);
    }
    
    private void radioButtonFactory(){
        runningRadioButton = new JRadioButton(Constants.runningRadioButtonText);
        runningRadioButton.setSelected(true);
        runningRadioButton.setFont(Constants.defaultFont);
        
        errorRadioButton = new JRadioButton(Constants.errorRadioButtonText);
        errorRadioButton.setSelected(false);
        errorRadioButton.setFont(Constants.defaultFont);
        
        completeRadioButton = new JRadioButton(Constants.completeRadioButtonText);
        completeRadioButton.setSelected(false);
        completeRadioButton.setFont(Constants.defaultFont);
        
        allRadioButton = new JRadioButton(Constants.allRadioButtonText);
        allRadioButton.setSelected(false);
        allRadioButton.setFont(Constants.defaultFont);
        
        radioButtonGroup = new ButtonGroup();
        radioButtonGroup.add(runningRadioButton);
        radioButtonGroup.add(errorRadioButton);
        radioButtonGroup.add(completeRadioButton);
        radioButtonGroup.add(allRadioButton);
    }
    
    private void labelFactory(){
        algorithmControlLabel = new JLabel(Constants.algorithmControlLabelText);
        algorithmControlLabel.setFont(Constants.algorithmControlLabelFont);
        
        showLabel = new JLabel(Constants.showLabelText);
        showLabel.setFont(Constants.defaultFont); 
        
        leftStatusLabel = new JLabel();
        centerStatusLabel = new JLabel();
        rightStatusLabel = new JLabel();        
    }
    
    private void menuFactory(){
        exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setMnemonic(KeyEvent.VK_F4);
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitmenuitemActionPerformed(evt);
            }
        });
        
        addAssociationMenuItem = new JMenuItem("Add Association");
        addAssociationMenuItem.setMnemonic(KeyEvent.VK_F3);
        addAssociationMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddAssociationDialog dlg = new AddAssociationDialog(currentComponentReference, true, algorithmView1, true, null, null, null);
                //createassocmenuitemActionPerformed(evt);
            }
        });
        
        logOutMenuItem = new JMenuItem("Logout");
        logOutMenuItem.setMnemonic(KeyEvent.VK_F2);
        logOutMenuItem.setMnemonic('S');
        logOutMenuItem.setText("Logout");
        logOutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setupmenuitemActionPerformed(evt);
            }
        });
        
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.add(exitMenuItem);
        
        associationMenu = new JMenu("Association");
        associationMenu.setMnemonic(KeyEvent.VK_A);
        associationMenu.add(addAssociationMenuItem);
        
        userMenu = new JMenu("User");
        userMenu.setMnemonic(KeyEvent.VK_U);
        userMenu.add(logOutMenuItem);
        
        mainMenuBar = new JMenuBar();
        mainMenuBar.add(fileMenu);
        mainMenuBar.add(associationMenu);
        mainMenuBar.add(userMenu);
    }
    
    private void tabbedPaneFactory(){
        treeHolderTabbedPane = new JTabbedPane();
        
        treeHolderTabbedPane.addTab(Constants.markerTabText, markersTabScrollPane);
        treeHolderTabbedPane.addTab(Constants.traitTabText, traitsTabScrollPane);
        treeHolderTabbedPane.addTab(Constants.associationTabText, associationTabScrollPane);
    }
    
    /**
     * When the GenAMap shuts down, it serializes the data used by the Model
     * and the AlgorithmControl. The AlgorithmControl makes sure to deserialize
     * the SQL settings. This ensures that the GUI can pick up just about where
     * it left off before it was closed down. 
     * @param evt
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {                                   
            Model.serialize();
            this.algorithmView1.acceptClosingMessage();
    }
    
    /**
     * The user can also exit the application from the menu, instead of clicking
     * on the bright red x usually used to exit the application. The same serialization
     * process takes place. 
     * @param evt
     */
    private void exitmenuitemActionPerformed(java.awt.event.ActionEvent evt) {                                             
        if (ThreadingItemFrame.getInstance().canClose())
        {
            Model.serialize();
            this.algorithmView1.acceptClosingMessage();
            System.exit(0);
        }
        else
        {
            JOptionPane.showMessageDialog(this, "You cannot quit until all data operations finish.");
        }
    }
    
    /**
     * All associations are run from the same option box. Because this
     * is common across all of them ... and it is a bit of the focus of the
     * application, we have an item in the main GUI menu. 
     * @param evt
     */
    private void createassocmenuitemActionPerformed(java.awt.event.ActionEvent evt) {                                                    
        AssociationAlgorithmDialog form = new AssociationAlgorithmDialog(this, true, this.algorithmView1, true, null, null, null);
        form.show();
    }
    
    /**
     * When the user logs out, we just mess up the serialization settings so
     * they will have to re-login on their next attempt. Then, we shut down
     * the application. 
     * @param evt
     */
    private void setupmenuitemActionPerformed(java.awt.event.ActionEvent evt) {                                              
        Data1.getInstance().mysqlusername = "bogus";
        exitmenuitemActionPerformed(evt);
    }
}
