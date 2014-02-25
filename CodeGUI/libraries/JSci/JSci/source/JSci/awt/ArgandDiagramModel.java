package JSci.awt;

import JSci.maths.*;

/**
* The ArgandDiagramModel provides a convenient implementation of
* the Graph2DModel interface for creating Argand diagrams using
* the LineGraph component.
* @version 1.0
* @author Mark Hale
*/
public final class ArgandDiagramModel extends AbstractGraphModel implements Graph2DModel {
        private Complex data[] = new Complex[0];

        public ArgandDiagramModel() {}
        /**
        * Sets the list of complex numbers to be plotted.
        */
        public void setData(Complex z[]) {
                if(data.length!=z.length)
                        data=new Complex[z.length];
                System.arraycopy(z,0,data,0,z.length);
                fireGraphDataChanged();
        }

// Graph2DModel interface

        public float getXCoord(int i) {
                return (float)data[i].real();
        }
        public float getYCoord(int i) {
                return (float)data[i].imag();
        }
        public int seriesLength() {
                return data.length;
        }
        public void firstSeries() {}
        public boolean nextSeries() {
                return false;
        }
}

