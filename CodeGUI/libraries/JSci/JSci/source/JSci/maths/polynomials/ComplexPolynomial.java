package JSci.maths.polynomials;

import JSci.GlobalSettings;
import JSci.maths.Complex;
import JSci.maths.fields.*;
import JSci.maths.groups.*;


/**
 * A Polynomial over the complex field.
 * For a description of the methods
 * @see JSci.maths.polynomials.RealPolynomial
 *
 * @author  b.dietrich
 */
public class ComplexPolynomial implements Polynomial {
    private Complex[] _coeff;

    /** Creates a new instance of ComplexPolynomial */
    public ComplexPolynomial( Complex[] coeff ) {
        if ( coeff == null ) {
            throw new NullPointerException("Coefficients cannot be null");
        }
        _coeff = normalise(coeff);
    }

        /**
        * Normalises the coefficient array.
        * Trims off any leading (high degree) zero terms.
        */
        private static Complex[] normalise(Complex[] c) {
                int i = c.length-1;
                while(i >= 0 && c[i].norm() <= 2.0*GlobalSettings.ZERO_TOL)
                        i--;
                if(i < 0) {
                        return new Complex[] {Complex.ZERO};
                } else if(i < c.length-1) {
                        Complex[] arr = new Complex[i+1];
                        System.arraycopy(c, 0, arr, 0, arr.length);
                        return arr;
                } else {
                        return c;
                }
        }

    /**
     * Creates a new ComplexPolynomial object.
     *
     * @param f
     */
    public ComplexPolynomial( Field.Member[] f ) {
        _coeff = normalise(ComplexPolynomialRing.toComplex( f ));
    }

    /**
     *
     * @param n
     */
    public Field.Member getCoefficient( int n ) {
        return getCoefficientAsComplex( n );
    }

    /**
     *
     * @param n
     */
    public Complex getCoefficientAsComplex( int n ) {
        if ( n >= _coeff.length ) {
            return Complex.ZERO;
        } else {
            return _coeff[n];
        }
    }

    /**
     */
    public Field.Member[] getCoefficients() {
        return getCoefficientsAsComplexes();
    }

    /**
     * Return the coefficients as an array of complex numbers.
     */
    public Complex[] getCoefficientsAsComplexes() {
        return _coeff;
    }

        /**
        * Evaluates this polynomial.
        */
        public Complex map(Complex z) {
                return PolynomialMath.evalPolynomial(this, z);
        }
        /**
        * Evaluates this polynomial.
        */
        public Complex map(double x, double y) {
                return map(new Complex(x, y));
        }

    /**
     *
     * @return the degree
     */
    public int degree() {
        return _coeff.length-1;
    }

	public Object getSet() {
		return ComplexPolynomialRing.getInstance();
	}

    /**
     *
     * @return true if this is equal to zero.
     */
    public boolean isZero() {
        for ( int k = 0; k < _coeff.length; k++ ) {
            if ( _coeff[k].norm() > ( GlobalSettings.ZERO_TOL * 2.0 ) ) {
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @return true if this is equal to one.
     */
    public boolean isOne() {
        if(_coeff[0].subtract( Complex.ONE ).norm() > ( GlobalSettings.ZERO_TOL * 2.0 ))
           return false;

        for ( int k = 1; k < _coeff.length; k++ ) {
            if ( _coeff[k].norm() > ( 2.0 * GlobalSettings.ZERO_TOL ) ) {
                return false;
            }
        }

        return true;
    }

    /** The group composition law.
     * @param g a group member
     *
     */
    public AbelianGroup.Member add( AbelianGroup.Member g ) {
        AbelianGroup.Member result = null;
        if ( g instanceof ComplexPolynomial ) {
            ComplexPolynomial p = (ComplexPolynomial) g;
            int maxgrade        = PolynomialMath.maxDegree( this, p );
            Complex[] c         = new Complex[maxgrade+1];
            for ( int k = 0; k < c.length; k++ ) {
                c[k] = getCoefficientAsComplex( k ).add( p.getCoefficientAsComplex( k ) );
            }
            result = new ComplexPolynomial( c );
        } else {
            throw new IllegalArgumentException("Member class not recognised by this method.");
        }

        return result;
    }

    /**
     * Returns the division of this polynomial by a scalar.
     * @param f
     *
     */
    public Polynomial scalarDivide( Field.Member f ) {
        if ( f instanceof Complex ) {
            return scalarDivide( (Complex) f );
        } else if ( f instanceof Number ) {
            return scalarDivide( ( (Number) f ).doubleValue() );
        } else {
            throw new IllegalArgumentException("Member class not recognised by this method.");
        }
    }

    /**
     * Returns the division of this polynomial by a scalar.
     * @param a
     */
    public ComplexPolynomial scalarDivide( Complex a ) {
        Complex[] c = new Complex[_coeff.length];
        for ( int k = 0; k < c.length; k++ ) {
            c[k] = _coeff[k].divide( a );
        }

        return new ComplexPolynomial( c );
    }

    /**
     * Returns the division of this polynomial by a scalar.
     * @param a
     */
    public ComplexPolynomial scalarDivide( double a ) {
        Complex[] c = new Complex[_coeff.length];
        for ( int k = 0; k < c.length; k++ ) {
            c[k] = _coeff[k].divide( a );
        }

        return new ComplexPolynomial( c );
    }

    /**
     *
     * @param o
     */
    public boolean equals( Object o ) {
        if ( o == this ) {
                return true;
        } else if ( o instanceof ComplexPolynomial ) {
            ComplexPolynomial p = (ComplexPolynomial) o;
            int maxgrade = PolynomialMath.maxDegree( this, p );
            for ( int k = 0; k<=maxgrade; k++ ) {
                if ( p.getCoefficientAsComplex(k).subtract( getCoefficientAsComplex(k) ).norm() > ( 2 * GlobalSettings.ZERO_TOL ) ) {
                        return false;
                }
            }
            return true;
        }

        return false;
    }

    /**
     *
     */
    public int hashCode() {
        int res = 0;
        for ( int k = 0; k < _coeff.length; k++ ) {
            res += (int) ( _coeff[k].norm() * 10.0 );
        }

        return res;
    }

    /**
     * Returns the multiplication of this polynomial by a scalar.
     * @param f
     */
    public Polynomial scalarMultiply( Field.Member f ) {
        if ( f instanceof Number ) {
            double a = ( (Number) f ).doubleValue();
            return scalarMultiply( a );
        } else if ( f instanceof Complex ) {
            return scalarMultiply( (Complex) f );
        } else {
            throw new IllegalArgumentException("Member class not recognised by this method.");
        }
    }

    /**
     * Returns the multiplication of this polynomial by a scalar.
     * @param a
     */
    public ComplexPolynomial scalarMultiply( double a ) {
        Complex[] c = new Complex[_coeff.length];
        for ( int k = 0; k < c.length; k++ ) {
            c[k] = _coeff[k].multiply( a );
        }

        return new ComplexPolynomial( c );
    }

    /**
     * Returns the multiplication of this polynomial by a scalar.
     * @param a
     */
    public ComplexPolynomial scalarMultiply( Complex a ) {
        Complex[] c = new Complex[_coeff.length];
        for ( int k = 0; k < c.length; k++ ) {
            c[k] = _coeff[k].multiply( a );
        }

        return new ComplexPolynomial( c );
    }

    /** The multiplication law.
     * @param r a ring member
     *
     */
    public Ring.Member multiply( Ring.Member r ) {
        if ( r instanceof ComplexPolynomial ) {
            ComplexPolynomial p = (ComplexPolynomial) r;
            int maxgrade        = PolynomialMath.maxDegree( this, p );
            int mingrade        = PolynomialMath.minDegree( this, p );
            int destgrade       = maxgrade + mingrade;
            Complex[] n         = new Complex[destgrade+1];
            for ( int k = 0; k < n.length; k++ ) {
                n[k] = Complex.ZERO;
            }
            for ( int k = 0; k < _coeff.length; k++ ) {
                Complex tis = _coeff[k];
                for ( int j = 0; j < p._coeff.length; j++ ) {
                    Complex tat = p._coeff[j];
                    n[k + j] = n[k + j].add( tis.multiply( tat ) );
                }
            }

            return new ComplexPolynomial( n );
        } else {
            throw new IllegalArgumentException("Member class not recognised by this method.");
        }
    }

    /** Returns the inverse member.
     *
     */
    public AbelianGroup.Member negate() {
        Complex[] c = new Complex[_coeff.length];
        for ( int k = 0; k < c.length; k++ ) {
            c[k] = (Complex) _coeff[k].negate();
        }

        return new ComplexPolynomial( c );
    }

    /** The group composition law with inverse.
     * @param g a group member
     *
     */
    public AbelianGroup.Member subtract( AbelianGroup.Member g ) {
        if ( g instanceof ComplexPolynomial ) {
            ComplexPolynomial p = (ComplexPolynomial) g;
            int maxgrade        = PolynomialMath.maxDegree( this, p );
            Complex[] c         = new Complex[maxgrade+1];
            for ( int k = 0; k < c.length; k++ ) {
                c[k] = getCoefficientAsComplex( k ).subtract( p.getCoefficientAsComplex( k ) );
            }
            return new ComplexPolynomial( c );
        } else {
            throw new IllegalArgumentException("Member class not recognised by this method.");
        }
    }

    /**
     *
     */
    public String toString() {
        StringBuffer sb = new StringBuffer( "P(z) = " );

        for ( int k = degree(); k > 0; k-- ) {
            sb.append( _coeff[k] ).append( "z^" ).append( k ).append( " + " );
        }
        sb.append( _coeff[0] );

        return sb.toString();
    }
}

