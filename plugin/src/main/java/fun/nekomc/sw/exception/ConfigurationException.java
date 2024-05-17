package fun.nekomc.sw.exception;

/**
 * 配置错误
 * created: 2022/3/2 23:37
 *
 * @author Chiru
 */
public class ConfigurationException extends SwException {

    public ConfigurationException(Throwable throwable) {
        super(throwable);
    }

    public ConfigurationException(String msg) {
        super(msg);
    }
}
