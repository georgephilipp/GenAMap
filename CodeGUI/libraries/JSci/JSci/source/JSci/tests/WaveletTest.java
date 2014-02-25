package JSci.tests;

import JSci.maths.WaveletMath;

/**
* Testcase for wavelet methods.
* @author Mark Hale
*/
public class WaveletTest extends junit.framework.TestCase {
        public static void main(String arg[]) {
                junit.textui.TestRunner.run(WaveletTest.class);
        }
        public WaveletTest(String name) {
                super(name);
        }
        protected void setUp() {
                JSci.GlobalSettings.ZERO_TOL=1.0e-6;
        }
        /**
        * This is a test that verifies perfect reconstruction
        * for a signal null near the boundaries (to avoid the
        * effects of the zero-padding we used).
        * @author Daniel Lemire
        */
        public void testReconstruction() {
                double[] data={0,0,0,1,0,0,0,0};
                double[] filter={0.48296291314,0.8365163037,0.224143868,-0.12940952255126};
                double[] lowpass=WaveletMath.downsample(filter,data);
                double[] filterh={-0.12940952255126,-0.224143868,0.8365163037,-0.48296291314};
                double[] highpass=WaveletMath.downsample(filterh,data);
                double[] reconstlow=WaveletMath.upsample(filter,lowpass);
                double[] reconsthigh=WaveletMath.upsample(filterh,highpass);
                double[] reconst=new double[data.length];
                for(int i=0;i<data.length;i++) {
                        reconst[i]=reconstlow[i]+reconsthigh[i];
                        assertEquals(data[i], reconst[i], JSci.GlobalSettings.ZERO_TOL);
                }
        }
        /**
        * This is a test to verify interpolation from a Lagrange filter.
        * @author Daniel Lemire
        */
        public void testLagrange() {
                double[] data={0,0,1,0,0};
                double[] filter={-0.0625,0,0.5625,0,1,0,0.5625,0,-0.0625};
                double[] interpol=WaveletMath.upsample(filter,data);
                for(int i=0;i<data.length;i++)
                        assertEquals(filter[i], interpol[i], JSci.GlobalSettings.ZERO_TOL);
        }
}

