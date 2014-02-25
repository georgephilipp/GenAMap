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
 * <p>Calculates the test statistic, confidence interval, and p-value for a
 * two-sample means problem.</p>
 * <p> </p>
 */

public class TwoSampMeansTest extends StatisticalInferenceTemplate
{

    /**
     * The level of significance.
     */

    public double alpha;

    /**
     * The value of the mean difference under test.
     */

    public double u12;

    /**
     * The test of interest.
     */

    public String testType;

    /**
     * The input data from population 1.
     */

    public double[] data1;

    /**
     * The input data from population 2.
     */

    public double[] data2;

    /**
     * The object represents a two-sample means test.
     */

    public StatisticalAnalysis statisticalAnalysis;

    /**
     * Default TwoSampMeansTest constructor.
     */

    public TwoSampMeansTest() {}

    /**
     * Constructs a two-sample means test given the input arguments and data.
     * @param argument the arguments with the following choices,
     * <br> TEST_TYPE, ALPHA, NULL_VALUE, SIDE: complete list of arguments;
     * <br> TEST_TYPE, NULL_VALUE, SIDE: default level of signifiance equal to
     *                                   0.05;
     * <br> TEST_TYPE, NULL_VALUE: default level of signifiance equal to 0.05
     *                             and two-sided alternative hypothesis;
     * <br> TEST_TYPE: default level of signifiance equal to 0.05, two-sided
     *                 alternative hypothesis and the null value equal to 0;
     * <br> empty argument: defalut level of significance equal to 0.05,
     *                      two-sided alternative hypothesis, the null value
     *                      equal to 0 and two-sample z test.
     * <br><br>
     * @param dataObject the input data from both populations.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     */

    public TwoSampMeansTest(Hashtable argument,
                            Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (argument.size() > 0 &&
            dataObject != null)
        {
            if (argument.get(TEST_TYPE) != null &&
                argument.get(ALPHA) != null &&
                argument.get(NULL_VALUE) != null &&
                argument.get(SIDE) != null &&
                dataObject.length == 2)
            {
                statisticalAnalysis = new TwoSampMeansTest(
                        (String) argument.get(TEST_TYPE),
                        (Double) argument.get(ALPHA),
                        ((Number) argument.get(NULL_VALUE)).doubleValue(),
                        (String) argument.get(SIDE),
                        (double[]) dataObject[0], (double[]) dataObject[1]);
            }
            else if (argument.get(TEST_TYPE) != null &&
                     argument.get(NULL_VALUE) != null &&
                     argument.get(SIDE) != null &&
                     dataObject.length == 2)
            {
                statisticalAnalysis = new TwoSampMeansTest(
                        (String) argument.get(TEST_TYPE),
                        ((Number) argument.get(NULL_VALUE)).doubleValue(),
                        (String) argument.get(SIDE),
                        (double[]) dataObject[0], (double[]) dataObject[1]);
            }
            else if (argument.get(TEST_TYPE) != null &&
                     argument.get(NULL_VALUE) != null &&
                     dataObject.length == 2)
            {
                statisticalAnalysis = new TwoSampMeansTest(
                        (String) argument.get(TEST_TYPE),
                        ((Number) argument.get(NULL_VALUE)).doubleValue(),
                        (double[]) dataObject[0], (double[]) dataObject[1]);
            }
            else if (argument.get(TEST_TYPE) != null &&
                     dataObject.length == 2)
            {
                statisticalAnalysis = new TwoSampMeansTest(
                        (String) argument.get(TEST_TYPE),
                        (double[]) dataObject[0], (double[]) dataObject[1]);
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
            statisticalAnalysis = new TwoSampMeansTest(
                    (double[]) dataObject[0], (double[]) dataObject[1]);
        }
        else if (dataObject == null)
        {
            statisticalAnalysis = new TwoSampMeansTest();
        }
        else
        {
            throw new IllegalArgumentException("Wrong input data.");
        }
    }

    /**
     * Constructs a two-sample means test given the input data, level of
     * significance, value of the mean difference under test and alternative
     * hypothesis.
     * @param testType the test of interest with the choices "Z" or "T".
     * @param alpha the level of significance.
     * @param u12 the value of the mean difference under test.
     * @param side the specification of the alternative hypothesis with the
     *             choices "greater", "less" or "equal" (or "two.sided").
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     */

    public TwoSampMeansTest(String testType,
                            double alpha,
                            double u12,
                            String side,
                            double[] data1,
                            double[] data2)
    {
        this.testType = testType;
        this.alpha = alpha;
        this.u12 = u12;
        this.side = side;
        this.data1 = data1;
        this.data2 = data2;
        confidenceInterval = confidenceInterval(testType, alpha, data1, data2);
        testStatistic = testStatistic(testType, u12, data1, data2);
        pValue = pValue(testType, u12, side, data1, data2);
    }

    /**
     * Constructs a two-sample means test with a 0.05 level of significance
     * given the input data, value of the mean difference under test and
     * alternative hypothesis.
     * @param testType the test of interest with the choices "Z" or "T".
     * @param u12 the value of the mean difference under test.
     * @param side the specification of the alternative hypothesis with the
     *             choices "greater", "less" or "equal" (or "two.sided").
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public TwoSampMeansTest(String testType,
                            double u12,
                            String side,
                            double[] data1,
                            double[] data2)
    {
        this(testType, 0.05, u12, side, data1, data2);
    }

    /**
     * Constructs a two-tailed test with a 0.05 level of significance given
     * the input data and value of the mean difference under test.
     * @param testType the test of interest with the choices "Z" or "T".
     * @param u12 the value of the mean difference under test.
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public TwoSampMeansTest(String testType,
                            double u12,
                            double[] data1,
                            double[] data2)
    {
        this(testType, 0.05, u12, "equal", data1, data2);
    }

    /**
     * Constructs a two-tailed test with a 0.05 level of significance given
     * the input data and value of the mean difference under test equal to 0.
     * @param testType the test of interest with the choices "Z" or "T".
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public TwoSampMeansTest(String testType,
                            double[] data1,
                            double[] data2)
    {
        this(testType, 0.05, 0.0, "equal", data1, data2);
    }

    /**
     * Constructs a two-tailed z test with a 0.05 level of significance given
     * the input data and value of the mean difference under test equal to 0.
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public TwoSampMeansTest(double[] data1,
                            double[] data2)
    {
        this("Z", 0.05, 0.0, "equal", data1, data2);
    }

    /**
     * Computes the point estimate given the input argument and data.
     * @param argument the empty argument.
     * @param dataObject the input data from both populations.
     * @return the point estimate.
     */

    public Double pointEstimate(Hashtable argument,
                                Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;

        return pointEstimate((double[]) dataObject[0],
                             (double[]) dataObject[1]);
    }

    /**
     * Computes the point estimate, which is the sample mean difference.
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @return the standard error of the point estimate.
     */

    public double pointEstimate(double[] data1,
                                double[] data2)
    {
        this.data1 = data1;
        this.data2 = data2;
        pointEstimate = new BasicStatistics().meanDifference(data1, data2);
        output.put(POINT_ESTIMATE, pointEstimate);

        return pointEstimate;
    }

    /**
     * Computes the standard error of the point estimate.
     * @param testType the test of interest with the choices "Z" and "T".
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @return the standard error of the point estimate.
     */

    public double pointEstimateSE(String testType,
                                  double[] data1,
                                  double[] data2)
    {
        this.testType = testType;
        this.data1 = data1;
        this.data2 = data2;
        argument.put(TEST_TYPE, testType);
        pointEstimateSE = new BasicStatistics().meanDifferenceSE(data1, data2,
                testType);
        output.put(POINT_ESTIMATE_SE, pointEstimateSE);

        return pointEstimateSE;
    }

    /**
     * Computes the standard error of the point estimate.
     * @param argument the empty argument.
     * @param dataObject the input data from both populations.
     * @return the standard error of the point estimate.
     */

    public Double pointEstimateSE(Hashtable argument,
                                  Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;

        return pointEstimateSE((String) argument.get(TEST_TYPE),
                               (double[]) dataObject[0],
                               (double[]) dataObject[1]);
    }

    /**
     * The confidence interval.
     * @param argument the arguments with the following choices,
     * <br> TEST_TYPE, ALPHA: complete list of arguments;
     * <br> TEST_TYPE: default level of signifiance equal to 0.05;
     * <br> empty argument: defalut level of significance equal to 0.05 and
     *                      two-sample z test.
     * <br><br>
     * @param dataObject the input data from both populations.
     * @return the confidence interval,
     * <br>    confidenceInterval[0]: the lower bound;
     * <br>    confidenceInterval[1]: the upper bound.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     */

    public double[] confidenceInterval(Hashtable argument,
                                       Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (argument.get(TEST_TYPE) != null &&
            argument.get(ALPHA) != null &&
            dataObject != null &&
            dataObject.length == 2)
        {
            confidenceInterval = confidenceInterval((String) argument.get(
                    TEST_TYPE),
                    (Double) argument.get(ALPHA),
                    (double[]) dataObject[0], (double[]) dataObject[1]);
        }
        else if (argument.get(TEST_TYPE) != null &&
                 dataObject != null &&
                 dataObject.length == 2)
        {
            confidenceInterval = confidenceInterval((String) argument.get(
                    TEST_TYPE),
                    (double[]) dataObject[0], (double[]) dataObject[1]);
        }
        else if (dataObject != null &&
                 dataObject.length == 2)
        {
            confidenceInterval = confidenceInterval((double[]) dataObject[0],
                    (double[]) dataObject[1]);
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
     * @param testType the test of interest with the choices "T" or "Z".
     * @param alpha the level of significance.
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @return the confidence interval,
     * <br>    confidenceInterval[0]: the lower bound;
     * <br>    confidenceInterval[1]: the upper bound.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     */

    public double[] confidenceInterval(String testType,
                                       double alpha,
                                       double[] data1,
                                       double[] data2)
    {
        if ((alpha <= 0.0) || (alpha > 1))
        {
            throw new IllegalArgumentException(
                    "The level of significance should be (strictly) positive " +
                    "and not greater than 1.");
        }
        if (data1.length == 0 || data2.length == 0)
        {
            throw new IllegalArgumentException(
                    "The length of the input data should not be 0.");
        }
        this.testType = testType;
        this.alpha = alpha;
        argument.put(TEST_TYPE, testType);
        argument.put(ALPHA, alpha);
        if (testType.equalsIgnoreCase("T") ||
            testType.equalsIgnoreCase("T Test"))
        {
            argument.put(CRITICAL_VALUE,
                         new TDistribution((data1.length + data2.length - 2)).
                         inverse((1 - alpha / 2)));
            return (double[])super.confidenceInterval(argument, data1, data2);
        }
        else
        {
            argument.put(CRITICAL_VALUE,
                         new NormalDistribution().inverse((1 - alpha / 2)));
            return (double[])super.confidenceInterval(argument, data1, data2);
        }
    }

    /**
     * The 95% confidence interval.
     * @param testType the test of interest with the choices "T" or "Z".
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @return the confidence interval,
     * <br>    confidenceInterval[0]: the lower bound;
     * <br>    confidenceInterval[1]: the upper bound.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double[] confidenceInterval(String testType,
                                       double[] data1,
                                       double[] data2)
    {
        return confidenceInterval(testType, 0.05, data1, data2);
    }

    /**
     * The 95% confidence interval based on the z statistic.
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @return the confidence interval,
     * <br>    confidenceInterval[0]: the lower bound;
     * <br>    confidenceInterval[1]: the upper bound.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double[] confidenceInterval(double[] data1,
                                       double[] data2)
    {
        return confidenceInterval("Z", 0.05, data1, data2);
    }

    /**
     * The test statistic given the input argument and data.
     * @param argument the argument with the following choices,
     * <br> NULL_VALUE: the null value;
     * <br> empty argument: the null value equal to 0.
     * <br><br>
     * @param dataObject the input data from both populations.
     * @return the value of the test statistic.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public Double testStatistic(Hashtable argument,
                                Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (argument.get(TEST_TYPE) != null &&
            argument.get(NULL_VALUE) != null &&
            dataObject != null &&
            dataObject.length == 2)
        {
            testStatistic = testStatistic((String) argument.get(TEST_TYPE),
                                          ((Number) argument.get(NULL_VALUE)).
                                          doubleValue(),
                                          (double[]) dataObject[0],
                                          (double[]) dataObject[1]);
        }
        else if (argument.get(TEST_TYPE) != null &&
                 dataObject != null &&
                 dataObject.length == 2)
        {
            testStatistic = testStatistic((String) argument.get(TEST_TYPE),
                                          (double[]) dataObject[0],
                                          (double[]) dataObject[1]);
        }
        else if (dataObject != null &&
                 dataObject.length == 2)
        {
            testStatistic = testStatistic((double[]) dataObject[0],
                                          (double[]) dataObject[1]);
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
     * @param testType the test of interest with the choices "T" or "Z".
     * @param u12 the value of the mean difference under test.
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @return the value of the test statistic.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double testStatistic(String testType,
                                double u12,
                                double[] data1,
                                double[] data2)
    {
        this.testType = testType;
        this.u12 = u12;
        argument.put(TEST_TYPE, testType);
        argument.put(NULL_VALUE, u12);

        return (Double)super.testStatistic(argument, data1, data2);
    }

    /**
     * The test statistic with the null value equal to 0.
     * @param testType the test of interest with the choices "T" or "Z".
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @return the value of the test statistic.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double testStatistic(String testType,
                                double[] data1,
                                double[] data2)
    {
        return testStatistic(testType, 0.0, data1, data2);
    }

    /**
     * The z statistic with the null value equal to 0.
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @return the value of the test statistic.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double testStatistic(double[] data1,
                                double[] data2)
    {
        return testStatistic("Z", 0.0, data1, data2);
    }

    /**
     * The p value.
     * @param argument the arguments with the following choices,
     * <br> TEST_TYPE, NULL_VALUE, SIDE: complete list of arguments;
     * <br> TEST_TYPE, NULL_VALUE: the two-sided alternative hypothesis;
     * <br> TEST_TYPE: the two-sided alternative hypothesis and null value equal
     *                 to 0;
     * <br> empty argument: the two-sided alternative hypothesis, null value
     *                      equal to 0 and two-sample z test.
     * <br><br>
     * @param dataObject the input data from both populations.
     * @return the p value for the test.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public Double pValue(Hashtable argument,
                         Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (argument.get(TEST_TYPE) != null &&
            argument.get(NULL_VALUE) != null &&
            argument.get(SIDE) != null &&
            dataObject != null &&
            dataObject.length == 2)
        {
            pValue = pValue((String) argument.get(TEST_TYPE),
                            ((Number) argument.get(NULL_VALUE)).doubleValue(),
                            (String) argument.get(SIDE),
                            (double[]) dataObject[0], (double[]) dataObject[1]);
        }
        else if (argument.get(TEST_TYPE) != null &&
                 argument.get(NULL_VALUE) != null &&
                   dataObject != null &&
                   dataObject.length == 2)
        {
            pValue = pValue((String) argument.get(TEST_TYPE),
                            ((Number) argument.get(NULL_VALUE)).doubleValue(),
                            (double[]) dataObject[0], (double[]) dataObject[1]);
        }
        else if (argument.get(TEST_TYPE) != null &&
                 dataObject != null &&
                 dataObject.length == 2)
        {
            pValue = pValue((String) argument.get(TEST_TYPE),
                            (double[]) dataObject[0], (double[]) dataObject[1]);
        }
        else if (dataObject != null &&
                 dataObject.length == 2)
        {
            pValue = pValue((double[]) dataObject[0], (double[]) dataObject[1]);
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
     * @param testType the test of interest with the choices "T" or "Z".
     * @param u12 the value of the mean difference under test.
     * @param side the specification of the alternative hypothesis with the
     *             choices "greater", "less" or "equal" (or "two.sided").
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @return the p value for the test.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double pValue(String testType,
                         double u12,
                         String side,
                         double[] data1,
                         double[] data2)
    {
        this.u12 = u12;
        this.side = side;
        testStatistic = testStatistic(testType, u12, data1, data2);
        argument.put(SIDE, side);
        if (testType.equalsIgnoreCase("T") ||
            testType.equalsIgnoreCase("T Test"))
        {
            argument.put(CDF,
                         new TDistribution((data1.length + data2.length - 2)).
                         cumulative(testStatistic));
            return super.pValue(argument);
        }
        else
        {
            argument.put(CDF,
                         new NormalDistribution().cumulative(testStatistic));
            return super.pValue(argument);
        }
    }

    /**
     * The p value for the two-sided alternative hypothesis.
     * @param testType the test of interest with the choices "T" or "Z".
     * @param u12 the value of the mean difference under test.
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @return the p value for the test.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double pValue(String testType,
                         double u12,
                         double[] data1,
                         double[] data2)
    {
        return pValue(testType, u12, "equal", data1, data2);
    }

    /**
     * The p value for the two-sided alternative hypothesis as the null value is
     * equal to 0.
     * @param testType the test of interest with the choices "T" or "Z".
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @return the p value for the test.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double pValue(String testType,
                         double[] data1,
                         double[] data2)
    {
        return pValue(testType, 0.0, "equal", data1, data2);
    }

    /**
     * The p value for the z test with the two-sided alternative hypothesis as
     * the null value is equal to 0.
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @return the p value for the test.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double pValue(double[] data1,
                         double[] data2)
    {
        return pValue("Z", 0.0, "equal", data1, data2);
    }

}
