package JSci.instruments;

import JSci.swing.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/** A simple PositionControl that does nothing!
 */
public class DummyPositionControl extends PositionControlAdapter {

    private double position = Double.NaN;

    public void setPosition(double p) {
	if (p<getMinimum() || p>getMaximum() || position==p ) return;
	position=p;
	doSetPosition(p);
	fireStateChanged();
    }
    
    protected void doSetPosition(double p) {}

    public double getPosition() {
	if (Double.isNaN(position)) position=getActualPosition();
	return position;
    }

    public double getActualPosition() {
	if (Double.isNaN(position)) position=50.0;
	return position;
    }

    public double getMinimum() {
	return 0.0;
    }

    public double getMaximum() {
	return 100.0;
    }

    public double getMinorStep() {
	return 10.0;
    }

    public double getMajorStep() {
	return 20.0;
    }

    public void sleep() {
    }

    private JComponent ccomp = null;
    
    private JComponent createControlPanel() {
	JPanel panel;
	final JPointer slider;
	final JSpinner spinner;

	JSliderPlus sliderHolder = new  JSliderPlus(JSlider.VERTICAL,getMinimum(),getMaximum());
	slider = new JPointer(JPointer.SLIDER_SIMPLE_TRIANGLE);
	slider.setValue(getPosition());
	slider.setColor(Color.MAGENTA);
	sliderHolder.setMinorTickSpacing(getMinorStep());
	sliderHolder.setMajorTickSpacing(getMajorStep());
	sliderHolder.setPaintMinorTicks(true);
	sliderHolder.setPaintMajorTicks(true);
	sliderHolder.setPaintLabels(true);
	sliderHolder.addJPointer(slider);

	spinner = new JSpinner(new SpinnerNumberModel(getPosition(),getMinimum(),getMaximum(),getMinorStep()));
	panel = new JPanel() {
		public void setEnabled(boolean b) {
		    super.setEnabled(b);
		    slider.setEnabled(b);
		    spinner.setEnabled(b);
		}
	    };
	panel.setLayout(new BorderLayout());
	panel.add("West",sliderHolder);
	JPanel spinnerPanel = new JPanel();
	spinnerPanel.add(spinner);
	panel.add("East",spinnerPanel);

	spinner.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent event) {
		    double p = ((Double)spinner.getValue()).doubleValue();
		    setPosition(p);
		}
	    });
	slider.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent event) {
		    double p = slider.getValue();
		    setPosition(p);
		}
	    });
	addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent event) {
		    double p = getPosition();
		    spinner.setValue(new Double(p));
		    slider.setValue(p);
		}
	    });

	return panel;
    }

    public Component getControlComponent() {
	if (ccomp==null) ccomp=createControlPanel();
	return ccomp;
    }

    public String getUnit() {
	return "unknown";
    }

    public static void main(String [] args) {
	PositionControl pc = new DummyPositionControl() {
		
	    };
	JFrame frm = new JFrame("Testing the DummyPositionControl");
	frm.getContentPane().add(pc.getControlComponent());
	frm.pack();
	frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frm.show();
    }

}
