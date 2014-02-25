package JSci.physics;

import JSci.maths.vectors.AbstractDoubleVector;
import JSci.maths.vectors.DoubleVector;

/**
* The drag force is proportional to the square of a particle's speed.
* D(v) = -k |v| v
*/
public class Drag {
        private final double coefficient;

        public Drag(double coeff) {
                coefficient = coeff;
        }
        public Force createForce(AbstractClassicalParticle p) {
                return new Force(p);
        }
        private class Force extends JSci.physics.Force {
                private final AbstractClassicalParticle p;
                public Force(AbstractClassicalParticle p) {
                        this.p = p;
                }
                public AbstractDoubleVector getVector(double t) {
                        AbstractDoubleVector pvel = p.getVelocity();
                        final double vec[] = new double[pvel.dimension()];
                        final double k = -coefficient*p.speed();
                        for(int i=0; i<vec.length; i++)
                                vec[i] = k*pvel.getComponent(i);
                        return new DoubleVector(vec);
                }
        }
}

