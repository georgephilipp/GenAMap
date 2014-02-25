package javastat.regression.nonparametric;

/**
 * <p>Title: javastat</p>
 * <p>Description: JAVA programs for statistical computations</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Tung Hai University</p>
 * @author Wen Hsiang Wei and Hsu Hui Chan
 * @version 1.4
 */

import java.util.*;

import javastat.*;
import javastat.regression.*;
import static javastat.util.Argument.*;
import javastat.util.*;

import Jama.*;

/**
 *
 * <p>Fits a weighted smoothing spline using a p-spline basis.</p>
 * <p> </p>
 * <br> Example:
 * <br> double [] ethanoly = {
 *        3.741, 2.295, 1.498, 2.881, 0.760, 3.120, 0.638, 1.170, 2.358, 0.606,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        3.669, 1.000, 0.981, 1.192, 0.926, 1.590, 1.806, 1.962, 4.028, 3.148,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        1.836, 2.845, 1.013, 0.414, 0.812, 0.374, 3.623, 1.869, 2.836, 3.567,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        0.866, 1.369, 0.542, 2.739, 1.200, 1.719, 3.423, 1.634, 1.021, 2.157,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        3.361, 1.390, 1.947, 0.962, 0.571, 2.219, 1.419, 3.519, 1.732, 3.206,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        2.471, 1.777, 2.571, 3.952, 3.931, 1.587, 1.397, 3.536, 2.202, 0.756,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        1.620, 3.656, 2.964, 3.760, 0.672, 3.677, 3.517, 3.290, 1.139, 0.727,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        2.581, 0.923, 1.527, 3.388, 2.085, 0.966, 3.488, 0.754, 0.797, 2.064,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        3.732, 0.586, 0.561, 0.563, 0.678, 0.370, 0.530, 1.900};
 * <br> double [] ethanolx = {
 *        0.907, 0.761, 1.108, 1.016, 1.189, 1.001, 1.231, 1.123, 1.042, 1.215,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        0.930, 1.152, 1.138, 0.601, 0.696, 0.686, 1.072, 1.074, 0.934, 0.808,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        1.071, 1.009, 1.142, 1.229, 1.175, 0.568, 0.977, 0.767, 1.006, 0.893,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        1.152, 0.693, 1.232, 1.036, 1.125, 1.081, 0.868, 0.762, 1.144, 1.045,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        0.797, 1.115, 1.070, 1.219, 0.637, 0.733, 0.715, 0.872, 0.765, 0.878,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        0.811, 0.676, 1.045, 0.968, 0.846, 0.684, 0.729, 0.911, 0.808, 1.168,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        0.749, 0.892, 1.002, 0.812, 1.230, 0.804, 0.813, 1.002, 0.696, 1.199,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        1.030, 0.602, 0.694, 0.816, 1.037, 1.181, 0.899, 1.227, 1.180, 0.795,
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        0.990, 1.201, 0.629, 0.608, 0.584, 0.562, 0.535, 0.655};
 * <br>
 * <br> // Non-null constructor
 * <br> double[] fittedValues =
 *        new PSplineRegression(0.013, 10, 3, 2, ethanoly, ethanolx).
 *        fittedValues;
 * <br>
 * <br> // Null constructor
 * <br> double[] coefficients =
 *        new PSplineRegression().
 *        coefficients(0.013, 10, 3, 2, ethanoly, ethanolx);
 * <br>
 * <br> // Non-null constructor
 * <br> Hashtable argument1 = new Hashtable();
 * <br> argument1.put(SMOOTHING_PARAMETER, 10);
 * <br> argument1.put(DIVISIONS, 10);
 * <br> StatisticalAnalysis testclass1 = new PSplineRegression(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument1, ethanoly, ethanolx).statisticalAnalysis;
 * <br> fittedValues = (double[]) testclass1.output.get(FITTED_VALUES);
 * <br>
 * <br> // Null constructor
 * <br> Hashtable argument2 = new Hashtable();
 * <br> PSplineRegression testclass2 = new PSplineRegression(argument2, null);
 * <br> argument2.put(SMOOTHING_PARAMETER, 1);
 * <br> argument2.put(DIVISIONS, 20);
 * <br> coefficients = testclass2.coefficients(argument2, ethanoly, ethanolx);
 * <br>
 * <br> // Obtains the information about the output
 * <br> out.println(testclass1.output.toString());
 * <br> out.println(testclass2.output.toString());
 */

public class PSplineRegression extends StatisticalAnalysis
{

    /**
     * The object represents a P-spline regression analysis.
     */

    public StatisticalAnalysis statisticalAnalysis;

    /**
     * The trace of the hat matrix.
     */

    public double hatMatrixTrace;

    /**
     * The spline regression design matrix.
     */

    public double[][] basis;

    /**
     * The difference matrix.
     */

    public double[][] difference;

    /**
     * The hat matrix.
     */

    public double[][] hatMatrix;

    /**
     * The estimated coefficients.
     */

    public double[] coefficients;

    /**
     * The fitted values.
     */

    public double[] fittedValues;

    /**
     * The residuals.
     */

    public double[] residuals;

    /**
     * The optimal smoothing parameter.
     */

    public double minimizer;

    /**
     * The responses.
     */

    public double[] response;

    /**
     * The values of the covariates (excluding the one corresponding to the
     * intercept),
     * <br> covariate[j]: the (j+1)'th covariate vector.
     */

    public double[][] covariate;

    /**
     * The weight matrix.
     */

    public double[][] weightMatrix;

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
     * The weigthed selection criterion being used.
     */

    public SelectionCriterion selectionCriterion;

    /**
     * The psi value being used.
     */

    public PsiFunction psiFunction;

    /**
     * The spline regression design matrix.
     */

    private BSplineBasis bSplineBasis;

    /**
     * The matrix used as searching for the minimizer of the weighted selection
     * criterion.
     */

    private Matrix A;

    /**
     * The first derivative of A.
     */

    private Matrix dA;

    /**
     * The spline regression design matrix.
     */

    private Matrix B;

    /**
     * The difference matrix.
     */

    private Matrix D;

    /**
     * The hat matrix.
     */

    private Matrix H;

    /**
     * The weight matrix.
     */

    private Matrix W;

    /**
     * The vector of responses.
     */

    private Matrix y;

    /**
     * The first derivative of the hat matrix.
     */

    private Matrix dH;

    /**
     * The second derivative of the hat matrix.
     */

    private Matrix ddH;

    /**
     * The weigthed residual sum of squares.
     */

    private double WRSS;

    /**
     * The first derivative of the weigthed residual sum of squares.
     */

    private double dWRSS;

    /**
     * The second derivative of the weigthed residual sum of squares.
     */

    private double ddWRSS;

    /**
     * The value determining the penalty.
     */

    private double mu;

    /**
     * The first derivative of mu function.
     */

    private double dmu;

    /**
     * The second derivative of mu function.
     */

    private double ddmu;

    /**
     * The penalty function.
     */

    private double phi;

    /**
     * The first derivative of the penalty function.
     */

    private double dphi;

    /**
     * The second derivative of the penalty function.
     */

    private double ddphi;

    /**
     * The first derivative of the weighted selection criterion.
     */

    private double objFunction;

    /**
     * The second derivative of the weighted selection criterion.
     */

    private double dObjFunction;

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
     * The variances of the principal components of the weight matrix.
     */

    private double[] pcaVariance;

    /**
     * The value reflects the effect of the weight matrix on the weighted
     * prediction risk.
     */

    private double psi;

    /**
     * Default PSplineRegression constructor.
     */

    public PSplineRegression() {}

    /**
     * Fits a weighted smoothing spline using the p-spline basis
     * given the input arguments and data.
     * @param argument the arguments with the following choices,
     * <br> SMOOTHING_PARAMETER, DIVISIONS, DEGREE, ORDER:
     *      complete list of arguments for a P-spline model;
     * <br> SMOOTHING_PARAMETER, DIVISIONS:
     *      a cubic P-spline model based on the second order difference.
     * <br> empty argument: default constructor.
     * <br><br>
     * @param dataObject the input weight matrix (optional), responses and
     *                   values of the covariates (excluding the one
     *                   corresponding to intercept).
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public PSplineRegression(Hashtable argument,
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
                argument.get(ORDER) != null)
            {
                if (dataLengthIndex == 1)
                {
                    statisticalAnalysis = new PSplineRegression(
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
                    statisticalAnalysis = new PSplineRegression(
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
                     argument.get(DIVISIONS) != null)
            {
                if (dataLengthIndex == 1)
                {
                    statisticalAnalysis = new PSplineRegression(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1], doubleCovariate);
                }
                else if (dataLengthIndex == 2)
                {
                    statisticalAnalysis = new PSplineRegression(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1],
                            (double[][]) dataObject[2]);
                }
                else if (dataLengthIndex == 3)
                {
                    statisticalAnalysis = new PSplineRegression(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            (double[]) dataObject[0], doubleCovariate);
                }
                else if (dataLengthIndex == 4)
                {
                    statisticalAnalysis = new PSplineRegression(
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
        }
        else if (dataObject == null)
        {
            statisticalAnalysis = new PSplineRegression();
        }
        else
        {
            throw new IllegalArgumentException(
                    "Wrong input argument(s) or data.");
        }
    }

    /**
     * Fits a weighted smoothing spline using the p-spline basis.
     * @param smoothingParameter the smoothing parameter.
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

    public PSplineRegression(double smoothingParameter,
                             double divisions,
                             double degree,
                             double order,
                             double[][] weightMatrix,
                             double[] response,
                             double[] ...covariate)
    {
        this.smoothingParameter = smoothingParameter;
        this.divisions = divisions;
        this.degree = degree;
        this.order = order;
        this.weightMatrix = weightMatrix;
        this.response = response;
        this.covariate = covariate;
        coefficients = coefficients(smoothingParameter, divisions, degree,
                                    order,
                                    weightMatrix, response, covariate);
    }

    /**
     * Fits a weighted cubic smoothing spline using the p-spline basis based on
     * the second order difference.
     * @param smoothingParameter the smoothing parameter.
     * @param divisions the number of intervals on the x-domain.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public PSplineRegression(double smoothingParameter,
                             double divisions,
                             double[][] weightMatrix,
                             double[] response,
                             double[] ...covariate)
    {
        coefficients = coefficients(smoothingParameter, divisions, 3.0, 2.0,
                                    weightMatrix, response, covariate);
    }

    /**
     * Fits a smoothing spline using the p-spline basis.
     * @param smoothingParameter the smoothing parameter.
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

    public PSplineRegression(double smoothingParameter,
                             double divisions,
                             double degree,
                             double order,
                             double[] response,
                             double[] ...covariate)
    {
        this(smoothingParameter, divisions, degree, order,
             new DataManager().identity(response.length), response, covariate);
    }

    /**
     * Fits a cubic smoothing spline using the p-spline basis based on the
     * second order difference.
     * @param smoothingParameter the smoothing parameter.
     * @param divisions the number of intervals on the x-domain.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public PSplineRegression(double smoothingParameter,
                             double divisions,
                             double[] response,
                             double[] ...covariate)
    {
        this(smoothingParameter, divisions, 3.0, 2.0,
             new DataManager().identity(response.length), response, covariate);
    }

    /**
     * Calculates the hat matrix for the weighted smoothing spline .
     * @param argument the arguments with the following choices,
     * <br> SMOOTHING_PARAMETER, DIVISIONS, DEGREE, ORDER:
     *      complete list of arguments for a P-spline model;
     * <br> SMOOTHING_PARAMETER, DIVISIONS:
     *      a cubic P-spline model based on the second order difference.
     * <br><br>
     * @param dataObject the input weight matrix (optional) and values of
     *                   the covariates (excluding the one corresponding to
     *                   intercept).
     * @return the hat matrix.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double[][] hatMatrix(Hashtable argument,
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
                argument.get(ORDER) != null)
            {
                if (dataObject.length >= 2 &&
                    dataObject[0].getClass().getName().
                    equalsIgnoreCase("[[D") &&
                    dataObject[1].getClass().getName().equalsIgnoreCase("[[D"))
                {
                    hatMatrix = hatMatrix(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            ((Number) argument.get(DEGREE)).doubleValue(),
                            ((Number) argument.get(ORDER)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[][]) dataObject[1]);
                }
                else if (dataObject.length >= 2 &&
                         dataObject[0].getClass().getName().
                         equalsIgnoreCase("[[D") &&
                         dataObject[1].getClass().getName().
                         equalsIgnoreCase("[D"))
                {
                    hatMatrix = hatMatrix(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            ((Number) argument.get(DEGREE)).doubleValue(),
                            ((Number) argument.get(ORDER)).doubleValue(),
                            (double[][]) dataObject[0],
                            new DataManager().castDoubleObject(1, dataObject));
                }
                else if (dataObject.length == 1 &&
                         dataObject[0].getClass().getName().
                         equalsIgnoreCase("[[D"))
                {
                    hatMatrix = hatMatrix(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            ((Number) argument.get(DEGREE)).doubleValue(),
                            ((Number) argument.get(ORDER)).doubleValue(),
                            (double[][]) dataObject[0]);
                }
                else if (dataObject.length >= 1 &&
                           dataObject[0].getClass().getName().
                           equalsIgnoreCase("[D"))
                {
                    hatMatrix = hatMatrix(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            ((Number) argument.get(DEGREE)).doubleValue(),
                            ((Number) argument.get(ORDER)).doubleValue(),
                            (double[][]) dataObject);
                }
                else
                {
                    throw new IllegalArgumentException("Wrong input data.");
                }
            }
            else if (argument.get(SMOOTHING_PARAMETER) != null &&
                     argument.get(DIVISIONS) != null)
            {
                if (dataObject.length >= 2 &&
                    dataObject[0].getClass().getName().
                    equalsIgnoreCase("[[D") &&
                    dataObject[1].getClass().getName().equalsIgnoreCase("[[D"))
                {
                    hatMatrix = hatMatrix(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[][]) dataObject[1]);
                }
                else if (dataObject.length >= 2 &&
                         dataObject[0].getClass().getName().
                         equalsIgnoreCase("[[D") &&
                         dataObject[1].getClass().getName().
                         equalsIgnoreCase("[D"))
                {
                    hatMatrix = hatMatrix(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            (double[][]) dataObject[0],
                            new DataManager().castDoubleObject(1, dataObject));
                }
                else if (dataObject.length == 1 &&
                         dataObject[0].getClass().getName().
                         equalsIgnoreCase("[[D"))
                {
                    hatMatrix = hatMatrix(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            (double[][]) dataObject[0]);
                }
                else if (dataObject.length >= 1 &&
                         dataObject[0].getClass().getName().
                         equalsIgnoreCase("[D"))
                {
                    hatMatrix = hatMatrix(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            (double[][]) dataObject);
                }
                else
                {
                    throw new IllegalArgumentException("Wrong input data.");
                }
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

        return hatMatrix;
    }

    /**
     * Calculates the hat matrix for the weighted smoothing spline given the
     * input arguments and data.
     * @param smoothingParameter the smoothing parameter.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @param order the order of the penalty.
     * @param weightMatrix the weight matrix.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the hat matrix.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double[][] hatMatrix(double smoothingParameter,
                                double divisions,
                                double degree,
                                double order,
                                double[][] weightMatrix,
                                double[] ...covariate)
    {
        this.smoothingParameter = smoothingParameter;
        this.divisions = divisions;
        this.degree = degree;
        this.order = order;
        this.weightMatrix = weightMatrix;
        this.covariate = covariate;
        new DataManager().checkDimension(covariate);
        bSplineBasis = new BSplineBasis();
        basis = bSplineBasis.basis(divisions, degree, covariate[0]);
        B = new Matrix(basis);
        difference = bSplineBasis.difference(order,
                                             Matrix.identity(B.
                getColumnDimension(), B.getColumnDimension()).getArray());
        W = new Matrix(weightMatrix);
        D = new Matrix(difference);
        H = B.times(((B.transpose().times(W).times(B)).plus(((D.transpose()).
                times(smoothingParameter)).times(D))).inverse()).times(B.
                transpose()).times(W);
        hatMatrix = H.getArray();
        output.put(Output.HAT_MATRIX, hatMatrix);

        return hatMatrix;
    }

    /**
     * Calculates the hat matrix for the weighted cubic smoothing spline with
     * the second order difference matrix.
     * @param smoothingParameter the smoothing parameter.
     * @param divisions the number of intervals on the x-domain.
     * @param weightMatrix the weight matrix.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the hat matrix.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double[][] hatMatrix(double smoothingParameter,
                                double divisions,
                                double[][] weightMatrix,
                                double[] ...covariate)
    {
        return hatMatrix(smoothingParameter, divisions, 3.0, 2.0, weightMatrix,
                         covariate);
    }

    /**
     * Calculates the hat matrix for the smoothing spline.
     * @param smoothingParameter the smoothing parameter.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @param order the order of the penalty.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the hat matrix.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double[][] hatMatrix(double smoothingParameter,
                                double divisions,
                                double degree,
                                double order,
                                double[] ...covariate)
    {
        return hatMatrix(smoothingParameter, divisions, degree, order,
                         new DataManager().identity(covariate.length),
                         covariate);
    }

    /**
     * Calculates the hat matrix for the cubic smoothing spline with the
     * second order difference matrix.
     * @param smoothingParameter the smoothing parameter.
     * @param divisions the number of intervals on the x-domain.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the hat matrix.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double[][] hatMatrix(double smoothingParameter,
                                double divisions,
                                double[] ...covariate)
    {
        return hatMatrix(smoothingParameter, divisions, 3.0, 2.0, covariate);
    }

    /**
     * Returns the estimated coefficients for the weighted smoothing spline
     * given the input arguments and data.
     * @param argument the arguments with the following choices,
     * <br> SMOOTHING_PARAMETER, DIVISIONS, DEGREE, ORDER:
     * complete list of arguments for a P-spline model;
     * <br> SMOOTHING_PARAMETER, DIVISIONS:
     * a cubic P-spline model based on the second order difference.
     * <br><br>
     * @param dataObject the input weight matrix (optional), responses and
     *                   values of the covariates (excluding the one
     *                   corresponding to intercept).
     * @return the estimated coefficients.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double[] coefficients(Hashtable argument,
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
                argument.get(ORDER) != null)
            {
                if (dataLengthIndex == 1)
                {
                    coefficients = coefficients(
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
                    coefficients = coefficients(
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
                     argument.get(DIVISIONS) != null)
            {
                if (dataLengthIndex == 1)
                {
                    coefficients = coefficients(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1], doubleCovariate);
                }
                else if (dataLengthIndex == 2)
                {
                    coefficients = coefficients(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1],
                            (double[][]) dataObject[2]);
                }
                else if (dataLengthIndex == 3)
                {
                    coefficients = coefficients(
                            ((Number) argument.get(SMOOTHING_PARAMETER)).
                            doubleValue(),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            (double[]) dataObject[0], doubleCovariate);
                }
                else if (dataLengthIndex == 4)
                {
                    coefficients = coefficients(
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
        }
        else
        {
            throw new IllegalArgumentException(
                    "Wrong input argument(s) or data.");
        }

        return coefficients;
    }

    /**
     * Returns the estimated coefficients for the weighted smoothing spline.
     * @param smoothingParameter the smoothing parameter.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @param order the order of the penalty.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the estimated coefficients.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double[] coefficients(double smoothingParameter,
                                 double divisions,
                                 double degree,
                                 double order,
                                 double[][] weightMatrix,
                                 double[] response,
                                 double[] ...covariate)
    {
        this.smoothingParameter = smoothingParameter;
        this.divisions = divisions;
        this.degree = degree;
        this.order = order;
        this.weightMatrix = weightMatrix;
        this.response = response;
        this.covariate = covariate;
        new DataManager().checkDimension(covariate);
        hatMatrix = hatMatrix(smoothingParameter, divisions, degree, order,
                              weightMatrix, covariate);
        y = new Matrix(response, response.length);
        coefficients = ((B.transpose().times(W).times(B)).plus(((D.transpose()).
                times(smoothingParameter)).
                times(D)).solve(B.transpose().times(W).times(y))).
                       getColumnPackedCopy();
        fittedValues = H.times(y).getColumnPackedCopy();
        residuals = (y.minus(H.times(y))).getColumnPackedCopy();
        output.put(Output.COEFFICIENTS, coefficients);
        output.put(Output.FITTED_VALUES, fittedValues);
        output.put(Output.RESIDUALS, residuals);

        return coefficients;
    }

    /**
     * Returns the estimated coefficients for the weighted cubic smoothing
     * spline with the second order difference matrix.
     * @param smoothingParameter the smoothing parameter.
     * @param divisions the number of intervals on the x-domain.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the estimated coefficients.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double[] coefficients(double smoothingParameter,
                                 double divisions,
                                 double[][] weightMatrix,
                                 double[] response,
                                 double[] ...covariate)
    {
        return coefficients(smoothingParameter, divisions, 3.0, 2.0,
                            weightMatrix, response, covariate);
    }

    /**
     * Returns the estimated coefficients for the smoothing spline.
     * @param smoothingParameter the smoothing parameter.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @param order the order of the penalty.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the estimated coefficients.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double[] coefficients(double smoothingParameter,
                                 double divisions,
                                 double degree,
                                 double order,
                                 double[] response,
                                 double[] ...covariate)
    {
        return coefficients(smoothingParameter, divisions, degree, order,
                            new DataManager().identity(response.length),
                            response, covariate);
    }

    /**
     * Returns the estimated coefficients for the cubic smoothing spline with
     * the second order difference matrix.
     * @param smoothingParameter the smoothing parameter.
     * @param divisions the number of intervals on the x-domain.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the estimated coefficients.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double[] coefficients(double smoothingParameter,
                                 double divisions,
                                 double[] response,
                                 double[] ...covariate)
    {
        return coefficients(smoothingParameter, divisions, 3.0, 2.0, response,
                            covariate);
    }

    /**
     * Obtains the minimizer of the weighted selection criterion given the input
     * arguments and data.
     * @param argument the arguments with the following choices,
     * <br> DIVISIONS, DEGREE, ORDER, SELECTION CRITERION, PSI_FUNCTION:
     *      complete list of arguments for a P-spline model;
     * <br> DIVISIONS, DEGREE, ORDER, SELECTION CRITERION:
     *      default psi value equal to the retained number of principal
     *      components of the weight matrix for a P-spline model;
     * <br> DIVISIONS, DEGREE, ORDER:
     *      default psi value equal to the retained number of principal
     *      components of the weight matrix and GCV being used for a P-spline
     *      model;
     * <br> DIVISIONS:
     *      default psi value equal to the retained number of principal
     *      components of the weight matrix and GCV being used for a cubic
     *      P-spline model based on the second order difference;
     * <br><br>
     * @param dataObject the input weight matrix (optional), responses and
     *                   values of the covariates (excluding the one
     *                   corresponding to intercept).
     * @return the minimizer.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double minimizer(Hashtable argument,
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
            if (argument.get(DIVISIONS) != null &&
                argument.get(DEGREE) != null &&
                argument.get(ORDER) != null &&
                argument.get(SELECTION_CRITERION) != null &&
                argument.get(PSI_FUNCTION) != null)
            {
                if (dataLengthIndex == 1)
                {
                    minimizer = minimizer(
                            (SelectionCriterion) argument.get(
                            SELECTION_CRITERION),
                            (PsiFunction) argument.get(PSI_FUNCTION),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            ((Number) argument.get(DEGREE)).doubleValue(),
                            ((Number) argument.get(ORDER)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1], doubleCovariate);
                }
                else if (dataLengthIndex == 2)
                {
                    minimizer = minimizer(
                            (SelectionCriterion) argument.get(
                            SELECTION_CRITERION),
                            (PsiFunction) argument.get(PSI_FUNCTION),
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
            else if (argument.get(DIVISIONS) != null &&
                     argument.get(DEGREE) != null &&
                     argument.get(ORDER) != null &&
                     argument.get(SELECTION_CRITERION) != null)
            {
                if (dataLengthIndex == 1)
                {
                    minimizer = minimizer(
                            (SelectionCriterion) argument.get(
                            SELECTION_CRITERION),
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            ((Number) argument.get(DEGREE)).doubleValue(),
                            ((Number) argument.get(ORDER)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1], doubleCovariate);
                }
                else if (dataLengthIndex == 2)
                {
                    minimizer = minimizer(
                            (SelectionCriterion) argument.get(
                            SELECTION_CRITERION),
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
            else if (argument.get(DIVISIONS) != null &&
                     argument.get(DEGREE) != null &&
                     argument.get(ORDER) != null)
            {
                if (dataLengthIndex == 1)
                {
                    minimizer = minimizer(
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            ((Number) argument.get(DEGREE)).doubleValue(),
                            ((Number) argument.get(ORDER)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1], doubleCovariate);
                }
                else if (dataLengthIndex == 2)
                {
                    minimizer = minimizer(
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            ((Number) argument.get(DEGREE)).doubleValue(),
                            ((Number) argument.get(ORDER)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1],
                            (double[][]) dataObject[2]);
                }
                else if (dataLengthIndex == 3)
                {
                    minimizer = minimizer(
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            ((Number) argument.get(DEGREE)).doubleValue(),
                            ((Number) argument.get(ORDER)).doubleValue(),
                            (double[]) dataObject[0], doubleCovariate);
                }
                else if (dataLengthIndex == 4)
                {
                    minimizer = minimizer(
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
            else if (argument.get(DIVISIONS) != null)
            {
                if (dataLengthIndex == 1)
                {
                    minimizer = minimizer(
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1], doubleCovariate);
                }
                else if (dataLengthIndex == 2)
                {
                    minimizer = minimizer(
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            (double[][]) dataObject[0],
                            (double[]) dataObject[1],
                            (double[][]) dataObject[2]);
                }
                else if (dataLengthIndex == 3)
                {
                    minimizer = minimizer(
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            (double[]) dataObject[0], doubleCovariate);
                }
                else if (dataLengthIndex == 4)
                {
                    minimizer = minimizer(
                            ((Number) argument.get(DIVISIONS)).doubleValue(),
                            (double[]) dataObject[0],
                            (double[][]) dataObject[1]);
                }
                else
                {
                    throw new IllegalArgumentException("Wrong input data.");
                }
            }
        }
        else
        {
            throw new IllegalArgumentException(
                    "Wrong input argument(s) or data.");
        }

        return minimizer;
    }

    /**
     * Obtains the minimizer of the weighted selection criterion.
     * @param selectionCriterion the enum in the class SelectionCriterion.
     * @param psiFunction the enum in the class PsiFunction.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @param order the order of the penalty.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the minimizer.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double minimizer(SelectionCriterion selectionCriterion,
                            PsiFunction psiFunction,
                            double divisions,
                            double degree,
                            double order,
                            double[][] weightMatrix,
                            double[] response,
                            double[] ...covariate)
    {
        this.selectionCriterion = selectionCriterion;
        this.psiFunction = psiFunction;
        this.divisions = divisions;
        this.degree = degree;
        this.order = order;
        this.weightMatrix = weightMatrix;
        this.response = response;
        this.covariate = covariate;
        minimizer = 0;
        objFunction = 999;
        while (Math.abs(objFunction) > 1e-6)
        {
            y = new Matrix(response, response.length);
            hatMatrix = hatMatrix(minimizer, divisions, degree, order,
                                  weightMatrix, covariate);

            WRSS = ((y.minus(H.times(y))).transpose().times(W).times(y.minus(H.
                    times(y)))).get(0, 0);
            A = ((B.transpose().times(W).times(B)).plus(((D.transpose()).
                    times(minimizer)).times(D))).inverse();
            dA = A.times(D.transpose().times(D)).times(A).times(-1.0);
            dH = (B.times(A).times(D.transpose().times(D)).times(A).times(B.
                    transpose())).times(W).times(-1.0);
            ddH = B.times(dA).times(D.transpose().times(D)).times(A).times(B.
                    transpose()).times(W).times(-2.0);
            switch (psiFunction)
            {
                case SAMPLE_SIZE:
                    mu = H.trace() / response.length;
                    dmu = dH.trace() / response.length;
                    ddmu = ddH.trace() / response.length;
                    break;
                case TRACE:
                    mu = H.trace() / W.trace();
                    dmu = dH.trace() / W.trace();
                    ddmu = ddH.trace() / W.trace();
                    break;
                case PCA_NUMBER:
                    pcaVariance = new EigenvalueDecomposition(W).
                                  getRealEigenvalues();
                    new DataManager().dataSort(pcaVariance);
                    psi = weightMatrix.length -
                          new BasicStatistics().pcaNumber(pcaVariance, 0.9);
                    mu = H.trace() / psi;
                    dmu = dH.trace() / psi;
                    ddmu = ddH.trace() / psi;
                    break;
                default:
                    throw new IllegalArgumentException
                            ("No input psi function.");
            }
            dWRSS = ((((dH.times(y)).transpose()).times(W).times(y.minus(H.
                    times(y)))).times(-2.0)).get(0, 0);
            ddWRSS = ((((ddH.times(y)).transpose()).times(W).times(y.minus(H.
                    times(y))).minus(
                            ((dH.times(y)).transpose()).times(W).times((dH.
                    times(y))))).times(-2.0)).get(0, 0);
            switch (selectionCriterion)
            {
                case GCV:
                    phi = Math.pow((1.0 - mu), 2.0);
                    dphi = -2.0 * (1.0 - mu) * dmu;
                    ddphi = 2.0 * (Math.pow(dmu, 2.0) + dmu * ddmu - ddmu);
                    break;
                case AIC:
                    phi = Math.exp( -2.0 * mu);
                    dphi = Math.exp( -2.0 * mu) * ( -2.0 * dmu);
                    ddphi = 2 * Math.exp( -2.0 * mu) *
                            (2 * Math.pow(dmu, 2.0) - ddmu);
                    break;
                case T:
                    phi = (1 - 2.0 * mu);
                    dphi = -2.0 * dmu;
                    ddphi = -2.0 * ddmu;
                    break;
                case FPE:
                    phi = (1.0 - mu) / (1.0 + mu);
                    dphi = ( -2.0 * dmu) / Math.pow(1.0 + mu, 2.0);
                    ddphi = (4 * Math.pow(dmu, 2.0) - 2.0 * ddmu * (1 + mu)) /
                            Math.pow(1.0 + mu, 3.0);
                    break;
                case nS:
                    phi = 1.0 / (1.0 + 2.0 * mu);
                    dphi = ( -2.0 * dmu) / Math.pow(1.0 + 2.0 * mu, 2.0);
                    ddphi = (8.0 * Math.pow(dmu, 2.0) -
                             2.0 * ddmu * (1.0 + 2.0 * mu)) /
                            Math.pow(1.0 + 2.0 * mu, 3.0);
                    break;
                case U:
                    phi = ((1.0 - mu) *
                           (response.length - 1 - response.length * mu)) /
                          (response.length - 1);
                    dphi = (dmu * (2 * response.length * mu -
                                   2 * response.length + 1)) /
                           (response.length - 1);
                    ddphi = (ddmu * (2 * response.length * mu -
                                     2 * response.length + 1) +
                             2.0 * response.length * Math.pow(dmu, 2.0)) /
                            (response.length - 1);
                    break;
                default:
                    throw new IllegalArgumentException(
                        "No input selection criterion.");
            }
            dObjFunction = ddWRSS / phi -
                           (2 * dWRSS * dphi) / Math.pow(phi, 2.0) +
                           (WRSS * (2 * Math.pow(dphi, 2.0) - ddphi * phi)) /
                           Math.pow(phi, 3.0);
            objFunction = dWRSS / phi - (WRSS * dphi) / Math.pow(phi, 2.0);
            minimizer = minimizer - (objFunction / dObjFunction);
        }
        output.put(Output.MINIMIZER, minimizer);

        return minimizer;
    }

    /**
     * Obtains the minimizer of the weighted selection criterion with default
     * psi value equal to the retained number of principal components of the
     * weight matrix.
     * @param selectionCriterion the enum in the class SelectionCriterion.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @param order the order of the penalty.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the minimizer.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double minimizer(SelectionCriterion selectionCriterion,
                            double divisions,
                            double degree,
                            double order,
                            double[][] weightMatrix,
                            double[] response,
                            double[] ...covariate)
    {
        return minimizer(selectionCriterion, PsiFunction.PCA_NUMBER, divisions,
                         degree, order, weightMatrix, response, covariate);
    }

    /**
     * Obtains the minimizer of the weighted GCV with default psi value equal to
     * the retained number of principal components of the weight matrix.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @param order the order of the penalty.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the minimizer.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double minimizer(double divisions,
                            double degree,
                            double order,
                            double[][] weightMatrix,
                            double[] response,
                            double[] ...covariate)
    {
        return minimizer(SelectionCriterion.GCV, PsiFunction.PCA_NUMBER,
                         divisions, degree, order,
                         weightMatrix, response, covariate);
    }

    /**
     * Obtains the minimizer of the weighted GCV with default psi value equal to
     * the retained number of principal components of the weight matrix for the
     * cubic smoothing spline based on the second order difference matrix.
     * @param divisions the number of intervals on the x-domain.
     * @param weightMatrix the weight matrix.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the minimizer.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double minimizer(double divisions,
                            double[][] weightMatrix,
                            double[] response,
                            double[] ...covariate)
    {
        return minimizer(SelectionCriterion.GCV, PsiFunction.PCA_NUMBER,
                         divisions, 3.0, 2.0,
                         weightMatrix, response, covariate);
    }

    /**
     * Obtains the minimizer of GCV with the weight matrix equal to the identity
     * matrix.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @param order the order of the penalty.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the minimizer.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double minimizer(double divisions,
                            double degree,
                            double order,
                            double[] response,
                            double[] ...covariate)
    {
        return minimizer(SelectionCriterion.GCV, PsiFunction.SAMPLE_SIZE,
                         divisions, degree, order,
                         new javastat.util.DataManager().
                         identity(response.length), response, covariate);
    }

    /**
     * Obtains the minimizer of GCV with the weight matrix equal to the identity
     * matrix for the cubic smoothing spline based on the second order
     * difference matrix.
     * @param divisions the number of intervals on the x-domain.
     * @param response the responses.
     * @param covariate the values of the covariates (excluding the one
     *                  corresponding to intercept),
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the minimizer.
     * @exception IllegalArgumentException all rows of the covariates must have
     *                                     the same length.
     */

    public double minimizer(double divisions,
                            double[] response,
                            double[] ...covariate)
    {
        return minimizer(SelectionCriterion.GCV, PsiFunction.SAMPLE_SIZE,
                         divisions, 3.0, 2.0,
                         new javastat.util.DataManager().
                         identity(response.length), response, covariate);
    }

}
