/*
 * DistributionViewer.java
 *
 * Created on Apr 29, 2011, 2:41:50 PM
 */
package views.snp;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.apache.commons.math.stat.inference.TTest;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.apache.commons.math.stat.inference.TTestImpl;

/**
 * Holds the Frequency table for viewing ... controls the general UI look and feel.
 * @author rcurtis
 */
public class DistributionViewer extends javax.swing.JFrame
{
    /**
     * Data object that holds all the tables of values for the total
     * and each population.
     */
    private ArrayList<ArrayList<ArrayList<Double>>> vals;
    /**
     * The total number of populations in this dataset.
     */
    private int numPops;
    /**
     * The current population that we are looking at. 
     */
    private int curPop;
    /**
     * The number of individuals with each genotype per population
     */
    private ArrayList<double[]> counts;

    public void setVisible()
    {
        this.setVisible(true);
    }

    /** Creates new form FreqTableViewer */
    public DistributionViewer(ArrayList<ArrayList<ArrayList<Double>>> values, ArrayList<double[]> means,
            ArrayList<double[]> counts, String traitname)
    {
        initComponents();
        this.vals = values;

        /*ArrayList<ArrayList<Double>> all = new ArrayList<ArrayList<Double>>();

        for(int j = 0; j < 3; j ++)
        {
        all.add(new ArrayList<Double>());
        }

        for(int i = 0; i < values.size(); i ++)
        {
        for(int j = 0; j < values.get(i).size(); j ++)
        {
        ArrayList<Double> list = values.get(i).get(j);
        for(Double d : list)
        {
        all.get(j).add(d);
        }
        }
        }

        values.add(0, all);*/

        this.curPop = 0;
        this.numPops = values.size() - 1;
        this.counts = counts;
        setupUI(0);
        if (counts.size() == 1)
        {
            lButton.setEnabled(false);
            rButton.setEnabled(false);
        }
        this.setTitle(traitname);
    }

    private void setupUI(int pop)
    {
        double max = -9e99;
        double min = 9e99;

        for (ArrayList<Double> list : vals.get(pop))
        {
            for (double d : list)
            {
                if (d < min)
                {
                    min = d;
                }
                if (d > max)
                {
                    max = d;
                }
            }
        }

        jPanel1.removeAll();
        ArrayList<double[]> samples = new ArrayList<double[]>();

        for (int C = 0; C < 3; C++)
        {
            HistogramDataset dataset = new HistogramDataset();
            dataset.setType(HistogramType.FREQUENCY);
            double[] arr = new double[vals.get(pop).get(C).size()];
            samples.add(arr);
            double sum = 0.0;
            for (int i = 0; i < arr.length; i++)
            {
                arr[i] = vals.get(pop).get(C).get(i);
                sum += arr[i];
            }
            Double mean = sum / arr.length;
            sum = 0;
            for (int i = 0; i < arr.length; i++)
            {
                sum += Math.pow(arr[i] - mean, 2.0);
            }
            sum = sum / (arr.length - 1);
            Double sigma = Math.sqrt(sum);
            Double se = sigma / Math.sqrt(arr.length);
            mean = ((double) Math.round(mean * 100) / 100.0);
            se = ((double) Math.round(se * 100) / 100.0);
            sigma = ((double) Math.round(sigma * 100) / 100.0);
            dataset.addSeries("Histogram", (double[]) arr, 50, min, max);
            String plotTitle = "" + C + " minor alleles";
            String xaxis = "mu= " + mean + ", sigma= " + sigma + ", se= " + se;
            String yaxis = "# individuals = " + counts.get(pop)[C];
            PlotOrientation orientation = PlotOrientation.VERTICAL;
            boolean show = false;
            boolean toolTips = true;
            boolean urls = false;
            JFreeChart chart = ChartFactory.createHistogram(plotTitle, xaxis, yaxis, dataset, orientation, show, toolTips, urls);

            XYPlot plot = (XYPlot) chart.getPlot();
            plot.setBackgroundPaint(Color.WHITE);
            XYBarRenderer bar_renderer = (XYBarRenderer) plot.getRenderer();
            bar_renderer.setBaseFillPaint(Color.BLACK);
            bar_renderer.setSeriesPaint(0, Color.BLUE);
            bar_renderer.setShadowVisible(false);

            int width = 300;
            int height = 300;

            BufferedImage image = chart.createBufferedImage(width, height);
            JLabel lblChart = new JLabel();
            lblChart.setIcon(new ImageIcon(image));
            jPanel1.add(lblChart);

            this.jPanel1.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = C;
            c.gridwidth = 4;
            c.gridy = 0;
            jPanel1.add(lblChart, c);
        }

        TTest t = new TTestImpl();
        try
        {
            double t1 = 1.0;
            double t2 = 1.0;
            double t3 = 1.0;
            try
            {
                t1 = t.tTest(samples.get(0), samples.get(1));
            }
            catch (Exception e)
            {
            }
            try
            {
                t2 = t.tTest(samples.get(0), samples.get(2));
            }
            catch (Exception e)
            {
            }
            try
            {
                t3 = t.tTest(samples.get(1), samples.get(2));
            }
            catch (Exception e)
            {
            }
            jLabel2.setText("t-test scores: 0v1=" + t1 + "; 0v2=" + t2 + "; 1v2=" + t3);
        }
        catch (IllegalArgumentException ex)
        {
        }

        jPanel1.updateUI();
        jPanel1.repaint();
        this.repaint();
    }

    private double[][] calcExpectedTable(double[][] table)
    {
        double total = 0.0;
        for (int i = 0; i < table.length; i++)
        {
            for (int j = 0; j < table[0].length; j++)
            {
                total += table[i][j];
            }
        }
        double[][] expected = new double[3][2];
        double perCase = 0.0;
        for (int i = 0; i < 3; i++)
        {
            perCase += table[i][0];
        }
        perCase /= total;
        double perCntr = 1 - perCase;
        for (int i = 0; i < 3; i++)
        {
            double perGen = table[i][0] + table[i][1];
            perGen /= total;
            expected[i][0] = perGen * perCase * total;
            expected[i][1] = perGen * perCntr * total;
        }
        return expected;
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
        jLabel1 = new javax.swing.JLabel();
        lButton = new javax.swing.JButton();
        rButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 894, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 304, Short.MAX_VALUE)
        );

        jLabel1.setText("ALL");

        lButton.setText("<");
        lButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lButtonActionPerformed(evt);
            }
        });

        rButton.setText(">");
        rButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("\"\"");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(124, 124, 124)
                        .addComponent(lButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rButton)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lButton)
                    .addComponent(jLabel1)
                    .addComponent(rButton)
                    .addComponent(jLabel2)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_lButtonActionPerformed
    {//GEN-HEADEREND:event_lButtonActionPerformed
        curPop--;
        if (curPop < 0)
        {
            curPop = numPops;
        }
        setupUI(curPop);//tables.get(curPop), chiSQ.get(curPop), pval.get(curPop));
        this.jLabel1.setText(curPop == 0 ? "ALL" : curPop + "");
    }//GEN-LAST:event_lButtonActionPerformed

    private void rButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_rButtonActionPerformed
    {//GEN-HEADEREND:event_rButtonActionPerformed
        curPop++;
        if (curPop > numPops)
        {
            curPop = 0;
        }
        setupUI(curPop);//tables.get(curPop), chiSQ.get(curPop), pval.get(curPop));
        this.jLabel1.setText(curPop == 0 ? "ALL" : curPop + "");

    }//GEN-LAST:event_rButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                new DistributionViewer(null, null, null, null).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton lButton;
    private javax.swing.JButton rButton;
    // End of variables declaration//GEN-END:variables
}
