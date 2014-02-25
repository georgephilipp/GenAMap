package JSci.awt;

import javax.swing.event.EventListenerList;

/**
* The AbstractGraphModel class handles the dispatching of
* GraphDataEvents to interested listeners.
* @version 1.2
* @author Mark Hale
*/
public abstract class AbstractGraphModel extends Object {
        private final EventListenerList listenerList = new EventListenerList();
        private final GraphDataEvent dataChangedEvent = new GraphDataEvent(this);

        /**
        * Notifies all listeners that the graph data may have changed.
        */
        protected final void fireGraphDataChanged() {
                fireGraphChanged(dataChangedEvent);
        }
        /**
        * Notifies all listeners that a series may have changed.
        */
        protected final void fireGraphSeriesUpdated(int series) {
                final GraphDataEvent event = new GraphDataEvent(this, series);
                fireGraphChanged(event);
        }
        /**
        * Notifies all listeners of a given graph data event.
        */
        protected final void fireGraphChanged(GraphDataEvent event) {
                final Object listeners[] = listenerList.getListenerList();
                for(int i=listeners.length-2; i>=0; i-=2) {
                        if(listeners[i] == GraphDataListener.class)
                                ((GraphDataListener)listeners[i+1]).dataChanged(event);
                }
        }
        /**
        * Adds a listener to the list that's notified each time a change to the graph data occurs.
        */
        public final void addGraphDataListener(GraphDataListener l) {
                listenerList.add(GraphDataListener.class, l);
        }
        /**
        * Removes a listener from the list that's notified each time a change to the graph data occurs.
        */
        public final void removeGraphDataListener(GraphDataListener l) {
                listenerList.remove(GraphDataListener.class, l);
        }
}

