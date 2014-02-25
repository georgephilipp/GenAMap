package JSci.maths.symbolic;
 
import JSci.maths.*;
import JSci.maths.fields.*;
import JSci.maths.groups.*;
import JSci.maths.analysis.*;

import java.util.*;

/** This class associates a <code>JSci.maths.analysis.RealFunction</code> to
 * an <code>Expression</code> argument, to generate an <code>Expression</code>.
 * <br>
 * This class will substitute the Function class.
 * <br>
 * See the <code>main()</code> for example.
 */
public class Evaluation extends Expression {

    private final RealFunction fn;
    private final Expression arg;

    /**
	* @param n the function; for example, 
	* @param a the argument
	*/
    public Evaluation(RealFunction n,Expression a) {
	fn=n;
	arg=a;
    }

    public String toString() { return "f<"+fn+">"+"("+arg+")"; }

    public int getPriority() {return 15;}

    public Expression differentiate(Variable x) {
	return Expression.product(
				  new Evaluation(fn.differentiate(),arg),
				  arg.differentiate(x)
				  );
    }

    public boolean equals(Object o) {
	if (!Evaluation.class.isInstance(o)) return false;
	Evaluation f = (Evaluation)o;
	return (fn.equals(f.fn) && arg.equals(f.arg));
    }

    public Object getSet() { return RealField.getInstance(); }
    
    public Expression evaluate() {
	Expression sarg = arg.evaluate();
	
	if (sarg instanceof Constant) {
	    MathDouble a = (MathDouble)((Constant)sarg).getValue();
	    return new Constant(new MathDouble(fn.map(a.doubleValue())));
	}

	return new Evaluation(fn,sarg);

    }

    /** An example */
    public static void main(String [] args) {
	Variable xVar = new Variable("x",RealField.getInstance());
	Expression expr = new Evaluation(new Exponential(1.0,1.0,0.0),xVar);
	System.out.println("expr = "+expr);
	xVar.setValue(new MathDouble(1.0));
	System.out.println("x = 1");
	System.out.println("expr = "+expr.evaluate());
	xVar.setValue(null);
	System.out.println("d/dx expr = "+expr.differentiate(xVar).evaluate());
    }
    

}	



