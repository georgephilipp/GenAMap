package JSci.maths.vectors;

import JSci.maths.Mapping;
import JSci.maths.MathInteger;
import JSci.maths.MathDouble;
import JSci.maths.groups.AbelianGroup;
import JSci.maths.algebras.Module;
import JSci.maths.fields.Ring;

/**
* An optimised implementation of a 3D integer vector.
* @version 2.0
* @author Mark Hale
*/
public final class Integer3Vector extends AbstractIntegerVector {
        protected int x;
        protected int y;
        protected int z;
        /**
        * Constructs an empty 3-vector.
        */
        public Integer3Vector() {
                super(3);
        }
        /**
        * Constructs a 3-vector.
        * @param x x coordinate.
        * @param y y coordinate.
        * @param z z coordinate.
        */
        public Integer3Vector(final int x, final int y, final int z) {
                this();
                this.x = x;
                this.y = y;
                this.z = z;
        }
        /**
        * Constructs a 3-vector.
        */
        public Integer3Vector(int[] array) {
                this();
                x = array[0];
                y = array[1];
                z = array[2];
        }
        /**
        * Compares two integer vectors for equality.
        * @param obj a integer 3-vector
        */
	public boolean equals(Object obj, double tol) {
                if(obj != null && (obj instanceof Integer3Vector)) {
                        final Integer3Vector vec = (Integer3Vector) obj;
                        int dx = x - vec.x;
                        int dy = y - vec.y;
                        int dz = z - vec.z;
                        return (dx*dx
                         + dy*dy
                         + dz*dz == 0);
                } else
                        return false;
        }
        /**
        * Returns a comma delimited string representing the value of this vector.
        */
        public String toString() {
                final StringBuffer buf = new StringBuffer(15);
                buf.append(x).append(',').append(y).append(',').append(z);
                return buf.toString();
        }
        /**
        * Converts this 3-vector to a double 3-vector.
        * @return a double 3-vector
        */
        public AbstractDoubleVector toDoubleVector() {
                return new Double3Vector(
                        x,
                        y,
                        z
                );
        }
        /**
        * Converts this 3-vector to a complex 3-vector.
        * @return a complex 3-vector
        */
        public AbstractComplexVector toComplexVector() {
                return new Complex3Vector(
                        x, 0.0,
                        y, 0.0,
                        z, 0.0
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
                        case 2 : return z;
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
                        case 2 : z = value; break;
                        default : throw new VectorDimensionException("Invalid component.");
                }
        }
        /**
        * Returns the l<sup>n</sup>-norm.
        */
        public double norm(final int n) {
                final double answer = Math.pow(Math.abs(x), n)
                        +Math.pow(Math.abs(y), n)
                        +Math.pow(Math.abs(z), n);
                return Math.pow(answer, 1.0/n);
        }
        /**
        * Returns the l<sup>2</sup>-norm (magnitude).
        */
        public double norm() {
                return Math.sqrt(
                        x*x
                        +y*y
                        +z*z
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
                abs = Math.abs(z);
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
                return new Integer3Vector(
                        -x,
                        -y,
                        -z
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
        * @param vec a integer 3-vector
        */
        public AbstractIntegerVector add(final AbstractIntegerVector vec) {
                if(vec.N == 3) {
                        return new Integer3Vector(
                                x+vec.getComponent(0),
                                y+vec.getComponent(1),
                                z+vec.getComponent(2)
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
        * @param vec a integer 3-vector
        */
        public AbstractIntegerVector subtract(final AbstractIntegerVector vec) {
                if(vec.N == 3) {
                        return new Integer3Vector(
                                x-vec.getComponent(0),
                                y-vec.getComponent(1),
                                z-vec.getComponent(2)
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
        * @return a integer 3-vector
        */
        public AbstractIntegerVector scalarMultiply(final int k) {
                return new Integer3Vector(
                        k*x,
                        k*y,
                        k*z
                );
        }


// SCALAR PRODUCT

        /**
        * Returns the scalar product of this vector and another.
        * @param vec a integer 3-vector
        */
        public int scalarProduct(final AbstractIntegerVector vec) {
                if(vec.N == 3) {
                        return x*vec.getComponent(0)
                                +y*vec.getComponent(1)
                                +z*vec.getComponent(2);
                } else
                        throw new VectorDimensionException("Vectors are different sizes.");
        }

// VECTOR PRODUCT

        /**
        * Returns the vector product of this vector and another (so(3) algebra).
        * @param vec a integer 3-vector
        */
        public Integer3Vector multiply(final Integer3Vector vec) {
                return new Integer3Vector(
                        y*vec.z - vec.y*z,
                        z*vec.x - vec.z*x,
                        x*vec.y - vec.x*y
                );
        }

}

