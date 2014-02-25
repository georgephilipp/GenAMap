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
public class JRoundDial extends JPanel {

    private double oneTurn = 360;
    private double zero = 0.0;
    private double minorTic = 10;
    private double majorTic = 30;
    private boolean setPaintMinorTicks = true;
    private boolean setPaintMajorTicks = true;
    private boolean setPaintLabels = true;
    private JPointer setPaintTurns = null;
    private double radius = 70;
    private ArrayList pointers = new ArrayList();
    private ChangeListener changeListener = new ChangeListener() {	
	    public void stateChanged(ChangeEvent event) {	
		repaint();
	    }
	};

    /** constructs a dial with no pointers 
     * @param o the value that corresponds to one turn 
     *          (e.g. 360 for measures in degrees); negative values for 
     *          counter-clockwise dial
     */
    public JRoundDial(double o) {
	oneTurn = o;
	setSize((int)(2*radius),(int)(2*radius));
	addMouseListener(new TheMouseListener());
	addMouseMotionListener(new TheMouseMotionListener());
    }

    /** define the position at which the zero of the dial will be placed
     * @param z the angle (from the top, clockwise, in rads) where the zero of
     *          the dial will be placed
     */
    public void setZero(double z) {
	zero = z;
    }

    /** as described in java.awt.Component */
    public void setSize(int w,int h) {
	super.setSize(w,h);
	radius = (w<h)?w/2:h/2;
	updateMinorTics();
	updateMajorTics();
	repaint();
    }

    /** as described in java.awt.Component */
    public void setSize(Dimension d) { super.setSize(d); setSize((int)d.getWidth(),(int)d.getHeight()); }
	
    /** as described in java.awt.Component */
    public Dimension getPreferredSize() { return new Dimension((int)(2*radius),(int)(2*radius)); }

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

    /** show the integer turn number of a JPointer
     * @param p the JPointer that contains the number of turns; it must be
     *          already added as a JPointer
     */
    public void setPaintTurns(JPointer p) {
	setPaintTurns = p;
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
	int N =(int)Math.abs(oneTurn/minorTic);
	minorTics =  new Line2D[N];
	for (int j=0;j<N;j++)
	    minorTics[j] = 
		new Line2D.Double(
				  radius+radius*0.65*Math.sin(zero+2.0*Math.PI*j*minorTic/oneTurn),
				  radius-radius*0.65*Math.cos(zero+2.0*Math.PI*j*minorTic/oneTurn),
				  radius+radius*0.75*Math.sin(zero+2.0*Math.PI*j*minorTic/oneTurn),
				  radius-radius*0.75*Math.cos(zero+2.0*Math.PI*j*minorTic/oneTurn)
				  );
    }

    private void updateMajorTics() {
	int N =(int)Math.abs(oneTurn/majorTic);
	majorTics =  new Line2D[N];
	ticLabelPos = new Point2D[N];
	ticLabelText = new String[N];
	for (int j=0;j<N;j++) {
	    majorTics[j] = 
		new Line2D.Double(
				  radius+radius*0.65*Math.sin(zero+2.0*Math.PI*j*majorTic/oneTurn),
				  radius-radius*0.65*Math.cos(zero+2.0*Math.PI*j*majorTic/oneTurn),
				  radius+radius*0.8*Math.sin(zero+2.0*Math.PI*j*majorTic/oneTurn),
				  radius-radius*0.8*Math.cos(zero+2.0*Math.PI*j*majorTic/oneTurn)
				  );
	    ticLabelText[j] = formatter.format(j*majorTic);
	    ticLabelPos[j] = 
		new Point2D.Double(
				   radius+radius*0.9*Math.sin(zero+2.0*Math.PI*j*majorTic/oneTurn)
				   -ticLabelText[j].length()*3,
				   radius-radius*0.9*Math.cos(zero+2.0*Math.PI*j*majorTic/oneTurn)
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
	if (setPaintTurns!=null) { 
	    String t = formatter.format(Math.floor(setPaintTurns.getValue()/Math.abs(oneTurn)));
	    g2.drawString(t,(int)(radius-t.length()*3),(int)(radius/2));
	}
	Color saveColor = g2.getColor();
	for (int j=0;j<pointers.size();j++)
	    ((JPointer)pointers.get(j)).paintOnDial(g2,radius,oneTurn,zero,radius,radius);
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
		double from = dragged.getValue();
		double to = (theta(e)+dragDelta)*oneTurn/2.0/Math.PI;
		to += (from-to)-(from-to)%oneTurn;
		while (to<from-Math.abs(oneTurn/2)) to+=Math.abs(oneTurn);
		while (to>from+Math.abs(oneTurn/2)) to-=Math.abs(oneTurn);
		dragged.setValue(to);
		repaint();
	    }
	}
    }



    // Main

    public static void main(String [] args) {
	JFrame frm = new JFrame();
	frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	JRoundDial d = new JRoundDial(360.0);
	d.setZero(Math.PI/6.0);
	frm.getContentPane().add(d);
	d.setSize(260,260);
	frm.pack();
	frm.show();
	try { Thread.sleep(3000); } catch (InterruptedException e) {}
	JPointer p = new JPointer(JPointer.POINTER_SIMPLE_QUADRANGLE);
	p.setColor(Color.MAGENTA);
	p.setValue(20.0);
	d.addJPointer(p);
	try { Thread.sleep(3000); } catch (InterruptedException e) {}
	JPointer p1 = new JPointer(JPointer.POINTER_SIMPLE_STOP);
	p1.setColor(Color.CYAN);
	p1.setValue(130.0);
	d.addJPointer(p1);
		
	d.setPaintTurns(p1);d.repaint();

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
