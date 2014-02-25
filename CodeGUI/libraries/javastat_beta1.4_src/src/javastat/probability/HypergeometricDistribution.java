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
 * hypergeometric distribution. </p>
 * <p> </p>
 * <br> Example:
 * <br> int N = 50;
 * <br> int r = 10;
 * <br> int n = 20;
 * <br> double hypergeometricPercentiles = 4.0;
 * <br> double hypergeometricX = 1.0;
 * <br> double hypergeometricProb = 0.90;
 * <br>
 * <br> HypergeometricDistribution testclass1 =
 * <br> &nbsp;&nbsp;&nbsp; new HypergeometricDistribution(N, r, n);
 * <br> double cumulative = testclass1.cumulative(hypergeometricPercentiles);
 * <br> double probability = testclass1.probability(hypergeometricX);
 * <br> double inverse = testclass1.inverse(hypergeometricProb);
 */

public class HypergeometricDistribution extends ProbabilityDistribution
{

    /**
     * The total number of elements in two populations.
     */

    public int N;

    /**
     * The number of elements in one population.
     */

    public int r;

    /**
     * The number of elements taken from the two populations.
     */

    public int n;

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
     * Default HypergeometricDistribution constructor.
     */

    public HypergeometricDistribution(){};

    /**
     * The HypergeometricDistribution constructor with the specified parameters.
     * @param N the total number of elements in two populations.
     * @param r the number of elements in one population.
     * @param n the number of elements taken from the two populations.
     */

    public HypergeometricDistribution(int N,
                                      int r,
                                      int n)
    {
        this.N = N;
        this.r = r;
        this.n = n;
    }

    /**
     * Calculates the cumulative distribution function (CDF) of a hypergeometric
     * random variable
     * @param N the total number of elements in two populations.
     * @param r the number of elements in one population.
     * @param n the number of elements taken from the two populations.
     * @param percentile the percentile.
     * @return the cumulative distribution function.
     */

    public double cumulative(int N,
                             int r,
                             int n,
                             double percentile)
    {
        this.N = N;
        this.r = r;
        this.n = n;
        cumulative = 0;
        int x = (int) floor(percentile);
        if (x >= 0 && N >= 0)
        {
            for (int i = max(r - N + n, 0); i <= x; i++)
            {
                cumulative = (basicStatistics.combination(r, i) *
                              basicStatistics.combination(N - r, n - i)) /
                             basicStatistics.combination(N, n) + cumulative;
            }
        }

        return cumulative;
    }

    /**
     * Calculates the cumulative distribution function (CDF) of a hypergeometric
     * random variable
     * @param percentile the percentile.
     * @return the cumulative distribution function.
     */

    public double cumulative(double percentile)
    {
        return cumulative(N, r, n, percentile);
    }

    /**
     * Calculates the probability density for a hypergeometic random variable.
     * @param N the total number of elements in two populations.
     * @param r the number of elements in one population.
     * @param n the number of elements taken from the two populations.
     * @param percentile the percentile for computing the probability density.
     * @return the probability density.
     */

    public double probability(int N,
                              int r,
                              int n,
                              double percentile)
    {
        this.N = N;
        this.r = r;
        this.n = n;
        probability = 0.0;
        int numberOfBall = (int) rint(percentile);
        if (percentile >= 0 && N >= 0)
        {
            probability =
                    (basicStatistics.combination(r, numberOfBall) *
                     basicStatistics.combination(N - r, n - numberOfBall)) /
                    basicStatistics.combination(N, n);
        }

        return probability;
    }

    /**
     * Calculates the probability density for a hypergeometic random variable.
     * @param percentile the percentile for computing the probability density.
     * @return the probability density.
     */

    public double probability(double percentile)
    {
        return probability(N, r, n, percentile);
    }

    /**
     * Calculates the percentile for a hypergeometric random variable.
     * @param N the total number of elements in two populations.
     * @param r the number of elements in one population.
     * @param n the number of elements taken from the two populations.
     * @param probabilityArgument the cumulative probability for computing the
     *                            percentile.
     * @return the percentile.
     * @exception OutOfRangeException the argument is out of range.
     */

    public double inverse(int N,
                          int r,
                          int n,
                          double probabilityArgument)
    {
        this.N = N;
        this.r = r;
        this.n = n;
        checkRange(probabilityArgument);
        inverse =
            Math.floor(findRoot(probabilityArgument, n / 2.0, 0.0, n));
        while (cumulative(inverse) < probabilityArgument)
        {
            inverse += 1.0;
        }

        return inverse;
    }

    /**
     * Calculates the percentile for a hypergeometric random variable.
     * @param probabilityArgument the cumulative probability for computing the
     *                            percentile.
     * @return the percentile.
     * @exception OutOfRangeException the argument is out of range.
     */

    public double inverse(double probabilityArgument)
    {
        return inverse(N, r, n, probabilityArgument);
    }

}
