package JSci.mathml;

import org.w3c.dom.*;
import org.w3c.dom.mathml.*;

/**
 * Implements a MathML <code>set</code> element.
 * @version 1.0
 * @author Mark Hale
 */
public class MathMLSetElementImpl extends MathMLContentContainerImpl implements MathMLSetElement {
        /**
         * Constructs a MathML <code>set</code> element.
         */
        public MathMLSetElementImpl(MathMLDocumentImpl owner, String qualifiedName) {
                super (owner, qualifiedName);
        }

        public boolean getIsExplicit() {
                return !(getFirstChild() instanceof MathMLBvarElement);
        }

        public String getType() {
                return getAttribute("type");
        }
        public void setType(String type) {
                setAttribute("type", type);
        }
}

