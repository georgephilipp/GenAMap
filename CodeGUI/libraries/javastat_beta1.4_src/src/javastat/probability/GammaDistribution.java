package javastat.probability;

/**
 * <p>Title: javastat</p>
 * <p>Description: JAVA programs for statistical computations</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Tung Hai University</p>
 * @version 1.4
 */

import static java.lang.Math.*;

import JSci.maths.*;
import JSci.maths.statistics.*;

/**
 *
 * <p>Calculates the density, probability, and percentile for a
 * Gamma distribution. </p>
 * <p> </p>
 * <br> Example:
 * <br> double alpha1 = 2.0;
 * <br> double beta1 = 3.0;
 * <br> double alpha2 = 3.0;
 * <br> double beta2 = 4.0;
 * <br> double alpha3 = 5.0;
 * <br> double beta3 = 7.0;
 * <br> double gammaPercentiles = 2.0;
 * <br> double gammaX = 1.0;
 * <br> double gammaProb = 0.95;
 * <br>
 * <br> GammaDistribution testclass1 = new GammaDistribution();
 * <br> double cumulative = testclass1.
 * <br> &nbsp;&nbsp;&nbsp; cumulative(alpha1, beta1, gammaPercentiles);
 * <br> double probability = testclass1.probability(alpha2, beta2, gammaX);
 * <br> double inverse = testclass1.inverse(alpha3, beta3, gammaProb);
 */

public class GammaDistribution extends ProbabilityDistribution
{

    /**
     * The location parameter.
     */

    public double alpha;

    /**
     * The scale parameter.
     */

    public double beta;

    /**
     * The cumulative distribution function (CDF).
     */

    public double cumulative;

    /**
     * The probability density.
     */

    public double probability;

    /**
     * The percentile.
     */

    public double inverse;

    /**
     * Default GammaDistribution constructor.
     */

    public GammaDistribution(){};

    /**
     * The GammaDistribution constructor with the specified parameters.
     * @param alpha the location parameter.
     * @param beta the scale parameter.
     */

    public GammaDistribution(double alpha,
                             double beta)
    {
        this.alpha = alpha;
        this.beta = beta;
    }

    /**
     * Calculates the cumulative distribution function (CDF) of a Gamma random
     * variable.
     * @param alpha the location parameter.
     * @param beta the scale parameter.
     * @param percentile the percentile.
     * @return the cumulative distribution function.
     */

    public double cumulative(double alpha,
                             double beta,
                             double percentile)
    {
        this.alpha = alpha;
        this.beta = beta;
        cumulative = 0;
        double spacing = 0.00001;
        if (percentile == 0)
        {
            return cumulative = cumulative;
        }
        else
        {
            if(percentile == Double.POSITIVE_INFINITY)
            {
                return cumulative = 1.0;
            }
            else
            {
                for (double i = spacing; i <= percentile; i = i + spacing)
                {
                    cumulative += (double) (percentile /
                            ((percentile / spacing))) *
                            pow(beta, alpha) * pow(i, alpha - 1) *
                            exp(-beta * i) / SpecialMath.gamma(alpha);
                }
                return cumulative = cumulative + (double) (percentile /
                        (2 * (percentile / spacing))) *
                        pow(beta, alpha) * pow(0, alpha - 1) *
                        exp(-beta * 0) / SpecialMath.gamma(alpha);
            }
        }
    }

    /**
     * Calculates the cumulative distribution function (CDF) of a Gamma random
     * variable.
     * @param percentile the percentile.
     * @return the cumulative distribution function.
     */

    public double cumulative(double percentile)
    {
        return cumulative(alpha, beta, percentile);
    }

    /**
     * Calculates the probability density for a Gamma random variable.
     * @param alpha the location parameter.
     * @param beta the scale parameter.
     * @param percentile the percentile for computing the probability density.
     * @return the probability density.
     */

    public double probability(double alpha,
                              double beta,
                              double percentile)
    {
        this.alpha =alpha;
        this.beta = beta;
        probability = 0;
        if(percentile == Double.POSITIVE_INFINITY)
        {
            probability = 0.0;
        }
        else
        {
            probability = pow(beta, alpha) * pow(percentile, alpha - 1) *
                          exp(-beta * percentile) / SpecialMath.gamma(alpha);
        }

        return probability;
    }

    /**
     * Calculates the probability density for a Gamma random variable.
     * @param percentile the percentile for computing the probability density.
     * @return the probability density.
     */

    public double probability(double percentile)
    {
        return probability(alpha, beta, percentile);
    }


    /**
     * Calculates the percentile for a Gamma random variable.
     * @param alpha the location parameter.
     * @param beta the scale parameter.
     * @param probabilityArgument the cumulative probability for computing the
     *                            percentile.
     * @return the percentile.
     * @exception OutOfRangeException the argument is out of range.
     */

    public double inverse(double alpha,
                          double beta,
                          double probabilityArgument)
    {
        this.alpha = alpha;
        this.beta = beta;
        checkRange(probabilityArgument);
        if(probabilityArgument == 0.0)
                return 0.0;
        if(probabilityArgument == 1.0)
                return Double.MAX_VALUE;

        return inverse = findRoot(probabilityArgument, alpha / beta, 0.0,
                                  Double.MAX_VALUE);
    }

    /**
     * Calculates the percentile for a Gamma random variable.
     * @param probabilityArgument the cumulative probability for computing the
     *                            percentile.
     * @return the percentile.
     * @exception OutOfRangeException the argument is out of range.
     */

   public double inverse(double probabilityArgument)
   {
       return inverse(alpha, beta, probabilityArgument);
   }

}
