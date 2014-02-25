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
* An optimised implementation of a 3D complex vector.
* @version 2.2
* @author Mark Hale
*/
public final class Complex3Vector extends AbstractComplexVector {
        protected double xre, xim;
        protected double yre, yim;
        protected double zre, zim;
        /**
        * Constructs an empty 3-vector.
        */
        public Complex3Vector() {
                super(3);
        }
        /**
        * Constructs a 3-vector.
        * @param x x coordinate.
        * @param y y coordinate.
        * @param z z coordinate.
        */
        public Complex3Vector(final Complex x, final Complex y, final Complex z) {
                this();
                xre = x.real();
                xim = x.imag();
                yre = y.real();
                yim = y.imag();
                zre = z.real();
                zim = z.imag();
        }
        public Complex3Vector(double xRe, double xIm, double yRe, double yIm, double zRe, double zIm) {
                this();
                xre = xRe;
                xim = xIm;
                yre = yRe;
                yim = yIm;
                zre = zRe;
                zim = zIm;
        }
        /**
        * Compares two complex vectors for equality.
        * @param obj a complex 3-vector
        */
	public boolean equals(Object obj, double tol) {
                if(obj != null && (obj instanceof Complex3Vector)) {
                        final Complex3Vector vec = (Complex3Vector) obj;
                        double dxRe = xre - vec.xre;
                        double dxIm = xim - vec.xim;
                        double dyRe = yre - vec.yre;
                        double dyIm = yim - vec.yim;
                        double dzRe = zre - vec.zre;
                        double dzIm = zim - vec.zim;
                        return (dxRe*dxRe + dxIm*dxIm
                         + dyRe*dyRe + dyIm*dyIm
                         + dzRe*dzRe + dzIm*dzIm <= tol*tol);
                } else
                        return false;
        }
        /**
        * Returns a comma delimited string representing the value of this vector.
        */
        public String toString() {
                final StringBuffer buf = new StringBuffer(15);
                buf.append(Complex.toString(xre, xim)).append(',').append(Complex.toString(yre, yim)).append(',').append(Complex.toString(zre, zim));
                return buf.toString();
        }
        /**
        * Returns the real part of this complex 3-vector.
        */
        public AbstractDoubleVector real() {
                return new Double3Vector(
                        xre,
                        yre,
                        zre
                );
        }
        /**
        * Returns the imaginary part of this complex 3-vector.
        */
        public AbstractDoubleVector imag() {
                return new Double3Vector(
                        xim,
                        yim,
                        zim
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
                        case 2 : return new Complex(zre, zim);
                        default : throw new VectorDimensionException("Invalid component.");
                }
        }
        public double getRealComponent(final int n) {
                switch(n) {
                        case 0 : return xre;
                        case 1 : return yre;
                        case 2 : return zre;
                        default : throw new VectorDimensionException("Invalid component.");
                }
        }
        public double getImagComponent(final int n) {
                switch(n) {
                        case 0 : return xim;
                        case 1 : return yim;
                        case 2 : return zim;
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
                        case 2 : zre = z.real(); zim = z.imag(); break;
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
                        case 2 : zre = x; zim = y; break;
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
                        +zre*zre + zim*zim
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
                modSq = zre*zre + zim*zim;
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
                return new Complex3Vector(
                        -xre, -xim,
                        -yre, -yim,
                        -zre, -zim
                );
        }

// COMPLEX CONJUGATE

        /**
        * Returns the complex conjugate of this vector.
        * @return a complex 3-vector
        */
        public AbstractComplexVector conjugate() {
                return new Complex3Vector(
                        xre, -xim,
                        yre, -yim,
                        zre, -zim
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
        * @param vec a complex 3-vector
        */
        public AbstractComplexVector add(final AbstractComplexVector vec) {
                if(vec.N == 3) {
                        return new Complex3Vector(
                                xre+vec.getComponent(0).real(), xim+vec.getComponent(0).imag(),
                                yre+vec.getComponent(1).real(), yim+vec.getComponent(1).imag(),
                                zre+vec.getComponent(2).real(), zim+vec.getComponent(2).imag()
                        );
                } else
                        throw new VectorDimensionException("Vectors are different sizes.");
        }
        /**
        * Returns the addition of this vector and another.
        * @param vec a double 3-vector
        */
        public AbstractComplexVector add(final AbstractDoubleVector vec) {
                if(vec.N == 3) {
                        return new Complex3Vector(
                                xre+vec.getComponent(0), xim,
                                yre+vec.getComponent(1), yim,
                                zre+vec.getComponent(2), zim
                        );
                } else
                        throw new VectorDimensionException("Vectors are different sizes.");
        }
        /**
        * Returns the addition of this vector and another.
        * @param vec an integer 3-vector
        */
        public AbstractComplexVector add(final AbstractIntegerVector vec) {
                if(vec.N == 3) {
                        return new Complex3Vector(
                                xre+vec.getComponent(0), xim,
                                yre+vec.getComponent(1), yim,
                                zre+vec.getComponent(2), zim
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
        * @param vec a complex 3-vector
        */
        public AbstractComplexVector subtract(final AbstractComplexVector vec) {
                if(vec.N == 3) {
                        return new Complex3Vector(
                                xre-vec.getComponent(0).real(), xim-vec.getComponent(0).imag(),
                                yre-vec.getComponent(1).real(), yim-vec.getComponent(1).imag(),
                                zre-vec.getComponent(2).real(), zim-vec.getComponent(2).imag()
                        );
                } else
                        throw new VectorDimensionException("Vectors are different sizes.");
        }
        /**
        * Returns the subtraction of this vector by another.
        * @param vec a double 3-vector
        */
        public AbstractComplexVector subtract(final AbstractDoubleVector vec) {
                if(vec.N == 3) {
                        return new Complex3Vector(
                                xre-vec.getComponent(0), xim,
                                yre-vec.getComponent(1), yim,
                                zre-vec.getComponent(2), zim
                        );
                } else
                        throw new VectorDimensionException("Vectors are different sizes.");
        }
        /**
        * Returns the subtraction of this vector by another.
        * @param vec an integer 3-vector
        */
        public AbstractComplexVector subtract(final AbstractIntegerVector vec) {
                if(vec.N == 3) {
                        return new Complex3Vector(
                                xre-vec.getComponent(0), xim,
                                yre-vec.getComponent(1), yim,
                                zre-vec.getComponent(2), zim
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
        * @return a complex 3-vector
        */
        public AbstractComplexVector scalarMultiply(final Complex z) {
                final double real=z.real();
                final double imag=z.imag();
                return new Complex3Vector(
                        xre*real-xim*imag, xre*imag+xim*real,
                        yre*real-yim*imag, yre*imag+yim*real,
                        zre*real-zim*imag, zre*imag+zim*real
                );
        }
        /**
        * Returns the multiplication of this vector by a scalar.
        * @param k a double
        * @return a complex 3-vector
        */
        public AbstractComplexVector scalarMultiply(final double k) {
                return new Complex3Vector(
                        k*xre, k*xim,
                        k*yre, k*yim,
                        k*zre, k*zim
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
        * @return a complex 3-vector
        * @exception ArithmeticException If divide by zero.
        */
        public AbstractComplexVector scalarDivide(final Complex z) {
                final double real=z.real();
                final double imag=z.imag();
                final double a,denom;
                if(Math.abs(real)<Math.abs(imag)) {
                        a=real/imag;
                        denom=real*a+imag;
                        return new Complex3Vector(
                                (xre*a+xim)/denom, (xim*a-xre)/denom,
                                (yre*a+yim)/denom, (yim*a-yre)/denom,
                                (zre*a+zim)/denom, (zim*a-zre)/denom
                        );
                } else {
                        a=imag/real;
                        denom=real+imag*a;
                        return new Complex3Vector(
                                (xre+xim*a)/denom, (xim-xre*a)/denom,
                                (yre+yim*a)/denom, (yim-yre*a)/denom,
                                (zre+zim*a)/denom, (zim-zre*a)/denom
                        );
                }
        }
        /**
        * Returns the division of this vector by a scalar.
        * @param k a double
        * @return a complex 3-vector
        * @exception ArithmeticException If divide by zero.
        */
        public AbstractComplexVector scalarDivide(final double k) {
                return new Complex3Vector(
                        xre/k, xim/k,
                        yre/k, yim/k,
                        zre/k, zim/k
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
                if(vec instanceof Complex3Vector)
                        return scalarProduct((Complex3Vector)vec);
                else {
                        if(vec.N == 3) {
                                return new Complex(
                                        xre*vec.getComponent(0).real()+xim*vec.getComponent(0).imag()+
                                        yre*vec.getComponent(1).real()+yim*vec.getComponent(1).imag()+
                                        zre*vec.getComponent(2).real()+zim*vec.getComponent(2).imag(),
                                        xim*vec.getComponent(0).real()-xre*vec.getComponent(0).imag()+
                                        yim*vec.getComponent(1).real()-yre*vec.getComponent(1).imag()+
                                        zim*vec.getComponent(2).real()-zre*vec.getComponent(2).imag()
                                );
                        } else
                                throw new VectorDimensionException("Vectors are different sizes.");
                }
        }
        /**
        * Returns the scalar product of this vector and another.
        * @param vec a complex 3-vector
        */
        public Complex scalarProduct(final Complex3Vector vec) {
                return new Complex(
                        xre*vec.xre+xim*vec.xim+
                        yre*vec.yre+yim*vec.yim+
                        zre*vec.zre+zim*vec.zim,
                        xim*vec.xre-xre*vec.xim+
                        yim*vec.yre-yre*vec.yim+
                        zim*vec.zre-zre*vec.zim
                );
        }

// VECTOR PRODUCT

        /**
        * Returns the vector product of this vector and another.
        * @param vec a complex 3-vector
        */
        public Complex3Vector multiply(final Complex3Vector vec) {
                return new Complex3Vector(
                        yre*vec.zre-yim*vec.zim-zre*vec.yre+zim*vec.yim,
                        yre*vec.zim+yim*vec.zre-zre*vec.yim-zim*vec.yre,
                        zre*vec.xre-zim*vec.xim-xre*vec.zre+xim*vec.zim,
                        zre*vec.xim+zim*vec.xre-xre*vec.zim-xim*vec.zre,
                        xre*vec.yre-xim*vec.yim-yre*vec.xre+yim*vec.xim,
                        xre*vec.yim+xim*vec.yre-yre*vec.xim-yim*vec.xre
                );
        }

// MAP COMPONENTS

        /**
        * Applies a function on all the vector components.
        * @param mapping a user-defined function
        * @return a complex 3-vector
        */
        public AbstractComplexVector mapComponents(final ComplexMapping mapping) {
                return new Complex3Vector(
                        mapping.map(xre, xim),
                        mapping.map(yre, yim),
                        mapping.map(zre, zim)
                );
        }
}
