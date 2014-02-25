package JSci.maths.analysis;

/**
* The sine function.
* @version 1.1
* @author Mark Hale
*/
public class Sine extends RealFunction {
        private final double A, w, k;
        /**
        * Constructs a sine function of the form <code>sin(x)</code>.
        */
        public Sine() {
		this(1.0, 1.0, 0.0);
        }
        /**
        * Constructs a sine function of the form <code>A sin(wx+k)</code>.
        */
        public Sine(double A, double w, double k) {
		this.A = A;
		this.w = w;
		this.k = k;
        }
        public double map(double x) {
                return A*Math.sin(w*x+k);
        }
        public RealFunction differentiate() {
		return new Cosine(A*w, w, k);
        }
}
