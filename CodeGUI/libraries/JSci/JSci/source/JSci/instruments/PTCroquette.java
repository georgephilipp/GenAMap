package JSci.instruments;

import JSci.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.text.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

/** an Effect that does the particle tracking in 3D, for symmetric images */
public class PTCroquette extends ImageFilterAdapter implements ParticleTracker, ROI {

    /** Length of the arms of the cross */
    public static int CROSS_L=32;
    /** Width of the arms of the cross */
    public static int CROSS_H=3;
    /** Steps in z for calibration (microns) */
    public static double CALIBR_STEP=0.5;
    /** While calibrating, averages this number of frames */
    public static int CALIBR_AVR=3;

    public String getName() { return "PT 3D symmetric"; };

    private PTCroquetteCross cross1,cross2;
    private PositionControl pc = null;

    private ParticleTrackerListener ptl = null;
    public void setListener(ParticleTrackerListener ptl) {
	this.ptl=ptl;
    }

    /** @param p the device that will control the motion of the microscope
     */
    public PTCroquette(PositionControl p) {
	cross1 = new PTCroquetteCross(CROSS_L,CROSS_H,new Point(50,50),Color.MAGENTA);
	cross2 = new PTCroquetteCross(CROSS_L,CROSS_H,new Point(50,120),Color.CYAN);
	pc = p;
	controlComponent = new PTControlComponent( new Control[] {pc,cross1,cross2} ); 
	pc.addChangeListener((ChangeListener)cross1.getControlComponent());
	pc.addChangeListener((ChangeListener)cross2.getControlComponent());
	controlComponent = new PTControlComponent( new Control[] {pc,cross1,cross2} ); 
    }

    /** processes a frame. Do not call this directly.
     * @param f the image that must be processed
     */
    public void filter(Image f) {
	cross1.setBBox(new Rectangle(new Point(0,0),getSize()));
	cross2.setBBox(new Rectangle(new Point(0,0),getSize()));
	cross1.findXY(f);
	cross2.findXY(f);
	if (!calibrating) {
	    cross1.findZ(f);
	    cross2.findZ(f);
	}
	else {
	    cross1.calibrationSendImage(f);
	    cross2.calibrationSendImage(f);
	}
	if (ptl!=null) 
	    ptl.receivePosition(f.getTimeStamp(),
				new int[] {0,1},
				new double[] {cross1.getLocation().getX(),cross2.getLocation().getX()},
				new double[] {cross1.getLocation().getY(),cross2.getLocation().getY()},
				new double[] {cross1.getZ()-pc.getPosition(),cross2.getZ()-pc.getPosition()}
				);
    }


    /* ------------------------------------------------------- */
    /*                          ROI                            */
    /* ------------------------------------------------------- */
    
    public Shape getShape() { return cross2; }
           
    public void paint(java.awt.Graphics g) {
	Graphics2D g2= (Graphics2D)g;
	g2.setColor(cross1.getColor());
	g2.draw(cross1);
	Point2D p = cross1.getLocation();
	g2.drawString("Ref",(int)p.getX()+CROSS_H,(int)p.getY()+CROSS_L);
	g2.setColor(cross2.getColor());
	g2.draw(cross2);
    }
 
    private Component mouseComponent;

    /** Set the component that displays the image, so that 
     * the crosses can be moved with the mouse.
     * @param c the component from which we want to get the mouse actions
     */
    public void setComponent(Component c) {
	mouseComponent=c;
	c.addMouseListener(new MouseHandler());
	c.addMouseMotionListener(new MouseMotionHandler());
	c.addMouseWheelListener(new MouseWheelHandler());
    }



    /* ------------------------------------------------------- */
    /*                       Calibration                       */
    /* ------------------------------------------------------- */

    private boolean calibrating = false;

    private void doCalibration(int zNum,int lNum,double minZ,double maxZ) {
	if (zNum<=0 || minZ>=maxZ) return;
	calibrating=true;
	cross1.calibrationStart(zNum,minZ,maxZ);
	cross2.calibrationStart(zNum,minZ,maxZ);
	for (int j=0;j<zNum;j++) {
	    pc.setPosition(j*(maxZ-minZ)/zNum+minZ);
	    pc.sleep();
	    for (int k=0;k<lNum;k++) {
		cross1.calibrationRequest(j);
		cross2.calibrationRequest(j);
		cross1.calibrationWait();
		cross2.calibrationWait();
	    }
	}
	cross1.calibrationEnd();
	cross2.calibrationEnd();
	calibrating=false;
    }



    /* ------------------------------------------------------- */
    /*                   Control Component                     */
    /* ------------------------------------------------------- */
 
    private PTControlComponent controlComponent;

    /**
     * @return the component with controls for the effect
     */
    public Component getFilterControlComponent() { return controlComponent; }

    private class PTControlComponent extends JPanel {

	private JButton calibrButton = new JButton("Calibrate");

	private JButton setMin = new JButton("Set min");
	private JButton setMax = new JButton("Set max");
	private JSpinner calibrMin = 
	    new JSpinner(new SpinnerNumberModel(0.0,pc.getMinimum(),pc.getMaximum(),0.1));
	private JSpinner calibrMax = 
	    new JSpinner(new SpinnerNumberModel(0.0,pc.getMinimum(),pc.getMaximum(),0.1));

	public PTControlComponent(Control [] cc) { 
	    super();
	    for (int j=0;j<cc.length;j++) add(cc[j].getControlComponent());
	    add(calibrButton);

	    calibrButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			Thread calibrThrd = new Thread(new Runnable() {
				public void run() {
				    calibrButton.setEnabled(false);
				    pc.getControlComponent().setEnabled(false);
				    double min = ((Double)calibrMin.getValue()).doubleValue();
				    double max = ((Double)calibrMax.getValue()).doubleValue();
				    doCalibration((int)Math.round((max-min)/CALIBR_STEP),CALIBR_AVR,min,max);
				    pc.getControlComponent().setEnabled(true);
				    calibrButton.setEnabled(true);
				}
			    });
			calibrThrd.start();
		    }
		});
	    
	    setMin.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			calibrMin.setValue(new Double(pc.getPosition()));
		    }
		});
	    setMax.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			calibrMax.setValue(new Double(pc.getPosition()));
		    }
		});
	    JPanel calibrPanel1 = new JPanel();
	    calibrPanel1.setLayout( new BorderLayout());
	    JPanel calibrPanel2 = new JPanel();
	    calibrPanel2.setLayout( new BorderLayout());
	    calibrPanel1.add("North",setMax);
	    calibrPanel2.add("North",calibrMax);
	    calibrPanel1.add("South",setMin);
	    calibrPanel2.add("South",calibrMin);
	    add(calibrPanel1);
	    add(calibrPanel2);
	}
    }


    /* ------------------------------------------------------- */
    /*                   Mouse Listeners                       */
    /* ------------------------------------------------------- */
 


    private Point refPos = new Point();
 

    private class MouseHandler extends MouseAdapter {
	public void mousePressed(MouseEvent event) {
	    if (cross1.getBounds().contains(event.getPoint())) {
		cross1.setDragging(true);
		refPos=new Point((int)cross1.getLocation().getX(),(int)cross1.getLocation().getY());
	    }
	    else if (cross2.getBounds().contains(event.getPoint())) {
		cross2.setDragging(true);
		refPos=new Point((int)cross2.getLocation().getX(),(int)cross2.getLocation().getY());
	    }
	    refPos.translate(-event.getPoint().x,-event.getPoint().y);
	}
	public void mouseReleased(MouseEvent event) {
	    cross1.setDragging(false);
	    cross2.setDragging(false);
	}
    }

    private class MouseMotionHandler extends MouseMotionAdapter {
	public void mouseMoved(MouseEvent event) {
	    if (
		cross1.getBounds().contains(event.getPoint()) ||
		cross2.getBounds().contains(event.getPoint())
		)
		mouseComponent.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
	    else 
		mouseComponent.setCursor(Cursor.getDefaultCursor());
	}
	public void mouseDragged(MouseEvent event) {
	    if (cross1.isDragging()) cross1.setLocation(new Point(event.getPoint().x+refPos.x,event.getPoint().y+refPos.y));
	    if (cross2.isDragging()) cross2.setLocation(new Point(event.getPoint().x+refPos.x,event.getPoint().y+refPos.y));
	}
    }

    private class MouseWheelHandler implements MouseWheelListener {
	public void mouseWheelMoved(MouseWheelEvent e) {
	    if (calibrating) return;
	    double p=pc.getPosition();
	    p-=e.getWheelRotation()*0.1;
	    pc.setPosition(p); 
	}
    }



    ///////////////////////////////////////////////////////////////////////////////////
    // MAIN - an example that uses some simulated controls.

    public static void main(String args[]) {
	PositionControl pc = new DummyPositionControl() {
		protected void doSetPosition(double p) {
		    Simulated.z=p;
		    super.doSetPosition(p);
		}
	    };
	Simulated.z=pc.getPosition();
	PTCroquette filter = new PTCroquette(pc);
	ImageSource source = new Simulated();
	Player player = new Player();
	player.addROI(filter);
	source.setSink(filter);
	filter.setSink(player);
	player.start();

	final NumberFormat formatter = NumberFormat.getNumberInstance();
	{ formatter.setMaximumFractionDigits(3);
	formatter.setMinimumFractionDigits(1);
	formatter.setMinimumIntegerDigits(1);
	formatter.setMaximumIntegerDigits(4);
	}
	ParticleTrackerListener ptl = new ParticleTrackerListener() {
		public void receivePosition(long time,int[] n,double[] x,double[] y,double[] z) {
		    System.out.println(time+" "+x[0]+" "+y[0]+" "+z[0]+" "+x[1]+" "+y[1]+" "+z[1]);
		}
	    };
	filter.setListener(ptl);



    }


}






////////////////////////////////////////////////////////////////////////////////////////
// Other classes ONLY for simulations and tests



///////////////////////////////////////////////////////////////////////////////////////
// Simulated image ImageSource

class Simulated implements ImageSource,Runnable {



    private Dimension dim = new Dimension(600,500);

    public Simulated() {
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

    public void run() {
	int num = 0;
	while (true) {
	    Image f = getImage();
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
	p.setLayout(new BorderLayout());
	final JButton oscillButton = new JButton("Oscillate");
	oscillButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    boolean state = ! Simulated.getOscillating();
		    Simulated.setOscillating(state);
		    if (state) oscillButton.setText("Stop");
		    else oscillButton.setText("Oscillate");
		}
	    });
	p.add(BorderLayout.NORTH,new JLabel("Test"));
	p.add(BorderLayout.SOUTH,oscillButton);
	Border etched = BorderFactory.createEtchedBorder();
	Border titled = BorderFactory.createTitledBorder(etched,"source");
	p.setBorder(titled);
	return p;
    }


    // generate the images

    public static double z;
    private long num=0;

    private double theta = 0.0, vs,vc,dh;
    private final double R=100.0;
    private double f(double x) {
	return 1.0/(1.0-Math.pow(x,2)+Math.pow(x,4));
    }
    private double g(double x,double y) {
	return f(Math.sqrt(Math.pow(x-vc,2)+Math.pow(y-vs,2))/(5.0+(z+dh)/4.0));
    }

    private Image getImage() {
	double x1,y1,z1,x2,y2,z2;
	vs=0.0;
	theta+=0.04;
	final byte[] ba = new byte[600*500];
	for (int j=0;j<500*600;j++) ba[j]=0;
	vc=R*Math.cos(theta);
	if (oscillate) dh=Math.sin(theta*1.3);
	else dh=0.0;
	for (int j=170;j<430;j++)
	    for (int k=320;k<380;k++)
		ba[j+k*600]=(byte)(160.0*g(j-300.0,k-350.0));
	x1=300.0+vc;y1=350;z1=dh;
	theta+=2.0;
	vc=R*Math.cos(theta);
	theta-=2.0;
	if (oscillate) dh=Math.cos(theta*1.3);
	else dh=0.0;
	for (int j=170;j<430;j++)
	    for (int k=120;k<180;k++)
		ba[j+k*600]=(byte)(100.0*g(j-300.0,k-150.0));
	x2=300.0+vc;y2=150;z2=dh;

	final long t = System.currentTimeMillis();
	final Dimension dim = new Dimension(getWidth(),getHeight());
	Image f = new Image() {
		public byte[] getData() { return ba; }
		public Dimension getSize() { return dim; }
		public long getTimeStamp() { return t; }
	    };

	System.out.println("#Simulated: "+num+" "+
			   +x1+" "+y1+" "+z1+" "+
			   x2+" "+y2+" "+z2
			   );

	return f;
    }

    static private boolean oscillate;
    static public void setOscillating(boolean o) {oscillate=o;}
    static public boolean getOscillating() {return oscillate;}

}
