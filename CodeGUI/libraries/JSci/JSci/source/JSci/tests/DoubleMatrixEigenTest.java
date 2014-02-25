package JSci.tests;

import JSci.maths.*;
import JSci.maths.matrices.*;
import JSci.maths.vectors.AbstractDoubleVector;
import JSci.maths.vectors.DoubleVector;
import JSci.util.MatrixToolkit;
import JSci.util.VectorToolkit;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
* Testcase for double eigenvalue/vector methods.
* @author Mark Hale
*/
public class DoubleMatrixEigenTest extends junit.framework.TestCase {
        private final int N = 10;
        private AbstractDoubleSquareMatrix sqmat;
        private double eval[];
        private AbstractDoubleVector evec[];

        public static void main(String arg[]) {
                junit.textui.TestRunner.run(suite());
        }
        public static Test suite() {
                TestSuite suite = new TestSuite(DoubleMatrixEigenTest.class.toString());
                suite.addTest(new junit.extensions.RepeatedTest(new TestSuite(DoubleMatrixEigenTest.class), 5));
                return suite;
        }
        public DoubleMatrixEigenTest(String name) {
                super(name);
        }
        protected void setUp() throws MaximumIterationsExceededException {
                JSci.GlobalSettings.ZERO_TOL=1.0e-6;
                sqmat=MatrixToolkit.randomSquareMatrix(N);
                sqmat=(DoubleSquareMatrix) sqmat.add(sqmat.transpose());    // make symmetric
                eval=new double[N];
                evec=new DoubleVector[N];
                eval=LinearMath.eigenSolveSymmetric(sqmat,evec);
        }

        public void testEigenvectors() {
                for(int i=0;i<N;i++) {
                        assertEquals(sqmat.multiply(evec[i]), evec[i].scalarMultiply(eval[i]));
                }
        }
        public void testOrthogonalTransformation() {
            DoubleSquareMatrix transf = new DoubleSquareMatrix(evec);
            DoubleDiagonalMatrix diag = new DoubleDiagonalMatrix(eval);
            assertTrue(transf.inverse().multiply(sqmat).multiply(transf).equals(diag));
        }
        public void testTrace() {
                double tr=0.0;
                for(int i=0;i<N;i++) {
                        tr+=eval[i];
                }
                assertEquals(sqmat.trace(), tr, JSci.GlobalSettings.ZERO_TOL);
        }
        public void testDet() {
                double det=1.0;
                for(int i=0;i<N;i++) {
                        det*=eval[i];
                }
                assertEquals(sqmat.det(), det, JSci.GlobalSettings.ZERO_TOL);
        }

        public void testDiagonalise() throws MaximumIterationsExceededException {
            LinearMath.diagonalizeSymmetric(sqmat);
            for(int i=0; i<eval.length; i++) {
                assertEquals(eval[i], sqmat.getElement(i, i), JSci.GlobalSettings.ZERO_TOL);
            }
        }

        public void testLanczos() throws MaximumIterationsExceededException {
            AbstractDoubleVector initial = VectorToolkit.randomVector(N).normalize();
            double[] results = LinearMath.eigenvalueSolveSymmetric(sqmat, initial, N);
            results = ArrayMath.sortMaxToMin(results);
            double[] expected = ArrayMath.sortMaxToMin(eval);
            for(int i=0; i<expected.length; i++) {
                assertEquals(expected[i], results[i], JSci.GlobalSettings.ZERO_TOL);
            }
        }

        public void testRosser() throws MaximumIterationsExceededException {
            double[] rosserEigenvalues = {
                1000,
                1000,
                10*Math.sqrt(10405),
                1020,
                510+100*Math.sqrt(26),
                510-100*Math.sqrt(26),
                0,
               -10*Math.sqrt(10405)
            };
            sqmat = MatrixToolkit.rosserMatrix();
            eval = LinearMath.eigenvalueSolveSymmetric(sqmat);
            for(int i=0; i<eval.length; i++) {
                assertEquals(rosserEigenvalues[i], eval[i], JSci.GlobalSettings.ZERO_TOL);
            }
        }

        public void testWilkinson() throws MaximumIterationsExceededException {
            JSci.GlobalSettings.ZERO_TOL=1.0e-13;
            final int n = 21;
            double[] eigenvalues = {
                10.74619418290332,
                10.74619418290340
            };
            sqmat = MatrixToolkit.wilkinsonMatrix(n);
            eval = LinearMath.eigenvalueSolveSymmetric(sqmat);
            for(int i=0; i<eigenvalues.length; i++) {
                assertEquals(eigenvalues[i], eval[i], JSci.GlobalSettings.ZERO_TOL);
            }
        }
}
