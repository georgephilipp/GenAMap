package views.snp;

import datamodel.Marker;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;
import java.awt.Polygon;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * When a SNP has been called to mark associated traits, it needs to transform
 * shapes so that it is clear which SNPs have been selected. Here, we can keep
 * track of those SNPs that need to be changed into another shape. Otherwise, we
 * just return the standard circle. 
 * @author rcurtis
 */
public class ChangableEllipseVertexShapeTransformer extends EllipseVertexShapeTransformer<Integer> implements Observer
{
    /**
     * The markers that should be a different shape than all the rest!
     */
    private Set<Marker> differentMarkers;
    private ArrayList<ArrayList<Marker>> groups;

    /**
     * Sets the different marker collection that will be drawn differently.
     */
    public void setDifferentMakers(Set<Integer> markers, ArrayList<ArrayList<Marker>> groups)
    {
        this.groups = groups;
        this.differentMarkers = new HashSet<Marker>();
        for (Integer i : markers)
        {
            differentMarkers.addAll(groups.get(i));
        }
    }

    @Override
    public Shape transform(Integer v)
    {
        if (groups == null || groups.size() <= v || v < 0 || differentMarkers == null)
        {
            return super.transform(v);
        }
        ArrayList<Marker> mymarkers = groups.get(v);
        boolean hasit = false;

        for (Marker m : mymarkers)
        {
            if (differentMarkers.contains(m))
            {
                hasit = true;
            }
        }
        if (differentMarkers == null || !hasit)
        {
            return super.transform(v);
        }
        int sz = (int) (2.0 * (double) 1 / 2.0) + 3;
        Polygon poly = new Polygon();
        poly.addPoint(-1 * sz, 3);
        poly.addPoint(sz, 3);
        poly.addPoint(0, -1 * sz * 2 + 3);
        return poly;
    }

    public void update(Observable o, Object arg)
    {
        this.groups = (ArrayList<ArrayList<Marker>>) arg;
    }
}
