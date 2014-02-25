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
 * sample mean problem.</p>
 * <p> </p>
 */

public class OneSampMeanTest extends StatisticalInferenceTemplate
{

    /**
     * The level of significance.
     */

    public double alpha;

    /**
     * The value of the mean under test.
     */

    public double u0;

    /**
     * The test of interest.
     */

    public String testType;

    /**
     * The input data.
     */

    public double[] data;

    /**
     * The object represents a one-sample mean test.
     */

    public StatisticalAnalysis statisticalAnalysis;

    /**
     * Constructs a one sample mean test.
     */

    public OneSampMeanTest() {}

    /**
     * Constructs a one sample mean test given the input arguments and data.
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
     *                      equal to 0 and one-sample z test.
     * <br><br>
     * @param dataObject the input data.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public OneSampMeanTest(Hashtable argument,
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
                dataObject.length == 1)
            {
                statisticalAnalysis = new OneSampMeanTest(
                        (String) argument.get(TEST_TYPE),
                        (Double) argument.get(ALPHA),
                        ((Number) argument.get(NULL_VALUE)).doubleValue(),
                        (String) argument.get(SIDE),
                        (double[]) dataObject[0]);
            }
            else if (argument.get(TEST_TYPE) != null &&
                     argument.get(NULL_VALUE) != null &&
                     argument.get(SIDE) != null && dataObject.length == 1)
            {
                statisticalAnalysis = new OneSampMeanTest(
                        (String) argument.get(TEST_TYPE),
                        ((Number) argument.get(NULL_VALUE)).doubleValue(),
                        (String) argument.get(SIDE),
                        (double[]) dataObject[0]);
            }
            else if (argument.get(TEST_TYPE) != null &&
                       argument.get(NULL_VALUE) != null &&
                       dataObject.length == 1)
            {
                statisticalAnalysis = new OneSampMeanTest(
                        (String) argument.get(TEST_TYPE),
                        ((Number) argument.get(NULL_VALUE)).doubleValue(),
                        (double[]) dataObject[0]);
            }
            else if (argument.get(TEST_TYPE) != null &&
                     dataObject.length == 1)
            {
                statisticalAnalysis = new OneSampMeanTest(
                        (String) argument.get(TEST_TYPE),
                        (double[]) dataObject[0]);
            }
            else
            {
                throw new IllegalArgumentException(
                        "Wrong input arguments or data.");
            }
        }
        else if (dataObject != null &&
                 dataObject.length == 1)
        {
            statisticalAnalysis = new OneSampMeanTest((double[]) dataObject[0]);
        }
        else if (dataObject == null)
        {
            statisticalAnalysis = new OneSampMeanTest();
        }
        else
        {
            throw new IllegalArgumentException("Wrong input data.");
        }
    }

    /**
     * Constructs a one sample mean test given the input data, level of
     * significance, value of the mean under test and alternative hypothesis.
     * @param testType the test of interest with the choices "T" or "Z".
     * @param alpha the level of significance.
     * @param u0 the value of the mean under test.
     * @param side the specification of the alternative hypothesis with the
     *             choices "greater", "less" or "equal" (or "two.sided").
     * @param data the input data.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public OneSampMeanTest(String testType,
                           double alpha,
                           double u0,
                           String side,
                           double[] data)
    {
        this.testType = testType;
        this.alpha = alpha;
        this.u0 = u0;
        this.side = side;
        confidenceInterval = confidenceInterval(testType, alpha, data);
        testStatistic = testStatistic(u0, data);
        pValue = pValue(testType, u0, side, data);
    }

    /**
     * Constructs a one sample mean test given the input data, level of
     * significance equal to 0.05, value of the mean under test and alternative
     * hypothesis.
     * @param testType the test of interest with the choices "T" or "Z".
     * @param u0 the value of the mean under test.
     * @param side the specification of the alternative hypothesis with the
     *             choices "greater", "less" or "equal" (or "two.sided").
     * @param data the input data.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public OneSampMeanTest(String testType,
                           double u0,
                           String side,
                           double[] data)
    {
        this(testType, 0.05, u0, side, data);
    }

    /**
     * Constructs a one sample mean test given the input data, level of
     * significance equal to 0.05, value of the mean under test and two-sided
     * alternative hypothesis.
     * @param testType the test of interest with the choices "T" or "Z".
     * @param u0 the value of the mean under test.
     * @param data the input data.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public OneSampMeanTest(String testType,
                           double u0,
                           double[] data)
    {
        this(testType, 0.05, u0, "equal", data);
    }

    /**
     * Constructs a one sample mean test given the input data, level of
     * significance equal to 0.05, value of the mean under test equal to 0 and
     * two-sided alternative hypothesis.
     * @param testType the test of interest with the choices "T" or "Z".
     * @param data the input data.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public OneSampMeanTest(String testType,
                           double[] data)
    {
        this(testType, 0.05, 0.0, "equal", data);
    }

    /**
     * Constructs a one sample z test given the input data, level of
     * significance equal to 0.05, value of the mean under test equal to 0 and
     * two-sided alternative hypothesis.
     * @param data the input data.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public OneSampMeanTest(double[] data)
    {
        this("Z", 0.05, 0.0, "equal", data);
    }

    /**
     * Computes the point estimate, which is the sample mean.
     * @param data the input data.
     * @return the point estimate.
     */

    public double pointEstimate(double[] data)
    {
        this.data = data;
        pointEstimate = new BasicStatistics().mean(data);
        output.put(POINT_ESTIMATE, pointEstimate);

        return pointEstimate;
    }

    /**
     * Computes the point estimate given the input argument and data.
     * @param argument the empty argument.
     * @param dataObject the input data.
     * @return the point estimate.
     */

    public Double pointEstimate(Hashtable argument,
                                Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;

        return pointEstimate((double[]) dataObject[0]);
    }

    /**
     * Computes the standard error of the point estimate.
     * @param data the input data.
     * @return the standard error of the point estimate.
     */

    public double pointEstimateSE(double[] data)
    {
        this.data = data;
        pointEstimateSE = new BasicStatistics().meanSE(data);
        output.put(POINT_ESTIMATE_SE, pointEstimateSE);

        return pointEstimateSE;
    }

    /**
     * Computes the standard error of the point estimate.
     * @param argument the empty argument.
     * @param dataObject the input data.
     * @return the standard error of the point estimate.
     */

    public Double pointEstimateSE(Hashtable argument,
                                  Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;

        return pointEstimateSE((double[]) dataObject[0]);
    }

    /**
     * The confidence interval.
     * @param argument the arguments with the following choices,
     * <br> TEST_TYPE, ALPHA: complete list of arguments;
     * <br> TEST_TYPE: default level of signifiance equal to 0.05;
     * <br> empty argument: defalut level of significance equal to 0.05 and
     *                      one-sample z test.
     * <br><br>
     * @param dataObject the input data.
     * @return the confidence interval,
     * <br>    confidenceInterval[0]: the lower bound;
     * <br>    confidenceInterval[1]: the upper bound.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double[] confidenceInterval(Hashtable argument,
                                       Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (argument.get(TEST_TYPE) != null &&
            argument.get(ALPHA) != null &&
            dataObject != null &&
            dataObject.length == 1)
        {
            confidenceInterval = confidenceInterval((String) argument.get(
                    TEST_TYPE),
                    (Double) argument.get(ALPHA),
                    (double[]) dataObject[0]);
        }
        else if (argument.get(TEST_TYPE) != null &&
                 dataObject != null &&
                 dataObject.length == 1)
        {
            confidenceInterval = confidenceInterval((String) argument.get(
                    TEST_TYPE),
                    (double[]) dataObject[0]);
        }
        else if (dataObject != null &&
                 dataObject.length == 1)
        {
            confidenceInterval = confidenceInterval((double[]) dataObject[0]);
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
     * @param data the input data.
     * @return the confidence interval,
     * <br>    confidenceInterval[0]: the lower bound;
     * <br>    confidenceInterval[1]: the upper bound.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double[] confidenceInterval(String testType,
                                       double alpha,
                                       double[] data)
    {
        if ((alpha <= 0.0) || (alpha > 1))
        {
            throw new IllegalArgumentException(
                    "The level of significance should be (strictly) positive " +
                    "and not greater than 1.");
        }
        if (data.length == 0)
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
                         new TDistribution((data.length - 1)).
                         inverse((1 - alpha / 2)));
        }
        else
        {
            argument.put(CRITICAL_VALUE,
                         new NormalDistribution().inverse((1 - alpha / 2)));
        }

        return (double[])super.confidenceInterval(argument, data);
    }

    /**
     * The 95% confidence interval.
     * @param testType the test of interest with the choices "T" or "Z".
     * @param data the input data.
     * @return the confidence interval,
     * <br>    confidenceInterval[0]: the lower bound;
     * <br>    confidenceInterval[1]: the upper bound.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double[] confidenceInterval(String testType,
                                       double[] data)
    {
        return confidenceInterval(testType, 0.05, data);
    }

    /**
     * The 95% confidence interval based on the z statistic.
     * @param data the input data.
     * @return the confidence interval,
     * <br>    confidenceInterval[0]: the lower bound;
     * <br>    confidenceInterval[1]: the upper bound.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double[] confidenceInterval(double[] data)
    {
        return confidenceInterval("Z", 0.05, data);
    }

    /**
     * The test statistic given the input argument and data.
     * @param argument the argument with the following choices,
     * <br> NULL_VALUE: the null value;
     * <br> empty argument: the null value equal to 0.
     * <br><br>
     * @param dataObject the input data.
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
        if (argument.get(NULL_VALUE) != null &&
            dataObject != null &&
            dataObject.length == 1)
        {
            testStatistic = testStatistic(
                    ((Number) argument.get(NULL_VALUE)).doubleValue(),
                    (double[]) dataObject[0]);
        }
        else if (dataObject != null &&
                 dataObject.length == 1)
        {
            testStatistic = testStatistic((double[]) dataObject[0]);
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
     * @param u0 the value of the mean under test.
     * @param data the input data.
     * @return the value of the test statistic.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double testStatistic(double u0,
                                double[] data)
    {
        this.u0 = u0;
        argument.put(NULL_VALUE, u0);

        return (Double)super.testStatistic(argument, data);
    }

    /**
     * The test statistic with the null value equal to 0.
     * @param data the input data.
     * @return the value of the test statistic.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double testStatistic(double[] data)
    {
        return testStatistic(0.0, data);
    }

    /**
     * The p value.
     * @param argument the arguments with the following choices,
     * <br> TEST_TYPE, NULL_VALUE, SIDE: complete list of arguments;
     * <br> TEST_TYPE, NULL_VALUE: the two-sided alternative hypothesis;
     * <br> TEST_TYPE: the two-sided alternative hypothesis and null value equal
     *                 to 0;
     * <br> empty argument: the two-sided alternative hypothesis, null value
     *                      equal to 0 and one-sample z test.
     * <br><br>
     * @param dataObject the input data.
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
            dataObject.length == 1)
        {
            pValue = pValue((String) argument.get(TEST_TYPE),
                            ((Number) argument.get(NULL_VALUE)).doubleValue(),
                            (String) argument.get(SIDE),
                            (double[]) dataObject[0]);
        }
        else if (argument.get(TEST_TYPE) != null &&
                 argument.get(NULL_VALUE) != null &&
                 dataObject != null &&
                 dataObject.length == 1)
        {
            pValue = pValue((String) argument.get(TEST_TYPE),
                            ((Number) argument.get(NULL_VALUE)).doubleValue(),
                            (double[]) dataObject[0]);
        }
        else if (argument.get(TEST_TYPE) != null &&
                 dataObject != null &&
                 dataObject.length == 1)
        {
            pValue = pValue((String) argument.get(TEST_TYPE),
                            (double[]) dataObject[0]);
        }
        else if (dataObject != null &&
                 dataObject.length == 1)
        {
            pValue = pValue((double[]) dataObject[0]);
        }
        else
        {
            throw new IllegalArgumentException("Wrong input arguments " +
                                               "or data.");
        }

        return pValue;
    }

    /**
     * The p value.
     * @param testType the test of interest with the choices "T" or "Z".
     * @param u0 the value of the mean under test.
     * @param side the specification of the alternative hypothesis with the
     *             choices "greater", "less" or "equal" (or "two.sided").
     * @param data the input data.
     * @return the p value for the test.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double pValue(String testType,
                         double u0,
                         String side,
                         double[] data)
    {
        this.testType = testType;
        this.u0 = u0;
        this.side = side;
        testStatistic = testStatistic(u0, data);
        argument.put(TEST_TYPE, testType);
        argument.put(SIDE, side);
        if (testType.equalsIgnoreCase("T") ||
            testType.equalsIgnoreCase("T Test"))
        {
            argument.put(CDF,
                         new TDistribution((data.length - 1)).cumulative(
                                 testStatistic));
        }
        else
        {
            argument.put(CDF,
                         new NormalDistribution().cumulative(testStatistic));
        }

        return super.pValue(argument);

    }

    /**
     * The p value for the two-sided alternative hypothesis.
     * @param testType the test of interest with the choices "T" or "Z".
     * @param u0 the value of the mean under test.
     * @param data the input data.
     * @return the p value for the test.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double pValue(String testType,
                         double u0,
                         double[] data)
    {
        return pValue(testType, u0, "equal", data);
    }

    /**
     * The p value for the two-sided alternative hypothesis as the null value is
     * equal to 0.
     * @param testType the test of interest with the choices "T" or "Z".
     * @param data the input data.
     * @return the p value for the test.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double pValue(String testType,
                         double[] data)
    {
        return pValue(testType, 0.0, "equal", data);
    }

    /**
     * The p value for the z test with the two-sided alternative hypothesis as
     * the null value is equal to 0.
     * @param data the input data.
     * @return the p value for the test.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double pValue(double[] data)
    {
        return pValue("T", 0.0, "equal", data);
    }

}
