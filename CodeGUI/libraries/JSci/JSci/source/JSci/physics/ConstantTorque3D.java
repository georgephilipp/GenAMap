package JSci.physics;

/**
* The ConstantTorque3D class provides an object for encapsulating constant torques in 3D.
* @version 1.0
* @author Mark Hale
*/
public class ConstantTorque3D extends Torque3D {
        protected final double Tx, Ty, Tz;
        /**
        * Constructs a torque.
        */
        public ConstantTorque3D(double wx, double wy, double wz) {
                Tx = wx;
                Ty = wy;
                Tz = wz;
        }
        /**
        * Returns the addition of this torque and another.
        */
        public Torque3D add(ConstantTorque3D T) {
                return new ConstantTorque3D(Tx+T.Tx, Ty+T.Ty, Tz+T.Tz);
        }
        /**
        * Returns the subtraction of this torque by another.
        */
        public Torque3D subtract(ConstantTorque3D T) {
                return new ConstantTorque3D(Tx-T.Tx, Ty-T.Ty, Tz-T.Tz);
        }
        public double getXComponent(double t) {
                return Tx;
        }
        public double getYComponent(double t) {
                return Ty;
        }
        public double getZComponent(double t) {
                return Tz;
        }
}

