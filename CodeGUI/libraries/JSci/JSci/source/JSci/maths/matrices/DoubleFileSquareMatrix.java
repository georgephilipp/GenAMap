package JSci.maths.matrices;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Uses an underlying file as storage.
 * @author Mark
 */
public class DoubleFileSquareMatrix extends AbstractDoubleSquareMatrix {
    private static final int DEFAULT_ROW_BUFFERS = 3;
    private static final boolean DEFAULT_DIAG_BUFFER = true;
    private static final int DOUBLE_BYTE_SIZE = 8;
    private final RandomAccessFile file;
    private final int[] rowHistory;
    private final int[] colHistory;
    private final int[] bufferRows;
    private final int[] bufferStartCols;
    private final int[] bufferEndCols;
    private final double[][] buffers;
    private final double[] diagBuffer;

    /**
     * @param numBuffers number of row buffers
     */
    public DoubleFileSquareMatrix(int size, File f, int numBuffers, boolean bufferDiag) throws IOException {
        this(size, f, numBuffers, bufferDiag, null);
    }
    private DoubleFileSquareMatrix(int size, File f, int numBuffers, boolean bufferDiag, double[][] initArray) throws IOException {
        super(size);
        rowHistory = new int[numBuffers];
        colHistory = new int[numBuffers];
        bufferRows = new int[numBuffers];
        bufferStartCols = new int[numBuffers];
        bufferEndCols = new int[numBuffers];
        buffers = new double[numBuffers][];
        for(int i=0; i<buffers.length; i++) {
            rowHistory[i] = -1;
            colHistory[i] = -1;
            bufferRows[i] = -1;
            bufferStartCols[i] = -1;
            bufferEndCols[i] = -1;
        }
        diagBuffer = (bufferDiag ? new double[size] : null);
        this.file = new RandomAccessFile(f, "rw");

        if(initArray != null) {
            for(int i=0; i<size; i++) {
                ByteArrayOutputStream arr = new ByteArrayOutputStream(numCols*DOUBLE_BYTE_SIZE);
                DataOutputStream out = new DataOutputStream(arr);
                for(int j=0; j<size; j++) {
                    out.writeDouble(initArray[i][j]);
                }
                out.close();
                file.write(arr.toByteArray());
                if(bufferDiag)
                    diagBuffer[i] = initArray[i][i];
            }
        } else {
            for(int i=0; i<size; i++) {
                ByteArrayOutputStream arr = new ByteArrayOutputStream(numCols*DOUBLE_BYTE_SIZE);
                DataOutputStream out = new DataOutputStream(arr);
                for(int j=0; j<size; j++) {
                    out.writeDouble(0.0);
                }
                out.close();
                file.write(arr.toByteArray());
            }
        }
    }
    public DoubleFileSquareMatrix(int size, String filename, int numBuffers, boolean bufferDiag) throws IOException {
        this(size, new File(filename), numBuffers, bufferDiag);
    }
    private DoubleFileSquareMatrix(int size, double[][] initArray) throws IOException {
        this(size, createTempMatrixFile(), DEFAULT_ROW_BUFFERS, DEFAULT_DIAG_BUFFER, initArray);
    }
    private static File createTempMatrixFile() throws IOException {
        File tmp = File.createTempFile("jsci", ".matrix");
        tmp.deleteOnExit();
        return tmp;
    }
    public DoubleFileSquareMatrix(int size) throws IOException {
        this(size, null);
    }
    public DoubleFileSquareMatrix(double[][] array) throws IOException {
        this(array.length, array);
    }

    private void writeBuffer(int bufferRow) throws IOException {
        if(bufferRows[bufferRow] != -1 && buffers[bufferRow] != null) {
            int startCol = bufferStartCols[bufferRow];
            int endCol = bufferEndCols[bufferRow];
            seek(bufferRows[bufferRow], startCol);
            int cols = endCol - startCol;
            ByteArrayOutputStream arr = new ByteArrayOutputStream(cols*DOUBLE_BYTE_SIZE);
            DataOutputStream out = new DataOutputStream(arr);
            for(int j=startCol; j<endCol; j++) {
                out.writeDouble(buffers[bufferRow][j]);
            }
            out.close();
            file.write(arr.toByteArray());
        }
    }
    private void readBuffer(int bufferRow, int i, int startCol, int endCol) throws IOException {
        if(buffers[bufferRow] == null)
            buffers[bufferRow] = new double[numCols];
        seek(i, startCol);
        int cols = endCol - startCol;
        byte[] arr = new byte[cols*DOUBLE_BYTE_SIZE];
        file.readFully(arr);
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(arr));
        for (int j=startCol; j<endCol; j++) {
            buffers[bufferRow][j] = in.readDouble();
        }
        in.close();
    }
    private double[] buffer(int i, int j, int oldCol) throws IOException {
        writeBuffer(buffers.length-1);
        double[] tmp = buffers[buffers.length-1];
        System.arraycopy(buffers, 0, buffers, 1, buffers.length-1);
        System.arraycopy(bufferRows, 0, bufferRows, 1, bufferRows.length-1);
        System.arraycopy(bufferStartCols, 0, bufferStartCols, 1, bufferStartCols.length-1);
        System.arraycopy(bufferEndCols, 0, bufferEndCols, 1, bufferEndCols.length-1);
        buffers[0] = tmp;
        int startCol, endCol;
        if(j >= oldCol) {
            // cache forward
            startCol = j;
            endCol = numCols;
        } else {
            // cache backward
            startCol = 0;
            endCol = j+1;
        }
        readBuffer(0, i, startCol, endCol);
        bufferRows[0] = i;
        bufferStartCols[0] = startCol;
        bufferEndCols[0] = endCol;
        return buffers[0];
    }
    private double[] extendBuffer(int bufferRow, int i, int j) throws IOException {
        int startCol = bufferStartCols[bufferRow];
        int endCol = bufferEndCols[bufferRow];
        if(j < startCol) {
            readBuffer(bufferRow, i, j, startCol);
            bufferStartCols[bufferRow] = j;
        } else if(j >= endCol) {
            readBuffer(bufferRow, i, endCol, j+1);
            bufferEndCols[bufferRow] = j+1;
        }
        return buffers[bufferRow];
    }

    public double getElement(int i, int j) {
        if(i>=0 && i<numRows && j>=0 && j<numCols) {
            double value;
            if(i == j && diagBuffer != null) {
                value = diagBuffer[i];
            } else {
                int bufferRow = find(bufferRows, i);
                if(bufferRow != -1) {
                    try {
                        value = extendBuffer(bufferRow,i,j)[j];
                    } catch (IOException ioe) {
                        throw new MatrixDimensionException(ioe.toString());
                    }
                } else {
                    int histIndex = find(rowHistory, i);
                    if(histIndex != -1) {
                        // cache
                        try {
                            int oldCol = colHistory[histIndex];
                            value = buffer(i,j,oldCol)[j];
                        } catch (IOException ioe) {
                            throw new MatrixDimensionException(ioe.toString());
                        }
                    } else {
                        try {
                            value = read(i, j);
                        } catch (IOException ioe) {
                            throw new MatrixDimensionException(ioe.toString());
                        }
                    }
                }
            }
            updateHistory(i, j);
            return value;
        } else {
                throw new MatrixDimensionException(getInvalidElementMsg(i,j));
        }
    }

    public void setElement(int i, int j, double x) {
        if(i>=0 && i<numRows && j>=0 && j<numCols) {
            if(i == j && diagBuffer != null) {
                diagBuffer[i] = x;
            } else {
                int bufferRow = find(bufferRows, i);
                if(bufferRow != -1) {
                    try {
                        extendBuffer(bufferRow,i,j)[j] = x;
                    } catch (IOException ioe) {
                        throw new MatrixDimensionException(ioe.toString());
                    }
                } else {
                    int histIndex = find(rowHistory, i);
                    if(histIndex != -1) {
                        // cache
                        try {
                            int oldCol = colHistory[histIndex];
                            buffer(i,j,oldCol)[j] = x;
                        } catch (IOException ioe) {
                            throw new MatrixDimensionException(ioe.toString());
                        }
                    } else {
                        try {
                            write(i, j, x);
                        } catch (IOException ioe) {
                            throw new MatrixDimensionException(ioe.toString());
                        }
                    }
                }
            }
            updateHistory(i, j);
        } else {
                throw new MatrixDimensionException(getInvalidElementMsg(i,j));
        }
    }

    private void seek(int i, int j) throws IOException {
        long pos = (i * (long) numCols + j) * DOUBLE_BYTE_SIZE;
        file.seek(pos);
    }

    private double read(int i, int j) throws IOException {
        seek(i, j);
        return file.readDouble();
    }

    private void write(int i, int j, double x) throws IOException {
        seek(i, j);
        file.writeDouble(x);
    }

    private void updateHistory(int i, int j) {
        if(rowHistory.length > 0) {
            System.arraycopy(rowHistory, 0, rowHistory, 1, rowHistory.length-1);
            System.arraycopy(colHistory, 0, colHistory, 1, colHistory.length-1);
            rowHistory[0] = i;
            colHistory[0] = j;
        }
    }

    public void close() throws IOException {
        for(int i=0; i<buffers.length; i++)
            writeBuffer(i);
        if(diagBuffer != null) {
            for(int i=0; i<numRows; i++)
                write(i, i, diagBuffer[i]);
        }
        file.close();
    }

    private static int find(int[] arr, int value) {
        for(int i=0; i<arr.length; i++) {
            if(arr[i] == value)
                return i;
        }
        return -1;
    }
}
