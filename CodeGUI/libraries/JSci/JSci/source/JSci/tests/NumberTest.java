package JSci.tests;

import JSci.maths.*;
import JSci.maths.fields.*;

/**
 * Testcase for Number subclasses.
 */
public class NumberTest extends junit.framework.TestCase {
	public static void main(String[] args) {
		junit.textui.TestRunner.run(NumberTest.class);
	}
	public NumberTest(String name) {
		super(name);
	}
	protected void setUp() throws Exception {
		JSci.GlobalSettings.ZERO_TOL=1.0e-9;
		super.setUp();
	}
	public void testOne() {
		// integers are a subset of reals
		assertTrue(RealField.ONE.equals(IntegerRing.ONE));
		assertFalse(IntegerRing.ONE.equals(RealField.ONE));
	}
}
