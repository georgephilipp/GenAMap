package control;

import BiNGO.BiNGOresults2GenAMap;
import datamodel.Trait;
import datamodel.TraitSet;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.JFrame;

/**
 * The GOFrame holds BiNGO's dialog and presents it to the user.
 * @author ARCurtis
 */
public class GOFrame extends javax.swing.JDialog
{
    /**
     * Pointer to the trait set that we are working with. 
     */
    private TraitSet ts;

    /** Creates new form GOFrame */
    public GOFrame(HashSet reference, HashSet selected, String species, TraitSet ts)
    {
        super(new JFrame(), true);
        initComponents();
        this.ts = ts;
        this.settingsPanel1.init(reference, selected, this, species);
    }

    /**
     * Automatically set the algorithms that will be used in the GO analysis
     * @param alpha
     * @param correction
     * @param algo
     */
    public void setParameters(String alpha, String correction, String algo, String ontology)
    {
        this.settingsPanel1.setRunParms(alpha, correction, algo, ontology);
    }

    /**
     * Automatically start the analysis so that it performs without the GUI
     * ever showing. This method should not have to reload the annotation
     * each time!!
     */
    public void autoPerformAnalysis(HashSet selected)
    {
        settingsPanel1.setNewVals(selected);
        settingsPanel1.directlyCalculateTheResults();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        settingsPanel1 = new BiNGO.SettingsPanel();

        javax.swing.GroupLayout settingsPanel1Layout = new javax.swing.GroupLayout(settingsPanel1);
        settingsPanel1.setLayout(settingsPanel1Layout);
        settingsPanel1Layout.setHorizontalGroup(
            settingsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 556, Short.MAX_VALUE)
        );
        settingsPanel1Layout.setVerticalGroup(
            settingsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 654, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(settingsPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(settingsPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                new GOFrame(null, null, null, null).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private BiNGO.SettingsPanel settingsPanel1;
    // End of variables declaration//GEN-END:variables

    /**
     * This method is going to check out the results to make sure that they are valid -
     * that is, that they finished. It will check the db for go Annotations.
     * And, it return the results that will be used.
     */
    public BiNGOresults2GenAMap getResults()
    {
        BiNGOresults2GenAMap result = settingsPanel1.getResults();
        if (result != null)
        {
            ArrayList<String> annocodes = ts.getGoAnnos();

            boolean alreadyAnnotated = false;
            String code = result.getAnnoCode();

            for (String s : annocodes)
            {
                if (s.equals(code))
                {
                    alreadyAnnotated = true;
                }
            }

            if (!alreadyAnnotated)
            {
                ArrayList<Trait> traits = ts.getTraits();

                for (Trait t : traits)
                {
                    ArrayList<String> gocats = result.getAnnotations(t.getName());
                    if (gocats != null)
                    {
                        t.goUpdate(gocats);
                    }
                }
                ts.addAnnotation(code);
            }
        }

        return result;
    }

    /**
     * Shows a BiNGO form, but doesn't allow BiNGO to actually start. 
     */
    public void getAndSetSettings()
    {
        this.settingsPanel1.setNoRunBit(true);
        this.setVisible(true);
        this.settingsPanel1.setNoRunBit(false);
    }
}
