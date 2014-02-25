package javastat.regression;

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
import static javastat.regression.PsiFunction.*;
import static javastat.regression.SelectionCriterion.*;
import javastat.regression.lm.*;
import javastat.regression.nonparametric.*;
import static javastat.util.Argument.*;
import static javastat.util.RegressionType.*;
import javastat.util.*;

import Jama.*;

/**
 *
 * <p>Calculates the weighted selection criterion.</p>
 * <p> </p>
 * <br> Example:
 * <br> DataManager dm = new DataManager();
 * <br> BasicStatistics bs = new BasicStatistics();
 * <br>
 * <br> // Loads the data from MySQL DBMS
 * <br> String[][] stringData = new DBLoader("jdbc:mysql://localhost/data",
 * <br> &nbsp;&nbsp;&nbsp;
 *        "root", "", "SELECT * FROM data").data;
 * <br>
 * <br> double[][] data = dm.transpose(dm.stringToDouble(stringData));
 * <br> double[] response = data[0];
 * <br> double[][] covariate =
 *        dm.getData(1, data.length - 1, 0, data[0].length - 1, data);
 * <br>
 * <br> // Generates the weight matrix corresponding to Gaussian AR(1) random
 *         errors
 * <br> double[][] weightMatrix =
 *        dm.inverse(bs.covarianceAR1(response.length, 0.2, 1));
 * <br>
 * <br> // Non-null constructor
 * <br> double aic = new WeightedSelectionCriterion(
 * <br> &nbsp;&nbsp;&nbsp;
 *        weightMatrix, response, covariate).weightedSelectionCriterion;
 * <br> double gcv = new WeightedSelectionCriterion(
 * <br> &nbsp;&nbsp;&nbsp;
 *        GCV, weightMatrix, response, covariate).weightedSelectionCriterion;
 * <br>
 * <br> // Null constructor
 * <br> WeightedSelectionCriterion criterion = new WeightedSelectionCriterion();
 * <br> double t = criterion.
 *        weightedSelectionCriterion(T, weightMatrix, response, covariate);
 * <br> double fpe = criterion.
 *        weightedSelectionCriterion(FPE, weightMatrix, response, covariate);
 * <br>
 * <br> // Non-null constructor
 * <br> Hashtable argument1 = new Hashtable();
 * <br> argument1.put(SELECTION_CRITERION, nS);
 * <br> StatisticalAnalysis testclass1 =
 *        new WeightedSelectionCriterion(argument1,
 * <br> &nbsp;&nbsp;&nbsp;
 *        weightMatrix, response, covariate).statisticalAnalysis;
 * <br> ns = (Double) testclass1.output.get(Output.SELECTION_CRITERION);
 * <br> nsPenalty = (Double) testclass1.output.get(Output.PENALTY);
 * <br> argument1.put(SELECTION_CRITERION, U);
 * <br> testclass1 = new WeightedSelectionCriterion(argument1,
 * <br> &nbsp;&nbsp;&nbsp;
 *        weightMatrix, response, covariate).statisticalAnalysis;
 * <br> u = (Double) testclass1.output.get(Output.SELECTION_CRITERION);
 * <br> uPenalty = (Double) testclass1.output.get(Output.PENALTY);
 * <br> double wRSS = (Double) testclass1.output.get(Output.RSS);
 * <br>
 * <br> // Null constructor
 * <br> Hashtable argument2 = new Hashtable();
 * <br> WeightedSelectionCriterion testclass2 =
 *        new WeightedSelectionCriterion(argument2, null);
 * <br> aic = (Double) testclass2.weightedSelectionCriterion(argument2,
 * <br> &nbsp;&nbsp;&nbsp;
 *        weightMatrix, response, covariate);
 * <br> aicPenalty = (Double) testclass2.
 *        penalty(argument2, weightMatrix, response, covariate);
 * <br> argument2.put(SELECTION_CRITERION, GCV);
 * <br> gcv = (Double) testclass2.weightedSelectionCriterion(argument2,
 * <br> &nbsp;&nbsp;&nbsp;
 *        weightMatrix, response, covariate);
 * <br> gcvPenalty = (Double) testclass2.
 *        penalty(argument2, weightMatrix, response, covariate);
 * <br> wRSS = (Double) testclass2.
 *        weightedRSS(argument2, weightMatrix, response, covariate);
 * <br>
 * <br> // Obtains the information about the output
 * <br> out.println(testclass1.output.toString());
 * <br> out.println(testclass2.output.toString());
 */

public class WeightedSelectionCriterion extends SelectionCriterionTemplate
{

    /**
     * The object for regression model selection.
     */

    public StatisticalAnalysis statisticalAnalysis;

    /**
     * The value determining the penalty.
     */

    public double mu;

    /**
     * The transformed residuals, which are the residual vector multiplied by
     * the square root matrix of the weight matrix.
     */

    public double[] residuals;

    /**
     * The tentatively used regression model.
     */

    public RegressionType regressonType;

    /**
     * The weigthed selection criterion being used.
     */

    public SelectionCriterion selectionCriterion;

    /**
     * The psi value being used.
     */

    public PsiFunction psiFunction;

    /**
     * The smoothing parameter.
     */

    public double smoothingParameter;

    /**
     * The number of intervals on the x-domain.
     */

    public double divisions;

    /**
     * The degree of the piecewise polynomial.
     */

    public double degree;

    /**
     * The order of the penalty.
     */

    public double order;

    /**
     * The hat matrix.
     */

    private Matrix H;

    /**
     * The square root matrix of the weight matrix.
     */

    private Matrix wSQRMatrix;

    /**
     * The residual matrix.
     */

    private Matrix residualMatrix;

    /**
     * The object represents a linear regression.
     */

    private LinearRegression linearRegression;

    /**
     * The object represents a P-spline regression.
     */

    private PSplineRegression pSplineRegression;

    /**
     * The hashtable contains information about the input data.
     */

    private Hashtable index;

    /**
     * The index used for manipulating the data.
     */

    private int dataLengthIndex;

    /**
     * The covariate.
     */

    private double[][] doubleCovariate;

    /**
     * Default WeightedSelectionCriterion constructor.
     */

    public WeightedSelectionCriterion() {}

    /**
     * Calculates the weighted selection criterion for a linear regression model
     * or a P-spline model given the input arguments and data.
     * @param argument the arguments with the following choices,
     * <br> SMOOTHING_PARAMETER, DIVISIONS, DEGREE, ORDER, SELECTION CRITERION,
     *      PSI_FUNCTION:
     *      complete list of arguments for a P-spline model;
     * <br> SMOOTHING_PARAMETER, DIVISIONS, DEGREE, ORDER, SELECTION CRITERION:
     *      default psi value equal to the retained number of principal
     *      components of the weight matrix for a P-spline model;
     * <br> SMOOTHING_PARAMETER, DIVISIONS, DEGREE, ORDER:
     *      default psi value equal to the retained number of principal
     *      components of the weight matrix and GCV being used for a P-spline
     *      model;
     * <br> SMOOTHING_PARAMETER, DIVISIONS:
     *      default psi value equal to the retained number of principal
     *      components of the weight matrix and GCV being used for a cubic
     *      P-spline model based on the second order difference;
     * <br> SELECTION CRITERION, PSI_FUNCTION:
     *      complete list of arguments for a linear regression model;
     * <br> SELECTION CRITERION:
     *      default psi value equal to the retained number of principal
     *      components of the weight matrix for a linear regression model;
     * <br> empty argument:
     *      default psi value equal to the retained number of principal
     *      components of the weight matrix and AIC being used for a linear
     *      regression model;
     * <br><br>
     * @param dataObject the input weight matrix (optional), responses and
     *                   values of the covariates (excluding the one
     *                   corresponding to intercept).
     * @exception IllegalArgumentException wrong input argument(s) or data.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public WeightedSelectionCriterion(Hashtable argument,
                                      Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        index = new DataManager().dataLengthIndex(dataObject);
        dataLengthIndex = (Integer) index.get("dataLengthIndex");
        if (index.get("doubleCovariate") != null)
        {
            doubleCovariate = (double[][]) index.get("doubleCovariate");
        }
        if (argument.size() > 0 &&
            dataObject != null)
        {
            if (argument.get(SMOOTHING_PARAMETER) != null &&
                argument.get(DIVISIONS) != null &&
                argument.get(DEGREE) != null &&
                argument.get(ORDER) != null &&
                argument.get(SELECTION_CRITERION) != null &&
                argument.get(PSI_FUNCTION) != null)
            {
                if (dataLengthIndex == 1)
                {
                    statisticalAnalysis = new WeightedSelectionCriterion(
                            (SelectionCriterion) argument.get(
                            SELECTION_CRITERION),
                            (PsiFunction) argument.get(PSI_FUNCTION),
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            ((Number) argument.get(DEGREE)).doubleValue(),
                            ((Number) argument.get(ORDER)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1], doubleCovariate);
                }
                else if (dataLengthIndex == 2)
                {
                    statisticalAnalysis = new WeightedSelectionCriterion(
                            (SelectionCriterion) argument.get(
                            SELECTION_CRITERION),
                            (PsiFunction) argument.get(PSI_FUNCTION),
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            ((Number) argument.get(DEGREE)).doubleValue(),
                            ((Number) argument.get(ORDER)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1],
                            (double[][]) dataObject[2]);
                }
                else
                {
                    throw new IllegalArgumentException("Wrong input data.");
                }
            }
            else if (argument.get(SMOOTHING_PARAMETER) != null &&
                     argument.get(DIVISIONS) != null &&
                     argument.get(DEGREE) != null &&
                     argument.get(ORDER) != null &&
                     argument.get(SELECTION_CRITERION) != null)
            {
                if (dataLengthIndex == 1)
                {
                    statisticalAnalysis = new WeightedSelectionCriterion(
                            (SelectionCriterion) argument.get(
                            SELECTION_CRITERION),
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            ((Number) argument.get(DEGREE)).doubleValue(),
                            ((Number) argument.get(ORDER)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1], doubleCovariate);
                }
                else if (dataLengthIndex == 2)
                {
                    statisticalAnalysis = new WeightedSelectionCriterion(
                            (SelectionCriterion) argument.get(
                            SELECTION_CRITERION),
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            ((Number) argument.get(DEGREE)).doubleValue(),
                            ((Number) argument.get(ORDER)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1],
                            (double[][]) dataObject[2]);
                }
                else
                {
                    throw new IllegalArgumentException("Wrong input data.");
                }
            }
            else if (argument.get(SMOOTHING_PARAMETER) != null &&
                     argument.get(DIVISIONS) != null &&
                     argument.get(DEGREE) != null &&
                     argument.get(ORDER) != null)
            {
                if (dataLengthIndex == 1)
                {
                    statisticalAnalysis = new WeightedSelectionCriterion(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            ((Number) argument.get(DEGREE)).doubleValue(),
                            ((Number) argument.get(ORDER)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1], doubleCovariate);
                }
                else if (dataLengthIndex == 2)
                {
                    statisticalAnalysis = new WeightedSelectionCriterion(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            ((Number) argument.get(DEGREE)).doubleValue(),
                            ((Number) argument.get(ORDER)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1],
                            (double[][]) dataObject[2]);
                }
                else if (dataLengthIndex == 3)
                {
                    statisticalAnalysis = new WeightedSelectionCriterion(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            ((Number) argument.get(DEGREE)).doubleValue(),
                            ((Number) argument.get(ORDER)).doubleValue(),
                            (double[]) dataObject[0], doubleCovariate);
                }
                else if (dataLengthIndex == 4)
                {
                    statisticalAnalysis = new WeightedSelectionCriterion(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            ((Number) argument.get(DEGREE)).doubleValue(),
                            ((Number) argument.get(ORDER)).doubleValue(),
                            (double[]) dataObject[0],
                            (double[][]) dataObject[1]);
                }
                else
                {
                    throw new IllegalArgumentException("Wrong input data.");
                }
            }
            else if (argument.get(SMOOTHING_PARAMETER) != null &&
                     argument.get(DIVISIONS) != null)
            {
                if (dataLengthIndex == 1)
                {
                    statisticalAnalysis = new WeightedSelectionCriterion(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1], doubleCovariate);
                }
                else if (dataLengthIndex == 2)
                {
                    statisticalAnalysis = new WeightedSelectionCriterion(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1],
                            (double[][]) dataObject[2]);
                }
                else if (dataLengthIndex == 3)
                {
                    statisticalAnalysis = new WeightedSelectionCriterion(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            (double[]) dataObject[0], doubleCovariate);
                }
                else if (dataLengthIndex == 4)
                {
                    statisticalAnalysis = new WeightedSelectionCriterion(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            (double[]) dataObject[0],
                            (double[][]) dataObject[1]);
                }
                else
                {
                    throw new IllegalArgumentException("Wrong input data.");
                }
            }
            else if (argument.get(SELECTION_CRITERION) != null &&
                     argument.get(PSI_FUNCTION) != null)
            {
                if (dataLengthIndex == 1)
                {
                    statisticalAnalysis = new WeightedSelectionCriterion(
                            (SelectionCriterion) argument.get(
                            SELECTION_CRITERION),
                            (PsiFunction) argument.get(PSI_FUNCTION),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1], doubleCovariate);
                }
                else if (dataLengthIndex == 2)
                {
                    statisticalAnalysis = new WeightedSelectionCriterion(
                            (SelectionCriterion) argument.get(
                            SELECTION_CRITERION),
                            (PsiFunction) argument.get(PSI_FUNCTION),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1],
                            (double[][]) dataObject[2]);
                }
                else
                {
                    throw new IllegalArgumentException("Wrong input data.");
                }
            }
            else if (argument.get(SELECTION_CRITERION) != null)
            {
                if (dataLengthIndex == 1)
                {
                    statisticalAnalysis = new WeightedSelectionCriterion(
                            (SelectionCriterion) argument.get(
                            SELECTION_CRITERION),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1], doubleCovariate);
                }
                else if (dataLengthIndex == 2)
                {
                    statisticalAnalysis = new WeightedSelectionCriterion(
                            (SelectionCriterion) argument.get(
                            SELECTION_CRITERION),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1],
                            (double[][]) dataObject[2]);
                }
                else
                {
                    throw new IllegalArgumentException("Wrong input data.");
                }
            }
            else
            {
                throw new IllegalArgumentException(
                        "Wrong input argument(s) or data.");
            }
        }
        else if (dataObject != null)
        {
            if (dataLengthIndex == 1)
            {
                statisticalAnalysis = new WeightedSelectionCriterion(
                        (double[][]) dataObject[0],
                        (double[]) dataObject[1], doubleCovariate);
            }
            else if (dataLengthIndex == 2)
            {
                statisticalAnalysis = new WeightedSelectionCriterion(
                        (double[][]) dataObject[0],
                        (double[]) dataObject[1], (double[][]) dataObject[2]);
            }
            else if (dataLengthIndex == 3)
            {
                statisticalAnalysis = new WeightedSelectionCriterion(
                        (double[]) dataObject[0], doubleCovariate);
            }
            else if (dataLengthIndex == 4)
            {
                statisticalAnalysis = new WeightedSelectionCriterion(
                        (double[]) dataObject[0], (double[][]) dataObject[1]);
            }
            else
            {
                throw new IllegalArgumentException("Wrong input data.");
            }
        }
        else if (dataObject == null)
        {
            statisticalAnalysis = new WeightedSelectionCriterion();
        }
        else
        {
            throw new IllegalArgumentException(
                    "Wrong input argument(s) or data.");
        }
    }

    /**
     * Calculates the weighted selection criterion for a linear regression model
     * or a P-spline model.
     * @param regressionType the enum in the class RegressionType.
     * @param selectionCriterion the enum in the class SelectionCriterion.
     * @param psiFunction the enum in the class PsiFunction.
     * @param smoothingParameter the smoothing parameter for the P-spline model.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @param order the order of the penalty.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    protected WeightedSelectionCriterion(RegressionType regressionType,
                                         SelectionCriterion selectionCriterion,
                                         PsiFunction psiFunction,
                                         double smoothingParameter,
                                         double divisions,
                                         double degree, double order,
                                         double[][] weightMatrix,
                                         double[] response,
                                         double[] ...covariate)
    {
        this.regressonType = regressionType;
        this.selectionCriterion = selectionCriterion;
        this.psiFunction = psiFunction;
        this.smoothingParameter = smoothingParameter;
        this.divisions = divisions;
        this.degree = degree;
        this.order = order;
        this.weightMatrix = weightMatrix;
        this.response = response;
        this.covariate = covariate;
        weightedSelectionCriterion = weightedSelectionCriterion(regressionType,
                selectionCriterion, psiFunction, smoothingParameter, divisions,
                degree, order, weightMatrix, response, covariate);
    }

    /**
     * Calculates the weighted selection criterion for a linear regression
     * model.
     * @param selectionCriterion the enum in the class SelectionCriterion.
     * @param psiFunction the enum in the class PsiFunction.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public WeightedSelectionCriterion(SelectionCriterion selectionCriterion,
                                      PsiFunction psiFunction,
                                      double[][] weightMatrix,
                                      double[] response,
                                      double[] ...covariate)
    {
        this(LINEAR, selectionCriterion, psiFunction,
             Double.NaN, Double.NaN, Double.NaN, Double.NaN,
             weightMatrix, response, covariate);
    }

    /**
     * Calculates the weighted selection criterion with default psi value equal
     * to the retained number of principal components of the weight matrix for a
     * linear regression model.
     * @param selectionCriterion the enum in the class SelectionCriterion.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public WeightedSelectionCriterion(SelectionCriterion selectionCriterion,
                                      double[][] weightMatrix,
                                      double[] response,
                                      double[] ...covariate)
    {
        this(selectionCriterion, PCA_NUMBER, weightMatrix, response, covariate);
    }

    /**
     * Calculates the weighted AIC criterion with default psi value equal to the
     * retained number of principal components of the weight matrix for a linear
     * regression model.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public WeightedSelectionCriterion(double[][] weightMatrix,
                                      double[] response,
                                      double[] ...covariate)
    {
        this(AIC, weightMatrix, response, covariate);
    }

    /**
     * Calculates the AIC criterion with the weight matrix equal to the identity
     * matrix for a linear regression model.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public WeightedSelectionCriterion(double[] response,
                                      double[] ...covariate)
    {
        this(AIC, SAMPLE_SIZE,
             new javastat.util.DataManager().identity(response.length),
             response, covariate);
    }

    /**
     * Calculates the weighted selection criterion for a P-spline model.
     * @param selectionCriterion the enum in the class SelectionCriterion.
     * @param psiFunction the enum in the class PsiFunction.
     * @param smoothingParameter the smoothing parameter for the P-spline model.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @param order the order of the penalty.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public WeightedSelectionCriterion(SelectionCriterion selectionCriterion,
                                      PsiFunction psiFunction,
                                      double smoothingParameter,
                                      double divisions,
                                      double degree,
                                      double order,
                                      double[][] weightMatrix,
                                      double[] response,
                                      double[] ...covariate)
    {
        this(P_SPLINE, selectionCriterion, psiFunction,
             smoothingParameter, divisions, degree, order,
             weightMatrix, response, covariate);
    }

    /**
     * Calculates the weighted selection criterion with default psi value equal
     * to the retained number of principal components of the weight matrix for a
     * P-spline model.
     * @param selectionCriterion the enum in the class SelectionCriterion.
     * @param smoothingParameter the smoothing parameter for the P-spline model.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @param order the order of the penalty.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public WeightedSelectionCriterion(SelectionCriterion selectionCriterion,
                                      double smoothingParameter,
                                      double divisions,
                                      double degree,
                                      double order,
                                      double[][] weightMatrix,
                                      double[] response,
                                      double[] ...covariate)
    {
        this(selectionCriterion, PCA_NUMBER, smoothingParameter, divisions,
             degree, order, weightMatrix, response, covariate);
    }

    /**
     * Calculates the weighted GCV criterion with default psi value equal to the
     * retained number of principal components of the weight matrix for a
     * P-spline model.
     * @param smoothingParameter the smoothing parameter for the P-spline model.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @param order the order of the penalty.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public WeightedSelectionCriterion(double smoothingParameter,
                                      double divisions,
                                      double degree,
                                      double order,
                                      double[][] weightMatrix,
                                      double[] response,
                                      double[] ...covariate)
    {
        this(GCV, smoothingParameter, divisions, degree, order,
             weightMatrix, response, covariate);
    }

    /**
     * Calculates the weighted GCV criterion with default psi value equal to the
     * retained number of principal components of the weight matrix for a cubic
     * P-spline model based on the second order difference.
     * @param smoothingParameter the smoothing parameter for the P-spline model.
     * @param divisions the number of intervals on the x-domain.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public WeightedSelectionCriterion(double smoothingParameter,
                                      double divisions,
                                      double[][] weightMatrix,
                                      double[] response,
                                      double[] ...covariate)
    {
        this(GCV, smoothingParameter, divisions, 3.0, 2.0, weightMatrix,
             response, covariate);
    }

    /**
     * Calculates the GCV criterion with the weight matrix equal to the identity
     * matrix for a P-spline model.
     * @param smoothingParameter the smoothing parameter for the P-spline model.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @param order the order of the penalty.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public WeightedSelectionCriterion(double smoothingParameter,
                                      double divisions,
                                      double degree,
                                      double order,
                                      double[] response,
                                      double[] ...covariate)
    {
        this(GCV, SAMPLE_SIZE,
             smoothingParameter, divisions, degree, order,
             new javastat.util.DataManager().identity(response.length),
             response, covariate);
    }

    /**
     * Calculates the GCV criterion with the weight matrix equal to the identity
     * matrix for a cubic P-spline model based on the second order difference.
     * @param smoothingParameter the smoothing parameter for the P-spline model.
     * @param divisions the number of intervals on the x-domain.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public WeightedSelectionCriterion(double smoothingParameter,
                                      double divisions,
                                      double[] response,
                                      double[] ...covariate)
    {
        this(smoothingParameter, divisions, 3.0, 2.0,
             new javastat.util.DataManager().identity(response.length),
             response, covariate);
    }

    /**
     * Calculates the penalty for a linear regression model or a P-spline model
     * given the input arguments and data.
     * @param argument the arguments with the following choices,
     * <br> SMOOTHING_PARAMETER, DIVISIONS, DEGREE, ORDER, SELECTION CRITERION,
     *      PSI_FUNCTION:
     *      complete list of arguments for a P-spline model;
     * <br> SMOOTHING_PARAMETER, DIVISIONS, DEGREE, ORDER, SELECTION CRITERION:
     *      default psi value equal to the retained number of principal
     *      components of the weight matrix for a P-spline model;
     * <br> SMOOTHING_PARAMETER, DIVISIONS, DEGREE, ORDER:
     *      default psi value equal to the retained number of principal
     *      components of the weight matrix and GCV being used for a P-spline
     *      model;
     * <br> SMOOTHING_PARAMETER, DIVISIONS:
     *      default psi value equal to the retained number of principal
     *      components of the weight matrix and GCV being used for a cubic
     *      P-spline model based on the second order difference;
     * <br> SELECTION CRITERION, PSI_FUNCTION:
     *      complete list of arguments for a linear regression model;
     * <br> SELECTION CRITERION:
     *      default psi value equal to the retained number of principal
     *      components of the weight matrix for a linear regression model;
     * <br> empty argument:
     *      default psi value equal to the retained number of principal
     *      components of the weight matrix and AIC being used for a linear
     *      regression model;
     * <br><br>
     * @param dataObject the input weight matrix (optional), responses and
     *                   values of the covariates (excluding the one
     *                   corresponding to intercept).
     * @return the penalty.
     * @exception IllegalArgumentException wrong input argument(s) or data.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public Double penalty(Hashtable argument,
                          Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        index = new DataManager().dataLengthIndex(dataObject);
        dataLengthIndex = (Integer) index.get("dataLengthIndex");
        if (index.get("doubleCovariate") != null)
        {
            doubleCovariate = (double[][]) index.get("doubleCovariate");
        }
        if (argument.size() > 0 && dataObject != null)
        {
            if (argument.get(SMOOTHING_PARAMETER) != null &&
                argument.get(DIVISIONS) != null &&
                argument.get(DEGREE) != null &&
                argument.get(ORDER) != null &&
                argument.get(SELECTION_CRITERION) != null &&
                argument.get(PSI_FUNCTION) != null)
            {
                if (dataLengthIndex == 1)
                {
                    penalty = penalty(
                            (SelectionCriterion) argument.get(
                                    SELECTION_CRITERION),
                            (PsiFunction) argument.get(PSI_FUNCTION),
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            ((Number) argument.get(DEGREE)).doubleValue(),
                            ((Number) argument.get(ORDER)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1], doubleCovariate);
                }
                else if (dataLengthIndex == 2)
                {
                    penalty = penalty(
                            (SelectionCriterion) argument.get(
                                    SELECTION_CRITERION),
                            (PsiFunction) argument.get(PSI_FUNCTION),
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            ((Number) argument.get(DEGREE)).doubleValue(),
                            ((Number) argument.get(ORDER)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1],
                            (double[][]) dataObject[2]);
                }
                else
                {
                    throw new IllegalArgumentException("Wrong input data.");
                }
            }
            else if (argument.get(SMOOTHING_PARAMETER) != null &&
                     argument.get(DIVISIONS) != null &&
                     argument.get(DEGREE) != null &&
                     argument.get(ORDER) != null &&
                     argument.get(SELECTION_CRITERION) != null)
            {
                if (dataLengthIndex == 1)
                {
                    penalty = penalty(
                            (SelectionCriterion) argument.get(
                                    SELECTION_CRITERION),
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            ((Number) argument.get(DEGREE)).doubleValue(),
                            ((Number) argument.get(ORDER)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1], doubleCovariate);
                }
                else if (dataLengthIndex == 2)
                {
                    penalty = penalty(
                            (SelectionCriterion) argument.get(
                                    SELECTION_CRITERION),
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            ((Number) argument.get(DEGREE)).doubleValue(),
                            ((Number) argument.get(ORDER)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1],
                            (double[][]) dataObject[2]);
                }
                else
                {
                    throw new IllegalArgumentException("Wrong input data.");
                }
            }
            else if (argument.get(SMOOTHING_PARAMETER) != null &&
                     argument.get(DIVISIONS) != null &&
                     argument.get(DEGREE) != null &&
                     argument.get(ORDER) != null)
            {
                if (dataLengthIndex == 1)
                {
                    penalty = penalty(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            ((Number) argument.get(DEGREE)).doubleValue(),
                            ((Number) argument.get(ORDER)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1], doubleCovariate);
                }
                else if (dataLengthIndex == 2)
                {
                    penalty = penalty(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            ((Number) argument.get(DEGREE)).doubleValue(),
                            ((Number) argument.get(ORDER)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1],
                            (double[][]) dataObject[2]);
                }
                else if (dataLengthIndex == 3)
                {
                    penalty = penalty(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            ((Number) argument.get(DEGREE)).doubleValue(),
                            ((Number) argument.get(ORDER)).doubleValue(),
                            (double[]) dataObject[0], doubleCovariate);
                }
                else if (dataLengthIndex == 4)
                {
                    penalty = penalty(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            ((Number) argument.get(DEGREE)).doubleValue(),
                            ((Number) argument.get(ORDER)).doubleValue(),
                            (double[]) dataObject[0],
                            (double[][]) dataObject[1]);
                }
                else
                {
                    throw new IllegalArgumentException("Wrong input data.");
                }
            }
            else if (argument.get(SMOOTHING_PARAMETER) != null &&
                     argument.get(DIVISIONS) != null)
            {
                if (dataLengthIndex == 1)
                {
                    penalty = penalty(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1], doubleCovariate);
                }
                else if (dataLengthIndex == 2)
                {
                    penalty = penalty(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1],
                            (double[][]) dataObject[2]);
                }
                else if (dataLengthIndex == 3)
                {
                    penalty = penalty(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            (double[]) dataObject[0], doubleCovariate);
                }
                else if (dataLengthIndex == 4)
                {
                    penalty = penalty(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            (double[]) dataObject[0],
                            (double[][]) dataObject[1]);
                }
                else
                {
                    throw new IllegalArgumentException("Wrong input data.");
                }
            }
            else if (argument.get(SELECTION_CRITERION) != null &&
                     argument.get(PSI_FUNCTION) != null)
            {
                if (dataLengthIndex == 1)
                {
                    penalty = penalty(
                            (SelectionCriterion) argument.get(
                                    SELECTION_CRITERION),
                            (PsiFunction) argument.get(PSI_FUNCTION),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1], doubleCovariate);
                }
                else if (dataLengthIndex == 2)
                {
                    penalty = penalty(
                            (SelectionCriterion) argument.get(
                                    SELECTION_CRITERION),
                            (PsiFunction) argument.get(PSI_FUNCTION),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1],
                            (double[][]) dataObject[2]);
                }
                else
                {
                    throw new IllegalArgumentException("Wrong input data.");
                }
            }
            else if (argument.get(SELECTION_CRITERION) != null)
            {
                if (dataLengthIndex == 1)
                {
                    penalty = penalty(
                            (SelectionCriterion) argument.get(
                                    SELECTION_CRITERION),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1], doubleCovariate);
                }
                else if (dataLengthIndex == 2)
                {
                    penalty = penalty(
                            (SelectionCriterion) argument.get(
                                    SELECTION_CRITERION),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1],
                            (double[][]) dataObject[2]);
                }
                else
                {
                    throw new IllegalArgumentException("Wrong input data.");
                }
            }
            else
            {
                throw new IllegalArgumentException(
                        "Wrong input argument(s) or data.");
            }
        }
        else if (dataObject != null)
        {
            if (dataLengthIndex == 1)
            {
                penalty = penalty(
                        (double[][]) dataObject[0],
                        (double[]) dataObject[1], doubleCovariate);
            }
            else if (dataLengthIndex == 2)
            {
                penalty = penalty(
                        (double[][]) dataObject[0],
                        (double[]) dataObject[1],
                        (double[][]) dataObject[2]);
            }
            else if (dataLengthIndex == 3)
            {
                penalty = penalty(
                        (double[]) dataObject[0], doubleCovariate);
            }
            else if (dataLengthIndex == 4)
            {
                penalty = penalty(
                        (double[]) dataObject[0],
                        (double[][]) dataObject[1]);
            }
            else
            {
                throw new IllegalArgumentException("Wrong input data.");
            }
        }
        else
        {
            throw new IllegalArgumentException(
                    "Wrong input argument(s) or data.");
        }

        return penalty;
    }

    /**
     * Calculates the penalty for a linear regression model or a P-spline model.
     * @param regressionType the enum in the class RegressionType.
     * @param selectionCriterion the enum in the class SelectionCriterion.
     * @param psiFunction the enum in the class PsiFunction.
     * @param smoothingParameter the smoothing parameter for the P-spline model.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @param order the order of the penalty.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the penalty.
     * @exception IllegalArgumentException wrong input selection criterion.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    protected Double penalty(RegressionType regressionType,
                             SelectionCriterion selectionCriterion,
                             PsiFunction psiFunction,
                             double smoothingParameter,
                             double divisions,
                             double degree,
                             double order,
                             double[][] weightMatrix,
                             double[] response,
                             double[] ...covariate)
    {
        this.regressonType = regressionType;
        this.selectionCriterion = selectionCriterion;
        this.psiFunction = psiFunction;
        this.smoothingParameter = smoothingParameter;
        this.divisions = divisions;
        this.degree = degree;
        this.order = order;
        this.weightMatrix = weightMatrix;
        this.response = response;
        this.covariate = covariate;
        weightedRSS(regressionType, smoothingParameter, divisions, degree,
                    order,
                    weightMatrix, response, covariate);
        argument = new Hashtable();
        argument.put(PSI_FUNCTION, psiFunction);
        psi = psi(argument, weightMatrix, response, covariate);
        switch (regressionType)
        {
            case P_SPLINE:
                H = new Matrix(pSplineRegression.hatMatrix);
                break;
            default:
                H = new Matrix(linearRegression.hatMatrix);
        }
        mu = H.trace() / psi;
        switch (selectionCriterion)
        {
            case GCV:
                penalty = Math.pow((1.0 - mu), 2.0);
                break;
            case AIC:
                penalty = Math.exp( -1.0 * 2.0 * mu);
                break;
            case T:
                penalty = (1.0 - 2.0 * mu);
                break;
            case FPE:
                penalty = (1.0 - mu) / (1.0 + mu);
                break;
            case nS:
                penalty = 1.0 / (1.0 + 2.0 * mu);
                break;
            case U:
                penalty = ((1.0 - mu) *
                           (response.length - 1 - response.length * mu)) /
                          (response.length - 1);
                break;
            default:
                throw new IllegalArgumentException(
                        "Wrong input selection criterion.");
        }
        output.put(Output.PENALTY, penalty);

        return penalty;
    }

    /**
     * Calculates the penalty for a linear regression model.
     * @param selectionCriterion the enum in the class SelectionCriterion.
     * @param psiFunction the enum in the class PsiFunction.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the penalty.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double penalty(SelectionCriterion selectionCriterion,
                          PsiFunction psiFunction,
                          double[][] weightMatrix,
                          double[] response,
                          double[] ...covariate)
    {
        return penalty(LINEAR, selectionCriterion, psiFunction,
                       Double.NaN, Double.NaN, Double.NaN, Double.NaN,
                       weightMatrix, response, covariate);
    }

    /**
     * Calculates the penalty with default psi value equal to the retained
     * number of principal components of the weight matrix for a linear
     * regression model.
     * @param selectionCriterion the enum in the class SelectionCriterion.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the penalty.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double penalty(SelectionCriterion selectionCriterion,
                          double[][] weightMatrix,
                          double[] response,
                          double[] ...covariate)
    {
        return penalty(selectionCriterion, PCA_NUMBER, weightMatrix, response,
                       covariate);
    }

    /**
     * Calculates the penalty with default psi value equal to the retained
     * number of principal components of the weight matrix and AIC being used
     * for a linear regression model.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the penalty.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double penalty(double[][] weightMatrix,
                          double[] response,
                          double[] ...covariate)
    {
        return penalty(AIC, weightMatrix, response, covariate);
    }

    /**
     * Calculates the penalty with AIC being used and the weight matrix equal to
     * the identity matrix for a linear regression model.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the penalty.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double penalty(double[] response,
                          double[] ...covariate)
    {
        return penalty(AIC, SAMPLE_SIZE,
                       new javastat.util.DataManager().
                       identity(response.length), response, covariate);
    }

    /**
     * Calculates the penalty for a P-spline model.
     * @param selectionCriterion the enum in the class SelectionCriterion.
     * @param psiFunction the enum in the class PsiFunction.
     * @param smoothingParameter the smoothing parameter for the P-spline model.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @param order the order of the penalty.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the penalty.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double penalty(SelectionCriterion selectionCriterion,
                          PsiFunction psiFunction,
                          double smoothingParameter,
                          double divisions,
                          double degree,
                          double order,
                          double[][] weightMatrix,
                          double[] response,
                          double[] ...covariate)
    {
        return penalty(P_SPLINE, selectionCriterion, psiFunction,
                       smoothingParameter, divisions, degree, order,
                       weightMatrix, response, covariate);
    }

    /**
     * Calculates the penalty with default psi value equal to the retained
     * number of principal components of the weight matrix for a P-spline model.
     * @param selectionCriterion the enum in the class SelectionCriterion.
     * @param smoothingParameter the smoothing parameter for the P-spline model.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @param order the order of the penalty.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the penalty.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double penalty(SelectionCriterion selectionCriterion,
                          double smoothingParameter,
                          double divisions,
                          double degree,
                          double order,
                          double[][] weightMatrix,
                          double[] response,
                          double[] ...covariate)
    {
        return penalty(selectionCriterion, PCA_NUMBER,
                       smoothingParameter, divisions, degree, order,
                       weightMatrix, response, covariate);
    }

    /**
     * Calculates the penalty with default psi value equal to the retained
     * number of principal components of the weight matrix and GCV being used
     * for a P-spline model.
     * @param smoothingParameter the smoothing parameter for the P-spline model.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @param order the order of the penalty.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the penalty.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double penalty(double smoothingParameter,
                          double divisions,
                          double degree,
                          double order,
                          double[][] weightMatrix,
                          double[] response,
                          double[] ...covariate)
    {
        return penalty(GCV, smoothingParameter, divisions, degree, order,
                       weightMatrix, response, covariate);
    }

    /**
     * Calculates the penalty with default psi value equal to the retained
     * number of principal components of the weight matrix and GCV being used
     * for a cubic P-spline model based on the second order difference.
     * @param smoothingParameter the smoothing parameter for the P-spline model.
     * @param divisions the number of intervals on the x-domain.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the penalty.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double penalty(double smoothingParameter,
                          double divisions,
                          double[][] weightMatrix,
                          double[] response,
                          double[] ...covariate)
    {
        return penalty(GCV, smoothingParameter, divisions, 3.0, 2.0,
                       weightMatrix, response, covariate);
    }

    /**
     * Calculates the penalty with GCV being used and the weight matrix equal to
     * the identity matrix for a P-spline model.
     * @param smoothingParameter the smoothing parameter for the P-spline model.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @param order the order of the penalty.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the penalty.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double penalty(double smoothingParameter,
                          double divisions,
                          double degree,
                          double order,
                          double[] response,
                          double[] ...covariate)
    {
        return penalty(GCV, SAMPLE_SIZE,
                       smoothingParameter, divisions, degree, order,
                       new javastat.util.DataManager().
                       identity(response.length), response, covariate);
    }

    /**
     * Calculates the penalty with GCV being used and the weight matrix equal to
     * the identity matrix for a cubic P-spline model based on the second order
     * difference.
     * @param smoothingParameter the smoothing parameter for the P-spline model.
     * @param divisions the number of intervals on the x-domain.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the penalty.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double penalty(double smoothingParameter,
                          double divisions,
                          double[] response,
                          double[] ...covariate)
    {
        return penalty(smoothingParameter, divisions, 3.0, 2.0,
                       new javastat.util.DataManager().
                       identity(response.length), response, covariate);
    }

    /**
     * Calculates the weighted residual sum of squares for a linear regression
     * model or a P-spline model given the input arguments and data.
     * @param argument the arguments with the following choices,
     * <br> SMOOTHING_PARAMETER, DIVISIONS, DEGREE, ORDER:
     *      complete list of arguments for a P-spline model;
     * <br> SMOOTHING_PARAMETER, DIVISIONS:
     *      a cubic P-spline model based on the second order difference;
     * <br> empty argument: the linear regression model being used;
     * <br><br>
     * @param dataObject the input weight matrix (optional), responses and
     *                   values of the covariates (excluding the one
     *                   corresponding to intercept).
     * @return the weighted residual sum of squares.
     * @exception IllegalArgumentException wrong input argument(s) or data.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public Double weightedRSS(Hashtable argument,
                              Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        index = new DataManager().dataLengthIndex(dataObject);
        dataLengthIndex = (Integer) index.get("dataLengthIndex");
        if (index.get("doubleCovariate") != null)
        {
            doubleCovariate = (double[][]) index.get("doubleCovariate");
        }
        if (argument.get(SMOOTHING_PARAMETER) != null &&
            argument.get(DIVISIONS) != null &&
            argument.get(DEGREE) != null &&
            argument.get(ORDER) != null &&
            dataObject != null)
        {
            if (dataLengthIndex == 1)
            {
                weightedRSS = weightedRSS(
                        ((Number) argument.get(SMOOTHING_PARAMETER)).
                        doubleValue(),
                        ((Number) argument.get(DIVISIONS)).doubleValue(),
                        ((Number) argument.get(DEGREE)).doubleValue(),
                        ((Number) argument.get(ORDER)).doubleValue(),
                        (double[][]) dataObject[0], (double[]) dataObject[1],
                        doubleCovariate);
            }
            else if (dataLengthIndex == 2)
            {
                weightedRSS = weightedRSS(
                        ((Number) argument.get(SMOOTHING_PARAMETER)).
                        doubleValue(),
                        ((Number) argument.get(DIVISIONS)).doubleValue(),
                        ((Number) argument.get(DEGREE)).doubleValue(),
                        ((Number) argument.get(ORDER)).doubleValue(),
                        (double[][]) dataObject[0], (double[]) dataObject[1],
                        (double[][]) dataObject[2]);
            }
            else if (dataLengthIndex == 3)
            {
                weightedRSS = weightedRSS(
                        ((Number) argument.get(SMOOTHING_PARAMETER)).
                        doubleValue(),
                        ((Number) argument.get(DIVISIONS)).doubleValue(),
                        ((Number) argument.get(DEGREE)).doubleValue(),
                        ((Number) argument.get(ORDER)).doubleValue(),
                        (double[]) dataObject[0], doubleCovariate);
            }
            else
            {
                weightedRSS = weightedRSS(
                        ((Number) argument.get(SMOOTHING_PARAMETER)).
                        doubleValue(),
                        ((Number) argument.get(DIVISIONS)).doubleValue(),
                        ((Number) argument.get(DEGREE)).doubleValue(),
                        ((Number) argument.get(ORDER)).doubleValue(),
                        (double[]) dataObject[0], (double[][]) dataObject[1]);
            }
        }
        if (argument.get(SMOOTHING_PARAMETER) != null &&
            argument.get(DIVISIONS) != null &&
            dataObject != null)
        {
            if (dataLengthIndex == 1)
            {
                weightedRSS = weightedRSS(
                        ((Number) argument.get(SMOOTHING_PARAMETER)).
                        doubleValue(),
                        ((Number) argument.get(DIVISIONS)).doubleValue(),
                        (double[][]) dataObject[0], (double[]) dataObject[1],
                        doubleCovariate);
            }
            else if (dataLengthIndex == 2)
            {
                weightedRSS = weightedRSS(
                        ((Number) argument.get(SMOOTHING_PARAMETER)).
                        doubleValue(),
                        ((Number) argument.get(DIVISIONS)).doubleValue(),
                        (double[][]) dataObject[0], (double[]) dataObject[1],
                        (double[][]) dataObject[2]);
            }
            else if (dataLengthIndex == 3)
            {
                weightedRSS = weightedRSS(
                        ((Number) argument.get(SMOOTHING_PARAMETER)).
                        doubleValue(),
                        ((Number) argument.get(DIVISIONS)).doubleValue(),
                        (double[]) dataObject[0], doubleCovariate);
            }
            else
            {
                weightedRSS = weightedRSS(
                        ((Number) argument.get(SMOOTHING_PARAMETER)).
                        doubleValue(),
                        ((Number) argument.get(DIVISIONS)).doubleValue(),
                        (double[]) dataObject[0], (double[][]) dataObject[1]);
            }
        }
        else if (dataObject != null)
        {
            if (dataLengthIndex == 1)
            {
                weightedRSS = weightedRSS(
                        (double[][]) dataObject[0],
                        (double[]) dataObject[1], doubleCovariate);
            }
            else if (dataLengthIndex == 2)
            {
                weightedRSS = weightedRSS(
                        (double[][]) dataObject[0],
                        (double[]) dataObject[1], (double[][]) dataObject[2]);
            }
            else if (dataLengthIndex == 3)
            {
                weightedRSS = weightedRSS(
                        (double[]) dataObject[0], doubleCovariate);
            }
            else
            {
                weightedRSS = weightedRSS(
                        (double[]) dataObject[0], (double[][]) dataObject[1]);
            }
        }
        else
        {
            throw new IllegalArgumentException(
                    "Wrong input argument(s) or data.");
        }

        return weightedRSS;
    }

    /**
     * Calculates the weighted residual sum of squares for a linear regression
     * model or a P-spline model.
     * @param regressionType the enum in the class RegressionType.
     * @param smoothingParameter the smoothing parameter for the P-spline model.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @param order the order of the penalty.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the weighted residual sum of squares.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    protected double weightedRSS(RegressionType regressionType,
                                 double smoothingParameter,
                                 double divisions,
                                 double degree,
                                 double order,
                                 double[][] weightMatrix,
                                 double[] response,
                                 double[] ...covariate)
    {
        this.regressonType = regressionType;
        this.smoothingParameter = smoothingParameter;
        this.divisions = divisions;
        this.degree = degree;
        this.order = order;
        this.weightMatrix = weightMatrix;
        this.response = response;
        this.covariate = covariate;
        wSQRMatrix = new javastat.util.DataManager().sqrtMatrix(weightMatrix);
        switch (regressionType)
        {
            case P_SPLINE:
                pSplineRegression = new PSplineRegression(smoothingParameter,
                        divisions, degree, order,
                        weightMatrix, response, covariate[0]);
                residuals = pSplineRegression.residuals;
                residuals = (wSQRMatrix.times(new Matrix(residuals,
                        residuals.length))).getColumnPackedCopy();
                break;
            default:
                wSQRMatrix = new javastat.util.DataManager().sqrtMatrix(
                    weightMatrix);
                linearRegression = new LinearRegression(false,
                        (wSQRMatrix.times(
                            new Matrix(response, response.length))).
                               getColumnPackedCopy(),
                        ((new Matrix(new DataManager().
                                     addIntercept(covariate))).
                                        times(wSQRMatrix)).getArray());
                residuals = linearRegression.residuals;
        }
        residualMatrix = new Matrix(residuals, residuals.length);
        weightedRSS = (residualMatrix.transpose()).times(residualMatrix).
                      get(0,0);
        output.put(Output.RSS, weightedRSS);
        output.put(Output.SSE, weightedRSS);

        return weightedRSS;
    }

    /**
     * Calculates the weighted residual sum of squares for a linear regression
     * model.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the weighted residual sum of squares.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double weightedRSS(double[][] weightMatrix,
                              double[] response,
                              double[] ...covariate)
    {
        return weightedRSS(LINEAR, Double.NaN, Double.NaN, Double.NaN,
                           Double.NaN,weightMatrix, response, covariate);
    }

    /**
     * Calculates the residual sum of squares for a linear regression model.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the residual sum of squares.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double weightedRSS(double[] response,
                              double[] ...covariate)
    {
        return weightedRSS(new javastat.util.DataManager().
                           identity(response.length), response, covariate);
    }

    /**
     * Calculates the weighted residual sum of squares for a P-spline model.
     * @param smoothingParameter the smoothing parameter for the P-spline model.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @param order the order of the penalty.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the weighted residual sum of squares.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double weightedRSS(double smoothingParameter,
                              double divisions,
                              double degree,
                              double order,
                              double[][] weightMatrix,
                              double[] response,
                              double[] ...covariate)
    {
        return weightedRSS(P_SPLINE, smoothingParameter, divisions, degree,
                           order, weightMatrix, response, covariate);
    }

    /**
     * Calculates the weighted residual sum of squares for a cubic P-spline
     * model based on the second order difference.
     * @param smoothingParameter the smoothing parameter for the P-spline model.
     * @param divisions the number of intervals on the x-domain.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the weighted residual sum of squares.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double weightedRSS(double smoothingParameter,
                              double divisions,
                              double[][] weightMatrix,
                              double[] response,
                              double[] ...covariate)
    {
        return weightedRSS(P_SPLINE, smoothingParameter, divisions, 3.0, 2.0,
                           weightMatrix, response, covariate);
    }

    /**
     * Calculates the residual sum of squares for a P-spline model.
     * @param smoothingParameter the smoothing parameter for the P-spline model.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @param order the order of the penalty.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the weighted residual sum of squares.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double weightedRSS(double smoothingParameter,
                              double divisions,
                              double degree,
                              double order,
                              double[] response,
                              double[] ...covariate)
    {
        return weightedRSS(smoothingParameter, divisions, degree, order,
                           new javastat.util.DataManager().
                           identity(response.length), response, covariate);
    }

    /**
     * Calculates the residual sum of squares for a cubic P-spline model based
     * on the seocnd order difference.
     * @param smoothingParameter the smoothing parameter for the P-spline model.
     * @param divisions the number of intervals on the x-domain.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
    * @return the weighted residual sum of squares.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double weightedRSS(double smoothingParameter,
                              double divisions,
                              double[] response,
                              double[] ...covariate)
    {
        return weightedRSS(smoothingParameter, divisions, 3.0, 2.0,
                           new javastat.util.DataManager().
                           identity(response.length), response, covariate);
    }

    /**
     * Calculates the weighted selection criterion for a linear regression model
     * or a P-spline model.
     * @param regressionType the enum in the class RegressionType.
     * @param selectionCriterion the enum in the class SelectionCriterion.
     * @param psiFunction the enum in the class PsiFunction.
     * @param smoothingParameter the smoothing parameter for the P-spline model.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @param order the order of the penalty.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the weighted selection criterion.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    protected double weightedSelectionCriterion(RegressionType regressionType,
                                                SelectionCriterion
                                                selectionCriterion,
                                                PsiFunction psiFunction,
                                                double smoothingParameter,
                                                double divisions,
                                                double degree,
                                                double order,
                                                double[][] weightMatrix,
                                                double[] response,
                                                double[] ...covariate)
    {
        this.regressonType = regressionType;
        this.selectionCriterion = selectionCriterion;
        this.psiFunction = psiFunction;
        this.smoothingParameter = smoothingParameter;
        this.divisions = divisions;
        this.degree = degree;
        this.order = order;
        this.weightMatrix = weightMatrix;
        this.response = response;
        this.covariate = covariate;
        argument.put(REGRESSION_TYPE, regressionType);
        argument.put(SELECTION_CRITERION, selectionCriterion);
        argument.put(PSI_FUNCTION, psiFunction);
        if (!(Double.isNaN(smoothingParameter) ||
            Double.isNaN(divisions) ||
            Double.isNaN(degree) || Double.isNaN(order)))
        {
            argument.put(SMOOTHING_PARAMETER, smoothingParameter);
            argument.put(DIVISIONS, divisions);
            argument.put(DEGREE, degree);
            argument.put(ORDER, order);
        }

        return (Double) weightedSelectionCriterion(argument,
                weightMatrix, response, covariate);
    }

    /**
     * Calculates the weighted selection criterion for a linear regression
     * model.
     * @param selectionCriterion the enum in the class SelectionCriterion.
     * @param psiFunction the enum in the class PsiFunction.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the weighted selection criterion.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double weightedSelectionCriterion(SelectionCriterion
                                             selectionCriterion,
                                             PsiFunction psiFunction,
                                             double[][] weightMatrix,
                                             double[] response,
                                             double[] ...covariate)
    {
        return weightedSelectionCriterion(LINEAR, selectionCriterion,
                                          psiFunction,
                                          Double.NaN, Double.NaN, Double.NaN,
                                          Double.NaN,
                                          weightMatrix, response, covariate);
    }

    /**
     * Calculates the weighted selection criterion with
     * default psi value equal to the retained number of principal components of
     * the weight matrix for a linear regression model.
     * @param selectionCriterion the enum in the class SelectionCriterion.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the weighted selection criterion.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double weightedSelectionCriterion(SelectionCriterion
                                             selectionCriterion,
                                             double[][] weightMatrix,
                                             double[] response,
                                             double[] ...covariate)
    {
        return weightedSelectionCriterion(selectionCriterion, PCA_NUMBER,
                                          weightMatrix, response, covariate);
    }

    /**
     * Calculates the weighted AIC criterion with default psi value equal to the
     * retained number of principal components of the weight matrix for a linear
     * regression model.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the weighted AIC criterion.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double weightedSelectionCriterion(double[][] weightMatrix,
                                             double[] response,
                                             double[] ...covariate)
    {
        return weightedSelectionCriterion(AIC, weightMatrix, response,
                                          covariate);
    }

    /**
     * Calculates the AIC criterion for a linear regression model.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the AIC criterion.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double weightedSelectionCriterion(double[] response,
                                             double[] ...covariate)
    {
        return weightedSelectionCriterion(AIC, SAMPLE_SIZE,
                                          new javastat.util.DataManager().
                                          identity(response.length),
                                          response, covariate);
    }

    /**
     * Calculates the weighted selection criterion for a P-spline model.
     * @param selectionCriterion the enum in the class SelectionCriterion.
     * @param psiFunction the enum in the class PsiFunction.
     * @param smoothingParameter the smoothing parameter for the P-spline model.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @param order the order of the penalty.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the weighted selection criterion.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double weightedSelectionCriterion(SelectionCriterion
                                             selectionCriterion,
                                             PsiFunction psiFunction,
                                             double smoothingParameter,
                                             double divisions,
                                             double degree,
                                             double order,
                                             double[][] weightMatrix,
                                             double[] response,
                                             double[] ...covariate)
    {
        return weightedSelectionCriterion(P_SPLINE, selectionCriterion,
                                          psiFunction,
                                          smoothingParameter, divisions, degree,
                                          order,
                                          weightMatrix, response, covariate);
    }

    /**
     * Calculates the weighted selection criterion with default psi value equal
     * to the retained number of principal components of the weight matrix for a
     * P-spline model.
     * @param selectionCriterion the enum in the class SelectionCriterion.
     * @param smoothingParameter the smoothing parameter for the P-spline model.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @param order the order of the penalty.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the weighted selection criterion.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double weightedSelectionCriterion(SelectionCriterion
                                             selectionCriterion,
                                             double smoothingParameter,
                                             double divisions,
                                             double degree,
                                             double order,
                                             double[][] weightMatrix,
                                             double[] response,
                                             double[] ...covariate)
    {
        return weightedSelectionCriterion(selectionCriterion, PCA_NUMBER,
                                          smoothingParameter, divisions, degree,
                                          order,
                                          weightMatrix, response, covariate);
    }

    /**
     * Calculates the weighted GCV criterion with default psi value equal to the
     * retained number of principal components of the weight matrix for a
     * P-spline model.
     * @param smoothingParameter the smoothing parameter for the P-spline model.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @param order the order of the penalty.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the weighted GCV criterion.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double weightedSelectionCriterion(double smoothingParameter,
                                             double divisions,
                                             double degree,
                                             double order,
                                             double[][] weightMatrix,
                                             double[] response,
                                             double[] ...covariate)
    {
        return weightedSelectionCriterion(GCV, smoothingParameter, divisions,
                                          degree, order,
                                          weightMatrix, response, covariate);
    }

    /**
     * Calculates the weighted GCV criterion with default psi value equal to the
     * retained number of principal components of the weight matrix for a cubic
     * P-spline model based on the second order difference.
     * @param smoothingParameter the smoothing parameter for the P-spline model.
     * @param divisions the number of intervals on the x-domain.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the weighted GCV criterion.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double weightedSelectionCriterion(double smoothingParameter,
                                             double divisions,
                                             double[][] weightMatrix,
                                             double[] response,
                                             double[] ...covariate)
    {
        return weightedSelectionCriterion(GCV, smoothingParameter, divisions,
                                          3.0, 2.0,
                                          weightMatrix, response, covariate);
    }

    /**
     * Calculates the GCV criterion for a P-spline model.
     * @param smoothingParameter the smoothing parameter for the P-spline model.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @param order the order of the penalty.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the GCV criterion.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double weightedSelectionCriterion(double smoothingParameter,
                                             double divisions,
                                             double degree,
                                             double order,
                                             double[] response,
                                             double[] ...covariate)
    {
        return weightedSelectionCriterion(GCV, SAMPLE_SIZE,
                                          smoothingParameter, divisions, degree,
                                          order,
                                          new javastat.util.DataManager().
                                          identity(response.length),
                                          response, covariate);
    }

    /**
     * Calculates the GCV criterion for a cubic P-spline model based on the
     * second order difference.
     * @param smoothingParameter the smoothing parameter for the P-spline model.
     * @param divisions the number of intervals on the x-domain.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the GCV criterion.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double weightedSelectionCriterion(double smoothingParameter,
                                             double divisions,
                                             double[] response,
                                             double[] ...covariate)
    {
        return weightedSelectionCriterion(smoothingParameter, divisions, 3.0,
                                          2.0, new javastat.util.DataManager().
                                          identity(response.length),
                                          response, covariate);
    }

}
