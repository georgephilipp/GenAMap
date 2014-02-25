package JSci.tests;

import JSci.maths.*;
import JSci.maths.polynomials.*;

/**
* Testcase for polynomials.
*/
public class PolynomialTest extends junit.framework.TestCase {
        private final int n = 15;

        public static void main(String arg[]) {
                junit.textui.TestRunner.run(PolynomialTest.class);
        }
        public PolynomialTest(String name) {
                super(name);
        }
        protected void setUp() throws Exception {
                JSci.GlobalSettings.ZERO_TOL=1.0e-6;
                super.setUp();
        }
        private static double[] createRandomArray(int n) {
                double[] arr = new double[n];
                for(int i=0; i<arr.length; i++)
                        arr[i] = 2.0*Math.random()-1.0;
                return arr;
        }
        public void testSubtract() {
                RealPolynomial p = new RealPolynomial(createRandomArray(n));
                p = (RealPolynomial) p.subtract(p);
                assertEquals(RealPolynomialRing.getInstance().zero(), p);
                assertEquals(0, p.degree());
        }
        public void testLeastSquaresFit() {
                // generate random polynomial
                RealPolynomial p = new RealPolynomial(createRandomArray(n));

                // generate random sample data
                final double scale = 10.0;
                double[][] data = new double[2][n];
                data[0][0] = scale*Math.random();
                data[1][0] = p.map(data[0][0]);
                for ( int k = 1; k < data[0].length; k++ ) {
                        data[0][k] = data[0][k - 1] + scale*Math.random();
                        data[1][k] = p.map(data[0][k]);
                }
                RealPolynomial fitted = LinearMath.leastSquaresFit(n-1, data);
                assertEquals(p, fitted);
        }
        public void testInterpolation() {
                // unstable at large n
                final int n = 5;
                // generate random sample data
                final double scale = 10.0;
                double[][] data = new double[2][n];
                data[0][0] = scale*Math.random();
                data[1][0] = scale*Math.random();
                for ( int k = 1; k < data[0].length; k++ ) {
                        data[0][k] = data[0][k - 1] + scale*Math.random();
                        data[1][k] = scale*Math.random();
                }

                RealPolynomial p = PolynomialMath.interpolateLagrange(data);
                for ( int k = 0; k < n; k++ ) {
                        assertEquals(data[1][k], p.map(data[0][k]), JSci.GlobalSettings.ZERO_TOL);
                }
        }
}

