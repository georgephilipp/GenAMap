package JSci.tests;

import java.lang.reflect.Constructor;
import java.util.*;
import junit.framework.*;
import JSci.maths.*;
import JSci.maths.matrices.*;
import JSci.util.MatrixToolkit;

/**
* Testcase for matrix decomposition methods.
* @author Mark Hale
*/
public class MatrixDecompositionTest extends TestCase {
        private final int N=5;
        private Constructor dConstructor, zConstructor;
        private double dArray[][];
        private Complex zArray[][];

        public static void main(String arg[]) {
                junit.textui.TestRunner.run(suite());
        }
        public static Test suite() {
                Class[] dClasses = new Class[] {
                        DoubleSquareMatrix.class, DoubleTridiagonalMatrix.class,
                        DoubleDiagonalMatrix.class, DoubleSparseSquareMatrix.class
                };
                Class[] zClasses = new Class[] {
                        ComplexSquareMatrix.class, ComplexTridiagonalMatrix.class,
                        ComplexDiagonalMatrix.class, ComplexSquareMatrix.class // dummy
                };
                TestSuite suite = new TestSuite(MatrixDecompositionTest.class.toString());
                for(int i=0; i<dClasses.length; i++) {
                        Map properties = new HashMap();
                        properties.put("test.matrix.double.class", dClasses[i]);
                        properties.put("test.matrix.complex.class", zClasses[i]);
                        suite.addTest(new junit.extensions.RepeatedTest(new TestProperties(new TestSuite(MatrixDecompositionTest.class, dClasses[i]+", "+zClasses[i]), properties), 5));
                }
                return suite;
        }
        public MatrixDecompositionTest(String name) {
                super(name);
        }
        protected void setUp() throws Exception {
                JSci.GlobalSettings.ZERO_TOL=1.0e-6;
                dArray=new double[N][N];
                Class matrixClass = (Class) TestProperties.getProperties().get("test.matrix.double.class");
                dConstructor = matrixClass.getConstructor(new Class[] {double[][].class});
                if(DiagonalMatrix.class.isAssignableFrom(matrixClass))
                        setUpDoubleDiagonal();
                else if(TridiagonalMatrix.class.isAssignableFrom(matrixClass))
                        setUpDoubleTridiagonal();
                else
                        setUpDoubleRectangular();
                zArray=new Complex[N][N];
                matrixClass = (Class) TestProperties.getProperties().get("test.matrix.complex.class");
                zConstructor = matrixClass.getConstructor(new Class[] {Complex[][].class});
                if(DiagonalMatrix.class.isAssignableFrom(matrixClass))
                        setUpComplexDiagonal();
                else if(TridiagonalMatrix.class.isAssignableFrom(matrixClass))
                        setUpComplexTridiagonal();
                else
                        setUpComplexRectangular();
                super.setUp();
        }
        private void setUpDoubleRectangular() {
                for(int i=0;i<N;i++) {
                        for(int j=0;j<N;j++) {
                                dArray[i][j]=ExtraMath.random(-1.0, 1.0);
                        }
                }
        }
        private void setUpDoubleTridiagonal() {
                for(int i=0;i<N;i++) {
                        for(int j=0;j<N;j++) {
                                if(j>=i-1 && j<=i+1) {
                                        dArray[i][j]=ExtraMath.random(-1.0, 1.0);
                                }
                        }
                }
        }
        private void setUpDoubleDiagonal() {
                for(int i=0;i<N;i++) {
                        dArray[i][i]=ExtraMath.random(-1.0, 1.0);
                }
        }
        private void setUpComplexRectangular() {
                for(int i=0;i<N;i++) {
                        for(int j=0;j<N;j++) {
                                zArray[i][j]=new Complex(dArray[i][j], ExtraMath.random(-1.0, 1.0));
                        }
                }
        }
        private void setUpComplexTridiagonal() {
                for(int i=0;i<N;i++) {
                        for(int j=0;j<N;j++) {
                                if(j>=i-1 && j<=i+1) {
                                        zArray[i][j]=new Complex(dArray[i][j], ExtraMath.random(-1.0, 1.0));
                                }
                        }
                }
        }
        private void setUpComplexDiagonal() {
                for(int i=0;i<N;i++) {
                        zArray[i][i]=new Complex(dArray[i][i], ExtraMath.random(-1.0, 1.0));
                }
        }

        protected AbstractDoubleSquareMatrix createDoubleInstance() {
                try {
                        return (AbstractDoubleSquareMatrix) dConstructor.newInstance(new Object[] {dArray});
                } catch(Exception e) {
                        throw new RuntimeException(e);
                }
        }
        protected AbstractComplexSquareMatrix createComplexInstance() {
                try {
                        return (AbstractComplexSquareMatrix) zConstructor.newInstance(new Object[] {zArray});
                } catch(Exception e) {
                        throw new RuntimeException(e);
                }
        }
        
        
        
        public void testLU_pivot() {
                AbstractDoubleSquareMatrix mat= createDoubleInstance();
                int p[]=new int[N+1];
                AbstractDoubleSquareMatrix luArray[]=mat.luDecompose(p);
                AbstractDoubleSquareMatrix lu=luArray[0].multiply(luArray[1]);
                double pmatArray[][]=new double[N][N];
                for(int j,i=0;i<N;i++) {
                        for(j=0;j<N;j++)
                                pmatArray[i][j]=mat.getElement(p[i],j);
                }
                DoubleSquareMatrix pmat=new DoubleSquareMatrix(pmatArray);
                assertEquals(pmat, lu);
        }
        public void testLU() {
                AbstractDoubleSquareMatrix mat= createDoubleInstance();
                AbstractDoubleSquareMatrix luArray[]=mat.luDecompose();
                AbstractDoubleSquareMatrix lu=luArray[0].multiply(luArray[1]);
                assertEquals(lu, mat); // square.equals(subclass)
        }
        public void testCholesky() {
                AbstractDoubleSquareMatrix mat= createDoubleInstance();
                mat=(AbstractDoubleSquareMatrix)mat.multiply(mat.transpose());    // make symmetric and positive
                AbstractDoubleSquareMatrix lu[]=mat.choleskyDecompose();
                assertEquals(mat, lu[0].multiply(lu[1]));
        }
        public void testQR() {
                AbstractDoubleSquareMatrix mat= createDoubleInstance();
                AbstractDoubleSquareMatrix qrArray[]=mat.qrDecompose();
                AbstractDoubleSquareMatrix qr=qrArray[0].multiply(qrArray[1]);
                assertEquals(qr, mat); // square.equals(subclass)
        }
        public void testSVD() {
                AbstractDoubleSquareMatrix mat= createDoubleInstance();
                AbstractDoubleSquareMatrix svdArray[]=mat.singularValueDecompose();
                AbstractDoubleSquareMatrix svd=(AbstractDoubleSquareMatrix)svdArray[0].multiply(svdArray[1]).multiply(svdArray[2].transpose());
                assertEquals(svd, mat); // square.equals(subclass)
        }
        public void testInverse() {
                AbstractDoubleSquareMatrix mat= createDoubleInstance();
                AbstractDoubleSquareMatrix inv=mat.inverse();
                assertEquals(mat.multiply(inv), DoubleDiagonalMatrix.identity(N)); // square.equals(subclass)
        }
        public void testComplexLU_pivot() {
                AbstractComplexSquareMatrix mat=createComplexInstance();
                int p[]=new int[N+1];
                AbstractComplexSquareMatrix luArray[]=mat.luDecompose(p);
                AbstractComplexSquareMatrix lu=luArray[0].multiply(luArray[1]);
                Complex pmatArray[][]=new Complex[N][N];
                for(int j,i=0;i<N;i++) {
                        for(j=0;j<N;j++)
                                pmatArray[i][j]=mat.getElement(p[i],j);
                }
                ComplexSquareMatrix pmat=new ComplexSquareMatrix(pmatArray);
                assertEquals(pmat, lu);
        }
        public void testComplexLU() {
                AbstractComplexSquareMatrix mat=createComplexInstance();
                AbstractComplexSquareMatrix luArray[]=mat.luDecompose();
                AbstractComplexSquareMatrix lu=luArray[0].multiply(luArray[1]);
                assertEquals(lu, mat); // square.equals(subclass)
        }
        public void testComplexInverse() {
                AbstractComplexSquareMatrix mat=createComplexInstance();
                AbstractComplexSquareMatrix inv=mat.inverse();
                assertEquals(mat.multiply(inv), ComplexDiagonalMatrix.identity(N)); // square.equals(subclass)
        }
}
