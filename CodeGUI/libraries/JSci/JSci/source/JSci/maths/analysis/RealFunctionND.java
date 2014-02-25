package JSci.maths.analysis;

import JSci.maths.Mapping;
import JSci.maths.DimensionException;
import JSci.maths.groups.AbelianGroup;
import JSci.maths.fields.Ring;

/**
* This class describes a function on an n-dimensional space.
* @version 1.0
* @author Mark Hale
*/
public abstract class RealFunctionND implements Ring.Member {
	protected final int dim;
	protected RealFunctionND(int dim) {
		this.dim = dim;
	}
        public abstract double map(double[] x);
	/**
	* Returns the dimension of the space this function is on.
	*/
	public final int dimension() {
		return dim;
	}

	public Object getSet() {
		throw new RuntimeException("Not implemented: file bug");
	}

        /**
        * Returns the negative of this matrix.
        */
        public AbelianGroup.Member negate() {
                return new Negation(this);
        }
        private static class Negation extends RealFunctionND {
                private final RealFunctionND f;
                public Negation(RealFunctionND f) {
			super(f.dim);
                        this.f = f;
                }
                public double map(double[] x) {
                        return -f.map(x);
                }
        }

        /**
        * Returns the addition of this function and another.
        */
        public AbelianGroup.Member add(final AbelianGroup.Member f) {
                if(f instanceof RealFunctionND)
                        return add((RealFunctionND)f);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        public RealFunctionND add(RealFunctionND f) {
		if(dim != f.dim)
			throw new DimensionException("Functions have different dimensions.");
                return new Sum(this, f);
        }
        private static class Sum extends RealFunctionND {
                private final RealFunctionND f1, f2;
                public Sum(RealFunctionND f1, RealFunctionND f2) {
			super(f1.dim);
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public double map(double[] x) {
                        return f1.map(x)+f2.map(x);
                }
        }

        /**
        * Returns the subtraction of this function and another.
        */
        public AbelianGroup.Member subtract(final AbelianGroup.Member f) {
                if(f instanceof RealFunctionND)
                        return subtract((RealFunctionND)f);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        public RealFunctionND subtract(RealFunctionND f) {
		if(dim != f.dim)
			throw new DimensionException("Functions have different dimensions.");
                return new Difference(this, f);
        }
        private static class Difference extends RealFunctionND {
                private final RealFunctionND f1, f2;
                public Difference(RealFunctionND f1, RealFunctionND f2) {
			super(f1.dim);
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public double map(double[] x) {
                        return f1.map(x)-f2.map(x);
                }
        }

        /**
        * Returns the multiplication of this function and another.
        */
        public Ring.Member multiply(Ring.Member f) {
                if(f instanceof RealFunctionND)
                        return multiply((RealFunctionND)f);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        public RealFunctionND multiply(RealFunctionND f) {
		if(dim != f.dim)
			throw new DimensionException("Functions have different dimensions.");
                return new Product(this, f);
        }
        private static class Product extends RealFunctionND {
                private final RealFunctionND f1, f2;
                public Product(RealFunctionND f1, RealFunctionND f2) {
			super(f1.dim);
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public double map(double[] x) {
                        return f1.map(x)*f2.map(x);
                }
        }

        /**
        * Returns the multiplicative inverse (reciprocal) of this function.
        */
        public Ring.Member inverse() {
                return new Reciprocal(this);
        }
        private static class Reciprocal extends RealFunctionND {
                private final RealFunctionND f;
                public Reciprocal(RealFunctionND f) {
			super(f.dim);
                        this.f = f;
                }
                public double map(double[] x) {
                        return 1.0/f.map(x);
                }
        }

        /**
        * Returns the division of this function and another.
        */
        public Ring.Member divide(Ring.Member f) {
                if(f instanceof RealFunctionND)
                        return divide((RealFunctionND)f);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        public RealFunctionND divide(RealFunctionND f) {
		if(dim != f.dim)
			throw new DimensionException("Functions have different dimensions.");
                return new Quotient(this, f);
        }
        private static class Quotient extends RealFunctionND {
                private final RealFunctionND f1, f2;
                public Quotient(RealFunctionND f1, RealFunctionND f2) {
			super(f1.dim);
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public double map(double[] x) {
                        return f1.map(x)/f2.map(x);
                }
        }

        public RealFunctionND tensor(RealFunction f) {
                return new TensorProductN1D(this, f);
        }
        private static class TensorProductN1D extends RealFunctionND {
                private final RealFunctionND f1;
		private final RealFunction f2;
                public TensorProductN1D(RealFunctionND f1, RealFunction f2) {
			super(f1.dim+1);
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public double map(double[] x) {
			double[] x1 = new double[f1.dim];
			System.arraycopy(x, 0, x1, 0, x1.length);
			double x2 = x[x1.length];
                        return f1.map(x1)*f2.map(x2);
                }
        }
        public RealFunctionND tensor(RealFunctionND f) {
                return new TensorProductND(this, f);
        }
        private static class TensorProductND extends RealFunctionND {
                private final RealFunctionND f1, f2;
                public TensorProductND(RealFunctionND f1, RealFunctionND f2) {
			super(f1.dim+f2.dim);
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public double map(double[] x) {
			double[] x1 = new double[f1.dim];
			double[] x2 = new double[f2.dim];
			System.arraycopy(x, 0, x1, 0, x1.length);
			System.arraycopy(x, x1.length, x2, 0, x2.length);
                        return f1.map(x1)*f2.map(x2);
                }
        }

	public static RealFunctionND constant(int dim, double k) {
		return new Constant(dim, k);
	}
	private static class Constant extends RealFunctionND {
		private final double A;
		public Constant(int dim, double A) {
			super(dim);
			this.A = A;
		}
		public double map(double[] x) {
			return A;
		}
	}
}
