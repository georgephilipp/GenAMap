package JSci.maths.vectors;

import JSci.GlobalSettings;
import JSci.maths.Complex;
import JSci.maths.ComplexMapping;
import JSci.maths.algebras.*;
import JSci.maths.fields.*;
import JSci.maths.groups.AbelianGroup;

/**
* The AbstractComplexVector class encapsulates vectors containing complex numbers.
* @version 1.0
* @author Mark Hale
*/
public abstract class AbstractComplexVector extends MathVector implements HilbertSpace.Member {
        protected AbstractComplexVector(final int dim) {
                super(dim);
        }
        /**
        * Compares two complex vectors for equality.
        * Two vectors are considered to be equal if the norm of their difference is within the zero tolerance.
        * @param obj a complex vector
        */
        public final boolean equals(Object obj) {
		return equals(obj, GlobalSettings.ZERO_TOL);
        }
	public boolean equals(Object obj, double tol) {
                if(obj != null && (obj instanceof AbstractComplexVector)) {
                        final AbstractComplexVector vec = (AbstractComplexVector) obj;
                        return (this.dimension() == vec.dimension() && this.subtract(vec).norm() <= tol);
                } else
                        return false;
        }
        /**
        * Returns a comma delimited string representing the value of this vector.
        */
        public String toString() {
                final StringBuffer buf = new StringBuffer(12*N);
                int i;
                for(i=0; i<N-1; i++) {
                        buf.append(getComponent(i).toString());
                        buf.append(',');
                }
                buf.append(getComponent(i).toString());
                return buf.toString();
        }
        /**
        * Returns a hashcode for this vector.
        */
        public int hashCode() {
                return (int)Math.exp(norm());
        }
        /**
        * Returns the real part of this complex vector.
        */
        public abstract AbstractDoubleVector real();
        /**
        * Returns the imaginary part of this complex vector.
        */
        public abstract AbstractDoubleVector imag();
        /**
        * Returns a component of this vector.
        * @param n index of the vector component
        * @exception VectorDimensionException If attempting to access an invalid component.
        */
        public abstract Complex getComponent(int n);
        public abstract double getRealComponent(int n);
        public abstract double getImagComponent(int n);
        /**
        * Sets the value of a component of this vector.
        * Should only be used to initialise this vector.
        * @param n index of the vector component
        * @param z a complex number
        * @exception VectorDimensionException If attempting to access an invalid component.
        */
        public abstract void setComponent(int n, Complex z);
        /**
        * Sets the value of a component of this vector.
        * Should only be used to initialise this vector.
        * @param n index of the vector component
        * @param x the real part of a complex number
        * @param y the imaginary part of a complex number
        * @exception VectorDimensionException If attempting to access an invalid component.
        */
        public abstract void setComponent(int n, double x, double y);
	public Object getSet() {
		throw new RuntimeException("Not implemented: file bug");
	}
        /**
        * Returns the l<sup>2</sup>-norm (magnitude).
        */
        public double norm() {
                double answer = getRealComponent(0)*getRealComponent(0) + getImagComponent(0)*getImagComponent(0);
                for(int i=1;i<N;i++)
                        answer += getRealComponent(i)*getRealComponent(i) + getImagComponent(i)*getImagComponent(i);
                return Math.sqrt(answer);
        }
        /**
        * Returns the l<sup><img border=0 alt="infinity" src="doc-files/infinity.gif"></sup>-norm.
        */
        public double infNorm() {
                double infNorm = getComponent(0).mod();
                for(int i=1;i<N;i++) {
                        double mod = getComponent(i).mod();
                        if(mod > infNorm)
                                infNorm = mod;
                }
                return infNorm;
        }

//============
// OPERATIONS
//============

        /**
        * Returns the complex conjugate of this vector.
        */
        public abstract AbstractComplexVector conjugate();
        /**
        * Returns the addition of this vector and another.
        * @param v a complex vector
        * @exception VectorDimensionException If the vectors are different sizes.
        */
        public abstract AbstractComplexVector add(AbstractComplexVector v);
        /**
        * Returns the subtraction of this vector by another.
        * @param v a complex vector
        * @exception VectorDimensionException If the vectors are different sizes.
        */
        public abstract AbstractComplexVector subtract(AbstractComplexVector v);
        /**
        * Returns the multiplication of this vector by a scalar.
        * @param z a complex number
        */
        public abstract AbstractComplexVector scalarMultiply(Complex z);
        /**
        * Returns the multiplication of this vector by a scalar.
        * @param x a double
        */
        public abstract AbstractComplexVector scalarMultiply(double x);
        /**
        * Returns the division of this vector by a scalar.
        * @param z a complex number
        * @exception ArithmeticException If divide by zero.
        */
        public abstract AbstractComplexVector scalarDivide(Complex z);
        /**
        * Returns the division of this vector by a scalar.
        * @param x a double
        * @exception ArithmeticException If divide by zero.
        */
        public abstract AbstractComplexVector scalarDivide(double x);
        /**
        * Returns a normalised vector (a vector with norm equal to one).
        */
        public AbstractComplexVector normalize() {
                return this.scalarDivide(norm());
        }
        /**
        * Returns the scalar product of this vector and another.
        * @param v a complex vector
        * @exception VectorDimensionException If the vectors are different sizes.
        */
        public abstract Complex scalarProduct(AbstractComplexVector v);
        /**
        * Applies a function on all the vector components.
        * @param f a user-defined function
        * @return a complex vector
        */
        public abstract AbstractComplexVector mapComponents(ComplexMapping f);
}

