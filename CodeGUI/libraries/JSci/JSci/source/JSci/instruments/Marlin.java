package JSci.instruments;

import javax.media.*;
import com.sun.media.ui.*;
import javax.media.protocol.*;
import javax.media.protocol.DataSource;
import java.net.*;
import java.io.*;
import java.util.*;


import JSci.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.text.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.media.format.*;
import com.inzyme.jmds.*;


public class Marlin implements ImageSource, BufferTransferHandler {

    private Dimension dim = new Dimension(640,480);

    public Marlin() {   
      DSCaptureDeviceInfo[] devices = DSCaptureDeviceManager.getCaptureDevices();
      DSCapturePin[] pins = devices[0].getPins();
      DSDataSource dataSource = new DSDataSource(pins[0]);
      try {
        dataSource.connect();
        dataSource.start();
      }
      catch (Exception e) { System.out.println("Unable to connect to camera: "+e); System.exit(2); }
      PushBufferStream[] pbss = dataSource.getStreams();
      pbss[0].setTransferHandler(this);
    }

    public void transferData(PushBufferStream stream) {
      if (sink==null) return;
      final byte[] ba = new byte[getSize().width*getSize().height];
      for (int j=0;j<ba.length;j++) ba[j]=0;
      Buffer bu = new Buffer();
      bu.setData(ba);
      bu.setLength(getSize().width*getSize().height);
      bu.setFormat(stream.getFormat());
      try { stream.read(bu); }
      catch (Exception e) { System.out.println("Frame lost "+e); throw new Error(); }
      final long t = System.currentTimeMillis();
      final Dimension dim = new Dimension(getWidth(),getHeight());
      JSci.instruments.Image f = new JSci.instruments.Image() {
	public byte[] getData() { return ba; }
	public Dimension getSize() { return dim; }
	public long getTimeStamp() { return t; }
      };
      sink.receive(f);
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

    /** @return the width of the image */
    public int getWidth() { return dim.width; }
    
    /** @return the height of the image */
    public int getHeight() { return dim.height; }
    
    /** @return the dimension of the image */
    public Dimension getSize() { return dim; }

    /** @return the component that controls the object that implements
	this interface */
    public Component getControlComponent() {
	return null;
    }

    public static void main(String args[]) {
      ImageSource source = new Marlin();
      JSci.instruments.Player player = new JSci.instruments.Player();
      source.setSink(player);
      player.start();
    }


}
