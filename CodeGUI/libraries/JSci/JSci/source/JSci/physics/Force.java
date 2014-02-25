package JSci.physics;

import JSci.maths.vectors.AbstractDoubleVector;

/**
* A superclass for forces.
* @version 1.0
*/
public abstract class Force extends Object implements java.io.Serializable {
        /**
        * Constructs a force.
        */
        public Force () {}
        /**
        * Returns a vector representing this force at a time <code>t</code>.
        */
        public abstract AbstractDoubleVector getVector(double t);
}

