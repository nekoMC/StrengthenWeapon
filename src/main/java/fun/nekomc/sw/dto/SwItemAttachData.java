package fun.nekomc.sw.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
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
public class SwItemAttachData implements PersistentDataType<String, SwItemAttachData>, Serializable {

    /**
     * 强化、洗练均为 0 级的默认数据信息
     * 同时用作 PersistentDataType 接口实现的单例
     */
    public static final SwItemAttachData DEFAULT_ATTACH_DATA = new SwItemAttachData();

    private static final Gson JSON_PARSER = new Gson();

    /**
     * 洗练等级 refine level
     */
    private int refLvl;

    /**
     * 强化等级 strengthen level
     */
    private int strLvl;

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
