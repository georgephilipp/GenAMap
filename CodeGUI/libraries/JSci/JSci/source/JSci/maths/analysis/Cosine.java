package JSci.maths.analysis;

/**
* The cosine function.
* @version 1.1
* @author Mark Hale
*/
public class Cosine extends RealFunction {
        private final double A, w, k;
        /**
        * Constructs a cosine function of the form <code>cos(x)</code>.
        */
        public Cosine() {
		this(1.0, 1.0, 0.0);
        }
        /**
        * Constructs a cosine function of the form <code>A cos(wx+k)</code>.
        */
        public Cosine(double A, double w, double k) {
		this.A = A;
		this.w = w;
		this.k = k;
        }
        public double map(double x) {
                return A*Math.cos(w*x+k);
        }
        public RealFunction differentiate() {
		return new Sine(-A*w, w, k);
        }
}
