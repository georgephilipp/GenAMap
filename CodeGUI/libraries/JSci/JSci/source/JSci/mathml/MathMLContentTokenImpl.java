package JSci.mathml;

import org.w3c.dom.*;
import org.w3c.dom.mathml.*;

/**
 * Implements a MathML content token.
 * @version 1.0
 * @author Mark Hale
 */
public class MathMLContentTokenImpl extends MathMLElementImpl implements MathMLContentToken {
        /**
         * Constructs a MathML content token.
         */
        public MathMLContentTokenImpl(MathMLDocumentImpl owner, String qualifiedName) {
                super (owner, qualifiedName);
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

        public MathMLNodeList getArguments() {
                return new MathMLNodeList() {
                        public int getLength() {
                                return getArgumentsGetLength();
                        }
                        public Node item(int index) {
                                return getArgumentsItem(index);
                        }
                };
        }
        public Node getArgument(int index) {
                Node arg = getArgumentsItem(index-1);

                return arg;
        }
        public Node setArgument(Node newArgument, int index) {
                final int argsLength = getArgumentsGetLength();

                return replaceChild(newArgument, getArgumentsItem(index-1));
        }
        public Node insertArgument(Node newArgument, int index) {
                final int argsLength = getArgumentsGetLength();

                return insertBefore(newArgument, getArgumentsItem(index-1));
        }
        public Node removeArgument(int index) {
                Node arg = getArgumentsItem(index-1);

                return removeChild(arg);
        }
        public void deleteArgument(int index) {
                removeArgument(index);
        }

        private int getArgumentsGetLength() {
                final int length = getLength();
                int numArgs = 0;

                for (int i = 0; i < length; i++) {
                        String localName = item(i).getLocalName();
                        if (!(localName != null && localName.equals("sep"))) {
                                numArgs++;
                        }
                }
                return numArgs;
        }
        private Node getArgumentsItem(int index) {
                final int argsLength = getArgumentsGetLength();

                if ((index < 0) || (index >= argsLength))
                        return null;

                Node node = null;
                int n = -1;
                for (int i = 0; n < index; i++) {
                        node = item(i);
                        String localName = item(i).getLocalName();
                        if (!(localName != null && localName.equals("sep"))) {
                                n++;
                        }
                }
                return node;
        }
}

