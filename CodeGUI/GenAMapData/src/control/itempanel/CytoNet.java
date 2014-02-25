package control.itempanel;

/**
 * This class allows us read in a file type where we have edge-edge-weight
 *
 * @author rcurtis
 */

    public class CytoNet
    {
        public String t1;
        public String t2;
        double weight;

        public CytoNet(String t1, String t2, double weight)
        {
            this.t1 = t1;
            this.t2 = t2;
            this.weight = weight;
        }
    }
