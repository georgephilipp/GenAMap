package JSci.io;

import java.io.*;
import java.util.Vector;

import JSci.maths.matrices.AbstractDoubleMatrix;

/**
* TextReader: reads data text files/streams.
* This class uses buffered I/O.
* @version 2.0
* @author Mark Hale
*/
public final class TextReader extends BufferedReader {
        /**
        * Reads text data from a reader.
        */
        public TextReader(Reader reader) {
                super(reader);
        }
        /**
        * Reads a text file with the specified system dependent file name.
        * @param name the system dependent file name.
        * @exception FileNotFoundException If the file is not found.
        */
        public TextReader(String name) throws FileNotFoundException {
                this(new FileReader(name));
        }
        /**
        * Reads a text file with the specified File object.
        * @param file the file to be opened for reading.
        * @exception FileNotFoundException If the file is not found.
        */
        public TextReader(File file) throws FileNotFoundException {
                this(new FileReader(file));
        }
        /**
        * Reads data to an array.
        * Autodetects the delimiter.
        * @exception IOException If an I/O error occurs.
        */
        public double[][] readArray() throws IOException {
                final Vector rows=new Vector();
                String line = readLine();
                while(line != null && line.length() > 0) {
                    line = line.trim();
                    // detect delimiter
                    int delimiter = -1;
                    int i;
                    for(i=0; i<line.length() && delimiter == -1; i++) {
                        char ch = line.charAt(i);
                        if(!isNumber(ch)) {
                            delimiter = ch;
                        }
                    }

                    double[] row;
                    if(delimiter == -1) {
                        // single column - parse
                        row = new double[1];
                        row[0] = Double.parseDouble(line);
                    } else {
                        // detect column count
                        int colCount = 2;
                        for(; i<line.length(); i++) {
                            char ch = line.charAt(i);
                            if(ch == delimiter)
                                colCount++;
                        }
                        // parse
                        row = new double[colCount];
                        int startPos = 0;
                        int endPos = line.indexOf(delimiter);
                        for(i=0; endPos != -1; i++) {
                            row[i] = Double.parseDouble(line.substring(startPos, endPos).trim());
                            startPos = endPos+1;
                            endPos = line.indexOf(delimiter, startPos);
                        }
                        row[i] = Double.parseDouble(line.substring(startPos).trim());
                    }
                    rows.addElement(row);
                    line = readLine();
                }

                double array[][]=new double[rows.size()][];
                for(int i=0;i<array.length;i++) {
                    array[i] = (double[]) rows.elementAt(i);
                }
                return array;
        }
        /**
        * Reads data into a matrix.
        * Autodetects the delimiter.
        * @exception IOException If an I/O error occurs.
        */
        public void read(AbstractDoubleMatrix m) throws IOException {
                String line = readLine();
                for(int i=0; line != null && line.length() > 0; i++) {
                    line = line.trim();
                    // detect delimiter
                    int delimiter = -1;
                    int k;
                    for(k=0; k<line.length() && delimiter == -1; k++) {
                        char ch = line.charAt(k);
                        if(!isNumber(ch)) {
                            delimiter = ch;
                        }
                    }
                    // parse
                    int startPos = 0;
                    int endPos = line.indexOf(delimiter);
                    for(k=0; endPos != -1; k++) {
                        m.setElement(i, k, Double.parseDouble(line.substring(startPos, endPos).trim()));
                        startPos = endPos+1;
                        endPos = line.indexOf(delimiter, startPos);
                    }
                    m.setElement(i, k, Double.parseDouble(line.substring(startPos).trim()));
                    line = readLine();
                }
        }
        /**
        * Reads a (row,column,value) table into a matrix.
        * Autodetects the delimiter.
        * @exception IOException If an I/O error occurs.
        */
        public void readTable(AbstractDoubleMatrix m) throws IOException {
                String line = readLine();
                for(int i=0; line != null && line.length() > 0; i++) {
                    line = line.trim();
                    // detect delimiter
                    int delimiter = -1;
                    int k;
                    for(k=0; k<line.length() && delimiter == -1; k++) {
                        char ch = line.charAt(k);
                        if(!isNumber(ch)) {
                            delimiter = ch;
                        }
                    }
                    // parse
                    int startPos = 0;
                    int endPos = k-1;
                    int row = Integer.parseInt(line.substring(startPos, endPos).trim());
                    startPos = endPos+1;
                    endPos = line.indexOf(delimiter, startPos);
                    int col = Integer.parseInt(line.substring(startPos, endPos).trim());
                    startPos = endPos+1;
                    double value = Double.parseDouble(line.substring(startPos).trim());
                    m.setElement(row, col, value);
                    line = readLine();
                }
        }
        private static boolean isNumber(int ch) {
                return Character.isDigit((char)ch) || ch=='.' || ch=='+' || ch=='-' || ch=='e' || ch=='E';
        }
}
