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
 * <p>Calculates Wilcoxon sign rank statistic and p-value for a one sample
 * median problem.</p>
 * <p> </p>
 * <br> Example:
 * <br> double [] testdata1 = {750, 1400, -300, 2400, -700, 800, 1300, -400,
 *                             1900, -1100, 1600, 300};
 * <br>
 * <br> // Non-null constructor
 * <br> SignRankTest testclass1 =
 *        new SignRankTest(0.05, 0, "equal", testdata1);
 * <br> double tAlpha = testclass1.tAlpha;
 * <br> double testStatistic = testclass1.testStatistic;
 * <br> double pValue = testclass1.pValue;
 * <br>
 * <br> // Null constructor
 * <br> SignRankTest testclass2 = new SignRankTest();
 * <br> testStatistic = testclass2.testStatistic(0, testdata1);
 * <br> tAlpha = testclass2.tAlpha(0.05, 0, testdata1);
 * <br> pValue = testclass2.pValue(0, "greater", testdata1);
 * <br>
 * <br> // Non-null constructor
 * <br> Hashtable argument1 = new Hashtable();
 * <br> argument1.put(ALPHA, 0.02);
 * <br> argument1.put(NULL_VALUE, 0);
 * <br> argument1.put(SIDE, "greater");
 * <br> StatisticalAnalysis testclass3 =
 * <br> &nbsp;&nbsp;&nbsp;
 *        new SignRankTest(argument1, testdata1).statisticalAnalysis;
 * <br> tAlpha = (Double) testclass3.output.get(TALPHA);
 * <br> testStatistic = (Double) testclass3.output.get(TEST_STATISTIC);
 * <br> pValue = (Double) testclass3.output.get(PVALUE);
 * <br>
 * <br> // Null constructor
 * <br> Hashtable argument2 = new Hashtable();
 * <br> RankSumTest testclass4 = new SignRankTest(argument2, null);
 * <br> argument2.put(NULL_VALUE, 100.0);
 * <br> testStatistic = testclass4.testStatistic(argument2, testdata1);
 * <br> tAlpha = testclass4.tAlpha(argument2, testdata1);
 * <br> argument2.put(SIDE, "equal");
 * <br> pValue = testclass4.pValue(argument2, testdata1);
 * <br>
 * <br> // Obtains the information about the output
 * <br> out.println(testclass3.output.toString());
 * <br> out.println(testclass4.output.toString());
 */

public class SignRankTest extends StatisticalInference implements
        OneSampInferenceInterface
{

    /**
     * The level of significance.
     */

    public double alpha;

    /**
     * The Wilcoxon sign rank statistic.
     */

    public double testStatistic;

    /**
     * Selected constant such that the upper-tailed probability for the null
     * distribution of the test statistic is equal to alpha.
     */

    public double tAlpha;

    /**
     * The p value.
     */

    public double pValue;

    /**
     * The value of the median under test.
     */

    public double med;

    /**
     * The specification of the alternative hypothesis with the choices
     * "greater", "less" or "equal" (or "two.sided").
     */

    public String side;

    /**
     * The input data.
     */

    public double[] data;

    /**
     * The object represents a Wilcoxon sign rank test.
     */

    public StatisticalAnalysis statisticalAnalysis;

    /**
     * The number of the input data equal to the median under test.
     */

    private int zeroNumber;

    /**
     * The rank of the data.
     */

    private double rank;

    /**
     * The z statistic based on large sample approximation.
     */

    private double zStatistic;

    /**
     * The expected value of the Wilcoxon sign rank statistic based on large
     * sample approximation.
     */

    private double signRankMean;

    /**
     * The variance of the Wilcoxon sign rank statistic based on large sample
     * approximation.
     */

    private double signRankVariance;

    /**
     * The tie number of (data-med), where med is the value of the median under
     * test.
     */

    private double[] tieNumber;

    /**
     * The index used to locate the position in the table corresponding to
     * the Wilcoxon sign rank test.
     */

    private int tableIndex;

    /**
     * The index used to locate the position in the table corresponding to
     * the Wilcoxon sign rank test.
     */

    private int lengthIndex;

    /**
     * The row length.
     */

    private int rowLengthIndex;

    /**
     * The class contains the collections of some basic methods for manipulating
     * the data.
     */

    private DataManager dataManager;

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
     * Constructs a one sample Wilcoxon sign rank test for population median.
     */

    public SignRankTest() {}

    /**
     * Constructs a one sample Wilcoxon sign rank test given the input
     * arguments and data.
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
     * @exception IllegalArgumentException the length of the input data should
     *                                     be larger than 2.
     * @exception IllegalArgumentException the input level of significance
     *                         should be less than the largest one in the table.
     * @exception IllegalArgumentException the test statistic should be within
     *                                     the range of the table.
     */

    public SignRankTest(Hashtable argument,
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
                statisticalAnalysis = new SignRankTest(
                        (Double) argument.get(ALPHA),
                        ((Number) argument.get(NULL_VALUE)).doubleValue(),
                        (String) argument.get(SIDE),
                        (double[]) dataObject[0]);
            }
            else if (argument.get(NULL_VALUE) != null &&
                       argument.get(SIDE) != null &&
                       dataObject.length == 1)
            {
                statisticalAnalysis = new SignRankTest(
                        ((Number) argument.get(NULL_VALUE)).doubleValue(),
                        (String) argument.get(SIDE),
                        (double[]) dataObject[0]);
            }
            else if (argument.get(NULL_VALUE) != null &&
                       dataObject.length == 1)
            {
                statisticalAnalysis = new SignRankTest(
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
            statisticalAnalysis = new SignRankTest((double[]) dataObject[0]);
        }
        else if (dataObject == null)
        {
            statisticalAnalysis = new SignRankTest();
        }
        else
        {
            throw new IllegalArgumentException("Wrong input data.");
        }
    }

    /**
     * Constructs a one sample Wilcoxon sign rank test given the input data,
     * level of significance, value of median under test and alternative
     * hypothesis.
     * @param alpha the level of significance.
     * @param med the value of the median under test.
     * @param side the specification of the alternative hypothesis with the
     *             choices "greater", "less" or "equal" (or "two.sided").
     * @param data the input data.
     * @exception IllegalArgumentException the length of the input data should
     *                                     be larger than 2.
     * @exception IllegalArgumentException the input level of significance
     *                         should be less than the largest one in the table.
     * @exception IllegalArgumentException the test statistic should be within
     *                                     the range of the table.
     */

    public SignRankTest(double alpha,
                        double med,
                        String side,
                        double[] data)
    {
        this.alpha = alpha;
        this.med = med;
        this.side = side;
        this.data = data;
        testStatistic = testStatistic(med, data);
        tAlpha = tAlpha(alpha, med, data);
        pValue = pValue(med, side, data);
    }

    /**
     * Constructs a one sample Wilcoxon sign rank test with a 0.05 level of
     * significance given the input data, value of median under test and
     * alternative hypothesis.
     * @param med the value of the median under test.
     * @param side the specification of the alternative hypothesis with the
     *             choices "greater", "less" or "equal" (or "two.sided").
     * @param data the input data.
     * @exception IllegalArgumentException the length of the input data should
     *                                     be larger than 2.
     * @exception IllegalArgumentException the input level of significance
     *                         should be less than the largest one in the table.
     * @exception IllegalArgumentException the test statistic should be within
     *                                     the range of the table.
     */

    public SignRankTest(double med,
                        String side,
                        double[] data)
    {
        this(0.05, med, side, data);
    }

    /**
     * Constructs a two-tailed Wilcoxon sign rank test with a 0.05 level of
     * significance given the input data and value of the median under test.
     * @param med the value of the median under test.
     * @param data the input data.
     * @exception IllegalArgumentException the length of the input data should
     *                                     be larger than 2.
     * @exception IllegalArgumentException the input level of significance
     *                         should be less than the largest one in the table.
     * @exception IllegalArgumentException the test statistic should be within
     *                                     the range of the table.
     */

    public SignRankTest(double med,
                        double[] data)
    {
        this(0.05, med, "equal", data);
    }

    /**
     * Constructs a two-tailed Wilcoxon sign rank test with a 0.05 level of
     * significance given the input data and value of the median under test
     * equal to 0.
     * @param data the input data.
     * @exception IllegalArgumentException the length of the input data should
     *                                     be larger than 2.
     * @exception IllegalArgumentException the input level of significance
     *                         should be less than the largest one in the table.
     * @exception IllegalArgumentException the test statistic should be within
     *                                     the range of the table.
     */

    public SignRankTest(double[] data)
    {
        this(0.05, 0.0, "equal", data);
    }

    /**
     * The Wilcoxon sign rank statistic.
     * @param argument the argument with the following choices,
     * <br> NULL_VALUE: the null value;
     * <br> empty argument: the null value equal to 0.
     * <br><br>
     * @param dataObject the input data.
     * @return the value of the Wilcoxon sign rank statistic.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the length of the input data should
     *                                     be larger than 2.
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
     * The Wilcoxon sign rank statistic.
     * @param med the value of the median under test.
     * @param data the input data.
     * @return the value of the Wilcoxon sign rank statistic.
     * @exception IllegalArgumentException the length of the input data should
     *                                     be larger than 2.
     */

    public double testStatistic(double med,
                                double[] data)
    {
        this.med = med;
        this.data = data;
        dataManager = new DataManager();
        zeroNumber = (int) dataManager.zeroNumber(med, data);
        if ((data.length - zeroNumber) < 3)
        {
            throw new IllegalArgumentException(
                    "The length of the input data should be larger than 2.");
        }
        testStatistic = 0.0;
        tieNumber = dataManager.tieNumber(med, data);
        for (int i = 0; i < data.length; i++)
        {
            if ((data[i] - med) > 0)
            {
                rank = 1.0;
                for (int k = 0; k < data.length; k++)
                {
                    if ((k != i) &&
                        (Math.abs((data[i] - med)) > Math.abs((data[k] - med))))
                    {
                        rank += 1.0;
                    }
                }
                rank -= zeroNumber;
                rank += tieNumber[i] * (tieNumber[i] - 1.0) /
                        (2.0 * tieNumber[i]);
                testStatistic += rank;
            }
        }
        output.put(TEST_STATISTIC, new Double(testStatistic));

        return testStatistic;
    }

    /**
     * The Wilcoxon sign rank statistic with the null value equal to 0.
     * @param data the input data.
     * @return the value of the Wilcoxon sign rank statistic.
     * @exception IllegalArgumentException the length of the input data should
     *                                     be larger than 2.
     */

    public double testStatistic(double[] data)
    {
        return testStatistic(0.0, data);
    }

    /**
     * Calculates the selected constant such that the upper-tailed probability
     * for the null distribution of the test statistic is equal to the level of
     * significance.
     * @param argument the arguments with the following choices,
     * <br> ALPHA, NULL_VALUE: complete list of arguments;
     * <br> NULL_VALUE: default level of signifiance equal to 0.05;
     * <br> empty argument: defalut level of significance equal to 0.05
     *                      and the null value equal to 0.
     * <br><br>
     * @param dataObject the input data.
     * @return the value of the selected constant.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the input level of significance
     *                         should be less than the largest one in the table.
     */

    public Double tAlpha(Hashtable argument,
                         Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (argument.get(ALPHA) != null &&
            argument.get(NULL_VALUE) != null &&
            dataObject != null &&
            dataObject.length == 1)
        {
            tAlpha = tAlpha((Double) argument.get(ALPHA),
                            ((Number) argument.get(NULL_VALUE)).doubleValue(),
                            (double[]) dataObject[0]);
        }
        else if (argument.get(NULL_VALUE) != null &&
                 dataObject != null &&
                 dataObject.length == 1)
        {
            tAlpha = tAlpha(((Number) argument.get(NULL_VALUE)).doubleValue(),
                            (double[]) dataObject[0]);
        }
        else if (dataObject != null &&
                   dataObject.length == 1)
        {
            tAlpha = tAlpha((double[]) dataObject[0]);
        }
        else
        {
            throw new IllegalArgumentException(
                    "Wrong input arguments or data.");
        }

        return tAlpha;
    }

    /**
     * Calculates the selected constant such that the upper-tailed probability
     * for the null distribution of the test statistic is equal to alpha.
     * @param alpha the level of significance.
     * @param med the value of the median under test.
     * @param data the input data.
     * @return the value of the selected constant.
     * @exception IllegalArgumentException the input level of significance
     *                         should be less than the largest one in the table.
     */

    public double tAlpha(double alpha,
                         double med,
                         double[] data)
    {
        this.alpha = alpha;
        this.med = med;
        this.data = data;
        int index = 0;
        zeroNumber = (int)new DataManager().zeroNumber(med, data);
        if ((data.length - zeroNumber) > 30 ||
            (data.length - zeroNumber) < 3)
        {
            tAlpha = Double.NaN;
        }
        else
        {
            if (alpha > BasicStatistics.
                signRankTable[data.length - zeroNumber - 3][0])
            {
                throw new IllegalArgumentException(
                        "The input level of significance should be less than " +
                        BasicStatistics.signRankTable[data.length - 3][0] +
                        ".");
            }
            while (index < BasicStatistics.
                     signRankTable[data.length - zeroNumber - 3].length &&
                     alpha <= BasicStatistics.
                     signRankTable[data.length - zeroNumber - 3][index])
            {
                index += 1;
            }
            tAlpha = index + BasicStatistics.
                     signRankIndex[data.length - zeroNumber - 3] - 1;
        }
        output.put(TALPHA, tAlpha);

        return tAlpha;
    }

    /**
     * Calculates the selected constant such that the upper-tailed probability
     * for the null distribution of the test statistic is equal to 0.05.
     * @param med the value of the median under test.
     * @param data the input data.
     * @return the value of the selected constant.
     * @exception IllegalArgumentException the input level of significance
     *                         should be less than the largest one in the table.
     */

    public double tAlpha(double med,
                         double[] data)
    {
        return tAlpha(0.05, med, data);
    }

    /**
     * Calculates the selected constant such that the upper-tailed probability
     * for the null distribution of the test statistic is equal to 0.05 and the
     * null value is equal to 0.
     * @param data the input data.
     * @return the value of the selected constant.
     * @exception IllegalArgumentException the input level of significance
     *                         should be less than the largest one in the table.
     */

    public double tAlpha(double[] data)
    {
        return tAlpha(0.05, 0.0, data);
    }

    /**
     * The p value.
     * @param argument the arguments with the following choices,
     * <br> NULL_VALUE, SIDE: complete list of arguments;
     * <br> NULL_VALUE: two-sided alternative hypothesis;
     * <br> empty argument: two-sided alternative hypothesis and the null value
     *                      equal to 0.
     * <br><br>
     * @param dataObject the input data.
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
        if (argument.get(NULL_VALUE) != null &&
            argument.get(SIDE) != null &&
            dataObject != null &&
            dataObject.length == 1)
        {
            pValue = pValue(((Number) argument.get(NULL_VALUE)).doubleValue(),
                            (String) argument.get(SIDE),
                            (double[]) dataObject[0]);
        }
        else if (argument.get(NULL_VALUE) != null &&
                   dataObject != null &&
                   dataObject.length == 1)
        {
            pValue = pValue(((Number) argument.get(NULL_VALUE)).doubleValue(),
                            (double[]) dataObject[0]);
        }
        else if (dataObject != null &&
                   dataObject.length == 1)
        {
            pValue = pValue((double[]) dataObject[0]);
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
     * @param med the value of the median under test.
     * @param side the specification of the alternative hypothesis with the
     *             choices "greater", "less" or "equal" (or "two.sided").
     * @param data the input data.
     * @return the p value for the test.
     * @exception IllegalArgumentException the length of the input data should
     *                                     be larger than 2.
     * @exception IllegalArgumentException the test statistic should be within
     *                                     the range of the table.
     */

    public double pValue(double med,
                         String side,
                         double[] data)
    {
        this.med = med;
        this.side = side;
        this.data = data;
        zeroNumber = (int)new DataManager().zeroNumber(med, data);
        testStatistic = testStatistic(med, data);
        if ((data.length - zeroNumber) >= 30)
        {
            basicStatistics = new BasicStatistics();
            signRankMean = basicStatistics.signRankMean(med, data);
            signRankVariance = basicStatistics.signRankVariance(med, data);
            normalDistribution = new NormalDistribution();
            zStatistic = (testStatistic - signRankMean) /
                         Math.sqrt(signRankVariance);
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
        else
        {
            tableIndex = (int) testStatistic - BasicStatistics.
                         signRankIndex[data.length - zeroNumber - 3];
            rowLengthIndex = BasicStatistics.
                             signRankTable[data.length - zeroNumber - 3].length;
            if (side.equalsIgnoreCase("greater"))
            {
                if (tableIndex < 0)
                {
                    tableIndex = (int) ((data.length - zeroNumber) *
                                        (data.length + 1 - zeroNumber) / 2 -
                                        testStatistic) -
                                 BasicStatistics.
                                 signRankIndex[data.length - zeroNumber - 3];
                    if (tableIndex < 0)
                    {
                        lengthIndex = BasicStatistics.
                                      signRankTable[data.length - zeroNumber -
                                      3].
                                      length - 1;
                        pValue = BasicStatistics.
                                 signRankTable[data.length - zeroNumber - 3]
                                 [lengthIndex];
                    }
                    else
                    {
                        if (tableIndex >= rowLengthIndex)
                        {
                            pValue = 1 - BasicStatistics.
                                     signRankTable[data.length - zeroNumber - 3]
                                     [rowLengthIndex - 1];
                        }
                        else
                        {
                            pValue = 1 - BasicStatistics.
                                     signRankTable[data.length - zeroNumber - 3]
                                     [tableIndex];
                        }
                    }
                }
                else
                {
                    if (tableIndex >= rowLengthIndex)
                    {
                        pValue = BasicStatistics.
                                 signRankTable[data.length - zeroNumber - 3]
                                 [rowLengthIndex - 1];
                    }
                    else
                    {
                        pValue = BasicStatistics.
                                 signRankTable[data.length - zeroNumber - 3]
                                 [tableIndex];
                    }
                }
            }
            else if (side.equalsIgnoreCase("less"))
            {
                if (tableIndex < 0)
                {
                    tableIndex = (int) ((data.length - zeroNumber) *
                                        (data.length + 1 - zeroNumber) / 2 -
                                        testStatistic) -
                                 BasicStatistics.
                                 signRankIndex[data.length - zeroNumber - 3];
                    if (tableIndex < 0)
                    {
                        lengthIndex = BasicStatistics.
                                      signRankTable[data.length - zeroNumber -
                                      3].
                                      length - 1;
                        pValue = BasicStatistics.
                                 signRankTable[data.length - zeroNumber - 3]
                                 [lengthIndex];
                    }
                    else
                    {
                        if (tableIndex >= rowLengthIndex)
                        {
                            pValue = BasicStatistics.
                                     signRankTable[data.length - zeroNumber - 3]
                                     [rowLengthIndex - 1];
                        }
                        else
                        {
                            pValue = BasicStatistics.
                                     signRankTable[data.length - zeroNumber - 3]
                                     [tableIndex];
                        }
                    }
                }
                else
                {
                    if (tableIndex >= rowLengthIndex)
                    {
                        pValue = 1 - BasicStatistics.
                                 signRankTable[data.length - zeroNumber - 3]
                                 [rowLengthIndex - 1];
                    }
                    else
                    {
                        pValue = 1 - BasicStatistics.signRankTable
                                 [data.length - zeroNumber - 3][tableIndex];
                    }
                }
            }
            else
            {
                tableIndex = (int) Math.max(testStatistic,
                                            ((data.length - zeroNumber) *
                                             (data.length + 1 - zeroNumber) / 2)
                                            - testStatistic) - BasicStatistics.
                             signRankIndex[data.length - zeroNumber - 3];
                if (tableIndex < 0)
                {
                    lengthIndex = BasicStatistics.
                                  signRankTable[data.length - zeroNumber -
                                  3].length - 1;
                    pValue = 2 * BasicStatistics.
                             signRankTable[data.length - zeroNumber - 3]
                             [lengthIndex];
                }
                else
                {
                    if (tableIndex >= rowLengthIndex)
                    {
                        pValue = 2 * BasicStatistics.
                                 signRankTable[data.length - zeroNumber - 3]
                                 [rowLengthIndex - 1];
                    }
                    else
                    {
                        pValue = 2 * BasicStatistics.
                                 signRankTable[data.length - zeroNumber - 3]
                                 [tableIndex];
                    }
                }
            }
        }
        output.put(PVALUE, pValue);

        return pValue;
    }

    /**
     * The p value for a two-sided alternative hypothesis.
     * @param med the value of the median under test.
     * @param data the input data.
     * @return the p value for the test.
     * @exception IllegalArgumentException the length of the input data should
     *                                     be larger than 2.
     * @exception IllegalArgumentException the test statistic should be within
     *                                     the range of the table.
     */

    public double pValue(double med,
                         double[] data)
    {
        return pValue(med, "equal", data);
    }

    /**
     * The p value for a two-sided alternative hypothesis as the null value is
     * equal to 0.
     * @param data the input data.
     * @return the p value for the test.
     * @exception IllegalArgumentException the length of the input data should
     *                                     be larger than 2.
     * @exception IllegalArgumentException the test statistic should be within
     *                                     the range of the table.
     */

    public double pValue(double[] data)
    {
        return pValue(0.0, "equal", data);
    }

}
