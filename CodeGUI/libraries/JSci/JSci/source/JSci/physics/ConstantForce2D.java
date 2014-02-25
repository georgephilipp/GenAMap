package JSci.physics;

/**
* The ConstantForce2D class provides an object for encapsulating constant forces in 2D.
* @version 1.1
* @author Mark Hale
* @author Silvere Martin-Michiellot
*/
public class ConstantForce2D extends Force2D {
        protected final double Fx, Fy;
        /**
        * Constructs a force.
        */
        public ConstantForce2D(double fx, double fy) {
                Fx = fx;
                Fy = fy;
        }
        /**
        * Returns the addition of this force and another.
        */
        public Force2D add(ConstantForce2D F) {
                return new ConstantForce2D(Fx+F.Fx, Fy+F.Fy);
        }
        /**
        * Returns the subtraction of this force by another.
        */
        public Force2D subtract(ConstantForce2D F) {
                return new ConstantForce2D(Fx-F.Fx, Fy-F.Fy);
        }
        public double getXComponent(double t) {
                return Fx;
        }
        public double getYComponent(double t) {
                return Fy;
        }
}

