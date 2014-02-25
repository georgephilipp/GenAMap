package JSci.physics;

public class GravityField {
        private final ClassicalParticle3D source;

        /**
        * Constructs the gravitational field of a particle.
        */
        public GravityField(ClassicalParticle3D p) {
                source = p;
        }
        /**
        * Returns the potential energy of a particle in this field.
        */
        public double energy(ClassicalParticle3D p) {
                final double dx = p.x-source.x;
                final double dy = p.y-source.y;
                final double dz = p.z-source.z;
                final double r = Math.sqrt(dx*dx+dy*dy+dz*dz);
                return -PhysicalConstants.GRAVITATION*source.getMass()*p.getMass()/r;
        }
        /**
        * Creates the force acting on a particle in this field.
        */
        public Force3D createForce(ClassicalParticle3D p) {
                return new Force(p);
        }
        private class Force extends Force3D {
                private final ClassicalParticle3D p;
                private double x, y, z;
                public Force(ClassicalParticle3D p) {
                        this.p = p;
                }
                private void calculate() {
                        final double dx = p.x-source.x;
                        final double dy = p.y-source.y;
                        final double dz = p.z-source.z;
                        final double rr = dx*dx+dy*dy+dz*dz;
                        final double r = Math.sqrt(rr);
                        final double magnitude = -PhysicalConstants.GRAVITATION*source.getMass()*p.getMass()/rr;
                        x = magnitude*dx/r;
                        y = magnitude*dy/r;
                        z = magnitude*dz/r;
                }
                public double getXComponent(double t) {
                        calculate();
                        return x;
                }
                public double getYComponent(double t) {
                        calculate();
                        return y;
                }
                public double getZComponent(double t) {
                        calculate();
                        return z;
                }
        }
}

