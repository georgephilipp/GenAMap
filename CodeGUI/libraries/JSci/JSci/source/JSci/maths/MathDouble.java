package JSci.maths;

import java.lang.Comparable;
import java.lang.Double;

import JSci.GlobalSettings;
import JSci.maths.groups.AbelianGroup;
import JSci.maths.fields.*;

/**
* The MathDouble class encapsulates double numbers.
* Methods will automatically promote objects from subsets.
* @see JSci.maths.fields.RealField
* @version 1.1
* @author Mark Hale
*/
public final class MathDouble extends Number implements Comparable, Field.Member {
        private static final long serialVersionUID = 8616680319093653108L;

        private final double x;
        /**
        * Constructs a double number.
        */
        public MathDouble(final double num) {
                x=num;
        }
        /**
        * Constructs the double number represented by a string.
        * @param s a string representing a double number.
        * @exception NumberFormatException if the string does not contain a parsable number.
        */
        public MathDouble(final String s) throws NumberFormatException {
                x=Double.parseDouble(s);
        }
        /**
        * Compares two numbers for equality.
        * @param obj a number.
        */
        public boolean equals(Object obj) {
		return equals(obj, GlobalSettings.ZERO_TOL);
        }
	public boolean equals(Object obj, double tol) {
                if(obj instanceof Number) {
                        return Math.abs(x-((Number)obj).doubleValue()) <= tol;
                } else
                        return false;
	}
	public int hashCode() {
		return (int) x;
	}
        /**
        * Compares two numbers.
        * @param obj a number.
        * @return a negative value if <code>this&lt;obj</code>,
        * zero if <code>this==obj</code>,
        * and a positive value if <code>this&gt;obj</code>.
        */
        public int compareTo(Object obj) throws IllegalArgumentException {
                if(obj!=null && (obj instanceof Number)) {
			double objValue = ((Number)obj).doubleValue();
                        if(Math.abs(x-objValue) <= GlobalSettings.ZERO_TOL) {
                                return 0;
                        } else {
                                return (x < objValue ? -1 : 1);
                        }
                } else
                        throw new IllegalArgumentException("Invalid object: "+obj.getClass());
        }
        /**
        * Returns a string representing the value of this double number.
        */
        public String toString() {
                return Double.toString(x);
        }
        /**
        * Returns the double value.
        */
        public double value() {
                return x;
        }
        public int intValue() {
                return (int) x;
        }
        public long longValue() {
                return (long) x;
        }
        public float floatValue() {
                return (float) x;
        }
        public double doubleValue() {
                return x;
        }
        /**
        * Returns true if the number is within the zero tolerance.
        */
        public static boolean isZero(double x) {
                return (Math.abs(x) <= GlobalSettings.ZERO_TOL);
        }
        /**
        * Returns true if this number is NaN.
        */
        public boolean isNaN() {
                return (x==Double.NaN);
        }
        /**
        * Returns true if this number is infinite.
        */
        public boolean isInfinite() {
                return (x==Double.POSITIVE_INFINITY) || (x==Double.NEGATIVE_INFINITY);
        }
	public Object getSet() {
		return RealField.getInstance();
	}
        /**
        * Returns the negative of this number.
        */
        public AbelianGroup.Member negate() {
                return new MathDouble(-x);
        }
        /**
        * Returns the inverse of this number.
        */
        public Field.Member inverse() {
                return new MathDouble(1.0/x);
        }
        /**
        * Returns the addition of this number and another.
        */
        public AbelianGroup.Member add(final AbelianGroup.Member n) {
                if(n instanceof Number)
                        return add(((Number)n).doubleValue());
                else
                        throw new IllegalArgumentException("Member class not recognised by this method: "+n.getClass());
        }
        /**
        * Returns the addition of this double number and another.
        */
        public MathDouble add(final MathDouble n) {
                return add(n.x);
        }
	public MathDouble add(double y) {
		return new MathDouble(x+y);
	}
        /**
        * Returns the subtraction of this number and another.
        */
        public AbelianGroup.Member subtract(final AbelianGroup.Member n) {
                if(n instanceof Number)
                        return subtract(((Number)n).doubleValue());
                else
                        throw new IllegalArgumentException("Member class not recognised by this method: "+n.getClass());
        }
        /**
        * Returns the subtraction of this double number and another.
        */
        public MathDouble subtract(final MathDouble n) {
                return subtract(n.x);
        }
	public MathDouble subtract(double y) {
		return new MathDouble(x-y);
	}
        /**
        * Returns the multiplication of this number and another.
        */
        public Ring.Member multiply(final Ring.Member n) {
                if(n instanceof Number)
                        return multiply(((Number)n).doubleValue());
                else
                        throw new IllegalArgumentException("Member class not recognised by this method: "+n.getClass());
        }
        /**
        * Returns the multiplication of this double number and another.
        */
        public MathDouble multiply(final MathDouble n) {
                return multiply(n.x);
        }
        public MathDouble multiply(double y) {
                return new MathDouble(x*y);
        }
        /**
        * Returns the division of this number and another.
        */
        public Field.Member divide(final Field.Member n) {
                if(n instanceof Number)
                        return divide(((Number)n).doubleValue());
                else
                        throw new IllegalArgumentException("Member class not recognised by this method: "+n.getClass());
        }
        /**
        * Returns the division of this double number and another.
        */
        public MathDouble divide(final MathDouble n) {
                return divide(n.x);
        }
        public MathDouble divide(double y) {
                return new MathDouble(x/y);
        }

//===========
// FUNCTIONS
//===========

// EXP

        /**
        * Returns the exponential number e(2.718...) raised to the power of a number.
        */
        public static MathDouble exp(final MathDouble x) {
                return exp(x.x);
        }
	public static MathDouble exp(Number x) {
		return exp(x.doubleValue());
	}
	public static MathDouble exp(double x) {
		return new MathDouble(Math.exp(x));
	}

// LOG

        /**
        * Returns the natural logarithm (base e) of a number.
        */
        public static MathDouble log(final MathDouble x) {
                return log(x.x);
        }
	public static MathDouble log(Number x) {
		return log(x.doubleValue());
	}
	public static MathDouble log(double x) {
		return new MathDouble(Math.log(x));
	}

// SIN

        /**
        * Returns the trigonometric sine of an angle.
        * @param x an angle that is measured in radians
        */
        public static MathDouble sin(final MathDouble x) {
                return sin(x.x);
        }
	public static MathDouble sin(Number x) {
		return sin(x.doubleValue());
	}
	public static MathDouble sin(double x) {
		return new MathDouble(Math.sin(x));
	}

// COS

        /**
        * Returns the trigonometric cosine of an angle.
        * @param x an angle that is measured in radians
        */
        public static MathDouble cos(final MathDouble x) {
                return cos(x.x);
        }
	public static MathDouble cos(Number x) {
		return cos(x.doubleValue());
	}
	public static MathDouble cos(double x) {
		return new MathDouble(Math.cos(x));
	}

// TAN

        /**
        * Returns the trigonometric tangent of an angle.
        * @param x an angle that is measured in radians
        */
        public static MathDouble tan(final MathDouble x) {
                return tan(x.x);
        }
	public static MathDouble tan(Number x) {
		return tan(x.doubleValue());
	}
	public static MathDouble tan(double x) {
		return new MathDouble(Math.tan(x));
	}

// SINH

        /**
        * Returns the hyperbolic sine of a number.
        */
        public static MathDouble sinh(final MathDouble x) {
                return sinh(x.x);
        }
	public static MathDouble sinh(Number x) {
		return sinh(x.doubleValue());
	}
	public static MathDouble sinh(double x) {
		return new MathDouble(ExtraMath.sinh(x));
	}

// COSH

        /**
        * Returns the hyperbolic cosine of a number.
        */
        public static MathDouble cosh(final MathDouble x) {
                return cosh(x.x);
        }
	public static MathDouble cosh(Number x) {
		return cosh(x.doubleValue());
	}
	public static MathDouble cosh(double x) {
		return new MathDouble(ExtraMath.cosh(x));
	}

// TANH

        /**
        * Returns the hyperbolic tangent of a number.
        */
        public static MathDouble tanh(final MathDouble x) {
                return tanh(x.x);
        }
	public static MathDouble tanh(Number x) {
		return tanh(x.doubleValue());
	}
	public static MathDouble tanh(double x) {
		return new MathDouble(ExtraMath.tanh(x));
	}

// INVERSE SIN

        /**
        * Returns the arc sine of a number.
        */
        public static MathDouble asin(final MathDouble x) {
                return asin(x.x);
        }
	public static MathDouble asin(Number x) {
		return asin(x.doubleValue());
	}
	public static MathDouble asin(double x) {
		return new MathDouble(Math.asin(x));
	}

// INVERSE COS

        /**
        * Returns the arc cosine of a number.
        */
        public static MathDouble acos(final MathDouble x) {
                return acos(x.x);
        }
	public static MathDouble acos(Number x) {
		return acos(x.doubleValue());
	}
	public static MathDouble acos(double x) {
                return new MathDouble(Math.acos(x));
        }

// INVERSE TAN

        /**
        * Returns the arc tangent of a number.
        */
        public static MathDouble atan(final MathDouble x) {
                return atan(x.x);
        }
	public static MathDouble atan(Number x) {
		return atan(x.doubleValue());
	}
	public static MathDouble atan(double x) {
                return new MathDouble(Math.atan(x));
        }

// INVERSE SINH

        /**
        * Returns the arc hyperbolic sine of a number.
        */
        public static MathDouble asinh(final MathDouble x) {
                return asinh(x.x);
        }
	public static MathDouble asinh(Number x) {
		return asinh(x.doubleValue());
	}
	public static MathDouble asinh(double x) {
		return new MathDouble(ExtraMath.asinh(x));
	}

// INVERSE COSH

        /**
        * Returns the arc hyperbolic cosine of a number.
        */
        public static MathDouble acosh(final MathDouble x) {
                return acosh(x.x);
        }
	public static MathDouble acosh(Number x) {
		return acosh(x.doubleValue());
	}
	public static MathDouble acosh(double x) {
		return new MathDouble(ExtraMath.acosh(x));
	}

// INVERSE TANH

        /**
        * Returns the arc hyperbolic tangent of a number.
        */
        public static MathDouble atanh(final MathDouble x) {
                return atanh(x.x);
        }
	public static MathDouble atanh(Number x) {
		return atanh(x.doubleValue());
	}
	public static MathDouble atanh(double x) {
		return new MathDouble(ExtraMath.atanh(x));
	}
}
