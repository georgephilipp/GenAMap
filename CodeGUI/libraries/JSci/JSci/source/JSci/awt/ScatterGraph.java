package JSci.awt;

import java.awt.*;

/**
* A scatter graph AWT component.
* @version 1.3
* @author Mark Hale
*/
public class ScatterGraph extends Graph2D {
        /**
        * Constructs a scatter graph.
        */
        public ScatterGraph(Graph2DModel gm) {
                super(gm);
                dataMarker = new Graph2D.DataMarker.Square(3);
        }
}

