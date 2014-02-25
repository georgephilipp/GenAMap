package BiNGO;

/*
 * Adapted from SignificantFigures.java in package com.Ostermiller.util
 * Routines for working with numbers in scientific notation.
 * Copyright (C) 2002 Stephen Ostermiller
 * http://ostermiller.org/contact.pl?regarding=Java+Utilities
 *
 */

/* * modified by Steven Maere (29/03/2005) :
* * changes : added one convenience method for converting number strings to scientific notation.
* *
* * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
* *
* * Authors : Steven Maere, Karel Heymans
* *
* * This program is free software; you can redistribute it and/or modify
* * it under the terms of the GNU General Public License as published by
* * the Free Software Foundation; either version 2 of the License, or
* * (at your option) any later version.
* *
* * This program is distributed in the hope that it will be useful,
* * but WITHOUT ANY WARRANTY; without even the implied warranty of
* * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* * The software and documentation provided hereunder is on an "as is" basis,
* * and the Flanders Interuniversitary Institute for Biotechnology
* * has no obligations to provide maintenance, support,
* * updates, enhancements or modifications.  In no event shall the
* * Flanders Interuniversitary Institute for Biotechnology
* * be liable to any party for direct, indirect, special,
* * incidental or consequential damages, including lost profits, arising
* * out of the use of this software and its documentation, even if
* * the Flanders Interuniversitary Institute for Biotechnology
* * has been advised of the possibility of such damage. See the
* * GNU General Public License for more details.
* *
* * You should have received a copy of the GNU General Public License
* * along with this program; if not, write to the Free Software
* * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
* *
*/

/**
 * A number with an associated number of significant figures.
 * This class handles parsing numbers, determining the number
 * of significant figures, adjusting the number of significant
 * figures (including scientific rounding), and displaying the number.
 * More information about this class is available from <a target="_top" href=
 * "http://ostermiller.org/utils/SignificantFigures.html">ostermiller.org</a>.
 * <p/>
 * When parsing a number to determine the number of significant figures,
 * these rules are used:
 * <ul>
 * <li>Non-zero digits are always significant.</li>
 * <li>All zeros between other significant digits are significant.</li>
 * <li>All zeros left of the decimal point between a significant digit and the decimal point are significant.</li>
 * <li>All trailing zeros to the right of the decimal point are significant.</li>
 * <li>If the number is contains no digits other than zero, every zero is significant.</li>
 * </ul>
 * <p/>
 * When rounding a number the following rules are used:
 * <ul>
 * <li>If the greatest insignificant digit is less than five, round down.</li>
 * <li>If the greatest insignificant digit is greater than five, round up.</li>
 * <li>If the greatest insignificant digit is five and followed by some non-zero digit, round up.</li>
 * <li>If the greatest insignificant digit is five and followed only by zeros, and the least significant
 * digit is odd, round up.</li>
 * <li>If the greatest insignificant digit is five and followed only by zeros, and the least significant
 * digit is even, round down.</li>
 * </ul>
 * <p/>
 * <p/>
 * Example of using this class to multiply numbers and display the result
 * with the proper number of significant figures:<br>
 * <pre> String[] args = {"1.0", "2.0", ...}
 * SignificantFigures number;
 * int sigs = Integer.MAX_VALUE;
 * double result = 1D;
 * for (int i=0; i&lt;args.length; i++){
 * &nbsp;   number = new SignificantFigures(args[i]);
 * &nbsp;   sigs = Math.min(sigs, number.getNumberSignificantFigures());
 * &nbsp;   result *= number.doubleValue();
 * }
 * number = new SignificantFigures(result);
 * number.setNumberSignificantFigures(sigs);
 * System.out.println(number);</pre>
 * <p/>
 * Example of using this class to add numbers and display the result
 * with the proper number of significant figures:<br>
 * <pre> String[] args = {"1.0", "2.0", ...}
 * SignificantFigures number;
 * int lsd = Integer.MIN_VALUE;
 * int msd = Integer.MIN_VALUE;
 * double result = 0D;
 * for (int i=0; i&lt;args.length; i++){
 * &nbsp;   number = new SignificantFigures(args[i]);
 * &nbsp;   lsd = Math.max(lsd, number.getLSD());
 * &nbsp;   msd = Math.max(msd, number.getMSD());
 * &nbsp;   result += number.doubleValue();
 * }
 * number = new SignificantFigures(result);
 * number.setLMSD(lsd, msd);
 * System.out.println(number);</pre>
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.00.00
 */
public class SignificantFigures extends Number {

    /**
     * In the case the a number
     * could not be parsed, the original is stored
     * for toString purposes.
     *
     * @since ostermillerutils 1.00.00
     */
    private String original;
    /**
     * Buffer of the significant digits.
     *
     * @since ostermillerutils 1.00.00
     */
    private StringBuffer digits;
    /**
     * The exponent of the digits if a
     * decimal place were inserted after
     * the first digit.
     *
     * @since ostermillerutils 1.00.00
     */
    private int mantissa = -1;
    /**
     * positive if true, negative if false.
     *
     * @since ostermillerutils 1.00.00
     */
    private boolean sign = true;
    /**
     * True if this number has no non-zero digits.
     *
     * @since ostermillerutils 1.00.00
     */
    private boolean isZero = false;

    /**
     * Create a SignificantFigures object from a String representation of a number.
     *
     * @param number String representation of the number.
     * @throws NumberFormatException if the String is not a valid number.
     * @since ostermillerutils 1.00.00
     */
    public SignificantFigures(String number) throws NumberFormatException {
        original = number;
        parse(original);
    }

    /**
     * Create a SignificantFigures object from a byte.
     *
     * @param number an 8 bit integer.
     * @since ostermillerutils 1.00.00
     */
    public SignificantFigures(byte number) {
        original = Byte.toString(number);
        try {
            parse(original);
        } catch (NumberFormatException nfe) {
            digits = null;
        }
    }

    /**
     * Create a SignificantFigures object from a short.
     *
     * @param number a 16 bit integer.
     * @since ostermillerutils 1.00.00
     */
    public SignificantFigures(short number) {
        original = Short.toString(number);
        try {
            parse(original);
        } catch (NumberFormatException nfe) {
            digits = null;
        }
    }

    /**
     * Create a SignificantFigures object from an integer.
     *
     * @param number a 32 bit integer.
     * @since ostermillerutils 1.00.00
     */
    public SignificantFigures(int number) {
        original = String.valueOf(number);
        try {
            parse(original);
        } catch (NumberFormatException nfe) {
            digits = null;
        }
    }

    /**
     * Create a SignificantFigures object from a long.
     *
     * @param number a 64 bit integer.
     * @since ostermillerutils 1.00.00
     */
    public SignificantFigures(long number) {
        original = Long.toString(number);
        try {
            parse(original);
        } catch (NumberFormatException nfe) {
            digits = null;
        }
    }

    /**
     * Create a SignificantFigures object from a float.
     *
     * @param number a 32 bit floating point.
     * @since ostermillerutils 1.00.00
     */
    public SignificantFigures(float number) {
        original = Float.toString(number);
        try {
            parse(original);
        } catch (NumberFormatException nfe) {
            digits = null;
        }
    }

    /**
     * Create a SignificantFigures object from a double.
     *
     * @param number a 64 bit floating point.
     * @since ostermillerutils 1.00.00
     */
    public SignificantFigures(double number) {
        original = Double.toString(number);
        try {
            parse(original);
        } catch (NumberFormatException nfe) {
            digits = null;
        }
    }

    /**
     * Create a SignificantFigures object from a java number such as
     * a BigDecimal, BigInteger, Byte, Double, Float, Integer, Long, or
     * Short.
     *
     * @param number a number.
     * @since ostermillerutils 1.00.00
     */
    public SignificantFigures(Number number) {
        original = number.toString();
        try {
            parse(original);
        } catch (NumberFormatException nfe) {
            digits = null;
        }
    }

    /*public static void main(String[] args){
        int significantFigures = 0;
        int lsd = Integer.MIN_VALUE;
        int msd = Integer.MAX_VALUE;
        for (int i=0; i<args.length; i++){
            if (args[i].equals("--sigfigs")){
                i++;
                significantFigures = Integer.parseInt(args[i]);
            } else if (args[i].equals("--lsd")){
                i++;
                lsd = Integer.parseInt(args[i]);
            } else if (args[i].equals("--msd")){
                i++;
                msd = Integer.parseInt(args[i]);
            } else {
                SignificantFigures sf = new SignificantFigures(args[i]);
                System.out.print(args[i] + " ");
                System.out.print(sf.getNumberSignificantFigures() + " ");
                System.out.print(sf.getLSD() + " ");
                if (significantFigures>0) sf.setNumberSignificantFigures(significantFigures);
                sf.setLMSD(lsd, msd);
                System.out.print(sf.toString() + " ");
                System.out.println(sf.toScientificNotation());
            }
        }
    }*/

    /**
     * Get the number of significant digits.
     * <p/>
     * If this number is not a number or infinity zero
     * will be returned.
     *
     * @return the number of significant digits in this number.
     * @since ostermillerutils 1.00.00
     */
    public int getNumberSignificantFigures() {
        if (digits == null) return 0;
        return digits.length();
    }

    /**
     * Adjust the number of significant figures such that the least
     * significant digit is at the given place.  This method may add
     * significant zeros to the end of this number, or remove significant
     * digits from this number.
     * <p/>
     * It is possible to remove all significant digits from this number which
     * will cause the string representation of this number to become "NaN".  This
     * could become a problem if you are adding numbers and the result is close
     * to zero.  All of the significant digits may get removed, even though the
     * result could be zero with some number of significant digits.  Its is safes
     * to use the setLMSD() method which will make a zero with the appropriate
     * number of significant figures in such instances.
     * <p/>
     * This method has no effect if this number is not a number or infinity.
     *
     * @param place the desired place of the least significant digit.
     * @return this number.
     * @since ostermillerutils 1.00.00
     */
    public SignificantFigures setLSD(int place) {
        setLMSD(place, Integer.MIN_VALUE);
        return this;
    }

    /**
     * Adjust the number of significant figures such that the leas
     * significant digit is at the given place.  This method may add
     * significant zeros to the end of this number, or remove significant
     * digits from this number.
     * <p/>
     * If all significant digits are removed from this number by truncating to
     * the least significant place, a zero will be created with significant figures
     * from the least to most significant places.
     * <p/>
     * This method has no effect if this number is not a number or infinity.
     *
     * @param leastPlace the desired place of the least significant digit or Integer.MIN_VALUE to ignore.
     * @param mostPlace  the desired place of the most significant digit or Integer.MIN_VALUE to ignore.
     * @return this number
     * @since ostermillerutils 1.00.00
     */
    public SignificantFigures setLMSD(int leastPlace, int mostPlace) {
        if (digits != null && leastPlace != Integer.MIN_VALUE) {
            int significantFigures = digits.length();
            int current = mantissa - significantFigures + 1;
            int newLength = significantFigures - leastPlace + current;
            if (newLength <= 0) {
                if (mostPlace == Integer.MIN_VALUE) {
                    original = "NaN";
                    digits = null;
                } else {
                    newLength = mostPlace - leastPlace + 1;
                    digits.setLength(newLength);
                    mantissa = leastPlace;
                    for (int i = 0; i < newLength; i++) {
                        digits.setCharAt(i, '0');
                    }
                    isZero = true;
                    sign = true;
                }
            } else {
                digits.setLength(newLength);
                for (int i = significantFigures; i < newLength; i++) {
                    digits.setCharAt(i, '0');
                }
            }
        }
        return this;
    }

    /**
     * Get the decimal place of the least significant digit.
     * <p/>
     * If this number is not a number or infinity Integer.MIN_VALUE
     * will be returned.
     *
     * @return the decimal place of the least significant digit.
     * @since ostermillerutils 1.00.00
     */
    public int getLSD() {
        if (digits == null) return Integer.MIN_VALUE;
        return mantissa - digits.length() + 1;
    }

    /**
     * Get the decimal place of the most significant digit.
     * <p/>
     * If this number is not a number or infinity Integer.MIN_VALUE
     * will be returned.
     *
     * @return the decimal place of the least significant digit.
     * @since ostermillerutils 1.00.00
     */
    public int getMSD() {
        if (digits == null) return Integer.MIN_VALUE;
        return mantissa + 1;
    }

    /**
     * Formats this number.
     * If the number is less than 10^-3 or greater than or equal to 10^7,
     * or the number might have an ambiguous number of significant figures,
     * scientific notation will be used.
     * <p/>
     * A string such as "NaN" or "Infinity" may be returned by this method.
     *
     * @return representation of this number.
     * @since ostermillerutils 1.00.00
     */
    public String toString() {
        if (digits == null) return original;
        StringBuffer digits = new StringBuffer(this.digits.toString());
        int length = digits.length();
        if (mantissa <= -4 || mantissa >= 7 ||
                (mantissa >= length &&
                        digits.charAt(digits.length() - 1) == '0') ||
                (isZero && mantissa != 0)) {
            // use scientific notation.
            if (length > 1) {
                digits.insert(1, '.');
            }
            if (mantissa != 0) {
                digits.append("E" + mantissa);
            }
        } else if (mantissa <= -1) {
            digits.insert(0, "0.");
            for (int i = mantissa; i < -1; i++) {
                digits.insert(2, '0');
            }
        } else if (mantissa + 1 == length) {
            if (length > 1 && digits.charAt(digits.length() - 1) == '0') {
                digits.append('.');
            }
        } else if (mantissa < length) {
            digits.insert(mantissa + 1, '.');
        } else {
            for (int i = length; i <= mantissa; i++) {
                digits.append('0');
            }
        }
        if (!sign) {
            digits.insert(0, '-');
        }
        return digits.toString();
    }

    /**
     * Formats this number in scientific notation.
     * <p/>
     * A string such as "NaN" or "Infinity" may be returned by this method.
     *
     * @return representation of this number in scientific notation.
     * @since ostermillerutils 1.00.00
     */
    public String toScientificNotation() {
        if (digits == null) return original;
        StringBuffer digits = new StringBuffer(this.digits.toString());
        int length = digits.length();
        if (length > 1) {
            digits.insert(1, '.');
        }
        if (mantissa != 0) {
            digits.append("E" + mantissa);
        }
        if (!sign) {
            digits.insert(0, '-');
        }
        return digits.toString();
    }

    /**
     * Parsing state:
     * Initial state before anything read.
     *
     * @since ostermillerutils 1.00.00
     */
    private final static int INITIAL = 0;
    /**
     * Parsing state:
     * State in which a possible sign and
     * possible leading zeros have been read.
     *
     * @since ostermillerutils 1.00.00
     */
    private final static int LEADZEROS = 1;
    /**
     * Parsing state:
     * State in which a possible sign and
     * at least one non-zero digit
     * has been read followed by some number of
     * zeros.  The decimal place has no
     * been encountered yet.
     *
     * @since ostermillerutils 1.00.00
     */
    private final static int MIDZEROS = 2;
    /**
     * Parsing state:
     * State in which a possible sign and
     * at least one non-zero digit
     * has been read.  The decimal place has no
     * been encountered yet.
     *
     * @since ostermillerutils 1.00.00
     */
    private final static int DIGITS = 3;
    /**
     * Parsing state:
     * State in which only a possible sign,
     * leading zeros, and a decimal point
     * have been encountered.
     *
     * @since ostermillerutils 1.00.00
     */
    private final static int LEADZEROSDOT = 4;
    /**
     * Parsing state:
     * State in which a possible sign,
     * at least one nonzero digit and a
     * decimal point have been encountered.
     *
     * @since ostermillerutils 1.00.00
     */
    private final static int DIGITSDOT = 5;
    /**
     * Parsing state:
     * State in which the exponent symbol
     * 'E' has been encountered.
     *
     * @since ostermillerutils 1.00.00
     */
    private final static int MANTISSA = 6;
    /**
     * Parsing state:
     * State in which the exponent symbol
     * 'E' has been encountered followed
     * by a possible sign or some number
     * of digits.
     *
     * @since ostermillerutils 1.00.00
     */
    private final static int MANTISSADIGIT = 7;

    /**
     * Parse a number from the given string.
     * A valid number has an optional sign, some digits
     * with an optional decimal point, and an optional
     * scientific notation part consisting of an 'E' followed
     * by an optional sign, followed by some digits.
     *
     * @param number String representation of a number.
     * @throws NumberFormatException if the string is not a valid number.
     * @since ostermillerutils 1.00.00
     */
    private void parse(String number) throws NumberFormatException {
        int length = number.length();
        digits = new StringBuffer(length);
        int state = INITIAL;
        int mantissaStart = -1;
        boolean foundMantissaDigit = false;
        // sometimes we don't know if a zero will be
        // significant or not when it is encountered.
        // keep track of the number of them so that
        // the all can be made significant if we find
        // out that they are.
        int zeroCount = 0;
        int leadZeroCount = 0;

        for (int i = 0; i < length; i++) {
            char c = number.charAt(i);
            switch (c) {
                case '.': {
                    switch (state) {
                        case INITIAL:
                        case LEADZEROS: {
                            state = LEADZEROSDOT;
                        }
                        break;
                        case MIDZEROS: {
                            // we now know that these zeros
                            // are more than just trailing placeholders.
                            for (int j = 0; j < zeroCount; j++) {
                                digits.append('0');
                            }
                            zeroCount = 0;
                            state = DIGITSDOT;
                        }
                        break;
                        case DIGITS: {
                            state = DIGITSDOT;
                        }
                        break;
                        default: {
                            throw new NumberFormatException(
                                    "Unexpected character '" + c + "' at position " + i
                            );
                        }
                    }
                }
                break;
                case '+': {
                    switch (state) {
                        case INITIAL: {
                            sign = true;
                            state = LEADZEROS;
                        }
                        break;
                        case MANTISSA: {
                            state = MANTISSADIGIT;
                        }
                        break;
                        default: {
                            throw new NumberFormatException(
                                    "Unexpected character '" + c + "' at position " + i
                            );
                        }
                    }
                }
                break;
                case '-': {
                    switch (state) {
                        case INITIAL: {
                            sign = false;
                            state = LEADZEROS;
                        }
                        break;
                        case MANTISSA: {
                            state = MANTISSADIGIT;
                        }
                        break;
                        default: {
                            throw new NumberFormatException(
                                    "Unexpected character '" + c + "' at position " + i
                            );
                        }
                    }
                }
                break;
                case '0': {
                    switch (state) {
                        case INITIAL:
                        case LEADZEROS: {
                            // only significant if number
                            // is all zeros.
                            zeroCount++;
                            leadZeroCount++;
                            state = LEADZEROS;
                        }
                        break;
                        case MIDZEROS:
                        case DIGITS: {
                            // only significant if followed
                            // by a decimal point or nonzero digit.
                            mantissa++;
                            zeroCount++;
                            state = MIDZEROS;
                        }
                        break;
                        case LEADZEROSDOT: {
                            // only significant if number
                            // is all zeros.
                            mantissa--;
                            zeroCount++;
                            state = LEADZEROSDOT;
                        }
                        break;
                        case DIGITSDOT: {
                            // non-leading zeros after
                            // a decimal point are always
                            // significant.
                            digits.append(c);
                        }
                        break;
                        case MANTISSA:
                        case MANTISSADIGIT: {
                            foundMantissaDigit = true;
                            state = MANTISSADIGIT;
                        }
                        break;
                        default: {
                            throw new NumberFormatException(
                                    "Unexpected character '" + c + "' at position " + i
                            );
                        }
                    }
                }
                break;
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9': {
                    switch (state) {
                        case INITIAL:
                        case LEADZEROS:
                        case DIGITS: {
                            zeroCount = 0;
                            digits.append(c);
                            mantissa++;
                            state = DIGITS;
                        }
                        break;
                        case MIDZEROS: {
                            // we now know that these zeros
                            // are more than just trailing placeholders.
                            for (int j = 0; j < zeroCount; j++) {
                                digits.append('0');
                            }
                            zeroCount = 0;
                            digits.append(c);
                            mantissa++;
                            state = DIGITS;
                        }
                        break;
                        case LEADZEROSDOT:
                        case DIGITSDOT: {
                            zeroCount = 0;
                            digits.append(c);
                            state = DIGITSDOT;
                        }
                        break;
                        case MANTISSA:
                        case MANTISSADIGIT: {
                            state = MANTISSADIGIT;
                            foundMantissaDigit = true;
                        }
                        break;
                        default: {
                            throw new NumberFormatException(
                                    "Unexpected character '" + c + "' at position " + i
                            );
                        }
                    }
                }
                break;
                case 'E':
                case 'e': {
                    switch (state) {
                        case INITIAL:
                        case LEADZEROS:
                        case DIGITS:
                        case LEADZEROSDOT:
                        case DIGITSDOT: {
                            // record the starting point of the mantissa
                            // so we can do a substring to get it back later
                            mantissaStart = i + 1;
                            state = MANTISSA;
                        }
                        break;
                        default: {
                            throw new NumberFormatException(
                                    "Unexpected character '" + c + "' at position " + i
                            );
                        }
                    }
                }
                break;
                default: {
                    throw new NumberFormatException(
                            "Unexpected character '" + c + "' at position " + i
                    );
                }
            }
        }
        if (mantissaStart != -1) {
            // if we had found an 'E'
            if (!foundMantissaDigit) {
                // we didn't actually find a mantissa to go with.
                throw new NumberFormatException(
                        "No digits in mantissa."
                );
            }
            // parse the mantissa.
            mantissa += Integer.parseInt(number.substring(mantissaStart));
        }
        if (digits.length() == 0) {
            if (zeroCount > 0) {
                // if nothing but zeros all zeros are significant.
                for (int j = 0; j < zeroCount; j++) {
                    digits.append('0');
                }
                mantissa += leadZeroCount;
                isZero = true;
                sign = true;
            } else {
                // a hack to catch some cases that we could catch
                // by adding a ton of extra states.  Things like:
                // "e2" "+e2" "+." "." "+" etc.
                throw new NumberFormatException(
                        "No digits in number."
                );
            }
        }
    }

    /**
     * Adjust the number of digits in the number.
     * Pad the tail with zeros if too short, round the
     * number according to scientific rounding if too long, leave alone
     * if just right.
     * <p/>
     * This method has no effect if this number is not a number or infinity.
     *
     * @param significantFigures desired number of significant figures.
     * @return This number.
     * @since ostermillerutils 1.00.00
     */
    public SignificantFigures setNumberSignificantFigures(int significantFigures) {
        if (significantFigures <= 0)
            throw new IllegalArgumentException("Desired number of significant figures must be positive.");
        if (digits != null) {
            int length = digits.length();
            if (length < significantFigures) {
                // number is not long enough, pad it with zeros.
                for (int i = length; i < significantFigures; i++) {
                    digits.append('0');
                }
            } else if (length > significantFigures) {
                // number is too long chop some of it off with rounding.
                boolean addOne; // we need to round up if true.
                char firstInSig = digits.charAt(significantFigures);
                if (firstInSig < '5') {
                    // first non-significant digit less than five, round down.
                    addOne = false;
                } else if (firstInSig == '5') {
                    // first non-significant digit equal to five
                    addOne = false;
                    for (int i = significantFigures + 1; !addOne && i < length; i++) {
                        // if its followed by any non-zero digits, round up.
                        if (digits.charAt(i) != '0') {
                            addOne = true;
                        }
                    }
                    if (!addOne) {
                        // if it was not followed by non-zero digits
                        // if the last significant digit is odd round up
                        // if the last significant digit is even round down
                        addOne = (digits.charAt(significantFigures - 1) & 1) == 1;
                    }
                } else {
                    // first non-significant digit greater than five, round up.
                    addOne = true;
                }
                // loop to add one (and carry a one if added to a nine)
                // to the last significant digit
                for (int i = significantFigures - 1; addOne && i >= 0; i--) {
                    char digit = digits.charAt(i);
                    if (digit < '9') {
                        digits.setCharAt(i, (char) (digit + 1));
                        addOne = false;
                    } else {
                        digits.setCharAt(i, '0');
                    }
                }
                if (addOne) {
                    // if the number was all nines
                    digits.insert(0, '1');
                    mantissa++;
                }
                // chop it to the correct number of figures.
                digits.setLength(significantFigures);
            }
        }
        return this;
    }

    /**
     * Returns the value of this number as a byte.
     *
     * @return the numeric value represented by this object after conversion to type byte.
     * @throws NumberFormatException if this number cannot be converted to a byte.
     * @since ostermillerutils 1.00.00
     */
    public byte byteValue() throws NumberFormatException {
        return Byte.parseByte(original);
    }

    /**
     * Returns the value of this number as a double.
     *
     * @return the numeric value represented by this object after conversion to type double.
     * @throws NumberFormatException if this number cannot be converted to a double.
     * @since ostermillerutils 1.00.00
     */
    public double doubleValue() throws NumberFormatException {
        return Double.parseDouble(original);
    }

    /**
     * Returns the value of this number as a float.
     *
     * @return the numeric value represented by this object after conversion to type float.
     * @throws NumberFormatException if this number cannot be converted to a float.
     * @since ostermillerutils 1.00.00
     */
    public float floatValue() throws NumberFormatException {
        return Float.parseFloat(original);
    }

    /**
     * Returns the value of this number as a int.
     *
     * @return the numeric value represented by this object after conversion to type int.
     * @throws NumberFormatException if this number cannot be converted to a int.
     * @since ostermillerutils 1.00.00
     */
    public int intValue() throws NumberFormatException {
        return Integer.parseInt(original);
    }

    /**
     * Returns the value of this number as a long.
     *
     * @return the numeric value represented by this object after conversion to type long.
     * @throws NumberFormatException if this number cannot be converted to a long.
     * @since ostermillerutils 1.00.00
     */
    public long longValue() throws NumberFormatException {
        return Long.parseLong(original);
    }

    /**
     * Returns the value of this number as a short.
     *
     * @return the numeric value represented by this object after conversion to type short.
     * @throws NumberFormatException if this number cannot be converted to a short.
     * @since ostermillerutils 1.00.00
     */
    public short shortValue() throws NumberFormatException {
        return Short.parseShort(original);
    }

    /**
     * Convenience method to display a number with the correct
     * significant digits.
     *
     * @param number             the number to display
     * @param significantFigures the number of significant figures to display.
     * @since ostermillerutils 1.02.07
     */
    public static String format(byte number, int significantFigures) {
        SignificantFigures sf = new SignificantFigures(number);
        sf.setNumberSignificantFigures(significantFigures);
        return sf.toString();
    }

    /**
     * Convenience method to display a number with the correct
     * significant digits.
     *
     * @param number             the number to display
     * @param significantFigures the number of significant figures to display.
     * @since ostermillerutils 1.02.07
     */
    public static String format(double number, int significantFigures) {
        SignificantFigures sf = new SignificantFigures(number);
        sf.setNumberSignificantFigures(significantFigures);
        return sf.toString();
    }

    /**
     * Convenience method to display a number with the correct
     * significant digits.
     *
     * @param number             the number to display
     * @param significantFigures the number of significant figures to display.
     * @since ostermillerutils 1.02.07
     */
    public static String format(float number, int significantFigures) {
        SignificantFigures sf = new SignificantFigures(number);
        sf.setNumberSignificantFigures(significantFigures);
        return sf.toString();
    }

    /**
     * Convenience method to display a number with the correct
     * significant digits.
     *
     * @param number             the number to display
     * @param significantFigures the number of significant figures to display.
     * @since ostermillerutils 1.02.07
     */
    public static String format(int number, int significantFigures) {
        SignificantFigures sf = new SignificantFigures(number);
        sf.setNumberSignificantFigures(significantFigures);
        return sf.toString();
    }

    /**
     * Convenience method to display a number with the correct
     * significant digits.
     *
     * @param number             the number to display
     * @param significantFigures the number of significant figures to display.
     * @since ostermillerutils 1.02.07
     */
    public static String format(long number, int significantFigures) {
        SignificantFigures sf = new SignificantFigures(number);
        sf.setNumberSignificantFigures(significantFigures);
        return sf.toString();
    }

    /**
     * Convenience method to display a number with the correct
     * significant digits.
     *
     * @param number             the number to display
     * @param significantFigures the number of significant figures to display.
     * @since ostermillerutils 1.02.07
     */
    public static String format(Number number, int significantFigures) {
        SignificantFigures sf = new SignificantFigures(number);
        sf.setNumberSignificantFigures(significantFigures);
        return sf.toString();
    }

    /**
     * Convenience method to display a number with the correct
     * significant digits.
     *
     * @param number             the number to display
     * @param significantFigures the number of significant figures to display.
     * @since ostermillerutils 1.02.07
     */
    public static String format(short number, int significantFigures) {
        SignificantFigures sf = new SignificantFigures(number);
        sf.setNumberSignificantFigures(significantFigures);
        return sf.toString();
    }

    /**
     * Convenience method to display a number with the correct
     * significant digits.
     *
     * @param number             the number to display
     * @param significantFigures the number of significant figures to display.
     * @throws NumberFormatException if the String is not a valid number.
     * @since ostermillerutils 1.02.07
     */
    public static String format(String number, int significantFigures) throws NumberFormatException {
        SignificantFigures sf = new SignificantFigures(number);
        sf.setNumberSignificantFigures(significantFigures);
        return sf.toString();
    }

    /**
     * Convenience method to display a number with the correct
     * significant digits in scientific notation.
     *
     * @param number             the number to display
     * @param significantFigures the number of significant figures to display.
     * @throws NumberFormatException if the String is not a valid number.
     * @added by Steven Maere 29/3/2005
     */

    public static String sci_format(String number, int significantFigures) throws NumberFormatException {
        SignificantFigures sf = new SignificantFigures(number);
        sf.setNumberSignificantFigures(significantFigures);
        return sf.toScientificNotation();
    }


}
