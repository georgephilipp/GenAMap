package JSci.maths.analysis;

import JSci.maths.Complex;
import JSci.maths.ComplexMapping;
import JSci.maths.groups.AbelianGroup;
import JSci.maths.fields.Ring;
import JSci.maths.polynomials.ComplexPolynomial;

/**
* This class describes a function on the complex numbers.
* @version 1.0
* @author Mark Hale
*/
public abstract class ComplexFunction implements ComplexMapping, Ring.Member {
	/**
	* Returns the (complex) dimension of the space this function is on.
	*/
	public final int dimension() {
		return 1;
	}
	public Object getSet() {
		throw new RuntimeException("Not implemented: please file bug report");
	}

        public ComplexFunction compose(ComplexFunction f) {
                return new Composition(this, f);
        }
        private static class Composition extends ComplexFunction {
                private final ComplexFunction f1, f2;
                public Composition(ComplexFunction f1, ComplexFunction f2) {
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public Complex map(double x, double y) {
                        return f1.map(f2.map(x, y));
                }
                public Complex map(Complex z) {
                        return f1.map(f2.map(z));
                }
                /**
                * Chain rule.
                */
                public ComplexFunction differentiate() {
                        return new Product(new Composition(f1.differentiate(), f2), f2.differentiate());
                }
        }

        /**
        * Returns the negative of this function.
        */
        public AbelianGroup.Member negate() {
                return new Negation(this);
        }
        private static class Negation extends ComplexFunction {
                private final ComplexFunction f;
                public Negation(ComplexFunction f) {
                        this.f = f;
                }
                public Complex map(double x, double y) {
                        return (Complex) f.map(x, y).negate();
                }
                public Complex map(Complex z) {
                        return (Complex) f.map(z).negate();
                }
                public ComplexFunction differentiate() {
                        return new Negation(f.differentiate());
                }
        }

        /**
        * Returns the addition of this function and another.
        */
        public AbelianGroup.Member add(final AbelianGroup.Member f) {
                if(f instanceof ComplexFunction)
                        return add((ComplexFunction)f);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        public ComplexFunction add(ComplexFunction f) {
                return new Sum(this, f);
        }
        private static class Sum extends ComplexFunction {
                private final ComplexFunction f1, f2;
                public Sum(ComplexFunction f1, ComplexFunction f2) {
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public Complex map(double x, double y) {
                        return f1.map(x, y).add(f2.map(x, y));
                }
                public Complex map(Complex z) {
                        return f1.map(z).add(f2.map(z));
                }
                public ComplexFunction differentiate() {
                        return new Sum(f1.differentiate(), f2.differentiate());
                }
        }

        /**
        * Returns the subtraction of this function and another.
        */
        public AbelianGroup.Member subtract(final AbelianGroup.Member f) {
                if(f instanceof ComplexFunction)
                        return subtract((ComplexFunction)f);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        public ComplexFunction subtract(ComplexFunction f) {
                return new Difference(this, f);
        }
        private static class Difference extends ComplexFunction {
                private final ComplexFunction f1, f2;
                public Difference(ComplexFunction f1, ComplexFunction f2) {
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public Complex map(double x, double y) {
                        return f1.map(x, y).subtract(f2.map(x, y));
                }
                public Complex map(Complex z) {
                        return f1.map(z).subtract(f2.map(z));
                }
                public ComplexFunction differentiate() {
                        return new Difference(f1.differentiate(), f2.differentiate());
                }
        }

        /**
        * Returns the multiplication of this function and another.
        */
        public Ring.Member multiply(Ring.Member f) {
                if(f instanceof ComplexFunction)
                        return multiply((ComplexFunction)f);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        public ComplexFunction multiply(ComplexFunction f) {
                return new Product(this, f);
        }
        private static class Product extends ComplexFunction {
                private final ComplexFunction f1, f2;
                public Product(ComplexFunction f1, ComplexFunction f2) {
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public Complex map(double x, double y) {
                        return f1.map(x, y).multiply(f2.map(x, y));
                }
                public Complex map(Complex z) {
                        return f1.map(z).multiply(f2.map(z));
                }
                /**
                * Product rule.
                */
                public ComplexFunction differentiate() {
                        return new Sum(new Product(f1.differentiate(), f2), new Product(f1, f2.differentiate()));
                }
        }

        /**
        * Returns the multiplicative inverse (reciprocal) of this function.
        */
        public Ring.Member inverse() {
                return new Reciprocal(this);
        }
        private static class Reciprocal extends ComplexFunction {
                private final ComplexFunction f;
                public Reciprocal(ComplexFunction f) {
                        this.f = f;
                }
                public Complex map(double x, double y) {
                        return Complex.ONE.divide(f.map(x, y));
                }
                public Complex map(Complex z) {
                        return Complex.ONE.divide(f.map(z));
                }
                public ComplexFunction differentiate() {
                        return new Quotient(new Negation(f.differentiate()), new Product(f, f));
                }
        }

        /**
        * Returns the division of this function and another.
        */
        public Ring.Member divide(Ring.Member f) {
                if(f instanceof ComplexFunction)
                        return divide((ComplexFunction)f);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        public ComplexFunction divide(ComplexFunction f) {
                return new Quotient(this, f);
        }
        private static class Quotient extends ComplexFunction {
                private final ComplexFunction f1, f2;
                public Quotient(ComplexFunction f1, ComplexFunction f2) {
                        this.f1 = f1;
                        this.f2 = f2;
                }
                public Complex map(double x, double y) {
                        return f1.map(x, y).divide(f2.map(x, y));
                }
                public Complex map(Complex z) {
                        return f1.map(z).divide(f2.map(z));
                }
                /**
                * Quotient rule.
                */
                public ComplexFunction differentiate() {
                        return new Quotient(new Difference(new Product(f1.differentiate(), f2), new Product(f1, f2.differentiate())), new Product(f2, f2));
                }
        }

        /**
        * Returns the differential of this function.
        */
        public abstract ComplexFunction differentiate();

	public static ComplexFunction constant(Complex k) {
		return new Constant(k);
	}
	private static final ComplexFunction ZERO = constant(Complex.ZERO);
	private static class Constant extends ComplexFunction {
		private final Complex A;
		public Constant(Complex A) {
			this.A = A;
		}
		public Complex map(double x, double y) {
			return A;
		}
		public Complex map(Complex z) {
			return A;
		}
		public ComplexFunction differentiate() {
			return ZERO;
		}
	}
}
