package roomescape.global.logging.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import static roomescape.global.logging.util.LogData.LEVEL;
import static roomescape.global.logging.util.LogData.LOG_TYPE;

public class Log {

    private static final ObjectWriter objectWriter = new ObjectMapper().writerWithDefaultPrettyPrinter();

    public static void httpRequest(Map<?, ?> logData) {
        new BizLogger(LogLevel.INFO, LogType.HTTP_REQUEST).add(LogData.MESSAGE, logData).write();
    }

    public static void httpResponse(Map<?, ?> logData) {
        new BizLogger(LogLevel.INFO, LogType.HTTP_RESPONSE).add(LogData.MESSAGE, logData).write();
    }

    @Slf4j
    public static class BizLogger {
        private final LogLevel level;
        private final LogType type;
        private final Map<LogData, Object> additionalData = new HashMap<>();

        public BizLogger(LogLevel level, LogType type) {
            this.level = level;
            this.type = type;
        }

        public BizLogger add(LogData dataType, Object value) {
            additionalData.put(dataType, value);
            return this;
        }

        public void write() {
            switch (level) {
                case DEBUG -> log.debug(parseLogContentToJson());
                case INFO -> log.info(parseLogContentToJson());
                case WARN -> log.warn(parseLogContentToJson());
                case ERROR -> log.error(parseLogContentToJson());
            }
        }

        private String parseLogContentToJson() {
            try {
                additionalData.put(LEVEL, level);
                additionalData.put(LOG_TYPE, type);
                return objectWriter.writeValueAsString(additionalData);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("로그를 JSON으로 파싱하는 중 오류가 발생하였습니다.", e);
            }
        }
    }
}
