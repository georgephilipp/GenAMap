/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import javax.swing.JOptionPane;

/**
 *
 * @author Georg
 */
public class UIMessages {

    public UIMessages() {
    }
    
    public static void showInformationMessage(String info){
        JOptionPane.showMessageDialog(null, info, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void showErrorMessage(String errormsg, String caption){
        JOptionPane.showMessageDialog(null, errormsg, caption, JOptionPane.ERROR_MESSAGE);
    }
}
