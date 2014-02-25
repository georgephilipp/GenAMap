package JSci.chemistry;

/**
* A covalent bond between two atoms.
*/
public class CovalentBond extends Bond {
        /** Sigma bond */
        public static final int SINGLE = 1;
        /** Sigma and one pi bond */
        public static final int DOUBLE = 2;
        /** Sigma and two pi bonds */
        public static final int TRIPLE = 3;

        private final int type;
        /**
        * Constructs a sigma bond between two atoms.
        */
        public CovalentBond(Atom a, Atom b) {
                this(a, b, SINGLE);
        }
        /**
        * Constructs a covalent bond between two atoms.
        * @param bondType one of SINGLE, DOUBLE or TRIPLE.
        */
        public CovalentBond(Atom a, Atom b, int bondType) {
                super(a, b);
                type = bondType;
        }
}

