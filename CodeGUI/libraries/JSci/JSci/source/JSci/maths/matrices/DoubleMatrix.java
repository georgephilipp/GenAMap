/* AUTO-GENERATED */
package JSci.maths.matrices;

import JSci.maths.ExtraMath;
import JSci.maths.Mapping;
import JSci.maths.DimensionException;
import JSci.maths.vectors.AbstractDoubleVector;
import JSci.maths.vectors.DoubleVector;
import JSci.maths.groups.AbelianGroup;
import JSci.maths.algebras.*;
import JSci.maths.fields.*;

/**
* The DoubleMatrix class provides an object for encapsulating double matrices.
* @version 2.2
* @author Mark Hale
*/
public class DoubleMatrix extends AbstractDoubleMatrix {
        /**
        * Array containing the elements of the matrix.
        */
        protected final double matrix[][];
        /**
        * Constructs a matrix by wrapping an array.
        * @param array an assigned value
        */
        public DoubleMatrix(final double array[][]) {
                super(array.length, array[0].length);
                matrix=array;
        }
        /**
        * Constructs an empty matrix.
        */
        public DoubleMatrix(final int rows,final int cols) {
                this(new double[rows][cols]);
        }
        /**
        * Constructs a matrix from an array of vectors (columns).
        * @param array an assigned value
        */
        public DoubleMatrix(final AbstractDoubleVector array[]) {
                this(array[0].dimension(), array.length);
                for(int i=0;i<numRows;i++) {
                        for(int j=0;j<numCols;j++)
                                matrix[i][j]=array[j].getComponent(i);
                }
        }
        /**
        * Compares two ${nativeTyp} matrices for equality.
        * @param m a double matrix
        */
        public boolean equals(AbstractDoubleMatrix m, double tol) {
                if(m != null && numRows == m.rows() && numCols == m.columns()) {
			double sumSqr = 0;
                        for(int i=0;i<numRows;i++) {
                                for(int j=0;j<numCols;j++) {
					double delta = matrix[i][j]-m.getElement(i,j);
					sumSqr += delta*delta;
                                }
                        }
                        return (sumSqr <= tol*tol);
                } else {
                        return false;
                }
        }
        /**
        * Returns a string representing this matrix.
        */
        public String toString() {
                final StringBuffer buf=new StringBuffer(5*numRows*numCols);
                for(int i=0;i<numRows;i++) {
                        for(int j=0;j<numCols;j++) {
                                buf.append(matrix[i][j]);
                                buf.append(' ');
                        }
                        buf.append('\n');
                }
                return buf.toString();
        }
        /**
        * Converts this matrix to an integer matrix.
        * @return an integer matrix
        */
        public AbstractIntegerMatrix toIntegerMatrix() {
                final int ans[][]=new int[numRows][numCols];
                for(int i=0;i<numRows;i++) {
                        for(int j=0;j<numCols;j++)
                                ans[i][j]=Math.round((float)matrix[i][j]);
                }
                return new IntegerMatrix(ans);
        }
        /**
        * Converts this matrix to a complex matrix.
        * @return a complex matrix
        */
        public AbstractComplexMatrix toComplexMatrix() {
                ComplexMatrix cm = new ComplexMatrix(numRows, numCols);
                for(int i=0;i<numRows;i++) {
                        for(int j=0;j<numCols;j++)
                                cm.setElement(i, j, matrix[i][j], 0.0);
                }
                return cm;
        }
        /**
        * Returns an element of the matrix.
        * @param i row index of the element
        * @param j column index of the element
        * @exception MatrixDimensionException If attempting to access an invalid element.
        */
        public double getElement(int i, int j) {
                if(i>=0 && i<numRows && j>=0 && j<numCols)
                        return matrix[i][j];
                else
                        throw new MatrixDimensionException(getInvalidElementMsg(i,j));
        }
        /**
        * Sets the value of an element of the matrix.
        * Should only be used to initialise this matrix.
        * @param i row index of the element
        * @param j column index of the element
        * @param x a number
        * @exception MatrixDimensionException If attempting to access an invalid element.
        */
        public void setElement(int i, int j, double x) {
                if(i>=0 && i<numRows && j>=0 && j<numCols)
                        matrix[i][j]=x;
                else
                        throw new MatrixDimensionException(getInvalidElementMsg(i,j));
        }
        /**
        * Returns the l<sup><img border=0 alt="infinity" src="doc-files/infinity.gif"></sup>-norm.
        * @author Taber Smith
        */
        public double infNorm() {
                double result=0,tmpResult;
                for(int i=0;i<numRows;i++) {
                        tmpResult=0;
                        for(int j=0;j<numCols;j++)
                                tmpResult+=Math.abs(matrix[i][j]);
                        if(tmpResult>result)
                                result=tmpResult;
                }
                return result;
        }
        /**
        * Returns the Frobenius or Hilbert-Schmidt (l<sup>2</sup>) norm.
        * @jsci.planetmath FrobeniusMatrixNorm
        */
        public double frobeniusNorm() {
                double result=0.0;
                for(int j,i=0;i<numRows;i++) {
                        for(j=0;j<numCols;j++)
                                result=ExtraMath.hypot(result, matrix[i][j]);
                }
                return result;
        }

//============
// OPERATIONS
//============

        /**
        * Returns the negative of this matrix.
        */
        public AbelianGroup.Member negate() {
                final double array[][]=new double[numRows][numCols];
                for(int i=0;i<numRows;i++) {
                        array[i][0] = -matrix[i][0];
                        for(int j=1;j<numCols;j++)
                                array[i][j] = -matrix[i][j];
                }
                return new DoubleMatrix(array);
        }

// ADDITION

        /**
        * Returns the addition of this matrix and another.
        * @param m a double matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public AbstractDoubleMatrix add(final AbstractDoubleMatrix m) {
                if(numRows==m.rows() && numCols==m.columns()) {
                        final double array[][]=new double[numRows][numCols];
                        for(int i=0;i<numRows;i++) {
                                array[i][0] = matrix[i][0]+m.getElement(i,0);
                                for(int j=1;j<numCols;j++)
                                        array[i][j] = matrix[i][j]+m.getElement(i,j);
                        }
                        return new DoubleMatrix(array);
                } else {
                        throw new MatrixDimensionException("Matrices are different sizes.");
                }
        }

// SUBTRACTION

        /**
        * Returns the subtraction of this matrix by another.
        * @param m a double matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public AbstractDoubleMatrix subtract(final AbstractDoubleMatrix m) {
                if(numRows==m.rows() && numCols==m.columns()) {
                        final double array[][]=new double[numRows][numCols];
                        for(int i=0;i<numRows;i++) {
                                array[i][0] = matrix[i][0]-m.getElement(i,0);
                                for(int j=1;j<numCols;j++)
                                        array[i][j] = matrix[i][j]-m.getElement(i,j);
                        }
                        return new DoubleMatrix(array);
                } else {
                        throw new MatrixDimensionException("Matrices are different sizes.");
                }
        }

// SCALAR MULTIPLICATION

        /**
        * Returns the multiplication of this matrix by a scalar.
        * @param x a double.
        * @return a double matrix.
        */
        public AbstractDoubleMatrix scalarMultiply(final double x) {
                final double array[][]=new double[numRows][numCols];
                for(int i=0;i<numRows;i++) {
                        array[i][0] = x*matrix[i][0];
                        for(int j=1;j<numCols;j++)
                                array[i][j] = x*matrix[i][j];
                }
                return new DoubleMatrix(array);
        }

// SCALAR DIVISON

        /**
        * Returns the division of this matrix by a scalar.
        * @param x a double.
        * @return a double matrix.
        */
        public AbstractDoubleMatrix scalarDivide(final double x) {
                final double array[][]=new double[numRows][numCols];
                for(int i=0;i<numRows;i++) {
                        array[i][0] = matrix[i][0]/x;
                        for(int j=1;j<numCols;j++)
                                array[i][j] = matrix[i][j]/x;
                }
                return new DoubleMatrix(array);
        }

// SCALAR PRODUCT

        /**
        * Returns the scalar product of this matrix and another.
        * @param m a double matrix.
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public double scalarProduct(final AbstractDoubleMatrix m) {
                if(m instanceof DoubleMatrix)
                        return scalarProduct((DoubleMatrix)m);

                if(numRows==m.rows() && numCols==m.columns()) {
                        double ans=0;
                        for(int i=0;i<numRows;i++) {
                                ans += matrix[i][0]*m.getElement(i,0);
                                for(int j=1;j<numCols;j++)
                                        ans += matrix[i][j]*m.getElement(i,j);
                        }
                        return ans;
                } else {
                       throw new MatrixDimensionException("Matrices are different sizes.");
                }
        }
        public double scalarProduct(final DoubleMatrix m) {
                if(numRows==m.numRows && numCols==m.numCols) {
                        double ans=0;
                        for(int i=0;i<numRows;i++) {
                                ans+=matrix[i][0]*m.matrix[i][0];
                                for(int j=1;j<numCols;j++)
                                        ans+=matrix[i][j]*m.matrix[i][j];
                        }
                        return ans;
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }

// MATRIX MULTIPLICATION

        /**
        * Returns the multiplication of a vector by this matrix.
        * @param v a double vector.
        * @exception DimensionException If the matrix and vector are incompatible.
        */
        public AbstractDoubleVector multiply(final AbstractDoubleVector v) {
                if(numCols==v.dimension()) {
                        final double array[]=new double[numRows];
                        for(int i=0;i<numRows;i++) {
                                array[i]=matrix[i][0]*v.getComponent(0);
                                for(int j=1;j<numCols;j++)
                                        array[i]+=matrix[i][j]*v.getComponent(j);
                        }
                        return new DoubleVector(array);
                } else {
                        throw new DimensionException("Matrix and vector are incompatible.");
                }
        }
        /**
        * Returns the multiplication of this matrix and another.
        * @param m a double matrix
        * @return a AbstractDoubleMatrix or a AbstractDoubleSquareMatrix as appropriate
        * @exception MatrixDimensionException If the matrices are incompatible.
        */
        public AbstractDoubleMatrix multiply(final AbstractDoubleMatrix m) {
                if(m instanceof DoubleMatrix)
                        return multiply((DoubleMatrix)m);

                if(numCols==m.rows()) {
                        final int mColumns = m.columns();
                        final double array[][]=new double[numRows][mColumns];
                        for(int j=0; j<numRows; j++) {
                                for(int k=0; k<mColumns; k++) {
                                        array[j][k] = matrix[j][0]*m.getElement(0,k);
                                        for(int n=1; n<numCols; n++)
                                                array[j][k] += matrix[j][n]*m.getElement(n,k);
                                }
                        }
                        if(numRows == mColumns)
                                return new DoubleSquareMatrix(array);
                        else
                                return new DoubleMatrix(array);
                } else {
                        throw new MatrixDimensionException("Incompatible matrices.");
                }
        }
        public AbstractDoubleMatrix multiply(final DoubleMatrix m) {
                if(numCols==m.numRows) {
                        final double array[][]=new double[numRows][m.numCols];
                        for(int j=0;j<numRows;j++) {
                                for(int k=0;k<m.numCols;k++) {
                                        array[j][k]=matrix[j][0]*m.matrix[0][k];
                                        for(int n=1;n<numCols;n++)
                                                array[j][k]+=matrix[j][n]*m.matrix[n][k];
                                }
                        }
                        if(numRows == m.numCols)
                                return new DoubleSquareMatrix(array);
                        else
                                return new DoubleMatrix(array);
                } else
                        throw new MatrixDimensionException("Incompatible matrices.");
        }

// DIRECT SUM

        /**
        * Returns the direct sum of this matrix and another.
        */
        public AbstractDoubleMatrix directSum(final AbstractDoubleMatrix m) {
                final double array[][]=new double[numRows+m.numRows][numCols+m.numCols];
                for(int i=0;i<numRows;i++) {
                        for(int j=0;j<numCols;j++)
                                array[i][j] = matrix[i][j];
                }
                for(int i=0;i<m.numRows;i++) {
                        for(int j=0;j<m.numCols;j++)
                                array[i+numRows][j+numCols] = m.getElement(i,j);
                }
                return new DoubleMatrix(array);
        }

// TENSOR PRODUCT

        /**
        * Returns the tensor product of this matrix and another.
        */
        public AbstractDoubleMatrix tensor(final AbstractDoubleMatrix m) {
                final double array[][]=new double[numRows*m.numRows][numCols*m.numCols];
                for(int i=0;i<numRows;i++) {
                        for(int j=0;j<numCols;j++) {
                                for(int k=0;k<m.numRows;j++) {
                                        for(int l=0;l<m.numCols;l++)
                                                array[i*m.numRows+k][j*m.numCols+l] = matrix[i][j]*m.getElement(k,l);
                                }
                        }
                }
                return new DoubleMatrix(array);
        }

// TRANSPOSE

        /**
        * Returns the transpose of this matrix.
        * @return a double matrix
        */
        public Matrix transpose() {
                final double array[][]=new double[numCols][numRows];
                for(int i=0;i<numRows;i++) {
                        array[0][i] = matrix[i][0];
                        for(int j=1;j<numCols;j++)
                                array[j][i] = matrix[i][j];
                }
                return new DoubleMatrix(array);
        }

// MAP ELEMENTS

        /**
        * Applies a function on all the matrix elements.
        * @param f a user-defined function
        * @return a double matrix
        */
        public AbstractDoubleMatrix mapElements(final Mapping f) {
                final double array[][]=new double[numRows][numCols];
                for(int i=0;i<numRows;i++) {
                        array[i][0] = f.map(matrix[i][0]);
                        for(int j=1;j<numCols;j++)
                                array[i][j] = f.map(matrix[i][j]);
                }
                return new DoubleMatrix(array);
        }
}
