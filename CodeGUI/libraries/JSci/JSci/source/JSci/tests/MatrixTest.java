package JSci.tests;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.HashMap;
import junit.framework.*;
import JSci.GlobalSettings;
import JSci.maths.*;
import JSci.maths.matrices.*;
import JSci.maths.vectors.*;
import JSci.maths.polynomials.*;

/**
* Testcase for matrices.
* @author Mark Hale
*/
public class MatrixTest extends junitx.extensions.EqualsHashCodeTestCase {
        private final int N = 10;
        private final int M = 10;
        private Class matrixClass;
        private double array[][];
        private double array2[][];

        public static void main(String arg[]) {
                junit.textui.TestRunner.run(suite());
        }
        public static Test suite() {
                Class[] classes = new Class[] {
                        DoubleMatrix.class, DoubleTridiagonalMatrix.class,
                        DoubleDiagonalMatrix.class, DoubleSparseMatrix.class,
                        DoubleSparseSquareMatrix.class,
                        DoubleFileSquareMatrix.class
                };
                TestSuite suite = new TestSuite(MatrixTest.class.toString());
                for(int i=0; i<classes.length; i++) {
                        Map properties = new HashMap();
                        properties.put("test.matrix.class", classes[i]);
                        suite.addTest(new junit.extensions.RepeatedTest(new TestProperties(new TestSuite(MatrixTest.class, classes[i].toString()), properties), 5));
                }
                return suite;
        }
        public MatrixTest(String name) {
                super(name);
        }
        protected void setUp() throws Exception {
                GlobalSettings.ZERO_TOL=1.0e-6;
                array=new double[N][M];
                array2=new double[N][M];
                matrixClass = (Class) TestProperties.getProperties().get("test.matrix.class");
                if(DiagonalMatrix.class.isAssignableFrom(matrixClass))
                        setUpDiagonal();
                else if(TridiagonalMatrix.class.isAssignableFrom(matrixClass))
                        setUpTridiagonal();
                else
                        setUpRectangular();
                super.setUp();
        }
        private void setUpRectangular() {
                for(int i=0;i<N;i++) {
                        for(int j=0;j<M;j++) {
                                array[i][j]=ExtraMath.random(-1.0, 1.0);
                                array2[i][j]=ExtraMath.random(-1.0, 1.0);
                        }
                }
        }
        private void setUpTridiagonal() {
                for(int i=0;i<N;i++) {
                        for(int j=0;j<M;j++) {
                                if(j>=i-1 && j<=i+1) {
                                        array[i][j]=ExtraMath.random(-1.0, 1.0);
                                        array2[i][j]=ExtraMath.random(-1.0, 1.0);
                                }
                        }
                }
        }
        private void setUpDiagonal() {
                for(int i=0;i<N;i++) {
                        array[i][i]=ExtraMath.random(-1.0, 1.0);
                        array2[i][i]=ExtraMath.random(-1.0, 1.0);
                }
        }
        protected Object createInstance() throws Exception {
                Constructor constructor = matrixClass.getConstructor(new Class[] {double[][].class});
                return constructor.newInstance(new Object[] {array});
        }
        protected Object createNotEqualInstance() throws Exception {
                Constructor constructor = matrixClass.getConstructor(new Class[] {double[][].class});
                return constructor.newInstance(new Object[] {array2});
        }
        protected Object createZeroInstance() throws Exception {
                if(SquareMatrix.class.isAssignableFrom(matrixClass)) {
                    Constructor constructor = matrixClass.getConstructor(new Class[] {int.class});
                    return constructor.newInstance(new Object[] {new Integer(N)});
                } else {
                    Constructor constructor = matrixClass.getConstructor(new Class[] {int.class,int.class});
                    return constructor.newInstance(new Object[] {new Integer(N),new Integer(M)});
                }
        }
        public void testConstructor() throws Exception {
                AbstractDoubleMatrix mat = (AbstractDoubleMatrix) createInstance();
                assertEquals(N, mat.rows());
                assertEquals(M, mat.columns());
                for(int i=0;i<mat.rows();i++) {
                        for(int j=0;j<mat.columns();j++) {
				String msg = mat.getClass().toString()+": ("+i+","+j+")";
                                assertEquals(msg, array[i][j], mat.getElement(i,j), GlobalSettings.ZERO_TOL);
                        }
                }
        }
        public void testSetGet() throws Exception {
                AbstractDoubleMatrix mat = (AbstractDoubleMatrix) createZeroInstance();
                // tests storage allocation for sparse
                for(int i=0;i<mat.rows();i++) {
                        for(int j=0;j<mat.columns();j++) {
                                try {
                                        mat.setElement(i,j, array[i][j]);
                                        assertEquals(array[i][j], mat.getElement(i,j), GlobalSettings.ZERO_TOL);
                                } catch(MatrixDimensionException e) {
					// ignore
				}
                        }
                }
                for(int i=0;i<array.length;i++) {
                        for(int j=0;j<array[i].length;j++) {
                                try {
                                        assertEquals(array[i][j], mat.getElement(i,j), GlobalSettings.ZERO_TOL);
                                } catch(MatrixDimensionException e) {
					// ignore
				}
                        }
                }
                // tests overwrite for sparse
                for(int i=0;i<mat.rows();i++) {
                        for(int j=0;j<mat.columns();j++) {
                                try {
                                        mat.setElement(i,j, array2[i][j]);
                                        assertEquals(array2[i][j], mat.getElement(i,j), GlobalSettings.ZERO_TOL);
                                } catch(MatrixDimensionException e) {
					// ignore
				}
                        }
                }
                for(int i=0;i<array.length;i++) {
                        for(int j=0;j<array[i].length;j++) {
                                try {
                                        assertEquals(array2[i][j], mat.getElement(i,j), GlobalSettings.ZERO_TOL);
                                } catch(MatrixDimensionException e) {
					// ignore
				}
                        }
                }
                // tests storage deallocation for sparse
                for(int i=0;i<mat.rows();i++) {
                        for(int j=0;j<mat.columns();j++) {
                                try {
                                        mat.setElement(i,j, 0.0);
                                        assertEquals(0.0, mat.getElement(i,j), GlobalSettings.ZERO_TOL);
                                } catch(MatrixDimensionException e) {
					// ignore
				}
                        }
                }
                for(int i=0;i<array.length;i++) {
                        for(int j=0;j<array[i].length;j++) {
                                try {
                                        assertEquals(0.0, mat.getElement(i,j), GlobalSettings.ZERO_TOL);
                                } catch(MatrixDimensionException e) {
					// ignore
				}
                        }
                }
        }
        public void testZeroTolerance() throws Exception {
                AbstractDoubleMatrix delta = (AbstractDoubleMatrix) createInstance();
                delta = delta.scalarMultiply(Math.random()*GlobalSettings.ZERO_TOL/delta.frobeniusNorm());
                AbstractDoubleMatrix m = (AbstractDoubleMatrix) createNotEqualInstance();
                assertTrue("m == m + delta", m.equals(m.add(delta)));
        }
        public void testAdd() throws Exception {
                AbstractDoubleMatrix mat = (AbstractDoubleMatrix) createInstance();
                AbstractDoubleMatrix ans=mat.add(mat);
                for(int i=0;i<ans.rows();i++) {
                        for(int j=0;j<ans.columns();j++) {
				String msg = mat.getClass().toString()+": ("+i+","+j+")";
                                assertEquals(msg, array[i][j]+array[i][j], ans.getElement(i,j), GlobalSettings.ZERO_TOL);
			}
                }
        }
        public void testSubtract() throws Exception {
                AbstractDoubleMatrix mat = (AbstractDoubleMatrix) createInstance();
                AbstractDoubleMatrix ans=mat.subtract(mat);
                for(int i=0;i<ans.rows();i++) {
                        for(int j=0;j<ans.columns();j++) {
				String msg = mat.getClass().toString()+": ("+i+","+j+")";
                                assertEquals(msg, array[i][j]-array[i][j], ans.getElement(i,j), GlobalSettings.ZERO_TOL);
			}
                }
        }
        public void testMultiply() throws Exception {
                AbstractDoubleMatrix mat = (AbstractDoubleMatrix) createInstance();
                AbstractDoubleMatrix ans=(AbstractDoubleMatrix)mat.multiply(mat.transpose());
                for(int i=0;i<ans.rows();i++) {
                        for(int j=0;j<ans.columns();j++) {
                                double sum=0.0;
                                for(int k=0;k<mat.columns();k++)
                                        sum+=array[i][k]*array[j][k];
				String msg = mat.getClass().toString()+": ("+i+","+j+")";
                                assertEquals(msg, sum, ans.getElement(i,j), GlobalSettings.ZERO_TOL);
                        }
                }
        }
	public void testMapElements() throws Exception {
                AbstractDoubleMatrix mat = (AbstractDoubleMatrix) createInstance();
		RealPolynomial map = new RealPolynomial(new double[] {2.0, 1.0});
		AbstractDoubleMatrix ans = mat.mapElements(map);
                for(int i=0;i<ans.rows();i++) {
                        for(int j=0;j<ans.columns();j++) {
				String msg = mat.getClass().toString()+": ("+i+","+j+")";
                                assertEquals(msg, map.map(array[i][j]), ans.getElement(i,j), GlobalSettings.ZERO_TOL);
			}
                }
	}
        public void testGMRes() throws Exception {
                AbstractDoubleMatrix mat = (AbstractDoubleMatrix) createInstance();
                AbstractDoubleVector y = JSci.util.VectorToolkit.randomVector(mat.rows());
                AbstractDoubleVector x = LinearMath.solveGMRes(mat, y, 100, GlobalSettings.ZERO_TOL);
                assertEquals(y, mat.multiply(x));
        }
}
