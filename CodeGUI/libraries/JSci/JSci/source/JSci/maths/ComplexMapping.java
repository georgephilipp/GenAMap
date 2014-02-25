package JSci.maths;

/**
* This interface defines a complex map or function.
* It is used to pass user-defined functions to some of
* the other maths classes.
* It is expected that <code>map(z)</code> gives an identical result to <code>map(z.real(), z.imag())</code>.
* @see NumericalMath
* @see Mapping
* @see MappingND
* @version 1.1
* @author Mark Hale
*/
public interface ComplexMapping {
        /**
        * A user-defined complex function.
        */
        Complex map(Complex z);
        /**
        * A user-defined complex function.
        * This method is designed to save the construction of a Complex object in cases where one is not given.
        */
        Complex map(double real, double imag);
}

