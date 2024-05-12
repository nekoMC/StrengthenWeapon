package fun.nekomc.sw.skill;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.common.ConfigManager;
import fun.nekomc.sw.common.Constants;
import fun.nekomc.sw.domain.dto.ConfigYmlDto;
import fun.nekomc.sw.domain.dto.SkillConfigDto;
import fun.nekomc.sw.skill.helper.SkillHelper;
import fun.nekomc.sw.skill.helper.Watcher;
import fun.nekomc.sw.utils.ItemUtils;
import fun.nekomc.sw.utils.RomanNumberUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mori
 * @since 2024/4/22 19:52
 */
@Getter
@Slf4j
public abstract class AbstractSwSkill implements Listener, Watcher, Keyed {

    private static final String DEFAULT_LEVEL_CHAR = "☆";

    private static final String DEFAULT_LEVEL_FORMAT = "%d";

    private final String configKey;

    protected AbstractSwSkill(String configKey) {
        this.configKey = configKey;
    }

    @Override
    @NotNull
    public NamespacedKey getKey() {
        return StrengthenWeapon.getWarpedKey(configKey);
    }

    /**
     * 获取等级描述信息，默认返回 level 个☆，也可以覆写成罗马数字等
     * @param level 技能等级
     */
    public String levelSuffix(Integer level) {
        SkillConfigDto configDto = getConfig();
        Map<String, String> ext = configDto.getExt();
        String configRule = "repeat";
        if (StrUtil.isBlank(configDto.getShowLevel())) {
            configRule = configDto.getShowLevel();
        }
        String levelChar = DEFAULT_LEVEL_CHAR;
        if (MapUtil.isNotEmpty(ext) && StrUtil.isNotBlank(ext.get(Constants.LEVEL_CHAR))) {
            levelChar = ext.get(Constants.LEVEL_CHAR);
        }
        String levelFormat = DEFAULT_LEVEL_FORMAT;
        if (MapUtil.isNotEmpty(ext) && StrUtil.isNotBlank(ext.get(Constants.LEVEL_FORMAT))) {
            levelFormat = ext.get(Constants.LEVEL_FORMAT);
        }
        return switch (configRule) {
            case "roman" -> RomanNumberUtils.toNumeral(level);
            case "format" -> String.format(levelFormat, level);
            default -> StrUtil.repeat(levelChar, level);
        };
    }

    public int levelFromLore(String lore) {
        String[] splits = lore.split(" ");
        String levelStr = StrUtil.EMPTY;
        if (splits.length > 1) {
            levelStr = splits[1];
        }
        SkillConfigDto configDto = getConfig();
        Map<String, String> ext = configDto.getExt();
        String configRule = "repeat";
        if (StrUtil.isBlank(configDto.getShowLevel())) {
            configRule = configDto.getShowLevel();
        }
        String levelChar = DEFAULT_LEVEL_CHAR;
        if (MapUtil.isNotEmpty(ext) && StrUtil.isNotBlank(ext.get(Constants.LEVEL_CHAR))) {
            levelChar = ext.get(Constants.LEVEL_CHAR);
        }
        try {
            return switch (configRule) {
                case "roman" -> RomanNumberUtils.fromNumeral(levelStr);
                case "format" -> {
                    Pattern p = Pattern.compile("(^|\\s)([0-9]+)($|\\s)");
                    Matcher m = p.matcher(levelStr);
                    if (m.find()) {
                        yield Integer.parseInt(m.group(2));
                    }
                    yield 0;
                }
                default -> StrUtil.count(levelChar, levelStr);
            };
        } catch (Exception e) {
            log.error("Parse skill level error, return 0 instead, lore: {}", lore);
            return 0;
        }

    }

    /**
     * 获取该项技能的 Lore 信息，格式如 “傻气 : ☆☆☆”
     * @param level 技能等级
     */
    public String lore(Integer level) {
        String prefix = getConfig().getDisplayName();
        return prefix + " " + levelSuffix(level);
    }

    public SkillConfigDto getConfig() {
        ConfigYmlDto configYml = ConfigManager.getConfigYml();
        Map<String, SkillConfigDto> skills = configYml.getSkills();
        return skills.get(getKey());
    }

    public String getDisplayName() {
        return this.getConfig().getDisplayName();
    }

    public String getSkillName() {
        return ChatColor.stripColor(this.getDisplayName());
    }


    /**
     * 获取根据 start、addition 配置得到的属性值，通常用作概率的计算
     *
     * @param level 技能等级
     */
    protected int getSkillLvlAttribute(int level) {
        SkillConfigDto config = getConfig();
        int start = null == config.getStart() ? config.getAddition() : config.getStart();
        return start + (level - 1) * config.getAddition();
    }

    /**
     * 当前技能等级下，是否通过概率校验
     *
     * @param level       技能等级
     * @return 是否要发动技能效果
     */
    protected boolean passChance(int level) {
        double chance = getConfig().getAddition() * level / 100.0;
        return RandomUtil.randomDouble(0, 1.0) < chance;
    }
}
