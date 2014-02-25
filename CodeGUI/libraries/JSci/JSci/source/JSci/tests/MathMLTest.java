package JSci.tests;

import JSci.io.*;
import java.net.URL;

public class MathMLTest extends junit.framework.TestCase {
        public static void main(String arg[]) {
                junit.textui.TestRunner.run(MathMLTest.class);
        }
        public MathMLTest(String name) {
                super(name);
        }
        protected void setUp() {
                JSci.GlobalSettings.ZERO_TOL=1.0e-9;
        }
	public void testParse() throws Exception {
		MathMLParser parser = new MathMLParser();
                final URL url = getClass().getResource("test.mml");
                if(url == null)
                    throw new Exception("test.mml not found!");
		parser.parse(url.toString());

		Object[] list = parser.translateToJSciObjects();
		for(int i=0; i<list.length; i+=2) {
			Object actual = list[i];
			Object expected = list[i+1];
			if(actual instanceof MathMLExpression)
				actual = ((MathMLExpression) actual).evaluate();
                        if(expected instanceof MathMLExpression)
                                expected = ((MathMLExpression) expected).evaluate();
			assertEquals(expected, actual);
		}
	}
        public void testParseToCode() throws Exception
        {
		MathMLParser parser = new MathMLParser();
                final URL url = getClass().getResource("test2.mml");
                if(url == null)
                    throw new Exception("test2.mml not found!");
		parser.parse(url.toString());

		Object[] list = parser.translateToJSciCode();
		for(int i=0; i<list.length; i+=2) {
			Object actual = "/*\n"+list[i]+"\n*/";
			Object expected = list[i+1];
			assertEquals(expected, actual);
		}
        }
}
