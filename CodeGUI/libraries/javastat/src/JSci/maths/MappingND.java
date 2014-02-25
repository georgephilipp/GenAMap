package JSci.maths;

/**
* This interface defines a map between N-dimensional spaces.
* @see Mapping
* @see ComplexMapping
* @version 1.1
* @author Mark Hale
*/
public interface MappingND {
        /**
        * A user-defined map.
        */
        double[] map(double x[]);
}

