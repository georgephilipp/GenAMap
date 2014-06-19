/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;

/**
 *
 * @author Georg
 */
public class Constants {
    public final static Font defaultFont = new Font("sansserif",0,12);
    public final static Font algorithmControlLabelFont = new Font("sansserif",1,16);
    public final static Font headingFont = new Font("sansserif",1,24);
    public final static Color hyperlinkColor = Color.BLUE;
    public final static int loginFrameWidth = 705;
    public final static int loginFrameHeight = 455;
    public final static int mainDialogWidth = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    public final static int mainDialogHeight = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    public final static int associationDialogWidth = 655;
    public final static int associationDialogHeight = 350;
    public final static int mainMenuBarHeight = 20;
    public final static int mainMenuBarWidth = mainDialogWidth;
    public final static int tabbedPaneWidth = 300;
    public final static int tabbedPaneHeight = 400;
    public final static int algorithmPaneHeight = 400;
    public final static int algorithmPaneWidth = 300;
    public final static int newTeamFrameWidth = 310;
    public final static int newTeamFrameHeight = 160;
    public final static int ioFilesForIOLassoFrameWidth = 370;
    public final static int ioFilesForIOLassoFrameHeight = 100;
    
    public final static String path = "";
    public final static String imagePath = "images/";
    public final static String documentPath = "docs/";
    public final static String configPath = "conf/";
    public final static String defaultConfigFile = "Config File.txt";
    public final static String algorithmFile = "algos.ser";
    public final static String licenseFileName = "LICENSE.txt";
    public final static String disclaimerFileName = "DISCLAIMER.txt";
    public final static String applicationTitle = "GenAMap User Login";
    public final static String applicationIcon = path + imagePath + "GAMicon.png";
    public final static String newTeamFrameTitle = "Add New Team";
    public final static String newTeamLabelText = "Team Name";
    public final static String securityCodeLabelText = "Security Code";
    public final static String addTeamButtonText = "Add Team";
    public final static String cancelButtonText = "Cancel";
    public final static String wellcomeLabelText = "Welcome to GenAMap!";
    public final static String loginUserNameLabelText = "User Name";
    public final static String loginPasswordLabelText = "Password";
    public final static String createAccountUserNameLabelText = "User Name";
    public final static String createAccountPasswordLabelText = "Password";
    public final static String createAccountFullNameLabelText = "Full Name";
    public final static String createAccountOrganizationLabelText = "Organization";
    public final static String createAccountEmailLabelText = "Email";
    public final static String createAccountSecurityCodeLabelText = "Security Code";
    public final static String createAccountTeamLabelText = "Team";
    public final static String createAccountTermsLabelText = "<html><font color=\"#0000CF\"><u>Terms and Conditions</u></font></html>";//"Terms and Conditions";
    public final static String nonProfitOrganizationCheckBoxText = "I am from a non profit organization";
    public final static String agreeTermsCheckBoxText = "I agree to the terms and conditions";
    public final static String loginButtonText = "Login";
    public final static String newTeamButtonText = "New Team";
    public final static String createAccountButtonText = "Create Account";
    public final static String loginPanelText = "User Login";
    public final static String createAccountPanelText = "Create New Account";
    public final static String algorithmControlLabelText = "Algorithm Control Center";
    public final static String showLabelText = "Show";
    public final static String completeRadioButtonText = "Complete";
    public final static String errorRadioButtonText = "Error";
    public final static String runningRadioButtonText = "Running";
    public final static String allRadioButtonText = "All";
    public final static String markerTabText = "Markers";
    public final static String traitTabText = "Traits";
    public final static String associationTabText = "Associations";
    public final static String createTeamSecurityCode = "X76;;PP!@zxiw";
    public final static String createUserSecurityCode = "TX12IUM-5PPQ";
    public final static String teamNameExistsErrorMessage = "Specified team name already exists.\n\nPlease choose a different team name";
    public final static String invalidSecurityCodeErrorMessage = "Security code is not valid.\n\nPlease contact rcurtis@cs.cmu.edu to obtain security code";
    public final static String teamCreationMessage = "Specified team has been created successfully";
    
    public final static String projectLabelText = "Project";
    public final static String traitSetLabelText = "Trait Set";
    public final static String markerSetLabelText = "Marker Set";
    public final static String associationNameLabelText = "Name";
    public final static String createAssociationText = "Create New Association";
    public final static String loadAssociationText = "Load Association From File";
    public final static String algorithmLabelText = "Algorithm";
    public final static String networkLabelText = "Network";
    public final static String numberLabelText = "Number";
    public final static String loadAssociationInfoLabelText = "Input file must be a tab delimeted JxK matrix";
    public final static String cancelAssociationButtonText = "Cancel";
    public final static String runAssociationButtonText = "Run";
    public final static String browseAssociationButtonText = "...";
    
    public final static String inputGroupFileLabelText = "Input Group Files";
    public final static String outputGroupFileLabelText = "Output Group Files";
    public final static String inputGroupFileBrowseButtonText = "...";
    public final static String outputGroupFileBrowseButtonText = "...";
    public final static String okButtonText = "Ok";
    
    public static boolean TERMS_AND_CONDITIONS_OPENED = false;
    public static boolean DATABASE_ACTIVITY_VERIFICATION_FLAG = false;
}
