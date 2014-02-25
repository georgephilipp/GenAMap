package JSci.instruments;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.border.*;
import java.text.*;

/** An object that creates an animated image from a ImageSource. 
Create a new Player, register it with a ImageSource; then you can
add some ROIs. It displays a window with the image and a window with
all the controls of the chain.
*/

public class Player extends JPanel implements ImageSink {

    private MemoryImageSource imgsource = null;
    private java.awt.Image img = null;
    private Image currentImage = null;

    /* source */
    private ImageSource source = null;
    public void setSource(ImageSource fs) {
	if (source!=fs) {
	    source=fs;
	    source.setSink(this);
	}
    }

    /* decimate frames */
    private int decimationFrame = 0;
    private int decimationNumber = 1;
    /** The Player can decide to show only some frames. 
     * @param s only one frame over s will be displayed 
     */
    public void setDecimationNumber(int s) { decimationNumber=s; }

    /* statistics */   
    private long receivedFrames = 0;
    private long displayedFrames = 0;
    private long lastReceivedFrames = 0;
    private long lastDisplayedFrames = 0;
    private long last = System.currentTimeMillis();
    private JLabel receivedFramesLabel;
    private JLabel displayedFramesLabel;
    private boolean newImagePresent=false;
    private static NumberFormat formatter = NumberFormat.getNumberInstance();
    { formatter.setMaximumFractionDigits(1); 
    formatter.setMinimumFractionDigits(1); 
    formatter.setMinimumIntegerDigits(1); 
    formatter.setMaximumIntegerDigits(3); 
    }

    public void runStatisticsUpdate() {
	long nowReceivedFrames;
	long nowDisplayedFrames;
	long now;
	while (true) {
	    nowReceivedFrames = receivedFrames;
	    nowDisplayedFrames = displayedFrames;
	    now = System.currentTimeMillis();
	    
	    receivedFramesLabel.setText("received: "+
					formatter.format(
							 (lastReceivedFrames - nowReceivedFrames)*
							 1000.0/
							 (last-now)
							 )+
					" Hz"
					);
	    displayedFramesLabel.setText("displayed: "+
					 formatter.format(
							  (lastDisplayedFrames - nowDisplayedFrames)*
							  1000.0/
							  (last-now)
							  )+
					 " Hz"
					 );

	    lastReceivedFrames = nowReceivedFrames;
	    lastDisplayedFrames = nowDisplayedFrames;
	    last = now;

	    try { Thread.sleep(1000); }
	    catch (InterruptedException e) {}
	}
    }


    /* interfaces implementation */
    public void receive(Image f) {
	receivedFrames++;
	currentImage=f;
	decimationFrame++;
	if (decimationFrame==decimationNumber) decimationFrame=0;
	if (imgsource == null) {
	    setSize(f.getSize());
  	    imgsource = new MemoryImageSource(f.getWidth(),f.getHeight(),f.getColorModel(),f.getData(),f.getOffset(),f.getScansize());
	    imgsource.setAnimated(true);
	    img = createImage(imgsource);
	}
	else if (decimationFrame==0) {	    
	    imgsource.newPixels(f.getData(),f.getColorModel(),f.getOffset(),f.getScansize());
	    newImagePresent=true;
	}
    }

    public void start() {
	while (imgsource == null) {               //TODO: wait
	    try { Thread.sleep(100); }
	    catch (InterruptedException e) {}
	}
	JFrame f = new JFrame("Player");
	f.setSize(getSize().width+10,getSize().height+30); //TODO: +10, +30
	f.getContentPane().add(this);
	f.setResizable(false);
	f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	f.setVisible(true);
	JFrame c = new JFrame("Controls");
	c.getContentPane().add(getControlComponent());
	c.pack();
	//c.setResizable(false);
	c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	c.setVisible(true);
    }

    ROI r = null;                    //TODO: use a list of ROIs
    public void addROI(ROI r) {
	this.r=r;
	r.setComponent(this);
    }

    public void paintComponent(Graphics g) {
	super.paintComponent(g);
	if (img!=null) g.drawImage(img,0,0,this);
	if (newImagePresent) {
	    displayedFrames++;
	    newImagePresent=false;
	}
	if (currentImage!=null)
	    currentImage.doOverlay(g);
	if (r!=null) r.paint(g);
    }
    
    /** @return the component that controls the object that implements
	this interface */
    public Component getControlComponent() {
	JPanel t = new JPanel();
	t.setLayout(new FlowLayout());
	if (source.getControlComponent()!=null)
	    t.add(source.getControlComponent());
	JPanel s = new JPanel();
	s.setLayout(new BorderLayout());
	receivedFramesLabel=new JLabel("received: 000.0 Hz");
	displayedFramesLabel=new JLabel("displayed: 000.0 Hz");
	Thread thrd = new Thread(new Runnable() {
		public void run() { Player.this.runStatisticsUpdate(); }
	    });
	thrd.setDaemon(true);
	thrd.start();
	s.add(BorderLayout.NORTH,receivedFramesLabel);
	s.add(BorderLayout.SOUTH,displayedFramesLabel);
	Border etched = BorderFactory.createEtchedBorder();
	Border titled = BorderFactory.createTitledBorder(etched,"player");
	s.setBorder(titled);
	t.add(s);
	return t;
    }

}







