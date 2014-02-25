/* AUTO-GENERATED */
package JSci.maths.matrices;

import JSci.maths.ExtraMath;
import JSci.maths.Mapping;
import JSci.maths.DimensionException;
import JSci.maths.MaximumIterationsExceededException;
import JSci.maths.vectors.AbstractIntegerVector;
import JSci.maths.vectors.IntegerVector;
import JSci.maths.groups.AbelianGroup;
import JSci.maths.algebras.*;
import JSci.maths.fields.*;

/**
* The IntegerDiagonalMatrix class provides an object for encapsulating integer diagonal matrices.
* @version 2.3
* @author Mark Hale
*/
public class IntegerDiagonalMatrix extends AbstractIntegerSquareMatrix implements DiagonalMatrix {
        /**
        * Diagonal data.
        */
        protected final int diag[];
        /**
        * Constructs an empty matrix.
        * @param size the number of rows/columns
        */
        public IntegerDiagonalMatrix(final int size) {
                this(new int[size]);
        }
        /**
        * Constructs a matrix from an array.
        * Any non-diagonal elements in the array are ignored.
        * @param array an assigned value
        * @exception MatrixDimensionException If the array is not square.
        */
        public IntegerDiagonalMatrix(final int array[][]) {
                this(array.length);
                for(int i=0;i<array.length;i++) {
                        if(array[i].length != array.length)
                                throw new MatrixDimensionException("Array is not square.");
                        diag[i]=array[i][i];
                }
        }
        /**
        * Constructs a matrix by wrapping an array containing the diagonal elements.
        * @param array an assigned value
        */
        public IntegerDiagonalMatrix(final int array[]) {
                super(array.length);
                diag=array;
        }
        /**
        * Creates an identity matrix.
        * @param size the number of rows/columns
        */
        public static IntegerDiagonalMatrix identity(final int size) {
                int array[]=new int[size];
                for(int i=0;i<size;i++)
                        array[i]=1;
                return new IntegerDiagonalMatrix(array);
        }
        /**
        * Compares two ${nativeTyp} matrices for equality.
        * @param m a int matrix
        */
        public boolean equals(AbstractIntegerMatrix m, double tol) {
                if(m instanceof DiagonalMatrix) {
                        if(numRows != m.rows() || numCols != m.columns())
                                return false;
			int sumSqr = 0;
			int delta = diag[0] - m.getElement(0,0);
			sumSqr += delta*delta;
                        for(int i=1;i<numRows;i++) {
				delta = diag[i] - m.getElement(i,i);
				sumSqr += delta*delta;
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
                                buf.append(getElement(i,j));
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
                final double array[]=new double[numRows];
                for(int i=0;i<numRows;i++)
                        array[i]=diag[i];
                return new DoubleDiagonalMatrix(array);
        }
        /**
        * Converts this matrix to a complex matrix.
        * @return a complex matrix
        */
        public AbstractComplexMatrix toComplexMatrix() {
                final double array[]=new double[numRows];
                for(int i=0;i<numRows;i++)
                        array[i]=diag[i];
                return new ComplexDiagonalMatrix(array,new double[numRows]);
        }
        /**
        * Returns an element of the matrix.
        * @param i row index of the element
        * @param j column index of the element
        * @exception MatrixDimensionException If attempting to access an invalid element.
        */
        public int getElement(int i, int j) {
                if(i>=0 && i<numRows && j>=0 && j<numCols) {
                        if(i == j)
                                return diag[i];
                        else
                                return 0;
                } else
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
        public void setElement(int i, int j, final int x) {
                if(i>=0 && i<numRows && j>=0 && j<numCols) {
                        if(i == j)
                                diag[i] = x;
                        else
                                throw new MatrixDimensionException(getInvalidElementMsg(i,j));
                } else
                        throw new MatrixDimensionException(getInvalidElementMsg(i,j));
        }
        /**
        * Returns true if this matrix is symmetric.
        */
        public boolean isSymmetric() {
                return true;
        }
        /**
        * Returns the determinant.
        */
        public int det() {
                int det=diag[0];
                for(int i=1;i<numRows;i++)
                        det*=diag[i];
                return det;
        }
        /**
        * Returns the trace.
        */
        public int trace() {
                int tr=diag[0];
                for(int i=1;i<numRows;i++)
                        tr+=diag[i];
                return tr;
        }
        /**
        * Returns the l<sup><img border=0 alt="infinity" src="doc-files/infinity.gif"></sup>-norm.
        * @author Taber Smith
        */
        public int infNorm() {
                int result=Math.abs(diag[0]);
                int tmpResult;
                for(int i=1;i<numRows;i++) {
                        tmpResult=Math.abs(diag[i]);
                        if(tmpResult>result)
                                result=tmpResult;
                }
                return result;
        }
        /**
        * Returns the Frobenius (l<sup>2</sup>) norm.
        * @author Taber Smith
        */
        public double frobeniusNorm() {
                double result=diag[0];
                for(int i=1;i<numRows;i++)
                        result=ExtraMath.hypot(result,diag[i]);
                return result;
        }
        /**
        * Returns the operator norm.
        * @exception MaximumIterationsExceededException If it takes more than 50 iterations to determine an eigenvalue.
        */
        public double operatorNorm() throws MaximumIterationsExceededException {
                return infNorm();
        }

//============
// OPERATIONS
//============

// ADDITION

        /**
        * Returns the addition of this matrix and another.
        * @param m a int matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public AbstractIntegerSquareMatrix add(final AbstractIntegerSquareMatrix m) {
                if(m instanceof IntegerDiagonalMatrix)
                        return add((IntegerDiagonalMatrix)m);
                if(m instanceof DiagonalMatrix)
                        return addDiagonal(m);
                if(m instanceof IntegerTridiagonalMatrix)
                        return add((IntegerTridiagonalMatrix)m);
                if(m instanceof TridiagonalMatrix)
                        return addTridiagonal(m);
                if(m instanceof IntegerSquareMatrix)
                        return add((IntegerSquareMatrix)m);

                if(numRows==m.rows() && numCols==m.columns()) {
                        final int array[][]=new int[numRows][numCols];
                        for(int i=0;i<numRows;i++) {
                                array[i][0]=m.getElement(i,0);
                                for(int j=1;j<numCols;j++)
                                        array[i][j]=m.getElement(i,j);
                        }
                        for(int i=0; i<numRows; i++)
                                array[i][i] += diag[i];
                        return new IntegerSquareMatrix(array);
                } else {
                        throw new MatrixDimensionException("Matrices are different sizes.");
                }
        }
        public IntegerSquareMatrix add(final IntegerSquareMatrix m) {
                if(numRows==m.numRows && numCols==m.numCols) {
                        final int array[][]=new int[numRows][numCols];
                        for(int i=0;i<numRows;i++)
                                System.arraycopy(m.matrix[i],0,array[i],0,numRows);
                        for(int i=0; i<numRows; i++)
                                array[i][i] += diag[i];
                        return new IntegerSquareMatrix(array);
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }
        /**
        * Returns the addition of this matrix and another.
        * @param m a int tridiagonal matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public IntegerTridiagonalMatrix add(final IntegerTridiagonalMatrix m) {
                if(numRows==m.numRows) {
                        final IntegerTridiagonalMatrix ans=new IntegerTridiagonalMatrix(numRows);
                        System.arraycopy(m.ldiag,0,ans.ldiag,0,m.ldiag.length);
                        System.arraycopy(m.udiag,0,ans.udiag,0,m.udiag.length);
                        ans.diag[0]=diag[0]+m.diag[0];
                        for(int i=1;i<numRows;i++)
                                ans.diag[i]=diag[i]+m.diag[i];
                        return ans;
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }
        private IntegerTridiagonalMatrix addTridiagonal(final AbstractIntegerSquareMatrix m) {
                int mRow=numRows;
                if(mRow==m.rows()) {
                        final IntegerTridiagonalMatrix ans=new IntegerTridiagonalMatrix(mRow);
                        ans.diag[0]=diag[0]+m.getElement(0,0);
                        ans.udiag[0]=m.getElement(0,1);
                        mRow--;
                        for(int i=1;i<mRow;i++) {
                                ans.ldiag[i]=m.getElement(i,i-1);
                                ans.diag[i]=diag[i]+m.getElement(i,i);
                                ans.udiag[i]=m.getElement(i,i+1);
                        }
                        ans.ldiag[mRow]=m.getElement(mRow,mRow-1);
                        ans.diag[mRow]=diag[mRow]+m.getElement(mRow,mRow);
                        return ans;
                } else {
                        throw new MatrixDimensionException("Matrices are different sizes.");
                }
        }
        /**
        * Returns the addition of this matrix and another.
        * @param m a int diagonal matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public IntegerDiagonalMatrix add(final IntegerDiagonalMatrix m) {
                if(numRows==m.numRows) {
                        final int array[]=new int[numRows];
                        array[0]=diag[0]+m.diag[0];
                        for(int i=1;i<numRows;i++)
                                array[i]=diag[i]+m.diag[i];
                        return new IntegerDiagonalMatrix(array);
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }
        private IntegerDiagonalMatrix addDiagonal(final AbstractIntegerSquareMatrix m) {
                if(numRows==m.numRows) {
                        final int array[]=new int[numRows];
                        array[0]=diag[0]+m.getElement(0,0);
                        for(int i=1;i<numRows;i++)
                                array[i]=diag[i]+m.getElement(i,i);
                        return new IntegerDiagonalMatrix(array);
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }

// SUBTRACTION

        /**
        * Returns the subtraction of this matrix by another.
        * @param m a int matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public AbstractIntegerSquareMatrix subtract(final AbstractIntegerSquareMatrix m) {
                if(m instanceof IntegerDiagonalMatrix)
                        return subtract((IntegerDiagonalMatrix)m);
                if(m instanceof DiagonalMatrix)
                        return subtractDiagonal(m);
                if(m instanceof IntegerTridiagonalMatrix)
                        return subtract((IntegerTridiagonalMatrix)m);
                if(m instanceof TridiagonalMatrix)
                        return subtractTridiagonal(m);
                if(m instanceof IntegerSquareMatrix)
                        return subtract((IntegerSquareMatrix)m);

                if(numRows==m.rows() && numCols==m.columns()) {
                        final int array[][]=new int[numRows][numCols];
                        for(int i=0;i<numRows;i++) {
                                array[i][0] = -m.getElement(i,0);
                                for(int j=1;j<numCols;j++)
                                        array[i][j] = -m.getElement(i,j);
                        }
                        for(int i=0; i<numRows; i++)
                                array[i][i] += diag[i];
                        return new IntegerSquareMatrix(array);
                } else {
                        throw new MatrixDimensionException("Matrices are different sizes.");
                }
        }
        public IntegerSquareMatrix subtract(final IntegerSquareMatrix m) {
                if(numRows==m.numRows && numCols==m.numCols) {
                        final int array[][]=new int[numRows][numCols];
                        for(int i=0;i<numRows;i++) {
                                array[i][0] = -m.matrix[i][0];
                                for(int j=1;j<numCols;j++)
                                        array[i][j] = -m.matrix[i][j];
                        }
                        for(int i=0; i<numRows; i++)
                                array[i][i] += diag[i];
                        return new IntegerSquareMatrix(array);
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }
        /**
        * Returns the subtraction of this matrix and another.
        * @param m a int tridiagonal matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public IntegerTridiagonalMatrix subtract(final IntegerTridiagonalMatrix m) {
                int mRow=numRows;
                if(mRow==m.numRows) {
                        final IntegerTridiagonalMatrix ans=new IntegerTridiagonalMatrix(mRow);
                        ans.diag[0]=diag[0]-m.diag[0];
                        ans.udiag[0]=-m.udiag[0];
                        mRow--;
                        for(int i=1;i<mRow;i++) {
                                ans.ldiag[i]=-m.ldiag[i];
                                ans.diag[i]=diag[i]-m.diag[i];
                                ans.udiag[i]=-m.udiag[i];
                        }
                        ans.ldiag[mRow]=-m.ldiag[mRow];
                        ans.diag[mRow]=diag[mRow]-m.diag[mRow];
                        return ans;
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }
        private IntegerTridiagonalMatrix subtractTridiagonal(final AbstractIntegerSquareMatrix m) {
                int mRow=numRows;
                if(mRow==m.rows()) {
                        final IntegerTridiagonalMatrix ans=new IntegerTridiagonalMatrix(mRow);
                        ans.diag[0]=diag[0]-m.getElement(0,0);
                        ans.udiag[0]=-m.getElement(0,1);
                        mRow--;
                        for(int i=1;i<mRow;i++) {
                                ans.ldiag[i]=-m.getElement(i,i-1);
                                ans.diag[i]=diag[i]-m.getElement(i,i);
                                ans.udiag[i]=-m.getElement(i,i+1);
                        }
                        ans.ldiag[mRow]=-m.getElement(mRow,mRow-1);
                        ans.diag[mRow]=diag[mRow]-m.getElement(mRow,mRow);
                        return ans;
                } else {
                        throw new MatrixDimensionException("Matrices are different sizes.");
                }
        }
        /**
        * Returns the subtraction of this matrix and another.
        * @param m a int diagonal matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public IntegerDiagonalMatrix subtract(final IntegerDiagonalMatrix m) {
                if(numRows==m.numRows) {
                        final int array[]=new int[numRows];
                        array[0]=diag[0]-m.diag[0];
                        for(int i=1;i<numRows;i++)
                                array[i]=diag[i]-m.diag[i];
                        return new IntegerDiagonalMatrix(array);
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }
        private IntegerDiagonalMatrix subtractDiagonal(final AbstractIntegerSquareMatrix m) {
                if(numRows==m.numRows) {
                        final int array[]=new int[numRows];
                        array[0]=diag[0]-m.getElement(0,0);
                        for(int i=1;i<numRows;i++)
                                array[i]=diag[i]-m.getElement(i,i);
                        return new IntegerDiagonalMatrix(array);
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }

// SCALAR MULTIPLICATION

        /**
        * Returns the multiplication of this matrix by a scalar.
        * @param x a int.
        * @return a int diagonal matrix.
        */
        public AbstractIntegerMatrix scalarMultiply(final int x) {
                final int array[]=new int[numRows];
                array[0] = x*diag[0];
                for(int i=1;i<numRows;i++)
                        array[i] = x*diag[i];
                return new IntegerDiagonalMatrix(array);
        }

// SCALAR DIVISON


// SCALAR PRODUCT

        /**
        * Returns the scalar product of this matrix and another.
        * @param m a int matrix.
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public int scalarProduct(final AbstractIntegerSquareMatrix m) {
                if(m instanceof IntegerDiagonalMatrix)
                        return scalarProduct((IntegerDiagonalMatrix)m);
                if(m instanceof IntegerTridiagonalMatrix)
                        return scalarProduct((IntegerTridiagonalMatrix)m);
                if(m instanceof IntegerSquareMatrix)
                        return scalarProduct((IntegerSquareMatrix)m);

                if(numRows==m.rows() && numCols==m.columns()) {
                        int ans = diag[0]*m.getElement(0,0);
                        for(int i=1;i<numRows;i++)
                                ans += diag[i]*m.getElement(i,i);
                        return ans;
                } else {
                       throw new MatrixDimensionException("Matrices are different sizes.");
                }
        }
        public int scalarProduct(final IntegerSquareMatrix m) {
                if(numRows==m.numRows && numCols==m.numCols) {
                        int ans = diag[0]*m.matrix[0][0];
                        for(int i=1;i<numRows;i++)
                                ans += diag[i]*m.matrix[i][i];
                        return ans;
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }
        public int scalarProduct(final IntegerTridiagonalMatrix m) {
                if(numRows==m.numRows) {
                        int ans = diag[0]*m.diag[0];
                        for(int i=1;i<numRows;i++)
                                ans += diag[i]*m.diag[i];
                        return ans;
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }
        public int scalarProduct(final IntegerDiagonalMatrix m) {
                if(numRows==m.numRows) {
                        int ans = diag[0]*m.diag[0];
                        for(int i=1;i<numRows;i++)
                                ans += diag[i]*m.diag[i];
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
                        array[0]=diag[0]*v.getComponent(0);
                        for(int i=1;i<numRows;i++)
                                array[i]=diag[i]*v.getComponent(i);
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
        public AbstractIntegerSquareMatrix multiply(final AbstractIntegerSquareMatrix m) {
                if(m instanceof IntegerDiagonalMatrix)
                        return multiply((IntegerDiagonalMatrix)m);
                if(m instanceof DiagonalMatrix)
                        return multiplyDiagonal(m);
                if(m instanceof IntegerTridiagonalMatrix)
                        return multiply((IntegerTridiagonalMatrix)m);
                if(m instanceof TridiagonalMatrix)
                        return multiplyTridiagonal(m);
                if(m instanceof IntegerSquareMatrix)
                        return multiply((IntegerSquareMatrix)m);

                if(numCols==m.rows()) {
                        final int mColumns = m.columns();
                        final int array[][]=new int[numRows][mColumns];
                        for(int i=0; i<numRows; i++) {
                                array[i][0]=diag[0]*m.getElement(i,0);
                                for(int j=1; j<mColumns; j++)
                                        array[i][j]=diag[i]*m.getElement(i,j);
                        }
                        return new IntegerSquareMatrix(array);
                } else {
                        throw new MatrixDimensionException("Incompatible matrices.");
                }
        }
        public IntegerSquareMatrix multiply(final IntegerSquareMatrix m) {
                if(numCols==m.numRows) {
                        final int array[][]=new int[numRows][m.numCols];
                        for(int i=0; i<numRows; i++) {
                                array[i][0]=diag[0]*m.matrix[i][0];
                                for(int j=1; j<m.numCols; j++)
                                        array[i][j]=diag[i]*m.matrix[i][j];
                        }
                        return new IntegerSquareMatrix(array);
                } else
                        throw new MatrixDimensionException("Incompatible matrices.");
        }
        public IntegerTridiagonalMatrix multiply(final IntegerTridiagonalMatrix m) {
                int mRow=numRows;
                if(numCols==m.numRows) {
                        final IntegerTridiagonalMatrix ans=new IntegerTridiagonalMatrix(mRow);
                        ans.diag[0]=diag[0]*m.diag[0];
                        ans.udiag[0]=diag[0]*m.udiag[0];
                        mRow--;
                        for(int i=1;i<mRow;i++) {
                                ans.ldiag[i]=diag[i]*m.ldiag[i];
                                ans.diag[i]=diag[i]*m.diag[i];
                                ans.udiag[i]=diag[i]*m.udiag[i];
                        }
                        ans.ldiag[mRow]=diag[mRow]*m.ldiag[mRow];
                        ans.diag[mRow]=diag[mRow]*m.diag[mRow];
                        return ans;
                } else
                        throw new MatrixDimensionException("Incompatible matrices.");
        }
        private IntegerTridiagonalMatrix multiplyTridiagonal(final AbstractIntegerSquareMatrix m) {
                int mRow=numRows;
                if(numCols==m.rows()) {
                        final IntegerTridiagonalMatrix ans=new IntegerTridiagonalMatrix(mRow);
                        ans.diag[0]=diag[0]*m.getElement(0,0);
                        ans.udiag[0]=diag[0]*m.getElement(0,1);
                        mRow--;
                        for(int i=1;i<mRow;i++) {
                                ans.ldiag[i]=diag[i]*m.getElement(i,i-1);
                                ans.diag[i]=diag[i]*m.getElement(i,i);
                                ans.udiag[i]=diag[i]*m.getElement(i,i+1);
                        }
                        ans.ldiag[mRow]=diag[mRow]*m.getElement(mRow,mRow-1);
                        ans.diag[mRow]=diag[mRow]*m.getElement(mRow,mRow);
                        return ans;
                } else {
                        throw new MatrixDimensionException("Incompatible matrices.");
                }
        }
        public IntegerDiagonalMatrix multiply(final IntegerDiagonalMatrix m) {
                if(numCols==m.numRows) {
                        final int array[]=new int[numRows];
                        array[0]=diag[0]*m.diag[0];
                        for(int i=1;i<numRows;i++) {
                                array[i]=diag[i]*m.diag[i];
                        }
                        return new IntegerDiagonalMatrix(array);
                } else
                        throw new MatrixDimensionException("Incompatible matrices.");
        }
        private IntegerDiagonalMatrix multiplyDiagonal(final AbstractIntegerSquareMatrix m) {
                if(numCols==m.rows()) {
                        final int array[]=new int[numRows];
                        array[0]=diag[0]*m.getElement(0,0);
                        for(int i=1;i<numRows;i++) {
                                array[i]=diag[i]*m.getElement(i,i);
                        }
                        return new IntegerDiagonalMatrix(array);
                } else {
                        throw new MatrixDimensionException("Incompatible matrices.");
                }
        }

// TRANSPOSE

        /**
        * Returns the transpose of this matrix.
        * @return a int matrix
        */
        public Matrix transpose() {
                return this;
        }

// INVERSE

        /**
        * Returns the inverse of this matrix.
        * @return a double diagonal matrix
        */
        public AbstractDoubleSquareMatrix inverse() {
                final double array[]=new double[numRows];
                array[0]=1.0/diag[0];
                for(int i=1;i<numRows;i++)
                        array[i]=1.0/diag[i];
                return new DoubleDiagonalMatrix(array);
        }

// LU DECOMPOSITION

        /**
        * Returns the LU decomposition of this matrix.
        * @param pivot an empty array of length <code>rows()+1</code>
        * to hold the pivot information (null if not interested).
        * The last array element will contain the parity.
        * @return an array with [0] containing the L-matrix
        * and [1] containing the U-matrix.
        */
        public AbstractDoubleSquareMatrix[] luDecompose(int pivot[]) {
                AbstractDoubleSquareMatrix[] LU = luDecompose_cache(pivot);
                if(LU != null)
                    return LU;
                if(pivot==null)
                        pivot=new int[numRows+1];
                for(int i=0;i<numRows;i++)
                        pivot[i]=i;
                pivot[numRows]=1;
                DoubleDiagonalMatrix L=DoubleDiagonalMatrix.identity(numRows);
                DoubleDiagonalMatrix U=(DoubleDiagonalMatrix)this.toDoubleMatrix();
                int[] LUpivot=new int[pivot.length];
                System.arraycopy(pivot,0,LUpivot,0,pivot.length);
                luCache = new DoubleLUCache(L, U, LUpivot);
                return new DoubleDiagonalMatrix[] {L, U};
        }
        /**
        * Returns the LU decomposition of this matrix.
        * @return an array with [0] containing the L-matrix
        * and [1] containing the U-matrix.
        * @jsci.planetmath LUDecomposition
        */
        public AbstractDoubleSquareMatrix[] luDecompose() {
                return luDecompose(null);
        }

// CHOLESKY DECOMPOSITION

        /**
        * Returns the Cholesky decomposition of this matrix.
        * Matrix must be symmetric and positive definite.
        * @return an array with [0] containing the L-matrix and [1] containing the U-matrix.
        */
        public AbstractDoubleSquareMatrix[] choleskyDecompose() {
                final AbstractDoubleSquareMatrix lu[]=new AbstractDoubleSquareMatrix[2];
                final double array[]=new double[numRows];
                array[0]=Math.sqrt(diag[0]);
                for(int i=1;i<numRows;i++)
                        array[i]=Math.sqrt(diag[i]);
                lu[0]=new DoubleDiagonalMatrix(array);
                lu[1]=lu[0];
                return lu;
        }

// QR DECOMPOSITION

        /**
        * Returns the QR decomposition of this matrix.
        * @return an array with [0] containing the Q-matrix and [1] containing the R-matrix.
        * @jsci.planetmath QRDecomposition
        */
        public AbstractDoubleSquareMatrix[] qrDecompose() {
                final AbstractDoubleSquareMatrix qr[]=new AbstractDoubleSquareMatrix[2];
                qr[0]=DoubleDiagonalMatrix.identity(numRows);
                qr[1]=(AbstractDoubleSquareMatrix)this.toDoubleMatrix();
                return qr;
        }

// SINGULAR VALUE DECOMPOSITION

        /**
        * Returns the singular value decomposition of this matrix.
        * @return an array with [0] containing the U-matrix, [1] containing the S-matrix and [2] containing the V-matrix.
        */
        public AbstractDoubleSquareMatrix[] singularValueDecompose() {
                final int N=numRows;
                final int Nm1=N-1;
                final double arrayU[]=new double[N];
                final double arrayS[]=new double[N];
                final double arrayV[]=new double[N];
                for(int i=0;i<Nm1;i++) {
                        arrayU[i]=-1.0;
                        arrayS[i]=Math.abs(diag[i]);
                        arrayV[i]=diag[i]<0.0 ? 1.0 : -1.0;
                }
                arrayU[Nm1]=1.0;
                arrayS[Nm1]=Math.abs(diag[Nm1]);
                arrayV[Nm1]=diag[Nm1]<0.0 ? -1.0 : 1.0;
                final AbstractDoubleSquareMatrix svd[]=new AbstractDoubleSquareMatrix[3];
                svd[0]=new DoubleDiagonalMatrix(arrayU);
                svd[1]=new DoubleDiagonalMatrix(arrayS);
                svd[2]=new DoubleDiagonalMatrix(arrayV);
                return svd;
        }

}
