package roomescape.global.logging.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.internal.LinkedTreeMap;
import lombok.extern.slf4j.Slf4j;

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

    public static void exception(Exception e) {
        new BizLogger(LogLevel.WARN, LogType.EXCEPTION)
                .add(LogData.EXCEPTION_NAME, e.getClass().getSimpleName())
                .add(LogData.EXCEPTION_MESSAGE, e.getMessage())
                .write();
    }

    public static void business(String event, String stage, String message, Class<?> location) {
        new BizLogger(LogLevel.INFO, LogType.BUSINESS)
                .add(LogData.EVENT, event)
                .add(LogData.STAGE, stage)
                .add(LogData.MESSAGE, message)
                .add(LogData.LOCATION, location.getSimpleName())
                .write();
    }

    @Slf4j
    public static class BizLogger {
        private final LogLevel level;
        private final Map<LogData, Object> additionalData = new LinkedTreeMap<>();

        public BizLogger(LogLevel level, LogType type) {
            this.level = level;
            additionalData.put(LOG_TYPE, type);
            additionalData.put(LEVEL, level);
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
                return objectWriter.writeValueAsString(additionalData);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("로그를 JSON으로 파싱하는 중 오류가 발생하였습니다.", e);
            }
        }
    }
}
