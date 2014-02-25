package JSci.maths.matrices;

import JSci.maths.algebras.Algebra;

/**
* The Matrix superclass provides an abstract encapsulation for matrices.
* @jsci.planetmath Matrix
 * @jsci.wikipedia Matrix_(mathematics)
* @version 2.2
* @author Mark Hale
*/
public abstract class Matrix extends Object implements Algebra.Member {
        /**
        * The number of rows.
        */
        protected final int numRows;
        /**
        * The number of columns.
        */
        protected final int numCols;
        /**
        * Constructs a matrix.
        */
        public Matrix(int rows, int cols) {
                numRows = rows;
                numCols = cols;
        }
        /**
        * Returns the number of rows.
        */
        public final int rows() {
                return numRows;
        }
        /**
        * Returns the number of columns.
        */
        public final int columns() {
                return numCols;
        }
        /**
        * Returns the transpose of this matrix.
        * @jsci.planetmath Transpose
        */
        public abstract Matrix transpose();
        /**
        * Returns an "invalid element" error message.
        * @param i row index of the element
        * @param j column index of the element
        */
        protected static String getInvalidElementMsg(int i, int j) {
                return "("+i+','+j+") is an invalid element for this matrix.";
        }
}
