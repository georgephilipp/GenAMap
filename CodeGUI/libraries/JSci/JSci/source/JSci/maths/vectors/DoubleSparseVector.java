package JSci.maths.vectors;

import JSci.GlobalSettings;
import JSci.maths.MathDouble;
import JSci.maths.MathInteger;
import JSci.maths.Mapping;
import JSci.maths.matrices.DoubleSparseMatrix;
import JSci.maths.groups.AbelianGroup;
import JSci.maths.algebras.Module;
import JSci.maths.algebras.VectorSpace;
import JSci.maths.fields.Ring;
import JSci.maths.fields.Field;

/**
* The DoubleSparseVector class encapsulates sparse vectors.
* Uses Morse-coding.
* @author Daniel Lemire
* @author Alain Beliveau
*/
public final class DoubleSparseVector extends AbstractDoubleVector {
        private double vector[];
        /**
        * Sparse indexing data.
        * Contains the component positions of each element,
        * e.g. <code>pos[n]</code> is the component position
        * of the <code>n</code>th element
        * (the <code>pos[n]</code>th component is stored at index <code>n</code>).
        */
        private int pos[];
        private int count = 0;
        /**
         * Amount by which to increase the capacity.
         */
        private int capacityIncrement = 1;
        /**
        * Constructs an empty vector.
        * @param dim the dimension of the vector.
        */
        public DoubleSparseVector(final int dim) {
                super(dim);
                vector=new double[0];
                pos=new int[0];
        }
        public DoubleSparseVector(final int dim, int capacityIncrement) {
            this(dim);
            this.capacityIncrement = capacityIncrement;
        }
        /**
        * Constructs a vector from an array.
        */
        public DoubleSparseVector(double array[]) {
                super(array.length);
                int n=0;
                for(int i=0;i<N;i++) {
                        if(Math.abs(array[i])>GlobalSettings.ZERO_TOL)
                                n++;
                }
                count = n;
                vector=new double[n];
                pos=new int[n];
                n=0;
                for(int i=0;i<N;i++) {
                        if(Math.abs(array[i])>GlobalSettings.ZERO_TOL) {
                                vector[n]=array[i];
                                pos[n]=i;
                                n++;
                        }
                }
        }
        /**
        * Compares two vectors for equality.
        * @param obj a double sparse vector
        */
	public boolean equals(Object obj, double tol) {
                if(obj!=null && (obj instanceof DoubleSparseVector) && N==((DoubleSparseVector)obj).N) {
                        DoubleSparseVector v=(DoubleSparseVector)obj;
                        double sumSqr = 0.0;
                        for(int i=0; i<count; i++) {
                                if(pos[i] != v.pos[i])
                                        return false;
                                double delta = vector[i]-v.vector[i];
                                sumSqr += delta*delta;
                        }
                        return (sumSqr <= tol*tol);
                } else
                        return false;
        }
        /**
        * Returns a component of this vector.
        * @param n index of the vector component
        * @exception VectorDimensionException If attempting to access an invalid component.
        */
        public double getComponent(int n) {
                if(n<0 || n>=N)
                        throw new VectorDimensionException(getInvalidComponentMsg(n));
                int k = 0;
                while(k<count && pos[k]<n) {
                    k++;
                }
                if(k<count && pos[k]==n)
                    return vector[k];
                else
                    return 0.0;
        }
        /**
        * Sets the value of a component of this vector.
        * @param n index of the vector component
        * @param x a number
        * @exception VectorDimensionException If attempting to access an invalid component.
        */
        public void setComponent(int n,double x) {
                if(n<0 || n>=N)
                        throw new VectorDimensionException(getInvalidComponentMsg(n));
                int k = 0;
                while(k<count && pos[k]<n) {
                    k++;
                }
                if(k<count && pos[k]==n) {
                    if(Math.abs(x)<=GlobalSettings.ZERO_TOL) {
                        // shrink if zero
                        System.arraycopy(vector,k+1,vector,k,count-k-1);
                        System.arraycopy(pos,k+1,pos,k,count-k-1);
                        count--;
                    } else {
                        // overwrite
                        vector[k] = x;
                    }
                } else if(Math.abs(x) > GlobalSettings.ZERO_TOL) {
                    // expand
                    if(count == vector.length) {
                        // increase capacity
                        double oldVector[] = vector;
                        int oldPos[] = pos;
                        vector=new double[oldVector.length+capacityIncrement];
                        pos=new int[oldPos.length+capacityIncrement];
                        System.arraycopy(oldVector,0,vector,0,k);
                        System.arraycopy(oldPos,0,pos,0,k);
                        System.arraycopy(oldVector,k,vector,k+1,oldVector.length-k);
                        System.arraycopy(oldPos,k,pos,k+1,oldPos.length-k);
                    } else {
                        System.arraycopy(vector,k,vector,k+1,count-k);
                        System.arraycopy(pos,k,pos,k+1,count-k);
                    }
                    vector[k] = x;
                    pos[k] = n;
                    count++;
                }
        }
        /**
         * Returns the number of non-zero components.
         */
        public int componentCount() {
            return count;
        }
        /**
        * Returns the l<sup>2</sup>-norm (magnitude).
        */
        public double norm() {
                return Math.sqrt(sumSquares());
        }
        /**
        * Returns the sum of the squares of the components.
        */
        public double sumSquares() {
                double norm=0.0;
  		for(int k=0;k<count;k++)
  			norm+=vector[k]*vector[k];
  		return norm;
        }
        /**
        * Returns the mass.
        */
        public double mass() {
                double mass=0.0;
  		for(int k=0;k<count;k++)
  			mass+=vector[k];
  		return mass;
        }

//============
// OPERATIONS
//============

        /**
        * Returns the negative of this vector.
        */
        public AbelianGroup.Member negate() {
                final DoubleSparseVector ans = new DoubleSparseVector(N);
                ans.count = count;
                ans.vector = new double[count];
                ans.pos = new int[count];
                System.arraycopy(pos, 0, ans.pos, 0, count);
                for(int i=0; i<count; i++)
                        ans.vector[i] =- vector[i];
                return ans;
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
        * @param v a double vector
        * @exception VectorDimensionException If the vectors are different sizes.
        */
        public AbstractDoubleVector add(final AbstractDoubleVector v) {
                if(v instanceof DoubleSparseVector)
                        return add((DoubleSparseVector)v);
                else if(v instanceof DoubleVector)
                        return add((DoubleVector)v);
                else {
                        if(N!=v.N)
                                throw new VectorDimensionException("Vectors are different sizes.");
                        double array[]=new double[N];
                        array[0]=v.getComponent(0);
                        for(int i=1;i<N;i++)
                                array[i]=v.getComponent(i);
                        for(int i=0;i<count;i++)
                                array[pos[i]]+=vector[i];
                        return new DoubleVector(array);
                }
        }
        public DoubleVector add(final DoubleVector v) {
                if(N!=v.N)
                        throw new VectorDimensionException("Vectors are different sizes.");
                double array[]=new double[N];
                System.arraycopy(v.vector,0,array,0,N);
                for(int i=0;i<count;i++)
                        array[pos[i]]+=vector[i];
                return new DoubleVector(array);
        }
        /**
        * Returns the addition of this vector and another.
        * @param v a double sparse vector
        * @exception VectorDimensionException If the vectors are different sizes.
        */
        public DoubleSparseVector add(final DoubleSparseVector v) {
                if(N!=v.N)
                        throw new VectorDimensionException("Vectors are different sizes.");
                double array[]=new double[N];
                for(int i=0;i<count;i++)
                        array[pos[i]]=vector[i]+v.getComponent(pos[i]);
                for(int m,i=0;i<v.count;i++) {
                        m=v.pos[i];
                        array[m]=getComponent(m)+v.vector[i];
                }
                return new DoubleSparseVector(array);
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
        * @param v a double vector
        * @exception VectorDimensionException If the vectors are different sizes.
        */
        public AbstractDoubleVector subtract(final AbstractDoubleVector v) {
                if(v instanceof DoubleSparseVector)
                        return subtract((DoubleSparseVector)v);
                else if(v instanceof DoubleVector)
                        return subtract((DoubleVector)v);
                else {
                        if(N!=v.N)
                                throw new VectorDimensionException("Vectors are different sizes.");
                        double array[]=new double[N];
                        array[0]=-v.getComponent(0);
                        for(int i=1;i<N;i++)
                                array[i]=-v.getComponent(i);
                        for(int i=0;i<count;i++)
                                array[pos[i]]+=vector[i];
                        return new DoubleVector(array);
                }
        }
        public DoubleVector subtract(final DoubleVector v) {
                if(N!=v.N)
                        throw new VectorDimensionException("Vectors are different sizes.");
                double array[]=new double[N];
                array[0]=-v.vector[0];
                for(int i=1;i<N;i++)
                        array[i]=-v.vector[i];
                for(int i=0;i<count;i++)
                        array[pos[i]]+=vector[i];
                return new DoubleVector(array);
        }
        /**
        * Returns the subtraction of this vector by another.
        * @param v a double sparse vector
        * @exception VectorDimensionException If the vectors are different sizes.
        */
        public DoubleSparseVector subtract(final DoubleSparseVector v) {
                if(N!=v.N)
                        throw new VectorDimensionException("Vectors are different sizes.");
                double array[]=new double[N];
                for(int i=0;i<count;i++)
                        array[pos[i]]=vector[i]-v.getComponent(pos[i]);
                for(int m,i=0;i<v.count;i++) {
                        m=v.pos[i];
                        array[m]=getComponent(m)-v.vector[i];
                }
                return new DoubleSparseVector(array);
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
        * @param x a double
        */
        public AbstractDoubleVector scalarMultiply(final double x) {
                final DoubleSparseVector ans=new DoubleSparseVector(N);
                ans.count = count;
                ans.vector=new double[count];
                ans.pos=new int[count];
                System.arraycopy(pos,0,ans.pos,0,count);
                for(int i=0;i<count;i++)
                        ans.vector[i]=x*vector[i];
                return ans;
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
        * @param x a double
        * @exception ArithmeticException If divide by zero.
        */
        public AbstractDoubleVector scalarDivide(final double x) {
                final DoubleSparseVector ans=new DoubleSparseVector(N);
                ans.count = count;
                ans.vector=new double[count];
                ans.pos=new int[count];
                System.arraycopy(pos,0,ans.pos,0,count);
                for(int i=0;i<count;i++)
                        ans.vector[i]=vector[i]/x;
                return ans;
        }

// SCALAR PRODUCT

        /**
        * Returns the scalar product of this vector and another.
        * @param v a double vector
        * @exception VectorDimensionException If the vectors are different sizes.
        */
        public double scalarProduct(final AbstractDoubleVector v) {
                if(v instanceof DoubleSparseVector)
                        return scalarProduct((DoubleSparseVector)v);
                else if(v instanceof DoubleVector)
                        return scalarProduct((DoubleVector)v);
                else {
                        if(N!=v.N)
                                throw new VectorDimensionException("Vectors are different sizes.");
                        double ps=0.0;
                        for(int i=0;i<count;i++)
                                ps+=vector[i]*v.getComponent(pos[i]);
                        return ps;
                }
        }
        public double scalarProduct(final DoubleVector v) {
                if(N!=v.N)
                        throw new VectorDimensionException("Vectors are different sizes.");
                double ps=0.0;
                for(int i=0;i<count;i++)
                        ps+=vector[i]*v.vector[pos[i]];
                return ps;
        }
        /**
        * Returns the scalar product of this vector and another.
        * @param v a double sparse vector
        * @exception VectorDimensionException If the vectors are different sizes.
        */
        public double scalarProduct(final DoubleSparseVector v) {
                if(N!=v.N)
                        throw new VectorDimensionException("Vectors are different sizes.");
                double ps=0.0;
                if(count<=v.count) {
                        for(int i=0;i<count;i++)
                                ps+=vector[i]*v.getComponent(pos[i]);
                } else {
                        for(int i=0;i<v.count;i++)
                                ps+=getComponent(v.pos[i])*v.vector[i];
                }
                return ps;
        }

// TENSOR PRODUCT

        /**
        * Returns the tensor product of this vector and another.
        */
        public DoubleSparseMatrix tensorProduct(final DoubleSparseVector v) {
                DoubleSparseMatrix ans=new DoubleSparseMatrix(N,v.N);
                for(int j,i=0;i<count;i++) {
                        for(j=0;j<v.count;j++)
                                ans.setElement(pos[i],v.pos[j],vector[i]*v.vector[j]);
                }
                return ans;
        }

// MAP COMPONENTS

        /**
        * Applies a function on all the vector components.
        * @param f a user-defined function
        * @return a double sparse vector
        */
        public AbstractDoubleVector mapComponents(final Mapping f) {
		double zeroValue = f.map(0.0);
		if(Math.abs(zeroValue) <= GlobalSettings.ZERO_TOL)
			return sparseMap(f);
		else
			return generalMap(f, zeroValue);
	}
	private AbstractDoubleVector sparseMap(Mapping f) {
                final DoubleSparseVector ans=new DoubleSparseVector(N);
                ans.count = count;
                ans.vector=new double[count];
                ans.pos=new int[count];
                System.arraycopy(pos,0,ans.pos,0,count);
                for(int i=0;i<count;i++)
                        ans.vector[i]=f.map(vector[i]);
                return ans;
        }
	private AbstractDoubleVector generalMap(Mapping f, double zeroValue) {
		double[] array = new double[N];
		for(int i=0; i<N; i++)
			array[i] = zeroValue;
                for(int i=0;i<count;i++)
                        array[i] = f.map(vector[pos[i]]);
                return new DoubleVector(array);
        }
}
