package JSci.instruments.pi;

import JSci.instruments.*;

import java.io.*;

/** A Phisik Instrumente LVPZT amplifier/servo position controller,
 * attached to the PC through a RS232 port.
 */
public class PiezoServo extends DummyPositionControl {

    private DataInput rfi;
    private DataOutput rfo;
    private String name;
    private int ttyNumber;
    private String type;
    private boolean inverted;

    /** the E-665 controller */
    public static final String E665="E-665";

    /** the E-662 controller */
    public static final String E662="E-662";

    /** the identification string, as returned by IDN? */
    private final String idnqm;

    /** @param n the number of the serial port (0,1,...) 
     * @param type the type of controller (E665, E662), see E665 and E662.
     * @param inverted the direction must be inverted?
     */
    public PiezoServo(int n,String type,boolean inverted) throws IOException {
	this(n, new RandomAccessFile("/dev/ttyS"+n,"rw"), type, inverted);
    }

    /** @param n the number of the serial port (0,1,...) 
     * @param raf the file connected to the port 
     * @param type the type of controller (E665, E662), see E665 and E662.
     * @param inverted the direction must be inverted?
     */
    public PiezoServo(int n, RandomAccessFile raf, String type,boolean inverted) throws IOException {
	this(n, raf, raf, type, inverted);
    }

    /** @param n the number of the serial port (0,1,...) 
     * @param rfi the input file connected to the port 
     * @param rfo the output file connected to the port 
     * @param type the type of controller (E665, E662), see E665 and E662.
     * @param inverted the direction must be inverted?
     */
    public PiezoServo(int n, DataInput rfi, DataOutput rfo, String type,boolean inverted) throws IOException {
	this.type=type;
	this.inverted=inverted;
	this.rfi = rfi;
	this.rfo = rfo;
	ttyNumber=n;
	if (type.equals(E665)) write("SVO A1");
	write("*IDN?");
	name=read();
	if (!name.substring(0,2).equals("PI"))
	    throw new IOException("Serial device is unknown: "+name);
	idnqm=name;
	write("MOV A99");
    }

    /** @return the identification string, as returned by IDN? */
    public String idn() {
        return idnqm;
    }

    private void write(String s) throws IOException {
	rfo.writeBytes(s);
	rfo.writeByte(10);
	try {Thread.sleep(100);} catch (InterruptedException e) {}
    }

    private String read() throws IOException {
	String s="";
	byte b;
	while ((b=rfi.readByte())!=10) s+=(char)b;
	return s;
    }

    public String toString() {
	return getClass().getName()+
            "[Port=/dev/ttyS"+ttyNumber+
            ",Controller="+name+"]";
    }

    protected final void doSetPosition(double z) {
	if (inverted) z=100.0-z;
	try {
	    if (type.equals(E665)) write("MOV A"+z);
	    if (type.equals(E662)) write("POS "+z);
	}
	catch (IOException e) {
	    System.err.println("Error writing to the "+name+": "+e);
	}
    }

    public double getActualPosition() {
	String v="";
	try {
	    if (type.equals(E665)) write("POS?A");
	    if (type.equals(E662)) write("POS?");
	    v=read();
	} catch (IOException e) {}
	double z = Double.valueOf(v).doubleValue();
	if (inverted) z=100.0-z;
        return z;
    }
    
    public void sleep() {
	try {Thread.sleep(300);} catch (InterruptedException e) {}
    }

    public static void main(String [] args) {
	PiezoServo p = null;
	try {
	    p = new PiezoServo(Integer.parseInt(args[0]),E665,false);
	    System.out.println(p);

	    System.out.println("Position: "+p.getActualPosition());
	    p.setPosition(3.0);
	    p.sleep();
	    System.out.println("Position: "+p.getActualPosition());
	    p.setPosition(6.0);
	    p.sleep();
	    System.out.println("Position: "+p.getActualPosition());
	    p.setPosition(9.0);
	    p.sleep();
	    System.out.println("Position: "+p.getActualPosition());
	}
	catch (IOException e) {
	    System.out.println(p.name+" error: "+e);
	}
    }

}
