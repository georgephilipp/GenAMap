package JSci.maths.analysis;

/**
* The exponential function.
* @version 1.1
* @author Mark Hale
*/
public class Exponential extends RealFunction {
        private final double A, w, n, k;
        /**
        * Constructs an exponential function of the form <code>exp(x)</code>.
        */
        public Exponential() {
		this(1.0, 1.0, 0.0, 1.0);
        }
        /**
        * Constructs an exponential function of the form <code>A exp(w(x+k))</code>.
        */
        public Exponential(double A, double w, double k) {
		this(A, w, k, 1.0);
        }
        /**
        * Constructs an exponential function of the form <code>A exp(w(x+k)<sup>n</sup>)</code>.
	* Constructor to create Gaussian functions.
        */
        public Exponential(double A, double w, double k, double n) {
                this.A = A;
                this.w = w;
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
                return A*Math.exp(w*x);
        }
        public RealFunction differentiate() {
		RealFunction diff = new Exponential(A*w, w, k, n);
		if(n != 1.0)
			diff = new Power(n, k, n-1).multiply(diff);
                return diff;
        }
}
