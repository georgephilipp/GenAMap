package JSci.maths.polynomials;

import JSci.GlobalSettings;
import JSci.maths.MathDouble;
import JSci.maths.analysis.RealFunction;
import JSci.maths.fields.*;
import JSci.maths.fields.Field;
import JSci.maths.groups.AbelianGroup;


/** A Polynomial as a <code>Ring.Member</code> over a <i>real</i> <code>Field</code>
 * @author b.dietrich
 * @author Mark Hale
 */
public class RealPolynomial extends RealFunction implements Polynomial {
    /** Coefficients in ascending degree order */
    private double[] _coeff;

    /** Creates a new instance of RealPolynomial */
    public RealPolynomial( double[] coeff ) {
        if ( coeff == null ) {
            throw new NullPointerException("Coefficients cannot be null");
        }
        _coeff = normalise(coeff);
    }
        /**
        * Normalises the coefficient array.
        * Trims off any leading (high degree) zero terms.
        */
        private static double[] normalise(double[] c) {
                int i = c.length-1;
                while(i >= 0 && Math.abs(c[i]) <= GlobalSettings.ZERO_TOL)
                        i--;
                if(i < 0) {
                        return new double[] {0.0};
                } else if(i < c.length-1) {
                        double[] arr = new double[i+1];
                        System.arraycopy(c, 0, arr, 0, arr.length);
                        return arr;
                } else {
                        return c;
                }
        }

    /**
     * Creates a new RealPolynomial object.
     *
     * @param f
     */
    public RealPolynomial( Field.Member[] f ) {
        if ( f == null ) {
            throw new NullPointerException("Coefficients cannot be null");
        }
        _coeff = normalise(toDoubleArray(f));
    }

        private static double[] toDoubleArray(Field.Member[] f) {
                double[] arr = new double[f.length];
                for(int i=0; i<arr.length; i++) {
                        if(f[i] instanceof MathDouble)
                                arr[i] = ((MathDouble)f[i]).value();
                        else
                                throw new IllegalArgumentException("Different fields. Argument was " + f[i].getClass());
                }
                return arr;
        }

    /** Get the coefficient of degree k, i.e. <I>a_k</I> if
     * <I>P(x)</I> := sum_{k=0}^n <I>a_k x^k</I>
     * @param k degree
     * @return coefficient as described above
     */
    public Field.Member getCoefficient( int k ) {
        return new MathDouble( getCoefficientAsDouble( k ) );
    }

    /** Get the coefficient of degree k, i.e. <I>a_k</I> if
     * <I>P(x)</I> := sum_{k=0}^n <I>a_k x^k</I> as a real number
     * @param k degree
     * @return coefficient as described above
     */
    public double getCoefficientAsDouble( int k ) {
        if ( k >= _coeff.length ) {
            return 0.0;
        } else {
            return _coeff[k];
        }
    }

    /** Get the coefficients as an array
      * @return the coefficients as an array
      */
    public Field.Member[] getCoefficients() {
        return RealPolynomialRing.toMathDouble( getCoefficientsAsDoubles() );
    }

    /** Get the coefficients as an array of doubles
     * @return the coefficients as an array
     */
    public double[] getCoefficientsAsDoubles() {
        return _coeff;
    }

        /**
        * Evaluates this polynomial.
        */
        public double map(double x) {
                return PolynomialMath.evalPolynomial(this, x);
        }

    /** The degree
     * @return the degree
     */
    public int degree() {
        return _coeff.length-1;
    }

	public Object getSet() {
		return RealPolynomialRing.getInstance();
	}

    /**                                               
     * Returns true if this polynomial is equal to zero.
     * All coefficients are tested for |a_k| < GlobalSettings.ZERO_TOL.
     * @return true if all coefficients <  GlobalSettings.ZERO_TOL
     */
    public boolean isZero() {
        for ( int k = 0; k < _coeff.length; k++ ) {
            if ( Math.abs( _coeff[k] ) > GlobalSettings.ZERO_TOL) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns true if this polynomial is equal to one.
     * It is tested, whether |a_0 - 1| <= GlobalSettings.ZERO_TOL and the remaining
     * coefficients are |a_k| < GlobalSettings.ZERO_TOL.
     * @return true if this is equal to one.
     */
    public boolean isOne() {
        if(Math.abs( _coeff[0] - 1.0 ) > GlobalSettings.ZERO_TOL)
                return false;

        for ( int k = 1; k < _coeff.length; k++ ) {
            if ( Math.abs( _coeff[k] ) > GlobalSettings.ZERO_TOL ) {
                return false;
            }
        }

        return true;
    }

    /** The group composition law. Returns a new polynom with grade = max( this.grade, g.grade)
     * @param g a group member
     *
     */
    public RealFunction add( RealFunction g ) {
        if ( g instanceof RealPolynomial ) {
            RealPolynomial p = (RealPolynomial) g;
            int maxgrade     = PolynomialMath.maxDegree( this, p );
            double[] c       = new double[maxgrade+1];
            for ( int k = 0; k < c.length; k++ ) {
                c[k] = getCoefficientAsDouble( k ) + p.getCoefficientAsDouble( k );
            }
            return new RealPolynomial( c );
        } else {
                return super.add(g);
        }
    }

    /**
     * Differentiate the real polynomial. Only useful iff the polynomial is built over
     * a Banach space and an appropriate multiplication law is provided.
     *
     * @return a new polynomial with degree = max(this.degree-1 , 0)
     */
    public RealFunction differentiate() {
        if ( degree() == 0 ) {
            return (RealPolynomial) RealPolynomialRing.getInstance().zero();
        } else {
            double[] dn = new double[degree()];
            for ( int k = 0; k < dn.length; k++ ) {
                dn[k] = getCoefficientAsDouble( k+1 ) * (k+1);
            }

            return new RealPolynomial( dn );
        }
    }

    /** return a new real Polynomial with coefficients divided by <I>f</I>
     * @param f divisor
     * @return new Polynomial with coefficients /= <I>f</I>
     */
    public Polynomial scalarDivide( Field.Member f ) {
        if ( f instanceof Number ) {
            double a = ( (Number) f ).doubleValue();

            return scalarDivide( a );
        } else {
            throw new IllegalArgumentException("Member class not recognised by this method.");
        }
    }

    /** return a new real Polynomial with coefficients divided by <I>a</I>
     * @param a divisor
     * @return new Polynomial with coefficients /= <I>a</I>
     */
    public RealPolynomial scalarDivide( double a ) {
        double[] c = new double[_coeff.length];
        for ( int k = 0; k < c.length; k++ ) {
            c[k] = _coeff[k] / a;
        }

        return new RealPolynomial( c );
    }

    /**
     * Returns true if this polynomial is equal to another.
     * @param o the other polynomial
     *
     * @return true if so
     */
    public boolean equals( Object o ) {
        if ( o == this ) {
            return true;
        } else if ( o instanceof RealPolynomial ) {
            RealPolynomial p = (RealPolynomial) o;

            return ( (RealPolynomial) this.subtract( p ) ).isZero();
        }

        return false;
    }

    /**
     * Some kind of hashcode... (Since I have an equals)
     * @return a hashcode
     */
    public int hashCode() {
        int res = 0;
        for ( int k = 0; k < _coeff.length; k++ ) {
            res += (int) ( _coeff[k] * 10.0 );
        }

        return res;
    }

    /**
     * "inverse" operation for differentiate
     * @return the integrated polynomial
     */
    public RealPolynomial integrate() {
        double[] dn = new double[_coeff.length+1];
        for ( int k = 1; k < dn.length; k++ ) {
            dn[k] = getCoefficientAsDouble( k-1 ) / k;
        }

        return new RealPolynomial( dn );
    }

    /**
     * Returns the multiplication of this polynomial by a scalar
     * @param f
     */
    public Polynomial scalarMultiply( Field.Member f ) {
        if ( f instanceof Number ) {
            double a = ( (Number) f ).doubleValue();
            return scalarMultiply( a );
        } else {
            throw new IllegalArgumentException("Member class not recognised by this method.");
        }
    }

    /**
     * Returns the multiplication of this polynomial by a scalar
    * @param a factor
    * @return new Polynomial with coefficients *= <I>a</I>
    */
    public RealPolynomial scalarMultiply( double a ) {
        double[] c = new double[_coeff.length];
        for ( int k = 0; k < c.length; k++ ) {
            c[k] = _coeff[k] * a;
        }

        return new RealPolynomial( c );
    }

    /**
     * The multiplication law. Multiplies this Polynomial with another
     * @param r a polynomial
     * @return a new Polynomial with grade = max( this.grade, r.grade) + min( this.grade, r.grade)
     */
    public RealFunction multiply( RealFunction r ) {
        if ( r instanceof RealPolynomial ) {
            RealPolynomial p = (RealPolynomial) r;
            int maxgrade     = PolynomialMath.maxDegree( this, p );
            int mingrade     = PolynomialMath.minDegree( this, p );
            int destgrade    = maxgrade + mingrade;
            double[] n       = new double[destgrade+1];
            for ( int k = 0; k < _coeff.length; k++ ) {
                for ( int j = 0; j < p._coeff.length; j++ ) {
                    n[k + j] += ( _coeff[k] * p._coeff[j] );
                }
            }

            return new RealPolynomial( n );
        } else {
            throw new IllegalArgumentException("Member class not recognised by this method.");
        }
    }

    /** Returns the inverse member. (That is mult(-1))
     * @return inverse
     */
    public AbelianGroup.Member negate() {
        double[] c = new double[_coeff.length];
        for ( int k = 0; k < c.length; k++ ) {
            c[k] = -_coeff[k];
        }

        return new RealPolynomial( c );
    }

    /** The group composition law with inverse.
     * @param g a group member
     *
     */
    public RealFunction subtract( RealFunction g ) {
        if ( g instanceof RealPolynomial ) {
            RealPolynomial p = (RealPolynomial) g;
            int maxgrade     = PolynomialMath.maxDegree( this, p );
            double[] c       = new double[maxgrade+1];
            for ( int k = 0; k < c.length; k++ ) {
                c[k] = getCoefficientAsDouble( k ) - p.getCoefficientAsDouble( k );
            }
            return new RealPolynomial( c );
        } else {
                return super.subtract(g);
        }
    }

    /**
     * String representation <I>P(x) = a_k x^k +...</I>
     * @return String
     */
    public String toString() {
        StringBuffer sb = new StringBuffer( "P(x) = " );
        if ( _coeff[degree()] < 0.0 ) {
            sb.append( "-" );
        } else {
            sb.append( " " );
        }
        for ( int k = degree(); k > 0; k-- ) {
            sb.append( Math.abs( _coeff[k] ) ).append( "x^" ).append( k )
              .append( ( _coeff[k-1] >= 0.0 ) ? " + " : " - " );
        }
        sb.append( Math.abs(_coeff[0]) );

        return sb.toString();
    }
}

