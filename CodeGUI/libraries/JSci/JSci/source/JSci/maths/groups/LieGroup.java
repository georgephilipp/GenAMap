package JSci.maths.groups;

import JSci.maths.*;
import JSci.maths.vectors.AbstractDoubleVector;
import JSci.maths.matrices.AbstractComplexMatrix;
import JSci.maths.matrices.AbstractComplexSquareMatrix;
import JSci.maths.matrices.ComplexDiagonalMatrix;

/**
* The LieGroup class provides an encapsulation for Lie groups.
* Elements are represented by complex matrices, and are limited
* to being near the identity.
* @jsci.planetmath LieGroup
* @version 1.3
* @author Mark Hale
*/
public class LieGroup extends Object {
        private AbstractComplexSquareMatrix generators[];
        private AbstractComplexSquareMatrix identityMatrix;
        /**
        * Constructs a Lie group from a Lie algebra.
        * @param gens the group generators
        */
        public LieGroup(AbstractComplexSquareMatrix gens[]) {
                generators=gens;
                identityMatrix=ComplexDiagonalMatrix.identity(generators[0].rows());
        }
        /**
        * Returns the dimension of the group.
        */
        public final int dimension() {
                return generators.length;
        }
        /**
        * Returns an element near the identity.
        * @param v a small element from the Lie algebra
        */
        public AbstractComplexSquareMatrix getElement(AbstractDoubleVector v) {
                if(generators.length!=v.dimension())
                        throw new IllegalArgumentException("The vector should match the generators.");
                AbstractComplexMatrix phase=generators[0].scalarMultiply(v.getComponent(0));
                for(int i=1;i<generators.length;i++)
                        phase=phase.add(generators[i].scalarMultiply(v.getComponent(i)));
                return (AbstractComplexSquareMatrix)identityMatrix.add(phase.scalarMultiply(Complex.I));
        }
        /**
        * Returns the identity element.
        */
        public AbstractComplexSquareMatrix identity() {
                return identityMatrix;
        }
        /**
        * Returns true if the element is the identity element of this group.
        * @param a a group element
        */
        public final boolean isIdentity(AbstractComplexSquareMatrix a) {
                return identityMatrix.equals(a);
        }
        /**
        * Returns true if one element is the inverse of the other.
        * @param a a group element
        * @param b a group element
        */
        public final boolean isInverse(AbstractComplexSquareMatrix a,AbstractComplexSquareMatrix b) {
                return isIdentity(a.multiply(b));
        }
}

