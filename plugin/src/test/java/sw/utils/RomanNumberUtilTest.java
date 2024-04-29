package sw.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * create at 2022/8/30 12:03
 *
 * @author Chiru
 */
public class RomanNumberUtilTest {

    @Test
    void normalRomanNumberTest() {
        for (int i = 1; i <= RomanNumberUtils.MAX_SUPPORT_ROMAN_NUMBER; i++) {
            String converted = RomanNumberUtils.toNumeral(i);
            int recovered = RomanNumberUtils.fromNumeral(converted);
            assertEquals(i, recovered, "转换、恢复后不变");
        }
    }

    @Test
    void malformedNumberTest() {
        assertThrows(NumberFormatException.class, () -> RomanNumberUtils.fromNumeral("XYZ"), "包含非法字符时，报错");
        assertThrows(NumberFormatException.class, () -> RomanNumberUtils.fromNumeral("123"), "纯数字，报错");
        assertEquals(0, RomanNumberUtils.fromNumeral(""), "空串解析为 0");
    }
}
