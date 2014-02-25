package JSci.chemistry;

/**
* A bond between two atoms.
*/
public abstract class Bond extends Object {
        protected final Atom atom1;
        protected final Atom atom2;

        public Bond(Atom a, Atom b) {
                atom1 = a;
                atom2 = b;
        }
}

