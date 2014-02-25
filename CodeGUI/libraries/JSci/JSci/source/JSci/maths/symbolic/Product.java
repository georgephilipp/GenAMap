package JSci.maths.symbolic;
 
import JSci.maths.*;
import JSci.maths.groups.*;
import JSci.maths.fields.*;

import java.util.*;

class Product extends Expression {

    private final List terms;

    public Product(Expression [] a) {
	terms = Arrays.asList(a);
    }

    public Product(List a) {
	terms = a;
    }

    public Product(Expression a,Expression b) {
	terms = new ArrayList();
	terms.add(a);
	terms.add(b);
    }

    public String toString() { 
	String r = "";
	Expression f;
	for (int j=0;j<terms.size();j++) {
	    if (j>0) r+="*";
	    f=(Expression)terms.get(j);
	    if (f.getPriority()<getPriority()) r+="("+f+")";
	    else  r+=""+f;
	}
	return r;
    }

    public Expression differentiate(Variable x) {
	List r = new ArrayList();
	List p;
	for (int j=0;j<terms.size();j++) {
	    p = new ArrayList();
	    for (int k=0;k<terms.size();k++) {
		if (j==k) p.add(((Expression)terms.get(k)).differentiate(x));
		else p.add(((Expression)terms.get(k)));
	    }
	    r.add(new Product(p));
	}
	return new Sum(r);
    }

    public Expression evaluate() {
	// svolge i prodotti contenuti -> t
	List t = new ArrayList();
	for (int j=0;j<terms.size();j++) {
	    Expression f=((Expression)terms.get(j)).evaluate();  // recursive
	    if (Product.class.isInstance(f))
		for (int k=0;k<((Product)f).terms.size();k++)
		    t.add(((Product)f).terms.get(k));
	    else t.add(f);
	}
	// raccoglie le costanti -> s
	Ring.Member c = null;
	List s = new ArrayList();
	for (int j=0;j<t.size();j++) {
	    Expression f=(Expression)t.get(j);
	    if (f instanceof Constant) {
		if (c==null) c=(Ring.Member)(((Constant)f).getValue());
		else c = c.multiply((Ring.Member)((Constant)f).getValue());
	    }
	    else s.add(f);
	}
	if (c!=null && ((AbelianGroup)(c.getSet())).isZero(c)) 
	    return new Constant(c);
	if (c!=null && ! (((Ring)(c.getSet())).isOne(c)))  
	    s.add(new Constant(c));
	// raduna i termini con base uguale -> h
	Hashtable h = new Hashtable();
	for (int j=0;j<s.size();j++) {
	    Expression b;
	    int e;
	    if (s.get(j) instanceof Power) {
		b = ((Power)s.get(j)).getBase();
		e = ((Power)s.get(j)).getExponent();
	    }
	    else {
		b = (Expression) s.get(j);
		e = 1;
	    }
	    if (h.containsKey(b)) 
		e+=((Integer)h.get(b)).intValue();
	    h.put(b,new Integer(e));
	}
	// traduce h -> s
	s = new  ArrayList();
	for (Enumeration fe=h.keys();fe.hasMoreElements();)  {
	    Expression b = (Expression)fe.nextElement();
	    int e = ((Integer)h.get(b)).intValue();
	    if (e!=0) {
		if (e==1) s.add(b);
		else if (e!=0) s.add(new Power(b,e));
	    }
	}
	// ritorno
	if (s.size()==0) return new Constant(((Ring)getSet()).one());
	if (s.size()==1) return (Expression)s.get(0);
	return new Product(s);
    }

    protected int getPriority() {return 10;}

    public Object getSet() { return ((Member)terms.get(0)).getSet(); }

}


