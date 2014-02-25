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
import javastat.inference.onesample.*;
import static javastat.util.Argument.*;
import javastat.util.*;

/**
 *
 * <p>Calculates the t statistic, confidence interval, and p-value for a
 * matched-sample means problem.</p>
 * <p> </p>
 * <br> Example:
 * <br> double [] testdata1 = {51, 22, 17, 11, 28, 17, 5, 21, 22, 10};
 * <br> double [] testdata2 = {32, 14, 9, 8, 28, 11, 3, 23, 12, 14};
 * <br>
 * <br> // Non-null constructor
 * <br> MatchedSampMeansTTest testclass1 =
 *        new MatchedSampMeansTTest(0.05, 0, "equal", testdata1, testdata2);
 * <br> double testStatistic = testclass1.testStatistic;
 * <br> double pValue = testclass1.pValue;
 * <br> double lowerBound = testclass1.confidencenInterval[0];
 * <br> double upperBound = testclass1.confidenceInterval[1];
 * <br>
 * <br> // Null constructor
 * <br> MatchedSampMeansTTest testclass2 = new MatchedSampMeansTTest();
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
 *        new MatchedSampMeansTTest(argument1, testdata1, testdata2).
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
 * <br> MatchedSampMeansTTest testclass4 =
 *        new MatchedSampMeansTTest(argument2, null);
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

public class MatchedSampMeansTTest extends OneSampMeanTTest implements
        TwoSampInferenceInterface
{

    /**
     * The sample mean difference.
     */

    public double meanDifference;

    /**
     * The standard error of the sample mean difference.
     */

    public double meanDifferenceSE;

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
     * The differences between the values of two input data from both
     * populations.
     */

    public double[] differencedData;

    /**
     * The object represents a matched-sample t test for the population mean
     * difference.
     */

    public StatisticalAnalysis statisticalAnalysis;

    /**
     * Default MatchedSampMeansTTest constructor.
     */

    public MatchedSampMeansTTest() {}

    /**
     * Constructs a matched-sample t test given the input arguments and data.
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
     * @exception IllegalArgumentException two data sets should have the same
     *                                     sample size.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     */

    public MatchedSampMeansTTest(Hashtable argument,
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
                statisticalAnalysis = new MatchedSampMeansTTest(
                        (Double) argument.get(ALPHA),
                        ((Number) argument.get(NULL_VALUE)).doubleValue(),
                        (String) argument.get(SIDE),
                        (double[]) dataObject[0], (double[]) dataObject[1]);
            }
            else if (argument.get(NULL_VALUE) != null &&
                     argument.get(SIDE) != null &&
                     dataObject.length == 2)
            {
                statisticalAnalysis = new MatchedSampMeansTTest(
                        ((Number) argument.get(NULL_VALUE)).doubleValue(),
                        (String) argument.get(SIDE),
                        (double[]) dataObject[0], (double[]) dataObject[1]);
            }
            else if (argument.get(NULL_VALUE) != null &&
                       dataObject.length == 2)
            {
                statisticalAnalysis = new MatchedSampMeansTTest(
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
            statisticalAnalysis =
                    new MatchedSampMeansTTest((double[]) dataObject[0],
                                              (double[]) dataObject[1]);
        }
        else if (dataObject == null)
        {
            statisticalAnalysis = new MatchedSampMeansTTest();
        }
        else
        {
            throw new IllegalArgumentException("Wrong input data.");
        }
    }

    /**
     * Constructs a matched-sample t test given the input data, the level of
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
     * @exception IllegalArgumentException two data sets should have the same
     *                                     sample size.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     */

    public MatchedSampMeansTTest(double alpha,
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
        this.differencedData = new DataManager().matchedDataDifference(data1,
                data2);
        pointEstimate = meanDifference = super.pointEstimate(differencedData);
        pointEstimateSE = meanDifferenceSE =
                super.pointEstimateSE(differencedData);
        confidenceInterval = confidenceInterval(alpha, data1, data2);
        degreeFreedom = degreeFreedom(data1, data2);
        testStatistic = testStatistic(u12, data1, data2);
        pValue = pValue(u12, side, data1, data2);
    }

    /**
     * Constructs a matched-sample t test with a 0.05 level of significance
     * given the input data, value of the mean difference under test and
     * alternative hypothesis.
     * @param u12 the value of the mean difference under test.
     * @param side the specification of the alternative hypothesis with the
     *             choices "greater", "less" or "equal" (or "two.sided").
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     * @exception IllegalArgumentException two data sets should have the same
     *                                     sample size.
     */

    public MatchedSampMeansTTest(double u12,
                                 String side,
                                 double[] data1,
                                 double[] data2)
    {
        this(0.05, u12, side, data1, data2);
    }

    /**
     * Constructs a two-tailed t test with a 0.05 level of significance given
     * the input data and value of the mean difference under test.
     * @param u12 the value of the mean difference under test.
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     * @exception IllegalArgumentException two data sets should have the same
     *                                     sample size.
     */

    public MatchedSampMeansTTest(double u12,
                                 double[] data1,
                                 double[] data2)
    {
        this(0.05, u12, "equal", data1, data2);
    }

    /**
     * Constructs a two-tailed t test with a 0.05 level of significance given
     * the input data and value of the mean difference under test equal to 0.
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     * @exception IllegalArgumentException two data sets should have the same
     *                                     sample size.
     */

    public MatchedSampMeansTTest(double[] data1,
                                 double[] data2)
    {
        this(0.05, 0.0, "equal", data1, data2);
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
     * @exception IllegalArgumentException two data sets should have the same
     *                                     sample size.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     */

    public double[] confidenceInterval(Hashtable argument,
                                       Object ...dataObject)
    {
        return super.confidenceInterval(argument,
                                        new DataManager().matchedDataDifference(
                                                (double[]) dataObject[0],
                                                (double[]) dataObject[1]));
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
     * @exception IllegalArgumentException two data sets should have the same
     *                                     sample size.
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
        this.differencedData =
            new DataManager().matchedDataDifference(data1, data2);
        confidenceInterval = super.confidenceInterval(alpha, differencedData);

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
     * @exception IllegalArgumentException two data sets should have the same
     *                                     sample size.
     */

    public double[] confidenceInterval(double[] data1,
                                       double[] data2)
    {
        return confidenceInterval(0.05, data1, data2);
    }

    /**
     * The degree of freedom.
     * @param argument the empty argument.
     * @param dataObject the input data.
     * @return the degree of freedom of the t statistic.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     * @exception IllegalArgumentException two data sets should have the same
     *                                     sample size.
     */

    public double degreeeFreedom(Hashtable argument,
                                 Object ...dataObject)
    {
        return degreeFreedom((double[]) dataObject[0],
                             (double[]) dataObject[1]);
    }

    /**
     * The degree of freedom.
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @return the degree of freedom of the t statistic.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     * @exception IllegalArgumentException two data sets should have the same
     *                                     sample size.
     */

    public double degreeFreedom(double[] data1,
                                double[] data2)
    {
        this.data1 = data1;
        this.data2 = data2;
        this.differencedData =
            new DataManager().matchedDataDifference(data1, data2);
        degreeFreedom = super.degreeFreedom(differencedData);

        return degreeFreedom;
    }

    /**
     * The t statistic.
     * @param argument the argument with the following choices,
     * <br> NULL_VALUE: the null value;
     * <br> empty argument: the null value equal to 0.
     * <br><br>
     * @param dataObject the input data from both populations.
     * @return the value of the t statistic.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     * @exception IllegalArgumentException two data sets should have the same
     *                                     sample size.
     */

    public Double testStatistic(Hashtable argument,
                                Object ...dataObject)
    {
        return super.testStatistic(argument,
                                   new DataManager().matchedDataDifference(
                                           (double[]) dataObject[0],
                                           (double[]) dataObject[1]));
    }

    /**
     * The t statistic.
     * @param u12 the value of the mean difference under test.
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @return the value of the t statistic.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     * @exception IllegalArgumentException two data sets should have the same
     *                                     sample size.
     */

    public double testStatistic(double u12,
                                double[] data1,
                                double[] data2)
    {
        this.u12 = u12;
        this.data1 = data1;
        this.data2 = data2;
        this.differencedData =
            new DataManager().matchedDataDifference(data1, data2);
        testStatistic = super.testStatistic(u12, differencedData);

        return testStatistic;
    }

    /**
     * The t statistic with the null value equal to 0.
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @return the value of the t statistic.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     * @exception IllegalArgumentException two data sets should have the same
     *                                     sample size.
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
     * <br> empty argument: the two-sided alternative hypothesis and
     *                      null value equal to 0.
     * <br><br>
     * @param dataObject the input data from both populations.
     * @return the p value for the test.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     * @exception IllegalArgumentException two data sets should have the same
     *                                     sample size.
     */

    public Double pValue(Hashtable argument,
                         Object ...dataObject)
    {
        return super.pValue(argument,
                            new DataManager().matchedDataDifference(
                                    (double[]) dataObject[0],
                                    (double[]) dataObject[1]));
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
     * @exception IllegalArgumentException two data sets should have the same
     *                                     sample size.
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
        this.differencedData = new DataManager().matchedDataDifference(data1,
                data2);
        pValue = super.pValue(u12, side, differencedData);

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
     * @exception IllegalArgumentException two data sets should have the same
     *                                     sample size.
     */

    public double pValue(double u12,
                         double[] data1,
                         double[] data2)
    {
        return pValue(u12, "equal", data1, data2);
    }

    /**
     * The p value for the two-sided alternative hypothesis as the null value is
     * equal to 0.
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @return the p value for the test.
     * @exception IllegalArgumentException the length of the input data should
     *                                     not be 0.
     * @exception IllegalArgumentException two data sets should have the same
     *                                     sample size.
     */

    public double pValue(double[] data1,
                         double[] data2)
    {
        return pValue(0.0, "equal", data1, data2);
    }

}
