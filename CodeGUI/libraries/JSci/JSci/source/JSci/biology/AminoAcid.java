package JSci.biology;

import JSci.chemistry.Molecule;

/**
 * A class representing an Amino-Acid.
 * @version 1.0
 * @author Silvere Martin-Michiellot
 * @author Mark Hale
 */
public class AminoAcid extends Molecule {
    private String name;
    private String abbreviation;
    private String symbol;
    private String molecularFormula ;
    private double molecularWeight;
    private double isoelectricPoint;
    private String casRegistryNumber;
    /**
     * Constructs an AminoAcid.
     */
    public AminoAcid(String name, String abbreviation, String symbol, String molecularFormula) {
        super(molecularFormula);
        this.name=name;
        this.abbreviation=abbreviation;
        this.symbol=symbol;
        this.molecularFormula=molecularFormula;
    }
    /**
     * Returns the name.
     */
    public String getName() {
        return name;
    }
    /**
     * Returns the abbreviation.
     */
    public String getAbbreviation() {
        return abbreviation;
    }
    /**
     * Returns the symbol.
     */
    public String getSymbol() {
        return symbol;
    }
    /**
     * Returns the molecular formula.
     */
    public String getMolecularFormula() {
        return molecularFormula ;
    }
    /**
     * Returns the molecular weight.
     */
    public double getMolecularWeight() {
        return molecularWeight;
    }
    /**
     * Sets the molecular weight.
     */
    protected void setMolecularWeight(double molecularWeight) {
        this.molecularWeight=molecularWeight;
    }
    /**
     * Returns the isoelectric point.
     */
    public double getIsoelectricPoint() {
        return isoelectricPoint;
    }
    /**
     * Sets the isoelectric point.
     */
    protected void setIsoelectricPoint(double isoelectricPoint) {
        this.isoelectricPoint=isoelectricPoint;
    }
    /**
     * Returns the CAS registry number.
     */
    public String getCASRegistryNumber() {
        return casRegistryNumber;
    }
    /**
     * Sets the CAS registry number.
     */
    protected void setCASRegistryNumber(String casRegistryNumber) {
        this.casRegistryNumber=casRegistryNumber;
    }
}
