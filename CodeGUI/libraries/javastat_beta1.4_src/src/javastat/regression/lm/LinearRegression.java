package javastat.regression.lm;

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
import static javastat.util.Output.*;
import javastat.util.*;

import JSci.maths.statistics.*;

import Jama.*;

/**
 *
 * <p>Calculates the least squares estimate, obtains the ANOVA table and
 * r-square for multiple linear regression model.</p>
 * <p> </p>
 * <br> Example:
 * <br> double [] pizzaResponse = {58, 105, 88, 118, 117, 137, 157, 169, 149,
 *                                 202};
 * <br> double [] pizzaCovariate = {2, 6, 8, 8, 12, 16, 20, 20, 22, 26};
 * <br> double [] hellerResponse = {102, 100, 120, 77, 46, 93, 26, 69, 65, 85};
 * <br> double [] hellerCovariate1 = {120, 140, 190, 130, 155, 175, 125, 145,
 *                                    180, 150};
 * <br> double [] hellerCovariate2 = {100, 110, 90, 150, 210, 150, 250, 270,
 *                                    300, 250};
 * <br>
 * <br> // Non-null constructor
 * <br> LinearRegression myClass1 = new LinearRegression(
 * <br> &nbsp;&nbsp;&nbsp;
 *        0.05, pizzaResponse, pizzaCovariate);
 * <br> double [] coefficients = myClass1.coefficients;
 * <br> double [] residuals = myClass1.residuals;
 * <br> double [] fittedValues = myClass1.fittedValues;
 * <br> double [] testStatistic = myClass1.testStatistic;
 * <br> double [] pValue = myClass1.pValue;
 * <br> double [][] confidenceInterval = {myClass1.confidenceInterval[0],
 * <br> &nbsp;&nbsp;&nbsp;
 *        myClass1.confidenceInterval[1]};
 * <br> double rSquare = myClass1.rSquare;
 * <br> double testFStatistic = myClass1.testFStatistic;
 * <br> double fPValue = myClass1.fPValue;
 * <br>
 * <br> // Null constructor
 * <br> LinearRegression myClass2 = new LinearRegression();
 * <br> coefficients = myClass2.coefficients(
 * <br> &nbsp;&nbsp;&nbsp;
 *        hellerResponse, hellerCovariate1, hellerCovariate2);
 * <br> confInterval = myClass2.confidenceInterval(
 * <br> &nbsp;&nbsp;&nbsp;
 *        0.05, hellerResponse, hellerCovariate1, hellerCovariate2);
 * <br> testStatistic = myClass2.testStatistic(
 * <br> &nbsp;&nbsp;&nbsp;
 *        hellerResponse, hellerCovariate1, hellerCovariate2);
 * <br> pValue = myClass2.pValue(
 * <br> &nbsp;&nbsp;&nbsp;
 *        hellerResponse, hellerCovariate1, hellerCovariate2);
 * <br> testFStatistic = myClass2.testFStatistic(
 * <br> &nbsp;&nbsp;&nbsp;
 *        hellerResponse, hellerCovariate1, hellerCovariate2);
 * <br> fPValue = myClass2.fPValue(
 * <br> &nbsp;&nbsp;&nbsp;
 *        hellerResponse, hellerCovariate1, hellerCovariate2);
 * <br>
 * <br> // Non-null constructor
 * <br> Hashtable argument1 = new Hashtable();
 * <br> argument1.put(ALPHA, 0.05);
 * <br> StatisticalAnalysis myClass3 = new LinearRegression(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument1, pizzaResponse, pizzaCovariate).statisticalAnalysis;
 * <br> confidenceInterval =
 *        (double[][]) myClass3.output.get(CONFIDENCE_INTERVAL);
 * <br> testStatistic = (double[]) myClass3.output.get(TEST_STATISTIC);
 * <br> pValue = (double[]) myClass3.output.get(PVALUE);
 * <br> rSquare = (Double) myClass3.output.get(R_SQUARE);
 * <br> testFStatistic = (Double) myClass3.output.get(F_STATISTIC);
 * <br> fPValue = (Double) myClass3.output.get(F_PVALUE);
 * <br> residuals = (double[]) myClass3.output.get(RESIDUALS);
 * <br> fittedValues = (double[]) myClass3.output.get(FITTED_VALUES);
 * <br>
 * <br> // Null constructor
 * <br> Hashtable argument2 = new Hashtable();
 * <br> LinearRegression myClass4 = new LinearRegression(argument2, null);
 * <br> coefficients = myClass4.coefficients(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument2, hellerResponse, hellerCovariate1, hellerCovariate2);
 * <br> argument2.put(ALPHA, 0.05);
 * <br> confidenceInterval = myClass4.confidenceInterval(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument2, hellerResponse, hellerCovariate1, hellerCovariate2);
 * <br> testStatistic = myClass4.testStatistic(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument2, hellerResponse, hellerCovariate1, hellerCovariate2);
 * <br> pValue = myClass4.pValue(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument2, hellerResponse, hellerCovariate1, hellerCovariate2);
 * <br> testFStatistic = myClass4.testFStatistic(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument2, hellerResponse, hellerCovariate1, hellerCovariate2);
 * <br> fPValue = myClass4.fPValue(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument2, hellerResponse, hellerCovariate1, hellerCovariate2);
 * <br>
 * <br> // Obtains the information about the output
 * <br> out.println(myClass3.output.toString());
 * <br> out.println(myClass4.output.toString());
 */

public class LinearRegression extends Regression
{

    /**
     * The object represents a linear regression analysis.
     */

    public StatisticalAnalysis statisticalAnalysis;

    /**
     * The level of significance.
     */

    public double alpha;

    /**
     * The parameter estimates,
     * <br> coefficients[j]: the parameter estiamte corresponding to the
     *                       (j+1)'th covariate.
     */

    public double[] coefficients;

    /**
     * The variance-covariance matrix of the estimated coefficients,
     * <br> variance[i][j]: the covariance of the (i+1)'th and the (j+1)'th
     *                      estiamted coefficients.
     */

    public double[][] variance;

    /**
     * The fitted values,
     * <br> fittedValues[i]: the fitted value of the (i+1)'th obvervation.
     */

    public double[] fittedValues;

    /**
     * The residuals,
     * <br> residuals[i]: the residual of the (i+1)'th obvervation.
     */

    public double[] residuals;

    /**
     * The t statistics for individual coefficients,
     * <br> testStstatistic[j]=coefficients[j]/Math.sqrt(variance[j][j]):
     *      the t statistic corresponding to the (j+1)'th coefficient estimate.
     */

    public double[] testStatistic;

    /**
     * The confidence intervals for the estimated coefficients,
     * <br> confidenceInterval[j][0]: the lower bound for the (j+1)'th estimated
     *                                coefficient;
     * <br> confidenceInterval[j][1]: the upper bound for the (j+1)'th estimated
     *                                coefficient.
     */

    public double[][] confidenceInterval;

    /**
     * The p values for the t tests,
     * <br> pValue[j]: the p value corresponding to the (j+1)'th parameter.
     */

    public double[] pValue;

    /**
     * Regression sum of squares.
     */

    public double SSR;

    /**
     * Residual (Error) sum of squares.
     */

    public double SSE;

    /**
     * Total sum of squares (corrected).
     */

    public double SST;

    /**
     * Mean residual sum of squares.
     */

    public double MSE;

    /**
     * The degrees of freedom of SSR, SSE, and SST.
     * <br> degreeFreedom[0]: degree of freedom of SSR;
     * <br> degreeFreedom[1]: degree of freedom of SSE;
     * <br> degreeFreedom[2]: degree of freedom of SST.
     */

    public double[] degreeFreedom;

    /**
     * R square.
     */

    public double rSquare;

    /**
     * F statistic for testing if the coefficients (excluding intercept) are
     * significant.
     */

    public double testFStatistic;

    /**
     * The p value for the f test.
     */

    public double fPValue;

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
     * The hat matrix.
     */

    public double[][] hatMatrix;

    /**
     * The index indicating if the column corresponding to
     * the intercept needs to be added.
     */

    public boolean hasIntercept = true;

    /**
     * The values of the covariates.
     */

    private double[][] doubleCovariate;

    /**
     * The constant such that the upper-tailed probability of the normal
     * random variable is equal to alpha/2.
     */

    private double zAlpha;

    /**
     * The values of the covariates (including the one corresponding to the
     * intercept).
     */

    private double[][] covariateWithIntercept;

    /**
     * The response in matrix form.
     */

    private Matrix responseMatrix;

    /**
     * The covariate values in matrix form.
     */

    private Matrix covariateMatrix;

    /**
     * The estimated coefficients in matrix form.
     */

    private Matrix coefficientMatrix;

    /**
     * The fitted values in matrix form.
     */

    private Matrix fittedMatrix;

    /**
     * The residuals in matrix form.
     */

    private Matrix residualMatrix;

    /**
     * The variance of the estimated coefficients in matrix form.
     */

    private Matrix varianceMatrix;

    /**
     * Total sum of squares in matrix form.
     */

    private Matrix SSTMatrix;

    /**
     * Regression sum of squares in matrix form.
     */

    private Matrix SSRMatrix;

    /**
     * Residual (Error) sum of squares in matrix form.
     */

    private Matrix SSEMatrix;


    /**
     * The class contains the collections of some basic methods used in
     * statistical computations.
     */

    private BasicStatistics basicStatistics;

    /**
     * The index indicating the number of columns should be added to the
     * covariate matrix.
     */

    private int addedColumnLength;

    /**
     * Constructs a multiple linear regression model.
     */

    public LinearRegression() {}

    /**
     * Constructs a multiple linear regression model given the input arguments
     * and data.
     * @param argument the argument with the following choices,
     * <br> ALPHA: the level of significance;
     * <br> empty argument: default level of significance equal to 0.05.
     * <br><br>
     * @param dataObject the input responses and values of the covariates
     *                   (excluding the one corresponding to intercept).
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public LinearRegression(Hashtable argument,
                            Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null)
        {
            if (dataObject.length == 2 &&
                dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
                dataObject[1].getClass().getName().equalsIgnoreCase("[[D"))
            {
                doubleCovariate = (double[][]) dataObject[1];
            }
            else if (dataObject.length >= 2 &&
                     dataObject[0].getClass().getName().
                     equalsIgnoreCase("[D") &&
                     (dataObject.getClass().getName().equalsIgnoreCase(
                             "[Ljava.lang.Object;") ||
                     dataObject.getClass().getName().equalsIgnoreCase("[[D")))
            {
                doubleCovariate = DataManager.castDoubleObject(1, dataObject);
            }
            else
            {
                throw new IllegalArgumentException("Wrong input data.");
            }
            if (argument.size() > 0 &&
                argument.get(ALPHA) != null &&
                argument.get(HAS_INTERCEPT) != null)
            {
                statisticalAnalysis = new LinearRegression(
                        (Double) argument.get(ALPHA),
                        (Boolean) argument.get(HAS_INTERCEPT),
                        (double[]) dataObject[0], doubleCovariate);
            }
            if (argument.size() > 0 &&
                argument.get(ALPHA) != null)
            {
                statisticalAnalysis = new LinearRegression(
                        (Double) argument.get(ALPHA), (double[]) dataObject[0],
                        doubleCovariate);
            }
            else if (argument.size() == 0)
            {
                statisticalAnalysis = new LinearRegression(
                        (double[]) dataObject[0], doubleCovariate);
            }
            else
            {
                throw new IllegalArgumentException("Wrong input argument(s).");
            }
        }
        else
        {
            statisticalAnalysis = new LinearRegression();
        }
    }

    /**
     * Constructs a multiple linear regression model given the level of
     * significance, the index indicating if the column corresponding to the
     * intercept needs to be added, responses and covariate values.
     * @param alpha the level of significance.
     * @param hasIntercept the index indicating if the column corresponding to
     *                     the intercept needs to be added.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public LinearRegression(double alpha,
                            boolean hasIntercept,
                            double[] response,
                            double[] ...covariate)
    {
        this.alpha = alpha;
        this.response = response;
        this.covariate = covariate;
        this.hasIntercept = hasIntercept;
        testStatistic = testStatistic(response, covariate);
        confidenceInterval = confidenceInterval(alpha, response, covariate);
    }

    /**
     * Constructs a multiple linear regression model with a 0.05 level of
     * significance given the index indicating if the column corresponding to
     * the intercept needs to be added, responses and covariate values.
     * @param hasIntercept the index indicating if the column corresponding to
     *                     the intercept needs to be added.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public LinearRegression(boolean hasIntercept,
                            double[] response,
                            double[] ...covariate)
    {
        this(0.05, hasIntercept, response, covariate);
    }

    /**
     * Constructs a multiple linear regression model given the level of
     * significance, responses and covariate values.
     * @param alpha the level of significance.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public LinearRegression(double alpha,
                            double[] response,
                            double[] ...covariate)
    {
        this(alpha, true, response, covariate);
    }

    /**
     * Constructs a multiple linear regression model with a 0.05 level of
     * significance given the responses and covariate values.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public LinearRegression(double[] response,
                            double[] ...covariate)
    {
        this(0.05, response, covariate);
    }

    /**
     * The least squares estimate.
     * @param argument the empty argument.
     * @param dataObject the input responses and values of the covariates
     *                   (excluding the one corresponding to intercept).
     * @return the estimated coefficients.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double[] coefficients(Hashtable argument,
                                 Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null &&
            dataObject.length == 2 &&
            dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
            dataObject[1].getClass().getName().equalsIgnoreCase("[[D"))
        {
            coefficients = coefficients((double[]) dataObject[0],
                                        (double[][]) dataObject[1]);
        }
        else if (dataObject != null &&
                 dataObject.length >= 2 &&
                 dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
                 (dataObject.getClass().getName().equalsIgnoreCase(
                     "[Ljava.lang.Object;") ||
                 dataObject.getClass().getName().equalsIgnoreCase("[[D")))
        {
            coefficients = coefficients((double[]) dataObject[0],
                                        DataManager.
                                        castDoubleObject(1,dataObject));
        }
        else
        {
            throw new IllegalArgumentException("Wrong input arguments or " +
                                               "data.");
        }

        return coefficients;
    }

    /**
     * The least squares estimate.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the estimated coefficients.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double[] coefficients(double[] response,
                                 double[] ...covariate)
    {
        testStatistic = testStatistic(response, covariate);

        return coefficients;
    }

    /**
     * The t statistics for individual coefficients.
     * @param argument the empty argument.
     * @param dataObject the input responses and values of the covariates
     *                   (excluding the one corresponding to intercept).
     * @return the values of the t statistics.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double[] testStatistic(Hashtable argument,
                                  Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null &&
            dataObject.length == 2 &&
            dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
            dataObject[1].getClass().getName().equalsIgnoreCase("[[D"))
        {
            testStatistic = testStatistic((double[]) dataObject[0],
                                          (double[][]) dataObject[1]);
        }
        else if (dataObject != null &&
                 dataObject.length >= 2 &&
                 dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
                 (dataObject.getClass().getName().equalsIgnoreCase(
                     "[Ljava.lang.Object;") ||
                 dataObject.getClass().getName().equalsIgnoreCase("[[D")))
        {
            testStatistic = testStatistic((double[]) dataObject[0],
                                          DataManager.
                                          castDoubleObject(1,dataObject));
        }
        else
        {
            throw new IllegalArgumentException("Wrong input arguments or " +
                                               "data.");
        }

        return testStatistic;
    }

    /**
     * The t statistics for individual coefficients.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the values of the t statistics.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double[] testStatistic(double[] response,
                                  double[] ...covariate)
    {
        this.response = response;
        this.covariate = covariate;
        new DataManager().checkDimension(covariate);
        degreeFreedom = new double[3];
        basicStatistics = new BasicStatistics();
        responseMatrix = new Matrix(response, response.length);
        if (hasIntercept)
        {
            covariateWithIntercept = new double[covariate.length + 1]
                                     [response.length];
            for (int i = 0; i < response.length; i++)
            {
                covariateWithIntercept[0][i] = 1.0;
            }
            for (int j = 1; j < (covariate.length + 1); j++)
            {
                covariateWithIntercept[j] = covariate[j - 1];
            }
            covariateMatrix = new Matrix(covariateWithIntercept);
        }
        else
        {
            covariateMatrix = new Matrix(covariate);
        }
        coefficientMatrix = covariateMatrix.times(covariateMatrix.transpose()).
                            inverse().
                            times(covariateMatrix.times(responseMatrix));
        fittedMatrix = covariateMatrix.transpose().times(coefficientMatrix);
        residualMatrix = responseMatrix.minus(fittedMatrix);
        SSRMatrix = coefficientMatrix.transpose().times(covariateMatrix.times(
                responseMatrix));
        SSTMatrix = responseMatrix.transpose().times(responseMatrix);
        SSEMatrix = SSTMatrix.minus(SSRMatrix);
        hatMatrix = (covariateMatrix.transpose().times((covariateMatrix.times(
                covariateMatrix.transpose()).
                inverse())).times(covariateMatrix)).getArray();
        coefficients = coefficientMatrix.getColumnPackedCopy();
        fittedValues = fittedMatrix.getColumnPackedCopy();
        residuals = residualMatrix.getColumnPackedCopy();
        SSE = SSEMatrix.get(0, 0);
        SST = SSTMatrix.get(0, 0) -
              response.length * Math.pow(basicStatistics.mean(response), 2.0);
        SSR = SSRMatrix.get(0, 0) -
              response.length * Math.pow(basicStatistics.mean(response), 2.0);
        degreeFreedom[0] = (double) covariate.length;
        degreeFreedom[1] = (double) (response.length - covariate.length - 1);
        degreeFreedom[2] = response.length - 1.0;
        MSE = SSE / degreeFreedom[1];
        varianceMatrix = covariateMatrix.times(covariateMatrix.transpose()).
                         inverse().times(MSE);
        variance = varianceMatrix.getArray();
        rSquare = SSR / SST;
        addedColumnLength = new Boolean(hasIntercept).compareTo(false);
        testStatistic = new double[covariate.length + addedColumnLength];
        pValue = new double[covariate.length + addedColumnLength];
        for (int j = 0; j < covariate.length + addedColumnLength; j++)
        {
            testStatistic[j] = coefficients[j] / Math.sqrt(variance[j][j]);
            pValue[j] = 2 *
                        (1 - new TDistribution(response.length -
                                               covariate.length - 1).
                         cumulative(Math.abs(testStatistic[j])));
        }
        testFStatistic = SSR / (degreeFreedom[0] * MSE);
        fPValue = 1 - new FDistribution(degreeFreedom[0], degreeFreedom[1]).
                  cumulative(testFStatistic);
        output.put(COEFFICIENTS, coefficients);
        output.put(COEFFICIENT_VARIANCE, variance);
        output.put(DEGREE_OF_FREEDOM, degreeFreedom);
        output.put(HAT_MATRIX, hatMatrix);
        output.put(Output.SSE, SSE);
        output.put(Output.RSS, SSE);
        output.put(Output.SSR, SSR);
        output.put(Output.SST, SST);
        output.put(Output.MSE, MSE);
        output.put(FITTED_VALUES, fittedValues);
        output.put(RESIDUALS, residuals);
        output.put(R_SQUARE, rSquare);
        output.put(PVALUE, pValue);
        output.put(F_PVALUE, fPValue);
        output.put(TEST_STATISTIC, testStatistic);
        output.put(F_STATISTIC, testFStatistic);

        return testStatistic;
    }

    /**
     * The confidence intervals for the estimated coefficients.
     * @param argument the argument with the following choices,
     * <br> ALPHA: the level of significance;
     * <br> empty argument: default level of significance equal to 0.05;
     * <br><br>
     * @param dataObject the input responses and values of the covariates
     *                   (excluding the one corresponding to intercept).
     * @return the confidence intervals for the estimated coefficients,
     * <br>    confidenceInterval[j][0]: the lower bound for the (j+1)'th
     *                                   estimated coefficient;
     * <br>    confidenceInterval[j][1]: the upper bound for the (j+1)'th
     *                                   estimated coefficient.
     * @exception IllegalArgumentException wrong input arguments or data or
     *                                     data type.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double[][] confidenceInterval(Hashtable argument,
                                         Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null)
        {
            if (dataObject.length == 2 &&
                dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
                dataObject[1].getClass().getName().equalsIgnoreCase("[[D"))
            {
                doubleCovariate = (double[][]) dataObject[1];
            }
            else if (dataObject.length >= 2 &&
                     dataObject[0].getClass().getName().
                     equalsIgnoreCase("[D") &&
                     (dataObject.getClass().getName().equalsIgnoreCase(
                             "[Ljava.lang.Object;") ||
                     dataObject.getClass().getName().equalsIgnoreCase("[[D")))
            {
                doubleCovariate = DataManager.castDoubleObject(1, dataObject);
            }
            else
            {
                throw new IllegalArgumentException("Wrong input data type");
            }
            if (argument.size() > 0 && argument.get(ALPHA) != null)
            {
                confidenceInterval = confidenceInterval((Double) argument.get(
                        ALPHA), (double[]) dataObject[0], doubleCovariate);
            }
            else if (argument.size() == 0)
            {
                confidenceInterval =
                        confidenceInterval((double[]) dataObject[0],
                                           doubleCovariate);
            }
            else
            {
                throw new IllegalArgumentException("Wrong input argument(s).");
            }
        }
        else
        {
            throw new IllegalArgumentException("Wrong input arguments or " +
                                               "data.");
        }

        return confidenceInterval;
    }

    /**
     * The confidence intervals for the estimated coefficients.
     * @param alpha the level of significance.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the confidence intervals for the estimated coefficients,
     * <br>    confidenceInterval[j][0]: the lower bound for the (j+1)'th
     *                                   estimated coefficient;
     * <br>    confidenceInterval[j][1]: the upper bound for the (j+1)'th
     *                                   estimated coefficient.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double[][] confidenceInterval(double alpha,
                                         double[] response,
                                         double[] ...covariate)
    {
        if ((alpha <= 0.0) || (alpha > 1))
        {
            throw new IllegalArgumentException(
                    "The level of significance should be (strictly) positive " +
                    "and not greater than 1.");
        }
        this.alpha = alpha;
        testStatistic = testStatistic(response, covariate);
        zAlpha = new NormalDistribution().inverse((1 - alpha / 2));
        confidenceInterval = new double[testStatistic.length][2];
        for (int i = 0; i < testStatistic.length; i++)
        {
            confidenceInterval[i][0] = coefficients[i] -
                                       zAlpha * Math.sqrt(variance[i][i]);
            confidenceInterval[i][1] = coefficients[i] +
                                       zAlpha * Math.sqrt(variance[i][i]);
        }
        output.put(CONFIDENCE_INTERVAL, confidenceInterval);

        return confidenceInterval;
    }

    /**
     * The 95% confidence intervals for the estimated coefficients.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     *                  covariate[j]: the (j+1)'th covariate vector.
     * @return the confidence intervals for the estimated coefficients,
     * <br>    confidenceInterval[j][0]: the lower bound for the (j+1)'th
     *                                   estimated coefficient;
     * <br>    confidenceInterval[j][1]: the upper bound for the (j+1)'th
     *                                   estimated coefficient.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double[][] confidenceInterval(double[] response,
                                         double[] ...covariate)
    {
        return confidenceInterval(0.05, response, covariate);
    }

    /**
     * The F statistic for testing if the coefficients (excluding intercept) are
     * significant.
     * @param argument the empty argument.
     * @param dataObject the input responses and values of the covariates
     *                   (excluding the one corresponding to intercept).
     * @return the value of the F statistic.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public Double testFStatistic(Hashtable argument,
                                 Object ...dataObject)
    {
        if (dataObject != null &&
            dataObject.length == 2 &&
            dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
            dataObject[1].getClass().getName().equalsIgnoreCase("[[D"))
        {
            testFStatistic = testFStatistic((double[]) dataObject[0],
                                            (double[][]) dataObject[1]);
        }
        else if (dataObject != null &&
                 dataObject.length >= 2 &&
                 dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
                 (dataObject.getClass().getName().equalsIgnoreCase(
                     "[Ljava.lang.Object;") ||
                 dataObject.getClass().getName().equalsIgnoreCase("[[D")))
        {
            testFStatistic = testFStatistic((double[]) dataObject[0],
                                            DataManager.
                                            castDoubleObject(1,dataObject));
        }
        else
        {
            throw new IllegalArgumentException("Wrong input arguments or " +
                                               "data.");
        }

        return testFStatistic;
    }

    /**
     * The F statistic for testing if the coefficients (excluding intercept) are
     * significant.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the value of the F statistic.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double testFStatistic(double[] response,
                                 double[] ...covariate)
    {
        testStatistic = testStatistic(response, covariate);

        return testFStatistic;
    }

    /**
     * The p values for the t tests.
     * @param argument the empty argument.
     * @param dataObject the input responses and values of the covariates
     *                   (excluding the one corresponding to intercept).
     * @return the p values.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double[] pValue(Hashtable argument,
                           Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null &&
            dataObject.length == 2 &&
            dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
            dataObject[1].getClass().getName().equalsIgnoreCase("[[D"))
        {
            pValue = pValue((double[]) dataObject[0],
                            (double[][]) dataObject[1]);
        }
        else if (dataObject != null && dataObject.length >= 2 &&
                 dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
                 (dataObject.getClass().getName().equalsIgnoreCase(
                     "[Ljava.lang.Object;") ||
                 dataObject.getClass().getName().equalsIgnoreCase("[[D")))
        {
            pValue = pValue((double[]) dataObject[0],
                            DataManager.castDoubleObject(1, dataObject));
        }
        else
        {
            throw new IllegalArgumentException("Wrong input arguments or " +
                                               "data.");
        }

        return pValue;
    }

    /**
     * The p values for the t tests.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the p values.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double[] pValue(double[] response,
                           double[] ...covariate)
    {
        testStatistic = testStatistic(response, covariate);

        return pValue;
    }

    /**
     * The p value for the F test.
     * @param argument the empty argument.
     * @param dataObject the input responses and values of the covariates
     *                   (excluding the one corresponding to intercept).
     * @return the p value.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public Double fPValue(Hashtable argument,
                          Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null &&
            dataObject.length == 2 &&
            dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
            dataObject[1].getClass().getName().equalsIgnoreCase("[[D"))
        {
            fPValue = fPValue((double[]) dataObject[0],
                              (double[][]) dataObject[1]);
        }
        else if (dataObject != null && dataObject.length >= 2 &&
                 dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
                 (dataObject.getClass().getName().equalsIgnoreCase(
                     "[Ljava.lang.Object;") ||
                 dataObject.getClass().getName().equalsIgnoreCase("[[D")))
        {
            fPValue = fPValue((double[]) dataObject[0],
                              DataManager.castDoubleObject(1, dataObject));
        }
        else
        {
            throw new IllegalArgumentException("Wrong input arguments or " +
                                               "data.");
        }

        return fPValue;
    }

    /**
     * The p value for the F test.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the p value.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double fPValue(double[] response,
                          double[] ...covariate)
    {
        testStatistic = testStatistic(response, covariate);

        return fPValue;
    }

}
