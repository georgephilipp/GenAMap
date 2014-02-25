package JSci.maths.symbolic;
 
import JSci.maths.*;
import JSci.maths.groups.*;
import java.util.*;

class Sum extends Expression {
    
    private final List terms;
    
    public Sum(Expression [] a) {
	terms = Arrays.asList(a);
    }

    public Sum(List a) {
	terms = a;
    }

    public Sum(Expression a,Expression b) {
	terms = new ArrayList();
	terms.add(a);
	terms.add(b);
    }

    public String toString() { 
	String r = "";
	Expression f;
	for (int j=0;j<terms.size();j++) {
	    if (j>0) r+="+";
	    f=(Expression)terms.get(j);
	    if (f.getPriority()<getPriority()) r+="("+f+")";
	    else  r+=""+f;
	}
	return r;
    }

    public Expression differentiate(Variable x) {
	List r = new ArrayList();
	for (int j=0;j<terms.size();j++) {
	    r.add(((Expression)terms.get(j)).differentiate(x));
	}
	return new Sum(r);
    }

    public Expression evaluate() {
	// svolge le somme contenute -> t
	List t = new ArrayList();
	for (int j=0;j<terms.size();j++) {
	    Expression f=((Expression)terms.get(j)).evaluate();  // recursive
	    if (f instanceof Sum)
		for (int k=0;k<((Sum)f).terms.size();k++)
		    t.add(((Sum)f).terms.get(k));
	    else t.add(f);
	}
	// raccoglie le costanti -> s
	AbelianGroup.Member c = null;
	List s = new ArrayList();
	for (int j=0;j<t.size();j++) {
	    Expression f=(Expression)t.get(j);
	    if (f instanceof Constant) {
		if (c==null) c=(AbelianGroup.Member)((Constant)f).getValue();
		else c = c.add((AbelianGroup.Member)((Constant)f).getValue());
	    }
	    else s.add(f);
	}
	if (c!=null && ! (((AbelianGroup)(c.getSet())).isZero(c)))
	    s.add(new Constant(c));

	// eventualmente qui si possono raccogliere i termini uguali a meno di una costante
	// AGGIUNGERE !!!

	// ritorno

	if (s.size()==0) return new Constant(((AbelianGroup)getSet()).zero());
	if (s.size()==1) return (Expression)s.get(0);
	return new Sum(s);
    }

    protected int getPriority() {return 0;}

    public Object getSet() { return ((Member)terms.get(0)).getSet(); }

}

