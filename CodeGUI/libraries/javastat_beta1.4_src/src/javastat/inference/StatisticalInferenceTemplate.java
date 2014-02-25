package javastat.inference;

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
import static javastat.util.Argument.*;
import static javastat.util.Output.*;

/**
 * The class defines the required methods for the sub-classes and implements the
 * methods of statistical inference for computing the common statistical
 * quantities, such as confidence interval, test statistic and p-value.
 */

public abstract class StatisticalInferenceTemplate extends StatisticalAnalysis
{

    /**
     * Default StatisticalInferenceTemplate constructor.
     */

    public StatisticalInferenceTemplate() {}

    /**
     * The point estimate.
     */

    public double pointEstimate;

    /**
     * The standard error of the point estimate.
     */

    public double pointEstimateSE;

    /**
     * The critical value.
     */

    public double criticalValue;

    /**
     * The null value.
     */

    public double nullValue;

    /**
     * The specification of the alternative hypothesis with the choices
     * "greater", "less" or "equal" (or "two.sided").
     */

    public String side;

    /**
     * The cumulative distribution function.
     */

    public double cdf;

    /**
     * The confidence interval.
     */

    public double [] confidenceInterval;

    /**
     * The test statistic.
     */

    public double testStatistic;

    /**
     * The p-value.
     */

    public double pValue;

    /**
     * The index for the specification of the alternative hypothesis..
     */

    private double [] sideIndex;

    /**
     * The abstract method (need to be implemented in sub-classes) for computing
     * the point estimate.
     * @param argument the arguments.
     * @param dataObject the input data.
     * @return the point estimate.
     */

    public abstract Object pointEstimate(Hashtable argument,
                                         Object... dataObject);

    /**
     * The abstract method (need to be implemented in sub-classes) for computing
     * the standard error of the point estimate.
     * @param argument the arguments.
     * @param dataObject the input data.
     * @return the standard error of the point estimate.
     */

    public abstract Object pointEstimateSE(Hashtable argument,
                                           Object... dataObject);

    /**
     * The confidence interval.
     * @param argument the argument: CRITICAL_VALUE.
     * @param dataObject the input data.
     * @return the confidence interval.
     */

    public Object confidenceInterval(Hashtable argument,
                                     Object... dataObject)
    {
        criticalValue = ((Double) argument.get(CRITICAL_VALUE)).doubleValue();
        pointEstimate = ((Double) this.pointEstimate(argument, dataObject))
            .doubleValue();
        pointEstimateSE = ((Double) this.pointEstimateSE(argument, dataObject))
            .doubleValue();
        confidenceInterval = new double[]{pointEstimate -
            criticalValue * pointEstimateSE,
            pointEstimate + criticalValue * pointEstimateSE};
        output.put(CONFIDENCE_INTERVAL, confidenceInterval);

        return confidenceInterval;
    }

    /**
     * The test statistic.
     * @param argument the argument: NULL_VALUE.
     * @param dataObject the input data.
     * @return the test statistic.
     */

    public Object testStatistic(Hashtable argument,
                                Object... dataObject)
    {
        nullValue = ((Double) argument.get(NULL_VALUE)).doubleValue();
        pointEstimate = ((Double) this.pointEstimate(argument, dataObject))
            .doubleValue();
        pointEstimateSE = ((Double) this.pointEstimateSE(argument, dataObject))
            .doubleValue();
        testStatistic = (pointEstimate - nullValue) / pointEstimateSE;
        output.put(TEST_STATISTIC, new Double(testStatistic));

        return testStatistic;
    }

    /**
     * The p-value for the test statistic.
     * @param argument the arguments: SIDE, CDF.
     * @return the p-value.
     */

    public double pValue(Hashtable argument)
    {
        side = (String) argument.get(SIDE);
        cdf = ((Double) argument.get(CDF)).doubleValue();
        if (side.equalsIgnoreCase("less"))
        {
            sideIndex = new double[] {0.0, 1.0};
        }
        else if (side.equalsIgnoreCase("greater"))
        {
            sideIndex = new double[] {0.0, 0.0};
        }
        else
        {
            sideIndex = new double[] {1.0, 0.0};
        }

        return pValue(sideIndex, cdf);
    }

    /**
     * The p-value for the test statistic.
     * @param sideIndex the vector associated with the specification of
     * the alternative hypothesis,
     * <br> {0,1}: less;
     * <br> {0,0}: greater;
     * <br> {1,0}: two-sided.
     * @param cdf the cumulative distribution.
     * @return the p-value.
     */

    public double pValue(double[] sideIndex,
                         double cdf)
    {
        pValue=sideIndex[0] * 2 * (1 - Math.max(cdf, 1.0 - cdf)) +
            sideIndex[1] * cdf + (1.0 - sideIndex[0] -
            sideIndex[1]) * (1.0 - cdf);
        output.put(PVALUE, new Double(pValue));

        return pValue;
    }

}


