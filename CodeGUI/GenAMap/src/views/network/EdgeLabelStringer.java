package views.network;

import datamodel.Edge;
import org.apache.commons.collections15.Transformer;
/**
 * This class is used to show the edge weight labels on a graph.
 *
 * @author James Moffatt
 *
 */
class EdgeLabelStringer implements Transformer<Edge,String> {
    
    boolean enabled=true;

    public EdgeLabelStringer(boolean e)
    {
        super();
        enabled=e;
    }

    /**
     *
     * 
     * @param Number e: Number to be tranformed into a string
     * @return the string form of the number represetned by e
     */
    public String transform(Edge e)
    {
        if(e==null)
            throw new NullPointerException();

        if(enabled) {
            String s = String.valueOf(e.weight);
            if(s.indexOf('E') > -1)
            {
                if(s.length() > 4)
                {
                    return s.substring(0, 4) + s.substring(s.indexOf('E'));
                }
                else
                {
                    return s;
                }
            }
            if(s.length() > 5)
                return s.substring(0, 6);
            return s;

        }
        return "";
    }
}