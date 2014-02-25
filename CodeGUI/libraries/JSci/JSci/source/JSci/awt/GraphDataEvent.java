package JSci.awt;

import java.util.EventObject;

/**
* Defines an event that encapsulates changes to a graph.
* @version 1.1
* @author Mark Hale
*/
public final class GraphDataEvent extends EventObject {
        /**
        * Specifies all series.
        */
        public static final int ALL_SERIES = -1;
        
        private final int series;
        private final boolean isIncremental;
        
        /**
        * All series data in the graph has changed.
        */
        public GraphDataEvent(Object src) {
                this(src, ALL_SERIES, false);
        }
        /**
        * This series has changed.
        * @param seriesChanged The index of the series that may have changed.
        */
        public GraphDataEvent(Object src, int seriesChanged) {
                this(src, seriesChanged, false);
        }
        /**
        * This series has changed incrementally.
        * Useful for streaming data to a graph.
        * @param seriesChanged The index of the series that may have changed.
        * @param isIncrementalChange True indicates an extra data point has been added.
        */
        public GraphDataEvent(Object src, int seriesChanged, boolean isIncrementalChange) {
                super(src);
                series = seriesChanged;
                isIncremental = isIncrementalChange;
        }
        /**
        * Returns the series that has changed.
        * @return The index of the series that may have changed or ALL_SERIES.
        */
        public int getSeries() {
                return series;
        }
        /**
        * Returns whether the change was incremental.
        * @return True if an extra data point has been added.
        */
        public boolean isIncremental() {
                return isIncremental;
        }
}

