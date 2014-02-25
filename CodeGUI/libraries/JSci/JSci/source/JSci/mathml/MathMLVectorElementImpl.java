package JSci.mathml;

import org.w3c.dom.*;
import org.w3c.dom.mathml.*;
import org.apache.xerces.dom.*;

/**
 * Implements a MathML <code>vector</code> element.
 * @version 1.0
 * @author Mark Hale
 */
public class MathMLVectorElementImpl extends MathMLElementImpl implements MathMLVectorElement {
        /**
         * Constructs a MathML <code>vector</code> element.
         */
        public MathMLVectorElementImpl(MathMLDocumentImpl owner, String qualifiedName) {
                super(owner, qualifiedName);
        }

        public int getNcomponents() {
                return getComponentsGetLength();
        }

        public MathMLContentElement getComponent(int index) throws DOMException {
                Node component = getComponentsItem(index-1);
                if (component == null) {
                        throw new DOMException(DOMException.INDEX_SIZE_ERR, "Index out of bounds");
                }
                return (MathMLContentElement) component;
        }
        public MathMLContentElement setComponent(MathMLContentElement newComponent, int index) throws DOMException {
                final int componentsLength = getComponentsGetLength();

                if ((index < 1) || (index > componentsLength+1)) {
                        throw new DOMException(DOMException.INDEX_SIZE_ERR, "Index out of bounds");
                }
                if (index == componentsLength+1) {
                        return (MathMLContentElement) appendChild(newComponent);
                } else {
                        return (MathMLContentElement) replaceChild(newComponent, getComponentsItem(index-1));
                }
        }
        public MathMLContentElement insertComponent(MathMLContentElement newComponent, int index) throws DOMException {
                final int componentsLength = getComponentsGetLength();

                if ((index < 0) || (index > componentsLength+1)) {
                        throw new DOMException(DOMException.INDEX_SIZE_ERR, "Index out of bounds");
                }
                if ((index == 0) || (index == componentsLength+1)) {
                        return (MathMLContentElement) appendChild(newComponent);
                } else {
                        return (MathMLContentElement) insertBefore(newComponent, getComponentsItem(index-1));
                }
        }
        public MathMLContentElement removeComponent(int index) throws DOMException {
                Node component = getComponentsItem(index-1);
                if (component == null) {
                        throw new DOMException(DOMException.INDEX_SIZE_ERR, "Index out of bounds");
                }
                return (MathMLContentElement) removeChild(component);
        }
        public void deleteComponent(int index) throws DOMException {
                removeComponent(index);
        }

        private int getComponentsGetLength() {
                final int length = getLength();
                int numComponents = 0;

                for (int i = 0; i < length; i++) {
                        if (item(i) instanceof MathMLContentElement) {
                                numComponents++;
                        }
                }
                return numComponents;
        }
        private Node getComponentsItem(int index) {
                final int componentsLength = getComponentsGetLength();

                if ((index < 0) || (index >= componentsLength))
                        return null;

                Node node = null;
                int n = -1;
                for (int i = 0; n < index; i++) {
                        node = item(i);
                        if (node instanceof MathMLContentElement) {
                                n++;
                        }
                }
                return node;
        }
}

