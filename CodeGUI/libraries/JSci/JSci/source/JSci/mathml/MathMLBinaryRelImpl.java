package JSci.mathml;

import org.w3c.dom.mathml.*;

/**
 * Implements a MathML predefined symbol (binary relation).
 * @version 1.0
 * @author Mark Hale
 */
public class MathMLBinaryRelImpl extends MathMLPredefinedSymbolImpl {
        /**
         * Constructs a MathML predefined symbol (binary relation).
         */
        public MathMLBinaryRelImpl(MathMLDocumentImpl owner, String qualifiedName) {
                super(owner, qualifiedName);
        }

        public String getArity() {
                return "2";
        }
}

