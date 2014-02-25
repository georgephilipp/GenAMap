package JSci.instruments;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/** A rectangular Region Of Interest
*/

public class RectangularROI extends ROIAdapter {

    private static final int LW = 3;
    private int minx,maxx,miny,maxy;

    public RectangularROI(int x0,int y0,int x1,int y1) {
	minx=x0;
	maxx=x1;
	miny=y0;
	maxy=y1;
    }

    public Shape getShape() {
	return new Rectangle(minx,miny,maxx-minx+1,maxy-miny+1);
    }

    private Component comp = null;
    public void setComponent(Component c) {
	if (comp!=null) {
	    comp.removeMouseListener((MouseListener)this);
	    comp.removeMouseMotionListener((MouseMotionListener)this);
	}
	comp=c;
	if (comp!=null) {
	    comp.addMouseListener((MouseListener)this);
	    comp.addMouseMotionListener((MouseMotionListener)this);
	}
    }

    public void paint(Graphics g) {
	g.setColor(Color.GREEN);
	((Graphics2D)g).draw(getShape());
    }

    private int region(Point p) {
	if (p.x<minx || p.x>maxx || p.y<miny || p.y>maxy) return 0;
	if (p.y<=miny+LW) {
	    if (p.x<=minx+LW) return 1;
	    if (p.x<maxx-LW) return 2;
	    return 3;
	}
	if (p.y<maxy-LW) {
	    if (p.x<=minx+LW) return 4;
	    if (p.x<maxx-LW) return 5;
	    return 6;
	}
	if (p.x<=minx+LW) return 7;
	if (p.x<maxx-LW) return 8;
	return 9;
    }

    private int state,iX,iY,vminx,vmaxx,vminy,vmaxy;

    public void mousePressed(MouseEvent event) {
	state = region(event.getPoint());
	iX=event.getX();
	iY=event.getY();
	vminx=minx;
	vmaxx=maxx;
	vminy=miny;
	vmaxy=maxy;
	comp.repaint();
    }

    public void mouseReleased(MouseEvent event) {
	state = 0;
    }

    public void mouseMoved(MouseEvent event) {
	switch (region(event.getPoint())) {
	case 0:comp.setCursor(Cursor.getDefaultCursor());break;
	case 1:comp.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));break;
	case 2:comp.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));break;
	case 3:comp.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));break;
	case 4:comp.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));break;
	case 5:comp.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));break;
	case 6:comp.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));break;
	case 7:comp.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));break;
	case 8:comp.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));break;
	case 9:comp.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));break;
	}
    }

    private int clip(int min,int x,int max) {
	if (x<min) return min;
	if (x>max) return max;
	return x;
    }

    public void mouseDragged(MouseEvent event) {
	int fX,fY,dX,dY;
	fX=event.getX();
	fY=event.getY();;
	dX=fX-iX;
	dY=fY-iY;
	switch (state) {
	case 0: 
	    minx=Math.min(fX,iX);
	    maxx=Math.max(fX,iX);
	    miny=Math.min(fY,iY);
	    maxy=Math.max(fY,iY);
	    break;
	case 1:
	    minx=clip(0,vminx+dX,maxx);
	    miny=clip(0,vminy+dY,maxy);
	    break;
	case 2:
	    miny=clip(0,vminy+dY,maxy);
	    break;
	case 3:
	    maxx=clip(minx,vmaxx+dX,comp.getWidth()-1);
	    miny=clip(0,vminy+dY,maxy);
	    break;
	case 4:
	    minx=clip(0,vminx+dX,maxx);
	    break;
	case 5:
	    if (vminx+dX<0) dX=-vminx;
	    if (vmaxx+dX>=comp.getWidth()) dX=comp.getWidth()-vmaxx-1;
	    if (vminy+dY<0) dY=-vminy;
	    if (vmaxy+dY>=comp.getHeight()) dY=comp.getHeight()-vmaxy-1;
	    minx=vminx+dX;
	    maxx=vmaxx+dX;
	    miny=vminy+dY;
	    maxy=vmaxy+dY;
	    break;
	case 6:
	    maxx=clip(minx,vmaxx+dX,comp.getWidth()-1);
	    break;
	case 7:
	    minx=clip(0,vminx+dX,maxx);
	    maxy=clip(miny,vmaxy+dY,comp.getHeight()-1);
	    break;
  	case 8:
	    maxy=clip(miny,vmaxy+dY,comp.getHeight()-1);
	    break;
   	case 9:
	    maxx=clip(minx,vmaxx+dX,comp.getWidth()-1);
	    maxy=clip(miny,vmaxy+dY,comp.getHeight()-1);
	    break;
   
	}
	comp.repaint();
    }

}

