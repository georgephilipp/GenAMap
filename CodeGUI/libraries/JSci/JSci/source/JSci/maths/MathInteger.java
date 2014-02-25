package JSci.maths;

import java.lang.Comparable;

import JSci.GlobalSettings;
import JSci.maths.groups.AbelianGroup;
import JSci.maths.fields.*;

/**
* The MathInteger class encapsulates integer numbers.
* @see JSci.maths.fields.IntegerRing
* @version 1.0
* @author Mark Hale
*/
public final class MathInteger extends Number implements Comparable, Ring.Member {
        private static final long serialVersionUID = 6893485894391864141L;

        private final int x;
        /**
        * Constructs an integer number.
        */
        public MathInteger(final int num) {
                x=num;
        }
        /**
        * Constructs the integer number represented by a string.
        * @param s a string representing an integer number.
        * @exception NumberFormatException if the string does not contain a parsable number.
        */
        public MathInteger(final String s) throws NumberFormatException {
                x=Integer.parseInt(s);
        }
        /**
        * Compares two integer numbers for equality.
        * @param obj an integer number.
        */
        public boolean equals(Object obj) {
                if(obj instanceof MathInteger)
                        return x == ((MathInteger)obj).value();
                else
                        return false;
        }
	public int hashCode() {
		return x;
	}
        /**
        * Compares two integer numbers.
        * @param obj an integer number.
        * @return a negative value if <code>this&lt;obj</code>,
        * zero if <code>this==obj</code>,
        * and a positive value if <code>this&gt;obj</code>.
        */
        public int compareTo(Object obj) throws IllegalArgumentException {
                if(obj!=null && (obj instanceof MathInteger)) {
			int objValue = ((MathInteger)obj).x;
                        if(x == objValue)
                                return 0;
                        else
                                return x-objValue;
                } else
                        throw new IllegalArgumentException("Invalid object: "+obj.getClass());
        }
        /**
        * Returns a string representing the value of this integer number.
        */
        public String toString() {
                return Integer.toString(x);
        }
        /**
        * Returns the integer value.
        */
        public int value() {
                return x;
        }
        public int intValue() {
                return x;
        }
        public long longValue() {
                return x;
        }
        public float floatValue() {
                return x;
        }
        public double doubleValue() {
                return x;
        }
        /**
        * Returns true if this number is even.
        */
        public boolean isEven() {
                return (x&1)==0;
        }
        /**
        * Returns true if this number is odd.
        */
        public boolean isOdd() {
                return (x&1)==1;
        }
	public Object getSet() {
		return IntegerRing.getInstance();
	}
        /**
        * Returns the negative of this number.
        */
        public AbelianGroup.Member negate() {
                return new MathInteger(-x);
        }
        /**
        * Returns the addition of this number and another.
        */
        public AbelianGroup.Member add(final AbelianGroup.Member n) {
                if(n instanceof MathInteger)
                        return add((MathInteger)n);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method: "+n.getClass());
        }
        /**
        * Returns the addition of this integer number and another.
        */
        public MathInteger add(final MathInteger n) {
                return add(n.x);
        }
	public MathInteger add(int y) {
		return new MathInteger(x+y);
	}
        /**
        * Returns the subtraction of this number and another.
        */
        public AbelianGroup.Member subtract(final AbelianGroup.Member n) {
                if(n instanceof MathInteger)
                        return subtract((MathInteger)n);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method: "+n.getClass());
        }
        /**
        * Returns the subtraction of this integer number and another.
        * @param n an integer number.
        */
        public MathInteger subtract(final MathInteger n) {
                return subtract(n.x);
        }
	public MathInteger subtract(int y) {
		return new MathInteger(x-y);
	}
        /**
        * Returns the multiplication of this number and another.
        */
        public Ring.Member multiply(final Ring.Member n) {
                if(n instanceof MathInteger)
                        return multiply((MathInteger)n);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method: "+n.getClass());
        }
        /**
        * Returns the multiplication of this integer number and another.
        * @param n an integer number.
        */
        public MathInteger multiply(final MathInteger n) {
                return multiply(n.x);
        }
	public MathInteger multiply(int y) {
		return new MathInteger(x*y);
	}
        /**
        * Returns this integer number raised to the power of another.
        * @param n an integer number.
        */
        public MathInteger pow(final MathInteger n) {
                if(n.x==0)
                        return IntegerRing.ONE;
                else {
                        int ans=x;
                        for(int i=1;i<n.x;i++)
                                ans*=x;
                        return new MathInteger(ans);
                }
        }
}
