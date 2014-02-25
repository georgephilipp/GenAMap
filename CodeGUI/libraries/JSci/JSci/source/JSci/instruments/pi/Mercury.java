package JSci.instruments.pi;

import JSci.instruments.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.util.prefs.*;
import java.io.*;
import java.text.*;

/** 
 * A mechanical device attached to a Mercury PI board.<br>
 * Attention! The Mercury board forgets the position of the axes when it is reset or
 * is turned off. Do not turn off the Mercury while the motor is moving!<br>
 * There is a lot of <code>final</code> and <code>protected</code> methods, because
 * this should be not be simply sub-classed by motor-specific classes, but it should be 
 * included in them.
 */
public abstract class Mercury extends PositionControlAdapter {
  
    private int port;
    private int axis;
    private double bias = 0.0;
    private String type = "";
    private String version = "";
    private static final int numAxis = 10 ;
    private static final int numPorts = 10;
    private static DataInput rfi = null;
    private static DataOutput rfo = null;
    private Preferences persPref = Preferences.userRoot().node("/it/unimi/pi/Mercury");  

    /* read byte without EOF */
    private byte readByteWithoutEOF() throws IOException {
	boolean found=false;
	byte c=0;
        while (!found) {
	  try {
	    c=rfi.readByte();
	    found=true;
	  }
	  catch (EOFException e) { try { Thread.sleep(10); } catch (InterruptedException e2) {} }
	}
	return c;
    }

    private void writeByte(int b) throws IOException {
        // System.out.println("Command: <"+b+">");
        rfo.writeByte(b);
    }

    private void writeBytes(String s) throws IOException {
        // System.err.println("Command: "+s);
	rfo.writeBytes(s);
    }


    /* read a line from Mercury */
    private String readLine() {
 	// System.err.println("Reading.");
	String s="";
	char c;
	try {
	    while ((c=(char)readByteWithoutEOF())!=13) s+=c;
	}
	catch (IOException e) { throw new RuntimeException("Cannot communicate with port "+port+"; exception "+e); }
	try {
	    if (readByteWithoutEOF()==10) readByteWithoutEOF();
	}
	catch (IOException e) { throw new RuntimeException("Cannot communicate with port "+port+"; exception "+e); }
 	// System.err.println("Read: "+s);
	return s;
    }

    /** Remember this class is abstract! 
     * @param p the ttyS port number at which the Mercury is attached
     * @param rfi the input file connected to the port; something like
     *            new RandomAccessFile("/dev/ttyS0","rw")
     * @param rfo the output file connected to the port; something like
     *            new RandomAccessFile("/dev/ttyS0","rw")
     * @param n the number of the axis
     */
    protected Mercury(int p, DataInput rfi, DataOutput rfo, int n) {
	if (n<0) throw(new IllegalArgumentException(n+": axis number should be positive"));
	if (p<0) throw(new IllegalArgumentException(n+": ttyS port number should be positive"));
	port=p;
	axis=n;
	this.rfi = rfi;
	this.rfo = rfo;
	try {
	    synchronized (rfo) {
		// version
		writeByte(1);
		writeBytes(""+axis);
		writeBytes("ve");
		writeByte(13);  
		try {
			rfi.readByte();
		}
		catch (EOFException e) {
			// reset
			writeByte(1);
			writeBytes(""+axis);
			writeBytes("rt");
			writeByte(13);
		}
		readLine();
		// version
		writeByte(1);
		writeBytes(""+axis);
		writeBytes("ve");
		writeByte(13);  
		version=readLine();
		// brake off
		writeByte(1);
		writeBytes(""+axis);
		writeBytes("bf");
		writeByte(13);  
		// motor on
		writeByte(1);
		writeBytes(""+axis);
		writeBytes("mn");
		writeByte(13);  
	    }
	} 
	catch (IOException e) { throw(new IllegalArgumentException("Error writing to /dev/ttyS"+port)); }
	bias=0.0;
	bias=persPref.getDouble("zero-"+port+"-"+axis,0.0)-doGetPosition();
    }


    /** set the limit switch polarity.
     * @param b the switch signals are high when out-of-limit?
     */
    protected final  void setLimitSwitches(boolean b) {
	try {
	    synchronized (rfo) {
		writeByte(1);
		writeBytes(""+axis);
		if (b) writeBytes("lh");
		else writeBytes("ll");
		writeByte(13);
	    }
	}
	catch (IOException e) { throw new RuntimeException("Cannot communicate with ttyS"+port); }
    }

    /** move to the position between the two limit switches.
     */ 
    protected void findCenter() {
	try {
	    synchronized (rfo) {
		writeByte(1);
		writeBytes(""+axis);
		writeBytes("fe");
		writeByte(13); 
	    }
	}
	catch (IOException e) { throw new RuntimeException("Cannot communicate with ttyS"+port); }
    }
  
    /////////////////////////////////////////////////////////////////////////////////////////////
    // Position

    /** move the device to the given position.
     * Checking the limits must be done (if necessary) by the extending class (note this class is abstract!)
     * @param p the position that we want to reach, in counts.
     *          It is rounded to its nearest integer position
     */ 
    public void setPosition(double p) {
	if (p==currPos) return;
	currPos=p;
	doSetPosition(p);
	persPref.putDouble("zero-"+port+"-"+axis,p);
	fireStateChanged();
    }

    /* actually set the position. */
    private  void doSetPosition(double p) {
	try {
	    synchronized (rfo) {
		writeByte(1);
		writeBytes(""+axis);
		writeBytes("ma"+(long)(p-bias));
		writeByte(13);
	    }
	} catch (IOException e) { throw new RuntimeException("Cannot communicate with ttyS"+port); }
    }
    
    /** get the position.
     * @return the position in counts.
     *          It is alwais an integer value
     */ 
    public double getPosition() {
	if (Double.isNaN(currPos)) currPos=doGetPosition();
	return Math.rint(currPos);
    }
    private double currPos = Double.NaN;
    /* actually get the position */
    public double doGetPosition() {
	double p = 0.0;
	try {
	    synchronized (rfo) {
		writeByte(1);
		writeBytes(""+axis);
		writeBytes("tt");
		writeByte(13);
		p=Double.parseDouble(readLine().substring(2));
	    }
	} catch (IOException e) { throw new RuntimeException("Cannot communicate with ttyS"+port); }
	return p+bias;
    }

    /** get the actual position while the axis is moving.
     * @return the position in counts.
     *          It is alwais an integer value
     */ 
    public  double getActualPosition() { 	
	double p = 0.0;
	try {
	    synchronized (rfo) {
		writeByte(1);
		writeBytes(""+axis);
		writeBytes("tp");
		writeByte(13);
		p=Double.parseDouble(readLine().substring(2));
	    }
	} catch (IOException e) { throw new RuntimeException("Cannot communicate with ttyS"+port); }
	return p+bias;
    }

    /** get the minimum value of position 
     * @return the minimum position
     */
    public double getMinimum() { return Math.rint(persPref.getDouble("min-"+port+"-"+axis,getPosition())); }

    /** get the maximum value of position 
     * @return the maximum position
     */
    public double getMaximum() { return Math.rint(persPref.getDouble("max-"+port+"-"+axis,getPosition())); }

    /** set the minimum value of position. Only root can do this.
     * @param m the minimum position
     */
    public void setMinimum(double m) { persPref.putDouble("min-"+port+"-"+axis,m); }

    /** set the maximum value of position. Only root can do this.
     * @param m the maximum position
     */
    public void setMaximum(double m) { persPref.putDouble("max-"+port+"-"+axis,m); }

    /** get the unit in which the position is expressed
     * @return the units
     */
    public String getUnit() {return "counts";}

    /** get the minor step suggested for changing the position
     * @return the suggested minor step
     */
    public double getMinorStep() {return 400.0;}

    /** get the major step suggested for changing the position
     * @return the suggested major step
     */
    public double getMajorStep() {return 4000.0;}

    /** get the error on the actual position.
     * @return the error on the position in counts.
     *          It is rounded to its nearest integer position.
     */ 
    public double getPositionError() { return doGetPositionError(); }

    /* actually get the error on the actual position.
     * return the error on the position in counts.
     *          It is rounded to its nearest integer position.
     */ 
    public  double doGetPositionError() { 
	double p = 0.0;
	try {
	    synchronized (rfo) {
		writeByte(1);
		writeBytes(""+axis);
		writeBytes("te");
		writeByte(13);
		p=Double.parseDouble(readLine().substring(2));
	    }
	} catch (IOException e) { throw new RuntimeException("Cannot communicate with ttyS"+port); }
	return p;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    // Velocity

    /** set the velocity the device will reach when moving.
     * Checking the limits must be done (if necessary) by the extending class (note this class is abstract!)
     * @param v the velocity in counts per cycle. It is rounded to its
     *          nearest integer position.
     */ 
    public  void setVelocity(double v) {
	try {
	    synchronized (rfo) {
		writeByte(1);
		writeBytes(""+axis);
		writeBytes("sv"+(long)v);
		writeByte(13);
	    }
	} catch (IOException e) { throw new RuntimeException("Cannot communicate with ttyS"+port); }
    }

    /** get the velocity that should be reached when moving.
     * @return the velocity in counts per cycle. It is rounded to its
     *          nearest integer position.
     */ 
    public  double getVelocity() {
	double p = 0.0;
	try {
	    synchronized (rfo) {
		writeByte(1);
		writeBytes(""+axis);
		writeBytes("ty");
		writeByte(13);
		p=Double.parseDouble(readLine().substring(2));
	    }
	} catch (IOException e) { throw new RuntimeException("Cannot communicate with ttyS"+port); }
	return p;
    }

    /** get the actual velocity while moving.
     * @return the velocity in counts per cycle. It is rounded to its
     *          nearest integer position.
     */ 
    public  double getActualVelocity() { 
	double p = 0.0;
	try {
	    synchronized (rfo) {
		writeByte(1);
		writeBytes(""+axis);
		writeBytes("tv");
		writeByte(13);
		p=Double.parseDouble(readLine().substring(2));
	    }
	} catch (IOException e) { throw new RuntimeException("Cannot communicate with ttyS"+port); }
	return p;
    }

    /** get the maximum value of velocity
     * @return the maximum velocity
     */
    public double getMaxVelocity() { return Double.POSITIVE_INFINITY; }

    /** get the step suggested for changing the velocity
     * @return the suggested step
     */
    public double getVelocityStep() {return 100000.0;}

    /** get the unit in which the velocity is expressed
     * @return the units
     */
    public String getVelocityUnit() {return "counts/cycle";}

    //////////////////////////////////////////////////////////////////////////////////////////////////
    // Motor controls
    
    /** Clear errors
     */ 
    protected void clearErrors() {}

    /** switch the motor on or off.
     * @param b do you want to switch the motor on? 
     */ 
    protected  void setMotorOn(boolean b) {	
	try {
	    synchronized (rfo) {
		writeByte(1);
		writeBytes(""+axis);
		if (b) writeBytes("mn");
		else writeBytes("mf");
		writeByte(13);
	    }
	} catch (IOException e) { throw new RuntimeException("Cannot communicate with ttyS"+port); }
	motor_on=b;
    }
    private boolean motor_on = true;

    /** check if the motor is on or off.
     * @return the motor is on? 
     */ 
    protected boolean getMotorOn() { return motor_on; }

    /** switch the brake on or off.
     * @param b do you want to switch the brake on? 
     */ 
    protected  void setBrake(boolean b) {
	try {
	    synchronized (rfo) {
		writeByte(1);
		writeBytes(""+axis);
		if (b) writeBytes("bn");
		else writeBytes("bf");
		writeByte(13);
	    }
	} catch (IOException e) { throw new RuntimeException("Cannot communicate with ttyS"+port); }
	brake_on=b;
    }
    private boolean brake_on;

    /** check if the brake is on or off.
     * @return the brake is on? 
     */ 
    protected boolean getBrake() { return brake_on; }



    /** get the status of event. CURRENTLY NOT IMPLEMENTED.
     * @return the properties of the event. 
     */ 
    protected Hashtable getEventStatus() { return null; }
    protected static final String EVENT_MOTION_COMPLETE = "Motion complete";
    protected static final String EVENT_WRAP_AROUND = "Wrap around";
    protected static final String EVENT_BREAKPOINT1 = "Breakpoint 1";
    protected static final String EVENT_CAPTURE_RECEIVED = "Capture received";
    protected static final String EVENT_MOTION_ERROR = "Motion error";
    protected static final String EVENT_POS_LIMIT = "Positive limit";
    protected static final String EVENT_NEG_LIMIT = "Negative limit";
    protected static final String EVENT_INSTRUCTION_ERR = "Instruction error";
    protected static final String EVENT_COMMUNICATION_ERR = "Communication error";
    protected static final String EVENT_BREAKPOINT2 = "Breakpoint 2";

    /** get the status of signal. CURRENTLY NOT IMPLEMENTED.
     * @return the properties of the signal. 
     */ 
    protected Hashtable getSignalStatus() { return null; }
    protected static final String SIGNAL_ENCODER_A = "Encoder A";
    protected static final String SIGNAL_ENCODER_B = "Encoder B";
    protected static final String SIGNAL_ENCODER_INDEX = "Encoder index";
    protected static final String SIGNAL_ENCODER_HOME = "Encoder home";
    protected static final String SIGNAL_POS_LIMIT = "Positive limit"; 
    protected static final String SIGNAL_NEG_LIMIT = "Negative limit";
    protected static final String SIGNAL_AXIS_IN = "Axis in";
    protected static final String SIGNAL_HALL_A = "Hall A";
    protected static final String SIGNAL_HALL_B = "Hall B";
    protected static final String SIGNAL_HALL_C = "Hall C";
    protected static final String SIGNAL_AXIS_OUT = "Axis out";
    protected static final String SIGNAL_STEP_OUTPUT = "Step output";
    protected static final String SIGNAL_MOTOR_OUTPUT = "Motor output";


    /** get the status of activity. CURRENTLY NOT IMPLEMENTED.
     * @return the property of activity
     */ 
    protected Hashtable getActivityStatus() { return null; }
    protected static final String ACTIVITY_PHASING_INITD = "Not for the PI boards!!!";
    protected static final String ACTIVITY_MAX_VELOCITY = "Running at max velocity";
    protected static final String ACTIVITY_TRACKING = "Tracking";
    protected static final String ACTIVITY_PROFILE_MODE_0 = "Profile mode 0";      
    protected static final String ACTIVITY_PROFILE_MODE_1 = "Profile mode 1";           
    protected static final String ACTIVITY_PROFILE_MODE_2 = "Profile mode 2";
    protected static final String ACTIVITY_RESERVED = "Reserved";
    protected static final String ACTIVITY_AXIS_SETTLED = "Setteled";
    protected static final String ACTIVITY_MOTOR_STATUS = "Motor status";
    protected static final String ACTIVITY_POSITION_CAPTURE = "Position capture";
    protected static final String ACTIVITY_IN_MOTION = "In motion";
    protected static final String ACTIVITY_ACT_POS_LIMIT = "Actual positive limit";
    protected static final String ACTIVITY_ACT_NEG_LIMIT = "Actual negative limit";


    /** sleep until the axis has reached its position */
    public void sleep() {
	do { 
	    try {
		Thread.sleep(200);
	    }
	    catch (InterruptedException e) {}
	} while (Math.abs(doGetPositionError())>19.5);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Visual Component


    /** Create a JPanel with the controls of the position
     * @param monitor monitor the actual position every 0.1 s.
     * @param vertical the direction of motion is vertical
     * @param inverted invert the direction of motion
     */
    protected JPanel createPositionPanel(boolean monitor,boolean vertical, boolean inverted) {
	final JButton setLimits = new JButton("Limits");
	final JSpinner spinner;
	JButton plus,minus;
	JPanel fastMovement = new JPanel();
	JPanel r = new JPanel();
	fastMovement.setLayout(new BorderLayout());
	r.setLayout(new FlowLayout());
	if (vertical) {
	    plus = new JButton("^");
	    minus = new JButton("v");
	    fastMovement.add("North",plus);
	    fastMovement.add("South",minus);
	}
	else {
	    plus = new JButton(">");
	    minus = new JButton("<");
	    fastMovement.add("East",plus);
	    fastMovement.add("West",minus);
	}
	spinner=new JSpinner(new SpinnerNumberModel(getPosition(),getMinimum(),getMaximum(),getMinorStep()));

	Dimension dim = spinner.getPreferredSize();
	dim.setSize(100,dim.getHeight());
	spinner.setPreferredSize(dim);

	r.add(spinner);
	r.add(new JLabel("("+getUnit()+")"));
	r.add(fastMovement);

	if (monitor) {
	    final JSpinner actpos;
	    actpos = new JSpinner(new SpinnerNumberModel(getPosition(),getMinimum(),getMaximum(),getMinorStep() ));
	    dim = actpos.getPreferredSize();
	    dim.setSize(100,dim.getHeight());
	    actpos.setPreferredSize(dim);
	    actpos.setEnabled(false);
	    r.add(new JLabel("Actual: "));
	    r.add(actpos);
	    r.add(new JLabel("("+getUnit()+")"));
	    class ActposChangeListener implements ChangeListener,Runnable {
		private boolean running = false;
		public void run() {
		    Thread t = new Thread(new Runnable() {
			    public void run() { sleep(); running=false; }
			});
		    t.setDaemon(true);
		    t.start();
		    do {
			actpos.setValue(new Double(getActualPosition())); 
			try { Thread.sleep(100); } catch (InterruptedException e) {}
		    } while (running);
		    actpos.setValue(new Double(getActualPosition())); 
		}
		public void stateChanged(ChangeEvent event) {
		    if (running) return;
		    Thread t = new Thread(this);
		    t.setDaemon(true);
		    running=true;
		    t.start();
		}
	    }
	    addChangeListener(new ActposChangeListener());
	}

	if (!inverted) {
	    minus.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			double p=getPosition()-getMajorStep();
			if (p>=getMinimum()) spinner.setValue(new Double(p));
		    }
		});
	    plus.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			double p=getPosition()+getMajorStep();
			if (p<=getMaximum()) spinner.setValue(new Double(p));
		    }
		});
	}
	else {
	    minus.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			double p=getPosition()+getMajorStep();
			if (p<=getMaximum()) spinner.setValue(new Double(p));
		    }
		});
	    plus.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			double p=getPosition()-getMajorStep();
			if (p>=getMinimum()) spinner.setValue(new Double(p));
		    }
		});
	}
	spinner.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent event) {
		    double p=((Double)spinner.getValue()).doubleValue();
		    setPosition(p);
		}
	    });
	addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent event) {
		    spinner.setValue(new Double(getPosition()));
		}
	    });

	r.add(setLimits);
	setLimits.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (setLimitsDialog==null) setLimits();
		    if (!setLimitsDialog.isVisible()) setLimitsDialog.setVisible(true);
		    setLimitsDialog.toFront();
		}
	    });

	r.setBorder(
		    BorderFactory.createTitledBorder(
						     BorderFactory.createEtchedBorder(),
						     "Position"
						     )
		    );
	return r;
    }

    /** Create a JPanel with the controls of the velocity
     * @param monitor monitor the actual velocity every 0.1 s.
     */
    protected JPanel createVelocityPanel(boolean monitor) {
	final JSpinner vel;
	final JRadioButton inMotion;
	JPanel r = new JPanel();
	r.setLayout(new FlowLayout());
	vel = new JSpinner(new SpinnerNumberModel(getVelocity(),0.0,getMaxVelocity(),getVelocityStep()));
	inMotion = new JRadioButton("In motion");
	inMotion.setSelected(false);
	inMotion.setEnabled(false);

	Dimension dim = vel.getPreferredSize();
	dim.setSize(100,dim.getHeight());
	vel.setPreferredSize(dim);

	r.add(vel);
	r.add(new JLabel("("+getVelocityUnit()+")"));

	if (monitor) {
	    final JSpinner actvel;
	    actvel = new JSpinner(new SpinnerNumberModel(getActualVelocity(),-getMaxVelocity(),getMaxVelocity(),getVelocityStep()) );

	    dim = actvel.getPreferredSize();
	    dim.setSize(100,dim.getHeight());
	    actvel.setPreferredSize(dim);
	    actvel.setEnabled(false);
	    r.add(new JLabel("Actual: "));
	    r.add(actvel);
	    r.add(new JLabel("("+getVelocityUnit()+")"));
	    class ActvelChangeListener implements ChangeListener,Runnable {
		private boolean running = false;
		public void run() {
		    Thread t = new Thread(new Runnable() {
			    public void run() { 
				inMotion.setSelected(true); 
				sleep(); 
				running=false; 
				inMotion.setSelected(false); 
			    }
			});
		    t.setDaemon(true);
		    t.start();
		    do {
			actvel.setValue(new Double(getActualVelocity())); 
			try { Thread.sleep(100); } catch (InterruptedException e) {}
		    } while (running);
		    actvel.setValue(new Double(getActualVelocity())); 
		}
		public void stateChanged(ChangeEvent event) {
		    if (running) return;
		    Thread t = new Thread(this);
		    t.setDaemon(true);
		    running=true;
		    t.start();
		}
	    }
	    addChangeListener(new ActvelChangeListener());
	}

	r.add(inMotion);

	vel.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent event) {
		    double v=((Double)vel.getValue()).doubleValue();
		    setVelocity(v);
		}
	    });

	r.setBorder(
		    BorderFactory.createTitledBorder(
						     BorderFactory.createEtchedBorder(),
						     "Velocity"
						     )
		    );
	return r;
    }

    /** Create a JPanel with buttons for controlling the motor.<br>
     * ATTENZIONE! NON E' COMPLETO!!!
     */
    protected JPanel createButtonControlPanel() {
	JPanel r = new JPanel();
	JButton 
	    bFindHome,bSetMotorOn,
	    bSetBrake,bSetMotorMode,bSetOutputMode,
	    bClearErrors;
	r.setLayout(new FlowLayout());
	    
	bFindHome = new JButton("Find home");
	bSetMotorOn = new JButton("Motor ON");
	bSetBrake = new JButton("Brake ON");
	bSetMotorMode = new JButton("Motor mode 1");
	bSetOutputMode = new JButton("DAC");
	bClearErrors = new JButton("Clear error");

	bFindHome.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    findCenter();
		}
	    });
	bSetMotorOn.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    setMotorOn(!getMotorOn());
		}
	    });
	bSetBrake.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    setBrake(!getBrake());
		}
	    });
	bClearErrors.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    clearErrors();
		}
	    });
	
	r.add(bFindHome);
	r.add(bSetMotorOn);
	r.add(bSetBrake);
	r.add(bClearErrors);

	r.setBorder(
		    BorderFactory.createTitledBorder(
						     BorderFactory.createEtchedBorder(),
						     "Motor controls"
						     )
		    );
	return r;
    }


    /** The motor can be controlled by a visual component, with buttons, 
     * sliders and so on. Call this to get the Component (actually, a JPanel).
     * This component displays also many informations. 
     */
    public abstract Component getControlComponent();


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // createMotorDevice()

    /** The C-150.PD motor */
    public static final String C150PD = "C-150.PD";
    /** The C-136.10 motor */
    public static final String C136 = "C-136.10";
    /** The M-126.PD motor */
    public static final String M126PD = "M-126.PD";
    /** A generic motor driven by Mercury */
    public static final String RAW = "Raw";


    /** Call this to build a driver for a given motor, instead of calling the constructor.
     * @param t the type of motor connected to the board (see RAW,C150PD, C136, M126PD).
     * @param p the number of the ttyS port
     * @param rfi the input file connected to the port
     * @param rfo the output file connected to the port
     * @param n the number of the axis
     * @param gf if there's a mechanical device such as a gear, gf is the ratio final motion/motor motion. gf=1 for RAW.
     */
    public static Mercury createMotorDevice(String t,int p, DataInput rfi, DataOutput rfo, int n, double gf) {
	if (gf==0.0) throw(new IllegalArgumentException("Gear factor = 0"));
	if (!( t.equals(RAW) || t.equals(C150PD) || t.equals(C136) || t.equals(M126PD) ))
	    throw(new IllegalArgumentException("Motor type not supported"));
	if ( t.equals(RAW) && gf!=1.0 ) throw(new IllegalArgumentException("Gear factor must be 1.0 for Row motor type"));
	Mercury c = null;
	final JPanel ccomp = new JPanel();
	final double rpc;
	final double vpc;
	final double maxv;
	final String propname = "vel-"+p+"-"+n+"-"+t;
	final String posUnit;
	final String velUnit;
	if (t.equals(RAW)) {
	    rpc = 1.0;
	    vpc = 1.0;
	    maxv = Double.POSITIVE_INFINITY;
	    posUnit = "counts";
	    velUnit = "counts/s";
	}
	else if (t.equals(C150PD)) {
	    rpc = gf/4000.0;
	    vpc = gf/4000.0;
	    maxv = 100.0 * Math.abs(gf);
	    posUnit = "turns";
	    velUnit = "turns/s";
	} 
	else if (t.equals(C136)) {
	    rpc = gf / 2000.0;
	    vpc = gf / 2000.0;
	    maxv = 3.3 * Math.abs(gf) ;
	    posUnit = "turns";
	    velUnit = "turns/s";
	}
	else {
	    if (! t.equals(M126PD)) throw new IllegalArgumentException("Unknown motor type: "+t);
	    rpc = 0.5/4000.0 * gf;
	    vpc = 0.5/4000.0 * gf;
	    maxv = 15.0 * Math.abs(gf);
	    posUnit = "mm";
	    velUnit = "mm/s";
	}
	
	c = new Mercury(p, rfi, rfo, n) { 
		public void setPosition(double p) { if (p>=getMinimum() && p<=getMaximum()) super.setPosition(p/rpc); }
		public double getPosition() { return super.getPosition()*rpc; }
		public double getActualPosition() { return super.getActualPosition()*rpc; }
		public double getMinimum() { return rpc*((rpc>0.0)?super.getMinimum():super.getMaximum()); }
		public double getMaximum() { return rpc*((rpc>0.0)?super.getMaximum():super.getMinimum()); }
		public void setMinimum(double m) { if (rpc>0.0) super.setMinimum(m/rpc); else super.setMaximum(m/rpc); }
		public void setMaximum(double m) { if (rpc>0.0) super.setMaximum(m/rpc); else super.setMinimum(m/rpc); }
		public String getUnit() { return posUnit; }
		public double getMinorStep() { return super.getMinorStep()*Math.abs(rpc);}
		public double getMajorStep() { return super.getMajorStep()*Math.abs(rpc);}
		public double getPositionError() { return super.getPositionError()*rpc; }
		public void setVelocity(double v) {
		    if (v>0.0 && v<getMaxVelocity()) {
			super.setVelocity(v/Math.abs(vpc)); 
			super.persPref.putDouble(propname,v); 
		    }
		}
		public double getVelocity() { return super.getVelocity()*Math.abs(vpc); }
		public double getActualVelocity() { return super.getActualVelocity()*vpc; }
		public double getMaxVelocity() { return maxv; }
		public double getVelocityStep() { return super.getVelocityStep()*Math.abs(vpc); }
		public String getVelocityUnit() { return velUnit; }
		public Component getControlComponent() { return ccomp; }
	    };
	if (t.equals(M126PD)) c.setLimitSwitches(true);
	if ( ! t.equals(RAW)) c.setMotorOn(true);

	if (!(c.getPosition()>=c.getMinimum() && c.getPosition()<=c.getMaximum())) throw new IllegalArgumentException("( Minimum <= Position <= Maximum ) failed");

	c.type=t;
	c.setVelocity(c.persPref.getDouble("vel-"+c.port+"-"+c.axis+"-"+c.type,0.1));

	ccomp.setLayout(new BorderLayout());
	if (t.equals(M126PD)) ccomp.add("North",c.createPositionPanel(true,true,false));
	else ccomp.add("North",c.createPositionPanel(true,false,false));
	ccomp.add("Center",c.createVelocityPanel(true));
	//ccomp.add("South",c.createButtonControlPanel());

	ccomp.setBorder(
			BorderFactory.createTitledBorder(
							 BorderFactory.createEtchedBorder(),
							 "ttyS"+p+" Axis "+n+" "+t
							 )
			);
	return c;
    }


    /** Call this to build a driver for a given motor, instead of calling the constructor.
     * @param t the type of motor connected to the board (see RAW,C150PD, C136, M126PD).
     * @param p the number of the ttyS port
     * @param n the number of the axis
     * @param gf if there's a mechanical device such as a gear, gf is the ratio final motion/motor motion. gf=1 for RAW.
     */
    public static Mercury createMotorDevice(String t, int p, int n, double gf) {
	RandomAccessFile raf = null;
	try {
	    raf = new RandomAccessFile("/dev/ttyS"+p,"rw");
	}
      catch (FileNotFoundException e) { 
	    throw new IllegalArgumentException("/dev/ttyS"+p+"does not exist"); 
	}
	return createMotorDevice(t, p, raf, raf, n, gf);
    }

    /** Call this to build a driver for a given motor, instead of calling the constructor.
     * @param t the type of motor connected to the board (see RAW,C150PD, C136, M126PD).
     * @param p the number of the ttyS port
     * @param n the number of the axis
     */
    public static Mercury createMotorDevice(String t,int p, int n) {
	RandomAccessFile raf = null;
	try {
	    raf = new RandomAccessFile("/dev/ttyS"+p,"rw");
	}
      catch (FileNotFoundException e) { 
	    throw new IllegalArgumentException("/dev/ttyS"+p+"does not exist"); 
	}
	return createMotorDevice(t, p, raf, raf, n, 1.0);
    }

    

    /** Report port number, device number and version */
    public String toString() {
	return getClass().getName()+
            "[\nPort=ttyS"+port+",axis="+axis+",type="+type+",version="+version+"]";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Main

 
    private static void printHelp() {
	System.out.println(
			   "Useful controls for the Mercury board. Parameters can be supplied.\n"+
			   "Syntax:\n"+
			   "java it.unimi.pi.Mercury {options} {commands}\n"+
			   "The options can be:\n"+
			   "-h : print the help\n"+
			   "-m type : set the motor type (\"Raw\" by default)\n"+
			   "-p ttyS_number : set the ttyS port number (0 by default)\n"+
			   "-n axis_number : set the axis number (0 by default)\n"+
			   "-g gear_factor : set the gear factor (1 by default)\n"+
			   "The commands can be:\n"+
			   "setMinimum position : set the minimum of the position (only root can do this)\n"+
			   "setMaximum position : set the maximum of the position (only root can do this)\n"+
			   "setPosition position : move the motor to the given position\n"+
			   "setLimits : graphical interface for setting limits of the given motor (only root can do this)\n"+
			   "monitor : open a window with the control component for the desired motor.\n"+
			   "motors : print a list of known motor types. \"Raw\" is a generic motor, with the\n"+
			   "units used by the board.\n"
			   );
	System.exit(0);
    }

    /** Useful controls for the Mercury board. Parameters can be supplied.<br>
     * Syntax:<br>
     * <code>java it.unimi.pi.Mercury {options} {commands}</code><br>
     * The options can be:<br>
     * -h : print the help<br>
     * -m type : set the motor type ("Raw" by default)<br>
     * -p ttyS_number : set the ttyS port number (0 by default)<br>
     * -n axis_number : set the axis number (0 by default)<br>
     * -g gear_factor : set the gear factor (1 by default)<br>
     * The commands can be:<br>
     * setMinimum position : set the minimum of the position (only root can do this)<br>
     * setMaximum position : set the maximum of the position (only root can do this)<br>
     * setPosition position : move the motor to the given position<br>
     * setLimits : graphical interface for setting limits of the given motor (only root can do this)<br>
     * monitor : open a window with the control component for the desired motor.<br>
     * motors : print a list of known motor types. "Raw" is a generic motor, with the
     * units used by the board.<br>
     */
    public static void main(String [] args) {
	String t="Raw";
	int a=0;
	double gf=1.0;
	int p=0;
	int n=0;
	while (n<args.length) {
	    if (args[n].equals("-m")) {
		n++;
		if (n>=args.length) printHelp();
		t=args[n];
	    } else
		if (args[n].equals("-p")) {
		    n++;
		    if (n>=args.length) printHelp();
		    p=Integer.parseInt(args[n]);
		} else 
		    if (args[n].equals("-n")) {
			n++;
			if (n>=args.length) printHelp();
			a=Integer.parseInt(args[n]);
		    } else 
			if (args[n].equals("-g")) {
			    n++;
			    if (n>=args.length) printHelp();
			    gf=Double.parseDouble(args[n]);
			} else 
			    if (args[n].equals("-h")) printHelp();
			    else break;
	    n++;
	}

	Mercury c = createMotorDevice(t,p,a,gf);


	while (n<args.length) {
	    if (args[n].equals("setMinimum")) {
		n++;
		if (n>=args.length) printHelp();
		c.setMinimum(Double.parseDouble(args[n]));
	    } else
		if (args[n].equals("setMaximum")) {
		    n++;
		    if (n>=args.length) printHelp();
		    c.setMaximum(Double.parseDouble(args[n]));
		} else 
		    if (args[n].equals("setPosition")) {
			n++;
			if (n>=args.length) printHelp();
			c.setPosition(Double.parseDouble(args[n]));
		    } else 
			if (args[n].equals("setLimits"))
			    c.setLimits();
			else 
			    if (args[n].equals("monitor")) {
				JFrame frm = new JFrame("Mercury");
				frm.getContentPane().add(c.getControlComponent());
				frm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frm.pack();
				frm.show();
			    } else 
				if (args[n].equals("motors")) {
				    System.out.println("Supported motor types:\nRaw\nC-150.PD\nC-136.10\nM-126.PD\n\n");
				} else printHelp();
	    n++;
	}
    }
	

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Graphical configuration of the limits
    
    JFrame setLimitsDialog = null;
    /** runs a graphical configuration of the limits. Only root can do this.
     * This operation allows the movement of the device outside the current limits; this could
     * damage the device.
     */
    public void setLimits() {
	setLimitsDialog = new JFrame("Axis "+axis+" "+type+" Set limits");
	final JSpinner min = new JSpinner(new SpinnerNumberModel(getMinimum(),Double.NEGATIVE_INFINITY,getPosition(),getMinorStep()));
	final JSpinner pos = new JSpinner(new SpinnerNumberModel(getPosition(),getMinimum(),getMaximum(),getMinorStep()));
	final JSpinner max = new JSpinner(new SpinnerNumberModel(getMaximum(),getPosition(),Double.POSITIVE_INFINITY,getMinorStep()));
	final JRadioButton rbPos = new JRadioButton("None",true);
	final JRadioButton rbMin = new JRadioButton("Min",false);
	final JRadioButton rbMax = new JRadioButton("Max",false);
	JButton toMin = new JButton("Move to min");
	JButton toMax = new JButton("Move to max");
	ButtonGroup bg = new ButtonGroup();
	bg.add(rbPos);bg.add(rbMin);bg.add(rbMax);

	min.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
		    double v = ((Double)min.getValue()).doubleValue();
		    setMinimum(v);
		    if (rbPos.isSelected())
			((SpinnerNumberModel)pos.getModel()).setMinimum(new Double(getMinimum()));
		    if (rbMin.isSelected()) {
			pos.setValue(new Double(getMinimum()));
			((SpinnerNumberModel)pos.getModel()).setMinimum(new Double(getMinimum()));
			((SpinnerNumberModel)pos.getModel()).setMaximum(new Double(getMinimum()));
			((SpinnerNumberModel)max.getModel()).setMinimum(new Double(getMinimum()));
		    }
		    if (rbMax.isSelected())
			((SpinnerNumberModel)max.getModel()).setMinimum(new Double(getMinimum()));
		}
	    });
	pos.addChangeListener(new ChangeListener() {
		private double currPos = 0.0;
		public void stateChanged(ChangeEvent e) {
		    if (currPos == 0.0) currPos = getPosition();
		    double v = ((Double)pos.getValue()).doubleValue();
		    if (v==currPos) return;
		    currPos=v;
		    setPosition(v);
		    if (rbPos.isSelected()) {
			((SpinnerNumberModel)min.getModel()).setMaximum(new Double(getPosition()));
			((SpinnerNumberModel)max.getModel()).setMinimum(new Double(getPosition()));
		    }
		}
	    });
	max.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
		    double v = ((Double)max.getValue()).doubleValue();
		    setMaximum(v);
		    if (rbPos.isSelected())
			((SpinnerNumberModel)pos.getModel()).setMaximum(new Double(getMaximum()));
		    if (rbMin.isSelected())
			((SpinnerNumberModel)min.getModel()).setMaximum(new Double(getMaximum()));
		    if (rbMax.isSelected()) {
			pos.setValue(new Double(getMaximum()));
			((SpinnerNumberModel)pos.getModel()).setMinimum(new Double(getMaximum()));
			((SpinnerNumberModel)pos.getModel()).setMaximum(new Double(getMaximum()));
			((SpinnerNumberModel)min.getModel()).setMaximum(new Double(getMaximum()));
		    }
		}
	    });

	rbPos.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    ((SpinnerNumberModel)min.getModel()).setMaximum(new Double(getPosition()));
		    ((SpinnerNumberModel)max.getModel()).setMinimum(new Double(getPosition()));
		    ((SpinnerNumberModel)pos.getModel()).setMinimum(new Double(getMinimum()));
		    ((SpinnerNumberModel)pos.getModel()).setMaximum(new Double(getMaximum()));
		}
	    });
	rbMin.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    pos.setValue(new Double(getMinimum()));
		    ((SpinnerNumberModel)min.getModel()).setMaximum(new Double(getMaximum()));
		    ((SpinnerNumberModel)pos.getModel()).setMinimum(new Double(getMinimum()));
		    ((SpinnerNumberModel)pos.getModel()).setMaximum(new Double(getMinimum()));
		    ((SpinnerNumberModel)max.getModel()).setMinimum(new Double(getMinimum()));
		}
	    });
	rbMax.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    pos.setValue(new Double(getMaximum()));
		    ((SpinnerNumberModel)max.getModel()).setMinimum(new Double(getMinimum()));
		    ((SpinnerNumberModel)pos.getModel()).setMinimum(new Double(getMaximum()));
		    ((SpinnerNumberModel)pos.getModel()).setMaximum(new Double(getMaximum()));
		    ((SpinnerNumberModel)min.getModel()).setMaximum(new Double(getMaximum()));
		}
	    });

	toMin.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (rbPos.isSelected()) 
			pos.setValue(new Double(getMinimum()));
		}
	    });
	toMax.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (rbPos.isSelected()) 
			pos.setValue(new Double(getMaximum()));
		}
	    });

	Dimension dim = min.getPreferredSize();
	dim = new Dimension(100,(int)dim.getHeight());
	min.setPreferredSize(dim);
	dim = pos.getPreferredSize();
	dim = new Dimension(100,(int)dim.getHeight());
	pos.setPreferredSize(dim);
	dim = max.getPreferredSize();
	dim = new Dimension(100,(int)dim.getHeight());
	max.setPreferredSize(dim);

	setLimitsDialog.getContentPane().setLayout(new GridBagLayout());
	GridBagConstraints constr = new GridBagConstraints();
	constr.fill=GridBagConstraints.NONE;
	constr.anchor=GridBagConstraints.EAST;
	constr.weightx=0;
	constr.weighty=0;

	constr.gridx=0;constr.gridy=0;constr.gridwidth=1;constr.gridheight=1;
	setLimitsDialog.getContentPane().add(new JLabel("Maximum:"),constr);
	constr.gridx=1;constr.gridy=0;constr.gridwidth=1;constr.gridheight=1;
	setLimitsDialog.getContentPane().add(max,constr);
	constr.gridx=2;constr.gridy=0;constr.gridwidth=1;constr.gridheight=1;
	setLimitsDialog.getContentPane().add(new JLabel(getUnit()),constr);
	constr.gridx=0;constr.gridy=1;constr.gridwidth=1;constr.gridheight=1;
	setLimitsDialog.getContentPane().add(new JLabel("Position:"),constr);
	constr.gridx=1;constr.gridy=1;constr.gridwidth=1;constr.gridheight=1;
	setLimitsDialog.getContentPane().add(pos,constr);
	constr.gridx=2;constr.gridy=1;constr.gridwidth=1;constr.gridheight=1;
	setLimitsDialog.getContentPane().add(new JLabel(getUnit()),constr);
	constr.gridx=0;constr.gridy=2;constr.gridwidth=1;constr.gridheight=1;
	setLimitsDialog.getContentPane().add(new JLabel("Minimum:"),constr);
	constr.gridx=1;constr.gridy=2;constr.gridwidth=1;constr.gridheight=1;
	setLimitsDialog.getContentPane().add(min,constr);
	constr.gridx=2;constr.gridy=2;constr.gridwidth=1;constr.gridheight=1;
	setLimitsDialog.getContentPane().add(new JLabel(getUnit()),constr);
	constr.gridx=0;constr.gridy=3;constr.gridwidth=1;constr.gridheight=1;
	setLimitsDialog.getContentPane().add(new JLabel("Anchor to: "),constr);
	constr.anchor=GridBagConstraints.WEST;
	constr.gridx=1;constr.gridy=4;constr.gridwidth=1;constr.gridheight=1;
	setLimitsDialog.getContentPane().add(rbPos,constr);
	constr.gridx=1;constr.gridy=5;constr.gridwidth=1;constr.gridheight=1;
	setLimitsDialog.getContentPane().add(rbMin,constr);
	constr.gridx=1;constr.gridy=6;constr.gridwidth=1;constr.gridheight=1;
	setLimitsDialog.getContentPane().add(rbMax,constr);
	constr.anchor=GridBagConstraints.CENTER;
	constr.gridx=0;constr.gridy=7;constr.gridwidth=1;constr.gridheight=1;
	setLimitsDialog.getContentPane().add(toMin,constr);
	constr.gridx=1;constr.gridy=7;constr.gridwidth=1;constr.gridheight=1;
	setLimitsDialog.getContentPane().add(toMax,constr);

	setLimitsDialog.pack();
	setLimitsDialog.setResizable(false);
	setLimitsDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	setLimitsDialog.setVisible(true);
    }



}

