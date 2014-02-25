package JSci.awt;

import java.awt.*;

/**
* The DoubleBufferedCanvas class provides double buffering functionality.
* Painting events simply cause the offscreen buffer to be painted.
* It is the responsibility of sub-classes to explicitly update the offscreen buffer.
* The offscreen buffer can be updated in two ways.
* <ol>
* <li>Override the {@link #offscreenPaint(Graphics) offscreenPaint} method and use the {@link #redraw() redraw} method. Passive rendering.</li>
* <li>Draw to the graphics context returned by the {@link #getOffscreenGraphics() getOffscreenGraphics} method and use the {@link java.awt.Component#repaint() repaint} method. Active rendering.</li>
* </ol>
* The first way alone should be sufficient for most purposes.
* @version 1.3
* @author Mark Hale
*/
public abstract class DoubleBufferedCanvas extends Canvas {
        private Image buffer = null;
        private boolean doRedraw = true;
        /**
        * Constructs a double buffered canvas.
        */
        public DoubleBufferedCanvas() {}
        /**
        * Paints the canvas using double buffering.
        * @see #offscreenPaint
        */
        public final void paint(Graphics g) {
                if(doRedraw) {
                        doRedraw = false;
                        final int width=getSize().width;
                        final int height=getSize().height;
                        buffer=createImage(width,height);
                        if(buffer == null)
                                return;
                        final Graphics graphics=buffer.getGraphics();
                        /* save original color */
                        Color oldColor = graphics.getColor();
                        graphics.setColor(getBackground());
                        graphics.fillRect(0,0,width,height);
                        /* restore original color */
                        graphics.setColor(oldColor);
                        offscreenPaint(graphics);
                }
                g.drawImage(buffer, 0, 0, null);
        }
        /**
        * Updates the canvas.
        */
        public final void update(Graphics g) {
                paint(g);
        }
        /**
        * Prints the canvas.
        */
        public final void print(Graphics g) {
                offscreenPaint(g);
        }
        /**
        * Redraws the canvas.
        * This method may safely be called from outside the event-dispatching thread.
        */
        public final void redraw() {
                doRedraw = true;
                repaint();
        }
        /**
        * Returns the offscreen graphics context or <code>null</code> if not available.
        */
        protected final Graphics getOffscreenGraphics() {
                return (buffer != null) ? buffer.getGraphics() : null;
        }
        /**
        * Paints the canvas off-screen.
        * Override this method instead of paint(Graphics g).
        */
        protected abstract void offscreenPaint(Graphics g);
}

