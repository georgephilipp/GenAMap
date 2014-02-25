package JSci.io;

import java.util.Set;
import java.util.HashSet;
import JSci.maths.*;
import JSci.maths.matrices.*;
import JSci.maths.vectors.*;
import JSci.maths.fields.*;
import org.w3c.dom.*;
import org.w3c.dom.mathml.*;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.apache.xerces.parsers.DOMParser;

/**
* The MathMLParser class will parse a MathML document into JSci objects.
* @version 0.9
* @author Mark Hale
*/
public final class MathMLParser extends DOMParser {
        /**
        * Constructs a MathMLParser.
        */
        public MathMLParser() {
                try {
                        setProperty("http://apache.org/xml/properties/dom/document-class-name", "JSci.mathml.MathMLDocumentImpl");
                } catch(SAXNotRecognizedException e) {
                        e.printStackTrace();
                } catch(SAXNotSupportedException e) {
                        e.printStackTrace();
                }
        }
        /**
        * Translates the document into JSci objects.
        */
        public Object[] translateToJSciObjects() {
                return translateToJSciObjects(getDocument().getDocumentElement());
        }
	public static Object[] translateToJSciObjects(Node root) {
                Translator translator=new JSciObjectTranslator();
                return translator.translate(root);
	}
        /**
        * Translates the document into JSci code.
        */
        public Object[] translateToJSciCode() {
                return translateToJSciCode(getDocument().getDocumentElement());
        }
	public static Object[] translateToJSciCode(Node root) {
                Translator translator=new JSciCodeTranslator();
                return translator.translate(root);
	}
        /**
        * Translator.
        */
        static abstract class Translator extends Object {
                public Translator() {}
                public Object[] translate(Node root) {
                        return parseMATH(root);
                }
                /**
                * Parses the &lt;math&gt; node.
                */
                private Object[] parseMATH(Node n) {
                        int len=0;
                        final NodeList nl=n.getChildNodes();
                        final Object objList[]=new Object[nl.getLength()];
                        Object obj;
                        for(int i=0;i<objList.length;i++) {
                                obj=processNode(nl.item(i));
                                if(obj!=null) {
                                        objList[len]=obj;
                                        len++;
                                }
                        }
                        final Object parseList[]=new Object[len];
                        System.arraycopy(objList,0,parseList,0,len);
                        return parseList;
                }
                /**
                * Processes a node.
                */
                protected Object processNode(Node n) {
                        final String nodeName=n.getNodeName();
                        if(n instanceof MathMLApplyElement || nodeName.equals("reln"))
                                return parseAPPLY((MathMLContentContainer)n);
                        else if(n instanceof MathMLCnElement)
                                return parseCN((MathMLCnElement)n);
                        else if(n instanceof MathMLCiElement)
                                return parseCI((MathMLCiElement)n);
                        else if(n instanceof MathMLPredefinedSymbol)
                                return parsePredefinedSymbol((MathMLPredefinedSymbol)n);
                        else if(n instanceof MathMLVectorElement)
                                return parseVECTOR((MathMLVectorElement)n);
                        else if(n instanceof MathMLMatrixElement)
                                return parseMATRIX((MathMLMatrixElement)n);
                        else if(n instanceof MathMLSetElement)
                                return parseSET((MathMLSetElement)n);
                        else if(n instanceof MathMLStringLitElement)
                                return parseMS((MathMLStringLitElement)n);
                        else if(nodeName.equals("mtext"))
                                return parseMTEXT((MathMLPresentationToken)n);
                        else
                                return null;
                }
                protected abstract Object parseAPPLY(MathMLContentContainer n);
                protected abstract Object parseCN(MathMLCnElement n);
                protected abstract Object parseCI(MathMLCiElement n);
                protected abstract Object parseVECTOR(MathMLVectorElement n);
                protected abstract Object parseMATRIX(MathMLMatrixElement n);
                protected abstract Object parseSET(MathMLSetElement n);
                protected abstract Object parseMS(MathMLStringLitElement n);
                protected abstract Object parseMTEXT(MathMLPresentationToken n);
                protected abstract Object parsePredefinedSymbol(MathMLPredefinedSymbol n);
        }
        /**
        * JSci object translator.
        */
        static class JSciObjectTranslator extends Translator {
                private final int INTEGER=0;
                private final int DOUBLE=1;
                private final int COMPLEX=2;
                private int setID=1;

                public JSciObjectTranslator() {}
                /**
                * Parses &lt;apply&gt; tags.
                * @return MathMLExpression.
                */
                protected Object parseAPPLY(MathMLContentContainer n) {
                        final MathMLExpression expr=new MathMLExpression();
                        final NodeList nl=n.getChildNodes();
                        Object obj;
                        int i;
                        for(i=0;nl.item(i).getNodeType()==Node.TEXT_NODE;i++)
                                ;
                        expr.setOperation(nl.item(i).getNodeName());
                        for(;i<nl.getLength();i++) {
                                obj=processNode(nl.item(i));
                                if(obj!=null)
                                        expr.addArgument(obj);
                        }
                        return expr;
                }
                /**
                * Parses &lt;cn&gt; tags.
                * @return Ring.Member.
                */
                protected Object parseCN(MathMLCnElement n) {
                        return parseNumber(n);
                }
                private Ring.Member parseNumber(MathMLCnElement n) {
                // support only base 10
                        if(!n.getBase().equals("10"))
                                return null;
                        final String attrType = n.getType();
                        if(attrType.equals("real")) {
                                return new MathDouble(n.getFirstChild().getNodeValue());
                        } else if(attrType.equals("integer")) {
                                return new MathInteger(n.getFirstChild().getNodeValue());
                        } else if(attrType.equals("rational")) {
                                final Node num=n.getArgument(1);
                                final Node denom=n.getArgument(2);
                                return new MathDouble(num.getNodeValue()).divide(new MathDouble(denom.getNodeValue()));
                        } else if(attrType.equals("complex-cartesian")) {
                                final Node re=n.getArgument(1);
                                final Node im=n.getArgument(2);
                                return new Complex(
                                        new Double(re.getNodeValue()).doubleValue(),
                                        new Double(im.getNodeValue()).doubleValue()
                                );
                        } else if(attrType.equals("complex-polar")) {
                                final Node mod=n.getArgument(1);
                                final Node arg=n.getArgument(2);
                                return Complex.polar(
                                        new Double(mod.getNodeValue()).doubleValue(),
                                        new Double(arg.getNodeValue()).doubleValue()
                                );
                        } else if(attrType.equals("constant")) {
                                final String value=n.getFirstChild().getNodeValue();
                                if(value.equals("&pi;"))
                                        return RealField.PI;
                                else if(value.equals("&ee;") || value.equals("&ExponentialE;"))
                                        return RealField.E;
                                else if(value.equals("&ii;") || value.equals("&ImaginaryI;"))
                                        return ComplexField.I;
                                else if(value.equals("&gamma;"))
                                        return RealField.GAMMA;
                                else if(value.equals("&infty;") || value.equals("&infin;"))
                                        return RealField.INFINITY;
                                else if(value.equals("&NaN;") || value.equals("&NotANumber;"))
                                        return RealField.NaN;
                                /*
                                else if(value.equals("&true;"))
                                        return MathBoolean.TRUE;
                                else if(value.equals("&false;"))
                                        return MathBoolean.FALSE;
                                 */
                                else
                                        return null;
                        } else
                                return null;
                }
                protected Object parsePredefinedSymbol(MathMLPredefinedSymbol n) {
                    String nodeName = n.getNodeName();
                    if(nodeName.equals("pi"))
                        return RealField.PI;
                    else if(nodeName.equals("exponentiale"))
                        return RealField.E;
                    else if(nodeName.equals("imaginaryi"))
                        return ComplexField.I;
                    else if(nodeName.equals("eulergamma"))
                        return RealField.GAMMA;
                    else if(nodeName.equals("infinity"))
                        return RealField.INFINITY;
                    else if(nodeName.equals("notanumber"))
                        return RealField.NaN;
                    /*
                    else if(nodeName.equals("true"))
                        return MathBoolean.TRUE;
                    else if(nodeName.equals("false"))
                        return MathBoolean.FALSE;
                     */
                    else
                        return null;
                }
                /**
                * Parses &lt;ci&gt; tags.
                * @return String.
                */
                protected Object parseCI(MathMLCiElement n) {
                        return n.getFirstChild().getNodeValue();
                }
                /**
                * Parses &lt;vector&gt; tags.
                * @return MathVector.
                */
                protected Object parseVECTOR(MathMLVectorElement n) {
                        int len=0,type=INTEGER;
                        final Ring.Member num[]=new Ring.Member[n.getNcomponents()];
                        for(int i=0;i<num.length;i++) {
                                MathMLContentElement elem = n.getComponent(i+1);
                                if(elem instanceof MathMLCnElement) {
                                        num[len]=parseNumber((MathMLCnElement)elem);
                                        if(num[len]!=null) {
                                        // work out number format needed
                                                if(num[len] instanceof MathDouble && type<DOUBLE)
                                                        type=DOUBLE;
                                                else if(num[len] instanceof Complex && type<COMPLEX)
                                                        type=COMPLEX;
                                                len++;
                                        }
                                }
                        }
                // output to JSci objects
                        if(type==INTEGER) {
                                final int array[]=new int[len];
                                for(int i=0;i<array.length;i++)
                                        array[i]=((MathInteger)num[i]).value();
                                return new IntegerVector(array);
                        } else if(type==DOUBLE) {
                                final double array[]=new double[len];
                                for(int i=0;i<array.length;i++) {
                                        if(num[i] instanceof MathInteger)
                                                array[i]=((MathInteger)num[i]).value();
                                        else
                                                array[i]=((MathDouble)num[i]).value();
                                }
                                return new DoubleVector(array);
                        } else {
                                final Complex array[]=new Complex[len];
                                for(int i=0;i<array.length;i++) {
                                        if(num[i] instanceof MathInteger)
                                                array[i]=new Complex(((MathInteger)num[i]).value(),0.0);
                                        else if(num[i] instanceof MathDouble)
                                                array[i]=new Complex(((MathDouble)num[i]).value(),0.0);
                                        else
                                                array[i]=(Complex)num[i];
                                }
                                return new ComplexVector(array);
                        }
                }
                /**
                * Parses &lt;matrix&gt; tags.
                * @return Matrix.
                */
                protected Object parseMATRIX(MathMLMatrixElement n) {
                        int rows=0,cols=Integer.MAX_VALUE;
                        final Ring.Member num[][]=new Ring.Member[n.getNrows()][];
                        for(int i=0;i<num.length;i++) {
                                num[rows]=parseMatrixRow(n.getRow(i+1));
                                if(num[rows].length<cols)
                                        cols=num[rows].length;
                                rows++;
                        }
                // work out number format needed
                        int type=INTEGER;
                        for(int j,i=0;i<rows;i++) {
                                for(j=0;j<cols;j++) {
                                        if(num[i][j] instanceof MathDouble && type<DOUBLE)
                                                type=DOUBLE;
                                        else if(num[i][j] instanceof Complex && type<COMPLEX)
                                                type=COMPLEX;
                                }
                        }
                // output to JSci objects
                        if(type==INTEGER) {
                                final int array[][]=new int[rows][cols];
                                for(int j,i=0;i<rows;i++) {
                                        for(j=0;j<cols;j++)
                                                array[i][j]=((MathInteger)num[i][j]).value();
                                }
                                if(rows==cols)
                                        return new IntegerSquareMatrix(array);
                                else
                                        return new IntegerMatrix(array);
                        } else if(type==DOUBLE) {
                                final double array[][]=new double[rows][cols];
                                for(int j,i=0;i<rows;i++) {
                                        for(j=0;j<cols;j++) {
                                                if(num[i][j] instanceof MathInteger)
                                                        array[i][j]=((MathInteger)num[i][j]).value();
                                                else
                                                        array[i][j]=((MathDouble)num[i][j]).value();
                                        }
                                }
                                if(rows==cols)
                                        return new DoubleSquareMatrix(array);
                                else
                                        return new DoubleMatrix(array);
                        } else {
                                final Complex array[][]=new Complex[rows][cols];
                                for(int j,i=0;i<rows;i++) {
                                        for(j=0;j<cols;j++) {
                                                if(num[i][j] instanceof MathInteger)
                                                        array[i][j]=new Complex(((MathInteger)num[i][j]).value(),0.0);
                                                else if(num[i][j] instanceof MathDouble)
                                                        array[i][j]=new Complex(((MathDouble)num[i][j]).value(),0.0);
                                                else
                                                        array[i][j]=(Complex)num[i][j];
                                        }
                                }
                                if(rows==cols)
                                        return new ComplexSquareMatrix(array);
                                else
                                        return new ComplexMatrix(array);
                        }
                }
                /**
                * Parses &lt;matrixrow&gt; tags.
                */
                private Ring.Member[] parseMatrixRow(MathMLMatrixrowElement n) {
                        int len=0;
                        final Ring.Member num[]=new Ring.Member[n.getNEntries()];
                        for(int i=0;i<num.length;i++) {
                                MathMLContentElement elem = n.getEntry(i+1);
                                if(elem instanceof MathMLCnElement) {
                                        num[len]=parseNumber((MathMLCnElement)elem);
                                        if(num[len]!=null)
                                                len++;
                                }
                        }
                        final Ring.Member row[]=new Ring.Member[len];
                        System.arraycopy(num,0,row,0,len);
                        return row;
                }
                /**
                * Parses &lt;set&gt; tags.
                * @return FiniteSet.
                */
                protected Object parseSET(MathMLSetElement n) {
                        final NodeList nl = n.getChildNodes();
                        final Set elements = new HashSet(nl.getLength());
                        for(int i=0;i<nl.getLength();i++) {
                                Node child = nl.item(i);
                                if(child instanceof MathMLCiElement)
                                        elements.add(parseCI((MathMLCiElement)child));
                        }
                // output to JSci objects
                        return new FiniteSet(elements);
                }
                /**
                * Parses &lt;ms&gt; tags.
                * @return String.
                */
                protected Object parseMS(MathMLStringLitElement n) {
                        return n.getFirstChild().getNodeValue();
                }
                /**
                * Parses &lt;mtext&gt; tags.
                * @return String.
                */
                protected Object parseMTEXT(MathMLPresentationToken n) {
                    Node child = n.getFirstChild();
                    if(child == null)
                    {
                        return "";
                    }
                    else
                    {
                        StringBuffer buf = new StringBuffer();
                        buf.append(child.getNodeValue());
                        while((child = child.getNextSibling()) != null)
                        {
                            buf.append(' ').append(child.getNodeValue());
                        }
                        return buf.toString();
                    }
                }
        }
        /**
        * JSci code translator.
        */
        static class JSciCodeTranslator extends Translator {
                public JSciCodeTranslator() {}
                /**
                * Parses &lt;apply&gt; tags.
                * @return String.
                */
                protected Object parseAPPLY(MathMLContentContainer n) {
                        final StringBuffer buf=new StringBuffer();
                        final NodeList nl=n.getChildNodes();
                        Object obj;
                        int i;
                        for(i=0;nl.item(i).getNodeType()==Node.TEXT_NODE;i++)
                                ;
                        String op=nl.item(i).getNodeName();
                        if(op.equals("plus"))
                                op="add";
                        else if(op.equals("minus"))
                                op="subtract";
                        else if(op.equals("times"))
                                op="multiply";
                        else if(op.equals("ln"))
                                op="log";
                        boolean isFirst=true;
                        boolean isUnary=true;
                        for(i++;i<nl.getLength();i++) {
                                obj=processNode(nl.item(i));
                                if(obj!=null) {
                                        if(isFirst) {
                                                buf.append(obj);
                                                isFirst=false;
                                        } else {
                                                buf.append('.').append(op).append('(').append(obj).append(')');
                                                isUnary=false;
                                        }
                                }
                        }
                        if(isUnary)
                        {
                            buf.append('.').append(op).append("()");
                        }
                        return buf.toString();
                }
                /**
                * Parses &lt;cn&gt; tags.
                * @return String.
                */
                protected Object parseCN(MathMLCnElement n) {
                // support only base 10
                        if(!n.getBase().equals("10"))
                                return null;
                        final String attrType = n.getType();
                        if(attrType.equals("real")) {
                                return "new MathDouble("+n.getFirstChild().getNodeValue()+')';
                        } else if(attrType.equals("integer")) {
                                return "new MathInteger("+n.getFirstChild().getNodeValue()+')';
                        } else if(attrType.equals("rational")) {
                                final Node num=n.getArgument(1);
                                final Node denom=n.getArgument(2);
                                return "new MathDouble("+num.getNodeValue()+'/'+denom.getNodeValue()+')';
                        } else if(attrType.equals("complex-cartesian")) {
                                final Node re=n.getArgument(1);
                                final Node im=n.getArgument(2);
                                return "new Complex("+re.getNodeValue()+','+im.getNodeValue()+')';
                        } else if(attrType.equals("complex-polar")) {
                                final Node mod=n.getArgument(1);
                                final Node arg=n.getArgument(2);
                                return "Complex.polar("+mod.getNodeValue()+','+arg.getNodeValue()+')';
                        } else if(attrType.equals("constant")) {
                                final String value=n.getFirstChild().getNodeValue();
                                if(value.equals("&pi;"))
                                        return "RealField.PI";
                                else if(value.equals("&ee;") || value.equals("&ExponentialE;"))
                                        return "RealField.E";
                                else if(value.equals("&ii;") || value.equals("&ImaginaryI;"))
                                        return "ComplexField.I";
                                else if(value.equals("&gamma;"))
                                        return "RealField.GAMMA";
                                else if(value.equals("&infty;") || value.equals("&infin;"))
                                        return "RealField.INFINITY";
                                else if(value.equals("&NaN;") || value.equals("&NotANumber;"))
                                        return "RealField.NaN";
                                else
                                        return null;
                        } else
                                return null;
                }
                /**
                * Parses &lt;ci&gt; tags.
                * @return String.
                */
                protected Object parseCI(MathMLCiElement n) {
                        return n.getFirstChild().getNodeValue();
                }
                /**
                * Parses &lt;vector&gt; tags.
                * @return String.
                */
                protected Object parseVECTOR(MathMLVectorElement n) {
                        return null;
                }
                /**
                * Parses &lt;matrix&gt; tags.
                * @return String.
                */
                protected Object parseMATRIX(MathMLMatrixElement n) {
                        return null;
                }
                /**
                * Parses &lt;set&gt; tags.
                * @return String.
                */
                protected Object parseSET(MathMLSetElement n) {
                        return null;
                }
                /**
                * Parses &lt;ms&gt; tags.
                * @return String.
                */
                protected Object parseMS(MathMLStringLitElement n) {
                        return n.getFirstChild().getNodeValue();
                }
                /**
                * Parses &lt;mtext&gt; tags.
                * @return String.
                */
                protected Object parseMTEXT(MathMLPresentationToken n) {
                    if(n.hasChildNodes())
                        return "/*\n"+n.getFirstChild().getNodeValue()+"\n*/";
                    else
                        return "";
                }
                protected Object parsePredefinedSymbol(MathMLPredefinedSymbol n) {
                    return n.getNodeName();
                }
        }
}
