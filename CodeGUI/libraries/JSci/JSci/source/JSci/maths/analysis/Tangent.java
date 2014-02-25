package JSci.maths.analysis;

/**
* The tangent function.
* @version 1.0
* @author Mark Hale
*/
public class Tangent extends RealFunction {
        private final double A, w, k;
        /**
        * Constructs a tangent function of the form <code>tan(x)</code>.
        */
        public Tangent() {
		this(1.0, 1.0, 0.0);
        }
        /**
        * Constructs a tangent function of the form <code>A tan(wx+k)</code>.
        */
        public Tangent(double A, double w, double k) {
		this.A = A;
		this.w = w;
		this.k = k;
        }
        public double map(double x) {
                return A*Math.tan(w*x+k);
        }
        public RealFunction differentiate() {
		Cosine cos = new Cosine(1.0, w, k);
		return RealFunction.constant(A*w).divide(cos.multiply(cos));
        }
}
