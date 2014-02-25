package JSci.mathml;

import org.w3c.dom.mathml.*;

/**
 * Implements a MathML predefined symbol (unary operation).
 * @version 1.0
 * @author Mark Hale
 */
public class MathMLUnaryOpImpl extends MathMLPredefinedSymbolImpl {
        /**
         * Constructs a MathML predefined symbol (unary operation).
         */
        public MathMLUnaryOpImpl(MathMLDocumentImpl owner, String qualifiedName) {
                super(owner, qualifiedName);
        }

        public String getArity() {
                return "1";
        }
}

