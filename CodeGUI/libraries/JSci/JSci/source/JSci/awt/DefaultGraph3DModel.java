package JSci.awt;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

/**
* The DefaultGraph3DModel class provides a default implementation
* of the Graph3DModel interface.
* @version 1.2
* @author Mark Hale
*/
public final class DefaultGraph3DModel extends AbstractGraphModel implements Graph3DModel, TableModelListener {
        private static final int X_AXIS_COLUMN = 0;
        private static final int Y_AXIS_COLUMN = 1;
        private static final int SERIES_COLUMN = 2;
                
        private float xAxis[] = new float[0];
        private float yAxis[] = new float[0];
        private final List series=new ArrayList();
        private int pos=0;
        private DataSeries curSeries=null;

        public DefaultGraph3DModel() {}
        /**
        * Sets the x-axis values.
        * A copy of the values is made.
        */
        public void setXAxis(float x[]) {
                if(xAxis.length!=x.length)
                        xAxis=new float[x.length];
                System.arraycopy(x,0,xAxis,0,x.length);
                fireGraphDataChanged();
        }
        /**
        * Sets the y-axis values.
        * A copy of the values is made.
        */
        public void setYAxis(float y[]) {
                if(yAxis.length!=y.length)
                        yAxis=new float[y.length];
                System.arraycopy(y,0,yAxis,0,y.length);
                fireGraphDataChanged();
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
        public void addSeries(float newSeries[]) {
                addSeries(new DataSeries(xAxis, yAxis, newSeries));
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
        * Removes a data series.
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
        }

// Graph3DModel interface

        public float getXCoord(int i) {
                return xAxis[i];
        }
        public float getYCoord(int i) {
                return yAxis[i];
        }
        public float getZCoord(int i) {
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
                protected float yAxis[] = new float[0];
                protected float series[] = new float[0];
                private boolean isVis = true;

                protected DataSeries() {}
                public DataSeries(float xValues[], float yValues[], float zValues[]) {
                        setXAxis(xValues);
                        setYAxis(yValues);
                        setValues(zValues);
                }
                public DataSeries(float xValues[], float yValues[], double zValues[]) {
                        setXAxis(xValues);
                        setYAxis(yValues);
                        setValues(zValues);
                }
                public DataSeries(double xValues[], double yValues[], double zValues[]) {
                        setXAxis(xValues);
                        setYAxis(yValues);
                        setValues(zValues);
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
                public void setYAxis(float yValues[]) {
                        if(yAxis.length != yValues.length)
                                yAxis = new float[yValues.length];
                        System.arraycopy(yValues,0,yAxis,0,yValues.length);
                        fireTableColumnUpdated(Y_AXIS_COLUMN);
                }
                public void setYAxis(double yValues[]) {
                        if(yAxis.length != yValues.length)
                                yAxis = new float[yValues.length];
                        for(int i=0;i<yValues.length;i++)
                                yAxis[i] = (float) yValues[i];
                        fireTableColumnUpdated(Y_AXIS_COLUMN);
                }
                public void setValues(float zValues[]) {
                        if(series.length != zValues.length)
                                series = new float[zValues.length];
                        System.arraycopy(zValues,0,series,0,zValues.length);
                        fireTableColumnUpdated(SERIES_COLUMN);
                }
                public void setValues(double zValues[]) {
                        if(series.length != zValues.length)
                                series = new float[zValues.length];
                        for(int i=0;i<zValues.length;i++)
                                series[i] = (float) zValues[i];
                        fireTableColumnUpdated(SERIES_COLUMN);
                }
                public float getXCoord(int i) {
                        return xAxis[i];
                }
                public void setXCoord(int i, float x) {
                        xAxis[i] = x;
                        fireTableCellUpdated(i, X_AXIS_COLUMN);
                }
                public float getYCoord(int i) {
                        return yAxis[i];
                }
                public void setYCoord(int i, float y) {
                        yAxis[i] = y;
                        fireTableCellUpdated(i, Y_AXIS_COLUMN);
                }
                public float getValue(int i) {
                        return series[i];
                }
                public void setValue(int i, float z) {
                        series[i] = z;
                        fireTableCellUpdated(i, SERIES_COLUMN);
                }
                public int length() {
                        return Math.min(Math.min(xAxis.length, yAxis.length), series.length);
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
                        else if(column == Y_AXIS_COLUMN)
                                fireTableChanged(new TableModelEvent(this, 0, yAxis.length-1, column));
                        else if(column == SERIES_COLUMN)
                                fireTableChanged(new TableModelEvent(this, 0, series.length-1, column));
                }
                
// TableModelInterface

                public String getColumnName(int col) {
                        if(col == X_AXIS_COLUMN)
                                return "X";
                        else if(col == Y_AXIS_COLUMN)
                                return "Y";
                        else if(col == SERIES_COLUMN)
                                return "Z";
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
                        return 3;
                }
                public Object getValueAt(int row, int col) {
                        if(col == X_AXIS_COLUMN)
                                return new Float(getXCoord(row));
                        else if(col == Y_AXIS_COLUMN)
                                return new Float(getYCoord(row));
                        else if(col == SERIES_COLUMN)
                                return new Float(getValue(row));
                        else
                                return null;
                }
                public void setValueAt(Object value, int row, int col) {
                        if(col == X_AXIS_COLUMN)
                                setXCoord(row, ((Float)value).floatValue());
                        else if(col == Y_AXIS_COLUMN)
                                setYCoord(row, ((Float)value).floatValue());
                        else if(col == SERIES_COLUMN)
                                setValue(row, ((Float)value).floatValue());
                }
                public boolean isCellEditable(int row, int col) {
                        return true;
                }
        }
}

