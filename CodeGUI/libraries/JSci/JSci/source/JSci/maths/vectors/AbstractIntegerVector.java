package JSci.maths.vectors;

import JSci.maths.ExtraMath;
import JSci.maths.groups.AbelianGroup;
import JSci.maths.fields.Ring;
import JSci.maths.algebras.Module;

/**
* The AbstractIntegerVector class encapsulates vectors containing integers.
* @version 1.0
* @author Mark Hale
*/
public abstract class AbstractIntegerVector extends MathVector {
        protected AbstractIntegerVector(final int dim) {
                super(dim);
        }
        /**
        * Compares two integer vectors for equality.
        * Two vectors are considered to be equal if the norm of their difference is zero.
        * @param obj an integer vector
        */
        public boolean equals(Object obj) {
                if(obj != null && (obj instanceof AbstractIntegerVector)) {
                        final AbstractIntegerVector vec = (AbstractIntegerVector) obj;
                        return (this.dimension() == vec.dimension() && this.subtract(vec).norm() == 0.0);
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
        * Converts this vector to a double vector.
        * @return a double vector
        */
        public AbstractDoubleVector toDoubleVector() {
                final double array[]=new double[N];
                for(int i=0;i<N;i++)
                        array[i]=getComponent(i);
                return new DoubleVector(array);
        }
        /**
        * Returns a component of this vector.
        * @param n index of the vector component
        * @exception VectorDimensionException If attempting to access an invalid component.
        */
        public abstract int getComponent(int n);
        /**
        * Sets the value of a component of this vector.
        * @param n index of the vector component
        * @param x an integer
        * @exception VectorDimensionException If attempting to access an invalid component.
        */
        public abstract void setComponent(int n, int x);
	public Object getSet() {
		throw new RuntimeException("Not implemented: file bug");
	}
        /**
        * Returns the l<sup>n</sup>-norm.
        */
        public double norm(final int n) {
                double answer = Math.pow(Math.abs(getComponent(0)), n);
                for(int i=1; i<N; i++)
                        answer += Math.pow(Math.abs(getComponent(i)), n);
                return Math.pow(answer, 1.0/n);
        }
        /**
        * Returns the l<sup>2</sup>-norm (magnitude).
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
        */
        public double infNorm() {
                int infNorm = Math.abs(getComponent(0));
                for(int i=1; i<N; i++) {
                        final int abs = Math.abs(getComponent(i));
                        if(abs > infNorm)
                                infNorm = abs;
                }
                return infNorm;
        }

//============
// OPERATIONS
//============

        /**
        * Returns the addition of this vector and another.
        * @param v an integer vector
        * @exception VectorDimensionException If the vectors are different sizes.
        */
        public abstract AbstractIntegerVector add(AbstractIntegerVector v);
        /**
        * Returns the subtraction of this vector by another.
        * @param v an integer vector
        * @exception VectorDimensionException If the vectors are different sizes.
        */
        public abstract AbstractIntegerVector subtract(AbstractIntegerVector v);
        /**
        * Returns the multiplication of this vector by a scalar.
        * @param x an integer
        */
        public abstract AbstractIntegerVector scalarMultiply(int x);
        /**
        * Returns the scalar product of this vector and another.
        * @param v an integer vector
        * @exception VectorDimensionException If the vectors are different sizes.
        */
        public abstract int scalarProduct(AbstractIntegerVector v);
}

