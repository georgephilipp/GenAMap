package javastat.inference.onesample;

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
import javastat.inference.*;
import static javastat.util.Argument.*;
import static javastat.util.Output.*;
import javastat.util.*;

import JSci.maths.statistics.*;

/**
 *
 * <p>Calculates the test statistic, confidence interval, and p-value for a one
 * sample proportion problem.</p>
 * <p> </p>
 * <br> Example:
 * <br> // Non-null constructor
 * <br> OneSampProp testclass1 = new OneSampProp(0.05, 0.5, "less", 30, 100);
 * <br> double testStatistic = testclass1.testStatistic;
 * <br> double pValue = testclass1.pValue;
 * <br> double lowerBound = testclass1.confidenceInterval[0];
 * <br> double upperBound = testclass1.confidenceInterval[1];
 * <br>
 * <br> // Null constructor
 * <br> OneSampProp testclass2 = new OneSampProp();
 * <br> double [] confidenceInterval =
 *        testclass2.confidenceInterval(0.05, 30, 100);
 * <br> testStatistic = testclass2.testStatistic(0.5, 30, 100);
 * <br> pValue = testclass2.pValue(0.5, "greater", 30, 100);
 * <br>
 * <br> // Non-null constructor
 * <br> Hashtable argument1 = new Hashtable();
 * <br> argument1.put(ALPHA, 0.05);
 * <br> argument1.put(NULL_VALUE, 0.5);
 * <br> argument1.put(SIDE, "less");
 * <br> StatisticalAnalysis testclass3 =
 * <br> &nbsp;&nbsp;&nbsp;
 *        new OneSampProp(argument1, 30, 100).statisticalAnalysis;
 * <br> testStatistic = (Double) testclass3.output.get(TEST_STATISTIC);
 * <br> pValue = (Double) testclass3.output.get(PVALUE);
 * <br> confidenceInterval =
 *        (double[]) testclass3.output.get(CONFIDENCE_INTERVAL);
 * <br> lowerBound = confidenceInterval[0];
 * <br> upperBound = confidenceInterval[1];
 * <br>
 * <br> // Null constructor
 * <br> Hashtable argument2 = new Hashtable();
 * <br> OneSampProp testclass4 = new OneSampProp(argument2, null);
 * <br> argument2.put(ALPHA, 0.05);
 * <br> confidenceInterval = testclass4.confidenceInterval(argument2, 30, 100);
 * <br> argument2.put(NULL_VALUE, 0.5);
 * <br> testStatistic = testclass4.testStatistic(argument2, 30, 100);
 * <br> argument2.put(SIDE, "greater");
 * <br> pValue = testclass4.pValue(argument2, 30, 100);
 * <br>
 * <br> // Obtains the information about the output
 * <br> out.println(testclass3.output.toString());
 * <br> out.println(testclass4.output.toString());
 */

public class OneSampProp extends StatisticalInferenceTemplate implements
        OneSampInferenceInterface
{

    /**
     * The level of significance.
     */

    public double alpha;

    /**
     * The standard error of the sample proportion under H0.
     */

    public double proportionSEH0;

    /**
     * The number of successes.
     */

    public double count;

    /**
     * The sample size (the number of trials).
     */

    public double sampleSize;

    /**
     * The value of the proportion under test.
     */

    public double p0;

    /**
     * The object represents a one-sample proportion test.
     */

    public StatisticalAnalysis statisticalAnalysis;

    /**
     * Constructs a one sample test for population proportion.
     */

    public OneSampProp() {}

    /**
     * Constructs a one sample test given the input arguments and data.
     * @param argument the arguments with the following choices,
     * <br> ALPHA, NULL_VALUE, SIDE: complete list of arguments;
     * <br> NULL_VALUE, SIDE: default level of signifiance equal to 0.05;
     * <br> NULL_VALUE: default level of signifiance equal to 0.05 and two-sided
     *                  alternative hypothesis;
     * <br> empty argument: defalut level of significance equal to 0.05,
     *                      two-sided alternative hypothesis and the null value
     *                      equal to 0.5.
     * <br><br>
     * @param dataObject the numbers of successes and trials.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the counts and sample size should
     *                                     be (strictly) positive.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the proportion under test should be
     *                                     (strictly) positive.
     */

    public OneSampProp(Hashtable argument,
                       Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (argument.size() > 0 &&
            dataObject != null)
        {
            if (argument.get(ALPHA) != null &&
                argument.get(NULL_VALUE) != null &&
                argument.get(SIDE) != null &&
                dataObject.length == 2)
            {
                statisticalAnalysis = new OneSampProp(
                        (Double) argument.get(ALPHA),
                        ((Number) argument.get(NULL_VALUE)).doubleValue(),
                        (String) argument.get(SIDE),
                        ((Number) dataObject[0]).doubleValue(),
                        ((Number) dataObject[1]).doubleValue());
            }
            else if (argument.get(NULL_VALUE) != null &&
                     argument.get(SIDE) != null &&
                     dataObject.length == 2)
            {
                statisticalAnalysis = new OneSampProp(
                        ((Number) argument.get(NULL_VALUE)).doubleValue(),
                        (String) argument.get(SIDE),
                        ((Number) dataObject[0]).doubleValue(),
                        ((Number) dataObject[1]).doubleValue());
            }
            else if (argument.get(NULL_VALUE) != null &&
                     dataObject.length == 2)
            {
                statisticalAnalysis = new OneSampProp(
                        ((Number) argument.get(NULL_VALUE)).doubleValue(),
                        ((Number) dataObject[0]).doubleValue(),
                        ((Number) dataObject[1]).doubleValue());
            }
            else
            {
                throw new IllegalArgumentException(
                        "Wrong input arguments or data.");
            }
        }
        else if (dataObject != null &&
                 dataObject.length == 2)
        {
            statisticalAnalysis = new OneSampProp(
                    ((Number) dataObject[0]).doubleValue(),
                    ((Number) dataObject[1]).doubleValue());
        }
        else if (dataObject == null)
        {
            statisticalAnalysis = new OneSampProp();
        }
        else
        {
            throw new IllegalArgumentException("Wrong input data.");
        }
    }

    /**
     * Constructs a one sample test given the input data, level of
     * significance, value of the proportion under test and alternative
     * hypothesis.
     * @param alpha the level of significance.
     * @param p0 the value of the proportion under test.
     * @param side the specification of the alternative hypothesis with the
     *             choices "greater", "less" or "equal" (or "two.sided").
     * @param count the number of successes.
     * @param sampleSize the sample size (the number of trials).
     * @exception IllegalArgumentException the counts and sample size should
     *                                      be (strictly) positive.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the proportion under test should be
     *                                     (strictly) positive.
     */

    public OneSampProp(double alpha,
                       double p0,
                       String side,
                       double count,
                       double sampleSize)
    {
        this.alpha = alpha;
        this.side = side;
        this.count = count;
        this.sampleSize = sampleSize;
        confidenceInterval = confidenceInterval(alpha, count, sampleSize);
        testStatistic = testStatistic(p0, count, sampleSize);
        pValue = pValue(p0, side, count, sampleSize);
    }

    /**
     * Constructs a one sample test with a 0.05 level of significance given
     * the input data, value of the proportion under test and alternative
     * hypothesis.
     * @param p0 the value of the proportion under test.
     * @param side the specification of the alternative hypothesis with the
     *             choices "greater", "less" or "equal" (or "two.sided").
     * @param count the number of successes.
     * @param sampleSize the sample size (the number of trials).
     * @exception IllegalArgumentException the counts and sample size should be
     *                                     (strictly) positive.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the proportion under test should be
     *                                     (strictly) positive.
     */

    public OneSampProp(double p0,
                       String side,
                       double count,
                       double sampleSize)
    {
        this(0.05, p0, side, count, sampleSize);
    }

    /**
     * Constructs a two-tailed test with a 0.05 level of significance given
     * the input data and value of the proportion under test.
     * @param p0 the value of the proportion under test.
     * @param count the number of successes.
     * @param sampleSize the sample size (the number of trials).
     * @exception IllegalArgumentException the counts and sample size should
     *                                     be (strictly) positive.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the proportion under test should be
     *                                     (strictly) positive.
     */

    public OneSampProp(double p0,
                       double count,
                       double sampleSize)
    {
        this(0.05, p0, "equal", count, sampleSize);
    }

    /**
     * Constructs a two-tailed test with a 0.05 level of significance given
     * the input data and value of the proportion under test equal to 0.5.
     * @param count the number of successes.
     * @param sampleSize the sample size (the number of trials).
     * @exception IllegalArgumentException the counts and sample size should
     *                                     be (strictly) positive.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the proportion under test should be
     *                                     (strictly) positive.
     */

    public OneSampProp(double count,
                       double sampleSize)
    {
        this(0.05, 0.5, "equal", count, sampleSize);
    }

    /**
     * Computes the point estimate, which is the sample proportion.
     * @param count the number of successes.
     * @param sampleSize the sample size (the number of trials).
     * @return the point estimate.
     */

    public double pointEstimate(double count,
                                double sampleSize)
    {
        pointEstimate = new BasicStatistics().proportion(count, sampleSize);
        output.put(POINT_ESTIMATE, pointEstimate);

        return pointEstimate;
    }

    /**
     * Computes the point estimate given the input argument and data.
     * @param argument the empty argument.
     * @param dataObject the numbers of successes and trials.
     * @return the point estimate.
     */

    public Double pointEstimate(Hashtable argument,
                                Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;

        return pointEstimate(((Number) dataObject[0]).doubleValue(),
                             ((Number) dataObject[1]).doubleValue());
    }

    /**
     * Computes the standard error of the point estimate.
     * @param argument the argument with the following choices,
     * <br> NULL_VALUE: the null value under the null hypothesis;
     * <br> empty argument: the null value equal to 0.5.
     * <br><br>
     * @param dataObject the numbers of successes and trials.
     * @return the standard error of the point estimate.
     * @exception IllegalArgumentException the proportion under test should be
     *                                     (strictly) positive.
     */

    public Double pointEstimateSE(Hashtable argument,
                                  Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (argument.get(NULL_VALUE) != null)
        {
            p0 = ((Number) argument.get(NULL_VALUE)).doubleValue();
            pointEstimateSE = pointEstimateSE(p0,
                                              ((Number) dataObject[0]).
                                              doubleValue(),
                                              ((Number) dataObject[1]).
                                              doubleValue());
        }
        else
        {
            pointEstimateSE = pointEstimateSE(((Number) dataObject[0]).
                                              doubleValue(),
                                              ((Number) dataObject[1]).
                                              doubleValue());
        }

        return pointEstimateSE;
    }

    /**
     * Computes the standard error of the point estimate under the null
     * hypothesis.
     * @param p0 the value of the proportion under test.
     * @param count the number of successes.
     * @param sampleSize the sample size (the number of trials).
     * @return the standard error of the point estimate.
     * @exception IllegalArgumentException the proportion under test should be
     *                                     (strictly) positive.
     */

    public double pointEstimateSE(double p0,
                                  double count,
                                  double sampleSize)
    {
        if ((p0 <= 0.0))
        {
            throw new IllegalArgumentException(
                    "The proportion under test should be (strictly) positive.");
        }
        argument.put(NULL_VALUE, p0);
        pointEstimateSE = proportionSEH0 =
                Math.sqrt((p0 * (1 - p0)) / sampleSize);
        output.put(POINT_ESTIMATE_SE, pointEstimateSE);
        output.put(PROPORTION_SE_H0, proportionSEH0);

        return pointEstimateSE;
    }

    /**
     * Computes the standard error of the point estimate.
     * @param count the number of successes.
     * @param sampleSize the sample size (the number of trials).
     * @return the standard error of the point estimate.
     */

    public double pointEstimateSE(double count,
                                  double sampleSize)
    {
        pointEstimateSE = new BasicStatistics().proportionSE(count, sampleSize);
        output.put(POINT_ESTIMATE_SE, pointEstimateSE);

        return pointEstimateSE;
    }

    /**
     * The confidence interval.
     * @param argument the argument with the following choices,
     * <br> ALPHA: the level of significance;
     * <br> empty argument: defalut level of significance equal to 0.05.
     * <br><br>
     * @param dataObject the numbers of successes and trials.
     * @return the confidence interval,
     * <br>    confidenceInterval[0]: the lower bound;
     * <br>    confidenceInterval[1]: the upper bound.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the counts and sample size should
     *                                     be (strictly) positive.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     */

    public double[] confidenceInterval(Hashtable argument,
                                       Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (argument.get(ALPHA) != null &&
            dataObject != null &&
            dataObject.length == 2)
        {
            confidenceInterval = confidenceInterval(
                    (Double) argument.get(ALPHA),
                    ((Number) dataObject[0]).doubleValue(),
                    ((Number) dataObject[1]).doubleValue());
        }
        else if (dataObject != null &&
                 dataObject.length == 2)
        {
            confidenceInterval = confidenceInterval(((Number) dataObject[0]).
                    doubleValue(),
                    ((Number) dataObject[1]).doubleValue());
        }
        else
        {
            throw new IllegalArgumentException("Wrong input arguments or " +
                                               "data.");
        }

        return confidenceInterval;
    }

    /**
     * The confidence interval.
     * @param alpha the level of significance.
     * @param count the number of successes.
     * @param sampleSize the sample size (the number of trials).
     * @return the confidence interval,
     * <br>    confidenceInterval[0]: the lower bound;
     * <br>    confidenceInterval[1]: the upper bound.
     * @exception IllegalArgumentException the counts and sample size should
     *                                     be (strictly) positive.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     */

    public double[] confidenceInterval(double alpha,
                                       double count,
                                       double sampleSize)
    {
        if ((alpha <= 0.0) || (alpha > 1))
        {
            throw new IllegalArgumentException(
                    "The level of significance should be (strictly) positive " +
                    "and not greater than 1.");
        }
        this.alpha = alpha;
        argument.put(ALPHA, alpha);
        argument.put(CRITICAL_VALUE,
                     new NormalDistribution().inverse((1 - alpha / 2)));

        return (double[])super.confidenceInterval(argument, count, sampleSize);
    }

    /**
     * The 95% confidence interval.
     * @param count the number of successes.
     * @param sampleSize the sample size (the number of trials).
     * @return the confidence interval,
     * <br>    confidenceInterval[0]: the lower bound;
     * <br>    confidenceInterval[1]: the upper bound.
     * @exception IllegalArgumentException the counts and sample size should
     *                                     be (strictly) positive.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     */

    public double[] confidenceInterval(double count,
                                       double sampleSize)
    {
        return confidenceInterval(0.05, count, sampleSize);
    }

    /**
     * The test statistic.
     * @param argument the argument with the following choices,
     * <br> NULL_VALUE: the null value;
     * <br> empty argument: the null value equal to 0.5.
     * <br><br>
     * @param dataObject the numbers of successes and trials.
     * @return the value of the test statistic.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the counts and sample size should
     *                                     be (strictly) positive.
     * @exception IllegalArgumentException the proportion under test should be
     *                                     (strictly) positive.
     */

    public Double testStatistic(Hashtable argument,
                                Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (argument.get(NULL_VALUE) != null &&
            dataObject != null &&
            dataObject.length == 2)
        {
            testStatistic = testStatistic(
                    ((Number) argument.get(NULL_VALUE)).doubleValue(),
                    ((Number) dataObject[0]).doubleValue(),
                    ((Number) dataObject[1]).doubleValue());
        }
        else if (dataObject != null &&
                 dataObject.length == 2)
        {
            testStatistic = testStatistic((
                    (Number) dataObject[0]).doubleValue(),
                    ((Number) dataObject[1]).doubleValue());
        }
        else
        {
            throw new IllegalArgumentException("Wrong input arguments or " +
                                               "data.");
        }

        return testStatistic;
    }

    /**
     * The test statistic.
     * @param p0 the value of the proportion under test.
     * @param count the number of successes.
     * @param sampleSize the sample size (the number of trials).
     * @return the value of the test statistic.
     * @exception IllegalArgumentException the counts and sample size should
     *                                     be (strictly) positive.
     * @exception IllegalArgumentException the proportion under test should be
     *                                     (strictly) positive.
     */

    public double testStatistic(double p0,
                                double count,
                                double sampleSize)
    {
        if ((p0 <= 0.0))
        {
            throw new IllegalArgumentException(
                    "The proportion under test should be (strictly) positive.");
        }
        this.p0 = p0;
        argument.put(NULL_VALUE, p0);

        return (Double)super.testStatistic(argument, count, sampleSize);
    }

    /**
     * The test statistic with the null value equal to 0.5.
     * @param count the number of successes.
     * @param sampleSize the sample size (the number of trials).
     * @return the value of the test statistic.
     * @exception IllegalArgumentException the counts and sample size should
     *                                     be (strictly) positive.
     * @exception IllegalArgumentException the proportion under test should be
     *                                     (strictly) positive.
     */

    public double testStatistic(double count,
                                double sampleSize)
    {
        return testStatistic(0.5, count, sampleSize);
    }

    /**
     * The p value.
     * @param argument the arguments with the following choices,
     * <br> NULL_VALUE, SIDE: complete list of arguments;
     * <br> NULL_VALUE: the two-sided alternative hypothesis;
     * <br> empty argument: the two-sided alternative hypothesis and
     *                      null value equal to 0.5.
     * <br><br>
     * @param dataObject the numbers of successes and trials.
     * @return the p value for the test.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the counts and sample size should
     *                                     be (strictly) positive.
     */

    public Double pValue(Hashtable argument,
                         Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (argument.get(NULL_VALUE) != null &&
            argument.get(SIDE) != null &&
            dataObject != null &&
            dataObject.length == 2)
        {
            pValue = pValue(((Number) argument.get(NULL_VALUE)).doubleValue(),
                            (String) argument.get(SIDE),
                            ((Number) dataObject[0]).doubleValue(),
                            ((Number) dataObject[1]).doubleValue());
        }
        else if (argument.get(NULL_VALUE) != null &&
                 dataObject != null &&
                   dataObject.length == 2)
        {
            pValue = pValue(((Number) argument.get(NULL_VALUE)).doubleValue(),
                            ((Number) dataObject[0]).doubleValue(),
                            ((Number) dataObject[1]).doubleValue());
        }
        else if (dataObject != null &&
                 dataObject.length == 2)
        {
            pValue = pValue(((Number) dataObject[0]).doubleValue(),
                            ((Number) dataObject[1]).doubleValue());
        }
        else
        {
            throw new IllegalArgumentException("Wrong input arguments or " +
                                               "data.");
        }

        return pValue;
    }

    /**
     * The p value.
     * @param p0 the value of the proportion under test.
     * @param side the specification of the alternative hypothesis with the
     *             choices "greater", "less" or "equal" (or "two.sided").
     * @param count the number of successes.
     * @param sampleSize the sample size (the number of trials).
     * @return  the p value for the test.
     * @exception IllegalArgumentException the counts and sample size should
     *                                     be (strictly) positive.
     * @exception IllegalArgumentException the proportion under test should be
     *                                     (strictly) positive.
     */

    public double pValue(double p0,
                         String side,
                         double count,
                         double sampleSize)
    {
        this.p0 = p0;
        this.side = side;
        testStatistic = testStatistic(p0, count, sampleSize);
        argument.put(SIDE, side);
        argument.put(CDF, new NormalDistribution().cumulative(testStatistic));

        return super.pValue(argument);
    }

    /**
     * The p value for the two-sided alternative hypothesis.
     * @param p0 the value of the proportion under test.
     * @param count the number of successes.
     * @param sampleSize the sample size (the number of trials).
     * @return the p value for the test.
     * @exception IllegalArgumentException the counts and sample size should
     *                                     be (strictly) positive.
     * @exception IllegalArgumentException the proportion under test should be
     *                                     (strictly) positive.
     */

    public double pValue(double p0,
                         double count,
                         double sampleSize)
    {
        return pValue(p0, "equal", count, sampleSize);
    }

    /**
     * The p value for the two-sided alternative hypothesis as the null value is
     * equal to 0.5.
     * @param count the number of successes.
     * @param sampleSize the sample size (the number of trials).
     * @return the p value for the test.
     * @exception IllegalArgumentException the counts and sample size should
     *                                     be (strictly) positive.
     */

    public double pValue(double count,
                         double sampleSize)
    {
        return pValue(0.5, "equal", count, sampleSize);
    }

}
