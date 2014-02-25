/* AUTO-GENERATED */
package JSci.maths.matrices;

import JSci.GlobalSettings;
import JSci.maths.MathDouble;
import JSci.maths.MathInteger;
import JSci.maths.Complex;
import JSci.maths.ComplexMapping;
import JSci.maths.DimensionException;
import JSci.maths.vectors.AbstractComplexVector;
import JSci.maths.vectors.ComplexVector;
import JSci.maths.groups.AbelianGroup;
import JSci.maths.algebras.*;
import JSci.maths.fields.*;

/**
* The AbstractComplexMatrix class provides an object for encapsulating matrices containing complex numbers.
* @version 2.2
* @author Mark Hale
*/
public abstract class AbstractComplexMatrix extends Matrix {
        /**
        * Constructs a matrix.
        */
        protected AbstractComplexMatrix(final int rows,final int cols) {
                super(rows,cols);
        }
        /**
        * Compares two complex matrices for equality.
        * @param obj a complex matrix
        */
        public final boolean equals(Object obj) {
                if(obj instanceof AbstractComplexMatrix) {
                        return equals((AbstractComplexMatrix)obj);
                } else {
                        return false;
                }
        }
        /**
        * Compares two complex matrices for equality.
        * Two matrices are considered to be equal if the Frobenius norm of their difference is within the zero tolerance.
        * @param m a complex matrix
        */
        public final boolean equals(AbstractComplexMatrix m) {
		return equals(m, GlobalSettings.ZERO_TOL);
        }
	public boolean equals(AbstractComplexMatrix m, double tol) {
                if(m != null && numRows == m.rows() && numCols == m.columns()) {
			double sumSqr = 0.0;
                        for(int i=0;i<numRows;i++) {
                                for(int j=0;j<numCols;j++) {
					double deltaRe = getRealElement(i,j)-m.getRealElement(i,j);
					double deltaIm = getImagElement(i,j)-m.getImagElement(i,j);
					sumSqr += deltaRe*deltaRe + deltaIm*deltaIm;
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
        * Returns the real part of this complex matrix.
        * @return a double matrix
        */
        public AbstractDoubleMatrix real() {
                final double ans[][]=new double[numRows][numCols];
                for(int i=0;i<numRows;i++) {
                        for(int j=0;j<numCols;j++)
                                ans[i][j]=getElement(i,j).real();
                }
                return new DoubleMatrix(ans);
        }
        /**
        * Returns the imaginary part of this complex matrix.
        * @return a double matrix
        */
        public AbstractDoubleMatrix imag() {
                final double ans[][]=new double[numRows][numCols];
                for(int i=0;i<numRows;i++) {
                        for(int j=0;j<numCols;j++)
                                ans[i][j]=getElement(i,j).imag();
                }
                return new DoubleMatrix(ans);
        }
        /**
        * Returns an element of the matrix.
        * @param i row index of the element
        * @param j column index of the element
        * @exception MatrixDimensionException If attempting to access an invalid element.
        */
        public abstract Complex getElement(final int i, final int j);
        /**
        * Returns the real part of an element of the matrix.
        * @param i row index of the element
        * @param j column index of the element
        * @exception MatrixDimensionException If attempting to access an invalid element.
        */
        public abstract double getRealElement(final int i, final int j);
        /**
        * Returns the imag part of an element of the matrix.
        * @param i row index of the element
        * @param j column index of the element
        * @exception MatrixDimensionException If attempting to access an invalid element.
        */
        public abstract double getImagElement(final int i, final int j);
        /**
        * Sets the value of an element of the matrix.
        * Should only be used to initialise this matrix.
        * @param i row index of the element
        * @param j column index of the element
        * @param z a complex number
        * @exception MatrixDimensionException If attempting to access an invalid element.
        */
        public abstract void setElement(final int i, final int j, final Complex z);
        /**
        * Sets the value of an element of the matrix.
        * Should only be used to initialise this matrix.
        * @param i row index of the element
        * @param j column index of the element
        * @param x the real part of a complex number
        * @param y the imaginary part of a complex number
        * @exception MatrixDimensionException If attempting to access an invalid element.
        */
        public abstract void setElement(final int i, final int j, final double x, final double y);
	public Object getSet() {
		throw new RuntimeException("Not implemented: file bug");
	}
        /**
        * Returns the l<sup><img border=0 alt="infinity" src="doc-files/infinity.gif"></sup>-norm.
        * @author Taber Smith
        */
        public double infNorm() {
                double result=0.0,tmpResult;
                for(int i=0;i<numRows;i++) {
                        tmpResult=0.0;
                        for(int j=0;j<numCols;j++)
                                tmpResult+=getElement(i,j).norm();
                        if(tmpResult>result)
                                result=tmpResult;
                }
                return result;
        }
        /**
        * Returns the Frobenius or Hilbert-Schmidt (l<sup>2</sup>) norm.
        * @jsci.planetmath FrobeniusMatrixNorm
        * @author Taber Smith
        */
        public double frobeniusNorm() {
                double result=0.0;
                for(int i=0;i<numRows;i++) {
                        for(int j=0;j<numCols;j++)
                                result += getRealElement(i,j)*getRealElement(i,j) + getImagElement(i,j)*getImagElement(i,j);
                }
                return Math.sqrt(result);
        }

//============
// OPERATIONS
//============

        /**
        * Returns the negative of this matrix.
        */
        public AbelianGroup.Member negate() {
                final double arrayRe[][]=new double[numRows][numCols];
                final double arrayIm[][]=new double[numRows][numCols];
                for(int i=0;i<numRows;i++) {
                        arrayRe[i][0]=-getRealElement(i,0);
                        arrayIm[i][0]=-getImagElement(i,0);
                        for(int j=1;j<numCols;j++) {
                                arrayRe[i][j]=-getRealElement(i,j);
                                arrayIm[i][j]=-getImagElement(i,j);
                        }
                }
                return new ComplexMatrix(arrayRe,arrayIm);
        }

// ADDITION

        /**
        * Returns the addition of this matrix and another.
        */
        public final AbelianGroup.Member add(final AbelianGroup.Member m) {
                if(m instanceof AbstractComplexMatrix)
                        return add((AbstractComplexMatrix)m);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the addition of this matrix and another.
        * @param m a complex matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public AbstractComplexMatrix add(final AbstractComplexMatrix m) {
                if(numRows==m.rows() && numCols==m.columns()) {
                        final double arrayRe[][]=new double[numRows][numCols];
                        final double arrayIm[][]=new double[numRows][numCols];
                        for(int i=0;i<numRows;i++) {
                                arrayRe[i][0] = getRealElement(i,0)+m.getRealElement(i,0);
                                arrayIm[i][0] = getImagElement(i,0)+m.getImagElement(i,0);
                                for(int j=1;j<numCols;j++) {
                                        arrayRe[i][j] = getRealElement(i,j)+m.getRealElement(i,j);
                                        arrayIm[i][j] = getImagElement(i,j)+m.getImagElement(i,j);
                                }
                        }
                        return new ComplexMatrix(arrayRe,arrayIm);
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }

// SUBTRACTION

        /**
        * Returns the subtraction of this matrix by another.
        */
        public final AbelianGroup.Member subtract(final AbelianGroup.Member m) {
                if(m instanceof AbstractComplexMatrix)
                        return subtract((AbstractComplexMatrix)m);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the subtraction of this matrix by another.
        * @param m a complex matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public AbstractComplexMatrix subtract(final AbstractComplexMatrix m) {
                if(numRows==m.rows() && numCols==m.columns()) {
                        final double arrayRe[][]=new double[numRows][numCols];
                        final double arrayIm[][]=new double[numRows][numCols];
                        for(int i=0;i<numRows;i++) {
                                arrayRe[i][0] = getRealElement(i,0)-m.getRealElement(i,0);
                                arrayIm[i][0] = getImagElement(i,0)-m.getImagElement(i,0);
                                for(int j=1;j<numCols;j++) {
                                        arrayRe[i][j] = getRealElement(i,j)-m.getRealElement(i,j);
                                        arrayIm[i][j] = getImagElement(i,j)-m.getImagElement(i,j);
                                }
                        }
                        return new ComplexMatrix(arrayRe,arrayIm);
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }

// SCALAR MULTIPLICATION

        /**
        * Returns the multiplication of this matrix by a scalar.
        */
        public final Module.Member scalarMultiply(Ring.Member x) {
                if(x instanceof Complex)
                        return scalarMultiply((Complex)x);
                else if(x instanceof MathDouble)
                        return scalarMultiply(((MathDouble)x).value());
                else if(x instanceof MathInteger)
                        return scalarMultiply(((MathInteger)x).value());
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the multiplication of this matrix by a scalar.
        * @param z a complex number
        * @return a complex matrix
        */
        public AbstractComplexMatrix scalarMultiply(final Complex z) {
                final double real=z.real();
                final double imag=z.imag();
                final double arrayRe[][]=new double[numRows][numCols];
                final double arrayIm[][]=new double[numRows][numCols];
                for(int i=0;i<numRows;i++) {
                        arrayRe[i][0] = real*getRealElement(i,0)-imag*getImagElement(i,0);
                        arrayIm[i][0] = imag*getRealElement(i,0)+real*getImagElement(i,0);
                        for(int j=1;j<numCols;j++) {
                                arrayRe[i][j] = real*getRealElement(i,j)-imag*getImagElement(i,j);
                                arrayIm[i][j] = imag*getRealElement(i,j)+real*getImagElement(i,j);
                        }
                }
                return new ComplexMatrix(arrayRe,arrayIm);
        }
        /**
        * Returns the multiplication of this matrix by a scalar.
        * @param x a double
        * @return a complex matrix
        */
        public AbstractComplexMatrix scalarMultiply(final double x) {
                final double arrayRe[][]=new double[numRows][numCols];
                final double arrayIm[][]=new double[numRows][numCols];
                for(int i=0;i<numRows;i++) {
                        arrayRe[i][0] = x*getRealElement(i,0);
                        arrayIm[i][0] = x*getImagElement(i,0);
                        for(int j=1;j<numCols;j++) {
                                arrayRe[i][j] = x*getRealElement(i,j);
                                arrayIm[i][j] = x*getImagElement(i,j);
                        }
                }
                return new ComplexMatrix(arrayRe,arrayIm);
        }

// SCALAR DIVISON

        /**
        * Returns the division of this matrix by a scalar.
        */
        public final VectorSpace.Member scalarDivide(Field.Member x) {
                if(x instanceof Complex)
                        return scalarDivide((Complex)x);
                if(x instanceof MathDouble)
                        return scalarDivide(((MathDouble)x).value());
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the division of this matrix by a scalar.
        * @param z a complex number
        * @return a complex matrix
        */
        public AbstractComplexMatrix scalarDivide(final Complex z) {
                final Complex array[][]=new Complex[numRows][numCols];
                for(int i=0;i<numRows;i++) {
                        array[i][0] = getElement(i,0).divide(z);
                        for(int j=1;j<numCols;j++)
                                array[i][j] = getElement(i,j).divide(z);
                }
                return new ComplexMatrix(array);
        }
        /**
        * Returns the division of this matrix by a scalar.
        * @param x a double
        * @return a complex matrix
        */
        public AbstractComplexMatrix scalarDivide(final double x) {
                final double arrayRe[][]=new double[numRows][numCols];
                final double arrayIm[][]=new double[numRows][numCols];
                for(int i=0;i<numRows;i++) {
                        arrayRe[i][0]=getRealElement(i,0)/x;
                        arrayIm[i][0]=getImagElement(i,0)/x;
                        for(int j=1;j<numCols;j++) {
                                arrayRe[i][j]=getRealElement(i,j)/x;
                                arrayIm[i][j]=getImagElement(i,j)/x;
                        }
                }
                return new ComplexMatrix(arrayRe,arrayIm);
        }

// SCALAR PRODUCT

        /**
        * Returns the scalar product of this matrix and another.
        * @param m a complex matrix.
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public Complex scalarProduct(final AbstractComplexMatrix m) {
                if(numRows==m.rows() && numCols==m.columns()) {
                        double real = 0.0, imag = 0.0;
                        for(int i=0; i<numRows; i++) {
                                real += getRealElement(i,0)*m.getRealElement(i,0) + getImagElement(i,0)*m.getImagElement(i,0);
                                imag += getImagElement(i,0)*m.getRealElement(i,0) - getRealElement(i,0)*m.getImagElement(i,0);
                                for(int j=1; j<numCols; j++) {
                                        real += getRealElement(i,j)*m.getRealElement(i,j) + getImagElement(i,j)*m.getImagElement(i,j);
                                        imag += getImagElement(i,j)*m.getRealElement(i,j) - getRealElement(i,j)*m.getImagElement(i,j);
                                }
                        }
                        return new Complex(real, imag);
                } else {
                       throw new MatrixDimensionException("Matrices are different sizes.");
                }
        }

// MATRIX MULTIPLICATION

        /**
        * Returns the multiplication of a vector by this matrix.
        * @param v a complex vector
        * @exception DimensionException If the matrix and vector are incompatible.
        */
        public AbstractComplexVector multiply(final AbstractComplexVector v) {
                if(numCols==v.dimension()) {
                        final double arrayRe[]=new double[numRows];
                        final double arrayIm[]=new double[numRows];
                        Complex tmp;
                        for(int i=0;i<numRows;i++) {
                                tmp = getElement(i,0).multiply(v.getComponent(0));
                                arrayRe[i]=tmp.real();
                                arrayIm[i]=tmp.imag();
                                for(int j=1;j<numCols;j++) {
                                        tmp = getElement(i,j).multiply(v.getComponent(j));
                                        arrayRe[i]+=tmp.real();
                                        arrayIm[i]+=tmp.imag();
                                }
                        }
                        return new ComplexVector(arrayRe,arrayIm);
                } else
                        throw new DimensionException("Matrix and vector are incompatible.");
        }
        /**
        * Returns the multiplication of this matrix and another.
        */
        public final Ring.Member multiply(final Ring.Member m) {
                if(m instanceof AbstractComplexMatrix)
                        return multiply((AbstractComplexMatrix)m);
                else
                        throw new IllegalArgumentException("Matrix class not recognised by this method.");
        }
        /**
        * Returns the multiplication of this matrix and another.
        * @param m a complex matrix
        * @return an AbstractComplexMatrix or an AbstractComplexSquareMatrix as appropriate
        * @exception MatrixDimensionException If the matrices are incompatible.
        */
        public AbstractComplexMatrix multiply(final AbstractComplexMatrix m) {
                if(numCols==m.rows()) {
                        final double arrayRe[][]=new double[numRows][m.columns()];
                        final double arrayIm[][]=new double[numRows][m.columns()];
                        Complex tmp;
                        for(int j=0;j<numRows;j++) {
                                for(int k=0;k<m.columns();k++) {
                                        tmp=getElement(j,0).multiply(m.getElement(0,k));
                                        arrayRe[j][k]=tmp.real();
                                        arrayIm[j][k]=tmp.imag();
                                        for(int n=1;n<numCols;n++) {
                                                tmp=getElement(j,n).multiply(m.getElement(n,k));
                                                arrayRe[j][k]+=tmp.real();
                                                arrayIm[j][k]+=tmp.imag();
                                        }
                                }
                        }
                        if(numRows==m.columns())
                                return new ComplexSquareMatrix(arrayRe,arrayIm);
                        else
                                return new ComplexMatrix(arrayRe,arrayIm);
                } else {
                        throw new MatrixDimensionException("Incompatible matrices.");
                }
        }

// DIRECT SUM

        /**
        * Returns the direct sum of this matrix and another.
        */
        public AbstractComplexMatrix directSum(final AbstractComplexMatrix m) {
                final double arrayRe[][]=new double[numRows+m.numRows][numCols+m.numCols];
                final double arrayIm[][]=new double[numRows+m.numRows][numCols+m.numCols];
                for(int j,i=0;i<numRows;i++) {
                        for(j=0;j<numCols;j++) {
                                arrayRe[i][j]=getRealElement(i,j);
                                arrayIm[i][j]=getImagElement(i,j);
                        }
                }
                for(int j,i=0;i<m.numRows;i++) {
                        for(j=0;j<m.numCols;j++) {
                                arrayRe[i+numRows][j+numCols]=m.getRealElement(i,j);
                                arrayIm[i+numRows][j+numCols]=m.getImagElement(i,j);
                        }
                }
                return new ComplexMatrix(arrayRe,arrayIm);
        }

// TENSOR PRODUCT

        /**
        * Returns the tensor product of this matrix and another.
        */
        public AbstractComplexMatrix tensor(final AbstractComplexMatrix m) {
                final double arrayRe[][]=new double[numRows*m.numRows][numCols*m.numCols];
                final double arrayIm[][]=new double[numRows*m.numRows][numCols*m.numCols];
                for(int i=0;i<numRows;i++) {
                        for(int j=0;j<numCols;j++) {
                                for(int k=0;k<m.numRows;j++) {
                                        for(int l=0;l<m.numCols;l++) {
                                                Complex tmp=getElement(i,j).multiply(m.getElement(k,l));
                                                arrayRe[i*m.numRows+k][j*m.numCols+l]=tmp.real();
                                                arrayIm[i*m.numRows+k][j*m.numCols+l]=tmp.imag();
                                        }
                                }
                        }
                }
                return new ComplexMatrix(arrayRe,arrayIm);
        }

// HERMITIAN ADJOINT

        /**
        * Returns the hermitian adjoint of this matrix.
        * @return a complex matrix
        */
        public AbstractComplexMatrix hermitianAdjoint() {
                final double arrayRe[][]=new double[numCols][numRows];
                final double arrayIm[][]=new double[numCols][numRows];
                for(int i=0;i<numRows;i++) {
                        arrayRe[0][i]=getRealElement(i,0);
                        arrayIm[0][i]=-getImagElement(i,0);
                        for(int j=1;j<numCols;j++) {
                                arrayRe[j][i]=getRealElement(i,j);
                                arrayIm[j][i]=-getImagElement(i,j);
                        }
                }
                return new ComplexMatrix(arrayRe,arrayIm);
        }

// CONJUGATE

        /**
        * Returns the complex conjugate of this matrix.
        * @return a complex matrix
        */
        public AbstractComplexMatrix conjugate() {
                final double arrayRe[][]=new double[numCols][numRows];
                final double arrayIm[][]=new double[numCols][numRows];
                for(int i=0;i<numRows;i++) {
                        arrayRe[i][0]=getRealElement(i,0);
                        arrayIm[i][0]=-getImagElement(i,0);
                        for(int j=1;j<numCols;j++) {
                                arrayRe[i][j]=getRealElement(i,j);
                                arrayIm[i][j]=-getImagElement(i,j);
                        }
                }
                return new ComplexMatrix(arrayRe,arrayIm);
        }

// TRANSPOSE

        /**
        * Returns the transpose of this matrix.
        * @return a complex matrix
        */
        public Matrix transpose() {
                final double arrayRe[][]=new double[numCols][numRows];
                final double arrayIm[][]=new double[numCols][numRows];
                for(int i=0;i<numRows;i++) {
                        arrayRe[0][i]=getRealElement(i,0);
                        arrayIm[0][i]=getImagElement(i,0);
                        for(int j=1;j<numCols;j++) {
                                arrayRe[j][i]=getRealElement(i,j);
                                arrayIm[j][i]=getImagElement(i,j);
                        }
                }
                return new ComplexMatrix(arrayRe,arrayIm);
        }

// MAP ELEMENTS

        /**
        * Applies a function on all the matrix elements.
        * @param f a user-defined function
        * @return a complex matrix
        */
        public AbstractComplexMatrix mapElements(final ComplexMapping f) {
                final Complex array[][]=new Complex[numRows][numCols];
                for(int i=0;i<numRows;i++) {
                        array[i][0]=f.map(getElement(i,0));
                        for(int j=1;j<numCols;j++)
                                array[i][j]=f.map(getElement(i,j));
                }
                return new ComplexMatrix(array);
        }
}

