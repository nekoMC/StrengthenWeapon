package fun.nekomc.sw.skill;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import fun.nekomc.sw.common.ConfigManager;
import fun.nekomc.sw.common.Constants;
import fun.nekomc.sw.domain.dto.ConfigYmlDto;
import fun.nekomc.sw.domain.dto.SkillConfigDto;
import fun.nekomc.sw.utils.RomanNumberUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.ChatColor;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mori
 * @since 2024/4/22 19:52
 */
@Getter
@Setter
@Slf4j
public abstract class AbstractSwSkill {

    private static final String DEFAULT_LEVEL_CHAR = "☆";

    private static final String DEFAULT_LEVEL_FORMAT = "%d";

    /**
     * 短键
     */
    private String key;

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
}
