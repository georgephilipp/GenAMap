package javastat.probability;

/**
 * <p>Title: javastat</p>
 * <p>Description: JAVA programs for statistical computations</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Tung Hai University</p>
 * @version 1.4
 */

import java.util.*;

import javastat.*;
import static javastat.util.Argument.*;
import static javastat.util.Output.*;
import javastat.util.*;

import JSci.maths.statistics.*;

/**
 *
 * <p>Calculates the density, probability, and percentile for a
 * probability distribution. </p>
 * <p> </p>
 * <br> Example:
 * <br> // Normal distribution
 * <br> double[] normalPara = {1.0, 2.0};
 * <br> double[] normalPercentiles = {1 -1.96 * 2, 1 + 1.96 * 2};
 * <br> double normalX = normalPara[0];
 * <br> double normalProb = 0.90;
 * <br> Probability normal =
 * <br> &nbsp;&nbsp;&nbsp; new Probability(NORMAL, normalPara,
 *                         normalPercentiles, normalX,
 * <br> &nbsp;&nbsp;&nbsp; normalProb);
 * <br>
 * <br> // Beta distribution
 * <br> double[] betaPara = {2.0, 3.0};
 * <br> double[] betaPercentiles = {0.0, 0.5};
 * <br> double betaX = 0.5;
 * <br> double betaProb = 0.90;
 * <br> Probability beta =
 * <br> &nbsp;&nbsp;&nbsp; new Probability(BETA, betaPara, betaPercentiles,
 *                         betaX, betaProb);
 * <br>
 * <br> // Binomial distribution
 * <br> double[] binomialPara = {10.0, 0.3};
 * <br> double[] binomialPercentiles = {0.0, 5.0};
 * <br> double binomialX = 5.0;
 * <br> double binomialProb = 0.96;
 * <br> Probability binomial =
 * <br> &nbsp;&nbsp;&nbsp; new Probability(BINOMIAL, binomialPara,
 *                         binomialPercentiles,
 * <br> &nbsp;&nbsp;&nbsp; binomialX, binomialProb);
 * <br>
 * <br> // Cauchy distribution
 * <br> double[] cauchyPara = {0.0, 1.0};
 * <br> double[] cauchyPercentiles = {-2.0, 2.0};
 * <br> double cauchyX = 0.0;
 * <br> double cauchyProb = 0.90;
 * <br> Probability cauchy =
 * <br> &nbsp;&nbsp;&nbsp; new Probability(CAUCHY, cauchyPara,
 *                         cauchyPercentiles, cauchyX,
 * <br> &nbsp;&nbsp;&nbsp; cauchyProb);
 * <br>
 * <br> // Chi-Square distribution
 * <br> double[] chisquarePara = {1.0};
 * <br> double[] chisquarePercentiles = {1.0, 2.0};
 * <br> double chisquareX = 1.0;
 * <br> double chisquareProb = 0.90;
 * <br> Probability chisquare =
 * <br> &nbsp;&nbsp;&nbsp; new Probability(CHISQUARE, chisquarePara,
 *                         chisquarePercentiles,
 * <br> &nbsp;&nbsp;&nbsp; chisquareX, chisquareProb);
 * <br>
 * <br> // Exponential distribution
 * <br> double[] exponentialPara = {2.0};
 * <br> double[] exponentialPercentiles = {1.0, 2.0};
 * <br> double exponentialX = 1.0;
 * <br> double exponentialProb = 0.90;
 * <br> Probability exponential =
 * <br> &nbsp;&nbsp;&nbsp; new Probability(EXPONENTIAL, exponentialPara,
 * <br> &nbsp;&nbsp;&nbsp; exponentialPercentiles,
 * <br> &nbsp;&nbsp;&nbsp; exponentialX, exponentialProb);
 * <br>
 * <br> // F distribution
 * <br> double[] fPara = {2.0, 3.0};
 * <br> double[] fPercentiles = {0.0, 4.0};
 * <br> double fX = 7.0;
 * <br> double fProb = 0.95;
 * <br> Probability f = new Probability(F, fPara, fPercentiles, fX, fProb);
 * <br>
 * <br> // Gamma distribution
 * <br> double[] gammaPara = {2.0, 3.0};
 * <br> double[] gammaPercentiles = {0.0, 2.0};
 * <br> double gammaX = 1.0;
 * <br> double gammaProb = 0.95;
 * <br> Probability gamma = new Probability(GAMMA, gammaPara, gammaPercentiles,
 * <br> &nbsp;&nbsp;&nbsp; gammaX, gammaProb);
 * <br>
 * <br> // Geometric distribution
 * <br> double[] geometricPara = {0.6};
 * <br> double[] geometricPercentiles = {0.0, 2.0};
 * <br> double geometricX = 1.0;
 * <br> double geometricProb = 0.92;
 * <br> Probability geometric =
 * <br> &nbsp;&nbsp;&nbsp; new Probability(GEOMETRIC, geometricPara,
 * <br> &nbsp;&nbsp;&nbsp; geometricPercentiles, geometricX,
 * <br> &nbsp;&nbsp;&nbsp; geometricProb);
 * <br>
 * <br> // Hypergeometric distribution
 * <br> double[] hypergeometricPara = {50, 10, 20};
 * <br> double[] hypergeometricPercentiles = {0.0, 4.0};
 * <br> double hypergeometricX = 1.0;
 * <br> double hypergeometricProb = 0.90;
 * <br> Probability hypergeometric =
 * <br> &nbsp;&nbsp;&nbsp; new Probability(HYPERGEOMETRIC, hypergeometricPara,
 * <br> &nbsp;&nbsp;&nbsp; hypergeometricPercentiles, hypergeometricX,
 * <br> &nbsp;&nbsp;&nbsp; hypergeometricProb);
 * <br>
 * <br> // Log-Normal distribution
 * <br> double[] lognormalPara = {0.0, 1.0};
 * <br> double[] lognormalPercentiles = {0.0, 2.0};
 * <br> double lognormalX = 1.0;
 * <br> double lognormalProb = 0.95;
 * <br> Probability lognormal =
 * <br> &nbsp;&nbsp;&nbsp; new Probability(LOGNORMAL, lognormalPara,
 *                         lognormalPercentiles,
 * <br> &nbsp;&nbsp;&nbsp; lognormalX, lognormalProb);
 * <br>
 * <br> // Negative-Binomial distribution
 * <br> double[] negativeBinomialPara = {3.0, 0.7};
 * <br> double[] negativeBinomialPercentiles = {0.0, 2.0};
 * <br> double negativeBinomialX = 3.0;
 * <br> double negativeBinomialProb = 0.95;
 * <br> Probability negativeBinomial =
 * <br> &nbsp;&nbsp;&nbsp; new Probability(NEGATIVE_BINOMIAL,
 *                         negativeBinomialPara,
 * <br> &nbsp;&nbsp;&nbsp; negativeBinomialPercentiles, negativeBinomialX,
 * <br> &nbsp;&nbsp;&nbsp; negativeBinomialProb);
 * <br>
 * <br> // Pareto distribution
 * <br> double[] paretoPara = {2.0, 3.0};
 * <br> double[] paretoPercentiles = {3.0, 7.0};
 * <br> double paretoX = 4.0;
 * <br> double paretoProb = 0.9;
 * <br> Probability pareto =
 * <br> &nbsp;&nbsp;&nbsp; new Probability(PARETO, paretoPara,
 *                         paretoPercentiles,
 * <br> &nbsp;&nbsp;&nbsp; paretoX, paretoProb);
 * <br>
 * <br> // Poisson distribution
 * <br> double[] poissonPara = {5.0};
 * <br> double[] poissonPercentiles = {3.0, 7.0};
 * <br> double poissonX = 5.0;
 * <br> double poissonProb = 0.9;
 * <br> Probability poisson =
 * <br> &nbsp;&nbsp;&nbsp; new Probability(POISSON, poissonPara,
 *                         poissonPercentiles,
 * <br> &nbsp;&nbsp;&nbsp; poissonX, poissonProb);
 * <br>
 * <br> // T distribution
 * <br> double[] tPara = {9.0};
 * <br> double[] tPercentiles = {-2.0, 2.0};
 * <br> double tX = 0.0;
 * <br> double tProb = 0.95;
 * <br> Probability t =
 * <br> &nbsp;&nbsp;&nbsp; new Probability(T, tPara, tPercentiles, tX, tProb);
 * <br>
 * <br> // Weibull distribution
 * <br> double[] weibullPara = {3.0};
 * <br> double[] weibullPercentiles = {0.0, 1.0};
 * <br> double weibullX = 1.0;
 * <br> double weibullProb = 0.95;
 * <br> Probability weibull =
 * <br> &nbsp;&nbsp;&nbsp; new Probability(WEIBULL, weibullPara,
 *                         weibullPercentiles, weibullX,
 * <br> &nbsp;&nbsp;&nbsp; weibullProb);
 * <br>
 * <br> // Normal distribution
 * <br> Hashtable argument = new Hashtable();
 * <br> argument.put(DISTRIBUTION_TYPE, NORMAL);
 * <br> argument.put(DISTRIBUTION_PARAMETER, normalPara);
 * <br> StatisticalAnalysis testclass1 =
 * <br> &nbsp;&nbsp;&nbsp; new Probability(argument, normalPercentiles,
 *                         normalX, normalProb).
 * <br> &nbsp;&nbsp;&nbsp; statisticalAnalysis;
 * <br>
 * <br> // Obtains the information about the output
 * <br> out.println("\nNormal Distribution        :" +
 * <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *      testclass1.output.toString());
 */

public class Probability extends StatisticalAnalysis
{

    /**
     * The object represents a probability distribution.
     */

    public StatisticalAnalysis statisticalAnalysis;

    /**
     * The distribution of interest.
     */

    public DistributionType distribution;

    /**
     * The parameters associated with the probability distribution. The
     * parameters for different distributions are as follows:
     * <br> &nbsp;&nbsp;&nbsp;
     * Normal distribution: parameter[0]: mean,
     *                      parameter[1]: standard deviation;
     * <br> &nbsp;&nbsp;&nbsp;
     * Beta distribution: parameter[0]: degree of freedom,
     *                    parameter[1]: degreee of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Binomial distribution: parameter[0]: number of trials,
     *                        parameter[1]: probability of a success;
     * <br> &nbsp;&nbsp;&nbsp;
     * Cauchy distribution: parameter[0]: location parameter,
     *                      parameter[1]: scale parameter;
     * <br> &nbsp;&nbsp;&nbsp;
     * Chi-Square distribution: parameter[0]: degree of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Exponential distribution: parameter[0]: the inverse of the mean;
     * <br> &nbsp;&nbsp;&nbsp;
     * F distribution: parameter[0]: numerator degree of freedom,
     *                 parameter[1]: denominator degreee of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Gamma distribution: The mean of the distribution is
     *                     parameter[0] / parameter[1], where
     *                     parameter[0]: location parameter,
     *                     parameter[1]: scale parameter;
     * <br> &nbsp;&nbsp;&nbsp;
     * Geometric distribution: parameter[0]: probability of a success;
     * <br> &nbsp;&nbsp;&nbsp;
     * Hypergoemtric distribution: parameter[0]: total number of elements in
     *                                           two populations,
     *                             parameter[1]: number of elements in one
     *                                           population,
     *                             parameter[2]: number of elements taken from
     *                                           the two populations;
     * <br> &nbsp;&nbsp;&nbsp;
     * Log-Normal distribution: parameter[0]: mean of the distribution of the
     *                                        log of the random variable,
     *                          parameter[1]: standard deviation of the
     *                                        distribution of the log of the
     *                                        random variable;
     * <br> &nbsp;&nbsp;&nbsp;
     * Negative Binomial distribution: parameter[0]: number of successes,
     *                                 parameter[1]: probability of a success;
     * <br> &nbsp;&nbsp;&nbsp;
     * Pareto distribution: parameter[0]: shape parameter,
     *                      parameter[1]: scale parameter;
     * <br> &nbsp;&nbsp;&nbsp;
     * Poisson distribution: parameter[0]: mean;
     * <br> &nbsp;&nbsp;&nbsp;
     * T distribution: parameter[0]: degree of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Weibull distribution: parameter[0]: shape parameter.
     */

    public double[] parameter;

    /**
     * The lower and upper limits for the probability.
     */

    public double[] range;

    /**
     * The percentile for computing the probability density.
     */

    public double percentile;

    /**
     * The cumulative probability for computing the percentile.
     */

    public double probabilityArgument;

    /**
     * The cumulative probability.
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
     * The index indicating if the upper limit and lower limit of
     * the interval are the same.
     */

    private double isEqual;

    /**
     * Default Probability constructor.
     */

    public Probability(){}

    /**
     * Calculates the density, probability, and percentile for a
     * probability distribution.
     * @param argument the argument with the following choices,
     * <br> DISTRIBUTION_TYPE: the enum in the class DistributionType or
     *                         the choices
     *                         "BETA", "BINOMIAL", "CAUCHY", "CHISQUARE",
     *                         "EXPONENTIAL", "F", "GAMMA", "GEOMETRIC",
     *                         "HYPERGEOMETRIC", "LOGISTIC", "LOGNORMAL",
     *                         "NORMAL", "PARETO", "POISSON", "T", "UNIFORM",
     *                         "WEIBULL;
     * <br> DISTRIBUTION_PARAMETER: the parameters corresponding to the
     *                              probability distribution;
     * <br> empty argument: standard normal distribution.
     * <br><br>
     * @param dataObject the input data, including the lower and upper limits
     *                   for the probability, the percentile, and
     *                   the probability for computing the percentile.
     * @exception IllegalArgumentException wrong input arguments or data.
     * @exception OutOfRangeException the argument is out of range.
     */

    public Probability(Hashtable argument,
                       Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null)
        {
            if(argument.size() > 0 &&
               argument.get(DISTRIBUTION_TYPE) != null &&
               argument.get(DISTRIBUTION_PARAMETER) != null &&
               dataObject.length == 3)
            {
                statisticalAnalysis = new Probability(
                        (DistributionType) argument.get(DISTRIBUTION_TYPE),
                        (double[]) argument.get(DISTRIBUTION_PARAMETER),
                        (double[]) dataObject[0], (Double) dataObject[1],
                        (Double) dataObject[2]);
            }
            else if (argument == null &&
                     dataObject.length == 3)
            {
                statisticalAnalysis = new Probability(
                        (double[]) dataObject[0], (Double) dataObject[1],
                        (Double) dataObject[2]);
            }
            else
            {
                throw new IllegalArgumentException(
                        "Wrong input arguments or data.");
            }
        }
        else
        {
            throw new IllegalArgumentException("Wrong input data.");
        }
    }

    /**
     * Calculates the density, probability, and percentile for a
     * probability distribution.
     * @param distribution the distribution of interest.
     * @param parameter the parameters corresponding to the distributions of
     *                  interest. The parameters for different distributions
     *                  are as follows:
     * <br> &nbsp;&nbsp;&nbsp;
     * Normal distribution: parameter[0]: mean,
     *                      parameter[1]: standard deviation;
     * <br> &nbsp;&nbsp;&nbsp;
     * Beta distribution: parameter[0]: degree of freedom,
     *                    parameter[1]: degreee of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Binomial distribution: parameter[0]: number of trials,
     *                        parameter[1]: probability of a success;
     * <br> &nbsp;&nbsp;&nbsp;
     * Cauchy distribution: parameter[0]: location parameter,
     *                      parameter[1]: scale parameter;
     * <br> &nbsp;&nbsp;&nbsp;
     * Chi-Square distribution: parameter[0]: degree of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Exponential distribution: parameter[0]: the inverse of the mean;
     * <br> &nbsp;&nbsp;&nbsp;
     * F distribution: parameter[0]: numerator degree of freedom,
     *                 parameter[1]: denominator degreee of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Gamma distribution: The mean of the distribution is
     *                     parameter[0] / parameter[1], where
     *                     parameter[0]: location parameter,
     *                     parameter[1]: scale parameter;
     * <br> &nbsp;&nbsp;&nbsp;
     * Geometric distribution: parameter[0]: probability of a success;
     * <br> &nbsp;&nbsp;&nbsp;
     * Hypergoemtric distribution: parameter[0]: total number of elements in
     *                                           two populations,
     *                             parameter[1]: number of elements in one
     *                                           population,
     *                             parameter[2]: number of elements taken from
     *                                           the two populations;
     * <br> &nbsp;&nbsp;&nbsp;
     * Log-Normal distribution: parameter[0]: mean of the distribution of the
     *                                        log of the random variable,
     *                          parameter[1]: standard deviation of the
     *                                        distribution of the log of the
     *                                        random variable;
     * <br> &nbsp;&nbsp;&nbsp;
     * Negative Binomial distribution: parameter[0]: number of successes,
     *                                 parameter[1]: probability of a success;
     * <br> &nbsp;&nbsp;&nbsp;
     * Pareto distribution: parameter[0]: shape parameter,
     *                      parameter[1]: scale parameter;
     * <br> &nbsp;&nbsp;&nbsp;
     * Poisson distribution: parameter[0]: mean;
     * <br> &nbsp;&nbsp;&nbsp;
     * T distribution: parameter[0]: degree of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Weibull distribution: parameter[0]: shape parameter.
     * @param range the lower and upper limits for the probability.
     * @param percentile the percentile for computing the probability density.
     * @param probabilityArgument the cumulative probability for computing the
     *                            percentile.
     * @exception OutOfRangeException the argument is out of range.
     */

    public Probability(DistributionType distribution,
                       double[] parameter,
                       double[] range,
                       double percentile,
                       double probabilityArgument)
    {
        cumulative = cumulative(distribution, parameter, range);
        probability = probability(distribution, parameter, percentile);
        inverse = inverse(distribution, parameter, probabilityArgument);
    }

    /**
     * Calculates the density, probability, and percentile for a
     * probability distribution.
     * @param distribution the distribution of interest.
     * @param parameter the parameters corresponding to the distributions of
     *                  interest. The parameters for different distributions
     *                  are as follows:
     * <br> &nbsp;&nbsp;&nbsp;
     * Normal distribution: parameter[0]: mean,
     *                      parameter[1]: standard deviation;
     * <br> &nbsp;&nbsp;&nbsp;
     * Beta distribution: parameter[0]: degree of freedom,
     *                    parameter[1]: degreee of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Binomial distribution: parameter[0]: number of trials,
     *                        parameter[1]: probability of a success;
     * <br> &nbsp;&nbsp;&nbsp;
     * Cauchy distribution: parameter[0]: location parameter,
     *                      parameter[1]: scale parameter;
     * <br> &nbsp;&nbsp;&nbsp;
     * Chi-Square distribution: parameter[0]: degree of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Exponential distribution: parameter[0]: the inverse of the mean;
     * <br> &nbsp;&nbsp;&nbsp;
     * F distribution: parameter[0]: numerator degree of freedom,
     *                 parameter[1]: denominator degreee of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Gamma distribution: The mean of the distribution is
     *                     parameter[0] / parameter[1], where
     *                     parameter[0]: location parameter,
     *                     parameter[1]: scale parameter;
     * <br> &nbsp;&nbsp;&nbsp;
     * Geometric distribution: parameter[0]: probability of a success;
     * <br> &nbsp;&nbsp;&nbsp;
     * Hypergoemtric distribution: parameter[0]: total number of elements in
     *                                           two populations,
     *                             parameter[1]: number of elements in one
     *                                           population,
     *                             parameter[2]: number of elements taken from
     *                                           the two populations;
     * <br> &nbsp;&nbsp;&nbsp;
     * Log-Normal distribution: parameter[0]: mean of the distribution of the
     *                                        log of the random variable,
     *                          parameter[1]: standard deviation of the
     *                                        distribution of the log of the
     *                                        random variable;
     * <br> &nbsp;&nbsp;&nbsp;
     * Negative Binomial distribution: parameter[0]: number of successes,
     *                                 parameter[1]: probability of a success;
     * <br> &nbsp;&nbsp;&nbsp;
     * Pareto distribution: parameter[0]: shape parameter,
     *                      parameter[1]: scale parameter;
     * <br> &nbsp;&nbsp;&nbsp;
     * Poisson distribution: parameter[0]: mean;
     * <br> &nbsp;&nbsp;&nbsp;
     * T distribution: parameter[0]: degree of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Weibull distribution: parameter[0]: shape parameter.
     * @param range the lower and upper limits for the probability.
     * @param percentile the percentile for computing the probability density.
     * @param probabilityArgument the cumulative probability for computing the
     *                            percentile.
     * @exception OutOfRangeException the argument is out of range.
     */

    public Probability(String distribution,
                       double[] parameter,
                       double[] range,
                       double percentile,
                       double probabilityArgument)
    {
        this(DistributionType.valueOf(distribution.toUpperCase()), parameter,
             range, percentile, probabilityArgument);
    }

    /**
     * Calculates the density, probability, and percentile for a
     * standard normal distribution.
     * @param range the lower and upper limits for the probability.
     * @param percentile the percentile for computing the probability density.
     * @param probabilityArgument the cumulative probability for computing the
     *                            percentile.
     * @exception OutOfRangeException the argument is out of range.
     */

    public Probability(double[] range,
                       double percentile,
                       double probabilityArgument)
    {
        this(DistributionType.NORMAL, new double[]{0.0, 1.0},
             range, percentile, probabilityArgument);
    }

    /**
     * Calculates the probability for the specified distribution within given
     * interval.
     * @param argument the argument with the following choices,
     * <br> DISTRIBUTION_TYPE: the enum in the class DistributionType or
     *                         the choices
     *                         "BETA", "BINOMIAL", "CAUCHY", "CHISQUARE",
     *                         "EXPONENTIAL", "F", "GAMMA", "GEOMETRIC",
     *                         "HYPERGEOMETRIC", "LOGISTIC", "LOGNORMAL",
     *                         "NORMAL", "PARETO", "POISSON", "T", "UNIFORM",
     *                         "WEIBULL;
     * <br> DISTRIBUTION_PARAMETER: the parameter(s) corresponding to the
     *                              probability distribution;
     * <br> empty argument: standard normal distribution.
     * <br><br>
     * @param dataObject the lower and upper limits for the probability.
     * @return the probability.
     * @exception IllegalArgumentException wrong input arguments or data.
     */

    public double cumulative(Hashtable argument,
                             Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null)
        {
            if (argument.size() > 0 &&
                argument.get(DISTRIBUTION_TYPE) != null &&
                argument.get(DISTRIBUTION_PARAMETER) != null &&
                dataObject.length >= 1 &&
                dataObject[0].getClass().getName().equalsIgnoreCase("[D"))
            {
                return this.cumulative = cumulative(
                        (DistributionType) argument.get(DISTRIBUTION_TYPE),
                        (double[]) argument.get(DISTRIBUTION_PARAMETER),
                        (double[]) dataObject[0]);
            }
            else if (argument == null &&
                     dataObject.length >= 1 &&
                     dataObject[0].getClass().getName().equalsIgnoreCase("[D"))
            {
                return this.cumulative = cumulative((double[]) dataObject[0]);
            }
            else
            {
                throw new IllegalArgumentException(
                        "Wrong input arguments or data.");
            }
        }
        else
        {
            throw new IllegalArgumentException("Wrong input data.");
        }
    }

    /**
     * Calculates the probability for the specified distribution within given
     * interval.
     * @param distribution the distribution of interest.
     * @param parameter the parameters corresponding to the distributions of
     *                  interest. The parameters for different distributions
     *                  are as follows:
     * <br> &nbsp;&nbsp;&nbsp;
     * Normal distribution: parameter[0]: mean,
     *                      parameter[1]: standard deviation;
     * <br> &nbsp;&nbsp;&nbsp;
     * Beta distribution: parameter[0]: degree of freedom,
     *                    parameter[1]: degreee of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Binomial distribution: parameter[0]: number of trials,
     *                        parameter[1]: probability of a success;
     * <br> &nbsp;&nbsp;&nbsp;
     * Cauchy distribution: parameter[0]: location parameter,
     *                      parameter[1]: scale parameter;
     * <br> &nbsp;&nbsp;&nbsp;
     * Chi-Square distribution: parameter[0]: degree of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Exponential distribution: parameter[0]: the inverse of the mean;
     * <br> &nbsp;&nbsp;&nbsp;
     * F distribution: parameter[0]: numerator degree of freedom,
     *                 parameter[1]: denominator degreee of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Gamma distribution: The mean of the distribution is
     *                     parameter[0] / parameter[1], where
     *                     parameter[0]: location parameter,
     *                     parameter[1]: scale parameter;
     * <br> &nbsp;&nbsp;&nbsp;
     * Geometric distribution: parameter[0]: probability of a success;
     * <br> &nbsp;&nbsp;&nbsp;
     * Hypergoemtric distribution: parameter[0]: total number of elements in
     *                                           two populations,
     *                             parameter[1]: number of elements in one
     *                                           population,
     *                             parameter[2]: number of elements taken from
     *                                           the two populations;
     * <br> &nbsp;&nbsp;&nbsp;
     * Log-Normal distribution: parameter[0]: mean of the distribution of the
     *                                        log of the random variable,
     *                          parameter[1]: standard deviation of the
     *                                        distribution of the log of the
     *                                        random variable;
     * <br> &nbsp;&nbsp;&nbsp;
     * Negative Binomial distribution: parameter[0]: number of successes,
     *                                 parameter[1]: probability of a success;
     * <br> &nbsp;&nbsp;&nbsp;
     * Pareto distribution: parameter[0]: shape parameter,
     *                      parameter[1]: scale parameter;
     * <br> &nbsp;&nbsp;&nbsp;
     * Poisson distribution: parameter[0]: mean;
     * <br> &nbsp;&nbsp;&nbsp;
     * T distribution: parameter[0]: degree of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Weibull distribution: parameter[0]: shape parameter.
     * @param range the interval of interest,
     * <br>              range[0]: lower limit;
     * <br>              range[1]: upper limit.
     * @return the probability.
     */

    public double cumulative(DistributionType distribution,
                             double[] parameter,
                             double[] range)
    {
        this.distribution = distribution;
        this.parameter = parameter;
        this.range = range;
        cumulative = 0.0;
        isEqual = 0.0;
        if (range[0] == range[1])
        {
            isEqual = 1.0;
        }
        switch (distribution)
        {
            case NORMAL:
                NormalDistribution normalDistribution = new
                        NormalDistribution(parameter[0],
                                           Math.pow(parameter[1], 2.0));
                cumulative = normalDistribution.cumulative(range[1]) -
                             normalDistribution.cumulative(range[0]);
                break;
            case BETA:
                BetaDistribution betaDistribution =
                        new BetaDistribution(parameter[0], parameter[1]);
                cumulative = betaDistribution.cumulative(range[1]) -
                             betaDistribution.cumulative(range[0]);
                break;
            case BINOMIAL:
                BinomialDistribution binomialDistribution = new
                        BinomialDistribution((int) parameter[0], parameter[1]);
                cumulative = discreteCDF(isEqual, binomialDistribution, range);
                break;
            case CAUCHY:
                CauchyDistribution cauchyDistribution = new CauchyDistribution(
                        parameter[0], parameter[1]);
                cumulative = cauchyDistribution.cumulative(range[1]) -
                             cauchyDistribution.cumulative(range[0]);
                break;
            case CHISQUARE:
                ChiSqrDistribution chisqrDistribution = new ChiSqrDistribution(
                        parameter[0]);
                cumulative = chisqrDistribution.cumulative(range[1]) -
                             chisqrDistribution.cumulative(range[0]);
                break;
            case EXPONENTIAL:
                ExponentialDistribution exponentialDistribution =
                        new ExponentialDistribution(parameter[0]);
                cumulative = exponentialDistribution.cumulative(range[1]) -
                             exponentialDistribution.cumulative(range[0]);
                break;
            case F:
                FDistribution fDistribution = new FDistribution(parameter[0],
                        parameter[1]);
                cumulative = fDistribution.cumulative(range[1]) -
                             fDistribution.cumulative(range[0]);
                break;
            case GAMMA:
                GammaDistribution gammaDistribution =
                        new GammaDistribution(parameter[0], parameter[1]);
                cumulative = gammaDistribution.cumulative(range[1]) -
                             gammaDistribution.cumulative(range[0]);
                break;
            case GEOMETRIC:
                GeometricDistribution geometricDistribution = new
                        GeometricDistribution(parameter[0]);

                cumulative = discreteCDF(isEqual, geometricDistribution,
                                         new double[]{
                                         range[0] + 1.0, range[1] + 1.0});
                break;
            case HYPERGEOMETRIC:
                HypergeometricDistribution hypergeometricDistribution =
                        new HypergeometricDistribution((int) parameter[0],
                        (int) parameter[1], (int) parameter[2]);
                cumulative = discreteCDF(isEqual, hypergeometricDistribution,
                                         range);
                break;
            case LOGNORMAL:
                LognormalDistribution lognormalDistribution = new
                        LognormalDistribution(parameter[0], parameter[1]);
                cumulative = lognormalDistribution.cumulative(range[1]) -
                             lognormalDistribution.cumulative(range[0]);
                break;
            case NEGATIVE_BINOMIAL:
                NegativeBinomialDistribution negativeBinomialDistribution =
                        new NegativeBinomialDistribution((int) parameter[0],
                        parameter[1]);
                cumulative = discreteCDF(isEqual, negativeBinomialDistribution,
                                         range);
                break;
            case PARETO:
                ParetoDistribution paretoDistribution =
                        new ParetoDistribution(parameter[0], parameter[1]);
                cumulative = paretoDistribution.cumulative(range[1]) -
                             paretoDistribution.cumulative(range[0]);
                break;
            case POISSON:
                PoissonDistribution poissonDistribution =
                        new PoissonDistribution(parameter[0]);
                cumulative = discreteCDF(isEqual, poissonDistribution, range);
                break;
            case T:
                TDistribution tDistribution =
                        new TDistribution((int) parameter[0]);
                cumulative = tDistribution.cumulative(range[1]) -
                             tDistribution.cumulative(range[0]);
                break;
            case WEIBULL:
                WeibullDistribution weibullDistribution =
                        new WeibullDistribution(parameter[0]);
                cumulative = weibullDistribution.cumulative(range[1]) -
                             weibullDistribution.cumulative(range[0]);
                break;
            default:
                throw new IllegalArgumentException
                        ("Input distribution function can not be found.");
        }
        output.put(CUMULATIVE, cumulative);

        return cumulative;
    }

    /**
     * Calculates the probability for the specified distribution within given
     * interval.
     * @param distribution the distribution of interest.
     * @param parameter the parameters corresponding to the distributions of
     *                  interest. The parameters for different distributions
     *                  are as follows:
     * <br> &nbsp;&nbsp;&nbsp;
     * Normal distribution: parameter[0]: mean,
     *                      parameter[1]: standard deviation;
     * <br> &nbsp;&nbsp;&nbsp;
     * Beta distribution: parameter[0]: degree of freedom,
     *                    parameter[1]: degreee of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Binomial distribution: parameter[0]: number of trials,
     *                        parameter[1]: probability of a success;
     * <br> &nbsp;&nbsp;&nbsp;
     * Cauchy distribution: parameter[0]: location parameter,
     *                      parameter[1]: scale parameter;
     * <br> &nbsp;&nbsp;&nbsp;
     * Chi-Square distribution: parameter[0]: degree of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Exponential distribution: parameter[0]: the inverse of the mean;
     * <br> &nbsp;&nbsp;&nbsp;
     * F distribution: parameter[0]: numerator degree of freedom,
     *                 parameter[1]: denominator degreee of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Gamma distribution: The mean of the distribution is
     *                     parameter[0] / parameter[1], where
     *                     parameter[0]: location parameter,
     *                     parameter[1]: scale parameter;
     * <br> &nbsp;&nbsp;&nbsp;
     * Geometric distribution: parameter[0]: probability of a success;
     * <br> &nbsp;&nbsp;&nbsp;
     * Hypergoemtric distribution: parameter[0]: total number of elements in
     *                                           two populations,
     *                             parameter[1]: number of elements in one
     *                                           population,
     *                             parameter[2]: number of elements taken from
     *                                           the two populations;
     * <br> &nbsp;&nbsp;&nbsp;
     * Log-Normal distribution: parameter[0]: mean of the distribution of the
     *                                        log of the random variable,
     *                          parameter[1]: standard deviation of the
     *                                        distribution of the log of the
     *                                        random variable;
     * <br> &nbsp;&nbsp;&nbsp;
     * Negative Binomial distribution: parameter[0]: number of successes,
     *                                 parameter[1]: probability of a success;
     * <br> &nbsp;&nbsp;&nbsp;
     * Pareto distribution: parameter[0]: shape parameter,
     *                      parameter[1]: scale parameter;
     * <br> &nbsp;&nbsp;&nbsp;
     * Poisson distribution: parameter[0]: mean;
     * <br> &nbsp;&nbsp;&nbsp;
     * T distribution: parameter[0]: degree of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Weibull distribution: parameter[0]: shape parameter.
     * @param range the interval of interest,
     * <br>         range[0]: lower limit;
     * <br>         range[1]: upper limit.
     * @return the probability.
     */

    public double cumulative(String distribution,
                             double[] parameter,
                             double[] range)
    {
        return cumulative(combotextToDistributionType(distribution),
                          parameter, range);
    }

    /**
     * Calculates the probability for the standard normal distribution within
     * given interval.
     * @param range the interval of interest,
     * <br>         range[0]: lower limit;
     * <br>         range[1]: upper limit.
     * @return the probability.
     */

    public double cumulative(double[] range)
    {
        return cumulative(
            DistributionType.NORMAL, new double[]{0.0, 1.0}, range);
    }

    /**
     * Calculates the probability density for the specified distribution.
     * @param argument the argument with the following choices,
     * <br> DISTRIBUTION_TYPE: the enum in the class DistributionType or
     *                         the choices
     *                         "BETA", "BINOMIAL", "CAUCHY", "CHISQUARE",
     *                         "EXPONENTIAL", "F", "GAMMA", "GEOMETRIC",
     *                         "HYPERGEOMETRIC", "LOGISTIC", "LOGNORMAL",
     *                         "NORMAL", "PARETO", "POISSON", "T", "UNIFORM",
     *                         "WEIBULL;
     * <br> DISTRIBUTION_PARAMETER: the parameter(s) corresponding to the
     *                              probability distribution;
     * <br> empty argument: standard normal distribution.
     * <br><br>
     * @param dataObject the percentile.
     * @return the probability density.
     * @exception IllegalArgumentException wrong input arguments or data.
     */

    public double probability(Hashtable argument,
                              Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null)
        {
            if (argument.size() > 0 &&
                argument.get(DISTRIBUTION_TYPE) != null &&
                argument.get(DISTRIBUTION_PARAMETER) != null &&
                dataObject.length >= 1)
            {
                if (dataObject.length == 1 &&
                    dataObject[0].getClass().getSuperclass().toString().
                    equalsIgnoreCase("class java.lang.Number"))
                 {
                     return this.probability = probability(
                             (DistributionType) argument.get(DISTRIBUTION_TYPE),
                             (double[]) argument.get(DISTRIBUTION_PARAMETER),
                             (Double) dataObject[0]);
                 }
                 else if (dataObject.length == 3 &&
                          dataObject[1].getClass().getSuperclass().toString().
                          equalsIgnoreCase("class java.lang.Number"))
                 {
                     return this.probability = probability(
                             (DistributionType) argument.get(DISTRIBUTION_TYPE),
                             (double[]) argument.get(DISTRIBUTION_PARAMETER),
                             (Double) dataObject[1]);
                 }
                 else
                 {
                     throw new IllegalArgumentException(
                             "Wrong input arguments.");
                 }
            }
            if (argument == null &&
                dataObject.length >= 1)
            {
                if (dataObject.length == 1 &&
                    dataObject[0].getClass().getSuperclass().toString().
                    equalsIgnoreCase("class java.lang.Number"))
                {
                    return this.probability =
                            probability((Double) dataObject[0]);
                }
                else if (dataObject.length == 3 &&
                         dataObject[1].getClass().getSuperclass().toString().
                         equalsIgnoreCase("class java.lang.Number"))
                {
                    return this.probability =
                            probability((Double) dataObject[1]);
                }
                else
                {
                    throw new IllegalArgumentException(
                            "Wrong input arguments.");
                }
            }
            else
            {
                throw new IllegalArgumentException(
                        "Wrong input arguments or data.");
            }
        }
        else
        {
            throw new IllegalArgumentException("Wrong input data.");
        }
    }

    /**
     * Calculates the probability density for the specified distribution.
     * @param distribution the distribution of interest.
     * @param parameter the parameters corresponding to the distributions of
     *                  interest. The parameters for different distributions
     *                  are as follows:
     * <br> &nbsp;&nbsp;&nbsp;
     * Normal distribution: parameter[0]: mean,
     *                      parameter[1]: standard deviation;
     * <br> &nbsp;&nbsp;&nbsp;
     * Beta distribution: parameter[0]: degree of freedom,
     *                    parameter[1]: degreee of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Binomial distribution: parameter[0]: number of trials,
     *                        parameter[1]: probability of a success;
     * <br> &nbsp;&nbsp;&nbsp;
     * Cauchy distribution: parameter[0]: location parameter,
     *                      parameter[1]: scale parameter;
     * <br> &nbsp;&nbsp;&nbsp;
     * Chi-Square distribution: parameter[0]: degree of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Exponential distribution: parameter[0]: the inverse of the mean;
     * <br> &nbsp;&nbsp;&nbsp;
     * F distribution: parameter[0]: numerator degree of freedom,
     *                 parameter[1]: denominator degreee of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Gamma distribution: The mean of the distribution is
     *                     parameter[0] / parameter[1], where
     *                     parameter[0]: location parameter,
     *                     parameter[1]: scale parameter;
     * <br> &nbsp;&nbsp;&nbsp;
     * Geometric distribution: parameter[0]: probability of a success;
     * <br> &nbsp;&nbsp;&nbsp;
     * Hypergoemtric distribution: parameter[0]: total number of elements in
     *                                           two populations,
     *                             parameter[1]: number of elements in one
     *                                           population,
     *                             parameter[2]: number of elements taken from
     *                                           the two populations;
     * <br> &nbsp;&nbsp;&nbsp;
     * Log-Normal distribution: parameter[0]: mean of the distribution of the
     *                                        log of the random variable,
     *                          parameter[1]: standard deviation of the
     *                                        distribution of the log of the
     *                                        random variable;
     * <br> &nbsp;&nbsp;&nbsp;
     * Negative Binomial distribution: parameter[0]: number of successes,
     *                                 parameter[1]: probability of a success;
     * <br> &nbsp;&nbsp;&nbsp;
     * Pareto distribution: parameter[0]: shape parameter,
     *                      parameter[1]: scale parameter;
     * <br> &nbsp;&nbsp;&nbsp;
     * Poisson distribution: parameter[0]: mean;
     * <br> &nbsp;&nbsp;&nbsp;
     * T distribution: parameter[0]: degree of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Weibull distribution: parameter[0]: shape parameter.
     * @param percentile the percentile.
     * @return the probability density.
     */

    public double probability(DistributionType distribution,
                              double[] parameter,
                              double percentile)
    {
        this.distribution = distribution;
        this.parameter = parameter;
        this.percentile = percentile;
        probability = 0.0;
        switch (distribution)
        {
            case NORMAL:
                NormalDistribution normalDistribution = new
                        NormalDistribution(parameter[0],
                                           Math.pow(parameter[1], 2.0));
                probability = normalDistribution.probability(percentile);
                break;
            case BETA:
                BetaDistribution betaDistribution =
                        new BetaDistribution(parameter[0], parameter[1]);
                probability = betaDistribution.probability(percentile);
                break;
            case BINOMIAL:
                BinomialDistribution binomialDistribution = new
                        BinomialDistribution((int) parameter[0], parameter[1]);
                probability = binomialDistribution.probability(percentile);
                break;
            case CAUCHY:
                CauchyDistribution cauchyDistribution = new CauchyDistribution(
                        parameter[0], parameter[1]);
                probability = cauchyDistribution.probability(percentile);
                break;
            case CHISQUARE:
                ChiSqrDistribution chisqrDistribution = new ChiSqrDistribution(
                        parameter[0]);
                probability = chisqrDistribution.probability(percentile);
                break;
            case EXPONENTIAL:
                ExponentialDistribution exponentialDistribution =
                        new ExponentialDistribution(parameter[0]);
                probability = exponentialDistribution.probability(percentile);
                break;
            case F:
                FDistribution fDistribution =
                        new FDistribution(parameter[0], parameter[1]);
                probability = fDistribution.probability(percentile);
                break;
            case GAMMA:
                GammaDistribution gammaDistribution =
                        new GammaDistribution(parameter[0], parameter[1]);
                probability = gammaDistribution.probability(percentile);
                break;
            case GEOMETRIC:
                GeometricDistribution geometricDistribution = new
                        GeometricDistribution(parameter[0]);
                probability = geometricDistribution.
                              probability(Math.floor(percentile + 1.0));
                break;
            case HYPERGEOMETRIC:
                HypergeometricDistribution hypergeometricDistribution =
                        new HypergeometricDistribution((int) parameter[0],
                        (int) parameter[1], (int) parameter[2]);
                probability =
                        hypergeometricDistribution.probability(percentile);
                break;
            case LOGNORMAL:
                LognormalDistribution lognormalDistribution = new
                        LognormalDistribution(parameter[0], parameter[1]);
                probability = lognormalDistribution.probability(percentile);
                break;
            case NEGATIVE_BINOMIAL:
                NegativeBinomialDistribution negativeBinomialDistribution =
                        new NegativeBinomialDistribution((int) parameter[0],
                        parameter[1]);
                probability =
                        negativeBinomialDistribution.probability(percentile);
            break;
            case PARETO:
                ParetoDistribution paretoDistribution =
                        new ParetoDistribution(parameter[0], parameter[1]);
                probability = paretoDistribution.probability(percentile);
                break;
            case POISSON:
                PoissonDistribution poissonDistribution =
                        new PoissonDistribution(parameter[0]);
                probability = poissonDistribution.probability(percentile);
                break;
            case T:
                TDistribution tDistribution =
                        new TDistribution((int) parameter[0]);
                probability = tDistribution.probability(percentile);
                break;
            case WEIBULL:
                WeibullDistribution weibullDistribution =
                        new WeibullDistribution(parameter[0]);
                probability = weibullDistribution.probability(percentile);
                break;
            default:
                throw new IllegalArgumentException
                        ("Input distribution function can not be found.");
        }
        output.put(PROBABILITY, probability);

        return probability;
    }

    /**
     * Calculates the probability density for the specified distribution.
     * @param distribution the distribution of interest.
     * @param parameter the parameters corresponding to the distributions of
     *                  interest. The parameters for different distributions
     *                  are as follows:
     * <br> &nbsp;&nbsp;&nbsp;
     * Normal distribution: parameter[0]: mean,
     *                      parameter[1]: standard deviation;
     * <br> &nbsp;&nbsp;&nbsp;
     * Beta distribution: parameter[0]: degree of freedom,
     *                    parameter[1]: degreee of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Binomial distribution: parameter[0]: number of trials,
     *                        parameter[1]: probability of a success;
     * <br> &nbsp;&nbsp;&nbsp;
     * Cauchy distribution: parameter[0]: location parameter,
     *                      parameter[1]: scale parameter;
     * <br> &nbsp;&nbsp;&nbsp;
     * Chi-Square distribution: parameter[0]: degree of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Exponential distribution: parameter[0]: the inverse of the mean;
     * <br> &nbsp;&nbsp;&nbsp;
     * F distribution: parameter[0]: numerator degree of freedom,
     *                 parameter[1]: denominator degreee of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Gamma distribution: The mean of the distribution is
     *                     parameter[0] / parameter[1], where
     *                     parameter[0]: location parameter,
     *                     parameter[1]: scale parameter;
     * <br> &nbsp;&nbsp;&nbsp;
     * Geometric distribution: parameter[0]: probability of a success;
     * <br> &nbsp;&nbsp;&nbsp;
     * Hypergoemtric distribution: parameter[0]: total number of elements in
     *                                           two populations,
     *                             parameter[1]: number of elements in one
     *                                           population,
     *                             parameter[2]: number of elements taken from
     *                                           the two populations;
     * <br> &nbsp;&nbsp;&nbsp;
     * Log-Normal distribution: parameter[0]: mean of the distribution of the
     *                                        log of the random variable,
     *                          parameter[1]: standard deviation of the
     *                                        distribution of the log of the
     *                                        random variable;
     * <br> &nbsp;&nbsp;&nbsp;
     * Negative Binomial distribution: parameter[0]: number of successes,
     *                                 parameter[1]: probability of a success;
     * <br> &nbsp;&nbsp;&nbsp;
     * Pareto distribution: parameter[0]: shape parameter,
     *                      parameter[1]: scale parameter;
     * <br> &nbsp;&nbsp;&nbsp;
     * Poisson distribution: parameter[0]: mean;
     * <br> &nbsp;&nbsp;&nbsp;
     * T distribution: parameter[0]: degree of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Weibull distribution: parameter[0]: shape parameter.
     * @param percentile the percentile.
     * @return the probability density.
     */

    public double probability(String distribution,
                              double[] parameter,
                              double percentile)
    {
        return probability(combotextToDistributionType(distribution),
                           parameter, percentile);
    }

    /**
     * Calculates the probability density for the standard normal distribution.
     * @param percentile the percentile.
     * @return the probability density.
     */

    public double probability(double percentile)
    {
        return probability(DistributionType.NORMAL,
                           new double[]{0.0, 1.0}, percentile);
    }

    /**
     * Calculates the percentile for the specified distribution.
     * @param argument the argument with the following choices,
     * <br> DISTRIBUTION_TYPE: the enum in the class DistributionType or
     *                         the choices
     *                         "BETA", "BINOMIAL", "CAUCHY", "CHISQUARE",
     *                         "EXPONENTIAL", "F", "GAMMA", "GEOMETRIC",
     *                         "HYPERGEOMETRIC", "LOGISTIC", "LOGNORMAL",
     *                         "NORMAL", "PARETO", "POISSON", "T", "UNIFORM",
     *                         "WEIBULL;
     * <br> DISTRIBUTION_PARAMETER: the parameter(s) corresponding to the
     *                              probability distribution;
     * <br> empty argument: standard normal distribution.
     * <br><br>
     * @param dataObject the probability.
     * @return the percentile.
     * @exception IllegalArgumentException wrong input arguments or data.
     */

    public double inverse(Hashtable argument,
                          Object ...dataObject)
    {
        this.argument = argument;
        this.dataObject = dataObject;
        if (dataObject != null)
        {
            if (argument.size() > 0 &&
                argument.get(DISTRIBUTION_TYPE) != null &&
                argument.get(DISTRIBUTION_PARAMETER) != null &&
                dataObject.length >= 1)
            {
                if (dataObject.length == 1 &&
                    dataObject[0].getClass().getSuperclass().toString().
                    equalsIgnoreCase("class java.lang.Number"))
                 {
                     return this.inverse = inverse(
                            (DistributionType) argument.get(DISTRIBUTION_TYPE),
                            (double[]) argument.get(DISTRIBUTION_PARAMETER),
                            (Double) dataObject[0]);
                 }
                 else if (dataObject.length == 3 &&
                          dataObject[2].getClass().getSuperclass().toString().
                          equalsIgnoreCase("class java.lang.Number"))
                 {
                     return this.inverse = inverse(
                             (DistributionType) argument.get(DISTRIBUTION_TYPE),
                             (double[]) argument.get(DISTRIBUTION_PARAMETER),
                             (Double) dataObject[2]);
                 }
                 else
                 {
                     throw new IllegalArgumentException(
                             "Wrong input arguments.");
                 }
            }
            else if (argument == null &&
                     dataObject.length >= 1)
            {
                if (dataObject.length == 1 &&
                    dataObject[0].getClass().getSuperclass().toString().
                    equalsIgnoreCase("class java.lang.Number"))
                 {
                     return this.inverse = inverse((Double) dataObject[0]);
                 }
                 else if (dataObject.length == 3 &&
                          dataObject[2].getClass().getSuperclass().toString().
                          equalsIgnoreCase("class java.lang.Number"))
                 {
                     return this.inverse = inverse((Double) dataObject[2]);
                 }
                 else
                 {
                     throw new IllegalArgumentException(
                             "Wrong input arguments.");
                 }
            }
            else
            {
                throw new IllegalArgumentException(
                        "Wrong input arguments or data.");
            }
        }
        else
        {
            throw new IllegalArgumentException("Wrong input data.");
        }
    }

    /**
     * Calculates the percentile for the specified distribution.
     * @param distribution the distribution of interest.
     * @param parameter the parameters corresponding to the distributions of
     *                  interest. The parameters for different distributions
     *                  are as follows:
     * <br> &nbsp;&nbsp;&nbsp;
     * Normal distribution: parameter[0]: mean,
     *                      parameter[1]: standard deviation;
     * <br> &nbsp;&nbsp;&nbsp;
     * Beta distribution: parameter[0]: degree of freedom,
     *                    parameter[1]: degreee of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Binomial distribution: parameter[0]: number of trials,
     *                        parameter[1]: probability of a success;
     * <br> &nbsp;&nbsp;&nbsp;
     * Cauchy distribution: parameter[0]: location parameter,
     *                      parameter[1]: scale parameter;
     * <br> &nbsp;&nbsp;&nbsp;
     * Chi-Square distribution: parameter[0]: degree of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Exponential distribution: parameter[0]: the inverse of the mean;
     * <br> &nbsp;&nbsp;&nbsp;
     * F distribution: parameter[0]: numerator degree of freedom,
     *                 parameter[1]: denominator degreee of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Gamma distribution: The mean of the distribution is
     *                     parameter[0] / parameter[1], where
     *                     parameter[0]: location parameter,
     *                     parameter[1]: scale parameter;
     * <br> &nbsp;&nbsp;&nbsp;
     * Geometric distribution: parameter[0]: probability of a success;
     * <br> &nbsp;&nbsp;&nbsp;
     * Hypergoemtric distribution: parameter[0]: total number of elements in
     *                                           two populations,
     *                             parameter[1]: number of elements in one
     *                                           population,
     *                             parameter[2]: number of elements taken from
     *                                           the two populations;
     * <br> &nbsp;&nbsp;&nbsp;
     * Log-Normal distribution: parameter[0]: mean of the distribution of the
     *                                        log of the random variable,
     *                          parameter[1]: standard deviation of the
     *                                        distribution of the log of the
     *                                        random variable;
     * <br> &nbsp;&nbsp;&nbsp;
     * Negative Binomial distribution: parameter[0]: number of successes,
     *                                 parameter[1]: probability of a success;
     * <br> &nbsp;&nbsp;&nbsp;
     * Pareto distribution: parameter[0]: shape parameter,
     *                      parameter[1]: scale parameter;
     * <br> &nbsp;&nbsp;&nbsp;
     * Poisson distribution: parameter[0]: mean;
     * <br> &nbsp;&nbsp;&nbsp;
     * T distribution: parameter[0]: degree of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Weibull distribution: parameter[0]: shape parameter.
     * @param probabilityArgument the cumulative probability for computing the
     *                            percentile.
     * @return the percentile.
     * @exception OutOfRangeException the argument is out of range.
     */

    public double inverse(DistributionType distribution,
                          double[] parameter,
                          double probabilityArgument)
    {
        this.distribution = distribution;
        this.parameter = parameter;
        this.probabilityArgument = probabilityArgument;
        inverse = 0.0;
        switch (distribution)
        {
            case NORMAL:
                NormalDistribution normalDistribution = new
                        NormalDistribution(parameter[0],
                                           Math.pow(parameter[1], 2.0));
                inverse = normalDistribution.inverse(probabilityArgument);
                break;
            case BETA:
                BetaDistribution betaDistribution =
                        new BetaDistribution(parameter[0], parameter[1]);
                inverse = betaDistribution.inverse(probabilityArgument);
                break;
            case BINOMIAL:
                BinomialDistribution binomialDistribution = new
                        BinomialDistribution((int) parameter[0], parameter[1]);
                inverse = discreteInverse(binomialDistribution,
                                          probabilityArgument);
                break;
            case CAUCHY:
                CauchyDistribution cauchyDistribution = new CauchyDistribution(
                        parameter[0], parameter[1]);
                inverse = cauchyDistribution.inverse(probabilityArgument);
                break;
            case CHISQUARE:
                ChiSqrDistribution chisqrDistribution = new ChiSqrDistribution(
                        parameter[0]);
                inverse = chisqrDistribution.inverse(probabilityArgument);
                break;
            case EXPONENTIAL:
                ExponentialDistribution exponentialDistribution =
                        new ExponentialDistribution(parameter[0]);
                inverse = exponentialDistribution.
                          inverse(probabilityArgument);
                break;
            case F:
                inverse = new FDistribution(parameter[0], parameter[1]).
                          inverse(probabilityArgument);
                break;
            case GAMMA:
                GammaDistribution gammaDistribution =
                        new GammaDistribution(parameter[0], parameter[1]);
                inverse = gammaDistribution.inverse(probabilityArgument);
                break;
            case GEOMETRIC:
                GeometricDistribution geometricDistribution = new
                        GeometricDistribution(parameter[0]);
                inverse = discreteInverse(geometricDistribution,
                                          probabilityArgument) - 1.0;

                break;
            case HYPERGEOMETRIC:
                HypergeometricDistribution hypergeometricDistribution =
                        new HypergeometricDistribution((int) parameter[0],
                        (int) parameter[1], (int) parameter[2]);
                inverse = hypergeometricDistribution.
                          inverse(probabilityArgument);
                break;
            case LOGNORMAL:
                LognormalDistribution lognormalDistribution = new
                        LognormalDistribution(parameter[0], parameter[1]);
                inverse = lognormalDistribution.inverse(probabilityArgument);
                break;
            case NEGATIVE_BINOMIAL:
                NegativeBinomialDistribution negativeBinomialDistribution =
                        new NegativeBinomialDistribution((int) parameter[0],
                        parameter[1]);
                inverse = negativeBinomialDistribution.
                          inverse(probabilityArgument);
                break;
            case PARETO:
                ParetoDistribution paretoDistribution =
                        new ParetoDistribution(parameter[0], parameter[1]);
                inverse = paretoDistribution.inverse(probabilityArgument);
                break;
            case POISSON:
                PoissonDistribution poissonDistribution =
                        new PoissonDistribution(parameter[0]);
                inverse = discreteInverse(poissonDistribution,
                                          probabilityArgument);
                break;
            case T:
                TDistribution tDistribution =
                        new TDistribution((int) parameter[0]);
                inverse = tDistribution.inverse(probabilityArgument);
                break;
            case WEIBULL:
                WeibullDistribution weibullDistribution =
                        new WeibullDistribution(parameter[0]);
                inverse = weibullDistribution.inverse(probabilityArgument);
                break;
            default:
                throw new IllegalArgumentException
                        ("Input distribution function can not be found.");
        }
        output.put(INVERSE, inverse);

        return inverse;
    }

    /**
     * Calculates the percentile for the specified distribution.
     * @param distribution the distribution of interest.
     * @param parameter the parameters corresponding to the distributions of
     *                  interest. The parameters for different distributions
     *                  are as follows:
     * <br> &nbsp;&nbsp;&nbsp;
     * Normal distribution: parameter[0]: mean,
     *                      parameter[1]: standard deviation;
     * <br> &nbsp;&nbsp;&nbsp;
     * Beta distribution: parameter[0]: degree of freedom,
     *                    parameter[1]: degreee of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Binomial distribution: parameter[0]: number of trials,
     *                        parameter[1]: probability of a success;
     * <br> &nbsp;&nbsp;&nbsp;
     * Cauchy distribution: parameter[0]: location parameter,
     *                      parameter[1]: scale parameter;
     * <br> &nbsp;&nbsp;&nbsp;
     * Chi-Square distribution: parameter[0]: degree of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Exponential distribution: parameter[0]: the inverse of the mean;
     * <br> &nbsp;&nbsp;&nbsp;
     * F distribution: parameter[0]: numerator degree of freedom,
     *                 parameter[1]: denominator degreee of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Gamma distribution: The mean of the distribution is
     *                     parameter[0] / parameter[1], where
     *                     parameter[0]: location parameter,
     *                     parameter[1]: scale parameter;
     * <br> &nbsp;&nbsp;&nbsp;
     * Geometric distribution: parameter[0]: probability of a success;
     * <br> &nbsp;&nbsp;&nbsp;
     * Hypergoemtric distribution: parameter[0]: total number of elements in
     *                                           two populations,
     *                             parameter[1]: number of elements in one
     *                                           population,
     *                             parameter[2]: number of elements taken from
     *                                           the two populations;
     * <br> &nbsp;&nbsp;&nbsp;
     * Log-Normal distribution: parameter[0]: mean of the distribution of the
     *                                        log of the random variable,
     *                          parameter[1]: standard deviation of the
     *                                        distribution of the log of the
     *                                        random variable;
     * <br> &nbsp;&nbsp;&nbsp;
     * Negative Binomial distribution: parameter[0]: number of successes,
     *                                 parameter[1]: probability of a success;
     * <br> &nbsp;&nbsp;&nbsp;
     * Pareto distribution: parameter[0]: shape parameter,
     *                      parameter[1]: scale parameter;
     * <br> &nbsp;&nbsp;&nbsp;
     * Poisson distribution: parameter[0]: mean;
     * <br> &nbsp;&nbsp;&nbsp;
     * T distribution: parameter[0]: degree of freedom;
     * <br> &nbsp;&nbsp;&nbsp;
     * Weibull distribution: parameter[0]: shape parameter.
     * @param probabilityArgument the cumulative probability for computing the
     *                            percentile.
     * @return the percentile.
     * @exception OutOfRangeException the argument is out of range.
     */

    public double inverse(String distribution,
                          double[] parameter,
                          double probabilityArgument)
    {
        return inverse(combotextToDistributionType(distribution),
                       parameter, probabilityArgument);
    }

    /**
     * Calculates the percentile for the standard normal distribution.
     * @param probabilityArgument the cumulative probability for computing the
     *                            percentile.
     * @return the percentile.
     * @exception OutOfRangeException the argument is out of range.
     */

    public double inverse(double probabilityArgument)
    {
        return inverse(DistributionType.NORMAL, new double[]{0.0, 1.0},
                       probabilityArgument);
    }

    /**
     * Calculates the posterior probabilities based on Bayes' theorem.
     * @param prior the prior probabilities, P(A1),..., P(An).
     * @param conditionalProbability the conditional probabilities,
     *                               conditionalProbability[i][j]: P(Bi|Aj).
     * @return the posterior probabilities.
     * @exception IllegalArgumentException the length of the vector
     *                                     conditionalProbability[i] should be
     *                                     consistent with the one of the vector
     *                                     prior.
     * @exception IllegalArgumentException the sum of the prior probabilities
     *                                     should be equal to 1.
     */

    public double[][] posterior(double[] prior,
                                double[] ...conditionalProbability)
    {
        double[][] posteriorProbability =
            new double[conditionalProbability.length][prior.length];
        double[] marginalProbability =
            new double[conditionalProbability.length];
        if(new BasicStatistics().sum(prior) != 1.0)
        {
            throw new IllegalArgumentException(
                    "The sum of the prior probabilities should be equal to 1.");
        }
        DataManager dataManager = new DataManager();
        for(int i = 0; i < posteriorProbability.length; i++)
        {
            if (posteriorProbability[i].length != prior.length)
            {
                throw new IllegalArgumentException(
                    "The length of the vector conditionalProbability[i] " +
                    "should be consistent with the one of the vector prior.");
            }
            marginalProbability[i] = 0.0;
            for(int j = 0; j < prior.length; j++)
            {
                posteriorProbability[i][j] = prior[j] *
                                             conditionalProbability[i][j];
                marginalProbability[i] += posteriorProbability[i][j];
            }
        }
        for(int i = 0; i < posteriorProbability.length; i++)
        {
            for (int j = 0; j < prior.length; j++)
            {
                posteriorProbability[i][j] /= marginalProbability[i];
            }
        }

        return posteriorProbability;
    }

    /**
     * Calculates the probability for the discrete distribution within given
     * interval.
     * @param isEqual the index indicating if the upper limit and lower limit of
     *                the interval are the same.
     * @param distribution the distribution of interest.
     * @param range the interval of interest,
     * <br>              range[0]: lower limit;
     * <br>              range[1]: upper limit.
     * @return the probability.
     */

    double discreteCDF(double isEqual,
                       ProbabilityDistribution distribution,
                       double[] range)
    {
        if(Math.rint(range[0]) == range[0])
        {
            cumulative = distribution.probability(range[0]) *
                         isEqual +
                         distribution.cumulative(range[1]) -
                         distribution.cumulative(range[0]) +
                         distribution.probability(range[0]);
        }
        else
        {
            cumulative = distribution.probability(range[0]) *
                         isEqual +
                         distribution.cumulative(range[1]) -
                         distribution.cumulative(range[0]);
        }

        return cumulative;
    }

    /**
     * Calculates the percentile for the discrete distribution within given
     * interval.
     * @param distribution the distribution of interest.
     * @param probabilityArgument the cumulative probability for computing the
     *                            percentile.
     * @return the percentile.
     */

    private double discreteInverse(ProbabilityDistribution distribution,
                                   double probabilityArgument)
    {
        inverse = Math.floor(distribution.inverse(probabilityArgument));
        while (distribution.cumulative(inverse) < probabilityArgument)
        {
            inverse += 1.0;
        }

        return inverse;
    }

    private DistributionType combotextToDistributionType(String combotext)
    {
        if (combotext.equalsIgnoreCase("Chi-square"))
        {
            distribution = DistributionType.CHISQUARE;
        }
        else if (combotext.equalsIgnoreCase("Log-normal"))
        {
            distribution = DistributionType.LOGNORMAL;
        }
        else if (combotext.equalsIgnoreCase("Negative Binomial"))
        {
            distribution = DistributionType.NEGATIVE_BINOMIAL;
        }
        else
        {
            distribution = DistributionType.valueOf(combotext.toUpperCase());
        }

        return distribution;
    }

}
