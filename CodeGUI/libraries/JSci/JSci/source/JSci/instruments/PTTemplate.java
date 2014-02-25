package JSci.instruments;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.text.*;

/** Find a given template in a two dimensional image. Usage:
 * press <strong>Add</strong> button; the image stops; select a template with the inner 
 * rectangle; press <strong>Done</strong>. The tracking starts immediately. If you want to 
 * track another object, simply repeat the procedure. Select an object
 * number before <strong>Remove</strong>.
 */
public class PTTemplate implements ImageFilter,ParticleTracker {

    private static long startTime = System.currentTimeMillis();;
    private TwoROI theROI = null;
    private Image stillImage=null;
    private boolean catchImage=false;
    private JComboBox crossCombo = new JComboBox();

    /** @param r a TwoROI is needed. The inner rectangle represents the
     * template to be searched for; the outer represents the region in which the template is searched. 
     */
    public PTTemplate(TwoROI r) {
	theROI = r;
    }

    private ImageSink sink;
    public void setSink(ImageSink fs) {
        if (sink!=fs) {
            sink=fs;
            sink.setSource(this);
        }
    }

    private ImageSource source = null;
    public void setSource(ImageSource fs) {
        if (source!=fs) {
            source=fs;
            source.setSink(this);
        }
    }

    public int getWidth() { return source.getWidth(); }
    public int getHeight() { return source.getHeight(); }
    public Dimension getSize() { return source.getSize(); }


    /** defines the name of the filter */
    public String getName() { return "PT Template"; }

    private ParticleTrackerListener ptl = null;
    public void setListener(ParticleTrackerListener ptl) {
	this.ptl=ptl;
    }

    public String toString() {
	return "Particle Tracking - Template; startTime="+startTime;
    }

    /** find the bead */
    public void receive(Image f) {
	ListModel l = crossCombo.getModel();
	if (l.getSize()>=0) {
	    int[] n=new int[l.getSize()];
	    double[] x=new double[l.getSize()];
	    double[] y=new double[l.getSize()];
	    for (int j=0;j<l.getSize();j++) {
		PTTemplateCross cross = (PTTemplateCross)l.getElementAt(j);	    
		cross.find(f);
		n[j]=cross.getN();
		x[j]=cross.getX();
		y[j]=cross.getY();
	    }
	    if (ptl!=null) ptl.receivePosition(f.getTimeStamp()-startTime,n,x,y,null);
	}
	if (catchImage && stillImage==null) { stillImage=f; }
	if (sink!=null) {
	    if (stillImage!=null) sink.receive(stillImage);
	    else sink.receive(f);
	}
    }
    
    JComponent comp;
    /** defines the Component that controls this filter */
    public Component getControlComponent() { 
	if (comp!=null) return comp;
	JPanel comp = new JPanel();
	// comp.setLayout(new BorderLayout());
	// add buttons
	final JButton addButton = new JButton("Add");
	comp.add(addButton);
	addButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (addButton.getText().equals("Add")) {
			stillImage=null;
			catchImage=true;
			addButton.setText("Done");
		    }
		    else {
			catchImage=false;
			addButton.setText("Add");
			if (stillImage!=null) {
			    PTTemplateCross cross = new PTTemplateCross((Rectangle)theROI.getShape(),theROI.getOuterRectangle(),stillImage.getSubImage((Rectangle)theROI.getShape()));
			    crossCombo.addItem(cross);
			}
			stillImage=null;
		    }
		}
	    });
	
	// remove button
	JButton removeButton = new JButton("Remove");
	comp.add(removeButton);
	removeButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    crossCombo.removeItem(crossCombo.getSelectedItem());
		}
	    });
	// combo box
	crossCombo.setEditable(false);
	comp.add(crossCombo);
	// TwoROI component
	comp.add(theROI.getControlComponent());
	// return
	Border etched = BorderFactory.createEtchedBorder();
	Border titled = BorderFactory.createTitledBorder(etched,getName());
	comp.setBorder(titled);
	return comp;
    }



    public static void main(String[] args) {
	TwoROI r = new TwoROI();
	PTTemplate f = new PTTemplate(r);
	Player p = new Player();
	ImageSource fs = new SimulatedBarycentreSource();
	fs.setSink(f);
	f.setSink(p);
	p.addROI(r);
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
