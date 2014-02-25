package JSci.physics;

/**
* The ConstantForce3D class provides an object for encapsulating constant forces in 3D.
* @version 1.0
* @author Mark Hale
* @author Silvere Martin-Michiellot
*/
public class ConstantForce3D extends Force3D {
        protected final double Fx, Fy, Fz;
        /**
        * Constructs a force.
        */
        public ConstantForce3D(double fx, double fy, double fz) {
                Fx = fx;
                Fy = fy;
                Fz = fz;
        }
        /**
        * Returns the addition of this force and another.
        */
        public Force3D add(ConstantForce3D F) {
                return new ConstantForce3D(Fx+F.Fx, Fy+F.Fy, Fz+F.Fz);
        }
        /**
        * Returns the subtraction of this force by another.
        */
        public Force3D subtract(ConstantForce3D F) {
                return new ConstantForce3D(Fx-F.Fx, Fy-F.Fy, Fz-F.Fz);
        }
        public double getXComponent(double t) {
                return Fx;
        }
        public double getYComponent(double t) {
                return Fy;
        }
        public double getZComponent(double t) {
                return Fz;
        }
}

