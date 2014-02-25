package javastat.survival.regression;

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
 * <p>Calculates the parameter estimates, z statistics and p-values for the
 * proportional hazards models.</p>
 * <p> </p>
 * <br> Example:
 * <br> double [] time = {156, 1040, 59, 421, 329, 769, 365, 770, 1227, 268,
 *                        475,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                        1129, 464, 1206, 638, 563, 1106, 431, 855, 803, 115,
 *                        744,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                        477, 448, 353, 377};
 * <br> double [] censor = {1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 1, 0,
 *                          1, 0, 0, 1, 0, 0, 0, 1, 0};
 * <br> double [][] covariate={{1, 1, 1, 2, 1, 2, 2, 2, 2, 1, 2, 2, 2, 2, 1, 2,
 *                              1, 1, 1, 1, 1, 2, 1, 1, 2, 2},
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;
 *                             {66, 38, 72, 53, 43, 59, 64, 57, 59, 74, 59, 53,
 *                              56, 44, 56, 55, 44, 50, 43,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                              39, 74, 50, 64, 56, 63, 58}};
 * <br>
 * <br> // Non-null constructor
 * <br> CoxRegression testclass1 =
 *        new CoxRegression(0.05, time, censor, covariate[0], covariate[1]);
 * <br> double [] coefficients = testclass1.coefficients;
 * <br> double [][] variance = {testclass1.variance[0], testclass1.variance[1]};
 * <br> double [] testStatistic = testclass1.testStatistic;
 * <br> double [] pValue = testclass1.pValue;
 * <br> double [][] confidenceInterval = {testclass1.confidenceInterval[0],
 *        testclass1.confidenceInterval[1]};
 * <br>
 * <br> // Null constructor
 * <br> CoxRegression testclass2 = new CoxRegression();
 * <br> double [] coefficients =
 *        testclass2.coefficients(time, censor, covariate);
 * <br> testStatistic = testclass2.testStatistic(time, censor, covariate);
 * <br> pValue = testclass2.pValue(time, censor, covariate);
 * <br> confidenceInterval =
 *        testclass2.confidenceInterval(0.1, time, censor, covariate);
 * <br>
 * <br> // Non-null constructor
 * <br> Hashtable argument1 = new Hashtable();
 * <br> argument1.put(ALPHA, 0.05);
 * <br> StatisticalAnalysis testclass3 = new CoxRegression(argument1,
 * <br> &nbsp;&nbsp;&nbsp;
 *        time, censor, covariate[0], covariate[1]).statisticalAnalysis;
 * <br> coefficients = (double[]) testclass3.output.get(COEFFICIENTS);
 * <br> variance = (double[][]) testclass3.output.get(COEFFICIENT_VARIANCE);
 * <br> testStatistic = (double[]) testclass3.output.get(TEST_STATISTIC);
 * <br> pValue = (double[]) testclass3.output.get(PVALUE);
 * <br> confidenceInterval =
 *        (double[][]) testclass3.output.get(CONFIDENCE_INTERVAL);
 * <br>
 * <br> // Null constructor
 * <br> Hashtable argument2 = new Hashtable();
 * <br> CoxRegression testclass4 = new CoxRegression(argument2, null);
 * <br> coefficients =
 *        testclass4.coefficients(argument2, time, censor, covariate);
 * <br> testStatistic =
 *        testclass4.testStatistic(argument2, time, censor, covariate);
 * <br> pValue = testclass4.pValue(argument2, time, censor, covariate);
 * <br> argument2.put(ALPHA, 0.1);
 * <br> confidenceInterval =
 *        testclass4.confidenceInterval(argument2, time, censor, covariate);
 * <br>
 * <br> // Obtains the information about the output
 * <br> out.println(testclass3.output.toString());
 * <br> out.println(testclass4.output.toString());
 */

public class CoxRegression extends Regression
{
    /**
     * The level of significance.
     */

    public double alpha;

    /**
     * The parameter estimates,
     * <br>coefficients[j]: the parameter estiamte corresponding to the (j+1)'th
     *                      covariate.
     */

    public double[] coefficients;

    /**
     * The variance-covariance matrix of the estimated coefficients,
     * <br> variance[i][j]: the covariance of the (i+1)'th and the (j+1)'th
     *                      estiamted coefficients.
     */

    public double[][] variance;

    /**
     * The z statistics,
     * <br> testStatistic[j]=coefficients[j]/Math.sqrt(variance[j][j]):
     *      the z statistic corresponding to the (j+1)'th covariate.
     */

    public double[] testStatistic;

    /**
     * The p values for the z tests,
     * <br> pValue[j]: the p value corresponding to the (j+1)'th parameter.
     */

    public double[] pValue;

    /**
     * The confidence intervals for the estimated coefficients,
     * <br> confidenceInterval[j][0]: the lower bound for the (j+1)'th estimated
     *                                coefficient;
     * <br> confidenceInterval[j][1]: the upper bound for the (j+1)'th estimated
     *                                coefficient.
     */

    public double[][] confidenceInterval;

    /**
     * The survival times of the patients.
     */

    public double[] time;

    /**
     * The censor indicators for the patients,
     * <br> censor[i]=1: death;
     * <br> censor[i]=0: censored.
     */

    public double[] censor;

    /**
     * The values of the covariates,
     * <br> covariate[j]: the (j+1)'th covariate vector.
     */

    public double[][] covariate;

    /**
     * The values of the covariates.
     */

    private double[][] doubleCovariate;

    /**
     * The object represents a fitted proportional hazards model.
     */

    public StatisticalAnalysis statisticalAnalysis;

    /**
     * The constant such that the upper-tailed probability of the normal
     * random variable is equal to alpha/2.
     */

    private double zAlpha;

    /**
     * The numerator of the expected covariate values.
     */

    private double numerator;

    /**
     * The denominator of the expected covariate values.
     */

    private double denominator;

    /**
     * The convergence threshold for the difference between the original
     * estimate and updated estimate.
     */

    private double error1;

    /**
     * The convergence threshold for the score function.
     */

    private double error2;

    /**
     * The risk factor.
     */

    private double[] riskFactor;

    /**
     * The expected covariate values.
     */

    private double[][] expectedCovariate;

    /**
     * The score function.
     */

    private double[] score;

    /**
     * The information matrix.
     */

    private double[][] information;

    /**
     * The score function in matrix form.
     */

    private Matrix scoreMatrix;

    /**
     * The information matrix.
     */

    private Matrix informationMatrix;

    /**
     * The estimated coefficients in matrix form.
     */

    private Matrix coefficientMatrix;

    /**
     * The convergence threshold in matrix form.
     */

    private Matrix errorMatrix;

    /**
     * The class contains the collections of some basic methods for manipulating
     * the data.
     */

    private DataManager dataManager;

    /**
     * The class for computing the cumulative normal distribution.
     */

    private NormalDistribution normalDistribution;

    /**
     * Constructs a proportional hazards model for survival data.
     */

    public CoxRegression() {}

    /**
     * Constructs a proportional hazards model given the input argument and
     * data.
     * @param argument the argument with the following choices,
     * <br> ALPHA: the level of significance;
     * <br> empty argument: default level of significance equal to 0.05;
     * <br><br>
     * @param dataObject the survival times of the patients and censor
     *                   indicators for the patients.
     * @exception IllegalArgumentException wrong input argument(s) or data type.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the time vector, censor vector, and
     *                                     rows of the covariate matrix must
     *                                     have the same length.
     * @exception IllegalArgumentException all elements of the time vector must
     *                                     be postive.
     * @exception IllegalArgumentException all elements of the censor vector
     *                                     must be either 1 or 0.
     * @exception IllegalArgumentException all rows of the covariate matrix must
     *                                     have the same length.
     */

    public CoxRegression(Hashtable argument,
                         Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null)
        {
            if (dataObject.length == 3 &&
                dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
                dataObject[1].getClass().getName().equalsIgnoreCase("[D") &&
                dataObject[2].getClass().getName().equalsIgnoreCase("[[D"))
            {
                doubleCovariate = (double[][]) dataObject[2];
            }
            else if (dataObject.length >= 3 &&
                     dataObject[0].getClass().getName().
                     equalsIgnoreCase("[D") &&
                     dataObject[1].getClass().getName().
                     equalsIgnoreCase("[D") &&
                     (dataObject.getClass().getName().
                      equalsIgnoreCase("[Ljava.lang.Object;") ||
                     dataObject.getClass().getName().equalsIgnoreCase("[[D")))
            {
                doubleCovariate = DataManager.castDoubleObject(2, dataObject);
            }
            else
            {
                throw new IllegalArgumentException("Wrong input data type");
            }
            if (argument.size() > 0 &&
                argument.get(ALPHA) != null)
            {
                statisticalAnalysis = new CoxRegression(
                        (Double) argument.get(ALPHA),
                        (double[]) dataObject[0], (double[]) dataObject[1],
                        doubleCovariate);
            }
            else if (argument.size() == 0)
            {
                statisticalAnalysis = new CoxRegression(
                        (double[]) dataObject[0], (double[]) dataObject[1],
                        doubleCovariate);
            }
            else
            {
                throw new IllegalArgumentException("Wrong input argument(s).");
            }
        }
        else
        {
            statisticalAnalysis = new CoxRegression();
        }
    }

    /**
     * Constructs a proportional hazards model given the level of significance,
     * survival times, censor indicators and covariate values.
     * @param alpha the level of significance.
     * @param time the survival times of the patients.
     * @param censor the censor indicators for the patients,
     * <br>          censor[i]=1: death;
     * <br>          censor[i]=0: censored.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the time vector, censor vector, and
     *                                     rows of the covariate matrix must
     *                                     have the same length.
     * @exception IllegalArgumentException all elements of the time vector must
     *                                     be postive.
     * @exception IllegalArgumentException all elements of the censor vector
     *                                     must be either 1 or 0.
     * @exception IllegalArgumentException all rows of the covariate matrix must
     *                                     have the same length.
     */

    public CoxRegression(double alpha,
                         double[] time,
                         double[] censor,
                         double[] ...covariate)
    {
        this.alpha = alpha;
        this.time = time;
        this.censor = censor;
        this.covariate = covariate;
        testStatistic = testStatistic(time, censor, covariate);
        confidenceInterval = confidenceInterval(alpha, time, censor, covariate);
    }

    /**
     * Constructs a proportional hazards model with a 0.05 level of significance
     * given the survival times, censor indicators and covariate values.
     * @param time the survival times of the patients.
     * @param censor the censor indicators for the patients,
     * <br>          censor[i]=1: death;
     * <br>          censor[i]=0: censored.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the time vector, censor vector, and
     *                                     rows of the covariate matrix must
     *                                     have the same length.
     * @exception IllegalArgumentException all elements of the time vector must
     *                                     be postive.
     * @exception IllegalArgumentException all elements of the censor vector
     *                                     must be either 1 or 0.
     * @exception IllegalArgumentException all rows of the covariate matrix must
     *                                     have the same length.
     */

    public CoxRegression(double[] time,
                         double[] censor,
                         double[] ...covariate)
    {
        this(0.05, time, censor, covariate);
    }

    /**
     * The estimated coefficients.
     * @param argument the empty argument.
     * @param dataObject the survival times of the patients and censor
     *                   indicators for the patients.
     * @return the estimated values of the coefficients.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the time vector, censor vector, and
     *                                     rows of the covariate matrix must
     *                                     have the same length.
     * @exception IllegalArgumentException all elements of the time vector must
     *                                     be postive.
     * @exception IllegalArgumentException all elements of the censor vector
     *                                     must be either 1 or 0.
     * @exception IllegalArgumentException all rows of the covariate matrix must
     *                                     have the same length.
     */

    public double[] coefficients(Hashtable argument,
                                 Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null && dataObject.length == 3 &&
            dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
            dataObject[1].getClass().getName().equalsIgnoreCase("[D") &&
            dataObject[2].getClass().getName().equalsIgnoreCase("[[D"))
        {
            coefficients = coefficients((double[]) dataObject[0],
                                        (double[]) dataObject[1],
                                        (double[][]) dataObject[2]);
        }
        else if (dataObject != null && dataObject.length >= 3 &&
                 dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
                 dataObject[1].getClass().getName().equalsIgnoreCase("[D") &&
                 (dataObject.getClass().getName().
                  equalsIgnoreCase("[Ljava.lang.Object;") ||
                 dataObject.getClass().getName().equalsIgnoreCase("[[D")))
        {
            coefficients = coefficients((double[]) dataObject[0],
                                        (double[]) dataObject[1],
                                        DataManager.
                                        castDoubleObject(2, dataObject));
        }
        else
        {
            throw new IllegalArgumentException(
                    "Wrong input arguments or data.");
        }

        return coefficients;
    }

    /**
     * The estimated coefficients.
     * @param time the survival times of the patients.
     * @param censor the censor indicators for the patients,
     * <br>          censor[i]=1: death;
     * <br>          censor[i]=0: censored.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the estimated values of the coefficients.
     * @exception IllegalArgumentException the time vector, censor vector, and
     *                                     rows of the covariate matrix must
     *                                     have the same length.
     * @exception IllegalArgumentException all elements of the time vector must
     *                                     be postive.
     * @exception IllegalArgumentException all elements of the censor vector
     *                                     must be either 1 or 0.
     * @exception IllegalArgumentException all rows of the covariate matrix must
     *                                     have the same length.
     */

    public double[] coefficients(double[] time,
                                 double[] censor,
                                 double[] ...covariate)
    {
        this.time = time;
        this.censor = censor;
        this.covariate = covariate;
        testStatistic = testStatistic(time, censor, covariate);

        return coefficients;
    }

    /**
     * The z statistics.
     * @param argument the empty argument.
     * @param dataObject the survival times of the patients and censor
     *                   indicators for the patients.
     * @return the values of the z statistics.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the time vector, censor vector, and
     *                                     rows of the covariate matrix must
     *                                     have the same length.
     * @exception IllegalArgumentException all elements of the time vector must
     *                                     be postive.
     * @exception IllegalArgumentException all elements of the censor vector
     *                                     must be either 1 or 0.
     * @exception IllegalArgumentException all rows of the covariate matrix must
     *                                     have the same length.
     */

    public double[] testStatistic(Hashtable argument,
                                  Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null &&
            dataObject.length == 3 &&
            dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
            dataObject[1].getClass().getName().equalsIgnoreCase("[D") &&
            dataObject[2].getClass().getName().equalsIgnoreCase("[[D"))
        {
            testStatistic = testStatistic((double[]) dataObject[0],
                                          (double[]) dataObject[1],
                                          (double[][]) dataObject[2]);
        }
        else if (dataObject != null && dataObject.length >= 3 &&
                 dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
                 dataObject[1].getClass().getName().equalsIgnoreCase("[D") &&
                 (dataObject.getClass().getName().
                  equalsIgnoreCase("[Ljava.lang.Object;") ||
                  dataObject.getClass().getName().equalsIgnoreCase("[[D")))
        {
            testStatistic = testStatistic((double[]) dataObject[0],
                                          (double[]) dataObject[1],
                                          DataManager.
                                          castDoubleObject(2, dataObject));
        }
        else
        {
            throw new IllegalArgumentException(
                    "Wrong input arguments or data.");
        }

        return testStatistic;
    }

    /**
     * The z statistics.
     * @param time the survival times of the patients.
     * @param censor the censor indicators for the patients,
     * <br>          censor[i]=1: death;
     * <br>          censor[i]=0: censored.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the values of the z statistics.
     * @exception IllegalArgumentException the time vector, censor vector, and
     *                                     rows of the covariate matrix must
     *                                     have the same length.
     * @exception IllegalArgumentException all elements of the time vector must
     *                                     be postive.
     * @exception IllegalArgumentException all elements of the censor vector
     *                                     must be either 1 or 0.
     * @exception IllegalArgumentException all rows of the covariate matrix must
     *                                     have the same length.
     */

    public double[] testStatistic(double[] time,
                                  double[] censor,
                                  double[] ...covariate)
    {
        this.time = time;
        this.censor = censor;
        this.covariate = covariate;
        BasicStatistics.convergenceCriterion = new double[]{0.00001};
        denominator = 0.0;
        numerator = 0.0;
        error1 = 1.0;
        error2 = 1.0;
        pValue = new double[covariate.length];
        testStatistic = new double[covariate.length];
        riskFactor = new double[time.length];
        expectedCovariate = new double[covariate.length][time.length];
        score = new double[covariate.length];
        coefficients = new double[covariate.length];
        information = new double[covariate.length][covariate.length];
        normalDistribution = new NormalDistribution();
        dataManager = new DataManager();
        dataManager.checkDimension(covariate);
        dataManager.checkPositiveRange(time, "time");
        dataManager.checkCensor(censor);
        if (time.length != covariate[0].length || time.length != censor.length)
        {
            throw new IllegalArgumentException(
                    "The time vector, censor vector, and rows of " +
                    "the covariate matrix must have the same length.");
        }
        coefficientMatrix = new Matrix(covariate.length, 1, 0.1);
        coefficients = coefficientMatrix.getColumnPackedCopy();
        while ((error1 > BasicStatistics.convergenceCriterion[0]) ||
               (error2 > BasicStatistics.convergenceCriterion[0]))
        {
            for (int i = 0; i < time.length; ++i)
            {
                riskFactor[i] = 1.0;
                for (int s = 0; s < covariate.length; ++s)
                {
                    riskFactor[i] *= Math.exp(coefficients[s] *
                                              covariate[s][i]);
                }
            }
            for (int j = 0; j < covariate.length; j++)
            {
                score[j] = 0.0;
                for (int i = 0; i < time.length; i++)
                {
                    for (int k = 0; k < time.length; k++)
                    {
                        if (time[k] >= time[i])
                        {
                            denominator += riskFactor[k];
                            numerator += covariate[j][k] * riskFactor[k];
                        }
                    }
                    expectedCovariate[j][i] = numerator / denominator;
                    score[j] += censor[i] *
                            (covariate[j][i] - expectedCovariate[j][i]);
                    numerator = 0.0;
                    denominator = 0.0;
                }
            }
            for (int r = 0; r < covariate.length; r++)
            {
                for (int s = 0; s < covariate.length; s++)
                {
                    information[r][s] = 0.0;
                    for (int i = 0; i < time.length; i++)
                    {
                        for (int k = 0; k < time.length; k++)
                        {
                            if (time[k] >= time[i])
                            {
                                denominator += riskFactor[k];
                                numerator +=
                                        (covariate[r][k] * covariate[s][k] *
                                         riskFactor[k]);
                            }
                        }
                        information[r][s] += censor[i] *
                                (expectedCovariate[r][i] *
                                 expectedCovariate[s][i] -
                                 (numerator / denominator));
                        numerator = 0.0;
                        denominator = 0.0;
                    }
                }
            }
            informationMatrix = new Matrix(information);
            scoreMatrix = new Matrix(score, covariate.length);
            errorMatrix = informationMatrix.inverse().times(scoreMatrix);
            coefficientMatrix.minusEquals(errorMatrix);
            coefficients = coefficientMatrix.getColumnPackedCopy();
            error1 = Math.sqrt(errorMatrix.normF());
            error2 = Math.sqrt(scoreMatrix.normF());
        }
        informationMatrix = new Matrix(information);
        variance = informationMatrix.inverse().times( -1.0).getArray();
        for (int s = 0; s < covariate.length; ++s)
        {
            testStatistic[s] = coefficients[s] / Math.sqrt(variance[s][s]);
        }
        for (int s = 0; s < testStatistic.length; s++)
        {
            pValue[s] = 2 * (1 - normalDistribution.
                             cumulative(Math.abs(testStatistic[s])));
        }
        output.put(COEFFICIENTS, coefficients);
        output.put(COEFFICIENT_VARIANCE, variance);
        output.put(TEST_STATISTIC, testStatistic);
        output.put(PVALUE, pValue);

        return testStatistic;
    }

    /**
     * The confidence intervals for the estimated coefficients.
     * @param argument the argument with the following choices,
     * <br> ALPHA: the level of significance;
     * <br> empty argument: default level of significance equal to 0.05;
     * <br><br>
     * @param dataObject the survival times of the patients and censor
     *                   indicators for the patients.
     * @return the confidence interval for the estimated coefficients,
     * <br>    confidenceInterval[j][0]: the lower bound for the (j+1)'th
     *                                   estimated coefficient;
     * <br>    confidenceInterval[j][1]: the upper bound for the (j+1)'th
     *                                   estimated coefficient.
     * @exception IllegalArgumentException wrong input arguments or data or
     *                                     data type.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the time vector, censor vector, and
     *                                     rows of the covariate matrix must
     *                                     have the same length.
     * @exception IllegalArgumentException all elements of the time vector must
     *                                     be postive.
     * @exception IllegalArgumentException all elements of the censor vector
     *                                     must be either 1 or 0.
     * @exception IllegalArgumentException all rows of the covariate matrix must
     *                                     have the same length.
     */

    public double[][] confidenceInterval(Hashtable argument,
                                         Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null)
        {
            if (dataObject.length == 3 &&
                dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
                dataObject[1].getClass().getName().equalsIgnoreCase("[D") &&
                dataObject[2].getClass().getName().equalsIgnoreCase("[[D"))
            {
                doubleCovariate = (double[][]) dataObject[2];
            }
            else if (dataObject.length >= 3 &&
                     dataObject[0].getClass().getName().
                     equalsIgnoreCase("[D") &&
                     dataObject[1].getClass().getName().
                     equalsIgnoreCase("[D") &&
                     (dataObject.getClass().getName().
                      equalsIgnoreCase("[Ljava.lang.Object;") ||
                     dataObject.getClass().getName().equalsIgnoreCase("[[D")))
            {
                doubleCovariate = DataManager.castDoubleObject(2, dataObject);
            }
            else
            {
                throw new IllegalArgumentException("Wrong input data type");
            }
            if (argument.size() > 0 &&
                argument.get(ALPHA) != null)
            {
                confidenceInterval = confidenceInterval(
                        (Double) argument.get(ALPHA),
                        (double[]) dataObject[0], (double[]) dataObject[1],
                        doubleCovariate);
            }
            else if (argument.size() == 0)
            {
                confidenceInterval = confidenceInterval(
                        (double[]) dataObject[0],
                        (double[]) dataObject[1], doubleCovariate);
            }
            else
            {
                throw new IllegalArgumentException("Wrong input argument(s).");
            }
        }
        else
        {
            throw new IllegalArgumentException("Wrong input data.");
        }

        return confidenceInterval;
    }

    /**
     * The confidence intervals for the estimated coefficients.
     * @param alpha the level of significance.
     * @param time the survival times of the patients.
     * @param censor the censor indicators for the patients,
     * <br>          censor[i]=1: death;
     * <br>          censor[i]=0: censored.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the confidence interval for the estimated coefficients,
     * <br>    confidenceInterval[j][0]: the lower bound for the (j+1)'th
     *                                   estimated coefficient;
     * <br>    confidenceInterval[j][1]: the upper bound for the (j+1)'th
     *                                   estimated coefficient.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the time vector, censor vector, and
     *                                     rows of the covariate matrix must
     *                                     have the same length.
     * @exception IllegalArgumentException all elements of the time vector must
     *                                     be postive.
     * @exception IllegalArgumentException all elements of the censor vector
     *                                     must be either 1 or 0.
     * @exception IllegalArgumentException all rows of the covariate matrix must
     *                                     have the same length.
     */

    public double[][] confidenceInterval(double alpha,
                                         double[] time,
                                         double[] censor,
                                         double[] ...covariate)
    {
        if ((alpha <= 0.0) || (alpha > 1))
        {
            throw new IllegalArgumentException(
                    "The level of significance should be (strictly) positive " +
                    "and not greater than 1.");
        }
        this.alpha = alpha;
        this.time = time;
        this.censor = censor;
        this.covariate = covariate;
        testStatistic = testStatistic(time, censor, covariate);
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
     * @param time the survival times of the patients.
     * @param censor the censor indicators for the patients,
     * <br>          censor[i]=1: death;
     * <br>          censor[i]=0: censored.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the confidence interval for the estimated coefficients,
     * <br>    confidenceInterval[j][0]: the lower bound for the (j+1)'th
     *                                   estimated coefficient;
     * <br>    confidenceInterval[j][1]: the upper bound for the (j+1)'th
     *                                   estimated coefficient.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the time vector, censor vector, and
     *                                     rows of the covariate matrix must
     *                                     have the same length.
     * @exception IllegalArgumentException all elements of the time vector must
     *                                     be postive.
     * @exception IllegalArgumentException all elements of the censor vector
     *                                     must be either 1 or 0.
     * @exception IllegalArgumentException all rows of the covariate matrix must
     *                                     have the same length.
     */

    public double[][] confidenceInterval(double[] time,
                                         double[] censor,
                                         double[][] covariate)
    {
        return confidenceInterval(0.05, time, censor, covariate);
    }

    /**
     * The p values for the z tests.
     * @param argument the empty argument.
     * @param dataObject the survival times of the patients and censor
     *                   indicators for the patients.
     * @return the p values.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the time vector, censor vector, and
     *                                     rows of the covariate matrix must
     *                                     have the same length.
     * @exception IllegalArgumentException all elements of the time vector must
     *                                     be postive.
     * @exception IllegalArgumentException all elements of the censor vector
     *                                     must be either 1 or 0.
     * @exception IllegalArgumentException all rows of the covariate matrix must
     *                                     have the same length.
     */

    public double[] pValue(Hashtable argument,
                           Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null &&
            dataObject.length == 3 &&
            dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
            dataObject[1].getClass().getName().equalsIgnoreCase("[D") &&
            dataObject[2].getClass().getName().equalsIgnoreCase("[[D"))
        {
            pValue = pValue((double[]) dataObject[0],
                            (double[]) dataObject[1],
                            (double[][]) dataObject[2]);
        }
        else if (dataObject != null &&
                 dataObject.length >= 3 &&
                 dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
                 dataObject[1].getClass().getName().equalsIgnoreCase("[D") &&
                 (dataObject.getClass().getName().
                  equalsIgnoreCase("[Ljava.lang.Object;") ||
                 dataObject.getClass().getName().equalsIgnoreCase("[[D")))
        {
            pValue = pValue((double[]) dataObject[0], (double[]) dataObject[1],
                            DataManager.castDoubleObject(2, dataObject));
        }
        else
        {
            throw new IllegalArgumentException(
                    "Wrong input arguments or data.");
        }

        return pValue;
    }

    /**
     * The p values for the z tests.
     * @param time the survival times of the patients.
     * @param censor the censor indicators for the patients,
     * <br>          censor[i]=1: death;
     * <br>          censor[i]=0: censored.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the p values.
     * @exception IllegalArgumentException the time vector, censor vector, and
     *                                     rows of the covariate matrix must
     *                                     have the same length.
     * @exception IllegalArgumentException all elements of the time vector must
     *                                     be postive.
     * @exception IllegalArgumentException all elements of the censor vector
     *                                     must be either 1 or 0.
     * @exception IllegalArgumentException all rows of the covariate matrix must
     *                                     have the same length.
     */

    public double[] pValue(double[] time,
                           double[] censor,
                           double[] ...covariate)
     {
        this.time = time;
        this.censor = censor;
        this.covariate = covariate;
        testStatistic = testStatistic(time, censor, covariate);

        return pValue;
    }

}
