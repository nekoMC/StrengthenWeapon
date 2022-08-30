package fun.nekomc.sw.exception;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * created: 2022/3/2 23:12
 *
 * @author Chiru
 */
@Slf4j
@NoArgsConstructor
public class SwException extends RuntimeException {

    public SwException(Throwable cause) {
        super(cause);
        log.error(cause.getMessage());
    }

    public SwException(String msg) {
        super(msg);
        log.error(msg);
    }

}
