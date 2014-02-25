package JSci.physics;

import JSci.maths.vectors.AbstractDoubleVector;
import JSci.maths.vectors.Double3Vector;

/**
* The ClassicalParticle3D class provides an object for
* encapsulating classical point particles that live in 3D.
* @version 1.0
* @author Silvere Martin-Michiellot
* @author Mark Hale
*/
public class ClassicalParticle3D extends AbstractClassicalParticle {
        /**
        * Mass.
        */
        protected double mass;
        /**
        * Position coordinates.
        */
        protected double x, y, z;
        /**
        * Velocity coordinates.
        */
        protected double vx, vy, vz;
        /**
        * Constructs a classical particle.
        */
        public ClassicalParticle3D() {}
        public void setMass(double m) {
                mass=m;
        }
        public double getMass() {
                return mass;
        }
        public void setPosition(double xPos, double yPos, double zPos) {
                x=xPos;
                y=yPos;
                z=zPos;
        }
        public AbstractDoubleVector getPosition() {
                return new Double3Vector(x, y, z);
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
        public void setZPosition(double zPos) {
                z=zPos;
        }
        public double getZPosition() {
                return z;
        }
        public void setVelocity(double xVel,double yVel,double zVel) {
                vx=xVel;
                vy=yVel;
                vz=zVel;
        }
        public AbstractDoubleVector getVelocity() {
                return new Double3Vector(vx, vy, vz);
        }
        public double getXVelocity() {
                return vx;
        }
        public double getYVelocity() {
                return vy;
        }
        public double getZVelocity() {
                return vz;
        }
        public double speed() {
                return Math.sqrt(vx*vx+vy*vy+vz*vz);
        }
        public void setMomentum(double xMom,double yMom,double zMom) {
                vx=xMom/mass;
                vy=yMom/mass;
                vz=zMom/mass;
        }
        public AbstractDoubleVector getMomentum() {
                return new Double3Vector(mass*vx, mass*vy, mass*vz);
        }
        public double getXMomentum() {
                return mass*vx;
        }
        public double getYMomentum() {
                return mass*vy;
        }
        public double getZMomentum() {
                return mass*vz;
        }
        /**
        * Returns the kinetic energy.
        */
        public double energy() {
                return mass*(vx*vx+vy*vy+vz*vz)/2.0;
        }
        /**
        * Evolves this particle forward according to its kinematics.
        * This method changes the particle's position.
        * @return this.
        */
        public ClassicalParticle3D move(double dt) {
                return translate(dt);
        }
        /**
        * Evolves this particle forward according to its linear kinematics.
        * This method changes the particle's position.
        * @return this.
        */
        public ClassicalParticle3D translate(double dt) {
                x+=vx*dt;
                y+=vy*dt;
                z+=vz*dt;
                return this;
        }
        /**
        * Accelerates this particle.
        * This method changes the particle's velocity.
        * It is additive, that is <code>accelerate(a1, dt).accelerate(a2, dt)</code>
        * is equivalent to <code>accelerate(a1+a2, dt)</code>.
        * @return this.
        */
        public ClassicalParticle3D accelerate(double ax,double ay,double az,double dt) {
                vx+=ax*dt;
                vy+=ay*dt;
                vz+=az*dt;
                return this;
        }
        /**
        * Applies a force to this particle.
        * This method changes the particle's velocity.
        * It is additive, that is <code>applyForce(F1, dt).applyForce(F2, dt)</code>
        * is equivalent to <code>applyForce(F1+F2, dt)</code>.
        * @return this.
        */
        public ClassicalParticle3D applyForce(double Fx,double Fy,double Fz,double dt) {
                return accelerate(Fx/mass, Fy/mass, Fz/mass, dt);
        }
        /**
        * Evolves two particles under their mutual gravitational attraction.
        * This method changes the velocity of both particles.
        * @return this.
        */
        public ClassicalParticle3D gravitate(ClassicalParticle3D p,double dt) {
                final double dx=p.x-x;
                final double dy=p.y-y;
                final double dz=p.z-z;
                final double rr=dx*dx+dy*dy+dz*dz;
                final double r=Math.sqrt(rr);
                final double g=p.mass/rr;
                final double pg=mass/rr;
                vx-=g*dx*dt/r;
                vy-=g*dy*dt/r;
                vz-=g*dz*dt/r;
                p.vx+=pg*dx*dt/r;
                p.vy+=pg*dy*dt/r;
                p.vz+=pg*dz*dt/r;
                return this;
        }
}

