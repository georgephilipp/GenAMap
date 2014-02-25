package javastat;

/**
 * <p>Title: javastat</p>
 * <p>Description: JAVA programs for statistical computations</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Tung Hai University</p>
 * @author Wen Hsiang Wei
 * @version 1.4
 */

import java.util.*;

import javastat.inference.*;
import javastat.inference.nonparametric.*;
import javastat.inference.onesample.*;
import javastat.inference.twosamples.*;
import javastat.survival.inference.*;
import static javastat.util.Argument.*;
import static javastat.util.TestType.*;

/**
 *
 * <p>This class provides a variety of statistical tests. </p>
 * <p> </p>
 * <br> Example:
 * <br> double [] oneSampMeanTestData = {7, 8, 10, 8, 6, 9, 6, 7, 7, 8, 9, 8};
 * <br> double [] twoSampMeansTestData1 = {300, 280, 344, 385, 372, 360, 288,
 *                                         321, 376, 290, 301, 283};
 * <br> double [] twoSampMeansTestData2 = {276, 222, 310, 338, 200, 302, 317,
 *                                         260, 320, 312, 334, 265};
 * <br>String [] colvar = {"M", "F", "M", "M", "M", "F", "F", "M", "F", "M",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                           "F", "F", "M", "F", "M", "M", "F", "F", "M", "F",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                           "M", "F", "F", "F", "F", "F", "M", "F", "M", "F",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                           "F", "M", "M", "F", "M", "F", "F", "F", "M", "F",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                           "F", "F", "M", "M", "F", "F", "F", "M", "F", "F"};
 * <br> String [] rowvar = {"E", "A", "R", "E", "E", "A", "A", "A", "A"," E",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                          "E", "A", "A", "A", "R", "R", "A", "A", "A", "E",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                          "R", "R", "E", "A", "A", "A", "R", "E", "A", "R",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            "R", "R", "R", "A", "R", "A", "E", "A", "R", "A",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            "E", "R", "E", "R", "A", "A", "R", "E", "E", "A"};
 * <br> double [][] anovaData = {{6.0, 7.0, 6.0, 8.0}, {8.0, 9.0, 8.0, 10.0},
 *                               {13.0, 14.0, 15.0}};
 * <br> double [] time1 = {156, 1040, 59, 329, 268, 638, 1106, 431, 855, 803,
 *                         115, 477, 448};
 * <br> double [] time2 = {421, 769, 365, 770, 1227, 475, 1129, 464, 1206, 563,
 *                         744, 353, 377};
 * <br> double [] censor1 = {1, 0, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0};
 * <br> double [] censor2 = {0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0};
 * <br>
 * <br> Hashtable argument=new Hashtable();
 * <br>
 * <br> // One-sample mean z test
 * <br> StatisticalAnalysis statisticalAnalysis1 =  StatisticalTests(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument, oneSampMeanTestData).statisticalAnalysis;
 * <br>
 * <br> // Two-sample means z test
 * <br> StatisticalAnalysis statisticalAnalysis2 = new StatisticalTests(
 * <br> &nbsp;&nbsp;&nbsp;
 *         argument, twoSampMeansTestData1, twoSampMeansTestData2).
 *         statisticalAnalysis;
 * <br>
 * <br> // One-sample proportion test
 * <br> StatisticalAnalysis statisticalAnalysis3 = new StatisticalTests(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument, 30, 100).statisticalAnalysis;
 * <br>
 * <br> // Two-sample proportions test
 * <br> StatisticalAnalysis statisticalAnalysis4 = new StatisticalTests(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument, 36, 150, 30, 100).statisticalAnalysis;
 * <br>
 * <br> // Chi-square test
 * <br> StatisticalAnalysis statisticalAnalysis5 = new StatisticalTests(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument, rowvar, colvar).statisticalAnalysis;
 * <br>
 * <br> // One-way ANOVA
 * <br> StatisticalAnalysis statisticalAnalysis6 = new StatisticalTests(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument, anovaData).statisticalAnalysis;
 * <br>
 * <br> // Log-rank test
 * <br> StatisticalAnalysis statisticalAnalysis7 = new StatisticalTests(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument, time1, censor1, time2, censor2).statisticalAnalysis;
 * <br>
 * <br> // One-sample mean t test
 * <br> argument.put(TEST_TYPE, "T");
 * <br> StatisticalAnalysis statisticalAnalysis8 = new StatisticalTests(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument, oneSampMeanTestData).statisticalAnalysis;
 * <br>
 * <br> // Wilcoxon sign-rank test
 * <br> argument.put(TEST_TYPE, "SignRank");
 * <br> StatisticalAnalysis statisticalAnalysis9 = new StatisticalTests(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument, oneSampMeanTestData).statisticalAnalysis;
 * <br>
 * <br> // Two-sample means t test
 * <br> argument.put(TEST_TYPE, "T");
 * <br> StatisticalAnalysis statisticalAnalysis10 = new StatisticalTests(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument, twoSampMeansTestData1, twoSampMeansTestData2).
 *        statisticalAnalysis;
 * <br>
 * <br> // Wilcoxon rank-sum test
 * <br> argument.put(TEST_TYPE, "RankSum");
 * <br> StatisticalAnalysis statisticalAnalysis11 = new StatisticalTests(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument, twoSampMeansTestData1, twoSampMeansTestData2).
 *        statisticalAnalysis;
 * <br>
 * <br> // Matched-sample means t test
 * <br> argument.put(TEST_TYPE, "Paired T");
 * <br> StatisticalAnalysis statisticalAnalysis12 = new StatisticalTests(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument, twoSampMeansTestData1, twoSampMeansTestData2).
 *        statisticalAnalysis;
 * <br>
 * <br> // Matched-sample means z test
 * <br> argument.put(TEST_TYPE, "Paired Z");
 * <br> StatisticalAnalysis statisticalAnalysis13 = new StatisticalTests(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument, twoSampMeansTestData1, twoSampMeansTestData2).
 *        statisticalAnalysis;
 * <br>
 * <br> // Wilcoxon test for survival data
 * <br> argument.put(TEST_TYPE, "Wilcoxon");
 * <br> StatisticalAnalysis statisticalAnalysis14 = new StatisticalTests(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument, time1, censor1, time2, censor2).statisticalAnalysis;
 */

public class StatisticalTests extends StatisticalAnalysis
{
    /**
     * The object represents a statistical test.
     */

    public StatisticalAnalysis statisticalAnalysis;

    /**
     * Default StatisticalTests constructor.
     */

    public StatisticalTests() {}

    /**
     * The test of interest.
     * @param argument the argument with the following choices,
     * <br> TEST_TYPE: the enum in the class TestType or the choices
     *                 "WILCOXON", "SIGNRANK", "RANKSUM", "Paired_Z","Paired_T",
     *                 "T";
     * <br> empty argument: default tests, including  one-sample mean z test,
     *                      two-sample means z test, one-sample proportion test,
     *                      two-sample proportions test, chi-square test,
     *                      one-way ANOVA, or log-rank test.
     * <br><br>
     * @param dataObject the input data.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException no input data.
     */

    public StatisticalTests(Hashtable argument,
                            Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null)
        {
            if (dataObject.length == 4 &&
                dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
                dataObject[1].getClass().getName().equalsIgnoreCase("[D") &&
                dataObject[2].getClass().getName().equalsIgnoreCase("[D") &&
                dataObject[3].getClass().getName().equalsIgnoreCase("[D"))

            {
                if (argument.get(TEST_TYPE) != null &&
                    argument.get(TEST_TYPE).toString().
                    equalsIgnoreCase("Wilcoxon"))
                {
                    argument.remove(TEST_TYPE);
                    statisticalAnalysis = new WilcoxonTest(argument,
                            dataObject).statisticalAnalysis;
                }
                else if (dataObject[0].getClass().getName().
                         equalsIgnoreCase("[D") &&
                         dataObject[1].getClass().getName().
                         equalsIgnoreCase("[D") &&
                         dataObject[2].getClass().getName().
                         equalsIgnoreCase("[D") &&
                         dataObject[3].getClass().getName().
                         equalsIgnoreCase("[D"))
                {
                    if (argument.get(TEST_TYPE) != null)
                    {
                        argument.remove(TEST_TYPE);
                    }
                    statisticalAnalysis = new LogRankTest(argument,
                            dataObject).statisticalAnalysis;
                }
                else
                {
                    throw new IllegalArgumentException(
                            "Wrong input arguments or data.");
                }
            }
            else if (dataObject.length == 4 &&
                     dataObject[0].getClass().getSuperclass().toString().
                     equalsIgnoreCase("class java.lang.Number") &&
                     dataObject[1].getClass().getSuperclass().toString().
                     equalsIgnoreCase("class java.lang.Number") &&
                     dataObject[2].getClass().getSuperclass().toString().
                     equalsIgnoreCase("class java.lang.Number") &&
                     dataObject[3].getClass().getSuperclass().toString().
                     equalsIgnoreCase("class java.lang.Number"))
            {
                statisticalAnalysis = new TwoSampProps(argument,
                        dataObject).statisticalAnalysis;
            }
            else if (dataObject.length == 2 &&
                     dataObject[0].getClass().getName().
                     equalsIgnoreCase("[Ljava.lang.String;") &&
                     dataObject[1].getClass().getName().
                     equalsIgnoreCase("[Ljava.lang.String;"))
            {
                if (argument.get(TEST_TYPE) != null)
                {
                    argument.remove(TEST_TYPE);
                }
                statisticalAnalysis = new ChisqTest(argument,
                        dataObject).statisticalAnalysis;
            }
            else if (dataObject.length == 2 &&
                     dataObject[0].getClass().getName().
                     equalsIgnoreCase("[D") &&
                     dataObject[1].getClass().getName().equalsIgnoreCase("[D"))
            {
                if (argument.get(TEST_TYPE) != null &&
                    !(argument.get(TEST_TYPE).toString().equalsIgnoreCase("Z")))
                {
                    if (argument.get(TEST_TYPE).toString().
                        equalsIgnoreCase("RankSum"))
                    {
                        argument.remove(TEST_TYPE);
                        statisticalAnalysis = new RankSumTest(argument,
                                dataObject).statisticalAnalysis;
                    }
                    else if (argument.get(TEST_TYPE).toString().
                             equalsIgnoreCase("Paired Z") ||
                             argument.get(TEST_TYPE).toString().
                             equalsIgnoreCase("Paired_Z"))
                    {
                        argument.remove(TEST_TYPE);
                        statisticalAnalysis = new MatchedSampMeansZTest(
                                argument, dataObject).statisticalAnalysis;
                    }
                    else if (argument.get(TEST_TYPE).toString().
                             equalsIgnoreCase("Paired T") ||
                             argument.get(TEST_TYPE).toString().
                             equalsIgnoreCase("Paired_T"))
                    {
                        argument.remove(TEST_TYPE);
                        statisticalAnalysis = new MatchedSampMeansTTest(
                                argument, dataObject).statisticalAnalysis;
                    }
                    else if (argument.get(TEST_TYPE).toString().
                               equalsIgnoreCase("T"))
                    {
                        argument.remove(TEST_TYPE);
                        statisticalAnalysis = new TwoSampMeansTTest(argument,
                                dataObject).statisticalAnalysis;
                    }
                    else
                    {
                        throw new IllegalArgumentException(
                                "Wrong input arguments or data.");
                    }
                }
                else
                {
                    statisticalAnalysis = new TwoSampMeansZTest(argument,
                            dataObject).statisticalAnalysis;
                }
            }
            else if (dataObject.length == 2 &&
                     dataObject[0].getClass().getSuperclass().toString().
                     equalsIgnoreCase("class java.lang.Number") &&
                     dataObject[1].getClass().getSuperclass().toString().
                     equalsIgnoreCase("class java.lang.Number"))
            {
                if (argument.get(TEST_TYPE) != null)
                {
                    argument.remove(TEST_TYPE);
                }
                statisticalAnalysis = new OneSampProp(argument,
                        dataObject).statisticalAnalysis;
            }
            else if (dataObject.length == 1 &&
                     dataObject.getClass().getName().equalsIgnoreCase(
                     "[Ljava.lang.Object;"))
            {
                if (argument.get(TEST_TYPE) != null &&
                    !(argument.get(TEST_TYPE).toString().equalsIgnoreCase("Z")))
                {
                    if (argument.get(TEST_TYPE).toString().
                        equalsIgnoreCase("SignRank"))
                    {
                        argument.remove(TEST_TYPE);
                        statisticalAnalysis = new SignRankTest(argument,
                                dataObject).statisticalAnalysis;
                    }
                    else if (argument.get(TEST_TYPE).toString().
                               equalsIgnoreCase("T"))
                    {
                        argument.remove(TEST_TYPE);
                        statisticalAnalysis = new OneSampMeanTTest(argument,
                                dataObject).statisticalAnalysis;
                    }
                    else
                    {
                        throw new IllegalArgumentException(
                                "Wrong input arguments or data.");
                    }
                }
                else
                {
                    if (argument.get(TEST_TYPE) != null)
                    {
                        argument.remove(TEST_TYPE);
                    }
                    statisticalAnalysis = new OneSampMeanZTest(argument,
                            dataObject).statisticalAnalysis;
                }
            }
            else if (dataObject.getClass().getName().equalsIgnoreCase("[[D"))
            {
                if (argument.get(TEST_TYPE) != null &&
                    !(argument.get(TEST_TYPE).toString().
                      equalsIgnoreCase("OneWayANOVA") ||
                    argument.get(TEST_TYPE).toString().
                    equalsIgnoreCase("ONE_WAY_ANOVA")))
                {
                    if (argument.get(TEST_TYPE).toString().
                        equalsIgnoreCase("CHISQUARE"))
                    {
                        argument.remove(TEST_TYPE);
                        statisticalAnalysis = new ChisqTest(argument,
                                dataObject).statisticalAnalysis;
                    }
                    else
                    {
                        throw new IllegalArgumentException(
                                "Wrong input arguments or data.");
                    }
                }
                else
                {
                    if (argument.get(TEST_TYPE) != null)
                    {
                        argument.remove(TEST_TYPE);
                    }
                    statisticalAnalysis = new OneWayANOVA(argument,
                            dataObject).statisticalAnalysis;
                }
            }
            else
            {
                throw new IllegalArgumentException(
                        "Wrong input arguments or data.");
            }
        }
        else
        {
            throw new IllegalArgumentException("No input data.");
        }
    }

}

