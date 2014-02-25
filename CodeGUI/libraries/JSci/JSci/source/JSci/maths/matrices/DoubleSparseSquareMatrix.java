package JSci.maths.matrices;

import JSci.GlobalSettings;
import JSci.maths.Mapping;
import JSci.maths.DimensionException;
import JSci.maths.vectors.AbstractDoubleVector;
import JSci.maths.vectors.DoubleVector;
import JSci.maths.vectors.DoubleSparseVector;

/**
* The DoubleSparseSquareMatrix class provides an object for encapsulating sparse square matrices.
* Uses compressed row storage (Yale sparse matrix format).
* @version 1.4
* @author Mark Hale
*/
public final class DoubleSparseSquareMatrix extends AbstractDoubleSquareMatrix {
        /**
        * Matrix elements.
        */
        private double elements[];
        /**
        * Sparse indexing data.
        * Contains the column positions of each element,
        * e.g. <code>colPos[n]</code> is the column position
        * of the <code>n</code>th element.
        */
        private int colPos[];
        /**
        * Sparse indexing data.
        * Contains the indices of the start of each row,
        * e.g. <code>rows[i]</code> is the index
        * where the <code>i</code>th row starts.
        */
        private int rows[];
        /**
         * Amount by which to increase the capacity.
         */
        private int capacityIncrement = 1;
        /**
        * Constructs an empty matrix.
        * @param size the number of rows/columns
        */
        public DoubleSparseSquareMatrix(final int size) {
                super(size);
                elements=new double[0];
                colPos=new int[0];
                rows=new int[numRows+1];
        }
        public DoubleSparseSquareMatrix(final int size, int capacityIncrement) {
            this(size);
            this.capacityIncrement = capacityIncrement;
        }
        /**
        * Constructs a matrix from an array.
        * @param array an assigned value
        * @exception MatrixDimensionException If the array is not square.
        */
        public DoubleSparseSquareMatrix(final double array[][]) {
                super(array.length);
                rows=new int[numRows+1];
                int n=0;
                for(int i=0;i<numRows;i++) {
                        if(array[i].length != array.length)
                                throw new MatrixDimensionException("Array is not square.");
                        for(int j=0;j<numCols;j++) {
                                if(Math.abs(array[i][j])>GlobalSettings.ZERO_TOL)
                                        n++;
                        }
                }
                elements=new double[n];
                colPos=new int[n];
                n=0;
                for(int i=0;i<numRows;i++) {
                        rows[i]=n;
                        for(int j=0;j<numCols;j++) {
                                if(Math.abs(array[i][j])>GlobalSettings.ZERO_TOL) {
                                        elements[n]=array[i][j];
                                        colPos[n]=j;
                                        n++;
                                }
                        }
                }
                rows[numRows]=n;
        }
        /**
        * Compares two double sparse square matrices for equality.
        * @param m a double matrix
        */
        public boolean equals(AbstractDoubleSquareMatrix m, double tol) {
                if(numRows==m.numRows && numCols==m.numCols) {
                        if(m instanceof DoubleSparseSquareMatrix) {
                                return this.equals((DoubleSparseSquareMatrix)m);
                        } else {
        			double sumSqr = 0;
                                for(int i=0;i<numRows;i++) {
                                        for(int j=0;j<numCols;j++) {
        					double delta = getElement(i,j)-m.getElement(i,j);
        					sumSqr += delta*delta;
                                        }
                                }
                                return (sumSqr <= tol*tol);
                        }
                } else
                        return false;
        }
        public final boolean equals(DoubleSparseSquareMatrix m) {
		return equals(m, GlobalSettings.ZERO_TOL);
	}
        public boolean equals(DoubleSparseSquareMatrix m, double tol) {
                if(numRows==m.numRows && numCols==m.numCols) {
                        for(int i=1;i<rows.length;i++) {
                                if(rows[i]!=m.rows[i])
                                        return false;
                        }
                        double sumSqr = 0.0;
                        for(int i=0;i<rows[numRows];i++) {
                                if(colPos[i]!=m.colPos[i])
                                        return false;
                                double delta = elements[i] - m.elements[i];
                                sumSqr += delta*delta;
                        }
                        return (sumSqr <= tol*tol);
                } else
                        return false;
        }
        /**
        * Returns a string representing this matrix.
        */
        public String toString() {
                final StringBuffer buf=new StringBuffer(numRows*numCols);
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
        * @return an integer square matrix
        */
        public AbstractIntegerMatrix toIntegerMatrix() {
                final int ans[][]=new int[numRows][numCols];
                for(int i=0;i<numRows;i++) {
                        for(int j=0;j<numCols;j++)
                                ans[i][j]=Math.round((float)getElement(i,j));
                }
                return new IntegerSquareMatrix(ans);
        }
        /**
        * Converts this matrix to a complex matrix.
        * @return a complex square matrix
        */
        public AbstractComplexMatrix toComplexMatrix() {
                final double arrayRe[][]=new double[numRows][numCols];
                for(int i=0;i<numRows;i++) {
                        for(int j=0;j<numCols;j++)
                                arrayRe[i][j]=getElement(i,j);
                }
                return new ComplexSquareMatrix(arrayRe,new double[numRows][numCols]);
        }
        /**
        * Returns an element of the matrix.
        * @param i row index of the element
        * @param j column index of the element
        * @exception MatrixDimensionException If attempting to access an invalid element.
        */
        public double getElement(final int i, final int j) {
                if(i>=0 && i<numRows && j>=0 && j<numCols) {
                        int p = rows[i];
                        while(p<rows[i+1] && colPos[p]<j) {
                            p++;
                        }
                        if(p<rows[i+1] && colPos[p]==j)
                            return elements[p];
                        else
                            return 0.0;
                } else
                        throw new MatrixDimensionException(getInvalidElementMsg(i,j));
        }
        /**
        * Sets the value of an element of the matrix.
        * @param i row index of the element
        * @param j column index of the element
        * @param x a number
        * @exception MatrixDimensionException If attempting to access an invalid element.
        */
        public void setElement(final int i, final int j, final double x) {
                if(i>=0 && i<numRows && j>=0 && j<numCols) {
                        int p = rows[i];
                        while(p<rows[i+1] && colPos[p]<j) {
                            p++;
                        }
                        if(p<rows[i+1] && colPos[p] == j) {
                             if(Math.abs(x)<=GlobalSettings.ZERO_TOL) {
                                // shrink if zero
                                System.arraycopy(elements,p+1,elements,p,rows[numRows]-p-1);
                                System.arraycopy(colPos,p+1,colPos,p,rows[numRows]-p-1);
                                for(int k=i+1;k<rows.length;k++) {
                                    rows[k]--;
                                }
                            } else {
                                // overwrite
                                elements[p]=x;
                            }
                        } else if(Math.abs(x) > GlobalSettings.ZERO_TOL) {
                            // expand
                            if(rows[numRows] == elements.length) {
                                // increase capacity
                                final double oldMatrix[]=elements;
                                final int oldColPos[]=colPos;
                                elements=new double[oldMatrix.length+capacityIncrement];
                                colPos=new int[oldColPos.length+capacityIncrement];
                                System.arraycopy(oldMatrix,0,elements,0,p);
                                System.arraycopy(oldColPos,0,colPos,0,p);
                                System.arraycopy(oldMatrix,p,elements,p+1,oldMatrix.length-p);
                                System.arraycopy(oldColPos,p,colPos,p+1,oldColPos.length-p);
                            } else {
                                System.arraycopy(elements,p,elements,p+1,rows[numRows]-p);
                                System.arraycopy(colPos,p,colPos,p+1,rows[numRows]-p);
                            }
                            elements[p]=x;
                            colPos[p]=j;
                            for(int k=i+1;k<rows.length;k++) {
                                rows[k]++;
                            }
                        }
                } else
                        throw new MatrixDimensionException(getInvalidElementMsg(i,j));
        }
        /**
         * Returns the number of non-zero elements.
         */
        public int elementCount() {
            return rows[numRows];
        }
        /**
        * Returns the determinant.
        */
        public double det() {
                int[] luPivot = new int[numRows+1];
                final AbstractDoubleSquareMatrix lu[]=this.luDecompose(luPivot);
                double det=lu[1].getElement(0,0);
                for(int i=1;i<numRows;i++)
                        det*=lu[1].getElement(i,i);
                return det*luPivot[numRows];
        }
        /**
        * Returns the trace.
        */
        public double trace() {
                double result=getElement(0,0);
                for(int i=1;i<numRows;i++)
                        result+=getElement(i,i);
                return result;
        }
        /**
        * Returns the l<sup><img border=0 alt="infinity" src="doc-files/infinity.gif"></sup>-norm.
        */
        public double infNorm() {
                double result=0.0,tmpResult;
                for(int j,i=0;i<numRows;i++) {
                        tmpResult=0.0;
                        for(j=rows[i];j<rows[i+1];j++)
                                tmpResult+=Math.abs(elements[j]);
                        if(tmpResult>result)
                                result=tmpResult;
                }
                return result;
        }
        /**
        * Returns the Frobenius (l<sup>2</sup>) norm.
        */
        public double frobeniusNorm() {
                double result=0.0;
                for(int i=0;i<rows[numRows];i++)
                        result+=elements[i]*elements[i];
                return Math.sqrt(result);
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
                if(m instanceof DoubleSparseSquareMatrix)
                        return add((DoubleSparseSquareMatrix)m);
                if(m instanceof DoubleSquareMatrix)
                        return add((DoubleSquareMatrix)m);

                if(numRows==m.rows() && numCols==m.columns()) {
                        final double array[][]=new double[numRows][numCols];
                        for(int i=0;i<numRows;i++) {
                                for(int j=rows[i];j<rows[i+1];j++)
                                        array[i][colPos[j]]=elements[j];
                                array[i][0]+=m.getElement(i,0);
                                for(int j=1;j<numCols;j++)
                                        array[i][j]+=m.getElement(i,j);
                        }
                        return new DoubleSquareMatrix(array);
                } else {
                        throw new MatrixDimensionException("Matrices are different sizes.");
                }
        }
        public DoubleSquareMatrix add(final DoubleSquareMatrix m) {
                if(numRows==m.numRows && numCols==m.numCols) {
                        final double array[][]=new double[numRows][numCols];
                        for(int i=0;i<numRows;i++) {
                                for(int j=rows[i];j<rows[i+1];j++)
                                        array[i][colPos[j]]=elements[j];
                                array[i][0]+=m.matrix[i][0];
                                for(int j=1;j<numCols;j++)
                                        array[i][j]+=m.matrix[i][j];
                        }
                        return new DoubleSquareMatrix(array);
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }
        /**
        * Returns the addition of this matrix and another.
        * @param m a double sparse matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public DoubleSparseSquareMatrix add(final DoubleSparseSquareMatrix m) {
                if(numRows==m.numRows && numCols==m.numCols) {
                        DoubleSparseSquareMatrix ans=new DoubleSparseSquareMatrix(numRows);
                        for(int i=0;i<numRows;i++) {
                                for(int j=0;j<numCols;j++)
                                        ans.setElement(i,j,getElement(i,j)+m.getElement(i,j));
                        }
                        return ans;
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }

// SUBTRACTION

        /**
        * Returns the subtraction of this matrix and another.
        * @param m a double matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public AbstractDoubleSquareMatrix subtract(final AbstractDoubleSquareMatrix m) {
                if(m instanceof DoubleSparseSquareMatrix)
                        return subtract((DoubleSparseSquareMatrix)m);
                if(m instanceof DoubleSquareMatrix)
                        return subtract((DoubleSquareMatrix)m);

                if(numRows==m.rows() && numCols==m.columns()) {
                        final double array[][]=new double[numRows][numCols];
                        for(int i=0;i<numRows;i++) {
                                for(int j=rows[i];j<rows[i+1];j++)
                                        array[i][colPos[j]]=elements[j];
                                array[i][0]-=m.getElement(i,0);
                                for(int j=1;j<numCols;j++)
                                        array[i][j]-=m.getElement(i,j);
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
                                for(int j=rows[i];j<rows[i+1];j++)
                                        array[i][colPos[j]]=elements[j];
                                array[i][0]-=m.matrix[i][0];
                                for(int j=1;j<numCols;j++)
                                        array[i][j]-=m.matrix[i][j];
                        }
                        return new DoubleSquareMatrix(array);
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }
        /**
        * Returns the addition of this matrix and another.
        * @param m a double sparse matrix
        * @exception MatrixDimensionException If the matrices are different sizes.
        */
        public DoubleSparseSquareMatrix subtract(final DoubleSparseSquareMatrix m) {
                if(numRows==m.numRows && numCols==m.numCols) {
                        DoubleSparseSquareMatrix ans=new DoubleSparseSquareMatrix(numRows);
                        for(int i=0;i<numRows;i++) {
                                for(int j=0;j<numCols;j++)
                                        ans.setElement(i,j,getElement(i,j)-m.getElement(i,j));
                        }
                        return ans;
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }

// SCALAR MULTIPLICATION

        /**
        * Returns the multiplication of this matrix by a scalar.
        * @param x a double
        * @return a double sparse matrix
        */
        public AbstractDoubleMatrix scalarMultiply(final double x) {
                final DoubleSparseSquareMatrix ans=new DoubleSparseSquareMatrix(numRows);
                ans.elements=new double[elements.length];
                ans.colPos=new int[colPos.length];
                System.arraycopy(colPos,0,ans.colPos,0,colPos.length);
                System.arraycopy(rows,0,ans.rows,0,rows.length);
                for(int i=0;i<rows[numRows];i++)
                        ans.elements[i]=x*elements[i];
                return ans;
        }

        public AbstractDoubleMatrix scalarDivide(final double x) {
                final DoubleSparseSquareMatrix ans=new DoubleSparseSquareMatrix(numRows);
                ans.elements=new double[elements.length];
                ans.colPos=new int[colPos.length];
                System.arraycopy(colPos,0,ans.colPos,0,colPos.length);
                System.arraycopy(rows,0,ans.rows,0,rows.length);
                for(int i=0;i<rows[numRows];i++)
                        ans.elements[i]=elements[i]/x;
                return ans;
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

                if(numRows==m.numRows && numCols==m.numCols) {
                        double ans=0.0;
                        for(int i=0;i<numRows;i++) {
                                for(int j=rows[i];j<rows[i+1];j++)
                                        ans+=elements[j]*m.getElement(i,colPos[j]);
                        }
                        return ans;
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }
        public double scalarProduct(final DoubleSquareMatrix m) {
                if(numRows==m.numRows && numCols==m.numCols) {
                        double ans=0.0;
                        for(int i=0;i<numRows;i++) {
                                for(int j=rows[i];j<rows[i+1];j++)
                                        ans+=elements[j]*m.matrix[i][colPos[j]];
                        }
                        return ans;
                } else
                        throw new MatrixDimensionException("Matrices are different sizes.");
        }

// MATRIX MULTIPLICATION

        /**
        * Returns the multiplication of a vector by this matrix.
        * @param v a double vector
        * @exception DimensionException If the matrix and vector are incompatible.
        */
        public AbstractDoubleVector multiply(final AbstractDoubleVector v) {
                if(v instanceof DoubleSparseVector)
                    return multiply((DoubleSparseVector)v);
                if(numCols==v.dimension()) {
                        final double array[]=new double[numRows];
                        for(int i=0;i<numRows;i++) {
                                for(int j=rows[i];j<rows[i+1];j++)
                                        array[i]+=elements[j]*v.getComponent(colPos[j]);
                        }
                        return new DoubleVector(array);
                } else
                        throw new DimensionException("Matrix and vector are incompatible.");
        }
        /**
        * Returns the multiplication of a sparse vector by this matrix.
        * @param v a double vector
        * @exception DimensionException If the matrix and vector are incompatible.
        */
        public AbstractDoubleVector multiply(final DoubleSparseVector v) {
                if(numCols==v.dimension()) {
                        DoubleSparseVector res = new DoubleSparseVector(numRows);
                        for(int i=0;i<numRows;i++) {
                                double tmp = 0.0;
                                for(int j=rows[i];j<rows[i+1];j++) {
                                        tmp += elements[j]*v.getComponent(colPos[j]);
                                }
                                res.setComponent(i, tmp);
                        }
                        return res;
                } else
                        throw new DimensionException("Matrix and vector are incompatible.");
        }
        /**
        * Returns the multiplication of this matrix and another.
        * @param m a double matrix
        * @exception MatrixDimensionException If the matrices are incompatible.
        */
        public AbstractDoubleSquareMatrix multiply(final AbstractDoubleSquareMatrix m) {
                if(m instanceof DoubleSparseSquareMatrix)
                        return multiply((DoubleSparseSquareMatrix)m);
                if(m instanceof DoubleSquareMatrix)
                        return multiply((DoubleSquareMatrix)m);

                if(numCols==m.numRows) {
                        final double array[][]=new double[numRows][m.numCols];
                        for(int j=0;j<numRows;j++) {
                                for(int k=0;k<m.numCols;k++) {
                                        for(int n=rows[j];n<rows[j+1];n++)
                                                array[j][k]+=elements[n]*m.getElement(colPos[n],k);
                                }
                        }
                        return new DoubleSquareMatrix(array);
                } else
                        throw new MatrixDimensionException("Incompatible matrices.");
        }
        public DoubleSquareMatrix multiply(final DoubleSquareMatrix m) {
                if(numCols==m.numRows) {
                        final double array[][]=new double[numRows][m.numCols];
                        for(int j=0;j<numRows;j++) {
                                for(int k=0;k<m.numCols;k++) {
                                        for(int n=rows[j];n<rows[j+1];n++)
                                                array[j][k]+=elements[n]*m.matrix[colPos[n]][k];
                                }
                        }
                        return new DoubleSquareMatrix(array);
                } else
                        throw new MatrixDimensionException("Incompatible matrices.");
        }
        /**
        * Returns the multiplication of this matrix and another.
        * @param m a double sparse matrix
        * @exception MatrixDimensionException If the matrices are incompatible.
        */
        public DoubleSparseSquareMatrix multiply(final DoubleSparseSquareMatrix m) {
                if(numCols==m.numRows) {
                        DoubleSparseSquareMatrix ans=new DoubleSparseSquareMatrix(numRows);
                        for(int j=0;j<numRows;j++) {
                                for(int k=0;k<numCols;k++) {
                                        double tmp=0.0;
                                        for(int n=rows[j];n<rows[j+1];n++)
                                                tmp+=elements[n]*m.getElement(colPos[n],k);
                                        ans.setElement(j,k,tmp);
                                }
                        }
                        return ans;
                } else
                        throw new MatrixDimensionException("Incompatible matrices.");
        }

// TRANSPOSE

        /**
        * Returns the transpose of this matrix.
        * @return a double sparse matrix
        */
        public Matrix transpose() {
                final DoubleSparseSquareMatrix ans=new DoubleSparseSquareMatrix(numRows);
                for(int i=0;i<numRows;i++) {
                        ans.setElement(0,i,getElement(i,0));
                        for(int j=1;j<numCols;j++)
                                ans.setElement(j,i,getElement(i,j));
                }
                return ans;
        }

// LU DECOMPOSITION

        /**
        * Returns the LU decomposition of this matrix.
        * @param pivot an empty array of length <code>rows()+1</code>
        * to hold the pivot information (null if not interested)
        * @return an array with [0] containing the L-matrix
        * and [1] containing the U-matrix.
        */
        public AbstractDoubleSquareMatrix[] luDecompose(int pivot[]) {
                AbstractDoubleSquareMatrix[] LU = luDecompose_cache(pivot);
                if(LU != null)
                    return LU;
                final double arrayL[][]=new double[numRows][numCols];
                final double arrayU[][]=new double[numRows][numCols];
                if(pivot==null)
                        pivot=new int[numRows+1];
                for(int i=0;i<numRows;i++)
                        pivot[i]=i;
                pivot[numRows]=1;
        // LU decomposition to arrayU
                for(int j=0;j<numCols;j++) {
                        for(int i=0;i<j;i++) {
                                double tmp=getElement(pivot[i],j);
                                for(int k=0;k<i;k++)
                                        tmp-=arrayU[i][k]*arrayU[k][j];
                                arrayU[i][j]=tmp;
                        }
                        double max=0.0;
                        int pivotrow=j;
                        for(int i=j;i<numRows;i++) {
                                double tmp=getElement(pivot[i],j);
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
                                pivot[numRows]=-pivot[numRows];
                        }
                // divide by pivot
                        for(int i=j+1;i<numRows;i++)
                                arrayU[i][j]/=arrayU[j][j];
                }
                // move lower triangular part to arrayL
                for(int j=0;j<numCols;j++) {
                        arrayL[j][j]=1.0;
                        for(int i=j+1;i<numRows;i++) {
                                arrayL[i][j]=arrayU[i][j];
                                arrayU[i][j]=0.0;
                        }
                }
                DoubleSquareMatrix L=new DoubleSquareMatrix(arrayL);
                DoubleSquareMatrix U=new DoubleSquareMatrix(arrayU);
                int[] LUpivot=new int[pivot.length];
                System.arraycopy(pivot,0,LUpivot,0,pivot.length);
                luCache = new DoubleLUCache(L, U, LUpivot);
                return new DoubleSquareMatrix[] {L, U};
        }
        /**
        * Returns the LU decomposition of this matrix.
        * Warning: no pivoting.
        * @return an array with [0] containing the L-matrix
        * and [1] containing the U-matrix.
        * @jsci.planetmath LUDecomposition
        */
        public AbstractDoubleSquareMatrix[] luDecompose() {
                final double arrayL[][]=new double[numRows][numCols];
                final double arrayU[][]=new double[numRows][numCols];
        // LU decomposition to arrayU
                for(int j=0;j<numCols;j++) {
                        for(int i=0;i<j;i++) {
                                double tmp=getElement(i,j);
                                for(int k=0;k<i;k++)
                                        tmp-=arrayU[i][k]*arrayU[k][j];
                                arrayU[i][j]=tmp;
                        }
                        for(int i=j;i<numRows;i++) {
                                double tmp=getElement(i,j);
                                for(int k=0;k<j;k++)
                                        tmp-=arrayU[i][k]*arrayU[k][j];
                                arrayU[i][j]=tmp;
                        }
                // divide
                        for(int i=j+1;i<numRows;i++)
                                arrayU[i][j]/=arrayU[j][j];
                }
                // move lower triangular part to arrayL
                for(int j=0;j<numCols;j++) {
                        arrayL[j][j]=1.0;
                        for(int i=j+1;i<numRows;i++) {
                                arrayL[i][j]=arrayU[i][j];
                                arrayU[i][j]=0.0;
                        }
                }
                AbstractDoubleSquareMatrix[] lu=new AbstractDoubleSquareMatrix[2];
                lu[0]=new DoubleSquareMatrix(arrayL);
                lu[1]=new DoubleSquareMatrix(arrayU);
                return lu;
        }

// CHOLESKY DECOMPOSITION

        /**
        * Returns the Cholesky decomposition of this matrix.
        * Matrix must be symmetric and positive definite.
        * @return an array with [0] containing the L-matrix and [1] containing the U-matrix.
        */
        public AbstractDoubleSquareMatrix[] choleskyDecompose() {
                final double arrayL[][]=new double[numRows][numCols];
                final double arrayU[][]=new double[numRows][numCols];
                arrayL[0][0]=arrayU[0][0]=Math.sqrt(getElement(0,0));
                for(int i=1;i<numRows;i++)
                        arrayL[i][0]=arrayU[0][i]=getElement(i,0)/arrayL[0][0];
                for(int j=1;j<numCols;j++) {
                        double tmp=getElement(j,j);
                        for(int i=0;i<j;i++)
                                tmp-=arrayL[j][i]*arrayL[j][i];
                        arrayL[j][j]=arrayU[j][j]=Math.sqrt(tmp);
                        for(int i=j+1;i<numRows;i++) {
                                tmp=getElement(i,j);
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

// MAP ELEMENTS

        /**
        * Applies a function on all the matrix elements.
        * @param f a user-defined function
        * @return a double sparse matrix
        */
        public AbstractDoubleMatrix mapElements(final Mapping f) {
                final DoubleSparseSquareMatrix ans=new DoubleSparseSquareMatrix(numRows);
                ans.elements=new double[elements.length];
                ans.colPos=new int[colPos.length];
                System.arraycopy(colPos,0,ans.colPos,0,colPos.length);
                System.arraycopy(rows,0,ans.rows,0,rows.length);
                for(int i=0;i<rows[numRows];i++)
                        ans.elements[i]=f.map(elements[i]);
                return ans;
        }
}

