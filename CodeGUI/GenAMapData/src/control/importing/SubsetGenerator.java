/*
 * SubsetGenerator.java
 *
 * Created on June 23, 2010, 10:42:51 PM
 */

package control.importing;

import datamodel.Trait;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.SwingWorker;
import realdata.DataManager;

/**
 * And it shall come to pass that the children of men shall want to run queries
 * regarding the networks.  And it will come to pass that we shall query the db
 * to find out the degree of each node and thus create a list of the top 100
 * connected genes.
 *
 * This class is called from the subset creation algorithm dialog. This dialog
 * doesn't actually spawn algorithms on the database, but actually just runs
 * some algorithms directly in GenAMap to find different subsets. 
 *
 * @author rcurtis
 */
public class SubsetGenerator extends javax.swing.JDialog
                                    implements PropertyChangeListener
{
    private Task task;
    private int tsid;
    private ArrayList<Trait> traits;
    private String dir;
    private int netid;
    public ArrayList<Integer> toRet = new ArrayList<Integer>();

    /**
     * Create a new Subset Generator
     */
    public SubsetGenerator(JFrame parent, boolean modal, int tsid, int netid)
    {
        super(parent, modal);
        initComponents();
        this.tsid = tsid;
        this.netid = netid;
       
        this.setLocation(parent.getLocation());
        this.jButton1ActionPerformed(null);
    }


    class Task extends SwingWorker<Void, Void>
    {

        protected boolean isError;
        @Override
        protected Void doInBackground() throws Exception
        {
            isError = false;
            setProgress(0);

            ArrayList<Integer> traits = getTraits(tsid);

            try
            {
                runQueryTraitByTrait(traits);
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }

            this.setProgress(100);
            return null;
        }

      
         @Override
         public void done()
         {
            if (isError)
            {
                jButton1.setEnabled(true);
                jLabel1.setText("Error occured while creating ...");
            }
            else
            {
                jButton1.setEnabled(true);
                jLabel1.setText("Successfully created!");
            }
         }

        private void runQueryTraitByTrait(ArrayList<Integer> traits) throws IOException
        {
            HashMap<Integer, Double> weights = new HashMap<Integer, Double>();
            for(int k = 0; k < traits.size(); k ++)
            {
                int i = traits.get(k);
                ArrayList<String> cols = new ArrayList<String>();
                cols.add("trait2");
                cols.add("weight");

                ArrayList<String> where = new ArrayList<String>();
                where.add("trait1 = " + i);
                where.add("netid = " + netid);
                ArrayList<HashMap<String, String>> res = DataManager.runMultiColSelectQuery(cols, "networkval", true, where, null);

                for(HashMap<String, String> hm: res)
                {
                    int t2 = Integer.parseInt(hm.get("trait2"));
                    double w = Math.abs(Double.parseDouble(hm.get("weight")));

                    Double d2 = weights.remove(t2);
                    Double d1 = weights.remove(i);

                    if(d2 == null) d2 =0.0;
                    if(d1 == null) d1 = 0.0;

                    d2 += w;
                    d1 += w;
                    weights.put(i, d1);
                    weights.put(t2, d2);
                }

                int prog = (k +1) * 100 / traits.size();
                this.setProgress(prog);
            }

            ArrayList<Double> vals = new ArrayList<Double>();
            for(int t : traits)
            {
                boolean added = false;
                Double w = weights.get(t);

                if(w == null)
                    continue;

                int j;
                for(j = 0; j < toRet.size(); j ++)
                {
                    if(w > vals.get(j))
                    {
                        int temp = toRet.get(j);
                        double temp2 = vals.get(j);
                        toRet.set(j, t);
                        vals.set(j, w);

                        int k;
                        for(k = j + 1; k < toRet.size(); k ++)
                        {
                            int c = toRet.get(k);
                            double d = vals.get(k);
                            toRet.set(k, temp);
                            vals.set(k, temp2);
                            temp = c;
                            temp2 = d;
                        }
                        toRet.add(temp);
                        vals.add(temp2);
                        added = true;
                        break;
                    }
                }
                if(j < 100 && !added)
                {
                    toRet.add(t);
                    vals.add(w);
                }
            }
        }

        private ArrayList<Integer> getTraits(int tsid)
        {
            ArrayList<Integer> traits = new ArrayList<Integer>();
            ArrayList<String> where = new ArrayList<String>();
            where.add("traitsetid = " + tsid);
            ArrayList<String> res = DataManager.runSelectQuery("id", "trait", true, where, "id");

            for(String s:res)
            {
                traits.add(Integer.parseInt(s));
            }
            return traits;
        }
    }

    /** Creates new form NetworkUploaderHelper */
    public SubsetGenerator(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);
        initComponents();
        
    }



    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Subset generation");

        jProgressBar1.setStringPainted(true);

        jButton1.setText("Start");
        jButton1.setName("Trait Upload"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(47, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton1)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(45, 45, 45))
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(174, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (this.jButton1.getText().equals("OK"))
        {
            this.setVisible(false);
            this.dispose();
        }
        else
        {
            this.jButton1.setText("OK");
            this.jButton1.setEnabled(false);
            this.jLabel1.setText("Creating Subset...");
            task = new Task();
            task.addPropertyChangeListener(this);
            task.execute();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * Called by the background task to set the status of the subset generation. 
     */
    public void setStatus(String s, boolean isError)
    {
        this.jButton1.setText(s);
        if (isError)
        {
            this.jProgressBar1.setVisible(false);
            this.jButton1.setEnabled(true);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JProgressBar jProgressBar1;
    // End of variables declaration//GEN-END:variables

    /**
     * Called to set the status of the running algorithm. 
     * @param evt
     */
    public void propertyChange(PropertyChangeEvent evt)
    {
        try
        {
            int progress = (Integer)evt.getNewValue();
            this.jProgressBar1.setValue(progress);
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }
    }

}
