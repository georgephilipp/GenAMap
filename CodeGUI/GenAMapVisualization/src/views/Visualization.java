package views;

import java.util.Observable;

/**
 * A visualization holds some way to view the data.
 * AssociationView sends it messages and it draws itself on the JPanel.  All
 * interaction of the viewer is controlled by its own control panel, which
 * it is in charge of creating
 *
 * The view controls itself - adding its own buttons, etc where it sees fit.
 * Some of the controls will come from this class.
 *
 * At this point, there are three primary views - the matrix view, the
 * chromosome view, and the network view.
 *
 * A view should also be observable so that it can receive signals from other
 * views, if necessary.
 * @author rcurtis
 */
public abstract class Visualization extends Observable
{
    /**
     * Visualizations who add a population selection panel need to be able
     * to handle selection events
     * @param popNo
     */
    public abstract void setPop(int popNo);
}
