package JSci.maths.symbolic;
 
import java.util.*;
import JSci.maths.*;
import JSci.maths.groups.*;
import JSci.maths.fields.*;

/** This class wraps any constant value. Probably it will become
 * obsolete! */
public class Constant extends Expression {
    private final Member value;

    /** A constant Expression
     * @param x the constant value
     */
    public Constant(Member x) {
	value=x;
    }

    public String toString() { 
	return value.toString();
    }

    public Expression differentiate(Variable x) {
	return new Constant(((AbelianGroup)getSet()).zero());
    }

    public Expression evaluate() {
	return this;
    }

    protected int getPriority() { 
	String sv=value.toString();
        if (
            sv.indexOf("+")!=-1 ||
            sv.indexOf("-")!=-1
            ) return 0;
        if (sv.indexOf("*")!=-1) return 10;
        if (sv.indexOf("^")!=-1) return 15;
        return 20;
    }

    public Object getSet() { 
	return value.getSet(); 
    }

    public Member getValue() { return value; }

    public boolean equals(Object o) {
	Object op;
	if (o instanceof Constant) op=((Constant)o).getValue();
	else op=o;
	return (value.equals(op));
    }

}




