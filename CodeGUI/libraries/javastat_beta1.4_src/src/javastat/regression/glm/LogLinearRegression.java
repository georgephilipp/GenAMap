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
 * <p>Fits a log-linear regression model.</p>
 * <p> </p>
 * <br> Example:
 * <br> String [][] shipData = {{"a", "a", "a", "a", "a", "a", "a", "a", "b",
 *                               "b", "b", "b", "b", "b", "b", "b",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                               "c", "c", "c", "c", "c", "c", "c", "c", "d",
 *                               "d", "d", "d", "d", "d", "d", "d",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                               "e", "e", "e", "e", "e", "e", "e", "e"},
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;
 *      {"1960-64", "1960-64", "1965-69", "1965-69", "1970-74", "1970-74",
 *       "1975-79", "1975-79",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       "1960-64", "1960-64", "1965-69", "1965-69", "1970-74", "1970-74",
 *       "1975-79", "1975-79",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       "1960-64", "1960-64", "1965-69", "1965-69", "1970-74", "1970-74",
 *       "1975-79", "1975-79",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       "1960-64", "1960-64", "1965-69", "1965-69", "1970-74", "1970-74",
 *       "1975-79", "1975-79",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       "1960-64", "1960-64", "1965-69", "1965-69", "1970-74", "1970-74",
 *       "1975-79", "1975-79"},
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;
 *      {"1960-74", "1975-79", "1960-74", "1975-79", "1960-74", "1975-79",
 *       "1960-74", "1975-79",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       "1960-74", "1975-79", "1960-74", "1975-79", "1960-74", "1975-79",
 *       "1960-74", "1975-79",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       "1960-74", "1975-79", "1960-74", "1975-79", "1960-74", "1975-79",
 *       "1960-74", "1975-79",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       "1960-74", "1975-79", "1960-74", "1975-79", "1960-74", "1975-79",
 *       "1960-74", "1975-79",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *       "1960-74", "1975-79", "1960-74", "1975-79", "1960-74", "1975-79",
 *       "1960-74", "1975-79"}};
 * <br> double[] offset = {127, 63, 1095, 1095, 1512, 3353, 0, 2244, 44882,
 *                         17176, 28609,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                         20370, 7064, 13099, 0, 7177, 1179, 552, 781, 676,
 *                         783, 1948, 0, 274, 251,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                         105, 288, 192, 349, 1208, 0, 2051, 45, 0, 789, 437,
 *                         1157, 2161, 0, 542};
 * <br> double[] damageNumber = {0, 0, 3, 4, 6, 18, 0, 11, 39, 29, 58, 53, 12,
 *                               44, 0, 18, 1,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;
 *                               1, 0, 1, 6, 2, 0, 1, 0, 0, 0, 0, 2, 11, 0, 4,
 *                               0, 0, 7, 7, 5, 12, 0, 1};
 * <br>
 * <br> // Non-null constructor
 * <br> LogLinearRegression testclass1 =
 *        new LogLinearRegression(damageNumber, offset, shipData);
 * <br> double[] coefficients = testclass1.coefficients;
 * <br> double[][] confidenceInterval = testclass1.confidenceInterval;
 * <br> double [] testStatistic = testclass1.testStatistic;
 * <br> double [] pValue = testclass1.pValue;
 * <br> double [][] devianceTable = testclass1.devianceTable;
 * <br>
 * <br> // Null constructor
 * <br> LogLinearRegression testclass2 = new LogLinearRegression();
 * <br> coefficients = testclass2.coefficients(damageNumber, offset, shipData);
 * <br> confidenceInterval =
 *        testclass2.confidenceInterval(0.1, damageNumber, offset, shipData);
 * <br> testStatistic =
 *        testclass2.testStatistic(damageNumber, offset, shipData);
 * <br> pValue = testclass2.pValue(damageNumber, offset, shipData);
 * <br> devianceTable =
 *        testclass2.devianceTable(damageNumber, offset, shipData);
 * <br>
 * <br> // Non-null constructor
 * <br> Hashtable argument1 = new Hashtable();
 * <br> StatisticalAnalysis testclass3 =
 * <br> &nbsp;&nbsp;&nbsp;
 *        new LogLinearRegression(argument1, damageNumber, offset, shipData).
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
 *        testclass4.coefficients(argument2, damageNumber, offset, shipData);
 * <br> argument2.put(ALPHA, 0.1);
 * <br> confidenceInterval = testclass4.
 *        confidenceInterval(argument2, damageNumber, offset, shipData);
 * <br> testStatistic =
 *        testclass4.testStatistic(argument2, damageNumber, offset, shipData);
 * <br> pValue = testclass4.pValue(argument2, damageNumber, offset, shipData);
 * <br> devianceTable =
 *        testclass4.devianceTable(argument2, damageNumber, offset, shipData);
 * <br>
 * <br>  // Obtains the information about the output
 * <br> out.println(testclass3.output.toString());
 * <br> out.println(testclass4.output.toString());
 */

public class LogLinearRegression extends GLMTemplate
{

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
     * The offset.
     */

    public double[] offset;

    /**
     * The logarithm of the offset.
     */

    public double[] logOffset;

    /**
     * The levels of the factors,
     * <br> level[j]: the level of the (j+1)'th factor.
     */

    public int[] level;

    /**
     * The object represents a log-linear regression model.
     */

    public StatisticalAnalysis statisticalAnalysis;

    /**
     * The residuals.
     */

    private double residuals;

    /**
     * The covarite matrix.
     */

    private Matrix covariateMatrix;

    /**
     * The coefficient matrix.
     */

    private Matrix coefficientMatrix;

    /**
     * The linear-predictor matrix.
     */

    private Matrix linearPredictorMatrix;

    /**
     * The object for the manipulations of the data.
     */

    private GLMDataManager glmDataManager = new GLMDataManager();

    /**
     * The required input data.
     */

    private Hashtable requiredInputData = new Hashtable();

    /**
     * Default LogLinearRegression constructor.
     */

    public LogLinearRegression()
    {
        link = LOG;
    }

    /**
     * Fits a log-linear regression model with the specified link function.
     * @param link the link function
     */

    public LogLinearRegression(LinkFunction link)
    {
        this.link = link;
    }

    /**
     * Fits a log-linear regression model given the input arguments and data.
     * @param argument the empty argument.
     * @param dataObject the input responses and values of the covariates
     *                   (excluding the one corresponding to intercept).
     * @exception IllegalArgumentException wrong input data type.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public LogLinearRegression(Hashtable argument,
                               Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null)
        {
            requiredInputData = getRequiredInputData(dataObject);
            if (requiredInputData.get("DATA_TYPE") != null)
            {
                switch ((Integer) requiredInputData.get("DATA_TYPE"))
                {
                    case 1:
                        statisticalAnalysis = new LogLinearRegression(
                            (double[]) requiredInputData.get("RESPONSE"),
                            (double[]) requiredInputData.get("OFFSET"),
                            (String[][]) requiredInputData.get(
                                "NOMINAL_COVARIATE"));
                        break;
                    case 2:
                        statisticalAnalysis = new LogLinearRegression(
                            (double[]) requiredInputData.get("RESPONSE"),
                            (double[]) requiredInputData.get("OFFSET"),
                            (double[][]) requiredInputData.get(
                                "CONTINUOUS_COVARIATE"));
                        break;
                    case 3:
                        statisticalAnalysis = new LogLinearRegression(
                            (double[]) requiredInputData.get("RESPONSE"),
                            (double[]) requiredInputData.get("OFFSET"),
                            (String[][]) requiredInputData.get(
                                "NOMINAL_COVARIATE"),
                            (double[][]) requiredInputData.get(
                                "CONTINUOUS_COVARIATE"));
                        break;
                    case 4:
                        statisticalAnalysis = new LogLinearRegression(
                            (double[]) requiredInputData.get("RESPONSE"),
                            (String[][]) requiredInputData.get(
                                "NOMINAL_COVARIATE"));
                        break;
                    case 5:
                        statisticalAnalysis = new LogLinearRegression(
                            (double[]) requiredInputData.get("RESPONSE"),
                            (double[][]) requiredInputData.get(
                                "CONTINUOUS_COVARIATE"));
                        break;
                    case 6:
                        statisticalAnalysis = new LogLinearRegression(
                            (double[]) requiredInputData.get("RESPONSE"),
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
        else
        {
            if (argument.get(LINK_FUNCTION) != null)
            {
                statisticalAnalysis = new LogLinearRegression((LinkFunction)
                        argument.get(LINK_FUNCTION));
            }
            else
            {
                statisticalAnalysis = new LogisticRegression();
            }
        }
    }

    /**
     * Constructs a log-linear regression model given the responses, values of
     * the covariates, offset and type index.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @param offset the offset.
     * @param hasDevianceTable the index used for generating the default
     *                         deviance table.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    private LogLinearRegression(double[] response,
                                double[][] covariate,
                                double[] offset,
                                boolean hasDevianceTable)
    {
        confidenceInterval = confidenceInterval(0.05, response, offset,
                                                covariate);
        covariate = glmDataManager.addIntercept(covariate);
        this.offset = offset;
        pValue = super.pValue(response, covariate);
        nullDeviance = nullDeviance(response);
        deviance = deviance(response, covariate);
        fittedValues = fittedValues(response, covariate, offset);
        devianceResiduals = devianceResiduals(response, covariate);
        pearsonResiduals = pearsonResiduals(response, covariate);
        responseResiduals = responseResiduals(response, covariate);
        if (hasDevianceTable)
        {
            devianceTable = devianceTable(response, covariate, offset);
        }
    }

    /**
     * Constructs a log-linear regression model given the input data.
     * @param dataObject the input data.
     * @param isMixedType the index indicating whether there exist data with
     *                    mixed type.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    private LogLinearRegression(Object[] dataObject,
                                boolean isMixedType)
    {
        this((double[]) dataObject[0], (double[][]) dataObject[1],
             (double[]) dataObject[2], (int[]) dataObject[3], isMixedType);
    }

    /**
     * Constructs a log-linear regression model given the responses, values of
     * the covariates, offset and type index.
     * @param response the responses.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @param offset the offset.
     * @param level the levels of the factors.
     * @param isMixedType the index indicating whether there exist data with
     *                    mixed type.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    private LogLinearRegression(double[] response,
                                double[][] nominalCovariate,
                                double[] offset,
                                int[] level,
                                boolean isMixedType)
    {
        this(response, nominalCovariate, offset, false);
        if (!isMixedType)
        {
            devianceTable = devianceTable(response, level, glmDataManager.
                                          addIntercept(nominalCovariate));
        }
    }

    /**
     * Constructs a log-linear regression model given the responses,
     * nominal values of the covariates and offset.
     * @param response the responses.
     * @param offset the offset.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public LogLinearRegression(double[] response,
                               double[] offset,
                               String[] ...nominalCovariate)
    {
        this(new GLMDataManager().setData("LogLinear", response,
                                          nominalCovariate, offset), false);
        this.nominalCovariate = nominalCovariate;
    }

    /**
     * Constructs a log-linear regression model given the responses,
     * nominal values of the covariates and zero offset.
     * @param response the responses.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public LogLinearRegression(double[] response,
                               String[] ...nominalCovariate)
    {
        this(response, new GLMDataManager().zeroArray(response.length),
             nominalCovariate);
    }

    /**
     * Constructs a log-linear regression model given the responses, numerical
     * values of the covariates and offset.
     * @param response the responses.
     * @param offset the offset.
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public LogLinearRegression(double[] response,
                               double[] offset,
                               double[] ...continuousCovariate)
    {
        this(response, continuousCovariate, offset, true);
    }

    /**
     * Constructs a log-linear regression model given the responses, numerical
     * values of the covariates and zero offset.
     * @param response the responses.
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public LogLinearRegression(double[] response,
                               double[] ...continuousCovariate)
    {
        this(response, new GLMDataManager().zeroArray(response.length),
             continuousCovariate);
    }

    /**
     * Constructs a logistic regression model given the binary responses, both
     * nominal and numerial values of the covariates and offset.
     * @param response the responses.
     * @param offset the offset.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept), continuousCovariate[j]:
     *                            the (j+1)'th covariate vector.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public LogLinearRegression(double[] response,
                               double[] offset,
                               String[][] nominalCovariate,
                               double[][] continuousCovariate)
    {
        this(new GLMDataManager().setData("LogLinear", response,
                                          nominalCovariate,
                                          continuousCovariate, offset), true);
        this.continuousCovariate = continuousCovariate;
        this.nominalCovariate = nominalCovariate;
        devianceTable = (double[][]) getRequiredOutput(DEVIANCE_TABLE, response,
                nominalCovariate, continuousCovariate, offset);
    }

    /**
     * Constructs a log-linear regression model given the responses and both
     * nominal and numerical values of the covariates.
     * @param response the responses.
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

    public LogLinearRegression(double[] response,
                               String[][] nominalCovariate,
                               double[][] continuousCovariate)
    {
        this(response, new GLMDataManager().zeroArray(response.length),
             nominalCovariate, continuousCovariate);
    }

    /**
     * The IRLS estimate.
     * @param argument the empty argument.
     * @param dataObject the input responses and values of the covariates
     *                   (excluding the one corresponding to intercept).
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
                        (double[]) requiredInputData.get("RESPONSE"),
                        (double[]) requiredInputData.get("OFFSET"),
                        (String[][]) requiredInputData.
                        get("NOMINAL_COVARIATE"));
                    break;
                case 2:
                    coefficients = coefficients(
                        (double[]) requiredInputData.get("RESPONSE"),
                        (double[]) requiredInputData.get("OFFSET"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                case 3:
                    coefficients = coefficients(
                        (double[]) requiredInputData.get("RESPONSE"),
                        (double[]) requiredInputData.get("OFFSET"),
                        (String[][]) requiredInputData.get("NOMINAL_COVARIATE"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                case 4:
                    coefficients = coefficients(
                        (double[]) requiredInputData.get("RESPONSE"),
                        (String[][]) requiredInputData.
                        get("NOMINAL_COVARIATE"));
                    break;
                case 5:
                    coefficients = coefficients(
                        (double[]) requiredInputData.get("RESPONSE"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                case 6:
                    coefficients = coefficients(
                        (double[]) requiredInputData.get("RESPONSE"),
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

        return coefficients;
    }

    /**
     * The IRLS estimate.
     * @param response the responses.
     * @param offset the offset.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @return the estimated coefficients.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] coefficients(double[] response,
                                 double[] offset,
                                 String[] ...nominalCovariate)
    {
        this.response = response;
        this.nominalCovariate = nominalCovariate;

        return (double[]) getRequiredOutput(COEFFICIENTS, response,
                                            nominalCovariate, offset);
    }

    /**
     * The IRLS estimate given zero offst.
     * @param response the responses.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @return the estimated coefficients.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] coefficients(double[] response,
                                 String[] ...nominalCovariate)
    {
        return coefficients(response, glmDataManager.zeroArray(response.length),
                            nominalCovariate);
    }

    /**
     * The IRLS estimate.
     * @param response the responses.
     * @param offset the offset.
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

    public double[] coefficients(double[] response,
                                 double[] offset,
                                 double[] ...continuousCovariate)
    {
        this.response = response;
        this.offset = offset;
        this.continuousCovariate = continuousCovariate;

        return super.coefficients(response,
                                  glmDataManager.
                                  addIntercept(continuousCovariate));
    }

    /**
     * The IRLS estimate given zero offset.
     * @param response the responses.
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

    public double[] coefficients(double[] response,
                                 double[] ...continuousCovariate)
    {
        return coefficients(response, glmDataManager.zeroArray(response.length),
                            continuousCovariate);
    }

    /**
     * The IRLS estimate.
     * @param response the responses.
     * @param offset the offset.
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

    public double[] coefficients(double[] response,
                                 double[] offset,
                                 String[][] nominalCovariate,
                                 double[][] continuousCovariate)
    {
        this.response = response;
        this.nominalCovariate = nominalCovariate;
        this.continuousCovariate = continuousCovariate;

        return (double[]) getRequiredOutput(COEFFICIENTS, response,
                                            nominalCovariate,
                                            continuousCovariate, offset);
    }

    /**
     * The IRLS estimate given zero offset.
     * @param response the responses.
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

    public double[] coefficients(double[] response,
                                 String[][] nominalCovariate,
                                 double[][] continuousCovariate)
    {
        return coefficients(response, glmDataManager.zeroArray(response.length),
                            nominalCovariate, continuousCovariate);
    }

    /**
     * The confidence intervals for the estimated coefficients.
     * @param argument the argument with the following choices,
     * <br> ALPHA: the level of significance;
     * <br> empty argument: default level of significance equal to 0.05.
     * <br><br>
     * @param dataObject the input response and values of the covariates
     *                   (excluding the one corresponding to intercept).
     * @return The confidence intervals for the estimated coefficients,
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
        alpha = (Double) argument.get(ALPHA);
        requiredInputData = getRequiredInputData(dataObject);
        if (requiredInputData.get("DATA_TYPE") != null)
        {
            switch ((Integer) requiredInputData.get("DATA_TYPE"))
            {
                case 1:
                    confidenceInterval = confidenceInterval(alpha,
                            (double[]) requiredInputData.get("RESPONSE"),
                            (double[]) requiredInputData.get("OFFSET"),
                            (String[][]) requiredInputData.get(
                                "NOMINAL_COVARIATE"));
                    break;
                case 2:
                    confidenceInterval = confidenceInterval(alpha,
                            (double[]) requiredInputData.get("RESPONSE"),
                            (double[]) requiredInputData.get("OFFSET"),
                            (double[][]) requiredInputData.get(
                                "CONTINUOUS_COVARIATE"));
                    break;
                case 3:
                    confidenceInterval = confidenceInterval(alpha,
                            (double[]) requiredInputData.get("RESPONSE"),
                            (double[]) requiredInputData.get("OFFSET"),
                            (String[][]) requiredInputData.get(
                                "NOMINAL_COVARIATE"),
                            (double[][]) requiredInputData.get(
                                "CONTINUOUS_COVARIATE"));
                    break;
                case 4:
                    confidenceInterval = confidenceInterval(alpha,
                            (double[]) requiredInputData.get("RESPONSE"),
                            (String[][]) requiredInputData.get(
                                "NOMINAL_COVARIATE"));
                    break;
                case 5:
                    confidenceInterval = confidenceInterval(alpha,
                            (double[]) requiredInputData.get("RESPONSE"),
                            (double[][]) requiredInputData.get(
                                "CONTINUOUS_COVARIATE"));
                    break;
                case 6:
                    confidenceInterval = confidenceInterval(alpha,
                            (double[]) requiredInputData.get("RESPONSE"),
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
        else
        {
            throw new IllegalArgumentException("Wrong input data.");
        }

        return confidenceInterval;
    }

    /**
     * The confidence intervals for the estimated coefficients.
     * @param alpha the level of significance.
     * @param response the responses.
     * @param offset the offset.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @return The confidence intervals for the estimated coefficients,
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
                                         double[] response,
                                         double[] offset,
                                         String[] ...nominalCovariate)
    {
        this.alpha = alpha;
        this.response = response;
        this.nominalCovariate = nominalCovariate;

        return (double[][]) getRequiredOutput(CONFIDENCE_INTERVAL, response,
                                              nominalCovariate, offset);
    }

    /**
     * The confidence intervals for the estimated coefficients given zero
     * offset.
     * @param alpha the level of significance.
     * @param response the responses.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     *                         nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @return The confidence intervals for the estimated coefficients,
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
                                         double[] response,
                                         String[] ...nominalCovariate)
    {
        return confidenceInterval(alpha, response,
                                  glmDataManager.zeroArray(response.length),
                                  nominalCovariate);
    }

    /**
     * The confidence intervals for the estimated coefficients.
     * @param alpha the level of significance.
     * @param response the responses.
     * @param offset the offset.
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @return The confidence intervals for the estimated coefficients,
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
                                         double[] response,
                                         double[] offset,
                                         double[] ...continuousCovariate)
    {
        this.response = response;
        this.offset = offset;
        this.continuousCovariate = continuousCovariate;
        argument.put(ALPHA, alpha);

        return super.confidenceInterval(argument, response,
                                        glmDataManager.
                                        addIntercept(continuousCovariate));
    }

    /**
     * The confidence intervals for the estimated coefficients given zero
     * offset.
     * @param alpha the level of significance.
     * @param response the responses.
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @return The confidence intervals for the estimated coefficients,
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
                                         double[] response,
                                         double[] ...continuousCovariate)
    {
        return confidenceInterval(alpha, response,
                                  glmDataManager.zeroArray(response.length),
                                  continuousCovariate);
    }

    /**
     * The confidence intervals for the estimated coefficients.
     * @param alpha the level of significance.
     * @param response the responses.
     * @param offset the offset.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @return The confidence intervals for the estimated coefficients,
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
                                         double[] response,
                                         double[] offset,
                                         String[][] nominalCovariate,
                                         double[][] continuousCovariate)
    {
        this.alpha = alpha;
        this.response = response;
        this.nominalCovariate = nominalCovariate;
        this.continuousCovariate = continuousCovariate;

        return (double[][]) getRequiredOutput(CONFIDENCE_INTERVAL, response,
                                              nominalCovariate,
                                              continuousCovariate, offset);
    }

    /**
     * The confidence intervals for the estimated coefficients given zero
     * offset.
     * @param alpha the level of significance.
     * @param response the responses.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     *                         nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @return The confidence intervals for the estimated coefficients,
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
                                         double[] response,
                                         String[][] nominalCovariate,
                                         double[][] continuousCovariate)
    {
        return confidenceInterval(alpha, response,
                                  glmDataManager.zeroArray(response.length),
                                  nominalCovariate, continuousCovariate);
    }

    /**
     * The z statistics for individual coefficients.
     * @param argument the empty argument.
     * @param dataObject the input responses and values of the covariates
     *                   (excluding the one corresponding to intercept).
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
                        (double[]) requiredInputData.get("RESPONSE"),
                        (double[]) requiredInputData.get("OFFSET"),
                        (String[][]) requiredInputData.
                        get("NOMINAL_COVARIATE"));
                    break;
                case 2:
                    testStatistic = testStatistic(
                        (double[]) requiredInputData.get("RESPONSE"),
                        (double[]) requiredInputData.get("OFFSET"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                case 3:
                    testStatistic = testStatistic(
                        (double[]) requiredInputData.get("RESPONSE"),
                        (double[]) requiredInputData.get("OFFSET"),
                        (String[][]) requiredInputData.get("NOMINAL_COVARIATE"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                case 4:
                    testStatistic = testStatistic(
                        (double[]) requiredInputData.get("RESPONSE"),
                        (String[][]) requiredInputData.
                        get("NOMINAL_COVARIATE"));
                    break;
                case 5:
                    testStatistic = testStatistic(
                        (double[]) requiredInputData.get("RESPONSE"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                case 6:
                    testStatistic = testStatistic(
                        (double[]) requiredInputData.get("RESPONSE"),
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
     * @param response the responses.
     * @param offset the offset.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @return the values of the z statistics.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] testStatistic(double[] response,
                                  double[] offset,
                                  String[] ...nominalCovariate)
    {
        this.response = response;
        this.nominalCovariate = nominalCovariate;

        return (double[]) getRequiredOutput(TEST_STATISTIC, response,
                                            nominalCovariate, offset);
    }

    /**
     * The z statistics for individual coefficients given zero offset.
     * @param response the responses.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @return the values of the z statistics.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] testStatistic(double[] response,
                                  String[] ...nominalCovariate)
    {
        return testStatistic(response,
                             glmDataManager.zeroArray(response.length),
                             nominalCovariate);
    }

    /**
     * The z statistics for individual coefficients.
     * @param response the responses.
     * @param offset the offset.
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

    public double[] testStatistic(double[] response,
                                  double[] offset,
                                  double[] ...continuousCovariate)
    {
        this.response = response;
        this.offset = offset;
        this.continuousCovariate = continuousCovariate;

        return super.testStatistic(response,
                                   glmDataManager.
                                   addIntercept(continuousCovariate));
    }

    /**
     * The z statistics for individual coefficients given zero offset.
     * @param response the responses.
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

    public double[] testStatistic(double[] response,
                                  double[] ...continuousCovariate)
    {
        return testStatistic(response,
                             glmDataManager.zeroArray(response.length),
                             continuousCovariate);
    }

    /**
     * The z statistics for individual coefficients.
     * @param response the responses.
     * @param offset the offset.
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

    public double[] testStatistic(double[] response,
                                  double[] offset,
                                  String[][] nominalCovariate,
                                  double[][] continuousCovariate)
    {
        this.response = response;
        this.nominalCovariate = nominalCovariate;
        this.continuousCovariate = continuousCovariate;

        return (double[]) getRequiredOutput(TEST_STATISTIC, response,
                                            nominalCovariate,
                                            continuousCovariate, offset);
    }

    /**
     * The z statistics for individual coefficients given zero offset.
     * @param response the responses.
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

    public double[] testStatistic(double[] response,
                                  String[][] nominalCovariate,
                                  double[][] continuousCovariate)
    {
        return testStatistic(response,
                             glmDataManager.zeroArray(response.length),
                             nominalCovariate, continuousCovariate);
    }

    /**
     * The p values for the z tests.
     * @param argument the empty argument.
     * @param dataObject the input responses and values of the covariates
     *                   (excluding the one corresponding to intercept).
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
                        (double[]) requiredInputData.get("RESPONSE"),
                        (double[]) requiredInputData.get("OFFSET"),
                        (String[][]) requiredInputData.
                        get("NOMINAL_COVARIATE"));
                    break;
                case 2:
                    pValue = pValue(
                        (double[]) requiredInputData.get("RESPONSE"),
                        (double[]) requiredInputData.get("OFFSET"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                case 3:
                    pValue = pValue(
                        (double[]) requiredInputData.get("RESPONSE"),
                        (double[]) requiredInputData.get("OFFSET"),
                        (String[][]) requiredInputData.get("NOMINAL_COVARIATE"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                case 4:
                    pValue = pValue(
                        (double[]) requiredInputData.get("RESPONSE"),
                        (String[][]) requiredInputData.
                        get("NOMINAL_COVARIATE"));
                    break;
                case 5:
                    pValue = pValue(
                        (double[]) requiredInputData.get("RESPONSE"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                case 6:
                    pValue = pValue(
                        (double[]) requiredInputData.get("RESPONSE"),
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

        return pValue;
    }

    /**
     * The p values for the z tests.
     * @param response the responses.
     * @param offset the offset.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @return the p values.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] pValue(double[] response,
                           double[] offset,
                           String[] ...nominalCovariate)
    {
        this.response = response;
        this.nominalCovariate = nominalCovariate;

        return (double[]) getRequiredOutput(PVALUE, response,
                                            nominalCovariate, offset);
    }

    /**
     * The p values for the z tests given zero offset.
     * @param response the responses.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
     *                                              vector.
     * @return the p values.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] pValue(double[] response,
                           String[] ...nominalCovariate)
    {
        return pValue(response, glmDataManager.zeroArray(response.length),
                      nominalCovariate);
    }

    /**
     * The p values for the z tests.
     * @param response the responses.
     * @param offset the offset.
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

    public double[] pValue(double[] response,
                           double[] offset,
                           double[] ...continuousCovariate)
    {
        this.response = response;
        this.offset = offset;
        this.continuousCovariate = continuousCovariate;

        return super.pValue(response,
                            glmDataManager.addIntercept(continuousCovariate));
    }

    /**
     * The p values for the z tests.
     * @param response the responses.
     * @param offset the offset.
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

    public double[] pValue(double[] response,
                           double[] offset,
                           String[][] nominalCovariate,
                           double[][] continuousCovariate)
    {
        this.response = response;
        this.nominalCovariate = nominalCovariate;
        this.continuousCovariate = continuousCovariate;

        return (double[]) getRequiredOutput(PVALUE, response, nominalCovariate,
                                            continuousCovariate, offset);
    }

    /**
     * The p values for the z tests given zero offset.
     * @param response the responses.
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

    public double[] pValue(double[] response,
                           String[][] nominalCovariate,
                           double[][] continuousCovariate)
    {
        return pValue(response, glmDataManager.zeroArray(response.length),
                      nominalCovariate, continuousCovariate);
    }

    /**
     * The means of the responses.
     * @param coefficients the estimated coefficients.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the means of the responses.
     * @exception IllegalArgumentException input link function is not supported.
     */

    protected double[] means(double[] coefficients,
                             double[] ...covariate)
    {
        if (link == null)
        {
            link = LOG;
        }
        means = this.means(link, coefficients, covariate);
        switch (link)
        {
            case LOG:
                for (int i = 0; i < means.length; i++)
                {
                    means[i] = offset[i] * means[i];
                }
                break;
            case IDENTITY:
                for (int i = 0; i < means.length; i++)
                {
                    means[i] = offset[i] + means[i];
                }
                break;
            case SQUARE_ROOT:
                for (int i = 0; i < means.length; i++)
                {
                    means[i] = Math.pow(Math.sqrt(offset[i]) + means[i], 2.0);
                }
                break;
            default:
                throw new IllegalArgumentException(
                    "Input link function is not supported.");
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
        weights = new double[means.length][means.length];
        switch (link)
        {
            case LOG:
                for (int i = 0; i < means.length; i++)
                {
                    weights[i][i] = means[i];
                }
                break;
            case IDENTITY:
                for (int i = 0; i < means.length; i++)
                {
                    weights[i][i] = 1.0 / means[i];
                }
                break;
            case SQUARE_ROOT:
                for (int i = 0; i < means.length; i++)
                {
                    weights[i][i] = 0.25;
                }
                break;
            default:
                throw new IllegalArgumentException(
                    "Input link function is not supported.");
        }

        return weights;
    }

    /**
     * The deviance function.
     * @param response the responses.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the residual deviance.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    protected double deviance(double[] response,
                              double[] ...covariate)
    {
        coefficients = super.coefficients(response, covariate);
        means = means(coefficients, covariate);
        deviance = 0.0;
        for (int i = 0; i < means.length; i++)
        {
            if (response[i] != 0.0)
            {
                deviance += 2 *
                        (response[i] * Math.log(response[i] / means[i]) -
                         (response[i] - means[i]));
            }
            else
            {
                deviance += 2 * means[i];
            }
        }

        return deviance;
    }

    /**
     * The null deviance function.
     * @param response the responses.
     * @return the null deviance.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    protected double nullDeviance(double[] response)
    {
        return deviance(response, new Matrix(1, response.length, 1).getArray());
    }

    /**
     * The Pearson residuals.
     * @param response the responses.
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
        responseVariance = responseVariance(means, ExponentialFamily.POISSON);
        pearsonResiduals = pearsonResiduals(responseVariance, response,
                                            covariate);
        output.put(PEARSON_RESIDUALS, pearsonResiduals);

        return pearsonResiduals;
    }

    /**
     * The deviance residuals.
     * @param response the binomial responses.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the deviance residuals.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    protected double[] devianceResiduals(double[] response,
                                         double[] ...covariate)
    {
        coefficients = super.coefficients(response, covariate);
        means = means(coefficients, covariate);
        devianceResiduals = new double[response.length];
        residuals = 0.0;
        for (int i = 0; i < means.length; i++)
        {
            if (response[i] / means[i] > 0 && response[i] != 0)
            {
                residuals = Math.sqrt(2 *
                                      (response[i] *
                                       Math.log(response[i] / means[i]) -
                                       (response[i] - means[i])));
            }
            else
            {
                residuals = Math.sqrt( -2 * (response[i] - means[i]));
            }
            if (response[i] - means[i] > 0)
            {
                devianceResiduals[i] = residuals;
            }
            else
            {
                devianceResiduals[i] = -1.0 * residuals;
            }
        }

        return devianceResiduals;
    }

    /**
     * The fitted values.
     * @param response the responses.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @param offset the offset.
     * @return the fitted values.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    private double[] fittedValues(double[] response,
                                  double[][] covariate,
                                  double[] offset)
    {
        coefficients = super.coefficients(response, covariate);
        logOffset = logOffset(offset);
        covariateMatrix = new Matrix(covariate);
        coefficientMatrix = new Matrix(coefficients, coefficients.length);
        linearPredictorMatrix = covariateMatrix.transpose().times(
                coefficientMatrix);
        linearPredictors = linearPredictorMatrix.getColumnPackedCopy();
        fittedValues = new double[response.length];
        for (int i = 0; i < linearPredictors.length; i++)
        {
            fittedValues[i] = Math.exp(linearPredictors[i] + logOffset[i]);
        }

        return fittedValues;
    }

    /**
     * The deviance table.
     * @param response the responses.
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
                                     int[] level,
                                     double[] ...covariate)
    {
        devianceTable = new double[level.length + 1][4];
        devianceTable[0][2] = response.length - 1;
        devianceTable[0][3] = nullDeviance(response);
        int k = 0;
        for (int i = 1; i < level.length + 1; i++)
        {
            k = k + level[i - 1] - 1;
            devianceTable[i][3] = deviance(response,
                                           new Matrix(covariate).getMatrix(0, k,
                    0, response.length - 1).getArray());
            devianceTable[i][2] = response.length - k - 1;
            devianceTable[i][1] = devianceTable[i - 1][3] - devianceTable[i][3];
            devianceTable[i][0] = level[i - 1] - 1;
        }
        output.put(DEVIANCE_TABLE, devianceTable);

        return devianceTable;
    }

    /**
     * The deviance table.
     * @param response the responses.
     * @param offset the offset.
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
                                     double[][] continuousCovariate,
                                     double[] offset)
    {
        devianceTable = new double[continuousCovariate.length][4];
        int c = 0;
        for (int j = 0; j < offset.length; j++)
        {
            c += offset[j];
        }
        devianceTable[0][2] = c - 1;
        devianceTable[0][3] = nullDeviance(response);
        int k = 0;
        for (int i = 1; i < continuousCovariate.length; i++)
        {
            k = k + 1;
            devianceTable[i][3] = deviance(response,
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
     * @param dataObject the input responses and values of the covariates
     *                   (excluding the one corresponding to intercept).
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
                        (double[]) requiredInputData.get("RESPONSE"),
                        (double[]) requiredInputData.get("OFFSET"),
                        (String[][]) requiredInputData.
                        get("NOMINAL_COVARIATE"));
                    break;
                case 2:
                    devianceTable = devianceTable(
                        (double[]) requiredInputData.get("RESPONSE"),
                        (double[]) requiredInputData.get("OFFSET"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                case 3:
                    devianceTable = devianceTable(
                        (double[]) requiredInputData.get("RESPONSE"),
                        (double[]) requiredInputData.get("OFFSET"),
                        (String[][]) requiredInputData.get("NOMINAL_COVARIATE"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                case 4:
                    devianceTable = devianceTable(
                        (double[]) requiredInputData.get("RESPONSE"),
                        (String[][]) requiredInputData.
                        get("NOMINAL_COVARIATE"));
                    break;
                case 5:
                    devianceTable = devianceTable(
                        (double[]) requiredInputData.get("RESPONSE"),
                        (double[][]) requiredInputData.get(
                            "CONTINUOUS_COVARIATE"));
                    break;
                case 6:
                    devianceTable = devianceTable(
                        (double[]) requiredInputData.get("RESPONSE"),
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

        return devianceTable;
    }

    /**
     * The deviance table.
     * @param response the responses.
     * @param offset the offset.
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

    public double[][] devianceTable(double[] response,
                                    double[] offset,
                                    String[] ...nominalCovariate)
    {
        this.response = response;
        this.nominalCovariate = nominalCovariate;

        return (double[][]) getRequiredOutput(DEVIANCE_TABLE, response,
                                              nominalCovariate,
                                              offset);
    }

    /**
     * The deviance table given zero offset.
     * @param response the responses.
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

    public double[][] devianceTable(double[] response,
                                    String[] ...nominalCovariate)
    {
        return devianceTable(response,
                             glmDataManager.zeroArray(response.length),
                             nominalCovariate);
    }

    /**
     * The deviance table.
     * @param response the responses.
     * @param offset the offset.
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

    public double[][] devianceTable(double[] response,
                                    double[] offset,
                                    double[] ...continuousCovariate)
    {
        this.response = response;
        this.continuousCovariate = continuousCovariate;

        return (double[][]) getRequiredOutput(DEVIANCE_TABLE, response,
                                              continuousCovariate, offset);
    }

    /**
     * The deviance table given zero offset.
     * @param response the responses.
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

    public double[][] devianceTable(double[] response,
                                    double[] ...continuousCovariate)
    {
        return devianceTable(response,
                             glmDataManager.zeroArray(response.length),
                             continuousCovariate);
    }

    /**
     * The deviance table.
     * @param response the responses.
     * @param offset the offset.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
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

    public double[][] devianceTable(double[] response,
                                    double[] offset,
                                    String[][] nominalCovariate,
                                    double[][] continuousCovariate)
    {
        this.response = response;
        this.nominalCovariate = nominalCovariate;
        this.continuousCovariate = continuousCovariate;

        return (double[][]) getRequiredOutput(DEVIANCE_TABLE, response,
                                              nominalCovariate,
                                              continuousCovariate, offset);
    }

    /**
     * The deviance table given zero offset.
     * @param response the responses.
     * @param nominalCovariate the nominal values of the covariates (excluding
     *                         the one corresponding to the intercept),
     * <br>                    nominalCovariate[j]: the (j+1)'th covariate
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

    public double[][] devianceTable(double[] response,
                                    String[][] nominalCovariate,
                                    double[][] continuousCovariate)
    {
        return devianceTable(response,
                             glmDataManager.zeroArray(response.length),
                             nominalCovariate, continuousCovariate);
    }

    /**
     * The logarithm of the offset.
     * @param offset the offset.
     * @return the logarithm of the offset.
     */

    double[] logOffset(double[] offset)
    {
        logOffset = new double[offset.length];
        for (int j = 0; j < logOffset.length; j++)
        {
            if (offset[j] > 0)
            {
                logOffset[j] = Math.log(offset[j]);
            }
            else
            {
                logOffset[j] = 0;
            }
        }

        return logOffset;
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
            requiredInputData.put("RESPONSE", dataObject[0]);
        }
        else
        {
            throw new IllegalArgumentException("Wrong input data.");
        }
        if (dataObject[1].getClass().getName().equalsIgnoreCase("[D"))
        {
            requiredInputData.put("OFFSET", dataObject[1]);
            if (dataObject.length == 3 &&
                dataObject[2].getClass().getName().equalsIgnoreCase(
                        "[[Ljava.lang.String;"))
            {
                requiredInputData.put("NOMINAL_COVARIATE", dataObject[2]);
                requiredInputData.put("DATA_TYPE", 1);
            }
            else if (dataObject.length == 3 &&
                     dataObject[2].getClass().getName().equalsIgnoreCase(
                             "[[D"))
            {
                requiredInputData.put("CONTINUOUS_COVARIATE", dataObject[2]);
                requiredInputData.put("DATA_TYPE", 2);
            }
            else if (dataObject.length == 4 &&
                     dataObject[2].getClass().getName().equalsIgnoreCase(
                             "[[Ljava.lang.String;") &&
                     dataObject[3].getClass().getName().equalsIgnoreCase(
                             "[[D"))
            {
                requiredInputData.put("NOMINAL_COVARIATE", dataObject[2]);
                requiredInputData.put("CONTINUOUS_COVARIATE", dataObject[3]);
                requiredInputData.put("DATA_TYPE", 3);
            }
            else if (dataObject.length >= 3 &&
                     (dataObject.getClass().getName().equalsIgnoreCase(
                             "[Ljava.lang.Object;") ||
                     dataObject.getClass().getName().equalsIgnoreCase("[[D")))
            {
                requiredInputData.put("CONTINUOUS_COVARIATE",
                                      DataManager.
                                      castDoubleObject(2, dataObject));
                requiredInputData.put("DATA_TYPE", 2);
            }
            else if (dataObject.length >= 3 &&
                     dataObject[2].getClass().getName().equalsIgnoreCase(
                             "[Ljava.lang.String;"))
            {
                requiredInputData.put("NOMINAL_COVARIATE",
                                      DataManager.
                                      castStringObject(2, dataObject));
                requiredInputData.put("DATA_TYPE", 1);
            }
            else
            {
                throw new IllegalArgumentException("Wrong input data.");
            }
        }
        else
        {
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
                     (dataObject.getClass().getName().equalsIgnoreCase(
                             "[Ljava.lang.Object;") ||
                     dataObject.getClass().getName().equalsIgnoreCase("[[D")))
            {
                requiredInputData.put("CONTINUOUS_COVARIATE",
                                      DataManager.
                                      castDoubleObject(1, dataObject));
                requiredInputData.put("DATA_TYPE", 5);
            }
            else if (dataObject.length >= 2 &&
                     dataObject[2].getClass().getName().equalsIgnoreCase(
                             "[Ljava.lang.String;"))
            {
                requiredInputData.put("NOMINAL_COVARIATE",
                                      DataManager.
                                      castStringObject(1, dataObject));
                requiredInputData.put("DATA_TYPE", 4);
            }
            else
            {
                throw new IllegalArgumentException("Wrong input data.");
            }
        }

        return requiredInputData;
    }

    /**
     * Obtains the required output.
     * @param option the option for the required output.
     * @param dataObject the input data.
     * @return the required output.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    private Object getRequiredOutput(javastat.util.Output option,
                                     Object ...dataObject)
    {
        Object requiredOutput;
        Object[] outputObject = new Object[4];
        if (dataObject[1].getClass().getName().equalsIgnoreCase("[[D"))
        {
            response = (double[]) dataObject[0];
            covariate = glmDataManager.addIntercept((double[][]) dataObject[1]);
            offset = (double[]) dataObject[2];
            devianceTable = devianceTable(response, covariate, offset);
        }
        else
        {
            outputObject = glmDataManager.setData("LogLinear", dataObject);
            response = (double[]) outputObject[0];
            covariate = glmDataManager.
                        addIntercept((double[][]) outputObject[1]);
            offset = (double[]) outputObject[2];
            level = (int[]) outputObject[3];
            if (dataObject.length == 4 ||
                (dataObject.length == 3 &&
                 !dataObject[2].getClass().getName().equalsIgnoreCase("[D")))
            {
                int[] newLevel = new int[level.length +
                                 continuousCovariate.length];
                for (int i = 0; i < level.length + continuousCovariate.length;
                             i++)
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
                devianceTable = devianceTable(response, newLevel, covariate);
            }
            else
            {
                devianceTable = devianceTable(response, level, covariate);
            }
        }
        switch (option)
        {
            case CONFIDENCE_INTERVAL:
                argument.put(ALPHA, this.alpha);
                requiredOutput = super.confidenceInterval(argument, response,
                        covariate);
                break;
            case TEST_STATISTIC:
                requiredOutput = super.testStatistic(response, covariate);
                break;
            case PVALUE:
                requiredOutput = super.pValue(response, covariate);
                break;
            case DEVIANCE_TABLE:
                requiredOutput = devianceTable;
                break;
            default:
                requiredOutput = super.coefficients(response, covariate);
                break;
        }

        return requiredOutput;
    }

}
