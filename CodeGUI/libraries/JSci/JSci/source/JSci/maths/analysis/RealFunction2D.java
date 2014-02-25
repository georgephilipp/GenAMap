package JSci.maths.analysis;

import JSci.maths.Mapping;
import JSci.maths.groups.AbelianGroup;
import JSci.maths.fields.Ring;

/**
* This class describes a function on a 2D space.
* @version 1.0
* @author Mark Hale
*/
public abstract class RealFunction2D implements Ring.Member {
        public abstract double map(double x, double y);
	/**
	* Returns the dimension of the space this function is on.
	*/
	public final int dimension() {
		return 2;
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
        private static class Negation extends RealFunction2D {
                private final RealFunction2D f;
                public Negation(RealFunction2D f) {
                        this.f = f;
                }
                public double map(double x, double y) {
                        return -f.map(x, y);
                }
        }

        /**
        * Returns the addition of this function and another.
        */
        public AbelianGroup.Member add(final AbelianGroup.Member f) {
                if(f instanceof RealFunction2D)
                        return add((RealFunction2D)f);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        public RealFunction2D add(RealFunction2D f) {
                return new Sum(this, f);
        }
        private static class Sum extends RealFunction2D {
                private final RealFunction2D f1, f2;
                public Sum(RealFunction2D f1, RealFunction2D f2) {
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public double map(double x, double y) {
                        return f1.map(x, y)+f2.map(x, y);
                }
        }

        /**
        * Returns the subtraction of this function and another.
        */
        public AbelianGroup.Member subtract(final AbelianGroup.Member f) {
                if(f instanceof RealFunction2D)
                        return subtract((RealFunction2D)f);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        public RealFunction2D subtract(RealFunction2D f) {
                return new Difference(this, f);
        }
        private static class Difference extends RealFunction2D {
                private final RealFunction2D f1, f2;
                public Difference(RealFunction2D f1, RealFunction2D f2) {
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public double map(double x, double y) {
                        return f1.map(x, y)-f2.map(x, y);
                }
        }

        /**
        * Returns the multiplication of this function and another.
        */
        public Ring.Member multiply(Ring.Member f) {
                if(f instanceof RealFunction2D)
                        return multiply((RealFunction2D)f);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        public RealFunction2D multiply(RealFunction2D f) {
                return new Product(this, f);
        }
        private static class Product extends RealFunction2D {
                private final RealFunction2D f1, f2;
                public Product(RealFunction2D f1, RealFunction2D f2) {
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public double map(double x, double y) {
                        return f1.map(x, y)*f2.map(x, y);
                }
        }

        /**
        * Returns the multiplicative inverse (reciprocal) of this function.
        */
        public Ring.Member inverse() {
                return new Reciprocal(this);
        }
        private static class Reciprocal extends RealFunction2D {
                private final RealFunction2D f;
                public Reciprocal(RealFunction2D f) {
                        this.f = f;
                }
                public double map(double x, double y) {
                        return 1.0/f.map(x, y);
                }
        }

        /**
        * Returns the division of this function and another.
        */
        public Ring.Member divide(Ring.Member f) {
                if(f instanceof RealFunction2D)
                        return divide((RealFunction2D)f);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        public RealFunction2D divide(RealFunction2D f) {
                return new Quotient(this, f);
        }
        private static class Quotient extends RealFunction2D {
                private final RealFunction2D f1, f2;
                public Quotient(RealFunction2D f1, RealFunction2D f2) {
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public double map(double x, double y) {
                        return f1.map(x, y)/f2.map(x, y);
                }
        }

        public RealFunction3D tensor(RealFunction f) {
                return new TensorProduct3D(this, f);
        }
        private static class TensorProduct3D extends RealFunction3D {
                private final RealFunction2D f1;
		private final RealFunction f2;
                public TensorProduct3D(RealFunction2D f1, RealFunction f2) {
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public double map(double x, double y, double z) {
                        return f1.map(x, y)*f2.map(z);
                }
        }
        public RealFunctionND tensor(RealFunction2D f) {
                return new TensorProduct4D(this, f);
        }
        private static class TensorProduct4D extends RealFunctionND {
                private final RealFunction2D f1;
		private final RealFunction2D f2;
                public TensorProduct4D(RealFunction2D f1, RealFunction2D f2) {
			super(4);
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public double map(double[] x) {
                        return f1.map(x[0], x[1])*f2.map(x[2], x[3]);
                }
        }

	public static RealFunction2D constant(double k) {
		return new Constant(k);
	}
	private static class Constant extends RealFunction2D {
		private final double A;
		public Constant(double A) {
			this.A = A;
		}
		public double map(double x, double y) {
			return A;
		}
	}
}
