package fun.nekomc.sw.promote;

import cn.hutool.core.text.CharSequenceUtil;
import fun.nekomc.sw.domain.enumeration.PromotionTypeEnum;
import fun.nekomc.sw.exception.ConfigurationException;
import fun.nekomc.sw.utils.ConfigManager;
import fun.nekomc.sw.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Keyed;
import org.bukkit.attribute.Attribute;

/**
 * 描述道具提升操作的类
 * create at 2022/3/23 12:18
 *
 * @author Chiru
 */
@Getter
@AllArgsConstructor
public class PromoteOperation {

    /**
     * 配置文件描述规则时，一个规则字符串需要分割成多少个部分。
     * 解析过程见 buildByConfig 方法
     */
    private static final int CONFIG_FORMAT_DATA_SECTION = 4;

    /**
     * 需要提升的操作类型
     */
    private final PromotionTypeEnum promotion;

    /**
     * 要操作的目标对象，可能是 Enchantment 也可能是 Attribute
     *
     * @see org.bukkit.enchantments.Enchantment
     * @see org.bukkit.attribute.Attribute
     */
    private final Keyed target;

    /**
     * 要提升的数值
     */
    private final String promotionValue;

    /**
     * 当前操作是要覆盖原（重名）属性，还是基于原属性进行追加
     */
    private final boolean rewrite;

    /**
     * 对当前操作进行概率计算时的权重
     */
    private final int weight;

    /**
     * 以配置文件中的格式构建当前对象
     *
     * @param configStr 如 ATTR:ARMOR:+4:80
     * @return 描述提升规则的 PromoteOperation 实例
     * @throws fun.nekomc.sw.exception.ConfigurationException 传入的字符串无法解析时抛出
     */
    public static PromoteOperation buildByConfig(String configStr) {
        // 校验格式
        if (CharSequenceUtil.isEmpty(configStr)) {
            throw new ConfigurationException(ConfigManager.getConfiguredMsg(Constants.Msg.CONFIG_ERROR));
        }
        String[] rules = configStr.split(":");
        if (CONFIG_FORMAT_DATA_SECTION != rules.length) {
            throw new ConfigurationException(ConfigManager.getConfiguredMsg(Constants.Msg.CONFIG_ERROR));
        }
        // 解析各部分
        PromotionTypeEnum promotionType = PromotionTypeEnum.valueOf(rules[0]);
        Attribute attributeToPromote = Attribute.valueOf(rules[1]);
        boolean rewrite = !(rules[2].startsWith("-") || rules[2].startsWith("+"));
        String promoteValue = rules[2];
        if (!rewrite) {
            promoteValue = promoteValue.substring(1);
        }
        if (!checkPromoteValue(promoteValue, promotionType)) {
            throw new ConfigurationException(ConfigManager.getConfiguredMsg(Constants.Msg.CONFIG_ERROR));
        }
        int weight = Integer.parseInt(rules[3]);
        // 构建
        return new PromoteOperation(promotionType, attributeToPromote, promoteValue, rewrite, weight);
    }

    /**
     * 校验 promoteValue 在当前的 promotionType 下是否可以正常解析
     */
    private static boolean checkPromoteValue(String promoteValue, PromotionTypeEnum promotionType) {
        boolean isNumeric = CharSequenceUtil.isNumeric(promoteValue);
        String[] promoteSplit = promoteValue.split("\\.");
        boolean isFloat = promoteSplit.length == 2
                && CharSequenceUtil.isNumeric(promoteSplit[0])
                && CharSequenceUtil.isNumeric(promoteSplit[1]);
        switch (promotionType) {
            case ATTR:
            case ATTR_UP:
                return isNumeric || isFloat;
            case ENCH:
            case ENCH_UP:
                return isNumeric;
            default:
                return true;
        }
    }
}
