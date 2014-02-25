package JSci.tests;

import JSci.maths.*;
import JSci.maths.analysis.RealFunction3D;
import JSci.maths.polynomials.*;

/**
* Testcase for numerical integration methods.
* @author Mark Hale
*/
public class NumericalTest extends junit.framework.TestCase {
        private final Mapping testFunc=new Mapping() {
                public double map(double x) {
                        return 1.0/x;
                }
        };
        private final double testFunc_a=1.0;
        private final double testFunc_b=Math.E;
        private final double testFuncExpected=1.0;

        /** dy/dx = y */
        private final RealPolynomial testDeriv=new RealPolynomial(new double[] {0.0, 1.0});
        private final double testDerivInitial=1.0;
        private final double testDerivExpected=Math.E;

        public static void main(String arg[]) {
                junit.textui.TestRunner.run(NumericalTest.class);
        }
        public NumericalTest(String name) {
                super(name);
        }
        protected void setUp() {
                JSci.GlobalSettings.ZERO_TOL=1.0e-6;
        }
        public void testSolveQuadratic() {
                double x1 = ExtraMath.random(-1.0, 1.0);
                double x2 = ExtraMath.random(-1.0, 1.0);
                double a = ExtraMath.random(-1.0, 1.0);
                double b = -a*(x1+x2);
                double c = a*x1*x2;
                double[] roots = NumericalMath.solveQuadratic(a, b, c);
                assertEquals(Math.min(x1,x2), Math.min(roots[0],roots[1]), JSci.GlobalSettings.ZERO_TOL);
                assertEquals(Math.max(x1,x2), Math.max(roots[0],roots[1]), JSci.GlobalSettings.ZERO_TOL);
        }
        public void testTrapezium() {
                double ans=NumericalMath.trapezium(500,testFunc,testFunc_a,testFunc_b);
                assertEquals(testFuncExpected, ans, JSci.GlobalSettings.ZERO_TOL);
        }
        public void testSimpson() {
                double ans=NumericalMath.simpson(20,testFunc,testFunc_a,testFunc_b);
                assertEquals(testFuncExpected, ans, JSci.GlobalSettings.ZERO_TOL);
        }
        public void testRichardson() {
                double ans=NumericalMath.richardson(5,testFunc,testFunc_a,testFunc_b);
                assertEquals(testFuncExpected, ans, JSci.GlobalSettings.ZERO_TOL);
        }
        public void testGaussian4() {
                double ans=NumericalMath.gaussian4(2,testFunc,testFunc_a,testFunc_b);
                assertEquals(testFuncExpected, ans, JSci.GlobalSettings.ZERO_TOL);
        }
        public void testGaussian8() {
                double ans=NumericalMath.gaussian8(1,testFunc,testFunc_a,testFunc_b);
                assertEquals(testFuncExpected, ans, JSci.GlobalSettings.ZERO_TOL);
        }
        public void testSimpleRungeKutta2() {
                double[] y = new double[5001];
                y[0] = testDerivInitial;
                NumericalMath.rungeKutta2(y, testDeriv, 1.0/(y.length-1));
                assertEquals(testDerivExpected, y[y.length-1], JSci.GlobalSettings.ZERO_TOL);
        }
        public void testSimpleRungeKutta4() {
                double[] y = new double[51];
                y[0] = testDerivInitial;
                NumericalMath.rungeKutta4(y, testDeriv, 1.0/(y.length-1));
                assertEquals(testDerivExpected, y[y.length-1], JSci.GlobalSettings.ZERO_TOL);
        }
        public void testRungeKutta2() {
                double[] y = new double[5001];
                y[0] = testDerivInitial;
                NumericalMath.rungeKutta2(y, testDeriv.tensor(testDeriv), 0.0, Math.sqrt(2.0)/(y.length-1));
                assertEquals(testDerivExpected, y[y.length-1], JSci.GlobalSettings.ZERO_TOL);
        }
        public void testRungeKutta4() {
                double[] y = new double[51];
                y[0] = testDerivInitial;
                NumericalMath.rungeKutta4(y, testDeriv.tensor(testDeriv), 0.0, Math.sqrt(2.0)/(y.length-1));
                assertEquals(testDerivExpected, y[y.length-1], JSci.GlobalSettings.ZERO_TOL);
        }
        /**
         * y'' = xy' - 3y
         * Exact solution:
         * y = x^3 - 3x
         * y' = 3x^2 - 3
         */
        private final RealFunction3D testFunc2_2 = new RealFunction3D() {
            public double map(double y, double dy, double x) {
                return x*dy - 3*y;
            }
        };
        private final RealPolynomial testFunc2 = new RealPolynomial(new double[] {0.0, -3.0, 0.0, 1.0});
        private final RealPolynomial testFunc2_1 = new RealPolynomial(new double[] {-3.0, 0.0, 3.0});
        public void testRungeKuttaNystrom() {
                final double x0 = 0.0;
                double[] y = new double[1001];
                y[0] = testFunc2.map(x0);
                double[] dy = new double[1001];
                dy[0] = testFunc2_1.map(x0);
                final double x1 = 1.0;
                double expectedY = testFunc2.map(x1);
                double expectedDY = testFunc2_1.map(x1);
                NumericalMath.rungeKuttaNystrom(y, dy, testFunc2_2, 0.0, x1/(y.length-1));
                assertEquals("y", expectedY, y[y.length-1], JSci.GlobalSettings.ZERO_TOL);
                assertEquals("dy/dx", expectedDY, dy[dy.length-1], JSci.GlobalSettings.ZERO_TOL);
        }
}

