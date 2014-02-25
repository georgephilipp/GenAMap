package javastat.inference.twosamples;

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
 * <p>Calculates the test statistic, confidence interval and p-value for a
 * two-sample proportions problem.</p>
 * <p> </p>
 * <br> Example:
 * <br> // Non-null constructor
 * <br> TwoSampProps testclass1 =
 *        new TwoSampProps(0.05, 0, "less", 36, 150, 30, 100);
 * <br> double testStatistic = testclass1.testStatistic;
 * <br> double pValue = testclass1.pValue;
 * <br> double lowerBound = testclass1.confidenceInterval[0];
 * <br> double upperBound = testclass1.confidenceInterval[1];
 * <br>
 * <br> // Null constructor
 * <br> TwoSampProps testclass2 = new TwoSampProps();
 * <br> double [] confidenceInterval =
 *        testclass2.confidenceInterval(0.05, 36, 150, 30, 100);
 * <br> testStatistic = testclass2.testStatistic(0, 36, 150, 30, 100);
 * <br> pValue = testclass2.pValue(0.5, "greater", 36, 150, 30, 100);
 * <br>
 * <br> // Non-null constructor
 * <br> Hashtable argument1=new Hashtable();
 * <br> argument1.put(ALPHA, 0.05);
 * <br> argument1.put(NULL_VALUE, 0);
 * <br> argument1.put(SIDE, "equal");
 * <br> StatisticalAnalysis testclass3 =
 * <br> &nbsp;&nbsp;&nbsp;
 *        new TwoSampProps(argument1, 36, 150, 30, 100).statisticalAnalysis;
 * <br> testStatistic = (Double) testclass3.output.get(TEST_STATISTIC);
 * <br> pValue = (Double) testclass3.output.get(PVALUE);
 * <br> confidenceInterval =
 *        (double[]) testclass3.output.get(CONFIDENCE_INTERVAL);
 * <br> lowerBound = confidenceInterval[0];
 * <br> upperBound = confidenceInterval[1];
 * <br>
 * <br> // Null constructor
 * <br> Hashtable argument2 = new Hashtable();
 * <br> TwoSampProps testclass4 = new TwoSampProps(argument2, null);
 * <br> argument2.put(ALPHA, 0.05);
 * <br> confidenceInterval =
 *        testclass4.confidenceInterval(argument2, 36, 150, 30, 100);
 * <br> argument2.put(NULL_VALUE, 0);
 * <br> testStatistic = testclass4.testStatistic(argument2, 36, 150, 30, 100);
 * <br> argument2.put(SIDE, "greater");
 * <br> pValue = testclass4.pValue(argument2, 36, 150, 30, 100);
 * <br>
 * <br> // Obtains the information about the output
 * <br> out.println(testclass3.output.toString());
 * <br> out.println(testclass4.output.toString());
 */

public class TwoSampProps extends StatisticalInferenceTemplate implements
        TwoSampInferenceInterface
{

    /**
     * The level of significance.
     */

    public double alpha;

    /**
     * The standard error of the sample proportion difference under the null
     * hypothesis.
     */

    public double proportionDifferenceSEH0;

    /**
     * The number of successes in population 1.
     */

    public double count1;

    /**
     * The number of successes in population 2.
     */

    public double count2;

    /**
     * The sample size from population 1.
     */

    public double sampleSize1;

    /**
     * The sample size from population 2.
     */

    public double sampleSize2;

    /**
     * The value of the proportion difference under test.
     */

    public double p12;

    /**
     * The estimate of the population proportion under H0: p1=p2.
     */

    public double proportionH0;

    /**
     * The object represents a two-sample proportions test.
     */

    public StatisticalAnalysis statisticalAnalysis;

    /**
     * Default TwoSampProps Constructor.
     */

    public TwoSampProps() {}

    /**
     * Constructs a two-sample z test given the input arguments and data.
     * @param argument the arguments with the following choices,
     * <br> ALPHA, NULL_VALUE, SIDE: complete list of arguments;
     * <br> NULL_VALUE, SIDE: default level of signifiance equal to 0.05;
     * <br> NULL_VALUE: default level of signifiance equal to 0.05 and two-sided
     *                  alternative hypothesis;
     * <br> empty argument: defalut level of significance equal to 0.05,
     *                      two-sided alternative hypothesis and the null value
     *                      equal to 0.5.
     * <br><br>
     * @param dataObject the numbers of successes and trials in both
     *                   populations.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the counts and sample sizes from
     *                                     both populations should be positive.
     * @exception IllegalArgumentException the absolute value of the difference
     *                                     under test should be not greater than
     *                                     1.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     */

    public TwoSampProps(Hashtable argument,
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
                dataObject.length == 4)
            {
                statisticalAnalysis = new TwoSampProps(
                        (Double) argument.get(ALPHA),
                        ((Number) argument.get(NULL_VALUE)).doubleValue(),
                        (String) argument.get(SIDE),
                        ((Number) dataObject[0]).doubleValue(),
                        ((Number) dataObject[1]).doubleValue(),
                        ((Number) dataObject[2]).doubleValue(),
                        ((Number) dataObject[3]).doubleValue());
            }
            else if (argument.get(NULL_VALUE) != null &&
                     argument.get(SIDE) != null &&
                     dataObject.length == 4)
            {
                statisticalAnalysis = new TwoSampProps(
                        ((Number) argument.get(NULL_VALUE)).doubleValue(),
                        (String) argument.get(SIDE),
                        ((Number) dataObject[0]).doubleValue(),
                        ((Number) dataObject[1]).doubleValue(),
                        ((Number) dataObject[2]).doubleValue(),
                        ((Number) dataObject[3]).doubleValue());
            }
            else if (argument.get(NULL_VALUE) != null &&
                     dataObject.length == 4)
            {
                statisticalAnalysis = new TwoSampProps(
                        ((Number) argument.get(NULL_VALUE)).doubleValue(),
                        ((Number) dataObject[0]).doubleValue(),
                        ((Number) dataObject[1]).doubleValue(),
                        ((Number) dataObject[2]).doubleValue(),
                        ((Number) dataObject[3]).doubleValue());
            }
            else
            {
                throw new IllegalArgumentException(
                        "Wrong input arguments or data.");
            }
        }
        else if (dataObject != null &&
                 dataObject.length == 4)
        {
            statisticalAnalysis = new TwoSampProps(
                    ((Number) dataObject[0]).doubleValue(),
                    ((Number) dataObject[1]).doubleValue(),
                    ((Number) dataObject[2]).doubleValue(),
                    ((Number) dataObject[3]).doubleValue());
        }
        else if (dataObject == null)
        {
            statisticalAnalysis = new TwoSampProps();
        }
        else
        {
            throw new IllegalArgumentException("Wrong input data.");
        }
    }

    /**
     * Constructs a two-sample z test given the input data, level of
     * significance, value of the proportion difference under test and
     * alternative hypothesis.
     * @param alpha the level of significance.
     * @param p12 the value of the proportion difference under test.
     * @param side the specification of the alternative hypothesis with the
     *             choices "greater", "less" or "equal" (or "two.sided").
     * @param count1 the number of successes in population 1.
     * @param count2 the number of successes in population 2.
     * @param sampleSize1 the sample size from population 1.
     * @param sampleSize2 the sample size from population 2.
     * @exception IllegalArgumentException the counts and sample sizes from
     *                                     both populations should be positive.
     * @exception IllegalArgumentException the absolute value of the difference
     *                                     under test should be not greater than
     *                                     1.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     */

    public TwoSampProps(double alpha,
                        double p12,
                        String side,
                        double count1,
                        double sampleSize1,
                        double count2,
                        double sampleSize2)
    {
        this.alpha = alpha;
        this.p12 = p12;
        this.side = side;
        this.count1 = count1;
        this.sampleSize1 = sampleSize1;
        this.count2 = count2;
        this.sampleSize2 = sampleSize2;
        confidenceInterval = confidenceInterval(alpha, count1, sampleSize1,
                                                count2, sampleSize2);
        testStatistic = testStatistic(p12, count1, sampleSize1, count2,
                                      sampleSize2);
        pValue = pValue(p12, side, count1, sampleSize1, count2, sampleSize2);
    }

    /**
     * Constructs a two-sample z test with a 0.05 level of significance given
     * the input data, value of the proportion difference under test and
     * alternative hypothesis.
     * @param p12 the value of the proportion difference under test.
     * @param side the specification of the alternative hypothesis with the
     *             choices "greater", "less" or "equal" (or "two.sided").
     * @param count1 the number of successes in population 1.
     * @param count2 the number of successes in population 2.
     * @param sampleSize1 the sample size from population 1.
     * @param sampleSize2 the sample size from population 2.
     * @exception IllegalArgumentException the counts and sample sizes from
     *                                     both populations should be positive.
     * @exception IllegalArgumentException the absolute value of the difference
     *                                     under test should be not greater than
     *                                     1.
     */

    public TwoSampProps(double p12,
                        String side,
                        double count1,
                        double sampleSize1,
                        double count2,
                        double sampleSize2)
    {
        this(0.05, p12, side, count1, sampleSize1, count2, sampleSize2);
    }

    /**
     * Constructs a two-tailed z test with a 0.05 level of significance
     * given the input data and value of the proportion difference under test.
     * @param p12 the value of the proportion difference under test.
     * @param count1 the number of successes in population 1.
     * @param count2 the number of successes in population 2.
     * @param sampleSize1 the sample size from population 1.
     * @param sampleSize2 the sample size from population 2.
     * @exception IllegalArgumentException the counts and sample sizes from
     *                                     both populations should be positive.
     * @exception IllegalArgumentException the absolute value of the difference
     *                                     under test should be not greater than
     *                                     1.
     */

    public TwoSampProps(double p12,
                        double count1,
                        double sampleSize1,
                        double count2,
                        double sampleSize2)
    {
        this(0.05, p12, "equal", count1, sampleSize1, count2, sampleSize2);
    }

    /**
     * Constructs a two-tailed z test with a 0.05 level of significance given
     * the input data and value of the proportion difference under test equal to
     * 0.
     * @param count1 the number of successes in population 1.
     * @param count2 the number of successes in population 2.
     * @param sampleSize1 the sample size from population 1.
     * @param sampleSize2 the sample size from population 2.
     * @exception IllegalArgumentException the counts and sample sizes from
     *                                     both populations should be positive.
     * @exception IllegalArgumentException the absolute value of the difference
     *                                     under test should be not greater than
     *                                     1.
     */

    public TwoSampProps(double count1,
                        double sampleSize1,
                        double count2,
                        double sampleSize2)
    {
        this(0.05, 0.0, "equal", count1, sampleSize1, count2, sampleSize2);
    }

    /**
     * Computes the point estimate given the input argument and data.
     * @param argument the empty argument.
     * @param dataObject the numbers of successes and trials in both
     *                   populations.
     * @return the point estimate.
     * @exception IllegalArgumentException the counts and sample sizes from
     *                                     both populations should be positive.
     */

    public Double pointEstimate(Hashtable argument,
                                Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;

        return pointEstimate(((Number) dataObject[0]).doubleValue(),
                             ((Number) dataObject[1]).doubleValue(),
                             ((Number) dataObject[2]).doubleValue(),
                             ((Number) dataObject[3]).doubleValue());
    }

    /**
     * Computes the point estimate, which is the sample proportion difference.
     * @param count1 the number of successes in population 1.
     * @param count2 the number of successes in population 2.
     * @param sampleSize1 the sample size from population 1.
     * @param sampleSize2 the sample size from population 2.
     * @return the point estimate.
     * @exception IllegalArgumentException the counts and sample sizes from
     *                                     both populations should be positive.
     */

    public double pointEstimate(double count1,
                                double sampleSize1,
                                double count2,
                                double sampleSize2)
    {
        this.count1 = count1;
        this.count2 = count2;
        this.sampleSize1 = sampleSize1;
        this.sampleSize2 = sampleSize2;
        pointEstimate = new BasicStatistics().proportionDifference(
                count1, sampleSize1, count2, sampleSize2);
        output.put(POINT_ESTIMATE, pointEstimate);

        return pointEstimate;
    }

    /**
     * Computes the standard error of the point estimate.
     * @param argument the argument with the following choices,
     * <br> NULL_VALUE: the null value for computing the standard error of the
     *                  point estimate under the null hypothesis;
     * <br> empty argument: computing the standard error of the point estimate.
     * <br><br>
     * @param dataObject the numbers of successes and trials in both
     *                   populations.
     * @return the standard error of the point estimate.
     * @exception IllegalArgumentException the counts and sample sizes from
     *                                     both populations should be positive.
     * @exception IllegalArgumentException the absolute value of the difference
     *                                     under test should be not greater than
     *                                     1.
     */

    public Double pointEstimateSE(Hashtable argument,
                                  Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (argument.get(NULL_VALUE) != null)
        {
            p12 = ((Number) argument.get(NULL_VALUE)).doubleValue();
            return pointEstimateSE(p12,
                                   ((Number) dataObject[0]).doubleValue(),
                                   ((Number) dataObject[1]).doubleValue(),
                                   ((Number) dataObject[2]).doubleValue(),
                                   ((Number) dataObject[3]).doubleValue());
        }
        else
        {
            return pointEstimateSE(((Number) dataObject[0]).doubleValue(),
                                   ((Number) dataObject[1]).doubleValue(),
                                   ((Number) dataObject[2]).doubleValue(),
                                   ((Number) dataObject[3]).doubleValue());
        }
    }

    /**
     * Computes the standard error of the point estimate.
     * @param count1 the number of successes in population 1.
     * @param count2 the number of successes in population 2.
     * @param sampleSize1 the sample size from population 1.
     * @param sampleSize2 the sample size from population 2.
     * @return the standard error of the point estimate.
     * @exception IllegalArgumentException the counts and sample sizes from
     *                                     both populations should be positive.
     */

    public double pointEstimateSE(double count1,
                                  double sampleSize1,
                                  double count2,
                                  double sampleSize2)
    {
        this.count1 = count1;
        this.count2 = count2;
        this.sampleSize1 = sampleSize1;
        this.sampleSize2 = sampleSize2;
        pointEstimateSE = new BasicStatistics().proportionDifferenceSE(
                count1, sampleSize1, count2, sampleSize2);
        output.put(POINT_ESTIMATE_SE, pointEstimateSE);

        return pointEstimateSE;

    }

    /**
     * Computes the standard error of the point estimate under the null
     * hypothesis.
     * @param p12 the value of the proportion under test.
     * @param count1 the number of successes in population 1.
     * @param count2 the number of successes in population 2.
     * @param sampleSize1 the sample size from population 1.
     * @param sampleSize2 the sample size from population 2.
     * @return the standard error of the point estimate.
     * @exception IllegalArgumentException the counts and sample sizes from
     *                                     both populations should be positive.
     * @exception IllegalArgumentException the absolute value of the difference
     *                                     under test should be not greater than
     *                                     1.
     */

    public double pointEstimateSE(double p12,
                                  double count1,
                                  double sampleSize1,
                                  double count2,
                                  double sampleSize2)
    {
        this.p12 = p12;
        this.count1 = count1;
        this.count2 = count2;
        this.sampleSize1 = sampleSize1;
        this.sampleSize2 = sampleSize2;
        if ((p12 < -1.0) || (p12 > 1.0))
        {
            throw new IllegalArgumentException(
                    "The absolute value of the difference under test " +
                    "should be not greater than 1.");
        }
        if (p12 == 0)
        {
            proportionH0 = (count1 + count2) / (sampleSize1 + sampleSize2);
            proportionDifferenceSEH0 = pointEstimateSE =
                    Math.sqrt((proportionH0 * (1 - proportionH0)) *
                              (1.0 / sampleSize1 + 1.0 / sampleSize2));
            output.put(PROPORTION_DIFFERENCE_SE_H0, proportionDifferenceSEH0);
        }
        else
        {
            pointEstimateSE = new BasicStatistics().proportionDifferenceSE(
                    count1, sampleSize1, count2, sampleSize2);
        }
        output.put(PROPORTION_H0, proportionH0);
        output.put(POINT_ESTIMATE_SE, pointEstimateSE);

        return pointEstimateSE;
    }

    /**
     * The confidence interval.
     * @param argument the argument with the following choices,
     * <br> ALPHA: the level of significance;
     * <br> empty argument: defalut level of significance equal to 0.05.
     * <br><br>
     * @param dataObject the numbers of successes and trials in both
     *                   populations.
     * @return the confidence interval,
     * <br>    confidenceInterval[0]: the lower bound;
     * <br>    confidenceInterval[1]: the upper bound.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the counts and sample sizes from
     *                                     both populations should be positive.
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
            dataObject.length == 4)
        {
            confidenceInterval = confidenceInterval(
                    (Double) argument.get(ALPHA),
                    ((Number) dataObject[0]).doubleValue(),
                    ((Number) dataObject[1]).doubleValue(),
                    ((Number) dataObject[2]).doubleValue(),
                    ((Number) dataObject[3]).doubleValue());
        }
        else if (dataObject != null &&
                 dataObject.length == 4)
        {
            confidenceInterval = confidenceInterval(
                    ((Number) dataObject[0]).doubleValue(),
                    ((Number) dataObject[1]).doubleValue(),
                    ((Number) dataObject[2]).doubleValue(),
                    ((Number) dataObject[3]).doubleValue());
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
     * @param count1 the number of successes in population 1.
     * @param count2 the number of successes in population 2.
     * @param sampleSize1 the sample size from population 1.
     * @param sampleSize2 the sample size from population 2.
     * @return the confidence interval,
     * <br>    confidenceInterval[0]: the lower bound;
     * <br>    confidenceInterval[1]: the upper bound.
     * @exception IllegalArgumentException the counts and sample sizes from
     *                                     both populations should be positive.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     */

    public double[] confidenceInterval(double alpha,
                                       double count1,
                                       double sampleSize1,
                                       double count2,
                                       double sampleSize2)
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

        return confidenceInterval = (double[])super.confidenceInterval(
                argument, count1, sampleSize1, count2, sampleSize2);
    }

    /**
     * The 95% confidence interval.
     * @param count1 the number of successes in population 1.
     * @param count2 the number of successes in population 2.
     * @param sampleSize1 the sample size from population 1.
     * @param sampleSize2 the sample size from population 2.
     * @return the confidence interval,
     * <br>    confidenceInterval[0]: the lower bound;
     * <br>    confidenceInterval[1]: the upper bound.
     * @exception IllegalArgumentException the counts and sample sizes from
     *                                     both populations should be positive.
     */

    public double[] confidenceInterval(double count1,
                                       double sampleSize1,
                                       double count2,
                                       double sampleSize2)
    {
        return confidenceInterval(0.05, count1, sampleSize1, count2,
                                  sampleSize2);
    }

    /**
     * The z statistic.
     * @param argument the argument with the following choices,
     * <br> NULL_VALUE: the null value;
     * <br> empty argument: the null value equal to 0.
     * <br><br>
     * @param dataObject the numbers of successes and trials in both
     *                   populations.
     * @return the value of the test statistic.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the counts and sample sizes from
     *                                     both populations should be positive.
     * @exception IllegalArgumentException the absolute value of the difference
     *                                     under test should be not greater than
     *                                     1.
     */

    public Double testStatistic(Hashtable argument,
                                Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (argument.get(NULL_VALUE) != null &&
            dataObject != null &&
            dataObject.length == 4)
        {
            testStatistic = testStatistic(
                    ((Number) argument.get(NULL_VALUE)).doubleValue(),
                    ((Number) dataObject[0]).doubleValue(),
                    ((Number) dataObject[1]).doubleValue(),
                    ((Number) dataObject[2]).doubleValue(),
                    ((Number) dataObject[3]).doubleValue());
        }
        else if (dataObject != null &&
                 dataObject.length == 4)
        {
            testStatistic = testStatistic(
                    ((Number) dataObject[0]).doubleValue(),
                    ((Number) dataObject[1]).doubleValue(),
                    ((Number) dataObject[2]).doubleValue(),
                    ((Number) dataObject[3]).doubleValue());
        }
        else
        {
            throw new IllegalArgumentException("Wrong input arguments or " +
                                               "data.");
        }

        return testStatistic;
    }

    /**
     * The z statistic.
     * @param p12 the value of the proportion difference under test.
     * @param count1 the number of successes in population 1.
     * @param count2 the number of successes in population 2.
     * @param sampleSize1 the sample size from population 1.
     * @param sampleSize2 the sample size from population 2.
     * @return the value of the z statistic.
     * @exception IllegalArgumentException the counts and sample sizes from
     *                                     both populations should be positive.
     * @exception IllegalArgumentException the absolute value of the difference
     *                                     under test should be not greater than
     *                                     1.
     */

    public double testStatistic(double p12,
                                double count1,
                                double sampleSize1,
                                double count2,
                                double sampleSize2)
    {
        this.p12 = p12;
        argument.put(NULL_VALUE, p12);

        return testStatistic = (Double)super.testStatistic(
                argument, count1, sampleSize1, count2, sampleSize2);
    }

    /**
     * The z statistic with the null value equal to 0.
     * @param count1 the number of successes in population 1.
     * @param count2 the number of successes in population 2.
     * @param sampleSize1 the sample size from population 1.
     * @param sampleSize2 the sample size from population 2.
     * @return the value of the test statistic.
     * @exception IllegalArgumentException the counts and sample sizes from
     *                                     both populations should be positive.
     */

    public double testStatistic(double count1,
                                double sampleSize1,
                                double count2,
                                double sampleSize2)
    {
        return testStatistic(0.0, count1, sampleSize1, count2, sampleSize2);
    }

    /**
     * The p value.
     * @param argument the arguments with the following choices,
     * <br> NULL_VALUE, SIDE: complete list of arguments;
     * <br> NULL_VALUE: the two-sided alternative hypothesis;
     * <br> empty argument: the two-sided alternative hypothesis and null value
     *                      equal to 0.
     * <br><br>
     * @param dataObject the numbers of successes and trials in both
     *                   populations.
     * @return the p value for the test.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the counts and sample sizes from both
     *                                     populations should be positive.
     * @exception IllegalArgumentException the absolute value of the difference
     *                                     under test should be not greater than
     *                                     1.
     */

    public Double pValue(Hashtable argument,
                         Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (argument.get(NULL_VALUE) != null &&
            argument.get(SIDE) != null &&
            dataObject != null &&
            dataObject.length == 4)
        {
            pValue = pValue(((Number) argument.get(NULL_VALUE)).doubleValue(),
                            (String) argument.get(SIDE),
                            ((Number) dataObject[0]).doubleValue(),
                            ((Number) dataObject[1]).doubleValue(),
                            ((Number) dataObject[2]).doubleValue(),
                            ((Number) dataObject[3]).doubleValue());
        }
        else if (argument.get(NULL_VALUE) != null &&
                 dataObject != null &&
                 dataObject.length == 4)
        {
            pValue = pValue(((Number) argument.get(NULL_VALUE)).doubleValue(),
                            ((Number) dataObject[0]).doubleValue(),
                            ((Number) dataObject[1]).doubleValue(),
                            ((Number) dataObject[2]).doubleValue(),
                            ((Number) dataObject[3]).doubleValue());
        }
        else if (dataObject != null &&
                 dataObject.length == 4)
        {
            pValue = pValue(((Number) dataObject[0]).doubleValue(),
                            ((Number) dataObject[1]).doubleValue(),
                            ((Number) dataObject[2]).doubleValue(),
                            ((Number) dataObject[3]).doubleValue());
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
     * @param p12 the value of the proportion difference under test.
     * @param side the specification of the alternative hypothesis with the
     *             choices "greater", "less" or "equal" (or "two.sided").
     * @param count1 the number of successes in population 1.
     * @param count2 the number of successes in population 2.
     * @param sampleSize1 the sample size from population 1.
     * @param sampleSize2 the sample size from population 2.
     * @return the p value for the test.
     * @exception IllegalArgumentException the counts and sample sizes from both
     *                                     populations should be positive.
     * @exception IllegalArgumentException the absolute value of the difference
     *                                     under test should be not greater than
     *                                     1.
     */

    public double pValue(double p12,
                         String side,
                         double count1,
                         double sampleSize1,
                         double count2,
                         double sampleSize2)
    {
        this.side = side;
        testStatistic = testStatistic(p12, count1, sampleSize1, count2,
                                      sampleSize2);
        argument.put(SIDE, side);
        argument.put(CDF, new NormalDistribution().cumulative(testStatistic));

        return pValue = (Double)super.pValue(argument);
    }

    /**
     * The p value for the two-sided alternative hypothesis.
     * @param p12 the value of the proportion difference under test.
     * @param count1 the number of successes in population 1.
     * @param count2 the number of successes in population 2.
     * @param sampleSize1 the sample size from population 1.
     * @param sampleSize2 the sample size from population 2.
     * @return the p value for the test.
     * @exception IllegalArgumentException the counts and sample sizes from both
     *                                     populations should be positive.
     * @exception IllegalArgumentException the absolute value of the difference
     *                                     under test should be not greater than
     *                                     1.
     */

    public double pValue(double p12,
                         double count1,
                         double sampleSize1,
                         double count2,
                         double sampleSize2)
    {
        return pValue(p12, "equal", count1, sampleSize1, count2, sampleSize2);
    }

    /**
     * The p value for the two-sided alternative hypothesis as the null value is
     * equal to 0.
     * @param count1 the number of successes in population 1.
     * @param count2 the number of successes in population 2.
     * @param sampleSize1 the sample size from population 1.
     * @param sampleSize2 the sample size from population 2.
     * @return the p value for the test.
     * @exception IllegalArgumentException the counts and sample sizes from both
     *                                     populations should be positive.
     * @exception IllegalArgumentException the absolute value of the difference
     *                                     under test should be not greater than
     *                                     1.
     */

    public double pValue(double count1,
                         double sampleSize1,
                         double count2,
                         double sampleSize2)
    {
        return pValue(0.0, "equal", count1, sampleSize1, count2, sampleSize2);
    }

}
