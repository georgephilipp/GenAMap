package JSci.awt;

import java.awt.geom.Point2D;
import java.util.List;

/**
* The Point2DListModel provides a convenient implementation of
* the Graph2DModel interface based upon a List of Point2D objects.
* @version 1.0
* @author Mark Hale
*/
public final class Point2DListModel extends AbstractGraphModel implements Graph2DModel {
        private List data;

        public Point2DListModel() {}
        /**
        * Sets the list of points to be plotted.
        */
        public void setData(List points) {
                data = points;
                fireGraphDataChanged();
        }

// Graph2DModel interface

        public float getXCoord(int i) {
                Point2D p = (Point2D) data.get(i);
                return (float) p.getX();
        }
        public float getYCoord(int i) {
                Point2D p = (Point2D) data.get(i);
                return (float) p.getY();
        }
        public int seriesLength() {
                return data.size();
        }
        public void firstSeries() {}
        public boolean nextSeries() {
                return false;
        }
}

