/* AUTO-GENERATED */
package JSci.maths.matrices;

import JSci.GlobalSettings;
import JSci.maths.ExtraMath;
import JSci.maths.Mapping;
import JSci.maths.DimensionException;
import JSci.maths.vectors.AbstractDoubleVector;
import JSci.maths.vectors.DoubleVector;
import JSci.maths.groups.AbelianGroup;
import JSci.maths.algebras.*;
import JSci.maths.fields.*;

/**
* The AbstractDoubleMatrix class provides an object for encapsulating double matrices.
* @version 2.2
* @author Mark Hale
*/
public abstract class AbstractDoubleMatrix extends Matrix {
        /**
        * Constructs a matrix.
        */
        protected AbstractDoubleMatrix(final int rows,final int cols) {
                super(rows, cols);
        }
        /**
        * Compares two ${nativeTyp} matrices for equality.
        * @param obj a double matrix
        */
        public final boolean equals(Object obj) {
                if(obj instanceof AbstractDoubleMatrix) {
                        return equals((AbstractDoubleMatrix)obj);
                } else {
                        return false;
                }
        }
        /**
        * Compares two ${nativeTyp} matrices for equality.
        * Two matrices are considered to be equal if the Frobenius norm of their difference is within the zero tolerance.
        * @param m a double matrix
        */
        public final boolean equals(AbstractDoubleMatrix m) {
		return equals(m, GlobalSettings.ZERO_TOL);
        }
	public boolean equals(AbstractDoubleMatrix m, double tol) {
                if(m != null && numRows == m.rows() && numCols == m.columns()) {
			double sumSqr = 0;
                        for(int i=0;i<numRows;i++) {
                                for(int j=0;j<numCols;j++) {
					double delta = getElement(i,j)-m.getElement(i,j);
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
        * Converts this matrix to an integer matrix.
        * @return an integer matrix
        */
        public AbstractIntegerMatrix toIntegerMatrix() {
                final int ans[][]=new int[numRows][numCols];
                for(int i=0;i<numRows;i++) {
                        for(int j=0;j<numCols;j++)
                                ans[i][j]=Math.round((float)getElement(i,j));
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
        public abstract double getElement(int i, int j);
        /**
        * Sets the value of an element of the matrix.
        * Should only be used to initialise this matrix.
        * @param i row index of the element
        * @param j column index of the element
        * @param x a number
        * @exception MatrixDimensionException If attempting to access an invalid element.
        */
        public abstract void setElement(int i, int j, double x);
	public final Object getSet() {
		return DoubleMatrixAlgebra.get(numRows, numCols);
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
                final double array[][]=new double[numRows][numCols];
                for(int i=0;i<numRows;i++) {
                        array[i][0] = -getElement(i,0);
                        for(int j=1;j<numCols;j++)
                                array[i][j] = -getElement(i,j);
                }
                return new DoubleMatrix(array);
        }

// ADDITION

        /**
        * Returns the addition of this matrix and another.
        */
        public final AbelianGroup.Member add(final AbelianGroup.Member m) {
                if(m instanceof AbstractDoubleMatrix)
                        return add((AbstractDoubleMatrix)m);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the addition of this matrix and another.
        * @param m a double matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public AbstractDoubleMatrix add(final AbstractDoubleMatrix m) {
                if(numRows==m.rows() && numCols==m.columns()) {
                        final double array[][]=new double[numRows][numCols];
                        for(int i=0;i<numRows;i++) {
                                array[i][0] = getElement(i,0)+m.getElement(i,0);
                                for(int j=1;j<numCols;j++)
                                        array[i][j] = getElement(i,j)+m.getElement(i,j);
                        }
                        return new DoubleMatrix(array);
                } else {
                        throw new MatrixDimensionException("Matrices are different sizes.");
                }
        }

// SUBTRACTION

        /**
        * Returns the subtraction of this matrix by another.
        */
        public final AbelianGroup.Member subtract(final AbelianGroup.Member m) {
                if(m instanceof AbstractDoubleMatrix)
                        return subtract((AbstractDoubleMatrix)m);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the subtraction of this matrix by another.
        * @param m a double matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public AbstractDoubleMatrix subtract(final AbstractDoubleMatrix m) {
                if(numRows==m.rows() && numCols==m.columns()) {
                        final double array[][]=new double[numRows][numCols];
                        for(int i=0;i<numRows;i++) {
                                array[i][0] = getElement(i,0)-m.getElement(i,0);
                                for(int j=1;j<numCols;j++)
                                        array[i][j] = getElement(i,j)-m.getElement(i,j);
                        }
                        return new DoubleMatrix(array);
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
                        return scalarMultiply(((Number)x).doubleValue());
                } else {
                        throw new IllegalArgumentException("Member class not recognised by this method.");
                }
        }
        /**
        * Returns the multiplication of this matrix by a scalar.
        * @param x a double.
        * @return a double matrix.
        */
        public AbstractDoubleMatrix scalarMultiply(final double x) {
                final double array[][]=new double[numRows][numCols];
                for(int i=0;i<numRows;i++) {
                        array[i][0] = x*getElement(i,0);
                        for(int j=1;j<numCols;j++)
                                array[i][j] = x*getElement(i,j);
                }
                return new DoubleMatrix(array);
        }

// SCALAR DIVISON

        /**
        * Returns the division of this matrix by a scalar.
        * Always throws an exception.
        */
        public final VectorSpace.Member scalarDivide(Field.Member x) {
                if(x instanceof Number) {
                        return scalarDivide(((Number)x).doubleValue());
                } else {
                        throw new IllegalArgumentException("Member class not recognised by this method.");
                }
        }
        /**
        * Returns the division of this matrix by a scalar.
        * @param x a double.
        * @return a double matrix.
        */
        public AbstractDoubleMatrix scalarDivide(final double x) {
                final double array[][]=new double[numRows][numCols];
                for(int i=0;i<numRows;i++) {
                        array[i][0] = getElement(i,0)/x;
                        for(int j=1;j<numCols;j++)
                                array[i][j] = getElement(i,j)/x;
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
                if(numRows==m.rows() && numCols==m.columns()) {
                        double ans = 0;
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
        * @param v a double vector.
        * @exception DimensionException If the matrix and vector are incompatible.
        */
        public AbstractDoubleVector multiply(final AbstractDoubleVector v) {
                if(numCols==v.dimension()) {
                        final double array[]=new double[numRows];
                        for(int i=0;i<numRows;i++) {
                                array[i]=getElement(i,0)*v.getComponent(0);
                                for(int j=1;j<numCols;j++)
                                        array[i]+=getElement(i,j)*v.getComponent(j);
                        }
                        return new DoubleVector(array);
                } else {
                        throw new DimensionException("Matrix and vector are incompatible.");
                }
        }
        /**
        * Returns the multiplication of this matrix and another.
        */
        public final Ring.Member multiply(final Ring.Member m) {
                if(m instanceof AbstractDoubleMatrix)
                        return multiply((AbstractDoubleMatrix)m);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the multiplication of this matrix and another.
        * @param m a double matrix
        * @return a AbstractDoubleMatrix or a AbstractDoubleSquareMatrix as appropriate
        * @exception MatrixDimensionException If the matrices are incompatible.
        */
        public AbstractDoubleMatrix multiply(final AbstractDoubleMatrix m) {
                if(numCols==m.rows()) {
                        final int mColumns = m.columns();
                        final double array[][]=new double[numRows][mColumns];
                        for(int j=0; j<numRows; j++) {
                                for(int k=0; k<mColumns; k++) {
                                        array[j][k] = getElement(j,0)*m.getElement(0,k);
                                        for(int n=1; n<numCols; n++)
                                                array[j][k] += getElement(j,n)*m.getElement(n,k);
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

// DIRECT SUM

        /**
        * Returns the direct sum of this matrix and another.
        */
        public AbstractDoubleMatrix directSum(final AbstractDoubleMatrix m) {
                final double array[][]=new double[numRows+m.numRows][numCols+m.numCols];
                for(int i=0;i<numRows;i++) {
                        for(int j=0;j<numCols;j++)
                                array[i][j] = getElement(i,j);
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
                                                array[i*m.numRows+k][j*m.numCols+l] = getElement(i,j)*m.getElement(k,l);
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
                        array[0][i] = getElement(i,0);
                        for(int j=1;j<numCols;j++)
                                array[j][i] = getElement(i,j);
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
                        array[i][0] = f.map(getElement(i,0));
                        for(int j=1;j<numCols;j++)
                                array[i][j] = f.map(getElement(i,j));
                }
                return new DoubleMatrix(array);
        }
}
