package javastat.util;

/**
 * <p>Title: javastat</p>
 * <p>Description: JAVA programs for statistical computations</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Tung Hai University</p>
 * @author B. J. Guo and Wen Hsiang Wei
 * @version 1.4
 */

import Jama.*;

/**
 *
 * <p>This class contains the collections of utility methods for generalized
 * linear models.</p>
 */

public class GLMDataManager extends Object
{
    /**
     * Default GLMDataManager constructor.
     */

    public GLMDataManager() {}

    /**
     *  The string covariates.
     */

    private String[][] stringCovariate;

    /**
     * The class size.
     */

    private double[] classSize;

    /**
     *  The responses with double values.
     */

    private double[] doubleResponse;

    /**
     * Obtains the binary (0 or 1) matrix associated with the covariates.
     * @param covariate the values of the covariates (excluding the one
     *        corresponding to intercept),
     * <br>   covariate[j]: the (j+1)'th covariate vector.
     * @return the binary (0 or 1) matrix associated with the covariate.
     */

    public double[][] zeroOneMatrix(String[] ...covariate)
    {
        covariate = transpose(covariate);
        int[][] hashCode = new int[covariate.length][covariate[0].length];
        for (int i = 0; i < covariate.length; i++)
        {
            for (int j = 0; j < covariate[0].length; j++)
            {
                hashCode[i][j] = covariate[i][j].toLowerCase().hashCode();
            }
        }
        int[] hashLevel = hashLevel(covariate);
        double[][] hashLevelString = hashLevelString(covariate);
        int a = 0;
        for (int i = 0; i < hashLevel.length; i++)
        {
            a += (hashLevel[i] - 1);
        }
        Matrix one = new Matrix(covariate.length, a, 1);
        double[][] zeroOneArray = one.getArray();
        int c = -1;
        for (int t = 0; t < hashLevel.length; t++)
        {
            for (int j = 1; j < hashLevel[t]; j++)
            {
                c += 1;
                for (int k = 0; k < covariate.length; k++)
                {
                    if (hashCode[k][t] != hashLevelString[j][t])
                    {
                        zeroOneArray[k][c] = 0;
                    }
                }
            }
        }

        return transpose(zeroOneArray);
    }

    /**
     * Obtains the binary (0 or 1) matrix associated with the covariates and
     * adds one vector corresponding to the intercept to the first row of the
     * binary matrix.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the binary (0 or 1) matrix with the first row equal to the vector
     *         of which elements all equal to 1.
     */

    public double[][] zeroOneMatrixWithIntercept(String[] ...covariate)
    {
        return addIntercept(zeroOneMatrix(covariate));
    }

    /**
     * Obtains the binary (0 or 1) vector associated with the covariate.
     * @param covariate the values of the covariate.
     * @return the binary (0 or 1) vector associated with the covariate.
     */

    public double[] zeroOneVector(String[] covariate)
    {
        int[] hashCode = new int[covariate.length];
        for (int i = 0; i < covariate.length; i++)
        {
            hashCode[i] = covariate[i].toLowerCase().hashCode();
        }
        double[] hashLevelString = hashLevelString(covariate);
        double[] zeroOneArray = new double[covariate.length];
        for (int k = 0; k < covariate.length; k++)
        {
            if (hashCode[k] == hashLevelString[1])
            {
                zeroOneArray[k] = 1;
            }
        }

        return zeroOneArray;
    }

    /**
     * Obtains the levels of the factors associated with the values of the
     * covariates.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the levels of the factors associated with the values of the
     *         covariates.
     */

    private int[] hashLevel(String[] ...covariate)
    {
        double hashLevel[][] = hashLevelString(covariate);
        Matrix hashLevelM = new Matrix(hashLevel);
        int[] level = new int[hashLevelM.getColumnDimension()];
        for (int i = 0; i < hashLevelM.getColumnDimension(); i++)
        {
            level[i] = 0;
            for (int j = 0; j < hashLevelM.getRowDimension(); j++)
            {
                if (hashLevel[j][i] != 0)
                {
                    level[i] += 1;
                }
            }
        }
        int L = 0;
        for (int i = 0; i < hashLevel.length; i++)
        {
            if (level[i] != 0)
            {
                L += 1;
            }
        }
        int[] level2 = new int[L];
        for (int i = 0; i < L; i++)
        {
            level2[i] = level[i];
        }

        return level2;
    }

    /**
     * Obtains the levels of the factors associated with the values of the
     * covariates.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the levels of the factors associated with the values of the
     *         covariates.
     */

    private int[] level(String[] ...covariate)
    {
        return hashLevel(transpose(covariate));
    }

    /**
     * Sorts the data.
     * @param data the data.
     * @return the sorted data.
     */

    public double[] sort(double[] data)
    {
        double buffer;
        for (int i = 0; i < data.length - 1; i++)
        {
            for (int j = 0; j < data.length - i - 1; j++)
            {
                if (data[j] > data[j + 1])
                {
                    buffer = data[j];
                    data[j] = data[j + 1];
                    data[j + 1] = buffer;
                }
            }
        }

        return data;
    }

    /**
     * Sorts the data.
     * @param data the data.
     * @return the sorted data.
     */

    public int[] sort(int[] data)
    {
        int buffer;
        for (int i = 0; i < data.length - 1; i++)
        {
            for (int j = 0; j < data.length - i - 1; j++)
            {
                if (data[j] > data[j + 1])
                {
                    buffer = data[j];
                    data[j] = data[j + 1];
                    data[j + 1] = buffer;
                }
            }
        }

        return data;
    }

    /**
     * Obtains the string array associated with the levels of the factors.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the string array associated with the levels of the factors.
     */

    private String[][] hashString(String[] ...covariate)
    {
        double[][] hashLevelString = hashLevelString(covariate);
        int[] hashLevel = hashLevel(covariate);
        int dim = 0;
        for (int i = 0; i < hashLevel.length; i++)
        {
            if (dim < hashLevel[i])
            {
                dim = hashLevel[i];
            }
        }
        String[][] hashString = new String[dim][hashLevel.length];
        for (int i = 0; i < hashLevel.length; i++)
        {
            for (int j = 0; j < hashLevelString.length; j++)
            {
                for (int k = 0; k < hashLevel[i]; k++)
                {
                    if (covariate[j][i].toLowerCase().hashCode() ==
                        hashLevelString[k][i])
                    {
                        hashString[k][i] = covariate[j][i];
                    }
                }
            }
        }

        return hashString;
    }

    /**
     * Obtains the levels of the factors associated with the values of the
     * covariate.
     * @param covariate the values of the covariate.
     * @return the levels of the factors associated with the values of the
     *         covariate.
     */

    private double[] hashLevelString(String[] covariate)
    {
        int[] hashCode = new int[covariate.length];
        for (int i = 0; i < covariate.length; i++)
        {
            hashCode[i] = covariate[i].toLowerCase().hashCode();
        }
        int[] sortHashCode = sort(hashCode);
        int hashLevelString[] = new int[covariate.length];
        hashLevelString[0] = sortHashCode[0];
        for (int i = 1; i < sortHashCode.length; i++)
        {
            if (sortHashCode[i] != sortHashCode[i - 1])
            {
                hashLevelString[i] = sortHashCode[i];
            }
        }
        int index = -1;
        double[] newHashLevelString = new double[covariate.length];
        for (int i = 0; i < hashLevelString.length; i++)
        {
            if (hashLevelString[i] != 0)
            {
                index++;
                newHashLevelString[index] = hashLevelString[i];
            }
        }

        return newHashLevelString;
    }

    /**
     * Obtains the levels of the factors associated with the values of the
     * covariates.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the levels of the factors associated with the values of the
     *         covariates.
     */

    private double[][] hashLevelString(String[] ...covariate)
    {
        int[][] hashCode = new int[covariate.length][covariate[0].length];
        int index;
        for (int i = 0; i < covariate.length; i++)
        {
            for (int j = 0; j < covariate[0].length; j++)
            {
                hashCode[i][j] = covariate[i][j].toLowerCase().hashCode();
            }
        }
        double[][] hashDoubleString = new double[covariate.length][covariate[0].
                                      length];
        for (int i = 0; i < covariate[0].length; i++)
        {
            for (int j = 0; j < covariate.length; j++)
            {
                hashDoubleString[j][i] = Double.parseDouble(Integer.toString(
                        hashCode[j][i]));
            }
        }
        Matrix hashMatrix = new Matrix(hashDoubleString);
        double hashLevelString[][] = new double[covariate.length]
                                     [hashCode.length];
        for (int i = 0; i < hashMatrix.getColumnDimension(); i++)
        {
            Matrix hashSubMatrix = hashMatrix.
                                   getMatrix(0, hashCode.length - 1, i, i);
            double[] sortHashSubMatrix =
                    sort(hashSubMatrix.getColumnPackedCopy());
            hashLevelString[0][i] = sortHashSubMatrix[0];
            for (int j = 1; j < hashSubMatrix.getRowDimension(); j++)
            {
                if (sortHashSubMatrix[j] != sortHashSubMatrix[j - 1])
                {
                    hashLevelString[j][i] = sortHashSubMatrix[j];
                }
            }
        }
        Matrix hashLevelStringMatrix = new Matrix(hashLevelString);
        double[][] newHashLevelString = new double[hashLevelStringMatrix.
                                        getRowDimension()]
                                        [hashLevelStringMatrix.
                                        getColumnDimension()];
        for (int i = 0; i < hashLevelStringMatrix.getColumnDimension(); i++)
        {
            index = -1;
            for (int j = 0; j < hashLevelStringMatrix.getRowDimension(); j++)
            {
                if (hashLevelString[j][i] != 0)
                {
                    index++;
                    newHashLevelString[index][i] = hashLevelString[j][i];
                }
            }
        }

        return newHashLevelString;
    }

    /**
     * Obtains the required string covariate matrix.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @param response the responses.
     * @return the required string covariate matrix.
     */

    private String[][] stringCovariate(String[][] covariate,
                                       String[] response)
    {
        String[][] twoDimResponse = new String[response.length][1];
        for (int i = 0; i < response.length; i++)
        {
            twoDimResponse[i][0] = response[i];
        }
        String[][] responseHashString = hashString(twoDimResponse);
        String[] bufferOneDimCovariate = new String[covariate.length];
        for (int i = 0; i < covariate.length; i++)
        {
            bufferOneDimCovariate[i] = covariate[i][0] + "\t";
            for (int j = 1; j < covariate[0].length; j++)
            {
                bufferOneDimCovariate[i] += covariate[i][j] + "\t";
            }
        }
        double[] responseSize = new double[response.length];
        double[] size = new double[covariate.length];
        for (int j = 0; j < covariate.length; j++)
        {
            responseSize[j] = 0;
            for (int i = 0; i < covariate.length; i++)
            {
                if (bufferOneDimCovariate[j].
                    compareToIgnoreCase(bufferOneDimCovariate[i]) == 0)
                {
                    size[j] += 1;
                    if (response[i].hashCode() ==
                        responseHashString[1][0].hashCode())
                    {
                        responseSize[j] += 1;
                    }
                }
            }
        }
        String[][] bufferCovariate =
            new String[covariate.length][covariate[0].length];
        bufferCovariate[0] = covariate[0];
        int index1 = 0;
        int index2, index3;
        for (int i = 1; i < covariate.length; i++)
        {
            index1 += 1;
            index2 = 0;
            for (int j = 0; j < i; j++)
            {
                index3 = 0;
                for (int k = 0; k < covariate[0].length; k++)
                {
                    if (covariate[i][k].equals(bufferCovariate[j][k]))
                    {
                        index3 += 1;
                    }
                }
                if (index3 != covariate[0].length)
                {
                    index2 += 1;
                }
            }
            if (index1 == index2)
            {
                bufferCovariate[i] = covariate[i];
            }
        }
        int nonNullIndex = 0;
        for (int i = 0; i < bufferCovariate.length; i++)
        {
            if (bufferCovariate[i][0] != null)
            {
                nonNullIndex += 1;
            }
        }
        stringCovariate = new String[nonNullIndex][bufferCovariate[0].length];
        classSize = new double[nonNullIndex];
        doubleResponse = new double[nonNullIndex];
        stringCovariate[0] = bufferCovariate[0];
        classSize[0] = size[0];
        doubleResponse[0] = responseSize[0];
        int k = 1;
        boolean isNotNull;
        for (int i = 1; i < nonNullIndex; i++)
        {
            for (int j = k; j < bufferCovariate.length; j++)
            {
                isNotNull = bufferCovariate[j][0] != null;
                if (isNotNull == true)
                {
                    k = j + 1;
                    stringCovariate[i] = bufferCovariate[j];
                    classSize[i] = size[j];
                    doubleResponse[i] = responseSize[j];
                    break;
                }
                else
                {
                    k += 1;
                }
            }
        }

        return stringCovariate;
    }

    /**
     * Obtains the required covariate matrix.
     * @param response the responses.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the required covariate matrix.
     */

    private String[][] covariate(String[] response,
                                 String[] ...covariate)
    {
        return stringCovariate =
            transpose(stringCovariate(transpose(covariate), response));
    }

    /**
     * Specifies the initial IRLS estimate.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the specified IRLS estimate.
     */

    public double[] setInitialEstimate(double[] ...covariate)
    {
        double[] initialEstimate = new double[covariate.length];
        Matrix covMatrix = new Matrix(covariate);
        double[][] covariateTranspose = covMatrix.getArray();
        double a;
        for (int i = 0; i < initialEstimate.length; i++)
        {
            a = 0;
            for (int j = 0; j < covariate[i].length; j++)
            {
                a += covariate[i][j];
            }
            initialEstimate[i] = covariate[i].length / (100 * a);
        }

        return initialEstimate;
    }

    /**
     * Combines both the nominal and numerical covariates
     * (in double array form).
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @return the combined covariate matrix.
     */

    public double[][] combined(double[][] nominalCovariate,
                               double[][] continuousCovariate)
    {
        Matrix nominalMatrix = new Matrix(nominalCovariate);
        Matrix continuousMatrix = new Matrix(continuousCovariate);
        Matrix cov = new Matrix(nominalCovariate.length +
                                continuousCovariate.length,
                                nominalCovariate[0].length);
        cov.setMatrix(0, nominalCovariate.length - 1, 0,
                      nominalCovariate[0].length - 1, nominalMatrix);
        cov.setMatrix(nominalCovariate.length,
                      nominalCovariate.length + continuousCovariate.length - 1,
                      0, nominalCovariate[0].length - 1, continuousMatrix);
        double[][] covariate = cov.getArray();

        return covariate;
    }

    /**
     * Combines both the nominal and numerical covariates
     * (in string array form).
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @return the combined covariate matrix.
     */

    public String[][] combined(String[][] nominalCovariate,
                               String[][] continuousCovariate)
    {
        String[][] combinedCovariate = new String[nominalCovariate.length +
                                       continuousCovariate.length][];
        for (int i = 0; i < nominalCovariate.length; i++)
        {
            combinedCovariate[i] = nominalCovariate[i];
        }
        for (int i = 0; i < continuousCovariate.length; i++)
        {
            combinedCovariate[nominalCovariate.length + i] =
                    continuousCovariate[i];
        }

        return combinedCovariate;
    }

    /**
     * Obtains the transpose of the covariate matrix.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the transpose of the covariate matrix.
     */

    public double[][] transpose(double[] ...covariate)
    {
        return new Matrix(covariate).transpose().getArray();
    }

    /**
     * Obtains the transpose of the covariate matrix.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the transpose of the covariate matrix.
     */

    public String[][] transpose(String[] ...covariate)
    {
        String[][] transposeCovariate =
            new String[covariate[0].length][covariate.length];
        for (int i = 0; i < covariate[0].length; i++)
        {
            for (int j = 0; j < covariate.length; j++)
            {
                transposeCovariate[i][j] = covariate[j][i];
            }
        }

        return transposeCovariate;
    }

    /**
     * Adds a vector corresponding to the intercept to the original covariate
     * matrix.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the covariate matrix with the first row equal to the vector of
     *         which elements all equal to 1.
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
     * Converts a two-dimensional double array to a string array.
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
     * Converts a one-dimensional double array to a string array.
     * @param doubleArray the input double array.
     * @return a string array converted from the input double array.
     */

    public String[] doubleToString(double[] doubleArray)
    {
        return doubleToString(new double[][] {doubleArray})[0];
    }

    /**
     * Obtains the required data.
     * @param option the model used for fitting the data with the choices of
     *               "Logistic" or "LogLinear".
     * @param dataObject the input data.
     * @return the required data for the analysis.
     */

    public Object[] setData(String option,
                            Object ...dataObject)
    {
        Object[] outputObject = new Object[4];
        if (option.equalsIgnoreCase("Logistic"))
        {
            if (dataObject.length == 2 &&
                dataObject[1].getClass().getName().equalsIgnoreCase(
                        "[[Ljava.lang.String;"))
            {
                if (dataObject[0].getClass().getName().equalsIgnoreCase("[D"))
                {
                    dataObject[0] = doubleToString((double[]) dataObject[0]);
                }
                stringCovariate = covariate((String[]) dataObject[0],
                                            (String[][]) dataObject[1]);
                outputObject[0] = doubleResponse;
                outputObject[1] = zeroOneMatrix(stringCovariate);
                outputObject[2] = classSize;
                outputObject[3] = level(stringCovariate);
            }
            else
            {
                if (dataObject[0].getClass().getName().equalsIgnoreCase("[D"))
                {
                    outputObject[0] = dataObject[0];
                }
                else
                {
                    outputObject[0] = zeroOneVector((String[]) dataObject[0]);
                }
                outputObject[2] = oneArray(((double[]) outputObject[0]).length);
                if (dataObject.length == 2 &&
                    dataObject[1].getClass().getName().equalsIgnoreCase("[[D"))
                {
                    outputObject[1] = dataObject[1];
                }
                else if (dataObject.length == 3 &&
                         dataObject[1].getClass().getName().equalsIgnoreCase(
                                 "[[Ljava.lang.String;") &&
                         dataObject[2].getClass().getName().
                         equalsIgnoreCase("[[D"))
                {
                    outputObject[1] = combined(zeroOneMatrix((String[][])
                            dataObject[1]), (double[][]) dataObject[2]);
                    outputObject[3] = level((String[][]) dataObject[1]);
                }
                else
                {
                    throw new IllegalArgumentException("Wrong input data.");
                }
            }
        }
        else
        {
            outputObject[0] = (double[]) dataObject[0];
            outputObject[3] = level((String[][]) dataObject[1]);
            if (dataObject.length == 4)
            {
                outputObject[1] = combined(
                        zeroOneMatrix((String[][]) dataObject[1]),
                        (double[][]) dataObject[2]);
                outputObject[2] = (double[]) dataObject[3];
            }
            else
            {
                outputObject[1] = zeroOneMatrix((String[][]) dataObject[1]);
                outputObject[2] = (double[]) dataObject[2];
            }
        }

        return outputObject;
    }

    /**
     * Constructs a zero array.
     * @param arrayDim the dimension of the zero array.
     * @return a zero array.
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
     * Constructs a array with all elements equal to 1.
     * @param arrayDim the dimension of the required array.
     * @return the array with all elements equal to 1.
     */

    public double[] oneArray(int arrayDim)
    {
        double[] data = new double[arrayDim];
        for (int i = 0; i < arrayDim; i++)
        {
            data[i] = 1.0;
        }

        return data;
    }

}
