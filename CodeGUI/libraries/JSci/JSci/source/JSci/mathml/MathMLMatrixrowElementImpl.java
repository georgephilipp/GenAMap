package JSci.mathml;

import org.w3c.dom.*;
import org.w3c.dom.mathml.*;
import org.apache.xerces.dom.*;

/**
 * Implements a MathML <code>matrixrow</code> element.
 * @version 1.0
 * @author Mark Hale
 */
public class MathMLMatrixrowElementImpl extends MathMLElementImpl implements MathMLMatrixrowElement {
        /**
         * Constructs a MathML <code>matrixrow</code> element.
         */
        public MathMLMatrixrowElementImpl(MathMLDocumentImpl owner, String qualifiedName) {
                super(owner, qualifiedName);
        }

        public int getNEntries() {
                return getEntriesGetLength();
        }

        public MathMLContentElement getEntry(int index) throws DOMException {
                Node entry = getEntriesItem(index-1);
                if (entry == null) {
                        throw new DOMException(DOMException.INDEX_SIZE_ERR, "Index out of bounds");
                }
                return (MathMLContentElement) entry;
        }
        public MathMLContentElement setEntry(MathMLContentElement newEntry, int index) throws DOMException {
                final int entriesLength = getEntriesGetLength();

                if ((index < 1) || (index > entriesLength+1)) {
                        throw new DOMException(DOMException.INDEX_SIZE_ERR, "Index out of bounds");
                }
                if (index == entriesLength+1) {
                        return (MathMLContentElement) appendChild(newEntry);
                } else {
                        return (MathMLContentElement) replaceChild(newEntry, getEntriesItem(index-1));
                }
        }
        public MathMLContentElement insertEntry(MathMLContentElement newEntry, int index) throws DOMException {
                final int entriesLength = getEntriesGetLength();

                if ((index < 0) || (index > entriesLength+1)) {
                        throw new DOMException(DOMException.INDEX_SIZE_ERR, "Index out of bounds");
                }
                if ((index == 0) || (index == entriesLength+1)) {
                        return (MathMLContentElement) appendChild(newEntry);
                } else {
                        return (MathMLContentElement) insertBefore(newEntry, getEntriesItem(index-1));
                }
        }
        public MathMLContentElement removeEntry(int index) throws DOMException {
                Node entry = getEntriesItem(index-1);
                if (entry == null) {
                        throw new DOMException(DOMException.INDEX_SIZE_ERR, "Index out of bounds");
                }
                return (MathMLContentElement) removeChild(entry);
        }
        public void deleteEntry(int index) throws DOMException {
                removeEntry(index);
        }

        private int getEntriesGetLength() {
                final int length = getLength();
                int numEntries = 0;

                for (int i = 0; i < length; i++) {
                        if (item(i) instanceof MathMLContentElement) {
                                numEntries++;
                        }
                }
                return numEntries;
        }
        private Node getEntriesItem(int index) {
                final int entriesLength = getEntriesGetLength();

                if ((index < 0) || (index >= entriesLength))
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

