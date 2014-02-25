package JSci.maths;

/**
* This exception occurs when a numerical algorithm exceeds it maximum number of allowable iterations.
* @version 1.1
* @author Mark Hale
*/
public final class MaximumIterationsExceededException extends Exception {
        private final Object value;
        /**
        * Constructs a MaximumIterationsExceededException with no detail message.
        */
        public MaximumIterationsExceededException() {
            this(null, null);
        }
        /**
        * Constructs a MaximumIterationsExceededException with the specified detail message.
        */
        public MaximumIterationsExceededException(String s, Object value) {
                super(s);
                this.value = value;
        }
        /**
         * The value of the algorithm just before this exception was thrown.
         */
        public Object getValue() {
            return value;
        }
}
