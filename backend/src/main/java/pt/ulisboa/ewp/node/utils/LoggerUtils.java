package pt.ulisboa.ewp.node.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtils {

  private LoggerUtils() {}

  public static void error(String message, String className) {
    Logger logger = LoggerFactory.getLogger(className);
    logger.error(message);
  }

  public static void info(String message, String className) {
    Logger logger = LoggerFactory.getLogger(className);
    logger.info(message);
  }

  public static void debug(String message, String className) {
    Logger logger = LoggerFactory.getLogger(className);
    logger.debug(message);
  }

  public static void warning(String message, String className) {
    Logger logger = LoggerFactory.getLogger(className);
    logger.warn(message);
  }
}
