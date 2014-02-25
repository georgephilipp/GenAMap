package JSci.physics;

import JSci.maths.NumericalConstants;

/**
* The RigidBody3D class provides an object for encapsulating rigid bodies that live in 3D.
* @version 1.0
* @author Mark Hale
*/
public class RigidBody3D extends ClassicalParticle3D {
        /**
        * Moment of inertia.
        */
        protected double angMass;
        /**
        * Angles (orientation).
        */
        protected double angx, angy, angz;
        /**
        * Angular velocity.
        */
        protected double angxVel, angyVel, angzVel;
        /**
        * Constructs a rigid body.
        */
        public RigidBody3D() {}
        /**
        * Sets the moment of inertia.
        */
        public void setMomentOfInertia(double MoI) {
                angMass = MoI;
        }
        /**
        * Returns the moment of inertia.
        */
        public double getMomentOfInertia() {
                return angMass;
        }
        /**
        * Sets the angles (orientation) of this body.
        * @param angleX an angle in radians.
        * @param angleY an angle in radians.
        * @param angleZ an angle in radians.
        */
        public void setAngles(double angleX, double angleY, double angleZ) {
                angx = angleX;
                angy = angleY;
                angz = angleZ;
        }
        /**
        * Returns the x-axis angle of this body.
        * @return an angle in radians.
        */
        public double getXAngle() {
                return angx;
        }
        /**
        * Returns the y-axis angle of this body.
        * @return an angle in radians.
        */
        public double getYAngle() {
                return angy;
        }
        /**
        * Returns the z-axis angle of this body.
        * @return an angle in radians.
        */
        public double getZAngle() {
                return angz;
        }
        public void setAngularVelocity(double angleXVel, double angleYVel, double angleZVel) {
                angxVel = angleXVel;
                angyVel = angleYVel;
                angzVel = angleZVel;
        }
        public double getXAngularVelocity() {
                return angxVel;
        }
        public double getYAngularVelocity() {
                return angyVel;
        }
        public double getZAngularVelocity() {
                return angzVel;
        }
        public void setAngularMomentum(double angleXMom, double angleYMom, double angleZMom) {
                angxVel = angleXMom/angMass;
                angyVel = angleYMom/angMass;
                angzVel = angleZMom/angMass;
        }
        public double getXAngularMomentum() {
                return angMass*angxVel;
        }
        public double getYAngularMomentum() {
                return angMass*angyVel;
        }
        public double getZAngularMomentum() {
                return angMass*angzVel;
        }
        /**
        * Returns the kinetic and rotational energy.
        */
        public double energy() {
                return (mass*(vx*vx+vy*vy+vz*vz)+angMass*(angxVel*angxVel+angyVel*angyVel+angzVel*angzVel))/2.0;
        }
        /**
        * Evolves this particle forward according to its kinematics.
        * This method changes the particle's position and orientation.
        * @return this.
        */
        public ClassicalParticle3D move(double dt) {
                return rotate(dt).translate(dt);
        }
        /**
        * Evolves this particle forward according to its rotational kinematics.
        * This method changes the particle's orientation.
        * @return this.
        */
        public RigidBody3D rotate(double dt) {
                angx += angxVel*dt;
                if(angx > NumericalConstants.TWO_PI)
                        angx -= NumericalConstants.TWO_PI;
                else if(angx < 0.0)
                        angx += NumericalConstants.TWO_PI;
                angy += angyVel*dt;
                if(angy > NumericalConstants.TWO_PI)
                        angy -= NumericalConstants.TWO_PI;
                else if(angy < 0.0)
                        angy += NumericalConstants.TWO_PI;
                angz += angzVel*dt;
                if(angz > NumericalConstants.TWO_PI)
                        angz -= NumericalConstants.TWO_PI;
                else if(angz < 0.0)
                        angz += NumericalConstants.TWO_PI;
                return this;
        }
        /**
        * Accelerates this particle.
        * This method changes the particle's angular velocity.
        * It is additive, that is <code>angularAccelerate(a1, dt).angularAccelerate(a2, dt)</code>
        * is equivalent to <code>angularAccelerate(a1+a2, dt)</code>.
        * @return this.
        */
        public RigidBody3D angularAccelerate(double ax, double ay, double az, double dt) {
                angxVel += ax*dt;
                angyVel += ay*dt;
                angzVel += az*dt;
                return this;
        }
        /**
        * Applies a torque to this particle.
        * This method changes the particle's angular velocity.
        * It is additive, that is <code>applyTorque(T1, dt).applyTorque(T2, dt)</code>
        * is equivalent to <code>applyTorque(T1+T2, dt)</code>.
        * @return this.
        */
        public RigidBody3D applyTorque(double tx, double ty, double tz, double dt) {
                return angularAccelerate(tx/angMass, ty/angMass, tz/angMass, dt);
        }
        /**
        * Applies a force acting at a point away from the centre of mass.
        * Any resultant torques are also applied.
        * This method changes the particle's angular velocity.
        * @param x x-coordinate from centre of mass.
        * @param y y-coordinate from centre of mass.
        * @param z z-coordinate from centre of mass.
        * @return this.
        */
        public RigidBody3D applyForce(double fx, double fy, double fz, double x, double y, double z, double dt) {
                applyTorque(y*fz-z*fy, z*fx-x*fz, x*fy-y*fx, dt); // T = r x F
                final double k=(x*fx+y*fy+z*fz)/(x*x+y*y+z*z); // r.F/|r|^2
                applyForce(k*x, k*y, k*z, dt);
                return this;
        }
}

