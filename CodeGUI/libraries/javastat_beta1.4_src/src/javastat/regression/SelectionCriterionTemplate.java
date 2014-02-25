package javastat.regression;

/**
 * <p>Title: javastat</p>
 * <p>Description: JAVA programs for statistical computations</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Tung Hai University</p>
 * @author Wen Hsiang Wei
 * @version 1.4
 */

import java.util.*;

import javastat.*;
import static javastat.util.Argument.*;
import javastat.util.*;

import Jama.*;

/**
 *
 * <p>The class defines the required methods and implements the default methods
 * for regression model selection. </p>
 */

public abstract class SelectionCriterionTemplate extends StatisticalAnalysis
{

    /**
     * The responses.
     */

    public double[] response;

    /**
     * The values of the covariates (excluding the one corresponding to the
     * intercept),
     * <br> covariate[j]: the (j+1)'th covariate vector.
     */

    public double[][] covariate;

    /**
     * The weight matrix.
     */

    public double[][] weightMatrix;

    /**
     * The weighted residual sum of squares.
     */

    public double weightedRSS;

    /**
     * The penalty.
     */

    public double penalty;

    /**
     * The weighted model selection criterion.
     */

    public double weightedSelectionCriterion;

    /**
     * The value reflects the effect of the weight matrix on the weighted
     * prediction risk.
     */

    public double psi;

    /**
     * The weight matrix.
     */

    private Matrix W;

    /**
     * The variances of the principal components of the weight matrix.
     */

    private double[] pcaVariance;

    /**
     * Defualt SelectionCriterionTemplate constructor.
     */

    public SelectionCriterionTemplate() {}

    /**
     * The value reflects the effect of the weight matrix on the weighted
     * prediction risk.
     * @param argument the arguments with the following choices,
     * <br> PSI_FUNCTION: the enum in the class PsiFunction;
     * <br> empty argument: default psi value equal to the retained number of
     *                      principal components of the weight matrix.
     * <br><br>
     * @param dataObject the input weight matrix (optinal), responses and values
     * of the covariates (excluding the one corresponding to intercept).
     * @return the value reflects the effect of the weight matrix on the
     *         weighted prediction risk.
     * @exception IllegalArgumentException wrong input argument(s).
     */

    protected double psi(Hashtable argument,
                         Object ...dataObject)
    {
        if (dataObject[0].getClass().getName().equalsIgnoreCase("[D"))
        {
            weightMatrix = new javastat.util.DataManager().identity(((double[])
                    dataObject[0]).length);
        }
        else
        {
            weightMatrix = (double[][]) dataObject[0];
        }
        W = new Matrix(weightMatrix);
        if (((PsiFunction) argument.get(PSI_FUNCTION)) == null ||
            ((PsiFunction) argument.get(PSI_FUNCTION)).equals(PsiFunction.
                PCA_NUMBER))
        {
            pcaVariance = new EigenvalueDecomposition(W).getRealEigenvalues();
            new DataManager().dataSort(pcaVariance);
            psi = weightMatrix.length -
                  new BasicStatistics().pcaNumber(pcaVariance, 0.9);
        }
        else
        {
            switch ((PsiFunction) argument.get(PSI_FUNCTION))
            {
                case SAMPLE_SIZE:
                    psi = weightMatrix[0].length;
                    break;
                case TRACE:
                    psi = W.trace();
                    break;
                default:
                    throw new IllegalArgumentException(
                        "Wrong input argument(s).");
            }
        }

        return psi;
    }

    /**
     * The weighted residual sum of squares.
     * @param argument the arguments.
     * @param dataObject the input data.
     * @return the weighted residual sum of squares.
     */

    public abstract Object weightedRSS(Hashtable argument,
                                       Object ...dataObject);

    /**
     * The penalty.
     * @param argument the arguments.
     * @param dataObject the input data.
     * @return the penalty.
     */

    public abstract Object penalty(Hashtable argument,
                                   Object ...dataObject);

    /**
     * Calculates the weighted selection criterion for a linear regression model
     * or a P-spline model given the input arguments and data.
     * @param argument the arguments with the following choices,
     * <br> SMOOTHING_PARAMETER, DIVISIONS, DEGREE, ORDER, SELECTION CRITERION,
     *      PSI_FUNCTION: complete list of arguments for a P-spline model;
     * <br> SMOOTHING_PARAMETER, DIVISIONS, DEGREE, ORDER, SELECTION CRITERION:
     *      default psi value equal to the retained number of principal
     *      components of the weight matrix for a P-spline model;
     * <br> SMOOTHING_PARAMETER, DIVISIONS, DEGREE, ORDER:
     *      default psi value equal to the retained number of principal
     *      components of the weight matrix and GCV being used for a P-spline
     *      model;
     * <br> SELECTION CRITERION, PSI_FUNCTION:
     *      complete list of arguments for a linear regression model;
     * <br> SELECTION CRITERION:
     *      default psi value equal to the retained number of principal
     *      components of the weight matrix for a linear regression model;
     * <br> empty argument:
     *      default psi value equal to the retained number of principal
     *      components of the weight matrix and AIC being used for a linear
     *      regression model;
     * <br><br>
     * @param dataObject the input weight matrix (optinal), responses and
     *                   values of the covariates (excluding the one
     *                   corresponding to intercept).
     * @return the weighted selection criterion.
     * @exception IllegalArgumentException wrong input data.
     * @exception  IllegalArgumentException wrong input argument(s) or data.
     */

    public Object weightedSelectionCriterion(Hashtable argument,
                                             Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        weightedSelectionCriterion =
            (Double) weightedRSS(argument, dataObject) /
            ((Double) psi(argument, dataObject) *
             (Double) penalty(argument, dataObject));
        output.put(Output.SELECTION_CRITERION, weightedSelectionCriterion);

        return weightedSelectionCriterion;
    }

}
