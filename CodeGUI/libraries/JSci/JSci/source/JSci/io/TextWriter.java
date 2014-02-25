package JSci.io;

import java.io.*;
import JSci.maths.matrices.AbstractDoubleMatrix;
import JSci.maths.matrices.AbstractIntegerMatrix;
import JSci.maths.vectors.AbstractDoubleVector;
import JSci.maths.vectors.AbstractIntegerVector;

/**
* TextWriter: writes data text files/streams.
* This class uses buffered I/O.
* @version 2.0
* @author Mark Hale
*/
public final class TextWriter extends BufferedWriter {
        private final char delimiter;

        /**
        * Writes text data to a writer.
        */
        public TextWriter(Writer writer, char ch) {
                super(writer);
                delimiter = ch;
        }
        /**
        * Writes to a text file with the specified system dependent file name.
        * @param name the system dependent file name.
        * @param ch the character that delimits data columns.
        * @exception IOException If the file is not found.
        */
        public TextWriter(String name,char ch) throws IOException {
                this(new FileWriter(name), ch);
        }
        /**
        * Writes to a text file with the specified File object.
        * @param file the file to be opened for writing.
        * @param ch the character that delimits data columns.
        * @exception IOException If the file is not found.
        */
        public TextWriter(File file,char ch) throws IOException {
                this(new FileWriter(file), ch);
        }
        public void writeDouble(double d) throws IOException {
            write(Double.toString(d));
        }
        public void writeInt(int i) throws IOException {
            write(Integer.toString(i));
        }
        /**
        * Writes an array of data as a row.
        * @param data the data to be written.
        * @exception IOException If an I/O error occurs.
        */
        public void write(double data[]) throws IOException {
                int i;
                for(i=0;i<data.length-1;i++) {
                        writeDouble(data[i]);
                        write(delimiter);
                }
                writeDouble(data[i]);
                newLine();
        }
        /**
        * Writes an array of data.
        * @param data the data to be written.
        * @exception IOException If an I/O error occurs.
        */
        public void write(double data[][]) throws IOException {
                for(int i=0;i<data.length;i++) {
                    write(data[i]);
                }
        }
        /**
        * Writes an array of data as a row.
        * @param data the data to be written.
        * @exception IOException If an I/O error occurs.
        */
        public void write(int data[]) throws IOException {
                int i;
                for(i=0;i<data.length-1;i++) {
                        writeInt(data[i]);
                        write(delimiter);
                }
                writeInt(data[i]);
                newLine();
        }
        /**
        * Writes an array of data.
        * @param data the data to be written.
        * @exception IOException If an I/O error occurs.
        */
        public void write(int data[][]) throws IOException {
                for(int i=0;i<data.length;i++) {
                    write(data[i]);
                }
        }
        /**
        * Writes a matrix.
        * @param matrix the matrix to be written.
        * @exception IOException If an I/O error occurs.
        */
        public void write(AbstractDoubleMatrix matrix) throws IOException {
                final int mRow=matrix.rows();
                final int mCol=matrix.columns();
                for(int j,i=0;i<mRow;i++) {
                        for(j=0;j<mCol-1;j++) {
				writeDouble(matrix.getElement(i,j));
                                write(delimiter);
                        }
                        writeDouble(matrix.getElement(i,j));
                        newLine();
                }
        }
        /**
        * Writes a matrix as a (row,column,value) table.
        * Zero elements are not written.
        * @param matrix the matrix to be written.
        * @exception IOException If an I/O error occurs.
        */
        public void writeTable(AbstractDoubleMatrix matrix) throws IOException {
                final int mRow=matrix.rows();
                final int mCol=matrix.columns();
                for(int i=0;i<mRow;i++) {
                        for(int j=0;j<mCol;j++) {
                                double value = matrix.getElement(i,j);
                                if(Math.abs(value) > JSci.GlobalSettings.ZERO_TOL) {
                                    writeInt(i);
                                    write(delimiter);
                                    writeInt(j);
                                    write(delimiter);
                                    writeDouble(value);
                                    newLine();
                                }
                        }
                }
        }
        /**
        * Writes a matrix.
        * @param matrix the matrix to be written.
        * @exception IOException If an I/O error occurs.
        */
        public void write(AbstractIntegerMatrix matrix) throws IOException {
                final int mRow=matrix.rows();
                final int mCol=matrix.columns();
                for(int j,i=0;i<mRow;i++) {
                        for(j=0;j<mCol-1;j++) {
				writeInt(matrix.getElement(i,j));
                                write(delimiter);
                        }
                        writeInt(matrix.getElement(i,j));
                        newLine();
                }
        }
        /**
        * Writes a vector as a row.
        * @param vector the vector to be written.
        * @exception IOException If an I/O error occurs.
        */
        public void write(AbstractDoubleVector vector) throws IOException {
                final int dim=vector.dimension();
                int i;
                for(i=0;i<dim-1;i++) {
                        writeDouble(vector.getComponent(i));
                        write(delimiter);
                }
                writeDouble(vector.getComponent(i));
                newLine();
        }
        /**
        * Writes a vector as a row.
        * @param vector the vector to be written.
        * @exception IOException If an I/O error occurs.
        */
        public void write(AbstractIntegerVector vector) throws IOException {
                final int dim=vector.dimension();
                int i;
                for(i=0;i<dim-1;i++) {
                        writeInt(vector.getComponent(i));
                        write(delimiter);
                }
                writeInt(vector.getComponent(i));
                newLine();
        }
}
