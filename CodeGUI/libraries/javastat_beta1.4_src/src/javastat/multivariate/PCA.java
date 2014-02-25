package javastat.multivariate;

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

import Jama.*;

/**
 *
 * <p>Calculates a set of principal components and their variances.</p>
 * <p> </p>
 * <br> Example:
 * <br> double [][] testscores = {
 *               {36, 62, 31, 76, 46, 12, 39, 30, 22, 9, 32, 40, 64,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                36, 24, 50, 42, 2, 56, 59, 28, 19, 36, 54, 14},
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *               {58, 54, 42, 78, 56, 42, 46, 51, 32, 40, 49, 62, 75,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                38, 46, 50, 42, 35, 53, 72, 50, 46, 56, 57, 35},
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *               {43, 50, 41, 69, 52, 38, 51, 54, 43, 47, 54, 51, 70,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                58, 44, 54, 52, 32, 42, 70, 50, 49, 56, 59, 38},
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *               {36, 46, 40, 66, 56, 38, 54, 52, 28, 30, 37, 40, 66,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                62, 55, 52, 38, 22, 40, 66, 42, 40, 54, 62, 29},
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *               {37, 52, 29, 81, 40, 28, 41, 32, 22, 24, 52, 49, 63,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                62, 49, 51, 50, 16, 32, 62, 63, 30, 52, 58, 20}};
 * <br>
 * <br> // Non-null constructor
 * <br> PCA testclass1 = new PCA(0.95, "covariance", testscores);
 * <br> double [] firstComponent = testclass1.principalComponents[0];
 * <br>
 * <br> // Null constructor
 * <br> PCA testclass2 = new PCA();
 * <br> double [][] principalComponents =
 *        testclass2.principalComponents(testscores);
 * <br> double [] variance = testclass2.componentVariance(testscores);
 * <br>
 * <br> // Non-null constructor
 * <br> Hashtable argument1 = new Hashtable();
 * <br> argument1.put(LEVEL, 0.95);
 * <br> argument1.put(COVARIANCE_CHOICE, "covariance");
 * <br> StatisticalAnalysis testclass3 = new PCA(argument1, testscores).
 *        statisticalAnalysis;
 * <br> principalComponents =
 *        (double[][]) testclass3.output.get(PRINCIPAL_COMPONENTS);
 * <br> variance = (double[]) testclass3.output.get(COMPONENT_VARIANCE);
 * <br>
 * <br> // Null constructor
 * <br> Hashtable argument2 = new Hashtable();
 * <br> PCA testclass4 = new PCA(argument2, null);
 * <br> principalComponents =
 *        testclass4.principalComponents(argument2, testscores);
 * <br> variance = testclass4.variance(argument2, testscores);
 * <br>
 * <br> // Obtains the information about the output
 * <br> out.println(testclass3.output.toString());
 * <br> out.println(testclass4.output.toString());
 */

public class PCA extends StatisticalAnalysis
{

    /**
     * The criterion for including just enough components to explain some amount
     * (100%*level) of the variance.
     */

    public double level;

    /**
     * The principal components,
     * <br>principalComponent[j][]: the (j+1)'th principal component with the
     *                              (j+1)'th largest variance.
     */

    public double[][] principalComponents;

    /**
     * The variances of the principal components in decreasing order,
     * <br> variance[j]: the variance of the (j+1)'th principal component.
     */

    public double[] variance;

    /**
     * The covariannce matrix or correlation matrix are of interest, with the
     * choices "covariance" or "correlation".
     */

    public String covChoice;

    /**
     * The input data,
     * <br> data[j]: the data corresponding to the (j+1)'th variable.
     */

    public double[][] data;

    /**
     * The object represents a principal component analysis.
     */

    public StatisticalAnalysis statisticalAnalysis;

    /**
     * The input data.
     */

    private double[][] doubleData;

    /**
     * The variance-covariance matrix for the input variables.
     */

    private double[][] covariance;

    /**
     * The variance-covariance matrix for the input variables.
     */

    private Matrix covarianceMatrix;

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
     * Obtains a set of principal components given the input arguments and data.
     * @param argument the arguments with the following choices,
     * <br> LEVEL, COVARIANCE_CHOICE: complete list of arguments;
     * <br> COVARIANCE: default criterion equal to 0.9 for excluding the
     *                  components;
     * <br> LEVEL: the correlation matrix being used for the principal component
     *             analysis;
     * <br> empty argument: default criterion equal to 0.9 for excluding the
     *                      components and the correlation matrix being used for
     *                      the principal component analysis.
     * <br><br>
     * @param dataObject the input data.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException the selection criterion should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     */

    public PCA(Hashtable argument,
               Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null)
        {
            if (dataObject.getClass().getName().equalsIgnoreCase("[[D"))
            {
                doubleData = (double[][]) dataObject;
            }
            else if (dataObject.getClass().getName().equalsIgnoreCase(
                    "[Ljava.lang.Object;"))
            {
                doubleData = DataManager.castDoubleObject(0, dataObject);
            }
            else
            {
                throw new IllegalArgumentException("Wrong input data.");
            }
            if (argument.size() > 0)
            {
                if (argument.get(LEVEL) != null &&
                    argument.get(COVARIANCE_CHOICE) != null)
                {
                    statisticalAnalysis = new PCA(
                            (Double) argument.get(LEVEL),
                            (String) argument.get(COVARIANCE_CHOICE),
                            doubleData);
                }
                else if ((argument.get(LEVEL) != null ||
                         argument.get(COVARIANCE_CHOICE) != null))
               {
                    if (argument.containsKey(LEVEL))
                    {
                        statisticalAnalysis = new PCA(
                                (Double) argument.get(LEVEL),
                                doubleData);
                    }
                    else if (argument.containsKey(COVARIANCE_CHOICE))
                    {
                        statisticalAnalysis = new PCA(
                                (String) argument.get(COVARIANCE_CHOICE),
                                doubleData);
                    }
                }
                else
                {
                    throw new IllegalArgumentException(
                            "Wrong input argument(s).");
                }
            }
            else
            {
                statisticalAnalysis = new PCA(doubleData);
            }
        }
        else
        {
            statisticalAnalysis = new PCA();
            level = 1.0;
        }
    }

    /**
     * Default PCA constructor.
     */

    public PCA()
    {
        this(1.0);
    }

    /**
     * Obtains a set of principal components with the criterion to retain the
     * principal components equal to level.
     * @param level the criterion for including just enough components to
     *              explain some amount (100%*level) of the variance.
     * @exception IllegalArgumentException the selection criterion should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     */

    public PCA(double level)
    {
        if ((level <= 0.0) || (level > 1))
        {
            throw new IllegalArgumentException(
                    "The selection criterion should be (strictly) positive " +
                    "and not greater than 1.");
        }
        this.level = level;
    }

    /**
     * Obtains a set of principal components given the input data, selection
     * criterion and choices of covariance or correlation matrix.
     * @param level the criterion for including just enough components to
     *              explain some amount (100%*level) of the variance.
     * @param covChoice the covariannce matrix or correlation matrix of
     *                  interest with the choices "covariance" or "correlation".
     * @param data the input data,
     * <br>        data[j]: the data corresponding to the (j+1)'th variable.
     * @exception IllegalArgumentException all rows of the input data must have
     *                                     the same length.
     * @exception IllegalArgumentException the selection criterion should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     */

    public PCA(double level,
               String covChoice,
               double[] ...data)
    {
        if ((level <= 0.0) || (level > 1))
        {
            throw new IllegalArgumentException(
                    "The selection criterion should be (strictly) positive " +
                    "and not greater than 1.");
        }
        this.level = level;
        this.covChoice = covChoice;
        this.data = data;
        principalComponents = principalComponents(covChoice, data);
    }

    /**
     * Obtains a set of principal components with a 0.9 selection criterion
     * given the input data and choices of covariance or correlation matrix.
     * @param covChoice the covariannce matrix or correlation matrix of
     *                  interest with the choices "covariance" or "correlation".
     * @param data the input data,
     * <br>        data[j]: the data corresponding to the (j+1)'th variable.
     * @exception IllegalArgumentException all rows of the input data must have
     *                                     the same length.
     * @exception IllegalArgumentException the selection criterion should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     */

    public PCA(String covChoice,
               double[] ...data)
    {
        this(0.9, covChoice, data);
    }

    /**
     * Obtains a set of principal components with a correlation matrix given
     * the input data and selection criterion.
     * @param level the criterion for including just enough components to
     *              explain some amount (100%*level) of the variance.
     * @param data the input data,
     * <br>        data[j]: the data corresponding to the (j+1)'th variable.
     * @exception IllegalArgumentException all rows of the input data must have
     *                                     the same length.
     * @exception IllegalArgumentException the selection criterion should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     */

    public PCA(double level,
               double[] ...data)
    {
        this(level, "correlation", data);
    }

    /**
     * Obtains a set of principal components with a correlation matrix and a
     * 0.9 selection criterion given the input data.
     * @param data the input data,
     * <br>        data[j]: the data corresponding to the (j+1)'th variable.
     * @exception IllegalArgumentException all rows of the input data must have
     *                                     the same length.
     * @exception IllegalArgumentException the selection criterion should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     */

    public PCA(double[] ...data)
    {
        this(0.9, "correlation", data);
    }

    /**
     * Obtains a set of principal components.
     * @param argument the argument with the following choices,
     * <br> COVARIANCE: the covariannce matrix or correlation matrix of
     *                  interest with the choices "covariance" or "correlation";
     * <br> empty argument: the correlation matrix being used for the principal
     *                      component analysis.
     * <br><br>
     * @param dataObject the input data.
     * @return the principal components,
     * <br>    principalComponent[j][]: the (j+1)'th principal component with
     *                                  the (j+1)'th largest variance.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException all rows of the input data must have
     *                                     the same length.
     */

    public double[][] principalComponents(Hashtable argument,
                                          Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (argument.get(COVARIANCE_CHOICE) != null &&
            dataObject != null &&
            dataObject.getClass().getName().equalsIgnoreCase("[[D"))
        {
            principalComponents = principalComponents((String) argument.get(
                    COVARIANCE_CHOICE),
                    (double[][]) dataObject);
        }
        else if (dataObject != null &&
                 dataObject.getClass().getName().equalsIgnoreCase("[[D"))
        {
            principalComponents = principalComponents((double[][]) dataObject);
        }
        else if (dataObject != null &&
                 dataObject.getClass().getName().equalsIgnoreCase(
                     "[Ljava.lang.Object;"))
        {
            principalComponents = principalComponents(DataManager.
                    castDoubleObject(0, dataObject));
        }
        else
        {
            throw new IllegalArgumentException("Wrong input arguments or " +
                                               "data.");
        }

        return principalComponents;
    }

    /**
     * Obtains a set of principal components.
     * @param covChoice the covariannce matrix or correlation matrix of
     *                  interest with the choices "covariance" or "correlation".
     * @param data the input data,
     * <br>        data[j]: the data corresponding to the (j+1)'th variable.
     * @return the principal components,
     * <br>    principalComponent[j][]: the (j+1)'th principal component with
     *                                  the (j+1)'th largest variance.
     * @exception IllegalArgumentException all rows of the input data must have
     *                                     the same length.
     */

    public double[][] principalComponents(String covChoice,
                                          double[] ...data)
    {
        this.covChoice = covChoice;
        this.data = data;
        dataManager = new DataManager();
        dataManager.checkDimension(data);
        basicStatistics = new BasicStatistics();
        if (covChoice.equalsIgnoreCase("covariance"))
        {
            covariance = basicStatistics.covarianceMatrix(data);
        }
        else
        {
            covariance = basicStatistics.correlationMatrix(data);
        }
        covarianceMatrix = new Matrix(covariance);
        EigenvalueDecomposition eigenCovMatrix = new EigenvalueDecomposition(
                covarianceMatrix);
        double[] eigenvalueCovMatrix = eigenCovMatrix.getRealEigenvalues();
        double[][] eigenvectorCov = eigenCovMatrix.getV().getArray();
        dataManager.dataSort(eigenvalueCovMatrix, eigenvectorCov);
        int indices = basicStatistics.pcaNumber(eigenvalueCovMatrix, level);
        principalComponents = new double[data.length - indices][data.length];
        variance = new double[data.length - indices];
        for (int i = data.length - 1; i >= indices; i--)
        {
            variance[data.length - 1 - i] = eigenvalueCovMatrix[i];
            for (int k = 0; k < data.length; k++)
            {
                principalComponents[data.length - 1 -i][k] =
                        eigenvectorCov[k][i];
            }
        }
        output.put(PRINCIPAL_COMPONENTS, principalComponents);
        output.put(COMPONENT_VARIANCE, variance);

        return principalComponents;
    }

    /**
     * Obtains a set of principal components based on the correlation matrix.
     * @param data the input data,
     * <br>        data[j]: the data corresponding to the (j+1)'th variable.
     * @return the principal components,
     * <br>    principalComponent[j][]: the (j+1)'th principal component with
     *                                  the (j+1)'th largest variance.
     * @exception IllegalArgumentException all rows of the input data must have
     *                                     the same length.
     */

    public double[][] principalComponents(double[] ...data)
    {
        return principalComponents("correlation", data);
    }

    /**
     * The variances of the principal components.
     * @param argument the argument with the following choices,
     * <br> COVARIANCE: the covariannce matrix or correlation matrix of
     *                  interest with the choices "covariance" or "correlation";
     * <br> empty argument: the correlation matrix being used for the principal
     *                      component analysis.
     * <br><br>
     * @param dataObject the input data.
     * @return the variances of the principal components,
     * <br>    variance[j]: the (j+1)'th largest variance.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException all rows of the input data must have
     *                                     the same length.
     */

    public double[] variance(Hashtable argument,
                             Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (argument.get(COVARIANCE_CHOICE) != null &&
            dataObject != null &&
            dataObject.getClass().getName().equalsIgnoreCase("[[D"))
        {
            variance = variance((String) argument.get(COVARIANCE_CHOICE),
                                (double[][]) dataObject);
        }
        else if (dataObject != null &&
                 dataObject.getClass().getName().equalsIgnoreCase("[[D"))
        {
            variance = variance((double[][]) dataObject);
        }
        else if (dataObject != null &&
                 dataObject.getClass().getName().equalsIgnoreCase(
                     "[Ljava.lang.Object;"))
        {
            variance = variance(DataManager.castDoubleObject(0, dataObject));
        }
        else
        {
            throw new IllegalArgumentException("Wrong input arguments or " +
                                               "data.");
        }

        return variance;
    }

    /**
     * The variances of the principal components.
     * @param covChoice the covariannce matrix or correlation matrix of
     *                  interest with the choices "covariance" or "correlation".
     * @param data the input data,
     * <br>        data[j]: the data corresponding to the (j+1)'th variable.
     * @return the variances of the principal components,
     * <br>    variance[j]: the (j+1)'th largest variance.
     * @exception IllegalArgumentException all rows of the input data must have
     *                                     the same length.
     */

    public double[] variance(String covChoice,
                             double[] ...data)
    {
        this.covChoice = covChoice;
        this.data = data;
        principalComponents = principalComponents(covChoice, data);

        return variance;
    }

    /**
     * The variances of the principal components based on the correlation
     * matrix.
     * @param data the input data,
     * <br>        data[j]: the data corresponding to the (j+1)'th variable.
     * @return the variances of the principal components,
     * <br>    variance[j]: the (j+1)'th largest variance.
     * @exception IllegalArgumentException all rows of the input data must have
     *                                     the same length.
     */

    public double[] variance(double[] ...data)
    {
        return variance("correlation", data);
    }

}
