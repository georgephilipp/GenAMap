package javastat.algorithm;

/**
 * <p>Title: javastat</p>
 * <p>Description: JAVA programs for statistical computations</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Tung Hai University</p>
 * @author Wen Hsiang Wei
 * @version 1.4
 */

import javax.swing.*;

import Jama.Matrix;


/**
 * The class defines the required methods for the sub-classes using
 * Newton-Raphson algorithm and implements the
 * Newton-Raphson algorithm.
 */

public abstract class NewtonRaphsonAlgorithm
{

    /**
     * Convergence threshold for the Newton-Raphson algorithm.
     */

    public double accuracy = 1.0e-6;

    /**
     * Maximum number of iterations.
     */

    public int maxIteration = 200;

    /**
     * The solutions.
     */

    public double[] solution;

    /**
     * The iterated vector of solutions.
     */

    private Matrix updatedSolutionVector;

    /**
     * The vector of solutions.
     */

    private Matrix solutionVector;

    /**
     * The vector of the differences between the iterated vectors of solutions.
     */

    private Matrix differenceVector;

    /**
     * The vector of first derivatives of the object functions.
     */

    private Matrix firstDerivativeMatrix;

    /**
     * Default NewtonRaphsonAlgorithm constructor.
     */

    public NewtonRaphsonAlgorithm(){}

    /**
     * The abstract method (need to be implemented in sub-classes) for computing
     * the object functions evaluated at the iterated solutions.
     * @param iteratedSolution the iterated vector of solutions.
     * @return the object functions evaluated at the iterated solutions.
     */

    public abstract double[] objectFunctionVector(double[] iteratedSolution);

    /**
     * The abstract method (need to be implemented in sub-classes) for computing
     * the first derivatives of the object functions evaluated at the iterated
     * solutions.
     * @param iteratedSolution the iterated vector of solutions.
     * @return the first derivatives of the object functions evaluated at the
     *         iterated solutions.
     */

    public abstract double[][] firstDerivativeMatrix(double[] iteratedSolution);

    /**
     * Returns the vector of solutions obtained by the Newton-Raphson algorithm.
     * @param initialValue the vector of initial values.
     * @return the vector of solutions.
     * @exception IllegalArgumentException the first derivative matrix of
     *                                     the object functions is singular.
     */

    public double[] getSolution(double[] initialValue)
    {
        solutionVector = new Matrix(initialValue, initialValue.length);
        updatedSolutionVector = new Matrix(initialValue, initialValue.length);
        differenceVector = new Matrix(initialValue.length, 1, 100);
        firstDerivativeMatrix = new Matrix(initialValue.length,
                                           initialValue.length);
        int i = 0;
        while (differenceVector.normF() > accuracy && i++ < maxIteration)
        {
            firstDerivativeMatrix = new Matrix(firstDerivativeMatrix(
                    solutionVector.getColumnPackedCopy()));
            if (firstDerivativeMatrix.det() != 0.0)
            {
                differenceVector = firstDerivativeMatrix.inverse().times(
                        new Matrix(objectFunctionVector(solutionVector.
                        getColumnPackedCopy()), initialValue.length));
                updatedSolutionVector = solutionVector.minus(differenceVector);
            }
            else
            {
                throw new IllegalArgumentException(
                        "The first derivative matrix of the object functions is "
                        + "singular.");
            }
            solutionVector = updatedSolutionVector;
        }
        if (i == maxIteration)
        {
            JOptionPane.showMessageDialog(
                    null,
                    "The maximum number of iterations have been acheived.");
        }

        return solutionVector.getColumnPackedCopy();
    }

}
