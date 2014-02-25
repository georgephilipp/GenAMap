package JSci.maths.analysis;

import JSci.maths.Mapping;
import JSci.maths.groups.AbelianGroup;
import JSci.maths.fields.Ring;
import JSci.maths.polynomials.RealPolynomial;

/**
* This class describes a function on the real numbers.
* @version 1.0
* @author Mark Hale
*/
public abstract class RealFunction implements Mapping, Ring.Member {
	/**
	* Returns the dimension of the space this function is on.
	*/
	public final int dimension() {
		return 1;
	}
	public Object getSet() {
		throw new RuntimeException("Not implemented: please file bug report");
	}

        public RealFunction compose(RealFunction f) {
                return new Composition(this, f);
        }
        private static class Composition extends RealFunction {
                private final RealFunction f1, f2;
                public Composition(RealFunction f1, RealFunction f2) {
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public double map(double x) {
                        return f1.map(f2.map(x));
                }
                /**
                * Chain rule.
                */
                public RealFunction differentiate() {
                        return new Product(new Composition(f1.differentiate(), f2), f2.differentiate());
                }
        }

        /**
        * Returns the negative of this function.
        */
        public AbelianGroup.Member negate() {
                return new Negation(this);
        }
        private static class Negation extends RealFunction {
                private final RealFunction f;
                public Negation(RealFunction f) {
                        this.f = f;
                }
                public double map(double x) {
                        return -f.map(x);
                }
                public RealFunction differentiate() {
                        return new Negation(f.differentiate());
                }
        }

        /**
        * Returns the addition of this function and another.
        */
        public AbelianGroup.Member add(final AbelianGroup.Member f) {
                if(f instanceof RealFunction)
                        return add((RealFunction)f);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        public RealFunction add(RealFunction f) {
                return new Sum(this, f);
        }
        private static class Sum extends RealFunction {
                private final RealFunction f1, f2;
                public Sum(RealFunction f1, RealFunction f2) {
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public double map(double x) {
                        return f1.map(x)+f2.map(x);
                }
                public RealFunction differentiate() {
                        return new Sum(f1.differentiate(), f2.differentiate());
                }
        }

        /**
        * Returns the subtraction of this function and another.
        */
        public AbelianGroup.Member subtract(final AbelianGroup.Member f) {
                if(f instanceof RealFunction)
                        return subtract((RealFunction)f);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        public RealFunction subtract(RealFunction f) {
                return new Difference(this, f);
        }
        private static class Difference extends RealFunction {
                private final RealFunction f1, f2;
                public Difference(RealFunction f1, RealFunction f2) {
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public double map(double x) {
                        return f1.map(x)-f2.map(x);
                }
                public RealFunction differentiate() {
                        return new Difference(f1.differentiate(), f2.differentiate());
                }
        }

        /**
        * Returns the multiplication of this function and another.
        */
        public Ring.Member multiply(Ring.Member f) {
                if(f instanceof RealFunction)
                        return multiply((RealFunction)f);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        public RealFunction multiply(RealFunction f) {
                return new Product(this, f);
        }
        private static class Product extends RealFunction {
                private final RealFunction f1, f2;
                public Product(RealFunction f1, RealFunction f2) {
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public double map(double x) {
                        return f1.map(x)*f2.map(x);
                }
                /**
                * Product rule.
                */
                public RealFunction differentiate() {
                        return new Sum(new Product(f1.differentiate(), f2), new Product(f1, f2.differentiate()));
                }
        }

        /**
        * Returns the multiplicative inverse (reciprocal) of this function.
        */
        public Ring.Member inverse() {
                return new Reciprocal(this);
        }
        private static class Reciprocal extends RealFunction {
                private final RealFunction f;
                public Reciprocal(RealFunction f) {
                        this.f = f;
                }
                public double map(double x) {
                        return 1.0/f.map(x);
                }
                public RealFunction differentiate() {
                        return new Quotient(new Negation(f.differentiate()), new Product(f, f));
                }
        }

        /**
        * Returns the division of this function and another.
        */
        public Ring.Member divide(Ring.Member f) {
                if(f instanceof RealFunction)
                        return divide((RealFunction)f);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        public RealFunction divide(RealFunction f) {
                return new Quotient(this, f);
        }
        private static class Quotient extends RealFunction {
                private final RealFunction f1, f2;
                public Quotient(RealFunction f1, RealFunction f2) {
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public double map(double x) {
                        return f1.map(x)/f2.map(x);
                }
                /**
                * Quotient rule.
                */
                public RealFunction differentiate() {
                        return new Quotient(new Difference(new Product(f1.differentiate(), f2), new Product(f1, f2.differentiate())), new Product(f2, f2));
                }
        }

        public RealFunction2D tensor(RealFunction f) {
                return new TensorProduct2D(this, f);
        }
        private static class TensorProduct2D extends RealFunction2D {
                private final RealFunction f1, f2;
                public TensorProduct2D(RealFunction f1, RealFunction f2) {
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public double map(double x, double y) {
                        return f1.map(x)*f2.map(y);
                }
        }

        /**
        * Returns the Taylor expansion of this function about a point.
        * @param a the point at which to expand about.
        * @param n the number of terms to expand to.
        * @return the Taylor series of f(x+a).
        */
        public RealPolynomial taylorExpand(double a, int n) {
                double coeff[] = new double[n];
                coeff[0] = map(a);
                RealFunction diff = this;
                int factorial = 1;
                for(int i=1; i<n; i++) {
                        diff = diff.differentiate();
                        factorial *= i;
                        coeff[i] = diff.map(a)/factorial;
                }
                return new RealPolynomial(coeff);
        }
        /**
        * Returns the differential of this function.
        */
        public abstract RealFunction differentiate();

	public static RealFunction constant(double k) {
		return new Constant(k);
	}
	private static final RealFunction ZERO = constant(0.0);
	private static class Constant extends RealFunction {
		private final double A;
		public Constant(double A) {
			this.A = A;
		}
		public double map(double x) {
			return A;
		}
		public RealFunction differentiate() {
			return ZERO;
		}
	}
}
