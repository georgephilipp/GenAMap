package JSci.tests;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.HashMap;
import junit.framework.*;
import JSci.maths.*;
import JSci.maths.vectors.*;
import JSci.maths.polynomials.*;

/**
* Testcase for vectors.
* @author Mark Hale
*/
public class VectorTest extends junitx.extensions.EqualsHashCodeTestCase {
        private final int N = 10;
        private Class vectorClass;
        private double array[];
        private double array2[];

        public static void main(String arg[]) {
                junit.textui.TestRunner.run(suite());
        }
        public static Test suite() {
                Class[] classes = new Class[] {
                        DoubleVector.class, DoubleSparseVector.class
                };
                TestSuite suite = new TestSuite(VectorTest.class.toString());
                for(int i=0; i<classes.length; i++) {
                        Map properties = new HashMap();
                        properties.put("test.vector.class", classes[i]);
                        suite.addTest(new TestProperties(new TestSuite(VectorTest.class, classes[i].toString()), properties));
                }
                return suite;
        }
        public VectorTest(String name) {
                super(name);
        }
        protected void setUp() throws Exception {
                JSci.GlobalSettings.ZERO_TOL=1.0e-6;
                array=new double[N];
                array2=new double[N];
                vectorClass = (Class) TestProperties.getProperties().get("test.vector.class");
                for(int i=0; i<N; i++) {
                        array[i]=Math.random();
                        array2[i]=Math.random();
                }
                super.setUp();
        }
        protected Object createInstance() throws Exception {
                Constructor constructor = vectorClass.getConstructor(new Class[] {double[].class});
                return constructor.newInstance(new Object[] {array});
        }
        protected Object createNotEqualInstance() throws Exception {
                Constructor constructor = vectorClass.getConstructor(new Class[] {double[].class});
                return constructor.newInstance(new Object[] {array2});
        }
        protected Object createZeroInstance() throws Exception {
                Constructor constructor = vectorClass.getConstructor(new Class[] {int.class});
                return constructor.newInstance(new Object[] {new Integer(N)});
        }
        public void testConstructor() throws Exception {
                AbstractDoubleVector vec = (AbstractDoubleVector) createInstance();
                assertEquals(N, vec.dimension());
                for(int i=0;i<vec.dimension();i++)
                        assertEquals(array[i], vec.getComponent(i), JSci.GlobalSettings.ZERO_TOL);
        }
        public void testSetGet() throws Exception {
                AbstractDoubleVector vec = (AbstractDoubleVector) createZeroInstance();
                // tests storage allocation for sparse
                for(int i=0;i<vec.dimension();i++) {
                        vec.setComponent(i,array[i]);
                        assertEquals(array[i], vec.getComponent(i), JSci.GlobalSettings.ZERO_TOL);
                }
                for(int i=0;i<array.length;i++) {
                        assertEquals(array[i], vec.getComponent(i), JSci.GlobalSettings.ZERO_TOL);
                }
                // tests overwrite for sparse
                for(int i=0;i<vec.dimension();i++) {
                        vec.setComponent(i,array2[i]);
                        assertEquals(array2[i], vec.getComponent(i), JSci.GlobalSettings.ZERO_TOL);
                }
                for(int i=0;i<array.length;i++) {
                        assertEquals(array2[i], vec.getComponent(i), JSci.GlobalSettings.ZERO_TOL);
                }
                // tests storage deallocation for sparse
                for(int i=0;i<vec.dimension();i++) {
                        vec.setComponent(i,0.0);
                        assertEquals(0.0, vec.getComponent(i), JSci.GlobalSettings.ZERO_TOL);
                }
                for(int i=0;i<array.length;i++) {
                        assertEquals(0.0, vec.getComponent(i), JSci.GlobalSettings.ZERO_TOL);
                }
        }
        public void testAdd() throws Exception {
                AbstractDoubleVector vec = (AbstractDoubleVector) createInstance();
                AbstractDoubleVector ans=vec.add(vec);
                for(int i=0;i<ans.dimension();i++) {
			String msg = vec.getClass().toString()+": ("+i+")";
                        assertEquals(msg, array[i]+array[i], ans.getComponent(i), JSci.GlobalSettings.ZERO_TOL);
		}
        }
        public void testSubtract() throws Exception {
                AbstractDoubleVector vec = (AbstractDoubleVector) createInstance();
                AbstractDoubleVector ans=vec.subtract(vec);
                for(int i=0;i<ans.dimension();i++) {
			String msg = vec.getClass().toString()+": ("+i+")";
                        assertEquals(msg, array[i]-array[i], ans.getComponent(i), JSci.GlobalSettings.ZERO_TOL);
		}
        }
        public void testScalarProduct() throws Exception {
                AbstractDoubleVector vec = (AbstractDoubleVector) createInstance();
                double ans=vec.scalarProduct(vec);
                double sp=ArrayMath.scalarProduct(array, array);
		String msg = vec.getClass().toString();
                assertEquals(msg, sp, ans, JSci.GlobalSettings.ZERO_TOL);
        }
	public void testMapComponents() throws Exception {
                AbstractDoubleVector vec = (AbstractDoubleVector) createInstance();
		RealPolynomial map = new RealPolynomial(new double[] {2.0, 1.0});
		AbstractDoubleVector ans = vec.mapComponents(map);
                for(int i=0;i<ans.dimension();i++) {
			String msg = vec.getClass().toString()+": ("+i+")";
                        assertEquals(msg, map.map(array[i]), ans.getComponent(i), JSci.GlobalSettings.ZERO_TOL);
                }
	}
}
