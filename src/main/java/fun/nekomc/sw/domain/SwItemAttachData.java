package fun.nekomc.sw.domain;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * 写入 Item 元数据的附加数据信息
 * 因为本类会写入 MetaData，因此字段名会简写以节约内存
 * created: 2022/3/5 18:01
 *
 * @author Chiru
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwItemAttachData implements PersistentDataType<String, SwItemAttachData>, Serializable {

    /**
     * 强化、洗练均为 0 级的默认数据信息
     * 不要修改其中的值
     */
    public static final SwItemAttachData LVL0_ATTACH_DATA = new SwItemAttachData();

    /**
     * 数据均为 null 的默认数据信息，用作强化洗脸材料的数据
     * 同时用作 PersistentDataType 接口实现的单例
     * 不要修改其中的值
     */
    public static final SwItemAttachData EMPTY_ATTACH_DATA = new SwItemAttachData(null, null);

    private static final Gson JSON_PARSER = new Gson();

    /**
     * 洗练等级 refine level
     */
    private Integer refLvl;

    /**
     * 强化等级 strengthen level
     */
    private Integer strLvl;

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
}
