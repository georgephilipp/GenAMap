package JSci.maths.analysis;

/**
* The power function.
* For polynomials or integer exponents use the classes in JSci.maths.polynomials.
* @version 1.1
* @author Mark Hale
*/
public class Power extends RealFunction {
        private final double A, n, k;
        /**
        * Constructs a power function of the form <code>x</code>.
        */
        public Power() {
		this(1.0, 0.0, 1.0);
        }
        /**
        * Constructs a power function of the form <code>Ax<sup>n</sup></code>.
        */
        public Power(double A, double n) {
		this(A, 0.0, n);
        }
        /**
        * Constructs a power function of the form <code>A(x+k)<sup>n</sup></code>.
        */
        public Power(double A, double k, double n) {
                this.A = A;
                this.k = k;
                this.n = n;
        }
        public double map(double x) {
		x += k;
		if(n == 1.0)
			x = x;
		else if(n == 2.0)
			x *= x;
		else
			x = Math.pow(x, n);
                return A*x;
        }
        public RealFunction differentiate() {
                return new Power(A*n, k, n-1);
        }
}
