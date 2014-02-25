package JSci.tests;

import JSci.io.TextReader;
import JSci.io.TextWriter;
import JSci.maths.matrices.*;
import JSci.util.MatrixToolkit;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.IOException;
import junit.framework.*;

/**
 * Testcase for TextReader/TextWriter.
 * @author Mark
 */
public class TextIOTest extends junit.framework.TestCase {
        public static void main(String arg[]) {
                junit.textui.TestRunner.run(TextIOTest.class);
        }
        public TextIOTest(String name) {
                super(name);
        }

        public void testArray2D() throws IOException {
            double[][] data = {{3.45}, {54.776, 16.045}, {-23.6,2.45,-13.67}};

            StringWriter buf = new StringWriter();
            TextWriter writer = new TextWriter(buf, ',');
            writer.write(data);
            writer.close();

            TextReader reader = new TextReader(new StringReader(buf.toString()));
            double[][] actual = reader.readArray();
            reader.close();

            assertEquals(data, actual);
        }
        public void testMatrix() throws IOException {
            AbstractDoubleMatrix m = MatrixToolkit.randomSquareMatrix(5);

            StringWriter buf = new StringWriter();
            TextWriter writer = new TextWriter(buf, ',');
            writer.write(m);
            writer.close();

            TextReader reader = new TextReader(new StringReader(buf.toString()));
            AbstractDoubleSquareMatrix actual = new DoubleSquareMatrix(m.rows());
            reader.read(actual);
            reader.close();

            assertEquals(m, actual);
        }
        public void testMatrixTable() throws IOException {
            AbstractDoubleMatrix m = MatrixToolkit.wilkinsonMatrix(7);

            StringWriter buf = new StringWriter();
            TextWriter writer = new TextWriter(buf, ',');
            writer.writeTable(m);
            writer.close();

            TextReader reader = new TextReader(new StringReader(buf.toString()));
            AbstractDoubleMatrix actual = new DoubleTridiagonalMatrix(m.rows());
            reader.readTable(actual);
            reader.close();

            assertEquals(m, actual);
        }

        public static void assertEquals(double[][] expected, double[][] actual)
        {
            if(expected.length != actual.length)
                throw new AssertionFailedError("Lengths are not equal");
            for(int i=0; i<expected.length; i++) {
                if(expected[i].length != actual[i].length)
                    throw new AssertionFailedError("Lengths are not equal");
                for(int j=0; j<expected[i].length; j++) {
                    assertEquals(expected[i][j], actual[i][j], 0.0);
                }
            }
        }
}
