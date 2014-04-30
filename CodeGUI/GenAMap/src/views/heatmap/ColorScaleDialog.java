package views.heatmap;

import heatchart.HeatChart;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Displays an adjust color scale dialog
 * @author kellychan
 */
public class ColorScaleDialog extends java.awt.Dialog
{
    /**
     * The heat map that we are adjusting values for
     */
    private HeatMap mv;
    /**
     * The maximum threshold currently set for the z value matrix
     */
    private double threshMax;
    /**
     * The minimum threshold currently set for the z value matrix
     */
    private double threshMin;
    /**
     * TRUE if the user likes their changes. 
     */
    private boolean success;

    /**
     * Constructor - gets values from matrix view and prepares dialog
     * @param parent
     * @param matrixView
     */
    public ColorScaleDialog(JFrame parent, HeatMap matrixView)
    {
        super(parent);
        this.setLocation(parent.getLocation());
        this.mv = matrixView;
        initComponents();
        final int prec = 1000; //threshold slider resolution
        threshMax = mv.getMaxWeight();
        threshMin = 0;
        double value = mv.getMaxThreshold();
        double stepSize = (threshMax - threshMin) / 10;

        //configure max spinner and slider
        final SpinnerNumberModel maxModelSpinner = new SpinnerNumberModel(value, threshMin, threshMax, stepSize);
        maxSpinner.setModel(maxModelSpinner);
        maxSpinner.setEditor(new JSpinner.NumberEditor(maxSpinner, ""));
        maxSlider.setMaximum(prec);
        maxSlider.setMinimum(0);
        maxSlider.setValue((int) (value * prec / threshMax));
        //configure min spinner and slider
        value = mv.getMinThreshold();
        final SpinnerNumberModel minModelSpinner = new SpinnerNumberModel(value, threshMin, threshMax, stepSize);
        minSpinner.setModel(minModelSpinner);
        minSpinner.setEditor(new JSpinner.NumberEditor(minSpinner, ""));
        minSlider.setMaximum(prec);
        minSlider.setMinimum(0);
        minSlider.setValue((int) (value * prec / threshMax));

        //add change listeners for spinners and sliders
        maxSpinner.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e)
            {
                maxSlider.setValue(Double.valueOf((Double) maxModelSpinner.getValue() * prec / threshMax).intValue());
                //if max  becomes smaller than min, set min to follow max
                if ((Double) maxModelSpinner.getValue() < (Double) minModelSpinner.getValue())
                {
                    minSlider.setValue(maxSlider.getValue());
                    minModelSpinner.setValue(maxModelSpinner.getValue());
                }
            }
        });

        maxSlider.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e)
            {
                maxModelSpinner.setValue(maxSlider.getValue() * threshMax / prec);
                //if max  becomes smaller than min, set min to follow max
                if (maxSlider.getValue() < minSlider.getValue())
                {
                    minSlider.setValue(maxSlider.getValue());
                    minModelSpinner.setValue(maxModelSpinner.getValue());
                }
            }
        });


        minSpinner.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e)
            {
                minSlider.setValue(Double.valueOf((Double) minModelSpinner.getValue() * prec / threshMax).intValue());
                //if min becomes larger than max, set max to follow min
                if ((Double) minModelSpinner.getValue() > (Double) minModelSpinner.getValue())
                {
                    maxSlider.setValue(minSlider.getValue());
                    maxModelSpinner.setValue(minModelSpinner.getValue());
                }
            }
        });

        minSlider.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e)
            {
                minModelSpinner.setValue(minSlider.getValue() * threshMax / prec);
                //if min becomes larger than max, set max to follow min
                if (minSlider.getValue() > maxSlider.getValue())
                {
                    maxSlider.setValue(minSlider.getValue());
                    maxModelSpinner.setValue(minModelSpinner.getValue());
                }
            }
        });

        //set color scale selections
        if (mv.getColorScale() == HeatChart.SCALE_LINEAR)
        {
            linearRadBtn.setSelected(true);
        }
        else if (mv.getColorScale() == HeatChart.SCALE_EXPONENTIAL)
        {
            expRadBtn.setSelected(true);
        }
        else if (mv.getColorScale() == HeatChart.SCALE_LOGARITHMIC)
        {
            logRadBtn.setSelected(true);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        minLabel = new javax.swing.JLabel();
        maxLabel = new javax.swing.JLabel();
        expRadBtn = new javax.swing.JRadioButton();
        linearRadBtn = new javax.swing.JRadioButton();
        applyBtn = new javax.swing.JButton();
        cancelBtn = new javax.swing.JButton();
        minSlider = new javax.swing.JSlider();
        maxSlider = new javax.swing.JSlider();
        adjustColorScaleTitleLabel = new javax.swing.JLabel();
        scaleLabel = new javax.swing.JLabel();
        logRadBtn = new javax.swing.JRadioButton();
        minSpinner = new javax.swing.JSpinner();
        maxSpinner = new javax.swing.JSpinner();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanel1.setBorder(new javax.swing.border.LineBorder(java.awt.Color.blue, 2, true));
        jPanel1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                jPanel1ComponentHidden(evt);
            }
        });

        minLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        minLabel.setText("Minimum");

        maxLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        maxLabel.setText("Maximum");

        buttonGroup1.add(expRadBtn);
        expRadBtn.setText("Exponential");
        expRadBtn.setActionCommand("Create a new Clustering");
        expRadBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expRadBtnActionPerformed(evt);
            }
        });

        buttonGroup1.add(linearRadBtn);
        linearRadBtn.setText("Linear");
        linearRadBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                linearRadBtnActionPerformed(evt);
            }
        });

        applyBtn.setText("Apply");
        applyBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyBtnActionPerformed(evt);
            }
        });

        cancelBtn.setText("Cancel");
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBtnActionPerformed(evt);
            }
        });

        adjustColorScaleTitleLabel.setFont(new java.awt.Font("Tahoma", 1, 14));
        adjustColorScaleTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        adjustColorScaleTitleLabel.setText("Adjust Color Scale");

        scaleLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        scaleLabel.setText("Scale");

        buttonGroup1.add(logRadBtn);
        logRadBtn.setText("Logarithmic");
        logRadBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logRadBtnActionPerformed(evt);
            }
        });

        minSpinner.setPreferredSize(new java.awt.Dimension(30, 20));

        maxSpinner.setPreferredSize(new java.awt.Dimension(30, 20));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(adjustColorScaleTitleLabel)
                        .addGap(95, 95, 95))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(scaleLabel)
                            .addComponent(maxLabel)
                            .addComponent(minLabel))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(logRadBtn)
                            .addComponent(expRadBtn)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(maxSlider, 0, 0, Short.MAX_VALUE)
                                    .addComponent(minSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(maxSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(minSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(linearRadBtn))))
                .addGap(26, 26, 26))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(82, 82, 82)
                .addComponent(applyBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(82, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(adjustColorScaleTitleLabel)
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(minLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                    .addComponent(minSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                    .addComponent(minSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(maxSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(maxLabel))
                        .addGap(9, 9, 9)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(scaleLabel)
                            .addComponent(linearRadBtn))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(expRadBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(logRadBtn))
                    .addComponent(maxSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(applyBtn)
                    .addComponent(cancelBtn))
                .addGap(23, 23, 23))
        );

        add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        if (!success)
        {
            mv.cancelAdjustColorScale();
        }
        dispose();
    }//GEN-LAST:event_closeDialog

    private void linearRadBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_linearRadBtnActionPerformed
}//GEN-LAST:event_linearRadBtnActionPerformed

    private void expRadBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expRadBtnActionPerformed
}//GEN-LAST:event_expRadBtnActionPerformed

    private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
        mv.cancelAdjustColorScale();
        success = true;
        this.closeDialog(null);
}//GEN-LAST:event_cancelBtnActionPerformed

    private void applyBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyBtnActionPerformed
        mv.applyAdjustColorScale((Double) minSpinner.getValue(), (Double) maxSpinner.getValue(),
                linearRadBtn.isSelected(), expRadBtn.isSelected(), logRadBtn.isSelected());
        success = true;
        this.closeDialog(null);
}//GEN-LAST:event_applyBtnActionPerformed

    private void logRadBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logRadBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_logRadBtnActionPerformed

    private void jPanel1ComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel1ComponentHidden
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel1ComponentHidden
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel adjustColorScaleTitleLabel;
    private javax.swing.JButton applyBtn;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton cancelBtn;
    private javax.swing.JRadioButton expRadBtn;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton linearRadBtn;
    private javax.swing.JRadioButton logRadBtn;
    private javax.swing.JLabel maxLabel;
    private javax.swing.JSlider maxSlider;
    private javax.swing.JSpinner maxSpinner;
    private javax.swing.JLabel minLabel;
    private javax.swing.JSlider minSlider;
    private javax.swing.JSpinner minSpinner;
    private javax.swing.JLabel scaleLabel;
    // End of variables declaration//GEN-END:variables
}
