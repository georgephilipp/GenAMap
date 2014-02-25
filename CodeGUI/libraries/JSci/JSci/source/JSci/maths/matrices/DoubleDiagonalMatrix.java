/* AUTO-GENERATED */
package JSci.maths.matrices;

import JSci.maths.ExtraMath;
import JSci.maths.Mapping;
import JSci.maths.DimensionException;
import JSci.maths.MaximumIterationsExceededException;
import JSci.maths.vectors.AbstractDoubleVector;
import JSci.maths.vectors.DoubleVector;
import JSci.maths.groups.AbelianGroup;
import JSci.maths.algebras.*;
import JSci.maths.fields.*;

/**
* The DoubleDiagonalMatrix class provides an object for encapsulating double diagonal matrices.
* @version 2.3
* @author Mark Hale
*/
public class DoubleDiagonalMatrix extends AbstractDoubleSquareMatrix implements DiagonalMatrix {
        /**
        * Diagonal data.
        */
        protected final double diag[];
        /**
        * Constructs an empty matrix.
        * @param size the number of rows/columns
        */
        public DoubleDiagonalMatrix(final int size) {
                this(new double[size]);
        }
        /**
        * Constructs a matrix from an array.
        * Any non-diagonal elements in the array are ignored.
        * @param array an assigned value
        * @exception MatrixDimensionException If the array is not square.
        */
        public DoubleDiagonalMatrix(final double array[][]) {
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
        public DoubleDiagonalMatrix(final double array[]) {
                super(array.length);
                diag=array;
        }
        /**
        * Creates an identity matrix.
        * @param size the number of rows/columns
        */
        public static DoubleDiagonalMatrix identity(final int size) {
                double array[]=new double[size];
                for(int i=0;i<size;i++)
                        array[i]=1;
                return new DoubleDiagonalMatrix(array);
        }
        /**
        * Compares two ${nativeTyp} matrices for equality.
        * @param m a double matrix
        */
        public boolean equals(AbstractDoubleMatrix m, double tol) {
                if(m instanceof DiagonalMatrix) {
                        if(numRows != m.rows() || numCols != m.columns())
                                return false;
			double sumSqr = 0;
			double delta = diag[0] - m.getElement(0,0);
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
        * Converts this matrix to an integer matrix.
        * @return an integer matrix
        */
        public AbstractIntegerMatrix toIntegerMatrix() {
                final int array[]=new int[numRows];
                for(int i=0;i<numRows;i++)
                        array[i]=Math.round((float)diag[i]);
                return new IntegerDiagonalMatrix(array);
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
        public double getElement(int i, int j) {
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
        public void setElement(int i, int j, final double x) {
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
        public double det() {
                double det=diag[0];
                for(int i=1;i<numRows;i++)
                        det*=diag[i];
                return det;
        }
        /**
        * Returns the trace.
        */
        public double trace() {
                double tr=diag[0];
                for(int i=1;i<numRows;i++)
                        tr+=diag[i];
                return tr;
        }
        /**
        * Returns the l<sup><img border=0 alt="infinity" src="doc-files/infinity.gif"></sup>-norm.
        * @author Taber Smith
        */
        public double infNorm() {
                double result=Math.abs(diag[0]);
                double tmpResult;
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
        * @param m a double matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public AbstractDoubleSquareMatrix add(final AbstractDoubleSquareMatrix m) {
                if(m instanceof DoubleDiagonalMatrix)
                        return add((DoubleDiagonalMatrix)m);
                if(m instanceof DiagonalMatrix)
                        return addDiagonal(m);
                if(m instanceof DoubleTridiagonalMatrix)
                        return add((DoubleTridiagonalMatrix)m);
                if(m instanceof TridiagonalMatrix)
                        return addTridiagonal(m);
                if(m instanceof DoubleSquareMatrix)
                        return add((DoubleSquareMatrix)m);

                if(numRows==m.rows() && numCols==m.columns()) {
                        final double array[][]=new double[numRows][numCols];
                        for(int i=0;i<numRows;i++) {
                                array[i][0]=m.getElement(i,0);
                                for(int j=1;j<numCols;j++)
                                        array[i][j]=m.getElement(i,j);
                        }
                        for(int i=0; i<numRows; i++)
                                array[i][i] += diag[i];
                        return new DoubleSquareMatrix(array);
                } else {
                        throw new MatrixDimensionException("Matrices are different sizes.");
                }
        }
        public DoubleSquareMatrix add(final DoubleSquareMatrix m) {
                if(numRows==m.numRows && numCols==m.numCols) {
                        final double array[][]=new double[numRows][numCols];
                        for(int i=0;i<numRows;i++)
                                System.arraycopy(m.matrix[i],0,array[i],0,numRows);
                        for(int i=0; i<numRows; i++)
                                array[i][i] += diag[i];
                        return new DoubleSquareMatrix(array);
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }
        /**
        * Returns the addition of this matrix and another.
        * @param m a double tridiagonal matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public DoubleTridiagonalMatrix add(final DoubleTridiagonalMatrix m) {
                if(numRows==m.numRows) {
                        final DoubleTridiagonalMatrix ans=new DoubleTridiagonalMatrix(numRows);
                        System.arraycopy(m.ldiag,0,ans.ldiag,0,m.ldiag.length);
                        System.arraycopy(m.udiag,0,ans.udiag,0,m.udiag.length);
                        ans.diag[0]=diag[0]+m.diag[0];
                        for(int i=1;i<numRows;i++)
                                ans.diag[i]=diag[i]+m.diag[i];
                        return ans;
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }
        private DoubleTridiagonalMatrix addTridiagonal(final AbstractDoubleSquareMatrix m) {
                int mRow=numRows;
                if(mRow==m.rows()) {
                        final DoubleTridiagonalMatrix ans=new DoubleTridiagonalMatrix(mRow);
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
        * @param m a double diagonal matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public DoubleDiagonalMatrix add(final DoubleDiagonalMatrix m) {
                if(numRows==m.numRows) {
                        final double array[]=new double[numRows];
                        array[0]=diag[0]+m.diag[0];
                        for(int i=1;i<numRows;i++)
                                array[i]=diag[i]+m.diag[i];
                        return new DoubleDiagonalMatrix(array);
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }
        private DoubleDiagonalMatrix addDiagonal(final AbstractDoubleSquareMatrix m) {
                if(numRows==m.numRows) {
                        final double array[]=new double[numRows];
                        array[0]=diag[0]+m.getElement(0,0);
                        for(int i=1;i<numRows;i++)
                                array[i]=diag[i]+m.getElement(i,i);
                        return new DoubleDiagonalMatrix(array);
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }

// SUBTRACTION

        /**
        * Returns the subtraction of this matrix by another.
        * @param m a double matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public AbstractDoubleSquareMatrix subtract(final AbstractDoubleSquareMatrix m) {
                if(m instanceof DoubleDiagonalMatrix)
                        return subtract((DoubleDiagonalMatrix)m);
                if(m instanceof DiagonalMatrix)
                        return subtractDiagonal(m);
                if(m instanceof DoubleTridiagonalMatrix)
                        return subtract((DoubleTridiagonalMatrix)m);
                if(m instanceof TridiagonalMatrix)
                        return subtractTridiagonal(m);
                if(m instanceof DoubleSquareMatrix)
                        return subtract((DoubleSquareMatrix)m);

                if(numRows==m.rows() && numCols==m.columns()) {
                        final double array[][]=new double[numRows][numCols];
                        for(int i=0;i<numRows;i++) {
                                array[i][0] = -m.getElement(i,0);
                                for(int j=1;j<numCols;j++)
                                        array[i][j] = -m.getElement(i,j);
                        }
                        for(int i=0; i<numRows; i++)
                                array[i][i] += diag[i];
                        return new DoubleSquareMatrix(array);
                } else {
                        throw new MatrixDimensionException("Matrices are different sizes.");
                }
        }
        public DoubleSquareMatrix subtract(final DoubleSquareMatrix m) {
                if(numRows==m.numRows && numCols==m.numCols) {
                        final double array[][]=new double[numRows][numCols];
                        for(int i=0;i<numRows;i++) {
                                array[i][0] = -m.matrix[i][0];
                                for(int j=1;j<numCols;j++)
                                        array[i][j] = -m.matrix[i][j];
                        }
                        for(int i=0; i<numRows; i++)
                                array[i][i] += diag[i];
                        return new DoubleSquareMatrix(array);
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }
        /**
        * Returns the subtraction of this matrix and another.
        * @param m a double tridiagonal matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public DoubleTridiagonalMatrix subtract(final DoubleTridiagonalMatrix m) {
                int mRow=numRows;
                if(mRow==m.numRows) {
                        final DoubleTridiagonalMatrix ans=new DoubleTridiagonalMatrix(mRow);
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
        private DoubleTridiagonalMatrix subtractTridiagonal(final AbstractDoubleSquareMatrix m) {
                int mRow=numRows;
                if(mRow==m.rows()) {
                        final DoubleTridiagonalMatrix ans=new DoubleTridiagonalMatrix(mRow);
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
        * @param m a double diagonal matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public DoubleDiagonalMatrix subtract(final DoubleDiagonalMatrix m) {
                if(numRows==m.numRows) {
                        final double array[]=new double[numRows];
                        array[0]=diag[0]-m.diag[0];
                        for(int i=1;i<numRows;i++)
                                array[i]=diag[i]-m.diag[i];
                        return new DoubleDiagonalMatrix(array);
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }
        private DoubleDiagonalMatrix subtractDiagonal(final AbstractDoubleSquareMatrix m) {
                if(numRows==m.numRows) {
                        final double array[]=new double[numRows];
                        array[0]=diag[0]-m.getElement(0,0);
                        for(int i=1;i<numRows;i++)
                                array[i]=diag[i]-m.getElement(i,i);
                        return new DoubleDiagonalMatrix(array);
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }

// SCALAR MULTIPLICATION

        /**
        * Returns the multiplication of this matrix by a scalar.
        * @param x a double.
        * @return a double diagonal matrix.
        */
        public AbstractDoubleMatrix scalarMultiply(final double x) {
                final double array[]=new double[numRows];
                array[0] = x*diag[0];
                for(int i=1;i<numRows;i++)
                        array[i] = x*diag[i];
                return new DoubleDiagonalMatrix(array);
        }

// SCALAR DIVISON

        /**
        * Returns the division of this matrix by a scalar.
        * @param x a double.
        * @return a double diagonal matrix.
        */
        public AbstractDoubleMatrix scalarDivide(final double x) {
                final double array[]=new double[numRows];
                array[0] = diag[0]/x;
                for(int i=1;i<numRows;i++)
                        array[i] = diag[i]/x;
                return new DoubleDiagonalMatrix(array);
        }

// SCALAR PRODUCT

        /**
        * Returns the scalar product of this matrix and another.
        * @param m a double matrix.
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public double scalarProduct(final AbstractDoubleSquareMatrix m) {
                if(m instanceof DoubleDiagonalMatrix)
                        return scalarProduct((DoubleDiagonalMatrix)m);
                if(m instanceof DoubleTridiagonalMatrix)
                        return scalarProduct((DoubleTridiagonalMatrix)m);
                if(m instanceof DoubleSquareMatrix)
                        return scalarProduct((DoubleSquareMatrix)m);

                if(numRows==m.rows() && numCols==m.columns()) {
                        double ans = diag[0]*m.getElement(0,0);
                        for(int i=1;i<numRows;i++)
                                ans += diag[i]*m.getElement(i,i);
                        return ans;
                } else {
                       throw new MatrixDimensionException("Matrices are different sizes.");
                }
        }
        public double scalarProduct(final DoubleSquareMatrix m) {
                if(numRows==m.numRows && numCols==m.numCols) {
                        double ans = diag[0]*m.matrix[0][0];
                        for(int i=1;i<numRows;i++)
                                ans += diag[i]*m.matrix[i][i];
                        return ans;
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }
        public double scalarProduct(final DoubleTridiagonalMatrix m) {
                if(numRows==m.numRows) {
                        double ans = diag[0]*m.diag[0];
                        for(int i=1;i<numRows;i++)
                                ans += diag[i]*m.diag[i];
                        return ans;
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }
        public double scalarProduct(final DoubleDiagonalMatrix m) {
                if(numRows==m.numRows) {
                        double ans = diag[0]*m.diag[0];
                        for(int i=1;i<numRows;i++)
                                ans += diag[i]*m.diag[i];
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
                        array[0]=diag[0]*v.getComponent(0);
                        for(int i=1;i<numRows;i++)
                                array[i]=diag[i]*v.getComponent(i);
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
        public AbstractDoubleSquareMatrix multiply(final AbstractDoubleSquareMatrix m) {
                if(m instanceof DoubleDiagonalMatrix)
                        return multiply((DoubleDiagonalMatrix)m);
                if(m instanceof DiagonalMatrix)
                        return multiplyDiagonal(m);
                if(m instanceof DoubleTridiagonalMatrix)
                        return multiply((DoubleTridiagonalMatrix)m);
                if(m instanceof TridiagonalMatrix)
                        return multiplyTridiagonal(m);
                if(m instanceof DoubleSquareMatrix)
                        return multiply((DoubleSquareMatrix)m);

                if(numCols==m.rows()) {
                        final int mColumns = m.columns();
                        final double array[][]=new double[numRows][mColumns];
                        for(int i=0; i<numRows; i++) {
                                array[i][0]=diag[0]*m.getElement(i,0);
                                for(int j=1; j<mColumns; j++)
                                        array[i][j]=diag[i]*m.getElement(i,j);
                        }
                        return new DoubleSquareMatrix(array);
                } else {
                        throw new MatrixDimensionException("Incompatible matrices.");
                }
        }
        public DoubleSquareMatrix multiply(final DoubleSquareMatrix m) {
                if(numCols==m.numRows) {
                        final double array[][]=new double[numRows][m.numCols];
                        for(int i=0; i<numRows; i++) {
                                array[i][0]=diag[0]*m.matrix[i][0];
                                for(int j=1; j<m.numCols; j++)
                                        array[i][j]=diag[i]*m.matrix[i][j];
                        }
                        return new DoubleSquareMatrix(array);
                } else
                        throw new MatrixDimensionException("Incompatible matrices.");
        }
        public DoubleTridiagonalMatrix multiply(final DoubleTridiagonalMatrix m) {
                int mRow=numRows;
                if(numCols==m.numRows) {
                        final DoubleTridiagonalMatrix ans=new DoubleTridiagonalMatrix(mRow);
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
        private DoubleTridiagonalMatrix multiplyTridiagonal(final AbstractDoubleSquareMatrix m) {
                int mRow=numRows;
                if(numCols==m.rows()) {
                        final DoubleTridiagonalMatrix ans=new DoubleTridiagonalMatrix(mRow);
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
        public DoubleDiagonalMatrix multiply(final DoubleDiagonalMatrix m) {
                if(numCols==m.numRows) {
                        final double array[]=new double[numRows];
                        array[0]=diag[0]*m.diag[0];
                        for(int i=1;i<numRows;i++) {
                                array[i]=diag[i]*m.diag[i];
                        }
                        return new DoubleDiagonalMatrix(array);
                } else
                        throw new MatrixDimensionException("Incompatible matrices.");
        }
        private DoubleDiagonalMatrix multiplyDiagonal(final AbstractDoubleSquareMatrix m) {
                if(numCols==m.rows()) {
                        final double array[]=new double[numRows];
                        array[0]=diag[0]*m.getElement(0,0);
                        for(int i=1;i<numRows;i++) {
                                array[i]=diag[i]*m.getElement(i,i);
                        }
                        return new DoubleDiagonalMatrix(array);
                } else {
                        throw new MatrixDimensionException("Incompatible matrices.");
                }
        }

// TRANSPOSE

        /**
        * Returns the transpose of this matrix.
        * @return a double matrix
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
                DoubleDiagonalMatrix U=this;
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
                qr[1]=this;
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

// MAP ELEMENTS

        /**
        * Applies a function on all the matrix elements.
        * @param f a user-defined function
        * @return a double matrix
        */
        public AbstractDoubleMatrix mapElements(final Mapping f) {
		double zeroValue = f.map(0.0);
		if(Math.abs(zeroValue) <= JSci.GlobalSettings.ZERO_TOL)
			return diagonalMap(f);
		else
			return generalMap(f, zeroValue);
	}
	private AbstractDoubleMatrix diagonalMap(Mapping f) {
                final double array[]=new double[numRows];
                array[0]=f.map(diag[0]);
                for(int i=1;i<numRows;i++)
                        array[i]=f.map(diag[i]);
                return new DoubleDiagonalMatrix(array);
        }
	private AbstractDoubleMatrix generalMap(Mapping f, double zeroValue) {
                final double array[][]=new double[numRows][numRows];
		for(int i=0; i<numRows; i++) {
			for(int j=0; j<numRows; j++) {
				array[i][j] = zeroValue;
			}
		}
                array[0][0]=f.map(diag[0]);
                for(int i=1;i<numRows;i++)
                        array[i][i]=f.map(diag[i]);
                return new DoubleSquareMatrix(array);
        }
}
