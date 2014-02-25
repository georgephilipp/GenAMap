package JSci.tests;

import JSci.GlobalSettings;
import JSci.maths.*;

/**
* Testcase for extra math methods.
* @author Mark Hale
*/
public class ExtraMathTest extends junit.framework.TestCase {
        public static void main(String arg[]) {
                junit.textui.TestRunner.run(ExtraMathTest.class);
        }
        public ExtraMathTest(String name) {
                super(name);
        }
        protected void setUp() {
                GlobalSettings.ZERO_TOL=1.0e-9;
        }
	private void assertIdentical(double exp, double actual) {
		assertEquals(new Double(exp), new Double(actual));
	}
	public void testAsinh() {
		assertIdentical(-0.0, ExtraMath.asinh(-0.0));
		assertIdentical(Double.NEGATIVE_INFINITY, ExtraMath.asinh(Double.NEGATIVE_INFINITY));
                double x = Math.random();
                assertEquals(x, ExtraMath.asinh(ExtraMath.sinh(x)), GlobalSettings.ZERO_TOL);
	}
	public void testAcosh() {
                double x = Math.random();
                assertEquals(x, ExtraMath.acosh(ExtraMath.cosh(x)), GlobalSettings.ZERO_TOL);
	}
	public void testAtanh() {
		assertIdentical(-0.0, ExtraMath.atanh(-0.0));
                double x = Math.random();
                assertEquals(x, ExtraMath.atanh(ExtraMath.tanh(x)), GlobalSettings.ZERO_TOL);
	}
	public void testAcsch() {
                double x = Math.random();
                assertEquals(x, ExtraMath.acsch(ExtraMath.csch(x)), GlobalSettings.ZERO_TOL);
	}
	public void testAsech() {
                double x = Math.random();
                assertEquals(x, ExtraMath.asech(ExtraMath.sech(x)), GlobalSettings.ZERO_TOL);
	}
	public void testAcoth() {
                double x = Math.random();
                assertEquals(x, ExtraMath.acoth(ExtraMath.coth(x)), GlobalSettings.ZERO_TOL);
	}
        public void testRound() {
		// +ve value, -ve exp, round-up
                assertEquals(0.0035, ExtraMath.round(0.00345238, 2), 0.0);
		// -ve value, -ve exp, round-down
                assertEquals(-0.0000345, ExtraMath.round(-0.0000345238, 3), 0.0);
		// +ve value, +ve exp, round-down
                assertEquals(1900.0, ExtraMath.round(1900.0001, 3), 0.0);
		// -ve value, +ve exp, round-up
                assertEquals(-3400000.0, ExtraMath.round(-3450000.0, 2), 0.0);
        }
}
