package JSci.mathml;

import org.w3c.dom.mathml.*;

/**
 * Implements a MathML predefined symbol (binary operation).
 * @version 1.0
 * @author Mark Hale
 */
public class MathMLBinaryOpImpl extends MathMLPredefinedSymbolImpl {
        /**
         * Constructs a MathML predefined symbol (binary operation).
         */
        public MathMLBinaryOpImpl(MathMLDocumentImpl owner, String qualifiedName) {
                super(owner, qualifiedName);
        }

        public String getArity() {
                return "2";
        }
}

