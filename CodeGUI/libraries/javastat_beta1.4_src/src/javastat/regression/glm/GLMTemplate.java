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
import static javastat.util.Argument.*;
import static javastat.util.Output.*;
import javastat.util.*;

import JSci.maths.statistics.*;

import Jama.*;

/**
 *
 * The class provides basic methods for fitting generalized linear models.</p>
 */

public abstract class GLMTemplate extends StatisticalAnalysis
{

    /**
     * The response.
     */

    public double[] response;

    /**
     * The values of the covariates (excluding the one corresponding to the
     * intercept),
     * <br> covariate[j]: the (j+1)'th covariate vector.
     */

    public double[][] covariate;

    /**
     * The parameter estimates,
     * <br> coefficients[j]: the parameter estiamte corresponding to the
     *                       (j+1)'th covariate.
     */

    public double[] coefficients;

    /**
     * The standard errors of the parameter estimates,
     * <br> coefficientSE[j]: the standard error of the (j+1)'th parameter
     *                        estimate.
     */

    public double[] coefficientSE;

    /**
     * The level of significance.
     */

    public double alpha;

    /**
     * The confidence intervals for the estimated coefficients,
     * <br> confidenceInterval[j][0]: the lower bound for the (j+1)'th estimated
     *                                coefficient;
     * <br> confidenceInterval[j][1]: the upper bound for the (j+1)'th estimated
     *                                coefficient.
     */

    public double[][] confidenceInterval;

    /**
     * The z statistics for individual coefficients,
     * <br> testStstatistic[j]=coefficients[j]/Math.sqrt(variance[j][j]):
     *      the z statistic corresponding to the (j+1)'th coefficient estimate.
     */

    public double[] testStatistic;

    /**
     * The p values for the z tests,
     * <br> pValue[j]: the p value corresponding to the (j+1)'th parameter.
     */

    public double[] pValue;

    /**
     * The linear predictor values,
     * <br> linearPredictor[i]: the value corresponding to the (i+1)'th
     *                          observation.
     */

    public double[] linearPredictors;

    /**
     * The correlation matrix of the estimated coefficients,
     * <br> correlation[i][j]: the correlation of the (i+1)'th and the (j+1)'th
     *                         estiamted coefficients.
     */

    public double[][] correlation;

    /**
     * The variance-covariance matrix of the estimated coefficients,
     * <br> variance[i][j]: the covariance of the (i+1)'th and the (j+1)'th
     *                      estiamted coefficients.
     */

    public double[][] variance;

    /**
     * The fitted values.
     */

    public double[] fittedValues;

    /**
     * The deviance residuals.
     */

    public double[] devianceResiduals;

    /**
     * The Pearson residuals.
     */

    public double[] pearsonResiduals;

    /**
     * The response residuals.
     */

    public double[] responseResiduals;

    /**
     * The weight matrix.
     */

    public double[][] weights;

    /**
     * The means of the responses.
     */

    public double[] means;

    /**
     * The variances of the responses.
     */

    public double[] responseVariance;

    /**
     * The deviance table,
     * <br>  devianceTable[j][0]: the degree of freedom corresponding to the
     *                            j'th factor.
     * <br>  devianceTable[j][1]: the deviance difference.
     * <br>  devianceTable[j][2]: the degree of freedom.
     * <br>  devianceTable[j][3]: the deviance.
     */

    public double[][] devianceTable;

    /**
     * The link function.
     */

    public LinkFunction link;

    /**
     * The double array with the values of (XWX).
     */

    private double[][] xwx;

    /**
     * The inverse of the weight matrix.
     */

    private double[][] inversedWeights;

    /**
     * The error.
     */

    private double error;

    /**
     * The response matrix.
     */

    private Matrix responseMatrix;

    /**
     * The covariate matrix.
     */

    private Matrix covariateMatrix;

    /**
     * The coefficient matrix.
     */

    private Matrix coefficientMatrix;

    /**
     * The matrix with linear predictor values.
     */

    private Matrix linearPredictorMatrix;

    /**
     * The weight matrix.
     */

    private Matrix weightMatrix;

    /**
     * The z matrix.
     */

    private Matrix zMatrix;

    /**
     * The (XWX) matrix.
     */

    private Matrix xwxMatrix;

    /**
     * The updated coefficient matrix.
     */

    private Matrix updatedCoefficientMatrix;

    /**
     * The object represents a normal distribution.
     */

    private NormalDistribution normalDistribution;

    /**
     * The constant such that the upper-tailed probability of the normal
     * random variable is equal to alpha/2.
     */

    private double zAlpha;

    /**
     * Default GLMTemplate constructor.
     */

    public GLMTemplate() {}

    /**
     * The IRLS estimate.
     * @param argument the empty argument.
     * @param dataObject the input responses and values of the covariates.
     * @return the estimated coefficients.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    protected double[] coefficients(Hashtable argument,
                                    Object ...dataObject)
    {
        return coefficients(dataObject);
    }

    /**
     * The IRLS estimate.
     * @param dataObject the input responses and values of the covariates,
     *                   for example, coefficients(response,covariate).
     * @return the estimated coefficients.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    protected double[] coefficients(Object ...dataObject)
    {
        response = (double[]) dataObject[0];
        covariate = (double[][]) dataObject[1];
        BasicStatistics.convergenceCriterion = new double[]{0.000001};
        new javastat.util.DataManager().checkDimension(covariate);
        if (response.length != covariate[0].length)
        {
            throw new IllegalArgumentException(
                    "The response vector and rows of the covariate matrix " +
                    "must have the same length.");
        }
        coefficients = new GLMDataManager().setInitialEstimate(covariate);
        covariateMatrix = new Matrix(covariate);
        responseMatrix = new Matrix(response, response.length);
        error = 1.0;
        while (error > BasicStatistics.convergenceCriterion[0])
        {
            coefficientMatrix = new Matrix(coefficients, coefficients.length);
            weights = weights(coefficients, covariate);
            inversedWeights = new double[weights.length][weights.length];
            linearPredictors = linearPredictors(covariate, coefficients);
            means = means(coefficients, covariate);
            weightMatrix = new Matrix(weights);
            for (int i = 0; i < weights.length; i++)
            {
                if (weights[i][i] == 0)
                {
                    inversedWeights[i][i] = 0;
                }
                else
                {
                    inversedWeights[i][i] = 1 / weights[i][i];
                }
            }
            linearPredictorMatrix = covariateMatrix.transpose().times(
                    coefficientMatrix);
            zMatrix = linearPredictorMatrix.plus(
                    new Matrix(inversedWeights).times(responseMatrix.minus(
                            new Matrix(means, means.length))));
            xwxMatrix = covariateMatrix.times(weightMatrix).times(
                    covariateMatrix.transpose());
            if (Math.abs(xwxMatrix.det()) <= 1e-8)
            {
                xwx = xwxMatrix.getArray();
                for (int i = 0; i < xwx.length; i++)
                {
                    xwx[i][i] = xwx[i][i] + 0.1;
                }
                xwxMatrix = new Matrix(xwx);
            }
            updatedCoefficientMatrix = xwxMatrix.inverse().times(
                    covariateMatrix.times(weightMatrix)).times(zMatrix);
            coefficients = updatedCoefficientMatrix.getColumnPackedCopy();
            error = Math.pow(updatedCoefficientMatrix.minus(
                    coefficientMatrix).normF(), 2.0);
        }
        output.put(COEFFICIENTS, coefficients);

        return coefficients;
    }

    /**
     * The linear predictor values.
     * @param argument the empty argument.
     * @param dataObject the input responses and values of the covariates.
     * @return the linear predictor values.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] linearPredictors(Hashtable argument,
                                     Object ...dataObject)
    {
        if (dataObject[1].getClass().getName().equalsIgnoreCase("[[D"))
        {
            return linearPredictors((double[]) dataObject[0],
                                    (double[][]) dataObject[1]);
        }
        else
        {
            return linearPredictors((double[][]) dataObject[0],
                                    (double[]) dataObject[1]);
        }
    }

    /**
     * The linear predictor values.
     * @param response the responses.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the linear predictor values.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] linearPredictors(double[] response,
                                     double[] ...covariate)
    {
        coefficients = coefficients(response, covariate);
        coefficientMatrix = new Matrix(coefficients, coefficients.length);
        linearPredictors = covariateMatrix.transpose().times(coefficientMatrix).
                           getColumnPackedCopy();
        output.put(LINEAR_PREDICTORS, linearPredictors);

        return linearPredictors;
    }

    /**
     * The linear predictor values.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @param coefficients the parameter estimates,
     * <br> coefficients[j]: the parameter estiamte corresponding to the
     *                       (j+1)'th covariate.
     * @return the linear predictor values.
     */

    public double[] linearPredictors(double[][] covariate,
                                     double[] coefficients)
    {
        linearPredictors = new Matrix(covariate).transpose().times(
                new Matrix(coefficients, coefficients.length)).
                           getColumnPackedCopy();

        return linearPredictors;
    }

    /**
     * The confidence intervals for the estimated coefficients.
     * @param argument the empty argument.
     * @param dataObject the input responses and values of the covariates.
     * @return the confidence interval for the estimated coefficients,
     * <br>    confidenceInterval[j][0]: the lower bound for the (j+1)'th
     *                                   estimated coefficient,
     * <br>    confidenceInterval[j][1]: the upper bound for the (j+1)'th
     *                                   estimated coefficient.
     * @exception IllegalArgumentException the level of significance should be
     *                              (strictly) positive and not greater than 1.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[][] confidenceInterval(Hashtable argument,
                                         Object ...dataObject)
    {
        this.alpha = (Double) argument.get(ALPHA);
        if ((this.alpha <= 0.0) || (this.alpha > 1))
        {
            throw new IllegalArgumentException(
                    "The level of significance should be (strictly) positive " +
                    "and not greater than 1.");
        }
        response = (double[]) dataObject[0];
        covariate = (double[][]) dataObject[1];
        testStatistic = testStatistic(response, covariate);
        zAlpha = new NormalDistribution().inverse((1 - this.alpha / 2));
        confidenceInterval = new double[testStatistic.length][2];
        for (int i = 0; i < testStatistic.length; i++)
        {
            confidenceInterval[i][0] = coefficients[i] -
                                       zAlpha * Math.sqrt(variance[i][i]);
            confidenceInterval[i][1] = coefficients[i] +
                                       zAlpha * Math.sqrt(variance[i][i]);
        }
        output.put(CONFIDENCE_INTERVAL, confidenceInterval);

        return confidenceInterval;

    }

    /**
     * The z statistics for individual coefficients.
     * @param argument the empty argument.
     * @param dataObject the input responses and values of the covariates.
     * @return the values of the z statistics.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    protected double[] testStatistic(Hashtable argument,
                                     Object ...dataObject)
    {
        return testStatistic(dataObject);
    }

    /**
     * The z statistics for individual coefficients.
     * @param dataObject the input responses and values of the covariates,
     *                   for example, testStatistic(response,covariate).
     * @return the values of the t statistics.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    protected double[] testStatistic(Object ...dataObject)
    {
        response = (double[]) dataObject[0];
        covariate = (double[][]) dataObject[1];
        coefficients = coefficients(response, covariate);
        weights = weights(coefficients, covariate);
        weightMatrix = new Matrix(weights);
        xwxMatrix = covariateMatrix.times(weightMatrix).times(
                covariateMatrix.transpose());
        variance = xwxMatrix.inverse().getArray();
        coefficientSE = new double[variance.length];
        for (int i = 0; i < variance.length; i++)
        {
            coefficientSE[i] = Math.sqrt(variance[i][i]);
        }
        correlation = new double[variance.length][variance.length];
        for (int i = 0; i < variance.length; i++)
        {
            for (int j = i; j < variance.length; j++)
            {
                correlation[i][j] = variance[i][j] /
                                    Math.sqrt(variance[i][i] * variance[j][j]);
                correlation[j][i] = correlation[i][j];
            }
        }
        testStatistic = new double[coefficients.length];
        for (int i = 0; i < testStatistic.length; i++)
        {
            testStatistic[i] = coefficients[i] / coefficientSE[i];
        }
        output.put(TEST_STATISTIC, testStatistic);

        return testStatistic;
    }

    /**
     * The p values for the z tests.
     * @param argument the empty argument.
     * @param dataObject the input responses and values of the covariates.
     * @return the p values.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    protected double[] pValue(Hashtable argument,
                              Object ...dataObject)
    {
        return pValue(dataObject);
    }

    /**
     * The p values for the z tests.
     * @param dataObject the input responses and values of the covariates,
     *                   for example, pValue(response,covariate).
     * @return the p values.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    protected double[] pValue(Object ...dataObject)
    {
        response = (double[]) dataObject[0];
        covariate = (double[][]) dataObject[1];
        testStatistic = testStatistic(response, covariate);
        linearPredictors = linearPredictors(covariate, coefficients);
        normalDistribution = new NormalDistribution();
        pValue = new double[testStatistic.length];
        for (int j = 0; j < testStatistic.length; j++)
        {
            pValue[j] = 2 * (1 -
                normalDistribution.cumulative(Math.abs(testStatistic[j])));
        }
        output.put(PVALUE, pValue);

        return pValue;
    }

    /**
     * The response residuals.
     * @param argument the empty argument.
     * @param dataObject the input responses and values of the covariates.
     * @return the response residuals.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    protected double[] responseResiduals(Hashtable argument,
                                         Object ...dataObject)
    {
        return responseResiduals((double[]) dataObject[0],
                                 (double[][]) dataObject[1]);
    }

    /**
     * The response residuals.
     * @param response the responses.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the response residuals.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    protected double[] responseResiduals(double[] response,
                                         double[] ...covariate)
    {
        coefficients = coefficients(response, covariate);
        means = means(coefficients, covariate);
        responseResiduals = new double[response.length];
        for (int i = 0; i < response.length; i++)
        {
            responseResiduals[i] = response[i] - means[i];
        }
        output.put(RESPONSE_RESIDUALS, responseResiduals);

        return responseResiduals;
    }

    /**
     * The Pearson residuals.
     * @param argument the empty argument.
     * @param dataObject the input responses, values of the covariates and
     *                   variances of the response.
     * @return the Pearson residuals.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    protected double[] pearsonResiduals(Hashtable argument,
                                        Object ...dataObject)
    {
        return pearsonResiduals((double[]) dataObject[0],
                                (double[]) dataObject[1],
                                (double[][]) dataObject[2]);
    }

    /**
     * The Pearson residuals.
     * @param responseVariance the variances of the responses,
     * <br>                    responseVariance[j]: the variance of the (j+1)'th
     *                         response.
     * @param response the responses.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the Pearson residuals.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    protected double[] pearsonResiduals(double[] responseVariance,
                                        double[] response,
                                        double[] ...covariate)
    {
        responseResiduals = responseResiduals(response, covariate);
        pearsonResiduals = new double[response.length];
        for (int i = 0; i < response.length; i++)
        {
            if(responseVariance[i] == 0)
            {
                pearsonResiduals[i] = 0.0;
            }
            else
            {
                pearsonResiduals[i] = responseResiduals[i] /
                                      Math.sqrt(responseVariance[i]);
            }
        }

        return pearsonResiduals;
    }

    /**
     * The weight function.
     * @param coefficients the parameter estimates,
     * <br>                coefficients[j]: the parameter estiamte corresponding
     *                     to the (j+1)'th covariate.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the weights.
     */

    protected abstract double[][] weights(double[] coefficients,
                                          double[] ...covariate);

    /**
     * The means of the responses.
     * @param coefficients the parameter estimates,
     * <br>                coefficients[j]: the parameter estiamte corresponding
     *                     to the (j+1)'th covariate.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the means of the responses.
     */

    protected abstract double[] means(double[] coefficients,
                                      double[] ...covariate);

    /**
     * The means of the responses.
     * @param argument the empty argument.
     * @param dataObject the input values of the covariates, parameter estimates
     *                   and link.
     * @return the means of the responses.
     */

    protected double[] means(Hashtable argument,
                             Object ...dataObject)
    {
        return means((LinkFunction) dataObject[2], (double[]) dataObject[1],
                     (double[][]) dataObject[0]);
    }

    /**
     * The means of the responses.
     * @param link the link.
     * @param coefficients the parameter estimates,
     * <br>                coefficients[j]: the parameter estiamte corresponding
     *                     to the (j+1)'th covariate.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the means of the responses.
     * @exception IllegalArgumentException wrong input link function.
     */

    protected double[] means(LinkFunction link,
                             double[] coefficients,
                             double[] ...covariate)
    {
        linearPredictors = linearPredictors(covariate, coefficients);
        means = new double[linearPredictors.length];
        switch (link)
        {
            case IDENTITY:
                for (int i = 0; i < means.length; i++)
                {
                    means[i] = linearPredictors[i];
                }
                break;
            case LOG:
                for (int i = 0; i < means.length; i++)
                {
                    means[i] = Math.exp(linearPredictors[i]);
                }
                break;
            case INVERSE:
                for (int i = 0; i < means.length; i++)
                {
                    means[i] = 1 / linearPredictors[i];
                }
                break;
            case INVERSE_SQUARE:
                for (int i = 0; i < means.length; i++)
                {
                    means[i] = Math.pow(linearPredictors[i], -1 * 0.5);
                }
                break;
            case SQUARE_ROOT:
                for (int i = 0; i < means.length; i++)
                {
                    means[i] = Math.pow(linearPredictors[i], 2.0);
                }
                break;
            case LOGIT:
                for (int i = 0; i < means.length; i++)
                {
                    means[i] = Math.exp(linearPredictors[i]) /
                               (1.0 + Math.exp(linearPredictors[i]));
                }
                break;
            case PROBIT:
                for (int i = 0; i < means.length; i++)
                {
                    means[i] = normalDistribution.inverse(linearPredictors[i]);
                }
                break;
            case COMPLEMENTARY_LOGLOG:
                for (int i = 0; i < means.length; i++)
                {
                    means[i] = 1.0 - Math.exp( -1.0 *
                                               Math.exp(linearPredictors[i]));
                }
                break;
            default:
                throw new IllegalArgumentException
                        ("Wrong input link function.");
        }
        output.put(MEANS, means);

        return means;
    }

    /**
     * The variances of the responses.
     * @param argument the empty argument.
     * @param dataObject the input means and distribution of the response..
     * @return the variances of the responses.
     */

    public double[] responseVariance(Hashtable argument,
                                     Object ...dataObject)
    {
        return responseVariance((double[]) dataObject[0],
                                (ExponentialFamily) dataObject[1]);
    }

    /**
     * The variances of the responses.
     * @param means the means of the responses.
     * @param distribution the distribution of the response.
     * @return the variances of the responses.
     * @exception IllegalArgumentException wrong input distribution function.
     */

    public double[] responseVariance(double[] means,
                                     ExponentialFamily distribution)
    {
        responseVariance = new double[means.length];
        switch (distribution)
        {
            case NORMAL:
                for (int i = 0; i < means.length; i++)
                {
                    responseVariance[i] = 1.0;
                }
                break;
            case POISSON:
                for (int i = 0; i < means.length; i++)
                {
                    responseVariance[i] = means[i];
                }
                break;
            case BINOMIAL:
                for (int i = 0; i < means.length; i++)
                {
                    responseVariance[i] = means[i] * (1.0 - means[i]);
                }
                break;
            case GAMMA:
                for (int i = 0; i < means.length; i++)
                {
                    responseVariance[i] = Math.pow(means[i], 2.0);
                }
                break;
            case INVERSE_GAUSSIAN:
                for (int i = 0; i < means.length; i++)
                {
                    responseVariance[i] = Math.pow(means[i], 3.0);
                }
                break;
            default:
                throw new IllegalArgumentException
                        ("Wrong input distribution function.");
        }
        output.put(RESPONSE_VARIANCE, responseVariance);

        return responseVariance;
    }

}
