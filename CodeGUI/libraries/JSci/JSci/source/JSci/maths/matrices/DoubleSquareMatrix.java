/* AUTO-GENERATED */
package JSci.maths.matrices;

import JSci.maths.ArrayMath;
import JSci.maths.ExtraMath;
import JSci.maths.LinearMath;
import JSci.maths.Mapping;
import JSci.maths.DimensionException;
import JSci.maths.MaximumIterationsExceededException;
import JSci.maths.vectors.AbstractDoubleVector;
import JSci.maths.vectors.DoubleVector;
import JSci.maths.groups.AbelianGroup;
import JSci.maths.algebras.*;
import JSci.maths.fields.*;

/**
* The DoubleSquareMatrix class provides an object for encapsulating double square matrices.
* @version 2.3
* @author Mark Hale
*/
public class DoubleSquareMatrix extends AbstractDoubleSquareMatrix {
        /**
        * Array containing the elements of the matrix.
        */
        protected final double matrix[][];
        /**
        * Constructs a matrix by wrapping an array.
        * @param array an assigned value
        */
        public DoubleSquareMatrix(final double array[][]) {
                super(array.length);
                if(!ArrayMath.isSquare(array))
                        throw new MatrixDimensionException("Array is not square.");
                matrix=array;
        }
        /**
        * Constructs an empty matrix.
        */
        public DoubleSquareMatrix(final int size) {
                this(new double[size][size]);
        }
        /**
        * Constructs a matrix from an array of vectors (columns).
        * @param array an assigned value
        */
        public DoubleSquareMatrix(final AbstractDoubleVector array[]) {
                this(array.length);
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
                return new IntegerSquareMatrix(ans);
        }
        /**
        * Converts this matrix to a complex matrix.
        * @return a complex matrix
        */
        public AbstractComplexMatrix toComplexMatrix() {
                ComplexSquareMatrix cm = new ComplexSquareMatrix(numRows);
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
                double result=0;
                for(int i=0;i<numRows;i++) {
                        double tmpResult=0;
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
        /**
        * Returns the determinant.
        */
        public double det() {
                if(numRows==2) {
                        return matrix[0][0]*matrix[1][1]-matrix[0][1]*matrix[1][0];
                } else {
                        int[] luPivot = new int[numRows+1];
                        final DoubleSquareMatrix lu[] = (DoubleSquareMatrix[]) this.luDecompose(luPivot);
                        double det=lu[1].matrix[0][0];
                        for(int i=1;i<numRows;i++)
                                det*=lu[1].matrix[i][i];
                        return det*luPivot[numRows];
                }
        }
        /**
        * Returns the trace.
        */
        public double trace() {
                double result=matrix[0][0];
                for(int i=1;i<numRows;i++)
                        result+=matrix[i][i];
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
                return new DoubleSquareMatrix(array);
        }

// ADDITION

        /**
        * Returns the addition of this matrix and another.
        * @param m a double matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public AbstractDoubleSquareMatrix add(final AbstractDoubleSquareMatrix m) {
                if(m instanceof DoubleSquareMatrix)
                        return add((DoubleSquareMatrix)m);

                if(numRows==m.rows() && numCols==m.columns()) {
                        final double array[][]=new double[numRows][numCols];
                        for(int i=0;i<numRows;i++) {
                                array[i][0] = matrix[i][0]+m.getElement(i,0);
                                for(int j=1;j<numCols;j++)
                                        array[i][j] = matrix[i][j]+m.getElement(i,j);
                        }
                        return new DoubleSquareMatrix(array);
                } else {
                        throw new MatrixDimensionException("Matrices are different sizes.");
                }
        }
        public DoubleSquareMatrix add(final DoubleSquareMatrix m) {
                if(numRows==m.numRows && numCols==m.numCols) {
                        final double array[][]=new double[numRows][numCols];
                        for(int j,i=0;i<numRows;i++) {
                                array[i][0]=matrix[i][0]+m.matrix[i][0];
                                for(j=1;j<numCols;j++)
                                        array[i][j]=matrix[i][j]+m.matrix[i][j];
                        }
                        return new DoubleSquareMatrix(array);
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
                if(m instanceof DoubleSquareMatrix)
                        return subtract((DoubleSquareMatrix)m);

                if(numRows==m.rows() && numCols==m.columns()) {
                        final double array[][]=new double[numRows][numCols];
                        for(int i=0;i<numRows;i++) {
                                array[i][0] = matrix[i][0]-m.getElement(i,0);
                                for(int j=1;j<numCols;j++)
                                        array[i][j] = matrix[i][j]-m.getElement(i,j);
                        }
                        return new DoubleSquareMatrix(array);
                } else {
                        throw new MatrixDimensionException("Matrices are different sizes.");
                }
        }
        public DoubleSquareMatrix subtract(final DoubleSquareMatrix m) {
                if(numRows==m.numRows && numCols==m.numCols) {
                        final double array[][]=new double[numRows][numCols];
                        for(int i=0;i<numRows;i++) {
                                array[i][0]=matrix[i][0]-m.matrix[i][0];
                                for(int j=1;j<numCols;j++)
                                        array[i][j]=matrix[i][j]-m.matrix[i][j];
                        }
                        return new DoubleSquareMatrix(array);
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }

// SCALAR MULTIPLICATION

        /**
        * Returns the multiplication of this matrix by a scalar.
        * @param x a double.
        * @return a double square matrix.
        */
        public AbstractDoubleMatrix scalarMultiply(final double x) {
                final double array[][]=new double[numRows][numCols];
                for(int i=0;i<numRows;i++) {
                        array[i][0] = x*matrix[i][0];
                        for(int j=1;j<numCols;j++)
                                array[i][j] = x*matrix[i][j];
                }
                return new DoubleSquareMatrix(array);
        }

// SCALAR DIVISON

        /**
        * Returns the division of this matrix by a scalar.
        * @param x a double.
        * @return a double square matrix.
        */
        public AbstractDoubleMatrix scalarDivide(final double x) {
                final double array[][]=new double[numRows][numCols];
                for(int i=0;i<numRows;i++) {
                        array[i][0] = matrix[i][0]/x;
                        for(int j=1;j<numCols;j++)
                                array[i][j] = matrix[i][j]/x;
                }
                return new DoubleSquareMatrix(array);
        }

// SCALAR PRODUCT

        /**
        * Returns the scalar product of this matrix and another.
        * @param m a double matrix.
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public double scalarProduct(final AbstractDoubleSquareMatrix m) {
                if(m instanceof DoubleSquareMatrix)
                        return scalarProduct((DoubleSquareMatrix)m);

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
        public double scalarProduct(final DoubleSquareMatrix m) {
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
        public AbstractDoubleSquareMatrix multiply(final AbstractDoubleSquareMatrix m) {
                if(m instanceof DoubleSquareMatrix)
                        return multiply((DoubleSquareMatrix)m);

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
                        return new DoubleSquareMatrix(array);
                } else {
                        throw new MatrixDimensionException("Incompatible matrices.");
                }
        }
        public DoubleSquareMatrix multiply(final DoubleSquareMatrix m) {
                if(numCols==m.numRows) {
                        final double array[][]=new double[numRows][m.numCols];
                        for(int j=0;j<numRows;j++) {
                                for(int k=0;k<m.numCols;k++) {
                                        array[j][k]=matrix[j][0]*m.matrix[0][k];
                                        for(int n=1;n<numCols;n++)
                                                array[j][k]+=matrix[j][n]*m.matrix[n][k];
                                }
                        }
                        return new DoubleSquareMatrix(array);
                } else
                        throw new MatrixDimensionException("Incompatible matrices.");
        }

// DIRECT SUM

        /**
        * Returns the direct sum of this matrix and another.
        */
        public AbstractDoubleSquareMatrix directSum(final AbstractDoubleSquareMatrix m) {
                final double array[][]=new double[numRows+m.numRows][numCols+m.numCols];
                for(int i=0;i<numRows;i++) {
                        for(int j=0;j<numCols;j++)
                                array[i][j] = matrix[i][j];
                }
                for(int i=0;i<m.numRows;i++) {
                        for(int j=0;j<m.numCols;j++)
                                array[i+numRows][j+numCols] = m.getElement(i,j);
                }
                return new DoubleSquareMatrix(array);
        }

// TENSOR PRODUCT

        /**
        * Returns the tensor product of this matrix and another.
        */
        public AbstractDoubleSquareMatrix tensor(final AbstractDoubleSquareMatrix m) {
                final double array[][]=new double[numRows*m.numRows][numCols*m.numCols];
                for(int i=0;i<numRows;i++) {
                        for(int j=0;j<numCols;j++) {
                                for(int k=0;k<m.numRows;j++) {
                                        for(int l=0;l<m.numCols;l++)
                                                array[i*m.numRows+k][j*m.numCols+l] = matrix[i][j]*m.getElement(k,l);
                                }
                        }
                }
                return new DoubleSquareMatrix(array);
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
                return new DoubleSquareMatrix(array);
        }

// INVERSE

        /**
        * Returns the inverse of this matrix.
        * @return a double square matrix.
        */
        public AbstractDoubleSquareMatrix inverse() {
                final int N=numRows;
                final double arrayL[][]=new double[N][N];
                final double arrayU[][]=new double[N][N];
                final int[] luPivot = new int[numRows+1];
                final DoubleSquareMatrix lu[] = (DoubleSquareMatrix[]) this.luDecompose(luPivot);
                arrayL[0][0]=1.0/lu[0].matrix[0][0];
                arrayU[0][0]=1.0/lu[1].matrix[0][0];
                for(int i=1;i<N;i++) {
                        arrayL[i][i]=1.0/lu[0].matrix[i][i];
                        arrayU[i][i]=1.0/lu[1].matrix[i][i];
                }
                for(int i=0;i<N-1;i++) {
                        for(int j=i+1;j<N;j++) {
                                double tmpL=0.0, tmpU=0.0;
                                for(int k=i;k<j;k++) {
                                        tmpL-=lu[0].matrix[j][k]*arrayL[k][i];
                                        tmpU-=arrayU[i][k]*lu[1].matrix[k][j];
                                }
                                arrayL[j][i]=tmpL/lu[0].matrix[j][j];
                                arrayU[i][j]=tmpU/lu[1].matrix[j][j];
                        }
                }
                // matrix multiply arrayU x arrayL
                final double inv[][]=new double[N][N];
                for(int i=0;i<N;i++) {
                        for(int j=0;j<i;j++) {
                                for(int k=i;k<N;k++)
                                        inv[i][luPivot[j]]+=arrayU[i][k]*arrayL[k][j];
                        }
                        for(int j=i;j<N;j++) {
                                for(int k=j;k<N;k++)
                                        inv[i][luPivot[j]]+=arrayU[i][k]*arrayL[k][j];
                        }
                }
                return new DoubleSquareMatrix(inv);
        }

// LU DECOMPOSITION

        /**
        * Returns the LU decomposition of this matrix.
        * @param pivot an empty array of length <code>rows()+1</code>
        * to hold the pivot information (null if not interested).
        * The last array element will contain the parity.
        * @return an array with [0] containing the L-matrix
        * and [1] containing the U-matrix.
        * @jsci.planetmath LUDecomposition
        */
        public final AbstractDoubleSquareMatrix[] luDecompose(int pivot[]) {
                AbstractDoubleSquareMatrix[] LU = luDecompose_cache(pivot);
                if(LU != null)
                    return LU;
                int pivotrow;
                final int N=numRows;
                final double arrayL[][]=new double[N][N];
                final double arrayU[][]=new double[N][N];
                double tmp;
                if(pivot==null)
                        pivot=new int[N+1];
                for(int i=0;i<N;i++)
                        pivot[i]=i;
                pivot[N]=1;
        // LU decomposition to arrayU
                for(int j=0;j<N;j++) {
                        for(int i=0;i<j;i++) {
                                tmp=matrix[pivot[i]][j];
                                for(int k=0;k<i;k++)
                                        tmp-=arrayU[i][k]*arrayU[k][j];
                                arrayU[i][j]=tmp;
                        }
                        double max=0.0;
                        pivotrow=j;
                        for(int i=j;i<N;i++) {
                                tmp=matrix[pivot[i]][j];
                                for(int k=0;k<j;k++)
                                        tmp-=arrayU[i][k]*arrayU[k][j];
                                arrayU[i][j]=tmp;
                        // while we're here search for a pivot for arrayU[j][j]
                                tmp=Math.abs(tmp);
                                if(tmp>max) {
                                        max=tmp;
                                        pivotrow=i;
                                }
                        }
                // swap row j with pivotrow
                        if(pivotrow!=j) {
                                double[] tmprow = arrayU[j];
                                arrayU[j] = arrayU[pivotrow];
                                arrayU[pivotrow] = tmprow;
                                int k=pivot[j];
                                pivot[j]=pivot[pivotrow];
                                pivot[pivotrow]=k;
                                // update parity
                                pivot[N]=-pivot[N];
                        }
                // divide by pivot
                        tmp=arrayU[j][j];
                        for(int i=j+1;i<N;i++)
                                arrayU[i][j]/=tmp;
                }
                // move lower triangular part to arrayL
                for(int j=0;j<N;j++) {
                        arrayL[j][j]=1.0;
                        for(int i=j+1;i<N;i++) {
                                arrayL[i][j]=arrayU[i][j];
                                arrayU[i][j]=0.0;
                        }
                }
                DoubleSquareMatrix L=new DoubleSquareMatrix(arrayL);
                DoubleSquareMatrix U=new DoubleSquareMatrix(arrayU);
                int[] LUpivot=new int[pivot.length];
                System.arraycopy(pivot,0,LUpivot,0,pivot.length);
                luCache = new DoubleLUCache(L, U, LUpivot);
                return new DoubleSquareMatrix[] {L,U};
        }
        /**
        * Returns the LU decomposition of this matrix.
        * Warning: no pivoting.
        * @return an array with [0] containing the L-matrix
        * and [1] containing the U-matrix.
        * @jsci.planetmath LUDecomposition
        */
        public final AbstractDoubleSquareMatrix[] luDecompose() {
                final int N=numRows;
                final double arrayL[][]=new double[N][N];
                final double arrayU[][]=new double[N][N];
                double tmp;
        // LU decomposition to arrayU
                for(int j=0;j<N;j++) {
                        for(int i=0;i<j;i++) {
                                tmp=matrix[i][j];
                                for(int k=0;k<i;k++)
                                        tmp-=arrayU[i][k]*arrayU[k][j];
                                arrayU[i][j]=tmp;
                        }
                        for(int i=j;i<N;i++) {
                                tmp=matrix[i][j];
                                for(int k=0;k<j;k++)
                                        tmp-=arrayU[i][k]*arrayU[k][j];
                                arrayU[i][j]=tmp;
                        }
                // divide
                        tmp=arrayU[j][j];
                        for(int i=j+1;i<N;i++)
                                arrayU[i][j]/=tmp;
                }
                // move lower triangular part to arrayL
                for(int j=0;j<N;j++) {
                        arrayL[j][j]=1.0;
                        for(int i=j+1;i<N;i++) {
                                arrayL[i][j]=arrayU[i][j];
                                arrayU[i][j]=0.0;
                        }
                }
                DoubleSquareMatrix[] lu=new DoubleSquareMatrix[2];
                lu[0]=new DoubleSquareMatrix(arrayL);
                lu[1]=new DoubleSquareMatrix(arrayU);
                return lu;
        }

// CHOLESKY DECOMPOSITION

        /**
        * Returns the Cholesky decomposition of this matrix.
        * Matrix must be symmetric and positive definite.
        * @return an array with [0] containing the L-matrix and [1] containing the U-matrix.
        * @jsci.planetmath CholeskyDecomposition
        */
        public AbstractDoubleSquareMatrix[] choleskyDecompose() {
                final int N=numRows;
                final double arrayL[][]=new double[N][N];
                final double arrayU[][]=new double[N][N];
                double tmp=Math.sqrt(matrix[0][0]);
                arrayL[0][0]=arrayU[0][0]=tmp;
                for(int i=1;i<N;i++)
                        arrayL[i][0]=arrayU[0][i]=matrix[i][0]/tmp;
                for(int j=1;j<N;j++) {
                        tmp=matrix[j][j];
                        for(int i=0;i<j;i++)
                                tmp-=arrayL[j][i]*arrayL[j][i];
                        arrayL[j][j]=arrayU[j][j]=Math.sqrt(tmp);
                        for(int i=j+1;i<N;i++) {
                                tmp=matrix[i][j];
                                for(int k=0;k<i;k++)
                                        tmp-=arrayL[j][k]*arrayU[k][i];
                                arrayL[i][j]=arrayU[j][i]=tmp/arrayU[j][j];
                        }
                }
                final AbstractDoubleSquareMatrix lu[]=new AbstractDoubleSquareMatrix[2];
                lu[0]=new DoubleSquareMatrix(arrayL);
                lu[1]=new DoubleSquareMatrix(arrayU);
                return lu;
        }

// QR DECOMPOSITION

        /**
        * Returns the QR decomposition of this matrix.
        * Based on the code from <a href="http://math.nist.gov/javanumerics/jama/">JAMA</a> (public domain).
        * @return an array with [0] containing the Q-matrix and [1] containing the R-matrix.
        * @jsci.planetmath QRDecomposition
        */
        public AbstractDoubleSquareMatrix[] qrDecompose() {
                final int N=numRows;
                final double array[][]=new double[N][N];
                final double arrayQ[][]=new double[N][N];
                final double arrayR[][]=new double[N][N];
                // copy matrix
                for(int i=0;i<N;i++) {
                        array[i][0]=matrix[i][0];
                        for(int j=1;j<N;j++)
                                array[i][j]=matrix[i][j];
                }
                for(int k=0; k<N; k++) {
                        // compute l2-norm of kth column
                        double norm = array[k][k];
                        for(int i=k+1; i<N; i++)
                                norm = ExtraMath.hypot(norm, array[i][k]);
                        if(norm != 0.0) {
                                // form kth Householder vector
                                if(array[k][k] < 0.0)
                                        norm = -norm;
                                for(int i=k; i<N; i++)
                                        array[i][k] /= norm;
                                array[k][k] += 1.0;
                                // apply transformation to remaining columns
                                for(int j=k+1; j<N; j++) {
                                        double s=array[k][k]*array[k][j];
                                        for(int i=k+1; i<N; i++)
                                                s += array[i][k]*array[i][j];
                                        s /= array[k][k];
                                        for(int i=k; i<N; i++)
                                                array[i][j] -= s*array[i][k];
                                }
                        }
                        arrayR[k][k] = -norm;
                }
                for(int k=N-1; k>=0; k--) {
                        arrayQ[k][k] = 1.0;
                        for(int j=k; j<N; j++) {
                                if(array[k][k] != 0.0) {
                                        double s = array[k][k]*arrayQ[k][j];
                                        for(int i=k+1; i<N; i++)
                                                s += array[i][k]*arrayQ[i][j];
                                        s /= array[k][k];
                                        for(int i=k; i<N; i++)
                                                arrayQ[i][j] -= s*array[i][k];
                                }
                        }
                }
                for(int i=0; i<N; i++) {
                        for(int j=i+1; j<N; j++)
                                arrayR[i][j] = array[i][j];
                }
                final AbstractDoubleSquareMatrix qr[]=new AbstractDoubleSquareMatrix[2];
                qr[0]=new DoubleSquareMatrix(arrayQ);
                qr[1]=new DoubleSquareMatrix(arrayR);
                return qr;
        }

// SINGULAR VALUE DECOMPOSITION

        /**
        * Returns the singular value decomposition of this matrix.
        * Based on the code from <a href="http://math.nist.gov/javanumerics/jama/">JAMA</a> (public domain).
        * @return an array with [0] containing the U-matrix, [1] containing the S-matrix and [2] containing the V-matrix.
        * @jsci.planetmath SingularValueDecomposition
        */
        public AbstractDoubleSquareMatrix[] singularValueDecompose() {
                final int N=numRows;
                final int Nm1=N-1;
                final double array[][]=new double[N][N];
                final double arrayU[][]=new double[N][N];
                final double arrayS[]=new double[N];
                final double arrayV[][]=new double[N][N];
                final double e[]=new double[N];
                final double work[]=new double[N];
                // copy matrix
                for(int i=0;i<N;i++) {
                        array[i][0]=matrix[i][0];
                        for(int j=1;j<N;j++)
                                array[i][j]=matrix[i][j];
                }
                // reduce matrix to bidiagonal form
                for(int k=0;k<Nm1;k++) {
                        // compute the transformation for the kth column
                        // compute l2-norm of kth column
                        arrayS[k]=array[k][k];
                        for(int i=k+1;i<N;i++)
                                arrayS[k]=ExtraMath.hypot(arrayS[k],array[i][k]);
                        if(arrayS[k]!=0.0) {
                                if(array[k][k]<0.0)
                                        arrayS[k]=-arrayS[k];
                                for(int i=k;i<N;i++)
                                        array[i][k]/=arrayS[k];
                                array[k][k]+=1.0;
                        }
                        arrayS[k]=-arrayS[k];
                        for(int j=k+1;j<N;j++) {
                                if(arrayS[k]!=0.0) {
                                        // apply the transformation
                                        double t=array[k][k]*array[k][j];
                                        for(int i=k+1; i<N; i++)
                                                t+=array[i][k]*array[i][j];
                                        t /= array[k][k];
                                        for(int i=k; i<N; i++)
                                                array[i][j] -= t*array[i][k];
                                }
                                e[j]=array[k][j];
                        }
                        for(int i=k;i<N;i++)
                                arrayU[i][k]=array[i][k];
                        if(k<N-2) {
                                // compute the kth row transformation
                                // compute l2-norm of kth column
                                e[k]=e[k+1];
                                for(int i=k+2;i<N;i++)
                                        e[k]=ExtraMath.hypot(e[k],e[i]);
                                if(e[k]!=0.0) {
                                        if(e[k+1]<0.0)
                                                e[k]=-e[k];
                                        for(int i=k+1;i<N;i++)
                                                e[i]/=e[k];
                                        e[k+1]+=1.0;
                                }
                                e[k]=-e[k];
                                if(e[k]!=0.0) {
                                        // apply the transformation
                                        for(int i=k+1;i<N;i++) {
                                                work[i]=0.0;
                                                for(int j=k+1;j<N;j++)
                                                        work[i]+=e[j]*array[i][j];
                                        }
                                        for(int j=k+1;j<N;j++) {
                                                double t = e[j]/e[k+1];
                                                for(int i=k+1;i<N;i++)
                                                        array[i][j] -= t*work[i];
                                        }
                                }
                                for(int i=k+1;i<N;i++)
                                        arrayV[i][k]=e[i];
                        }
                }
                // setup the final bidiagonal matrix of order p
                int p=N;
                arrayS[Nm1]=array[Nm1][Nm1];
                e[N-2]=array[N-2][Nm1];
                e[Nm1]=0.0;
                arrayU[Nm1][Nm1]=1.0;
                for(int k=N-2;k>=0;k--) {
                        if(arrayS[k]!=0.0) {
                                for(int j=k+1;j<N;j++) {
                                        double t=arrayU[k][k]*arrayU[k][j];
                                        for(int i=k+1;i<N;i++)
                                                t+=arrayU[i][k]*arrayU[i][j];
                                        t /= arrayU[k][k];
                                        for(int i=k;i<N;i++)
                                                arrayU[i][j] -= t*arrayU[i][k];
                                }
                                for(int i=k;i<N;i++)
                                        arrayU[i][k]=-arrayU[i][k];
                                arrayU[k][k]+=1.0;
                                for(int i=0;i<k-1;i++)
                                        arrayU[i][k]=0.0;
                        } else {
                                for(int i=0;i<N;i++)
                                        arrayU[i][k]=0.0;
                                arrayU[k][k]=1.0;
                        }
                }
                for(int k=Nm1;k>=0;k--) {
                        if(k<N-2 && e[k]!=0.0) {
                                for(int j=k+1;j<N;j++) {
                                        double t=arrayV[k+1][k]*arrayV[k+1][j];
                                        for(int i=k+2;i<N;i++)
                                                t+=arrayV[i][k]*arrayV[i][j];
                                        t /= arrayV[k+1][k];
                                        for(int i=k+1;i<N;i++)
                                                arrayV[i][j] -= t*arrayV[i][k];
                                }
                        }
                        for(int i=0;i<N;i++)
                                arrayV[i][k]=0.0;
                        arrayV[k][k]=1.0;
                }
                final double eps=Math.pow(2.0,-52.0);
                int iter=0;
                while(p>0) {
                        int k, action;
                        // action = 1 if arrayS[p] and e[k-1] are negligible and k<p
                        // action = 2 if arrayS[k] is negligible and k<p
                        // action = 3 if e[k-1] is negligible, k<p, and arrayS[k], ..., arrayS[p] are not negligible (QR step)
                        // action = 4 if e[p-1] is negligible (convergence)
                        for(k=p-2;k>=-1;k--) {
                                if(k==-1)
                                        break;
                                if(Math.abs(e[k])<=eps*(Math.abs(arrayS[k])+Math.abs(arrayS[k+1]))) {
                                        e[k]=0.0;
                                        break;
                                }
                        }
                        if(k==p-2) {
                                action=4;
                        } else {
                                int ks;
                                for(ks=p-1;ks>=k;ks--) {
                                        if(ks==k)
                                                break;
                                        double t=(ks!=p ? Math.abs(e[ks]) : 0.0)+(ks!=k+1 ? Math.abs(e[ks-1]) : 0.0);
                                        if(Math.abs(arrayS[ks])<=eps*t) {
                                                arrayS[ks]=0.0;
                                                break;
                                        }
                                }
                                if(ks==k) {
                                        action=3;
                                } else if(ks==p-1) {
                                        action=1;
                                } else {
                                        action=2;
                                        k=ks;
                                }
                        }
                        k++;
                        switch(action) {
                                // deflate negligible arrayS[p]
                                case 1: {
                                        double f=e[p-2];
                                        e[p-2]=0.0;
                                        for(int j=p-2;j>=k;j--) {
                                                double t=ExtraMath.hypot(arrayS[j],f);
                                                final double cs=arrayS[j]/t;
                                                final double sn=f/t;
                                                arrayS[j]=t;
                                                if(j!=k) {
                                                        f=-sn*e[j-1];
                                                        e[j-1]*=cs;
                                                }
                                                for(int i=0;i<N;i++) {
                                                        t=cs*arrayV[i][j]+sn*arrayV[i][p-1];
                                                        arrayV[i][p-1]=-sn*arrayV[i][j]+cs*arrayV[i][p-1];
                                                        arrayV[i][j]=t;
                                                }
                                        }
                                        } break;
                                // split at negligible arrayS[k]
                                case 2: {
                                        double f=e[k-1];
                                        e[k-1]=0.0;
                                        for(int j=k;j<p;j++) {
                                                double t=ExtraMath.hypot(arrayS[j],f);
                                                final double cs=arrayS[j]/t;
                                                final double sn=f/t;
                                                arrayS[j]=t;
                                                f=-sn*e[j];
                                                e[j]*=cs;
                                                for(int i=0;i<N;i++) {
                                                        t=cs*arrayU[i][j]+sn*arrayU[i][k-1];
                                                        arrayU[i][k-1]=-sn*arrayU[i][j]+cs*arrayU[i][k-1];
                                                        arrayU[i][j]=t;
                                                }
                                        }
                                        } break;
                                // perform one QR step
                                case 3: {
                                        // calculate the shift
                                        final double scale=Math.max(Math.max(Math.max(Math.max(
                                                Math.abs(arrayS[p-1]),Math.abs(arrayS[p-2])),Math.abs(e[p-2])),
                                                Math.abs(arrayS[k])),Math.abs(e[k]));
                                        double sp=arrayS[p-1]/scale;
                                        double spm1=arrayS[p-2]/scale;
                                        double epm1=e[p-2]/scale;
                                        double sk=arrayS[k]/scale;
                                        double ek=e[k]/scale;
                                        double b=((spm1+sp)*(spm1-sp)+epm1*epm1)/2.0;
                                        double c=(sp*epm1)*(sp*epm1);
                                        double shift=0.0;
                                        if(b!=0.0 || c!=0.0) {
                                                shift=Math.sqrt(b*b+c);
                                                if(b<0.0)
                                                        shift=-shift;
                                                shift=c/(b+shift);
                                        }
                                        double f=(sk+sp)*(sk-sp)+shift;
                                        double g=sk*ek;
                                        // chase zeros
                                        for(int j=k;j<p-1;j++) {
                                                double t=ExtraMath.hypot(f,g);
                                                double cs=f/t;
                                                double sn=g/t;
                                                if(j!=k)
                                                        e[j-1]=t;
                                                f=cs*arrayS[j]+sn*e[j];
                                                e[j]=cs*e[j]-sn*arrayS[j];
                                                g=sn*arrayS[j+1];
                                                arrayS[j+1]*=cs;
                                                for(int i=0;i<N;i++) {
                                                        t=cs*arrayV[i][j]+sn*arrayV[i][j+1];
                                                        arrayV[i][j+1]=-sn*arrayV[i][j]+cs*arrayV[i][j+1];
                                                        arrayV[i][j]=t;
                                                }
                                                t=ExtraMath.hypot(f,g);
                                                cs=f/t;
                                                sn=g/t;
                                                arrayS[j]=t;
                                                f=cs*e[j]+sn*arrayS[j+1];
                                                arrayS[j+1]=-sn*e[j]+cs*arrayS[j+1];
                                                g=sn*e[j+1];
                                                e[j+1]*=cs;
                                                if(j<Nm1) {
                                                        for(int i=0;i<N;i++) {
                                                                t=cs*arrayU[i][j]+sn*arrayU[i][j+1];
                                                                arrayU[i][j+1]=-sn*arrayU[i][j]+cs*arrayU[i][j+1];
                                                                arrayU[i][j]=t;
                                                        }
                                                }
                                        }
                                        e[p-2]=f;
                                        iter++;
                                        } break;
                                // convergence
                                case 4: {
                                        // make the singular values positive
                                        if(arrayS[k]<=0.0) {
                                                arrayS[k]=-arrayS[k];
                                                for(int i=0;i<p;i++)
                                                        arrayV[i][k]=-arrayV[i][k];
                                        }
                                        // order the singular values
                                        while(k<p-1) {
                                                if(arrayS[k]>=arrayS[k+1])
                                                        break;
                                                double tmp=arrayS[k];
                                                arrayS[k]=arrayS[k+1];
                                                arrayS[k+1]=tmp;
                                                if(k<Nm1) {
                                                        for(int i=0;i<N;i++) {
                                                                tmp=arrayU[i][k+1];
                                                                arrayU[i][k+1]=arrayU[i][k];
                                                                arrayU[i][k]=tmp;
                                                                tmp=arrayV[i][k+1];
                                                                arrayV[i][k+1]=arrayV[i][k];
                                                                arrayV[i][k]=tmp;
                                                        }
                                                }
                                                k++;
                                        }
                                        iter=0;
                                        p--;
                                        } break;
                        }
                }
                final AbstractDoubleSquareMatrix svd[]=new AbstractDoubleSquareMatrix[3];
                svd[0]=new DoubleSquareMatrix(arrayU);
                svd[1]=new DoubleDiagonalMatrix(arrayS);
                svd[2]=new DoubleSquareMatrix(arrayV);
                return svd;
        }

// POLAR DECOMPOSITION

        /**
        * Returns the polar decomposition of this matrix.
        */
        public AbstractDoubleSquareMatrix[] polarDecompose() {
                final int N=numRows;
                final AbstractDoubleVector evec[]=new AbstractDoubleVector[N];
                double eval[];
                try {
                        eval=LinearMath.eigenSolveSymmetric(this,evec);
                } catch(MaximumIterationsExceededException e) {
                        return null;
                }
                final double tmpa[][]=new double[N][N];
                final double tmpm[][]=new double[N][N];
                double abs;
                for(int i=0;i<N;i++) {
                        abs=Math.abs(eval[i]);
                        tmpa[i][0]=eval[i]*evec[i].getComponent(0)/abs;
                        tmpm[i][0]=abs*evec[i].getComponent(0);
                        for(int j=1;j<N;j++) {
                                tmpa[i][j]=eval[i]*evec[i].getComponent(j)/abs;
                                tmpm[i][j]=abs*evec[i].getComponent(j);
                        }
                }
                final double arg[][]=new double[N][N];
                final double mod[][]=new double[N][N];
                for(int i=0;i<N;i++) {
                        for(int j=0;j<N;j++) {
                                arg[i][j]=evec[0].getComponent(i)*tmpa[0][j];
                                mod[i][j]=evec[0].getComponent(i)*tmpm[0][j];
                                for(int k=1;k<N;k++) {
                                        arg[i][j]+=evec[k].getComponent(i)*tmpa[k][j];
                                        mod[i][j]+=evec[k].getComponent(i)*tmpm[k][j];
                                }
                        }
                }
                final AbstractDoubleSquareMatrix us[]=new AbstractDoubleSquareMatrix[2];
                us[0]=new DoubleSquareMatrix(arg);
                us[1]=new DoubleSquareMatrix(mod);
                return us;
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
                return new DoubleSquareMatrix(array);
        }
}
