/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import controller.Constants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;
import realdata.DataManager;

/**
 *
 * @author Georg
 */
public class AddNewTeamFrame extends JDialog{
    private JLabel teamNameLabel;
    private JLabel securityCodeLabel;
    private JLabel passwordLabel;
    
    public JTextField teamNameTextField;
    public JTextField securityCodeTextField;
    public JPasswordField passwordTextField;
    
    private JButton addTeamButton;
    private JButton cancelButton;
    
    AddNewTeamFrame addNewTeamFrameReference;
    
    public AddNewTeamFrame(){
        addNewTeamFrameReference = this;
        
        frameSettings();
        labelFactory();
        textFieldFactory();
        buttonFactory();
        componentSettings();
    }
    
    private void componentSettings(){
        this.add(teamNameLabel, new AbsoluteConstraints(10,10,80,20));
        this.add(teamNameTextField, new AbsoluteConstraints(100,10,200,20));
        this.add(passwordLabel, new AbsoluteConstraints(10, 40, 80, 20));
        this.add(passwordTextField, new AbsoluteConstraints(100, 40, 200, 20));
        this.add(securityCodeLabel, new AbsoluteConstraints(10,70,80,20));
        this.add(securityCodeTextField, new AbsoluteConstraints(100,70,200,20));
        this.add(addTeamButton, new AbsoluteConstraints(70,100,80,20));
        this.add(cancelButton, new AbsoluteConstraints(160,100,80,20));
    }
    
    private void frameSettings(){
        this.pack();
        this.setTitle(Constants.newTeamFrameTitle);
        this.setSize(Constants.newTeamFrameWidth,Constants.newTeamFrameHeight);
        this.setLayout(new AbsoluteLayout());
        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setIconImage(new ImageIcon(Constants.applicationIcon).getImage());
        this.validate();
        this.setVisible(true);           
    }
    
    private void labelFactory(){
        teamNameLabel = new JLabel(Constants.newTeamLabelText);
        teamNameLabel.setFont(Constants.defaultFont);
        
        passwordLabel = new JLabel(Constants.createAccountPasswordLabelText);
        teamNameLabel.setFont(Constants.defaultFont);
        
        securityCodeLabel = new JLabel(Constants.securityCodeLabelText);
        securityCodeLabel.setFont(Constants.defaultFont);
    }
    
    private void textFieldFactory(){
        teamNameTextField = new JTextField();
        teamNameTextField.setEnabled(true);
        teamNameTextField.setFont(Constants.defaultFont);
        teamNameTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(teamNameTextField.getText().length() >0 && securityCodeTextField.getText().length() >0 && passwordTextField.getPassword().length > 0)
                    addTeamButton.setEnabled(true);
                else
                    addTeamButton.setEnabled(false);
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        
        passwordTextField = new JPasswordField();
        passwordTextField.setEnabled(true);
        passwordTextField.setFont(Constants.defaultFont);
        passwordTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(teamNameTextField.getText().length() > 0 && securityCodeTextField.getText().length() >0 && passwordTextField.getPassword().length > 0)
                    addTeamButton.setEnabled(true);
                else
                    addTeamButton.setEnabled(false);
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        
        securityCodeTextField = new JTextField();
        securityCodeTextField.setEnabled(true);
        securityCodeTextField.setFont(Constants.defaultFont);
        securityCodeTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(teamNameTextField.getText().length() > 0 && securityCodeTextField.toString().length() >0 && passwordTextField.getPassword().length > 0)
                    addTeamButton.setEnabled(true);
                else
                    addTeamButton.setEnabled(false);
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }
    
    private void buttonFactory(){
        addTeamButton = new JButton(Constants.addTeamButtonText);
        addTeamButton.setBorder(BorderFactory.createRaisedBevelBorder());
        addTeamButton.setEnabled(false);
        addTeamButton.addActionListener(new ActionListener() {
        @Override
            public void actionPerformed(ActionEvent e) {
                String teamName = teamNameTextField.getText();
                String securityCode = securityCodeTextField.getText();
                String password = "";
                for(int counter=0; counter<passwordTextField.getPassword().length; counter++)
                    password+=passwordTextField.getPassword()[counter];
                createTeam(teamName, password, securityCode);
                /*String teamName = teamNameTextField.getText();
                String createTeamSecurityCode = securityCodeTextField.getText();
                
                if(DatabaseHandler.isTeamPresent(teamName))
                    UIMessages.showErrorMessage("Specified team already exisits", "Error");
                else{
                    addNewTeamFrameReference.dispose();
                }*/
            }
        });
        
        cancelButton = new JButton(Constants.cancelButtonText);
        cancelButton.setBorder(BorderFactory.createRaisedBevelBorder());
        cancelButton.setEnabled(true);
        cancelButton.addActionListener(new ActionListener() {
        @Override
            public void actionPerformed(ActionEvent e) {
                addNewTeamFrameReference.dispose();
            }
        });
    }
    
    private void createTeam(String teamName, String password, String securityCode){
        boolean teamNameExists = false;
        boolean isSecurityCodeValid = false;
        for(int counter=0; counter<GenAMapLoginWindow.teams.size();counter++)
            if(GenAMapLoginWindow.teams.get(counter).toLowerCase().equals(teamName.toLowerCase()))
                teamNameExists = true;
        
        if(securityCode.equals(Constants.createTeamSecurityCode))
            isSecurityCodeValid = true;
        
        if(teamNameExists == true)
            JOptionPane.showMessageDialog(this, Constants.teamNameExistsErrorMessage, "Error", JOptionPane.ERROR_MESSAGE);
        else if(isSecurityCodeValid == false)
            JOptionPane.showMessageDialog(this, Constants.invalidSecurityCodeErrorMessage, "Error", JOptionPane.ERROR_MESSAGE);
        else{
            DataManager.createTeam(teamName, password);
            GenAMapLoginWindow.teamComboBox.addItem(teamName);
            GenAMapLoginWindow.teamComboBox.setSelectedItem(teamName);
            JOptionPane.showMessageDialog(this, Constants.teamCreationMessage, "Information", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        }
    }    
}
