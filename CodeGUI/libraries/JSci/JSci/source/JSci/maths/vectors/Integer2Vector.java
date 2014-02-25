package JSci.maths.vectors;

import JSci.maths.Mapping;
import JSci.maths.MathInteger;
import JSci.maths.MathDouble;
import JSci.maths.groups.AbelianGroup;
import JSci.maths.algebras.Module;
import JSci.maths.fields.Ring;

/**
* An optimised implementation of a 2D integer vector.
* @version 2.0
* @author Mark Hale
*/
public final class Integer2Vector extends AbstractIntegerVector {
        protected int x;
        protected int y;
        /**
        * Constructs an empty 2-vector.
        */
        public Integer2Vector() {
                super(2);
        }
        /**
        * Constructs a 2-vector.
        * @param x x coordinate.
        * @param y y coordinate.
        */
        public Integer2Vector(final int x, final int y) {
                this();
                this.x = x;
                this.y = y;
        }
        /**
        * Constructs a 2-vector.
        */
        public Integer2Vector(int[] array) {
                this();
                x = array[0];
                y = array[1];
        }
        /**
        * Compares two integer vectors for equality.
        * @param obj a integer 2-vector
        */
	public boolean equals(Object obj, double tol) {
                if(obj != null && (obj instanceof Integer2Vector)) {
                        final Integer2Vector vec = (Integer2Vector) obj;
                        int dx = x - vec.x;
                        int dy = y - vec.y;
                        return (dx*dx
                         + dy*dy == 0);
                } else
                        return false;
        }
        /**
        * Returns a comma delimited string representing the value of this vector.
        */
        public String toString() {
                final StringBuffer buf = new StringBuffer(15);
                buf.append(x).append(',').append(y);
                return buf.toString();
        }
        /**
        * Converts this 2-vector to a double 2-vector.
        * @return a double 2-vector
        */
        public AbstractDoubleVector toDoubleVector() {
                return new Double2Vector(
                        x,
                        y
                );
        }
        /**
        * Converts this 2-vector to a complex 2-vector.
        * @return a complex 2-vector
        */
        public AbstractComplexVector toComplexVector() {
                return new Complex2Vector(
                        x, 0.0,
                        y, 0.0
                );
        }
        /**
        * Returns a component of this vector.
        * @param n index of the vector component
        * @exception VectorDimensionException If attempting to access an invalid component.
        */
        public int getComponent(final int n) {
                switch(n) {
                        case 0 : return x;
                        case 1 : return y;
                        default : throw new VectorDimensionException("Invalid component.");
                }
        }
        /**
        * Sets the value of a component of this vector.
        * Should only be used to initialise this vector.
        * @param n index of the vector component
        * @param value a number
        * @exception VectorDimensionException If attempting to access an invalid component.
        */
        public void setComponent(final int n, final int value) {
                switch(n) {
                        case 0 : x = value; break;
                        case 1 : y = value; break;
                        default : throw new VectorDimensionException("Invalid component.");
                }
        }
        /**
        * Returns the l<sup>n</sup>-norm.
        */
        public double norm(final int n) {
                final double answer = Math.pow(Math.abs(x), n)
                        +Math.pow(Math.abs(y), n);
                return Math.pow(answer, 1.0/n);
        }
        /**
        * Returns the l<sup>2</sup>-norm (magnitude).
        */
        public double norm() {
                return Math.sqrt(
                        x*x
                        +y*y
                );
        }
        /**
        * Returns the l<sup><img border=0 alt="infinity" src="doc-files/infinity.gif"></sup>-norm.
        * @author Taber Smith
        */
        public double infNorm() {
                int infNorm = 0;
                int abs;
                abs = Math.abs(x);
                if(abs > infNorm)
                        infNorm = abs;
                abs = Math.abs(y);
                if(abs > infNorm)
                        infNorm = abs;
                return infNorm;
        }

//============
// OPERATIONS
//============

        /**
        * Returns the negative of this vector.
        */
        public AbelianGroup.Member negate() {
                return new Integer2Vector(
                        -x,
                        -y
                );
        }

// ADDITION

        /**
        * Returns the addition of this vector and another.
        */
        public AbelianGroup.Member add(final AbelianGroup.Member vec) {
                if(vec instanceof AbstractIntegerVector)
                        return add((AbstractIntegerVector)vec);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the addition of this vector and another.
        * @param vec a integer 2-vector
        */
        public AbstractIntegerVector add(final AbstractIntegerVector vec) {
                if(vec.N == 2) {
                        return new Integer2Vector(
                                x+vec.getComponent(0),
                                y+vec.getComponent(1)
                        );
                } else
                        throw new VectorDimensionException("Vectors are different sizes.");
        }

// SUBTRACTION

        /**
        * Returns the subtraction of this vector by another.
        */
        public AbelianGroup.Member subtract(final AbelianGroup.Member vec) {
                if(vec instanceof AbstractIntegerVector)
                        return subtract((AbstractIntegerVector)vec);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the subtraction of this vector by another.
        * @param vec a integer 2-vector
        */
        public AbstractIntegerVector subtract(final AbstractIntegerVector vec) {
                if(vec.N == 2) {
                        return new Integer2Vector(
                                x-vec.getComponent(0),
                                y-vec.getComponent(1)
                        );
                } else
                        throw new VectorDimensionException("Vectors are different sizes.");
        }

// SCALAR MULTIPLICATION

        /**
        * Returns the multiplication of this vector by a scalar.
        */
        public Module.Member scalarMultiply(Ring.Member x) {
                if(x instanceof MathInteger)
                        return scalarMultiply(((MathInteger)x).value());
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the multiplication of this vector by a scalar.
        * @param k a integer
        * @return a integer 2-vector
        */
        public AbstractIntegerVector scalarMultiply(final int k) {
                return new Integer2Vector(
                        k*x,
                        k*y
                );
        }


// SCALAR PRODUCT

        /**
        * Returns the scalar product of this vector and another.
        * @param vec a integer 2-vector
        */
        public int scalarProduct(final AbstractIntegerVector vec) {
                if(vec.N == 2) {
                        return x*vec.getComponent(0)
                                +y*vec.getComponent(1);
                } else
                        throw new VectorDimensionException("Vectors are different sizes.");
        }


}

