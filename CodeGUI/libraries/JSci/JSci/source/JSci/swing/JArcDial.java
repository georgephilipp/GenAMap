package JSci.swing;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.text.*;
import java.util.*;

/** A round dial, where the pointer can rotate many turns; the turns are indicated 
    by digits inside the dial. JPointer s must be added. */
public class JArcDial extends JPanel {

    private double oneTurn;
    private double zero;
    private double startValue;	
    private double endValue;
    private double startAngle = -2*Math.PI/6;
    private double endAngle = 2*Math.PI/6;
    // form of the bounding box
    private double ha;
    private double hb;
    private double va;
    private double vb;
    private double minorTic = 5;
    private double majorTic = 20;
    private boolean setPaintMinorTicks = true;
    private boolean setPaintMajorTicks = true;
    private boolean setPaintLabels = true;
    private double radius = 70;
    private ArrayList pointers = new ArrayList();
    private ChangeListener changeListener = new ChangeListener() {	
	    public void stateChanged(ChangeEvent event) {	
		repaint();
	    }
	};

    /** constructs a dial with no pointers; the pointers will move
     * inside a given range.
     * @param start starting value of the dial
     * @param end end value of the dial 
     */
    public JArcDial(double start,double end) {
	startValue=start;
	endValue=end;
	oneTurn = (endValue-startValue)*2.0*Math.PI/(endAngle-startAngle);
	zero = startAngle-2.0*Math.PI*startValue/oneTurn;
	setArc(-2*Math.PI/6,2*Math.PI/6);
	setSize((int)(2*radius),(int)(2*radius));
	addMouseListener(new TheMouseListener());
	addMouseMotionListener(new TheMouseMotionListener());
    }

    /** Define the positions at which the the dial scale will start and stop.
     * The angles must be such that stop>start.
     * Counter-clockwise dials can be created by inverting the start and stop values
     * (not angles!) in the constructor.
     * @param start the angle (from the top, clockwise, in rads) where the
     *          scale start
     * @param stop  the angle (from the top, clockwise, in rads) where the
     *          scale will end
     */
    public void setArc(double start,double stop) {
	if (start>stop) { throw new IllegalArgumentException("start angle >stop angle"); }
	startAngle = start;
	endAngle = stop;
	oneTurn = (endValue-startValue)*2.0*Math.PI/(endAngle-startAngle);
	zero = startAngle-2.0*Math.PI*startValue/oneTurn;

	double f;

	f=-Math.PI/2+2.0*Math.PI*Math.round(start);
	while (f>start) f-=2.0*Math.PI;while (f<start) f+=2.0*Math.PI;
	if (f<stop) ha=1.0;
	else ha=Math.max(-Math.sin(start)+0.1,-Math.sin(stop)+0.1);

	f=Math.PI/2+2.0*Math.PI*Math.round(start);
	while (f>start) f-=2.0*Math.PI;while (f<start) f+=2.0*Math.PI;
	if (f<stop) hb=1.0;
	else hb=Math.max(Math.sin(start)+0.1,Math.sin(stop)+0.1);

	f=2.0*Math.PI*Math.round(start);
	while (f>start) f-=2.0*Math.PI;while (f<start) f+=2.0*Math.PI;
	if (f<stop) va=1.0;
	else va=Math.max(Math.cos(start)+0.1,Math.cos(stop)+0.1);

	f=Math.PI+2.0*Math.PI*Math.round(start);
	while (f>start) f-=2.0*Math.PI;while (f<start) f+=2.0*Math.PI;
	if (f<stop) vb=1.0;
	else vb=Math.max(-Math.cos(start)+0.1,-Math.cos(stop)+0.1);

	setSize((int)((ha+hb)*radius),(int)((va+vb)*radius));
	repaint();
    }

    /** as described in java.awt.Component */
    public void setSize(int w,int h) {
	super.setSize(w,h);
	radius = Math.min(w/(ha+hb),h/(va+vb));
	updateMinorTics();
	updateMajorTics();
	repaint();
    }

    /** as described in java.awt.Component */
    public void setSize(Dimension d) { super.setSize(d); setSize((int)d.getWidth(),(int)d.getHeight()); }
	
    /** as described in java.awt.Component */
    public Dimension getPreferredSize() { return new Dimension((int)((ha+hb)*radius),(int)((va+vb)*radius)); }

    /** set the spacing between minor tics
     * @param s the spacing between minor tics 
     */
    public void setMinorTickSpacing(double s) {
	minorTic = s;
	updateMinorTics();
	repaint();
    }

    /** set the spacing between major tics
     * @param s the spacing between major tics 
     */
    public void setMajorTickSpacing(double s) {
	majorTic = s;
	updateMajorTics();
	repaint();
    }

    /** show the minor tics?
     * @param b show the minor tics? 
     */
    public void setPaintMinorTicks(boolean b) {
	setPaintMinorTicks = b;
	repaint();
    }

    /** show the major tics?
     * @param b show the major tics? 
     */
    public void setPaintMajorTicks(boolean b) {
	setPaintMajorTicks = b;
	repaint();
    }

    /** show the tic labels?
     * @param b show the tic labels? 
     */
    public void setPaintLabels(boolean b) {
	setPaintLabels = b;
	repaint();
    }

    private Line2D [] minorTics = null;
    private Line2D [] majorTics = null;
    private Point2D [] ticLabelPos = null;
    private String [] ticLabelText = null;
    private static NumberFormat formatter; 
    static {
	formatter = NumberFormat.getNumberInstance();
	formatter.setMaximumFractionDigits(2);
	formatter.setMaximumIntegerDigits(3);
    }

    private void updateMinorTics() {
	int N =(int)Math.abs((endValue-startValue)/minorTic);
	double from = (startValue<endValue)?startValue:endValue;
	minorTics =  new Line2D[N];
	for (int j=0;j<N;j++)
	    minorTics[j] = 
		new Line2D.Double(
				  radius+radius*0.65*Math.sin(zero+2.0*Math.PI*(from+j*minorTic)/oneTurn),
				  radius-radius*0.65*Math.cos(zero+2.0*Math.PI*(from+j*minorTic)/oneTurn),
				  radius+radius*0.75*Math.sin(zero+2.0*Math.PI*(from+j*minorTic)/oneTurn),
				  radius-radius*0.75*Math.cos(zero+2.0*Math.PI*(from+j*minorTic)/oneTurn)
				  );
    }

    private void updateMajorTics() {
	int N =(int)Math.abs((endValue-startValue)/majorTic)+1;
	double from = (startValue<endValue)?startValue:endValue;
	majorTics =  new Line2D[N];
	ticLabelPos = new Point2D[N];
	ticLabelText = new String[N];
	for (int j=0;j<N;j++) {
	    majorTics[j] = 
		new Line2D.Double(
				  radius+radius*0.65*Math.sin(zero+2.0*Math.PI*(from+j*majorTic)/oneTurn),
				  radius-radius*0.65*Math.cos(zero+2.0*Math.PI*(from+j*majorTic)/oneTurn),
				  radius+radius*0.8*Math.sin(zero+2.0*Math.PI*(from+j*majorTic)/oneTurn),
				  radius-radius*0.8*Math.cos(zero+2.0*Math.PI*(from+j*majorTic)/oneTurn)
				  );
	    ticLabelText[j] = formatter.format(from+j*majorTic);
	    ticLabelPos[j] = 
		new Point2D.Double(
				   radius+radius*0.9*Math.sin(zero+2.0*Math.PI*(from+j*majorTic)/oneTurn)
				   -ticLabelText[j].length()*3,
				   radius-radius*0.9*Math.cos(zero+2.0*Math.PI*(from+j*majorTic)/oneTurn)
				   +6
				   );
	}

    }


    /** draw the component - don't call this directly, but use repaint() */
    public void paintComponent(Graphics g) {
	super.paintComponent(g);
	Graphics2D g2 = (Graphics2D)g;
	//g2.draw(new Ellipse2D.Double(0,0,2*radius,2*radius));
	if (setPaintMinorTicks) 
	    for (int j=0;j<minorTics.length;j++) 
		g2.draw(minorTics[j]);
	if (setPaintMajorTicks) 
	    for (int j=0;j<majorTics.length;j++) 
		g2.draw(majorTics[j]);	
	if (setPaintLabels)
	    for (int j=0;j<majorTics.length;j++) 
		g2.drawString(ticLabelText[j],(int)ticLabelPos[j].getX(),(int)ticLabelPos[j].getY());
	Color saveColor = g2.getColor();
	for (int j=0;j<pointers.size();j++)
	    ((JPointer)pointers.get(j)).paintOnDial(g2,radius,oneTurn,zero,radius*ha,radius*va);
	g2.setColor(saveColor);
	g2.fill(new Ellipse2D.Double(radius-3.0,radius-3.0,6.0,6.0));
    }

    /** add a pointer; it will be the actual component that contains informations 
     * @param p the pointer that must be added
     */
    public void addJPointer(JPointer p) {
	for (int j=0;j<pointers.size();j++) if (pointers.get(j).equals(p)) return;
	pointers.add(p);
	p.addChangeListener(changeListener);
	repaint();
    }

    /** remove a pointer; it should have been added with addJPointer()
     * @param p the pointer that must be removed
     */
    public void removeJPointer(JPointer p) {
	int j=0;
	while (j<pointers.size()) { 
	    if (pointers.get(j).equals(p)) pointers.remove(j);
	    else j++;
	}
	p.removeChangeListener(changeListener);
	repaint();
    }

    // Mouse listeners

    private JPointer dragged = null;
    private double dragDelta;
    private double theta(MouseEvent e) { return Math.atan2(e.getX()-radius,-(e.getY()-radius)); }
    private class TheMouseListener extends MouseAdapter {
	public void mousePressed(MouseEvent e) {
	    for (int j=pointers.size()-1;j>=0;j--)
		if (((JPointer)pointers.get(j)).contains(e.getPoint())) {
		    dragged=(JPointer)pointers.get(j);
		    dragDelta=2.0*Math.PI*dragged.getValue()/oneTurn-theta(e);
		    dragged.setAdjusting(true);
		    break;
		}
	}
	public void mouseReleased(MouseEvent e) {
	    if (dragged!=null) dragged.setAdjusting(false);
	    dragged = null;
	    boolean inside = false;
	    for (int j=0;j<pointers.size();j++)
		if (((JPointer)pointers.get(j)).contains(e.getPoint())) {
		    inside=true;
		    break;
		}
	    if (inside) setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	    else setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
    }

    private class TheMouseMotionListener extends MouseMotionAdapter {
	public void mouseMoved(MouseEvent e) {
	    boolean inside = false;
	    for (int j=0;j<pointers.size();j++)
		if (((JPointer)pointers.get(j)).contains(e.getPoint())) {
		    inside=true;
		    break;
		}
	    if (inside) setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	    else setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	public void mouseDragged(MouseEvent e) {
	    if (dragged!=null) {
		double to = (theta(e)+dragDelta);
		if (zero+to>endAngle) to=endAngle-zero;
		if (zero+to<startAngle) to=startAngle-zero;
		to*=oneTurn/2.0/Math.PI;
		dragged.setValue(to);
		repaint();
	    }
	}
    }



    // Main

    public static void main(String [] args) {
	JFrame frm = new JFrame();
	frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	JArcDial d = new JArcDial(90.0,10.0);
	d.setArc(-4*Math.PI/6,Math.PI/6);
	frm.getContentPane().add(d);
	d.setSize(260,260);
	frm.pack();
	frm.show();
	try { Thread.sleep(3000); } catch (InterruptedException e) {}
	JPointer p = new JPointer(JPointer.POINTER_SIMPLE_QUADRANGLE);
	p.setColor(Color.MAGENTA);
	p.setValue(25.0);
	d.addJPointer(p);
	try { Thread.sleep(3000); } catch (InterruptedException e) {}
	JPointer p1 = new JPointer(JPointer.POINTER_SIMPLE_TRIANGLE);
	p1.setColor(Color.CYAN);
	p1.setValue(75.0);
	d.addJPointer(p1);
		
	p1.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent e) {	
		    double v=((JPointer)e.getSource()).getValue();
		    System.out.println("State changed: "+v);
		}
	    });

	//try { Thread.sleep(3000); } catch (InterruptedException e) {}
	//p.setValue(p.getValue()+10.0);
	//try { Thread.sleep(3000); } catch (InterruptedException e) {}
	//p.setValue(p.getValue()+10.0);
	//try { Thread.sleep(3000); } catch (InterruptedException e) {}
	//p.setValue(p.getValue()+10.0);
	//try { Thread.sleep(3000); } catch (InterruptedException e) {}
	//p.setValue(p.getValue()+10.0);

	//try { Thread.sleep(3000); } catch (InterruptedException e) {}
	//d.removeJPointer(p);
	//try { Thread.sleep(3000); } catch (InterruptedException e) {}
	//d.removeJPointer(p1);
	//try { Thread.sleep(3000); } catch (InterruptedException e) {}
    }


	
}
