package javastat.regression.glm;

/**
 * <p>Title: javastat</p>
 * <p>Description: JAVA programs for statistical computations</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Tung Hai University</p>
 * @author Wen Hsiang Wei and B. J. Guo
 * @version 1.4
 */

import java.util.*;

import javastat.*;
import static javastat.regression.glm.LinkFunction.*;
import static javastat.util.Argument.*;
import static javastat.util.Output.*;
import javastat.util.*;

import Jama.*;

/**
 *
 * <p>Fits a logistic regression model.</p>
 * <p> </p>
 * <br> Example:
 * <br> String []  binaryResponse = {"s", "d", "s", "d", "s", "d", "d", "s",
 *                                   "d", "s", "d", "d"};
 * <br> String [][] nominalCovariate = {{"a", "a", "b", "b", "c", "c", "a", "a",
 *                                       "b", "c", "c", "c"},
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                                      {"T", "T", "T", "F", "T", "F", "T", "F",
 *                                       "F", "T", "F", "F"}};
 * <br>
 * <br> // Non-null constructor
 * <br> LogisticRegression testclass1 =
 *        new LogisticRegression(binaryResponse, nominalCovariate);
 * <br> double[] coefficients = testclass1.coefficients;
 * <br> double[][] confidenceInterval = testclass1.confidenceInterval;
 * <br> double [] testStatistic = testclass1.testStatistic;
 * <br> double [] pValue = testclass1.pValue;
 * <br> double [][] devianceTable = testclass1.devianceTable;
 * <br>
 * <br> // Null constructor
 * <br> LogisticRegression testclass2 = new LogisticRegression();
 * <br> coefficients = testclass2.coefficients(binaryResponse,
 *        nominalCovariate);
 * <br> confidenceInterval =
 *        testclass2.confidenceInterval(0.1, binaryResponse, nominalCovariate);
 * <br> testStatistic =
 *        testclass2.testStatistic(binaryResponse, nominalCovariate);
 * <br> pValue = testclass2.pValue(binaryResponse, nominalCovariate);
 * <br> devianceTable =
 *        testclass2.devianceTable(binaryResponse, nominalCovariate);
 * <br>
 * <br> // Non-null constructor
 * <br> Hashtable argument1 = new Hashtable();
 * <br> StatisticalAnalysis testclass3 =
 * <br> &nbsp;&nbsp;&nbsp;
 *        new LogisticRegression(argument1, binaryResponse, nominalCovariate).
 *        statisticalAnalysis;
 * <br> coefficients = (double[]) testclass3.output.get(COEFFICIENTS);
 * <br> confidenceInterval =
 *        (double[][]) testclass3.output.get(CONFIDENCE_INTERVAL);
 * <br> testStatistic = (double[]) testclass3.output.get(TEST_STATISTIC);
 * <br> pValue = (double[]) testclass3.output.get(PVALUE);
 * <br> devianceTable = (double[][]) testclass3.output.get(DEVIANCE_TABLE);
 * <br>
 * <br> // Null constructor
 * <br> Hashtable argument2 = new Hashtable();
 * <br> LogisticRegression testclass4 = new LogisticRegression(argument2, null);
 * <br> coefficients =
 *        testclass4.coefficients(argument2, binaryResponse, nominalCovariate);
 * <br> argument2.put(ALPHA, 0.1);
 * <br> confidenceInterval = testclass4.
 *        confidenceInterval(argument2, binaryResponse, nominalCovariate);
 * <br> testStatistic =
 *        testclass4.testStatistic(argument2, binaryResponse, nominalCovariate);
 * <br> pValue = testclass4.pValue(argument2, binaryResponse, nominalCovariate);
 * <br> devianceTable =
 *        testclass4.devianceTable(argument2, binaryResponse, nominalCovariate);
 * <br>
 * <br> // Obtains the information about the output
 * <br> out.println(testclass3.output.toString());
 * <br> out.println(testclass4.output.toString());
 */

public class LogisticRegression extends GLMTemplate
{

    /**
     * The string binary response.
     */

    public String[] stringBinaryResponse;

    /**
     * The binary response.
     */

    public double[] binaryResponse;

    /**
     * The nominal covariate.
     */

    public String[][] nominalCovariate;

    /**
     * The continuous covariate.
     */

    public double[][] continuousCovariate;

    /**
     * The null deviance.
     */

    public double nullDeviance;

    /**
     * The residual deviance.
     */

    public double deviance;

    /**
     * The probability of "success".
     */

    public double[] pi;

    /**
     * The class size.
     */

    public double[] classSize;

    /**
     * The levels of the factors,
     * <br> level[j]: the level of the (j+1)'th factor.
     */

    public int[] level;

    /**
     * The object represents a logistic regression model.
     */

    public StatisticalAnalysis statisticalAnalysis;

    /**
     * The residuals.
     */

    private double residuals;

    /**
     * The constants used for computing the associated degrees of freedom in the
     * deviance table.
     */

    private int c, k;

    /**
     * The levels of the factors.
     */

    private int[] newLevel;

    /**
     * The object for the manipulations of the data.
     */

    private GLMDataManager glmDataManager = new GLMDataManager();

    /**
     * The required input data.
     */

    private Hashtable requiredInputData = new Hashtable();

    /**
     * Default LogisticRegression constructor.
     */

    public LogisticRegression()
    {
        link = LOGIT;
    }

    /**
     * Fits a logistic regression model with the specified link function.
     * @param link the link function.
     */

    public LogisticRegression(LinkFunction link)
    {
        this.link = link;
    }

    /**
     * Fits a logistic regression model given the input argument and data.
     * @param argument the argument with the following choices,
     * <br> ALPHA: the level of significance;
     * <br> empty argument: default level of significance equal to 0.05;
     * <br><br>
     * @param dataObject the input responses and values of the covariates
     *                   (excluding the one corresponding to the intercept).
     * @exception IllegalArgumentException wrong input data type.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public LogisticRegression(Hashtable argument,
                              Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null)
        {
            if (dataObject.length == 3 &&
                dataObject[0].getClass().getName().equalsIgnoreCase("[D") &&
                dataObject[1].getClass().getName().equalsIgnoreCase("[D") &&
                dataObject[2].getClass().getName().equalsIgnoreCase("[[D"))
            {
                statisticalAnalysis = new LogisticRegression((double[])
                        dataObject[0],
                        (double[]) dataObject[1], (double[][]) dataObject[2]);
            }
            else if (dataObject.length >= 3 &&
                     dataObject[1].getClass().getName().
                     equalsIgnoreCase("[D") &&
                     (dataObject.getClass().getName().equalsIgnoreCase(
                             "[Ljava.lang.Object;") ||
                     dataObject.getClass().getName().equalsIgnoreCase("[[D")))
           {
                statisticalAnalysis = new LogisticRegression((double[])
                        dataObject[0],
                        (double[]) dataObject[1],
                        DataManager.castDoubleObject(1, dataObject));
            }
            else
            {
                requiredInputData = getRequiredInputData(dataObject);
                if (requiredInputData.get("DATA_TYPE") != null)
                {
                    switch ((Integer) requiredInputData.get("DATA_TYPE"))
                    {
                        case 1:
                            statisticalAnalysis = new LogisticRegression(
                                (double[]) requiredInputData.get(
                                    "BINARY_RESPONSE"),
                                (String[][]) requiredInputData.get(
                                    "NOMINAL_COVARIATE"));
                            break;
                        case 2:
                            statisticalAnalysis = new LogisticRegression(
                                (double[]) requiredInputData.get(
                                    "BINARY_RESPONSE"),
                                (double[][]) requiredInputData.get(
                                    "CONTINUOUS_COVARIATE"));
                            break;
                        case 3:
                            statisticalAnalysis = new LogisticRegression(
                                (double[]) requiredInputData.get(
                                    "BINARY_RESPONSE"),
                                (String[][]) requiredInputData.get(
                                    "NOMINAL_COVARIATE"),
                                (double[][]) requiredInputData.get(
                                    "CONTINUOUS_COVARIATE"));
                            break;
                        case 4:
                            statisticalAnalysis = new LogisticRegression(
                                (String[]) requiredInputData.get(
                                    "STRING_BINARY_RESPONSE"),
                                (String[][]) requiredInputData.get(
                                    "NOMINAL_COVARIATE"));
                            break;
                        case 5:
                            statisticalAnalysis = new LogisticRegression(
                                (String[]) requiredInputData.get(
                                    "STRING_BINARY_RESPONSE"),
                                (double[][]) requiredInputData.get(
                                    "CONTINUOUS_COVARIATE"));
                            break;
                        case 6:
                            statisticalAnalysis = new LogisticRegression(
                                (double[]) requiredInputData.get(
                                    "STRING_BINARY_RESPONSE"),
                                (String[][]) requiredInputData.get(
                                    "NOMINAL_COVARIATE"),
                                (double[][]) requiredInputData.get(
                                    "CONTINUOUS_COVARIATE"));
                            break;
                        default:
                            throw new IllegalArgumentException(
                                "Wrong input data type.");
                    }
                }
            }
        }
        else
        {
            if (argument.get(LINK_FUNCTION) != null)
            {
                statisticalAnalysis = new LogisticRegression((LinkFunction)
                        argument.get(LINK_FUNCTION));
            }
            else
            {
                statisticalAnalysis = new LogisticRegression();
            }
        }
    }

    /**
     * Constructs a logistic regression model given the responses, values of the
     * covariates, class sizes and type index.
     * @param response the binomial responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to the intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @param classSize the class size.
     * @param hasDevianceTable the index used for generating the default
     *                         deviance table.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    private LogisticRegression(double[] response,
                               double[][] covariate,
                               double[] classSize,
                               boolean hasDevianceTable)
    {
        this.response = response;
        covariate = glmDataManager.addIntercept(covariate);
        this.classSize = classSize;
        confidenceInterval = confidenceInterval(0.05, response, classSize,
                                                covariate);
        pValue = pValue(response, classSize, covariate);
        nullDeviance = nullDeviance(response, classSize);
        deviance = deviance(response, classSize, covariate);
        fittedValues = fittedValues(response, classSize, covariate);
        devianceResiduals = devianceResiduals(response, classSize, covariate);
        responseResiduals = responseResiduals(response, covariate);
        pearsonResiduals = pearsonResiduals(response, covariate);
        if (hasDevianceTable)
        {
            devianceTable = devianceTable(response, classSize, covariate);
        }
    }

    /**
     * Constructs a logistic regression model given the responses, values of the
     * covariates, class sizes and levels of the factors.
     * @param response the binomial responses.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @param classSize the class size.
     * @param level the levels of the factors.
     * @param isMixedType the index indicating whether there exist data with
     *                    mixed type.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    private LogisticRegression(double[] response,
                               double[][] nominalCovariate,
                               double[] classSize,
                               int[] level,
                               boolean isMixedType)
    {
        this(response, nominalCovariate, classSize, false);
        this.level = level;
        if (!isMixedType)
        {
            devianceTable = devianceTable(response, classSize, level,
                                          glmDataManager.
                                          addIntercept(nominalCovariate));
        }
    }

    /**
     * Constructs a logistic regression model given the input data.
     * @param dataObject the input data.
     * @param isMixedType the index indicating whether there exist data with
     *                    mixed type.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    private LogisticRegression(Object[] dataObject,
                               boolean isMixedType)
    {
        this((double[]) dataObject[0], (double[][]) dataObject[1],
             (double[]) dataObject[2], (int[]) dataObject[3], isMixedType);
    }

    /**
     * Constructs a logistic regression model given the input data and type
     * index.
     * @param hasDevianceTable the index used for generating the default
     *                         deviance table.
     * @param dataObject the input data.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    private LogisticRegression(boolean hasDevianceTable,
                               Object ...dataObject)
    {
        this((double[]) dataObject[0], (double[][]) dataObject[1],
             (double[]) dataObject[2], hasDevianceTable);
    }

    /**
     * Constructs a logistic regression model given the binary responses and
     * nominal values of the covariates.
     * @param binaryResponse the binary responses with values of 0 or 1.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public LogisticRegression(double[] binaryResponse,
                              String[] ...nominalCovariate)
    {
        this(new GLMDataManager().setData("Logistic", binaryResponse,
                                          nominalCovariate), false);
        this.binaryResponse = binaryResponse;
        this.nominalCovariate = nominalCovariate;
    }

    /**
     * Constructs a logistic regression model given the responses, values of the
     * covariates and class sizes.
     * @param response the binomial responses.
     * @param classSize the class size.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to the intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public LogisticRegression(double[] response,
                              double[] classSize,
                              double[] ...covariate)
    {
        this(response, covariate, classSize, true);
    }

    /**
     * Constructs a logistic regression model given the binary responses and
     * numerical values of the covariates.
     * @param binaryResponse the binary responses with values of 0 or 1.
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public LogisticRegression(double[] binaryResponse,
                              double[] ...continuousCovariate)
    {
        this(true,
             new GLMDataManager().setData("Logistic", binaryResponse,
                                          continuousCovariate));
        this.binaryResponse = binaryResponse;
        this.continuousCovariate = continuousCovariate;
    }

    /**
     * Constructs a logistic regression model given the binary responses and
     * both nominal and numerial values of the covariates.
     * @param binaryResponse the binary responses with values of 0 or 1.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public LogisticRegression(double[] binaryResponse,
                              String[][] nominalCovariate,
                              double[][] continuousCovariate)
    {
        this(new GLMDataManager().setData("Logistic", binaryResponse,
                                          nominalCovariate,
                                          continuousCovariate), true);
        this.binaryResponse = binaryResponse;
        this.nominalCovariate = nominalCovariate;
        this.continuousCovariate = continuousCovariate;
        devianceTable = (double[][]) getRequiredOutput(DEVIANCE_TABLE,
                binaryResponse,
                nominalCovariate, continuousCovariate);
    }

    /**
     * Constructs a logistic regression model given the string binary responses
     * and nominal values of the covariates.
     * @param stringBinaryResponse the string binary responses with values such
     *                             as "Male" or "Female".
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public LogisticRegression(String[] stringBinaryResponse,
                              String[] ...nominalCovariate)
    {
        this(new GLMDataManager().setData("Logistic", stringBinaryResponse,
                                          nominalCovariate), false);
        this.stringBinaryResponse = stringBinaryResponse;
        this.nominalCovariate = nominalCovariate;
    }

    /**
     * Constructs a logistic regression model given the string binary responses
     * and numerical values of the covariates.
     * @param stringBinaryResponse the string binary responses with values such
     *                             as "Male" or "Female".
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public LogisticRegression(String[] stringBinaryResponse,
                              double[] ...continuousCovariate)
    {
        this(true,
             new GLMDataManager().setData("Logistic", stringBinaryResponse,
                                          continuousCovariate));
        this.stringBinaryResponse = stringBinaryResponse;
        this.continuousCovariate = continuousCovariate;
    }

    /**
     * Constructs a logistic regression model given the string binary responses
     * and both nominal and numerial values of the covariates.
     * @param stringBinaryResponse the string binary responses with values such
     *                             as "Male" or "Female".
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public LogisticRegression(String[] stringBinaryResponse,
                              String[][] nominalCovariate,
                              double[][] continuousCovariate)
    {
        this(new GLMDataManager().setData("Logistic", stringBinaryResponse,
                                          nominalCovariate,
                                          continuousCovariate), true);
        this.stringBinaryResponse = stringBinaryResponse;
        this.nominalCovariate = nominalCovariate;
        this.continuousCovariate = continuousCovariate;
        devianceTable = (double[][]) getRequiredOutput(DEVIANCE_TABLE,
                stringBinaryResponse,
                nominalCovariate, continuousCovariate);
    }

    /**
     * The IRLS estimate.
     * @param response the binomial responses.
     * @param classSize the class size.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the estimated coefficients.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    private double[] coefficients(double[] response,
                                  double[] classSize,
                                  double[] ...covariate)
    {
        this.classSize = classSize;

        return super.coefficients(response, covariate);
    }

    /**
     * The IRLS estimate.
     * @param argument the empty argument.
     * @param dataObject the input response and values of the covariates
     *                   (excluding the one corresponding to the intercept).
     * @return the estimated coefficients.
     * @exception IllegalArgumentException wrong input data or data type.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] coefficients(Hashtable argument,
                                 Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        requiredInputData = getRequiredInputData(dataObject);
        if (requiredInputData.get("DATA_TYPE") != null)
        {
            switch ((Integer) requiredInputData.get("DATA_TYPE"))
            {
                case 1:
                    coefficients = coefficients(
                        (double[]) requiredInputData.get("BINARY_RESPONSE"),
                        (String[][]) requiredInputData.
                        get("NOMINAL_COVARIATE"));
                    break;
                case 2:
                    coefficients = coefficients(
                        (double[]) requiredInputData.get("BINARY_RESPONSE"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                case 3:
                    coefficients = coefficients(
                        (double[]) requiredInputData.get("BINARY_RESPONSE"),
                        (String[][]) requiredInputData.get("NOMINAL_COVARIATE"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                case 4:
                    coefficients = coefficients(
                        (String[]) requiredInputData.get(
                            "STRING_BINARY_RESPONSE"),
                        (String[][]) requiredInputData.
                        get("NOMINAL_COVARIATE"));
                    break;
                case 5:
                    coefficients = coefficients(
                        (String[]) requiredInputData.get(
                            "STRING_BINARY_RESPONSE"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                case 6:
                    coefficients = coefficients(
                        (double[]) requiredInputData.get(
                            "STRING_BINARY_RESPONSE"),
                        (String[][]) requiredInputData.get("NOMINAL_COVARIATE"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                default:
                    throw new IllegalArgumentException
                            ("Wrong input data type.");
            }
        }
        else
        {
            throw new IllegalArgumentException("Wrong input data.");
        }

        return coefficients;
    }

    /**
     * The IRLS estimate.
     * @param binaryResponse the binary responses with values of 0 or 1.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @return the estimated coefficients.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] coefficients(double[] binaryResponse,
                                 String[] ...nominalCovariate)
    {
        this.binaryResponse = binaryResponse;
        this.nominalCovariate = nominalCovariate;

        return (double[]) getRequiredOutput(COEFFICIENTS,
                                            binaryResponse, nominalCovariate);
    }

    /**
     * The IRLS estimate.
     * @param binaryResponse the binary responses with values of 0 or 1.
     * @param continuousCovariate the numerical values of the covariates
     *                           (excluding the one corresponding to the
     *                           intercept),
     * <br>                      continuousCovariate[j]: the (j+1)'th covariate
     *                                                   vector.
     * @return the estimated coefficients.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] coefficients(double[] binaryResponse,
                                 double[] ...continuousCovariate)
    {
        this.binaryResponse = binaryResponse;
        this.continuousCovariate = continuousCovariate;

        return (double[]) getRequiredOutput(COEFFICIENTS,
                                            binaryResponse,
                                            continuousCovariate);
    }

    /**
     * The IRLS estimate.
     * @param binaryResponse the binary responses with values of 0 or 1.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @return the estimated coefficients.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] coefficients(double[] binaryResponse,
                                 String[][] nominalCovariate,
                                 double[][] continuousCovariate)
    {
        this.binaryResponse = binaryResponse;
        this.nominalCovariate = nominalCovariate;
        this.continuousCovariate = continuousCovariate;

        return (double[]) getRequiredOutput(COEFFICIENTS,
                                            binaryResponse, nominalCovariate,
                                            continuousCovariate);
    }

    /**
     * The IRLS estimate.
     * @param stringBinaryResponse the string binary responses with values such
     *                             as "Male" or "Female".
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @return the estimated coefficients.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] coefficients(String[] stringBinaryResponse,
                                 String[] ...nominalCovariate)
    {
        this.stringBinaryResponse = stringBinaryResponse;
        this.nominalCovariate = nominalCovariate;

        return (double[]) getRequiredOutput(COEFFICIENTS,
                                            stringBinaryResponse,
                                            nominalCovariate);
    }

    /**
     * The IRLS estimate.
     * @param stringBinaryResponse the string binary responses with values such
     *                             as "Male" or "Female".
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @return the estimated coefficients.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] coefficients(String[] stringBinaryResponse,
                                 double[] ...continuousCovariate)
    {
        this.stringBinaryResponse = stringBinaryResponse;
        this.continuousCovariate = continuousCovariate;

        return (double[]) getRequiredOutput(COEFFICIENTS,
                                            stringBinaryResponse,
                                            continuousCovariate);
    }

    /**
     * The IRLS estimate.
     * @param stringBinaryResponse the string binary responses with values such
     *                             as "Male" or "Female".
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @return the estimated coefficients.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] coefficients(String[] stringBinaryResponse,
                                 String[][] nominalCovariate,
                                 double[][] continuousCovariate)
    {
        this.stringBinaryResponse = stringBinaryResponse;
        this.nominalCovariate = nominalCovariate;
        this.continuousCovariate = continuousCovariate;

        return (double[]) getRequiredOutput(COEFFICIENTS,
                                            stringBinaryResponse,
                                            nominalCovariate,
                                            continuousCovariate);
    }

    /**
     * The confidence intervals for the estimated coefficients.
     * @param alpha the level of significance.
     * @param response the binomial responses.
     * @param classSize the class size.
     * @param covariate the values of the covariates,
     *                  covariate[j]: the (j+1)'th covariate vector.
     * @return the confidence intervals for the estimated coefficients,
     * <br>    confidenceInterval[j][0]: the lower bound for the (j+1)'th
     *                                   estimated coefficient;
     * <br>    confidenceInterval[j][1]: the upper bound for the (j+1)'th
     *                                   estimated coefficient.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    private double[][] confidenceInterval(double alpha,
                                          double[] response,
                                          double[] classSize,
                                          double[] ...covariate)
    {
        this.classSize = classSize;
        argument.put(ALPHA, alpha);

        return super.confidenceInterval(argument, response, covariate);
    }

    /**
     * The confidence intervals for the estimated coefficients.
     * @param argument the argument with the following choices,
     * <br> ALPHA: the level of significance;
     * <br> empty argument: default level of significance equal to 0.05;
     * <br><br>
     * @param dataObject the input response and values of the covariates
     *                   (excluding the one corresponding to the intercept).
     * @return the confidence intervals for the estimated coefficients,
     * <br>    confidenceInterval[j][0]: the lower bound for the (j+1)'th
     *                                   estimated coefficient;
     * <br>    confidenceInterval[j][1]: the upper bound for the (j+1)'th
     *                                   estimated coefficient.
     * @exception IllegalArgumentException wrong input data or data type.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[][] confidenceInterval(Hashtable argument,
                                         Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        alpha = (Double) argument.get(javastat.util.Argument.ALPHA);
        requiredInputData = getRequiredInputData(dataObject);
        if (requiredInputData.get("DATA_TYPE") != null)
        {
            switch ((Integer) requiredInputData.get("DATA_TYPE"))
            {
                case 1:
                    confidenceInterval = confidenceInterval(alpha,
                            (double[]) requiredInputData.get("BINARY_RESPONSE"),
                            (String[][]) requiredInputData.get(
                                "NOMINAL_COVARIATE"));
                    break;
                case 2:
                    confidenceInterval = confidenceInterval(alpha,
                            (double[]) requiredInputData.get("BINARY_RESPONSE"),
                            (double[][]) requiredInputData.get(
                                "CONTINUOUS_COVARIATE"));
                    break;
                case 3:
                    confidenceInterval = confidenceInterval(alpha,
                            (double[]) requiredInputData.get("BINARY_RESPONSE"),
                            (String[][]) requiredInputData.get(
                                "NOMINAL_COVARIATE"),
                            (double[][]) requiredInputData.get(
                                "CONTINUOUS_COVARIATE"));
                    break;
                case 4:
                    confidenceInterval = confidenceInterval(alpha,
                            (String[]) requiredInputData.get(
                                "STRING_BINARY_RESPONSE"),
                            (String[][]) requiredInputData.get(
                                "NOMINAL_COVARIATE"));
                    break;
                case 5:
                    confidenceInterval = confidenceInterval(alpha,
                            (String[]) requiredInputData.get(
                                "STRING_BINARY_RESPONSE"),
                            (double[][]) requiredInputData.get(
                                "CONTINUOUS_COVARIATE"));
                    break;
                case 6:
                    confidenceInterval = confidenceInterval(alpha,
                            (double[]) requiredInputData.get(
                                "STRING_BINARY_RESPONSE"),
                            (String[][]) requiredInputData.get(
                                "NOMINAL_COVARIATE"),
                            (double[][]) requiredInputData.get(
                                "CONTINUOUS_COVARIATE"));
                    break;
                default:
                    throw new IllegalArgumentException
                            ("Wrong input data type.");
            }
        }
        else
        {
            throw new IllegalArgumentException("Wrong input data.");
        }

        return confidenceInterval;
    }

    /**
     * The confidence intervals for the estimated coefficients.
     * @param alpha the level of significance.
     * @param binaryResponse the binary responses with values of 0 or 1.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @return the confidence intervals for the estimated coefficients,
     * <br>    confidenceInterval[j][0]: the lower bound for the (j+1)'th
     *                                   estimated coefficient;
     * <br>    confidenceInterval[j][1]: the upper bound for the (j+1)'th
     *                                   estimated coefficient.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[][] confidenceInterval(double alpha,
                                         double[] binaryResponse,
                                         String[] ...nominalCovariate)
    {
        this.alpha = alpha;
        this.binaryResponse = binaryResponse;
        this.nominalCovariate = nominalCovariate;

        return (double[][]) getRequiredOutput(CONFIDENCE_INTERVAL,
                                              binaryResponse,
                                              nominalCovariate);
    }

    /**
     * The confidence intervals for the estimated coefficients.
     * @param alpha the level of significance.
     * @param binaryResponse the binary responses with values of 0 or 1.
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @return the confidence intervals for the estimated coefficients,
     * <br>    confidenceInterval[j][0]: the lower bound for the (j+1)'th
     *                                   estimated coefficient;
     * <br>    confidenceInterval[j][1]: the upper bound for the (j+1)'th
     *                                   estimated coefficient.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[][] confidenceInterval(double alpha,
                                         double[] binaryResponse,
                                         double[] ...continuousCovariate)
    {
        this.alpha = alpha;
        this.binaryResponse = binaryResponse;
        this.continuousCovariate = continuousCovariate;

        return (double[][]) getRequiredOutput(CONFIDENCE_INTERVAL,
                                              binaryResponse,
                                              continuousCovariate);
    }

    /**
     * The confidence intervals for the estimated coefficients.
     * @param alpha the level of significance.
     * @param binaryResponse the binary responses with values of 0 or 1.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @return the confidence intervals for the estimated coefficients,
     * <br>    confidenceInterval[j][0]: the lower bound for the (j+1)'th
     *                                   estimated coefficient;
     * <br>    confidenceInterval[j][1]: the upper bound for the (j+1)'th
     *                                   estimated coefficient.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[][] confidenceInterval(double alpha,
                                         double[] binaryResponse,
                                         String[][] nominalCovariate,
                                         double[][] continuousCovariate)
    {
        this.alpha = alpha;
        this.binaryResponse = binaryResponse;
        this.nominalCovariate = nominalCovariate;
        this.continuousCovariate = continuousCovariate;

        return (double[][]) getRequiredOutput(CONFIDENCE_INTERVAL,
                                              binaryResponse,
                                              nominalCovariate,
                                              continuousCovariate);
    }

    /**
     * The confidence intervals for the estimated coefficients.
     * @param alpha the level of significance.
     * @param stringBinaryResponse the string binary responses with values such
     *                             as "Male" or "Female".
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @return the confidence intervals for the estimated coefficients,
     * <br>    confidenceInterval[j][0]: the lower bound for the (j+1)'th
     *                                   estimated coefficient;
     * <br>    confidenceInterval[j][1]: the upper bound for the (j+1)'th
     *                                   estimated coefficient.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[][] confidenceInterval(double alpha,
                                         String[] stringBinaryResponse,
                                         String[] ...nominalCovariate)
    {
        this.alpha = alpha;
        this.stringBinaryResponse = stringBinaryResponse;
        this.nominalCovariate = nominalCovariate;

        return (double[][]) getRequiredOutput(CONFIDENCE_INTERVAL,
                                              stringBinaryResponse,
                                              nominalCovariate);
    }

    /**
     * The confidence intervals for the estimated coefficients.
     * @param alpha the level of significance.
     * @param stringBinaryResponse the string binary responses with values such
     *                             as "Male" or "Female".
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @return the confidence intervals for the estimated coefficients,
     * <br>    confidenceInterval[j][0]: the lower bound for the (j+1)'th
     *                                   estimated coefficient;
     * <br>    confidenceInterval[j][1]: the upper bound for the (j+1)'th
     *                                   estimated coefficient.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[][] confidenceInterval(double alpha,
                                         String[] stringBinaryResponse,
                                         double[] ...continuousCovariate)
    {
        this.alpha = alpha;
        this.stringBinaryResponse = stringBinaryResponse;
        this.continuousCovariate = continuousCovariate;

        return (double[][]) getRequiredOutput(CONFIDENCE_INTERVAL,
                                              stringBinaryResponse,
                                              continuousCovariate);
    }

    /**
     * The confidence intervals for the estimated coefficients.
     * @param alpha the level of significance.
     * @param stringBinaryResponse the string binary responses with values such
     *                             as "Male" or "Female".
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @return the confidence intervals for the estimated coefficients,
     * <br>    confidenceInterval[j][0]: the lower bound for the (j+1)'th
     *                                   estimated coefficient;
     * <br>    confidenceInterval[j][1]: the upper bound for the (j+1)'th
     *                                   estimated coefficient.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[][] confidenceInterval(double alpha,
                                         String[] stringBinaryResponse,
                                         String[][] nominalCovariate,
                                         double[][] continuousCovariate)
    {
        this.alpha = alpha;
        this.stringBinaryResponse = stringBinaryResponse;
        this.nominalCovariate = nominalCovariate;
        this.continuousCovariate = continuousCovariate;

        return (double[][]) getRequiredOutput(CONFIDENCE_INTERVAL,
                                              stringBinaryResponse,
                                              nominalCovariate,
                                              continuousCovariate);
    }

    /**
     * The means of the responses.
     * @param coefficients the estimated coefficients.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the means of the responses.
     */

    protected double[] means(double[] coefficients,
                             double[] ...covariate)
    {
        if (link == null)
        {
            link = LOGIT;
        }
        means = means(link, coefficients, covariate);
        pi = new double[means.length];
        for (int i = 0; i < means.length; i++)
        {
            pi[i] = means[i];
            means[i] = classSize[i] * means[i];
        }

        return means;
    }

    /**
     * The weights.
     * @param coefficients the estimated coefficients.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the weights.
     * @exception IllegalArgumentException input link function is not supported.
     */

    protected double[][] weights(double[] coefficients,
                                 double[] ...covariate)
    {
        means = means(coefficients, covariate);
        weights = new double[classSize.length][classSize.length];
        switch (link)
        {
            case LOGIT:
                for (int i = 0; i < classSize.length; i++)
                {
                    weights[i][i] = means[i] * (1 - (means[i] / classSize[i]));
                }
                break;
            case PROBIT:
                for (int i = 0; i < classSize.length; i++)
                {
                    weights[i][i] = Math.exp(
                            -1.0 * Math.pow((means[i] / classSize[i]), 2.0)) /
                                    (2 * Math.pow(classSize[i], 3.0) *
                                     (means[i] / classSize[i]) *
                                     (1 - (means[i] / classSize[i])));
                }
                break;
            case COMPLEMENTARY_LOGLOG:
                for (int i = 0; i < classSize.length; i++)
                {
                    weights[i][i] = ((classSize[i] - means[i]) * Math.pow(
                            Math.log(1.0 - (means[i] / classSize[i])), 2.0)) /
                                    (means[i] / classSize[i]);
                }
                break;
            default:
                throw new IllegalArgumentException(
                    "Input link function is not supported.");
        }

        return weights;
    }

    /**
     * The z statistics for individual coefficients.
     * @param response the binomial responses.
     * @param classSize the class size.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the values of the z statistics.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    private double[] testStatistic(double[] response,
                                   double[] classSize,
                                   double[] ...covariate)
    {
        this.classSize = classSize;

        return super.testStatistic(response, covariate);
    }

    /**
     * The z statistics for individual coefficients.
     * @param argument the empty argument.
     * @param dataObject the input response and values of the covariates
     *                   (excluding the one corresponding to the intercept).
     * @return the values of the z statistics.
     * @exception IllegalArgumentException wrong input data or data type.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] testStatistic(Hashtable argument,
                                  Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        requiredInputData = getRequiredInputData(dataObject);
        if (requiredInputData.get("DATA_TYPE") != null)
        {
            switch ((Integer) requiredInputData.get("DATA_TYPE"))
            {
                case 1:
                    testStatistic = testStatistic(
                        (double[]) requiredInputData.get("BINARY_RESPONSE"),
                        (String[][]) requiredInputData.
                        get("NOMINAL_COVARIATE"));
                    break;
                case 2:
                    testStatistic = testStatistic(
                        (double[]) requiredInputData.get("BINARY_RESPONSE"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                case 3:
                    testStatistic = testStatistic(
                        (double[]) requiredInputData.get("BINARY_RESPONSE"),
                        (String[][]) requiredInputData.get("NOMINAL_COVARIATE"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                case 4:
                    testStatistic = testStatistic(
                        (String[]) requiredInputData.
                        get("STRING_BINARY_RESPONSE"),
                        (String[][]) requiredInputData.
                        get("NOMINAL_COVARIATE"));
                    break;
                case 5:
                    testStatistic = testStatistic(
                        (String[]) requiredInputData.get(
                            "STRING_BINARY_RESPONSE"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                case 6:
                    testStatistic = testStatistic(
                        (double[]) requiredInputData.get(
                            "STRING_BINARY_RESPONSE"),
                        (String[][]) requiredInputData.get("NOMINAL_COVARIATE"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                default:
                    throw new IllegalArgumentException(
                            "Wrong input data type.");
            }
        }
        else
        {
            throw new IllegalArgumentException("Wrong input data.");
        }

        return testStatistic;
    }

    /**
     * The z statistics for individual coefficients.
     * @param binaryResponse the binary responses with values of 0 or 1.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @return the values of the z statistics.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] testStatistic(double[] binaryResponse,
                                  String[] ...nominalCovariate)
    {
        this.binaryResponse = binaryResponse;
        this.nominalCovariate = nominalCovariate;

        return (double[]) getRequiredOutput(TEST_STATISTIC, binaryResponse,
                                            nominalCovariate);
    }

    /**
     * The z statistics for individual coefficients.
     * @param binaryResponse the binary responses with values of 0 or 1.
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @return the values of the z statistics.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] testStatistic(double[] binaryResponse,
                                  double[] ...continuousCovariate)
    {
        this.binaryResponse = binaryResponse;
        this.continuousCovariate = continuousCovariate;

        return (double[]) getRequiredOutput(TEST_STATISTIC, binaryResponse,
                                            continuousCovariate);
    }

    /**
     * The z statistics for individual coefficients.
     * @param binaryResponse the binary responses with values of 0 or 1.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @return the values of the z statistics.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] testStatistic(double[] binaryResponse,
                                  String[][] nominalCovariate,
                                  double[][] continuousCovariate)
    {
        this.binaryResponse = binaryResponse;
        this.nominalCovariate = nominalCovariate;
        this.continuousCovariate = continuousCovariate;

        return (double[]) getRequiredOutput(TEST_STATISTIC, binaryResponse,
                                            nominalCovariate,
                                            continuousCovariate);
    }

    /**
     * The z statistics for individual coefficients.
     * @param stringBinaryResponse the string binary responses with values such
     *                             as "Male" or "Female".
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @return the values of the z statistics.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] testStatistic(String[] stringBinaryResponse,
                                  String[] ...nominalCovariate)
    {
        this.stringBinaryResponse = stringBinaryResponse;
        this.nominalCovariate = nominalCovariate;

        return (double[]) getRequiredOutput(TEST_STATISTIC,
                                            stringBinaryResponse,
                                            nominalCovariate);
    }

    /**
     * The z statistics for individual coefficients.
     * @param stringBinaryResponse the string binary responses with values such
     *                             as "Male" or "Female".
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @return the values of the z statistics.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] testStatistic(String[] stringBinaryResponse,
                                  double[] ...continuousCovariate)
    {
        this.stringBinaryResponse = stringBinaryResponse;
        this.continuousCovariate = continuousCovariate;

        return (double[]) getRequiredOutput(TEST_STATISTIC,
                                            stringBinaryResponse,
                                            continuousCovariate);
    }

    /**
     * The z statistics for individual coefficients.
     * @param stringBinaryResponse the string binary responses with values such
     *                             as "Male" or "Female".
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @return the values of the z statistics.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] testStatistic(String[] stringBinaryResponse,
                                  String[][] nominalCovariate,
                                  double[][] continuousCovariate)
    {
        this.stringBinaryResponse = stringBinaryResponse;
        this.nominalCovariate = nominalCovariate;
        this.continuousCovariate = continuousCovariate;

        return (double[]) getRequiredOutput(TEST_STATISTIC,
                                            stringBinaryResponse,
                                            nominalCovariate,
                                            continuousCovariate);
    }

    /**
     * The p values for the z tests.
     * @param response the binomial responses.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @param classSize the class size.
     * @return the p values.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    private double[] pValue(double[] response,
                            double[] classSize,
                            double[][] covariate)
    {
        this.classSize = classSize;

        return super.pValue(response, covariate);
    }

    /**
     * The p values for the z tests.
     * @param argument the empty argument.
     * @param dataObject the input response and values of the covariates
     *                   (excluding the one corresponding to the intercept).
     * @return the p values.
     * @exception IllegalArgumentException wrong input data or data type.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] pValue(Hashtable argument,
                           Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        requiredInputData = getRequiredInputData(dataObject);
        if (requiredInputData.get("DATA_TYPE") != null)
        {
            switch ((Integer) requiredInputData.get("DATA_TYPE"))
            {
                case 1:
                    pValue = pValue(
                        (double[]) requiredInputData.get("BINARY_RESPONSE"),
                        (String[][]) requiredInputData.
                        get("NOMINAL_COVARIATE"));
                    break;
                case 2:
                    pValue = pValue(
                        (double[]) requiredInputData.get("BINARY_RESPONSE"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                case 3:
                    pValue = pValue(
                        (double[]) requiredInputData.get("BINARY_RESPONSE"),
                        (String[][]) requiredInputData.get("NOMINAL_COVARIATE"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                case 4:
                    pValue = pValue(
                        (String[]) requiredInputData.get(
                            "STRING_BINARY_RESPONSE"),
                        (String[][]) requiredInputData.
                        get("NOMINAL_COVARIATE"));
                    break;
                case 5:
                    pValue = pValue(
                        (String[]) requiredInputData.get(
                            "STRING_BINARY_RESPONSE"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                case 6:
                    pValue = pValue(
                        (double[]) requiredInputData.get(
                            "STRING_BINARY_RESPONSE"),
                        (String[][]) requiredInputData.get("NOMINAL_COVARIATE"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                default:
                    throw new IllegalArgumentException
                            ("Wrong input data type.");
            }
        }
        else
        {
            throw new IllegalArgumentException("Wrong input data.");
        }

        return pValue;
    }

    /**
     * The p values for the z tests.
     * @param binaryResponse the binary responses with values of 0 or 1.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @return the p values.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] pValue(double[] binaryResponse,
                           String[] ...nominalCovariate)
    {
        this.binaryResponse = binaryResponse;
        this.nominalCovariate = nominalCovariate;

        return (double[]) getRequiredOutput(PVALUE, binaryResponse,
                                            nominalCovariate);
    }

    /**
     * The p values for the z tests.
     * @param binaryResponse the binary responses with values of 0 or 1.
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @return the p values.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] pValue(double[] binaryResponse,
                           double[] ...continuousCovariate)
    {
        this.binaryResponse = binaryResponse;
        this.continuousCovariate = continuousCovariate;

        return (double[]) getRequiredOutput(PVALUE, binaryResponse,
                                            continuousCovariate);
    }

    /**
     * The p values for the z tests.
     * @param binaryResponse the binary responses with values of 0 or 1.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @return the p values.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] pValue(double[] binaryResponse,
                           String[][] nominalCovariate,
                           double[][] continuousCovariate)
    {
        this.binaryResponse = binaryResponse;
        this.nominalCovariate = nominalCovariate;
        this.continuousCovariate = continuousCovariate;

        return (double[]) getRequiredOutput(PVALUE, binaryResponse,
                                            nominalCovariate,
                                            continuousCovariate);
    }

    /**
     * The p values for the z tests.
     * @param stringBinaryResponse the string binary responses with values such
     *                             as "Male" or "Female".
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @return the p values.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] pValue(String[] stringBinaryResponse,
                           String[] ...nominalCovariate)
    {
        this.stringBinaryResponse = stringBinaryResponse;
        this.nominalCovariate = nominalCovariate;

        return (double[]) getRequiredOutput(PVALUE, stringBinaryResponse,
                                            nominalCovariate);
    }

    /**
     * The p values for the z tests.
     * @param stringBinaryResponse the string binary responses with values such
     *                             as "Male" or "Female".
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @return the p values.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] pValue(String[] stringBinaryResponse,
                           double[] ...continuousCovariate)
    {
        this.stringBinaryResponse = stringBinaryResponse;
        this.continuousCovariate = continuousCovariate;

        return (double[]) getRequiredOutput(PVALUE, stringBinaryResponse,
                                            continuousCovariate);
    }

    /**
     * The p values for the z tests.
     * @param stringBinaryResponse the string binary responses with values such
     *                             as "Male" or "Female".
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @return the p values.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] pValue(String[] stringBinaryResponse,
                           String[][] nominalCovariate,
                           double[][] continuousCovariate)
    {
        this.stringBinaryResponse = stringBinaryResponse;
        this.nominalCovariate = nominalCovariate;
        this.continuousCovariate = continuousCovariate;

        return (double[]) getRequiredOutput(PVALUE, stringBinaryResponse,
                                            nominalCovariate,
                                            continuousCovariate);
    }

    /**
     * The deviance function.
     * @param response the binomial responses.
     * @param classSize the class size.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the residual deviance.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    protected double deviance(double[] response,
                              double[] classSize,
                              double[] ...covariate)
    {
        coefficients = coefficients(response, classSize, covariate);
        means = means(coefficients, covariate);
        deviance = 0.0;
        for (int i = 0; i < linearPredictors.length; i++)
        {
            if (classSize[i] != response[i] && response[i] != 0.0)
            {
                deviance += 2 *
                        (response[i] * Math.log(response[i] / means[i]) +
                         (classSize[i] - response[i]) *
                         Math.log((classSize[i] - response[i]) /
                                  (classSize[i] - means[i])));
            }
            else if (response[i] == 0.0)
            {
                deviance += 2 *
                        (classSize[i] *
                         Math.log(classSize[i] / (classSize[i] - means[i])));
            }
            else
            {
                deviance += 2 *
                        (response[i] * Math.log(response[i] / means[i]));
            }
        }

        return deviance;
    }

    /**
     * The null deviance function.
     * @param response the binomial responses.
     * @param classSize the class size.
     * @return the null deviance.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    protected double nullDeviance(double[] response,
                                  double[] classSize)
    {
        return deviance(response, classSize,
                        new Matrix(1, response.length, 1).getArray());
    }

    /**
     * The fitted values.
     * @param response the binomial responses.
     * @param classSize the class size.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the fitted values.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    private double[] fittedValues(double[] response,
                                  double[] classSize,
                                  double[] ...covariate)
    {
        coefficients = coefficients(response, classSize, covariate);
        fittedValues = means(coefficients, covariate);
        output.put(FITTED_VALUES, fittedValues);

        return fittedValues;
    }

    /**
     * The Pearson residuals.
     * @param response the binomial responses.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the Pearson residuals.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    protected double[] pearsonResiduals(double[] response,
                                        double[] ...covariate)
    {
        coefficients = super.coefficients(response, covariate);
        means = means(coefficients, covariate);
        responseVariance = responseVariance(pi, ExponentialFamily.BINOMIAL);
        pearsonResiduals = pearsonResiduals(responseVariance, response,
                                            covariate);
        for (int i = 0; i < response.length; i++)
        {
            pearsonResiduals[i] = pearsonResiduals[i] / Math.sqrt(classSize[i]);
        }
        output.put(PEARSON_RESIDUALS, pearsonResiduals);

        return pearsonResiduals;
    }

    /**
     * The deviance residuals.
     * @param response the binomial responses.
     * @param classSize the class size.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the deviance residuals.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    protected double[] devianceResiduals(double[] response,
                                         double[] classSize,
                                         double[] ...covariate)
    {
        coefficients = coefficients(response, classSize, covariate);
        means = means(coefficients, covariate);
        devianceResiduals = new double[response.length];
        residuals = 0.0;
        for (int i = 0; i < response.length; i++)
        {
            if (classSize[i] != response[i] && response[i] != 0.0)
            {
                residuals = 2 *
                            (response[i] * Math.log(response[i] / means[i]) +
                             (classSize[i] - response[i]) *
                             Math.log((classSize[i] - response[i]) /
                                      (classSize[i] - means[i])));
            }
            else if (response[i] == 0.0)
            {
                residuals = 2 *(classSize[i] * Math.log(
                        classSize[i] / (classSize[i] - means[i])));
            }
            else
            {
                residuals =
                        2 * (response[i] * Math.log(response[i] / means[i]));
            }
            if (response[i] - means[i] > 0)
            {
                devianceResiduals[i] = residuals;
            }
            else
            {
                devianceResiduals[i] = -1 * residuals;
            }
        }
        output.put(DEVIANCE_RESIDUALS, devianceResiduals);

        return devianceResiduals;
    }

    /**
     * The deviance table.
     * @param response the binomial responses.
     * @param classSize the class size.
     * @param level the levels of the factors.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the deviance table.
     * <br>  devianceTable[j][0]: the degree of freedom corresponding to the
     *                            j'th factor.
     * <br>  devianceTable[j][1]: the deviance difference.
     * <br>  devianceTable[j][2]: the degree of freedom.
     * <br>  devianceTable[j][3]: the deviance.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    private double[][] devianceTable(double[] response,
                                     double[] classSize,
                                     int[] level,
                                     double[] ...covariate)
    {
        devianceTable = new double[level.length + 1][4];
        c = 0;
        for (int j = 0; j < classSize.length; j++)
        {
            c += classSize[j];
        }
        devianceTable[0][2] = c - 1;
        devianceTable[0][3] = nullDeviance(response, classSize);
        k = 0;
        for (int i = 1; i < level.length + 1; i++)
        {
            k = k + level[i - 1] - 1;
            devianceTable[i][3] = deviance(response, classSize,
                                           new Matrix(covariate).getMatrix(0, k,
                    0, response.length - 1).getArray());
            devianceTable[i][2] = c - k - 1;
            devianceTable[i][1] = devianceTable[i - 1][3] - devianceTable[i][3];
            devianceTable[i][0] = level[i - 1] - 1;
        }
        output.put(DEVIANCE_TABLE, devianceTable);

        return devianceTable;
    }

    /**
     * The deviance table.
     * @param response the binomial responses.
     * @param classSize the class size.
     * @param continuousCovariate the numerical values of the covariates,
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @return the deviance table.
     * <br>  devianceTable[j][0]: the degree of freedom corresponding to the
     *                            j'th factor.
     * <br>  devianceTable[j][1]: the deviance difference.
     * <br>  devianceTable[j][2]: the degree of freedom.
     * <br>  devianceTable[j][3]: the deviance.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    private double[][] devianceTable(double[] response,
                                     double[] classSize,
                                     double[] ...continuousCovariate)
    {
        devianceTable = new double[continuousCovariate.length][4];
        c = 0;
        for (int j = 0; j < classSize.length; j++)
        {
            c += classSize[j];
        }
        devianceTable[0][2] = c - 1;
        devianceTable[0][3] = nullDeviance(response, classSize);
        k = 0;
        for (int i = 1; i < continuousCovariate.length; i++)
        {
            k = k + 1;
            devianceTable[i][3] = deviance(response, classSize,
                                           new Matrix(continuousCovariate).
                                           getMatrix(0, k, 0,
                    response.length - 1).getArray());
            devianceTable[i][2] = c - k - 1;
            devianceTable[i][1] = devianceTable[i - 1][3] - devianceTable[i][3];
            devianceTable[i][0] = 1;
        }
        output.put(DEVIANCE_TABLE, devianceTable);

        return devianceTable;
    }

    /**
     * The deviance table.
     * @param argument the empty argument.
     * @param dataObject the input response and values of the covariates
     *                   (excluding the one corresponding to the intercept).
     * @return the deviance table.
     * <br>  devianceTable[j][0]: the degree of freedom corresponding to the
     *                            j'th factor.
     * <br>  devianceTable[j][1]: the deviance difference.
     * <br>  devianceTable[j][2]: the degree of freedom.
     * <br>  devianceTable[j][3]: the deviance.
     * @exception IllegalArgumentException wrong input data or data type.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[][] devianceTable(Hashtable argument,
                                    Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        requiredInputData = getRequiredInputData(dataObject);
        if (requiredInputData.get("DATA_TYPE") != null)
        {
            switch ((Integer) requiredInputData.get("DATA_TYPE"))
            {
                case 1:
                    devianceTable = devianceTable(
                        (double[]) requiredInputData.get("BINARY_RESPONSE"),
                        (String[][]) requiredInputData.
                        get("NOMINAL_COVARIATE"));
                    break;
                case 2:
                    devianceTable = devianceTable(
                        (double[]) requiredInputData.get("BINARY_RESPONSE"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                case 3:
                    devianceTable = devianceTable(
                        (double[]) requiredInputData.get("BINARY_RESPONSE"),
                        (String[][]) requiredInputData.get("NOMINAL_COVARIATE"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                case 4:
                    devianceTable = devianceTable(
                        (String[]) requiredInputData.get(
                            "STRING_BINARY_RESPONSE"),
                        (String[][]) requiredInputData.
                        get("NOMINAL_COVARIATE"));
                    break;
                case 5:
                    devianceTable = devianceTable(
                        (String[]) requiredInputData.get(
                            "STRING_BINARY_RESPONSE"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                case 6:
                    devianceTable = devianceTable(
                        (double[]) requiredInputData.get(
                            "STRING_BINARY_RESPONSE"),
                        (String[][]) requiredInputData.get("NOMINAL_COVARIATE"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                default:
                    throw new IllegalArgumentException
                            ("Wrong input data type.");
            }
        }
        else
        {
            throw new IllegalArgumentException("Wrong input data.");
        }

        return devianceTable;
    }

    /**
     * The deviance table.
     * @param binaryResponse the binary responses with values of 0 or 1.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @return the deviance table.
     * <br>  devianceTable[j][0]: the degree of freedom corresponding to the
     *                            j'th factor.
     * <br>  devianceTable[j][1]: the deviance difference.
     * <br>  devianceTable[j][2]: the degree of freedom.
     * <br>  devianceTable[j][3]: the deviance.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[][] devianceTable(double[] binaryResponse,
                                    String[] ...nominalCovariate)
    {
        this.binaryResponse = binaryResponse;
        this.nominalCovariate = nominalCovariate;

        return (double[][]) getRequiredOutput(DEVIANCE_TABLE, binaryResponse,
                                              nominalCovariate);
    }

    /**
     * The deviance table.
     * @param binaryResponse the binary responses with values of 0 or 1.
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @return the deviance table.
     * <br>  devianceTable[j][0]: the degree of freedom corresponding to the
     *                            j'th factor.
     * <br>  devianceTable[j][1]: the deviance difference.
     * <br>  devianceTable[j][2]: the degree of freedom.
     * <br>  devianceTable[j][3]: the deviance.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[][] devianceTable(double[] binaryResponse,
                                    double[] ...continuousCovariate)
    {
        this.binaryResponse = binaryResponse;
        this.continuousCovariate = continuousCovariate;

        return (double[][]) getRequiredOutput(DEVIANCE_TABLE, binaryResponse,
                                              continuousCovariate);
    }

    /**
     * The deviance table.
     * @param binaryResponse the binary responses with values of 0 or 1.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     *                         nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @return the deviance table.
     * <br>  devianceTable[j][0]: the degree of freedom corresponding to the
     *                            j'th factor.
     * <br>  devianceTable[j][1]: the deviance difference.
     * <br>  devianceTable[j][2]: the degree of freedom.
     * <br>  devianceTable[j][3]: the deviance.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[][] devianceTable(double[] binaryResponse,
                                    String[][] nominalCovariate,
                                    double[][] continuousCovariate)
    {
        this.binaryResponse = binaryResponse;
        this.nominalCovariate = nominalCovariate;
        this.continuousCovariate = continuousCovariate;

        return (double[][]) getRequiredOutput(DEVIANCE_TABLE, binaryResponse,
                                              nominalCovariate,
                                              continuousCovariate);
    }

    /**
     * The deviance table.
     * @param stringBinaryResponse the string binary responses with values such
     *                             as "Male" or "Female".
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     *                         nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @return the deviance table.
     * <br>  devianceTable[j][0]: the degree of freedom corresponding to the
     *                            j'th factor.
     * <br>  devianceTable[j][1]: the deviance difference.
     * <br>  devianceTable[j][2]: the degree of freedom.
     * <br>  devianceTable[j][3]: the deviance.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[][] devianceTable(String[] stringBinaryResponse,
                                    String[] ...nominalCovariate)
    {
        this.stringBinaryResponse = stringBinaryResponse;
        this.nominalCovariate = nominalCovariate;

        return (double[][]) getRequiredOutput(DEVIANCE_TABLE,
                                              stringBinaryResponse,
                                              nominalCovariate);
    }

    /**
     * The deviance table.
     * @param stringBinaryResponse the string binary responses with values such
     *                             as "Male" or "Female".
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @return the deviance table.
     * <br>  devianceTable[j][0]: the degree of freedom corresponding to the
     *                            j'th factor.
     * <br>  devianceTable[j][1]: the deviance difference.
     * <br>  devianceTable[j][2]: the degree of freedom.
     * <br>  devianceTable[j][3]: the deviance.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[][] devianceTable(String[] stringBinaryResponse,
                                    double[] ...continuousCovariate)
    {
        this.stringBinaryResponse = stringBinaryResponse;
        this.continuousCovariate = continuousCovariate;

        return (double[][]) getRequiredOutput(DEVIANCE_TABLE,
                                              stringBinaryResponse,
                                              continuousCovariate);
    }

    /**
     * The deviance table.
     * @param stringBinaryResponse the string binary responses with values such
     *                             as "Male" or "Female".
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     *                         nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @return the deviance table.
     * <br>  devianceTable[j][0]: the degree of freedom corresponding to the
     *                            j'th factor.
     * <br>  devianceTable[j][1]: the deviance difference.
     * <br>  devianceTable[j][2]: the degree of freedom.
     * <br>  devianceTable[j][3]: the deviance.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[][] devianceTable(String[] stringBinaryResponse,
                                    String[][] nominalCovariate,
                                    double[][] continuousCovariate)
    {
        this.stringBinaryResponse = stringBinaryResponse;
        this.nominalCovariate = nominalCovariate;
        this.continuousCovariate = continuousCovariate;

        return (double[][]) getRequiredOutput(DEVIANCE_TABLE,
                                              stringBinaryResponse,
                                              nominalCovariate,
                                              continuousCovariate);
    }

    /**
     * Obtains the required input data.
     * @param dataObject the original input data.
     * @return the required input data.
     * @exception IllegalArgumentException wrong input data.
     */

    private Hashtable getRequiredInputData(Object ...dataObject)
    {
        if (dataObject[0].getClass().getName().equalsIgnoreCase("[D"))
        {
            requiredInputData.put("BINARY_RESPONSE", dataObject[0]);
            if (dataObject.length == 2 &&
                dataObject[1].getClass().getName().equalsIgnoreCase(
                        "[[Ljava.lang.String;"))
            {
                requiredInputData.put("NOMINAL_COVARIATE", dataObject[1]);
                requiredInputData.put("DATA_TYPE", 1);
            }
            else if (dataObject.length == 2 &&
                     dataObject[1].getClass().getName().equalsIgnoreCase("[[D"))
            {
                requiredInputData.put("CONTINUOUS_COVARIATE", dataObject[1]);
                requiredInputData.put("DATA_TYPE", 2);
            }
            else if (dataObject.length == 3 &&
                     dataObject[1].getClass().getName().equalsIgnoreCase(
                             "[[Ljava.lang.String;") &&
                     dataObject[2].getClass().getName().equalsIgnoreCase("[[D"))
            {
                requiredInputData.put("NOMINAL_COVARIATE", dataObject[1]);
                requiredInputData.put("CONTINUOUS_COVARIATE", dataObject[2]);
                requiredInputData.put("DATA_TYPE", 3);
            }
            else if (dataObject.length >= 2 &&
                     (dataObject.getClass().getName().equalsIgnoreCase(
                             "[Ljava.lang.Object;") ||
                     dataObject.getClass().getName().equalsIgnoreCase("[[D")))
           {
                requiredInputData.put("CONTINUOUS_COVARIATE",
                                      DataManager.
                                      castDoubleObject(1, dataObject));
                requiredInputData.put("DATA_TYPE", 2);
            }
            else if (dataObject.length >= 2 &&
                     dataObject[1].getClass().getName().equalsIgnoreCase(
                             "[Ljava.lang.String;"))
            {
                requiredInputData.put("NOMINAL_COVARIATE",
                                      DataManager.castStringObject(1,
                        dataObject));
                requiredInputData.put("DATA_TYPE", 1);
            }
        }
        else if (dataObject[0].getClass().getName().equalsIgnoreCase(
            "[Ljava.lang.String;"))
        {
            requiredInputData.put("STRING_BINARY_RESPONSE", dataObject[0]);
            if (dataObject.length == 2 &&
                dataObject[1].getClass().getName().equalsIgnoreCase(
                        "[[Ljava.lang.String;"))
            {
                requiredInputData.put("NOMINAL_COVARIATE", dataObject[1]);
                requiredInputData.put("DATA_TYPE", 4);
            }
            else if (dataObject.length == 2 &&
                     dataObject[1].getClass().getName().equalsIgnoreCase("[[D"))
            {
                requiredInputData.put("CONTINUOUS_COVARIATE", dataObject[1]);
                requiredInputData.put("DATA_TYPE", 5);
            }
            else if (dataObject.length == 3 &&
                     dataObject[1].getClass().getName().equalsIgnoreCase(
                             "[[Ljava.lang.String;") &&
                     dataObject[2].getClass().getName().equalsIgnoreCase("[[D"))
            {
                requiredInputData.put("NOMINAL_COVARIATE", dataObject[1]);
                requiredInputData.put("CONTINUOUS_COVARIATE", dataObject[2]);
                requiredInputData.put("DATA_TYPE", 6);
            }
            else if (dataObject.length >= 2 &&
                     dataObject[1].getClass().getName().equalsIgnoreCase("[D"))
            {
                requiredInputData.put("CONTINUOUS_COVARIATE",
                                      DataManager.
                                      castDoubleObject(1, dataObject));
                requiredInputData.put("DATA_TYPE", 5);
            }
            else if (dataObject.length >= 2 &&
                     (dataObject.getClass().getName().equalsIgnoreCase(
                             "[Ljava.lang.Object;") ||
                     dataObject.getClass().getName().equalsIgnoreCase(
                              "[[Ljava.lang.String;")))
            {
                requiredInputData.put("NOMINAL_COVARIATE",
                                      DataManager.
                                      castStringObject(1, dataObject));
                requiredInputData.put("DATA_TYPE", 4);
            }
        }
        else
        {
            throw new IllegalArgumentException("Wrong input data.");
        }

        return requiredInputData;
    }

    /**
     * Obtains the required output.
     * @param option the option for the required output.
     * @param dataObject the input data.
     * @return the required output.
     * @exception IllegalArgumentException the level of significance should be
     *                                     (strictly) positive and not greater
     *                                     than 1.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    private Object getRequiredOutput(javastat.util.Output option,
                                     Object ...dataObject)
    {
        Object requiredOutput;
        Object[] outputObject = new Object[4];
        outputObject = glmDataManager.setData("Logistic", dataObject);
        response = (double[]) outputObject[0];
        covariate = glmDataManager.addIntercept((double[][]) outputObject[1]);
        classSize = (double[]) outputObject[2];
        if (option == DEVIANCE_TABLE)
        {
            if (!dataObject[1].getClass().getName().equalsIgnoreCase("[[D"))
            {
                level = (int[]) outputObject[3];
                if (dataObject.length == 3)
                {
                    newLevel = new int[level.length +
                               this.continuousCovariate.length];
                    for (int i = 0;
                         i < level.length +
                         this.continuousCovariate.length; i++)
                    {
                        if (i < level.length)
                        {
                            newLevel[i] = level[i];
                        }
                        else
                        {
                            newLevel[i] = 2;
                        }
                    }
                    for (int i = 0; i < newLevel.length; i++)
                    {
                        devianceTable = devianceTable(response, classSize,
                                newLevel, covariate);
                    }
                }
                else
                {
                    devianceTable = devianceTable(response, classSize, level,
                                                  covariate);
                }
            }
            else
            {
                devianceTable = devianceTable(response, classSize, covariate);
            }
        }
        switch (option)
        {
            case CONFIDENCE_INTERVAL:
                requiredOutput = confidenceInterval(this.alpha, response,
                                                    classSize, covariate);
                break;
            case TEST_STATISTIC:
                requiredOutput = testStatistic(response, classSize, covariate);
                break;
            case PVALUE:
                requiredOutput = pValue(response, classSize, covariate);
                break;
            case DEVIANCE_TABLE:
                requiredOutput = devianceTable;
                break;
            default:
                requiredOutput = coefficients(response, classSize, covariate);
                break;
        }

        return requiredOutput;
    }

}
