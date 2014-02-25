package JSci.maths.vectors;

import JSci.maths.ExtraMath;
import JSci.maths.MathDouble;
import JSci.maths.MathInteger;
import JSci.maths.Mapping;
import JSci.maths.algebras.Module;
import JSci.maths.algebras.VectorSpace;
import JSci.maths.fields.Ring;
import JSci.maths.fields.Field;
import JSci.maths.groups.AbelianGroup;

/**
* An array-based implementation of a double vector.
* @version 2.1
* @author Mark Hale
*/
public class DoubleVector extends AbstractDoubleVector {
        /**
        * Array containing the components of the vector.
        */
        protected double vector[];
        /**
        * Constructs an empty vector.
        * @param dim the dimension of the vector.
        */
        public DoubleVector(final int dim) {
                super(dim);
                vector=new double[dim];
        }
        /**
        * Constructs a vector by wrapping an array.
        * @param array an assigned value.
        */
        public DoubleVector(final double array[]) {
                super(array.length);
                vector=array;
        }
        /**
        * Compares two double vectors for equality.
        * @param a a double vector.
        */
        public boolean equals(Object a, double tol) {
                if(a!=null && (a instanceof AbstractDoubleVector) && N==((AbstractDoubleVector)a).N) {
                        final AbstractDoubleVector dv=(AbstractDoubleVector)a;
                        double sumSqr = 0.0;
                        for(int i=0; i<N; i++) {
                                double delta = vector[i] - dv.getComponent(i);
                                sumSqr += delta*delta;
                        }
                        return (sumSqr <= tol*tol);
                } else
                        return false;
        }
        /**
        * Returns a comma delimited string representing the value of this vector.
        */
        public String toString() {
                final StringBuffer buf=new StringBuffer(8*N);
                int i;
                for(i=0;i<N-1;i++) {
                        buf.append(vector[i]);
                        buf.append(',');
                }
                buf.append(vector[i]);
                return buf.toString();
        }
        /**
        * Converts this vector to an integer vector.
        * @return an integer vector.
        */
        public AbstractIntegerVector toIntegerVector() {
                final int array[]=new int[N];
                for(int i=0;i<N;i++)
                        array[i]=Math.round((float)vector[i]);
                return new IntegerVector(array);
        }
        /**
        * Converts this vector to a complex vector.
        * @return a complex vector.
        */
        public AbstractComplexVector toComplexVector() {
                return new ComplexVector(vector,new double[N]);
        }
        /**
        * Returns a component of this vector.
        * @param n index of the vector component.
        * @exception VectorDimensionException If attempting to access an invalid component.
        */
        public double getComponent(final int n) {
                if(n>=0 && n<N)
                        return vector[n];
                else
                        throw new VectorDimensionException(getInvalidComponentMsg(n));
        }
        /**
        * Sets the value of a component of this vector.
        * @param n index of the vector component.
        * @param x a number.
        * @exception VectorDimensionException If attempting to access an invalid component.
        */
        public void setComponent(final int n, final double x) {
                if(n>=0 && n<N)
                        vector[n]=x;
                else
                        throw new VectorDimensionException(getInvalidComponentMsg(n));
        }
        /**
        * Returns the l<sup>n</sup>-norm.
        * @jsci.planetmath VectorPnorm
        */
        public double norm(int n) {
                double answer=Math.pow(Math.abs(vector[0]), n);
                for(int i=1; i<N; i++)
                        answer+=Math.pow(Math.abs(vector[i]), n);
                return Math.pow(answer, 1.0/n);
        }
        /**
        * Returns the l<sup>2</sup>-norm (magnitude).
        * @jsci.planetmath VectorPnorm
        */
        public double norm() {
                double answer=vector[0];
                for(int i=1;i<N;i++)
                        answer=ExtraMath.hypot(answer,vector[i]);
                return answer;
        }
        /**
        * Returns the l<sup><img border=0 alt="infinity" src="doc-files/infinity.gif"></sup>-norm.
        * @author Taber Smith
        * @jsci.planetmath VectorPnorm
        */
        public double infNorm() {
                double infNorm = Math.abs(vector[0]);
                for(int i=1; i<N; i++) {
                        final double abs = Math.abs(vector[i]);
                        if(abs > infNorm)
                                infNorm = abs;
                }
                return infNorm;
        }

//============
// OPERATIONS
//============

        /**
        * Returns the negative of this vector.
        */
        public AbelianGroup.Member negate() {
                final double array[]=new double[N];
                array[0]=-vector[0];
                for(int i=1;i<N;i++)
                        array[i]=-vector[i];
                return new DoubleVector(array);
        }

// ADDITION

        /**
        * Returns the addition of this vector and another.
        */
        public AbelianGroup.Member add(final AbelianGroup.Member v) {
                if(v instanceof AbstractDoubleVector)
                        return add((AbstractDoubleVector)v);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the addition of this vector and another.
        * @param v a double vector.
        * @exception VectorDimensionException If the vectors are different sizes.
        */
        public AbstractDoubleVector add(AbstractDoubleVector v) {
                if(v instanceof DoubleVector)
                        return add((DoubleVector)v);
                else {
                        if(N==v.N) {
                                final double array[]=new double[N];
                                array[0]=vector[0]+v.getComponent(0);
                                for(int i=1;i<N;i++)
                                        array[i]=vector[i]+v.getComponent(i);
                                return new DoubleVector(array);
                        } else
                                throw new VectorDimensionException("Vectors are different sizes.");
                }
        }
        public DoubleVector add(final DoubleVector v) {
                if(N==v.N) {
                        final double array[]=new double[N];
                        array[0]=vector[0]+v.vector[0];
                        for(int i=1;i<N;i++)
                                array[i]=vector[i]+v.vector[i];
                        return new DoubleVector(array);
                } else
                        throw new VectorDimensionException("Vectors are different sizes.");
        }

// SUBTRACTION

        /**
        * Returns the subtraction of this vector by another.
        */
        public AbelianGroup.Member subtract(final AbelianGroup.Member v) {
                if(v instanceof AbstractDoubleVector)
                        return subtract((AbstractDoubleVector)v);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the subtraction of this vector by another.
        * @param v a double vector.
        * @exception VectorDimensionException If the vectors are different sizes.
        */
        public AbstractDoubleVector subtract(final AbstractDoubleVector v) {
                if(v instanceof DoubleVector)
                        return subtract((DoubleVector)v);
                else {
                        if(N==v.N) {
                                final double array[]=new double[N];
                                array[0]=vector[0]-v.getComponent(0);
                                for(int i=1;i<N;i++)
                                        array[i]=vector[i]-v.getComponent(i);
                                return new DoubleVector(array);
                        } else
                                throw new VectorDimensionException("Vectors are different sizes.");
                }
        }
        public DoubleVector subtract(final DoubleVector v) {
                if(N==v.N) {
                        final double array[]=new double[N];
                        array[0]=vector[0]-v.vector[0];
                        for(int i=1;i<N;i++)
                                array[i]=vector[i]-v.vector[i];
                        return new DoubleVector(array);
                } else
                        throw new VectorDimensionException("Vectors are different sizes.");
        }

// SCALAR MULTIPLICATION

        /**
        * Returns the multiplication of this vector by a scalar.
        */
        public Module.Member scalarMultiply(Ring.Member x) {
                if(x instanceof MathDouble)
                        return scalarMultiply(((MathDouble)x).value());
                else if(x instanceof MathInteger)
                        return scalarMultiply(((MathInteger)x).value());
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the multiplication of this vector by a scalar.
        * @param x a double.
        */
        public AbstractDoubleVector scalarMultiply(final double x) {
                final double array[]=new double[N];
                array[0]=x*vector[0];
                for(int i=1;i<N;i++)
                        array[i]=x*vector[i];
                return new DoubleVector(array);
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
        * @param x a double.
        * @exception ArithmeticException If divide by zero.
        */
        public AbstractDoubleVector scalarDivide(final double x) {
                final double array[]=new double[N];
                array[0]=vector[0]/x;
                for(int i=1;i<N;i++)
                        array[i]=vector[i]/x;
                return new DoubleVector(array);
        }

// SCALAR PRODUCT

        /**
        * Returns the scalar product of this vector and another.
        * @param v a double vector.
        * @exception VectorDimensionException If the vectors are different sizes.
        */
        public double scalarProduct(final AbstractDoubleVector v) {
                if(v instanceof DoubleVector)
                        return scalarProduct((DoubleVector)v);
                else {
                        if(N==v.N) {
                                double answer=vector[0]*v.getComponent(0);
                                for(int i=1;i<N;i++)
                                        answer+=vector[i]*v.getComponent(i);
                                return answer;
                        } else
                                throw new VectorDimensionException("Vectors are different sizes.");
                }
        }
        public double scalarProduct(final DoubleVector v) {
                if(N==v.N) {
                        double answer=vector[0]*v.vector[0];
                        for(int i=1;i<N;i++)
                                answer+=vector[i]*v.vector[i];
                        return answer;
                } else
                        throw new VectorDimensionException("Vectors are different sizes.");
        }

// MAP COMPONENTS

        /**
        * Applies a function on all the vector components.
        * @param f a user-defined function.
        * @return a double vector.
        */
        public AbstractDoubleVector mapComponents(final Mapping f) {
                final double array[]=new double[N];
                array[0]=f.map(vector[0]);
                for(int i=1;i<N;i++)
                        array[i]=f.map(vector[i]);
                return new DoubleVector(array);
        }
}

