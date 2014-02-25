package JSci.maths.statistics;

/**
 *
 * @author Mark
 */
public class DiscreteDistribution {
    private final double[] weights;
    private final double mass;

    public DiscreteDistribution(double[] weights)
    {
        this.weights = weights;
        double sum = 0.0;
        for(int i=0; i<weights.length; i++)
        {
            sum += weights[i];
        }
        this.mass = sum;
    }

    public double probability(int n)
    {
        return weights[n]/mass;
    }
    
    public double cumulative(int n)
    {
        double total = 0.0;
        for(int i=0; i<n; i++)
        {
            total += weights[i];
        }
        return total;
    }

    public int inverse(double probability)
    {
        double w = probability*mass;
        double total = 0.0;
        for(int i=0; i<weights.length; i++)
        {
            total += weights[i];
            if(total > w)
            {
                return i;
            }
        }
        return weights.length;
    }
}
