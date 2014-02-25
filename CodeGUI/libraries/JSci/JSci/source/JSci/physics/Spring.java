package JSci.physics;

import JSci.maths.vectors.AbstractDoubleVector;
import JSci.maths.vectors.DoubleVector;

public class Spring {
        private final double coefficient;
        private AbstractDoubleVector x;

        /**
        * Constructs a mechanical spring.
        * @param coeff the spring constant.
        */
        public Spring(double coeff) {
                coefficient = coeff;
        }
        public void setPosition(AbstractDoubleVector pos) {
                x = pos;
        }
        public AbstractDoubleVector getPosition() {
                return x;
        }
        /**
        * Returns the potential energy of a particle attached to this spring.
        */
        public double energy(AbstractClassicalParticle p) {
                double rr = 0.0;
                AbstractDoubleVector ppos = p.getPosition();
                for(int i=0; i<x.dimension(); i++) {
                        final double dx = ppos.getComponent(i)-x.getComponent(i);
                        rr += dx*dx;
                }
                return coefficient*rr/2.0;
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
                        AbstractDoubleVector ppos = p.getPosition();
                        final double vec[] = new double[x.dimension()];
                        for(int i=0; i<vec.length; i++)
                                vec[i] = -coefficient*(ppos.getComponent(i)-x.getComponent(i));
                        return new DoubleVector(vec);
                }
        }
}

