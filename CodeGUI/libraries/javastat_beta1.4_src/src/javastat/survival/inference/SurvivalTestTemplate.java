package javastat.survival.inference;

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
import static javastat.util.Output.*;
import javastat.util.*;

import JSci.maths.statistics.*;

/**
 *
 * <p>Calculates the test statistic and p-value for the comparison of two groups
 * of survival data.</p>
 * <p> </p>
 */

public abstract class SurvivalTestTemplate extends StatisticalInference
{

    /**
     * The test statistic.
     */

    public double testStatistic;

    /**
     * The variance of the test statistic.
     */

    public double variance;

    /**
     * The p value.
     */

    public double pValue;

    /**
     * The survival times of the patients in group 1.
     */

    public double[] time1;

    /**
     * The censor indicators for the patients in group 1,
     * <br> censor1[i]=1.0: death;
     * <br> censor1[i]=0.0: censored.
     */

    public double[] censor1;

    /**
     * The survival times of the patients in group 2.
     */

    public double[] time2;

    /**
     * The censor indicators for the patients in group 2,
     * <br> censor2[i]=1.0: death;
     * <br> censor2[i]=0.0: censored.
     */

    public double[] censor2;

    /**
     * The object represents a test for the comparison of two groups of survival
     * data.
     */

    public StatisticalAnalysis statisticalAnalysis;

    /**
     * The total number of patients at some time T.
     */

    private double ni;

    /**
     * The number of patients who die at some time T.
     */

    private double di;

    /**
     * The number of patients from group 1 at some time T.
     */

    private double n1i;

    /**
     * The number of patients from group 1 who die at some time T.
     */

    private double d1i;

    /**
     * The number of patients from group 2 at some time T.
     */

    private double n2i;

    /**
     * The number of patients from group 2 who die at some time T.
     */

    private double d2i;

    /**
     * The number of patients at risk and the number of patients who die in
     * group 1.
     */

    private double[][] survivalIndex1;

    /**
     * The number of patients at risk and the number of patients who die in
     * group 2.
     */

    private double[][] survivalIndex2;

    /**
     * The class contains the collections of some basic methods for manipulating
     * the data.
     */

    private DataManager dataManager;

    /**
     * Default SurvivalTestTemplate constructor.
     */

    public SurvivalTestTemplate() {}

    /**
     * Constructs a test given the survival times and censor indicators in two
     * groups.
     * @param argument the empty argument.
     * @param dataObject the survival times of the patients and censor
     *                   indicators for the patients in both groups.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     in group 1 must have the same length.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     in group 2 must have the same length.
     * @exception IllegalArgumentException all elements of the time vector in
     *                                     group 1 must be postive.
     * @exception IllegalArgumentException all elements of the censor vector in
     *                                     group 1 must be either 1 or 0.
     * @exception IllegalArgumentException all elements of the time vector in
     *                                     group 2 must be postive.
     * @exception IllegalArgumentException all elements of the censor vector in
     *                                     group 2 must be either 1 or 0.
     */

    public SurvivalTestTemplate(Hashtable argument,
                                Object ...dataObject)
    {
        this((double[]) dataObject[0], (double[]) dataObject[1],
             (double[]) dataObject[2], (double[]) dataObject[3]);
        this.argument = argument;
        this.dataObject = dataObject;
    }

    /**
     * Constructs a test given the survival times and censor indicators in
     * two groups.
     * @param time1 the survival times of the patients in group 1.
     * @param censor1 the censor indicators for the patients in group 1,
     * <br>           censor1[i]=1.0: death;
     * <br>           censor1[i]=0.0: censored.
     * @param time2 the survival times of the patients in group 2.
     * @param censor2 the censor indicators for the patients in group 2,
     * <br>           censor2[i]=1.0: death;
     * <br>           censor2[i]=0.0: censored.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     in group 1 must have the same length.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     in group 2 must have the same length.
     * @exception IllegalArgumentException all elements of the time vector in
     *                                     group 1 must be postive.
     * @exception IllegalArgumentException all elements of the censor vector in
     *                                     group 1 must be either 1 or 0.
     * @exception IllegalArgumentException all elements of the time vector in
     *                                     group 2 must be postive.
     * @exception IllegalArgumentException all elements of the censor vector in
     *                                     group 2 must be either 1 or 0.
     */

    public SurvivalTestTemplate(double[] time1,
                                double[] censor1,
                                double[] time2,
                                double[] censor2)
    {
        this.time1 = time1;
        this.censor1 = censor1;
        this.time2 = time2;
        this.censor2 = censor2;
        testStatistic = testStatistic(time1, censor1, time2, censor2);
    }

    /**
     * The weigtht function.
     * @param parameter the parameter for the weight function.
     * @return the weight function evaluated at the value of the parameter.
     */

    public abstract double weight(double parameter);

    /**
     * The test statistic.
     * @param argument the empty argument.
     * @param dataObject the survival times of the patients and
     * censor indicators for the patients in both groups.
     * @return the value of the test statistic.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     in group 1 must have the same length.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     in group 2 must have the same length.
     * @exception IllegalArgumentException all elements of the time vector in
     *                                     group 1 must be postive.
     * @exception IllegalArgumentException all elements of the censor vector in
     *                                     group 1 must be either 1 or 0.
     * @exception IllegalArgumentException all elements of the time vector in
     *                                     group 2 must be postive.
     * @exception IllegalArgumentException all elements of the censor vector in
     *                                     group 2 must be either 1 or 0.
     */

    public Double testStatistic(Hashtable argument,
                                Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null &&
            dataObject.length == 4)
        {
            testStatistic = testStatistic(
                    (double[]) dataObject[0], (double[]) dataObject[1],
                    (double[]) dataObject[2], (double[]) dataObject[3]);
        }
        else
        {
            throw new IllegalArgumentException(
                    "Wrong input arguments or data.");
        }

        return testStatistic;
    }

    /**
     * The test statistic.
     * @param time1 the survival times of the patients in group 1.
     * @param censor1 the censor indicators for the patients in group 1,
     * <br>           censor1[i]=1.0: death;
     * <br>           censor1[i]=0.0: censored.
     * @param time2 the survival times of the patients in group 2.
     * @param censor2 the censor indicators for the patients in group 2,
     * <br>           censor2[i]=1.0: death;
     * <br>           censor2[i]=0.0: censored.
     * @return the value of the test statistic.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     in group 1 must have the same length.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     in group 2 must have the same length.
     * @exception IllegalArgumentException all elements of the time vector in
     *                                     group 1 must be postive.
     * @exception IllegalArgumentException all elements of the censor vector in
     *                                     group 1 must be either 1 or 0.
     * @exception IllegalArgumentException all elements of the time vector in
     *                                     group 2 must be postive.
     * @exception IllegalArgumentException all elements of the censor vector in
     *                                     group 2 must be either 1 or 0.
     */

    public double testStatistic(double[] time1,
                                double[] censor1,
                                double[] time2,
                                double[] censor2)
    {
        this.time1 = time1;
        this.censor1 = censor1;
        this.time2 = time2;
        this.censor2 = censor2;
        dataManager = new DataManager();
        dataManager.checkPositiveRange(time1, "time1");
        dataManager.checkPositiveRange(time2, "time1");
        if (time1.length != censor1.length)
        {
            throw new IllegalArgumentException(
                    "The time vector and the censor vector in group 1 must " +
                    "have the same length.");
        }
        if (time2.length != censor2.length)
        {
            throw new IllegalArgumentException(
                    "The time vector and the censor vector in group 2 must " +
                    "have the same length.");
        }
        survivalIndex1 = dataManager.survivalIndex(time1, censor1);
        survivalIndex2 = dataManager.survivalIndex(time2, censor2);
        testStatistic = 0.0;
        variance = 0.0;
        for (int i = 0; i < (time1.length + time2.length - 1); i++)
        {
            if (i >= time1.length)
            {
                if ((int) censor2[i - time1.length] == 1)
                {
                    n1i = 0.0;
                    d1i = 0.0;
                    for (int k = 0; k < time1.length; k++)
                    {
                        if (time1[k] > time2[i - time1.length])
                        {
                            n1i += 1.0;
                        }
                        if (time1[k] == time2[i - time1.length])
                        {
                            n1i += 1.0;
                            if ((int) censor1[k] == 1)
                            {
                                d1i += 1;
                            }
                        }
                    }
                    di = d1i + survivalIndex2[1][i - time1.length];
                    ni = n1i + survivalIndex2[0][i - time1.length];
                    testStatistic += weight(ni) * (d1i - (n1i * di / ni));
                    variance += Math.pow(weight(ni), 2.0) *
                            (n1i * survivalIndex2[0][i -
                             time1.length] * di * (ni - di)) /
                            (Math.pow(ni, 2.0) * (ni - 1));
                }
            }
            else
            {
                if ((int) censor1[i] == 1)
                {
                    n2i = 0.0;
                    d2i = 0.0;
                    for (int k = 0; k < time2.length; k++)
                    {
                        if (time2[k] > time1[i])
                        {
                            n2i += 1.0;
                        }
                        if (time2[k] == time1[i])
                        {
                            n2i += 1.0;
                            if ((int) censor2[k] == 1)
                            {
                                d2i += 1;
                            }
                        }
                    }
                    di = d2i + survivalIndex1[1][i];
                    ni = n2i + survivalIndex1[0][i];
                    testStatistic += weight(ni) *
                            (survivalIndex1[1][i] -
                             (survivalIndex1[0][i] * di / ni));
                    variance += Math.pow(weight(ni), 2.0) *
                            ((n2i * survivalIndex1[0][i] * di * (ni - di)) /
                             (Math.pow(ni, 2.0) * (ni - 1)));
                }
            }
        }
        pValue = 2 * (1 - new NormalDistribution().cumulative(
            Math.abs(testStatistic / Math.pow(variance, 0.5))));
        output.put(TEST_STATISTIC, testStatistic);
        output.put(PVALUE, pValue);

        return testStatistic;
    }

    /**
     * The p-value.
     * @param argument the empty argument.
     * @param dataObject the survival times of the patients and
     * censor indicators for the patients in both groups.
     * @return the p value for the test.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     in group 1 must have the same length.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     in group 2 must have the same length.
     * @exception IllegalArgumentException all elements of the time vector in
     *                                     group 1 must be postive.
     * @exception IllegalArgumentException all elements of the censor vector in
     *                                     group 1 must be either 1 or 0.
     * @exception IllegalArgumentException all elements of the time vector in
     *                                     group 2 must be postive.
     * @exception IllegalArgumentException all elements of the censor vector in
     *                                     group 2 must be either 1 or 0.
     */

    public Double pValue(Hashtable argument,
                         Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null &&
            dataObject.length == 4)
        {
            pValue = pValue((double[]) dataObject[0], (double[]) dataObject[1],
                            (double[]) dataObject[2], (double[]) dataObject[3]);
        }
        else
        {
            throw new IllegalArgumentException("Wrong input arguments or " +
                                               "data.");
        }

        return pValue;
    }

    /**
     * The p-value.
     * @param time1 the survival times of the patients in group 1.
     * @param censor1 the censor indicators for the patients in group 1,
     * <br>           censor1[i]=1.0: death;
     * <br>           censor1[i]=0.0: censored.
     * @param time2 the survival times of the patients in group 2.
     * @param censor2 the censor indicators for the patients in group 2,
     * <br>           censor2[i]=1.0: death;
     * <br>           censor2[i]=0.0: censored.
     * @return the p value for the test.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     in group 1 must have the same length.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     in group 2 must have the same length.
     * @exception IllegalArgumentException all elements of the time vector in
     *                                     group 1 must be postive.
     * @exception IllegalArgumentException all elements of the censor vector in
     *                                     group 1 must be either 1 or 0.
     * @exception IllegalArgumentException all elements of the time vector in
     *                                     group 2 must be postive.
     * @exception IllegalArgumentException all elements of the censor vector in
     *                                     group 2 must be either 1 or 0.
     */

    public double pValue(double[] time1,
                         double[] censor1,
                         double[] time2,
                         double[] censor2)
    {
        this.time1 = time1;
        this.censor1 = censor1;
        this.time2 = time2;
        this.censor2 = censor2;
        testStatistic(time1, censor1, time2, censor2);

        return pValue;
    }

}
