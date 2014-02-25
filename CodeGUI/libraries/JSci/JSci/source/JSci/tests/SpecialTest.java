package JSci.tests;

import JSci.maths.*;

/**
* Testcase for special function methods.
* @author Mark Hale
*/
public class SpecialTest extends junit.framework.TestCase {
        public static void main(String arg[]) {
                junit.textui.TestRunner.run(SpecialTest.class);
        }
        public SpecialTest(String name) {
                super(name);
        }
        /**
        * Tests the gamma function against its functional equation.
        */
        public void testGammaFunctional() {
                for(int i=0; i<10; i++) {
                        double x = SpecialMath.GAMMA_X_MAX_VALUE*Math.random();
                        double expected = x*SpecialMath.gamma(x);
                        double ans = SpecialMath.gamma(x+1.0);
                        assertEquals(expected, ans, 1.0e-10*Math.abs(expected));
                }
        }
        /**
        * Tests the gamma function against the Legendre duplication formula.
        */
        public void testGammaDuplication() {
                for(int i=0; i<10; i++) {
                        double x = Math.sqrt(SpecialMath.GAMMA_X_MAX_VALUE)*Math.random();
                        double expected = Math.pow(4.0, x)/(2.0*Math.sqrt(Math.PI))*SpecialMath.gamma(x)*SpecialMath.gamma(x+0.5);
                        double ans = SpecialMath.gamma(2.0*x);
                        assertEquals(expected, ans, 1.0e-10*Math.abs(expected));
                }
        }
}

