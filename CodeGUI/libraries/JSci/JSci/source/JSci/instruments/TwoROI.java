package JSci.instruments;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * A ROI made by two rectangular regions, one inside the other (two levels of interest)
 */
public class TwoROI extends ROIAdapter implements Control {
    private Rectangle template = new Rectangle(0,0,-2,-2);
    private Rectangle search = new Rectangle(0,0,-2,-2);
    private int factor = 1;
    private boolean useFactor = true;
    private static final int LW = 3;
    private int state;
    private int iX;
    private int iY;


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


    /**
     * draws the ROI. Doesn't overwrite the background; call it after the other paint operations
     */
    public void paint(Graphics g) {
	Graphics2D g2 = (Graphics2D)g;
	g2.setPaint(Color.GREEN);
	g2.draw(search);
	g2.setPaint(Color.MAGENTA);
	g2.draw(template);
    }

    /**
     * Use this to select the behaviour of the outer rectangle
     * @param u The outer rectangle is n-times the inner one?
     */
    public void setUseFactor(boolean u) { 
	useFactor = u;
	if (useFactor) search.setFrame(
				       template.getMinX()-factor*template.getWidth(),
				       template.getMinY()-factor*template.getHeight(),
				       (2*factor+1)*template.getWidth(),
				       (2*factor+1)*template.getHeight());
	if (comp!=null) comp.repaint();
    }


    /**
     * Use this to select the behaviour of the outer rectangle
     * @param f The outer rectangle is f-times the inner one.
     */
    public void setFactor(int f) { 
	factor = f;
	if (useFactor) search.setFrame(
				       template.getMinX()-factor*template.getWidth(),
				       template.getMinY()-factor*template.getHeight(),
				       (2*factor+1)*template.getWidth(),
				       (2*factor+1)*template.getHeight());
	if (comp!=null) comp.repaint();
    }


    /**
     * @return the inner rectangle
     */
    public Shape getShape() {
        return (Rectangle)template.clone();
    }

    /**
     * @return the outer rectangle.
     */
    public Rectangle getOuterRectangle() { return (Rectangle)search.clone(); }

    /**
     * Testing...
     */
    public static void main(String[] args) {
        final ROI r = new TwoROI();
        Player p = new Player();
        JSci.instruments.ImageSource fs = new TestImageSource();
	ImageFilter filter = new ImageFilterAdapter() { 
		public void filter(Image f) {} 
		public Component getFilterControlComponent() { return ((TwoROI)r).getControlComponent(); }
	    };
        fs.setSink(filter);
	filter.setSink(p);
        p.addROI(r);
        p.start();
    }


    JPanel ccomp=null;
    /** @return a component that controls this ROI */ 
    public Component getControlComponent() {
	if (ccomp!=null) return ccomp;
	ccomp=new JPanel();
	ccomp.setLayout(new BorderLayout());
	final JCheckBox modifySearchArea;
	final JSlider factor;
	modifySearchArea = new JCheckBox("Modify search area",false);
	modifySearchArea.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    setUseFactor(!modifySearchArea.isSelected());
		}
	    });
	factor = new JSlider(1,5,1);
	setFactor(1);
	factor.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
		    setFactor(factor.getValue());
		}
	    });
	ccomp.add(BorderLayout.NORTH,modifySearchArea);
	ccomp.add(BorderLayout.SOUTH,factor);
	return ccomp;
    }

    private int region(Point2D p) {
	if (p.getX()>=template.getMinX()-LW &&
	    p.getX()<=template.getMaxX()+LW &&
	    p.getY()>=template.getMinY()-LW &&
	    p.getY()<=template.getMaxY()+LW ) {
	    if (p.getX()<=template.getMinX()) {
		if (p.getY()<=template.getMinY()) return 10;
		if (p.getY()<template.getMaxY()) return 17;
		return 16;
	    }
	    if (p.getX()<template.getMaxX()) {
		if (p.getY()<=template.getMinY()) return 11;
		if (p.getY()<template.getMaxY()) return 1;
		return 15;
	    }
	    if (p.getY()<=template.getMinY()) return 12;
	    if (p.getY()<template.getMaxY()) return 13;
	    return 14;
	}
	if (useFactor) {
	    if (search.contains(p)) return 1;
	    else return 0;
	}
	else {
	    if (p.getX()>=search.getMinX()-LW &&
		p.getX()<=search.getMaxX()+LW &&
		p.getY()>=search.getMinY()-LW &&
		p.getY()<=search.getMaxY()+LW ) {
		if (p.getX()<=search.getMinX()) {
		    if (p.getY()<=search.getMinY()) return 2;
		    if (p.getY()<search.getMaxY()) return 9;
		    return 8;
		}
		if (p.getX()<search.getMaxX()) {
		    if (p.getY()<=search.getMinY()) return 3;
		    if (p.getY()<search.getMaxY()) return 1;
		    return 7;
		}
		if (p.getY()<=search.getMinY()) return 4;
		if (p.getY()<search.getMaxY()) return 5;
		return 6;
	    }
	    else
		return 0;
	}
    }

    public void mousePressed(MouseEvent event) {
	state = region(event.getPoint());
	iX=event.getX();
	iY=event.getY();
	if (state==0) {
	    template.setFrame(iX,iY,0,0);
	    search.setFrame(iX,iY,0,0);
	}
	if (comp!=null) comp.repaint();
    }

    public void mouseReleased(MouseEvent event) {
	state = 0;
    }
  
    public void mouseMoved(MouseEvent event) {
	switch (region(event.getPoint())) {
	case 0:comp.setCursor(Cursor.getDefaultCursor());break;
	case 1:comp.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));break;
	case 2:comp.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));break;

	case 3:comp.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));break;

	case 4:comp.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));break;

	case 5:comp.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));break;

	case 6:comp.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));break;

	case 7:comp.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));break;

	case 8:comp.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));break;

	case 9:comp.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));break;

	case 10:comp.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));break;

	case 11:comp.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));break;

	case 12:comp.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));break;

	case 13:comp.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));break;

	case 14:comp.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));break;

	case 15:comp.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));break;

	case 16:comp.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));break;

	case 17:comp.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));break;

	}
    }
    public void mouseDragged(MouseEvent event) {
	int dX,dY,minX,minY;
	if (useFactor) switch (state) {
	case 0:
	    minX=(iX<event.getX())?iX:event.getX();
	    minY=(iY<event.getY())?iY:event.getY();
	    dX=iX-event.getX();if (dX<0) dX=-dX;
	    dY=iY-event.getY();if (dY<0) dY=-dY;
	    template.setFrame(minX,minY,dX,dY);
	    break;
	case 1:
	    template.setFrame(template.getMinX()+event.getX()-iX,template.getMinY()+event.getY()-iY,template.getWidth(),template.getHeight());
	    iX=event.getX();
	    iY=event.getY();
	    break;
	case 10:
	    dX=event.getX()-iX;
	    dY=event.getY()-iY;
	    if (template.getMinX()+dX>template.getMaxX()) 
		dX=(int)(template.getMaxX()-template.getMinX());
	    if (template.getMinY()+dY>template.getMaxY()) 
		dY=(int)(template.getMaxY()-template.getMinY());
	    template.setFrame(template.getMinX()+dX,template.getMinY()+dY,template.getWidth()-dX,template.getHeight()-dY);
	    iX=event.getX();
	    iY=event.getY();
	    break;
	case 11:
	    dY=event.getY()-iY;
	    if (template.getMinY()+dY>template.getMaxY()) 
		dY=(int)(template.getMaxY()-template.getMinY());
	    template.setFrame(template.getMinX(),template.getMinY()+dY,template.getWidth(),template.getHeight()-dY);
	    iY=event.getY();
	    break;
	case 12:
	    dX=event.getX()-iX;
	    dY=event.getY()-iY;
	    if (template.getMaxX()+dX<template.getMinX()) 
		dX=(int)(template.getMinX()-template.getMaxX());
	    if (template.getMinY()+dY>template.getMaxY()) 
		dY=(int)(template.getMaxY()-template.getMinY());
	    template.setFrame(template.getMinX(),template.getMinY()+dY,template.getWidth()+dX,template.getHeight()-dY);
	    iX=event.getX();
	    iY=event.getY();
	    break;
	case 13:
	    dX=event.getX()-iX;
	    if (template.getMaxX()+dX<template.getMinX()) 
		dX=(int)(template.getMinX()-template.getMaxX());
	    template.setFrame(template.getMinX(),template.getMinY(),template.getWidth()+dX,template.getHeight());
	    iX=event.getX();
	    break;
	case 14:
	    dX=event.getX()-iX;
	    dY=event.getY()-iY;
	    if (template.getMaxX()+dX<template.getMinX()) 
		dX=(int)(template.getMinX()-template.getMaxX());
	    if (template.getMaxY()+dY<template.getMinY()) 
		dY=(int)(template.getMinY()-template.getMaxY());
	    template.setFrame(template.getMinX(),template.getMinY(),template.getWidth()+dX,template.getHeight()+dY);
	    iX=event.getX();
	    iY=event.getY();
	    break;
	case 15:
	    dY=event.getY()-iY;
	    if (template.getMaxY()+dY<template.getMinY()) 
		dY=(int)(template.getMinY()-template.getMaxY());
	    template.setFrame(template.getMinX(),template.getMinY(),template.getWidth(),template.getHeight()+dY);
	    iY=event.getY();
	    break;
	case 16:
	    dY=event.getY()-iY;
	    dX=event.getX()-iX;
	    if (template.getMinX()+dX>template.getMaxX()) 
		dX=(int)(template.getMaxX()-template.getMinX());
	    if (template.getMaxY()+dY<template.getMinY()) 
		dY=(int)(template.getMinY()-template.getMaxY());
	    template.setFrame(template.getMinX()+dX,template.getMinY(),template.getWidth()-dX,template.getHeight()+dY);
	    iX=event.getX();
	    iY=event.getY();
	    break;
	case 17:
	    dX=event.getX()-iX;
	    if (template.getMinX()+dX>template.getMaxX()) 
		dX=(int)(template.getMaxX()-template.getMinX());
	    template.setFrame(template.getMinX()+dX,template.getMinY(),template.getWidth()-dX,template.getHeight());
	    iX=event.getX();
	    break;

	}
	if (!useFactor) switch (state) {
	case 0:
	    minX=(iX<event.getX())?iX:event.getX();
	    minY=(iY<event.getY())?iY:event.getY();
	    dX=iX-event.getX();if (dX<0) dX=-dX;
	    dY=iY-event.getY();if (dY<0) dY=-dY;
	    template.setFrame(minX,minY,dX,dY);
	    search.setFrame(minX-factor*dX,minY-factor*dY,(2*factor+1)*dX,(2*factor+1)*dY);
	    break;
	case 1:
	    template.setFrame(template.getMinX()+event.getX()-iX,template.getMinY()+event.getY()-iY,template.getWidth(),template.getHeight());
	    search.setFrame(search.getMinX()+event.getX()-iX,search.getMinY()+event.getY()-iY,search.getWidth(),search.getHeight());
	    iX=event.getX();
	    iY=event.getY();
	    break;
	case 2:
	    dX=event.getX()-iX;
	    dY=event.getY()-iY;
	    if (search.getMinX()+dX>template.getMinX()) 
		dX=(int)(template.getMinX()-search.getMinX());
	    if (search.getMinY()+dY>template.getMinY()) 
		dY=(int)(template.getMinY()-search.getMinY());
	    search.setFrame(search.getMinX()+dX,search.getMinY()+dY,search.getWidth()-dX,search.getHeight()-dY);
	    iX=event.getX();
	    iY=event.getY();
	    break;
	case 3:
	    dY=event.getY()-iY;
	    if (search.getMinY()+dY>template.getMinY()) 
		dY=(int)(template.getMinY()-search.getMinY());
	    search.setFrame(search.getMinX(),search.getMinY()+dY,search.getWidth(),search.getHeight()-dY);
	    iY=event.getY();
	    break;
	case 4:
	    dX=event.getX()-iX;
	    dY=event.getY()-iY;
	    if (search.getMinY()+dY>template.getMinY()) 
		dY=(int)(template.getMinY()-search.getMinY());
	    if (search.getMaxX()+dX<template.getMaxX())
		dX=(int)(template.getMaxX()-search.getMaxX());
	    search.setFrame(search.getMinX(),search.getMinY()+dY,search.getWidth()+dX,search.getHeight()-dY);
	    iX=event.getX();
	    iY=event.getY();
	    break;
	case 5:
	    dX=event.getX()-iX;
	    if (search.getMaxX()+dX<template.getMaxX())
		dX=(int)(template.getMaxX()-search.getMaxX());
	    search.setFrame(search.getMinX(),search.getMinY(),search.getWidth()+dX,search.getHeight());
	    iX=event.getX();
	    break;
	case 6:
	    dX=event.getX()-iX;
	    dY=event.getY()-iY;
	    if (search.getMaxX()+dX<template.getMaxX())
		dX=(int)(template.getMaxX()-search.getMaxX());
	    if (search.getMaxY()+dY<template.getMaxY())
		dY=(int)(template.getMaxY()-search.getMaxY());
	    search.setFrame(search.getMinX(),search.getMinY(),search.getWidth()+dX,search.getHeight()+dY);
	    iX=event.getX();
	    iY=event.getY();
	    break;
	case 7:
	    dY=event.getY()-iY;
	    if (search.getMaxY()+dY<template.getMaxY())
		dY=(int)(template.getMaxY()-search.getMaxY());
	    search.setFrame(search.getMinX(),search.getMinY(),search.getWidth(),search.getHeight()+dY);
	    iY=event.getY();
	    break;
	case 8:
	    dY=event.getY()-iY;
	    dX=event.getX()-iX;
	    if (search.getMaxY()+dY<template.getMaxY())
		dY=(int)(template.getMaxY()-search.getMaxY());
	    if (search.getMinX()+dX>template.getMinX()) 
		dX=(int)(template.getMinX()-search.getMinX());
	    search.setFrame(search.getMinX()+dX,search.getMinY(),search.getWidth()-dX,search.getHeight()+dY);
	    iX=event.getX();
	    iY=event.getY();
	    break;
	case 9:
	    dX=event.getX()-iX;
	    if (search.getMinX()+dX>template.getMinX()) 
		dX=(int)(template.getMinX()-search.getMinX());
	    search.setFrame(search.getMinX()+dX,search.getMinY(),search.getWidth()-dX,search.getHeight());
	    iX=event.getX();
	    break;
	case 10:
	    dX=event.getX()-iX;
	    dY=event.getY()-iY;
	    if (template.getMinX()+dX>template.getMaxX()) 
		dX=(int)(template.getMaxX()-template.getMinX());
	    if (template.getMinX()+dX<search.getMinX()) 
		dX=(int)(search.getMinX()-template.getMinX());
	    if (template.getMinY()+dY>template.getMaxY()) 
		dY=(int)(template.getMaxY()-template.getMinY());
	    if (template.getMinY()+dY<search.getMinY()) 
		dY=(int)(search.getMinY()-template.getMinY());
	    template.setFrame(template.getMinX()+dX,template.getMinY()+dY,template.getWidth()-dX,template.getHeight()-dY);
	    iX=event.getX();
	    iY=event.getY();
	    break;
	case 11:
	    dY=event.getY()-iY;
	    if (template.getMinY()+dY>template.getMaxY()) 
		dY=(int)(template.getMaxY()-template.getMinY());
	    if (template.getMinY()+dY<search.getMinY()) 
		dY=(int)(search.getMinY()-template.getMinY());
	    template.setFrame(template.getMinX(),template.getMinY()+dY,template.getWidth(),template.getHeight()-dY);
	    iY=event.getY();
	    break;
	case 12:
	    dX=event.getX()-iX;
	    dY=event.getY()-iY;
	    if (template.getMaxX()+dX<template.getMinX()) 
		dX=(int)(template.getMinX()-template.getMaxX());
	    if (template.getMaxX()+dX>search.getMaxX()) 
		dX=(int)(search.getMaxX()-template.getMaxX());
	    if (template.getMinY()+dY>template.getMaxY()) 
		dY=(int)(template.getMaxY()-template.getMinY());
	    if (template.getMinY()+dY<search.getMinY()) 
		dY=(int)(search.getMinY()-template.getMinY());
	    template.setFrame(template.getMinX(),template.getMinY()+dY,template.getWidth()+dX,template.getHeight()-dY);
	    iX=event.getX();
	    iY=event.getY();
	    break;
	case 13:
	    dX=event.getX()-iX;
	    if (template.getMaxX()+dX<template.getMinX()) 
		dX=(int)(template.getMinX()-template.getMaxX());
	    if (template.getMaxX()+dX>search.getMaxX()) 
		dX=(int)(search.getMaxX()-template.getMaxX());
	    template.setFrame(template.getMinX(),template.getMinY(),template.getWidth()+dX,template.getHeight());
	    iX=event.getX();
	    break;
	case 14:
	    dX=event.getX()-iX;
	    dY=event.getY()-iY;
	    if (template.getMaxX()+dX<template.getMinX()) 
		dX=(int)(template.getMinX()-template.getMaxX());
	    if (template.getMaxX()+dX>search.getMaxX()) 
		dX=(int)(search.getMaxX()-template.getMaxX());
	    if (template.getMaxY()+dY<template.getMinY()) 
		dY=(int)(template.getMinY()-template.getMaxY());
	    if (template.getMaxY()+dY>search.getMaxY()) 
		dY=(int)(search.getMaxY()-template.getMaxY());
	    template.setFrame(template.getMinX(),template.getMinY(),template.getWidth()+dX,template.getHeight()+dY);
	    iX=event.getX();
	    iY=event.getY();
	    break;
	case 15:
	    dY=event.getY()-iY;
	    if (template.getMaxY()+dY<template.getMinY()) 
		dY=(int)(template.getMinY()-template.getMaxY());
	    if (template.getMaxY()+dY>search.getMaxY()) 
		dY=(int)(search.getMaxY()-template.getMaxY());
	    template.setFrame(template.getMinX(),template.getMinY(),template.getWidth(),template.getHeight()+dY);
	    iY=event.getY();
	    break;
	case 16:
	    dY=event.getY()-iY;
	    dX=event.getX()-iX;
	    if (template.getMinX()+dX>template.getMaxX()) 
		dX=(int)(template.getMaxX()-template.getMinX());
	    if (template.getMinX()+dX<search.getMinX()) 
		dX=(int)(search.getMinX()-template.getMinX());
	    if (template.getMaxY()+dY<template.getMinY()) 
		dY=(int)(template.getMinY()-template.getMaxY());
	    if (template.getMaxY()+dY>search.getMaxY()) 
		dY=(int)(search.getMaxY()-template.getMaxY());
	    template.setFrame(template.getMinX()+dX,template.getMinY(),template.getWidth()-dX,template.getHeight()+dY);
	    iX=event.getX();
	    iY=event.getY();
	    break;
	case 17:
	    dX=event.getX()-iX;
	    if (template.getMinX()+dX>template.getMaxX()) 
		dX=(int)(template.getMaxX()-template.getMinX());
	    if (template.getMinX()+dX<search.getMinX()) 
		dX=(int)(search.getMinX()-template.getMinX());
	    template.setFrame(template.getMinX()+dX,template.getMinY(),template.getWidth()-dX,template.getHeight());
	    iX=event.getX();
	    break;

	}
	if (useFactor) search.setFrame(
				       template.getMinX()-factor*template.getWidth(),
				       template.getMinY()-factor*template.getHeight(),
				       (2*factor+1)*template.getWidth(),
				       (2*factor+1)*template.getHeight());
	if (comp!=null) comp.repaint();
    }

}
