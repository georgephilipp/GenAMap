package JSci.physics;

import JSci.maths.vectors.AbstractDoubleVector;
import JSci.maths.vectors.Double3Vector;

/**
* The Force3D class provides an object for encapsulating forces in 3D.
* @version 1.0
* @author Mark Hale
*/
public abstract class Force3D extends Force {
        public abstract double getXComponent(double t);
        public abstract double getYComponent(double t);
        public abstract double getZComponent(double t);
        public AbstractDoubleVector getVector(double t) {
                return new Double3Vector(getXComponent(t), getYComponent(t), getZComponent(t));
        }
}

