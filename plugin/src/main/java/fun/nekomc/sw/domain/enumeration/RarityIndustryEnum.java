package fun.nekomc.sw.domain.enumeration;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Sets;
import fun.nekomc.sw.common.ConfigManager;
import fun.nekomc.sw.domain.dto.SwItemConfigDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 稀有度行业枚举
 */
@Getter
@AllArgsConstructor
public enum RarityIndustryEnum implements BaseEnum<Integer> {
    /**
     * 未知
     */
    UNKNOWN(0, "", Sets.newHashSet()),
    /**
     * 渔业
     */
    FISHERY(1, "fishery", Sets.newHashSet()),
    /**
     * 农业
     */
    AGRICULTURE(2, "agriculture", Set.of(
            Material.WHEAT,
            Material.BEETROOTS,
            Material.BEETROOT,
            Material.NETHER_WART,
            Material.TORCHFLOWER_CROP,
            Material.PITCHER_CROP,
            Material.PUMPKIN,
            Material.MELON
    )),
    /**
     * 矿业
     */
    MINING(3, "mining", Set.of(
            Material.COAL_ORE,
            Material.IRON_ORE,
            Material.GOLD_ORE,
            Material.DIAMOND_ORE,
            Material.EMERALD_ORE,
            Material.LAPIS_ORE,
            Material.REDSTONE_ORE,
            Material.COPPER_ORE,
            Material.DEEPSLATE_COAL_ORE,
            Material.DEEPSLATE_IRON_ORE,
            Material.DEEPSLATE_GOLD_ORE,
            Material.DEEPSLATE_DIAMOND_ORE,
            Material.DEEPSLATE_EMERALD_ORE,
            Material.DEEPSLATE_LAPIS_ORE,
            Material.DEEPSLATE_REDSTONE_ORE,
            Material.DEEPSLATE_COPPER_ORE,
            Material.NETHER_GOLD_ORE,
            Material.NETHER_QUARTZ_ORE,
            Material.STONE,
            Material.COBBLESTONE,
            Material.GRANITE,
            Material.DIORITE,
            Material.ANDESITE,
            Material.TUFF,
            Material.BASALT,
            Material.BLACKSTONE,
            Material.DEEPSLATE,
            Material.NETHERRACK,
            Material.END_STONE
    )),
    /**
     * 药水类（巫术）
     */
    POTION(4, "potion", Sets.newHashSet()),
    /**
     * 战斗类
     */
    BATTLE(5, "battle", Sets.newHashSet()),
    ;

    private final Integer code;

    private final String key;

    private final Set<Material> focusTypes;

    /**
     * 获取关注该方块的类别
     */
    public static Optional<RarityIndustryEnum> getByMaterial(Material type) {
        for (RarityIndustryEnum value : values()) {
            if (value.getFocusTypes().contains(type)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    /**
     * 从物品配制获取指定类别物品的稀有度权重
     */
    public Integer getRarityFrom(SwItemConfigDto configDto) {
        if (configDto == null || MapUtil.isEmpty(configDto.getRarity())) {
            return 0;
        }
        return configDto.getRarity().get(key);
    }

    /**
     * 本次操作是否有神的礼物
     */
    public boolean godHasGift() {
        Map<String, Double> gift = ConfigManager.getConfigYml().getGift();
        if (MapUtil.isEmpty(gift)) {
            return false;
        }
        Double rate = gift.get(key);
        return rate != null && RandomUtil.randomDouble(0, 1.0) <= rate;
    }
}
