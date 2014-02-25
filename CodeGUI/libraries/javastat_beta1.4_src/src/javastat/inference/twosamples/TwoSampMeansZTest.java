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

/**
 *
 * <p>Calculates the z statistic, confidence interval, and p-value for a
 * two-sample means problem.</p>
 * <p> </p>
 * <br> Example:
 * <br> double [] testdata1 = {30, 40, 28, 5, 28, 29, 38, 36, 23, 22, 51, 33,
 *                             35, 42,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                             21, 31, 44, 32, 30, 32, 30, 30, 45, 29, 35, 33,
 *                             40, 31,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                             37, 41, 34, 40, 29, 42, 17, 29, 34, 39};
 * <br> double [] testdata2 = {60, 68, 63, 37, 57, 52, 63, 68, 47, 44, 74, 71,
 *                             83, 92,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                             66, 64, 62, 73, 62, 51, 77, 77, 71, 53, 72, 58,
 *                             70, 58,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                             72, 81, 64, 60, 60, 72, 40, 61, 58, 69};
 * <br>
 * <br> // Non-null constructor
 * <br> TwoSampMeansZTest testclass1 =
 *        new TwoSampMeansZTest(0.05, 0, "equal", testdata1, testdata2);
 * <br> double testStatistic = testclass1.testStatistic;
 * <br> double pValue = testclass1.pValue;
 * <br> double lowerBound = testclass1.confidenceInterval[0];
 * <br> double upperBound = testclass1.confidenceInterval[1];
 * <br>
 * <br> // Null constructor
 * <br> TwoSampMeansZTest testclass2 = new TwoSampMeansZTest();
 * <br> double [] confidenceInterval =
 *        testclass2.confidenceInterval(0.05, testdata1, testdata2);
 * <br> testStatistic = testclass2.testStatistic(0, testdata1, testdata2);
 * <br> pValue = testclass2.pValue(0, "greater", testdata1, testdata2);
 * <br>
 * <br> // Non-null constructor
 * <br> Hashtable argument1 = new Hashtable();
 * <br> argument1.put(ALPHA, 0.05);
 * <br> argument1.put(NULL_VALUE, 0);
 * <br> argument1.put(SIDE, "equal");
 * <br> StatisticalAnalysis testclass3 =
 * <br> &nbsp;&nbsp;&nbsp;
 *        new TwoSampMeansZTest(argument1, testdata1, testdata2).
 *        statisticalAnalysis;
 * <br> testStatistic = (Double) testclass3.output.get(TEST_STATISTIC);
 * <br> pValue = (Double) testclass3.output.get(PVALUE);
 * <br> confidenceInterval =
 *        (double[]) testclass3.output.get(CONFIDENCE_INTERVAL);
 * <br> lowerBound = confidenceInterval[0];
 * <br> upperBound = confidenceInterval[1];
 * <br>
 * <br> // Null constructor
 * <br> Hashtable argument2 = new Hashtable();
 * <br> TwoSampMeansZTest testclass4 = new TwoSampMeansZTest(argument2, null);
 * <br> argument2.put(ALPHA, 0.05);
 * <br> confidenceInterval =
 *        testclass4.confidenceInterval(argument2, testdata1, testdata2);
 * <br> argument2.put(NULL_VALUE, 0);
 * <br> testStatistic =
 *        testclass4.testStatistic(argument2, testdata1, testdata2);
 * <br> argument2.put(SIDE, "greater");
 * <br> pValue = testclass4.pValue(argument2, testdata1, testdata2);
 * <br>
 * <br> // Obtains the information about the output
 * <br> out.println(testclass3.output.toString());
 * <br> out.println(testclass4.output.toString());
 */

public class TwoSampMeansZTest extends StatisticalInferenceTemplate implements
        TwoSampInferenceInterface
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
     * The input data from population 1.
     */

    public double[] data1;

    /**
     * The input data from population 2.
     */

    public double[] data2;

    /**
     * The object represents a two-sample z test for the population mean
     * difference.
     */

    public StatisticalAnalysis statisticalAnalysis;

    /**
     * Default TwoSampMeansZTest constructor.
     */

    public TwoSampMeansZTest() {}

    /**
     * Constructs a two-sample z test given the input arguments and data.
     * @param argument the arguments with the following choices,
     * <br> ALPHA, NULL_VALUE, SIDE: complete list of arguments;
     * <br> NULL_VALUE, SIDE: default level of signifiance equal to 0.05;
     * <br> NULL_VALUE: default level of signifiance equal to 0.05 and two-sided
     *                  alternative hypothesis;
     * <br> empty argument: defalut level of significance equal to 0.05,
     *                      two-sided alternative hypothesis and the null value
     *                      equal to 0.
     * <br><br>
     * @param dataObject the input data from both populations.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     */

    public TwoSampMeansZTest(Hashtable argument,
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
                statisticalAnalysis = new TwoSampMeansZTest(
                        (Double) argument.get(ALPHA),
                        ((Number) argument.get(NULL_VALUE)).doubleValue(),
                        (String) argument.get(SIDE),
                        (double[]) dataObject[0], (double[]) dataObject[1]);
            }
            else if (argument.get(NULL_VALUE) != null &&
                     argument.get(SIDE) != null &&
                     dataObject.length == 2)
            {
                statisticalAnalysis = new TwoSampMeansZTest(
                        ((Number) argument.get(NULL_VALUE)).doubleValue(),
                        (String) argument.get(SIDE),
                        (double[]) dataObject[0], (double[]) dataObject[1]);
            }
            else if (argument.get(NULL_VALUE) != null &&
                     dataObject.length == 2)
            {
                statisticalAnalysis = new TwoSampMeansZTest(
                        ((Number) argument.get(NULL_VALUE)).doubleValue(),
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
            statisticalAnalysis = new TwoSampMeansZTest(
                    (double[]) dataObject[0], (double[]) dataObject[1]);
        }
        else if (dataObject == null)
        {
            statisticalAnalysis = new TwoSampMeansZTest();
        }
        else
        {
            throw new IllegalArgumentException("Wrong input data.");
        }
    }

    /**
     * Constructs a two-sample z test given the input data, level of
     * significance, value of the mean difference under test and alternative
     * hypothesis.
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

    public TwoSampMeansZTest(double alpha,
                             double u12,
                             String side,
                             double[] data1,
                             double[] data2)
    {
        this.alpha = alpha;
        this.u12 = u12;
        this.side = side;
        this.data1 = data1;
        this.data2 = data2;
        pointEstimate = pointEstimate(data1, data2);
        pointEstimateSE = pointEstimateSE(data1, data2);
        confidenceInterval = confidenceInterval(alpha, data1, data2);
        testStatistic = testStatistic(u12, data1, data2);
        pValue = pValue(u12, side, data1, data2); ;
    }

    /**
     * Constructs a two-sample z test with a 0.05 level of significance given
     * the input data, value of the mean difference under test and alternative
     * hypothesis.
     * @param u12 the value of the mean difference under test.
     * @param side the specification of the alternative hypothesis with the
     *             choices "greater", "less" or "equal" (or "two.sided").
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public TwoSampMeansZTest(double u12,
                             String side,
                             double[] data1,
                             double[] data2)
    {
        this(0.05, u12, side, data1, data2);
    }

    /**
     * Constructs a two-tailed z test with a 0.05 level of significance given
     * the input data and value of the mean difference under test.
     * @param u12 the value of the mean difference under test.
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public TwoSampMeansZTest(double u12,
                             double[] data1,
                             double[] data2)
    {
        this(0.05, u12, "equal", data1, data2);
    }

    /**
     * Constructs a two-tailed z test with a 0.05 level of significance given
     * the input data and value of the mean difference under test equal to 0.
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public TwoSampMeansZTest(double[] data1,
                             double[] data2)
    {
        this(0.05, 0.0, "equal", data1, data2);
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
     * @return the point estimate.
     */

    public double pointEstimate(double[] data1,
                                double[] data2)
    {
        this.data1 = data1;
        this.data2 = data2;
        pointEstimate = new TwoSampMeansTest().pointEstimate(data1, data2);
        output.put(POINT_ESTIMATE, pointEstimate);

        return pointEstimate;
    }

    /**
     * Computes the standard error of the point estimate.
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @return the standard error of the point estimate.
     */

    public double pointEstimateSE(double[] data1,
                                  double[] data2)
    {
        this.data1 = data1;
        this.data2 = data2;
        pointEstimateSE =
            new TwoSampMeansTest().pointEstimateSE("Z", data1, data2);
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

        return pointEstimateSE((double[]) dataObject[0],
                               (double[]) dataObject[1]);
    }

    /**
     * The confidence interval.
     * @param argument the argument with the following choices,
     * <br> ALPHA: the level of significance;
     * <br> empty argument: defalut level of significance equal to 0.05.
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
        argument.put(TEST_TYPE, "Z");
        this.argument = argument;
        this.dataObject = dataObject;
        confidenceInterval =
            new TwoSampMeansTest().confidenceInterval(argument, dataObject);
        output.put(CONFIDENCE_INTERVAL, confidenceInterval);

        return confidenceInterval;
    }

    /**
     * The confidence interval.
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

    public double[] confidenceInterval(double alpha,
                                       double[] data1,
                                       double[] data2)
    {
        this.alpha = alpha;
        this.data1 = data1;
        this.data2 = data2;
        confidenceInterval = new TwoSampMeansTest().confidenceInterval("Z",
                alpha, data1, data2);
        output.put(CONFIDENCE_INTERVAL, confidenceInterval);

        return confidenceInterval;
    }

    /**
     * The 95% confidence interval.
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
        return confidenceInterval(0.05, data1, data2);
    }

    /**
     * The z statistic given the input argument and data.
     * @param argument the argument with the following choices,
     * <br> NULL_VALUE: the null value;
     * <br> empty argument: the null value equal to 0.
     * <br><br>
     * @param dataObject the input data from both populations.
     * @return the value of the z statistic.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public Double testStatistic(Hashtable argument,
                                Object ...dataObject)
    {
        argument.put(TEST_TYPE, "Z");
        this.argument = argument;
        this.dataObject = dataObject;
        testStatistic = new TwoSampMeansTest().testStatistic(argument,
                dataObject);
        output.put(TEST_STATISTIC, testStatistic);

        return testStatistic;
    }

    /**
     * The z statistic.
     * @param u12 the value of the mean difference under test.
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @return the value of the z statistic.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double testStatistic(double u12,
                                double[] data1,
                                double[] data2)
    {
        this.u12 = u12;
        this.data1 = data1;
        this.data2 = data2;
        testStatistic = new TwoSampMeansTest().testStatistic("Z", u12, data1,
                data2);
        output.put(TEST_STATISTIC, testStatistic);

        return testStatistic;
    }

    /**
     * The z statistic with the null value equal to 0.
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @return the value of the z statistic.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double testStatistic(double[] data1,
                                double[] data2)
    {
        return testStatistic(0.0, data1, data2);
    }

    /**
     * The p value.
     * @param argument the arguments with the following choices,
     * <br> NULL_VALUE, SIDE: complete list of arguments;
     * <br> NULL_VALUE: the two-sided alternative hypothesis;
     * <br> empty argument: the two-sided alternative hypothesis and null value
     *                      equal to 0.
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
        argument.put(TEST_TYPE, "Z");
        this.argument = argument;
        this.dataObject = dataObject;
        pValue = new TwoSampMeansTest().pValue(argument, dataObject);
        output.put(PVALUE, pValue);

        return pValue;
    }

    /**
     * The p value.
     * @param u12 the value of the mean difference under test.
     * @param side the specification of the alternative hypothesis with the
     *             choices "greater", "less" or "equal" (or "two.sided").
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @return the p value for the test.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double pValue(double u12,
                         String side,
                         double[] data1,
                         double[] data2)
    {
        this.u12 = u12;
        this.side = side;
        this.data1 = data1;
        this.data2 = data2;
        pValue = new TwoSampMeansTest().pValue("Z", u12, side, data1, data2);
        output.put(PVALUE, pValue);

        return pValue;
    }

    /**
     * The p value for the two-sided alternative hypothesis.
     * @param u12 the value of the mean difference under test.
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @return the p value for the test.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     */

    public double pValue(double u12,
                         double[] data1,
                         double[] data2)
    {
        return pValue(u12, "equal", data1, data2);
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
        return pValue(0.0, "equal", data1, data2);
    }

}
