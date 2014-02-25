package JSci.swing;

import java.awt.*;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import JSci.awt.*;import JSci.maths.ExtraMath;

/**
* A bar graph Swing component.
* Multiple series are side-by-side.
* @version 1.2
* @author Ismael Orenstein
*/
public class JBarGraph extends JCategoryGraph2D {
        /**
        * Bar colors.
        */
        protected Color barColor[]={Color.blue,Color.green,Color.red,Color.yellow,Color.cyan,Color.lightGray,Color.magenta,Color.orange,Color.pink};
        /**
        * Min and max data points.
        */
        protected float minY,maxY;
        /**
        * Axis scaling.
        */
        private final float yIncPixels = 40.0f;
        private float yInc;
        private boolean autoYInc = true;
        private float xScale,yScale;
        protected int barWidth;
        /**
        * Padding.
        */
        protected final int barPad=0;
        /**
        * Axis numbering.
        */
        protected boolean numbering=true;
        protected NumberFormat yNumberFormat = new DecimalFormat("##0.0");
        /**
        * Constructs a bar graph.
        */
        public JBarGraph(CategoryGraph2DModel cgm) {
                super(cgm);
                dataChanged(new GraphDataEvent(model));
        }
        /**
        * Implementation of GraphDataListener.
        * Application code will not use this method explicitly, it is used internally.
        */
        public void dataChanged(GraphDataEvent e) {
                minY=0.0f;
                maxY=Float.NEGATIVE_INFINITY;
                model.firstSeries();
                do {
                        for(int i=0;i<model.seriesLength();i++) {
                                float tmp=model.getValue(i);
                                minY=Math.min(tmp,minY);
                                maxY=Math.max(tmp,maxY);
                        }
                } while(model.nextSeries());
                if(minY==maxY) {
                        minY-=0.5f;
                        maxY+=0.5f;
                }
                setNumbering(numbering);
        }
        /**
        * Sets the bar color of the nth series.
        * @param n the index of the series.
        * @param c the line color.
        */
        public final void setColor(int n,Color c) {
                barColor[n]=c;
                redraw();
        }
        /**
        * Gets the bar color of the nth series.
        * @param n the index of the series.
        */
        public final Color getColor(int n) {
                return barColor[n];
        }
        /**
        * Turns axis numbering on/off.
        */
        public final void setNumbering(boolean flag) {
                numbering=flag;
                leftAxisPad=axisPad;
                if(numbering && getFont() != null) {
                        // adjust leftAxisPad to accomodate y-axis numbering
                        final FontMetrics metrics = getFontMetrics(getFont());
                        final int maxYNumLen = metrics.stringWidth(yNumberFormat.format(maxY));
                        final int minYNumLen = metrics.stringWidth(yNumberFormat.format(minY));
                        final int yNumPad = Math.max(minYNumLen, maxYNumLen);
                        leftAxisPad+=yNumPad;
                }
                rescale();
        }
        public void addNotify() {
                super.addNotify();
                // getFont() is now not null
                // recalculate padding
                setNumbering(numbering);
        }
        /**
        * Sets the display format used for axis numbering.
        * Convenience method.
        * @see #setYNumberFormat(NumberFormat)
        */
        public final void setNumberFormat(NumberFormat format) {
                yNumberFormat = format;
                setNumbering(numbering);
        }
        /**
        * Sets the display format used for y-axis numbering.
        */
        public final void setYNumberFormat(NumberFormat format) {
                yNumberFormat = format;
                setNumbering(numbering);
        }
        /**
        * Sets the y-axis numbering increment.
        * @param dy use 0.0f for auto-adjusting (default).
        */
        public final void setYIncrement(float dy) {
                if(dy < 0.0f) {
                        throw new IllegalArgumentException("Increment should be positive.");
                } else if(dy == 0.0f) {
                        if(!autoYInc) {
                                autoYInc = true;
                                rescale();
                        }
                } else {
                        autoYInc = false;
                        if(dy != yInc) {
                                yInc = dy;
                                rescale();
                        }
                }
        }
        /**
        * Returns the y-axis numbering increment.
        */
        public final float getYIncrement() {
                return yInc;
        }
	public final float getYMinimum() {
		return minY;
	}
	public final float getYMaximum() {
		return maxY;
	}
        /**
        * Draws the graph axes.
        */
        protected final void drawAxes(Graphics g) {
                g.setColor(getForeground());
// axis - Swing optimised
                if(minY > 0.0f) {
                        g.drawLine(leftAxisPad-scalePad,getHeight()-axisPad,getWidth()-(axisPad-scalePad),getHeight()-axisPad);
                } else {
                        g.drawLine(leftAxisPad-scalePad,origin.y,getWidth()-(axisPad-scalePad),origin.y);
                }
                g.drawLine(origin.x,axisPad-scalePad,origin.x,getHeight()-(axisPad-scalePad));
// x-axis labels
                for(int x=0; x<model.seriesLength(); x++) {
			drawXLabel(g, x);
                }
// numbering
                if(numbering) {
// y-axis numbering
                        for(double y=yInc; y<=maxY; y+=yInc) {
				drawYLabel(g, (float) y);
                        }
                        for(double y=-yInc; y>=minY; y-=yInc) {
				drawYLabel(g, (float) y);
                        }
                }
        }
	protected void drawXLabel(Graphics g, int x) {
		Point p = dataToScreen(x+1.0f, 0.0f);
		String str = model.getCategory(x);
                FontMetrics metrics = g.getFontMetrics();
		int strWidth = metrics.stringWidth(str);
                int strHeight = metrics.getHeight();
		boolean numberingAbove = (maxY <= 0.0f);
		if(numberingAbove) {
	                g.drawLine(p.x,p.y,p.x,p.y-5);
			g.drawString(str, dataToScreen(x+0.5f-0.5f*strWidth/xScale, 0.0f).x, origin.y-5);
		} else {
			g.drawLine(p.x,p.y,p.x,p.y+5);
			g.drawString(str, dataToScreen(x+0.5f-0.5f*strWidth/xScale, 0.0f).x, origin.y+strHeight);
		}
	}
	protected void drawYLabel(Graphics g, float y) {
		Point p = dataToScreen(0.0f, y);
		String str = yNumberFormat.format(y);
                FontMetrics metrics = g.getFontMetrics();
		int strWidth = metrics.stringWidth(str);
                int strHeight = metrics.getHeight();
		g.drawLine(p.x, p.y, p.x-5, p.y);
		g.drawString(str, p.x-8-strWidth, p.y+strHeight/3);
	}
        /**
        * Draws the graph bars.
        */
        protected void drawBars(Graphics g) {
// bars
                int numSeries=1;
                model.firstSeries();
                while(model.nextSeries())
                        numSeries++;
                if(numSeries==1) {
                        for(int i=0;i<model.seriesLength();i++)
                                drawBar(g, i, model.getValue(i), barColor[0], barWidth, 0);
                } else {
                        final float subBarWidth = ((float)barWidth)/((float)numSeries);
                        final int subBarPixelWidth = Math.round(subBarWidth);
                        for(int i=0;i<model.seriesLength();i++) {
                                // draw
                                model.firstSeries();
                                for(int j=0;j<numSeries;j++) {
                                        drawBar(g, i, model.getValue(i), barColor[j], subBarPixelWidth, Math.round(j*subBarWidth));
                                        model.nextSeries();
                                }
                        }
                }
        }
        /**
        * Draws a bar.
        */
        private void drawBar(Graphics g, int pos, float value, Color color, int width, int xoffset) {
                final Point p = dataToScreen(pos, value);
                final int y = (value<0.0f ? origin.y : p.y);
                final int dy=Math.abs(p.y-origin.y);
                g.setColor(color);
                g.fillRect(p.x+barPad+xoffset, y, width, dy);
                g.setColor(Color.black);
                g.drawRect(p.x+barPad+xoffset, y, width, dy);
        }
        /**
        * Paint the graph.
        */
        protected void offscreenPaint(Graphics g) {
                drawBars(g);
                drawAxes(g);
        }
        /**
        * Reshapes the bar graph to the specified bounding box.
        */
        public final void setBounds(int x,int y,int width,int height) {
                super.setBounds(x,y,width,height);
                rescale();
        }
        /**
        * Rescales the bar graph.
        */
        protected final void rescale() {
                final Dimension minSize=getMinimumSize();
                // Swing optimised
                final int thisWidth=Math.max(getWidth(),minSize.width);
                final int thisHeight=Math.max(getHeight(),minSize.height);
                xScale = (float)(thisWidth-(leftAxisPad+axisPad)) / (float)model.seriesLength();
			float deltaY;
			if(minY > 0.0f)
				deltaY = maxY;
			else if(maxY < 0.0)
				deltaY = -minY;
			else
				deltaY = maxY-minY;
                yScale = (float) ((double)(thisHeight-2*axisPad) / (double)(deltaY));
                if(autoYInc) {
                        yInc = (float) ExtraMath.round((double)yIncPixels/(double)yScale, 1);
                        if(yInc == 0.0f)
                                yInc = Float.MIN_VALUE;
                }
                barWidth=Math.round(xScale-2*barPad);
                origin.x=leftAxisPad;
                origin.y=thisHeight-axisPad+Math.round(minY*yScale);
                redraw();
        }
        /**
        * Converts a data point to screen coordinates.
        */
        protected final Point dataToScreen(float x,float y) {
                return new Point(origin.x+Math.round(xScale*x),origin.y-Math.round(yScale*y));
        }
        /**
        * Converts a screen point to data coordinates.
        */
        protected final Point2D.Float screenToData(Point p) {
                double x = (double)(p.x-origin.x) / (double)xScale;
                double y = (double)(origin.y-p.y) / (double)yScale;
                return new Point2D.Float((float)x, (float)y);
        }
}
