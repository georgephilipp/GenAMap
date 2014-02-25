package JSci.maths.statistics;

/**
 *
 * @author Mark
 */
public class DiracDeltaDistribution extends ProbabilityDistribution {
    private final double mean;

    public DiracDeltaDistribution(double mean)
    {
        this.mean = mean;
    }
    public double probability(double X) {
        return (X == mean ? 1.0 : 0.0);
    }

    public double cumulative(double X) {
        return (X < mean ? 0.0 : 1.0);
    }

    public double inverse(double probability) {
        throw new UnsupportedOperationException("Undefined");
    }

}
