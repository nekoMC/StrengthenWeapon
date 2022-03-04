package fun.nekomc.sw.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 封装了集合相关操作的工具类
 * created: 2022/3/3 01:28
 *
 * @author Chiru
 */
@UtilityClass
public class ServiceUtils {

    /**
     * 转换 Map 值的类型
     *
     * @param srcMap         要转换的 Map
     * @param valueConverter 转换函数
     * @param <KEY>          键类型
     * @param <SRC>          原始值类型
     * @param <TAR>          目标值类型
     * @return 转换了值类型的 Map，键类型不会变化
     */
    public static <KEY, SRC, TAR> Map<KEY, TAR> convertMapValue(Map<KEY, SRC> srcMap, Function<SRC, TAR> valueConverter) {
        Assert.notNull(valueConverter, "valueConverter cannot be null");
        if (CollectionUtil.isEmpty(srcMap)) {
            return Collections.emptyMap();
        }

        HashMap<KEY, TAR> targetMap = new HashMap<>(srcMap.size());
        srcMap.forEach((key, raw) -> targetMap.put(key, valueConverter.apply(raw)));
        return targetMap;
    }

    /**
     * 转换 List 值的类型
     *
     * @param srcList   要转换的 List
     * @param converter 转换函数
     * @param <SRC>     原始值类型
     * @param <TAR>     目标值类型
     * @return 转换了值类型的 Map，键类型不会变化
     */
    public static <SRC, TAR> List<TAR> convertList(List<SRC> srcList, Function<SRC, TAR> converter) {
        Assert.notNull(converter, "converter cannot be null");
        if (CollectionUtil.isEmpty(srcList)) {
            return Collections.emptyList();
        }
        return srcList.stream().map(converter).collect(Collectors.toList());
    }
}
