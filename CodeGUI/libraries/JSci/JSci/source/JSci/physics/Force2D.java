package JSci.physics;

import JSci.maths.vectors.AbstractDoubleVector;
import JSci.maths.vectors.Double2Vector;

/**
* The Force2D class provides an object for encapsulating forces in 2D.
* @version 1.1
* @author Mark Hale
*/
public abstract class Force2D extends Force {
        public abstract double getXComponent(double t);
        public abstract double getYComponent(double t);
        public AbstractDoubleVector getVector(double t) {
                return new Double2Vector(getXComponent(t), getYComponent(t));
        }
}
