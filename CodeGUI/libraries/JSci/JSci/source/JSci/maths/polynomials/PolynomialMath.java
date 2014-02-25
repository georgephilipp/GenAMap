package JSci.maths.polynomials;

import JSci.maths.AbstractMath;
import JSci.maths.Complex;
import JSci.maths.matrices.AbstractComplexSquareMatrix;
import JSci.maths.matrices.AbstractDoubleSquareMatrix;
import JSci.maths.matrices.ComplexSquareMatrix;
import JSci.maths.matrices.DoubleSquareMatrix;

/**
 *
 * @author  b.dietrich
 */
public final class PolynomialMath extends AbstractMath {
        private PolynomialMath() {}

    /**
     * Returns the companion matrix of a given polynomial.
     * The eigenvalues of the companion matrix are the roots of the polynomial.
     * 
     * @param p the polynomial
     *
     * @return the companion matrix
     * @jsci.planetmath CompanionMatrix
     */
    public static AbstractDoubleSquareMatrix toCompanionMatrix( RealPolynomial p ) {
        RealPolynomial np = normalize(p);

        int n = np.degree();
        if ( n < 1 ) {
            throw new IllegalArgumentException( "Cannot get a companion matrix for a constant factor" );
        }

        AbstractDoubleSquareMatrix dsm = new DoubleSquareMatrix( n );

        for ( int k = 0; k < (n-1); k++ ) {
        // fill subdiagonal
            dsm.setElement( k+1, k, 1.0 );
        // fill lastCol
            dsm.setElement( k, n-1, np.getCoefficientAsDouble( k ) );
        }
        // fill lastRow/lastCol
        dsm.setElement( n-1, n-1, np.getCoefficientAsDouble( n-1 ) );

        return dsm;
    }
    public static AbstractComplexSquareMatrix toCompanionMatrix( ComplexPolynomial p ) {
        ComplexPolynomial np = normalize(p);

        int n = np.degree();
        if ( n < 1 ) {
            throw new IllegalArgumentException( "Cannot get a companion matrix for a constant factor" );
        }

        AbstractComplexSquareMatrix csm = new ComplexSquareMatrix( n );

        for ( int k = 0; k < ( n-1 ); k++ ) {
        // fill subdiagonal
            csm.setElement( k+1, k, 1.0, 0.0 );
        // fill lastCol
            csm.setElement( k, n-1, np.getCoefficientAsComplex( k ) );
        }
        // fill lastRow/lastCol
        csm.setElement( n-1, n-1, np.getCoefficientAsComplex( n-1 ) );

        return csm;
    }

    /**
     * Calculates the roots of a given polynomial by solving the
     * eigenvalue problem for the companion matrix.
     * This is not yet implemented (depends on a QR- decomposition)
     * @param p the polynomial
     *
     * @return (unordered) list of roots.
     */
    public static Complex[] findRoots( RealPolynomial p ) {
        AbstractDoubleSquareMatrix matrix = toCompanionMatrix( p );

        // return solveEigenvalueByQR(c);
        throw new java.lang.UnsupportedOperationException("Not yet implemented.");
    }

    /**
     * Get the maximum degree of two polynomials
     * @param p1
     * @param p2
     */
    public static int maxDegree( Polynomial p1, Polynomial p2 ) {
        return Math.max( p1.degree(), p2.degree() );
    }

    /**
     * Get the minimal degree of two polynomials
     * @param p1
     * @param p2
     */
    public static int minDegree( Polynomial p1, Polynomial p2 ) {
        return Math.min( p1.degree(), p2.degree() );
    }

    /**
     * Evaluates a polynomial by Horner's scheme.
     * @param p
     * @param t
     */
    public static double evalPolynomial( RealPolynomial p, double t ) {
        final int n    = p.degree();
        double r = p.getCoefficientAsDouble(n);
        for ( int i = n-1; i >= 0; i-- ) {
            r = p.getCoefficientAsDouble(i) + ( r * t );
        }

        return r;
    }

    /**
     * Evaluates a polynomial by Horner's scheme.
     * @param p
     * @param t
     */
    public static Complex evalPolynomial( ComplexPolynomial p, Complex t ) {
        final int n     = p.degree();
        Complex r = p.getCoefficientAsComplex(n);
        for ( int i = n-1; i >= 0; i-- ) {
            r = p.getCoefficientAsComplex(i).add( r.multiply( t ) );
        }

        return r;
    }

    /**
     * Interpolates a polynomial.
     * Caveat: this method is brute-force, slow and not very stable.
     * It shouldn't be used for more than approx. 10 points.
     * Remember the strong variations of higher degree polynomials.
     *
     * @param samplingPoints an array[2][n] where array[0] denotes x-values, array[1] y-values
     */
    public static RealPolynomial interpolateLagrange( double[][] samplingPoints ) {
        RealLagrangeBasis r = new RealLagrangeBasis( samplingPoints[0] );
        return ( (RealPolynomial) r.superposition( samplingPoints[1] ) );
    }

    /**    
     * Interpolates a polynomial.
     * Caveat: this method is brute-force, slow and not very stable.
     * It shouldn't be used for more than approx. 10 points.
     * Remember the strong variations of higher degree polynomials.
     *
     * @param samplingPoints an array[2][n] where array[0] denotes x-values, array[1] y-values
     */
    public static ComplexPolynomial interpolateLagrange( Complex[][] samplingPoints ) {
        ComplexLagrangeBasis r = new ComplexLagrangeBasis( samplingPoints[0] );
        return ( (ComplexPolynomial) r.superposition( samplingPoints[1] ) );
    }

    /**
     * Normalizes a given real polynomial, i.e. divide by the leading coefficient.
     * @param p
     */
    public static RealPolynomial normalize( RealPolynomial p ) {
        final int n       = p.degree();
        final double c   = p.getCoefficientAsDouble( n );
        double[] m = new double[n+1];
        m[n]    = 1.0;
        for ( int i = 0; i < n; i++ ) {
            m[i] = p.getCoefficientAsDouble( i ) / c;
        }

        return new RealPolynomial( m );
    }
    /**
     * Normalizes a given complex polynomial, i.e. divide by the leading coefficient.
     * @param p
     */
    public static ComplexPolynomial normalize( ComplexPolynomial p ) {
        final int n       = p.degree();
        final Complex c   = p.getCoefficientAsComplex( n );
        Complex[] m = new Complex[n+1];
        m[n]    = Complex.ONE;
        for ( int i = 0; i < n; i++ ) {
            m[i] = p.getCoefficientAsComplex( i ).divide( c );
        }

        return new ComplexPolynomial( m );
    }

    /**
     *
     * Try to cast a Polynomial to a complex polynomial
     */
    public static ComplexPolynomial toComplex( Polynomial p ) {
        if ( p instanceof ComplexPolynomial ) {
            return (ComplexPolynomial) p;
        } else if ( p instanceof RealPolynomial ) {
            double[] d  = ( (RealPolynomial) p ).getCoefficientsAsDoubles();
            Complex[] c = new Complex[d.length];
            for ( int k = 0; k < d.length; k++ ) {
                c[k] = new Complex( d[k], 0 );
            }

            return new ComplexPolynomial( c );
        } else {
            throw new IllegalArgumentException("Polynomial class not recognised by this method.");
        }
    }
}

