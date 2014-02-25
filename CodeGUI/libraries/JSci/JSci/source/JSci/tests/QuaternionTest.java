package JSci.tests;

import JSci.maths.Quaternion;

/**
* Testcase for quaternions.
* @author Mark Hale
*/
public class QuaternionTest extends junitx.extensions.EqualsHashCodeTestCase {
        private double w, x, y, z;
        private double s, t, u, v;

        public static void main(String arg[]) {
                junit.textui.TestRunner.run(QuaternionTest.class);
        }
        public QuaternionTest(String name) {
                super(name);
        }
        protected void setUp() throws Exception {
                JSci.GlobalSettings.ZERO_TOL=1.0e-10;
                w = Math.random();
                x = Math.random();
                y = Math.random();
                z = Math.random();
                s = Math.random();
                t = Math.random();
                u = Math.random();
                v = Math.random();
                super.setUp();
        }
        protected Object createInstance() {
                return new Quaternion(w, x, y, z);
        }
        protected Object createNotEqualInstance() {
                return new Quaternion(s, t, u, v);
        }
        public void testExp() {
                Quaternion expected = (Quaternion) createInstance();
                Quaternion actual=Quaternion.exp(Quaternion.log(expected));
                assertEquals(expected, actual);
        }
        public void testRotationMatrixConversion() {
                Quaternion expected = (Quaternion) createInstance();
                // normalise
                expected = expected.divide(expected.norm());
                Quaternion actual = Quaternion.rotation(expected.toRotationMatrix());
                assertEquals(expected, actual);
        }
}

