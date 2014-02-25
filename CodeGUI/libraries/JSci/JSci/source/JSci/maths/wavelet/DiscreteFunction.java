package JSci.maths.wavelet;

import JSci.maths.*;
import JSci.maths.vectors.AbstractDoubleVector;
import JSci.maths.vectors.DoubleVector;
import JSci.util.*;
import java.util.Arrays;

/**********************************************
* This class is used to be able to mix the wavelet
* and other type of functions such as given signals.
* @author Daniel Lemire
*************************************************/
public class DiscreteFunction extends MultiscaleFunction implements Cloneable  {
        protected AbstractDoubleVector data;

        public DiscreteFunction(double[] v) {
		setData(v);
	}
	public void setData (double[] v) {
		data = new DoubleVector(v);
	}
  /*******************************
  * Return a String representation
  * of the object
  ********************************/
  public String toString() {
    return(data.toString());
  }
  /**********************
  * Makes the L2 norm of the
  * internal array equal to 1.
  ***********************/
  public final void normalize() {
    data = data.normalize();
  }

	/************************************************
  * Return as an array the sampled values
  * of the function
	*************************************************/
	public final double[] evaluate() {
		return(VectorToolkit.toArray(data));
	}

  /*****************************************
  * Check if another object is equal to this
  * DiscreteFunction object
  ******************************************/
  public final boolean equals(Object a) {
    if((a!=null) && (a instanceof DiscreteFunction))  {
      DiscreteFunction iv=(DiscreteFunction)a;
        return data.equals(iv.data);
    }
    return false;
  }
	/************************************************
  * Return as an array the sampled values
  * of the function
  * @param j number of iterations (doesn't do anything)
	*************************************************/
	public double[] evaluate (int j) {
		return(evaluate());
	}
  /******************************************
  * Compute the mass (integral)
  * @param a left boundary of the interval
  * @param b right boundary of the interval
  * @param jfin number of iterations to consider
  *   (precision)
  **********************************************/
	public double mass(double a, double b, int jfin) {
                return data.mass()/(data.dimension()-1)*Math.abs(b-a);
	}
	/***************************
  * Compute the L2 norm of the
  * signal
	****************************/
  public final double norm () {
    return(data.norm());
  }
	/***************************
  * Compute the L2 norm of the
  * function
  * @param j number of iterations
	****************************/
  public double norm (int j) {
    return(data.norm());
  }
  /********************************************
  * Return a copy of this object
  *********************************************/
	public Object clone() {
    DiscreteFunction df=(DiscreteFunction) super.clone();
    df.setData(VectorToolkit.toArray(data));
    return(df);
	}
  /*****************************************
  * Tells you how many samples you'll get
  * from this function (will not depend
  * on the parameter)
  ******************************************/
	public int dimension(int jfin) {
		return(data.dimension());
	}
  /*****************************************
  * Tells you how many samples you'll get
  * from this function
  ******************************************/
	public final int dimension() {
		return(data.dimension());
	}
	/****************************************
  * This method is used to compute
  * how the number of scaling functions
  * changes from on scale to the other.
  * Basically, if you have k scaling
  * function and a Filter of type t, you'll
  * have 2*k+t scaling functions at the
  * next scale (dyadic case).
  * Notice that this method assumes
  * that one is working with the dyadic
  * grid while the method "previousDimension"
  * define in the interface "Filter" doesn't.
	******************************************/
	public int getFilterType () {
		return(data.dimension());
	}
}

