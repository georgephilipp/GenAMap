package javastat.survival.inference;

/**
 * <p>Title: javastat</p>
 * <p>Description: JAVA programs for statistical computations</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Tung Hai University</p>
 * @author Wen Hsiang Wei
 * @version 1.4
 */

import java.util.*;

import javastat.survival.*;

/**
 *
 * <p>Calculates the Wilcoxon statistic and p-value for the comparison of two
 * groups of survival data.</p>
 * <p> </p>
 * <br> Example:
 * <br> double [] time1 = {156, 1040, 59, 329, 268, 638, 1106, 431, 855, 803,
 *                         115, 477, 448};
 * <br> double [] time2 = {421, 769, 365, 770, 1227, 475, 1129, 464, 1206, 563,
 *                         744, 353, 377};
 * <br> double [] censor1={1, 0, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0};
 * <br> double [] censor2={0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0};
 * <br>
 * <br> // Non-null constructor
 * <br> WilcoxonTest testclass1 =
 *        new WilcoxonTest(time1, censor1, time2, censor2);
 * <br> double testStatistic = testclass1.testStatistic;
 * <br> double pValue = testclass1.pValue;
 * <br>
 * <br> // Null constructor
 * <br> WilcoxonTest testclass2 = new WilcoxonTest();
 * <br> testStatistic =
 *        testclass2.testStatistic(time1, censor1, time2, censor2);
 * <br> pValue = testclass2.pValue(time1, censor1, time2, censor2);
 * <br>
 * <br> // Non-null constructor
 * <br> Hashtable argument = new Hashtable();
 * <br> StatisticalAnalysis testclass3 = LogRankTest(argument,
 * <br> &nbsp;&nbsp;&nbsp;
 *        time1, censor1, time2, censor2).statisticalAnalysis;
 * <br> testStatistic = (Double) testclass3.output.get(TEST_STATISTIC);
 * <br> pValue = (Double) testclass3.output.get(PVALUE);
 * <br>
 * <br> // Null constructor
 * <br> LogRankTest testclass4 = new LogRankTest(argument, null);
 * <br> testStatistic =
 *        testclass4.testStatistic(argument, time1, censor1, time2, censor2);
 * <br> pValue = testclass4.pValue(argument, time1, censor1, time2, censor2);
 * <br>
 * <br> // Obtains the information about the output
 * <br> out.println(testclass3.output.toString());
 * <br> out.println(testclass4.output.toString());
 */

public class WilcoxonTest extends SurvivalTestTemplate implements
        SurvivalTwoSampTestsInterface
{

    /**
     * Default WilcoxonTest constructor.
     */

    public WilcoxonTest() {}

    /**
     * Construct a Wilcoxon test given the survival times and censor indicators
     * in two groups.
     * @param argument the empty argument.
     * @param dataObject the survival times of the patients and censor
     *                   indicators for the patients in both groups.
     * @exception IllegalArgumentException wrong input data.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     in group 1 must have the same length.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     in group 2 must have the same length.
     * @exception IllegalArgumentException all elements of the time vector in
     *                                     group 1 must be postive.
     * @exception IllegalArgumentException all elements of the censor vector in
     *                                     group 1 must be either 1 or 0.
     * @exception IllegalArgumentException all elements of the time vector in
     *                                     group 2 must be postive.
     * @exception IllegalArgumentException all elements of the censor vector in
     *                                     group 2 must be either 1 or 0.
     */

    public WilcoxonTest(Hashtable argument,
                        Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null &&
            dataObject.length == 4)
        {
            statisticalAnalysis = new WilcoxonTest(
                    (double[]) dataObject[0], (double[]) dataObject[1],
                    (double[]) dataObject[2], (double[]) dataObject[3]);
        }
        else if (dataObject == null)
        {
            statisticalAnalysis = new WilcoxonTest();
        }
        else
        {
            throw new IllegalArgumentException("Wrong input data.");
        }
    }

    /**
     * Construct a Wilcoxon test given the survival times and censor indicators
     * in two groups.
     * @param time1 the survival times of the patients in group 1.
     * @param censor1 the censor indicators for the patients in group 1,
     * <br>           censor1[i]=1.0: death;
     * <br>           censor1[i]=0.0: censored.
     * @param time2 the survival times of the patients in group 2.
     * @param censor2 the censor indicators for the patients in group 2,
     * <br>           censor2[i]=1.0: death;
     * <br>           censor2[i]=0.0: censored.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     in group 1 must have the same length.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     in group 2 must have the same length.
     * @exception IllegalArgumentException all elements of the time vector in
     *                                     group 1 must be postive.
     * @exception IllegalArgumentException all elements of the censor vector in
     *                                     group 1 must be either 1 or 0.
     * @exception IllegalArgumentException all elements of the time vector in
     *                                     group 2 must be postive.
     * @exception IllegalArgumentException all elements of the censor vector in
     *                                     group 2 must be either 1 or 0.
     */

    public WilcoxonTest(double[] time1,
                        double[] censor1,
                        double[] time2,
                        double[] censor2)
    {
        super(time1, censor1, time2, censor2);
    }

    /**
     * The weigtht function.
     * @param parameter the parameter for the weight function.
     * @return the weight function evaluated at the value of the parameter.
     */

    public double weight(double parameter)
    {
        return parameter;
    }

}
