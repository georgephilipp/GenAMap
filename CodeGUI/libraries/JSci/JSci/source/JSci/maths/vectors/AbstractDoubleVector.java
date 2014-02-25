package JSci.maths.vectors;

import JSci.GlobalSettings;
import JSci.maths.ExtraMath;
import JSci.maths.Mapping;
import JSci.maths.algebras.*;
import JSci.maths.fields.*;
import JSci.maths.groups.AbelianGroup;

/**
* The AbstractDoubleVector class encapsulates vectors containing doubles.
* @version 1.0
* @author Mark Hale
*/
public abstract class AbstractDoubleVector extends MathVector implements BanachSpace.Member {
        protected AbstractDoubleVector(final int dim) {
                super(dim);
        }
        /**
        * Compares two double vectors for equality.
        * Two vectors are considered to be equal if the norm of their difference is within the zero tolerance.
        * @param obj a double vector
        */
        public final boolean equals(Object obj) {
		return equals(obj, GlobalSettings.ZERO_TOL);
        }
	public boolean equals(Object obj, double tol) {
                if(obj != null && (obj instanceof AbstractDoubleVector)) {
                        final AbstractDoubleVector vec = (AbstractDoubleVector) obj;
                        return (this.dimension() == vec.dimension() && this.subtract(vec).norm() <= tol);
                } else
                        return false;
        }
        /**
        * Returns a comma delimited string representing the value of this vector.
        */
        public String toString() {
                final StringBuffer buf = new StringBuffer(8*N);
                int i;
                for(i=0; i<N-1; i++) {
                        buf.append(getComponent(i));
                        buf.append(',');
                }
                buf.append(getComponent(i));
                return buf.toString();
        }
        /**
        * Returns a hashcode for this vector.
        */
        public int hashCode() {
                return (int)Math.exp(norm());
        }
        /**
        * Returns a component of this vector.
        * @param n index of the vector component.
        * @exception VectorDimensionException If attempting to access an invalid component.
        */
        public abstract double getComponent(int n);
        /**
        * Sets the value of a component of this vector.
        * @param n index of the vector component.
        * @param x a number.
        * @exception VectorDimensionException If attempting to access an invalid component.
        */
        public abstract void setComponent(int n, double x);
	public Object getSet() {
		throw new RuntimeException("Not implemented: file bug");
	}
        /**
        * Returns the l<sup>n</sup>-norm.
        * @jsci.planetmath VectorPnorm
        */
        public double norm(int n) {
                double answer = Math.pow(Math.abs(getComponent(0)), n);
                for(int i=1; i<N; i++)
                        answer += Math.pow(Math.abs(getComponent(i)), n);
                return Math.pow(answer, 1.0/n);
        }
        /**
        * Returns the l<sup>2</sup>-norm (magnitude).
        * @jsci.planetmath VectorPnorm
        */
        public double norm() {
                double answer = getComponent(0);
                for(int i=1; i<N; i++)
                        answer = ExtraMath.hypot(answer, getComponent(i));
                return answer;
        }
        /**
        * Returns the l<sup><img border=0 alt="infinity" src="doc-files/infinity.gif"></sup>-norm.
        * @author Taber Smith
        * @jsci.planetmath VectorPnorm
        */
        public double infNorm() {
                double infNorm = Math.abs(getComponent(0));
                for(int i=1; i<N; i++) {
                        final double abs = Math.abs(getComponent(i));
                        if(abs > infNorm)
                                infNorm = abs;
                }
                return infNorm;
        }
        /**
        * Returns the mass (l<sup>1</sup>-norm).
        */
        public double mass() {
                double mass=0.0;
                for(int i=1; i<N; i++)
  			mass+=getComponent(i);
  		return mass;
        }

//============
// OPERATIONS
//============

        /**
        * Returns the addition of this vector and another.
        * @param v a double vector.
        * @exception VectorDimensionException If the vectors are different sizes.
        */
        public abstract AbstractDoubleVector add(AbstractDoubleVector v);
        /**
        * Returns the subtraction of this vector by another.
        * @param v a double vector.
        * @exception VectorDimensionException If the vectors are different sizes.
        */
        public abstract AbstractDoubleVector subtract(AbstractDoubleVector v);
        /**
        * Returns the multiplication of this vector by a scalar.
        * @param x a double.
        */
        public abstract AbstractDoubleVector scalarMultiply(double x);
        /**
        * Returns the division of this vector by a scalar.
        * @param x a double.
        * @exception ArithmeticException If divide by zero.
        */
        public abstract AbstractDoubleVector scalarDivide(double x);
        /**
        * Returns a normalised vector (a vector with norm equal to one).
        */
        public AbstractDoubleVector normalize() {
                return this.scalarDivide(norm());
        }
        /**
        * Returns the scalar product of this vector and another.
        * @param v a double vector.
        * @exception VectorDimensionException If the vectors are different sizes.
        */
        public abstract double scalarProduct(AbstractDoubleVector v);
        /**
        * Applies a function on all the vector components.
        * @param f a user-defined function.
        * @return a double vector.
        */
        public abstract AbstractDoubleVector mapComponents(final Mapping f);
}

