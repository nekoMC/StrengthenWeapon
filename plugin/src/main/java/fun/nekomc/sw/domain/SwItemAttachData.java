package fun.nekomc.sw.domain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import fun.nekomc.sw.skill.AbstractSwSkill;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 写入 Item 元数据的附加数据信息
 * 因为本类会写入 MetaData，因此字段名会简写以节约内存
 * created: 2022/3/5 18:01
 *
 * @author Chiru
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class SwItemAttachData implements PersistentDataType<String, SwItemAttachData>, Serializable {

    /**
     * 强化、洗练均为 0 级的默认数据信息
     * 不要修改其中的值
     */
    public static final SwItemAttachData LVL0_ATTACH_DATA = new SwItemAttachData(0, 0);

    /**
     * 数据均为 null 的默认数据信息，用作强化洗脸材料的数据
     * 同时用作 PersistentDataType 接口实现的单例
     * 不要修改其中的值
     */
    public static final SwItemAttachData EMPTY_ATTACH_DATA = new SwItemAttachData(null, null);

    /**
     * Json 转化器，默认不处理 null
     * 如果有其他地方使用 Json 的转化，需要将相关方法抽离出去
     */
    private static final Gson JSON_PARSER = new GsonBuilder().create();

    /**
     * 洗练等级 refine level
     */
    @SerializedName("r")
    private Integer refineLevel;

    /**
     * 强化等级 strengthen level
     */
    @SerializedName("s")
    private Integer strengthenLevel;

    @SerializedName("k")
    private Map<String, Integer> skills;

    public SwItemAttachData(Integer refLv, Integer strLv) {
        this.refineLevel = refLv;
        this.strengthenLevel = strLv;
        this.skills = new HashMap<>();
    }

    public SwItemAttachData(Integer refLv, Integer strLv, Map<String, Integer> skills) {
        this.refineLevel = refLv;
        this.strengthenLevel = strLv;
        this.skills = skills;
    }

    @NotNull
    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @NotNull
    @Override
    public Class<SwItemAttachData> getComplexType() {
        return SwItemAttachData.class;
    }

    @NotNull
    @Override
    public String toPrimitive(@NotNull SwItemAttachData complex, @NotNull PersistentDataAdapterContext context) {
        return JSON_PARSER.toJson(complex);
    }

    @NotNull
    @Override
    public SwItemAttachData fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
        return JSON_PARSER.fromJson(primitive, SwItemAttachData.class);
    }

    public void putSkill(AbstractSwSkill skill, int level) {
        if (skill == null) {
            return;
        }
        if (skills == null) {
            skills = new HashMap<>();
        }
        if (level == 0) {
            skills.remove(skill.getConfigKey());
            return;
        }
        skills.put(skill.getConfigKey(), level);
    }
}
