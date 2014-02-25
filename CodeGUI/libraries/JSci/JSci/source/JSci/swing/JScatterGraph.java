package JSci.swing;

import java.awt.*;
import JSci.awt.*;

/**
* A scatter graph Swing component.
* @version 1.3
* @author Mark Hale
*/
public class JScatterGraph extends JGraph2D {
        /**
        * Constructs a scatter graph.
        */
        public JScatterGraph(Graph2DModel gm) {
                super(gm);
                dataMarker = new Graph2D.DataMarker.Square(3);
        }
}

