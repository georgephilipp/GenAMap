package JSci.instruments;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class PTTwoDBarycentreCross {

    /** threshold for defininf the light and dark regions; 0&lt;ALPHA&lt;1 (ALPHA approx 0.3) */
    public static double ALPHA = 0.3;
    /** the weight of the light region vs. the dark region (%) */
    public static int WEIGHT_LIGHT_PART = 50;
    /** the speed of movement of the region; 0 = the region doesn't move; 1 = the region follows the bead */
    public static double REGION_SPEED = 0.2;
    /** the meaning of fields in interlaced images; 1 for odd/even, 0 for odd-only fields */
    public  static int ODD_EVEN = 1;
    
    private Rectangle region;
    private double x;
    private double y;
    private double x2;
    private double y2;
    private double ox; // original position with respect to the upper left corner of the region 
    private double oy;
    private double cx; // floating point position of the upper left corner of the region
    private double cy;
    private boolean firstTime = true;

    private static int currNum = 0;
    private int num;

    /** @param r the initial region where to look for the bead */
    public PTTwoDBarycentreCross(Rectangle r) {
	region = (Rectangle)r.clone();
	cx=region.x;
	cy=region.y;
	num=currNum++;
    }
    





    /////////////////////////////////////////////////////////////////////////////////
    // TRACKING EX-NATIVO
    
    private int fromImg(byte c) { if (c>=0) return (int)c; else return (int)(256+c); }

    private void contiguous(Hashtable h,Point p) {
	HashSet candidati=new HashSet(),effettivi=new HashSet();
	candidati.add(p);
	while (!candidati.isEmpty()) {
	    p=(Point)candidati.iterator().next();
	    candidati.remove(p);
	    if (h.containsKey(p) && ! effettivi.contains(p)) {
		effettivi.add(p);
		candidati.add(new Point(p.x+1,p.y));
		candidati.add(new Point(p.x-1,p.y));
		candidati.add(new Point(p.x,p.y+1));
		candidati.add(new Point(p.x,p.y-1));
	    }
	}
	for (Enumeration e=h.keys();e.hasMoreElements();) {
	    p=(Point)e.nextElement();
	    if (!effettivi.contains(p)) h.remove(p);
	}
    }

    /*
      Parameters:
      im            : the image
      start         : the index of the first pixel of the ROI
      width         : the width of the ROI
      height        : the height of the ROI
      linestride    : the line width of the whole image
      alpha         : the factor for defining the tresholds
      weight        : the weight of the light region vs. the dark region (%)
      x[0],x[1]     : the resulting position
    */

    private void PTTwoDBarycentreFindNative(
		 byte []im,
		 int start,int width,int height,int linestride,
		 double alpha,
		 int weight,
		 double []x
		 ) {
	/* posizione e valore degli estremi */
	int maxj,maxk,minj,mink;
	int maxv,minv;
	/* tagli superiore e inferiore */
	int inf,sup;
	/* coordinate */
	int j,k,p,q;
	/* somme */
	long s,sx,sy;
	/* valori finali x,y ligth/dark */
	double xl,yl,xd,yd;
	/* utili */
	int abv;
	/* elenco pixel interessati */
	Hashtable dark = new Hashtable(),light = new Hashtable();

	/* minimo, massimo, media */
       	maxj=minj=maxk=mink=0;
	maxv=minv=fromImg(im[start]);s=0;
	abv=linestride-width;
	for (k=0,p=start;k<height;k++,p+=abv) for (j=0;j<width;j++,p++) {
	    if (fromImg(im[p])>maxv) {maxv=fromImg(im[p]);maxj=j;maxk=k;}
	    if (fromImg(im[p])<minv) {minv=fromImg(im[p]);minj=j;mink=k;}
	    s+=fromImg(im[p]);
	}
	s/=width*height;
	
	/* superiore e inferiore per tagliare l'immagine */
	sup=(int)((1.0-alpha)*s+alpha*maxv);
	inf=(int)((1.0-alpha)*s+alpha*minv);

	/* zone di interesse */
	for (k=0,p=start;k<height;k++,p+=abv) for (j=0;j<width;j++,p++) {
	    if (fromImg(im[p])>sup) light.put(new Point(j,k),new Integer(fromImg(im[p])));
	    if (fromImg(im[p])<inf) dark.put(new Point(j,k),new Integer(fromImg(im[p])));
	}
	
	/* cerca zone contigue */
	contiguous(light,new Point(maxj,maxk));
	contiguous(dark,new Point(minj,mink));

	/* media nella parte superiore */
	if (weight!=0) {
	    sx=sy=s=0;
	    Point pt;int v;
	    for (Enumeration e = light.keys();e.hasMoreElements();) {
		pt=(Point)e.nextElement();
		v=((Integer)(light.get(pt))).intValue();
		sx+=(v-sup)*pt.x;
		sy+=(v-sup)*pt.y;
		s+=v-sup;
	    }
	    xl=(double)sx/s;
	    yl=(double)sy/s;
	}
	else { xl=yl=0.0; }

	/* media nella parte inferiore */
	if (weight!=100) {
	    sx=sy=s=0;
	    Point pt;int v;
	    for (Enumeration e = dark.keys();e.hasMoreElements();) {
		pt=(Point)e.nextElement();
		v=((Integer)(dark.get(pt))).intValue();
		sx+=(inf-v)*pt.x;
		sy+=(inf-v)*pt.y;
		s+=inf-v;
	    }
	    xd=(double)sx/s;
	    yd=(double)sy/s;
	}
	else { xd=yd=0.0; }
	
	/* risultati */
	x[0]=(xl*weight+xd*(100-weight))/100;
	x[1]=(yl*weight+yd*(100-weight))/100;

    }


    // FINE TRACKING EX-NATIVO
    ////////////////////////////////////////////////////////////////////////////////



    /** ask the cross to find the bead. For non-interlaced images. */
    public void find(Image i) {
	// find the bead
	double[] rp = new double[2];
	PTTwoDBarycentreFindNative(
			    i.getData(),
			    region.x+region.y*i.getScansize()+i.getOffset(),region.width,region.height,i.getScansize(),
			    ALPHA,
			    WEIGHT_LIGHT_PART,
			    rp);
	double dx = rp[0];
	double dy = rp[1];
	if (Double.isNaN(dx) || Double.isNaN(dy)) return;
	x = region.x+dx;
	y = region.y+dy;
	// move the region
	if (firstTime) {
	    ox=dx;
	    oy=dy;
	    cx=region.x;
	    cy=region.y;
	    firstTime=false;
	}
	cx+=(dx-ox)*REGION_SPEED;
	cy+=(dy-oy)*REGION_SPEED;
	if (cx<0) cx=0;
	if (cx+region.width>=i.getWidth()) cx=i.getWidth()-1;
	if (cy<0) cy=0;
	if (cy+region.height>=i.getHeight()) cy=i.getHeight()-1;
	region.setLocation((int)cx,(int)cy);
	// add overlays
	i.addOverlay(new Overlay() {
		public void paint(Graphics g) {
		    Graphics2D g2 = (Graphics2D)g;
		    g2.setColor(Color.RED);
		    g2.draw(new Line2D.Double(x,y-3,x,y+3));
		    g2.draw(new Line2D.Double(x-3,y,x+3,y));
		    g2.setColor(Color.MAGENTA);
		    g2.draw(region);
		    g2.drawString(PTTwoDBarycentreCross.this.toString(),region.x,region.y+region.height+12);
		}
	    });
    }


    /** ask the cross to find the bead. For interlaced images; generates also x2 and y2 coords. */
    public void findInterlaced(Image i) {
	// find the bead - first field
	double[] rp = new double[2];
	PTTwoDBarycentreFindNative(
			    i.getData(),
			    region.x+region.y*i.getScansize()+i.getOffset(),region.width,region.height/2,i.getScansize()*2,
			    ALPHA,
			    WEIGHT_LIGHT_PART,
			    rp);
	double dx = rp[0];
	double dy = rp[1]*2;
	if (Double.isNaN(dx) || Double.isNaN(dy)) return;
	x = region.x+dx;
	y = region.y+dy;
	// find the bead - second field
	PTTwoDBarycentreFindNative(
			    i.getData(),
			    region.x+(region.y+1)*i.getScansize()+i.getOffset(),region.width,region.height/2,i.getScansize()*2,
			    ALPHA,
			    WEIGHT_LIGHT_PART,
			    rp);
	double dx2 = rp[0];
	double dy2 = rp[1]*2+ODD_EVEN;
	if (Double.isNaN(dx2) || Double.isNaN(dy2)) return;
	x2 = region.x+dx2;
	y2 = region.y+dy2;
	// move the region
	dx=(dx+dx2)/2;
	dy=(dy+dy2)/2;
	if (firstTime) {
	    ox=dx;
	    oy=dy;
	    cx=region.x;
	    cy=region.y;
	    firstTime=false;
	}
	cx+=(dx-ox)*REGION_SPEED;
	cy+=(dy-oy)*REGION_SPEED;
	if (cx<0) cx=0;
	if (cx+region.width>=i.getWidth()) cx=i.getWidth()-1;
	if (cy<0) cy=0;
	if (cy+region.height>=i.getHeight()) cy=i.getHeight()-1;
	region.setLocation((int)cx,(int)cy);
	// add overlays
	i.addOverlay(new Overlay() {
		public void paint(Graphics g) {
		    Graphics2D g2 = (Graphics2D)g;
		    g2.setColor(Color.RED);
		    g2.draw(new Line2D.Double(x,y-3,x,y+3));
		    g2.draw(new Line2D.Double(x-3,y,x+3,y));
		    g2.setColor(Color.MAGENTA);
		    g2.draw(region);
		    g2.drawString(PTTwoDBarycentreCross.this.toString(),region.x,region.y+region.height+12);
		}
	    });
    }

    /** @return the number of the cross; increases for each instance is created */
    public String toString() { return ""+num; }

    /** @return the number of the bead */
    public int getN() { return num; }
    /** @return the x position of the bead */
    public double getX() { return x; }
    /** @return the y position of the bead */
    public double getY() { return y; }
    /** @return the x position of the bead, in the second field, only for interlaced images */
    public double getX2() { return x2; }
    /** @return the y position of the bead, in the second field, only for interlaced images  */
    public double getY2() { return y2; }

}
