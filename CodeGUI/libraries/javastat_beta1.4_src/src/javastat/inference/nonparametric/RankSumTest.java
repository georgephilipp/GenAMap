package javastat.inference.nonparametric;

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
 * <p>Calculates Wilcoxon rank sum statistic and p-value for a two-sample
 * medians problem.</p>
 * <p> </p>
 * <br> Example:
 * <br> double [] testdata2 =
 *        {0.8, 0.83, 1.89, 1.04, 1.45, 1.38, 1.91, 1.64, 0.73, 1.46};
 * <br> double [] testdata1 = {1.15, 0.88, 0.9, 0.74, 1.21};
 * <br>
 * <br> // Non-null constructor
 * <br> RankSumTest testclass1 =
 *        new RankSumTest(0.05, "equal", testdata1, testdata2);
 * <br> double wAlpha = testclass1.wAlpha;
 * <br> double testStatistic = testclass1.testStatistic;
 * <br> double pValue = testclass1.pValue;
 * <br>
 * <br> // Null constructor
 * <br> RankSumTest testclass2 = new RankSumTest();
 * <br> testStatistic = testclass2.testStatistic(testdata1, testdata2);
 * <br> wAlpha = testclass2.wAlpha(0.05, testdata1, testdata2);
 * <br> pValue = testclass2.pValue("equal", testdata1, testdata2);
 * <br>
 * <br> // Non-null constructor
 * <br> Hashtable argument1 = new Hashtable();
 * <br> argument1.put(ALPHA, 0.05);
 * <br> argument1.put(SIDE, "equal");
 * <br> StatisticalAnalysis testclass3 =
 * <br> &nbsp;&nbsp;&nbsp;
 *        new RankSumTest(argument1, testdata1, testdata2).statisticalAnalysis;
 * <br> wAlpha = (Double) testclass3.output.get(WALPHA);
 * <br> testStatistic = (Double) testclass3.output.get(TEST_STATISTIC);
 * <br> pValue = (Double) testclass3.output.get(PVALUE);
 * <br>
 * <br> // Null constructor
 * <br> Hashtable argument2 = new Hashtable();
 * <br> RankSumTest testclass4 = new RankSumTest(argument2, null);
 * <br> wAlpha = testclass4.wAlpha(argument2, testdata1, testdata2);
 * <br> testStatistic =
 *        testclass4.testStatistic(argument, testdata1, testdata2);
 * <br> argument2.put(SIDE, "greater");
 * <br> pValue = testclass4.pValue(argument, testdata1, testdata2);
 * <br>
 * <br> // Obtains the information about the output
 * <br> out.println(testclass3.output.toString());
 * <br> out.println(testclass4.output.toString());
 */

public class RankSumTest extends StatisticalInference implements
        TwoSampInferenceInterface
{

    /**
     * The level of significance.
     */

    public double alpha;

    /**
     * The Wilcoxon rank sum statistic.
     */

    public double testStatistic;

    /**
     * Selected constant such that the upper-tailed probability for the null
     * distribution of the test statistic is equal to alpha.
     */

    public double wAlpha;

    /**
     * The p value.
     */

    public double pValue;

    /**
     * The specification of alternative hypothesis with the choices "greater",
     * "less" or "equal" (or "two.sided").
     */

    public String side;

    /**
     * The input data from the population with smaller sample size
     * (population 1).
     */

    public double[] data1;

    /**
     * The input data from the population with larger sample size
     * (population 2).
     */

    public double[] data2;

    /**
     * The object represents a Wilcoxon rank sum test.
     */

    public StatisticalAnalysis statisticalAnalysis;

    /**
     * The combinantion of data1 and data2.
     */

    private double[] data;

    /**
     * The rank of the data.
     */

    private double rank;

    /**
     * The z statistic based on large sample approximation.
     */

    private double zStatistic;

    /**
     * The tie numbers of (data1-data2), where data1 and data2 are
     * the input data from the two populations, respectively.
     */

    private double tieNumber;

    /**
     * The expected value of Wilcoxon rank sum statistic based on large sample
     * approximation.
     */

    private double rankSumMean;

    /**
     * The variance of Wilcoxon rank sum statistic based on large sample
     * approximation.
     */

    private double rankSumVariance;

    /**
     * The index used to locate the position in the table corresponding to
     * the Wilcoxon rank sum test.
     */

    private int tableIndex1;

    /**
     * The boolean index used to indicate if large sample approximation might be
     * required.
     */

    private boolean isNormalApproximation;

    /**
     * The index used to locate the position in the table corresponding to
     * the Wilcoxon rank sum test.
     */

    private int data2Index;

    /**
     * The index used to locate the position in the table corresponding to
     * the Wilcoxon rank sum test.
     */

    private int tableIndex3;

    /**
     * The class contains the collections of some basic methods used in
     * statistical computations.
     */

    private BasicStatistics basicStatistics;

    /**
     * The class for computing the cumulative normal distribution.
     */

    private NormalDistribution normalDistribution;

    /**
     * Constructs a two-sample Wilcoxon rank sum test for testing the equality
     * of the medians of two populations.
     */

    public RankSumTest() {}

    /**
     * Constructs a two-sample Wilcoxon rank sum test given the input arguments
     * and data.
     * @param argument the arguments with the following choices,
     * <br> ALPHA, SIDE: complete list of arguments;
     * <br> SIDE: default level of significance equal to 0.05;
     * <br> empty argument: defalut level of significance equal to 0.05 and
     *                      two-sided alternative hypothesis.
     * <br><br>
     * @param dataObject the input data from both populations.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the length of the input data should
     *                                     be larger than 2.
     * @exception IllegalArgumentException the input level of significance
     *                                     should be less than the largest one
     *                                     in the table.
     * @exception IllegalArgumentException the test statistic should be within
     *                                     the range of the table.
     */

    public RankSumTest(Hashtable argument,
                       Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (argument.size() > 0 &&
            dataObject != null)
        {
            if (argument.get(ALPHA) != null &&
                argument.get(SIDE) != null &&
                dataObject.length == 2)
            {
                statisticalAnalysis = new RankSumTest(
                        (Double) argument.get(ALPHA),
                        (String) argument.get(SIDE),
                        (double[]) dataObject[0], (double[]) dataObject[1]);
            }
            else if (argument.get(SIDE) != null &&
                     dataObject.length == 2)
            {
                statisticalAnalysis = new RankSumTest(
                        (String) argument.get(SIDE),
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
            statisticalAnalysis = new RankSumTest(
                    (double[]) dataObject[0], (double[]) dataObject[1]);
        }
        else if (dataObject == null)
        {
            statisticalAnalysis = new RankSumTest();
        }
        else
        {
            throw new IllegalArgumentException("Wrong input data.");
        }
    }

    /**
     * Constructs a two-sample Wilcoxon rank sum test given the input data,
     * level of significance and alternative hypothesis.
     * @param alpha the level of significance.
     * @param side the specification of the alternative hypothesis with
     *             the choices "greater", "less" or "equal" (or "two.sided").
     * @param data1 the input data from the population (population 1).
     * @param data2 the input data from the population (population 2).
     * @exception IllegalArgumentException the length of the input data should
     *                                     be larger than 2.
     * @exception IllegalArgumentException the input level of significance
     *                                     should be less than the largest one
     *                                     in the table.
     * @exception IllegalArgumentException the test statistic should be within
     *                                     the range of the table.
     */

    public RankSumTest(double alpha,
                       String side,
                       double[] data1,
                       double[] data2)
    {
        this.alpha = alpha;
        this.side = side;
        this.data1 = data1;
        this.data2 = data2;
        testStatistic = testStatistic(data1, data2);
        wAlpha = wAlpha(alpha, data1, data2);
        pValue = pValue(side, data1, data2);
    }

    /**
     * Constructs a two-sample Wilcoxon rank sum test with a 0.05 level of
     * significance given the input data and alternative hypothesis.
     * @param side the specification of the alternative hypothesis with
     *             the choices "greater", "less" or "equal" (or "two.sided").
     * @param data1 the input data from the population (population 1).
     * @param data2 the input data from the population (population 2).
     * @exception IllegalArgumentException the length of the input data should
     *                                     be larger than 2.
     * @exception IllegalArgumentException the input level of significance
     *                                     should be less than the largest one
     *                                     in the table.
     * @exception IllegalArgumentException the test statistic should be within
     *                                     the range of the table.
     */

    public RankSumTest(String side,
                       double[] data1,
                       double[] data2)
    {
        this(0.05, side, data1, data2);
    }

    /**
     * Constructs a two-tailed Wilcoxon rank sum test with a 0.05 level of
     * significance given the input data.
     * @param data1 the input data from the population (population 1).
     * @param data2 the input data from the population (population 2).
     * @exception IllegalArgumentException the length of the input data should
     *                                     be larger than 2.
     * @exception IllegalArgumentException the input level of significance
     *                                     should be less than the largest one
     *                                     in the table.
     * @exception IllegalArgumentException the test statistic should be within
     *                                     the range of the table.
     */

    public RankSumTest(double[] data1,
                       double[] data2)
    {
        this(0.05, "equal", data1, data2);
    }

    /**
     * The Wilcoxon rank sum statistic.
     * @param argument the empty argument.
     * @param dataObject the input data from both populations.
     * @return the value of the Wilcoxon rank sum statistic.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the data from population 1 should
     *                                     have smaller sample size.
     */

    public Double testStatistic(Hashtable argument,
                                Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject == null ||
            dataObject.length != 2)
        {
            throw new IllegalArgumentException(
                    "Wrong input arguments or data.");
        }

        return testStatistic((double[]) dataObject[0],
                             (double[]) dataObject[1]);
    }

    /**
     * The Wilcoxon rank sum statistic.
     * @param data1 the input data from the population (population 1).
     * @param data2 the input data from the population (population 2).
     * @return the value of the Wilcoxon rank sum statistic.
     * @exception IllegalArgumentException the length of the input data should
     *                                     be larger than 2.
     * @exception IllegalArgumentException the data from population 1 should
     *                                     have smaller sample size.
     */

    public double testStatistic(double[] data1,
                                double[] data2)
    {
        if (data1.length > data2.length)
        {
            return testStatistic(data2, data1);
        }
        else
        {
            this.data1 = data1;
            this.data2 = data2;
            testStatistic = 0.0;
            data = new DataManager().dataMerge(data1, data2);
            if (data.length < 3)
            {
                throw new IllegalArgumentException(
                        "The length of the input data should be larger " +
                        "than 2.");
            }
            for (int i = 0; i < data1.length; i++)
            {
                tieNumber = 1;
                rank = 1.0;
                for (int k = 0; k < data.length; k++)
                {
                    if (k != i)
                    {
                        if (data[i] == data[k])
                        {
                            tieNumber += 1.0;
                        }
                        if (data[i] > data[k])
                        {
                            rank += 1.0;
                        }
                    }
                }
                if (tieNumber > 1.0)
                {
                    rank += tieNumber * (tieNumber - 1) / (2 * tieNumber);
                }
                testStatistic += rank;
            }
            output.put(TEST_STATISTIC, testStatistic);
            return testStatistic;
        }
    }

    /**
     * Calculates the selected constant such that the upper-tailed probability
     * for the null distribution of the test statistic is equal to the level of
     * significance.
     * @param argument the argument with the following choices,
     * <br> ALPHA: the level of significance;
     * <br> empty argument: defalut level of significance equal to 0.05.
     * <br><br>
     * @param dataObject the input data from both populations.
     * @return the value of the selected constant.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the input level of significance
     *                                     should be less than the largest one
     *                                     in the table.
     */

    public Double wAlpha(Hashtable argument,
                         Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (argument.get(ALPHA) != null &&
            dataObject != null &&
            dataObject.length == 2)
        {
            wAlpha = wAlpha((Double) argument.get(ALPHA),
                            (double[]) dataObject[0], (double[]) dataObject[1]);
        }
        else if (dataObject != null &&
                 dataObject.length == 2)
        {
            wAlpha = wAlpha((double[]) dataObject[0], (double[]) dataObject[1]);
        }
        else
        {
            throw new IllegalArgumentException("Wrong input arguments or " +
                                               "data.");
        }

        return wAlpha;
    }

    /**
     * Calculates the selected constant such that the upper-tailed probability
     * for the null distribution of the test statistic is equal to alpha.
     * @param alpha the level of significance.
     * @param data1 the input data from the population (population 1).
     * @param data2 the input data from population (population 2).
     * @return the value of the selected constant.
     * @exception IllegalArgumentException the input level of significance
     *                                     should be less than the largest one
     *                                     in the table.
     */

    public double wAlpha(double alpha,
                         double[] data1,
                         double[] data2)
    {
        if (data1.length > data2.length)
        {
            return wAlpha(alpha, data2, data1);
        }
        else
        {
            this.alpha = alpha;
            this.data1 = data1;
            this.data2 = data2;
            tableIndex1 = 0;
            data2Index = data2.length - data1.length;
            if (data1.length <= 2)
            {
                data2Index = data2.length - 3;
            }
            if ((data1.length <= 4 && data2.length <= 20) ||
                (data1.length <= 10 && data2.length <= 10))
            {
                if (alpha > BasicStatistics.
                    rankSumTable[data1.length - 1][data2Index][0])
                {
                    throw new IllegalArgumentException(
                        "The input level of significance should be less than "
                        + BasicStatistics.
                        rankSumTable[data1.length][data2Index][0] + ".");
                }
                while (alpha <= BasicStatistics.rankSumTable[data1.length - 1]
                         [data2Index][tableIndex1])
                {
                    tableIndex1 += 1;
                }
                wAlpha = tableIndex1 +
                         BasicStatistics.
                         rankSumIndex[data1.length - 1][data2Index] - 1;
                output.put(WALPHA, wAlpha);
                return tableIndex1 + BasicStatistics.
                        rankSumIndex[data1.length - 1][data2Index] - 1;
            }
            else
            {
                wAlpha = Double.NaN;
                output.put(WALPHA, Double.NaN);
                return Double.NaN;
            }
        }
    }

    /**
     * Calculates the selected constant such that the upper-tailed probability
     * for the null distribution of the test statistic is equal to 0.05.
     * @param data1 the input data from the population (population 1).
     * @param data2 the input data from population (population 2).
     * @return the value of the selected constant.
     * @exception IllegalArgumentException the input level of significance
     *                                     should be less than the largest one
     *                                     in the table.
     */

    public double wAlpha(double[] data1,
                         double[] data2)
    {
        return wAlpha(0.05, data1, data2);
    }

    /**
     * The p value.
     * @param argument the argument with the following choices,
     * <br> SIDE: the specification of alternative hypothesis with the choices
     *            "greater", "less" or "equal" (or "two.sided");
     * <br> empty argument: two-sided alternative hypothesis.
     * <br><br>
     * @param dataObject the input data from both populations.
     * @return the p value for the test.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the length of the input data should
     *                                     be larger than 2.
     * @exception IllegalArgumentException the test statistic should be within
     *                                     the range of the table.
     */

    public Double pValue(Hashtable argument,
                         Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (argument.get(SIDE) != null &&
            dataObject != null &&
            dataObject.length == 2)
        {
            pValue = pValue((String) argument.get(SIDE),
                            (double[]) dataObject[0], (double[]) dataObject[1]);
        }
        else if (dataObject != null &&
                 dataObject.length == 2)
        {
            pValue = pValue((double[]) dataObject[0], (double[]) dataObject[1]);
        }
        else
        {
            throw new IllegalArgumentException(
                    "Wrong input arguments or data.");
        }

        return pValue;
    }

    /**
     * The p value.
     * @param side the specification of alternative hypothesis with the choices
     *             "greater", "less" or "equal" (or "two.sided").
     * @param data1 input data from the population (population 1).
     * @param data2 input data from population (population 2).
     * @return the p value for the test.
     * @exception IllegalArgumentException the length of the input data should
     *                                     be larger than 2.
     * @exception IllegalArgumentException the test statistic should be within
     *                                     the range of the table.
     */

    public double pValue(String side,
                         double[] data1,
                         double[] data2)
    {
        if (data1.length > data2.length)
        {
            return pValue(side, data2, data1);
        }
        else
        {
            this.side = side;
            this.data1 = data1;
            this.data2 = data2;
            testStatistic = testStatistic(data1, data2);
            isNormalApproximation = false;
            data2Index = data2.length - data1.length;
            if (data1.length <= 2)
            {
                data2Index = data2.length - 3;
            }
            if ((data1.length <= 4 && data2.length <= 20) ||
                (data1.length <= 10 && data2.length <= 10))
            {
                isNormalApproximation = true;
            }
            if (data1.length <= 10 &&
                data2Index >= 0 &&
                isNormalApproximation)
            {
                tableIndex3 = (int) testStatistic -
                              BasicStatistics.rankSumIndex[data1.length -
                              1][data2Index];
                if (side.equalsIgnoreCase("greater"))
                {
                    if (tableIndex3 < 0)
                    {
                        tableIndex3 = data1.length *
                                      (data1.length + data2.length + 1) -
                                      (int) testStatistic - BasicStatistics.
                                      rankSumIndex[data1.length - 1]
                                      [data2Index];
                        pValue = 1 - BasicStatistics.
                                 rankSumTable[data1.length - 1]
                                 [data2Index][tableIndex3];
                    }
                    else
                    {
                        pValue = BasicStatistics.rankSumTable[data1.length - 1]
                                 [data2Index][tableIndex3];
                    }
                }
                else if (side.equalsIgnoreCase("less"))
                {
                    if (tableIndex3 < 0)
                    {
                        tableIndex3 = data1.length *
                                      (data1.length + data2.length + 1) -
                                      (int) testStatistic - BasicStatistics.
                                      rankSumIndex[data1.length - 1]
                                      [data2Index];
                        pValue = BasicStatistics.rankSumTable[data1.length - 1]
                                 [data2Index][tableIndex3];
                    }
                    else
                    {
                        pValue = 1 - BasicStatistics.
                                 rankSumTable[data1.length - 1]
                                 [data2Index][tableIndex3];
                    }
                }
                else
                {
                    tableIndex3 = Math.max((int) testStatistic, data1.length *
                                           (data1.length + data2.length + 1) -
                                           (int) testStatistic)
                                  - BasicStatistics.
                                  rankSumIndex[data1.length - 1][data2Index];
                    pValue = 2 * BasicStatistics.rankSumTable[data1.length - 1]
                             [data2Index][tableIndex3];
                    if (pValue > 1)
                    {
                        pValue = 1.0;
                    }
                }
            }
            else
            {
                basicStatistics = new BasicStatistics();
                rankSumMean = basicStatistics.rankSumMean(data1, data2);
                rankSumVariance = basicStatistics.rankSumVariance(data1, data2);
                normalDistribution = new NormalDistribution();
                zStatistic = (testStatistic - rankSumMean) /
                             Math.sqrt(rankSumVariance);
                if (side.equalsIgnoreCase("less"))
                {
                    pValue = normalDistribution.cumulative(zStatistic);
                }
                else if (side.equalsIgnoreCase("greater"))
                {
                    pValue = 1 - normalDistribution.cumulative(zStatistic);
                }
                else
                {
                    pValue = 2 * (1 - normalDistribution.
                                  cumulative(Math.abs(zStatistic)));
                }
            }
            output.put(PVALUE, pValue);
            return pValue;
        }
    }

    /**
     * The p value for a two-sided alternative hypothesis.
     * @param data1 input data from the population (population 1).
     * @param data2 input data from population (population 2).
     * @return the p value for the test.
     * @exception IllegalArgumentException the length of the input data should
     *                                     be larger than 2.
     * @exception IllegalArgumentException the test statistic should be within
     *                                     the range of the table.
     */

    public double pValue(double[] data1,
                         double[] data2)
    {
        return pValue("equal", data1, data2);
    }

}
