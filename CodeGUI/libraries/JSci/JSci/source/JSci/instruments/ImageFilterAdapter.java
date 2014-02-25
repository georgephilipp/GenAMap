package JSci.instruments;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/** An object that filters frames. We only need to override
the <code>filter</code> method */

public abstract class ImageFilterAdapter implements ImageFilter {

    private ImageSink sink;
    public void setSink(ImageSink fs) {
	if (sink!=fs) {
	    sink=fs;
	    sink.setSource(this);
	}
    }

    private ImageSource source = null;
    public void setSource(ImageSource fs) {
	if (source!=fs) {
	    source=fs;
	    source.setSink(this);
	}
    }

    public void receive(Image f) {
	filter(f); 
	if (sink!=null) sink.receive(f);
    }

    public Component getControlComponent() {
	JPanel t = new JPanel();
	t.setLayout(new FlowLayout());
	if (source.getControlComponent()!=null)
	    t.add(source.getControlComponent());
	if (getFilterControlComponent()!=null) {
	    JPanel s = new JPanel();
	    s.setLayout(new BorderLayout());
	    s.add(getFilterControlComponent());
	    Border etched = BorderFactory.createEtchedBorder();
	    Border titled = BorderFactory.createTitledBorder(etched,getName());
	    s.setBorder(titled);
	    t.add(s);
	}
	return t;
    }

    public int getWidth() { return source.getWidth(); }
    public int getHeight() { return source.getHeight(); }
    public Dimension getSize() { return source.getSize(); }

    /** defines the Component that controls this filter */
    public Component getFilterControlComponent() { return null; } 

    /** defines the operations acutally needed for filtering */
    public abstract void filter(Image f);

    /** defines the name of the filter */
    public String getName() { return "Filter"; }

}

