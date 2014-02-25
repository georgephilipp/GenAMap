package JSci.maths.algebras;

import JSci.maths.*;
import JSci.maths.matrices.AbstractComplexSquareMatrix;
import JSci.maths.vectors.AbstractDoubleVector;
import JSci.maths.fields.ComplexField;

/**
* The LieAlgebra class provides an abstract encapsulation for Lie algebras.
* Elements are represented by vectors with a matrix basis.
* @jsci.planetmath LieAlgebra
* @version 1.2
* @author Mark Hale
*/
public abstract class LieAlgebra extends Object {
        private String label;
        /**
        * Constructs a Lie algebra.
        * @param aLabel a label that identifies this algebra
        */
        public LieAlgebra(String aLabel) {
                label=aLabel;
        }
        /**
        * Returns a string representing this algebra.
        */
        public final String toString() {
                return label;
        }
        /**
        * Returns an element as a matrix (vector*basis).
        */
        public abstract AbstractComplexSquareMatrix getElement(AbstractDoubleVector v);
        /**
        * Returns the Lie bracket (commutator) of two elements.
        */
        public abstract AbstractDoubleVector multiply(AbstractDoubleVector a, AbstractDoubleVector b);
        /**
        * Returns the basis used to represent the Lie algebra.
        */
        public abstract AbstractComplexSquareMatrix[] basis();
}

