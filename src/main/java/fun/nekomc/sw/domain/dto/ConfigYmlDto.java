package fun.nekomc.sw.domain.dto;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * config.yml 对应 DTO
 * created: 2022/3/6 00:20
 *
 * @author Chiru
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigYmlDto implements Serializable {

    /**
     * 可配置的提示信息
     */
    private Map<String, String> message;

    /**
     * 强化容器的标题
     */
    private String strengthTitle;

    /**
     * 洗练容器的标题
     */
    private String refineTitle;

    /**
     * 是否允许玩家对插件物品进行自定义附魔
     */
    private Boolean enablePlayerEnchant;

    /**
     * 自定义附魔配置
     */
    private Map<String, EnchantmentConfigDto> enchants;

    /**
     * 获取 message 中指定的消息内容
     *
     * @param key 键
     * @return 存在时返回配置的消息内容，不存在时返回空串
     */
    public String getMessageByKey(String key) {
        if (StringUtils.isBlank(key) || CollUtil.isEmpty(message) || !message.containsKey(key)) {
            return StringUtils.EMPTY;
        }
        return message.get(key);
    }
}
