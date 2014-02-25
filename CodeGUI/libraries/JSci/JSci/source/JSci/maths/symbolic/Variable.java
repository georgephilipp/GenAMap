package JSci.maths.symbolic;
 
import JSci.maths.*;
import JSci.maths.groups.*;
import JSci.maths.fields.*;

import java.util.*;

/** Variables in an Expression. */
public class Variable extends Expression {
    private final String name;
    private final Object valueSet;
    private Member value=null;

    /** 
     * @param n the name (symbol) of the variable
     * @param valueSet the set to which the variable values belong,
     * e.g. RealField.getInstance(). Note 
     * it is not the Class of the values (odd thing indeed).
     */
    public Variable(String n,Object valueSet) {
	name=n;
	this.valueSet=valueSet;
    }

    /** Set the value of the variable. 
     * @param o the value; can be null to unset the variable
     */
    public void setValue(Member o) {
	if (o==null) { value=null; return; }
	if (valueSet.getClass().isInstance(o.getSet()))
	    value=o;
	else
	    throw new ClassCastException("Variable "+this+" set to "+o.getSet()+" value ("+valueSet+" required.");
    }

    /** Get the value of the variable. 
     * @return the value of the variable; null if the variable is unset.
     */
    public Member getValue() {
	return value;
    }

    public boolean equals(Object o) {
	if (!(o instanceof Variable)) return false;
	else return this==o;
    }

    public String toString() { return name; }

    public Expression differentiate(Variable x) {
	if (this.equals(x)) return new Constant(((Ring)valueSet).one());
	else return new Constant(((AbelianGroup)valueSet).zero());
    }

    public Expression evaluate() {
	if (value==null) return this;
	if (value instanceof Expression) return ((Expression)value).evaluate();
	return new Constant(value);
    }

    protected int getPriority() {return 20;}

    public Object getSet() { return (AbelianGroup)valueSet; }

}
