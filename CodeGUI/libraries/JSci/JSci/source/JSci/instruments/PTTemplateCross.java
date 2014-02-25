package JSci.instruments;

import java.awt.*;
import java.io.*;

/**
 * Locating a template in the image
 */
class PTTemplateCross {  

    /** How fast the search area is moved (0-1) */
    public static double REGION_SPEED = 0.2;

    private int number;
    private Rectangle position;
    private Rectangle searchArea;
    private Image templateImage;
    private double templateBckgrnd;
    private double[][] corr;
    private static int nextN = 0;
    
    private double x;
    private double y;
    private double ox; // original position with respect to the upper left corner of the searchArea 
    private double oy;
    private double cx; // floating point position of the upper left corner of the searchArea
    private double cy;

    private int fromImg(byte c) { if (c>=0) return (int)c; else return (int)(256+c); }

    /**
     * @param p initial position of the template in the image
     * @param s search area
     * @param i the image to be searched (template)
     */
    public PTTemplateCross(Rectangle p, Rectangle s, Image i) {
	// template number
	number = nextN++;
	// rectangles
	position = (Rectangle)p.clone();
	searchArea = (Rectangle)s.clone();
	ox=position.x-searchArea.x;
	oy=position.y-searchArea.y;
	cx=searchArea.x;
	cy=searchArea.y;
	// template image
	templateImage = i;
	// template background
	int m,n;
	templateBckgrnd = 0.0;
	for (m=0;m<templateImage.getWidth();m++) 
	    for (n=0;n<templateImage.getHeight();n++) 
		templateBckgrnd+=fromImg(templateImage.getData()[templateImage.getOffset()+m+n*templateImage.getScansize()]);
	templateBckgrnd/=templateImage.getWidth()*templateImage.getHeight();
	// correlation function
	corr = new double[searchArea.width-templateImage.getWidth()]
	    [searchArea.height-templateImage.getHeight()];
    }

    /**
     * Locates the template; draws the current bounds of the template on the image
     * @param img the image that has been just grabbed
     */
    public void find(Image img) {

	double max=-100.0;
	int maxx = 0;
	int maxy = 0;

 	for (int j=0;j<corr.length;j++) for (int k=0;k<corr[0].length;k++) {
 	    corr[j][k]=0.0;
 	    for (int m=0;m<templateImage.getWidth();m++) for (int n=0;n<templateImage.getHeight();n++)
 		corr[j][k]+=
 		    fromImg(img.getData()[img.getOffset()+(j+m+searchArea.x)+(k+n+searchArea.y)*img.getScansize()])*
 		    (fromImg(templateImage.getData()[templateImage.getOffset()+m+n*templateImage.getScansize()])-templateBckgrnd);
 	    if (corr[j][k]>max) {
 		maxx=j;
 		maxy=k;
 		max=corr[j][k];
 	    }
 	}
	
	double Dx,Dy;
	if (maxx==0 | maxy==0 | maxx==corr.length-1 | maxy==corr[0].length-1) {Dx=Dy=0;}
	else {
	    double cxp=(corr[maxx+1][maxy]-corr[maxx-1][maxy])/2.0;
	    double cyp=(corr[maxx][maxy+1]-corr[maxx][maxy-1])/2.0;
	    double cxpyp=(corr[maxx+1][maxy+1]-corr[maxx+1][maxy-1]-corr[maxx-1][maxy+1]+corr[maxx-1][maxy-1])/4.0;
	    double cxpp=-2.0*corr[maxx][maxy]+corr[maxx+1][maxy]+corr[maxx-1][maxy];
	    double cypp=-2.0*corr[maxx][maxy]+corr[maxx][maxy+1]+corr[maxx][maxy-1];
	    double Det=cxpp*cypp-cxpyp*cxpyp;
	    Dx=-(cxp*cypp-cyp*cxpyp)/Det;
	    Dy=-(cxpp*cyp-cxpyp*cxp)/Det;
	}
	
	x=searchArea.getMinX()+maxx+Dx;
	y=searchArea.getMinY()+maxy+Dy;

 	cx+=(x-searchArea.x-ox)*REGION_SPEED;
 	cy+=(y-searchArea.y-oy)*REGION_SPEED;
 	if (cx<0) cx=0;
 	if (cx+searchArea.width>=img.getWidth()) cx=img.getWidth()-1-searchArea.width;
 	if (cy<0) cy=0;
 	if (cy+searchArea.height>=img.getHeight()) cy=img.getHeight()-1-searchArea.height;

	position.setLocation((int)Math.rint(x),(int)Math.rint(y));
	searchArea.setLocation((int)Math.rint(cx),(int)Math.rint(cy));

	img.addOverlay(new Overlay() {
		public void paint(Graphics g) {
		    Graphics2D g2 = (Graphics2D)g;
		    g2.setColor(Color.RED);
		    g2.draw(position);
		    g2.setColor(Color.MAGENTA);
		    g2.draw(searchArea);
		    g2.drawString(PTTemplateCross.this.toString(),searchArea.x,searchArea.y+searchArea.height+12);
		}
	    });

    }

    /** 
     * @return the (sequential) number of the template
     */
    public int getN() {return number;}

    /** @return the number of the cross; increases for each instance is created */
    public String toString() { return ""+number; }

    /** 
     * @return X position of the object
     */
    public double getX() {return x;}

    /** 
     * @return Y position of the object
     */
    public double getY() {return y;}

}
