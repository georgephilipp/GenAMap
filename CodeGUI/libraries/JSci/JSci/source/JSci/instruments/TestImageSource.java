package JSci.instruments;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/** An object that delivers sample frames */

public class TestImageSource implements ImageSource,Runnable {

    Dimension dim = new Dimension(130,97);

    public TestImageSource() {
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
	    final int n = num++;
	    final long t = System.currentTimeMillis();
	    final byte[] im = new byte[getWidth()*getHeight()];
	    final Dimension dim = new Dimension(getWidth(),getHeight());
	    for (int j=0;j<getWidth();j++) 
		for (int k=0;k<getHeight();k++)
		    im[j+k*getWidth()] = (byte) ((j+n)%256);
	    Image f = new Image() {
		    public byte[] getData() { return im; };
		    public Dimension getSize() { return dim; }
		    public long getTimeStamp() { return t; }
		};
	    f.addOverlay(new Overlay() {
		    public void paint(Graphics g) {
			g.setColor(Color.MAGENTA);
			((Graphics2D)g).draw(new Rectangle(30+n%10,34,40,40));
		    }
		});
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




