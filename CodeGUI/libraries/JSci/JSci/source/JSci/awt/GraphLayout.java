package JSci.awt;

import java.awt.*;

/**
* A graph layout arranges components in the style of a graph.
* Available regions are:
* <code>Title</code>,
* <code>Graph</code> (default),
* <code>X-axis</code>,
* <code>Y-axis</code>.
* @version 0.5
* @author Mark Hale
*/
public final class GraphLayout implements LayoutManager2 {
        public final static String TITLE = "Title";
        public final static String GRAPH = "Graph";
        public final static String X_AXIS = "X-axis";
        public final static String Y_AXIS = "Y-axis";
        private Component title,graph,xaxis,yaxis;

        public GraphLayout() {}
        public void addLayoutComponent(String name,Component comp) {
                if(TITLE.equals(name))
                        title=comp;
                else if(GRAPH.equals(name) || name == null)
                        graph=comp;
                else if(X_AXIS.equals(name))
                        xaxis=comp;
                else if(Y_AXIS.equals(name))
                        yaxis=comp;
        }
        public void addLayoutComponent(Component comp,Object constraint) {
		String name = (constraint != null ? constraint.toString() : null);
                addLayoutComponent(name, comp);
        }
        public void removeLayoutComponent(Component comp) {
                if(comp == title)
                        title=null;
                if(comp == graph)
                        graph=null;
                if(comp == xaxis)
                        xaxis=null;
                if(comp == yaxis)
                        yaxis=null;
        }
        public void layoutContainer(Container parent) {
                synchronized(parent.getTreeLock()) {
                        Dimension size=parent.getSize();
                        Insets insets=parent.getInsets();
                        final int width=size.width-insets.left-insets.right;
                        final int height=size.height-insets.top-insets.bottom;
                        int graphLeftPad=0,graphAxisPad=0;
                        if(graph instanceof Graph2D) {
                                graphLeftPad=((Graph2D)graph).leftAxisPad;
                                graphAxisPad=((Graph2D)graph).axisPad;
                        } else if(graph instanceof CategoryGraph2D) {
                                graphLeftPad=((CategoryGraph2D)graph).leftAxisPad;
                                graphAxisPad=((CategoryGraph2D)graph).axisPad;
                        }
                        int yaxisWidth=getMinimumSize(yaxis).width;
                        int graphWidth=width-yaxisWidth;
                        int titleWidth=graphWidth-graphLeftPad-graphAxisPad;
                        int xaxisWidth=titleWidth;
                        int titleHeight=getMinimumSize(title).height;
                        int xaxisHeight=getMinimumSize(xaxis).height;
                        int graphHeight=height-titleHeight-xaxisHeight;
                        int yaxisHeight=graphHeight-2*graphAxisPad;
                        if(title!=null)
                                title.setBounds(insets.left+yaxisWidth+graphLeftPad,insets.top,titleWidth,titleHeight);
                        if(graph!=null)
                                graph.setBounds(insets.left+yaxisWidth,insets.top+titleHeight,graphWidth,graphHeight);
                        if(yaxis!=null)
                                yaxis.setBounds(insets.left,insets.top+titleHeight+graphAxisPad,yaxisWidth,yaxisHeight);
                        if(xaxis!=null)
                                xaxis.setBounds(insets.left+yaxisWidth+graphLeftPad,height-xaxisHeight,xaxisWidth,xaxisHeight);
                }
        }
        public void invalidateLayout(Container parent) {}
        public float getLayoutAlignmentX(Container parent) {
                return 0.5f;
        }
        public float getLayoutAlignmentY(Container parent) {
                return 0.5f;
        }
        public Dimension minimumLayoutSize(Container parent) {
                return calculateLayoutSize(parent.getInsets(),
                        getMinimumSize(title),
                        getMinimumSize(graph),
                        getMinimumSize(xaxis),
                        getMinimumSize(yaxis));
        }
        private static Dimension getMinimumSize(Component comp) {
                if(comp==null)
                        return new Dimension();
                else
                        return comp.getMinimumSize();
        }
        public Dimension preferredLayoutSize(Container parent) {
                return calculateLayoutSize(parent.getInsets(),
                        getMinimumSize(title),
                        getPreferredSize(graph),
                        getMinimumSize(xaxis),
                        getMinimumSize(yaxis));
        }
        private static Dimension getPreferredSize(Component comp) {
                if(comp==null)
                        return new Dimension();
                else
                        return comp.getPreferredSize();
        }
        public Dimension maximumLayoutSize(Container parent) {
                return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
        private static Dimension calculateLayoutSize(Insets insets,Dimension title,Dimension graph,Dimension xaxis,Dimension yaxis) {
                int width = insets.left+insets.right;
                width += Math.max(title.width, yaxis.width+graph.width);
                int height = insets.top+insets.bottom;
                height += title.height+xaxis.height;
                height += Math.max(yaxis.height, graph.height);
                return new Dimension(width,height);
        }
}

