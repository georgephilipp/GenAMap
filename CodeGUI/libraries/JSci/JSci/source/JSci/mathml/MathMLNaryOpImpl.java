package JSci.mathml;

import org.w3c.dom.mathml.*;

/**
 * Implements a MathML predefined symbol (n-ary operation).
 * @version 1.0
 * @author Mark Hale
 */
public class MathMLNaryOpImpl extends MathMLPredefinedSymbolImpl {
        /**
         * Constructs a MathML predefined symbol (n-ary operation).
         */
        public MathMLNaryOpImpl(MathMLDocumentImpl owner, String qualifiedName) {
                super(owner, qualifiedName);
        }

        public String getArity() {
                return "variable";
        }
}

