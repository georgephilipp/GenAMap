package JSci.tests;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import junit.framework.Test;
import junit.framework.TestResult;
import junitx.extensions.TestSetup;

/**
 * A TestSetup which provides additional fixture state in the form of thread-local properties.
 * A TestCase can use these properties in its setUp() method to customize its fixture.
 * <pre>
 * public static Test suite() {
 *      TestSuite suite = new TestSuite();
 *      Map test1Properties = new HashMap();
 *      test1Properties.put("test.object", new MyFirstTestObject());
 *      suite.addTest(new TestProperties(new TestSuite(MyTestCase.class), test1Properties));
 *
 *      Map test2Properties = new HashMap();
 *      test2Properties.put("test.object", new MySecondTestObject());
 *      suite.addTest(new TestProperties(new TestSuite(MyTestCase.class), test2Properties));
 *      return suite;
 * }
 *
 * protected void setUp() throws Exception {
 *      Map properties = TestProperties.getProperties();
 *      Object obj = properties.get("test.object");
 *      // do fixture setup with obj
 * }
 * </pre>
 * @author Mark Hale
 */
public class TestProperties extends TestSetup {
        /** Thread-local properties. */
        private static final ThreadLocal PROPERTIES = new ThreadLocal() {
                protected Object initialValue() {
                        return new HashMap();
                }
        };
        /**
         * Returns the properties for this fixture.
         */
        public static Map getProperties() {
                return (Map) PROPERTIES.get();
        }

        private final Map properties;
        /**
         * @param properties the properties to be made available at <code>setUp()</code> time.
         */
        public TestProperties(Test test, Map properties) {
                super(test);
                this.properties = properties;
        }
        /**
         * Adds <code>this</code> properties to the fixture properties.
         */
        protected void setUp() throws Exception {
                getProperties().putAll(properties);
        }
        /**
         * Removes <code>this</code> properties from the fixture properties.
         */
        protected void tearDown() throws Exception {
                final Map threadLocalProperties = getProperties();
                for(Iterator iter = properties.keySet().iterator(); iter.hasNext(); ) {
                        threadLocalProperties.remove(iter.next());
                }
        }
}

