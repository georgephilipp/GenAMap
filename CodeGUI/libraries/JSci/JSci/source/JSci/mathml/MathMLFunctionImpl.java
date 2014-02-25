package JSci.mathml;

import org.w3c.dom.mathml.*;

/**
 * Implements a MathML predefined symbol (function).
 * @version 1.0
 * @author Mark Hale
 */
public class MathMLFunctionImpl extends MathMLPredefinedSymbolImpl {
        /**
         * Constructs a MathML predefined symbol (function).
         */
        public MathMLFunctionImpl(MathMLDocumentImpl owner, String qualifiedName) {
                super(owner, qualifiedName);
        }

        public String getArity() {
                return "1";
        }
}

