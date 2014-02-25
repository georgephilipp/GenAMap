package JSci.mathml;

import org.w3c.dom.*;
import org.w3c.dom.mathml.*;
import org.apache.xerces.dom.*;

/**
 * Implements a MathML <code>matrix</code> element.
 * @version 1.0
 * @author Mark Hale
 */
public class MathMLMatrixElementImpl extends MathMLElementImpl implements MathMLMatrixElement {
        /**
         * Constructs a MathML <code>matrix</code> element.
         */
        public MathMLMatrixElementImpl(MathMLDocumentImpl owner, String qualifiedName) {
                super(owner, qualifiedName);
        }

        public int getNrows() {
                return getRowsGetLength();
        }
        public int getNcols() {
                return getRow(1).getNEntries();
        }

        public MathMLNodeList getRows() {
                return new MathMLNodeList() {
                        public int getLength() {
                                return getRowsGetLength();
                        }
                        public Node item(int index) {
                                return getRowsItem(index);
                        }
                };
        }

        public MathMLMatrixrowElement getRow(int index) throws DOMException {
                Node row = getRowsItem(index-1);
                if (row == null) {
                        throw new DOMException(DOMException.INDEX_SIZE_ERR, "Index out of bounds");
                }
                return (MathMLMatrixrowElement) row;
        }
        public MathMLMatrixrowElement setRow(MathMLMatrixrowElement newRow, int index) throws DOMException {
                final int rowsLength = getRowsGetLength();

                if ((index < 1) || (index > rowsLength+1)) {
                        throw new DOMException(DOMException.INDEX_SIZE_ERR, "Index out of bounds");
                }
                if (index == rowsLength+1) {
                        return (MathMLMatrixrowElement) appendChild(newRow);
                } else {
                        return (MathMLMatrixrowElement) replaceChild(newRow, getRowsItem(index-1));
                }
        }
        public MathMLMatrixrowElement insertRow(MathMLMatrixrowElement newRow, int index) throws DOMException {
                final int rowsLength = getRowsGetLength();

                if ((index < 0) || (index > rowsLength+1)) {
                        throw new DOMException(DOMException.INDEX_SIZE_ERR, "Index out of bounds");
                }
                if ((index == 0) || (index == rowsLength+1)) {
                        return (MathMLMatrixrowElement) appendChild(newRow);
                } else {
                        return (MathMLMatrixrowElement) insertBefore(newRow, getRowsItem(index-1));
                }
        }
        public MathMLMatrixrowElement removeRow(int index) throws DOMException {
                Node row = getRowsItem(index-1);
                if (row == null) {
                        throw new DOMException(DOMException.INDEX_SIZE_ERR, "Index out of bounds");
                }
                return (MathMLMatrixrowElement) removeChild(row);
        }
        public void deleteRow(int index) throws DOMException {
                removeRow(index);
        }

        private int getRowsGetLength() {
                final int length = getLength();
                int numRows = 0;

                for (int i = 0; i < length; i++) {
                        if (item(i) instanceof MathMLMatrixrowElement) {
                                numRows++;
                        }
                }
                return numRows;
        }
        private Node getRowsItem(int index) {
                final int rowsLength = getRowsGetLength();

                if ((index < 0) || (index >= rowsLength))
                        return null;

                Node node = null;
                int n = -1;
                for (int i = 0; n < index; i++) {
                        node = item(i);
                        if (node instanceof MathMLMatrixrowElement) {
                                n++;
                        }
                }
                return node;
        }
}

