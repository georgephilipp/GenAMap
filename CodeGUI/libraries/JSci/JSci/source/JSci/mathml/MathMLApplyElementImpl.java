package JSci.mathml;

import org.w3c.dom.*;
import org.w3c.dom.mathml.*;

/**
 * Implements a MathML <code>apply</code> element.
 * @version 1.0
 * @author Mark Hale
 */
public class MathMLApplyElementImpl extends MathMLContentContainerImpl implements MathMLApplyElement {
        /**
         * Constructs a MathML <code>apply</code> element.
         */
        public MathMLApplyElementImpl(MathMLDocumentImpl owner, String qualifiedName) {
                super (owner, qualifiedName);
        }

        public MathMLElement getOperator() {
                return (MathMLElement) getFirstChild();
        }
        public void setOperator(MathMLElement operator) {
                replaceChild(operator, getFirstChild());
        }

        public MathMLElement getLowLimit() {
                return (MathMLElement) getNodeByName("lowlimit");
        }
        public void setLowLimit(MathMLElement lowlimit) throws DOMException {
                setNodeByName(lowlimit, "lowlimit");
        }

        public MathMLElement getUpLimit() {
                return (MathMLElement) getNodeByName("uplimit");
        }
        public void setUpLimit(MathMLElement uplimit) throws DOMException {
                setNodeByName(uplimit, "uplimit");
        }
}

