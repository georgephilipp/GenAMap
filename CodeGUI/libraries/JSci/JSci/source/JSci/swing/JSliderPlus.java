package JSci.swing;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.text.*;
import java.util.*;

/** A place where <code>JPointer</code>s can be placed. 
    <code>JPointer</code>s must be added. */
public class JSliderPlus extends JPanel {

    private double startValue;	
    private double endValue;
    private double minorTic = 10;
    private double majorTic = 20;
    private boolean setPaintMinorTicks = true;
    private boolean setPaintMajorTicks = true;
    private boolean setPaintLabels = true;
    private ArrayList sliders = new ArrayList();
    private ChangeListener changeListener = new ChangeListener() {	
	    public void stateChanged(ChangeEvent event) {	
		repaint();
	    }
	};

    /** For building a vertical SliderHolder */
    public static final int VERTICAL = 0;
    /** For building a horizontal SliderHolder */
    public static final int HORIZONTAL = 1;

    /** constructs a SliderHolder with no sliders; the sliders will move
     * inside a given range.
     * @param o orientation (currently only VERTICAL)
     * @param start starting value of the SliderHolder
     * @param end end value of the SliderHolder 
     */
    public JSliderPlus(int o,double start,double end) {
	startValue=start;
	endValue=end;
	setSize(getPreferredSize());
	updateMinorTics();
	updateMajorTics();
	addMouseListener(new TheMouseListener());
	addMouseMotionListener(new TheMouseMotionListener());
    }

    /** set the limits of the Slider
     * @param start starting value of the SliderHolder
     * @param end end value of the SliderHolder
     */
    public void setLimits(double start,double end) {
	startValue=start;
	endValue=end;
	setSize(getPreferredSize());
	updateMinorTics();
	updateMajorTics();
    }

    /** as described in java.awt.Component */
    public Dimension getPreferredSize() { 
	return new Dimension(100,200); 
    }

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
	double width = getSize().getWidth();
	double heigth = getSize().getHeight();
	int N =(int)Math.abs((endValue-startValue)/minorTic);
	minorTics =  new Line2D[N+1];
	for (int j=0;j<=N;j++)
	    minorTics[j] = new Line2D.Double(
					     width*0.3,heigth*(0.9-j*0.8/N),
					     width*0.4,heigth*(0.9-j*0.8/N)
					     );
    }

    private void updateMajorTics() {
	double width = getSize().getWidth();
	double heigth = getSize().getHeight();
	int N =(int)Math.abs((endValue-startValue)/majorTic);
	majorTics =  new Line2D[N+1];
	ticLabelPos = new Point2D[N+1];
	ticLabelText = new String[N+1];
	for (int j=0;j<=N;j++) {
	    majorTics[j] = 
		new Line2D.Double(
				  width*0.25,heigth*(0.9-j*0.8/N),
				  width*0.4,heigth*(0.9-j*0.8/N)
				  );
	    ticLabelText[j] = formatter.format(startValue+j*(endValue-startValue)/N);
	    ticLabelPos[j] = 
		new Point2D.Double(0.0,heigth*(0.9-j*0.8/N)+6);
	}
    }


    /** draw the component - don't call this directly, but use repaint() */
    public void paintComponent(Graphics g) {
	super.paintComponent(g);
	Graphics2D g2 = (Graphics2D)g;
	g2.draw(new Rectangle2D.Double(
				       getSize().getWidth()*0.45,getSize().getHeight()*0.05,
				       getSize().getWidth()*0.1,getSize().getHeight()*0.9)
		);
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
	for (int j=0;j<sliders.size();j++)
	    ((JPointer)sliders.get(j)).paintOnSlider(g2,startValue,endValue,getSize().getWidth(),getSize().getHeight());
	g2.setColor(saveColor);
    }

    /** add a slider; it will be the actual component that contains informations 
     * @param p the slider that must be added
     */
    public void addJPointer(JPointer p) {
	for (int j=0;j<sliders.size();j++) if (sliders.get(j).equals(p)) return;
	sliders.add(p);
	p.addChangeListener(changeListener);
	repaint();
    }

    /** remove a slider; it should have been added with addJPointer()
     * @param p the slider that must be removed
     */
    public void removeJPointer(JPointer p) {
	int j=0;
	while (j<sliders.size()) { 
	    if (sliders.get(j).equals(p)) sliders.remove(j);
	    else j++;
	}
	p.removeChangeListener(changeListener);
	repaint();
    }

    // Mouse listeners

    private JPointer dragged = null;
    private double dragDelta;
    private double mousePosition(MouseEvent e) {
	return startValue+(0.9-e.getY()/getSize().getHeight())/0.8*(endValue-startValue);
    }
    private class TheMouseListener extends MouseAdapter {
	public void mousePressed(MouseEvent e) {
	    for (int j=sliders.size()-1;j>=0;j--)
		if (((JPointer)sliders.get(j)).contains(e.getPoint())) {
		    dragged=(JPointer)sliders.get(j);
		    dragDelta=dragged.getValue()-mousePosition(e);
		    dragged.setAdjusting(true);
		    break;
		}
	}
	public void mouseReleased(MouseEvent e) {
	    if (dragged!=null) dragged.setAdjusting(false);
	    dragged = null;
	    boolean inside = false;
	    for (int j=0;j<sliders.size();j++)
		if (((JPointer)sliders.get(j)).contains(e.getPoint())) {
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
	    for (int j=0;j<sliders.size();j++)
		if (((JPointer)sliders.get(j)).contains(e.getPoint())) {
		    inside=true;
		    break;
		}
	    if (inside) setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	    else setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	public void mouseDragged(MouseEvent e) {
	    if (dragged!=null) {
		double to = (mousePosition(e)+dragDelta);
		if (to>Math.max(startValue,endValue)) to=Math.max(startValue,endValue);
		if (to<Math.min(startValue,endValue)) to=Math.min(startValue,endValue);
		dragged.setValue(to);
		repaint();
	    }
	}
    }



    // Main

    public static void main(String [] args) {
	JFrame frm = new JFrame();
	frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	JSliderPlus d = new JSliderPlus(JSliderPlus.VERTICAL,0.0,100.0);
	frm.getContentPane().add(d);
	frm.pack();
	frm.show();
		
	try { Thread.sleep(3000); } catch (InterruptedException e) {}
	JPointer p = new JPointer(JPointer.SLIDER_SIMPLE_QUADRANGLE);
	p.setColor(Color.MAGENTA);
	p.setValue(25.0);
	d.addJPointer(p);
	try { Thread.sleep(3000); } catch (InterruptedException e) {}
	JPointer p1 = new JPointer(JPointer.SLIDER_SIMPLE_TRIANGLE);
	p1.setColor(Color.CYAN);
	p1.setValue(75.0);
	d.addJPointer(p1);
		
	p1.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent e) {	
		    double v=((JPointer)e.getSource()).getValue();
		    System.out.println("State changed: "+v);
		}
	    });

	d.setLimits(-10.0,140.0);
	d.setMinorTickSpacing(10.0);
	d.setMajorTickSpacing(30.0);

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
