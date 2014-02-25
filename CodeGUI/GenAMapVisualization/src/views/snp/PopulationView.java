package views.snp;

import datamodel.Model;
import java.awt.Font;
import datamodel.Population;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

/**
 * The population view is the view screen that will show the populationview
 * in two different layouts - the eigen layout and the pie chart layout.
 *
 * These are both done through the JFreeChart library. 
 * @author akgoyal
 */
public class PopulationView
{
    /**
     * The label that is the chart that is shown
     */
    private JLabel lblChart;
    /**
     * Pointer to the population object.
     */
    private Population popu;
    /**
     * The AssociationView to draw on.
     */
    private JPanel jp;
    /**
     * The jpanel with the controls
     */
    private JPanel labels;
    /**
     * The JPanel that holds the labels and the chart.
     */
    private JPanel contentPanel;
    /**
     * The factor to scale the population view to.
     */
    private double factor;
    /**
     * The color of the AssociationView objects.
     */
    private static final Color BACKCOLOR = new Color(246, 248, 254);
    /**
     * The combo box for the x axis
     */
    final JComboBox chroms = new JComboBox();
    /**
     * The combo box for the y axis
     */
    final JComboBox chroms1 = new JComboBox();
    /**
     * The combo box for selecting the number of pops
     */
    final JComboBox numPopsComboBox = new JComboBox();
    /**
     * The combo box for the type of graph
     */
    final JComboBox chroms3 = new JComboBox();

    public PopulationView()
    {
        for (int i = 1; i <= 5 && chroms.getItemCount() < 5; i++)
        {
            chroms.addItem(new Integer(i));
        }
        chroms.setSelectedItem(1);
        chroms.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                drawpopulationData((int) Integer.parseInt(chroms.getSelectedItem().toString()), (int) Integer.parseInt(chroms1.getSelectedItem().toString()), (int) Integer.parseInt(numPopsComboBox.getSelectedItem().toString()), chroms3.getSelectedItem().toString());
            }
        });

        chroms1.setBackground(BACKCOLOR);
        chroms1.setFont(new Font("Tahoma", Font.PLAIN, 11));
        for (int i = 1; i <= 5 && chroms1.getItemCount() < 5; i++)
        {
            chroms1.addItem(new Integer(i));
        }
        chroms1.setSelectedItem(2);
        chroms1.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {

                drawpopulationData((int) Integer.parseInt(chroms.getSelectedItem().toString()), (int) Integer.parseInt(chroms1.getSelectedItem().toString()), (int) Integer.parseInt(numPopsComboBox.getSelectedItem().toString()), chroms3.getSelectedItem().toString());
            }
        });




        numPopsComboBox.setBackground(BACKCOLOR);
        numPopsComboBox.setFont(new Font("Tahoma", Font.PLAIN, 11));
        //if (this.popu.isStructureGenerated())
        {
            for (int i = 2; i <= 10 && numPopsComboBox.getItemCount() < 9; i++)
            {

                numPopsComboBox.addItem(new Integer(i));
            }
        }
        //else
        //{
        //    chroms2.addItem("1");
        //
        //}
        numPopsComboBox.setSelectedItem(2);
        numPopsComboBox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (numPopsComboBox.getSelectedItem() != null)
                {
                    drawpopulationData((int) Integer.parseInt(chroms.getSelectedItem().toString()), (int) Integer.parseInt(chroms1.getSelectedItem().toString()), (int) Integer.parseInt(numPopsComboBox.getSelectedItem().toString()), chroms3.getSelectedItem().toString());
                }
            }
        });

        chroms3.setBackground(BACKCOLOR);
        chroms3.setFont(new Font("Tahoma", Font.PLAIN, 11));
        if (chroms3.getItemCount() < 2)
        {
            chroms3.addItem("Pie");
            chroms3.addItem("Scatter");
            chroms3.setSelectedIndex(1);
        }

        if (chroms3.getSelectedIndex() < 0)
        {
        }

        chroms3.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (chroms3.getSelectedItem().toString().compareToIgnoreCase("Pie") == 0)
                {
                    chroms1.disable();
                    chroms.disable();
                }
                else if (popu.isEigenGenerated())
                {
                    chroms1.enable();
                    chroms.enable();
                }
                drawpopulationData((int) Integer.parseInt(chroms.getSelectedItem().toString()), (int) Integer.parseInt(chroms1.getSelectedItem().toString()), (int) Integer.parseInt(numPopsComboBox.getSelectedItem().toString()), chroms3.getSelectedItem().toString());
            }
        });

    }

    /**
     * Removes the content from the jpanel view. 
     */
    public void removeFromCanvas()
    {
        for (int i = 0; jp != null && i < jp.getComponentCount(); i++)
        {
            if (jp.getComponent(i) == contentPanel)
            {
                this.jp.remove(contentPanel);
            }
        }
    }

    /**
     * Draws the population and passes it into the jpanel.
     * @param jp the jpanel to draw on
     * @param p the population to draw
     * @param layoutLoc where on the jpanel to draw.
     */
    public void drawOnCanvas(JPanel jp, Population p, String layoutLoc, double factor,
            int popNo)
    {
        this.popu = p;
        this.jp = jp;
        this.factor = factor;

        if (contentPanel != null)
        {
            jp.remove(contentPanel);
        }
        setupContentPanel_pop();

        if (this.popu.isStructureGenerated())
        {
            drawpopulationData(1, 2, 2, "Scatter");
        }
        else
        {
            drawpopulationData(1, 2, 1, "Scatter");
        }

        if (!p.isStructureGenerated())
        {
            this.numPopsComboBox.setSelectedIndex(popNo - 2);
            this.numPopsComboBox.setEnabled(false);
        }
        else
        {
            this.numPopsComboBox.setSelectedIndex(0);
            this.numPopsComboBox.setEnabled(true);
        }
        jp.add(contentPanel, layoutLoc);
    }

    public void setFactor(int factor)
    {
        this.factor = factor;
    }

    /**
     * Sets up the content panel for the population view. 
     */
    private void setupContentPanel_pop()
    {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKCOLOR);
        labels = addLabelsForPopulation();
        contentPanel.add(labels, BorderLayout.NORTH);
        contentPanel.addPropertyChangeListener("Size", null);
    }

    /**
     * Draws the population view in teh specified format. Called each
     * time we want to draw the population. 
     * @param eig1 The eigen value on the x axis
     * @param eig2 The eigen value on the y axis
     * @param pop_no The number of populations to draw
     * @param type The type of drawing to create.
     */
    protected void drawpopulationData(int eig1, int eig2, int pop_no, String type)
    {
        int pop_no_new = 0;
        if (this.popu.isStructureGenerated() == true)
        {
            pop_no_new = pop_no;
            if (pop_no_new == 1) //to account for switching.
            {
                pop_no_new = 2;
                pop_no = 2;
            }
        }
        else
        {
            pop_no_new = this.popu.getTotPopIfUserGen();
            pop_no = 1;
        }

        boolean isPie = true;
        if (this.chroms3.getSelectedIndex() == 1)
        {
            try
            {
                ArrayList<HashMap<String, String>> ret = this.popu.getScatterData(eig1, eig2, pop_no);
                XYSeriesCollection dataset = new XYSeriesCollection();

                for (int p = 0; p < pop_no_new; p++)
                {
                    XYSeries series1 = new XYSeries(Integer.toString(p + 1));
                    for (HashMap<String, String> netEdge : ret)
                    {
                        int popno_v = Integer.parseInt(netEdge.get("pop" + pop_no));
                        if (popno_v == (p + 1))
                        {
                            double eig1_v = Double.parseDouble(netEdge.get("eig" + eig1));
                            double eig2_v = Double.parseDouble(netEdge.get("eig" + eig2));
                            series1.add(eig1_v, eig2_v);
                        }
                    }
                    dataset.addSeries(series1);
                }
                JFreeChart chart = ChartFactory.createScatterPlot("Populations by Eigen Values", "eigen value " + eig1, "eigen value " + eig2, dataset, PlotOrientation.VERTICAL, true, true, false);
                chart.setBackgroundPaint(BACKCOLOR);

                Color[] colors = Model.colors;
                XYPlot plot = (XYPlot) chart.getPlot();
                plot.setBackgroundPaint(new Color(245,255,245));
                XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
                for (int i = 0; i < dataset.getSeriesCount(); i++)
                {
                    renderer.setSeriesPaint(i, colors[i]);
                }
                plot.setRenderer(renderer);

                this.chroms3.setSelectedIndex(1);
                BufferedImage image = chart.createBufferedImage(jp.getWidth() - 20,(int) ((jp.getHeight() - 210) / factor));
                JPanel pop = new JPanel();
                pop.setLayout(new BorderLayout());
                pop.setBackground(BACKCOLOR);
                lblChart = new JLabel();
                lblChart.setIcon(new ImageIcon(image));
                pop.add(lblChart);
                isPie = false;
            }
            catch (Exception e)
            {
            }
        }
        if (isPie)
        {
            int[] data = this.popu.getPieChartCounts(pop_no, pop_no_new);
            DefaultPieDataset pieDataset = new DefaultPieDataset();
            for (int i = 0; i < data.length; i++)
            {
                pieDataset.setValue(Integer.toString(i + 1), new Integer(data[i]));

            }

            JFreeChart chart = ChartFactory.createPieChart("Distribution of Populations", pieDataset, true, true, true);
            StandardPieSectionLabelGenerator labels2 = new StandardPieSectionLabelGenerator("{0} = {2}");
            ((PiePlot) chart.getPlot()).setLabelGenerator(labels2);
            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setBackgroundPaint(new Color(245,255,245));

            // Specify the colors here
            Color[] colors = Model.colors;
            ColorRenderer renderer = new ColorRenderer(colors);
            renderer.setColor(plot, pieDataset);

            chart.setBackgroundPaint(BACKCOLOR);
            BufferedImage image = chart.createBufferedImage(jp.getWidth() - 20,(int) ((jp.getHeight() - 210) / factor));
            JPanel pop = new JPanel();
            pop.setLayout(new BorderLayout());
            pop.setBackground(BACKCOLOR);
            lblChart = new JLabel();
            lblChart.setIcon(new ImageIcon(image));
            pop.add(lblChart, BorderLayout.CENTER);
        }

        contentPanel.removeAll();
        contentPanel.add(labels, BorderLayout.NORTH);
        contentPanel.add(lblChart, BorderLayout.CENTER);
        jp.updateUI();
    }

    /*
     * @author http://javabeanz.wordpress.com/2007/08/06/creating-pie-charts-using-custom-colors-jfreechart/
     * A simple renderer for setting custom colors
     * for a pie chart.
     */
    public static class ColorRenderer
    {
        private Color[] color;

        public ColorRenderer(Color[] color)
        {
            this.color = color;
        }

        public void setColor(PiePlot plot, DefaultPieDataset dataset)
        {
            List<Comparable> keys = dataset.getKeys();
            int aInt;

            for (int i = 0; i < keys.size(); i++)
            {
                aInt = i % this.color.length;
                plot.setSectionPaint(keys.get(i), this.color[aInt]);
            }
        }

        public void setColor(XYPlot plot, DefaultPieDataset dataset)
        {
            List<Comparable> keys = dataset.getKeys();
            int aInt;

            for (int i = 0; i < keys.size(); i++)
            {
                aInt = i % this.color.length;

                //plot.setRenderers(renderers)
                //plot.setSectionPaint(keys.get(i), this.color[aInt]);
            }
        }
    }

    /**
     * Adds the labels to the controllers, along with the action methods. 
     * @return
     */
    protected JPanel addLabelsForPopulation()
    {
        JPanel labels = new JPanel();
        labels.setLayout(new BorderLayout());
        labels.setBackground(BACKCOLOR);
        JPanel chr = new JPanel();
        chr.setBackground(BACKCOLOR);
        JLabel l = new JLabel("X-axis", JLabel.CENTER);
        l.setFont(new Font("Tahoma", Font.BOLD, 11));
        chr.add(l);
        chroms.enable();
        chroms1.enable();
        numPopsComboBox.enable();
        chroms3.enable();

        chroms.disable();
        chroms1.disable();
        if (this.popu.isStructureGenerated() == false)
        {
            numPopsComboBox.disable();
        }
        if (this.popu.isEigenGenerated() == false)
        {
            chroms.disable();
            chroms1.disable();
        }
        chroms.setBackground(BACKCOLOR);
        chroms.setFont(new Font("Tahoma", Font.PLAIN, 11));

        if (popu.isEigenGenerated())
        {
            chroms3.enable();
        }
        else
        {
            chroms3.disable();
        }

        chr.add(chroms);

        JLabel l1 = new JLabel("Y-axis", JLabel.CENTER);
        l1.setFont(new Font("Tahoma", Font.BOLD, 11));
        chr.add(new JLabel("    "));
        chr.add(l1);
        chr.add(chroms1);
        chr.add(new JLabel("    "));
        JLabel l2 = new JLabel("Number of Populations", JLabel.CENTER);
        l2.setFont(new Font("Tahoma", Font.BOLD, 11));
        chr.add(l2);
        chr.add(numPopsComboBox);
        chr.add(new JLabel("    "));

        JLabel l3 = new JLabel("Chart type", JLabel.CENTER);
        l3.setFont(new Font("Tahoma", Font.BOLD, 11));
        chr.add(l3);
        chr.add(chroms3);
        labels.add(chr, BorderLayout.CENTER);
        return labels;
    }

    public void resize()
    {
        if (chroms != null)
        {
            if (numPopsComboBox.getSelectedItem() == null)
            {
                numPopsComboBox.setSelectedIndex(0);
            }
            drawpopulationData((int) Integer.parseInt(chroms.getSelectedItem().toString()), (int) Integer.parseInt(chroms1.getSelectedItem().toString()), (int) Integer.parseInt(numPopsComboBox.getSelectedItem().toString()), chroms3.getSelectedItem().toString());
        }
    }
}
