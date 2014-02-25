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
import static javastat.util.Output.*;

/**
 *
 * <p>Constructs a frequency table for qualitative data.</p>
 * <p> </p>
 * <br> Example:
 * <br> String [] testdata1 = {"IBM", "Compaq", "Apple", "Packard Bell",
 *                             "Gateway 2000",
 *  <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       &nbsp;
 *                             "IBM", "Apple", "Packard Bell", "Packard Bell",
 *                             "Gateway 2000",
 *  <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       &nbsp;
 *                             "Compaq", "Compaq", "Apple", "Compaq", "Apple",
 *  <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       &nbsp;
 *                             "Apple", "Apple", "IBM", "IBM", "Apple",
 *  <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       &nbsp;
 *                             "Compaq", "Packard Bell", "IBM", "Compaq",
 *                             "Apple",
 *  <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       &nbsp;
 *                             "Apple", "Compaq", "Gateway 2000",
 *                             "Packard Bell", "IBM",
 *  <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       &nbsp;
 *                             "IBM", "Gateway 2000", "Apple", "IBM",
 *                             "Packard Bell",
 *  <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       &nbsp;
 *                             "Packard Bell", "Gateway 2000", "Compaq",
 *                             "Compaq", "Apple",
 *  <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       &nbsp;
 *                             "IBM", "Packard Bell", "Compaq", "Packard Bell",
 *                             "Compaq",
 *  <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       &nbsp;
 *                             "Packard Bell", "Compaq", "Apple",
 *                             "Packard Bell", "Apple"};
 * <br>
 * <br> // Non-null constructor
 * <br> QualitativeDataAnalysis testclass1 = new
 *        QualitativeDataAnalysis(testdata1);
 * <br> String [] dataValues = testclass1.dataVa lues;
 * <br> String [] frequency = testclass1.frequency;
 * <br>
 * <br> // Null constructor
 * <br> QualitativeDataAnalysis testclass2 = new QualitativeDataAnalysis();
 * <br> dataValues = testclass2.frequencyTable(testdata1)[0];
 * <br> frequency = testclass2.frequencyTable(testdata1)[1];
 * <br>
 * <br> // Non-null constructor
 * <br> Hashtable argument = new Hashtable();
 * <br> StatisticalAnalysis testclass3 =
 * <br> &nbsp;&nbsp;&nbsp;
 *        new QualitativeDataAnalysis(argument, testdata1).statisticalAnalysis;
 * <br> dataValues = (String[]) testclass3.output.get(DATA_VALUES);
 * <br> frequency = (String[]) testclass3.output.get(FREQUENCY);
 * <br>
 * <br> // Null constructor
 * <br> QualitativeDataAnalysis testclass4 = new
 *        QualitativeDataAnalysis(argument, null);
 * <br> String[] category = new String[]{"Apple", "IBM", "OTHER"};
 * <br> dataValues = testclass4.
 *        frequencyTable(argument, category, testdata1)[0];
 * <br> frequency = testclass4.
 *        frequencyTable(argument, category, testdata1)[1];
 * <br>
 * <br> // Obtains the information about the output
 * <br> out.println(testclass3.output.toString());
 * <br> out.println(testclass4.output.toString());
 */

public class QualitativeDataAnalysis extends StatisticalAnalysis
{

    /**
     * The frequency table,
     * <br> frequencyTable[0][i]: the data value of the i'th class;
     * <br> frequencyTable[1][i]: the frequency corresponding to the i'th class.
     */

    public String[][] frequencyTable;

    /**
     * The data values,
     * <br> dataValues[i]: the data value of the i'th class.
     */

    public String[] dataValues;

    /**
     * The frequency,
     * <br> frequency[i]: the frequency of the i'th class.
     */

    public String[] frequency;

    /**
     * The input data.
     */

    public Object[] data;

    /**
     * The categories for the categorical data.
     */

    public String[] category;

    /**
     * The object represents a qualitative data analysis.
     */

    public StatisticalAnalysis statisticalAnalysis;

    /**
     * The number of data in a class.
     */

    private int categoryNumber;

    /**
     * The index indicating if a loop should be stopped.
     */

    private int indExit;

    /**
     * The index indicating the starting position of the first data in a class.
     */

    private int startIndex;

    /**
     * The size of an ArrayList.
     */

    private int size;

    /**
     * The index indicating the position of removed data in an ArrayList.
     */

    private int rmInd;

    /**
     * The number of data in classes.
     */

    private int[] counts;

    /**
     * The first categorical data in one class.
     */

    private String initial;

    /**
     * The ArrayList used to construct the frequency table.
     */

    private ArrayList<Object> arraylist;

    /**
     * The copy of an ArrayList arraylist.
     */

    private ArrayList<Object> copy;

    /**
     * Default QualitativeDataAnalysis constructor.
     */

    public QualitativeDataAnalysis() {}

    /**
     * Constructs a class for qualitative data analysis given the specified
     * argument and data.
     * @param argument the empty argument.
     * @param dataObject the categorical data.
     */

    public QualitativeDataAnalysis(Hashtable argument,
                                   Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null)
        {
            statisticalAnalysis = new QualitativeDataAnalysis(dataObject);
        }
        else
        {
            statisticalAnalysis = new QualitativeDataAnalysis();
        }
    }


    /**
     * Constructs a class for qualitative data analysis with the specified data.
     * @param data the categorical data.
     */

    public QualitativeDataAnalysis(Object[] data)
    {
        this.data = data;
        frequencyTable = frequencyTable(data);
    }

    /**
     * Calculates the number of counts and associated data values for
     * categorical data.
     * @param argument the empty argument.
     * @param dataObject the input data.
     * @return the number of counts associated with the data values,
     * <br>    frequencyTable[0]: the data values;
     * <br>    frequencyTable[1]: the number of counts associated with
     *                            the data values.
     * @exception IllegalArgumentException wrong input data.
     */

    public String[][] frequencyTable(Hashtable argument,
                                     Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null &&
            dataObject.length == 2)
        {
            frequencyTable = frequencyTable((String[]) dataObject[0],
                                            (String[]) dataObject[1]);
        }
        else if (dataObject != null &&
                 dataObject.length == 1)
        {
            frequencyTable = frequencyTable(dataObject);
        }
        else
        {
            throw new IllegalArgumentException("Wrong input data.");
        }

        return frequencyTable;
    }

    /**
     * Calculates the number of counts and associated data values for
     * categorical data.
     * @param data the categorical data.
     * @return the number of counts associated with the data values,
     * <br>    frequencyTable[0]: the data values;
     * <br>    frequencyTable[1]: the number of counts associated with
     *                            the data values.
     */

    public String[][] frequencyTable(Object[] data)
    {
        this.data = data;
        arraylist = new ArrayList<Object>();
        for (int i = 0; i < data.length; i++)
        {
            arraylist.add(i, data[i]);
        }
        copy = (ArrayList<Object>) arraylist.clone();
        categoryNumber = 1;
        indExit = 0;
        startIndex = 0;
        counts = new int[data.length];
        while (indExit < data.length)
        {
            rmInd = 0;
            initial = (String) arraylist.get(startIndex);
            for (int i = startIndex; i < (arraylist.size() - 1); i++)
            {
                if (initial.equalsIgnoreCase((String) arraylist.get(i + 1)))
                {
                    copy.remove(i + 1 - rmInd);
                    categoryNumber += 1;
                    rmInd += 1;
                }
            }
            counts[startIndex] = categoryNumber;
            startIndex += 1;
            indExit += categoryNumber;
            categoryNumber = 1;
            arraylist = (ArrayList<Object>) copy.clone();
            size = arraylist.size();
        }
        frequencyTable = new String[2][arraylist.size()];
        for (int j = 0; j < arraylist.size(); j++)
        {
            frequencyTable[0][j] = (String) arraylist.get(j);
            frequencyTable[1][j] = Integer.toString(counts[j]);
        }
        dataValues = frequencyTable[0];
        frequency = frequencyTable[1];
        output.put(DATA_VALUES, dataValues);
        output.put(FREQUENCY, frequency);
        output.put(FREQUENCY_TABLE, frequencyTable);

        return frequencyTable;
    }

    /**
     * Calculates the number of counts and associated data values for
     * categorical data.
     * @param category the categories for the categorical data.
     * @param data the categorical data.
     * @return the number of counts associated with the data values,
     * <br>    frequencyTable[0]: the data values;
     * <br>    frequencyTable[1]: the number of counts associated with
     *                            the data values.
     */

    public String[][] frequencyTable(String[] category,
                                     String[] data)
    {
        this.data = (Object[]) data;
        this.category = category;
        arraylist = new ArrayList<Object>();
        for (int i = 0; i < data.length; i++)
        {
            arraylist.add(i, data[i]);
        }
        copy = (ArrayList<Object>) arraylist.clone();
        categoryNumber = 0;
        size = copy.size();
        int[] counts = new int[category.length];
        for (int j = 0; j < category.length; j++)
        {
            rmInd = 0;
            for (int i = 0; i < size; i++)
            {
                initial = (String) arraylist.get(i);
                if (initial.equalsIgnoreCase(category[j]))
                {
                    copy.remove(i - rmInd);
                    categoryNumber += 1;
                    rmInd += 1;
                }
            }
            counts[j] = categoryNumber;
            if (copy == null)
            {
                break;
            }
            categoryNumber = 0;
            size = copy.size();
            arraylist = (ArrayList<Object>) copy.clone();
        }
        if (copy.size() > 0)
        {
            String[][] addCount = frequencyTable(copy.toArray());
            frequencyTable =
                    new String[2][category.length + addCount[0].length];
            for (int i = 0; i < addCount[0].length; i++)
            {
                frequencyTable[0][category.length + i] = addCount[0][i];
                frequencyTable[1][category.length + i] = addCount[1][i];
            }
        }
        else
        {
            frequencyTable = new String[2][category.length];
        }
        for (int k = 0; k < category.length; k++)
        {
            frequencyTable[0][k] = category[k];
            frequencyTable[1][k] = Integer.toString(counts[k]);
        }
        dataValues = frequencyTable[0];
        frequency = frequencyTable[1];
        output.put(DATA_VALUES, dataValues);
        output.put(FREQUENCY, frequency);
        output.put(FREQUENCY_TABLE, frequencyTable);

        return frequencyTable;
    }

}
