package JSci.instruments;

import java.awt.*;

/** An object that has a Dimension */

public interface Dimensions {
 
    /** @return the width of the image */
    int getWidth();
    
    /** @return the height of the image */
    int getHeight();
    
    /** @return the dimension of the image */
    Dimension getSize();

}
