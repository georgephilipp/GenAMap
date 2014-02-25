package JSci.maths.symbolic;
 
import JSci.maths.*;
import JSci.maths.fields.*;
import JSci.maths.groups.*;

import java.util.*;

class Negative extends Expression {

    private final Expression arg;

    public Negative(Expression arg) {
	this.arg=arg;
    }

    public String toString() { 
	String r = "-";
	if (arg.getPriority()<getPriority()) r+="("+arg+")";
	else  r+=""+arg;
	return r;
    }

    public Expression differentiate(Variable x) {
	return new Negative(arg.differentiate(x));
    }

    public Expression evaluate() {
	Expression a = arg.evaluate();
	if (a instanceof Constant) {
	    AbelianGroup.Member p = (AbelianGroup.Member)((Constant)a).getValue();
	    return new Constant(p.negate());
	}
	return new Negative(a);
    }

    protected int getPriority() {return -1;}

    public Object getSet() { return arg.getSet(); }

}



