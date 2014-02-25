package JSci.swing;

import java.awt.*;
import JSci.awt.*;
/**
* A pie chart Swing component.
* @version 1.2
* @author Mark Hale
*/
public class JPieChart extends JCategoryGraph2D {
        /**
        * Total value of pie.
        */
        private float pieTotal;
        /**
        * Slice colors.
        */
        protected Color sliceColor[]={Color.blue,Color.green,Color.red,Color.yellow,Color.cyan,Color.lightGray,Color.magenta,Color.orange,Color.pink};
        /**
        * Constructs a pie chart.
        */
        public JPieChart(CategoryGraph2DModel cgm) {
                super(cgm);
                dataChanged(new GraphDataEvent(model));
        }
        /**
        * Implementation of GraphDataListener.
        * Application code will not use this method explicitly, it is used internally.
        */
        public void dataChanged(GraphDataEvent e) {
                model.firstSeries();
                final int len=model.seriesLength();
                pieTotal=0.0f;
                for(int i=0;i<len;i++)
                        pieTotal+=model.getValue(i);
                if(len>sliceColor.length) {
                        Color tmp[]=sliceColor;
                        sliceColor=new Color[len];
                        System.arraycopy(tmp,0,sliceColor,0,tmp.length);
                        for(int i=tmp.length;i<sliceColor.length;i++)
                                sliceColor[i]=sliceColor[i-tmp.length];
                }
                rescale();
        }
        /**
        * Sets the slice color of the nth slice.
        * @param n the index of the slice.
        * @param c the slice color.
        */
        public final void setColor(int n,Color c) {
                sliceColor[n]=c;
        }
        /**
        * Gets the slice color of the nth slice.
        * @param n the index of the slice.
        */
        public final Color getColor(int n) {
                return sliceColor[n];
        }
        /**
        * Reshapes the JPieChart to the specified bounding box.
        */
        public final void setBounds(int x,int y,int width,int height) {
                super.setBounds(x,y,width,height);
                rescale();
        }
        /**
        * Rescales the JPieChart.
        */
        protected final void rescale() {
                // Swing optimised
                origin.x=getWidth()/2;
                origin.y=getHeight()/2;
                redraw();
        }
        /**
        * Paint the graph.
        */
        protected void offscreenPaint(Graphics g) {
                final int width=2*(origin.x-axisPad);
                final int height=2*(origin.y-axisPad);
                final double xRadius=0.4*width;
                final double yRadius=0.4*height;
// slices
                int angle=0;
                model.firstSeries();
                final int n = model.seriesLength();
                if(n > 0) {
                        for(int i=0; i<n-1; i++) {
                                g.setColor(sliceColor[i]);
                                int arcAngle=Math.round(model.getValue(i)*360.0f/pieTotal);
                                g.fillArc(axisPad,axisPad,width,height,angle,arcAngle);
                                angle+=arcAngle;
                        }
                        g.setColor(sliceColor[n-1]);
                        g.fillArc(axisPad,axisPad,width,height,angle,360-angle);
                }
// border
                g.setColor(getForeground());
                g.drawArc(axisPad,axisPad,width,height,0,360);
// labels
                final int strHalfHeight=g.getFontMetrics().getHeight()/2;
                double dAngle=0.0;
                g.setColor(getForeground());
                for(int i=0; i<n; i++) {
                        double dHalfArcAngle=model.getValue(i)*Math.PI/pieTotal;
                        dAngle+=dHalfArcAngle;
			String str = model.getCategory(i);
			int strHalfWidth=g.getFontMetrics().stringWidth(str)/2;
			g.drawString(str,origin.x-strHalfWidth+(int)(xRadius*Math.cos(dAngle)),origin.y+strHalfHeight-(int)(yRadius*Math.sin(dAngle)));
                        dAngle+=dHalfArcAngle;
                }
        }
}
