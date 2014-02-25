package JSci.mathml;

import org.w3c.dom.*;
import org.w3c.dom.mathml.*;
import org.apache.xerces.dom.DOMImplementationImpl;

/**
 * Implements a MathML DOM implementation.
 * @version 1.0
 * @author Mark Hale
 */
public class MathMLDOMImplementationImpl extends DOMImplementationImpl implements MathMLDOMImplementation {
        private static final MathMLDOMImplementation singleton = new MathMLDOMImplementationImpl();

        private MathMLDOMImplementationImpl() {
                super();
        }

        public final MathMLDocument createMathMLDocument() {
                return new MathMLDocumentImpl();
        }

        /** NON-DOM: Obtain and return a single shared object */
        public static DOMImplementation getDOMImplementation() {
                return singleton;
        }
}

