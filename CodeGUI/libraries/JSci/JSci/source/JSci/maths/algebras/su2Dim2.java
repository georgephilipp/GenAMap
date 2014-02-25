package JSci.maths.algebras;

import JSci.maths.*;
import JSci.maths.matrices.AbstractComplexMatrix;
import JSci.maths.matrices.AbstractComplexSquareMatrix;
import JSci.maths.matrices.ComplexSquareMatrix;
import JSci.maths.vectors.AbstractDoubleVector;
import JSci.maths.vectors.Double3Vector;
import JSci.maths.vectors.VectorDimensionException;
import JSci.maths.fields.ComplexField;

/**
* The su2Dim2 class encapsulates su(2) algebras using
* the 2 dimensional (fundamental) representation.
* Elements are represented by 3-vectors with a matrix basis.
* @version 1.2
* @author Mark Hale
*/
public final class su2Dim2 extends LieAlgebra {
        private final static Complex t1[][]={
                {Complex.ZERO,ComplexField.HALF},
                {ComplexField.HALF,Complex.ZERO}
        };
        private final static Complex t2[][]={
                {Complex.ZERO,ComplexField.HALF_I},
                {ComplexField.MINUS_HALF_I,Complex.ZERO}
        };
        private final static Complex t3[][]={
                {ComplexField.HALF,Complex.ZERO},
                {Complex.ZERO,ComplexField.MINUS_HALF}
        };
        /**
        * Basis.
        */
        private final static AbstractComplexSquareMatrix basisMatrices[]={
                new ComplexSquareMatrix(t1),
                new ComplexSquareMatrix(t2),
                new ComplexSquareMatrix(t3)
        };

        private final static su2Dim2 _instance = new su2Dim2();
        /**
        * Constructs an su(2) algebra.
        */
        private su2Dim2() {
                super("su(2) [2]");
        }
        /**
        * Singleton.
        */
        public static final su2Dim2 getInstance() {
                return _instance;
        }
        /**
        * Returns an element as a matrix (vector*basis).
        */
        public AbstractComplexSquareMatrix getElement(final AbstractDoubleVector v) {
                AbstractComplexMatrix m=basisMatrices[0].scalarMultiply(v.getComponent(0));
                m=m.add(basisMatrices[1].scalarMultiply(v.getComponent(1)));
                m=m.add(basisMatrices[2].scalarMultiply(v.getComponent(2)));
                return (AbstractComplexSquareMatrix)m.scalarMultiply(Complex.I);
        }
        /**
        * Returns the Lie bracket (commutator) of two elements.
        * Same as the vector cross product.
        */
        public AbstractDoubleVector multiply(final AbstractDoubleVector a, final AbstractDoubleVector b) {
                if(!(a instanceof Double3Vector) || !(b instanceof Double3Vector))
                        throw new VectorDimensionException("Vectors must be 3-vectors.");
                return ((Double3Vector)a).multiply((Double3Vector)b);
        }
        /**
        * Returns the basis used to represent the Lie algebra.
        */
        public AbstractComplexSquareMatrix[] basis() {
                return basisMatrices;
        }
}

