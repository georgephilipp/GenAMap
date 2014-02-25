package JSci.tests;

import java.util.Map;
import java.util.HashMap;
import junit.framework.*;
import JSci.maths.statistics.*;

/**
* Testcase for statistical distributions.
* @author Mark Hale
*/
public class StatisticsTest extends junit.framework.TestCase {
        private ProbabilityDistribution distribution;

        public static void main(String arg[]) {
                junit.textui.TestRunner.run(suite());
        }
        public static Test suite() {
                ProbabilityDistribution[] distrs = new ProbabilityDistribution[] {
                        new NormalDistribution(),
                        new CauchyDistribution(),
                        new BetaDistribution(5, 13),
                        new FDistribution(5, 13),
                        new TDistribution(11),
                        new GeometricDistribution(0.3),
                        new GammaDistribution(1.5),
                        new ExponentialDistribution(),
                        new LognormalDistribution(),
                        new WeibullDistribution(1.5),
                        new UniformDistribution(0.0, 1.0)
                };
                TestSuite suite = new TestSuite(StatisticsTest.class.toString());
                for(int i=0; i<distrs.length; i++) {
                        Map properties = new HashMap();
                        properties.put("test.statistics.distribution", distrs[i]);
                        suite.addTest(new TestProperties(new TestSuite(StatisticsTest.class, distrs[i].toString()), properties));
                }
                return suite;
        }
        public StatisticsTest(String name) {
                super(name);
        }
        protected void setUp() {
                JSci.GlobalSettings.ZERO_TOL=1.0e-8;
                distribution = (ProbabilityDistribution) TestProperties.getProperties().get("test.statistics.distribution");
        }
        /**
        * Test inverse of cumulative distribution function.
        */
        public void testLeftInverse() {
                double expected = Math.random();
                double actual = distribution.inverse(distribution.cumulative(expected));
                assertEquals(distribution.toString(), expected, actual, JSci.GlobalSettings.ZERO_TOL);
        }
        /**
        * Test inverse of cumulative distribution function.
        */
        public void testRightInverse() {
                double expected = Math.random();
                double actual = distribution.cumulative(distribution.inverse(expected));
                assertEquals(distribution.toString(), expected, actual, JSci.GlobalSettings.ZERO_TOL);
        }
}

