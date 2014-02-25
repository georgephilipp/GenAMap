package views.snp;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections15.Transformer;
import views.network.threeway.ThreeWayGeneGraphObject;
import views.network.threeway.ThreeWayTraitGraphObject;

/**
 * This class was downloaded to transform a label into a string for the
 * three way network visualization.
     * A simple implementation of VertexStringer that
     * gets Vertex labels from a Map
     *
     * @author Tom Nelson
     *
     *
     */
public class VertexStringerImpl<V> implements Transformer<V,String>
{

    Map<V,String> map = new HashMap<V,String>();

    boolean enabled = true;
    public boolean traitenabled = true;
    public boolean geneenabled = true;

    public VertexStringerImpl(Map<V,String> map)
    {
        this.map = map;
    }

    /**
     * @see edu.uci.ics.jung.graph.decorators.VertexStringer#getLabel(edu.uci.ics.jung.graph.Vertex)
     */
    public String transform(V v) {
        if(isEnabled() ||
                (traitenabled && v instanceof ThreeWayTraitGraphObject) ||
                (geneenabled && v instanceof ThreeWayGeneGraphObject)) {
            return map.get(v);
        } else {
            return "";
        }
    }

    /**
     * @return Returns the enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled The enabled to set.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}