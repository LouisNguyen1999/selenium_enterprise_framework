package utils;

import org.apache.logging.log4j.LogManager;

public class Logger {
    private final org.apache.logging.log4j.Logger logger;

    public Logger(Class<?> clazz) {
        this.logger = LogManager.getLogger(clazz);
    }

    public void info(String message) {
        logger.info(message);
    }

    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }
}
