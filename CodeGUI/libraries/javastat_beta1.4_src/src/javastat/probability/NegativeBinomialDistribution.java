package javastat.probability;

/**
 * <p>Title: javastat</p>
 * <p>Description: JAVA programs for statistical computations</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Tung Hai University</p>
 * @version 1.4
 */

import static java.lang.Math.*;

import javastat.util.*;

import JSci.maths.statistics.*;

/**
 *
 * <p>Calculates the density, probability, and percentile for a
 * negative-binomial distribution. </p>
 * <p> </p>
 * <br> Example:
 * <br> int r1 = 3;
 * <br> double p1 = 0.7;
 * <br> int r2 = 5;
 * <br> double p2 = 0.3;
 * <br> int r3 = 4;
 * <br> double p3 = 0.5;
 * <br> double negativeBinomialPercentiles = 2.0;
 * <br> double negativeBinomialX = 3.0;
 * <br> double negativeBinomialProb = 0.95;
 * <br>
 * <br> NegativeBinomialDistribution testclass1 =
 * <br> &nbsp;&nbsp;&nbsp; new NegativeBinomialDistribution();
 * <br> double cumulative = testclass1.
 * <br> &nbsp;&nbsp;&nbsp; cumulative(r1, p1, negativeBinomialPercentiles);
 * <br> double probability = testclass1.probability(r2, p2, negativeBinomialX);
 * <br> double inverse = testclass1.inverse(r3, p3, negativeBinomialProb);
 */

public class NegativeBinomialDistribution extends ProbabilityDistribution
{

    /**
     * The specified number of successes.
     */

    public int r;

    /**
     * The probability of a success.
     */

    public double p;

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
     * The class contains the collections of some basic methods used in
     * statistical computations.
     */

    private BasicStatistics basicStatistics = new BasicStatistics();

    /**
     * Default NegativeBinomialDistribution constructor.
     */

    public NegativeBinomialDistribution(){}

    /**
     * The NegativeBinomialDistribution constructor with the specified
     * parameters.
     * @param r the specified number of successes.
     * @param p the probability of a success.
     */

    public NegativeBinomialDistribution(int r,
                                        double p)
    {
        this.r = r;
        this.p = p;
    }

    /**
     * Calculates the cumulative distribution function (CDF) of a negative
     * binomial random variable
     * @param r the specified number of successes.
     * @param p the probability of a success.
     * @param percentile the percentile.
     * @return the cumulative distribution function.
     */

    public double cumulative(int r,
                             double p,
                             double percentile)
    {
        this.r = r;
        this.p = p;
        cumulative = 0;
        int x = (int) floor(percentile);
        if (x == Double.POSITIVE_INFINITY)
        {
            cumulative = 1.0;
        }
        else if (x >= 0 && (p >= 0.0 && p <= 1.0) && r >= 1)
        {
            for (int i = 0; i <= x; i++)
            {
                cumulative = (basicStatistics.combination(i + r - 1, i) *
                              pow(p, r) * pow(1.0 - p, i)) + cumulative;
            }
        }

        return cumulative;
    }

    /**
     * Calculates the cumulative distribution function (CDF) of a negative
     * binomial random variable
     * @param percentile the percentile.
     * @return the cumulative distribution function.
     */

    public double cumulative(double percentile)
    {
        return cumulative(r, p, percentile);
    }

    /**
     * Calculates the probability density for a negative binomial
     * random variable.
     * @param r the specified number of successes.
     * @param p the probability of a success.
     * @param percentile the percentile for computing the probability density.
     * @return the probability density.
     */

    public double probability(int r,
                              double p,
                              double percentile)
    {
        probability = 0;
        int numberOfFailure = (int) rint(percentile);
        if (numberOfFailure == Double.POSITIVE_INFINITY)
        {
            return probability = 0.0;
        }
        else if (numberOfFailure >= 0 && (p >= 0.0 && p <= 1.0) && r >= 1)
        {
            probability = (basicStatistics.combination(numberOfFailure + r - 1,
                    numberOfFailure) * pow(p, r) *
                           pow(1.0 - p, numberOfFailure));
        }

        return probability;
    }

    /**
     * Calculates the probability density for a negative binomial
     * random variable.
     * @param percentile the percentile for computing the probability density.
     * @return the probability density.
     */

    public double probability(double percentile)
    {
        return probability(r, p, percentile);
    }

    /**
     * Calculates the percentile for a negative binomial random variable.
     * @param r the specified number of successes.
     * @param p the probability of a success.
     * @param probabilityArgument the cumulative probability for computing the
     *                            percentile.
     * @return the percentile.
     * @exception OutOfRangeException the argument is out of range.
     */

    public double inverse(int r,
                          double p,
                          double probabilityArgument)
    {
        this.r = r;
        this.p = p;
        checkRange(probabilityArgument);
        inverse = Math.floor(findRoot(probabilityArgument, r * p, 0.0,
                                             Double.MAX_VALUE));
        while (cumulative(inverse) < probabilityArgument)
        {
            inverse += 1.0;
        }

        return inverse;
    }

    /**
     * Calculates the percentile for a negative binomial random variable.
     * @param probabilityArgument the cumulative probability for computing the
     *                            percentile.
     * @return the percentile.
     * @exception OutOfRangeException the argument is out of range.
     */

    public double inverse(double probabilityArgument)
    {
        return inverse(r, p, probabilityArgument);
    }

}
