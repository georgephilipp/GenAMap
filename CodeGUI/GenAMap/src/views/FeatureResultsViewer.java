/*
 * GoResultsViewer.java
 *
 * Created on Mar 23, 2011, 8:41:50 AM
 */
package views;

import BiNGO.GoItems;
import datamodel.Marker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;
import views.network.GoListener;

/**
 * This class acts in parallel to the network display and to the tree display.
 * It displays the results from the GO analysis. In some cases, users can use it
 * to save a subset of nodes ... Title should be the name of the subset, the
 * traittree node, or "unnamed subset". 
 * @author rcurtis
 */
public class FeatureResultsViewer extends javax.swing.JFrame
{
    /**
     * There is
     *  only one instance of this class. It can be put up as visible or 
     * not coordinated with updating the table
     */
    private static FeatureResultsViewer instance;

    /**
     * Returns the running instance of this class to set visible, hide, or
     * whatever.
     * @return
     */
    public static FeatureResultsViewer getInstance()
    {
        if (instance == null)
        {
            instance = new FeatureResultsViewer(FeatInformationTable.getInstance());
        }
        return instance;
    }

    /**
     * Makes the go information panel visible to the user so they can explore
     * go enrichments
     * @param b
     * @param title the title of the panel
     * @param items the go items to display in the panel
     * @param gocats the go categories
     * @param goCatColorMap the color map of the go categories
     * @param listener the listener that will be notified when the table selections change
     * @param type the type of go display that this is.
     */
    public void setVisible(boolean b, String title, ArrayList<Marker> items, GoListener listener, int type)
    {
        FeatInformationTable.getInstance().setup(items, listener, type);
        this.setTitle(title);
        if(title.equals("Unnamed subset"))
            this.jPanel1.add(jButton1, BorderLayout.SOUTH);
        this.setVisible(b);
    }

    /** Creates new form GoResultsViewer */
    private FeatureResultsViewer(FeatInformationTable table)
    {
        initComponents();
        this.setAlwaysOnTop(true);

        if (table != null)
        {
            this.jPanel1.remove(this.jButton1);
            this.jPanel1.setLayout(new BorderLayout());
            this.jPanel1.add(table, BorderLayout.CENTER);
            this.jPanel1.updateUI();
            this.repaint();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();

        jButton1.setText("Save as Subset");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(584, Short.MAX_VALUE)
                .addComponent(jButton1))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(130, Short.MAX_VALUE)
                .addComponent(jButton1))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
