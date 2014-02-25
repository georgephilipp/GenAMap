/* AUTO-GENERATED */
package JSci.maths.vectors;

import JSci.GlobalSettings;
import JSci.maths.Complex;
import JSci.maths.ComplexMapping;
import JSci.maths.MathDouble;
import JSci.maths.MathInteger;
import JSci.maths.groups.AbelianGroup;
import JSci.maths.algebras.Module;
import JSci.maths.algebras.VectorSpace;
import JSci.maths.algebras.HilbertSpace;
import JSci.maths.fields.Ring;
import JSci.maths.fields.Field;

/**
* An optimised implementation of a 2D complex vector.
* @version 2.2
* @author Mark Hale
*/
public final class Complex2Vector extends AbstractComplexVector {
        protected double xre, xim;
        protected double yre, yim;
        /**
        * Constructs an empty 2-vector.
        */
        public Complex2Vector() {
                super(2);
        }
        /**
        * Constructs a 2-vector.
        * @param x x coordinate.
        * @param y y coordinate.
        */
        public Complex2Vector(final Complex x, final Complex y) {
                this();
                xre = x.real();
                xim = x.imag();
                yre = y.real();
                yim = y.imag();
        }
        public Complex2Vector(double xRe, double xIm, double yRe, double yIm) {
                this();
                xre = xRe;
                xim = xIm;
                yre = yRe;
                yim = yIm;
        }
        /**
        * Compares two complex vectors for equality.
        * @param obj a complex 2-vector
        */
	public boolean equals(Object obj, double tol) {
                if(obj != null && (obj instanceof Complex2Vector)) {
                        final Complex2Vector vec = (Complex2Vector) obj;
                        double dxRe = xre - vec.xre;
                        double dxIm = xim - vec.xim;
                        double dyRe = yre - vec.yre;
                        double dyIm = yim - vec.yim;
                        return (dxRe*dxRe + dxIm*dxIm
                         + dyRe*dyRe + dyIm*dyIm <= tol*tol);
                } else
                        return false;
        }
        /**
        * Returns a comma delimited string representing the value of this vector.
        */
        public String toString() {
                final StringBuffer buf = new StringBuffer(15);
                buf.append(Complex.toString(xre, xim)).append(',').append(Complex.toString(yre, yim));
                return buf.toString();
        }
        /**
        * Returns the real part of this complex 2-vector.
        */
        public AbstractDoubleVector real() {
                return new Double2Vector(
                        xre,
                        yre
                );
        }
        /**
        * Returns the imaginary part of this complex 2-vector.
        */
        public AbstractDoubleVector imag() {
                return new Double2Vector(
                        xim,
                        yim
                );
        }
        /**
        * Returns a component of this vector.
        * @param n index of the vector component
        * @exception VectorDimensionException If attempting to access an invalid component.
        */
        public Complex getComponent(final int n) {
                switch(n) {
                        case 0 : return new Complex(xre, xim);
                        case 1 : return new Complex(yre, yim);
                        default : throw new VectorDimensionException("Invalid component.");
                }
        }
        public double getRealComponent(final int n) {
                switch(n) {
                        case 0 : return xre;
                        case 1 : return yre;
                        default : throw new VectorDimensionException("Invalid component.");
                }
        }
        public double getImagComponent(final int n) {
                switch(n) {
                        case 0 : return xim;
                        case 1 : return yim;
                        default : throw new VectorDimensionException("Invalid component.");
                }
        }
        /**
        * Sets the value of a component of this vector.
        * Should only be used to initialise this vector.
        * @param n index of the vector component
        * @param z a complex number
        * @exception VectorDimensionException If attempting to access an invalid component.
        */
        public void setComponent(final int n, final Complex z) {
                switch(n) {
                        case 0 : xre = z.real(); xim = z.imag(); break;
                        case 1 : yre = z.real(); yim = z.imag(); break;
                        default : throw new VectorDimensionException("Invalid component.");
                }
        }
        /**
        * Sets the value of a component of this vector.
        * Should only be used to initialise this vector.
        * @param n index of the vector component
        * @param x the real part of a complex number
        * @param y the imaginary part of a complex number
        * @exception VectorDimensionException If attempting to access an invalid component.
        */
        public void setComponent(final int n, final double x, final double y) {
                switch(n) {
                        case 0 : xre = x; xim = y; break;
                        case 1 : yre = x; yim = y; break;
                        default : throw new VectorDimensionException("Invalid component.");
                }
        }
        /**
        * Returns the l<sup>2</sup>-norm (magnitude).
        */
        public double norm() {
                return Math.sqrt(
                        xre*xre + xim*xim
                        +yre*yre + yim*yim
                );
        }
        /**
        * Returns the l<sup><img border=0 alt="infinity" src="doc-files/infinity.gif"></sup>-norm.
        */
        public double infNorm() {
                double infNormSq = 0;
                double modSq;
                modSq = xre*xre + xim*xim;
                if(modSq > infNormSq)
                        infNormSq = modSq;
                modSq = yre*yre + yim*yim;
                if(modSq > infNormSq)
                        infNormSq = modSq;
                return Math.sqrt(infNormSq);
        }

//============
// OPERATIONS
//============

        /**
        * Returns the negative of this vector.
        */
        public AbelianGroup.Member negate() {
                return new Complex2Vector(
                        -xre, -xim,
                        -yre, -yim
                );
        }

// COMPLEX CONJUGATE

        /**
        * Returns the complex conjugate of this vector.
        * @return a complex 2-vector
        */
        public AbstractComplexVector conjugate() {
                return new Complex2Vector(
                        xre, -xim,
                        yre, -yim
                );
        }

// ADDITION

        /**
        * Returns the addition of this vector and another.
        */
        public AbelianGroup.Member add(final AbelianGroup.Member vec) {
                if(vec instanceof AbstractComplexVector)
                        return add((AbstractComplexVector)vec);
                else if(vec instanceof AbstractDoubleVector)
                        return add((AbstractDoubleVector)vec);
                else if(vec instanceof AbstractIntegerVector)
                        return add((AbstractIntegerVector)vec);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the addition of this vector and another.
        * @param vec a complex 2-vector
        */
        public AbstractComplexVector add(final AbstractComplexVector vec) {
                if(vec.N == 2) {
                        return new Complex2Vector(
                                xre+vec.getComponent(0).real(), xim+vec.getComponent(0).imag(),
                                yre+vec.getComponent(1).real(), yim+vec.getComponent(1).imag()
                        );
                } else
                        throw new VectorDimensionException("Vectors are different sizes.");
        }
        /**
        * Returns the addition of this vector and another.
        * @param vec a double 2-vector
        */
        public AbstractComplexVector add(final AbstractDoubleVector vec) {
                if(vec.N == 2) {
                        return new Complex2Vector(
                                xre+vec.getComponent(0), xim,
                                yre+vec.getComponent(1), yim
                        );
                } else
                        throw new VectorDimensionException("Vectors are different sizes.");
        }
        /**
        * Returns the addition of this vector and another.
        * @param vec an integer 2-vector
        */
        public AbstractComplexVector add(final AbstractIntegerVector vec) {
                if(vec.N == 2) {
                        return new Complex2Vector(
                                xre+vec.getComponent(0), xim,
                                yre+vec.getComponent(1), yim
                        );
                } else
                        throw new VectorDimensionException("Vectors are different sizes.");
        }

// SUBTRACTION

        /**
        * Returns the subtraction of this vector by another.
        */
        public AbelianGroup.Member subtract(final AbelianGroup.Member vec) {
                if(vec instanceof AbstractComplexVector)
                        return subtract((AbstractComplexVector)vec);
                else if(vec instanceof AbstractDoubleVector)
                        return subtract((AbstractDoubleVector)vec);
                else if(vec instanceof AbstractIntegerVector)
                        return subtract((AbstractIntegerVector)vec);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the subtraction of this vector by another.
        * @param vec a complex 2-vector
        */
        public AbstractComplexVector subtract(final AbstractComplexVector vec) {
                if(vec.N == 2) {
                        return new Complex2Vector(
                                xre-vec.getComponent(0).real(), xim-vec.getComponent(0).imag(),
                                yre-vec.getComponent(1).real(), yim-vec.getComponent(1).imag()
                        );
                } else
                        throw new VectorDimensionException("Vectors are different sizes.");
        }
        /**
        * Returns the subtraction of this vector by another.
        * @param vec a double 2-vector
        */
        public AbstractComplexVector subtract(final AbstractDoubleVector vec) {
                if(vec.N == 2) {
                        return new Complex2Vector(
                                xre-vec.getComponent(0), xim,
                                yre-vec.getComponent(1), yim
                        );
                } else
                        throw new VectorDimensionException("Vectors are different sizes.");
        }
        /**
        * Returns the subtraction of this vector by another.
        * @param vec an integer 2-vector
        */
        public AbstractComplexVector subtract(final AbstractIntegerVector vec) {
                if(vec.N == 2) {
                        return new Complex2Vector(
                                xre-vec.getComponent(0), xim,
                                yre-vec.getComponent(1), yim
                        );
                } else
                        throw new VectorDimensionException("Vectors are different sizes.");
        }

// SCALAR MULTIPLICATION

        /**
        * Returns the multiplication of this vector by a scalar.
        */
        public Module.Member scalarMultiply(Ring.Member x) {
                if(x instanceof Complex)
                        return scalarMultiply((Complex)x);
                else if(x instanceof MathDouble)
                        return scalarMultiply(((MathDouble)x).value());
                else if(x instanceof MathInteger)
                        return scalarMultiply(((MathInteger)x).value());
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the multiplication of this vector by a scalar.
        * @param z a complex number
        * @return a complex 2-vector
        */
        public AbstractComplexVector scalarMultiply(final Complex z) {
                final double real=z.real();
                final double imag=z.imag();
                return new Complex2Vector(
                        xre*real-xim*imag, xre*imag+xim*real,
                        yre*real-yim*imag, yre*imag+yim*real
                );
        }
        /**
        * Returns the multiplication of this vector by a scalar.
        * @param k a double
        * @return a complex 2-vector
        */
        public AbstractComplexVector scalarMultiply(final double k) {
                return new Complex2Vector(
                        k*xre, k*xim,
                        k*yre, k*yim
                );
        }

// SCALAR DIVISION

        /**
        * Returns the division of this vector by a scalar.
        */
        public VectorSpace.Member scalarDivide(Field.Member x) {
                if(x instanceof Complex)
                        return scalarDivide((Complex)x);
                else if(x instanceof MathDouble)
                        return scalarDivide(((MathDouble)x).value());
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the division of this vector by a scalar.
        * @param z a complex number
        * @return a complex 2-vector
        * @exception ArithmeticException If divide by zero.
        */
        public AbstractComplexVector scalarDivide(final Complex z) {
                final double real=z.real();
                final double imag=z.imag();
                final double a,denom;
                if(Math.abs(real)<Math.abs(imag)) {
                        a=real/imag;
                        denom=real*a+imag;
                        return new Complex2Vector(
                                (xre*a+xim)/denom, (xim*a-xre)/denom,
                                (yre*a+yim)/denom, (yim*a-yre)/denom
                        );
                } else {
                        a=imag/real;
                        denom=real+imag*a;
                        return new Complex2Vector(
                                (xre+xim*a)/denom, (xim-xre*a)/denom,
                                (yre+yim*a)/denom, (yim-yre*a)/denom
                        );
                }
        }
        /**
        * Returns the division of this vector by a scalar.
        * @param k a double
        * @return a complex 2-vector
        * @exception ArithmeticException If divide by zero.
        */
        public AbstractComplexVector scalarDivide(final double k) {
                return new Complex2Vector(
                        xre/k, xim/k,
                        yre/k, yim/k
                );
        }

// SCALAR PRODUCT

        /**
        * Returns the scalar product of this vector and another.
        */
        public Complex scalarProduct(HilbertSpace.Member vec) {
                if(vec instanceof AbstractComplexVector)
                        return scalarProduct((AbstractComplexVector)vec);
                else
                        throw new IllegalArgumentException("Member class not recognised by this method.");
        }
        /**
        * Returns the scalar product of this vector and another.
        * @param vec a complex vector
        * @exception VectorDimensionException If the vectors are different sizes.
        */
        public Complex scalarProduct(final AbstractComplexVector vec) {
                if(vec instanceof Complex2Vector)
                        return scalarProduct((Complex2Vector)vec);
                else {
                        if(vec.N == 2) {
                                return new Complex(
                                        xre*vec.getComponent(0).real()+xim*vec.getComponent(0).imag()+
                                        yre*vec.getComponent(1).real()+yim*vec.getComponent(1).imag(),
                                        xim*vec.getComponent(0).real()-xre*vec.getComponent(0).imag()+
                                        yim*vec.getComponent(1).real()-yre*vec.getComponent(1).imag()
                                );
                        } else
                                throw new VectorDimensionException("Vectors are different sizes.");
                }
        }
        /**
        * Returns the scalar product of this vector and another.
        * @param vec a complex 2-vector
        */
        public Complex scalarProduct(final Complex2Vector vec) {
                return new Complex(
                        xre*vec.xre+xim*vec.xim+
                        yre*vec.yre+yim*vec.yim,
                        xim*vec.xre-xre*vec.xim+
                        yim*vec.yre-yre*vec.yim
                );
        }


// MAP COMPONENTS

        /**
        * Applies a function on all the vector components.
        * @param mapping a user-defined function
        * @return a complex 2-vector
        */
        public AbstractComplexVector mapComponents(final ComplexMapping mapping) {
                return new Complex2Vector(
                        mapping.map(xre, xim),
                        mapping.map(yre, yim)
                );
        }
}
