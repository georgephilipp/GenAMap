package javastat.eda;

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
import javastat.util.*;

/**
 *
 * <p>Constructs a frequency table and obtains a five-number summary for
 * quantitative data.</p>
 * <p> </p>
 * <br> Example:
 * <br> double [] testdata1 = {2350, 2450, 2550, 2380, 2255, 2210,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                             2390, 2630, 2440, 2825, 2420, 2380};
 * <br> String[] printOut = {"Min.", "Q1  ","Q2  ","Q3  ","Max."};
 * <br>
 * <br> // Non-null constructor
 * <br> QuantitativeDataAnalysis testclass1 =
 *        new QuantitativeDataAnalysis(3, testdata1);
 * <br> double [] fiveNumberSummary = testclass1.fiveNumberSummary;
 * <br> double [][] frequencyTable = testclass1.frequencyTable;
 * <br>
 * <br> // Null constructor
 * <br> QuantitativeDataAnalysis testclass2 = new QuantitativeDataAnalysis();
 * <br> fiveNumberSummary = testclass2.fiveNumberSummary(testdata1);
 * <br> frequencyTable = testclass2.frequencyTable(4, testdata1);
 * <br>
 * <br> // Non-null constructor
 * <br> Hashtable argument = new Hashtable();
 * <br> argument.put(NUMBER_OF_CLASS, 3);
 * <br> StatisticalAnalysis testclass3 =
 * <br> &nbsp;&nbsp;&nbsp;
 *        new QuantitativeDataAnalysis(argument, testdata1).statisticalAnalysis;
 * <br> fiveNumberSummary = (double[]) testclass3.output.
 *        get(FIVE_NUMBER_SUMMARY);
 * <br> frequencyTable = (double[][]) testclass3.output.get(FREQUENCY_TABLE);
 * <br>
 * <br> // Null constructor
 * <br> QuantitativeDataAnalysis testclass4 =
 *        new QuantitativeDataAnalysis(argument, null);
 * <br> fiveNumberSummary = testclass4.fiveNumberSummary(argument, testdata1);
 * <br> frequencyTable = testclass4.frequencyTable(argument, testdata1);
 * <br>
 * <br> // Obtains the information about the output
 * <br> out.println(testclass3.output.toString());
 * <br> out.println(testclass4.output.toString());
 */

public class QuantitativeDataAnalysis extends StatisticalAnalysis
{
    /**
     * The five number summary,
     * <br> fiveNumberSummary[0]: the minimum;
     * <br> fiveNumberSummary[1]: the first quartile;
     * <br> fiveNumberSummary[2]: the median;
     * <br> fiveNumberSummary[3]: the third quartile;
     * <br> fiveNumberSummary[4]: the maximum;
     */

    public double[] fiveNumberSummary;

    /**
     * The frequency table,
     * <br> frequencyTable[0][i]: the data value of the i'th class;
     * <br> frequencyTable[1][i]: the frequency corresponding to the i'th class.
     */

    public double[][] frequencyTable;

    /**
     * The number of classes.
     */

    public int nClass;

    /**
     * The input data.
     */

    public double[] data;

    /**
     * The width of a class.
     */

    public double classWidth;

    /**
     * The object represents a quantitative data analysis.
     */

    public StatisticalAnalysis statisticalAnalysis;

    /**
     * The number of data in a class.
     */

    private int counts;

    /**
     * The index indicating the starting position of the first data in a class.
     */

    private int startIndex;

    /**
     * The copy of input data.
     */

    private double[] copy;

    /**
     * Default QuantitativeDataAnalysis constructor.
     */

    public QuantitativeDataAnalysis() {}

    /**
     * Constructs a class given the specified number of classes for quantitative
     * data analysis.
     * @param argument the argument: NUMBER_OF_CLASS, default value equal to 4.
     * @param dataObject the input data.
     * @exception IllegalArgumentException wrong input data.
     */

    public QuantitativeDataAnalysis(Hashtable argument,
                                    Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (argument.size() > 0 &&
            dataObject != null)
        {
            if (argument.get(NUMBER_OF_CLASS) != null &&
                dataObject.length == 1)
            {
                statisticalAnalysis = new QuantitativeDataAnalysis(
                        (Integer) argument.get(NUMBER_OF_CLASS),
                        (double[]) dataObject[0]);
            }
            else
            {
                throw new IllegalArgumentException("Wrong input data.");
            }
        }
        else if (dataObject != null &&
                 dataObject.length == 1)
        {
            statisticalAnalysis = new QuantitativeDataAnalysis(
                    (double[]) dataObject[0]);
        }
        else if (dataObject == null)
        {
            statisticalAnalysis = new QuantitativeDataAnalysis();
        }
        else
        {
            throw new IllegalArgumentException("Wrong input data.");
        }
    }

    /**
     * Constructs a class with the specified number of classes for quantitative
     * data analysis.
     * @param nClass the number of classes used in a frequency distribution.
     * @param data the input data.
     */

    public QuantitativeDataAnalysis(int nClass,
                                    double[] data)
    {
        this.nClass = nClass;
        this.data = data;
        fiveNumberSummary = fiveNumberSummary(data);
        frequencyTable = frequencyTable(nClass, data);
    }

    /**
     * Constructs a class for quantitative data analysis with default number of
     * classes equal to 4.
     * @param data the input data.
     */

    public QuantitativeDataAnalysis(double[] data)
    {
        this(4, data);
    }

    /**
     * Calculates the five number summary, including the miminum, first
     * quartile, median, thrid quartile, maximun.
     * @param argument the empty argument.
     * @param dataObject the input data.
     * @return the five number summary.
     * @exception IllegalArgumentException wrong input arguments or data.
     */

    public double[] fiveNumberSummary(Hashtable argument,
                                      Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null &&
            dataObject.length == 1)
        {
            fiveNumberSummary = fiveNumberSummary((double[]) dataObject[0]);
        }
        else
        {
            throw new IllegalArgumentException(
                    "Wrong input arguments or data.");
        }

        return fiveNumberSummary;
    }

    /**
     * Calculates the five number summary, including the miminum, first
     * quartile, median, thrid quartile, maximun.
     * @param data the input data.
     * @return the five number summary.
     */

    public double[] fiveNumberSummary(double[] data)
    {
        this.data = data;
        fiveNumberSummary = new BasicStatistics().fiveNumberSummary(data);
        output.put(FIVE_NUMBER_SUMMARY, fiveNumberSummary);

        return fiveNumberSummary;
    }

    /**
     * Calculates the frequency distribution for the input data.
     * @param argument the argument: NUMBER_OF_CLASS, default value equal to 4.
     * @param dataObject the input data.
     * @return the frequency distribution.
     * @exception IllegalArgumentException wrong input arguments or data.
     */

    public double[][] frequencyTable(Hashtable argument,
                                     Object ...dataObject)
    {

        if (argument.get(NUMBER_OF_CLASS) != null &&
            dataObject != null &&
            dataObject.length == 1)
        {
            frequencyTable = frequencyTable(
                    (Integer) argument.get(NUMBER_OF_CLASS),
                    (double[]) dataObject[0]);
        }
        else if (dataObject != null &&
                 dataObject.length == 1)
        {
            frequencyTable = frequencyTable((double[]) dataObject[0]);
        }
        else
        {
            throw new IllegalArgumentException(
                    "Wrong input arguments or data.");
        }

        return frequencyTable;
    }

    /**
     * Calculates the frequency distribution for the input data.
     * @param nClass the number of classes used in a frequency distribution.
     * @param data the input data.
     * @return the frequency distribution.
     * @exception IllegalArgumentException the number of classes should be
     *                                     greater thant 0.
     */

    public double[][] frequencyTable(int nClass,
                                     double[] data)
    {
        if (nClass <= 0)
        {
            throw new IllegalArgumentException(
                    "The number of classes should be greater thant 0.");
        }
        this.nClass = nClass;
        this.data = data;
        copy = (double[]) data.clone();
        frequencyTable = new double[2][nClass];
        startIndex = 0;
        Arrays.sort(copy);
        classWidth = Math.ceil((Math.ceil(copy[copy.length - 1]) -
                                Math.floor(copy[0])) / nClass);
        for (int i = 0; i < nClass; i++)
        {
            frequencyTable[0][i] = Math.floor(copy[0]) + classWidth * (i + 1);
            counts = 0;
            while ((startIndex < copy.length) &&
                   (copy[startIndex] <= frequencyTable[0][i]))
            {
                startIndex += 1;
                counts += 1;
            }
            frequencyTable[1][i] = counts;
        }
        output.put(FREQUENCY_TABLE, frequencyTable);

        return frequencyTable;
    }

    /**
     * Calculates the frequency distribution with default number of classes
     * equal to 4 for the input data.
     * @param data the input data.
     * @return the frequency distribution.
     * @exception IllegalArgumentException the number of classes should be
     *                                     greater thant 0.
     */

    public double[][] frequencyTable(double[] data)
    {
        return frequencyTable(4, data);
    }

}
