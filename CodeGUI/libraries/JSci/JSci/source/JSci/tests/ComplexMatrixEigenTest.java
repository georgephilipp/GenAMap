package JSci.tests;

import JSci.maths.*;
import JSci.maths.matrices.*;
import JSci.maths.vectors.AbstractComplexVector;
import JSci.maths.vectors.ComplexVector;
import JSci.util.MatrixToolkit;

/**
* Testcase for complex eigenvalue/vector methods.
* @author Mark Hale
*/
public class ComplexMatrixEigenTest extends junit.framework.TestCase {
        private final int N=5;
        private AbstractComplexSquareMatrix sqmat;
        private double eval[];
        private AbstractComplexVector evec[];

        public static void main(String arg[]) {
                junit.textui.TestRunner.run(ComplexMatrixEigenTest.class);
        }
        public ComplexMatrixEigenTest(String name) {
                super(name);
        }
        protected void setUp() {
                JSci.GlobalSettings.ZERO_TOL=1.0e-6;
                sqmat=MatrixToolkit.randomComplexSquareMatrix(N);
                sqmat=(AbstractComplexSquareMatrix) sqmat.add(sqmat.hermitianAdjoint());    // make hermitian
                eval=new double[N];
                evec=new ComplexVector[N];
                try {
                        eval=LinearMath.eigenSolveHermitian(sqmat,evec);
                } catch(MaximumIterationsExceededException e) {fail(e.toString());}
        }
        public void testEigenvectors() {
                for(int i=0;i<N;i++)
                        assertEquals(sqmat.multiply(evec[i]), evec[i].scalarMultiply(eval[i]));
        }
        public void testTrace() {
                double tr=0.0;
                for(int i=0;i<N;i++)
                        tr+=eval[i];
                assertEquals(sqmat.trace(), new Complex(tr,0.0));
        }
        public void testDet() {
                double det=1.0;
                for(int i=0;i<N;i++)
                        det*=eval[i];
                assertEquals(sqmat.det(), new Complex(det,0.0));
        }
}

