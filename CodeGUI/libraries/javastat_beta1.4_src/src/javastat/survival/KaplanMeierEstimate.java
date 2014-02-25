package javastat.survival;

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
import javastat.survival.regression.*;
import static javastat.util.Argument.*;
import static javastat.util.Output.*;
import javastat.util.*;

import JSci.maths.statistics.*;

/**
 *
 * <p>Calculates the Kaplan-Meier estimates of survival function, the variances
 * of Kaplan-Meier estimates and the confidence intervals for values of the
 * survival function. </p>
 * <p> </p>
 * <br> Example:
 * <br> double [] time1 = {156, 1040, 59, 421, 329, 769, 365, 770, 1227, 268,
 *                         475,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                         1129, 464, 1206, 638, 563, 1106, 431, 855, 803, 115,
 *                         744,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                         477, 448, 353, 377};
 * <br> double [] censor1={1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1,
 *                         0, 0, 1, 0, 0, 0, 1, 0};
 * <br> double [][] covariate1={{1, 1, 1, 2, 1, 2, 2, 2, 2, 1, 2, 2, 2, 2, 1, 2,
 *                               1, 1, 1, 1, 1, 2, 1, 1, 2, 2},
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;
 *                              {66, 38, 72, 53, 43, 59, 64, 57, 59, 74, 59, 53,
 *                               56, 44, 56, 55, 44, 50, 43,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                               39, 74, 50, 64, 56, 63, 58}};
 * <br>
 * <br> // Non-null constructor without proportional hazards assumption
 * <br> KaplanMeierEstimate testclass1 =
 *        new KaplanMeierEstimate(0.05, time1, censor1);
 * <br> double [] estimate1 = testclass1.estimate;
 * <br> double [] variance = testclass1.variance;
 * <br> double [][] interval = new double[time1.length][2];
 * <br>
 * <br> // Null constructor
 * <br> KaplanMeierEstimate testclass2 = new KaplanMeierEstimate();
 * <br> double [][] confidenceInterval =
 *        testclass2.confidenceInterval(0.05, time1, censor1);
 * <br> estimate1 = testclass2.estimate(time1, censor1);
 * <br> variance = testclass2.variance(time1, censor1);
 * <br> // Survival fit under proportional hazards assumption
 * <br> double [] estimate2 = testclass2.estimate(time1, censor1, covariate1);
 * <br>
 * <br> // Non-null constructor under proportional hazards assumption
 * <br> KaplanMeierEstimate testclass3 =
 *        new KaplanMeierEstimate(time1, censor1, covariate1);
 * <br> double [] estimate3 = testclass3.estimate;
 * <br>
 * <br> // Non-null constructor without proportional hazards assumption
 * <br> Hashtable argument1 = new Hashtable();
 * <br> argument1.put(ALPHA, 0.05);
 * <br> StatisticalAnalysis testclass4 = new KaplanMeierEstimate(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument1, time1, censor1).statisticalAnalysis;
 * <br> estimate1 = (double[]) testclass4.output.get(SURVIVAL_ESTIMATE);
 * <br> variance = (double[]) testclass4.output.get(SURVIVAL_ESTIMATE_VARIANCE);
 * <br> confidenceInterval =
 *        (double[][]) testclass4.output.get(CONFIDENCE_INTERVAL);
 * <br>
 * <br> // Null constructor
 * <br> Hashtable argument2 = new Hashtable();
 * <br> KaplanMeierEstimate testclass5 =
 *        new KaplanMeierEstimate(argument2, null);
 * <br> argument2.put(ALPHA, 0.05);
 * <br> confidenceInterval =
 *        testclass5.confidenceInterval(argument2, time1, censor1);
 * <br> estimate1 = testclass5.estimate(argument2, time1, censor1);
 * <br> variance = testclass5.variance(argument2, time1, censor1);
 * <br> // Survival fit under proportional hazards assumption
 * <br> estimate2 = testclass5.estimate(argument2, time1, censor1, covariate1);
 * <br>
 * <br> // Non-null constructor under proportional hazards assumption
 * <br> Hashtable argument3 = new Hashtable();
 * <br> StatisticalAnalysis testclass6 = new KaplanMeierEstimate(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument3, time1, censor1, covariate1).statisticalAnalysis;
 * <br> estimate3 = (double[]) testclass6.output.get(SURVIVAL_ESTIMATE);
 * <br> double[] coefficients = (double[]) testclass6.output.get(COEFFICIENTS);
 * <br>
 * <br>  // Obtains the information about the output
 * <br> out.println(testclass4.output.toString());
 * <br> out.println(testclass5.output.toString());
 * <br> out.println(testclass6.output.toString());
 */

public class KaplanMeierEstimate extends StatisticalAnalysis implements
        SurvivalEstimateInterface
{

    /**
     * The level of significance.
     */

    public double alpha;

    /**
     * The Kaplan Meier estimates,
     * <br> estimate[i]: the estimate corresponding to the survival time of the
     *                  (i+1)'th patient.
     */

    public double[] estimate;

    /**
     * The variances of the Kaplan-Meier estimates,
     * <br> variance[i]: the variance of the Kaplan-Meier estimate corresponding
     *                   to the survival time of the (i+1)'th patient.
     */

    public double[] variance;

    /**
     * Confidence intervals for values of the survival function,
     * <br> confidenceInterval[i][0]: the lower bound corresponding to the
     *                                survival time of the (i+1)'th patient;
     * <br> confidenceInterval[i][1]: the upper bound corresponding to the
     *                                survival time of the (i+1)'th patient.
     */

    public double[][] confidenceInterval;

    /**
     * The parameter estimates for the proportional hazards model,
     * <br> coefficients[j]: the parameter estiamte corresponding to the
     *                       (j+1)'th covariate.
     */

    public double[] coefficients;

    /**
     * The CoxRegression class employed for calculating the Kaplan-Meier
     * estimates under proportional hazards assumption.
     */

    public CoxRegression coxRegression;

    /**
     * The survival times of the patients.
     */

    public double[] time;

    /**
     * The censor indicator for the patients,
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
     * The boolean variable indicating if the Kaplan-Meier estimate is under
     * proportional hazards assumption,
     * <br> isProportionalHazards=true: under proportional hazards assumption;
     * <br> isProportionalHazards=false: otherwise.
     */

    public boolean isProportionalHazards;

    /**
     * The object represents a Kaplan-Meier estimates.
     */

    public StatisticalAnalysis statisticalAnalysis;

    /**
     * The constant such that the upper-tailed probability of the normal
     * random variable is equal to alpha/2.
     */

    private double zAlpha;

    /**
     * The risk factor.
     */

    private double riskFactor;

    /**
     * The vector of risk factors.
     */

    private double[] riskFactorVector;

    /**
     * The number of patients at risk and the number of patients who die.
     */

    private double[][] survivalIndex;

    /**
     * Constructs a Kaplan Meier estimate for survival data.
     */

    public KaplanMeierEstimate() {}

    /**
     * Computes a Kaplan-Meier estimate given the input argument and data.
     * @param argument the argument with the following choices,
     * <br> ALPHA: the level of significance;
     * <br> empty argument: default level of significance equal to 0.05.
     * <br><br>
     * @param dataObject the survival times of the patients and censor
     *                   indicators for the patients.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     must have the same length.
     * @exception IllegalArgumentException all elements of the time vector must
     *                                     be postive.
     * @exception IllegalArgumentException all elements of the censor vector
     *                                     must be either 1 or 0.
     */

    public KaplanMeierEstimate(Hashtable argument,
                               Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null)
        {
            if (dataObject.length == 2 &&
                dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
                dataObject[1].getClass().getName().equalsIgnoreCase("[D"))
            {
                statisticalAnalysis = new KaplanMeierEstimate(
                        (double[]) dataObject[0], (double[]) dataObject[1]);
            }
            else if (argument.get(ALPHA) != null &&
                     dataObject.length == 2 &&
                     dataObject[0].getClass().getName().
                     equalsIgnoreCase("[D") &&
                     dataObject[1].getClass().getName().equalsIgnoreCase("[D"))
            {
                statisticalAnalysis = new KaplanMeierEstimate(
                        (Double) argument.get(ALPHA),
                        (double[]) dataObject[0], (double[]) dataObject[1]);
            }
            else if (dataObject.length == 3 &&
                     dataObject[2].getClass().getName().equalsIgnoreCase("[[D"))
            {
                statisticalAnalysis = new KaplanMeierEstimate(
                        (double[]) dataObject[0], (double[]) dataObject[1],
                        (double[][]) dataObject[2]);
            }
            else if (dataObject.length >= 3 &&
                     dataObject[0].getClass().getName().
                     equalsIgnoreCase("[D") &&
                     dataObject[1].getClass().getName().
                     equalsIgnoreCase("[D") &&
                     (dataObject.getClass().getName().equalsIgnoreCase(
                             "[Ljava.lang.Object;") ||
                     dataObject.getClass().getName().equalsIgnoreCase("[[D")))
            {
                statisticalAnalysis = new KaplanMeierEstimate(
                        (double[]) dataObject[0], (double[]) dataObject[1],
                        DataManager.castDoubleObject(2, dataObject));
            }
        }
        else
        {
            statisticalAnalysis = new KaplanMeierEstimate();
        }
    }

    /**
     * Computes a Kaplan-Meier estimate given the level of significance,
     * survival times and censor indicators.
     * @param alpha the level of significance.
     * @param time the survival times of the patients.
     * @param censor the censor indicators for the patients,
     * <br>          censor[i]=1: death;
     * <br>          censor[i]=0: censored.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     must have the same length.
     * @exception IllegalArgumentException all elements of the time vector must
     *                                     be postive.
     * @exception IllegalArgumentException all elements of the censor vector
     *                                     must be either 1 or 0.
     */

    public KaplanMeierEstimate(double alpha,
                               double[] time,
                               double[] censor)
    {
        this.alpha = alpha;
        this.time = time;
        this.censor = censor;
        isProportionalHazards = false;
        estimate = estimate(time, censor);
        confidenceInterval = confidenceInterval(alpha, time, censor);
        coefficients = null;
    }

    /**
     * Computes a Kaplan-Meier estimate with a 0.05 level of significance
     * given the survival times and censor indicators.
     * @param time the survival times of the patients.
     * @param censor the censor indicators for the patients,
     * <br>          censor[i]=1: death;
     * <br>          censor[i]=0: censored.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     must have the same length.
     * @exception IllegalArgumentException all elements of the time vector must
     *                                     be postive.
     * @exception IllegalArgumentException all elements of the censor vector
     *                                     must be either 1 or 0.
     */

    public KaplanMeierEstimate(double[] time,
                               double[] censor)
    {
        this(0.05, time, censor);
    }

    /**
     * Computes a Kaplan-Meier estimate under proportional hazards assumption
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
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     must have the same length.
     * @exception IllegalArgumentException all elements of the time vector must
     *                                     be postive.
     * @exception IllegalArgumentException all elements of the censor vector
     *                                     must be either 1 or 0.
     */

    public KaplanMeierEstimate(double[] time,
                               double[] censor,
                               double[] ...covariate)
    {
        this.time = time;
        this.censor = censor;
        this.covariate = covariate;
        isProportionalHazards = true;
        estimate = estimate(time, censor, covariate);
    }

    /**
     * The Kaplan-Meier estimate.
     * @param argument the empty argument.
     * @param dataObject the survival times of the patients and censor
     *                   indicators for the patients.
     * @return the values of the Kaplan Meier estimates,
     * <br>    estimate[i]: the estimate corresponding to the survival time of
     *                      the (i+1)'th patient.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     must have the same length.
     * @exception IllegalArgumentException all elements of the time vector must
     *                                     be postive.
     * @exception IllegalArgumentException all elements of the censor vector
     *                                     must be either 1 or 0.
     */

    public double[] estimate(Hashtable argument,
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
            estimate = estimate((double[]) dataObject[0],
                                (double[]) dataObject[1],
                                (double[][]) dataObject[2]);
        }
        else if (dataObject != null &&
                 dataObject.length == 2 &&
                 dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
                 dataObject[1].getClass().getName().equalsIgnoreCase("[D"))
        {
            estimate = estimate((double[]) dataObject[0],
                                (double[]) dataObject[1]);
        }
        else if (dataObject != null &&
                 dataObject.length >= 3 &&
                 dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
                 dataObject[1].getClass().getName().equalsIgnoreCase("[D") &&
                 (dataObject.getClass().getName().equalsIgnoreCase(
                     "[Ljava.lang.Object;") ||
                 dataObject.getClass().getName().equalsIgnoreCase("[[D")))
        {
            estimate = estimate((double[]) dataObject[0],
                                (double[]) dataObject[1],
                                DataManager.castDoubleObject(2, dataObject));
        }
        else
        {
            throw new IllegalArgumentException(
                    "Wrong input arguments or data.");
        }

        return estimate;
    }

    /**
     * The Kaplan-Meier estimate.
     * @param time the survival times of the patients.
     * @param censor the censor indicators for the patients,
     * <br>          censor[i]=1: death;
     * <br>          censor[i]=0: censored.
     * @return the values of the Kaplan Meier estimates,
     * <br>    estimate[i]: the estimate corresponding to the survival time of
     *                      the (i+1)'th patient.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     must have the same length.
     * @exception IllegalArgumentException all elements of the time vector must
     *                                     be postive.
     * @exception IllegalArgumentException all elements of the censor vector
     *                                     must be either 1 or 0.
     */

    public double[] estimate(double[] time,
                             double[] censor)
    {
        this.time = time;
        this.censor = censor;
        isProportionalHazards = false;
        survivalIndex = new DataManager().survivalIndex(time, censor);
        estimate = new double[time.length];
        variance = new double[time.length];
        for (int i = 0; i < time.length; i++)
        {
            estimate[i] = 1.0;
            variance[i] = 0.0;
            for (int k = 0; k < time.length; k++)
            {
                if (time[k] <= time[i])
                {
                    estimate[i] *=
                            (1 - (survivalIndex[1][k] / survivalIndex[0][k]));
                    variance[i] += survivalIndex[1][k] / (survivalIndex[0][k] *
                            (survivalIndex[0][k] - survivalIndex[1][k]));
                }
            }
            variance[i] *= Math.pow(estimate[i], 2.0);
        }
        output.put(SURVIVAL_ESTIMATE, estimate);
        output.put(SURVIVAL_ESTIMATE_VARIANCE, variance);

        return estimate;
    }

    /**
     * The Kaplan-Meier estimate under proportional hazards assumption.
     * @param time time the survival times of the patients.
     * @param censor the censor indicators for the patients,
     * <br>          censor[i]=1: death;
     * <br>          censor[i]=0: censored.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the values of the Kaplan Meier estimates,
     * <br>    estimate[i]: the estimate corresponding to the survival time of
     *                      the (i+1)'th patient.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     must have the same length.
     * @exception IllegalArgumentException all elements of the time vector must
     *                                     be postive.
     * @exception IllegalArgumentException all elements of the censor vector
     *                                     must be either 1 or 0.
     */

    public double[] estimate(double[] time,
                             double[] censor,
                             double[] ...covariate)
    {
        this.time = time;
        this.censor = censor;
        this.covariate = covariate;
        isProportionalHazards = true;
        coxRegression = new CoxRegression();
        coefficients = coxRegression.coefficients(time, censor, covariate);
        survivalIndex = new DataManager().survivalIndex(time, censor);
        riskFactorVector = new double[time.length];
        estimate = new double[time.length];
        for (int i = 0; i < time.length; i++)
        {
            if ((int) censor[i] == 0)
            {
                riskFactorVector[i] = 1.0;
            }
            else
            {
                riskFactorVector[i] = 0.0;
                for (int k = 0; k < time.length; k++)
                {
                    if (time[k] >= time[i])
                    {
                        riskFactor = 1;
                        for (int l = 0; l < coefficients.length; l++)
                        {
                            riskFactor *=
                                    Math.exp(coefficients[l] * covariate[l][k]);
                        }
                        riskFactorVector[i] += riskFactor;
                    }
                }
            }
        }
        for (int i = 0; i < time.length; i++)
        {
            estimate[i] = 1.0;
            for (int k = 0; k < time.length; k++)
            {
                if (time[k] <= time[i])
                {
                    estimate[i] *=
                            (1 - (survivalIndex[1][k] / riskFactorVector[k]));
                }
            }
        }
        output.put(COEFFICIENTS, coefficients);
        output.put(SURVIVAL_ESTIMATE, estimate);

        return estimate;
    }

    /**
     * The confidence intervals for survival function.
     * @param argument the argument with the following choices,
     * <br> ALPHA: the level of significance;
     * <br> empty argument: default level of significance equal to 0.05.
     * <br><br>
     * @param dataObject the survival times of the patients and censor
     *                   indicators for the patients.
     * @return the confidence intervals for values of the survival function,
     * <br>    confidenceInterval[i][0]: the lower bound corresponding to the
     *                                   survival time of the (i+1)'th patient;
     * <br>    confidenceInterval[i][1]: the upper bound corresponding to the
     *                                   survival time of the (i+1)'th patient.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     must have the same length.
     * @exception IllegalArgumentException all elements of the time vector must
     *                                     be postive.
     * @exception IllegalArgumentException all elements of the censor vector
     *                                     must be either 1 or 0.
     */

    public double[][] confidenceInterval(Hashtable argument,
                                         Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (argument.get(ALPHA) != null &&
            dataObject != null &&
            dataObject.length == 2 &&
            dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
            dataObject[1].getClass().getName().equalsIgnoreCase("[D"))
        {
            confidenceInterval = confidenceInterval(
                    (Double) argument.get(ALPHA),
                    (double[]) dataObject[0], (double[]) dataObject[1]);
        }
        else if (dataObject != null &&
                 dataObject.length == 2 &&
                 dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
                 dataObject[1].getClass().getName().equalsIgnoreCase("[D"))
        {
            confidenceInterval = confidenceInterval(
                    (double[]) dataObject[0], (double[]) dataObject[1]);
        }
        else
        {
            throw new IllegalArgumentException(
                    "Wrong input arguments or data.");
        }

        return confidenceInterval;
    }

    /**
     * The confidence intervals for survival function.
     * @param alpha the level of significance.
     * @param time the survival times of the patients.
     * @param censor the censor indicators for the patients,
     * <br>          censor[i]=1: death;
     * <br>          censor[i]=0: censored.
     * @return the confidence intervals for values of the survival function,
     * <br>    confidenceInterval[i][0]: the lower bound corresponding to the
     *                                   survival time of the (i+1)'th patient;
     * <br>    confidenceInterval[i][1]: the upper bound corresponding to the
     *                                   survival time of the (i+1)'th patient.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     must have the same length.
     * @exception IllegalArgumentException all elements of the time vector must
     *                                     be postive.
     * @exception IllegalArgumentException all elements of the censor vector
     *                                     must be either 1 or 0.
     */

    public double[][] confidenceInterval(double alpha,
                                         double[] time,
                                         double[] censor)
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
        isProportionalHazards = false;
        estimate = estimate(time, censor);
        zAlpha = new NormalDistribution().inverse((1 - alpha / 2));
        confidenceInterval = new double[time.length][2];
        for (int i = 0; i < time.length; i++)
        {
            confidenceInterval[i][0] = estimate[i] -
                                       zAlpha * Math.sqrt(variance[i]);
            confidenceInterval[i][1] = estimate[i] +
                                       zAlpha * Math.sqrt(variance[i]);
            if (confidenceInterval[i][0] < 0)
            {
                confidenceInterval[i][0] = 0.0;
            }
            if (confidenceInterval[i][1] > 1)
            {
                confidenceInterval[i][1] = 1.0;
            }
        }
        output.put(CONFIDENCE_INTERVAL, confidenceInterval);

        return confidenceInterval;
    }

    /**
     * The 95% confidence intervals for survival function.
     * @param time the survival times of the patients.
     * @param censor the censor indicators for the patients,
     * <br>          censor[i]=1: death;
     * <br>          censor[i]=0: censored.
     * @return the confidence intervals for values of the survival function,
     * <br>    confidenceInterval[i][0]: the lower bound corresponding to the
     *                                   survival time of the (i+1)'th patient;
     * <br>    confidenceInterval[i][1]: the upper bound corresponding to the
     *                                   survival time of the (i+1)'th patient.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     must have the same length.
     * @exception IllegalArgumentException all elements of the time vector must
     *                                     be postive.
     * @exception IllegalArgumentException all elements of the censor vector
     *                                     must be either 1 or 0.
     */

    public double[][] confidenceInterval(double[] time,
                                         double[] censor)
    {
        return confidenceInterval(0.05, time, censor);
    }

    /**
     * The variances of the Kaplan-Meier estimates.
     * @param argument the empty argument.
     * @param dataObject the survival times of the patients and censor
     *                   indicators for the patients.
     * @return the variances of the Kaplan Meier estimates,
     * <br>    variance[i]: the variance of the Kaplan Meier estimate
     *                      corresponding to the survival time of the (i+1)'th
     *                      patient.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     must have the same length.
     * @exception IllegalArgumentException all elements of the time vector must
     *                                     be postive.
     * @exception IllegalArgumentException all elements of the censor vector
     *                                     must be either 1 or 0.
     */

    public double[] variance(Hashtable argument,
                             Object ...dataObject)
    {
        if (dataObject != null &&
            dataObject.length == 2 &&
            dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
            dataObject[1].getClass().getName().equalsIgnoreCase("[D"))
        {
            variance = variance((double[]) dataObject[0],
                                (double[]) dataObject[1]);
        }
        else
        {
            throw new IllegalArgumentException(
                    "Wrong input arguments or data.");
        }

        return variance;
    }

    /**
     * The variances of the Kaplan-Meier estimates.
     * @param time the survival times of the patients.
     * @param censor the censor indicators for the patients,
     * <br>          censor[i]=1: death;
     * <br>          censor[i]=0: censored.
     * @return the variances of the Kaplan Meier estimates,
     * <br>    variance[i]: the variance of the Kaplan Meier estimate
     *                      corresponding to the survival time of the (i+1)'th
     *                      patient.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     must have the same length.
     * @exception IllegalArgumentException all elements of the time vector must
     *                                     be postive.
     * @exception IllegalArgumentException all elements of the censor vector
     *                                     must be either 1 or 0.
     */

    public double[] variance(double[] time,
                             double[] censor)
    {
        this.time = time;
        this.censor = censor;
        isProportionalHazards = false;
        estimate(time, censor);

        return variance;
    }

}
