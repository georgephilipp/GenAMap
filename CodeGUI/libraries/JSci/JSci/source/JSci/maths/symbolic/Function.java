package JSci.maths.symbolic;
 
import JSci.maths.*;
import JSci.maths.fields.*;
import JSci.maths.groups.*;

import java.util.*;

/** A function like sin(x), exp(x) or sqrt(x). <br>
This will be substituted by <code>Evaluation</code>.
*/
public class Function extends Expression {

    public static final String SIN = "sin";
    public static final String COS = "cos";
    public static final String TAN = "tan";
    public static final String ASIN = "asin";
    public static final String ACOS = "acos";
    public static final String ATAN = "atan";
    public static final String SINH = "sinh";
    public static final String COSH = "cosh";
    public static final String TANH = "tanh";
    public static final String ASINH = "asinh";
    public static final String ACOSH = "acosh";
    public static final String ATANH = "atanh";
    public static final String EXP = "exp";
    public static final String LOG = "log";
    public static final String SQRT = "sqrt";

    private final String type;
    private final Expression arg;

    /**
	* @param n the name (type) of the function; for example, Function.SIN
	* @param a the argument
	*/
    public Function(String n,Expression a) {
	type=n;
	arg=a;
    }

    public String toString() { return type+"("+arg+")"; }

    public int getPriority() {return 15;}

    public Expression differentiate(Variable x) {
	Expression d=null;
	if (type.equals(SIN)) d=new Function(COS,arg);
	if (type.equals(COS)) d=Expression.negative(new Function(SIN,arg));
	if (type.equals(TAN)) d=Expression.power(new Function(COS,arg),-2);
	if (type.equals(ASIN)) d=Expression.inverse(new Function(SQRT,Expression.sum( ((Ring)getSet()).one(), Expression.negative(Expression.power(arg,2)) )));
	if (type.equals(ACOS)) d=Expression.inverse(new Function(SQRT,Expression.sum( ((Ring)getSet()).one(), Expression.negative(Expression.power(arg,2)) ) ));
	if (type.equals(ATAN)) d=Expression.inverse(Expression.sum( ((Ring)getSet()).one(), Expression.power(arg,2) ));
	if (type.equals(SINH)) d=new Function(COSH,arg);
	if (type.equals(COSH)) d=new Function(SINH,arg);
	if (type.equals(TANH)) d=Expression.power(new Function(COSH,arg),-2);
	if (type.equals(ASINH)) d=Expression.inverse(new Function(SQRT,Expression.sum(((Ring)getSet()).one(),Expression.power(arg,2))));
    	if (type.equals(ACOSH)) d=Expression.inverse(new Function(SQRT,Expression.sum(((Ring)getSet()).one().negate(),Expression.power(arg,2))));
	if (type.equals(ATANH)) d=Expression.inverse(new Function(SQRT,Expression.sum(((Ring)getSet()).one(),Expression.power(arg,2).negate())));
	if (type.equals(EXP)) d=new Function(EXP,arg);
	if (type.equals(LOG)) d=Expression.inverse(arg);
	if (type.equals(SQRT)) d=Expression.inverse(Expression.product((Ring.Member)((Ring)getSet()).one().add(((Ring)getSet()).one()),new Function(SQRT,arg)));

	if (d==null) 
	    throw new IllegalArgumentException("Unknown Function type in derivative()");
	return Expression.product(d,arg.differentiate(x));
    }

    public boolean equals(Object o) {
	if (!Function.class.isInstance(o)) return false;
	Function f = (Function)o;
	return (type.equals(f.type) && arg.equals(f.arg));
    }

    public Object getSet() { return arg.getSet(); }
    
    public Expression evaluate() {
	Expression sarg = arg.evaluate();
	
	if (sarg instanceof Constant) {
	    if (((Constant)sarg).getValue() instanceof Complex) {
		Complex a = (Complex)((Constant)sarg).getValue();
		if (type.equals(SIN)) return new Constant(Complex.sin(a));
		if (type.equals(COS)) return new Constant(Complex.cos(a));
		if (type.equals(TAN)) return new Constant(Complex.tan(a));
		if (type.equals(ASIN)) return new Constant(Complex.asin(a));
		if (type.equals(ACOS)) return new Constant(Complex.acos(a));
		if (type.equals(ATAN)) return new Constant(Complex.atan(a));
		if (type.equals(SIN)) return new Constant(Complex.sinh(a));
		if (type.equals(COS)) return new Constant(Complex.cosh(a));
		if (type.equals(TAN)) return new Constant(Complex.tanh(a));
		if (type.equals(ASIN)) return new Constant(Complex.asinh(a));
		if (type.equals(ACOS)) return new Constant(Complex.acosh(a));
		if (type.equals(ATAN)) return new Constant(Complex.atanh(a));
		if (type.equals(EXP)) return new Constant(Complex.exp(a));
		if (type.equals(LOG)) return new Constant(Complex.log(a));
		if (type.equals(SQRT)) return new Constant(a.sqrt());
		throw new IllegalArgumentException("Unknown Function type in evaluate()");
	    }
	    if (((Constant)sarg).getValue() instanceof MathDouble) {
		MathDouble a = (MathDouble)((Constant)sarg).getValue();
		if (type.equals(SIN)) return new Constant(MathDouble.sin(a));
		if (type.equals(COS)) return new Constant(MathDouble.cos(a));
		if (type.equals(TAN)) return new Constant(MathDouble.tan(a));
		if (type.equals(ASIN)) return new Constant(MathDouble.asin(a));
		if (type.equals(ACOS)) return new Constant(MathDouble.acos(a));
		if (type.equals(ATAN)) return new Constant(MathDouble.atan(a));
		if (type.equals(SIN)) return new Constant(MathDouble.sinh(a));
		if (type.equals(COS)) return new Constant(MathDouble.cosh(a));
		if (type.equals(TAN)) return new Constant(MathDouble.tanh(a));
		if (type.equals(ASIN)) return new Constant(MathDouble.asinh(a));
		if (type.equals(ACOS)) return new Constant(MathDouble.acosh(a));
		if (type.equals(ATAN)) return new Constant(MathDouble.atanh(a));
		if (type.equals(EXP)) return new Constant(MathDouble.exp(a));
		if (type.equals(LOG)) return new Constant(MathDouble.log(a));
		if (type.equals(SQRT)) return new Constant(new MathDouble(Math.sqrt(a.value())));
		throw new IllegalArgumentException("Unknown Function type in evaluate()");
	    }
	    throw new IllegalArgumentException("Function argument is "+sarg.getSet()+" ; must be Complex or MathDouble");
	}

	if (sarg instanceof Function) {
	    Function f = (Function)sarg;
	    if (type.equals(SIN) && f.type.equals(ASIN)) return f.arg; 
	    if (type.equals(COS) && f.type.equals(ACOS)) return f.arg; 
	    if (type.equals(TAN) && f.type.equals(ATAN)) return f.arg; 
	    if (type.equals(ASIN) && f.type.equals(SIN)) return f.arg; 
	    if (type.equals(ACOS) && f.type.equals(COS)) return f.arg; 
	    if (type.equals(ATAN) && f.type.equals(TAN)) return f.arg; 
	    if (type.equals(SINH) && f.type.equals(ASINH)) return f.arg; 
	    if (type.equals(COSH) && f.type.equals(ACOSH)) return f.arg; 
	    if (type.equals(TANH) && f.type.equals(ATANH)) return f.arg; 
	    if (type.equals(ASINH) && f.type.equals(SINH)) return f.arg; 
	    if (type.equals(ACOSH) && f.type.equals(COSH)) return f.arg; 
	    if (type.equals(ATANH) && f.type.equals(TANH)) return f.arg; 
	    if (type.equals(EXP) && f.type.equals(LOG)) return f.arg; 
	    if (type.equals(LOG) && f.type.equals(EXP)) return f.arg;
	}

	return new Function(type,sarg);

    }

}	



