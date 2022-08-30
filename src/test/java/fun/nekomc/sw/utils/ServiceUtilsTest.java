package fun.nekomc.sw.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * create at 2022/8/30 12:03
 *
 * @author Chiru
 */
public class ServiceUtilsTest {

    @Test
    void normalMapConvert() {
        Map<String, String> originMap = new HashMap<>(2);
        originMap.put("one", "111");
        originMap.put("two", "2147483647");
        Map<String, Integer> convertedMap = ServiceUtils.convertMapValue(originMap, Integer::parseInt);
        assertEquals(111, convertedMap.get("one"), "值可以正常转换");
        assertEquals(Integer.MAX_VALUE, convertedMap.get("two"), "值可以正常转换");
    }

    @Test
    void malformedMapConvert() {
        Map<String, String> originMap = MapUtil.of("one", "9223372036854775807");
        assertThrows(NumberFormatException.class, () -> ServiceUtils.convertMapValue(originMap, Integer::parseInt),
                "无法正常转换时");

        Map<Void, Void> emptyMap = MapUtil.empty();
        Map<Void, Void> convertedEmptyMap = ServiceUtils.convertMapValue(emptyMap, v -> v);
        assertTrue(CollUtil.isEmpty(convertedEmptyMap), "空 Map 转换后是空");
    }

    @Test
    void normalListConvert() {
        List<String> originList = ListUtil.of("123", "-2147483648");
        List<Integer> convertedList = ServiceUtils.convertList(originList, Integer::parseInt);
        assertEquals(123, convertedList.get(0), "值可以正常转换");
        assertEquals(Integer.MIN_VALUE, convertedList.get(1), "值可以正常转换");
    }

    @Test
    void malformedListConvert() {
        List<String> originList = ListUtil.of("4.2147483648");
        assertThrows(NumberFormatException.class, () -> ServiceUtils.convertList(originList, Integer::parseInt),
                "无法正常转换时");

        List<Void> emptyList = ListUtil.empty();
        List<Void> convertedEmptyList = ServiceUtils.convertList(emptyList, v -> v);
        assertTrue(CollUtil.isEmpty(convertedEmptyList), "空 List 转换后是空");
    }

    @Test
    void randomByWeight() {
        try (MockedStatic<RandomUtil> mockedRandomUtil = mockStatic(RandomUtil.class)) {
            mockRandomMethodReturnMidOfMinAndMax(mockedRandomUtil);
            List<Integer> candidates = new ArrayList<>(4);
            assertNull(ServiceUtils.randomByWeight(candidates, v -> v), "空候选集时，返回 null");
            candidates.add(0);
            assertNull(ServiceUtils.randomByWeight(candidates, v -> v), "全部候选都不满足随机数的要求，返回 null");
            candidates.add(1);
            assertEquals(1, ServiceUtils.randomByWeight(candidates, v -> v), "向右取最接近随机数的候选");
            candidates.add(2);
            assertEquals(1, ServiceUtils.randomByWeight(candidates, v -> v), "刚好碰上随机数时，直接返回该候选");
        }
    }

    private void mockRandomMethodReturnMidOfMinAndMax(MockedStatic<RandomUtil> mockedRandomUtil) {
        mockedRandomUtil.when(RandomUtil::randomInt).thenAnswer(invocation -> {
            int min = invocation.getArgument(0);
            int max = invocation.getArgument(1);
            return (min + max) >> 1;
        });
    }
}
