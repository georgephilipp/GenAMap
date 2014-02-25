package JSci.io;

import java.lang.Comparable;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import JSci.maths.*;
import JSci.maths.matrices.*;
import JSci.maths.vectors.*;
import JSci.maths.groups.AbelianGroup;
import JSci.maths.algebras.*;
import JSci.maths.fields.*;

/**
* The MathMLExpression class is used by the MathMLParser to
* encapsulate math expressions described by the <code>&lt;apply&gt;</code> tag.
* @version 0.8
* @author Mark Hale
*/
public final class MathMLExpression extends Object {
        private String operation;
        private List args=new ArrayList();

        /**
        * Constructs a MathML expression.
        */
        public MathMLExpression() {}
        /**
        * Set the operation to be applied to the arguments.
        * @param op a MathML tag name
        * (<code>plus</code>, <code>minus</code>, <code>times</code>, <code>divide</code>, etc).
        */
        public void setOperation(String op) {
                operation=op;
        }
        /**
        * Returns the operation to be applied to the arguments.
        * @return a MathML tag name.
        */
        public String getOperation() {
                return operation;
        }
        /**
        * Adds an argument to this expression.
        */
        public void addArgument(Object obj) {
                args.add(obj);
        }
        /**
        * Returns an argument from this expression.
        */
        public Object getArgument(int n) {
                return args.get(n);
        }
        /**
        * Returns the number of arguments.
        */
        public int length() {
                return args.size();
        }
        /**
        * Substitutes a value for a variable in this expression.
        * @param var the variable to substitute for.
        * @param value the value of the variable.
        * @return the expression after substitution.
        */
        public MathMLExpression substitute(String var,Object value) {
                MathMLExpression subExpr=new MathMLExpression();
                subExpr.operation=operation;
                Object arg;
                for(int i=0;i<length();i++) {
                        arg=getArgument(i);
                        if(arg instanceof MathMLExpression)
                                arg=((MathMLExpression)arg).substitute(var,value);
                        else if(arg.equals(var))
                                arg=value;
                        subExpr.addArgument(arg);
                }
                return subExpr;
        }
        /**
        * Substitutes several values for variables in this expression.
        * @param vars a hashtable of variables and values to substitute.
        * @return the expression after substitution.
        */
        public MathMLExpression substitute(Map vars) {
                MathMLExpression subExpr=new MathMLExpression();
                subExpr.operation=operation;
                Object arg;
                for(int i=0;i<length();i++) {
                        arg=getArgument(i);
                        if(arg instanceof MathMLExpression)
                                arg=((MathMLExpression)arg).substitute(vars);
                        else if(vars.containsKey(arg))
                                arg=vars.get(arg);
                        subExpr.addArgument(arg);
                }
                return subExpr;
        }
        /**
        * Evaluates this expression.
        */
        public Object evaluate() {
                if(length()==1)
                        return unaryEvaluate();
                else if(length()==2)
                        return binaryEvaluate();
                else
                        return nAryEvaluate();
        }
        /**
        * Evaluates unary expressions.
        */
        private Object unaryEvaluate() {
                Object value=getArgument(0);
                if(value instanceof MathMLExpression)
                        value=((MathMLExpression)value).evaluate();
                if(operation.equals("abs")) {
                        if(value instanceof Number)
                                value=new MathDouble(Math.abs(((Number)value).doubleValue()));
                        else if(value instanceof Complex)
                                value=new MathDouble(((Complex)value).mod());
                        else
                                value = "|"+value+"|";
                } else if(operation.equals("minus")) {
                    if(value instanceof AbelianGroup.Member)
                        value = ((AbelianGroup.Member)value).negate();
                    else
                        value = "-("+value+")";
                } else if(operation.equals("arg")) {
                        if(value instanceof Number) {
                                if(((Number)value).doubleValue()>=0.0)
                                        value=RealField.ZERO;
                                else
                                        value=RealField.PI;
                        } else if(value instanceof Complex) {
                                value=new MathDouble(((Complex)value).arg());
                        } else
                                value = "arg("+value+")";
                } else if(operation.equals("real")) {
                        if(value instanceof Complex)
                                value=new MathDouble(((Complex)value).real());
                        else if(value instanceof AbstractComplexVector)
                                value=((AbstractComplexVector)value).real();
                        else if(value instanceof AbstractComplexMatrix)
                                value=((AbstractComplexMatrix)value).real();
                        else
                                value = "re("+value+")";
                } else if(operation.equals("imaginary")) {
                        if(value instanceof Complex)
                                value=new MathDouble(((Complex)value).imag());
                        else if(value instanceof AbstractComplexVector)
                                value=((AbstractComplexVector)value).imag();
                        else if(value instanceof AbstractComplexMatrix)
                                value=((AbstractComplexMatrix)value).imag();
                        else
                                value = "im("+value+")";
                } else if(operation.equals("conjugate")) {
                        if(value instanceof Complex)
                                value=((Complex)value).conjugate();
                        else if(value instanceof AbstractComplexVector)
                                value=((AbstractComplexVector)value).conjugate();
                        else if(value instanceof AbstractComplexMatrix)
                                value=((AbstractComplexMatrix)value).conjugate();
                        else
                                value = "("+value+")*";
                } else if(operation.equals("transpose")) {
                        if(value instanceof Matrix)
                                value=((Matrix)value).transpose();
                        else
                                value = "("+value+")^T";
                } else if(operation.equals("exp")) {
                        if(value instanceof Number)
                                value=MathDouble.exp((Number)value);
                        else if(value instanceof Complex)
                                value=Complex.exp((Complex)value);
                        else
                                value = "exp("+value+")";
                } else if(operation.equals("ln")) {
                        if(value instanceof Number)
                                value=MathDouble.log((Number)value);
                        else if(value instanceof Complex)
                                value=Complex.log((Complex)value);
                        else
                                value = "log("+value+")";
                } else if(operation.equals("sin")) {
                        if(value instanceof Number)
                                value=MathDouble.sin((Number)value);
                        else if(value instanceof Complex)
                                value=Complex.sin((Complex)value);
                        else
                                value = "sin("+value+")";
                } else if(operation.equals("cos")) {
                        if(value instanceof Number)
                                value=MathDouble.cos((Number)value);
                        else if(value instanceof Complex)
                                value=Complex.cos((Complex)value);
                        else
                                value = "cos("+value+")";
                } else if(operation.equals("tan")) {
                        if(value instanceof Number)
                                value=MathDouble.tan((Number)value);
                        else if(value instanceof Complex)
                                value=Complex.tan((Complex)value);
                        else
                                value = "tan("+value+")";
                } else if(operation.equals("arcsin")) {
                        if(value instanceof Number)
                                value=MathDouble.asin((Number)value);
                        else if(value instanceof Complex)
                                value=Complex.asin((Complex)value);
                        else
                                value = "arcsin("+value+")";
                } else if(operation.equals("arccos")) {
                        if(value instanceof Number)
                                value=MathDouble.acos((Number)value);
                        else if(value instanceof Complex)
                                value=Complex.acos((Complex)value);
                        else
                                value = "arccos("+value+")";
                } else if(operation.equals("arctan")) {
                        if(value instanceof Number)
                                value=MathDouble.atan((Number)value);
                        else if(value instanceof Complex)
                                value=Complex.atan((Complex)value);
                        else
                                value = "arctan("+value+")";
                } else if(operation.equals("sinh")) {
                        if(value instanceof Number)
                                value=MathDouble.sinh((Number)value);
                        else if(value instanceof Complex)
                                value=Complex.sinh((Complex)value);
                        else
                                value = "sinh("+value+")";
                } else if(operation.equals("cosh")) {
                        if(value instanceof Number)
                                value=MathDouble.cosh((Number)value);
                        else if(value instanceof Complex)
                                value=Complex.cosh((Complex)value);
                        else
                                value = "cosh("+value+")";
                } else if(operation.equals("tanh")) {
                        if(value instanceof Number)
                                value=MathDouble.tanh((Number)value);
                        else if(value instanceof Complex)
                                value=Complex.tanh((Complex)value);
                        else
                                value = "tanh("+value+")";
                } else if(operation.equals("factorial")) {
                        if(value instanceof Number)
                                value=new MathDouble(ExtraMath.factorial(((Number)value).doubleValue()));
                        else
                                value = "("+value+")!";
                } else if(operation.equals("not")) {
                        if(value instanceof Boolean)
                                value=new Boolean(!((Boolean)value).booleanValue());
                        else
                                value = "!("+value+")";
                }
                return value;
        }
        /**
        * Evaluates binary expressions.
        */
        private Object binaryEvaluate() {
                Object value=getArgument(0);
                if(value instanceof MathMLExpression)
                        value=((MathMLExpression)value).evaluate();
                Object next=getArgument(1);
                if(next instanceof MathMLExpression)
                        next=((MathMLExpression)next).evaluate();
                if(operation.equals("minus")) {
                        if(value instanceof AbelianGroup.Member && next instanceof AbelianGroup.Member)
                                value=((AbelianGroup.Member)value).subtract((AbelianGroup.Member)next);
                        else
                                value = "("+value+")-("+next+")";
                } else if(operation.equals("divide")) {
                        if(next instanceof Field.Member) {
                                if(value instanceof Field.Member)
                                        value=((Field.Member)value).divide((Field.Member)next);
                                else if(value instanceof VectorSpace.Member)
                                        value=((VectorSpace.Member)value).scalarDivide((Field.Member)next);
                                else
                                    value = "("+value+")/("+next+")";
                        } else
                                value = "("+value+")/("+next+")";
                } else if(operation.equals("scalarproduct")) {
                        if(value instanceof AbstractDoubleVector && next instanceof AbstractDoubleVector)
                                value=new MathDouble(((AbstractDoubleVector)value).scalarProduct((AbstractDoubleVector)next));
                        else if(value instanceof AbstractComplexVector && next instanceof AbstractComplexVector)
                                value=((AbstractComplexVector)value).scalarProduct((AbstractComplexVector)next);
                        else
                                value = "("+value+").("+next+")";
                } else if(operation.equals("vectorproduct")) {
                        if(value instanceof Double3Vector && next instanceof Double3Vector)
                                value=((Double3Vector)value).multiply((Double3Vector)next);
                        else if(value instanceof Complex3Vector && next instanceof Complex3Vector)
                                value=((Complex3Vector)value).multiply((Complex3Vector)next);
                        else
                                value = "("+value+")x("+value+")";
                } else if(operation.equals("power")) {
                    if(value instanceof Number && next instanceof Number)
                        value=new MathDouble(Math.pow(((Number)value).doubleValue(),((Number)next).doubleValue()));
                        else
                                value = "("+value+")^("+value+")";
                } else if(operation.equals("neq")) {
                        value=new Boolean(!value.equals(next));
                } else
                        return nAryEvaluate();
                return value;
        }
        /**
        * Evaluates n-ary expressions.
        */
        private Object nAryEvaluate() {
                Object value=getArgument(0);
                if(value instanceof MathMLExpression)
                        value=((MathMLExpression)value).evaluate();
                if(operation.equals("plus")) {
                        for(int i=1;i<length();i++) {
                                Object next=getArgument(i);
                                if(next instanceof MathMLExpression)
                                        next=((MathMLExpression)next).evaluate();
                                if(value instanceof AbelianGroup.Member && next instanceof AbelianGroup.Member)
                                        value=((AbelianGroup.Member)value).add((AbelianGroup.Member)next);
                                else
                                        value = "("+value+")+("+next+")";
                        }
                } else if(operation.equals("times")) {
                        for(int i=1;i<length();i++) {
                                Object next=getArgument(i);
                                if(next instanceof MathMLExpression)
                                        next=((MathMLExpression)next).evaluate();
                                if(value instanceof Ring.Member && next instanceof Ring.Member)
                                        value=((Ring.Member)value).multiply((Ring.Member)next);
                                else if(value instanceof Module.Member && next instanceof Module.Member)
                                        value=((Module.Member)next).scalarMultiply((Ring.Member)value);
                                else
                                        value = "("+value+")*("+next+")";
                        }
                } else if(operation.equals("min")) {
                        for(int i=1;i<length();i++) {
                                Object next=getArgument(i);
                                if(next instanceof MathMLExpression)
                                        next=((MathMLExpression)next).evaluate();
                                if(value instanceof Comparable)
                                {
                                    if(((Comparable)value).compareTo(next)>0)
                                            value=next;
                                }
                        }
                } else if(operation.equals("max")) {
                        for(int i=1;i<length();i++) {
                                Object next=getArgument(i);
                                if(next instanceof MathMLExpression)
                                        next=((MathMLExpression)next).evaluate();
                                if(value instanceof Comparable)
                                {
                                    if(((Comparable)value).compareTo(next)<0)
                                            value=next;
                                }
                        }
                } else if(operation.equals("mean")) {
                        value=new MathDouble(mean());
                } else if(operation.equals("sdev")) {
                        value=new MathDouble(Math.sqrt(variance()));
                } else if(operation.equals("var")) {
                        value=new MathDouble(variance());
                } else if(operation.equals("median")) {
                        double nums[]=new double[length()];
                        nums[0]=((Number)value).doubleValue();
                        for(int i=1;i<nums.length;i++) {
                                Object next=getArgument(i);
                                if(next instanceof MathMLExpression)
                                        next=((MathMLExpression)next).evaluate();
                                nums[i]=((Number)next).doubleValue();
                        }
                        value=new MathDouble(ArrayMath.median(nums));
                } else if(operation.equals("union")) {
                        if(value instanceof MathSet) {
                                MathSet ans=(MathSet)value;
                                for(int i=1;i<length();i++) {
                                        Object next=getArgument(i);
                                        if(next instanceof MathMLExpression)
                                                next=((MathMLExpression)next).evaluate();
                                        if(next instanceof MathSet)
                                                ans=ans.union((MathSet)next);
                                }
                                value=ans;
                        }
                } else if(operation.equals("intersect")) {
                        if(value instanceof MathSet) {
                                MathSet ans=(MathSet)value;
                                for(int i=1;i<length();i++) {
                                        Object next=getArgument(i);
                                        if(next instanceof MathMLExpression)
                                                next=((MathMLExpression)next).evaluate();
                                        if(next instanceof MathSet)
                                                ans=ans.intersect((MathSet)next);
                                }
                                value=ans;
                        }
                } else if(operation.equals("and")) {
                        if(value instanceof Boolean) {
                                boolean ans=((Boolean)value).booleanValue();
                                for(int i=1;i<length();i++) {
                                        Object next=getArgument(i);
                                        if(next instanceof MathMLExpression)
                                                next=((MathMLExpression)next).evaluate();
                                        if(next instanceof Boolean)
                                                ans&=((Boolean)next).booleanValue();
                                }
                                value=new Boolean(ans);
                        }
                } else if(operation.equals("or")) {
                        if(value instanceof Boolean) {
                                boolean ans=((Boolean)value).booleanValue();
                                for(int i=1;i<length();i++) {
                                        Object next=getArgument(i);
                                        if(next instanceof MathMLExpression)
                                                next=((MathMLExpression)next).evaluate();
                                        if(next instanceof Boolean)
                                                ans|=((Boolean)next).booleanValue();
                                }
                                value=new Boolean(ans);
                        }
                } else if(operation.equals("xor")) {
                        if(value instanceof Boolean) {
                                boolean ans=((Boolean)value).booleanValue();
                                for(int i=1;i<length();i++) {
                                        Object next=getArgument(i);
                                        if(next instanceof MathMLExpression)
                                                next=((MathMLExpression)next).evaluate();
                                        if(next instanceof Boolean)
                                                ans^=((Boolean)next).booleanValue();
                                }
                                value=new Boolean(ans);
                        }
                } else if(operation.equals("eq")) {
                        Object arg1=value;
                        boolean ans=true;
                        for(int i=1;i<length();i++) {
                                Object arg2=getArgument(i);
                                if(arg2 instanceof MathMLExpression)
                                        arg2=((MathMLExpression)arg2).evaluate();
                                ans&=arg1.equals(arg2);
                                arg1=arg2;
                        }
                        value=new Boolean(ans);
                } else if(operation.equals("lt")) {
                        if(value instanceof Comparable) {
                                Comparable arg1=(Comparable)value;
                                boolean ans=true;
                                for(int i=1;i<length();i++) {
                                        Object arg2=getArgument(i);
                                        if(arg2 instanceof MathMLExpression)
                                                arg2=((MathMLExpression)arg2).evaluate();
                                        ans&=arg1.compareTo(arg2)<0;
                                        if(arg2 instanceof Comparable)
                                                arg1=(Comparable)arg2;
                                }
                                value=new Boolean(ans);
                        }
                } else if(operation.equals("leq")) {
                        if(value instanceof Comparable) {
                                Comparable arg1=(Comparable)value;
                                boolean ans=true;
                                for(int i=1;i<length();i++) {
                                        Object arg2=getArgument(i);
                                        if(arg2 instanceof MathMLExpression)
                                                arg2=((MathMLExpression)arg2).evaluate();
                                        ans&=arg1.compareTo(arg2)<=0;
                                        if(arg2 instanceof Comparable)
                                                arg1=(Comparable)arg2;
                                }
                                value=new Boolean(ans);
                        }
                } else if(operation.equals("gt")) {
                        if(value instanceof Comparable) {
                                Comparable arg1=(Comparable)value;
                                boolean ans=true;
                                for(int i=1;i<length();i++) {
                                        Object arg2=getArgument(i);
                                        if(arg2 instanceof MathMLExpression)
                                                arg2=((MathMLExpression)arg2).evaluate();
                                        ans&=arg1.compareTo(arg2)>0;
                                        if(arg2 instanceof Comparable)
                                                arg1=(Comparable)arg2;
                                }
                                value=new Boolean(ans);
                        }
                } else if(operation.equals("geq")) {
                        if(value instanceof Comparable) {
                                Comparable arg1=(Comparable)value;
                                boolean ans=true;
                                for(int i=1;i<length();i++) {
                                        Object arg2=getArgument(i);
                                        if(arg2 instanceof MathMLExpression)
                                                arg2=((MathMLExpression)arg2).evaluate();
                                        ans&=arg1.compareTo(arg2)>=0;
                                        if(arg2 instanceof Comparable)
                                                arg1=(Comparable)arg2;
                                }
                                value=new Boolean(ans);
                        }
                }
                return value;
        }
        /**
        * Calculates the mean for this expression.
        */
        private double mean() {
                double sum=((Number)getArgument(0)).doubleValue();
                for(int i=1;i<length();i++) {
                        Object next=getArgument(i);
                        if(next instanceof MathMLExpression)
                                next=((MathMLExpression)next).evaluate();
                        sum+=((Number)next).doubleValue();
                }
                return sum/length();
        }
        /**
        * Calculates the variance for this expression.
        */
        private double variance() {
                final double m=mean();
                double num=((Number)getArgument(0)).doubleValue();
                double ans=(num-m)*(num-m);
                for(int i=1;i<length();i++) {
                        Object next=getArgument(i);
                        if(next instanceof MathMLExpression)
                                next=((MathMLExpression)next).evaluate();
                        num=((Number)next).doubleValue();
                        ans+=(num-m)*(num-m);
                }
                return ans/(length()-1);
        }
}
