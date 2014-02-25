package JSci.chemistry;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import JSci.physics.*;

/**
* A class representing molecules.
* @version 0.2
* @author Mark Hale
*/
public class Molecule extends Particle {
        /**
        * Atoms.
        */
        private final Set atoms = new HashSet();
        /**
        * Constructs a molecule from a chemical formula.
        */
        public Molecule(String formula) {
                if(formula == null || formula.length() == 0)
                        throw new IllegalArgumentException("Formula cannot be null or empty.");
                int startPos = 0, endPos;
                boolean isNumber = false;
                String symbol = null;
                int count;
                for(int i=1; i<formula.length(); i++) {
                        char ch = formula.charAt(i);
                        if(Character.isUpperCase(ch)) {
                                endPos = i;
                                if(isNumber) {
                                        count = Integer.parseInt(formula.substring(startPos, endPos));
                                        isNumber = false;
                                } else {
                                        symbol = formula.substring(startPos, endPos);
                                        count = 1;
                                }
                                String name = PeriodicTable.getName(symbol);
                                if(name != null) {
                                        Element element = PeriodicTable.getElement(name);
                                        for(int j=0; j<count; j++)
                                                atoms.add(new Atom(element));
                                }
                                startPos = endPos;
                        } else if(!isNumber && Character.isDigit(ch)) {
                                isNumber = true;
                                endPos = i;
                                symbol = formula.substring(startPos, endPos);
                                startPos = endPos;
                        }
                }
                endPos = formula.length();
                if(isNumber) {
                        count = Integer.parseInt(formula.substring(startPos, endPos));
                        isNumber = false;
                } else {
                        symbol = formula.substring(startPos, endPos);
                        count = 1;
                }
                String name = PeriodicTable.getName(symbol);
                if(name != null) {
                        Element element = PeriodicTable.getElement(name);
                        for(int j=0; j<count; j++)
                                atoms.add(new Atom(element));
                }
        }
        /**
        * Constructs a molecule from two atoms.
        */
        public Molecule(Atom a, Atom b) {
                atoms.add(a);
                atoms.add(b);
        }
        /**
        * Constructs a molecule from an array of atoms.
        */
        public Molecule(Atom a[]) {
                for(int i=0; i<a.length; i++)
                        atoms.add(a[i]);
        }
        /**
        * Returns the atoms in this molecule.
        */
        public Set getAtoms() {
                return Collections.unmodifiableSet(atoms);
        }
        /**
        * Binds with an atom.
        * @return this.
        */
        public Molecule bind(Atom a) {
                atoms.add(a);
                return this;
        }
        /**
        * Binds with a molecule.
        * @return this.
        */
        public Molecule bind(Molecule m) {
                atoms.addAll(m.atoms);
                return this;
        }
}

