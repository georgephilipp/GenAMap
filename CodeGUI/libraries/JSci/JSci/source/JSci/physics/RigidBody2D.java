package JSci.physics;

import JSci.maths.NumericalConstants;

/**
* The RigidBody2D class provides an object for
* encapsulating rigid bodies that live in 2D.
* @version 1.0
* @author Mark Hale
*/
public class RigidBody2D extends ClassicalParticle2D {
        /**
        * Moment of inertia.
        */
        protected double angMass;
        /**
        * Angle (orientation).
        */
        protected double ang;
        /**
        * Angular velocity.
        */
        protected double angVel;
        /**
        * Constructs a rigid body.
        */
        public RigidBody2D() {}
        /**
        * Sets the moment of inertia.
        */
        public void setMomentOfInertia(double MoI) {
                angMass=MoI;
        }
        /**
        * Returns the moment of inertia.
        */
        public double getMomentOfInertia() {
                return angMass;
        }
        /**
        * Sets the angle (orientation) of this body.
        * @param angle an angle in radians.
        */
        public void setAngle(double angle) {
                ang=angle;
        }
        /**
        * Returns the angle (orientation) of this body.
        * @return an angle in radians.
        */
        public double getAngle() {
                return ang;
        }
        /**
        * Sets the angular velocity.
        */
        public void setAngularVelocity(double angleVel) {
                angVel=angleVel;
        }
        /**
        * Returns the angular velocity.
        */
        public double getAngularVelocity() {
                return angVel;
        }
        public void setAngularMomentum(double angleMom) {
                angVel=angleMom/angMass;
        }
        public double getAngularMomentum() {
                return angMass*angVel;
        }
        /**
        * Returns the kinetic and rotational energy.
        */
        public double energy() {
                return (mass*(vx*vx+vy*vy)+angMass*angVel*angVel)/2.0;
        }
        /**
        * Evolves this particle forward according to its kinematics.
        * This method changes the particle's position and orientation.
        * @return this.
        */
        public ClassicalParticle2D move(double dt) {
                return rotate(dt).translate(dt);
        }
        /**
        * Evolves this particle forward according to its rotational kinematics.
        * This method changes the particle's orientation.
        * @return this.
        */
        public RigidBody2D rotate(double dt) {
                ang+=angVel*dt;
                if(ang>NumericalConstants.TWO_PI)
                        ang-=NumericalConstants.TWO_PI;
                else if(ang<0.0)
                        ang+=NumericalConstants.TWO_PI;
                return this;
        }
        /**
        * Accelerates this particle.
        * This method changes the particle's angular velocity.
        * It is additive, that is <code>angularAccelerate(a1, dt).angularAccelerate(a2, dt)</code>
        * is equivalent to <code>angularAccelerate(a1+a2, dt)</code>.
        * @return this.
        */
        public RigidBody2D angularAccelerate(double a, double dt) {
                angVel += a*dt;
                return this;
        }
        /**
        * Applies a torque to this particle.
        * This method changes the particle's angular velocity.
        * It is additive, that is <code>applyTorque(T1, dt).applyTorque(T2, dt)</code>
        * is equivalent to <code>applyTorque(T1+T2, dt)</code>.
        * @return this.
        */
        public RigidBody2D applyTorque(double T, double dt) {
                return angularAccelerate(T/angMass, dt);
        }
        /**
        * Applies a force acting at a point away from the centre of mass.
        * Any resultant torques are also applied.
        * This method changes the particle's angular velocity.
        * @param x x-coordinate from centre of mass.
        * @param y y-coordinate from centre of mass.
        * @return this.
        */
        public RigidBody2D applyForce(double fx, double fy, double x, double y, double dt) {
                applyTorque(x*fy-y*fx, dt); // T = r x F
                final double k=(x*fx+y*fy)/(x*x+y*y); // r.F/|r|^2
                applyForce(k*x, k*y, dt);
                return this;
        }
        /**
        * Collides this particle with another.
        * This method calculates the resultant velocities.
        * @param theta centre of mass deflection angle.
        * @param e coefficient of restitution.
        * @return this.
        */
        public RigidBody2D collide(RigidBody2D p,double theta,double e) {
                final double totalMass = mass+p.mass;
                final double deltaVx = p.vx-vx;
                final double deltaVy = p.vy-vy;
                final double cos = Math.cos(theta);
                final double sin = Math.sin(theta);
                vx += p.mass*(e*(deltaVx*cos+deltaVy*sin)+deltaVx)/totalMass;
                vy += p.mass*(e*(deltaVy*cos-deltaVx*sin)+deltaVy)/totalMass;
                p.vx -= mass*(e*(deltaVx*cos+deltaVy*sin)+deltaVx)/totalMass;
                p.vy -= mass*(e*(deltaVy*cos-deltaVx*sin)+deltaVy)/totalMass;
                return this;
        }
        /**
        * Collides this particle with another.
        * This method calculates the resultant angular velocities.
        * @param e coefficient of restitution.
        * @return this.
        */
        public RigidBody2D angularCollide(RigidBody2D p,double e) {
                final double meanMass = (angMass+p.angMass)/(e+1.0);
                final double delta = p.angVel-angVel;
                angVel += p.angMass*delta/meanMass;
                p.angVel -= angMass*delta/meanMass;
                return this;
        }
}

