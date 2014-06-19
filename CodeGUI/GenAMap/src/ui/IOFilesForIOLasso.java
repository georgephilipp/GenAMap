/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import controller.Constants;
import datamodel.Model;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

/**
 *
 * @author Georg Schoenherr <Georg Schoenherr>
 */
public class IOFilesForIOLasso extends JDialog{
    private JPanel mainPanel;
    
    private JLabel inputGroupFileLabel;
    private JLabel outputGroupFileLabel;
    
    private JTextField inputGroupFileTextField;
    private JTextField outputGroupFileTextField;
    
    private JButton inputGroupFileBrowseButton;
    private JButton outputGroupFileBrowseButton;
    private JButton okButton;
   
    private Component parentComponentReference;
    
    public IOFilesForIOLasso(Component parent){
        parentComponentReference = parent;
        
        labelFactory();
        textFieldFactory();
        buttonFactory();
        dialogSettings();
        componentSettings();
    }
    
    private void componentSettings(){
        mainPanel.add(inputGroupFileLabel, new AbsoluteConstraints(10, 10, 100, 20));
        mainPanel.add(inputGroupFileTextField, new AbsoluteConstraints(120, 10, 200, 20));
        mainPanel.add(inputGroupFileBrowseButton, new AbsoluteConstraints(330, 10, 30, 20));
        mainPanel.add(outputGroupFileLabel, new AbsoluteConstraints(10, 40, 100, 20));
        mainPanel.add(outputGroupFileTextField, new AbsoluteConstraints(120, 40, 200, 20));
        mainPanel.add(outputGroupFileBrowseButton, new AbsoluteConstraints(330, 40, 30, 20));
        mainPanel.add(okButton, new AbsoluteConstraints(280, 70, 80, 20));
        
        this.add(mainPanel, new AbsoluteConstraints(0, 0, Constants.ioFilesForIOLassoFrameWidth, Constants.ioFilesForIOLassoFrameHeight));
    }
    
    private void dialogSettings(){
        this.pack();
        this.setTitle("Input & Output Groups");
        this.setSize(Constants.ioFilesForIOLassoFrameWidth,Constants.ioFilesForIOLassoFrameHeight);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLayout(new AbsoluteLayout());
        this.setResizable(false); 
        this.setLocationRelativeTo(parentComponentReference);
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
    }
    private void labelFactory(){
        inputGroupFileLabel = new JLabel(Constants.inputGroupFileLabelText);
        inputGroupFileLabel.setFont(Constants.defaultFont);
        
        outputGroupFileLabel = new JLabel(Constants.outputGroupFileLabelText);
        outputGroupFileLabel.setFont(Constants.defaultFont);
    }
    
    private void textFieldFactory(){
        inputGroupFileTextField = new JTextField();
        inputGroupFileTextField.setFont(Constants.defaultFont);        
        
        outputGroupFileTextField = new JTextField();
        outputGroupFileTextField.setFont(Constants.defaultFont);        
    }
    
    private void buttonFactory(){
        inputGroupFileBrowseButton = new JButton(Constants.inputGroupFileBrowseButtonText);
        inputGroupFileBrowseButton.setBorder(BorderFactory.createRaisedBevelBorder());
        inputGroupFileBrowseButton.setEnabled(false);
        inputGroupFileBrowseButton.addActionListener(new ActionListener() {
        @Override
            public void actionPerformed(ActionEvent e) {
                inputGroupFileButtonActionPerformed(e);
            }
        });
        
        outputGroupFileBrowseButton = new JButton(Constants.outputGroupFileBrowseButtonText);
        outputGroupFileBrowseButton.setBorder(BorderFactory.createRaisedBevelBorder());
        outputGroupFileBrowseButton.setEnabled(false);
        outputGroupFileBrowseButton.addActionListener(new ActionListener() {
        @Override
            public void actionPerformed(ActionEvent e) {
                outputGroupFileButtonActionPerformed(e);
            }
        });
        
        okButton = new JButton(Constants.okButtonText);
        okButton.setBorder(BorderFactory.createRaisedBevelBorder());
        okButton.setEnabled(false);
        okButton.addActionListener(new ActionListener() {
        @Override
            public void actionPerformed(ActionEvent e) {
                okButtonActionPerformed(e);
            }
        });
    }
    
    private void inputGroupFileButtonActionPerformed(java.awt.event.ActionEvent evt)                                         
    {                                             
        JFileChooser c = new JFileChooser(Model.getInstance().GetLastFilePath());
        int rVal = c.showOpenDialog(this);
        if (rVal == JFileChooser.APPROVE_OPTION)
        {
            this.inputGroupFileTextField.setText(c.getSelectedFile().getAbsolutePath());
            Model.getInstance().AccountForLastFilePath(c.getSelectedFile().getAbsolutePath());
        }
    }                                        

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt)                                         
    {                                             
        this.setVisible(false);
    }                                        

    private void outputGroupFileButtonActionPerformed(java.awt.event.ActionEvent evt)                                         
    {                                             
        JFileChooser c = new JFileChooser(Model.getInstance().GetLastFilePath());
        int rVal = c.showOpenDialog(this);
        if (rVal == JFileChooser.APPROVE_OPTION)
        {
            this.outputGroupFileTextField.setText(c.getSelectedFile().getAbsolutePath());
            Model.getInstance().AccountForLastFilePath(c.getSelectedFile().getAbsolutePath());
        }
    }
    
    public String getInputFilePath()
    {
        return inputGroupFileTextField.getText();
    }

    public String getOutputFilePath()
    {
        return outputGroupFileTextField.getText();
    }
}
