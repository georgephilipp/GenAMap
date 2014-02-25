package JSci.awt;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

/**
* The DefaultGraph2DModel class provides a default implementation
* of the Graph2DModel interface.
* @version 1.2
* @author Mark Hale
*/
public final class DefaultGraph2DModel extends AbstractGraphModel implements Graph2DModel, TableModelListener {
        private static final int X_AXIS_COLUMN = 0;
        private static final int SERIES_COLUMN = 1;
        
        private float defaultXAxis[]=new float[0];
        private final List series=new ArrayList();
        private int pos=0;
        private DataSeries curSeries=null;

        public DefaultGraph2DModel() {}
        /**
        * Sets the default x-axis values.
        * A copy of the values is made.
        * This method does not change the x-axis values of any data series
        * and so does not fire any events.
        */
        public void setXAxis(float x[]) {
                if(defaultXAxis.length!=x.length)
                        defaultXAxis=new float[x.length];
                System.arraycopy(x,0,defaultXAxis,0,x.length);
        }
        /**
        * Sets the default x-axis values.
        * A copy of the values is made.
        * This method does not change the x-axis values of any data series
        * and so does not fire any events.
        */
        public void setXAxis(double x[]) {
                if(defaultXAxis.length!=x.length)
                        defaultXAxis=new float[x.length];
                for(int i=0;i<x.length;i++)
                        defaultXAxis[i]=(float)x[i];
        }
        /**
        * Sets the default x-axis values.
        * This method does not change the x-axis values of any data series
        * and so does not fire any events.
        * @param a start of interval.
        * @param b end of interval.
        * @param n number of values.
        */
        public void setXAxis(float a,float b,int n) {
                if(defaultXAxis.length != n)
                        defaultXAxis = new float[n];
                final float scale = (b-a)/(n-1);
                for(int i=0; i<n; i++)
                        defaultXAxis[i] = scale*i+a;
        }
        /**
        * Adds a data series using the default values for the x-axis.
	* Be sure to call {@link #setXAxis(float[]) setXAxis} first.
        */
        public void addSeries(float newSeries[]) {
                addSeries(new DataSeries(defaultXAxis, newSeries));
        }
        /**
        * Adds a data series using the default values for the x-axis.
	* Be sure to call {@link #setXAxis(double[]) setXAxis} first.
        */
        public void addSeries(double newSeries[]) {
                addSeries(new DataSeries(defaultXAxis, newSeries));
        }
        /**
        * Adds a data series.
        */
        public void addSeries(DataSeries newSeries) {
                series.add(newSeries);
                fireGraphDataChanged();
                newSeries.addTableModelListener(this);
        }
        /**
        * Adds a data series.
        * Convenience method.
        */
        public void addSeries(float newXAxis[], float newSeries[]) {
                addSeries(new DataSeries(newXAxis, newSeries));
        }
        /**
        * Adds a data series.
        * Convenience method.
        */
        public void addSeries(float newXAxis[], double newSeries[]) {
                addSeries(new DataSeries(newXAxis, newSeries));
        }
        /**
        * Changes a data series.
        */
        public void changeSeries(int i,DataSeries newSeries) {
                getSeries(i).removeTableModelListener(this);
                series.set(i, newSeries);
                fireGraphDataChanged();
                newSeries.addTableModelListener(this);
        }
        /**
        * Changes a data series.
        * Convenience method.
        */
        public void changeSeries(int i,float newSeries[]) {
                getSeries(i).setValues(newSeries);
        }
        /**
        * Changes a data series.
        * Convenience method.
        */
        public void changeSeries(int i,double newSeries[]) {
                getSeries(i).setValues(newSeries);
        }
        /**
        * Remove a data series.
        */
        public void removeSeries(int i) {
                getSeries(i).removeTableModelListener(this);
                series.remove(i);
                fireGraphDataChanged();
        }
        public DataSeries getSeries(int i) {
                return (DataSeries)series.get(i);
        }
        /**
        * Convenience method.
        */
        public void setSeriesVisible(int i,boolean flag) {
                getSeries(i).setVisible(flag);
        }

        /**
        * Implementation of TabelModelListener.
        * Application code will not use this method explicitly, it is used internally.
        */
        public void tableChanged(TableModelEvent evt) {
                if(evt.getColumn() == SERIES_COLUMN)
                        fireGraphSeriesUpdated(series.indexOf(evt.getSource()));
                else
                        fireGraphDataChanged();
        }

// Graph2DModel interface

        public float getXCoord(int i) {
                return curSeries.getXCoord(i);
        }
        public float getYCoord(int i) {
                return curSeries.getValue(i);
        }
        public int seriesLength() {
                return curSeries.length();
        }
        public void firstSeries() {
                curSeries=getSeries(0);
                for(pos=0;!curSeries.isVisible() && pos<series.size()-1;)
                        curSeries=getSeries(++pos);
        }
        public boolean nextSeries() {
                if(pos==series.size()-1)
                        return false;
                do {
                        curSeries=getSeries(++pos);
                } while(!curSeries.isVisible() && pos<series.size()-1);
                return curSeries.isVisible();
        }

        /**
        * The DataSeries class encapsulates a data series for a graph.
        */
        public static class DataSeries extends AbstractTableModel {
                protected float xAxis[] = new float[0];
                protected float series[] = new float[0];
                private boolean isVis = true;

                protected DataSeries() {}
                public DataSeries(float xValues[], float yValues[]) {
                        setXAxis(xValues);
                        setValues(yValues);
                }
                public DataSeries(float xValues[], double yValues[]) {
                        setXAxis(xValues);
                        setValues(yValues);
                }
                public DataSeries(double xValues[], double yValues[]) {
                        setXAxis(xValues);
                        setValues(yValues);
                }
                public void setXAxis(float xValues[]) {
                        if(xAxis.length != xValues.length)
                                xAxis = new float[xValues.length];
                        System.arraycopy(xValues,0,xAxis,0,xValues.length);
                        fireTableColumnUpdated(X_AXIS_COLUMN);
                }
                public void setXAxis(double xValues[]) {
                        if(xAxis.length != xValues.length)
                                xAxis = new float[xValues.length];
                        for(int i=0;i<xValues.length;i++)
                                xAxis[i] = (float) xValues[i];
                        fireTableColumnUpdated(X_AXIS_COLUMN);
                }
                public void setValues(float yValues[]) {
                        if(series.length != yValues.length)
                                series = new float[yValues.length];
                        System.arraycopy(yValues,0,series,0,yValues.length);
                        fireTableColumnUpdated(SERIES_COLUMN);
                }
                public void setValues(double yValues[]) {
                        if(series.length != yValues.length)
                                series = new float[yValues.length];
                        for(int i=0;i<yValues.length;i++)
                                series[i] = (float) yValues[i];
                        fireTableColumnUpdated(SERIES_COLUMN);
                }
                public float getXCoord(int i) {
                        return xAxis[i];
                }
                public void setXCoord(int i, float x) {
                        xAxis[i] = x;
                        fireTableCellUpdated(i, X_AXIS_COLUMN);
                }
                public float getValue(int i) {
                        return series[i];
                }
                public void setValue(int i, float y) {
                        series[i] = y;
                        fireTableCellUpdated(i, SERIES_COLUMN);
                }
                public int length() {
                        return Math.min(xAxis.length, series.length);
                }
                public final void setVisible(boolean flag) {
                        if(flag != isVis) {
                                isVis = flag;
                                fireTableDataChanged();
                        }
                }
                public final boolean isVisible() {
                        return isVis;
                }
                private void fireTableColumnUpdated(int column) {
                        if(column == X_AXIS_COLUMN)
                                fireTableChanged(new TableModelEvent(this, 0, xAxis.length-1, column));
                        else if(column == SERIES_COLUMN)
                                fireTableChanged(new TableModelEvent(this, 0, series.length-1, column));
                }

// TableModelInterface

                public String getColumnName(int col) {
                        if(col == X_AXIS_COLUMN)
                                return "X";
                        else if(col == SERIES_COLUMN)
                                return "Y";
                        else
                                return null;
                }
                public Class getColumnClass(int col) {
                        return Float.class;
                }
                public final int getRowCount() {
                        return length();
                }
                public final int getColumnCount() {
                        return 2;
                }
                public Object getValueAt(int row, int col) {
                        if(col == X_AXIS_COLUMN)
                                return new Float(getXCoord(row));
                        else if(col == SERIES_COLUMN)
                                return new Float(getValue(row));
                        else
                                return null;
                }
                public void setValueAt(Object value, int row, int col) {
                        if(col == X_AXIS_COLUMN)
                                setXCoord(row, ((Float)value).floatValue());
                        else if(col == SERIES_COLUMN)
                                setValue(row, ((Float)value).floatValue());
                }
                public boolean isCellEditable(int row, int col) {
                        return true;
                }
        }
}

