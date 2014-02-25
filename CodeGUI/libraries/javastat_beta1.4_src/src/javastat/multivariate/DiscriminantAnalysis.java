package javastat.multivariate;

/**
 * <p>Title: javastat</p>
 * <p>Description: JAVA programs for statistical computations</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Tung Hai University</p>
 * @author Wen Hsiang Wei
 * @version 1.4
 */

import java.util.Hashtable;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

import javastat.StatisticalAnalysis;
import javastat.util.BasicStatistics;
import javastat.util.DataManager;
import static javastat.util.Output.*;

/**
 *
 * <p>Calculates a set of linear discriminants and obtains the predictions for
 * the data.</p>
 * <p> </p>
 * <br> Example:
 * <br> DataManager dm = new DataManager();
 * <br> double [][] testdata = new double [4][];
 * <br> dm.scanFileToMatrix("C:\\iris.txt", testdata, 4);
 * <br> double [] testgroup = new double[150];
 * <br> for(int j = 0; j < 3; j++)
 * <br> {
 * <br> &nbsp;&nbsp;&nbsp;
 *        for(int i = 0; i < 50; i++)
 * <br> &nbsp;&nbsp;&nbsp;
 *        {
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        testgroup[j * 50 + i] = j + 1;
 * <br> &nbsp;&nbsp;&nbsp;
 *        }
 * <br> }
 * <br>
 * <br> // Non-null constructor without predicting group membership of any data
 * <br> DiscriminantAnalysis testclass1 =
 *        new DiscriminantAnalysis(testgroup, testdata);
 * <br> double [][] linearDiscriminants1 = testclass1.linearDiscriminants;
 * <br>
 * <br> // Non-null constructor with the group memberships of some data
 *         predicted
 * <br> DiscriminantAnalysis testclass3 =
 *        new DiscriminantAnalysis(testgroup, testdata, testdata);
 * <br> double [][] linearDiscriminants2 = testclass3.linearDiscriminants;
 * <br> int [] predictedGroup = testclass3.predictedGroup;
 * <br>
 * <br> // Null constructor
 * <br> DiscriminantAnalysis testclass2 = new DiscriminantAnalysis();
 * <br> linearDiscriminants1 =
 *        testclass2.linearDiscriminant(testgroup, testdata);
 * <br> double [][] predata = new double [4][];
 * <br> dm.scanFileToMatrix("C:\\iris2.txt", predata, 4);
 * <br> int [] predictedGroup2 =
 *        testclass2.predictedGroup(testgroup, testdata, predata);
 * <br>
 * <br> // Non-null constructor
 * <br> Hashtable argument1 = new Hashtable();
 * <br> StatisticalAnalysis testclass4 =
 * <br> &nbsp;&nbsp;&nbsp;
 *        new DiscriminantAnalysis(argument1, testgroup, testdata).
 *        statisticalAnalysis;
 * <br> linearDiscriminants1 =
 *        (double[][]) testclass4.output.get(LINEAR_DISCRIMINANTS);
 * <br>
 * <br> // Null constructor
 * <br> Hashtable argument2 = new Hashtable();
 * <br> DiscriminantAnalysis testclass5 =
 *        new DiscriminantAnalysis(argument2, null);
 * <br> linearDiscriminants2 =
 *        testclass5.linearDiscriminants(argument2, testgroup, testdata);
 * <br> predictedGroup2 =
 *        testclass5.predictedGroup(argument2, testgroup, testdata, predata);
 * <br>
 * <br> // Null constructor
 * <br> Hashtable argument3 = new Hashtable();
 * <br> StatisticalAnalysis testclass6 =
 * <br> &nbsp;&nbsp;&nbsp;
 *        new DiscriminantAnalysis(argument3, testgroup, testdata, testdata).
 *        statisticalAnalysis;
 * <br> linearDiscriminants3 =
 *        (double[][]) testclass6.output.get(LINEAR_DISCRIMINANTS);
 * <br> predictedGroup3 = (int[]) testclass6.output.get(PREDICTED_GROUP);
 * <br> preg = predictedGroup3[predictedGroup3.length - 1];
 * <br>
 * <br> // Obtains the information about the output
 * <br> out.println(testclass4.output.toString());
 * <br> out.println(testclass5.output.toString());
 * <br> out.println(testclass6.output.toString());
 */

public class DiscriminantAnalysis extends StatisticalAnalysis
{

    /**
     * The groups the new data are classified to.
     */

    public int[] predictedGroup;

    /**
     * The linear discriminants,
     * <br> discriminants[j][]: the (j+1)'th linear discriminant.
     */

    public double[][] linearDiscriminants;

    /**
     * The groups the training data belong to.
     */

    public double[] groups;

    /**
     * The values of the covariates of the training data,
     * <br> covariate[j]: the (j+1)'th covariate vector.
     */

    public double[][] covariate;

    /**
     * The values of the covariates of the new data,
     * <br> newCovariate[j]: the (j+1)'th covariate vector.
     */

    public double[][] newCovariate;

    /**
     * The variable indicating if there are new data to be classified,
     * <br> isPredictive = true: there are new data to be classified;
     * <br> isPredictive = false: otherwise.
     */

    public boolean isPredictive;

    /**
     * The object represents a linear discriminant analysis.
     */

    public StatisticalAnalysis statisticalAnalysis;

    /**
     * The index indicating which group the new data should be classified.
     */

    private int predictedGroupIndex;

    /**
     * The index for the first data of a new group.
     */

    private int bufferGroupIndex;


    /**
     * The index used to locate the last observation in each group.
     */

    private int covariateIndex;

    /**
     * The index indicating how many linear discriminants should be included.
     */

    private int eigenValueInd;

    /**
     * The number of data in each group.
     */

    private int[] groupIndex;

    /**
     * The minimum distance between the predicted responses and the mean
     * responses for different groups.
     */

    private double minimumDistance;

    /**
     * The vector of overall means.
     */

    private double[] overallMean;

    /**
     * The eigenvalues of the product of the square root matrix of
     * spoolInverseMatrix, the sample between matrix and the square root matrix
     * of spoolInverseMatrix.
     */

    private double[] eigenValues;

    /**
     * The eigenvalues of the inverse of the sample within matrix.
     */

    private double[] spoolInverseEigenValues;

    /**
     * The covariate matrix.
     */

    private double[][] covariateBuffer;

    /**
     * The matrix of sample means for different groups.
     */

    private double[][] meanVector;

    /**
     * The linear discriminants in reverse order.
     */

    private double[][] InverseOrderDiscriminants;

    /**
     * The eigenvectors of the product of the square root matrix of
     * spoolInverseMatrix, the sample between matrix and the square root matrix
     * of spoolInverseMatrix.
     */

    private double[][] eigenVectors;

    /**
     * The eigenvalues of the square root matrix of the inverse of the sample
     * within matrix.
     */

    private double[][] spoolInverseSQREigenValues;

    /**
     * The vector of sample means for population 1 in matrix form.
     */

    private Matrix meanMatrix1;

    /**
     * The vector of sample means for population 2 in matrix form.
     */

    private Matrix meanMatrix2;

    /**
     * The difference between the vector of the overall means and the vector of
     * the sample means for some population in matrix form.
     */

    private Matrix meanDifferenceMatrix;

    /**
     * The sample between matrix.
     */

    private Matrix SSBMatrix;

    /**
     * The vector of overall means in matrix form.
     */

    private Matrix overallMeanMatrix;

    /**
     * The sample within matrix.
     */

    private Matrix spoolMatrix;

    /**
     * The inverse of the sample within matrix.
     */

    private Matrix spoolInverseMatrix;

    /**
     * The linear discriminants in matrix form.
     */

    private Matrix discriminantMatrix;

    /**
     * The vector of precicted responses in matrix form.
     */

    private Matrix predictionMatrix;

    /**
     * The submatrix of predictionMatrix.
     */

    private Matrix predictionSubMatrix;

    /**
     * The vector of mean precicted responses in matrix form.
     */

    private Matrix meanPredictionMatrix;

    /**
     * The eigenvectors of the inverse of the sample within matrix in matrix
     * form.
     */

    private Matrix spoolInverseEigenVectorMatrix;

    /**
     * The eigenvectors of the square root matrix of spoolInverseMatrix.
     */

    private Matrix spoolInverseSQRMatrix;

    /**
     * The differences between the predicted responses and the mean responses
     * for different groups in matrix form.
     */

    private Matrix differenceMatrix;

    /**
     * The sum of the square differences between the predicted responses and
     * the mean responses for different groups in matrix form.
     */

    private Matrix differenceSquareMatrix;

    /**
     * The eigendecomposition of the product of the square root matrix of
     * spoolInverseMatrix, the sample between matrix and the square root matrix
     * of spoolInverseMatrix.
     */

    private EigenvalueDecomposition eigenDecomposition;

    /**
     * The eigendecomposition of the inverse of the sample within matrix.
     */

    private EigenvalueDecomposition spoolInverseEigenDecomposition;

    /**
     * The class contains the collections of some basic methods used in
     * statistical computations.
     */

    private BasicStatistics basicStatistics;

    /**
     * The class contains the collections of some basic methods for manipulating
     * the data.
     */

    private DataManager dataManager;

    /**
     * Constructs a class for a linear discriminant analysis.
     */

    public DiscriminantAnalysis() {}

    /**
     * Constructs a class for a discriminant analysis given the input argument
     * and data.
     * @param argument the empty argument.
     * @param dataObject the input data, including the groups the training data
     *                   belong to, the values of the covariates of the training
     *                   data, and if exists, the values of the covariates of
     *                   the new data.
     * @exception IllegalArgumentException all rows of the matrix covariate
     *                                     must have the same length.
     * @exception IllegalArgumentException all rows of the matrix newCovariate
     *                                     must have the same length.
     */

    public DiscriminantAnalysis(Hashtable argument,
                                Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null &&
            dataObject.length == 3 &&
            dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
            dataObject[1].getClass().getName().equalsIgnoreCase("[[D") &&
            dataObject[2].getClass().getName().equalsIgnoreCase("[[D"))
        {
            statisticalAnalysis = new DiscriminantAnalysis(
                    (double[]) dataObject[0], (double[][]) dataObject[1],
                    (double[][]) dataObject[2]);
        }
        else if (dataObject != null &&
                 dataObject.length == 2 &&
                 dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
                 dataObject[1].getClass().getName().equalsIgnoreCase("[[D"))
        {
            statisticalAnalysis = new DiscriminantAnalysis(
                    (double[]) dataObject[0], (double[][]) dataObject[1]);
        }
        else if (dataObject != null &&
                 dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
                 (dataObject.getClass().getName().equalsIgnoreCase(
                     "[Ljava.lang.Object;") ||
                 dataObject.getClass().getName().equalsIgnoreCase("[[D")))
       {
            statisticalAnalysis = new DiscriminantAnalysis(
                    (double[]) dataObject[0],
                    DataManager.castDoubleObject(1, dataObject));
        }
        else if (dataObject == null)
        {
            statisticalAnalysis = new DiscriminantAnalysis();
        }
        else
        {
            throw new IllegalArgumentException("Wrong input data.");
        }
    }

    /**
     * Constructs a class for a discriminant analysis given the groups and
     * covariates of the training data and new data of which groups to be
     * predicted.
     * @param groups the groups the training data belong to.
     * @param covariate the values of the covariates of the training data,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @param newCovariate the values of the covariates of the new data,
     * <br>                newCovariate[j]: the (j+1)'th covariate vector.
     * @exception IllegalArgumentException all rows of the matrix covariate
     *                                     must have the same length.
     * @exception IllegalArgumentException all rows of the matrix newCovariate
     *                                     must have the same length.
     */

    public DiscriminantAnalysis(double[] groups,
                                double[][] covariate,
                                double[][] newCovariate)
    {
        this.groups = groups;
        this.covariate = covariate;
        this.newCovariate = newCovariate;
        isPredictive = true;
        linearDiscriminants = linearDiscriminants(groups, covariate,
                                                  newCovariate);
    }

    /**
     * Constructs a class for a discriminant analysis given the groups and
     * covariates of the training data.
     * @param groups the groups the training data belong to.
     * @param covariate the values of the covariates of the training data,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @exception IllegalArgumentException all rows of the matrix covariate
     *                                     must have the same length.
     */

    public DiscriminantAnalysis(double[] groups,
                                double[] ...covariate)
    {
        this.groups = groups;
        this.covariate = covariate;
        isPredictive = false;
        linearDiscriminants = linearDiscriminants(groups, covariate);
    }

    /**
     * Obtains the linear discriminants.
     * @param argument the empty argument.
     * @param dataObject the input data, including the groups the training data
     *                   belong to, the values of the covariates of the training
     *                   data, and if exists, the values of the covariates of
     *                   the new data.
     * @return the linear discriminants,
     * <br>    linearDiscriminants[j][]: the (j+1)'th linear discriminant.
     * @exception IllegalArgumentException all rows of the matrix covariate
     *                                     must have the same length.
     * @exception IllegalArgumentException all rows of the matrix newCovariate
     *                                     must have the same length.
     */

    public double[][] linearDiscriminants(Hashtable argument,
                                          Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null &&
            dataObject.length == 3 &&
            dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
            dataObject[1].getClass().getName().equalsIgnoreCase("[[D") &&
            dataObject[2].getClass().getName().equalsIgnoreCase("[[D"))
        {
            linearDiscriminants = linearDiscriminants((double[]) dataObject[0],
                    (double[][]) dataObject[1], (double[][]) dataObject[2]);
        }
        else if (dataObject != null &&
                 dataObject.length == 2 &&
                 dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
                 dataObject[1].getClass().getName().equalsIgnoreCase("[[D"))
        {
            linearDiscriminants = linearDiscriminants((double[]) dataObject[0],
                    (double[][]) dataObject[1]);
        }
        else if (dataObject != null &&
                 dataObject.length >= 2 &&
                 dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
                 (dataObject.getClass().getName().equalsIgnoreCase(
                     "[Ljava.lang.Object;") ||
                 dataObject.getClass().getName().equalsIgnoreCase("[[D")))
       {
            linearDiscriminants = linearDiscriminants((double[]) dataObject[0],
                    DataManager.castDoubleObject(1, dataObject));
        }
        else
        {
            throw new IllegalArgumentException("Wrong input arguments or " +
                                               "data.");
        }

        return linearDiscriminants;
    }

    /**
     * Obtains the linear discriminants.
     * @param groups the groups the training data belong to.
     * @param covariate the values of the covariates of the training data,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the linear discriminants,
     * <br>    linearDiscriminants[j][]: the (j+1)'th linear discriminant.
     * @exception IllegalArgumentException all rows of the matrix covariate
     *                                     must have the same length.
     */

    public double[][] linearDiscriminants(double[] groups,
                                          double[] ...covariate)
    {
        this.groups = groups;
        this.covariate = covariate;
        isPredictive = false;
        linearDiscriminants = linearDiscriminants(groups, covariate,
                                                  this.newCovariate);

        return linearDiscriminants;
    }

    /**
     * Obtains the linear discriminants and predictions of the new data.
     * @param groups the groups the training data belong to.
     * @param covariate the values of the covariates of the training data,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @param newCovariate the values of the covariates of the new data,
     * <br>                newCovariate[j]: the (j+1)'th covariate vector.
     * @return the linear discriminants,
     * <br>    linearDiscriminants[j][]: the (j+1)'th linear discriminant.
     * @exception IllegalArgumentException all rows of the matrix covariate
     *                                     must have the same length.
     * @exception IllegalArgumentException all rows of the matrix newCovariate
     *                                     must have the same length.
     */

    public double[][] linearDiscriminants(double[] groups,
                                          double[][] covariate,
                                          double[][] newCovariate)
    {
        this.groups = groups;
        this.covariate = covariate;
        BasicStatistics.convergenceCriterion =
            new double[]{Math.pow(10, -7), Math.pow(10, -3)};
        basicStatistics = new BasicStatistics();
        dataManager = new DataManager();
        isPredictive = true;
        if (newCovariate == null)
        {
            isPredictive = false;
        }
        else
        {
            dataManager.checkDimension(newCovariate);
            predictedGroup = new int[newCovariate[0].length];
            this.newCovariate = newCovariate;
        }
        dataManager.dataSort(groups, covariate);
        groupIndex = dataManager.groupIndex(groups);
        covariateIndex = 0;
        overallMean = basicStatistics.meanVector(covariate);
        overallMeanMatrix = new Matrix(overallMean, overallMean.length);
        spoolMatrix = new Matrix(covariate.length, covariate.length, 0.0);
        SSBMatrix = new Matrix(covariate.length, covariate.length, 0.0);
        meanVector = new double[groupIndex.length][covariate.length];
        for (int j = 0; j < groupIndex.length; j++)
        {
            covariateBuffer = new double[covariate.length][groupIndex[j]];
            for (int l = 0; l < groupIndex[j]; l++)
            {
                for (int k = 0; k < covariate.length; k++)
                {
                    covariateBuffer[k][l] = covariate[k][covariateIndex + l];
                }
            }
            covariateIndex += groupIndex[j];
            meanVector[j] = basicStatistics.meanVector(covariateBuffer);
            meanDifferenceMatrix = new Matrix(meanVector[j],
                                              meanVector[j].length).
                                   minus(overallMeanMatrix);
            SSBMatrix.plusEquals(meanDifferenceMatrix.times(
                    meanDifferenceMatrix.
                    transpose()).timesEquals(groupIndex[j]));
            spoolMatrix.plusEquals(new Matrix(basicStatistics.covarianceMatrix(
                    covariateBuffer)).timesEquals((groupIndex[j] - 1)));
        }
        spoolInverseMatrix = spoolMatrix.inverse();
        if (groupIndex.length == 2)
        {
            meanMatrix1 = new Matrix(meanVector[0], meanVector[0].length);
            meanMatrix2 = new Matrix(meanVector[1], meanVector[1].length);
            discriminantMatrix = spoolInverseMatrix.times(meanMatrix1.minus(
                    meanMatrix2));
            discriminantMatrix.timesEquals(groups.length - groupIndex.length);
            if (isPredictive)
            {
                predictionMatrix = discriminantMatrix.transpose().times(
                        new Matrix(newCovariate));
                meanPredictionMatrix = discriminantMatrix.transpose().times(
                        meanMatrix1.plusEquals(meanMatrix2).timesEquals(0.5));
                for (int l = 0; l < newCovariate[0].length; l++)
                {
                    if (predictionMatrix.get(0, l) >=
                        meanPredictionMatrix.get(0, 0))
                    {
                        predictedGroup[l] = (int) groups[0];
                    }
                    else
                    {
                        predictedGroup[l] = (int) groups[groupIndex[0]];
                    }
                }
            }
        }
        else
        {
            spoolInverseMatrix.timesEquals(groups.length - groupIndex.length);
            spoolInverseEigenDecomposition = new EigenvalueDecomposition(
                    spoolInverseMatrix);
            spoolInverseEigenValues = spoolInverseEigenDecomposition.
                                      getRealEigenvalues();
            spoolInverseSQREigenValues = dataManager.zeroArray(
                    spoolInverseEigenValues.length,
                    spoolInverseEigenValues.length);
            for (int i = 0; i < spoolInverseEigenValues.length; i++)
            {
                spoolInverseSQREigenValues[i][i] = Math.sqrt(
                        spoolInverseEigenValues[i]);
            }
            spoolInverseEigenVectorMatrix = spoolInverseEigenDecomposition.
                                            getV().transpose();
            spoolInverseSQRMatrix = spoolInverseEigenDecomposition.getV().times(
                    new Matrix(spoolInverseSQREigenValues)).times(
                            spoolInverseEigenVectorMatrix);
            eigenDecomposition = new EigenvalueDecomposition(
                    spoolInverseSQRMatrix.times(SSBMatrix).times(
                    spoolInverseSQRMatrix));
            eigenValues = dataManager.abs(eigenDecomposition.
                                          getRealEigenvalues());
            eigenVectors = eigenDecomposition.getV().getArray();
            dataManager.dataSort(eigenValues, eigenVectors);
            eigenValueInd = 0;
            for (int i = 0; i < eigenValues.length; i++)
            {
                if (eigenValues[i] < BasicStatistics.convergenceCriterion[0] ||
                    (eigenValues[i] / basicStatistics.sum(eigenValues)) <
                    BasicStatistics.convergenceCriterion[1])
                {
                    eigenValueInd++;
                }
            }
            discriminantMatrix = spoolInverseSQRMatrix.times(new Matrix(
                    eigenVectors).getMatrix(0, eigenValues.length - 1,
                                            eigenValueInd,
                                            eigenValues.length - 1));
            if (isPredictive)
            {
                predictionMatrix = discriminantMatrix.transpose().times(new
                        Matrix(newCovariate));
                meanPredictionMatrix = discriminantMatrix.transpose().times(new
                        Matrix(meanVector).transpose());
                for (int s = 0; s < newCovariate[0].length; s++)
                {
                    predictionSubMatrix = predictionMatrix.getMatrix(0,
                            eigenValues.length - 1 - eigenValueInd, s, s);
                    differenceMatrix = predictionSubMatrix.minus(
                            meanPredictionMatrix.getMatrix(0,
                            eigenValues.length - 1 - eigenValueInd, 0, 0));
                    minimumDistance = differenceMatrix.transpose().times(
                            differenceMatrix).get(0, 0);
                    predictedGroupIndex = (int) groups[0];
                    bufferGroupIndex = 0;
                    for (int m = 1; m < groupIndex.length; m++)
                    {
                        bufferGroupIndex += (int) groupIndex[m - 1];
                        differenceMatrix = predictionSubMatrix.minus(
                                meanPredictionMatrix.getMatrix(0,
                                eigenValues.length - 1 - eigenValueInd, m, m));
                        differenceSquareMatrix = differenceMatrix.transpose().
                                                 times(differenceMatrix);
                        if (differenceSquareMatrix.get(0, 0) < minimumDistance)
                        {
                            predictedGroupIndex =
                                    (int) groups[bufferGroupIndex];
                            minimumDistance = differenceSquareMatrix.get(0, 0);
                        }
                    }
                    predictedGroup[s] = predictedGroupIndex;
                }
            }
        }
        InverseOrderDiscriminants = discriminantMatrix.transpose().getArray();
        linearDiscriminants = new double[InverseOrderDiscriminants.length][];
        for (int m = 0; m < InverseOrderDiscriminants.length; m++)
        {
            linearDiscriminants[m] = InverseOrderDiscriminants[
                                     InverseOrderDiscriminants.length - 1 - m];
        }
        output.put(LINEAR_DISCRIMINANTS, linearDiscriminants);
        if (predictedGroup != null)
        {
            output.put(PREDICTED_GROUP, predictedGroup);
        }

        return linearDiscriminants;
    }

    /**
     * Classifies the data.
     * @param argument the empty argument.
     * @param dataObject the input data, including the groups the training data
     *                   belong to, the values of the covariates of the training
     *                   data, and if exists, the values of the covariates of
     *                   the new data.
     * @return the groups the new data belong to.
     * @exception IllegalArgumentException all rows of the matrix covariate
     *                                     must have the same length.
     * @exception IllegalArgumentException all rows of the matrix newCovariate
     *                                     must have the same length.
     */

    public int[] predictedGroup(Hashtable argument,
                                Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null &&
            dataObject.length == 3 &&
            dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
            dataObject[1].getClass().getName().equalsIgnoreCase("[[D") &&
            dataObject[2].getClass().getName().equalsIgnoreCase("[[D"))
        {
            predictedGroup = predictedGroup((double[]) dataObject[0],
                                            (double[][]) dataObject[1],
                                            (double[][]) dataObject[2]);
        }
        else if (dataObject != null &&
                 dataObject.length == 2 &&
                 dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
                 dataObject[1].getClass().getName().equalsIgnoreCase("[[D"))
        {
            predictedGroup = predictedGroup((double[]) dataObject[0],
                                            (double[][]) dataObject[1]);
        }
        else if (dataObject != null &&
                 dataObject.length >= 2 &&
                 dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
                 (dataObject.getClass().getName().equalsIgnoreCase(
                     "[Ljava.lang.Object;") ||
                 dataObject.getClass().getName().equalsIgnoreCase("[[D")))
       {
            predictedGroup = predictedGroup((double[]) dataObject[0],
                                            DataManager.castDoubleObject(1,
                                            dataObject));
        }
        else
        {
            throw new IllegalArgumentException("Wrong input arguments or " +
                                               "data.");
        }

        return predictedGroup;
    }

    /**
     * Classifies the new data.
     * @param groups the groups the training data belong to.
     * @param covariate the values of the covariates of the training data,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @param newCovariate the values of the covariates of the new data,
     * <br>                newcovariate[j]: the (j+1)'th covariate vector.
     * @return the groups the new data belong to.
     * @exception IllegalArgumentException all rows of the matrix covariate
     *                                     must have the same length.
     * @exception IllegalArgumentException all rows of the matrix newCovariate
     *                                     must have the same length.
     */

    public int[] predictedGroup(double[] groups,
                                double[][] covariate,
                                double[][] newCovariate)
    {
        this.groups = groups;
        this.covariate = covariate;
        this.newCovariate = newCovariate;
        isPredictive = true;
        linearDiscriminants(groups, covariate, newCovariate);

        return predictedGroup;
    }

    /**
     * Classifies the training data.
     * @param groups the groups the training data belong to.
     * @param covariate the values of the covariates of the training data,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the groups the training data belong to.
     * @exception IllegalArgumentException all rows of the matrix covariate
     *                                     must have the same length.
     */

    public int[] predictedGroup(double[] groups,
                                double[] ...covariate)
    {
        this.groups = groups;
        this.covariate = covariate;
        isPredictive = true;
        linearDiscriminants(groups, covariate, covariate);

        return predictedGroup;
    }

}
