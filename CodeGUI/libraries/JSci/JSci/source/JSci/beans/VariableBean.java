package JSci.beans;

import java.beans.*;
import java.util.Vector;
import JSci.maths.*;
import JSci.maths.matrices.DoubleMatrix;
import JSci.maths.vectors.DoubleVector;

public final class VariableBean extends Object implements java.io.Serializable {
        private PropertyChangeSupport changes=new PropertyChangeSupport(this);
        private Vector variableListeners=new Vector();
        private String variable=new String();
        private Object value;

        public VariableBean() {}
        public synchronized void setVariable(String var) {
                String oldVar=variable;
                variable=var;
                changes.firePropertyChange("variable",oldVar,var);
        }
        public synchronized String getVariable() {
                return variable;
        }
        public synchronized void setValueAsNumber(double x) {
                value=new MathDouble(x);
                changes.firePropertyChange("valueAsNumber",null,new Double(x));
                fireVariableChanged(new VariableEvent(this,variable,value));
        }
        public synchronized double getValueAsNumber() {
                if(value instanceof MathDouble)
                        return ((MathDouble)value).value();
                else
                        return Double.NaN;
        }
        public synchronized void setValueAsVector(double v[]) {
                value=new DoubleVector(v);
                changes.firePropertyChange("valueAsVector",null,v);
                fireVariableChanged(new VariableEvent(this,variable,value));
        }
        public synchronized void setValueAsMatrix(double m[][]) {
                value=new DoubleMatrix(m);
                changes.firePropertyChange("valueAsMatrix",null,m);
                fireVariableChanged(new VariableEvent(this,variable,value));
        }
        public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
                changes.addPropertyChangeListener(l);
        }
        public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
                changes.removePropertyChangeListener(l);
        }
        public synchronized void addVariableListener(VariableListener l) {
                variableListeners.addElement(l);
        }
        public synchronized void removeVariableListener(VariableListener l) {
                variableListeners.removeElement(l);
        }
        private void fireVariableChanged(VariableEvent evt) {
                for(int i=0;i<variableListeners.size();i++)
                        ((VariableListener)variableListeners.elementAt(i)).variableChanged(evt);
        }
}

