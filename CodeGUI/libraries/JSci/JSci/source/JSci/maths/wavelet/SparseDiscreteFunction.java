package JSci.maths.wavelet;

import JSci.maths.vectors.DoubleSparseVector;
import JSci.util.*;

public class SparseDiscreteFunction extends DiscreteFunction  implements Cloneable {

	public SparseDiscreteFunction(double[] v) {
                super(v);
	}
	public void setData (double[] v) {
		data = new DoubleSparseVector(v);
	}

  /********************************************
  * Return a copy of this object
  *********************************************/
	public Object clone() {
    SparseDiscreteFunction sdf=(SparseDiscreteFunction) super.clone();
    sdf.setData(VectorToolkit.toArray(data));
    return(sdf);
	}
}

