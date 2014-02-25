package javastat;

/**
 * <p>Title: javastat</p>
 * <p>Description: JAVA programs for statistical computations</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Tung Hai University</p>
 * @author Wen Hsiang Wei
 * @version 1.4
 */

import java.util.*;

import javastat.regression.glm.*;
import javastat.regression.lm.*;
import javastat.regression.nonparametric.*;
import javastat.survival.regression.*;
import static javastat.util.Argument.*;
import static javastat.util.RegressionType.*;

/**
 *
 * <p>Fits a general regression model. </p>
 * <p> </p>
 * <br> Example:
 * <br> double [] response = {58, 105, 88, 118, 117, 137, 157, 169, 149, 202};
 * <br> double [][] covariate = {{2, 6, 8, 8, 12, 16, 20, 20, 22, 26}};
 * <br> double [] survivalTime = {156, 1040, 59, 421, 329, 769, 365, 770,
 *                                1227, 268, 475,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                         1129, 464, 1206, 638, 563, 1106, 431, 855, 803, 115,
 *                         744,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                         477, 448, 353, 377};
 * <br> double [] survivalCensor = {1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 0, 1, 0,
 *                                  1, 1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0};
 * <br> double [][] survivalCovariate = {{1, 1, 1, 2, 1, 2, 2, 2, 2, 1, 2, 2,
 *                                        2, 2, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1,
 *                                        2, 2},
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                                {66, 38, 72, 53, 43, 59, 64, 57, 59, 74, 59,
 *                                 53, 56, 44, 56, 55, 44, 50, 43,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                                 39, 74, 50, 64, 56, 63, 58}};
 * <br> String []  binaryResponse = {"s", "d", "s", "d", "s", "d", "d", "s",
 *                                   "d", "s", "d", "d"};
 * <br> String [][] nominalCovariate = {{"a", "a", "b", "b", "c", "c", "a",
 *                                       "a", "b", "c", "c", "c"},
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                {"T", "T", "T", "F", "T", "F", "T", "F", "F", "T", "F", "F"}};
 * <br> String [][] shipData = {{"a", "a", "a", "a", "a", "a", "a", "a", "b",
 *                               "b", "b", "b", "b", "b", "b", "b",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *               "c", "c", "c", "c", "c", "c", "c", "c", "d", "d", "d", "d",
 *               "d", "d", "d", "d",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *               "e", "e", "e", "e", "e", "e", "e", "e"},
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *            {"1960-64", "1960-64", "1965-69", "1965-69", "1970-74", "1970-74",
 *             "1975-79", "1975-79",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *             "1960-64", "1960-64", "1965-69", "1965-69", "1970-74", "1970-74",
 *             "1975-79","1975-79",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *             "1960-64", "1960-64", "1965-69", "1965-69", "1970-74", "1970-74",
 *             "1975-79","1975-79",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *             "1960-64", "1960-64", "1965-69", "1965-69", "1970-74", "1970-74",
 *             "1975-79", "1975-79",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *             "1960-64", "1960-64", "1965-69", "1965-69", "1970-74", "1970-74",
 *             "1975-79", "1975-79"},
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *            {"1960-74", "1975-79", "1960-74", "1975-79", "1960-74", "1975-79",
 *             "1960-74", "1975-79",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *             "1960-74", "1975-79", "1960-74", "1975-79", "1960-74", "1975-79",
 *             "1960-74", "1975-79",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *             "1960-74", "1975-79", "1960-74", "1975-79", "1960-74", "1975-79",
 *             "1960-74", "1975-79",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *             "1960-74", "1975-79", "1960-74", "1975-79", "1960-74", "1975-79",
 *             "1960-74", "1975-79",
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *             "1960-74", "1975-79", "1960-74", "1975-79", "1960-74", "1975-79",
 *             "1960-74", "1975-79"}};
 * <br> double[] offset = {127, 63, 1095, 1095, 1512, 3353, 0, 2244, 44882,
 *                         17176, 28609,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                           20370, 7064, 13099, 0, 7177, 1179, 552, 781, 676,
 *                           783, 1948, 0, 274, 251,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                           105, 288, 192, 349, 1208, 0, 2051, 45, 0, 789, 437,
 *                           1157, 2161, 0, 542};
 * <br>   double[] damageNumber = {0, 0, 3, 4, 6, 18, 0, 11, 39, 29, 58, 53, 12,
 *                                 44, 0, 18, 1,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                           1, 0, 1, 6, 2, 0, 1, 0, 0, 0, 0, 2, 11, 0, 4, 0, 0,
 *                           7, 7, 5, 12, 0, 1};
 * <br>
 * <br> Hashtable argument = new Hashtable();
 * <br> // Fits a linear regression model
 * <br> StatisticalAnalysis regressionAnalysis1 = new RegressionModels(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument, response, covariate).statisticalAnalysis;
 * <br>
 * <br> // Fits a Cox proportional hazards regression model
 * <br> StatisticalAnalysis regressionAnalysis2 = new RegressionModels(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument, survivalTime, survivalCensor, survivalCovariate).
 *        statisticalAnalysis;
 * <br>
 * <br> // Fits a logistic regression model
 * <br> StatisticalAnalysis regressionAnalysis3 = new RegressionModels(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument, binaryResponse, nominalCovariate).
 *                         statisticalAnalysis;
 * <br>
 * <br> // Fits a log-linear regression model
 * <br> argument.put(REGRESSION_TYPE, LOG_LINEAR);
 * <br> StatisticalAnalysis regressionAnalysis4 = new RegressionModels(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument, damageNumber, offset, shipData).
 *                         statisticalAnalysis;
 * <br>
 * <br> // Fits a P-spline regression model
 * <br> argument.put(REGRESSION_TYPE, P_SPLINE);
 * <br> argument.put(SMOOTHING_PARAMETER, 10);
 * <br> argument.put(DIVISIONS, 10);
 * <br> StatisticalAnalysis regressionAnalysis5 = new RegressionModels(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument, ethanoly, ethanolx).statisticalAnalysis;
 */

public class RegressionModels extends StatisticalAnalysis
{

    /**
     * The object represents a fitted regression model.
     */

    public StatisticalAnalysis statisticalAnalysis;

    /**
     * Default RegressionModels constructor.
     */

    public RegressionModels() {}

    /**
     * Fits a general regression model.
     * @param argument the argument with the following choices,
     * <br> REGRESSION_TYPE: the enum in the class RegressionType or the choices
     *                       "LOG_LINEAR" or "LOGISTIC";
     * <br> empty argument: default models, including linear regression model,
     *                      Cox proportional hazards model or logistic
     *                      regression model.
     * <br><br>
     * @param dataObject the input data.
     * @exception IllegalArgumentException no input data.
     */

    public RegressionModels(Hashtable argument,
                            Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null)
        {
            if (argument.get(REGRESSION_TYPE) != null)
            {
                if (argument.get(REGRESSION_TYPE).toString().
                    equalsIgnoreCase("Log_Linear") ||
                    argument.get(REGRESSION_TYPE).toString().
                    equalsIgnoreCase("LogLinear"))
                {
                    statisticalAnalysis = new LogLinearRegression(argument,
                            dataObject).statisticalAnalysis;
                }
                else if (argument.get(REGRESSION_TYPE).toString().
                         equalsIgnoreCase("Logistic") &&
                         dataObject.length == 3 &&
                         dataObject[0].getClass().getName().
                         equalsIgnoreCase("[D") &&
                         dataObject[1].getClass().getName().
                         equalsIgnoreCase("[D") &&
                         dataObject[2].getClass().getName().
                         equalsIgnoreCase("[[D"))
                {
                    statisticalAnalysis = new LogisticRegression(argument,
                            dataObject).statisticalAnalysis;
                }
                else if (argument.get(REGRESSION_TYPE).toString().
                           equalsIgnoreCase("P_SPLINE") ||
                           argument.get(REGRESSION_TYPE).toString().
                           equalsIgnoreCase("PSPLINE"))
                {
                    statisticalAnalysis = new PSplineRegression(argument,
                            dataObject).statisticalAnalysis;
                }
            }
            else if (dataObject.length == 3 &&
                     dataObject[0].getClass().getName().
                     equalsIgnoreCase("[D") &&
                     dataObject[1].getClass().getName().
                     equalsIgnoreCase("[D") &&
                     dataObject[2].getClass().getName().equalsIgnoreCase("[[D"))
            {
                statisticalAnalysis = new CoxRegression(argument, dataObject).
                                      statisticalAnalysis;
            }
            else if (dataObject.length == 2 &&
                     dataObject[0].getClass().getName().
                     equalsIgnoreCase("[D") &&
                     dataObject[1].getClass().getName().equalsIgnoreCase("[[D"))
            {
                statisticalAnalysis = new LinearRegression(argument,
                        dataObject).statisticalAnalysis;
            }
            else
            {
                statisticalAnalysis = new LogisticRegression(argument,
                        dataObject).statisticalAnalysis;
            }
        }
        else
        {
            throw new IllegalArgumentException("No input data.");
        }
    }

}
