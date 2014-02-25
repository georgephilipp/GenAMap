package JSci.biology;

import java.lang.Double;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import JSci.chemistry.Molecule;

/**
 * This class provides access to amino acids.
 * @version 1.0
 * @author Silvere Martin-Michiellot
 * @author Mark Hale
 */
public final class AminoAcids {
        private static final Map table = new HashMap();
        private static Map symbols;

        private AminoAcids() {}
        /**
        * Returns the name for a symbol.
        */
        public static String getName(String symbol) {
                if(symbols == null)
                        symbols = loadIndex();
                return (String)symbols.get(symbol);
        }
    /**
     * Returns an amino acid.
     */
    public static AminoAcid getAminoAcid(String name) {
        name=name.toLowerCase();
        AminoAcid aminoacid = (AminoAcid) table.get(name);
        if(aminoacid==null) {
            aminoacid=loadAminoAcid("aminoacids/"+name.replace(' ', '-')+".xml");
                if(aminoacid != null)
            table.put(name,aminoacid);
        }
        return aminoacid;
    }
        /**
        * Loads the XML index.
        */
        private static Map loadIndex() {
                DocumentBuilderFactory docFactory=DocumentBuilderFactory.newInstance();
                try {
                        DocumentBuilder docBuilder=docFactory.newDocumentBuilder();
                        Document doc=docBuilder.parse(AminoAcids.class.getResource("aminoacids/index.xml").toString());
                        Node root=doc.getDocumentElement();
                        NodeList nl=root.getChildNodes();
                        Map index = new HashMap();
                        for(int i=0; i<nl.getLength(); i++) {
                                Node node = nl.item(i);
                                if(node.getNodeName().equals("amino-acid")) {
                                        NamedNodeMap attr = node.getAttributes();
                                        index.put(attr.getNamedItem("symbol").getNodeValue(), attr.getNamedItem("name").getNodeValue());
                                }
                        }
                        return index;
                } catch(ParserConfigurationException e) {
                        return Collections.EMPTY_MAP;
                } catch(IOException e) {
                        return Collections.EMPTY_MAP;
                } catch(SAXException e) {
                        return Collections.EMPTY_MAP;
                }
        }
    /**
     * Loads an amino acid from its XML resource.
     */
    private static AminoAcid loadAminoAcid(String resname) {
        DocumentBuilderFactory docFactory=DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder=docFactory.newDocumentBuilder();
            Document doc=docBuilder.parse(AminoAcids.class.getResource(resname).toString());
            Node root=doc.getDocumentElement();
            NodeList nl=root.getChildNodes();
            String name=findStringValue(nl,"name");
            String abbreviation=findStringValue(nl,"abbreviation");
            String symbol=findStringValue(nl,"symbol");
            String formula=findStringValue(nl,"molecular-formula");
            AminoAcid aminoacid=new AminoAcid(name, abbreviation, symbol, formula);
            aminoacid.setMolecularWeight(findDoubleValue(nl,"molecular-weight"));
            aminoacid.setIsoelectricPoint(findDoubleValue(nl,"isoelectric-point"));
            aminoacid.setCASRegistryNumber(findStringValue(nl,"CAS-registry-number"));
            return aminoacid;
                } catch(ParserConfigurationException e) {
                        return null;
                } catch(IOException e) {
                        return null;
                } catch(SAXException e) {
                        return null;
        }
    }
    private static String findStringValue(NodeList nl,String name) {
        Node item;
        for(int i=0;i<nl.getLength();i++) {
            item=nl.item(i);
            if(item.getNodeName().equals(name))
                return item.getFirstChild().getNodeValue();
        }
        return "";
    }
    private static double findDoubleValue(NodeList nl,String name) {
        Node item;
        for(int i=0;i<nl.getLength();i++) {
            item=nl.item(i);
            if(item.getNodeName().equals(name))
                return Double.parseDouble(item.getFirstChild().getNodeValue());
        }
        return Double.NaN;
    }
}
