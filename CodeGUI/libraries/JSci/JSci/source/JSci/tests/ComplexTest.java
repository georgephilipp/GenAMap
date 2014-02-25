package JSci.tests;

import JSci.GlobalSettings;
import JSci.maths.Complex;
import JSci.maths.ExtraMath;
import junit.framework.*;

/**
* Testcase for complex numbers.
* @author Mark Hale
*/
public class ComplexTest extends junitx.extensions.EqualsHashCodeTestCase {
        private double x, y;
        private double u, v;

        public static void main(String arg[]) {
                junit.textui.TestRunner.run(suite());
        }
        public static Test suite() {
                return new junit.extensions.RepeatedTest(new TestSuite(ComplexTest.class), 5);
        }
        public ComplexTest(String name) {
                super(name);
        }
        protected void setUp() throws Exception {
                GlobalSettings.ZERO_TOL=1.0e-9;
                x = ExtraMath.random(-1.0, 1.0);
                y = ExtraMath.random(-1.0, 1.0);
                u = ExtraMath.random(-1.0, 1.0);
                v = ExtraMath.random(-1.0, 1.0);
                super.setUp();
        }
        protected Object createInstance() {
                return new Complex(x, y);
        }
        protected Object createNotEqualInstance() {
                return new Complex(u, v);
        }
        public void testParseString() {
                Complex expected = (Complex) createInstance();
                Complex actual = new Complex(expected.toString());
                assertEquals(expected, actual);
                expected = new Complex(0.0, Double.MAX_VALUE);
                actual = new Complex(expected.toString());
                assertEquals(expected, actual);
                expected = new Complex(Double.MIN_VALUE, 0.0);
                actual = new Complex(expected.toString());
                assertEquals(expected, actual);
        }
        public void testZero() {
                assertTrue("0 is zero", Complex.ZERO.isZero());
                Complex delta = (Complex) createInstance();
                delta = delta.multiply(Math.random()*GlobalSettings.ZERO_TOL/delta.mod());
                assertTrue("delta is zero", delta.isZero());
                assertTrue("delta == zero", delta.equals(Complex.ZERO));
                Complex z = (Complex) createNotEqualInstance();
                assertTrue("z == z + delta", z.equals(z.add(delta)));
        }
        public void testSin() {
                Complex expected = (Complex) createInstance();
                Complex actual=Complex.sin(Complex.asin(expected));
                assertEquals(expected, actual);
        }
        public void testCos() {
                Complex expected = (Complex) createInstance();
                Complex actual=Complex.cos(Complex.acos(expected));
                assertEquals(expected, actual);
        }
        public void testTan() {
                Complex expected = (Complex) createInstance();
                Complex actual=Complex.tan(Complex.atan(expected));
                assertEquals(expected, actual);
        }
        public void testSinh() {
                Complex expected = (Complex) createInstance();
                Complex actual=Complex.sinh(Complex.asinh(expected));
                assertEquals(expected, actual);
        }
        public void testCosh() {
                Complex expected = (Complex) createInstance();
                Complex actual=Complex.cosh(Complex.acosh(expected));
                assertEquals(expected, actual);
        }
        public void testTanh() {
                Complex expected = (Complex) createInstance();
                Complex actual=Complex.tanh(Complex.atanh(expected));
                assertEquals(expected, actual);
        }
}

