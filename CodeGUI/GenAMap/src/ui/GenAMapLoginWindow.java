/*
 * This class is used to display the login window of GenAMap and contains methods to 
 * create different UI components and display them at screen. Also the action events of these
 * components are included with in the 
 */

package ui;

import controller.Constants;
import java.awt.Container;
import realdata.BareBonesBrowserLaunch;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;
import realdata.Data1;
import realdata.DataManager;

/**
 *
 * @author Georg
 */
public class GenAMapLoginWindow extends JFrame{
    private JPanel loginPanel;
    private JPanel createAccountPanel;
    
    private JLabel wellcomeLabel;
    private JLabel loginUserNameLabel;
    private JLabel loginPasswordLabel;
    private JLabel createAccountUserNameLabel;
    private JLabel createAccountPasswordLabel;
    private JLabel createAccountFullNameLabel;
    private JLabel createAccountOrganizationLabel;
    private JLabel createAccountEmailLabel;
    private JLabel createAccountSecurityCodeLabel;
    private JLabel createAccountTeamLabel;
    private JLabel createAccountTermsLabel;
    private JLabel termsHyperlink;
    
    private JTextField loginUserNameTextField;
    private JPasswordField loginPasswordTextField;
    private JTextField createAccountUserNameTextField;
    private JPasswordField createAccountPasswordTextField;
    private JTextField createAccountFullNameTextField;
    private JTextField createAccountOrganizationTextField;
    private JTextField createAccountEmailTextField;
    private JTextField createAccountSecurityCodeTextField;
    
    public static JComboBox teamComboBox;
    
    private JCheckBox nonProfitOrganizationCheckBox;
    private JCheckBox agreeTermsCheckBox;
    
    private JButton loginButton;
    private JButton newTeamButton;
    private JButton createAccountButton;
    private JButton termsAndConditionsButton;
    
    private GenAMapMainDialog genAMapMainWindowObject;
    private String userCreationErrorMessage;
    
    //private boolean isclicked;
    public static ArrayList<String> teams;
    
    public GenAMapLoginWindow(){//Container parent, boolean modal){
        //initLookAndFeel();
        //super(null, modal);
        
        Constants.TERMS_AND_CONDITIONS_OPENED = false;
        userCreationErrorMessage = "";
       
        textFieldFactory();
        checkBoxFactory();
        labelFactory();
        comboBoxFactory();
        buttonFactory();
        panelFactory();
        //loadTeamsIntoComboBox();
        frameSettings();
        componentSettings();
        
        Data1 sets = Data1.getInstance();
        if (sets.mysqlusername != null && !sets.mysqlusername.equals("bogus"))
        {
            loginUserNameTextField.setText(sets.mysqlusername);
        }
        if (sets.mysqlpassword != null && !sets.mysqlusername.equals("bogus"))
        {
            loginPasswordTextField.setText(sets.mysqlpassword);
        }

        teams = DataManager.queryForTeams();
        for (String s : teams)
        {
            teamComboBox.addItem(s);
        }
    }
    
    private void componentSettings(){
        loginPanel.add(loginUserNameLabel, new AbsoluteConstraints(10,20,80,20));
        loginPanel.add(loginUserNameTextField, new AbsoluteConstraints(100,20,200,20));
        loginPanel.add(loginPasswordLabel, new AbsoluteConstraints(10,50,80,20));
        loginPanel.add(loginPasswordTextField, new AbsoluteConstraints(100,50,200,20));
        loginPanel.add(loginButton, new AbsoluteConstraints(220,80,80,20));
        
        createAccountPanel.add(createAccountUserNameLabel, new AbsoluteConstraints(10,20,80,20));
        createAccountPanel.add(createAccountUserNameTextField, new AbsoluteConstraints(100,20,200,20));
        createAccountPanel.add(createAccountPasswordLabel, new AbsoluteConstraints(10,50,80,20));
        createAccountPanel.add(createAccountPasswordTextField, new AbsoluteConstraints(100,50,200,20));
        createAccountPanel.add(createAccountFullNameLabel, new AbsoluteConstraints(10,80,80,20));
        createAccountPanel.add(createAccountFullNameTextField, new AbsoluteConstraints(100,80,200,20));
        createAccountPanel.add(createAccountOrganizationLabel, new AbsoluteConstraints(10,110,80,20));
        createAccountPanel.add(createAccountOrganizationTextField, new AbsoluteConstraints(100,110,200,20));
        createAccountPanel.add(createAccountEmailLabel, new AbsoluteConstraints(10,140,80,20));
        createAccountPanel.add(createAccountEmailTextField, new AbsoluteConstraints(100,140,200,20));
        createAccountPanel.add(createAccountSecurityCodeLabel, new AbsoluteConstraints(10,170,80,20));
        createAccountPanel.add(createAccountSecurityCodeTextField, new AbsoluteConstraints(100,170,100,20));
        createAccountPanel.add(createAccountTeamLabel, new AbsoluteConstraints(10,200,80,20));
        createAccountPanel.add(teamComboBox, new AbsoluteConstraints(100,200,150,20));
        createAccountPanel.add(newTeamButton, new AbsoluteConstraints(260,200,80,20));
        createAccountPanel.add(nonProfitOrganizationCheckBox, new AbsoluteConstraints(10,230,250,20));
        createAccountPanel.add(agreeTermsCheckBox, new AbsoluteConstraints(10,260,250,20));
        createAccountPanel.add(termsHyperlink, new AbsoluteConstraints(50,290,150,20));
        createAccountPanel.add(createAccountButton, new AbsoluteConstraints(240,320,100,20));
        
        this.add(wellcomeLabel, new AbsoluteConstraints(200,10,300,50));
        this.add(loginPanel, new AbsoluteConstraints(10,70,310,110));
        this.add(createAccountPanel, new AbsoluteConstraints(330,70,360,350));
    }
    
    private void frameSettings(){
        this.pack();
        this.setTitle(Constants.applicationTitle);
        this.setSize(Constants.loginFrameWidth,Constants.loginFrameHeight);
        this.setLayout(new AbsoluteLayout());
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.getRootPane().setDefaultButton(loginButton);
        this.setIconImage(new ImageIcon(Constants.applicationIcon).getImage());
        this.validate();
        this.setVisible(true);
    }
    
    private void labelFactory(){
        wellcomeLabel = new JLabel(Constants.wellcomeLabelText);
        wellcomeLabel.setFont(Constants.headingFont);
        
        loginUserNameLabel = new JLabel(Constants.loginUserNameLabelText);
        loginUserNameLabel.setFont(Constants.defaultFont);
        
        loginPasswordLabel = new JLabel(Constants.loginPasswordLabelText);
        loginPasswordLabel.setFont(Constants.defaultFont);
        
        createAccountUserNameLabel = new JLabel(Constants.createAccountUserNameLabelText);
        createAccountUserNameLabel.setFont(Constants.defaultFont);
        
        createAccountPasswordLabel = new JLabel(Constants.createAccountPasswordLabelText);
        createAccountPasswordLabel.setFont(Constants.defaultFont);
        
        createAccountFullNameLabel = new JLabel(Constants.createAccountFullNameLabelText);
        createAccountFullNameLabel.setFont(Constants.defaultFont);
        
        createAccountOrganizationLabel = new JLabel(Constants.createAccountOrganizationLabelText);
        createAccountOrganizationLabel.setFont(Constants.defaultFont);
        
        createAccountEmailLabel = new JLabel(Constants.createAccountEmailLabelText);
        createAccountEmailLabel.setFont(Constants.defaultFont);
        
        createAccountSecurityCodeLabel = new JLabel(Constants.createAccountSecurityCodeLabelText);
        createAccountSecurityCodeLabel.setFont(Constants.defaultFont);
        
        createAccountTeamLabel = new JLabel(Constants.createAccountTeamLabelText);
        createAccountTeamLabel.setFont(Constants.defaultFont);
        
        termsHyperlink = new JLabel();
        termsHyperlink.setFont(new java.awt.Font("Tahoma", 0, 12));
        termsHyperlink.setForeground(new java.awt.Color(0, 0, 255));
        termsHyperlink.setLabelFor(agreeTermsCheckBox);
        termsHyperlink.setText("Terms and Conditions");
        termsHyperlink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                termsHyperLinkMouseClicked(evt);
            }
        });
    }
    
    private void textFieldFactory(){
        loginUserNameTextField = new JTextField();
        loginUserNameTextField.setEnabled(true);
        loginUserNameTextField.setFont(Constants.defaultFont);
        loginUserNameTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(loginUserNameTextField.getText().length() >0 && loginPasswordTextField.getPassword().toString().length() >0)
                    loginButton.setEnabled(true);
                else
                    loginButton.setEnabled(false);
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        
        loginPasswordTextField = new JPasswordField();
        loginPasswordTextField.setEnabled(true);
        loginPasswordTextField.setFont(Constants.defaultFont);
        loginUserNameTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(loginUserNameTextField.getText().length() > 0 && loginPasswordTextField.getPassword().toString().length() >0)
                    loginButton.setEnabled(true);
                else
                    loginButton.setEnabled(false);
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        
        createAccountUserNameTextField = new JTextField();
        createAccountUserNameTextField.setEnabled(true);
        createAccountUserNameTextField.setFont(Constants.defaultFont);
        createAccountUserNameTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createAccountUserNameTextBoxActionPerformed(e);
            }
        });
        
        createAccountPasswordTextField = new JPasswordField();
        createAccountPasswordTextField.setEnabled(true);
        createAccountPasswordTextField.setFont(Constants.defaultFont);
        createAccountPasswordTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        
        createAccountFullNameTextField = new JTextField();
        createAccountFullNameTextField.setEnabled(true);
        createAccountFullNameTextField.setFont(Constants.defaultFont);
        createAccountFullNameTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        
        createAccountOrganizationTextField = new JTextField();
        createAccountOrganizationTextField.setEnabled(true);
        createAccountOrganizationTextField.setFont(Constants.defaultFont);
        createAccountOrganizationTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        
        createAccountEmailTextField = new JTextField();
        createAccountEmailTextField.setEnabled(true);
        createAccountEmailTextField.setFont(Constants.defaultFont);
        createAccountEmailTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        
        createAccountSecurityCodeTextField = new JTextField();
        createAccountSecurityCodeTextField.setEnabled(true);
        createAccountSecurityCodeTextField.setFont(Constants.defaultFont);
        createAccountSecurityCodeTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }
    
    private void comboBoxFactory(){
        teamComboBox = new JComboBox();
    }
    
    private void checkBoxFactory(){
        nonProfitOrganizationCheckBox = new JCheckBox(Constants.nonProfitOrganizationCheckBoxText);
        nonProfitOrganizationCheckBox.setFont(Constants.defaultFont);
        nonProfitOrganizationCheckBox.setSelected(false);
        nonProfitOrganizationCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });        
        
        agreeTermsCheckBox = new JCheckBox(Constants.agreeTermsCheckBoxText);
        agreeTermsCheckBox.setFont(Constants.defaultFont);
        agreeTermsCheckBox.setSelected(false);
        agreeTermsCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });        
    }
    
    private void panelFactory(){
        loginPanel = new JPanel();        
        loginPanel.setEnabled(true);
        loginPanel.setVisible(true);
        loginPanel.setLayout(new AbsoluteLayout());
        loginPanel.setBorder(BorderFactory.createTitledBorder(Constants.loginPanelText));     
        
        createAccountPanel = new JPanel();        
        createAccountPanel.setEnabled(true);
        createAccountPanel.setVisible(true);
        createAccountPanel.setLayout(new AbsoluteLayout());
        createAccountPanel.setBorder(BorderFactory.createTitledBorder(Constants.createAccountPanelText));
    }
    
    private void buttonFactory(){
        loginButton = new JButton(Constants.loginButtonText);
        loginButton.setBorder(BorderFactory.createRaisedBevelBorder());
        loginButton.setEnabled(false);
        loginButton.addActionListener(new ActionListener() {
        @Override
            public void actionPerformed(ActionEvent e) {
                loginButtonActionPerformed(e);
            }
        });
        
        newTeamButton = new JButton(Constants.newTeamButtonText);
        newTeamButton.setBorder(BorderFactory.createRaisedBevelBorder());
        newTeamButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddNewTeamFrame addNewTeamFrameObject = new AddNewTeamFrame();
            }
        });
        
        createAccountButton = new JButton(Constants.createAccountButtonText);
        createAccountButton.setBorder(BorderFactory.createRaisedBevelBorder());
        createAccountButton.setEnabled(true);
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createUserAccount();
            }
        });
        
        termsAndConditionsButton = new JButton(Constants.createAccountTermsLabelText);
        termsAndConditionsButton.setBorder(BorderFactory.createEmptyBorder());
        termsAndConditionsButton.setContentAreaFilled(false);
        termsAndConditionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BareBonesBrowserLaunch.openURL(Constants.documentPath + Constants.licenseFileName);
                BareBonesBrowserLaunch.openURL(Constants.documentPath + Constants.disclaimerFileName);
                Constants.TERMS_AND_CONDITIONS_OPENED = true;
            }
            
        });
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /*try{
           UIManager.setLookAndFeel("de.javasoft.plaf.synthetica.SyntheticaBlackMoonLookAndFeel");
        } 
        catch (Exception e){
            e.printStackTrace();
        }*/
        
        datamodel.Model.isNewCode = true;
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GenAMapLoginWindow();               
            }
	});
    }
    
    /*public void loadTeamsIntoComboBox(){
        ArrayList<String> allTeams = new ArrayList<String>();
        allTeams = DatabaseHandler.getTeams();
        for(int counter = 0; counter < allTeams.size(); counter++)
            teamComboBox.addItem(allTeams.get(counter));
    }*/
    
    private boolean validateUserName(){
        if(createAccountUserNameTextField.getText().contains(" ") || createAccountUserNameTextField.getText().length() <3)
            return false;
            
        return true;
    }
    
    private void createUserAccount(){
        userCreationErrorMessage = "";
        String userName = createAccountUserNameTextField.getText();
        String organization = createAccountOrganizationTextField.getText();
        String email = createAccountEmailTextField.getText();
        String name = createAccountFullNameTextField.getText();
        String team = teamComboBox.getSelectedItem().toString();
        String password = "";
        for(int counter=0; counter<createAccountPasswordTextField.getPassword().length; counter++)
            password += createAccountPasswordTextField.getPassword()[counter];
        
        if(validateUserName() == false)
            userCreationErrorMessage += "Username must be atleast 3 characters long\n";
        else if(createAccountPasswordTextField.getPassword().length < 5)
            userCreationErrorMessage += "Password length must be greater than 5\n";
        else if(createAccountFullNameTextField.getText().trim().length() == 0)
            userCreationErrorMessage += "Please provide your full name\n";
        else if(createAccountOrganizationTextField.getText().trim().length() == 0)
            userCreationErrorMessage += "Please provide an organization name\n";
        else if(createAccountEmailTextField.getText().trim().length() == 0)
            userCreationErrorMessage += "Please provide your Email address\n";
        else if(verifyEmailAddress(createAccountEmailTextField.getText()) == false)
            userCreationErrorMessage += "Please provide a valid email address\n";
        else if(!(createAccountSecurityCodeTextField.getText().equals("TX12IUM-5PPQ")))
            userCreationErrorMessage += "Please provide a valid security code\n";
        else if(!(teamComboBox.getSelectedItem().toString().length() >0))
            userCreationErrorMessage += "Please select a team or create one\n";
        else if(nonProfitOrganizationCheckBox.isSelected() == false)
            userCreationErrorMessage += "To create an account, you must belong to a non profit organization\n";
        else if(agreeTermsCheckBox.isSelected() == false)
            userCreationErrorMessage += "To create an account, You must agree with our terms and conditions\n";
        else if(Constants.TERMS_AND_CONDITIONS_OPENED == false)
            userCreationErrorMessage += "Please open and read terms and conditions\n";
        else if(isUserNameInUse(createAccountUserNameTextField.getText()))
            userCreationErrorMessage += "Username already exists. Please choose some other username\n";
            
        if(!(userCreationErrorMessage.equals("")))
            UIMessages.showErrorMessage(userCreationErrorMessage, "Error");
        else{
            boolean status = DataManager.createUser(userName,password,organization,email,name,team);
            if(status == true){
                UIMessages.showInformationMessage("User has been created successfully");
                resetLoginWindow();
            }
            else
                UIMessages.showErrorMessage("Unknown error has occured while creating user", "Error");
        }
    }
    
    private boolean verifyEmailAddress(String emailAddress){
        String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        Boolean isValid = emailAddress.matches(EMAIL_REGEX);
        
        return isValid;
    }
    
    /*
    public void initLookAndFeel()
    {
	UIManager.removeAuxiliaryLookAndFeel(UIManager.getLookAndFeel());
	try
	{
            String key = new String(new byte[] { 
            67, 49, 52, 49, 48, 50, 57, 52, 45, 54, 49, 66, 54, 52, 65, 65,
            67, 45, 52, 66, 55, 68, 51, 48, 51, 57, 45, 56, 51, 52, 65,
            56, 50, 65, 49, 45, 51, 55, 69, 53, 68, 54, 57, 53 
            },
            "UTF-8");
            if(key != null)
            {
                String[] license = {
                "Licensee=AppWork UG",
                "LicenseRegistrationNumber=289416475",
                "Product=Synthetica",
                "LicenseType=Small Business License",
                "ExpireDate=--.--.----", "MaxVersion=2.999.999" };
                UIManager.put("Synthetica.license.info", license);
                UIManager.put("Synthetica.license.key", key);
            }
            UIManager.setLookAndFeel(new SyntheticaBlackMoonLookAndFeel());
        } 
	catch (UnsupportedLookAndFeelException e)
	{
            e.printStackTrace();
	} 
        catch(ParseException e){
            e.printStackTrace();
        }
	catch (UnsupportedEncodingException e)
	{
            e.printStackTrace();
	}
    }*/
    
    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {                                      
        boolean success = true;
        Data1 sets = Data1.getInstance();
        sets.mysqlusername = this.loginUserNameTextField.getText();
        sets.setWebsiteAddress("cogito.ml.cmu.edu");
        sets.mysqlpassword = "";

        for (int i = 0; i < this.loginPasswordTextField.getPassword().length; i++)
        {
            sets.mysqlpassword += this.loginPasswordTextField.getPassword()[i];
        }

        if (success)
        {
            this.resetLoginWindow();
            this.setVisible(false);
            GenAMapMainDialog mainDialogObject = new GenAMapMainDialog();
        }
    }      
    
    /**
     * Checks the user name length
     * @param evt
     */
    private void createAccountUserNameTextBoxActionPerformed(java.awt.event.ActionEvent evt)                                                   
    {                                                       
        String s = createAccountUserNameTextField.getText();
        if (s.length() > 7)
        {
            createAccountUserNameTextField.setText(s.substring(0, 7));
        }
    }                                                   

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
    
    private void termsHyperLinkMouseClicked(java.awt.event.MouseEvent evt)                                            
    {                                                
        BareBonesBrowserLaunch.openURL("LICENSE.txt");
        BareBonesBrowserLaunch.openURL("DISCLAIMER.txt");
        Constants.TERMS_AND_CONDITIONS_OPENED = true;
    }    
    
    
    private void resetLoginWindow(){
        loginUserNameTextField.setText("");
        loginPasswordTextField.setText("");
        createAccountUserNameTextField.setText("");
        createAccountPasswordTextField.setText("");
        createAccountFullNameTextField.setText("");
        createAccountOrganizationTextField.setText("");
        createAccountEmailTextField.setText("");
        createAccountSecurityCodeTextField.setText("");
        teamComboBox.setSelectedIndex(0);
        nonProfitOrganizationCheckBox.setSelected(false);
        agreeTermsCheckBox.setSelected(false);
        Constants.TERMS_AND_CONDITIONS_OPENED = false;
    }
}
