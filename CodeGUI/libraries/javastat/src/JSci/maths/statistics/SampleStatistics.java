package JSci.maths.statistics;

/**
* This class calculates commonly used sample statistics in an incremental fashion.
* @version 1.0
* @author Mark Hale
*/
public class SampleStatistics {
        private int n = 0;
        private double sum = 0.0;
        private double sumSqr = 0.0;
        private double min = Double.POSITIVE_INFINITY;
        private double max = Double.NEGATIVE_INFINITY;

        public SampleStatistics() {}
        public void update(double x) {
                n++;
                sum += x;
                sumSqr += x*x;
                min = Math.min(x, min);
                max = Math.max(x, max);
        }
        public int getCount() {
                return n;
        }
        public double getMean() {
                return sum/n;
        }
        public double getVariance() {
                return (sumSqr - sum*sum/n)/(n-1);
        }
        public double getMinimum() {
                return min;
        }
        public double getMaximum() {
                return max;
        }
}

