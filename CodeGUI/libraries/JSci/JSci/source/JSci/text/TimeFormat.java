package JSci.text;

import java.text.*;
import java.util.Date;

/**
* The TimeFormat class formats a number as a date string,
* by first scaling the number and adding an offset,
* and then treating the result as a time in milliseconds.
* @version 1.0
* @author Mark Hale
*/
public final class TimeFormat extends NumberFormat {
        private DateFormat dateFormat;
        private double scale;
        private long offset;

        /**
         * Constructs a TimeFormat object.
         * @param offset in milliseconds.
         */
        public TimeFormat(DateFormat dateFormat, double scale, long offset) {
                this.dateFormat = dateFormat;
                this.scale = scale;
                this.offset = offset;
        }
        private Date toDate(double x) {
                return new Date(((long)(scale*x))+offset);
        }
        /**
         */
        public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
                pos.setBeginIndex(-1);
                pos.setEndIndex(-1);
                return toAppendTo.append(dateFormat.format(toDate(number)));
        }
        public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
                pos.setBeginIndex(-1);
                pos.setEndIndex(-1);
                return toAppendTo.append(dateFormat.format(toDate(number)));
        }
        private Number toNumber(Date date) {
                return new Double((date.getTime()-offset)/scale);
        }
        public Number parse(String text, ParsePosition parsePosition) {
                return toNumber(dateFormat.parse(text, parsePosition));
        }
}

