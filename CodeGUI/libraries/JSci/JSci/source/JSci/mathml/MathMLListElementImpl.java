package JSci.mathml;

import org.w3c.dom.*;
import org.w3c.dom.mathml.*;

/**
 * Implements a MathML <code>list</code> element.
 * @version 1.0
 * @author Mark Hale
 */
public class MathMLListElementImpl extends MathMLContentContainerImpl implements MathMLListElement {
        /**
         * Constructs a MathML <code>list</code> element.
         */
        public MathMLListElementImpl(MathMLDocumentImpl owner, String qualifiedName) {
                super (owner, qualifiedName);
        }

        public boolean getIsExplicit() {
                return !(getFirstChild() instanceof MathMLBvarElement);
        }

        public String getOrdering() {
                return getAttribute("order");
        }
        public void setOrdering(String ordering) {
                setAttribute("order", ordering);
        }
}

