package JSci.maths.statistics;

/**
 *
 * @author Mark
 */
public class UniformDistribution extends ProbabilityDistribution {
    private final double min;
    private final double max;
    private final double range;

    public UniformDistribution(double minX, double maxX)
    {
        this.min = minX;
        this.max = maxX;
        this.range = maxX - minX;
    }

    public double probability(double X) {
        return (X >= min && X <= max ? 1.0/range : 0.0);
    }

    public double cumulative(double X) {
        if(X < min)
            return 0.0;
        else if(X <= max)
            return (X-min)/range;
        else
            return 1.0;
    }

    public double inverse(double probability) {
        return probability*range + min;
    }
}
