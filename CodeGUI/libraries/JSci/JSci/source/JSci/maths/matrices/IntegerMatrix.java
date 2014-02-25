/* AUTO-GENERATED */
package JSci.maths.matrices;

import JSci.maths.ExtraMath;
import JSci.maths.Mapping;
import JSci.maths.DimensionException;
import JSci.maths.vectors.AbstractIntegerVector;
import JSci.maths.vectors.IntegerVector;
import JSci.maths.groups.AbelianGroup;
import JSci.maths.algebras.*;
import JSci.maths.fields.*;

/**
* The IntegerMatrix class provides an object for encapsulating integer matrices.
* @version 2.2
* @author Mark Hale
*/
public class IntegerMatrix extends AbstractIntegerMatrix {
        /**
        * Array containing the elements of the matrix.
        */
        protected final int matrix[][];
        /**
        * Constructs a matrix by wrapping an array.
        * @param array an assigned value
        */
        public IntegerMatrix(final int array[][]) {
                super(array.length, array[0].length);
                matrix=array;
        }
        /**
        * Constructs an empty matrix.
        */
        public IntegerMatrix(final int rows,final int cols) {
                this(new int[rows][cols]);
        }
        /**
        * Constructs a matrix from an array of vectors (columns).
        * @param array an assigned value
        */
        public IntegerMatrix(final AbstractIntegerVector array[]) {
                this(array[0].dimension(), array.length);
                for(int i=0;i<numRows;i++) {
                        for(int j=0;j<numCols;j++)
                                matrix[i][j]=array[j].getComponent(i);
                }
        }
        /**
        * Compares two ${nativeTyp} matrices for equality.
        * @param m a int matrix
        */
        public boolean equals(AbstractIntegerMatrix m, double tol) {
                if(m != null && numRows == m.rows() && numCols == m.columns()) {
			int sumSqr = 0;
                        for(int i=0;i<numRows;i++) {
                                for(int j=0;j<numCols;j++) {
					int delta = matrix[i][j]-m.getElement(i,j);
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
        * Converts this matrix to a double matrix.
        * @return a double matrix
        */
        public AbstractDoubleMatrix toDoubleMatrix() {
                final double ans[][]=new double[numRows][numCols];
                for(int i=0;i<numRows;i++) {
                        for(int j=0;j<numCols;j++)
                                ans[i][j]=matrix[i][j];
                }
                return new DoubleMatrix(ans);
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
        public int getElement(int i, int j) {
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
        public void setElement(int i, int j, int x) {
                if(i>=0 && i<numRows && j>=0 && j<numCols)
                        matrix[i][j]=x;
                else
                        throw new MatrixDimensionException(getInvalidElementMsg(i,j));
        }
        /**
        * Returns the l<sup><img border=0 alt="infinity" src="doc-files/infinity.gif"></sup>-norm.
        * @author Taber Smith
        */
        public int infNorm() {
                int result=0,tmpResult;
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
                final int array[][]=new int[numRows][numCols];
                for(int i=0;i<numRows;i++) {
                        array[i][0] = -matrix[i][0];
                        for(int j=1;j<numCols;j++)
                                array[i][j] = -matrix[i][j];
                }
                return new IntegerMatrix(array);
        }

// ADDITION

        /**
        * Returns the addition of this matrix and another.
        * @param m a int matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public AbstractIntegerMatrix add(final AbstractIntegerMatrix m) {
                if(numRows==m.rows() && numCols==m.columns()) {
                        final int array[][]=new int[numRows][numCols];
                        for(int i=0;i<numRows;i++) {
                                array[i][0] = matrix[i][0]+m.getElement(i,0);
                                for(int j=1;j<numCols;j++)
                                        array[i][j] = matrix[i][j]+m.getElement(i,j);
                        }
                        return new IntegerMatrix(array);
                } else {
                        throw new MatrixDimensionException("Matrices are different sizes.");
                }
        }

// SUBTRACTION

        /**
        * Returns the subtraction of this matrix by another.
        * @param m a int matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public AbstractIntegerMatrix subtract(final AbstractIntegerMatrix m) {
                if(numRows==m.rows() && numCols==m.columns()) {
                        final int array[][]=new int[numRows][numCols];
                        for(int i=0;i<numRows;i++) {
                                array[i][0] = matrix[i][0]-m.getElement(i,0);
                                for(int j=1;j<numCols;j++)
                                        array[i][j] = matrix[i][j]-m.getElement(i,j);
                        }
                        return new IntegerMatrix(array);
                } else {
                        throw new MatrixDimensionException("Matrices are different sizes.");
                }
        }

// SCALAR MULTIPLICATION

        /**
        * Returns the multiplication of this matrix by a scalar.
        * @param x a int.
        * @return a int matrix.
        */
        public AbstractIntegerMatrix scalarMultiply(final int x) {
                final int array[][]=new int[numRows][numCols];
                for(int i=0;i<numRows;i++) {
                        array[i][0] = x*matrix[i][0];
                        for(int j=1;j<numCols;j++)
                                array[i][j] = x*matrix[i][j];
                }
                return new IntegerMatrix(array);
        }

// SCALAR DIVISON


// SCALAR PRODUCT

        /**
        * Returns the scalar product of this matrix and another.
        * @param m a int matrix.
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public int scalarProduct(final AbstractIntegerMatrix m) {
                if(m instanceof IntegerMatrix)
                        return scalarProduct((IntegerMatrix)m);

                if(numRows==m.rows() && numCols==m.columns()) {
                        int ans=0;
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
        public int scalarProduct(final IntegerMatrix m) {
                if(numRows==m.numRows && numCols==m.numCols) {
                        int ans=0;
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
        * @param v a int vector.
        * @exception DimensionException If the matrix and vector are incompatible.
        */
        public AbstractIntegerVector multiply(final AbstractIntegerVector v) {
                if(numCols==v.dimension()) {
                        final int array[]=new int[numRows];
                        for(int i=0;i<numRows;i++) {
                                array[i]=matrix[i][0]*v.getComponent(0);
                                for(int j=1;j<numCols;j++)
                                        array[i]+=matrix[i][j]*v.getComponent(j);
                        }
                        return new IntegerVector(array);
                } else {
                        throw new DimensionException("Matrix and vector are incompatible.");
                }
        }
        /**
        * Returns the multiplication of this matrix and another.
        * @param m a int matrix
        * @return a AbstractIntegerMatrix or a AbstractIntegerSquareMatrix as appropriate
        * @exception MatrixDimensionException If the matrices are incompatible.
        */
        public AbstractIntegerMatrix multiply(final AbstractIntegerMatrix m) {
                if(m instanceof IntegerMatrix)
                        return multiply((IntegerMatrix)m);

                if(numCols==m.rows()) {
                        final int mColumns = m.columns();
                        final int array[][]=new int[numRows][mColumns];
                        for(int j=0; j<numRows; j++) {
                                for(int k=0; k<mColumns; k++) {
                                        array[j][k] = matrix[j][0]*m.getElement(0,k);
                                        for(int n=1; n<numCols; n++)
                                                array[j][k] += matrix[j][n]*m.getElement(n,k);
                                }
                        }
                        if(numRows == mColumns)
                                return new IntegerSquareMatrix(array);
                        else
                                return new IntegerMatrix(array);
                } else {
                        throw new MatrixDimensionException("Incompatible matrices.");
                }
        }
        public AbstractIntegerMatrix multiply(final IntegerMatrix m) {
                if(numCols==m.numRows) {
                        final int array[][]=new int[numRows][m.numCols];
                        for(int j=0;j<numRows;j++) {
                                for(int k=0;k<m.numCols;k++) {
                                        array[j][k]=matrix[j][0]*m.matrix[0][k];
                                        for(int n=1;n<numCols;n++)
                                                array[j][k]+=matrix[j][n]*m.matrix[n][k];
                                }
                        }
                        if(numRows == m.numCols)
                                return new IntegerSquareMatrix(array);
                        else
                                return new IntegerMatrix(array);
                } else
                        throw new MatrixDimensionException("Incompatible matrices.");
        }

// DIRECT SUM

        /**
        * Returns the direct sum of this matrix and another.
        */
        public AbstractIntegerMatrix directSum(final AbstractIntegerMatrix m) {
                final int array[][]=new int[numRows+m.numRows][numCols+m.numCols];
                for(int i=0;i<numRows;i++) {
                        for(int j=0;j<numCols;j++)
                                array[i][j] = matrix[i][j];
                }
                for(int i=0;i<m.numRows;i++) {
                        for(int j=0;j<m.numCols;j++)
                                array[i+numRows][j+numCols] = m.getElement(i,j);
                }
                return new IntegerMatrix(array);
        }

// TENSOR PRODUCT

        /**
        * Returns the tensor product of this matrix and another.
        */
        public AbstractIntegerMatrix tensor(final AbstractIntegerMatrix m) {
                final int array[][]=new int[numRows*m.numRows][numCols*m.numCols];
                for(int i=0;i<numRows;i++) {
                        for(int j=0;j<numCols;j++) {
                                for(int k=0;k<m.numRows;j++) {
                                        for(int l=0;l<m.numCols;l++)
                                                array[i*m.numRows+k][j*m.numCols+l] = matrix[i][j]*m.getElement(k,l);
                                }
                        }
                }
                return new IntegerMatrix(array);
        }

// TRANSPOSE

        /**
        * Returns the transpose of this matrix.
        * @return a int matrix
        */
        public Matrix transpose() {
                final int array[][]=new int[numCols][numRows];
                for(int i=0;i<numRows;i++) {
                        array[0][i] = matrix[i][0];
                        for(int j=1;j<numCols;j++)
                                array[j][i] = matrix[i][j];
                }
                return new IntegerMatrix(array);
        }

}
