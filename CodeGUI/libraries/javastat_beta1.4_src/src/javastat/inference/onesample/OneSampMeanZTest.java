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

/**
 *
 * <p>Calculates the z statistic, confidence interval, and p-value for a one
 * sample mean problem.</p>
 * <p> </p>
 * <br> Example:
 * <br> double [] testdata = {86, 97, 159, 92, 146, 138, 101, 87, 151, 69,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                         97, 79, 103, 104, 104, 105, 105, 102, 112, 116,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                         118, 124, 125, 129, 132, 98, 88, 68, 135, 100};
 * <br>
 * <br> // Non-null constructor
 * <br> OneSampMeanZTest testclass1 =
 *        new OneSampMeanZTest(0.05, 100, "equal", testdata);
 * <br> double testStatistic = testclass1.testStatistic;
 * <br> double pValue = testclass1.pValue;
 * <br> double lowerBound = testclass1.confidenceInterval[0];
 * <br> double upperBound = testclass1.confidenceInterval[1];
 * <br>
 * <br> // Null constructor
 * <br> OneSampMeanZTest testclass2 = new OneSampMeanZTest();
 * <br> double [] confidenceInterval =
 *        testclass2.confidenceInterval(0.05, testdata);
 * <br> testStatistic = testclass2.testStatistic(100, testdata);
 * <br> pValue = testclass2.pValue(100, "greater", testdata);
 * <br>
 * <br> // Non-null constructor
 * <br> Hashtable argument1 = new Hashtable();
 * <br> argument1.put(NULL_VALUE, 100.0);
 * <br> argument1.put(SIDE, "equal");
 * <br> argument1.put(ALPHA, 0.05);
 * <br> StatisticalAnalysis testclass3 =
 * <br> &nbsp;&nbsp;&nbsp;
 *        new OneSampMeanZTest(argument1, testdata).statisticalAnalysis;
 * <br> testStatistic = (Double) testclass3.output.get(TEST_STATISTIC);
 * <br> pValue = (Double) testclass3.output.get(PVALUE);
 * <br> confidenceInterval =
 *        (double[]) testclass3.output.get(CONFIDENCE_INTERVAL);
 * <br> lowerBound = confidenceInterval[0];
 * <br> upperBound = confidenceInterval[1];
 * <br>
 * <br> // Null constructor
 * <br> Hashtable argument2 = new Hashtable();
 * <br> OneSampMeanZTest testclass4 = new OneSampMeanZTest(argument2, null);
 * <br> argument2.put(ALPHA, 0.05);
 * <br> confidenceInterval = testclass4.confidenceInterval(argument2, testdata);
 * <br> argument2.put(NULL_VALUE, 100.0);
 * <br> testStatistic = testclass4.testStatistic(argument2, testdata);
 * <br> argument2.put(SIDE, "greater");
 * <br> pValue = testclass4.pValue(argument2, testdata);
 * <br>
 * <br> // Obtains the information about the output
 * <br> out.println(testclass3.output.toString());
 * <br> out.println(testclass4.output.toString());
 */

public class OneSampMeanZTest extends StatisticalInferenceTemplate implements
        OneSampInferenceInterface
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
     * The input data.
     */

    public double[] data;

    /**
     * The object represents a one-sample z test.
     */

    public StatisticalAnalysis statisticalAnalysis;

    /**
     * Default OneSampMeanZTest constructor.
     */

    public OneSampMeanZTest() {}

    /**
     * Constructs a one sample z test given the input arguments and data.
     * @param argument the arguments with the following choices,
     * <br> ALPHA, NULL_VALUE, SIDE: complete list of arguments;
     * <br> NULL_VALUE, SIDE: default level of signifiance equal to 0.05;
     * <br> NULL_VALUE: default level of signifiance equal to 0.05 and two-sided
     *                  alternative hypothesis;
     * <br> empty argument: defalut level of significance equal to 0.05,
     *                      two-sided alternative hypothesis and the null value
     *                      equal to 0.
     * <br><br>
     * @param dataObject the input data.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public OneSampMeanZTest(Hashtable argument,
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
                dataObject.length == 1)
            {
                statisticalAnalysis = new OneSampMeanZTest(
                        (Double) argument.get(ALPHA),
                        ((Number) argument.get(NULL_VALUE)).doubleValue(),
                        (String) argument.get(SIDE),
                        (double[]) dataObject[0]);
            }
            else if (argument.get(NULL_VALUE) != null &&
                     argument.get(SIDE) != null &&
                     dataObject.length == 1)
            {
                statisticalAnalysis = new OneSampMeanZTest(
                        ((Number) argument.get(NULL_VALUE)).doubleValue(),
                        (String) argument.get(SIDE),
                        (double[]) dataObject[0]);
            }
            else if (argument.get(NULL_VALUE) != null &&
                     dataObject.length == 1)
            {
                statisticalAnalysis = new OneSampMeanZTest(
                        ((Number) argument.get(NULL_VALUE)).doubleValue(),
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
            statisticalAnalysis =
                    new OneSampMeanZTest((double[]) dataObject[0]);
        }
        else if (dataObject == null)
        {
            statisticalAnalysis = new OneSampMeanZTest();
        }
        else
        {
            throw new IllegalArgumentException("Wrong input data.");
        }
    }

    /**
     * Constructs a one sample z test given the input data, level of
     * significance, value of the mean under test and alternative hypothesis.
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

    public OneSampMeanZTest(double alpha,
                            double u0,
                            String side,
                            double[] data)
    {
        this.alpha = alpha;
        this.u0 = u0;
        this.side = side;
        this.data = data;
        pointEstimate = pointEstimate(data);
        pointEstimateSE = pointEstimateSE(data);
        confidenceInterval = confidenceInterval(alpha, data);
        testStatistic = testStatistic(u0, data);
        pValue = pValue(u0, side, data);
    }

    /**
     * Constructs a one sample z test with a 0.05 level of significance given
     * the input data, value of the mean under test and alternative hypothesis.
     * @param u0 the value of the mean under test.
     * @param side the specification of the alternative hypothesis with the
     *             choices "greater", "less" or "equal" (or "two.sided").
     * @param data the input data.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public OneSampMeanZTest(double u0,
                            String side,
                            double[] data)
    {
        this(0.05, u0, side, data);
    }

    /**
     * Constructs a two-tailed z test with a 0.05 level of significance given
     * the input data and value of the mean under test.
     * @param u0 the value of the mean under test.
     * @param data the input data.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public OneSampMeanZTest(double u0,
                            double[] data)
    {
        this(0.05, u0, "equal", data);
    }

    /**
     * Constructs a two-tailed z test with a 0.05 level of significance given
     * the input data and value of the mean under test equal to 0.
     * @param data the input data.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public OneSampMeanZTest(double[] data)
    {
        this(0.05, 0.0, "equal", data);
    }

    /**
     * Computes the point estimate, which is the sample mean.
     * @param data the input data.
     * @return the point estimate.
     */

    public double pointEstimate(double[] data)
    {
        this.data = data;
        pointEstimate = new OneSampMeanTest().pointEstimate(data);
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
        pointEstimateSE = new OneSampMeanTest().pointEstimateSE(data);
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
     * @param argument the argument with the following choices,
     * <br> ALPHA: the level of significance;
     * <br> empty argument: defalut level of significance equal to 0.05.
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
        argument.put(TEST_TYPE, "Z");
        this.argument = argument;
        this.dataObject = dataObject;
        confidenceInterval = new OneSampMeanTest().confidenceInterval(argument,
                dataObject);
        output.put(CONFIDENCE_INTERVAL, confidenceInterval);

        return confidenceInterval;
    }

    /**
     * The confidence interval.
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

    public double[] confidenceInterval(double alpha,
                                       double[] data)
    {
        this.alpha = alpha;
        this.data = data;
        confidenceInterval =
                new OneSampMeanTest().confidenceInterval("Z", alpha, data);
        output.put(CONFIDENCE_INTERVAL, confidenceInterval);

        return confidenceInterval;
    }

    /**
     * The 95% confidence interval.
     * @param data the input data.
     * @return the confidence interval,
     * <br>    confidenceInterval[0]: the lower bound;
     * <br>    confidenceInterval[1]: the upper bound.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double[] confidenceInterval(double[] data)
    {
        return confidenceInterval = new OneSampMeanTest().confidenceInterval(
                "Z", 0.05, data);
    }

    /**
     * The z statistic.
     * @param argument the argument with the following choices,
     * <br> NULL_VALUE: the null value;
     * <br> empty argument: the null value equal to 0.
     * <br><br>
     * @param dataObject the input data.
     * @return the value of the z statistic.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public Double testStatistic(Hashtable argument,
                                Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        testStatistic = new OneSampMeanTest().testStatistic(argument,
                dataObject);
        output.put(TEST_STATISTIC, testStatistic);

        return testStatistic;
    }

    /**
     * The z statistic.
     * @param u0 the value of the mean under test.
     * @param data the input data.
     * @return the value of the z statistic.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double testStatistic(double u0,
                                double[] data)
    {
        this.u0 = u0;
        this.data = data;
        testStatistic = new OneSampMeanTest().testStatistic(u0, data);
        output.put(TEST_STATISTIC, testStatistic);

        return testStatistic;
    }

    /**
     * The z statistic with the null value equal to 0.
     * @param data the input data.
     * @return the value of the z statistic.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double testStatistic(double[] data)
    {
        return testStatistic = testStatistic(0.0, data);
    }

    /**
     * The p value.
     * @param argument the arguments with the following choices,
     * <br> NULL_VALUE, SIDE: complete list of arguments;
     * <br> NULL_VALUE: the two-sided alternative hypothesis;
     * <br> empty argument: the two-sided alternative hypothesis and
     *                      null value equal to 0.
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
        argument.put(TEST_TYPE, "Z");
        this.argument = argument;
        this.dataObject = dataObject;
        pValue = new OneSampMeanTest().pValue(argument, dataObject);
        output.put(PVALUE, pValue);

        return pValue;
    }

    /**
     * The p value.
     * @param u0 the value of the mean under test.
     * @param side the specification of the alternative hypothesis with the
     *             choices "greater", "less" or "equal" (or "two.sided").
     * @param data the input data.
     * @return the p value for the test.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double pValue(double u0,
                         String side,
                         double[] data)
    {
        this.u0 = u0;
        this.side = side;
        this.data = data;
        pValue = new OneSampMeanTest().pValue("Z", u0, side, data);
        output.put(PVALUE, pValue);

        return pValue;
    }

    /**
     * The p value for the two-sided alternative hypothesis.
     * @param u0 the value of the mean under test.
     * @param data the input data.
     * @return the p value for the test.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double pValue(double u0,
                         double[] data)
    {
        return pValue = pValue(u0, "equal", data);
    }

    /**
     * The p value for the two-sided alternative hypothesis as the null value is
     * equal to 0.
     * @param data the input data.
     * @return the p value for the test.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double pValue(double[] data)
    {
        return pValue = pValue(0.0, "equal", data);
    }

}
