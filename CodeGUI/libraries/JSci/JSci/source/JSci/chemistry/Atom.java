package JSci.chemistry;

import JSci.physics.*;
import JSci.physics.particles.*;

/**
* A class representing atoms.
* @version 0.2
* @author Mark Hale
*/
public class Atom extends Particle {
        /**
        * Shell.
        */
        private Lepton shell[];
        private final int shellSize;
        /**
        * Nucleus.
        */
        private Nucleon nucleus[];
        private final int nucleusSize;
        /**
        * Constructs an atom.
        */
        public Atom(Element e) {
                shellSize = e.getAtomicNumber();
                nucleusSize = e.getMassNumber();
        }
        /**
        * Returns the nucleus.
        */
        public Nucleon[] getNucleus() {
                if(nucleus == null) {
                        nucleus = new Nucleon[nucleusSize];
                        int i;
                        for(i=0; i<shellSize; i++)
                                nucleus[i] = new Proton();
                        for(; i<nucleusSize; i++)
                                nucleus[i] = new Neutron();
                }
                return nucleus;
        }
        /**
        * Returns the electron shell.
        */
        public Lepton[] getShell() {
                if(shell == null) {
                        shell = new Lepton[shellSize];
                        for(int i=0; i<shellSize; i++)
                                shell[i] = new Electron();
                }
                return shell;
        }
        /**
        * Binds this atom with another.
        */
        public Molecule bind(Atom a) {
                return new Molecule(this, a);
        }
}

