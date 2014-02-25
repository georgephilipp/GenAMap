package JSci.physics;

import JSci.maths.vectors.AbstractDoubleVector;
import JSci.maths.vectors.DoubleVector;

/**
* The ClassicalParticle class provides an object for encapsulating classical point particles.
* This class is suitable for representing particles that live in an arbitrary number of dimensions.
* @version 1.0
* @author Mark Hale
*/
public class ClassicalParticle extends AbstractClassicalParticle {
        protected double mass;
        protected AbstractDoubleVector x;
        protected AbstractDoubleVector v;
        /**
        * Constructs a classical particle.
        * @param n number of dimensions.
        */
        public ClassicalParticle(int n) {
                x = new DoubleVector(n);
                v = new DoubleVector(n);
        }
        /**
        * Sets the mass of this particle.
        */
        public void setMass(double m) {
                mass = m;
        }
        /**
        * Returns the mass of this particle.
        */
        public double getMass() {
                return mass;
        }
        public void setPosition(AbstractDoubleVector pos) {
                x = pos;
        }
        public AbstractDoubleVector getPosition() {
                return x;
        }
        public void setVelocity(AbstractDoubleVector vel) {
                v = vel;
        }
        public AbstractDoubleVector getVelocity() {
                return v;
        }
        private double speedSqr() {
                return v.scalarProduct(v);
        }
        public double speed() {
                return v.norm();
        }
        public void setMomentum(AbstractDoubleVector momentum) {
                v = momentum.scalarDivide(mass);
        }
        public AbstractDoubleVector getMomentum() {
                return v.scalarMultiply(mass);
        }
        /**
        * Returns the energy of this particle.
        */
        public double energy() {
                return mass*speedSqr()/2.0;
        }
        /**
        * Evolves this particle forward according to its kinematics.
        * This method changes the particle's position.
        * @return this.
        */
        public ClassicalParticle move(double dt) {
                x = x.add(v.scalarMultiply(dt));
                return this;
        }
        /**
        * Accelerates this particle.
        * This method changes the particle's velocity.
        * It is additive, that is <code>accelerate(a1, dt).accelerate(a2, dt)</code>
        * is equivalent to <code>accelerate(a1+a2, dt)</code>.
        * @return this.
        */
        public ClassicalParticle accelerate(AbstractDoubleVector a, double dt) {
                v = v.add(a.scalarMultiply(dt));
                return this;
        }
        /**
        * Applies a force to this particle.
        * This method changes the particle's velocity.
        * It is additive, that is <code>applyForce(F1, dt).applyForce(F2, dt)</code>
        * is equivalent to <code>applyForce(F1+F2, dt)</code>.
        * @return this.
        */
        public ClassicalParticle applyForce(AbstractDoubleVector F,double dt) {
                return accelerate(F.scalarDivide(mass), dt);
        }
}

