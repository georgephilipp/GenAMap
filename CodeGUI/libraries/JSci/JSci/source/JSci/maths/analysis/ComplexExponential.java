package JSci.maths.analysis;

import JSci.maths.Complex;

/**
* The complex exponential function.
* @version 1.0
* @author Mark Hale
*/
public class ComplexExponential extends ComplexFunction {
	private final Complex A;
	private final Complex w;
        /**
        * Constructs an exponential function of the form <code>exp(iz)</code>.
        */
        public ComplexExponential() {
		A = Complex.ONE;
		w = Complex.ONE;
        }
        /**
        * Constructs an exponential function of the form <code>A exp(iwz)</code>.
        */
        public ComplexExponential(Complex A, Complex w) {
		this.A = A;
		this.w = w;
        }
	public Complex map(double x, double y) {
		final double iwzRe = -(w.imag()*x + w.real()*y);
		final double iwzIm = w.real()*x - w.imag()*y;
                return A.multiply(new Complex(
                        Math.exp(iwzRe)*Math.cos(iwzIm),
                        Math.exp(iwzRe)*Math.sin(iwzIm)
                ));
        }
	public Complex map(Complex z) {
		return map(z.real(), z.imag());
	}
        public ComplexFunction differentiate() {
                return new ComplexExponential(A.multiply(Complex.I.multiply(w)), w);
        }
	public ComplexFunction integrate() {
                return new ComplexExponential(A.divide(Complex.I.multiply(w)), w);
	}
}
