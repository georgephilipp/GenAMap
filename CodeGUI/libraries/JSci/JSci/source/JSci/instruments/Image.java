package JSci.instruments;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

/** Describes a frame, that holds the informations to access an image */

public abstract class Image implements Dimensions {

    /** @return the width of the image */
    public int getWidth() { return getSize().width; }

    /** @return the height of the image */
    public int getHeight() { return getSize().height; }

    /** @return the dimension of the image */
    public abstract Dimension getSize();

    /** @return the scansize of the image in the getData() */
    public int getScansize() { return getSize().width; }

    /** @return the offset of the image in the getData() */
    public int getOffset() { return 0; }

    private static ColorModel cm;
    static {
	byte [] r = new byte[256];
	byte [] g = new byte[256];
	byte [] b = new byte[256];
	for (int j=0;j<256;j++) r[j]=g[j]=b[j]=(byte)j;
	cm = new IndexColorModel(8,256,r,g,b);
    }
    /** @return the ColorModel */
    public ColorModel getColorModel() { return cm; }

    /** @return the data */
    public abstract byte[] getData();

    /** @return the time in milliseconds */
    public long getTimeStamp() { return 0; }

    /* Overlay */
    private ArrayList ovrl = new ArrayList();
    
    /** Images (poligons and text) can be overlaid onto the images.
     * This method is called to actually paint the overlays.
     * @param g the graphical environment on which we want to paint 
     */
    public void doOverlay(Graphics g) { 
	for (int j=0;j<ovrl.size();j++) ((Overlay)(ovrl.get(j))).paint(g) ;
    }

    /** Images (poligons and text) can be overlaid onto the images
     * @param o the objects that must be paint over the image 
     */
    public void addOverlay(Overlay o) { ovrl.add(o); }

    /** Creates a new image, from a part of this.
     * @param r the part that we want to extract 
     */
    public Image getSubImage(Rectangle r) {
	final int w=r.width;
	final int h=r.height;
	final byte[] b=new byte[w*h];
	for (int j=0;j<w;j++) for (int k=0;k<h;k++) 
	    b[j+k*w]=getData()[getOffset()+(r.x+j)+(r.y+k)*getScansize()];
	return new Image() {
		public int getWidth() { return w; }
		public int getHeight() { return h; }
		public Dimension getSize() { return new Dimension(w,h); }
		public int getScansize() { return w; }
		public int getOffset() { return 0; }
		public byte[] getData() { return b; }
	    };
    }
   

}


