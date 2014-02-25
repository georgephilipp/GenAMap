package JSci.beans;

import java.awt.event.*;
import java.beans.*;
import java.util.*;
import JSci.maths.*;
import JSci.maths.matrices.AbstractComplexMatrix;
import JSci.maths.matrices.AbstractDoubleMatrix;
import JSci.maths.matrices.AbstractIntegerMatrix;
import JSci.maths.vectors.AbstractComplexVector;
import JSci.maths.vectors.AbstractDoubleVector;
import JSci.maths.vectors.AbstractIntegerVector;
import JSci.io.*;
import JSci.util.*;

public final class MathBean extends Object implements java.io.Serializable,
VariableListener, ActionListener {
        private PropertyChangeSupport changes=new PropertyChangeSupport(this);
        private MathMLExpression expr;
        private String mathml="";
        private Hashtable variables=new Hashtable();
        private Object result=new MathDouble(Double.NaN);

        public MathBean() {}
        public synchronized void setMathML(String uri) {
                try {
                        MathMLParser parser=new MathMLParser();
                        parser.parse(uri);
                        expr=(MathMLExpression)(parser.translateToJSciObjects()[0]);
                } catch(Exception e) {}
                String oldUri=mathml;
                mathml=uri;
                changes.firePropertyChange("mathml",oldUri,uri);
        }
        public synchronized String getMathML() {
                return mathml;
        }
        public synchronized double getResultAs0DArray() {
                if(result instanceof MathDouble)
                        return ((MathDouble)result).value();
                else if(result instanceof MathInteger)
                        return ((MathInteger)result).value();
                else
                        return Double.NaN;
        }
        public synchronized double[] getResultAs1DArray() {
                if(result instanceof Complex) {
                        double array[]={((Complex)result).real(),((Complex)result).imag()};
                        return array;
                } else if(result instanceof AbstractDoubleVector) {
                        return VectorToolkit.toArray((AbstractDoubleVector)result);
                } else if(result instanceof AbstractIntegerVector) {
                        return VectorToolkit.toArray(((AbstractIntegerVector)result).toDoubleVector());
                } else
                        return null;
        }
        public synchronized double[][] getResultAs2DArray() {
                if(result instanceof AbstractComplexVector) {
                        double array[][]=new double[2][];
                        array[0]=VectorToolkit.toArray(((AbstractComplexVector)result).real());
                        array[1]=VectorToolkit.toArray(((AbstractComplexVector)result).imag());
                        return array;
                } else if(result instanceof AbstractDoubleMatrix)
                        return MatrixToolkit.toArray((AbstractDoubleMatrix)result);
                else if(result instanceof AbstractIntegerMatrix)
                        return MatrixToolkit.toArray(((AbstractIntegerMatrix)result).toDoubleMatrix());
                else
                        return null;
        }
        public synchronized double[][][] getResultAs3DArray() {
                if(result instanceof AbstractComplexMatrix) {
                        double array[][][]=new double[2][][];
                        array[0]=MatrixToolkit.toArray(((AbstractComplexMatrix)result).real());
                        array[1]=MatrixToolkit.toArray(((AbstractComplexMatrix)result).imag());
                        return array;
                } else
                        return null;
        }
        public void variableChanged(VariableEvent evt) {
                variables.put(evt.getVariable(),evt.getValue());
        }
        public void actionPerformed(ActionEvent evt) {
                MathMLExpression evalExp=expr;
                Enumeration vars=variables.keys();
                while(vars.hasMoreElements()) {
                        Object var=vars.nextElement();
                        evalExp=evalExp.substitute(var.toString(),variables.get(var));
                }
                result=evalExp.evaluate();
                changes.firePropertyChange("resultAs0DArray",null,new Double(getResultAs0DArray()));
                changes.firePropertyChange("resultAs1DArray",null,getResultAs1DArray());
                changes.firePropertyChange("resultAs2DArray",null,getResultAs2DArray());
                changes.firePropertyChange("resultAs3DArray",null,getResultAs3DArray());
        }
        public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
                changes.addPropertyChangeListener(l);
        }
        public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
                changes.removePropertyChangeListener(l);
        }
}

