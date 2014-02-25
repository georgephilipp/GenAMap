package JSci.instruments;

import java.awt.*;

/**
* An object that privides Components that control or show something
* about it.
*/
public interface Control {
    /** @return the component that controls the object that implements
	this interface */
    Component getControlComponent();
}
