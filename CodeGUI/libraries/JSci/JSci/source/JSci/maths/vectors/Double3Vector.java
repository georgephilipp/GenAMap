package JSci.maths.vectors;

import JSci.GlobalSettings;
import JSci.maths.Mapping;
import JSci.maths.MathInteger;
import JSci.maths.MathDouble;
import JSci.maths.groups.AbelianGroup;
import JSci.maths.algebras.Module;
import JSci.maths.fields.Ring;
import JSci.maths.algebras.VectorSpace;
import JSci.maths.fields.Field;

/**
* An optimised implementation of a 3D double vector.
* @version 2.0
* @author Mark Hale
*/
public final class Double3Vector extends AbstractDoubleVector {
        protected double x;
        protected double y;
        protected double z;
        /**
        * Constructs an empty 3-vector.
        */
        public Double3Vector() {
                super(3);
        }
        /**
        * Constructs a 3-vector.
        * @param x x coordinate.
        * @param y y coordinate.
        * @param z z coordinate.
        */
        public Double3Vector(final double x, final double y, final double z) {
                this();
                this.x = x;
                this.y = y;
                this.z = z;
        }
        /**
        * Constructs a 3-vector.
        */
        public Double3Vector(double[] array) {
                this();
                x = array[0];
                y = array[1];
                z = array[2];
        }
        /**
        * Compares two double vectors for equality.
        * @param obj a double 3-vector
        */
	public boolean equals(Object obj, double tol) {
                if(obj != null && (obj instanceof Double3Vector)) {
                        final Double3Vector vec = (Double3Vector) obj;
                        double dx = x - vec.x;
                        double dy = y - vec.y;
                        double dz = z - vec.z;
                        return (dx*dx
                         + dy*dy
                         + dz*dz <= tol*tol);
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
        * Converts this 3-vector to an integer 3-vector.
        * @return an integer 3-vector
        */
        public AbstractIntegerVector toIntegerVector() {
                return new Integer3Vector(
                        Math.round((float)x),
                        Math.round((float)y),
                        Math.round((float)z)
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
        public double getComponent(final int n) {
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
        public void setComponent(final int n, final double value) {
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
                double infNorm = 0;
                double abs;
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
                return new Double3Vector(
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
                if(vec instanceof AbstractDoubleVector)
                        return add((AbstractDoubleVector)vec);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the addition of this vector and another.
        * @param vec a double 3-vector
        */
        public AbstractDoubleVector add(final AbstractDoubleVector vec) {
                if(vec.N == 3) {
                        return new Double3Vector(
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
                if(vec instanceof AbstractDoubleVector)
                        return subtract((AbstractDoubleVector)vec);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the subtraction of this vector by another.
        * @param vec a double 3-vector
        */
        public AbstractDoubleVector subtract(final AbstractDoubleVector vec) {
                if(vec.N == 3) {
                        return new Double3Vector(
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
                else if(x instanceof MathDouble)
                        return scalarMultiply(((MathDouble)x).value());
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the multiplication of this vector by a scalar.
        * @param k a double
        * @return a double 3-vector
        */
        public AbstractDoubleVector scalarMultiply(final double k) {
                return new Double3Vector(
                        k*x,
                        k*y,
                        k*z
                );
        }

// SCALAR DIVISION

        /**
        * Returns the division of this vector by a scalar.
        */
        public VectorSpace.Member scalarDivide(Field.Member x) {
                if(x instanceof MathDouble)
                        return scalarDivide(((MathDouble)x).value());
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the division of this vector by a scalar.
        * @param k a double
        * @return a double 3-vector
        * @exception ArithmeticException If divide by zero.
        */
        public AbstractDoubleVector scalarDivide(final double k) {
                return new Double3Vector(
                        x/k,
                        y/k,
                        z/k
                );
        }

// SCALAR PRODUCT

        /**
        * Returns the scalar product of this vector and another.
        * @param vec a double 3-vector
        */
        public double scalarProduct(final AbstractDoubleVector vec) {
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
        * @param vec a double 3-vector
        */
        public Double3Vector multiply(final Double3Vector vec) {
                return new Double3Vector(
                        y*vec.z - vec.y*z,
                        z*vec.x - vec.z*x,
                        x*vec.y - vec.x*y
                );
        }

// MAP COMPONENTS

        /**
        * Applies a function on all the vector components.
        * @param mapping a user-defined function
        * @return a double 3-vector
        */
        public AbstractDoubleVector mapComponents(final Mapping mapping) {
                return new Double3Vector(
                        mapping.map(x),
                        mapping.map(y),
                        mapping.map(z)
                );
        }
}

