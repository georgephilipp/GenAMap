/*
 * TraitImporter.java
 *
 * Created on Aug 12, 2009, 11:11:25 AM
 */
package control;

import realdata.BareBonesBrowserLaunch;
import realdata.Tester;
import realdata.DataManager;
import realdata.Data1;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * This package will interface with the user to get all the information needed
 * to import data into the model.
 *
 * It gets information that it then uses to access a script on cogito-b.
 * The script then can access the database through cogito ... and thus
 * access to the cluster is tightly controlled.
 * @author ross
 */
public class DatabaseSetup extends java.awt.Dialog
{
    private boolean isclicked = false;
    private ArrayList<String> teams;

    /** Creates new form  */
    public DatabaseSetup(java.awt.Container parent, boolean modal)
    {
        super(null, modal);
        initComponents();

        Data1 sets = Data1.getInstance();
        if (sets.mysqlusername != null && !sets.mysqlusername.equals("bogus"))
        {
            this.usernameTextBox.setText(sets.mysqlusername);
        }
        if (sets.mysqlpassword != null && !sets.mysqlusername.equals("bogus"))
        {
            this.passwordTextBox.setText(sets.mysqlpassword);
        }

        teams = DataManager.queryForTeams();
        for (String s : teams)
        {
            this.teamSelectionBox.addItem(s);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dimButtonGroup = new javax.swing.ButtonGroup();
        headButtonGroup = new javax.swing.ButtonGroup();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        usernameTextBox = new javax.swing.JTextField();
        passwordTextBox = new javax.swing.JPasswordField();
        okBtn = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        newUserNameTextBox = new javax.swing.JTextField();
        newPasswordTextBox = new javax.swing.JPasswordField();
        createAccntBtn = new javax.swing.JButton();
        termsCheckBox = new javax.swing.JCheckBox();
        termsHyperLink = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        fullNameTextBox = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        orgTextBox = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        emailTextBox = new javax.swing.JTextField();
        teamSelectionBox = new javax.swing.JComboBox();
        newTeamButton = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        errorLabel = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel11 = new javax.swing.JLabel();
        securityCodeBox = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();

        setTitle("Distributed Settings");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.green, java.awt.Color.darkGray));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24));
        jLabel1.setText("Welcome to GenAMap!");

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.blue, java.awt.Color.green, null, java.awt.Color.white), "Login", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N
        jPanel3.setFont(new java.awt.Font("Tahoma", 0, 14));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel2.setText("User Name:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel3.setText("Password:");

        usernameTextBox.setFont(new java.awt.Font("Tahoma", 0, 14));
        usernameTextBox.setToolTipText("Username that you've already created to login to GenAMap");

        passwordTextBox.setFont(new java.awt.Font("Tahoma", 0, 14));
        passwordTextBox.setToolTipText("Password");

        okBtn.setText("Login");
        okBtn.setToolTipText("Login to GenAMap using your account you've already created");
        okBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addContainerGap(228, Short.MAX_VALUE)
                        .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(passwordTextBox, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                            .addComponent(usernameTextBox, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(usernameTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(passwordTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(okBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.blue, new java.awt.Color(204, 0, 0), java.awt.Color.blue, java.awt.Color.white), "Create New Account", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N
        jPanel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel4.setText("User Name:");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel5.setText("Password:");

        newUserNameTextBox.setFont(new java.awt.Font("Tahoma", 0, 14));
        newUserNameTextBox.setToolTipText("Choose a username (8 chars)");
        newUserNameTextBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newUserNameTextBoxActionPerformed(evt);
            }
        });
        newUserNameTextBox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                newUserNameTextBoxKeyTyped(evt);
            }
        });

        newPasswordTextBox.setFont(new java.awt.Font("Tahoma", 0, 14));
        newPasswordTextBox.setToolTipText("Choose a secure password.");

        createAccntBtn.setText("Create Account");
        createAccntBtn.setToolTipText("When you're ready, create your new account!");
        createAccntBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createAccntBtnActionPerformed(evt);
            }
        });

        termsCheckBox.setFont(new java.awt.Font("Tahoma", 0, 12));
        termsCheckBox.setText("I have read and agree to the");
        termsCheckBox.setToolTipText("Please review two documents decribing the use of GenAMap by clicking on the hyperlink below.");

        termsHyperLink.setFont(new java.awt.Font("Tahoma", 0, 12));
        termsHyperLink.setForeground(new java.awt.Color(0, 0, 255));
        termsHyperLink.setLabelFor(termsCheckBox);
        termsHyperLink.setText("Terms and Conditions");
        termsHyperLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                termsHyperLinkMouseClicked(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel7.setText("Full Name:");

        fullNameTextBox.setFont(new java.awt.Font("Tahoma", 0, 14));
        fullNameTextBox.setToolTipText("Tell us your full name.");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel8.setText("Organization:");

        orgTextBox.setFont(new java.awt.Font("Tahoma", 0, 14));
        orgTextBox.setToolTipText("With what organization do you work?");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setText("email:");

        emailTextBox.setFont(new java.awt.Font("Tahoma", 0, 14));
        emailTextBox.setToolTipText("What email can we contact you with?");

        teamSelectionBox.setBackground(new java.awt.Color(51, 51, 255));
        teamSelectionBox.setFont(new java.awt.Font("Tahoma", 0, 12));
        teamSelectionBox.setForeground(new java.awt.Color(255, 255, 255));
        teamSelectionBox.setToolTipText("Choose the team that you will share data with.  A passcode is required to join a team.");

        newTeamButton.setText("New Team");
        newTeamButton.setToolTipText("Create a new team and join it.");
        newTeamButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newTeamButtonActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel10.setText("Team:");

        errorLabel.setForeground(new java.awt.Color(255, 0, 51));
        errorLabel.setText("                                                                                                                                 ");

        jCheckBox1.setText("I am from a non-profit organization");

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel11.setText("security code:");

        securityCodeBox.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        securityCodeBox.setToolTipText("Contact rcurtis@cs.cmu.edu to get a valid code.");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(teamSelectionBox, 0, 265, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(newTeamButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                    .addGap(38, 38, 38)
                                    .addComponent(termsHyperLink)
                                    .addGap(29, 29, 29))
                                .addComponent(termsCheckBox))
                            .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(createAccntBtn))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(errorLabel))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)
                            .addComponent(jLabel9)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel11)
                                .addComponent(jLabel8)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(securityCodeBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
                            .addComponent(emailTextBox, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
                            .addComponent(orgTextBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
                            .addComponent(fullNameTextBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
                            .addComponent(newPasswordTextBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
                            .addComponent(newUserNameTextBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newUserNameTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newPasswordTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fullNameTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(orgTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(emailTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(securityCodeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addGap(35, 35, 35)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(teamSelectionBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(newTeamButton))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(createAccntBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jCheckBox1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(termsCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(termsHyperLink, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(19, 19, 19)
                .addComponent(errorLabel))
        );

        securityCodeBox.getAccessibleContext().setAccessibleDescription("Contact rcurtis@cs.cmu.edu to get a valid code.");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(187, 187, 187)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    /**
     * Checks all the parameters and then logs into GenAMap using the name and pwd
     * @param evt
     */
    private void okBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okBtnActionPerformed
        boolean success = true;
        Data1 sets = Data1.getInstance();
        sets.mysqlusername = this.usernameTextBox.getText();
        sets.setWebsiteAddress("cogito.ml.cmu.edu");
        sets.mysqlpassword = "";

        for (int i = 0; i < this.passwordTextBox.getPassword().length; i++)
        {
            sets.mysqlpassword += this.passwordTextBox.getPassword()[i];
        }

        if (success)
        {
            this.closeDialog(null);
        }
    }//GEN-LAST:event_okBtnActionPerformed

    /**
     * Checks parameters and creates an account for the user.
     * @param evt
     */
    private void createAccntBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_createAccntBtnActionPerformed
    {//GEN-HEADEREND:event_createAccntBtnActionPerformed
        this.errorLabel.setText("");
        if (this.newUserNameTextBox.getText().length() < 3)
        {
            this.errorLabel.setText("Username must be at least 3 characters long!");
            return;
        }
        if (isUserNameInUse(this.newUserNameTextBox.getText()))
        {
            this.errorLabel.setText("Choose another username please");
            return;
        }
        String name = this.fullNameTextBox.getText();
        if (name.length() < 3 || name.length() > 30)
        {
            this.errorLabel.setText("Are you sure your full name is " + name + "?");
            return;
        }
        String org = this.orgTextBox.getText();
        if (org.length() < 2)
        {
            this.errorLabel.setText("Please expound on your organization's name.");
            return;
        }

        String pwd = "";
        for (int i = 0; i < this.newPasswordTextBox.getPassword().length; i++)
        {
            pwd += this.newPasswordTextBox.getPassword()[i];
        }
        if (pwd.length() < 4)
        {
            this.errorLabel.setText("You can come up with a better password than " + pwd + "!");
            return;
        }
        String email = this.emailTextBox.getText();
        if (email.indexOf("@") < 1 || (email.indexOf("@") > email.lastIndexOf(".")))
        {
            this.errorLabel.setText("Double check that email address!");
            return;
        }
        if (!isclicked)
        {
            this.errorLabel.setText("Please at least open the Terms and Conditions");
            return;
        }
        if (!this.termsCheckBox.isSelected())
        {
            this.errorLabel.setText("You must agree to the terms and conditions");
            return;
        }
        if (!this.jCheckBox1.isSelected())
        {
            this.errorLabel.setText("You must be from a non-profit organization to use this software");
            return;
        }
        String team = (String) this.teamSelectionBox.getSelectedItem();
        if (this.teamSelectionBox.isEnabled())
        {
            NewDataNameGetter ndng = new NewDataNameGetter(this, true,
                    "Please enter the passcode for " + team + ": ", new ArrayList<String>());
            ndng.setVisible(true);
            String pscd = ndng.newName;
            if (!DataManager.isPassCodeForTeam(team, pscd))
            {
                this.errorLabel.setText("You have not entered the correct team passcode!");
                return;
            }
        }

        if (!this.securityCodeBox.getText().equals("TX12IUM-5PPQ"))
        {
            this.errorLabel.setText("That is not the correct security code.");
        }

        DataManager.createUser(this.newUserNameTextBox.getText(),
                pwd, org, email, name, team);

        this.usernameTextBox.setText(this.newUserNameTextBox.getText());
        this.passwordTextBox.setText(pwd);
        okBtnActionPerformed(null);
    }//GEN-LAST:event_createAccntBtnActionPerformed

    /**
     * Shows the terms of agreement for using GenAMap.
     * @param evt
     */
    private void termsHyperLinkMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_termsHyperLinkMouseClicked
    {//GEN-HEADEREND:event_termsHyperLinkMouseClicked
        BareBonesBrowserLaunch.openURL("LICENSE.txt");
        BareBonesBrowserLaunch.openURL("DISCLAIMER.txt");
        this.isclicked = true;
    }//GEN-LAST:event_termsHyperLinkMouseClicked

    /**
     * Checks the user name length
     * @param evt
     */
    private void newUserNameTextBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_newUserNameTextBoxActionPerformed
    {//GEN-HEADEREND:event_newUserNameTextBoxActionPerformed
        String s = this.newUserNameTextBox.getText();
        if (s.length() > 7)
        {
            this.newUserNameTextBox.setText(s.substring(0, 7));
        }
    }//GEN-LAST:event_newUserNameTextBoxActionPerformed

    private void newUserNameTextBoxKeyTyped(java.awt.event.KeyEvent evt)//GEN-FIRST:event_newUserNameTextBoxKeyTyped
    {//GEN-HEADEREND:event_newUserNameTextBoxKeyTyped
        newUserNameTextBoxActionPerformed(null);
    }//GEN-LAST:event_newUserNameTextBoxKeyTyped

    /**
     * For now, just allow users to create their own teams. This could come back
     * to bite us if someone were to download the software and create a bunch of
     * teams.
     * @param evt
     */
    private void newTeamButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_newTeamButtonActionPerformed
    {//GEN-HEADEREND:event_newTeamButtonActionPerformed
        NewDataNameGetter ndng = new NewDataNameGetter(this, true, "Please enter the security code: ",
                new ArrayList<String>());
        ndng.setVisible(true);
        if (ndng.SUCCESS && Data1.getInstance().checkTeamCreationCode(ndng.newName))
        {

            ndng = new NewDataNameGetter(this, true,
                    "Please enter the name of your new team: ", this.teams);
            NewDataNameGetter ndng1 = new NewDataNameGetter(this, true,
                    "Please enter the passcode for this team: ", new ArrayList<String>());

            ndng.setVisible(true);
            if (ndng.SUCCESS)
            {
                ndng1.setVisible(true);
                if (ndng1.SUCCESS)
                {
                    String name = ndng.newName;
                    String passcode = ndng1.newName;

                    DataManager.createTeam(name, passcode);

                    this.teamSelectionBox.addItem(name);
                    this.teamSelectionBox.setSelectedItem(name);
                    this.teamSelectionBox.setEnabled(false);
                }
            }
        }
    }//GEN-LAST:event_newTeamButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                DatabaseSetup dialog = new DatabaseSetup(new java.awt.Frame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter()
                {
                    public void windowClosing(java.awt.event.WindowEvent e)
                    {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton createAccntBtn;
    private javax.swing.ButtonGroup dimButtonGroup;
    private javax.swing.JTextField emailTextBox;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JTextField fullNameTextBox;
    private javax.swing.ButtonGroup headButtonGroup;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPasswordField newPasswordTextBox;
    private javax.swing.JButton newTeamButton;
    private javax.swing.JTextField newUserNameTextBox;
    private javax.swing.JButton okBtn;
    private javax.swing.JTextField orgTextBox;
    private javax.swing.JPasswordField passwordTextBox;
    private javax.swing.JTextField securityCodeBox;
    private javax.swing.JComboBox teamSelectionBox;
    private javax.swing.JCheckBox termsCheckBox;
    private javax.swing.JLabel termsHyperLink;
    private javax.swing.JTextField usernameTextBox;
    // End of variables declaration//GEN-END:variables

    /**
     * Checks to see if the username is in use.
     * @param text
     * @return
     */
    private boolean isUserNameInUse(String text)
    {
        if (DataManager.queryForUser(text))
        {
            return true;
        }
        return false;
    }
}
