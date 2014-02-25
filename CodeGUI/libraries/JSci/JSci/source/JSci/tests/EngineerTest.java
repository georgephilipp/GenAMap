package JSci.tests;

import java.util.Map;
import java.util.HashMap;
import junit.framework.*;
import JSci.maths.*;

/**
* Testcase for Engineer methods.
* @author Mark Hale
*/
public class EngineerTest extends TestCase {
	private static final int N=127;
	private double[] signal = FourierTest.gaussian(N, 1.0, 8.0);

	public static void main(String arg[]) {
                junit.textui.TestRunner.run(EngineerTest.class);
        }
        public EngineerTest(String name) {
                super(name);
        }
        protected void setUp() throws Exception {
                JSci.GlobalSettings.ZERO_TOL=1.0e-10;
        }
	public void testResample() {
		double[] resample = EngineerMath.resample(signal, N);
		for(int i=0; i<N; i++)
			assertEquals(signal[i], resample[i], JSci.GlobalSettings.ZERO_TOL);

		resample = EngineerMath.resample(signal, (N+1)/2);
		for(int i=0; i<(N+1)/2; i++)
			assertEquals(signal[2*i], resample[i], JSci.GlobalSettings.ZERO_TOL);

		resample = EngineerMath.resample(signal, 2*N-1);
		for(int i=0; i<N; i++)
			assertEquals(signal[i], resample[2*i], JSci.GlobalSettings.ZERO_TOL);
        }
}
