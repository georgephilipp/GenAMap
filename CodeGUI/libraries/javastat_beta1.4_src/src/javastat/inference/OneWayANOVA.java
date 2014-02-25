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
import static javastat.util.Output.*;
import javastat.util.*;

import JSci.maths.statistics.*;

/**
 *
 * <p>Calculates the F statistic and p-value for testing the equality of means
 * for three or more populations.</p>
 * <p> </p>
 * <br> Example:
 * <br> double [][] anovadata1 = {{6.0, 7.0, 6.0, 8.0}, {8.0, 9.0, 8.0, 10.0},
 *                                {13.0, 14.0, 15.0}};
 * <br> double [] anovadata21 = {85, 75, 82, 76, 71, 85};
 * <br> double [] anovadata22 = {71, 75, 73, 74, 69, 82};
 * <br> double [] anovadata23 = {59, 64, 62, 69, 75, 67};
 * <br>
 * <br> // Non-null constructor
 * <br> OneWayANOVA testclass1 = new OneWayANOVA(anovadata1);
 * <br> double testStatistic = testclass1.testStatistic;
 * <br> double pValue = testclass1.pValue;
 * <br>
 * <br> // Null constructor
 * <br> OneWayANOVA testclass2 = new OneWayANOVA();
 * <br> testStatistic = testclass2.
 *        testStatistic(anovadata21, anovadata22, anovadata23);
 * <br> pValue = testclass2.pValue(anovadata21, anovadata22, anovadata23);
 * <br>
 * <br> // Non-null constructor
 * <br> Hashtable argument = new Hashtable();
 * <br> StatisticalAnalysis testclass3 = new OneWayANOVA(argument,
 * <br> &nbsp;&nbsp;&nbsp;
 *        anovadata21, anovadata22, anovadata23).statisticalAnalysis;
 * <br> testStatistic = (Double) testclass3.output.get(TEST_STATISTIC);
 * <br> pValue = (Double) testclass3.output.get(PVALUE);
 * <br>
 * <br> // Null constructor
 * <br> OneWayANOVA testclass4 = new OneWayANOVA(argument, null);
 * <br> testStatistic = testclass4.testStatistic(argument, anovadata1);
 * <br> pValue = testclass4.pValue(argument, anovadata1);
 * <br>
 * <br> // Obtains the information about the output
 * <br> out.println(testclass3.output.toString());
 * <br> out.println(testclass4.output.toString());
 */

public class OneWayANOVA extends StatisticalInference
{
    /**
     * The F statistic for ANOVA.
     */

    public double testStatistic;

    /**
     * The p value.
     */

    public double pValue;

    /**
     * Within (Error) sum of squares.
     */

    public double ssWithin;

    /**
     * Between (Treatment) sum of squares.
     */

    public double ssBetween;

    /**
     * The degrees of freedom of between sum of squares and within sum of
     * squares,
     * <br> degreeFreedom[0]: the degree of freedom of between sum of squares;
     * <br> degreeFreedom[1]: the degree of freedom of within sum of squares.
     */

    public double [] degreeFreedom;

    /**
     * The input data,
     * <br> data[j][]: the data from (j+1)'th population (treatment).
     */

    public double [][] data;

    /**
     * The object represents a one-way analysis of variance.
     */

    public StatisticalAnalysis statisticalAnalysis;

    /**
     * The sample size.
     */

    private double sampleSize;

    /**
     * The vector of sample means.
     */

    private double [] meanVector;

    /**
     * The sample variance.
     */

    private double variance;

    /**
     * The sum of data values.
     */

    private double dataSum;

    /**
     * The class contains the collections of some basic methods used in
     * statistical computations.
     */

    private BasicStatistics basicStatistics;

    /**
     * Constructs a F test for testing the equality of means for three or
     * more populations.
     */

    public OneWayANOVA(){}

    /**
     * Constructs a F test with the input argument and data.
     * @param argument the empty argument.
     * @param dataObject the input data.
     * @exception IllegalArgumentException wrong input data.
     */

    public OneWayANOVA(Hashtable argument,
                       Object... dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null &&
            dataObject.getClass().getName().equalsIgnoreCase("[[D"))
        {
            statisticalAnalysis = new OneWayANOVA((double[][]) dataObject);
        }
        else if (dataObject!=null &&
                 dataObject.getClass().getName().
                 equalsIgnoreCase("[Ljava.lang.Object;"))
        {
            statisticalAnalysis =
                    new OneWayANOVA(DataManager.castDoubleObject(0,dataObject));
        }
        else if (dataObject == null)
        {
            statisticalAnalysis = new OneWayANOVA();
        }
        else
        {
            throw new IllegalArgumentException("Wrong input data.");
        }
    }

    /**
     * Constructs a F test with the input data.
     * @param data the input data,
     * <br>        data[j][]: the data from (j+1)'th population (treatment).
     */

    public OneWayANOVA(double[]... data)
    {
        this.data = data;
        degreeFreedom = degreeFreedom(data);
        testStatistic = testStatistic(data);
        pValue = pValue(data);
    }

    /**
     * The degrees of freedom of between sum of squares and within sum of
     * squares.
     * @param argument the empty argument.
     * @param dataObject the input data.
     * @return the degree of freedom,
     * <br>    degreeFreedom[0]: the degree of freedom of between sum of
     *                           squares;
     * <br>    degreeFreedom[1]: the degree of freedom of within sum of squares.
     * @exception IllegalArgumentException wrong input arguments or data.
     */

    public double [] degreeFreedom(Hashtable argument,
                                   Object... dataObject)
    {
       if (dataObject!=null &&
           dataObject.getClass().getName().equalsIgnoreCase("[[D"))
       {
           degreeFreedom = degreeFreedom((double[][]) dataObject);
       }
       else if (dataObject!=null &&
                dataObject.getClass().getName().
                equalsIgnoreCase("[Ljava.lang.Object;"))
       {
           degreeFreedom = degreeFreedom(DataManager.
                                         castDoubleObject(0, dataObject));
       }
       else
       {
           throw new IllegalArgumentException("Wrong input arguments or data.");
       }

       return degreeFreedom;
    }

    /**
     * The degrees of freedom of between sum of squares and within sum of
     * squares.
     * @param data the input data,
     * <br>        data[j][]: the data from (j+1)'th population (treatment).
     * @return the degree of freedom,
     * <br>    degreeFreedom[0]: the degree of freedom of between sum of
     *                           squares;
     * <br>    degreeFreedom[1]: the degree of freedom of within sum of squares.
     */

    public double [] degreeFreedom(double[]... data)
    {
        this.data = data;
        degreeFreedom = new double[2];
        sampleSize = new DataManager().sampleSize(data);
        degreeFreedom[0] = data.length - 1.0;
        degreeFreedom[1] = sampleSize - data.length;
        output.put(javastat.util.Output.DEGREE_OF_FREEDOM, degreeFreedom);

        return degreeFreedom;
    }

    /**
     * The F statistic for testing the equality of means for three or more
     * populations.
     * @param argument the empty argument.
     * @param dataObject the input data.
     * @return the value of the F statistic.
     * @exception IllegalArgumentException wrong input arguments or data.
     */

    public Double testStatistic(Hashtable argument,
                                Object... dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null &&
            dataObject.getClass().getName().equalsIgnoreCase("[[D"))
        {
            testStatistic = testStatistic((double[][]) dataObject);
        }
        else if (dataObject != null &&
                 dataObject.getClass().getName().
                 equalsIgnoreCase("[Ljava.lang.Object;"))
        {
            testStatistic = testStatistic(DataManager.
                                          castDoubleObject(0, dataObject));
        }
        else
        {
            throw new IllegalArgumentException(
                    "Wrong input arguments or data.");
        }

        return testStatistic;
    }

    /**
     * The F statistic for testing the equality of means for three or more
     * populations.
     * @param data the input data,
     * <br>        data[j][]: the data from (j+1)'th population (treatment).
     * @return the value of the F statistic.
     */

    public double testStatistic(double[]... data)
    {
        this.data = data;
        basicStatistics = new BasicStatistics();
        meanVector = basicStatistics.meanVector(data);
        sampleSize = new DataManager().sampleSize(data);
        dataSum = 0.0;
        ssWithin = 0.0;
        ssBetween = 0.0;
        for (int j = 0; j < data.length; j++)
        {
            ssBetween += (data[j].length) * Math.pow(meanVector[j], 2.0);
            variance = basicStatistics.variance(data[j]);
            ssWithin += (data[j].length - 1) * variance;
            dataSum += meanVector[j] * data[j].length;
        }
        ssBetween -= (Math.pow(dataSum, 2.0) / sampleSize);
        testStatistic = (ssBetween*(sampleSize - data.length)) /
            (ssWithin * (data.length - 1.0));
        output.put(TEST_STATISTIC, testStatistic);

        return testStatistic;
    }

    /**
     * The p value.
     * @param argument the empty argument.
     * @param dataObject the input data.
     * @return the p value for the test.
     * @exception IllegalArgumentException wrong input arguments or data.
     */

    public Double pValue(Hashtable argument,
                         Object... dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null &&
            dataObject.getClass().getName().equalsIgnoreCase("[[D"))
        {
            pValue = pValue((double[][]) dataObject);
        }
        else if (dataObject != null &&
                 dataObject.getClass().getName().
                 equalsIgnoreCase("[Ljava.lang.Object;"))
        {
            pValue = pValue(DataManager.castDoubleObject(0, dataObject));
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
     * @param data the input data,
     * <br>        data[j][]: the data from (j+1)'th population (treatment).
     * @return the p value for the test.
     */

    public double pValue(double[]... data)
    {
        testStatistic = testStatistic(data);
        sampleSize = new DataManager().sampleSize(data);
        pValue = 1 - new FDistribution(data.length - 1.0,
            sampleSize - data.length).cumulative(testStatistic);
        output.put(PVALUE, pValue);

        return pValue;
    }

}


