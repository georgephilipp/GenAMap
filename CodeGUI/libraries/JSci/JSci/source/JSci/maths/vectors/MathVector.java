package JSci.maths.vectors;

import JSci.maths.algebras.Module;

/**
* The MathVector superclass provides an abstract encapsulation for vectors.
* @jsci.planetmath Vector
* @version 2.2
* @author Mark Hale
*/
public abstract class MathVector extends Object implements Module.Member {
        /**
        * The vector's dimension.
        */
        protected final int N;
        /**
        * Constructs a mathematical vector.
        * @param n the dimension of the vector.
        */
        public MathVector(int n) {
                N=n;
        }
        /**
        * Returns the norm (magnitude).
        */
        public abstract double norm();
        /**
        * Returns the vector's dimension.
        */
        public final int dimension() {
                return N;
        }
        /**
        * Returns an "invalid component" error message.
        * @param i index of the component
        */
        protected static String getInvalidComponentMsg(int i) {
                return "("+i+") is an invalid component for this vector.";
        }
}

