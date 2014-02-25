package JSci.mathml;

import org.w3c.dom.mathml.*;

/**
 * Implements a MathML numeric content element.
 * @version 1.0
 * @author Mark Hale
 */
public class MathMLCnElementImpl extends MathMLContentTokenImpl implements MathMLCnElement {
        /**
         * Constructs a MathML numeric content element.
         */
        public MathMLCnElementImpl(MathMLDocumentImpl owner, String qualifiedName) {
                super(owner, qualifiedName);
        }

        public String getType() {
                String type = getAttribute("type");
                if(type.length() == 0)
                        type = "real";
                return type;
        }
        public void setType(String type) {
                setAttribute("type", type);
        }

        public String getBase() {
                String base = getAttribute("base");
                if(base.length() == 0)
                        base = "10";
                return base;
        }
        public void setBase(String base) {
                setAttribute("base", base);
        }

        public int getNargs() {
                final int length = getLength();
                int numArgs = 1;

                for (int i = 0; i < length; i++) {
                        String localName = item(i).getLocalName();
                        if (localName != null && localName.equals("sep")) {
                                numArgs++;
                        }
                }
                return numArgs;
        }
}

