package views.network.threeway;

import org.apache.commons.collections15.Transformer;

/**
 * This class is used to show the edge weight labels on a graph.
 *
 * @author James Moffatt
 * @author rcurtis
 */
public class ThreeWayEdgeLabelStringer implements Transformer<ThreeWayGraphEdge, String>
{
    private boolean assocenabled = true;
    private boolean geneenabled = true;
    private boolean traitenabled = true;

    /**
     * Determines whether association labels should be shown.
     * @param b
     */
    public void setAssocEnabeled(boolean b)
    {
        assocenabled = b;
    }

    /**
     * Determines whether trait labels should be shown.
     * @param e
     */
    public void setTraitEnabled(boolean b)
    {
        traitenabled = b;
    }

        /**
     * Determines whether gene labels should be shown.
     * @param e
     */
    public void setGeneEnabled(boolean b)
    {
        geneenabled = b;
    }

    public ThreeWayEdgeLabelStringer(boolean e)
    {
        super();
        //enabled = e;
    }

    /**
     *
     * 
     * @param Number e: Number to be tranformed into a string
     * @return the string form of the number represetned by e
     */
    public String transform(ThreeWayGraphEdge e)
    {
        if (e == null)
        {
            throw new NullPointerException();
        }

        if ((assocenabled && e instanceof ThreeWayAssociationEdge) ||
             (geneenabled && e instanceof ThreeWayCorrelationEdge && e.getT1() instanceof ThreeWayGeneGraphObject) ||
             (traitenabled && e instanceof ThreeWayCorrelationEdge && e.getT1() instanceof ThreeWayTraitGraphObject))
        {
            String s = String.valueOf(e.weight);
            if (s.indexOf('E') > -1)
            {
                if (s.length() > 4)
                {
                    return s.substring(0, 4) + s.substring(s.indexOf('E'));
                }
                else
                {
                    return s;
                }
            }
            if (s.length() > 5)
            {
                return s.substring(0, 6);
            }
            return s;

        }
        return "";
    }
}
