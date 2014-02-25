/**
* The NexStar class encapsulates a NexStar telescope.
* Copyright (C) 1999-2001  Mark Hale
* @author Mark Hale
*/

package JSci.astro.telescope;

import java.io.*;
import javax.comm.*;

public final class NexStar extends Object {
        private SerialPort serial;
        private InputStreamReader in;
        private OutputStreamWriter out;
        /**
        * Convert RA from a string to a number.
        */
        public static int raToInt(String ra) {
                final int hrs=Integer.valueOf(ra.substring(0,2)).intValue();
                final int mins=Integer.valueOf(ra.substring(3,5)).intValue();
                final int secs=Integer.valueOf(ra.substring(6,8)).intValue();
                return hrs*600+mins*10+secs;
        }
        /**
        * Convert dec from a string to a number.
        */
        public static int decToInt(String dec) {
                final int degs=Integer.valueOf(dec.substring(0,3)).intValue();
                final int mins=Integer.valueOf(dec.substring(4,6)).intValue();
                final int secs=Integer.valueOf(dec.substring(7,9)).intValue();
                if(degs>=0)
                        return degs*600+mins*10+secs;
                else
                        return degs*600-mins*10-secs;
        }
        /**
        * Convert alt from a string to a number.
        */
        public static int altToInt(String alt) {
                final int degs=Integer.valueOf(alt.substring(0,3)).intValue();
                final int mins=Integer.valueOf(alt.substring(4,6)).intValue();
                final int secs=Integer.valueOf(alt.substring(7,9)).intValue();
                if(degs>=0)
                        return degs*600+mins*10+secs;
                else
                        return degs*600-mins*10-secs;
        }
        /**
        * Convert az from a string to a number.
        */
        public static int azToInt(String az) {
                final int degs=Integer.valueOf(az.substring(0,3)).intValue();
                final int mins=Integer.valueOf(az.substring(4,6)).intValue();
                final int secs=Integer.valueOf(az.substring(7,9)).intValue();
                return degs*600+mins*10+secs;
        }
        /**
        * Constructs a NexStar object.
        */
        public NexStar(String port) {
                try {
                        CommPortIdentifier portID=CommPortIdentifier.getPortIdentifier(port);
                        serial=(SerialPort)portID.open("NexStar",10);
                        serial.setSerialPortParams(9600,SerialPort.DATABITS_8,
                                SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                        in=new InputStreamReader(serial.getInputStream());
                        out=new OutputStreamWriter(serial.getOutputStream());
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
        * Returns the current RA and dec.
        */
        public synchronized String getRADec() throws IOException {
                sendCmd("E");
                return readString();
        }
        /**
        * Returns the current az and alt.
        */
        public synchronized String getAzAlt() throws IOException {
                sendCmd("Z");
                return readString();
        }
        /**
        * Goto the coordinates.
        */
        public synchronized void gotoRADec(String ra,String dec) throws IOException {
                final int raBytes=raToInt(ra);
                final int decBytes=decToInt(dec);
                out.write('R');
                out.write((byte)(raBytes>>8));
                out.write((byte)(raBytes));
                out.write(',');
                out.write((byte)(decBytes>>8));
                out.write((byte)(decBytes));
                out.flush();
        }
        /**
        * Goto the coordinates.
        */
        public synchronized void gotoAzAlt(String az,String alt) throws IOException {
                final int azBytes=azToInt(az);
                final int altBytes=altToInt(alt);
                out.write('A');
                out.write((byte)(azBytes>>8));
                out.write((byte)(azBytes));
                out.write(',');
                out.write((byte)(altBytes>>8));
                out.write((byte)(altBytes));
                out.flush();
        }
        /**
        * Check whether the scope is moving.
        */
        public synchronized boolean isMoving() throws IOException {
                sendCmd("L");
                return readString().equals("1");
        }
        /**
        * Sends a command to the scope.
        */
        private void sendCmd(String cmd) throws IOException {
                out.write(cmd);
                out.flush();
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

