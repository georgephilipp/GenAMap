package JSci.physics;

/**
* The Torque3D class provides an object for encapsulating torques in 3D.
* @version 1.0
* @author Mark Hale
*/
public abstract class Torque3D {
        public abstract double getXComponent(double t);
        public abstract double getYComponent(double t);
        public abstract double getZComponent(double t);
}

