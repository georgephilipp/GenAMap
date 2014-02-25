package JSci.mathml;

import java.lang.reflect.*;
import java.util.Hashtable;
import org.w3c.dom.*;
import org.w3c.dom.mathml.*;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.DocumentTypeImpl;

/**
 * Implements a MathML document.
 * @version 1.0
 * @author Mark Hale
 */
public class MathMLDocumentImpl extends DocumentImpl implements MathMLDocument {
        private static final String DOCTYPE_PUBLIC = "-//W3C//DTD MathML 2.0//EN";
        private static final String DOCTYPE_SYSTEM = "http://www.w3.org/TR/MathML2/dtd/mathml2.dtd";
        private static final Hashtable _elementTypesMathML;
        private static final Class[] _elemClassSigMathML = new Class [] {MathMLDocumentImpl.class, String.class};

        static {
                _elementTypesMathML = new Hashtable(160);
                _elementTypesMathML.put("math", MathMLMathElementImpl.class);
                _elementTypesMathML.put("annotation", MathMLAnnotationElementImpl.class);
                _elementTypesMathML.put("xml-annotation", MathMLXMLAnnotationElementImpl.class);
                // presentation
                _elementTypesMathML.put("mi", MathMLPresentationTokenImpl.class);
                _elementTypesMathML.put("mn", MathMLPresentationTokenImpl.class);
                _elementTypesMathML.put("msub", MathMLScriptElementImpl.class);
                _elementTypesMathML.put("msup", MathMLScriptElementImpl.class);
                _elementTypesMathML.put("msubsup", MathMLScriptElementImpl.class);
                _elementTypesMathML.put("munder", MathMLUnderOverElementImpl.class);
                _elementTypesMathML.put("mover", MathMLUnderOverElementImpl.class);
                _elementTypesMathML.put("munderover", MathMLUnderOverElementImpl.class);
                _elementTypesMathML.put("mfrac", MathMLFractionElementImpl.class);
                _elementTypesMathML.put("msqrt", MathMLRadicalElementImpl.class);
                _elementTypesMathML.put("mroot", MathMLRadicalElementImpl.class);
                _elementTypesMathML.put("mrow", MathMLPresentationContainerImpl.class);
                _elementTypesMathML.put("mpadded", MathMLPaddedElementImpl.class);
                _elementTypesMathML.put("mfenced", MathMLFencedElementImpl.class);
                _elementTypesMathML.put("menclose", MathMLEncloseElementImpl.class);
                _elementTypesMathML.put("mglyph", MathMLGlyphElementImpl.class);
                _elementTypesMathML.put("maligngroup", MathMLAlignGroupElementImpl.class);
                _elementTypesMathML.put("malignmark", MathMLAlignMarkElementImpl.class);
                _elementTypesMathML.put("mtext", MathMLPresentationTokenImpl.class);
                _elementTypesMathML.put("mspace", MathMLSpaceElementImpl.class);
                _elementTypesMathML.put("ms", MathMLStringLitElementImpl.class);
                _elementTypesMathML.put("mphantom", MathMLPresentationContainerImpl.class);
                _elementTypesMathML.put("maction", MathMLActionElementImpl.class);
                _elementTypesMathML.put("merror", MathMLPresentationContainerImpl.class);
                // content
                _elementTypesMathML.put("apply", MathMLApplyElementImpl.class);
                _elementTypesMathML.put("ci", MathMLCiElementImpl.class);
                _elementTypesMathML.put("cn", MathMLCnElementImpl.class);
                _elementTypesMathML.put("csymbol", MathMLCsymbolElementImpl.class);
                _elementTypesMathML.put("bvar", MathMLBvarElementImpl.class);
                _elementTypesMathML.put("condition", MathMLConditionElementImpl.class);
                _elementTypesMathML.put("uplimit", MathMLContentContainerImpl.class);
                _elementTypesMathML.put("lowlimit", MathMLContentContainerImpl.class);
                _elementTypesMathML.put("domainofapplication", MathMLContentContainerImpl.class);
                _elementTypesMathML.put("degree", MathMLContentContainerImpl.class);
                _elementTypesMathML.put("otherwise", MathMLContentContainerImpl.class);
                // matrices
                _elementTypesMathML.put("vector", MathMLVectorElementImpl.class);
                _elementTypesMathML.put("matrix", MathMLMatrixElementImpl.class);
                _elementTypesMathML.put("matrixrow", MathMLMatrixrowElementImpl.class);
                // arithmetic
                _elementTypesMathML.put("plus", MathMLNaryOpImpl.class);
                _elementTypesMathML.put("minus", MathMLBinaryOpImpl.class);
                _elementTypesMathML.put("times", MathMLNaryOpImpl.class);
                _elementTypesMathML.put("divide", MathMLBinaryOpImpl.class);
                _elementTypesMathML.put("quotient", MathMLBinaryOpImpl.class);
                _elementTypesMathML.put("rem", MathMLBinaryOpImpl.class);
                _elementTypesMathML.put("power", MathMLBinaryOpImpl.class);
                _elementTypesMathML.put("root", MathMLFunctionImpl.class);
                _elementTypesMathML.put("min", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("max", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("gcd", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("lcm", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("floor", MathMLFunctionImpl.class);
                _elementTypesMathML.put("ceiling", MathMLFunctionImpl.class);
                _elementTypesMathML.put("factorial", MathMLUnaryOpImpl.class);
                _elementTypesMathML.put("conjugate", MathMLUnaryOpImpl.class);
                _elementTypesMathML.put("abs", MathMLFunctionImpl.class);
                _elementTypesMathML.put("arg", MathMLFunctionImpl.class);
                _elementTypesMathML.put("real", MathMLFunctionImpl.class);
                _elementTypesMathML.put("imaginary", MathMLFunctionImpl.class);
                // calculus
                _elementTypesMathML.put("int", MathMLUnaryOpImpl.class);
                _elementTypesMathML.put("diff", MathMLUnaryOpImpl.class);
                _elementTypesMathML.put("partialdiff", MathMLUnaryOpImpl.class);
                _elementTypesMathML.put("divergence", MathMLUnaryOpImpl.class);
                _elementTypesMathML.put("grad", MathMLUnaryOpImpl.class);
                _elementTypesMathML.put("curl", MathMLUnaryOpImpl.class);
                _elementTypesMathML.put("laplacian", MathMLUnaryOpImpl.class);
                // functional
                _elementTypesMathML.put("inverse", MathMLUnaryOpImpl.class);
                _elementTypesMathML.put("compose", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("ident", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("domain", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("codomain", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("image", MathMLPredefinedSymbolImpl.class);
                // sequences
                _elementTypesMathML.put("sum", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("product", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("limit", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("tendsto", MathMLTendsToElementImpl.class);
                // logic
                _elementTypesMathML.put("and", MathMLNaryOpImpl.class);
                _elementTypesMathML.put("or", MathMLNaryOpImpl.class);
                _elementTypesMathML.put("xor", MathMLNaryOpImpl.class);
                _elementTypesMathML.put("not", MathMLUnaryOpImpl.class);
                _elementTypesMathML.put("implies", MathMLBinaryRelImpl.class);
                _elementTypesMathML.put("forall", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("exists", MathMLPredefinedSymbolImpl.class);
                // relations
                _elementTypesMathML.put("eq", MathMLNaryRelImpl.class);
                _elementTypesMathML.put("neq", MathMLBinaryRelImpl.class);
                _elementTypesMathML.put("gt", MathMLNaryRelImpl.class);
                _elementTypesMathML.put("lt", MathMLNaryRelImpl.class);
                _elementTypesMathML.put("geq", MathMLNaryRelImpl.class);
                _elementTypesMathML.put("leq", MathMLNaryRelImpl.class);
                _elementTypesMathML.put("equivalent", MathMLNaryRelImpl.class);
                _elementTypesMathML.put("approx", MathMLBinaryRelImpl.class);
                _elementTypesMathML.put("factorof", MathMLBinaryRelImpl.class);
                // set theory
                _elementTypesMathML.put("set", MathMLSetElementImpl.class);
                _elementTypesMathML.put("list", MathMLListElementImpl.class);
                _elementTypesMathML.put("union", MathMLNaryOpImpl.class);
                _elementTypesMathML.put("intersect", MathMLNaryOpImpl.class);
                _elementTypesMathML.put("cartesianproduct", MathMLNaryOpImpl.class);
                _elementTypesMathML.put("in", MathMLBinaryRelImpl.class);
                _elementTypesMathML.put("notin", MathMLBinaryRelImpl.class);
                _elementTypesMathML.put("subset", MathMLNaryRelImpl.class);
                _elementTypesMathML.put("prsubset", MathMLNaryRelImpl.class);
                _elementTypesMathML.put("notsubset", MathMLBinaryRelImpl.class);
                _elementTypesMathML.put("notprsubset", MathMLBinaryRelImpl.class);
                _elementTypesMathML.put("setdiff", MathMLBinaryOpImpl.class);
                _elementTypesMathML.put("card", MathMLFunctionImpl.class);
                // functions
                _elementTypesMathML.put("exp", MathMLFunctionImpl.class);
                _elementTypesMathML.put("ln", MathMLFunctionImpl.class);
                _elementTypesMathML.put("log", MathMLFunctionImpl.class);
                // trig functions
                _elementTypesMathML.put("sin", MathMLFunctionImpl.class);
                _elementTypesMathML.put("cos", MathMLFunctionImpl.class);
                _elementTypesMathML.put("tan", MathMLFunctionImpl.class);
                _elementTypesMathML.put("csc", MathMLFunctionImpl.class);
                _elementTypesMathML.put("sec", MathMLFunctionImpl.class);
                _elementTypesMathML.put("cot", MathMLFunctionImpl.class);
                _elementTypesMathML.put("sinh", MathMLFunctionImpl.class);
                _elementTypesMathML.put("cosh", MathMLFunctionImpl.class);
                _elementTypesMathML.put("tanh", MathMLFunctionImpl.class);
                _elementTypesMathML.put("csch", MathMLFunctionImpl.class);
                _elementTypesMathML.put("sech", MathMLFunctionImpl.class);
                _elementTypesMathML.put("coth", MathMLFunctionImpl.class);
                _elementTypesMathML.put("arcsin", MathMLFunctionImpl.class);
                _elementTypesMathML.put("arccos", MathMLFunctionImpl.class);
                _elementTypesMathML.put("arctan", MathMLFunctionImpl.class);
                _elementTypesMathML.put("arccsc", MathMLFunctionImpl.class);
                _elementTypesMathML.put("arcsec", MathMLFunctionImpl.class);
                _elementTypesMathML.put("arccot", MathMLFunctionImpl.class);
                _elementTypesMathML.put("arcsinh", MathMLFunctionImpl.class);
                _elementTypesMathML.put("arccosh", MathMLFunctionImpl.class);
                _elementTypesMathML.put("arctanh", MathMLFunctionImpl.class);
                _elementTypesMathML.put("arccsch", MathMLFunctionImpl.class);
                _elementTypesMathML.put("arcsech", MathMLFunctionImpl.class);
                _elementTypesMathML.put("arccoth", MathMLFunctionImpl.class);
                // statistics
                _elementTypesMathML.put("mean", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("mode", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("median", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("moment", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("momentabout", MathMLContentContainerImpl.class);
                _elementTypesMathML.put("sdev", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("variance", MathMLPredefinedSymbolImpl.class);
                // linear algebra
                _elementTypesMathML.put("determinant", MathMLFunctionImpl.class);
                _elementTypesMathML.put("transpose", MathMLUnaryOpImpl.class);
                _elementTypesMathML.put("selector", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("vectorproduct", MathMLBinaryOpImpl.class);
                _elementTypesMathML.put("scalarproduct", MathMLBinaryOpImpl.class);
                _elementTypesMathML.put("outerproduct", MathMLBinaryOpImpl.class);
                // symbols
                _elementTypesMathML.put("integers", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("reals", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("rationals", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("naturalnumbers", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("complexes", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("primes", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("exponentiale", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("imaginaryi", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("notanumber", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("true", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("false", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("emptyset", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("pi", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("eulergamma", MathMLPredefinedSymbolImpl.class);
                _elementTypesMathML.put("infinity", MathMLPredefinedSymbolImpl.class);
                // deprecated
                _elementTypesMathML.put("fn", MathMLFnElementImpl.class);
                _elementTypesMathML.put("reln", MathMLContentContainerImpl.class);
        }

        public MathMLDocumentImpl() {
                super();
        }

        public DocumentType getDoctype() {
                if (docType == null) {
                        docType = new DocumentTypeImpl(this, "math", DOCTYPE_PUBLIC, DOCTYPE_SYSTEM);
                }
                return docType;
        }

        public Element getDocumentElement() {
                Node math;

                math = super.getDocumentElement();
                if (math == null) {
                        math = new MathMLMathElementImpl(this, "math");
                        appendChild(math);
                }
                return (Element) math;
        }

        /** Xerces specific */
        public Element createElementNS(String namespaceURI, String qualifiedName, String localpart) throws DOMException {
                return createElementNS(namespaceURI, qualifiedName);
        }

        public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
                if (MathMLElementImpl.mathmlURI.equals(namespaceURI)) {
                        String localName;
                        Class elemClass;
                        Constructor cnst;

                        int index = qualifiedName.indexOf(':');
                        if (index < 0) {
                                localName = qualifiedName;
                        } else {
                                localName = qualifiedName.substring(index+1);
                        }

                        elemClass = (Class) _elementTypesMathML.get(localName);
                        if (elemClass != null) {
                                try {
                                        cnst = elemClass.getConstructor(_elemClassSigMathML);
                                        return (Element) cnst.newInstance(new Object[] {this, qualifiedName});
                                } catch (Exception except) {
                                        Throwable thrw;

                                        if (except instanceof InvocationTargetException) {
                                                thrw = ((InvocationTargetException) except).getTargetException();
                                        } else {
                                                thrw = except;
                                        }

                                        System.out.println("Exception " + thrw.getClass().getName());
                                        System.out.println(thrw.getMessage());

                                        throw new IllegalStateException("Tag '" + qualifiedName + "' associated with an Element class that failed to construct.");
                                }
                        } else {
                                return new MathMLElementImpl(this, qualifiedName);
                        }
                } else {
                        return super.createElementNS(namespaceURI, qualifiedName);
                }
        }

        public String getReferrer() {
                return null;
        }
        public String getDomain() {
                return null;
        }
        public String getURI() {
                return null;
        }
}

