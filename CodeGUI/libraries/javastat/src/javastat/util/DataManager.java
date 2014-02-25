package javastat.util;

/**
 * <p>Title: javastat</p>
 * <p>Description: JAVA programs for statistical computations</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Tung Hai University</p>
 * @author Wen Hsiang Wei
 * @version 1.4
 */

import java.io.*;
import java.util.*;

import static javastat.util.FunctionType.*;
import static javastat.util.OperationType.*;

import Jama.*;

/**
 *
 * <p>This class contains the collections of some basic methods for manipulating
 * the data.</p>
 */

public class DataManager extends Object
{

    /**
     * Default DataManager constructor.
     */

    public DataManager() {};

    /**
     * Obtains the vector of the absolute values of the input data.
     * @param data the input data,
     * <br>        data[i]: the (i+1)'th data.
     * @return the vector of the absolute values of the input data.
     */

    public double[][] abs(double[] ...data)
    {
        double[][] absData = new double[data.length][];
        for (int i = 0; i < data.length; i++)
        {
            absData[i] = new double[data[i].length];
            for (int j = 0; j < data[i].length; j++)
            {
                absData[i][j] = Math.abs(data[i][j]);
            }
        }

        return absData;
    }

    /**
     * Obtains the vector of the absolute values of the input data.
     * @param data the input data,
     * <br>        data[i]: the (i+1)'th data.
     * @return the vector of the absolute values of the input data.
     */

    public double[] abs(double[] data)
    {
        return abs(new double[][] {data})[0];
    }

    /**
     * Rounds a number to the required digit.
     * @param a the input number.
     * @param d the required digit to be rounded.
     * @return a number to the required digit.
     */

    public double roundDigits(double a,
                              double d)
    {
        return (double) Math.round(a * Math.pow(10.0, d)) / Math.pow(10.0, d);
    }

    /**
     * Converts a string array to a double array.
     * @param stringArray the input string array.
     * @return a double array converted from the input string array.
     */

    public double[] stringToDouble(String[] stringArray)
    {
        double[] doubleArray = new double[stringArray.length];
        for (int i = 0; i <= stringArray.length - 1; i++)
        {
            doubleArray[i] = Double.parseDouble(stringArray[i]);
        }

        return doubleArray;
    }

    /**
     * Converts a string array to a double array.
     * @param stringArray the input string array.
     * @return a double array converted from the input string array.
     */

    public double[][] stringToDouble(String[] ...stringArray)
    {
        double[][] doubleArray = new double[stringArray.length][];
        for (int i = 0; i <= stringArray.length - 1; i++)
        {
            doubleArray[i] = stringToDouble(stringArray[i]);
        }

        return doubleArray;
    }

    /**
     * Converts a double array to a string array.
     * @param doubleArray the input double array.
     * @return a string array converted from the input double array.
     */

    public String[][] doubleToString(double[] ...doubleArray)
    {
        String[][] stringArray = new String[doubleArray.length][];
        for (int i = 0; i < doubleArray.length; i++)
        {
            stringArray[i] = new String[doubleArray[i].length];
            for (int j = 0; j < doubleArray[i].length; j++)
            {
                stringArray[i][j] = Double.toString(doubleArray[i][j]);
            }
        }

        return stringArray;
    }

    /**
     * Converts a double array to a string array.
     * @param doubleArray the input double array.
     * @return a string array converted from the input double array.
     */

    public String[] doubleToString(double[] doubleArray)
    {
        return doubleToString(new double[][] {doubleArray})[0];
    }

    /**
     * Converts a string array to an integer array.
     * @param stringArray the input string array.
     * @return an integer array converted from the input string array.
     */

    public int[] stringToInteger(String[] stringArray)
    {
        int[] intArray = new int[stringArray.length];
        for (int i = 0; i <= stringArray.length - 1; i++)
        {
            intArray[i] = Integer.parseInt((stringArray[i]));
        }

        return intArray;
    }

    /**
     * Converts a string array to an integer array.
     * @param stringArray the input string array.
     * @return an integer array converted from the input string array.
     */

    public int[][] stringToInteger(String[] ...stringArray)
    {
        int[][] intArray = new int[stringArray.length][];
        for (int i = 0; i <= stringArray.length - 1; i++)
        {
            intArray[i] = stringToInteger((stringArray[i]));
        }

        return intArray;
    }

    /**
     * Calculates the number of string elements equal to the specified string.
     * @param str the input string array.
     * @param criterion the speicified string.
     * @return the number of string elements equal to the specified string.
     */

    public double numberOfClass(String[] str,
                                String criterion)
    {
        double counts = 0.0;
        for (int i = 0; i < str.length; i++)
        {
            if (str[i].equalsIgnoreCase(criterion))
            {
                counts += 1.0;
            }
        }

        return counts;
    }

    /**
     * Calculates the number of counts for the joint occurrence of two levels,
     * one level for each of two categorical variables.
     * @param colVar the levels for the column variable (categorical variable).
     * @param rowVar the levels for the row variable (categorical variable).
     * @return the number of counts for the joint occurrence of two levels.
     */

    public double[][] contingencyTable(String[] colVar,
                                       String[] rowVar)
    {
        int[] colVarHcode = new int[colVar.length];
        int[] rowVarHcode = new int[colVar.length];
        int[] colVarHcodeClone = new int[colVar.length];
        int[] rowVarHcodeClone = new int[colVar.length];
        double[] totalHcode = new double[rowVar.length];
        int ind1, ind2 = 0;
        boolean isZero;
        for (int i = 0; i < colVar.length; i++)
        {
            colVarHcode[i] = colVar[i].toLowerCase().hashCode();
            rowVarHcode[i] = rowVar[i].toLowerCase().hashCode();
            colVarHcodeClone[i] = colVar[i].toLowerCase().hashCode();
            rowVarHcodeClone[i] = rowVar[i].toLowerCase().hashCode();
            totalHcode[i] = ((double) rowVar[i].toLowerCase().hashCode()) +
                            (1.0 / colVarHcode[i]);
        }
        Arrays.sort(colVarHcode);
        Arrays.sort(rowVarHcode);
        int[] uniColHcode = unique(colVarHcode);
        int[] uniRowHcode = unique(rowVarHcode);
        int nc = uniColHcode.length;
        int nr = uniRowHcode.length;
        Arrays.sort(totalHcode);
        int[] totalIndex = groupIndex(totalHcode);
        double[][] table = new double[nr][nc];
        for (int j = 0; j < nr; j++)
        {
            for (int k = 0; k < nc; k++)
            {
                ind1 = 0;
                isZero = true;
                while ((ind1 < colVar.length) && isZero)
                {
                    if ((uniColHcode[k] == colVarHcodeClone[ind1]) &&
                        (uniRowHcode[j] == rowVarHcodeClone[ind1]))
                    {
                        isZero = false;
                    }
                    ind1 += 1;
                }
                if (isZero)
                {
                    table[j][k] = 0.0;
                }
                else
                {
                    table[j][k] = (double) totalIndex[ind2];
                    ind2 += 1;
                }
            }
        }

        return table;
    }

    /**
     * Returns the information associated with the contingency table.
     * one level for each of two categorical variables.
     * @param colVar the levels for the column variable (categorical variable).
     * @param rowVar the levels for the row variable (categorical variable).
     * @return the information associated with the contingency table.
     */

    public Hashtable<String, Object> contingencyTableInfo(String[] colVar,
                                                          String[] rowVar)
    {
        Hashtable<String, Object> tableInfo = new Hashtable<String,Object>();
        int[] colVarHcode = new int[colVar.length];
        int[] rowVarHcode = new int[colVar.length];
        int[] colVarHcodeClone = new int[colVar.length];
        int[] rowVarHcodeClone = new int[colVar.length];
        double[] totalHcode = new double[rowVar.length];
        for (int i = 0; i < colVar.length; i++)
        {
            colVarHcode[i] = colVar[i].toLowerCase().hashCode();
            rowVarHcode[i] = rowVar[i].toLowerCase().hashCode();
            colVarHcodeClone[i] = colVar[i].toLowerCase().hashCode();
            rowVarHcodeClone[i] = rowVar[i].toLowerCase().hashCode();
            totalHcode[i] = ((double) rowVar[i].toLowerCase().hashCode()) +
                            (1.0 / colVarHcode[i]);
        }
        Arrays.sort(colVarHcode);
        Arrays.sort(rowVarHcode);
        int[] uniColHcode = unique(colVarHcode);
        int[] uniRowHcode = unique(rowVarHcode);
        String[] columnNames = uniqueNames(colVar, uniColHcode);
        String[] rowNames = uniqueNames(rowVar, uniRowHcode);
        tableInfo.put("COLUMN_CODE", uniColHcode);
        tableInfo.put("ROW_CODE", uniRowHcode);
        tableInfo.put("TOTAL_CODE", totalHcode);
        tableInfo.put("COLUMN_NAMES", columnNames);
        tableInfo.put("ROW_NAMES", rowNames);

        return tableInfo;
    }

    /**
     * Returns the values of the input names without any repetition.
     * @param names the input names.
     * @param uniqueCode the hash codes without any repetition.
     * @return the values of the input names without any repetition.
     */

    private String[] uniqueNames(String[] names,
                                 int[] uniqueCode)
    {
        String[] uniqueNames = new String[uniqueCode.length];
        for(int i = 0; i < uniqueNames.length; i++)
        {
            for(int j =0; j < names.length; j++)
            {
                if(names[j].toLowerCase().hashCode() == uniqueCode[i])
                {
                    uniqueNames[i] = names[j];
                    break;
                }
            }
        }

        return uniqueNames;
    }

    /**
     * Creates a vector of evenly spaced numbers.
     * @param from the starting value of the sequence.
     * @param to the ending value of the sequence.
     * @param spacing the spacing between successive values in the sequence.
     * @return the vector of evenly spaced numbers.
     */

    public double[] sequence(double from,
                             double to,
                             double spacing)
    {
        double[] sequence =
            new double[((int ) Math.ceil(Math.abs((to - from) / spacing))) + 1];
        sequence[0] = from;
        sequence[sequence.length - 1] = to;
        for(int i = 1; i < sequence.length - 1; i++)
        {
            sequence[i] += spacing * i;
        }

        return sequence;
    }

    /**
     * Creates a vector of evenly spaced numbers with the specified number of
     * intervals.
     * @param from the starting value of the sequence.
     * @param to the ending value of the sequence.
     * @param length the specified number of intervals.
     * @return the vector of evenly spaced numbers.
     */

    public double[] sequence(double from,
                             double to,
                             int length)
    {
        return sequence(from, to, (to - from) / length);
    }

    /**
     * Calculates the positions of the data elements in ascending order,
     * i.e., the first integer is the position of the smallest data element,
     * etc.
     * @param data the input data.
     * @return the positions of the data elements in ascending order.
     */

    public int[] orderIndex(double[] data)
    {
        boolean isFirstNumber;
        int j;
        int[] index = new int[data.length];
        double[] buffer = (double[]) data.clone();
        Arrays.sort(buffer);
        for (int i = 0; i < data.length; i++)
        {
            isFirstNumber = true;
            j = 0;
            if ((i > 0) && buffer[i] == buffer[i - 1])
            {
                j = index[i - 1] + 1;
            }
            while (isFirstNumber && (j < data.length))
            {
                if (buffer[i] == data[j])
                {
                    isFirstNumber = false;
                    index[i] = j;
                }
                j = j + 1;
            }
        }

        return index;
    }

    /**
     * Obtains the values of the input data without any repetition.
     * @param orderedData the input ordered data.
     * @return the values of the input data without any repetition.
     */

    public int[] unique(int[] orderedData)
    {
        int[] originalData = new int[orderedData.length];
        int j = 1, index = 0, number;
        while (j < orderedData.length)
        {
            number = 1;
            while ((orderedData[j - 1] == orderedData[j]))
            {
                number += 1;
                j += 1;
                if (j == orderedData.length)
                {
                    break;
                }
            }
            originalData[index] = orderedData[j - 1];
            j += 1;
            index += 1;
        }
        int[] uniqueData = new int[index];
        for (int i = 0; i < index; i++)
        {
            uniqueData[i] = originalData[i];
        }

        return uniqueData;
    }

    /**
     * Creates a one-dimensional zero array.
     * @param arrayDim the dimension of the array.
     * @return a one-dimensional zero array.
     */

    public double[] zeroArray(int arrayDim)
    {
        double[] data = new double[arrayDim];
        for (int i = 0; i < arrayDim; i++)
        {
            data[i] = 0.0;
        }

        return data;
    }

    /**
     * Creates a two-dimensional zero array.
     * @param arrayDim1 the row dimension of the array.
     * @param arrayDim2 the column dimension of the array.
     * @return a two-dimensional zero array.
     */

    public double[][] zeroArray(int arrayDim1,
                                int arrayDim2)
    {
        double[][] data = new double[arrayDim1][arrayDim2];
        for (int i = 0; i < arrayDim1; i++)
        {
            for (int j = 0; j < arrayDim2; j++)
            {
                data[i][j] = 0.0;
            }
        }

        return data;
    }

    /**
     * Creates a diagonal matrix with the specified diagonal elements.
     * @param dataValues the elements in the diagonal.
     * @param arrayDim the order of the diagonal matrix.
     * @return the diagnoal matrix with the specified elements.
     */

    public double[][] diagonal(double[] dataValues,
                               int arrayDim)
    {
        double[][] data = new double[arrayDim][arrayDim];
        for (int i = 0; i < arrayDim; i++)
        {
            data[i][i] = dataValues[i];
        }

        return data;
    }

    /**
     * Creates an identity matrix with the specified diagonal elements.
     * @param arrayDim the order of the identity matrix.
     * @return the diagnoal matrix.
     */

    public double[][] identity(int arrayDim)
    {
        double[] dataValues = new double[arrayDim];
        for (int i = 0; i < arrayDim; i++)
        {
            dataValues[i] = 1.0;
        }

        return diagonal(dataValues, arrayDim);
    }

    /**
     * Calculates the differences of the data pairs.
     * @param data1 the input data from population 1.
     * @param data2 the input data from population 2.
     * @return the differences of the data pairs.
     * @exception IllegalArgumentException the two data sets should have the
     *                                     same sample size.
     */

    public double[] matchedDataDifference(double[] data1,
                                          double[] data2)
    {
        if (data1.length != data2.length)
        {
            throw new IllegalArgumentException(
                    "The sample sizes of the two data sets must agree.");
        }
        double[] mData = new double[data1.length];
        for (int i = 0; i < data1.length; i++)
        {
            mData[i] = data1[i] - data2[i];
        }

        return mData;
    }

    /**
     * Calculates the number of the input data equal to the median under test.
     * @param med the median under test.
     * @param data the input data.
     * @return the number of the input data equal to the median under test.
     */

    public double zeroNumber(double med,
                             double[] data)
    {
        double zn = 0.0;
        for (int i = 0; i < data.length; i++)
        {
            if ((data[i] - med) == 0)
            {
                zn += 1.0;
            }
        }

        return zn;
    }

    /**
     * Obtains the input data not equal to the median under test.
     * @param med the median under test.
     * @param data the input data.
     * @return the input data not equal to the median under test.
     */

    public double[] nonZeroData(double med,
                                double[] data)
    {
        int zn = (int) zeroNumber(med, data);
        double[] nonzeroData = new double[data.length - zn];
        int ind = 0;
        for (int i = 0; i < data.length; i++)
        {
            if ((data[i] - med) != 0)
            {
                nonzeroData[ind] = data[i];
                ind++;
            }
        }

        return nonzeroData;
    }

    /**
     * Calculates the tie number of the data.
     * @param data the input data.
     * @return the tie number of data.
     */

    public double[] tieNumber(double[] data)
    {
        double[] tn = new double[data.length];
        for (int i = 0; i < data.length; i++)
        {
            tn[i] = 0.0;
            for (int k = 0; k < data.length; k++)
            {
                if (data[i] == data[k])
                {
                    tn[i] += 1.0;
                }
            }
        }

        return tn;
    }

    /**
     * Calculates the tie number of (data-med).
     * @param med the median under test.
     * @param data the input data.
     * @return the tie numbers of (data-med).
     */

    public double[] tieNumber(double med,
                              double[] data)
    {
        double[] tn = new double[data.length];
        for (int i = 0; i < data.length; i++)
        {
            tn[i] = 0.0;
            for (int k = 0; k < data.length; k++)
            {
                if (Math.abs((data[i] - med)) == Math.abs((data[k] - med)))
                {
                    tn[i] += 1.0;
                }
            }
        }

        return tn;
    }

    /**
     * Sorts the data of which group indexes into ascending order.
     * @param groups the group indexes of the input data.
     * <br>          group[i]: the group index of the (i+1)'th data.
     */

    public void dataSort(double[] groups)
    {
        int j;
        double buffer;
        for (int i = 1; i < groups.length; i++)
        {
            j = i;
            while ((groups[j] < groups[j - 1]))
            {
                buffer = groups[j];
                groups[j] = groups[j - 1];
                groups[j - 1] = buffer;
                j -= 1;
                if (j == 0)
                {
                    break;
                }
            }
        }
    }

    /**
     * Sorts the data of which group indexes into ascending order.
     * @param groups the group indexes of the input data,
     * <br>          group[i]: the group index of the (i+1)'th data.
     * @param covariate the values of the covariates,
     * <br>             covariate[][i]: the value of the covariate of the
     *                                  (i+1)'th data.
     * @exception IllegalArgumentException the group vector and rows of the
     *                                     covariate matrix should have the same
     *                                     length.
     */

    public void dataSort(double[] groups,
                         double[] ...covariate)
    {
        checkDimension(covariate);
        if (groups.length != covariate[0].length)
        {
            throw new IllegalArgumentException(
                    "The group vector and rows of the covariate matrix must " +
                    "have the same length.");
        }
        int j;
        double buffer;
        for (int i = 1; i < groups.length; i++)
        {
            j = i;
            while ((groups[j] < groups[j - 1]))
            {
                buffer = groups[j];
                groups[j] = groups[j - 1];
                groups[j - 1] = buffer;
                for (int k = 0; k < covariate.length; k++)
                {
                    buffer = covariate[k][j];
                    covariate[k][j] = covariate[k][j - 1];
                    covariate[k][j - 1] = buffer;
                }
                j -= 1;
                if (j == 0)
                {
                    break;
                }
            }
        }
    }

    /**
     * Merges two data arrays into one data array.
     * @param data1 the input data array 1.
     * @param data2 the input data array 2.
     * @return the merged data array.
     */

    public double[] dataMerge(double[] data1,
                              double[] data2)
    {
        double[] data = new double[data1.length + data2.length];
        for (int s = 0; s < data1.length; s++)
        {
            data[s] = data1[s];
        }
        for (int m = data1.length; m < data.length; m++)
        {
            data[m] = data2[m - data1.length];
        }

        return data;
    }

    /**
     * Calculates the number of elements of a two-dimensional array.
     * @param data the input two-dimensional arry.
     * @return the number of elements of a two-dimensional array.
     */

    public double sampleSize(double[] ...data)
    {
        double samSize = 0.0;
        for (int j = 0; j < data.length; j++)
        {
            samSize += data[j].length;
        }

        return samSize;
    }

    /**
     * Calculates the number of data in each group.
     * @param orderGroups the ordered group indexes of the data.
     * @return the number of data in each group.
     */

    public int[] groupIndex(double[] orderGroups)
    {
        int[] oriIndex = new int[orderGroups.length];
        int j = 1, index = 0, number;
        while (j < orderGroups.length)
        {
            number = 1;
            while ((orderGroups[j - 1] == orderGroups[j]))
            {
                number += 1;
                j += 1;
                if (j == orderGroups.length)
                {
                    break;
                }
            }
            oriIndex[index] = number;
            j += 1;
            index += 1;
        }
        int[] gindex = new int[index];
        for (int i = 0; i < index; i++)
        {
            gindex[i] = oriIndex[i];
        }

        return gindex;
    }

    /**
     * Calculates the numbers at risk and death.
     * @param time the survival times of the patients.
     * @param censor the censor indicators for the patients,
     * <br>          censor[i]=1: death;
     * <br>          censor[i]=0: censored.
     * @return the number at risk and death,
     * <br>    [0][i]: the number at risk corresponding to the (i+1)'th patient;
     * <br>    [1][i]: the number of death corresponding to the (i+1)'th
     *                 patient.
     * @exception IllegalArgumentException the time vector and censor vector
     *                                     should have the same length.
     */

    public double[][] survivalIndex(double[] time,
                                    double[] censor)
    {
        checkPositiveRange(time, "time");
        checkCensor(censor);
        if (time.length != censor.length)
        {
            throw new IllegalArgumentException(
                    "The time vector and censor vector must have the same " +
                    "length.");
        }
        double[][] index = zeroArray(2, time.length);
        for (int i = 0; i < time.length; i++)
        {
            for (int k = 0; k < time.length; k++)
            {
                if (time[k] > time[i])
                {
                    index[0][i] += 1.0;
                }
                if (time[k] == time[i])
                {
                    index[0][i] += 1.0;
                    if ((int) censor[i] == 1)
                    {
                        index[1][i] += 1.0;
                    }
                }
            }
        }

        return index;
    }

    /**
     * Checks if the elements of the input table are all positive and the rows
     * of the table have the same length.
     * @param table the input table.
     * @exception IllegalArgumentException all rows of the input table must
     *                                     have the same length.
     * @exception IllegalArgumentException all elements of the input table
     *                                     must be postive.
     */

    public final void checkPositiveRangeDimension(double[] ...table)
    {
        for (int i = 0; i < table.length; i++)
        {
            if (table[i].length != table[0].length)
            {
                throw new IllegalArgumentException
                        ("All rows must have the same length.");
            }
            for (int j = 0; j < table[i].length; j++)
            {
                if (table[i][j] < 0.0)
                {
                    throw new IllegalArgumentException(
                            "The elements of the table should be positive.");
                }
            }
        }
    }

    /**
     * Checks if the elements of the input vector are all positive.
     * @param vector the input vector.
     * @param s the name of the input vector.
     * @exception IllegalArgumentException all elements must be postive.
     */

    public final void checkPositiveRange(double[] vector,
                                         String s)
    {
        for (int i = 0; i < vector.length; i++)
        {
            if (vector[i] < 0.0)
            {
                throw new IllegalArgumentException(
                        "The elements of the " + s +
                        " vector should be positive.");
            }
        }
    }

    /**
     * Checks if the elements of the input censor vector are either 0 or 1.
     * @param censor the input censor vector.
     * @exception IllegalArgumentException all elements must be either 1 or 0.
     */

    public final void checkCensor(double[] censor)
    {
        for (int i = 0; i < censor.length; i++)
        {
            if ((int) censor[i] != 0 && (int) censor[i] != 1)
            {
                throw new IllegalArgumentException(
                        "The elements of the censor vector should be either" +
                        " 1 or 0.");
            }
        }
    }

    /**
     * Checks if the rows of the input covariate matrix have the same length.
     * @param covariate the input covariate matrix.
     * @exception IllegalArgumentException all rows of the covariate matrix must
     *                                     have the same length.
     */

    public final void checkDimension(double[] ...covariate)
    {
        for (int i = 1; i < covariate.length; i++)
        {
            if (covariate[i].length != covariate[0].length)
            {
                throw new IllegalArgumentException
                        ("All rows must have the same length.");
            }
        }
    }

    /**
     * Reads the numerical data from a file and assigns them to a matrix.
     * @param fileName the name of the input data file.
     * @param matrix the matrix the input data are assigned to.
     * @param nc the number of columns in the data file.
     */

    public void scanFileToMatrix(String fileName,
                                 double[][] matrix,
                                 int nc)
    {
        try
        {
            File file = new File(fileName);
            int size = (int) file.length();
            int readind = 0, ind1 = 0, ind2 = 0;
            FileReader in = new FileReader(file);
            char[] data = new char[size];
            while (in.ready())
            {
                readind += in.read(data, readind, size - readind);
            }
            in.close();
            String s1 = new String(data, 0, readind);
            StringTokenizer st = new StringTokenizer(s1, "\n\r\t, :;");
            double[][] m = new double[nc][st.countTokens() / nc];
            while (st.hasMoreTokens())
            {
                if (ind2 == nc)
                {
                    ind2 = 0;
                    ind1++;
                }
                m[ind2++][ind1] = Double.parseDouble(st.nextToken());
            }
            for (int j = 0; j < nc; j++)
            {
                matrix[j] = m[j];
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Returns the data within specified range.
     * @param fromRowIndex initial row index.
     * @param toRowIndex final row index.
     * @param fromColumnIndex initial column index.
     * @param toColumnIndex final column index.
     * @param data the input data.
     * @return the data within the specified range.
     */

    public static double[][] getData(int fromRowIndex,
                                     int toRowIndex,
                                     int fromColumnIndex,
                                     int toColumnIndex,
                                     double[] ...data)
    {
        double[][] copy = new double[toRowIndex - fromRowIndex + 1]
                          [toColumnIndex - fromColumnIndex + 1];
        for (int i = fromRowIndex; i <= toRowIndex; i++)
        {
            for (int j = fromColumnIndex; j <= toColumnIndex; j++)
            {
                copy[i - fromRowIndex][j - fromColumnIndex] = data[i][j];
            }
        }

        return copy;
    }

    /**
     * Returns the duplicated data.
     * @param data the input data.
     * @return the duplicated data.
     */

    public double[] copyData(double[] data)
    {
        double[] copy = new double[data.length];
        System.arraycopy(data, 0, copy, 0, data.length);

        return copy;
    }

    /**
     * Returns the values of the input data raised to the powers of the second
     * argument.
     * @param data the input data.
     * @param power the powers
     * @return the values.
     */

    public double[][] pow(double[][] data,
                          double[][] power)
    {
        double[][] dataPower = new double[data.length][];
        for (int i = 0; i < data.length; i++)
        {
            dataPower[i] = new double[data[i].length];
            for (int j = 0; j < data[i].length; j++)
            {
                dataPower[i][j] = Math.pow(data[i][j], power[i][j]);
            }
        }

        return dataPower;
    }

    /**
     * Returns the values of the input data raised to the power of the second
     * argument.
     * @param data the input data.
     * @param power the power
     * @return the values.
     */

    public double[][] pow(double[][] data,
                          double power)
    {
        double[][] dataPower = new double[data.length][];
        for (int i = 0; i < data.length; i++)
        {
            dataPower[i] = new double[data[i].length];
            for (int j = 0; j < data[i].length; j++)
            {
                dataPower[i][j] = Math.pow(data[i][j], power);
            }
        }

        return dataPower;
    }

    /**
     * Returns the values of the input data raised to the powers of the second
     * argument.
     * @param data the input data.
     * @param power the powers.
     * @return the values.
     */

    public double[] pow(double[] data,
                        double[] power)
    {
        return pow(new double[][] {data}, new double[][] {power})[0];
    }

    /**
     * Returns the values of the input data raised to the power of the second
     * argument.
     * @param data the input data.
     * @param power the power.
     * @return the values.
     */

    public double[] pow(double[] data,
                        double power)
    {
        return pow(new double[][] {data}, power)[0];
    }

    /**
     * Returns the correctly rounded positive square roots of the input data.
     * @param data the input data.
     * @return the positive square roots of the input data.
     */

    public double[][] sqrt(double[] ...data)
    {
        return pow(data, 0.5);
    }

    /**
     * Returns the correctly rounded positive square roots of the input data.
     * @param data the input data.
     * @return the positive square roots of the input data.
     */

    public double[] sqrt(double[] data)
    {
        return pow(data, 0.5);
    }

    /**
     * Returns the squares of the input data.
     * @param data the input data.
     * @return the squares of the input data.
     */

    public double[][] square(double[] ...data)
    {
        return pow(data, 2.0);
    }

    /**
     * Returns the squares of the input data.
     * @param data the input data.
     * @return the squares of the input data.
     */

    public double[] square(double[] data)
    {
        return pow(data, 2.0);
    }

    /**
     * Returns the cubes of the input data.
     * @param data the input data.
     * @return the cubes of the input data.
     */

    public double[][] cube(double[] ...data)
    {
        return pow(data, 3.0);
    }

    /**
     * Returns the cubes of the input data.
     * @param data the input data.
     * @return the cubes of the input data.
     */

    public double[] cube(double[] data)
    {
        return pow(data, 3.0);
    }

    /**
     * Returns the reciprocals of the input data.
     * @param data the input data.
     * @return the reciprocals of the input data.
     */

    public double[][] reciprocal(double[] ...data)
    {
        return pow(data, -1.0);
    }

    /**
     * Returns the reciprocals of the input data.
     * @param data the input data.
     * @return the reciprocals of the input data.
     */

    public double[] reciprocal(double[] data)
    {
        return pow(data, -1.0);
    }

    /**
     * Returns the cubic roots of the input data.
     * @param data the input data.
     * @return the cubic roots of the input data.
     */

    public double[][] cbrt(double[] ...data)
    {
        return pow(data, 1.0 / 3.0);
    }

    /**
     * Returns the cubic roots of the input data.
     * @param data the input data.
     * @return the cubic roots of the input data.
     */

    public double[] cbrt(double[] data)
    {
        return pow(data, 1.0 / 3.0);
    }

    /**
     * Returns the trigonometric sines of the input data.
     * @param data the input data.
     * @return the trigonometric sines of the input data.
     */

    public double[][] sin(double[] ...data)
    {
        double[][] sinData = new double[data.length][];
         for (int i = 0; i < data.length; i++)
         {
            sinData[i] = new double[data[i].length];
            for (int j = 0; j < data[i].length; j++)
            {
                sinData[i][j] = Math.sin(data[i][j]);
            }
        }

        return sinData;
    }

    /**
     * Returns the trigonometric sines of the input data.
     * @param data the input data.
     * @return the trigonometric sines of the input data.
     */

    public double[] sin(double[] data)
    {
        return sin(new double[][] {data})[0];
    }

    /**
     * Returns the trigonometric cosines of the input data.
     * @param data the input data.
     * @return the trigonometric cosines of the input data.
     */

    public double[][] cos(double[] ...data)
    {
        double[][] cosData = new double[data.length][];
        for (int i = 0; i < data.length; i++)
        {
            cosData[i] = new double[data[i].length];
            for (int j = 0; j < data[i].length; j++)
            {
                cosData[i][j] = Math.cos(data[i][j]);
            }
        }

        return cosData;
    }

    /**
     * Returns the trigonometric cosines of the input data.
     * @param data the input data.
     * @return the trigonometric cosines of the input data.
     */

    public double[] cos(double[] data)
    {
        return cos(new double[][] {data})[0];
    }

    /**
     * Returns the trigonometric tangents of the input data.
     * @param data the input data.
     * @return the trigonometric tangents of the input data.
     */

    public double[][] tan(double[] ...data)
    {
        double[][] tanData = new double[data.length][];
        for (int i = 0; i < data.length; i++)
        {
            tanData[i] = new double[data[i].length];
            for (int j = 0; j < data[i].length; j++)
            {
                tanData[i][j] = Math.tan(data[i][j]);
            }
        }

        return tanData;
    }

    /**
     * Returns the trigonometric tangents of the input data.
     * @param data the input data.
     * @return the trigonometric tangents of the input data.
     */

    public double[] tan(double[] data)
    {
        return tan(new double[][] {data})[0];
    }

    /**
     * Returns the trigonometric transformation of the input data.
     * @param option the trigonometric function with options:
     * <br> FunctionType.SIN: the sine function;
     * <br> FunctionType.COS: the cosine function;
     * <br> FunctionType.TAN: the tangent function.
     * @param data the input data.
     * @return the trigonometric transformation of the input data.
     */

    public double[][] trigonoFunction(FunctionType option,
                                      double[] ...data)
    {
        double[][] trigonoData;
        switch (option)
        {
            case SIN:
                trigonoData = sin(data);
                break;
            case COS:
                trigonoData = cos(data);
                break;
            case TAN:
                trigonoData = tan(data);
                break;
            default:
                throw new IllegalArgumentException
                        ("The input function type does not exist.");
        }

        return trigonoData;
    }

    /**
     * Returns the trigonometric transformation of the input data.
     * @param option the trigonometric function with options:
     * <br>FunctionType.SIN: the sine function;
     * <br>FunctionType.COS: the cosine function;
     * <br>FunctionType.TAN: the tangent function.
     * @param data the input data.
     * @return the trigonometric transformation of the input data.
     */

    public double[] trigonoFunction(FunctionType option,
                                    double[] data)
    {
        return trigonoFunction(option, new double[][] {data})[0];
    }

    /**
     * Returns the trigonometric transformation of the input data.
     * @param option the trigonometric function with options:
     * <br>"SIN": the sine function;
     * <br>"COS": the cosine function;
     * <br>"TAN": the tangent function.
     * @param data the input data.
     * @return the trigonometric transformation of the input data.
     */

    public double[][] trigonoFunction(String option,
                                      double[] ...data)
    {
        return trigonoFunction(
            FunctionType.valueOf(option.toUpperCase()), data);
    }

    /**
     * Returns the trigonometric transformation of the input data.
     * @param option the trigonometric function with options:
     * <br> "SIN": the sine function;
     * <br> "COS": the cosine function;
     * <br> "TAN": the tangent function.
     * @param data the input data.
     * @return the trigonometric transformation of the input data.
     */

    public double[] trigonoFunction(String option,
                                    double[] data)
    {
        return trigonoFunction(
            FunctionType.valueOf(option.toUpperCase()), data);
    }

    /**
     * Returns the arc sines of the input data.
     * @param data the input data.
     * @return the arc sines of the input data.
     */

    public double[][] asin(double[] ...data)
    {
        double[][] asinData = new double[data.length][];
        for (int i = 0; i < data.length; i++)
        {
            asinData[i] = new double[data[i].length];
            for (int j = 0; j < data[i].length; j++)
            {
                asinData[i][j] = Math.asin(data[i][j]);
            }
        }

        return asinData;
    }

    /**
     * Returns the arc sines of the input data.
     * @param data the input data.
     * @return the arc sines of the input data.
     */

    public double[] asin(double[] data)
    {
        return asin(new double[][] {data})[0];
    }

    /**
     * Returns the arc cosines of the input data.
     * @param data the input data.
     * @return the arc cosines of the input data.
     */

    public double[][] acos(double[] ...data)
    {
        double[][] acosData = new double[data.length][];
        for (int i = 0; i < data.length; i++)
        {
            acosData[i] = new double[data[i].length];
            for (int j = 0; j < data[i].length; j++)
            {
                acosData[i][j] = Math.acos(data[i][j]);
            }
        }

        return acosData;
    }

    /**
     * Returns the arc cosines of the input data.
     * @param data the input data.
     * @return the arc cosines of the input data.
     */

    public double[] acos(double[] data)
    {
        return acos(new double[][] {data})[0];
    }

    /**
     * Returns the arc tangents of the input data.
     * @param data the input data.
     * @return the arc tangents of the input data.
     */

    public double[][] atan(double[] ...data)
    {
        double[][] atanData = new double[data.length][];
        for (int i = 0; i < data.length; i++)
        {
            atanData[i] = new double[data[i].length];
            for (int j = 0; j < data[i].length; j++)
            {
                atanData[i][j] = Math.atan(data[i][j]);
            }
        }

        return atanData;
    }

    /**
     * Returns the arc tangents of the input data.
     * @param data the input data.
     * @return the arc tangents of the input data.
     */

    public double[] atan(double[] data)
    {
        return atan(new double[][] {data})[0];
    }

    /**
     * Returns the inverse trigonometric transformation of the input data.
     * @param option the inverse trigonometric function with options:
     * <br> FunctionType.ASIN: the arc sine function;
     * <br> FunctionType.ACOS: the arc cosine function;
     * <br> FunctionType.ATAN: the arc tangent function.
     * @param data the input data.
     * @return the inverse trigonometric transformation of the input data.
     */

    public double[][] arcFunction(FunctionType option,
                                  double[] ...data)
    {
        double[][] arcData;
        switch (option)
        {
            case ASIN:
                arcData = asin(data);
                break;
            case ACOS:
                arcData = acos(data);
                break;
            case ATAN:
                arcData = atan(data);
                break;
            default:
                throw new IllegalArgumentException
                        ("The input function type does not exist.");
        }

        return arcData;
    }

    /**
     * Returns the inverse trigonometric transformation of the input data.
     * @param option the inverse trigonometric function with options:
     * <br> FunctionType.ASIN: the arc sine function;
     * <br> FunctionType.ACOS: the arc cosine function;
     * <br> FunctionType.ATAN: the arc tangent function.
     * @param data the input data.
     * @return the inverse trigonometric transformation of the input data.
     */

    public double[] arcFunction(FunctionType option,
                                double[] data)
    {
        return arcFunction(option, new double[][] {data})[0];
    }

    /**
     * Returns the inverse trigonometric transformation of the input data.
     * @param option the inverse trigonometric function with options:
     * <br> "ASIN": the arc sine function;
     * <br> "ACOS": the arc cosine function;
     * <br> "ATAN": the arc tangent function.
     * @param data the input data.
     * @return the inverse trigonometric transformation of the input data.
     */

    public double[][] arcFunction(String option,
                                  double[] ...data)
    {
        return arcFunction(FunctionType.valueOf(option.toUpperCase()), data);
    }

    /**
     * Returns the inverse trigonometric transformation of the input data.
     * @param option the inverse trigonometric function with options:
     * <br> "ASIN": the arc sine function;
     * <br> "ACOS": the arc cosine function;
     * <br> "ATAN": the arc tangent function.
     * @param data the input data.
     * @return the inverse trigonometric transformation of the input data.
     */

    public double[] arcFunction(String option,
                                double[] data)
    {
        return arcFunction(FunctionType.valueOf(option.toUpperCase()), data);
    }

    /**
     * Returns the hyperbolic sines of the input data.
     * @param data the input data.
     * @return the hyperbolic sines of the input data.
     */

    public double[][] sinh(double[] ...data)
    {
        double[][] sinhData = new double[data.length][];
        for (int i = 0; i < data.length; i++)
        {
            sinhData[i] = new double[data[i].length];
            for (int j = 0; j < data[i].length; j++)
            {
                sinhData[i][j] = Math.sinh(data[i][j]);
            }
        }

        return sinhData;
    }

    /**
     * Returns the hyperbolic sines of the input data.
     * @param data the input data.
     * @return the hyperbolic sines of the input data.
     */

    public double[] sinh(double[] data)
    {
        return sinh(new double[][] {data})[0];
    }

    /**
     * Returns the hyperbolic cosines of the input data.
     * @param data the input data.
     * @return the hyperbolic cosines of the input data.
     */

    public double[][] cosh(double[] ...data)
    {
        double[][] coshData = new double[data.length][];
        for (int i = 0; i < data.length; i++)
        {
            coshData[i] = new double[data[i].length];
            for (int j = 0; j < data[i].length; j++)
            {
                coshData[i][j] = Math.cosh(data[i][j]);
            }
        }

        return coshData;
    }

    /**
     * Returns the hyperbolic cosines of the input data.
     * @param data the input data.
     * @return the hyperbolic cosines of the input data.
     */

    public double[] cosh(double[] data)
    {
        return cosh(new double[][] {data})[0];
    }

    /**
     * Returns the hyperbolic tangents of the input data.
     * @param data the input data.
     * @return the hyperbolic tangents of the input data.
     */

    public double[][] tanh(double[] ...data)
    {
        double[][] tanhData = new double[data.length][];
        for (int i = 0; i < data.length; i++)
        {
            tanhData[i] = new double[data[i].length];
            for (int j = 0; j < data[i].length; j++)
            {
                tanhData[i][j] = Math.tanh(data[i][j]);
            }
        }

        return tanhData;
    }

    /**
     * Returns the hyperbolic tangents of the input data.
     * @param data the input data.
     * @return the hyperbolic tangents of the input data.
     */

    public double[] tanh(double[] data)
    {
        return tanh(new double[][] {data})[0];
    }

    /**
     * Returns the inverse hyperbolic sines of the input data.
     * @param data the input data.
     * @return the inverse hyperbolic sines of the input data.
     */

    public double[][] asinh(double[] ...data)
    {
        double[][] asinhData = new double[data.length][];
        for (int i = 0; i < data.length; i++)
        {
            asinhData[i] = new double[data[i].length];
            for (int j = 0; j < data[i].length; j++)
            {
                asinhData[i][j] = Math.log(data[i][j] + Math.sqrt(data[i][j] *
                        data[i][j] + 1.0));
            }
        }

        return asinhData;
    }

    /**
     * Returns the inverse hyperbolic sines of the input data.
     * @param data the input data.
     * @return the inverse hyperbolic sines of the input data.
     */

    public double[] asinh(double[] data)
    {
        return asinh(new double[][] {data})[0];
    }

    /**
     * Returns the inverse hyperbolic cosines of the input data.
     * @param data the input data.
     * @return the inverse hyperbolic cosines of the input data.
     */

    public double[][] acosh(double[] ...data)
    {
        double[][] acoshData = new double[data.length][];
        for (int i = 0; i < data.length; i++)
        {
            acoshData[i] = new double[data[i].length];
            for (int j = 0; j < data[i].length; j++)
            {
                acoshData[i][j] = Math.log(data[i][j] + Math.sqrt(data[i][j] *
                        data[i][j] - 1.0));
            }
        }

        return acoshData;
    }

    /**
     * Returns the inverse hyperbolic cosines of the input data.
     * @param data the input data.
     * @return the inverse hyperbolic cosines of the input data.
     */

    public double[] acosh(double[] data)
    {
        return acosh(new double[][] {data})[0];
    }

    /**
     * Returns the inverse hyperbolic tangents of the input data.
     * @param data the input data.
     * @return the inverse hyperbolic tangents of the input data.
     */

    public double[][] atanh(double[] ...data)
    {
        double[][] atanhData = new double[data.length][];
        for (int i = 0; i < data.length; i++)
        {
            atanhData[i] = new double[data[i].length];
            for (int j = 0; j < data[i].length; j++)
            {
                atanhData[i][j] = 0.5 * Math.log((1.0 + data[i][j]) /
                                                 (1.0 - data[i][j]));
            }
        }

        return atanhData;
    }

    /**
     * Returns the inverse hyperbolic tangents of the input data.
     * @param data the input data.
     * @return the inverse hyperbolic tangents of the input data.
     */

    public double[] atanh(double[] data)
    {
        return atanh(new double[][] {data})[0];
    }

    /**
     * Returns the hyperbolic transformation of the input data.
     * @param option the hyperbolic function with options:
     * <br> FunctionType.SINH: the hyperbolic sine function;
     * <br> FunctionType.COSH: the hyperbolic cosine function;
     * <br> FunctionType.TANH: the hyperbolic tangent function.
     * @param data the input data.
     * @return the hyperbolic transfomation of the input data.
     */

    public double[][] hyperbolicFunction(FunctionType option,
                                         double[] ...data)
    {
        double[][] hyperbolicData;
        switch (option)
        {
            case SINH:
                hyperbolicData = sinh(data);
                break;
            case COSH:
                hyperbolicData = cosh(data);
                break;
            case TANH:
                hyperbolicData = tanh(data);
                break;
            default:
                throw new IllegalArgumentException
                        ("The input function type does not exist.");
        }

        return hyperbolicData;
    }

    /**
     * Returns the hyperbolic transformation of the input data.
     * @param option the hyperbolic function with options:
     * <br> FunctionType.SINH: the hyperbolic sine function;
     * <br> FunctionType.COSH: the hyperbolic cosine function;
     * <br> FunctionType.TANH: the hyperbolic tangent function.
     * @param data the input data.
     * @return the hyperbolic transfomation of the input data.
     */

    public double[] hyperbolicFunction(FunctionType option,
                                       double[] data)
    {
        return hyperbolicFunction(option, new double[][] {data})[0];
    }

    /**
     * Returns the hyperbolic transformation of the input data.
     * @param option the hyperbolic function with options:
     * <br> "SINH": the hyperbolic sine function;
     * <br> "COSH": the hyperbolic cosine function;
     * <br> "TANH": the hyperbolic tangent function.
     * @param data the input data.
     * @return the hyperbolic transfomation of the input data.
     */

    public double[][] hyperbolicFunction(String option,
                                         double[] ...data)
    {

        return hyperbolicFunction(FunctionType.valueOf(option.toUpperCase()),
                                  data);
    }

    /**
     * Returns the hyperbolic transformation of the input data.
     * @param option the hyperbolic function with options:
     * <br> "SINH": the hyperbolic sine function;
     * <br> "COSH": the hyperbolic cosine function;
     * <br> "TANH": the hyperbolic tangent function.
     * @param data the input data.
     * @return the hyperbolic transfomation of the input data.
     */

    public double[] hyperbolicFunction(String option,
                                       double[] data)
    {
        return hyperbolicFunction(FunctionType.valueOf(option.toUpperCase()),
                                  data);
    }

    /**
     * Returns the inverse hyperbolic transformation of the input data.
     * @param option the hyperbolic function with options:
     * <br> FunctionType.ASINH: the inverse hyperbolic sine function;
     * <br> FunctionType.ACOSH: the inverse hyperbolic cosine function;
     * <br> FunctionType.ATANH: the inverse hyperbolic tangent function.
     * @param data the input data.
     * @return the inverse hyperbolic transfomation of the input data.
     */

    public double[][] inverseHyperbolicFunction(FunctionType option,
                                                double[] ...data)
    {
        double[][] inverseHyperbolicData;
        switch (option)
        {
            case ASINH:
                inverseHyperbolicData = asinh(data);
                break;
            case ACOSH:
                inverseHyperbolicData = acosh(data);
                break;
            case ATANH:
                inverseHyperbolicData = atanh(data);
                break;
            default:
                throw new IllegalArgumentException
                        ("The input function type does not exist.");
        }

        return inverseHyperbolicData;
    }

    /**
     * Returns the inverse hyperbolic transformation of the input data.
     * @param option the hyperbolic function with options:
     * <br> FunctionType.ASINH: the inverse hyperbolic sine function;
     * <br> FunctionType.ACOSH: the inverse hyperbolic cosine function;
     * <br> FunctionType.ATANH: the inverse hyperbolic tangent function.
     * @param data the input data.
     * @return the inverse hyperbolic transfomation of the input data.
     */

    public double[] inverseHyperbolicFunction(FunctionType option,
                                              double[] data)
    {
        return inverseHyperbolicFunction(option, new double[][] {data})[0];
    }

    /**
     * Returns the inverse hyperbolic transformation of the input data.
     * @param option the hyperbolic function with options:
     * <br> "ASINH": the inverse hyperbolic sine function;
     * <br> "ACOSH": the inverse hyperbolic cosine function;
     * <br> "ATANH": the inverse hyperbolic tangent function.
     * @param data the input data.
     * @return the inverse hyperbolic transfomation of the input data.
     */

    public double[][] inverseHyperbolicFunction(String option,
                                                double[] ...data)
    {
        return inverseHyperbolicFunction(FunctionType.valueOf(option.
                toUpperCase()), data);
    }

    /**
     * Returns the inverse hyperbolic transformation of the input data.
     * @param option the hyperbolic function with options:
     * <br> "ASINH": the inverse hyperbolic sine function;
     * <br> "ACOSH": the inverse hyperbolic cosine function;
     * <br> "ATANH": the inverse hyperbolic tangent function.
     * @param data the input data.
     * @return the inverse hyperbolic transfomation of the input data.
     */

    public double[] inverseHyperbolicFunction(String option,
                                              double[] data)
    {
        return inverseHyperbolicFunction(FunctionType.valueOf("option"), data);
    }

    /**
     * Returns the logarithms of the input data.
     * @param data the input data.
     * @param base the bases for the logarithms.
     * @return the logarithms of the input data.
     */

    public double[][] log(double[][] data,
                          double[][] base)
    {
        double[][] logData = new double[data.length][];
        for (int i = 0; i < data.length; i++)
        {
            logData[i] = new double[data[i].length];
            for (int j = 0; j < data[i].length; j++)
            {
                logData[i][j] = Math.log(data[i][j]) / Math.log(base[i][j]);
            }
        }

        return logData;
    }

    /**
     * Returns the logarithms of the input data.
     * @param data the input data.
     * @param base the bases for the logarithms.
     * @return the logarithms of the input data.
     */

    public double[] log(double[] data,
                        double[] base)
    {
        return log(new double[][] {data}, new double[][] {base})[0];
    }

    /**
     * Returns the logarithms of the input data.
     * @param data the input data.
     * @param base the base for the logarithms.
     * @return the logarithms of the input data.
     */

    public double[][] log(double[][] data,
                          double base)
    {
        double[][] logData = new double[data.length][];
        for (int i = 0; i < data.length; i++)
        {
            logData[i] = new double[data[i].length];
            for (int j = 0; j < data[i].length; j++)
            {
                logData[i][j] = Math.log(data[i][j]) / Math.log(base);
            }
        }

        return logData;
    }

    /**
     * Returns the logarithms of the input data.
     * @param data the input data.
     * @param base the base for the logarithms.
     * @return the logarithms of the input data.
     */

    public double[] log(double[] data,
                        double base)
    {
        return log(new double[][] {data}, base)[0];
    }

    /**
     * Returns the nature logarithms (base e) of the input data.
     * @param data the input data.
     * @return the natural logarithms of the input data.
     */

    public double[][] log(double[] ...data)
    {
        return log(data, Math.E);
    }

    /**
     * Returns the nature logarithms (base e) of the input data.
     * @param data the input data.
     * @return the natural logarithms of the input data.
     */

    public double[] log(double[] data)
    {
        return log(new double[][] {data})[0];
    }

    /**
     * Returns the logarithms (base 10) of the input data.
     * @param data the input data.
     * @return the logarithms (base 10) of the input data.
     */

    public double[][] log10(double[] ...data)
    {
        return log(data, 10);
    }

    /**
     * Returns the logarithms (base 10) of the input data.
     * @param data the input data.
     * @return the logarithms (base 10) of the input data.
     */

    public double[] log10(double[] data)
    {
        return log10(new double[][] {data})[0];
    }

    /**
     * Returns the exponential transformation of the input data.
     * @param data the input data.
     * @return the exponential transformation of the input data.
     */

    public double[][] exp(double[] ...data)
    {
        double[][] expData = new double[data.length][];
        for (int i = 0; i < data.length; i++)
        {
            expData[i] = new double[data[i].length];
            for (int j = 0; j < data[i].length; j++)
            {
                expData[i][j] = Math.exp(data[i][j]);
            }
        }

        return expData;
    }

    /**
     * Returns the exponential transformation of the input data.
     * @param data the input data.
     * @return the exponential transformation of the input data.
     */

    public double[] exp(double[] data)
    {
        return exp(new double[][] {data})[0];
    }

    /**
     * Returns the values of the input angles in radians.
     * @param data the input data.
     * @return the values of the input angles in radians.
     */

    public double[][] toRadians(double[] ...data)
    {
        double[][] radiansData = new double[data.length][];
        for (int i = 0; i < data.length; i++)
        {
            radiansData[i] = new double[data[i].length];
            for (int j = 0; j < data[i].length; j++)
            {
                radiansData[i][j] = Math.toRadians(data[i][j]);
            }
        }

        return radiansData;
    }

    /**
     * Returns the values of the input angles in radians.
     * @param data the input data.
     * @return the values of the input angles in radians.
     */

    public double[] toRadians(double[] data)
    {
        return toRadians(new double[][] {data})[0];
    }

    /**
     * Returns non-decreasing numbers which are the cumulative maxima of the
     * input data.
     * @param data the input data.
     * @return the cumulative maxima of the input data.
     */

    public double[][] cummax(double[] ...data)
    {
        double[][] cumData = new double[data.length][];
        double maxValue = data[0][0];
        for (int i = 0; i < data.length; i++)
        {
            cumData[i] = new double[data[i].length];
            for (int j = 0; j < data[i].length; j++)
            {
                if (data[i][j] <= maxValue)
                {
                    cumData[i][j] = maxValue;
                }
                else
                {
                    cumData[i][j] = data[i][j];
                    maxValue = data[i][j];
                }
            }
        }

        return cumData;
    }

    /**
     * Returns non-decreasing numbers which are the cumulative maxima of the
     * input data.
     * @param data the input data.
     * @return the cumulative maxima of the input data.
     */

    public double[] cummax(double[] data)
    {
        return cummax(new double[][] {data})[0];
    }

    /**
     * Returns non-increasing numbers which are the cumulative minima of the
     * input data.
     * @param data the input data.
     * @return the cumulative minima of the input data.
     */

    public double[][] cummin(double[] ...data)
    {
        double[][] cumData = new double[data.length][];
        double minValue = data[0][0];
        for (int i = 0; i < data.length; i++)
        {
            cumData[i] = new double[data[i].length];
            for (int j = 0; j < data[i].length; j++)
            {
                if (data[i][j] >= minValue)
                {
                    cumData[i][j] = minValue;
                }
                else
                {
                    cumData[i][j] = data[i][j];
                    minValue = data[i][j];
                }
            }
        }

        return cumData;
    }

    /**
     * Returns non-increasing numbers which are the cumulative minima of the
     * input data.
     * @param data the input data.
     * @return the cumulative minima of the input data.
     */

    public double[] cummin(double[] data)
    {
        return cummin(new double[][] {data})[0];
    }

    /**
     * Returns the cumulative sums of the input data.
     * @param data the input data.
     * @return the cumulative sums of the input data.
     */

    public double[][] cumsum(double[] ...data)
    {
        double[][] cumData = new double[data.length][];
        double total = 0;
        for (int i = 0; i < data.length; i++)
        {
            cumData[i] = new double[data[i].length];
            for (int j = 0; j < data[i].length; j++)
            {
                total += data[i][j];
                cumData[i][j] = total;
            }
        }

        return cumData;
    }

    /**
     * Returns the cumulative sums of the input data.
     * @param data the input data.
     * @return the cumulative sums of the input data.
     */

    public double[] cumsum(double[] data)
    {
        return cumsum(new double[][] {data})[0];
    }

    /**
     * Returns the cumulative products of the input data.
     * @param data the input data.
     * @return the cumulative products of the input data.
     */

    public double[][] cumprod(double[] ...data)
    {
        double[][] cumData = new double[data.length][];
        double total = 1;
        for (int i = 0; i < data.length; i++)
        {
            cumData[i] = new double[data[i].length];
            for (int j = 0; j < data[i].length; j++)
            {
                total *= data[i][j];
                cumData[i][j] = total;
            }
        }

        return cumData;
    }

    /**
     * Returns the cumulative products of the input data.
     * @param data the input data.
     * @return the cumulative products of the input data.
     */

    public double[] cumprod(double[] data)
    {
        return cumprod(new double[][] {data})[0];
    }

    /**
     * Returns the cumulative results of the input data.
     * @param option the cumulative function with options
     * <br> FunctionType.CUMMIN: the cumulative minima;
     * <br> FunctionType.CUMMAX: the umulative maxima;
     * <br> FunctionType.CUMSUM: the cumulative sums;
     * <br> FunctionType.CUMPROD: the cumulative products.
     * @param data the input data.
     * @return the cumulative results of the input data.
     */

    public double[][] cumFunction(FunctionType option,
                                  double[] ...data)
    {
        double[][] cumData;
        switch (option)
        {
            case CUMMIN:
                cumData = cummin(data);
                break;
            case CUMMAX:
                cumData = cummax(data);
                break;
            case CUMSUM:
                cumData = cumsum(data);
                break;
            case CUMPROD:
                cumData = cumprod(data);
                break;
            default:
                throw new IllegalArgumentException
                        ("The input function type does not exist.");
        }

        return cumData;
    }

    /**
     * Returns the cumulative results of the input data.
     * @param option the cumulative function with options
     * <br> FunctionType.CUMMIN: the cumulative minima;
     * <br> FunctionType.CUMMAX: the cumulative maxima;
     * <br> FunctionType.CUMSUM: the cumulative sums;
     * <br> FunctionType.CUMPROD: the cumulative products.
     * @param data the input data.
     * @return the cumulative results of the input data.
     */

    public double[] cumFunction(FunctionType option,
                                double[] data)
    {
        return cumFunction(option, new double[][] {data})[0];
    }

    /**
     * Returns the cumulative results of the input data.
     * @param option the cumulative function with options
     * <br> "CUMMIN": the cumulative minima;
     * <br> "CUMMAX": the cumulative maxima;
     * <br> "CUMSUM": the cumulative sums;
     * <br> "CUMPROD": the cumulative products.
     * @param data the input data.
     * @return the cumulative results of the input data.
     */

    public double[][] cumFunction(String option,
                                  double[] ...data)
    {
        return cumFunction(FunctionType.valueOf(option.toUpperCase()), data);
    }

    /**
     * Returns the cumulative results of the input data.
     * @param option the cumulative function with options
     * <br> "CUMMIN": the cumulative minima;
     * <br> "CUMMAX": the cumulative maxima;
     * <br> "CUMSUM": the cumulative sums;
     * <br> "CUMPROD": the cumulative products.
     * @param data the input data.
     * @return the cumulative results of the input data.
     */

    public double[] cumFunction(String option,
                                double[] data)
    {
        return cumFunction(FunctionType.valueOf(option.toUpperCase()), data);
    }

    /**
     * Sorts the input data in ascending order.
     * @param data the input data.
     * @return the sorted data.
     */

    public double[][] sort(double[] ...data)
    {
        double[] oneDData = twoDToOneD(data);
        double[][] sortedData = new double[data.length][];
        int len = 0;
        Arrays.sort(oneDData);
        for (int i = 0; i < data.length; i++)
        {
            sortedData[i] = new double[data[i].length];
            len += data[i].length;
            for (int j = 0; j < data[i].length; j++)
            {
                sortedData[i][j] = oneDData[len - data[i].length + j];
            }
        }

        return sortedData;
    }

    /**
     * Returns the sorted data in ascending order.
     * @param data the input data.
     * @return the sorted data.
     */

    public double[] sort(double[] data)
    {
        return sort(new double[][] {data})[0];
    }

    /**
     * Converts a two-dimentional array to a one-dimentional array.
     * @param data the two dimentional array.
     * @return the one-dimentional array.
     */

    public double[] twoDToOneD(double[] ...data)
    {
        double[] oneDData = new double[length(data)];
        int len = 0;
        for (int i = 0; i < data.length; i++)
        {
            len += data[i].length;
            for (int j = data[i].length; j > 0; j--)
            {
                oneDData[len - j] = data[i][data[i].length - j];
            }
        }

        return oneDData;
    }

    /**
     * Returns the number of elements in a two-dimentional array.
     * @param data the two dimentional array.
     * @return the number of elements.
     */

    public int length(double[] ...data)
    {
        int len = 0;
        for (int i = 0; i < data.length; i++)
        {
            len += data[i].length;
        }

        return len;
    }

    /**
     * Returns the sum of two input data.
     * @param data1 the input data.
     * @param data2 the input data.
     * @return the sum of the two input data.
     */

    public double[][] add(double[][] data1,
                          double[][] data2)
    {
        double[][] addedData = new double[data1.length][];
        for (int i = 0; i < data1.length; i++)
        {
            addedData[i] = new double[data1[i].length];
            for (int j = 0; j < data1[i].length; j++)
            {
                addedData[i][j] = data1[i][j] + data2[i][j];
            }
        }

        return addedData;
    }

    /**
     * Returns the sum of two input data.
     * @param data1 the input data.
     * @param data2 the input data.
     * @return the sum of the two input data.
     */

    public double[] add(double[] data1,
                        double[] data2)
    {
        return add(new double[][] {data1}, new double[][] {data2})[0];
    }

    /**
     * Returns the difference of two input data.
     * @param data1 the input data.
     * @param data2 the input data.
     * @return the difference of the two input data.
     */

    public double[][] minus(double[][] data1,
                            double[][] data2)
    {
        double[][] substractedData = new double[data1.length][];
        for (int i = 0; i < data1.length; i++)
        {
            substractedData[i] = new double[data1[i].length];
            for (int j = 0; j < data1[i].length; j++)
            {
                substractedData[i][j] = data1[i][j] - data2[i][j];
            }
        }

        return substractedData;
    }

    /**
     * Returns the difference of two input data.
     * @param data1 the input data.
     * @param data2 the input data.
     * @return the difference of the two input data.
     */

    public double[] minus(double[] data1,
                          double[] data2)
    {
        return minus(new double[][] {data1}, new double[][] {data2})[0];
    }

    /**
     * Returns the product of two input data.
     * @param data1 the input data.
     * @param data2 the input data.
     * @return the product of the two input data.
     */

    public double[][] prod(double[][] data1,
                           double[][] data2)
    {
        double[][] multipliedData = new double[data1.length][];
        for (int i = 0; i < data1.length; i++)
        {
            multipliedData[i] = new double[data1[i].length];
            for (int j = 0; j < data1[i].length; j++)
            {
                multipliedData[i][j] = data1[i][j] * data2[i][j];
            }
        }

        return multipliedData;
    }

    /**
     * Returns the product of two input data.
     * @param data1 the input data.
     * @param data2 the input data.
     * @return the product of the two input data.
     */

    public double[] prod(double[] data1,
                         double[] data2)
    {
        return prod(new double[][] {data1}, new double[][] {data2})[0];
    }

    /**
     * Returns the division of two input data.
     * @param data1 the input data.
     * @param data2 the input data.
     * @return the division of the two input data.
     */

    public double[][] divide(double[][] data1,
                             double[][] data2)
    {
        double[][] dividedData = new double[data1.length][];
        for (int i = 0; i < data1.length; i++)
        {
            dividedData[i] = new double[data1[i].length];
            for (int j = 0; j < data1[i].length; j++)
            {
                dividedData[i][j] = data1[i][j] / data2[i][j];
            }
        }

        return dividedData;
    }

    /**
     * Returns the division of two input data.
     * @param data1 the input data.
     * @param data2 the input data.
     * @return the division of the two input data.
     */

    public double[] divide(double[] data1,
                           double[] data2)
    {
        return divide(new double[][] {data1}, new double[][] {data2})[0];
    }

    /**
     * Performs matrix multiplication.
     * @param matrix1 the input matrix.
     * @param matrix2 the input matrix.
     * @return the matrix product of the two input matrices.
     */

    public double[][] multiply(double[][] matrix1,
                               double[][] matrix2)
    {
        return new Matrix(matrix1).times(new Matrix(matrix2)).getArray();
    }

    /**
     * Performs matrix multiplication.
     * @param matrix1 the input matrix.
     * @param matrix2 the input matrix.
     * @return the matrix product of the two input matrices.
     */

    public double[] multiply(double[] matrix1,
                             double[] matrix2)
    {
        return multiply(new double[][] {matrix1}, new double[][] {matrix2})[0];
    }

    /**
     * Returns the inverse of the input matrix.
     * @param matrix the input matrix.
     * @return the inverse of the input matrix.
     */

    public double[][] inverse(double[] ...matrix)
    {
        return new Matrix(matrix).inverse().getArray();
    }

    /**
     * Returns the transpose of the input matrix.
     * @param matrix the input matrix.
     * @return the transpose of the input matrix.
     */

    public double[][] transpose(double[] ...matrix)
    {
        return new Matrix(matrix).transpose().getArray();
    }

    /**
     * Returns the transpose of the input vector.
     * @param matrix the input vector.
     * @return the transpose of the input vector.
     */

    public double[] transpose(double[] matrix)
    {
        return transpose(new double[][] {matrix})[0];
    }

    /**
     * Returns the eigenvalus and eigenvecotrs of the input matrix.
     * @param matrix the input matrix.
     * @return the eigenvalus and eigenvecotrs of the input matrix.
     */

    public Hashtable eigenAnalysis(double[] ...matrix)
    {
        Hashtable<String, Object> eigenTable = new Hashtable<String, Object>();
        EigenvalueDecomposition eigenvalueDecomposition =
                new EigenvalueDecomposition(new Matrix(matrix));
        eigenTable.put("Eigenvalues",
                       eigenvalueDecomposition.getRealEigenvalues());
        eigenTable.put("Eigenvectors",
                       eigenvalueDecomposition.getV().getArray());

        return eigenTable;
    }

    /**
     * Returns the eigenvalus and eigenvecotrs of the input matrix.
     * @param matrix the input matrix.
     * @return the eigenvalus and eigenvecotrs of the input matrix.
     */

    public Vector eigen(double[] ...matrix)
    {
        EigenvalueDecomposition eigenvalueDecomposition =
                new EigenvalueDecomposition(new Matrix(matrix));
        Vector eigenTable = new Vector();
        eigenTable.add(eigenvalueDecomposition.getRealEigenvalues());
        eigenTable.add(eigenvalueDecomposition.getV().getArray());

        return eigenTable;
    }

    /**
     * Returns the eigenvalus of the input matrix.
     * @param matrix the input matrix.
     * @return the eigenvalus of the input matrix.
     */

    public double[] eigenvalue(double[] ...matrix)
    {
        return (double[]) eigenAnalysis(matrix).get("Eigenvalues");
    }

    /**
     * Returns the eigenvecotrs of the input matrix.
     * @param matrix the input matrix.
     * @return the eigenvecotrs of the input matrix.
     */

    public double[][] eigenvectors(double[] ...matrix)
    {
        return (double[][]) eigenAnalysis(matrix).get("Eigenvectors");
    }

    /**
     * Returns the determinant of the input matrix.
     * @param matrix the input matrix.
     * @return the determinant of the input matrix.
     */

    public double det(double[] ...matrix)
    {
        return new Matrix(matrix).det();
    }

    /**
     * Returns the determinant of the input matrix.
     * @param matrix the input matrix.
     * @return the determinant of the input matrix.
     */

    public double[][] determinant(double[] ...matrix)
    {
        return new double[][] {{det(matrix)}};
    }

    /**
     * Returns the transforamtion of the input data.
     * @param option a variety of functions.
     * @param data the input data.
     * @return the transforamtion of the input data.
     */

    public double[][] dataManipulation(FunctionType option,
                                       double[] ...data)
    {
        double[][] transformedData;
        Vector eigenTable;
        BasicStatistics basicStatistics = new BasicStatistics();
        switch (option)
        {
            case RECIPROCAL:
                transformedData = reciprocal(data);
                break;
            case SQUARE:
                transformedData = square(data);
                break;
            case CUBE:
                transformedData = cube(data);
                break;
            case SQRT:
                transformedData = sqrt(data);
                break;
            case CBRT:
                transformedData = cbrt(data);
                break;
            case ABS:
                transformedData = abs(data);
                break;
            case ACOS:
                transformedData = acos(data);
                break;
            case ASIN:
                transformedData = asin(data);
                break;
            case ATAN:
                transformedData = atan(data);
                break;
            case ACOSH:
                transformedData = acosh(data);
                break;
            case ASINH:
                transformedData = asinh(data);
                break;
            case ATANH:
                transformedData = atanh(data);
                break;
            case COS:
                transformedData = cos(data);
                break;
            case COSH:
                transformedData = cosh(data);
                break;
            case CUMMAX:
                transformedData = cummax(data);
                break;
            case CUMMIN:
                transformedData = cummin(data);
                break;
            case CUMPROD:
                transformedData = cumprod(data);
                break;
            case CUMSUM:
                transformedData = cumsum(data);
                break;
            case EXP:
                transformedData = exp(data);
                break;
            case INVERSE:
                transformedData = inverse(data);
                break;
            case TRANSPOSE:
                transformedData = transpose(data);
                break;
            case EIGEN:
                eigenTable = eigen(data);
                transformedData = new double[data.length + 1][];
                transformedData[0] = (double[]) eigenTable.get(0);
                for (int i = 0; i < data.length; i++)
                {
                    transformedData[i + 1] =
                            ((double[][]) eigenTable.get(1))[i];
                }
                break;
            case DET:
                transformedData = determinant(data);
                break;
            case LOG:
                transformedData = log(data);
                break;
            case LOG10:
                transformedData = log10(data);
                break;
            case SIN:
                transformedData = sin(data);
                break;
            case SINH:
                transformedData = sinh(data);
                break;
            case SORT:
                transformedData = sort(data);
                break;
            case TAN:
                transformedData = tan(data);
                break;
            case TANH:
                transformedData = tanh(data);
                break;
            case COVARIANCE:
                transformedData = basicStatistics.covarianceMatrix(data);
                break;
            case CORRELATION:
                transformedData = basicStatistics.correlationMatrix(data);
                break;
            case TORADIANS:
                transformedData = toRadians(data);
                break;
            default:
                throw new IllegalArgumentException
                        ("The input function type does not exist.");
        }

        return transformedData;
    }

    /**
     * Returns the transforamtion of the input data.
     * @param option a variety of functions.
     * @param data the input data.
     * @return the transforamtion of the input data.
     */

    public double[][] dataManipulation(String option,
                                       double[] ...data)
    {
        return dataManipulation(FunctionType.valueOf(option), data);
    }

    /**
     * Returns arithemtic operations of two input data.
     * @param option a variety of operations.
     * @param data1 the input data.
     * @param data2 the input data.
     * @return the arithemtic operations of two input data.
     */

    public double[][] dataManipulation(OperationType option,
                                       double[][] data1,
                                       double[][] data2)
    {
        double[][] operationData;
        switch (option)
        {
            case ADD:
                operationData = add(data1, data2);
                break;
            case MINUS:
                operationData = minus(data1, data2);
                break;
            case PROD:
                operationData = prod(data1, data2);
                break;
            case DIVIDE:
                operationData = divide(data1, data2);
                break;
            case POW:
                operationData = pow(data1, data2);
                break;
            case MULTIPLY:
                operationData = multiply(data1, data2);
                break;
            default:
                throw new IllegalArgumentException
                        ("The input function type does not exist.");
        }

        return operationData;
    }

    /**
     * Returns arithemtic operations of two input data.
     * @param option a variety of operations.
     * @param data1 the input data.
     * @param data2 the input data.
     * @return the arithemtic operations of two input data.
     */

    public double[][] dataManipulation(String option,
                                       double[][] data1,
                                       double[][] data2)
    {
        return dataManipulation(OperationType.valueOf(option), data1, data2);
    }

    /**
     * Returns the square root matrix of the input matrix.
     * @param matrix the input matrix.
     * @return the square root matrix of the input matrix.
     */

    public Matrix sqrtMatrix(double[] ...matrix)
    {
        Matrix M = new Matrix(matrix);
        EigenvalueDecomposition eigenDecomposition = new
                EigenvalueDecomposition(M);
        double[] eigenValues = eigenDecomposition.getRealEigenvalues();
        double[][] sqrtEigenValues = zeroArray(eigenValues.length,
                                               eigenValues.length);
        for (int i = 0; i < eigenValues.length; i++)
        {
            sqrtEigenValues[i][i] = Math.sqrt(eigenValues[i]);
        }

        return eigenDecomposition.getV().times(new Matrix(sqrtEigenValues)).
                times(eigenDecomposition.getV().transpose());
    }

    /**
     * Converts the text into FunctionType value.
     * @param text the text.
     * @return the FunctionType value associated with the text.
     */

    public FunctionType textToFunctionType(String text)
    {
        if (text.equalsIgnoreCase("reciprocal (1/x)"))
        {
            return RECIPROCAL;
        }
        else if (text.equalsIgnoreCase("square (x^2)"))
        {
            return SQUARE;
        }
        else if (text.equalsIgnoreCase("cubic (x^3)"))
        {
            return CUBE;
        }
        else if (text.equalsIgnoreCase("absolute value"))
        {
            return ABS;
        }
        else if (text.equalsIgnoreCase("arc cosine"))
        {
            return ACOS;
        }
        else if (text.equalsIgnoreCase("arc sine"))
        {
            return ASIN;
        }
        else if (text.equalsIgnoreCase("arc tangent"))
        {
            return ATAN;
        }
        else if (text.equalsIgnoreCase("inverse hyperbolic cosine"))
        {
            return ACOSH;
        } else if (text.equalsIgnoreCase("inverse hyperbolic sine"))
        {
            return ASINH;
        }
        else if (text.equalsIgnoreCase("inverse hyperbolic tangent"))
        {
            return ATANH;
        }
        else if (text.equalsIgnoreCase("cubic root"))
        {
            return CBRT;
        }
        else if (text.equalsIgnoreCase("cosine"))
        {
            return COS;
        }
        else if (text.equalsIgnoreCase("sine"))
        {
            return SIN;
        }
        else if (text.equalsIgnoreCase("hyperbolic cosine"))
        {
            return COSH;
        }
        else if (text.equalsIgnoreCase("cumulative maxima"))
        {
            return CUMMAX;
        }
        else if (text.equalsIgnoreCase("cumulative minima"))
        {
            return CUMMIN;
        }
        else if (text.equalsIgnoreCase("cumulative product"))
        {
            return CUMPROD;
        }
        else if (text.equalsIgnoreCase("cumulative sum"))
        {
            return CUMSUM;
        }
        else if (text.equalsIgnoreCase("exponential"))
        {
            return EXP;
        }
        else if (text.equalsIgnoreCase("natural logarithm (base e)"))
        {
            return LOG;
        }
        else if (text.equalsIgnoreCase("base 10 logarithm"))
        {
            return LOG10;
        }
        else if (text.equalsIgnoreCase("inverse matrix"))
        {
            return INVERSE;
        }
        else if (text.equalsIgnoreCase("inverse matrix"))
        {
            return INVERSE;
        }
        else if (text.equalsIgnoreCase("matrix transpose"))
        {
            return TRANSPOSE;
        }
        else if (text.equalsIgnoreCase("eigen analysis"))
        {
            return EIGEN;
        }
        else if (text.equalsIgnoreCase("matrix determinant"))
        {
            return DET;
        }
        else if (text.equalsIgnoreCase("hyperbolic sine"))
        {
            return SINH;
        }
        else if (text.equalsIgnoreCase("sort"))
        {
            return SORT;
        }
        else if (text.equalsIgnoreCase("square root"))
        {
            return SQRT;
        }
        else if (text.equalsIgnoreCase("tangent"))
        {
            return TAN;
        }
        else if (text.equalsIgnoreCase("hyperbolic tangent"))
        {
            return TANH;
        }
        else if (text.equalsIgnoreCase("covariance"))
        {
            return COVARIANCE;
        }
        else if (text.equalsIgnoreCase("correlation"))
        {
            return CORRELATION;
        }
        else
        {
            return TORADIANS;
        }
    }

    /**
     * Converts the text into OperationType value.
     * @param text the text.
     * @return the OperationType value associated with the text.
     */

    public OperationType textToOperationType(String text)
    {
        if (text.equalsIgnoreCase("+"))
        {
            return ADD;
        }
        else if (text.equalsIgnoreCase("-"))
        {
            return MINUS;
        }
        else if (text.equalsIgnoreCase("*"))
        {
            return PROD;
        }
        else if (text.equalsIgnoreCase("/"))
        {
            return DIVIDE;
        }
        else if (text.equalsIgnoreCase("^"))
        {
            return POW;
        }
        else
        {
            return MULTIPLY;
        }
    }

    /**
     * Converts the original data to a double array.
     * @param fromIndex the starting index.
     * @param dataObject the original data.
     * @return the double array.
     * @exception IllegalArgumentException wrong input data.
     */

    public static double[][] castDoubleObject(int fromIndex,
                                              Object[] dataObject)
    {
        double[][] doubleObject = new double[dataObject.length - fromIndex][];
        for (int i = fromIndex; i < dataObject.length; i++)
        {
            if (!dataObject[i].getClass().getName().equalsIgnoreCase("[D"))
            {
                throw new IllegalArgumentException("Wrong input data type.");
            }
            else
            {
                doubleObject[i - fromIndex] = (double[]) dataObject[i];
            }
        }

        return doubleObject;
    }

    /**
     * Converts the original data to a string array.
     * @param fromIndex the starting index.
     * @param dataObject the original data.
     * @return the string array.
     * @exception IllegalArgumentException wrong input data.
     */

    public static String[][] castStringObject(int fromIndex,
                                              Object[] dataObject)
    {
        String[][] stringObject = new String[dataObject.length - fromIndex][];
        for (int i = fromIndex; i < dataObject.length; i++)
        {
            if (!dataObject[i].getClass().getName().equalsIgnoreCase(
                    "[Ljava.lang.String;"))
            {
                throw new IllegalArgumentException("Wrong input data type.");
            }
            else
            {
                stringObject[i - fromIndex] = (String[]) dataObject[i];
            }
        }

        return stringObject;
    }

    /**
     * Adds a vector corresponding to the intercept to the original covariate
     * matrix.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the covariate matrix with the first row equal to the unit vector.
     */

    public double[][] addIntercept(double[] ...covariate)
    {
        double[][] covariateWithIntercept =
            new double[covariate.length + 1][covariate[0].length];
        for (int i = 0; i < covariate[0].length; i++)
        {
            covariateWithIntercept[0][i] = 1.0;
        }
        for (int i = 1; i < covariateWithIntercept.length; i++)
        {
            covariateWithIntercept[i] = covariate[i - 1];
        }

        return covariateWithIntercept;
    }

    /**
     * Obtains the index associated with the type of the input data.
     * @param dataObject the input weight matrix (optional), responses and
     *                   values of the covariates (excluding the one
     *                   corresponding to intercept).
     * @return the index.
     */

    public Hashtable dataLengthIndex(Object ...dataObject)
    {
        int dataLengthIndex;
        double[][] doubleCovariate;
        Hashtable index = new Hashtable();
        if (dataObject != null)
        {
            if (dataObject.length >= 3 &&
                dataObject[0].getClass().getName().equalsIgnoreCase("[[D") &&
                dataObject[1].getClass().getName().equalsIgnoreCase("[D") &&
                dataObject[2].getClass().getName().equalsIgnoreCase("[D"))
            {
                dataLengthIndex = 1;
                doubleCovariate = castDoubleObject(2, dataObject);
                index.put("doubleCovariate", doubleCovariate);
            }
            else if (dataObject.length == 3 &&
                     dataObject[0].getClass().getName().
                     equalsIgnoreCase("[[D") &&
                     dataObject[1].getClass().getName().
                     equalsIgnoreCase("[D") &&
                     dataObject[2].getClass().getName().equalsIgnoreCase("[[D"))
            {
                dataLengthIndex = 2;
            }
            else if (dataObject.length >= 2 &&
                     dataObject[0].getClass().getName().
                     equalsIgnoreCase("[D") &&
                     dataObject[1].getClass().getName().equalsIgnoreCase("[D"))
            {
                dataLengthIndex = 3;
                doubleCovariate = castDoubleObject(1, dataObject);
                index.put("doubleCovariate", doubleCovariate);
            }
            else if (dataObject.length == 2 &&
                     dataObject[0].getClass().getName().
                     equalsIgnoreCase("[D") &&
                     dataObject[1].getClass().getName().equalsIgnoreCase("[[D"))
            {
                dataLengthIndex = 4;
            }
            else
            {
                throw new IllegalArgumentException("Wrong input data.");
            }
        }
        else
        {
            dataLengthIndex = 0;
        }
        index.put("dataLengthIndex", dataLengthIndex);

        return index;
    }

}
