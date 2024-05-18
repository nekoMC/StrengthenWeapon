package fun.nekomc.sw.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.TreeMap;

/**
 * Utilities / API methods for numbers.
 * created: 2022/3/18 22:54
 *
 * @author Chiru
 * @see <a href="https://github.com/Auxilor/eco">参考 Eco</a>
 */
@UtilityClass
public final class RomanNumberUtils {

    public static final int MAX_SUPPORT_ROMAN_NUMBER = 4096;

    /**
     * Set of roman numerals to look up.
     */
    private static final TreeMap<Integer, String> NUMERALS = new TreeMap<>();

    static {
        NUMERALS.put(1000, "M");
        NUMERALS.put(900, "CM");
        NUMERALS.put(500, "D");
        NUMERALS.put(400, "CD");
        NUMERALS.put(100, "C");
        NUMERALS.put(90, "XC");
        NUMERALS.put(50, "L");
        NUMERALS.put(40, "XL");
        NUMERALS.put(10, "X");
        NUMERALS.put(9, "IX");
        NUMERALS.put(5, "V");
        NUMERALS.put(4, "IV");
        NUMERALS.put(1, "I");
    }

    /**
     * Get Roman Numeral from number.
     *
     * @param number The number to convert.
     * @return The number, converted to a roman numeral.
     */
    @NotNull
    public static String toNumeral(final int number) {
        if (number >= 1 && number <= MAX_SUPPORT_ROMAN_NUMBER) {
            int l = NUMERALS.floorKey(number);
            if (number == l) {
                return NUMERALS.get(number);
            }
            return NUMERALS.get(l) + toNumeral(number - l);
        } else {
            return String.valueOf(number);
        }
    }


    /**
     * Get number from roman numeral.
     *
     * @param numeral The numeral to convert.
     * @return The number, converted from a roman numeral.
     */
    public static int fromNumeral(@NotNull final String numeral) {
        if (numeral.isEmpty()) {
            return 0;
        }
        for (Map.Entry<Integer, String> entry : NUMERALS.descendingMap().entrySet()) {
            if (numeral.startsWith(entry.getValue())) {
                return entry.getKey() + fromNumeral(numeral.substring(entry.getValue().length()));
            }
        }
        String errMsg = String.format("Cannot parse: [%s], is it malformed or greater than %s?", numeral, MAX_SUPPORT_ROMAN_NUMBER);
        throw new NumberFormatException(errMsg);
    }
}