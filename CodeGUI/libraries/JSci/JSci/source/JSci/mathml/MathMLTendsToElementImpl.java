package JSci.mathml;

import org.w3c.dom.mathml.*;

/**
 * Implements a MathML <code>tendsto</code> element.
 * @version 1.0
 * @author Mark Hale
 */
public class MathMLTendsToElementImpl extends MathMLPredefinedSymbolImpl implements MathMLTendsToElement {
        /**
         * Constructs a MathML <code>tendsto</code> element.
         */
        public MathMLTendsToElementImpl(MathMLDocumentImpl owner, String qualifiedName) {
                super(owner, qualifiedName);
        }

        public String getType() {
                return getAttribute("type");
        }
        public void setType(String type) {
                setAttribute("type", type);
        }
}

