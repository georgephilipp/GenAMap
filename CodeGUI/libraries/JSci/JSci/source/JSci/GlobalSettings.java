package JSci;

/**
* The GlobalSettings class controls numeric behaviour.
* @version 1.0
* @author Mark Hale
*/
public final class GlobalSettings extends Object {
        /**
        * Zero tolerance.
        * Default value is zero.
	* Note, in most cases, setting this to a non-zero value
	* will break the transitivity of <code>equals()</code>.
        */
        public static double ZERO_TOL=0.0;
        private GlobalSettings() {}
}

