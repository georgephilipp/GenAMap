package JSci.maths.algebras;

import JSci.maths.*;
import JSci.maths.matrices.AbstractComplexMatrix;
import JSci.maths.matrices.AbstractComplexSquareMatrix;
import JSci.maths.matrices.ComplexSquareMatrix;
import JSci.maths.matrices.AbstractDoubleSquareMatrix;
import JSci.maths.matrices.DoubleSquareMatrix;
import JSci.maths.vectors.AbstractDoubleVector;
import JSci.maths.vectors.Double3Vector;
import JSci.maths.vectors.VectorDimensionException;
import JSci.maths.fields.ComplexField;

/**
* The su2Dim3 class encapsulates su(2) algebras using
* the 3 dimensional (adjoint) representation.
* Elements are represented by 3-vectors with a matrix basis.
* @version 1.2
* @author Mark Hale
*/
public final class su2Dim3 extends LieAlgebra {
        /**
        * Basis array.
        */
        private final static Complex t1[][]={
                {Complex.ZERO,ComplexField.SQRT_HALF,Complex.ZERO},
                {ComplexField.SQRT_HALF,Complex.ZERO,ComplexField.SQRT_HALF},
                {Complex.ZERO,ComplexField.SQRT_HALF,Complex.ZERO}
        };
        private final static Complex t2[][]={
                {Complex.ZERO,ComplexField.MINUS_SQRT_HALF_I,Complex.ZERO},
                {ComplexField.SQRT_HALF_I,Complex.ZERO,ComplexField.MINUS_SQRT_HALF_I},
                {Complex.ZERO,ComplexField.SQRT_HALF_I,Complex.ZERO}
        };
        private final static Complex t3[][]={
                {Complex.ONE,Complex.ZERO,Complex.ZERO},
                {Complex.ZERO,Complex.ZERO,Complex.ZERO},
                {Complex.ZERO,Complex.ZERO,ComplexField.MINUS_ONE}
        };
        /**
        * Basis.
        */
        private final static AbstractComplexSquareMatrix basisMatrices[]={
                new ComplexSquareMatrix(t1),
                new ComplexSquareMatrix(t2),
                new ComplexSquareMatrix(t3)
        };
        /**
        * Metric array.
        */
        private final static double g[][]={
                {-2.0,0.0,0.0},
                {0.0,-2.0,0.0},
                {0.0,0.0,-2.0}
        };
        /**
        * Cartan metric.
        */
        private final static AbstractDoubleSquareMatrix metricMatrix=new DoubleSquareMatrix(g);

        private final static su2Dim3 _instance = new su2Dim3();
        /**
        * Constructs an su(2) algebra.
        */
        private su2Dim3() {
                super("su(2) [3]");
        }
        /**
        * Singleton.
        */
        public static final su2Dim3 getInstance() {
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
                return ((Double3Vector)b).multiply((Double3Vector)a);
        }
        /**
        * Returns the Killing Form of two elements (scalar product).
        */
        public double killingForm(final AbstractDoubleVector a, final AbstractDoubleVector b) {
                return a.scalarProduct(metricMatrix.multiply(b));
        }
        /**
        * Returns the basis used to represent the Lie algebra.
        */
        public AbstractComplexSquareMatrix[] basis() {
                return basisMatrices;
        }
        /**
        * Returns the Cartan metric.
        */
        public AbstractDoubleSquareMatrix cartanMetric() {
                return metricMatrix;
        }
}

