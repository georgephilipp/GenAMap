package JSci.maths.analysis;

import JSci.maths.Mapping;
import JSci.maths.groups.AbelianGroup;
import JSci.maths.fields.Ring;

/**
* This class describes a function on a 3D space.
* @version 1.0
* @author Mark Hale
*/
public abstract class RealFunction3D implements Ring.Member {
        public abstract double map(double x, double y, double z);
	/**
	* Returns the dimension of the space this function is on.
	*/
	public final int dimension() {
		return 3;
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
        private static class Negation extends RealFunction3D {
                private final RealFunction3D f;
                public Negation(RealFunction3D f) {
                        this.f = f;
                }
                public double map(double x, double y, double z) {
                        return -f.map(x, y, z);
                }
        }

        /**
        * Returns the addition of this function and another.
        */
        public AbelianGroup.Member add(final AbelianGroup.Member f) {
                if(f instanceof RealFunction3D)
                        return add((RealFunction3D)f);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        public RealFunction3D add(RealFunction3D f) {
                return new Sum(this, f);
        }
        private static class Sum extends RealFunction3D {
                private final RealFunction3D f1, f2;
                public Sum(RealFunction3D f1, RealFunction3D f2) {
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public double map(double x, double y, double z) {
                        return f1.map(x, y, z)+f2.map(x, y, z);
                }
        }

        /**
        * Returns the subtraction of this function and another.
        */
        public AbelianGroup.Member subtract(final AbelianGroup.Member f) {
                if(f instanceof RealFunction3D)
                        return subtract((RealFunction3D)f);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        public RealFunction3D subtract(RealFunction3D f) {
                return new Difference(this, f);
        }
        private static class Difference extends RealFunction3D {
                private final RealFunction3D f1, f2;
                public Difference(RealFunction3D f1, RealFunction3D f2) {
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public double map(double x, double y, double z) {
                        return f1.map(x, y, z)-f2.map(x, y, z);
                }
        }

        /**
        * Returns the multiplication of this function and another.
        */
        public Ring.Member multiply(Ring.Member f) {
                if(f instanceof RealFunction3D)
                        return multiply((RealFunction3D)f);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        public RealFunction3D multiply(RealFunction3D f) {
                return new Product(this, f);
        }
        private static class Product extends RealFunction3D {
                private final RealFunction3D f1, f2;
                public Product(RealFunction3D f1, RealFunction3D f2) {
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public double map(double x, double y, double z) {
                        return f1.map(x, y, z)*f2.map(x, y, z);
                }
        }

        /**
        * Returns the multiplicative inverse (reciprocal) of this function.
        */
        public Ring.Member inverse() {
                return new Reciprocal(this);
        }
        private static class Reciprocal extends RealFunction3D {
                private final RealFunction3D f;
                public Reciprocal(RealFunction3D f) {
                        this.f = f;
                }
                public double map(double x, double y, double z) {
                        return 1.0/f.map(x, y, z);
                }
        }

        /**
        * Returns the division of this function and another.
        */
        public Ring.Member divide(Ring.Member f) {
                if(f instanceof RealFunction3D)
                        return divide((RealFunction3D)f);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        public RealFunction3D divide(RealFunction3D f) {
                return new Quotient(this, f);
        }
        private static class Quotient extends RealFunction3D {
                private final RealFunction3D f1, f2;
                public Quotient(RealFunction3D f1, RealFunction3D f2) {
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public double map(double x, double y, double z) {
                        return f1.map(x, y, z)/f2.map(x, y, z);
                }
        }

        public RealFunctionND tensor(RealFunction f) {
                return new TensorProduct4D(this, f);
        }
        private static class TensorProduct4D extends RealFunctionND {
                private final RealFunction3D f1;
		private final RealFunction f2;
                public TensorProduct4D(RealFunction3D f1, RealFunction f2) {
			super(4);
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public double map(double[] x) {
                        return f1.map(x[0], x[1], x[2])*f2.map(x[3]);
                }
        }
        public RealFunctionND tensor(RealFunction2D f) {
                return new TensorProduct5D(this, f);
        }
        private static class TensorProduct5D extends RealFunctionND {
                private final RealFunction3D f1;
		private final RealFunction2D f2;
                public TensorProduct5D(RealFunction3D f1, RealFunction2D f2) {
			super(5);
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public double map(double[] x) {
                        return f1.map(x[0], x[1], x[2])*f2.map(x[3], x[4]);
                }
        }

	public static RealFunction3D constant(double k) {
		return new Constant(k);
	}
	private static class Constant extends RealFunction3D {
		private final double A;
		public Constant(double A) {
			this.A = A;
		}
		public double map(double x, double y, double z) {
			return A;
		}
	}
}
