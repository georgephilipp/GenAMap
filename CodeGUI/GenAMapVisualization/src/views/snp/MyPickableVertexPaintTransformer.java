package views.snp;

import edu.uci.ics.jung.visualization.picking.PickedInfo;
import java.awt.Color;
import java.awt.Paint;
import java.util.Map;
import org.apache.commons.collections15.Transformer;

/**
 * This class takes a vertex and returns what color it should be painted. This
 * is used to color nodes by association. 
 * @author jmoffatt
 */
public class MyPickableVertexPaintTransformer<V> implements Transformer<V, Paint>
{
    /**
     * The map of nodes in the graph back to paint colors to color them
     */
    protected Map<V, Color> fill_paint;
    /**
     * The color that picked nodes should be
     */
    protected Paint picked_paint;
    /**
     * The default paint
     */
    protected Paint default_paint;
    /**
     * What nodes are currently selected
     */
    protected PickedInfo<V> pi;

    /**
     * Constructor
     */
    public MyPickableVertexPaintTransformer(PickedInfo<V> pi,
            Map<V, Color> fill_paint_map, Paint fill_paint_default, Paint picked_paint)
    {
        if (pi == null)
        {
            throw new IllegalArgumentException("PickedInfo instance must be non-null");
        }
        this.pi = pi;
        this.fill_paint = fill_paint_map;
        this.picked_paint = picked_paint;
        default_paint = fill_paint_default;
    }

    /**
     * Transforms the JUNG node v into a paint color for that node.
     * @param v
     * @return
     */
    public Paint transform(V v)
    {
        if (pi.isPicked(v))
        {
            return picked_paint;
        }
        else
        {
            if (fill_paint.get(v) != null)
            {
                return (Paint) (fill_paint.get(v));
            }
            else
            {
                return default_paint;
            }
        }
    }
}
