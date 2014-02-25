package JSci.util;

import JSci.maths.Complex;
import JSci.maths.vectors.*;

/**
* This is a useful collection of vector related methods.
* @author Mark Hale
*/
public final class VectorToolkit {
        private VectorToolkit() {}

        /**
        * Creates a random generated vector.
        */
        public static AbstractDoubleVector randomVector(int size) {
                return new DoubleVector(size).mapComponents(RandomMap.MAP);
        }
        /**
        * Creates a random generated vector.
        */
        public static AbstractComplexVector randomComplexVector(int size) {
                return new ComplexVector(size).mapComponents(RandomMap.MAP);
        }
        /**
        * Converts a vector to an array.
        */
        public static double[] toArray(AbstractDoubleVector v) {
                double array[]=new double[v.dimension()];
                array[0]=v.getComponent(0);
                for(int i=1;i<array.length;i++)
                        array[i]=v.getComponent(i);
                return array;
        }
        /**
        * Converts a vector to an array.
        */
        public static Complex[] toArray(AbstractComplexVector v) {
                Complex array[]=new Complex[v.dimension()];
                array[0]=v.getComponent(0);
                for(int i=1;i<array.length;i++)
                        array[i]=v.getComponent(i);
                return array;
        }
}
