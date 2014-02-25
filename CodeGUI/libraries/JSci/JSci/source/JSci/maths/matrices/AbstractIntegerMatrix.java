/* AUTO-GENERATED */
package JSci.maths.matrices;

import JSci.GlobalSettings;
import JSci.maths.ExtraMath;
import JSci.maths.Mapping;
import JSci.maths.DimensionException;
import JSci.maths.vectors.AbstractIntegerVector;
import JSci.maths.vectors.IntegerVector;
import JSci.maths.groups.AbelianGroup;
import JSci.maths.algebras.*;
import JSci.maths.fields.*;

/**
* The AbstractIntegerMatrix class provides an object for encapsulating integer matrices.
* @version 2.2
* @author Mark Hale
*/
public abstract class AbstractIntegerMatrix extends Matrix {
        /**
        * Constructs a matrix.
        */
        protected AbstractIntegerMatrix(final int rows,final int cols) {
                super(rows, cols);
        }
        /**
        * Compares two ${nativeTyp} matrices for equality.
        * @param obj a int matrix
        */
        public final boolean equals(Object obj) {
                if(obj instanceof AbstractIntegerMatrix) {
                        return equals((AbstractIntegerMatrix)obj);
                } else {
                        return false;
                }
        }
        /**
        * Compares two ${nativeTyp} matrices for equality.
        * Two matrices are considered to be equal if the Frobenius norm of their difference is within the zero tolerance.
        * @param m a int matrix
        */
        public final boolean equals(AbstractIntegerMatrix m) {
		return equals(m, GlobalSettings.ZERO_TOL);
        }
	public boolean equals(AbstractIntegerMatrix m, double tol) {
                if(m != null && numRows == m.rows() && numCols == m.columns()) {
			int sumSqr = 0;
                        for(int i=0;i<numRows;i++) {
                                for(int j=0;j<numCols;j++) {
					int delta = getElement(i,j)-m.getElement(i,j);
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
                                buf.append(getElement(i,j));
                                buf.append(' ');
                        }
                        buf.append('\n');
                }
                return buf.toString();
        }
        /**
        * Returns a hashcode for this matrix.
        */
        public int hashCode() {
                return (int)Math.exp(infNorm());
        }
        /**
        * Converts this matrix to a double matrix.
        * @return a double matrix
        */
        public AbstractDoubleMatrix toDoubleMatrix() {
                final double ans[][]=new double[numRows][numCols];
                for(int i=0;i<numRows;i++) {
                        for(int j=0;j<numCols;j++)
                                ans[i][j]=getElement(i,j);
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
                                cm.setElement(i, j, getElement(i, j), 0.0);
                }
                return cm;
        }
        /**
        * Returns an element of the matrix.
        * @param i row index of the element
        * @param j column index of the element
        * @exception MatrixDimensionException If attempting to access an invalid element.
        */
        public abstract int getElement(int i, int j);
        /**
        * Sets the value of an element of the matrix.
        * Should only be used to initialise this matrix.
        * @param i row index of the element
        * @param j column index of the element
        * @param x a number
        * @exception MatrixDimensionException If attempting to access an invalid element.
        */
        public abstract void setElement(int i, int j, int x);
	public final Object getSet() {
		return IntegerMatrixAlgebra.get(numRows, numCols);
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
                                tmpResult+=Math.abs(getElement(i,j));
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
                                result=ExtraMath.hypot(result, getElement(i,j));
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
                        array[i][0] = -getElement(i,0);
                        for(int j=1;j<numCols;j++)
                                array[i][j] = -getElement(i,j);
                }
                return new IntegerMatrix(array);
        }

// ADDITION

        /**
        * Returns the addition of this matrix and another.
        */
        public final AbelianGroup.Member add(final AbelianGroup.Member m) {
                if(m instanceof AbstractIntegerMatrix)
                        return add((AbstractIntegerMatrix)m);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the addition of this matrix and another.
        * @param m a int matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public AbstractIntegerMatrix add(final AbstractIntegerMatrix m) {
                if(numRows==m.rows() && numCols==m.columns()) {
                        final int array[][]=new int[numRows][numCols];
                        for(int i=0;i<numRows;i++) {
                                array[i][0] = getElement(i,0)+m.getElement(i,0);
                                for(int j=1;j<numCols;j++)
                                        array[i][j] = getElement(i,j)+m.getElement(i,j);
                        }
                        return new IntegerMatrix(array);
                } else {
                        throw new MatrixDimensionException("Matrices are different sizes.");
                }
        }

// SUBTRACTION

        /**
        * Returns the subtraction of this matrix by another.
        */
        public final AbelianGroup.Member subtract(final AbelianGroup.Member m) {
                if(m instanceof AbstractIntegerMatrix)
                        return subtract((AbstractIntegerMatrix)m);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the subtraction of this matrix by another.
        * @param m a int matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public AbstractIntegerMatrix subtract(final AbstractIntegerMatrix m) {
                if(numRows==m.rows() && numCols==m.columns()) {
                        final int array[][]=new int[numRows][numCols];
                        for(int i=0;i<numRows;i++) {
                                array[i][0] = getElement(i,0)-m.getElement(i,0);
                                for(int j=1;j<numCols;j++)
                                        array[i][j] = getElement(i,j)-m.getElement(i,j);
                        }
                        return new IntegerMatrix(array);
                } else {
                        throw new MatrixDimensionException("Matrices are different sizes.");
                }
        }

// SCALAR MULTIPLICATION

        /**
        * Returns the multiplication of this matrix by a scalar.
        */
        public final Module.Member scalarMultiply(Ring.Member x) {
                if(x instanceof Number) {
                        return scalarMultiply(((Number)x).intValue());
                } else {
                        throw new IllegalArgumentException("Member class not recognised by this method.");
                }
        }
        /**
        * Returns the multiplication of this matrix by a scalar.
        * @param x a int.
        * @return a int matrix.
        */
        public AbstractIntegerMatrix scalarMultiply(final int x) {
                final int array[][]=new int[numRows][numCols];
                for(int i=0;i<numRows;i++) {
                        array[i][0] = x*getElement(i,0);
                        for(int j=1;j<numCols;j++)
                                array[i][j] = x*getElement(i,j);
                }
                return new IntegerMatrix(array);
        }

// SCALAR DIVISON

        /**
        * Returns the division of this matrix by a scalar.
        * Always throws an exception.
        */
        public final VectorSpace.Member scalarDivide(Field.Member x) {
                throw new UnsupportedOperationException("Not an algebra");
        }

// SCALAR PRODUCT

        /**
        * Returns the scalar product of this matrix and another.
        * @param m a int matrix.
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public int scalarProduct(final AbstractIntegerMatrix m) {
                if(numRows==m.rows() && numCols==m.columns()) {
                        int ans = 0;
                        for(int i=0; i<numRows; i++) {
                                ans += getElement(i,0)*m.getElement(i,0);
                                for(int j=1; j<numCols; j++)
                                        ans += getElement(i,j)*m.getElement(i,j);
                        }
                        return ans;
                } else {
                       throw new MatrixDimensionException("Matrices are different sizes.");
                }
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
                                array[i]=getElement(i,0)*v.getComponent(0);
                                for(int j=1;j<numCols;j++)
                                        array[i]+=getElement(i,j)*v.getComponent(j);
                        }
                        return new IntegerVector(array);
                } else {
                        throw new DimensionException("Matrix and vector are incompatible.");
                }
        }
        /**
        * Returns the multiplication of this matrix and another.
        */
        public final Ring.Member multiply(final Ring.Member m) {
                if(m instanceof AbstractIntegerMatrix)
                        return multiply((AbstractIntegerMatrix)m);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the multiplication of this matrix and another.
        * @param m a int matrix
        * @return a AbstractIntegerMatrix or a AbstractIntegerSquareMatrix as appropriate
        * @exception MatrixDimensionException If the matrices are incompatible.
        */
        public AbstractIntegerMatrix multiply(final AbstractIntegerMatrix m) {
                if(numCols==m.rows()) {
                        final int mColumns = m.columns();
                        final int array[][]=new int[numRows][mColumns];
                        for(int j=0; j<numRows; j++) {
                                for(int k=0; k<mColumns; k++) {
                                        array[j][k] = getElement(j,0)*m.getElement(0,k);
                                        for(int n=1; n<numCols; n++)
                                                array[j][k] += getElement(j,n)*m.getElement(n,k);
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

// DIRECT SUM

        /**
        * Returns the direct sum of this matrix and another.
        */
        public AbstractIntegerMatrix directSum(final AbstractIntegerMatrix m) {
                final int array[][]=new int[numRows+m.numRows][numCols+m.numCols];
                for(int i=0;i<numRows;i++) {
                        for(int j=0;j<numCols;j++)
                                array[i][j] = getElement(i,j);
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
                                                array[i*m.numRows+k][j*m.numCols+l] = getElement(i,j)*m.getElement(k,l);
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
                        array[0][i] = getElement(i,0);
                        for(int j=1;j<numCols;j++)
                                array[j][i] = getElement(i,j);
                }
                return new IntegerMatrix(array);
        }

}
