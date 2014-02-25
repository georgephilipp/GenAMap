package JSci.maths.symbolic;
 
import JSci.maths.*;
import JSci.maths.fields.*;
import JSci.maths.groups.*;

import java.util.*;

class Power extends Expression {

    private final Expression base;
    private final int exponent;

    public Power(Expression b,int e) {
	base=b;
	exponent=e;
    }

    public Expression getBase() {return base;}
    public int getExponent() {return exponent;}

    public String toString() { 
	String r = "";
	int ep;
	if (base.getPriority()<getPriority()) r+="("+base+")";
	else  r+=""+base;
	r+="^";
	if (exponent>=0) ep=20; else ep=0;
	if (ep<getPriority()) r+="("+exponent+")";
	else  r+=""+exponent;
	return r;
    }

    public Expression differentiate(Variable x) {
	return new Product(new Expression[] {
	    new Constant(new MathDouble(exponent)),
	    new Power(base,exponent-1),
	    base.differentiate(x)
	});
    }

    public Expression evaluate() {
	Expression b = base.evaluate();
	if (b instanceof Constant) {
	    Field.Member p = (Field.Member)((Ring)((Constant)b).getValue().getSet()).one();
	    if (exponent>=0) {
		Ring.Member c = (Ring.Member)((Constant)b).getValue();
		for (int j=0;j<exponent;j++) 
		    p=(Field.Member)p.multiply((Field.Member)c);
	    }
	    else {
		Field.Member c = (Field.Member)((Constant)b).getValue();
		c=c.inverse();
		for (int j=0;j<-exponent;j++) p=(Field.Member)p.multiply(c);
	    }
	    return new Constant(p);
	}
	if (exponent==0) return new Constant(((Ring)base.getSet()).one());
	if (exponent==1) return base;
	return new Power(b,exponent);
    }

    protected int getPriority() {return 15;}

    public Object getSet() { return base.getSet(); }

}



