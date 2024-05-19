package fun.nekomc.sw.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import fun.nekomc.sw.common.ConfigManager;
import fun.nekomc.sw.common.Constants;
import fun.nekomc.sw.domain.dto.SwItemConfigDto;
import fun.nekomc.sw.domain.enumeration.RarityIndustryEnum;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        if (CollUtil.isEmpty(srcMap)) {
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
     * @return 转换了值类型的 List，键类型不会变化
     */
    public static <SRC, TAR> List<TAR> convertList(List<SRC> srcList, Function<SRC, TAR> converter) {
        Assert.notNull(converter, "converter cannot be null");
        if (CollUtil.isEmpty(srcList)) {
            return Collections.emptyList();
        }
        return srcList.stream().map(converter).collect(Collectors.toList());
    }

    /**
     * 根据一组带有权重的实体，计算应该返回哪个实体
     *
     * @param candidates      需要进行随机的一组实体
     * @param weightConverter 将实体转化为概率权重的函数
     * @param <D>             实体类型
     * @return 随机得到的目标实体，失败时返回 null
     */
    @Nullable
    public static <D> D randomByWeight(Collection<D> candidates, Function<D, Integer> weightConverter) {
        if (CollUtil.isEmpty(candidates)) {
            return null;
        }
        // 计算总权重
        int totalWeight = 0;
        for (D candidate : candidates) {
            Integer now = weightConverter.apply(candidate);
            if (null != now) {
                totalWeight += now;
            }
        }
        if (totalWeight == 0) {
            return null;
        }
        // 计算随机数，并寻找满足该随机数范围的配置项
        int randomInt = RandomUtil.randomInt(0, totalWeight);
        int nowWeight = 0;
        for (D candidate : candidates) {
            Integer newToAdd = weightConverter.apply(candidate);
            newToAdd = null == newToAdd ? 0 : newToAdd;
            if (nowWeight <= randomInt && randomInt < newToAdd + nowWeight) {
                return candidate;
            }
            nowWeight += newToAdd;
        }
        return null;
    }

    public static Optional<ItemStack> getReward(@NotNull Player player, RarityIndustryEnum industry, boolean broadcast) {
        // 可以获得宝藏
        Collection<SwItemConfigDto> candidates = ConfigManager.getItemConfigList();
        SwItemConfigDto itemConfigToReturn = ServiceUtils.randomByWeight(candidates, industry::getRarityFrom);
        if (itemConfigToReturn == null) {
            return Optional.empty();
        }
        Optional<ItemStack> targetItem = ItemUtils.buildItemByConfig(itemConfigToReturn);
        if (targetItem.isEmpty()) {
            return targetItem;
        }
        // 获得的目标物品
        ItemMeta itemMeta = targetItem.get().getItemMeta();
        String displayName = itemMeta != null ? itemMeta.getDisplayName() : targetItem.get().getType().name();
        if (broadcast) {
            MsgUtils.broadcase(ConfigManager.getConfiguredMsg(Constants.Msg.GOT_GODS_GIFT), player.getDisplayName(), displayName);
        }
        return targetItem;
    }
}
