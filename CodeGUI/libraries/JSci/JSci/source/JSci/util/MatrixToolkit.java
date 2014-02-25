package JSci.util;

import JSci.maths.Complex;
import JSci.maths.matrices.*;

/**
* This is a useful collection of matrix related methods.
* @author Mark Hale
*/
public final class MatrixToolkit {
        private MatrixToolkit() {}

        /**
        * Creates a random generated square matrix.
        */
        public static DoubleSquareMatrix randomSquareMatrix(int size) {
                return (DoubleSquareMatrix)new DoubleSquareMatrix(size).mapElements(RandomMap.MAP);
        }
        /**
        * Creates a random generated tridiagonal matrix.
        */
        public static DoubleTridiagonalMatrix randomTridiagonalMatrix(int size) {
                return new DoubleTridiagonalMatrix(toArray(randomSquareMatrix(size)));
        }
        /**
        * Creates a random generated diagonal matrix.
        */
        public static DoubleDiagonalMatrix randomDiagonalMatrix(int size) {
                return new DoubleDiagonalMatrix(toArray(randomSquareMatrix(size)));
        }

        /**
        * Creates a random generated square matrix.
        */
        public static ComplexSquareMatrix randomComplexSquareMatrix(int size) {
                return (ComplexSquareMatrix)new ComplexSquareMatrix(size).mapElements(RandomMap.MAP);
        }
        /**
        * Creates a random generated tridiagonal matrix.
        */
        public static ComplexTridiagonalMatrix randomComplexTridiagonalMatrix(int size) {
                return new ComplexTridiagonalMatrix(toArray(randomComplexSquareMatrix(size)));
        }
        /**
        * Creates a random generated diagonal matrix.
        */
        public static ComplexDiagonalMatrix randomComplexDiagonalMatrix(int size) {
                return new ComplexDiagonalMatrix(toArray(randomComplexSquareMatrix(size)));
        }

        /**
        * Converts a matrix to an array.
        */
        public static double[][] toArray(AbstractDoubleMatrix v) {
                double array[][]=new double[v.rows()][v.columns()];
                for(int j,i=0;i<array.length;i++) {
                        for(j=0;j<array[0].length;j++)
                                array[i][j]=v.getElement(i,j);
                }
                return array;
        }
        /**
        * Converts a matrix to an array.
        */
        public static Complex[][] toArray(AbstractComplexMatrix v) {
                Complex array[][]=new Complex[v.rows()][v.columns()];
                for(int j,i=0;i<array.length;i++) {
                        for(j=0;j<array[0].length;j++)
                                array[i][j]=v.getElement(i,j);
                }
                return array;
        }

        public static DoubleSquareMatrix rosserMatrix() {
            return new DoubleSquareMatrix(new double[][] {
                { 611,  196, -192,  407,   -8,  -52,  -49,   29},
                { 196,  899,  113, -192,  -71,  -43,   -8,  -44},
                {-192,  113,  899,  196,   61,   49,    8,   52},
                { 407, -192,  196,  611,    8,   44,   59,  -23},
                {  -8,  -71,   61,    8,  411, -599,  208,  208},
                { -52,  -43,   49,   44, -599,  411,  208,  208},
                { -49,   -8,    8,   59,  208,  208,   99, -911},
                {  29,  -44,   52,  -23,  208,  208, -911,   99}
            });
        }

        public static DoubleTridiagonalMatrix wilkinsonMatrix(int n) {
            DoubleTridiagonalMatrix m = new DoubleTridiagonalMatrix(n);
            int half = (n-1)/2;
            for(int i=0; i<half; i++) {
                m.setElement(i, i+1, 1.0);
                m.setElement(i+1, i, 1.0);
                m.setElement(i, i, half-i);
                m.setElement(n-1-i, n-1-i, half-i);
                m.setElement(n-1-i, n-2-i, 1.0);
                m.setElement(n-2-i, n-1-i, 1.0);
            }
            return m;
        }
}
