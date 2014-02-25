/**
* The LX200 class encapsulates an LX200 telescope.
* Copyright (C) 1999-2001  Mark Hale
* @author Mark Hale
*/

package JSci.astro.telescope;

import java.io.*;
import javax.comm.*;

public final class LX200 extends Object {
        private SerialPort serial;
        private InputStreamReader in;
        private OutputStreamWriter out;
        /**
        * Focus rates.
        */
        public final static int FOCUS_FAST=1;
        public final static int FOCUS_SLOW=2;
        /**
        * Focus directions.
        */
        public final static int FOCUS_IN=1;
        public final static int FOCUS_OUT=2;
        /**
        * Slew rates.
        */
        public final static int SLEW_SLEW=1;
        public final static int SLEW_FIND=2;
        public final static int SLEW_CENTER=3;
        public final static int SLEW_GUIDE=4;
        /**
        * Slew directions.
        */
        public final static int SLEW_NORTH=1;
        public final static int SLEW_EAST=2;
        public final static int SLEW_SOUTH=3;
        public final static int SLEW_WEST=4;
        /**
        * Convert RA from a string to a number.
        */
        public static float raToFloat(String ra) {
                final float hrs=Integer.valueOf(ra.substring(0,2)).floatValue();
                final float mins=Integer.valueOf(ra.substring(3,5)).floatValue();
                final float secs=Integer.valueOf(ra.substring(6,8)).floatValue();
                return hrs+mins/60.0f+secs/600.0f;
        }
        /**
        * Convert dec from a string to a number.
        */
        public static float decToFloat(String dec) {
                final float degs=Integer.valueOf(dec.substring(0,3)).floatValue();
                final float mins=Integer.valueOf(dec.substring(4,6)).floatValue();
                final float secs=Integer.valueOf(dec.substring(7,9)).floatValue();
                if(degs>=0.0)
                        return degs+mins/60.0f+secs/600.0f;
                else
                        return degs-mins/60.0f-secs/600.0f;
        }
        /**
        * Convert alt from a string to a number.
        */
        public static float altToFloat(String alt) {
                final float degs=Integer.valueOf(alt.substring(0,3)).floatValue();
                final float mins=Integer.valueOf(alt.substring(4,6)).floatValue();
                final float secs=Integer.valueOf(alt.substring(7,9)).floatValue();
                if(degs>=0.0)
                        return degs+mins/60.0f+secs/600.0f;
                else
                        return degs-mins/60.0f-secs/600.0f;
        }
        /**
        * Convert az from a string to a number.
        */
        public static float azToFloat(String az) {
                final float degs=Integer.valueOf(az.substring(0,3)).floatValue();
                final float mins=Integer.valueOf(az.substring(4,6)).floatValue();
                final float secs=Integer.valueOf(az.substring(7,9)).floatValue();
                return degs+mins/60.0f+secs/600.0f;
        }
        /**
        * Constructs an LX200 object.
        */
        public LX200(String port) {
                try {
                        CommPortIdentifier portID=CommPortIdentifier.getPortIdentifier(port);
                        serial=(SerialPort)portID.open("LX200",10);
                        serial.setSerialPortParams(9600,SerialPort.DATABITS_8,
                                SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                        in=new InputStreamReader(serial.getInputStream());
                        out=new OutputStreamWriter(serial.getOutputStream());
                        setHighPrecision(true);
                        setLongFormat(true);
                } catch(NoSuchPortException e) {
                        System.err.println("Port does not exist: "+e.getMessage());
                        e.printStackTrace();
                } catch(PortInUseException e) {
                        System.err.println("Port is in use by another process: "+e.getMessage());
                        e.printStackTrace();
                } catch(UnsupportedCommOperationException e) {
                } catch(IOException e) {}
        }
        /**
        * Sets high precision.
        */
        public synchronized void setHighPrecision(boolean setHigh) throws IOException {
                final boolean isHigh=toggleHighPrecision();
                if(setHigh!=isHigh)
                        toggleHighPrecision();
        }
        private boolean toggleHighPrecision() throws IOException {
                char reply[]=new char[14];
                sendCmd("#:P#");
                in.read(reply,0,14);
                return (reply[0]=='H');
        }
        /**
        * Sets long format.
        */
        public synchronized void setLongFormat(boolean setLong) throws IOException {
                final boolean isLong=isLongFormatEnabled();
                if(setLong!=isLong)
                        sendCmd("#:U#");
        }
        private boolean isLongFormatEnabled() throws IOException {
                sendCmd("#:GR#");
                String reply=readString();
                return (reply.length()==9);
        }
        /**
        * Set focus rate.
        */
        public synchronized void setFocusRate(int rate) throws IOException {
                switch(rate) {
                        case FOCUS_FAST: sendCmd("#:FF#");break;
                        case FOCUS_SLOW: sendCmd("#:FS#");break;
                }
        }
        /**
        * Start focus.
        */
        public synchronized void startFocus(int direction) throws IOException {
                switch(direction) {
                        case FOCUS_IN: sendCmd("#:F+#");break;
                        case FOCUS_OUT: sendCmd("#:F-#");break;
                }
        }
        /**
        * Stop focus.
        */
        public synchronized void stopFocus() throws IOException {
                sendCmd("#:FQ#");
        }
        /**
        * Set slew rate.
        */
        public synchronized void setSlewRate(int rate) throws IOException {
                switch(rate) {
                        case SLEW_SLEW: sendCmd("#:RS#");break;
                        case SLEW_FIND: sendCmd("#:RM#");break;
                        case SLEW_CENTER: sendCmd("#:RC#");break;
                        case SLEW_GUIDE: sendCmd("#:RG#");break;
                }
        }
        /**
        * Start slewing the scope.
        * @param direction the direction to start slewing in.
        */
        public synchronized void startSlew(int direction) throws IOException {
                switch(direction) {
                        case SLEW_NORTH: sendCmd("#:Mn#");break;
                        case SLEW_EAST: sendCmd("#:Me#");break;
                        case SLEW_SOUTH: sendCmd("#:Ms#");break;
                        case SLEW_WEST: sendCmd("#:Mw#");break;
                }
        }
        /**
        * Stop slewing the scope.
        * @param direction the direction to stop slewing in.
        */
        public synchronized void stopSlew(int direction) throws IOException {
                switch(direction) {
                        case SLEW_NORTH: sendCmd("#:Qn#");break;
                        case SLEW_EAST: sendCmd("#:Qe#");break;
                        case SLEW_SOUTH: sendCmd("#:Qs#");break;
                        case SLEW_WEST: sendCmd("#:Qw#");break;
                }
        }
        /**
        * Returns the current RA.
        */
        public synchronized String getRA() throws IOException {
                sendCmd("#:GR#");
                return readString();
        }
        /**
        * Returns the current dec.
        */
        public synchronized String getDec() throws IOException {
                sendCmd("#:GD#");
                return readString();
        }
        /**
        * Returns the current alt.
        */
        public synchronized String getAlt() throws IOException {
                sendCmd("#:GA#");
                return readString();
        }
        /**
        * Returns the current az.
        */
        public synchronized String getAz() throws IOException {
                sendCmd("#:GZ#");
                return readString();
        }
        /**
        * Sets the object/target coordinates.
        */
        public synchronized boolean setObjectCoords(String ra,String dec) throws IOException {
                boolean rc;
                sendCmd("#:Sr"+ra+"#");
                rc=readBoolean();
                sendCmd("#:Sd"+dec+"#");
                rc&=readBoolean();
                return rc;
        }
        /**
        * Slew to the object coordinates.
        * @return 0 if slew is possible,
        * 1 if object is below the horizon,
        * 2 if object is below the higher.
        */
        public synchronized int slewToObject() throws IOException {
                sendCmd("#:MS#");
                final int rc=in.read();
                if(rc=='0') {
                        return 0;
                } else if(rc=='1') {
                        readString();
                        return 1;
                } else if(rc=='2') {
                        readString();
                        return 2;
                } else
                        return -1;
        }
        /**
        * Checks the scope's position.
        * @param ra RA to check against.
        * @param dec dec to check against.
        */
        public synchronized boolean checkPosition(float ra,float dec) throws IOException {
                final float raError=raToFloat(getRA())-ra;
                final float decError=decToFloat(getDec())-dec;
                return (Math.abs(raError) <= 1.0/(15.0*30.0)) && (Math.abs(decError) <= 1.0/30.0);
        }
        /**
        * Check whether the scope is moving.
        */
        public synchronized boolean isMoving() throws IOException {
                final String oldRA=getRA();
                final String oldDec=getDec();
                try {
                        Thread.sleep(20000);
                } catch(InterruptedException e) {}
                final String newRA=getRA();
                final String newDec=getDec();
                return !(newRA.equals(oldRA) && newDec.equals(oldDec));
        }
        /**
        * Returns the local time.
        */
        public synchronized String getLocalTime() throws IOException {
                sendCmd("#:GL#");
                return readString();
        }
        /**
        * Sets the local time.
        */
        public synchronized boolean setLocalTime(String time) throws IOException {
                sendCmd("#:SL"+time+"#");
                return readBoolean();
        }
        /**
        * Synchronize the scope coordinates to the object coordinates.
        */
        public synchronized void syncCoords() throws IOException {
                sendCmd("#:CM#");
                readString();
        }
        /**
        * Sends a command to the scope.
        */
        private void sendCmd(String cmd) throws IOException {
                out.write(cmd);
                out.flush();
        }
        /**
        * Reads a boolean from the scope.
        */
        private boolean readBoolean() throws IOException {
                return (in.read()=='1');
        }
        /**
        * Reads a string from the scope, dropping the terminating #.
        */
        private String readString() throws IOException {
                StringBuffer msg=new StringBuffer();
                int ch=in.read();
                while(ch!='#') {
                        msg.append(ch);
                        ch=in.read();
                }
                return msg.toString();
        }
        /**
        * Closes the connection to the scope.
        */
        public synchronized void close() throws IOException {
                in.close();
                out.close();
                serial.close();
        }
}

