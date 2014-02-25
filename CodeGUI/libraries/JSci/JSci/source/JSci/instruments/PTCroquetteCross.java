package JSci.instruments;

import JSci.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.util.*;
import java.text.*;
import java.awt.image.*;

/** Data about a cross that can be put on an image, follow the
 * center, find the height
*/
public class PTCroquetteCross extends Polygon implements Control {

    private int crossHW=3;
    private int crossL=32;
    private Point2D position = new Point2D.Double();
    private Point intPosition = new Point();
    private double z = 0.0;
    private boolean dragging = true;
    private Rectangle bbox = null;
    private Color color;

    /** 
     * Crates a new cross
     * @param l length of the arm of the cross
     * @param hw half width of the arms of the cross
     * @param p initial center of the cross
     */
    public PTCroquetteCross(int l, int hw, Point p, Color col) {
	super(new int[] {-hw,hw,hw,l,l,hw,hw,-hw,-hw,-l,-l,-hw},
	      new int[] {-l,-l,-hw,-hw,hw,hw,l,l,hw,hw,-hw,-hw},
	      12
	      );
	crossHW=hw;
	crossL=l;
	intPosition.setLocation(p);
	translate(intPosition.x,intPosition.y);
	position.setLocation(p);
	color=col;
	z = 0.0;
	dragging = false;
	calibrating=false;
    }





    private void FFT(double []x,double []y,int d)
    {
	int n,m,l,le,le1,i,j,ip,nv2,nm1,k;
	double ur,ui,wr,wi,tr,ti,sr,si;
	
	n=x.length;
	m=2;
	switch (n) {
	case 4:m=2;break;
	case 8:m=3;break;
	case 16:m=4;break;
	case 32:m=5;break;
	case 64:m=6;break;
	case 128:m=7;break;
	case 256:m=8;break;
	case 512:m=9;break;
	case 1024:m=10;break;
	case 2048:m=11;break;
	}
	le1=n;
	for (l=1;l<=m;l++)
	    {
		le=le1;
		le1=le/2;
		ur=1.0;ui=0.0;
		wr=Math.cos(Math.PI/le1);wi=d*Math.sin(Math.PI/le1);
		for (j=0;j<le1;j++)
		    {
			for (i=j;i<n;i+=le)
			    {
				ip=i+le1;
				tr=x[i]+x[ip];ti=y[i]+y[ip];
				sr=x[i]-x[ip];si=y[i]-y[ip];
				x[ip]=sr*ur-si*ui;y[ip]=sr*ui+si*ur;
				x[i]=tr;y[i]=ti;
			    }
			tr=ur*wr-ui*wi;
			ui=ur*wi+ui*wr;
			ur=tr;
		    }
		
	    }
	
	nv2=n/2;
	nm1=n-1;
	j=0;
	for (i=0;i<nm1;i++)
	    {
		if (i<j)
		    {
			tr=x[j];ti=y[j];
			x[j]=x[i];y[j]=y[i];
			x[i]=tr;y[i]=ti;
		    }
		k=nv2;
		while (k<j+1) {j=j-k;k/=2;}
		j+=k;
	    }
	
    }


    int fromImg(byte c) { if (c>=0) return (int)c; else return (int)(256+c); }

    private Point2D findJNIXY(Image f,int x0,int y0,int l,int w) {
	double t,tx,ty,frp,fip,frm,fim,tr,ti;
	double h[],v[];
	int j,k,mx,my;
	
	h=new double[2*l];
	v=new double[2*l];
	
	for (j=0;j<2*l;j++) h[j]=v[j]=0.0;
	for (k=-w;k<w;k++) for (j=0;j<2*l;j++) h[j]+=fromImg(f.getData()[f.getOffset()+x0-l+j+(y0+k)*f.getScansize()]);
	for (k=0;k<2*l;k++) for (j=-w;j<w;j++) v[k]+=fromImg(f.getData()[f.getOffset()+x0+j+(y0-l+k)*f.getScansize()]);
	
	FFT(h,v,1);
	for (j=0;j<=l;j++) {
	    if (j==0) k=0; else k=2*l-j;
	    /* f */
	    frp=h[j]; fip=v[j]; frm=h[k]; fim=v[k];
	    /* h, v = 0 */
	    h[j]=v[j]=h[k]=v[k]=0.0;
	    /* h */
	    tr=(frp+frm)/2.0; ti=(fip-fim)/2.0;
	    /* h^2 */
	    t=Math.pow(tr,2)-Math.pow(ti,2); ti=2.0*tr*ti; tr=t;
	    /* sums h^2 */
	    h[j]+=tr; v[j]+=ti;
	    h[k]+=tr; v[k]+=-ti;
	    /* v */
	    tr=(fip+fim)/2.0; ti=-(frp-frm)/2.0;
	    /* v^2 */
	    t=Math.pow(tr,2)-Math.pow(ti,2); ti=2.0*tr*ti; tr=t;
	    /* sums v^2 */
	    h[j]+=-ti; v[j]+=tr;
	    h[k]+=ti; v[k]+=tr;
	}
	FFT(h,v,-1);
	
	tx=h[0];ty=v[0];mx=my=0;
	for (j=0;j<2*l;j++) {
	    if (h[j]>tx) { tx=h[j]; mx=j; }
	    if (v[j]>ty) { ty=v[j]; my=j; }
	}
	
	if (mx>=l) mx-=2*l;
	if (my>=l) my-=2*l;
	return new Point2D.Double(mx/2.0+x0,my/2.0+y0);
	
    }


    private double findJNIZ(Image f,int x0,int y0,int l,int w,int n,double []templ) {
	int j,k;
	double msd[],s[],mv,c1,c2;
	int mp;
	s=new double[l];
	for (j=0;j<l;j++) s[j]=0.0;
	for (k=-w;k<w;k++) for (j=0;j<l;j++) 
	    s[j]+=
		fromImg(f.getData()[f.getOffset()+x0+j+(y0+k)*f.getScansize()])+
		fromImg(f.getData()[f.getOffset()+x0-j+(y0+k)*f.getScansize()])+
		fromImg(f.getData()[f.getOffset()+x0+k+(y0+j)*f.getScansize()])+
		fromImg(f.getData()[f.getOffset()+x0+k+(y0-j)*f.getScansize()]);
	mv=0.0;
	for (j=0;j<l;j++) mv+=s[j];
	for (j=0;j<l;j++) s[j]/=mv;
	msd=new double[n];
	for (k=0;k<n;k++) {
	    msd[k]=0.0;
	    for (j=0;j<l;j++) msd[k]+=Math.pow(templ[k*l+j]-s[j],2);
	}
	mv=msd[4];mp=4;
	for (k=0;k<n;k++) if (msd[k]<mv) {mv=msd[k];mp=k;}
	if (mp==0) mp=1;
	if (mp==n-1) mp=n-2;
	c1 = (msd[mp+1]-msd[mp-1])/2.0;
	c2 = msd[mp+1]+msd[mp-1]-2.0*msd[mp];
	return mp-c1/c2;
    }

    void addJNIToTemplate(Image f,int x0,int y0,int l,int w,double []templ,int z) {
	int j,k;
	double s[],mv;
	s=new double[l];
	for (j=0;j<l;j++) s[j]=0.0;
	for (k=-w;k<w;k++) for (j=0;j<l;j++) 
	    s[j]+=
		fromImg(f.getData()[f.getOffset()+x0+j+(y0+k)*f.getScansize()])+
		fromImg(f.getData()[f.getOffset()+x0-j+(y0+k)*f.getScansize()])+
		fromImg(f.getData()[f.getOffset()+x0+k+(y0+j)*f.getScansize()])+
		fromImg(f.getData()[f.getOffset()+x0+k+(y0-j)*f.getScansize()]);
	mv=0.0;
	for (j=0;j<l;j++) mv+=s[j];
	for (j=0;j<l;j++) s[j]/=mv;
	for (j=0;j<l;j++) templ[j+l*z]+=s[j];
    }




    /** Let the cross find the best center of the bead that is pointing
*/ /*     * @param ph the PixelHandler to access the frame
     */
    public synchronized void findXY(Image f) {
	if (dragging) return;
 	setLocation(findJNIXY(f,intPosition.x,intPosition.y,crossL,crossHW));
    }
    

    /** Find the best position of the bead along the vertical axis
*/ /*     * @param ph the PixelHandler to access the frame
     */
    public void findZ(Image f) {
 	if (calibrating || dragging || calibration==null) return;
	z=findJNIZ(f,
		   intPosition.x,intPosition.y,
		   crossL,crossHW,
		   zNum,calibration
		   )*(maxZ-minZ)/zNum+minZ;
	component.setValue(z);
    }

 
    /** call this when the mouse starts dragging the cross
     * @param v is dragged - don't move automatically, even if findXY is called
     */
    public synchronized void setDragging(boolean v) {
	dragging = v;
    }


    /** the cross is being dragged by the mouse?
     * @return the cross is being dragged by the mouse?
     */
    public boolean isDragging() {
	return dragging;
    }


    /** set the bounding box of the whole image. Ensures that the cross doesn't exit from the image.
     * @param r the bounding box
     */
    public void setBBox(Rectangle r) {
	bbox=r;
    }


    /** set the position of the cross.
     * @param p the new position
     */
    public void setLocation(Point2D p) {
	Point ip = new Point((int)p.getX(),(int)p.getY());
	if (!bbox.contains(getBounds(ip))) return;
	position.setLocation(p);
	translate(-intPosition.x+ip.x,-intPosition.y+ip.y);
	intPosition.setLocation(ip);
    }


    /** get the position of the bead
     * @return the position
     */
    public Point2D getLocation() {
	Point2D r = new Point2D.Double();
	r.setLocation(position);
	return r;
    }

    /** get the position of the bead along Z axis
     * @return the position
     */
    public double getZ() {
	return z;
    }
 

    private Rectangle getBounds(Point p) {
	return new Rectangle(p.x-crossL,p.y-crossL,2*crossL,2*crossL);
    }


    /** get the bounds of the cross.
     * @return the bounds of the cross
     */
    public Rectangle getBounds() {
	return getBounds(intPosition);
    }


    /** get the color of the cross.
     * @return the color of the cross
     */
    public Color getColor() {
	return color;
    }


    private CComponent component = new CComponent();
    private class CComponent extends JPanel implements ChangeListener {
	JPointer sliderZero = new JPointer(JPointer.SLIDER_SIMPLE_STOP);
	JPointer sliderFound = new JPointer(JPointer.SLIDER_SIMPLE_QUADRANGLE);
	JSliderPlus sliderHolder = new JSliderPlus(JSliderPlus.VERTICAL,0.0,100.0);
	JLabel calibrImg = new JLabel();
	public CComponent() {
	    add(calibrImg);
	    sliderZero.setColor(Color.MAGENTA);
	    sliderFound.setColor(Color.CYAN);
	    sliderHolder.addJPointer(sliderZero);
	    sliderHolder.addJPointer(sliderFound);
	    add(sliderHolder);
	    sliderZero.setEnabled(false);
	    sliderFound.setEnabled(false);
	}
	public void setValue(double p) {
	    sliderFound.setValue(p);
	}
	public void setMinMax(double a,double b) {
	    sliderHolder.setLimits(a,b);
	    if (Math.abs(b-a)<5.0) {
		sliderHolder.setMinorTickSpacing(0.5);
		sliderHolder.setMajorTickSpacing(1.0);
	    }
	    else  {
		sliderHolder.setMinorTickSpacing(5.0);
		sliderHolder.setMajorTickSpacing(10.0);
	    }
	}
	public void stateChanged(ChangeEvent e) {
	    double p=((PositionControl)e.getSource()).getPosition();
	    sliderZero.setValue(p);
	}
	public void updateCalibrationImageLabel() {
	    java.awt.Image img=calibrImg.createImage(createCalibrationImage());
	    calibrImg.setIcon(new ImageIcon(img));
	}
    }
    /** get a Component that describes the Z position of the cross. 
     * It is also a ChangeListener, that can be added to a Spinner,
     * and listens to the z-movements of the objective
     * @return the Component
     */
    public Component getControlComponent() {
	return component;
    }

    // ---------------------------
    // Calibration

    private double [] calibration = null;
    private boolean calibrating = true;
    private int zNum,zLevel=-1;
    private double minZ,maxZ;

    /** Start the calibration
     * @param zNum number of position of the microscope objective
     * @param minZ minimum position of the microscope objective
     * @param maxZ maximum position of the microscope objective
     */
    public void calibrationStart(int zNum, double minZ, double maxZ) {
	calibrating = true;
	this.zNum=zNum;
	this.minZ=minZ;
	this.maxZ=maxZ;
	zLevel=-1;
	calibration = new double[zNum*crossL];
	component.setMinMax(minZ,maxZ);
    }

    /** send the image that has been acquired, so that it is used for
     * calibration
     * @param f the image from which data must be obtained
     */
    synchronized public void calibrationSendImage(Image f) {
	if (zLevel<0 || zLevel>=zNum) return;
	addJNIToTemplate(f,
			 intPosition.x,intPosition.y,
			 crossL,crossHW,
			 calibration,zLevel);
	zLevel=-1;
	notify();
    }

    
    /** Call this when the microscope objective position is right
     * for the calibration. This method returns immediately; to wait for the
     * processing to be done, call calibrationWait()
     * @param zLevel position in the calibration image. Evaluate 
     *    zNum = how many levels are in the calibration image? 
     *    minZ, maxZ = the min and max levels (in micron)
     *    z = where is now the objective? 
     *    So zLevel = (z-minZ)*zNum/(maxZ/minZ)
     * 
     */
    synchronized public void calibrationRequest(int zLevel) {
	this.zLevel = zLevel;
    }


    /** wait that the calibration, requested by calibrationRequest(), has been 
     * completed.
     */
    synchronized public void calibrationWait() {
	while (zLevel>=0) {
	    try { wait(); } catch (InterruptedException e) {}
	}
    }


    /** call this when the calibration has been done
     */
    public void calibrationEnd() {
	double s;
	for (int j=0;j<zNum;j++) {
	    s=0.0;
	    for (int k=0;k<crossL;k++) s+=calibration[j*crossL+k];
	    for (int k=0;k<crossL;k++) calibration[j*crossL+k]/=s;
	}
	calibrating=false;
	component.updateCalibrationImageLabel();
    }

    private ImageProducer createCalibrationImage() {
	int width=crossL;
	int height=zNum;
	byte [] ima = new byte[width*height];
	for (int j=0;j<width;j++) for (int k=0;k<height;k++) 
	    ima[j+k*width]=(byte)(int)(calibration[j+(height-1-k)*width]*crossL*128.0);
	byte [] reds = new byte[256];
	byte [] greens = new byte[256];
	byte [] blues = new byte[256];
	for (int j=0;j<256;j++) { reds[j]=greens[j]=blues[j]=(byte)j; }
	ColorModel icm = new IndexColorModel(8,256,reds,greens,blues);
	ImageProducer imgsrc=new MemoryImageSource(width,height,icm,ima,0,width);
	return imgsrc;
    }

}
