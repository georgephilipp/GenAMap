package JSci.mathml;

import org.w3c.dom.mathml.*;

/**
 * Implements a MathML predefined symbol (n-ary relation).
 * @version 1.0
 * @author Mark Hale
 */
public class MathMLNaryRelImpl extends MathMLPredefinedSymbolImpl {
        /**
         * Constructs a MathML predefined symbol (n-ary relation).
         */
        public MathMLNaryRelImpl(MathMLDocumentImpl owner, String qualifiedName) {
                super(owner, qualifiedName);
        }

        public String getArity() {
                return "variable";
        }
}

