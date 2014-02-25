package javastat.regression.glm;

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
import static javastat.util.RegressionType.*;

/**
 *
 * <p>Fits generalized linear models.</p>
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
 * <br> Hashtable argument = new Hashtable();
 * <br> // Fits a logistic regression model
 * <br> argument.put(REGRESSION_TYPE, "Logistic");
 * <br> StatisticalAnalysis testclass1 =
 * <br> &nbsp;&nbsp;&nbsp;
 *        new GLM(argument1, binaryResponse, nominalCovariate).
 *        statisticalAnalysis;
 * <br> double[] coefficients = (double[]) testclass1.output.get(COEFFICIENTS);
 * <br> double[][] confidenceInterval =
 *        (double[][]) testclass1.output.get(CONFIDENCE_INTERVAL);
 * <br> double[] testStatistic =
 *        (double[]) testclass1.output.get(TEST_STATISTIC);
 * <br> double[] pValue = (double[]) testclass1.output.get(PVALUE);
 * <br> double[][] devianceTable =
 *        (double[][]) testclass1.output.get(DEVIANCE_TABLE);
 * <br>
 * <br> // Fits a log-linear regression model
 * <br> argument.put(REGRESSION_TYPE, "Log_Linear");
 * <br> StatisticalAnalysis testclass2 =
 * <br> &nbsp;&nbsp;&nbsp;
 *        new GLM(argument1, damageNumber, offset, shipData).
 *        statisticalAnalysis;
 * <br> coefficients = (double[]) testclass2.output.get(COEFFICIENTS);
 * <br> confidenceInterval =
 *        (double[][]) testclass2.output.get(CONFIDENCE_INTERVAL);
 * <br> testStatistic = (double[]) testclass2.output.get(TEST_STATISTIC);
 * <br> pValue = (double[]) testclass2.output.get(PVALUE);
 * <br> devianceTable = (double[][]) testclass2.output.get(DEVIANCE_TABLE);
 */

public class GLM extends StatisticalAnalysis
{

    /**
     * The object represents generalized linear models.
     */

    public StatisticalAnalysis statisticalAnalysis;

    /**
     * Default GLM constructor.
     */

    public GLM() {}

    /**
     * Fits generalized linear models given the input argument and data.
     * @param argument the argument with the following choices,
     * <br> REGRESSION_TYPE: the enum in the class RegressionType or the choices
     *                       "LogLinear" or "Logistic";
     * <br> empty argument: fits a logistic regression model.
     * <br><br>
     * @param dataObject the input responses and values of the covariates
     *                   (excluding the one corresponding to intercept).
     * @exception IllegalArgumentException wrong input argument(s).
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public GLM(Hashtable argument,
               Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (argument.get(REGRESSION_TYPE) != null)
        {
            if (argument.get(REGRESSION_TYPE).toString().equalsIgnoreCase(
                    "Log_Linear") ||
                argument.get(REGRESSION_TYPE).toString().equalsIgnoreCase(
                    "LogLinear"))
            {
                statisticalAnalysis = new LogLinearRegression(argument,
                        dataObject).statisticalAnalysis;
            }
            else if (argument.get(REGRESSION_TYPE).toString().
                     equalsIgnoreCase("Logistic"))
            {
                statisticalAnalysis = new LogisticRegression(argument,
                        dataObject).statisticalAnalysis;
            }
            else
            {
                throw new IllegalArgumentException("Wrong input argument(s).");
            }
        }
        else
        {
            statisticalAnalysis = new LogisticRegression(argument, dataObject);
        }
    }

}
