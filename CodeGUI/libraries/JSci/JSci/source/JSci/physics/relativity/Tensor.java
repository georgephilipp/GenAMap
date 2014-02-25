package JSci.physics.relativity;

import JSci.maths.Member;

/**
* The Tensor superclass provides an abstract encapsulation for tensors.
* @version 1.0
* @author Mark Hale
*/
public abstract class Tensor implements Member {
        /**
        * Constructs a tensor.
        */
        public Tensor() {}
	public Object getSet() {
		throw new RuntimeException("Not yet implemented: please file bug report");
	}
}
