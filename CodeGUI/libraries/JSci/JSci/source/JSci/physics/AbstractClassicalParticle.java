package JSci.physics;

import JSci.maths.vectors.AbstractDoubleVector;

public abstract class AbstractClassicalParticle extends Particle {
	/**
	 * @jsci.planetphysics mass
	 */
        public abstract double getMass();
        public abstract AbstractDoubleVector getPosition();
	/**
	 * @jsci.planetphysics velocity
	 */
        public abstract AbstractDoubleVector getVelocity();
	/**
	 * @jsci.planetphysics momentum
	 */
        public AbstractDoubleVector getMomentum() {
                return getVelocity().scalarMultiply(getMass());
        }
        public double speed() {
                return getVelocity().norm();
        }
        public double energy() {
                AbstractDoubleVector vel = getVelocity();
                return getMass()*vel.scalarProduct(vel)/2.0;
        }
}

