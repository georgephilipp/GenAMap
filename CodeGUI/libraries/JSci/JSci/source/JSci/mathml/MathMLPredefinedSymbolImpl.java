package JSci.mathml;

import org.w3c.dom.mathml.*;

/**
 * Implements a MathML predefined symbol.
 * @version 1.0
 * @author Mark Hale
 */
public class MathMLPredefinedSymbolImpl extends MathMLElementImpl implements MathMLPredefinedSymbol {
        /**
         * Constructs a MathML predefined symbol.
         */
        public MathMLPredefinedSymbolImpl(MathMLDocumentImpl owner, String qualifiedName) {
                super(owner, qualifiedName);
        }

        public String getDefinitionURL() {
                return getAttribute("definitionURL");
        }
        public void setDefinitionURL(String definitionURL) {
                setAttribute("definitionURL", definitionURL);
        }

        public String getEncoding() {
                return getAttribute("encoding");
        }
        public void setEncoding(String encoding) {
                setAttribute("encoding", encoding);
        }

        public String getArity() {
                return "0";
        }

        public String getSymbolName() {
                return getLocalName();
        }
}

