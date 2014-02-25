package JSci.maths.symbolic;
 
import JSci.maths.*;

import JSci.maths.*;
import JSci.maths.fields.*;
import JSci.maths.groups.*;

import java.util.*;
import java.io.*;

/** The basic abstract class for doing analytical calculations.
*/

public abstract class Expression implements Field.Member {

    /** Differentiation of the expression with respect to the variables
     * @param ht table with variable / derivative order
     */
    public Expression differentiate(Hashtable ht) {
	Expression r = this;
	Variable x;
	for (Enumeration e = ht.keys();e.hasMoreElements();) {
	    x=(Variable)e.nextElement();
	    for (int n=0;n<((Integer)ht.get(x)).intValue();n++) 
		r=r.differentiate(x);
	}
	return r;
    }

    /** Differentiation of the expression with respect to a variable
     * @param x the variable
     */
    public abstract Expression differentiate(Variable x);
	
    /** This method substitutes the variable with the variable
     * values, if non-null; they can be <code>Contant</code>s or
     * other <code>Expression</code>s. Simplification is performed.
     * @return the evaluated Expression. Can be a Constant.
     */
    public abstract Expression evaluate();

    /** Get the priority of the operator described by the expression.
     * This is needed only for allowing toString()
     * to generate the parenthesis when needed. 
     * @return the priority
     */
    protected abstract int getPriority();



    ///////////////////////////////////////////////////////////////////////
    // For implementing Field

    /**
     * @return the inverse member.
     */
    public Field.Member inverse() { 
	return new Power(this,-1);
    }
    
    /**
     * The multiplication law with inverse.
     * @param f a field member
     */
    public Field.Member divide(Field.Member f) {
	Expression e;
	if (f instanceof Expression) e=(Expression)f.inverse();
	else e=new Constant(f.inverse());
	return new Product(this,e);
    }
    
    /**
     * The multiplication law.
     * @param r a ring member
     */
    public Ring.Member multiply(Ring.Member r) { 
	Expression e;
	if (r instanceof Expression) e=(Expression)r;
	else e=new Constant(r);
	return new Product(this,e);
    }

    /**
     * The group composition law.
     * @param g a group member
     */
    public AbelianGroup.Member add(AbelianGroup.Member g) {
	Expression e;
	if (g instanceof Expression) e=(Expression)g;
	else e=new Constant(g);
	return new Sum(this,e);
    }

    /**
     * Returns the negative member.
     */
    public AbelianGroup.Member negate() {
	return new Negative(this);
    }

    /**
     * The group composition law with inverse.
     * @param g a group member
     */
    public AbelianGroup.Member subtract(AbelianGroup.Member g) { 
	Expression e;
	if (g instanceof Expression) e=(Expression)g.negate();
	else e=new Constant(g.negate());
	return new Sum(this,e); 
    }   

    // other operations
    /** Power of an expression. Only constant and integer exponent.
     *  @param f the exponent.  
     */
    public Ring.Member rise(int f) {
	return new Power(this, f);
    }



    ////////////////////////////////////////////////////////////////////
    // static methods for making operations

    /** Method for generating an Expression by a formal operation on 
     * a set member
     */
    public static Expression negative(AbelianGroup.Member r) {
	if (r instanceof Expression) return (Expression)r.negate();
	else return new Constant(r.negate());
    }

    /** Method for generating an Expression by a formal operation on 
     * a set member
     */
    public static Expression sum(AbelianGroup.Member a, AbelianGroup.Member b) {
	Expression ea,eb;
	if (a instanceof Expression) ea=(Expression)a;
	else ea=new Constant(a);
	if (b instanceof Expression) eb=(Expression)b;
	else eb=new Constant(b);
	return (Expression)ea.add(eb);
    }

    /** Method for generating an Expression by a formal operation on 
     * a set member
     */
    public static Expression sum(AbelianGroup.Member[] args) {
	Expression[] exprs = new Expression[args.length];
	for (int j=0;j<args.length;j++) {
	    if (args[j] instanceof Expression) exprs[j]=(Expression)args[j];
	    else exprs[j]=new Constant(args[j]);
	}
	return new Sum(exprs);
    }

    /** Method for generating an Expression by a formal operation on 
     * a set member
     */
    public static Expression difference(AbelianGroup.Member a, AbelianGroup.Member b) {
	Expression ea,eb;
	if (a instanceof Expression) ea=(Expression)a;
	else ea=new Constant(a);
	if (b instanceof Expression) eb=(Expression)b;
	else eb=new Constant(b);
	return (Expression)ea.subtract(eb);
    }

    /** Method for generating an Expression by a formal operation on 
     * a set member
     */
    public static Expression product(Ring.Member a, Ring.Member b) {
	Expression ea,eb;
	if (a instanceof Expression) ea=(Expression)a;
	else ea=new Constant(a);
	if (b instanceof Expression) eb=(Expression)b;
	else eb=new Constant(b);
	return (Expression)ea.multiply(eb);
    }

    /** Method for generating an Expression by a formal operation on 
     * a set member
     */
    public static Expression product(Ring.Member[] args) {
	Expression[] exprs = new Expression[args.length];
	for (int j=0;j<args.length;j++) {
	    if (args[j] instanceof Expression) exprs[j]=(Expression)args[j];
	    else exprs[j]=new Constant(args[j]);
	}
	return new Product(exprs);
    }

    /** Method for generating an Expression by a formal operation on 
     * a set member
     */
    public static Expression inverse(Field.Member f) {
	if (f instanceof Expression) return (Expression)f.inverse();
	else return new Constant(f.inverse());
    } 

    /** Method for generating an Expression by a formal operation on 
     * a set member
     */
    public static Expression divide(Field.Member f,Field.Member g) {
	Expression ef,eg;
	if (f instanceof Expression) ef=(Expression)f;
	else ef=new Constant(f);
	if (g instanceof Expression) eg=(Expression)g;
	else eg=new Constant(g);
	return (Expression)ef.divide(eg);
    }

    /** Method for generating an Expression by a formal operation on 
     * a set member
     */
    public static Expression power(Field.Member f,int g) {
	Expression ef;
	if (f instanceof Expression) ef=(Expression)f;
	else ef=new Constant(f);
	return new Power(ef, g);
    }




    /** Put some expressions like "sin(2*x)/x+3/z" in the command line.
     * The program writes the expression, the simplifyed expression and
     * the derivative.
     */
    public static void main(String args[]) throws ParseException {
	for (int j=0;j<args.length;j++) {
	    Map vars=new Hashtable();
	    vars.put("y",new Variable("y",RealField.getInstance()));
	    Expression e=ExpressionParser.parse(args[j],vars);
	    System.out.println("F = "+e);
	    System.out.println("F = "+e.evaluate());
	    System.out.println("dF/dx = "+e.differentiate((Variable)vars.get("x")).evaluate());
	    System.out.println("dF/dy = "+e.differentiate((Variable)vars.get("y")).evaluate());
	    System.out.println(" ");
	}
    }

}
