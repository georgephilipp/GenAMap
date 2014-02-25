package JSci.physics.quantum;

import JSci.maths.Complex;
import JSci.maths.Member;
import JSci.maths.DimensionException;
import JSci.maths.MaximumIterationsExceededException;
import JSci.maths.matrices.AbstractComplexSquareMatrix;
import JSci.maths.vectors.AbstractComplexVector;
import JSci.maths.vectors.ComplexVector;

/**
* The Operator class provides an object for encapsulating quantum mechanical operators.
* @version 1.5
* @author Mark Hale
*/
public class Operator implements Member {
        protected AbstractComplexSquareMatrix representation;

        /**
        * Constructs an operator given a matrix representation.
        * @param rep a matrix representation
        */
        public Operator(AbstractComplexSquareMatrix rep) {
                representation=rep;
        }
        /**
        * Compares two operators for equality.
        * @param a an operator
        */
        public boolean equals(Object a) {
                return representation.equals(((Operator)a).representation);
        }
        /**
        * Returns a string representing this operator.
        */
        public String toString() {
                return representation.toString();
        }
        /**
        * Returns a hashcode for this operator.
        */
        public int hashCode() {
                return (int)Math.exp(trace().mod());
        }
        /**
        * Returns the representation.
        */
        public AbstractComplexSquareMatrix getRepresentation() {
                return representation;
        }
        /**
        * Returns true if this operator is self-adjoint.
        */
        public boolean isSelfAdjoint() {
                return representation.isHermitian();
        }
        /**
        * Returns true if this operator is unitary.
        */
        public boolean isUnitary() {
                return representation.isUnitary();
        }
        /**
        * Returns the trace.
        */
        public Complex trace() {
                return representation.trace();
        }
        /**
        * Returns the operator norm.
        */
        public double norm() {
                try {
                        return representation.operatorNorm();
                } catch(MaximumIterationsExceededException e) {
                        return -1.0;
                }
        }
        /**
        * Returns the dimension.
        */
        public int dimension() {
                return representation.columns();
        }
	public Object getSet() {
		return representation.getSet();
	}

//============
// OPERATIONS
//============

// ADDITION

        /**
        * Returns the addition of this operator and another.
        * @param op an operator
        * @exception MatrixDimensionException If the operators have different dimensions.
        */
        public Operator add(Operator op) {
                return new Operator(representation.add(op.representation));
        }

// SUBTRACTION

        /**
        * Returns the subtraction of this operator and another.
        * @param op an operator
        * @exception MatrixDimensionException If the operators have different dimensions.
        */
        public Operator subtract(Operator op) {
                return new Operator(representation.subtract(op.representation));
        }

// MULTIPLICATION

        /**
        * Returns the multiplication of this operator and another.
        * @param op an operator
        * @exception MatrixDimensionException If the operators have different dimensions.
        */
        public Operator multiply(Operator op) {
                return new Operator(representation.multiply(op.representation));
        }
        /**
        * Returns the multiplication of this operator and a ket vector.
        * @param ket a ket vector
        * @exception DimensionException If the operator and vector have different dimensions.
        */
        public KetVector multiply(KetVector ket) {
                int opDim=dimension();
                if(opDim==ket.dimension()) {
                        AbstractComplexVector ketRep=ket.getRepresentation();
                        Complex tmp,array[]=new Complex[opDim];
                        for(int j,i=0;i<opDim;i++) {
                                tmp=representation.getElement(i,0).multiply(ketRep.getComponent(0));
                                for(j=1;j<opDim;j++)
                                        tmp=tmp.add(representation.getElement(i,j).multiply(ketRep.getComponent(j)));
                                array[i]=tmp;
                        }
                        return new KetVector(new ComplexVector(array));
                } else
                        throw new DimensionException("Operator and vector have different dimensions.");
        }
}

