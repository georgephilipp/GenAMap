package JSci.physics.quantum;

import JSci.maths.matrices.AbstractComplexMatrix;
import JSci.maths.matrices.AbstractComplexSquareMatrix;

/**
* The DensityMatrix class provides an object for encapsulating density matrices.
* @version 1.5
* @author Mark Hale
*/
public final class DensityMatrix extends Operator {
        private static AbstractComplexSquareMatrix constructor(KetVector kets[],double probs[]) {
                AbstractComplexMatrix rep=(new Projector(kets[0])).getRepresentation().scalarMultiply(probs[0]);
                for(int i=1;i<kets.length;i++)
                        rep=rep.add((new Projector(kets[i])).getRepresentation().scalarMultiply(probs[i]));
                return (AbstractComplexSquareMatrix)rep;
        }
        private static AbstractComplexSquareMatrix constructor(Projector projs[],double probs[]) {
                AbstractComplexMatrix rep=projs[0].getRepresentation().scalarMultiply(probs[0]);
                for(int i=1;i<projs.length;i++)
                        rep=rep.add(projs[i].getRepresentation().scalarMultiply(probs[i]));
                return (AbstractComplexSquareMatrix)rep;
        }

        /**
        * Constructs a density matrix.
        * @param kets an array of ket vectors
        * @param probs the probabilities of being in the ket vector states.
        */
        public DensityMatrix(KetVector kets[],double probs[]) {
                super(constructor(kets,probs));
        }
        /**
        * Constructs a density matrix.
        * @param projs an array of projectors
        * @param probs the probabilities of being in the projector states.
        */
        public DensityMatrix(Projector projs[],double probs[]) {
                super(constructor(projs,probs));
        }
        /**
        * Returns true if this density matrix is a pure state.
        */
        public boolean isPureState() {
                return representation.equals(representation.multiply(representation));
        }
}

