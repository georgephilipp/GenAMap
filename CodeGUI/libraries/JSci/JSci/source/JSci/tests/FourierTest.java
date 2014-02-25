package JSci.tests;

import java.util.Map;
import java.util.HashMap;
import junit.framework.*;
import JSci.maths.*;

/**
* Testcase for Fourier methods.
* @author Mark Hale
*/
public class FourierTest extends TestCase {
	private static final int N=128;
	private double[] signal;

	public static void main(String arg[]) {
                junit.textui.TestRunner.run(suite());
        }
	public static Test suite() {
		double[][] signals = new double[][] {
			gaussian(N, 1.0, 8.0),
			topHat(N, 1.0),
			constant(N, 1.0),
			square(N, 1.0),
			triangle(N, 1.0),
			sine(N, 1.0, 16)
		};
		TestSuite suite = new TestSuite(FourierTest.class.toString());
		for(int i=0; i<signals.length; i++) {
			Map properties = new HashMap();
			properties.put("test.signal", signals[i]);
			suite.addTest(new TestProperties(new TestSuite(FourierTest.class), properties));
		}
		return suite;
	}
        public FourierTest(String name) {
                super(name);
        }
        protected void setUp() throws Exception {
                JSci.GlobalSettings.ZERO_TOL=1.0e-10;
		signal = (double[]) TestProperties.getProperties().get("test.signal");
		super.setUp();
        }
	/** Tests invFFT(f) = (FFT(f*))* */
	public void testInverseTransform() {
		Complex[] inverse = FourierMath.inverseTransform(signal);
		Complex[] transform = FourierMath.transform(signal);
		for(int i=0; i<N; i++) {
			Complex result = transform[i].conjugate().divide(N);
			assertEquals("Real", inverse[i].real(), result.real(), JSci.GlobalSettings.ZERO_TOL);
			assertEquals("Imag", inverse[i].imag(), result.imag(), JSci.GlobalSettings.ZERO_TOL);
		}
	}
	public void testTransform() {
		Complex[] transform = FourierMath.transform(signal);
		Complex[] result = FourierMath.inverseTransform(transform);
		for(int i=0; i<N; i++) {
			assertEquals("Real", signal[i], result[i].real(), JSci.GlobalSettings.ZERO_TOL);
			assertEquals("Imag", 0.0, result[i].imag(), JSci.GlobalSettings.ZERO_TOL);
		}
	}
	public void testParsevalTheorem() {
		Complex[] transform = FourierMath.transform(signal);
		assertEquals(N*ArrayMath.sumSquares(signal), ArrayMath.sumModSqrs(transform), JSci.GlobalSettings.ZERO_TOL);
        }

// A selection of test signals

        /**
        * Under transform should give something like exp(-x^2).
        * Real spectrum.
        */
        public static double[] gaussian(int n, double amplitude, double k) {
                double data[]=new double[n];
                double x;
                for(int i=0;i<n;i++) {
                        x=(i-n/2)/k;
                        data[i]=amplitude*Math.exp(-x*x);
                }
                return data;
        }
        /**
        * Under transform should give something like cos(x)/x.
        * Real spectrum.
        */
        public static double[] topHat(int n, double amplitude) {
                double data[]=new double[n];
                int i=0;
                for(;i<n/4;i++)
                        data[i]=0.0;
                for(;i<3*n/4;i++)
                        data[i]=amplitude;
                for(;i<n;i++)
                        data[i]=0.0;
                return data;
        }
        /**
        * Under transform should give a delta-function at origin.
        * Real spectrum.
        */
        public static double[] constant(int n, double amplitude) {
                double data[]=new double[n];
                for(int i=0;i<n;i++)
                        data[i]=amplitude;
                return data;
        }
        /**
        * Under transform should give something like i*sin(x)/x.
        * Complex spectrum.
        */
        public static double[] square(int n, double amplitude) {
                double data[]=new double[n];
                int i=0;
                for(;i<n/2;i++)
                        data[i]=-amplitude;
                for(;i<n;i++)
                        data[i]=amplitude;
                return data;
        }
        /**
        * Under transform should give something like i*sin(x)/x^2.
        * Complex spectrum.
        */
        public static double[] triangle(int n, double amplitude) {
                double data[]=new double[n];
                double gradient=amplitude*4.0/n;
                int i=0;
                for(;i<n/4;i++)
                        data[i]=-gradient*i;
                for(;i<3*n/4;i++)
                        data[i]=-2.0*amplitude+gradient*i;
                for(;i<n;i++)
                        data[i]=4.0*amplitude-gradient*i;
                return data;
        }
        /**
        * Under transform should give two delta-functions at +/- frequency.
        * Complex spectrum.
        */
        public static double[] sine(int n, double amplitude, int cycles) {
                double data[]=new double[n];
                double w=NumericalConstants.TWO_PI*cycles/n;
                for(int i=0;i<n;i++)
                        data[i]=amplitude*Math.sin((i-n/2)*w);
                return data;
        }
}
