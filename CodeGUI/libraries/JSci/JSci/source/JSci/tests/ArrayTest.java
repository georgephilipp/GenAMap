package JSci.tests;

import java.util.Random;
import JSci.maths.*;

/**
* Testcase for array methods.
* @author Mark Hale
*/
public class ArrayTest extends junit.framework.TestCase {
        private final Random random = new Random();

        public static void main(String arg[]) {
                junit.textui.TestRunner.run(ArrayTest.class);
        }
        public ArrayTest(String name) {
                super(name);
        }
        protected void setUp() {
                JSci.GlobalSettings.ZERO_TOL=1.0e-6;
        }
        public void testMedian() {
                final int N = random.nextInt(31)+1;
                double array[] = new double[N];
                array[0] = random.nextDouble();
                for(int i=1; i<N; i++) {
                        array[i] = random.nextDouble();
                        if(array[i] < array[i-1])
                                array[i]+=array[i-1];
                }
                double expected;
                if(isEven(N))
                        expected = (array[N/2-1]+array[N/2])/2.0;
                else
                        expected = array[N/2];
                randomize(array);
                double ans = ArrayMath.median(array);
                assertEquals("["+ArrayMath.toString(array)+"]", expected, ans, 0.0);
        }
        private static boolean isEven(int n) {
                return (n % 2 == 0);
        }
        private void randomize(double array[]) {
                for(int i=0; i<array.length; i++) {
                        int p = random.nextInt(array.length);
                        int q = random.nextInt(array.length);
                        double tmp = array[q];
                        array[q] = array[p];
                        array[p] = tmp;
                }
        }
}

