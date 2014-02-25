package JSci.maths;

import JSci.GlobalSettings;
import JSci.maths.matrices.AbstractDoubleSquareMatrix;
import JSci.maths.matrices.DoubleSquareMatrix;
import JSci.maths.matrices.MatrixDimensionException;
import JSci.maths.vectors.Double3Vector;
import JSci.maths.groups.AbelianGroup;
import JSci.maths.fields.*;
import JSci.maths.algebras.*;

/**
* The Quaternion class encapsulates quaternions.
* @jsci.planetmath Quaternions
 * @jsci.wikipedia Quaternion
* @version 1.1
* @author Mark Hale
*
* Quaternions are a noncommutative C<sup>*</sup>-algebra over the reals.
*/
public final class Quaternion implements Field.Member, CStarAlgebra.Member {
        private static final long serialVersionUID = 1605315490425547301L;

        private double re;
        private double imi, imj, imk;

        public static final Quaternion ONE=new Quaternion(1.0, 0.0, 0.0, 0.0);
        public static final Quaternion I=new Quaternion(0.0, 1.0, 0.0, 0.0);
        public static final Quaternion J=new Quaternion(0.0, 0.0, 1.0, 0.0);
        public static final Quaternion K=new Quaternion(0.0, 0.0, 0.0, 1.0);
        /**
        * Constructs a quaternion.
        */
        public Quaternion(final double real,final Double3Vector imag) {
                re=real;
                imi=imag.getComponent(0);
                imj=imag.getComponent(1);
                imk=imag.getComponent(2);
        }
        /**
        * Constructs the quaternion q<sub>0</sub>+iq<sub>1</sub>+jq<sub>2</sub>+kq<sub>3</sub>.
        */
        public Quaternion(final double q0,final double q1,final double q2,final double q3) {
                re=q0;
                imi=q1;
                imj=q2;
                imk=q3;
        }
        /**
        * Constructs a quaternion representing a rotation matrix.
        * Note: if the matrix is not orthogonal then the quaternion will not have unit norm.
        * Note: unit quaternions are a double cover of SO(3).
        * @param m a rotation matrix
        * @author Steve Lamont
        */
        public static Quaternion rotation(AbstractDoubleSquareMatrix m) {
                if(m.rows() != 3 && m.columns() != 3)
                        throw new MatrixDimensionException("The matrix is not 3-dimensional.");
                double re, imi, imj, imk;
                double wSqr = ( 1.0 + m.trace() ) / 4.0;
                if(wSqr > GlobalSettings.ZERO_TOL) {
                        re = Math.sqrt( wSqr );
                        imi = ( m.getElement(2,1) - m.getElement(1,2) ) / ( re * 4.0 );
                        imj = ( m.getElement(0,2) - m.getElement(2,0) ) / ( re * 4.0 );
                        imk = ( m.getElement(1,0) - m.getElement(0,1) ) / ( re * 4.0 );
        	} else {
                        double xSqr = -( m.getElement(1,1) + m.getElement(2,2) ) / 2.0;
                        re = 0.0;
                        if(xSqr > GlobalSettings.ZERO_TOL) {
                		imi = Math.sqrt( xSqr );
                		imj = m.getElement(1,0) / ( 2.0 * imi );
                		imk = m.getElement(2,0) / ( 2.0 * imi );
                        } else {
                		double ySqr = ( 1.0 - m.getElement(2,2) ) / 2.0;
                		imi = 0.0;
                                if(ySqr > GlobalSettings.ZERO_TOL) {
                                        imj = Math.sqrt( ySqr );
                                        imk = m.getElement(2,1) / ( 2.0 * imj );
                		} else {
                                        imj = 0.0;
                                        imk = 1.0;
                		}
                        }
        	}
                return new Quaternion(re, imi, imj, imk);
        }
        /**
        * Returns a 3D rotation matrix representing this quaternion.
        * Note: if this quaternion does not have unit norm then the matrix will not be orthogonal.
        * @author Steve Lamont
        */
        public AbstractDoubleSquareMatrix toRotationMatrix() {
                final double[][] array = new double[3][3];

                array[0][0] = 1.0 - 2.0 * ( imj * imj + imk * imk );
                array[0][1] = 2.0 * ( imi * imj - re * imk );
        	array[0][2] = 2.0 * ( imi * imk + re * imj );

        	array[1][0] = 2.0 * ( imi * imj + re * imk );
        	array[1][1] = 1.0 - 2.0 * ( imi * imi + imk * imk );
        	array[1][2] = 2.0 * ( imj * imk - re * imi );

                array[2][0] = 2.0 * ( imi * imk - re * imj );
                array[2][1] = 2.0 * ( imj * imk + re * imi );
                array[2][2] = 1.0 - 2.0 * ( imi * imi + imj * imj );

                return new DoubleSquareMatrix( array );
        }
        /**
        * Compares two quaternions for equality.
        * @param obj a quaternion
        */
        public boolean equals(Object obj) {
                if(obj instanceof Quaternion) {
                        final Quaternion q = (Quaternion)obj;
                        return this.subtract(q).norm() <= GlobalSettings.ZERO_TOL;
                } else
                        return false;
        }
        /**
        * Returns a string representing the value of this quaternion.
        */
        public String toString() {
                final StringBuffer buf=new StringBuffer(40);
                buf.append(re);
                if(imi>=0.0)
                        buf.append("+");
                buf.append(imi);
                buf.append("i");
                if(imj>=0.0)
                        buf.append("+");
                buf.append(imj);
                buf.append("j");
                if(imk>=0.0)
                        buf.append("+");
                buf.append(imk);
                buf.append("k");
                return buf.toString();
        }
        /**
        * Returns a hashcode for this quaternion.
        */
        public int hashCode() {
                return (int)(Math.exp(norm()));
        }
        /**
        * Returns true if either the real or imaginary part is NaN.
        */
        public boolean isNaN() {
                return (re==Double.NaN) || (imi==Double.NaN) ||
                (imj==Double.NaN) || (imk==Double.NaN);
        }
        /**
        * Returns true if either the real or imaginary part is infinite.
        */
        public boolean isInfinite() {
                return (re==Double.POSITIVE_INFINITY) || (re==Double.NEGATIVE_INFINITY)
                        || (imi==Double.POSITIVE_INFINITY) || (imi==Double.NEGATIVE_INFINITY)
                        || (imj==Double.POSITIVE_INFINITY) || (imj==Double.NEGATIVE_INFINITY)
                        || (imk==Double.POSITIVE_INFINITY) || (imk==Double.NEGATIVE_INFINITY);
        }
        /**
        * Returns the real part of this quaternion.
        */
        public double real() {
                return re;
        }
        /**
        * Returns the imaginary part of this quaternion.
        */
        public Double3Vector imag() {
                return new Double3Vector(imi, imj, imk);
        }
	public Object getSet() {
		throw new RuntimeException("Not yet implemented: please file bug report");
	}
        /**
        * Returns the l<sup>2</sup>-norm (magnitude),
        * which is also the C<sup>*</sup> norm.
        */
        public double norm() {
                return Math.sqrt(sumSquares());
        }
        /**
        * Returns the sum of the squares of the components.
        */
        public double sumSquares() {
                return re*re+imi*imi+imj*imj+imk*imk;
        }

//============
// OPERATIONS
//============

        /**
        * Returns the negative of this quaternion.
        */
        public AbelianGroup.Member negate() {
                return new Quaternion(-re, -imi, -imj, -imk);
        }
        /**
        * Returns the inverse of this quaternion.
        */
        public Field.Member inverse() {
                final double sumSqr=sumSquares();
                return new Quaternion(re/sumSqr, -imi/sumSqr, -imj/sumSqr, -imk/sumSqr);
        }
        /**
        * Returns the involution of this quaternion.
        */
        public CStarAlgebra.Member involution() {
                return conjugate();
        }
        /**
        * Returns the conjugate of this quaternion.
        */
        public Quaternion conjugate() {
                return new Quaternion(re, -imi, -imj, -imk);
        }

// ADDITION

        /**
        * Returns the addition of this number and another.
        */
        public AbelianGroup.Member add(final AbelianGroup.Member x) {
                if(x instanceof Quaternion)
                        return add((Quaternion)x);
                else if(x instanceof MathDouble)
                        return addReal(((MathDouble)x).value());
                else if(x instanceof MathInteger)
                        return addReal(((MathInteger)x).value());
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the addition of this quaternion and another.
        * @param q a quaternion
        */
        public Quaternion add(final Quaternion q) {
                return new Quaternion(re+q.re, imi+q.imi, imj+q.imj, imk+q.imk);
        }
        /**
        * Returns the addition of this quaternion with a real part.
        * @param real a real part
        */
        public Quaternion addReal(final double real) {
                return new Quaternion(re+real, imi, imj, imk);
        }
        /**
        * Returns the addition of this quaternion with an imaginary part.
        * @param imag an imaginary part
        */
        public Quaternion addImag(final Double3Vector imag) {
                return new Quaternion(re, imi+imag.getComponent(0), imj+imag.getComponent(1), imk+imag.getComponent(2));
        }

// SUBTRACTION

        /**
        * Returns the subtraction of this number and another.
        */
        public AbelianGroup.Member subtract(final AbelianGroup.Member x) {
                if(x instanceof Quaternion)
                        return subtract((Quaternion)x);
                else if(x instanceof MathDouble)
                        return subtractReal(((MathDouble)x).value());
                else if(x instanceof MathInteger)
                        return subtractReal(((MathInteger)x).value());
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the subtraction of this quaternion by another.
        * @param q a quaternion
        */
        public Quaternion subtract(final Quaternion q) {
                return new Quaternion(re-q.re, imi-q.imi, imj-q.imj, imk-q.imk);
        }
        /**
        * Returns the subtraction of this quaternion by a real part.
        * @param real a real part
        */
        public Quaternion subtractReal(final double real) {
                return new Quaternion(re-real, imi, imj, imk);
        }
        /**
        * Returns the subtraction of this quaternion by an imaginary part.
        * @param imag an imaginary part
        */
        public Quaternion subtractImag(final Double3Vector imag) {
                return new Quaternion(re, imi-imag.getComponent(0), imj-imag.getComponent(1), imk-imag.getComponent(2));
        }

// MULTIPLICATION

        /**
        * Returns the multiplication of this number by a real scalar.
        */
        public Module.Member scalarMultiply(final Ring.Member x) {
                if(x instanceof MathDouble)
                        return multiply(((MathDouble)x).value());
                else if(x instanceof MathInteger)
                        return multiply(((MathInteger)x).value());
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the multiplication of this number and another.
        */
        public Ring.Member multiply(final Ring.Member x) {
                if(x instanceof Quaternion)
                        return multiply((Quaternion)x);
                else if(x instanceof MathDouble)
                        return multiply(((MathDouble)x).value());
                else if(x instanceof MathInteger)
                        return multiply(((MathInteger)x).value());
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the multiplication of this quaternion and another.
        * @param q a quaternion
        */
        public Quaternion multiply(final Quaternion q) {
                return new Quaternion(
                        re*q.re-imi*q.imi-imj*q.imj-imk*q.imk,
                        re*q.imi+q.re*imi+(imj*q.imk-q.imj*imk),
                        re*q.imj+q.re*imj+(imk*q.imi-q.imk*imi),
                        re*q.imk+q.re*imk+(imi*q.imj-q.imi*imj)
                );
        }
        /**
        * Returns the multiplication of this quaternion by a scalar.
        * @param x a real number
        */
        public Quaternion multiply(final double x) {
                return new Quaternion(x*re, x*imi, x*imj, x*imk);
        }

// DIVISION

        /**
        * Returns the division of this number by a real scalar.
        */
        public VectorSpace.Member scalarDivide(final Field.Member x) {
                if(x instanceof MathDouble)
                        return divide(((MathDouble)x).value());
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the division of this number and another.
        */
        public Field.Member divide(final Field.Member x) {
                if(x instanceof Quaternion)
                        return divide((Quaternion)x);
                else if(x instanceof MathDouble)
                        return divide(((MathDouble)x).value());
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the division of this quaternion by another.
        * @param q a quaternion
        * @exception ArithmeticException If divide by zero.
        */
        public Quaternion divide(final Quaternion q) {
                final double qSumSqr=q.sumSquares();
                return new Quaternion(
                        (re*q.re+imi*q.imi+imj*q.imj+imk*q.imk)/qSumSqr,
                        (q.re*imi-re*q.imi-(imj*q.imk-q.imj*imk))/qSumSqr,
                        (q.re*imj-re*q.imj-(imk*q.imi-q.imk*imi))/qSumSqr,
                        (q.re*imk-re*q.imk-(imi*q.imj-q.imi*imj))/qSumSqr
                );
        }
        /**
        * Returns the division of this quaternion by a scalar.
        * @param x a real number
        * @exception ArithmeticException If divide by zero.
        */
        public Quaternion divide(final double x) {
                return new Quaternion(re/x, imi/x, imj/x, imk/x);
        }
        public Quaternion normalize() {
                return this.divide(norm());
        }

//============
// FUNCTIONS
//============

        public static Quaternion exp(Quaternion q) {
                final double k = Math.exp(q.re);
                Double3Vector imag = q.imag();
                final double imagNorm = imag.norm();
                return new Quaternion(k*Math.cos(imagNorm), (Double3Vector) imag.normalize().scalarMultiply(k*Math.sin(imagNorm)));
        }
        public static Quaternion log(Quaternion q) {
                final double norm = q.norm();
                return new Quaternion(Math.log(norm), (Double3Vector) q.imag().normalize().scalarMultiply(Math.acos(q.re/norm)));
        }
        public static Quaternion sin(Quaternion q) {
                Double3Vector imag = q.imag();
                final double imagNorm = imag.norm();
                return new Quaternion(Math.sin(q.re)*ExtraMath.cosh(imagNorm), (Double3Vector) imag.normalize().scalarMultiply(Math.cos(q.re)*ExtraMath.sinh(imagNorm)));
        }
        public static Quaternion cos(Quaternion q) {
                Double3Vector imag = q.imag();
                final double imagNorm = imag.norm();
                return new Quaternion(Math.cos(q.re)*ExtraMath.cosh(imagNorm), (Double3Vector) imag.normalize().scalarMultiply(-Math.sin(q.re)*ExtraMath.sinh(imagNorm)));
        }
        public static Quaternion tan(Quaternion q) {
                return sin(q).divide(cos(q));
        }
        public static Quaternion sinh(Quaternion q) {
                Double3Vector imag = q.imag();
                final double imagNorm = imag.norm();
                return new Quaternion(ExtraMath.sinh(q.re)*Math.cos(imagNorm), (Double3Vector) imag.normalize().scalarMultiply(ExtraMath.cosh(q.re)*Math.sin(imagNorm)));
        }
        public static Quaternion cosh(Quaternion q) {
                Double3Vector imag = q.imag();
                final double imagNorm = imag.norm();
                return new Quaternion(ExtraMath.cosh(q.re)*Math.cos(imagNorm), (Double3Vector) imag.normalize().scalarMultiply(ExtraMath.sinh(q.re)*Math.sin(imagNorm)));
        }
        public static Quaternion tanh(Quaternion q) {
                return sinh(q).divide(cosh(q));
        }
}

