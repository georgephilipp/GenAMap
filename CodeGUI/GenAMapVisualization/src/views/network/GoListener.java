package views.network;

import java.awt.Color;

/**
 * To implement this interface, we handle changes to the GO selection and
 * color by that selection
 * @author rcurtis
 */
public interface GoListener
{
    /**
     * We learn what category has been selected and know to color by that color
     * @param cat
     */
    public void colorByCat(String cat, Color c);
}
