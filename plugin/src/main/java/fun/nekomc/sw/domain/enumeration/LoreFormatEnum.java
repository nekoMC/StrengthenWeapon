package fun.nekomc.sw.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文字颜色枚举
 * 似乎没用
 *
 * @author Mori
 * @since 2024/4/23 15:13
 */
@AllArgsConstructor
@Getter
@Deprecated
public enum LoreFormatEnum implements BaseEnum<String> {

    // ============ 原生格式 ============ //

    /**
     * #000000
     */
    BLACK("§0"),
    /**
     * #0000AA
     */
    DARK_BLUE("§1"),
    /**
     * #00AA00
     */
    DARK_GREEN("§2"),
    /**
     * #00AAAA
     */
    DARK_AQUA("§3"),
    /**
     * #AA0000
     */
    DARK_RED("§4"),
    /**
     * #AA00AA
     */
    DARK_PURPLE("§5"),
    /**
     * #FFAA00
     */
    GOLD("§6"),
    /**
     * #AAAAAA
     */
    GRAY("§7"),
    /**
     * #555555
     */
    DARK_GRAY("§8"),
    /**
     * #5555FF
     */
    BLUE("§9"),
    /**
     * #55FF55
     */
    GREEN("§a"),
    /**
     * #55FFFF
     */
    AQUA("§b"),
    /**
     * #FF5555
     */
    RED("§c"),
    /**
     * #FF55FF
     */
    LIGHT_PURPLE("§d"),
    /**
     * #FFFF55
     */
    YELLOW("§e"),
    /**
     * #FFFFFF
     */
    WHITE("§f"),
    /**
     * 随机字符
     */
    RANDOM("§k"),
    /**
     * 粗体
     */
    BOLD("§l"),
    /**
     * 删除线
     */
    DELETE_LINE("§m"),
    /**
     * 下划线
     */
    UNDERLINE("§n"),
    /**
     * 斜体
     */
    ITALIC("§o"),
    /**
     * 重置
     */
    RESET("§r"),
    ;

    private final String code;

}
