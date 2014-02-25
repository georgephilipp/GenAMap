package JSci.physics;

import JSci.maths.vectors.AbstractDoubleVector;
import JSci.maths.vectors.Double2Vector;

/**
* The ClassicalParticle2D class provides an object for
* encapsulating classical point particles that live in 2D.
* @version 1.0
* @author Mark Hale
*/
public class ClassicalParticle2D extends AbstractClassicalParticle {
        /**
        * Mass.
        */
        protected double mass;
        /**
        * Position coordinates.
        */
        protected double x, y;
        /**
        * Velocity coordinates.
        */
        protected double vx, vy;
        /**
        * Constructs a classical particle.
        */
        public ClassicalParticle2D() {}
        /**
        * Sets the mass of this particle.
        */
        public void setMass(double m) {
                mass=m;
        }
        /**
        * Returns the mass of this particle.
        */
        public double getMass() {
                return mass;
        }
        /**
        * Sets the position of this particle.
        */
        public void setPosition(double xPos,double yPos) {
                x=xPos;
                y=yPos;
        }
        public AbstractDoubleVector getPosition() {
                return new Double2Vector(x, y);
        }
        public void setXPosition(double xPos) {
                x=xPos;
        }
        public double getXPosition() {
                return x;
        }
        public void setYPosition(double yPos) {
                y=yPos;
        }
        public double getYPosition() {
                return y;
        }
        /**
        * Sets the velocity of this particle.
        */
        public void setVelocity(double xVel,double yVel) {
                vx=xVel;
                vy=yVel;
        }
        public AbstractDoubleVector getVelocity() {
                return new Double2Vector(vx, vy);
        }
        public double getXVelocity() {
                return vx;
        }
        public double getYVelocity() {
                return vy;
        }
        /**
        * Returns the speed of this particle.
        */
        public double speed() {
                return Math.sqrt(vx*vx+vy*vy);
        }
        /**
        * Sets the momentum of this particle.
        */
        public void setMomentum(double xMom,double yMom) {
                vx=xMom/mass;
                vy=yMom/mass;
        }
        public AbstractDoubleVector getMomentum() {
                return new Double2Vector(mass*vx, mass*vy);
        }
        public double getXMomentum() {
                return mass*vx;
        }
        public double getYMomentum() {
                return mass*vy;
        }
        /**
        * Returns the kinetic energy.
        */
        public double energy() {
                return mass*(vx*vx+vy*vy)/2.0;
        }
        /**
        * Evolves this particle forward according to its kinematics.
        * This method changes the particle's position.
        * @return this.
        */
        public ClassicalParticle2D move(double dt) {
                return translate(dt);
        }
        /**
        * Evolves this particle forward according to its linear kinematics.
        * This method changes the particle's position.
        * @return this.
        */
        public ClassicalParticle2D translate(double dt) {
                x+=vx*dt;
                y+=vy*dt;
                return this;
        }
        /**
        * Accelerates this particle.
        * This method changes the particle's velocity.
        * It is additive, that is <code>accelerate(a1, dt).accelerate(a2, dt)</code>
        * is equivalent to <code>accelerate(a1+a2, dt)</code>.
        * @return this.
        */
        public ClassicalParticle2D accelerate(double ax,double ay,double dt) {
                vx+=ax*dt;
                vy+=ay*dt;
                return this;
        }
        /**
        * Applies a force to this particle.
        * This method changes the particle's velocity.
        * It is additive, that is <code>applyForce(F1, dt).applyForce(F2, dt)</code>
        * is equivalent to <code>applyForce(F1+F2, dt)</code>.
        * @return this.
        */
        public ClassicalParticle2D applyForce(double Fx,double Fy,double dt) {
                return accelerate(Fx/mass, Fy/mass, dt);
        }
        /**
        * Evolves two particles under their mutual gravitational attraction.
        * This method changes the velocity of both particles.
        * @return this.
        */
        public ClassicalParticle2D gravitate(ClassicalParticle2D p,double dt) {
                final double dx=p.x-x;
                final double dy=p.y-y;
                final double rr=dx*dx+dy*dy;
                final double r=Math.sqrt(rr);
                final double g=p.mass/rr;
                final double pg=mass/rr;
                vx-=g*dx*dt/r;
                vy-=g*dy*dt/r;
                p.vx+=pg*dx*dt/r;
                p.vy+=pg*dy*dt/r;
                return this;
        }
        /**
        * Collides this particle with another (elastic collision).
        * This method calculates the resultant velocities.
        * @param theta centre of mass deflection angle.
        * @return this.
        */
        public ClassicalParticle2D collide(ClassicalParticle2D p, double theta) {
                final double totalMass = mass+p.mass;
                final double deltaVx = p.vx-vx;
                final double deltaVy = p.vy-vy;
                final double cos = Math.cos(theta);
                final double sin = Math.sin(theta);
                vx += p.mass*(deltaVx*cos+deltaVy*sin+deltaVx)/totalMass;
                vy += p.mass*(deltaVy*cos-deltaVx*sin+deltaVy)/totalMass;
                p.vx -= mass*(deltaVx*cos+deltaVy*sin+deltaVx)/totalMass;
                p.vy -= mass*(deltaVy*cos-deltaVx*sin+deltaVy)/totalMass;
                return this;
        }
}
