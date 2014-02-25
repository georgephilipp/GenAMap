package javastat.regression.nonparametric;

/**
 * <p>Title: javastat</p>
 * <p>Description: JAVA programs for statistical computations</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Tung Hai University</p>
 * @author Hsu Hui Chan and Wen Hsiang Wei
 * @version 1.4
 */

import java.util.*;

import javastat.*;
import static javastat.util.Argument.*;
import static javastat.util.Output.*;
import javastat.util.*;

/**
 *
 * <p>Obtains the spline regression design matrix.</p>
 * <p> </p>
 * <br> Example:
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
 * <br> double[][] basis = new BSplineBasis(10, ethanolx).basis;
 * <br>
 * <br> // Null constructor
 * <br> basis = new BSplineBasis().basis(10, ethanolx);
 * <br>
 * <br> // Non-null constructor
 * <br> Hashtable argument1 = new Hashtable();
 * <br> argument1.put(DIVISIONS, 10);
 * <br> StatisticalAnalysis testclass1 = new BSplineBasis(
 * <br> &nbsp;&nbsp;&nbsp;
 *        argument1, ethanolx).statisticalAnalysis;
 * <br> basis = (double[][]) testclass1.output.get(BASIS);
 * <br>
 * <br> // Null constructor
 * <br> Hashtable argument2 = new Hashtable();
 * <br> BSplineBasis testclass2 = new BSplineBasis(argument2, null);
 * <br> argument2.put(DIVISIONS, 10);
 * <br> basis = testclass2.basis(argument2, ethanolx);
 * <br>
 * <br> // Obtains the information about the output
 * <br> out.println(testclass1.output.toString());
 * <br> out.println(testclass2.output.toString());
 */

public class BSplineBasis extends StatisticalAnalysis
{

    /**
     * The object for the B-spline basis.
     */

    public StatisticalAnalysis statisticalAnalysis;

    /**
     * The spline regression design matrix.
     */

    public double[][] basis;

    /**
     * The difference matrix.
     */

    public double[][] difference;

    /**
     * The input data.
     */

    public double[] data;

    /**
     * The input coefficients to be differenced.
     */

    public double[][] coefficients;

    /**
     * The number of intervals on the x-domain.
     */

    public double divisions;

    /**
     * The degree of the piecewise polynomial.
     */

    public double degree;

    /**
     * The number of the differences to be done.
     */

    public double order;

    /**
     * The class contains the collections of some basic methods for manipulating
     * the data.
     */

    private DataManager dataManager;

    /**
     * The distance between knots.
     */

    private double dx;

    /**
     * The left of x-domain.
     */

    private double xl;

    /**
     * The left of x-domain.
     */

    private double rowSum;

    /**
     * The vector used for compuing spline regression design matrix.
     */

    private double[] t;

    /**
     * The vector used for compuing spline regression design matrix.
     */

    private double[] P;

    /**
     * The vector used for compuing spline regression design matrix.
     */

    private double[] B;

    /**
     * The difference matrix.
     */

    private double[][] coefficientClone;

    /**
     * The first element of B vector.
     */

    private double B0;

    /**
     * Default BSplineBasis constructor.
     */

    public BSplineBasis() {}

    /**
     * Obtains the spline regression design matrix given the input arguments and
     * data.
     * @param argument the arguments with the following choices,
     * <br> DIVISIONS, DEGREE: complete list of arguments;
     * <br> DIVISIONS: the cubic spline being used;
     * <br> empty argument: default constructor.
     * <br><br>
     * @param dataObject the input data.
     * @exception IllegalArgumentException wrong input arguments or data.
     */

    public BSplineBasis(Hashtable argument,
                        Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (argument.size() > 0 &&
            dataObject != null)
        {
            if (argument.get(DIVISIONS) != null &&
                argument.get(DEGREE) != null)
            {
                statisticalAnalysis = new BSplineBasis(((Number) argument.get(
                        DIVISIONS)).doubleValue(),
                        ((Number) argument.get(DEGREE)).doubleValue(),
                        (double[]) dataObject[0]);
            }
            else if (argument.get(DIVISIONS) != null)
            {
                statisticalAnalysis = new BSplineBasis(((Number) argument.get(
                        DIVISIONS)).doubleValue(),
                        (double[]) dataObject[0]);
            }
            else
            {
                throw new IllegalArgumentException("Wrong input data.");
            }
        }
        else if (dataObject == null)
        {
            statisticalAnalysis = new BSplineBasis();
        }
        else
        {
            throw new IllegalArgumentException(
                    "Wrong input argument(s) or data.");
        }
    }

    /**
     * Obtains the spline regression design matrix.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @param data the input data.
     */

    public BSplineBasis(double divisions,
                        double degree,
                        double[] data)
    {
        this.divisions = divisions;
        this.degree = degree;
        this.data = data;
        basis = basis(divisions, degree, data);
    }

    /**
     * Obtains the cubic spline regression design matrix.
     * @param divisions the number of intervals on the x-domain.
     * @param data the input data.
     */

    public BSplineBasis(double divisions,
                        double[] data)
    {
        basis = basis(divisions, 3.0, data);
    }

    /**
     * Returns the spline regression design matrix.
     * @param argument the arguments with the following choices,
     * <br> DIVISIONS, DEGREE: complete list of arguments;
     * <br> DIVISIONS: the cubic spline being used;
     * <br><br>
     * @param dataObject the input data.
     * @return the spline regression design matrix.
     * @exception IllegalArgumentException wrong input arguments or data.
     */

    public double[][] basis(Hashtable argument,
                            Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (argument.size() > 0 &&
            dataObject != null)
        {
            if (argument.get(DIVISIONS) != null &&
                argument.get(DEGREE) != null)
            {
                basis = basis(((Number) argument.get(DIVISIONS)).doubleValue(),
                              ((Number) argument.get(DEGREE)).doubleValue(),
                              (double[]) dataObject[0]);
            }
            else if (argument.get(DIVISIONS) != null)
            {
                basis = basis(((Number) argument.get(DIVISIONS)).doubleValue(),
                              (double[]) dataObject[0]);
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

        return basis;
    }

    /**
     * Returns the spline regression design matrix.
     * @param data the input data.
     * @param divisions the number of intervals on the x-domain.
     * @param degree the degree of the piecewise polynomial.
     * @return the spline regression design matrix.
     */

    public double[][] basis(double divisions,
                            double degree,
                            double[] data)
    {
        this.divisions = divisions;
        this.degree = degree;
        this.data = data;
        int m = data.length;
        int n = (int) (divisions + degree);
        dataManager = new DataManager();
        basis = new double[m][n];
        xl = dataManager.cummin(data)[m - 1];
        dx = (dataManager.cummax(data)[m - 1] - xl) / divisions;
        t = new double[n];
        P = new double[n];
        B = new double[n];
        for (int i = 0; i < m; i++)
        {
            rowSum = 0;
            for (int j = 0; j < n; j++)
            {
                t[j] = xl + (dx * (j - degree));
                P[j] = (data[i] - t[j]) / dx;
                if ((t[j] <= data[i]) & (data[i] < (t[j] + dx)))
                {
                    B[j] = 1;
                }
                else
                {
                    B[j] = 0;
                }
            }
            for (int k = 1; k <= degree; k++)
            {
                B0 = B[0];
                for (int j = 0; j < (n - 1); j++)
                {
                    B[j] = (P[j] * B[j] + (k + 1 - P[j]) * B[j + 1]) / k;
                }
                B[n - 1] = (P[n - 1] * B[n - 1] + (k + 1 - P[n - 1]) * B0) / k;
            }
            for (int j = 0; j < n; j++)
            {
                rowSum += B[j];
            }
            if (rowSum != 0)
            {
                for (int j = 0; j < n; j++)
                {
                    basis[i][j] = B[j];
                }
            }
            else
            {
                for (int j = 0; j < n; j++)
                {
                    basis[i][j] = basis[0][n - j - 1];
                }
            }
        }
        output.put(BASIS, basis);

        return basis;
    }

    /**
     * Returns the cubic spline regression design matrix.
     * @param data the input data.
     * @param divisions the number of intervals on the x-domain.
     * @return the spline regression design matrix.
     */

    public double[][] basis(double divisions,
                            double[] data)
    {
        return basis(divisions, 3, data);
    }

    /**
     * Returns the difference matrix.
     * @param argument the arguments with the following choice,
     * <br> ORDER: the number of the differences to be done.
     * <br><br>
     * @param dataObject the input coefficients to be differenced.
     * @return the difference matrix.
     * @exception IllegalArgumentException wrong input arguments or data.
     */

    public double[][] difference(Hashtable argument,
                                 Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (argument.size() > 0 &&
            dataObject != null)
        {
            if (argument.get(ORDER) != null)
            {
                difference = difference(((Number) argument.get(ORDER)).
                                        doubleValue(),
                                        (double[]) dataObject[0]);
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

        return difference;
    }

    /**
     * Returns the difference matrix.
     * @param coefficients the input coefficients to be differenced.
     * @param order the number of the differences to be done.
     * @return the difference matrix.
     */

    public double[][] difference(double order,
                                 double[] ...coefficients)
    {
        this.order = order;
        this.coefficients = coefficients;
        coefficientClone = coefficients;
        for (int i = 1; i <= order; i++)
        {
            for (int j = 0; j < (coefficients.length - i); j++)
            {
                for (int k = 0; k < coefficients[0].length; k++)
                {
                    coefficientClone[j][k] = coefficientClone[j + 1][k] -
                                             coefficientClone[j][k];
                }
            }
        }
        difference = new double[coefficients.length -
                     (int) order][coefficients[0].length];
        for (int j = 0; j < (coefficients.length - order); j++)
        {
            for (int k = 0; k < coefficients[0].length; k++)
            {
                difference[j][k] = coefficientClone[j][k];
            }
        }
        output.put(DIFFERENCE, difference);

        return difference;
    }

    /**
     * Returns the second order difference matrix.
     * @param coefficients the input coefficients to be differenced.
     * @return the difference matrix.
     */

    public double[][] difference(double[] ...coefficients)
    {
        return difference(2, coefficients);
    }

}
