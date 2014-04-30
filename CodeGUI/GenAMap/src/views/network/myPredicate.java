package views.network;

import org.apache.commons.collections15.Predicate;

/**
 * We don't want any edges in the tree!!
 * @author RCurtis
 */
public class myPredicate implements Predicate
{
    public boolean evaluate(Object t)
    {
        return false;
    }
}
