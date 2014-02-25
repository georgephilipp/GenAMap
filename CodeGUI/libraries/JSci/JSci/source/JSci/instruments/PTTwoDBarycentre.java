package JSci.instruments;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.text.*;

/** Find a bead in two dimensions; Barycentre algorithm */

public class PTTwoDBarycentre extends ImageFilterAdapter implements ParticleTracker {

    /** the image must be considered interlaced, that is composed by two frames distant by TIME_FIELD? */
    public static boolean INTERLACED = true;
    /** for interlaced images (see INTERLACED),two frames are distant by TIME_FIELD ms */
    public static int TIME_FIELD = 20;

    private JComboBox crossCombo = new JComboBox();
    private static long startTime = System.currentTimeMillis();;

    /** defines the name of the filter */
    public String getName() { return "PT TwoD Barycentre"; }

    private ParticleTrackerListener ptl = null;
    public void setListener(ParticleTrackerListener ptl) {
	this.ptl=ptl;
    }

    public String toString() {
	return "Particle Tracking; INTERLACED="+INTERLACED+
	    "; ALPHA="+PTTwoDBarycentreCross.ALPHA+
	    "; WEIGHT_LIGHT_PART="+PTTwoDBarycentreCross.WEIGHT_LIGHT_PART+
	    "; REGION_SPEED="+PTTwoDBarycentreCross.REGION_SPEED+
	    "; ODD_EVEN="+PTTwoDBarycentreCross.ODD_EVEN+
	    "; startTime="+startTime;
    }

    /** find the bead */
    public void filter(Image f) {
	ListModel l = crossCombo.getModel();
	if (l.getSize()==0) return;
	int[] n=new int[l.getSize()];
	double[] x=new double[l.getSize()];
	double[] y=new double[l.getSize()];
	for (int j=0;j<l.getSize();j++) {
	    PTTwoDBarycentreCross cross = (PTTwoDBarycentreCross)l.getElementAt(j);	    
	    if (INTERLACED) cross.findInterlaced(f);
	    else cross.find(f);
	    n[j]=cross.getN();
	    x[j]=cross.getX();
	    y[j]=cross.getY();
	}
	if (ptl!=null) ptl.receivePosition(f.getTimeStamp()-startTime,n,x,y,null);
	if (!INTERLACED) return;
	for (int j=0;j<l.getSize();j++) {
	    PTTwoDBarycentreCross cross = (PTTwoDBarycentreCross)l.getElementAt(j);
	    x[j]=cross.getX2();
	    y[j]=cross.getY2();
	}
	if (ptl!=null) ptl.receivePosition(f.getTimeStamp()+TIME_FIELD-startTime,n,x,y,null);
    }
    
    JComponent comp;
    /** defines the Component that controls this filter */
    public Component getFilterControlComponent() { 
	if (comp!=null) return comp;
	JPanel comp = new JPanel();
	comp.setLayout(new BorderLayout());
	// add button
	JButton addButton = new JButton("Add");
	comp.add(BorderLayout.NORTH,addButton);
	addButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (theROI==null) return;
		    PTTwoDBarycentreCross cross = new PTTwoDBarycentreCross((Rectangle)theROI.getShape());
		    crossCombo.addItem(cross);
		}
	    });
	// remove button
	JButton removeButton = new JButton("Remove");
	comp.add(BorderLayout.SOUTH,removeButton);
	removeButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    crossCombo.removeItem(crossCombo.getSelectedItem());
		}
	    });
	// combo box
	crossCombo.setEditable(false);
	comp.add(BorderLayout.EAST,crossCombo);
	// return
	return comp;
    }

    private ROI theROI = null;
    /** set the ROI for the selections */
    public void setROI(ROI r) {
	theROI = r;
    }

    public static void main(String[] args) {
	ROI r = new RectangularROI(10,10,30,30);
	PTTwoDBarycentre f = new PTTwoDBarycentre();
	Player p = new Player();
	ImageSource fs = new SimulatedBarycentreSource();
	fs.setSink(f);
	f.setSink(p);
	p.addROI(r);
	f.setROI(r);
	p.start();

	final NumberFormat formatter = NumberFormat.getNumberInstance();
	{ formatter.setMaximumFractionDigits(3);
	formatter.setMinimumFractionDigits(1);
	formatter.setMinimumIntegerDigits(1);
	formatter.setMaximumIntegerDigits(4);
	}
	
	ParticleTrackerListener ptl=new ParticleTrackerListener() {
		public void receivePosition(long time,int[] n,double[] x,double[] y,double[] z) {
		    System.out.print(time);
		    for (int j=0;j<n.length;j++)
			System.out.print(" "+n[j]+" "+formatter.format(x[j])+" "+formatter.format(y[j]));
		    System.out.println();
		}
	    };
	
	f.setListener(ptl);

    }











/////////////////////////////////////////////////////////////////////////////
// Simulated - for testing

static class SimulatedBarycentreSource implements ImageSource,Runnable {

    Dimension dim = new Dimension(130,97);

    public SimulatedBarycentreSource() {
	Thread t = new Thread(this);
	t.setDaemon(true);
	t.start();
    }

    private ImageSink sink;
    /** set the object that must read the frames through receive() method.
     * @param fs the object that reads the frames
     */
    public void setSink(ImageSink fs) {
	if (sink!=fs) {
	    sink=fs;
	    sink.setSource(this);
	}
    }


    private static final double SIGMA = 4.0;
    private static final double DELTAX = 4.0;
    private static final double DELTAY = 2.0;
    private double f(double x,double y) {
	return 
	    Math.exp(-(x*x+y*y)/(2.0*SIGMA*SIGMA))-
	    Math.exp(-((x-DELTAX)*(x-DELTAX)+(y-DELTAY)*(y-DELTAY))/(2.0*SIGMA*SIGMA));
    }

    public void run() {
	int num = 0;
	while (true) {
	    final int n = num++;
	    final long t = System.currentTimeMillis();
	    final double x0 = Math.random()*4+40.0;
	    final double y0 = Math.random()*4+50.0;
	    final byte[] im = new byte[getWidth()*getHeight()];
	    final Dimension dim = new Dimension(getWidth(),getHeight());
	    Image f = new Image() {
		    public byte[] getData() { return im; }
		    public Dimension getSize() { return dim; }
		    public long getTimeStamp() { return t; }
		};

	    for (int j=0;j<f.getWidth();j++) 
		for (int k=0;k<f.getHeight();k++)
		    f.getData()[j+k*f.getWidth()] = (byte) (128.0+128.0*SimulatedBarycentreSource.this.f(j-x0,k-y0));

	    if (sink!=null) sink.receive(f);
	    try { Thread.sleep(100); }
	    catch (InterruptedException e) {}
	}
    }

    /** @return the width of the image */
    public int getWidth() { return dim.width; }
    
    /** @return the height of the image */
    public int getHeight() { return dim.height; }
    
    /** @return the dimension of the image */
    public Dimension getSize() { return dim; }

    /** @return the component that controls the object that implements
	this interface */
    public Component getControlComponent() {
	JPanel p = new JPanel();
	p.add(new JLabel("Test"));
	Border etched = BorderFactory.createEtchedBorder();
	Border titled = BorderFactory.createTitledBorder(etched,"source");
	p.setBorder(titled);
	return p;
    }

}
}



