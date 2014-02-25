/* AUTO-GENERATED */
package JSci.maths.matrices;

import JSci.maths.ArrayMath;
import JSci.maths.Complex;
import JSci.maths.ComplexMapping;
import JSci.maths.LinearMath;
import JSci.maths.DimensionException;
import JSci.maths.MaximumIterationsExceededException;
import JSci.maths.vectors.AbstractComplexVector;
import JSci.maths.vectors.ComplexVector;

/**
* The ComplexTridiagonalMatrix class provides an object for encapsulating tridiagonal matrices containing complex numbers.
* Uses compressed diagonal storage.
* @version 2.2
* @author Mark Hale
*/
public class ComplexTridiagonalMatrix extends AbstractComplexSquareMatrix implements TridiagonalMatrix {
        /**
        * Tridiagonal data.
        */
        protected final double ldiagRe[],ldiagIm[];
        protected final double diagRe[],diagIm[];
        protected final double udiagRe[],udiagIm[];
        /**
        * Constructs an empty matrix.
        * @param size the number of rows/columns
        */
        public ComplexTridiagonalMatrix(final int size) {
                super(size);
                ldiagRe=new double[size];
                ldiagIm=new double[size];
                diagRe=new double[size];
                diagIm=new double[size];
                udiagRe=new double[size];
                udiagIm=new double[size];
        }
        /**
        * Constructs a matrix from an array.
        * Any non-tridiagonal elements in the array are ignored.
        * @param array an assigned value
        * @exception MatrixDimensionException If the array is not square.
        */
        public ComplexTridiagonalMatrix(final Complex array[][]) {
                this(array.length);
                if(!ArrayMath.isSquare(array))
                        throw new MatrixDimensionException("Array is not square.");
                diagRe[0]=array[0][0].real();
                diagIm[0]=array[0][0].imag();
                udiagRe[0]=array[0][1].real();
                udiagIm[0]=array[0][1].imag();
                int i=1;
                for(;i<array.length-1;i++) {
                        ldiagRe[i]=array[i][i-1].real();
                        ldiagIm[i]=array[i][i-1].imag();
                        diagRe[i]=array[i][i].real();
                        diagIm[i]=array[i][i].imag();
                        udiagRe[i]=array[i][i+1].real();
                        udiagIm[i]=array[i][i+1].imag();
                }
                ldiagRe[i]=array[i][i-1].real();
                ldiagIm[i]=array[i][i-1].imag();
                diagRe[i]=array[i][i].real();
                diagIm[i]=array[i][i].imag();
        }
        /**
        * Compares two complex matrices for equality.
        * @param m a complex matrix
        */
        public boolean equals(AbstractComplexMatrix m, double tol) {
                if(m instanceof TridiagonalMatrix) {
                        if(numRows != m.rows() || numCols != m.columns())
                                return false;
			double sumSqr = 0;
			double ldeltaRe,ldeltaIm;
			double deltaRe = diagRe[0] - m.getRealElement(0,0);
			double deltaIm = diagIm[0] - m.getImagElement(0,0);
			double udeltaRe = udiagRe[0] - m.getRealElement(0,1);
			double udeltaIm = udiagIm[0] - m.getImagElement(0,1);
			sumSqr += deltaRe*deltaRe+deltaIm*deltaIm + udeltaRe*udeltaRe+udeltaIm*udeltaIm;
                        int i=1;
                        for(;i<numRows-1;i++) {
				ldeltaRe = ldiagRe[i] - m.getRealElement(i,i-1);
				ldeltaIm = ldiagIm[i] - m.getImagElement(i,i-1);
				deltaRe = diagRe[i] - m.getRealElement(i,i);
				deltaIm = diagIm[i] - m.getImagElement(i,i);
				udeltaRe = udiagRe[i] - m.getRealElement(i,i+1);
				udeltaIm = udiagIm[i] - m.getImagElement(i,i+1);
				sumSqr += ldeltaRe*ldeltaRe+ldeltaIm*ldeltaIm + deltaRe*deltaRe+deltaIm*deltaIm + udeltaRe*udeltaRe+udeltaIm*udeltaIm;
                        }
			ldeltaRe = ldiagRe[i] - m.getRealElement(i,i-1);
			ldeltaIm = ldiagIm[i] - m.getImagElement(i,i-1);
			deltaRe = diagRe[i] - m.getRealElement(i,i);
			deltaIm = diagIm[i] - m.getImagElement(i,i);
			sumSqr += ldeltaRe*ldeltaRe+ldeltaIm*ldeltaIm + deltaRe*deltaRe+deltaIm*deltaIm;
                        return (sumSqr <= tol*tol);
                } else {
                        return false;
                }
        }
        /**
        * Returns a string representing this matrix.
        */
        public String toString() {
                final StringBuffer buf=new StringBuffer(5*rows()*columns());
                for(int i=0;i<rows();i++) {
                        for(int j=0;j<columns();j++) {
                                buf.append(getElement(i,j).toString());
                                buf.append(' ');
                        }
                        buf.append('\n');
                }
                return buf.toString();
        }
        /**
        * Returns the real part of this complex matrix.
        * @return a double tridiagonal matrix
        */
        public AbstractDoubleMatrix real() {
                final DoubleTridiagonalMatrix m=new DoubleTridiagonalMatrix(numRows);
                m.diag[0]=diagRe[0];
                m.udiag[0]=udiagRe[0];
                int i=1;
                for(;i<numRows-1;i++) {
                        m.ldiag[i]=ldiagRe[i];
                        m.diag[i]=diagRe[i];
                        m.udiag[i]=udiagRe[i];
                }
                m.ldiag[i]=ldiagRe[i];
                m.diag[i]=diagRe[i];
                return m;
        }
        /**
        * Returns the imaginary part of this complex matrix.
        * @return a double tridiagonal matrix
        */
        public AbstractDoubleMatrix imag() {
                final DoubleTridiagonalMatrix m=new DoubleTridiagonalMatrix(numRows);
                m.diag[0]=diagIm[0];
                m.udiag[0]=udiagIm[0];
                int i=1;
                for(;i<numRows-1;i++) {
                        m.ldiag[i]=ldiagIm[i];
                        m.diag[i]=diagIm[i];
                        m.udiag[i]=udiagIm[i];
                }
                m.ldiag[i]=ldiagIm[i];
                m.diag[i]=diagIm[i];
                return m;
        }
        /**
        * Returns an element of the matrix.
        * @param i row index of the element
        * @param j column index of the element
        * @exception MatrixDimensionException If attempting to access an invalid element.
        */
        public Complex getElement(final int i, final int j) {
                if(i>=0 && i<numRows && j>=0 && j<numCols) {
                        if(j == i-1)
                                return new Complex(ldiagRe[i], ldiagIm[i]);
                        else if(j == i)
                                return new Complex(diagRe[i], diagIm[i]);
                        else if(j == i+1)
                                return new Complex(udiagRe[i], udiagIm[i]);
                        else
                                return Complex.ZERO;
                } else
                        throw new MatrixDimensionException(getInvalidElementMsg(i,j));
        }
        public double getRealElement(final int i, final int j) {
                if(i>=0 && i<numRows && j>=0 && j<numCols) {
                        if(j == i-1)
                                return ldiagRe[i];
                        else if(j == i)
                                return diagRe[i];
                        else if(j == i+1)
                                return udiagRe[i];
                        else
                                return 0.0;
                } else
                        throw new MatrixDimensionException(getInvalidElementMsg(i,j));
        }
        public double getImagElement(final int i, final int j) {
                if(i>=0 && i<numRows && j>=0 && j<numCols) {
                        if(j == i-1)
                                return ldiagIm[i];
                        else if(j == i)
                                return diagIm[i];
                        else if(j == i+1)
                                return udiagIm[i];
                        else
                                return 0.0;
                } else
                        throw new MatrixDimensionException(getInvalidElementMsg(i,j));
        }
        /**
        * Sets the value of an element of the matrix.
        * Should only be used to initialise this matrix.
        * @param i row index of the element
        * @param j column index of the element
        * @param z a complex number
        * @exception MatrixDimensionException If attempting to access an invalid element.
        */
        public void setElement(final int i, final int j, final Complex z) {
                if(i>=0 && i<numRows && j>=0 && j<numCols) {
                        if(j == i-1) {
                                ldiagRe[i] = z.real();
                                ldiagIm[i] = z.imag();
                        } else if(j == i) {
                                diagRe[i] = z.real();
                                diagIm[i] = z.imag();
                        } else if(j == i+1) {
                                udiagRe[i] = z.real();
                                udiagIm[i] = z.imag();
                        } else {
                                throw new MatrixDimensionException(getInvalidElementMsg(i,j));
                        }
                } else
                        throw new MatrixDimensionException(getInvalidElementMsg(i,j));
        }
        /**
        * Sets the value of an element of the matrix.
        * Should only be used to initialise this matrix.
        * @param i row index of the element
        * @param j column index of the element
        * @param x the real part of a complex number
        * @param y the imaginary part of a complex number
        * @exception MatrixDimensionException If attempting to access an invalid element.
        */
        public void setElement(final int i, final int j, final double x, final double y) {
                if(i>=0 && i<numRows && j>=0 && j<numCols) {
                        if(j == i-1) {
                                ldiagRe[i] = x;
                                ldiagIm[i] = y;
                        } else if(j == i) {
                                diagRe[i] = x;
                                diagIm[i] = y;
                        } else if(j == i+1) {
                                udiagRe[i] = x;
                                udiagIm[i] = y;
                        } else {
                                throw new MatrixDimensionException(getInvalidElementMsg(i,j));
                        }
                } else
                        throw new MatrixDimensionException(getInvalidElementMsg(i,j));
        }
        /**
        * Returns the trace.
        */
        public Complex trace() {
                double trRe=diagRe[0];
                double trIm=diagIm[0];
                for(int i=1;i<numRows;i++) {
                        trRe+=diagRe[i];
                        trIm+=diagIm[i];
                }
                return new Complex(trRe,trIm);
        }
        /**
        * Returns the l<sup><img border=0 alt="infinity" src="doc-files/infinity.gif"></sup>-norm.
        * @author Taber Smith
        */
        public double infNorm() {
                double result = Math.sqrt((diagRe[0]*diagRe[0] + diagIm[0]*diagIm[0])) + Math.sqrt((udiagRe[0]*udiagRe[0] + udiagIm[0]*udiagIm[0]));
                double tmpResult;
                int i=1;
                for(;i<numRows-1;i++) {
                        tmpResult = Math.sqrt((ldiagRe[i]*ldiagRe[i] + ldiagIm[i]*ldiagIm[i])) + Math.sqrt((diagRe[i]*diagRe[i] + diagIm[i]*diagIm[i])) + Math.sqrt((udiagRe[i]*udiagRe[i] + udiagIm[i]*udiagIm[i]));
                        if(tmpResult>result)
                                result=tmpResult;
                }
                tmpResult = Math.sqrt((ldiagRe[i]*ldiagRe[i] + ldiagIm[i]*ldiagIm[i])) + Math.sqrt((diagRe[i]*diagRe[i] + diagIm[i]*diagIm[i]));
                if(tmpResult>result)
                        result=tmpResult;
                return result;
        }
        /**
        * Returns the Frobenius (l<sup>2</sup>) norm.
        * @author Taber Smith
        */
        public double frobeniusNorm() {
                double result=diagRe[0]*diagRe[0]+diagIm[0]*diagIm[0]+
                        udiagRe[0]*udiagRe[0]+udiagIm[0]*udiagIm[0];
                int i=1;
                for(;i<numRows-1;i++) {
                        result+=ldiagRe[i]*ldiagRe[i]+ldiagIm[i]*ldiagIm[i]+
                                diagRe[i]*diagRe[i]+diagIm[i]*diagIm[i]+
                                udiagRe[i]*udiagRe[i]+udiagIm[i]*udiagIm[i];
                }
                result+=ldiagRe[i]*ldiagRe[i]+ldiagIm[i]*ldiagIm[i]+
                        diagRe[i]*diagRe[i]+diagIm[i]*diagIm[i];
                return Math.sqrt(result);
        }
        /**
        * Returns the operator norm.
        * @exception MaximumIterationsExceededException If it takes more than 50 iterations to determine an eigenvalue.
        */
        public double operatorNorm() throws MaximumIterationsExceededException {
                return Math.sqrt(ArrayMath.max(LinearMath.eigenvalueSolveHermitian((ComplexTridiagonalMatrix)(this.hermitianAdjoint().multiply(this)))));
        }

//============
// OPERATIONS
//============

// ADDITION

        /**
        * Returns the addition of this matrix and another.
        * @param m a complex matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public AbstractComplexSquareMatrix add(final AbstractComplexSquareMatrix m) {
                if(m instanceof ComplexTridiagonalMatrix)
                        return add((ComplexTridiagonalMatrix)m);
                if(m instanceof TridiagonalMatrix)
                        return addTridiagonal(m);
                if(m instanceof ComplexSquareMatrix)
                        return add((ComplexSquareMatrix)m);

                if(numRows==m.rows() && numCols==m.columns()) {
                        final double arrayRe[][]=new double[numRows][numCols];
                        final double arrayIm[][]=new double[numRows][numCols];
                        for(int i=0;i<numRows;i++) {
                                Complex elem=getElement(i,0).add(m.getElement(i,0));
                                arrayRe[i][0]=elem.real();
                                arrayIm[i][0]=elem.imag();
                                for(int j=1;j<numCols;j++) {
                                        elem=getElement(i,j).add(m.getElement(i,j));
                                        arrayRe[i][j]=elem.real();
                                        arrayIm[i][j]=elem.imag();
                                }
                        }
                        return new ComplexSquareMatrix(arrayRe,arrayIm);
                } else {
                        throw new MatrixDimensionException("Matrices are different sizes.");
                }
        }
        public ComplexSquareMatrix add(final ComplexSquareMatrix m) {
                if(numRows==m.numRows && numCols==m.numCols) {
                        final double arrayRe[][]=new double[numRows][numRows];
                        final double arrayIm[][]=new double[numRows][numRows];
                        for(int i=0;i<numRows;i++) {
                                System.arraycopy(m.matrixRe[i],0,arrayRe[i],0,numRows);
                                System.arraycopy(m.matrixIm[i],0,arrayIm[i],0,numRows);
                        }
                        arrayRe[0][0]+=diagRe[0];
                        arrayIm[0][0]+=diagIm[0];
                        arrayRe[0][1]+=udiagRe[0];
                        arrayIm[0][1]+=udiagIm[0];
                        int n=numCols-1;
                        for(int i=1;i<n;i++) {
                                arrayRe[i][i-1]+=ldiagRe[i];
                                arrayIm[i][i-1]+=ldiagIm[i];
                                arrayRe[i][i]+=diagRe[i];
                                arrayIm[i][i]+=diagIm[i];
                                arrayRe[i][i+1]+=udiagRe[i];
                                arrayIm[i][i+1]+=udiagIm[i];
                        }
                        arrayRe[n][n-1]+=ldiagRe[n];
                        arrayIm[n][n-1]+=ldiagIm[n];
                        arrayRe[n][n]+=diagRe[n];
                        arrayIm[n][n]+=diagIm[n];
                        return new ComplexSquareMatrix(arrayRe,arrayIm);
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }
        public ComplexTridiagonalMatrix add(final ComplexTridiagonalMatrix m) {
                int mRow=numRows;
                if(mRow==m.numRows) {
                        final ComplexTridiagonalMatrix ans=new ComplexTridiagonalMatrix(mRow);
                        ans.diagRe[0]=diagRe[0]+m.diagRe[0];
                        ans.diagIm[0]=diagIm[0]+m.diagIm[0];
                        ans.udiagRe[0]=udiagRe[0]+m.udiagRe[0];
                        ans.udiagIm[0]=udiagIm[0]+m.udiagIm[0];
                        mRow--;
                        for(int i=1;i<mRow;i++) {
                                ans.ldiagRe[i]=ldiagRe[i]+m.ldiagRe[i];
                                ans.ldiagIm[i]=ldiagIm[i]+m.ldiagIm[i];
                                ans.diagRe[i]=diagRe[i]+m.diagRe[i];
                                ans.diagIm[i]=diagIm[i]+m.diagIm[i];
                                ans.udiagRe[i]=udiagRe[i]+m.udiagRe[i];
                                ans.udiagIm[i]=udiagIm[i]+m.udiagIm[i];
                        }
                        ans.ldiagRe[mRow]=ldiagRe[mRow]+m.ldiagRe[mRow];
                        ans.ldiagIm[mRow]=ldiagIm[mRow]+m.ldiagIm[mRow];
                        ans.diagRe[mRow]=diagRe[mRow]+m.diagRe[mRow];
                        ans.diagIm[mRow]=diagIm[mRow]+m.diagIm[mRow];
                        return ans;
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }
        private ComplexTridiagonalMatrix addTridiagonal(final AbstractComplexSquareMatrix m) {
                int mRow=numRows;
                if(mRow==m.rows()) {
                        final ComplexTridiagonalMatrix ans=new ComplexTridiagonalMatrix(mRow);
                        Complex elem=m.getElement(0,0);
                        ans.diagRe[0]=diagRe[0]+elem.real();
                        ans.diagIm[0]=diagIm[0]+elem.imag();
                        elem=m.getElement(0,1);
                        ans.udiagRe[0]=udiagRe[0]+elem.real();
                        ans.udiagIm[0]=udiagIm[0]+elem.imag();
                        mRow--;
                        for(int i=1;i<mRow;i++) {
                                        elem=m.getElement(i,i-1);
                                        ans.ldiagRe[i]=ldiagRe[i]+elem.real();
                                        ans.ldiagIm[i]=ldiagIm[i]+elem.imag();
                                        elem=m.getElement(i,i);
                                        ans.diagRe[i]=diagRe[i]+elem.real();
                                        ans.diagIm[i]=diagIm[i]+elem.imag();
                                        elem=m.getElement(i,i+1);
                                        ans.udiagRe[i]=udiagRe[i]+elem.real();
                                        ans.udiagIm[i]=udiagIm[i]+elem.imag();
                        }
                        elem=m.getElement(mRow,mRow-1);
                        ans.ldiagRe[mRow]=ldiagRe[mRow]+elem.real();
                        ans.ldiagIm[mRow]=ldiagIm[mRow]+elem.imag();
                        elem=m.getElement(mRow,mRow);
                        ans.diagRe[mRow]=diagRe[mRow]+elem.real();
                        ans.diagIm[mRow]=diagIm[mRow]+elem.imag();
                        return ans;
                } else {
                        throw new MatrixDimensionException("Matrices are different sizes.");
                }
        }

// SUBTRACTION

        /**
        * Returns the subtraction of this matrix and another.
        * @param m a complex matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public AbstractComplexSquareMatrix subtract(final AbstractComplexSquareMatrix m) {
                if(m instanceof ComplexTridiagonalMatrix)
                        return subtract((ComplexTridiagonalMatrix)m);
                if(m instanceof TridiagonalMatrix)
                        return subtractTridiagonal(m);
                if(m instanceof ComplexSquareMatrix)
                        return subtract((ComplexSquareMatrix)m);

                if(numRows==m.rows() && numCols==m.columns()) {
                        final double arrayRe[][]=new double[numRows][numCols];
                        final double arrayIm[][]=new double[numRows][numCols];
                        Complex elem;
                        for(int i=0;i<numRows;i++) {
                                elem=getElement(i,0).subtract(m.getElement(i,0));
                                arrayRe[i][0]=elem.real();
                                arrayIm[i][0]=elem.imag();
                                for(int j=1;j<numCols;j++) {
                                        elem=getElement(i,j).subtract(m.getElement(i,j));
                                        arrayRe[i][j]=elem.real();
                                        arrayIm[i][j]=elem.imag();
                                }
                        }
                        return new ComplexSquareMatrix(arrayRe,arrayIm);
                } else {
                        throw new MatrixDimensionException("Matrices are different sizes.");
                }
        }
        public ComplexSquareMatrix subtract(final ComplexSquareMatrix m) {
                if(numRows==m.numRows && numCols==m.numCols) {
                        final double arrayRe[][]=new double[numRows][numCols];
                        final double arrayIm[][]=new double[numRows][numCols];
                        Complex elem;
                        for(int j,i=0;i<numRows;i++) {
                                elem=getElement(i,0);
                                arrayRe[i][0]=elem.real()-m.matrixRe[i][0];
                                arrayIm[i][0]=elem.imag()-m.matrixIm[i][0];
                                for(j=1;j<numCols;j++) {
                                        elem=getElement(i,j);
                                        arrayRe[i][j]=elem.real()-m.matrixRe[i][j];
                                        arrayIm[i][j]=elem.imag()-m.matrixIm[i][j];
                                }
                        }
                        return new ComplexSquareMatrix(arrayRe,arrayIm);
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }
        /**
        * Returns the subtraction of this matrix and another.
        * @param m a complex tridiagonal matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public ComplexTridiagonalMatrix subtract(final ComplexTridiagonalMatrix m) {
                int mRow=numRows;
                if(mRow==m.numRows) {
                        final ComplexTridiagonalMatrix ans=new ComplexTridiagonalMatrix(mRow);
                        ans.diagRe[0]=diagRe[0]-m.diagRe[0];
                        ans.diagIm[0]=diagIm[0]-m.diagIm[0];
                        ans.udiagRe[0]=udiagRe[0]-m.udiagRe[0];
                        ans.udiagIm[0]=udiagIm[0]-m.udiagIm[0];
                        mRow--;
                        for(int i=1;i<mRow;i++) {
                                ans.ldiagRe[i]=ldiagRe[i]-m.ldiagRe[i];
                                ans.ldiagIm[i]=ldiagIm[i]-m.ldiagIm[i];
                                ans.diagRe[i]=diagRe[i]-m.diagRe[i];
                                ans.diagIm[i]=diagIm[i]-m.diagIm[i];
                                ans.udiagRe[i]=udiagRe[i]-m.udiagRe[i];
                                ans.udiagIm[i]=udiagIm[i]-m.udiagIm[i];
                        }
                        ans.ldiagRe[mRow]=ldiagRe[mRow]-m.ldiagRe[mRow];
                        ans.ldiagIm[mRow]=ldiagIm[mRow]-m.ldiagIm[mRow];
                        ans.diagRe[mRow]=diagRe[mRow]-m.diagRe[mRow];
                        ans.diagIm[mRow]=diagIm[mRow]-m.diagIm[mRow];
                        return ans;
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }
        private ComplexTridiagonalMatrix subtractTridiagonal(final AbstractComplexSquareMatrix m) {
                int mRow=numRows;
                if(mRow==m.rows()) {
                        final ComplexTridiagonalMatrix ans=new ComplexTridiagonalMatrix(mRow);
                        Complex elem=m.getElement(0,0);
                        ans.diagRe[0]=diagRe[0]-elem.real();
                        ans.diagIm[0]=diagIm[0]-elem.imag();
                        elem=m.getElement(0,1);
                        ans.udiagRe[0]=udiagRe[0]-elem.real();
                        ans.udiagIm[0]=udiagIm[0]-elem.imag();
                        mRow--;
                        for(int i=1;i<mRow;i++) {
                                        elem=m.getElement(i,i-1);
                                        ans.ldiagRe[i]=ldiagRe[i]-elem.real();
                                        ans.ldiagIm[i]=ldiagIm[i]-elem.imag();
                                        elem=m.getElement(i,i);
                                        ans.diagRe[i]=diagRe[i]-elem.real();
                                        ans.diagIm[i]=diagIm[i]-elem.imag();
                                        elem=m.getElement(i,i+1);
                                        ans.udiagRe[i]=udiagRe[i]-elem.real();
                                        ans.udiagIm[i]=udiagIm[i]-elem.imag();
                        }
                        elem=m.getElement(mRow,mRow-1);
                        ans.ldiagRe[mRow]=ldiagRe[mRow]-elem.real();
                        ans.ldiagIm[mRow]=ldiagIm[mRow]-elem.imag();
                        elem=m.getElement(mRow,mRow);
                        ans.diagRe[mRow]=diagRe[mRow]-elem.real();
                        ans.diagIm[mRow]=diagIm[mRow]-elem.imag();
                        return ans;
                } else {
                        throw new MatrixDimensionException("Matrices are different sizes.");
                }
        }

// SCALAR MULTIPLICATION

        /**
        * Returns the multiplication of this matrix by a scalar.
        * @param z a complex number
        * @return a complex tridiagonal matrix
        */
        public AbstractComplexMatrix scalarMultiply(final Complex z) {
                final double real=z.real();
                final double imag=z.imag();
                int mRow=numRows;
                final ComplexTridiagonalMatrix ans=new ComplexTridiagonalMatrix(mRow);
                ans.diagRe[0]=real*diagRe[0]-imag*diagIm[0];
                ans.diagIm[0]=imag*diagRe[0]+real*diagIm[0];
                ans.udiagRe[0]=real*udiagRe[0]-imag*udiagIm[0];
                ans.udiagIm[0]=imag*udiagRe[0]+real*udiagIm[0];
                mRow--;
                for(int i=1;i<mRow;i++) {
                        ans.ldiagRe[i]=real*ldiagRe[i]-imag*ldiagIm[i];
                        ans.ldiagIm[i]=imag*ldiagRe[i]+real*ldiagIm[i];
                        ans.diagRe[i]=real*diagRe[i]-imag*diagIm[i];
                        ans.diagIm[i]=imag*diagRe[i]+real*diagIm[i];
                        ans.udiagRe[i]=real*udiagRe[i]-imag*udiagIm[i];
                        ans.udiagIm[i]=imag*udiagRe[i]+real*udiagIm[i];
                }
                ans.ldiagRe[mRow]=real*ldiagRe[mRow]-imag*ldiagIm[mRow];
                ans.ldiagIm[mRow]=imag*ldiagRe[mRow]+real*ldiagIm[mRow];
                ans.diagRe[mRow]=real*diagRe[mRow]-imag*diagIm[mRow];
                ans.diagIm[mRow]=imag*diagRe[mRow]+real*diagIm[mRow];
                return ans;
        }
        /**
        * Returns the multiplication of this matrix by a scalar.
        * @param x a double
        * @return a complex tridiagonal matrix
        */
        public AbstractComplexMatrix scalarMultiply(final double x) {
                int mRow=numRows;
                final ComplexTridiagonalMatrix ans=new ComplexTridiagonalMatrix(mRow);
                ans.diagRe[0]=x*diagRe[0];
                ans.diagIm[0]=x*diagIm[0];
                ans.udiagRe[0]=x*udiagRe[0];
                ans.udiagIm[0]=x*udiagIm[0];
                mRow--;
                for(int i=1;i<mRow;i++) {
                        ans.ldiagRe[i]=x*ldiagRe[i];
                        ans.ldiagIm[i]=x*ldiagIm[i];
                        ans.diagRe[i]=x*diagRe[i];
                        ans.diagIm[i]=x*diagIm[i];
                        ans.udiagRe[i]=x*udiagRe[i];
                        ans.udiagIm[i]=x*udiagIm[i];
                }
                ans.ldiagRe[mRow]=x*ldiagRe[mRow];
                ans.ldiagIm[mRow]=x*ldiagIm[mRow];
                ans.diagRe[mRow]=x*diagRe[mRow];
                ans.diagIm[mRow]=x*diagIm[mRow];
                return ans;
        }

// MATRIX MULTIPLICATION

        /**
        * Returns the multiplication of a vector by this matrix.
        * @param v a complex vector
        * @exception DimensionException If the matrix and vector are incompatible.
        */
        public AbstractComplexVector multiply(final AbstractComplexVector v) {
                int mRow=numRows;
                if(mRow==v.dimension()) {
                        final double arrayRe[]=new double[mRow];
                        final double arrayIm[]=new double[mRow];
                        Complex comp;
                        comp=v.getComponent(0);
                        arrayRe[0]=(diagRe[0]*comp.real() - diagIm[0]*comp.imag());
                        arrayIm[0]=(diagIm[0]*comp.real() + diagRe[0]*comp.imag());
                        comp=v.getComponent(1);
                        arrayRe[0]+=(udiagRe[0]*comp.real() - udiagIm[0]*comp.imag());
                        arrayIm[0]+=(udiagIm[0]*comp.real() + udiagRe[0]*comp.imag());
                        mRow--;
                        for(int i=1;i<mRow;i++) {
                                comp=v.getComponent(i-1);
                                arrayRe[i]=(ldiagRe[i]*comp.real() - ldiagIm[i]*comp.imag());
                                arrayIm[i]=(ldiagIm[i]*comp.real() + ldiagRe[i]*comp.imag());
                                comp=v.getComponent(i);
                                arrayRe[i]+=(diagRe[i]*comp.real() - diagIm[i]*comp.imag());
                                arrayIm[i]+=(diagIm[i]*comp.real() + diagRe[i]*comp.imag());
                                comp=v.getComponent(i+1);
                                arrayRe[i]+=(udiagRe[i]*comp.real() - udiagIm[i]*comp.imag());
                                arrayIm[i]+=(udiagIm[i]*comp.real() + udiagRe[i]*comp.imag());
                        }
                        comp=v.getComponent(mRow-1);
                        arrayRe[mRow]=(ldiagRe[mRow]*comp.real() - ldiagIm[mRow]*comp.imag());
                        arrayIm[mRow]=(ldiagIm[mRow]*comp.real() + ldiagRe[mRow]*comp.imag());
                        comp=v.getComponent(mRow);
                        arrayRe[mRow]+=(diagRe[mRow]*comp.real() - diagIm[mRow]*comp.imag());
                        arrayIm[mRow]+=(diagIm[mRow]*comp.real() + diagRe[mRow]*comp.imag());
                        return new ComplexVector(arrayRe,arrayIm);
                } else
                        throw new DimensionException("Matrix and vector are incompatible.");
        }
        /**
        * Returns the multiplication of this matrix and another.
        * @param m a complex matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public AbstractComplexSquareMatrix multiply(final AbstractComplexSquareMatrix m) {
                if(m instanceof ComplexTridiagonalMatrix)
                        return multiply((ComplexTridiagonalMatrix)m);
                if(m instanceof TridiagonalMatrix)
                        return multiplyTridiagonal(m);
                if(m instanceof ComplexSquareMatrix)
                        return multiply((ComplexSquareMatrix)m);

                if(numCols==m.rows()) {
                        final int mColumns = m.columns();
                        final double arrayRe[][]=new double[numRows][mColumns];
                        final double arrayIm[][]=new double[numRows][mColumns];
                        final int lastRow = numRows-1;
                        for(int j=0; j<numRows; j++) {
                                arrayRe[0][j]=(diagRe[0]*m.getRealElement(0,j) - diagIm[0]*m.getImagElement(0,j)) + (udiagRe[0]*m.getRealElement(1,j) - udiagIm[0]*m.getImagElement(1,j));
                                arrayIm[0][j]=(diagIm[0]*m.getRealElement(0,j) + diagRe[0]*m.getImagElement(0,j)) + (udiagIm[0]*m.getRealElement(1,j) + udiagRe[0]*m.getImagElement(1,j));
                                for(int i=1;i<lastRow;i++) {
                                    arrayRe[i][j]=(ldiagRe[i]*m.getRealElement(i-1,j) - ldiagIm[i]*m.getImagElement(i-1,j)) + (diagRe[i]*m.getRealElement(i,j) - diagIm[i]*m.getImagElement(i,j)) + (udiagRe[i]*m.getRealElement(i+1,j) - udiagIm[i]*m.getImagElement(i+1,j));
                                    arrayIm[i][j]=(ldiagIm[i]*m.getRealElement(i-1,j) + ldiagRe[i]*m.getImagElement(i-1,j)) + (diagIm[i]*m.getRealElement(i,j) + diagRe[i]*m.getImagElement(i,j)) + (udiagIm[i]*m.getRealElement(i+1,j) + udiagRe[i]*m.getImagElement(i+1,j));
                                }
                                arrayRe[lastRow][j]=(ldiagRe[lastRow]*m.getRealElement(lastRow-1,j) - ldiagIm[lastRow]*m.getImagElement(lastRow-1,j)) + (diagRe[lastRow]*m.getRealElement(lastRow,j) - diagIm[lastRow]*m.getImagElement(lastRow,j));
                                arrayIm[lastRow][j]=(ldiagIm[lastRow]*m.getRealElement(lastRow-1,j) + ldiagRe[lastRow]*m.getImagElement(lastRow-1,j)) + (diagIm[lastRow]*m.getRealElement(lastRow,j) + diagRe[lastRow]*m.getImagElement(lastRow,j));
                        }
                        return new ComplexSquareMatrix(arrayRe,arrayIm);
                } else {
                        throw new MatrixDimensionException("Incompatible matrices.");
                }
        }
        public ComplexSquareMatrix multiply(final ComplexSquareMatrix m) {
                if(numCols==m.numRows) {
                        final double arrayRe[][]=new double[numRows][m.numCols];
                        final double arrayIm[][]=new double[numRows][m.numCols];
                        final int lastRow = numRows-1;
                        for(int j=0; j<numRows; j++) {
                                arrayRe[0][j]=(diagRe[0]*m.matrixRe[0][j] - diagIm[0]*m.matrixIm[0][j]) + (udiagRe[0]*m.matrixRe[1][j] - udiagIm[0]*m.matrixIm[1][j]);
                                arrayIm[0][j]=(diagIm[0]*m.matrixRe[0][j] + diagRe[0]*m.matrixIm[0][j]) + (udiagIm[0]*m.matrixRe[1][j] + udiagRe[0]*m.matrixIm[1][j]);
                                for(int i=1;i<lastRow;i++) {
                                    arrayRe[i][j]=(ldiagRe[i]*m.matrixRe[i-1][j] - ldiagIm[i]*m.matrixIm[i-1][j]) + (diagRe[i]*m.matrixRe[i][j] - diagIm[i]*m.matrixIm[i][j]) + (udiagRe[i]*m.matrixRe[i+1][j] - udiagIm[i]*m.matrixIm[i+1][j]);
                                    arrayIm[i][j]=(ldiagIm[i]*m.matrixRe[i-1][j] + ldiagRe[i]*m.matrixIm[i-1][j]) + (diagIm[i]*m.matrixRe[i][j] + diagRe[i]*m.matrixIm[i][j]) + (udiagIm[i]*m.matrixRe[i+1][j] + udiagRe[i]*m.matrixIm[i+1][j]);
                                }
                                arrayRe[lastRow][j]=(ldiagRe[lastRow]*m.matrixRe[lastRow-1][j] - ldiagIm[lastRow]*m.matrixIm[lastRow-1][j]) + (diagRe[lastRow]*m.matrixRe[lastRow][j] - diagIm[lastRow]*m.matrixIm[lastRow][j]);
                                arrayIm[lastRow][j]=(ldiagIm[lastRow]*m.matrixRe[lastRow-1][j] + ldiagRe[lastRow]*m.matrixIm[lastRow-1][j]) + (diagIm[lastRow]*m.matrixRe[lastRow][j] + diagRe[lastRow]*m.matrixIm[lastRow][j]);
                        }
                        return new ComplexSquareMatrix(arrayRe,arrayIm);
                } else {
                        throw new MatrixDimensionException("Incompatible matrices.");
                }
        }
        /**
        * Returns the multiplication of this matrix and another.
        * @param m a complex tridiagonal matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public ComplexSquareMatrix multiply(final ComplexTridiagonalMatrix m) {
                int mRow=numRows;
                if(numCols==m.numRows) {
                        final double arrayRe[][]=new double[mRow][mRow];
                        final double arrayIm[][]=new double[mRow][mRow];
                        arrayRe[0][0]=(diagRe[0]*m.diagRe[0] - diagIm[0]*m.diagIm[0])+(udiagRe[0]*m.ldiagRe[1] - udiagIm[0]*m.ldiagIm[1]);
                        arrayIm[0][0]=(diagIm[0]*m.diagRe[0] + diagRe[0]*m.diagIm[0])+(udiagIm[0]*m.ldiagRe[1] + udiagRe[0]*m.ldiagIm[1]);
                        arrayRe[0][1]=(diagRe[0]*m.udiagRe[0] - diagIm[0]*m.udiagIm[0])+(udiagRe[0]*m.diagRe[1] - udiagIm[0]*m.diagIm[1]);
                        arrayIm[0][1]=(diagIm[0]*m.udiagRe[0] + diagRe[0]*m.udiagIm[0])+(udiagIm[0]*m.diagRe[1] + udiagRe[0]*m.diagIm[1]);
                        arrayRe[0][2]=(udiagRe[0]*m.udiagRe[1] - udiagIm[0]*m.udiagIm[1]);
                        arrayIm[0][2]=(udiagIm[0]*m.udiagRe[1] + udiagRe[0]*m.udiagIm[1]);
                        if(mRow>3) {
                                arrayRe[1][0]=(ldiagRe[1]*m.diagRe[0] - ldiagIm[1]*m.diagIm[0])+(diagRe[1]*m.ldiagRe[1] - diagIm[1]*m.ldiagIm[1]);
                                arrayIm[1][0]=(ldiagIm[1]*m.diagRe[0] + ldiagRe[1]*m.diagIm[0])+(diagIm[1]*m.ldiagRe[1] + diagRe[1]*m.ldiagIm[1]);
                                arrayRe[1][1]=(ldiagRe[1]*m.udiagRe[0] - ldiagIm[1]*m.udiagIm[0])+(diagRe[1]*m.diagRe[1] - diagIm[1]*m.diagIm[1])+(udiagRe[1]*m.ldiagRe[2] - udiagIm[1]*m.ldiagIm[2]);
                                arrayIm[1][1]=(ldiagIm[1]*m.udiagRe[0] + ldiagRe[1]*m.udiagIm[0])+(diagIm[1]*m.diagRe[1] + diagRe[1]*m.diagIm[1])+(udiagIm[1]*m.ldiagRe[2] + udiagRe[1]*m.ldiagIm[2]);
                                arrayRe[1][2]=(diagRe[1]*m.udiagRe[1] - diagIm[1]*m.udiagIm[1])+(udiagRe[1]*m.diagRe[2] - udiagIm[1]*m.diagIm[2]);
                                arrayIm[1][2]=(diagIm[1]*m.udiagRe[1] + diagRe[1]*m.udiagIm[1])+(udiagIm[1]*m.diagRe[2] + udiagRe[1]*m.diagIm[2]);
                                arrayRe[1][3]=(udiagRe[1]*m.udiagRe[2] - udiagIm[1]*m.udiagIm[2]);
                                arrayIm[1][3]=(udiagIm[1]*m.udiagRe[2] + udiagRe[1]*m.udiagIm[2]);
                        }
                        if(mRow==3) {
                                arrayRe[1][0]=(ldiagRe[1]*m.diagRe[0] - ldiagIm[1]*m.diagIm[0])+(diagRe[1]*m.ldiagRe[1] - diagIm[1]*m.ldiagIm[1]);
                                arrayIm[1][0]=(ldiagIm[1]*m.diagRe[0] + ldiagRe[1]*m.diagIm[0])+(diagIm[1]*m.ldiagRe[1] + diagRe[1]*m.ldiagIm[1]);
                                arrayRe[1][1]=(ldiagRe[1]*m.udiagRe[0] - ldiagIm[1]*m.udiagIm[0])+(diagRe[1]*m.diagRe[1] - diagIm[1]*m.diagIm[1])+(udiagRe[1]*m.ldiagRe[2] - udiagIm[1]*m.ldiagIm[2]);
                                arrayIm[1][1]=(ldiagIm[1]*m.udiagRe[0] + ldiagRe[1]*m.udiagIm[0])+(diagIm[1]*m.diagRe[1] + diagRe[1]*m.diagIm[1])+(udiagIm[1]*m.ldiagRe[2] + udiagRe[1]*m.ldiagIm[2]);
                                arrayRe[1][2]=(diagRe[1]*m.udiagRe[1] - diagIm[1]*m.udiagIm[1])+(udiagRe[1]*m.diagRe[2] - udiagIm[1]*m.diagIm[2]);
                                arrayIm[1][2]=(diagIm[1]*m.udiagRe[1] + diagRe[1]*m.udiagIm[1])+(udiagIm[1]*m.diagRe[2] + udiagRe[1]*m.diagIm[2]);
                        } else if(mRow>4) {
                                for(int i=2;i<mRow-2;i++) {
                                        arrayRe[i][i-2]=(ldiagRe[i]*m.ldiagRe[i-1] - ldiagIm[i]*m.ldiagIm[i-1]);
                                        arrayIm[i][i-2]=(ldiagIm[i]*m.ldiagRe[i-1] + ldiagRe[i]*m.ldiagIm[i-1]);
                                        arrayRe[i][i-1]=(ldiagRe[i]*m.diagRe[i-1] - ldiagIm[i]*m.diagIm[i-1])+(diagRe[i]*m.ldiagRe[i] - diagIm[i]*m.ldiagIm[i]);
                                        arrayIm[i][i-1]=(ldiagIm[i]*m.diagRe[i-1] + ldiagRe[i]*m.diagIm[i-1])+(diagIm[i]*m.ldiagRe[i] + diagRe[i]*m.ldiagIm[i]);
                                        arrayRe[i][i]=(ldiagRe[i]*m.udiagRe[i-1] - ldiagIm[i]*m.udiagIm[i-1])+(diagRe[i]*m.diagRe[i] - diagIm[i]*m.diagIm[i])+(udiagRe[i]*m.ldiagRe[i+1] - udiagIm[i]*m.ldiagIm[i+1]);
                                        arrayIm[i][i]=(ldiagIm[i]*m.udiagRe[i-1] + ldiagRe[i]*m.udiagIm[i-1])+(diagIm[i]*m.diagRe[i] + diagRe[i]*m.diagIm[i])+(udiagIm[i]*m.ldiagRe[i+1] + udiagRe[i]*m.ldiagIm[i+1]);
                                        arrayRe[i][i+1]=(diagRe[i]*m.udiagRe[i] - diagIm[i]*m.udiagIm[i])+(udiagRe[i]*m.diagRe[i+1] - udiagIm[i]*m.diagIm[i+1]);
                                        arrayIm[i][i+1]=(diagIm[i]*m.udiagRe[i] + diagRe[i]*m.udiagIm[i])+(udiagIm[i]*m.diagRe[i+1] + udiagRe[i]*m.diagIm[i+1]);
                                        arrayRe[i][i+2]=(udiagRe[i]*m.udiagRe[i+1] - udiagIm[i]*m.udiagIm[i+1]);
                                        arrayIm[i][i+2]=(udiagIm[i]*m.udiagRe[i+1] + udiagRe[i]*m.udiagIm[i+1]);
                                }
                        }
                        if(mRow>3) {
                                arrayRe[mRow-2][mRow-4]=(ldiagRe[mRow-2]*m.ldiagRe[mRow-3] - ldiagIm[mRow-2]*m.ldiagIm[mRow-3]);
                                arrayIm[mRow-2][mRow-4]=(ldiagIm[mRow-2]*m.ldiagRe[mRow-3] + ldiagRe[mRow-2]*m.ldiagIm[mRow-3]);
                                arrayRe[mRow-2][mRow-3]=(ldiagRe[mRow-2]*m.diagRe[mRow-3] - ldiagIm[mRow-2]*m.diagIm[mRow-3])+(diagRe[mRow-2]*m.ldiagRe[mRow-2] - diagIm[mRow-2]*m.ldiagIm[mRow-2]);
                                arrayIm[mRow-2][mRow-3]=(ldiagIm[mRow-2]*m.diagRe[mRow-3] + ldiagRe[mRow-2]*m.diagIm[mRow-3])+(diagIm[mRow-2]*m.ldiagRe[mRow-2] + diagRe[mRow-2]*m.ldiagIm[mRow-2]);
                                arrayRe[mRow-2][mRow-2]=(ldiagRe[mRow-2]*m.udiagRe[mRow-3] - ldiagIm[mRow-2]*m.udiagIm[mRow-3])+(diagRe[mRow-2]*m.diagRe[mRow-2] - diagIm[mRow-2]*m.diagIm[mRow-2])+(udiagRe[mRow-2]*m.ldiagRe[mRow-1] - udiagIm[mRow-2]*m.ldiagIm[mRow-1]);
                                arrayIm[mRow-2][mRow-2]=(ldiagIm[mRow-2]*m.udiagRe[mRow-3] + ldiagRe[mRow-2]*m.udiagIm[mRow-3])+(diagIm[mRow-2]*m.diagRe[mRow-2] + diagRe[mRow-2]*m.diagIm[mRow-2])+(udiagIm[mRow-2]*m.ldiagRe[mRow-1] + udiagRe[mRow-2]*m.ldiagIm[mRow-1]);
                                arrayRe[mRow-2][mRow-1]=(diagRe[mRow-2]*m.udiagRe[mRow-2] - diagIm[mRow-2]*m.udiagIm[mRow-2])+(udiagRe[mRow-2]*m.diagRe[mRow-1] - udiagIm[mRow-2]*m.diagIm[mRow-1]);
                                arrayIm[mRow-2][mRow-1]=(diagIm[mRow-2]*m.udiagRe[mRow-2] + diagRe[mRow-2]*m.udiagIm[mRow-2])+(udiagIm[mRow-2]*m.diagRe[mRow-1] + udiagRe[mRow-2]*m.diagIm[mRow-1]);
                        }
                        mRow--;
                        arrayRe[mRow][mRow-2]=(ldiagRe[mRow]*m.ldiagRe[mRow-1] - ldiagIm[mRow]*m.ldiagIm[mRow-1]);
                        arrayIm[mRow][mRow-2]=(ldiagIm[mRow]*m.ldiagRe[mRow-1] + ldiagRe[mRow]*m.ldiagIm[mRow-1]);
                        arrayRe[mRow][mRow-1]=(ldiagRe[mRow]*m.diagRe[mRow-1] - ldiagIm[mRow]*m.diagIm[mRow-1])+(diagRe[mRow]*m.ldiagRe[mRow] - diagIm[mRow]*m.ldiagIm[mRow]);
                        arrayIm[mRow][mRow-1]=(ldiagIm[mRow]*m.diagRe[mRow-1] + ldiagRe[mRow]*m.diagIm[mRow-1])+(diagIm[mRow]*m.ldiagRe[mRow] + diagRe[mRow]*m.ldiagIm[mRow]);
                        arrayRe[mRow][mRow]=(ldiagRe[mRow]*m.udiagRe[mRow-1] - ldiagIm[mRow]*m.udiagIm[mRow-1])+(diagRe[mRow]*m.diagRe[mRow] - diagIm[mRow]*m.diagIm[mRow]);
                        arrayIm[mRow][mRow]=(ldiagIm[mRow]*m.udiagRe[mRow-1] + ldiagRe[mRow]*m.udiagIm[mRow-1])+(diagIm[mRow]*m.diagRe[mRow] + diagRe[mRow]*m.diagIm[mRow]);
                        return new ComplexSquareMatrix(arrayRe,arrayIm);
                } else
                        throw new MatrixDimensionException("Incompatible matrices.");
        }
        private ComplexSquareMatrix multiplyTridiagonal(final AbstractComplexSquareMatrix m) {
                int mRow=numRows;
                if(numCols==m.rows()) {
                        final double arrayRe[][]=new double[mRow][mRow];
                        final double arrayIm[][]=new double[mRow][mRow];
                        Complex elem1,elem2,elem3;
                        elem1=m.getElement(0,0);elem2=m.getElement(1,0);
                        arrayRe[0][0]=(diagRe[0]*elem1.real() - diagIm[0]*elem1.imag())+(udiagRe[0]*elem2.real() - udiagIm[0]*elem2.imag());
                        arrayIm[0][0]=(diagIm[0]*elem1.real() + diagRe[0]*elem1.imag())+(udiagIm[0]*elem2.real() + udiagRe[0]*elem2.imag());
                        elem1=m.getElement(0,1);elem2=m.getElement(1,1);
                        arrayRe[0][1]=(diagRe[0]*elem1.real() - diagIm[0]*elem1.imag())+(udiagRe[0]*elem2.real() - udiagIm[0]*elem2.imag());
                        arrayIm[0][1]=(diagIm[0]*elem1.real() + diagRe[0]*elem1.imag())+(udiagIm[0]*elem2.real() + udiagRe[0]*elem2.imag());
                        elem1=m.getElement(1,2);
                        arrayRe[0][2]=(udiagRe[0]*elem1.real() - udiagIm[0]*elem1.imag());
                        arrayIm[0][2]=(udiagIm[0]*elem1.real() + udiagRe[0]*elem1.imag());
                        if(mRow>3) {
                                elem1=m.getElement(0,0);elem2=m.getElement(1,0);
                                arrayRe[1][0]=(ldiagRe[1]*elem1.real() - ldiagIm[1]*elem1.imag())+(diagRe[1]*elem2.real() - diagIm[1]*elem2.imag());
                                arrayIm[1][0]=(ldiagIm[1]*elem1.real() + ldiagRe[1]*elem1.imag())+(diagIm[1]*elem2.real() + diagRe[1]*elem2.imag());
                                elem1=m.getElement(0,1);elem2=m.getElement(1,1);elem3=m.getElement(2,1);
                                arrayRe[1][1]=(ldiagRe[1]*elem1.real() - ldiagIm[1]*elem1.imag())+(diagRe[1]*elem2.real() - diagIm[1]*elem2.imag())+(udiagRe[1]*elem3.real() - udiagIm[1]*elem3.imag());
                                arrayIm[1][1]=(ldiagIm[1]*elem1.real() + ldiagRe[1]*elem1.imag())+(diagIm[1]*elem2.real() + diagRe[1]*elem2.imag())+(udiagIm[1]*elem3.real() + udiagRe[1]*elem3.imag());
                                elem1=m.getElement(1,2);elem2=m.getElement(2,2);
                                arrayRe[1][2]=(diagRe[1]*elem1.real() - diagIm[1]*elem1.imag())+(udiagRe[1]*elem2.real() - udiagIm[1]*elem2.imag());
                                arrayIm[1][2]=(diagIm[1]*elem1.real() + diagRe[1]*elem1.imag())+(udiagIm[1]*elem2.real() + udiagRe[1]*elem2.imag());
                                elem1=m.getElement(2,3);
                                arrayRe[1][3]=(udiagRe[1]*elem1.real() - udiagIm[1]*elem1.imag());
                                arrayIm[1][3]=(udiagIm[1]*elem1.real() + udiagRe[1]*elem1.imag());
                        }
                        if(mRow==3) {
                                elem1=m.getElement(0,0);elem2=m.getElement(1,0);
                                arrayRe[1][0]=(ldiagRe[1]*elem1.real() - ldiagIm[1]*elem1.imag())+(diagRe[1]*elem2.real() - diagIm[1]*elem2.imag());
                                arrayIm[1][0]=(ldiagIm[1]*elem1.real() + ldiagRe[1]*elem1.imag())+(diagIm[1]*elem2.real() + diagRe[1]*elem2.imag());
                                elem1=m.getElement(0,1);elem2=m.getElement(1,1);elem3=m.getElement(2,1);
                                arrayRe[1][1]=(ldiagRe[1]*elem1.real() - ldiagIm[1]*elem1.imag())+(diagRe[1]*elem2.real() - diagIm[1]*elem2.imag())+(udiagRe[1]*elem3.real() - udiagIm[1]*elem3.imag());
                                arrayIm[1][1]=(ldiagIm[1]*elem1.real() + ldiagRe[1]*elem1.imag())+(diagIm[1]*elem2.real() + diagRe[1]*elem2.imag())+(udiagIm[1]*elem3.real() + udiagRe[1]*elem3.imag());
                                elem1=m.getElement(1,2);elem2=m.getElement(2,2);
                                arrayRe[1][2]=(diagRe[1]*elem1.real() - diagIm[1]*elem1.imag())+(udiagRe[1]*elem2.real() - udiagIm[1]*elem2.imag());
                                arrayIm[1][2]=(diagIm[1]*elem1.real() + diagRe[1]*elem1.imag())+(udiagIm[1]*elem2.real() + udiagRe[1]*elem2.imag());
                        } else if(mRow>4) {
                                for(int i=2;i<mRow-2;i++) {
                                        elem1=m.getElement(i-1,i-2);
                                        arrayRe[i][i-2]=(ldiagRe[i]*elem1.real() - ldiagIm[i]*elem1.imag());
                                        arrayIm[i][i-2]=(ldiagIm[i]*elem1.real() + ldiagRe[i]*elem1.imag());
                                        elem1=m.getElement(i-1,i-1);elem2=m.getElement(i,i-1);
                                        arrayRe[i][i-1]=(ldiagRe[i]*elem1.real() - ldiagIm[i]*elem1.imag())+(diagRe[i]*elem2.real() - diagIm[i]*elem2.imag());
                                        arrayIm[i][i-1]=(ldiagIm[i]*elem1.real() + ldiagRe[i]*elem1.imag())+(diagIm[i]*elem2.real() + diagRe[i]*elem2.imag());
                                        elem1=m.getElement(i-1,i);elem2=m.getElement(i,i);elem3=m.getElement(i+1,i);
                                        arrayRe[i][i]=(ldiagRe[i]*elem1.real() - ldiagIm[i]*elem1.imag())+(diagRe[i]*elem2.real() - diagIm[i]*elem2.imag())+(udiagRe[i]*elem3.real() - udiagIm[i]*elem3.imag());
                                        arrayIm[i][i]=(ldiagIm[i]*elem1.real() + ldiagRe[i]*elem1.imag())+(diagIm[i]*elem2.real() + diagRe[i]*elem2.imag())+(udiagIm[i]*elem3.real() + udiagRe[i]*elem3.imag());
                                        elem1=m.getElement(i,i+1);elem2=m.getElement(i+1,i+1);
                                        arrayRe[i][i+1]=(diagRe[i]*elem1.real() - diagIm[i]*elem1.imag())+(udiagRe[i]*elem2.real() - udiagIm[i]*elem2.imag());
                                        arrayIm[i][i+1]=(diagIm[i]*elem1.real() + diagRe[i]*elem1.imag())+(udiagIm[i]*elem2.real() + udiagRe[i]*elem2.imag());
                                        elem1=m.getElement(i+1,i+2);
                                        arrayRe[i][i+2]=(udiagRe[i]*elem1.real() - udiagIm[i]*elem1.imag());
                                        arrayIm[i][i+2]=(udiagIm[i]*elem1.real() + udiagRe[i]*elem1.imag());
                                }
                        }
                        if(mRow>3) {
                                elem1=m.getElement(mRow-3,mRow-4);
                                arrayRe[mRow-2][mRow-4]=(ldiagRe[mRow-2]*elem1.real() - ldiagIm[mRow-2]*elem1.imag());
                                arrayIm[mRow-2][mRow-4]=(ldiagIm[mRow-2]*elem1.real() + ldiagRe[mRow-2]*elem1.imag());
                                elem1=m.getElement(mRow-3,mRow-3);elem2=m.getElement(mRow-2,mRow-3);
                                arrayRe[mRow-2][mRow-3]=(ldiagRe[mRow-2]*elem1.real() - ldiagIm[mRow-2]*elem1.imag())+(diagRe[mRow-2]*elem2.real() - diagIm[mRow-2]*elem2.imag());
                                arrayIm[mRow-2][mRow-3]=(ldiagIm[mRow-2]*elem1.real() + ldiagRe[mRow-2]*elem1.imag())+(diagIm[mRow-2]*elem2.real() + diagRe[mRow-2]*elem2.imag());
                                elem1=m.getElement(mRow-3,mRow-2);elem2=m.getElement(mRow-2,mRow-2);elem3=m.getElement(mRow-1,mRow-2);
                                arrayRe[mRow-2][mRow-2]=(ldiagRe[mRow-2]*elem1.real() - ldiagIm[mRow-2]*elem1.imag())+(diagRe[mRow-2]*elem2.real() - diagIm[mRow-2]*elem2.imag())+(udiagRe[mRow-2]*elem3.real() - udiagIm[mRow-2]*elem3.imag());
                                arrayIm[mRow-2][mRow-2]=(ldiagIm[mRow-2]*elem1.real() + ldiagRe[mRow-2]*elem1.imag())+(diagIm[mRow-2]*elem2.real() + diagRe[mRow-2]*elem2.imag())+(udiagIm[mRow-2]*elem3.real() + udiagRe[mRow-2]*elem3.imag());
                                elem1=m.getElement(mRow-2,mRow-1);elem2=m.getElement(mRow-1,mRow-1);
                                arrayRe[mRow-2][mRow-1]=(diagRe[mRow-2]*elem1.real() - diagIm[mRow-2]*elem1.imag())+(udiagRe[mRow-2]*elem2.real() - udiagIm[mRow-2]*elem2.imag());
                                arrayIm[mRow-2][mRow-1]=(diagIm[mRow-2]*elem1.real() + diagRe[mRow-2]*elem1.imag())+(udiagIm[mRow-2]*elem2.real() + udiagRe[mRow-2]*elem2.imag());
                        }
                        mRow--;
                        elem1=m.getElement(mRow-1,mRow-2);
                        arrayRe[mRow][mRow-2]=(ldiagRe[mRow]*elem1.real() - ldiagIm[mRow]*elem1.imag());
                        arrayIm[mRow][mRow-2]=(ldiagIm[mRow]*elem1.real() + ldiagRe[mRow]*elem1.imag());
                        elem1=m.getElement(mRow-1,mRow-1);elem2=m.getElement(mRow,mRow-1);
                        arrayRe[mRow][mRow-1]=(ldiagRe[mRow]*elem1.real() - ldiagIm[mRow]*elem1.imag())+(diagRe[mRow]*elem2.real() - diagIm[mRow]*elem2.imag());
                        arrayIm[mRow][mRow-1]=(ldiagIm[mRow]*elem1.real() + ldiagRe[mRow]*elem1.imag())+(diagIm[mRow]*elem2.real() + diagRe[mRow]*elem2.imag());
                        elem1=m.getElement(mRow-1,mRow);elem2=m.getElement(mRow,mRow);
                        arrayRe[mRow][mRow]=(ldiagRe[mRow]*elem1.real() - ldiagIm[mRow]*elem1.imag())+(diagRe[mRow]*elem2.real() - diagIm[mRow]*elem2.imag());
                        arrayIm[mRow][mRow]=(ldiagIm[mRow]*elem1.real() + ldiagRe[mRow]*elem1.imag())+(diagIm[mRow]*elem2.real() + diagRe[mRow]*elem2.imag());
                        return new ComplexSquareMatrix(arrayRe,arrayIm);
                } else {
                        throw new MatrixDimensionException("Incompatible matrices.");
                }
        }

// HERMITIAN ADJOINT

        /**
        * Returns the hermitian adjoint of this matrix.
        * @return a complex tridiagonal matrix
        */
        public AbstractComplexMatrix hermitianAdjoint() {
                int mRow=numRows;
                final ComplexTridiagonalMatrix ans=new ComplexTridiagonalMatrix(mRow);
                System.arraycopy(ldiagRe,1,ans.udiagRe,0,ldiagRe.length-1);
                System.arraycopy(diagRe,0,ans.diagRe,0,diagRe.length);
                System.arraycopy(udiagRe,0,ans.ldiagRe,1,udiagRe.length-1);
                ans.diagIm[0]=-diagIm[0];
                ans.ldiagIm[1]=-udiagIm[0];
                mRow--;
                for(int i=1;i<mRow;i++) {
                        ans.udiagIm[i-1]=-ldiagIm[i];
                        ans.diagIm[i]=-diagIm[i];
                        ans.ldiagIm[i+1]=-udiagIm[i];
                }
                ans.udiagIm[mRow-1]=-ldiagIm[mRow];
                ans.diagIm[mRow]=-diagIm[mRow];
                return ans;
        }

// CONJUGATE

        /**
        * Returns the complex conjugate of this matrix.
        * @return a complex tridiagonal matrix
        */
        public AbstractComplexMatrix conjugate() {
                int mRow=numRows;
                final ComplexTridiagonalMatrix ans=new ComplexTridiagonalMatrix(mRow);
                System.arraycopy(ldiagRe,1,ans.ldiagRe,0,ldiagRe.length-1);
                System.arraycopy(diagRe,0,ans.diagRe,0,diagRe.length);
                System.arraycopy(udiagRe,0,ans.udiagRe,1,udiagRe.length-1);
                ans.diagIm[0]=-diagIm[0];
                ans.udiagIm[0]=-udiagIm[0];
                mRow--;
                for(int i=1;i<mRow;i++) {
                        ans.ldiagIm[i]=-ldiagIm[i];
                        ans.diagIm[i]=-diagIm[i];
                        ans.udiagIm[i]=-udiagIm[i];
                }
                ans.ldiagIm[mRow]=-ldiagIm[mRow];
                ans.diagIm[mRow]=-diagIm[mRow];
                return ans;
        }

// TRANSPOSE

        /**
        * Returns the transpose of this matrix.
        * @return a complex tridiagonal matrix
        */
        public Matrix transpose() {
                final ComplexTridiagonalMatrix ans=new ComplexTridiagonalMatrix(numRows);
                System.arraycopy(ldiagRe,1,ans.udiagRe,0,ldiagRe.length-1);
                System.arraycopy(ldiagIm,1,ans.udiagIm,0,ldiagIm.length-1);
                System.arraycopy(diagRe,0,ans.diagRe,0,diagRe.length);
                System.arraycopy(diagIm,0,ans.diagIm,0,diagIm.length);
                System.arraycopy(udiagRe,0,ans.ldiagRe,1,udiagRe.length-1);
                System.arraycopy(udiagIm,0,ans.ldiagIm,1,udiagIm.length-1);
                return ans;
        }

// MAP ELEMENTS

        /**
        * Applies a function on all the matrix elements.
        * @param f a user-defined function
        * @return a complex tridiagonal matrix
        */
        public AbstractComplexMatrix mapElements(final ComplexMapping f) {
		Complex zeroValue = f.map(Complex.ZERO);
		if(zeroValue.mod() <= JSci.GlobalSettings.ZERO_TOL)
			return tridiagonalMap(f);
		else
			return generalMap(f, zeroValue);
	}
	private AbstractComplexMatrix tridiagonalMap(ComplexMapping f) {
                int mRow=numRows;
                final ComplexTridiagonalMatrix ans=new ComplexTridiagonalMatrix(mRow);
                ans.setElement(0,0,f.map(diagRe[0],diagIm[0]));
                ans.setElement(0,1,f.map(udiagRe[0],udiagIm[0]));
                mRow--;
                for(int i=1;i<mRow;i++) {
                        ans.setElement(i,i-1,f.map(ldiagRe[i],ldiagIm[i]));
                        ans.setElement(i,i,f.map(diagRe[i],diagIm[i]));
                        ans.setElement(i,i+1,f.map(udiagRe[i],udiagIm[i]));
                }
                ans.setElement(mRow,mRow-1,f.map(ldiagRe[mRow],ldiagIm[mRow]));
                ans.setElement(mRow,mRow,f.map(diagRe[mRow],diagIm[mRow]));
                return ans;
        }
	private AbstractComplexMatrix generalMap(ComplexMapping f, Complex zeroValue) {
                final double arrayRe[][]=new double[numRows][numRows];
                final double arrayIm[][]=new double[numRows][numRows];
		for(int i=0; i<numRows; i++) {
			for(int j=0; j<numRows; j++) {
				arrayRe[i][j] = zeroValue.real();
				arrayIm[i][j] = zeroValue.imag();
			}
		}
                int mRow=numRows;
		Complex z = f.map(diagRe[0], diagIm[0]);
                arrayRe[0][0]=z.real(); arrayIm[0][0]=z.imag();
		z = f.map(udiagRe[0], udiagIm[0]);
                arrayRe[0][1]=z.real(); arrayIm[0][1]=z.imag();
                mRow--;
                for(int i=1;i<mRow;i++) {
			z = f.map(ldiagRe[i], ldiagIm[i]);
	                arrayRe[i][i-1]=z.real(); arrayIm[i][i-1]=z.imag();
			z = f.map(diagRe[i], diagIm[i]);
	                arrayRe[i][i]=z.real(); arrayIm[i][i]=z.imag();
			z = f.map(udiagRe[i], udiagIm[i]);
	                arrayRe[i][i+1]=z.real(); arrayIm[i][i+1]=z.imag();
                }
		z = f.map(ldiagRe[mRow], ldiagIm[mRow]);
                arrayRe[mRow][mRow-1]=z.real(); arrayIm[mRow][mRow-1]=z.imag();
		z = f.map(diagRe[mRow], diagIm[mRow]);
                arrayRe[mRow][mRow]=z.real(); arrayIm[mRow][mRow]=z.imag();
                return new ComplexSquareMatrix(arrayRe, arrayIm);
	}
}
