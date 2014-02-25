package JSci.astro;

import JSci.physics.PhysicalConstants;

/**
 * A class defining common constants in astronomy.
 * All values expressed in SI units.
 * @version 1.0
 * @author Silvere Martin-Michiellot
 * @author Mark Hale
 */
public interface AstronomicalConstants extends PhysicalConstants {
	/**
        * Length of an Astronomical Unit, in meters.
	*/
	double AU = 149597870660.0;
	/**
        * Length of a parsec, in meters.
	*/
	double PARSEC = 3.0856775807E+16;
	/**
        * Length of a (Gregorian) light year (defined), in meters.
	*/
	double LIGHT_YEAR = 9460536207068016.0;
}
